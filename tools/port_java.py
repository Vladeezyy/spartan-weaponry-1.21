#!/usr/bin/env python3
"""Mechanical Forge 1.20.1 -> NeoForge 1.21.1 rewrites for the Spartan Weaponry sources (idempotent).

    python3 -I tools/port_java.py src/main/java

Only renames whose meaning does not change. Everything with different behaviour (capabilities, network, enchantments,
recipes, item NBT) is ported by hand; see PORTLOG.md.
"""
import os
import re
import sys

# exact import renames (old fully qualified name -> new); None removes the import
IMPORTS = {
    'net.minecraftforge.api.distmarker.Dist': 'net.neoforged.api.distmarker.Dist',
    'net.minecraftforge.api.distmarker.OnlyIn': 'net.neoforged.api.distmarker.OnlyIn',
    'net.minecraftforge.eventbus.api.SubscribeEvent': 'net.neoforged.bus.api.SubscribeEvent',
    'net.minecraftforge.eventbus.api.IEventBus': 'net.neoforged.bus.api.IEventBus',
    'net.minecraftforge.eventbus.api.EventPriority': 'net.neoforged.bus.api.EventPriority',
    'net.minecraftforge.eventbus.api.Event': 'net.neoforged.bus.api.Event',
    'net.minecraftforge.fml.common.Mod': 'net.neoforged.fml.common.Mod',
    'net.minecraftforge.fml.common.Mod.EventBusSubscriber': 'net.neoforged.fml.common.EventBusSubscriber',
    'net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus': 'net.neoforged.fml.common.EventBusSubscriber.Bus',
    'net.minecraftforge.fml.ModList': 'net.neoforged.fml.ModList',
    'net.minecraftforge.fml.ModLoadingContext': 'net.neoforged.fml.ModLoadingContext',
    'net.minecraftforge.fml.config.ModConfig': 'net.neoforged.fml.config.ModConfig',
    'net.minecraftforge.fml.event.config.ModConfigEvent': 'net.neoforged.fml.event.config.ModConfigEvent',
    'net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent': 'net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent',
    'net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent': 'net.neoforged.fml.event.lifecycle.FMLClientSetupEvent',
    'net.minecraftforge.fml.loading.FMLEnvironment': 'net.neoforged.fml.loading.FMLEnvironment',
    'net.minecraftforge.fml.loading.FMLLoader': 'net.neoforged.fml.loading.FMLLoader',
    'net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext': None,
    'net.minecraftforge.common.MinecraftForge': 'net.neoforged.neoforge.common.NeoForge',
    'net.minecraftforge.common.ForgeConfigSpec': 'net.neoforged.neoforge.common.ModConfigSpec',
    'net.minecraftforge.common.ForgeConfigSpec.BooleanValue': 'net.neoforged.neoforge.common.ModConfigSpec.BooleanValue',
    'net.minecraftforge.common.ForgeConfigSpec.IntValue': 'net.neoforged.neoforge.common.ModConfigSpec.IntValue',
    'net.minecraftforge.common.ForgeConfigSpec.DoubleValue': 'net.neoforged.neoforge.common.ModConfigSpec.DoubleValue',
    'net.minecraftforge.common.ForgeConfigSpec.EnumValue': 'net.neoforged.neoforge.common.ModConfigSpec.EnumValue',
    'net.minecraftforge.common.ForgeConfigSpec.ConfigValue': 'net.neoforged.neoforge.common.ModConfigSpec.ConfigValue',
    'net.minecraftforge.common.ToolAction': 'net.neoforged.neoforge.common.ItemAbility',
    'net.minecraftforge.common.ToolActions': 'net.neoforged.neoforge.common.ItemAbilities',
    'net.minecraftforge.common.Tags': 'net.neoforged.neoforge.common.Tags',
    'net.minecraftforge.common.ForgeMod': 'net.neoforged.neoforge.common.NeoForgeMod',
    'net.minecraftforge.common.util.FakePlayer': 'net.neoforged.neoforge.common.util.FakePlayer',
    'net.minecraftforge.common.util.INBTSerializable': 'net.neoforged.neoforge.common.util.INBTSerializable',
    'net.minecraftforge.common.loot.IGlobalLootModifier': 'net.neoforged.neoforge.common.loot.IGlobalLootModifier',
    'net.minecraftforge.common.loot.LootModifier': 'net.neoforged.neoforge.common.loot.LootModifier',
    'net.minecraftforge.common.brewing.IBrewingRecipe': 'net.neoforged.neoforge.common.brewing.IBrewingRecipe',
    'net.minecraftforge.common.extensions.IForgeMenuType': 'net.neoforged.neoforge.common.extensions.IMenuTypeExtension',
    'net.minecraftforge.event.ForgeEventFactory': 'net.neoforged.neoforge.event.EventHooks',
    'net.minecraftforge.event.AnvilUpdateEvent': 'net.neoforged.neoforge.event.AnvilUpdateEvent',
    'net.minecraftforge.event.LootTableLoadEvent': 'net.neoforged.neoforge.event.LootTableLoadEvent',
    'net.minecraftforge.event.RegisterCommandsEvent': 'net.neoforged.neoforge.event.RegisterCommandsEvent',
    'net.minecraftforge.event.TagsUpdatedEvent': 'net.neoforged.neoforge.event.TagsUpdatedEvent',
    'net.minecraftforge.event.brewing.PlayerBrewedPotionEvent': 'net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent',
    'net.minecraftforge.event.entity.EntityTeleportEvent': 'net.neoforged.neoforge.event.entity.EntityTeleportEvent',
    'net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent': 'net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent',
    'net.minecraftforge.event.entity.living.ShieldBlockEvent': 'net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent',
    'net.minecraftforge.event.entity.player.ItemTooltipEvent': 'net.neoforged.neoforge.event.entity.player.ItemTooltipEvent',
    'net.minecraftforge.event.entity.player.PlayerInteractEvent': 'net.neoforged.neoforge.event.entity.player.PlayerInteractEvent',
    'net.minecraftforge.event.village.VillagerTradesEvent': 'net.neoforged.neoforge.event.village.VillagerTradesEvent',
    'net.minecraftforge.items.IItemHandler': 'net.neoforged.neoforge.items.IItemHandler',
    'net.minecraftforge.items.ItemStackHandler': 'net.neoforged.neoforge.items.ItemStackHandler',
    'net.minecraftforge.items.SlotItemHandler': 'net.neoforged.neoforge.items.SlotItemHandler',
    'net.minecraftforge.client.event.EntityRenderersEvent': 'net.neoforged.neoforge.client.event.EntityRenderersEvent',
    'net.minecraftforge.client.event.InputEvent': 'net.neoforged.neoforge.client.event.InputEvent',
    'net.minecraftforge.client.event.ModelEvent': 'net.neoforged.neoforge.client.event.ModelEvent',
    'net.minecraftforge.client.event.RegisterClientReloadListenersEvent': 'net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent',
    'net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent': 'net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent',
    'net.minecraftforge.client.event.RegisterColorHandlersEvent': 'net.neoforged.neoforge.client.event.RegisterColorHandlersEvent',
    'net.minecraftforge.client.event.RegisterKeyMappingsEvent': 'net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent',
    'net.minecraftforge.client.event.RegisterParticleProvidersEvent': 'net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent',
    'net.minecraftforge.client.event.RenderTooltipEvent': 'net.neoforged.neoforge.client.event.RenderTooltipEvent',
    'net.minecraftforge.client.settings.KeyConflictContext': 'net.neoforged.neoforge.client.settings.KeyConflictContext',
    'net.minecraftforge.client.ForgeRenderTypes': 'net.neoforged.neoforge.client.NeoForgeRenderTypes',
    'net.minecraftforge.client.RenderTypeGroup': 'net.neoforged.neoforge.client.RenderTypeGroup',
    'net.minecraftforge.client.model.CompositeModel': 'net.neoforged.neoforge.client.model.CompositeModel',
    'net.minecraftforge.client.model.ItemLayerModel': 'net.neoforged.neoforge.client.model.ItemLayerModel',
    'net.minecraftforge.client.model.IModelBuilder': 'net.neoforged.neoforge.client.model.IModelBuilder',
    'net.minecraftforge.client.model.ForgeFaceData': 'net.neoforged.neoforge.client.model.ExtraFaceData',
    'net.minecraftforge.client.model.geometry.IGeometryBakingContext': 'net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext',
    'net.minecraftforge.client.model.geometry.IGeometryLoader': 'net.neoforged.neoforge.client.model.geometry.IGeometryLoader',
    'net.minecraftforge.client.model.geometry.IUnbakedGeometry': 'net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry',
    'net.minecraftforge.client.model.geometry.UnbakedGeometryHelper': 'net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper',
    'net.minecraftforge.registries.DeferredRegister': 'net.neoforged.neoforge.registries.DeferredRegister',
    'net.minecraftforge.registries.RegistryObject': 'net.neoforged.neoforge.registries.DeferredHolder',
    'net.minecraftforge.registries.RegistryBuilder': 'net.neoforged.neoforge.registries.RegistryBuilder',
    'net.minecraftforge.registries.ForgeRegistries': 'net.minecraft.core.registries.BuiltInRegistries',
}

