#!/usr/bin/env python3
"""Item NBT (1.20.1 ItemStack tag) -> util.ItemNbt over minecraft:custom_data, simple forms only (idempotent).
Statements that keep a CompoundTag from getOrCreateTag() in a variable are left for hand porting (listed)."""
import os, re, sys
R = r'([A-Za-z_]\w*(?:\.[A-Za-z_]\w*(?:\(\))?)*)'
RULES = [
    # whole statements that only write
    (re.compile(r'^(\s*)' + R + r'\.getOrCreateTag\(\)\.((?:put\w*|remove))\((.*)\);[ \t]*$', re.M), r'\1ItemNbt.update(\2, (nbt) -> nbt.\3(\4));'),
    (re.compile(r'^(\s*)' + R + r'\.getTag\(\)\.((?:put\w*|remove))\((.*)\);[ \t]*$', re.M), r'\1ItemNbt.update(\2, (nbt) -> nbt.\3(\4));'),
    # null checks / presence
    (re.compile(R + r'\.getTag\(\) != null'), r'ItemNbt.has(\1)'),
    (re.compile(R + r'\.getTag\(\) == null'), r'!ItemNbt.has(\1)'),
    (re.compile(R + r'\.hasTag\(\)'), r'ItemNbt.has(\1)'),
    # reads
    (re.compile(R + r'\.getOrCreateTag\(\)\.contains\('), r'ItemNbt.get(\1).contains('),
    (re.compile(R + r'\.getTag\(\)\.contains\('), r'ItemNbt.get(\1).contains('),
    (re.compile(R + r'\.getOrCreateTag\(\)\.((?:get|has)\w*)\('), r'ItemNbt.get(\1).\2('),
    (re.compile(R + r'\.getTag\(\)\.((?:get|has)\w*)\('), r'ItemNbt.get(\1).\2('),
]
left = []
for d, _, fs in os.walk(sys.argv[1]):
    for f in fs:
        if not f.endswith('.java') or f == 'ItemNbt.java':
            continue
        p = os.path.join(d, f)
        s = open(p, encoding='utf-8').read()
        n = s
        for rx, rep in RULES:
            n = rx.sub(rep, n)
        if n != s and 'import com.oblivioussp.spartanweaponry.util.ItemNbt;' not in n:
            m = re.search(r'^package [^;]+;\n', n, re.M)
            n = n[:m.end()] + '\nimport com.oblivioussp.spartanweaponry.util.ItemNbt;\n' + n[m.end():]
        if n != s:
            open(p + '.tmp', 'w', encoding='utf-8').write(n); os.replace(p + '.tmp', p)
        for i, line in enumerate(n.split('\n'), 1):
            if re.search(r'\.(getOrCreateTag|getTag|getOrCreateTagElement|getTagElement|setTag|removeTagKey)\(', line) and 'ItemNbt' not in line:
                left.append('%s:%d: %s' % (p.split('spartanweaponry/')[-1], i, line.strip()[:140]))
print('\n'.join(left)); print(len(left), 'left for hand porting')
