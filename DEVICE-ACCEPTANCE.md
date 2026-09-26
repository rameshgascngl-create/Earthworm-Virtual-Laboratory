# Native v2 physical-device acceptance

Target branch: `native/v2.0.0-alpha1`  
Current release state: **DO NOT SUBMIT — physical-device QA required**

Emulator QA is useful pre-screening only. It does **not** replace this real-device acceptance pass.

Record for every device:
- manufacturer/model;
- Android version / API level;
- display resolution and density;
- navigation mode (gesture / 3-button where applicable);
- system font scale;
- installed TTS engine and selected offline Tamil/English voice;
- build commit SHA;
- installed APK SHA-256;
- tester/date.

## A. Install, identity and offline behaviour

- [ ] Fresh install succeeds.
- [ ] App Info shows package `in.ramesh.zoology.earthwormlab`.
- [ ] Version displays `2.0.0-alpha1` for this QA build.
- [ ] Airplane mode launch succeeds.
- [ ] No screen requires network content to display core teaching material.
- [ ] Android permission list does **not** include INTERNET, camera, microphone, location or storage permissions.
- [ ] Public Privacy Policy button launches the external browser only after a user tap.
- [ ] Returning from the browser restores the app normally.

## B. Android 16 edge-to-edge / system bars

Test at minimum on one Android 16 device.

- [ ] Home content begins below the status/display-cutout area.
- [ ] Anatomy screen is not obscured by status or navigation bars.
- [ ] Guided practical is not obscured by system bars.
- [ ] Assessment options remain reachable above the bottom navigation area.
- [ ] Microscopy content is not clipped by status/navigation bars.
- [ ] Privacy screen is not clipped.
- [ ] Gesture navigation and 3-button navigation both preserve usable bottom spacing where available.
- [ ] Rotation does not introduce stale insets or clipped controls.

## C. Phone/tablet layout matrix

The CI emulator captures 60 screenshots across comparable profiles. Physical-device QA must verify at least the available real-device equivalents.

### Small phone
- [ ] ~360 dp portrait, font 1.0×.
- [ ] ~390 dp portrait, font 1.3×.
- [ ] ~360 dp portrait, font 1.5×.

### Tablet
- [ ] Tablet portrait, font 1.3×.
- [ ] Tablet landscape, font 1.3×.

For every profile:
- [ ] no horizontal clipping;
- [ ] no Tamil vowel-sign detachment;
- [ ] wrapped buttons expand vertically instead of clipping text;
- [ ] every interactive control is comfortably tappable;
- [ ] long assessment options remain fully readable;
- [ ] microscopic diagrams remain inside their panels;
- [ ] system/structure buttons remain reachable by scrolling.

## D. Bilingual parity

### Home
- [ ] English/Tamil toggle changes headings, system names, summaries and action buttons.
- [ ] Entering a module preserves the selected language.

### Anatomy
- [ ] System title changes language.
- [ ] Structure-index buttons change language.
- [ ] Canvas selected label uses the same authoritative bilingual structure name.
- [ ] Detailed location/function/teaching text remains semantically equivalent in both languages.

### Guided practical
- [ ] All 56 actions show student-facing instructions; no raw internal IDs appear.
- [ ] Tool/method names are translated appropriately.
- [ ] Tamil and English instructions refer to the same structure and action.
- [ ] Previous/Next controls localise correctly.

### Assessment
- [ ] All 72 question stems and options render in both languages.
- [ ] Switching language does not change the correct answer.
- [ ] System metadata localises.
- [ ] Long Tamil options wrap rather than truncate.

### Microscopy
- [ ] All 9 lesson titles/descriptions localise.
- [ ] Native Canvas plate remains visible after toggling language.
- [ ] Tamil diagram description is available to accessibility services.

### Privacy
- [ ] In-app privacy text matches the public QA policy.
- [ ] Tamil privacy text conveys the same data-storage/offline-TTS meaning.

## E. Scientific anatomy hotspot audit

For every hotspot, confirm that the marker endpoint visually corresponds to the structure named in the authoritative dataset.

