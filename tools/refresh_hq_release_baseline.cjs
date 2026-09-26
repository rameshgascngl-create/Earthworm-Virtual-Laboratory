const fs=require('node:fs');
const crypto=require('node:crypto');
const assert=require('node:assert/strict');
const {JSDOM,VirtualConsole}=require('jsdom');

const htmlPath=process.argv[2]||'app/src/main/assets/index.html';
const baselinePath=process.argv[3]||'tests/release-baseline.json';
const html=fs.readFileSync(htmlPath,'utf8');
const baseline=JSON.parse(fs.readFileSync(baselinePath,'utf8'));
const sha=x=>crypto.createHash('sha256').update(x).digest('hex');

const anchor='    initSegments();bind();';
assert.equal(html.split(anchor).length,2,'authoritative initialization anchor must remain unique');
const code=html.replace(anchor,'window.__hqReviewData={SYSTEMS,STRUCTURES,MODULES,QUESTIONS,DEEP_DIVES,PROCEDURE_STEPS,TA_INTROS};'+anchor);

const vc=new VirtualConsole();
const runtimeErrors=[];
vc.on('jsdomError',e=>runtimeErrors.push(String(e)));
vc.on('error',e=>runtimeErrors.push(String(e)));

const dom=new JSDOM(code,{
  url:'https://appassets.androidplatform.net/assets/index.html',
  runScripts:'dangerously',
  virtualConsole:vc,
  beforeParse(w){
    w.matchMedia=()=>({matches:false,addEventListener(){},removeEventListener(){}});
    w.HTMLElement.prototype.scrollIntoView=()=>{};
    w.requestAnimationFrame=cb=>setTimeout(()=>cb(Date.now()),0);
    w.cancelAnimationFrame=id=>clearTimeout(id);
    if(w.HTMLDialogElement){
      w.HTMLDialogElement.prototype.showModal=function(){this.setAttribute('open','')};
      w.HTMLDialogElement.prototype.close=function(){this.removeAttribute('open')};
    }
  }
});
const data=dom.window.__hqReviewData;
assert.ok(data,'review data must be exposed');

const oldHashes={...baseline.dataHashes};
const actual={};
for(const key of Object.keys(oldHashes)) actual[key]=sha(JSON.stringify(data[key]));

for(const key of Object.keys(oldHashes)){
  if(key==='STRUCTURES') continue;
  assert.equal(actual[key],oldHashes[key],'unexpected educational-data change: '+key);
}
assert.notEqual(actual.STRUCTURES,oldHashes.STRUCTURES,'STRUCTURES must change for authorised species-specific correction');

const svgMatch=html.match(/<svg id="anatomySvg"[\s\S]*?<\/svg>/);
assert.ok(svgMatch,'anatomy SVG must be present');
const newSvgSha=sha(svgMatch[0]);
assert.notEqual(newSvgSha,baseline.renderingContext.svgSHA256,'SVG hash must change for authorised digestive label correction');

const oldStyleHashes=[...baseline.renderingContext.styleSHA256];
const staticStyles=[...html.matchAll(/<style\b[^>]*>([\s\S]*?)<\/style>/g)].map(m=>sha(m[1]));
assert.equal(staticStyles.length,oldStyleHashes.length,'stylesheet block count must remain unchanged');
assert.ok(html.includes('.hq-atlas-panel{'),'atlas CSS must be present in the reviewed static stylesheet');
assert.notDeepEqual(staticStyles,oldStyleHashes,'stylesheet hash must change for the authorised atlas layout addition');

assert.ok(html.includes('>Gizzard</text>'),'digestive SVG label must not bake the disputed gizzard segment number');
assert.ok(!html.includes('>Gizzard VIII†</text>'),'old gizzard VIII SVG label must be absent');
assert.ok(html.includes('>Intestinal caeca XXVII</text>'),'species-specific caecal SVG annotation must use XXVII');
assert.ok(!html.includes('>Intestinal caeca XXVI†</text>'),'old caecal XXVI SVG label must be absent');
assert.ok(html.includes('segments IX–X in identified Metaphire posthuma specimens'),'gizzard structure text must carry specimen-level IX–X evidence');
assert.ok(html.includes('arising at segment XXVII and extending anteriorly to about XXV'),'caecal structure text must carry specimen-level evidence');

baseline.previousScientificBaseline={
  dataSTRUCTURESSHA256:oldHashes.STRUCTURES,
  svgSHA256:baseline.renderingContext.svgSHA256,
  styleSHA256:oldStyleHashes
};
baseline.dataHashes.STRUCTURES=actual.STRUCTURES;
baseline.renderingContext.svgSHA256=newSvgSha;
baseline.renderingContext.styleSHA256=staticStyles;
baseline.renderingContext.version='1.3.8-hq-atlas-and-digestive-specimen-correction';
baseline.scientificCorrection={
  scope:'Metaphire posthuma digestive annotation only; geometry/hotspots unchanged',
  evidence:'Bantaowong et al. 2011, Tropical Natural History 11(1), p.58',
  changes:['gizzard annotation no longer bakes VIII','gizzard structure text uses specimen-level IX–X','intestinal caeca annotation uses XXVII','caeca described as paired simple smooth outgrowths extending anteriorly','all non-STRUCTURES reviewed data hashes unchanged','stylesheet block count unchanged; style hash updated only for reviewed HQ atlas layout CSS']
};

fs.writeFileSync(baselinePath,JSON.stringify(baseline,null,2)+'\n');
dom.window.close();
assert.equal(runtimeErrors.length,0,'JSDOM runtime errors: '+runtimeErrors.join('; '));
console.log(JSON.stringify({
  preservedData:Object.keys(oldHashes).filter(k=>k!=='STRUCTURES'),
  structures:{old:oldHashes.STRUCTURES,new:actual.STRUCTURES},
  svg:{old:baseline.previousScientificBaseline.svgSHA256,new:newSvgSha},
  staticStyles,
  runtimeErrors
},null,2));
