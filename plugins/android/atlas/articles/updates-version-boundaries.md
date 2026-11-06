# OTA, Mainline and version boundaries

A–Z topic O. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

An update can change code, metadata, policy or only a modular component. Android supports modular updates and APEX packages as well as full-system mechanisms. Virtual A/B uses snapshots and a successful-boot/merge workflow. Old observations must be reconsidered when relevant bytes change.

**Our mismatch:** the source baseline is Android 17 r1, while the GSI metadata is QPR1. Even if names look identical, we cannot assume exact source-to-binary equivalence. Likewise, an iOS restore archive does not prove every later independently delivered patch is installed.

**Try:** make a dependency list for one claim. Which changed files invalidate it? [Virtual A/B](https://source.android.com/docs/core/ota/virtual_ab), [update lesson](android-systems-lab.md).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Understand modular and system updates, and why Android17 r1 source is not a proven QPR1 binary match.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
