# A real APK static-analysis lab

**Scope:** Historical AOSP HelloActivity prebuilt (version P/27, target SDK 23) retained in Android17 r1 tag; not Android17 system app.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

This lesson uses a small real prebuilt APK from Google's AOSP repository. It is the historical ApiDemos **HelloActivity** sample retained in the Android 17 source tag, not an Android 17 system application, not a GSI extraction, and not proof about the OS as a whole. Keeping that boundary explicit makes the sample useful: we can inspect every visible piece and understand what each tool actually proves before applying the workflow to larger system packages.

## 1. Establish the target

Repository: `platform/development`; tag: `android-17.0.0_r1`; resolved commit: `4dafd114fab3c3d9543a5aff0ad097f479915176`. The downloaded sample is 12,770 bytes and hashes to SHA-256 `d84e553a7ead7382359cf54cbfc0b6ffec4654c4f71dc49c443eb214ffd6804a`. Its Git blob SHA-1 matches `a299fa2514b999675006a9c05e82205dc2c147fa`, the object identified by the source repository. The HTTPS acquisition and exact bytes are recorded in `tooling/hello-provenance.json`; this is not a signed Git tag verification.

[Official pinned APK](https://android.googlesource.com/platform/development/+/4dafd114fab3c3d9543a5aff0ad097f479915176/samples/ApiDemos/assets/HelloActivity.apk)

## 2. Separate the layers

The ZIP directory contains seven members: `AndroidManifest.xml`, `classes.dex`, `resources.arsc`, one layout XML and three `META-INF` signing files. Each answers a different question:

- The manifest declares package identity, components and requested capabilities.
- DEX carries Dalvik bytecode. A decompiler reconstructs a readable representation; it does not recover author comments or guarantee the original source text.
- Compiled resources connect integer identifiers in code to layouts, strings and other assets.
- Signing metadata declares signers and integrity structures. A displayed certificate is not enough to establish who produced the bytes.

The APK's ZIP CRC checks passed. CRC detects accidental member corruption; it is not a trust or authenticity check. Whole-file SHA-256 identifies this exact sample, but a hash without trusted provenance is also not a producer identity.

## 3. Read REA's signature result carefully

REA's `inspect_apk_signature` produced evidence `ev_3c9e31b6b12f02ee76f0e8495ea19a1859efc641531bad3625d4b9a7a3f84e68`, retained in `tooling/hello-signature-evidence.json`. It observed v1 and v2 structures, no v3/v3.1, and agreeing declared certificate fingerprints. The recognized SHA-256 content digest matched the APK bytes.

Crucial limitation: **REA did not cryptographically verify the signature**. Its own output explicitly says that digest agreement does not establish authenticity: somebody changing both data and the declared digest can preserve that agreement. An additional signature-verification tool and trust decision would be needed before making a stronger claim. This is why evidence descriptions need precise verbs such as “declares,” “matches,” “verified,” and “executed.”

## 4. Follow the entry point, then check what is missing

The pinned source counterpart `samples/HelloActivity/src/com/example/android/helloactivity/HelloActivity.java` has one activity callback. It calls `super.onCreate`, inflates a layout resource, then passes the resulting View to `setContentView`. The source is retained as `tooling/HelloActivity.java`. It gives a hypothesis to test against bytecode; it does not establish that this prebuilt was compiled from the current source file.

[Official pinned source](https://android.googlesource.com/platform/development/+/4dafd114fab3c3d9543a5aff0ad097f479915176/samples/HelloActivity/src/com/example/android/helloactivity/HelloActivity.java)

The completed provider acquisition and bytecode findings follow below.

## Questions to test your understanding

1. If ZIP CRC and SHA-256 both match your records, have you proved that Google signed the APK? Why not?
2. Why can decompilation show a call to `setContentView` without proving that a screen ever appeared?
3. If a prebuilt APK and a Java source file live in the same Git commit, what additional evidence would link that binary to that source?
4. Which layer would you inspect to connect `R.layout.hello_activity` to actual UI elements?
5. Why is this sample a provider qualification exercise rather than a claim that Android 17's applications are understood?

## 5. Actual decompilation succeeded

The resumed JADX bridge download completed: 42,619,858 bytes, SHA-256 `6e5eacf500b64292bfb73c49797c1958f6ee44646e43e868039ae7feb573ff75`, matching both the official release metadata and REA's audited pin. The durable download job returned exit 0 and verified process-group cleanup.

A readiness check caught another issue: the initially selected Node 22.23.3 was below REA 6.8.0's supported minimum. The existing app-bundled Node 24.22.1 at `/Applications/ChatGPT.app/Contents/Resources/cua_node/bin/node` met the requirement. The case-local wrapper now selects that runtime with Java 17. No global configuration or system software was changed. The scoped JADX doctor reports healthy; unrelated native engines and registrations remain outside that success claim.

The real APK produced these REA observations:

| Inspection | Observed result | Retained evidence |
| --- | --- | --- |
| Package | `com.example.android.helloactivity`, version `P` / `27`, min SDK 23, target SDK 23, no requested permissions, one MAIN/LAUNCHER activity | `tooling/hello-package-evidence.json` |
| Class inventory | Two top-level classes: HelloActivity and R | `tooling/hello-class-inventory.json` |
| Activity members | Constructor and `onCreate(Bundle)`, no declared fields or inner classes | `tooling/hello-class-evidence.json` |
| onCreate method | Java representation available; calls superclass, inflates `R.layout.hello_activity` with null parent, calls `setContentView` | `tooling/hello-oncreate-evidence.json` |
| Incoming references | Zero in-package using nodes for `onCreate`; not proof of dead code | `tooling/hello-oncreate-references.json` |

The method evidence ID is `ev_bb37084de8dadbed3001190d3669a278bff2ff22d4fed951f72921742fd39728`. The decompiled method's sequence agrees with the retained source counterpart. That is a limited structural comparison; no reproducible-build claim is made.

The SDK/version declarations show why merely retrieving a file from an Android 17 tag does not make its contents “Android 17 code.” This sample prebuilt still declares a historical platform version and SDK target. That finding prevents a misleading coverage claim.

## 6. Why zero references does not mean unused

Android's framework calls Activity lifecycle methods. A framework callback need not have a caller inside the application's own DEX. This artifact-level reference search therefore cannot prove that `onCreate` is unreachable. The declared launcher component, method override and [Android Activity lifecycle contract](https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)) explain a plausible entry path; observing that path on a running Android system is a different experiment.

Each REA CLI command ran in a fresh process against the same hash-pinned APK, providing a practical resume check for static parsing. No APK was installed or launched. The bundled generic coverage helper rejected the Android method operation as unsupported, recorded in `tooling/hello-coverage-gate.json`; no semantic or runtime coverage certification is claimed.

## Answer key

1. No: integrity checks identify or compare bytes; they do not verify an authorized signer.
2. Decompiled code is a static representation. Layout inflation, lifecycle dispatch and rendering were not exercised.
3. Build provenance or a reproducible build would be needed; co-location alone is insufficient.
4. Compiled resource identifiers and the packaged layout XML connect code references to UI declarations.
5. The sample is small and historical. Success proves this configured provider can inspect this APK, not every system component.
6. An extra question: why are there no in-package callers of onCreate? Because callback invocation can originate in the Android framework outside the APK.

## Next experiment

After completely acquiring and verifying the selected GSI, apply the same package → component → class → method → references workflow to an extracted system APK. Keep its new hash and version separate from this sample. Add resource decoding and a controlled emulator exercise before claiming visible behavior. The present case-local provider is ready for that next artifact.


## Evidence and limits

Inspect official historical HelloActivity bytes through package, class, method and reference evidence.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- The historical sample qualifies this static workflow; it is not a system APK and signatures were not cryptographically verified.

Original case-authored material: [tooling.md](case://local/android-ios-reverse-lab/epoch-2/tooling.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
