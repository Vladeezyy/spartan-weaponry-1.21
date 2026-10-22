package com.oblivioussp.spartanweaponry.entity.projectile;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.registries.Registries;
import java.util.Optional;

import com.oblivioussp.spartanweaponry.util.PotionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;

import java.util.Arrays;

import com.google.common.collect.Lists;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.init.ModDamageTypes;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModItems;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;

public class BoltEntity extends AbstractArrow
{
	protected final String NBT_BOLT = "Bolt";
	protected final String NBT_POTION = "Potion";
	protected final String NBT_POTION_COLOUR = "PotionColour";

	protected static final EntityDataAccessor<Integer> DATA_COLOUR = SynchedEntityData.defineId(BoltEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<ItemStack> DATA_BOLT = SynchedEntityData.defineId(BoltEntity.class, EntityDataSerializers.ITEM_STACK);
	protected Optional<Holder<Potion>> potion = Optional.empty();
	protected int knockback = 0;
	/** 1.20.1 AbstractArrow.shotFromCrossbow (the heavy crossbow sets it) */
	protected boolean shotFromCrossbow = false;
	
	protected float baseDamage = 1.0f;
	protected float rangeMultiplier = 1.0f;
	protected float armorPiercingFactor = 0.0f;
	
	public BoltEntity(EntityType<? extends BoltEntity> type, Level level)
    {
        super(type, level);
    }

    public BoltEntity(EntityType<? extends BoltEntity> type, double x, double y, double z, Level level)
    {
        super(type, x, y, z, level, ItemStack.EMPTY, null);
    }
    
    public BoltEntity(EntityType<? extends BoltEntity> type, LivingEntity shooter, Level level)
    {
        super(type, shooter, level, ItemStack.EMPTY, null);
    }

    public BoltEntity(LivingEntity shooter, Level level)
    {
    	this(ModEntities.BOLT.get(), shooter, level);
    }
    
	
	public void initEntity(float baseDamage, float rangeMultiplier, float armorPiercingFactor, ItemStack boltStack)
	{
		this.baseDamage = baseDamage;
		this.rangeMultiplier = rangeMultiplier;
		this.armorPiercingFactor = armorPiercingFactor;
		this.setBaseDamage(baseDamage);
		setBoltStack(boltStack);
	}
    
/*    @Override
    public void shootFromRotation(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy)
    {
    	super.shootFromRotation(shooter, pitch, yaw, p_184547_4_, (float)(velocity * rangeMultiplier), inaccuracy);
    }*/
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) 
	{
		super.defineSynchedData(builder);
		builder.define(DATA_COLOUR, -1);
		builder.define(DATA_BOLT, ItemStack.EMPTY);
	}
	
	protected void setBoltStack(ItemStack boltStack)
	{
		ItemStack copy = boltStack.copy();
		copy.setCount(1);
		getEntityData().set(DATA_BOLT, copy);
		// 1.21 saves this stack with the entity (1.20.1 did not save the bolt at all: reloaded bolts were empty)
		setPickupItemStack(copy);
	}
	
	public void setKnockback(int knockbackIn)
	{
		knockback = knockbackIn;
	}
	
	public int getKnockback()
	{
		return knockback;
	}
	
	public void setShotFromCrossbow(boolean shotFromCrossbowIn)
	{
		shotFromCrossbow = shotFromCrossbowIn;
	}
	
