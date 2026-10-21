package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;

/** NeoForge EntityTeleportEvent.EnderPearl: a disrupted player is not teleported (nor hurt, nor followed by an endermite). */
@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin
{
	@WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isAcceptingMessages()Z"))
	private boolean spartanweaponry$teleport(ServerGamePacketListenerImpl connection, Operation<Boolean> original)
	{
		return original.call(connection) && !CommonEventHandler.checkToCancelTeleport(connection.player);
	}
}
