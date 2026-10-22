package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ItemIds;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;

import net.minecraft.core.HolderLookup;

import com.oblivioussp.spartanweaponry.util.LegacyItems;

import net.minecraft.world.item.component.ItemContainerContents;

import com.oblivioussp.spartanweaponry.init.ModDataComponents;

import com.oblivioussp.spartanweaponry.util.ItemNbt;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.capability.IQuiverItemHandler;
import com.oblivioussp.spartanweaponry.capability.QuiverItemStackHandler;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.inventory.tooltip.QuiverTooltip;
import com.oblivioussp.spartanweaponry.util.Defaults;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class QuiverBaseItem extends Item
{
	public enum SlotType
	{
		UNDEFINED,
		MAIN_HAND,
		OFF_HAND,
		HOTBAR,
		CURIO
	}
	
	public static final String NBT_AMMO_COLLECT = "AmmoCollect";
	public static final String NBT_AMMO = "Ammo";
	public static final String NBT_OFFHAND_MOVED = "OffhandMoved";
	public static final String NBT_ITEM_ID = "Id";
	public static final String NBT_ITEM_SLOT = "Slot";
	public static final String NBT_PROIRITY_SLOT = "PrioritySlot";
	
	protected int ammoSlots = Defaults.SlotsQuiverSmall;

	public QuiverBaseItem(int inventorySize)
	{
		super(ItemIds.properties().stacksTo(1));
		
		
		ammoSlots = inventorySize;
	}

	public int getAmmoSlots()
	{
		return ammoSlots;
	}

	/** The number of non-empty slots (for the quiver's model states) */
	public int getAmmoCount(ItemStack stack)
	{
		int ammo = (int)stack.getOrDefault(ModDataComponents.QUIVER_CONTENTS.get(), ItemContainerContents.EMPTY).nonEmptyItems().spliterator().getExactSizeIfKnown();
		if(ammo < 0)
		{
			ammo = 0;
			for(ItemStack item : stack.getOrDefault(ModDataComponents.QUIVER_CONTENTS.get(), ItemContainerContents.EMPTY).nonEmptyItems())
				ammo++;
		}
		
		// Have 6 separate states for the Heavy Arrow Quiver, instead of 4
		if(ammoSlots >= Defaults.SlotsQuiverLarge)
			ammo = Mth.clamp(ammo, 0, 5);
		else
			ammo = Mth.clamp(ammo, 0, 3);
		
		return ammo;
	}
	
	/** Moves the inventory of a quiver from 1.20.1 (its "Ammo" NBT) into the quiver_contents component */
	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot equipmentSlot)
	{
		// 1.21.5+: server side only, with the equipment slot instead of the inventory slot and "selected"
		int slotId = equipmentSlot == null ? -1 : equipmentSlot.getIndex();
		boolean isSelected = equipmentSlot == EquipmentSlot.MAINHAND;
		if(!level.isClientSide())
			migrateLegacyContents(stack, level.registryAccess());
		super.inventoryTick(stack, level, entity, equipmentSlot);
	}
	
	public static void migrateLegacyContents(ItemStack stack, HolderLookup.Provider registries)
	{
		if(!ItemNbt.contains(stack, NBT_AMMO))
			return;
		CompoundTag ammo = ItemNbt.get(stack).getCompoundOrEmpty(NBT_AMMO);
		ListTag list = ammo.getListOrEmpty("Items");
		int size = Math.max(ammo.getIntOr("Size", 0), stack.getItem() instanceof QuiverBaseItem quiver ? quiver.ammoSlots : 0);
		NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
		for(int i = 0; i < list.size(); i++)
		{
			CompoundTag tag = list.getCompoundOrEmpty(i);
			int slot = tag.getIntOr(NBT_ITEM_SLOT, 0);
			if(slot >= 0 && slot < size)
				items.set(slot, LegacyItems.parse(tag, registries));
		}
		stack.set(ModDataComponents.QUIVER_CONTENTS.get(), ItemContainerContents.fromItems(items));
		ItemNbt.remove(stack, NBT_AMMO);
	}
	
	@Override
	public InteractionResult use(Level levelIn, Player playerIn, InteractionHand handIn) 
	{
		ItemStack heldItem = playerIn.getItemInHand(handIn);
		
		if(!levelIn.isClientSide())
			migrateLegacyContents(heldItem, levelIn.registryAccess());
			
		if(!levelIn.isClientSide())
		{
			if(!playerIn.isCrouching())
			{
				SlotType slotType = handIn == InteractionHand.OFF_HAND ? SlotType.OFF_HAND : SlotType.MAIN_HAND;
				openGui(heldItem, playerIn, slotType, -1);
				return InteractionResult.CONSUME;
			}
			else
			{
				// Toggle ammo collection
				boolean ammoCollect = !ItemNbt.read(heldItem).getBooleanOr(NBT_AMMO_COLLECT, false);
				ItemNbt.update(heldItem, (nbt) -> nbt.putBoolean(NBT_AMMO_COLLECT, ammoCollect));
				
				String collectStatus = ammoCollect ? "enabled" : "disabled";
				ChatFormatting collectColour = ammoCollect ? ChatFormatting.GREEN : ChatFormatting.RED;
				playerIn.displayClientMessage(Component.translatable("message." + ModSpartanWeaponry.ID + ".ammo_collect_toggle", Component.translatable("tooltip." + ModSpartanWeaponry.ID + "." + collectStatus).withStyle(collectColour)), true);
				return InteractionResult.FAIL;
			}
		}
        return InteractionResult.PASS;
	}
	

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn) 
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			if(ItemNbt.read(stack).contains("ClientInventory"))
				ItemNbt.update(stack, (nbt) -> nbt.remove("ClientInventory"));
		
			tooltip.flush();
			super.appendHoverText(stack, levelIn, display, tooltipOut, flagIn);
		
			boolean ammoCollect = ItemNbt.read(stack).getBooleanOr(NBT_AMMO_COLLECT, false);
			String collectStatus = ammoCollect ? "enabled" : "disabled";
			ChatFormatting statusColour = ammoCollect ? ChatFormatting.GREEN : ChatFormatting.RED;
			tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".quiver_collect_status").append(Component.translatable("tooltip." + ModSpartanWeaponry.ID + "." + collectStatus).withStyle(statusColour)).withStyle(ChatFormatting.DARK_AQUA));
		
			if(ammoSlots != Defaults.SlotsQuiverHuge)
				tooltip.add(Component.translatable("tooltip."+ ModSpartanWeaponry.ID + ".quiver_upgrade").withStyle(ChatFormatting.YELLOW));
		}
		finally
		{
			tooltip.flush();
		}
	}
	
	public Optional<TooltipComponent> makeTooltipImage(ItemStack stackIn, boolean isBoltQuiver)
	{
		int prioritySlot = ItemNbt.read(stackIn).getIntOr(NBT_PROIRITY_SLOT, 0);
		
		NonNullList<ItemStack> items = NonNullList.withSize(ammoSlots, ItemStack.EMPTY);
		stackIn.getOrDefault(ModDataComponents.QUIVER_CONTENTS.get(), ItemContainerContents.EMPTY).copyInto(items);
		return Optional.of(new QuiverTooltip(items, prioritySlot, isBoltQuiver));
	}
	
	@Override
	public abstract Optional<TooltipComponent> getTooltipImage(ItemStack stackIn);
	
	public abstract void openGui(ItemStack stack, Player player, SlotType slotType, int slot);
	
	public abstract boolean isAmmoValid(ItemStack pickedUpStack, ItemStack quiver);
	
}
