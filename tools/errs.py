#!/usr/bin/env python3
"""Compact javac errors from build-compile.log: tools/errs.py [path-substring] -> file:line message [symbol]"""
import re, sys
log = open('build-compile.log', encoding='utf-8', errors='replace').read().split('\n')
flt = sys.argv[1] if len(sys.argv) > 1 else ''
out = []
for i, l in enumerate(log):
    m = re.search(r'spartanweaponry/(.+\.java):(\d+): error: (.*)', l)
    if not m or flt not in m.group(1):
        continue
    sym = ''
    for j in range(i + 1, min(i + 5, len(log))):
        s = re.search(r'symbol: +(.*)', log[j])
        if s:
            sym = ' [' + s.group(1).strip() + ']'
            break
    out.append('%s:%s %s%s' % (m.group(1), m.group(2), m.group(3)[:150], sym))
seen = set()
for o in out:
    if o not in seen:
        seen.add(o)
        print(o)
