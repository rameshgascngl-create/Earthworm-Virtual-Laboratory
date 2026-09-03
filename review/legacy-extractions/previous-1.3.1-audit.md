# Earthworm simulator audit and production handover

**Date:** 3 September 2026  
**Input:** Earthworm_Dissection_Simulator_Advanced_REALISTIC.html, version 1.2.2, 300,988 bytes  
**Output:** revised version 1.3.1-beta, 520,919 bytes  
**Verdict:** a revised teaching beta and an Android source candidate; neither a production-certified application nor a built APK.

The source, content data, SVG structure, state handling, controls, references and Android packaging requirements were reviewed. The revised application passed **1,419 data and DOM interaction checks** and **232 Tamil/narration contract checks** with simulated speech events. Live browser layout, physical touch, audio output, printing, Android compilation, Android lint and device testing remain pending. The session's browser URL policy blocked local preview; the Android SDK/Gradle build environment was unavailable and the Gradle download did not complete. These limitations are not passed tests.

## Tamil typography and narration revision — 1.3.1-beta

தமிழ் எழுத்துரு, வரி இடைவெளி, வரைபடக் குறிப்புகள், சொற்களின் ஒருமைப்பாடு, குரல் விளக்கங்களின் வாக்கிய அமைப்பு ஆகியவை திருத்தப்பட்டுள்ளன. ஒவ்வொரு வாக்கியமும் முடிந்த பின்னரே அடுத்த வாக்கியம் தொடங்கும். செய்முறை விளக்கமும் குரல் முடியும் வரை காத்திருக்கும்.

| Audit finding | Correction |
|---|---|
| No Tamil font was bundled; display depended on fonts available on each device. | Embedded Noto Sans Tamil WOFF2 subsets, weights 400/600/700, within the offline HTML. License and provenance accompany the source. |
| Tight English-oriented line heights, letter spacing and small mobile controls affected Tamil. | Added Tamil-specific paragraph/control spacing, larger supporting text, normal letter spacing, wrapped labels and two-column mobile header/procedure controls. |
| Tamil translations could overrun fixed SVG text areas. | Added 127 translation-layout entries with whole-word lines; preserved character shaping and avoided horizontal text squeezing. Repositioned two overlapping labels and one clipped microscopic label found during proof inspection. |
| Inconsistent or awkward terms appeared across the interface and educational content. | Standardized key terminology and corrected case endings; examples appear below. |
| The same written fragments were passed directly to speech. | Added reviewed spoken introductions for all 55 structures and rewrote all seven Tamil procedure scripts with shorter, connected sentences. |
| Roman segment numbers and gas formulae could be spoken as letters. | Kept scientific notation on the diagram; expanded it only for narration. For example, XIV–XVI becomes “பதினான்காம் முதல் பதினாறாம்”; O₂ and CO₂ become their Tamil names. |
| The procedure advanced every 5.2 seconds regardless of speech duration. | Advance now waits for the complete narration and a minimum visual dwell time. Cancellation or an engine error stops automatic playback. |
| One long utterance, delayed voice loading and stale callbacks caused fragile playback. | Queued sentences with short pauses, retained active utterance objects, waited for browser voice discovery, handled completion/errors and invalidated cancelled callbacks. |
| Voice choice was opaque and native quality selection was arbitrary. | Added an installed-voice selector and a Tamil sample button. Android prefers an exact locale and higher reported quality, then lower latency; an explicit available user choice takes precedence. Network-required/uninstalled voices are excluded. |

| Earlier wording | Revised wording |
|---|---|
| தெளிவுமாறு | நிற வேறுபாடு |
| பிரித்தறிதல் | உடற்கூறாய்வு |
| நுண்ணமைப்பு ஆழ்நோக்கு | நுண்ணமைப்பு விளக்கம் |
| பொதுவான திருத்தம் | தவிர்க்க வேண்டிய குழப்பம் |
| அரைவைப்பை | அரவைப்பை |
| உணவுக்குழல் / உணவுக்குழாய் | உணவுக்குழாய் for the oesophagus |
| விரைகள் / விந்தகங்கள் | விந்தகங்கள் |
| குடல் சீக்கா / குடல் பிதுக்கங்கள் / குடல் நீட்சிகள் | குடல் நீட்சிகள் |
| கீழ்ப்புற நாளம் / நரம்புவடம் | வயிற்றுப்புற நாளம் / நரம்புவடம் |
| நீள்த் தசை | நீளவாட்டுத் தசை |

The existing speed setting is retained; available rates remain 0.8×, 0.9× and 1×. Tap **தமிழ் → குரலைச் சோதிக்க** and compare installed voices at the preferred speed. The app uses device speech synthesis. It does not contain human-recorded Tamil narration, and this session did not listen to a real device voice. Pronunciation and audible naturalness therefore remain device acceptance items, especially technical names. Native Java source was updated for utterance completion events but has not been compiled or run here.

The test archive includes `tests/tamil-voice.cjs`, its 232-check result, a 71-entry editorial change log, Tamil plate contact sheets and the standalone typography result. The tests cover all structure introductions, notation expansion, sentence sequencing, cancellation, late voice discovery, missing voices, long procedure narration, engine errors and browser/native event contracts. The final copy edits retain all counts, question answer indices and scientific convention notes.

