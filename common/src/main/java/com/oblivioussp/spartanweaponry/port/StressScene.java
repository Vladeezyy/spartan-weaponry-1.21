package com.oblivioussp.spartanweaponry.port;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.capability.IQuiverItemHandler;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowBaseEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.BoltEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.ThrowingWeaponEntity;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.init.ModEnchantments;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.item.ArrowBaseItem;
import com.oblivioussp.spartanweaponry.item.BoltItem;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.QuiverBaseItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.network.QuiverAccessPacket;
import com.oblivioussp.spartanweaponry.network.QuiverButtonPacket;
import com.oblivioussp.spartanweaponry.network.QuiverPrioritySlotPacket;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import com.oblivioussp.spartanweaponry.util.LegacyItems;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.OilHelper;
import com.oblivioussp.spartanweaponry.util.PotionHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Functional and stress tests, inert unless the {@code spartanweaponry.stress} system property is set
 * ({@code ./gradlew runStress}, {@code tools/stresstest.sh}). World {@code sw_stress}: weapon data in the inventory, a
 * chest and projectiles in the world, saved and read again after leaving and rejoining; items saved by 1.20.1 (their
 * NBT through the data fixer); quiver packet spam with bad values; 400 armor stands with weapons on screen (FPS); 40
 * skeletons with longbows and 40 armed zombies against iron golems (TPS); 600 projectiles at once. Results:
 * {@code [stress] CHECK} / {@code [stress] STAT} lines.
 */
public final class StressScene
{
	private static final boolean ENABLED = System.getProperty("spartanweaponry.stress") != null;
	private static final String WORLD = "sw_stress";
	private static final List<Pair<Integer, Consumer<Minecraft>>> FIRST = new ArrayList<>();
	private static final List<Pair<Integer, Consumer<Minecraft>>> SECOND = new ArrayList<>();
	private static int phase = 1;
	private static boolean opened;
	private static int tick;
	private static int failures;
	private static BlockPos stage;
	private static final List<Integer> mobs = new ArrayList<>();

	static
	{
		// Phase 1: weapon data in the inventory, a chest and the world, then leave
		FIRST.add(Pair.of(40, mc -> server(mc, StressScene::setup)));
		FIRST.add(Pair.of(60, mc -> server(mc, StressScene::writeData)));
		FIRST.add(Pair.of(120, mc -> server(mc, StressScene::checkProjectilesLanded)));
		FIRST.add(Pair.of(130, mc -> {
			Log.info("[stress] leaving the world");
			mc.level.disconnect(net.minecraft.network.chat.Component.empty());
			mc.disconnect(new TitleScreen(), false);
			opened = false;
			phase = 2;
			tick = 0;
		}));
		// Phase 2: rejoined
		// Issue #165 of the original: heavy crossbow bolts turned black after rejoining
		SECOND.add(Pair.of(30, mc -> server(mc, p -> p.teleportTo(p.level(), stage.getX() + 0.5, stage.getY() + 1.5, stage.getZ() + 2.5, java.util.Set.of(), 0.0F, 35.0F, true))));
		SECOND.add(Pair.of(42, mc -> { setHudHidden(mc, true); shot(mc, "rejoined_projectiles"); }));
		SECOND.add(Pair.of(44, mc -> setHudHidden(mc, false)));
		SECOND.add(Pair.of(46, mc -> server(mc, StressScene::checkData)));
		SECOND.add(Pair.of(48, mc -> server(mc, StressScene::legacyItems)));
		SECOND.add(Pair.of(50, mc -> server(mc, StressScene::prepareSpam)));
		SECOND.add(Pair.of(60, mc -> {
			for(int i = 0; i < 200; i++)
			{
				NetworkHandler.sendPacketToServer(new QuiverPrioritySlotPacket(i % 2 == 0 ? -128 + i : 127 - i));
				NetworkHandler.sendPacketToServer(new QuiverButtonPacket(i % 2 == 0));
				NetworkHandler.sendPacketToServer(new QuiverAccessPacket());
			}
		}));
		SECOND.add(Pair.of(80, mc -> {
			// 1.20.1 (and the port before the fix) disconnected the client when a quiver screen arrived before the
			// client's inventory had the quiver
			check("the client is still connected after the quiver packet spam", mc.player != null && mc.getConnection() != null && mc.level != null, mc.gui.screen());
			if(mc.gui.screen() != null)
				mc.player.closeContainer();
			server(mc, StressScene::checkSpam);
		}));
		SECOND.add(Pair.of(90, mc -> server(mc, StressScene::renderStress)));
		SECOND.add(Pair.of(170, mc -> fps(mc, "400 armor stands with weapons (half oiled) + 160 item frames")));
		SECOND.add(Pair.of(230, mc -> { fps(mc, "400 armor stands with weapons (half oiled) + 160 item frames"); shot(mc, "render"); }));
		// The same with vanilla swords: the baseline for the FPS above
		SECOND.add(Pair.of(235, mc -> server(mc, StressScene::vanillaBaseline)));
		SECOND.add(Pair.of(310, mc -> fps(mc, "the same 400 armor stands with vanilla swords (baseline)")));
		SECOND.add(Pair.of(320, mc -> server(mc, StressScene::combatStress)));
		SECOND.add(Pair.of(720, mc -> { fps(mc, "40 skeletons with longbows + 40 armed zombies vs 30 golems"); shot(mc, "combat"); }));
		SECOND.add(Pair.of(920, mc -> server(mc, StressScene::checkCombatStress)));
		SECOND.add(Pair.of(930, mc -> server(mc, StressScene::projectileStress)));
		SECOND.add(Pair.of(950, mc -> { fps(mc, "600 projectiles in the air"); shot(mc, "projectiles"); }));
		SECOND.add(Pair.of(1130, mc -> server(mc, StressScene::checkProjectileStress)));
		SECOND.add(Pair.of(1150, mc -> {
			Log.info("[stress] DONE, failed checks: " + failures);
			mc.stop();
			phase = 3;
		}));
	}

