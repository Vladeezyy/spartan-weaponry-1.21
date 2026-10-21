package com.oblivioussp.spartanweaponry.fabric.mixin.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.oblivioussp.spartanweaponry.event.ClientEventHandler;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * The oil picture of RenderTooltipEvent.GatherComponents (NeoForge puts it after the name; vanilla draws the tooltip
 * image there too). Only for items without a picture of their own.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin
{
	@ModifyReturnValue(method = "getTooltipImage", at = @At("RETURN"))
	private Optional<TooltipComponent> spartanweaponry$oilTooltip(Optional<TooltipComponent> image)
	{
		if(image.isPresent())
			return image;
		TooltipComponent[] oil = new TooltipComponent[1];
		ClientEventHandler.onRenderTooltip((ItemStack)(Object)this, element -> element.ifRight(component -> oil[0] = component));
		return Optional.ofNullable(oil[0]);
	}
}