Technical sources consulted: [Noto font usage and bundling](https://notofonts.github.io/noto-docs/website/use/), [Noto Tamil upstream and license](https://github.com/notofonts/tamil), [Android voice quality, locale and latency](https://developer.android.com/reference/android/speech/tts/Voice), [Android utterance completion](https://developer.android.com/reference/android/speech/tts/UtteranceProgressListener), [Android uninstalled voice feature](https://developer.android.com/reference/android/speech/tts/TextToSpeech.Engine#KEY_FEATURE_NOT_INSTALLED), [browser voice discovery](https://developer.mozilla.org/en-US/docs/Web/API/SpeechSynthesis/voiceschanged_event), [browser utterance completion](https://developer.mozilla.org/en-US/docs/Web/API/SpeechSynthesisUtterance/end_event).

## Content retained

| Component | Revised inventory |
|---|---:|
| Systems, including setup and transverse section | 9 |
| Anatomical structure records, each with English and Tamil fields | 55 |
| Guided actions | 56 |
| Assessment questions | 72 |
| Microscopic explanation panels | 9 |
| Animated procedure stages | 7 |

The bilingual structure descriptions, guided activities, review system and assessments remain present. Two question stems were revised to avoid teaching disputed details as universal facts. The simulator remains one self-contained HTML file; reference links are optional and online.

## Principal findings and corrections

| Finding in the supplied file | Change made |
|---|---|
| Four heart paths represented a label claiming four pairs. | Redrew eight contractile paths in four paired groups; labelled the conventional VII, IX, XII and XIII arrangement and distinguished a connection diagram from a depth view. |
| Four spermathecal shapes represented four pairs; testes and ovaries were also incomplete as bilateral displays. | Redrew eight spermathecae, four testes, paired seminal vesicles and paired ovaries; indicated exaggerated spacing. |
| Ventral spermathecal pores were drawn on one side only. Male pores were arranged sequentially along the body. | Redrew four bilateral pore pairs and a male-pore pair at the same longitudinal level; retained the single median female pore. |
| External segmentation was obscured by the opaque overlying body shape. | Added visible segment boundaries, moderated the broad glare and made the clitellar region explicit. Posterior segment compression is labelled. |
| Septal nephridia resembled three continuous tubes. | Replaced them with discrete coiled units associated with septa. Pharyngeal nephridia now appear as three paired tufts. |
| A “typical nephridium” could imply that every type has a nephrostome. | Relabelled the enlarged unit as septal and clarified the nephrostome distinction in both languages. |
| Caeca were depicted with returning tubular paths; the typhlosole appeared through an intact gut. | Made the caeca blind-ended and explicitly presented the typhlosole through an opened gut window. |
| The transverse-section nerve cord and subneural vessel occupied the muscle-wall region; labels overlapped. | Rebuilt the section with the ventral vessel, nerve cord and subneural vessel ordered within the inner body-wall boundary, and separated leader lines and labels. |
| A long vertical system menu dominated a phone screen. | Added a compact mobile system selector and a desktop grid, plus previous/next system controls. |
| Small or overlapping diagram targets were difficult to operate. | Added a large structure selector; reduced oversized invisible hit outlines and retained keyboard activation. |
| Zoom could pan the specimen out of view and dragging could trigger a target action. | Clamped panning, added drag-click suppression and reset the view when changing systems. Zoom supports up to 5× through the controls. |
| “Optional review” continued filtering out future-due structures. | Added a genuine optional-review mode and an accurate status label. |
| Stored assessments could have inconsistent completion flags and counts; prototype names could be treated as valid IDs. | Strengthened completion/count checks and rejected inherited dictionary keys when restoring system/tool selections. |
| Selected anatomical details could remain in the previous language. | Refresh selected details when the language changes. |
| Speech could fail silently or use a network voice despite the privacy wording. | Require a matching local browser voice or Android offline TTS voice, show an unavailable-voice notice, add 0.8–1.0 speed and Stop voice, and stop speech when hidden. |
| Continuous effects and repeated texture filters added unnecessary rendering work. | Added Animation control; pause effects when hidden/offscreen; suppress texture layers on small/low-memory devices. Performance benefit is expected, not benchmarked on a phone. |
| Reference names lacked usable evidence links. | Added direct scientific references with stated scope and a separate technical resource list. |
| Completion wording could imply a formal certificate. | Renamed it a local practice completion record; retain separate first-attempt and retry results. It is not independently verified assessment evidence. |

## Scientific discrepancy requiring faculty sign-off

The file mixes a conventional practical account with an accepted species name. A published specimen redescription differs on these points:

| Character | Supplied convention | Bantaowong et al. (2011), p. 58 |
|---|---|---|
| Intestinal caecal origin | XXVI | XXVII |
| Gizzard position | VIII | IX–X |
| Clitellar setae | Absent | Ventral retention recorded |

The revised app labels the retained convention, provides the specimen evidence in the relevant structure descriptions and references, and removes the conflicting scored claims. This distinction must be settled against the institution's practical manual and identified material before examination use. One redescription should not itself be treated as proof that every population has identical morphology. [Primary redescription](https://li01.tci-thaijo.org/index.php/tnh/article/download/103012/82556/260320).

The nephridial corrections have direct support in Bahl's primary account: discrete units, different external/gut discharge routes, paired pharyngeal tufts, and septal nephridia beginning at septum 15/16. [Bahl, 1919](https://journals.biologists.com/jcs/article/s2-64/253/67/62908/On-a-New-Type-of-Nephridia-found-in-Indian).

The four-pair heart arrangement is retained as the supplied conventional teaching model. It has not been independently established for every specimen by this audit. The remaining general descriptions and Tamil terminology have been reviewed for internal consistency; they still need specialist teaching sign-off. The diagrams are edited vector schematics, not photographs or validated reconstructions of a measured specimen.

## Verification actually completed

- JavaScript syntax check passed.
- Final test suite passed 1,419 checks with no captured DOM runtime errors.
- All 56 guided actions were exercised in both languages against available targets.
- All 72 assessment items were exercised in both languages, including incorrect attempts followed by correction; retries did not inflate first-attempt results.
- All 55 structure records were checked for bilingual fields and exercised through the selector; microscopic panels opened where linked.
- Saved-state restoration, malformed JSON, unavailable storage, inconsistent completion data, optional review and language switching were checked.
- SVG references and DOM ID uniqueness were checked.
- All nine English plates were rendered with a standalone SVG renderer and visually inspected; several layout and anatomy defects were corrected during this pass. These images are not browser screenshots.
- Tamil glyph shaping was inspected with installed Noto Sans Tamil through Inkscape/Pango. Ten main diagram views (including both external surfaces) and nine microscopic views were rendered. All 124 visible label bounding boxes remained within their viewports, with no text-to-text overlap beyond the stated 2-unit tolerance. This is a standalone SVG check; browser/mobile layout and device font scaling remain unverified.
- Android manifest/resource XML parsed successfully. The HTML bundled in the Android assets matches the delivered HTML byte for byte.
- The Android project's locked Node dependencies installed offline from the local cache and its included test command completed successfully.

No measured startup time, memory ceiling, WCAG conformance result, screen-reader result, APK signature, installation result or Play approval is claimed.

## Android production initiated

The accompanying ZIP contains a dedicated Java Android project, the bundled revised simulator, a vector launcher icon, a GitHub Actions build workflow, DOM tests, an unrun Android instrumentation test, a privacy draft, a device acceptance checklist and verified resource links.

| Setting | Prepared value |
|---|---|
| Application ID | `in.ramesh.zoology.earthwormlab` — confirm before first public signed release |
| Version | `1.3.1-beta` / versionCode `10301` |
| Minimum Android | API 24 |
| Compile / target SDK | API 36 |
| Build baseline | JDK 17, Gradle 8.13, AGP 8.13.2 |
| WebKit library | AndroidX WebKit 1.14.0, pinned baseline |
| Content loading | WebViewAssetLoader, bundled HTML, local HTTPS origin |
| Permissions | No INTERNET, camera, microphone or storage permission |
| Native bridge | Main-frame-only, origin-scoped messages for voice listing, speech, stop and print |
| Recovery | Local-load/initialisation/renderer-failure screens and retry |
| Updates | Reviewed, newly bundled and signed app releases; no remote code updater |

The build baseline follows the published [AGP compatibility table](https://developer.android.com/build/releases/agp-8-13-0-release-notes). Local content and native messaging follow Android's [asset-loading](https://developer.android.com/develop/ui/views/layout/webapps/load-local-content) and [origin-scoped bridge](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge) guidance.

The project is **not compiled**. The standard Gradle wrapper binary was not available; the CI workflow provisions Gradle 8.13, and the README explains local setup and wrapper generation. No signing key, repository write, paid account, publication or remote-update connection was created.

## Next production action

1. Put the archive's contents in a dedicated GitHub repository, preserving `.github/workflows/android.yml` at the root.
2. Run **Actions → Validate and build Android beta**. Review actual Android lint/compile results. The first successful run produces a debug-signed test APK and separately labelled unsigned release candidates.
3. Test the APK on a phone using `DEVICE-ACCEPTANCE.md`, prioritising offline startup, Tamil display and TTS, small-screen navigation, rapid switching, Back, reload, print and low-memory recovery.
4. Obtain anatomy/Tamil sign-off, resolve the documented specimen/convention differences, then generate a release APK/AAB using an owner-controlled signing key. Preserve that key and increase versionCode for every update.

The highest-value later additions are a validated progress backup/restore flow, teacher-reviewed answer rationales, and specimen-linked illustration references. These are recommendations, not implemented features.

## Delivered HTML identity

Version: `1.3.1-beta`  
SHA-256: `dcf0dc4dff79db0355c9cb904fc5af241715062431c6c6d29d3c7099699cc04f`  
Bytes: 520919

The standalone HTML and Android asset are byte-identical.