# whole-word symbol renames (applied after the imports)
SYMBOLS = [
    (r'\bMinecraftForge\.EVENT_BUS\b', 'NeoForge.EVENT_BUS'),
    (r'\bForgeConfigSpec\b', 'ModConfigSpec'),
    (r'\bToolActions\b', 'ItemAbilities'),
    (r'\bToolAction\b', 'ItemAbility'),
    (r'\bForgeEventFactory\b', 'EventHooks'),
    (r'\bForgeRenderTypes\b', 'NeoForgeRenderTypes'),
    (r'\bForgeFaceData\b', 'ExtraFaceData'),
    (r'\bIForgeMenuType\b', 'IMenuTypeExtension'),
    (r'\bForgeMod\b', 'NeoForgeMod'),
    (r'\bShieldBlockEvent\b', 'LivingShieldBlockEvent'),
    (r'@Mod\.EventBusSubscriber\b', '@EventBusSubscriber'),
    (r'\bMod\.EventBusSubscriber\.Bus\b', 'EventBusSubscriber.Bus'),
    (r'\bRegistryObject<', 'DeferredHolder<?, '),
    (r'\bnew ResourceLocation\(([^(),]+),\s*', r'ResourceLocation.fromNamespaceAndPath(\1, '),
    (r'\bnew ResourceLocation\(', 'ResourceLocation.parse('),
    (r'\.setSecondsOnFire\(', '.igniteForSeconds('),
    # 1.20.5: mob types became entity type tags
    (r'([A-Za-z_][\w.()]*?)\.getMobType\(\) == MobType\.UNDEAD', r'\1.getType().is(EntityTypeTags.UNDEAD)'),
    (r'([A-Za-z_][\w.()]*?)\.getMobType\(\) == MobType\.ARTHROPOD', r'\1.getType().is(EntityTypeTags.ARTHROPOD)'),
    (r'([A-Za-z_][\w.()]*?)\.getMobType\(\) == MobType\.WATER', r'\1.getType().is(EntityTypeTags.AQUATIC)'),
    (r'([A-Za-z_][\w.()]*?)\.getMobType\(\) == MobType\.ILLAGER', r'\1.getType().is(EntityTypeTags.ILLAGER)'),
    (r'import net\.minecraft\.world\.entity\.MobType;', 'import net.minecraft.tags.EntityTypeTags;'),
    # 1.20.5: attribute modifier operations
    (r'\bOperation\.ADDITION\b', 'Operation.ADD_VALUE'),
    (r'\bOperation\.MULTIPLY_BASE\b', 'Operation.ADD_MULTIPLIED_BASE'),
    (r'\bOperation\.MULTIPLY_TOTAL\b', 'Operation.ADD_MULTIPLIED_TOTAL'),
    (r'\bNeoForgeMod\.ENTITY_REACH\.get\(\)', 'Attributes.ENTITY_INTERACTION_RANGE'),
    (r'\bNeoForgeMod\.BLOCK_REACH\.get\(\)', 'Attributes.BLOCK_INTERACTION_RANGE'),
]

