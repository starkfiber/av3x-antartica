# HAL contracts and vendor implementations

A–Z topic H. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

A hardware abstraction layer provides an interface between higher system code and hardware-specific implementations. The interface can be known while the implementation remains unavailable. Knowing an AIDL method signature does not reveal a vendor's driver algorithm, calibration or firmware behavior.

**Our boundary:** AOSP source and a generic image support investigation of common interfaces. They do not prove that every vendor implements them identically. Interface compatibility and implementation equivalence are different claims.

**Try:** write the input, output, error and identity contract for a hypothetical sensor request. Then separately list implementation questions. [Official HAL overview](https://source.android.com/docs/core/architecture/hal).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Distinguish a hardware-interface contract from the unavailable behavior of a vendor implementation.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
