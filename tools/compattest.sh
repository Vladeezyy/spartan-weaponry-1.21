#!/bin/sh
# Compatibility runs on LOADER=neoforge|fabric: the client scene with -Pwith_compat (Sodium, Iris with the shader pack
# of run/shaderpacks, Lithium, FerriteCore, ModernFix, Jade, EMI, JEED, Shoulder Surfing 5.x; on NeoForge JEI and
# Curios are in every dev run), again with Shoulder Surfing 4.x, then the stress scene with the same mods. Outputs:
# build-compat-<loader>-*.out, screenshots in build/compat-<loader>-*/.
. "$(dirname "$0")/common.sh"
mkdir -p "$RUN/shaderpacks" "$RUN/config"
cp run/shaderpacks/*.zip "$RUN/shaderpacks/" 2>/dev/null
[ -f run/config/iris.properties ] && cp run/config/iris.properties "$RUN/config/"
OUT=build-compat-$LOADER
tools/scenetest.sh -Pwith_compat > $OUT-scene.out 2>&1
rm -rf build/compat-$LOADER-scene && mkdir -p build/compat-$LOADER-scene && cp "$RUN"/screenshots/scene_*.png build/compat-$LOADER-scene/ 2>/dev/null
tools/scenetest.sh -Pwith_compat -Pwith_ss4 > $OUT-scene-ss4.out 2>&1
tools/stresstest.sh -Pwith_compat > $OUT-stress.out 2>&1
rm -rf build/compat-$LOADER-stress && mkdir -p build/compat-$LOADER-stress && cp "$RUN"/screenshots/stress_*.png build/compat-$LOADER-stress/ 2>/dev/null
for f in $OUT-scene.out $OUT-scene-ss4.out $OUT-stress.out; do
	echo "=== $f: $(grep -c 'CHECK' $f) checks, $(grep -oE 'DONE, failed checks: [0-9]+' $f)"
	grep 'CHECK FAIL' $f | head -10
	sed -n '/--- errors/,$p' $f | head -12
done
