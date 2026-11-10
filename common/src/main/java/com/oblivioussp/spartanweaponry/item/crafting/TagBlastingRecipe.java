package com.oblivioussp.spartanweaponry.item.crafting;

import java.util.List;

import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.util.LoadedTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * Blasting with an item tag as the result: the first item of the tag when the recipes load (an empty stack for an empty
 * tag: nothing is made). A BlastingRecipe, as RecipeType.BLASTING promises: other mods cast the blasting recipes to it
 * and read the result (Alloy Forgery turns them into forge recipes; a cooking recipe of its own crashed it with a
 * ClassCastException when the world loaded, Fabric 26.1.2).
 */
public class TagBlastingRecipe extends BlastingRecipe implements ITagCookingRecipe
{
	protected final TagKey<Item> resultTag;
	
	public TagBlastingRecipe(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn) 
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
	public RecipeSerializer<BlastingRecipe> getSerializer() 
	{
		// Typed to vanilla's serializer; ours makes this class, a BlastingRecipe
		return (RecipeSerializer<BlastingRecipe>)(RecipeSerializer<?>)ModRecipeSerializers.TAGGED_BLASTING.get();
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
