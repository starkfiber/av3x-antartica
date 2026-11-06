# Drivers and device-specific boundaries

A–Z topic D. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match; Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

A driver connects the operating system to a particular hardware facility. Its behavior may depend on registers, DMA, interrupts, firmware and device-tree or platform configuration. Android's generic system image cannot describe all vendor drivers, bootloaders or modem firmware. Apple's application-processor software likewise does not subsume its Secure Enclave or baseband implementation.

**Our boundary:** the selected GSI archive contains system.img, vbmeta.img and build.prop. It is not a whole-device firmware package. The iOS manifest demonstrates multiple component families, but their names alone do not reveal internal behavior.

**Try:** for a camera failure, separate application, framework/service, HAL, kernel driver, sensor firmware and hardware hypotheses. A successful app decompilation only addresses one part of that chain. [System boundaries](android-systems-lab.md).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Understand why generic system metadata cannot reveal every vendor driver, modem or coprocessor.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
