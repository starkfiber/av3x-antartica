# Android systems and service boundaries

**Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** source-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

This chapter extends the startup/Binder lab into ten other subsystem boundaries. **Documented** identifies official architecture descriptions; **observed source** identifies inspected code from a pinned Android 17 base tag; **derived** identifies an explanation drawn from that evidence. No subsystem here has been demonstrated on a running Android image. The selected QPR1 GSI and the `android-17.0.0_r1` source baseline remain different revisions.

The productive way to learn is to follow one request across boundaries. A video call, for example, touches input, graphics, camera, audio, networking, permissions, scheduling and storage. Learning the boundaries lets you ask narrower questions instead of treating all of that as one opaque application.

## 1. Graphics and input: separate the drawing path from the touch path

**Documented.** SurfaceFlinger receives image buffers and layer metadata, composes them and supplies display output. Window management and composition are distinct responsibilities. Input travels in the other direction: hardware events are interpreted and delivered through the input pipeline. An app can receive input yet fail to produce a new buffer, or produce buffers whose presentation is delayed. [Composition architecture](https://source.android.com/docs/core/graphics/surfaceflinger-windowmanager), [input pipeline](https://source.android.com/docs/core/interaction/input).

**Observed source.** The retained SurfaceFlinger entry point starts a Binder thread pool, creates the compositor, initializes it before publishing its services, then enters its run path. It also attempts scheduling-policy changes and has a logging branch for a priority-change failure. This establishes startup order and a failure branch, not successful rendering. [Pinned startup](case://local/android-ios-reverse-lab/epoch-3/android-sources/main_surfaceflinger.cpp:61).

**Derived.** “The screen is frozen” is a symptom spanning several possible boundaries. Distinguish event delivery, app work, buffer production, composition and physical presentation before blaming the GPU. **Next experiment:** correlate input timestamps, frame production and presentation for one controlled interaction; no such trace was collected here.

## 2. Audio, camera and media: control messages accompany continuous data

**Documented.** Audio and camera frameworks meet device-specific implementations through HAL interfaces. Camera HAL documentation describes AIDL support for modern devices while retaining older HIDL compatibility. Media components handle parsing and codecs; the updatable Media Codecs module includes software Codec2 components. [Audio architecture](https://source.android.com/docs/core/audio), [camera HAL](https://source.android.com/docs/core/camera/camera3), [media modules](https://source.android.com/docs/core/media/media-modules).

**Derived.** A useful diagram separates control requests from the stream itself. Opening a camera, configuring an output, starting capture, receiving frames and closing it are different operations. For playback, recognizing a container is different from decoding its tracks and rendering them. A supported filename extension does not establish the exact codec, profile, hardware route or latency.

**Unknown.** We have not inspected this GSI's media binaries or a vendor camera/audio implementation. An emulator or GSI also cannot establish the behavior of a particular sensor, microphone path or hardware codec. **Exercise:** draw where a camera permission failure, unavailable device and decoder failure would occur; do not collapse them into one “media error.”

## 3. Networking: connection, naming and reachability are separate states

**Documented.** The Network Stack module includes IP provisioning/maintenance, DHCP and network validation/captive-portal handling. DNS resolution is a separate modular responsibility that maps names to addresses and provides caching through framework/native entry points. [Network Stack](https://source.android.com/docs/core/ota/modular-system/networking), [DNS Resolver](https://source.android.com/docs/core/ota/modular-system/dns-resolver).

**Derived.** A Wi-Fi association does not by itself prove an app can reach its server. To explain a failure, separate link connectivity, IP configuration, routing, name resolution, policy and the application protocol. A captive portal can produce a working local link with restricted external reachability; a stale or failed name lookup is another distinct boundary.

**Unknown.** We have not captured packet traffic or examined this target's active VPN, DNS, firewall, radio or modem state. **Next experiment:** on an explicitly selected isolated test runtime, record a successful request and a controlled naming failure, then compare the evidence. Packet capture alone would still need attribution to the correct process and request.

## 4. Storage and encryption: a pathname does not establish availability

**Documented.** File-based encryption permits files to use independently unlockable keys. Android distinguishes credential-encrypted storage from device-encrypted storage available during Direct Boot. This lets limited functions operate before the first user unlock while keeping other data unavailable. The volume-management layer manages encrypted keys and storage transitions. [File-based encryption](https://source.android.com/docs/security/features/encryption/file-based).

**Derived.** “The file exists” and “this process can read plaintext now” are different claims. Mount state, user identity, access control and key availability all matter. A system-image dump also generally describes system files, not a particular user's unlocked application data. That distinction prevents a firmware inventory from being mistaken for a complete device-state reconstruction.

**Unknown.** No user-data image, key material, unlock transition or filesystem access was examined here. **Exercise:** design a two-row table for “before first unlock” and “after unlock.” For each proposed file, identify its storage class and the additional evidence required to claim availability. Do not assume the file's directory name alone proves its encryption state.

## 5. Keystore: using a key is not the same as obtaining its bytes

**Documented.** Applications can request cryptographic operations through Android Keystore. The system daemon interfaces with KeyMint; hardware-backed implementations delegate sensitive work into a secure environment. Stored keyblobs and aliases are not necessarily exposed raw secret material. Authentication-bound keys can require an authorization condition before use. [Keystore architecture](https://source.android.com/docs/security/features/keystore).

**Derived.** When reading a call graph, distinguish key creation, key lookup, operation authorization and the cryptographic operation. A caller holding a key identifier does not imply the key can be exported or used for every purpose. Likewise, a Keystore API call does not by itself prove hardware backing for that specific key and device.

**Unknown.** No KeyMint implementation, attestation or actual key operation was tested. **Learning task:** write down what evidence would support each claim: “an alias exists,” “a signing operation succeeded,” “the private key is non-exportable,” and “the operation ran in specified secure hardware.” Each claim requires a different observation.

## 6. HALs and kernels: an interface contract is not the vendor implementation

**Documented.** HALs provide interfaces between platform clients and device-specific services. Modern development uses AIDL, while existing HIDL implementations remain relevant. The Generic Kernel Image project separates common kernel concerns from vendor-specific pieces and provides a more stable boundary for vendor modules. [HAL overview](https://source.android.com/docs/core/architecture/hal), [GKI project](https://source.android.com/docs/core/architecture/kernel/generic-kernel-image).

**Derived.** Knowing an interface method tells you the expected shape of a request, not exactly how a vendor fulfills it. The implementation may cross a driver or firmware boundary that is absent from a generic system image. For reverse engineering, record the interface version, actual service binary, kernel/module build and device identity separately.

**Unknown.** No matched kernel, device tree, vendor partition, modem firmware or peripheral firmware is available in this learning pass. **Exercise:** choose camera capture and mark which parts a generic system image can plausibly reveal and which require a named device's artifacts. Explain why “Android version 17” is an insufficient identity for all those binaries.

## 7. Mainline and APEX: a system version is a set of versions

**Documented.** Mainline permits selected system components to update outside the full platform release cycle. Modules can use APK or APEX containers. APEX accommodates lower-level components that need a different lifecycle from an ordinary app package, including early-boot use. [Mainline](https://source.android.com/docs/core/ota/modular-system), [APEX format and lifecycle](https://source.android.com/docs/core/ota/apex).

**Observed source.** The retained `apexd` entry initializes image/session managers, distinguishes booting from other invocations, registers a Binder service, and then waits for boot status before cleanup in the boot path. Its comments and ordering distinguish activation from later readiness. A failure to obtain the volume checkpoint interface is logged; the code continues initialization with the available pointer state. [Pinned lifecycle](case://local/android-ios-reverse-lab/epoch-3/android-sources/apexd_main.cpp:165).

**Derived.** A base build number alone does not identify every active module. Package version, activation state and relevant update state belong in the evidence manifest. **Unknown:** we have not inspected active APEX mount state or verified module signatures on the selected target.

## 8. OTA updates: successful transfer is only one stage

**Documented.** Virtual A/B uses snapshots for dynamic partitions rather than duplicating every partition in a second physical slot. Changes are merged after a successful boot is confirmed. Its design includes a recovery path when the updated system fails to boot. [Virtual A/B overview](https://source.android.com/docs/core/ota/virtual_ab).

**Derived.** Update analysis should distinguish downloading, validating, preparing new state, booting that state, confirming success and merging/cleanup. A progress bar reaching the end of a download proves none of the later stages. Similarly, finding a payload file does not prove the device accepted or booted it.

**Unknown.** This case has not applied an update or examined a live snapshot/merge state. No rollback or bootability claim follows from this chapter. **Exercise:** build a state diagram with an interruption after each stage. Mark which evidence would distinguish “not installed,” “prepared,” “booted but unconfirmed” and “fully committed.” This is a reasoning exercise; it is not a device-operation instruction.

## 9. Power and jobs: scheduled does not mean immediately runnable

**Documented.** Android can defer background CPU/network work during Doze and apply limits based on app/device state and standby buckets. Work scheduled through frameworks such as WorkManager can be subject to the underlying job limits. Exact quotas evolve and are not universal timing promises. [Platform Doze](https://source.android.com/docs/core/power/platform_mgmt), [resource-limit documentation](https://developer.android.com/topic/performance/power/power-details).

**Derived.** A job can be validly registered yet not currently eligible to run. Separate admission, constraints, quota, process availability, execution and completion. A timer in application code does not prove the OS will keep the process alive or permit the requested network operation at that instant.

**Unknown.** No job history, wake-lock trace, power measurement or thermal state was captured. **Exercise:** for a queued upload, list which evidence could distinguish unmet network constraints, power deferral, exhausted execution allowance, process death and server failure. A single “upload delayed” log message cannot decide among them.

## 10. Permissions: declaration, grant and data delivery are distinct checks

**Documented.** Runtime permissions let users grant or deny access to protected capabilities while an app is running. Permission categories and restrictions affect how access is granted; a manifest request is not itself proof of current access. [Runtime-permission model](https://source.android.com/docs/core/permissions/runtime_perms).

**Observed source.** The retained PermissionChecker wrapper calls its manager with different flags for preflight and actual data delivery. It passes attribution information rather than only a permission name. Its calling-only data-delivery helper explicitly denies the same-process case before using Binder caller identity. [Delivery wrapper](case://local/android-ios-reverse-lab/epoch-3/android-sources/PermissionChecker.java:314), [preflight wrapper](case://local/android-ios-reverse-lab/epoch-3/android-sources/PermissionChecker.java:572), [calling-only boundary](case://local/android-ios-reverse-lab/epoch-3/android-sources/PermissionChecker.java:686).

**Derived.** Caller identity and the time of the check affect what a permission result means. Checking eligibility when a listener registers is not equivalent to checking access when data is delivered. **Unknown:** the downstream manager, a concrete service's use of these helpers and its final state-changing operation still need inspection. A helper's existence cannot prove every caller uses it correctly.

## A practical sequence for learning across subsystems

Start with a narrow user-visible action, such as opening a camera preview. Draw the app, permission/service, HAL and buffer/display boundaries. Then pick one arrow and acquire the exact interface plus implementation used by the selected target. Record the version and hash before drawing conclusions. Follow its failure path as carefully as its success path. Finally, design a controlled observation that could disprove your current explanation.

Use this evidence ladder:

| Evidence | What it can establish | What it cannot establish alone |
| --- | --- | --- |
| Official architecture documentation | Intended roles and interfaces | Exact behavior of a selected binary |
| Pinned source | Branches, calls and declared ordering | Build flags, compiler output or executed path |
| Shipped artifact inspection | Actual packaged bytes and recoverable structure | Every dynamic path or external dependency |
| Controlled runtime observation | What happened in the recorded scenario | All devices, configurations or future runs |

The exercise is complete when you can explain what crosses a boundary, why it exists, how it can fail and which evidence supports the explanation. “All methods decompiled” would not answer those questions by itself.

## Evidence retained and corrections

Three complete source anchors were acquired from official AOSP, identified by commit, and locally hashed. [Provenance](case://local/android-ios-reverse-lab/epoch-3/android-sources/provenance.json) and [verification](case://local/android-ios-reverse-lab/epoch-3/android-sources/verification.json) retain the details. The source tag is Android 17 base r1; QPR1 binary equivalence is unproven. No source was compiled, no APK was executed and no physical device was accessed for this chapter.

The general camera overview contains older HIDL wording; the focused Camera HAL page documents AIDL development beginning with Android 13 and was used for the current explanation. The general media page also contains legacy OpenMAX discussion, so the focused media-modules documentation was used for the Codec2 point. These differences are a reminder to compare scope and age even within official documentation. Two direct Mainline-page opens timed out; official indexed results supplied only the specific Mainline facts cited above.

This chapter is a documented learning map with three concrete source anchors. It is not a claim that graphics, networking, encryption, updates or hardware behavior have been fully reverse engineered.


## Evidence and limits

Study subsystem contracts and selected source anchors without claiming full vendor or runtime coverage.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Pinned source observation does not establish behavior of the selected shipped binary or device.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
