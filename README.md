# Earthworm Virtual Laboratory — 1.3.5

Offline bilingual teaching simulator for *Metaphire posthuma* (syn. *Pheretima posthuma*).  
Department of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu. R.Ramesh.

## This branch

`v1.3.5-advanced` updates `app/src/main/assets/index.html` to the Advanced practical track:

- No crop; four heart pairs (VII, IX, XII, XIII)
- Advanced spotter (click the named organ on the schematic)
- Schematic vs Tray look (reconstruction, not a wet-lab photograph)
- Extra Pheretima landmarks and viva traps against *Lumbricus* posters

Application ID: `in.ramesh.zoology.earthwormlab`  
versionName: `1.3.5`  
versionCode: `10305`

Progress storage key is unchanged (`earthwormDissection.v1`).

## Build

```sh
npm ci --ignore-scripts
npm test
gradle --no-daemon :app:assembleDebug
```

A sideload debug APK may also be produced outside CI. Uninstall an older lab signed with a different debug key before installing, or use a test profile. Uninstalling clears local progress.

## Species caution

Do not label this specimen from *Lumbricus terrestris* posters or YouTube trays (crop + five hearts). This module is the Indian megascolecid teaching account.

See CHANGELOG.md, DEVICE-ACCEPTANCE.md and PRIVACY.md.
