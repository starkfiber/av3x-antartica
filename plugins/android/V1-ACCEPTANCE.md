Historical milestone document. For current public scope, read [STATUS](docs/STATUS.md).

# v1 acceptance contract

Current successor preview:3.0.0-preview.1. Original v1 gates are retained and updated below; stable acceptance remains gated by the rendered-reader check. A version is not a whole-OS completion percentage.

| Check | State | Evidence and boundary |
| --- | --- | --- |
| Published coverage and gaps | Passed | 39 dated topics with per-entry platform, evidence, version and limitations; broader claims remain outside coverage |
| Article provenance audit | Passed for declared audit | All41 baseline article bodies and source metadata read; two errata corrected. Review checks evidence scope/consistency, not independent truth of every upstream assertion. Two additional V3 research entries are explicitly proposals |
| Retrieval | Passed for bounded suite | Synonyms, ambiguous/missing queries, pagination and exact-section tests; retrieval quality can improve with usage |
| Navigation | Passed statically | Heading IDs, fenced code, internal paths and repeated-heading collisions checked |
| Reader visual/accessibility acceptance | Partial | Nine actual-reader DOM checks passed after skip-link, hierarchy and focus fixes. Rendered layout, native keyboard traversal and assistive technology remain unverified; DOM tests do not replace them |
| Fresh Codex skill/MCP discovery | Passed in fresh CLI session | Initial startup failure reproduced, explicit source-launcher registration applied, then real android_status and android_development_plan calls succeeded after skill read. Desktop UI refresh remains unobserved |
| Update and rollback | Passed locally | Isolated finding added/rebuilt/retrieved, invalid candidate rejected without writing, original snapshot restored |
| Clean-location portability | Passed locally | Copied plugin reads packaged corpus without original evidence files; symlinked launch path corrected |
| Independent scenarios | Passed for bounded suite | Android/Huawei/iOS source/evidence lookups exercised by independent reviewer; no runtime platform validation |

Stable release still requires rendered-reader layout/keyboard/accessibility checks. Prior fresh-session and scoped article-audit gaps are now closed with retained receipts. The session's local-file browser preview restriction was not bypassed. Do not erase those gaps by changing the version string. Prior0.1.0 payload and research evidence remain retained. Review the V2 debate as proposed research, not newly discovered platform vulnerabilities.
