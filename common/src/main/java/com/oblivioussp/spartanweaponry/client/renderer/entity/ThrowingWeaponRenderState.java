package com.oblivioussp.spartanweaponry.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

/** 1.21.2+: what the throwing weapon renderers need of the entity, taken once per frame. */
public class ThrowingWeaponRenderState extends EntityRenderState
{
	public final ItemStackRenderState item = new ItemStackRenderState();
	public float xRot, yRot;
	/** The spin of a tomahawk or boomerang in the air, in degrees */
	public float spin;
}
