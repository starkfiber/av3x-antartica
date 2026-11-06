# Provenance and bundled material

Articles are case-authored explanations and findings with source links. Public source and platform documentation are attributed where used; their publishers retain their rights. This package does not redistribute firmware, APKs, full third-party manuals, REA, JADX, XNU or OpenHarmony source trees. Case-local evidence locators point outside this package.

The MCP server, search/index helpers, reader and skill are authored for this task. Runtime requires an existing Node installation; no npm modules are bundled. Optional comparison-import tooling consumes the user's research case. The separate validation environment is not shipped.

The source-only Malaysia map starter references MapLibre Native, OpenFreeMap/OpenMapTiles/OpenStreetMap, Nominatim and OSRM. See examples/maze-map/THIRD_PARTY_NOTICES.md for provider URLs, attribution and license boundaries. No map SDK binary, tile dataset, private key or APK is bundled in the plugin; Gradle resolves declared dependencies when the user builds.

Public release: original plugin code, original articles and original assets are MIT licensed under LICENSE. Historical private absolute paths are replaced by case://local/ locators; raw cases are not redistributed. Third-party documents and linked projects retain their own rights. Comparison links do not imply incorporated code or endorsement.

The examples include standard Gradle wrapper JARs from Gradle (Apache-2.0): https://github.com/gradle/gradle/blob/master/LICENSE . Android libraries are resolved by the user's build, not bundled SDK binaries. The full Apache-2.0 text is included under licenses/Apache-2.0.txt. OSM-derived fixture/data content remains subject to its own terms and attribution.
