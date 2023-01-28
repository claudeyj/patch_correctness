/*   0*/package com.google.javascript.rhino.testing;
/*   0*/
/*   0*/import com.google.javascript.rhino.JSTypeExpression;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.FunctionBuilder;
/*   0*/import com.google.javascript.rhino.jstype.FunctionType;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeNative;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import com.google.javascript.rhino.jstype.ObjectType;
/*   0*/import com.google.javascript.rhino.jstype.ParameterizedType;
/*   0*/import com.google.javascript.rhino.jstype.RecordTypeBuilder;
/*   0*/import junit.framework.TestCase;
/*   0*/
/*   0*/public abstract class BaseJSTypeTestCase extends TestCase {
/*   0*/  protected JSTypeRegistry registry;
/*   0*/  
/*   0*/  protected TestErrorReporter errorReporter;
/*   0*/  
/*   0*/  protected JSType ALL_TYPE;
/*   0*/  
/*   0*/  protected ObjectType NO_OBJECT_TYPE;
/*   0*/  
/*   0*/  protected ObjectType NO_TYPE;
/*   0*/  
/*   0*/  protected ObjectType NO_RESOLVED_TYPE;
/*   0*/  
/*   0*/  protected JSType ARRAY_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType ARRAY_TYPE;
/*   0*/  
/*   0*/  protected JSType BOOLEAN_OBJECT_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType BOOLEAN_OBJECT_TYPE;
/*   0*/  
/*   0*/  protected JSType BOOLEAN_TYPE;
/*   0*/  
/*   0*/  protected JSType CHECKED_UNKNOWN_TYPE;
/*   0*/  
/*   0*/  protected JSType DATE_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType DATE_TYPE;
/*   0*/  
/*   0*/  protected JSType ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType ERROR_TYPE;
/*   0*/  
/*   0*/  protected JSType EVAL_ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType EVAL_ERROR_TYPE;
/*   0*/  
/*   0*/  protected FunctionType FUNCTION_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected FunctionType FUNCTION_INSTANCE_TYPE;
/*   0*/  
/*   0*/  protected ObjectType FUNCTION_PROTOTYPE;
/*   0*/  
/*   0*/  protected JSType GREATEST_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected JSType LEAST_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected JSType MATH_TYPE;
/*   0*/  
/*   0*/  protected JSType NULL_TYPE;
/*   0*/  
/*   0*/  protected JSType NUMBER_OBJECT_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType NUMBER_OBJECT_TYPE;
/*   0*/  
/*   0*/  protected JSType NUMBER_STRING_BOOLEAN;
/*   0*/  
/*   0*/  protected JSType NUMBER_TYPE;
/*   0*/  
/*   0*/  protected FunctionType OBJECT_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected JSType NULL_VOID;
/*   0*/  
/*   0*/  protected JSType OBJECT_NUMBER_STRING;
/*   0*/  
/*   0*/  protected JSType OBJECT_NUMBER_STRING_BOOLEAN;
/*   0*/  
/*   0*/  protected JSType OBJECT_PROTOTYPE;
/*   0*/  
/*   0*/  protected ObjectType OBJECT_TYPE;
/*   0*/  
/*   0*/  protected JSType RANGE_ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType RANGE_ERROR_TYPE;
/*   0*/  
/*   0*/  protected JSType REFERENCE_ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType REFERENCE_ERROR_TYPE;
/*   0*/  
/*   0*/  protected JSType REGEXP_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType REGEXP_TYPE;
/*   0*/  
/*   0*/  protected JSType STRING_OBJECT_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType STRING_OBJECT_TYPE;
/*   0*/  
/*   0*/  protected JSType STRING_TYPE;
/*   0*/  
/*   0*/  protected JSType SYNTAX_ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType SYNTAX_ERROR_TYPE;
/*   0*/  
/*   0*/  protected JSType TYPE_ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType TYPE_ERROR_TYPE;
/*   0*/  
/*   0*/  protected FunctionType U2U_CONSTRUCTOR_TYPE;
/*   0*/  
/*   0*/  protected FunctionType U2U_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType UNKNOWN_TYPE;
/*   0*/  
/*   0*/  protected JSType URI_ERROR_FUNCTION_TYPE;
/*   0*/  
/*   0*/  protected ObjectType URI_ERROR_TYPE;
/*   0*/  
/*   0*/  protected JSType VOID_TYPE;
/*   0*/  
/*   0*/  protected int NATIVE_PROPERTIES_COUNT;
/*   0*/  
/*   0*/  public static final String ALL_NATIVE_EXTERN_TYPES = "/**\n * @constructor\n * @param {*} opt_value\n */\nfunction Object(opt_value) {}\n\n/**\n * @constructor\n * @extends {Object}\n * @param {*} var_args\n */\n\nfunction Function(var_args) {}\n/**\n * @constructor\n * @extends {Object}\n * @param {*} var_args\n * @return {!Array}\n */\nfunction Array(var_args) {}\n\n/**\n * @constructor\n * @param {*} opt_value\n * @return {boolean}\n */\nfunction Boolean(opt_value) {}\n\n/**\n * @constructor\n * @param {*} opt_value\n * @return {number}\n */\nfunction Number(opt_value) {}\n\n/**\n * @constructor\n * @return {string}\n */\nfunction Date(opt_yr_num, opt_mo_num, opt_day_num, opt_hr_num,    opt_min_num, opt_sec_num, opt_ms_num) {}\n\n/**\n * @constructor\n * @extends {Object}\n * @param {*} opt_str\n * @return {string}\n */\nfunction String(opt_str) {}\n\n/**\n * @constructor\n * @param {*} opt_pattern\n * @param {*} opt_flags\n * @return {!RegExp}\n */\nfunction RegExp(opt_pattern, opt_flags) {}\n\n/**\n * @constructor\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!Error}\n */\nfunction Error(opt_message, opt_file, opt_line) {}\n\n/**\n * @constructor\n * @extends {Error}\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!EvalError}\n */\nfunction EvalError(opt_message, opt_file, opt_line) {}\n\n/**\n * @constructor\n * @extends {Error}\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!RangeError}\n */\nfunction RangeError(opt_message, opt_file, opt_line) {}\n\n/**\n * @constructor\n * @extends {Error}\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!ReferenceError}\n */\nfunction ReferenceError(opt_message, opt_file, opt_line) {}\n\n/**\n * @constructor\n * @extends {Error}\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!SyntaxError}\n */\nfunction SyntaxError(opt_message, opt_file, opt_line) {}\n\n/**\n * @constructor\n * @extends {Error}\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!TypeError}\n */\nfunction TypeError(opt_message, opt_file, opt_line) {}\n\n/**\n * @constructor\n * @extends {Error}\n * @param {*} opt_message\n * @param {*} opt_file\n * @param {*} opt_line\n * @return {!URIError}\n */\nfunction URIError(opt_message, opt_file, opt_line) {}\n\n/**\n * @param {string} progId\n * @param {string} opt_location\n * @constructor\n */\nfunction ActiveXObject(progId, opt_location) {}\n";
/*   0*/  
/*   0*/  protected void setUp() throws Exception {
/* 116*/    super.setUp();
/* 117*/    this.errorReporter = new TestErrorReporter(null, null);
/* 118*/    this.registry = new JSTypeRegistry(this.errorReporter);
/* 119*/    initTypes();
/*   0*/  }
/*   0*/  
/*   0*/  protected void initTypes() {
/* 123*/    this.ALL_TYPE = this.registry.getNativeType(JSTypeNative.ALL_TYPE);
/* 125*/    this.NO_OBJECT_TYPE = this.registry.getNativeObjectType(JSTypeNative.NO_OBJECT_TYPE);
/* 127*/    this.NO_TYPE = this.registry.getNativeObjectType(JSTypeNative.NO_TYPE);
/* 129*/    this.NO_RESOLVED_TYPE = this.registry.getNativeObjectType(JSTypeNative.NO_RESOLVED_TYPE);
/* 131*/    this.ARRAY_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.ARRAY_FUNCTION_TYPE);
/* 133*/    this.ARRAY_TYPE = this.registry.getNativeObjectType(JSTypeNative.ARRAY_TYPE);
/* 135*/    this.BOOLEAN_OBJECT_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.BOOLEAN_OBJECT_FUNCTION_TYPE);
/* 137*/    this.BOOLEAN_OBJECT_TYPE = this.registry.getNativeObjectType(JSTypeNative.BOOLEAN_OBJECT_TYPE);
/* 139*/    this.BOOLEAN_TYPE = this.registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
/* 141*/    this.CHECKED_UNKNOWN_TYPE = this.registry.getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE);
/* 143*/    this.DATE_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.DATE_FUNCTION_TYPE);
/* 145*/    this.DATE_TYPE = this.registry.getNativeObjectType(JSTypeNative.DATE_TYPE);
/* 147*/    this.ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.ERROR_FUNCTION_TYPE);
/* 149*/    this.ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.ERROR_TYPE);
/* 151*/    this.EVAL_ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.EVAL_ERROR_FUNCTION_TYPE);
/* 153*/    this.EVAL_ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.EVAL_ERROR_TYPE);
/* 155*/    this.FUNCTION_FUNCTION_TYPE = this.registry.getNativeFunctionType(JSTypeNative.FUNCTION_FUNCTION_TYPE);
/* 157*/    this.FUNCTION_INSTANCE_TYPE = this.registry.getNativeFunctionType(JSTypeNative.FUNCTION_INSTANCE_TYPE);
/* 159*/    this.FUNCTION_PROTOTYPE = this.registry.getNativeObjectType(JSTypeNative.FUNCTION_PROTOTYPE);
/* 161*/    this.GREATEST_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.GREATEST_FUNCTION_TYPE);
/* 163*/    this.LEAST_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.LEAST_FUNCTION_TYPE);
/* 165*/    this.NULL_TYPE = this.registry.getNativeType(JSTypeNative.NULL_TYPE);
/* 167*/    this.NUMBER_OBJECT_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.NUMBER_OBJECT_FUNCTION_TYPE);
/* 169*/    this.NUMBER_OBJECT_TYPE = this.registry.getNativeObjectType(JSTypeNative.NUMBER_OBJECT_TYPE);
/* 171*/    this.NUMBER_STRING_BOOLEAN = this.registry.getNativeType(JSTypeNative.NUMBER_STRING_BOOLEAN);
/* 173*/    this.NUMBER_TYPE = this.registry.getNativeType(JSTypeNative.NUMBER_TYPE);
/* 175*/    this.OBJECT_FUNCTION_TYPE = this.registry.getNativeFunctionType(JSTypeNative.OBJECT_FUNCTION_TYPE);
/* 177*/    this.NULL_VOID = this.registry.getNativeType(JSTypeNative.NULL_VOID);
/* 179*/    this.OBJECT_NUMBER_STRING = this.registry.getNativeType(JSTypeNative.OBJECT_NUMBER_STRING);
/* 181*/    this.OBJECT_NUMBER_STRING_BOOLEAN = this.ALL_TYPE;
/* 183*/    this.OBJECT_PROTOTYPE = this.registry.getNativeType(JSTypeNative.OBJECT_PROTOTYPE);
/* 185*/    this.OBJECT_TYPE = this.registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
/* 187*/    this.RANGE_ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.RANGE_ERROR_FUNCTION_TYPE);
/* 189*/    this.RANGE_ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.RANGE_ERROR_TYPE);
/* 191*/    this.REFERENCE_ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.REFERENCE_ERROR_FUNCTION_TYPE);
/* 193*/    this.REFERENCE_ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.REFERENCE_ERROR_TYPE);
/* 195*/    this.REGEXP_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.REGEXP_FUNCTION_TYPE);
/* 197*/    this.REGEXP_TYPE = this.registry.getNativeObjectType(JSTypeNative.REGEXP_TYPE);
/* 199*/    this.STRING_OBJECT_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.STRING_OBJECT_FUNCTION_TYPE);
/* 201*/    this.STRING_OBJECT_TYPE = this.registry.getNativeObjectType(JSTypeNative.STRING_OBJECT_TYPE);
/* 203*/    this.STRING_TYPE = this.registry.getNativeType(JSTypeNative.STRING_TYPE);
/* 205*/    this.SYNTAX_ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.SYNTAX_ERROR_FUNCTION_TYPE);
/* 207*/    this.SYNTAX_ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.SYNTAX_ERROR_TYPE);
/* 209*/    this.TYPE_ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.TYPE_ERROR_FUNCTION_TYPE);
/* 211*/    this.TYPE_ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.TYPE_ERROR_TYPE);
/* 213*/    this.U2U_CONSTRUCTOR_TYPE = this.registry.getNativeFunctionType(JSTypeNative.U2U_CONSTRUCTOR_TYPE);
/* 215*/    this.U2U_FUNCTION_TYPE = this.registry.getNativeFunctionType(JSTypeNative.U2U_FUNCTION_TYPE);
/* 217*/    this.UNKNOWN_TYPE = this.registry.getNativeObjectType(JSTypeNative.UNKNOWN_TYPE);
/* 219*/    this.URI_ERROR_FUNCTION_TYPE = this.registry.getNativeType(JSTypeNative.URI_ERROR_FUNCTION_TYPE);
/* 221*/    this.URI_ERROR_TYPE = this.registry.getNativeObjectType(JSTypeNative.URI_ERROR_TYPE);
/* 223*/    this.VOID_TYPE = this.registry.getNativeType(JSTypeNative.VOID_TYPE);
/* 226*/    addNativeProperties(this.registry);
/* 228*/    this.NATIVE_PROPERTIES_COUNT = this.OBJECT_TYPE.getPropertiesCount();
/*   0*/  }
/*   0*/  
/*   0*/  public static void addNativeProperties(JSTypeRegistry registry) {
/* 233*/    JSType booleanType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
/* 234*/    JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
/* 235*/    JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
/* 236*/    JSType unknownType = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
/* 238*/    ObjectType objectType = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
/* 240*/    ObjectType arrayType = registry.getNativeObjectType(JSTypeNative.ARRAY_TYPE);
/* 242*/    ObjectType dateType = registry.getNativeObjectType(JSTypeNative.DATE_TYPE);
/* 244*/    ObjectType regexpType = registry.getNativeObjectType(JSTypeNative.REGEXP_TYPE);
/* 246*/    ObjectType booleanObjectType = registry.getNativeObjectType(JSTypeNative.BOOLEAN_OBJECT_TYPE);
/* 248*/    ObjectType numberObjectType = registry.getNativeObjectType(JSTypeNative.NUMBER_OBJECT_TYPE);
/* 250*/    ObjectType stringObjectType = registry.getNativeObjectType(JSTypeNative.STRING_OBJECT_TYPE);
/* 253*/    ObjectType objectPrototype = registry.getNativeFunctionType(JSTypeNative.OBJECT_FUNCTION_TYPE).getPrototype();
/* 256*/    addMethod(registry, objectPrototype, "constructor", objectType);
/* 257*/    addMethod(registry, objectPrototype, "toString", stringType);
/* 258*/    addMethod(registry, objectPrototype, "toLocaleString", stringType);
/* 259*/    addMethod(registry, objectPrototype, "valueOf", unknownType);
/* 260*/    addMethod(registry, objectPrototype, "hasOwnProperty", booleanType);
/* 261*/    addMethod(registry, objectPrototype, "isPrototypeOf", booleanType);
/* 262*/    addMethod(registry, objectPrototype, "propertyIsEnumerable", booleanType);
/* 264*/    ObjectType arrayPrototype = registry.getNativeFunctionType(JSTypeNative.ARRAY_FUNCTION_TYPE).getPrototype();
/* 267*/    addMethod(registry, arrayPrototype, "constructor", arrayType);
/* 268*/    addMethod(registry, arrayPrototype, "toString", stringType);
/* 269*/    addMethod(registry, arrayPrototype, "toLocaleString", stringType);
/* 270*/    addMethod(registry, arrayPrototype, "concat", arrayType);
/* 271*/    addMethod(registry, arrayPrototype, "join", stringType);
/* 272*/    addMethod(registry, arrayPrototype, "pop", unknownType);
/* 273*/    addMethod(registry, arrayPrototype, "push", numberType);
/* 274*/    addMethod(registry, arrayPrototype, "reverse", arrayType);
/* 275*/    addMethod(registry, arrayPrototype, "shift", unknownType);
/* 276*/    addMethod(registry, arrayPrototype, "slice", arrayType);
/* 277*/    addMethod(registry, arrayPrototype, "sort", arrayType);
/* 278*/    addMethod(registry, arrayPrototype, "splice", arrayType);
/* 279*/    addMethod(registry, arrayPrototype, "unshift", numberType);
/* 280*/    arrayType.defineDeclaredProperty("length", numberType, null);
/* 282*/    ObjectType booleanPrototype = registry.getNativeFunctionType(JSTypeNative.BOOLEAN_OBJECT_FUNCTION_TYPE).getPrototype();
/* 285*/    addMethod(registry, booleanPrototype, "constructor", booleanObjectType);
/* 286*/    addMethod(registry, booleanPrototype, "toString", stringType);
/* 287*/    addMethod(registry, booleanPrototype, "valueOf", booleanType);
/* 289*/    ObjectType datePrototype = registry.getNativeFunctionType(JSTypeNative.DATE_FUNCTION_TYPE).getPrototype();
/* 292*/    addMethod(registry, datePrototype, "constructor", dateType);
/* 293*/    addMethod(registry, datePrototype, "toString", stringType);
/* 294*/    addMethod(registry, datePrototype, "toDateString", stringType);
/* 295*/    addMethod(registry, datePrototype, "toTimeString", stringType);
/* 296*/    addMethod(registry, datePrototype, "toLocaleString", stringType);
/* 297*/    addMethod(registry, datePrototype, "toLocaleDateString", stringType);
/* 298*/    addMethod(registry, datePrototype, "toLocaleTimeString", stringType);
/* 299*/    addMethod(registry, datePrototype, "valueOf", numberType);
/* 300*/    addMethod(registry, datePrototype, "getTime", numberType);
/* 301*/    addMethod(registry, datePrototype, "getFullYear", numberType);
/* 302*/    addMethod(registry, datePrototype, "getUTCFullYear", numberType);
/* 303*/    addMethod(registry, datePrototype, "getMonth", numberType);
/* 304*/    addMethod(registry, datePrototype, "getUTCMonth", numberType);
/* 305*/    addMethod(registry, datePrototype, "getDate", numberType);
/* 306*/    addMethod(registry, datePrototype, "getUTCDate", numberType);
/* 307*/    addMethod(registry, datePrototype, "getDay", numberType);
/* 308*/    addMethod(registry, datePrototype, "getUTCDay", numberType);
/* 309*/    addMethod(registry, datePrototype, "getHours", numberType);
/* 310*/    addMethod(registry, datePrototype, "getUTCHours", numberType);
/* 311*/    addMethod(registry, datePrototype, "getMinutes", numberType);
/* 312*/    addMethod(registry, datePrototype, "getUTCMinutes", numberType);
/* 313*/    addMethod(registry, datePrototype, "getSeconds", numberType);
/* 314*/    addMethod(registry, datePrototype, "getUTCSeconds", numberType);
/* 315*/    addMethod(registry, datePrototype, "getMilliseconds", numberType);
/* 316*/    addMethod(registry, datePrototype, "getUTCMilliseconds", numberType);
/* 317*/    addMethod(registry, datePrototype, "getTimezoneOffset", numberType);
/* 318*/    addMethod(registry, datePrototype, "setTime", numberType);
/* 319*/    addMethod(registry, datePrototype, "setMilliseconds", numberType);
/* 320*/    addMethod(registry, datePrototype, "setUTCMilliseconds", numberType);
/* 321*/    addMethod(registry, datePrototype, "setSeconds", numberType);
/* 322*/    addMethod(registry, datePrototype, "setUTCSeconds", numberType);
/* 323*/    addMethod(registry, datePrototype, "setMinutes", numberType);
/* 324*/    addMethod(registry, datePrototype, "setUTCMinutes", numberType);
/* 325*/    addMethod(registry, datePrototype, "setHours", numberType);
/* 326*/    addMethod(registry, datePrototype, "setUTCHours", numberType);
/* 327*/    addMethod(registry, datePrototype, "setDate", numberType);
/* 328*/    addMethod(registry, datePrototype, "setUTCDate", numberType);
/* 329*/    addMethod(registry, datePrototype, "setMonth", numberType);
/* 330*/    addMethod(registry, datePrototype, "setUTCMonth", numberType);
/* 331*/    addMethod(registry, datePrototype, "setFullYear", numberType);
/* 332*/    addMethod(registry, datePrototype, "setUTCFullYear", numberType);
/* 333*/    addMethod(registry, datePrototype, "toUTCString", stringType);
/* 334*/    addMethod(registry, datePrototype, "toGMTString", stringType);
/* 336*/    ObjectType numberPrototype = registry.getNativeFunctionType(JSTypeNative.NUMBER_OBJECT_FUNCTION_TYPE).getPrototype();
/* 339*/    addMethod(registry, numberPrototype, "constructor", numberObjectType);
/* 340*/    addMethod(registry, numberPrototype, "toString", stringType);
/* 341*/    addMethod(registry, numberPrototype, "toLocaleString", stringType);
/* 342*/    addMethod(registry, numberPrototype, "valueOf", numberType);
/* 343*/    addMethod(registry, numberPrototype, "toFixed", stringType);
/* 344*/    addMethod(registry, numberPrototype, "toExponential", stringType);
/* 345*/    addMethod(registry, numberPrototype, "toPrecision", stringType);
/* 347*/    ObjectType regexpPrototype = registry.getNativeFunctionType(JSTypeNative.REGEXP_FUNCTION_TYPE).getPrototype();
/* 350*/    addMethod(registry, regexpPrototype, "constructor", regexpType);
/* 351*/    addMethod(registry, regexpPrototype, "exec", registry.createNullableType(arrayType));
/* 353*/    addMethod(registry, regexpPrototype, "test", booleanType);
/* 354*/    addMethod(registry, regexpPrototype, "toString", stringType);
/* 355*/    regexpType.defineDeclaredProperty("source", stringType, null);
/* 356*/    regexpType.defineDeclaredProperty("global", booleanType, null);
/* 357*/    regexpType.defineDeclaredProperty("ignoreCase", booleanType, null);
/* 358*/    regexpType.defineDeclaredProperty("multiline", booleanType, null);
/* 359*/    regexpType.defineDeclaredProperty("lastIndex", numberType, null);
/* 361*/    ObjectType stringPrototype = registry.getNativeFunctionType(JSTypeNative.STRING_OBJECT_FUNCTION_TYPE).getPrototype();
/* 364*/    addMethod(registry, stringPrototype, "constructor", stringObjectType);
/* 365*/    addMethod(registry, stringPrototype, "toString", stringType);
/* 366*/    addMethod(registry, stringPrototype, "valueOf", stringType);
/* 367*/    addMethod(registry, stringPrototype, "charAt", stringType);
/* 368*/    addMethod(registry, stringPrototype, "charCodeAt", numberType);
/* 369*/    addMethod(registry, stringPrototype, "concat", stringType);
/* 370*/    addMethod(registry, stringPrototype, "indexOf", numberType);
/* 371*/    addMethod(registry, stringPrototype, "lastIndexOf", numberType);
/* 372*/    addMethod(registry, stringPrototype, "localeCompare", numberType);
/* 373*/    addMethod(registry, stringPrototype, "match", registry.createNullableType(arrayType));
/* 375*/    addMethod(registry, stringPrototype, "replace", stringType);
/* 376*/    addMethod(registry, stringPrototype, "search", numberType);
/* 377*/    addMethod(registry, stringPrototype, "slice", stringType);
/* 378*/    addMethod(registry, stringPrototype, "split", arrayType);
/* 379*/    addMethod(registry, stringPrototype, "substring", stringType);
/* 380*/    addMethod(registry, stringPrototype, "toLowerCase", stringType);
/* 381*/    addMethod(registry, stringPrototype, "toLocaleLowerCase", stringType);
/* 382*/    addMethod(registry, stringPrototype, "toUpperCase", stringType);
/* 383*/    addMethod(registry, stringPrototype, "toLocaleUpperCase", stringType);
/* 384*/    stringObjectType.defineDeclaredProperty("length", numberType, null);
/*   0*/  }
/*   0*/  
/*   0*/  private static void addMethod(JSTypeRegistry registry, ObjectType receivingType, String methodName, JSType returnType) {
/* 390*/    receivingType.defineDeclaredProperty(methodName, new FunctionBuilder(registry).withReturnType(returnType).build(), null);
/*   0*/  }
/*   0*/  
/*   0*/  protected JSType createUnionType(JSType... variants) {
/* 396*/    return this.registry.createUnionType(variants);
/*   0*/  }
/*   0*/  
/*   0*/  protected RecordTypeBuilder createRecordTypeBuilder() {
/* 400*/    return new RecordTypeBuilder(this.registry);
/*   0*/  }
/*   0*/  
/*   0*/  protected JSType createNullableType(JSType type) {
/* 404*/    return this.registry.createNullableType(type);
/*   0*/  }
/*   0*/  
/*   0*/  protected JSType createOptionalType(JSType type) {
/* 408*/    return this.registry.createOptionalType(type);
/*   0*/  }
/*   0*/  
/*   0*/  protected void assertTypeEquals(JSType expected, Node actual) {
/* 416*/    assertTypeEquals(expected, new JSTypeExpression(actual, ""));
/*   0*/  }
/*   0*/  
/*   0*/  protected void assertTypeEquals(JSType expected, JSTypeExpression actual) {
/* 423*/    assertEquals(expected, resolve(actual, new String[0]));
/*   0*/  }
/*   0*/  
/*   0*/  protected JSType resolve(JSTypeExpression n, String... warnings) {
/* 430*/    this.errorReporter.setWarnings(warnings);
/* 431*/    return n.evaluate(null, this.registry);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void assertTypeEquals(JSType a, JSType b) {
/* 577*/    Asserts.assertTypeEquals(a, b);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void assertTypeEquals(String msg, JSType a, JSType b) {
/* 581*/    Asserts.assertTypeEquals(msg, a, b);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void assertTypeNotEquals(JSType a, JSType b) {
/* 585*/    Asserts.assertTypeNotEquals(a, b);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void assertTypeNotEquals(String msg, JSType a, JSType b) {
/* 589*/    Asserts.assertTypeNotEquals(msg, a, b);
/*   0*/  }
/*   0*/  
/*   0*/  protected final ParameterizedType parameterize(ObjectType objType, JSType t) {
/* 593*/    return this.registry.createParameterizedType(objType, t);
/*   0*/  }
/*   0*/}
