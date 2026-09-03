const {defineConfig}=require('@playwright/test');
module.exports=defineConfig({
  testDir:'tests/browser',testMatch:'**/*.spec.cjs',timeout:45000,
  expect:{timeout:8000},fullyParallel:false,workers:1,retries:0,forbidOnly:!!process.env.CI,
  outputDir:'test-results/browser',
  reporter:[['list'],['html',{outputFolder:'playwright-report',open:'never'}],['json',{outputFile:'tests/browser-results.json'}]],
  use:{baseURL:'http://127.0.0.1:4173',browserName:'chromium',deviceScaleFactor:1,
    reducedMotion:'reduce',trace:'retain-on-failure',screenshot:'only-on-failure',serviceWorkers:'block'},
  projects:[
    {name:'renderer-capabilities',testMatch:'**/renderer.spec.cjs',use:{viewport:{width:820,height:260}}},
    {name:'desktop',testMatch:'**/app.spec.cjs',dependencies:['renderer-capabilities'],use:{viewport:{width:1366,height:900}}},
    {name:'phone',testMatch:'**/app.spec.cjs',dependencies:['renderer-capabilities'],use:{viewport:{width:390,height:844},isMobile:true,hasTouch:true}},
    {name:'small-phone',testMatch:'**/app.spec.cjs',dependencies:['renderer-capabilities'],use:{viewport:{width:360,height:800},isMobile:true,hasTouch:true}}
  ],
  webServer:{command:'node tests/browser/server.cjs',url:'http://127.0.0.1:4173/health',reuseExistingServer:false,timeout:15000}
});
