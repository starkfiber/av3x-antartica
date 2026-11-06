# Maze: free to users, funded reliability

Decision: 2026-11-06. Prepare an emulator beta now; no Play account, publication, purchasing or cloud deployment authorized for this milestone.

## What is built and what remains

The native app is an independent map and road-planning preview. It is not active turn-by-turn navigation, live traffic, emergency routing or a reconstructed Google/Waze backend. The lab variant permanently labels controlled sample search and recorded routes. The release variant has no default search/routing gateway and rejects fixture responses. API 36 is targeted; that meets the current target-level floor but is not store approval.

## A service we can fund

Keep the user app free. Pay for a reviewed commercial or independently hosted geocoder, routing engine and map delivery with suitable usage rights, Malaysian coverage and an agreed service level. Use one owned HTTPS gateway to adapt providers, keep credentials on the server, limit traffic and switch providers without replacing the app. Do not silently send coordinates to a second provider: disclose service recipients and minimization before enabling it.

Budget from measured active users × searches/routes/map traffic plus storage, monitoring, bandwidth and on-call support. Do not invent a monthly price before choosing a provider and measuring beta usage. Set spend alerts and request limits with an honest degraded mode. An exhausted budget must not produce fabricated roads.

Proposed beta objectives, not achieved measurements: 99.9% monthly gateway availability; p95 successful route latency below 3 seconds for a declared Malaysia workload. Provider freshness, actual road correctness and regional coverage are separate checks. Measure successful useful requests, malformed data, latency, cooldowns and fallback use with minimized logs; do not retain exact location trails by default.

## Failure behavior

At most one automatic retry for transient GET failure, with cancellation and finite timeouts. A 429 enters a cooldown without retry. Repeated failures open a short circuit. Saved trips are explicit, device-local, bounded and labelled stale. Small offline maps require an explicit download and completion check; outside their area/zoom range coverage can be absent. No app can promise that networks, hardware, storage or map data will never fail.

## Before public distribution

1. Choose the legal publisher/support contact and create the Play developer account later.
2. Select/configure funded provider contracts, credential custody, attribution, failover and privacy disclosures. Run a real gateway staging beta; local fixtures do not prove this.
3. Review location handling, deletion, backup exclusion, Data safety, privacy policy, dependency notices and security/abuse boundaries.
4. Select the final application ID and protected upload signing key. Current lab signer is not a production identity. Review Play App Signing and recovery with the owner.
5. Test physical devices, supported APIs, TalkBack, switch/keyboard use, process death, real poor networks, storage pressure and sustained map interaction. No physical device was authorized here.
6. Build a signed Android App Bundle and upload to the appropriate internal/closed testing track. Account-specific testing requirements must be rechecked in Play Console. Prepare listing, screenshots and support, then obtain the owner's publishing instruction.
7. Roll out gradually with crash/ANR monitoring, service-health alerts and rollback. An older APK cannot roll back server data; version the service contract separately.

## Primary policies checked 2026-11-06

- Nominatim: https://operations.osmfoundation.org/policies/nominatim/ — public endpoint is not a generic generated app-search backend; per-application restrictions and deliberate informed responsibility apply. The generic starter no longer embeds it. A proxy does not remove its policy.
- OpenFreeMap: https://openfreemap.org/quick_start/ — native map usage and bounded offline downloads; maximum 3,000 tiles or 50 MB, four parallel requests and an identifying User-Agent. This public map service is not a contracted SLA.
- Play target level: https://support.google.com/googleplay/android-developer/answer/11926878 — new phone apps/updates require API36+ from 2026-08-31. Recheck all publication requirements at submission.
