# Containers, filesystems and transformations

A–Z topic F. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

An archive is not a filesystem, and a filesystem image is not the files it contains. ZIP compression, Android sparse encoding, disk-image structures, encryption and filesystem metadata form different layers. Extraction must identify each layer correctly and preserve which bytes were transformed.

**Our example:** one iOS file ending in `.root_hash` is an IM4P wrapper with a 208-byte inner structure, not simply a bare 32-byte digest. Another selected payload expands from 80 compressed bytes to 272 bytes. Decompression is not decryption or code execution.

**Try:** label each transformation in “IPSW → member → wrapper → compressed payload → decoded data.” Which hash belongs to which layer? [Worked iOS byte analysis](ios-firmware-lab.md).

Continue with [iOS firmware wrappers and trust metadata](ios-firmware-lab.md).

## Evidence and limits

Track IPSW members, IM4P wrappers and decompressed payloads without conflating decoding with execution.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.

Original case-authored material: [ios-learning.md](case://local/android-ios-reverse-lab/epoch-2/ios-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
