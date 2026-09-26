# Native Android migration — v2.0.0-alpha1

This branch begins the architectural conversion of Earthworm Virtual Laboratory from a WebView-hosted HTML simulator to a true Android runtime.

## Hard architectural rule

A native release must contain:
- no `android.webkit.WebView` usage;
- no AndroidX WebKit dependency;
- no packaged `assets/index.html`;
- no JavaScript bridge;
- no DOM/localStorage dependency for progress or assessment.

The application package remains `in.ramesh.zoology.earthwormlab`. The native generation starts at `2.0.0-alpha1 / 200001` so it cannot be confused with the signed 1.3.x WebView lineage.

## Implemented in alpha1

- Native Android launcher/dashboard.
- Native system navigation.
- Native interactive anatomy surface using Android Canvas hit-testing.
- Native text-to-speech invocation.
- Native progress persistence using SharedPreferences.
- Native assessment activity.
- Existing native Privacy screen retained.
- API 36 / minSdk 24 / Java 17 baseline retained.
- WebKit dependency removed.

## Academic parity gate — NOT YET PASSED

v1.3.8 contains 55 structure records, 56 guided actions, 72 questions and nine microscopic lessons. Alpha1 deliberately does **not** claim parity. The initial native dataset covers representative external, digestive, circulatory, nervous, excretory and reproductive structures and only a smoke-test assessment set.

Before native production release:
1. migrate and hash-reconcile all 55 structure records;
2. migrate all 56 guided actions into a native procedure state machine;
3. migrate all 72 questions with identical answer keys and explanations;
4. migrate all nine microscopic lessons;
5. add English/Tamil string-resource parity, including scientific terminology review;
6. reconstruct high-fidelity anatomy diagrams as native vector/drawable or Canvas layers;
7. add accessibility semantics/content descriptions and large-font testing;
8. add instrumentation tests for persistence, rotation, process death, predictive Back and offline launch;
9. perform physical-device QA;
10. only then remove the WebView lineage from release documentation and consider merging.

The v1.3.8 HTML remains recoverable from Git history at commit `ac47a83762ae2ecbaded7b077f25702daf2d1fa8`; it is not required as a native runtime component.
