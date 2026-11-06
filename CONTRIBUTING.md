# Contributing

Start with an Issue for a bug, evidence correction or focused feature. Include the package version, expected and observed behavior, a minimal non-sensitive example, and relevant primary sources. We welcome code, documentation, accessibility verification and independently reproduced findings.

Work in plugins/android for payload changes. Its docs are bundled with the installed plugin. Keep the Android skill focused on decisions/workflow; substantial task-specific explanations belong in references or atlas articles. Root README is the public entry point.

For a new finding, record the question, exact source/build/date, observation versus inference, retained evidence and limitations. Add a unique topic ID and catalog metadata, run `node scripts/build.mjs`, then `node scripts/check.mjs`. Building accepts reviewed bytes as a new catalog snapshot. Never rebuild merely to trust hostile content.

Tests run with existing Node22+ and no npm install. Use isolated temporary fixtures; never select the first connected device. Device execution, accounts, publication and external messages need matching user authorization. No cloud credentials or privileged hardware are needed for repository checks.

Do not include signing keys, firmware, private manuals, user data or location trails. Preserve upstream license notices. Use small original explanations and primary links rather than copying articles. Contributions are under the repository's MIT license for original material; identify separately licensed material explicitly. No CLA or DCO requirement is imposed by this repository.

A pull request should explain the concrete problem, resulting behavior, evidence and checks, with material limits. Retain meaningful failures; a separately corrected test does not retroactively make the original suite pass. Maintainers review before merge and versioned publication.
