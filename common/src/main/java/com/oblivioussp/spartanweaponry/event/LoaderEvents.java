package com.oblivioussp.spartanweaponry.event;

import net.minecraft.resources.ResourceKey;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;

/**
 * The game events the common handlers react to, with the method names of the NeoForge events they came from. The
 * NeoForge module fills them from its events, the Fabric module from its callbacks and mixins, and copies the results
 * back.
 */
public final class LoaderEvents
{
	private LoaderEvents() {}

	/** NeoForge LivingIncomingDamageEvent: after the invulnerability checks of LivingEntity.hurt, before the shield. */
	public static class IncomingDamage
	{
		private final LivingEntity entity;
		private final DamageSource source;
		private float amount;
		private boolean canceled;

		public IncomingDamage(LivingEntity entity, DamageSource source, float amount)
		{
			this.entity = entity;
			this.source = source;
			this.amount = amount;
		}

		public LivingEntity getEntity()
		{
			return entity;
		}

		public DamageSource getSource()
		{
			return source;
		}

		public float getAmount()
		{
			return amount;
		}

		public void setAmount(float amount)
		{
			this.amount = amount;
		}

		public boolean isCanceled()
		{
			return canceled;
		}

		public void setCanceled(boolean canceled)
		{
			this.canceled = canceled;
		}
	}

	/** NeoForge LivingEquipmentChangeEvent. */
	public record EquipmentChange(LivingEntity getEntity, EquipmentSlot getSlot, ItemStack getFrom, ItemStack getTo) {}

	/** NeoForge ItemEntityPickupEvent.Pre (the handler takes what it wants out of the item entity itself). */
	public record ItemPickup(Player getPlayer, ItemEntity getItemEntity) {}

	/** NeoForge LootTableLoadEvent; the pools are added by the loader. */
	public interface LootTableLoad
	{
		Identifier getName();

		/** 26.3: the registries of the load (a loot table reference is a holder now) */
		net.minecraft.core.HolderGetter.Provider registries();

		void addPool(LootPool pool);
	}


	/** NeoForge AnvilUpdateEvent. */
	public static class AnvilUpdate
	{
		private final ItemStack left, right;
		private ItemStack output = ItemStack.EMPTY;
		private long cost;
		private int materialCost;

		public AnvilUpdate(ItemStack left, ItemStack right)
		{
			this.left = left;
			this.right = right;
		}

		public ItemStack getLeft()
		{
			return left;
		}

		public ItemStack getRight()
		{
			return right;
		}

		public ItemStack getOutput()
		{
			return output;
		}

		public void setOutput(ItemStack output)
		{
			this.output = output;
		}

		public long getCost()
		{
			return cost;
		}

		public void setCost(long cost)
		{
			this.cost = cost;
		}

		public int getMaterialCost()
		{
			return materialCost;
		}

		public void setMaterialCost(int materialCost)
		{
			this.materialCost = materialCost;
		}
	}

	/** NeoForge PlayerInteractEvent.RightClickBlock; a cancellation result other than PASS cancels the click. */
	public static class RightClickBlock
	{
		private final Player player;
		private final InteractionHand hand;
		private final BlockPos pos;
		private InteractionResult result = InteractionResult.PASS;

		public RightClickBlock(Player player, InteractionHand hand, BlockPos pos)
		{
			this.player = player;
			this.hand = hand;
			this.pos = pos;
		}

		public Player getEntity()
		{
			return player;
		}

		public InteractionHand getHand()
		{
			return hand;
		}

		public Level getLevel()
		{
			return player.level();
		}

		public BlockPos getPos()
		{
			return pos;
		}

		public InteractionResult getCancellationResult()
		{
			return result;
		}

		public void setCancellationResult(InteractionResult result)
		{
			this.result = result;
		}
	}
}
