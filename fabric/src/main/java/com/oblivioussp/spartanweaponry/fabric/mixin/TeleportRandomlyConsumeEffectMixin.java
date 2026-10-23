package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;

/** NeoForge EntityTeleportEvent.ItemConsumption (1.21.1: ChorusFruit): Ender Disruption stops the teleport. 1.21.2+: chorus
 * fruit teleports through its consume effect. */
@Mixin(TeleportRandomlyConsumeEffect.class)
public abstract class TeleportRandomlyConsumeEffectMixin
{
	@WrapOperation(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZLnet/minecraft/tags/TagKey;)Z"))
	private boolean spartanweaponry$teleport(LivingEntity entity, double x, double y, double z, boolean particles, net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> avoid, Operation<Boolean> original)
	{
		return !CommonEventHandler.checkToCancelTeleport(entity) && original.call(entity, x, y, z, particles, avoid);
	}
}
