package com.oblivioussp.spartanweaponry.client.gui;

import org.joml.Matrix3x2fStack;
import net.minecraft.client.renderer.RenderPipelines;

import com.oblivioussp.spartanweaponry.util.ItemNbt;

import com.mojang.renderpearl.backend.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;
import com.oblivioussp.spartanweaponry.util.ClientConfig;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.Gui;

public class HudCrosshairThrowingWeapon
{
	public static void render(Gui gui, GuiGraphicsExtractor guiGraphics, float partialTicks, int screenWidth, int screenHeight, ItemStack equippedStack) 
	{
		RenderSystem.assertOnRenderThread();
		
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		
		if((!ClientConfig.INSTANCE.disableNewCrosshairsThrowingWeapon.get() || ClientConfig.INSTANCE.forceCompatibilityCrosshairs.get()) &&
				equippedStack.getItem() instanceof ThrowingWeaponItem throwingWeapon)	// Assert that the equipped stack is a Throwing Weapon; otherwise abort the rendering
		{
			int offset = ClientConfig.INSTANCE.forceCompatibilityCrosshairs.get() ? 20 : 10;;
			if(player.isUsingItem())
			{
				float percentage = Mth.clamp((player.getTicksUsingItem() + partialTicks) / throwingWeapon.getMaxChargeTicks(equippedStack), 0.0f, 1.0f);
				offset *= (1.0f - percentage);
			}
			
			
	
//	        RenderSystem.setShaderTexture(0, HudCrosshair.CROSSHAIR_TEXTURES);
			
			if(ClientConfig.INSTANCE.forceCompatibilityCrosshairs.get())
			{
				int crossOriginX = (mc.getWindow().getGuiScaledWidth() - 15) / 2;
				int crossOriginY = (mc.getWindow().getGuiScaledHeight() - 15) / 2;
				
				offset = Mth.floor(Math.sqrt((offset * offset) / 2.0));
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 - offset, crossOriginY + 2 - offset, 11, 12, 4, 4, 256, 256);			// Top-Left Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 + 7 + offset, crossOriginY + 2 - offset, 18, 12, 4, 4, 256, 256);		// Top-Right Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 - offset, crossOriginY + 2 + 7 + offset, 11, 19, 4, 4, 256, 256);		// Bottom-Left Part
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, crossOriginX + 2 + 7 + offset, crossOriginY + 2 + 7 + offset, 18, 19, 4, 4, 256, 256);	// Bottom-Right Part
			}
			else
			{
				int centreOriginX = (mc.getWindow().getGuiScaledWidth() - 9) / 2;
				int centreOriginY = (mc.getWindow().getGuiScaledHeight() - 5) / 2;
				
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX, centreOriginY - 2, 12, 1, 9, 5, 256, 256);
				guiGraphics.blit(RenderPipelines.CROSSHAIR, HudCrosshair.CROSSHAIR_TEXTURES, centreOriginX, centreOriginY - 2 - 3 - offset, 12, 1, 9, 5, 256, 256);
			}
	        
//	        RenderSystem.setShaderTexture(0, HudCrosshair.ICONS_LOCATION);
	        
	        // Render the attack indicator if applicable
	        if (mc.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR && (!ClientConfig.INSTANCE.forceCompatibilityCrosshairs.get()))
	        {
	            float f = player.getAttackStrengthScale(0.0F);
	            boolean flag = false;
	
	            if (mc.crosshairPickEntity != null && mc.crosshairPickEntity instanceof LivingEntity living && f >= 1.0F)
	            {
	                flag = mc.player.getCurrentItemAttackStrengthDelay() > 5.0F;
	                flag = flag & living.isAlive();
	            }
	
	            int i = screenHeight - 7 + 16;
	            int j = screenWidth - 8;
	
	            if (flag)
	            {
	            	guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, HudCrosshair.ATTACK_INDICATOR_FULL, j, i, 16, 16);
	            }
	            else if (f < 1.0F)
	            {
	                int k = (int)(f * 17.0F);
	                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, HudCrosshair.ATTACK_INDICATOR_BACKGROUND, j, i, 16, 4);
	                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, HudCrosshair.ATTACK_INDICATOR_PROGRESS, 16, 4, 0, 0, j, i, k, 4);
	            }
	        }

	        
	        if(ItemNbt.read(equippedStack).contains(ThrowingWeaponItem.NBT_AMMO_USED))
	        {
				int maxAmmo = throwingWeapon.getMaxAmmo(equippedStack);
	        	int ammo = maxAmmo - ItemNbt.read(equippedStack).getIntOr(ThrowingWeaponItem.NBT_AMMO_USED, 0);
	        	
	        	String text = String.format("%d/%d", ammo, maxAmmo);
				
//				MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

				int textX = screenWidth / 2;
				int textY = screenHeight / 2 + 20;
//				mc.font.drawInBatch(text, textX - (mc.font.width(text) / 2), textY, 0xFFFFFFFF,
//						true, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
				Matrix3x2fStack guiPose = guiGraphics.pose();
				guiPose.pushMatrix();
				guiPose.identity();
				guiGraphics.text(mc.font, text, textX - (mc.font.width(text) / 2), textY, 0xFFFFFFFF);
				guiPose.popMatrix();

//				buffer.endBatch();
	        }
	        
		}
	}

}
