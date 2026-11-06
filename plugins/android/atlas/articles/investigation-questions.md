# Questions that narrow an investigation

A–Z topic Q. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** derived. This names the main evidence anchor, not universal verification of every explanatory statement.

“Understand the whole OS” becomes useful when decomposed into questions with observable answers. For example: “Which bytes does this digest cover?”, “What starts this process?”, or “What happens when this remote peer dies?” Each question narrows the next tool call and the evidence required.

**Our example:** instead of downloading every iOS payload, we selected three small members whose digests and wrappers could be fully checked. This yielded concrete lessons in composition and integrity while the full filesystem remained unavailable.

**Try:** write two competing explanations for an observation and choose the smallest inspection that distinguishes them. A broad string dump often distinguishes neither. [Investigation checkpoint](case://local/android-ios-reverse-lab/checkpoint.md).

Continue with [iOS firmware wrappers and trust metadata](ios-firmware-lab.md).

## Evidence and limits

Turn a whole-OS goal into discriminating questions and bounded inspections.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.

Original case-authored material: [ios-learning.md](case://local/android-ios-reverse-lab/epoch-2/ios-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
