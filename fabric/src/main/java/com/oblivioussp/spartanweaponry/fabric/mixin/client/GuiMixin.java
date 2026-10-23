package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

/** NeoForge IItemExtension#getHighlightTip of the selected item name (the GUI layers: HudElementRegistry). */
@Mixin(Gui.class)
public abstract class GuiMixin
{
	@Shadow
	private ItemStack lastToolHighlight;

	@ModifyVariable(method = "extractSelectedItemName", at = @At("STORE"), ordinal = 0)
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
