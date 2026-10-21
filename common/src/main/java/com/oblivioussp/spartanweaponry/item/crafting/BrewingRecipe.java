package com.oblivioussp.spartanweaponry.item.crafting;

import net.minecraft.world.item.ItemStack;

/**
 * A brewing stand recipe with code instead of fixed potions (NeoForge: wrapped in an IBrewingRecipe, Fabric: asked by
 * a mixin in PotionBrewing).
 */
public interface BrewingRecipe
{
	boolean isInput(ItemStack input);

	boolean isIngredient(ItemStack ingredient);

	/** The result, or an empty stack when the recipe does not take the input and ingredient. */
	ItemStack getOutput(ItemStack input, ItemStack ingredient);
}