### External morphology
- [ ] prostomium;
- [ ] mouth;
- [ ] spermathecal pores;
- [ ] dorsal pores;
- [ ] female genital pore;
- [ ] clitellum;
- [ ] male genital pores;
- [ ] perichaetine setae;
- [ ] anus.
- [ ] Genital markings are visually distinct from male pores.
- [ ] Ventral clitellar setae are not falsely shown as universally absent.

### Digestive system
- [ ] pharynx;
- [ ] oesophagus;
- [ ] gizzard;
- [ ] post-gizzard/pre-intestinal teaching region;
- [ ] intestine;
- [ ] paired intestinal caeca;
- [ ] typhlosole.
- [ ] Typhlosole is shown as an intestinal infolding, not an independent tube.
- [ ] Gizzard is not presented as having one universal segment assignment for every *M. posthuma* specimen.
- [ ] Caecal origin is XXVII in the teaching text; anterior extent is treated as specimen-variable.

### Circulatory system
- [ ] dorsal vessel;
- [ ] supra-oesophageal vessel;
- [ ] lateral hearts / vascular arches;
- [ ] paired lateral-oesophageal vessels;
- [ ] ventral vessel;
- [ ] subneural vessel;
- [ ] segmental capillary networks.
- [ ] Traditional VII/IX/XII/XIII heart map is presented as a practical-teaching convention, not misattributed as a complete modern redescription.

### Cutaneous respiration
- [ ] mucus film;
- [ ] moist epidermis;
- [ ] subepidermal blood capillaries;
- [ ] cutaneous gas-exchange relationship.
- [ ] Gas exchange is taught as observation/physiology, not as a structure to probe.

### Excretory system
- [ ] pharyngeal nephridia;
- [ ] septal nephridia;
- [ ] integumentary nephridia;
- [ ] enlarged septal nephridium.
- [ ] The three nephridial categories remain visually distinct.
- [ ] Enlarged septal nephridium shows a nephrostomal funnel and coiled tubule rather than being conflated with body-wall units.

### Reproductive system
- [ ] four **pairs** of spermathecae;
- [ ] two pairs of testes;
- [ ] two pairs of seminal vesicles;
- [ ] paired ovaries;
- [ ] oviducts;
- [ ] paired prostate fields;
- [ ] paired vasa deferentia.
- [ ] Hotspot positions align with the corrected paired-organ drawing.

### Nervous system
- [ ] cerebral ganglia;
- [ ] circumpharyngeal connectives;
- [ ] subpharyngeal ganglion;
- [ ] ventral nerve cord;
- [ ] segmental ganglia.
- [ ] No vascular structure is mistaken for the ventral nerve cord.

### Transverse section
- [ ] cuticle;
- [ ] epidermis;
- [ ] circular muscle;
- [ ] longitudinal muscle;
- [ ] peritoneum;
- [ ] coelom;
- [ ] intestinal wall / typhlosole;
- [ ] dorsal vessel;
- [ ] ventral vessel;
- [ ] ventral nerve cord;
- [ ] subneural vessel;
- [ ] setae.
- [ ] Below-gut order is ventral vessel → ventral nerve cord → subneural vessel.
- [ ] Typhlosole enters the intestinal lumen as a fold.

## F. Nine microscopy/deep-dive plates

Individually review:
- [ ] gizzard cross-section;
- [ ] typhlosolar intestine;
- [ ] nephridium;
- [ ] spermatheca;
- [ ] skin/cutaneous gas exchange;
- [ ] contractile heart-vessel;
- [ ] ganglion/paired nerve cord;
- [ ] gonadal region;
- [ ] body-wall layers.

For each plate:
- [ ] scientific relationship correct;
- [ ] English/Tamil description agrees with plate;
- [ ] labels readable at 1.0× and 1.5× font;
- [ ] no clipping in portrait;
- [ ] no clipping in tablet landscape;
- [ ] TalkBack description is meaningful.

## G. Guided practical

- [ ] All 56 actions can be traversed.
- [ ] No action displays an internal ID such as `lateral-oesophageal-vessels`.
- [ ] Fine scissors / forceps / blunt probe / magnifier / inspection actions are appropriate to the target.
- [ ] Cross-section identification uses magnification/inspection, not destructive probing.
- [ ] Cutaneous gas exchange uses observation, not probing.
- [ ] Rotation preserves current step.
- [ ] Language change preserves current step.

