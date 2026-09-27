# Earthworm Virtual Laboratory — v1.3.9 Pre-Release Issue Register

**Register date:** 27 September 2026  
**Release target:** 1.3.9 / 10309, only after all release gates close  
**Current application checkpoint:** `51eeb0fab6ce205107d500370225217b942d1357`  
**Base home checkpoint:** `55e9e4e71868c25383dba1432a08644f91f29dcc`  
**Package:** `in.ramesh.zoology.earthwormlab`  
**minSdk / targetSdk:** 24 / 36

## Basis and reconciliation rule

This register reconciles the detailed audit of
`Earthworm-1.3.8-SVG-Label-Renderer-v3-DEVICE-QA-CANDIDATE.apk`
(SHA-256 `76acdb2565eab284ca6d0e464156baebab6eafb4f2819c3a6ec56a3147a99113`)
with the current repository source.

The audit is treated as evidence, but findings caused specifically by the deliberate
debug/device-QA build are not misclassified as application-source defects. Likewise,
content present in JSON/repositories is not treated as a completed student-facing
feature unless current Compose navigation exposes and verifies that feature.

## Release decision rule

The final 1.3.9 release must not be produced until every **P0 RELEASE BLOCKER** is
closed. P1 items marked required for 1.3.9 must also close. Deferred items must not
expand the 1.3.9 scope.

## P0 — Release blockers

| ID | Finding | Reconciled evidence | Required action | Status |
|---|---|---|---|---|
| P0-01 | SVG label legibility on real devices | The heavy dark-stroke correction visibly degraded labels. The v3 renderer removes dark glyph stroke while preserving all 16 SVG source assets. | Device-test all 8 atlas families in EN/TA at normal view and 1×/2×/4×, portrait/landscape. Correct only demonstrated residual labels. | **OPEN — v3 build PASS, visual QA pending** |
| P0-02 | Production signed-release workflow is obsolete | Existing production release workflow still carries hybrid/WebView-era assumptions. | Replace it with native Compose release validation after application/device QA closes. | **OPEN** |
| P0-03 | Final version/signing identity | The audited APK is intentionally debug-signed and debuggable because it is a DEVICE-QA candidate. Source release buildType already declares `debuggable false`, R8/minification and conditional release signing. | After QA, stamp `1.3.9 / 10309`; build signed APK/AAB; record certificate SHA-256 and artifact hashes. | **OPEN — not an app-source defect** |
| P0-04 | Production merged-manifest hygiene | Audit found PreviewActivity and an exported ComponentActivity in the debug APK. Source manifest declares only `.MainActivity` exported; tooling/test-manifest dependencies are debug-only. | Inspect the **release merged manifest and final release APK/AAB**. Require no PreviewActivity, no unintended exported ComponentActivity, no test-only components. | **OPEN — verify release artifact before changing source** |
| P0-05 | Privacy policy does not fully match current native behavior | Current `PRIVACY.md` contains a Printing section although current native source has no print UI. It also describes broader progress/review fields than are clearly persisted. | Rewrite policy to actual 1.3.9 behavior only; remove stale printing text; narrow storage wording; publish one stable public HTTPS URL and use the same URL in-app and in store consoles. | **OPEN** |
| P0-06 | End-to-end physical-device acceptance | Static/build checks cannot prove clipping, Tamil shaping, gestures, rotation, TTS, process flow or privacy navigation. | Complete acceptance matrix on current candidate and repeat the critical subset on final signed release. | **OPEN** |

## P1 — Required quality gates for 1.3.9

| ID | Finding | Required action | Status |
|---|---|---|---|
| P1-01 | Tamil/English atlas fit and legibility | Verify all 16 SVG views for clipping, crowding and glyph shaping. Use screenshot-specific corrections only; no further global restyling without evidence. | **OPEN** |
| P1-02 | Zoom/pan/reset/rotation stability | Test each atlas at 1×/2×/4×, rotate while zoomed, reset, switch systems repeatedly, and confirm semantic zoom/focus/orientation remain coherent. | **OPEN** |
| P1-03 | Offline TTS | Test EN/TA narration in airplane mode and confirm no network-required voice is selected. | **OPEN** |
| P1-04 | Privacy access | Verify the in-app button, external-browser opening and safe Back return. | **OPEN** |
| P1-05 | Accessibility smoke test | Test TalkBack on home/atlas controls and system font scale up to 200% for clipping or inaccessible controls. | **OPEN** |
| P1-06 | Home presentation | Verify hero graphic, designer identity, institution, Tamil wrapping and narrow portrait layout. | **OPEN** |

## Content present vs. feature implemented

### Confirmed current user-facing navigation

`EarthwormApp.kt` currently routes only:

- `PREPARATION -> PreparationScreen()`
- the eight anatomical systems -> `AtlasScreen(...)`

