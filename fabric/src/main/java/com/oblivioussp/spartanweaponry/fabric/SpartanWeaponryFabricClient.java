package com.oblivioussp.spartanweaponry.fabric;


import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import com.oblivioussp.spartanweaponry.client.item.WeaponryItemModels;
import net.minecraft.client.KeyMapping;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.client.ClientHelper;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.event.ClientEventHandler;
import com.oblivioussp.spartanweaponry.init.ModParticles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SpartanWeaponryFabricClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ModSpartanWeaponry.clientSetup();
		KeyMapping.Category.register(KeyBinds.CATEGORY.id());
		KeyMappingHelper.registerKeyMapping(KeyBinds.KEY_ACCESS_QUIVER);
		// 1.21.4+: item definitions (assets/spartanweaponry/items) with these properties and tints, instead of item
		// properties, item colours and the oil coating model (1.21.1: OilCoatingModels)
		WeaponryItemModels.registerConditionalProperties(ConditionalItemModelProperties.ID_MAPPER::put);
		WeaponryItemModels.registerRangeProperties(RangeSelectItemModelProperties.ID_MAPPER::put);
		WeaponryItemModels.registerTintSources(ItemTintSources.ID_MAPPER::put);
		ClientHelper.registerEntityRenders(new ClientHelper.RendererRegistrar()
		{
			@Override
			public <T extends Entity> void entity(EntityType<? extends T> type, EntityRendererProvider<T> provider)
			{
				EntityRendererRegistry.register(type, provider);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public <T extends BlockEntity, S extends BlockEntityRenderState> void blockEntity(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T, S> provider)
			{
				BlockEntityRendererRegistry.register((BlockEntityType<T>)type, provider);
			}
		});
		ClientHelper.registerModelLayers((layer, definition) -> ModelLayerRegistry.registerModelLayer(layer, definition::get));
		ClientHelper.registerScreens(new ClientHelper.ScreenRegistrar()
		{
			@Override
			public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor)
			{
				MenuScreens.register(type, constructor);
			}
		});
		ModParticles.registerFactories(new ModParticles.SpriteSetRegistrar()
		{
			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, java.util.function.Function<SpriteSet, ParticleProvider<T>> provider)
			{
				ParticleProviderRegistry.getInstance().register(type, provider::apply);
			}
		});
		ClientTooltipComponentCallback.EVENT.register(ClientHelper::createTooltipComponent);
		// The GUI layers of NeoForge: above everything, and right after the crosshair, which the weapons with their own
		// crosshair hide (NeoForge: RenderGuiLayerEvent.Pre of the crosshair)
		ClientHelper.registerHudOverlays((id, layer) -> HudElementRegistry.addLast(id, layer::render),
				(id, layer) -> HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR, id, layer::render));
		HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, (crosshair) -> (graphics, delta) ->
		{
			if(!ClientEventHandler.hideVanillaCrosshair())
				crosshair.extractRenderState(graphics, delta);
		});
		
		// NeoForge: the key and mouse button input events; on Fabric before the game handles the key bindings each tick
		ClientTickEvents.START_CLIENT_TICK.register(mc ->
		{
			while(KeyBinds.KEY_ACCESS_QUIVER.consumeClick())
				ClientEventHandler.onAccessQuiverKey();
		});
		ItemTooltipCallback.EVENT.register((stack, context, flags, lines) ->
		{
			// The text line of RenderTooltipEvent.GatherComponents (the oil picture: mixin/client/ItemStackClientMixin)
			ClientEventHandler.onRenderTooltip(stack, element -> element.ifLeft(text -> lines.add(Math.min(1, lines.size()), (Component)text)));
			ClientEventHandler.onTooltip(stack, flags, lines);
		});
		
		// The test scenes (dev runs only, not in the jar)
		if(System.getProperty("spartanweaponry.scene") != null || System.getProperty("spartanweaponry.stress") != null)
		{
			try
			{
				Class.forName("com.oblivioussp.spartanweaponry.port.FabricSceneHooks").getMethod("init").invoke(null);
			}
			catch(ReflectiveOperationException e)
			{
				throw new IllegalStateException("The test scenes are not in this build", e);
			}
		}
	}
}
