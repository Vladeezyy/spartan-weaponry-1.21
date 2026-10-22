package com.oblivioussp.spartanweaponry.client.gui;

import com.oblivioussp.spartanweaponry.platform.Platform;
import net.minecraft.util.Mth;

import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.compat.shouldersurfing.ShoulderSurfingCompat;
import com.oblivioussp.spartanweaponry.item.IHudCrosshair;
import com.oblivioussp.spartanweaponry.util.ClientConfig;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.gui.Gui;

public class HudCrosshair
{
	public static final Identifier CROSSHAIR_TEXTURES = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/gui/crosshairs.png");
	// 1.20.1 drew these from textures/gui/icons.png, which is a set of sprites since 1.20.2
	public static final Identifier ATTACK_INDICATOR_FULL = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_full");
	public static final Identifier ATTACK_INDICATOR_BACKGROUND = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_background");
	public static final Identifier ATTACK_INDICATOR_PROGRESS = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_progress");
	protected static boolean isVanillaCrosshairDisabled = false;
	
	public static void render(Gui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight)
	{
		Minecraft mc = Minecraft.getInstance();
		Options options = mc.options;
		LocalPlayer player= mc.player;
			
		ItemStack equipStack = ItemStack.EMPTY;
		if(player.getMainHandItem().getItem() instanceof IHudCrosshair)
			equipStack = player.getMainHandItem();
		else if(player.getUseItem().getItem() instanceof IHudCrosshair)
			equipStack = player.getUseItem();
		
		if(equipStack.isEmpty())
		{
			if(isVanillaCrosshairDisabled)
				isVanillaCrosshairDisabled = false;
/*			if(!OverlayRegistry.getEntry(ForgeIngameGui.CROSSHAIR_ELEMENT).isEnabled())
				OverlayRegistry.enableOverlay(ForgeIngameGui.CROSSHAIR_ELEMENT, true);*/
			return;
		}
		else
		{
			IHudCrosshair crosshairItem = ((IHudCrosshair)equipStack.getItem());
			
			if(crosshairItem.getCrosshairHudElement() != null)
			{
//				if(OverlayRegistry.getEntry(ForgeIngameGui.CROSSHAIR_ELEMENT).isEnabled())
//					OverlayRegistry.enableOverlay(ForgeIngameGui.CROSSHAIR_ELEMENT, false);
				if(!isVanillaCrosshairDisabled)
					isVanillaCrosshairDisabled = true;

				if((options.getCameraType().isFirstPerson() || Platform.INSTANCE.isModLoaded("leawind_third_person") || (!ClientConfig.INSTANCE.disableShoulderSurfingIntegration.get() && Platform.INSTANCE.isModLoaded("shouldersurfing") && ShoulderSurfingCompat.isShoulderSurfing())) 				
						&& (mc.gameMode.getPlayerMode() != GameType.SPECTATOR || canRenderCrosshairForSpectator(mc)))
				{
					// Do the debug rendering for crosshairs even with the custom crosshairs enabled
		            if (mc.getDebugOverlay().showDebugScreen() && !options.hideGui && !player.isReducedDebugInfo() && !options.reducedDebugInfo().get())
		            {
		               Camera camera = mc.gameRenderer.getMainCamera();
		               Matrix4fStack posestack = RenderSystem.getModelViewStack();
		               posestack.pushMatrix();
		               posestack.mul(guiGraphics.pose().last().pose());
		               posestack.translate((float)(screenWidth / 2), (float)(screenHeight / 2), 0.0f);
		               posestack.rotateX(-camera.getXRot() * Mth.DEG_TO_RAD);
		               posestack.rotateY(camera.getYRot() * Mth.DEG_TO_RAD);
		               posestack.scale(-1.0F, -1.0F, -1.0F);
		               RenderSystem.applyModelViewMatrix();
		               RenderSystem.renderCrosshair(10);
		               posestack.popMatrix();
		               RenderSystem.applyModelViewMatrix();
		            }
		            else
		            	crosshairItem.getCrosshairHudElement().render(gui, guiGraphics, partialTicks, screenWidth, screenHeight, equipStack);
				}
			}
		}
	}
	
	public static boolean isVanillaCrosshairDisabled()
	{
		return isVanillaCrosshairDisabled;
	}
	
	private static boolean canRenderCrosshairForSpectator(Minecraft mc)
	{
		HitResult hitResult = mc.hitResult;
		if (hitResult == null)
			return false;
		else if (hitResult.getType() == HitResult.Type.ENTITY)
			return ((EntityHitResult)hitResult).getEntity() instanceof MenuProvider;
		else if (hitResult.getType() == HitResult.Type.BLOCK)
		{
			BlockPos blockpos = ((BlockHitResult)hitResult).getBlockPos();
			Level level = mc.level;
			return level.getBlockState(blockpos).getMenuProvider(level, blockpos) != null;
		}
		else
			return false;
	}
}
