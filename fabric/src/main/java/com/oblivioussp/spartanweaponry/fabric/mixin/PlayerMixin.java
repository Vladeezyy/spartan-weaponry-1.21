package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.oblivioussp.spartanweaponry.api.ModToolActions;
import com.oblivioussp.spartanweaponry.api.ToolAction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

/** NeoForge: a sweep attack needs the sword_sweep ability (vanilla: any SwordItem); the weapons sweep by their traits. */
@Mixin(Player.class)
public abstract class PlayerMixin
{
	@WrapOperation(method = "attack", constant = @Constant(classValue = SwordItem.class))
	private boolean spartanweaponry$sweep(Object item, Operation<Boolean> original)
	{
		ItemStack stack = ((Player)(Object)this).getMainHandItem();
		if(stack.getItem() instanceof ToolAction.IToolActionItem weapon)
			return weapon.canPerformToolAction(stack, ModToolActions.SWORD_SWEEP);
		return original.call(item);
	}
}
