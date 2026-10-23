package com.oblivioussp.spartanweaponry.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import java.util.function.Function;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.rendertype.RenderType;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.minecraft.world.inventory.MenuType;

import net.minecraft.client.gui.screens.inventory.MenuAccess;

import net.minecraft.client.gui.screens.Screen;

import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import net.minecraft.world.level.block.SkullBlock;

import net.minecraft.client.model.object.skull.SkullModelBase;

import net.minecraft.client.model.geom.ModelLayerLocation;


import java.util.function.Supplier;

import java.util.function.BiConsumer;

import com.oblivioussp.spartanweaponry.api.ToolAction;
import com.oblivioussp.spartanweaponry.platform.Platform;
import net.minecraft.client.Minecraft;

import com.oblivioussp.spartanweaponry.util.ItemNbt;


import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.ModToolActions;
import com.oblivioussp.spartanweaponry.api.ModelOverrides;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;
import com.oblivioussp.spartanweaponry.block.ExtendedSkullBlock;
import com.oblivioussp.spartanweaponry.client.gui.HudCrosshair;
import com.oblivioussp.spartanweaponry.client.gui.HudLoadState;
import com.oblivioussp.spartanweaponry.client.gui.HudOilUses;
import com.oblivioussp.spartanweaponry.client.gui.HudQuiverAmmo;
import com.oblivioussp.spartanweaponry.client.gui.container.QuiverArrowScreen;
import com.oblivioussp.spartanweaponry.client.gui.container.QuiverBoltScreen;
import com.oblivioussp.spartanweaponry.client.inventory.ClientOilCoatingTooltip;
import com.oblivioussp.spartanweaponry.client.inventory.ClientQuiverTooltip;
import com.oblivioussp.spartanweaponry.client.model.LargeArrowQuiverModel;
import com.oblivioussp.spartanweaponry.client.model.LargeBoltQuiverModel;
import com.oblivioussp.spartanweaponry.client.model.MediumArrowQuiverModel;
import com.oblivioussp.spartanweaponry.client.model.MediumBoltQuiverModel;
import com.oblivioussp.spartanweaponry.client.model.SmallArrowQuiverModel;
import com.oblivioussp.spartanweaponry.client.model.SmallBoltQuiverModel;
import com.oblivioussp.spartanweaponry.client.renderer.entity.ArrowBaseRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.BoltRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.BoomerangRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.EndermanHeadModel;
import com.oblivioussp.spartanweaponry.client.renderer.entity.ExtendedSkullHelper;
import com.oblivioussp.spartanweaponry.client.renderer.entity.IllagerHeadModel;
import com.oblivioussp.spartanweaponry.client.renderer.entity.JavelinRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.ModelLayers;
import com.oblivioussp.spartanweaponry.client.renderer.entity.SimpleArrowRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.ThrowingWeaponRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.TomahawkRenderer;
import com.oblivioussp.spartanweaponry.client.renderer.entity.WitchHeadModel;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowExplosiveEntity;
import com.oblivioussp.spartanweaponry.init.ModBlockEntities;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.init.ModMenus;
import com.oblivioussp.spartanweaponry.inventory.tooltip.OilCoatingTooltip;
import com.oblivioussp.spartanweaponry.inventory.tooltip.QuiverTooltip;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.LongbowItem;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.object.skull.PiglinHeadModel;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.util.PotionHelper;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import java.util.List;

/** The client registrations of the mod; NeoForge and Fabric call these from their client setup. */
public class ClientHelper 
{
	
	// 1.21: GUI layers (1.20.1: Forge overlays with the same render methods)
	public static final HudLayer LOAD_STATE = (graphics, delta) -> HudLoadState.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	public static final HudLayer QUIVER_AMMO = (graphics, delta) -> HudQuiverAmmo.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	public static final HudLayer OIL_USES = (graphics, delta) -> HudOilUses.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	public static final HudLayer NEW_CROSSHAIR = (graphics, delta) -> HudCrosshair.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	
	/** A HUD layer (NeoForge GuiLayer, Fabric HudElement). */
	@FunctionalInterface
	public interface HudLayer
	{
		void render(GuiGraphicsExtractor graphics, DeltaTracker delta);
	}
	
