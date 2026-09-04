# Earthworm simulator — pre-GitHub audit, 1.3.3-beta

Date: 4 September 2026  
Status: revised source candidate under GitHub verification. **Android compilation, APK generation and publication have not yet succeeded.**

## What was corrected

| Finding | Revision | Evidence |
|---|---|---|
| The native pause hook stopped speech but did not cancel a silent procedure's auto-advance timer. | A shared pause handler stops playback, clears the procedure timer, cancels narration and saves state. Native pause and page-hide use it. | New regression test failed against 1.3.2 at “Native pause cancels silent procedure timer”; it passes after the fix. |
| Native interruption had no explicit completion event for the web narration job. | Added Android onStop → voice-cancelled handling; matching jobs resolve as cancelled and stale events are ignored. | Web/native message contracts pass; actual Android callback behavior remains untested on a device. |
| Old WebView error/message callbacks could affect a replacement view. | Added current-view checks and cleanup of speech, print and pending Back state during recovery. | Source review and Java syntax parsing only. |
| Back handling could stay pending indefinitely if JavaScript did not answer. | Added a 1.5-second guarded fallback that offers a deliberate close choice, with stale-callback and lifecycle cleanup. | Source review and syntax parsing only; native timing requires device verification. |
| Print-adapter creation occurred outside the error cleanup; an old completion could affect a later print job. | Moved setup inside the guarded path; added request-specific completion and safe reply cleanup. | Source review and syntax parsing only; printing remains a device acceptance item. |
| Previous visual evidence overclaimed what an approximate SVG export could establish. | Explicitly withdrew those acceptance claims, segregated historical proofs and required real-browser evidence. | The first real Chromium run completed and correctly stopped the build at a demonstrated procedure-stage defect. |
| The probe animation replaced, rather than composed with, the probe group's SVG translation. | The animation now carries the base 260 × 212 translation through all keyframes. | GitHub Chromium run 1 placed the probe label above the viewport in English and Tamil at all three widths; 67 browser tests passed and six procedure tests stopped at this defect. |

Android documents onStop separately from normal utterance completion. This supports handling cancellation explicitly; it does not prove that the revised native path has executed successfully here. [Android utterance callbacks](https://developer.android.com/reference/android/speech/tts/UtteranceProgressListener).

## Evidence-bounded diagram change

The full anatomy SVG remains **byte-identical to 1.3.2-beta**. One CSS keyframe was changed in response to direct Chromium geometry evidence. Scientific records, all question answers, procedure scripts, microscopic lessons and Tamil spoken introductions still match frozen content hashes.

Inventory retained: 55 structure records, 56 guided actions, 72 questions, nine microscopic panels and nine sections including preparation and transverse section.

The earlier black-box, “weak/broken” artwork and normal-view label-collision findings were not established by faithful live-app rendering. The redesigns already present in 1.3.2 are retained, but are not presented as proven repairs to those alleged defects. No rollback or new artwork redesign was made in this revision.

The earlier “264 labels without overlap” statement is withdrawn as browser/mobile acceptance evidence. It was a measurement of approximate exports with a tolerance, not proof of how students see the app. Historical files remain under review/legacy-extractions with an explicit warning.

## SVG verification protocol now included

The new inventory covers 1 inline style block, 355 leaf CSS rules including font and keyframe rules, 57 conditional rules, 40 SVG class tokens and 25 definition IDs. This is an inventory—not a substitute for computed browser styling.

The browser suite serves the exact bundled HTML bytes. It does not extract organ groups, rewrite CSS, substitute Tamil fonts or manually reconstruct transforms.

Its renderer-capability project precedes the application projects and isolates transparency, hidden layers, gradients, paint order, turbulence and backdrop blur. An unsupported renderer feature must be treated as a tooling limitation, not an anatomical defect.

Application projects cover English/Tamil views at desktop, 390-pixel and 360-pixel widths; both external surfaces; all procedure stages; microscopic panels; selection, zoom, contrast and print state. They are configured to attach complete-page screenshots and geometry. Label-box intersections are recorded for inspection rather than automatically declared collisions. [Playwright configuration](https://playwright.dev/docs/test-configuration), [CI setup](https://playwright.dev/docs/ci-intro).

## Checks actually completed

| Check | Outcome and limit |
|---|---|
| DOM/data interaction tests | **1,491 passed**, including added microscopic-panel DOM-ID and SVG-reference checks. Not layout or touch testing. |
| Tamil speech and lifecycle contracts | **238 passed**, including pause, page-hide, cancellation and stale-event cases. Speech events are simulated; no listening assessment. |
| Source preflight | Passed version/hash consistency, educational-data preservation, reviewed SVG/style baselines, complete rule inventory, offline-resource checks, workflow structure, XML and JavaScript syntax checks. |
| Native Java | Both application and instrumentation source files parsed successfully as Java 17. No Android symbol/type checking, bytecode compilation or device execution is implied. |
| npm dependency audit | No known advisories reported for the locked npm development dependencies at the time checked. Not an Android dependency scan or a security certification. |
| First full-browser run | **67 passed and six failed** at one reproduced probe-label overflow across English/Tamil and three widths. The targeted correction must pass the current GitHub rerun before browser acceptance. |
| Build configuration | Pinned JDK 17 / Gradle 8.13 / AGP 8.13.2 / SDK 36 settings match the published compatibility baseline. Actual dependency resolution and build remain pending. |

Local browser access and Android tooling remain unavailable. Real Chromium execution is performed by the repository workflow; its current result controls acceptance.

The pinned Android baseline supports Gradle 8.13, JDK 17 and the selected SDK level. [AGP compatibility table](https://developer.android.com/build/releases/agp-8-13-0-release-notes).

## What GitHub should do next

The revised workflow has a read-only repository permission and two dependent jobs:

1. **validate** — install locked test dependencies; run source, data, speech and Java-syntax checks; install the pinned Chromium engine; run renderer fixtures followed by full-page application tests; retain reports on failure.
2. **build** — only after validation passes, run Android lint and compile debug, instrumentation and unsigned release candidates.

No signed production release or automatic publication is configured. Test results, screenshots and real build logs must be reviewed before distribution. A browser-gate failure is a diagnostic stop, not a reason to bypass validation.

The archive contents are now in the repository root with .github/workflows/android.yml in place. The README supplies exact commands and artifact names.

## Remaining acceptance requirements

- Execute the browser suite and review screenshots, especially Tamil shaping, diagram occlusion, print output and small-screen controls.
- Compile and run Android lint; install a real test APK and run the instrumentation test.
- Test background/foreground, recovery, Back, print failures and speech interruption on a low-memory phone.
- Listen to Tamil technical vocabulary and sentence boundaries with the intended installed local voice. Device speech is not equivalent to recorded human narration.
- Obtain faculty sign-off on the existing documented practical-convention/specimen differences. Those scientific caveats were preserved, not silently resolved.
- Confirm application identity and use an owner-controlled release signing key. Debug CI keys may differ between runners; uninstalling to resolve a signature mismatch removes local progress.

## Delivered identity

Application ID: in.ramesh.zoology.earthwormlab  
versionName: 1.3.3-beta  
versionCode: 10303  
HTML bytes: 549328  
HTML SHA-256: d22bd650486a0dfd94f1d8513fbcc95ae7f2d331762d684ce8567d6fcbf00c9c

The standalone HTML and bundled Android asset are byte-identical. This audit supersedes the visual-certification wording in the preceding audit.
