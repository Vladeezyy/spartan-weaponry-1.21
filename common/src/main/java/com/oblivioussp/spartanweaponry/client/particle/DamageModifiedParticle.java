package com.oblivioussp.spartanweaponry.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class DamageModifiedParticle extends SingleQuadParticle 
{
	DamageModifiedParticle(ClientLevel levelIn, double xPos, double yPos, double zPos,
			double xDel, double yDel, double zDel, TextureAtlasSprite spriteIn) 
	{
		super(levelIn, xPos, yPos, zPos, xDel, yDel, zDel, spriteIn);
		friction = 0.7f;
		gravity = 0.5f;
		xd *= 0.1f;
		yd *= 0.1f;
		zd *= 0.1f;
		xd += xDel * 0.4d;
		yd += yDel * 0.4d;
		zd += zDel * 0.4d;
		float colour = (float)(Math.random() * 0.3d + 0.6d);
		rCol = colour;
		gCol = colour;
		bCol = colour;
		quadSize *= 0.75f;
		lifetime = Math.max(Mth.ceil(6.0d / (Math.random() * 0.8d + 0.6d)), 1);
		hasPhysics = false;
		tick();
	}
	
	@Override
	public void tick() 
	{
		gCol *= 0.96f;
		bCol *= 0.9f;
		super.tick();
	}

	@Override
	public SingleQuadParticle.Layer getLayer() 
	{
		return SingleQuadParticle.Layer.OPAQUE;
	}

	public static class DamageBoostedProvider implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteSet;
		
		public DamageBoostedProvider(SpriteSet spriteSetIn)
		{
			spriteSet = spriteSetIn;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel levelIn, double xPos,
				double yPos, double zPos, double xDel, double yDel, double zDel, RandomSource randomIn)
		{
			DamageModifiedParticle particle = new DamageModifiedParticle(levelIn, xPos, yPos, zPos, xDel, yDel, zDel, spriteSet.get(randomIn));
			particle.rCol = 0.5f;
			particle.gCol = 1.0f;
			particle.bCol = 0.2f;
			return particle;
		}
	}
	
	public static class DamageReducedProvider implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteSet;
		
		public DamageReducedProvider(SpriteSet spriteSetIn)
		{
			spriteSet = spriteSetIn;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel levelIn, double xPos,
				double yPos, double zPos, double xDel, double yDel, double zDel, RandomSource randomIn)
		{
			DamageModifiedParticle particle = new DamageModifiedParticle(levelIn, xPos, yPos, zPos, xDel, yDel, zDel, spriteSet.get(randomIn));
			particle.rCol = 0.5f;
			particle.gCol = 0.2f;
			particle.bCol = 0.5f;
			return particle;
		}
	}
	
	public static class OilDamageBoostedProvider implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteSet;
		
		public OilDamageBoostedProvider(SpriteSet spriteSetIn)
		{
			spriteSet = spriteSetIn;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel levelIn, double xPos,
				double yPos, double zPos, double xDel, double yDel, double zDel, RandomSource randomIn)
		{
			DamageModifiedParticle particle = new DamageModifiedParticle(levelIn, xPos, yPos, zPos, xDel, yDel, zDel, spriteSet.get(randomIn));
			particle.rCol = 1.0f;
			particle.gCol = 0.75f;
			particle.bCol = 0.25f;
			return particle;
		}
	}
}
