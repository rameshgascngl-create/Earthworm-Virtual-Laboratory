# Changelog

## 1.3.6 — Indus Appstore review correction

- Added a native Android launcher/dashboard with direct entry points for continuing study, guided study, systems exploration, assessment and review.
- Added a native About & Privacy screen with an in-app public privacy-policy link.
- Finalised the public privacy policy to match the offline/no-account/no-analytics architecture.
- Kept the reviewed Zoology educational data, anatomy baselines, guided steps, questions and microscopic lessons unchanged.
- Updated Android/package/UI release identity from 1.3.5 / 10305 to 1.3.6 / 10306.
- Retained no INTERNET permission, `allowBackup=false`, `usesCleartextTraffic=false`, native Android TTS, print service, predictive Back handling and renderer recovery.

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