# ForgeRegistries.Keys.X / ForgeRegistries.X -> Registries.Y / BuiltInRegistries.Y
REG_NAMES = {
    'ITEMS': 'ITEM', 'BLOCKS': 'BLOCK', 'ENTITY_TYPES': 'ENTITY_TYPE', 'BLOCK_ENTITY_TYPES': 'BLOCK_ENTITY_TYPE',
    'ENCHANTMENTS': 'ENCHANTMENT', 'RECIPE_SERIALIZERS': 'RECIPE_SERIALIZER', 'RECIPE_TYPES': 'RECIPE_TYPE',
    'MENU_TYPES': 'MENU', 'SOUND_EVENTS': 'SOUND_EVENT', 'PARTICLE_TYPES': 'PARTICLE_TYPE', 'MOB_EFFECTS': 'MOB_EFFECT',
    'POTIONS': 'POTION', 'ATTRIBUTES': 'ATTRIBUTE', 'FLUIDS': 'FLUID',
}


def rewrite(src):
    out = src
    # imports
    def imp(m):
        static, name = m.group(1) or '', m.group(2)
        if name in IMPORTS:
            new = IMPORTS[name]
            return '' if new is None else 'import %s%s;' % (static, new)
        return m.group(0)
    out = re.sub(r'import (static )?([A-Za-z0-9_.]+);', imp, out)
    for a, b in SYMBOLS:
        out = re.sub(a, b, out)
    def keys(m):
        n = REG_NAMES.get(m.group(1))
        return 'Registries.%s' % n if n else m.group(0)
    out = re.sub(r'\bForgeRegistries\.Keys\.([A-Z_]+)', keys, out)
    def inst(m):
        n = REG_NAMES.get(m.group(1))
        return 'BuiltInRegistries.%s' % n if n else m.group(0)
    out = re.sub(r'\bForgeRegistries\.([A-Z_]+)\b', inst, out)
    # imports the rewritten code needs
    needs = []
    if 'Registries.' in out and 'import net.minecraft.core.registries.Registries;' not in out and re.search(r'(?<![A-Za-z])Registries\.', out):
        needs.append('net.minecraft.core.registries.Registries')
    if 'NeoForge.EVENT_BUS' in out and 'import net.neoforged.neoforge.common.NeoForge;' not in out:
        needs.append('net.neoforged.neoforge.common.NeoForge')
    if '@EventBusSubscriber' in out and 'import net.neoforged.fml.common.EventBusSubscriber;' not in out:
        needs.append('net.neoforged.fml.common.EventBusSubscriber')
    if needs:
        m = re.search(r'^package [^;]+;\n', out, re.M)
        ins = ''.join('import %s;\n' % n for n in needs)
        out = out[:m.end()] + '\n' + ins + out[m.end():]
    # drop duplicate imports
    seen, lines = set(), []
    for line in out.split('\n'):
        if line.startswith('import '):
            if line in seen:
                continue
            seen.add(line)
        lines.append(line)
    return '\n'.join(lines)


def main(root):
    changed = 0
    for d, _, files in os.walk(root):
        for f in files:
            if not f.endswith('.java'):
                continue
            p = os.path.join(d, f)
            with open(p, encoding='utf-8') as fh:
                src = fh.read()
            new = rewrite(src)
            if new != src:
                tmp = p + '.tmp'
                with open(tmp, 'w', encoding='utf-8') as fh:
                    fh.write(new)
                os.replace(tmp, p)
                changed += 1
    print('changed', changed, 'files')


if __name__ == '__main__':
    main(sys.argv[1])
