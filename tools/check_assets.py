"""Every referenced drawable exists, nothing bundled is unused, and images stay within budget."""
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main = root / "app/src/main"
images = {p.stem: p for p in (main / "res/drawable-nodpi").iterdir()}
code = "\n".join(p.read_text(encoding="utf-8") for p in (main / "java").rglob("*.kt"))
xml = "\n".join(p.read_text(encoding="utf-8") for p in (main / "res").rglob("*.xml")) + (main / "AndroidManifest.xml").read_text(encoding="utf-8")
referenced = set(re.findall(r"R\.drawable\.([a-z0-9_]+)", code)) | set(re.findall(r"@drawable/([a-z0-9_]+)", xml))
drawables = images.keys() | {p.stem for p in (main / "res/drawable").iterdir()}
missing = sorted(referenced - drawables)
unused = sorted(set(images) - referenced)
assert not missing, f"Missing drawables: {missing}"
assert not unused, f"Unused bundled images: {unused}"
too_big = {k: p.stat().st_size for k, p in images.items() if p.stat().st_size > 600 * 1024}
assert not too_big, f"Images over 600 KB: {too_big}"
assert all(p.suffix == ".webp" for p in images.values()), "Bundle photographs as WebP"
total = sum(p.stat().st_size for p in images.values())
assert total < 12_000_000, f"Image budget exceeded: {total:,} bytes"
print(f"{len(images)} images referenced and within budget: {total / 1e6:.1f} MB.")
