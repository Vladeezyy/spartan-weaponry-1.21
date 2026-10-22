package com.oblivioussp.spartanweaponry.client.gui;

import net.minecraft.client.renderer.RenderPipelines;

import com.oblivioussp.spartanweaponry.util.ItemNbt;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.util.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.Gui;

public class HudCrosshairHeavyCrossbow
{
	public static void render(Gui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight, ItemStack equippedStack) 
	{
		RenderSystem.assertOnRenderThread();
		
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		
		if((!ClientConfig.INSTANCE.disableNewCrosshairsCrossbow.get() || ClientConfig.INSTANCE.forceCompatibilityCrosshairs.get()) &&
				equippedStack.getItem() instanceof HeavyCrossbowItem crossbowItem)	// Assert that the equipped stack is a Heavy Crossbow; otherwise abort the rendering
		{
//            gui.setBlitOffset(-90);
			
			// Fixed the crosshair size to account for the actual aim area, while retaining scaling.
			int offset = Mth.floor((mc.getWindow().getGuiScaledHeight() / 2) * 0.2f);
			if(equippedStack != null && !equippedStack.isEmpty() && ItemNbt.read(equippedStack).getBooleanOr(HeavyCrossbowItem.NBT_CHARGED, false) && player.getTicksUsingItem() != 0)
			{
				float percentage = Mth.clamp((player.getTicksUsingItem() + partialTicks) / crossbowItem.getAimTicks(equippedStack), 0.0f, 1.0f);
				offset *= (1.0f - percentage);
			}

			
//	        RenderSystem.setShaderTexture(0, );
//            gui.setupOverlayRenderState(true, false);
			
			if(ClientConfig.INSTANCE.forceCompatibilityCrosshairs.get())
			{
				int crossOriginX = (mc.getWindow().getGuiScaledWidth() - 15) / 2;
				int crossOriginY = (mc.getWindow().getGuiScaledHeight() - 15) / 2;
				
				offset = Mth.floor(Math.sqrt((offset * offset) / 2.0));
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 - offset, crossOriginY + 2 - offset, 11, 12, 4, 4, 256, 256);	// Top-Left Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 + 7 + offset, crossOriginY + 2 - offset, 18, 12, 4, 4, 256, 256);	// Top-Right Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 - offset, crossOriginY + 2 + 7 + offset, 11, 19, 4, 4, 256, 256);	// Bottom-Left Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 + 7 + offset, crossOriginY + 2 + 7 + offset, 18, 19, 4, 4, 256, 256);	// Bottom-Right Part
			}
			else
			{
				int centreOriginX = (mc.getWindow().getGuiScaledWidth() - 3) / 2;
				int centreOriginY = (mc.getWindow().getGuiScaledHeight() - 3) / 2;
				
				//offset = 0;
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX, centreOriginY, 4, 4, 3, 3, 256, 256);		// Center Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX + 1, centreOriginY - 4 - offset, 5, 0, 1, 4, 256, 256);	// Top Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX + 1, centreOriginY + 3 + offset, 5, 7, 1, 4, 256, 256);	// Bottom Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX - 4 - offset, centreOriginY + 1, 0, 5, 4, 1, 256, 256);	// Left Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX + 3 + offset, centreOriginY + 1, 0, 5, 4, 1, 256, 256);	// Right Part
			}
			
		}
	}
}
