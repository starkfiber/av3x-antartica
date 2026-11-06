# Android performance: observe, isolate, improve and remeasure

Official guidance checked 2026-11-06. Use this workflow for app performance work. Documentation, source inspection, synthetic models and device measurements are different evidence levels. Do not convert one into another or promise that an optimization improves every device.

## Establish a reproducible symptom

Describe the trigger and observable consequence: slow cold launch, a hitch during a particular scroll, delayed input, excessive allocations or a background task draining battery. Record app revision/variant, OS build, hardware, refresh rate, compilation state, dataset, network state and thermal/power conditions when available. Define the metric and acceptable change before optimizing. Average FPS alone cannot establish good pacing or low input latency.

First inspect relevant existing traces and benchmarks. If none exist, implement reproducible benchmark scenarios in the project when justified, but do not run instrumentation against an arbitrary connected device. Require the authorized target identity. A local unit benchmark, emulator result and physical-device result must be labeled separately. No production-display operations or OS policy changes are part of this workflow.

## Separate the kinds of delay

A thread can be running, runnable but not scheduled, or blocked on a dependency. Low total CPU utilization does not exclude a lock, Binder or I/O delay. High utilization does not prove waste. Follow the critical path from user action through UI work, render submission, GPU completion and presentation; annotate which stages the chosen tools actually expose.

Use traces to distinguish main-thread execution, waits, allocation/GC, recomposition/layout/drawing and downstream GPU work. Android's rendering guidance describes diagnostic tools and frame problems; its nominal refresh budgets are not guaranteed uninterrupted application CPU allocations. A trace correlation is a hypothesis until a controlled change explains it. [Rendering diagnosis](https://developer.android.com/topic/performance/issues/render), [system tracing](https://developer.android.com/topic/performance/tracing).

## Optimize Compose only where work is demonstrated

Review state ownership and invalidation before rewriting components. Use stable item keys for identity in lazy lists; keys are not a universal speed switch. Avoid repeated sorting or expensive calculations in composition when the result can be computed once for the relevant inputs. Use `remember` with correct keys for suitable cached calculations, not as persistent storage or a blanket wrapper around everything.

Read frequently changing state in the latest phase that can correctly handle it, where supported; moving a drawing-only update out of composition can reduce work. Use `derivedStateOf` when it reduces unnecessary updates from more frequently changing inputs, not merely because a value is derived. Keep side effects in appropriate effect APIs; do not write state backwards during composition. Inspect compiler/stability evidence before adding stability annotations—an incorrect immutability promise can hide necessary updates. Prefer a correct UI over a superficially reduced recomposition count. [Compose performance practices](https://developer.android.com/develop/ui/compose/performance/bestpractices).

For images and data, bound decoded sizes and loading concurrency, release lifecycle-owned resources and avoid retaining activities or large objects accidentally. A cache trades memory for recomputation/network cost; measure both. Do not suppress memory-pressure symptoms by globally increasing limits or disabling protections.

## Benchmark startup and user journeys appropriately

Macrobenchmark exercises larger application interactions such as launch and scrolling; use a supported release-like/profileable configuration as required by the current library guidance. Debug builds and debugger attachment can materially distort timing. Pin the benchmark library and document its SDK/toolchain requirements. Separate cold/warm/hot startup and compilation modes instead of merging incomparable samples. Save raw results and trace artifacts, repeat runs and inspect outliers before claiming improvement. [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview).

Microbenchmarks answer narrower code-level questions and do not establish end-to-end frame latency. Before changing architecture, check that an isolated faster function lies on the actual critical path. An improvement smaller than run-to-run variation is inconclusive. Do not remove assertions, error handling or security checks just to improve a number.

Baseline Profiles identify important code paths for ART optimization. Generate profiles from representative critical journeys and verify their integration into the intended build variant. Compare controlled compilation states; a generated profile file alone is not evidence of faster startup or coverage of all code. Generation and benchmark execution are distinct outputs—even a generation task that runs benchmarks may discard their results. Profiles require toolchain/OS support and ongoing maintenance as code changes. [Baseline Profiles](https://developer.android.com/topic/performance/baselineprofiles/overview), [profile generation](https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile).

## Keep GPU throughput, pacing and latency separate

For native game rendering, investigate whether the engine already integrates Android Frame Pacing before adding a second timing mechanism. Swappy uses presentation timing and fences to address queue stuffing; integration must match the graphics API and existing render loop. It cannot create GPU capacity, remove all vendor variability or guarantee deadlines. Choreographer ticks alone are not proof of correct presentation pacing. [Android Frame Pacing](https://developer.android.com/games/sdk/frame-pacing).

At 60 Hz the nominal interval is about 16.67 ms and at 120 Hz about 8.33 ms. Those are arithmetic intervals, not benchmark results. CPU/GPU pipeline overlap means throughput is not simply the sum of every stage duration; queueing can keep throughput high while visible state gets older. Distinguish application event-to-present timing from true physical input-to-photon measurement. Report dropped/repeated frames and latency distributions, not only an average FPS counter.

## Validate the improvement and its tradeoffs

Change one relevant factor or explicitly account for combined changes. Re-run the same representative scenario under comparable conditions and retain before/after artifacts. Check correctness, accessibility, memory, battery implications and startup/background transitions affected by the change. Reducing refresh rate may improve power and consistency while changing interaction feel; preloading can improve one launch path while increasing memory or startup work elsewhere. State the tradeoff instead of declaring a universal win.

A useful result is: “On this named build/device, this controlled journey improved this measured distribution; these checks passed; these conditions remain untested.” An equally useful honest result is that the hypothesis was not supported. Never label an unexecuted benchmark, synthetic timeline or source-only reading as a reproduced performance defect, security vulnerability or measured platform comparison.

## AV3X Antartica runtime protocol

Read ANTARTICA.md at plugin root. On Apple silicon verify the chip and hypervisor, use an ARM64 guest and record actual graphics backend from emulator logs. `auto` can choose software rendering; a hardware-capable CPU does not prove hardware GPU use. APK-to-macOS native execution requires more than ISA matching.

Retain baseline/candidate APK/source hashes and balanced run order, with equal variant, content, viewport and animation settings. Keep startup and interaction frame metrics separate. Parse framestats by named headers, but validate clocks and field plausibility rather than trusting every printed value. Do not turn `1000/frame-duration` or sporadic UI redraw counts into physical FPS. Debug/emulator results are not device/release performance guarantees.

The initial Antartica experiment used AppleM5, an owned API35 ARM64 AVD and the same baseline/candidate apps under software and host-GPU backends, followed by a software return control. The app patch did not show a reliable speed gain; backend configuration was the much larger observed factor. Read the dated runtime findings atlas entry and original raw case before reusing numeric claims. Physical FPS, power, battery life and input-to-photon latency were not measured.
