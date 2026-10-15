package com.oblivioussp.spartanweaponry.item.crafting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.oblivioussp.spartanweaponry.api.crafting.condition.TypeDisabledCondition;

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
 * are recipe conditions now ({@code spartanweaponry:type_disabled}, {@code neoforge:tag_empty}). As in 1.20.1, each
 * entry of an "ingredient" array may name a {@code "disabled_type"}: the entry is left out when that weapon type is
 * disabled in the config.
 */
public class TagCookingRecipeSerializer<T extends ITagCookingRecipe> implements RecipeSerializer<T> 
{
	private static final String DISABLED_TYPE = "disabled_type";
	private static final Codec<Ingredient> INPUT_CODEC = Codec.PASSTHROUGH.flatXmap(TagCookingRecipeSerializer::readInput, 
			(ingredient) -> Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).map((json) -> new Dynamic<>(JsonOps.INSTANCE, json)));
	
	private final MapCodec<T> codec;
	private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;
	
	public TagCookingRecipeSerializer(RecipeFactory<T> factoryIn, int defaultCookingTimeIn)
	{
		codec = RecordCodecBuilder.mapCodec((instance) -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(ITagCookingRecipe::getGroup),
				CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(ITagCookingRecipe::getCategory),
				INPUT_CODEC.fieldOf("ingredient").forGetter(ITagCookingRecipe::getInputIngredient),
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

	private static <O> DataResult<Ingredient> readInput(Dynamic<O> input)
	{
		Optional<Stream<Dynamic<O>>> entries = input.asStreamOpt().result();
		if(entries.isEmpty())
			return Ingredient.CODEC_NONEMPTY.parse(input);
		List<Dynamic<O>> enabled = entries.get().filter((entry) -> entry.get(DISABLED_TYPE).asString().result()
				.map((type) -> !TypeDisabledCondition.disabledRecipeTypes.contains(type)).orElse(true))
				.map((entry) -> entry.remove(DISABLED_TYPE)).toList();
		return Ingredient.CODEC_NONEMPTY.parse(input.createList(enabled.stream()));
	}
	
	@FunctionalInterface
	public interface RecipeFactory<T extends ITagCookingRecipe>
	{
		T create(String groupIn, CookingBookCategory categoryIn, Ingredient inputIngredientIn, TagKey<Item> resultTagIn, float experienceIn, int cookTimeIn);
	}
	
}
