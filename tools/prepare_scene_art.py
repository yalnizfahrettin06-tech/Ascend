#!/usr/bin/env python3
"""Bundle full-screen Ascend scene artwork without modifying source images.

Requires Pillow. Example:
  python tools/prepare_scene_art.py --map ../scene-art-map.json \
      --prompts ../scene-art-prompts.json

Only proportional resizing and WebP encoding are applied. The manifest records
the generation prompts and source/output hashes without machine-specific paths.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

from PIL import Image, ImageOps, __version__ as pillow_version

SCENES = {
    "arena", "kale", "hisar", "sovalye", "cadi", "bordo_doku", "turkuaz_doku",
    "grafit_doku", "lacivert_doku", "ametist_doku", "zeytin_doku", "bakir_doku",
    "kar_muhafizi", "atli_yolcu", "orman_muhafizi", "tas_salon", "col_yolcusu",
    "kiyi_nobeti", "kale_nobeti",
}
MAX_SIZE = (1080, 1440)
QUALITY = 88


def read_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8-sig"))


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--map", type=Path, required=True, dest="mapping")
    parser.add_argument("--prompts", type=Path, required=True)
    parser.add_argument("--repo", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    repo = args.repo.resolve()
    mapping = read_json(args.mapping)
    prompts = read_json(args.prompts)
    if isinstance(prompts, dict):
        prompts = [{"id": key, "prompt": value} for key, value in prompts.items()]
    if not isinstance(prompts, list) or any(
        not isinstance(item, dict) or not isinstance(item.get("prompt"), str) or not item.get("prompt")
        for item in prompts
    ):
        parser.error("Prompts must be ID/prompt records or an object mapping IDs to prompt strings")
    if set(mapping) != SCENES or {item["id"] for item in prompts} != SCENES or len(prompts) != len(SCENES):
        parser.error(f"The map and prompt set must each contain these {len(SCENES)} scenes exactly once: " + ", ".join(sorted(SCENES)))
    sources = {key: Path(value).resolve() for key, value in mapping.items()}
    if any(not path.is_file() for path in sources.values()):
        parser.error("A mapped scene source image does not exist")
    hashes = {key: sha256(path) for key, path in sources.items()}
    if len(set(hashes.values())) != len(SCENES):
        parser.error("Every scene must have a distinct source image")
    output = repo / "app/src/main/res/drawable-nodpi"
    output.mkdir(parents=True, exist_ok=True)
    records = []
    for item in prompts:
        key = item["id"]
        target = output / f"scene_{key}.webp"
        with Image.open(sources[key]) as source:
            source_size = list(source.size)
            image = ImageOps.exif_transpose(source).convert("RGB")
            image.thumbnail(MAX_SIZE, Image.Resampling.LANCZOS)
            image.save(target, "WEBP", quality=QUALITY, method=6, exact=True)
            size = list(image.size)
        # Confirm that the final Android resource decodes to the expected dimensions.
        with Image.open(target) as bundled:
            bundled.load()
            if list(bundled.size) != size:
                parser.error(f"Encoded scene dimensions differ: {key}")
        records.append({
            "id": key,
            "prompt": item["prompt"],
            "asset": target.relative_to(repo).as_posix(),
            "sourceSize": source_size,
            "sourceSha256": hashes[key],
            "size": size,
            "bytes": target.stat().st_size,
            "sha256": sha256(target),
        })
    if len({record["sha256"] for record in records}) != len(SCENES):
        parser.error("The bundled scenes are not distinct")
    manifest = {
        "schemaVersion": 1,
        "generator": "image_gen.imagegen",
        "purpose": "Original full-screen scene backgrounds for Ascend home and Pro sharing",
        "interpretation": "AI-generated editorial illustrations, not documentary photographs of verified historical sites.",
        "processing": {
            "resize": "Proportional Lanczos thumbnail, no creative retouching or cropping",
            "maximumSize": list(MAX_SIZE),
            "format": "RGB WebP",
            "quality": QUALITY,
            "pillowVersion": pillow_version,
            "metadata": "No local source paths or image metadata are bundled",
        },
        "count": len(records),
        "scenes": records,
    }
    (repo / "docs/scene-art.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"prepared": len(records), "uniqueImages": len({item["sha256"] for item in records}), "totalBytes": sum(item["bytes"] for item in records), "size": list(MAX_SIZE)}))


if __name__ == "__main__":
    main()
