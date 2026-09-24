// Source/data checks, not a renderer. Do not label this output browser verification.
const fs=require('node:fs'),path=require('node:path'),crypto=require('node:crypto'),vm=require('node:vm'),assert=require('node:assert/strict');
const YAML=require('yaml'),{JSDOM,VirtualConsole}=require('jsdom');
const root=path.resolve(__dirname,'..');process.chdir(root);
const read=p=>fs.readFileSync(p,'utf8'),sha=x=>crypto.createHash('sha256').update(x).digest('hex');
const html=read('app/src/main/assets/index.html'),pkg=JSON.parse(read('package.json')),lock=JSON.parse(read('package-lock.json'));
const baseline=JSON.parse(read('tests/release-baseline.json')),manifest=JSON.parse(read('SOURCE-MANIFEST.json'));
const evidence=[];function check(value,message){assert.ok(value,message);evidence.push(message)}
check(!html.includes('\uFFFD'),'No replacement characters in the HTML');
check(lock.version===pkg.version&&lock.packages[''].version===pkg.version,'Package and lockfile versions agree');
check(html.includes('APP_VERSION="1.3.8"'),'Laboratory HTML payload version remains the reviewed 1.3.8 text');
const gradle=read('app/build.gradle');check(gradle.includes("versionName '"+pkg.version+"'"),'Android version name agrees');
check(manifest.version===pkg.version&&gradle.includes('versionCode '+manifest.androidVersionCode),'Version code agrees with manifest');
check(manifest.revised.sha256===sha(html)&&manifest.revised.bytes===Buffer.byteLength(html),'Delivered HTML matches source manifest hash and size');
check(html.includes('STORAGE_KEY="earthwormDissection.v1"'),'Existing progress-storage key preserved');
const logs=[],log=new VirtualConsole();log.on('jsdomError',e=>logs.push(e.message));
const anchor='    initSegments();bind();';check(html.split(anchor).length===2,'Test hook has one explicit initialization anchor');
const code=html.replace(anchor,'window.__releaseData={SYSTEMS,STRUCTURES,MODULES,QUESTIONS,DEEP_DIVES,PROCEDURE_STEPS,TA_INTROS};'+anchor);
const dom=new JSDOM(code,{url:'https://appassets.androidplatform.net/assets/index.html',runScripts:'dangerously',virtualConsole:log,beforeParse(w){w.matchMedia=()=>({matches:false});w.HTMLElement.prototype.scrollIntoView=()=>{}}});
const d=dom.window.document,data=JSON.parse(JSON.stringify(dom.window.__releaseData));
for(const [key,expected] of Object.entries(baseline.dataHashes))check(sha(JSON.stringify(data[key]))===expected,'Reviewed educational data preserved: '+key);
const svg=html.match(/<svg id="anatomySvg"[\s\S]*?<\/svg>/)[0],styles=[...html.matchAll(/<style\b[^>]*>([\s\S]*?)<\/style>/g)].map(m=>m[1]);
check(sha(svg)===baseline.renderingContext.svgSHA256,'SVG markup matches the reviewed rendering baseline');
check(JSON.stringify(styles.map(sha))===JSON.stringify(baseline.renderingContext.styleSHA256),'Style blocks match the reviewed rendering baseline');
check(html.includes('@keyframes probeTravel{0%{transform:translate(260px,212px)}50%{transform:translate(710px,212px)}100%{transform:translate(1020px,212px)}}'),'Probe animation preserves its base SVG translation');
check(d.querySelectorAll('script[src],link[rel="stylesheet"]').length===0,'No external executable/style startup resources');
const csp=d.querySelector('meta[http-equiv="Content-Security-Policy"]')?.content||'';
for(const rule of ["default-src 'none'","connect-src 'none'","font-src data:"])check(csp.includes(rule),'Offline CSP retains '+rule);
const definitions=[...d.querySelectorAll('#anatomySvg defs [id]')].map(n=>n.id),ids=[...d.querySelectorAll('[id]')].map(n=>n.id);
check(ids.length===new Set(ids).size,'Initial DOM IDs are unique');
for(const m of svg.matchAll(/url\(#([^)]*)\)/g))check(definitions.includes(m[1]),'SVG definition resolves: '+m[1]);
const allRules=[],conditionals=[];
function walk(rules,context=[]){for(const r of rules){
  if(r.cssRules){walk(r.cssRules,[...context,r.conditionText||r.media?.mediaText||r.name||r.constructor.name]);continue;}
  if(!r.style)continue;
  const entry={kind:r.constructor.name,selector:r.selectorText||'',keyText:r.keyText||null,context,style:r.style.cssText};allRules.push(entry);
  if(/(?:display\s*:\s*none|visibility\s*:\s*hidden|opacity\s*:\s*0(?:[;\s]|$))/.test(entry.style)||/\[hidden\]|\.(?:active|open|opened|animate|revealed|procedure-active|print-atlas|print-certificate)\b/.test(entry.selector))conditionals.push(entry);
}}
for(const sheet of d.styleSheets)walk(sheet.cssRules);
const classes=[...new Set([...d.querySelectorAll('#anatomySvg [class]')].flatMap(n=>[...n.classList]))].sort();
const classRules=Object.fromEntries(classes.map(c=>[c,allRules.filter(r=>new RegExp('\\.'+c.replace(/[.*+?^${}()|[\]\\]/g,'\\$&')+'(?![\\w-])').test(r.selector)).map(r=>({selector:r.selector,context:r.context}))]));
const inventory={scope:'Complete stylesheet/rule inventory, not computed styles or rendering verification',htmlSHA256:sha(html),styleBlocks:styles.length,styleSHA256:styles.map(sha),svgDefinitionIds:definitions,svgClasses:classRules,conditionalRules:conditionals,allAuthorRules:allRules};
fs.mkdirSync('review',{recursive:true});fs.writeFileSync('review/svg-context-inventory.json',JSON.stringify(inventory,null,2));
check(d.styleSheets.length===styles.length,'Every inline stylesheet is inventoried');
check(conditionals.some(r=>r.context.some(c=>c.includes('print'))),'Print media rules inventoried');
check(conditionals.some(r=>r.selector.includes('.procedure-demo')),'Procedure visibility rules inventoried');
const workflow=YAML.parse(read('.github/workflows/android.yml'));
check(workflow.on.workflow_dispatch!==undefined||Object.hasOwn(workflow.on,'workflow_dispatch'),'Workflow supports manual dispatch');
check(workflow.permissions.contents==='read','Workflow has read-only repository permission');
check(workflow.jobs.build.needs==='validate','Android artifacts require successful validation');
const validate=workflow.jobs.validate.steps.map(s=>s.run||'').join('\n');
check(validate.includes('npm test')&&validate.includes('npm run test:browser'),'Both DOM/data and full-browser gates are configured');
check(!workflow.on.pull_request_target,'No privileged pull-request-target workflow');
const native=read('app/src/main/java/in/ramesh/zoology/earthwormlab/MainActivity.java');
const nativeHome=read('app/src/main/java/in/ramesh/zoology/earthwormlab/NativeHomeActivity.java');
const privacyActivity=read('app/src/main/java/in/ramesh/zoology/earthwormlab/PrivacyActivity.java');
check(!native.includes('addJavascriptInterface('),'No unrestricted JavaScript bridge');
check(nativeHome.includes('Continue Laboratory')&&nativeHome.includes('Guided Study')&&nativeHome.includes('Assessment'),'Native educational dashboard exposes substantive learning entry points');
check(native.includes('EXTRA_LAUNCH_ACTION')&&native.includes('applyNativeLaunchAction'),'Native dashboard routes into laboratory modules');
check(privacyActivity.includes('PUBLIC_POLICY_URL')&&privacyActivity.includes('privacy.html'),'Native About & Privacy screen links to the public policy');
check(native.includes('registerOnBackInvokedCallback')&&native.includes('@SuppressLint("GestureBackNavigation")'),'Predictive Back is registered while the legacy API 24–32 fallback is retained');
const android=new JSDOM(read('app/src/main/AndroidManifest.xml'),{contentType:'text/xml'});
check(android.window.document.querySelectorAll('uses-permission').length===0,'Android manifest requests no permissions');
const appNode=android.window.document.querySelector('application');
check(appNode?.getAttribute('android:icon')==='@mipmap/ic_launcher','Manifest uses canonical mipmap launcher icon');
check(appNode?.getAttribute('android:roundIcon')==='@mipmap/ic_launcher_round','Manifest declares canonical round launcher icon');
const activities=[...android.window.document.querySelectorAll('activity')];
const launcher=activities.find(a=>a.querySelector('action[android\\:name="android.intent.action.MAIN"]')&&a.querySelector('category[android\\:name="android.intent.category.LAUNCHER"]'));
check(launcher?.getAttribute('android:name')==='.NativeHomeActivity','Native dashboard is the Android launcher activity');
check(activities.some(a=>a.getAttribute('android:name')==='.PrivacyActivity'),'Native privacy activity is packaged');
const iconDir='app/src/main/icon-payload';
const iconParts=fs.readdirSync(iconDir).filter(n=>/^part\d\d\.b64$/.test(n)).sort();
check(JSON.stringify(iconParts)===JSON.stringify(Array.from({length:8},(_,i)=>'part'+String(i).padStart(2,'0')+'.b64')),'Launcher payload has exactly eight ordered parts');
const iconBytes=Buffer.from(iconParts.map(n=>read(path.join(iconDir,n)).trim()).join(''),'base64');
check(iconBytes.length===23974,'Launcher WebP byte length is exact');
check(sha(iconBytes)==='2ade2a9eedd49679a0b0f15a42a4d75852771272ea6ba0944e8f118a93a8a931','Launcher WebP SHA-256 matches accepted artwork');
check(iconBytes.subarray(0,4).toString('ascii')==='RIFF'&&iconBytes.subarray(8,12).toString('ascii')==='WEBP','Launcher payload is RIFF/WebP');
check(iconBytes.readUInt32LE(4)+8===iconBytes.length,'Launcher WebP RIFF length is internally consistent');
const adaptiveFiles=['app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml','app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml'];
for(const f of adaptiveFiles){const x=read(f);check(x.includes('@drawable/ic_launcher_foreground')&&x.includes('@color/ic_launcher_background'),'Adaptive icon is wired to accepted foreground/background: '+f);}
for(const density of ['mdpi','hdpi','xhdpi','xxhdpi','xxxhdpi'])for(const name of ['ic_launcher.xml','ic_launcher_round.xml']){const f='app/src/main/res/mipmap-'+density+'/'+name;check(read(f).includes('@drawable/ic_launcher_source'),'Legacy launcher alias uses accepted artwork: '+f);}
check(read('app/src/main/res/drawable/ic_launcher_foreground.xml').includes('@drawable/ic_launcher_source'),'Adaptive foreground uses accepted artwork');
check(!fs.existsSync('app/src/main/res/drawable/ic_launcher.xml'),'Superseded vector launcher removed');
check(!fs.existsSync('app/src/main/res/drawable-nodpi/ic_launcher_photo.webp'),'Malformed v1.3.7 launcher asset removed');
android.window.close();
function files(dir){return fs.readdirSync(dir,{withFileTypes:true}).flatMap(e=>['node_modules','.git','.gradle','build','test-results','playwright-report'].includes(e.name)?[]:e.isDirectory()?files(path.join(dir,e.name)):[path.join(dir,e.name)])}
const javaFiles=files(path.join(root,'app/src')).filter(f=>f.endsWith('.java'));
const parsed=require('node:child_process').spawnSync('java',['tests/ParseJava.java',...javaFiles],{encoding:'utf8'});
check(parsed.status===0,'Java source parses without syntax errors: '+(parsed.stderr||''));
fs.writeFileSync('tests/java-parse-results.json',JSON.stringify(JSON.parse(parsed.stdout),null,2));
for(const f of files(root)){
  check(!/\.(jks|keystore)$|(?:^|[/\\])(?:local|keystore)\.properties$/.test(f),'No private build/signing file: '+path.relative(root,f));
  if(/\.(?:cjs|js)$/.test(f)){new vm.Script(read(f),{filename:path.relative(root,f)});evidence.push('JavaScript syntax: '+path.relative(root,f));}
  if(f.endsWith('.xml')){const x=new JSDOM(read(f),{contentType:'text/xml'});x.window.close();evidence.push('XML parsed: '+path.relative(root,f));}
}
for(const n of d.querySelectorAll('script:not([src])')){new vm.Script(n.textContent);evidence.push('Embedded JavaScript syntax');}
check(logs.length===0,'No captured initialization errors');dom.window.close();
const report={date:new Date().toISOString().slice(0,10),version:pkg.version,htmlSHA256:sha(html),checks:evidence.length,errors:[],evidence,browserExecution:'not performed by preflight',androidCompilation:'not performed by preflight'};
fs.writeFileSync('tests/preflight-results.json',JSON.stringify(report,null,2));console.log(JSON.stringify({...report,evidence:undefined},null,2));
