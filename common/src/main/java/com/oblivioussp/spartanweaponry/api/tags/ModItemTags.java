package com.oblivioussp.spartanweaponry.api.tags;

import net.minecraft.core.registries.Registries;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * This class contains all the different item tags used by Spartan Weaponry. 
 * Addon authors should add their weapons to these tags as necessary to allow Quivers to work with addon weapons and Advancements to trigger
 * @author ObliviousSpartan
 *
 */
public class ModItemTags 
{
	// Handles and Poles
	public static final TagKey<Item> HANDLES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":handles"));
	public static final TagKey<Item> POLES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":poles"));
	
	// Tags for all weapons of a specified type
	public static final TagKey<Item> DAGGERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":daggers"));
	public static final TagKey<Item> PARRYING_DAGGERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":parrying_daggers"));
	public static final TagKey<Item> LONGSWORDS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":longswords"));
	public static final TagKey<Item> KATANAS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":katanas"));
	public static final TagKey<Item> SABERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":sabers"));
	public static final TagKey<Item> RAPIERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":rapiers"));
	public static final TagKey<Item> GREATSWORDS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":greatswords"));
	public static final TagKey<Item> CLUBS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":clubs"));
	public static final TagKey<Item> CESTUSAE = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":cestusae"));
	public static final TagKey<Item> BATTLE_HAMMERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":battle_hammers"));
	public static final TagKey<Item> WARHAMMERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":warhammers"));
	public static final TagKey<Item> SPEARS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":spears"));
	public static final TagKey<Item> HALBERDS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":halberds"));
	public static final TagKey<Item> PIKES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":pikes"));
	public static final TagKey<Item> LANCES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":lances"));
	public static final TagKey<Item> LONGBOWS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":longbows"));
	public static final TagKey<Item> HEAVY_CROSSBOWS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":heavy_crossbows"));
	public static final TagKey<Item> THROWING_KNIVES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":throwing_knives"));
	public static final TagKey<Item> TOMAHAWKS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":tomahawks"));
	public static final TagKey<Item> JAVELINS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":javelins"));
	public static final TagKey<Item> BOOMERANGS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":boomerangs"));
	public static final TagKey<Item> BATTLEAXES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":battleaxes"));
	public static final TagKey<Item> FLANGED_MACES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":flanged_maces"));
	public static final TagKey<Item> GLAIVES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":glaives"));
	public static final TagKey<Item> QUARTERSTAVES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":quarterstaves"));
	public static final TagKey<Item> SCYTHES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":scythes"));
	
	// Tags for all weapons made from a specific material
	public static final TagKey<Item> WOODEN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":wooden_weapons"));
	public static final TagKey<Item> STONE_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":stone_weapons"));
	public static final TagKey<Item> LEATHER_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":leather_weapons"));
	public static final TagKey<Item> IRON_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":iron_weapons"));
	public static final TagKey<Item> GOLDEN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":golden_weapons"));
	public static final TagKey<Item> DIAMOND_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":diamond_weapons"));
	public static final TagKey<Item> NETHERITE_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":netherite_weapons"));

	public static final TagKey<Item> COPPER_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":copper_weapons"));
	public static final TagKey<Item> TIN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":tin_weapons"));
	public static final TagKey<Item> BRONZE_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":bronze_weapons"));
	public static final TagKey<Item> STEEL_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":steel_weapons"));
	public static final TagKey<Item> SILVER_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":silver_weapons"));
	public static final TagKey<Item> ELECTRUM_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":electrum_weapons"));
	public static final TagKey<Item> LEAD_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":lead_weapons"));
	public static final TagKey<Item> NICKEL_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":nickel_weapons"));
	public static final TagKey<Item> INVAR_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":invar_weapons"));
	public static final TagKey<Item> CONSTANTAN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":constantan_weapons"));
	public static final TagKey<Item> PLATINUM_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":platinum_weapons"));
	public static final TagKey<Item> ALUMINUM_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":aluminum_weapons"));
	
	// Arrows and Bolts
	public static final TagKey<Item> ARROWS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":arrows"));
	public static final TagKey<Item> COPPER_PROJECTILES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":copper_projectiles"));
	public static final TagKey<Item> DIAMOND_PROJECTILES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":diamond_projectiles"));
	public static final TagKey<Item> NETHERITE_PROJECTILES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":netherite_projectiles"));
	public static final TagKey<Item> BOLTS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":bolts"));
	
	// Quivers
	public static final TagKey<Item> ARROW_QUIVERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":arrow_quivers"));
	public static final TagKey<Item> BOLT_QUIVERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":bolt_quivers"));
	public static final TagKey<Item> QUIVERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":quivers"));
	public static final TagKey<Item> SMALL_QUIVERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":small_quivers"));
	public static final TagKey<Item> UPGRADED_QUIVERS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":upgraded_quivers"));
	public static final TagKey<Item> UPGRADED_QUIVERS_MAX = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":upgraded_quivers_max"));
	
	public static final TagKey<Item> EXPLOSIVES = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":explosives"));
	
	public static final TagKey<Item> HEADS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":heads"));
	
	public static final TagKey<Item> THROWING_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":throwing_weapons"));	
	public static final TagKey<Item> HAS_CUSTOM_CROSSHAIR = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":has_custom_crosshair"));
	
	// Materials for repairing weapons
	public static final TagKey<Item> COBBLESTONE = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:cobblestones"));
	public static final TagKey<Item> LEATHER = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:leathers"));
	public static final TagKey<Item> COPPER_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/copper"));
	public static final TagKey<Item> IRON_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/iron"));
	public static final TagKey<Item> GOLD_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/gold"));
	public static final TagKey<Item> DIAMOND = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:gems/diamond"));
	public static final TagKey<Item> NETHERITE_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/netherite"));
	public static final TagKey<Item> TIN_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/tin"));
	public static final TagKey<Item> BRONZE_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/bronze"));
	public static final TagKey<Item> STEEL_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/steel"));
	public static final TagKey<Item> SILVER_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/silver"));
	public static final TagKey<Item> ELECTRUM_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/electrum"));
	public static final TagKey<Item> LEAD_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/lead"));
	public static final TagKey<Item> NICKEL_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/nickel"));
	public static final TagKey<Item> INVAR_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/invar"));
	public static final TagKey<Item> CONSTANTAN_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/constantan"));
	public static final TagKey<Item> PLATINUM_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/platinum"));
	public static final TagKey<Item> ALUMINUM_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "ingots/aluminum"));	// The below two alumin(i)um ingot tag variants combined (for compatibility)
	public static final TagKey<Item> FORGE_ALUMINUM_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/aluminum"));
	public static final TagKey<Item> FORGE_ALUMINIUM_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/aluminium"));
	
