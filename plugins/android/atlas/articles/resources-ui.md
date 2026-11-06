# Resources and UI declarations

A–Z topic R. **Scope:** Historical AOSP HelloActivity prebuilt (version P/27, target SDK 23) retained in Android17 r1 tag; not Android17 system app.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

Applications include layouts, strings, icons and other resources in addition to code. Integer resource identifiers connect code to compiled resource tables and files. Decompiled calls can identify which resource is selected, while resource decoding explains what it declares.

**Our sample:** `R.layout.hello_activity` is passed to layout inflation. Recovering the method does not by itself recover every layout attribute or prove how the screen renders on a particular device.

**Try:** follow the identifier through the resource table to the packaged layout, then distinguish declaration from runtime rendering. [APK layers](apk-static-lab.md).

Continue with [A real APK static-analysis lab](apk-static-lab.md).

## Evidence and limits

Connect a recovered layout identifier to the next resource-analysis question without claiming rendered pixels.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.

Original case-authored material: [tooling.md](case://local/android-ios-reverse-lab/epoch-2/tooling.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
