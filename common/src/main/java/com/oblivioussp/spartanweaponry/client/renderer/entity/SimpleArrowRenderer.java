package com.oblivioussp.spartanweaponry.client.renderer.entity;

import com.oblivioussp.spartanweaponry.entity.projectile.ArrowEntitySW;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class SimpleArrowRenderer<T extends ArrowEntitySW> extends ArrowRenderer<T> 
{
	public final Identifier texture;

	public SimpleArrowRenderer(EntityRendererProvider.Context rendererProvider, Identifier textureLocation) 
	{
		super(rendererProvider);
		texture = textureLocation;
	}

	@Override
	public Identifier getTextureLocation(T entity)
	{
		return texture;
	}

}
