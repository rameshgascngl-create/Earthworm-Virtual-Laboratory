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
ok(Object.keys(t.STRUCTURES).length===55,'55 structures preserved');
ok(Object.values(t.QUESTIONS).reduce((a,x)=>a+x.length,0)===72,'72 questions preserved');
ok(Object.values(t.MODULES).reduce((a,x)=>a+x.length,0)===56,'56 guided steps preserved');
ok(Object.keys(t.DEEP_DIVES).length===9,'Nine microscopic explanations preserved');
const ids=[...d.querySelectorAll('[id]')].map(x=>x.id);ok(new Set(ids).size===ids.length,'No duplicate DOM IDs');
for(const node of d.querySelectorAll('[fill],[stroke],[filter],[marker-end]'))for(const a of ['fill','stroke','filter','marker-end']){const m=(node.getAttribute(a)||'').match(/^url\(#(.+)\)$/);if(m)ok(d.getElementById(m[1]),'SVG reference '+m[1])}
for(const [id,s] of Object.entries(t.STRUCTURES))for(const key of ['en','ta','locEn','locTa','fnEn','fnTa','sigEn','sigTa','fixEn','fixTa'])ok(typeof s[key]==='string'&&s[key].trim(),'Bilingual field '+id+' '+key);
for(const lang of ['en','ta']){
  t.setState({lang,sound:false,voice:false});t.renderAll();
  for(const system of Object.keys(t.SYSTEMS)){
    t.selectSystem(system);t.selectMode('guided');
    for(const step of t.MODULES[system]){
      const current=t.getState().steps[system];
      d.querySelector(`[data-tool="${step.tool}"]`).click();
      // Avoid toggling a tool already selected across consecutive steps.
      if(t.getState().tool!==step.tool)d.querySelector(`[data-tool="${step.tool}"]`).click();
      const node=t.activeStructure(step.target);ok(node,'Visible guided target '+system+':'+step.target);
      node.dispatchEvent(new w.MouseEvent('click',{bubbles:true}));
      ok(t.getState().steps[system]===current+1,'Guided progression '+lang+':'+step.target);
    }
    t.selectMode('assessment');
    const total=t.QUESTIONS[system].length;
    for(let i=0;i<total;i++){
      const a=t.getState().assessment[system],q=t.QUESTIONS[system][a.order[a.index]];
      ok(q.oEn.length===4&&q.oTa.length===4&&Number.isInteger(q.a)&&q.a>=0&&q.a<4,'Valid choices');
      const wrong=(q.a+1)%4;
      d.querySelector(`[data-original="${wrong}"]`).click();
      d.querySelector(`[data-original="${q.a}"]`).click();
      ok(t.getState().assessment[system].firstCorrect===0,'Retry never counts as first attempt');
      d.querySelector('[data-next-question]').click();
    }
    const a=t.getState().assessment[system];ok(a.finished&&a.masteryCorrect===total&&a.firstCorrect===0,'Correct completion state');
    ok(t.validateAssessmentData(a,total),'Completed assessment reloads');
    if(system!=='setup'){
      t.selectMode('explore');
      for(const [id,x] of Object.entries(t.STRUCTURES).filter(([,x])=>x.system===system)){
        const picker=d.querySelector('#structureSelect');picker.value=id;picker.dispatchEvent(new w.Event('change'));
        ok(d.querySelector('#detailCard h3').textContent===x[lang],'Picker opens matching detail '+id);
        ok(t.activeStructure(id),'External orientation resolves '+id);
        if(x.deep){
          d.querySelector('[data-deep]').click();ok(d.querySelector('#deepBody svg'),'Deep diagram '+x.deep);
          const openIds=[...d.querySelectorAll('[id]')].map(n=>n.id);ok(openIds.length===new Set(openIds).size,'Open microscopic panel has unique DOM IDs: '+x.deep);
          const refs=[...d.querySelectorAll('#deepBody svg *')].flatMap(n=>[...n.attributes].flatMap(a=>[...a.value.matchAll(/url\(#([^)]*)\)/g)].map(m=>m[1])));
          ok(refs.every(id=>d.getElementById(id)),'Microscopic SVG references resolve in full document: '+x.deep);
          d.querySelector('#deepDialog [data-close-dialog]').click();
        }
      }
    }
  }
}
// Malformed state must not mint a completion or choose Object.prototype properties.
for(const system of ['__proto__','constructor','toString','no-such-system'])ok(t.normalizeState({system,tool:system}).system==='setup','Prototype key rejected');
let bad=JSON.parse(JSON.stringify(t.getState()));bad.assessment.crosssection.finished=false;ok(!t.normalizeState(bad).assessment.crosssection,'Inconsistent completion rejected');
bad=JSON.parse(JSON.stringify(t.getState()));bad.assessment.crosssection.firstCorrect=100;ok(!t.normalizeState(bad).assessment.crosssection,'Impossible score rejected');
t.setState({system:'digestive',mode:'review',sound:false,voice:false,review:Object.fromEntries(Object.keys(t.STRUCTURES).map(id=>[id,{box:3,due:'2099-01-01',last:'2026-09-03'}]))});t.reviewView.optional=false;t.reviewView.id=null;t.reviewView.seen.clear();t.renderAll();
ok(d.querySelector('[data-review-restart]'),'No-due review shows optional action');d.querySelector('[data-review-restart]').click();ok(d.querySelector('[data-review-reveal]'),'Optional review includes future-due structures');
// Details must change language with the header.
t.selectMode('explore');const picker=d.querySelector('#structureSelect');picker.value='gizzard';picker.dispatchEvent(new w.Event('change'));d.querySelector('#langBtn').click();ok(d.querySelector('#detailCard h3').textContent===t.STRUCTURES.gizzard[t.getState().lang],'Selected detail translates on switch');
const snapshot=w.localStorage.getItem('earthwormDissection.v1'),restored=create(snapshot);ok(restored.window.__test.getState().system==='digestive','Progress restores after reload');restored.window.close();
const malformed=create('{broken');ok(malformed.window.__test.getState().system==='setup','Malformed JSON recovers');malformed.window.close();
const unavailable=create(null,true);ok(unavailable.window.document.querySelector('#storageWarning').classList.contains('show'),'Storage denied shows warning without crash');unavailable.window.close();
t.openProcedure();for(let i=0;i<6;i++)t.moveProcedure(1);ok(!d.querySelector('#procedurePanel').hidden,'Seven-step demonstration renders');t.closeProcedure();
ok(errors.length===0,'No DOM runtime errors: '+errors.join('\n'));
const report={date:new Date().toISOString().slice(0,10),htmlSHA256:require('node:crypto').createHash('sha256').update(fs.readFileSync(input)).digest('hex'),checks,systems:9,structures:55,guidedSteps:56,questions:72,microscopicExplanations:9,errors,testScope:'Data validation and DOM interaction simulation; no browser layout, touch, TTS, Android build or device test implied.'};
fs.writeFileSync('tests/test-results.json',JSON.stringify(report,null,2));console.log(JSON.stringify(report,null,2));w.close();
