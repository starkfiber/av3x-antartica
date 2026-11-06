# Upstream UI/UX review: HTML reader and native Compose demo

Reviewed 2026-11-06. Read-only local source review plus primary upstream retrieval; no plugin/demo code edited. No live browser, browser-policy workaround, emulator, device or screenshot test was attempted. The previous local-file-preview block remains a separate acceptance limitation. Findings describe code behavior and engineering implications, not observed screen-reader speech or rendered layout defects.

Reviewed reader template SHA-256: `efd6b1072dfb2ddb5f8860a1b5304fc7a24f7ea575f33b3768a2aba1a38a551b`. Native `MainActivity.kt` SHA-256: `a66fd75a14426ba9fdfb07b233321ef1c3b42df6e345ddc33f1e8461c3bcfae5`.

## Primary GitHub anchors

Resolved repository heads through the GitHub commits API, then read exact-commit raw files in memory. No signed-commit verification or upstream build was performed.

| Upstream | Resolved revision | Observed use |
|---|---|---|
| android/nowinandroid main | `2982e63cb691ad49cd009aab43dc206fee6699c2`, commit date2026-10-20 | README describes adaptive layouts and screenshot testing, including host-platform baseline differences. NiaApp uses adaptive window information, a navigation-suite scaffold and explicit consumption of scaffold padding. |
| androidx/androidx androidx-main | `6b5236195fc4a29252f0de76cb8c56b267509d19`, commit date2026-11-05 | Material3 Scaffold documents padding ownership/consumption. NavigationSuiteScaffold chooses navigation forms from window adaptation information. |

Exact files and observed SHA-256:

