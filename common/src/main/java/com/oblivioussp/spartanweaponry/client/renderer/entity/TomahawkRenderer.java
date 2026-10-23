package com.oblivioussp.spartanweaponry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oblivioussp.spartanweaponry.entity.projectile.TomahawkEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TomahawkRenderer<T extends TomahawkEntity> extends ThrowingWeaponRenderer<T> 
{
	
	public TomahawkRenderer(EntityRendererProvider.Context rendererProvider)
	{
		super(rendererProvider);
	}

	@Override
	public void extractRenderState(T entity, ThrowingWeaponRenderState state, float partialTicks)
	{
		super.extractRenderState(entity, state, partialTicks);
		int ticksInAir = entity.getTicksInAir();
		state.spin = ticksInAir != 0 ? ((float)ticksInAir + partialTicks) * 30.0f % 360.0f : 0.0f;
	}

	@Override
	protected void doRenderTransformations(ThrowingWeaponRenderState state, PoseStack matrixStack)
	{
		matrixStack.scale(2.0f, 2.0f, 2.0f);
		matrixStack.rotate(Axis.YP.rotationDegrees(state.yRot - 90.0f));
		matrixStack.rotate(Axis.ZP.rotationDegrees(state.xRot - 90.0f));
		matrixStack.rotate(Axis.ZP.rotationDegrees(90.0f));
		matrixStack.translate(-0.05d, 0.05d, 0.0d);
		if(state.spin != 0.0f)
			matrixStack.rotate(Axis.ZN.rotationDegrees(state.spin));
	}
}
