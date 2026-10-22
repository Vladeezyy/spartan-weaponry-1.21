package com.oblivioussp.spartanweaponry.entity.projectile;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.registries.Registries;
import java.util.Optional;

import javax.annotation.Nullable;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.util.LegacyItems;
import com.oblivioussp.spartanweaponry.util.PotionHelper;
import com.oblivioussp.spartanweaponry.util.ProjectileKnockback;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ArrowBaseEntity extends AbstractArrow
{
	/** 1.20.1 saved the arrow item here; 1.21 saves it as the vanilla "item" (still read for old worlds) */
	protected final String NBT_ARROW = "Arrow";
	protected final String NBT_POTION = "Potion";
	protected final String NBT_POTION_COLOUR = "PotionColour";

	protected static final EntityDataAccessor<Integer> COLOUR = SynchedEntityData.defineId(ArrowBaseEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<ItemStack> ARROW = SynchedEntityData.defineId(ArrowBaseEntity.class, EntityDataSerializers.ITEM_STACK);
	protected Optional<Holder<Potion>> potion = Optional.empty();

	protected float baseDamage = 1.0f;
	protected float rangeMultiplier = 1.0f;
	protected int knockback = 0;

	public ArrowBaseEntity(EntityType<? extends ArrowBaseEntity> type, Level level)
	{
		super(type, level);
	}

	public ArrowBaseEntity(Level level, double x, double y, double z)
	{
		super(ModEntities.ARROW_SW.get(), x, y, z, level, ItemStack.EMPTY, null);
	}

	/** @param weapon the bow that shot it (its enchantments apply, as on vanilla arrows); null for none */
	public ArrowBaseEntity(Level level, LivingEntity shooter, @Nullable ItemStack weapon)
	{
		super(ModEntities.ARROW_SW.get(), shooter, level, ItemStack.EMPTY, weapon);
	}

	public void initEntity(float baseDamage, float rangeMultiplier, ItemStack arrowStack)
	{
		this.baseDamage = baseDamage;
		this.rangeMultiplier = rangeMultiplier;
		setBaseDamage(baseDamage);
		setArrowStack(arrowStack);
	}

	@Override
	public void shootFromRotation(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy)
	{
		super.shootFromRotation(shooter, pitch, yaw, p_184547_4_, (float)(velocity * rangeMultiplier), inaccuracy);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
		super.defineSynchedData(builder);
		builder.define(COLOUR, -1);
		builder.define(ARROW, ItemStack.EMPTY);
	}

	@Override
	public void tick()
	{
		super.tick();
		Level level = level();
		if(level.isClientSide())
		{
			if(inGround)
			{
				if(inGroundTime % 5 == 0)
					spawnPotionParticles(1);
			}
			else
				spawnPotionParticles(2);
		}
		else if(inGround && inGroundTime != 0 && inGroundTime >= 600)
		{
	         level.broadcastEntityEvent(this, (byte)0);
	         potion = Optional.empty();
	         getEntityData().set(COLOUR, -1);
		}
	}

	public void setKnockback(int knockbackIn)
	{
		knockback = knockbackIn;
	}

	public int getKnockback()
	{
		return knockback;
	}

	@Override
	protected void doKnockback(LivingEntity target, DamageSource source)
	{
		super.doKnockback(target, source);
		ProjectileKnockback.apply(this, target, knockback);
	}

	@Override
	protected void doPostHurtEffects(LivingEntity living)
	{
		Entity entity = getEffectSource();
		Level level = level();

		if(potion.isPresent())
		{
			for(MobEffectInstance effect : potion.get().value().getEffects())
			{
				living.addEffect(new MobEffectInstance(effect.getEffect(), Math.max(effect.getDuration() / 8, 1), effect.getAmplifier(), effect.isAmbient(), effect.isVisible()), entity);
			}
		}

		Item arrowItem = getPickupItem().getItem();

		// Spawn lightning under the right weather conditions (during a thunderstorm). Port fix: 1.20.1 missed the
		// brackets, so tipped copper arrows called lightning in any weather
		if(level.isThundering() && (arrowItem == ModItems.COPPER_ARROW.get() || arrowItem == ModItems.TIPPED_COPPER_ARROW.get()))
		{
			// Roll a chance to spawn lightning under the right circumstances
			if(random.nextInt(4) < 1)	// ~25%
			{
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.COMMAND);
				if(lightning != null)
				{
					lightning.snapTo(Vec3.atBottomCenterOf(living.blockPosition()));
					// The shooter caused it (1.20.1 credited the entity that was hit)
					lightning.setCause(getOwner() instanceof ServerPlayer player ? player : null);
					level.addFreshEntity(lightning);
				}
			}
		}
	}

	@Override
	public void handleEntityEvent(byte id)
	{
		if (id == 0)
		{
			int i = getEntityData().get(COLOUR);
			if (i != -1)
			{
	           for(int j = 0; j < 20; ++j)
	           {
	              level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.opaque(i)), getRandomX(0.5d), getRandomY(), getRandomZ(0.5d), 0.0d, 0.0d, 0.0d);
	           }
			}
		}
		else
		{
			super.handleEntityEvent(id);
		}
	}

	@Override
	protected ItemStack getPickupItem()
	{
		return getEntityData().get(ARROW).copy();
	}

	@Override
	protected ItemStack getDefaultPickupItem()
	{
		return new ItemStack(ModItems.IRON_ARROW.get());
	}

	protected void setArrowStack(ItemStack stack)
	{
		ItemStack copy = stack.copy();
		copy.setCount(1);
		getEntityData().set(ARROW, copy);
		setPickupItemStack(copy);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		potion.ifPresent((potionHolder) -> compound.putString(NBT_POTION, PotionHelper.getId(potionHolder)));
		compound.putInt(NBT_POTION_COLOUR, getEntityData().get(COLOUR));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		if(compound.contains(NBT_ARROW, CompoundTag.TAG_COMPOUND))	// 1.20.1
			setArrowStack(LegacyItems.parse(compound.getCompoundOrEmpty(NBT_ARROW), registryAccess()));
		else
			setArrowStack(getPickupItemStackOrigin());

		potion = PotionHelper.getPotion(compound);
		getEntityData().set(COLOUR, compound.contains(NBT_POTION_COLOUR) ? compound.getIntOr(NBT_POTION_COLOUR, 0) : -1);
	}

	public boolean isValid()
	{
		return !getEntityData().get(ARROW).isEmpty();
	}

	public Identifier getTexture()
	{
		String arrowRegName = BuiltInRegistries.ITEM.getKey(getEntityData().get(ARROW).getItem()).getPath();

		String prefix = "tipped_";
		int idx = arrowRegName.indexOf(prefix);
		if(idx != -1)
		{
			arrowRegName = arrowRegName.substring(idx + prefix.length());
		}
		return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/projectiles/" + arrowRegName + ".png");
	}

	public void setPotionEffect(ItemStack stack)
	{
		potion = PotionHelper.getPotion(stack);
		getEntityData().set(COLOUR, potion.isPresent() ? PotionHelper.getColor(stack) : -1);
	}

	public void spawnPotionParticles(int particleCount)
	{
		int colour = getEntityData().get(COLOUR);
		if(colour != -1 && particleCount > 0)
		{
	        for(int i = 0; i < particleCount; i++)
	        {
	        	level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.opaque(colour)), getRandomX(0.5d), getRandomY(), getRandomZ(0.5d), 0.0d, 0.0d, 0.0d);
	        }
		}
	}
}
