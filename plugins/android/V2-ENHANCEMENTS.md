Historical milestone document. For current public scope, read [STATUS](docs/STATUS.md).

# V2: ten implemented development enhancements

V2 expands Android Atlas into an SDK-native development companion. It does not modify Google's AndroidX libraries or the operating system. The supported scope is project inspection, task guidance, implementation workflow, reference retrieval and static-investigation planning; app build/runtime outcomes require separate evidence.

| # | Enhancement | Concrete delivery | Verification |
| --- | --- | --- | --- |
| 1 | SDK/toolchain inventory | android_sdk_doctor; explicit SDK directory, platforms/build tools/NDK/JDK directories | Real and isolated SDK inventory; no executable compatibility claim |
| 2 | Existing-project inspection | android_inspect_project scans Gradle, manifests, catalogs and CMake/ndk-build metadata | Mixed module, manifest, symlink, depth and ndk-build fixtures |
| 3 | SDK-aware development routing | android_development_plan routes UI/state/data/security/performance/build work | Routes exercised against representative tasks; public/privileged API boundary retained |
| 4 | Native AndroidX UI workflow | Compose/Views interoperability, adaptive layouts, accessibility and native controls reference | Dated official sources and real Compose demo source |
| 5 | Lifecycle and data architecture | ViewModel/state, lifecycle collection, restoration, repositories, Room/DataStore and background work guidance | Reference and demo SavedStateHandle; broader persistence paths are guidance, not implemented demo features |
| 6 | Permissions/security workflow | Denial/revocation, exported components, scoped storage and credential handling | Inspector declaration extraction plus task-specific checks; no vulnerability claim |
| 7 | App acceptance planning | android_acceptance_plan separates configuration, compilation, logic, UI, performance and release proof | Routing/validation tests; stages do not pass themselves |
| 8 | CPU/GPU performance workflow | Trace-first startup, jank, pacing, Macrobenchmark/Baseline Profile guidance | Dated reference plus illustrative demo; no measured improvement claim |
| 9 | REA-to-development bridge | android_rea_plan maps selected APK/native artifact to bounded analysis steps | Validated explicit-file planning; actual analysis requires available REA provider |
| 10 | Native demonstration and preview | Kotlin AndroidX Compose Material3 learning app and conceptual design preview | Build/runtime results retained separately in project/case; concept image is not an app screenshot |

Five reference MCP tools plus five development tools are available. CLI fallback: scripts/develop.mjs. Ten means ten delivered capabilities, not ten proven improvements to Android itself.

Known acceptance limits from v1 remain explicit: actual Codex-chat discovery, rendered-reader/accessibility verification and full sentence-by-sentence claim audit are not established by this development expansion. Stable whole-product acceptance must not be inferred from the V2 feature count.
