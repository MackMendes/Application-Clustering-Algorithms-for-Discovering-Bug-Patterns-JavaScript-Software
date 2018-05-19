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
    List<String> keywords = Arrays.asList(new String[] { "abstract", "arguments", "boolean", "break", "byte", "case", "catch", 
    		"char", "class", "const", "continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval", 
    		"export", "extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements", 
    		"import", "in", "instanceof", "int", "interface", "let", "long", "native", "new", "null", "package", "private", 
    		"protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "undefined", 
    		"throw", "throws", "transient", "true", "try", "typeof", "var", "void", "volatile", "while", "with", "yield", 
    		"callback", "error", "undefined", "falsey", "sheq", "eq" });
    
    List<PackageAPI> packages = buildTopLevelPackages();
    
    List<String> methods = Arrays.asList(new String[] { "eval", "hasOwnProperty", "isFinite", "isNaN", "isPrototypeOf", 
    		"toString", "valueOf", "decodeURI", "decodeURIComponent", "encodeURI", "encodeURIComponent", "Number", 
    		"parseFloat", "parseInt", "String", "unescape", "callback" });
    
    List<String> fields = Arrays.asList(new String[] { "length", "name", "prototype", "constructor", "Infinity", "NaN" });
    
    List<String> constants = Arrays.asList(new String[0]);
    
    List<String> events = Arrays.asList(new String[0]);
    
    List<String> exceptions = Arrays.asList(new String[] { "exception" });
    
    List<ClassAPI> classes = buildTopLevelClasses();
    
    return new TopLevelAPI(keywords, packages, methods, fields, constants, events, exceptions, classes);
  }
  
  @SuppressWarnings("unused")
public static List<PackageAPI> buildTopLevelPackages()
  {

	PackageAPI fileSystem = buildFileSystemPackage();
    
    PackageAPI path = buildPathPackage();
    
    return Arrays.asList(new PackageAPI[0]);
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
    		Arrays.asList(new String[] { "concat", "indexOf", "lastIndexOf", "pop", "push", "reverse", "shift", 
    				"splice", "sort", "unshift", "forEach", "every", "some", "filter", "map", "reduce", "reduceRight" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
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
    		Arrays.asList(new String[] { "abs", "acos", "asin", "atan", "atan2", "ceil", "cos", "exp", "floor", "max", "min", "pow", "random", "round", "sin", "sqrt", "tan" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "E", "LN2", "LN10", "LOG2E", "LOG10E", "PI", "SQRT1_2", "SQRT2" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI object = new ClassAPI("Object", 
    		Arrays.asList(new String[] { "create", "defineProperty", "defineProperties", 
    		"freeze", "getOwnPropertyDescriptor", "getOwnPropertyNames", "getPrototypeOf", "is", "isExtensible", 
    		"isFrozen", "isSealed", "keys", "preventExtensions", "seal", "hasOwnProperty", "isPrototypeOf", 
    		"propertyIsEnumerable", "toLocalString", "toString" }), 
    		Arrays.asList(new String[] { "length", "prototype", "constructor", "_noSuchMethod_" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI error = new ClassAPI("Error", 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "message", "name" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI number = new ClassAPI("Number", 
    		Arrays.asList(new String[] { "isNaN", "isFinite", "isInteger", "isSafeInteger", "parseFloat", "parseInt", 
    				"toExponential", "toFixed", "toPrecision" }), 
    		Arrays.asList(new String[] { "NaN" }), 
    		Arrays.asList(new String[] { "MAX_VALUE", "MIN_VALUE", "NEGATIVE_INFINITY", "POSITIVE_INFINITY" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI string = new ClassAPI("String", 
    		Arrays.asList(new String[] { "fromCharCode", "charAt", "charCodeAt", "concat", "localeCompare", "match", "replace", 
    				"search", "slice", "split", "substr", "substring", "toLocaleLowerCase", "toLocaleUpperCase", "toLowerCase", 
    				"toUpperCase", "trim" }), 
    		Arrays.asList(new String[] { "length" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI regexp = new ClassAPI("RegExp", 
    		Arrays.asList(new String[] { "compile", "exec", "test" }), 
    		Arrays.asList(new String[] { "lastIndex", "global", "ignoreCase", "multiline", "source" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    return Arrays.asList(new ClassAPI[] { json, function, error, array, date, math, number, string, regexp, object });
  }
  
  public static PackageAPI buildPathPackage()
  {
    PackageAPI path = new PackageAPI("path", 
    		Arrays.asList(new String[] { "normalize", "join", "resolve", "isAbsolute", "relative", "dirname", "basename", 
    				"extname", "parse", "format" }), 
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
    		Arrays.asList(new String[] { "atime", "mtime", "ctime", "birthtime" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI writeStream = new ClassAPI("WriteStream", 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "bytesWritten" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "open" }), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI readStream = new ClassAPI("ReadStream", 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "open" }), 
    		Arrays.asList(new ClassAPI[0]));
    
    ClassAPI fsWatcher = new ClassAPI("FSWatcher", 
    		Arrays.asList(new String[] { "close" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[] { "change", "error" }), 
    		Arrays.asList(new ClassAPI[0]));
    
    return new PackageAPI("fs", 
    		Arrays.asList(new String[] { "rename", "renameSync", "ftruncate", "ftruncateSync", "truncate", "truncateSync", 
    				"chown", "chownSync", "lchown", "lchownSync", "chmod", "chmodSync", "fchmod", "fchmodSync", "lchmod", 
    				"lchmodSync", "stat", "lstat", "fstat", "statSync", "lstatSync", "fstatSync", "link", "linkSync", 
    				"symlink", "symlinkSync", "readlink", "readlinkSync", "realpath", "realpathSync", "unlink", 
    				"unlinkSync", "rmdir", "rmdirSync", "mkdir", "mkdirSync", "readdir", "readdirSync", "close", 
    				"closeSync", "open", "openSync", "utimes", "utimesSync", "futimes", "futimesSync", "fsync", 
    				"fsyncSync", "write", "writeSync", "read", "readSync", "readFile", "readFileSync", "writeFile", 
    				"writeFileSync", "appendFile", "appendFileSync", "watchFile", "unwatchFile", "watch", "exists", 
    				"existsSync", "access", "accessSync", "createReadStream", "createWriteStream" }), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new String[0]), 
    		Arrays.asList(new ClassAPI[] { stats, writeStream, readStream, fsWatcher }));
  }
}
