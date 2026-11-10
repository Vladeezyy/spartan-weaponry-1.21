package com.oblivioussp.spartanweaponry.item.crafting;

import java.util.List;

import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.util.LoadedTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * Smelting with an item tag as the result: the first item of the tag when the recipes load (an empty stack for an empty
 * tag: nothing is made). A SmeltingRecipe, as RecipeType.SMELTING promises: other mods cast the smelting recipes to it
 * and read the result (TagBlastingRecipe: Alloy Forgery crashed with a ClassCastException on a blasting recipe that
 * was only a cooking recipe, Fabric 26.1.2).
 */
public class TagSmeltingRecipe extends SmeltingRecipe implements ITagCookingRecipe
{
	protected final TagKey<Item> resultTag;
	
	public TagSmeltingRecipe(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn) 
	{
		super(groupIn, categoryIn, inputIngredientIn, LoadedTags.firstItem(resultTagIn).map(ItemStack::new).orElse(ItemStack.EMPTY), experienceIn, cookTimeIn);
		resultTag = resultTagIn;
	}

	@Override
	public List<RecipeDisplay> display()
	{
		return List.of(new FurnaceRecipeDisplay(input().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.TagSlotDisplay(resultTag),
				new SlotDisplay.ItemSlotDisplay(furnaceIcon()), cookingTime(), experience()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public RecipeSerializer<SmeltingRecipe> getSerializer() 
	{
		// Typed to vanilla's serializer; ours makes this class, a SmeltingRecipe
		return (RecipeSerializer<SmeltingRecipe>)(RecipeSerializer<?>)ModRecipeSerializers.TAGGED_SMELTING.get();
	}

	@Override
	public CookingBookCategory getCategory() 
	{
		return category();
	}

	@Override
	public Ingredient getInputIngredient() 
	{
		return input();
	}

	@Override
	public TagKey<Item> getResultTag()
	{
		return resultTag;
	}

	@Override
	public int getCookTime() 
	{
		return cookingTime();
	}

	@Override
	public float getExperienceDrop()
	{
		return experience();
	}
}
