package com.oblivioussp.spartanweaponry.init;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import com.oblivioussp.spartanweaponry.platform.Registrar;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.client.particle.DamageModifiedParticle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModParticles 
{
	public static final Registrar<ParticleType<?>> REGISTRY = Registrar.create(Registries.PARTICLE_TYPE);
	
	public static final Registrar.Entry<ParticleType<?>, SimpleParticleType> DAMAGE_BOOSTED = REGISTRY.register("damage_boosted", () -> new SimpleParticleType(false) {} {});
	public static final Registrar.Entry<ParticleType<?>, SimpleParticleType> DAMAGE_REDUCED = REGISTRY.register("damage_reduced", () -> new SimpleParticleType(false) {} {});
	public static final Registrar.Entry<ParticleType<?>, SimpleParticleType> OIL_DAMAGE_BOOSTED = REGISTRY.register("oil_damage_boosted", () -> new SimpleParticleType(false) {} {});
	// TODO: Also consider adding boomerang trail particle and possibly for other throwing weapons too
	
	/** NeoForge: RegisterParticleProvidersEvent#registerSpriteSet, Fabric: ParticleFactoryRegistry. */
	public static void registerFactories(SpriteSetRegistrar ev)
	{
		ev.register(DAMAGE_BOOSTED.get(), DamageModifiedParticle.DamageBoostedProvider::new);
		ev.register(DAMAGE_REDUCED.get(), DamageModifiedParticle.DamageReducedProvider::new);
		ev.register(OIL_DAMAGE_BOOSTED.get(), DamageModifiedParticle.OilDamageBoostedProvider::new);
	}
	
	public interface SpriteSetRegistrar
	{
		<T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> provider);
	}
}
