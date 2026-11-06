# V3 research design: bounded AI-assisted performance control on ARM

Dated 2026-11-06. “2030” is a planning horizon, not a forecast, release date or description of existing hardware. This is an implementable research proposal, not a completed OS, proven new scheduling algorithm, fastest-system claim or measured improvement. No ARM device, firmware or scheduler was modified. The proposal builds on established scheduling, feedback control and learned-policy work.

## Existing foundations and the proposed contribution

Linux Energy Aware Scheduling already uses a CPU energy model to inform placement on supported heterogeneous systems. Its documented topology/configuration requirements matter: an ARM instruction set alone does not establish that EAS is active or that a model fits the device. Linux deadline scheduling already has runtime/deadline/period parameters and admission constraints; these are not synonymous with ordinary application priority. [EAS](https://docs.kernel.org/scheduler/sched-energy.html), [deadline scheduling](https://docs.kernel.org/scheduler/sched-deadline.html).

Linux's thermal power allocator already implements a PID controller and a sustainable-power term. Android ADPF already lets performance-intensive applications provide hints and adapt workload to thermal conditions. A learned controller must compete with these competent baselines, not with a deliberately untuned system. It must leave underlying thermal and power protection intact. [Thermal power allocator](https://docs.kernel.org/driver-api/thermal/power_allocator.html), [ADPF](https://developer.android.com/games/optimize/adpf).

Decima is prior research on learned scheduling for distributed data-processing DAGs. Its workload and evaluation are a cluster setting; its reported results cannot be transferred to mobile UI deadlines. The relevant lesson is that a dependency-aware policy can be learned for a defined workload, not that an LLM should choose each kernel context switch. [Decima, Mao et al., 2019 version](https://arxiv.org/abs/1810.01963v4).

**Proposed synthesis:** use a small, bounded, user-space predictor to estimate near-term application workload and select among prevalidated application policies. A deterministic supervisor enforces legal actions, cost limits and rollback. Kernel scheduling and thermal enforcement remain responsible for their existing mechanisms. An optional offline AI assistant helps analyze traces and propose candidate policies; no remote language model participates in a frame's critical path. This is a design choice, not a novelty claim.

## Scope, observations and actions

First prototype: one instrumented native ARM application or engine on an explicitly selected test platform. Observe timestamped frame stages, completed work, queue depth if exposed, supported thermal status/headroom, current quality setting and model age. Keep sensor provenance and missing-value flags. Thermal headroom is an API-specific forecast/normalized signal, not degrees Celsius; unsupported or stale readings must not be converted into invented temperatures. [Android Thermal API](https://developer.android.com/games/optimize/adpf/thermal).

Actions are a finite set: retain policy; choose a supported rendering cadence; change a bounded rendering-quality parameter; defer explicitly optional application work; report truthful work durations/targets through a supported performance-hint API. Preserve a user-selected minimum quality/accessibility contract. Do not alter kernel frequencies, disable thermal trips, grant realtime privileges or assume an app can pin execution to a particular core. An OS-integrated research extension would require a separate privilege, admission and fairness design.

Run prediction on a bounded worker, not synchronously in the UI loop. Candidate initial cadence is one decision per 100 ms; this is a tunable experimental parameter, not a recommended universal setting. Detect catastrophic overload using a simple deterministic fast path. Rate-limit policy changes and use separate entry/exit thresholds to prevent oscillation. Missing telemetry, stale model, deadline overrun or unsupported API selects a known baseline policy immediately.

## Equations with units and assumptions

### Deadline and dependency accounting

For frame i, release r_i and required presentation time d_i are monotonic-clock timestamps in seconds. Its budget is B_i = d_i - r_i. Measure or conservatively estimate the critical dependency path rather than adding parallel work:

    finish(v) = max(release(v), max finish(predecessors(v)))
                + queue_wait(v) + service_time(v)
    L_i = finish(present_i) - r_i
    miss_i = 1[L_i > B_i]

This recurrence assumes an acyclic dependency graph and that resource waiting is represented once. Real display-latch/scanout delays belong in the graph when claiming physical latency. It is not a scheduler simulator without resource-contention rules. A render-submission timestamp alone cannot supply finish(present_i).

For candidate action a, predict a high quantile of critical-path latency, including measurement, policy and transition overhead:

    S_i(a) = B_i - Q_(1-epsilon)[L_i | x_i,a] - margin_i
    admissible_latency(a) iff S_i(a) >= 0

Use calibrated end-to-end samples where possible. Summing per-stage 95th percentiles does not produce an end-to-end 95th percentile. Confidence under a training distribution is not a hard worst-case bound. The guard rejects uncertain predictions; it cannot make unseen failures impossible.

### Queue stability and freshness

For one work-conserving FIFO stage, workload backlog W_i in seconds follows Lindley's recurrence:

    W_(i+1) = max(0, W_i + service_i - interarrival_i)

The ideal recurrence excludes finite buffers and backpressure; those must be added to the prototype's model. Under stationary assumptions a necessary load condition is rho = lambda * E[service] < 1. For several serial stages, check each bottleneck, including controller work on shared CPUs. A low average rho does not guarantee short tails or deadlines.

For stable long-run observations, Little's law is N = lambda * L with consistent system boundaries. It is a diagnostic relation, not a command to increase buffers. Track frame age at presentation separately from throughput. If freshness is the goal, dropping obsolete optional render work may be preferable to an ever-growing FIFO; preserve simulation correctness and never discard user transactions as if they were frames.

### Energy and thermal constraints

Measure total energy over the evaluation interval:

    E_total = integral P_device(t) dt
    E_per_completed_task = E_total / completed_tasks

Use joules and seconds consistently. Count CPU/GPU/NPU/model work, telemetry and policy transitions, and disclose whether power measures the entire device or a subsystem. Do not infer total device energy from CPU utilization alone.

For planning only, a first-order thermal model is:

    T_(k+1) = T_ambient + a*(T_k-T_ambient)
              + R_th*(1-a)*P_k
    a = exp(-delta_t/(R_th*C_th))

Here temperature is in kelvin or consistently Celsius differences, R_th is K/W, C_th is J/K, and P_k is watts. Fit parameters to a known sensor/location; multiple thermal zones and changing ambient conditions can invalidate a single-node model. Enforce predicted temperature <= approved limit minus uncertainty margin only where actual temperature telemetry exists. Otherwise use the supported thermal API's documented semantics. Firmware/kernel protection always overrides the controller.

### Controller overhead and selection

Let h be the decision interval and c_ctl measured CPU execution time per decision. Its approximate CPU-time share on one core is c_ctl/h, not a guarantee of negligible wall-clock latency. Bound both worker execution and end-to-end decision delay, reject late results, and cap retained telemetry/model memory. NPU offload is not free: include transfer, scheduling, shared bandwidth and energy.

A dimensionless candidate objective is:

    J(a) = w_m*predicted_miss_rate
         + w_l*predicted_tail_latency / latency_reference
         + w_e*predicted_energy / energy_reference
         + w_q*quality_loss(a)
         + w_s*policy_switch_cost(a)

Choose the lowest J only among actions satisfying legal capability, quality, thermal, resource and uncertainty guards. Weights are explicit user/product tradeoffs; changing them changes the target. If no candidate is feasible, use a deterministic overload policy: reduce optional load within allowed bounds, report degraded service and preserve essential work. Never conceal infeasibility by redefining the deadline after observing the outcome.

A simple break-even check is net latency saving = baseline latency - controlled latency, with inference and transition costs already inside controlled latency. Positive predicted savings alone are insufficient. Measure confidence intervals, energy and quality too. Optimizing one fraction of work cannot arbitrarily accelerate the whole application: Amdahl's bound 1/((1-p)+p/s) applies to a fixed serial decomposition with fraction p accelerated by s; queueing and overlap require the richer model above.

## Implementable development sequence

1. Build a deterministic baseline policy and timestamp schema. Define workload versions, clock domain, supported APIs and fallback behavior. Use bounded ring buffers; collect performance metadata without application content.
2. Capture authorized traces later and fit a compact predictor offline. Split training/evaluation by session and workload, and hold out device configurations to expose transfer failure. Do not randomly split adjacent frames and call that independent generalization.
3. Run shadow mode: predict actions but do not apply them. Measure calibration, overhead and rejected/missing observations. Shadow traces cannot establish the causal performance of unexecuted actions.
4. Apply a small finite action set with deterministic guards. Use prevalidated transitions, cooldown and a watchdog. No online exploratory action outside the set. Record model hash, features, action, guard decision and actual outcome.
5. Compare against fixed policies, thermal-aware heuristics and existing ADPF integration. Only expand the controller after it improves a declared workload without violating constraints. Keep a feature flag and immediate baseline fallback; no self-modifying kernel or automatic policy deployment.

## Three falsification experiments on ARM

**A — the AI tax.** Compare baseline, heuristic and learned controller on the same repeated workload with matched quality and platform settings. Include model/telemetry costs. Prediction fails if any apparent gain vanishes after overhead or falls within run variance; reject a claimed improvement that buys speed by silently reducing quality. Measure p50/p95/p99 frame latency, misses, energy and controller time.

**B — sustained heat and distribution shift.** Run long sessions across at least two explicitly identified ARM configurations with documented cooling conditions; vary workload phases and use held-out traces. Keep vendor protections active. Reject cross-device benefit if calibration degrades, thermal/quality constraints are breached, or the controller oscillates versus the heuristic. An early cold-device boost is not sustained speed.

**C — failure injection and contention.** In a controlled app test harness, inject stale/missing telemetry, delayed inference, phase changes and competing authorized CPU/GPU work. Verify a late policy never overrides a newer one, memory stays bounded and fallback activates within its declared interval. Measure the impact on concurrent essential workloads; an app-local improvement that starves them fails the broader OS proposal. Passing finite tests supports those conditions only, not a hard realtime proof.

No experiment has run. No speed percentage is defensible yet. ARM specifies an architecture family, not one energy model, cache hierarchy, GPU, scheduler or thermal envelope. A successful prototype would support a bounded application-policy claim; a 2030 OS-wide claim would require much broader workloads, fairness, reliability and hardware validation.

## Evidence gaps and acquisition note

The initially attempted `/games/optimize/adpf/performance-hint-api` URL returned 404. This report relies on the verified ADPF overview and Thermal API pages rather than inventing that missing page's contents. All equations labeled proposed/model are this design's explicit synthesis or standard mathematical relations, not recovered Android/Huawei/Apple implementation. The research citation is prior work, not a claimed reproduction.

## Peer challenge

The iOS reviewer challenged the undefined “fastest ARM by2030” comparator and highlighted inference overhead, oscillation, misprediction and tail deadlines. This design accepts those objections: its strongest defensible scope is a bounded application-policy controller with measured overhead, explicit fallback and matched quality/energy comparisons. OS-wide speed and fairness remain unproven. This records an actual peer exchange, not a completed experiment.
