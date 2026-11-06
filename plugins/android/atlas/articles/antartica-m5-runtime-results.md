# AV3X Antartica — first real M5 emulator experiment

Client date6November2026. Host verified AppleM5, macOS26.5.1. User codename Antartica (spelling preserved), major version3. This is an app/emulator experiment, not a new OS or a native-macOS APK runtime.

## Target and environment

Owned AVD AV3X_Antartica_QA, exact serial emulator-5580, ADB server port5041. Name and ro.kernel.qemu=1 checked before mutations. Another emulator was discovered by the dedicated server; it was never selected or operated. No physical phone/showroom target was selected. Existing AVDs were not used.

Shared emulator37.1.11 failed to launch due to missingQtWebEngine dylib. SDK Manager installed a separate emulator37.2.13 build16740729 under case://local/av3x-antartica-runtime, reusing existing license records. Existing API35 google_apis arm64-v8a image used; the default image was incomplete. Hypervisor.Framework acceleration check succeeded. Guest Android15/API35 buildAE3A.240806.043/12960925; app min26/target36.4vCPUs,2GiB RAM,1080x2400,420dpi,60Hz. Window/transition animation scales1.0; animator scale absent/default, not changed.

`-gpu auto` actually selected lavapipe/swangle/SwiftShader. `-gpu host` selected AppleM5 Vulkan/OpenGLES translator. All phases used ARM64+HVF; GPU mode was an independent factor. APK code did not become a macOS process or gain macOS framework compatibility.

## Artifacts and workload

A: original debugAPK SHA25652ef715a78f1cc86d079bffcbc67cc144ca7710a2bcf8d4763bc2d57a5562f92.
B: candidate debugAPK SHA2566432e19b7741223099c5261765912771dc74fdbc488e1e11db180808717fe5f1.
Same package dev.atlas.demo, toolchain, five lessons, controls, output quality and app behavior scope. Candidate adds cached search work, consumed insets, per-tab scroll state, expansion semantics and static row keys. The scroll workload does not separately establish search-performance benefits.

Each main phase used ABBA BAAB,4runs per variant,2warmup starts/run,3measured process-cold starts/run,2reset+frame batches/run,2fixed250ms swipes/batch with700ms pauses. Software return used two A runs with identical protocol.54measured cold starts and36frame batches total. Raw requests, screenshots, startup outputs and dumps retained per run. No animation disabling, resolution lowering or selective favorable-run removal.

## Observed baseline A across renderer phases

| Metric | Initial software | M5 host GPU | Return software |
| --- | ---: | ---: | ---: |
| Process-cold startup median |1258ms|518ms|1135ms|
| Completion-duration p95 |91.554ms|18.863ms|78.222ms|
| Android-reported jank |182/586(31.06%)|36/683(5.27%)|72/314(22.93%)|
| Measured cold starts |12|12|6|
| Accepted interaction batches |8|8|4|

The return control slowed again, weakening a simple monotonic warm-up explanation. This supports a large practical backend-phase effect for this app/AVD/M5. Phases were sequential with restarts, not randomized/thermally calibrated; no universal causal effect size is certified. Energy, battery, physical display FPS and input-to-photon latency were not measured.

## App patch: no reliable speed gain

Software A/B startup medians1258/1211ms; completion p95 91.554/95.828ms; jank31.06/28.69%. Paired run startup medians B−A:+36,+3,−11,+11ms. Host A/B startup518/514ms; completion p95 18.863/19.913ms; jank5.27/5.04%. Host paired startup deltas−46,+4,−17,+3ms. Mixed directions and small run count do not support a reliable code-level speedup. Keep the quality/state changes documented; do not promote a performance claim from them.

## Metrics and anomalies

Primary frame metric is FrameCompleted−IntendedVsync for valid Flags0 rows on the single app window. Conventional midpoint median; nearest-rank p95. Aggregate jank keeps the platform's own tracked-frame denominator. Individual frames are not independent experimental runs.

Guest dump headers show FrameInterval values resembling timestamps and FrameStartTime resembling intervals. No column swapping was performed. DisplayPresentTime and GPU values sometimes appear invalid. One host A row had FrameCompleted35,188,667,075,888ns while printed guest uptime was66,278,000,000ns: excluded as outside capture-clock envelope, not as a slow-frame cutoff. The disclosed envelope uses header uptime+1second for collection lag and assumes aligned guest monotonic domains. All raw rows retained; A684raw→683valid, B694→694. Other frame batches had no such exclusion. These anomalies prevent physical FPS or GPU-only timing claims.

## Actual UI and cleanup

PNG screenshots are real owned-emulator captures. Lab tab navigation succeeded and UI hierarchy contains 'Explore a frame.' This is one runtime interaction/visual check, not comprehensive accessibility, restoration or responsive qualification. The Lab's CPU/GPU sliders remain illustrative arithmetic and are not benchmarks.

All three owned emulator processes exited after scoped name verification and emu kill. The user-owned AVD/runtime files remain for reuse. Port5041 ADB service remains; it also discovered a separate existing emulator, so it was not globally stopped. No other ADB server/device was reset. Host pmset reported no recorded thermal/performance warning; no power/thermal telemetry was calibrated. Do not infer environmental savings.

## Plugin follow-through

Findings become an atlas topic and benchmark reference. New read-only android_frame_stats accepts an explicit bounded8MiB single-window dump, validates clock/format limits, reports exclusions and refuses an FPS interpretation. Synthetic parser tests and raw-data parity review support this helper, not every Android format. The full native app/demo build and runtime observations do not close the separate web-reader rendered/assistive gate.
