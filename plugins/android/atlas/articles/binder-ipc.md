# Binder and process boundaries

A–Z topic I. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

Inter-process communication lets separately isolated processes exchange requests. Android's Binder makes this resemble method calls, but the receiving process can die, reject work or respond asynchronously. AIDL describes interfaces; generated bindings and the Binder transport carry them across the boundary.

**Our source trace:** native transaction construction reaches the driver write/read boundary. Dead-object and failed-transaction responses have distinct paths. One-way calls still have transport-level work; “no ordinary reply” does not mean “nothing can fail.”

**Try:** compare a local function call with a remote transaction using the same apparent arguments. Which extra failures and identity checks arise? [Pinned Binder trace](android-startup-lab.md).

Continue with [Android startup: init to application lifecycle](android-startup-lab.md).

## Evidence and limits

Trace transaction construction and driver interaction while retaining remote failure and authorization boundaries.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-learning.md](case://local/android-ios-reverse-lab/epoch-2/android-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
