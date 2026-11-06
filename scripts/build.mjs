import {spawnSync} from 'node:child_process';import {fileURLToPath} from 'node:url';
for(const [relative,args] of [['../plugins/android/scripts/build.mjs',[]],['./manifest.mjs',['--write']]]){const r=spawnSync(process.execPath,[fileURLToPath(new URL(relative,import.meta.url)),...args],{stdio:'inherit'});if(r.status!==0)process.exit(r.status??1);}
