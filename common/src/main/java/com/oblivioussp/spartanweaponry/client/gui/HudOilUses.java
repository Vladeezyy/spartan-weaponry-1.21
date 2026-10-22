package com.oblivioussp.spartanweaponry.client.gui;

import org.joml.Matrix3x2fStack;
import net.minecraft.core.Holder;

import org.joml.Matrix4fStack;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.client.gui.AlignmentHelper.Alignment;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.client.gui.Gui;

public class HudOilUses
{
	
	public static void render(Gui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight)
	{
		RenderSystem.assertOnRenderThread();
		
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		LocalPlayer player = mc.player;
		
		ItemStack weaponStack = ItemStack.EMPTY;
		ItemStack oilStack = ItemStack.EMPTY;
		int usesCount = 0;
		Alignment align = ClientConfig.INSTANCE.oilUsesHudAlignment.get();
		String usesStr = "";
		int offsetX = 0;
		int offsetY = 0;
		
		weaponStack = player.getMainHandItem();
		
		Optional<IOilHandler> handlerOpt = Optional.ofNullable(ModCapabilities.getOil(weaponStack));
		if(!handlerOpt.isPresent())
			return;
		
		IOilHandler oilHandler = handlerOpt.get();
		if(!oilHandler.isOiled())
			return;
		
		Optional<Holder<Potion>> potionOpt = oilHandler.getPotion();
		oilStack = potionOpt.isPresent() ? OilHelper.makePotionOilStack(potionOpt.get()) : OilHelper.makeOilStack(oilHandler.getEffect().get());
		usesCount = oilHandler.getUsesLeft();
		
		usesStr = String.format("%d/%d", usesCount, oilHandler.getEffect().get().getMaxUses());
		offsetX = AlignmentHelper.getAlignedX(align, ClientConfig.INSTANCE.oilUsesHudOffsetX.get(), 22);
		offsetY = AlignmentHelper.getAlignedY(align, ClientConfig.INSTANCE.oilUsesHudOffsetY.get(), 22);

//        MultiBufferSource.BufferSource renderBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        
        guiGraphics.renderFakeItem(oilStack, offsetX - 17, offsetY);
        
		Matrix3x2fStack guiPose = guiGraphics.pose();
		guiPose.pushMatrix();
		guiPose.identity();
        guiGraphics.drawString(font, usesStr, offsetX , offsetY + 6, 0xFFFFFFFF);
		guiPose.popMatrix();
//		font.drawInBatch(usesStr, offsetX , offsetY + 6, 0xFFFFFF, true, poseStack.last().pose(), renderBuffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
		
//		renderBuffer.endBatch();
	}
}
