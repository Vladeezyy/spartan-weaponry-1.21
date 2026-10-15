package com.oblivioussp.spartanweaponry.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Smelting / blasting with an item tag as the result ({@code "result_tag"}): weapons melt into nuggets of whatever mod
 * provides the material. The 1.20.1 checks done while reading the JSON (disabled weapon types, an empty result tag)
 * are recipe conditions now ({@code spartanweaponry:type_disabled}, {@code neoforge:tag_empty}).
 */
public class TagCookingRecipeSerializer<T extends ITagCookingRecipe> implements RecipeSerializer<T> 
{
	private final MapCodec<T> codec;
	private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;
	
	public TagCookingRecipeSerializer(RecipeFactory<T> factoryIn, int defaultCookingTimeIn)
	{
		codec = RecordCodecBuilder.mapCodec((instance) -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(ITagCookingRecipe::getGroup),
				CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(ITagCookingRecipe::getCategory),
				Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ITagCookingRecipe::getInputIngredient),
				TagKey.codec(Registries.ITEM).fieldOf("result_tag").forGetter(ITagCookingRecipe::getResultTag),
				Codec.FLOAT.fieldOf("experience").orElse(0.0f).forGetter(ITagCookingRecipe::getExperienceDrop),
				Codec.INT.fieldOf("cookingtime").orElse(defaultCookingTimeIn).forGetter(ITagCookingRecipe::getCookTime))
				.apply(instance, factoryIn::create));
		streamCodec = StreamCodec.of((buffer, recipe) -> 
		{
			buffer.writeUtf(recipe.getGroup());
			buffer.writeEnum(recipe.getCategory());
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getInputIngredient());
			buffer.writeResourceLocation(recipe.getResultTag().location());
			buffer.writeFloat(recipe.getExperienceDrop());
			buffer.writeVarInt(recipe.getCookTime());
		}, (buffer) -> factoryIn.create(buffer.readUtf(), buffer.readEnum(CookingBookCategory.class), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
				TagKey.create(Registries.ITEM, buffer.readResourceLocation()), buffer.readFloat(), buffer.readVarInt()));
	}

	@Override
	public MapCodec<T> codec()
	{
		return codec;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
	{
		return streamCodec;
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends ITagCookingRecipe>
	{
		T create(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn);
	}
	
}
