# iBoot payload acquisition and static probes

**Scope:** Selected iPhone14,7 iOS 27.0.1 build 24A446 restore components; no hardware execution.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

This round acquired and decoded the **actual iBoot payload member** from the selected iPhone14,7 / iOS 27.0.1 / 24A446 restore archive. The previous round's tiny iBootData object was a different component. We now have a concrete binary candidate for deeper static analysis, with exact identity and a reproducible transformation chain. We have not executed it, recovered original source, or established its runtime load address.

## The chain of evidence

```text
Apple CDN ZIP member
Firmware/all_flash/iBoot.d27.RELEASE.im4p
1,188,914 bytes
    ↓ ZIP CRC check + SHA-384 match to both BuildManifest identities
IM4P wrapper: type ibot, description mBoot-20457.2.37
    ↓ parse OCTET STRING at member byte offset 40
LZFSE stream: 1,188,864 bytes
    ↓ system libcompression, output bounded to 32 MiB
Decoded binary: 2,525,752 bytes
    ↓ compare output size to wrapper's declared value
Static header and string observations; REA byte-entropy inspection
```

All offsets in this chapter are **file offsets**, not processor addresses. A file can be loaded at a nonzero address, relocated or contain several embedded objects. Until that mapping is established, adding a guessed base address would manufacture precision.

| Retained artifact | SHA-256 |
| --- | --- |
| Complete IM4P member | `3049da83081cdb48a1a35d11d53173753602a020a235725e8c4f9bebad1c6edf` |
| Extracted compressed payload | `65a90f6e95352a77f93cd965e7e58f4dd2388fd1bf1e69bf179416f931997527` |
| Decoded binary | `c5fee46797d3b60cd6c524d44a4bdc078e310752e5aed482ca0a62c42cbaa372` |

