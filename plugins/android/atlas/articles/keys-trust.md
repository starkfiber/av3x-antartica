# Keys, signatures and authorized trust

A–Z topic K. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A digital signature relates exact bytes to a public key. A trust policy decides whether that key is authorized. Those are separate checks. A system also needs to decide which version is acceptable and what permissions signed content receives.

**Our real check:** the Android vbmeta digest and RSA signature passed, including independent OpenSSL verification. The key came from the image. We did not establish that a particular phone trusts it. iOS member digests matched the BuildManifest, but that did not verify Apple's signature chain or a device-bound ticket.

**Try:** explain how an attacker-created file can have a mathematically valid signature but remain untrusted. [Verified-boot lab](avb-integrity-lab.md).

Continue with [AVB integrity, signatures and policy](avb-integrity-lab.md).

## Evidence and limits

Separate matching digests, mathematical signature checks and authorization of a trusted signing key.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- Embedded-key signature consistency does not establish OEM trust, rollback state or device acceptance.

Original case-authored material: [verified-boot-learning.md](case://local/android-ios-reverse-lab/epoch-2/verified-boot-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
