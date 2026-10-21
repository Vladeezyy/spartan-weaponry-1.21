package com.oblivioussp.spartanweaponry.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.FastColor;


import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.core.component.DataComponents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.core.registries.BuiltInRegistries;

public class OilCoatingColours
{
	/**
	 * Asked for every quad of the coating layer of every oiled weapon on screen, every frame. It used to make an oil
	 * capability handler (reading the item's NBT) each time; now it reads the oil tag in place, and the colour of the
	 * last item data is kept, since the quads of one weapon come one after another (render thread only).
	 */
	private static Object lastData;
	private static int lastColour;

	public static final ItemColor OIL_COATED_WEAPON = (stack, idx) -> 
	{
		if(idx != 100) return 0xFFFFFFFF;
		Object data = stack.get(DataComponents.CUSTOM_DATA);
		if(data != null && data == lastData)
			return lastColour;
		int colour;
		if(!stack.is(ModItemTags.OILABLE_WEAPONS))
			colour = 0;
		else
			// ARGB: the oil colour, opaque; not oiled = fully transparent coating layer (as in 1.20.1)
			colour = OilHandler.isOiled(stack) ? FastColor.ARGB32.opaque(OilHelper.getOilFromStack(stack).getColor(stack)) : 0x00000000;
		lastData = data;
		lastColour = colour;
		return colour;
	};

	@SuppressWarnings("deprecation")
	public static void reload() 
	{
		if(FMLEnvironment.dist == Dist.CLIENT)
		{
			BuiltInRegistries.ITEM.getTagOrEmpty(ModItemTags.OILABLE_WEAPONS).forEach((item) -> Minecraft.getInstance().getItemColors().register(OIL_COATED_WEAPON, item.value()));
		}
	}

}
