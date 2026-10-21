package com.oblivioussp.spartanweaponry.client;

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

import net.minecraft.client.model.SkullModelBase;

import net.minecraft.client.model.geom.ModelLayerLocation;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;

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

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.util.PotionHelper;
import net.minecraft.util.FastColor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.LayeredDraw;

/** The client registrations of the mod; NeoForge and Fabric call these from their client setup. */
public class ClientHelper 
{
	
	// 1.21: GUI layers (1.20.1: Forge overlays with the same render methods)
	public static final LayeredDraw.Layer LOAD_STATE = (graphics, delta) -> HudLoadState.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	public static final LayeredDraw.Layer QUIVER_AMMO = (graphics, delta) -> HudQuiverAmmo.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	public static final LayeredDraw.Layer OIL_USES = (graphics, delta) -> HudOilUses.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	public static final LayeredDraw.Layer NEW_CROSSHAIR = (graphics, delta) -> HudCrosshair.render(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight());
	
	// 1.21: item colours are ARGB (an alpha of 0 hides the layer); -1 = no tint
	public static final ItemColor COLOR_TIPPED_PROJECTILE = (stack, idx) -> idx == 1 ? FastColor.ARGB32.opaque(PotionHelper.getColor(stack)) : -1;
	public static final ItemColor COLOR_OIL = (stack, idx) ->
		{
			OilEffect oilEffect = OilHelper.getOilFromStack(stack);
			return idx == 1 ? FastColor.ARGB32.opaque(oilEffect.getColor(stack)) : -1;
		};
	
	public static void registerItemColoursHandler(BiConsumer<ItemColor, Item[]> ev)
	{
		ev.accept(COLOR_TIPPED_PROJECTILE, new Item[] {ModItems.TIPPED_WOODEN_ARROW.get(), 
				ModItems.TIPPED_COPPER_ARROW.get(),
				ModItems.TIPPED_IRON_ARROW.get(),
				ModItems.TIPPED_DIAMOND_ARROW.get(),
				ModItems.TIPPED_NETHERITE_ARROW.get(),
				ModItems.TIPPED_BOLT.get(),
				ModItems.TIPPED_COPPER_BOLT.get(),
				ModItems.TIPPED_DIAMOND_BOLT.get(),
				ModItems.TIPPED_NETHERITE_BOLT.get()});
		ev.accept(COLOR_OIL, new Item[] {ModItems.WEAPON_OIL.get()});
	}
	
	
	public static void registerMeleeWeaponPropertyOverrides(SwordBaseItem meleeWeapon)
	{
		PENDING_PROPERTIES.add(() ->
		{
			itemProperty(meleeWeapon, ModelOverrides.BLOCKING, (stack, world, living, value) ->
			{
				return ToolAction.canPerform(stack, ModToolActions.MELEE_BLOCK) &&  living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0f : 0.0f;
			});
			Item weapon = meleeWeapon.getAsItem();
			itemProperty(weapon, ModelOverrides.THROWING, (stack, world, living, value) ->
			{
				if(living == null || !meleeWeapon.hasWeaponTrait(WeaponTraits.THROWABLE.get()) || !stack.is(living.getUseItem().getItem()))	return 0.0f;
				return living.getTicksUsingItem() > 0 ? 1.0f : 0.0f;
			});
		});
	}

	public static void registerHeavyCrossbowPropertyOverrides(HeavyCrossbowItem crossbow)
	{
		PENDING_PROPERTIES.add(() ->
		{
			itemProperty(crossbow, ModelOverrides.PULL, (stack, world, living, value) ->
			{
				if(living != null /*&& stack.getItem() == crossbow*/)
					return crossbow.isLoaded(stack) ? 0.0f : (float)(crossbow.getLoadingTicks(stack, living)) / crossbow.getFullLoadTicks(stack);
				return 0.0f;
			});
			itemProperty(crossbow, ModelOverrides.PULLING, (stack, world, living, value) ->
			{
				return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0f : 0.0f;
			});
			itemProperty(crossbow, ModelOverrides.CHARGED, (stack, world, living, value) ->
			{
				return crossbow.isLoaded(stack) ? 1.0f : 0.0f;
			});
		});
	}
	
	public static void registerLongbowPropertyOverrides(LongbowItem longbow)
	{
		PENDING_PROPERTIES.add(() ->
		{
			itemProperty(longbow, ModelOverrides.PULLING, (stack, world, living, value) ->
			{
				return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0f : 0.0f;
			});
			itemProperty(longbow, ModelOverrides.PULL, (stack, world, shooter, value) -> 
			{
				return shooter != null && shooter.getUseItem() == stack ? longbow.getNockProgress(stack, shooter) : 0.0f;
			});
		});
	}
	
	public static void registerThrowingWeaponPropertyOverrides(ThrowingWeaponItem throwingWeapon)
	{
		PENDING_PROPERTIES.add(() ->
		{
	//		Log.debug("Registering Throwing Weapon Property Overrides for item: \"" + throwingWeapon.getRegistryName().toString() + "\"");
			itemProperty(throwingWeapon, ModelOverrides.THROWING, (stack, world, living, value) ->
			{
				if(living == null || !stack.is(living.getUseItem().getItem()))	return 0.0f;
				return living.getTicksUsingItem() > 0 ? 1.0f : 0.0f;
			});
			itemProperty(throwingWeapon, ModelOverrides.EMPTY, (stack, world, living, value) ->
			{
				return ItemNbt.read(stack).getInt(ThrowingWeaponItem.NBT_AMMO_USED) == ((ThrowingWeaponItem)throwingWeapon).getMaxAmmo(stack) ? 1 : 0;
			});
		});
	}
	
