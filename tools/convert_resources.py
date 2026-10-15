#!/usr/bin/env python3
"""Rebuilds src/main/resources/{assets,data} for 1.21.1 from the 1.20.1 Spartan Weaponry resources.

    python3 -I tools/convert_resources.py <SpartanWeaponry 1.20.1 checkout> src/main/resources

Inputs: <checkout>/src/main/resources (hand-written assets and data) and <checkout>/src/generated/resources (datagen
output: recipes, advancements, tags, item models, loot tables). Hand-written 1.21 data in tools/overlay/ is copied on
top (lang files there are merged key by key). Running it again gives the same result.
"""
import json
import os
import re
import shutil
import sys

MOD = 'spartanweaponry'

# 1.20.1 forge tags -> 1.21.1 common (c) tags, where the name is not just forge:x -> c:x
FORGE_TAGS = {
    'forge:cobblestone': 'c:cobblestones',
    'forge:string': 'c:strings',
    'forge:leather': 'c:leathers',
    'forge:slimeballs': 'c:slime_balls',
    'forge:gunpowder': 'c:gunpowders',
    'forge:foods/meat/raw': 'c:foods/raw_meat',
}
CONDITIONS = {
    'forge:mod_loaded': 'neoforge:mod_loaded',
    'forge:not': 'neoforge:not',
    'forge:tag_empty': 'neoforge:tag_empty',
    'forge:and': 'neoforge:and',
    'forge:or': 'neoforge:or',
}
# items and blocks renamed in 1.20.3+
RENAMED = {
    'minecraft:grass': 'minecraft:short_grass',
}

MELEE = ['daggers', 'parrying_daggers', 'longswords', 'katanas', 'sabers', 'rapiers', 'greatswords', 'clubs', 'cestusae',
         'battle_hammers', 'warhammers', 'spears', 'halberds', 'pikes', 'lances', 'battleaxes', 'flanged_maces', 'glaives',
         'quarterstaves', 'scythes']
THROWING = ['throwing_knives', 'tomahawks', 'javelins', 'boomerangs']
RANGED = ['longbows', 'heavy_crossbows']

# 1.20.1 enchantment classes (rarity, getMinCost/getMaxCost, getMaxLevel, checkCompatibility) as 1.21 enchantment data.
# Rarity -> weight / anvil cost: COMMON 10/1, UNCOMMON 5/2, RARE 2/4, VERY_RARE 1/8. Costs are base + per_level * (lvl-1).
ENCHANTMENTS = {
    # name: (supported item tag, max level, weight, anvil cost, min (base, per level), max (base, per level), exclusive)
    'propel': ('throwing_weapon', 3, 5, 2, (10, 10), (41, 10), None),
    'razors_edge': ('throwing_weapon', 5, 10, 1, (1, 10), (16, 10), None),
    'incendiary': ('throwing_weapon', 1, 2, 4, (20, 0), (50, 0), MOD + ':hydrodynamic'),
    'lucky_throw': ('throwing_weapon', 3, 2, 4, (15, 9), (61, 10), 'minecraft:silk_touch'),
    'hydrodynamic': ('throwing_weapon', 1, 2, 4, (20, 0), (50, 0), MOD + ':incendiary'),
    'supercharge': ('throwing_weapon', 2, 2, 4, (15, 15), (25, 15), None),
    'expanse': ('throwing_weapon', 2, 2, 4, (20, 10), (25, 10), None),
    'sharpshooter': ('heavy_crossbow', 3, 2, 4, (15, 10), (50, 0), None),
    'collectorang': ('boomerang', 4, 5, 2, (10, 5), (40, 5), None),
}


def tag(name):
    if name in FORGE_TAGS:
        return FORGE_TAGS[name]
    if name.startswith('forge:'):
        return 'c:' + name[len('forge:'):]
    return name


def item_id(name):
    return RENAMED.get(name, name)


def condition(c):
    c = dict(c)
    c['type'] = CONDITIONS.get(c['type'], c['type'])
    if 'tag' in c:
        c['tag'] = tag(c['tag'])
    if 'value' in c:
        c['value'] = condition(c['value'])
    if 'values' in c:
        c['values'] = [condition(v) for v in c['values']]
    return c


def ingredient(i):
    if isinstance(i, list):
        return [ingredient(x) for x in i]
    i = dict(i)
    if 'tag' in i:
        i['tag'] = tag(i['tag'])
    if 'item' in i:
        i['item'] = item_id(i['item'])
    assert set(i) <= {'tag', 'item', 'disabled_type'}, i
    return i


