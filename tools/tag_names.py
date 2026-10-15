"""Names of the mod's item tags ("tag.item.<namespace>.<path>", shown by EMI and other recipe viewers), used by
gen_translations.py. Languages: en_us, ru_ru, uk_ua, de_de, es_es (also es_mx), pt_br, zh_cn, fr_fr. Machine
translations, worded like the item names of each language file."""

LANGS = ('en_us', 'ru_ru', 'uk_ua', 'de_de', 'es_es', 'pt_br', 'zh_cn', 'fr_fr')

# Weapon types (plural)
TYPES = {
    'daggers': ('Daggers', 'Кинжалы', 'Кинджали', 'Dolche', 'Dagas', 'Adagas', '匕首', 'Dagues'),
    'parrying_daggers': ('Parrying Daggers', 'Парирующие кинжалы', 'Парувальні кинджали', 'Parierdolche', 'Dagas defensivas', 'Adagas de Aparar', '格挡匕首', 'Dagues de parade'),
    'longswords': ('Longswords', 'Полутораручные мечи', 'Півтораручні мечі', 'Langschwerter', 'Espadas largas', 'Espadas Longas', '长剑', 'Épées longues'),
    'katanas': ('Katanas', 'Самурайские мечи', 'Катани', 'Katanas', 'Katanas', 'Katanas', '太刀', 'Katanas'),
    'sabers': ('Sabers', 'Сабли', 'Шаблі', 'Säbel', 'Sables', 'Sabres', '军刀', 'Sabres'),
    'rapiers': ('Rapiers', 'Рапиры', 'Рапіри', 'Rapiere', 'Estoques', 'Floretes', '迅捷剑', 'Rapières'),
    'greatswords': ('Greatswords', 'Двуручные мечи', 'Дворучні мечі', 'Zweihänder', 'Mandobles', 'Montantes', '大剑', 'Espadons'),
    'clubs': ('Clubs', 'Дубинки', 'Кийки', 'Keulen', 'Garrotes', 'Clavas', '棍棒', 'Gourdins'),
    'cestusae': ('Cestus', 'Цестусы', 'Цестуси', 'Cestus', 'Cestus', 'Cestos', '拳套', 'Cestes'),
    'battle_hammers': ('Battle Hammers', 'Боевые молоты', 'Бойові молоти', 'Streithämmer', 'Martillos', 'Martelos de Batalha', '锤', 'Marteaux de bataille'),
    'warhammers': ('Warhammers', 'Клевцы', 'Клевці', 'Kriegshämmer', 'Martillos de guerra', 'Martelos de Guerra', '战锤', 'Marteaux de guerre'),
    'spears': ('Spears', 'Копья', 'Списи', 'Speere', 'Lanzas', 'Dorys', '矛', 'Lances'),
    'halberds': ('Halberds', 'Алебарды', 'Алебарди', 'Hellebarden', 'Alabardas', 'Alabardas', '战戟', 'Hallebardes'),
    'pikes': ('Pikes', 'Пики', 'Піки', 'Piken', 'Picas', 'Piques', '长矛', 'Piques'),
    'lances': ('Lances', 'Турнирные копья', 'Турнірні списи', 'Turnierlanzen', 'Lanzas de caballería', 'Lanças', '骑枪', 'Lances de joute'),
    'longbows': ('Longbows', 'Большие луки', 'Великі луки', 'Langbögen', 'Arcos largos', 'Arcos Longos', '长弓', 'Arcs longs'),
    'heavy_crossbows': ('Heavy Crossbows', 'Крупные арбалеты', 'Важкі арбалети', 'Schwere Armbrüste', 'Ballestas pesadas', 'Balestras Largas', '重弩', 'Arbalètes lourdes'),
    'throwing_knives': ('Throwing Knives', 'Ножи для метания', 'Метальні ножі', 'Wurfmesser', 'Cuchillos arrojadizos', 'Facas Arremessáveis', '飞刀', 'Couteaux de lancer'),
    'tomahawks': ('Tomahawks', 'Томагавки', 'Томагавки', 'Tomahawks', 'Tomahawks', 'Machados Arremessáveis', '印第安战斧', 'Tomahawks'),
    'javelins': ('Javelins', 'Метательные копья', 'Метальні списи', 'Wurfspeere', 'Jabalinas', 'Zagaias', '标枪', 'Javelots'),
    'boomerangs': ('Boomerangs', 'Бумеранги', 'Бумеранги', 'Bumerangs', 'Bumeranes', 'Bumerangues', '回力镖', 'Boomerangs'),
    'battleaxes': ('Battleaxes', 'Боевые топоры', 'Бойові сокири', 'Streitäxte', 'Hachas de batalla', 'Machados de Batalha', '战斧', 'Haches de bataille'),
    'flanged_maces': ('Flanged Maces', 'Перначи', 'Перначі', 'Streitkolben', 'Mazas de armas', 'Maças Flangeadas', '凸缘钉头锤', 'Masses à ailettes'),
    'glaives': ('Glaives', 'Глевии', 'Глефи', 'Glefen', 'Gujas', 'Glaives', '长柄刀', 'Glaives'),
    'quarterstaves': ('Quarterstaves', 'Боевые посохи', 'Бойові палиці', 'Kampfstäbe', 'Bastones', 'Bastões', '长棍', 'Bâtons de combat'),
    'scythes': ('Scythes', 'Косы', 'Коси', 'Sensen', 'Guadañas', 'Foices', '镰刀', 'Faux'),
}

