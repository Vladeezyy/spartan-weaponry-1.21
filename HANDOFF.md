# HANDOFF: Spartan Weaponry → NeoForge + Fabric (ветка `multiloader`)

Задача владельца (2026-10-21): все 4 Spartan-мода (Weaponry, Shields, Arcana, HUD Baubles) на Fabric 1.21.1,
затем NeoForge + Fabric на 1.21.11, 26.1.2, 26.2, 26.3 (ветки `mc/<версия>`, как в iron-backpacks-1.21).
HUD Baubles на Fabric — только 1.21.1 с Trinkets. Порядок: по версиям (сначала всё на 1.21.1).

## Готово
- **Shields** (`spartan-shields-1.21@multiloader`): common/neoforge/fabric, Fabric 1.21.1 работает;
  сцена 20/20 и стресс 22/22 на обоих загрузчиках. Не сделано: README, compat-прогон на Fabric (Sodium/Iris),
  prod-сервер Fabric, merge в main, релиз.

## Weaponry — в процессе (этот коммит НЕ собирается)
- Раскладка: `common/` (код без NeoForge), `neoforge/`, `fabric/` (пока только build.gradle и AW).
- Сделано в common: `platform/Registrar` (вместо DeferredRegister), `platform/Platform` (интерфейс, реализаций
  ещё нет), `capability/IItemHandler`, `ComponentItemHandler`, `inventory/SlotItemHandler` (свои вместо NeoForge),
  `init/ModCapabilities` = `getOil(stack)` / `getQuiver(stack)`, `api/ToolAction` вместо `ItemAbility`,
  `event/LoaderEvents` (мини-события с методами NeoForge), CommonEventHandler / ClientEventHandler / ClientHelper /
  NetworkHandler (`PacketContext`) / KeyBinds / ModCommands / ModParticles / ModMenus переписаны на
  загрузчико-нейтральные методы.
- Перенесены в neoforge: CurioRenderer, OilCoatedItemModel, OilCoatingItemBakedModel, ModModelLoaders,
  ItemModelGeneratorMixin (убрать его из common mixins json).
- Осталось в common: `TypeDisabledCondition` (убрать `implements ICondition`, `test()` без аргументов),
  ModSpartanWeaponry (разнести на common init + entry points), Config/ClientConfig (события конфига),
  предметы (`@Override` у NeoForge-расширений убрать: getMaxDamage, damageItem, getDefaultAttributeModifiers,
  supportsEnchantment, isPrimaryItemFor, canDisableShield, doesSneakBypassUse, getHighlightTip, isInfinite,
  canApplyAtEnchantingTable, getEnchantmentValue(stack)), LongbowItem `EventHooks.onArrowLoose` →
  `Platform.onArrowLoose`, брожение (свой IBrewingRecipe), DecapitateLootModifier (правило в common, GLM в
  neoforge, на Fabric миксин в LootTable), MeleeBlockWeaponTrait.onBlockEvent, AT: добавить `SKIN_BY_TYPE`.
- Потом: neoforge-модуль (entry point, NeoForgePlatform, подписка событий, миксин canPerformAction(ItemAbility)),
  fabric-модуль (entry points, FabricPlatform, миксины: урон LivingEntity.hurt через @WrapMethod, sweep в
  Player.attack, canDisableShield, isDamageSourceBlocked для melee block, getMaxDamage/атрибуты в ItemStack,
  анвил, телепорты, подбор в колчан, брожение, черепа, прицел), условия рецептов скриптом
  `tools/port/neo2fabric_conditions.py <res> spartanweaponry spartanweaponry:type_disabled`.
- Затем Arcana и HUD Baubles, потом ветки версий.

## Сборка / тесты
JDK: `export JAVA_HOME=~/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2`. `./gradlew :common:compileJava`.
Тест-скрипты Shields: `LOADER=neoforge|fabric sh tools/scenetest.sh` (stresstest/servertest). Грабли — в
modding-kb `porting/multiloader.md` (раздел Spartan Shields).
