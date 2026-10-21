package com.oblivioussp.spartanweaponry.event;

import com.oblivioussp.spartanweaponry.platform.Platform;

import net.minecraft.world.item.TooltipFlag;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import net.minecraft.network.chat.FormattedText;

import java.util.function.Consumer;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.oblivioussp.spartanweaponry.util.ItemNbt;


import java.util.Optional;


import com.mojang.datafixers.util.Either;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;
import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.inventory.tooltip.OilCoatingTooltip;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.network.QuiverAccessPacket;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

/** Client event handlers; the NeoForge and Fabric modules call them. */
public class ClientEventHandler 
{
	/** The quiver access key was pressed (NeoForge: the key and mouse button input events, Fabric: each client tick). */
	public static void onAccessQuiverKey()
	{
		Minecraft mc = Minecraft.getInstance();
		if(mc.level == null || mc.screen != null || mc.isPaused())
			return;
		NetworkHandler.sendPacketToServer(new QuiverAccessPacket());
	}
	
	// High priority to allow other mods to place elements further above this
	/** The oil coating line or picture of a tooltip, inserted after the name (NeoForge: RenderTooltipEvent.GatherComponents). */
	public static void onRenderTooltip(ItemStack stack, Consumer<Either<FormattedText, TooltipComponent>> tooltipElement)
	{
		
		if(stack.is(ModItemTags.OILABLE_WEAPONS))
		{
			Optional<IOilHandler> handler = Optional.ofNullable(ModCapabilities.getOil(stack));
			handler.ifPresent((oilHandler) -> 
			{
				if(oilHandler.isOiled())
				{
					OilEffect oilEffect = oilHandler.getEffect().get();
					Optional<Holder<Potion>> potionOpt = oilHandler.getPotion();
					ItemStack oilStack = potionOpt.isPresent() ? OilHelper.makePotionOilStack(potionOpt.get()) : OilHelper.makeOilStack(oilEffect);
					
					tooltipElement.accept(Either.right(new OilCoatingTooltip(oilStack, oilHandler.getUsesLeft(), oilEffect.getMaxUses())));
				}
				else
					tooltipElement.accept(Either.left(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".oilable").withStyle(ChatFormatting.BLUE)));
			});
		}
	}
	
	/** The vanilla crosshair is hidden while a weapon with its own crosshair is held. */
	public static boolean hideVanillaCrosshair()
	{
		Minecraft mc = Minecraft.getInstance();
		return mc.player != null && mc.player.getMainHandItem().is(ModItemTags.HAS_CUSTOM_CROSSHAIR);
	}
	
	// Debug NBT viewer; Enable if NBT needs to be debugged
	public static void onTooltip(ItemStack stack, TooltipFlag flags, List<Component> tooltip)
	{

        // Debug (Show NBT data on *EVERYTHING*)
		
        if(Platform.INSTANCE.isDevelopment() && ItemNbt.has(stack) && flags.isAdvanced())
        {
        	// Format NBT debug string
        	String nbtStr = ItemNbt.get(stack).toString();
        	tooltip.add(Component.literal("NBT: " + ChatFormatting.DARK_GRAY + nbtStr).withStyle(ChatFormatting.DARK_PURPLE));
        }
	}
}
