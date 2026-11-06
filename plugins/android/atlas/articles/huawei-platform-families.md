# Android, Huawei commercial systems, and OpenHarmony

Evidence checked 6 November 2026. **This comparison contains official documentation and one inspected OpenHarmony source file. It contains no Huawei firmware extraction, running-device observation, benchmark, or demonstrated APK compatibility.** Android mechanisms refer to the separately retained Android chapters and their explicit r1-source/QPR1-image mismatch. Names must be resolved to a product, region, build and device before transferring a claim.

## 1. Four names that must stay separate

| Name | What this investigation can establish | What it cannot establish |
|---|---|---|
| EMUI | Huawei's regional Pura 80 Ultra specification lists EMUI 15.0. Huawei support groups EMUI and pre-5 HarmonyOS under guidance that discusses Android app OS-version requirements. | An EMUI marketing version does not identify an exact Android security patch, ABI, kernel configuration or Google-services availability. |
| Legacy HarmonyOS | Official support explicitly distinguishes versions below HarmonyOS 5 and discusses Android compatibility requirements in that scope. This supports treating those products separately from the newer native app platform. | A universal statement about all watches, televisions, phones and builds carrying the HarmonyOS name. |
| HarmonyOS NEXT / newer commercial HarmonyOS | NEXT remains a developer-portal name. Current Huawei consumer documentation identifies HarmonyOS 7, announced 7 September 2026, with a mainland-China upgrade scope. | Calling NEXT a single permanent release number, or assuming every Huawei phone worldwide now uses it. |
| OpenHarmony | An OpenAtom open-source project with modular system profiles and public repositories. Huawei describes its commercial HarmonyOS7 as developed from OpenHarmony. | Equating public OpenHarmony with the complete commercial Huawei OS, its private services, shipped drivers, security implementations or exact kernel. |

