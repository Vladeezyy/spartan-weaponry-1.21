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
- **`./gradlew :common:compileJava` собирается (2026-10-21).**
- **NeoForge 1.21.1 готов (2026-10-21):** `neoforge/…/neoforge/SpartanWeaponryNeoForge` (реестры, NeoForge-реестры
  `decapitate`/`type_disabled`/аргументы команд, payload'ы, capabilities `oil`/`quiver_items` для других модов,
  конфиги, все игровые события), `SpartanWeaponryNeoForgeClient` (клиентские регистрации и события),
  `NeoForgePlatform` (+ `META-INF/services`), `neoforge/mixin/ItemStackMixin` (ItemAbility → ToolAction), AT кладётся в
  jar из `common/accesstransformer.cfg`. Сцена 54/54, стресс 20/20, dev-сервер стартует без ошибок. Не сделано:
  prod-прогон jar (`prodtest/serve.sh`), compat-прогон (`tools/compattest.sh` ещё под старую раскладку).
- Тест-скрипты: `LOADER=neoforge|fabric sh tools/scenetest.sh` (stresstest/servertest), общий `tools/common.sh`;
  игра запускается в `<loader>/run`, логи `build-<scene|stress|server>-<loader>.log`.
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
- **Fabric 1.21.1 готов (2026-10-21):** `fabric/…/fabric/SpartanWeaponryFabric` (регистрация в порядке
  BuiltInRegistries, конфиги через Forge Config API Port — слушатели до register, payload'ы, условия рецептов
  `type_disabled` + `tag_not_empty`, команды и аргументы, сделки на первом старте сервера, лут, правый клик, теги,
  чары через EnchantmentEvents), `SpartanWeaponryFabricClient` (клиент, HUD, тултипы, частицы, ключ колчана по
  тику), `FabricPlatform`, `DecapitationModifiers` (те же JSON `loot_modifiers`), `OilCoatingModels` (слой масла
  через Model Loading API), миксины в `fabric/mixin` (урон, смерть/дроп, блок, щит, взмах, атрибуты, прочность,
  наковальня, телепорты, подбор, брожение, лут, лук у мобов, позы арбалета, головы, прицел, подпись арбалета,
  картинка масла в тултипе). Сцена 52/52 (2 проверки Curios на Fabric пропускаются), стресс 20/20, dev-сервер
  стартует. Условия Fabric дописаны в 923 JSON скриптом `tools/port/neo2fabric_conditions.py`.
- Нет на Fabric: слот колчана (Curios; можно Trinkets), `customArrow`/`onArrowLoose` других модов.
  Один прогон сцены Fabric показал «a hit uses the oil once (18/20)», три следующих — 19/20; не воспроизвелось.
- Не сделано для обоих загрузчиков: prod-прогон jar, compat-прогон (`tools/compattest.sh` под старую раскладку),
  README, merge в main, релиз.
- **Arcana готова на NeoForge + Fabric 1.21.1 (2026-10-21)**: `spartan-weaponry-arcana-1.21@multiloader`, та же
  раскладка, свои `platform/Platform` и `Registrar`. Сцена 22/22 без Botania и 34/34 с ней, сетевой тест 6/6, на
  обоих загрузчиках. На Fabric Arcana стартует из entrypoint `spartanweaponry`, его вызывает Weaponry в конце своего
  `onInitialize` (`SpartanWeaponryFabric`). Botania: локальные сборки ветки 1.21.1-porting
  (`~/Desktop/mod-scout/src/Botania`, `:Fabric:remapJar`, `:NeoForge:build`) в `arcana/libs/botania/{xplat,neoforge,fabric}`
  (не в git); Fabric-сборке нужны Loom 1.15.5 и Loader 0.17.3. Jar'ы Weaponry в `arcana/libs/` пересобирать после
  изменений Weaponry (`:common:jar :neoforge:jar :fabric:remapJar`).
- **HUD Baubles готов на NeoForge (Curios) + Fabric (Trinkets) 1.21.1 (2026-10-22)**:
  `spartan-hud-baubles-1.21@multiloader`. Сцена 84/84, стресс 13/13, сеть 6/6 на обоих. Аксессуары за
  `platform/Platform`, тестовые слоты сцен за `port/SceneAccessories`. На Fabric: HUD — миксин в конце
  `Gui.renderExperienceLevel`, конфиг и экран — Forge Config API Port, событие для модов — `RenderBaubleOnHUDCallback`.
  `TranslatableEnum` в common — заглушка `common/src/stubs` (API-jar FCAP в intermediary).
- **Все четыре мода на 1.21.1 готовы на обоих загрузчиках.** Релиз 1.21.1 (2026-10-22), без стресс-тестов и замеров FPS
  (решение владельца):
  - prod (`NO_STRESS=1 sh tools/prodtest.sh`: NeoForge 21.1.197/259, Fabric-сервер, production-клиент Fabric):
    - пройден: Weaponry (53/53), Shields (20/20), Arcana (с Botania и без: 34/34, 22/22);
    - **не сделан: HUD Baubles**.
  - compat (`NO_STRESS=1 LOADER=neoforge|fabric sh tools/compattest.sh`):
    - пройден: Weaponry (NeoForge 58/58, Fabric 56/56, оба и с SS4), Shields (20/20 оба), Arcana (34/34 оба);
    - **не сделан: HUD Baubles**.
  - Осталось:
    - тесты HUD Baubles;
    - секции «Tested» в README (EN + RU) всех четырёх;
    - PORTLOG;
    - merge `multiloader` → `main`;
    - GitHub-релизы (теги `v<версия>+1.21.1`, neoforge- и fabric-jar без `-scenes`, заметки EN; у Arcana — нужен
      Weaponry 3.2.1.2+).
  - Arcana: в `libs/` — jar Weaponry 3.2.1.2 этой ветки, пересобрать и скопировать при изменении Weaponry.
- Потом: ветки версий `mc/1.21.11`, `mc/26.1.2`, `mc/26.2`, `mc/26.3` для всех четырёх (как в iron-backpacks-1.21;
  HUD Baubles на Fabric — только 1.21.1). Weaponry на Fabric: слота колчана нет (Curios → можно Trinkets).

## Сборка / тесты
JDK: Windows `export JAVA_HOME=~/.gradle/jdks/eclipse_adoptium-21-amd64-windows.2`, Mac
`export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.12.1/libexec/openjdk.jdk/Contents/Home`. `./gradlew :common:compileJava`.
Тест-скрипты Shields: `LOADER=neoforge|fabric sh tools/scenetest.sh` (stresstest/servertest). Грабли — в
modding-kb `porting/multiloader.md` (раздел Spartan Shields).
