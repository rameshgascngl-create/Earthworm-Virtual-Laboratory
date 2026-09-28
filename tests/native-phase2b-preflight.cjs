// Native Phase-2B CI preflight. No browser/HTML assumptions.
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');

const root = path.resolve(__dirname, '..');
process.chdir(root);
const read = p => fs.readFileSync(p, 'utf8');
const exists = p => fs.existsSync(p);
const walk = dir => fs.readdirSync(dir, {withFileTypes:true}).flatMap(e => {
  const p = path.join(dir, e.name);
  if (['.git','.gradle','build','node_modules','test-results','playwright-report'].includes(e.name)) return [];
  return e.isDirectory() ? walk(p) : [p];
});
const check = (v, m) => { assert.ok(v, m); console.log('PASS:', m); };
const expectedVersionCode = process.env.EXPECTED_VERSION_CODE || '10308';
const expectedVersionName = process.env.EXPECTED_VERSION_NAME || '1.3.8';

check(exists('settings.gradle') || exists('settings.gradle.kts'), 'Android settings file exists');
check(exists('app/build.gradle') || exists('app/build.gradle.kts'), 'Android app Gradle file exists');
check(exists('app/src/main/AndroidManifest.xml'), 'Android manifest exists');
check(exists('app/src/main/java/in/ramesh/zoology/earthwormlab/ui/atlas/AtlasScreen.kt'), 'AtlasScreen.kt exists');

const all = walk('app/src/main');
const contentJson = all.find(f => path.basename(f) === 'earthworm_content_v138.json');
const geometryKt = all.find(f => path.basename(f) === 'StructureGeometry.kt');
check(contentJson, 'earthworm_content_v138.json exists');
check(geometryKt, 'StructureGeometry.kt exists');

const data = JSON.parse(read(contentJson));
const structures = Array.isArray(data.structures) ? data.structures :
  (data.structures && typeof data.structures === 'object' ? Object.values(data.structures) : []);
check(structures.length === 55, 'Scientific content contains exactly 55 structures');

const geometry = read(geometryKt);
const hotspotCount = (geometry.match(/^\s*\"[^\"]+\"\s+to\s+\(/gm) || []).length;
check(hotspotCount === 55, 'StructureGeometry.kt contains exactly 55 hotspot definitions');

const hq = [
  'hq_atlas_external.webp','hq_atlas_digestive.webp','hq_atlas_circulatory.webp',
  'hq_atlas_respiratory.webp','hq_atlas_excretory.webp','hq_atlas_reproductive.webp',
  'hq_atlas_nervous.webp','hq_atlas_crosssection.webp'
];
for (const f of hq) check(exists(path.join('app/src/main/res/drawable-nodpi', f)), 'HQ WebP present: ' + f);

const svgFiles = all.filter(f => /[\\/]res[\\/]raw[\\/]atlas_(external|digestive|circulatory|respiratory|excretory|reproductive|nervous|crosssection)_(en|ta)\.svg$/.test(f));
check(svgFiles.length === 16, 'Exactly 16 EN/TA atlas SVG resources are present');

const atlas = read('app/src/main/java/in/ramesh/zoology/earthwormlab/ui/atlas/AtlasScreen.kt');
const mappings = {
  EXTERNAL:'hq_atlas_external', DIGESTIVE:'hq_atlas_digestive',
  CIRCULATORY:'hq_atlas_circulatory', RESPIRATORY:'hq_atlas_respiratory',
  EXCRETORY:'hq_atlas_excretory', REPRODUCTIVE:'hq_atlas_reproductive',
  NERVOUS:'hq_atlas_nervous', TRANSVERSE_SECTION:'hq_atlas_crosssection'
};
for (const [system, drawable] of Object.entries(mappings)) {
  const re = new RegExp('EarthwormSystem\\.' + system + '\\s*->\\s*R\\.drawable\\.' + drawable);
  check(re.test(atlas), 'Stable system-to-HQ mapping exists: ' + system + ' -> ' + drawable);
}
check(/ContentScale\.Fit/.test(atlas) && /HQ_ASPECT_RATIO\s*=\s*4f\s*\/\s*3f/.test(atlas), 'Detailed image mode preserves 4:3 FIT behaviour');
check(!/detailedMode[\s\S]{0,1000}structures\.forEach/.test(atlas), 'Detailed Image mode has no inherited SVG hotspot overlay');

const manifest = read('app/src/main/AndroidManifest.xml');
check(!/<uses-permission[^>]+android:name=["']android\.permission\.INTERNET["']/.test(manifest), 'INTERNET permission is absent');

const appFiles = walk('app').filter(f => /\.(kt|java|gradle|kts|xml)$/i.test(f));
const stripComments = x => x.replace(/\/\*[\s\S]*?\*\//g, '').replace(/^\s*\/\/.*$/gm, '');
const appText = appFiles.map(f => stripComments(read(f))).join('\n');
check(!/\bandroid\.webkit\.WebView\b|\bWebViewClient\b|androidx\.webkit/.test(appText), 'No WebView runtime dependency is present');
check(!/\b(okhttp|retrofit|ktor-client|volley|fuel)\b/i.test(appText), 'No common network client dependency is present');

const browserPayload = all.filter(f => /\.(html?|js|mjs|cjs)$/i.test(f));
check(browserPayload.length === 0, 'No HTML/JavaScript browser payload exists under app/src/main');

const gradle = read('app/build.gradle');
check(/applicationId\s+['"]in\.ramesh\.zoology\.earthwormlab['"]/.test(gradle), 'Expected applicationId retained');
check(/minSdk\s+24\b/.test(gradle), 'minSdk 24 retained');
check(/targetSdk\s+36\b/.test(gradle), 'targetSdk 36 retained');
check(new RegExp('versionCode\\s+' + expectedVersionCode + '\\b').test(gradle), 'versionCode ' + expectedVersionCode + ' retained');
const escapedVersionName = expectedVersionName.replace(/[.*+?^${}()|[\\]\\]/g, '\\check(/versionCode\s+10308\b/.test(gradle), 'versionCode 10308 retained');
check(/versionName\s+['"]1\.3\.8['"]/.test(gradle), 'versionName 1.3.8 retained');');
check(new RegExp('versionName\\s+[\\\'"]' + escapedVersionName + '[\\\'"]').test(gradle), 'versionName ' + expectedVersionName + ' retained');

console.log(JSON.stringify({
  scope: 'native-phase2b-preflight',
  structures: structures.length,
  hotspots: hotspotCount,
  hqWebPs: hq.length,
  atlasSvgs: svgFiles.length,
  browserPayloadFiles: browserPayload.length
}, null, 2));
