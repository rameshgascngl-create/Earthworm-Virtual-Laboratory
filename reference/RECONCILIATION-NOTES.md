# Digestive terminology — OPEN RECONCILIATION (do not resolve silently)

Source: direct comparison of `app/src/main/assets/index.html` content across four
distinct byte-identical-within-group variants found in this repository's branches,
2026-09-26.

## Gizzard — NO discrepancy found
Every variant labels the primary hotspot **"Gizzard VIII"** and only uses
**"gizzard IX–X"** inside an explicit in-app literature citation sentence:
> "...(2011), p. 58, report caeca XXVII, gizzard IX–X and retained ventral
> clitellar setae..."
This is intentional dual-convention annotation (textbook label + cited
redescription caveat), not a bug. An earlier audit pass in this project
misread this as an internal contradiction; it is not.

## Intestinal caeca — REAL discrepancy, unresolved
| Branch group | index.html SHA-256 (short) | Primary label | †-footnote label |
|---|---|---|---|
| `main`, `integration/v1.3.8-hq-atlas-20260926` | `d9b8cca…` / `5dbc6fe…` | Intestinal caeca **XXVI** | Intestinal caeca **XXVI†** |
| `ui/v1.3.9-mobile-system-tabs-20260926` (= the uploaded reference APK), `qa/v1.3.9-hq-emulator-20260926` | `4921509f…` / `2f599d1f…` | Intestinal caeca **XXVII** | Intestinal caeca **XXVII†** |

All four variants agree the *cited* redescription (Bantaowong et al. 2011, p.58)
reports **XXVII**. They disagree on what the *primary teaching label* should be:
two variants keep XXVI as the traditional convention with XXVII only in the
citation; two variants (including the one that produced the physical APK you
supplied) have collapsed the primary label to XXVII as well, losing the
dual-convention distinction.

`app/src/main/res/raw/earthworm_content_v138.json` (the machine-portable
dataset used by `ContentRepository.java`, and the source for
`scientific-content.json` in this directory) currently follows the
**XXVII-primary** convention — i.e. it agrees with the physical APK, not with
`main`.

**Correction to the paragraph above, found after inspecting the structure
content itself (not just the provenance summary):** `earthworm_content_v138.json`
→ `structures.intestinal-caeca` carries an explicit, reasoned correction note,
not silence:

> `fixEn`: "Do not move the caecal origin to XXVI, and do not present XXIV or
> XXV as a universal anterior endpoint."
> `sigEn`: "The XXVII origin is the more stable species-level landmark;
> anterior reach should not be taught as invariant."

So this is not an unresolved ambiguity in the JSON content layer — it is a
resolved, cited scientific decision (XXVII, with the traditional XXVI figure
identified and explicitly rejected). `main` and
`integration/v1.3.8-hq-atlas-20260926` are the branches that are **behind**
this decision, not the other way round.

**Revised action for R. Ramesh:** no scientific decision is actually pending
here — confirm you're comfortable with the JSON's existing XXVII/`fixEn`
position (it reads as sound: citation-backed, and it explicitly declines to
overstate a single anterior boundary across specimens), and treat `main`'s
still-dual-labelled hybrid HTML as the stale artifact to update, not the
JSON as provisional. The one remaining open item is presentational, not
scientific: whether the native UI keeps the hybrid's "†"-footnote convention
(primary label + cited-caveat variant) at all, or folds the caveat into the
structure detail text (`locEn`/`sigEn`/`fixEn`) instead, which is what
`ScientificContentRepository.contentFor()` currently assumes.
