package ca.ubc.ece.salt.pangor.original.js.api;

import ca.ubc.ece.salt.pangor.original.api.ClassAPI;
import ca.ubc.ece.salt.pangor.original.api.PackageAPI;
import ca.ubc.ece.salt.pangor.original.api.TopLevelAPI;
import java.util.Arrays;
import java.util.List;

public class JSAPIFactory
{
  public static TopLevelAPI buildTopLevelAPI()
  {
    List<String> keywords = Arrays.asList(new String[] { "abstract", "arguments", "await", "boolean", "break", "byte", "case", "catch", 
    		"char", "class", "const", "continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval", 
    		"export", "extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements", 
    		"import", "in", "instanceof", "int", "interface", "let", "long", "module", "native", "new", "null", 
    		"number", "package", "private", "protected", "public", "regexp", "return", "short", "static", 
    		"string", "super", "switch", "synchronized", "this", "undefined", 
    		"throw", "throws", "transient", "true", "try", "typeof", "var", "void", "volatile", "while", "with", "yield", 
    		"callback", "error", "undefined", "falsey", "sheq", "eq" });
    
    List<PackageAPI> packages = buildTopLevelPackages();
    
    List<String> methods = Arrays.asList(new String[] { "alert","anchor","applicationCache","atob","AudioListener","BaseAudioContext",
    		"big","BigInt","BigInt64Array","BiquadFilterNode","blink","bold","btoa","callback","cancelAnimationFrame",
    		"cancelIdleCallback","charAt","charCodeAt","close","codePointAt","concat","confirm","console",
    		"CustomEvent","DataTransfer","DataTransferItem","DataTransferItemList","DataView","Date","decodeURI",
    		"decodeURIComponent","DelayNode","DeviceMotionEvent","DOMError","DOMException","DOMImplementation",
    		"DOMMatrix","DOMMatrixReadOnly","DOMParser","DOMPoint","DOMPointReadOnly","DOMQuad","DOMRect",
    		"DOMRectReadOnly","DOMStringList","DOMStringMap","DOMTokenList","Element","encodeURI","encodeURIComponent",
    		"endsWith","escape","eval","FederatedCredential","fetch","File","FileList","FileReader","find","fixed",
    		"Float32Array","Float64Array","fontcolor","fontsize","FormData","Function","GainNode","Gamepad","GamepadButton",
    		"GamepadEvent","getComputedStyle","getSelection","Gyroscope","hasOwnProperty","Headers","HTMLAllCollection",
    		"HTMLAnchorElement","HTMLAreaElement","HTMLAudioElement","HTMLBaseElement","HTMLBodyElement","HTMLBRElement",
    		"HTMLButtonElement","HTMLCanvasElement","HTMLCollection","HTMLContentElement","HTMLDataElement",
    		"HTMLDataListElement","HTMLDetailsElement","HTMLDialogElement","HTMLDivElement","HTMLDListElement",
    		"HTMLDocument","HTMLElement","HTMLEmbedElement","HTMLFieldSetElement","HTMLFontElement",
    		"HTMLFormControlsCollection","HTMLFormElement","HTMLFrameSetElement","HTMLHeadElement",
    		"HTMLHeadingElement","HTMLHRElement","HTMLHtmlElement","HTMLIFrameElement","HTMLImageElement",
    		"HTMLInputElement","HTMLLabelElement","HTMLLegendElement","HTMLLIElement","HTMLLinkElement",
    		"HTMLMapElement","HTMLMarqueeElement","HTMLMediaElement","HTMLMenuElement","HTMLMetaElement",
    		"HTMLMeterElement","HTMLModElement","HTMLObjectElement","HTMLOListElement","HTMLOptGroupElement",
    		"HTMLOptionElement","HTMLOptionsCollection","HTMLOutputElement","HTMLParagraphElement","HTMLParamElement",
    		"HTMLPictureElement","HTMLPreElement","HTMLProgressElement","HTMLQuoteElement","HTMLScriptElement",
    		"HTMLSelectElement","HTMLShadowElement","HTMLSlotElement","HTMLSourceElement","HTMLSpanElement",
    		"HTMLStyleElement","HTMLTableCaptionElement","HTMLTableCellElement","HTMLTableColElement","HTMLTableElement",
    		"HTMLTableRowElement","HTMLTableSectionElement","HTMLTemplateElement","HTMLTextAreaElement",
    		"HTMLTimeElement","HTMLTitleElement","HTMLTrackElement","HTMLUListElement","HTMLUnknownElement",
    		"HTMLVideoElement","indexOf","isFinite","isNaN","isPrototypeOf","italics","localeCompare","Map",
    		"match","matchMedia","MediaCapabilities","MediaCapabilitiesInfo","MediaDeviceInfo","MediaDevices",
    		"MediaElementAudioSourceNode","MediaEncryptedEvent","MediaError","MediaKeyMessageEvent","MediaKeySession",
    		"MediaKeyStatusMap","MediaKeySystemAccess","MediaList","MediaQueryList","MediaQueryListEvent","MediaRecorder",
    		"MediaSettingsRange","MediaSource","MediaStream","MediaStreamAudioDestinationNode","MediaStreamAudioSourceNode",
    		"MediaStreamEvent","MediaStreamTrack","MessageChannel","MessageEvent","MessagePort","MIDIAccess",
    		"MIDIConnectionEvent","MIDIInput","MIDIInputMap","MIDIMessageEvent","MIDIOutput","MIDIOutputMap",
    		"MIDIPort","MimeType","MimeTypeArray","moveBy","moveTo","MutationEvent","MutationObserver",
    		"MutationRecord","normalize","Number","open","openDatabase","opener","Option","padEnd","padStart",
    		"PageTransitionEvent","PannerNode","parseFloat","parseInt","Path2D","PaymentRequestUpdateEvent",
    		"Performance","PerformanceEntry","PerformanceLongTaskTiming","PerformanceMark","PerformanceMeasure",
    		"PerformanceNavigation","PerformanceNavigationTiming","PerformanceObserver","PerformanceObserverEntryList",
    		"PerformancePaintTiming","PerformanceResourceTiming","PerformanceServerTiming","PerformanceTiming",
    		"PeriodicWave","Permissions","PermissionStatus","PhotoCapabilities","Plugin","PluginArray","PointerEvent",
    		"PopStateEvent","postMessage","ProgressEvent","Promise","PromiseRejectionEvent","prompt","Proxy","Range",
    		"RangeError","releaseEvents","RemotePlayback","repeat","requestIdleCallback","resizeBy","ResizeObserver",
    		"ResizeObserverEntry","resizeTo","Response","RTCCertificate","RTCDataChannel","RTCDataChannelEvent",
    		"RTCDTMFSender","RTCDTMFToneChangeEvent","RTCIceCandidate","RTCPeerConnection","RTCPeerConnectionIceEvent",
    		"RTCRtpContributingSource","RTCRtpReceiver","RTCRtpSender","RTCSessionDescription","RTCStatsReport",
    		"RTCTrackEvent","Screen","ScriptProcessorNode","scroll","scrollBy","search","SecurityPolicyViolationEvent",
    		"ServiceWorker","ServiceWorkerContainer","ServiceWorkerRegistration","ShadowRoot","SharedWorker","slice",
    		"small","SourceBuffer","SourceBufferList","SpeechSynthesisEvent","SpeechSynthesisUtterance","split",
    		"startsWith","stop","Storage","strike","String","StylePropertyMap","StylePropertyMapReadOnly","sub",
    		"substr","substring","SVGAElement","SVGAngle","SVGAnimatedPreserveAspectRatio","SVGAnimateElement",
    		"SVGAnimateMotionElement","SVGAnimateTransformElement","Symbol","SyncManager","SyntaxError",
    		"TaskAttributionTiming","Text","TextDecoder","TextEncoder","TextEvent","TextMetrics","TextTrack",
    		"TextTrackCue","TextTrackCueList","TextTrackList","TimeRanges","toLocaleLowerCase","toLocaleString",
    		"toLocaleUpperCase","toLowerCase","toString","Touch","TouchEvent","TouchList","TrackEvent","TransformStream",
    		"TransitionEvent","TreeWalker","trim","trimEnd","trimLeft","trimRight","trimStart","TypeError","UIEvent",
    		"Uint16Array","Uint32Array","Uint8Array","Uint8ClampedArray","unescape","URIError","URL","URLSearchParams",
    		"USB","USBAlternateInterface","USBConfiguration","USBConnectionEvent","USBDevice","USBEndpoint",
    		"USBInterface","USBInTransferResult","USBIsochronousInTransferPacket","USBIsochronousInTransferResult",
    		"USBIsochronousOutTransferPacket","USBIsochronousOutTransferResult","USBOutTransferResult","ValidityState",
    		"valueOf","VisualViewport","VTTCue","WaveShaperNode","WeakMap","WeakSet","WebGL2RenderingContext",
    		"WebGLActiveInfo","WebGLBuffer","WebGLContextEvent","WebGLFramebuffer","WebGLProgram","WebGLQuery",
    		"WebGLRenderbuffer","WebGLRenderingContext","WebGLSampler","WebGLShader","WebGLShaderPrecisionFormat",
    		"WebGLSync","WebGLTexture","WebGLTransformFeedback","WebGLUniformLocation","WebGLVertexArrayObject",
    		"WebKitAnimationEvent","webkitCancelAnimationFrame","WebKitCSSMatrix","webkitMediaStream",
    		"WebKitMutationObserver","webkitRequestAnimationFrame","webkitRequestFileSystem",
    		"webkitResolveLocalFileSystemURL","webkitRTCPeerConnection","webkitSpeechGrammar",
    		"webkitSpeechGrammarList","webkitSpeechRecognition","webkitSpeechRecognitionError",
    		"webkitSpeechRecognitionEvent","webkitStorageInfo","WebKitTransitionEvent","webkitURL",
    		"WebSocket","WheelEvent","Worker","WritableStream","XMLDocument","XMLHttpRequest",
    		"XMLHttpRequestEventTarget","XMLHttpRequestUpload","XMLSerializer","XPathEvaluator","XPathExpression",
    		"XPathResult","XSLTProcessor"});
    
    List<String> fields = Arrays.asList(new String[] { "console","constructor","devicePixelRatio","external","history","Infinity",
    		"lastDdllogResponse","length","localStorage","location","locationbar","microphone","moveBy",
    		"moveTo","name","NaN","navigator","offscreenBuffering","pageXOffset","pageYOffset","parent",
    		"print","prototype","Reflect","screen","screenLeft","screenX","screenY","scrollbars","scrollX",
    		"scrollY","self","sessionStorage","speechSynthesis","status","statusbar","styleMedia","top",
    		"visualViewport","WebAssembly","window"});
    
    List<String> constants = Arrays.asList(new String[0]);
    
    List<String> events = Arrays.asList(new String[] { "onabort","onafterprint","onanimationend","onanimationiteration",
    		"onanimationstart","onappinstalled","onauxclick","onbeforeinstallprompt","onbeforeprint","onbeforeunload",
    		"onblur","oncancel","oncanplay","oncanplaythrough","onchange","onclick","onclose","oncontextmenu",
    		"oncuechange","ondblclick","ondevicemotion","ondeviceorientation","ondeviceorientationabsolute",
    		"ondrag","ondragend","ondragenter","ondragleave","ondragover","ondragstart","ondrop","ondurationchange",
    		"onemptied","onended","onerror","onfocus","ongotpointercapture","onhashchange","oninput","oninvalid",
    		"onlanguagechange","onload","onloadeddata","onloadedmetadata","onloadstart","onlostpointercapture","onmessage",
    		"onmessageerror","onmousedown","onmouseenter","onmouseleave","onmousemove","onmouseout","onmouseover","onmouseup",
    		"onmousewheel","onoffline","ononline","onpagehide","onpageshow","onpause","onplay","onplaying","onpointercancel",
    		"onpointerdown","onpointerenter","onpointerleave","onpointermove","onpointerout","onpointerup","onpopstate",
    		"onprogress","onratechange","onrejectionhandled","onreset","onresize","onscroll","onsearch","onseeked","onseeking",
    		"onselect","onstalled","onsubmit","onsuspend","ontimeupdate","ontoggle","ontransitionend","onunhandledrejection",
    		"onunload","onvolumechange","onwaiting","onwebkitanimationend","onwebkitanimationiteration","onwebkitanimationstart",
    		"onwebkittransitionend","onwheel" });
    
    List<String> exceptions = Arrays.asList(new String[] { "exception" });
    
    List<ClassAPI> classes = buildTopLevelClasses();
    
    return new TopLevelAPI(keywords, packages, methods, fields, constants, events, exceptions, classes);
  }
  