# Material weapon tags: "<material>_weapons"
MATERIALS = {
    'wooden': ('Wooden', 'Деревянное', 'Дерев\'яна', 'Holz', 'madera', 'Madeira', '木', 'bois'),
    'stone': ('Stone', 'Каменное', 'Кам\'яна', 'Stein', 'piedra', 'Pedra', '石', 'pierre'),
    'leather': ('Leather', 'Кожаное', 'Шкіряна', 'Leder', 'cuero', 'Couro', '皮革', 'cuir'),
    'copper': ('Copper', 'Медное', 'Мідна', 'Kupfer', 'cobre', 'Cobre', '铜', 'cuivre'),
    'iron': ('Iron', 'Железное', 'Залізна', 'Eisen', 'hierro', 'Ferro', '铁', 'fer'),
    'golden': ('Golden', 'Золотое', 'Золота', 'Gold', 'oro', 'Ouro', '金', 'or'),
    'diamond': ('Diamond', 'Алмазное', 'Діамантова', 'Diamant', 'diamante', 'Diamante', '钻石', 'diamant'),
    'netherite': ('Netherite', 'Незеритовое', 'Незеритова', 'Netherit', 'netherita', 'Netherita', '下界合金', 'netherite'),
    'tin': ('Tin', 'Оловянное', 'Олов\'яна', 'Zinn', 'estaño', 'Estanho', '锡', 'étain'),
    'bronze': ('Bronze', 'Бронзовое', 'Бронзова', 'Bronze', 'bronce', 'Bronze', '青铜', 'bronze'),
    'steel': ('Steel', 'Стальное', 'Сталева', 'Stahl', 'acero', 'Aço', '钢', 'acier'),
    'silver': ('Silver', 'Серебряное', 'Срібна', 'Silber', 'plata', 'Prata', '银', 'argent'),
    'electrum': ('Electrum', 'Электрумовое', 'Електрумова', 'Elektrum', 'electro', 'Electrum', '琥珀金', 'électrum'),
    'lead': ('Lead', 'Свинцовое', 'Свинцева', 'Blei', 'plomo', 'Chumbo', '铅', 'plomb'),
    'nickel': ('Nickel', 'Никелевое', 'Нікелева', 'Nickel', 'níquel', 'Níquel', '镍', 'nickel'),
    'invar': ('Invar', 'Инваровое', 'Інварова', 'Invar', 'invar', 'Invar', '殷钢', 'invar'),
    'constantan': ('Constantan', 'Константановое', 'Константанова', 'Konstantan', 'constantán', 'Constantan', '康铜', 'constantan'),
    'platinum': ('Platinum', 'Платиновое', 'Платинова', 'Platin', 'platino', 'Platina', '铂', 'platine'),
    'aluminum': ('Aluminium', 'Алюминиевое', 'Алюмінієва', 'Aluminium', 'aluminio', 'Alumínio', '铝', 'aluminium'),
}

