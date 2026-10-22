# Spartan Weaponry — port log

Port of **Spartan Weaponry 3.2.1** (ObliviousSpartan, Apache-2.0) from Minecraft 1.20.1 (Forge 47) to
**Minecraft 1.21.1, NeoForge 21.1** (built with 21.1.259, `neoforge` range `[21.1.197,)`). Mod id, item ids, recipe ids,
config files and keys, textures and the API package are unchanged.

License: the port is LGPL-3.0-only (`LICENSE`, GPL text in `licenses/COPYING`); the original's Apache License 2.0 text
is kept in `licenses/LICENSE-APACHE` (Apache 2.0 allows this). All texts are in the jar under `licenses/spartanweaponry/`.

## Sources

| What | Where |
| --- | --- |
| 1.20.1, v3.2.1 (the spec) | github.com/ObliviousSpartan/SpartanWeaponry, branch `1.20.1`, `e45342c` |
| Issues | the same repository's issue tracker (open issues read 2026-10-15) |
| Minecraft / NeoForge sources | `neoforge-21.1.259-sources.jar` (`build/tmp/src`) |

## How it was made

- **Java**: ported by hand from the 1.20.1 sources (21k lines), package layout and class names kept. Mechanical renames
  were scripted (`tools/port_java.py`: imports, registries, `RegistryObject` → `DeferredHolder`, resource locations;
  `tools/port_nbt.py`: item NBT calls → `util/ItemNbt`), the rest by hand. The datagen classes were not ported: their
  1.20.1 output is the source of the resources.
- **Resources**: `python3 -I tools/convert_resources.py <1.20.1 checkout> src/main/resources` rebuilds `assets/` and
  `data/` from the 1.20.1 resources and datagen output (1.21 folder names, `forge:` → `c:` tags, `neoforge:conditions`,
  results `item` → `id`, advancement predicates and icons, enchantments as data, enchantable tags, the fixes below).
  Translations: `python3 -I tools/gen_translations.py <1.20.1 checkout>` writes `tools/overlay/.../lang`, which the
  converter merges. Never edit `src/main/resources/{assets,data}` by hand.
- **Tests** (long, run in the background): `tools/servertest.sh` (dev server, fresh world), `tools/scenetest.sh`
  (client scene `port/DevScene`: screenshots + functional checks), `tools/stresstest.sh` (`port/StressScene`: rejoin,
  1.20.1 items, packet spam, load), `tools/compattest.sh` (the two scenes with popular mods), `prodtest/serve.sh` (the
  release jar on a real NeoForge server). The scenes are inert unless their system property is set.

## Changes forced by 1.21 (same behaviour)

- **Item NBT → data components.** Everything the mod kept in item NBT (oil, throwing ammo, quiver settings, heavy
  crossbow state, ...) lives in `minecraft:custom_data` with the same keys (`util/ItemNbt`), so 1.20.1 items convert
  through the game's data fixer and keep their state. Exceptions with a vanilla component: projectile potions use
  `minecraft:potion_contents` (the 1.20.1 `"Potion"` key is still read), the loaded heavy crossbow bolt uses
  `minecraft:charged_projectiles` (the 1.20.1 `"Projectile"` key is still read), quiver contents use
  `spartanweaponry:quiver_contents` (1.20.1 `"Ammo"` lists are moved over the first time the quiver ticks or is used).
- **Enchantments are data** (`data/spartanweaponry/enchantment/`): the nine enchantments with the 1.20.1 costs, max
  levels, rarity (weight / anvil cost) and exclusivity (Incendiary × Hydrodynamic, Lucky Throw × Silk Touch); the
  effects stay in Java. Supported items are tags (`#spartanweaponry:enchantable/throwing_weapon`, `heavy_crossbow`,
  `boomerang`). Vanilla enchantments follow the 1.20.1 item classes: melee weapons are in `#minecraft:enchantable/sword`
  and `sharp_weapon` (Sweeping Edge only with the Sweep I trait, as before), longbows in `enchantable/bow`, heavy
  crossbows in `enchantable/crossbow`, every weapon in `enchantable/durability`; throwing weapons accept Loyalty.
- **Lucky Throw**: 1.20.1 set the Looting level of a kill by a thrown weapon (`LootingLevelEvent`, gone). 1.21 loot
  reads Looting from the killer's equipment; `mixin/EnchantmentHelperMixin` answers with the Lucky Throw level while
  the drops of such a kill are made.
