package com.oblivioussp.spartanweaponry.port;

import com.oblivioussp.spartanweaponry.api.tags.ModItemTags;

import com.oblivioussp.spartanweaponry.platform.Platform;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


import com.mojang.datafixers.util.Pair;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.OilEffects;
import com.oblivioussp.spartanweaponry.capability.IOilHandler;
import com.oblivioussp.spartanweaponry.capability.IQuiverItemHandler;
import com.oblivioussp.spartanweaponry.entity.projectile.ArrowExplosiveEntity;
import com.oblivioussp.spartanweaponry.entity.projectile.ThrowingWeaponEntity;
import com.oblivioussp.spartanweaponry.init.ModBlocks;
import com.oblivioussp.spartanweaponry.init.ModCapabilities;
import com.oblivioussp.spartanweaponry.init.ModCreativeTabs;
import com.oblivioussp.spartanweaponry.init.ModEnchantments;
import com.oblivioussp.spartanweaponry.init.ModItems;
import com.oblivioussp.spartanweaponry.init.ModMobEffects;
import com.oblivioussp.spartanweaponry.item.HeavyCrossbowItem;
import com.oblivioussp.spartanweaponry.item.ThrowingWeaponItem;
import com.oblivioussp.spartanweaponry.util.ItemNbt;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.OilHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Development scene, inert unless the {@code spartanweaponry.scene} system property is set ({@code ./gradlew runScene}):
 * creates the world {@code sw_scene}, shows every item (item frame walls, creative tab, tooltip, dropped items, heads,
 * quiver screens, first person bow / heavy crossbow / throwing), runs functional checks (real client input for the
 * ranged weapons, server side for the rest) and logs them as {@code [scene] CHECK}, saves
 * {@code run/screenshots/scene_*.png}, then quits. {@code -Dspartanweaponry.scene.lang=ru_ru} switches the language.
 */
public final class DevScene
{
	private static final boolean ENABLED = System.getProperty("spartanweaponry.scene") != null;
	private static final String LANG = System.getProperty("spartanweaponry.scene.lang", "en_us");
	private static final String WORLD = "sw_scene";
	private static final List<Pair<Integer, Consumer<Minecraft>>> STEPS = new ArrayList<>();
	private static boolean opened;
	private static int tick;
	private static int failures;
	private static BlockPos stage;
	private static int golemId;
	private static float golemHealth;
	private static int countBefore;

	static
	{
		at(40, mc -> server(mc, DevScene::setup));
		at(45, mc -> setHudHidden(mc, true));
		at(60, mc -> server(mc, DevScene::buildStage));
		at(160, mc -> server(mc, p -> look(p, wall(0), 0)));
		at(220, mc -> shot(mc, "wall_vanilla"));
		at(225, mc -> server(mc, p -> look(p, wall(1), 0)));
		at(285, mc -> shot(mc, "wall_modded"));
		at(290, mc -> server(mc, p -> look(p, wall(2), 0)));
		at(350, mc -> shot(mc, "wall_ammo"));
		// Creative tab and tooltip
		at(355, mc -> {
			setHudHidden(mc, false);
			mc.gui.toastManager().clear();
			mc.gui.setScreen(new CreativeModeInventoryScreen(mc.player, mc.player.connection.enabledFeatures(), true));
		});
		at(365, mc -> selectTab(mc, ModCreativeTabs.BASIC.get()));
		at(385, mc -> shot(mc, "creative_tab"));
		at(390, mc -> mc.gui.setScreen(new TooltipScreen(List.of(new ItemStack(ModItems.GREATSWORDS.iron.get()), oiled(ModItems.KATANAS.diamond.get()),
				new ItemStack(ModItems.HEAVY_CROSSBOWS.iron.get()), new ItemStack(ModItems.JAVELINS.iron.get())))));
		at(405, mc -> shot(mc, "tooltip"));
		// The oil coating layer: the same weapons plain (top) and oiled (bottom)
		at(406, mc -> mc.gui.setScreen(new ItemsScreen(List.of(ModItems.KATANAS.diamond.get(), ModItems.GREATSWORDS.iron.get(), ModItems.SPEARS.gold.get(), ModItems.BATTLEAXES.netherite.get()))));
		at(409, mc -> shot(mc, "oil_coating"));
		at(410, mc -> { mc.gui.setScreen(null); setHudHidden(mc, true); });
		// Dropped weapons (#168) and heads
		at(415, mc -> server(mc, DevScene::dropped));
		at(470, mc -> shot(mc, "dropped"));
		at(475, mc -> server(mc, DevScene::heads));
		at(520, mc -> shot(mc, "heads"));
		// First person: oiled katana
		at(525, mc -> server(mc, DevScene::firstPerson));
		at(560, mc -> { setHudHidden(mc, false); shot(mc, "first_person_oiled"); });
		// Longbow with real input: draw, screenshot, shoot the golem
		at(565, mc -> server(mc, DevScene::prepareLongbow));
		at(600, mc -> mc.options.keyUse.setDown(true));
		at(640, mc -> shot(mc, "longbow_draw"));
		at(642, mc -> mc.options.keyUse.setDown(false));
		at(690, mc -> server(mc, DevScene::checkLongbow));
		// Heavy crossbow: load (hold), aim (hold), fire
		at(695, mc -> server(mc, DevScene::prepareHeavyCrossbow));
		at(720, mc -> mc.options.keyUse.setDown(true));
		at(800, mc -> mc.options.keyUse.setDown(false));
		at(810, mc -> { shot(mc, "heavy_crossbow_loaded"); server(mc, DevScene::checkLoaded); });
		at(815, mc -> mc.options.keyUse.setDown(true));
		at(850, mc -> shot(mc, "heavy_crossbow_aim"));
		at(870, mc -> mc.options.keyUse.setDown(false));
		at(920, mc -> server(mc, DevScene::checkHeavyCrossbow));
		// Javelin thrown at the golem, picked up again
		at(925, mc -> server(mc, DevScene::prepareThrow));
		at(950, mc -> mc.options.keyUse.setDown(true));
		at(975, mc -> shot(mc, "throw_charge"));
		at(978, mc -> mc.options.keyUse.setDown(false));
		at(1040, mc -> server(mc, DevScene::checkThrowHit));
		at(1080, mc -> server(mc, DevScene::checkThrowPickup));
		// Issue #188: an arrow shot into the ground and picked up again gives back one arrow
		at(1085, mc -> server(mc, DevScene::prepareArrowPickup));
		at(1110, mc -> mc.options.keyUse.setDown(true));
		at(1135, mc -> mc.options.keyUse.setDown(false));
		at(1175, mc -> server(mc, DevScene::walkToArrow));
		at(1200, mc -> server(mc, DevScene::checkArrowPickup));
		// Quiver: arrows go to the off hand when a bow is selected; quiver screens (#193)
		at(1205, mc -> server(mc, DevScene::prepareQuiver));
		at(1215, mc -> server(mc, p -> p.getInventory().setSelectedSlot(1)));
		at(1235, mc -> server(mc, DevScene::checkQuiverOffhand));
		at(1240, mc -> server(mc, p -> p.getInventory().setSelectedSlot(0)));
		at(1250, mc -> { mc.options.keyUse.setDown(true); });
		at(1252, mc -> mc.options.keyUse.setDown(false));
		at(1270, mc -> shot(mc, "quiver_small"));
		at(1275, mc -> { if(mc.gui.screen() != null) mc.player.closeContainer(); server(mc, DevScene::hugeQuiver); });
		at(1290, mc -> mc.options.keyUse.setDown(true));
		at(1292, mc -> mc.options.keyUse.setDown(false));
		at(1310, mc -> { check("quiver screen opens", mc.gui.screen() != null && mc.gui.screen().getClass().getSimpleName().contains("Quiver"), mc.gui.screen()); shot(mc, "quiver_huge"); });
		at(1315, mc -> { if(mc.gui.screen() != null) mc.player.closeContainer(); setHudHidden(mc, true); });
		// Server side checks
		at(1320, mc -> server(mc, DevScene::oil));
		at(1325, mc -> server(mc, DevScene::prepareMelee));
		at(1360, mc -> server(mc, DevScene::melee));
		at(1390, mc -> server(mc, DevScene::armourPiercing));
		at(1395, mc -> server(mc, DevScene::reach));
		at(1398, mc -> server(mc, DevScene::reachBack));
		at(1405, mc -> server(mc, DevScene::prepareTwoHanded));
		at(1420, mc -> server(mc, DevScene::checkTwoHanded));
		at(1440, mc -> server(mc, DevScene::checkInfiniteFatigue));
		at(1445, mc -> server(mc, DevScene::parry));
		at(1450, mc -> server(mc, DevScene::decapitate));
		at(1455, mc -> server(mc, DevScene::luckyThrow));
		at(1460, mc -> server(mc, DevScene::prepareSkeleton));
		at(1660, mc -> server(mc, DevScene::checkSkeleton));
		at(1665, mc -> server(mc, DevScene::enderDisruption));
		at(1670, mc -> server(mc, DevScene::explosive));
		at(1740, mc -> server(mc, DevScene::checkExplosive));
		at(1745, mc -> server(mc, DevScene::throwDown));
		at(1760, mc -> server(mc, DevScene::throwDownFirstHit));
		at(1860, mc -> server(mc, DevScene::throwDownLater));
		at(1865, mc -> server(mc, DevScene::checkData));
		// Curios: a quiver in the back slot feeds the bow too
		at(1870, mc -> server(mc, DevScene::prepareCuriosQuiver));
		at(1875, mc -> server(mc, p -> p.getInventory().setSelectedSlot(1)));
		at(1895, mc -> server(mc, DevScene::checkCuriosQuiver));
		// Shoulder Surfing: a loaded heavy crossbow in the shoulder view (issue #192 of the original: crash)
		at(1910, mc -> server(mc, DevScene::prepareShoulderSurfing));
		at(1920, mc -> shoulderSurfing(mc, "SHOULDER_SURFING"));
		at(1960, mc -> { if(isShoulderSurfing()) { setHudHidden(mc, false); shot(mc, "shoulder_surfing"); } });
		at(1965, mc -> { shoulderSurfing(mc, "FIRST_PERSON"); setHudHidden(mc, true); });
		// Issue #162 of the original: a data pack with "replace": true can take traits away
		at(1970, mc -> server(mc, DevScene::traitDataPack));
		at(2050, mc -> server(mc, DevScene::checkTraitDataPack));
		at(2060, mc -> {
			Log.info("[scene] DONE, failed checks: " + failures);
			mc.stop();
		});
	}

