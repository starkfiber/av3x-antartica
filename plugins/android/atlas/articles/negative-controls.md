# Verification and negative controls

A–Z topic V. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A check that always says “success” is useless. A good verification exercise includes an input that should fail and explains the boundary it tests. It also preserves the exact target identity so later runs cannot silently inspect different bytes.

**Our negative control:** changing one byte in memory changed the signed-region digest and made the recovered RSA encoding mismatch. The original vbmeta was untouched. This is a focused integrity demonstration, not a general parser certification or device boot test.

**Try:** for every claimed pass, ask “What would make this check fail?” and “What does it leave unchecked?” [Verification evidence](case://local/android-ios-reverse-lab/epoch-2/avb/vbmeta-verification.json).

Continue with [AVB integrity, signatures and policy](avb-integrity-lab.md).

## Evidence and limits

Study the AVB original-byte pass and changed-byte rejection without elevating them to a device boot test.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- Embedded-key signature consistency does not establish OEM trust, rollback state or device acceptance.

Original case-authored material: [verified-boot-learning.md](case://local/android-ios-reverse-lab/epoch-2/verified-boot-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
