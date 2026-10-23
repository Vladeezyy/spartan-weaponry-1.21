package com.oblivioussp.spartanweaponry.client.gui;

import org.joml.Matrix3x2fStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.client.renderer.RenderPipelines;
import com.oblivioussp.spartanweaponry.init.ModDataComponents;
import net.minecraft.world.item.component.ItemContainerContents;

import com.oblivioussp.spartanweaponry.util.ItemNbt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.client.gui.AlignmentHelper.Alignment;
import com.oblivioussp.spartanweaponry.client.gui.AlignmentHelper.VerticalAlignment;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import com.oblivioussp.spartanweaponry.util.QuiverHelper.IQuiverInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.Gui;

public class HudQuiverAmmo
{
	// 1.20.1: textures/gui/widgets.png (the off hand slot of the hotbar), a sprite since 1.20.2
	protected static final Identifier OFFHAND_SLOT = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");

	public static void render(Gui gui, GuiGraphicsExtractor guiGraphics, float partialTicks, int screenWidth, int screenHeight) 
	{
		RenderSystem.assertOnRenderThread();
		
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		LocalPlayer player = mc.player;
		
		ItemStack quiverStack = ItemStack.EMPTY;
		int ammoCount = 0;
		Alignment align = ClientConfig.INSTANCE.quiverHudAlignment.get();
		String ammoStr = "";
		int offsetX = 0;
		int offsetY = 0;
		
		// Check and see if the weapon equipped has an appropriate quiver first  [first pass]
		for(IQuiverInfo info : QuiverHelper.info)
		{
			if(info.isWeapon(player.getMainHandItem()))
			{
				quiverStack = QuiverHelper.findFirstOfType(player, info);
				break;
			}
		}
		
		// Now check and find the first available quiver if none was found in the first pass [second pass]
		if(quiverStack.isEmpty())
		{
			quiverStack = QuiverHelper.findFirstQuiver(player);
		}
		
		if(quiverStack.isEmpty())
			return;
		
		for(ItemStackTemplate ammoStack : quiverStack.getOrDefault(ModDataComponents.QUIVER_CONTENTS.get(), ItemContainerContents.EMPTY).nonEmptyItems())
			ammoCount += ammoStack.count();
		
		ammoStr = Integer.toString(ammoCount);
		offsetX = AlignmentHelper.getAlignedX(align, ClientConfig.INSTANCE.quiverHudOffsetX.get(), 22);
		offsetY = AlignmentHelper.getAlignedY(align, ClientConfig.INSTANCE.quiverHudOffsetY.get(), 22);
		
		Matrix3x2fStack poseStack = guiGraphics.pose();
		poseStack.pushMatrix();
//        MultiBufferSource.BufferSource renderBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        
//        RenderSystem.setShaderTexture(0, WIDGETS);
        
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, OFFHAND_SLOT, 29, 24, 0, 1, offsetX, offsetY, 22, 22);
		guiGraphics.fakeItem(quiverStack, offsetX + 3, offsetY + 3);
		guiGraphics.text(font, ammoStr, offsetX + 20 - font.width(ammoStr), offsetY + 13, ammoCount == 0 ? 0xFFFF6060 : 0xFFFFC000, true);
//		font.drawInBatch(ammoStr, offsetX + 20 - font.width(ammoStr), offsetY + 13, ammoCount == 0 ? 0xFF6060 : 0xFFC000, true, poseStack.last().pose(), renderBuffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
		
		// Draw the key (in text form) required to open this quiver
		if(!KeyBinds.KEY_ACCESS_QUIVER.isUnbound())
		{
			String inventoryKey = "[" + KeyBinds.KEY_ACCESS_QUIVER.getTranslatedKeyMessage().getString().toUpperCase() + "]";
			int keyTextYOffset = align.getVertical() == VerticalAlignment.TOP ? 22 : -8;
			guiGraphics.text(font, inventoryKey, Math.round(offsetX + 11 - ((float)font.width(inventoryKey) / 2.0f)), Math.round(offsetY + keyTextYOffset), 0xFFFFFFFF, true);
//			font.drawInBatch(inventoryKey, offsetX + 11 - ((float)font.width(inventoryKey) / 2.0f), offsetY + keyTextYOffset, 0xFFFFFF, true, poseStack.last().pose(), renderBuffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
		}
//		renderBuffer.endBatch();
		poseStack.popMatrix();
	}
}
