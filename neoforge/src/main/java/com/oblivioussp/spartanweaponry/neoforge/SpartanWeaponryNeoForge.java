package com.oblivioussp.spartanweaponry.neoforge;

import com.mojang.serialization.MapCodec;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.crafting.condition.TypeDisabledCondition;
import com.oblivioussp.spartanweaponry.api.trait.MeleeBlockWeaponTrait;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.capability.IQuiverItemHandler;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.event.LoaderEvents;
import com.oblivioussp.spartanweaponry.event.ReloadResourceEventHandler;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.init.ModCommands;
import com.oblivioussp.spartanweaponry.init.ModOilRecipes;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.item.crafting.BrewingRecipe;
import com.oblivioussp.spartanweaponry.loot.DecapitateLootModifier;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.network.PacketContext;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.Config;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(ModSpartanWeaponry.ID)
public class SpartanWeaponryNeoForge
{
	/** The oil and quiver capabilities of the NeoForge port, kept for other mods (the mod itself reads the components). */
	public static final ItemCapability<IOilHandler, Void> OIL_CAPABILITY = ItemCapability.createVoid(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil"), IOilHandler.class);
	public static final ItemCapability<IQuiverItemHandler, Void> QUIVER_ITEM_CAPABILITY = ItemCapability.createVoid(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_items"), IQuiverItemHandler.class);

	public SpartanWeaponryNeoForge(IEventBus modBus, ModContainer container)
	{
		ModSpartanWeaponry.init();

		modBus.addListener((NewRegistryEvent ev) -> NeoForgePlatform.NEW_REGISTRIES.forEach(ev::register));
		modBus.addListener(SpartanWeaponryNeoForge::register);
		modBus.addListener((FMLCommonSetupEvent ev) -> ev.enqueueWork(ModSpartanWeaponry::setup));
		modBus.addListener(SpartanWeaponryNeoForge::registerPayloads);
		modBus.addListener(SpartanWeaponryNeoForge::registerCapabilities);
		modBus.addListener((ModConfigEvent.Loading ev) -> onConfig(ev.getConfig()));
		modBus.addListener((ModConfigEvent.Reloading ev) -> onConfig(ev.getConfig()));

		IEventBus bus = NeoForge.EVENT_BUS;
		// 1.20.1 LivingAttackEvent (HIGH: the melee block sees the unmodified damage), then LivingHurtEvent
		bus.addListener(EventPriority.HIGH, (LivingIncomingDamageEvent ev) -> incomingDamage(ev, true));
		bus.addListener((LivingIncomingDamageEvent ev) -> incomingDamage(ev, false));
		bus.addListener(EventPriority.LOWEST, (LivingDeathEvent ev) ->
		{
			if(!ev.isCanceled())
				CommonEventHandler.onLivingDeath(ev.getSource());
			else
				CommonEventHandler.onLivingDrops();
		});
		bus.addListener(EventPriority.HIGHEST, (LivingDropsEvent ev) -> CommonEventHandler.onLivingDrops());
		bus.addListener((LivingEquipmentChangeEvent ev) -> CommonEventHandler.onEquipmentChange(new LoaderEvents.EquipmentChange(ev.getEntity(), ev.getSlot(), ev.getFrom(), ev.getTo())));
		bus.addListener((ItemEntityPickupEvent.Pre ev) -> CommonEventHandler.onPlayerPickup(new LoaderEvents.ItemPickup(ev.getPlayer(), ev.getItemEntity())));
		bus.addListener((LootTableLoadEvent ev) -> CommonEventHandler.onLootTableLoad(new LoaderEvents.LootTableLoad()
		{
			@Override
			public Identifier getName()
			{
				return ev.getName();
			}

			@Override
			public void addPool(net.minecraft.world.level.storage.loot.LootPool pool)
			{
				ev.getTable().addPool(pool);
			}
		}));
		bus.addListener((VillagerTradesEvent ev) -> CommonEventHandler.addVillagerTrades(new LoaderEvents.VillagerTrades(ev.getType(), ev.getTrades())));
		bus.addListener((EntityTeleportEvent.EnderEntity ev) -> ev.setCanceled(CommonEventHandler.checkToCancelTeleport(ev.getEntityLiving())));
		bus.addListener((EntityTeleportEvent.EnderPearl ev) -> ev.setCanceled(CommonEventHandler.checkToCancelTeleport(ev.getPlayer())));
		bus.addListener((EntityTeleportEvent.ChorusFruit ev) -> ev.setCanceled(CommonEventHandler.checkToCancelTeleport(ev.getEntityLiving())));
		bus.addListener(SpartanWeaponryNeoForge::anvilUpdate);
		bus.addListener((PlayerBrewedPotionEvent ev) -> CommonEventHandler.onBrewPotion(ev.getEntity(), ev.getStack()));
		bus.addListener(SpartanWeaponryNeoForge::rightClickBlock);
		bus.addListener(EventPriority.HIGH, (TagsUpdatedEvent ev) -> ReloadResourceEventHandler.onUpdateTags());
		bus.addListener((RegisterCommandsEvent ev) -> ModCommands.registerCommands(ev.getDispatcher()));
		bus.addListener((RegisterBrewingRecipesEvent ev) -> ModOilRecipes.initOilRecipes(recipe -> ev.getBuilder().addRecipe(new BrewingRecipeNeoForge(recipe))));
		bus.addListener((LivingShieldBlockEvent ev) ->
		{
			if(MeleeBlockWeaponTrait.cancelsBlock(ev.getEntity(), ev.getDamageSource()))
				ev.setCanceled(true);
		});

		container.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
		container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG_SPEC);
	}

	private static void onConfig(ModConfig config)
	{
		Config.onConfigChanged(config.getFileName(), config.getSpec());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void register(RegisterEvent ev)
	{
		for(Registrar<?> registrar : Registrar.all())
		{
			if(ev.getRegistryKey().equals(registrar.registryKey()))
			{
				for(Registrar.Entry entry : registrar.getEntries())
					ev.register((ResourceKey<? extends Registry<Object>>)registrar.registryKey(), entry.getId(), () -> Registrar.create(entry));
			}
		}
		// The registries of NeoForge itself
		if(ev.getRegistryKey().equals(NeoForgeRegistries.Keys.CONDITION_CODECS))
			ev.register(NeoForgeRegistries.Keys.CONDITION_CODECS, id("type_disabled"), () -> TypeDisabledConditionNeoForge.CODEC);
		if(ev.getRegistryKey().equals(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS))
			ev.register(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, id("decapitate"), () -> DecapitateLootModifier.CODEC);
		if(ev.getRegistryKey().equals(Registries.COMMAND_ARGUMENT_TYPE))
			ModCommands.registerArgumentTypes(new ModCommands.ArgumentRegistrar()
			{
				@Override
				public <A extends com.mojang.brigadier.arguments.ArgumentType<?>, T extends net.minecraft.commands.synchronization.ArgumentTypeInfo.Template<A>> void register(String name, Class<A> argumentClass, net.minecraft.commands.synchronization.ArgumentTypeInfo<A, T> info)
				{
					ev.register(Registries.COMMAND_ARGUMENT_TYPE, id(name), () -> ArgumentTypeInfos.registerByClass(argumentClass, info));
				}
			});
	}

	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, path);
	}

