function russel_ui(){
  var $wnd_0 = window, $doc_0 = document, $stats = $wnd_0.__gwtStatsEvent?function(a){
    return $wnd_0.__gwtStatsEvent(a);
  }
  :null, $sessionId_0 = $wnd_0.__gwtStatsSessionId?$wnd_0.__gwtStatsSessionId:null, scriptsDone, loadDone, bodyDone, base = '', metaProps = {}, values = [], providers = [], answers = [], softPermutationId = 0, onLoadErrorFunc, propertyErrorFunc;
  $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'bootstrap', millis:(new Date).getTime(), type:'begin'});
  if (!$wnd_0.__gwt_stylesLoaded) {
    $wnd_0.__gwt_stylesLoaded = {};
  }
  if (!$wnd_0.__gwt_scriptsLoaded) {
    $wnd_0.__gwt_scriptsLoaded = {};
  }
  function isHostedMode(){
    var result = false;
    try {
      var query = $wnd_0.location.search;
      return (query.indexOf('gwt.codesvr=') != -1 || (query.indexOf('gwt.hosted=') != -1 || $wnd_0.external && $wnd_0.external.gwtOnLoad)) && query.indexOf('gwt.hybrid') == -1;
    }
     catch (e) {
    }
    isHostedMode = function(){
      return result;
    }
    ;
    return result;
  }

  function maybeStartModule(){
    if (scriptsDone && loadDone) {
      var iframe = $doc_0.getElementById('russel_ui');
      var frameWnd = iframe.contentWindow;
      if (isHostedMode()) {
        frameWnd.__gwt_getProperty = function(name_0){
          return computePropValue(name_0);
        }
        ;
      }
      russel_ui = null;
      frameWnd.gwtOnLoad(onLoadErrorFunc, 'russel_ui', base, softPermutationId);
      $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'moduleStartup', millis:(new Date).getTime(), type:'end'});
    }
  }

  function computeScriptBase(){
    function getDirectoryOfFile(path){
      var hashIndex = path.lastIndexOf('#');
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf('?');
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf('/', Math.min(queryIndex, hashIndex));
      return slashIndex >= 0?path.substring(0, slashIndex + 1):'';
    }

    function ensureAbsoluteUrl(url){
      if (url.match(/^\w+:\/\//)) {
      }
       else {
        var img = $doc_0.createElement('img');
        img.src = url + 'clear.cache.gif';
        url = getDirectoryOfFile(img.src);
      }
      return url;
    }

    function tryMetaTag(){
      var metaVal = __gwt_getMetaProperty('baseUrl');
      if (metaVal != null) {
        return metaVal;
      }
      return '';
    }

    function tryNocacheJsTag(){
      var scriptTags = $doc_0.getElementsByTagName('script');
      for (var i = 0; i < scriptTags.length; ++i) {
        if (scriptTags[i].src.indexOf('russel_ui.nocache.js') != -1) {
          return getDirectoryOfFile(scriptTags[i].src);
        }
      }
      return '';
    }

    function tryMarkerScript(){
      var thisScript;
      if (typeof isBodyLoaded == 'undefined' || !isBodyLoaded()) {
        var markerId = '__gwt_marker_russel_ui';
        var markerScript;
        $doc_0.write('<script id="' + markerId + '"><\/script>');
        markerScript = $doc_0.getElementById(markerId);
        thisScript = markerScript && markerScript.previousSibling;
        while (thisScript && thisScript.tagName != 'SCRIPT') {
          thisScript = thisScript.previousSibling;
        }
        if (markerScript) {
          markerScript.parentNode.removeChild(markerScript);
        }
        if (thisScript && thisScript.src) {
          return getDirectoryOfFile(thisScript.src);
        }
      }
      return '';
    }

    function tryBaseTag(){
      var baseElements = $doc_0.getElementsByTagName('base');
      if (baseElements.length > 0) {
        return baseElements[baseElements.length - 1].href;
      }
      return '';
    }

    var tempBase = tryMetaTag();
    if (tempBase == '') {
      tempBase = tryNocacheJsTag();
    }
    if (tempBase == '') {
      tempBase = tryMarkerScript();
    }
    if (tempBase == '') {
      tempBase = tryBaseTag();
    }
    if (tempBase == '') {
      tempBase = getDirectoryOfFile($doc_0.location.href);
    }
    tempBase = ensureAbsoluteUrl(tempBase);
    base = tempBase;
    return tempBase;
  }

  function processMetas(){
    var metas = document.getElementsByTagName('meta');
    for (var i = 0, n = metas.length; i < n; ++i) {
      var meta = metas[i], name_0 = meta.getAttribute('name'), content_0;
      if (name_0) {
        name_0 = name_0.replace('russel_ui::', '');
        if (name_0.indexOf('::') >= 0) {
          continue;
        }
        if (name_0 == 'gwt:property') {
          content_0 = meta.getAttribute('content');
          if (content_0) {
            var value, eq = content_0.indexOf('=');
            if (eq >= 0) {
              name_0 = content_0.substring(0, eq);
              value = content_0.substring(eq + 1);
            }
             else {
              name_0 = content_0;
              value = '';
            }
            metaProps[name_0] = value;
          }
        }
         else if (name_0 == 'gwt:onPropertyErrorFn') {
          content_0 = meta.getAttribute('content');
          if (content_0) {
            try {
              propertyErrorFunc = eval(content_0);
            }
             catch (e) {
              alert('Bad handler "' + content_0 + '" for "gwt:onPropertyErrorFn"');
            }
          }
        }
         else if (name_0 == 'gwt:onLoadErrorFn') {
          content_0 = meta.getAttribute('content');
          if (content_0) {
            try {
              onLoadErrorFunc = eval(content_0);
            }
             catch (e) {
              alert('Bad handler "' + content_0 + '" for "gwt:onLoadErrorFn"');
            }
          }
        }
      }
    }
  }

  function __gwt_getMetaProperty(name_0){
    var value = metaProps[name_0];
    return value == null?null:value;
  }

  function unflattenKeylistIntoAnswers(propValArray, value){
    var answer = answers;
    for (var i = 0, n = propValArray.length - 1; i < n; ++i) {
      answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
    }
    answer[propValArray[n]] = value;
  }

  function computePropValue(propName){
    var value = providers[propName](), allowedValuesMap = values[propName];
    if (value in allowedValuesMap) {
      return value;
    }
    var allowedValuesList = [];
    for (var k in allowedValuesMap) {
      allowedValuesList[allowedValuesMap[k]] = k;
    }
    if (propertyErrorFunc) {
      propertyErrorFunc(propName, allowedValuesList, value);
    }
    throw null;
  }

  var frameInjected;
  function maybeInjectFrame(){
    if (!frameInjected) {
      frameInjected = true;
      var iframe = $doc_0.createElement('iframe');
      iframe.src = "javascript:''";
      iframe.id = 'russel_ui';
      iframe.style.cssText = 'position:absolute;width:0;height:0;border:none';
      iframe.tabIndex = -1;
      $doc_0.body.appendChild(iframe);
      $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'moduleStartup', millis:(new Date).getTime(), type:'moduleRequested'});
      iframe.contentWindow.location.replace(base + initialHtml);
    }
  }

  providers['user.agent'] = function(){
    var ua = navigator.userAgent.toLowerCase();
    var makeVersion = function(result){
      return parseInt(result[1]) * 1000 + parseInt(result[2]);
    }
    ;
    if (function(){
      return ua.indexOf('opera') != -1;
    }
    ())
      return 'opera';
    if (function(){
      return ua.indexOf('webkit') != -1 || function(){
        if (ua.indexOf('chromeframe') != -1) {
          return true;
        }
        if (typeof window['ActiveXObject'] != 'undefined') {
          try {
            var obj = new ActiveXObject('ChromeTab.ChromeFrame');
            if (obj) {
              obj.registerBhoIfNeeded();
              return true;
            }
          }
           catch (e) {
          }
        }
        return false;
      }
      ();
    }
    ())
      return 'safari';
    if (function(){
      return ua.indexOf('msie') != -1 && $doc_0.documentMode >= 9;
    }
    ())
      return 'ie9';
    if (function(){
      return ua.indexOf('msie') != -1 && $doc_0.documentMode >= 8;
    }
    ())
      return 'ie8';
    if (function(){
      var result = /msie ([0-9]+)\.([0-9]+)/.exec(ua);
      if (result && result.length == 3)
        return makeVersion(result) >= 6000;
    }
    ())
      return 'ie6';
    if (function(){
      return ua.indexOf('gecko') != -1;
    }
    ())
      return 'gecko1_8';
    return 'unknown';
  }
  ;
  values['user.agent'] = {gecko1_8:0, ie6:1, ie8:2, ie9:3, opera:4, safari:5};
  russel_ui.onScriptLoad = function(){
    if (frameInjected) {
      loadDone = true;
      maybeStartModule();
    }
  }
  ;
  russel_ui.onInjectionDone = function(){
    scriptsDone = true;
    $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'loadExternalRefs', millis:(new Date).getTime(), type:'end'});
    maybeStartModule();
  }
  ;
  processMetas();
  computeScriptBase();
  var strongName;
  var initialHtml;
  if (isHostedMode()) {
    if ($wnd_0.external && ($wnd_0.external.initModule && $wnd_0.external.initModule('russel_ui'))) {
      $wnd_0.location.reload();
      return;
    }
    initialHtml = 'hosted.html?russel_ui';
    strongName = '';
  }
  $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'bootstrap', millis:(new Date).getTime(), type:'selectingPermutation'});
  if (!isHostedMode()) {
    try {
      unflattenKeylistIntoAnswers(['ie8'], '329DD72F08EB82E90F6B15100D18E480');
      unflattenKeylistIntoAnswers(['ie6'], '890D133BBB83EA5F745053ED6975989E');
      unflattenKeylistIntoAnswers(['ie9'], 'BADA65347613C5F209C74D47253F099A');
      unflattenKeylistIntoAnswers(['opera'], 'DFCA3F5B164036C99D92392FC86C0DC3');
      unflattenKeylistIntoAnswers(['gecko1_8'], 'ECFBD4D8C0DD076C895C299FDF44AF6A');
      unflattenKeylistIntoAnswers(['safari'], 'EE1AEA16B8C125F29D9CDB948806103C');
      strongName = answers[computePropValue('user.agent')];
      var idx = strongName.indexOf(':');
      if (idx != -1) {
        softPermutationId = Number(strongName.substring(idx + 1));
        strongName = strongName.substring(0, idx);
      }
      initialHtml = strongName + '.cache.html';
    }
     catch (e) {
      return;
    }
  }
  var onBodyDoneTimerId;
  function onBodyDone(){
    if (!bodyDone) {
      bodyDone = true;
      if (!__gwt_stylesLoaded['gwt/clean/clean.css']) {
        var l = $doc_0.createElement('link');
        __gwt_stylesLoaded['gwt/clean/clean.css'] = l;
        l.setAttribute('rel', 'stylesheet');
        l.setAttribute('href', base + 'gwt/clean/clean.css');
        $doc_0.getElementsByTagName('head')[0].appendChild(l);
      }
      maybeStartModule();
      if ($doc_0.removeEventListener) {
        $doc_0.removeEventListener('DOMContentLoaded', onBodyDone, false);
      }
      if (onBodyDoneTimerId) {
        clearInterval(onBodyDoneTimerId);
      }
    }
  }

  if ($doc_0.addEventListener) {
    $doc_0.addEventListener('DOMContentLoaded', function(){
      maybeInjectFrame();
      onBodyDone();
    }
    , false);
  }
  var onBodyDoneTimerId = setInterval(function(){
    if (/loaded|complete/.test($doc_0.readyState)) {
      maybeInjectFrame();
      onBodyDone();
    }
  }
  , 50);
  $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'bootstrap', millis:(new Date).getTime(), type:'end'});
  $stats && $stats({moduleName:'russel_ui', sessionId:$sessionId_0, subSystem:'startup', evtGroup:'loadExternalRefs', millis:(new Date).getTime(), type:'begin'});
  if (!__gwt_scriptsLoaded['sc/modules/ISC_Core.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_Core.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_Core.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_Foundation.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_Foundation.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_Foundation.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_Containers.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_Containers.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_Containers.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_Grids.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_Grids.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_Grids.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_Forms.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_Forms.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_Forms.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_RichTextEditor.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_RichTextEditor.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_RichTextEditor.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_Calendar.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_Calendar.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_Calendar.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/modules/ISC_DataBinding.js']) {
    __gwt_scriptsLoaded['sc/modules/ISC_DataBinding.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/modules/ISC_DataBinding.js"><\/script>');
  }
  if (!__gwt_scriptsLoaded['sc/skins/Enterprise/load_skin.js']) {
    __gwt_scriptsLoaded['sc/skins/Enterprise/load_skin.js'] = true;
    document.write('<script language="javascript" src="' + base + 'sc/skins/Enterprise/load_skin.js"><\/script>');
  }
  $doc_0.write('<script defer="defer">russel_ui.onInjectionDone(\'russel_ui\')<\/script>');
}

russel_ui();