/*	public static final TagKey<Item> COPPER_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/copper"));
	public static final TagKey<Item> IRON_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/iron"));
	public static final TagKey<Item> GOLD_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/gold"));
	public static final TagKey<Item> TIN_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/tin"));
	public static final TagKey<Item> BRONZE_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/bronze"));
	public static final TagKey<Item> STEEL_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/steel"));
	public static final TagKey<Item> SILVER_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/silver"));
	public static final TagKey<Item> ELECTRUM_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/gold"));
	public static final TagKey<Item> LEAD_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/lead"));
	public static final TagKey<Item> NICKEL_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/nickel"));
	public static final TagKey<Item> INVAR_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/invar"));
	public static final TagKey<Item> CONSTANTAN_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/constantan"));
	public static final TagKey<Item> PLATINUM_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/platinum"));
	public static final TagKey<Item> ALUMINUM_NUGGET = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:nuggets/aluminum"));*/

	public static final TagKey<Item> GRASS = TagKey.create(Registries.ITEM, ResourceLocation.parse("forge:grass"));
	public static final TagKey<Item> RAW_MEAT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/raw_meat"));
	public static final TagKey<Item> OILABLE_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":oilable_weapons"));
	
	public static final TagKey<Item> ZOMBIE_SPAWN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":zombie_spawn_weapons"));
	public static final TagKey<Item> SKELETON_SPAWN_LONGBOWS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":skeleton_spawn_longbows"));
	public static final TagKey<Item> PIGLIN_SPAWN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":piglin_spawn_weapons"));
	public static final TagKey<Item> PIGLIN_BRUTE_SPAWN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":piglin_brute_spawn_weapons"));
	public static final TagKey<Item> WITHER_SKELETON_SPAWN_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.parse(SpartanWeaponryAPI.MOD_ID + ":wither_skeleton_spawn_weapons"));
}
