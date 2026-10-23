package com.oblivioussp.spartanweaponry.item;

import javax.annotation.Nullable;
import com.oblivioussp.spartanweaponry.util.TooltipLines;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.server.level.ServerLevel;

import com.oblivioussp.spartanweaponry.util.ItemDamage;

import com.oblivioussp.spartanweaponry.util.WeaponAttributes;

import net.minecraft.world.item.component.ItemAttributeModifiers;

import net.minecraft.core.Holder;

import com.oblivioussp.spartanweaponry.util.Enchants;

import com.oblivioussp.spartanweaponry.util.ItemNbt;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.IReloadable;
import com.oblivioussp.spartanweaponry.api.IWeaponTraitContainer;
import com.oblivioussp.spartanweaponry.api.ReloadableHandler;
import com.oblivioussp.spartanweaponry.api.WeaponMaterial;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.api.trait.IGenericTraitCallback;
import com.oblivioussp.spartanweaponry.api.trait.IThrowingTraitCallback;
import com.oblivioussp.spartanweaponry.api.trait.WeaponTrait;
import com.oblivioussp.spartanweaponry.client.gui.HudCrosshairThrowingWeapon;
import com.oblivioussp.spartanweaponry.client.gui.ICrosshairOverlay;
import com.oblivioussp.spartanweaponry.entity.projectile.ThrowingWeaponEntity;
import com.oblivioussp.spartanweaponry.init.ModEnchantments;
import com.oblivioussp.spartanweaponry.init.ModEntities;
import com.oblivioussp.spartanweaponry.init.ModSounds;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.WeaponArchetype;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;

public class ThrowingWeaponItem extends Item implements IWeaponTraitContainer<ThrowingWeaponItem>, IReloadable, IHudCrosshair
{
	public static final String NBT_AMMO_USED = "AmmoUsed";
	public static final String NBT_UUID = "UUID";
	public static final String NBT_ORIGINAL = "Original";
	
	protected float attackDamage = 1.0f;
	protected double attackSpeed = 0.0D;
	protected float throwVelocity = 2.0f;
	protected float throwDamageMultiplier = 2.0f;
	protected WeaponMaterial material;
	protected String customDisplayName = null;
	protected boolean doCraftCheck = true;
	protected boolean canBeCrafted = true;
	protected int maxAmmo = 1;
	protected int maxChargeTicks = 5;

	protected Multimap<Holder<Attribute>, AttributeModifier> modifiers;
	protected ItemAttributeModifiers defaultModifiers = ItemAttributeModifiers.EMPTY;
	protected final WeaponArchetype archetype;
	
	protected List<WeaponTrait> traits;
	
	public ThrowingWeaponItem(Item.Properties prop, WeaponMaterial materialIn, WeaponArchetype archetypeIn, float weaponBaseDamage, float weaponDamageMultiplier, float weaponSpeed, int maxAmmoCapacity, int chargeTicks)
	{
		super(prop.durability(materialIn.getUses() / 4).repairable(materialIn.getRepairTag()).enchantable(materialIn.getEnchantmentValue()));
		material = materialIn;
		setAttackDamage(weaponBaseDamage, weaponDamageMultiplier);
		setAttackSpeed(weaponSpeed);
		maxAmmo = maxAmmoCapacity;
		setChargeTicks(chargeTicks);

		
		archetype = archetypeIn;
		ReloadableHandler.addToItemReloadList(this);
	}
	
	public ThrowingWeaponItem(Item.Properties prop, WeaponMaterial material, WeaponArchetype archetypeIn, float weaponBaseDamage, float weaponDamageMultiplier, float weaponSpeed, int maxAmmoCapacity, int chargeTicks, String customDisplayNameIn)
	{
		this(prop, material, archetypeIn, weaponBaseDamage, weaponDamageMultiplier, weaponSpeed, maxAmmoCapacity, chargeTicks);
		if(material.useCustomDisplayName())
			customDisplayName = customDisplayNameIn;
	}

