# Evidence status for 1.3.3-beta

The current source inventory and test results are not browser screenshots or Android execution evidence.

- SVG-AUDIT-PROTOCOL.md is the supplied correction protocol.
- svg-context-inventory.json enumerates every inline stylesheet, nested rule context, SVG class, definition and conditional-display rule. The complete HTML remains the rendering source.
- ../tests/release-baseline.json freezes the preceding version's educational data hashes and exact SVG/style hashes.
- ../tests/browser contains full-document Chromium tests and isolated renderer capability fixtures. The capability project must pass before the app projects run. These tests are pending actual execution in an authorized environment.
- legacy-extractions contains the earlier standalone SVGs, contact sheets, bounding-box measurements and historical audit. They are non-authoritative and must not be cited as proof of live-app defects or browser/mobile acceptance.

Do not reuse the old extraction script as the acceptance renderer. Keep all SVG definitions, fonts, CSS, media rules and actual state classes. A renderer failure is not an application defect. After any test/extraction correction, rerun the complete affected suite, not just the view that first looked wrong.

The new browser suite attaches complete-page screenshots and geometry. Label-box intersections are recorded for inspection, not automatically equated to collisions. Fonts, shape occlusion, animation transitions, native WebView rendering, physical touch and audible voice quality still require appropriate review.
