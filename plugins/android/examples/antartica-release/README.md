# Antartica release laboratory sample

Minified native five-lesson teaching app, with release build, isolated black-box test module and debug Compose tests. SDK and Java17 are separate prerequisites; configure a local SDK path outside the package. Production signing is intentionally absent. The benchmark variant uses a lab debug identity and is profileable; the unsigned release requires an explicitly chosen signing identity to install.

Runtime acceptance results and failed attempts are documented by the antartica-emulator-release-readiness atlas topic. A passing build is not a passing device test. The black-box harness is restricted to the exact owned AV3X_Antartica_QA emulator and may require test-environment adaptation in another lab. No device is selected automatically by this sample. Do not install or execute against production displays.

The Lab screen is illustrative arithmetic, not hardware telemetry. This sample is not an OS or universal APK compatibility layer. See skills/android/references/release-readiness.md at plugin root for acceptance distinctions.

Current sample is versionCode4, with snapshot-backed UI state and SavedStateHandle persistence. This removes the UI-only Flow collection handoff; external data streams may still appropriately use lifecycle-aware collection. A previous one-off input-order mismatch was not reproduced in the controlled comparison, so no causal fix or reliability-rate improvement is established. The included instrumented black-box module remains diagnostic source with retained failing runs; exact-release acceptance uses the separate case-owned, source-pinned host controller. Do not infer that all instrumented suites passed.
