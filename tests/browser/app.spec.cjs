// Full-page rendering only: no SVG extraction, rewritten CSS, replaced fonts or
// manually reconstructed transforms. These tests remain UNRUN until real CI/device execution.
const fs=require('node:fs'),path=require('node:path'),crypto=require('node:crypto');
const {test,expect}=require('@playwright/test');
const source=fs.readFileSync(path.join(__dirname,'../../app/src/main/assets/index.html'));
const version=require('../../package.json').version;
const systems=['setup','external','digestive','circulatory','respiratory','excretory','reproductive','nervous','crosssection'];
const {microscopicTargets}=require('../release-baseline.json');
const digest=buffer=>crypto.createHash('sha256').update(buffer).digest('hex');
async function load(page,lang,system='setup'){
  const errors=[],requests=[];
  page.on('pageerror',e=>errors.push(e.message));
  page.on('request',r=>{if(!r.url().startsWith('http://127.0.0.1:4173/')&&!r.url().startsWith('data:'))requests.push(r.url())});
  await page.addInitScript(({lang,system})=>localStorage.setItem('earthwormDissection.v1',JSON.stringify({lang,system,mode:system==='setup'?'guided':'explore',voice:false,sound:false,motion:false})),{lang,system});
  const response=await page.goto('/');expect(digest(await response.body())).toBe(digest(source));
  await expect.poll(()=>page.evaluate(()=>window.EarthwormApp?.version)).toBe(version);
  await page.evaluate(()=>document.fonts.ready);
  if(lang==='ta')expect(await page.evaluate(()=>document.fonts.check('16px "Earthworm Tamil"','மண்புழு'))).toBe(true);
  return ()=>{expect(errors).toEqual([]);expect(requests,'No external startup resources').toEqual([])};
}
async function inspect(page,info,label){
  const result=await page.evaluate(()=>{
    const svg=document.querySelector('#anatomySvg'),box=svg.getBoundingClientRect();
    function visible(e){let opacity=1;for(let n=e;n&&n instanceof Element;n=n.parentElement){const s=getComputedStyle(n);if(s.display==='none'||s.visibility==='hidden')return false;opacity*=Number(s.opacity)}return opacity>=.1}
    const labels=[...svg.querySelectorAll('text')].filter(visible).map(n=>{const b=n.getBoundingClientRect();return {text:n.textContent,x:b.x,y:b.y,width:b.width,height:b.height}});
    const overflow=labels.filter(b=>b.x<box.left-2||b.y<box.top-2||b.x+b.width>box.right+2||b.y+b.height>box.bottom+2);
    const overlaps=[];
    for(let i=0;i<labels.length;i++)for(let j=i+1;j<labels.length;j++){const a=labels[i],b=labels[j];const x=Math.min(a.x+a.width,b.x+b.width)-Math.max(a.x,b.x),y=Math.min(a.y+a.height,b.y+b.height)-Math.max(a.y,b.y);if(x>2&&y>2)overlaps.push([a.text,b.text,x,y])}
    const hits=[...svg.querySelectorAll('.hit')].filter(visible).map(n=>{const s=getComputedStyle(n);return {fill:s.fill,stroke:s.stroke}});
    const invalidHits=hits.filter(s=>![s.fill,s.stroke].every(v=>v==='none'||v==='transparent'||/^rgba\([^)]*,\s*0\)$/.test(v)));
    return {overflow,overlaps,invalidHits,labels,activeLayers:[...svg.querySelectorAll('.layer')].filter(visible).map(n=>n.id),pageOverflow:document.documentElement.scrollWidth>innerWidth+2};
  });
  await info.attach(label+'-geometry',{body:Buffer.from(JSON.stringify(result,null,2)),contentType:'application/json'});
  await info.attach(label+'-page',{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
  expect(result.activeLayers).toHaveLength(1);expect(result.invalidHits).toEqual([]);expect(result.pageOverflow).toBe(false);
  expect(result.overflow,'Viewport overflow in full browser; inspect evidence').toEqual([]);
  // Record intersections for human review: rotated labels, outlines and intentional
  // content can make an axis-aligned rectangle an unreliable collision verdict.
}
for(const lang of ['en','ta']){
  for(const system of systems)test(`${lang}: ${system} full context`,async({page},info)=>{
    const done=await load(page,lang,system);await expect(page.locator('.layer.active')).toHaveAttribute('id','layer-'+system);
    await expect(page.locator('#procedureDemo')).toBeHidden();await inspect(page,info,lang+'-'+system);
    if(system==='external'){await page.locator('[data-external-view="ventral"]').click();await inspect(page,info,lang+'-external-ventral')}
    done();
  });
  test(`${lang}: every procedure stage and return to normal`,async({page},info)=>{
    const done=await load(page,lang);await page.locator('#procedureBtn').click();
    for(let i=0;i<7;i++){
      if(i)await page.locator('#procedureNext').click();
      await expect(page.locator('#procedureDemo')).toHaveAttribute('data-step',String(i));
      for(const [selector,stage] of [['.demo-scissors',3],['.demo-probe',4],['.demo-forceps',5]]){
        if(i===stage)await expect(page.locator(selector)).toBeVisible();else await expect(page.locator(selector)).toBeHidden();
      }
      await inspect(page,info,`${lang}-procedure-${i+1}`);
    }
    await expect(page.locator('.demo-open-label')).toHaveCSS('opacity','1');
    await page.locator('#procedureClose').click();await expect(page.locator('#procedureDemo')).toBeHidden();done();
  });
  test(`${lang}: language, selection, zoom, contrast and print state`,async({page},info)=>{
    const done=await load(page,lang,'digestive');await page.locator('#structureSelect').selectOption('gizzard');
    await expect(page.locator('#layer-digestive [data-structure="gizzard"]')).toHaveClass(/selected/);
    await page.locator('#langBtn').click();await expect(page.locator('html')).toHaveAttribute('data-lang',lang==='ta'?'en':'ta');
    await page.locator('#langBtn').click();await page.locator('#contrastBtn').click();await inspect(page,info,lang+'-contrast');
    await page.locator('#contrastBtn').click();
    const initial=await page.locator('#anatomySvg').getAttribute('style');await page.locator('#zoomIn').click();
    expect(await page.locator('#anatomySvg').getAttribute('style')).not.toBe(initial);
    await page.locator('#zoomReset').click();
    await page.evaluate(()=>{window.print=()=>window.dispatchEvent(new Event('afterprint'))});
    await page.locator('#printAtlasBtn').click();await expect(page.locator('body')).not.toHaveClass(/print-atlas/);
    await page.evaluate(()=>document.body.classList.add('print-atlas'));await page.emulateMedia({media:'print'});
    await info.attach(lang+'-print',{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
    await page.emulateMedia({media:'screen'});await page.evaluate(()=>document.body.classList.remove('print-atlas'));
    done();
  });
  test(`${lang}: all microscopic panels keep their SVG definitions`,async({page},info)=>{
    const done=await load(page,lang,'digestive');
    for(const target of microscopicTargets){
      if(await page.locator('#systemSelect').isVisible())await page.locator('#systemSelect').selectOption(target.system);
      else await page.locator(`[data-system="${target.system}"]`).click();
      await page.locator('#structureSelect').selectOption(target.id);await page.locator('#detailCard [data-deep]').click();
      await expect(page.locator('#deepDialog')).toBeVisible();
      const missing=await page.locator('#deepBody svg').evaluate(svg=>[...svg.querySelectorAll('*')].flatMap(n=>[...n.attributes].flatMap(a=>[...a.value.matchAll(/url\(#([^)]*)\)/g)].map(m=>m[1]))).filter(id=>!document.getElementById(id)));
      expect(missing).toEqual([]);
      await info.attach(lang+'-micro-'+target.deep,{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
      await page.locator('#deepDialog [data-close-dialog]').click();
    }
    done();
  });
}
