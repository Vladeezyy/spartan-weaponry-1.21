#!/usr/bin/env python3
"""26.1 villager trades as data (VillagerTradesEvent and ItemListing are gone): the 1.21.11 trades of
CommonEventHandler.addVillagerTrades, WeaponsmithTrades and FletcherTrades become data/spartanweaponry/villager_trade
JSONs, appended to the vanilla tags of the levels (data/minecraft/tags/villager_trade/<profession>/level_N, which the
trade sets pick from). Weaponsmith: one trade per level, the random weapon of the config lists by
spartanweaponry:weaponsmith_trade_item. Fletcher: spartanweaponry:trade_enabled as the merchant predicate.
26.3 format: typed conditions and loot functions, given_item_modifier (one or a list), whole numbers.
Run from the repository root."""
import json, os

DATA = 'common/src/main/resources/data'
ENCHANT = {'type': 'minecraft:enchant_with_levels', 'levels': {'type': 'minecraft:uniform', 'min': 5, 'max': 19},
           'options': '#minecraft:on_traded_equipment'}

def save(path, data):
    path = os.path.join(DATA, path)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        json.dump(data, f, indent=2)
        f.write('\n')

def trade(emeralds, gives, max_uses, xp, discount, weapon=None, modifiers=()):
    t = {'wants': {'id': 'minecraft:emerald', 'count': emeralds}, 'gives': gives, 'max_uses': max_uses,
         'xp': xp, 'reputation_discount': discount}
    if weapon is not None:
        t['merchant_predicate'] = {'type': 'spartanweaponry:trade_enabled', 'weapon': weapon}
    if modifiers:
        # 26.3: one modifier or a list, typed loot functions
        t['given_item_modifier'] = modifiers[0] if len(modifiers) == 1 else list(modifiers)
    return t

tags = {}
def add(profession, level, name, data):
    save(f'spartanweaponry/villager_trade/{profession}/{level}/{name}.json', data)
    tags.setdefault((profession, level), []).append(f'spartanweaponry:{profession}/{level}/{name}')

# Weaponsmith (RandomisedBuy[Enchanted]ItemWithEmeraldsTrade(items, 5, xp, 0.2)): price 0 + the weapon's price
for level, xp in [(1, 2), (2, 10), (3, 20), (4, 25), (5, 30)]:
    modifiers = [{'type': 'spartanweaponry:weaponsmith_trade_item', 'level': level}] + ([ENCHANT] if level == 5 else [])
    add('weaponsmith', level, 'emerald_random_weapon', trade(0, {'id': 'minecraft:iron_sword'}, 5, xp, 0.2, modifiers=modifiers))

# Fletcher
def item(name, count=1):
    return {'id': 'spartanweaponry:' + name, **({'count': count} if count > 1 else {})}
add('fletcher', 1, 'emerald_wooden_longbow', trade(4, item('wooden_longbow'), 3, 2, 0.2, 'longbow'))
add('fletcher', 3, 'emerald_iron_longbow', trade(4, item('iron_longbow'), 3, 15, 0.2, 'longbow'))
add('fletcher', 3, 'emerald_iron_heavy_crossbow', trade(4, item('iron_heavy_crossbow'), 3, 15, 0.2, 'heavy_crossbow'))
add('fletcher', 3, 'emerald_bolt', trade(2, item('bolt', 16), 12, 2, 1.0, 'heavy_crossbow'))
add('fletcher', 5, 'emerald_enchanted_diamond_longbow', trade(8, item('diamond_longbow'), 5, 40, 0.2, 'longbow', [ENCHANT]))
add('fletcher', 5, 'emerald_enchanted_diamond_heavy_crossbow', trade(8, item('diamond_heavy_crossbow'), 5, 40, 0.2, 'heavy_crossbow', [ENCHANT]))

for (profession, level), values in tags.items():
    save(f'minecraft/tags/villager_trade/{profession}/level_{level}.json', {'replace': False, 'values': values})
print(sum(map(len, tags.values())), 'trades,', len(tags), 'tags')
