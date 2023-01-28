/*   0*/package com.fasterxml.jackson.core.base;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonParseException;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonStreamContext;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.io.NumberInput;
/*   0*/import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*   0*/import com.fasterxml.jackson.core.util.VersionUtil;
/*   0*/import java.io.IOException;
/*   0*/
/*   0*/public abstract class ParserMinimalBase extends JsonParser {
/*   0*/  protected static final int INT_TAB = 9;
/*   0*/  
/*   0*/  protected static final int INT_LF = 10;
/*   0*/  
/*   0*/  protected static final int INT_CR = 13;
/*   0*/  
/*   0*/  protected static final int INT_SPACE = 32;
/*   0*/  
/*   0*/  protected static final int INT_LBRACKET = 91;
/*   0*/  
/*   0*/  protected static final int INT_RBRACKET = 93;
/*   0*/  
/*   0*/  protected static final int INT_LCURLY = 123;
/*   0*/  
/*   0*/  protected static final int INT_RCURLY = 125;
/*   0*/  
/*   0*/  protected static final int INT_QUOTE = 34;
/*   0*/  
/*   0*/  protected static final int INT_BACKSLASH = 92;
/*   0*/  
/*   0*/  protected static final int INT_SLASH = 47;
/*   0*/  
/*   0*/  protected static final int INT_COLON = 58;
/*   0*/  
/*   0*/  protected static final int INT_COMMA = 44;
/*   0*/  
/*   0*/  protected static final int INT_HASH = 35;
/*   0*/  
/*   0*/  protected static final int INT_PERIOD = 46;
/*   0*/  
/*   0*/  protected static final int INT_e = 101;
/*   0*/  
/*   0*/  protected static final int INT_E = 69;
/*   0*/  
/*   0*/  protected JsonToken _currToken;
/*   0*/  
/*   0*/  protected JsonToken _lastClearedToken;
/*   0*/  
/*   0*/  protected ParserMinimalBase() {}
/*   0*/  
/*   0*/  protected ParserMinimalBase(int features) {
/*  73*/    super(features);
/*   0*/  }
/*   0*/  
/*   0*/  public abstract JsonToken nextToken() throws IOException;
/*   0*/  
/*   0*/  public JsonToken getCurrentToken() {
/*  98*/    return this._currToken;
/*   0*/  }
/*   0*/  
/*   0*/  public final int getCurrentTokenId() {
/* 101*/    JsonToken t = this._currToken;
/* 102*/    return (t == null) ? 0 : t.id();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasCurrentToken() {
/* 105*/    return (this._currToken != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasTokenId(int id) {
/* 107*/    JsonToken t = this._currToken;
/* 108*/    if (t == null) {
/* 109*/        return (0 == id); 
/*   0*/       }
/* 111*/    return (t.id() == id);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasToken(JsonToken t) {
/* 115*/    return (this._currToken == t);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExpectedStartArrayToken() {
/* 118*/    return (this._currToken == JsonToken.START_ARRAY);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExpectedStartObjectToken() {
/* 119*/    return (this._currToken == JsonToken.START_OBJECT);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken nextValue() throws IOException {
/* 127*/    JsonToken t = nextToken();
/* 128*/    if (t == JsonToken.FIELD_NAME) {
/* 129*/        t = nextToken(); 
/*   0*/       }
/* 131*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser skipChildren() throws IOException {
/* 137*/    if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
/* 139*/        return this; 
/*   0*/       }
/* 141*/    int open = 1;
/*   0*/    while (true) {
/* 147*/      JsonToken t = nextToken();
/* 148*/      if (t == null) {
/* 149*/        _handleEOF();
/* 154*/        return this;
/*   0*/      } 
/* 156*/      if (t.isStructStart()) {
/* 157*/        open++;
/*   0*/        continue;
/*   0*/      } 
/* 158*/      if (t.isStructEnd() && 
/* 159*/        --open == 0) {
/* 160*/          return this; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract void _handleEOF() throws JsonParseException;
/*   0*/  
/*   0*/  public abstract String getCurrentName() throws IOException;
/*   0*/  
/*   0*/  public abstract void close() throws IOException;
/*   0*/  
/*   0*/  public abstract boolean isClosed();
/*   0*/  
/*   0*/  public abstract JsonStreamContext getParsingContext();
/*   0*/  
/*   0*/  public void clearCurrentToken() {
/* 191*/    if (this._currToken != null) {
/* 192*/      this._lastClearedToken = this._currToken;
/* 193*/      this._currToken = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken getLastClearedToken() {
/* 197*/    return this._lastClearedToken;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract void overrideCurrentName(String paramString);
/*   0*/  
/*   0*/  public abstract String getText() throws IOException;
/*   0*/  
/*   0*/  public abstract char[] getTextCharacters() throws IOException;
/*   0*/  
/*   0*/  public abstract boolean hasTextCharacters();
/*   0*/  
/*   0*/  public abstract int getTextLength() throws IOException;
/*   0*/  
/*   0*/  public abstract int getTextOffset() throws IOException;
/*   0*/  
/*   0*/  public abstract byte[] getBinaryValue(Base64Variant paramBase64Variant) throws IOException;
/*   0*/  
/*   0*/  public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
/* 230*/    JsonToken t = this._currToken;
/* 231*/    if (t != null) {
/*   0*/      String str;
/*   0*/      Object value;
/* 232*/      switch (t.id()) {
/*   0*/        case 6:
/* 234*/          str = getText().trim();
/* 235*/          if ("true".equals(str)) {
/* 236*/              return true; 
/*   0*/             }
/* 238*/          if ("false".equals(str)) {
/* 239*/              return false; 
/*   0*/             }
/* 241*/          if (_hasTextualNull(str)) {
/* 242*/              return false; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 7:
/* 246*/          return (getIntValue() != 0);
/*   0*/        case 9:
/* 248*/          return true;
/*   0*/        case 10:
/*   0*/        case 11:
/* 251*/          return false;
/*   0*/        case 12:
/* 253*/          value = getEmbeddedObject();
/* 254*/          if (value instanceof Boolean) {
/* 255*/              return (Boolean)value; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 261*/    return defaultValue;
/*   0*/  }
/*   0*/  
/*   0*/  public int getValueAsInt() throws IOException {
/* 267*/    JsonToken t = this._currToken;
/* 268*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 269*/        return getIntValue(); 
/*   0*/       }
/* 271*/    if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 272*/        return getIntValue(); 
/*   0*/       }
/* 274*/    return getValueAsInt(0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getValueAsInt(int defaultValue) throws IOException {
/* 280*/    JsonToken t = this._currToken;
/* 281*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 282*/        return getIntValue(); 
/*   0*/       }
/* 284*/    if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 285*/        return getIntValue(); 
/*   0*/       }
/* 287*/    if (t != null) {
/*   0*/      String str;
/*   0*/      Object value;
/* 288*/      switch (t.id()) {
/*   0*/        case 6:
/* 290*/          str = getText();
/* 291*/          if (_hasTextualNull(str)) {
/* 292*/              return 0; 
/*   0*/             }
/* 294*/          return NumberInput.parseAsInt(str, defaultValue);
/*   0*/        case 9:
/* 296*/          return 1;
/*   0*/        case 10:
/* 298*/          return 0;
/*   0*/        case 11:
/* 300*/          return 0;
/*   0*/        case 12:
/* 302*/          value = getEmbeddedObject();
/* 303*/          if (value instanceof Number) {
/* 304*/              return ((Number)value).intValue(); 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 308*/    return defaultValue;
/*   0*/  }
/*   0*/  
/*   0*/  public long getValueAsLong() throws IOException {
/* 314*/    JsonToken t = this._currToken;
/* 315*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 316*/        return getLongValue(); 
/*   0*/       }
/* 318*/    if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 319*/        return getLongValue(); 
/*   0*/       }
/* 321*/    return getValueAsLong(0L);
/*   0*/  }
/*   0*/  
/*   0*/  public long getValueAsLong(long defaultValue) throws IOException {
/* 327*/    JsonToken t = this._currToken;
/* 328*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 329*/        return getLongValue(); 
/*   0*/       }
/* 331*/    if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 332*/        return getLongValue(); 
/*   0*/       }
/* 334*/    if (t != null) {
/*   0*/      String str;
/*   0*/      Object value;
/* 335*/      switch (t.id()) {
/*   0*/        case 6:
/* 337*/          str = getText();
/* 338*/          if (_hasTextualNull(str)) {
/* 339*/              return 0L; 
/*   0*/             }
/* 341*/          return NumberInput.parseAsLong(str, defaultValue);
/*   0*/        case 9:
/* 343*/          return 1L;
/*   0*/        case 10:
/*   0*/        case 11:
/* 346*/          return 0L;
/*   0*/        case 12:
/* 348*/          value = getEmbeddedObject();
/* 349*/          if (value instanceof Number) {
/* 350*/              return ((Number)value).longValue(); 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 354*/    return defaultValue;
/*   0*/  }
/*   0*/  
/*   0*/  public double getValueAsDouble(double defaultValue) throws IOException {
/* 360*/    JsonToken t = this._currToken;
/* 361*/    if (t != null) {
/*   0*/      String str;
/*   0*/      Object value;
/* 362*/      switch (t.id()) {
/*   0*/        case 6:
/* 364*/          str = getText();
/* 365*/          if (_hasTextualNull(str)) {
/* 366*/              return 0.0D; 
/*   0*/             }
/* 368*/          return NumberInput.parseAsDouble(str, defaultValue);
/*   0*/        case 7:
/*   0*/        case 8:
/* 371*/          return getDoubleValue();
/*   0*/        case 9:
/* 373*/          return 1.0D;
/*   0*/        case 10:
/*   0*/        case 11:
/* 376*/          return 0.0D;
/*   0*/        case 12:
/* 378*/          value = getEmbeddedObject();
/* 379*/          if (value instanceof Number) {
/* 380*/              return ((Number)value).doubleValue(); 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 384*/    return defaultValue;
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueAsString() throws IOException {
/* 389*/    if (this._currToken == JsonToken.VALUE_STRING) {
/* 390*/        return getText(); 
/*   0*/       }
/* 392*/    return getValueAsString(null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueAsString(String defaultValue) throws IOException {
/* 397*/    if (this._currToken == JsonToken.VALUE_STRING) {
/* 398*/        return getText(); 
/*   0*/       }
/* 400*/    if (this._currToken == null || this._currToken == JsonToken.VALUE_NULL || !this._currToken.isScalarValue()) {
/* 401*/        return defaultValue; 
/*   0*/       }
/* 403*/    return getText();
/*   0*/  }
/*   0*/  
/*   0*/  protected void _decodeBase64(String str, ByteArrayBuilder builder, Base64Variant b64variant) throws IOException {
/*   0*/    try {
/* 420*/      b64variant.decode(str, builder);
/* 421*/    } catch (IllegalArgumentException e) {
/* 422*/      _reportError(e.getMessage());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected void _reportInvalidBase64(Base64Variant b64variant, char ch, int bindex, String msg) throws JsonParseException {
/*   0*/    String base;
/* 437*/    if (ch <= ' ') {
/* 438*/      base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
/* 439*/    } else if (b64variant.usesPaddingChar(ch)) {
/* 440*/      base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
/* 441*/    } else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
/* 443*/      base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*   0*/    } else {
/* 445*/      base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*   0*/    } 
/* 447*/    if (msg != null) {
/* 448*/        base = base + ": " + msg; 
/*   0*/       }
/* 450*/    throw _constructError(base);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected void _reportBase64EOF() throws JsonParseException {
/* 459*/    throw _constructError("Unexpected end-of-String in base64 content");
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _hasTextualNull(String value) {
/* 475*/    return "null".equals(value);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUnexpectedChar(int ch, String comment) throws JsonParseException {
/* 485*/    if (ch < 0) {
/* 486*/        _reportInvalidEOF(); 
/*   0*/       }
/* 488*/    String msg = "Unexpected character (" + _getCharDesc(ch) + ")";
/* 489*/    if (comment != null) {
/* 490*/        msg = msg + ": " + comment; 
/*   0*/       }
/* 492*/    _reportError(msg);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportInvalidEOF() throws JsonParseException {
/* 496*/    _reportInvalidEOF(" in " + this._currToken);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportInvalidEOF(String msg) throws JsonParseException {
/* 500*/    _reportError("Unexpected end-of-input" + msg);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportInvalidEOFInValue() throws JsonParseException {
/* 504*/    _reportInvalidEOF(" in a value");
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportMissingRootWS(int ch) throws JsonParseException {
/* 508*/    _reportUnexpectedChar(ch, "Expected space separating root-level values");
/*   0*/  }
/*   0*/  
/*   0*/  protected void _throwInvalidSpace(int i) throws JsonParseException {
/* 512*/    char c = (char)i;
/* 513*/    String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
/* 514*/    _reportError(msg);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
/* 524*/    if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
/* 525*/      char c = (char)i;
/* 526*/      String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
/* 527*/      _reportError(msg);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
/* 533*/    if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
/* 534*/        return ch; 
/*   0*/       }
/* 537*/    if (ch == '\'' && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 538*/        return ch; 
/*   0*/       }
/* 540*/    _reportError("Unrecognized character escape " + _getCharDesc(ch));
/* 541*/    return ch;
/*   0*/  }
/*   0*/  
/*   0*/  protected static final String _getCharDesc(int ch) {
/* 552*/    char c = (char)ch;
/* 553*/    if (Character.isISOControl(c)) {
/* 554*/        return "(CTRL-CHAR, code " + ch + ")"; 
/*   0*/       }
/* 556*/    if (ch > 255) {
/* 557*/        return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")"; 
/*   0*/       }
/* 559*/    return "'" + c + "' (code " + ch + ")";
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _reportError(String msg) throws JsonParseException {
/* 563*/    throw _constructError(msg);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _wrapError(String msg, Throwable t) throws JsonParseException {
/* 567*/    throw _constructError(msg, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _throwInternal() {
/* 571*/    VersionUtil.throwInternal();
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonParseException _constructError(String msg, Throwable t) {
/* 575*/    return new JsonParseException(msg, getCurrentLocation(), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected static byte[] _asciiBytes(String str) {
/* 579*/    byte[] b = new byte[str.length()];
/* 580*/    for (int i = 0, len = str.length(); i < len; i++) {
/* 581*/        b[i] = (byte)str.charAt(i); 
/*   0*/       }
/* 583*/    return b;
/*   0*/  }
/*   0*/  
/*   0*/  protected static String _ascii(byte[] b) {
/*   0*/    try {
/* 588*/      return new String(b, "US-ASCII");
/* 589*/    } catch (IOException e) {
/* 590*/      throw new RuntimeException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/}
