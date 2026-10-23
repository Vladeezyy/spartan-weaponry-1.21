package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.world.entity.monster.Enderman;

/** NeoForge EntityTeleportEvent.EnderEntity: Ender Disruption stops the teleport. */
@Mixin(Enderman.class)
public abstract class EnderManMixin
{
	@WrapOperation(method = "teleport(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Enderman;randomTeleport(DDDZLnet/minecraft/tags/TagKey;)Z"))
	private boolean spartanweaponry$teleport(Enderman self, double x, double y, double z, boolean particles, net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> avoid, Operation<Boolean> original)
	{
		return !CommonEventHandler.checkToCancelTeleport(self) && original.call(self, x, y, z, particles, avoid);
	}
}
