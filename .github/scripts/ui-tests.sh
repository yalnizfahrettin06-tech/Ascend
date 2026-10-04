#!/usr/bin/env bash
set -uo pipefail
bash gradlew :app:connectedDebugAndroidTest --no-daemon --max-workers=2
result=$?
mkdir -p screenshots
adb pull /data/local/tmp/ascend-screens/. screenshots/ || true
exit "$result"
