#!/usr/bin/env bash
set +e
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.OnboardingTest,com.yalnizfahrettin.azim.VisualAcceptanceTest --no-daemon --max-workers=2
test_status=$?
adb pull /sdcard/Download/ascend-screenshots screenshots
adb pull /sdcard/Movies/Ascend screenshots/exported-videos
adb pull /sdcard/Pictures/Ascend screenshots/exported-images
exit "$test_status"
