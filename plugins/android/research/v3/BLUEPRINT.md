# V3: the response-age controller

Status: research specification and synthetic model, not an implemented OS controller. Planning horizon2030 is aspirational. This builds on existing scheduling, performance-hint, control-theory and learned-policy work; novelty and superiority are unproved.

## The one central idea

Optimize the age of useful state reaching the user, under correctness, quality, power, thermal and fairness constraints. Average FPS and CPU utilization are supporting signals. A predictor works outside the critical path; a deterministic supervisor selects among a finite, measured set of legal app actions. Unsupported or uncertain input immediately falls back to the baseline. No language model chooses every kernel context switch.

## Equations that decide whether an idea can help

- Frame interval: T = 1000 / refresh_Hz milliseconds (16.67ms at60Hz;8.33ms at120Hz).
- Critical-path latency: L = finish(presentation) − release(input/state). Include waits and synchronization once, not a sum of parallel lane times.
- Deadline slack: S(a) = budget − predicted_tail_latency(a) − transition_cost(a) − safety_margin. Only consider actions with nonnegative slack and valid telemetry. A predicted quantile is not a worst-case guarantee.
- Amdahl: speedup = 1 / ((1−p)+p/k). If40% of fixed work becomes2× faster, total speedup is1.25× before added overhead.
- Memory bound: operations/s ≤ min(compute ceiling, memory bandwidth × operations/byte). Improving CPU scheduling does not remove a memory bandwidth limit.
- Energy per useful task: E = integral(power(t) dt). Less time at high power may help, but raising frequency or model overhead may erase the gain.
- Net benefit: latency saved must exceed sensing + inference + policy transition + any induced contention. Measure all terms.

## Experimental contract

Track A holds output quality, required work and target cadence fixed. Compare platform default, a simple hand-tuned deterministic controller, predictor-only ablation and bounded learned advisor. Measure p50/p95/p99 response age, deadline misses, energy/task, thermal behavior and background progress on the same named device/build. Count drops, correctness failures and model fallbacks explicitly.

Track B permits user-approved quality/cadence adaptation and reports the quality-energy-latency Pareto frontier. It is not an equal-quality speed comparison.

Initial exploratory goal: improve p95 age by10% while keeping p99, energy and background-progress regressions within preregistered margins established during pilot measurement. This is a proposed target, not a result or universal threshold. Uncertainty and measurement resolution must be smaller than any claimed benefit. A result that fails these conditions rejects the policy for that workload.

## Prototype progression

1. Instrument one app's CPU work/waits, GPU completion and presentation in supported tracing tools.
2. Calibrate clock domains and overhead; do not call app timestamps physical input-to-photon latency.
3. Reproduce the synthetic queue-age experiment with bounded real buffers and controlled refresh behavior.
4. Fit an offline predictor and test unseen traces; retain deterministic policy as the baseline.
5. Add a bounded worker advisor with hysteresis, action limits, model-age checks and immediate fallback.
6. Only after app-level evidence, separately scope privileged research into OS-wide admission, fairness and resource control. This plugin does not grant those privileges or modify the OS.

A future OS could benefit from better dependency information, workload contracts and hardware/software cooperation. Algorithms are useful when they remove measured work/waits or make bounded decisions; equations and AI branding alone do not increase ARM throughput.

## Retained work

model.mjs compares FIFO and latest-ready presentation after an artificial display pause. Latest-ready deliberately drops old completed frames. The test verifies conservation, completion ordering and the freshness/drop tradeoff. Unlimited buffers, fixed stage times and no scanout make it a teaching model, not a mobile scheduler benchmark.

The primary-source technical positions and actual reviewer exchange are v3-scheduling.md and v3-challenge.md in this directory. Their strongest unresolved concerns are equal-quality comparisons, tail uncertainty, fairness, fallback overhead and policy generalization.
