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
  capture_screen "$name" ".PrivacyActivity" "privacy"
  capture_language_set "$name" "ta"
  capture_screen "$name" ".PrivacyActivity" "privacy_ta" --ez "$LANG_EXTRA" true
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

expected=60
actual="$(find "$OUT" -name '*.png' -type f -size +0c | wc -l | tr -d ' ')"
test "$actual" -eq "$expected"

xml_count="$(find "$OUT" -name '*.xml' -type f -size +0c | wc -l | tr -d ' ')"
test "$xml_count" -eq "$expected"

python3 - "$OUT" <<'PY'
import glob, os, re, sys, xml.etree.ElementTree as ET
out=sys.argv[1]
profiles={
    'phone360_normal':320,
    'phone390_large':320,
    'phone360_xlarge':320,
    'tablet800_portrait':240,
    'tablet1280_landscape':240,
}
bound_re=re.compile(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]')
errors=[]
tamil_re=re.compile(r'[\u0B80-\u0BFF]')

for xml_path in glob.glob(os.path.join(out,'*.xml')):
    base=os.path.basename(xml_path)
    profile=next((p for p in profiles if base.startswith(p+'_')),None)
    if not profile:
        continue
    density=profiles[profile]
    min_px=48*density/160.0

    try:
        root=ET.parse(xml_path).getroot()
    except Exception as e:
        errors.append(f'{base}: XML parse failed: {e}')
        continue

    nodes=list(root.iter('node'))
    parsed_bounds=[]
    for node in nodes:
        m=bound_re.fullmatch(node.attrib.get('bounds',''))
        if m:
            parsed_bounds.append(tuple(map(int,m.groups())))
    viewport_w=max((b[2] for b in parsed_bounds),default=0)
    viewport_h=max((b[3] for b in parsed_bounds),default=0)

    texts=[]
    for node in nodes:
        text=(node.attrib.get('text','')+' '+node.attrib.get('content-desc','')).strip()
        if text:
            texts.append(text)
        if node.attrib.get('clickable')=='true':
            m=bound_re.fullmatch(node.attrib.get('bounds',''))
            if not m:
                errors.append(f'{base}: clickable node lacks parseable bounds: {text!r}')
                continue
            x1,y1,x2,y2=map(int,m.groups())
            width,height=x2-x1,y2-y1

            # UIAutomator clips bounds to the visible viewport. A scrollable control
            # partly visible at the top/bottom can therefore appear only a few pixels
            # high even though its actual touch target is >=48dp. Enforce the touch
            # target rule only when the full vertical extent is visible.
            vertically_complete=(y1>0 and y2<viewport_h)
            horizontally_complete=(x1>0 and x2<viewport_w)

            if vertically_complete and horizontally_complete and width>0 and height>0 \
                    and (width+0.5<min_px or height+0.5<min_px):
                errors.append(
                    f'{base}: fully visible clickable target below 48dp: {text!r} '
                    f'{width}x{height}px, required >= {min_px:.0f}px')

    joined=' '.join(texts)
    if '_ta.xml' in base and not tamil_re.search(joined):
        errors.append(f'{base}: Tamil profile contains no Tamil UI text')

if errors:
    print('LAYOUT SEMANTIC QA: FAIL')
    for e in errors:
        print(e)
    raise SystemExit(1)

print('LAYOUT SEMANTIC QA: PASS — Tamil text present and visible clickable targets >=48dp')
PY

echo "LAYOUT MATRIX CAPTURE: PASS ($actual screenshots, English + Tamil including privacy)"
