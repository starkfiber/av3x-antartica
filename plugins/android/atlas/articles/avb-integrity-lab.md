# AVB integrity, signatures and policy

**Scope:** Android 17 AOSP android-17.0.0_r1; QPR1 GSI metadata CP3A.260905.010 is not a proven source/binary match.

**Evidence level:** artifact-inspected. This names the main evidence anchor, not universal verification of every explanatory statement.

The small `vbmeta.img` from Google's Android 17 QPR1 GSI lets us study a real security structure without booting or changing a phone. Its 4,096 bytes are enough to demonstrate the difference between integrity, signature consistency, trust and enforcement.

## 1. A file can describe how other files should be checked

The image begins with `AVB0`. Its 256-byte header gives offsets and sizes for an authentication block and an auxiliary block. The authentication block holds a hash and signature. The auxiliary block holds descriptors and a public key. A descriptor can name another partition or describe how to check its contents. These are data structures, not an executed bootloader.

Our parser bounds-checks the fields against the actual file before slicing bytes. This is necessary: a filename or a four-byte magic value alone cannot prove that every declared region exists. The incomplete IPSW in this case is a direct example of why declared sizes must be compared with bytes actually present.

Observed layout: 256-byte header, 576-byte authentication block, 1,664-byte auxiliary block. The remainder is not included in the signed header-plus-auxiliary region. The algorithm field is 2, corresponding to SHA-256 with RSA-4096 in the retained AOSP implementation. There is one chain-partition descriptor. See `avb/vbmeta-verification.json` for its decoded partition name, key fingerprint and rollback location.

## 2. Hashing proves a specific correspondence

We computed SHA-256 over the header followed by the auxiliary block. It exactly matches the hash stored in the authentication block. This checks agreement between those bytes and the stored digest. A hash by itself does not identify who supplied the file: anyone who changes the payload could also replace an unsigned hash.

We then used the embedded RSA public key to verify the signature's PKCS#1 v1.5 encoding and digest. A separate OpenSSL verification of the same signed bytes returned `Verified OK`. Both checks passed. The first OpenSSL attempt rejected the key container because it was PKCS#1 rather than SubjectPublicKeyInfo; wrapping the same modulus and exponent in the expected public-key container resolved that format error. The failed attempt is preserved in `avb/vbmeta-first-attempt.json`.

A negative control flipped one byte in an in-memory copy of the signed region. The stored digest and recovered RSA digest no longer matched. The original image was never altered. This demonstrates that these checks distinguish the original signed bytes from that specific changed input. It does not test every malformed image or certify the parser.

## 3. A valid signature is not the whole trust chain

The public key used here came from the image itself. A signature valid under that key proves a mathematical relationship between a key, signature and bytes. To establish OEM trust, a verifier must additionally know which key is authorized, often through a hardware-rooted boot chain or a trusted parent descriptor. We have not inspected such a trust anchor on a device.

A useful thought experiment: I can generate my own key, sign my own file and include my public key. The signature can be perfectly valid while a locked phone correctly rejects it because my key is not authorized. Thus, “signature verifies” and “this phone will boot it” answer different questions.

The AOSP design describes a bootloader integration that supplies I/O, trusted-key validation and rollback metadata to libavb. Our file-only analysis has none of the device's rollback storage, bootloader settings, trusted keys or acceptance result. [AOSP AVB design](https://source.android.com/docs/security/features/verifiedboot/avb).

## 4. Policy can be signed too

Our image's flags field equals 2. The retained Android 17 AOSP header defines bit 1 as `AVB_VBMETA_IMAGE_FLAGS_VERIFICATION_DISABLED`. This is an observed declaration in the selected GSI metadata. It must not be generalized into “Android 17 has verification disabled” or “the user's phone is unlocked.” A generic development image is not a representative sample of all production firmware, and no device boot flow was observed.

The signature covers the header that carries this flag. This illustrates an important distinction: a signature protects the integrity of a policy declaration; it does not make every possible declared policy equally restrictive. Actual handling also depends on the bootloader and its accepted state. [Pinned flag definitions](https://android.googlesource.com/platform/external/avb/+/refs/tags/android-17.0.0_r1/libavb/avb_vbmeta_image.h).

## 5. Large files require a different integrity strategy

A small boot image can be hashed as a whole before use. Large read-only filesystems can use a hash tree so blocks are checked as they are read, with the expected root anchored in trusted metadata. Rollback protection is a separate check against accepting an older vulnerable version. These concepts solve related but different problems: altered bytes, unauthorized producers, and old-but-valid software. [AOSP verification flow](https://source.android.com/docs/security/features/verifiedboot/verified-boot).

We have not downloaded the complete system.img, verified its tree, followed the chain descriptor to its target bytes, or inspected a phone's rollback state. Those checks remain open.

## Try explaining it back

1. Why is a matching SHA-256 not enough to establish who published a file?
2. Why can OpenSSL say `Verified OK` while a phone still refuses to boot?
3. Which exact bytes did we authenticate, and which partition contents remain unchecked?
4. Why does a signed verification-disabled flag not contradict the fact that the signature is valid?
5. What additional evidence would establish runtime enforcement?

Answers: a hash needs a trusted reference; the key must also be authorized; the signed region was header plus auxiliary block, not the missing system partition; authenticity and policy strength are separate; the last question requires the exact device's trusted boot path, configuration and observed behavior.

## Reproduce and inspect

Run the retained `avb/inspect_vbmeta.py` against the unchanged local image to reproduce this static observation. It writes only case-owned evidence, parses the image as data, and calls OpenSSL to verify a signature. Its expected input is deliberately narrow: AVB SHA256_RSA4096. It is not a general firmware verification tool.

Evidence: original vbmeta SHA-256 `b3281c527ecf2a6a743b37f5b9e118f492d2038e0a82e6777d292d1c5b42601a`; source header retained as `avb/avb_vbmeta_image.h`; partial avbtool source explicitly named `.partial.py.txt` and never executed. This is direct byte analysis plus official source interpretation, not REA native decompilation or a runtime test.


## Evidence and limits

Verify a selected GSI vbmeta with its embedded key and distinguish cryptographic consistency from authorized boot.

- No device or emulator execution is represented by this entry.
- Evidence levels label the main retained anchor; documentation and derived explanations are not extra runtime proof.
- This is imported case-authored teaching material, not a new independent verification.
- Only identified retained artifacts and named checks were inspected; this is not whole-OS coverage.
- Embedded-key signature consistency does not establish OEM trust, rollback state or device acceptance.

Original case-authored material: [verified-boot-learning.md](case://local/android-ios-reverse-lab/epoch-2/verified-boot-learning.md). Raw evidence remains in the local case; the atlas contains explanatory text only.
