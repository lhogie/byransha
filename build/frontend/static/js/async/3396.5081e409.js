"use strict";(self.webpackChunklogin_page=self.webpackChunklogin_page||[]).push([["3396"],{43310:function(e,t,a){function i(e,t){e.accDescr&&t.setAccDescription?.(e.accDescr),e.accTitle&&t.setAccTitle?.(e.accTitle),e.title&&t.setDiagramTitle?.(e.title)}a.d(t,{A:()=>i}),(0,a(134).eW)(i,"populateCommonDb")},75304:function(e,t,a){a.d(t,{diagram:()=>W});var i=a(43310),l=a(19085),r=a(27395),n=a(134),s=a(3194),o=a(92548),c=n.vZ.pie,p={sections:new Map,showData:!1,config:c},d=p.sections,g=p.showData,u=structuredClone(c),f=(0,n.eW)(()=>structuredClone(u),"getConfig"),h=(0,n.eW)(()=>{d=new Map,g=p.showData,(0,n.ZH)()},"clear"),x=(0,n.eW)(({label:e,value:t})=>{d.has(e)||(d.set(e,t),n.cM.debug(`added new section: ${e}, with value: ${t}`))},"addSection"),m=(0,n.eW)(()=>d,"getSections"),w=(0,n.eW)(e=>{g=e},"setShowData"),S=(0,n.eW)(()=>g,"getShowData"),T={getConfig:f,clear:h,setDiagramTitle:n.g2,getDiagramTitle:n.Kr,setAccTitle:n.GN,getAccTitle:n.eu,setAccDescription:n.U$,getAccDescription:n.Mx,addSection:x,getSections:m,setShowData:w,getShowData:S},$=(0,n.eW)((e,t)=>{(0,i.A)(e,t),t.setShowData(e.showData),e.sections.map(t.addSection)},"populateDb"),y={parse:(0,n.eW)(async e=>{let t=await (0,s.Qc)("pie",e);n.cM.debug(t),$(t,T)},"parse")},D=(0,n.eW)(e=>`
  .pieCircle{
    stroke: ${e.pieStrokeColor};
    stroke-width : ${e.pieStrokeWidth};
    opacity : ${e.pieOpacity};
  }
  .pieOuterCircle{
    stroke: ${e.pieOuterStrokeColor};
    stroke-width: ${e.pieOuterStrokeWidth};
    fill: none;
  }
  .pieTitleText {
    text-anchor: middle;
    font-size: ${e.pieTitleTextSize};
    fill: ${e.pieTitleTextColor};
    font-family: ${e.fontFamily};
  }
  .slice {
    font-family: ${e.fontFamily};
    fill: ${e.pieSectionTextColor};
    font-size:${e.pieSectionTextSize};
    // fill: white;
  }
  .legend text {
    fill: ${e.pieLegendTextColor};
    font-family: ${e.fontFamily};
    font-size: ${e.pieLegendTextSize};
  }
`,"getStyles"),C=(0,n.eW)(e=>{let t=[...e.entries()].map(e=>({label:e[0],value:e[1]})).sort((e,t)=>t.value-e.value);return(0,o.ve8)().value(e=>e.value)(t)},"createPieArcs"),W={parser:y,db:T,renderer:{draw:(0,n.eW)((e,t,a,i)=>{n.cM.debug("rendering pie chart\n"+e);let s=i.db,c=(0,n.nV)(),p=(0,l.Rb)(s.getConfig(),c.pie),d=(0,r.P)(t),g=d.append("g");g.attr("transform","translate(225,225)");let{themeVariables:u}=c,[f]=(0,l.VG)(u.pieOuterStrokeWidth);f??=2;let h=p.textPosition,x=(0,o.Nb1)().innerRadius(0).outerRadius(185),m=(0,o.Nb1)().innerRadius(185*h).outerRadius(185*h);g.append("circle").attr("cx",0).attr("cy",0).attr("r",185+f/2).attr("class","pieOuterCircle");let w=s.getSections(),S=C(w),T=[u.pie1,u.pie2,u.pie3,u.pie4,u.pie5,u.pie6,u.pie7,u.pie8,u.pie9,u.pie10,u.pie11,u.pie12],$=(0,o.PKp)(T);g.selectAll("mySlices").data(S).enter().append("path").attr("d",x).attr("fill",e=>$(e.data.label)).attr("class","pieCircle");let y=0;w.forEach(e=>{y+=e}),g.selectAll("mySlices").data(S).enter().append("text").text(e=>(e.data.value/y*100).toFixed(0)+"%").attr("transform",e=>"translate("+m.centroid(e)+")").style("text-anchor","middle").attr("class","slice"),g.append("text").text(s.getDiagramTitle()).attr("x",0).attr("y",-200).attr("class","pieTitleText");let D=g.selectAll(".legend").data($.domain()).enter().append("g").attr("class","legend").attr("transform",(e,t)=>"translate(216,"+(22*t-22*$.domain().length/2)+")");D.append("rect").attr("width",18).attr("height",18).style("fill",$).style("stroke",$),D.data(S).append("text").attr("x",22).attr("y",14).text(e=>{let{label:t,value:a}=e.data;return s.getShowData()?`${t} [${a}]`:t});let W=512+Math.max(...D.selectAll("text").nodes().map(e=>e?.getBoundingClientRect().width??0));d.attr("viewBox",`0 0 ${W} 450`),(0,n.v2)(d,450,W,p.useMaxWidth)},"draw")},styles:D}}}]);
//# sourceMappingURL=3396.5081e409.js.map