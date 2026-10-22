package com.oblivioussp.spartanweaponry.client.item;

import java.util.function.BiConsumer;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.ModToolActions;
import com.oblivioussp.spartanweaponry.api.ToolAction;
import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.OilHandler;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.LongbowItem;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import com.oblivioussp.spartanweaponry.util.OilHelper;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 1.21.4+: the item definitions (assets/spartanweaponry/items) ask these properties and tints, instead of the item
 * properties and item colours of 1.21.1 (ClientHelper#register...PropertyOverrides, OilCoatingColours). Each is asked
 * once per stack on screen per frame, not per quad.
 */
public final class WeaponryItemModels
{
	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, path);
	}

	private static boolean using(ItemStack stack, @Nullable LivingEntity living)
	{
		return living != null && living.isUsingItem() && living.getUseItem() == stack;
	}

	/** spartanweaponry:blocking: a melee weapon held up to block. */
	public record Blocking() implements ConditionalItemModelProperty
	{
		public static final MapCodec<Blocking> MAP_CODEC = MapCodec.unit(new Blocking());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living, int seed, ItemDisplayContext context)
		{
			return using(stack, living) && ToolAction.canPerform(stack, ModToolActions.MELEE_BLOCK);
		}

		@Override
		public MapCodec<Blocking> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:throwing: a throwing weapon, or a melee weapon with the throwable trait, being drawn back. */
	public record Throwing() implements ConditionalItemModelProperty
	{
		public static final MapCodec<Throwing> MAP_CODEC = MapCodec.unit(new Throwing());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living, int seed, ItemDisplayContext context)
		{
			if(living == null || living.getTicksUsingItem() <= 0 || !stack.is(living.getUseItem().getItem()))
				return false;
			return !(stack.getItem() instanceof SwordBaseItem weapon) || weapon.hasWeaponTrait(WeaponTraits.THROWABLE.get());
		}

		@Override
		public MapCodec<Throwing> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:empty: a throwing weapon with all of its ammo thrown. */
	public record Empty() implements ConditionalItemModelProperty
	{
		public static final MapCodec<Empty> MAP_CODEC = MapCodec.unit(new Empty());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living, int seed, ItemDisplayContext context)
		{
			return stack.getItem() instanceof ThrowingWeaponItem weapon && ItemNbt.read(stack).getIntOr(ThrowingWeaponItem.NBT_AMMO_USED, 0) == weapon.getMaxAmmo(stack);
		}

		@Override
		public MapCodec<Empty> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:charged: a loaded heavy crossbow. */
	public record Charged() implements ConditionalItemModelProperty
	{
		public static final MapCodec<Charged> MAP_CODEC = MapCodec.unit(new Charged());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living, int seed, ItemDisplayContext context)
		{
			return stack.getItem() instanceof HeavyCrossbowItem crossbow && crossbow.isLoaded(stack);
		}

		@Override
		public MapCodec<Charged> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:oiled: an oiled weapon, which gets the coating layer. */
	public record Oiled() implements ConditionalItemModelProperty
	{
		public static final MapCodec<Oiled> MAP_CODEC = MapCodec.unit(new Oiled());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living, int seed, ItemDisplayContext context)
		{
			return stack.is(ModItemTags.OILABLE_WEAPONS) && OilHandler.isOiled(stack);
		}

		@Override
		public MapCodec<Oiled> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:pull: how far a longbow is drawn or a heavy crossbow is loaded (0 to 1). */
	public record Pull() implements RangeSelectItemModelProperty
	{
		public static final MapCodec<Pull> MAP_CODEC = MapCodec.unit(new Pull());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed)
		{
			LivingEntity living = owner == null ? null : owner.asLivingEntity();
			if(living == null)
				return 0.0f;
			if(stack.getItem() instanceof HeavyCrossbowItem crossbow)
				return crossbow.isLoaded(stack) ? 0.0f : (float)crossbow.getLoadingTicks(stack, living) / crossbow.getFullLoadTicks(stack);
			if(stack.getItem() instanceof LongbowItem longbow)
				return living.getUseItem() == stack ? longbow.getNockProgress(stack, living) : 0.0f;
			return 0.0f;
		}

		@Override
		public MapCodec<Pull> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:ammo: the number of arrows or bolts in a quiver. */
	public record Ammo() implements RangeSelectItemModelProperty
	{
		public static final MapCodec<Ammo> MAP_CODEC = MapCodec.unit(new Ammo());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed)
		{
			return stack.getItem() instanceof QuiverBaseItem quiver ? quiver.getAmmoCount(stack) : 0.0f;
		}

		@Override
		public MapCodec<Ammo> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:potion: the potion of a tipped arrow or bolt. */
	public record PotionTint() implements ItemTintSource
	{
		public static final MapCodec<PotionTint> MAP_CODEC = MapCodec.unit(new PotionTint());

		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living)
		{
			return ARGB.opaque(PotionHelper.getColor(stack));
		}

		@Override
		public MapCodec<PotionTint> type()
		{
			return MAP_CODEC;
		}
	}

	/** spartanweaponry:oil: the oil of a weapon oil bottle, or of the coating of an oiled weapon. */
	public record OilTint() implements ItemTintSource
	{
		public static final MapCodec<OilTint> MAP_CODEC = MapCodec.unit(new OilTint());

		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity living)
		{
			return ARGB.opaque(OilHelper.getOilFromStack(stack).getColor(stack));
		}

		@Override
		public MapCodec<OilTint> type()
		{
			return MAP_CODEC;
		}
	}

	public static void registerConditionalProperties(BiConsumer<Identifier, MapCodec<? extends ConditionalItemModelProperty>> registrar)
	{
		registrar.accept(id("blocking"), Blocking.MAP_CODEC);
		registrar.accept(id("throwing"), Throwing.MAP_CODEC);
		registrar.accept(id("empty"), Empty.MAP_CODEC);
		registrar.accept(id("charged"), Charged.MAP_CODEC);
		registrar.accept(id("oiled"), Oiled.MAP_CODEC);
	}

	public static void registerRangeProperties(BiConsumer<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> registrar)
	{
		registrar.accept(id("pull"), Pull.MAP_CODEC);
		registrar.accept(id("ammo"), Ammo.MAP_CODEC);
	}

	public static void registerTintSources(BiConsumer<Identifier, MapCodec<? extends ItemTintSource>> registrar)
	{
		registrar.accept(id("potion"), PotionTint.MAP_CODEC);
		registrar.accept(id("oil"), OilTint.MAP_CODEC);
	}
}
