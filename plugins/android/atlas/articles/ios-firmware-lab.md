# iOS firmware wrappers and trust metadata

**Scope:** Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

This chapter follows one concrete target: the Apple-hosted restore archive named `iPhone14,7_27.0.1_24A446_Restore.ipsw`. Its retrieved manifests declare iOS 27.0.1, build 24A446, for iPhone14,7 / d27ap. We inspected selected members, not a running iPhone and not every byte of the archive. Ru’OS is outside this investigation.

The most useful result in this round is a chain you can reproduce: **select a manifest component → fetch the exact archive member → check its digest → parse its wrapper → inspect its inner data → state what remains unproved.** This is more informative than treating a successful download as a successful reverse-engineering result.

## 1. Start with three different questions

“Which file is this?” is an identity question. “How is it structured?” is a format question. “Will a device accept and execute it?” is a policy and behavior question. Each needs different evidence.

For this case, a SHA-256 fingerprint identifies a retained local file. A SHA-384 comparison ties three selected members to the manifest's declared digests. DER parsing identifies their outer fields. None of these checks independently validates Apple's signature chain, a personalized boot ticket, the entire filesystem seal, or observed execution on hardware. Passing an earlier step does not automatically pass later steps.

Use these evidence labels while learning:

| Label | Meaning here | Example |
| --- | --- | --- |
| Observed bytes | Directly read from retained data | The wrapper contains `IM4P` and `ibdt` strings |
| Derived result | Computed from observed bytes | SHA-384 equals the manifest's digest |
| Documented design | What a cited platform description explains | Boot ROM verifies the next boot stage |
| Unknown behavior | Needs more artifacts or execution evidence | Which particular instruction enforces a check in this build |

## 2. An IPSW is a delivery container

The remote ZIP directory lists 125 entries. This includes directory entries and individual files; it is not a count of 125 independent software subsystems. The BuildManifest has two build identities—erase and update—with 101 component declarations each. Those 202 declarations refer to **76 unique paths**. A component can be reused by multiple identities and several logical components can refer to the same file.

