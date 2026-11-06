# Source, bytecode and machine code

A–Z topic C. **Scope:** Historical AOSP HelloActivity prebuilt (version P/27, target SDK 23) retained in Android17 r1 tag; not Android17 system app.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

Source code is an author's build input. DEX is managed bytecode. Native machine code encodes CPU instructions. Decompiled text is a tool's reconstruction of a compiled representation. These forms are related, but they are not interchangeable. Optimization, generated code, obfuscation and runtime loading can obscure the relationship.

**Our example:** REA recovered the teaching APK's `onCreate` sequence and it agreed structurally with a retained source file. We did not claim original-source recovery or prove that the APK was built from that exact commit.

**Try:** list what decompilation does not recover reliably: original comments, all names, build flags, and observed behavior are useful starting points. [Real APK decompilation](apk-static-lab.md).

Continue with [A real APK static-analysis lab](apk-static-lab.md).

## Evidence and limits

Separate authored source, DEX, machine code and reconstructed text using a real APK method.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.

Original case-authored material: [tooling.md](case://local/android-ios-reverse-lab/epoch-2/tooling.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
