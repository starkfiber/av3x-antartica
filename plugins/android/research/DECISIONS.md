# V2 debate: decisions and unresolved questions

These are conclusions from the actual Android and Apple reviewer exchange, with primary citations and complete arguments in android-position.md and apple-position.md. They are research proposals; no new Google/Apple vulnerability, priority claim or device benchmark was established.

| Proposition | Android position | Apple position | Resolution |
| --- | --- | --- | --- |
| Consistent UI | Material conventions and inspectable framework paths support consistency; custom stacks increase variability | Shared system controls, semantic type and accessibility conventions reduce repeated design work | Test task completion, clipping, focus and state restoration across text sizes/locales; avoid judging by pixel identity |
| Smoothness | Frame pacing and visible buffer/fence mechanisms help diagnosis | MainActor discipline, bounded work and platform tracing help responsiveness | Neither priority nor requested refresh guarantees deadlines; measure queue age and tail latency |
| Platform integration | Flexibility supports varied hardware and applications but enlarges validation matrix | Tighter integration can reduce combinations to validate | Plausible advantage under declared conditions, not proof of superiority for every app |
| What teams missed | Candidate failures include queue-age blindness and inconsistent lifecycle/layout boundaries | Candidate failures include actor-inherited UI work and over-trusting asynchronous submission | Reproduce and falsify before calling these OS bugs; novelty requires prior-art investigation |

## V2 priorities

1. A claim ledger: observation, derivation, hypothesis, counterevidence, exact target and experiment status.
2. A frame-path evidence view connecting CPU running/waiting, IPC, GPU completion and presentation. Imported traces must identify their clock domains and measurement uncertainty.
3. A UI invariants matrix: large text, localization, keyboard/insets, resizing, focus, accessibility and lifecycle restoration.
4. A reproducible experiment pack with fixed workload versions, thermal/refresh state and latency distributions. Runtime operations require an identified test target.
5. A counterexample-first comparison view that retrieves dissent and missing evidence alongside each proposed advantage.

## What would count as an original finding?

A concrete reproducible failure, exact affected versions, minimized trigger, alternative explanations tested, and a prior-art search supporting the claimed novelty. None of the hypotheses has reached that threshold here. The first useful experiment is distinguishing queue depth/input age from average FPS; then investigate blocked UI work and state-boundary correctness. A platform team may already know a mechanism even when one application integrates it incorrectly.
