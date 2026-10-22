package com.oblivioussp.spartanweaponry.entity.projectile;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.particles.SpellParticleOption;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BoltSpectralEntity extends BoltEntity 
{
	private int duration = 200;
	private final String NBT_DURATION = "Duration";
	
	public BoltSpectralEntity(EntityType<? extends BoltEntity> type, Level level)
    {
        super(type, level);
    }

    public BoltSpectralEntity(EntityType<? extends BoltEntity> type, double x, double y, double z, Level level)
    {
        super(type, x, y, z, level);
    }

    public BoltSpectralEntity(LivingEntity shooter, Level level)
    {
        super(ModEntities.BOLT_SPECTRAL.get(), shooter, level);
    }
    
    
    @Override
    public void tick() 
    {
    	Level level = level();
    	super.tick();
    	if(level.isClientSide() && !isInGround())
    		level.addParticle(SpellParticleOption.create(ParticleTypes.INSTANT_EFFECT, -1, 1.0F), getX(), getY(), getZ(), 0.0d, 0.0d, 0.0d);
    }
    
    @Override
    protected ItemStack getPickupItem() 
    {
    	return new ItemStack(ModItems.SPECTRAL_BOLT.get());
    }
    
    @Override
    protected void doPostHurtEffects(LivingEntity living) 
    {
    	super.doPostHurtEffects(living);
    	MobEffectInstance effect = new MobEffectInstance(MobEffects.GLOWING, duration, 0);
    	living.addEffect(effect);
    }
    
    @Override
    public Identifier getTexture() 
    {
    	return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "textures/entity/projectiles/spectral_bolt.png");
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput compound)
    {
    	super.readAdditionalSaveData(compound);
    	duration = compound.getIntOr(NBT_DURATION, duration);
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput compound) 
    {
    	super.addAdditionalSaveData(compound);
    	compound.putInt(NBT_DURATION, duration);
    }
}
