// Deterministic teaching model. No device, OS, GPU or ML execution.
export function pipeline({cpu=3,gpu=8,hz=60,frames=20,pause=[50,150],policy='fifo'}={}){
 if(!['fifo','latest-ready'].includes(policy)||![cpu,gpu,hz,frames,...pause].every(Number.isFinite)||cpu<0||gpu<0||hz<=0||!Number.isInteger(frames)||frames<1||frames>1000||pause.length!==2||pause[0]<0||pause[1]<pause[0])throw Error('Invalid model parameters');
 const T=1000/hz,work=[];let ce=0,ge=0;
 for(let i=0;i<frames;i++){const release=i*T;ce=Math.max(release,ce)+cpu;ge=Math.max(ce,ge)+gpu;work.push({id:i,release,ready:ge});}
 const waiting=[],presented=[];let next=0,dropped=0;
 for(let tick=0;presented.length+dropped<frames;tick++){
  const now=tick*T;while(next<frames&&work[next].ready<=now)waiting.push(work[next++]);
  if(now>=pause[0]&&now<pause[1])continue;
  if(waiting.length){if(policy==='latest-ready'&&waiting.length>1){dropped+=waiting.length-1;waiting.splice(0,waiting.length-1);}const f=waiting.shift();presented.push({...f,present:now,age:now-f.release});}
 }
 const ages=presented.map(f=>f.age).sort((a,b)=>a-b),quantile=q=>ages[Math.max(0,Math.ceil(q*ages.length)-1)];
 return {policy,modelOnly:true,framesProduced:frames,framesDisplayed:presented.length,framesDropped:dropped,p50FrameAgeMs:quantile(.5),p95FrameAgeMs:quantile(.95),presented,assumptions:['Unlimited buffers and no producer backpressure','CPU/GPU fixed serial durations and separate resources','Artificial display pause; no scanout/input sensor/driver cost','Latest-ready deliberately discards older completed frames','No learned controller or hardware benchmark']};
}
export function amdahl(parallelFraction,speedup){if(parallelFraction<0||parallelFraction>1||speedup<1)throw Error('Invalid Amdahl parameters');return 1/((1-parallelFraction)+parallelFraction/speedup);}
if(process.argv[1]&&import.meta.url.endsWith('/'+process.argv[1].split('/').pop())){
 console.log(JSON.stringify({scope:'Synthetic illustration only; no performance claim',refreshBudgetMs:{hz60:1000/60,hz120:1000/120},amdahlExample:{fraction:.4,localSpeedup:2,totalSpeedup:amdahl(.4,2)},fifo:pipeline(),latestReady:pipeline({policy:'latest-ready'})},null,2));
}
