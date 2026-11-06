# System services and dependency order

A–Z topic S. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A service is a role, not necessarily a unique executable or process. Android's system_server starts multiple services in ordered phases; other services live elsewhere. Their startup can depend on configuration, hardware and earlier services. A list of service names loses this order and dependency information.

**Our source finding:** SystemServer prepares its environment and runs bootstrap, core, other and APEX phases before continuing its persistent loop. Escaping failures are handled explicitly. This does not establish every service implementation or selected-device configuration.

**Try:** draw a dependency graph rather than a flat inventory. Mark conditional branches and fatal errors. [SystemServer source lesson](android-startup-lab.md).

Continue with [Android startup: init to application lifecycle](android-startup-lab.md).

## Evidence and limits

Read ordered service startup, guards and failure paths instead of flattening Android into service names.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-learning.md](case://local/android-ios-reverse-lab/epoch-2/android-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
