// DOM and data regression tests. These do not emulate layout, Android, audio, or touch rendering.
const fs=require('fs'), assert=require('node:assert/strict'), path=require('path');
const {JSDOM,VirtualConsole}=require('jsdom');
const input=process.argv[2]||'output/Earthworm_Dissection_Simulator_Advanced_REALISTIC.html';
let source=fs.readFileSync(input,'utf8');
const exposed=`window.__test={SYSTEMS,STRUCTURES,MODULES,QUESTIONS,DEEP_DIVES,normalizeState,validateAssessmentData,selectSystem,selectMode,activeStructure,renderAll,renderInstruction,renderProcedure,openProcedure,closeProcedure,moveProcedure,rateReview,reviewCandidates,zoomBy,applyTransform,getState:()=>state,setState:x=>{state=normalizeState(x)},reviewView};`;
source=source.replace('    initSegments();bind();',exposed+'\n    initSegments();bind();');
const errors=[],log=new VirtualConsole();log.on('jsdomError',e=>errors.push(e.message));
function create(saved,blocked=false){return new JSDOM(source,{url:'https://appassets.androidplatform.net/assets/index.html',runScripts:'dangerously',pretendToBeVisual:true,virtualConsole:log,beforeParse(w){w.HTMLElement.prototype.scrollIntoView=()=>{};w.matchMedia=()=>({matches:false});w.print=()=>{};if(saved)w.localStorage.setItem('earthwormDissection.v1',saved);if(blocked)Object.defineProperty(w,'localStorage',{get(){throw Error('blocked')}})}})}
let checks=0;function ok(v,msg){assert.ok(v,msg);checks++}
const dom=create(),w=dom.window,d=w.document,t=w.__test;
ok(t,'Script initialised');
ok(Object.keys(t.SYSTEMS).length===9,'Nine systems preserved');
ok(Object.keys(t.STRUCTURES).length>=55,'At least 55 structures preserved');
ok(Object.values(t.QUESTIONS).reduce((a,x)=>a+x.length,0)>=72,'At least 72 questions preserved');
ok(Object.values(t.MODULES).reduce((a,x)=>a+x.length,0)>=56,'At least 56 guided steps preserved');
ok(Object.keys(t.DEEP_DIVES).length>=5,'Microscopic explanations preserved');
const ids=[...d.querySelectorAll('[id]')].map(x=>x.id);ok(new Set(ids).size===ids.length,'No duplicate DOM IDs');
for(const node of d.querySelectorAll('[fill],[stroke],[filter],[marker-end]'))for(const a of ['fill','stroke','filter','marker-end']){const m=(node.getAttribute(a)||'').match(/^url\\(#(.+)\\)$/);if(m)ok(d.getElementById(m[1]),'SVG reference '+m[1])}
for(const [id,s] of Object.entries(t.STRUCTURES))for(const key of ['en','ta','locEn','locTa','fnEn','fnTa','sigEn','sigTa','fixEn','fixTa'])ok(typeof s[key]==='string'&&s[key].trim(),'Bilingual field '+id+' '+key);
