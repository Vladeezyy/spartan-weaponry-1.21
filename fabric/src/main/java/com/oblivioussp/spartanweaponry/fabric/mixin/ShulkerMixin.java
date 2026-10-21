package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.monster.Shulker;

/** NeoForge EntityTeleportEvent.EnderEntity of shulkers: no surface to teleport to while disrupted. */
@Mixin(Shulker.class)
public abstract class ShulkerMixin
{
	@WrapOperation(method = "teleportSomewhere", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Shulker;findAttachableSurface(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Direction;"))
	private Direction spartanweaponry$teleport(Shulker self, BlockPos pos, Operation<Direction> original)
	{
		return CommonEventHandler.checkToCancelTeleport(self) ? null : original.call(self, pos);
	}
}
