# Foundations learning and engineering map

This is the project curriculum and experiment framework, not a claim that these layers have been implemented or fully reverse-engineered. Read PROJECT-DIRECTION.md at plugin root for the governing priorities.

## Six connected tracks

| Track | Core questions | Bounded proof exercise |
| --- | --- | --- |
| Machine code learning | What instruction bytes, registers, addressing and memory ordering mean for one selected ISA/version | Compile a tiny owned function; inspect exact bytes and conditional instruction behavior; distinguish decoding from execution |
| Assembly learning | Calling conventions, stack layout, linkage, exception entry and compiler transformations | Compare debug/optimized output and trace one call boundary with a stated ABI; record assumptions |
| Kernel learning | Privilege, interrupts, virtual memory, scheduling, synchronization, IPC, drivers and isolation | Follow one pinned source path and model its success/failure invariants; reserve runtime claims for a selected controlled target |
| OS learning | Boot, process/runtime models, storage, services, graphics, input, networking, updates and recovery | Map one end-to-end behavior and its dependencies, trust boundaries, state authority and failure states |
| Android learning | How ART, Binder, framework lifecycle, partitions, graphics and app contracts implement those concepts | Reuse retained source/APK evidence and build the native demo; identify where source-to-binary or runtime proof is missing |
| iOS learning | How public source, API contracts, manifests and selected firmware objects expose comparable responsibilities | Reproduce retained identity/digest findings; keep public XNU separate from unmatched shipped code and signature trust |

Machine code is executable instruction representation; it is not the same subject as machine learning. AI/model engineering is an additional cross-cutting discipline.

## Cross-cutting acceptance contracts

- **Correctness/security:** required output, invariant violations, trust boundaries, privilege, attacker/failure model and isolation evidence.
- **Stability:** bounded queues/retries, crash/fault containment, state restoration, concurrency correctness and recovery evidence.
- **Performance:** response-age and throughput distributions, dependency waits, missed deadlines and instrumentation overhead.
- **Efficiency/environment:** energy per successful task at matched output quality, memory peak/traffic, storage writes and thermal conditions. State the power measurement boundary.
- **Compatibility:** explicit ISA/ABI/API/runtime/service/graphics/version matrix. Test feature behavior and failure handling, not just package parsing.
- **Model accuracy:** task-specific loss, held-out devices/workloads, calibration, confidence/abstention, drift and fallback rate. A high aggregate accuracy can conceal dangerous tail errors.
- **AI readiness:** timestamped typed telemetry with provenance, bounded actions, deterministic guards, audit records and rollback. Do not put an unbounded remote inference call on a hard deadline path.

## First integrated experiment

Choose an owned app operation. Preserve required behavior and output quality. Trace its compiled entry point, framework boundary, kernel/service dependencies and presentation result where tools permit. Measure only what the environment actually exposes. Compare a simple baseline with one narrowly changed policy; retain failures and counterexamples. Evaluate energy and tail latency together rather than reporting a peak-only speedup.

Compatibility adaptation is allowed at different layers: recompilation against public APIs, a library facade, runtime translation or emulation. Each has different semantics, resource costs and security obligations. Choose the smallest adaptation that meets the explicit behavior contract; do not promise universal compatibility.

## Useful existing plugin material

Use the atlas index for boot, ART, Binder, memory, graphics, AVB and firmware lessons. Use development/performance references for ordinary SDK work; core-apps/recovery references for authority, freshness and failure boundaries. Use research/v3 contracts for checked arithmetic/invariants on supplied data. Those validators do not execute a kernel or verify telemetry truth.
