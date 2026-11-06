# Jobs, queues and deferred work

A–Z topic J. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A request may enqueue work rather than perform it immediately. Main-thread queues, background workers, system schedulers and service event loops affect when work happens. Power and background-execution policies can defer work further. Following a call only to the enqueue operation misses the eventual consumer.

**Our source example:** the retained ActivityThread prefix receives application-binding work and posts a handler message. The retained prefix does not include the complete downstream implementation, so the trace stops there explicitly.

**Try:** annotate a flow with “synchronous call,” “queued message,” and “callback.” Identify where failure or cancellation could occur before the next stage. [Execution and queues](android-startup-lab.md), [power/jobs context](android-systems-lab.md).

Continue with [Android startup: init to application lifecycle](android-startup-lab.md).

## Evidence and limits

Follow application-binding work into a handler queue and state where the retained source prefix stops.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-learning.md](case://local/android-ios-reverse-lab/epoch-2/android-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
