package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import com.oblivioussp.spartanweaponry.platform.Platform;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.inventory.QuiverBoltMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class QuiverBoltItem extends QuiverBaseItem 
{
	public static final Identifier TEXTURE_SMALL = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_small.png");
	public static final Identifier TEXTURE_MEDIUM = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_medium.png");
	public static final Identifier TEXTURE_LARGE = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_large.png");
	public static final Identifier TEXTURE_HUGE = Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/model/quiver_bolt_huge.png");

	public QuiverBoltItem(int inventorySize) 
	{
		super(inventorySize);
	}

	@Override
	public void openGui(ItemStack stack, Player player, SlotType slotType, int slot) 
	{
		Platform.INSTANCE.openMenu((ServerPlayer)player, new ContainerProvider(Component.translatable("gui." + ModSpartanWeaponry.ID + ".quiver_bolt.title"), stack), buf -> 
			{
				buf.writeEnum(slotType);
				buf.writeInt(slot);
				ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
			});
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext levelIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn) 
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			tooltip.add(Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".modifiers.ammo.type", 
					Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".modifiers.ammo.bolt").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_AQUA));
			tooltip.flush();
			super.appendHoverText(stack, levelIn, display, tooltipOut, flagIn);
		}
		finally
		{
			tooltip.flush();
		}
	}

	protected class ContainerProvider implements MenuProvider
	{
		private final Component displayName;
		private final ItemStack quiverStack;
		
		protected ContainerProvider(Component displayName, ItemStack quiverStack)
		{
			this.displayName = displayName;
			this.quiverStack = quiverStack;
		}

		@Override
		public AbstractContainerMenu createMenu(int id, Inventory inventory,
				Player player)
		{
			return new QuiverBoltMenu(id, inventory, quiverStack);
		}

		@Override
		public Component getDisplayName() 
		{
			return displayName;
		}
	}

	@Override
	public boolean isAmmoValid(ItemStack pickedUpStack, ItemStack quiver) 
	{
		return pickedUpStack.is(ModItemTags.BOLTS);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stackIn) 
	{
		return makeTooltipImage(stackIn, true);
	}

}
