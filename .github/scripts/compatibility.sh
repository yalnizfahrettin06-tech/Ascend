#!/usr/bin/env bash
set -uo pipefail
mkdir -p compatibility-evidence
adb shell wm size 640x1280
adb shell wm density 320
adb shell settings put system font_scale 1.3
adb shell cmd overlay enable --user 0 com.android.internal.systemui.navbar.threebutton || true
adb shell cmd overlay list > compatibility-evidence/overlays.txt
adb shell dumpsys meminfo > compatibility-evidence/memory-before.txt
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.CompactUiTest,com.yalnizfahrettin.azim.WallpaperSystemTest --no-daemon --max-workers=2
result=$?
adb logcat -d > compatibility-evidence/logcat.txt
adb pull /sdcard/Download/ascend-screenshots compatibility-evidence/ || true
exit "$result"
