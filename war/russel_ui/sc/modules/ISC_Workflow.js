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

if(window.isc&&window.isc.module_Core&&!window.isc.module_Workflow){isc.module_Workflow=1;isc._moduleStart=isc._Workflow_start=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc._moduleEnd&&(!isc.Log||(isc.Log && isc.Log.logIsDebugEnabled('loadTime')))){isc._pTM={ message:'Workflow load/parse time: ' + (isc._moduleStart-isc._moduleEnd) + 'ms', category:'loadTime'};
if(isc.Log && isc.Log.logDebug)isc.Log.logDebug(isc._pTM.message,'loadTime')
else if(isc._preLog)isc._preLog[isc._preLog.length]=isc._pTM
else isc._preLog=[isc._pTM]}isc.definingFramework=true;isc.defineClass("ProcessElement");isc.ProcessElement.addProperties({})
isc.defineClass("ProcessSequence","ProcessElement");isc.ProcessSequence.addProperties({})
isc.defineClass("Task","ProcessElement");isc.Task.addProperties({})
isc.defineClass("Process","Task");isc.A=isc.Process.getPrototype();isc.B=isc._allFuncs;isc.C=isc.B._maxIndex;isc.D=isc._funcClasses;isc.D[isc.C]=isc.A.Class;isc.A.autoStart=false;isc.B.push(isc.A.init=function isc_Process_init(){var _1=this.Super("init",arguments);if(this.autoStart){this.start()}
return _1}
,isc.A.getElement=function isc_Process_getElement(_1){return this.searchElement(this,_1)}
,isc.A.searchElement=function isc_Process_searchElement(_1,_2){if(_1.sequences!=null){for(var i=0;i<_1.sequences.length;i++){var s=_1.sequences[i];if(s.ID==_2){return s}else if(s.sequences!=null||s.elements!=null){var _5=this.searchElement(s,_2);if(_5!=null){return _5}}}}
if(_1.elements!=null){for(var i=0;i<_1.elements.length;i++){var e=_1.elements[i];if(e.ID==_2){return e}else if(e.sequences!=null||e.elements!=null){var _5=this.searchElement(e,_2);if(_5!=null){return _5}}}}}
,isc.A.start=function isc_Process_start(){if(this.executionStack==null){this.executionStack=[]}
while(this.next()!=null){var _1=this.getFirstTask();if(_1==null){continue}
if(_1.getClassName()=="ScriptTask"){if(!this.executeScriptTaskElement(_1)){return}}else if(_1.getClassName()=="ServiceTask"){this.executeServiceTaskElement(_1)
return}}
this.finished()}
,isc.A.finished=function isc_Process_finished(){}
,isc.A.next=function isc_Process_next(){var _1=this.executionStack.last();if(_1==null){if(this.startElement!=null){return this.gotoElement(this,this.startElement)}else if(this.sequences!=null&&this.sequences.length>0){this.executionStack.add({el:this,sIndex:0});return this.sequences[0]}else if(this.elements!=null&&this.elements.length>0){this.executionStack.add({el:this,eIndex:0});return this.elements[0]}else{isc.logWarn("There are neither sequences or elements. Nothing to execute.")}}else{var _2=null;if(_1.sIndex!=null){_2=_1.el.sequences[_1.sIndex]}else if(_1.eIndex!=null){_2=_1.el.elements[_1.eIndex]}
if(_2.nextElement!=null){this.executionStack=[];var _3=this.gotoElement(this,_2.nextElement);return _3}else{return this.findNextElement()}}}
,isc.A.gotoElement=function isc_Process_gotoElement(_1,_2){var _3={el:_1};this.executionStack.add(_3);if(_1.sequences!=null){for(var i=0;i<_1.sequences.length;i++){var s=_1.sequences[i];_3.sIndex=i;if(s.ID==_2){return s}else if(s.sequences!=null||s.elements!=null){var _6=this.gotoElement(s,_2);if(_6!=null){return _6}}}}
delete _3.sIndex;if(_1.elements!=null){for(var i=0;i<_1.elements.length;i++){var e=_1.elements[i];_3.eIndex=i;if(e.ID==_2){return e}else if(e.sequences!=null||e.elements!=null){var _6=this.gotoElement(e,_2);if(_6!=null){return _6}}}}
this.executionStack.removeAt(this.executionStack.length-1)}
,isc.A.findNextElement=function isc_Process_findNextElement(){var _1=this.executionStack.last();if(_1.sIndex!=null){if(_1.sIndex==_1.el.sequences.length-1){this.executionStack.removeAt(this.executionStack.length-1);if(_1.el==this){return}else{return this.findNextElement()}}else{_1.sIndex++;return _1.el.sequences[_1.sIndex]}}
if(_1.eIndex!=null){if(_1.eIndex==_1.el.elements.length-1){this.executionStack.removeAt(this.executionStack.length-1);if(_1.el==this){return}else{return this.findNextElement()}}else{_1.eIndex++;return _1.el.elements[_1.eIndex]}}}
,isc.A.getFirstTask=function isc_Process_getFirstTask(){var _1=this.executionStack.last();var _2=null;if(_1.sIndex!=null){_2=_1.el.sequences[_1.sIndex]}else if(_1.eIndex!=null){_2=_1.el.elements[_1.eIndex]}
if(_2.sequences==null&&_2.elements==null){return _2}
var _3={el:_2};this.executionStack.add(_3);if(_2.sequences!=null){for(var i=0;i<_2.sequences.length;i++){_3.sIndex=i
var _5=this.getFirstTask(_2.sequences[i]);if(_5!=null){return _5}}}
if(_2.elements!=null){for(var i=0;i<_2.elements.length;i++){_3.eIndex=i
var _5=this.getFirstTask(_2.elements[i]);if(_5!=null){return _5}}}
this.executionStack.removeAt(this.executionStack.length-1)}
,isc.A.executeScriptTaskElement=function isc_Process_executeScriptTaskElement(_1){var _2;var _3;if(_1.inputFieldList!=null){_3={};for(var i=0;i<_1.inputFieldList.length;i++){_3[_1.inputFieldList[i]]=this.state[_1.inputFieldList[i]]}}
if(_1.inputField!=null){_2=this.state[_1.inputField];if(_3!=null){_3[_1.inputField]=_2}}
_1.inputData=_2;_1.inputRecord=_3;_1.process=this;try{var _5=_1.execute(_2,_3)}catch(e){isc.logWarn("Error while executing ScriptTask: "+e.toString())}
if(_1.isAsync){return false}
if(typeof _5=='undefined'){return true}
this.processTaskOutput(_1,_5);return true}
,isc.A.processTaskOutput=function isc_Process_processTaskOutput(_1,_2){if(_1.outputFieldList!=null){for(var i=0;i<_1.outputFieldList.length;i++){if(typeof _2[_1.outputFieldList[i]]!='undefined'){this.state[_1.outputFieldList[i]]=_2[_1.outputFieldList[i]]}}}
if(_1.outputField!=null){if(_1.outputFieldList==null){if(typeof _2!='undefined'){this.state[_1.outputField]=_2}}else{if(typeof _2[_1.outputField]!='undefined'){this.state[_1.outputField]=_2[_1.outputField]}}}}
,isc.A.finishTask=function isc_Process_finishTask(_1,_2,_3){if(_2==null){this.processTaskOutput(_1,_3)}else{if(_3!=null){_2[_1.outputField]=_3}
this.processTaskOutput(_1,_2)}
if(_1.isAsync){this.start()}}
,isc.A.executeServiceTaskElement=function isc_Process_executeServiceTaskElement(_1){var _2=_1.dataSource;if(_2.getClassName==null||_2.getClassName()!="DataSource"){_2=isc.DataSource.get(_2)}
var _3={};if(_1.inputFieldList!=null){for(var i=0;i<_1.inputFieldList.length;i++){_3[_1.inputFieldList[i]]=this.state[_1.inputFieldList[i]]}}
if(_1.inputField!=null){_3[_1.inputField]=this.state[_1.inputField]}
var _5=null;if(_1.operationType=="fetch"){if(_1.criteria!=null){_5=_1.criteria;this.processCriteriaExpressions(_5,_1,_3)}
if(_1.fixedCriteria!=null){if(_5==null){_5=_1.fixedCriteria}else{_5=isc.DataSource.combineCriteria(_5,_1.fixedCriteria)}}}
if(_5==null){_5=_3}
var _6=this;_2.performDSOperation(_1.operationType,_5,function(_7){if(_7.data.length==1){if(_1.outputFieldList!=null){for(var i=0;i<_1.outputFieldList.length;i++){if(typeof _7.data[0][_1.outputFieldList[i]]!='undefined'){_6.state[_1.outputFieldList[i]]=_7.data[0][_1.outputFieldList[i]]}}}
if(_1.outputField!=null){_6.state[_1.outputField]=_7.data[0][_1.outputField]}}
_6.start()})}
,isc.A.processCriteriaExpressions=function isc_Process_processCriteriaExpressions(_1,_2,_3){for(var _4 in _1){if(_4=="criteria"){this.processCriteriaExpressions(_1.criteria)}else if(_1[_4].startsWith("$input")){var _5="state."+_1[_4].replace("$input",_2.inputField);_1[_4]=isc.Class.evaluate(_5,{state:_3})}else if(_1[_4].startsWith("$inputRecord")){var _5=_1[_4].replace("$inputRecord","state");_1[_4]=isc.Class.evaluate(_5,{state:_3})}}}
);isc.B._maxIndex=isc.C+14;isc.defineClass("ServiceTask","Task");isc.A=isc.ServiceTask.getPrototype();isc.A.operationType="fetch";isc.defineClass("ScriptTask","Task");isc.A=isc.ScriptTask.getPrototype();isc.B=isc._allFuncs;isc.C=isc.B._maxIndex;isc.D=isc._funcClasses;isc.D[isc.C]=isc.A.Class;isc.A.isAsync=false;isc.B.push(isc.A.getInputData=function isc_ScriptTask_getInputData(){return this.inputData}
,isc.A.setOutputData=function isc_ScriptTask_setOutputData(_1){this.process.finishTask(this,null,_1)}
,isc.A.getInputRecord=function isc_ScriptTask_getInputRecord(){return this.inputRecord}
,isc.A.setOutputRecord=function isc_ScriptTask_setOutputRecord(_1){this.process.finishTask(this,_1)}
,isc.A.execute=function isc_ScriptTask_execute(_1,_2){}
);isc.B._maxIndex=isc.C+5;isc.defineClass("XORGateway","ProcessElement");isc.XORGateway.addProperties({})
isc.defineClass("DecisionGateway","Task");isc.DecisionGateway.addProperties({})
isc._moduleEnd=isc._Workflow_end=(isc.timestamp?isc.timestamp():new Date().getTime());if(isc.Log&&isc.Log.logIsInfoEnabled('loadTime'))isc.Log.logInfo('Workflow module init time: ' + (isc._moduleEnd-isc._moduleStart) + 'ms','loadTime');delete isc.definingFramework;}else{if(window.isc && isc.Log && isc.Log.logWarn)isc.Log.logWarn("Duplicate load of module 'Workflow'.");}
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

