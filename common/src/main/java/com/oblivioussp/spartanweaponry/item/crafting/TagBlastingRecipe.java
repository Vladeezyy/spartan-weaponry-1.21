package com.oblivioussp.spartanweaponry.item.crafting;

import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.Registries;
import java.util.List;
import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/**
 * Blasting with an item tag as the result: the first item of the tag when it is made (1.21.2+: a cooking recipe of its own,
 * vanilla's BlastingRecipe#getSerializer is typed to its own serializer)
 */
public class TagBlastingRecipe extends AbstractCookingRecipe implements ITagCookingRecipe
{
	protected final TagKey<Item> resultTag;
	
	public TagBlastingRecipe(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn) 
	{
		// 26.1: the result template cannot be empty; a barrier, never made (assemble and display use the tag)
		super(new Recipe.CommonInfo(true), new AbstractCookingRecipe.CookingBookInfo(categoryIn, groupIn), inputIngredientIn, new ItemStackTemplate(Items.BARRIER), experienceIn, cookTimeIn);
		resultTag = resultTagIn;
	}
	
	@Override
	public ItemStack assemble(SingleRecipeInput containerIn)
	{
		return BuiltInRegistries.ITEM.get(resultTag).flatMap((tag) -> tag.stream().findFirst()).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	@Override
	public List<RecipeDisplay> display()
	{
		return List.of(new FurnaceRecipeDisplay(input().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.TagSlotDisplay(resultTag),
				new SlotDisplay.ItemSlotDisplay(furnaceIcon()), cookingTime(), experience()));
	}

	@Override
	protected Item furnaceIcon()
	{
		return Items.BLAST_FURNACE;
	}

	@Override
	public RecipeSerializer<TagBlastingRecipe> getSerializer() 
	{
		return (RecipeSerializer<TagBlastingRecipe>)ModRecipeSerializers.TAGGED_BLASTING.get();
	}

	@Override
	public RecipeType<? extends AbstractCookingRecipe> getType()
	{
		return RecipeType.BLASTING;
	}

	@Override
	public RecipeBookCategory recipeBookCategory()
	{
		return RecipeBookCategories.BLAST_FURNACE_MISC;
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
