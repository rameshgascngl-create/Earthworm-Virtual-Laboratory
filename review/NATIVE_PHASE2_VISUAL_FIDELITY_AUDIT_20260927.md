# Earthworm Native Phase 2 — visual-fidelity audit

Date: 2026-09-27  
Scope: anatomical visual layer only. No physiology animation, no production release, no main-branch modification.

## Provenance

- Authoritative native Phase-2 baseline: `native/v2-phase2-visual-fidelity` at `12ee24e0cbc5b67ed66d8fcb3e2bffa733fd308c`.
- Audit branch: `audit/native-v2-phase2-visual-fidelity-20260927`.
- The Phase-1 migration removed the hybrid `app/src/main/assets/index.html` payload from the native branch. The historical wrapped/HQ payload remains preserved on archive branches.
- The accepted wrapped/HQ source used for recovery is `archive/hq-candidate-5dbc6fe-20260926`, `app/src/main/assets/index.html`, Git blob `ff79a4fc360175cc64112b9727c69fcb5a8774f5`.
- That HTML contains exactly eight embedded `HQ_ATLAS` WebP plates. They have been recovered byte-for-byte into this audit branch under `app/src/main/res/drawable-nodpi/`. They are audit assets only at this checkpoint; the app UI does not yet reference them.
- Every recovered HQ plate is 2048×1536 (4:3). The native Phase-2 atlas SVGs are 1200×560 (15:7).

## Root-cause finding

The quality change is **not an aggressive recompression of the accepted HQ plates**. The eight HQ WebP plates were omitted from the native migration and the app presents the standalone interactive SVG diagrams instead. The native SVGs are pedagogically useful but are deliberately flatter, more schematic and much wider in aspect ratio.

The wrapped application originally kept both layers: the interactive diagram and a separate “High-resolution anatomical atlas” panel with a full-resolution 2048×1536 WebP reference plate. Native Phase 2 retained the interaction layer but lost the high-resolution reference layer.

A later Phase-3A experiment also introduced a second device-visible regression by dimming non-selected SVG structure groups and drawing oversized visible hotspot rings. That experiment is not an acceptable visual baseline and must not be promoted.

## Recovered HQ asset identity

| System | Recovered HQ WebP SHA-256 | Dimensions |
|---|---|---:|
| External morphology | `d439c1953e1e69acab0e26d2f4ca508a9260904b9fc7e9850fe3172847ed5026` | 2048×1536 |
| Digestive | `52ec7092e82b82ec14db508e6c8573ea5b0feb8989db060d4537e66691365506` | 2048×1536 |
| Circulatory | `76277234651906cf9f2aa5d3e0c7af86a450f845f968b02297c93a61d0004bb0` | 2048×1536 |
| Cutaneous respiration | `88e2ad2b73b3210ec4d3d877a55c04673a7de22e49643b843175e2f659da62fb` | 2048×1536 |
| Excretory | `8c56a27dd2262750a2fb5fc4923c4c4ba1a25825bb3ec5e308f37ae5ccec984e` | 2048×1536 |
| Reproductive | `23ef28d2e073a46afe01b026051b5da5d7e8d5ddaa08e6e344151f207a2ef212` | 2048×1536 |
| Nervous | `74bc0bb1272b6a0b444c08d475e633f66b4d49471402e80ed8944f9232d8c242` | 2048×1536 |
| Transverse section | `590cdb9f9d1b227ec8f4b990aa87ec6ea037f33bd5c0d7051a1cface293826ac` | 2048×1536 |

## System-by-system visual audit

| System | Native SVG structure coverage | Fidelity finding | Required handling |
|---|---:|---|---|
| External morphology | 5 SVG structure groups / 9 preserved data structures | **Major loss.** HQ plate shows separate dorsal and ventral surfaces, pore positions, setae and both terminal openings. Native SVG shows only one flattened schematic body. Four preserved external structures exist only through native hotspot/data records, not matching SVG structure groups. | Keep SVG as interactive atlas; restore HQ WebP as Detailed Image. HQ mode is especially important here. |
| Digestive | 7/7 | **Major visual simplification.** Organ sequence is retained, but the HQ plate has substantially better organ form, depth, dissection context and an opened intestinal wall/typhlosole view. Native labels become very small on portrait because the 15:7 plate is fitted to phone width. | Dual mode. Keep scientific caveat on source-image segment conventions. |
| Circulatory | 7/7 | **Major visual simplification.** Vessel topology is recognizable in SVG, but dimensional hearts, longitudinal vessels and capillary beds are much clearer in HQ. | Dual mode. Preserve evidence-aware heart-position text/cautions. |
| Cutaneous respiration | 4/4 | **Moderate-to-major detail loss.** SVG preserves the teaching concept, but HQ tissue layers, mucus surface, capillaries and gas-direction cues are far more legible. | Dual mode; HQ may be the preferred teaching view. |
| Excretory | 4/4 | **Major loss.** HQ clearly separates pharyngeal, septal and integumentary nephridia and provides a large septal-nephridium unit. Native SVG compresses these into a much smaller schematic/inset. | Dual mode mandatory. Do not conflate nephridial categories. |
| Reproductive | 7/7 | **Major loss.** HQ provides large paired organs, segment context and clear duct relationships. Native SVG is substantially flatter and labels are too small at phone scale. | Dual mode mandatory. |
| Nervous | 5/5 | **Moderate loss.** Native SVG retains topology reasonably well, but HQ improves ganglion/cord recognition and anatomical context. | Dual mode. |
| Transverse section | 12/12 | **Major loss of tissue detail.** Native SVG is a clean schematic; HQ is far better for body-wall layers, coelom, gut/typhlosole, vessels, nerve cord, subneural vessel and setae. | Keep both: SVG for simplified atlas, HQ for detailed teaching. |

