package com.oblivioussp.spartanweaponry.client.renderer.entity;

import com.oblivioussp.spartanweaponry.entity.projectile.ArrowBaseEntity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class ArrowBaseRenderer<T extends ArrowBaseEntity> extends ArrowRenderer<T, ArrowTextureRenderState> 
{
	public ArrowBaseRenderer(EntityRendererProvider.Context rendererProvider) 
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
		state.texture = entity.getTexture();
	}

	@Override
	protected Identifier getTextureLocation(ArrowTextureRenderState state)
	{
		return state.texture;
	}

}
