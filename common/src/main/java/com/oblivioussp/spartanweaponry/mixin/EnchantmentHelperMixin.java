package com.oblivioussp.spartanweaponry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.event.CommonEventHandler;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/** Lucky Throw: Looting of a kill by a thrown weapon (see CommonEventHandler.getLuckyThrowLooting) */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
	@Inject(at = @At("HEAD"), method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I", cancellable = true)
	private static void luckyThrowLooting(Holder<Enchantment> enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> callback)
	{
		int level = CommonEventHandler.getLuckyThrowLooting();
		if(level >= 0 && enchantment.is(Enchantments.LOOTING))
			callback.setReturnValue(level);
	}
}
