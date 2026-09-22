# 1.3.6 — Indus Appstore remediation

- Added a native Android home/dashboard so the app launches as an educational Android application rather than directly into the WebView.
- Added native entry points for Continue Laboratory, Guided Dissection, Explore Anatomy, Assessment & Revision, and About & Privacy.
- Added a persistent native laboratory toolbar with Home and Privacy actions.
- Added an in-app native privacy policy and a public HTTPS privacy-policy link.
- Preserved the offline bundled laboratory, native Android TTS, Android print/save, predictive Back integration, renderer recovery and device-local progress.
- Kept the Android manifest permission-free; no INTERNET, camera, microphone, storage or location permission is requested.
- Bumped versionName to 1.3.6 and versionCode to 10306 without changing the reviewed academic data sets or SVG anatomy baseline.

# 1.3.3-beta — pre-GitHub revision

- Reproduced and fixed silent procedure auto-advance continuing after native pause; page-hide now uses the same complete pause handler.
- Added immediate handling of a matching native speech-cancelled event; stale cancellation events remain ignored.
- Added Android onStop propagation, stale-WebView guards, recovery cleanup, bounded Back handling and print completion/error cleanup.
- Added frozen educational-data and SVG/style hashes, full CSS/state inventory, XML/JavaScript checks and Java 17 syntax-only parsing.
- Added full-page Chromium tests at three viewport sizes, with isolated renderer tests as a prerequisite. Actual browser execution is pending.
- Made Android artifacts depend on successful source and browser validation.
- Added microscopic-panel DOM ID/reference checks.
- Explicitly superseded unsupported earlier visual claims and segregated historical extraction evidence.
- Kept scientific text, answers, Tamil narration, drawings and local-progress identity unchanged.
- Corrected the probe-travel CSS animation after real Chromium evidence showed that it replaced the SVG group's base translation and moved its bilingual label above the viewport.
- Documented the API 24–32 onBackPressed fallback with a narrow lint annotation; API 33+ continues through the registered predictive-Back callback.
