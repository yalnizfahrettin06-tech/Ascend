#!/usr/bin/env bash
set -uo pipefail
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.ShareRegressionTest --no-daemon --max-workers=2
result=$?
mkdir -p screenshots
adb pull /sdcard/Download/ascend-screenshots/. screenshots/ || true
exit "$result"
