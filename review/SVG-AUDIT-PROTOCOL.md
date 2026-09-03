# Correction prompt: verifying CSS-driven layered SVGs before reporting defects

Use this before auditing or reviewing any single-file HTML app (dissection
simulators, anatomy atlases, diagram-heavy learning tools) that uses
`class="layer"` / `.active` toggling, gradients, or conditional overlays to
show one view of a shared SVG at a time.

## What went wrong, concretely

When asked to audit the Earthworm Dissection Simulator, I extracted each
organ-system `<g id="layer-X">` and rendered it standalone to check the
visuals. I reported four defects — a "broken" black box over the respiratory
view, a "sparse, low-contrast" nervous system, a "broken chevron" standing in
for the intestinal caeca, and colliding Anterior/Posterior labels in the setup
view. **All four were false.** Every one was caused by my own extraction
dropping something the live app supplies automatically:

1. **Missing `<style>` rules.** I pulled the `<svg>` node but not the page's
   `<style>` block, so `.hit{fill:transparent}` never applied — an invisible
   click-target rect rendered as a solid black box instead.
2. **Missing `<defs>` gradients/filters.** Shapes styled `fill="url(#someGrad)"`
   rendered flat/dark instead of the intended shaded, dimensional look —
   making a fully-realised nervous system and intestinal caeca look sparse or
   broken.
3. **Missing conditional-display rules.** `.procedure-demo{display:none}`
   (only shown during an animated walkthrough) wasn't applied, so a
   demo-only orientation label rendered simultaneously with the always-visible
   base label, looking like a duplicate/collision that doesn't exist in normal
   use.
4. I only caught this because I kept re-verifying after each correction
   instead of trusting the first "accurate-looking" render — the first two
   fixes each *still* left one wrong rendering pass in place.

The user should not have to catch this. The point of rendering before
reporting is to replace guesswork with evidence — a rendering pipeline that's
silently dropping content produces *worse* evidence than not rendering at
all, because it looks authoritative.

## Protocol: before rendering any layer/state of a shared SVG

1. **Extract the complete rendering context, not just the target markup.**
   At minimum: the full `<svg>...</svg>` including its own `<defs>`, *and*
   every `<style>` block in the document that contains a selector matching
   anything inside that SVG (`.layer`, `.hit`, `.organ-label`, any
   `class="..."` you see in the markup). Grep the stylesheet for each class
   name you find in the SVG and confirm each one is included — don't assume
   a hand-picked subset is complete.

2. **Enumerate every conditional-display rule before rendering anything.**
   Search the stylesheet for `display:none`, `[hidden]`, and any class whose
   sibling `.active`/`.open`/`.animate` variant implies a default hidden
   state. Build the *complete* list of "hidden unless X" rules and apply all
   of them for the state you're simulating — not just the one you happened
   to notice.

3. **If a CSS property might not be supported by your rendering tool**
   (e.g. `paint-order`, `backdrop-filter`, `feTurbulence`), test that
   specific property in isolation first with a trivial example. If it's
   unsupported, say so explicitly in your findings rather than reporting
   what the broken fallback rendering looks like as if it were the truth —
   an unsupported property producing an ugly result is a tooling gap, not a
   defect in the file.

4. **Re-render after every fix to your own extraction, not just once.**
   Each time you discover a missing piece (a style block, a gradient, a
   display rule), assume there may be more and re-verify the *whole* set of
   layers again — don't stop at the first clean-looking result.

5. **Before writing "this is broken" or "this is sparse/weak," ask:**
   could this be explained by something my extraction dropped, rather than
   something the file's author got wrong? If there's any doubt, render a
   minimal isolated test case to confirm the property/rule actually does
   what you think before attributing the result to the source file.

## How to report findings afterward

- State plainly which parts of the review are based on verified rendering
  (full defs + full relevant CSS + confirmed-supported properties) versus
  raw markup inspection versus assumption.
- If a finding later turns out to be a tooling artifact, correct it
  explicitly and say so — don't quietly drop it or soften it into vague
  language. Precision about what was wrong and why is more useful than a
  clean narrative.
- Prefer "I rendered this with X, Y, Z applied and confirmed..." over
  unqualified verdicts like "this looks broken" — the former is falsifiable
  and lets the person spot exactly where to double-check if something still
  seems off.