Sources: [regional EMUI specification](https://consumer.huawei.com/levant/phones/pura80-ultra/specs/), [legacy-scoped support](https://consumer.huawei.com/cn/support/content/zh-cn00445299/), [NEXT developer portal](https://developer.huawei.com/consumer/en/harmonyos/develop/), [current commercial FAQ](https://consumer.huawei.com/cn/support/content/zh-cn16103427/).

The [HarmonyOS7 model page](https://consumer.huawei.com/cn/support/harmonyos/models-7/) distinguishes formal releases, public beta and other trial groups; inclusion is not proof that a particular physical unit has upgraded. The retrieved OpenHarmony release index lists 6.1 dated 8 March 2026 and 6.0.0.2 dated 24 March 2026. However, the linked 6.1 release document returned HTTP404. Therefore this report does not certify the newest complete OpenHarmony release. Its source anchor deliberately uses a resolvable **6.0 Release** tag, not a guessed equivalent of commercial HarmonyOS7.

## 2. CPU scheduling and runtime solve different problems

Android's ART executes managed app code and its JIT/AOT machinery produces or selects executable CPU code; Linux schedules runnable threads. Compilation policy and scheduler policy interact through workload but are different layers. Read the retained epoch-4 Android chapter for source-level details rather than treating an OS brand as a scheduler description.

OpenHarmony's [overview](https://github.com/openharmony/docs/blob/master/en/OpenHarmony-Overview.md) describes Linux or LiteOS selected for resource classes, with a kernel abstraction layer. This is documentation about a configurable open project, not evidence that every commercial Huawei device uses Linux or that every device runs the same microkernel. Its [ETS runtime repository](https://github.com/openharmony/arkcompiler_ets_runtime/blob/master/README.md) documents ECMAScript libraries, native C++ APIs and garbage collectors. ArkTS/ArkCompiler names must not be substituted for ART/Dex: these are different language, runtime and tooling contracts.

**Derived implication:** identical ARM instruction-set support does not make Android and HarmonyOS app binaries interchangeable. A native library also depends on ABI, loader, linked libraries and platform services. Neither compiler branding nor a “distributed” architecture establishes higher CPU speed, lower power use, deterministic scheduling or hard realtime deadlines. Those need matched workloads and measurements.

## 3. A concrete lifecycle anchor, including failure paths

The source acquisition resolves `OpenHarmony-v6.0-Release` through annotated tag object `6635e85923c99e97ffcf2dd821d8a817c14f12f8` to commit `74e01f0c15715f404a80c802aaebb958cf25b908` in `openharmony/ability_ability_runtime`. The complete 54,095-byte file is retained as `ui_ability.cpp`; SHA-256 is `1e3c863dd88775124dd3bb29f0db011b79111a28c509100366486a17f4a0db17`. [Pinned upstream file](https://github.com/openharmony/ability_ability_runtime/blob/74e01f0c15715f404a80c802aaebb958cf25b908/frameworks/native/ability/native/ui_ability.cpp).

**Observed source:** `UIAbility::Create`, line62, selects a concrete ability by runtime language. JS uses `JsUIAbility::Create`; ETS calls `CreateETSUIAbility`; CJ support is guarded by `CJ_FRONTEND`. Null runtime and the default branch create the base UIAbility. This directly demonstrates configuration-sensitive dispatch, not one universal language path.

`OnStart`, line220, first checks ability metadata and returns if absent. It records the Want, conditionally initializes screen-related state under `SUPPORT_SCREEN`, checks the lifecycle executor, then dispatches `STARTED_NEW`. A further null check precedes the `ON_START` lifecycle event. A function entry alone therefore does not prove the event is dispatched.

`OnForeground`, line807, calls `DoOnForeground` before checking silent foreground. The silent branch returns without the subsequent `DispatchLifecycleOnForeground` call. `OnBackground`, line830, checks metadata, backgrounds a present scene, and conditionally schedules state saving only when recovery/context/restore checks pass. It then checks the executor before changing lifecycle state. `OnStop`, line256, conditionally destroys a scene, validates the executor and lifecycle objects, and dispatches stop state/event. These branches make good exercises in separating optional behavior from unconditional behavior.

**Documentation versus source:** the current [UIAbility lifecycle guide](https://github.com/openharmony/docs/blob/master/en/application-dev/application-models/uiability-lifecycle.md) describes app callbacks such as onCreate/onForeground/onBackground/onDestroy. The native methods above are lower-level implementation evidence; this single file does not trace all binding code to those ArkTS callbacks. Android Activity lifecycle callbacks and Harmony UIAbility callbacks address comparable app-lifetime concerns, but are not drop-in semantic equivalents. No application was launched to validate either sequence.

## 4. Graphics: comparable duties, different contracts

Android's retained evidence follows BufferQueue ownership, synchronization fences and SurfaceFlinger composition. OpenHarmony's [graphics repository overview](https://github.com/openharmony/graphic_graphic_2d/blob/master/README.md) describes native drawing interfaces and a Render Service layer that processes ArkUI descriptions into rendering information, alongside drawing, animation, effects, display and memory functions. This establishes the documented division of responsibility, not the exact GPU command stream in a Huawei phone.

Both systems must coordinate CPU-produced scene information, GPU resources, completed buffers and display timing. Similar jobs do not imply identical queue policies, shader compilers, driver behavior or frame pacing. A Vulkan-capable device is not automatically compatible with every Android game: surface creation, libraries, input, audio, assets, permissions, store services and lifecycle integration also matter. No FPS winner can be inferred from these documents.

## 5. Packages and app compatibility

OpenHarmony's [package guide](https://github.com/openharmony/docs/blob/master/en/application-dev/quick-start/application-package-overview.md) distinguishes HAP application modules, HAR build-time library reuse and HSP runtime sharing. HAP is an installation unit; an application can contain multiple HAPs. Android APK/AAB packaging has different manifests, delivery and runtime contracts. Renaming an APK to HAP does not translate code, permissions, resources or service dependencies.

For porting, separate reusable domain logic from the platform shell: lifecycle, UI, storage, networking, notifications, payments, maps and identity each need supported APIs. Native C/C++ may be reusable after rebuilding and replacing incompatible dependencies; this is a project-specific possibility, not a compatibility guarantee. Android Google services and Huawei services must be evaluated independently of whether application code starts.

Avoid the opposite overclaim that no Android-origin app can ever appear on newer HarmonyOS. Huawei's [current file-management support](https://consumer.huawei.com/cn/support/content/zh-cn16071232/) explicitly mentions applications inside Zhuoyitong/Chujingyi and compatible-app data directories on HarmonyOS5+. That documents a compatibility-product context. It does **not** prove that arbitrary APKs are native Harmony applications, that these products work on every model, or what emulation/container implementation they use. This case did not inspect those products or test an APK. The correct comparison separates native app support, third-party compatibility environments and an individual app's observed behavior.

## 6. Distributed architecture is a stack of services

The [DSoftBus repository](https://github.com/openharmony/communication_dsoftbus/blob/master/README.md) describes discovery, connection, networking and data transport across nearby devices while abstracting communication links. The broader project documents distributed data management and scheduling as additional components. A useful conceptual sequence is discover a candidate device, establish an authorized relationship, connect a transport, transfer data and coordinate an application operation. This is an explanatory decomposition, not a captured trace.

“Distributed” does not mean a process automatically runs transparently across every CPU in a room, a remote device bypasses permissions, or all state remains consistent during disconnection. Device identity, authentication, permissions, version negotiation, latency and failure handling remain explicit engineering concerns. Android also offers cross-device APIs and vendor ecosystems; the defensible difference here is OpenHarmony's documented subsystem organization, not a claim that Android lacks networking or collaboration.

## 7. A learning experiment without inventing results

Start with the pinned UIAbility file: mark each early return, compile-time gate and lifecycle dispatch. Compare its responsibilities with Android ActivityThread and Activity lifecycle dispatch in epoch2. Next draw two package dependency graphs: HAP/HAR/HSP and APK plus native libraries. Finally map graphics producers, consumers and completion signals, keeping documentation distinct from observed source.

For a future runtime comparison, first identify specific test hardware, region, OS build, application versions and available tracing APIs. Measure startup, missed frames, memory pressure, background recovery and a network-disconnection scenario under equivalent workloads. No device was selected or operated here. No Huawei firmware or APK/HAP was acquired; all downloads are small source/documentation files. Failed documentation paths are retained in `sources.json` rather than silently replaced with unsupported claims.


## Import provenance

Case-authored snapshot: case://local/android-ios-reverse-lab/epoch-5/huawei/comparison.md
No new runtime validation is implied by packaging this article.