- [Now in Android README](https://github.com/android/nowinandroid/blob/2982e63cb691ad49cd009aab43dc206fee6699c2/README.md): `d6eeade4c74e4cb521987ac2fe88b43bf90dd16610c27019b337ff6e04bd5536`.
- [NiaApp.kt](https://github.com/android/nowinandroid/blob/2982e63cb691ad49cd009aab43dc206fee6699c2/app/src/main/kotlin/com/google/samples/apps/nowinandroid/ui/NiaApp.kt): `689980be44418a06cc4381df0874afde3819f47de0985c8fb8c0234540316fd4`.
- [Material3 Scaffold.kt](https://github.com/androidx/androidx/blob/6b5236195fc4a29252f0de76cb8c56b267509d19/compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/Scaffold.kt): `051005662a2dce50b9eb67a55397de39d75f371f42ba484224f2193f64735bd7`.
- [NavigationSuiteScaffold.kt](https://github.com/androidx/androidx/blob/6b5236195fc4a29252f0de76cb8c56b267509d19/compose/material3/material3-adaptive-navigation-suite/src/commonMain/kotlin/androidx/compose/material3/adaptive/navigationsuite/NavigationSuiteScaffold.kt): `7371ced16e203d121687d388d56b68bbbd18743c959c95e8b837159f9123245d`.

The first attempted adaptive source path incorrectly placed the module under `compose/material3/adaptive/` and returned404. The correct path above was read successfully. An initial local extraction command also had an unmatched parenthesis, failed before acquisition, and was corrected. Neither failed attempt supplies evidence.

These are **source snapshots**, not release pins for the existing Compose BOM2025.04.01 demo. Do not copy Navigation3/new adaptive APIs from current NiaApp into that project without choosing a compatible library version and compiling it. The relevant reusable ideas are padding ownership, responsive navigation, state preservation and realistic test coverage—not copying Nia's complete dependency graph.

## HTML reader: changes supported by source inspection

The reader is plain HTML/CSS/JavaScript, not Compose. Android's semantics, FocusRequester and Scaffold APIs do not apply to its DOM. Use native HTML elements and web accessibility guidance here.

### H1 — Make section navigation move keyboard focus

In `show()`, generated heading links call preventDefault and scrollIntoView only. The heading is not focusable and focus is never moved. Consequently that handler changes the viewport without explicitly moving the keyboard navigation starting point to the requested section. Give destination headings `tabindex=-1`; on activation focus the heading and scroll predictably. Do not steal focus when filtering on every keystroke. Preserve the selected article context in any shareable section URL rather than replacing the topic identity with an unresolvable section-only fragment. Test Enter/Space as appropriate to the actual native element and Tab continuation from the destination.

### H2 — Supply an efficient route past the topic list

The current DOM places all topic buttons before article contents, with no skip link. Add a visible-on-focus or always-visible “Skip to article” link targeting a focusable article title/content start. Also consider an “Article contents” navigation landmark with its own skip target. This reduces keyboard traversal without replacing buttons with a complicated custom listbox. W3C's technique describes both visual visibility and actual focus movement; it is guidance, not an assertion that this source review establishes a WCAG violation. [W3C skip-link technique](https://www.w3.org/WAI/WCAG22/Techniques/general/G1).

### H3 — Normalize the reader's document hierarchy

The page has a site H1, article-title H2, then a second copy of the article title rendered from Markdown as H1. The original body hierarchy restarts inside the article. Render one article title H2, omit or map the matching first body H1, then make article sections H3 and preserve relative nesting through H6. Avoid mechanically flattening all sections to one rank. Associate `<article aria-labelledby="title">`; replace the generic `div aria-label="Article headings"` with an actual `<nav aria-label="On this page">` or an appropriately labeled region. Multiple H1s alone are not the claimed defect—the duplicate title and hierarchy reset are the concrete source issue. [W3C heading organization](https://www.w3.org/WAI/tutorials/page-structure/headings/).

### H4 — Make reading links and structures usable without accepting raw HTML

The current renderer places nonheading lines into paragraph.textContent. This is a good injection boundary, but Markdown links, tables, lists and code fences display literally. Internal topic citations therefore do not function as reading navigation. A bounded renderer can support paragraphs, safe links, lists, fenced code and simple tables by constructing DOM nodes. Resolve internal links against a catalog allowlist; permit only explicit HTTP(S) for external anchors; render case-local evidence paths as nonportable locators. Never introduce innerHTML of untrusted article text as a shortcut. Add a short visible “opens external browser/new tab” cue where appropriate. Test malicious schemes and malformed Markdown as parser boundaries, separately from visual usability.

### H5 — Preserve strengths and avoid noisy updates

Existing strengths: labeled native search/select controls, named topics navigation, visible focus styles, result-count status region, native details/summary and textContent-based output. Keep those. Filtering replaces the entire article on every input event; preserve input focus and consider announcing only the count, not the full article. If source sections are open and the same article remains selected, avoid gratuitously resetting reading/disclosure state. On narrow screens the topic list stacks ahead of the article; a compact topic disclosure or jump-to-results control is a reasonable enhancement, but actual viewport/keyboard testing must decide the final presentation.

## Native Compose sample: specific improvements

### C1 — Consume Scaffold padding before IME padding

At MainActivity.kt line122 the LazyColumn modifier is `fillMaxSize().padding(padding).imePadding()`. It applies visual padding but does not tell descendant inset modifiers that Scaffold's insets were consumed. Add `consumeWindowInsets(padding)` at the relevant boundary before further IME handling, while retaining a single owner for system-bar/top/bottom-bar padding. Verify search keyboard open/close, gesture versus three-button navigation and landscape; do not claim the exact rendered gap without that test. Both the pinned Scaffold contract and official documentation describe consumption explicitly. [Material3 inset guidance](https://developer.android.com/develop/ui/compose/system/material-insets).

### C2 — Expose expansion state and focused actions

The lesson card toggles expanded content but supplies no explicit expansion state description. Add a localized `stateDescription` for expanded/collapsed and an action label that explains the toggle. Keep the nested source-opening button separately discoverable; do not merge/clear the entire card's child semantics to hide it. Result counts can use a polite live region if testing shows helpful announcements; avoid assertive speech on every search character. Existing native Material buttons, chips, slider roles and visible labels should be preserved. [Compose semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics).

### C3 — Keep each destination's reading position

Learn, Lab and Evidence share one LazyColumn call/default remembered list state. Switching the data at that call site can reuse a previous destination's scroll position. Use destination-keyed list state or separate screen composables with appropriate saved state. Define back behavior for an expanded lesson and explicit focus restoration after returning from an external source. FocusRequester can manage keyboard focus from an event or effect; it is not proof of TalkBack focus behavior, and requests should not run directly in a composable body. [Official focus requests](https://developer.android.com/develop/ui/compose/touch-input/focus/request-focus).

### C4 — Adapt to window space, then add theme/localization support

The demo always uses bottom navigation and one full-width list. For a larger-window version, evaluate a compatible navigation-suite dependency or explicit rail/bar layouts, plus bounded reading width or a list/detail layout. Base this on available window dimensions, not a device-name guess. The upstream adaptive scaffold is an example of that decision, not a mandate to upgrade the demo. Official guidance describes changing navigation with available space. [Adaptive navigation](https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation).

The hardcoded light scheme and inline English strings are acknowledged demo limitations. Add a coherent dark scheme and resource strings/plurals before claiming theme/localization support. Do not assume dynamic color alone guarantees contrast. Add previews for compact/expanded windows, large fonts and both themes; screenshot comparison should run on a controlled host baseline, following Nia's documented host-variation caution. Screenshot results still cannot prove accessible names, focus movement or TalkBack interaction.

## Suggested order and acceptance boundary

First implement H1–H3 and C1–C3; they address concrete navigation/state/inset mechanics with limited dependency impact. Then H4 and responsive reading refinements, with parser safety checks. Theme/localization and larger-screen Compose adaptations can follow as explicitly scoped enhancements.

Source checks can verify DOM heading structure, allowed URLs, event handlers, semantics configuration and compile correctness. Actual acceptance still needs browser keyboard traversal, focus visibility, zoom/narrow layout, light/dark rendering, plus an authorized native runtime for IME, large fonts, navigation restoration and accessibility-service behavior. This review provides no fabricated rendered result and does not bypass the existing browser preview restriction. The referenced GitHub projects are public examples/source, not installed Codex skills; no third-party skill was installed or its instructions adopted.
