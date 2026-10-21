package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ChorusFruitItem;

/** NeoForge EntityTeleportEvent.ChorusFruit: Ender Disruption stops the teleport. */
@Mixin(ChorusFruitItem.class)
public abstract class ChorusFruitItemMixin
{
	@WrapOperation(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZ)Z"))
	private boolean spartanweaponry$teleport(LivingEntity entity, double x, double y, double z, boolean particles, Operation<Boolean> original)
	{
		return !CommonEventHandler.checkToCancelTeleport(entity) && original.call(entity, x, y, z, particles);
	}
}