  public static List<PackageAPI> buildTopLevelPackages()
  {

	  return Arrays.asList(new PackageAPI[0]);
	  
	  //PackageAPI fileSystem = buildFileSystemPackage();
    
    //PackageAPI path = buildPathPackage();
    
    //PackageAPI dns = buildDNSPackage();
    
    //PackageAPI test = buildTestPackage();
    
    //PackageAPI process = buildProcessPackage();
    
    //PackageAPI server = buildServerPackage();
    
    //PackageAPI socket = buildSocketPackage();
    
    //PackageAPI net = buildNetPackage();
    
    //return Arrays.asList(new PackageAPI[]{ fileSystem, path, dns, test, process, server, socket, net });
  }
  
  public static PackageAPI buildProcessPackage()
  {
	  PackageAPI process = new PackageAPI("process", 
	    		Arrays.asList(new String[] { "abort", "chdir", "cpuUsage", "cwd", "disconnect", "dlopen", "emitWarning", "exit", 
	    				"getegid", "geteuid", "getgid", "getgroups", "getuid", "hasUncaughtExceptionCaptureCallback", "hrtime", 
	    				"initgroups", "kill", "memoryUsage", "nextTick", "send", "setegid", "seteuid", "setgid", "setgroups", 
	    				"setuid", "setUncaughtExceptionCaptureCallback", "umask", "uptime"}), 
	    		Arrays.asList(new String[] { "arch", "argv", "argv0", "channel", "config", "connected", "debugPort", 
	    				"env", "execArgv", "execPath", "exitCode", "mainModule", "noDeprecation", "pid", "platform", "ppid",
	    				"release", "stderr", "stdin", "stdout", "throwDeprecation", "title", "traceDeprecation", "version", 
	    				"versions"}), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new ClassAPI[0]));
	  