	private static void at(int t, Consumer<Minecraft> action)
	{
		STEPS.add(Pair.of(t, action));
	}

	/** Puts the quiver into the back accessory slot (NeoForge: Curios; set by the loader's scene hooks). */
	public static java.util.function.BiPredicate<ServerPlayer, ItemStack> accessorySlot;

	// The loader modules call this after every client tick (port/*SceneHooks)
	public static void onTick()
	{
		if(!ENABLED)
			return;
		Minecraft mc = Minecraft.getInstance();
		if(!opened && mc.gui.screen() instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen)
		{
			mc.options.onboardAccessibility = false;
			mc.options.save();
			mc.gui.setScreen(new TitleScreen());
			return;
		}
		// The language switch reloads the resources; the world is created after that, not during it
		if(!opened && mc.gui.overlay() != null)
			return;
		// NeoForge 26.2: load warnings (e.g. another mod's deprecated logoFile) open LoadingErrorScreen before the menu
		if(!opened && mc.gui.screen() != null && mc.gui.screen().getClass().getSimpleName().equals("LoadingErrorScreen"))
		{
			Log.info("[scene] NeoForge load warnings screen, going on to the main menu");
			mc.gui.setScreen(new TitleScreen());
			return;
		}
		if(!opened && mc.gui.screen() instanceof TitleScreen)
		{
			if(!mc.options.languageCode.equals(LANG))
			{
				mc.options.languageCode = LANG;
				mc.getLanguageManager().setSelected(LANG);
				mc.options.save();
				mc.reloadResourcePacks();
				return;
			}
			opened = true;
			mc.getWindow().setWindowed(1600, 900);
			if(mc.getLevelSource().levelExists(WORLD))
				mc.createWorldOpenFlows().openWorld(WORLD, () -> mc.gui.setScreen(new TitleScreen()));
			else
			{
				LevelSettings settings = new LevelSettings(WORLD, GameType.CREATIVE, new LevelSettings.DifficultySettings(Difficulty.NORMAL, false, false), true, WorldDataConfiguration.DEFAULT);
				mc.createWorldOpenFlows().createFreshLevel(WORLD, settings, new WorldOptions(1234L, false, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
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
		for(Pair<Integer, Consumer<Minecraft>> step : STEPS)
		{
			if(step.getFirst() == tick)
			{
				Log.info("[scene] tick " + tick);
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

	static void check(String name, boolean ok, Object detail)
	{
		if(!ok)
			failures++;
		Log.info("[scene] CHECK " + (ok ? "OK   " : "FAIL ") + name + " (" + detail + ")");
	}

	private static void shot(Minecraft mc, String name)
	{
		Screenshot.grab(mc.gameDirectory, "scene_" + name + ".png", mc.gameRenderer.mainRenderTarget(), 1, c -> Log.info("[scene] " + c.getString()));
	}

	private static void selectTab(Minecraft mc, CreativeModeTab tab)
	{
		if(mc.gui.screen() instanceof CreativeModeInventoryScreen screen)
		{
			try
			{
				// Fabric API pages the tabs: a tab on another page is not selected without switching to its page first
				// (FabricCreativeInventoryScreen, by reflection: the common code does not see Fabric API)
				try
				{
					int page = (int)screen.getClass().getMethod("getPage", CreativeModeTab.class).invoke(screen, tab);
					screen.getClass().getMethod("switchToPage", int.class).invoke(screen, page);
				}
				catch(NoSuchMethodException e)
				{
					// NeoForge: its own pages follow the selected tab
				}
				// The private selectTab(CreativeModeTab), found by its signature: its name differs on a production (intermediary) client
				var select = java.util.Arrays.stream(CreativeModeInventoryScreen.class.getDeclaredMethods()).filter(m -> m.getReturnType() == void.class && java.util.Arrays.equals(m.getParameterTypes(), new Class<?>[] { net.minecraft.world.item.CreativeModeTab.class })).findFirst().orElseThrow(NoSuchMethodException::new);
				select.setAccessible(true);
				select.invoke(screen, tab);
			}
			catch(ReflectiveOperationException e)
			{
				Log.error("[scene] cannot select the creative tab: " + e);
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------ stage

	private static void setup(ServerPlayer player)
	{
		MinecraftServer server = player.level().getServer();
		player.setGameMode(GameType.CREATIVE);
		server.setDifficulty(Difficulty.NORMAL, true);
		server.overworld().getGameRules().set(GameRules.SPAWN_MOBS, false, server);
		server.overworld().getGameRules().set(GameRules.ADVANCE_TIME, false, server);
		server.overworld().getGameRules().set(GameRules.ADVANCE_WEATHER, false, server);
		server.overworld().getGameRules().set(GameRules.MOB_GRIEFING, false, server);
		player.level().getServer().getCommands().performPrefixedCommand(player.level().getServer().createCommandSourceStack().withSuppressedOutput(), "time set 6000");
		player.level().getServer().getCommands().performPrefixedCommand(player.level().getServer().createCommandSourceStack().withSuppressedOutput(), "weather clear");
	}

	/** Item frame wall n (0 vanilla materials, 1 modded materials, 2 ammo): its centre */
	private static BlockPos wall(int n)
	{
		return stage.offset(-60 + n * 60, 0, 0);
	}

	private static void look(ServerPlayer player, BlockPos wall, float pitch)
	{
		player.teleportTo(player.level(), wall.getX() + 0.5, wall.getY() + 2, wall.getZ() - 14.5, java.util.Set.of(), 0.0F, pitch, true);
	}

	private static void buildStage(ServerPlayer player)
	{
		ServerLevel level = player.level();
		BlockPos spawn = level.getRespawnData().pos();
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());
		// In the air, above the terrain (no overhang shading the walls)
		stage = new BlockPos(spawn.getX(), Math.max(y + 40, 140), spawn.getZ());
		for(int dx = -80; dx <= 80; dx++)
			for(int dz = -20; dz <= 30; dz++)
			{
				level.setBlockAndUpdate(stage.offset(dx, -1, dz), Blocks.POLISHED_ANDESITE.defaultBlockState());
				for(int dy = 0; dy < 14; dy++)
					level.setBlockAndUpdate(stage.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
			}
		frames(level, wall(0), tabItems(level, ModCreativeTabs.BASIC.get(), false), 26);
		frames(level, wall(1), tabItems(level, ModCreativeTabs.MODDED.get(), false), 26);
		frames(level, wall(2), tabItems(level, ModCreativeTabs.ARROWS_BOLTS.get(), true), 13);
		player.teleportTo(level, stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, 0.0F, true);
	}

	/** The tab's items, one stack per item (tipped projectiles: the first potion only) */
	private static List<ItemStack> tabItems(ServerLevel level, CreativeModeTab tab, boolean ammo)
	{
		tab.buildContents(new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), false, level.registryAccess()));
		List<ItemStack> all = new ArrayList<>();
		for(ItemStack s : tab.getDisplayItems())
			if(all.stream().noneMatch(a -> a.is(s.getItem())))
				all.add(s.copy());
		Log.info("[scene] tab " + tab.getDisplayName().getString() + ": " + all.size() + " items");
		return all;
	}

	private static void frames(ServerLevel level, BlockPos centre, List<ItemStack> items, int columns)
	{
		int rows = (items.size() + columns - 1) / columns;
		for(int i = 0; i < items.size(); i++)
		{
			int col = i % columns, line = i / columns;
			BlockPos framePos = centre.offset(columns / 2 - col, rows - line, 0);
			level.setBlockAndUpdate(framePos.south(), Blocks.SPRUCE_PLANKS.defaultBlockState());
			ItemFrame frame = new ItemFrame(level, framePos, Direction.NORTH);
			frame.setItem(items.get(i), false);
			level.addFreshEntity(frame);
		}
	}

	private static ItemStack oiled(Item weapon)
	{
		ItemStack stack = new ItemStack(weapon);
		IOilHandler handler = ModCapabilities.getOil(stack);
		if(handler != null)
			handler.setEffect(OilEffects.UNDEAD.get(), OilHelper.makeOilStack(OilEffects.UNDEAD.get()));
		return stack;
	}

	private static void dropped(ServerPlayer player)
	{
		ServerLevel level = player.level();
		BlockPos at = stage.offset(0, 0, 10);
		Item[] items = { Items.IRON_SWORD, ModItems.BATTLEAXES.iron.get(), ModItems.LONGSWORDS.iron.get(), ModItems.WARHAMMERS.iron.get(), ModItems.GREATSWORDS.iron.get(), ModItems.HALBERDS.iron.get() };
		for(int i = 0; i < items.length; i++)
		{
			ItemEntity entity = new ItemEntity(level, at.getX() + 0.5 + (i - items.length / 2) * 1.2, at.getY() + 0.1, at.getZ() + 0.5, new ItemStack(items[i]));
			entity.setDeltaMovement(Vec3.ZERO);
			entity.setNeverPickUp();
			entity.setUnlimitedLifetime();
			level.addFreshEntity(entity);
		}
		player.teleportTo(level, at.getX() + 0.5, at.getY(), at.getZ() - 3.0, java.util.Set.of(), 0.0F, 30.0F, true);
	}

	private static void heads(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.level().getEntitiesOfClass(ItemEntity.class, new AABB(stage).inflate(40)).forEach(Entity::discard);
		BlockPos at = stage.offset(0, 0, 16);
		List<net.minecraft.world.level.block.Block> heads = new ArrayList<>();
		BuiltInRegistries.BLOCK.entrySet().stream().filter(e -> e.getKey().identifier().getNamespace().equals(ModSpartanWeaponry.ID) && !e.getKey().identifier().getPath().contains("wall"))
				.forEach(e -> heads.add(e.getValue()));
		for(int i = 0; i < heads.size(); i++)
			level.setBlockAndUpdate(at.offset(i - heads.size() / 2, 0, 0), heads.get(i).defaultBlockState());
		check("extended heads placed", heads.size() == 10, heads.size());
		player.teleportTo(level, at.getX() + 0.5, at.getY() + 0.5, at.getZ() - 4.0, java.util.Set.of(), 0.0F, 15.0F, true);
	}

	private static void firstPerson(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.setItemSlot(EquipmentSlot.MAINHAND, oiled(ModItems.KATANAS.diamond.get()));
		player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
		player.teleportTo(level, stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, 10.0F, true);
		IronGolem golem = EntityTypes.IRON_GOLEM.spawn(level, stage.offset(0, 0, 12), EntitySpawnReason.COMMAND);
		golem.setNoAi(true);
		golem.setYRot(180.0F);
		golemId = golem.getId();
	}

	private static IronGolem golem(ServerPlayer player)
	{
		IronGolem golem = (IronGolem)player.level().getEntity(golemId);
		return golem;
	}

	private static void resetGolem(ServerPlayer player)
	{
		IronGolem golem = golem(player);
		golem.setHealth(golem.getMaxHealth());
		golem.setInvulnerableTime(0);
		golemHealth = golem.getHealth();
	}

	// ------------------------------------------------------------------------------------------------ ranged

	private static void prepareLongbow(ServerPlayer player)
	{
		player.setGameMode(GameType.SURVIVAL);
		player.getAbilities().invulnerable = true;
		player.onUpdateAbilities();
		player.getInventory().clearContent();
		player.getInventory().setItem(0, new ItemStack(ModItems.LONGBOWS.iron.get()));
		player.getInventory().setItem(8, new ItemStack(ModItems.IRON_ARROW.get(), 16));
		player.getInventory().setSelectedSlot(0);
		player.teleportTo(player.level(), stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, -3.0F, true);
		resetGolem(player);
	}

	private static void checkLongbow(ServerPlayer player)
	{
		IronGolem golem = golem(player);
		int arrows = player.getInventory().countItem(ModItems.IRON_ARROW.get());
		check("longbow (real input) shoots an iron arrow from the inventory", arrows == 15, arrows + " arrows left");
		check("the arrow hits the golem", golem.getHealth() < golemHealth, golemHealth + " -> " + golem.getHealth());
		check("longbow loses durability", player.getInventory().getItem(0).getDamageValue() == 1, player.getInventory().getItem(0).getDamageValue());
	}

	private static void prepareHeavyCrossbow(ServerPlayer player)
	{
		player.level().getEntitiesOfClass(AbstractArrow.class, new AABB(stage).inflate(40)).forEach(Entity::discard);
		player.getInventory().clearContent();
		player.getInventory().setItem(0, new ItemStack(ModItems.HEAVY_CROSSBOWS.iron.get()));
		player.getInventory().setItem(8, new ItemStack(ModItems.BOLT.get(), 8));
		player.getInventory().setSelectedSlot(0);
		player.teleportTo(player.level(), stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, -2.0F, true);
		resetGolem(player);
		Log.info("[scene] heavy crossbow load ticks " + ((HeavyCrossbowItem)ModItems.HEAVY_CROSSBOWS.iron.get()).getFullLoadTicks(player.getInventory().getItem(0)));
	}

	private static void checkLoaded(ServerPlayer player)
	{
		ItemStack crossbow = player.getInventory().getItem(0);
		ItemStack bolt = HeavyCrossbowItem.getLoadedBolt(crossbow, player.registryAccess());
		check("heavy crossbow loads a bolt (charged_projectiles)", !bolt.isEmpty() && bolt.is(ModItems.BOLT.get()), bolt);
		check("loading takes the bolt from the inventory", player.getInventory().countItem(ModItems.BOLT.get()) == 7, player.getInventory().countItem(ModItems.BOLT.get()));
	}

	private static void checkHeavyCrossbow(ServerPlayer player)
	{
		IronGolem golem = golem(player);
		ItemStack crossbow = player.getInventory().getItem(0);
		check("heavy crossbow fires (unloaded afterwards)", HeavyCrossbowItem.getLoadedBolt(crossbow, player.registryAccess()).isEmpty(), crossbow.get(DataComponents.CHARGED_PROJECTILES));
		check("the bolt hits the golem", golem.getHealth() < golemHealth, golemHealth + " -> " + golem.getHealth());
	}

	private static void prepareThrow(ServerPlayer player)
	{
		player.level().getEntitiesOfClass(AbstractArrow.class, new AABB(stage).inflate(40)).forEach(Entity::discard);
		player.getInventory().clearContent();
		ItemStack javelins = ((ThrowingWeaponItem)ModItems.JAVELINS.iron.get()).makeTabStack();
		player.getInventory().setItem(0, javelins);
		player.getInventory().setSelectedSlot(0);
		player.teleportTo(player.level(), stage.getX() + 0.5, stage.getY(), stage.getZ() + 4.5, java.util.Set.of(), 0.0F, -6.0F, true);
		resetGolem(player);
		countBefore = ammo(player.getInventory().getItem(0));
	}

	private static int ammo(ItemStack stack)
	{
		if(!(stack.getItem() instanceof ThrowingWeaponItem item))
			return -1;
		return item.getMaxAmmo(stack) - ItemNbt.get(stack).getIntOr(ThrowingWeaponItem.NBT_AMMO_USED, 0);
	}

	private static void checkThrowHit(ServerPlayer player)
	{
		IronGolem golem = golem(player);
		int left = ammo(player.getInventory().getItem(0));
		check("javelin thrown (real input) uses one ammo", left == countBefore - 1, countBefore + " -> " + left);
		check("the javelin hits the golem", golem.getHealth() < golemHealth, golemHealth + " -> " + golem.getHealth());
		List<ThrowingWeaponEntity> thrown = player.level().getEntitiesOfClass(ThrowingWeaponEntity.class, new AABB(stage).inflate(40));
		check("one thrown javelin in the world", thrown.size() == 1, thrown.size());
	}

	private static void checkThrowPickup(ServerPlayer player)
	{
		List<ThrowingWeaponEntity> thrown = player.level().getEntitiesOfClass(ThrowingWeaponEntity.class, new AABB(stage).inflate(40));
		if(!thrown.isEmpty())
		{
			ThrowingWeaponEntity javelin = thrown.get(0);
			javelin.playerTouch(player);
			for(int i = 0; i < 10 && javelin.isAlive(); i++)
				javelin.playerTouch(player);
		}
		int left = ammo(player.getInventory().getItem(0));
		int stacks = 0;
		for(ItemStack s : player.getInventory().getNonEquipmentItems())
			if(s.is(ModItems.JAVELINS.iron.get()))
				stacks++;
		check("picking the javelin up gives back exactly one ammo", left == countBefore && stacks == 1, "ammo " + left + "/" + countBefore + ", stacks " + stacks);
	}

	private static void prepareArrowPickup(ServerPlayer player)
	{
		player.getInventory().clearContent();
		player.getInventory().setItem(0, new ItemStack(ModItems.LONGBOWS.iron.get()));
		player.getInventory().setItem(8, new ItemStack(ModItems.IRON_ARROW.get(), 10));
		player.getInventory().setSelectedSlot(0);
		player.teleportTo(player.level(), stage.getX() - 10.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, 40.0F, true);
	}

	private static void walkToArrow(ServerPlayer player)
	{
		List<AbstractArrow> arrows = player.level().getEntitiesOfClass(AbstractArrow.class, new AABB(stage.offset(-10, 0, 0)).inflate(12));
		check("the arrow is in the ground", arrows.size() == 1, arrows.size());
		if(!arrows.isEmpty())
		{
			AbstractArrow arrow = arrows.get(0);
			player.teleportTo(player.level(), arrow.getX(), arrow.getY(), arrow.getZ(), java.util.Set.of(), 0.0F, 70.0F, true);
		}
	}

	private static void checkArrowPickup(ServerPlayer player)
	{
		int arrows = player.getInventory().countItem(ModItems.IRON_ARROW.get());
		long left = player.level().getEntitiesOfClass(AbstractArrow.class, new AABB(stage.offset(-10, 0, 0)).inflate(12)).size();
		check("issue #188: shooting and picking the arrow up gives back one arrow, not two", arrows == 10 && left == 0, arrows + " arrows, " + left + " left in the world");
	}

	private static ItemStack quiverWith(Item quiver, int arrows)
	{
		ItemStack stack = new ItemStack(quiver);
		IQuiverItemHandler handler = ModCapabilities.getQuiver(stack);
		handler.insertItem(0, new ItemStack(ModItems.IRON_ARROW.get(), Math.min(64, arrows)), false);
		if(arrows > 64)
			handler.insertItem(1, new ItemStack(Items.ARROW, arrows - 64), false);
		return stack;
	}

	private static void prepareQuiver(ServerPlayer player)
	{
		player.getInventory().clearContent();
		player.getInventory().setItem(0, quiverWith(ModItems.SMALL_ARROW_QUIVER.get(), 32));
		player.getInventory().setItem(1, new ItemStack(ModItems.LONGBOWS.iron.get()));
		player.getInventory().setItem(2, new ItemStack(Items.TORCH, 4));
		player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
		player.getInventory().setSelectedSlot(2);
	}

	private static void checkQuiverOffhand(ServerPlayer player)
	{
		ItemStack offhand = player.getOffhandItem();
		ItemStack quiver = player.getInventory().getItem(0);
		IQuiverItemHandler handler = ModCapabilities.getQuiver(quiver);
		int inQuiver = 0;
		for(int i = 0; i < handler.getSlots(); i++)
			inQuiver += handler.getStackInSlot(i).getCount();
		check("selecting a bow takes arrows out of the quiver into the off hand", offhand.is(ModItems.IRON_ARROW.get()) && offhand.getCount() + inQuiver == 32, offhand + ", in the quiver " + inQuiver);
	}

	private static void prepareCuriosQuiver(ServerPlayer player)
	{
		if(!Platform.INSTANCE.isModLoaded("curios"))
			return;
		player.setGameMode(GameType.SURVIVAL);
		player.getInventory().clearContent();
		player.getInventory().setItem(1, new ItemStack(ModItems.LONGBOWS.iron.get()));
		player.getInventory().setItem(2, new ItemStack(Items.TORCH, 4));
		player.getInventory().setSelectedSlot(2);
		ItemStack quiver = quiverWith(ModItems.MEDIUM_ARROW_QUIVER.get(), 40);
		boolean placed = accessorySlot != null && accessorySlot.test(player, quiver);
		check("Curios: the quiver goes into the back slot", placed, "");
	}

	private static void checkCuriosQuiver(ServerPlayer player)
	{
		if(!Platform.INSTANCE.isModLoaded("curios"))
			return;
		ItemStack offhand = player.getOffhandItem();
		check("Curios: selecting a bow takes arrows out of the quiver in the back slot", offhand.is(ModItems.IRON_ARROW.get()), offhand);
		player.setGameMode(GameType.CREATIVE);
	}

	private static boolean isShoulderSurfing()
	{
		return Platform.INSTANCE.isModLoaded("shouldersurfing");
	}

	private static void prepareShoulderSurfing(ServerPlayer player)
	{
		if(!isShoulderSurfing())
			return;
		player.getInventory().clearContent();
		ItemStack crossbow = new ItemStack(ModItems.HEAVY_CROSSBOWS.iron.get());
		HeavyCrossbowItem.setLoadedBolt(crossbow, new ItemStack(ModItems.BOLT.get()), 1);
		ItemNbt.update(crossbow, nbt -> nbt.putBoolean(HeavyCrossbowItem.NBT_CHARGED, true));
		player.getInventory().setItem(0, crossbow);
		player.getInventory().setItem(1, new ItemStack(ModItems.JAVELINS.iron.get()));
		player.getInventory().setSelectedSlot(0);
		player.teleportTo(player.level(), stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, 0.0F, true);
	}

	/** Through the API by reflection (the 4.x and 5.x API classes differ, the scene compiles against neither) */
	private static void shoulderSurfing(Minecraft mc, String perspective)
	{
		if(!isShoulderSurfing())
			return;
		try
		{
			Class<?> api;
			try
			{
				api = Class.forName("com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing");	// 5.2+
			}
			catch(ClassNotFoundException e)
			{
				api = Class.forName("com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing");
			}
			Object instance = api.getMethod("getInstance").invoke(null);
			Class<?> perspectiveClass;
			try
			{
				perspectiveClass = Class.forName("com.github.exopandora.shouldersurfing.api.client.Perspective");	// 5.x
			}
			catch(ClassNotFoundException e)
			{
				perspectiveClass = Class.forName("com.github.exopandora.shouldersurfing.api.model.Perspective");	// 4.x
			}
			Object value = perspectiveClass.getMethod("valueOf", String.class).invoke(null, perspective);
			api.getMethod("changePerspective", perspectiveClass).invoke(instance, value);
			check("Shoulder Surfing perspective " + perspective + " with a heavy crossbow", true, mc.options.getCameraType());
		}
		catch(ReflectiveOperationException | RuntimeException e)
		{
			check("Shoulder Surfing perspective " + perspective + " with a heavy crossbow", false, e);
		}
	}

	private static void traitDataPack(ServerPlayer player)
	{
		MinecraftServer server = player.level().getServer();
		try
		{
			java.nio.file.Path pack = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.DATAPACK_DIR).resolve("sw_traits");
			java.nio.file.Path tag = pack.resolve("data/spartanweaponry/tags/spartanweaponry/weapon_traits/warhammer.json");
			java.nio.file.Files.createDirectories(tag.getParent());
			java.nio.file.Files.writeString(pack.resolve("pack.mcmeta"), "{\"pack\": {\"pack_format\": 48, \"description\": \"scene\"}}");
			java.nio.file.Files.writeString(tag, "{\"replace\": true, \"values\": [\"spartanweaponry:armour_piercing\"]}");
		}
		catch(java.io.IOException e)
		{
			check("data pack written", false, e);
			return;
		}
		server.getPackRepository().reload();
		List<String> selected = new ArrayList<>(server.getPackRepository().getSelectedIds());
		selected.add("file/sw_traits");
		server.reloadResources(selected);
	}

	private static void checkTraitDataPack(ServerPlayer player)
	{
		var warhammer = (com.oblivioussp.spartanweaponry.api.IWeaponTraitContainer<?>)ModItems.WARHAMMERS.iron.get();
		List<String> traits = warhammer.getAllWeaponTraits().stream().map(t -> String.valueOf(com.oblivioussp.spartanweaponry.api.WeaponTraits.TRAIT_REGISTRY.getKey(t))).toList();
		check("issue #162: a data pack replaces the warhammer traits (no Two-Handed any more)", traits.contains("spartanweaponry:armour_piercing") && traits.stream().noneMatch(t -> t.contains("two_handed")), traits);
	}

	private static void hugeQuiver(ServerPlayer player)
	{
		player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
		player.getInventory().setItem(0, quiverWith(ModItems.HUGE_ARROW_QUIVER.get(), 100));
		player.getInventory().setSelectedSlot(0);
	}

	// ------------------------------------------------------------------------------------------------- melee

	private static void oil(ServerPlayer player)
	{
		player.getInventory().clearContent();
		player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.KATANAS.iron.get()));
		player.setItemSlot(EquipmentSlot.OFFHAND, OilHelper.makeOilStack(OilEffects.UNDEAD.get()));
		player.getOffhandItem().use(player.level(), player, InteractionHand.OFF_HAND);
		IOilHandler handler = ModCapabilities.getOil(player.getMainHandItem());
		check("weapon oil applies to the katana in the other hand", handler != null && handler.isOiled() && handler.getEffect().map(e -> e == OilEffects.UNDEAD.get()).orElse(false), handler == null ? "no handler" : handler.getEffect());
		check("the oil bottle is used up (glass bottle back)", player.getOffhandItem().isEmpty() && player.getInventory().countItem(Items.GLASS_BOTTLE) == 1, player.getOffhandItem());
		player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
	}

	private static Zombie target(ServerPlayer player, double dz, boolean armored)
	{
		ServerLevel level = player.level();
		Zombie zombie = EntityTypes.ZOMBIE.spawn(level, BlockPos.containing(player.getX(), player.getY(), player.getZ() + dz), EntitySpawnReason.COMMAND);
		zombie.setNoAi(true);
		for(EquipmentSlot slot : EquipmentSlot.values())
			zombie.setItemSlot(slot, ItemStack.EMPTY);
		if(armored)
		{
			zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
			zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
			zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
			zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
		}
		// No burning in the daylight (the damage checks look at the health)
		zombie.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
		zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200.0);
		zombie.setHealth(200.0F);
		return zombie;
	}

	private static ItemStack meleeKatana;

	private static void prepareMelee(ServerPlayer player)
	{
		player.teleportTo(player.level(), stage.getX() + 20.5, stage.getY(), stage.getZ() + 0.5, java.util.Set.of(), 0.0F, 0.0F, true);
		meleeKatana = player.getMainHandItem();
	}

	/** Full strength hits (the hand item was set more than a second ago) */
	private static float hit(ServerPlayer player, Zombie target)
	{
		float before = target.getHealth();
		target.setInvulnerableTime(0);
		setAttackTicker(player, 1000);
		player.attack(target);
		return before - target.getHealth();
	}

	private static void setAttackTicker(ServerPlayer player, int ticks)
	{
		// Opened by the access transformer / widener (by name it would be another on a production Fabric client)
		player.attackStrengthTicker = ticks;
	}

	private static void melee(ServerPlayer player)
	{
		// Oil: the undead bonus on a zombie, one use gone
		Zombie zombie = target(player, 2.0, false);
		float oiledDamage = hit(player, zombie);
		IOilHandler handler = ModCapabilities.getOil(meleeKatana);
		int usesLeft = handler.getUsesLeft();
		handler.clearEffect();
		float plainDamage = hit(player, zombie);
		check("Hallowed oil adds damage against undead", oiledDamage > plainDamage + 0.5F, oiledDamage + " vs " + plainDamage);
		check("a hit uses the oil once", usesLeft == OilEffects.UNDEAD.get().getMaxUses() - 1, usesLeft + "/" + OilEffects.UNDEAD.get().getMaxUses());
		zombie.discard();
		// Attack damage of the item (a chestplate on the target: no Chest Damage Bonus of the katana)
		Zombie plated = target(player, 2.0, false);
		plated.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		float katanaAttack = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float katanaDealt = hit(player, plated);
		float expected = CombatRules.getDamageAfterAbsorb(plated, katanaAttack, player.damageSources().playerAttack(player), (float)plated.getArmorValue(), (float)plated.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
		check("katana hit deals its attack damage (after armour)", Math.abs(katanaDealt - expected) < 0.01F, katanaDealt + " vs " + expected + " of " + katanaAttack);
		plated.discard();
		player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WARHAMMERS.iron.get()));
		// The armour counts from the next tick on
		armoredId = target(player, 2.0, true).getId();
	}

	private static int armoredId;

	/** Armour piercing: the warhammer against diamond armour beats the vanilla armour formula */
	private static void armourPiercing(ServerPlayer player)
	{
		Zombie armored = (Zombie)player.level().getEntity(armoredId);
		float attack = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
		float dealt = hit(player, armored);
		float vanilla = CombatRules.getDamageAfterAbsorb(armored, attack, player.damageSources().playerAttack(player), (float)armored.getArmorValue(), (float)armored.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
		check("Armour Piercing: warhammer against diamond armour", dealt > vanilla + 0.3F, dealt + " dealt, vanilla armour formula " + vanilla + " of " + attack);
		armored.discard();
		player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SPEARS.iron.get()));
	}

	private static void reach(ServerPlayer player)
	{
		double reach = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
		check("Reach: a spear extends the entity interaction range", reach > 3.0, reach);
		// 26.1: the trades are data; the weaponsmith level 1 trade as a villager makes it
		var trade = player.level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.VILLAGER_TRADE).getValue(Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, "weaponsmith/1/emerald_random_weapon"));
		var context = new net.minecraft.world.level.storage.loot.LootContext.Builder(new net.minecraft.world.level.storage.loot.LootParams.Builder(player.level())
				.withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN, player.position())
				.withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY, player)
				.withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, net.minecraft.util.Unit.INSTANCE)
				.create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VILLAGER_TRADE)).create(java.util.Optional.empty());
		var offer = trade == null ? null : trade.getOffer(context);
		check("weaponsmith trade offers an iron weapon (1.20.1 kept the trades on empty lists)", offer != null && offer.getResult().is(ModItemTags.OILABLE_WEAPONS), offer == null ? "none" : offer.getResult());
		int price = offer == null ? -1 : com.oblivioussp.spartanweaponry.merchant.villager.WeaponsmithTrades.LVL1_ITEMS.stream().filter(item -> offer.getResult().is(item.getItem())).findFirst().map(item -> item.getEmeraldCost()).orElse(-1);
		check("the weaponsmith trade costs the weapon's emeralds (26.1: the additional trade cost)", offer != null && price > 0 && offer.getBaseCostA().is(net.minecraft.world.item.Items.EMERALD) && offer.getBaseCostA().getCount() == price, offer == null ? "none" : offer.getBaseCostA() + ", list price " + price);
		player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
	}

	private static void reachBack(ServerPlayer player)
	{
		double handReach = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
		check("the reach goes back with an empty hand", handReach == 3.0, handReach);
	}

	private static int fallingZombieId;
	private static float fallingHealth;

	/** Issue #186: a weapon thrown down onto a mob hit it every time the hurt cooldown ran out */
	private static void throwDown(ServerPlayer player)
	{
		ServerLevel level = player.level();
		Zombie zombie = target(player, 6.0, false);
		fallingZombieId = zombie.getId();
		ItemStack javelin = new ItemStack(ModItems.JAVELINS.iron.get());
		ThrowingWeaponEntity thrown = ((ThrowingWeaponItem)javelin.getItem()).createThrowingWeaponEntity(level, player, javelin, 20);
		thrown.setWeapon(javelin);
		thrown.setBaseDamage(6.0);
		thrown.setPos(zombie.getX(), zombie.getY() + 6.0, zombie.getZ());
		thrown.setDeltaMovement(0.0, -1.5, 0.0);
		level.addFreshEntity(thrown);
	}

	private static void throwDownFirstHit(ServerPlayer player)
	{
		Zombie zombie = (Zombie)player.level().getEntity(fallingZombieId);
		fallingHealth = zombie.getHealth();
		check("the javelin thrown down hits the zombie", fallingHealth < 200.0F, fallingHealth);
	}

	private static void throwDownLater(ServerPlayer player)
	{
		Zombie zombie = (Zombie)player.level().getEntity(fallingZombieId);
		check("issue #186: it hits once, not every time the hurt cooldown ends", zombie.getHealth() == fallingHealth, fallingHealth + " after the hit, " + zombie.getHealth() + " 5 s later");
		zombie.discard();
		player.level().getEntitiesOfClass(ThrowingWeaponEntity.class, zombie.getBoundingBox().inflate(10)).forEach(Entity::discard);
	}

	private static void prepareTwoHanded(ServerPlayer player)
	{
		player.removeAllEffects();
		player.getInventory().clearContent();
		player.getInventory().setItem(0, new ItemStack(ModItems.GREATSWORDS.iron.get()));
		player.getInventory().setSelectedSlot(0);
		player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TORCH));
	}

	private static void checkTwoHanded(ServerPlayer player)
	{
		MobEffectInstance fatigue = player.getEffect(MobEffects.MINING_FATIGUE);
		check("Two-Handed: greatsword with an off hand item gives Mining Fatigue", fatigue != null, fatigue);
		player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
		player.removeAllEffects();
		player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, MobEffectInstance.INFINITE_DURATION, 0));
	}

	private static void checkInfiniteFatigue(ServerPlayer player)
	{
		MobEffectInstance fatigue = player.getEffect(MobEffects.MINING_FATIGUE);
		check("issue #169: a two-handed weapon keeps Mining Fatigue from other sources", fatigue != null && fatigue.isInfiniteDuration(), fatigue);
		player.removeAllEffects();
	}

	private static void parry(ServerPlayer player)
	{
		player.getAbilities().invulnerable = false;
		player.onUpdateAbilities();
		player.setHealth(player.getMaxHealth());
		player.getInventory().clearContent();
		player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.PARRYING_DAGGERS.iron.get()));
		Zombie zombie = target(player, 1.5, false);
		zombie.setYRot(180.0F);
		player.startUsingItem(InteractionHand.MAIN_HAND);
		boolean using = player.isUsingItem();
		player.setInvulnerableTime(0);
		player.hurt(player.damageSources().mobAttack(zombie), 4.0F);
		float blocked = player.getMaxHealth() - player.getHealth();
		player.stopUsingItem();
		player.setInvulnerableTime(0);
		player.hurt(player.damageSources().mobAttack(zombie), 4.0F);
		float taken = player.getMaxHealth() - player.getHealth() - blocked;
		check("Melee Block: a parrying dagger in use blocks a melee hit", using && blocked == 0.0F && taken > 0.0F, "using " + using + ", blocked hit " + blocked + ", unblocked " + taken);
		check("the block wears the dagger", player.getMainHandItem().getDamageValue() > 0, player.getMainHandItem().getDamageValue());
		zombie.discard();
		player.setHealth(player.getMaxHealth());
		player.getAbilities().invulnerable = true;
		player.onUpdateAbilities();
	}

	private static void decapitate(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SCYTHES.iron.get()));
		int heads = 0, kills = 0;
		for(; kills < 60; kills++)
		{
			Zombie zombie = target(player, 2.0, false);
			zombie.hurt(player.damageSources().playerAttack(player), 1000.0F);
			heads += level.getEntitiesOfClass(ItemEntity.class, zombie.getBoundingBox().inflate(3)).stream().filter(e -> e.getItem().is(Items.ZOMBIE_HEAD)).mapToInt(e -> e.getItem().getCount()).sum();
			level.getEntitiesOfClass(ItemEntity.class, zombie.getBoundingBox().inflate(3)).forEach(Entity::discard);
			level.getEntitiesOfClass(net.minecraft.world.entity.ExperienceOrb.class, zombie.getBoundingBox().inflate(3)).forEach(Entity::discard);
			zombie.discard();
		}
		check("Decapitate: a scythe drops zombie heads (25%)", heads >= 5 && heads <= 30, heads + " heads in " + kills + " kills");
		// Without the trait: no heads
		player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
		int plain = 0;
		for(int i = 0; i < 40; i++)
		{
			Zombie zombie = target(player, 2.0, false);
			zombie.hurt(player.damageSources().playerAttack(player), 1000.0F);
			plain += level.getEntitiesOfClass(ItemEntity.class, zombie.getBoundingBox().inflate(3)).stream().filter(e -> e.getItem().is(Items.ZOMBIE_HEAD)).mapToInt(e -> e.getItem().getCount()).sum();
			level.getEntitiesOfClass(ItemEntity.class, zombie.getBoundingBox().inflate(3)).forEach(Entity::discard);
			zombie.discard();
		}
		check("no heads without the trait", plain == 0, plain);
	}

	private static int fleshFromThrownKills(ServerPlayer player, ItemStack weapon, int kills)
	{
		ServerLevel level = player.level();
		int flesh = 0;
		for(int i = 0; i < kills; i++)
		{
			Zombie zombie = target(player, 2.0, false);
			ThrowingWeaponEntity thrown = ((ThrowingWeaponItem)weapon.getItem()).createThrowingWeaponEntity(level, player, weapon, 20);
			thrown.setWeapon(weapon);
			zombie.hurt(player.damageSources().thrown(thrown, player), 1000.0F);
			flesh += level.getEntitiesOfClass(ItemEntity.class, zombie.getBoundingBox().inflate(3)).stream().filter(e -> e.getItem().is(Items.ROTTEN_FLESH)).mapToInt(e -> e.getItem().getCount()).sum();
			level.getEntitiesOfClass(ItemEntity.class, zombie.getBoundingBox().inflate(3)).forEach(Entity::discard);
			thrown.discard();
			zombie.discard();
		}
		return flesh;
	}

	private static void luckyThrow(ServerPlayer player)
	{
		player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		ItemStack plain = new ItemStack(ModItems.THROWING_KNIVES.iron.get());
		ItemStack lucky = plain.copy();
		var enchantments = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		ItemEnchantments.Mutable ench = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		ench.set(enchantments.getOrThrow(ModEnchantments.LUCKY_THROW), 3);
		lucky.set(DataComponents.ENCHANTMENTS, ench.toImmutable());
		int plainFlesh = fleshFromThrownKills(player, plain, 40);
		int luckyFlesh = fleshFromThrownKills(player, lucky, 40);
		// Zombies: 0-2 rotten flesh, +0-1 per Looting level (40 kills: about 40 vs 100)
		check("Lucky Throw III: thrown weapon kills drop loot like Looting III", luckyFlesh > plainFlesh * 1.6F, "rotten flesh " + plainFlesh + " plain, " + luckyFlesh + " with Lucky Throw III");
	}

	private static int skeletonId, villagerId;

	private static void prepareSkeleton(ServerPlayer player)
	{
		ServerLevel level = player.level();
		player.setGameMode(GameType.CREATIVE);
		BlockPos at = stage.offset(-30, 0, 0);
		Skeleton skeleton = EntityTypes.SKELETON.spawn(level, at, EntitySpawnReason.COMMAND);
		skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.LONGBOWS.iron.get()));
		Villager villager = EntityTypes.VILLAGER.spawn(level, at.offset(0, 0, 10), EntitySpawnReason.COMMAND);
		villager.setNoAi(true);
		villager.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500.0);
		villager.setHealth(500.0F);
		skeleton.setTarget(villager);
		skeletonId = skeleton.getId();
		villagerId = villager.getId();
	}

	private static void checkSkeleton(ServerPlayer player)
	{
		ServerLevel level = player.level();
		Skeleton skeleton = (Skeleton)level.getEntity(skeletonId);
		Villager villager = (Villager)level.getEntity(villagerId);
		long arrows = level.getEntitiesOfClass(AbstractArrow.class, new AABB(stage.offset(-30, 0, 0)).inflate(20)).stream().filter(a -> a.getOwner() == skeleton).count();
		check("issue #185: a skeleton with a longbow shoots", skeleton != null && (arrows > 0 || villager.getHealth() < 500.0F), "arrows " + arrows + ", villager " + (villager == null ? "gone" : villager.getHealth()));
		if(skeleton != null)
			skeleton.discard();
		if(villager != null)
			villager.discard();
	}

	private static void enderDisruption(ServerPlayer player)
	{
		ServerLevel level = player.level();
		Enderman enderman = EntityTypes.ENDERMAN.spawn(level, stage.offset(30, 0, 0), EntitySpawnReason.COMMAND);
		enderman.setNoAi(true);
		enderman.addEffect(new MobEffectInstance(ModMobEffects.ENDER_DISRPUTION.holder(), 600));
		Vec3 start = enderman.position();
		boolean teleported = false;
		// Enderman.teleport() is opened by the access transformer / widener
		for(int i = 0; i < 30; i++)
			teleported |= enderman.teleport();
		check("Ender Disruption stops an enderman from teleporting", !teleported && enderman.position().equals(start), enderman.position());
		enderman.removeAllEffects();
		boolean free = false;
		for(int i = 0; i < 30 && !free; i++)
			free = enderman.teleport();
		check("without the effect it teleports", free, enderman.position());
		enderman.discard();
	}

	private static void explosive(ServerPlayer player)
	{
		ServerLevel level = player.level();
		ArrowExplosiveEntity arrow = new ArrowExplosiveEntity(level, player, new ItemStack(ModItems.LONGBOWS.iron.get()));
		arrow.setPos(stage.getX() + 40.5, stage.getY() + 6, stage.getZ() + 0.5);
		arrow.setDeltaMovement(0.0, -1.5, 0.0);
		level.addFreshEntity(arrow);
		ThrowingWeaponEntity boomerang = ((ThrowingWeaponItem)ModItems.BOOMERANGS.iron.get()).createThrowingWeaponEntity(level, player, new ItemStack(ModItems.BOOMERANGS.iron.get()), 20);
		boomerang.setWeapon(new ItemStack(ModItems.BOOMERANGS.iron.get()));
		level.addFreshEntity(boomerang);
	}

	private static void checkExplosive(ServerPlayer player)
	{
		long arrows = player.level().getEntitiesOfClass(ArrowExplosiveEntity.class, new AABB(stage.offset(40, 0, 0)).inflate(10)).size();
		check("explosive arrow explodes on impact", arrows == 0, arrows + " left");
	}

	// ------------------------------------------------------------------------------------------------- data

	private static void checkData(ServerPlayer player)
	{
		ServerLevel level = player.level();
		var recipes = level.recipeAccess();
		long ours = recipes.getRecipes().stream().filter(r -> r.id().identifier().getNamespace().equals(ModSpartanWeaponry.ID)).count();
		check("recipes loaded", ours > 200, ours + " recipes");
		check("modded material recipes skipped without the material (no tin ingots)", recipes.byKey(recipe("tin_longsword")).isEmpty(), "");
		var smelting = recipes.byKey(recipe("iron_nugget_from_smelting_iron_weapons"));
		check("tag smelting: iron weapons melt into iron nuggets", smelting.map(r -> smelted(r, level).is(Items.IRON_NUGGET)
				&& ((net.minecraft.world.item.crafting.AbstractCookingRecipe)r.value()).input().test(new ItemStack(ModItems.LONGSWORDS.iron.get()))).orElse(false), smelting.map(r -> smelted(r, level)).orElse(ItemStack.EMPTY));
		// 26.3: the oil brewing stand recipes are data (CodeBrewingRecipe); the stand finds them and takes their items
		ItemStack baseOil = com.oblivioussp.spartanweaponry.util.OilHelper.makeOilStack(com.oblivioussp.spartanweaponry.api.OilEffects.NONE.get());
		ItemStack melon = new ItemStack(Items.GLISTERING_MELON_SLICE);
		var oilInput = new net.minecraft.world.item.crafting.BrewingInput(baseOil, melon);
		var oilRecipe = recipes.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.BREWING, oilInput, level);
		ItemStack brewedOil = oilRecipe.map(r -> r.value().assemble(oilInput)).orElse(ItemStack.EMPTY);
		check("oil brewing: oil base + glistering melon slice makes undead oil", com.oblivioussp.spartanweaponry.util.OilHelper.getOilFromStack(brewedOil) == com.oblivioussp.spartanweaponry.api.OilEffects.UNDEAD.get(), oilRecipe.map(r -> r.id() + " -> " + brewedOil).orElse("no recipe"));
		check("oil brewing: the brewing stand takes the oil and the reagent", recipes.propertySet(net.minecraft.world.item.crafting.RecipePropertySet.BREWING_INPUTS).test(baseOil)
				&& recipes.propertySet(net.minecraft.world.item.crafting.RecipePropertySet.BREWING_REAGENTS).test(melon), "");
		ItemStack poison = net.minecraft.world.item.alchemy.PotionContents.createItemStack(Items.POTION, net.minecraft.world.item.alchemy.Potions.POISON);
		var potionInput = new net.minecraft.world.item.crafting.BrewingInput(poison, new ItemStack(ModItems.GREASE_BALL.get()));
		ItemStack potionOil = recipes.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.BREWING, potionInput, level).map(r -> r.value().assemble(potionInput)).orElse(ItemStack.EMPTY);
		check("oil brewing: a potion + grease ball makes a potion oil", potionOil.is(ModItems.WEAPON_OIL.get()) && com.oblivioussp.spartanweaponry.util.OilHelper.getPotionFromStack(potionOil).isPresent(), potionOil);
		var plainInput = new net.minecraft.world.item.crafting.BrewingInput(PotionContentsAwkward(), new ItemStack(Items.BLAZE_POWDER));
		check("vanilla brewing still works (awkward potion + blaze powder)", recipes.getRecipeFor(net.minecraft.world.item.crafting.RecipeType.BREWING, plainInput, level).isPresent(), "");
		// Enchanting table
		var possible = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE).orElseThrow();
		check("enchanting table: throwing knife", offered(new ItemStack(ModItems.THROWING_KNIVES.iron.get()), possible).containsAll(List.of("spartanweaponry:razors_edge", "spartanweaponry:propel", "spartanweaponry:lucky_throw", "minecraft:unbreaking")), offered(new ItemStack(ModItems.THROWING_KNIVES.iron.get()), possible));
		List<String> katana = offered(new ItemStack(ModItems.KATANAS.iron.get()), possible);
		check("enchanting table: katana (Sweep I trait: Sweeping Edge)", katana.containsAll(List.of("minecraft:sharpness", "minecraft:looting", "minecraft:sweeping_edge")) && !katana.contains("spartanweaponry:propel"), katana);
		List<String> greatsword = offered(new ItemStack(ModItems.GREATSWORDS.iron.get()), possible);
		check("enchanting table: no Sweeping Edge on a greatsword (Sweep III, as in 1.20.1)", greatsword.contains("minecraft:sharpness") && !greatsword.contains("minecraft:sweeping_edge"), greatsword);
		List<String> warhammer = offered(new ItemStack(ModItems.WARHAMMERS.iron.get()), possible);
		check("enchanting table: no Sweeping Edge on a warhammer", warhammer.contains("minecraft:sharpness") && !warhammer.contains("minecraft:sweeping_edge"), warhammer);
		List<String> crossbow = offered(new ItemStack(ModItems.HEAVY_CROSSBOWS.iron.get()), possible);
		check("enchanting table: heavy crossbow", crossbow.containsAll(List.of("spartanweaponry:sharpshooter", "minecraft:piercing")), crossbow);
		check("enchanting table: longbow", offered(new ItemStack(ModItems.LONGBOWS.iron.get()), possible).contains("minecraft:power"), offered(new ItemStack(ModItems.LONGBOWS.iron.get()), possible));
		check("enchanting table: boomerang", offered(new ItemStack(ModItems.BOOMERANGS.iron.get()), possible).contains("spartanweaponry:collectorang"), offered(new ItemStack(ModItems.BOOMERANGS.iron.get()), possible));
		check("Loyalty on throwing weapons (anvil)", ((ThrowingWeaponItem)ModItems.JAVELINS.iron.get()).supportsEnchantment(new ItemStack(ModItems.JAVELINS.iron.get()), level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOYALTY)), "");
		// Loot injected into the village weaponsmith chest
		var table = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.VILLAGE_WEAPONSMITH);
		int found = 0;
		for(int i = 0; i < 100; i++)
		{
			LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(stage)).create(LootContextParamSets.CHEST);
			for(ItemStack s : table.getRandomItems(params))
				if(BuiltInRegistries.ITEM.getKey(s.getItem()).getNamespace().equals(ModSpartanWeaponry.ID))
					found++;
		}
		check("village weaponsmith chests contain Spartan weapons", found > 0, found + " in 100 chests");
		// Durability from the config (issue #167 longbow, #183 throwing weapons)
		ItemStack longbow = new ItemStack(ModItems.LONGBOWS.iron.get());
		check("longbow durability from the material", longbow.getMaxDamage() > 300, longbow.getMaxDamage());
		if(Platform.INSTANCE.isModLoaded("jeed"))
			check("JEED: effect provider recipe for Ender Disruption loaded", recipes.byKey(recipe("compat/jeed/ender_disruption")).isPresent() != recipes.byKey(recipe("compat/jeed/ender_disruption_fabric")).isPresent(), "one per loader");
		check("heads: 11 extended skull blocks", ModBlocks.REGISTRY.getEntries().size() >= 11, ModBlocks.REGISTRY.getEntries().size());
	}

	/** 1.21.2+: a recipe is looked up by its key */
	private static net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> recipe(String path)
	{
		return net.minecraft.resources.ResourceKey.create(Registries.RECIPE, id(path));
	}

	/** What a tag cooking recipe makes of an iron longsword (1.21.2+: recipes have no result item to ask) */
	private static ItemStack smelted(net.minecraft.world.item.crafting.RecipeHolder<?> recipe, ServerLevel level)
	{
		return recipe.value() instanceof net.minecraft.world.item.crafting.AbstractCookingRecipe cooking
				? ((net.minecraft.world.item.crafting.Recipe<net.minecraft.world.item.crafting.SingleRecipeInput>)cooking).assemble(new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(ModItems.LONGSWORDS.iron.get()))) : ItemStack.EMPTY;
	}

	private static ItemStack PotionContentsAwkward()
	{
		return net.minecraft.world.item.alchemy.PotionContents.createItemStack(Items.POTION, net.minecraft.world.item.alchemy.Potions.AWKWARD);
	}

	private static List<String> offered(ItemStack stack, net.minecraft.core.HolderSet.Named<net.minecraft.world.item.enchantment.Enchantment> possible)
	{
		List<EnchantmentInstance> available = EnchantmentHelper.getAvailableEnchantmentResults(30, stack, possible.stream());
		return available.stream().map(e -> e.enchantment().unwrapKey().get().identifier().toString()).distinct().sorted().toList();
	}

	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath(ModSpartanWeaponry.ID, path);
	}

	/** Shows the tooltips of some items side by side */
	private static class TooltipScreen extends Screen
	{
		private final List<ItemStack> stacks;

		TooltipScreen(List<ItemStack> stacks)
		{
			super(Component.empty());
			this.stacks = stacks;
		}

		@Override
		public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
		{
			graphics.fill(0, 0, width, height, 0xFF2B2B33);
			for(int i = 0; i < stacks.size(); i++)
			{
				ItemStack stack = stacks.get(i);
				List<Component> lines = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
				// 1.21.6+: one tooltip per frame is deferred; these four are drawn at once, as vanilla's tooltip
				List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent> components = new ArrayList<>();
				lines.forEach(line -> components.add(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.create(line.getVisualOrderText())));
				stack.getTooltipImage().map(com.oblivioussp.spartanweaponry.client.ClientHelper::createTooltipComponent).ifPresent(image -> components.add(Math.min(1, components.size()), image));
				graphics.tooltip(font, components, (i % 2) * width / 2 + 4, (i / 2) * height / 2 + 16, net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE, null, false);
				for(Component line : lines)
					if(line.getString().contains("tooltip.spartanweaponry") || line.getString().contains("item.spartanweaponry"))
						Log.info("[scene] CHECK FAIL untranslated tooltip line (" + line.getString() + ")");
			}
		}
	}

	private static class ItemsScreen extends Screen
	{
		private final List<Item> items;

		ItemsScreen(List<Item> items)
		{
			super(Component.empty());
			this.items = items;
		}

		@Override
		public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
		{
			graphics.fill(0, 0, width, height, 0xFF2B2B33);
			float scale = Math.min(width / (items.size() * 20.0F), height / 40.0F);
			graphics.pose().pushMatrix();
			graphics.pose().scale(scale, scale);
			for(int i = 0; i < items.size(); i++)
			{
				graphics.item(new ItemStack(items.get(i)), 2 + i * 20, 2);
				graphics.item(oiled(items.get(i)), 2 + i * 20, 22);
			}
			graphics.pose().popMatrix();
		}
	}

	/** 26.2: the HUD is hidden through Minecraft.gui.hud, which only toggles */
	private static void setHudHidden(net.minecraft.client.Minecraft mc, boolean hidden)
	{
		if(mc.gui.hud.isHidden() != hidden)
			mc.gui.hud.toggle();
	}
}
