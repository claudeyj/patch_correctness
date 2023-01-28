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
/*   0*/  public JsonFactory() {
/* 285*/    this((JsonFactoryBuilder)null);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory(ObjectCodec oc) {
/* 287*/    this._objectCodec = oc;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonFactory(JsonFactory src, ObjectCodec codec) {
/* 296*/    this._objectCodec = codec;
/* 297*/    this._factoryFeatures = src._factoryFeatures;
/* 298*/    this._parserFeatures = src._parserFeatures;
/* 299*/    this._generatorFeatures = src._generatorFeatures;
/* 300*/    this._characterEscapes = src._characterEscapes;
/* 301*/    this._inputDecorator = src._inputDecorator;
/* 302*/    this._outputDecorator = src._outputDecorator;
/* 303*/    this._rootValueSeparator = src._rootValueSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory(JsonFactoryBuilder b) {
/* 317*/    this(b, false);
/* 318*/    this._characterEscapes = b._characterEscapes;
/* 319*/    this._rootValueSeparator = b._rootValueSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonFactory(TSFBuilder<?, ?> b, boolean bogus) {
/* 331*/    this._objectCodec = null;
/* 332*/    this._factoryFeatures = b._factoryFeatures;
/* 333*/    this._parserFeatures = b._streamReadFeatures;
/* 334*/    this._generatorFeatures = b._streamWriteFeatures;
/* 335*/    this._inputDecorator = b._inputDecorator;
/* 336*/    this._outputDecorator = b._outputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public TSFBuilder<?, ?> rebuild() {
/* 347*/    _requireJSONFactory("Factory implementation for format (%s) MUST override `rebuild()` method");
/* 348*/    return new JsonFactoryBuilder(this);
/*   0*/  }
/*   0*/  
/*   0*/  public static TSFBuilder<?, ?> builder() {
/* 361*/    return new JsonFactoryBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory copy() {
/* 380*/    _checkInvalidCopy(JsonFactory.class);
/* 382*/    return new JsonFactory(this, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkInvalidCopy(Class<?> exp) {
/* 390*/    if (getClass() != exp) {
/* 391*/        throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected Object readResolve() {
/* 408*/    return new JsonFactory(this, this._objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresPropertyOrdering() {
/* 433*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canHandleBinaryNatively() {
/* 448*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseCharArrays() {
/* 462*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canParseAsync() {
/* 476*/    return _isJSONFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatReadFeatureType() {
/* 481*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/* 486*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseSchema(FormatSchema schema) {
/* 507*/    if (schema == null) {
/* 508*/        return false; 
/*   0*/       }
/* 510*/    String ourFormat = getFormatName();
/* 511*/    return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
/*   0*/  }
/*   0*/  
/*   0*/  public String getFormatName() {
/* 528*/    if (getClass() == JsonFactory.class) {
/* 529*/        return "JSON"; 
/*   0*/       }
/* 531*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/* 541*/    if (getClass() == JsonFactory.class) {
/* 542*/        return hasJSONFormat(acc); 
/*   0*/       }
/* 544*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresCustomCodec() {
/* 561*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 570*/    return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 581*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public final JsonFactory configure(Feature f, boolean state) {
/* 598*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory enable(Feature f) {
/* 609*/    this._factoryFeatures |= f.getMask();
/* 610*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory disable(Feature f) {
/* 621*/    this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 622*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(Feature f) {
/* 629*/    return ((this._factoryFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final int getParserFeatures() {
/* 634*/    return this._parserFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public final int getGeneratorFeatures() {
/* 639*/    return this._generatorFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public int getFormatParserFeatures() {
/* 645*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public int getFormatGeneratorFeatures() {
/* 651*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/* 665*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonParser.Feature f) {
/* 673*/    this._parserFeatures |= f.getMask();
/* 674*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonParser.Feature f) {
/* 682*/    this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 683*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonParser.Feature f) {
/* 691*/    return ((this._parserFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(StreamReadFeature f) {
/* 698*/    return ((this._parserFeatures & f.mappedFeature().getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public InputDecorator getInputDecorator() {
/* 706*/    return this._inputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory setInputDecorator(InputDecorator d) {
/* 716*/    this._inputDecorator = d;
/* 717*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/* 731*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonGenerator.Feature f) {
/* 739*/    this._generatorFeatures |= f.getMask();
/* 740*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonGenerator.Feature f) {
/* 748*/    this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 749*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f) {
/* 757*/    return ((this._generatorFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(StreamWriteFeature f) {
/* 764*/    return ((this._generatorFeatures & f.mappedFeature().getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterEscapes getCharacterEscapes() {
/* 771*/    return this._characterEscapes;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/* 778*/    this._characterEscapes = esc;
/* 779*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public OutputDecorator getOutputDecorator() {
/* 787*/    return this._outputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory setOutputDecorator(OutputDecorator d) {
/* 797*/    this._outputDecorator = d;
/* 798*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setRootValueSeparator(String sep) {
/* 811*/    this._rootValueSeparator = (sep == null) ? null : new SerializedString(sep);
/* 812*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRootValueSeparator() {
/* 819*/    return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCodec(ObjectCodec oc) {
/* 836*/    this._objectCodec = oc;
/* 837*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 840*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(File f) throws IOException, JsonParseException {
/* 871*/    IOContext ctxt = _createContext(f, true);
/* 872*/    InputStream in = new FileInputStream(f);
/* 873*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(URL url) throws IOException, JsonParseException {
/* 899*/    IOContext ctxt = _createContext(url, true);
/* 900*/    InputStream in = _optimizedStreamFromURL(url);
/* 901*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/* 927*/    IOContext ctxt = _createContext(in, false);
/* 928*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/* 948*/    IOContext ctxt = _createContext(r, false);
/* 949*/    return _createParser(_decorate(r, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 960*/    IOContext ctxt = _createContext(data, true);
/* 961*/    if (this._inputDecorator != null) {
/* 962*/      InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 963*/      if (in != null) {
/* 964*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 967*/    return _createParser(data, 0, data.length, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 982*/    IOContext ctxt = _createContext(data, true);
/* 984*/    if (this._inputDecorator != null) {
/* 985*/      InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 986*/      if (in != null) {
/* 987*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 990*/    return _createParser(data, offset, len, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(String content) throws IOException, JsonParseException {
/*1001*/    int strLen = content.length();
/*1003*/    if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays()) {
/*1006*/        return createParser(new StringReader(content)); 
/*   0*/       }
/*1008*/    IOContext ctxt = _createContext(content, true);
/*1009*/    char[] buf = ctxt.allocTokenBuffer(strLen);
/*1010*/    content.getChars(0, strLen, buf, 0);
/*1011*/    return _createParser(buf, 0, strLen, ctxt, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content) throws IOException {
/*1022*/    return createParser(content, 0, content.length);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/*1032*/    if (this._inputDecorator != null) {
/*1033*/        return createParser(new CharArrayReader(content, offset, len)); 
/*   0*/       }
/*1035*/    return _createParser(content, offset, len, _createContext(content, true), false);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(DataInput in) throws IOException {
/*1051*/    IOContext ctxt = _createContext(in, false);
/*1052*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createNonBlockingByteArrayParser() throws IOException {
/*1078*/    _requireJSONFactory("Non-blocking source not (yet?) supported for this format (%s)");
/*1079*/    IOContext ctxt = _createNonBlockingContext(null);
/*1080*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1081*/    return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1117*/    IOContext ctxt = _createContext(out, false);
/*1118*/    ctxt.setEncoding(enc);
/*1119*/    if (enc == JsonEncoding.UTF8) {
/*1120*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1122*/    Writer w = _createWriter(out, enc, ctxt);
/*1123*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out) throws IOException {
/*1136*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(Writer w) throws IOException {
/*1156*/    IOContext ctxt = _createContext(w, false);
/*1157*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/*1179*/    OutputStream out = new FileOutputStream(f);
/*1181*/    IOContext ctxt = _createContext(out, true);
/*1182*/    ctxt.setEncoding(enc);
/*1183*/    if (enc == JsonEncoding.UTF8) {
/*1184*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1186*/    Writer w = _createWriter(out, enc, ctxt);
/*1187*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/*1198*/    return createGenerator(_createDataOutputWrapper(out), enc);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out) throws IOException {
/*1211*/    return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/*1241*/    return createParser(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/*1266*/    return createParser(url);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/*1292*/    return createParser(in);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/*1311*/    return createParser(r);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/*1321*/    return createParser(data);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/*1336*/    return createParser(data, offset, len);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/*1347*/    return createParser(content);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1380*/    return createGenerator(out, enc);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/*1400*/    return createGenerator(out);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/*1413*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/*1437*/    return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/*1454*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures));
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/*1466*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/*1484*/    return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/*1497*/    _requireJSONFactory("InputData source not (yet?) supported for this format (%s)");
/*1500*/    int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/*1501*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1502*/    return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/*1525*/    WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1527*/    if (this._characterEscapes != null) {
/*1528*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1530*/    SerializableString rootSep = this._rootValueSeparator;
/*1531*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1532*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1534*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/*1548*/    UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1550*/    if (this._characterEscapes != null) {
/*1551*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1553*/    SerializableString rootSep = this._rootValueSeparator;
/*1554*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1555*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1557*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/*1563*/    if (enc == JsonEncoding.UTF8) {
/*1564*/        return new UTF8Writer(ctxt, out); 
/*   0*/       }
/*1567*/    return new OutputStreamWriter(out, enc.getJavaName());
/*   0*/  }
/*   0*/  
/*   0*/  protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/*1580*/    if (this._inputDecorator != null) {
/*1581*/      InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/*1582*/      if (in2 != null) {
/*1583*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1586*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/*1593*/    if (this._inputDecorator != null) {
/*1594*/      Reader in2 = this._inputDecorator.decorate(ctxt, in);
/*1595*/      if (in2 != null) {
/*1596*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1599*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/*1606*/    if (this._inputDecorator != null) {
/*1607*/      DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/*1608*/      if (in2 != null) {
/*1609*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1612*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/*1619*/    if (this._outputDecorator != null) {
/*1620*/      OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/*1621*/      if (out2 != null) {
/*1622*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1625*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/*1632*/    if (this._outputDecorator != null) {
/*1633*/      Writer out2 = this._outputDecorator.decorate(ctxt, out);
/*1634*/      if (out2 != null) {
/*1635*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1638*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferRecycler _getBufferRecycler() {
/*1659*/    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/*1660*/        return BufferRecyclers.getBufferRecycler(); 
/*   0*/       }
/*1662*/    return new BufferRecycler();
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
/*1670*/    return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createNonBlockingContext(Object srcRef) {
/*1682*/    BufferRecycler recycler = new BufferRecycler();
/*1683*/    return new IOContext(recycler, srcRef, false);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _requireJSONFactory(String msg) {
/*1705*/    if (!_isJSONFactory()) {
/*1706*/        throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final boolean _isJSONFactory() {
/*1713*/    return (getFormatName() == "JSON");
/*   0*/  }
/*   0*/}
