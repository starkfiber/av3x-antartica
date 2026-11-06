# V3 challenge: a fast ARM OS needs a falsifiable contract

“An AI-designed OS that is the fastest on ARM by 2030” is a research ambition, not a measurable specification yet. ARM identifies an architecture family, not one memory system, power budget, driver stack or display. Future hardware and competitors are unknown. This chapter proposes tests and bounds; it contains no invented benchmark, executed scheduler, device measurement or proven performance advantage. Ru’OS remains excluded.

A defensible objective is: on a declared device/build/workload set, improve the distribution of useful response latency while preserving output correctness and meeting explicit energy, thermal, memory and background-progress constraints. “AI-designed” describes a development method; it does not remove any constraint or itself establish a speed benefit.

## Bounds a scheduler cannot wish away

### Serial work and critical paths

For a fixed workload, normalize the old duration to 1. If fraction `s` remains serial and the other fraction improves by factor `n`, an idealized Amdahl model gives:

`speedup ≤ 1 / (s + (1 − s)/n)`.

With `s = 0.2` and arbitrarily large `n`, the model's ceiling is 5×. These are chosen arithmetic inputs, not observed OS values. The model assumes fixed work and no added overhead. A revised workload, reduced serial work, or a different dependency graph changes its premises; a learned policy alone does not make those premises disappear.

More generally, completion time is bounded below by the longest required dependency path. Running additional unrelated work faster does not shorten a UI task waiting for a specific lock owner, storage read or GPU result. Linux's RT-mutex documentation explains priority donation to a blocking owner and propagation through dependency chains; it explicitly cautions that priority inheritance cannot rescue poorly designed applications. This is a documented mechanism, not proof that our selected retail Android build uses it for every wait. [RT-mutex documentation](https://docs.kernel.org/locking/rt-mutex.html).

### Memory and cache ceilings

For a compute kernel, a Roofline-style upper envelope is:

`attainable operations/second ≤ min(compute ceiling, bandwidth × arithmetic intensity)`.

