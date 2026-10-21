package com.oblivioussp.spartanweaponry.network;

import com.oblivioussp.spartanweaponry.platform.Platform;

import net.minecraft.network.codec.StreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.BiConsumer;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** The mod's packets (1.20.1: a SimpleChannel; 1.21: NeoForge payloads, all from client to server) */
public class NetworkHandler 
{
	protected static final String PROTOCOL_VERSION = "1";
	
	/** Registers the packets with the loader (all from the client to the server). */
	public static void register(PacketRegistrar registrar)
	{
		registrar.playToServer(QuiverAccessPacket.TYPE, QuiverAccessPacket.STREAM_CODEC, (packet, ctx) -> packet.handle(ctx));
		registrar.playToServer(QuiverPrioritySlotPacket.TYPE, QuiverPrioritySlotPacket.STREAM_CODEC, (packet, ctx) -> packet.handle(ctx));
		registrar.playToServer(QuiverButtonPacket.TYPE, QuiverButtonPacket.STREAM_CODEC, (packet, ctx) -> packet.handle(ctx));
	}
	
	public interface PacketRegistrar
	{
		<T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, PacketContext> handler);
	}
	
	public static void sendPacketTo(CustomPacketPayload packet, ServerPlayer player)
	{
		Platform.INSTANCE.sendToPlayer(player, packet);
	}
	
	public static void sendPacketToServer(CustomPacketPayload packet)
	{
		Platform.INSTANCE.sendToServer(packet);
	}
}
