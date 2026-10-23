package com.oblivioussp.spartanweaponry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import com.oblivioussp.spartanweaponry.entity.projectile.BoltEntity;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * The bolt is drawn from quads of its own (1.21.1: ArrowRenderer#vertex); 1.21.2+ arrows are a model, so this is an
 * entity renderer with the arrow render state, and the quads are one custom geometry submission.
 */
public class BoltRenderer<T extends BoltEntity> extends EntityRenderer<T, ArrowTextureRenderState> 
{
	public BoltRenderer(EntityRendererProvider.Context rendererProvider) 
	{
		super(rendererProvider);
	}

	@Override
	public ArrowTextureRenderState createRenderState()
	{
		return new ArrowTextureRenderState();
	}

	@Override
	public void extractRenderState(T entity, ArrowTextureRenderState state, float partialTicks)
	{
		super.extractRenderState(entity, state, partialTicks);
		state.xRot = entity.getXRot(partialTicks);
		state.yRot = entity.getYRot(partialTicks);
		state.shake = entity.shakeTime - partialTicks;
		state.texture = entity.getTexture();
	}

	@Override
	public void submit(ArrowTextureRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera)
	{
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
		matrixStackIn.mulPose(Axis.ZP.rotationDegrees(state.xRot));
	    
		// Move the Bolt forward when rendering.
		matrixStackIn.translate(0.2f, 0.0f, 0.0f);
		
		float f9 = state.shake;
		if (f9 > 0.0F) 
		{
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f10));
		}
	
		matrixStackIn.mulPose(Axis.XP.rotationDegrees(45.0F));
		matrixStackIn.scale(0.05625F, 0.05625F, 0.05625F);
		matrixStackIn.translate(-4.0D, 0.0D, 0.0D);
		int packedLightIn = state.lightCoords;
		collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(state.texture), (matrixEntry, consumer) ->
		{
			vertex(matrixEntry, consumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLightIn);
			vertex(matrixEntry, consumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLightIn);
		
			PoseStack.Pose side = matrixEntry.copy();
			for(int j = 0; j < 4; ++j) 
			{
				side.rotate(Axis.XP.rotationDegrees(90.0F));
				vertex(side, consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLightIn);
				vertex(side, consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLightIn);
				vertex(side, consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLightIn);
				vertex(side, consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLightIn);
			}
		});
	
		matrixStackIn.popPose();
		super.submit(state, matrixStackIn, collector, camera);
	}

	/** ArrowRenderer#vertex of 1.21.1 */
	private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int packedLight)
	{
		consumer.addVertex(pose, (float)x, (float)y, (float)z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, (float)normalX, (float)normalY, (float)normalZ);
	}
}
