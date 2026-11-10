package com.oblivioussp.spartanweaponry.item.crafting;

import java.util.List;
import java.util.Optional;

import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.util.LoadedTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * Smelting with an item tag as the result: the first item of the tag when the recipes load. A SmeltingRecipe, as
 * RecipeType.SMELTING promises: other mods cast the smelting recipes to it and read the result (TagBlastingRecipe:
 * Alloy Forgery crashed with a ClassCastException on a blasting recipe that was only a cooking recipe).
 */
public class TagSmeltingRecipe extends SmeltingRecipe implements ITagCookingRecipe
{
	protected final TagKey<Item> resultTag;
	/** False for an empty tag: nothing is made (26.1: the result template cannot be empty, a barrier stands in) */
	protected final boolean hasResult;

	public TagSmeltingRecipe(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn)
	{
		this(groupIn, categoryIn, inputIngredientIn, resultTagIn, LoadedTags.firstItem(resultTagIn), experienceIn, cookTimeIn);
	}

	private TagSmeltingRecipe(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, Optional<Item> resultIn, float experienceIn, int cookTimeIn)
	{
		super(new Recipe.CommonInfo(true), new AbstractCookingRecipe.CookingBookInfo(categoryIn, groupIn), inputIngredientIn, new ItemStackTemplate(resultIn.orElse(Items.BARRIER)), experienceIn, cookTimeIn);
		resultTag = resultTagIn;
		hasResult = resultIn.isPresent();
	}

	@Override
	public ItemStack assemble(SingleRecipeInput containerIn)
	{
		return hasResult ? super.assemble(containerIn) : ItemStack.EMPTY;
	}

	@Override
	public List<RecipeDisplay> display()
	{
		return List.of(new FurnaceRecipeDisplay(input().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.TagSlotDisplay(BuiltInRegistries.ITEM.getOrThrow(resultTag)),
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
