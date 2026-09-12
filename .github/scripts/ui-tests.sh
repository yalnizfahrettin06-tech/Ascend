#!/usr/bin/env bash
set +e
mkdir -p screenshots
adb shell wm size 720x1600
adb shell wm density 280
(while true; do
  adb pull /sdcard/Download/ascend-screenshots screenshots >/dev/null 2>&1
  free -m >> screenshots/host-memory.txt
  sleep 3
done) &
evidence_pid=$!
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.VisualAcceptanceTest --no-daemon --max-workers=2
visual_status=$?
adb pull /sdcard/Download/ascend-screenshots screenshots
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.OnboardingTest --no-daemon --max-workers=2
onboarding_status=$?
kill "$evidence_pid" 2>/dev/null
adb pull /sdcard/Download/ascend-screenshots screenshots
sudo dmesg | tail -60 > screenshots/host-diagnostics.txt
if [ "$visual_status" -ne 0 ]; then exit "$visual_status"; fi
exit "$onboarding_status"
