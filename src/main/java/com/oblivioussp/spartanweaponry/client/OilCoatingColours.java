package com.oblivioussp.spartanweaponry.client;

import net.minecraft.util.FastColor;

import java.util.Optional;

import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.minecraft.core.registries.BuiltInRegistries;

public class OilCoatingColours
{
	public static final ItemColor OIL_COATED_WEAPON = (stack, idx) -> 
	{
		if(idx != 100) return 0xFFFFFFFF;
		Optional<IOilHandler> oilLazyHandler = Optional.ofNullable(stack.getCapability(ModCapabilities.OIL_CAPABILITY));
		if(oilLazyHandler.isPresent())
		{
			IOilHandler oilHandler = oilLazyHandler.get();
			// ARGB: the oil colour, opaque; not oiled = fully transparent coating layer (as in 1.20.1)
			return oilHandler.isOiled() ? FastColor.ARGB32.opaque(oilHandler.getEffect().get().getColor(stack)) : 0x00000000;
		}
		return 0;
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
