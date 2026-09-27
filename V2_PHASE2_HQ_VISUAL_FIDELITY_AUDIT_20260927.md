# Earthworm Native Phase 2 — HQ Visual-Fidelity Audit

Date: 2026-09-27

## Gate result

**VISUAL-FIDELITY GATE: FAIL — correction required before Phase 3 physiology/audio work.**

Authoritative native baseline:

- branch: `native/v2-phase2-visual-fidelity`
- HEAD: `12ee24e0cbc5b67ed66d8fcb3e2bffa733fd308c`
- debug APK SHA-256: `162fea216e3e12b172512a4b701197a64da27f49a6bc112056ce7f4646324eee`
- debug APK size: 11,851,266 bytes

Audit branch:

- `audit/v2-phase2-hq-visual-fidelity-20260927`
- no modification to `main`
- no release APK authorised by this audit

The later Phase-3A debug artifact was used only as device-QA evidence. Its 16 SVG resources are byte-identical to Phase 2.1, so the darker appearance in the supplied screenshots is a **presentation regression**, not a new SVG asset. Phase 3A's selection-focus implementation dims non-selected `data-structure` groups to 34% opacity and overlays large visible markers. That change must not be carried forward.

## 1. Provenance reconciliation

The earlier accepted HQ wrapped atlas survives in:

- branch: `archive/hq-candidate-5dbc6fe-20260926`
- file: `app/src/main/assets/index.html`
- Git blob: `ff79a4fc360175cc64112b9727c69fcb5a8774f5`
- contains exactly 8 embedded `data:image/webp;base64` anatomical reference plates.

The current Phase-2.1 APK contains:

- 16 bilingual SVG atlas files (8 EN + 8 TA)
- **0 HQ anatomical WebP files**
- **0 anatomical PNG files**
- launcher WebP only

Therefore the detailed raster anatomical layer was not migrated into the current Phase-2.1 APK.

The attached older audit describes a different 1.3.8 device-QA candidate (17,600,945 bytes, SHA-256 `76acdb...`) that did contain eight 2048×1536 WebP atlas images. It is not the same artifact as the current 11,851,266-byte Phase-2.1 APK.

## 2. What changed during native migration

### 2.1 HQ WebP layer

**Status: omitted/substituted.**

The wrapped-HQ application contained eight high-detail 2048×1536 WebP plates:

1. external morphology
2. digestive system
3. circulatory system
4. cutaneous respiration
5. excretory system
6. reproductive system
7. nervous system
8. transverse section

The native Phase-2 APK does not package these images. The interactive schematic SVG became the sole anatomical image surface.

### 2.2 SVG layer

**Status: not anatomically redrawn during Phase 2.**

The native 16 SVGs were compared against `review/legacy-extractions/svg`.

For every EN/TA system file:

- node order: unchanged
- text/tspan content: unchanged
- non-style attributes: unchanged
- viewBox: unchanged at 1200×560
- only label-style properties differ

Thus the source anatomy was not redrawn during the native migration; the visual downgrade comes mainly from **using the older schematic SVG as the primary full-screen atlas while omitting the HQ raster plate**.

### 2.3 External morphology ventral plate

**Status: omitted — release-blocking functional visual defect.**

The legacy source contains separate:

- `en-external.svg` / `ta-external.svg`
- `en-external-ventral.svg` / `ta-external-ventral.svg`

The current native app packages only the first pair.

The visible native external SVG has five structure groups:

- prostomium
- clitellum
- setae
- anus
- dorsal-pores

but the preserved structure model still has nine external structures. The four ventral-only structures are:

- mouth
- spermathecal-pores
- female-pore
- male-pores

They retain native hotspot markers even though their corresponding ventral plate is absent. The supplied external screenshot therefore shows markers that cannot be visually reconciled with the displayed dorsal-only plate.

This is **P0 / scientific-interaction failure**, not a cosmetic issue.

## 3. Portrait readability and scaling

Each SVG uses a 1200×560 (2.143:1) canvas.

On the supplied 691-pixel-wide phone screenshot the actual plate is approximately 660–665 px wide, so the source atlas is displayed at roughly 0.55× its coordinate width.

The SVG labels use 16 px and 20 px source font sizes. At the observed portrait scale these become approximately:

- 16 px source label → ~8.8 device pixels
- 20 px source label → ~11 device pixels

This explains why labels look thin/small even when the source vector itself remains sharp.

The accepted HQ images are 2048×1536 (4:3). At the same phone width they can occupy approximately 495–500 px of vertical space instead of ~309 px, giving anatomy and baked reference labels substantially more readable screen area.

