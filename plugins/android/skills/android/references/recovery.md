# Recovery contracts and the limits of “unbrickable”

Design for bounded recovery from specified faults. Do not promise that software prevents every hardware failure. This reference describes prior art and proposed checks; it does not implement a bootloader, updater or recovery service. No device operation follows from reading it.

## Name the failure before choosing a remedy

“Soft brick” and “hard brick” are informal terms. Here, a soft brick means normal OS use fails but some trusted recovery path remains reachable. A hard brick means the ordinary boot/recovery paths are unavailable; it may still be repairable through vendor service. Physical damage or component failure is a separate axis. A blank screen alone does not distinguish them.

| Observed or modeled state | Plausible failure boundary | Recovery design requirement | What an app/plugin cannot promise |
| --- | --- | --- | --- |
| One app crashes; system usable | App code/data or compatibility | Isolate failure, preserve data, use validated update/rollback | Repair arbitrary system firmware |
| System crash loop; bootloader reachable | Bad system configuration, update or service | Bounded retries, trusted alternate boot/recovery, diagnostic record | Every boot-success marker proves usable UI |
| Candidate update interrupted before activation | Partial target data/metadata | Keep a validated fallback reachable; reject incomplete candidate | Survive arbitrary damage to shared storage |
| AVB rejects candidate | Wrong identity/key, corruption or rollback-policy violation | Preserve trust; select an authorized recovery route | Safely bypass authentication as a recovery shortcut |
| Virtual A/B snapshot merge interrupted | Coupled base/snapshot/metadata state | Recover durable merge state; prevent incompatible wipes | Treat snapshots as two independent permanent full OS copies |
| Kernel/driver hangs | Deadlock, device fault or resource starvation | Independent detection and bounded escalation | A reset cures its underlying cause |
| Early boot/recovery unavailable | Boot firmware/metadata failure or hardware fault | Vendor-specific immutable/trusted service path if provided | Generic app can reconstruct missing trust anchors |
| Storage cannot retain data, power path fails, board is damaged | Physical component boundary | Redundancy/service/replacement according to hardware design | Software restores absent electrical or storage function |

The last row is a logical hardware boundary, not a diagnosis of a particular device. No failure rates or component lifetimes are inferred. Preserve a separate data-recoverability assessment: a bootable device can still have lost user data, and a failed screen can coexist with intact storage.

## Existing mechanisms to learn from

**A/B boot control:** AOSP specifies bootability, successful-boot state and bounded retry counts. The framework marks success; the bootloader selects another eligible successful slot or recovery/error when attempts are exhausted. Do not confuse having a second slot with proving every fallback dependency remains intact. [AOSP bootloader OTA contract](https://source.android.com/docs/core/architecture/bootloader/updating).

**Boot success is a narrow signal:** AOSP's update description notes that a system able to apply updates can be marked successful even while major components such as UI are broken. Product health acceptance should therefore be separately defined; do not silently redefine the platform's marker. [A/B update lifecycle](https://source.android.com/docs/core/ota/ab).

**Virtual A/B:** dynamic partitions use snapshots merged into their base after a successful boot, rather than a permanent second full dynamic-partition copy. Recovery reasoning must include the merge stage. [Virtual A/B overview](https://source.android.com/docs/core/ota/virtual_ab). AOSP also requires merge-status awareness when operations would erase relevant metadata or userdata; device-specific integration matters. [Implementation contract](https://source.android.com/docs/core/ota/virtual_ab/implement).

**AVB:** reference verification integrates device I/O, trusted keys and rollback metadata. Recovery must respect that policy: “go back” is not permission to boot an arbitrarily older vulnerable version. [Android Verified Boot](https://source.android.com/docs/security/features/verifiedboot/avb).

**Rescue Party:** repeated core-system crashes can trigger escalating recovery actions, with factory-reset prompting as a last resort. Availability/configuration is device-specific; this is not a universal data-preservation guarantee. [AOSP Rescue Party](https://source.android.com/docs/core/tests/debug/rescue-party).

**Watchdogs:** a hardware watchdog can reset a system after expected liveness signals stop. This does not prove application health or repair a persistent fault; repeated resets require bounded escalation. Linux's API description is historically dated, so use it for the mechanism, not a current phone driver contract. [Linux watchdog API](https://docs.kernel.org/watchdog/watchdog-api.html).

## Proposed invariants — obligations, not verified properties

Let `A` mean an authenticated, rollback-policy-eligible boot/recovery path exists; `H` mean required hardware/storage and the assumed durable-write primitive function; `U` mean the update transition is inside the specified fault model.

1. **Recovery reachability:** `H ∧ U ∧ A_before ⇒ A_after`. List shared components and allowed failures. This is not a universal theorem: arbitrary shared-storage loss can violate its assumptions.
2. **Candidate admission:** `activate(candidate) ⇒ identity_match ∧ integrity_verified ∧ authorization_valid ∧ metadata_durable`. A manifest hash alone is not sufficient authorization.
3. **Bounded retry:** if unsuccessful attempts consume a positive retry budget, eventually select an eligible alternative or an explicit recovery/error state. Do not reset the budget on each crash and loop forever.
4. **Merge consistency:** `merge_in_progress ⇒ preserve_required_base_snapshot_metadata`. Resume must interpret one coherent durable state; incompatible destructive operations are rejected.
5. **Trust monotonicity:** recovery never silently lowers rollback policy or disables verification to achieve a boot. Coordinate policy advancement with the actual platform update contract so required recovery paths remain eligible.
6. **Health separation:** `boot_marked_successful` does not imply `UI_healthy`, `data_preserved` or `all_apps_compatible`. Track those as separate evidence.
7. **AI containment:** experimental policy may change only validated performance controls; it cannot write boot slots, change trust policy or suppress an independent recovery watchdog. This is a proposed design boundary, not enforcement supplied by this plugin.

## Two falsifiable experiments without device operations

**1. Interrupted-update state model.** In a regular-file or in-memory model, enumerate transition prefixes around candidate writes, verification, activation, boot attempts and merge-state commits. Inject a modeled interruption after each persistent step. Define torn-write behavior and shared-storage assumptions explicitly. Assert recovery reachability, retry termination and merge consistency on every restart state. A counterexample trace falsifies the model's claimed invariant. A pass proves only the enumerated model, not flash firmware or real power-loss behavior. No physical power cut or flashing is part of this experiment proposal.

**2. False-health and watchdog model.** Feed synthetic heartbeats while the modeled UI makes no progress, then simulate a missing heartbeat, repeated candidate failure and stale AI-policy output. Assert that heartbeat receipt alone cannot mark UI health; the retry budget terminates; stale policy falls back; diagnostics distinguish detection, reset, recovery and data-preservation results. Failure is an infinite retry, false healthy label, or policy authority beyond its allowed actions. A pass does not establish real watchdog independence, timeout accuracy or hardware reset functionality.

Both experiments are proposed, not reported as run. The useful product claim is “recovers from enumerated faults under stated assumptions, with these tested exceptions,” not “cannot brick or break.”