That distinction matters when reporting progress. Inspecting a shared file once can inform several declarations, but it does not mean several different programs were analyzed. The manifest also contains 122 declarations with 48-byte digests, 68 with 32-byte digests, and 12 without a Digest value. A parser must not assume that every component uses the same hash or that an absent digest is an error. These counts are observations of this manifest, not a universal IPSW specification. See [manifest-semantics.json](case://local/android-ios-reverse-lab/epoch-2/ios/manifest-semantics.json).

The locally downloaded 3 GB IPSW is incomplete. Its first ZIP member declares more than 8 GB of stored content. We did not “repair” it by pretending the missing bytes were present. Instead, selected members came directly from the Apple CDN via exact HTTP byte-range requests, with status 206, Content-Range and response length checked. Python's ZIP reader also checked each selected member's CRC during extraction. One initial attempt timed out while reading the central directory; the successful retry and both receipt files are retained.

## 3. A worked example: iBootData

The acquired `Firmware/all_flash/iBootData.d27.RELEASE.im4p` is only 123 bytes. It is **data for a boot component**, not the complete iBoot executable. This small artifact is especially useful for learning because its layers can be examined completely without downloading a large disk image.

| Byte-level observation | Result |
| --- | --- |
| Outer encoding | DER SEQUENCE, 123 bytes including header |
| First string | `IM4P` |
| Type string | `ibdt` |
| Description string | `mBoot-20457.2.37` |
| OCTET STRING | 80 bytes, begins with `bvxn` |
| Following sequence | Integer values 1 and 272 |
| Decompression | System libcompression decoded 80 bytes to 272 bytes using LZFSE |

The readable payload did not require a decryption key. It contains the same `mBoot-20457.2.37` text. This supports the narrow observation that this selected member's inner data is accessible through decompression. It does not support “all iOS firmware is unencrypted,” nor does it reveal iBoot's complete logic. The meaning of the other decoded fields remains unassigned.

Its whole-member SHA-384 is:

`c89d3e7f824fa9cacce3fa12f69ad7b991aecd07d67b3f4710c424ca98138ae4328772b218b1123fc17a00eedd8f008f`

That value exactly matches `iBootData.Digest` in both build identities. The check used the complete IM4P member, not just its inner payload. This is an important reverse-engineering habit: identify **which byte range** a checksum covers. See [digest comparisons](case://local/android-ios-reverse-lab/epoch-2/ios/digest-verification.json), [structural observations](case://local/android-ios-reverse-lab/epoch-2/ios/byte-inspection.json), and the independent [OpenSSL DER output](case://local/android-ios-reverse-lab/epoch-2/ios/ibootdata-asn1.txt).

An IM4P is a payload object within Apple's Image4 family. A payload file by itself is not the same as an authenticated Image4 container carrying a manifest. The upstream Image4 tool's own documentation distinguishes payload, manifest and restore objects, and its signature-verification operation requires the relevant manifest material. [Primary tool documentation](https://github.com/tihmstar/img4tool).

## 4. Cryptex metadata: a second worked example

Two more acquired members match their declared SHA-384 values in both erase and update identities:

| Logical component | File size | Parsed wrapper | Inner data |
| --- | ---: | --- | ---: |
| Cryptex1,AppVolume | 229 bytes | IM4P, type `casy`, description `0` | 208 bytes |
| Cryptex1,AppTrustCache | 863 bytes | IM4P, type `trca`, description `1` | 840 bytes |

The first filename ends in `.root_hash`, but the whole 229-byte file is not a bare 32-byte hash. It is a typed wrapper containing a structured payload. This is why inferring a file format from its name alone is unreliable. We have not validated the referenced disk image against that root metadata.

For the trust cache, a pinned independent parser's v2 structure matches the acquired data: a 24-byte header plus 34 records of 24 bytes exactly fills the 840-byte payload. The record hashes are sorted. Each record exposes a 20-byte code-directory hash field and four single-byte fields. We interpret the latter as hash type, flags, constraint category and reserved byte according to that parser's layout. This is a format interpretation corroborated by lengths; it is not proof that the exact iOS 27 kernel applies a particular category policy. [Pinned parser source](https://github.com/CRKatri/trustcache/blob/aa0e8847529cf76576fce8d2dbc9e088c8f1a0df/trustcache.h).

A hash record is not a filename. To name the corresponding executable, the next investigation must acquire the relevant cryptex filesystem and compare executable CodeDirectory hashes. We have not done that mapping. Therefore “34 trust-cache records” must not be rewritten as “34 applications” or “34 services.”

Apple documents cryptexes as sealed disk images containing independently patchable OS or app content. Their measurements, filesystem seals and trust caches are represented in a separate device-bound Image4 ticket; they are brought into use after the kernel boots. This explains why the manifest has separate app/system image, trust-cache and volume metadata roles. It does not prove that this archive has an additional Background Security Improvement installed. [Apple's Background Security Improvements description](https://support.apple.com/ro-md/guide/security/sec87fc038c2/web).

## 5. Follow the documented boot chain, then mark the missing evidence

The following diagram is a learning map based on Apple's published design, **not a trace captured from this firmware**:

```text
Application-processor Boot ROM
    → verify and start iBoot
        → verify and start kernel
            → initialize operating-system services and protected content

Separate trust boundary:
Secure Enclave Boot ROM → verify sepOS → run Secure Enclave services

Cellular subsystem:
baseband secure boot → signed baseband firmware
```

Apple describes the application processor's immutable Boot ROM as the starting trust anchor. It validates the next boot stage, and iBoot validates the kernel. Apple's account specifically places the additional LLB stage on A9-and-earlier devices; the mere presence of an `LLB` filename in this modern archive does not establish that older boot sequence. Apple also describes recovery/DFU outcomes for failures at different stages. We have not triggered any of those modes. [Apple boot-process documentation](https://support.apple.com/en-ie/guide/security-pdf/secb3000f149/web).

The Secure Enclave is a distinct protected subsystem, not merely another user-space service. Apple's documentation describes its own immutable Boot ROM, protected memory initialization and sepOS hash/signature checks before control is transferred. Signature failure is described as preventing further Secure Enclave use until reset. The existence of a SEP firmware member in our manifest is consistent with that architecture; it does not independently prove its internal checks or expose device secrets. [Apple Secure Enclave documentation](https://support.apple.com/en-gb/guide/security/sec59b0b31ff/web).

This distinction helps avoid a common mistake: extracting the application processor's kernel would still leave Secure Enclave, baseband and other coprocessor implementations as separate investigation targets.

## 6. Compression, encryption, signing and sealing answer different questions

Our iBootData example demonstrates compression: an 80-byte stream expands to 272 bytes. The successful decode did not execute firmware instructions.

The manifest also names `.dmg.aea` images and declares AEA content encoding. Apple provides a dedicated archive-encryption context with keys, encryption mode, compression and signing parameters. These are separate concepts in the API. We have not retrieved or decrypted the full AEA images; a filename or readable outer header would not establish that their filesystems are accessible. [Apple ArchiveEncryptionContext](https://developer.apple.com/documentation/applearchive/archiveencryptioncontext).

A signature asks who authorized specific data under a key and policy. A filesystem seal binds a structured set of filesystem contents. A checksum comparison asks whether selected bytes match a declared measurement. These mechanisms can be composed, but no one check substitutes for all the others.

## 7. What you can now reproduce

1. Open `acquired-artifacts.json` and identify the precise archive member, byte count and local fingerprint.
2. Read `digest-verification.json`. Confirm that all six comparisons pass and notice that they cover only three unique members.
3. Read `ibootdata-asn1.txt`. Locate the OCTET STRING at value offset 34 and length 80.
4. Inspect `byte-inspection.json`. Compare the 272-byte decoded output with the original 123-byte wrapper size. Explain why decompressed data can exceed its entire compressed container.
5. Check the trust-cache arithmetic: `24 + 34 × 24 = 840`. Explain why that proves a consistent layout, but not executable identities or runtime acceptance.
6. Read `SystemVersion.plist`: it independently declares `ProductVersion=27.0.1` and `ProductBuildVersion=24A446`. Agreement between two archive files supports internal consistency; it still does not validate an Apple signature.

Do not run the acquisition script just to repeat these exercises: all selected bytes and results are retained locally. `inspect-members.py` is a bounded local parser/decompressor and can reproduce the structural output without a network or device.

## 8. Remaining questions for the next round

- Which exact files correspond to the 34 trust-cache records? Requires the matching cryptex image and CodeDirectory analysis.
- What do all fields in the decoded iBootData structure mean? Requires format/source correlation or disassembly of its consumer.
- Does the selected root metadata match the entire cryptex filesystem? Requires image acquisition and format-correct seal verification.
- Which checks are performed by this build's iBoot, kernel, SPTM, TXM and sepOS? Requires exact payload acquisition, decryption/decompression where applicable, supported disassembly and careful call-site analysis.
- Does a particular path execute on actual hardware? Requires a separately scoped runtime observation. Static strings and manifest flags cannot answer it.

This round completed selected byte acquisition, three digest matches across two identities, wrapper parsing, a real decompression and a bounded trust-cache layout analysis. It did not complete iOS reverse engineering. The next useful work is to follow one of these concrete unanswered questions rather than increase a generic percentage.


## Evidence and limits

Follow selected archive members through digests, wrappers, decompression and bounded trust-cache interpretation.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- No Apple signature/ticket chain, filesystem seal, actual load address or runtime enforcement was established.

Original case-authored material: [ios-learning.md](case://local/android-ios-reverse-lab/epoch-2/ios-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
