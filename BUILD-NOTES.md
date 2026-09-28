# BUILD-NOTES — native/v2.0.0-compose (Phase 1)

**Status: still not built.** Every command below is checked for correctness
against this repository's actual files (root `build.gradle`, `settings.gradle`,
`app/build.gradle`, absence of a committed Gradle wrapper) and against
publicly documented version-compatibility data (cited below, found by web
search during this audit, not recalled from memory) — but none of it has
been executed, because this authoring environment has no JDK, no Android
SDK, and — confirmed empirically, not assumed — no network access to
Google's Maven repository or Maven Central (`dl.google.com`,
`repo.maven.apache.org`, `repo1.maven.org` all returned
`403 host_not_allowed` when probed directly).

## Resolved build matrix

| Component | Version | Source |
|---|---|---|
| Gradle | **8.13** (no wrapper committed — must be installed locally or generated, see below) | Required minimum for AGP 8.13.2 (developer.android.com) |
| AGP | 8.13.2 | `build.gradle` (unchanged from `main`) |
| Kotlin Gradle Plugin | **2.2.0** (corrected from 2.1.0 during this audit) | Kotlin 2.2.0 docs: "fully compatible with Gradle 7.6.3 through 8.14" — covers the required Gradle 8.13 cleanly. Kotlin 2.1.0's documented range only reliably covers Gradle up to 8.10. |
| Compose compiler | tracks Kotlin 1:1 (`org.jetbrains.kotlin.plugin.compose` pinned to the same 2.2.0) | developer.android.com/develop/ui/compose/bom: "As of Kotlin 2.0, the Compose compiler is managed alongside the Kotlin compiler and uses the same version." |
| Compose BOM | 2025.01.00 | `app/build.gradle`. Not independently build-verified against Kotlin 2.2.0 specifically — low risk (Compose libraries are generally forward-compatible with newer Kotlin compilers) but flagged rather than asserted. |
| compileSdk / targetSdk | 36 / 36 | `app/build.gradle` (unchanged from `main`) |
| minSdk | 24 | `app/build.gradle` (unchanged from `main`) |
| JDK | 17 | `compileOptions`/`kotlinOptions` in `app/build.gradle`; also AGP 8.13's own documented minimum JDK is 17 |

## Prerequisites

- JDK 17
- Gradle 8.13 on `PATH`, **or** a JDK/Gradle-having machine to generate the
  wrapper (this repo has never committed one — true on `main` too, per its
  own `README.md`)
- Network access to `google()` and `mavenCentral()` (declared in
  `settings.gradle`)

## Commands (still unexecuted)

```sh
# from repository root, on native/v2.0.0-compose, commit 285f76f
gradle wrapper --gradle-version 8.13   # generates gradlew; skip if Gradle 8.13 is already on PATH and you'd rather call `gradle` directly
./gradlew --no-daemon clean
./gradlew --no-daemon :app:assembleDebug
./gradlew --no-daemon :app:lintDebug
./gradlew --no-daemon :app:testDebugUnitTest
```

No Kotlin unit test source set exists yet — `:app:testDebugUnitTest` will
report zero tests, not failures. High-value Phase 1 unit tests worth adding
before this gate is meaningful: default selected system, system-switching
persistence, language switch not resetting selected system, and
`ScientificContentRepository`'s structure count against the bundled JSON —
none written yet; adding superficial tests just to have a number was
avoided rather than done.

The one pre-existing instrumentation test (`OfflineLaunchTest.java`) was
removed from this branch — it asserted against `MainActivity
.webViewForTest()` / `android.webkit.WebView`, neither of which exist here.

## What would make each gate real evidence

- `assembleDebug` succeeding → **NATIVE COMPILE PASS** (not yet Phase 1 pass)
- + `testDebugUnitTest` and `lintDebug` clean → still not sufficient alone
- + actual device/emulator QA (9-tab navigation, EN/TA switch preserving
  selection, rotation, Home/resume, Back, no crash/ANR) → only then
  **NATIVE PHASE 1 PASS — READY FOR ATLAS MIGRATION**

None of these have happened. This file records what commands are believed
correct, not what has run.
