package com.oblivioussp.spartanweaponry.client.renderer.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oblivioussp.spartanweaponry.entity.projectile.BoomerangEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

public class BoomerangRenderer extends ThrowingWeaponRenderer<BoomerangEntity> 
{
	private static final Quaternionf FLIP = new Quaternionf().setAngleAxis(Mth.PI, new Vector3f(1.0f, 1.0f, 0.0f).normalize().x, new Vector3f(1.0f, 1.0f, 0.0f).normalize().y, 0.0f);
	private static final Quaternionf QUARTER = new Quaternionf().setAngleAxis(Mth.PI / 2.0f, new Vector3f(1.0f, -1.0f, 0.0f).normalize().x, new Vector3f(1.0f, -1.0f, 0.0f).normalize().y, 0.0f);

	public BoomerangRenderer(EntityRendererProvider.Context rendererProvider) 
	{
		super(rendererProvider);
	}
	
	@Override
	public void extractRenderState(BoomerangEntity entity, ThrowingWeaponRenderState state, float partialTicks)
	{
		super.extractRenderState(entity, state, partialTicks);
		float rotationInAir = entity.getTicksInAir() != 0 && (!entity.isUnderWater()) ? (entity.getTicksInAir() + partialTicks)  * 40.0f % 360.0f : 0.0f;
		if(entity.getTicksInAir() != 0)
			entity.setNoGravity(true);
		float partTicks = rotationInAir == 0.0f ? 0.0f : partialTicks;
		state.yRot = Mth.lerp(partTicks, entity.yRotO, entity.getYRot());
		state.xRot = Mth.lerp(partTicks, entity.xRotO, entity.getXRot());
		state.spin = rotationInAir;
	}

	@Override
	protected void doRenderTransformations(ThrowingWeaponRenderState state, PoseStack matrixStack) 
	{
		matrixStack.scale(2.0f, 2.0f, 2.0f);
		matrixStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0f));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(state.xRot - 135.0f));
		matrixStack.mulPose(FLIP);			// NOTE: PI = 180 degrees around (1, 1, 0)
		matrixStack.mulPose(QUARTER);		// NOTE: PI / 2 = 90 degrees around (1, -1, 0)
		matrixStack.mulPose(Axis.ZP.rotationDegrees(state.spin));
		matrixStack.translate(0.075f, 0.25f, 0.0f);
	}
}
