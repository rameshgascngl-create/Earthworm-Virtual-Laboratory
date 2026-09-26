const fs=require('node:fs');
const path=require('node:path');
const crypto=require('node:crypto');
const {test,expect}=require('@playwright/test');

const source=fs.readFileSync(path.join(__dirname,'../../app/src/main/assets/index.html'));
const digest=b=>crypto.createHash('sha256').update(b).digest('hex');
const expectedSha='4921509f78c91f3f9c0a52bfccef6c22a52652e66c26a930b534d597679829ec';
const systems=[
  ['setup','Preparation','ஆயத்தம்'],
  ['external','External morphology','புற அமைப்பியல்'],
  ['digestive','Digestive','செரிமான மண்டலம்'],
  ['circulatory','Circulatory','இரத்த ஓட்ட மண்டலம்'],
  ['respiratory','Cutaneous respiration','தோல் வழிச் சுவாசம்'],
  ['excretory','Excretory','கழிவு நீக்க மண்டலம்'],
  ['reproductive','Reproductive','இனப்பெருக்க மண்டலம்'],
  ['nervous','Nervous','நரம்பு மண்டலம்'],
  ['crosssection','Transverse section','குறுக்குவெட்டுத் தோற்றம்']
];

async function boot(page,{lang='en',system='setup',width=360,height=800,instrument=false}={}){
  await page.setViewportSize({width,height});
  const errors=[],consoleItems=[],external=[];
  page.on('pageerror',e=>errors.push(String(e)));
  page.on('console',m=>consoleItems.push({type:m.type(),text:m.text()}));
  page.on('request',r=>{const u=r.url();if(!u.startsWith('http://127.0.0.1:4173/')&&!u.startsWith('data:'))external.push(u)});
  if(instrument){
    await page.addInitScript(()=>{
      const original=Storage.prototype.setItem;
      window.__qaStorageWrites=0;
      Storage.prototype.setItem=function(k,v){
        if(k==='earthwormDissection.v1') window.__qaStorageWrites++;
        return original.call(this,k,v);
      };
    });
  }
  await page.addInitScript(({lang,system})=>{
    localStorage.setItem('earthwormDissection.v1',JSON.stringify({
      lang,system,mode:system==='setup'?'guided':'explore',
      voice:false,sound:false,motion:false
    }));
  },{lang,system});
  const res=await page.goto('/');
  expect(digest(await res.body())).toBe(expectedSha);
  await page.evaluate(()=>document.fonts.ready);
  await expect.poll(()=>page.evaluate(()=>window.EarthwormApp?.version)).toBe('1.3.8');
  return {errors,consoleItems,external};
}

async function navGeometry(page){
  return page.evaluate(()=>{
    const strip=document.querySelector('#systemTabs');
    const root=document.documentElement;
    const sr=strip.getBoundingClientRect();
    return {
      strip:{left:sr.left,right:sr.right,width:sr.width,scrollWidth:strip.scrollWidth,clientWidth:strip.clientWidth,scrollLeft:strip.scrollLeft,overflowX:getComputedStyle(strip).overflowX},
      page:{scrollWidth:root.scrollWidth,clientWidth:root.clientWidth},
      visibleNavigators:[
        ['select',document.querySelector('#systemSelect')],
        ['tabs',strip]
      ].filter(([,el])=>{if(!el)return false;const s=getComputedStyle(el),r=el.getBoundingClientRect();return s.display!=='none'&&s.visibility!=='hidden'&&r.width>0&&r.height>0}).map(([name])=>name),
      rows:[...strip.querySelectorAll('[data-system]')].map(b=>{
        const r=b.getBoundingClientRect();return {id:b.dataset.system,left:r.left,right:r.right,top:r.top,bottom:r.bottom,text:b.textContent.trim(),pressed:b.getAttribute('aria-pressed')};
      })
    };
  });
}

