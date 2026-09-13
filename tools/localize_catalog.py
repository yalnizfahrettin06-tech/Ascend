"""Editorial translation import. Network is used only when explicitly run, never in the app/CI."""
from pathlib import Path
import re,json,hashlib,time,urllib.request,urllib.parse,unicodedata,sys
import xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
LANGS=['pt','de','fr','it','ru']
def strings(s):
    i=0
    while i<len(s):
        if s.startswith('//',i):
            i=s.find('\n',i) if '\n' in s[i:] else len(s);continue
        if s.startswith('/*',i):
            j=s.find('*/',i+2);i=len(s) if j<0 else j+2;continue
        if s[i]!='"': i+=1;continue
        i+=1;out='';args=0
        while i<len(s):
            if s[i]=='\\' and i+1<len(s):
                out+={'n':'\n','t':'\t','r':'\r'}.get(s[i+1],s[i+1]);i+=2
            elif s[i]=='"': i+=1;break
            elif s.startswith('${',i):
                depth=1;i+=2
                while i<len(s) and depth:
                    if s[i]=='"':
                        i+=1
                        while i<len(s) and s[i]!='"': i+=2 if s[i]=='\\' else 1
                    elif s[i]=='{': depth+=1
                    elif s[i]=='}': depth-=1
                    i+=1
                out+='{'+str(args)+'}';args+=1
            elif s[i]=='$' and re.match(r'[A-Za-z_]',s[i+1:i+2]):
                m=re.match(r'\$\w+',s[i:]);i+=len(m[0]);out+='{'+str(args)+'}';args+=1
            else: out+=s[i];i+=1
        yield out
def ui_source():
    vals=set()
    for p in (ROOT/'app/src/main/java').rglob('*.kt'):
        if p.name in {'IcerikVerisi.kt','EskiSozler.kt'} or p.name.startswith('Locale'):continue
        for v in strings(p.read_text(encoding='utf-8')):
            if 1<=len(v)<=1200 and re.search('[A-Za-z]',v) and not re.search('[çğıöşüÇĞİÖŞÜ]',v):
                if (' ' in v or re.fullmatch(r'[A-Z][a-zA-ZéêàâîïôûùëèñóíőøæÉ]+',v)) and not any(x in v for x in ['://','android.','com.','{\"','<resources','video/','image/','SELECT ','CREATE ','viewModel']): vals.add(v)
    for e in ET.parse(ROOT/'app/src/main/res/values-en/strings.xml').getroot():
        if e.tag=='string': vals.add(''.join(e.itertext()).replace("\\'","'"))
    return sorted(vals)
def request(text,lang):
    params=urllib.parse.urlencode(dict(client='gtx',sl='en',tl=lang,dt='t',q=text))
    with urllib.request.urlopen('https://translate.googleapis.com/translate_a/single?'+params,timeout=45) as r: data=json.load(r)
    return unicodedata.normalize('NFC',''.join(x[0] for x in data[0] if x[0]))
def translate(values,lang,cache):
    missing=[v for v in values if v not in cache]
    for start in range(0,len(missing),16):
        batch=missing[start:start+16]
        # Preserve line boundaries, interpolation and Android formatting through translation.
        protected=[];tokens=[]
        for v in batch:
            tok=[]
            def sub(m): tok.append(m[0]);return f'ZXQ{len(tok)-1}QXZ'
            protected.append(re.sub(r'\{\d+\}|%\d+\$[dsf]|%[dsf]|\n',sub,v));tokens.append(tok)
        for attempt in range(4):
            try:
                output=request('\n'.join(protected),lang).split('\n')
                if len(output)!=len(batch): output=[request(v,lang) for v in protected]
                for original,result,tok in zip(batch,output,tokens):
                    for n,value in enumerate(tok):
                        pattern=rf'ZXQ\s*{n}\s*QXZ'
                        assert re.search(pattern,result,re.I),(lang,original,result)
                        result=re.sub(pattern,lambda m:value,result,flags=re.I)
                    assert result.strip()
                    cache[original]=result.strip()
                break
            except Exception:
                if attempt==3: raise
                time.sleep(3*(attempt+1))
        (ROOT/f'content/translations/cache-{lang}.json').write_text(json.dumps(cache,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
        print(lang,start+len(batch),'/',len(missing),flush=True)
        time.sleep(.4)
def main():
    master=json.loads((ROOT/'content/source.en.json').read_text(encoding='utf-8'))
    ui=ui_source()
    (ROOT/'content/ui').mkdir(exist_ok=True)
    (ROOT/'content/ui/source.en.json').write_text(json.dumps(ui,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print('UI source strings:',len(ui),flush=True)
    for lang in sys.argv[1:] or LANGS:
        path=ROOT/f'content/translations/cache-{lang}.json'
        cache=json.loads(path.read_text(encoding='utf-8')) if path.exists() else {}
        translate(list(dict.fromkeys([q['text'] for q in master['quotes']]+ui)),lang,cache)
        data={'schemaVersion':1,'locale':lang,'sourceCatalogVersion':master['catalogVersion'],
              'editorialStatus':'machine-assisted; sampled review pending','quotes':{q['id']:{'text':cache[q['text']],'sourceHash':hashlib.sha256(q['text'].encode()).hexdigest()} for q in master['quotes']}}
        (ROOT/f'content/translations/{lang}.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
        (ROOT/f'content/ui/{lang}.json').write_text(json.dumps({v:cache[v] for v in ui},ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
if __name__=='__main__': main()
