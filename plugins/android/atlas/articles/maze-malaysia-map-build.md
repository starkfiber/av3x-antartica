# Maze: from an Android scaffold to a real map

Historical Maze0.1 evidence. Revision02 replaces default public search/routing with explicit gateway configuration and labelled local lab fixtures. Read `maze-reliable-free-beta` before scaffolding. The earlier generic public Nominatim default was incorrect for the current policy and has been removed from the template.

Maze Malaysia Way is an independently implemented native Kotlin/Compose map prototype. MapLibre Native 13.7.1 OpenGL renders provider-hosted OpenFreeMap vector tiles; Nominatim resolves explicit Malaysian place searches; OSRM returns road geometry and route estimates. No Google/Waze assets, private APIs, traffic feed or source code are included. The user's final direction for this slice was a pure maps experience.

## What was actually demonstrated

The minified lab-signed release SHA256 is 66818ee2f3ccef4e4ab4b6c57292c3372a7c9f292076ebc0353f7b9093a78a10. All170 non-signature ZIP payload entries equal the unsigned release. The signer is the existing Android Debug laboratory identity, not a production publisher. Four ABI-specific libmaplibre.so members are present.

Eight owned API35 / ARM64 emulator journeys pass: local search/no-result; actual road route/alternatives/directions; denied location with manual planning; offline route failure without invented geometry; landscape controls;200% text; explicit online Malaysian search; selected online destination restoration after verified background process death. Six local data tests pass. Raw receipts bind the installed APK digest, exact AV3X_Antartica_QA/emulator-5580/ADB5041 target and restored display/network settings.

The observed KL Sentral→KLCC service result was9.9km/15min with an alternative estimate16min. These are provider road estimates, not observed trip times, current traffic or a route-quality victory. Native logs identify the Android Emulator OpenGL ES Translator on Apple M5; that is not presented-FPS, energy or native-macOS APK proof.

## Scaffold a separate native map project

The bundled source is examples/maze-map. From plugin root use:

```sh
node scripts/scaffold.mjs --template maps --output /absolute/new-project --application-id my.example.maps --name 'My Maps'
```

The CLI requires an explicit empty/absent output, validates identity strings, rejects output symlinks/nonempty directories and copies no SDK paths, keys, build outputs or APKs. It creates source only; it does not execute Gradle, start an emulator, install an app or apply a guard proposal. The13 MCP tools remain read-only. Configure the user's SDK/JDK17, review the generated source/providers and build separately. Branding/icon and Malaysia-focused content remain starter assets to customize.

One local warm-cache source-copy CLI run took 36.10ms, including process startup; a renamed starter compiled in 7 seconds with dependencies cached. These are narrow local measurements, not end-to-end delivery promises or a head-to-head AI benchmark. The prototype's initial clean build took 42seconds and first optimized build 40seconds after source authoring.

## Foundations that matter here

Rendering, place lookup, road graphs, routing weights, GPS/guidance and traffic/community services are separate subsystems. Use an existing native renderer rather than rebuilding one for the app. Keep language-model inference off the pan/zoom/frame path. Never substitute a straight connector when road routing fails. Ordinary SDK apps still need permissions and remain sandboxed; OS knowledge grants no platform-signing privilege.

Waterfall organizes mostly sequential stages. RAD rapidly builds and tests working prototypes. This slice uses iterative/RAD work: explicit contract→reviewed scaffold→real data→working journey→failure/lifecycle checks→artifact identity. A full3D game similarly needs an actual game engine, assets and gameplay; Blender is an asset/animation tool, not an automatic finished-game generator.

## Retained failures and boundaries

Runtime caught MapLibre HTTP setup before SDK initialization. Initialization order was corrected. Resource lint caught an API27 theme item under minSdk26. Rendered review caught route-endpoint overlap because header measurement excluded outer insets; it was corrected before final8-journey qualification. Initial large-APK hash capture exceeded a controller buffer; final verification uses on-device SHA256. Failures are retained rather than retroactively relabeled as passing.

REA public-web work remains scoped: Waze five same-origin scripts; Google Maps15 retained source records with13 complete/two unavailable. Route-pattern inventories found zero candidates for their selected captures; complete application route sets remain unknown. Private Android clients/server algorithms, live traffic and whole-product fidelity are not established. Connected JADX was unconfigured; archive inspection is not decompilation. The legacy coverage helper does not certify these browser/Android schemas.

Production service provisioning/signing, active navigation/rerouting, offline regions, broader address recall, full assistive testing, physical devices and render/power budgets remain separate work. The older HTML reader rendered/keyboard/assistive gate is still open. Google AI Studio also supports native app generation; no superiority over it, Gemini, Stitch or other builders has been established.
