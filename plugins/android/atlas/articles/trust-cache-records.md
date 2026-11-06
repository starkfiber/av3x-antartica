# Trust-cache records and code measurements

A–Z topic T. **Scope:** Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

A trust-cache record can contain a code hash and flags, but it is not automatically an app name or a process. To associate records with code, acquire the matching executable and compute the corresponding code-directory measurement. Runtime acceptance is another step.

**Our iOS data:** a selected 840-byte payload fits a 24-byte header plus 34 records of 24 bytes. Its structure agrees with a pinned independent parser. The 34 records are not “34 apps,” and their category fields do not prove the current kernel's enforcement behavior.

**Try:** explain which additional files would let you map a record to an executable. [Trust-cache lab](ios-firmware-lab.md).

Continue with [iOS firmware wrappers and trust metadata](ios-firmware-lab.md).

## Evidence and limits

Interpret a 34-record trust-cache layout while leaving code-hash-to-file mapping and acceptance unproved.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- No Apple signature/ticket chain, filesystem seal, actual load address or runtime enforcement was established.

Original case-authored material: [ios-learning.md](case://local/android-ios-reverse-lab/epoch-2/ios-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
