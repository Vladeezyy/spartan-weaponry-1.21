#!/bin/sh
# Runs the functional / stress scene (port/StressScene, max 20 min) on a fresh world, then prints the checks, the
# measurements and the errors. A crash report stops the client. Extra gradle arguments are passed on.
cd "$(dirname "$0")/.."
rm -rf run/saves/sw_stress run/config/spartanweaponry-common.toml run/screenshots/stress_*.png build-stress.log
perl -e 'alarm 1200; exec @ARGV' ./gradlew runStress --console=plain "$@" > build-stress.log 2>&1 &
gradle_pid=$!
while kill -0 $gradle_pid 2>/dev/null; do
	if grep -q -- '---- Minecraft Crash Report ----' build-stress.log 2>/dev/null; then
		sleep 3
		pkill -f 'fml.modFolders=spartanweaponry.*' 2>/dev/null
	fi
	sleep 2
done
wait $gradle_pid
echo "exit $?"
grep -E '\[stress\] (CHECK|STAT|DONE)' build-stress.log | sed -E 's/^.*\[stress\] //'
ls run/screenshots/ 2>/dev/null | grep stress_
echo "--- errors"
grep -E '/(ERROR|FATAL)\]|Caused by|Exception' build-stress.log | grep -v 'NoSuchFileException: server.properties' | sed -E 's/^\[[^]]*\] //' | cut -c1-300 | sort | uniq -c | sort -rn | head -30
