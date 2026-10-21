package com.oblivioussp.spartanweaponry.network;

import net.minecraft.world.entity.player.Player;

/** The context of a received packet (NeoForge's IPayloadContext; the Fabric module makes one from its handler context). */
public interface PacketContext
{
	Player player();

	/** Runs the work on the main thread of the receiving side. */
	void enqueueWork(Runnable work);
}
