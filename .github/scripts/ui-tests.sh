#!/usr/bin/env bash
set -uo pipefail
bash gradlew :app:connectedDebugAndroidTest --no-daemon --max-workers=2
