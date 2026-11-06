# Existing IPSW versus Android: compare contracts and evidence

This comparison concerns the retained Apple archive metadata declaring **iOS 27.0.1, build 24A446, iPhone14,7**. It does not establish that this is today's latest release or currently restorable. The archive remains incomplete; individually acquired members are separately verified. Android evidence combines pinned AOSP source, selected GSI metadata and a historical sample APK, not a complete matched retail phone. Huawei conclusions require the separately scoped Huawei investigation; a brand name alone does not identify an OS generation, runtime or app ecosystem.

## New static result: erase and update share nearly all declarations

We recursively compared the complete two `BuildIdentities` objects in the retained BuildManifest, including byte-valued fields. The input SHA256 is `1e116653d2d73f00566ca7f83daf006c846e14c3474777904afe359dc94e5742`. [Reproducible parser](case://local/android-ios-reverse-lab/epoch-5/apple/compare_identities.py), [complete result](case://local/android-ios-reverse-lab/epoch-5/apple/identity-comparison.json).

Both identities contain the same **101 component names**. **99 complete component objects are equal**. Across the entire identities, exactly seven leaf values differ:

| Field | Erase identity | Update identity |
| --- | --- | --- |
| `Info/RestoreBehavior` | Erase | Update |
| `Info/Variant` | Customer Erase Install (IPSW) | Customer Upgrade Install (IPSW) |
| `RestoreRamDisk/Info/Path` | `043-69915-784.dmg` | `043-69093-778.dmg` |
| `RestoreRamDisk/Digest` | Distinct 48-byte digest | Distinct 48-byte digest |
| `RestoreTrustCache/Info/Path` | `Firmware/043-69915-784.dmg.trustcache` | `Firmware/043-69093-778.dmg.trustcache` |
| `RestoreTrustCache/Digest` | Distinct 48-byte digest | Distinct 48-byte digest |
| `UniqueBuildID` | Distinct byte value | Distinct byte value |

**Inference:** these declarations describe two installation variants sharing most component descriptions, with different restore-environment images and associated trust-cache declarations. They are not two entirely separate declared OS component collections. **Not established:** the ramdisks' contents, the code that selects a variant, whether a particular operation preserves user data, or a device's acceptance of either identity. Neither ramdisk was fetched or executed in this probe.

The erase identity's 101 roles refer to **74 unique paths**, with **12 paths serving multiple named roles**. Examples include `KernelCache`/`RestoreKernelCache`, `DeviceTree`/`RestoreDeviceTree`, and `SEP`/`RestoreSEP`/`Yonkers,SepObject`. Counting role names as separate firmware binaries would overcount. A shared pathname also does not establish identical runtime arguments, privilege, load location or execution context. Across both identities there are 76 distinct paths; the two extra update paths explain the difference from 74.

This provides a practical RE lesson: a manifest is a relationship graph among identities, roles and objects. Investigate its relationships before treating every name as a separate executable or assuming “update” and “erase” name entirely different operating systems.

## Integrity, authorization and execution are different questions

Earlier work extracted an actual iBoot IM4P member, checked its whole-member SHA384 against both identities, decoded its LZFSE payload and reproduced a bounded 256-byte instruction-prefix view. That proves specific byte relationships and conditional instruction decoding. It does not authenticate Apple's full signature chain, identify the real runtime entry/load address, or show a boot completing. The high compressed entropy did not imply encryption: decoding succeeded without a secret key. [iBoot evidence chapter](case://local/android-ios-reverse-lab/epoch-3/ios-code.md).

Apple documents installation authorization using component measurements, a random anti-replay value and a device ECID. The authorization service signs an accepted measurement set personalized for that device. Consequently, having the expected archive bytes and being permitted to install them are different conditions; download availability is not current signing eligibility. This case has not requested device personalization. [Apple secure software updates](https://support.apple.com/guide/security/secure-software-updates-secf683e0b36/web).

Android AVB similarly requires more than a hash match. Its public integration contract supplies bootloader-specific I/O, root-of-trust handling and rollback metadata to `libavb`; delegated partition verification is supported. [AOSP AVB](https://source.android.com/docs/security/features/verifiedboot/avb). Our earlier GSI vbmeta signature check was against its embedded key, with flags value 2 observed. It did not establish a retail OEM trust anchor or full enforcing device chain. Thus neither side has earned a “secure device boot verified” claim from these local artifacts alone.

## Criterion-by-criterion tradeoffs

The advantages below are scoped analytical judgments, not benchmarks or a universal winner.

| Criterion | Apple side: evidence and implication | Android side: evidence and implication |
| --- | --- | --- |
| Learning implementation details | Public XNU provides useful kernel-policy source, but it was not matched to this iOS build; proprietary framework/firmware behavior remains a separate target | Pinned AOSP lifecycle/IPC/system source already lets this case follow concrete implementation paths; vendor components and source-to-shipped-binary matching remain gaps |
| Firmware organization | This manifest offers a precise device/build/variant component graph; useful for comparing roles and object identity | System/vendor partitions and VINTF separate framework from hardware-specific code; useful for reasoning about replaceable boundaries, with compatibility obligations |
| Update trust | Documented personalized Apple authorization provides a centrally controlled installation decision; independent installation experimentation is constrained by that decision | AVB exposes a reference verifier and platform integration points; the actual OEM key policy, rollback storage and device state still determine behavior |
| Static accessibility | Actual small iBoot bytes and metadata were acquired and decoded; large AEA/filesystem contents are not inspected here | Actual small vbmeta and APK bytes were inspected; the GSI system image remains incomplete, so matched system apps/native libraries are not yet available |
| Graphics learning | Metal documentation and CPU/GPU resource contracts are accessible; no device timing or graphics firmware trace was measured | Android graphics source/API documentation offers source-oriented investigation; no comparable measured workload on a named Android device was collected |
| App portability | Matching ISA alone cannot supply Apple framework, runtime, signing, sandbox, platform-service and backend behavior | APK parsing or an AOSP build alone cannot supply every app's APIs, vendor features, platform services and backend behavior |
| Performance and efficiency | No measured winner | No measured winner |

AOSP describes `system`, `boot`, `init_boot`, vendor-specific partitions and their stable VINTF boundary. This describes a platform architecture, not a guarantee that any image works on any phone. [AOSP partition overview](https://source.android.com/docs/core/architecture/partitions).

The public XNU snapshot used here is commit `f6217f891ac0bb64f3d375211650a4c1ff8ca1ea`. Its policy checks are source observations; they must never be labelled disassembly of the actual iOS 27.0.1 kernel. The CPU/GPU chapter also distinguishes legacy Metal command objects from Metal 4 contracts. A firmware manifest cannot answer shader throughput, frame pacing, QoS tail latency or memory-pressure behavior. [CPU/GPU source and documentation chapter](case://local/android-ios-reverse-lab/epoch-4/apple-cpu-gpu.md).

## How Huawei belongs in a defensible three-way comparison

Use a concrete product, OS build, region and application format from the Huawei chapter before making the third column. Do not equate a Huawei retail build with an OpenHarmony source tree, assume all generations share Android application compatibility, or infer a kernel from marketing terminology. Those are hypotheses requiring the selected build's documentation or artifact evidence.

Then run the same questions across all three: What object is signed? Who supplies the trusted key? Where is rollback state held? Which runtime executes an app? Which platform APIs and services must exist? What is the measured workload and hardware? Evidence of an API contract is not evidence of its performance, and access to source is not proof that a particular consumer device runs that exact source.

For an Android plugin atlas, the Apple comparison is most useful as a set of explicit boundaries: archive inventory, identity graph, wrapper decoding, digest checking, signature trust, runtime format, API/service compatibility and measured execution. A plugin that handles only inventory should expose that limit instead of presenting a “fully reverse-engineered OS” badge.

## What to learn next from these exact artifacts

1. Review the seven manifest differences and explain why 101 roles are not 101 distinct files.
2. Follow one role from a selected identity to its path, digest and already-retained bytes; identify where signature trust remains unproved.
3. Compare this graph with AVB descriptor relationships without assuming they are the same format or trust policy.
4. Before a performance comparison, define equivalent workloads, hardware constraints and timestamps. No static result in this round supplies those measurements.

This epoch performed local bounded plist parsing only. It did not download another firmware image, restore a device, execute firmware, install an application or change system configuration. Ru’OS remains excluded.


## Import provenance

Case-authored snapshot: case://local/android-ios-reverse-lab/epoch-5/apple/comparison.md
No new runtime validation is implied by packaging this article.
