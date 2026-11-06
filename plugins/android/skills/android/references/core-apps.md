# Core-app responsiveness and authoritative state

Primary sources checked 2026-11-06. Use this reference when investigating SystemUI, Launcher, Settings or a similarly important application. System privilege does not guarantee zero lag. Diagnose a concrete operation and its dependencies; do not disable protections or change OS priorities to manufacture responsiveness.

## Trace state before tuning speed

Identify the authoritative service/provider and distinguish four events: request accepted, state committed, observer refreshed and frame presented. A cached UI value may be legitimate but is not necessarily current. Track user/profile identity and request generation alongside asynchronous results. Define permissible staleness for the operation: a decorative preview and an access-control decision require different treatment.

A pinned AOSP example is `UserSettingObserver.java` at frameworks/base commit `94b4c163b7dfe5ce3607f7bb8456f9573f7de57d`. Its `getValue()` uses cached state while listening, writes go through a settings proxy, and an observer re-reads and updates the cache. Registration includes a completion re-read; changing the user restarts listening. These are existing reconciliation mechanisms, not evidence of a reproduced race. This source is Android17 r1 context and is not certified to match a particular shipped QPR1/OEM build. [Pinned SystemUI helper](https://android.googlesource.com/platform/frameworks/base/+/94b4c163b7dfe5ce3607f7bb8456f9573f7de57d/packages/SystemUI/src/com/android/systemui/qs/UserSettingObserver.java).

For Settings changes, render pending/confirmed/rejected accurately. For Launcher, separate input response from launch dispatch and first useful application frame. For SystemUI, trace the specific controller/service and visible state. Do not infer full implementations from these responsibility descriptions or claim all three applications were reverse-engineered from one helper.

## Diagnose dependencies, not labels

Inspect main-thread work, waits, Binder dependency chains, resource contention and rendering timelines. An ANR is a detection category, not the threshold at which users first perceive lag. System-server Watchdog monitors selected progress paths; its presence does not certify UI frame deadlines. [ANR diagnosis](https://developer.android.com/topic/performance/anrs/diagnose-and-fix-anrs), [Watchdog reading pointer](https://android.googlesource.com/platform/frameworks/base/+/master/services/core/java/com/android/server/Watchdog.java).

Synchronous Binder work can block; nested calls can re-enter; one-way calls change completion semantics and can queue. Current documented realtime priority inheritance is opt-in per node. Priority inheritance mitigates some inversions but cannot solve a dependency cycle or create capacity. Treat kernel/domain configuration as build-specific evidence. [Binder threading](https://source.android.com/docs/core/architecture/ipc/binder-threading), [priority inheritance](https://source.android.com/docs/core/architecture/ipc/priority-inheritance).

## Design changes with observable contracts

Use the project's existing architecture. Introduce generation checks for obsolete responses, bounded/coalesced refresh work for replaceable snapshots, explicit invalidation/reconciliation and retry-safe operations where needed. Keep ordered transactions distinct from replaceable observations. Move blocking operations off the UI critical path when semantics permit and represent pending state honestly. Bound optional rendering/background work; preserve essential controls and accessibility.

Measure response age and recovery correctness separately from FPS. Evaluate whether a faster visible response merely hides delayed completion. Recover from interruption by reconciling durable intent with authoritative state, not blindly replaying side effects. Cancellation, timeout and unavailable data must have defined outcomes. An AI-assisted controller may select only prevalidated policies; it cannot invent authority, bypass permissions or guarantee arbitrary deadlines.

## Validation workflow

Start with owned fake-service/provider tests: delay acknowledgments, reorder responses, switch identities and simulate reconnect. Verify that old results cannot overwrite newer requests or cross profile boundaries. Test interrupted retries for duplicate side effects and bounded recovery. Add tracing only for the state transition under investigation; do not log secrets or user content.

Runtime instrumentation requires an explicitly identified authorized emulator/test device. Never select the first connected device or crash shared core services as a routine test. A synthetic failure validates the test scenario, not an AOSP vulnerability. Report source revision, observation level, latency distributions, correctness checks and untested hardware/build paths. Use “met these measured bounds under these conditions,” not “core apps never lag.”
