package com.oblivioussp.spartanweaponry.item.crafting;

import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class TagBlastingRecipe extends BlastingRecipe implements ITagCookingRecipe
{
	protected final TagKey<Item> resultTag;
	protected final Ingredient result;
	
	public TagBlastingRecipe(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn) 
	{
		super(groupIn, categoryIn, inputIngredientIn, ItemStack.EMPTY, experienceIn, cookTimeIn);
		resultTag = resultTagIn;
		result = Ingredient.of(resultTagIn);
	}
	
	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) 
	{
		ItemStack[] items = result.getItems();
		return items.length > 0 ? items[0] : ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack assemble(SingleRecipeInput containerIn, HolderLookup.Provider registryIn)
	{
		return getResultItem(registryIn).copy();
	}

	@Override
	public RecipeSerializer<?> getSerializer() 
	{
		return ModRecipeSerializers.TAGGED_BLASTING.get();
	}

	@Override
	public CookingBookCategory getCategory() 
	{
		return category();
	}

	@Override
	public Ingredient getInputIngredient() 
	{
		return ingredient;
	}

	@Override
	public Ingredient getResultIngredient() 
	{
		return result;
	}

	@Override
	public TagKey<Item> getResultTag()
	{
		return resultTag;
	}

	@Override
	public int getCookTime() 
	{
		return getCookingTime();
	}

	@Override
	public float getExperienceDrop()
	{
		return getExperience();
	}
}
