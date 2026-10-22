package com.oblivioussp.spartanweaponry.fabric;

import net.minecraft.resources.ResourceKey;
import java.util.ArrayList;
import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.event.LoaderEvents;
import com.oblivioussp.spartanweaponry.event.ReloadResourceEventHandler;
import com.oblivioussp.spartanweaponry.init.ModCommands;
import com.oblivioussp.spartanweaponry.init.ModOilRecipes;
import com.oblivioussp.spartanweaponry.item.crafting.BrewingRecipe;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.network.PacketContext;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.Config;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootPool;
import net.neoforged.fml.config.ModConfig;

public class SpartanWeaponryFabric implements ModInitializer
{
	/** The oil brewing recipes (NeoForge: its brewing recipe registry), asked by mixin/PotionBrewingMixin. */
	public static final List<BrewingRecipe> BREWING_RECIPES = new ArrayList<>();

	private static boolean tradesAdded;

	@Override
	public void onInitialize()
	{
		ModSpartanWeaponry.init();
		registerAll();

		// Forge Config API Port loads a common config right in register(): the listeners come first
		ModConfigEvents.loading(ModSpartanWeaponry.ID).register(config -> Config.onConfigChanged(config.getFileName(), config.getSpec()));
		ModConfigEvents.reloading(ModSpartanWeaponry.ID).register(config -> Config.onConfigChanged(config.getFileName(), config.getSpec()));
		ConfigRegistry.INSTANCE.register(ModSpartanWeaponry.ID, ModConfig.Type.COMMON, Config.CONFIG_SPEC);
		ConfigRegistry.INSTANCE.register(ModSpartanWeaponry.ID, ModConfig.Type.CLIENT, ClientConfig.CONFIG_SPEC);
		ModSpartanWeaponry.setup();

		registerPayloads();
		ModOilRecipes.initOilRecipes(BREWING_RECIPES::add);
		ResourceConditions.register(TypeDisabledResourceCondition.TYPE);
		ResourceConditions.register(TagNotEmptyResourceCondition.TYPE);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(DecapitationModifiers.INSTANCE);

		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> ModCommands.registerCommands(dispatcher));
		ModCommands.registerArgumentTypes(new ModCommands.ArgumentRegistrar()
		{
			@Override
			public <A extends com.mojang.brigadier.arguments.ArgumentType<?>, T extends net.minecraft.commands.synchronization.ArgumentTypeInfo.Template<A>> void register(String name, Class<A> argumentClass, net.minecraft.commands.synchronization.ArgumentTypeInfo<A, T> info)
			{
				ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, name), argumentClass, info);
			}
		});

		// NeoForge TagsUpdatedEvent: on the server and on the client
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> ReloadResourceEventHandler.onUpdateTags());
		ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, from, to) -> CommonEventHandler.onEquipmentChange(new LoaderEvents.EquipmentChange(entity, slot, from, to)));
		LootTableEvents.MODIFY.register((key, table, source, registries) -> CommonEventHandler.onLootTableLoad(new LoaderEvents.LootTableLoad()
		{
			@Override
			public Identifier getName()
			{
				return key.identifier();
			}

			@Override
			public void addPool(LootPool pool)
			{
				table.pool(pool);
			}
		}));
		UseBlockCallback.EVENT.register((player, level, hand, hit) ->
		{
			LoaderEvents.RightClickBlock click = new LoaderEvents.RightClickBlock(player, hand, hit.getBlockPos());
			CommonEventHandler.onItemRightClick(click);
			InteractionResult result = click.getCancellationResult();
			// A client result other than SUCCESS would not send the click to the server (NeoForge sends it)
			return level.isClientSide() && result != InteractionResult.PASS ? InteractionResult.SUCCESS : result;
		});
		// NeoForge IItemExtension#supportsEnchantment (anvil, /enchant) and #isPrimaryItemFor (enchanting table)
		EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, stack, context) -> FabricItemHooks.allowEnchanting(stack, enchantment, context == EnchantingContext.PRIMARY));
		// NeoForge VillagerTradesEvent; the trade lists come from the config and the registries, so on the first start
		ServerLifecycleEvents.SERVER_STARTING.register(server -> addVillagerTrades());
		
		// Addons (entrypoint "spartanweaponry" in their fabric.mod.json) start after the mod: their weapons are made by
		// the API, and Fabric does not order the "main" entrypoints of dependent mods (NeoForge: their constructors
		// run first, the registration later)
		FabricLoader.getInstance().invokeEntrypoints(ModSpartanWeaponry.ID, ModInitializer.class, ModInitializer::onInitialize);
	}

	/** Registers every registrar, registry by registry in the order of BuiltInRegistries.REGISTRY (as NeoForge does). */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void registerAll()
	{
		for(Registry<?> registry : BuiltInRegistries.REGISTRY)
		{
			for(Registrar<?> registrar : Registrar.all())
			{
				if(!registry.key().equals(registrar.registryKey()))
					continue;
				for(Registrar.Entry entry : registrar.getEntries())
					Registry.register((Registry)registry, entry.getKey(), Registrar.create(entry));
			}
		}
	}

	private static void registerPayloads()
	{
		NetworkHandler.register(new NetworkHandler.PacketRegistrar()
		{
			@Override
			public <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, java.util.function.BiConsumer<T, PacketContext> handler)
			{
				PayloadTypeRegistry.playC2S().register(type, codec);
				// Fabric runs the receivers on the server thread
				ServerPlayNetworking.registerGlobalReceiver(type, (packet, ctx) -> handler.accept(packet, new PacketContext()
				{
					@Override
					public Player player()
					{
						return ctx.player();
					}

					@Override
					public void enqueueWork(Runnable work)
					{
						ctx.server().execute(work);
					}
				}));
			}
		});
	}

	private static void addVillagerTrades()
	{
		if(tradesAdded)
			return;
		tradesAdded = true;
		for(ResourceKey<VillagerProfession> profession : List.of(VillagerProfession.WEAPONSMITH, VillagerProfession.FLETCHER))
		{
			Int2ObjectOpenHashMap<List<ItemListing>> trades = new Int2ObjectOpenHashMap<>();
			for(int level = 1; level <= 5; level++)
				trades.put(level, new ArrayList<>());
			CommonEventHandler.addVillagerTrades(new LoaderEvents.VillagerTrades(profession, trades));
			trades.forEach((level, listings) ->
			{
				if(!listings.isEmpty())
					TradeOfferHelper.registerVillagerOffers(profession, level, factories -> factories.addAll(listings));
			});
		}
	}
}