def result(r):
    r = dict(r)
    if 'item' in r:
        r['id'] = item_id(r.pop('item'))
    if 'nbt' in r:
        # 1.20.1 item NBT of this mod's items lives in the custom_data component (see util/ItemNbt)
        nbt = r.pop('nbt')
        assert r['id'].startswith(MOD + ':') and not re.search(r'\b(Damage|Enchantments|display|Potion)\b', nbt), nbt
        r['components'] = {'minecraft:custom_data': nbt}
    return r


def recipe(d):
    out = {}
    if d.get('disabled_types'):
        # 1.20.1 skipped the tag cooking recipe while reading it when one of these types was disabled
        out['neoforge:conditions'] = [{'type': MOD + ':type_disabled', 'disabled': d['disabled_types']}]
    if 'result_tag' in d:
        # ... or when the result tag was empty
        out.setdefault('neoforge:conditions', []).append(
            {'type': 'neoforge:not', 'value': {'type': 'neoforge:tag_empty', 'tag': tag(d['result_tag'])}})
    for k, v in d.items():
        if k == 'conditions':
            out['neoforge:conditions'] = [condition(c) for c in v]
        elif k == 'disabled_types':
            pass
        elif k == 'result_tag':
            out[k] = tag(v)
        elif k == 'key':
            out[k] = {s: ingredient(i) for s, i in v.items()}
        elif k in ('base', 'addition', 'template', 'ingredient'):
            out[k] = ingredient(v)
        elif k == 'ingredients':
            out[k] = [ingredient(i) for i in v]
        elif k == 'result' and isinstance(v, dict):
            out[k] = result(v)
        else:
            out[k] = v
    return out


def jeed_recipe(d):
    """jeed:effect_provider of JEED 1.21: "effect" is the effect id, "providers" are ingredients (item NBT as a
    neoforge:components ingredient matching the custom_data component)"""
    providers = []
    for p in d['providers']:
        if 'nbt' in p:
            providers.append({'type': 'neoforge:components', 'items': p['item'],
                              'components': {'minecraft:custom_data': p['nbt']}})
        else:
            providers.append(ingredient(p))
    return {'type': d['type'], 'neoforge:conditions': [condition(c) for c in d['conditions']],
            'effect': d['effect']['id'], 'providers': providers}


def item_predicate(p):
    assert set(p) <= {'tag', 'items'}, p
    if 'tag' in p:
        return {'items': '#' + tag(p['tag'])}
    items = [item_id(i) for i in p['items']]
    return {'items': items[0] if len(items) == 1 else items}


def advancement(d, recipe_conditions):
    out = {}
    if recipe_conditions:
        out['neoforge:conditions'] = recipe_conditions
    for k, v in d.items():
        if k == 'criteria':
            crit = {}
            for name, c in v.items():
                c = json.loads(json.dumps(c))
                cond = c.get('conditions', {})
                if 'items' in cond:
                    cond['items'] = [item_predicate(p) for p in cond['items']]
                crit[name] = c
            out[k] = crit
        elif k == 'display':
            disp = dict(v)
            icon = dict(disp['icon'])
            icon['id'] = item_id(icon.pop('item'))
            # only {Damage:0}, which is the default
            assert icon.pop('nbt', '{Damage:0}') == '{Damage:0}'
            disp['icon'] = icon
            out[k] = disp
        else:
            out[k] = v
    return out


