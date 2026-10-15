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

## Not changed on purpose

- A thrown weapon loses its enchantments when it lands (1.20.1 anti-dupe for ammo stacks); the item is kept.
- Sweeping Edge needs the Sweep I trait (katana, ...), not Sweep II / III.
- The Lucky Throw level replaces the killer's Looting for kills by a thrown weapon (1.20.1 set the level).

## Verified

See the end of the file (filled in after the test runs).
