/*
 * Isomorphic SmartClient
 * Version SC_SNAPSHOT-2011-08-02 (2011-08-02)
 * Copyright(c) 1998 and beyond Isomorphic Software, Inc. All rights reserved.
 * "SmartClient" is a trademark of Isomorphic Software, Inc.
 *
 * licensing@smartclient.com
 *
 * http://smartclient.com/license
 */

if(window.isc&&window.isc.module_Core&&!window.isc.module_SQLBrowser){isc.module_SQLBrowser=1;isc._moduleStart=isc._SQLBrowser_start=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc._moduleEnd&&(!isc.Log||(isc.Log && isc.Log.logIsDebugEnabled('loadTime')))){isc._pTM={ message:'SQLBrowser load/parse time: ' + (isc._moduleStart-isc._moduleEnd) + 'ms', category:'loadTime'};
if(isc.Log && isc.Log.logDebug)isc.Log.logDebug(isc._pTM.message,'loadTime')
else if(isc._preLog)isc._preLog[isc._preLog.length]=isc._pTM
else isc._preLog=[isc._pTM]}isc.definingFramework=true;isc.DataSource.create({
ID:"QuartzScheduler",
dbName:"ANY_DATA",
serverConstructor:"com.isomorphic.scheduler.QuartzScheduler",
fields:{
name:{
canEdit:false,
name:"name",
type:"text"
},
state:{
canEdit:false,
name:"state",
type:"intEnum",
valueMap:{
"0":"Shutdown",
"1":"Standby",
"2":"Started"
}
}
},
operationBinding:[
{
operationId:"start",
operationType:"custom"
},
{
operationId:"shutdown",
operationType:"custom"
},
{
operationId:"standby",
operationType:"custom"
},
{
operationId:"doit",
operationType:"custom",
serverObject:{
className:"com.isomorphic.scheduler.QuartzScheduler",
methodName:"doit"
}
}
]
})
isc.DataSource.create({
ID:"QuartzJobGroups",
dbName:"ANY_DATA",
serverConstructor:"com.isomorphic.scheduler.QuartzJobGroups",
fields:{
name:{
name:"name",
primaryKey:true,
type:"string"
}
}
})
isc.DataSource.create({
ID:"QuartzJobs",
dbName:"ANY_DATA",
serverConstructor:"com.isomorphic.scheduler.QuartzJobs",
fields:{
group:{
name:"group",
primaryKey:true,
required:true,
type:"string"
},
name:{
name:"name",
primaryKey:true,
required:true,
type:"string"
},
description:{
name:"description",
type:"string"
},
"class":{
name:"class",
required:true,
type:"string"
},
dataMap:{
name:"dataMap",
type:"string"
},
state:{
canEdit:false,
name:"state",
type:"intEnum",
valueMap:{
"0":"Normal",
"1":"Paused",
"2":"Complete",
"3":"Error",
"4":"Blocked",
"-1":"None"
}
},
startTime:{
name:"startTime",
type:"datetime"
},
endTime:{
name:"endTime",
type:"datetime"
},
cronExpression:{
name:"cronExpression",
type:"text"
},
timeZone:{
name:"timeZone",
type:"text"
}
}
})
isc.DataSource.create({
ID:"QuartzJobDataMap",
dbName:"ANY_DATA",
serverConstructor:"com.isomorphic.scheduler.QuartzJobDataMap",
fields:{
group:{
name:"group",
primaryKey:true,
required:true,
type:"string"
},
name:{
name:"name",
primaryKey:true,
required:true,
type:"string"
},
key:{
name:"key",
required:true,
type:"string"
},
value:{
name:"value",
required:true,
type:"string"
}
}
})
isc.defineClass("QuartzJobDetailPane","VLayout");isc.A=isc.QuartzJobDetailPane.getPrototype();isc.B=isc._allFuncs;isc.C=isc.B._maxIndex;isc.D=isc._funcClasses;isc.D[isc.C]=isc.A.Class;isc.A.headerDefaults={_constructor:"ToolStrip",width:"100%",height:33,titleDefaults:{_constructor:"Label",contents:"&nbsp;<b>Job Detail</b>"},members:["autoChild:title"]};isc.A.editFormProperties={_constructor:"DynamicForm",autoDraw:false,numCols:4,colWidths:[100,100,100,"*"],dataSource:"QuartzJobs",autoFocus:true,fields:[{name:"group",width:300,colSpan:3,tabIndex:10},{name:"name",width:300,colSpan:3,tabIndex:20},{name:"description",width:300,colSpan:3,tabIndex:30},{name:"class",width:300,tabIndex:40,colSpan:3},{name:"startTime",tabIndex:200},{name:"endTime",tabIndex:210},{name:"cronExpression",width:300,colSpan:3,tabIndex:220},{name:"timeZone",width:300,tabIndex:230,colSpan:3,endRow:true},{type:"rowSpacer"},{name:"btnApply",type:"button",title:"Apply",width:75,startRow:false,endRow:false,icon:"[SKIN]actions/save.png",click:"form.save()"},{name:"btnCancel",type:"button",title:"Cancel",width:75,startRow:false,endRow:false,icon:"[SKIN]actions/undo.png",click:"form.reset()"}],save:function(){this.saveData(function(_1,_2,_3){this.editRecord(_2);this.getField("group").setDisabled(true);this.getField("name").setDisabled(true)})}};isc.A.members=["autoChild:header","autoChild:editForm"];isc.B.push(isc.A.editNew=function isc_QuartzJobDetailPane_editNew(){this.editForm.editNewRecord();this.editForm.getField("group").setDisabled(false);this.editForm.getField("name").setDisabled(false)}
,isc.A.edit=function isc_QuartzJobDetailPane_edit(_1){this.editForm.editRecord(_1);this.editForm.getField("group").setDisabled(true);this.editForm.getField("name").setDisabled(true)}
);isc.B._maxIndex=isc.C+2;isc.defineClass("QuartzManager","VLayout");isc.A=isc.QuartzManager.getPrototype();isc.A.headerDefaults={_constructor:"ToolStrip",width:"100%",height:33,titleDefaults:{_constructor:"Label",contents:"&nbsp;<b>Jobs</b>"},refreshBtnDefaults:{_constructor:"ToolStripButton",showRollOver:false,icon:"[SKIN]actions/refresh.png",prompt:"Refresh jobs",click:"this.creator.creator.jobGrid.refresh()"},addBtnDefaults:{_constructor:"ToolStripButton",showRollOver:false,icon:"[SKIN]actions/add.png",prompt:"Add job",click:"this.creator.creator.jobEdit.editNew()"},removeBtnDefaults:{_constructor:"ToolStripButton",showRollOver:false,icon:"[SKIN]actions/remove.png",prompt:"Remove job",click:"this.creator.creator.jobGrid.removeSelectedData()"},members:["autoChild:title","starSpacer","autoChild:refreshBtn","autoChild:addBtn","autoChild:removeBtn"]};isc.A.jobGridDefaults={_constructor:"ListGrid",autoDraw:false,width:"100%",height:300,dataSource:"QuartzJobs",useAllDataSourceFields:true,autoFetchData:true,selectionType:"single",recordClick:"this.creator.jobEdit.edit(record)",refresh:function(){this.invalidateCache();this.fetchData()},add:function(){this.creator.jobEdit.editNew()},remove:function(){}};isc.A.jobDetailHeaderDefaults={_constructor:"ToolStrip",width:"100%",height:33,titleDefaults:{_constructor:"Label",contents:"&nbsp;<b>Job Detail</b>"},members:["autoChild:title"]};isc.A.jobEditDefaults={_constructor:"QuartzJobDetailPane",autoDraw:false};isc.A.members=["autoChild:header","autoChild:jobGrid","autoChild:jobEdit"];isc._moduleEnd=isc._SQLBrowser_end=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc.Log&&isc.Log.logIsInfoEnabled('loadTime'))isc.Log.logInfo('SQLBrowser module init time: ' + (isc._moduleEnd-isc._moduleStart) + 'ms','loadTime');delete isc.definingFramework;}else{if(window.isc && isc.Log && isc.Log.logWarn)isc.Log.logWarn("Duplicate load of module 'SQLBrowser'.");}
/*
 * Isomorphic SmartClient
 * Version SC_SNAPSHOT-2011-08-02 (2011-08-02)
 * Copyright(c) 1998 and beyond Isomorphic Software, Inc. All rights reserved.
 * "SmartClient" is a trademark of Isomorphic Software, Inc.
 *
 * licensing@smartclient.com
 *
 * http://smartclient.com/license
 */

