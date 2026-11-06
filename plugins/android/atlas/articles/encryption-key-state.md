# Storage encryption and key state

A–Z topic U. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

A filesystem can be structurally readable while its protected contents remain unavailable without the appropriate key state. Android file-based encryption distinguishes storage availability across boot and credential-unlock states. Hardware-backed key services and access policies add further boundaries.

**Current proof level:** official design documentation, not extraction of user data. We have not read personal databases, obtained device keys or tested lock/unlock transitions. OS research here uses public firmware and source.

**Try:** distinguish the questions “Can I parse this filesystem?”, “Can I decrypt this file?” and “Is this caller authorized to use the key?” [File-based encryption](https://source.android.com/docs/security/features/encryption/file-based), [storage lesson](android-systems-lab.md).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Separate filesystem parsing, decryption and authorization to use a key across device states.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
