package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.event.LoaderEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/** NeoForge's living entity hooks the common handlers use. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{


	/**
	 * NeoForge LivingIncomingDamageEvent: after the invulnerability checks of hurt, before the shield. The melee block
	 * (1.20.1 LivingAttackEvent, HIGH on NeoForge) comes first, then the damage handler with the amount it may change.
	 */
	@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z", ordinal = 0), cancellable = true)
	private void spartanweaponry$incomingDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef amountRef)
	{
		LoaderEvents.IncomingDamage damage = new LoaderEvents.IncomingDamage((LivingEntity)(Object)this, source, amountRef.get());
		CommonEventHandler.attackEvent(damage);
		if(!damage.isCanceled())
			CommonEventHandler.onLivingHurt(damage);
		if(damage.isCanceled())
			cir.setReturnValue(false);
		else
			amountRef.set(damage.getAmount());
	}

	/** NeoForge LivingDeathEvent (LOWEST): the Lucky Throw looting of the drops. */
	@Inject(method = "die", at = @At("HEAD"))
	private void spartanweaponry$die(DamageSource source, CallbackInfo ci)
	{
		CommonEventHandler.onLivingDeath(source);
	}

	/** NeoForge LivingDropsEvent (HIGHEST): the drops are made. */
	@Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
	private void spartanweaponry$dropped(ServerLevel level, DamageSource source, CallbackInfo ci)
	{
		CommonEventHandler.onLivingDrops();
	}
	// 1.21.5+: only items with the blocks_attacks component block as a shield, and Shield Breach disables a shield
	// through the common mixin/LivingEntityMixin (1.21.1: isBlocking and canDisableShield here)
}
