package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.world.entity.monster.EnderMan;

/** NeoForge EntityTeleportEvent.EnderEntity: Ender Disruption stops the teleport. */
@Mixin(EnderMan.class)
public abstract class EnderManMixin
{
	@WrapOperation(method = "teleport(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan;randomTeleport(DDDZ)Z"))
	private boolean spartanweaponry$teleport(EnderMan self, double x, double y, double z, boolean particles, Operation<Boolean> original)
	{
		return !CommonEventHandler.checkToCancelTeleport(self) && original.call(self, x, y, z, particles);
	}
}
