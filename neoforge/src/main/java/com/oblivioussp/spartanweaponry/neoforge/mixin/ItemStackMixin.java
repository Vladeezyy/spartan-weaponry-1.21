package com.oblivioussp.spartanweaponry.neoforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.api.ToolAction;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;

/** NeoForge's item abilities of the weapons come from their tool actions (common code, same names). */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Inject(method = "canPerformAction", at = @At("HEAD"), cancellable = true, remap = false)
	private void spartanweaponry$canPerformAction(ItemAbility ability, CallbackInfoReturnable<Boolean> cir)
	{
		ItemStack stack = (ItemStack)(Object)this;
		if(stack.getItem() instanceof ToolAction.IToolActionItem item)
			cir.setReturnValue(item.canPerformToolAction(stack, ToolAction.get(ability.name())));
	}
}
