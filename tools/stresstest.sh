#!/bin/sh
# Runs the functional / stress scene (port/StressScene, max 20 min) on LOADER=neoforge|fabric with a fresh world and
# the default config, then prints the checks, the measurements and the errors. Extra gradle arguments are passed on.
. "$(dirname "$0")/common.sh"
LOG=build-stress-$LOADER.log
rm -rf "$RUN/saves/sw_stress" "$RUN/config/spartanweaponry-common.toml" "$RUN"/screenshots/stress_*.png "$LOG"
run_watched "$LOG" 1200 ":$LOADER:runStress" "$@"
grep -E '\[stress\] (CHECK|STAT|DONE)' "$LOG" | sed -E 's/^.*\[stress\] //'
ls "$RUN/screenshots/" 2>/dev/null | grep stress_
echo "--- errors"
errors "$LOG"
