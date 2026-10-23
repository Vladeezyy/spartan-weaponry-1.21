package com.oblivioussp.spartanweaponry.item.crafting;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 26.3: brewing stand recipes are data (RecipeType.BREWING of vanilla's BrewingRecipe; NeoForge's brewing recipe
 * registry and the PotionBrewing class are gone). A recipe in the data ({"type": "spartanweaponry:oil_brewing"}) that
 * asks one of the mod's code recipes. The input and reagent ingredients list the items the stand takes for it (the
 * stand's slot and reagent checks come from them); the output only matters to recipe viewers.
 */
public class CodeBrewingRecipe extends net.minecraft.world.item.crafting.BrewingRecipe
{
	private final BrewingRecipe recipe;
	private final RecipeSerializer<CodeBrewingRecipe> serializer;

	public CodeBrewingRecipe(BrewingRecipe recipe, Ingredient input, Ingredient reagent, ItemStackTemplate output, Supplier<RecipeSerializer<CodeBrewingRecipe>> serializer)
	{
		super(new PotionIngredient(input, Optional.empty()), new PotionIngredient(reagent, Optional.empty()), output);
		this.recipe = recipe;
		this.serializer = serializer.get();
	}

	public BrewingRecipe recipe()
	{
		return recipe;
	}

	@Override
	public boolean matches(BrewingInput input, Level level)
	{
		return matches(input);
	}

	@Override
	public boolean matches(BrewingInput input)
	{
		return !recipe.getOutput(input.input(), input.reagent()).isEmpty();
	}

	@Override
	public ItemStack assemble(BrewingInput input)
	{
		return recipe.getOutput(input.input(), input.reagent());
	}

	@Override
	public RecipeSerializer<net.minecraft.world.item.crafting.BrewingRecipe> getSerializer()
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		RecipeSerializer<net.minecraft.world.item.crafting.BrewingRecipe> raw = (RecipeSerializer)serializer;
		return raw;
	}

	/** A serializer for a recipe without fields, made when the data is read (the items are registered then). */
	public static RecipeSerializer<CodeBrewingRecipe> serializer(Supplier<CodeBrewingRecipe> factory)
	{
		return new RecipeSerializer<>(MapCodec.unit(factory), StreamCodec.of((RegistryFriendlyByteBuf buffer, CodeBrewingRecipe value) -> {}, buffer -> factory.get()));
	}
}
