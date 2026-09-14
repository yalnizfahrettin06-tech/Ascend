"""Check new artwork identity, integrity and all picker references."""
import hashlib,json,re
from pathlib import Path
root=Path(__file__).resolve().parents[1]
manifest=json.loads((root/'docs/warrior-art.json').read_text(encoding='utf-8'))
prompts=json.loads((root/'docs/warrior-prompts.json').read_text(encoding='utf-8'))['scenes']
rows=manifest['artworks']
assert len(rows)==10 and len({r['sha256'] for r in rows})==10
assert {r['id'] for r in rows}==set(prompts)
code=(root/'app/src/main/java/com/yalnizfahrettin/azim/ui/Atmosfer.kt').read_text(encoding='utf-8-sig')
refs=set(re.findall(r'R.drawable.warrior_([a-z]+)',code))
assert refs==set(prompts)
for r in rows:
 data=(root/r['asset']).read_bytes()
 assert len(data)==r['bytes'] and hashlib.sha256(data).hexdigest()==r['sha256']
 assert data[:4]==b'RIFF' and data[8:12]==b'WEBP'
 assert r['size']==[1024,1536] and len(prompts[r['id']])>80
 assert r['asset']==f"app/src/main/res/drawable-nodpi/warrior_{r['id']}.webp"
for name in ['PaylasimEkrani.kt','AtmosferSecici.kt']:
 s=(root/'app/src/main/java/com/yalnizfahrettin/azim/ui'/name).read_text(encoding='utf-8-sig')
 assert 'Atmosfer.gallery' in s and 'Atmosfer.entries' not in s
print(f'10 unique original warrior artworks, full resolution and curated picker references verified: {sum(r["bytes"] for r in rows):,} bytes.')
