#!/usr/bin/env bash
set +e
mkdir -p screenshots
adb logcat -c
adb logcat -v threadtime > screenshots/session-logcat.txt &
logcat_pid=$!
adb shell wm size 720x1600
adb shell wm density 280
adb shell cmd overlay enable --user 0 com.android.internal.systemui.navbar.gestural || true
adb shell cmd overlay list > screenshots/navigation-overlays.txt
# Avoid concurrent bulk ADB transfers while instrumentation owns its shell connection.
# Screenshots are still copied after each test suite below; failures remain failures.
(while true; do
  free -m >> screenshots/host-memory.txt
  sleep 3
done) &
evidence_pid=$!
# Compile before dismissing the emulator launcher; cold boot launcher ANRs can steal focus.
bash gradlew :app:assembleDebug :app:assembleDebugAndroidTest --no-daemon --max-workers=2 || exit $?
adb shell am force-stop com.google.android.apps.nexuslauncher
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.VisualAcceptanceTest --no-daemon --max-workers=2
visual_status=$?
mkdir -p screenshots/visual-diagnostics
cp -R app/build/outputs/androidTest-results screenshots/visual-diagnostics/results 2>/dev/null
cp -R app/build/reports/androidTests screenshots/visual-diagnostics/reports 2>/dev/null
adb logcat -d > screenshots/visual-diagnostics/logcat.txt
adb pull /sdcard/Download/ascend-screenshots screenshots
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.Days59UiTest,com.yalnizfahrettin.azim.OnboardingTest,com.yalnizfahrettin.azim.CompactUiTest,com.yalnizfahrettin.azim.AuditPhasesUiTest,com.yalnizfahrettin.azim.ArtworkRenderingTest,com.yalnizfahrettin.azim.Phase34UiTest,com.yalnizfahrettin.azim.Phase34StorageTest,com.yalnizfahrettin.azim.WallpaperJourneyTest --no-daemon --max-workers=2
onboarding_status=$?
adb logcat -d | sed -n 's/.*ASCEND_PERFORMANCE=//p' | tail -1 > screenshots/days59-performance.json
mkdir -p screenshots/ui-suite-results
cp -R app/build/outputs/androidTest-results screenshots/ui-suite-results/results
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.WallpaperSystemTest --no-daemon --max-workers=2
wallpaper_status=$?
adb logcat -d > screenshots/final-logcat.txt
kill "$logcat_pid" 2>/dev/null
kill "$evidence_pid" 2>/dev/null
adb pull /sdcard/Download/ascend-screenshots screenshots
sudo dmesg | tail -60 > screenshots/host-diagnostics.txt
if [ "$visual_status" -ne 0 ]; then exit "$visual_status"; fi
if [ "$onboarding_status" -ne 0 ]; then exit "$onboarding_status"; fi
exit "$wallpaper_status"
