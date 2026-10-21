package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.oblivioussp.spartanweaponry.api.ToolAction;
import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.event.LoaderEvents;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** NeoForge's living entity hooks the common handlers use. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Shadow
	protected ItemStack useItem;

	@Shadow
	public abstract ItemStack getMainHandItem();

	/**
	 * NeoForge LivingIncomingDamageEvent: after the invulnerability checks of hurt, before the shield. The melee block
	 * (1.20.1 LivingAttackEvent, HIGH on NeoForge) comes first, then the damage handler with the amount it may change.
	 */
	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z", ordinal = 0), cancellable = true)
	private void spartanweaponry$incomingDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef amountRef)
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

	/**
	 * NeoForge: only items with the shield_block ability block like a shield. The melee block weapons have the block
	 * animation but block melee hits only (CommonEventHandler#attackEvent), not as a shield.
	 */
	@Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$isBlocking(CallbackInfoReturnable<Boolean> cir)
	{
		if(useItem.getItem() instanceof ToolAction.IToolActionItem)
			cir.setReturnValue(false);
	}

	/** NeoForge IItemExtension#canDisableShield (vanilla: axes only). */
	@Inject(method = "canDisableShield", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$canDisableShield(CallbackInfoReturnable<Boolean> cir)
	{
		ItemStack weapon = getMainHandItem();
		if(weapon.getItem() instanceof SwordBaseItem item)
		{
			LivingEntity self = (LivingEntity)(Object)this;
			cir.setReturnValue(item.canDisableShield(weapon, useItem, self, self));
		}
	}
}
