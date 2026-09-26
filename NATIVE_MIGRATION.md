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

## Academic parity gate — PARTIALLY RECONCILED

The authoritative v1.3.8 data objects have now been extracted into `res/raw/earthworm_content_v138.json` with source provenance pinned to commit `ac47a83762ae2ecbaded7b077f25702daf2d1fa8` and HTML SHA-256 `d9b8cca85459764f4169edf4a74aa2e159ea5281968fd49586b15c69176795ed`.

CI enforces these exact migrated counts:

- 55 structure records
- 56 guided actions
- 72 assessment questions
- 9 microscopic lessons
- 9 systems
- 7 top-level preparation procedure steps

All 55 structures are exposed through the native structure index, all 56 actions through the native guided flow, all 72 questions through the native assessment activity, and all nine microscopy lessons through the native microscopy screen.

**Visual parity is not yet passed.** Browser SVG strings were intentionally omitted from the native dataset. Native anatomical plates must be reconstructed and audited separately.

Before native production release:
1. **DONE:** migrate and count-reconcile all 55 structure records.
2. **DONE:** migrate all 56 guided actions into native data and a native guided flow.
3. **DONE:** migrate all 72 questions with preserved answer indices and bilingual options.
4. **DONE:** migrate all nine microscopic lesson texts.
5. **OPEN:** complete English/Tamil UI-resource parity and scientific terminology review.
6. **IMPLEMENTED / QA OPEN:** native system-specific Canvas plates and all nine microscopy/deep-dive Canvas plates now exist; academic/device acceptance is tracked in `NATIVE_DIAGRAM_AUDIT.md`.
7. **OPEN:** add accessibility semantics/content descriptions and large-font testing.
8. **OPEN:** add instrumentation tests for persistence, rotation, process death, predictive Back and offline launch.
9. **OPEN:** perform physical-device QA.
10. **BLOCKED:** production merge/release until all open gates pass.

The v1.3.8 HTML remains recoverable from Git history at commit `ac47a83762ae2ecbaded7b077f25702daf2d1fa8`; it is not required as a native runtime component.
