package com.oblivioussp.spartanweaponry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oblivioussp.spartanweaponry.entity.projectile.JavelinEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class JavelinRenderer<T extends JavelinEntity> extends ThrowingWeaponRenderer<T> 
{
	private float scale = 1.5f;
	
	public JavelinRenderer(EntityRendererProvider.Context rendererProvider)
	{
		super(rendererProvider);
	}

	@Override
	protected void doRenderTransformations(ThrowingWeaponRenderState state, PoseStack matrixStack)
	{
		scale = 1.5f;
		matrixStack.scale(scale, scale, scale);
		matrixStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0f));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(state.xRot - 45.0f));

		matrixStack.translate(-0.45f, -0.35f, 0.0f);
	}
}
