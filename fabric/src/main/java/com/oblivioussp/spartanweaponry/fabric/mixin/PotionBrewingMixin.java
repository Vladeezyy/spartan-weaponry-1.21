package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.fabric.SpartanWeaponryFabric;
import com.oblivioussp.spartanweaponry.item.crafting.BrewingRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

/** NeoForge's brewing recipe registry: the oil recipes come before the vanilla mixes (as in BrewingRecipeRegistry). */
@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin
{
	@Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		for(BrewingRecipe recipe : SpartanWeaponryFabric.BREWING_RECIPES)
		{
			if(recipe.isIngredient(stack))
			{
				cir.setReturnValue(true);
				return;
			}
		}
	}

	// Vanilla's names: (bottle, ingredient), as BrewingStandBlockEntity calls it
	@Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$hasMix(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir)
	{
		for(BrewingRecipe recipe : SpartanWeaponryFabric.BREWING_RECIPES)
		{
			if(!recipe.getOutput(input, ingredient).isEmpty())
			{
				cir.setReturnValue(true);
				return;
			}
		}
	}

	// (ingredient, bottle)
	@Inject(method = "mix", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$mix(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir)
	{
		for(BrewingRecipe recipe : SpartanWeaponryFabric.BREWING_RECIPES)
		{
			ItemStack output = recipe.getOutput(input, ingredient);
			if(!output.isEmpty())
			{
				cir.setReturnValue(output);
				return;
			}
		}
	}
}
