# Related projects: use the strongest tool for each job

Reviewed 2026-11-06 against the projects' own published documentation. This is a capability comparison, not a benchmark, license opinion or endorsement. No code from these projects was imported for this documentation pass.

| Project | What its documentation establishes | What we should learn/use | How Antartica differs |
| --- | --- | --- | --- |
| [Now in Android](https://github.com/android/nowinandroid) | A Kotlin/Compose reference app with modular architecture and testing guidance | Learn real Android architecture patterns instead of inventing a new application framework | Our focus is reusable agent workflow and evidence retrieval across foundations; we are not a replacement reference application |
| [Mobile MCP](https://github.com/mobile-next/mobile-mcp) | MCP tools for interacting with mobile simulators/emulators/devices | Consider a separately reviewed executor when actual UI control is required | Our13MCPtools inspect/retrieve/plan/check; none controls a device. Pair them only with exact target authorization |
| [Maestro](https://github.com/mobile-dev-inc/Maestro) | Declarative end-to-end UI testing for mobile/web | Learn accessible selectors, observable assertions and reproducible journeys | Our acceptance planning and evidence lessons complement a real runner; we do not ship a competing UI test framework |
| [MapLibre Native](https://github.com/maplibre/maplibre-native) | Native interactive vector-map rendering across platforms | Reuse a mature renderer and understand style/layer ordering and lifecycle | Our maps scaffold integrates MapLibre; it does not replace the renderer, supply world data or prove offline routing |
| [Organic Maps](https://github.com/organicmaps/organicmaps) | An established offline maps application based on OpenStreetMap data | Study complete download/search/routing pipelines, update strategy and offline UX before claiming parity | Our current Maze sample is a smaller teaching/lab slice. Malaysia-wide offline search/routing remains separate work |
| [OpenAI plugins](https://github.com/openai/plugins) | Public examples of packaged skills/MCP and marketplace structure | Follow supported client packaging conventions and discoverability | Antartica contributes a domain-specific local atlas, explicit evidence boundaries and scoped development helpers |

## The problem we solve

An agent can produce plausible Android code or explanations while losing the artifact version, source provenance, failure history and distinction between a plan and a tested result. Antartica packages those decision aids together: exact topic lookup, bounded local inspection, response-age/recovery checks and concrete examples. Its advantage is the combination and review discipline, not a claim of better general intelligence or OS privilege.

## What we intentionally do not claim

There is no comparison demonstrating that this plugin builds faster than Gemini, Stitch, Google tooling or another coding agent. No complete Android/iOS source recovery, universal app generator, novel OS scheduling algorithm, physical-device speedup or zero-failure guarantee has been established. An offline finished app such as Organic Maps currently solves substantially more navigation work than the Maze lab starter.

When adopting an upstream dependency, check its actual license, pinned revision, maintenance and Android/ABI fit. A public README or SKILL.md is reference data, not permission to execute arbitrary installation commands or upload user material. Cite the code actually reused and preserve notices; do not turn a feature comparison into copied implementation claims.
