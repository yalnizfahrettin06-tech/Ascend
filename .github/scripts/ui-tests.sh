#!/usr/bin/env bash
set -uo pipefail
bash gradlew :app:connectedDebugAndroidTest --no-daemon --max-workers=2
result=$?
mkdir -p screenshots
adb pull /data/local/tmp/ascend-screens/. screenshots/ || true
# Small previews in the log, for reviewers without artifact access.
python3 -m pip install -q pillow >/dev/null 2>&1 && python3 - <<'PY' || true
import base64, io, pathlib
from PIL import Image
for p in sorted(pathlib.Path("screenshots").glob("*.png")):
    im = Image.open(p).convert("RGB"); im.thumbnail((400, 900))
    buf = io.BytesIO(); im.save(buf, "JPEG", quality=70)
    print(f"ASCEND_SCREEN {p.stem} {base64.b64encode(buf.getvalue()).decode()}")
PY
exit "$result"
