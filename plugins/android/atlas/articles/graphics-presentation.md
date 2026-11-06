# Drawing versus displaying

A–Z topic G. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

An app produces visual content, while the system coordinates windows, buffers and composition before the display receives a frame. A UI method call alone does not prove that pixels appeared. Scheduling, surfaces, buffer ownership, composition and display hardware all matter.

**Our evidence:** the sample's recovered method calls `setContentView`. That establishes a static UI setup path, not a screenshot or successful rendering. Android's graphics documentation explains the broader producer/compositor structure; the systems lesson adds a concrete SurfaceFlinger source anchor.

**Try:** trace one frame as ownership changes: who creates content, who queues a buffer, who composes it, and who presents it? Keep missing edges explicit. [Graphics architecture](https://source.android.com/docs/core/graphics), [systems lesson](android-systems-lab.md).

Continue with [GPU work and real-time frame engines](gpu-frame-engine-lab.md).

## Evidence and limits

Separate application UI setup from buffer submission, composition and actual display presentation.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [gpu-frame-engine.md](case://local/android-ios-reverse-lab/epoch-4/gpu-frame-engine.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
