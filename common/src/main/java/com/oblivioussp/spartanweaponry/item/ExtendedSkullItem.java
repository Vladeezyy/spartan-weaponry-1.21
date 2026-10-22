package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.List;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

public class ExtendedSkullItem extends StandingAndWallBlockItem 
{

	public ExtendedSkullItem(Block floorBlockIn, Block wallBlockIn, Properties builder, Direction directionIn) 
	{
		super(floorBlockIn, wallBlockIn, builder, directionIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn) 
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + "." + BuiltInRegistries.ITEM.getKey(this).getPath() + ".desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			tooltip.flush();
			super.appendHoverText(stack, levelIn, display, tooltipOut, flagIn);
		}
		finally
		{
			tooltip.flush();
		}
	}
}
