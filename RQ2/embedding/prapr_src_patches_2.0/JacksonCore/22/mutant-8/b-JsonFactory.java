/*   0*/package com.fasterxml.jackson.core;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.format.InputAccessor;
/*   0*/import com.fasterxml.jackson.core.format.MatchStrength;
/*   0*/import com.fasterxml.jackson.core.io.CharacterEscapes;
/*   0*/import com.fasterxml.jackson.core.io.DataOutputAsStream;
/*   0*/import com.fasterxml.jackson.core.io.IOContext;
/*   0*/import com.fasterxml.jackson.core.io.InputDecorator;
/*   0*/import com.fasterxml.jackson.core.io.OutputDecorator;
/*   0*/import com.fasterxml.jackson.core.io.SerializedString;
/*   0*/import com.fasterxml.jackson.core.io.UTF8Writer;
/*   0*/import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
/*   0*/import com.fasterxml.jackson.core.json.PackageVersion;
/*   0*/import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
/*   0*/import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
/*   0*/import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
/*   0*/import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
/*   0*/import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
/*   0*/import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*   0*/import com.fasterxml.jackson.core.util.BufferRecycler;
/*   0*/import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*   0*/import java.io.CharArrayReader;
/*   0*/import java.io.DataInput;
/*   0*/import java.io.DataOutput;
/*   0*/import java.io.File;
/*   0*/import java.io.FileInputStream;
/*   0*/import java.io.FileOutputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.io.OutputStreamWriter;
/*   0*/import java.io.Reader;
/*   0*/import java.io.Serializable;
/*   0*/import java.io.StringReader;
/*   0*/import java.io.Writer;
/*   0*/import java.lang.ref.SoftReference;
/*   0*/import java.net.URL;
/*   0*/
/*   0*/public class JsonFactory implements Versioned, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public static final String FORMAT_NAME_JSON = "JSON";
/*   0*/  
/*   0*/  public enum Feature {
/*  77*/    INTERN_FIELD_NAMES(true),
/*  87*/    CANONICALIZE_FIELD_NAMES(true),
/* 103*/    FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
/* 120*/    USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true);
/*   0*/    
/*   0*/    private final boolean _defaultState;
/*   0*/    
/*   0*/    public static int collectDefaults() {
/* 134*/      int flags = 0;
/* 135*/      for (Feature f : values()) {
/* 136*/        if (f.enabledByDefault()) {
/* 136*/            flags |= f.getMask(); 
/*   0*/           }
/*   0*/      } 
/* 138*/      return flags;
/*   0*/    }
/*   0*/    
/*   0*/    Feature(boolean defaultState) {
/* 141*/      this._defaultState = defaultState;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean enabledByDefault() {
/* 143*/      return this._defaultState;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean enabledIn(int flags) {
/* 144*/      return ((flags & getMask()) != 0);
/*   0*/    }
/*   0*/    
/*   0*/    public int getMask() {
/* 145*/      return 1 << ordinal();
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 163*/  protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
/*   0*/  
/* 169*/  protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*   0*/  
/* 175*/  protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*   0*/  
/* 177*/  private static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*   0*/  
/* 190*/  protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();
/*   0*/  
/* 198*/  protected final transient CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
/*   0*/  
/* 209*/  protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
/*   0*/  
/*   0*/  protected ObjectCodec _objectCodec;
/*   0*/  
/* 229*/  protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*   0*/  
/* 234*/  protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*   0*/  
/* 239*/  protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*   0*/  
/*   0*/  protected CharacterEscapes _characterEscapes;
/*   0*/  
/*   0*/  protected InputDecorator _inputDecorator;
/*   0*/  
/*   0*/  protected OutputDecorator _outputDecorator;
/*   0*/  
/* 267*/  protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
/*   0*/  
/*   0*/  public JsonFactory() {
/* 285*/    this(null, objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory(ObjectCodec oc) {
/* 287*/    this._objectCodec = oc;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonFactory(JsonFactory src, ObjectCodec codec) {
/* 296*/    this._objectCodec = null;
/* 297*/    this._factoryFeatures = src._factoryFeatures;
/* 298*/    this._parserFeatures = src._parserFeatures;
/* 299*/    this._generatorFeatures = src._generatorFeatures;
/* 300*/    this._characterEscapes = src._characterEscapes;
/* 301*/    this._inputDecorator = src._inputDecorator;
/* 302*/    this._outputDecorator = src._outputDecorator;
/* 303*/    this._rootValueSeparator = src._rootValueSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory copy() {
/* 327*/    _checkInvalidCopy(JsonFactory.class);
/* 329*/    return new JsonFactory(this, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkInvalidCopy(Class<?> exp) {
/* 338*/    if (getClass() != exp) {
/* 339*/        throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected Object readResolve() {
/* 356*/    return new JsonFactory(this, this._objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresPropertyOrdering() {
/* 380*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canHandleBinaryNatively() {
/* 394*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseCharArrays() {
/* 408*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canParseAsync() {
/* 421*/    return _isJSONFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatReadFeatureType() {
/* 432*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/* 443*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseSchema(FormatSchema schema) {
/* 462*/    if (schema == null) {
/* 463*/        return false; 
/*   0*/       }
/* 465*/    String ourFormat = getFormatName();
/* 466*/    return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
/*   0*/  }
/*   0*/  
/*   0*/  public String getFormatName() {
/* 482*/    if (getClass() == JsonFactory.class) {
/* 483*/        return "JSON"; 
/*   0*/       }
/* 485*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/* 495*/    if (getClass() == JsonFactory.class) {
/* 496*/        return hasJSONFormat(acc); 
/*   0*/       }
/* 498*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresCustomCodec() {
/* 515*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 524*/    return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 535*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(Feature f, boolean state) {
/* 549*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(Feature f) {
/* 557*/    this._factoryFeatures |= f.getMask();
/* 558*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(Feature f) {
/* 566*/    this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 567*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(Feature f) {
/* 574*/    return ((this._factoryFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/* 588*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonParser.Feature f) {
/* 596*/    this._parserFeatures |= f.getMask();
/* 597*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonParser.Feature f) {
/* 605*/    this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 606*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonParser.Feature f) {
/* 613*/    return ((this._parserFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public InputDecorator getInputDecorator() {
/* 621*/    return this._inputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setInputDecorator(InputDecorator d) {
/* 628*/    this._inputDecorator = d;
/* 629*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/* 643*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonGenerator.Feature f) {
/* 652*/    this._generatorFeatures |= f.getMask();
/* 653*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonGenerator.Feature f) {
/* 661*/    this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 662*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f) {
/* 669*/    return ((this._generatorFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterEscapes getCharacterEscapes() {
/* 676*/    return this._characterEscapes;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/* 683*/    this._characterEscapes = esc;
/* 684*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public OutputDecorator getOutputDecorator() {
/* 692*/    return this._outputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setOutputDecorator(OutputDecorator d) {
/* 699*/    this._outputDecorator = d;
/* 700*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setRootValueSeparator(String sep) {
/* 713*/    this._rootValueSeparator = (sep == null) ? null : new SerializedString(sep);
/* 714*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRootValueSeparator() {
/* 721*/    return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCodec(ObjectCodec oc) {
/* 738*/    this._objectCodec = oc;
/* 739*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 742*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(File f) throws IOException, JsonParseException {
/* 772*/    IOContext ctxt = _createContext(f, true);
/* 773*/    InputStream in = new FileInputStream(f);
/* 774*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(URL url) throws IOException, JsonParseException {
/* 799*/    IOContext ctxt = _createContext(url, true);
/* 800*/    InputStream in = _optimizedStreamFromURL(url);
/* 801*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/* 826*/    IOContext ctxt = _createContext(in, false);
/* 827*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/* 846*/    IOContext ctxt = _createContext(r, false);
/* 847*/    return _createParser(_decorate(r, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 857*/    IOContext ctxt = _createContext(data, true);
/* 858*/    if (this._inputDecorator != null) {
/* 859*/      InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 860*/      if (in != null) {
/* 861*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 864*/    return _createParser(data, 0, data.length, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 878*/    IOContext ctxt = _createContext(data, true);
/* 880*/    if (this._inputDecorator != null) {
/* 881*/      InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 882*/      if (in != null) {
/* 883*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 886*/    return _createParser(data, offset, len, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(String content) throws IOException, JsonParseException {
/* 896*/    int strLen = content.length();
/* 898*/    if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays()) {
/* 901*/        return createParser(new StringReader(content)); 
/*   0*/       }
/* 903*/    IOContext ctxt = _createContext(content, true);
/* 904*/    char[] buf = ctxt.allocTokenBuffer(strLen);
/* 905*/    content.getChars(0, strLen, buf, 0);
/* 906*/    return _createParser(buf, 0, strLen, ctxt, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content) throws IOException {
/* 916*/    return createParser(content, 0, content.length);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/* 925*/    if (this._inputDecorator != null) {
/* 926*/        return createParser(new CharArrayReader(content, offset, len)); 
/*   0*/       }
/* 928*/    return _createParser(content, offset, len, _createContext(content, true), false);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(DataInput in) throws IOException {
/* 943*/    IOContext ctxt = _createContext(in, false);
/* 944*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createNonBlockingByteArrayParser() throws IOException {
/* 969*/    _requireJSONFactory("Non-blocking source not (yet?) support for this format (%s)");
/* 970*/    IOContext ctxt = _createContext(null, false);
/* 971*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 972*/    return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/*1002*/    return createParser(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/*1027*/    return createParser(url);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/*1053*/    return createParser(in);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/*1072*/    return createParser(r);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/*1082*/    return createParser(data);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/*1097*/    return createParser(data, offset, len);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/*1108*/    return createParser(content);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1143*/    IOContext ctxt = _createContext(out, false);
/*1144*/    ctxt.setEncoding(enc);
/*1145*/    if (enc == JsonEncoding.UTF8) {
/*1146*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1148*/    Writer w = _createWriter(out, enc, ctxt);
/*1149*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out) throws IOException {
/*1161*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(Writer w) throws IOException {
/*1180*/    IOContext ctxt = _createContext(w, false);
/*1181*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/*1202*/    OutputStream out = new FileOutputStream(f);
/*1204*/    IOContext ctxt = _createContext(out, true);
/*1205*/    ctxt.setEncoding(enc);
/*1206*/    if (enc == JsonEncoding.UTF8) {
/*1207*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1209*/    Writer w = _createWriter(out, enc, ctxt);
/*1210*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/*1220*/    return createGenerator(_createDataOutputWrapper(out), enc);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out) throws IOException {
/*1232*/    return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1265*/    return createGenerator(out, enc);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/*1285*/    return createGenerator(out);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/*1298*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/*1322*/    return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/*1339*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures));
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/*1351*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/*1369*/    return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/*1382*/    _requireJSONFactory("InputData source not (yet?) support for this format (%s)");
/*1385*/    int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/*1386*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1387*/    return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/*1410*/    WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1412*/    if (this._characterEscapes != null) {
/*1413*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1415*/    SerializableString rootSep = this._rootValueSeparator;
/*1416*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1417*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1419*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/*1433*/    UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1435*/    if (this._characterEscapes != null) {
/*1436*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1438*/    SerializableString rootSep = this._rootValueSeparator;
/*1439*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1440*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1442*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/*1448*/    if (enc == JsonEncoding.UTF8) {
/*1449*/        return new UTF8Writer(ctxt, out); 
/*   0*/       }
/*1452*/    return new OutputStreamWriter(out, enc.getJavaName());
/*   0*/  }
/*   0*/  
/*   0*/  protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/*1465*/    if (this._inputDecorator != null) {
/*1466*/      InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/*1467*/      if (in2 != null) {
/*1468*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1471*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/*1478*/    if (this._inputDecorator != null) {
/*1479*/      Reader in2 = this._inputDecorator.decorate(ctxt, in);
/*1480*/      if (in2 != null) {
/*1481*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1484*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/*1491*/    if (this._inputDecorator != null) {
/*1492*/      DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/*1493*/      if (in2 != null) {
/*1494*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1497*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/*1504*/    if (this._outputDecorator != null) {
/*1505*/      OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/*1506*/      if (out2 != null) {
/*1507*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1510*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/*1517*/    if (this._outputDecorator != null) {
/*1518*/      Writer out2 = this._outputDecorator.decorate(ctxt, out);
/*1519*/      if (out2 != null) {
/*1520*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1523*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferRecycler _getBufferRecycler() {
/*1544*/    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/*1545*/      SoftReference<BufferRecycler> ref = _recyclerRef.get();
/*1546*/      BufferRecycler br = (ref == null) ? null : ref.get();
/*1548*/      if (br == null) {
/*1549*/        br = new BufferRecycler();
/*1550*/        _recyclerRef.set(new SoftReference<BufferRecycler>(br));
/*   0*/      } 
/*1552*/      return br;
/*   0*/    } 
/*1554*/    return new BufferRecycler();
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
/*1562*/    return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
/*   0*/  }
/*   0*/  
/*   0*/  protected OutputStream _createDataOutputWrapper(DataOutput out) {
/*1569*/    return new DataOutputAsStream(out);
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
/*1578*/    if ("file".equals(url.getProtocol())) {
/*1585*/      String host = url.getHost();
/*1586*/      if (host == null || host.length() == 0) {
/*1588*/        String path = url.getPath();
/*1589*/        if (path.indexOf('%') < 0) {
/*1590*/            return new FileInputStream(url.getPath()); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1596*/    return url.openStream();
/*   0*/  }
/*   0*/  
/*   0*/  private final void _requireJSONFactory(String msg) {
/*1618*/    if (!_isJSONFactory()) {
/*1619*/        throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final boolean _isJSONFactory() {
/*1626*/    return (getFormatName() == "JSON");
/*   0*/  }
/*   0*/}
