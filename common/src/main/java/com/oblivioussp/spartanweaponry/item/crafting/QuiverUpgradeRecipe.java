package com.oblivioussp.spartanweaponry.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

/**
 * Smithing a quiver into a bigger one. 1.21's smithing transform copies the components of the base item, so the
 * quiver contents carry over by themselves (1.20.1 resized the copied NBT inventory here).
 */
public class QuiverUpgradeRecipe extends SmithingTransformRecipe
{
	protected final Ingredient templateIn, baseIn, additionIn;
	protected final ItemStack resultOut;

	public QuiverUpgradeRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result)
	{
		super(template, base, addition, result);
		templateIn = template;
		baseIn = base;
		additionIn = addition;
		resultOut = result;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return ModRecipeSerializers.QUIVER_UPGRADE_SMITHING.get();
	}

	@Override
	public RecipeType<?> getType() 
	{
		return RecipeType.SMITHING;
	}

	public static class Serializer implements RecipeSerializer<QuiverUpgradeRecipe>
	{
		private static final MapCodec<QuiverUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
				Ingredient.CODEC.fieldOf("template").forGetter((recipe) -> recipe.templateIn),
				Ingredient.CODEC.fieldOf("base").forGetter((recipe) -> recipe.baseIn),
				Ingredient.CODEC.fieldOf("addition").forGetter((recipe) -> recipe.additionIn),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter((recipe) -> recipe.resultOut))
				.apply(instance, QuiverUpgradeRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, QuiverUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, (recipe) -> recipe.templateIn,
				Ingredient.CONTENTS_STREAM_CODEC, (recipe) -> recipe.baseIn,
				Ingredient.CONTENTS_STREAM_CODEC, (recipe) -> recipe.additionIn,
				ItemStack.STREAM_CODEC, (recipe) -> recipe.resultOut,
				QuiverUpgradeRecipe::new);

		public Serializer() {}

		@Override
		public MapCodec<QuiverUpgradeRecipe> codec()
		{
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, QuiverUpgradeRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
