# OpenHarmony UIAbility lifecycle source trace



The source acquisition resolves `OpenHarmony-v6.0-Release` through annotated tag object `6635e85923c99e97ffcf2dd821d8a817c14f12f8` to commit `74e01f0c15715f404a80c802aaebb958cf25b908` in `openharmony/ability_ability_runtime`. The complete 54,095-byte file is retained as `ui_ability.cpp`; SHA-256 is `1e3c863dd88775124dd3bb29f0db011b79111a28c509100366486a17f4a0db17`. [Pinned upstream file](https://github.com/openharmony/ability_ability_runtime/blob/74e01f0c15715f404a80c802aaebb958cf25b908/frameworks/native/ability/native/ui_ability.cpp).

**Observed source:** `UIAbility::Create`, line62, selects a concrete ability by runtime language. JS uses `JsUIAbility::Create`; ETS calls `CreateETSUIAbility`; CJ support is guarded by `CJ_FRONTEND`. Null runtime and the default branch create the base UIAbility. This directly demonstrates configuration-sensitive dispatch, not one universal language path.

`OnStart`, line220, first checks ability metadata and returns if absent. It records the Want, conditionally initializes screen-related state under `SUPPORT_SCREEN`, checks the lifecycle executor, then dispatches `STARTED_NEW`. A further null check precedes the `ON_START` lifecycle event. A function entry alone therefore does not prove the event is dispatched.

`OnForeground`, line807, calls `DoOnForeground` before checking silent foreground. The silent branch returns without the subsequent `DispatchLifecycleOnForeground` call. `OnBackground`, line830, checks metadata, backgrounds a present scene, and conditionally schedules state saving only when recovery/context/restore checks pass. It then checks the executor before changing lifecycle state. `OnStop`, line256, conditionally destroys a scene, validates the executor and lifecycle objects, and dispatches stop state/event. These branches make good exercises in separating optional behavior from unconditional behavior.

**Documentation versus source:** the current [UIAbility lifecycle guide](https://github.com/openharmony/docs/blob/master/en/application-dev/application-models/uiability-lifecycle.md) describes app callbacks such as onCreate/onForeground/onBackground/onDestroy. The native methods above are lower-level implementation evidence; this single file does not trace all binding code to those ArkTS callbacks. Android Activity lifecycle callbacks and Harmony UIAbility callbacks address comparable app-lifetime concerns, but are not drop-in semantic equivalents. No application was launched to validate either sequence.



## Scope

Pinned OpenHarmony6.0 public source; not a commercial Huawei binary or runtime trace. See huawei-platform-families for the wider comparison.