## Device screenshot findings

The supplied portrait screenshots are treated as the latest device evidence.

1. The visible atlas is too shallow vertically for anatomical teaching. This follows directly from fitting a 1200×560 (15:7) diagram to a portrait phone width.
2. Labels inside the SVG are present but too small to function as the primary teaching labels at 1× portrait scale.
3. The large white circular markers obscure anatomy and printed labels. The touch target and visible indicator must be decoupled: retain a minimum 48 dp touch target but use a much smaller visible marker.
4. In the Phase-3A screenshot build, non-selected anatomy is visibly darkened. This is caused by selection-driven opacity modulation and is rejected.
5. The screenshots do not establish landscape acceptance for all eight systems. Landscape remains a required device gate rather than an assumed pass.

## Cropping and proportion

The native screenshots do not primarily show a crop of the HQ plate. They show a **different visual asset** with a different composition and aspect ratio. The original HQ 4:3 plates contain information that is not present in the 15:7 standalone SVGs (for example, the ventral external surface and the large excretory inset).

For Detailed Image mode the 2048×1536 plate must be rendered with aspect-preserving FIT behavior. No center-crop is permitted.

## Hotspots

The authoritative 55 structure records and existing `StructureGeometry` coordinates must remain unchanged.

Those coordinates were authored for the native/interactive atlas geometry and must not simply be re-used on the 4:3 HQ plates. Detailed Image mode requires its own calibrated overlay geometry while retaining the original hotspot map for Atlas SVG mode. This is an additive mapping, not a replacement of the existing 55 hotspots.

Visible markers must be small; accessibility-sized hit regions may remain invisible around them.

## Zoom / pan / Reset View

The interaction contract to carry forward is:

- pinch zoom and pan;
- image and hotspot overlay transformed together;
- `Reset View` returns exactly to scale 1.0 and zero translation;
- at the baseline 1× view, do not force the SVG through a Compose graphics layer solely to support future transforms;
- no selection effect may dim or blur unselected anatomy;
- each visual mode keeps its own intrinsic aspect ratio.

## English / Tamil

The native SVG atlas correctly has separate English and Tamil SVG resources.

The accepted HQ layer historically used the **same English-labelled raster plate** in both language modes while localizing the surrounding title, alt text and explanatory caption. That behavior must be preserved rather than generating new Tamil raster artwork. Tamil structure names, detail cards, accessibility descriptions and interactive overlays remain native and bilingual.

## Visual-layer acceptance gate before physiology work

The next visual-integration candidate must satisfy all of the following before touch-audio, guided anatomy or physiological animations begin:

- all eight recovered HQ WebPs are bundled locally and hash-verified;
- `Atlas SVG` and `Detailed Image` modes are available where applicable;
- no WebView/network dependency is reintroduced;
- no anatomical SVG or recovered HQ WebP is regenerated or recompressed;
- 55 scientific structure records remain unchanged;
- existing SVG hotspot geometry remains unchanged;
- detailed-image hotspots use an additive, separately audited mapping;
- markers do not obscure anatomy;
- 1× SVG rendering retains Phase-2 fidelity with no opacity/dimming effect;
- 4:3 HQ plates use FIT, never crop;
- zoom/pan/reset are verified in both modes;
- portrait and landscape are checked on physical hardware for all eight systems;
- English/Tamil switching is checked in both modes;
- only after this gate passes may Phase 3 continue to audio, guided learning and physiology animation.

## Audit conclusion

**Visual-fidelity audit: FAIL for the current single-layer native presentation.**

The native migration preserved the scientific dataset and an interactive SVG atlas, but it omitted the accepted high-resolution reference layer. The correct repair is not to redraw the anatomy; it is to restore the exact recovered HQ WebPs as a second native visual mode while preserving the SVG atlas and all scientific/interaction data.
