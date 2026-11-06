# Exact-release emulator acceptance

This is a bounded teaching-app laboratory milestone, not whole-OS, universal compatibility or production-distribution certification. The user chose emulator-only validation. Physical devices and showroom displays were excluded.

## Artifact identity

The current five-lesson candidate uses UI-only synchronous Compose snapshot feedback, removing its earlier Flow-collection handoff while preserving SavedStateHandle keys; lesson text reflects that implementation. It is versionCode4 within major version3, not an OS revision or a proven fix for a diagnosed Android defect.

Current unsigned minified release SHA256:94e121799b3623aa98e210fc58ce235aea4f41e504a4619370e257b81d9389c9. Current lab-signed copy SHA256:afe0a73c2aeba684ccb7c973ee73972558da1e3884d4d2b75051cd24ed87b1dd. All76 nonsignature ZIP entries compare byte-for-byte after decompression, including META-INF runtime/service metadata. Whole APK/container bytes differ due to signing. The lab certificate remains a laboratory identity; production key custody and publication are incomplete. No key or APK is included in this plugin.

Historical versionCode3 unsigned SHA256:cd9e1179b039cfc0a08fb70b4acf28842fd1667b58b331ee882962e60cf5d95a; lab copy:0bcb8549953ffa4c634ec507e233527d1a955c519914223cdae79da822a3f8fe. Its own76-entry parity is retained separately. Candidate parity does not assert equality with this older payload.

## Verification layers

Earlier local logic/lint receipts retain their original build scope. The final candidate separately passes four debug Compose tests covering search/clear, expansion semantics, separate tab scroll state and activity recreation. Its debug artifact is distinct from the minified release; those tests do not by themselves establish release runtime behavior.

The exact installed lab-signed release is separately driven by host-side native input and XML observations, with installed APK digest verification, exact qemu/AVD identity guards, positive state assertions and retained screenshots. Runtime configuration and final settings restoration require readback. The project-local epoch13 receipts contain actual per-configuration results; a planned matrix is never a pass.

## Test pitfalls retained as findings

Embedding Compose instrumentation in the minified target first failed from missing tracing and Kotlin test runtime classes. Additional target-library keeps would change the artifact. Debug Compose tests and a separate black-box controller preserve the distinction.

A filtered Compose list can fit its viewport and report scrollable=false. Requiring that flag before clicking an already visible control incorrectly fails. Observe current content geometry and reacquire nodes after bounded gestures. Label bounds and clickable-ancestor bounds are distinct; use the actual actionable target.

An offscreen Hide label is not proof of collapse or a failed expansion. Filter to one identified lesson, expand once and positively observe Hide lesson, then collapse once and positively observe Read lesson. A second diagnostic tap can undo the first action. Immediate screenshots may look identical when the changed content lies below the viewport.

Earlier instrumentation and first host-controller failures remain failures. Independent later journeys do not retroactively turn those logs into passing suites. No new Android platform bug or app speed gain follows from repairing a test harness.

## Recovery and limits

Background process-death checks must first observe a live process, move the app out of view, verify termination, then reopen the preserved task without CLEAR_TASK. Check restored query, count and expansion separately from activity recreation. This does not prove reboot, crash, power-loss or every low-memory scenario.

The HTML reader's rendered layout, native keyboard traversal and assistive testing remain open: supported access was denied. Android native app tests and DOM checks do not close that gate. No alternate access route was used to bypass it.

No physical FPS, battery, thermal, carbon reduction, native-macOS APK execution or universal compatibility claim is established. Read antartica-m5-runtime-results for the separate earlier debug-renderer experiment. The Lab screen remains illustrative arithmetic, not telemetry. See examples/antartica-release and skills/android/references/release-readiness.md for source and reusable acceptance guidance.


## Completed final candidate matrix and retained counterevidence

The versionCode4 exact installed lab-signed release passes four host-controlled flows in each of portrait/default text, portrait200% text and landscape/default text:12 successful executions across four test designs. Each underlying receipt verifies installed APK digest, active font/rotation readback, per-flow success and restoration of prior settings. Flows cover search/no-result/exact-clear, expansion/collapse, destination content and background process-death restoration. Candidate debug Compose coverage remains a separate four-test result.

The original versionCode3 host matrix passed11/12: one landscape search requested frame but observed famer. Follow-up paired fast/slow input probes passed10/10 for the original and10/10 for the candidate; the mismatch cause was not reproduced. Therefore the candidate's finite passing matrix is not proof of causal repair, reliability superiority or speed gain.

The earlier native-swipe instrumentation matrix failed all three configurations and remains failed harness evidence. The interrupted candidate v4 controller run also remains retained: stale IME state and Back navigation invalidated its setup. Final v5 host receipts establish the current scoped pass without retroactively changing those histories. The HTML reader gate, production signing and physical hardware/energy qualification remain incomplete.
