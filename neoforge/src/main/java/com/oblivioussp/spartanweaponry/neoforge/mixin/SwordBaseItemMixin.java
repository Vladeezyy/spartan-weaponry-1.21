package com.oblivioussp.spartanweaponry.neoforge.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.oblivioussp.spartanweaponry.api.ToolAction;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * NeoForge's item abilities of the weapons come from their tool actions (common code, same names): Player.isSweepAttack
 * asks ItemStack.canPerformAction(SWORD_SWEEP), which calls this. The method is a default one of IItemExtension, so it is
 * added to our class here; ItemStack does not declare canPerformAction (a default of ItemInstanceExtension), an @Inject
 * into ItemStack finds no target and is silently skipped (CurseForge reports 2026-11-08: no sweep with katanas etc.).
 */
@Mixin(SwordBaseItem.class)
public abstract class SwordBaseItemMixin
{
	public boolean canPerformAction(ItemInstance stack, ItemAbility ability)
	{
		// The traits only look at the item; a template (no ItemStack) is rare
		ItemStack itemStack = stack instanceof ItemStack s ? s : new ItemStack(stack.typeHolder());
		return ((SwordBaseItem)(Object)this).canPerformToolAction(itemStack, ToolAction.get(ability.name()));
	}
}
