# HANDOFF: Spartan Weaponry → NeoForge + Fabric (ветка `multiloader`)

Задача владельца (2026-10-21): все 4 Spartan-мода (Weaponry, Shields, Arcana, HUD Baubles) на Fabric 1.21.1,
затем NeoForge + Fabric на 1.21.11, 26.1.2, 26.2, 26.3 (ветки `mc/<версия>`, как в iron-backpacks-1.21).
HUD Baubles на Fabric — только 1.21.1 с Trinkets. Порядок: по версиям (сначала всё на 1.21.1).

## Готово
- **Shields** (`spartan-shields-1.21@multiloader`): common/neoforge/fabric, Fabric 1.21.1 работает;
  сцена 20/20 и стресс 22/22 на обоих загрузчиках. Не сделано: README, compat-прогон на Fabric (Sodium/Iris),
  prod-сервер Fabric, merge в main, релиз.

## Weaponry — в процессе
- Раскладка: `common/` (код без NeoForge), `neoforge/`, `fabric/` (пока только build.gradle и AW).
- **`./gradlew :common:compileJava` собирается (2026-10-21).** neoforge/fabric модули ещё не собираются.
- Сделано в common: `platform/Registrar`, `platform/Platform` (интерфейс, реализаций нет), свои `IItemHandler` /
  `ComponentItemHandler` / `SlotItemHandler`, `ToolAction`, `event/LoaderEvents`, обработчики переписаны на них;
  `ModSpartanWeaponry` = `init()` / `setup()` / `clientSetup(ItemPropertyRegistrar)` (как в Shields);
  `Config.onConfigChanged(fileName, spec)`; `TypeDisabledCondition.test()` без аргументов;
  `MeleeBlockWeaponTrait.cancelsBlock(living, source)`; `item/crafting/BrewingRecipe` (свой IBrewingRecipe),
  `ModOilRecipes.initOilRecipes(Consumer<BrewingRecipe>)`; `loot/Decapitation.apply(...)` (правило черепов);
  NeoForge-методы предметов без `@Override` (`// NeoForge IItemExtension`), super → `item/ItemExtensions`;
  `Platform.creativeTabBuilder(after)`, сцены: `DevScene/StressScene.onTick()` без события,
  `DevScene.accessorySlot` (Curios ставит `port/NeoForgeSceneHooks`). AT/AW: `SKIN_BY_TYPE`,
  `MenuScreens$ScreenConstructor`, `BlockEntityType$BlockEntitySupplier`, `ItemEntity.target`, `Minecraft.itemColors`.
- Уже в neoforge (ждут модуля): CurioRenderer, OilCoated*Model, ModModelLoaders, `neoforge/mixin/ItemModelGeneratorMixin`
  (свой `spartanweaponry.neoforge.mixins.json`, добавлен в mods.toml), `loot/DecapitateLootModifier` (зовёт
  `Decapitation`), `port/NeoForgeSceneHooks`.
- NeoForge-модулю зарегистрировать то, что было в DeferredRegister'ах NeoForge-реестров: GLM `decapitate`
  (`DecapitateLootModifier.CODEC`), условие рецептов `type_disabled` (обёртка `ICondition` над
  `TypeDisabledCondition`), аргументы команд `oil`/`potion` (`ModCommands.registerArgumentTypes`), конфиги
  (`registerConfig` + `ModConfigEvent` → `Config.onConfigChanged`), payload'ы, capabilities, брожение
  (`RegisterBrewingRecipesEvent` → обёртка `IBrewingRecipe`), `LivingShieldBlockEvent` → `cancelsBlock`,
  `onArrowLoose`, `customArrow` (в common вызов убран: по умолчанию NeoForge возвращает стрелу как есть).
- Fabric: те же методы предметов звать миксинами (getMaxDamage, атрибуты, damageItem, supportsEnchantment,
  isPrimaryItemFor, canDisableShield, doesSneakBypassUse, getHighlightTip, getEnchantmentValue(stack));
  `Decapitation` из миксина в LootTable по данным `loot_modifiers`; брожение — миксин в PotionBrewing.
- Потом: neoforge-модуль (entry point, NeoForgePlatform, подписка событий, миксин canPerformAction(ItemAbility)),
  fabric-модуль (entry points, FabricPlatform, миксины: урон LivingEntity.hurt через @WrapMethod, sweep в
  Player.attack, canDisableShield, isDamageSourceBlocked для melee block, getMaxDamage/атрибуты в ItemStack,
  анвил, телепорты, подбор в колчан, брожение, черепа, прицел), условия рецептов скриптом
  `tools/port/neo2fabric_conditions.py <res> spartanweaponry spartanweaponry:type_disabled`.
- Затем Arcana и HUD Baubles, потом ветки версий.

## Сборка / тесты
JDK: Windows `export JAVA_HOME=~/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2`, Mac
`export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.12.1/libexec/openjdk.jdk/Contents/Home`. `./gradlew :common:compileJava`.
Тест-скрипты Shields: `LOADER=neoforge|fabric sh tools/scenetest.sh` (stresstest/servertest). Грабли — в
modding-kb `porting/multiloader.md` (раздел Spartan Shields).
