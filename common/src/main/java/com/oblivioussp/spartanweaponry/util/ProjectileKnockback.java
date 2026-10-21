package com.oblivioussp.spartanweaponry.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

/**
 * Knockback of a projectile (1.20.1 AbstractArrow.setKnockback; 1.21 only takes knockback from the bow's enchantments).
 * Same formula as 1.20.1.
 */
public final class ProjectileKnockback
{
	private ProjectileKnockback() {}

	public static void apply(AbstractArrow projectile, LivingEntity target, int knockback)
	{
		if(knockback <= 0)
			return;
		double resistance = Math.max(0.0d, 1.0d - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
		Vec3 push = projectile.getDeltaMovement().multiply(1.0d, 0.0d, 1.0d).normalize().scale(knockback * 0.6d * resistance);
		if(push.lengthSqr() > 0.0d)
			target.push(push.x, 0.1d, push.z);
	}
}
