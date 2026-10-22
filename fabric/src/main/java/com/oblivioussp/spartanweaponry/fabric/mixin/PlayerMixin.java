package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.api.ModToolActions;
import com.oblivioussp.spartanweaponry.api.ToolAction;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** NeoForge: the sweep attack of any item with the sword_sweep ability (vanilla 1.21.11: the swords tag). */
@Mixin(Player.class)
public abstract class PlayerMixin
{
	@WrapOperation(method = "isSweepAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean spartanweaponry$sweep(ItemStack stack, TagKey<Item> tag, Operation<Boolean> original)
	{
		if(tag == ItemTags.SWORDS && stack.getItem() instanceof ToolAction.IToolActionItem weapon)
			return weapon.canPerformToolAction(stack, ModToolActions.SWORD_SWEEP);
		return original.call(stack, tag);
	}
}
