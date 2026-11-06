# GPU work and real-time frame engines

**Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

This chapter combines inspected Android source with a deliberately simplified teaching model. It contains no hardware performance measurements. Source snapshot: frameworks/native commit ae266dcb706d083868578cfedce381ef44488a07 (Android 17 r1 context), not a verified binary match to the selected QPR1 GSI. Acquisition URLs, sizes and hashes are in graphics-source/provenance.json.

## 1. An engine orchestrates several different machines

A game or interactive engine normally owns a simulation loop: read input, advance state, prepare a scene, submit rendering work. Physics, animation, audio and asset loading have their own timing and dependency needs. The operating system schedules CPU threads; a graphics driver translates and submits work for GPU execution; the display system chooses when a completed image can be presented. None of these layers is a single universal clock.

A fixed simulation timestep makes integration more predictable, but a slow frame can require multiple simulation updates. An engine needs a catch-up policy; unlimited catch-up can consume all available time. Rendering may interpolate between simulation states. Audio also needs timely buffers and must not be treated as an occasional frame-loop side effect. These are architectural patterns, not findings that every engine uses identical internals.

## 2. CPU work and GPU work are different

The CPU handles control flow, input, object state, resource management and command preparation. The GPU executes large sets of suitably parallel work: vertices, fragments, image filters or compute kernels. A shader is a program executed on the GPU, not a request that the CPU draw every pixel itself.

More CPU cores only help work that can be divided and scheduled without overwhelming coordination or memory traffic. More GPU arithmetic capacity only helps if the workload is limited by that capacity. Memory bandwidth, texture access, overdraw, synchronization and power limits can dominate instead. Submission returning does not mean the GPU has finished.

## 3. Follow the Android buffer, not just the draw call

An app produces images into buffers. A consumer receives those images for composition or another use. BufferQueue tracks ownership and availability; fences communicate completion of asynchronous work. SurfaceFlinger combines surfaces with help from the hardware composer and, where required, GPU composition. It is incorrect to assume every layer always requires an extra full-screen GPU pass.

In the inspected BufferQueueProducer.cpp, the waitForFreeSlotThenRelock path checks whether a slot is unavailable or too many buffers are outstanding. Depending on state, it returns WOULD_BLOCK or waits for buffer release; a timeout can produce TIMED_OUT. This is concrete evidence of backpressure. An app cannot safely reuse an in-flight buffer merely because it wants to draw the next frame.

In Output.cpp, the composition path either uses a predicted result or obtains a render buffer and calls composeSurfaces. If an optional ready fence is absent it returns; otherwise it passes the ready fence to mRenderSurface->queueBuffer. These branches demonstrate that composition and queueing carry completion information. They do not establish the duration of composition on a particular phone.

Read the exact local sources: graphics-source/BufferQueueProducer.cpp around lines 420–460 and graphics-source/Output.cpp around lines 1320–1348. These are source observations, not device traces.

## 4. Synchronization is part of correctness

A dependency can mean “this texture upload must finish before this draw reads it,” “this GPU write must become visible to a later read,” or “the CPU must not overwrite a buffer still in use.” Ordering, visibility and ownership are related but distinct concerns. A fence is not a universal replacement for every graphics API synchronization primitive.

Vulkan exposes substantial synchronization responsibility to the application. Metal supplies its own command, resource and synchronization models, and Metal 4 requires attention to the relevant newer API rules. Applying one backend's synchronization assumptions to another can cause corruption or unnecessary waits. See the Apple chapter for the version distinctions.

## 5. Frame rate, latency and pacing answer different questions

At 60 Hz, refresh opportunities are about 16.67 ms apart; at 120 Hz, about 8.33 ms apart. These are intervals, not a guarantee that the application gets that much uninterrupted CPU or GPU time. A frame must reach the appropriate presentation stage in time, with margin for other work.

Throughput asks how many frames finish per second. Latency asks how long a particular input or frame takes to reach the display. Pacing asks whether presentation intervals are regular. A long queue can keep a GPU busy while making visible state older. Increasing the number of queued buffers can change throughput and latency differently.

## 6. The interactive model's exact assumptions

Four frames are released at i × T, where T is the selected refresh interval. One serial CPU lane processes each frame, then one serial GPU lane processes it. CPU and GPU lanes may overlap across different frames. A frame waits for the next refresh boundary after GPU completion, with at most one frame presented at each boundary and FIFO ordering.

The model has an unlimited queue, fixed CPU/GPU durations, exact boundary availability and zero compositor cost. It omits scanout, input sampling, preemption, driver overhead, memory contention, thermal throttling, variable refresh, dropped frames and actual BufferQueue capacity. It is a teaching model, not an Android/iOS emulator or benchmark. In particular it deliberately omits the bounded-buffer backpressure observed in Android source.

With 4 ms of CPU work and 12 ms of GPU work at 60 Hz, frame 1 is GPU-ready at 16 ms and presents at 16.67 ms in this ideal model. Reducing CPU work to 2 ms still presents at that same boundary. At 120 Hz, 12 ms GPU work exceeds the refresh interval, so queued work grows if every refresh releases another frame. A real engine must pace, shed work or adopt another policy; it cannot make capacity appear by setting a priority flag.

## 7. How an engine reaches many devices

An engine presents a relatively stable internal rendering interface, then implements backends such as Vulkan or Metal. It compiles suitable shader variants, checks supported features, selects formats, manages resources and implements fallback paths. OS APIs and drivers hide some hardware differences, but capability checks, testing and optimization remain necessary.

The renderer also needs an application shell: lifecycle callbacks, surfaces, input, storage, networking, audio and permissions. Android's ART supports managed application code while native libraries can execute CPU code through the platform ABI. Apple's application frameworks and runtime provide their own contracts. A cross-platform engine cannot erase differences in process suspension, background execution or graphics features.

## 8. A meaningful next performance experiment

On an explicitly identified test device, collect CPU scheduling and frame timelines, GPU timing where supported, and presentation intervals for a controlled scene. Change one workload at a time: simulation complexity, pixel load, texture traffic or queued-frame policy. Compare distributions and missed deadlines, not only an average FPS counter. Record refresh rate, device, build, thermal state and tracing overhead.

No such experiment was run in this case. No connected device was selected or operated. A search for a prebuilt Vulkan sample release did not produce an acquired sample; there is no new GPU binary or execution evidence from that attempt.

## Primary references

- Android graphics architecture: https://source.android.com/docs/core/graphics/arch-bq-gralloc
- SurfaceFlinger and WindowManager: https://source.android.com/docs/core/graphics/surfaceflinger-windowmanager
- Android Frame Pacing: https://developer.android.com/games/sdk/frame-pacing
- Vulkan synchronization: https://docs.vulkan.org/guide/latest/synchronization.html
- Pinned source URLs and hashes: graphics-source/provenance.json


## Evidence and limits

Follow BufferQueue backpressure, composition fences and a simplified CPU/GPU frame model.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.
- Frame timings are hypothetical model inputs; no hardware benchmark or current engine binary was analyzed.

Original case-authored material: [gpu-frame-engine.md](case://local/android-ios-reverse-lab/epoch-4/gpu-frame-engine.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
