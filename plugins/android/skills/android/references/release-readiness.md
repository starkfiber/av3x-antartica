# Scoped Android release readiness

Official guidance checked2026-11-06. Separate a local plugin release, a native app release and an operating-system research claim. A successful app build does not qualify a kernel/firmware release, and source documentation does not prove runtime compatibility.

## Define the deliverable and preserve its identity

Name the audience, supported OS/device matrix, application ID, version and distribution channel. Preserve project constraints and intentional behavior. Finish authorized local implementation and validation without introducing unnecessary approval stops. Publishing, production signing custody and unrelated device operations require matching task authorization; they are not implied by a compile check.

For a plugin, verify the packaged files/digests, corpus/tool schemas, bounded input handling, fresh-process loading and regressions against previously reproduced defects. Test the final installable package as well as the working tree. Distinguish MCP transport, browser-reader and host integration results.

## Build and inspect the actual app artifact

Use the release variant with a compatible pinned toolchain. Check final merged manifest and APK: ID/version, min/targetSDK, debuggability, exported components, permissions, resources and native ABIs where present. Confirm debug runtime tooling is excluded using the resolved release dependency graph and packaged contents. Preview annotations are not equivalent to a runtime debugger; nevertheless keep preview-only dependencies/source out of production when unnecessary. If R8/resource shrinking is enabled, test that exact output and preserve required mappings. Never infer minification from the word “release.”

Record artifact SHA-256 and signature/certificate fingerprint after signing. Keep private keys/passwords out of the project, logs and release bundle. Lab-generated signing establishes a laboratory identity, not a production trust/custody decision. Same-ID updates require appropriate certificate continuity and versioning; do not erase user data to work around signer conflicts. Distinguish Play upload keys from app-signing keys and directly signed APK distribution. [Signing](https://developer.android.com/studio/publish/app-signing), [release preparation](https://developer.android.com/studio/publish/preparing).

## Test the real feature and failure boundaries

Choose meaningful user journeys: search including empty/no-results/case/clear; navigation/back; selection identity; state restoration; offline/retry when networking exists; missing external intent handler; accessibility and window/keyboard behavior. Configuration recreation, background return and process recreation are separate tests. Force-stop/relaunch does not by itself prove saved-state restoration.

Test permission denial/revocation only for capabilities the final app actually requests. A no-permission teaching app needs manifest verification, not fabricated camera/location tests. Verify exported entry points and input handling consistent with their purpose. Static analysis, unit tests and device UI tests each prove different things. Keep assertions tied to user-observable behavior instead of merely mirroring implementation.

## Report performance honestly

Use supported non-debuggable/profileable benchmark configurations for release claims, matched quality/workload and repeated measurements. Emulator/debug results can guide local investigation but do not establish physical-device speed, battery life or thermal behavior. Startup latency is not FPS; a static interface does not need continuous redraw. Do not suppress benchmark environment warnings without retaining them in the report. [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview).

## Close with evidence, not a blanket badge

Record passed/failed/not-run/not-applicable per scoped gate, artifact digests, actual targets and evidence paths. Describe a locally signed candidate as such. Production distribution may additionally require channel policy, developer identity, privacy disclosures, signing custody and physical-device validation appropriate to the claims; verify current requirements for the chosen channel rather than inventing a universal checklist.

Do not claim a complete OS, guaranteed zero lag, unbrickability, battery improvement or hardware qualification from an app/plugin release. Complete the authorized local work and clearly identify the remaining external or unmeasured boundary.

## AV3X Antartica emulator milestone: interpretation guide

For the retained epoch13 release case, keep three artifacts distinct: the unsigned minified release APK, the separately configured benchmark APK, and a lab-signed copy of the exact release payload. The independent audit compares all76 nonsignature ZIP entries of the unsigned release and lab-signed copy, including META-INF runtime/service metadata. Equality means decompressed payload parity; APK container/signing-block bytes and whole-file hashes differ. A benchmark variant with a profileable manifest is not automatically payload-identical to release.

The lab-signed release copy remains a laboratory distribution identity. A non-debuggable app can still be signed with an Android Debug certificate; debuggability and signer identity are independent properties. Production signing, custody and publication are not complete merely because local signature verification succeeds.

Treat the four debug Compose instrumentation passes as debug-target coverage: search, expansion, independent tab scroll state and activity recreation. The separate self-instrumenting black-box suite targets the untouched minified release payload. Its four methods cover search/clear, expansion/collapse, destinations and background process death with explicit PID-disappearance verification before restoring the task. A planned three-configuration run is four tests × three configurations, not12 distinct test designs. Claim that matrix only after each raw run passes and settings readback identifies portrait/default text, portrait200% text and landscape/default text. Retain earlier failures and explain any harness correction; test-harness recovery is not automatically an app performance fix.

These Android test results do not close the separate HTML reader gate. Rendered layout, native keyboard traversal and assistive-technology acceptance remain blocked where the supported access route was denied. Existing DOM checks and a queued-open response are not rendered proof. Do not bypass the restriction through an alternate route.

A bounded emulator milestone can be reported complete while production signing, physical hardware, power/thermal behavior and the reader gate remain explicitly incomplete. Do not upgrade that scoped result to a blanket production-ready or OS-complete claim.

### Later epoch13 route correction

The native-swipe instrumentation matrix subsequently completed with all three configurations failed. Preserve it as failed harness evidence; do not leave it pending or infer an app defect solely from that outcome. A separate host controller corrected element discovery/input targeting: fitting content need not be scrollable, so bounded app-content fallback and clickable-parent targeting are used. Its portrait-v2 receipt independently passes four flows with installed-payload digest and settings-restoration readback. Other host configurations require their own completed receipts before claiming a full matrix. These host-controller results are distinct from both the failed instrumentation matrix and the four debug Compose passes. A successful harness correction is not itself an application optimization or proof that every earlier failure had one cause.
