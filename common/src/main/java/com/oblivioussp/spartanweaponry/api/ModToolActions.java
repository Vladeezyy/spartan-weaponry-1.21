package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.ToolAction;
import java.util.Set;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

public class ModToolActions 
{
	public static final ToolAction MELEE_BLOCK = ToolAction.get(ModSpartanWeaponry.ID + ":melee_block");

	// The NeoForge ItemAbilities the weapons use (same names)
	public static final ToolAction SWORD_DIG = ToolAction.get("sword_dig");
	public static final ToolAction SWORD_SWEEP = ToolAction.get("sword_sweep");
	public static final ToolAction AXE_DIG = ToolAction.get("axe_dig");
	public static final ToolAction AXE_STRIP = ToolAction.get("axe_strip");
	public static final ToolAction AXE_SCRAPE = ToolAction.get("axe_scrape");
	public static final ToolAction AXE_WAX_OFF = ToolAction.get("axe_wax_off");
	public static final Set<ToolAction> DEFAULT_AXE_ACTIONS = Set.of(AXE_DIG, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF);
}