	// The loader modules call this after every client tick (port/*SceneHooks)
	public static void onTick()
	{
		if(!ENABLED)
			return;
		Minecraft mc = Minecraft.getInstance();
		if(!opened && mc.gui.screen() instanceof AccessibilityOnboardingScreen)
		{
			mc.options.onboardAccessibility = false;
			mc.options.save();
			mc.gui.setScreen(new TitleScreen());
			return;
		}
		// NeoForge 26.2: load warnings (e.g. another mod's deprecated logoFile) open LoadingErrorScreen before the menu
		if(!opened && mc.gui.screen() != null && mc.gui.screen().getClass().getSimpleName().equals("LoadingErrorScreen"))
		{
			Log.info("[scene] NeoForge load warnings screen, going on to the main menu");
			mc.gui.setScreen(new TitleScreen());
			return;
		}
		if(!opened && mc.gui.screen() instanceof TitleScreen)
		{
			opened = true;
			if(mc.getLevelSource().levelExists(WORLD))
				mc.createWorldOpenFlows().openWorld(WORLD, () -> mc.gui.setScreen(new TitleScreen()));
			else
			{
				LevelSettings settings = new LevelSettings(WORLD, GameType.CREATIVE, new LevelSettings.DifficultySettings(Difficulty.NORMAL, false, false), true, WorldDataConfiguration.DEFAULT);
				mc.createWorldOpenFlows().createFreshLevel(WORLD, settings, new WorldOptions(4321L, false, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
			}
			return;
		}
		if(mc.player == null || mc.getSingleplayerServer() == null)
			return;
		mc.options.pauseOnLostFocus = false;
		mc.getTutorial().setStep(net.minecraft.client.tutorial.TutorialSteps.NONE);
		mc.gui.toastManager().clear();
		if(mc.gui.screen() instanceof PauseScreen)
			mc.gui.setScreen(null);
		tick++;
		for(Pair<Integer, Consumer<Minecraft>> step : phase == 1 ? FIRST : phase == 2 ? SECOND : List.<Pair<Integer, Consumer<Minecraft>>>of())
		{
			if(step.getFirst() == tick)
			{
				Log.info("[stress] phase " + phase + " tick " + tick);
				try
				{
					step.getSecond().accept(mc);
				}
				catch(RuntimeException e)
				{
					check("step at tick " + tick + " ran", false, e);
					Log.error(e);
				}
			}
		}
	}

	private static void server(Minecraft mc, Consumer<ServerPlayer> action)
	{
		MinecraftServer server = mc.getSingleplayerServer();
		server.execute(() ->
		{
			try
			{
				action.accept(server.getPlayerList().getPlayer(mc.player.getUUID()));
			}
			catch(RuntimeException e)
			{
				check("server step ran", false, e);
				Log.error(e);
			}
		});
	}

	private static void check(String name, boolean ok, Object detail)
	{
		if(!ok)
			failures++;
		Log.info("[stress] CHECK " + (ok ? "OK   " : "FAIL ") + name + " (" + detail + ")");
	}

	private static void stat(String name, Object value)
	{
		Log.info("[stress] STAT " + name + ": " + value);
	}

	private static void shot(Minecraft mc, String name)
	{
		Screenshot.grab(mc.gameDirectory, "stress_" + name + ".png", mc.gameRenderer.mainRenderTarget(), 1, c -> Log.info("[stress] " + c.getString()));
	}

	private static void fps(Minecraft mc, String what)
	{
		MinecraftServer server = mc.getSingleplayerServer();
		stat("client FPS with " + what, mc.getFps());
		stat("server tick ms with " + what, String.format("%.2f", server.getAverageTickTimeNanos() / 1.0E6));
	}

	private static void setup(ServerPlayer player)
	{
		MinecraftServer server = player.level().getServer();
		ServerLevel level = player.level();
		player.setGameMode(GameType.CREATIVE);
		server.overworld().getGameRules().set(GameRules.SPAWN_MOBS, false, server);
		server.overworld().getGameRules().set(GameRules.ADVANCE_TIME, false, server);
		server.overworld().getGameRules().set(GameRules.ADVANCE_WEATHER, false, server);
		server.overworld().getGameRules().set(GameRules.MOB_GRIEFING, false, server);
		level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack().withSuppressedOutput(), "time set 6000");
		BlockPos spawn = level.getRespawnData().pos();
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());
		stage = new BlockPos(spawn.getX(), Math.max(y + 40, 140), spawn.getZ());
		for(int dx = -40; dx <= 40; dx++)
			for(int dz = -20; dz <= 60; dz++)
				level.setBlockAndUpdate(stage.offset(dx, -1, dz), Blocks.POLISHED_ANDESITE.defaultBlockState());
		player.teleportTo(level, stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, 10.0F, true);
	}

