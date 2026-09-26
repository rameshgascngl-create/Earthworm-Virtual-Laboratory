const {defineConfig}=require('@playwright/test');
module.exports=defineConfig({
  testDir:'tests/browser',
  testMatch:'**/app.spec.cjs',
  timeout:45000,
  expect:{timeout:8000},
  fullyParallel:false,
  workers:1,
  retries:0,
  outputDir:'test-results/tablet',
  reporter:[['list'],['html',{outputFolder:'playwright-tablet-report',open:'never'}]],
  use:{
    baseURL:'http://127.0.0.1:4173',
    browserName:'chromium',
    deviceScaleFactor:1,
    reducedMotion:'reduce',
    trace:'retain-on-failure',
    screenshot:'only-on-failure',
    serviceWorkers:'block',
    viewport:{width:800,height:1280},
    isMobile:true,
    hasTouch:true
  },
  webServer:{command:'node tests/browser/server.cjs',url:'http://127.0.0.1:4173/health',reuseExistingServer:false,timeout:15000}
});
