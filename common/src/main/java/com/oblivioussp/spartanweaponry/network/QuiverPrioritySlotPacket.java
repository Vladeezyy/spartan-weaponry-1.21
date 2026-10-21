package com.oblivioussp.spartanweaponry.network;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.inventory.QuiverBaseMenu;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import com.oblivioussp.spartanweaponry.util.QuiverHelper.IQuiverInfo;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record QuiverPrioritySlotPacket(int prioritySlot) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<QuiverPrioritySlotPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_priority_slot"));
	public static final StreamCodec<RegistryFriendlyByteBuf, QuiverPrioritySlotPacket> STREAM_CODEC = ByteBufCodecs.BYTE.map((b) -> new QuiverPrioritySlotPacket((int)b), (p) -> (byte)p.prioritySlot()).cast();

	@Override
	public CustomPacketPayload.Type<QuiverPrioritySlotPacket> type()
	{
		return TYPE;
	}

	public void handle(PacketContext ctx)
	{
		if(!(ctx.player() instanceof ServerPlayer player))
			return;
		ctx.enqueueWork(() ->
		{
				AbstractContainerMenu menu = player.containerMenu;
				if(menu instanceof QuiverBaseMenu quiverMenu)
				{
					// The slot comes from the client: only the quiver's own slots (1.20.1 did not check it)
					if(prioritySlot < 0 || prioritySlot >= quiverMenu.getQuiverSlotCount())
						return;
					ItemStack quiver = quiverMenu.getQuiverStack();
					ItemNbt.update(quiver, (nbt) -> nbt.putInt(QuiverBaseItem.NBT_PROIRITY_SLOT, prioritySlot));
					
					// Do nothing if the priority slot is empty
					Slot slot = quiverMenu.getSlot(prioritySlot);
					if(!slot.hasItem())
						return;
					
					for(IQuiverInfo quiverInfo : QuiverHelper.info)
					{
						if(quiverInfo.isQuiver(quiver))
						{
							InteractionHand ammoHand = quiverInfo.isWeapon(player.getMainHandItem()) ? InteractionHand.OFF_HAND : quiverInfo.isWeapon(player.getOffhandItem()) ? InteractionHand.MAIN_HAND : null;
							
							if(ammoHand != null)
							{
								ItemStack priorityStack = slot.getItem();
								ItemStack ammoStack = player.getItemInHand(ammoHand);
								
								// Swap out priority stack with ammo stack
								player.setItemInHand(ammoHand, priorityStack);
								slot.set(ammoStack);
							}
						}
					}
				}
				
			});
	}
}
