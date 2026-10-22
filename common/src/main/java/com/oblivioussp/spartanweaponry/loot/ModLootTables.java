package com.oblivioussp.spartanweaponry.loot;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ModLootTables 
{
	public static final Identifier INJECT_VILLAGE_WEAPONSMITH = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "inject/" + BuiltInLootTables.VILLAGE_WEAPONSMITH.identifier().getPath());
	public static final Identifier INJECT_VILLAGE_FLETCHER = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "inject/" + BuiltInLootTables.VILLAGE_FLETCHER.identifier().getPath());
	public static final Identifier INJECT_END_CITY_TREASURE = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "inject/" + BuiltInLootTables.END_CITY_TREASURE.identifier().getPath());
}
