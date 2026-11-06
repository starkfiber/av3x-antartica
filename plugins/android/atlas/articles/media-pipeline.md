# Media, buffers and hardware boundaries

A–Z topic M. **Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** documentation. This names the main evidence anchor, not universal verification of every explanatory statement.

Playing audio or capturing a camera frame involves application APIs, service policy, buffers, codecs, hardware interfaces and drivers. Performance and privacy checks can occur at different boundaries. A permission declaration does not prove successful capture, and a codec's presence does not prove it is selected.

**Current proof level:** architectural documentation and selected system source context. We have not recorded audio/video, acquired a vendor camera stack or observed hardware codec selection.

**Try:** trace “tap record” to stored bytes. At each step ask who owns the buffer, who checks permission, and how errors return. This turns a broad media stack into a tractable investigation. [Media and camera lesson](android-systems-lab.md).

Continue with [Android systems and service boundaries](android-systems-lab.md).

## Evidence and limits

Break capture and playback into APIs, services, buffers, codecs, HALs and drivers.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- The central mechanism is documented design, not a recovered implementation or measurement.

Original case-authored material: [android-systems.md](case://local/android-ios-reverse-lab/epoch-3/android-systems.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
