# Cross-reference search boundaries

A–Z topic X. **Scope:** Historical AOSP HelloActivity prebuilt (version P/27, target SDK 23) retained in Android17 r1 tag; not Android17 system app.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A cross-reference says that a tool found a relationship within its analyzed representation. It may miss framework callbacks, reflection, dynamically loaded code, indirect calls or code outside the artifact. “No results” must be reported with its search boundary.

**Our concrete example:** REA found no in-package using nodes for the teaching activity's `onCreate`. The lifecycle contract supplies an external entry path; static absence within the APK does not negate it.

**Try:** rewrite “this method is never called” into the strongest claim the evidence supports. Answer: “this query found no incoming references within the analyzed APK representation.” [Reference evidence](case://local/android-ios-reverse-lab/tooling/hello-oncreate-references.json).

Continue with [A real APK static-analysis lab](apk-static-lab.md).

## Evidence and limits

Interpret zero in-package references correctly when framework callbacks lie outside the APK.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.

Original case-authored material: [tooling.md](case://local/android-ios-reverse-lab/epoch-2/tooling.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
