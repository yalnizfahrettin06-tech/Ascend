#!/usr/bin/env python3
"""Verify the bundled category art contract with Python's standard library only."""
import hashlib
import json
import re
from pathlib import Path

repo = Path(__file__).resolve().parents[1]
catalog = json.loads((repo / "content/source.en.json").read_text(encoding="utf-8"))
manifest = json.loads((repo / "docs/category-art.json").read_text(encoding="utf-8"))
expected = {record["id"] for record in catalog["categories"]}
records = manifest["categories"]
assert len(records) == manifest["count"] == len(expected) == 70, "All 70 categories need art"
assert {record["category"] for record in records} == expected, "Missing or unknown category artwork"
assert len({record["sha256"] for record in records}) == 70, "Duplicate bundled category images"
assert len({record["sourceSha256"] for record in records}) == 70, "Duplicate generated category sources"
for record in records:
    key = record["category"]
    assert record["asset"] == f"app/src/main/res/drawable-nodpi/category_{key}.webp", f"Unexpected resource path: {key}"
    data = (repo / record["asset"]).read_bytes()
    assert len(data) == record["bytes"] > 0, f"Incorrect resource length: {key}"
    assert hashlib.sha256(data).hexdigest() == record["sha256"], f"Changed category image: {key}"
    assert data[:4] == b"RIFF" and data[8:12] == b"WEBP", f"Invalid WebP header: {key}"
    assert 0 < record["size"][0] <= 512 and 0 < record["size"][1] <= 683, f"Oversized category image: {key}"
    assert "Subject: " in record["prompt"], f"Missing original prompt: {key}"

code = (repo / "app/src/main/java/com/yalnizfahrettin/azim/ui/KategoriGorseli.kt").read_text(encoding="utf-8")
pairs = re.findall(r'"([a-z][a-z0-9_]*)" to R\.drawable\.category_([a-z][a-z0-9_]*)', code)
assert len(pairs) == 70 and {key for key, _ in pairs} == expected, "Kotlin artwork lookup is incomplete"
assert all(key == resource for key, resource in pairs), "Category artwork maps to the wrong resource ID"
bundled = {path.stem.removeprefix("category_") for path in (repo / "app/src/main/res/drawable-nodpi").glob("category_*.webp")}
assert bundled == expected, "Unexpected or missing category resources"
print(f"70 unique category images, complete Kotlin map and matching provenance; {sum(record['bytes'] for record in records):,} bytes.")
