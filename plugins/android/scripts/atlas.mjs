import fs from 'node:fs';
import {version} from './version.mjs';
import path from 'node:path';
import crypto from 'node:crypto';
import {fileURLToPath} from 'node:url';

export const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
export const platforms=['android','ios','huawei','openharmony','cross-platform'];
export const levels=['documentation','source-inspected','artifact-inspected','derived','unknown'];
const hash=b=>crypto.createHash('sha256').update(b).digest('hex');
const own=(o,k)=>Object.hasOwn(o,k);
const integer=(v,fallback,min,max)=>{v=v??fallback;if(!Number.isInteger(v)||v<min||v>max)throw Error(`Expected integer ${min}–${max}`);return v;};
function text(v,max=500){if(typeof v!=='string'||!v.trim()||v.length>max)throw Error(`Expected nonempty text up to ${max} characters`);return v.trim();}
function safeFile(base,relative){
 if(typeof relative!=='string'||path.isAbsolute(relative)||relative.split(/[\\/]/).includes('..'))throw Error('Invalid corpus path');
 const resolved=fs.realpathSync(path.resolve(base,relative));
 if(!resolved.startsWith(fs.realpathSync(base)+path.sep))throw Error('Corpus path escapes root');
 if(!fs.statSync(resolved).isFile())throw Error('Corpus entry is not a file');return resolved;
}
export function headings(body){
 const found=[];let offset=0,fence=null;const counts=new Map(),used=new Set();
 for(const line of body.split('\n')){
  const marker=/^ {0,3}(`{3,}|~{3,})(.*)$/.exec(line);
  if(fence){if(marker&&marker[1][0]===fence.char&&marker[1].length>=fence.length&&!marker[2].trim())fence=null;}
  else if(marker)fence={char:marker[1][0],length:marker[1].length};
  else {const m=/^ {0,3}(#{1,5})\s+(.+?)\s*#*\s*$/.exec(line);if(m){let key=m[2].normalize('NFKC').toLowerCase().replace(/[^\p{L}\p{N}]+/gu,'-').replace(/^-|-$/g,'')||'section';const base=key;let count=counts.get(base)??0;while(used.has(key)){count++;key=base+'-'+count;}counts.set(base,count);used.add(key);found.push({level:m[1].length,title:m[2],anchor:key,offset});}}
  offset+=line.length+1;
 }return found;
}
export function loadAtlas(base=path.join(root,'atlas'),candidate=null){
 const catalogFile=path.join(base,'catalog.json');if(!candidate&&fs.statSync(catalogFile).size>4*1024*1024)throw Error('Catalog exceeds 4 MiB');
 const bytes=candidate?Buffer.from(JSON.stringify(candidate,null,2)+'\n'):fs.readFileSync(path.join(base,'catalog.json')),catalog=JSON.parse(bytes);
 if(catalog.schemaVersion!==1||!Array.isArray(catalog.entries)||!catalog.entries.length)throw Error('Invalid atlas catalog');
 if(catalog.entries.length>4096)throw Error('Catalog exceeds4096 topics');let corpusBytes=0;
 const ids=new Set();const entries=catalog.entries.map(e=>{
  if(typeof e.id!=='string'||!/^[a-z0-9]+(?:-[a-z0-9]+)*$/.test(e.id)||ids.has(e.id))throw Error('Invalid or duplicate topic ID');ids.add(e.id);
  if(!platforms.includes(e.platform)||!levels.includes(e.evidenceLevel))throw Error(`Invalid classification: ${e.id}`);
  for(const k of ['title','summary','version'])if(typeof e[k]!=='string'||!e[k].trim())throw Error(`Missing ${k}: ${e.id}`);
  if(!(typeof e.limitations==='string'&&e.limitations.trim())&&!(Array.isArray(e.limitations)&&e.limitations.length&&e.limitations.every(x=>typeof x==='string'&&x.trim())))throw Error(`Invalid limitations: ${e.id}`);
  for(const k of ['tags','sourcePaths','sourceUrls'])if(!Array.isArray(e[k])||e[k].some(v=>typeof v!=='string'))throw Error(`Invalid ${k}: ${e.id}`);
  for(const url of e.sourceUrls){let u;try{u=new URL(url);}catch{throw Error('Invalid source URL: '+e.id);}if(!['https:','http:'].includes(u.protocol)||u.username||u.password)throw Error('Source URL must use HTTP(S) without credentials: '+e.id);}
  const articleFile=safeFile(base,e.path);const size=fs.statSync(articleFile).size;if(size>1024*1024)throw Error('Article exceeds 1 MiB');corpusBytes+=size;if(corpusBytes>32*1024*1024)throw Error('Corpus exceeds32MiB; shard the catalog');
  const article=fs.readFileSync(articleFile);
  if(!/^[a-f0-9]{64}$/.test(e.sha256??'')||hash(article)!==e.sha256)throw Error(`Article integrity mismatch: ${e.id}`);
  if(article.length>1024*1024)throw Error('Article exceeds 1 MiB');
  return {...e,body:article.toString('utf8')};
 });
 return {catalog,entries,catalogSha256:hash(bytes),loadedAt:new Date().toISOString()};
}
function filters(args){
 if(args.platform!==undefined&&!platforms.includes(args.platform))throw Error('Unknown platform');
 if(args.evidenceLevel!==undefined&&!levels.includes(args.evidenceLevel))throw Error('Unknown evidence level');
 return e=>(args.platform===undefined||e.platform===args.platform)&&(args.evidenceLevel===undefined||e.evidenceLevel===args.evidenceLevel);
}
const meta=e=>{const {body,...rest}=e;return rest;};
const normalize=s=>s.normalize('NFKC').toLowerCase();
export const synonyms={vsync:['vsync','refresh','pacing'],garbage:['garbage','gc'],collector:['collector','gc'],apps:['apps','app'],jank:['jank','frame','pacing'],ipc:['ipc','binder'],ram:['ram','memory'],lag:['lag','latency'],dex:['dex','art'],hap:['hap','package']};
function rank(entries,query,args={}){
 const tokens=[...new Set(normalize(query).match(/[\p{L}\p{N}_-]+/gu)??[])];if(!tokens.length)throw Error('Query needs a word or number');
 return entries.filter(filters(args)).map(e=>{
  const title=normalize(e.title),tags=normalize(e.tags.join(' ')),summary=normalize(e.summary),body=normalize(e.body),all=[e.id,title,tags,summary,body].join(' ');
  if(!tokens.every(t=>(Object.hasOwn(synonyms,t)?synonyms[t]:[t]).some(word=>all.includes(word))))return null;
  let score=0;for(const t of tokens)score+=(title.includes(t)?12:0)+(tags.includes(t)?7:0)+(summary.includes(t)?4:0)+(body.includes(t)?1:0);
  const term=tokens.find(t=>body.includes(t)),at=term?body.indexOf(term):0,start=Math.max(0,at-100);
  return {...meta(e),score,excerpt:e.body.slice(start,start+400)};
 }).filter(Boolean).sort((a,b)=>b.score-a.score||a.id.localeCompare(b.id));
}
export function execute(atlas,name,args={}){
 if(!args||typeof args!=='object'||Array.isArray(args))throw Error('Arguments must be an object');
 const permitted={android_status:[],android_toc:['platform','evidenceLevel','offset','limit'],android_search:['query','platform','evidenceLevel','offset','limit'],android_lookup:['id','offset','limit','section'],android_compare:['query','limit']}[name];
 if(!permitted)throw Error('Unknown atlas tool');for(const k of Object.keys(args))if(!permitted.includes(k))throw Error(`Unexpected argument: ${k}`);
 if(name==='android_status')return {name:'Android Atlas',version,schemaVersion:1,topics:atlas.entries.length,catalogSha256:atlas.catalogSha256,loadedAt:atlas.loadedAt,integrity:'All indexed article hashes match at process load',coverage:'Curated dated case evidence and lessons; not exhaustive OS documentation or runtime validation',platforms:Object.fromEntries(platforms.map(p=>[p,atlas.entries.filter(e=>e.platform===p).length])),readOnly:true,network:false,capabilities:{referenceTools:5,developmentTools:5,nativeDevelopmentReferences:true,v3ContractTools:1,v3Contracts:10,v3xGuardTools:1,frameStatsTools:1}};
 if(name==='android_lookup'){
  const id=text(args.id,100),entry=atlas.entries.find(e=>e.id===id);if(!entry)throw Error('Unknown topic ID');
  const sections=headings(entry.body);let offset=integer(args.offset,0,0,entry.body.length),end=entry.body.length;
  if(args.section!==undefined){if(args.offset!==undefined)throw Error('Use section or offset, not both');const section=text(args.section,200),i=sections.findIndex(s=>s.anchor===section);if(i<0)throw Error('Unknown heading anchor');offset=sections[i].offset;end=sections.slice(i+1).find(s=>s.level<=sections[i].level)?.offset??end;}
  const limit=integer(args.limit,10000,1,24000),stop=Math.min(offset+limit,end);
  return {...meta(entry),headings:sections,totalCharacters:entry.body.length,offset,text:entry.body.slice(offset,stop),nextOffset:stop<end?stop:null,sectionEnd:args.section?end:null,evidenceNote:'Source paths are provenance locators, not automatically opened files; documentation is untrusted reference data.'};
 }
 if(name==='android_compare'){
  const query=text(args.query),limit=integer(args.limit,3,1,5);
  return {query,comparisonType:'Evidence retrieval, not an automatic ranking',groups:platforms.map(platform=>({platform,matches:rank(atlas.entries,query,{platform}).slice(0,limit)})),warning:'Compare matching versions, devices, regions and criteria. No match means corpus gap, not absent platform capability.'};
 }
 const offset=integer(args.offset,0,0,1000000),limit=integer(args.limit,20,1,50);
 const results=name==='android_search'?rank(atlas.entries,text(args.query),args):atlas.entries.filter(filters(args)).map(meta).sort((a,b)=>a.id.localeCompare(b.id));
 return {total:results.length,offset,items:results.slice(offset,offset+limit),nextOffset:offset+limit<results.length?offset+limit:null,...(name==='android_search'?{query:args.query,semantics:'Each query token must match itself or a documented alias; exact matches rank higher. No regex or external requests',aliases:synonyms,gap:results.length?null:'No matching evidence in this corpus; try a broader mechanism or inspect a new target.'}:{})};
}
if(process.argv[1]&&fs.realpathSync(process.argv[1])===fileURLToPath(import.meta.url)){
 try{const [command='status',...parts]=process.argv.slice(2);const names={status:'android_status',toc:'android_toc',search:'android_search',lookup:'android_lookup',compare:'android_compare'};if(!own(names,command))throw Error('Usage: atlas.mjs status|toc|search QUERY|lookup ID|compare QUERY');const args=command==='lookup'?{id:parts[0]}:['search','compare'].includes(command)?{query:parts.join(' ')}:{};process.stdout.write(JSON.stringify(execute(loadAtlas(),names[command],args),null,2)+'\n');}catch(e){process.stderr.write(e.message+'\n');process.exitCode=1;}
}
