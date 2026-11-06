import fs from 'node:fs';
import {parseInlineLinks} from './inline-links.mjs';
import path from 'node:path';
import crypto from 'node:crypto';
import {root,loadAtlas,headings,synonyms} from './atlas.mjs';
const base=path.join(root,'atlas'),catalogPath=path.join(base,'catalog.json');
const catalog=JSON.parse(fs.readFileSync(catalogPath,'utf8'));
for(const e of catalog.entries){
 const p=path.resolve(base,e.path),real=fs.realpathSync(p);
 if(!real.startsWith(fs.realpathSync(base)+path.sep))throw Error('Article escapes corpus');
 e.sha256=crypto.createHash('sha256').update(fs.readFileSync(real)).digest('hex');
}
catalog.builtAt=new Date().toISOString();
const atlas=loadAtlas(base,catalog),data=JSON.stringify(atlas.entries.map(e=>({...e,headings:headings(e.body)}))).replaceAll('<','\\u003c').replaceAll('\u2028','\\u2028').replaceAll('\u2029','\\u2029');
let html=fs.readFileSync(path.join(root,'scripts/atlas-template.html'),'utf8');
if(!html.includes('ATLAS_DATA_PLACEHOLDER'))throw Error('Missing data slot');
html=html.replace('ATLAS_DATA_PLACEHOLDER',()=>data).replace('ATLAS_ALIASES_PLACEHOLDER',()=>JSON.stringify(synonyms)).replace('INLINE_PARSER_PLACEHOLDER',()=>parseInlineLinks.toString());
fs.writeFileSync(catalogPath,JSON.stringify(catalog,null,2)+'\n');
fs.writeFileSync(path.join(base,'index.html'),html);
const toc=['# Android Atlas — contents','','Curated evidence-linked reference. No whole-OS completeness or runtime validation claim.',''];
for(const e of atlas.entries)toc.push(`- [${e.title}](${e.path}) — \`${e.id}\` · ${e.platform} · ${e.evidenceLevel}`);
fs.writeFileSync(path.join(base,'CONTENTS.md'),toc.join('\n')+'\n');
console.log(JSON.stringify({topics:atlas.entries.length,catalogSha256:atlas.catalogSha256,htmlBytes:Buffer.byteLength(html)}));
