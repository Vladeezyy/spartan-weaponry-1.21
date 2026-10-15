#!/usr/bin/env python3
"""Writes tools/overlay/assets/spartanweaponry/lang/{en_us,ru_ru,uk_ua,de_de,es_es,pt_br,zh_cn}.json.

    python3 -I tools/gen_translations.py <SpartanWeaponry 1.20.1 checkout>

uk_ua and de_de are new and complete. ru_ru, es_es, pt_br and zh_cn keep the 1.20.1 translations; they only get the
keys they lacked, and the 1.20.1 strings whose placeholders did not match the English ones (those showed broken text,
e.g. "%.2f" where the game passes an already formatted value) or that named the wrong material. en_us gets the names of
the potions added in 1.21. Weapon names are built from the material and the weapon word; the rest is a table. Vanilla
potion names follow the game's own translations. Machine translations (no native speaker checked them).
Run tools/convert_resources.py afterwards.
"""
import json
import os
import re
import sys

MOD = 'spartanweaponry'
P = 'item.%s.' % MOD

# ---------------------------------------------------------------------------------------------------- weapon names
# material: uk adjective (m, f, n), uk instrumental (strengthened with), de compound prefix, es noun, pt noun
MAT = {
    'wooden': (('Дерев\'яний', 'Дерев\'яна', 'Дерев\'яне'), 'деревом', 'Holz', 'madera', 'Madeira'),
    'stone': (('Кам\'яний', 'Кам\'яна', 'Кам\'яне'), 'каменем', 'Stein', 'piedra', 'Pedra'),
    'leather': (('Шкіряний', 'Шкіряна', 'Шкіряне'), 'шкірою', 'Leder', 'cuero', 'Couro'),
    'copper': (('Мідний', 'Мідна', 'Мідне'), 'міддю', 'Kupfer', 'cobre', 'Cobre'),
    'iron': (('Залізний', 'Залізна', 'Залізне'), 'залізом', 'Eisen', 'hierro', 'Ferro'),
    'golden': (('Золотий', 'Золота', 'Золоте'), 'золотом', 'Gold', 'oro', 'Ouro'),
    'diamond': (('Діамантовий', 'Діамантова', 'Діамантове'), 'діамантом', 'Diamant', 'diamante', 'Diamante'),
    'netherite': (('Незеритовий', 'Незеритова', 'Незеритове'), 'незеритом', 'Netherit', 'netherita', 'Netherita'),
    'tin': (('Олов\'яний', 'Олов\'яна', 'Олов\'яне'), 'оловом', 'Zinn', 'estaño', 'Estanho'),
    'bronze': (('Бронзовий', 'Бронзова', 'Бронзове'), 'бронзою', 'Bronze', 'bronce', 'Bronze'),
    'steel': (('Сталевий', 'Сталева', 'Сталеве'), 'сталлю', 'Stahl', 'acero', 'Aço'),
    'silver': (('Срібний', 'Срібна', 'Срібне'), 'сріблом', 'Silber', 'plata', 'Prata'),
    'electrum': (('Електрумовий', 'Електрумова', 'Електрумове'), 'електрумом', 'Elektrum', 'electro', 'Electrum'),
    'lead': (('Свинцевий', 'Свинцева', 'Свинцеве'), 'свинцем', 'Blei', 'plomo', 'Chumbo'),
    'nickel': (('Нікелевий', 'Нікелева', 'Нікелеве'), 'нікелем', 'Nickel', 'níquel', 'Níquel'),
    'invar': (('Інваровий', 'Інварова', 'Інварове'), 'інваром', 'Invar', 'invar', 'Invar'),
    'constantan': (('Константановий', 'Константанова', 'Константанове'), 'константаном', 'Konstantan', 'constantán',
                   'Constantan'),
    'platinum': (('Платиновий', 'Платинова', 'Платинове'), 'платиною', 'Platin', 'platino', 'Platina'),
    'aluminum': (('Алюмінієвий', 'Алюмінієва', 'Алюмінієве'), 'алюмінієм', 'Aluminium', 'aluminio', 'Alumínio'),
}
# weapon: uk noun (lower case, may start with an adjective), uk gender (0 m, 1 f, 2 n), de noun (lower case for the
# compound), de standalone noun
WEAPON = {
    'dagger': ('кинджал', 0, 'dolch', 'Dolch'),
    'parrying_dagger': ('парувальний кинджал', 0, 'parierdolch', 'Parierdolch'),
    'longsword': ('півтораручний меч', 0, 'langschwert', 'Langschwert'),
    'katana': ('катана', 1, 'katana', 'Katana'),
    'saber': ('шабля', 1, 'säbel', 'Säbel'),
    'rapier': ('рапіра', 1, 'rapier', 'Rapier'),
    'greatsword': ('дворучний меч', 0, 'zweihänder', 'Zweihänder'),
    'club': ('кийок', 0, 'keule', 'Keule'),
    'cestus': ('цестус', 0, 'cestus', 'Cestus'),
    'battle_hammer': ('бойовий молот', 0, 'streithammer', 'Streithammer'),
    'warhammer': ('клевець', 0, 'kriegshammer', 'Kriegshammer'),
    'spear': ('спис', 0, 'speer', 'Speer'),
    'halberd': ('алебарда', 1, 'hellebarde', 'Hellebarde'),
    'pike': ('піка', 1, 'pike', 'Pike'),
    'lance': ('турнірний спис', 0, 'lanze', 'Lanze'),
    'throwing_knife': ('метальний ніж', 0, 'wurfmesser', 'Wurfmesser'),
    'tomahawk': ('томагавк', 0, 'tomahawk', 'Tomahawk'),
    'javelin': ('метальний спис', 0, 'wurfspeer', 'Wurfspeer'),
    'boomerang': ('бумеранг', 0, 'bumerang', 'Bumerang'),
    'battleaxe': ('бойова сокира', 1, 'streitaxt', 'Streitaxt'),
    'flanged_mace': ('пернач', 0, 'streitkolben', 'Streitkolben'),
    'glaive': ('глефа', 1, 'glefe', 'Glefe'),
    'quarterstaff': ('бойовий посох', 0, 'kampfstab', 'Kampfstab'),
    'scythe': ('коса', 1, 'sense', 'Sense'),
    'arrow': ('стріла', 1, 'pfeil', 'Pfeil'),
    'bolt': ('болт', 0, 'bolzen', 'Bolzen'),
}
# "<material>-Strengthened" ranged weapons
STRENGTHENED = {
    'longbow': ('довгий лук', 'Посилений %s довгий лук', 'Langbogen', '%sverstärkter Langbogen'),
    'heavy_crossbow': ('важкий арбалет', 'Посилений %s важкий арбалет', 'Schwere Armbrust',
                       '%sverstärkte schwere Armbrust'),
}
SPECIAL_NAMES = {
    # key: (uk, de)
    'studded_club': ('Кийок із шипами', 'Stachelkeule'),
    'studded_cestus': ('Цестус із шипами', 'Stachelcestus'),
    'cestus': ('Цестус', 'Cestus'),
    'wooden_longbow': ('Дерев\'яний довгий лук', 'Holzlangbogen'),
    'wooden_heavy_crossbow': ('Дерев\'яний важкий арбалет', 'Schwere Holzarmbrust'),
}


def weapon_names(en):
    """uk and de names of every <material>_<weapon> item of en_us"""
    out = {}
    for key in en:
        if not key.startswith(P):
            continue
        name = key[len(P):]
        if name in SPECIAL_NAMES:
            out[key] = SPECIAL_NAMES[name]
            continue
        for mat, (uk_adj, uk_instr, de_prefix, _, _) in MAT.items():
            if not name.startswith(mat + '_'):
                continue
            weapon = name[len(mat) + 1:]
            if weapon in STRENGTHENED:
                uk_noun, uk_fmt, _, de_fmt = STRENGTHENED[weapon]
                out[key] = (uk_fmt % uk_instr, de_fmt % de_prefix)
            elif weapon in WEAPON:
                uk_noun, gender, de_noun, _ = WEAPON[weapon]
                out[key] = (uk_adj[gender] + ' ' + uk_noun, de_prefix + de_noun)
    return out


def custom_names():
    """item.spartanweaponry.custom_<weapon>: "%s <Weapon>" with the material name of another mod"""
    out = {}
    for weapon, (uk_noun, _, _, de_noun) in WEAPON.items():
        out[P + 'custom_' + weapon] = (uk_noun[0].upper() + uk_noun[1:] + ' (%s)', '%s-' + de_noun)
    for weapon, (uk_noun, _, de_noun, de_fmt) in STRENGTHENED.items():
        out[P + 'custom_' + weapon] = ('Посилений ' + uk_noun + ' (%s)', de_fmt % '%s-')
    return out