	// ------------------------------------------------------------------------------------------------ save data

	private static ItemStack enchanted(Item item, net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key, int level, ServerPlayer player)
	{
		ItemStack stack = new ItemStack(item);
		var enchantments = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		ItemEnchantments.Mutable ench = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		ench.set(enchantments.getOrThrow(key), level);
		stack.set(DataComponents.ENCHANTMENTS, ench.toImmutable());
		return stack;
	}

	/** The test items: the same in the inventory (slots 0..n) and in the chest */
	private static List<ItemStack> testItems(ServerPlayer player)
	{
		List<ItemStack> items = new ArrayList<>();
		ItemStack oiled = new ItemStack(ModItems.KATANAS.iron.get());
		ModCapabilities.getOil(oiled).setEffect(OilEffects.UNDEAD.get(), OilHelper.makeOilStack(OilEffects.UNDEAD.get()));
		items.add(oiled);
		ItemStack potionOiled = new ItemStack(ModItems.LONGSWORDS.diamond.get());
		ModCapabilities.getOil(potionOiled).setPotion(Potions.STRONG_POISON, OilHelper.makePotionOilStack(Potions.STRONG_POISON));
		items.add(potionOiled);
		ItemStack crossbow = new ItemStack(ModItems.HEAVY_CROSSBOWS.diamond.get());
		HeavyCrossbowItem.setLoadedBolt(crossbow, new ItemStack(ModItems.DIAMOND_BOLT.get()), 1);
		ItemNbt.update(crossbow, nbt -> nbt.putBoolean(HeavyCrossbowItem.NBT_CHARGED, true));
		items.add(crossbow);
		ItemStack knives = ((ThrowingWeaponItem)ModItems.THROWING_KNIVES.steel.get()).makeTabStack();
		ItemNbt.update(knives, nbt -> nbt.putInt(ThrowingWeaponItem.NBT_AMMO_USED, 2));
		items.add(knives);
		items.add(enchanted(ModItems.JAVELINS.netherite.get(), ModEnchantments.LUCKY_THROW, 3, player));
		ItemStack quiver = new ItemStack(ModItems.LARGE_ARROW_QUIVER.get());
		IQuiverItemHandler handler = ModCapabilities.getQuiver(quiver);
		handler.insertItem(0, new ItemStack(ModItems.DIAMOND_ARROW.get(), 64), false);
		handler.insertItem(3, PotionHelper.setPotion(new ItemStack(ModItems.TIPPED_IRON_ARROW.get(), 32), Potions.POISON), false);
		items.add(quiver);
		items.add(PotionHelper.setPotion(new ItemStack(ModItems.TIPPED_BOLT.get(), 16), Potions.HARMING));
		items.add(OilHelper.makePotionOilStack(Potions.LONG_SLOWNESS));
		return items;
	}

