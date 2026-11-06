#!/bin/sh
set -eu
atlas_root=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
for atlas_node in "${ANDROID_ATLAS_NODE:-}" /opt/homebrew/opt/node@22/bin/node /usr/local/opt/node@22/bin/node node /Applications/ChatGPT.app/Contents/Resources/cua_node/bin/node; do
  [ -n "$atlas_node" ] || continue
  if "$atlas_node" -e 'if(Number(process.versions.node.split(".")[0])<22)process.exit(1)' >/dev/null 2>&1; then
    exec "$atlas_node" "$atlas_root/scripts/mcp-server.mjs" --stdio
  fi
done
printf '%s\n' 'Android Atlas needs working Node 22+. Set ANDROID_ATLAS_NODE to its executable.' >&2
exit 1
