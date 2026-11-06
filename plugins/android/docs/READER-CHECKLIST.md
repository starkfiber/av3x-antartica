# Rendered reader acceptance

The current gate is open. On 6 November2026 the browser automation tool rejected the local file protocol and explicitly prohibited workarounds for that denial. No localhost mirror, alternate browser surface or indirect preview was used. Code/DOM/MCP checks cannot substitute for rendered keyboard or screen-reader feedback.

An authorized person can open atlas/index.html in their own browser and record:

1. Browser/OS, package version, viewport and appearance. Check readable text/controls and no unintended horizontal overflow at a narrow width and200%zoom.
2. Tab/Shift-Tab order and visible focus. Activate Skip to article with the keyboard; confirm focus reaches the article heading, including the no-results fallback.
3. Search, platform/evidence filters, no-results state and recovery. Confirm focus remains usable and counts reflect the selected state.
4. Select a topic and a section using the keyboard. Confirm expected content/focus and source disclosure behavior; do not navigate private evidence locators as though they are public downloads.
5. VoiceOver or another named screen reader: meaningful control labels, topic/current state, headings, result-count feedback and usable article traversal. Record actual feedback; an accessibility-tree inspection alone is insufficient.

Retain actual pass/fail results and limitations. A text-only human report can establish the reported scenarios, but do not infer untested combinations or a universal WCAG conformance claim. Keep the gate open wherever required observations remain missing. Historical V1 findings and failed attempts remain preserved.
