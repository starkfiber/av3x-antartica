"""Import this case's authored comparisons, not third-party manuals or binaries."""
import json,re,hashlib,sys
from pathlib import Path
root=Path(__file__).resolve().parents[1];atlas=root/'atlas';case=Path(sys.argv[1]).resolve() if len(sys.argv)==2 else None
if case is None: raise SystemExit('Usage: import-comparisons.py /absolute/path/to/research-case')
p=atlas/'catalog.json';catalog=json.loads(p.read_text());entries=catalog['entries']
imports=[('huawei-platform-families','Huawei platform families and Android comparison','huawei','epoch-5/huawei/comparison.md','documentation','HarmonyOS7 official documentation as of 2026-11-06; regional EMUI15; pinned OpenHarmony6.0 source separately scoped','Distinguish EMUI, legacy HarmonyOS, current commercial HarmonyOS and OpenHarmony; compare runtime, graphics and packages.'),('ios-restore-identity-comparison','iOS restore identities versus Android firmware contracts','ios','epoch-5/apple/comparison.md','artifact-inspected','iPhone14,7 iOS27.0.1 build24A446 retained manifest; no runtime','Compare seven identity differences, 99 shared component objects and installation trust boundaries.'),('platform-tradeoffs','Android, Huawei and iOS: criteria-based tradeoffs','cross-platform','epoch-5/comparison-atlas.md','derived','Scoped case evidence as of 2026-11-06','Compare inspectability, app compatibility, graphics contracts and unanswered performance questions.')]
records=[]
for id,title,platform,rel,level,version,summary in imports:
 source=case/rel;body=source.read_text()
 # Existing relative links are provenance locators, not paths within the packaged corpus.
 def link(m):
  label,target=m.groups()
  if re.match(r'^[a-z]+:',target) or target.startswith('#'):return m.group(0)
  return '['+label+']('+str((source.parent/target).resolve())+')'
 body=re.sub(r'\[([^\]]+)\]\(([^)]+)\)',link,body)
 body+='\n\n## Import provenance\n\nCase-authored snapshot: '+str(source)+'\nNo new runtime validation is implied by packaging this article.\n'
 target=atlas/'articles'/f'{id}.md';target.write_text(body)
 urls=sorted(set(x.rstrip('.,;') for x in re.findall(r'https?://[^\s)<>]+',body)))
 entries=[e for e in entries if e['id']!=id]
 entries.append(dict(id=id,title=title,platform=platform,tags=['comparison','cpu','gpu','runtime','compatibility','firmware','huawei' if platform=='huawei' else platform],evidenceLevel=level,version=version,summary=summary,path=f'articles/{id}.md',sourcePaths=[str(source)],sourceUrls=urls,limitations=['No device execution or measured performance comparison.','Public source and documentation do not establish a matching commercial binary.','Follow per-claim evidence scope in the article.']))
 records.append({'source':str(source),'sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'article':str(target.relative_to(atlas))})
# A separate exact source topic makes the OpenHarmony evidence discoverable by platform.
source=case/'epoch-5/huawei/comparison.md';body=source.read_text();section=body.split('## 3. A concrete lifecycle anchor, including failure paths')[1].split('## 4.')[0]
id='openharmony-uiability-lifecycle';article='# OpenHarmony UIAbility lifecycle source trace\n\n'+section+'\n\n## Scope\n\nPinned OpenHarmony6.0 public source; not a commercial Huawei binary or runtime trace. See huawei-platform-families for the wider comparison.\n'
(atlas/'articles'/f'{id}.md').write_text(article)
entries=[e for e in entries if e['id']!=id];entries.append(dict(id=id,title='OpenHarmony UIAbility lifecycle source trace',platform='openharmony',tags=['lifecycle','runtime','uiability','cpu','application'],evidenceLevel='source-inspected',version='OpenHarmony6.0 tag resolved to 74e01f0c15715f404a80c802aaebb958cf25b908',summary='Trace runtime-language dispatch, early returns and optional lifecycle work in pinned UIAbility source.',path=f'articles/{id}.md',sourcePaths=[str(source),str(case/'epoch-5/huawei/ui_ability.cpp'),str(case/'epoch-5/huawei/source-provenance.json')],sourceUrls=['https://github.com/openharmony/ability_ability_runtime/blob/74e01f0c15715f404a80c802aaebb958cf25b908/frameworks/native/ability/native/ui_ability.cpp'],limitations=['One complete source file inspected; callback binding and runtime behavior not fully traced.','No commercial Huawei binary/source equivalence established.']))
catalog['entries']=entries;p.write_text(json.dumps(catalog,indent=2,ensure_ascii=False)+'\n');(atlas/'comparison-import-provenance.json').write_text(json.dumps(records,indent=2)+'\n');print('Topics:',len(entries))
