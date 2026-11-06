# V3X: local red-team findings and bounded defenses

V3X is a plugin hardening preview. It does not modify LLM weights, learn an OS controller, execute proposals or claim resistance to every unknown attack.

## Reproduced findings

| Finding | Result | Fix |
| --- | --- | --- |
| Search inherited object names | constructor/__proto__ crash runtime/reader alias handling | Own-property lookup; literal fallback |
| Invalid SDK selector | false/0/empty silently select a default | Presence-based selection with strict directory validation |
| Decimal boundary arithmetic | 0.1+0.2 falsely exceeds0.3 in deadline model | Nearest-nanosecond integer ticks, safe ranges/aggregates and raw reversed-clock checks |
| Links in code examples | Fenced/inline code could become active hyperlinks | Inert pre/code blocks and code-aware inline parser |
| Parenthesized Markdown URLs | URL truncated at first closing parenthesis | Balanced-parenthesis parsing |
| Metadata/direct API validation gaps | Unsafe source schemes accepted; inherited fields accepted by direct JS validator | HTTP(S), no URL credentials; required own fields. No successful XSS/plain-JSON inherited-field exploit was demonstrated |

Additional hardening bounds incoming line bytes before parsing, catalogs4MiB/articles1MiB/corpus32MiB/topics4096, and rejects explicit null tool arguments. These limits prevent the tested unbounded-input patterns; they are not a complete process-resource isolation mechanism. Shard larger corpora rather than silently removing limits.

## Guard and evolution boundary

android_v3x_guard accepts a strictly shaped proposal and snapshot. It checks target/snapshot/generation binding, age, deadline including overhead, thermal status, state freshness, required work and fairness. Latest-ready additionally needs explicit optional-loss permission. Any failure rejects to baseline. Even a passing proposal receives decision quarantine.

Quarantine is a workflow state, not an operating-system sandbox. No executor, policy mutation interface, model-weight access, self-approval or automatic release exists. The plugin cannot constrain arbitrary shell/other tools used outside its own interfaces. The underlying assistant/model and host still need their own permissions and isolation.

Policy SHA256 identifies the fixed policy bytes; it is not a signature or trust anchor. Caller assertions are not authenticated telemetry. A future actuator would need authentic target-bound observations, atomic generation recheck, admission/authorization, isolated execution, monitoring and rollback before application.

Evolution workflow: retain finding → reproduce failure → propose reviewed diff → run adversarial/regression checks → independent review → versioned hash-bound release → preserve rollback snapshot. Changes to knowledge/instructions/policy are explicit source updates, not changes to neural-network weights.

## Test evidence

Case epoch11 retains original failures, resolutions and tool versions.24Node test groups pass. Independent final timing suite12cases and guard18cases pass. Reader cases use jsdom and do not constitute rendered/assistive acceptance. Existing V1 reader visual gate remains open.

Owned demo APK unchanged: SHA25652ef715a78f1cc86d079bffcbc67cc144ca7710a2bcf8d4763bc2d57a5562f92. SDK apksigner verifies its v2 signature. Debug signer and exported Compose PreviewActivity are debug surfaces, not a demonstrated remote-code exploit or production-release qualification. ZIP checks did not find duplicate/traversal entries; a filename scan is not a full secret audit.

Injected script/scheme fixtures tested in the reader stayed inert. One attack article with a matching hash was still retrievable as untrusted data: hashes cannot establish semantic truth or stop prompt injection. No behavioral LLM prompt-injection immunity or zero-day coverage is claimed.
