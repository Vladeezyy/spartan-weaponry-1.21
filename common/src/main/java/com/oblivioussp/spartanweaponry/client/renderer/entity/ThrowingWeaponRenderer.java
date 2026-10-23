package com.oblivioussp.spartanweaponry.client.renderer.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oblivioussp.spartanweaponry.entity.projectile.ThrowingWeaponEntity;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

public class ThrowingWeaponRenderer<T extends ThrowingWeaponEntity> extends EntityRenderer<T, ThrowingWeaponRenderState> 
{
	/** Half a turn around (1, 1, 0): the same for every frame, made once */
	private static final Quaternionf FLIP = new Quaternionf().setAngleAxis(Mth.PI, new Vector3f(1.0f, 1.0f, 0.0f).normalize().x, new Vector3f(1.0f, 1.0f, 0.0f).normalize().y, 0.0f);
	private final ItemModelResolver itemModelResolver;

	public ThrowingWeaponRenderer(EntityRendererProvider.Context rendererProvider) 
	{
		super(rendererProvider);
		this.itemModelResolver = rendererProvider.getItemModelResolver();
	}

	@Override
	public ThrowingWeaponRenderState createRenderState()
	{
		return new ThrowingWeaponRenderState();
	}

	@Override
	public void extractRenderState(T entityIn, ThrowingWeaponRenderState state, float partialTicks)
	{
		super.extractRenderState(entityIn, state, partialTicks);
		state.yRot = Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
		state.xRot = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
		state.spin = 0.0f;
		itemModelResolver.updateForNonLiving(state.item, entityIn.getWeaponItem(), ItemDisplayContext.GROUND, entityIn);
	}
	
	@Override
	public void submit(ThrowingWeaponRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera)
	{
		matrixStackIn.pushPose();
		doRenderTransformations(state, matrixStackIn);
		
		matrixStackIn.rotate(FLIP);
		matrixStackIn.translate(-0.10d, -0.20d, 0.0d);
		
		if(!state.item.isEmpty())
			state.item.submit(matrixStackIn, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
		matrixStackIn.popPose();
		super.submit(state, matrixStackIn, collector, camera);
	}
	
	protected void doRenderTransformations(ThrowingWeaponRenderState state, PoseStack matrixStack)
	{
		matrixStack.scale(2.0f, 2.0f, 2.0f);
		matrixStack.rotate(Axis.YP.rotationDegrees(state.yRot - 90.0f));
		matrixStack.rotate(Axis.ZP.rotationDegrees(state.xRot - 45.0f));
	}
}
