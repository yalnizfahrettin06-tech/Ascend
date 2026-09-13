"""Apply reviewed UI vocabulary and preserve brand names in machine-assisted drafts."""
import json,re,sys
from localize_catalog import ROOT,LANGS,translate
terms={}
for line in (ROOT/'content/ui/reviewed-terms.txt').read_text(encoding='utf-8').splitlines():
    if line.strip():
        source,*targets=line.split(' | ')
        assert len(targets)==5,source
        terms[source]=dict(zip(LANGS,targets))
source_path=ROOT/'content/ui/source.en.json'
sources=json.loads(source_path.read_text(encoding='utf-8'))
sources=sorted(set(sources)|set(terms))
for lang in sys.argv[1:] or LANGS:
    path=ROOT/f'content/ui/{lang}.json'
    ui=json.loads(path.read_text(encoding='utf-8'))
    brands=[s for s in sources if 'Ascend' in s and s not in terms and s.strip()!='Ascend']
    # Regenerate just the few remaining brand-bearing strings with a protected proper name.
    cache_path=ROOT/f'content/translations/cache-{lang}.json'
    cache=json.loads(cache_path.read_text(encoding='utf-8'))
    protected=[s.replace('Ascend','ZXBRANDQXZ') for s in brands]
    translate(protected,lang,cache)
    for s,p in zip(brands,protected):
        assert 'ZXBRANDQXZ' in cache[p]
        ui[s]=cache[p].replace('ZXBRANDQXZ','Ascend')
    for s,targets in terms.items():ui[s]=targets[lang]
    for source in list(ui):
        template=re.sub(r'%([0-9]+)\$d',lambda m:'{'+str(int(m[1])-1)+'}',source)
        if template != source and template in terms:
            ui[source]=re.sub(r'\{([0-9]+)}',lambda m:'%'+str(int(m[1])+1)+'$d',terms[template][lang])
    if '  Ascend' in ui:ui['  Ascend']='  Ascend'
    path.write_text(json.dumps(ui,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    content_path=ROOT/f'content/translations/{lang}.json'
    content=json.loads(content_path.read_text(encoding='utf-8'))
    content['editorialStatus']='machine-assisted; key UI terminology and representative content sampled by assistant; native-language review pending'
    content_path.write_text(json.dumps(content,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
source_path.write_text(json.dumps(sources,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
