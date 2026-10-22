#!/bin/sh
# Compatibility runs on LOADER=neoforge|fabric: the client scene and the stress scene with -Pwith_compat (Sodium, Iris
# with a shader pack from run/shaderpacks, Lithium, FerriteCore, Jade, Shoulder Surfing 5.x; JEI and Curios are in every
# NeoForge dev run). 1.21.11 has no ModernFix, EMI, JEED or Shoulder Surfing 4.x.
# Outputs: build-compat-<loader>-*.out, screenshots in build/compat-<loader>-*/.
. "$(dirname "$0")/common.sh"
mkdir -p "$RUN/shaderpacks" "$RUN/config"
cp run/shaderpacks/*.zip "$RUN/shaderpacks/" 2>/dev/null
[ -f run/config/iris.properties ] && cp run/config/iris.properties "$RUN/config/"
# NO_SHADERS=1: Iris without its shader pack (Iris 1.10.8 for NeoForge 1.21.11 crashes in its shadow pass on any
# enchanted item in hand, vanilla's too: "Missing sampler Sampler1")
[ -n "$NO_SHADERS" ] && sed -i.bak 's/^enableShaders=.*/enableShaders=false/' "$RUN/config/iris.properties" && rm -f "$RUN/config/iris.properties.bak"
# NeoForge 21.11 wraps the GPU device in its validation layer in dev runs; Sodium and Iris cast it to GlDevice and crash
C="$RUN/config/neoforge-client.toml"
if [ "$LOADER" = neoforge ]; then
	if [ -f "$C" ] && grep -q enableB3DValidationLayer "$C"; then sed -i.bak 's/^enableB3DValidationLayer.*/enableB3DValidationLayer = false/' "$C"; else echo 'enableB3DValidationLayer = false' >> "$C"; fi
fi
OUT=build-compat-$LOADER
tools/scenetest.sh -Pwith_compat > $OUT-scene.out 2>&1
rm -rf build/compat-$LOADER-scene && mkdir -p build/compat-$LOADER-scene && cp "$RUN"/screenshots/scene_*.png build/compat-$LOADER-scene/ 2>/dev/null
# NO_STRESS=1: the scenes only
if [ -z "$NO_STRESS" ]; then
tools/stresstest.sh -Pwith_compat > $OUT-stress.out 2>&1
rm -rf build/compat-$LOADER-stress && mkdir -p build/compat-$LOADER-stress && cp "$RUN"/screenshots/stress_*.png build/compat-$LOADER-stress/ 2>/dev/null
fi
for f in $(ls $OUT-scene*.out; [ -n "$NO_STRESS" ] || echo $OUT-stress.out); do
	echo "=== $f: $(grep -c 'CHECK' $f) checks, $(grep -oE 'DONE, failed checks: [0-9]+' $f)"
	grep 'CHECK FAIL' $f | head -10
	sed -n '/--- errors/,$p' $f | head -12
done
# The other dev runs keep NeoForge's validation layer
[ -f "$C" ] && sed -i.bak 's/^enableB3DValidationLayer.*/enableB3DValidationLayer = true/' "$C" && rm -f "$C.bak"
