package com.oblivioussp.spartanweaponry.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.client.renderer.entity.ModelLayers;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * The quivers on the back, from the Curios slot. Curios 14 (1.21.11) renders through the submit node collector; the
 * quiver models are baked again when the entity models are (1.21.1: a client reload listener), checked per render.
 */
public class CurioRenderer
{
	private static final Identifier smallArrowQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_arrow_small.png");
	private static final Identifier mediumArrowQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_arrow_medium.png");
	private static final Identifier largeArrowQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_arrow_large.png");
	private static final Identifier hugeArrowQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_arrow_huge.png");
	private static final Identifier smallBoltQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_small.png");
	private static final Identifier mediumBoltQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_medium.png");
	private static final Identifier largeBoltQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_large.png");
	private static final Identifier hugeBoltQuiverTexture = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_huge.png");

	private static EntityModelSet bakedFrom;
	private static QuiverModelBase smallArrow, mediumArrow, largeArrow, smallBolt, mediumBolt, largeBolt;

	private CurioRenderer() {}
	
	public static void register()
	{
		CuriosRendererRegistry.register(ModItems.SMALL_ARROW_QUIVER.get(), () -> new Renderer(Kind.SMALL_ARROW, smallArrowQuiverTexture));
		CuriosRendererRegistry.register(ModItems.MEDIUM_ARROW_QUIVER.get(), () -> new Renderer(Kind.MEDIUM_ARROW, mediumArrowQuiverTexture));
		CuriosRendererRegistry.register(ModItems.LARGE_ARROW_QUIVER.get(), () -> new Renderer(Kind.LARGE_ARROW, largeArrowQuiverTexture));
		CuriosRendererRegistry.register(ModItems.HUGE_ARROW_QUIVER.get(), () -> new Renderer(Kind.LARGE_ARROW, hugeArrowQuiverTexture));
		
		CuriosRendererRegistry.register(ModItems.SMALL_BOLT_QUIVER.get(), () -> new Renderer(Kind.SMALL_BOLT, smallBoltQuiverTexture));
		CuriosRendererRegistry.register(ModItems.MEDIUM_BOLT_QUIVER.get(), () -> new Renderer(Kind.MEDIUM_BOLT, mediumBoltQuiverTexture));
		CuriosRendererRegistry.register(ModItems.LARGE_BOLT_QUIVER.get(), () -> new Renderer(Kind.LARGE_BOLT, largeBoltQuiverTexture));
		CuriosRendererRegistry.register(ModItems.HUGE_BOLT_QUIVER.get(), () -> new Renderer(Kind.LARGE_BOLT, hugeBoltQuiverTexture));
	}

	private enum Kind { SMALL_ARROW, MEDIUM_ARROW, LARGE_ARROW, SMALL_BOLT, MEDIUM_BOLT, LARGE_BOLT }

	private static QuiverModelBase model(Kind kind)
	{
		EntityModelSet models = Minecraft.getInstance().getEntityModels();
		if(models != bakedFrom)
		{
			smallArrow = new SmallArrowQuiverModel(models.bakeLayer(ModelLayers.SMALL_ARROW_QUIVER));
			mediumArrow = new MediumArrowQuiverModel(models.bakeLayer(ModelLayers.MEDIUM_ARROW_QUIVER));
			largeArrow = new LargeArrowQuiverModel(models.bakeLayer(ModelLayers.LARGE_ARROW_QUIVER));
			smallBolt = new SmallBoltQuiverModel(models.bakeLayer(ModelLayers.SMALL_BOLT_QUIVER));
			mediumBolt = new MediumBoltQuiverModel(models.bakeLayer(ModelLayers.MEDIUM_BOLT_QUIVER));
			largeBolt = new LargeBoltQuiverModel(models.bakeLayer(ModelLayers.LARGE_BOLT_QUIVER));
			bakedFrom = models;
		}
		return switch(kind)
		{
			case SMALL_ARROW -> smallArrow;
			case MEDIUM_ARROW -> mediumArrow;
			case LARGE_ARROW -> largeArrow;
			case SMALL_BOLT -> smallBolt;
			case MEDIUM_BOLT -> mediumBolt;
			case LARGE_BOLT -> largeBolt;
		};
	}
	
	private record Renderer(Kind kind, Identifier texture) implements ICurioRenderer
	{
		@Override
		public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext,
				PoseStack poseStack, SubmitNodeCollector collector, int light, S renderState, RenderLayerParent<S, M> renderLayerParent,
				EntityRendererProvider.Context context, float yRotation, float xRotation)
		{
			int arrowsToRender = stack.getItem() instanceof QuiverBaseItem quiver ? quiver.getAmmoCount(stack) : 0;
			QuiverModelBase model = model(kind);
			poseStack.pushPose();
			ICurioRenderer.translateIfSneaking(poseStack, slotContext.entity());
			ICurioRenderer.rotateIfSneaking(poseStack, slotContext.entity());
			collector.submitModel(model, arrowsToRender, poseStack, model.renderType(texture), light, OverlayTexture.NO_OVERLAY, renderState.outlineColor, null);
			poseStack.popPose();
		}
	}

}