	private static void writeData(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.getInventory().clearContent();
		List<ItemStack> items = testItems(player);
		for(int i = 0; i < items.size(); i++)
			player.getInventory().setItem(i, items.get(i));
		BlockPos chestPos = stage.offset(3, 0, 3);
		level.setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState());
		ChestBlockEntity chest = (ChestBlockEntity)level.getBlockEntity(chestPos);
		for(int i = 0; i < items.size(); i++)
			chest.setItem(i, testItems(player).get(i));
		// Projectiles that land and stay in the world: a thrown javelin, a diamond bolt, a tipped arrow
		ItemStack javelin = enchanted(ModItems.JAVELINS.gold.get(), ModEnchantments.LUCKY_THROW, 2, player);
		ThrowingWeaponEntity thrown = ((ThrowingWeaponItem)javelin.getItem()).createThrowingWeaponEntity(level, player, javelin, 20);
		thrown.setWeapon(javelin);
		launch(level, thrown, stage.offset(-4, 3, 6));
		BoltEntity bolt = ((BoltItem)ModItems.DIAMOND_BOLT.get()).createBolt(level, new ItemStack(ModItems.DIAMOND_BOLT.get()), player);
		launch(level, bolt, stage.offset(0, 3, 6));
		ItemStack tipped = PotionHelper.setPotion(new ItemStack(ModItems.TIPPED_IRON_ARROW.get()), Potions.POISON);
		AbstractArrow arrow = ((ArrowBaseItem)tipped.getItem()).createArrow(level, tipped, player, new ItemStack(ModItems.LONGBOWS.iron.get()));
		launch(level, arrow, stage.offset(4, 3, 6));
	}

	private static void launch(ServerLevel level, AbstractArrow projectile, BlockPos at)
	{
		projectile.setPos(at.getX() + 0.5, at.getY(), at.getZ() + 0.5);
		projectile.setDeltaMovement(0.0, -1.0, 0.0);
		projectile.pickup = AbstractArrow.Pickup.ALLOWED;
		level.addFreshEntity(projectile);
	}

	/**
	 * The inventory and the landed projectiles' items before leaving (throwing weapons get a UUID in the inventory), as
	 * saved data: the stacks themselves would keep the registry entries (enchantments, potions) of the old world, which
	 * vanilla compares by identity (NeoForge by key) and cannot save with the new registries.
	 */
	private static final List<net.minecraft.nbt.Tag> savedInventory = new ArrayList<>();
	private static final List<net.minecraft.nbt.Tag> savedProjectiles = new ArrayList<>();

	private static net.minecraft.nbt.Tag saved(ServerPlayer player, ItemStack stack)
	{
		return stack.isEmpty() ? net.minecraft.nbt.EndTag.INSTANCE : ItemStack.CODEC.encodeStart(player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), stack).getOrThrow();
	}

	private static void checkProjectilesLanded(ServerPlayer player)
	{
		List<AbstractArrow> landed = player.level().getEntitiesOfClass(AbstractArrow.class, new AABB(stage.offset(0, 0, 6)).inflate(8));
		check("three projectiles in the ground before leaving", landed.size() == 3, landed);
		savedInventory.clear();
		for(int i = 0; i < 8; i++)
			savedInventory.add(saved(player, player.getInventory().getItem(i)));
		savedProjectiles.clear();
		landed.stream().filter(e -> e instanceof ThrowingWeaponEntity).forEach(e -> savedProjectiles.add(saved(player, ((ThrowingWeaponEntity)e).getWeaponItem())));
	}

	private static boolean sameItems(ServerPlayer player, List<net.minecraft.nbt.Tag> expected, java.util.function.IntFunction<ItemStack> actual, StringBuilder detail)
	{
		boolean ok = true;
		for(int i = 0; i < expected.size(); i++)
		{
			ItemStack a = actual.apply(i);
			if(!expected.get(i).equals(saved(player, a)))
			{
				ok = false;
				detail.append(i).append(": ").append(a).append(" ").append(a.getComponentsPatch()).append(", expected ").append(expected.get(i)).append("; ");
			}
		}
		return ok;
	}

	private static void checkData(ServerPlayer player)
	{
		ServerLevel level = player.level();
		List<ItemStack> expected = testItems(player);
		StringBuilder detail = new StringBuilder();
		check("inventory after rejoining: oils, loaded bolt, ammo, enchantments, quiver contents, tipped bolts, potion oil", sameItems(player, savedInventory, player.getInventory()::getItem, detail), detail.length() == 0 ? savedInventory.size() + " stacks" : detail);
		ChestBlockEntity chest = (ChestBlockEntity)level.getBlockEntity(stage.offset(3, 0, 3));
		StringBuilder chestDetail = new StringBuilder();
		check("chest after rejoining", chest != null && sameItems(player, expected.stream().map(stack -> saved(player, stack)).toList(), chest::getItem, chestDetail), chestDetail.length() == 0 ? "same" : chestDetail);
		IOilHandler oil = ModCapabilities.getOil(player.getInventory().getItem(0));
		check("oil handler reads the saved oil", oil != null && oil.isOiled() && oil.getUsesLeft() == OilEffects.UNDEAD.get().getMaxUses(), oil == null ? "none" : oil.getEffect() + " " + oil.getUsesLeft());
		check("loaded bolt survives", HeavyCrossbowItem.getLoadedBolt(player.getInventory().getItem(2), player.registryAccess()).is(ModItems.DIAMOND_BOLT.get()), HeavyCrossbowItem.getLoadedBolt(player.getInventory().getItem(2), player.registryAccess()));
		List<AbstractArrow> landed = level.getEntitiesOfClass(AbstractArrow.class, new AABB(stage.offset(0, 0, 6)).inflate(8));
		ThrowingWeaponEntity javelin = landed.stream().filter(e -> e instanceof ThrowingWeaponEntity).map(e -> (ThrowingWeaponEntity)e).findFirst().orElse(null);
		BoltEntity bolt = landed.stream().filter(e -> e instanceof BoltEntity).map(e -> (BoltEntity)e).findFirst().orElse(null);
		ArrowBaseEntity arrow = landed.stream().filter(e -> e instanceof ArrowBaseEntity).map(e -> (ArrowBaseEntity)e).findFirst().orElse(null);
		// 1.20.1 strips the enchantments of a thrown weapon that lands (anti-dupe); the rest of the item is kept
		check("thrown javelin saved with its weapon item", javelin != null && savedProjectiles.size() == 1 && saved(player, javelin.getWeaponItem()).equals(savedProjectiles.get(0)), javelin == null ? "gone" : javelin.getWeaponItem().getComponentsPatch());
		check("bolt in the ground saved (1.20.1 lost it)", bolt != null && bolt.getPickupItemStackOrigin().is(ModItems.DIAMOND_BOLT.get()), bolt == null ? "gone" : bolt.getPickupItemStackOrigin());
		check("tipped arrow saved with its potion", arrow != null && PotionHelper.getPotion(arrow.getPickupItemStackOrigin()).map(p -> p.is(Potions.POISON)).orElse(false), arrow == null ? "gone" : arrow.getPickupItemStackOrigin());
		if(javelin != null)
		{
			int before = player.getInventory().countItem(ModItems.JAVELINS.gold.get());
			javelin.playerTouch(player);
			check("the saved javelin can be picked up", !javelin.isAlive() && player.getInventory().countItem(ModItems.JAVELINS.gold.get()) == before + 1, before + " -> " + player.getInventory().countItem(ModItems.JAVELINS.gold.get()));
		}
	}

	/** Items as 1.20.1 saved them ({id, Count, tag}), read through the data fixer (old worlds, old chests) */
	private static void legacyItems(ServerPlayer player)
	{
		var registries = player.registryAccess();
		CompoundTag oil = new CompoundTag();
		oil.putString("Effect", "spartanweaponry:undead");
		oil.putInt("UsesLeft", 7);
		CompoundTag katanaTag = new CompoundTag();
		katanaTag.put("Oil", oil);
		katanaTag.putInt("Damage", 12);
		ItemStack katana = LegacyItems.parse(old("spartanweaponry:iron_katana", 1, katanaTag), registries);
		IOilHandler handler = ModCapabilities.getOil(katana);
		check("1.20.1 oiled katana: oil and uses kept, damage moved to the component", handler != null && handler.isOiled() && handler.getUsesLeft() == 7 && katana.getDamageValue() == 12, katana + " " + katana.getComponentsPatch());

		CompoundTag potionOil = new CompoundTag();
		potionOil.putString("Effect", "spartanweaponry:potion");
		potionOil.putString("Potion", "minecraft:strong_poison");
		potionOil.putInt("UsesLeft", 5);
		CompoundTag swordTag = new CompoundTag();
		swordTag.put("Oil", potionOil);
		ItemStack sword = LegacyItems.parse(old("spartanweaponry:iron_longsword", 1, swordTag), registries);
		IOilHandler swordOil = ModCapabilities.getOil(sword);
		check("1.20.1 potion oil on a longsword", swordOil != null && swordOil.getPotion().map(p -> p.is(Potions.STRONG_POISON)).orElse(false), sword.getComponentsPatch());

		CompoundTag tippedTag = new CompoundTag();
		tippedTag.putString("Potion", "minecraft:long_poison");
		ItemStack tipped = LegacyItems.parse(old("spartanweaponry:tipped_diamond_bolt", 12, tippedTag), registries);
		check("1.20.1 tipped bolt keeps its potion", tipped.getCount() == 12 && PotionHelper.getPotion(tipped).map(p -> p.is(Potions.LONG_POISON)).orElse(false), tipped.getComponentsPatch());

		CompoundTag knifeTag = new CompoundTag();
		knifeTag.putInt("AmmoUsed", 3);
		knifeTag.putBoolean("Original", true);
		ItemStack knives = LegacyItems.parse(old("spartanweaponry:iron_throwing_knife", 1, knifeTag), registries);
		check("1.20.1 throwing knives keep the used ammo", ItemNbt.get(knives).getIntOr(ThrowingWeaponItem.NBT_AMMO_USED, 0) == 3, knives.getComponentsPatch());

		CompoundTag crossbowTag = new CompoundTag();
		crossbowTag.putBoolean("Charged", true);
		crossbowTag.put("Projectile", old("spartanweaponry:netherite_bolt", 1, null));
		ItemStack crossbow = LegacyItems.parse(old("spartanweaponry:iron_heavy_crossbow", 1, crossbowTag), registries);
		check("1.20.1 loaded heavy crossbow keeps its bolt", HeavyCrossbowItem.getLoadedBolt(crossbow, registries).is(ModItems.NETHERITE_BOLT.get()), crossbow.getComponentsPatch());

		ListTag ammo = new ListTag();
		CompoundTag a = old("spartanweaponry:iron_arrow", 40, null);
		a.putInt("Slot", 0);
		ammo.add(a);
		CompoundTag b = old("spartanweaponry:tipped_iron_arrow", 8, tippedTag);
		b.putInt("Slot", 2);
		ammo.add(b);
		CompoundTag ammoTag = new CompoundTag();
		ammoTag.put("Items", ammo);
		ammoTag.putInt("Size", 6);
		CompoundTag quiverTag = new CompoundTag();
		quiverTag.put("Ammo", ammoTag);
		ItemStack quiver = LegacyItems.parse(old("spartanweaponry:medium_arrow_quiver", 1, quiverTag), registries);
		QuiverBaseItem.migrateLegacyContents(quiver, registries);
		IQuiverItemHandler handler2 = ModCapabilities.getQuiver(quiver);
		boolean quiverOk = handler2 != null && handler2.getStackInSlot(0).is(ModItems.IRON_ARROW.get()) && handler2.getStackInSlot(0).getCount() == 40
				&& handler2.getStackInSlot(2).getCount() == 8 && PotionHelper.getPotion(handler2.getStackInSlot(2)).map(p -> p.is(Potions.LONG_POISON)).orElse(false)
				&& !ItemNbt.contains(quiver, QuiverBaseItem.NBT_AMMO);
		check("1.20.1 quiver contents move to the quiver_contents component", quiverOk, quiver.getComponentsPatch());
	}

	private static CompoundTag old(String id, int count, CompoundTag tag)
	{
		CompoundTag stack = new CompoundTag();
		stack.putString("id", id);
		stack.putByte("Count", (byte)count);
		if(tag != null)
			stack.put("tag", tag.copy());
		return stack;
	}

	// ------------------------------------------------------------------------------------------------ spam

	private static void prepareSpam(ServerPlayer player)
	{
		player.getInventory().clearContent();
		ItemStack quiver = new ItemStack(ModItems.HUGE_ARROW_QUIVER.get());
		ModCapabilities.getQuiver(quiver).insertItem(0, new ItemStack(ModItems.IRON_ARROW.get(), 64), false);
		player.getInventory().setItem(0, quiver);
		player.getInventory().setSelectedSlot(0);
	}

	private static void checkSpam(ServerPlayer player)
	{
		ItemStack quiver = player.getInventory().getItem(0);
		IQuiverItemHandler handler = ModCapabilities.getQuiver(quiver);
		int arrows = 0;
		for(int i = 0; i < handler.getSlots(); i++)
			arrows += handler.getStackInSlot(i).getCount();
		int priority = ItemNbt.get(quiver).getIntOr(QuiverBaseItem.NBT_PROIRITY_SLOT, 0);
		check("600 quiver packets with bad slot numbers: no crash, arrows kept, priority slot in range", player.isAlive() && arrows == 64 && priority >= 0 && priority < handler.getSlots(), "arrows " + arrows + ", priority slot " + priority + "/" + handler.getSlots());
	}

	// ------------------------------------------------------------------------------------------------ load

	private static void renderStress(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.getInventory().clearContent();
		List<Item> weapons = new ArrayList<>();
		for(var holder : ModItems.REGISTRY.getEntries())
		{
			Item item = holder.get();
			if(item.getDefaultInstance().is(com.oblivioussp.spartanweaponry.api.tags.ModItemTags.OILABLE_WEAPONS))
				weapons.add(item);
		}
		int n = 0;
		for(int x = 0; x < 20; x++)
			for(int z = 0; z < 20; z++)
			{
				ArmorStand stand = EntityTypes.ARMOR_STAND.create(level, EntitySpawnReason.COMMAND);
				stand.snapTo(stage.getX() - 19 + x * 2 + 0.5, stage.getY(), stage.getZ() + 6 + z * 2 + 0.5, 180.0F, 0.0F);
				stand.setShowArms(true);
				stand.setNoBasePlate(true);
				ItemStack weapon = new ItemStack(weapons.get(n++ % weapons.size()));
				if(n % 2 == 0)
					ModCapabilities.getOil(weapon).setEffect(OilEffects.CREEPER.get(), OilHelper.makeOilStack(OilEffects.CREEPER.get()));
				stand.setItemSlot(EquipmentSlot.MAINHAND, weapon);
				stand.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(weapons.get((n * 7) % weapons.size())));
				level.addFreshEntity(stand);
				mobs.add(stand.getId());
			}
		for(int i = 0; i < 160; i++)
		{
			BlockPos framePos = stage.offset(-20 + i % 40, 1 + i / 40, 50);
			level.setBlockAndUpdate(framePos.south(), Blocks.SPRUCE_PLANKS.defaultBlockState());
			ItemFrame frame = new ItemFrame(level, framePos, Direction.NORTH);
			frame.setItem(new ItemStack(weapons.get(i % weapons.size())), false);
			level.addFreshEntity(frame);
			mobs.add(frame.getId());
		}
		player.teleportTo(level, stage.getX() + 0.5, stage.getY() + 4, stage.getZ() - 4.5, java.util.Set.of(), 0.0F, 20.0F, true);
		stat("weapon kinds on the stands", weapons.size());
	}

	private static void vanillaBaseline(ServerPlayer player)
	{
		ServerLevel level = player.level();
		for(int id : mobs)
		{
			Entity e = level.getEntity(id);
			if(e instanceof ArmorStand stand)
			{
				stand.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
				stand.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.DIAMOND_AXE));
			}
			else if(e instanceof ItemFrame frame)
				frame.setItem(new ItemStack(Items.GOLDEN_SWORD), false);
		}
	}

	private static long combatStart;

	private static void combatStress(ServerPlayer player)
	{
		ServerLevel level = player.level();
		for(int id : mobs)
		{
			Entity e = level.getEntity(id);
			if(e != null)
				e.discard();
		}
		mobs.clear();
		player.setGameMode(GameType.SPECTATOR);
		player.teleportTo(level, stage.getX() + 0.5, stage.getY() + 12, stage.getZ() - 10.5, java.util.Set.of(), 0.0F, 35.0F, true);
		List<Item> melee = List.of(ModItems.KATANAS.iron.get(), ModItems.GREATSWORDS.diamond.get(), ModItems.WARHAMMERS.iron.get(), ModItems.SPEARS.gold.get(),
				ModItems.HALBERDS.iron.get(), ModItems.SCYTHES.iron.get(), ModItems.BATTLEAXES.netherite.get(), ModItems.DAGGERS.stone.get());
		for(int i = 0; i < 40; i++)
		{
			Skeleton skeleton = EntityTypes.SKELETON.spawn(level, stage.offset(-20 + i, 0, 0), EntitySpawnReason.COMMAND);
			skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(i % 2 == 0 ? ModItems.LONGBOWS.iron.get() : ModItems.LONGBOWS.wood.get()));
			skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
			skeleton.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION));
			mobs.add(skeleton.getId());
			Zombie zombie = EntityTypes.ZOMBIE.spawn(level, stage.offset(-20 + i, 0, 4), EntitySpawnReason.COMMAND);
			zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(melee.get(i % melee.size())));
			zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
			zombie.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION));
			mobs.add(zombie.getId());
		}
		for(int i = 0; i < 30; i++)
		{
			IronGolem golem = EntityTypes.IRON_GOLEM.spawn(level, stage.offset(-15 + i, 0, 20), EntitySpawnReason.COMMAND);
			mobs.add(golem.getId());
		}
		for(int id : mobs)
			if(level.getEntity(id) instanceof Mob mob && !(mob instanceof IronGolem))
				mob.setTarget(level.getEntitiesOfClass(IronGolem.class, new AABB(stage).inflate(60)).stream().findAny().orElse(null));
		combatStart = System.nanoTime();
	}

	private static void checkCombatStress(ServerPlayer player)
	{
		ServerLevel level = player.level();
		double tickMs = player.level().getServer().getAverageTickTimeNanos() / 1.0E6;
		long arrows = level.getEntitiesOfClass(AbstractArrow.class, new AABB(stage).inflate(80)).size();
		long alive = mobs.stream().filter(id -> level.getEntity(id) != null && level.getEntity(id).isAlive()).count();
		stat("mobs alive after 30 s (of 110)", alive);
		stat("arrows in the world", arrows);
		stat("average server tick ms", String.format("%.2f", tickMs));
		check("combat stress: server keeps 20 TPS (tick < 50 ms)", tickMs < 50.0, String.format("%.2f ms", tickMs));
		check("combat stress: the skeletons with longbows shoot", arrows > 0 || alive < 110, arrows + " arrows");
		for(int id : mobs)
		{
			Entity e = level.getEntity(id);
			if(e != null)
				e.discard();
		}
		mobs.clear();
		level.getEntitiesOfClass(AbstractArrow.class, new AABB(stage).inflate(80)).forEach(Entity::discard);
		level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new AABB(stage).inflate(80)).forEach(Entity::discard);
	}

	private static void projectileStress(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.setGameMode(GameType.CREATIVE);
		var random = level.getRandom();
		List<Item> throwing = List.of(ModItems.THROWING_KNIVES.iron.get(), ModItems.TOMAHAWKS.diamond.get(), ModItems.JAVELINS.gold.get(), ModItems.BOOMERANGS.wood.get());
		for(int i = 0; i < 200; i++)
		{
			ItemStack weapon = new ItemStack(throwing.get(i % throwing.size()));
			ThrowingWeaponEntity thrown = ((ThrowingWeaponItem)weapon.getItem()).createThrowingWeaponEntity(level, player, weapon, 20);
			thrown.setWeapon(weapon);
			thrown.setPos(stage.getX() + random.nextInt(40) - 20, stage.getY() + 10 + random.nextInt(10), stage.getZ() + random.nextInt(40));
			thrown.setDeltaMovement(random.nextGaussian() * 0.5, 0.5, random.nextGaussian() * 0.5);
			thrown.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			level.addFreshEntity(thrown);
		}
		for(int i = 0; i < 400; i++)
		{
			ItemStack ammo = new ItemStack(i % 2 == 0 ? ModItems.IRON_ARROW.get() : ModItems.TIPPED_IRON_ARROW.get());
			if(i % 2 == 1)
				PotionHelper.setPotion(ammo, Potions.POISON);
			AbstractArrow arrow = i % 4 == 3 ? ((BoltItem)ModItems.BOLT.get()).createBolt(level, new ItemStack(ModItems.BOLT.get()), player)
					: ((ArrowBaseItem)ammo.getItem()).createArrow(level, ammo, player, new ItemStack(ModItems.LONGBOWS.iron.get()));
			arrow.setPos(stage.getX() + random.nextInt(40) - 20, stage.getY() + 10 + random.nextInt(10), stage.getZ() + random.nextInt(40));
			arrow.setDeltaMovement(random.nextGaussian() * 0.5, 1.0, random.nextGaussian() * 0.5);
			arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			level.addFreshEntity(arrow);
		}
		player.teleportTo(level, stage.getX() + 0.5, stage.getY() + 6, stage.getZ() - 12.5, java.util.Set.of(), 0.0F, 15.0F, true);
	}

	private static void checkProjectileStress(ServerPlayer player)
	{
		ServerLevel level = player.level();
		double tickMs = player.level().getServer().getAverageTickTimeNanos() / 1.0E6;
		List<AbstractArrow> all = level.getEntitiesOfClass(AbstractArrow.class, new AABB(stage).inflate(80));
		long flying = all.stream().filter(a -> a.getY() > stage.getY() + 1.0).count();
		stat("projectiles left in the world / still above the floor after 9 s", all.size() + " / " + flying);
		stat("average server tick ms", String.format("%.2f", tickMs));
		check("projectile stress: server keeps 20 TPS", tickMs < 50.0, String.format("%.2f ms", tickMs));
		all.forEach(Entity::discard);
	}

	/** 26.2: the HUD is hidden through Minecraft.gui.hud, which only toggles */
	private static void setHudHidden(net.minecraft.client.Minecraft mc, boolean hidden)
	{
		if(mc.gui.hud.isHidden() != hidden)
			mc.gui.hud.toggle();
	}
}
