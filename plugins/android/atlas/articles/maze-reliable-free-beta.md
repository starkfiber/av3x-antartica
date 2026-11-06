# Maze revision 02: free users and explicit service reliability

2026-11-06. Native Compose/MapLibre development; emulator-only qualification. This revises the Maze0.1 starter, not the historical receipts.

## Service-policy correction

The old generated starter embedded the public Nominatim endpoint. Its current policy forbids generic generated place-search integration through no-code/low-code/vibe-coding platforms without the developer's deliberate informed responsibility. Its application-wide rate limit is not a per-phone allowance; autocomplete is forbidden. A proxy is not a policy exemption. The current starter removes that endpoint and does not silently substitute another public demo service.

The production variant requires an explicitly reviewed credential-free HTTPS gateway. Provider credentials belong on the server. Release rejects fixture metadata and does not permit loopback cleartext. The separately identified `.lab` variant uses an owned loopback fixture server; the app permanently labels sample search and recorded routes. The fixture server performs no upstream requests and only has one recorded road pair. It is not production infrastructure or current traffic.

## UI and recovery contracts

Day/Night/System themes, compact/expanded trip details, source/time labels, local saved trips and a bounded small-map download are implemented with public Android APIs. Attribution remains visible in compact mode. Settings sheets need a bounded viewport and an accessible close path; the first native test caught an unreachable close action, retained as a failure and repaired.

Search and routes use finite connection/read/call timeouts, cancellation, bounded response bodies, at most one automatic transient GET retry and rate-limit cooldown. After repeated failures a short circuit prevents repeated network work. Invalid road data never becomes a straight-line driving route. Existing/saved geometry remains explicitly a preview, not a fresh result. A checksum detects accidental saved-file corruption, not adversarial tampering or authenticity.

Up to four trips are saved only by explicit action, on-device, with backup excluded. Map downloads are separate from saved route geometry and steps. Native region completion, bounds, style and zoom range matter; cached screen tiles alone do not establish an offline map. OpenFreeMap's published cap is 3,000 tiles or50MB per download, four concurrent requests and an identifying User-Agent. The app stops downloads on observed status limits; this is not a byte-perfect transport quota.

Night-map review caught route occlusion by later road layers. The route is inserted after the last road/fill layer and below subsequent symbol labels where available; choosing the first symbol layer is not sufficient for every style. This is an app integration fix, not a newly discovered OS bug.

## Free to users is a funding decision

The user chose free access with a budget for reliable services. Select licensed or independently hosted mapping/search/routing, an owned switchable gateway, monitoring, request/spend limits and support. Derive costs from measured usage and provider contracts. No account, cloud purchase or deployment was performed. Proposed service objectives are planning targets, not achieved availability evidence.

The release uses target API36, matching the current phone-app submission floor. A built bundle is not a signed/published product. Publisher identity, protected upload key, Play account, privacy/Data safety review, account-specific testing, physical devices, broader accessibility and staged rollout remain publication gates. See examples/maze-map/PUBLISHING.md. No zero-failure guarantee, FPS improvement, energy saving or superiority to another builder is established by these changes.

## Evidence and scope

Project-local revision evidence is under case://local/MazeMalaysiaWay/research/revision-02. Read its RESULTS.md and runtime receipts for the exact APK digest, observed journeys and failed attempts. Debug JVM tests, minified APK runtime, source scaffold compilation and production-service deployment are separate proof levels. The prior HTML reader rendered/keyboard/accessibility gate remains open; native app testing does not close it.
