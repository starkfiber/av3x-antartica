import fs from 'node:fs';import {spawnSync} from 'node:child_process';import {fileURLToPath} from 'node:url';
const root=fileURLToPath(new URL('../',import.meta.url));const tests=fs.readdirSync(root+'plugins/android/tests').filter(p=>p.endsWith('.test.mjs')).map(p=>root+'plugins/android/tests/'+p);
for(const args of [['--test',...tests],[root+'scripts/smoke.mjs'],[root+'scripts/manifest.mjs']]){const r=spawnSync(process.execPath,args,{stdio:'inherit',cwd:root});if(r.status!==0)process.exit(r.status??1);}
