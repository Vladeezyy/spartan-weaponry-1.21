package com.oblivioussp.spartanweaponry.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/** 1.20.1/1.21.1 Player#playNotifySound (a sound only this player hears), gone in 1.21.11 */
public final class Sounds
{
	private Sounds() {}

	public static void notify(Player player, SoundEvent sound, SoundSource source, float volume, float pitch)
	{
		if(player instanceof ServerPlayer serverPlayer)
			serverPlayer.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, player.getX(), player.getY(), player.getZ(),
					volume, pitch, player.getRandom().nextLong()));
	}
}
