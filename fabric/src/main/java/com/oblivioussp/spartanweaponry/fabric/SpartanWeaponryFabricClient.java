package com.oblivioussp.spartanweaponry.fabric;

import java.util.ArrayList;
import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.client.ClientHelper;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.event.ClientEventHandler;
import com.oblivioussp.spartanweaponry.init.ModParticles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
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
	/** The HUD layers NeoForge draws right above the vanilla crosshair (drawn by mixin/client/GuiMixin). */
	public static final List<LayeredDraw.Layer> ABOVE_CROSSHAIR = new ArrayList<>();
	private static final List<LayeredDraw.Layer> ABOVE_ALL = new ArrayList<>();
	
	@Override
	public void onInitializeClient()
	{
		ModSpartanWeaponry.clientSetup(FabricModelPredicateProviderRegistry::register);
		KeyBindingHelper.registerKeyBinding(KeyBinds.KEY_ACCESS_QUIVER);
		ClientHelper.registerItemColoursHandler((colour, items) -> ColorProviderRegistry.ITEM.register(colour, items));
		ClientHelper.registerEntityRenders(new ClientHelper.RendererRegistrar()
		{
			@Override
			public <T extends Entity> void entity(EntityType<? extends T> type, EntityRendererProvider<T> provider)
			{
				EntityRendererRegistry.register(type, provider);
			}
			
			@Override
			public <T extends BlockEntity> void blockEntity(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider)
			{
				BlockEntityRendererRegistry.register(type, provider);
			}
		});
		ClientHelper.registerModelLayers((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
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
				ParticleFactoryRegistry.getInstance().register(type, provider::apply);
			}
		});
		TooltipComponentCallback.EVENT.register(ClientHelper::createTooltipComponent);
		ClientHelper.registerHudOverlays((id, layer) -> ABOVE_ALL.add(layer), (id, layer) -> ABOVE_CROSSHAIR.add(layer));
		HudRenderCallback.EVENT.register(SpartanWeaponryFabricClient::renderHud);
		OilCoatingModels.register();
		
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
	
	// As NeoForge's registerAboveAll layers: drawn with the GUI hidden (F1) too
	private static void renderHud(GuiGraphics graphics, DeltaTracker delta)
	{
		for(LayeredDraw.Layer layer : ABOVE_ALL)
			layer.render(graphics, delta);
	}
}
