# Current coverage and acceptance

Release3.2.0-antartica.7 is a public **community preview** of the V3 line. Major3 and the user's Antartica spelling are preserved. Publishing this package does not close an acceptance gate or certify an operating system.

| Area | Available evidence | Remaining boundary |
| --- | --- | --- |
| Local corpus | 51 dated topics; load-time body hashes; literal/alias search; exact IDs and H1–H5 lookup | Curated subset, not an exhaustive manual; upstream facts can change |
| MCP | 13 read-only tools; fresh-process protocol tests and smoke | Actual discovery must be checked in each client/session |
| Code checks | 30 Node regression tests; red-team input/transport cases; portability/update tests | Bounded tests do not sandbox the host or prove absence of all bugs |
| Reader | Self-contained HTML, navigation/focus fixes, prior DOM checks | Rendered keyboard and full assistive acceptance remain open |
| Emulator research | Scoped M5/API35 ARM64 work and retained negative results summarized in articles | No physical FPS/energy/app-code speedup claim |
| Maze sample | Compose/MapLibre source, small completed offline region, saved trips, bounded recovery; final main runner8/9 plus corrected independent search on same artifact | Fixtures are recorded/sample; no Malaysia-wide offline graph/POI package in this release |
| Security guard | Structured reject/quarantine checks | No executor, model-weight control or self-approval |
| Publishing | Public source and reproducible package checks | GitHub distribution is not OpenAI directory approval or Google Play approval |

Historical case paths are converted to `case://local/…` locators. Raw research files, credentials and datasets are not included. Those locators are descriptive provenance, not a public reproducibility archive. Hashes establish byte identity relative to the published manifest; they do not establish source truth, author authenticity or upstream coverage.

## Next useful contributions

1. Independently verify reader rendering, keyboard focus and assistive behavior with retained results.
2. Improve lookup examples and clearly sourced explanations where the corpus has gaps.
3. Add portable Android example checks with explicit runtime/API/ABI boundaries.
4. Bring reviewed results from the separate offline Malaysia sprint, including licensing, data size, missing POIs and real airplane-mode routing/search evidence.
5. Reproduce security/transport edge cases without credentials or live third-party targets.

Use Issues for bugs, evidence corrections and feature proposals. Do not replace an unresolved gate with a favorable release number. See V1-ACCEPTANCE.md and V3X-SECURITY.md for historical details.

## Antartica.7 qualification update

The corpus now has52 topics. The new Maze community pipeline lesson pins a public source commit and its successful build/JVM/lint/packager CI; it does not inherit ongoing-sprint runtime results. The reader gate remains open: local-file preview was rejected by the browser automation policy, and no workaround was attempted. Manual results were requested. Use [the reader checklist](READER-CHECKLIST.md) for the actual rendered/assistive evidence still needed.
