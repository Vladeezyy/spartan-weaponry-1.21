#!/bin/sh
# Release check: the jars as players have them. NeoForge: the release jar on the real NeoForge servers in prodtest/*/
# (prodtest/serve.sh). Fabric: the release jar on a real Fabric server (:fabric:prodServer), then the client scene and
# the stress scene on a production client (:fabric:prodScene / prodStress run a jar that is the
# release jar plus port/). Outputs: prodtest/<version>/server.out, build-prod*-fabric.log.
. "$(dirname "$0")/common.sh"
MOD=spartanweaponry
./gradlew :neoforge:jar :fabric:jar -q || exit 1
VERSION=$(grep '^mod_version=' gradle.properties | cut -d= -f2)+$(grep '^minecraft_version=' gradle.properties | cut -d= -f2)
JAR=neoforge/build/libs/$MOD-neoforge-$VERSION.jar
for dir in prodtest/*/; do
	[ -f "$dir/run.sh" ] || continue
	rm -f "$dir"/mods/SpartanWeaponry-neoforge-*.jar "$dir"/mods/spartanweaponry-neoforge-*.jar
	mkdir -p "$dir/mods"
	cp "$JAR" "$dir/mods/"
	echo "=== NeoForge server $(grep -o 'neoforge/[0-9.]*' "$dir/run.sh" | head -1): $(ls "$dir/mods" | tr '\n' ' ')"
	(cd "$dir" && sh ../serve.sh)
done
echo "=== Fabric server"
mkdir -p fabric/run-prod-server && echo "eula=true" > fabric/run-prod-server/eula.txt
rm -rf fabric/run-prod-server/world
LOG=build-prodserver-fabric.log
./gradlew :fabric:prodServer --console=plain > "$LOG" 2>&1 &
i=0
until grep -qE 'Done \(|Crash report|crash report|BUILD FAILED|Exception in server tick' "$LOG" 2>/dev/null || [ $i -gt 300 ]; do sleep 2; i=$((i+1)); done
sleep 3
pkill -f "fabric.addMods=.*$MOD-fabric" 2>/dev/null; sleep 3
grep -E 'Done \(|Crash report|crash report|BUILD FAILED' "$LOG" | head -3
grep -E '/(ERROR|FATAL)\]|Caused by|Exception' "$LOG" | grep -vE 'server.properties|No data fixer registered' | cut -c1-250 | sort | uniq -c | sort -rn | head -10
mkdir -p fabric/run-prod
[ -f fabric/run-prod/options.txt ] || cp fabric/run/options.txt fabric/run-prod/ 2>/dev/null
# NO_STRESS=1: the client scene only
for t in Scene $([ -n "$NO_STRESS" ] || echo Stress); do
	rm -rf fabric/run-prod/saves/sw_scene fabric/run-prod/saves/sw_stress fabric/run-prod/screenshots fabric/run-prod/config/spartanweaponry-common.toml
	LOG=build-prod$(echo $t | tr 'A-Z' 'a-z')-fabric.log
	run_watched "$LOG" 1500 ":fabric:prod$t"
	echo "=== Fabric production client, $t: $(grep -c '\] CHECK' "$LOG") checks, $(grep -oE 'DONE, failed checks: [0-9]+' "$LOG")"
	grep -E 'CHECK FAIL' "$LOG" | sed 's/.*CHECK/CHECK/' | head -20
	grep -E 'Mixin apply failed|InvalidInjection|Caused by' "$LOG" | cut -c1-250 | sort -u | head -10
done
