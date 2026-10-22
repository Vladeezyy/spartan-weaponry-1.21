package com.oblivioussp.spartanweaponry.compat.jei;

import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.core.registries.Registries;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.registries.BuiltInRegistries;

public class TippedProjectileRecipeMaker
{
	public static List<RecipeHolder<CraftingRecipe>> getRecipes(IVanillaRecipeFactory factory, Item projectile, Item tippedProjectile)
	{
		List<RecipeHolder<CraftingRecipe>> list = new ArrayList<>();
		String recipeGroup = "jei.spartanweaponry.tipped_projectile";
    
		for (Holder.Reference<Potion> potionType : BuiltInRegistries.POTION.listElements().toList()) 
		{
			if (potionType.value() != Potions.WATER.value() && potionType.value() != Potions.MUNDANE.value() && potionType.value() != Potions.THICK.value() && 
						potionType.value() != Potions.AWKWARD.value()) 
			{
				ItemStack potionStack = PotionContents.createItemStack(Items.LINGERING_POTION, potionType);
				ItemStack outputStack = PotionHelper.setPotion(new ItemStack(tippedProjectile, 8), potionType);
        
				// 1.21.2+: an ingredient is a set of items; the potion is shown through the slot display
				CraftingRecipe recipe = factory.createShapedRecipeBuilder(CraftingBookCategory.MISC, new SlotDisplay.ItemStackSlotDisplay(outputStack))
						.define('#', Ingredient.of(projectile))
						.define('P', Ingredient.of(Items.LINGERING_POTION), new SlotDisplay.ItemStackSlotDisplay(potionStack))
						.pattern("###").pattern("#P#").pattern("###")
						.group(recipeGroup)
						.build();
				
        		// The leading "/" marks the id as synthetic (EMI reads the JEI recipes and looks them up otherwise)
        		Identifier recipeResLoc = Identifier.fromNamespaceAndPath("spartanweaponry", "/tipped_projectile." + BuiltInRegistries.ITEM.getKey(outputStack.getItem()).getPath() + PotionHelper.getName(Optional.of(potionType), ".effect."));
        		list.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, recipeResLoc), recipe));
      		} 
    	} 
    	return list;
	}
}