package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import net.minecraft.core.registries.Registries;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.crafting.condition.TypeDisabledCondition;
import com.oblivioussp.spartanweaponry.item.crafting.ApplyOilRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.CodeBrewingRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.TagBlastingRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.TagCookingRecipeSerializer;
import com.oblivioussp.spartanweaponry.item.crafting.TagSmeltingRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.TippedProjectileBaseRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModRecipeSerializers
{
	public static final Registrar<RecipeSerializer<?>> REGISTRY = Registrar.create(Registries.RECIPE_SERIALIZER);
	
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<TippedProjectileBaseRecipe>> TIPPED_PROJECTILE_BASE = REGISTRY.register("tipped_projectile", () -> TippedProjectileBaseRecipe.Serializer.INSTANCE);
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<ApplyOilRecipe>> APPLY_OIL = REGISTRY.register("apply_oil", () -> ApplyOilRecipe.SERIALIZER);
	
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<TagSmeltingRecipe>> TAGGED_SMELTING = REGISTRY.register("tag_smelting", () -> TagCookingRecipeSerializer.create(TagSmeltingRecipe::new, 200));
	// 26.3: the oil brewing stand recipes (data/spartanweaponry/recipe/oil_brewing.json, potion_to_oil_brewing.json)
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<CodeBrewingRecipe>> OIL_BREWING = REGISTRY.register("oil_brewing", () -> CodeBrewingRecipe.serializer(ModOilRecipes::oilBrewing));
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<CodeBrewingRecipe>> POTION_TO_OIL_BREWING = REGISTRY.register("potion_to_oil_brewing", () -> CodeBrewingRecipe.serializer(ModOilRecipes::potionToOilBrewing));
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<TagBlastingRecipe>> TAGGED_BLASTING = REGISTRY.register("tag_blasting", () -> TagCookingRecipeSerializer.create(TagBlastingRecipe::new, 200));
	
}
