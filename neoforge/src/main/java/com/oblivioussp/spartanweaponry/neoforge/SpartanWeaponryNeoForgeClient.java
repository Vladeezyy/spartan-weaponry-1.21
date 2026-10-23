package com.oblivioussp.spartanweaponry.neoforge;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.oblivioussp.spartanweaponry.client.item.WeaponryItemModels;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;

import com.mojang.blaze3d.platform.InputConstants;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.client.ClientHelper;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.client.inventory.ClientOilCoatingTooltip;
import com.oblivioussp.spartanweaponry.client.inventory.ClientQuiverTooltip;
import com.oblivioussp.spartanweaponry.client.model.CurioRenderer;
import com.oblivioussp.spartanweaponry.event.ClientEventHandler;
import com.oblivioussp.spartanweaponry.init.ModParticles;
import com.oblivioussp.spartanweaponry.inventory.tooltip.OilCoatingTooltip;
import com.oblivioussp.spartanweaponry.inventory.tooltip.QuiverTooltip;

import java.util.function.Function;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = ModSpartanWeaponry.ID, dist = Dist.CLIENT)
public class SpartanWeaponryNeoForgeClient
{
	public SpartanWeaponryNeoForgeClient(IEventBus modBus)
	{
		modBus.addListener((FMLClientSetupEvent ev) -> ev.enqueueWork(() ->
		{
			ModSpartanWeaponry.clientSetup();
			if(ModList.get().isLoaded("curios"))
				CurioRenderer.register();
		}));
		modBus.addListener((RegisterKeyMappingsEvent ev) ->
		{
			KeyBinds.KEY_ACCESS_QUIVER.setKeyConflictContext(KeyConflictContext.IN_GAME);
			ev.registerCategory(KeyBinds.CATEGORY);
			ev.register(KeyBinds.KEY_ACCESS_QUIVER);
		});
		// 1.21.4+: item definitions (assets/spartanweaponry/items) with these properties and tints, instead of item
		// properties, item colours and the oil coating model loader
		modBus.addListener((RegisterConditionalItemModelPropertyEvent ev) -> WeaponryItemModels.registerConditionalProperties(ev::register));
		modBus.addListener((RegisterRangeSelectItemModelPropertyEvent ev) -> WeaponryItemModels.registerRangeProperties(ev::register));
		modBus.addListener((RegisterColorHandlersEvent.ItemTintSources ev) -> WeaponryItemModels.registerTintSources(ev::register));
		modBus.addListener((EntityRenderersEvent.RegisterRenderers ev) -> ClientHelper.registerEntityRenders(new ClientHelper.RendererRegistrar()
		{
			@Override
			public <T extends Entity> void entity(EntityType<? extends T> type, EntityRendererProvider<T> provider)
			{
				ev.registerEntityRenderer(type, provider);
			}

			@Override
			public <T extends BlockEntity, S extends BlockEntityRenderState> void blockEntity(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T, S> provider)
			{
				ev.registerBlockEntityRenderer(type, provider);
			}
		}));
		modBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions ev) -> ClientHelper.registerModelLayers(ev::registerLayerDefinition));
		modBus.addListener((EntityRenderersEvent.CreateSkullModels ev) -> ClientHelper.registerSkullModels(ev::registerSkullModel));
		modBus.addListener((RegisterMenuScreensEvent ev) -> ClientHelper.registerScreens(new ClientHelper.ScreenRegistrar()
		{
			@Override
			public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor)
			{
				ev.register(type, constructor);
			}
		}));
		modBus.addListener((RegisterGuiLayersEvent ev) -> ClientHelper.registerHudOverlays((id, layer) -> ev.registerAboveAll(id, layer::render), (id, layer) -> ev.registerAbove(VanillaGuiLayers.CROSSHAIR, id, layer::render)));
		modBus.addListener((RegisterClientTooltipComponentFactoriesEvent ev) ->
		{
			ev.register(QuiverTooltip.class, ClientQuiverTooltip::new);
			ev.register(OilCoatingTooltip.class, ClientOilCoatingTooltip::new);
		});
		modBus.addListener((RegisterParticleProvidersEvent ev) -> ModParticles.registerFactories(new ModParticles.SpriteSetRegistrar()
		{
			@Override
			public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> provider)
			{
				ev.registerSpriteSet(type, provider::apply);
			}
		}));

		IEventBus bus = NeoForge.EVENT_BUS;
		// 1.20.1 listened to both the Pre and Post mouse events (a click sent two packets)
		bus.addListener(EventPriority.NORMAL, true, (InputEvent.MouseButton.Post ev) ->
		{
			if(isAccessQuiver(InputConstants.Type.MOUSE, ev.getButton(), ev.getAction()))
				ClientEventHandler.onAccessQuiverKey();
		});
		bus.addListener(EventPriority.NORMAL, true, (InputEvent.Key ev) ->
		{
			if(isAccessQuiver(InputConstants.Type.KEYBOARD, ev.getKey(), ev.getAction()))
				ClientEventHandler.onAccessQuiverKey();
		});
		// High priority to allow other mods to place elements further above this
		bus.addListener(EventPriority.HIGH, (RenderTooltipEvent.GatherComponents ev) -> ClientEventHandler.onRenderTooltip(ev.getItemStack(), element -> ev.getTooltipElements().add(1, element)));
		bus.addListener((RenderGuiLayerEvent.Pre ev) ->
		{
			if(ev.getName().equals(VanillaGuiLayers.CROSSHAIR) && ClientEventHandler.hideVanillaCrosshair())
				ev.setCanceled(true);
		});
		bus.addListener((ItemTooltipEvent ev) -> ClientEventHandler.onTooltip(ev.getItemStack(), ev.getFlags(), ev.getToolTip()));
	}

	private static boolean isAccessQuiver(InputConstants.Type type, int code, int action)
	{
		KeyMapping key = KeyBinds.KEY_ACCESS_QUIVER;
		return key.getKey().getType() == type && code == key.getKey().getValue() && action == InputConstants.PRESS;
	}
}
