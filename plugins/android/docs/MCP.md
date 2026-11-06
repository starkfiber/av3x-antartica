# MCP: setup, calls and troubleshooting

MCP makes the local atlas and bounded inspectors available as tools. Skills explain when to use them; they do not magically add an Android SDK or an execution engine.

## Plugin route

The repository marketplace points at plugins/android. Its compatibility manifest registers `.codex-plugin/mcp.json`, which launches the bundled script with `${PLUGIN_ROOT}`. The launcher needs an existing Node22+ and emits MCP messages on stdout. Diagnostics belong on stderr. Refresh the client after installation.

## Standalone local stdio route

Use this alternative if your client supports local stdio MCP but you are not installing the plugin. Do not register a duplicate server when the plugin already supplies it.

```sh
codex mcp add android-atlas -- node /absolute/av3x-antartica/plugins/android/scripts/mcp-server.mjs --stdio
codex mcp list --json
```

For another compatible client, adapt its configuration using command `node`, arguments `[/absolute/…/scripts/mcp-server.mjs, --stdio]` and your installed Node executable when PATH is unavailable. This server is local stdio, not an HTTP endpoint. API keys are not required for the atlas. The client/model service may have its own requirements.

## Practical tool arguments

| Tool | Arguments | Use |
| --- | --- | --- |
| android_status | `{}` | Version, integrity and actual scope |
| android_toc | `{"offset":0,"limit":5}` | Small index page |
| android_search | `{"query":"GPU","limit":5}` | Literal/alias search, not a web search |
| android_lookup | `{"id":"gpu-frame-engine-lab","limit":1800}` | Exact topic plus provenance |
| android_compare | `{"query":"lifecycle","limit":2}` | Evidence by platform, without inventing a winner |
| android_development_plan | `{"task":"Compose screen with offline saved state"}` | Development routing |
| android_inspect_project | `{"directory":"/absolute/my-app"}` | Bounded local configuration inspection |
| android_sdk_doctor | `{"sdkDirectory":"/absolute/android-sdk"}` | Read-only inventory |
| android_acceptance_plan | `{"task":"Accessible offline screen"}` | Observable test plan |
| android_rea_plan | `{"artifact":"/absolute/app.apk"}` | Analysis plan, no decompilation |
| android_frame_stats | `{"file":"/absolute/recorded-frames.txt"}` | Parse selected capture |

`android_v3_check` and `android_v3x_guard` require structured contract/snapshot data. Read their discovered tool schemas and the bundled V3/V3X references; do not invent missing telemetry. Passing a contract is arithmetic on supplied data. Guard candidates are rejected or quarantined; no action executes.

## Verify a fresh server

From repository root, run `node scripts/smoke.mjs`. It starts the actual packaged stdio server, initializes MCP, lists all13tools, retrieves status and an article, and checks a negative lookup. Exit0 means those bounded checks passed. `node scripts/check.mjs` adds regression and manifest checks.

## Troubleshoot by observation

- No tools: check Node22+, manifest path, client enablement and a fresh session. Compare direct smoke output with the client. Do not assume an already-open conversation rediscovered a new version.
- Hash mismatch: stop and review changed article/catalog bytes. Building accepts a new reviewed snapshot; do not rebuild untrusted content merely to silence an error.
- Empty search: try the mechanism/stable ID and note the gap. No live web search is performed.
- Inspector failure: give an explicit absolute directory/file; symlinks and size limits can cause intentional skips. These are heuristic inspectors, not a full build model.
- Build/device failure: the MCP tools never launched Gradle/ADB. Diagnose the actual executor and exact selected test target separately.

Official references: [OpenAI plugin packaging](https://developers.openai.com/plugins/build/plugins), [MCP architecture](https://modelcontextprotocol.io/docs/learn/architecture). Configuration surfaces evolve; verify your client version.
