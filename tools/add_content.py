"""Merge reviewed bilingual additions (content/additions/*.txt) into the English master and Turkish translation.

Line format: category | English text | Turkish text. IDs continue each category's numbering and are
stable once merged: an addition that already exists (same English text) is skipped, never renumbered.
Run: python tools/add_content.py   (then python tools/icerik_derle.py)
"""
import json, re, sys
from pathlib import Path
from icerik_derle import ROOT, fingerprint, norm, validate_text

def main():
    master_path = ROOT / 'content/source.en.json'
    tr_path = ROOT / 'content/translations/tr.json'
    master = json.loads(master_path.read_text(encoding='utf-8'))
    tr = json.loads(tr_path.read_text(encoding='utf-8'))
    categories = {c['id'] for c in master['categories']}
    known_en = {norm(q['text']) for q in master['quotes']}
    known_tr = {norm(r['text']) for r in tr['quotes'].values()}
    added = 0
    for file in sorted((ROOT / 'content/additions').glob('*.txt')):
        for n, line in enumerate(file.read_text(encoding='utf-8').splitlines(), 1):
            if not line.strip() or line.startswith('#'): continue
            parts = [p.strip() for p in line.split(' | ')]
            assert len(parts) == 3, f'{file.name}:{n} expected 3 fields'
            category, en, tr_text = parts
            assert category in categories, f'{file.name}:{n} unknown category {category}'
            validate_text(en, f'{file.name}:{n} en'); validate_text(tr_text, f'{file.name}:{n} tr')
            if norm(en) in known_en: continue
            assert norm(tr_text) not in known_tr, f'{file.name}:{n} repeated Turkish text'
            numbers = [int(q['id'].rsplit('_', 1)[1]) for q in master['quotes'] if q['category'] == category]
            qid = f'v5_{category}_{max(numbers) + 1:02d}'
            master['quotes'].append({'id': qid, 'category': category, 'text': en})
            tr['quotes'][qid] = {'text': tr_text, 'sourceHash': fingerprint(en)}
            known_en.add(norm(en)); known_tr.add(norm(tr_text)); added += 1
    master_path.write_text(json.dumps(master, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    tr_path.write_text(json.dumps(tr, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(f'{added} new bilingual quotes merged.')

if __name__ == '__main__':
    try: main()
    except AssertionError as e: print(f'Addition rejected: {e}', file=sys.stderr); sys.exit(1)
