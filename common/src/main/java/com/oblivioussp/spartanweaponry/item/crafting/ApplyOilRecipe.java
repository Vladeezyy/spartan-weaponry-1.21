package com.oblivioussp.spartanweaponry.item.crafting;

import java.util.Optional;

import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.api.oil.OilEffect;
import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.init.ModRecipeSerializers;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.core.Holder;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ApplyOilRecipe extends CustomRecipe
{
	/** 26.1: a RecipeSerializer is a record of the two codecs; the recipe has no fields */
	public static final ApplyOilRecipe INSTANCE = new ApplyOilRecipe();
	public static final RecipeSerializer<ApplyOilRecipe> SERIALIZER = new RecipeSerializer<>(MapCodec.unit(INSTANCE), StreamCodec.unit(INSTANCE));
	
	public ApplyOilRecipe() {}

	@Override
	public boolean matches(CraftingInput containerIn, Level levelIn)
	{
		boolean foundOil = false,
				foundWeapon = false;
		
		for(int i = 0; i < containerIn.width(); i++)
		{
			for(int j = 0; j < containerIn.height(); j++)
			{
				ItemStack stack = containerIn.getItem(j * containerIn.width() + i);
				// Oil found
				if(stack.is(ModItems.WEAPON_OIL.get()))
				{
					// Aleady have an oil; not a valid recipe
					if(foundOil)
						return false;
					foundOil = true;
				}
				// Oilable weapon found
				else if(stack.is(ModItemTags.OILABLE_WEAPONS))
				{
					// Aleady have an oilable weapon; not a valid recipe
					if(foundWeapon)
						return false;
					foundWeapon = true;
				}
				// Other item found; not a valid recipe
				else if(!stack.isEmpty())
					return false;
			}
		}
		return foundOil && foundWeapon;
	}

	@Override
	public ItemStack assemble(CraftingInput containerIn) 
	{
		ItemStack oilStack = ItemStack.EMPTY,
				weaponStack = ItemStack.EMPTY;
		
		for(int i = 0; i < containerIn.width(); i++)
		{
			for(int j = 0; j < containerIn.height(); j++)
			{
				ItemStack stack = containerIn.getItem(j * containerIn.width() + i);
				// Oil found
				if(stack.is(ModItems.WEAPON_OIL.get()))
				{
					// Aleady have an oil; not a valid recipe
					if(!oilStack.isEmpty())
						return ItemStack.EMPTY;
					oilStack = stack;
				}
				// Oilable weapon found
				else if(stack.is(ModItemTags.OILABLE_WEAPONS))
				{
					// Aleady have an oilable weapon; not a valid recipe
					if(!weaponStack.isEmpty())
						return ItemStack.EMPTY;
					weaponStack = stack;
				}
				// Other item found; not a valid recipe
				else if(!stack.isEmpty())
					return ItemStack.EMPTY;
			}
		}
		if(!oilStack.isEmpty() && !weaponStack.isEmpty())
		{
			ItemStack resultStack = weaponStack.copy();
			OilEffect effect = OilHelper.getOilFromStack(oilStack);
			Optional<IOilHandler> handler = Optional.ofNullable(ModCapabilities.getOil(resultStack));
			if(effect != OilEffects.NONE.get() && handler.isPresent())
			{
				IOilHandler oilHandler = handler.get();
				if(effect == OilEffects.POTION.get())
				{
					Optional<Holder<Potion>> potion = OilHelper.getPotionFromStack(oilStack);
					if(potion.isEmpty())
						return ItemStack.EMPTY;
					oilHandler.setPotion(potion.get(), oilStack);
				}
				else
					oilHandler.setEffect(effect, oilStack);
				return resultStack.copy();
			}
		}
		return ItemStack.EMPTY;
	}

	// Crafting need to have a minimum of 2 slots

	@Override
	public RecipeSerializer<ApplyOilRecipe> getSerializer() 
	{
		return ModRecipeSerializers.APPLY_OIL.get();
	}
}
