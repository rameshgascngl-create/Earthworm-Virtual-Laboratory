# Earthworm Virtual Laboratory — 1.3.8

Offline bilingual teaching simulator for *Metaphire posthuma* (syn. *Pheretima posthuma*).  
Department of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu. R.Ramesh.

## Status before GitHub

This branch is the Indus Appstore review-correction candidate. It preserves the reviewed educational WebView payload while adding a native Android launcher/dashboard and a native About & Privacy screen so the application exposes meaningful Android functionality before entering the offline laboratory.

The revision fixes a reproduced silent-procedure pause defect and adds native cancellation handling and recovery safeguards. A real Chromium run also exposed and corrected one probe-animation transform defect that moved its bilingual label outside the SVG. It preserves the SVG markup, all 55 structure records, 56 guided actions, 72 questions, nine microscopic lessons and reviewed Tamil narration.

Earlier extracted SVG proofs are historical, non-authoritative evidence. They do not establish live-browser layout correctness. See AUDIT.md and review/SVG-AUDIT-PROTOCOL.md.

## Next GitHub action

1. Upload the archive's contents to a dedicated repository root, including .github/workflows/android.yml. Do not nest the project inside a second folder.
2. Open Actions → Validate and build Android beta → Run workflow.
3. The validate job runs source/data/speech tests, then isolated renderer checks and the complete unchanged HTML in Chromium at desktop, 390-pixel and 360-pixel widths.
4. The build job runs only after validation succeeds. It runs Android lint and compiles the app, instrumentation APK and unsigned release candidates.
5. After a successful build, download Earthworm-1.3.8-validation-debug-DO-NOT-SUBMIT. Review browser screenshots and complete DEVICE-ACCEPTANCE.md before classroom distribution.

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
versionName: `1.3.8`  
versionCode: `10308`

Confirm the application ID before the first public signed release. CI debug APKs use a test key, which may change between clean runners; installing over a differently signed build can fail. Do not uninstall an app containing needed progress just to bypass a signature mismatch—uninstalling removes local data. Use a test device/profile.

Unsigned release outputs are not final distributable apps. Use an owner-controlled signing key, keep it secure, preserve the package ID and signing identity, and increase versionCode for updates.

The source includes no signing key, credentials, account connection or publication. See AUDIT.md, RESOURCES.md, DEVICE-ACCEPTANCE.md and PRIVACY.md.


## Production release

Do not submit the validation debug APK or unsigned release candidates. Production release signing is performed only by the manual **Build signed Earthworm v1.3.8 release** workflow on the `main` branch.

Required GitHub Actions repository secrets:

- `EARTHWORM_KEYSTORE_BASE64`
- `EARTHWORM_KEYSTORE_PASSWORD`
- `EARTHWORM_KEY_ALIAS`
- `EARTHWORM_KEY_PASSWORD`

The keystore must be the permanent Earthworm production signing identity. Do not commit the keystore or passwords to the repository.


## Launcher icon gate — v1.3.8

The launcher is wired through `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`. API 26+ uses adaptive-icon XML resources; mdpi through xxxhdpi have explicit legacy aliases. Every path resolves to the same accepted Earthworm/book/laboratory WebP reconstructed deterministically during Gradle preBuild from eight hash-pinned payload parts. The superseded vector launcher and malformed v1.3.7 launcher resource are removed.

Accepted launcher source: 23,974 bytes; SHA-256 `2ade2a9eedd49679a0b0f15a42a4d75852771272ea6ba0944e8f118a93a8a931`.
