// Bounds retained line bytes before JSON parsing, including newline-free input.
export async function* boundedLines(input,maxBytes=1024*1024){
 let chunks=[],bytes=0,discard=false;
 for await(const raw of input){const buffer=Buffer.isBuffer(raw)?raw:Buffer.from(raw);let start=0;
  while(start<buffer.length){const nl=buffer.indexOf(10,start),end=nl<0?buffer.length:nl,part=buffer.subarray(start,end);
   if(!discard){if(bytes+part.length>maxBytes){chunks=[];bytes=0;discard=true;yield {oversized:true};}else{chunks.push(part);bytes+=part.length;}}
   if(nl>=0){if(!discard)yield {line:Buffer.concat(chunks,bytes).toString('utf8')};chunks=[];bytes=0;discard=false;start=nl+1;}else break;
  }
 }
 if(bytes&&!discard)yield {line:Buffer.concat(chunks,bytes).toString('utf8')};
}
