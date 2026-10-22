package com.oblivioussp.spartanweaponry.platform;

import java.util.ServiceLoader;
import java.util.function.Consumer;

import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** What the common code needs from the mod loader (NeoForge or Fabric), found with the {@link ServiceLoader}. */
public interface Platform
{
	Platform INSTANCE = ServiceLoader.load(Platform.class, Platform.class.getClassLoader()).findFirst()
			.orElseThrow(() -> new IllegalStateException("No Spartan Weaponry platform implementation"));

	/** "neoforge" or "fabric". */
	String name();

	boolean isModLoaded(String modId);

	/** The physical client (the game window), not the logical side. */
	boolean isClient();

	/** A development environment (not the release jar in a normal game). */
	boolean isDevelopment();

	CreativeModeTab.Builder creativeTabBuilder();

	/** A tab placed after the given one (NeoForge: withTabsBefore; Fabric keeps the registration order). */
	CreativeModeTab.Builder creativeTabBuilder(ResourceKey<CreativeModeTab> after);

	/** A synced registry, created before the registration of the entries; defaultKey may be null. */
	<T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, Identifier defaultKey);

	void sendToServer(CustomPacketPayload payload);

	void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

	// Menus with extra data (NeoForge: IMenuTypeExtension, Fabric: ExtendedScreenHandlerType)

	@FunctionalInterface
	interface MenuFactory<T extends AbstractContainerMenu>
	{
		T create(int id, Inventory inventory, RegistryFriendlyByteBuf data);
	}

	<T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);

	void openMenu(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> data);

	/** NeoForge's ArrowLooseEvent (other mods can change the charge, or cancel with a negative value); Fabric: as is. */
	int onArrowLoose(ItemStack bow, Level level, Player player, int charge, boolean hasAmmo);

	/** The quiver in an accessory slot (NeoForge: Curios), or an empty stack. */
	ItemStack findAccessoryQuiver(Player player);
}