OTHER = {
    'spartanweaponry:arrows': ('Arrows', 'Стрелы', 'Стріли', 'Pfeile', 'Flechas', 'Flechas', '箭', 'Flèches'),
    'spartanweaponry:bolts': ('Bolts', 'Арбалетные болты', 'Арбалетні болти', 'Bolzen', 'Virotes', 'Virotes', '弩矢', 'Carreaux'),
    'spartanweaponry:quivers': ('Quivers', 'Колчаны', 'Сагайдаки', 'Köcher', 'Carcajs', 'Aljavas', '箭袋', 'Carquois'),
    'spartanweaponry:arrow_quivers': ('Arrow Quivers', 'Колчаны для стрел', 'Сагайдаки для стріл', 'Pfeilköcher', 'Carcajs de flechas', 'Aljavas de Flechas', '箭矢箭袋', 'Carquois à flèches'),
    'spartanweaponry:bolt_quivers': ('Bolt Quivers', 'Колчаны для болтов', 'Сагайдаки для болтів', 'Bolzenköcher', 'Carcajs de virotes', 'Aljavas de Virotes', '弩矢箭袋', 'Carquois à carreaux'),
    'spartanweaponry:small_quivers': ('Small Quivers', 'Малые колчаны', 'Малі сагайдаки', 'Kleine Köcher', 'Carcajs pequeños', 'Aljavas Pequenas', '小型箭袋', 'Petits carquois'),
    'spartanweaponry:upgraded_quivers': ('Upgradable Quivers', 'Улучшаемые колчаны', 'Покращувані сагайдаки', 'Verbesserbare Köcher', 'Carcajs mejorables', 'Aljavas Aprimoráveis', '可升级箭袋', 'Carquois améliorables'),
    'spartanweaponry:upgraded_quivers_max': ('Fully Upgraded Quivers', 'Колчаны наибольшего размера', 'Сагайдаки найбільшого розміру', 'Köcher der größten Stufe', 'Carcajs al máximo', 'Aljavas no Máximo', '满级箭袋', 'Carquois au maximum'),
    'spartanweaponry:copper_projectiles': ('Copper Projectiles', 'Медные снаряды', 'Мідні снаряди', 'Kupfergeschosse', 'Proyectiles de cobre', 'Projéteis de Cobre', '铜弹药', 'Projectiles en cuivre'),
    'spartanweaponry:diamond_projectiles': ('Diamond Projectiles', 'Алмазные снаряды', 'Діамантові снаряди', 'Diamantgeschosse', 'Proyectiles de diamante', 'Projéteis de Diamante', '钻石弹药', 'Projectiles en diamant'),
    'spartanweaponry:netherite_projectiles': ('Netherite Projectiles', 'Незеритовые снаряды', 'Незеритові снаряди', 'Netheritgeschosse', 'Proyectiles de netherita', 'Projéteis de Netherita', '下界合金弹药', 'Projectiles en netherite'),
    'spartanweaponry:explosives': ('Explosives', 'Взрывчатка', 'Вибухівка', 'Sprengstoffe', 'Explosivos', 'Explosivos', '爆炸物', 'Explosifs'),
    'spartanweaponry:handles': ('Handles', 'Рукоятки', 'Руків\'я', 'Griffe', 'Mangos', 'Cabos', '手柄', 'Poignées'),
    'spartanweaponry:poles': ('Poles', 'Шесты', 'Держална', 'Stangen', 'Astas', 'Hastes', '长手柄', 'Hampes'),
    'spartanweaponry:heads': ('Mob Heads', 'Головы мобов', 'Голови мобів', 'Mobköpfe', 'Cabezas de criaturas', 'Cabeças de Criaturas', '生物头颅', 'Têtes de créatures'),
    'spartanweaponry:has_custom_crosshair': ('Weapons with a Crosshair', 'Оружие со своим прицелом', 'Зброя з власним прицілом', 'Waffen mit Fadenkreuz', 'Armas con mira propia', 'Armas com Mira Própria', '自带准星的武器', 'Armes avec réticule'),
    'spartanweaponry:oilable_weapons': ('Oilable Weapons', 'Оружие, покрываемое маслом', 'Зброя, яку можна змастити олією', 'Ölbare Waffen', 'Armas aceitables', 'Armas Lubrificáveis', '可涂油武器', 'Armes enduisables d\'huile'),
    'spartanweaponry:throwing_weapons': ('Throwing Weapons', 'Метательное оружие', 'Метальна зброя', 'Wurfwaffen', 'Armas arrojadizas', 'Armas de Arremesso', '投掷武器', 'Armes de lancer'),
    'spartanweaponry:enchantable/boomerang': ('Enchantable Boomerangs', 'Зачаровываемые бумеранги', 'Зачаровувані бумеранги', 'Verzauberbare Bumerangs', 'Bumeranes encantables', 'Bumerangues Encantáveis', '可附魔回力镖', 'Boomerangs enchantables'),
    'spartanweaponry:enchantable/heavy_crossbow': ('Enchantable Heavy Crossbows', 'Зачаровываемые крупные арбалеты', 'Зачаровувані важкі арбалети', 'Verzauberbare schwere Armbrüste', 'Ballestas pesadas encantables', 'Balestras Largas Encantáveis', '可附魔重弩', 'Arbalètes lourdes enchantables'),
    'spartanweaponry:enchantable/throwing_weapon': ('Enchantable Throwing Weapons', 'Зачаровываемое метательное оружие', 'Зачаровувана метальна зброя', 'Verzauberbare Wurfwaffen', 'Armas arrojadizas encantables', 'Armas de Arremesso Encantáveis', '可附魔投掷武器', 'Armes de lancer enchantables'),
    'spartanweaponry:ingots/aluminum': ('Aluminium Ingots', 'Алюминиевые слитки', 'Алюмінієві злитки', 'Aluminiumbarren', 'Lingotes de aluminio', 'Lingotes de Alumínio', '铝锭', 'Lingots d\'aluminium'),
    'spartanweaponry:zombie_spawn_weapons': ('Zombie Spawn Weapons', 'Оружие зомби', 'Зброя зомбі', 'Zombiewaffen', 'Armas de zombis', 'Armas de Zumbis', '僵尸生成武器', 'Armes des zombies'),
    'spartanweaponry:skeleton_spawn_longbows': ('Skeleton Spawn Longbows', 'Большие луки скелетов', 'Великі луки скелетів', 'Skelett-Langbögen', 'Arcos largos de esqueletos', 'Arcos Longos de Esqueletos', '骷髅生成长弓', 'Arcs longs des squelettes'),
    'spartanweaponry:wither_skeleton_spawn_weapons': ('Wither Skeleton Spawn Weapons', 'Оружие скелетов-иссушителей', 'Зброя скелетів-візерів', 'Witherskelettwaffen', 'Armas de esqueletos wither', 'Armas de Esqueletos Wither', '凋灵骷髅生成武器', 'Armes des squelettes wither'),
    'spartanweaponry:piglin_spawn_weapons': ('Piglin Spawn Weapons', 'Оружие пиглинов', 'Зброя піглінів', 'Piglinwaffen', 'Armas de piglins', 'Armas de Piglins', '猪灵生成武器', 'Armes des piglins'),
    'spartanweaponry:piglin_brute_spawn_weapons': ('Piglin Brute Spawn Weapons', 'Оружие брутальных пиглинов', 'Зброя піглінів-громил', 'Piglin-Barbarenwaffen', 'Armas de piglins brutos', 'Armas de Piglins Brutos', '猪灵蛮兵生成武器', 'Armes des piglins barbares'),
    'c:weapons': ('Weapons', 'Оружие', 'Зброя', 'Waffen', 'Armas', 'Armas', '武器', 'Armes'),
    'c:grass': ('Grass', 'Трава', 'Трава', 'Gras', 'Hierba', 'Grama', '草', 'Herbe'),
}


def material_weapons(values):
    en, ru, uk, de, es, pt, zh, fr = values
    return (en + ' Weapons', ru + ' оружие', uk + ' зброя', de + 'waffen', 'Armas de ' + es, 'Armas de ' + pt, zh + '武器',
            'Armes en ' + fr)


def tag_names():
    """tag id → names in LANGS order"""
    names = {'spartanweaponry:' + tag: values for tag, values in TYPES.items()}
    for material, values in MATERIALS.items():
        names['spartanweaponry:%s_weapons' % material] = material_weapons(values)
    # The 1.20.1 data has a misspelt copy of the Constantan tag
    names['spartanweaponry:contantan_weapons'] = material_weapons(MATERIALS['constantan'])
    names.update(OTHER)
    return names


def keys():
    """lang → {translation key: name}; es_mx gets the Spanish names"""
    out = {lang: {} for lang in LANGS + ('es_mx',)}
    for tag, values in tag_names().items():
        namespace, path = tag.split(':')
        key = 'tag.item.%s.%s' % (namespace, path.replace('/', '.'))
        for lang, value in zip(LANGS, values):
            out[lang][key] = value
        out['es_mx'][key] = values[LANGS.index('es_es')]
    return out
