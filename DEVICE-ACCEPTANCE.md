# Required acceptance before release

All items below are **pending real device verification**. Record phone model, Android version, System WebView version, installed TTS engine/voices, build SHA and artifact hash.

| Test | Expected result |
|---|---|
| Fresh install; airplane-mode launch | All nine systems, diagrams and lesson text load without a network connection. |
| Every guided module, both languages | All 56 target actions complete; selected tools and current target agree. |
| External dorsal/ventral selection | Mouth and genital openings appear only on the appropriate surface. |
| Small phone at 360×800 and 320px width; large font | No page-level horizontal overflow; all controls reachable; Tamil labels readable with zoom. |
| Pinch/drag or provided zoom buttons; page scrolling | No accidental action after dragging; specimen cannot be panned irretrievably out of view. Pinch zoom is not implemented; use the +/− controls. |
| Fast system changes ×50; background/foreground ×10 | No freeze, stale lesson, accumulating sound or lost saved progress. |
| Reduced motion / Animation off | Animations pause; target highlighting and procedure step visibility remain useful. |
| Tamil sample at 0.8×, 0.9×, 1× on each installed local voice | Listen for natural phrase boundaries and pronunciation of கிளைட்டெல்லம், அரவைப்பை, நெஃப்ரிடியம், டைஃப்ளோசோல் and numbered segments. Record engine and selected voice name. |
| Long Tamil procedure; stop midway; change language; resume | No mid-sentence automatic advance, overlapping speech or stale sentence after cancellation. Error pauses the demonstration. |
| Tamil glyphs at enlarged device font settings | Vowel signs remain attached; controls wrap; diagram labels remain readable with zoom; microscopic labels remain within the panel. |
| English and Tamil TTS in airplane mode | Uses installed local voices; missing voice produces a clear notice; Stop voice works. |
| One wrong and then correct answer; reload mid-quiz | First-attempt score stays lower; completion-after-retry is distinct; order/progress restore. |
| All questions completed | Practice record represents local activity only; it must not be described as an accredited certificate. |
| Review with all cards due in future | Optional review starts and rating saves a new due date. |
| Close/relaunch, rotation, Android Back | Progress persists, dialogs/zoom dismiss, app exit is deliberate. |
| Print labelled diagram; print record; cancel print | Correct output; no blank page; screen styling restores after cancel or finish. |
| Renderer termination / low memory | Recovery screen appears and retry reloads bundled lessons. |
| Silent procedure; Home, lock screen or app switch | The procedure pauses without advancing offscreen; resumption is deliberate. |
| Native speech interruption | A stopped utterance ends the narration job immediately and does not restart from a stale callback. |
| Unresponsive page followed by Android Back | The bounded fallback offers a deliberate close choice; the Back control does not remain permanently locked. |
| Print startup failure and rapid recovery/retry | Print state resets; a late callback from an older page/job cannot alter the new page/job. |
| External link tap | Opens system browser; external navigation does not replace local app content. |
| Release lint, package validation and signing | No blocking lint failures; install/update succeeds with expected certificate and versionCode. |
| Faculty anatomy and Tamil review | All nine systems plus microscopic panels approved against specimen and the institutional practical manual. |

Suggested device matrix: Android 7/API24 minimum, Android 10/API29, Android 13/API33 and Android 16/API36; include a low-memory phone. The current source is not a promise of zero errors.

- [ ] Inspect all seven procedure stages in English and Tamil: only the relevant moving instrument is visible, open flaps leave labels readable, and the final caption fits at the phone's display/font settings.
