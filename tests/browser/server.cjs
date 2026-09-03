// CI-only fixture server. The application response is the exact bundled HTML.
// Never use this server to bypass a browser/environment access restriction.
const http=require('node:http'),fs=require('node:fs'),path=require('node:path');
const app=fs.readFileSync(path.join(__dirname,'../../app/src/main/assets/index.html'));
const fixture=fs.readFileSync(path.join(__dirname,'renderer-fixture.html'));
const server=http.createServer((req,res)=>{
  const routes={'/':app,'/index.html':app,'/renderer-fixture.html':fixture,'/health':Buffer.from('ready')};
  const body=Object.hasOwn(routes,req.url)?routes[req.url]:null;
  if(!body||!['GET','HEAD'].includes(req.method)){res.writeHead(404);res.end();return;}
  res.writeHead(200,{'Content-Type':req.url==='/health'?'text/plain; charset=utf-8':'text/html; charset=utf-8','Cache-Control':'no-store','X-Content-Type-Options':'nosniff'});
  res.end(req.method==='HEAD'?undefined:body);
});
server.listen(4173,'127.0.0.1');
for(const signal of ['SIGINT','SIGTERM'])process.on(signal,()=>server.close(()=>process.exit(0)));
