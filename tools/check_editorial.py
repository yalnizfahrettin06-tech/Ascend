"""Editorial release contracts, not a substitute for native-language review."""
import json, re, unicodedata
from pathlib import Path
from icerik_derle import ROOT, read_master, fingerprint, norm

def check():
    master = read_master()
    source = {q['id']: q['text'] for q in master['quotes']}
    catalog = {'en': source}
    for lang in ['tr', 'pt', 'de', 'fr', 'it', 'ru']:
        rows = json.loads((ROOT / f'content/translations/{lang}.json').read_text(encoding='utf-8'))['quotes']
        assert set(rows) == set(source), f'{lang}: missing or orphaned IDs'
        assert all(r['sourceHash'] == fingerprint(source[qid]) for qid, r in rows.items()), f'{lang}: stale source'
        catalog[lang] = {qid: r['text'] for qid, r in rows.items()}
    intents = {}
    for line in (ROOT / 'content/editorial/category-intents.tr.txt').read_text(encoding='utf-8-sig').splitlines():
        if not line.strip() or line.startswith('#'): continue
        key, purpose = line.split('|', 1)
        assert key not in intents and len(purpose) >= 30, key
        intents[key] = purpose
    assert set(intents) == {c['id'] for c in master['categories']}, 'Category intent coverage'
    reviewed = set()
    for line in (ROOT / 'content/editorial/revision-9.19.txt').read_text(encoding='utf-8-sig').splitlines():
        if not line.strip() or line.startswith('#'): continue
        fields = [x.strip() for x in line.split(' | ')]
        assert len(fields) == 8
        qid = 'v5_' + fields[0]
        assert qid not in reviewed
        reviewed.add(qid)
        for lang, text in zip(['tr', 'en', 'pt', 'de', 'fr', 'it', 'ru'], fields[1:]):
            assert catalog[lang][qid] == text, (qid, lang, 'review/runtime mismatch')
    corrections = json.loads((ROOT / 'content/editorial/locale-corrections-9.19.json').read_text(encoding='utf-8-sig'))
    for lang, rows in corrections.items():
        for qid, text in rows.items(): assert catalog[lang][qid] == text, (qid, lang)
    for lang, rows in catalog.items():
        assert len({norm(t) for t in rows.values()}) == len(rows), f'{lang}: duplicate text'
        for qid, text in rows.items():
            assert unicodedata.normalize('NFC', text) == text and text.strip() == text
            assert (40 <= len(text) <= 120) if lang in ['en', 'tr'] else (20 <= len(text) <= 300), (qid,lang)
            if lang == 'en': continue
            # Catch accidentally retained source clauses, including punctuation differences.
            en = re.findall(r'\w+', source[qid].casefold())
            target = ' '.join(re.findall(r'\w+', text.casefold()))
            assert not any(' '.join(en[i:i+5]) in target for i in range(len(en)-4)), (qid,lang,'English clause remains')
    print(f'Editorial contracts passed: {len(intents)} category intents; {len(source)*7} texts screened; {len(reviewed)} seven-language revisions; {sum(map(len,corrections.values()))} additional locale fixes.')

if __name__ == '__main__': check()
