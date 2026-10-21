package com.oblivioussp.spartanweaponry.compat.jei;

import net.minecraft.core.registries.Registries;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.core.registries.BuiltInRegistries;

public class TippedProjectileRecipeMaker
{
	public static List<RecipeHolder<CraftingRecipe>> getRecipes(Item projectile, Item tippedProjectile)
	{
		List<RecipeHolder<CraftingRecipe>> list = new ArrayList<>();
		String recipeGroup = "jei.spartanweaponry.tipped_projectile";
    
		for (Holder.Reference<Potion> potionType : BuiltInRegistries.POTION.holders().toList()) 
		{
			if (potionType.value() != Potions.WATER.value() && potionType.value() != Potions.MUNDANE.value() && potionType.value() != Potions.THICK.value() && 
						potionType.value() != Potions.AWKWARD.value()) 
			{
				ItemStack projStack = new ItemStack(projectile);
				ItemStack potionStack = PotionContents.createItemStack(Items.LINGERING_POTION, potionType);
				ItemStack outputStack = PotionHelper.setPotion(new ItemStack(tippedProjectile, 8), potionType);
        
				Ingredient projIngredient = Ingredient.of(new ItemStack[] { projStack });
				Ingredient potionIngredient = Ingredient.of(new ItemStack[] { potionStack });
				
				NonNullList<Ingredient> recipeIngredients = NonNullList.of(Ingredient.EMPTY, new Ingredient[] {
						projIngredient, projIngredient, projIngredient, 
						projIngredient, potionIngredient, projIngredient, 
						projIngredient, projIngredient, projIngredient});
				
        		// The leading "/" marks the id as synthetic (EMI reads the JEI recipes and looks them up otherwise)
        		ResourceLocation recipeResLoc = ResourceLocation.fromNamespaceAndPath("spartanweaponry", "/tipped_projectile." + BuiltInRegistries.ITEM.getKey(outputStack.getItem()).getPath() + Potion.getName(Optional.of(potionType), ".effect."));
        		ShapedRecipe recipe = new ShapedRecipe(recipeGroup, CraftingBookCategory.MISC, new ShapedRecipePattern(3, 3, recipeIngredients, Optional.empty()), outputStack);
        		list.add(new RecipeHolder<>(recipeResLoc, recipe));
      		} 
    	} 
    	return list;
	}
}