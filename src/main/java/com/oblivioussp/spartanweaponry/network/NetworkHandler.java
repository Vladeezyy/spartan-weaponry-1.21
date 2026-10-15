package com.oblivioussp.spartanweaponry.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** The mod's packets (1.20.1: a SimpleChannel; 1.21: NeoForge payloads, all from client to server) */
public class NetworkHandler 
{
	protected static final String PROTOCOL_VERSION = "1";
	
	public static void register(RegisterPayloadHandlersEvent ev)
	{
		PayloadRegistrar registrar = ev.registrar(PROTOCOL_VERSION);
		registrar.playToServer(QuiverAccessPacket.TYPE, QuiverAccessPacket.STREAM_CODEC, (packet, ctx) -> packet.handle(ctx));
		registrar.playToServer(QuiverPrioritySlotPacket.TYPE, QuiverPrioritySlotPacket.STREAM_CODEC, (packet, ctx) -> packet.handle(ctx));
		registrar.playToServer(QuiverButtonPacket.TYPE, QuiverButtonPacket.STREAM_CODEC, (packet, ctx) -> packet.handle(ctx));
	}
	
	public static void sendPacketTo(CustomPacketPayload packet, ServerPlayer player)
	{
		if(!(player instanceof FakePlayer))
			PacketDistributor.sendToPlayer(player, packet);
	}
	
	public static void sendPacketToServer(CustomPacketPayload packet)
	{
		PacketDistributor.sendToServer(packet);
	}
}