test('mobile nav identity, reachability, no page overflow, and single visible navigator',async({page},info)=>{
  const obs=await boot(page,{width:360,height:800});
  await expect(page.locator('#systemTabs')).toBeVisible();
  await expect(page.locator('#systemSelect')).toBeHidden();
  await expect(page.locator('#systemTabs [data-system]')).toHaveCount(9);
  let g=await navGeometry(page);
  expect(g.visibleNavigators).toEqual(['tabs']);
  expect(['auto','scroll']).toContain(g.strip.overflowX);
  expect(g.strip.scrollWidth).toBeGreaterThan(g.strip.clientWidth);
  expect(g.page.scrollWidth).toBeLessThanOrEqual(g.page.clientWidth+2);
  const tops=new Set(g.rows.map(x=>Math.round(x.top)));
  expect(tops.size,'tabs must remain one horizontal row').toBe(1);

  for(const [id,en] of systems){
    const tab=page.locator('#systemTabs [data-system="'+id+'"]');
    await tab.click();
    await expect(tab).toHaveAttribute('aria-pressed','true');
    await expect(page.locator('#systemSelect')).toHaveValue(id);
    await expect(page.locator('.layer.active')).toHaveAttribute('id','layer-'+id);
    const r=await tab.evaluate(el=>{const a=el.getBoundingClientRect(),p=el.parentElement.getBoundingClientRect();return {left:a.left,right:a.right,pLeft:p.left,pRight:p.right,text:el.textContent.trim()}});
    expect(r.left).toBeGreaterThanOrEqual(r.pLeft-2);
    expect(r.right).toBeLessThanOrEqual(r.pRight+2);
    expect(r.text).toContain(en);
  }
  g=await navGeometry(page);
  expect(g.page.scrollWidth).toBeLessThanOrEqual(g.page.clientWidth+2);
  expect(obs.errors).toEqual([]);
  expect(obs.external).toEqual([]);
  await info.attach('mobile-nav-en-final',{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
});

test('Tamil all nine tabs, state preservation, and English-Tamil-English round trip',async({page},info)=>{
  const obs=await boot(page,{width:360,height:800,system:'reproductive'});
  await page.locator('#langBtn').click();
  await expect(page.locator('html')).toHaveAttribute('data-lang','ta');
  await expect(page.locator('#systemTabs [data-system="reproductive"]')).toHaveAttribute('aria-pressed','true');
  for(const [id,,ta] of systems){
    const tab=page.locator('#systemTabs [data-system="'+id+'"]');
    await expect(tab).toContainText(ta);
    const text=await tab.textContent();
    expect(/[A-Za-z]/.test(text.replace(/Metaphire|Pheretima/g,'')),'tab contains English letters: '+id).toBe(false);
    await tab.click();
    await expect(tab).toHaveAttribute('aria-pressed','true');
    const r=await tab.evaluate(el=>{const a=el.getBoundingClientRect(),p=el.parentElement.getBoundingClientRect();return {left:a.left,right:a.right,pLeft:p.left,pRight:p.right,height:a.height,width:a.width}});
    expect(r.left).toBeGreaterThanOrEqual(r.pLeft-2);
    expect(r.right).toBeLessThanOrEqual(r.pRight+2);
    expect(r.height).toBeGreaterThanOrEqual(44);
    expect(r.width).toBeGreaterThan(40);
  }
  await page.locator('#systemTabs [data-system="digestive"]').click();
  await page.locator('#langBtn').click();
  await expect(page.locator('html')).toHaveAttribute('data-lang','en');
  await expect(page.locator('#systemTabs [data-system="digestive"]')).toHaveAttribute('aria-pressed','true');
  await expect(page.locator('#systemTabs [data-system="digestive"]')).toContainText('Digestive');
  expect((await navGeometry(page)).page.scrollWidth).toBeLessThanOrEqual((await navGeometry(page)).page.clientWidth+2);
  expect(obs.errors).toEqual([]);
  await info.attach('mobile-nav-ta',{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
});

test('mobile landscape retains one-row tabs without page-wide horizontal overflow',async({page},info)=>{
  const obs=await boot(page,{width:640,height:360,system:'external'});
  await expect(page.locator('#systemSelect')).toBeHidden();
  await expect(page.locator('#systemTabs')).toBeVisible();
  const g=await navGeometry(page);
  expect(g.visibleNavigators).toEqual(['tabs']);
  expect(new Set(g.rows.map(x=>Math.round(x.top))).size).toBe(1);
  expect(g.page.scrollWidth).toBeLessThanOrEqual(g.page.clientWidth+2);
  await page.locator('#systemTabs [data-system="crosssection"]').click();
  const last=page.locator('#systemTabs [data-system="crosssection"]');
  const r=await last.evaluate(el=>{const a=el.getBoundingClientRect(),p=el.parentElement.getBoundingClientRect();return {left:a.left,right:a.right,pLeft:p.left,pRight:p.right}});
  expect(r.left).toBeGreaterThanOrEqual(r.pLeft-2);expect(r.right).toBeLessThanOrEqual(r.pRight+2);
  expect(obs.errors).toEqual([]);
  await info.attach('mobile-nav-landscape',{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
});

test('tablet-desktop intended navigator remains tabs and mobile select stays hidden',async({page})=>{
  await boot(page,{width:1024,height:768,system:'circulatory'});
  await expect(page.locator('#systemTabs')).toBeVisible();
  await expect(page.locator('#systemSelect')).toBeHidden();
  await expect(page.locator('#systemTabs [data-system="circulatory"]')).toHaveAttribute('aria-pressed','true');
});

test('20-cycle event-binding stress: one persistence write per switch, stable DOM, no console growth',async({page},info)=>{
  const obs=await boot(page,{width:360,height:800,instrument:true});
  await page.evaluate(()=>window.__qaStorageWrites=0);
  const beforeNodes=await page.evaluate(()=>document.querySelectorAll('*').length);
  const sequence=['external','digestive','nervous','crosssection','setup'];
  let taps=0;
  for(let cycle=0;cycle<20;cycle++){
    for(const id of sequence){
      await page.locator('#systemTabs [data-system="'+id+'"]').click();
      await expect(page.locator('#systemTabs [data-system="'+id+'"]')).toHaveAttribute('aria-pressed','true');
      taps++;
    }
  }
  await page.waitForTimeout(100);
  const writes=await page.evaluate(()=>window.__qaStorageWrites);
  const afterNodes=await page.evaluate(()=>document.querySelectorAll('*').length);
  expect(writes,'each system switch should persist exactly once').toBe(taps);
  expect(await page.locator('#systemTabs [data-system]').count()).toBe(9);
  expect(afterNodes).toBeLessThanOrEqual(beforeNodes+20);
  expect(obs.errors).toEqual([]);
  const badConsole=obs.consoleItems.filter(x=>['error','warning'].includes(x.type));
  expect(badConsole).toEqual([]);
  expect(obs.consoleItems.length,'routine switching should not generate console chatter').toBe(0);
  await info.attach('event-binding-stress',{body:Buffer.from(JSON.stringify({taps,writes,beforeNodes,afterNodes,consoleItems:obs.consoleItems},null,2)),contentType:'application/json'});
});

test('mobile navigation coexists with assessment, hotspot, full-resolution viewer and Back contract',async({page},info)=>{
  const obs=await boot(page,{width:360,height:800,system:'digestive'});
  await page.locator('#modeButtons [data-mode="assessment"]').click();
  await expect(page.locator('#modeButtons [data-mode="assessment"]')).toHaveAttribute('aria-pressed','true');
  expect(await page.evaluate(()=>window.EarthwormApp.back())).toBe(true);
  await expect(page.locator('#modeButtons [data-mode="guided"]')).toHaveAttribute('aria-pressed','true');

  await page.locator('#modeButtons [data-mode="explore"]').click();
  const gizzard=page.locator('#layer-digestive .svg-structure[data-structure="gizzard"],#layer-digestive .svg-structure[data-target="gizzard"]').first();
  await expect(gizzard).toBeVisible();
  await gizzard.click();
  await expect(page.locator('#structureSelect')).toHaveValue('gizzard');

  await page.locator('#hqAtlasOpenBtn').click();
  await expect(page.locator('#hqAtlasDialog')).toBeVisible();
  expect(await page.evaluate(()=>window.EarthwormApp.back())).toBe(true);
  await expect(page.locator('#hqAtlasDialog')).not.toBeVisible();

  await page.locator('#systemTabs [data-system="nervous"]').click();
  expect(await page.evaluate(()=>window.EarthwormApp.back())).toBe(true);
  await expect(page.locator('#systemTabs [data-system="setup"]')).toHaveAttribute('aria-pressed','true');
  expect(obs.errors).toEqual([]);
  await info.attach('mobile-nav-contracts',{body:await page.screenshot({fullPage:true}),contentType:'image/png'});
});
