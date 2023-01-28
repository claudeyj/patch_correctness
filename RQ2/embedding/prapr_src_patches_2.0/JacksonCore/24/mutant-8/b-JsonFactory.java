/*   0*/package com.fasterxml.jackson.core;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.format.InputAccessor;
/*   0*/import com.fasterxml.jackson.core.format.MatchStrength;
/*   0*/import com.fasterxml.jackson.core.io.CharacterEscapes;
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
/*   0*/import com.fasterxml.jackson.core.util.BufferRecyclers;
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
/*   0*/import java.net.URL;
/*   0*/
/*   0*/public class JsonFactory extends TokenStreamFactory implements Versioned, Serializable {
/*   0*/  private static final long serialVersionUID = 2L;
/*   0*/  
/*   0*/  public static final String FORMAT_NAME_JSON = "JSON";
/*   0*/  
/*   0*/  public enum Feature {
/*  79*/    INTERN_FIELD_NAMES(true),
/*  89*/    CANONICALIZE_FIELD_NAMES(true),
/* 105*/    FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
/* 122*/    USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true);
/*   0*/    
/*   0*/    private final boolean _defaultState;
/*   0*/    
/*   0*/    public static int collectDefaults() {
/* 136*/      int flags = 0;
/* 137*/      for (Feature f : values()) {
/* 138*/        if (f.enabledByDefault()) {
/* 138*/            flags |= f.getMask(); 
/*   0*/           }
/*   0*/      } 
/* 140*/      return flags;
/*   0*/    }
/*   0*/    
/*   0*/    Feature(boolean defaultState) {
/* 143*/      this._defaultState = defaultState;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean enabledByDefault() {
/* 145*/      return this._defaultState;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean enabledIn(int flags) {
/* 146*/      return ((flags & getMask()) != 0);
/*   0*/    }
/*   0*/    
/*   0*/    public int getMask() {
/* 147*/      return 1 << ordinal();
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 165*/  protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
/*   0*/  
/* 171*/  protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*   0*/  
/* 177*/  protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*   0*/  
/* 179*/  public static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*   0*/  
/* 192*/  protected final transient CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
/*   0*/  
/* 203*/  protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
/*   0*/  
/* 214*/  protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*   0*/  
/* 219*/  protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*   0*/  
/* 224*/  protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*   0*/  
/*   0*/  protected ObjectCodec _objectCodec;
/*   0*/  
/*   0*/  protected CharacterEscapes _characterEscapes;
/*   0*/  
/*   0*/  protected InputDecorator _inputDecorator;
/*   0*/  
/*   0*/  protected OutputDecorator _outputDecorator;
/*   0*/  
/* 267*/  protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
/*   0*/  
/*   0*/  protected int _maximumNonEscapedChar;
/*   0*/  
/*   0*/  public JsonFactory() {
/* 295*/    this(null, objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory(ObjectCodec oc) {
/* 297*/    this._objectCodec = oc;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonFactory(JsonFactory src, ObjectCodec codec) {
/* 306*/    this._objectCodec = codec;
/* 307*/    this._factoryFeatures = src._factoryFeatures;
/* 308*/    this._parserFeatures = src._parserFeatures;
/* 309*/    this._generatorFeatures = src._generatorFeatures;
/* 310*/    this._characterEscapes = src._characterEscapes;
/* 311*/    this._inputDecorator = src._inputDecorator;
/* 312*/    this._outputDecorator = src._outputDecorator;
/* 313*/    this._rootValueSeparator = src._rootValueSeparator;
/* 314*/    this._maximumNonEscapedChar = src._maximumNonEscapedChar;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory(JsonFactoryBuilder b) {
/* 323*/    this(b, false);
/* 324*/    this._characterEscapes = b._characterEscapes;
/* 325*/    this._rootValueSeparator = b._rootValueSeparator;
/* 326*/    this._maximumNonEscapedChar = b._maximumNonEscapedChar;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus) {
/* 338*/    this._objectCodec = null;
/* 339*/    this._factoryFeatures = b._factoryFeatures;
/* 340*/    this._parserFeatures = b._streamReadFeatures;
/* 341*/    this._generatorFeatures = b._streamWriteFeatures;
/* 342*/    this._inputDecorator = b._inputDecorator;
/* 343*/    this._outputDecorator = b._outputDecorator;
/* 345*/    this._maximumNonEscapedChar = 0;
/*   0*/  }
/*   0*/  
/*   0*/  public TSFBuilder<?, ?> rebuild() {
/* 356*/    _requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
/* 357*/    return new JsonFactoryBuilder(this);
/*   0*/  }
/*   0*/  
/*   0*/  public static TSFBuilder<?, ?> builder() {
/* 370*/    return new JsonFactoryBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory copy() {
/* 389*/    _checkInvalidCopy(JsonFactory.class);
/* 391*/    return new JsonFactory(this, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkInvalidCopy(Class<?> exp) {
/* 399*/    if (getClass() != exp) {
/* 400*/        throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected Object readResolve() {
/* 417*/    return new JsonFactory(this, this._objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresPropertyOrdering() {
/* 442*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canHandleBinaryNatively() {
/* 457*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseCharArrays() {
/* 471*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canParseAsync() {
/* 485*/    return _isJSONFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatReadFeatureType() {
/* 490*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/* 495*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseSchema(FormatSchema schema) {
/* 516*/    if (schema == null) {
/* 517*/        return false; 
/*   0*/       }
/* 519*/    String ourFormat = getFormatName();
/* 520*/    return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
/*   0*/  }
/*   0*/  
/*   0*/  public String getFormatName() {
/* 537*/    if (getClass() == JsonFactory.class) {
/* 538*/        return "JSON"; 
/*   0*/       }
/* 540*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/* 550*/    if (getClass() == JsonFactory.class) {
/* 551*/        return hasJSONFormat(acc); 
/*   0*/       }
/* 553*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresCustomCodec() {
/* 570*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 579*/    return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 590*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public final JsonFactory configure(Feature f, boolean state) {
/* 607*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory enable(Feature f) {
/* 618*/    this._factoryFeatures |= f.getMask();
/* 619*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory disable(Feature f) {
/* 630*/    this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 631*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(Feature f) {
/* 638*/    return ((this._factoryFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final int getParserFeatures() {
/* 643*/    return this._parserFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public final int getGeneratorFeatures() {
/* 648*/    return this._generatorFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public int getFormatParserFeatures() {
/* 654*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public int getFormatGeneratorFeatures() {
/* 660*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/* 674*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonParser.Feature f) {
/* 682*/    this._parserFeatures |= f.getMask();
/* 683*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonParser.Feature f) {
/* 691*/    this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 692*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonParser.Feature f) {
/* 700*/    return ((this._parserFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(StreamReadFeature f) {
/* 707*/    return ((this._parserFeatures & f.mappedFeature().getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public InputDecorator getInputDecorator() {
/* 715*/    return this._inputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory setInputDecorator(InputDecorator d) {
/* 725*/    this._inputDecorator = d;
/* 726*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/* 740*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonGenerator.Feature f) {
/* 748*/    this._generatorFeatures |= f.getMask();
/* 749*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonGenerator.Feature f) {
/* 757*/    this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 758*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f) {
/* 766*/    return ((this._generatorFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(StreamWriteFeature f) {
/* 773*/    return ((this._generatorFeatures & f.mappedFeature().getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterEscapes getCharacterEscapes() {
/* 780*/    return this._characterEscapes;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/* 787*/    this._characterEscapes = esc;
/* 788*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public OutputDecorator getOutputDecorator() {
/* 796*/    return this._outputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory setOutputDecorator(OutputDecorator d) {
/* 806*/    this._outputDecorator = d;
/* 807*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setRootValueSeparator(String sep) {
/* 820*/    this._rootValueSeparator = (sep == null) ? null : new SerializedString(sep);
/* 821*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRootValueSeparator() {
/* 828*/    return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCodec(ObjectCodec oc) {
/* 845*/    this._objectCodec = oc;
/* 846*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 849*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(File f) throws IOException, JsonParseException {
/* 880*/    IOContext ctxt = _createContext(f, true);
/* 881*/    InputStream in = new FileInputStream(f);
/* 882*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(URL url) throws IOException, JsonParseException {
/* 908*/    IOContext ctxt = _createContext(url, true);
/* 909*/    InputStream in = _optimizedStreamFromURL(url);
/* 910*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/* 936*/    IOContext ctxt = _createContext(in, false);
/* 937*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/* 957*/    IOContext ctxt = _createContext(r, false);
/* 958*/    return _createParser(_decorate(r, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 969*/    IOContext ctxt = _createContext(data, true);
/* 970*/    if (this._inputDecorator != null) {
/* 971*/      InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 972*/      if (in != null) {
/* 973*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 976*/    return _createParser(data, 0, data.length, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 991*/    IOContext ctxt = _createContext(data, true);
/* 993*/    if (this._inputDecorator != null) {
/* 994*/      InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 995*/      if (in != null) {
/* 996*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 999*/    return _createParser(data, offset, len, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(String content) throws IOException, JsonParseException {
/*1010*/    int strLen = content.length();
/*1012*/    if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays()) {
/*1015*/        return createParser(new StringReader(content)); 
/*   0*/       }
/*1017*/    IOContext ctxt = _createContext(content, true);
/*1018*/    char[] buf = ctxt.allocTokenBuffer(strLen);
/*1019*/    content.getChars(0, strLen, buf, 0);
/*1020*/    return _createParser(buf, 0, strLen, ctxt, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content) throws IOException {
/*1031*/    return createParser(content, 0, content.length);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/*1041*/    if (this._inputDecorator != null) {
/*1042*/        return createParser(new CharArrayReader(content, offset, len)); 
/*   0*/       }
/*1044*/    return _createParser(content, offset, len, _createContext(content, true), false);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(DataInput in) throws IOException {
/*1060*/    IOContext ctxt = _createContext(in, false);
/*1061*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createNonBlockingByteArrayParser() throws IOException {
/*1087*/    _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
/*1088*/    IOContext ctxt = _createNonBlockingContext(null);
/*1089*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1090*/    return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1126*/    IOContext ctxt = _createContext(out, false);
/*1127*/    ctxt.setEncoding(enc);
/*1128*/    if (enc == JsonEncoding.UTF8) {
/*1129*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1131*/    Writer w = _createWriter(out, enc, ctxt);
/*1132*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out) throws IOException {
/*1145*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(Writer w) throws IOException {
/*1165*/    IOContext ctxt = _createContext(w, false);
/*1166*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/*1188*/    OutputStream out = new FileOutputStream(f);
/*1190*/    IOContext ctxt = _createContext(out, true);
/*1191*/    ctxt.setEncoding(enc);
/*1192*/    if (enc == JsonEncoding.UTF8) {
/*1193*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1195*/    Writer w = _createWriter(out, enc, ctxt);
/*1196*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/*1207*/    return createGenerator(_createDataOutputWrapper(out), enc);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out) throws IOException {
/*1220*/    return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/*1250*/    return createParser(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/*1275*/    return createParser(url);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/*1301*/    return createParser(in);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/*1320*/    return createParser(r);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/*1330*/    return createParser(data);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/*1345*/    return createParser(data, offset, len);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/*1356*/    return createParser(content);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1389*/    return createGenerator(out, enc);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/*1409*/    return createGenerator(out);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/*1422*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/*1446*/    return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/*1463*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures));
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/*1475*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/*1493*/    return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/*1506*/    _requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
/*1509*/    int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/*1510*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1511*/    return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/*1534*/    WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1536*/    if (this._maximumNonEscapedChar > 0) {
/*1537*/        gen.setHighestNonEscapedChar(this._maximumNonEscapedChar); 
/*   0*/       }
/*1539*/    if (this._characterEscapes != null) {
/*1540*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1542*/    SerializableString rootSep = this._rootValueSeparator;
/*1543*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1544*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1546*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/*1560*/    UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1562*/    if (this._maximumNonEscapedChar > 0) {
/*1563*/        gen.setHighestNonEscapedChar(this._maximumNonEscapedChar); 
/*   0*/       }
/*1565*/    if (this._characterEscapes != null) {
/*1566*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1568*/    SerializableString rootSep = this._rootValueSeparator;
/*1569*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1570*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1572*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/*1578*/    if (enc == JsonEncoding.UTF8) {
/*1579*/        return new UTF8Writer(ctxt, out); 
/*   0*/       }
/*1582*/    return new OutputStreamWriter(out, enc.getJavaName());
/*   0*/  }
/*   0*/  
/*   0*/  protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/*1595*/    if (this._inputDecorator != null) {
/*1596*/      InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/*1597*/      if (in2 != null) {
/*1598*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1601*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/*1608*/    if (this._inputDecorator != null) {
/*1609*/      Reader in2 = this._inputDecorator.decorate(ctxt, in);
/*1610*/      if (in2 != null) {
/*1611*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1614*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/*1621*/    if (this._inputDecorator != null) {
/*1622*/      DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/*1623*/      if (in2 != null) {
/*1624*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1627*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/*1634*/    if (this._outputDecorator != null) {
/*1635*/      OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/*1636*/      if (out2 != null) {
/*1637*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1640*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/*1647*/    if (this._outputDecorator != null) {
/*1648*/      Writer out2 = this._outputDecorator.decorate(ctxt, out);
/*1649*/      if (out2 != null) {
/*1650*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1653*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferRecycler _getBufferRecycler() {
/*1674*/    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/*1675*/        return BufferRecyclers.getBufferRecycler(); 
/*   0*/       }
/*1677*/    return new BufferRecycler();
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
/*1685*/    return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createNonBlockingContext(Object srcRef) {
/*1697*/    BufferRecycler recycler = new BufferRecycler();
/*1698*/    return new IOContext(recycler, srcRef, false);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _requireJSONFactory(String msg) {
/*1720*/    if (!_isJSONFactory()) {
/*1721*/        throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final boolean _isJSONFactory() {
/*1728*/    return (getFormatName() == "JSON");
/*   0*/  }
/*   0*/}
