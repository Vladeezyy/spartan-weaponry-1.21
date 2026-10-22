package com.oblivioussp.spartanweaponry.neoforge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.platform.Platform;

import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class NeoForgePlatform implements Platform
{
	/** The registries made by {@link #createRegistry}, added to the game in NewRegistryEvent. */
	static final List<Registry<?>> NEW_REGISTRIES = new ArrayList<>();

	@Override
	public String name()
	{
		return "neoforge";
	}

	@Override
	public boolean isModLoaded(String modId)
	{
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isClient()
	{
		return FMLEnvironment.dist.isClient();
	}

	@Override
	public boolean isDevelopment()
	{
		return !FMLLoader.isProduction();
	}

	@Override
	public CreativeModeTab.Builder creativeTabBuilder()
	{
		return CreativeModeTab.builder();
	}

	@Override
	public CreativeModeTab.Builder creativeTabBuilder(ResourceKey<CreativeModeTab> after)
	{
		return CreativeModeTab.builder().withTabsBefore(after);
	}

	@Override
	public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, Identifier defaultKey)
	{
		RegistryBuilder<T> builder = new RegistryBuilder<>(key).sync(true);
		if(defaultKey != null)
			builder.defaultKey(defaultKey);
		Registry<T> registry = builder.create();
		NEW_REGISTRIES.add(registry);
		return registry;
	}

	@Override
	public void sendToServer(CustomPacketPayload payload)
	{
		PacketDistributor.sendToServer(payload);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
	{
		PacketDistributor.sendToPlayer(player, payload);
	}

	@Override
	public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory)
	{
		return IMenuTypeExtension.create(factory::create);
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<net.minecraft.network.RegistryFriendlyByteBuf> data)
	{
		player.openMenu(provider, data);
	}

	@Override
	public int onArrowLoose(ItemStack bow, Level level, Player player, int charge, boolean hasAmmo)
	{
		return EventHooks.onArrowLoose(bow, level, player, charge, hasAmmo);
	}

	@Override
	public ItemStack findAccessoryQuiver(Player player)
	{
		if(!ModList.get().isLoaded("curios"))
			return ItemStack.EMPTY;
		return CuriosQuiver.find(player);
	}

	/** Loaded only with Curios. */
	private static final class CuriosQuiver
	{
		static ItemStack find(Player player)
		{
			return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
					.flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof QuiverBaseItem))
					.map(result -> result.stack()).orElse(ItemStack.EMPTY);
		}
	}
}