# ----------------------------------------------------------------------------------------- tipped projectile names
# "%s of <Effect>": effect (the last part of the key, without long_/strong_/_long/_strong) -> ru, uk, de, es, pt
# (only the parts after the projectile name)
TIPPED = {
    'water': ('с водяной взвесью', 'водяна %s', 'Nasser %s', '%s de salpicadura', '%s sem Efeito'),
    'empty': ('%s, смоченная зельем', 'Оброблена зіллям %s', 'Getränkter %s', '%s con poción', '%s com Poção'),
    'night_vision': ('ночного зрения', 'нічного бачення', 'der Nachtsicht', 'de visión nocturna', 'de Visão Noturna'),
    'invisibility': ('невидимости', 'невидимості', 'der Unsichtbarkeit', 'de invisibilidad', 'de Invisibilidade'),
    'leaping': ('прыгучести', 'стрибучості', 'der Sprungkraft', 'de salto', 'de Salto'),
    'fire_resistance': ('огнестойкости', 'вогнестійкості', 'der Feuerresistenz', 'de resistencia al fuego',
                        'de Resistência ao Fogo'),
    'swiftness': ('стремительности', 'прудкості', 'der Schnelligkeit', 'de rapidez', 'de Velocidade'),
    'slowness': ('замедления', 'повільності', 'der Langsamkeit', 'de lentitud', 'de Lentidão'),
    'water_breathing': ('подводного дыхания', 'підводного дихання', 'der Unterwasseratmung', 'de respiración acuática',
                        'de Respiração Aquática'),
    'healing': ('исцеления', 'зцілення', 'der Heilung', 'de curación', 'de Cura'),
    'harming': ('вреда', 'шкоди', 'des Schadens', 'de daño', 'de Dano'),
    'poison': ('отравления', 'отруєння', 'der Vergiftung', 'de veneno', 'de Veneno'),
    'regeneration': ('регенерации', 'регенерації', 'der Regeneration', 'de regeneración', 'de Regeneração'),
    'strength': ('силы', 'сили', 'der Stärke', 'de fuerza', 'de Força'),
    'weakness': ('слабости', 'слабкості', 'der Schwäche', 'de debilidad', 'de Fraqueza'),
    'levitation': ('левитации', 'левітації', 'der Schwebe', 'de levitación', 'de Levitação'),
    'luck': ('везения', 'везіння', 'des Glücks', 'de suerte', 'de Sorte'),
    'turtle_master': ('черепашьей мощи', 'майстра черепах', 'des Schildkrötenmeisters', 'del maestro tortuga',
                      'do Mestre-Tartaruga'),
    'slow_falling': ('плавного падения', 'повільного падіння', 'des sanften Falls', 'de caída lenta',
                     'de Queda Lenta'),
    'wind_charged': ('ветрового заряда', 'вітряності', 'der Windladung', 'de cierzo', 'de Ventania'),
    'weaving': ('плетения', 'плетіння', 'des Webens', 'de tejedura', 'de Tecitura'),
    'oozing': ('слизистости', 'слизькості', 'des Schleimens', 'de mucosidad', 'Pegajosa'),
    'infested': ('заражения', 'зараження', 'des Befalls', 'de infestación', 'de Infestação'),
    # Quark, Cyclic, Botania, Ars Nouveau, Mekanism and other mods with potions (1.20.1 list)
    'resilience': ('стойкости', 'стійкості', 'der Standfestigkeit', 'de estabilidad', 'de Estabilidade'),
    'quark_resistance': ('сопротивления', 'опору', 'der Seelenstärke', 'de fortaleza', 'de Fortitude'),
    'danger_sight': ('предвидения', 'передбачення', 'der Vorahnung', 'de presciencia', 'de Presciência'),
    'haste': ('спешки', 'квапливості', 'der Eile', 'de prisa', 'de Pressa'),
    'cyclic_strong_haste': ('спешки II', 'квапливості II', 'der Eile II', 'de prisa II', 'de Pressa II'),
    'cyclic_stun': ('парализованности', 'паралічу', 'der Lähmung', 'de parálisis', 'de Paralisia'),
    'cyclic_swimspeed': ('скорости плавания', 'швидкості плавання', 'der Schwimmgeschwindigkeit',
                         'de velocidad de nado', 'de Velocidade de Nado'),
    'absorption': ('поглощения', 'поглинання', 'der Absorption', 'de absorción', 'de Absorção'),
    'fatigue': ('утомления', 'втоми', 'der Abbaulähmung', 'de cansancio', 'de Fadiga de Mineração'),
    'resistance': ('сопротивления', 'опору', 'des Widerstands', 'de resistencia', 'de Resistência'),
    'wither': ('иссушения', 'іссушення', 'der Ausdörrung', 'de descomposición', 'de Wither'),
    'sundering': ('раскола', 'розколу', 'der Zerschmetterung', 'de quiebre', 'de Ruptura'),
    'knowledge': ('древних знаний', 'давніх знань', 'des alten Wissens', 'de conocimiento antiguo',
                  'de Conhecimento Antigo'),
    'insulating': ('изоляции', 'ізоляції', 'der Isolierung', 'de aislamiento', 'de Isolamento'),
    'grounding': ('заземления', 'заземлення', 'der Erdung', 'de conexión a tierra', 'de Aterramento'),
    'insomnia': ('бессонницы', 'безсоння', 'der Schlaflosigkeit', 'de insomnio', 'de Insônia'),
    'restfulness': ('успокоения', 'спокою', 'der Erholsamkeit', 'de descanso', 'de Descanso'),
    'repellence': ('отталкивающей способности', 'відштовхування', 'der Abstoßung', 'de repelencia', 'de Repelência'),
    'vibing': ('вибрации', 'вайбу', 'der guten Stimmung', 'de buen rollo', 'de Vibração'),
    'extension': ('расширения', 'розширення', 'der Ausdehnung', 'de extensión', 'de Extensão'),
    'mana_regen_potion': ('мана регенерации', 'регенерації мани', 'der Manaregeneration', 'de regeneración de maná',
                          'de Regeneração de Mana'),
    'spell_damage_potion': ('магического урона', 'магічної шкоди', 'des Zauberschadens', 'de daño mágico',
                            'de Dano Mágico'),
    'recovery_potion': ('восстановления', 'відновлення', 'der Erholung', 'de recuperación', 'de Recuperação'),
    'blasting_potion': ('взрыва', 'вибуху', 'der Sprengung', 'de explosión', 'de Explosão'),
    'freezing_potion': ('заморозки', 'заморожування', 'des Gefrierens', 'de congelación', 'de Congelamento'),
    'shielding_potion': ('защиты', 'захисту', 'der Abschirmung', 'de escudo', 'de Proteção'),
    'enderference_potion': ('анти-телепорта', 'антителепорту', 'der Enderstörung', 'de interferencia ender',
                            'de Interferência Ender'),
    'shock_potion': ('статического электричества', 'статичного заряду', 'der statischen Ladung', 'de carga estática',
                     'de Carga Estática'),
    'chilled': ('обморожения', 'охолодження', 'der Abkühlung', 'de escalofrío', 'de Resfriamento'),
    'anchored': ('закрепления', 'закріплення', 'der Verankerung', 'de anclaje', 'de Ancoragem'),
    'decay': ('распада', 'розпаду', 'des Verfalls', 'de descomposición', 'de Decomposição'),
    'reinforced': ('укрепления', 'зміцнення', 'der Verstärkung', 'de refuerzo', 'de Reforço'),
    'undeath': ('нежизни', 'неживості', 'der Untoten', 'de no-muerte', 'de Morte-viva'),
    'vulnerable': ('уязвимости', 'вразливості', 'der Verwundbarkeit', 'de vulnerabilidad', 'de Vulnerabilidade'),
    'abyspower': ('силы бездны', 'сили безодні', 'der Abgrundkraft', 'de poder abisal', 'de Poder Abissal'),
    'fear': ('страха', 'страху', 'der Furcht', 'de miedo', 'de Medo'),
    'ghostvision': ('призрачного видения', 'примарного зору', 'der Geistersicht', 'de visión fantasmal',
                    'de Visão Fantasma'),
    'node_potion': ('узлового зелья', 'вузлового зілля', 'des Knotentranks', 'de poción de nodo', 'de Poção de Nó'),
    'time_stop_potion_effect': ('остановки времени', 'зупинки часу', 'des Zeitstillstands', 'de tiempo detenido',
                                'de Parada do Tempo'),
    'bane_potion': ('проклятия', 'прокляття', 'des Fluchs', 'de perdición', 'de Ruína'),
    'nausea_potion': ('тошноты', 'нудоти', 'der Übelkeit', 'de náuseas', 'de Náusea'),
    'bat_vision': ('зрения летучей мыши', 'зору кажана', 'der Fledermaussicht', 'de visión de murciélago',
                   'de Visão de Morcego'),
    'broken_armor': ('поломанной брони', 'зламаної броні', 'der zerbrochenen Rüstung', 'de armadura rota',
                     'de Armadura Quebrada'),
    'electro_resistance': ('электрического сопротивления', 'електростійкості', 'der Elektroresistenz',
                           'de electrorresistencia', 'de Eletrorresistência'),
    'sea_vision': ('морского зрения', 'морського зору', 'der Meeressicht', 'de visión marina', 'de Visão Marinha'),
    'thorns': ('шипов', 'шипів', 'der Dornen', 'de espinas', 'de Espinhos'),
    'bug_pheremones': ('жучьих феромонов', 'жучиних феромонів', 'der Käferpheromone', 'de feromonas de bicho',
                       'de Feromônios de Inseto'),
    'clinging': ('цепкости', 'чіпкості', 'des Haftens', 'de adherencia', 'de Aderência'),
    'knockback_resistance': ('сопротивления к отбрасыванию', 'опору відкиданню', 'der Rückstoßresistenz',
                             'de resistencia al empuje', 'de Resistência a Repulsão'),
    'lava_vision': ('лавового зрения', 'лавового зору', 'der Lavasicht', 'de visión de lava', 'de Visão de Lava'),
    'poison_resistance': ('сопротивления к ядам', 'опору отрутам', 'der Giftresistenz', 'de resistencia al veneno',
                          'de Resistência a Veneno'),
    'soulsteal': ('кражи души', 'крадіжки душі', 'des Seelenraubs', 'de robo de almas', 'de Roubo de Alma'),
    'speed_iii': ('скорости III', 'прудкості III', 'der Schnelligkeit III', 'de rapidez III', 'de Velocidade III'),
    'burning': ('горения', 'горіння', 'des Brennens', 'de combustión', 'de Queimadura'),
    'spore': ('споры', 'спор', 'der Sporen', 'de esporas', 'de Esporos'),
    'cold_resistance': ('сопротивления к холоду', 'опору холоду', 'der Kälteresistenz', 'de resistencia al frío',
                        'de Resistência ao Frio'),
    'heat_resistance': ('сопротивления к жаре', 'опору спеці', 'der Hitzeresistenz', 'de resistencia al calor',
                        'de Resistência ao Calor'),
    'end_veil': ('завесы Энда', 'завіси Енду', 'des Endschleiers', 'de velo del End', 'de Véu do End'),
    'flight': ('полёта', 'польоту', 'des Flugs', 'de vuelo', 'de Voo'),
    'warp': ('деформации', 'деформації', 'der Verzerrung', 'de distorsión', 'de Distorção'),
    'infection': ('инфекции', 'інфекції', 'der Infektion', 'de infección', 'de Infecção'),
    'luminous': ('яркости', 'яскравості', 'des Leuchtens', 'de luminiscencia', 'de Luminescência'),
    'lightflight': ('состояния души', 'стану душі', 'des Seelenzustands', 'de estado de alma', 'de Estado de Alma'),
    'unluck': ('невезения', 'невезіння', 'des Pechs', 'de mala suerte', 'de Azar'),
    'magic_proficiency': ('магического мастерства', 'магічної майстерності', 'der Magiebeherrschung',
                          'de competencia mágica', 'de Proficiência Mágica'),
    'mirror': ('отражения', 'віддзеркалення', 'der Spiegelung', 'de reflejo', 'de Espelhamento'),
    'swine': ('свинства', 'свинства', 'des Schweins', 'de cerdo', 'de Porco'),
    'oakwood_brew': ('дубовой брони', 'дубової броні', 'der Eichenholzrüstung', 'de armadura de roble',
                     'de Armadura de Carvalho'),
    'shadow_brew': ('теневой формы', 'тіньової форми', 'der Schattengestalt', 'de forma sombría',
                    'de Forma Sombria'),
    'pastey': ('пастообразности', 'пастоподібності', 'der Pastigkeit', 'de pastosidad', 'de Pastosidade'),
    'poison_resist': ('сопротивления к ядам', 'опору отрутам', 'der Giftresistenz', 'de resistencia al veneno',
                      'de Resistência a Veneno'),
    'potion_of_cinder_soul': ('зелья пепельной души', 'зілля попелястої душі', 'des Trankes der Aschenseele',
                              'de poción de alma de ceniza', 'de Poção de Alma de Cinzas'),
    'relief': ('облегчения', 'полегшення', 'der Erleichterung', 'de alivio', 'de Alívio'),
    'worsening': ('ухудшения', 'погіршення', 'der Verschlechterung', 'de empeoramiento', 'de Piora'),
    'vitality': ('жизненной силы', 'життєвої сили', 'der Vitalität', 'de vitalidad', 'de Vitalidade'),
    'wither_cure': ('антииссушения', 'зцілення від іссушення', 'der Ausdörrungsheilung', 'de cura de descomposición',
                    'de Cura de Wither'),
    'earthly_garden': ('земного сада', 'земного саду', 'des irdischen Gartens', 'de jardín terrenal',
                       'de Jardim Terreno'),
    'bait': ('наживки', 'наживки', 'des Köders', 'de cebo', 'de Isca'),
    'frostbite': ('обморожения', 'обмороження', 'der Erfrierung', 'de congelamiento', 'de Ulceração pelo Frio'),
    'darkness': ('тьмы', 'темряви', 'der Dunkelheit', 'de oscuridad', 'de Escuridão'),
    'discretion': ('осмотрительности', 'обачності', 'der Diskretion', 'de discreción', 'de Discrição'),
    'restoration': ('восстановления', 'відновлення', 'der Wiederherstellung', 'de restauración', 'de Restauração'),
    'grievous': ('ослабления лечения', 'послаблення лікування', 'der Heilungsminderung', 'de reducción de curación',
                 'de Redução de Cura'),
    'flying': ('полёта', 'польоту', 'des Fliegens', 'de vuelo', 'de Voo'),
    'brittleness': ('хрупкости', 'крихкості', 'der Sprödigkeit', 'de fragilidad', 'de Fragilidade'),
    'featherweight': ('лёгкости', 'легкості', 'des Federgewichts', 'de peso pluma', 'de Peso-pena'),
    'virulent_resistance': ('сопротивления к заразе', 'опору заразі', 'der Virulenzresistenz',
                            'de resistencia virulenta', 'de Resistência Virulenta'),
    'glowing': ('свечения', 'світіння', 'des Leuchtens', 'de brillo', 'de Brilho'),
    'forest_dasher': ('лесной ярости', 'лісового спринтера', 'des Waldsprinters', 'del corredor del bosque',
                      'do Corredor da Floresta'),
    'sculk_affinity': ('родства со скалком', 'спорідненості зі скалком', 'der Sculk-Affinität',
                       'de afinidad con el sculk', 'de Afinidade com Sculk'),
    'mana': ('маны', 'мани', 'des Manas', 'de maná', 'de Mana'),
    'creosote': ('креозота', 'креозоту', 'des Kreosots', 'de creosota', 'de Creosoto'),
}
SIGHT = {  # <ore>_sight (Mining Sight potions): ru, uk, de, es, pt ore names
    'coal': ('угольного', 'вугільного', 'Kohle', 'carbón', 'Carvão'),
    'iron': ('железного', 'залізного', 'Eisen', 'hierro', 'Ferro'),
    'redstone': ('редстоунского', 'редстоунового', 'Redstone', 'redstone', 'Redstone'),
    'lapis': ('лазуритного', 'лазуритового', 'Lapis', 'lapislázuli', 'Lápis'),
    'gold': ('золотого', 'золотого', 'Gold', 'oro', 'Ouro'),
    'diamond': ('алмазного', 'діамантового', 'Diamant', 'diamante', 'Diamante'),
    'emerald': ('изумрудного', 'смарагдового', 'Smaragd', 'esmeralda', 'Esmeralda'),
    'aluminium': ('алюминиевого', 'алюмінієвого', 'Aluminium', 'aluminio', 'Alumínio'),
    'copper': ('медного', 'мідного', 'Kupfer', 'cobre', 'Cobre'),
    'tin': ('оловянного', 'олов\'яного', 'Zinn', 'estaño', 'Estanho'),
    'nickel': ('никелевого', 'нікелевого', 'Nickel', 'níquel', 'Níquel'),
    'uranium': ('уранового', 'уранового', 'Uran', 'uranio', 'Urânio'),
    'lead': ('свинцового', 'свинцевого', 'Blei', 'plomo', 'Chumbo'),
    'silver': ('серебряного', 'срібного', 'Silber', 'plata', 'Prata'),
    'zinc': ('цинкового', 'цинкового', 'Zink', 'zinc', 'Zinco'),
    'osmium': ('осмиевого', 'осмієвого', 'Osmium', 'osmio', 'Ósmio'),
    'quartz': ('кварцевого', 'кварцового', 'Quarz', 'cuarzo', 'Quartzo'),
    'bizmuth': ('висмутового', 'вісмутового', 'Bismut', 'bismuto', 'Bismuto'),
    'bismuth': ('висмутового', 'вісмутового', 'Bismut', 'bismuto', 'Bismuto'),
    'crimsoniron': ('багрово-железного', 'багряно-залізного', 'Karmineisen', 'hierro carmesí', 'Ferro Carmesim'),
    'platinum': ('платинового', 'платинового', 'Platin', 'platino', 'Platina'),
    'netherite': ('незеритового', 'незеритового', 'Netherit', 'netherita', 'Netherita'),
    'allthemodium': ('алдемодиевого', 'олземодієвого', 'Allthemodium', 'allthemodium', 'Allthemodium'),
    'vibranium': ('вибраниевого', 'вібранієвого', 'Vibranium', 'vibranio', 'Vibrânio'),
    'unobtainium': ('анобтаниевого', 'анобтанієвого', 'Unobtainium', 'unobtainium', 'Unobtainium'),
}
TIPPED_ALIASES = {'cyclic_haste': 'haste', 'cyclic_absorption': 'absorption', 'quark.resistance': 'quark_resistance',
                  'quark.resilience': 'resilience', 'quark.danger_sight': 'danger_sight', 'insomnia_strong': 'insomnia',
                  'restfulness_strong': 'restfulness'}
# 1.21 potions (Potion.getName gives the base name for the long_ and strong_ variants)
NEW_TIPPED_EN = {'wind_charged': '%s of Wind Charging', 'weaving': '%s of Weaving', 'oozing': '%s of Oozing',
                 'infested': '%s of Infestation'}


def tipped_names(en):
    out = {}
    for key in en:
        if not key.startswith(P + 'proj_tipped.effect.'):
            continue
        effect = key[len(P + 'proj_tipped.effect.'):]
        base = TIPPED_ALIASES.get(effect, effect)
        while re.search(r'^(long|strong)_|_(long|strong)$', base):
            base = re.sub(r'^(long|strong)_|_(long|strong)$', '', base)
        base = TIPPED_ALIASES.get(base, base)
        if base == 'knockback_potion':
            out[key] = ('Отбрасывающая %s', 'Відкидальна %s', '%s des Rückstoßes', '%s de empuje', '%s de Repulsão')
        elif base == 'noisy_potion':
            out[key] = ('Шумная %s', 'Галаслива %s', 'Lauter %s', '%s ruidosa', '%s Barulhenta')
        elif base.endswith('_sight') and base not in TIPPED:
            ru, uk, de, es, pt = SIGHT[base[:-len('_sight')]]
            out[key] = ('%s ' + ru + ' восприятия', '%s ' + uk + ' сприйняття', '%s der ' + de + 'sicht',
                        '%s de visión de ' + es, '%s de Visão de ' + pt)
        elif base in ('water', 'empty'):
            out[key] = TIPPED[base]
        else:
            ru, uk, de, es, pt = TIPPED[base]
            out[key] = ('%s ' + ru, '%s ' + uk, '%s ' + de, '%s ' + es, '%s ' + pt)
    return out


