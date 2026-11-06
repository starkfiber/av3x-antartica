import test from 'node:test';import assert from 'node:assert/strict';import fs from 'node:fs';import os from 'node:os';import path from 'node:path';import {spawnSync} from 'node:child_process';import {root} from '../scripts/atlas.mjs';
test('clean copy can add a finding, rebuild, reject invalid update, and restore prior snapshot',()=>{
 const temp=fs.mkdtempSync(path.join(os.tmpdir(),'atlas-portable-'));try{
 const copy=path.join(temp,'moved-plugin');fs.cpSync(root,copy,{recursive:true,filter:p=>!p.includes('/.git')});
 const run=(script,...args)=>spawnSync(process.execPath,[path.join(copy,'scripts',script),...args],{encoding:'utf8',cwd:temp});
 assert.equal(run('atlas.mjs','lookup','gpu-frame-engine-lab').status,0);
 const file=path.join(copy,'atlas/catalog.json'),prior=fs.readFileSync(file);const catalog=JSON.parse(prior),article=path.join(copy,'atlas/articles/test-finding.md');fs.writeFileSync(article,'# Reviewed test finding\n\nThis is an isolated update-workflow test, not a platform claim.\n');
 catalog.entries.push({...catalog.entries[0],id:'test-finding',title:'Test finding',path:'articles/test-finding.md'});fs.writeFileSync(file,JSON.stringify(catalog));assert.equal(run('build.mjs').status,0);assert.equal(JSON.parse(run('atlas.mjs','lookup','test-finding').stdout).id,'test-finding');
 const valid=fs.readFileSync(file),invalid=JSON.parse(valid);invalid.entries.push({...invalid.entries[0]});fs.writeFileSync(file,JSON.stringify(invalid));const before=fs.readFileSync(file);assert.notEqual(run('build.mjs').status,0);assert.deepEqual(fs.readFileSync(file),before);
 fs.writeFileSync(file,prior);fs.unlinkSync(article);assert.equal(run('atlas.mjs','lookup','gpu-frame-engine-lab').status,0);assert.notEqual(run('atlas.mjs','lookup','test-finding').status,0);
 }finally{fs.rmSync(temp,{recursive:true,force:true});}
});
