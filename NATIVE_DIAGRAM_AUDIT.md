# Native diagram audit — v2.0.0-alpha1

## Status vocabulary
- IMPLEMENTED: native Android drawing exists and builds.
- ACADEMIC QA OPEN: anatomical relationships/labels still require explicit faculty review.
- DEVICE QA OPEN: small-screen readability, touch targets, rotation and density behavior remain unverified.
- ACCEPTED: both academic and device QA passed.

## Whole-organism/system plates

| Plate | Native renderer | Academic status | Device status |
|---|---|---|---|
| Preparation | Android Canvas schematic | ACADEMIC QA OPEN | DEVICE QA OPEN |
| External morphology | segmented native body map with hotspot overlay | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Digestive system | native opened-body + alimentary-canal schematic | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Circulatory system | longitudinal vessels, vascular arches and segmental network schematic | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Cutaneous respiration | layered integument/capillary/exchange schematic | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Excretory system | separate pharyngeal, septal and integumentary visual regions | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Reproductive system | gonads, storage sacs, glands and ducts schematic | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Nervous system | cerebral ganglion/connectives/ventral cord/segmental ganglia schematic | ACADEMIC QA OPEN | DEVICE QA OPEN |
| Transverse section | concentric body-wall/coelom/gut plus dorsal/ventral vascular-neural landmarks | ACADEMIC QA OPEN | DEVICE QA OPEN |

All 55 migrated structure IDs are represented by native visual hotspot geometry. Scientific prose is not duplicated in drawing code; each hotspot resolves to the authoritative bilingual native dataset.

## Microscopic/deep-dive plates

| Lesson | Native Canvas implementation | Status |
|---|---|---|
| Gizzard cross-section | yes | ACADEMIC QA OPEN |
| Typhlosolar intestine | yes | ACADEMIC QA OPEN |
| Nephridial tubule | yes | ACADEMIC QA OPEN |
| Spermatheca | yes | ACADEMIC QA OPEN |
| Skin/cutaneous exchange | yes | ACADEMIC QA OPEN |
| Contractile heart-vessel wall | yes | ACADEMIC QA OPEN |
| Ganglion and paired nerve cord | yes | ACADEMIC QA OPEN |
| Gonadal region | yes | ACADEMIC QA OPEN |
| Body-wall layers | yes | ACADEMIC QA OPEN |

## Explicit non-claims

The current Canvas plates are educational schematics, not yet production-certified anatomical illustrations. They must not be described as visually equivalent to the reviewed v1.3.8 SVGs until side-by-side faculty review is completed.

The browser SVGs are not packaged or rendered by the v2 native runtime.

## Required acceptance pass

For every plate:
1. verify organism/specimen convention and segment numbering;
2. verify relative organ positions and leader/hotspot endpoints;
3. verify English/Tamil terminology against the authoritative dataset;
4. check selected-state visibility and contrast;
5. check 360 dp, 390 dp, tablet portrait and tablet landscape;
6. check font scale 1.0, 1.3 and 1.5;
7. check touch target usability;
8. check TalkBack description;
9. verify no clipping under Android 16 edge-to-edge behavior;
10. record PASS/FAIL with screenshot evidence before production release.
