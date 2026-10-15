#!/bin/sh
# Compatibility runs: the client scene with -Pwith_compat (Sodium, Iris + run/shaderpacks, Lithium, FerriteCore,
# ModernFix, Jade, EMI, JEED, Shoulder Surfing 5.x; JEI and Curios are in every dev run), again with Shoulder Surfing
# 4.x, then the stress scene with the same mods. Outputs: build-compat-*.out, screenshots in build/compat-*/.
cd "$(dirname "$0")/.."
tools/scenetest.sh -Pwith_compat > build-compat-scene.out 2>&1
rm -rf build/compat-scene && mkdir -p build/compat-scene && cp run/screenshots/scene_*.png build/compat-scene/ 2>/dev/null; cp build-scene.log build/compat-scene/
tools/scenetest.sh -Pwith_compat -Pwith_ss4 > build-compat-scene-ss4.out 2>&1
cp build-scene.log build/compat-scene/build-scene-ss4.log
tools/stresstest.sh -Pwith_compat > build-compat-stress.out 2>&1
rm -rf build/compat-stress && mkdir -p build/compat-stress && cp run/screenshots/stress_*.png build/compat-stress/ 2>/dev/null; cp build-stress.log build/compat-stress/
