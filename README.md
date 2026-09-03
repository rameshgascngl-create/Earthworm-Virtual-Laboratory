# Earthworm Virtual Laboratory — 1.3.3-beta

Offline bilingual teaching simulator for *Metaphire posthuma* (syn. *Pheretima posthuma*).  
Department of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu. R.Ramesh.

## Status before GitHub

This is a source candidate prepared for a verification build, not a compiled or production-certified Android app. No repository was created or changed in this revision.

The revision fixes a reproduced silent-procedure pause defect and adds native cancellation handling and recovery safeguards. It preserves the SVG, every style block, all 55 structure records, 56 guided actions, 72 questions, nine microscopic lessons and reviewed Tamil narration.

Earlier extracted SVG proofs are historical, non-authoritative evidence. They do not establish live-browser layout correctness. See AUDIT.md and review/SVG-AUDIT-PROTOCOL.md.

## Next GitHub action

1. Upload the archive's contents to a dedicated repository root, including .github/workflows/android.yml. Do not nest the project inside a second folder.
2. Open Actions → Validate and build Android beta → Run workflow.
3. The validate job runs source/data/speech tests, then isolated renderer checks and the complete unchanged HTML in Chromium at desktop, 390-pixel and 360-pixel widths.
4. The build job runs only after validation succeeds. It runs Android lint and compiles the app, instrumentation APK and unsigned release candidates.
5. After a successful build, download Earthworm-1.3.3-beta-debug-APK. Review browser screenshots and complete DEVICE-ACCEPTANCE.md before classroom distribution.

The browser suite has been syntax-checked and collected, but not executed here. A first CI failure must be investigated from its evidence; do not disable the gate merely to obtain an APK. Renderer-fixture failures are tooling issues, not anatomical defects.

## Local validation and compilation

Requires Node 22–24 and JDK 17 or newer for source validation; use JDK 17 for the pinned Android build.

```sh
npm ci --ignore-scripts
npm test
npm run test:browser:list
npx --no-install playwright install --with-deps chromium
npm run test:browser
gradle --no-daemon :app:lintDebug :app:lintRelease :app:assembleDebug :app:assembleDebugAndroidTest
gradle :app:connectedDebugAndroidTest
```

Run browser tests only in an environment where local browser access is permitted. The final command requires an emulator or connected Android device.

The baseline is Gradle 8.13, AGP 8.13.2, compile/target API 36, minimum API 24 and AndroidX WebKit 1.14.0. The standard Gradle wrapper binary is not included. CI provisions Gradle 8.13; locally install that version or generate the standard wrapper with `gradle wrapper --gradle-version 8.13`. Never substitute an unofficial wrapper binary.

## Tamil and offline operation

Noto Sans Tamil fonts are embedded; no font CDN or remote startup page is required. The app uses installed local English/Tamil speech voices. Select தமிழ் → குரலைச் சோதிக்க and compare voices at 0.8×, 0.9× or 1×. Naturalness and technical pronunciation still require listening on the intended phone; human-recorded narration is not bundled.

The Android wrapper uses WebViewAssetLoader and an origin-scoped, main-frame native bridge. There is no INTERNET, camera, microphone or storage permission. User-initiated scientific references open in the external browser. Progress stays in local WebView storage; no cloud account or remote code updater is present.

## Signing and identity

Application ID: `in.ramesh.zoology.earthwormlab`  
versionName: `1.3.3-beta`  
versionCode: `10303`

Confirm the application ID before the first public signed release. CI debug APKs use a test key, which may change between clean runners; installing over a differently signed build can fail. Do not uninstall an app containing needed progress just to bypass a signature mismatch—uninstalling removes local data. Use a test device/profile.

Unsigned release outputs are not final distributable apps. Use an owner-controlled signing key, keep it secure, preserve the package ID and signing identity, and increase versionCode for updates.

The source includes no signing key, credentials, account connection or publication. See AUDIT.md, RESOURCES.md, DEVICE-ACCEPTANCE.md and PRIVACY.md.