- **Damage events**: `LivingHurtEvent` / `LivingAttackEvent` → `LivingIncomingDamageEvent` (parry at high priority);
  oils are only used by hits that are not cancelled by the hurt cooldown.
- **Longbows** pass themselves as the arrow's weapon, so vanilla Power / Punch / Flame / Piercing work through the 1.21
  enchantment effects; the mod's arrows and bolts are in `#minecraft:arrows` (entity type tag) for that. The heavy
  crossbow keeps its own enchantment code (as in 1.20.1).
- **Attributes**: weapon modifiers are built on every config / data reload and returned as the item's default
  attribute modifiers (main hand); Reach uses `minecraft:player.entity_interaction_range` (1.20.1: Forge reach).
- **Registries**: weapon traits and oil effects are NeoForge registries (synced); conditions, loot modifiers, loot
  conditions, argument types, criteria and recipe serializers use deferred registers and codecs.
- **Network**: custom payloads (quiver access, button, priority slot).
- **Client**: GUI layers instead of overlays, menu screens via `RegisterMenuScreensEvent`, ARGB item tints, item
  properties registered in client setup, the oil coating model loader on the 1.21 geometry API, HUD sprites instead
  of `icons.png` / `widgets.png` (removed in 1.20.2: the quiver ammo HUD and the throwing attack indicator drew
  missing textures).
- **Tags**: `forge:` → `c:` (`c:ingots/*`, `c:rods/wooden`, `c:strings`, `c:leathers`, `c:cobblestones`,
  `c:foods/raw_meat`, ...); the mod's own `forge:` tags (`creepers`, `ender`, `humanoids`, `grass`, `heads`, `weapons`)
  became `c:` tags with the same contents (`minecraft:grass` → `minecraft:short_grass`); melee and throwing weapons are
  also in `c:tools/melee_weapon`, longbows and heavy crossbows in `c:tools/ranged_weapon`.
- **Compat**: JEI 19 (recipe holders, subtype interpreters), EMI 1.1, Curios 9 (quiver in the back slot), Shoulder
  Surfing 4.x and 5.x (5.x replaced the registrar with an event bus; the plugin has both entry points). The
  Apotheosis fletching recipes were dropped: Apotheosis for 1.21.1 has no fletching table. The JEED effect recipe uses
  the JEED 1.21 format.

## Bugs of the original, fixed

Issues of github.com/ObliviousSpartan/SpartanWeaponry:

