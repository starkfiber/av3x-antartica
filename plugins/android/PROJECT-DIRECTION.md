# Governing direction — foundation-first systems creation

User direction recorded6November2026, Asia/Kuala_Lumpur. This supersedes competitor-led framing in earlier roadmaps. Existing experiments remain historical evidence; their verification limits do not change.

## Purpose

Build a research, learning and engineering environment for people to understand, create and validate their own systems. Android, iOS and other existing platforms are reference implementations, compatibility targets and sources of lessons. They are not the definition of success or the visual identity to reproduce. The user anticipates widespread AI-assisted OS creation; treat that as the motivating vision, not an established prediction.

The existing `$android` entry remains the practical bootstrap and compatibility entry point. Broader OS architecture proposals must identify which capabilities the plugin actually implements, which it models, and which require a future kernel, runtime or device implementation.

## Foundations to learn

Machine code and ISA behavior; assembly and calling conventions; operating-system architecture; kernel mechanisms; Android implementation; iOS implementation. Connect all six rather than collecting isolated terminology. Trace a behavior through application/runtime → ABI/system interface → kernel → driver/hardware → observable result. Separate ISA facts, source observations, compiled bytes and measured execution.

## Governing qualities

1. Security: explicit trust boundaries, least privilege, isolation and authenticated changes.
2. Stability: correctness, bounded resource use, failure containment and tested recovery.
3. Efficiency: useful work per unit of time, memory, storage and energy.
4. Environmental responsibility: measure energy per successful task at fixed quality and report measurement boundaries. Energy savings alone do not establish a full lifecycle carbon benefit.
5. Model accuracy: held-out evaluation, calibration, uncertainty, drift detection and abstention/fallback.
6. AI readiness: structured telemetry, reproducible experiments, bounded decisions and auditable policies. AI must account for its own overhead.
7. Compatibility: explicit versioned contracts and evidence for each supported combination.
8. Human agency: understandable controls, accessible interaction, inspectable decisions and reversible experimentation.

## Compatibility is an engineering programme

The ambition is broad reuse across devices and platforms. Each claim must name its layer: ISA/ABI, executable format, system calls, runtime, framework/API, services, graphics, input, storage or application behavior. Record supported versions, dependencies, translation/emulation costs, test cases and unsupported capabilities. Matching one layer never establishes whole-app compatibility. Fail explicitly where a contract is absent; do not silently invent an equivalent behavior.

## How future work is selected

Choose a user-visible contract and a bounded experiment. Identify correctness/security requirements first; then quantify latency, tail behavior, energy, memory, compatibility and prediction quality. Keep comparable workload/output quality, failure cases and uncertainty. Prefer reducing unnecessary work, movement and waiting before adding adaptive complexity. A learned policy must outperform a competent simpler baseline within stated constraints.

Preserve project-local findings, source identities, failed approaches and open checks. Clearly label observation, derivation, hypothesis and measured result. Novelty requires prior-art review and independent reproduction. No universal speed, compatibility, zero-lag or unbreakable-hardware claim follows from the vision.

## Scope and continuity

This direction does not authorize flashing, recovery, privileged device changes or production takeover. Ru’OS remains outside this case unless explicitly reintroduced by the user. No shared/remote memory write or publication is implied. Maintain this direction in the source checkout and project-owned case; carry it into reviewed plugin releases.