# ----------------------------------------------------------------------------------------------------- the rest
# key (without the "spartanweaponry" part: X) -> (uk, de)
T = {
    'itemGroup.X.basic': ('Spartan Weaponry: ванільні матеріали', 'Spartan Weaponry: Vanilla-Materialien'),
    'itemGroup.X.modded': ('Spartan Weaponry: матеріали з модів', 'Spartan Weaponry: Mod-Materialien'),
    'itemGroup.X.arrows_bolts': ('Spartan Weaponry: стріли та болти', 'Spartan Weaponry: Pfeile & Bolzen'),
    'item.X.simple_handle': ('Примітивне руків\'я', 'Einfacher Griff'),
    'item.X.handle': ('Руків\'я', 'Griff'),
    'item.X.simple_pole': ('Примітивне держално', 'Einfacher Schaft'),
    'item.X.pole': ('Держално', 'Schaft'),
    'item.X.explosive_charge': ('Вибуховий заряд', 'Sprengladung'),
    'item.X.greaseball': ('Кулька мастила', 'Fettkugel'),
    'item.X.grease_ball': ('Кулька мастила', 'Fettkugel'),
    'item.X.explosive_arrow': ('Вибухова стріла', 'Explosivpfeil'),
    'item.X.bolt': ('Болт', 'Bolzen'),
    'item.X.spectral_bolt': ('Спектральний болт', 'Spektralbolzen'),
    'item.X.proj_tipped': ('%s з наконечником', 'Getränkter %s'),
    'item.X.small_arrow_quiver': ('Малий сагайдак для стріл', 'Kleiner Pfeilköcher'),
    'item.X.medium_arrow_quiver': ('Середній сагайдак для стріл', 'Mittlerer Pfeilköcher'),
    'item.X.large_arrow_quiver': ('Великий сагайдак для стріл', 'Großer Pfeilköcher'),
    'item.X.huge_arrow_quiver': ('Величезний сагайдак для стріл', 'Riesiger Pfeilköcher'),
    'item.X.small_bolt_quiver': ('Малий сагайдак для болтів', 'Kleiner Bolzenköcher'),
    'item.X.medium_bolt_quiver': ('Середній сагайдак для болтів', 'Mittlerer Bolzenköcher'),
    'item.X.large_bolt_quiver': ('Великий сагайдак для болтів', 'Großer Bolzenköcher'),
    'item.X.huge_bolt_quiver': ('Величезний сагайдак для болтів', 'Riesiger Bolzenköcher'),
    'item.X.quiver_compartment': ('Відділення сагайдака / ковальський «шаблон»', 'Köcherfach / Schmiede-„Vorlage“'),
    'item.X.medium_quiver_brace': ('Середнє кріплення сагайдака', 'Mittlere Köcherverstärkung'),
    'item.X.large_quiver_brace': ('Велике кріплення сагайдака', 'Große Köcherverstärkung'),
    'item.X.huge_quiver_brace': ('Величезне кріплення сагайдака', 'Riesige Köcherverstärkung'),
    'item.X.dynamite': ('Динаміт', 'Dynamit'),
    'item.X.weapon_oil': ('Збройова олія', 'Waffenöl'),
    'item.X.weapon_oil.none': ('Основа збройової олії', 'Waffenöl-Basis'),
    'item.X.weapon_oil.undead': ('Освячена олія', 'Geweihtes Öl'),
    'item.X.weapon_oil.undead_strong': ('Сильна освячена олія', 'Starkes geweihtes Öl'),
    'item.X.weapon_oil.arthropod': ('Олія проти членистоногих', 'Gliederfüßer-Öl'),
    'item.X.weapon_oil.arthropod_strong': ('Сильна олія проти членистоногих', 'Starkes Gliederfüßer-Öl'),
    'item.X.weapon_oil.cryotic': ('Кріогенна олія', 'Kryo-Öl'),
    'item.X.weapon_oil.cryotic_strong': ('Сильна кріогенна олія', 'Starkes Kryo-Öl'),
    'item.X.weapon_oil.necrotic': ('Некротична олія', 'Nekrotisches Öl'),
    'item.X.weapon_oil.necrotic_strong': ('Сильна некротична олія', 'Starkes nekrotisches Öl'),
    'item.X.weapon_oil.creeper': ('Олія проти криперів', 'Creeper-Öl'),
    'item.X.weapon_oil.creeper_strong': ('Сильна олія проти криперів', 'Starkes Creeper-Öl'),
    'item.X.weapon_oil.aquatic': ('Водна олія', 'Wasser-Öl'),
    'item.X.weapon_oil.aquatic_strong': ('Сильна водна олія', 'Starkes Wasser-Öl'),
    'item.X.weapon_oil.ender': ('Ендер-олія', 'Ender-Öl'),
    'item.X.weapon_oil.ender_strong': ('Сильна ендер-олія', 'Starkes Ender-Öl'),
    'item.X.weapon_oil.poison': ('Отруйна олія', 'Giftöl'),
    'item.X.weapon_oil.poison_long': ('Тривка отруйна олія', 'Anhaltendes Giftöl'),
    'item.X.weapon_oil.poison_strong': ('Сильна отруйна олія', 'Starkes Giftöl'),
    'item.X.weapon_oil.wither': ('Іссушувальна олія', 'Wither-Öl'),
    'item.X.weapon_oil.wither_long': ('Тривка іссушувальна олія', 'Anhaltendes Wither-Öl'),
    'item.X.weapon_oil.wither_strong': ('Сильна іссушувальна олія', 'Starkes Wither-Öl'),
    'item.X.weapon_oil.potion': ('Олія', 'Öl'),
    'item.X.parrying_dagger': ('Парувальний кинджал', 'Parierdolch'),
    'item.X.silent_gear_parrying_dagger': ('Парувальний кинджал', 'Parierdolch'),
    'item.X.silent_gear_parrying_dagger.nameProper': ('Парувальний кинджал (%s)', '%s-Parierdolch'),
    'item.X.blueprint.parrying_dagger.desc': ('Захист старої школи', 'Verteidigung der alten Schule'),
    'part.X.parrying_dagger_blade': ('Клинок парувального кинджала', 'Parierdolchklinge'),
    'item.X.parrying_dagger_blade': ('Складений клинок парувального кинджала', 'Zusammengesetzte Parierdolchklinge'),
    'item.X.parrying_dagger_blade.nameProper': ('Клинок парувального кинджала (%s)', '%s-Parierdolchklinge'),
    'gearType.silentgear.parrying_dagger': ('Парувальний кинджал', 'Parierdolch'),
    'block.X.blaze_head': ('Голова ифрита', 'Lohenkopf'),
    'block.X.enderman_head': ('Голова ендермена', 'Endermankopf'),
    'block.X.spider_head': ('Голова павука', 'Spinnenkopf'),
    'block.X.cave_spider_head': ('Голова печерного павука', 'Höhlenspinnenkopf'),
    'block.X.piglin_head': ('Голова пігліна', 'Piglinkopf'),
    'block.X.zombified_piglin_head': ('Голова зомбіфікованого пігліна', 'Zombifizierter-Piglin-Kopf'),
    'block.X.husk_head': ('Голова кадавра', 'Wüstenzombiekopf'),
    'block.X.stray_skull': ('Череп зимогора', 'Eiswandererschädel'),
    'block.X.drowned_head': ('Голова утопленика', 'Ertrunkenenkopf'),
    'block.X.illager_head': ('Голова злоселянина', 'Illagerkopf'),
    'block.X.witch_head': ('Голова відьми', 'Hexenkopf'),
    'entity.X.arrow': ('Стріла', 'Pfeil'),
    'entity.X.explosive_arrow': ('Вибухова стріла', 'Explosivpfeil'),
    'entity.X.bolt': ('Болт', 'Bolzen'),
    'entity.X.spectral_bolt': ('Спектральний болт', 'Spektralbolzen'),
    'entity.X.throwing_weapon': ('Метальна зброя', 'Wurfwaffe'),
    'entity.X.throwing_knife': ('Метальний ніж', 'Wurfmesser'),
    'entity.X.tomahawk': ('Томагавк', 'Tomahawk'),
    'entity.X.javelin': ('Метальний спис', 'Wurfspeer'),
    'entity.X.boomerang': ('Бумеранг', 'Bumerang'),
    'enchantment.X.propel': ('Поштовх', 'Antrieb'),
    'enchantment.X.propel.desc': ('Збільшує дальність кидка цієї зброї', 'Erhöht die Wurfweite dieser Waffe'),
    'enchantment.X.razors_edge': ('Гостре лезо', 'Rasiermesserschärfe'),
    'enchantment.X.razors_edge.desc': ('Збільшує шкоду цієї метальної зброї', 'Erhöht den Schaden dieser Wurfwaffe'),
    'enchantment.X.incendiary': ('Підпал', 'Brandwurf'),
    'enchantment.X.incendiary.desc': ('Наповнює цю метальну зброю вогнем, що підпалює уражені цілі',
                                      'Erfüllt diese Wurfwaffe mit Feuer, das getroffene Ziele in Brand setzt'),
    'enchantment.X.lucky_throw': ('Вдалий кидок', 'Glückswurf'),
    'enchantment.X.lucky_throw.desc': ('Збільшує здобич, яку дає ця метальна зброя',
                                       'Erhöht die Beute, die diese Wurfwaffe einbringt'),
    'enchantment.X.hydrodynamic': ('Гідродинаміка', 'Hydrodynamik'),
    'enchantment.X.hydrodynamic.desc': ('Дає метальній зброї змогу діяти під водою',
                                        'Lässt Wurfwaffen unter Wasser funktionieren'),
    'enchantment.X.supercharge': ('Надзаряд', 'Superladung'),
    'enchantment.X.supercharge.desc': ('Зменшує час замаху метальної зброї приблизно на 20%% за рівень',
                                       'Verringert die Aufladezeit von Wurfwaffen um ca. 20%% pro Stufe'),
    'enchantment.X.expanse': ('Простір', 'Weite'),
    'enchantment.X.expanse.desc': ('Збільшує найбільший запас метальної зброї на 25%% або на одну одиницю за рівень, '
                                   'залежно від того, що більше',
                                   'Erhöht die maximale Munition von Wurfwaffen pro Stufe um 25%% oder um eins, je '
                                   'nachdem, was größer ist'),
    'enchantment.X.sharpshooter': ('Снайпер', 'Scharfschütze'),
    'enchantment.X.sharpshooter.desc': ('Зменшує час прицілювання важких арбалетів на 0,1 с за рівень',
                                        'Verringert die Zielzeit schwerer Armbrüste um 0,1 s pro Stufe'),
    'enchantment.X.collectorang': ('Збирач', 'Sammelrang'),
    'enchantment.X.collectorang.desc': ('Бумеранг підбирає предмети з землі: 1 стос або сферу досвіду за рівень',
                                        'Der Bumerang sammelt Gegenstände vom Boden auf: 1 Stapel bzw. '
                                        'Erfahrungskugel pro Stufe'),
    'tooltip.X.material.wood': ('Дерево', 'Holz'),
    'tooltip.X.material.stone': ('Камінь', 'Stein'),
    'tooltip.X.material.leather': ('Шкіра', 'Leder'),
    'tooltip.X.material.copper': ('Мідь', 'Kupfer'),
    'tooltip.X.material.iron': ('Залізо', 'Eisen'),
    'tooltip.X.material.gold': ('Золото', 'Gold'),
    'tooltip.X.material.diamond': ('Діамант', 'Diamant'),
    'tooltip.X.material.netherite': ('Незерит', 'Netherit'),
    'tooltip.X.material.tin': ('Олово', 'Zinn'),
    'tooltip.X.material.bronze': ('Бронза', 'Bronze'),
    'tooltip.X.material.steel': ('Сталь', 'Stahl'),
    'tooltip.X.material.silver': ('Срібло', 'Silber'),
    'tooltip.X.material.electrum': ('Електрум', 'Elektrum'),
    'tooltip.X.material.lead': ('Свинець', 'Blei'),
    'tooltip.X.material.nickel': ('Нікель', 'Nickel'),
    'tooltip.X.material.invar': ('Інвар', 'Invar'),
    'tooltip.X.material.constantan': ('Константан', 'Konstantan'),
    'tooltip.X.material.platinum': ('Платина', 'Platin'),
    'tooltip.X.material.aluminum': ('Алюміній', 'Aluminium'),
    'tooltip.X.traits': ('Особливості: %s', 'Eigenschaften: %s'),
    'tooltip.X.show_details': ('[Натисніть %s, щоб показати подробиці]', '[%s drücken für Details]'),
    'tooltip.X.showing_details': ('[Показано подробиці]', '[Details werden angezeigt]'),
    'tooltip.X.trait.material_bonus': ('Бонус матеріалу:', 'Materialbonus:'),
    'tooltip.X.trait.invalid.archetype_tag': ('ПОМИЛКА: не знайдено тег особливостей зброї «%s» для типу «%s»',
                                              'FEHLER: Waffeneigenschafts-Tag „%s“ des Typs „%s“ nicht gefunden'),
    'tooltip.X.trait.invalid.material_tag': ('ПОМИЛКА: не знайдено тег особливостей зброї «%s» для матеріалу «%s»',
                                             'FEHLER: Waffeneigenschafts-Tag „%s“ des Materials „%s“ nicht gefunden'),
    'tooltip.X.trait.invalid.multiple_action_traits': ('ПОМИЛКА: зброя не може мати більше однієї особливості дії',
                                                       'FEHLER: Eine Waffe kann nur eine Aktionseigenschaft haben'),
    'tooltip.X.trait.invalid.material_action_trait': ('ПОМИЛКА: особливості дії не можна застосувати до матеріалів',
                                                      'FEHLER: Aktionseigenschaften können nicht auf Materialien '
                                                      'angewendet werden'),
    'tooltip.X.trait.invalid.weapon_not_melee': ('ПОМИЛКА: особливість ближнього бою не можна застосувати до цієї '
                                                 'зброї', 'FEHLER: Nahkampfeigenschaft ist für diese Waffe nicht '
                                                 'möglich'),
    'tooltip.X.trait.invalid.weapon_not_ranged': ('ПОМИЛКА: особливість дальнього бою не можна застосувати до цієї '
                                                  'зброї', 'FEHLER: Fernkampfeigenschaft ist für diese Waffe nicht '
                                                  'möglich'),
    'tooltip.X.trait.invalid.weapon_not_throwing': ('ПОМИЛКА: особливість кидання не можна застосувати до цієї зброї',
                                                    'FEHLER: Wurfeigenschaft ist für diese Waffe nicht möglich'),
    'tooltip.X.trait.invalid.weapon_not_supported': ('ПОМИЛКА: недійсну особливість не можна застосувати до цієї '
                                                     'зброї', 'FEHLER: Ungültige Eigenschaft ist für diese Waffe '
                                                     'nicht möglich'),
    'tooltip.X.trait.type.melee': ('Ближній бій', 'Nahkampf'),
    'tooltip.X.trait.type.ranged': ('Дальній бій', 'Fernkampf'),
    'tooltip.X.trait.type.throwing': ('Кидання', 'Wurf'),
    'tooltip.X.trait.type.action': ('Дія', 'Aktion'),
    'tooltip.X.trait.throwable': ('Метальна', 'Werfbar'),
    'tooltip.X.trait.throwable.desc': ('Цю зброю можна кидати у ворогів', 'Diese Waffe kann auf Gegner geworfen '
                                       'werden'),
    'tooltip.X.trait.block_melee': ('Блок ближнього бою', 'Nahkampfblock'),
    'tooltip.X.trait.block_melee.desc': ('Блокує атаки ближнього бою, але не снаряди',
                                         'Kann Nahkampfangriffe blocken, aber keine Geschosse'),
    'tooltip.X.trait.two_handed': ('Дворучна %s', 'Zweihändig %s'),
    'tooltip.X.trait.two_handed.desc': ('Будь-який предмет у другій руці сповільнює атаку й зменшує шкоду на %d%%',
                                        'Jeder Gegenstand in der anderen Hand verlangsamt Angriffe und verringert '
                                        'den Schaden um %d%%'),
    'tooltip.X.trait.extra_damage_chest': ('Бонус шкоди без нагрудника', 'Brustschadensbonus'),
    'tooltip.X.trait.extra_damage_chest.desc': ('+%f%% шкоди, коли ворог без нагрудника',
                                                '+%f%% Schaden, wenn der Gegner keinen Brustpanzer trägt'),
    'tooltip.X.trait.extra_damage_chest.desc.armor_points': ('+%f%% шкоди, коли ворог без нагрудника й має менше %f '
                                                             'одиниць броні',
                                                             '+%f%% Schaden, wenn der Gegner keinen Brustpanzer '
                                                             'trägt und weniger als %f Rüstungspunkte hat'),
    'tooltip.X.trait.extra_damage_helmet': ('Бонус шкоди в голову', 'Kopfschadensbonus'),
    'tooltip.X.trait.extra_damage_helmet.desc': ('+%f%% шкоди, коли ворог без шолома',
                                                 '+%f%% Schaden, wenn der Gegner keinen Helm trägt'),
    'tooltip.X.trait.extra_damage_helmet.desc.armor_points': ('+%f%% шкоди, коли ворог без шолома й має менше %f '
                                                              'одиниць броні',
                                                              '+%f%% Schaden, wenn der Gegner keinen Helm trägt und '
                                                              'weniger als %f Rüstungspunkte hat'),
    'tooltip.X.trait.extra_damage_unarmoured': ('Бонус шкоди без броні', 'Schadensbonus gegen Ungerüstete'),
    'tooltip.X.trait.extra_damage_unarmoured.desc': ('+%f%% шкоди, коли ворог без броні',
                                                     '+%f%% Schaden, wenn der Gegner keine Rüstung trägt'),
    'tooltip.X.trait.extra_damage_unarmoured.desc.armor_points': ('+%f%% шкоди, коли ворог без броні й має менше %f '
                                                                  'одиниць броні',
                                                                  '+%f%% Schaden, wenn der Gegner keine Rüstung '
                                                                  'trägt und weniger als %f Rüstungspunkte hat'),
    'tooltip.X.trait.extra_damage_riding': ('Бонус шкоди верхи', 'Reitschadensbonus'),
    'tooltip.X.trait.extra_damage_riding.desc': ('+%f%% шкоди верхи на тварині чи в транспорті',
                                                 '+%f%% Schaden beim Reiten auf einem Reittier oder in einem Fahrzeug'),
    'tooltip.X.trait.extra_damage_thrown': ('Бонус шкоди кидка %s', 'Wurfschadensbonus %s'),
    'tooltip.X.trait.extra_damage_thrown.desc': ('+%f%% шкоди, коли цю зброю кинуто',
                                                 '+%f%% Schaden, wenn diese Waffe geworfen wird'),
    'tooltip.X.trait.extra_damage_undead': ('Бонус шкоди нежиті', 'Schadensbonus gegen Untote'),
    'tooltip.X.trait.extra_damage_undead.desc': ('+%f%% шкоди нежиті', '+%f%% Schaden gegen Untote'),
    'tooltip.X.trait.extra_damage_backstab': ('Бонус удару в спину', 'Rückenstichbonus'),
    'tooltip.X.trait.extra_damage_backstab.desc': ('+%.2f%% шкоди, коли зброя влучає ворогові в спину',
                                                   '+%.2f%% Schaden, wenn diese Waffe einen Gegner in den Rücken '
                                                   'trifft'),
    'tooltip.X.trait.damage_absorb': ('Поглинання шкоди', 'Schadensabsorption'),
    'tooltip.X.trait.damage_absorb.desc': ('%d%% отриманої шкоди йде на міцність цієї зброї',
                                           '%d%% des erlittenen Schadens geht auf die Haltbarkeit dieser Waffe'),
    'tooltip.X.trait.reach': ('Досяжність %s', 'Reichweite %s'),
    'tooltip.X.trait.reach.desc': ('Більша дальність ближнього бою. Вищі рівні дають більшу дальність',
                                   'Größere Nahkampfreichweite. Höhere Stufen reichen weiter'),
    'tooltip.X.trait.sweep_damage': ('Розмах %s', 'Schwung %s'),
    'tooltip.X.trait.sweep_damage.desc': ('Усі цілі в зоні розмаху отримують %d%% звичайної шкоди',
                                          'Alle Ziele im Schwungbereich erleiden %d%% des normalen Schadens'),
    'tooltip.X.trait.sweep_damage.fixed.desc': ('Усі цілі в зоні розмаху отримують %f шкоди',
                                                'Alle Ziele im Schwungbereich erleiden %f Schaden'),
    'tooltip.X.trait.knockback': ('Посилене відкидання', 'Verstärkter Rückstoß'),
    'tooltip.X.trait.knockback.desc': ('Ворогів відкидає далі', 'Gegner werden weiter zurückgestoßen'),
    'tooltip.X.trait.nausea': ('Приголомшливий удар', 'Betäubender Schlag'),
    'tooltip.X.trait.nausea.desc': ('Накладає нудоту II (%d с) при влучанні, якщо ворог без шолома',
                                    'Verursacht beim Treffer Übelkeit II (%d s), außer der Gegner trägt einen Helm'),
    'tooltip.X.trait.armour_piercing': ('Бронебійність', 'Rüstungsdurchschlag'),
    'tooltip.X.trait.armour_piercing.desc': ('%d%% завданої шкоди ігнорує броню',
                                             '%d%% des verursachten Schadens ignorieren Rüstung'),
    'tooltip.X.trait.shield_breach': ('Пробиття щита', 'Schilddurchbruch'),
    'tooltip.X.trait.shield_breach.desc': ('Шанс пробити щит ворога', 'Chance, den Schild eines Gegners zu '
                                           'durchbrechen'),
    'tooltip.X.trait.versatile': ('Універсальна (%s)', 'Vielseitig (%s)'),
    'tooltip.X.trait.versatile.desc': ('Добуває блоки як зазначений інструмент',
                                       'Kann Blöcke wie das angegebene Werkzeug abbauen'),
    'tooltip.X.trait.versatile.nothing': ('...нічого?', '...nichts?'),
    'tooltip.X.trait.versatile.axe': ('Сокира', 'Axt'),
    'tooltip.X.trait.versatile.pickaxe': ('Кайло', 'Spitzhacke'),
    'tooltip.X.trait.versatile.shovel': ('Лопата', 'Schaufel'),
    'tooltip.X.trait.quick_strike': ('Швидкий удар', 'Schnellschlag'),
    'tooltip.X.trait.quick_strike.desc': ('Ця зброя завдає шквал швидких ударів з дуже коротким часом невразливості '
                                          'цілі', 'Diese Waffe kann eine Reihe schneller Schläge mit sehr kurzer '
                                          'Unverwundbarkeitszeit austeilen'),
    'tooltip.X.trait.fireproof': ('Вогнетривка', 'Feuerfest'),
    'tooltip.X.trait.fireproof.desc': ('Не знищується у вогні чи лаві', 'Wird in Feuer oder Lava nicht zerstört'),
    'tooltip.X.trait.lightweight': ('Легка %s', 'Leicht %s'),
    'tooltip.X.trait.lightweight.desc': ('Пришвидшує атаку, натягування, заряджання та замах для кидка',
                                         'Beschleunigt Angriff, Spannen, Laden und Aufladen von Würfen'),
    'tooltip.X.trait.heavy': ('Важка %s', 'Schwer %s'),
    'tooltip.X.trait.heavy.desc': ('Сповільнює атаку, натягування, заряджання та замах для кидка',
                                   'Verlangsamt Angriff, Spannen, Laden und Aufladen von Würfen'),
    'tooltip.X.trait.decapitate': ('Обезголовлення', 'Enthaupten'),
    'tooltip.X.trait.decapitate.desc': ('%f%% шансу, що з убитого моба випаде голова чи череп',
                                        '%f%% Chance, beim Töten eines Mobs einen Kopf oder Schädel zu erhalten'),
    'tooltip.X.trait.harvester': ('Женець', 'Ernter'),
    'tooltip.X.trait.harvester.desc': ('Збирає дозрілий урожай на площі 3×3', 'Erntet reife Pflanzen in einem '
                                       '3×3-Bereich'),
    'tooltip.X.trait.hammer_slam': ('Удар молотом', 'Hammerschlag'),
    'tooltip.X.trait.hammer_slam.desc': ('Атакує всіх ворогів на невеликій площі на половину шкоди зброї',
                                         'Greift alle Gegner in einem kleinen Bereich mit halbem Waffenschaden an'),
    'tooltip.X.simple_handle.desc': ('Примітивне руків\'я для простої зброї. Застосуйте палицю на траві, щоб зробити',
                                     'Ein einfacher Griff für einfache Waffen. Benutze einen Stock an Gras, um ihn '
                                     'herzustellen'),
    'tooltip.X.handle.desc': ('Якісніше руків\'я для більшості зброї', 'Ein hochwertigerer Griff für die meisten '
                              'Waffen'),
    'tooltip.X.simple_pole.desc': ('Примітивне держално для простої древкової зброї',
                                   'Ein einfacher Schaft für einfache Stangenwaffen'),
    'tooltip.X.pole.desc': ('Якісніше держално для древкової зброї', 'Ein hochwertigerer Schaft für Stangenwaffen'),
    'tooltip.X.explosive_charge.desc': ('Нестабільний заряд для вибухових стріл і динаміту',
                                        'Eine instabile Ladung für Explosivpfeile und Dynamit'),
    'tooltip.X.grease_ball.desc': ('Основа олій. Покладіть у пляшечки, щоб почати варити олію',
                                   'Grundzutat für Öle. In Flaschen geben, um mit der Ölherstellung zu beginnen'),
    'tooltip.X.dynamite.desc': ('Вибухає через %f с після кидка, слабше за динамітний блок',
                                'Explodiert %f Sekunden nach dem Wurf, schwächer als TNT'),
    'tooltip.X.dynamite.terrain_damage': ('Руйнує місцевість, якщо ввімкнено mobGriefing',
                                          'Beschädigt das Gelände, wenn mobGriefing aktiv ist'),
    'tooltip.X.dynamite.no_terrain_damage': ('Не руйнує місцевість, навіть якщо ввімкнено mobGriefing',
                                             'Beschädigt das Gelände nicht, auch wenn mobGriefing aktiv ist'),
    'tooltip.X.weapon_oil.desc': ('Змастіть зброю цією олією, щоб надати їй особливих бонусів. Має обмежену кількість '
                                  'використань.', 'Überziehe Waffen mit diesem Öl, um ihnen besondere Boni zu geben. '
                                  'Begrenzte Anwendungen.'),
    'tooltip.X.blaze_head.desc': ('Випадає з ифрита, убитого зброєю з особливістю «Обезголовлення»',
                                  'Erhältlich durch Töten einer Lohe mit einer Waffe mit der Eigenschaft „Enthaupten“'),
    'tooltip.X.enderman_head.desc': ('Випадає з ендермена, убитого зброєю з особливістю «Обезголовлення»',
                                     'Erhältlich durch Töten eines Endermans mit einer Waffe mit der Eigenschaft '
                                     '„Enthaupten“'),
    'tooltip.X.spider_head.desc': ('Випадає з павука, убитого зброєю з особливістю «Обезголовлення»',
                                   'Erhältlich durch Töten einer Spinne mit einer Waffe mit der Eigenschaft '
                                   '„Enthaupten“'),
    'tooltip.X.cave_spider_head.desc': ('Випадає з печерного павука, убитого зброєю з особливістю «Обезголовлення»',
                                        'Erhältlich durch Töten einer Höhlenspinne mit einer Waffe mit der '
                                        'Eigenschaft „Enthaupten“'),
    'tooltip.X.piglin_head.desc': ('Випадає з пігліна, убитого зброєю з особливістю «Обезголовлення»',
                                   'Erhältlich durch Töten eines Piglins mit einer Waffe mit der Eigenschaft '
                                   '„Enthaupten“'),
    'tooltip.X.zombified_piglin_head.desc': ('Випадає із зомбіфікованого пігліна, убитого зброєю з особливістю '
                                             '«Обезголовлення»', 'Erhältlich durch Töten eines zombifizierten Piglins '
                                             'mit einer Waffe mit der Eigenschaft „Enthaupten“'),
    'tooltip.X.husk_head.desc': ('Випадає з кадавра, убитого зброєю з особливістю «Обезголовлення»',
                                 'Erhältlich durch Töten eines Wüstenzombies mit einer Waffe mit der Eigenschaft '
                                 '„Enthaupten“'),
    'tooltip.X.stray_skull.desc': ('Випадає із зимогора, убитого зброєю з особливістю «Обезголовлення»',
                                   'Erhältlich durch Töten eines Eiswanderers mit einer Waffe mit der Eigenschaft '
                                   '„Enthaupten“'),
    'tooltip.X.drowned_head.desc': ('Випадає з утопленика, убитого зброєю з особливістю «Обезголовлення»',
                                    'Erhältlich durch Töten eines Ertrunkenen mit einer Waffe mit der Eigenschaft '
                                    '„Enthaupten“'),
    'tooltip.X.illager_head.desc': ('Випадає з розбійника, поборника, ілюзіоніста чи заклинача, убитого зброєю з '
                                    'особливістю «Обезголовлення»', 'Erhältlich durch Töten eines Plünderers, Diener, '
                                    'Illusionisten oder Magiers mit einer Waffe mit der Eigenschaft „Enthaupten“'),
    'tooltip.X.witch_head.desc': ('Випадає з відьми, убитої зброєю з особливістю «Обезголовлення»',
                                  'Erhältlich durch Töten einer Hexe mit einer Waffe mit der Eigenschaft '
                                  '„Enthaupten“'),
    'tooltip.X.medium_quiver_brace.desc': ('Матеріал для покращення малого сагайдака до середнього на ковальському '
                                           'столі', 'Material, um kleine Köcher am Schmiedetisch zu mittleren '
                                           'aufzuwerten'),
    'tooltip.X.large_quiver_brace.desc': ('Матеріал для покращення середнього сагайдака до великого на ковальському '
                                          'столі', 'Material, um mittlere Köcher am Schmiedetisch zu großen '
                                          'aufzuwerten'),
    'tooltip.X.huge_quiver_brace.desc': ('Матеріал для покращення великого сагайдака до величезного на ковальському '
                                         'столі', 'Material, um große Köcher am Schmiedetisch zu riesigen '
                                         'aufzuwerten'),
    'tooltip.X.quiver_compartment.applies_to': ('Сагайдаки для стріл і болтів', 'Pfeil- und Bolzenköcher'),
    'tooltip.X.quiver_compartment.ingredients': ('Кріплення сагайдака наступного рівня',
                                                 'Köcherverstärkung der nächsten Stufe'),
    'tooltip.X.quiver_compartment.upgrade_description': ('Збільшення місткості сагайдака',
                                                         'Köcherkapazitäts-Aufwertung'),
    'tooltip.X.quiver_compartment.base_slot_description': ('Комірка сагайдака', 'Köcherplatz'),
    'tooltip.X.quiver_compartment.additonal_slot_description': ('Комірка кріплення сагайдака',
                                                                'Köcherverstärkungsplatz'),
    'tooltip.X.description': ('Опис: %s', 'Beschreibung: %s'),
    'tooltip.X.show_description': ('[Натисніть %s, щоб показати опис]', '[%s drücken für Beschreibung]'),
    'tooltip.X.showing_description': ('[Показано опис]', '[Beschreibung wird angezeigt]'),
    'tooltip.X.longbow.desc': ('Далекобійна версія звичайного лука', 'Weitreichende Version des normalen Bogens'),
    'tooltip.X.longbow.desc_2': ('Повне натягування триває довше', 'Braucht länger zum vollen Spannen'),
    'tooltip.X.heavy_crossbow.desc': ('Висока шкода й дальність', 'Hoher Schaden & große Reichweite'),
    'tooltip.X.heavy_crossbow.desc_2': ('Прицілювання після заряджання покращує точність',
                                        'Zielen nach dem Laden verbessert die Genauigkeit'),
    'tooltip.X.heavy_crossbow.desc_3': ('Тримайте приціл для точного пострілу', 'Halte das Ziel für einen '
                                        'Präzisionsschuss'),
    'tooltip.X.heavy_crossbow.loaded_bolt': ('Заряджено: %s', 'Geladen mit: %s'),
    'tooltip.X.oilable': ('Можна змастити збройовою олією', 'Kann mit Waffenöl überzogen werden'),
    'tooltip.X.oiled': ('Застосовано: %s', 'Aufgetragen: %s'),
    'tooltip.X.oiled_uses': ('Використання: %d/%d', 'Anwendungen: %d/%d'),
    'tooltip.X.modifiers': ('Модифікатори:', 'Modifikatoren:'),
    'tooltip.X.modifiers.ammo.type': ('Тип набоїв: %s', 'Munitionstyp: %s'),
    'tooltip.X.modifiers.ammo.arrow': ('Стріли', 'Pfeile'),
    'tooltip.X.modifiers.ammo.bolt': ('Болти', 'Bolzen'),
    'tooltip.X.modifiers.longbow.draw_length': ('Час натягування: %s', 'Spannzeit: %s'),
    'tooltip.X.modifiers.longbow.draw_length.value': ('%.2f с', '%.2f s'),
    'tooltip.X.modifiers.longbow.speed_multiplier': ('Швидкість стріли: %s', 'Pfeilgeschwindigkeit: %s'),
    'tooltip.X.modifiers.longbow.speed_multiplier.value': ('x%.2f', 'x%.2f'),
    'tooltip.X.modifiers.heavy_crossbow.load_time': ('Час заряджання: %s', 'Ladezeit: %s'),
    'tooltip.X.modifiers.heavy_crossbow.load_time.value': ('%.2f с', '%.2f s'),
    'tooltip.X.modifiers.heavy_crossbow.aim_time': ('Час прицілювання: %s', 'Zielzeit: %s'),
    'tooltip.X.modifiers.heavy_crossbow.aim_time.value': ('%.2f с', '%.2f s'),
    'tooltip.X.modifiers.projectile.base_damage': ('Базова шкода: %s', 'Grundschaden: %s'),
    'tooltip.X.modifiers.projectile.base_damage.value': ('%.2f', '%.2f'),
    'tooltip.X.modifiers.projectile.range': ('Дальність: %s', 'Reichweite: %s'),
    'tooltip.X.modifiers.projectile.range.value': ('x%.2f', 'x%.2f'),
    'tooltip.X.modifiers.projectile.armor_piercing_factor': ('Бронебійність: %s', 'Rüstungsdurchschlag: %s'),
    'tooltip.X.modifiers.projectile.armor_piercing_factor.value': ('%.2f%%', '%.2f%%'),
    'tooltip.X.modifiers.projectile.impact.explosion': ('Вибухає при влучанні', 'Explodiert beim Aufprall'),
    'tooltip.X.modifiers.projectile.impact.glowing': ('Накладає світіння на уражену істоту',
                                                      'Lässt getroffene Kreaturen leuchten'),
    'tooltip.X.modifiers.max_stack_size': ('Найбільший стос: %d', 'Maximale Stapelgröße: %d'),
    'tooltip.X.throwable.ammo': ('Набої: %s', 'Munition: %s'),
    'tooltip.X.throwable.ammo.value': ('%d/%d', '%d/%d'),
    'tooltip.X.throwable.charge_time': ('Час замаху: %s', 'Aufladezeit: %s'),
    'tooltip.X.throwable.charge_time.value': ('%f с', '%f Sekunden'),
    'tooltip.X.throwable.not_original': ('Не лагодиться й не зачаровується; коли набої скінчаться, порожній стос не '
                                         'лишиться', 'Kann nicht repariert oder verzaubert werden und hinterlässt '
                                         'keinen leeren Stapel, wenn die Munition aufgebraucht ist'),
    'tooltip.X.weapon_oil.uses': ('%d використань', '%d Anwendungen'),
    'tooltip.X.weapon_oil.uses_remaining': ('Лишилося використань: %d', '%d Anwendungen übrig'),
    'tooltip.X.weapon_oil.base': ('Не має дії. Варіть далі на варильній стійці', 'Hat keine Wirkung. Muss im '
                                  'Braustand weitergebraut werden'),
    'tooltip.X.weapon_oil.applied.undead': ('+%f%% шкоди нежиті', '+%f%% Schaden gegen Untote'),
    'tooltip.X.weapon_oil.applied.arthropod': ('+%f%% шкоди членистоногим', '+%f%% Schaden gegen Gliederfüßer'),
    'tooltip.X.weapon_oil.applied.cryotic': ('+%f%% шкоди вогняним мобам', '+%f%% Schaden gegen feurige Mobs'),
    'tooltip.X.weapon_oil.applied.necrotic': ('+%f%% шкоди людиноподібним мобам, включно з гравцями',
                                              '+%f%% Schaden gegen menschenähnliche Mobs, auch Spieler'),
    'tooltip.X.weapon_oil.applied.poison': ('Накладає %s на %f с', 'Verursacht %s für %f Sekunden'),
    'tooltip.X.weapon_oil.applied.wither': ('Накладає %s на %f с', 'Verursacht %s für %f Sekunden'),
    'tooltip.X.weapon_oil.applied.creeper': ('+%f%% шкоди криперам', '+%f%% Schaden gegen Creeper'),
    'tooltip.X.weapon_oil.applied.aquatic': ('+%f%% шкоди водним мобам', '+%f%% Schaden gegen Wassermobs'),
    'tooltip.X.weapon_oil.applied.ender': ('+%f%% шкоди ендер-мобам і накладає %s на %f с',
                                           '+%f%% Schaden gegen Ender-Mobs und verursacht %s für %f Sekunden'),
    'tooltip.X.quiver_capacity': ('Найбільша місткість: ', 'Maximale Kapazität: '),
    'tooltip.X.quiver_capacity.value': ('%d стосів', '%d Stapel'),
    'tooltip.X.quiver_show_contents': ('[Натисніть %s, щоб показати весь вміст]', '[%s drücken für den ganzen '
                                       'Inhalt]'),
    'tooltip.X.quiver_contains': ('Містить:', 'Enthält:'),
    'tooltip.X.quiver_ammo': ('- %dx %s', '- %dx %s'),
    'tooltip.X.quiver_none': ('- Нічого!', '- Nichts!'),
    'tooltip.X.quiver_collect_status': ('Збирання набоїв: ', 'Munition sammeln: '),
    'tooltip.X.quiver_upgrade': ('Покращуйте відділенням сагайдака й кріпленням наступного рівня на ковальському '
                                 'столі', 'Mit einem Köcherfach und einer Köcherverstärkung der nächsten Stufe am '
                                 'Schmiedetisch aufwerten'),
    'tooltip.X.enabled': ('Увімкнено', 'Aktiviert'),
    'tooltip.X.disabled': ('Вимкнено', 'Deaktiviert'),
    'tooltip.X.uncraftable_missing_material': ('Не можна створити: у тегу «%s» немає предметів',
                                               'Nicht herstellbar: kein Gegenstand im Tag „%s“'),
    'tooltip.X.highlight_heavy_crossbow': ('%s [%s x%d]', '%s [%s x%d]'),
    'gui.X.set_priority_slot': ('Задати пріоритетну комірку / змінити набої', 'Vorrangplatz setzen / Munition '
                                'wechseln'),
    'gui.X.ammo_collect_enabled': ('Збирання набоїв увімкнено', 'Munition sammeln aktiviert'),
    'gui.X.ammo_collect_disabled': ('Збирання набоїв вимкнено', 'Munition sammeln deaktiviert'),
    'command.X.apply_oil.success': ('Олію «%s» нанесено на предмет «%s» гравця «%s»',
                                    'Öl „%s“ auf Gegenstand „%s“ von Spieler „%s“ aufgetragen'),
    'command.X.apply_potion_oil.success': ('Олію з зілля «%s» нанесено на предмет «%s» гравця «%s»',
                                           'Trank-Öl „%s“ auf Gegenstand „%s“ von Spieler „%s“ aufgetragen'),
    'command.X.clear_oil.success': ('Олію знято з предмета «%s» гравця «%s»',
                                    'Öl von Gegenstand „%s“ von Spieler „%s“ entfernt'),
    'command.X.apply_oil.error.unknown_oil_effect': ('Невідома дія олії «%s»', 'Unbekannte Ölwirkung „%s“'),
    'command.X.apply_oil.error.invalid_oil_effect': ('Недійсна дія олії «%s»', 'Ungültige Ölwirkung „%s“'),
    'command.X.apply_oil.error.unknown_potion': ('Невідоме зілля «%s»', 'Unbekannter Trank „%s“'),
    'command.X.apply_oil.error.invalid_potion': ('Недійсне зілля «%s»', 'Ungültiger Trank „%s“'),
    'command.X.apply_oil.error.no_item': ('Не вдалося нанести олію: гравець «%s» нічого не тримає',
                                          'Öl kann nicht aufgetragen werden: Spieler „%s“ hält nichts'),
    'command.X.apply_oil.error.incompatible_item': ('Не вдалося нанести олію на «%s»: предмета немає в тегу '
                                                    '«spartanweaponry:oilable_weapons»',
                                                    'Öl kann nicht auf „%s“ aufgetragen werden: Der Gegenstand ist '
                                                    'nicht im Tag „spartanweaponry:oilable_weapons“'),
    'message.X.ammo_collect_toggle': ('Збирання набоїв: %s', 'Munition sammeln: %s'),
    'message.X.quiver_not_found': ('Сагайдак не знайдено!', 'Kein Köcher gefunden!'),
    'message.X.oil_applied': ('%s нанесено на %s', '%s auf %s aufgetragen'),
    'message.X.no_oilable_weapon': ('Не вдалося нанести %s: немає зброї, яку можна змастити',
                                    '%s kann nicht aufgetragen werden: keine ölbare Waffe gefunden'),
    'message.X.weapon_already_oiled': ('Не вдалося нанести %s: %s вже змащено',
                                       '%s kann nicht aufgetragen werden: %s ist bereits geölt'),
    'message.X.oil_depleted': ('%s на %s вичерпалася', '%s auf %s ist verbraucht'),
    'effect.X.ender_disruption': ('Ендер-завада', 'Enderstörung'),
    'effect.X.ender_disruption.description': ('Не дає ураженим мобам телепортуватися перлинами Енду, плодами хоруса й '
                                              'власними здібностями ендер-мобів',
                                              'Verhindert, dass betroffene Mobs sich mit Enderperlen, Chorusfrüchten '
                                              'oder den Fähigkeiten von Ender-Mobs teleportieren'),
    'key.X.category.title': ('Spartan Weaponry', 'Spartan Weaponry'),
    'key.X.access_quiver': ('Відкрити вдягнений сагайдак', 'Angelegten Köcher öffnen'),
    'attribute.X.attack_reach': ('Дальність атаки', 'Angriffsreichweite'),
    'dev.X.wip': ('У розробці', 'In Arbeit'),
    'advancement.X.root.title': ('Spartan Weaponry', 'Spartan Weaponry'),
    'advancement.X.root.desc': ('Зброї вдосталь!', 'Waffen in Hülle und Fülle!'),
    'advancement.X.craft_dagger.title': ('Найкращий друг пройдисвіта', 'Des Schurken bester Freund'),
    'advancement.X.craft_dagger.desc': ('Отримайте кинджал', 'Erhalte einen Dolch'),
    'advancement.X.craft_parrying_dagger.title': ('Захист старої школи', 'Verteidigung der alten Schule'),
    'advancement.X.craft_parrying_dagger.desc': ('Отримайте парувальний кинджал', 'Erhalte einen Parierdolch'),
    'advancement.X.craft_longsword.title': ('Клинок більший', 'Eine größere Klinge'),
    'advancement.X.craft_longsword.desc': ('Отримайте півтораручний меч', 'Erhalte ein Langschwert'),
    'advancement.X.craft_katana.title': ('Клинок самурая', 'Klinge des Samurai'),
    'advancement.X.craft_katana.desc': ('Отримайте катану', 'Erhalte ein Katana'),
    'advancement.X.craft_saber.title': ('Головоріз', 'Draufgänger'),
    'advancement.X.craft_saber.desc': ('Отримайте шаблю', 'Erhalte einen Säbel'),
    'advancement.X.craft_rapier.title': ('До бою!', 'En garde!'),
    'advancement.X.craft_rapier.desc': ('Отримайте рапіру', 'Erhalte ein Rapier'),
    'advancement.X.craft_greatsword.title': ('На голову вище', 'Ein Schnitt über den Rest'),
    'advancement.X.craft_greatsword.desc': ('Отримайте дворучний меч', 'Erhalte einen Zweihänder'),
    'advancement.X.craft_cestus.title': ('Кулачний бій', 'Faustkampf'),
    'advancement.X.craft_cestus.desc': ('Отримайте цестус', 'Erhalte einen Cestus'),
    'advancement.X.craft_club.title': ('Кийком по голові', 'Keulenschwinger'),
    'advancement.X.craft_club.desc': ('Отримайте кийок', 'Erhalte eine Keule'),
    'advancement.X.craft_battle_hammer.title': ('Час молотити!', 'Hammerzeit!'),
    'advancement.X.craft_battle_hammer.desc': ('Отримайте бойовий молот', 'Erhalte einen Streithammer'),
    'advancement.X.craft_warhammer.title': ('Тупа сила', 'Stumpfe Gewalt'),
    'advancement.X.craft_warhammer.desc': ('Отримайте клевець', 'Erhalte einen Kriegshammer'),
    'advancement.X.craft_spear.title': ('Вістря списа', 'Speerspitze'),
    'advancement.X.craft_spear.desc': ('Отримайте спис', 'Erhalte einen Speer'),
    'advancement.X.craft_halberd.title': ('Алебардник', 'Hellebardier'),
    'advancement.X.craft_halberd.desc': ('Отримайте алебарду', 'Erhalte eine Hellebarde'),
    'advancement.X.craft_pike.title': ('Піки до бою!', 'Piken hoch!'),
    'advancement.X.craft_pike.desc': ('Отримайте піку', 'Erhalte eine Pike'),
    'advancement.X.craft_lance.title': ('Четвертий вершник', 'Der vierte Reiter'),
    'advancement.X.craft_lance.desc': ('Отримайте турнірний спис', 'Erhalte eine Lanze'),
    'advancement.X.craft_longbow.title': ('Цілься, хлопці!', 'Zielen, Jungs!'),
    'advancement.X.craft_longbow.desc': ('Отримайте довгий лук', 'Erhalte einen Langbogen'),
    'advancement.X.craft_heavy_crossbow.title': ('Важка артилерія XV століття', 'Schwere Artillerie des 15. '
                                                 'Jahrhunderts'),
    'advancement.X.craft_heavy_crossbow.desc': ('Отримайте важкий арбалет', 'Erhalte eine schwere Armbrust'),
    'advancement.X.craft_throwing_knife.title': ('Необхідне асасину', 'Was ein Assassine braucht'),
    'advancement.X.craft_throwing_knife.desc': ('Отримайте метальний ніж', 'Erhalte ein Wurfmesser'),
    'advancement.X.craft_tomahawk.title': ('Закопати сокиру', 'Das Kriegsbeil begraben'),
    'advancement.X.craft_tomahawk.desc': ('Отримайте томагавк', 'Erhalte einen Tomahawk'),
    'advancement.X.craft_javelin.title': ('Не олімпійський', 'Nicht die olympische Sorte'),
    'advancement.X.craft_javelin.desc': ('Отримайте метальний спис', 'Erhalte einen Wurfspeer'),
    'advancement.X.craft_boomerang.title': ('А він повернеться?', 'Kommt er zurück?'),
    'advancement.X.craft_boomerang.desc': ('Отримайте бумеранг', 'Erhalte einen Bumerang'),
    'advancement.X.craft_battleaxe.title': ('...І моя сокира!', '...Und meine Axt!'),
    'advancement.X.craft_battleaxe.desc': ('Отримайте бойову сокиру', 'Erhalte eine Streitaxt'),
    'advancement.X.craft_flanged_mace.title': ('Трощи й бий!', 'Zerschmettern!'),
    'advancement.X.craft_flanged_mace.desc': ('Отримайте пернач', 'Erhalte einen Streitkolben'),
    'advancement.X.craft_glaive.title': ('Випрямлена коса', 'Begradigte Sensen'),
    'advancement.X.craft_glaive.desc': ('Отримайте глефу', 'Erhalte eine Glefe'),
    'advancement.X.craft_quarterstaff.title': ('Не така вже й гостра палиця!', 'Ein nicht so spitzer Stock!'),
    'advancement.X.craft_quarterstaff.desc': ('Отримайте бойовий посох', 'Erhalte einen Kampfstab'),
    'advancement.X.craft_quiver.title': ('Повний наплічник набоїв!', 'Ein Rucksack voller Munition!'),
    'advancement.X.craft_quiver.desc': ('Отримайте будь-який сагайдак для стріл чи болтів',
                                        'Erhalte einen beliebigen Pfeil- oder Bolzenköcher'),
    'advancement.X.upgrade_quiver.title': ('Ще більший сагайдак?', 'Ein noch größerer Köcher?'),
    'advancement.X.upgrade_quiver.desc': ('Покращте будь-який сагайдак', 'Werte einen beliebigen Köcher auf'),
    'advancement.X.upgrade_quiver_max.title': ('Найбільша місткість', 'Maximale Kapazität'),
    'advancement.X.upgrade_quiver_max.desc': ('Покращте будь-який сагайдак до величезного',
                                              'Werte einen beliebigen Köcher zu einem riesigen auf'),
    'advancement.X.craft_scythe.title': ('Що посієш, те й пожнеш', 'Ernten, was man sät'),
    'advancement.X.craft_scythe.desc': ('Отримайте косу', 'Erhalte eine Sense'),
    'advancement.X.collect_heads.title': ('Голови з пліч!', 'Kopf ab!'),
    'advancement.X.collect_heads.desc': ('Зберіть косою всі голови й черепи (крім голів гравця та дракона Енду)',
                                         'Sammle mit einer Sense jeden Kopf und Schädel (außer Spieler- und '
                                         'Enderdrachenkopf)'),
    'advancement.X.upgrade_netherite.title': ('Покращ мене уламками', 'Aufwertung mit Schrott'),
    'advancement.X.upgrade_netherite.desc': ('Покращте будь-яку діамантову зброю до незеритової',
                                             'Werte eine beliebige Diamantwaffe zur Netheritversion auf'),
    'advancement.X.brew_oil.title': ('Алхімія Білого Вовка', 'Alchemie des Weißen Wolfs'),
    'advancement.X.brew_oil.desc': ('Зваріть олію, що посилює зброю', 'Braue ein Öl, das den Waffenschaden '
                                    'verstärkt'),
    'advancement.X.bounce_kill.title': ('Відскок!', 'Abgeprallt!'),
    'advancement.X.bounce_kill.desc': ('Убийте моба бумерангом, що відскочив від поверхні',
                                       'Töte einen Mob, nachdem dein Bumerang von einer Oberfläche abgeprallt ist'),
    'advancement.X.return_kill.title': ('Обережно! Він повертається!', 'Vorsicht! Er kommt zurück!'),
    'advancement.X.return_kill.desc': ('Убийте моба бумерангом, що повертається до вас',
                                       'Töte einen Mob mit einem Bumerang auf dem Rückweg'),
    'subtitle.X.thrown_weapon_throw': ('Зброю кинуто', 'Waffe geworfen'),
    'subtitle.X.thrown_weapon_hit_mob': ('Зброя влучає в моба', 'Waffe trifft Mob'),
    'subtitle.X.thrown_weapon_hit_ground': ('Зброя влучає в землю', 'Waffe trifft Boden'),
    'subtitle.X.throwing_knife_throw': ('Метальний ніж кинуто', 'Wurfmesser geworfen'),
    'subtitle.X.throwing_knife_hit_mob': ('Метальний ніж влучає в моба', 'Wurfmesser trifft Mob'),
    'subtitle.X.throwing_knife_hit_ground': ('Метальний ніж влучає в землю', 'Wurfmesser trifft Boden'),
    'subtitle.X.tomahawk_throw': ('Томагавк кинуто', 'Tomahawk geworfen'),
    'subtitle.X.tomahawk_hit_mob': ('Томагавк влучає в моба', 'Tomahawk trifft Mob'),
    'subtitle.X.tomahawk_hit_ground': ('Томагавк влучає в землю', 'Tomahawk trifft Boden'),
    'subtitle.X.javelin_throw': ('Метальний спис кинуто', 'Wurfspeer geworfen'),
    'subtitle.X.javelin_hit_mob': ('Метальний спис влучає в моба', 'Wurfspeer trifft Mob'),
    'subtitle.X.javelin_hit_ground': ('Метальний спис влучає в землю', 'Wurfspeer trifft Boden'),
    'subtitle.X.boomerang_throw': ('Бумеранг кинуто', 'Bumerang geworfen'),
    'subtitle.X.boomerang_fly': ('Бумеранг летить', 'Bumerang fliegt'),
    'subtitle.X.boomerang_bounce': ('Бумеранг відскакує', 'Bumerang prallt ab'),
    'subtitle.X.boomerang_hit_mob': ('Бумеранг влучає в моба', 'Bumerang trifft Mob'),
    'subtitle.X.boomerang_hit_ground': ('Бумеранг влучає в землю', 'Bumerang trifft Boden'),
    'subtitle.X.throwing_weapon_loyalty_return': ('Метальна зброя повертається', 'Wurfwaffe kehrt zurück'),
    'subtitle.X.oil_applied': ('Олію нанесено на зброю', 'Öl auf Waffe aufgetragen'),
    'subtitle.X.hammer_slams_into_ground': ('Молот б\'є об землю', 'Hammer schlägt auf den Boden'),
}