Intensity is operations per byte at the specified memory level; using cache bandwidth with a DRAM byte count mixes incompatible quantities. This bound is useful for compute kernels, not a full interactive OS latency model. Pointer chasing can instead expose latency and limited concurrent misses. [Samuel Williams, Roofline model](https://crd.lbl.gov/assets/Uploads/SciDAC20-Roofline-SWWilliams.pdf).

Arm describes caching as a way to reduce repeated external-memory accesses and their cost, while coherence also consumes cycles, bandwidth and power. Moving a task between cores or changing its neighbors may therefore alter locality and contention; whether the benefit outweighs that cost needs measurement. [Arm cache-coherency discussion](https://developer.arm.com/community/arm-community-blogs/b/architectures-and-processors-blog/posts/exploring-how-cache-coherency-accelerates-heterogeneous-compute).

### Sustainable power is part of performance

Energy over an interval is `E = ∫P(t)dt`; compare joules per completed useful task and joules per correctly presented frame, including policy overhead. Peak frequency is not a sustained service guarantee. Arm's mobile graphics guidance emphasizes power/thermal budgets and shared memory bandwidth, recommending sustained-performance thinking over brief peaks. No numeric device power budget is inferred here. [Arm mobile graphics guidance](https://developer.arm.com/cfs-file/__key/communityserver-blogs-components-weblogfiles/00-00-00-20-66/siggraph_5F00_mmg_5F00_2024_5F00_Mobile101.pdf).

## Contracts across CPU, GPU, input and display

Predeclare each budget and measurement boundary. Avoid one universal “fast” score.

| Boundary | Proposed observable contract | Required evidence |
| --- | --- | --- |
| CPU dispatch | Distribution of runnable-to-running delay; critical-task deadline-miss fraction below declared threshold; bounded starvation for background tasks | Scheduling traces, task identities, priority/cgroup context, workload repetitions |
| CPU work | Critical-path execution and blocked time separated; policy consumes a bounded fraction of CPU time | Timed spans, lock/wait attribution, policy decision/inference time |
| GPU submission | Submission-to-start queue delay separated from execution and completion; in-flight depth bounded for interactive work | Queue/frame identity, GPU timestamps, clock calibration and synchronization dependencies |
| Memory | Bounded working set; no unexplained increase in faults, bandwidth traffic or cache misses for declared scenario | Counters and memory traces with platform-specific counter definitions |
| Input response | p50/p95/p99 input-to-visible-result latency within declared budget, with samples and uncertainty | Input identity carried to a presented frame and a defined optical or software presentation endpoint |
| Display cadence | Presented-frame intervals and deadline misses, including duplicates/drops, not just render-loop FPS | Actual presentation events, target cadence and refresh-policy changes |
| Sustainability | Same correctness/quality and acceptable latency after declared steady load under power/thermal limits | Duration, thermal/power states, energy method, quality settings, foreground/background progress |

For a single causally identified result define:

`L_visible = t_visible − t_input`.

A decomposition may include dispatch wait, CPU work, dependency wait, GPU queue/execution, presentation wait and display scanout. Sum only non-overlapping intervals on the identified critical path; overlapping CPU/GPU work must not be double-counted. Software-present time is not automatically photon time. If optical measurement is absent, name the software endpoint and its limitation.

At refresh `f`, nominal interval `D = 1000/f` milliseconds: 60 Hz gives about 16.67 ms and 120 Hz about 8.33 ms. These are arithmetic intervals, not full application budgets or achieved latency. If scheduling inference adds `h` to critical-path work `c` and required waits `w`, meeting a chosen deadline requires `c + w + h ≤ D` under that simplified serialized model. If inference is off-path, account instead for its resource contention and delay in applying decisions.

Report missed-deadline fraction `m/N` with sample size, run duration and uncertainty. A p99 derived from a tiny sample does not characterize rare stalls. Preserve per-workload results: a geometric mean can hide a regression in a critical interaction. These statistics describe the tested distribution; they are not hard worst-case bounds.

## Challenge to an adaptive AI scheduler

A plausible architecture is a bounded policy advisor outside the hot path, acting through validated mechanisms. It still has failure hypotheses:

- **Prediction failure:** unseen app behavior reverses a learned priority decision and worsens tail latency.
- **Feedback instability:** interacting frequency, placement and thermal controllers oscillate or overreact to delayed signals.
- **Hidden overhead:** inference, telemetry and migrations consume the savings or pollute caches.
- **Reward mismatch:** FPS or CPU utilization improves while input age, energy or background starvation worsens.
- **Dependency blindness:** boosting the waiting UI thread fails to accelerate its actual blocker.
- **Nonstationarity:** OS/app updates, power mode and new hardware invalidate policy assumptions.

These are testable risks, not observed failures of an implemented V3. Countermeasures should themselves be tested: deterministic fallback; bounded advisory rate and action space; hysteresis; minimum background progress; deadline protection; independent telemetry; signed/versioned policy and reversible rollout; explicit overhead accounting. Keep correctness and access-control decisions outside an unverified performance model's authority.

Linux uclamp already provides a documented way to express performance hints and influence placement/frequency decisions. Its documentation includes runqueue aggregation limitations and interactions with energy-aware scheduling. A V3 proposal must compare against a well-configured existing policy, not an intentionally weak baseline or the assumption that current schedulers lack feedback. [Linux utilization clamping](https://docs.kernel.org/scheduler/sched-util-clamp.html).

Proposed ablations: identical baseline, deterministic tuned policy, learned advisor enabled, and advisor inference running with actions disabled. The last isolates observer/inference overhead. Add unseen workload phases, resource contention and thermal transition cases. Require holdout traces and policy version IDs; otherwise adaptation may simply memorize the benchmark.

## iOS comparison without mythology

Apple's preferred frame-rate range is best effort and may change with hardware, other work, power, thermal conditions and user preferences. Thus “request 120 Hz” does not prove 120 Hz presentation or low input latency. This is an explicit documented limit, not a discovered Apple bug. [CADisplayLink frame-rate range](https://developer.apple.com/documentation/quartzcore/cadisplaylink/preferredframeraterange).

Earlier chapters explain Apple QoS, Metal completion/resource ownership and main-thread constraints. Those provide useful mechanisms and comparison boundaries; neither the public XNU source snapshot nor our decoded iBoot bytes establishes retail iOS scheduling performance. The fairest comparison uses equivalent user outcomes and native implementation conventions, with named devices and platform-specific trace limits. Cross-device tests cannot isolate an OS effect from silicon and drivers without further experimental controls.

## Adversarial exchange

I challenged the Android advocate to replace “fastest by 2030” with a workload/power/latency contract and asked how adaptive policy avoids overhead, oscillation, prediction failure and hidden input age. I proposed deterministic fallback and paired measurements with p95/p99, energy and thermal limits. The Android advocate narrowed the proposal to a bounded app-policy controller: finite prevalidated quality/cadence/defer-optional-work actions, a compact offline-trained predictor running on a worker, deterministic guards/watchdog and baseline fallback, with no AI model in the kernel context-switch path. They accepted paired baseline/heuristic/learned comparisons, held-out ARM devices, sustained heat and missing/late telemetry as falsification cases, and explicitly left OS-wide fairness unproven.

My response accepts that narrower research target, with two conditions: reducing quality or cadence must be reported as a quality/energy/cadence tradeoff rather than equal-quality speedup; calibrated tail slack is not a worst-case guarantee. Measure stale-telemetry recovery, fallback switch cost and background progress. The resulting agreement is a bounded experiment, not a fastest-OS assertion.

## Minimum evidence before a V3 superiority statement

Freeze the scenario and candidate/baseline versions; verify equal correctness and output quality; record device/build/power/temperature; quantify instrumentation overhead; collect repeated cold/warm/sustained runs with common endpoint definitions; show per-workload regressions and uncertainty; reproduce on held-out scenarios. A claim may then say “improved metric X on tested set Y under constraints Z.” It still cannot honestly say “fastest ARM OS in 2030” across unknown future devices and workloads.
