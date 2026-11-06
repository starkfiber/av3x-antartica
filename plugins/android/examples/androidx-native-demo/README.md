# Android Atlas — native AndroidX demonstration

A small Kotlin Android app using Jetpack Compose Material 3. Five embedded lessons, searchable expandable cards, Learn / Lab / Evidence bottom navigation, ViewModel + SavedStateHandle state, and lifecycle-aware state collection. The Lab computes an explicitly unmeasured single-frame CPU → GPU → next-refresh model. It does not sample hardware or compare Android/iOS speed. No network permission, device discovery or REA execution is included. Source buttons open official references in the user's browser.

This is a native project, separate from the Codex plugin and any browser mock. The embedded lessons are a curated demonstration, not a live import of the full atlas. `@Preview` provides a Compose design preview after dependency resolution. A real debug APK was built successfully and Android lint completed with zero errors. No native rendered preview or device execution is claimed.

## Pinned toolchain

| Component | Pin |
|---|---|
| AGP | 8.11.0 |
| Gradle distribution | 8.14.3, official distribution SHA-256 in wrapper properties |
| Java | 17 |
| Kotlin / Compose compiler plugin | 2.1.20 / 2.1.20 |
| Compose BOM | 2025.04.01 |
| Activity Compose | 1.10.1 |
| Lifecycle Compose / SavedState | 2.8.7 |
| compileSdk / targetSdk | 36 / 36 |
| Build Tools / minSdk | 35.0.0 / 26 |

These are deliberate stable historical pins, not claims of the newest release. The official AGP 8.11 table supports API 36 and requires at least Gradle 8.13 / JDK 17. Kotlin's Compose compiler plugin matches the Kotlin version; the Compose BOM controls library versions separately.

## Build

Prerequisites: an existing JDK 17 and Android SDK containing `platforms;android-36` and `build-tools;35.0.0`. Configure the existing SDK in Android Studio, or set `ANDROID_HOME` to its actual directory. Do not point it at the empty default SDK directory. First dependency resolution requires network access. These commands build only and do not operate any device:

```sh
cd examples/androidx-native-demo
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
# Configure local.properties or ANDROID_HOME for your own installed SDK.
./gradlew --no-daemon :app:assembleDebug
./gradlew --no-daemon :app:lintDebug
```

On a different machine replace the checkout/JDK paths. The expected output after a successful build is `app/build/outputs/apk/debug/app-debug.apk`; it was produced by the verified build. In Android Studio, open this root project, select JDK 17, sync and open `MainActivity.kt`'s `LearnPreview`. Selecting an emulator or physical deployment target is a separate action; none was selected or operated here.

## Actual validation and blocker

JDK 17.0.20.1 and a cached Gradle 8.14.3 executable were available. A plain bootstrap project generated the standard wrapper scripts/JAR initially targeting 8.11.1; its distribution was subsequently updated to the available 8.14.3 with a fresh official checksum. That wrapper-task success is **not** an Android build. The distribution checksum was retrieved from Gradle's official `.sha256` endpoint.

A bounded offline `:app:assembleDebug` attempt using the existing Gradle 8.14.3 failed during plugin resolution: AGP 8.9.2 was not cached. The failed build log remains in the unbundled private case. Kotlin compilation was never reached. That initial pin was then revised to cached AGP 8.11.0 / Gradle 8.14.3. The default SDK has no platform/build-tools installation; two legacy SDK paths supplied from stale local.properties files also did not exist. A corrected existing SDK contained API36 and Build Tools35.0.0. The final assemble/lint run used that SDK and succeeded. No SDK, licenses or emulator were installed and no ADB command ran. TalkBack, rotation/process restoration and on-device UI behavior remain unverified.

## Implementation notes

Material controls supply native accessibility roles and touch target defaults. Search and sliders have labels; section titles expose heading semantics; bottom navigation exposes selected state and decorative numbers are hidden from accessibility. Content scrolls and allows wrapping; no fixed text heights. These design choices still require TalkBack, large-font, landscape and keyboard testing. English text is embedded for this demo; localization remains work for a production app.

UI updates flow through an immutable state object owned by a ViewModel. SavedStateHandle retains small query/tab/selection/model inputs across supported state restoration. No durable user progress is claimed. The single-frame model assumes serial CPU/GPU work, zero compositor cost and exact refresh boundaries; it omits buffering, scanout, thermal policy and contention. It is intentionally narrower than a real engine pipeline.

## Primary references consulted, 2026-11-06

- [AGP 8.11 compatibility](https://developer.android.com/build/releases/agp-8-11-0-release-notes)
- [Compose April 2025 BOM release](https://android-developers.googleblog.com/2025/04/whats-new-in-jetpack-compose-april-25.html)
- [Compose compiler setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler)
- [Activity release history, including 1.10.1](https://developer.android.com/jetpack/androidx/releases/activity)
- [Lifecycle release history, including 2.8.7](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [Kotlin 2.1.20](https://kotlinlang.org/docs/whatsnew2120.html)
- [Gradle 8.14.3 distribution checksum](https://services.gradle.org/distributions/gradle-8.14.3-bin.zip.sha256)

## Final build receipt

`JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew --no-daemon :app:assembleDebug :app:lintDebug` exited 0: BUILD SUCCESSFUL in 3m 19s, 46 tasks executed. The original logs remain in the unbundled private case. Kotlin compiled; APK packaging completed. Lint reports 0 errors and 8 warnings: six notices about deliberately pinned older versions, one missing application icon, and one Android 12+ backup-rules recommendation. These two demo packaging gaps remain; no production-ready release claim is made. A native-library debug-symbol stripping notice and Gradle deprecation notice also remain.

APK: `app/build/outputs/apk/debug/app-debug.apk`, 10,122,795 bytes, SHA-256 `52ef715a78f1cc86d079bffcbc67cc144ca7710a2bcf8d4763bc2d57a5562f92`. It is a debug artifact, not a signed store release. Root performs separate static REA inspection; this document does not assert its result.

For a portable source copy exclude `.gradle/`, `.kotlin/`, `build/`, `app/build/`, `local.properties` and machine-specific logs if not needed. Keep the standard wrapper scripts/JAR, pinned properties, Kotlin source, manifests/resources and Gradle build files. The copied project needs its own valid SDK path.
