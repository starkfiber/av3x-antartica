# Practical learning labs

A–Z topic Y. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** derived. This names the main evidence anchor, not universal verification of every explanatory statement.

Do these in order, using retained data rather than repeating downloads:

1. **Identity lab:** compare complete-member size declarations with the truncated IPSW size. Explain the failure.
2. **Startup lab:** follow init's stage arguments and locate a fatal branch.
3. **APK lab:** read manifest → class → method → references. Explain the callback gap.
4. **Integrity lab:** inspect vbmeta's signed region, embedded key, successful verification and negative control.
5. **iOS structure lab:** compare whole-member digests, DER wrapper fields and decompressed payload size.
6. **System lab:** choose one camera or display action and draw the process and hardware boundaries.

Each detailed lesson contains an answer key or evidence links. Do not claim you have observed runtime behavior until you actually run a separately scoped experiment.

Continue with [A real APK static-analysis lab](apk-static-lab.md).

## Evidence and limits

Work through identity, startup, APK, integrity, iOS structures and system-boundary exercises using retained evidence.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.

Original case-authored material: [tooling.md](case://local/android-ios-reverse-lab/epoch-2/tooling.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
