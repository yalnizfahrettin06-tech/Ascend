#!/usr/bin/env python3
"""Verify scene artwork and all Atmosfer references using the standard library only."""
import hashlib
import json
import re
from pathlib import Path


def webp_size(data: bytes) -> tuple[int, int]:
    assert data[:4] == b"RIFF" and data[8:12] == b"WEBP", "Invalid WebP container"
    offset = 12
    while offset + 8 <= len(data):
        kind = data[offset:offset + 4]
        length = int.from_bytes(data[offset + 4:offset + 8], "little")
        chunk = data[offset + 8:offset + 8 + length]
        assert len(chunk) == length, "Truncated WebP chunk"
        if kind == b"VP8 ":
            assert len(chunk) >= 10 and chunk[3:6] == b"\x9d\x01\x2a", "Invalid VP8 frame"
            return int.from_bytes(chunk[6:8], "little") & 0x3FFF, int.from_bytes(chunk[8:10], "little") & 0x3FFF
        if kind == b"VP8X":
            assert len(chunk) >= 10, "Invalid extended WebP frame"
            return 1 + int.from_bytes(chunk[4:7], "little"), 1 + int.from_bytes(chunk[7:10], "little")
        if kind == b"VP8L":
            assert len(chunk) >= 5 and chunk[0] == 0x2F, "Invalid lossless WebP frame"
            bits = int.from_bytes(chunk[1:5], "little")
            return (bits & 0x3FFF) + 1, ((bits >> 14) & 0x3FFF) + 1
        offset += 8 + length + (length % 2)
    raise AssertionError("WebP image dimensions were not found")


repo = Path(__file__).resolve().parents[1]
manifest = json.loads((repo / "docs/scene-art.json").read_text(encoding="utf-8"))
records = manifest["scenes"]
code = (repo / "app/src/main/java/com/yalnizfahrettin/azim/ui/Atmosfer.kt").read_text(encoding="utf-8")
refs = re.findall(r'^\s+[A-Z_]+\(R\.drawable\.(scene_[a-z0-9_]+),', code, re.MULTILINE)
legacy = {"scene_summit", "scene_sea", "scene_wisdom", "scene_forest"}
assert len(refs) == len(set(refs)) == 23, "Atmosfer must define 23 distinct scenes"
assert legacy.issubset(refs), "One of the four existing scenes is missing"
expected = {name.removeprefix("scene_") for name in set(refs) - legacy}
assert len(records) == manifest["count"] == len(expected) == 19, "All 19 new scenes need provenance"
assert {record["id"] for record in records} == expected, "Scene metadata does not match Atmosfer"
assert len({record["sourceSha256"] for record in records}) == 19, "Duplicate generated scene source"
assert len({record["sha256"] for record in records}) == 19, "Duplicate bundled scene image"
for record in records:
    key = record["id"]
    assert record["asset"] == f"app/src/main/res/drawable-nodpi/scene_{key}.webp", f"Unexpected path: {key}"
    data = (repo / record["asset"]).read_bytes()
    assert len(data) == record["bytes"] > 0, f"Incorrect scene length: {key}"
    assert hashlib.sha256(data).hexdigest() == record["sha256"], f"Scene content changed: {key}"
    assert list(webp_size(data)) == record["size"] == [1080, 1440], f"Incorrect encoded dimensions: {key}"
    assert isinstance(record["prompt"], str) and len(record["prompt"]) > 80, f"Missing generation prompt: {key}"

resolved = []
for name in refs:
    files = [path for folder in (repo / "app/src/main/res").glob("drawable*") for path in folder.glob(name + ".*") if path.is_file()]
    assert files, f"Atmosfer references a missing resource: {name}"
    resolved.append(hashlib.sha256(files[0].read_bytes()).hexdigest())
assert len(set(resolved)) == 23, "Atmosfer references repeated scene content"
bundled = {path.stem.removeprefix("scene_") for path in (repo / "app/src/main/res/drawable-nodpi").glob("scene_*.webp")}
assert bundled == expected, "Unexpected or missing new scene resources"
print(f"19 unique scene images with matching provenance; all 23 Atmosfer resources exist; {sum(record['bytes'] for record in records):,} new scene bytes.")