def dump(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')


def load(path):
    with open(path, encoding='utf-8') as f:
        text = f.read()
    # comment lines (accepted by the lenient Gson reader, not by json)
    text = re.sub(r'^\s*//.*$', '', text, flags=re.M)
    return json.loads(text)


def copytree(src, dst):
    for root, _, files in os.walk(src):
        for name in files:
            s = os.path.join(root, name)
            d = os.path.join(dst, os.path.relpath(s, src))
            os.makedirs(os.path.dirname(d), exist_ok=True)
            shutil.copyfile(s, d)


def walk_json(base):
    for root, _, files in os.walk(base):
        for name in sorted(files):
            if name.endswith('.json'):
                yield os.path.join(root, name)


def main(upstream, out):
    main_res = os.path.join(upstream, 'src/main/resources')
    gen_res = os.path.join(upstream, 'src/generated/resources')
    for sub in ('assets', 'data'):
        shutil.rmtree(os.path.join(out, sub), ignore_errors=True)

    # --- assets: unchanged formats (models, blockstates, particles, sounds, atlases), lang files normalised
    for res in (main_res, gen_res):
        src_assets = os.path.join(res, 'assets')
        for root, _, files in os.walk(src_assets):
            for name in files:
                s = os.path.join(root, name)
                rel = os.path.relpath(s, src_assets)
                if rel.startswith(MOD + '/lang/'):
                    dump(os.path.join(out, 'assets', rel), load(s))
                else:
                    d = os.path.join(out, 'assets', rel)
                    os.makedirs(os.path.dirname(d), exist_ok=True)
                    shutil.copyfile(s, d)

    # Issue #168: battleaxes have 16x16 textures but the ground transform of the long weapons (scale 0.9375), so a
    # dropped battleaxe was about twice the size of a dropped sword; it now uses the default handheld one
    battleaxe = os.path.join(out, 'assets', MOD, 'models', 'item', 'base', 'battleaxe.json')
    d = load(battleaxe)
    del d['display']['ground']
    dump(battleaxe, d)

    data_out = os.path.join(out, 'data')

    # --- recipes (1.21: data/<ns>/recipe)
    recipes = {}
    for res in (gen_res, main_res):
        rdir = os.path.join(res, 'data', MOD, 'recipes')
        for src in walk_json(rdir):
            name = os.path.relpath(src, rdir)[:-5].replace(os.sep, '/')
            d = load(src)
            if d['type'] == 'apotheosis:fletching':
                # Apotheosis for 1.21.1 has no Fletching Table recipes any more
                continue
            recipes[name] = jeed_recipe(d) if d['type'] == 'jeed:effect_provider' else recipe(d)
    recipe_conditions = {}
    for name, d in sorted(recipes.items()):
        recipe_conditions[MOD + ':' + name] = d.get('neoforge:conditions')
        dump(os.path.join(data_out, MOD, 'recipe', name + '.json'), d)

    # --- advancements (1.21: data/<ns>/advancement); a recipe advancement gets the conditions of its recipe
    for res in (gen_res, main_res):
        adir = os.path.join(res, 'data', MOD, 'advancements')
        for src in walk_json(adir):
            d = load(src)
            unlocked = d.get('rewards', {}).get('recipes', [])
            conds = recipe_conditions.get(unlocked[0]) if unlocked else None
            dump(os.path.join(data_out, MOD, 'advancement', os.path.relpath(src, adir)), advancement(d, conds))

    # --- tags (1.21: tags/item, tags/block, tags/entity_type; custom registries keep tags/<ns>/<path>)
    tag_dirs = {'items': 'item', 'blocks': 'block', 'entity_types': 'entity_type', 'damage_type': 'damage_type',
                MOD + '/weapon_traits': MOD + '/weapon_traits'}
    for res in (gen_res, main_res):
        ddir = os.path.join(res, 'data')
        for ns in sorted(os.listdir(ddir)):
            for old, new in tag_dirs.items():
                tdir = os.path.join(ddir, ns, 'tags', old)
                if not os.path.isdir(tdir):
                    continue
                for src in walk_json(tdir):
                    rel = os.path.relpath(src, tdir)[:-5].replace(os.sep, '/')
                    ns_out, path_out = tag(ns + ':' + rel).split(':', 1)
                    d = load(src)
                    d['values'] = [('#' + tag(v[1:])) if v.startswith('#') else item_id(v) for v in d['values']]
                    dump(os.path.join(data_out, ns_out, 'tags', new, path_out + '.json'), d)

    item_tags = os.path.join(data_out, MOD, 'tags', 'item')

    def mod_items(*tags):
        items = []
        for t in tags:
            for v in load(os.path.join(item_tags, t + '.json'))['values']:
                if v.startswith('#' + MOD + ':'):
                    items += mod_items(v[len(MOD) + 2:])
                elif v.startswith(MOD + ':'):
                    items.append(v)
        return items

    # The datagen misspelt the tag: "contantan_weapons". The weapons are in constantan_weapons now; the old name
    # stays as an alias for data packs that used it.
    os.replace(os.path.join(item_tags, 'contantan_weapons.json'), os.path.join(item_tags, 'constantan_weapons.json'))
    dump(os.path.join(item_tags, 'contantan_weapons.json'), {'values': ['#' + MOD + ':constantan_weapons']})
    # Issue #149: constantan weapons used the invar material traits tag (ModWeaponTraitTags.CONSTANTAN pointed at
    # materials/invar); constantan has no traits of its own in the datagen
    dump(os.path.join(data_out, MOD, 'tags', MOD, 'weapon_traits', 'materials', 'constantan.json'), {'values': []})

    # --- enchantability. 1.20.1 used the item classes (SwordItem: weapon enchantments, BowItem, CrossbowItem; every
    # damageable item: Unbreaking, Mending, Curse of Vanishing) and the mod's own categories; 1.21 uses item tags
    melee, throwing, ranged = mod_items(*MELEE), mod_items(*THROWING), mod_items(*RANGED)
    mc_enchantable = os.path.join(data_out, 'minecraft', 'tags', 'item', 'enchantable')
    dump(os.path.join(mc_enchantable, 'sword.json'), {'values': melee})
    dump(os.path.join(mc_enchantable, 'sharp_weapon.json'), {'values': melee})
    dump(os.path.join(mc_enchantable, 'bow.json'), {'values': mod_items('longbows')})
    dump(os.path.join(mc_enchantable, 'crossbow.json'), {'values': mod_items('heavy_crossbows')})
    dump(os.path.join(mc_enchantable, 'durability.json'), {'values': melee + throwing + ranged})
    own_enchantable = os.path.join(item_tags, 'enchantable')
    dump(os.path.join(own_enchantable, 'throwing_weapon.json'), {'values': throwing})
    dump(os.path.join(own_enchantable, 'heavy_crossbow.json'), {'values': mod_items('heavy_crossbows')})
    dump(os.path.join(own_enchantable, 'boomerang.json'), {'values': mod_items('boomerangs')})

    # 1.21 weapon tags of the common namespace (1.20.1: forge:weapons, which stays as c:weapons)
    dump(os.path.join(data_out, 'c', 'tags', 'item', 'tools', 'melee_weapon.json'), {'values': melee + throwing})
    dump(os.path.join(data_out, 'c', 'tags', 'item', 'tools', 'ranged_weapon.json'), {'values': ranged})

    # --- enchantments (1.21: data driven). The effects stay in code; the data gives the enchanting table values.
    for name, (supported, max_level, weight, anvil, min_cost, max_cost, exclusive) in ENCHANTMENTS.items():
        d = {
            'description': {'translate': 'enchantment.%s.%s' % (MOD, name)},
            'supported_items': '#%s:enchantable/%s' % (MOD, supported),
            'weight': weight,
            'max_level': max_level,
            'min_cost': {'base': min_cost[0], 'per_level_above_first': min_cost[1]},
            'max_cost': {'base': max_cost[0], 'per_level_above_first': max_cost[1]},
            'anvil_cost': anvil,
            'slots': ['mainhand'],
        }
        if exclusive:
            d['exclusive_set'] = exclusive
        dump(os.path.join(data_out, MOD, 'enchantment', name + '.json'), d)
    dump(os.path.join(data_out, 'minecraft', 'tags', 'enchantment', 'non_treasure.json'),
         {'values': [MOD + ':' + n for n in ENCHANTMENTS]})
    dump(os.path.join(data_out, 'minecraft', 'tags', 'enchantment', 'in_enchanting_table.json'),
         {'values': [MOD + ':' + n for n in ENCHANTMENTS]})
    dump(os.path.join(data_out, 'minecraft', 'tags', 'enchantment', 'tradeable.json'),
         {'values': [MOD + ':' + n for n in ENCHANTMENTS]})
    dump(os.path.join(data_out, 'minecraft', 'tags', 'enchantment', 'on_random_loot.json'),
         {'values': [MOD + ':' + n for n in ENCHANTMENTS]})

    # Power, Punch and Flame of a bow act on the projectile types in this tag (1.20.1: BowItem code)
    dump(os.path.join(data_out, 'minecraft', 'tags', 'entity_type', 'arrows.json'),
         {'values': [MOD + ':arrow', MOD + ':explosive_arrow', MOD + ':bolt', MOD + ':spectral_bolt']})

    # --- loot tables (1.21: data/<ns>/loot_table), damage types, weapon attributes, curios: same format
    for res in (gen_res, main_res):
        for old, new in (('loot_tables', 'loot_table'), ('damage_type', 'damage_type'),
                         ('weapon_attributes', 'weapon_attributes'), ('curios', 'curios'),
                         ('loot_modifiers', 'loot_modifiers')):
            src = os.path.join(res, 'data', MOD, old)
            if os.path.isdir(src):
                copytree(src, os.path.join(data_out, MOD, new))
        glm = os.path.join(res, 'data', 'forge', 'loot_modifiers', 'global_loot_modifiers.json')
        if os.path.exists(glm):
            dump(os.path.join(data_out, 'neoforge', 'loot_modifiers', 'global_loot_modifiers.json'), load(glm))

    # --- hand-written 1.21 data on top
    overlay = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'overlay')
    for root, _, files in os.walk(overlay):
        for name in files:
            src = os.path.join(root, name)
            dst = os.path.join(out, os.path.relpath(src, overlay))
            if '/lang/' in src.replace(os.sep, '/') and os.path.exists(dst):
                merged = load(dst)
                merged.update(load(src))
                dump(dst, merged)
            else:
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                shutil.copyfile(src, dst)


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])
