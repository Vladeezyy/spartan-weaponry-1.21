package com.oblivioussp.spartanweaponry.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

/** 1.21.2+: the arrow render state with the texture of the arrow or bolt, taken from the entity. */
public class ArrowTextureRenderState extends ArrowRenderState
{
	public Identifier texture;
}
