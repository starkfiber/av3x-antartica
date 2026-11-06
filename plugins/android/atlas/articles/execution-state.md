# Execution depends on state

A–Z topic E. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** derived. This names the main evidence anchor, not universal verification of every explanatory statement.

The same bytes can behave differently with different arguments, permissions, data, hardware, process state or build configuration. Static analysis describes possible structure; runtime evidence shows what happened in a specific environment. A function present in a binary may not execute in the path being studied.

**Our example:** a zygote service declaration exists in a source file. That does not prove the service started on a particular device. The import path, trigger, selected configuration and process observation are separate facts.

**Try:** replace “this code runs” with a precise statement: “this source branch calls X when condition Y holds.” Then name the experiment needed to observe that condition. [Startup failure paths](android-startup-lab.md).

Continue with [Android startup: init to application lifecycle](android-startup-lab.md).

## Evidence and limits

Explain why static presence of code or a service declaration is weaker than observing a path execute.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.

Original case-authored material: [android-learning.md](case://local/android-ios-reverse-lab/epoch-2/android-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
