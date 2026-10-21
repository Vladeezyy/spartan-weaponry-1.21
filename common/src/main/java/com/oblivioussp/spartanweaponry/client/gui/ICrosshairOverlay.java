package com.oblivioussp.spartanweaponry.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.Gui;

public interface ICrosshairOverlay 
{
	void render(Gui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight, ItemStack stack);
}
