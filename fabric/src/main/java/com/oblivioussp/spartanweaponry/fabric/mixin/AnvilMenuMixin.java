package com.oblivioussp.spartanweaponry.fabric.mixin;

import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.oblivioussp.spartanweaponry.event.CommonEventHandler;
import com.oblivioussp.spartanweaponry.event.LoaderEvents;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/** NeoForge AnvilUpdateEvent (CommonHooks.onAnvilChange): an output from the handler replaces the vanilla result. */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu
{
	@Shadow
	private int repairItemCountCost;

	@Shadow
	@Final
	private DataSlot cost;

	private AnvilMenuMixin(MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slots)
	{
		super(type, containerId, playerInventory, access, slots);
	}

	@Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
	private void spartanweaponry$anvilUpdate(CallbackInfo ci)
	{
		ItemStack left = inputSlots.getItem(0);
		if(left.isEmpty())
			return;
		LoaderEvents.AnvilUpdate update = new LoaderEvents.AnvilUpdate(left, inputSlots.getItem(1));
		CommonEventHandler.handleAnvilUpdate(update);
		if(update.getOutput().isEmpty())
			return;
		resultSlots.setItem(0, update.getOutput());
		cost.set((int)Math.max(0L, Math.min(Integer.MAX_VALUE, update.getCost())));
		repairItemCountCost = update.getMaterialCost();
		broadcastChanges();
		ci.cancel();
	}
}
