#!/usr/bin/env bash
set -euo pipefail

PKG="in.ramesh.zoology.earthwormlab"
LANG_EXTRA="earthworm.language.tamil"
OUT="${1:-layout-qa}"
mkdir -p "$OUT"

adb reconnect >/dev/null 2>&1 || true
ready=0
for i in $(seq 1 45); do
  if [ "$(adb get-state 2>/dev/null || true)" = "device" ]; then
    ready=1
    break
  fi
  sleep 2
done
if [ "$ready" != "1" ]; then
  echo "Emulator did not return to a healthy ADB device state."
  adb devices -l || true
  exit 1
fi
adb wait-for-device

APK="$(find app/build/outputs/apk/debug -name '*.apk' -type f | head -n1)"
if [ -z "$APK" ]; then
  gradle --no-daemon :app:assembleDebug
  APK="$(find app/build/outputs/apk/debug -name '*.apk' -type f | head -n1)"
fi
adb install -r "$APK" >/dev/null

adb shell settings put system accelerometer_rotation 0 || true
adb logcat -c || true

capture_screen() {
  local profile="$1"
  local activity="$2"
  local label="$3"
  shift 3

  adb shell am force-stop "$PKG"
  adb shell am start -W -n "$PKG/$activity" "$@" > "$OUT/${profile}_${label}_am.txt"
  sleep 1

  local focus
  focus="$(adb shell dumpsys window 2>/dev/null | grep -E 'mCurrentFocus|mFocusedApp' | head -n2 || true)"
  printf '%s\n' "$focus" > "$OUT/${profile}_${label}_focus.txt"

  adb exec-out screencap -p > "$OUT/${profile}_${label}.png"
  adb shell uiautomator dump /sdcard/window.xml >/dev/null 2>&1 || true
  adb pull /sdcard/window.xml "$OUT/${profile}_${label}.xml" >/dev/null 2>&1 || true

  if ! grep -q "$PKG" "$OUT/${profile}_${label}_focus.txt"; then
    echo "Expected app focus missing for $profile/$label"
    cat "$OUT/${profile}_${label}_focus.txt"
    return 1
  fi
  test -s "$OUT/${profile}_${label}.png"
  test -s "$OUT/${profile}_${label}.xml"
}

capture_language_set() {
  local profile="$1" lang="$2"
  local suffix="" extra=()
  if [ "$lang" = "ta" ]; then
    suffix="_ta"
    extra=(--ez "$LANG_EXTRA" true)
  fi

  capture_screen "$profile" ".NativeHomeActivity" "home${suffix}" "${extra[@]}"
  capture_screen "$profile" ".MainActivity" "digestive${suffix}" --es earthworm.native.system digestive "${extra[@]}"
  capture_screen "$profile" ".GuidedActivity" "guided${suffix}" "${extra[@]}"
  capture_screen "$profile" ".AssessmentActivity" "assessment${suffix}" "${extra[@]}"
  capture_screen "$profile" ".MicroscopyActivity" "microscopy${suffix}" "${extra[@]}"
}

apply_profile() {
  local name="$1" size="$2" density="$3" font="$4" rotation="$5"
  adb shell wm size "$size"
  adb shell wm density "$density"
  adb shell settings put system font_scale "$font"
  adb shell settings put system user_rotation "$rotation"
  sleep 1

  {
    echo "name=$name"
    adb shell wm size
    adb shell wm density
    echo "font_scale=$(adb shell settings get system font_scale | tr -d '\r')"
    echo "rotation=$(adb shell settings get system user_rotation | tr -d '\r')"
  } > "$OUT/${name}_profile.txt"

  capture_language_set "$name" "en"
  capture_language_set "$name" "ta"
  capture_screen "$name" ".PrivacyActivity" "privacy"
}

apply_profile "phone360_normal" "720x1600" "320" "1.0" "0"
apply_profile "phone390_large" "780x1688" "320" "1.3" "0"
apply_profile "phone360_xlarge" "720x1600" "320" "1.5" "0"
apply_profile "tablet800_portrait" "1200x1920" "240" "1.3" "0"
apply_profile "tablet1280_landscape" "1920x1200" "240" "1.3" "1"

adb logcat -d > "$OUT/layout-logcat.txt"

if grep -E "FATAL EXCEPTION|ANR in $PKG|Process: $PKG.*has died" "$OUT/layout-logcat.txt"; then
  echo "Crash/ANR signature detected during layout matrix."
  exit 1
fi

expected=55
actual="$(find "$OUT" -name '*.png' -type f -size +0c | wc -l | tr -d ' ')"
test "$actual" -eq "$expected"

xml_count="$(find "$OUT" -name '*.xml' -type f -size +0c | wc -l | tr -d ' ')"
test "$xml_count" -eq "$expected"

echo "LAYOUT MATRIX CAPTURE: PASS ($actual screenshots, English + Tamil)"