	  return process;

  }
  
  public static PackageAPI buildServerPackage()
  {
	  PackageAPI server = new PackageAPI("server", 
	    		Arrays.asList(new String[] { "address", "close", "getConnections", "listen", "ref", "unref" }), 
	    		Arrays.asList(new String[] { "connections", "listening" , "maxConnections"}), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new ClassAPI[0]));
	  
	  return server;

  }
  
  public static PackageAPI buildSocketPackage()
  {
	  PackageAPI socket = new PackageAPI("socket", 
	    		Arrays.asList(new String[] { "address", "connect", "destroy", "end", "pause", "ref", "resume", "setEncoding", 
	    				"setKeepAlive", "setNoDelay", "setTimeout", "unref", "write"}), 
	    		Arrays.asList(new String[] { "bufferSize", "bytesRead" , "bytesWritten", "connecting", "destroyed", "localAddress",
	    				"localPort", "remoteAddress", "remoteFamily", "remotePort"}), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new ClassAPI[0]));
	  
	  return socket;

  }
  
  public static PackageAPI buildNetPackage()
  {
	  PackageAPI net = new PackageAPI("net", 
	    		Arrays.asList(new String[] { "connect", "createConnection", "createServer", "isIP", "isIPv4", "isIPv6" }), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new ClassAPI[0]));
	  
	  return net;

  }
  
  public static PackageAPI buildDNSPackage()
  {
	  PackageAPI dns = new PackageAPI("dns", 
	    		Arrays.asList(new String[] { "getServers","lookup","lookupService","resolve","resolve4","resolve6","resolveAny",
	    				"resolveCname","resolveMx","resolveNaptr","resolveNs","resolvePtr","resolveSoa",
	    				"resolveSrv","resolveTxt","reverse","setServers" }), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new ClassAPI[0]));
	  
	  return dns;

  }
  
  public static PackageAPI buildTestPackage()
  {
	  
	  PackageAPI test = new PackageAPI("assert", 
	    		Arrays.asList(new String[] { "deepEqual","deepStrictEqual","doesNotReject","doesNotThrow","equal","fail",
	    				"ifError","notDeepEqual","notDeepStrictEqual","notEqual","notStrictEqual",
	    				"ok","rejects","strictEqual","throws" }), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new String[0]), 
	    		Arrays.asList(new ClassAPI[0]));
	    
	    return test;
  }
  
  public static List<ClassAPI> buildTopLevelClasses()
  {
    ClassAPI json = new ClassAPI("JSON",
    		Arrays.asList(new String[] { "parse", "stringify" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI function = new ClassAPI("Function",
    		Arrays.asList(new String[] { "apply", "bind", "call" }), 
    		Arrays.asList(new String[] { "length" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]),
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI array = new ClassAPI("Array", 
    		Arrays.asList(new String[] { "from", "of", "concat", "indexOf", "lastIndexOf", "pop", "push", "reverse", "shift", 
    				"splice", "sort", "unshift", "forEach", "every", "some", "filter", "fill", "map", "reduce", "reduceRight", 
    				"propertyIsEnumerable", "isArray", "bind", "entries", "copyWithin", "includes", "keys"}), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "length" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI date = new ClassAPI("Date", 
    		Arrays.asList(new String[] { "getDate", "getDay", "getFullYear", "getHours", 
    		"getMilliseconds", "getMinutes", "getMonth", "getSeconds", "getTime", "getTimezoneOffset", "getUTCDate", 
    		"getUTCDay", "getUTCFullyear", "getUTCHours", "getUTCMilliseconds", "getUTCMinutes", "getUTCMonth", 
    		"getUTCSeconds", "getYear", "parse", "setDate", "setFullYear", "setHours", "setMilliseconds", "setMinutes", 
    		"setMonth", "setSeconds", "setTime", "setUTCDate", "setUTCFullYear", "setUTCHours", "setUTCMilliseconds", 
    		"setUTCMinutes", "setUTCMonth", "setUTCSeconds", "setYear", "toDateString", "toGMTString", "toISOString", 
    		"toJSON", "toLocaleDateString", "toLocaleTimeString", "toLocaleString", "toTimeString", "toUTCString", "UTC" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI math = new ClassAPI("Math", 
    		Arrays.asList(new String[] { "abs", "acos", "asin", "atan", "atan2", "ceil", "cos", "exp", "floor", "max", "min", "pow", 
    				"random", "round", "sin", "sqrt", "tan" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "E", "LN2", "LN10", "LOG2E", "LOG10E", "PI", "SQRT1_2", "SQRT2" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI object = new ClassAPI("Object", 
    		Arrays.asList(new String[] { "assign", "create", "defineProperty", "defineProperties", "freeze", 
    		"getOwnPropertyDescriptor", "getOwnPropertyDescriptors", "getOwnPropertyNames", "getPrototypeOf", "is", "isExtensible", 
    		"isFrozen", "isSealed", "keys", "preventExtensions", "seal", "hasOwnProperty", "isPrototypeOf", 
    		"propertyIsEnumerable", "toLocalString", "toString", "getOwnPropertySymbols" }), 
    		Arrays.asList(new String[] { "length", "prototype", "constructor", "_noSuchMethod_" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI error = new ClassAPI("Error", 
    		Arrays.asList(new String[] { "captureStackTrace" }), 
    		Arrays.asList(new String[] { "message", "stackTraceLimit" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI number = new ClassAPI("Number", 
    		Arrays.asList(new String[] { "isNaN", "isFinite", "isInteger", "isSafeInteger", "parseFloat", "parseInt", 
    				"toExponential", "toFixed", "toPrecision", "isSafeInteger", "isInteger", "isFinite", "toString" }), 
    		Arrays.asList(new String[] { "length", "NaN" }), 
    		Arrays.asList(new String[] { "MAX_VALUE", "MIN_VALUE", "NEGATIVE_INFINITY", "POSITIVE_INFINITY", "EPSILON", "MAX_SAFE_INTEGER" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI string = new ClassAPI("String", 
    		Arrays.asList(new String[] { "fromCharCode", "fromCodePoint", "charAt", "charCodeAt", "concat", "localeCompare", "match", 
    				"replace", 
    				"search", "slice", "split", "substr", "substring", "toLocaleLowerCase", "toLocaleUpperCase", "toLowerCase", 
    				"toUpperCase", "trim" }), 
    		Arrays.asList(new String[] { "length" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI regexp = new ClassAPI("RegExp", 
    		Arrays.asList(new String[] { "compile", "exec", "test", "toString" }), 
    		Arrays.asList(new String[] { "lastIndex", "global", "ignoreCase", "multiline", "source", "lastMatch", "flags", "input",
    				"leftContext", "rightContext", "dotAll", "flags", "sticky"}), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    return Arrays.asList(new ClassAPI[] { json, function, error, array, date, math, number, string, regexp, object });
  }
  
  public static PackageAPI buildPathPackage()
  {
    PackageAPI path = new PackageAPI("path", 
    		Arrays.asList(new String[] { "normalize", "join", "resolve", "isAbsolute", "relative", "dirname", "basename", 
    				"extname", "parse", "format", "toNamespacedPath" }), 
    		Arrays.asList(new String[] { "sep", "delimiter", "posix", "win32" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    return path;
  }
  
  public static PackageAPI buildFileSystemPackage()
  {
    ClassAPI stats = new ClassAPI("Stats", 
    		Arrays.asList(new String[] { "isFile", "isDirectory", "isBlockDevice", "isCharacterDevice", "isSymbolicLink", 
    				"isFIFO", "isSocket" }), 
    		Arrays.asList(new String[] { "dev","ino","mode","nlink","uid","gid","rdev","size",
    				"blksize","blocks","atimeMs","mtimeMs","ctimeMs","birthtimeMs","atime","mtime",
    				"ctime","birthtime"}), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI writeStream = new ClassAPI("WriteStream", 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "bytesWritten", "path" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "open", "close", "ready" }), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI readStream = new ClassAPI("ReadStream", 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[]{"bytesRead", "path"}), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "open", "close", "ready" }), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI fsWatcher = new ClassAPI("FSWatcher", 
    		Arrays.asList(new String[] { "close" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "change", "error", "close" }), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI promises = new ClassAPI("FileHandle", 
    		Arrays.asList(new String[] { "appendFile","chmod","chown","close","datasync","fd","read","readFile","stat",
    				"sync","truncate","utimes","write","writeFile" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
        
    
    return new PackageAPI("fs", 
    		Arrays.asList(new String[] { "access","accessSync","appendFile","appendFileSync","chmod","chmodSync","chown",
    				"chownSync","close","closeSync","constantscopyFile","copyFileSync","createReadStream","createWriteStream",
    				"exists","existsSync","fchmod","fchmodSync","fchown","fchownSync","fdatasync","fdatasyncSync","fstat",
    				"fstatSync","fsync","fsyncSync","ftruncate","ftruncateSync","futimes","futimesSync","lchmod","lchmodSync",
    				"lchown","lchownSync","link","linkSync","lstat","lstatSync","mkdir","mkdirSync","mkdtemp","mkdtempSync",
    				"open","openSync","read","readdir","readdirSync","readFile","readFileSync","readlink","readlinkSync",
    				"readSync","realpath","realpathSync","rename","renameSync","rmdir","rmdirSync","stat","statSync","symlink",
    				"symlinkSync","truncate","truncateSync","unlink","unlinkSync","unwatchFile","utimes","utimesSync","watch",
    				"watchFile","write","writeFile","writeFileSync","writeSync" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[] { stats, writeStream, readStream, fsWatcher, promises }));
  }
}
