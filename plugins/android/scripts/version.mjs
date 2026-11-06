import fs from 'node:fs';
export const version=JSON.parse(fs.readFileSync(new URL('../.codex-plugin/plugin.json',import.meta.url))).version;
