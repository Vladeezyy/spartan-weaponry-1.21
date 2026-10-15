package com.oblivioussp.spartanweaponry.item;

import net.minecraft.server.level.ServerLevel;

import com.oblivioussp.spartanweaponry.util.WeaponAttributes;

import net.minecraft.world.item.component.ItemAttributeModifiers;

import net.minecraft.core.Holder;

import com.oblivioussp.spartanweaponry.util.Enchants;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.IReloadable;
import com.oblivioussp.spartanweaponry.api.ReloadableHandler;
import com.oblivioussp.spartanweaponry.api.WeaponMaterial;
import com.oblivioussp.spartanweaponry.api.trait.IGenericTraitCallback;
import com.oblivioussp.spartanweaponry.api.trait.IRangedTraitCallback;
import com.oblivioussp.spartanweaponry.api.trait.WeaponTrait;
import com.oblivioussp.spartanweaponry.client.ClientHelper;
import com.oblivioussp.spartanweaponry.util.ClientConfig;
import com.oblivioussp.spartanweaponry.util.Defaults;
import com.oblivioussp.spartanweaponry.util.WeaponType;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.fml.loading.FMLEnvironment;
import net.minecraft.core.registries.BuiltInRegistries;

public class LongbowItem extends BowItem implements IReloadable
{
	protected WeaponMaterial material;
	protected float drawTime = 1.25f;
	protected float maxVelocity = 1.25f;
	protected String modId = null;
	protected String customDisplayName = null;
	protected boolean doCraftCheck = true;
	protected boolean canBeCrafted = true;
	
	protected final WeaponType type = WeaponType.RANGED;
	protected List<WeaponTrait> rangedTraits;
	protected Multimap<Holder<Attribute>, AttributeModifier> modifiers;
	protected ItemAttributeModifiers defaultModifiers = ItemAttributeModifiers.EMPTY;
	
	public LongbowItem(Item.Properties prop, WeaponMaterial material)
	{
		super(prop.durability(Math.max(1, (int)(material.getUses() * 2.0f))));
		this.material = material;
		maxVelocity = Defaults.MultiplierLongbow;
		
		if(FMLEnvironment.dist.isClient())
			ClientHelper.registerLongbowPropertyOverrides(this);
		
		ReloadableHandler.addToItemReloadList(this);
	}
	
	public LongbowItem(Item.Properties prop, WeaponMaterial material, String customDisplayName)
	{
		this(prop, material);
		if(material.useCustomDisplayName())
			this.customDisplayName = customDisplayName;
	}
	
	@Override
	public void reload()
	{
		drawTime = 1.25f;
		rangedTraits = material.getBonusTraits(type);

		ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> mapBuilder = ImmutableMultimap.builder();
		for(WeaponTrait trait : rangedTraits)
		{
			Optional<IRangedTraitCallback> opt = trait.getRangedCallback();
			if(opt.isPresent())
			{
				drawTime = opt.get().modifyLongbowDrawTime(material, drawTime);
			}
			Optional<IGenericTraitCallback> generic = trait.getGenericCallback();
			generic.ifPresent((callback) -> callback.onModifyAttributes(mapBuilder));
		}
		modifiers = mapBuilder.build();
		defaultModifiers = WeaponAttributes.toItemModifiers(modifiers);
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
		return WeaponAttributes.forStack(stack, defaultModifiers, super.getDefaultAttributeModifiers(stack));
    }
	
