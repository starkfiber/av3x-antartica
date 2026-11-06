# Activity lifecycle versus process lifetime

A–Z topic L. **Scope:** Historical AOSP HelloActivity prebuilt (version P/27, target SDK 23) retained in Android17 r1 tag; not Android17 system app.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

An Android activity receives lifecycle callbacks; a process hosts execution. Destroying an activity does not necessarily destroy its process, and losing a process can require recreating application state later. Framework-controlled callbacks may have no direct caller in the application's own code.

**Our example:** the sample's `onCreate` has zero incoming in-package references in the selected REA query. That is not dead-code proof. The framework is outside the APK and can invoke lifecycle methods.

**Try:** draw separate timelines for an activity and its process. Mark which events need runtime observation rather than static inference. [Activity lifecycle](https://developer.android.com/guide/components/activities/activity-lifecycle), [callback lab](apk-static-lab.md).

Continue with [A real APK static-analysis lab](apk-static-lab.md).

## Evidence and limits

Distinguish activity callbacks from process survival, using the sample callback without claiming runtime results.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [tooling.md](case://local/android-ios-reverse-lab/epoch-2/tooling.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
