package com.oblivioussp.spartanweaponry.loot;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ModLootTables 
{
	public static final ResourceLocation INJECT_VILLAGE_WEAPONSMITH = ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "inject/" + BuiltInLootTables.VILLAGE_WEAPONSMITH.location().getPath());
	public static final ResourceLocation INJECT_VILLAGE_FLETCHER = ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "inject/" + BuiltInLootTables.VILLAGE_FLETCHER.location().getPath());
	public static final ResourceLocation INJECT_END_CITY_TREASURE = ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "inject/" + BuiltInLootTables.END_CITY_TREASURE.location().getPath());
}