The complete member's SHA-384 equals the iBoot Digest in both erase and update build identities. The output size equals the trailing wrapper integer 2,525,752. These are separate checks: the first binds downloaded bytes to a manifest declaration; the second checks consistency of the transformation. Neither validates Apple's signature chain or a personalized ticket. See [inspection](case://local/android-ios-reverse-lab/epoch-3/ios/iboot-inspection.json), [acquisition receipts](case://local/android-ios-reverse-lab/epoch-3/ios/range-receipts.json), and [manifest declarations](case://local/android-ios-reverse-lab/epoch-3/ios/iboot-manifest-declarations.json).

## Why “looks random” did not mean “encrypted”

REA inspected the complete compressed and decoded files using 16,384-byte windows. The compressed payload's overall Shannon entropy was approximately **7.9904 bits per byte**; the decoded binary's was **6.2496**. REA covered 73 and 155 windows respectively, including their final partial windows. Evidence IDs:

- Compressed: `ev_a69351828a8bfc1c47cc6822fcec6f6bbc09950affb80243d04d2504daea2e84`
- Decoded: `ev_7c84f654fefa1f48f79daa45f443ac7cd32eaf500f01d491b4f4ce4d1e080d5f`

The first value is close to the maximum for byte data. Yet this payload decoded successfully using LZFSE without any decryption key. This is a direct counterexample to the shortcut “high entropy proves encryption.” Compression can also produce a nearly uniform byte distribution. A useful diagnosis combines the header, a format-aware decoder, size consistency and retained hashes; an entropy score alone identifies none of the program's functions.

Conversely, the lower decoded score does not prove that all bytes are executable instructions. A binary can contain code, constants, strings, padding and embedded firmware. We have not classified every region. [REA evidence export](case://local/android-ios-reverse-lab/epoch-3/ios/rea-entropy-evidence.json).

## What the wrapper actually says

The wrapper is one well-formed DER SEQUENCE. A bounded local parser and OpenSSL's syntax parser both accepted it. Its fields include:

| Field | Observed value |
| --- | --- |
| Format marker | IM4P |
| Component type | ibot |
| Description | mBoot-20457.2.37 |
| Payload start | Byte 40 of the IM4P member |
| Payload size | 1,188,864 bytes |
| Payload prefix | `bvx2` |
| Trailing sequence | Integer 1 followed by integer 2,525,752 |

The parser extracted only the OCTET STRING before decompressing it. Feeding the entire IM4P directly to an instruction decoder would make it interpret metadata and compressed bytes as if they were instructions. This is why identifying and removing container layers is a prerequisite to useful disassembly.

The decoded file does not start with recognized Mach-O or ELF magic, and the system `file` utility reports generic data. That does not prove it contains no executable code; it means the common executable-header route has not established the layout. A raw ARM64 interpretation is a platform-informed candidate, not a load-map fact recovered from a Mach-O header. A deeper REA import must record any chosen architecture and analysis base as assumptions until independently supported.

## What strings teach—and what they cannot

A bounded scan found 212 printable ASCII runs matching selected boot, version, firmware and validation keywords. All matches are retained in [selected-strings.json](case://local/android-ios-reverse-lab/epoch-3/ios/selected-strings.json). Some useful anchors are:

| Decoded file offset | Observed text | Useful next question |
| ---: | --- | --- |
| 768 | `mBoot-20457.2.37` | Which header or metadata region contains this text? |
| 1,714,695 | `/usr/standalone/firmware/FUD/iBootData.img4` | Which code refers to this path, and under what condition? |
| 1,714,745 | `/System/Library/Caches/com.apple.kernelcaches/kernelcache` | Is a caller trying this path, validating it, or merely reporting it? |
| 1,721,503 | `image4_callbacks.validity_cb is NULL` | Which callback setup and error path refer to this diagnostic? |
| 1,723,821 | `Kernelcache too large` | Which size, bound and branch produce this message? |
| 1,723,844 | `Kernelcache image not valid` | What validation result leads to this message? |

A string is a clue to a possible code path. It is not that path. The filename may be unused in this device variant; a diagnostic may be in shared code; a reference may depend on an unreachable configuration. The next defensible claim requires a real code reference, an understood branch condition and relevant callers. A runtime claim additionally requires observing an authorized execution path.

The shared version text in the wrapper, iBootData and decoded binary is useful internal consistency evidence. It should not be elevated to complete version provenance without the archive and digest context already recorded.

## Acquisition lessons

The first attempt failed when the remote connection closed during ZIP64 directory lookup. The retry used cached aligned byte ranges, reducing repeated requests for nearby archive metadata. It checked HTTP 206, exact Content-Range, exact response size and ZIP CRC. Cached responses are retained along with range hashes. The body transfer completed successfully; no download remains active.

The original incomplete local IPSW was not overwritten. These observations apply to the selected Apple CDN member, not to missing bytes in that local archive. No firmware was installed or booted. Decompression ran a host library on data; it did not execute the target's instructions.

## A short exercise

1. Find the four relevant sizes: ZIP compressed member, IM4P member, inner LZFSE stream and decoded binary. Explain why they differ.
2. Explain why matching SHA-384 to a manifest is stronger evidence of byte consistency than matching a filename, but weaker than authenticated signature verification.
3. Select `Kernelcache too large`. Write a precise next question: which numeric comparison reaches this message? Do not state that a limit is enforced until the branch is identified.
4. Explain why entropy 7.9904 did not block this investigation: the next successful check was format-aware decompression.
5. Explain why offset 768 should not be presented as virtual address `0x300` on an actual device.

## The next concrete boundary

The ready target is [iboot.decoded.bin](case://local/android-ios-reverse-lab/epoch-3/ios/iboot.decoded.bin). Deep static work now needs a working raw-binary analysis provider, an explicitly documented architecture hypothesis, and evidence for load mapping and entry points. Then follow one small path—for example a reference to the kernel-size diagnostic—through its checks and failure branch. Keep unresolved indirect calls and device-condition branches visible.

This round proves exact member acquisition, manifest-digest consistency, wrapper parsing, successful decompression, output-size consistency and byte-level observations. It does not prove boot eligibility, signature acceptance, whole-program control-flow semantics, exploitability or whole-iOS coverage.

## Additional bounded probe: the first 256 bytes as AArch64

A small static probe now provides actual instruction decoding for the prefix. The installed Apple clang 21.0.0 and LLVM objdump 21.0.0 support AArch64. We placed the first 256 decoded bytes inside a **synthetic Mach-O object** solely so objdump could decode the raw bytes. This object is not Apple's original firmware format; its `_iboot_prefix` symbol and zero section address were created for analysis. It was assembled, not linked or executed.

An independent read of the synthetic section confirmed that its 256 bytes exactly equal offsets 0–255 of the retained decoded binary, with zero relocations. Prefix SHA-256: `e41cf925603f68cb025cd12559f20c6068594268849bbae43e0b439ccbc8921c`. See the [byte-equality proof](case://local/android-ios-reverse-lab/epoch-3/ios/native-prefix/byte-equality-proof.json) and [64-instruction disassembly](case://local/android-ios-reverse-lab/epoch-3/ios/native-prefix/disassembly.txt).

Under this AArch64 interpretation, three small effects are directly understandable:

| Relative byte offsets | Decoded instructions | Effect if reached and executed normally |
| --- | --- | --- |
| `0x0c`, `0x10` | `orr x2, x2, #0x400000000`; `orr x2, x2, #0x8000000` | Sets bits 34 and 27 of x2 without clearing its other bits. The surrounding read/write names HCR_EL2, but this probe does not infer the complete hardware policy. |
| `0x44`, `0x48` | `add x2, x2, #0x3f`; `and x2, x2, #0xffffffffffffffc0` | Adds 63, then clears the lowest six bits. For values that do not overflow, this rounds upward to a multiple of 64; 64-bit wraparound remains a boundary case. |
| `0xb0`, `0xb4` | `ldp x12, x13, [x10], #0x10`; `stp x12, x13, [x9], #0x10` | Loads two 64-bit values from the source address, stores them to the destination, and advances both pointers by 16. This local pair supports a data-copy interpretation, not a proved complete relocation algorithm. |

The logical and addressing interpretations follow Arm's instruction model; see [Arm's logical-instruction reference](https://support.arm.com/documentation/dui0802/a/A64-General-Instructions/MOV--bitmask-immediate-?lang=en) and [A64 load/store addressing modes](https://support.arm.com/documentation/ddi0487/mb/-Part-C-The-AArch64-Instruction-Set/-Chapter-C1-The-A64-Instruction-Set/-C1-3-Address-generation/-C1-3-3-Load-store-addressing-modes).

Several literal loads and branch targets fall outside these 256 bytes, including the displayed relative call target `0x38330`. Their destinations were not disassembled in this probe. The output's `ltmp0` labels are synthetic assembler labels, not recovered original symbols. We still have no proof that offset zero is the hardware entry point, what actual address it occupies, which exception level executes it, or whether these paths run for the selected device. This probe advances instruction-level learning while leaving those boundaries explicit.


## Evidence and limits

Decode a real iBoot member and inspect bytes and a synthetic native-prefix probe without inventing a load map.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- No Apple signature/ticket chain, filesystem seal, actual load address or runtime enforcement was established.

Original case-authored material: [ios-code.md](case://local/android-ios-reverse-lab/epoch-3/ios-code.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
