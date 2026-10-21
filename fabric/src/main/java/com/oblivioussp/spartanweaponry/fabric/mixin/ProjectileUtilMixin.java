package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** NeoForge: the hand of a mob's bow is the hand with any BowItem (skeletons with longbows; vanilla: Items.BOW only). */
@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin
{
	@Inject(method = "getWeaponHoldingHand(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/InteractionHand;", at = @At("HEAD"), cancellable = true)
	private static void spartanweaponry$bowHand(LivingEntity shooter, Item weapon, CallbackInfoReturnable<InteractionHand> cir)
	{
		if(weapon == Items.BOW)
			cir.setReturnValue(shooter.getMainHandItem().getItem() instanceof BowItem ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
	}
}
