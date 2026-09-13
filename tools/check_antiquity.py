"""Keep the reviewed bilingual expansion aligned with the packaged catalogue."""
import json
from icerik_derle import ROOT, read_master, render
master = read_master()
translation = json.loads((ROOT/'content/translations/tr.json').read_text(encoding='utf-8'))
render(master, translation)
source = {q['id']: q for q in master['quotes']}
seen = set()
for line in (ROOT/'content/katalog/antiquity-expansion.txt').read_text(encoding='utf-8').splitlines():
    if not line.strip(): continue
    if line.startswith('['):
        key, tr_name, en_name, group = line[1:-1].split('|')
        category = next(c for c in master['categories'] if c['id'] == key)
        assert category['group'] == group and category['name'] == en_name
        assert category['kind'] == 'inspired_reflection'
        index = 0
    else:
        tr, en = line.split(' | ')
        index += 1
        qid = f'v5_{key}_{index:02}'
        assert qid not in seen
        seen.add(qid)
        assert source[qid]['text'] == en and source[qid]['category'] == key
        assert translation['quotes'][qid]['text'] == tr
assert len(seen) == 70
print('70 reviewed bilingual reflections match the expansion source and stable IDs.')