	// 1.21.4+: item model properties and tints are in WeaponryItemModels, used by the item definitions
	
	public static void registerEntityRenders(RendererRegistrar ev)
	{
		Log.info("Registering Entity Renderers!");
		ev.entity(ModEntities.ARROW_SW.get(), ArrowBaseRenderer::new);
		ev.entity(ModEntities.ARROW_EXPLOSIVE.get(), (rendererProvider) -> 
			new SimpleArrowRenderer<ArrowExplosiveEntity>(rendererProvider, Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/projectiles/explosive_arrow.png")));
		ev.entity(ModEntities.BOLT.get(), BoltRenderer::new);
		ev.entity(ModEntities.BOLT_SPECTRAL.get(), BoltRenderer::new);
		ev.entity(ModEntities.THROWING_WEAPON.get(), ThrowingWeaponRenderer::new);
		ev.entity(ModEntities.THROWING_KNIFE.get(), ThrowingWeaponRenderer::new);
		ev.entity(ModEntities.TOMAHAWK.get(), TomahawkRenderer::new);
		ev.entity(ModEntities.JAVELIN.get(), JavelinRenderer::new);
		ev.entity(ModEntities.BOOMERANG.get(), BoomerangRenderer::new);
		ev.entity(ModEntities.DYNAMITE.get(), ThrownItemRenderer::new);
		
		ev.blockEntity(ModBlockEntities.EXTENDED_SKULL_TYPE.get(), SkullBlockRenderer::new);
	}
	
	public static void registerModelLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> ev)
	{
		Log.info("Registering Model Layers!");
		ev.accept(ModelLayers.SMALL_ARROW_QUIVER, SmallArrowQuiverModel::createLayer);
		ev.accept(ModelLayers.MEDIUM_ARROW_QUIVER, MediumArrowQuiverModel::createLayer);
		ev.accept(ModelLayers.LARGE_ARROW_QUIVER, LargeArrowQuiverModel::createLayer);
		
		ev.accept(ModelLayers.SMALL_BOLT_QUIVER, SmallBoltQuiverModel::createLayer);
		ev.accept(ModelLayers.MEDIUM_BOLT_QUIVER, MediumBoltQuiverModel::createLayer);
		ev.accept(ModelLayers.LARGE_BOLT_QUIVER, LargeBoltQuiverModel::createLayer);
		
		ev.accept(ModelLayers.BLAZE_HEAD, SkullModel::createMobHeadLayer);
		ev.accept(ModelLayers.ENDERMAN_HEAD, EndermanHeadModel::createLayer);
		ev.accept(ModelLayers.SPIDER_HEAD, ExtendedSkullHelper::createSpiderLayer);
		ev.accept(ModelLayers.CAVE_SPIDER_HEAD, ExtendedSkullHelper::createSpiderLayer);
		ev.accept(ModelLayers.ZOMBIFIED_PIGLIN_HEAD, () -> LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64));
		ev.accept(ModelLayers.HUSK_HEAD, ExtendedSkullHelper::createHuskLayer);
		ev.accept(ModelLayers.STRAY_SKULL, ExtendedSkullHelper::createHeadWithHatLayer);
		ev.accept(ModelLayers.DROWNED_HEAD, ExtendedSkullHelper::createHeadWithHatLayer);
		ev.accept(ModelLayers.ILLAGER_HEAD, IllagerHeadModel::createLayer);
		ev.accept(ModelLayers.WITCH_HEAD, WitchHeadModel::createLayer);
		
		Log.info("Model Layer registration complete!");
	}
	
	
	/** The models and textures of the extra mob heads (NeoForge: EntityRenderersEvent.CreateSkullModels, Fabric: a
	 * mixin in SkullBlockRenderer#createModel); 1.21.11 makes the skull models when an item model or the block entity
	 * renderer asks for them. */
	public static void registerSkullModels(SkullRegistrar ev)
	{
		ev.register(ExtendedSkullBlock.Types.BLAZE, (models) -> new SkullModel(models.bakeLayer(ModelLayers.BLAZE_HEAD)), Identifier.withDefaultNamespace("textures/entity/blaze/blaze.png"));
		ev.register(ExtendedSkullBlock.Types.ENDERMAN, (models) -> new EndermanHeadModel(models.bakeLayer(ModelLayers.ENDERMAN_HEAD)), Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/skull/enderman_head.png"));
		ev.register(ExtendedSkullBlock.Types.SPIDER, (models) -> new SkullModel(models.bakeLayer(ModelLayers.SPIDER_HEAD)), Identifier.withDefaultNamespace("textures/entity/spider/spider.png"));
		ev.register(ExtendedSkullBlock.Types.CAVE_SPIDER, (models) -> new SkullModel(models.bakeLayer(ModelLayers.CAVE_SPIDER_HEAD)), Identifier.withDefaultNamespace("textures/entity/spider/cave_spider.png"));
		ev.register(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, (models) -> new PiglinHeadModel(models.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_HEAD)), Identifier.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png"));
		ev.register(ExtendedSkullBlock.Types.HUSK, (models) -> new SkullModel(models.bakeLayer(ModelLayers.HUSK_HEAD)), Identifier.withDefaultNamespace("textures/entity/zombie/husk.png"));
		ev.register(ExtendedSkullBlock.Types.STRAY, (models) -> new SkullModel(models.bakeLayer(ModelLayers.STRAY_SKULL)), Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/skull/stray_skull.png"));
		ev.register(ExtendedSkullBlock.Types.DROWNED, (models) -> new SkullModel(models.bakeLayer(ModelLayers.DROWNED_HEAD)), Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/skull/drowned_head.png"));
		ev.register(ExtendedSkullBlock.Types.ILLAGER, (models) -> new SkullModel(models.bakeLayer(ModelLayers.ILLAGER_HEAD)), Identifier.withDefaultNamespace("textures/entity/illager/pillager.png"));
		ev.register(ExtendedSkullBlock.Types.WITCH, (models) -> new SkullModel(models.bakeLayer(ModelLayers.WITCH_HEAD)), Identifier.withDefaultNamespace("textures/entity/witch/witch.png"));
	}
	
	@FunctionalInterface
	public interface SkullRegistrar
	{
		void register(SkullBlock.Type type, Function<EntityModelSet, SkullModelBase> model, Identifier texture);
	}
	
	public static void registerScreens(ScreenRegistrar ev)
	{
		ev.register(ModMenus.QUIVER_ARROW.get(), QuiverArrowScreen::new);
		ev.register(ModMenus.QUIVER_BOLT.get(), QuiverBoltScreen::new);
	}
	
	/** AbstractContainerScreen.renderSlotHighlight with a colour (NeoForge adds that overload). */
	public static void renderSlotHighlight(GuiGraphicsExtractor guiGraphics, int x, int y, int blitOffset, int colour)
	{
		// 1.21.6+: the GUI is drawn in order, the offset is gone
		guiGraphics.fill(x, y, x + 16, y + 16, colour);
	}
	
	/** The HUD layers: above everything (aboveAll) or right above the vanilla crosshair (aboveCrosshair). */
	public static void registerHudOverlays(BiConsumer<Identifier, HudLayer> aboveAll, BiConsumer<Identifier, HudLayer> aboveCrosshair)
	{
		aboveAll.accept(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "load_state"), LOAD_STATE);
		aboveAll.accept(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_ammo"), QUIVER_AMMO);
		aboveAll.accept(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil_uses"), OIL_USES);
		aboveCrosshair.accept(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "crosshair"), NEW_CROSSHAIR);
	}

	/** The tooltip pictures of quivers and oiled weapons. */
	public static ClientTooltipComponent createTooltipComponent(TooltipComponent component)
	{
		if(component instanceof QuiverTooltip quiver)
			return new ClientQuiverTooltip(quiver);
		if(component instanceof OilCoatingTooltip oil)
			return new ClientOilCoatingTooltip(oil);
		return null;
	}

	/** Entity and block entity renderers (EntityRenderers / BlockEntityRenderers.register). */
	public interface RendererRegistrar
	{
		<T extends Entity> void entity(EntityType<? extends T> type, EntityRendererProvider<T> provider);

		<T extends BlockEntity, S extends BlockEntityRenderState> void blockEntity(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T, S> provider);
	}

	/** MenuScreens.register. */
	public interface ScreenRegistrar
	{
		<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor);
	}
}
