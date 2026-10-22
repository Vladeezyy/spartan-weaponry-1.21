package com.oblivioussp.spartanweaponry.client.inventory;


import net.minecraft.util.ARGB;
import com.oblivioussp.spartanweaponry.inventory.tooltip.OilCoatingTooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ClientOilCoatingTooltip implements ClientTooltipComponent
{
	private final ItemStack oilStack;
	private final String text;
	
	public ClientOilCoatingTooltip(OilCoatingTooltip tooltipIn)
	{
		oilStack = tooltipIn.getOilStack();
		text = String.format("%d/%d", tooltipIn.getUsesLeft(), tooltipIn.getMaxUses());
	}
	
	@Override
	public int getHeight(Font font) 
	{
		return 20;
	}

	@Override
	public int getWidth(Font fontIn) 
	{
		return 20 + 2 + fontIn.width(oilStack.getHoverName());
	}
	
	

	@Override
	public void renderImage(Font fontIn, int posXIn, int posYIn, int widthIn, int heightIn, GuiGraphics guiGraphics)
	{
		guiGraphics.renderItem(oilStack, posXIn, posYIn + 1);
	}
	
	@Override
	public void renderText(GuiGraphics guiGraphics, Font fontIn, int posXIn, int posYIn)
	{
		guiGraphics.drawString(fontIn, oilStack.getHoverName(), posXIn + 20, posYIn, 0xFFFFFFFF, true);
		guiGraphics.drawString(fontIn, text, posXIn + 20, posYIn + 10, ARGB.opaque(ChatFormatting.GOLD.getColor()), true);
	}
}
