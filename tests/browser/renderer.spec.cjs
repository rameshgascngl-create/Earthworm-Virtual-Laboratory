// This gate diagnoses renderer capability. A failure is not an anatomy defect.
const {test,expect}=require('@playwright/test'),{PNG}=require('pngjs');
test('isolated transparency, visibility, gradients, paint order and filter rendering',async({page},info)=>{
  await page.setViewportSize({width:820,height:260});
  await page.goto('/renderer-fixture.html');
  const buffer=await page.screenshot({fullPage:true});await info.attach('isolated-renderer-fixture',{body:buffer,contentType:'image/png'});
  const png=PNG.sync.read(buffer),pixel=(x,y)=>[...png.data.subarray((y*png.width+x)*4,(y*png.width+x)*4+3)];
  for(const [x,y] of [[30,30],[110,30],[160,30],[315,45],[520,30]])expect(pixel(x,y)).toEqual([34,204,68]);
  expect(pixel(200,35)[0]).toBeGreaterThan(pixel(200,35)[2]);
  expect(pixel(260,35)[2]).toBeGreaterThan(pixel(260,35)[0]);
  const samples=new Set();for(let y=20;y<65;y+=5)for(let x=420;x<465;x+=5)samples.add(pixel(x,y).join(','));
  expect(samples.size,'feTurbulence must produce visible, nonconstant output').toBeGreaterThan(8);
  const supported=await page.evaluate(()=>CSS.supports('backdrop-filter','blur(5px)'));
  expect(supported,'Unsupported backdrop-filter is a renderer limitation').toBe(true);
  const blurred=pixel(70,160);expect(Math.min(blurred[0],blurred[2])).toBeGreaterThan(35);
  await page.evaluate(()=>document.body.classList.add('procedure-active'));
  await expect(page.locator('.procedure-demo')).toBeVisible();
});
