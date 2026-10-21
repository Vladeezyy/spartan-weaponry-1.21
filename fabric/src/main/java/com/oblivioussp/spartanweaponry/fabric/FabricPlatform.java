package com.oblivioussp.spartanweaponry.fabric;

import java.util.function.Consumer;

import com.oblivioussp.spartanweaponry.platform.Platform;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FabricPlatform implements Platform
{
	@Override
	public String name()
	{
		return "fabric";
	}

	@Override
	public boolean isModLoaded(String modId)
	{
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isClient()
	{
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public boolean isDevelopment()
	{
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public CreativeModeTab.Builder creativeTabBuilder()
	{
		return FabricItemGroup.builder();
	}

	// Fabric keeps the tabs in the order of their registration
	@Override
	public CreativeModeTab.Builder creativeTabBuilder(ResourceKey<CreativeModeTab> after)
	{
		return FabricItemGroup.builder();
	}

	@Override
	public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, ResourceLocation defaultKey)
	{
		FabricRegistryBuilder<T, ?> builder = defaultKey != null ? FabricRegistryBuilder.createDefaulted(key, defaultKey) : FabricRegistryBuilder.createSimple(key);
		return builder.attribute(RegistryAttribute.SYNCED).buildAndRegister();
	}

	@Override
	public void sendToServer(CustomPacketPayload payload)
	{
		ClientPlayNetworking.send(payload);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
	{
		ServerPlayNetworking.send(player, payload);
	}

	// The extra data of a menu goes as the bytes the common code writes (NeoForge sends the buffer itself)
	@Override
	public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory)
	{
		return new ExtendedScreenHandlerType<T, byte[]>((id, inventory, data) -> factory.create(id, inventory, buffer(inventory, data)), ByteBufCodecs.BYTE_ARRAY);
	}

	private static RegistryFriendlyByteBuf buffer(Inventory inventory, byte[] data)
	{
		return new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), inventory.player.registryAccess());
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> data)
	{
		RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
		data.accept(buf);
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		buf.release();
		player.openMenu(new ExtendedScreenHandlerFactory<byte[]>()
		{
			@Override
			public byte[] getScreenOpeningData(ServerPlayer player)
			{
				return bytes;
			}

			@Override
			public Component getDisplayName()
			{
				return provider.getDisplayName();
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
			{
				return provider.createMenu(id, inventory, player);
			}
		});
	}

	// Fabric has no ArrowLooseEvent
	@Override
	public int onArrowLoose(ItemStack bow, Level level, Player player, int charge, boolean hasAmmo)
	{
		return charge;
	}

	// No accessory slots on Fabric yet (NeoForge: the Curios back slot)
	@Override
	public ItemStack findAccessoryQuiver(Player player)
	{
		return ItemStack.EMPTY;
	}
}
