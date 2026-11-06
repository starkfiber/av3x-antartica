# Offline map pipelines: public source, local data and measured limits

Reviewed 6 November 2026 against Maze community commit463582fa7487692ab5ca174935c905428e38700b. This is a source/build lesson from that fixed snapshot. A separate active sprint can change the app; later features and tests are not inherited automatically.

## Separate the deliverables

A small Android application, regional databases and font glyphs have different distribution and update needs. The community repository contains Kotlin/Compose/MapLibre source and data-building tools, not a ready-to-use country pack or production-signed APK. Building downloads dependencies. Offline operation is a separate runtime property after the matching pack has been imported.

The source uses local SQLite place/address search, an on-device conservative road graph and a loopback tile listener. Android's INTERNET permission can still be needed for a localhost map transport; declaring that permission does not establish an external API dependency or prove absence of network traffic. Establish actual offline behavior with a cold-start/airplane-mode test of the exact artifact and pack.

## Use licensed bulk inputs

The documented pipeline obtains Geofabrik's Malaysia/Singapore/Brunei OSM PBF and matching Shortbread1.0 MBTiles. This regional extract is not an authoritative Malaysia-only boundary. PyOsmium builds place and road databases; the explicit-input packager adds only three chosen datasets, a manifest and attribution. A decompressed hash roundtrip detects changed bytes, not geographic truth.

The app pins expected dataset sizes/digests. After generating a different pack, review its manifest, pin it into the app and rebuild. Importing arbitrary data under an old trusted manifest is not a safe update strategy. Public redistribution of OSM-derived databases retains ODbL obligations. Font software and glyph packaging retain their OFL/BSD notices; the app's MIT source license does not replace those terms.

## Keep mapped objects distinct

Shop points, representative building points, tagged entrances and motorway exits are different data. A representative point is not a verified door. A missing entrance remains missing. The conservative graph excludes unsupported restrictions/access cases and ferries, so legitimate roads can be disconnected. Junction snapping, finite search budgets and visible failure are preferable to inventing a straight-line driving route. These road previews are not active navigation, lane guidance, live traffic or a fastest-route guarantee.

## What was actually checked

At the stated public commit, the publication task built the isolated source, passed16JVM tests and lint, verified the official Gradle wrapper checksum, and fetched256glyph blocks with matching recorded upstream Git blob identities. A synthetic packager test checked exact ZIP membership, decompressed hashes and rejection of an existing output. The main-branch GitHub run repeated Android build/JVM/lint and the packager check successfully.

This establishes source/build and packaging evidence. It does not establish country-wide route quality, energy savings, physical FPS, complete shop coverage, independent accessibility acceptance or a new emulator proof for this public snapshot. Keep earlier lab results attached to their actual artifacts rather than transferring them to changed code.

## Continue through reviewable updates

The ongoing app lives in the separate Maze repository. Review a source diff, preserve known failures, identify the exact dataset/ABI/API and test the artifact again before updating this atlas. Keep APKs, keys, location histories and raw local research out of source commits. The atlas remains a reference companion; the SDK, app, data and verification environment do the application work.
