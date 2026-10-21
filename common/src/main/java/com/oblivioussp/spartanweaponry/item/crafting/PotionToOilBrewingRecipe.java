package com.oblivioussp.spartanweaponry.item.crafting;

import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.util.Config;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import java.util.Optional;

import com.oblivioussp.spartanweaponry.util.PotionHelper;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

public class PotionToOilBrewingRecipe implements IBrewingRecipe 
{
	@Override
	public boolean isInput(ItemStack input) 
	{
		Optional<Holder<Potion>> inputPotion = PotionHelper.getPotion(input);
		return !Config.INSTANCE.disableOilRecipes.get() && input.is(Items.POTION) && inputPotion.isPresent() && OilHelper.isValidPotion(inputPotion.get());
	}
	
	@Override
	public boolean isIngredient(ItemStack ingredient) 
	{
		return !Config.INSTANCE.disableOilRecipes.get() && ingredient.is(ModItems.GREASE_BALL.get());
	}
	
	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient)
	{
		if(isInput(input) && isIngredient(ingredient))
		{
			return OilHelper.makePotionOilStack(PotionHelper.getPotion(input).orElseThrow());
		}
		
		return ItemStack.EMPTY;
	}
}
