# Maze native map starter — revision 02

Kotlin / Compose / MapLibre native map and road-planning preview. Not active navigation.

The production release has no search/routing service configured. Supply a reviewed credential-free HTTPS gateway with `-PmazeGatewayBase=https://your-owned-gateway.example/`. Never put provider API secrets in the app. See PUBLISHING.md for the funded-service/free-user plan and outstanding publication gates.

For controlled emulator tests only, run `node tools/lab-gateway.mjs` on the emulator host, then build `./gradlew :app:assembleLab`. This distinct `.lab` application uses 10.0.2.2:8787, labels sample data and only routes the recorded KL Sentral–KLCC fixture. The server binds loopback and makes no upstream requests. Release rejects fixture responses. Main/release cleartext remains disabled; the lab permits only 10.0.2.2.

Gateway contract: GET /v1/search?q=...&country=my&limit=5 returns {places:[{id,name,area,lat,lon}],maze:{mode:"gateway",provider:"..."}}. GET /v1/routes?from=lon,lat&to=lon,lat returns OSRM-compatible code/routes including GeoJSON geometry and steps plus maze metadata. Optional recorded_at_ms identifies source age; it is not proof of freshness. HTTP429 Retry-After seconds is bounded to 1–300. No redirect following; finite timeout, bounded body, one transient retry, cooldown. Self-hosted or licensed services must be deliberately reviewed; the public Nominatim endpoint is not a generic generated search backend.

User saves up to four trips explicitly on-device. Checksums detect accidental corruption, not malicious edits or authenticity. No invented roads on service failure. Small MapLibre offline areas require explicit download; their real offline completion/rendering must be checked on each target. Attribution remains visible in compact mode. No APK, production signer, SDK or credentials are bundled.
