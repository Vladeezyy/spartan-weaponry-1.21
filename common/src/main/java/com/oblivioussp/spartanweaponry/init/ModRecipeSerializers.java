package com.oblivioussp.spartanweaponry.init;

import net.minecraft.world.item.Item;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import net.minecraft.core.registries.Registries;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.crafting.condition.TypeDisabledCondition;
import com.oblivioussp.spartanweaponry.item.crafting.ApplyOilRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.QuiverUpgradeRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.TagBlastingRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.TagCookingRecipeSerializer;
import com.oblivioussp.spartanweaponry.item.crafting.TagSmeltingRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.TippedProjectileBaseRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModRecipeSerializers
{
	public static final Registrar<RecipeSerializer<?>> REGISTRY = Registrar.create(Registries.RECIPE_SERIALIZER);
	
	public static final Registrar.Entry<RecipeSerializer<?>, TippedProjectileBaseRecipe.Serializer> TIPPED_PROJECTILE_BASE = REGISTRY.register("tipped_projectile", () -> new TippedProjectileBaseRecipe.Serializer());
	public static final Registrar.Entry<RecipeSerializer<?>, QuiverUpgradeRecipe.Serializer> QUIVER_UPGRADE_SMITHING = REGISTRY.register("quiver_upgrade_smithing", () -> new QuiverUpgradeRecipe.Serializer());
	public static final Registrar.Entry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<ApplyOilRecipe>> APPLY_OIL = REGISTRY.register("apply_oil", () -> new SimpleCraftingRecipeSerializer<>(ApplyOilRecipe::new));
	
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<TagSmeltingRecipe>> TAGGED_SMELTING = REGISTRY.register("tag_smelting", () -> new TagCookingRecipeSerializer<>(TagSmeltingRecipe::new, 200));
	public static final Registrar.Entry<RecipeSerializer<?>, RecipeSerializer<TagBlastingRecipe>> TAGGED_BLASTING = REGISTRY.register("tag_blasting", () -> new TagCookingRecipeSerializer<>(TagBlastingRecipe::new, 100));
	
}
