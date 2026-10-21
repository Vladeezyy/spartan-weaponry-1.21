package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.fabric.SpartanWeaponryFabric;
import com.oblivioussp.spartanweaponry.item.crafting.BrewingRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

/** NeoForge: hoppers put the inputs of the brewing recipes into the empty bottle slots too. */
@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin
{
	@Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$canPlaceItem(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		if(index > 2 || !((BrewingStandBlockEntity)(Object)this).getItem(index).isEmpty())
			return;
		for(BrewingRecipe recipe : SpartanWeaponryFabric.BREWING_RECIPES)
		{
			if(recipe.isInput(stack))
			{
				cir.setReturnValue(true);
				return;
			}
		}
	}
}
