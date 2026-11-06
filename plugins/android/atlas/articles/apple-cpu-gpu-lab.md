# Apple scheduling, QoS and Metal

**Scope:** Public XNU f6217f891ac0bb64f3d375211650a4c1ff8ca1ea; Metal legacy and Metal4 documentation; unmatched to selected iOS27 kernel.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

This chapter explains the Apple side of the investigation using current Apple documentation and two pinned public XNU source files. **It contains no newly measured iOS CPU/GPU timings.** The earlier iBoot payload, digest and instruction-prefix work establishes facts about those firmware bytes; it does not establish Metal behavior, GPU performance, scheduler latency or application compatibility. Ru’OS remains excluded.

The useful organizing idea is a dependency chain: an app schedules CPU work, submits GPU work, coordinates memory access, and presents the result before a useful deadline. A game engine builds a larger system around that chain. Running a large app catalog additionally requires the operating-system contracts those apps expect.

## 1. CPU work: threads, queues and urgency

A CPU thread carries execution state. An application task or job is a unit of work; it need not own a permanent thread. A queue describes work and ordering, whereas the operating system decides when runnable threads get CPU time. More queued work does not create more cores or eliminate dependency waits.

Apple's QoS model lets software describe urgency: user-interactive work affects immediate interaction, user-initiated work produces a result the person is waiting for, utility work can make visible progress over time, and background work can be deferred. Apple documents effects on scheduling, CPU/I/O throughput and timer latency, with inference or promotion in some dependency cases. QoS expresses importance; it is not a reservation of a named performance core, a fixed CPU frequency or a promise that every frame deadline will be met. [Apple iOS QoS guide](https://developer.apple.com/library/archive/documentation/Performance/Conceptual/EnergyGuide-iOS/PrioritizeWorkWithQoS.html).

Consider a game frame with input handling, a physics step, asset decoding and a UI update. Treating all four as equally urgent hides the fact that input/UI and the required simulation state are on the next frame's critical path, while some future asset work can wait. This is an analytical example, not a measured engine implementation. The next investigation question is “which work blocks the next useful output?” before “how many threads can we create?”

## 2. What public XNU source actually shows

