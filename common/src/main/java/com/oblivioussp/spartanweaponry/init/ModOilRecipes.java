package com.oblivioussp.spartanweaponry.init;

import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.item.crafting.OilBrewingRecipe;
import com.oblivioussp.spartanweaponry.item.crafting.PotionToOilBrewingRecipe;
import com.oblivioussp.spartanweaponry.util.Config;

import com.oblivioussp.spartanweaponry.init.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import com.oblivioussp.spartanweaponry.item.crafting.CodeBrewingRecipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;

public class ModOilRecipes
{
	public static OilBrewingRecipe oilRecipes = null;
	public static PotionToOilBrewingRecipe potionToOilRecipes = null;
	
	// 26.3: the brewing recipes are data (data/spartanweaponry/recipe/*_brewing.json, CodeBrewingRecipe), made when the
	// recipes are read
	public static CodeBrewingRecipe oilBrewing()
	{
		if(oilRecipes == null)
			oilRecipes = new OilBrewingRecipe();
		if(OilBrewingRecipe.getValidMixes().isEmpty())
			loadOilMixes();
		Item[] reagents = OilBrewingRecipe.getValidMixes().stream().flatMap(mix -> mix.brewingIngredient.items()).map(Holder::value).distinct().toArray(Item[]::new);
		return new CodeBrewingRecipe(oilRecipes, Ingredient.of(ModItems.WEAPON_OIL.get()), Ingredient.of(reagents),
				new ItemStackTemplate(ModItems.WEAPON_OIL.get()), ModRecipeSerializers.OIL_BREWING::get);
	}
	
	public static CodeBrewingRecipe potionToOilBrewing()
	{
		if(potionToOilRecipes == null)
			potionToOilRecipes = new PotionToOilBrewingRecipe();
		return new CodeBrewingRecipe(potionToOilRecipes, Ingredient.of(Items.POTION), Ingredient.of(ModItems.GREASE_BALL.get()),
				new ItemStackTemplate(ModItems.WEAPON_OIL.get()), ModRecipeSerializers.POTION_TO_OIL_BREWING::get);
	}
	
	/** The oil mixes; the config switch (disableOilRecipes) is asked when brewing, so the reagents stay known */
	public static void loadOilMixes()
	{
		OilBrewingRecipe.clearMixes();
		
		OilBrewingRecipe.addBaseOilMix(Ingredient.of(Items.GLISTERING_MELON_SLICE), OilEffects.UNDEAD.get());
		OilBrewingRecipe.addOilMix(OilEffects.UNDEAD.get(), Ingredient.of(Items.GOLDEN_APPLE), OilEffects.UNDEAD_STRONG.get());
		
		OilBrewingRecipe.addBaseOilMix(Ingredient.of(Items.MAGMA_CREAM), OilEffects.ARTHOPOD.get());
		OilBrewingRecipe.addOilMix(OilEffects.ARTHOPOD.get(), Ingredient.of(Items.FIRE_CHARGE), OilEffects.ARTHOPOD_STRONG.get());
		
		OilBrewingRecipe.addBaseOilMix(Ingredient.of(Items.PACKED_ICE), OilEffects.CRYOTIC.get());
		OilBrewingRecipe.addOilMix(OilEffects.CRYOTIC.get(), Ingredient.of(Items.BLUE_ICE), OilEffects.CRYOTIC_STRONG.get());
		
		OilBrewingRecipe.addOilMix(OilEffects.UNDEAD.get(), Ingredient.of(Items.FERMENTED_SPIDER_EYE), OilEffects.NECROTIC.get());
		OilBrewingRecipe.addOilMix(OilEffects.NECROTIC.get(), Ingredient.of(Items.GLOWSTONE_DUST), OilEffects.NECROTIC_STRONG.get());
		OilBrewingRecipe.addOilMix(OilEffects.UNDEAD_STRONG.get(), Ingredient.of(Items.FERMENTED_SPIDER_EYE), OilEffects.NECROTIC_STRONG.get());
		
		// TODO: Change Creeper Oil recipe? (Something related to cats? Cat Fur?)
		OilBrewingRecipe.addOilMix(OilEffects.ARTHOPOD.get(), Ingredient.of(Items.FERMENTED_SPIDER_EYE), OilEffects.CREEPER.get());
		OilBrewingRecipe.addOilMix(OilEffects.CREEPER.get(), Ingredient.of(Items.FIRE_CHARGE), OilEffects.CREEPER_STRONG.get());
		OilBrewingRecipe.addOilMix(OilEffects.ARTHOPOD_STRONG.get(), Ingredient.of(Items.FERMENTED_SPIDER_EYE), OilEffects.CREEPER_STRONG.get());
		
		OilBrewingRecipe.addOilMix(OilEffects.ENDER.get(), Ingredient.of(Items.FERMENTED_SPIDER_EYE), OilEffects.AQUATIC.get());
		OilBrewingRecipe.addOilMix(OilEffects.AQUATIC.get(), Ingredient.of(Items.SPONGE), OilEffects.AQUATIC_STRONG.get());
		OilBrewingRecipe.addOilMix(OilEffects.ENDER_STRONG.get(), Ingredient.of(Items.FERMENTED_SPIDER_EYE), OilEffects.AQUATIC_STRONG.get());
		
		OilBrewingRecipe.addBaseOilMix(Ingredient.of(Items.PRISMARINE_CRYSTALS), OilEffects.ENDER.get());
		OilBrewingRecipe.addOilMix(OilEffects.ENDER.get(), Ingredient.of(Items.GLOW_INK_SAC), OilEffects.ENDER_STRONG.get());

		OilBrewingRecipe.addBaseOilMix(Ingredient.of(Items.WITHER_ROSE), OilEffects.WITHER.get());
		OilBrewingRecipe.addOilMix(OilEffects.WITHER.get(), Ingredient.of(Items.WITHER_SKELETON_SKULL), OilEffects.WITHER_STRONG.get());
		OilBrewingRecipe.addOilMix(OilEffects.WITHER.get(), Ingredient.of(Items.BONE), OilEffects.WITHER_LONG.get());
	}
}
