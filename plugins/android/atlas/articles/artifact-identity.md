# Artifacts and provenance

A–Z topic A. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

An artifact is a particular file or package: an APK, a filesystem image, a firmware component, or an archive holding many components. Its name is a clue, not proof of its contents. Establish its byte length, content type, hash and provenance before interpreting it. A matching hash identifies the same bytes; it does not by itself identify a trustworthy producer.

**Our example:** the local IPSW's name looked valid, but its first ZIP member declared an end beyond the actual file. Three parsers rejected the directory. The correct conclusion was “incomplete archive,” not “unusual iOS encryption.”

**Try:** explain why opening a file by its magic bytes is weaker than successfully validating every archive member. [Initial evidence report](case://local/android-ios-reverse-lab/REPORT.md).

Continue with [iOS firmware wrappers and trust metadata](ios-firmware-lab.md).

## Evidence and limits

Identify exact bytes, distinguish integrity from origin, and recognize the incomplete local IPSW.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.

Original case-authored material: [ios-learning.md](case://local/android-ios-reverse-lab/epoch-2/ios-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