	private static void registerPayloads(RegisterPayloadHandlersEvent ev)
	{
		var registrar = ev.registrar("1");
		NetworkHandler.register(new NetworkHandler.PacketRegistrar()
		{
			@Override
			public <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void playToServer(net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, T> codec, java.util.function.BiConsumer<T, PacketContext> handler)
			{
				registrar.playToServer(type, codec, (packet, ctx) -> handler.accept(packet, context(ctx)));
			}
		});
	}

	private static PacketContext context(IPayloadContext ctx)
	{
		return new PacketContext()
		{
			@Override
			public Player player()
			{
				return ctx.player();
			}

			@Override
			public void enqueueWork(Runnable work)
			{
				ctx.enqueueWork(work);
			}
		};
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent ev)
	{
		// Oil: any item in the "oilable_weapons" tag, checked per stack like the 1.20.1 AttachCapabilitiesEvent did
		Item[] allItems = BuiltInRegistries.ITEM.stream().toArray(Item[]::new);
		ev.registerItem(OIL_CAPABILITY, (stack, context) -> ModCapabilities.getOil(stack), allItems);
		for(Item item : BuiltInRegistries.ITEM)
		{
			if(item instanceof QuiverBaseItem)
				ev.registerItem(QUIVER_ITEM_CAPABILITY, (stack, context) -> ModCapabilities.getQuiver(stack), item);
		}
	}

	private static void incomingDamage(LivingIncomingDamageEvent ev, boolean attack)
	{
		LoaderEvents.IncomingDamage damage = new LoaderEvents.IncomingDamage(ev.getEntity(), ev.getSource(), ev.getAmount());
		if(attack)
			CommonEventHandler.attackEvent(damage);
		else
			CommonEventHandler.onLivingHurt(damage);
		ev.setAmount(damage.getAmount());
		if(damage.isCanceled())
			ev.setCanceled(true);
	}

	private static void anvilUpdate(AnvilUpdateEvent ev)
	{
		LoaderEvents.AnvilUpdate update = new LoaderEvents.AnvilUpdate(ev.getLeft(), ev.getRight());
		update.setCost(ev.getCost());
		update.setMaterialCost(ev.getMaterialCost());
		CommonEventHandler.handleAnvilUpdate(update);
		if(!update.getOutput().isEmpty())
		{
			ev.setOutput(update.getOutput());
			ev.setCost(update.getCost());
			ev.setMaterialCost(update.getMaterialCost());
		}
	}

	private static void rightClickBlock(PlayerInteractEvent.RightClickBlock ev)
	{
		LoaderEvents.RightClickBlock click = new LoaderEvents.RightClickBlock(ev.getEntity(), ev.getHand(), ev.getPos());
		CommonEventHandler.onItemRightClick(click);
		if(click.getCancellationResult() != InteractionResult.PASS)
		{
			ev.setCancellationResult(click.getCancellationResult());
			ev.setCanceled(true);
		}
	}

	/** spartanweaponry:type_disabled for NeoForge's recipe conditions. */
	public record TypeDisabledConditionNeoForge(TypeDisabledCondition condition) implements ICondition
	{
		public static final MapCodec<TypeDisabledConditionNeoForge> CODEC = TypeDisabledCondition.CODEC.xmap(TypeDisabledConditionNeoForge::new, TypeDisabledConditionNeoForge::condition);

		@Override
		public boolean test(IContext context)
		{
			return condition.test();
		}

		@Override
		public MapCodec<? extends ICondition> codec()
		{
			return CODEC;
		}
	}

	/** The oil brewing recipes for NeoForge's brewing registry. */
	private record BrewingRecipeNeoForge(BrewingRecipe recipe) implements IBrewingRecipe
	{
		@Override
		public boolean isInput(ItemStack input)
		{
			return recipe.isInput(input);
		}

		@Override
		public boolean isIngredient(ItemStack ingredient)
		{
			return recipe.isIngredient(ingredient);
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredient)
		{
			return recipe.getOutput(input, ingredient);
		}
	}
}
