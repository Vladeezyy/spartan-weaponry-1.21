package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.oblivioussp.spartanweaponry.event.ClientEventHandler;
import com.oblivioussp.spartanweaponry.fabric.SpartanWeaponryFabricClient;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

/** NeoForge's GUI layers around the crosshair, and IItemExtension#getHighlightTip of the selected item name. */
@Mixin(Gui.class)
public abstract class GuiMixin
{
	@Shadow
	private ItemStack lastToolHighlight;

	/** NeoForge RenderGuiLayerEvent.Pre of the crosshair: the weapons with their own crosshair hide the vanilla one. */
	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$hideCrosshair(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci)
	{
		if(ClientEventHandler.hideVanillaCrosshair())
		{
			spartanweaponry$aboveCrosshair(graphics, delta);
			ci.cancel();
		}
	}

	@Inject(method = "renderCrosshair", at = @At("RETURN"))
	private void spartanweaponry$crosshair(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci)
	{
		spartanweaponry$aboveCrosshair(graphics, delta);
	}

	private void spartanweaponry$aboveCrosshair(GuiGraphics graphics, DeltaTracker delta)
	{
		for(LayeredDraw.Layer layer : SpartanWeaponryFabricClient.ABOVE_CROSSHAIR)
			layer.render(graphics, delta);
	}

	@ModifyVariable(method = "renderSelectedItemName", at = @At("STORE"), ordinal = 0)
	private MutableComponent spartanweaponry$highlightTip(MutableComponent name)
	{
		if(lastToolHighlight.getItem() instanceof HeavyCrossbowItem crossbow)
		{
			Component tip = crossbow.getHighlightTip(lastToolHighlight, name);
			if(tip != name)
				return Component.empty().append(tip);
		}
		return name;
	}
}