	public static void registerQuiverPropertyOverrides(QuiverBaseItem quiver)
	{
		PENDING_PROPERTIES.add(() ->
		{
			itemProperty(quiver, ModelOverrides.ARROW, (stack, world, living, value) ->
			{
				return quiver.getAmmoCount(stack);
			});
		});
	}
	
/*	public static void registerMeleeWeaponBlockingPropertyOverrides(Item meleeWeapon)
	{
		itemProperty(meleeWeapon, Const.MODEL, (stack, world, living, value) ->
		{
			return GearData.getModelIndex(stack);
		});
		itemProperty(meleeWeapon, Const.BROKEN_PROPERTY, (stack, world, living, value) ->
		{
			return GearHelper.isBroken(stack) ? 0 : 1;
		});
		itemProperty(meleeWeapon, ModelOverrides.BLOCKING, (stack, world, living, value) ->
		{
			return ToolAction.canPerform(stack, ModToolActions.MELEE_BLOCK) && living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0f : 0.0f;
		});
	}*/
	
	public static void registerEntityRenders(RendererRegistrar ev)
	{
		Log.info("Registering Entity Renderers!");
		ev.entity(ModEntities.ARROW_SW.get(), ArrowBaseRenderer::new);
		ev.entity(ModEntities.ARROW_EXPLOSIVE.get(), (rendererProvider) -> 
			new SimpleArrowRenderer<ArrowExplosiveEntity>(rendererProvider, ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/projectiles/explosive_arrow.png")));
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
	
	
	/** NeoForge: EntityRenderersEvent.CreateSkullModels, Fabric: a mixin in SkullBlockRenderer.createSkullRenderers. */
	public static void registerSkullModels(EntityModelSet entityModelSet, BiConsumer<SkullBlock.Type, SkullModelBase> ev)
	{
		ev.accept(ExtendedSkullBlock.Types.BLAZE, new SkullModel(entityModelSet.bakeLayer(ModelLayers.BLAZE_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.ENDERMAN, new EndermanHeadModel(entityModelSet.bakeLayer(ModelLayers.ENDERMAN_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.SPIDER, new SkullModel(entityModelSet.bakeLayer(ModelLayers.SPIDER_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.CAVE_SPIDER, new SkullModel(entityModelSet.bakeLayer(ModelLayers.CAVE_SPIDER_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, new PiglinHeadModel(entityModelSet.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.HUSK, new SkullModel(entityModelSet.bakeLayer(ModelLayers.HUSK_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.STRAY, new SkullModel(entityModelSet.bakeLayer(ModelLayers.STRAY_SKULL)));
		ev.accept(ExtendedSkullBlock.Types.DROWNED, new SkullModel(entityModelSet.bakeLayer(ModelLayers.DROWNED_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.ILLAGER, new SkullModel(entityModelSet.bakeLayer(ModelLayers.ILLAGER_HEAD)));
		ev.accept(ExtendedSkullBlock.Types.WITCH, new SkullModel(entityModelSet.bakeLayer(ModelLayers.WITCH_HEAD)));
	}
	
	public static void registerSkullTextures()
	{
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.BLAZE, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/blaze.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.ENDERMAN, ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/skull/enderman_head.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.SPIDER, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/spider/spider.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.CAVE_SPIDER, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/spider/cave_spider.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.ZOMBIE_PIGLIN, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/piglin/zombified_piglin.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.HUSK, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/zombie/husk.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.STRAY, ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/skull/stray_skull.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.DROWNED, ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/skull/drowned_head.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.ILLAGER, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/illager/pillager.png"));
		SkullBlockRenderer.SKIN_BY_TYPE.put(ExtendedSkullBlock.Types.WITCH, ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/witch.png"));
	}
	
	public static void registerScreens(ScreenRegistrar ev)
	{
		ev.register(ModMenus.QUIVER_ARROW.get(), QuiverArrowScreen::new);
		ev.register(ModMenus.QUIVER_BOLT.get(), QuiverBoltScreen::new);
	}
	
	/** Item model properties, queued by the items' constructors and registered in client setup (1.21) */
	private static final List<Runnable> PENDING_PROPERTIES = new ArrayList<>();
	
	/** ItemProperties.register (NeoForge makes it public, Fabric has its own registry for it). */
	@FunctionalInterface
	public interface ItemPropertyRegistrar
	{
		void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property);
	}

	private static ItemPropertyRegistrar propertyRegistrar;

	private static void itemProperty(Item item, ResourceLocation name, ClampedItemPropertyFunction property)
	{
		propertyRegistrar.register(item, name, property);
	}

	public static void registerPendingItemProperties(ItemPropertyRegistrar registrar)
	{
		propertyRegistrar = registrar;
		PENDING_PROPERTIES.forEach(Runnable::run);
		PENDING_PROPERTIES.clear();
	}
	
	/** The HUD layers: above everything (aboveAll) or right above the vanilla crosshair (aboveCrosshair). */
	public static void registerHudOverlays(BiConsumer<ResourceLocation, LayeredDraw.Layer> aboveAll, BiConsumer<ResourceLocation, LayeredDraw.Layer> aboveCrosshair)
	{
		aboveAll.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "load_state"), LOAD_STATE);
		aboveAll.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_ammo"), QUIVER_AMMO);
		aboveAll.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "oil_uses"), OIL_USES);
		aboveCrosshair.accept(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "crosshair"), NEW_CROSSHAIR);
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

		<T extends BlockEntity> void blockEntity(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider);
	}

	/** MenuScreens.register. */
	public interface ScreenRegistrar
	{
		<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor);
	}
}
