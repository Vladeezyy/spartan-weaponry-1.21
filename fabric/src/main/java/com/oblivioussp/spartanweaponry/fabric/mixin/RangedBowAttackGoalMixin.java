package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;

/** NeoForge: the bow goal runs with any BowItem in hand (vanilla: Items.BOW only). */
@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalMixin
{
	@WrapOperation(method = "isHoldingBow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;isHolding(Lnet/minecraft/world/item/Item;)Z"))
	private boolean spartanweaponry$isHoldingBow(Monster mob, Item item, Operation<Boolean> original)
	{
		return mob.isHolding(stack -> stack.getItem() instanceof BowItem);
	}
}