# Keys the 1.20.1 ru_ru, es_es, pt_br and zh_cn lacked or had wrong (see the docstring): key -> (ru, es, pt, zh); None
# keeps the language's own string
FIX = {
    'item.X.quiver_compartment': ('Отделение колчана / кузнечный «шаблон»', 'Compartimento de carcaj / «plantilla» '
                                  'de herrería', 'Compartimento de Aljava / "Molde" de Ferraria', None),
    'item.X.medium_quiver_brace': ('Среднее крепление колчана', 'Refuerzo de carcaj mediano',
                                   'Reforço de Aljava Média', None),
    'item.X.large_quiver_brace': ('Большое крепление колчана', 'Refuerzo de carcaj grande', 'Reforço de Aljava Grande',
                                  None),
    'item.X.huge_quiver_brace': ('Огромное крепление колчана', 'Refuerzo de carcaj enorme', 'Reforço de Aljava Enorme',
                                 None),
    'item.X.huge_arrow_quiver': (None, None, None, '巨型箭袋'),
    'item.X.parrying_dagger': ('Парирующий кинжал', 'Daga de parada', 'Adaga de Aparar', '格挡匕首'),
    'item.X.silent_gear_parrying_dagger': ('Парирующий кинжал', 'Daga de parada', 'Adaga de Aparar', '格挡匕首'),
    'item.X.silent_gear_parrying_dagger.nameProper': ('Парирующий кинжал (%s)', 'Daga de parada de %s',
                                                      'Adaga de Aparar de %s', '%s格挡匕首'),
    'item.X.blueprint.parrying_dagger.desc': ('Защита старой школы', 'Defensa de la vieja escuela',
                                              'Defesa à Moda Antiga', '老派防御'),
    'part.X.parrying_dagger_blade': ('Клинок парирующего кинжала', 'Hoja de daga de parada',
                                     'Lâmina de Adaga de Aparar', '格挡匕首刃'),
    'item.X.parrying_dagger_blade': ('Составной клинок парирующего кинжала', 'Hoja compuesta de daga de parada',
                                     'Lâmina Composta de Adaga de Aparar', '复合格挡匕首刃'),
    'item.X.parrying_dagger_blade.nameProper': ('Клинок парирующего кинжала (%s)', 'Hoja de daga de parada de %s',
                                                'Lâmina de Adaga de Aparar de %s', '%s格挡匕首刃'),
    'gearType.silentgear.parrying_dagger': ('Парирующий кинжал', 'Daga de parada', 'Adaga de Aparar', '格挡匕首'),
    'tooltip.X.trait.lightweight': ('Лёгкий %s', None, None, '轻盈 %s'),
    'tooltip.X.trait.lightweight.desc': (None, 'Acelera el ataque, el tensado, la recarga y la carga de lanzamiento',
                                         'Acelera o ataque, o preparo, a recarga e a carga de arremesso',
                                         '提高攻击速度、拉弓速度、装填时间和投掷蓄力时间'),
    'tooltip.X.trait.heavy': ('Тяжёлый %s', 'Pesado %s', 'Pesado %s', None),
    'tooltip.X.quiver_compartment.applies_to': ('Колчаны для стрел и арбалетных снарядов', 'Carcajes de flechas y '
                                                'saetas', 'Aljavas de Flechas e Dardos', '箭袋与弩箭袋'),
    'tooltip.X.quiver_compartment.ingredients': ('Крепление колчана следующего уровня', 'Refuerzo de carcaj del '
                                                 'siguiente nivel', 'Reforço de Aljava do próximo nível',
                                                 '下一级箭袋支架'),
    'tooltip.X.quiver_compartment.upgrade_description': ('Увеличение вместимости колчана', 'Mejora de capacidad de '
                                                         'carcaj', 'Melhoria de Capacidade de Aljava', None),
    'tooltip.X.quiver_compartment.base_slot_description': ('Ячейка колчана', 'Espacio de carcaj', 'Espaço de Aljava',
                                                           '箭袋槽'),
    'tooltip.X.quiver_compartment.additonal_slot_description': ('Ячейка крепления колчана', 'Espacio de refuerzo de '
                                                                'carcaj', 'Espaço de Reforço de Aljava', '箭袋支架槽'),
    'tooltip.X.medium_quiver_brace.desc': ('Материал для улучшения малого колчана до среднего на кузнечном столе',
                                           'Material para mejorar carcajes pequeños a medianos en una mesa de herrería',
                                           'Material para melhorar Aljavas Pequenas para Médias em uma Mesa de Ferraria',
                                           None),
    'tooltip.X.large_quiver_brace.desc': ('Материал для улучшения среднего колчана до большого на кузнечном столе',
                                          'Material para mejorar carcajes medianos a grandes en una mesa de herrería',
                                          'Material para melhorar Aljavas Médias para Grandes em uma Mesa de Ferraria',
                                          None),
    'tooltip.X.huge_quiver_brace.desc': ('Материал для улучшения большого колчана до огромного на кузнечном столе',
                                         'Material para mejorar carcajes grandes a enormes en una mesa de herrería',
                                         'Material para melhorar Aljavas Grandes para Enormes em uma Mesa de Ferraria',
                                         None),
    'enchantment.X.supercharge.desc': ('Уменьшает время зарядки метательного оружия примерно на 20%% за уровень',
                                       None, None, None),
    'enchantment.X.expanse.desc': ('Увеличивает максимальный запас боеприпасов для метательного оружия либо на 25%%, '
                                   'либо на один снаряд за уровень, в зависимости от того, что больше', None, None,
                                   None),
    'tooltip.X.trait.extra_damage_thrown': ('Бонус к урону от метания %s', 'Bonus de daño arrojadizo %s',
                                            'Dano Bônus de Arremesso %s', None),
    'tooltip.X.trait.two_handed.desc': (None, 'Cualquier objeto en la otra mano reduce la velocidad de ataque y el '
                                        'daño en un %d%%', 'Qualquer item na outra mão reduz a velocidade de ataque e '
                                        'o dano em %d%%', None),
    'tooltip.X.trait.versatile': (None, 'Versátil (%s)', 'Versátil (%s)', None),
    'tooltip.X.modifiers.longbow.draw_length': ('Время натяжения: %s', 'Tiempo de tensado: %s', 'Tempo de preparo: %s',
                                                None),
    'tooltip.X.modifiers.longbow.speed_multiplier': ('Скорость стрелы: %s', 'Velocidad de las flechas: %s',
                                                     'Velocidade da Flecha: %s', None),
    'tooltip.X.modifiers.heavy_crossbow.load_time': ('Время зарядки: %s', 'Tiempo de carga: %s',
                                                     'Tempo de recarga: %s', None),
    'tooltip.X.modifiers.heavy_crossbow.aim_time': ('Время прицеливания: %s', 'Tiempo de apuntado: %s',
                                                    'Tempo para mirar: %s', None),
    'tooltip.X.modifiers.projectile.base_damage': ('Базовый урон: %s', 'Daño base: %s', 'Dano Base: %s', None),
    'tooltip.X.modifiers.projectile.range': ('Дальность: %s', 'Rango: %s', 'Distância: %s', None),
    'tooltip.X.modifiers.longbow.draw_length.value': ('%.2f с', None, None, None),
    'tooltip.X.modifiers.heavy_crossbow.load_time.value': ('%.2f с', None, None, None),
    'tooltip.X.modifiers.heavy_crossbow.aim_time.value': ('%.2f с', None, None, None),
    'tooltip.X.weapon_oil.applied.undead': (None, '+%f%% de daño contra no-muertos', '+%f%% de dano contra '
                                            'Mortos-vivos', None),
    'tooltip.X.weapon_oil.applied.poison': (None, 'Aplica %s durante %f segundos', 'Aplica %s por %f segundos', None),
    'tooltip.X.weapon_oil.applied.wither': (None, 'Aplica %s durante %f segundos', 'Aplica %s por %f segundos', None),
    'tooltip.X.weapon_oil.applied.creeper': (None, '+%f%% de daño contra creepers', '+%f%% de dano contra Creepers',
                                             None),
    'tooltip.X.weapon_oil.applied.aquatic': (None, '+%f%% de daño contra mobs acuáticos', '+%f%% de dano contra '
                                             'criaturas aquáticas', None),
    'tooltip.X.weapon_oil.applied.ender': (None, '+%f%% de daño contra mobs del End y aplica %s durante %f segundos',
                                           '+%f%% de dano contra criaturas do End e aplica %s por %f segundos', None),
    'tooltip.X.weapon_oil.applied.arthropod': (None, '+%f%% de daño contra artrópodos', '+%f%% de dano contra '
                                               'Artrópodes', None),
    'tooltip.X.weapon_oil.applied.cryotic': (None, '+%f%% de daño contra mobs de fuego', '+%f%% de dano contra '
                                             'criaturas de fogo', None),
    'tooltip.X.weapon_oil.applied.necrotic': ('+%f%% урона против человекоподобных мобов, включая игроков',
                                              '+%f%% de daño contra mobs humanoides, incluidos los jugadores',
                                              '+%f%% de dano contra criaturas humanoides, incluindo jogadores', None),
    'tooltip.X.quiver_capacity': (None, 'Capacidad máxima de munición: ', 'Capacidade máxima de munição: ', None),
    # 1.20.1 es_es / pt_br: the key was misspelt (pike_custom)
    'item.X.custom_pike': (None, 'Pica de %s', 'Pique de %s', None),
    'item.X.custom_scythe': (None, None, 'Foice de %s', None),
    'item.X.custom_parrying_dagger': (None, None, 'Adaga de Aparar de %s', None),
    # 1.20.1 es_es / pt_br: wrong material
    'item.X.iron_boomerang': (None, 'Bumerán de hierro', None, None),
    'item.X.iron_arrow': (None, None, 'Flecha de Ferro', None),
}
# The rest of the keys es_es and pt_br lacked: key -> (es, pt)
ES_PT = {
    'item.X.simple_handle': ('Mango simple', 'Cabo Simples'),
    'item.X.simple_pole': ('Asta simple', 'Haste Simples'),
    'item.X.weapon_oil.undead_strong': ('Aceite consagrado potente', 'Óleo Consagrado Potente'),
    'item.X.weapon_oil.arthropod': ('Aceite de artrópodo', 'Óleo de Artrópode'),
    'item.X.weapon_oil.arthropod_strong': ('Aceite de artrópodo potente', 'Óleo de Artrópode Potente'),
    'item.X.weapon_oil.cryotic': ('Aceite criótico', 'Óleo Criótico'),
    'item.X.weapon_oil.cryotic_strong': ('Aceite criótico potente', 'Óleo Criótico Potente'),
    'item.X.weapon_oil.necrotic': ('Aceite necrótico', 'Óleo Necrótico'),
    'item.X.weapon_oil.necrotic_strong': ('Aceite necrótico potente', 'Óleo Necrótico Potente'),
    'item.X.weapon_oil.creeper_strong': ('Aceite de creeper potente', 'Óleo de Creeper Potente'),
    'item.X.weapon_oil.aquatic_strong': ('Aceite acuático potente', 'Óleo Aquático Potente'),
    'item.X.weapon_oil.ender_strong': ('Aceite de ender potente', 'Óleo de Ender Potente'),
    'item.X.weapon_oil.poison_long': ('Aceite venenoso prolongado', 'Óleo Venenoso Prolongado'),
    'item.X.weapon_oil.poison_strong': ('Aceite venenoso potente', 'Óleo Venenoso Potente'),
    'item.X.weapon_oil.wither_long': ('Aceite de wither prolongado', 'Óleo de Wither Prolongado'),
    'item.X.weapon_oil.wither_strong': ('Aceite de wither potente', 'Óleo de Wither Potente'),
    'item.X.weapon_oil.potion': ('Aceite', 'Óleo'),
    'entity.X.throwing_knife': ('Cuchillo arrojadizo', 'Faca de Arremesso'),
    'enchantment.X.collectorang': ('Recolectorang', 'Coletorangue'),
    'enchantment.X.collectorang.desc': ('Permite al bumerán recoger objetos del suelo: 1 montón u orbe de experiencia '
                                        'por nivel', 'Permite que o Bumerangue apanhe itens do chão: 1 pilha ou orbe '
                                        'de experiência por nível'),
    'tooltip.X.material.wood': ('Madera', 'Madeira'),
    'tooltip.X.material.stone': ('Piedra', 'Pedra'),
    'tooltip.X.material.leather': ('Cuero', 'Couro'),
    'tooltip.X.material.copper': ('Cobre', 'Cobre'),
    'tooltip.X.material.iron': ('Hierro', 'Ferro'),
    'tooltip.X.material.gold': ('Oro', 'Ouro'),
    'tooltip.X.material.diamond': ('Diamante', 'Diamante'),
    'tooltip.X.material.netherite': ('Netherita', 'Netherita'),
    'tooltip.X.material.tin': ('Estaño', 'Estanho'),
    'tooltip.X.material.bronze': ('Bronce', 'Bronze'),
    'tooltip.X.material.steel': ('Acero', 'Aço'),
    'tooltip.X.material.silver': ('Plata', 'Prata'),
    'tooltip.X.material.electrum': ('Electro', 'Electrum'),
    'tooltip.X.material.lead': ('Plomo', 'Chumbo'),
    'tooltip.X.material.nickel': ('Níquel', 'Níquel'),
    'tooltip.X.material.invar': ('Invar', 'Invar'),
    'tooltip.X.material.constantan': ('Constantán', 'Constantan'),
    'tooltip.X.material.platinum': ('Platino', 'Platina'),
    'tooltip.X.material.aluminum': ('Aluminio', 'Alumínio'),
    'tooltip.X.trait.material_bonus': ('Bonus de material:', 'Bônus de Material:'),
    'tooltip.X.trait.invalid.archetype_tag': ('ERROR: no se encontró la etiqueta de rasgos «%s» del tipo «%s»',
                                              'ERRO: tag de características "%s" do tipo "%s" não encontrada'),
    'tooltip.X.trait.invalid.material_tag': ('ERROR: no se encontró la etiqueta de rasgos «%s» del material «%s»',
                                             'ERRO: tag de características "%s" do material "%s" não encontrada'),
    'tooltip.X.trait.invalid.multiple_action_traits': ('ERROR: un arma no puede tener más de un rasgo de acción',
                                                       'ERRO: uma arma não pode ter mais de uma característica de ação'),
    'tooltip.X.trait.invalid.material_action_trait': ('ERROR: los rasgos de acción no se pueden aplicar a materiales',
                                                      'ERRO: características de ação não podem ser aplicadas a '
                                                      'materiais'),
    'tooltip.X.trait.invalid.weapon_not_melee': ('ERROR: el rasgo cuerpo a cuerpo no se puede aplicar a esta arma',
                                                 'ERRO: a característica corpo a corpo não pode ser aplicada a esta '
                                                 'arma'),
    'tooltip.X.trait.invalid.weapon_not_ranged': ('ERROR: el rasgo a distancia no se puede aplicar a esta arma',
                                                  'ERRO: a característica à distância não pode ser aplicada a esta '
                                                  'arma'),
    'tooltip.X.trait.invalid.weapon_not_throwing': ('ERROR: el rasgo arrojadizo no se puede aplicar a esta arma',
                                                    'ERRO: a característica de arremesso não pode ser aplicada a esta '
                                                    'arma'),
    'tooltip.X.trait.invalid.weapon_not_supported': ('ERROR: el rasgo no válido no se puede aplicar a esta arma',
                                                     'ERRO: a característica inválida não pode ser aplicada a esta '
                                                     'arma'),
    'tooltip.X.trait.type.melee': ('Cuerpo a cuerpo', 'Corpo a corpo'),
    'tooltip.X.trait.type.ranged': ('A distancia', 'À distância'),
    'tooltip.X.trait.type.throwing': ('Arrojadizo', 'Arremesso'),
    'tooltip.X.trait.type.action': ('Acción', 'Ação'),
    'tooltip.X.trait.lightweight': ('Ligero %s', 'Leve %s'),
    'tooltip.X.trait.harvester': ('Cosechador', 'Colhedor'),
    'tooltip.X.trait.harvester.desc': ('Cosecha los cultivos maduros en un área de 3x3',
                                       'Colhe plantações maduras em uma área 3x3'),
    'tooltip.X.trait.hammer_slam': ('Martillazo', 'Martelada'),
    'tooltip.X.trait.hammer_slam.desc': ('Ataca a todos los enemigos de un área pequeña con la mitad del daño del arma',
                                         'Ataca todos os inimigos em uma pequena área com metade do dano da arma'),
    'tooltip.X.simple_handle.desc': ('Un mango primitivo para armas básicas. Usa un palo sobre hierba para crearlo',
                                     'Um cabo primitivo para armas básicas. Use um graveto na grama para criá-lo'),
    'tooltip.X.simple_pole.desc': ('Un asta primitiva para armas de asta básicas',
                                   'Uma haste primitiva para armas de haste básicas'),
    'tooltip.X.quiver_compartment.upgrade_description': ('Mejora de capacidad de carcaj',
                                                         'Melhoria de Capacidade de Aljava'),
    'tooltip.X.oilable': ('Se puede cubrir con aceite para armas', 'Pode ser coberta com Óleo para Armas'),
    'tooltip.X.oiled': ('Aplicado: %s', 'Aplicado: %s'),
    'tooltip.X.oiled_uses': ('Usos: %d/%d', 'Usos: %d/%d'),
    'tooltip.X.weapon_oil.base': ('No tiene efecto. Necesita más destilación en un soporte para pociones',
                                  'Não tem efeito. Precisa ser preparado mais em uma Estante de Poções'),
    'tooltip.X.uncraftable_missing_material': ('No se puede fabricar: falta un objeto en la etiqueta «%s»',
                                               'Não pode ser fabricado: falta um item na tag "%s"'),
    'gui.X.set_priority_slot': ('Fijar espacio prioritario / cambiar munición',
                                'Definir Espaço Prioritário / Trocar Munição'),
    'gui.X.ammo_collect_enabled': ('Recogida de munición activada', 'Coleta de Munição Ativada'),
    'gui.X.ammo_collect_disabled': ('Recogida de munición desactivada', 'Coleta de Munição Desativada'),
    'command.X.apply_oil.success': ("Aceite '%s' aplicado al objeto '%s' del jugador '%s'",
                                    "Óleo '%s' aplicado ao item '%s' do jogador '%s'"),
    'command.X.apply_potion_oil.success': ("Aceite de poción '%s' aplicado al objeto '%s' del jugador '%s'",
                                           "Óleo de poção '%s' aplicado ao item '%s' do jogador '%s'"),
    'command.X.clear_oil.success': ("Aceite eliminado del objeto '%s' del jugador '%s'",
                                    "Óleo removido do item '%s' do jogador '%s'"),
    'command.X.apply_oil.error.unknown_oil_effect': ("Efecto de aceite desconocido '%s'",
                                                     "Efeito de óleo desconhecido '%s'"),
    'command.X.apply_oil.error.invalid_oil_effect': ("Efecto de aceite no válido '%s'", "Efeito de óleo inválido '%s'"),
    'command.X.apply_oil.error.unknown_potion': ("Poción desconocida '%s'", "Poção desconhecida '%s'"),
    'command.X.apply_oil.error.invalid_potion': ("Poción no válida '%s'", "Poção inválida '%s'"),
    'command.X.apply_oil.error.no_item': ("No se puede aplicar el aceite: el jugador '%s' no sostiene nada",
                                          "Não é possível aplicar o óleo: o jogador '%s' não está segurando nada"),
    'command.X.apply_oil.error.incompatible_item': ("No se puede aplicar aceite al objeto '%s': no está en la etiqueta "
                                                    "'spartanweaponry:oilable_weapons'",
                                                    "Não é possível aplicar óleo ao item '%s': ele não está na tag "
                                                    "'spartanweaponry:oilable_weapons'"),
    'message.X.oil_applied': ('%s aplicado a tu %s', '%s aplicado em sua %s'),
    'message.X.no_oilable_weapon': ('No se puede aplicar %s: no hay ningún arma que se pueda aceitar',
                                    'Não é possível aplicar %s: nenhuma arma que aceite óleo foi encontrada'),
    'message.X.weapon_already_oiled': ('No se puede aplicar %s: %s ya tiene aceite',
                                       'Não é possível aplicar %s: %s já tem óleo'),
    'message.X.oil_depleted': ('%s de tu %s se ha agotado', '%s em sua %s acabou'),
    'effect.X.ender_disruption': ('Perturbación ender', 'Perturbação Ender'),
    'effect.X.ender_disruption.description': ('Impide que los mobs afectados se teletransporten con perlas de ender, '
                                              'frutas coral o las habilidades de los mobs del End',
                                              'Impede que as criaturas afetadas se teletransportem com Pérolas do Ender, '
                                              'Frutas do Coro ou as habilidades das criaturas do End'),
    'advancement.X.craft_heavy_crossbow.desc': ('Consigue una ballesta pesada', 'Obtenha uma Balestra Larga'),
    'advancement.X.brew_oil.title': ('Alquimia del Lobo Blanco', 'Alquimia do Lobo Branco'),
    'advancement.X.brew_oil.desc': ('Destila un aceite que aumente el daño de las armas',
                                    'Prepare um óleo que aumente o dano das armas'),
    'subtitle.X.thrown_weapon_throw': ('Arma lanzada', 'Arma arremessada'),
    'subtitle.X.thrown_weapon_hit_mob': ('Un arma impacta a un mob', 'Arma atinge criatura'),
    'subtitle.X.thrown_weapon_hit_ground': ('Un arma impacta en el suelo', 'Arma atinge o chão'),
    'subtitle.X.oil_applied': ('Aceite aplicado al arma', 'Óleo aplicado na arma'),
    'subtitle.X.hammer_slams_into_ground': ('Un martillo golpea el suelo', 'Martelo golpeia o chão'),
}
# weapons pt_br left in English: es, pt ("%s" is the material)
ES_PT_WEAPON = {
    'parrying_dagger': ('Daga defensiva de %s', 'Adaga de Aparar de %s'),
    'scythe': ('Guadaña de %s', 'Foice de %s'),
}
# Strings the 1.20.1 pt_br had left in English
PT = {
    'block.X.blaze_head': 'Cabeça de Blaze',
    'block.X.enderman_head': 'Cabeça de Enderman',
    'block.X.spider_head': 'Cabeça de Aranha',
    'block.X.cave_spider_head': 'Cabeça de Aranha de Caverna',
    'block.X.piglin_head': 'Cabeça de Piglin',
    'block.X.zombified_piglin_head': 'Cabeça de Piglin Zumbificado',
    'block.X.husk_head': 'Cabeça de Zumbi-Múmia',
    'block.X.stray_skull': 'Crânio de Esqueleto Errante',
    'block.X.drowned_head': 'Cabeça de Afogado',
    'block.X.illager_head': 'Cabeça de Illager',
    'block.X.witch_head': 'Cabeça de Bruxa',
    'enchantment.X.propel': 'Propulsão',
    'enchantment.X.propel.desc': 'Aumenta o alcance de arremesso desta arma',
    'enchantment.X.razors_edge': 'Fio de Navalha',
    'enchantment.X.razors_edge.desc': 'Aumenta o dano desta arma de arremesso',
    'enchantment.X.incendiary': 'Incendiário',
    'enchantment.X.incendiary.desc': 'Imbui esta arma de arremesso com fogo, que incendeia os alvos atingidos',
    'enchantment.X.lucky_throw': 'Arremesso da Sorte',
    'enchantment.X.lucky_throw.desc': 'Aumenta os espólios obtidos com esta arma de arremesso',
    'enchantment.X.hydrodynamic': 'Hidrodinâmica',
    'enchantment.X.hydrodynamic.desc': 'Permite que armas de arremesso funcionem debaixo d\'água',
    'enchantment.X.supercharge': 'Supercarga',
    'enchantment.X.supercharge.desc': 'Reduz o tempo de carga de armas de arremesso em ~20%% por nível',
    'enchantment.X.expanse': 'Amplitude',
    'enchantment.X.expanse.desc': 'Aumenta a munição máxima de armas de arremesso em 25%% ou em uma unidade por nível, '
                                  'o que for maior',
    'enchantment.X.sharpshooter': 'Atirador de Elite',
    'enchantment.X.sharpshooter.desc': 'Reduz o tempo de mira das Balestras Largas em 0,1 s por nível',
    'tooltip.X.material.bronze': 'Bronze',
    'tooltip.X.material.electrum': 'Electrum',
    'tooltip.X.material.invar': 'Invar',
    'tooltip.X.material.constantan': 'Constantan',
    'tooltip.X.trait.block_melee': 'Bloqueio Corpo a Corpo',
    'tooltip.X.trait.block_melee.desc': 'Pode bloquear ataques corpo a corpo, mas não projéteis',
    'tooltip.X.trait.extra_damage_chest.desc.armor_points': '+%f%% de dano quando o inimigo não tem peitoral e tem '
                                                            'menos de %f pontos de armadura',
    'tooltip.X.trait.extra_damage_helmet': 'Bônus de Dano na Cabeça',
    'tooltip.X.trait.extra_damage_helmet.desc': '+%f%% de dano quando o inimigo não tem capacete',
    'tooltip.X.trait.extra_damage_helmet.desc.armor_points': '+%f%% de dano quando o inimigo não tem capacete e tem '
                                                             'menos de %f pontos de armadura',
    'tooltip.X.trait.extra_damage_unarmoured.desc.armor_points': '+%f%% de dano quando o inimigo não tem armadura e '
                                                                 'tem menos de %f pontos de armadura',
    'tooltip.X.trait.extra_damage_backstab': 'Bônus de Ataque pelas Costas',
    'tooltip.X.trait.extra_damage_backstab.desc': '+%.2f%% de dano quando esta arma atinge as costas do inimigo',
    'tooltip.X.trait.heavy.desc': 'Reduz a velocidade de ataque, de preparo, de recarga e de carga de arremesso',
    'tooltip.X.trait.decapitate': 'Decapitar',
    'tooltip.X.trait.decapitate.desc': 'Dá %f%% de chance de derrubar uma cabeça ou crânio ao matar uma criatura',
    'tooltip.X.dynamite.desc': 'Explode %f segundos após o arremesso, com uma explosão menor que a de TNT',
    'tooltip.X.enderman_head.desc': 'Obtida ao matar um Enderman com uma arma com a característica "Decapitar"',
    'tooltip.X.modifiers.projectile.armor_piercing_factor': 'Perfuração de Armadura: %s',
    'tooltip.X.modifiers.projectile.impact.glowing': 'Aplica Brilho à criatura atingida',
    'tooltip.X.throwable.ammo': 'Munição: %s',
    'tooltip.X.throwable.charge_time': 'Tempo de Carga: %s',
    'tooltip.X.throwable.charge_time.value': '%f segundos',
    'tooltip.X.throwable.not_original': 'Não pode ser reparada nem encantada e não deixa uma pilha vazia quando a '
                                        'munição acaba',
    'tooltip.X.quiver_capacity.value': '%d pilhas',
    'advancement.X.craft_dagger.title': 'O Melhor Amigo do Ladino',
    'advancement.X.craft_dagger.desc': 'Obtenha uma Adaga',
    'advancement.X.craft_parrying_dagger.title': 'Defesa à Moda Antiga',
    'advancement.X.craft_parrying_dagger.desc': 'Obtenha uma Adaga de Aparar',
    'advancement.X.craft_longsword.title': 'Uma Lâmina Maior',
    'advancement.X.craft_longsword.desc': 'Obtenha uma Espada Longa',
    'advancement.X.craft_katana.title': 'Lâmina do Samurai',
    'advancement.X.craft_katana.desc': 'Obtenha uma Katana',
    'advancement.X.craft_saber.title': 'Espadachim',
    'advancement.X.craft_saber.desc': 'Obtenha um Sabre',
    'advancement.X.craft_rapier.title': 'En Garde!',
    'advancement.X.craft_rapier.desc': 'Obtenha uma Rapieira',
    'advancement.X.craft_greatsword.title': 'Um Corte Acima dos Demais',
    'advancement.X.craft_greatsword.desc': 'Obtenha uma Montante',
    'advancement.X.craft_cestus.title': 'Na Base do Soco',
    'advancement.X.craft_cestus.desc': 'Obtenha um Cesto',
    'advancement.X.craft_club.title': 'Bate-Pau',
    'advancement.X.craft_club.desc': 'Obtenha uma Clava',
    'advancement.X.craft_battle_hammer.title': 'Hora do Martelo!',
    'advancement.X.craft_battle_hammer.desc': 'Obtenha um Martelo de Batalha',
    'advancement.X.craft_warhammer.title': 'Trauma Contundente',
    'advancement.X.craft_warhammer.desc': 'Obtenha um Martelo de Guerra',
    'advancement.X.craft_spear.title': 'Ponta de Lança',
    'advancement.X.craft_spear.desc': 'Obtenha uma Lança',
    'advancement.X.craft_halberd.title': 'Alabardeiro',
    'advancement.X.craft_halberd.desc': 'Obtenha uma Alabarda',
    'advancement.X.craft_pike.title': 'Pique Nele!',
    'advancement.X.craft_pike.desc': 'Obtenha um Pique',
    'advancement.X.craft_lance.title': 'O Quarto Cavaleiro',
    'advancement.X.craft_lance.desc': 'Obtenha uma Lança de Justa',
    'advancement.X.craft_longbow.title': 'Mirem, Rapazes!',
    'advancement.X.craft_longbow.desc': 'Obtenha um Arco Longo',
    'advancement.X.craft_heavy_crossbow.title': 'A Artilharia do Século XV',
    'advancement.X.craft_throwing_knife.title': 'O Que um Assassino Precisa',
    'advancement.X.craft_throwing_knife.desc': 'Obtenha uma Faca de Arremesso',
    'advancement.X.craft_tomahawk.title': 'Enterrando o Machado',
    'advancement.X.craft_tomahawk.desc': 'Obtenha uma Machadinha',
    'advancement.X.craft_javelin.title': 'Não do Tipo Olímpico',
    'advancement.X.craft_javelin.desc': 'Obtenha um Dardo de Arremesso',
    'advancement.X.craft_boomerang.title': 'Será Que Volta?',
    'advancement.X.craft_boomerang.desc': 'Obtenha um Bumerangue',
    'advancement.X.craft_battleaxe.title': '...E o Meu Machado!',
    'advancement.X.craft_battleaxe.desc': 'Obtenha um Machado de Batalha',
    'advancement.X.craft_flanged_mace.title': 'Esmaga e Amassa!',
    'advancement.X.craft_flanged_mace.desc': 'Obtenha uma Maça Flangeada',
    'advancement.X.craft_glaive.title': 'Endireitando as Foices',
    'advancement.X.craft_glaive.desc': 'Obtenha uma Glaive',
    'advancement.X.craft_quarterstaff.title': 'Um Graveto Nem Tão Pontudo!',
    'advancement.X.craft_quarterstaff.desc': 'Obtenha um Bastão',
    'advancement.X.craft_quiver.title': 'Mochila Cheia de Munição!',
    'advancement.X.craft_quiver.desc': 'Obtenha qualquer Aljava de Flechas ou de Dardos',
    'advancement.X.upgrade_quiver.title': 'Uma Aljava Ainda Maior?',
    'advancement.X.upgrade_quiver.desc': 'Melhore qualquer Aljava',
    'advancement.X.upgrade_quiver_max.title': 'Capacidade Máxima',
    'advancement.X.upgrade_quiver_max.desc': 'Melhore qualquer Aljava para uma Aljava Enorme',
    'advancement.X.craft_scythe.title': 'Colhendo o Que Plantou',
    'advancement.X.craft_scythe.desc': 'Obtenha uma Foice',
    'advancement.X.collect_heads.title': 'Cortem-lhes as Cabeças!',
    'advancement.X.collect_heads.desc': 'Use uma Foice para coletar todas as cabeças e crânios (exceto as cabeças de '
                                        'jogador e do Dragão do End)',
    'advancement.X.upgrade_netherite.title': 'Melhore-me com Detritos',
    'advancement.X.upgrade_netherite.desc': 'Melhore qualquer arma de diamante para a versão de netherita',
    'advancement.X.bounce_kill.title': 'Ricocheteando!',
    'advancement.X.bounce_kill.desc': 'Mate uma criatura depois de ricochetear um Bumerangue em uma superfície próxima',
    'advancement.X.return_kill.title': 'Cuidado! Ele Volta!',
    'advancement.X.return_kill.desc': 'Mate uma criatura com um Bumerangue enquanto ele volta para você',
    'subtitle.X.throwing_weapon_loyalty_return': 'Arma de arremesso retorna',
}
# The 1.20.1 ru_ru lacked these as well
RU = {
    'item.X.quiver_compartment': 'Отделение колчана / кузнечный «шаблон»',
}


