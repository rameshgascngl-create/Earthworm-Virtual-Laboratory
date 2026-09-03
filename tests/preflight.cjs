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
check(html.includes('APP_VERSION="'+pkg.version+'"'),'HTML version agrees with package');
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
check(sha(svg)===baseline.renderingContext.svgSHA256,'SVG markup unchanged from 1.3.2');
check(JSON.stringify(styles.map(sha))===JSON.stringify(baseline.renderingContext.styleSHA256),'All style blocks unchanged from 1.3.2');
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
check(!native.includes('addJavascriptInterface('),'No unrestricted JavaScript bridge');
const android=new JSDOM(read('app/src/main/AndroidManifest.xml'),{contentType:'text/xml'});
check(android.window.document.querySelectorAll('uses-permission').length===0,'Android manifest requests no permissions');android.window.close();
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