**Conclusion:** this is primarily a layout/presentation problem, not merely SVG anti-aliasing.

## 4. System-by-system visual assessment

| System | Current native SVG | Earlier HQ WebP | Audit finding | Gate |
|---|---|---|---|---|
| External morphology | Flat dorsal schematic; ventral plate absent | High-detail dorsal + ventral morphology | HQ is substantially superior; restore HQ image and ventral SVG | **FAIL / P0** |
| Digestive | Correct schematic concept, but flattened, small labels | High-detail alimentary anatomy with opened typhlosole region | Keep SVG as interactive atlas; restore HQ as detailed reference | **FAIL / P1** |
| Circulatory | Simplified vessels/hearts; dense small labels | High-detail dorsal/ventral/supra-oesophageal vessels, heart pairs and capillary beds | HQ substantially improves spatial relationships | **FAIL / P1** |
| Cutaneous respiration | Clean schematic but simple block model | High-detail epidermis/mucus/capillary/gas-exchange teaching plate | Both are useful; SVG for interaction, HQ for detailed physiology/anatomy | **FAIL / P1** |
| Excretory | Highly schematic; enlarged unit is visually weak | HQ distinguishes pharyngeal, septal, integumentary nephridia + enlarged septal unit | HQ is much stronger; native hotspot for `nephridium` also needs recalibration | **FAIL / P0** |
| Reproductive | Simplified paired organs; labels crowded at portrait scale | High-detail organ relationships and segment context | HQ substantially superior; retain caveat/teaching-convention text | **FAIL / P1** |
| Nervous | Usable schematic, but topology is visually simplified | HQ clearly shows cerebral/subpharyngeal ganglia and ventral cord continuity | Use both modes | **FAIL / P1** |
| Transverse section | Useful vector schematic but relatively flat | HQ has much stronger tissue-layer and spatial differentiation | HQ detailed mode strongly preferred for study | **FAIL / P1** |

## 5. Hotspot audit

All 55 structure records and their current native point anchors must be preserved as historical data, but **preserved does not mean proven accurate for every migrated visual**.

Objective checks against authored SVG hit regions exposed drift:

- digestive pharynx: aligned
- digestive oesophagus: small drift
- digestive gizzard: moderate drift
- digestive stomach: moderate drift
- digestive intestine: small drift
- digestive intestinal caeca: significant vertical drift
- digestive typhlosole: aligned
- circulatory capillary networks: small drift
- respiratory cutaneous capillaries: moderate drift
- respiratory cutaneous exchange: **major drift**
- nervous structures: several moderate 5–8% canvas offsets
- excretory enlarged `nephridium`: **major visual mismatch**; point anchor lies far from the inset unit
- external mouth/spermathecal/female/male pore markers: **no corresponding group in the displayed dorsal plate**

Therefore the next visual layer must not copy the existing normalized point set blindly onto the HQ WebPs.

### Required hotspot policy

- retain existing 55 IDs and legacy coordinates for provenance
- preserve current SVG interaction until each point is recalibrated
- add a separate `DetailedImageGeometry` map for HQ WebP coordinates
- never assume SVG-normalized coordinates equal WebP coordinates
- maintain an invisible >=48 dp touch target
- keep the visible marker small (approximately 8–12 dp)
- selection must highlight without dimming the entire remaining atlas

## 6. Screenshot-specific Phase-3A regression

The supplied device screenshots show:

- anatomy outside the selected structure darkened
- oversized white rings obscuring organs/labels
- labels losing contrast
- reproductive/digestive/circulatory plates appearing lower quality than Phase 2

The SVG files themselves are byte-identical between the Phase-2.1 and Phase-3A APKs.

The regression is caused by UI rendering logic:

- non-selected SVG structure groups were forced to opacity `0.34`
- the complete atlas + markers were transformed through a Compose graphics layer
- visible marker rings were too large for dense anatomy

**Correct rule:** selection may accent the chosen structure but must never reduce the base anatomical plate below its original contrast.

## 7. English–Tamil assessment

Static content parity remains strong and must not be rewritten during this visual correction.

The 16 SVG resources are paired EN/TA and the scientific JSON preserves the bilingual model. However, portrait rendering remains a QA risk because the Tamil strings are longer while the same 1200×560 canvas and label coordinates are used.

Required device tests:

- every system in EN and TA
- labels ON/OFF
- 1×, 2× and 4× zoom
- long Tamil labels near reproductive, circulatory and excretory structures
- Tamil glyph shaping and line height
- no label clipping at canvas edges
- switching EN→TA→EN while zoomed and while a structure is selected

## 8. Zoom, pan, reset and rotation

The Phase-3A interaction work is useful but not accepted yet.

