// Bounded data parser, not an HTML renderer. Code spans and incomplete links stay text.
export function parseInlineLinks(text){
 const links=[];let i=0;
 while(i<text.length){
  if(text[i]==='`'){let n=1;while(text[i+n]==='`')n++;const close=text.indexOf('`'.repeat(n),i+n);i=close>=0?close+n:i+n;continue;}
  if(text[i]!=='['||(i>0&&text[i-1]==='\\')){i++;continue;}
  const labelEnd=text.indexOf('](',i+1);if(labelEnd<0)break;
  let j=labelEnd+2,depth=1;const begin=j;
  for(;j<text.length;j++){if(text[j]==='\\'){j++;continue;}if(text[j]==='(')depth++;else if(text[j]===')'&&--depth===0)break;}
  if(depth!==0)break;
  links.push({label:text.slice(i+1,labelEnd),target:text.slice(begin,j),start:i,end:j+1});i=j+1;
 }
 return links;
}
