"""Release contracts for the reviewed needs catalogue; no machine 'quality score'."""
from pathlib import Path
import json
from icerik_derle import read_master, render

ROOT = Path(__file__).resolve().parents[1]
master = read_master()
translation = json.loads((ROOT / 'content/translations/tr.json').read_text(encoding='utf-8'))
render(master, translation)
draft = json.loads((ROOT / 'content/katalog/faz1-kategori-taslagi.tr.json').read_text(encoding='utf-8'))
assert {c['id'] for c in draft['categories']} == {c['id'] for c in master['categories']}
expected = {c['id'] for c in draft['categories'] if c['isNew'] and c['type'] != 'person'}
pairs = {}
for line in (ROOT / 'content/katalog/faz2-ihtiyaclar-pairs.txt').read_text(encoding='utf-8').splitlines():
    if line.startswith('['):
        key = line[1:-1]
        assert key not in pairs
        pairs[key] = []
    elif line.strip():
        pairs[key].append(line.split(' | '))
assert set(pairs) == expected and len(expected) == 30
source = {q['id']: q for q in master['quotes']}
for key, rows in pairs.items():
    assert len(rows) == 10, key
    for index, (tr, en) in enumerate(rows, 1):
        qid = f'v5_{key}_{index:02}'
        assert source[qid]['text'] == en and translation['quotes'][qid]['text'] == tr, qid
        assert source[qid]['category'] == key
print('Phase 1 coverage complete: 30 new needs, 300 reviewed bilingual pairs, matching runtime source.')