Required behavior:

- default 1× rendering must preserve original asset fidelity
- pinch zoom up to 4×
- pan only when zoomed
- touch hotspots must transform in the same coordinate system as the selected visual
- `Reset View` returns scale and pan to exact baseline
- rotation must not reuse stale raw-pixel pan offsets
- page vertical scrolling must not fight atlas pan gestures
- selected structure remains selected after reset
- language switching must reload the correct EN/TA asset without changing anatomical viewport unexpectedly

No supplied screenshot proves landscape, rotation, Tamil, or reset behavior for all eight systems, so those remain **DEVICE-QA PENDING** rather than PASS.

## 9. Recovered authoritative assets

The accepted HQ WebPs have been recovered from the authoritative wrapped-HQ HTML and bundled on this audit branch only; they are **not wired into production UI yet**.

| Resource | Git blob | Bytes |
|---|---|---:|
| `hq_external.webp` | `3ae4139831f4ebce1556d33641d219c7c79abf6a` | 705,080 |
| `hq_digestive.webp` | `684a8c96f78ea2065513c0932614217d8bcd2dd0` | 564,018 |
| `hq_circulatory.webp` | `557aad4c9879d0c5bb341f0c3ec7e3920c31f3fc` | 791,804 |
| `hq_respiratory.webp` | `60e94eb22a1e8760fdf5eb6f1a5a436ff8fce4ba` | 737,444 |
| `hq_excretory.webp` | `0bf2a899ea6c330f6524991652b0b718408abe6a` | 814,344 |
| `hq_reproductive.webp` | `a12c46281259ede13f7e0b0c51fa4fe7b33a1007` | 850,040 |
| `hq_nervous.webp` | `33826b087a4635341b64f194606dd42bcd5e5194` | 416,620 |
| `hq_crosssection.webp` | `9ff3089e0d998cbcf5975d3c6ae7715b2234b2de` | 723,848 |

Recovery commit:

- `b3bb146ab18952b715d1977cd0d2dc2f9c29adce`

Recovered ventral external SVGs:

- EN commit: `5e28db8554eb048f6ab0187bbf81f12076a07bb1`
- TA commit: `40034305e990d32683f34349de3befad65fc015e`

## 10. Correct visual architecture

The native app should expose two complementary modes.

### Atlas SVG

Purpose:

- interactive identification
- EN/TA vector labels
- hide/show labels
- hotspot selection
- assessment/exam mode
- structure highlighting
- scalable line work

Rules:

- restore 100% base opacity
- add dorsal/ventral external subview
- calibrate hotspot geometry
- keep visible hotspot rings small
- preserve 55 structure IDs and scientific content

### Detailed Image

Purpose:

- visually rich anatomical recognition
- practical-study reference
- gross spatial relationships
- high-quality tissue/system presentation

Rules:

- use only the recovered accepted HQ WebPs
- do not regenerate anatomy
- package locally; no network dependency
- preserve the source captions/cautions from the wrapped-HQ version
- keep detailed-image hotspot geometry separate from SVG geometry
- if a raster label reflects a teaching convention or variable segment account, the interactive scientific card remains authoritative and the caveat must be visible

## 11. Implementation sequence after this audit

1. Freeze the Phase-2.1 SVGs and scientific JSON.
2. Keep `main` untouched.
3. Create visual-correction work only from this audit branch.
4. Restore 100% atlas opacity and reduce visible marker size.
5. Add External Dorsal / Ventral subview and bind the four ventral-only structures correctly.
6. Add Atlas SVG / Detailed Image selector.
7. Wire the eight recovered HQ WebPs locally.
8. Add a separate detailed-image hotspot map; calibrate system-by-system.
9. Preserve structure selection and detail cards across view-mode switching.
10. Re-run EN/TA label and hotspot audit for all 55 structures.
11. Test portrait + landscape, 1×/2×/4×, pan, reset, rotation, font scale and language switching.
12. Only after this visual layer passes physical-device QA may audio, guided anatomy and physiological animation continue.

## Acceptance gate

The visual layer is accepted only when:

- all 8 HQ WebPs are present and selectable
- all 16 EN/TA SVGs remain available
- external dorsal + ventral anatomy is complete
- 55/55 structures remain in the scientific model
- no structure marker is knowingly mapped to the wrong visual location
- no selection effect darkens unrelated anatomy
- labels are legible at 1× or the UI clearly directs the learner to zoom
- Reset View is deterministic
- English/Tamil switching is stable
- portrait and landscape pass
- no WebView/HTML runtime is reintroduced

Until these conditions pass, **Phase 3 physiology/audio is blocked**.