- [#149](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/149) Constantan used the Invar material traits
  (`ModWeaponTraitTags.CONSTANTAN` pointed at `materials/invar`). Constantan has its own (empty) traits tag.
- [#167](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/167) Longbow durability ignored the material
  config. [#183](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/183) Thrown weapons ignored the damage and
  speed config (they kept the values of the first load). Both now follow the config on every reload.
- [#168](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/168) Dropped battleaxes were twice the size of a
  sword (16 px textures with the long weapons' ground transform).
- [#169](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/169) Two-handed weapons removed Mining Fatigue of
  infinite duration from any source.
- [#170](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/170),
  [#148](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/148),
  [#160](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/160),
  [#178](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/178),
  [#196](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/196),
  [#201](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/201),
  [#203](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/203) crashes with C2ME / threaded world generation:
  mobs spawned with weapons during structure generation picked the weapon with the level's random (owned by the
  server thread). They use the mob's own random now.
- [#172](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/172) The mod's enchantments had no supported items
  for other mods to see: they are data now, with item tags.
- [#185](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/185) Skeletons spawned with longbows: verified that
  they shoot (the scene gives one a longbow and a target).
- [#186](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/186) A thrown weapon dealt its full damage again
  every time the hurt cooldown ran out (thrown down onto a mob it stayed on its head and "melted" it). A throw now
  damages each mob once, like a trident; a boomerang may hit again after it turns back or bounces off a block (its
  return / bounce kills stay possible).
- [#188](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/188) Arrows and bolts picked up twice: not
  reproducible in the port (the scene shoots an arrow into the ground and picks it up: one arrow back).
- [#193](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/193) Quiver screen broken after an upgrade: the
  small and huge quiver screens render (screenshots in the scene).
- [#165](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/165) Heavy crossbow bolts black after rejoining,
  and thrown weapons / bolts lost on reload: bolts and thrown weapons were not saved with their item. They are saved now
  (the stress scene rejoins and finds them, with their items).

Found while porting:

- **Quiver `isEmpty` was inverted**, so the quiver moved the off-hand item away even when it was empty.
- **Material traits in the tooltip**: weapons listed every trait of their material, also those for other weapon kinds.
- **Copper arrows and bolts** called lightning in any weather when tipped (missing brackets), and credited the hit mob
  with the lightning instead of the shooter.
- **Oil commands**: the oil suggestions never showed (`||` instead of `&&`), `potion` was accepted as a plain oil, and
  setting a potion oil read the potion from the wrong NBT place.
- **Villager trades**: an off-by-one index could pick past the end of the list (crash).
- **Quiver priority slot packet** took any slot index from the client; it is checked against the quiver size. The
  quiver menu also refuses swapping the open quiver with the hotbar / off-hand keys.
- **Oil coated item models** baked the coating layer with the face data of the base layer.
- **Damage Bonus `max_armor_value`** had a default (0) below its minimum (1), so the config was "corrected" on every
  start.
- **Quiver key on a mouse button** sent two packets per click (the Pre and Post mouse events) and compared the button
  with key bindings of any type.

## Issues of the original reviewed, not changed

- [#162](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/162) traits can't be removed by data packs: tags
  merge unless the data pack says `"replace": true`; with it the traits are replaced (checked in the scene).
- [#159](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/159) Loyalty on daggers, [#152](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/152)
  off hand with Two-Handed I, [#166](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/166) scythes mining hoe
  blocks, [#179](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/179) quivers following player animation
  packs, [#184](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/184) reach config: requests, not bugs.
- [#158](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/158) silver durability, [#177](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/177)
  low damage: the values are the config defaults (both can be changed in the config, which now applies to every weapon).
- [#197](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/197) oil not applying: the oil goes on the weapon in
  the other hand (works in the scene).
- [#192](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/192) crash with a heavy crossbow in Shoulder Surfing
  (1.18.2): not reproducible with Shoulder Surfing 4.x / 5.x on 1.21.1 (compatibility run).
- [#200](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/200), [#189](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/189),
  [#191](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/191), [#156](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/156),
  [#154](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/154): no log or a log without the mod's code; the
  mod does not touch dungeon loot. [#204](https://github.com/ObliviousSpartan/SpartanWeaponry/issues/204) (1.19.2):
  the config is no longer applied before the items exist.
- 1.12.2 / 1.18.2 issues (#202, #194, #182, #181, #176, #174, #157, #155, #150, #133) are about those versions.

## Languages

1.20.1 had en_us, es_es, es_mx, fr_fr, pt_br, ru_ru, zh_cn. `tools/gen_translations.py` adds **uk_ua** and **de_de**
(complete) and completes ru_ru, es_es, pt_br and zh_cn: their missing keys (quiver upgrades, materials, oils, newer
potions), about 120 pt_br strings that were still English, and 1.20.1 strings whose placeholders did not match the
English ones (they showed broken text: the Russian longbow / heavy crossbow tooltips, the Spanish and Portuguese oil
tooltips) or named the wrong material. Weapon names are built from the material and the weapon word; vanilla potion
names follow the game's own translations; the 1.21 potions (wind charged, weaving, oozing, infested) got names in every
language. Machine translations (no native speaker checked them). The existing translations are kept.

**Item tag names** (2026-10-15): the mod's 73 item tags (`#spartanweaponry:longswords`, `#spartanweaponry:iron_weapons`,
quivers, projectiles, mob spawn weapons, ...) plus `#c:weapons` and `#c:grass` had no names, so EMI showed raw tag ids
in recipes and logged 144 "Untranslated tag" errors. `tools/tag_names.py` names them (`tag.item.<namespace>.<path>`) in
all nine languages (es_mx uses the Spanish names); `gen_translations.py` writes them into the overlay. Checked with EMI:
no untranslated tag of the mod left (`#curios:back` / `#curios:quiver` are Curios' slot tags, named by Curios).

## Not changed on purpose

- A thrown weapon loses its enchantments when it lands (1.20.1 anti-dupe for ammo stacks); the item is kept.
- Sweeping Edge needs the Sweep I trait (katana, ...), not Sweep II / III.
- The Lucky Throw level replaces the killer's Looting for kills by a thrown weapon (1.20.1 set the level).

## Verified (2026-10-15)

- `./gradlew build`: jar `SpartanWeaponry-neoforge-3.2.1+1.21.1.jar` (LGPL, GPL and the original's Apache texts inside;
  the test scenes are left out).
- Dev server, fresh world: starts clean, no recipe / advancement / tag / enchantment / loot errors.
- Client scene (`tools/scenetest.sh`, 54 checks, all pass, in English and in Russian): longbow, heavy crossbow (load,
  aim, fire) and javelin with real client input; arrow and javelin pickup (one back, #188); quiver to off hand on bow
  select, quiver in the Curios back slot, quiver screens (#193); weapon oil applied, undead bonus, one use per hit; katana
  damage, Armour Piercing (warhammer: 4.2 vs 1.4 through diamond armour), Reach, Two-Handed, #169, parrying dagger
  block, Decapitate (about 25 %), Lucky Throw III (Looting III drops), #186 (one hit when thrown down); a skeleton with a
  longbow shoots (#185); Ender Disruption stops teleports; explosive arrow; recipes, tag smelting, enchanting table
  offers per weapon kind, Loyalty on throwing weapons, village chest loot; a data pack with `"replace": true` replaces
  the warhammer traits (#162); JEED recipe.
- Stress scene (`tools/stresstest.sh`, 20 checks, all pass): leave and rejoin keeps oils, potion oil, loaded bolt,
  throwing ammo, enchantments, quiver contents, tipped bolts in the inventory and in a chest, and the thrown javelin,
  diamond bolt (not black, #165) and tipped arrow in the ground; 1.20.1 items through the data fixer (oiled katana with
  damage, potion oil, tipped bolt, throwing knives, loaded heavy crossbow, quiver contents); 600 quiver packets with bad
  slot numbers (client stays connected); 400 armor stands with 800 weapons (half oiled) + 160 item frames: 39-40 FPS,
  the same stands with vanilla swords 52 FPS (the layered weapon models; 31-37 before the oil model fix); 40
  skeletons with longbows and 40 armed zombies against 30 golems: server tick 2-3 ms; 600 projectiles: tick 2 ms.
- Compatibility (`tools/compattest.sh`): Sodium 0.6.13, Iris 1.8.12 with Complementary Reimagined r5.9.3, Lithium
  0.15.4, FerriteCore 7.0.3, ModernFix 5.27.26, Jade 15.10.6, JEI 19.64, EMI 1.1.24, JEED 2.3.5, Curios 9.5.1 and
  Shoulder Surfing 5.2.0 or 4.5.1: the client scene (all checks, plus a loaded heavy crossbow in the shoulder view, #192)
  and the stress scene pass. The only errors in those logs are the shader pack's own and Iris' optional mixins for mods
  that are not installed.
- Release jar on real NeoForge servers 21.1.259 and 21.1.197 (`prodtest/serve.sh`): starts clean.
- Not run: a dedicated client joining the dedicated server, C2ME (not on NeoForge 1.21.1 in the test set; the fix is the
  random source), Better Combat.

## Performance check (2026-10-17)

Stress scene (`tools/stresstest.sh`) with a JFR recording (`JDK_JAVA_OPTIONS="-XX:StartFlightRecording=filename=run/x-%p.jfr,settings=profile,dumponexit=true"`).

- **Oil coating colour**: the item colour handler runs for every quad of the coating layer of every oiled weapon on
  screen, every frame, and made an oil capability handler (reading the item's NBT) each time: 2.4 % of the render
  thread with 400 armor stands (half oiled). It now reads the oil tag in place and keeps the colour of the last item
  data (the quads of one weapon come one after another): 0.4 %.
- **Item NBT copies**: `ItemNbt.get` copies the whole tag; 56 places only read one value from it (throwing weapon ammo
  every tick and every frame for the "empty" model, the crosshair HUDs, the quiver, the heavy crossbow ...). They use
  `ItemNbt.read` now (the component's own tag, read only). Copies stay where the tag is changed and in the one-time
  migration of 1.20.1 data. Throwing weapons no longer copy their tag every tick to check that it is complete.
- The thrown weapon renderer makes its fixed rotation once instead of every frame.
- Measured (noisy laptop, same scene): 400 armor stands with 800 weapons + 160 frames 42-46 FPS (39-50 before), the
  same stands with vanilla swords 60-62. The rest of that gap is the original art: 165 weapon textures are 32x32 or
  64x64, and a generated item model has more faces the bigger its sprite; the oil coating is one more pass on oiled
  weapons (not drawn on the others). The server tick of that scene (6-13 ms, vanilla swords 4.5-4.9 ms) is mostly the
  water spreading in the test world: Spartan Weaponry code is about 1 % of the server thread.
- Client scene and stress scene: all checks pass.

## Multiloader: NeoForge module (2026-10-21, branch `multiloader`)

- The code is split into `common` (no NeoForge), `neoforge` and `fabric` (in progress). The NeoForge module wires the
  common handlers to the same events, priorities and registries as the NeoForge-only port.
- Client scene (`LOADER=neoforge sh tools/scenetest.sh`): 54 checks, 0 failed. Stress scene: 20 checks, 0 failed.
  Dev server starts without errors.
- Measured (MacBook, stress scene): 400 armor stands with weapons (half oiled) + 160 frames 41-49 FPS, server tick
  6.7-9.6 ms; the same stands with vanilla swords 58 FPS, 4.5 ms; 40 skeletons + 40 zombies vs 30 golems 107 FPS,
  2.2 ms; 600 projectiles 99 FPS, 1.8 ms. The same as the NeoForge-only port.

## Multiloader: Fabric module (2026-10-21, branch `multiloader`)

- Fabric 1.21.1 (Fabric API 0.116.17, Forge Config API Port 21.1.6): the NeoForge hooks the mod relies on are
  Fabric API events or mixins (`fabric/src/main/java/.../fabric/mixin`); the oil coating is a Model Loading API
  wrapper; the decapitate loot modifiers read the same `loot_modifiers` JSON.
- Fixes of the original found on the way (both loaders): the weaponsmith trades kept the empty item lists of before
  the config was read (1.20.1 made new lists; picking such a trade threw), now the same lists are filled and an empty
  level gives no trade; the extended heads' block entity checked the vanilla skull type (`isValidBlockState`).
- Client scene: Fabric 52 checks (the 2 Curios checks are skipped), NeoForge 54; 0 failed. Stress scene 20/20 on
  both. New scene shot `scene_oil_coating.png` (plain and oiled weapons) looks the same on both loaders.
- Measured (MacBook, stress scene), Fabric / NeoForge: 400 armor stands with weapons (half oiled) + 160 frames
  37-50 / 38-47 FPS, 6.2-9.8 / 8.6-11.3 ms server tick; vanilla swords baseline 72-75 / 60 FPS; 80 mobs vs golems
  89-104 / 103 FPS, 2.0-2.2 ms; 600 projectiles 92-102 / 99 FPS, 1.7-1.9 ms.


## NeoForge and Fabric release check (2026-10-22, 3.2.1.2)

- Release jar on real NeoForge servers 21.1.259 and 21.1.197 and on a dedicated Fabric server: starts clean. The scene on a
  Fabric production client (intermediary names): 53/53 (the stress scene 20/20 there before the repair fix below). The test scenes used reflection with
  Mojang names (`attackStrengthTicker`, `EnderMan#teleport`, `CreativeModeInventoryScreen#selectTab`), which fails
  there: the first two are opened by the access transformer and widener, `selectTab` is found by its signature.
- Compatibility on both loaders (Sodium 0.6.13, Iris 1.8.8 on Fabric with Complementary Reimagined r5.9.3, Lithium,
  FerriteCore, ModernFix, Jade, JEED, Shoulder Surfing 4.5; JEI and Curios on NeoForge, EMI on Fabric): scene 58/58 on
  NeoForge, 56/56 on Fabric (the Curios checks are NeoForge only), with Shoulder Surfing 4 the same.
- Fixed for Fabric on the way:
  - `WeaponMaterial#getRepairIngredient` returned a tag ingredient even for an empty or unknown tag. Vanilla
    `Ingredient#isEmpty` only counts the tag values (NeoForge counts the items), so EMI read the first item of an empty
    array: 198 "Exception thrown registering repair recipes". Now it gives `Ingredient.EMPTY` for those.
  - The ingredient was kept in a field. A vanilla `Ingredient` keeps the items of its tag from its first look, so a
    data pack that filled the tag later was not seen for repairs. Now it is made on each call (only anvils and
    repair checks ask for it).
  - The `emi` entrypoint pointed at the EMI plugin, which is commented out as in the original. Removed.
  - The JEED Ender Disruption recipe used NeoForge's components ingredient. It now has a Fabric copy with
    `fabric:components`, and each loader loads only its own file.
  - JEED for Fabric 2.3.4+ is built with Loom 1.17, which Loom 1.13 cannot remap, so the compatibility run uses 2.3.3.
- No stress scene and no FPS measurements on the compatibility run (owner's call).
- Later the same day: Loom's `ClientProductionRunTask` also puts the project's `remapJar` into `mods`, so the scenes
  jar and the release jar ran together (Fabric happened to load the scenes jar). The task now uses `mods.setFrom`, and the
  production runs were repeated with only the scenes jar: all checks pass. `fabric.mod.json` in the jar is valid JSON
  now (the description's `\n` had become a raw newline when Gradle expanded the template).

## 1.21.11 (branch mc/1.21.11, 2026-10-22, 3.2.1.2+1.21.11)

NeoForge 21.11.48, Fabric Loader 0.19.5 + Fabric API 0.141.6, Forge Config API Port 21.11.1, Curios 14.0.0, JEI 27.53,
Fabric Loom 1.17.21 (Gradle 9.8.1). No Parchment for 1.21.11; EMI, JEED, ModernFix and Shoulder Surfing 4.x have no
1.21.11 version, so their integrations are not tested here (the data stays).

- **Items**: the id goes into the properties, so every item gets properties of its own (the weapon sets shared one
  object per set before, which now gave the items each other's ids). Repair and enchantability are properties; swords
  are items with the sword properties of their material (no SwordItem), with no attribute component of their own so the
  mod's per-stack attributes (config, weapon_attributes data) still apply. Tooltips go through a Consumer.
- **Looks from item definitions** (`assets/*/items`, made by `tools/port/gen_item_definitions.py` from the 1.21.1 models
  and overrides): blocking, throwing, empty, charged and oiled conditions, pull and ammo ranges, potion and oil tints.
  The oil coating is no longer a model loader: an oiled weapon draws its model plus a coating model tinted with the oil;
  a plain weapon draws only its model. The vanilla swords, which can be oiled too, get definitions the same way.
- **Rendering**: arrows, bolts and throwing weapons with render states (the bolt's own quads as one custom geometry
  submission), quiver models with the arrow count as their state, the extra mob heads through the skull model hooks
  (NeoForge CreateSkullModels, a Fabric mixin into SkullBlockRenderer#createModel), HUD with GUI pipelines (the
  crosshairs invert as vanilla's; the debug 3D crosshair is the world's now), tooltip components, buttons.
- **Recipes**: string ingredients (modding-kb `scripts/ingredients_1_21_2.py`); the tag cooking recipes are cooking
  recipes of their own with a tag slot display; the quiver upgrades are vanilla smithing transforms (1.21's transform
  copies the quiver contents). `spartanweaponry:tag_not_empty` reads the tags of the reload (the named sets of a first
  load are unbound when the conditions run); NeoForge's own `not tag_empty` only asks whether a tag exists.
  `neoforge:false` is `neoforge:never` in 21.11.
- **Enchanting**: the swords enchantable tag is `enchantable/melee_weapon` in 1.21.11, Sweeping Edge has
  `enchantable/sweeping`. Shield Breach disables shields through LivingEntity#getSecondsToDisableBlocking (the weapon
  component), on both loaders.
- **Mixins** on the 1.21.11 methods: equipment in LivingEntity, Player#doSweepAttack / createAttackSource /
  isSweepAttack, AbstractSkeleton#canUseNonMeleeWeapon, the chorus fruit teleport in its consume effect (Fabric),
  LivingEntity#hurtServer (Fabric). Overloaded methods are named with their descriptors (a bare name selects one).
- JEI 27 (subtype data, the tipped projectile recipes through the vanilla recipe factory), Curios 14 (submit node
  collector), Forge Config API Port v5.
- **Verified**: client scene 55 checks on NeoForge, 53 on Fabric (Curios ones are NeoForge only), all pass; release jar
  on a real NeoForge 21.11.48 server and a dedicated Fabric server; scene and stress scene on a Fabric production client;
  compatibility runs with Sodium 0.8.14, Iris 1.10.8, Lithium, FerriteCore, Jade, Shoulder Surfing 5.2 (Fabric with the
  Complementary shaders; NeoForge without a shader pack, Iris 1.10.8's NeoForge shadow pass crashes on enchanted items).
  No stress scene with FPS numbers on the compatibility runs (owner's call).