We retained `osfmk/mach/thread_policy.h` and `osfmk/kern/thread_policy.c` at commit **f6217f891ac0bb64f3d375211650a4c1ff8ca1ea**. This is a public-source snapshot, **not a matched source release for the inspected iOS 27 kernel**. No source was compiled or executed. Hashes are in [xnu-provenance.json](case://local/android-ios-reverse-lab/epoch-4/apple-sources/xnu-provenance.json).

**Anchor A — a real-time request has several parameters.** [thread_policy.h:120](case://local/android-ios-reverse-lab/epoch-4/apple-sources/thread_policy.h:120) describes period, computation and constraint in absolute time units. The constraint is at least the requested computation; the comment describes their difference as latency. These units must not be relabeled “milliseconds” without conversion. This is a request structure, not a guarantee of successful execution. [Pinned source](https://github.com/apple-oss-distributions/xnu/blob/f6217f891ac0bb64f3d375211650a4c1ff8ca1ea/osfmk/mach/thread_policy.h#L120).

**Anchor B — the implementation validates and changes requests.** In [thread_policy.c:300](case://local/android-ios-reverse-lab/epoch-4/apple-sources/thread_policy.c:300), certain work-interval flags reject a request to become real-time. The policy handler at line 391 checks structure length, rejects constraint smaller than computation and rejects computation outside named minimum/maximum quanta. It may raise computation toward half the constraint, capped by the maximum, before storing fields and selecting `TH_MODE_REALTIME`. The separate QoS branch at line 591 validates its tier and relative-priority inputs. [Pinned implementation](https://github.com/apple-oss-distributions/xnu/blob/f6217f891ac0bb64f3d375211650a4c1ff8ca1ea/osfmk/kern/thread_policy.c#L300).

These observations answer a narrow question: public policy code treats real-time requests as structured, checked state changes. They do not tell us the exact deadline success rate of a phone, the values of every configuration constant, or every path by which app frameworks call into this code. A comment or a mode flag alone cannot prove a complete scheduling algorithm.

## 3. “Real-time” needs a deadline and a failure definition

A 60 Hz frame interval is approximately 16.67 ms; 120 Hz is approximately 8.33 ms. Those are arithmetic intervals, not measured budgets on a particular device. Some of the interval is consumed by other work, handoffs and presentation timing. The application needs headroom for variation.

Real-time audio provides a more concrete deadline model than the vague phrase “fast engine.” Apple describes audio workgroups as groups of real-time threads cooperating on a common audio deadline. Missing that deadline can produce an interruption in playback. Framework-provided audio threads are automatically joined to the appropriate workgroup; apps with auxiliary real-time threads have additional APIs to describe that cooperation. This does not turn ordinary background tasks into unrestricted real-time threads. [Apple Audio Workgroups](https://developer.apple.com/documentation/audiotoolbox/understanding-audio-workgroups).

A useful engine report therefore separates average throughput from missed deadlines and tail latency. “Most frames take 6 ms” leaves unanswered whether a rare 80 ms stall is visible. Audio, simulation, rendering, networking and asset streaming also need different failure policies. A renderer may lower detail; an asset streamer may defer a texture; an audio callback cannot substitute an arbitrarily late buffer for the one already needed.

## 4. GPU work: encode, submit, execute, complete

For the established `MTLCommandQueue` API, the CPU creates a command buffer and encodes render/compute commands into it. Committing submits work for GPU scheduling; it does not mean the GPU has already finished. A committed `MTLCommandBuffer` is single-use, and completion notifications are a different event from submission. [Apple command-structure guide](https://developer.apple.com/documentation/Metal/setting-up-a-command-structure), [commit contract](https://developer.apple.com/documentation/metal/mtlcommandbuffer/commit%28%29).

```text
CPU: input → simulation → prepare resources → encode commands → submit
GPU:                                             wait → execute passes → complete
Display:                                                            present when scheduled
```

The diagram is a conceptual dependency map, not a captured trace. CPU work for frame N+1 can overlap GPU work for frame N when resources and dependencies allow. In an idealized pipeline with 3 ms CPU work and 6 ms GPU work, steady-state production might approach one result per 6 ms rather than one per 9 ms. A particular result still experiences both stages plus handoff/queue/presentation delays. Overlap can improve throughput without reducing input-to-display latency by the same amount.

Do not apply the old object's lifetime rules to all current Metal APIs. **Metal 4 introduces distinct `MTL4CommandQueue` and `MTL4CommandBuffer` types**, reusable command buffers, command allocators and changed work-submission/resource-management contracts. Apple supports incremental adoption and runtime capability checks. A lesson or engine port must say which API family it means; a device's OS version alone is not the full capability matrix. [Apple Metal 4 core API](https://developer.apple.com/documentation/metal/understanding-the-metal-4-core-api), [feature tables](https://developer.apple.com/metal/capabilities/).

## 5. Synchronization describes dependencies

Suppose pass A writes a texture and pass B reads it. “I encoded A first” is not a universal substitute for the API's resource synchronization rules. The producer's relevant writes must become available before the consumer's reads.

Apple distinguishes barriers, fences and events by scope. For current Metal 4, fences coordinate passes within one command queue; events coordinate across queues, while shared events can also coordinate CPU and other device work. Legacy tracked resources submitted through `MTLCommandQueue` have conditional automatic hazard handling. That automatic handling has conditions, including tracked mode and direct resource binding; it must not be assumed for every resource or every API generation. [Apple resource synchronization](https://developer.apple.com/documentation/metal/resource-synchronization).

Apple's fence page explicitly notes a generation difference: Metal 3 fences can work across multiple queues on one device; Metal 4 cross-queue synchronization uses events/shared events. This is why a sentence such as “Metal fences always have exactly this scope” can become misleading. [Apple fence documentation](https://developer.apple.com/documentation/metal/synchronizing-passes-with-a-fence).

The engineering question is to identify the smallest dependency that must wait. Waiting for every GPU task after every pass can make the result correct while removing useful concurrency. Removing all waits can expose read/write races. Correctness and overlap must be designed together.

## 6. Unified memory is shared capacity, not unlimited capacity

Apple GPUs share system memory with the CPU, but access depends on resource mode. `shared` supports CPU and GPU access; `private` supports GPU access; `memoryless` is for temporary texture content in GPU tile memory and does not apply to buffers. The choice describes access/lifetime, not just a “fast versus slow” switch. [Apple storage modes](https://developer.apple.com/documentation/metal/choosing-a-resource-storage-mode-for-apple-gpus).

Shared allocation does not make simultaneous CPU writes and GPU reads safe. A practical ownership sequence is:

```text
CPU may fill slot A → submitted GPU work may read A
                    → GPU completion makes A reusable by CPU
```

Multiple slots can avoid overwriting in-flight data, but extra buffering consumes memory and may increase latency. This sequence is an illustrative design requirement, not a benchmark or a statement that every engine needs exactly three buffers.

Memory arithmetic makes the limit tangible. An uncompressed 3840×2160 texture with four bytes per pixel contains 33,177,600 bytes—about **31.64 MiB**—before alignment, mipmaps or other overhead. Three such surfaces contain about 94.92 MiB. Depth targets, multisampling, shadow maps, meshes, CPU objects and other apps add further demands. A device having unified memory does not grant an app the entire physical pool. We have not measured this target's resource limits or working-set headroom; a Mac-specific allocation percentage must not be copied into an iPhone claim.

## 7. Frame pacing: regular delivery, not just a high average

A display link is a timing signal, not an unconditional instruction to run at the highest refresh rate. Apple's `preferredFrameRateRange` is a best-effort range; hardware, system work, power, thermal conditions and user settings can change availability. Consistently meeting an appropriate cadence matters more than requesting a number the app cannot sustain. [Apple display-link contract](https://developer.apple.com/documentation/quartzcore/cadisplaylink/preferredframeraterange).

Illustrative diagnosis: two engines can average 60 frames per second, while one delivers evenly spaced frames and the other alternates short bursts with stalls. Their average rates match but their motion and input response differ. Capture per-frame CPU start/end, GPU start/end and presentation timestamps to distinguish CPU delay, GPU delay and queueing. No such capture was made in this case.

Avoid conflating simulation time with rendering cadence. A simulation can use a defined step with interpolation while presentation follows the actual display timing. How an engine handles catch-up, pause and dropped frames is a behavior contract to inspect, not something proven by seeing a display-link symbol in a binary.

## 8. Where a game engine fits

| Engine concern | Typical boundary to inspect | Evidence that would answer the question |
| --- | --- | --- |
| Input and simulation | CPU jobs, state dependencies | Task graph and timings, not just thread count |
| Geometry/material preparation | CPU data and GPU resources | Resource lifetime and upload/copy traces |
| Rendering and compute | Metal backend and shaders | Encoded passes, capabilities and GPU captures |
| Audio | Audio callbacks/workgroups | Deadline and overload observations |
| Asset streaming | Storage/decode/upload pipeline | I/O, decompression, residency and frame stalls |
| Presentation | Drawable/display timing | Presented-frame intervals and latency |

These are analyst-defined investigation boundaries. An engine such as Unity, Unreal or a custom engine can organize these concerns differently. Apple documents platform tooling for both established engines and custom ports; that does not imply that an arbitrary build from another platform becomes an iOS build without backend, platform-service and asset work. [Apple game-porting overview](https://developer.apple.com/videos/play/meet-with-apple/239/).

The GPU is one execution component, not the entire engine. A faster shader does not repair a blocked CPU dependency, an audio underrun or a server response that has not arrived.

## 9. Why “millions of apps” is a different question

A large catalog is a distribution fact. Compatibility is a collection of contracts. The following is an analytical checklist, not a claim that this case implemented any layer:

| Layer | What must match or be translated |
| --- | --- |
| CPU instructions | Architecture, required instruction features and execution state |
| Binary/runtime | Executable format, calling conventions, linking, Objective-C/Swift runtime behavior |
| Platform APIs | Framework methods, data formats, lifecycle and error behavior |
| Graphics/audio | Metal features, shader/resource behavior and audio timing APIs |
| Security model | Code identity, sandbox boundaries, entitlements and permission flow |
| Platform services | Purchases, push notifications, cloud/account integrations and other dependencies |
| App backend | The app's own server protocols, authentication and supported versions |
| Distribution | Packaging, delivery, updates, app availability and account conditions |

Sharing an instruction architecture addresses only part of the first row. It does not supply all remaining services. Likewise, successfully booting a kernel is not evidence that arbitrary apps behave correctly.

Apple describes iOS third-party apps as sandboxed and access to privileged features as controlled through signed entitlements and system-provided services. Those are part of the app's expected runtime environment, not merely UI conventions. [Apple runtime security](https://support.apple.com/en-gb/guide/security/sec15bfe098e/web).

A defensible compatibility claim is scoped: which app versions, which features, which devices/OS builds, which failure cases and which external services were exercised? Store catalog counts, framework-name matches or a successful launch cannot replace that matrix. This investigation has not installed or tested an app catalog.

## 10. What this round proves and what comes next

**Observed source:** two hash-pinned public XNU files, including concrete policy validation and state-transition branches. **Documented design:** Metal work submission, generation-specific synchronization, storage modes, frame-rate hints, audio workgroups and sandbox contracts. **Derived illustrations:** frame-interval, pipeline and texture-memory arithmetic. **Unknown:** this firmware's scheduler implementation, GPU firmware internals, real frame times, power/thermal behavior and application compatibility.

Useful next tests require a declared app and target: identify a CPU bottleneck, capture a GPU dependency, test resource reuse across completion, measure frame-time tails, and verify a specific app feature through its backend. The existing iBoot evidence cannot substitute for any of these measurements.

Learning checks:

1. Why can a queue contain a thousand jobs while only a few execute simultaneously?
2. Why does command-buffer commit not prove that the CPU can overwrite a resource immediately?
3. What changes when code uses `MTL4CommandQueue` instead of `MTLCommandQueue`?
4. Why can a six-millisecond GPU stage still lead to more than six milliseconds of input latency?
5. Which compatibility layers remain even after the CPU can decode an application's instructions?

Answers should identify the relevant boundary and required evidence, rather than replace one broad performance promise with another.


## Evidence and limits

Connect public XNU policy checks and version-specific Metal contracts while preserving the unmatched-kernel boundary.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [apple-cpu-gpu.md](case://local/android-ios-reverse-lab/epoch-4/apple-cpu-gpu.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