	// Port fix (issue #167 of the original): durability follows the material config (1.20.1 kept the value from
	// item creation)
	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return Math.max(1, (int)(material.getUses() * 2.0f));
	}
	
	/**
     * Called when the player stops using an Item (stops holding the right mouse button).
     * 1.21: ammo through ProjectileWeaponItem.draw (Infinity, creative ammo) and the longbow is the arrows' weapon, so
     * Power, Punch, Flame and Piercing work as on a vanilla bow (1.20.1 applied the first three by hand, same numbers).
     */
	@Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft)
    {
        if (entityLiving instanceof Player player)
        {
            ItemStack itemstack = player.getProjectile(stack);
            if (itemstack.isEmpty())
            	return;

            int i = this.getUseDuration(stack, entityLiving) - timeLeft;
            i = EventHooks.onArrowLoose(stack, level, player, i, true);
            if (i < 0) return;

            float f = getArrowSpeed(i);

            if (f >= 0.1D)
            {
            	List<ItemStack> projectiles = draw(stack, itemstack, player);
                if (level instanceof ServerLevel && !projectiles.isEmpty())
                {
                	for(ItemStack ammo : projectiles)
                	{
	                	ArrowItem itemarrow = ammo.getItem() instanceof ArrowItem arrowItem ? arrowItem : (ArrowItem)Items.ARROW;
	                    AbstractArrow entityarrow = customArrow(itemarrow.createArrow(level, ammo, player, stack), ammo, stack);
	                    entityarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, f * 3.0f, 0.5f);
	                    
	                	for(WeaponTrait trait : rangedTraits)
	                		trait.getRangedCallback().ifPresent((callback) -> callback.onProjectileSpawn(material, entityarrow));
	                    
	                    if (f >= maxVelocity)
	                        entityarrow.setCritArrow(true);
	
	                    level.addFreshEntity(entityarrow);
                	}
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                }

                level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (level.random.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

		/**
     * Gets the velocity of the arrow entity from the longbow's charge
     */
    public float getArrowSpeed(int charge)
    {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f > maxVelocity)
        {
            f = maxVelocity;
        }

        return f;
    }
	
	@Override
	public int getEnchantmentValue(ItemStack stack)
	{
		return material.getEnchantmentValue();
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) 
	{
		return material.getRepairIngredient().test(repair);
	}
    
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) 
    {
    	int damage = amount;
    	for(WeaponTrait trait : rangedTraits)
    	{
    		if(trait.getGenericCallback().isPresent())
    			damage = trait.getGenericCallback().get().onDamageItem(stack, entity, damage);
    		if(damage <= 0)
    			break;
    	}
    	return Math.max(0, damage);
    }

	@Override
	public Component getName(ItemStack stack) 
	{
		if(customDisplayName == null)
			return super.getName(stack);
		return Component.translatable(customDisplayName, material.translateName());
	}
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext contextIn, List<Component> tooltip, TooltipFlag flagIn)
    {
		boolean isShiftPressed = Screen.hasShiftDown();
		
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

    	material.addTagErrorTooltip(stack, tooltip);
		
    	if(material.hasAnyBonusTraits(type))
    	{
			if(rangedTraits != null && !rangedTraits.isEmpty())
			{
				if(isShiftPressed)
					tooltip.add(Component.translatable(String.format("tooltip.%s.traits", ModSpartanWeaponry.ID), Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".showing_details").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GOLD));
				else
					tooltip.add(Component.translatable(String.format("tooltip.%s.traits", ModSpartanWeaponry.ID), Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".show_details", ChatFormatting.DARK_AQUA.toString() + "SHIFT").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GOLD));
				tooltip.add(Component.translatable(String.format("tooltip.%s.trait.material_bonus", ModSpartanWeaponry.ID)).withStyle(ChatFormatting.AQUA));
				
				rangedTraits.forEach((trait) -> trait.addTooltip(stack, tooltip, isShiftPressed));
	        	tooltip.add(Component.empty());
			}
    	}
		
    	if(isShiftPressed)
    	{
			tooltip.add(Component.translatable(String.format("tooltip.%s.description", ModSpartanWeaponry.ID), Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".showing_details").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GOLD));
    		tooltip.add(Component.translatable(String.format("tooltip.%s.longbow.desc", ModSpartanWeaponry.ID)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    		tooltip.add(Component.translatable(String.format("tooltip.%s.longbow.desc_2", ModSpartanWeaponry.ID)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    	}
    	else
			tooltip.add(Component.translatable(String.format("tooltip.%s.description", ModSpartanWeaponry.ID), Component.translatable("tooltip." + ModSpartanWeaponry.ID + ".show_details", ChatFormatting.AQUA.toString() + "SHIFT").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GOLD));
   	
    	
    	tooltip.add(Component.empty());
    	tooltip.add(Component.translatable(String.format("tooltip.%s.modifiers.ammo.type", ModSpartanWeaponry.ID), Component.translatable(String.format("tooltip.%s.modifiers.ammo.arrow", ModSpartanWeaponry.ID)).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_AQUA));
    	tooltip.add(Component.translatable(String.format("tooltip.%s.modifiers.longbow.draw_length", ModSpartanWeaponry.ID), Component.translatable(String.format("tooltip.%s.modifiers.longbow.draw_length.value", ModSpartanWeaponry.ID), drawTime).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_AQUA));
    	tooltip.add(Component.translatable(String.format("tooltip.%s.modifiers.longbow.speed_multiplier", ModSpartanWeaponry.ID), Component.translatable(String.format("tooltip.%s.modifiers.longbow.speed_multiplier.value", ModSpartanWeaponry.ID), maxVelocity).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_AQUA));
    	tooltip.add(Component.empty());
    }
	
	// ---- ---- ---- ---- ---- ---- ---- ----
	// Internal methods
	// ---- ---- ---- ---- ---- ---- ---- ----
    public float getNockProgress(ItemStack stack, LivingEntity shooter)
    {
    	return (float)(shooter.getTicksUsingItem() / (20.0F * drawTime));
    }
}
