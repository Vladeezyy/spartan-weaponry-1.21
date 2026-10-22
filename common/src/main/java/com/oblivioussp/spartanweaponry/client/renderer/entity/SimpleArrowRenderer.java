package com.oblivioussp.spartanweaponry.client.renderer.entity;

import com.oblivioussp.spartanweaponry.entity.projectile.ArrowEntitySW;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class SimpleArrowRenderer<T extends ArrowEntitySW> extends ArrowRenderer<T, ArrowRenderState> 
{
	public final Identifier texture;

	public SimpleArrowRenderer(EntityRendererProvider.Context rendererProvider, Identifier textureLocation) 
	{
		super(rendererProvider);
		texture = textureLocation;
	}

	@Override
	public ArrowRenderState createRenderState()
	{
		return new ArrowRenderState();
	}

	@Override
	protected Identifier getTextureLocation(ArrowRenderState state)
	{
		return texture;
	}

}
