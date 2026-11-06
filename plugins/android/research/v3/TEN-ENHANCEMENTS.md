# V3: ten executable contracts, with explicit scope

This preview adds a pure validation engine, android_v3_check, plus source-backed references. It does not replace Android's scheduler, system apps, recovery firmware or hardware protection. Results are derived from caller-supplied data; no actual system action executes.

| # | Contract | Runnable result | What remains unproved |
| --- | --- | --- | --- |
| 1 | response-age | Compare input/state timestamp to presentation timestamp and age budget; reject reversed clocks | Physical input-to-photon latency needs calibrated measurement |
| 2 | recovery | Require verified candidate, authorized rollback, retry budget and recovery availability | Cannot validate these booleans or prevent physical failure |
| 3 | dependency-waits | Classify supplied disjoint running/blocked/runnable/GPU waiting intervals | Largest category is not a causal diagnosis |
| 4 | deadline | Account for critical path, controller, transition and safety margin before admission | Predicted tails are not worst-case guarantees |
| 5 | state-freshness | Compare provider/render versions and observation age | Provider authority does not guarantee instant propagation |
| 6 | generation | Reject stale asynchronous results and unknown future generations | Application must enforce the decision at mutation boundary |
| 7 | backpressure | Enforce finite queue capacity; distinguish optional loss from required work | Do not apply a frame-dropping policy to durable writes |
| 8 | thermal | Conservatively refuse more work for stale readings or severe status | Does not control frequencies or override thermal protection |
| 9 | fairness | Check bounded waiting and progress of required work | A snapshot is not an OS-wide starvation proof |
| 10 | claim-evidence | Require reproduced runtime evidence and matched quality for equal-quality comparisons; never auto-certify novelty | Prior-art investigation and independent reproduction remain necessary |

The eight additions after response-age and recovery are implemented as testable predicates, not eight proven mobile performance gains. Call android_v3_check with the contract ID and exact typed data; missing, extra, non-finite and invalid fields fail validation.

Example:

```json
{"contract":"deadline","data":{"budgetMs":8.33,"criticalPathMs":6,"controllerMs":0.5,"transitionMs":0.4,"marginMs":1}}
```

Required work is7.9ms, leaving0.43ms in this arithmetic model. This is not a measured phone deadline result.

## New observations in this investigation

Pinned SystemUI UserSettingObserver source separates cached reads, provider writes and observer refresh. Provider write does not directly update the observer cache; registration completion and setting-change paths reread state. This is a new retained source observation for this case, not an undiscovered race or Google defect.

Recovery review distinguishes boot-success metadata from usable UI health. A/B fallback, AVB authorization and Virtual A/B merge state have different responsibilities. Existing mechanisms are prior art; software cannot guarantee a device survives physical storage, power or board failure.

The reader review found source-level focus-transfer, heading and skip-navigation defects. Those were fixed and DOM-regression tested. Pinned official Now in Android and AndroidX references inform a separate native UI backlog; the previously compiled demo APK is unchanged.

## UI integration

The reader now has a skip link, semantic section navigation, one page H1, shifted article heading levels, focus transfer on section/topic selection, and safe recognized Markdown links. It retains visible focus and native controls. DOM tests exercise real reader JS and focus/click events. Native keyboard traversal, rendered layout/contrast and actual screen-reader checks are still not claimed.

See the core-apps and recovery skill references, BLUEPRINT.md, v3-scheduling.md and v3-challenge.md for mechanisms, equations, sources, experiments and limitations. See the project-local epoch9 for the pinned upstream UI review.
