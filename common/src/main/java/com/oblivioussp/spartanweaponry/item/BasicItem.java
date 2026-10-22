package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

public class BasicItem extends Item 
{

	public BasicItem(Properties properties) 
	{
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn)
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			tooltip.add(Component.translatable(String.format("tooltip.%s.%s.desc", ModSpartanWeaponry.ID, BuiltInRegistries.ITEM.getKey(this).getPath())).withStyle(ChatFormatting.GRAY));
			tooltip.flush();
			super.appendHoverText(stack, levelIn, display, tooltipOut, flagIn);
		}
		finally
		{
			tooltip.flush();
		}
	}
}
