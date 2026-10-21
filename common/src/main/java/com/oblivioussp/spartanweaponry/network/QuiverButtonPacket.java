package com.oblivioussp.spartanweaponry.network;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.inventory.QuiverBaseMenu;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record QuiverButtonPacket(boolean state) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<QuiverButtonPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ModSpartanWeaponry.ID, "quiver_button"));
	public static final StreamCodec<RegistryFriendlyByteBuf, QuiverButtonPacket> STREAM_CODEC = ByteBufCodecs.BOOL.map(QuiverButtonPacket::new, QuiverButtonPacket::state).cast();

	@Override
	public CustomPacketPayload.Type<QuiverButtonPacket> type()
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
					ItemNbt.update(quiverMenu.getQuiverStack(), (nbt) -> nbt.putBoolean(QuiverBaseItem.NBT_AMMO_COLLECT, state));
				}
			});
	}
}