## H. Assessment

- [ ] Complete at least one full 72-question pass.
- [ ] Score increments only on correct answers.
- [ ] Switching language does not alter answer indexing.
- [ ] Rotate mid-assessment; question index and score remain intact.
- [ ] Background/foreground mid-assessment does not reset state.
- [ ] Final score is stored locally and appears on Home.
- [ ] App never describes the assessment as an accredited certificate.

## I. Accessibility

- [ ] TalkBack can reach Home controls in logical order.
- [ ] TalkBack can reach all system buttons.
- [ ] Canvas itself announces that screen-reader users can use the accessible structure list.
- [ ] Every structure is selectable through the accessible button list even if Canvas hotspots are not individually exposed as virtual accessibility nodes.
- [ ] Guided Previous/Next are announced correctly.
- [ ] Assessment option buttons are announced with complete text.
- [ ] Microscopy images have meaningful content descriptions.
- [ ] Focus remains visible and does not jump unexpectedly after rotation.

## J. Offline TTS

Test in airplane mode.

### English
- [ ] Installed offline English voice narrates selected structure.
- [ ] If no offline voice exists, app shows the clear unavailable-voice notice and does not start network speech.

### Tamil
- [ ] Installed offline Tamil voice narrates selected structure.
- [ ] Pronunciation review includes: கிளைட்டெல்லம், அரவைப்பை, நெஃப்ரிடியம்/நெஃப்ரிடியா, டைஃப்ளோசோல், கண்ட எண்கள் and *Metaphire posthuma*.
- [ ] If no offline Tamil voice exists, app shows the unavailable-voice notice.

## K. Lifecycle / navigation / stress

- [ ] Rotate Home.
- [ ] Rotate Anatomy; selected system remains.
- [ ] Rotate Guided; current step and language remain.
- [ ] Rotate Assessment; current index, score and language remain.
- [ ] Rotate Microscopy; language remains.
- [ ] Rotate Privacy; language remains.
- [ ] Android Back returns naturally through the activity stack.
- [ ] Predictive Back on Android 16 behaves normally.
- [ ] Background/foreground ×10.
- [ ] Rapid system changes ×50 without freeze or stale selection.
- [ ] Low-memory/background process recreation does not corrupt SharedPreferences.
- [ ] Relaunch restores last anatomy system, visited count and latest assessment score.

## L. Privacy truthfulness

- [ ] App requests no INTERNET permission.
- [ ] Stored local state matches the policy: last system, visited structure IDs, latest score.
- [ ] Guided-step position and language are not falsely claimed as long-term stored learning records.
- [ ] Narration is attempted only with a voice Android reports as not network-required.
- [ ] Public Privacy Policy opened by this alpha matches the alpha's actual behaviour.

## M. Evidence required for PASS

For each tested physical device retain:
- [ ] Home English screenshot.
- [ ] Home Tamil screenshot.
- [ ] Digestive English/Tamil screenshots.
- [ ] Circulatory screenshot.
- [ ] Excretory screenshot.
- [ ] Reproductive screenshot.
- [ ] Transverse-section screenshot.
- [ ] Guided English/Tamil screenshot.
- [ ] Assessment English/Tamil screenshot.
- [ ] Microscopy English/Tamil screenshot.
- [ ] Privacy English/Tamil screenshot.
- [ ] 1.5× font screenshot.
- [ ] landscape/tablet screenshot where available.
- [ ] App Info permissions/version screenshot.
- [ ] recorded APK SHA-256.
- [ ] PASS/FAIL notes for every failed hotspot or label.

## Promotion rule

Do **not** create `2.0.0-rc1`, switch PR #8 out of draft, sign a production candidate, or merge to `main` until:

1. native CI is green;
2. API-36 emulator instrumentation is green;
3. emulator phone/tablet/Tamil layout matrix is green;
4. physical-device QA above is completed;
5. every scientific plate defect found on-device is corrected and re-tested;
6. the final stable public Privacy Policy URL is published and the alpha branch URL is removed.
