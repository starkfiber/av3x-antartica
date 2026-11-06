# Apple position: consistency mechanisms, measurable limits

## Position and evidence boundary

Apple's strongest defensible argument is a reusable set of platform conventions and measurement tools, not flawless UI or universally faster hardware. Standard text views, semantic fonts, adaptation APIs and view-update tooling give developers mechanisms for reducing inconsistencies and diagnosing delays. Whether a particular app uses those mechanisms correctly is an empirical question. This round refreshed official documentation; it did not execute an app, measure an iPhone, or prove a defect in iOS 27.0.1. Earlier iBoot inspection does not supply UI-performance evidence.

## Concrete mechanisms and their failure boundaries

**Text consistency has APIs behind it.** Apple documents standard text views with system defaults and adaptation to appearance/accessibility settings. UIKit text styles describe the purpose of text; `preferredFont(forTextStyle:)` and `adjustsFontForContentSizeCategory` support user-selected size changes. Custom fonts need appropriate scaling through font metrics. These mechanisms reduce independent typography decisions, but a fixed-height container or an incorrectly configured custom control can still clip content. “Uses a system font” is not a complete accessibility proof. [Text display overview](https://developer.apple.com/documentation/technologyoverviews/text-display), [Scaling fonts automatically](https://developer.apple.com/documentation/uikit/scaling-fonts-automatically).

**Responsiveness has an execution boundary.** Apple recommends keeping non-UI work off the main thread and warns that Swift concurrency code can accidentally remain on `MainActor`. `async` syntax alone does not demonstrate background execution or timely UI service. Thread placement, required UI work and dependency waits must be inspected in a trace. [Improving app responsiveness](https://developer.apple.com/documentation/xcode/improving-app-responsiveness).

**Declarative updates still cost time.** Apple's SwiftUI performance guidance identifies both long view-body calculations and overly frequent updates as causes of hitches, and provides Instruments tracks for investigation. That is an observability advantage, not proof that automatic view updates always stay within budget. [Understanding and improving SwiftUI performance](https://developer.apple.com/documentation/xcode/understanding-and-improving-swiftui-performance).

**Deferring work can move the failure.** Apple's own tutorial describes replacing eager views with lazy evaluation, then observing remaining hangs and scrolling hitches. Its sample targets Xcode 16/iOS 18; its measured improvement is not a benchmark for our retained iOS build. The general lesson is to check both initial appearance and later interaction after an optimization. [Reducing main-thread work by doing less](https://developer.apple.com/tutorials/instruments/reducing-main-thread-work-by-doing-less).

## Debate exchange with the Android advocate

The Android advocate argued that ART, task profiles, BufferQueue, Swappy/Perfetto and Material provide inspectable mechanisms and tuning/consistency tools; device/API/theme variability adds validation cost, not inherent inferiority. They challenged any Apple perfection claim on QoS, refresh-rate hints, application locks and GPU/memory constraints.

**Concession:** agreed. Neither platform earns a deadline or compatibility guarantee from an API name. Apple's own documented lazy-view tradeoff disproves the argument that choosing the native framework alone prevents hitches.

**Rebuttal:** public source and trace tooling demonstrate inspectability. They do not establish a retail OEM's frame timing, input age or the behavior of its proprietary components. Likewise, Apple's shared typography conventions plausibly reduce custom implementation burden, but that burden reduction is a hypothesis until the same feature and accessibility cases are compared.

**Common test standard proposed:** identical content and interaction intent; declared device/OS/build, font and accessibility state; comparable presentation timestamps; cold and warm states; distinguish throughput from latency. Match outcomes rather than forcing both platforms to use an identical implementation that disadvantages one framework. No winner is assigned before measurement. The Android advocate accepted this rebuttal and added that localization belongs in the matrix and native conventions should be judged by equivalent usability, not pixel identity. The remaining dispute is empirical: validation effort and latency variation over a declared device/workload set.

## Three falsifiable failure experiments

These are proposed experiments, not performed tests or established OS bugs. Execution would require a separately identified test target and appropriate scope.

| Experiment | Hypothesis and controlled change | Measurements and failure criterion | What would refute the hypothesis |
| --- | --- | --- | --- |
| Text adaptation under constrained layout | An app with fixed row height or unscaled custom text clips essential content as text size increases; compare with adaptive system text styles/layout using the same content | Test default and accessibility sizes, a long localized string and right-to-left presentation. Record visible labels, overlap, focus order and reachability. Failure: essential content or action becomes unavailable, or announced meaning diverges from visible meaning | All specified states preserve content, action reachability and semantic order; the predicted clipping is absent. A pass is limited to that matrix |
| Deferred work moves a hitch | Lazy creation reduces initial work but moves expensive decoding/calculation into scroll-time view construction; compare eager, lazy, and independently precomputed content | Use equal data and image assets, declare cache state, capture view-update duration/main-thread activity and presentation intervals over repeated entry/scroll sequences. Predeclare a product-specific latency budget. Failure: reduced launch work accompanies increased missed presentation deadlines during scrolling | Scroll deadline distributions do not worsen with adequate repetitions, or a trace attributes delay to unrelated I/O/GPU work; reject the proposed causal attribution |
| More buffering hides stale input | Increasing allowed in-flight frames maintains throughput while increasing input-to-present delay under GPU pressure | Change only resource-slot/queue policy in a controlled sample. Record input timestamp, frame identity, GPU completion and actual presentation in a common clock domain. Failure: higher input age beyond the predeclared budget despite acceptable FPS | Input age does not rise under the specified load, or submission/presentation timestamps show that queueing did not increase. A CPU/GPU-only timestamp is insufficient to claim visible latency |

Use an Android counterpart with its corresponding UI and trace mechanisms. Differences in hardware, workload quality, power state or clock domains must remain explicit confounders. A failed application experiment first proves that application's behavior in the tested state; calling it an OS bug additionally requires isolating an OS-contract violation.

## Practical V2 plugin features

1. **Claim cards with proof status.** Every comparison point carries platform/build/device, criterion, evidence kind, source date, local artifact hash when applicable, and a falsifier. Documentation, hypothesis, reproduced application failure and established platform defect must be separate values.
2. **Experiment cards beside V1 retrieval.** Preserve the read-only atlas; add structured proposed protocols with controls, metrics, clock domain, sample scope, pass/fail definition and explicit `not-run` status. A generated protocol must not be rendered as a test result.
3. **Comparability checks.** Flag missing target identity, different content/quality settings, unmatched cache state or absent presentation timestamps. Report “not comparable yet” rather than aggregating incompatible numbers into an OS ranking.
4. **Counterevidence and revision links.** A claim may link to a rebuttal, corrected version and reproducible result without overwriting historical evidence. The plugin-runtime review's immutable-file handling shows why amendments need new versioned paths.
5. **UI consistency matrix.** Retrieve typography, contrast, layout direction, text-size, motion and interaction-state criteria as separate checks. Passing one screenshot cannot imply accessibility or timing passes for all states.

No feature above is claimed implemented by this chapter. V1 remains the baseline reference; V2 should add traceable argument and experiment structure, not manufacture measurements from documentation.
