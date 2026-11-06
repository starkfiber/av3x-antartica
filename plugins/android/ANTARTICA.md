# AV3X Antartica

Canonical name: AndroidVersion3X. Short name: AV3X. Codename: Antartica, preserving the user's spelling. All further work stays in major version3 and uses Antartica revisions. This is a plugin/research revision identity, not a newly implemented Android OS.

The environmental objective is useful work with less waste: correctness and accessible output first, then energy/task, memory, latency, thermal behavior and model overhead. No carbon or battery-life benefit is inferred from a faster emulator without measurement.

`$android` remains the compatibility entry point. Keep the stable source checkout/MCP registration paths; old cache-version links may disappear after updates. Existing OS/platforms remain reference implementations and compatibility targets under PROJECT-DIRECTION.md. Ru’OS remains outside this case.

## Runtime experiments

Use explicitly identified owned test emulators/simulators. Never select the first ADB device. Do not touch the showroom display, connected phones or other virtual devices. Retain baseline/candidate hashes, runtime identity, actual renderer logs and all valid trials. Compare source changes within the same runtime; compare runtime configurations as a separate experiment.

An ARM64 Android guest on Apple silicon can use hardware virtualization. This does not turn its APK into a native macOS app. Android runtime/kernel/services/ABI and GPU translation remain part of the execution path.

Measure before changing and test a return-to-baseline condition when feasible. Stop when changes fail the declared acceptance conditions or the result is inconclusive. Repeating until one favorable run appears is not optimization evidence. Preserve negative findings and data-format anomalies.

When idle after experiments, shut down only the owned emulator to avoid unnecessary resource use. Explicitly record remaining processes and unmeasured environmental effects.
