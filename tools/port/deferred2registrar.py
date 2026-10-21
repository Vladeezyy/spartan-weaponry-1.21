"""Replaces NeoForge DeferredRegister/DeferredHolder/DeferredItem with the common Registrar (Spartan multiloader port)."""
import re, sys, pathlib

pkg = sys.argv[2]  # e.g. com.oblivioussp.spartanshields.platform
for path in pathlib.Path(sys.argv[1]).rglob('*.java'):
    src = path.read_text(encoding='utf-8')
    orig = src
    src = re.sub(r'DeferredRegister\.Items (\w+) = DeferredRegister\.createItems\([^)]*\)', r'Registrar<Item> \1 = Registrar.create(Registries.ITEM)', src)
    src = re.sub(r'DeferredRegister\.Blocks (\w+) = DeferredRegister\.createBlocks\([^)]*\)', r'Registrar<Block> \1 = Registrar.create(Registries.BLOCK)', src)
    src = re.sub(r'DeferredRegister\.DataComponents (\w+) = DeferredRegister\.createDataComponents\(([\w.]+), [^)]*\)', r'Registrar<DataComponentType<?>> \1 = Registrar.create(\2)', src)
    src = re.sub(r'DeferredRegister<(.+?)> (\w+) = DeferredRegister\.create\(([\w.]+), [\w.]+\)', r'Registrar<\1> \2 = Registrar.create(\3)', src)
    src = re.sub(r'DeferredItem<', 'Registrar.Entry<Item, ', src)
    src = re.sub(r'DeferredBlock<', 'Registrar.Entry<Block, ', src)
    src = re.sub(r'DeferredHolder<', 'Registrar.Entry<', src)
    if src != orig:
        src = re.sub(r'import net\.neoforged\.neoforge\.registries\.(DeferredRegister|DeferredHolder|DeferredItem|DeferredBlock);\n', '', src)
        imports = [f'import {pkg}.Registrar;', 'import net.minecraft.core.registries.Registries;', 'import net.minecraft.world.item.Item;']
        for imp in imports:
            if imp not in src:
                src = re.sub(r'(package [\w.]+;\n)', r'\1\n' + imp, src, count=1)
        path.write_text(src, encoding='utf-8')
        print('converted', path)
