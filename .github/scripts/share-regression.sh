#!/usr/bin/env bash
set -uo pipefail
bash gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.yalnizfahrettin.azim.VisualUnityTest,com.yalnizfahrettin.azim.ProJourneyUiTest,com.yalnizfahrettin.azim.AccessPreviewTest,com.yalnizfahrettin.azim.WarriorGalleryTest,com.yalnizfahrettin.azim.SimplificationTest,com.yalnizfahrettin.azim.LocaleSmokeTest,com.yalnizfahrettin.azim.ContentReleaseTest,com.yalnizfahrettin.azim.KatalogDepoTest,com.yalnizfahrettin.azim.ShareRegressionTest --no-daemon --max-workers=2
result=$?
mkdir -p screenshots
adb pull /sdcard/Download/ascend-screenshots/. screenshots/ || true
find app/build -name "*.xml" -path "*connected*" -exec cat {} \;
exit "$result"