There is no separate current app-shell destination for assessment, microscopy, quiz,
teacher dashboard or a standalone procedure module.

### Content/data present but not yet proven as a complete current UI feature

- 72 assessment questions and `AssessmentRepository.kt`
- assessment score persistence field
- guided/procedure reference data
- microscopy content in the scientific data model
- privacy-acknowledgement repository state

Therefore 1.3.9 store copy must not claim a complete assessment suite, microscopy
simulator, privacy-acknowledgement workflow, teacher dashboard or standalone
procedure module unless those UI flows are implemented and device-tested before
release.

## Privacy-policy reconciliation for 1.3.9

Current verified native behavior includes:

- no INTERNET permission;
- no advertising SDK;
- no analytics SDK;
- no WebView application shell;
- AppCompat-managed app-language preference;
- local last-system / visited-structure / assessment-score fields in DataStore;
- local privacy-acknowledgement repository field;
- Android TTS with offline-capability checks;
- user-initiated external-browser opening for the privacy URL.

Policy statements requiring correction before production:

1. Remove **Printing / Save as PDF** unless a real native print action exists.
2. Narrow **guided-learning progress / review dates / related study-state settings** to fields actually persisted by the released app.
3. Retain no-account/no-analytics/no-advertising/no-remote-collection language only while final release verification confirms it.
4. Host the final policy at one stable public HTTPS address and use exactly that address in the app and store listing.

## Debug-artifact findings — do not fix application source prematurely

The audited QA APK reports:

- `android:debuggable=true`
- Android Debug certificate
- debug/development component surface

Those are expected for a device-QA debug APK. They become blockers only if they
survive into the final release artifact.

Current source already declares:

- `release { debuggable false }`
- `minifyEnabled true`
- `shrinkResources true`
- conditional controlled release signing
- `ui-tooling` and `ui-test-manifest` as `debugImplementation`

The correct action is therefore to inspect and gate the **final release artifact**,
not alter validated application code solely because of the debug manifest.

## Scientific/content freeze for 1.3.9

Do not change without a separately demonstrated scientific defect:

- 55 scientific structures;
- 55 hotspot definitions;
- 8 authoritative HQ WebPs;
- 16 EN/TA SVG scientific assets;
- Phase-3C.1 digestive scientific corrections;
- `earthworm_content_v138.json`;
- `StructureGeometry.kt`;
- `ProcessGuidance.kt`;
- `StructureNarrator.kt`;
- assessment bank;
- bilingual scientific terminology.

The v3 label correction intentionally changes only native rendering in
`AtlasScreen.kt`; SVG scientific assets remain frozen.

## Post-1.3.9 — explicitly deferred

The audit proposes valuable enhancements, but they must not expand this release:

- learner-visible evidence/uncertainty badges;
- beginner / practical / scholar modes;
- adaptive misconception remediation;
- teacher mode and worksheet/export tools;
- expanded microscopy simulation;
- layered/3D spatial anatomy;
- atlas search and text-only structure list;
- fuller laboratory-safety module;
- per-module references/read-more panels;
- practical-exam mode and assessment UI;
- Spatial Anatomy Explorer / cross-system relation layer.

## 1.3.9 acceptance sequence

1. v3 bright-label renderer physical-device QA.
2. Correct only demonstrated residual label defects.
3. Full EN/TA visual regression across 8 systems.
4. Home/designer/privacy device QA.
5. TTS/process/offline/rotation/navigation regression.
6. Finalise and publish corrected privacy policy.
7. Replace obsolete hybrid production release workflow with native release engineering.
8. Stamp `versionName 1.3.9` / `versionCode 10309`.
9. Clean signed release build with R8/resource shrinking.
10. Verify release merged manifest.
11. Verify `debuggable=false`.
12. Verify no PreviewActivity/test-only/unintended exported components.
13. Verify no INTERNET permission / no WebView / no browser payload.
14. Verify 8 HQ WebPs + 16 SVGs + 55 structures + 55 hotspots.
15. Verify signing certificate SHA-256 and APK/AAB SHA-256.
16. Install signed release on a physical device and run the final critical acceptance subset.
17. Only then consider merge/release.

## Current release classification

**SCIENTIFIC CORE:** PASS  
**NATIVE ARCHITECTURE:** PASS  
**BILINGUAL DATA PARITY:** PASS  
**HOME ENHANCEMENT BUILD:** PASS  
**V3 BRIGHT-LABEL RENDERER BUILD:** PASS  
**SVG DEVICE VISUAL QA:** OPEN  
**PRIVACY POLICY PARITY:** OPEN  
**NATIVE SIGNED-RELEASE PIPELINE:** OPEN  
**FINAL SIGNED 1.3.9 / 10309:** NOT YET PRODUCED  
**PRODUCTION RELEASE:** BLOCKED UNTIL P0 ITEMS CLOSE
