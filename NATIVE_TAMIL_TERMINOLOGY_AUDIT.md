# Native Tamil terminology audit — Pass 1

Target: `native/v2.0.0-alpha1`

## Editorial principle

The Tamil layer follows Tamil Nadu textbook-style scientific prose. Established zoological loan terms are retained when they are recognisable to students and teachers; they are not replaced with forced literal coinages merely to reduce English-derived terminology.

Public Tamil Nadu teaching material uses the register represented by terms such as:
- இரத்த ஓட்ட மண்டலம்
- கழிவு நீக்க மண்டலம்
- நெஃப்ரிடியா
- இரத்த நுண் நாளங்கள்
- அண்டநாளம்
- நரம்புத் திரள்

## Terms deliberately retained

| English | Tamil app term | Decision |
|---|---|---|
| Clitellum | கிளைட்டெல்லம் (சுரப்புப் பட்டை) | RETAIN |
| Nephridia | நெஃப்ரிடியா | RETAIN |
| Typhlosole | டைஃப்ளோசோல் | RETAIN |
| Peritoneum | பெரிட்டோனியம் | RETAIN |
| Vasa deferentia | விந்துநாளங்கள் | RETAIN |
| Oviduct | அண்டநாளம் | RETAIN |
| Spermathecae | விந்தேற்புப் பைகள் | RETAIN |
| Circulatory system | இரத்த ஓட்ட மண்டலம் | RETAIN |
| Excretory system | கழிவு நீக்க மண்டலம் | RETAIN |

## Targeted editorial corrections applied

1. **Pharynx correction text**
   - Removed the stale English assertion that the gizzard is specifically in segment VIII.
   - Tamil now contrasts function and wall structure without forcing one disputed gizzard segment.

2. **Capillary terminology**
   - `Subepidermal capillaries` is now:
     `புறத்தோலுக்குக் கீழுள்ள இரத்த நுண்நாளங்கள் (தந்துகிகள்)`.
   - This preserves the technical synonym while foregrounding the textbook-readable phrase `இரத்த நுண்நாளங்கள்`.

3. **Cerebral ganglia**
   - Tamil label now:
     `மூளை நரம்புத் திரள்கள் (தொண்டைமேல் நரம்புத் திரள்கள்)`.
   - The first term is student-friendly; the second supplies the positional textbook alias.

4. **Subpharyngeal ganglion**
   - Normalised to `தொண்டைக்கீழ் நரம்புத் திரள்` for direct anatomical meaning.

5. **Bilingual UI parity**
   - System navigation, selected Canvas labels, structure index heading, speech button and Canvas accessibility description now switch with Tamil/English mode.
   - Canvas no longer owns a separate Tamil terminology table. Selected labels come from the authoritative content repository.

## Terms intentionally not changed

- Roman segment numerals remain Roman numerals.
- *Metaphire posthuma* remains a scientific binomen and is not transliterated as a substitute for the Latin name.
- Provenance language and uncertainty are retained in Tamil wherever English states specimen variation or evidence limitations.

## Remaining Tamil QA

- Full sentence-level semantic parity across all 72 questions.
- Full guided-action Tamil register review across 56 actions.
- TTS pronunciation quality for mixed Tamil + Latin scientific names.
- Physical-device review for Tamil glyph clipping and large-font layout.

No production release is authorised by this pass alone.
