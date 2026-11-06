# Boot stages and handoffs

A–Z topic B. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

Boot is a chain of execution and trust decisions, not one program. In Android, the bootloader and kernel precede init, then system processes and applications. In Apple's documented design, the application processor's Boot ROM anchors verification of later stages, while the Secure Enclave has its own chain. Neither chain is fully represented by an app package.

**Our evidence:** Android init source explicitly changes stages using `selinux_setup` and `second_stage`. The iOS manifest names several boot-related components, but a declaration is not a captured boot trace. Failure handling is part of the behavior: mount, label-restore and exec failures can prevent normal continuation.

**Try:** draw separate boxes for preparation, verification and execution. Never use one arrow to imply all three without evidence. [Android startup](android-startup-lab.md), [Apple boot boundaries](ios-firmware-lab.md).

Continue with [Android startup: init to application lifecycle](android-startup-lab.md).

## Evidence and limits

Follow Android init stages and distinguish their source handoffs from Apple documented boot design.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-learning.md](case://local/android-ios-reverse-lab/epoch-2/android-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
