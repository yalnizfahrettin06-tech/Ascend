#!/usr/bin/env bash
set +e
mkdir -p screenshots
# Preserve completed screen evidence even if the emulator exits during a later test.
(while true; do adb pull /sdcard/Download/ascend-screenshots screenshots >/dev/null 2>&1; sleep 3; done) &
evidence_pid=$!
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.OnboardingTest,com.yalnizfahrettin.azim.VisualAcceptanceTest --no-daemon --max-workers=2
test_status=$?
kill "$evidence_pid" 2>/dev/null
adb pull /sdcard/Download/ascend-screenshots screenshots
adb pull /sdcard/Movies/Ascend screenshots/exported-videos
adb pull /sdcard/Pictures/Ascend screenshots/exported-images
exit "$test_status"