def k(key):
    return key.replace('.X.', '.%s.' % MOD)


def lead(name):
    return name[0].upper() + name[1:]


def placeholders(s):
    return sorted(re.findall(r'%(?:\d+\$)?[-#+ 0,(]*\d*(?:\.\d+)?[a-zA-Z%]', s))


def main(upstream):
    src = os.path.join(upstream, 'src/main/resources/assets', MOD, 'lang')

    def load(lang):
        with open(os.path.join(src, lang + '.json'), encoding='utf-8') as f:
            return json.loads(re.sub(r'^\s*//.*$', '', f.read(), flags=re.M))

    en = load('en_us')
    old = {lang: load(lang) for lang in ('ru_ru', 'es_es', 'pt_br', 'zh_cn')}
    en_new = {k(P.replace(MOD, 'X') + 'proj_tipped.effect.' + e): v for e, v in NEW_TIPPED_EN.items()}
    en_all = dict(en, **en_new)
    names = weapon_names(en_all)
    names.update(custom_names())
    tipped = tipped_names(en_all)

    out = {lang: {} for lang in ('en_us', 'ru_ru', 'uk_ua', 'de_de', 'es_es', 'pt_br', 'zh_cn')}
    out['en_us'] = en_new
    missing = []
    for key in en_all:
        if key in names:
            uk, de = names[key]
        elif key in tipped:
            ru_t, uk, de, es_t, pt_t = tipped[key]
            uk, de = lead(uk), lead(de)
            for lang, value in (('ru_ru', lead(ru_t)), ('es_es', lead(es_t)), ('pt_br', lead(pt_t))):
                if key not in old[lang] or old[lang][key] == en_all[key]:
                    out[lang][key] = value
        elif key.replace('.%s.' % MOD, '.X.') in T:
            uk, de = T[key.replace('.%s.' % MOD, '.X.')]
        else:
            missing.append(key)
            continue
        out['uk_ua'][key] = uk
        out['de_de'][key] = de

    for xkey, values in FIX.items():
        key = k(xkey)
        for lang, value in zip(('ru_ru', 'es_es', 'pt_br', 'zh_cn'), values):
            if value is not None:
                out[lang][key] = value
    for xkey, (es, pt) in ES_PT.items():
        for lang, value in (('es_es', es), ('pt_br', pt)):
            if k(xkey) not in old[lang] and k(xkey) not in out[lang]:
                out[lang][k(xkey)] = value
    for xkey, value in PT.items():
        if old['pt_br'].get(k(xkey)) == en_all[k(xkey)]:
            out['pt_br'][k(xkey)] = value
    for xkey, value in RU.items():
        out['ru_ru'].setdefault(k(xkey), value)

    # es_es / pt_br names of the weapons they lacked (or left in English): the iron weapon's name with the material
    # word replaced
    for lang, idx in (('es_es', 3), ('pt_br', 4)):
        iron_word = MAT['iron'][idx]
        fixed = dict(old[lang], **out[lang])
        for key in en_all:
            if key not in names or (key in old[lang] and (old[lang][key] != en_all[key] or key == P + 'cestus')) \
                    or key in out[lang]:
                continue
            name = key[len(P):]
            mat = next((m for m in MAT if name.startswith(m + '_')), None)
            weapon = name[len(mat) + 1:] if mat else None
            template = fixed.get(P + 'iron_' + weapon) if weapon else None
            if template == en_all.get(P + 'iron_' + str(weapon)):
                template = ES_PT_WEAPON[weapon][idx - 3] % iron_word if weapon in ES_PT_WEAPON else None
            if weapon == 'bolt':
                template = old[lang].get(P + 'diamond_bolt', '').replace(MAT['diamond'][idx], iron_word)
            if template and iron_word in template:
                out[lang][key] = template.replace(iron_word, MAT[mat][idx])
            else:
                missing.append(lang + ':' + key)

    # every language complete, placeholders as in English
    for lang in ('ru_ru', 'uk_ua', 'de_de', 'es_es', 'pt_br'):
        merged = dict(old.get(lang, {}), **out[lang])
        for key, value in en_all.items():
            if key not in merged:
                missing.append(lang + ':' + key)
            elif placeholders(merged[key]) != placeholders(value):
                missing.append('%s:%s placeholders %r vs %r' % (lang, key, merged[key], value))
    if missing:
        sys.exit('untranslated or broken:\n  ' + '\n  '.join(missing))

    dst = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'overlay', 'assets', MOD, 'lang')
    os.makedirs(dst, exist_ok=True)
    for lang, data in out.items():
        with open(os.path.join(dst, lang + '.json'), 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
            f.write('\n')
        print(lang, len(data))


if __name__ == '__main__':
    main(sys.argv[1])