	public boolean shotFromCrossbow()
	{
		return shotFromCrossbow;
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
	         getEntityData().set(DATA_COLOUR, -1);
		}
    }
    
    @Override
    protected void onHitEntity(EntityHitResult result)
    {
    	Level level = level();
    	Entity entity = result.getEntity();
        float velocity = (float)getDeltaMovement().length();
        int damage = Mth.ceil(Mth.clamp((double)velocity * getBaseDamage(), 0.0D, 2.147483647E9D));
        if(getPierceLevel() > 0) 
        {
        	if(piercingIgnoreEntityIds == null)
        		piercingIgnoreEntityIds = new IntOpenHashSet(5);

        	if(piercedAndKilledEntities == null)
        		piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
		
        	if(piercingIgnoreEntityIds.size() >= getPierceLevel() + 1)
        	{
        		discard();
        		return;
        	}

        	piercingIgnoreEntityIds.add(entity.getId());
        }

        if(isCritArrow()) 
        {
        	long critDamageBonus = (long)random.nextInt(damage / 2 + 2);
        	damage = (int)Math.min(critDamageBonus + (long)damage, 2147483647L);
        }

        Entity shooter = getOwner();
        DamageSource source;
        if(shooter == null)
        	source = ModDamageTypes.armorPiercingProjectile(this, this);
        else 
        {
        	source = ModDamageTypes.armorPiercingProjectile(this, shooter);
        	if(shooter instanceof LivingEntity)
        		((LivingEntity)shooter).setLastHurtMob(entity);
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int fireTimer = entity.getRemainingFireTicks();
        if(isOnFire() && !isEnderman)
        	entity.igniteForSeconds(5);

        if(entity.hurt(source, (float)damage)) 
        {
        	if(isEnderman)
        		return;

        	if(entity instanceof LivingEntity) 
        	{
        		LivingEntity livingentity = (LivingEntity)entity;
        		if(!level.isClientSide() && getPierceLevel() <= 0)
        			livingentity.setArrowCount(livingentity.getArrowCount() + 1);

        		if(getKnockback() > 0)
        		{
        			Vec3 vector3d = getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)getKnockback() * 0.6D);
        			if (vector3d.lengthSqr() > 0.0D)
        				livingentity.push(vector3d.x, 0.1D, vector3d.z);
        		}

        		if(level instanceof ServerLevel serverLevel) 
        			EnchantmentHelper.doPostAttackEffects(serverLevel, livingentity, source);

        		doPostHurtEffects(livingentity);
        		if(shooter != null && livingentity != shooter && livingentity instanceof Player && shooter instanceof ServerPlayer && !isSilent())
        			((ServerPlayer)shooter).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));

        		if(!entity.isAlive() && piercedAndKilledEntities != null)
        			piercedAndKilledEntities.add(livingentity);

        		if(!level.isClientSide() && shooter instanceof ServerPlayer)
        		{
        			ServerPlayer serverplayerentity = (ServerPlayer)shooter;
        			if (piercedAndKilledEntities != null && shotFromCrossbow()) 
        				CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, piercedAndKilledEntities);
        			else if (!entity.isAlive() && shotFromCrossbow())
        				CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, Arrays.asList(entity));
        		}
        	}

        	playSound(getHitGroundSoundEvent(), 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
        	if(getPierceLevel() <= 0)
        		discard();
        } 
        else 
        {
        	entity.setRemainingFireTicks(fireTimer);
        	setDeltaMovement(getDeltaMovement().scale(-0.1D));
        	setYRot(getYRot() + 180.0f);
        	yRotO += 180.0F;
        	if(!level.isClientSide() && getDeltaMovement().lengthSqr() < 1.0E-7D)
        	{
        		if (pickup == AbstractArrow.Pickup.ALLOWED)
        			spawnAtLocation(getPickupItem(), 0.1F);

        		discard();
        	}
        }
    }
	
	@Override
	protected void doPostHurtEffects(LivingEntity living) 
	{
		super.doPostHurtEffects(living);
    	Level level = level();
		
		if(potion.isPresent())
		{
			for(MobEffectInstance effect : potion.get().value().getEffects())
			{
				living.addEffect(new MobEffectInstance(effect.getEffect(), Math.max(effect.getDuration() / 8, 1), effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
			}
		}
		
		Item arrowItem = getPickupItem().getItem();
		
		// Spawn lightning under the right weather conditions (during a thunderstorm). Port fix: brackets (tipped copper
		// bolts called lightning in any weather)
		if(level.isThundering() && (arrowItem == ModItems.COPPER_BOLT.get() || arrowItem == ModItems.TIPPED_COPPER_BOLT.get()))
		{
			// Roll a chance to spawn lightning under the right circumstances
			if(random.nextInt(4) < 1)	// ~25%
			{
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.COMMAND);
				if(lightning != null)
				{
					lightning.snapTo(Vec3.atBottomCenterOf(living.blockPosition()));
					lightning.setCause(getOwner() instanceof ServerPlayer player ? player : null);
					level.addFreshEntity(lightning);
				}
			}
		}
	}

	@Override
	protected ItemStack getPickupItem()
	{
		return getEntityData().get(DATA_BOLT).copy();
	}
	
	@Override
	protected ItemStack getDefaultPickupItem()
	{
		return new ItemStack(ModItems.BOLT.get());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		
		setBoltStack(getPickupItemStackOrigin());
		potion = PotionHelper.getPotion(compound);
		
		getEntityData().set(DATA_COLOUR, compound.contains(NBT_POTION_COLOUR) ? compound.getIntOr(NBT_POTION_COLOUR, 0) : -1);
	}


	@Override
	public void addAdditionalSaveData(CompoundTag compound) 
	{
		super.addAdditionalSaveData(compound);
		
		potion.ifPresent((potionHolder) -> compound.putString(NBT_POTION, PotionHelper.getId(potionHolder)));
		
		compound.putInt(NBT_POTION_COLOUR, getEntityData().get(DATA_COLOUR));
	}
	
	public float getRangeMultiplier() 
	{
		return rangeMultiplier;
	}
	
	public void setPotionEffect(ItemStack stack)
	{
		potion = PotionHelper.getPotion(stack);
		getEntityData().set(DATA_COLOUR, potion.isPresent() ? PotionHelper.getColor(stack) : -1);
	}
	
	private void spawnPotionParticles(int particleCount)
	{
		int colour = getEntityData().get(DATA_COLOUR);
		if(colour != -1 && particleCount > 0)
		{
	    	Level level = level();
	        for(int i = 0; i < particleCount; i++)
	        {
	        	level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.opaque(colour)), getRandomX(0.5d), getRandomY(), getRandomZ(0.5d), 0.0d, 0.0d, 0.0d);
	        }
		}
	}
	
	public boolean isValid()
	{
		return !getEntityData().get(DATA_BOLT).isEmpty();
	}
	
	public Identifier getTexture()
	{
		ItemStack boltStack = getEntityData().get(DATA_BOLT);
		if(boltStack.isEmpty())
			return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "missing_stack");
					
		String boltRegName = BuiltInRegistries.ITEM.getKey(boltStack.getItem()).getPath();
		
		String prefix = "tipped_";
		int idx = boltRegName.indexOf(prefix);
		if(idx != -1)
		{
			boltRegName = boltRegName.substring(idx + prefix.length());
		}
		return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/projectiles/" + boltRegName + ".png");
	}
}
