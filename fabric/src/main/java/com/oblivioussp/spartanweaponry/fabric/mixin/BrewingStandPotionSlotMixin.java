package com.oblivioussp.spartanweaponry.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.fabric.SpartanWeaponryFabric;
import com.oblivioussp.spartanweaponry.item.crafting.BrewingRecipe;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** NeoForge: the bottle slots take the inputs of the brewing recipes (weapon oil); taking a brew is PlayerBrewedPotionEvent. */
@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public abstract class BrewingStandPotionSlotMixin
{
	@Inject(method = "mayPlaceItem(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
	private static void spartanweaponry$mayPlaceItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		for(BrewingRecipe recipe : SpartanWeaponryFabric.BREWING_RECIPES)
		{
			if(recipe.isInput(stack))
			{
				cir.setReturnValue(true);
				return;
			}
		}
	}

	@Inject(method = "onTake", at = @At("HEAD"))
	private void spartanweaponry$onTake(Player player, ItemStack stack, CallbackInfo ci)
	{
		if(player instanceof ServerPlayer)
			CommonEventHandler.onBrewPotion(player, stack);
	}
}
