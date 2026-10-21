package com.oblivioussp.spartanweaponry.util;

import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemRandomizer 
{
	/**
	 * @param random the mob's own random. 1.20.1 used the level's random, which belongs to the server thread: mobs
	 * equipped while structures generate on other threads crashed with C2ME (issue #170)
	 */
	public static ItemStack generate(RandomSource random, List<Item> items)
	{
		float weaponRand = random.nextFloat();
		float divider = 1.0f / items.size();
		int idx = Mth.floor(weaponRand / divider);
		idx = idx > items.size() - 1 ? items.size() - 1 : idx;
		
		return new ItemStack(items.get(idx));
	}
}
