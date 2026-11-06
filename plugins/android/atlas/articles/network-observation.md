# Network leads versus observed traffic

A–Z topic N. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

A URL found in code can be unused, dynamically changed, or reachable only under certain states. Network behavior depends on name resolution, sockets, transport security, routing, policy and remote responses. A static string is a useful lead, not proof that data was transmitted.

**Our actual network observations:** byte-range downloads returned specific archive segments, with response status, Content-Range, byte count and hashes recorded. Those observations establish acquisition, not any phone's network behavior.

**Try:** distinguish “the binary contains a hostname,” “a function constructs a request,” and “a captured execution sent these bytes.” [Networking lesson](android-systems-lab.md).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Distinguish a URL string, request construction and captured transmission; no phone traffic was measured.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