	@Override
	public void reload() 
	{
		// Port fix (issue #183 of the original): damage and speed from the archetype (config and weapon_attributes),
		// like the melee weapons; 1.20.1 kept the values the item was created with
		setAttackDamage(archetype.getBaseDamage(), archetype.getDamageMultiplier());
		setAttackSpeed(archetype.getAttackSpeed());
		
		ImmutableList.Builder<WeaponTrait> builder = ImmutableList.builder();
		builder.addAll(archetype.getTraits());
		builder.addAll(material.getBonusTraits(archetype.getType()));
		traits = builder.build();

		// Initialize the weapon's attribute modifier map
		ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> mapBuilder = ImmutableMultimap.builder();
		mapBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)getDirectAttackDamage(), AttributeModifier.Operation.ADD_VALUE));
		mapBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed - 4.0d, AttributeModifier.Operation.ADD_VALUE));
		
		// Add melee attributes from Weapon Traits
//		if(traits != null)
			traits.forEach((trait) -> getGenericCallback(trait).ifPresent((callback) -> callback.onModifyAttributes(mapBuilder)));
		
		modifiers = mapBuilder.build();
		defaultModifiers = WeaponAttributes.toItemModifiers(modifiers);

		WeaponTrait extraDamageTrait = getFirstWeaponTraitWithType(WeaponTraits.TYPE_DAMAGE_BONUS_THROWN);
		throwDamageMultiplier = extraDamageTrait != null ? extraDamageTrait.getMagnitude() : 1.0f;
	}
	
	// NeoForge IItemExtension (Fabric: a mixin)
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return WeaponAttributes.forStack(stack, defaultModifiers, components().getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY));
	}
	
	// Port fix: durability follows the material config (1.20.1 kept the value from item creation, like issue #167)
	// NeoForge IItemExtension (Fabric: a mixin)
	public int getMaxDamage(ItemStack stack) 
	{
		return Math.max(1, material.getUses() / 4);
	}
	
	@Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot equipmentSlot)
	{
		// 1.21.5+: server side only, with the equipment slot instead of the inventory slot and "selected"
		int itemSlot = equipmentSlot == null ? -1 : equipmentSlot.getIndex();
		boolean isSelected = equipmentSlot == EquipmentSlot.MAINHAND;
		initNBT(stack, true);
		
		if(entity instanceof LivingEntity)
		{
			LivingEntity living = (LivingEntity)entity;
			
			if(traits != null)
			{
				traits.forEach((trait) -> getGenericCallback(trait).ifPresent((callback) -> callback.onItemUpdate(material, stack, level, living, itemSlot, isSelected)));
			}
		}
    }

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
    {
		return archetype.isBladed() && state.is(Blocks.COBWEB) ? 15.0f : 1.0f;
    }
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos,
			LivingEntity entityLiving)
	{
		// Make the throwing weapon take damage when digging
		if(!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0f)
		{
			damageThrowingWeapon(stack, 2, entityLiving);
		}
		return false;
	}
	
	public void damageThrowingWeapon(ItemStack stack, int damage, LivingEntity entity)
	{
		//stack.damageItem(damage, entity);
		if(stack.isDamageableItem() && ItemNbt.read(stack).getIntOr(NBT_AMMO_USED, 0) < getMaxAmmo(stack) && 
				(!(entity instanceof Player) || !((Player)entity).getAbilities().instabuild) && entity.level() instanceof ServerLevel serverLevel)
		{
			if(ItemDamage.hurt(stack, damage, serverLevel, entity instanceof ServerPlayer ? (ServerPlayer)entity : null))
			{
				EquipmentSlot breakSlot = stack == entity.getOffhandItem() ? EquipmentSlot.OFFHAND : stack == entity.getMainHandItem() ? EquipmentSlot.MAINHAND : null;
				if(breakSlot != null)
					entity.onEquippedItemBroken(stack, breakSlot);
				
				int ammo = ItemNbt.read(stack).getIntOr(NBT_AMMO_USED, 0) + 1;
				ItemNbt.update(stack, (nbt) -> nbt.putInt(NBT_AMMO_USED, ammo));
				
				if(entity instanceof Player)
				{
					((Player)entity).awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
				}
				
				stack.setDamageValue(0);
			}
		}
	}
	
	@Override
	public Component getName(ItemStack stack) 
	{
		if(customDisplayName == null)
			return super.getName(stack);
		return Component.translatable(customDisplayName, material.translateName());
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext contextIn, TooltipDisplay display, Consumer<Component> tooltipOut, TooltipFlag flagIn) 
	{
		// 1.21.5+: a Consumer (TooltipLines keeps the List code of 1.21.1)
		TooltipLines tooltip = new TooltipLines(tooltipOut);
		try
		{
			boolean isShiftPressed = net.minecraft.client.Minecraft.getInstance().hasShiftDown();

	    	if(doCraftCheck && contextIn.registries() != null)
	    	{
	    		if(!ClientConfig.INSTANCE.forceDisableUncraftableTooltips.get() && material.getModId() == ModSpartanWeaponry.ID)
	    		{
	        		if(!WeaponAttributes.isTagFilled(material.getRepairTag()))
		    			canBeCrafted = false;
	    		}
		    	doCraftCheck = false;
	    	}

	    	if(!canBeCrafted)
	    		tooltip.add(Component.translatable(String.format("tooltip.%s.uncraftable_missing_material", ModSpartanWeaponry.ID), material.getRepairTagName()).withStyle(ChatFormatting.RED));
    	
	    	archetype.addTagErrorTooltip(stack, tooltip);
	    	material.addTagErrorTooltip(stack, tooltip);
    	
	    	if(ItemNbt.read(stack).contains(NBT_ORIGINAL) && !ItemNbt.read(stack).getBooleanOr(NBT_ORIGINAL, false))
	    		tooltip.add(Component.translatable(String.format("tooltip.%s.throwable.not_original", ModSpartanWeaponry.ID)).withStyle(ChatFormatting.DARK_RED));
	    	if(ItemNbt.read(stack).read(NBT_UUID, net.minecraft.core.UUIDUtil.CODEC).isPresent() && flagIn.isAdvanced())
	    		tooltip.add(Component.literal("UUID: " + ChatFormatting.GRAY.toString() + ItemNbt.read(stack).read(NBT_UUID, net.minecraft.core.UUIDUtil.CODEC).orElseThrow().toString()).withStyle(ChatFormatting.DARK_PURPLE));
	    	int mxAmmo = getMaxAmmo(stack);
	    	tooltip.add(Component.translatable(String.format("tooltip.%s.throwable.ammo", ModSpartanWeaponry.ID), Component.translatable(String.format("tooltip.%s.throwable.ammo.value", ModSpartanWeaponry.ID), mxAmmo - ItemNbt.read(stack).getIntOr(NBT_AMMO_USED, 0), mxAmmo).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_AQUA));
			tooltip.add(Component.translatable(String.format("tooltip.%s.throwable.charge_time", ModSpartanWeaponry.ID), Component.translatable(String.format("tooltip.%s.throwable.charge_time.value", ModSpartanWeaponry.ID), getMaxChargeTicks(stack) / 20.0f).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_AQUA));
    	
			if(traits != null && !traits.isEmpty())
			{
				if(isShiftPressed)
					tooltip.add(Component.translatable(String.format("tooltip.%s.traits", ModSpartanWeaponry.ID), Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".showing_details").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GOLD));
				else
					tooltip.add(Component.translatable(String.format("tooltip.%s.traits", ModSpartanWeaponry.ID), Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".show_details", ChatFormatting.AQUA.toString() + "SHIFT").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GOLD));

				archetype.addTraitsToTooltip(stack, tooltip, isShiftPressed);
			
		    	material.addTraitsToTooltip(stack, archetype.getType(), tooltip, isShiftPressed);
				tooltip.add(Component.empty());
			}

			tooltip.flush();
			super.appendHoverText(stack, contextIn, display, tooltipOut, flagIn);
		}
		finally
		{
			tooltip.flush();
		}
	}

	@Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
		traits.forEach((trait) -> trait.getMeleeCallback().ifPresent((callback) -> callback.onHitEntity(material, stack, target, attacker, null)));
    	
		// Deal double durability damage when used as a melee weapon
    	if(ItemNbt.read(stack).getIntOr(NBT_AMMO_USED, 0) < getMaxAmmo(stack))
    		damageThrowingWeapon(stack, 2, attacker);
    	
        return;
    }
	
	@Override
	public InteractionResult use(Level levelIn, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(ItemNbt.read(stack).getIntOr(NBT_AMMO_USED, 0) == getMaxAmmo(stack))
			return InteractionResult.FAIL;
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
	}
	
	@Override
	public boolean releaseUsing(ItemStack stack, Level levelIn, LivingEntity entityLiving, int timeLeft) 
	{
		if(entityLiving instanceof Player)
		{
			int maxAmmo = getMaxAmmo(stack);
			int ammoCount = maxAmmo - ItemNbt.read(stack).getIntOr(NBT_AMMO_USED, 0);
			
			if(ammoCount > 0)
			{
				Player player = (Player)entityLiving;
	
				int maxCharge = getMaxChargeTicks(stack);
	            int charge = Math.min(getUseDuration(stack, entityLiving) - timeLeft, maxCharge);
				
				if (!levelIn.isClientSide() && charge > 2)
		        {
		            ThrowingWeaponEntity thrown = createThrowingWeaponEntity(levelIn, player, stack, charge);
		            float chargePerc = (charge / (float)maxCharge);
		            
		            if(thrown == null)	return false;
		            
		            thrown.setWeapon(stack);
		            int velocityBonus = Enchants.level(stack, ModEnchantments.PROPEL);
		            thrown.shootFromRotation(player, player.xRotO, player.yRotO, 0.0F, throwVelocity * ((velocityBonus * 0.2f) + 1) * (chargePerc * 0.9f + 0.1f), 0.5f);
		            
		            traits.forEach((trait) -> trait.getThrowingCallback().ifPresent((callback) -> callback.onThrowingProjectileSpawn(material, thrown)));
		            
		            double damageMultiplier = (throwDamageMultiplier - 1.0f) * chargePerc + 1.0f;
		            thrown.setBaseDamage((getDirectAttackDamage() + 1.0d) * damageMultiplier);
		            
		            // Apply enchantments as necessary
		            int j = Enchants.level(stack, ModEnchantments.RAZORS_EDGE);
		            if (j > 0)
		            {
		            	thrown.setBaseDamage(thrown.getBaseDamage() + j * 0.5D + 0.5D);
		            }
		            if (Enchants.level(stack, ModEnchantments.INCENDIARY) > 0)
		            {
		            	thrown.igniteForSeconds(100);
		            }
		            /*int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
		            if (k > 0)
		            {
		            	thrown.setKnockbackStrength(k);
		            }*/
		            
		            if(player.getAbilities().instabuild)
		            	thrown.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		            else if(thrown.isValidThrowingWeapon())
		            {
		            	ammoCount--;
		            	int ammoUsed = maxAmmo - ammoCount;
		            	ItemNbt.update(stack, (nbt) -> nbt.putInt(NBT_AMMO_USED, ammoUsed));
		            	
		            	// If there is no ammo left and the stack isn't original (picked up from the ground to create a new stack), then delete the stack
		            	if(ammoCount == 0 && !ItemNbt.read(stack).getBooleanOr(NBT_ORIGINAL, false))
		            	{
			                stack.shrink(1);
			                if(stack.getCount() <= 0)
			                	player.getInventory().removeItem(stack);
		            	}
		            }
		            
		            if(thrown.isValidThrowingWeapon())
		            {
		            	stack.setDamageValue(0);
		            	levelIn.playSound((Player)null, player.getX(), player.getY(), player.getZ(), getThrowingSound(), SoundSource.PLAYERS, 0.5F, 0.4F / (levelIn.getRandom().nextFloat() * 0.4F + 0.8F));
		            	levelIn.addFreshEntity(thrown);
		            }
		            
			        player.awardStat(Stats.ITEM_USED.get(this));
		        }
			}
		}
		super.releaseUsing(stack, levelIn, entityLiving, timeLeft);
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) 
	{
		return 72000;
	}
	
	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) 
	{
		return ItemUseAnimation.SPEAR;
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player playerIn) 
	{
		traits.forEach((trait) -> getGenericCallback(trait).ifPresent((callback) -> callback.onCreateItem(material, stack)));
		
		initNBT(stack, true);
	}
	
	public ItemStack makeTabStack()
	{
		ItemStack stack = new ItemStack(this);
		
		initNBT(stack, false);
		return stack;
	}

	// NeoForge IItemExtension (Fabric: getEnchantmentValue() above)
	public int getEnchantmentValue(ItemStack stack) 
	{
		return material.getEnchantmentValue();
	}
	
	// Allow Loyalty enchantments to work on Throwing Weapons (1.20.1 canApplyAtEnchantingTable)
	// NeoForge IItemExtension (Fabric: a mixin)
	public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) 
	{
		return ItemExtensions.isPrimaryItemFor(stack, enchantment, supportsEnchantment(stack, enchantment)) || enchantment.is(Enchantments.LOYALTY);
	}
	
	// NeoForge IItemExtension (Fabric: a mixin)
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) 
	{
		return ItemExtensions.supportsEnchantment(stack, enchantment) || enchantment.is(Enchantments.LOYALTY);
	}
    
    // NeoForge IItemExtension (Fabric: a mixin)
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<ItemStack> onBroken) 
    {
    	int damage = amount;
    	for(WeaponTrait trait : traits)
    	{
    		if(trait.getGenericCallback().isPresent())
    			damage = trait.getGenericCallback().get().onDamageItem(stack, entity, damage);
    		if(damage <= 0)
    			break;
    	}
    	return Math.max(0, damage);
    }
	
	// IWeaponTraitContainer

	@Override
	public ThrowingWeaponItem getAsItem() 
	{
		return this;
	}
    
	@Override
	public boolean hasWeaponTrait(WeaponTrait prop)
	{
		return traits.contains(prop);
	}

	@Override
	public boolean hasWeaponTraitWithType(String type) 
	{
		return traits.stream().anyMatch((trait) -> trait.getType() == type);
	}

	@Override
	public WeaponTrait getFirstWeaponTraitWithType(String type)
	{
		for(WeaponTrait trait : traits)
		{
			if(trait.getType() == type)
				return trait;
		}
		return null;
	}

	@Override
	public List<WeaponTrait> getAllWeaponTraitsWithType(String type) 
	{
		if(traits.isEmpty())
			return ImmutableList.of();
		
		return traits.stream().filter((trait) -> trait.getType() == type).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Collection<WeaponTrait> getAllWeaponTraits() 
	{
		// Traits are immutable after reloading anyway so it should be safe to reference them directly
		return traits;
	}

	@Override
	public WeaponMaterial getMaterial()
	{
		return material;
	}
	
	// New methods
	public float getDirectAttackDamage() 
	{
		return attackDamage;
	}

	public void setAttackDamage(float baseDamage, float damageMultiplier)
	{
		attackDamage = (material.getAttackDamageBonus() * damageMultiplier) + baseDamage - 1.0f;
	}
	
	public void setAttackSpeed(double speed)
	{
		attackSpeed = speed;
	}
	
	public void setChargeTicks(int chargeTicks)
	{
		maxChargeTicks = chargeTicks;
	}
	
	private Optional<IGenericTraitCallback> getGenericCallback(WeaponTrait trait)
	{
		return trait.getMeleeCallback().isPresent() ? Optional.of(trait.getMeleeCallback().get()) : trait.getGenericCallback().isPresent() ? trait.getGenericCallback() : Optional.empty();
	}
	
	public void updateFromConfig(float baseDamage, float damageMultiplier, double speed, int chargeTicks)
	{
		setAttackDamage(baseDamage, damageMultiplier);
		setAttackSpeed(speed);
		setChargeTicks(chargeTicks);
	}
	
	/**
	 * Creates a new Throwing Weapon Entity that is used as a projectile.
	 * @param levelIn The World instance
	 * @param player The Player throwing the weapon
	 * @param stack The Throwing Weapon Item
	 * @param charge The total time (in ticks) that the weapon is held for before throwing it
	 * @return
	 */
	public ThrowingWeaponEntity createThrowingWeaponEntity(Level levelIn, Player player, ItemStack stack, int charge)
	{
		return new ThrowingWeaponEntity(ModEntities.THROWING_WEAPON.get(), player, levelIn);
	}
	
	protected SoundEvent getThrowingSound()
	{
		return ModSounds.THROWN_WEAPON_THROW.get();
	}
	
	protected void initNBT(ItemStack stack, boolean initUUID) 
	{
		// runs every tick for every throwing weapon in an inventory: nothing to copy once the tags are there
		CompoundTag current = ItemNbt.read(stack);
		if(current.contains(NBT_AMMO_USED) && (!initUUID || current.read(NBT_UUID, net.minecraft.core.UUIDUtil.CODEC).isPresent()))
			return;
		CompoundTag tag = ItemNbt.get(stack);
		boolean changed = false;
		// Initialise ammo tag if necessary
		if(!tag.contains(NBT_AMMO_USED))
		{
			// And, because I don't think it would be a good idea to transfer the ammo value from the old version to the new one
			// Just fill 'er up
			tag.putInt(NBT_AMMO_USED, 0);
			changed = true;
		}
		// Initialise UUID tag if necessary, and flag as original stack
	    if(initUUID && !tag.read(NBT_UUID, net.minecraft.core.UUIDUtil.CODEC).isPresent())
	    {
			tag.store(NBT_UUID, net.minecraft.core.UUIDUtil.CODEC, UUID.randomUUID());
			tag.putBoolean(NBT_ORIGINAL, true);
			changed = true;
	    }
	    if(changed)
	    	ItemNbt.set(stack, tag);
	}
	
	public int getMaxAmmo(ItemStack stack)
	{
		int level = Enchants.level(stack, ModEnchantments.EXPANSE);
		// Find the value to increase by per level (if ammo increase is too small e.g. Boomerang; then use ammo + 1 per level instead)
		int increasePerLevel = Math.max((int)(maxAmmo * 0.25f), 1);
		return maxAmmo + (increasePerLevel * level);
	}
	
	public int getMaxAmmoBase()
	{
		return maxAmmo;
	}
	
	public int getMaxChargeTicks(ItemStack stack)
	{
		int chargeTicks = (int)(maxChargeTicks * (1 - Enchants.level(stack, ModEnchantments.SUPERCHARGE) * 0.2f));
		if(traits != null)
			for(WeaponTrait trait : traits)
			{
				Optional<IThrowingTraitCallback> opt = trait.getThrowingCallback();
				if(opt.isPresent())
					chargeTicks = opt.get().modifyThrowingChargeTime(material, chargeTicks);
			}
		return chargeTicks;
	}
    
    @Override
    public ICrosshairOverlay getCrosshairHudElement() 
    {
    	return HudCrosshairThrowingWeapon::render;
    }
}
