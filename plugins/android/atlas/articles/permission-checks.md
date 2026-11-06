# Permissions, identity and enforcement

A–Z topic P. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A manifest can request a permission. Grant state, caller identity, AppOps, service checks, filesystem permissions and SELinux policy can still affect the operation. A process boundary transports requests; it does not automatically decide whether every request is authorized.

**Our evidence:** the small teaching APK requests no permissions. That says little about the full OS permission system. The source-based systems lesson follows a permission-checking entry and states where the actual policy implementation remains outside the inspected code.

**Try:** identify the component that performs the sensitive operation, then follow its caller identity and authorization path. Do not stop at the manifest. [Application sandbox](https://source.android.com/docs/security/app-sandbox), [permission lesson](android-systems-lab.md).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Follow permission-checking entry points and keep grants, caller identity and kernel policy separate.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
