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
/* 285*/    this();
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
/* 418*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatReadFeatureType() {
/* 428*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/* 439*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseSchema(FormatSchema schema) {
/* 458*/    if (schema == null) {
/* 459*/        return false; 
/*   0*/       }
/* 461*/    String ourFormat = getFormatName();
/* 462*/    return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
/*   0*/  }
/*   0*/  
/*   0*/  public String getFormatName() {
/* 478*/    if (getClass() == JsonFactory.class) {
/* 479*/        return "JSON"; 
/*   0*/       }
/* 481*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/* 491*/    if (getClass() == JsonFactory.class) {
/* 492*/        return hasJSONFormat(acc); 
/*   0*/       }
/* 494*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresCustomCodec() {
/* 511*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 520*/    return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 531*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(Feature f, boolean state) {
/* 545*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(Feature f) {
/* 553*/    this._factoryFeatures |= f.getMask();
/* 554*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(Feature f) {
/* 562*/    this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 563*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(Feature f) {
/* 570*/    return ((this._factoryFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/* 584*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonParser.Feature f) {
/* 592*/    this._parserFeatures |= f.getMask();
/* 593*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonParser.Feature f) {
/* 601*/    this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 602*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonParser.Feature f) {
/* 609*/    return ((this._parserFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public InputDecorator getInputDecorator() {
/* 617*/    return this._inputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setInputDecorator(InputDecorator d) {
/* 624*/    this._inputDecorator = d;
/* 625*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/* 639*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonGenerator.Feature f) {
/* 648*/    this._generatorFeatures |= f.getMask();
/* 649*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonGenerator.Feature f) {
/* 657*/    this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 658*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f) {
/* 665*/    return ((this._generatorFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterEscapes getCharacterEscapes() {
/* 672*/    return this._characterEscapes;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/* 679*/    this._characterEscapes = esc;
/* 680*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public OutputDecorator getOutputDecorator() {
/* 688*/    return this._outputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setOutputDecorator(OutputDecorator d) {
/* 695*/    this._outputDecorator = d;
/* 696*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setRootValueSeparator(String sep) {
/* 709*/    this._rootValueSeparator = (sep == null) ? null : new SerializedString(sep);
/* 710*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRootValueSeparator() {
/* 717*/    return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCodec(ObjectCodec oc) {
/* 734*/    this._objectCodec = oc;
/* 735*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 738*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(File f) throws IOException, JsonParseException {
/* 768*/    IOContext ctxt = _createContext(f, true);
/* 769*/    InputStream in = new FileInputStream(f);
/* 770*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(URL url) throws IOException, JsonParseException {
/* 795*/    IOContext ctxt = _createContext(url, true);
/* 796*/    InputStream in = _optimizedStreamFromURL(url);
/* 797*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/* 822*/    IOContext ctxt = _createContext(in, false);
/* 823*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/* 842*/    IOContext ctxt = _createContext(r, false);
/* 843*/    return _createParser(_decorate(r, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 853*/    IOContext ctxt = _createContext(data, true);
/* 854*/    if (this._inputDecorator != null) {
/* 855*/      InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 856*/      if (in != null) {
/* 857*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 860*/    return _createParser(data, 0, data.length, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 874*/    IOContext ctxt = _createContext(data, true);
/* 876*/    if (this._inputDecorator != null) {
/* 877*/      InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 878*/      if (in != null) {
/* 879*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 882*/    return _createParser(data, offset, len, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(String content) throws IOException, JsonParseException {
/* 892*/    int strLen = content.length();
/* 894*/    if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays()) {
/* 897*/        return createParser(new StringReader(content)); 
/*   0*/       }
/* 899*/    IOContext ctxt = _createContext(content, true);
/* 900*/    char[] buf = ctxt.allocTokenBuffer(strLen);
/* 901*/    content.getChars(0, strLen, buf, 0);
/* 902*/    return _createParser(buf, 0, strLen, ctxt, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content) throws IOException {
/* 912*/    return createParser(content, 0, content.length);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/* 921*/    if (this._inputDecorator != null) {
/* 922*/        return createParser(new CharArrayReader(content, offset, len)); 
/*   0*/       }
/* 924*/    return _createParser(content, offset, len, _createContext(content, true), false);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(DataInput in) throws IOException {
/* 939*/    IOContext ctxt = _createContext(in, false);
/* 940*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createNonBlockingByteArrayParser() throws IOException {
/* 965*/    _requireJSONFactory("Non-blocking source not (yet?) support for this format (%s)");
/* 966*/    IOContext ctxt = _createContext(null, false);
/* 967*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 968*/    return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/* 998*/    return createParser(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/*1023*/    return createParser(url);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/*1049*/    return createParser(in);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/*1068*/    return createParser(r);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/*1078*/    return createParser(data);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/*1093*/    return createParser(data, offset, len);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/*1104*/    return createParser(content);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1139*/    IOContext ctxt = _createContext(out, false);
/*1140*/    ctxt.setEncoding(enc);
/*1141*/    if (enc == JsonEncoding.UTF8) {
/*1142*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1144*/    Writer w = _createWriter(out, enc, ctxt);
/*1145*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out) throws IOException {
/*1157*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(Writer w) throws IOException {
/*1176*/    IOContext ctxt = _createContext(w, false);
/*1177*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/*1198*/    OutputStream out = new FileOutputStream(f);
/*1200*/    IOContext ctxt = _createContext(out, true);
/*1201*/    ctxt.setEncoding(enc);
/*1202*/    if (enc == JsonEncoding.UTF8) {
/*1203*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1205*/    Writer w = _createWriter(out, enc, ctxt);
/*1206*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/*1216*/    return createGenerator(_createDataOutputWrapper(out), enc);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out) throws IOException {
/*1228*/    return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1261*/    return createGenerator(out, enc);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/*1281*/    return createGenerator(out);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/*1294*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/*1318*/    return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/*1335*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures));
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/*1347*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/*1365*/    return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/*1378*/    _requireJSONFactory("InputData source not (yet?) support for this format (%s)");
/*1381*/    int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/*1382*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1383*/    return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/*1406*/    WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1408*/    if (this._characterEscapes != null) {
/*1409*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1411*/    SerializableString rootSep = this._rootValueSeparator;
/*1412*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1413*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1415*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/*1429*/    UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1431*/    if (this._characterEscapes != null) {
/*1432*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1434*/    SerializableString rootSep = this._rootValueSeparator;
/*1435*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1436*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1438*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/*1444*/    if (enc == JsonEncoding.UTF8) {
/*1445*/        return new UTF8Writer(ctxt, out); 
/*   0*/       }
/*1448*/    return new OutputStreamWriter(out, enc.getJavaName());
/*   0*/  }
/*   0*/  
/*   0*/  protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/*1461*/    if (this._inputDecorator != null) {
/*1462*/      InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/*1463*/      if (in2 != null) {
/*1464*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1467*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/*1474*/    if (this._inputDecorator != null) {
/*1475*/      Reader in2 = this._inputDecorator.decorate(ctxt, in);
/*1476*/      if (in2 != null) {
/*1477*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1480*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/*1487*/    if (this._inputDecorator != null) {
/*1488*/      DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/*1489*/      if (in2 != null) {
/*1490*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1493*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/*1500*/    if (this._outputDecorator != null) {
/*1501*/      OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/*1502*/      if (out2 != null) {
/*1503*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1506*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/*1513*/    if (this._outputDecorator != null) {
/*1514*/      Writer out2 = this._outputDecorator.decorate(ctxt, out);
/*1515*/      if (out2 != null) {
/*1516*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1519*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferRecycler _getBufferRecycler() {
/*1540*/    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/*1541*/      SoftReference<BufferRecycler> ref = _recyclerRef.get();
/*1542*/      BufferRecycler br = (ref == null) ? null : ref.get();
/*1544*/      if (br == null) {
/*1545*/        br = new BufferRecycler();
/*1546*/        _recyclerRef.set(new SoftReference<BufferRecycler>(br));
/*   0*/      } 
/*1548*/      return br;
/*   0*/    } 
/*1550*/    return new BufferRecycler();
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
/*1558*/    return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
/*   0*/  }
/*   0*/  
/*   0*/  protected OutputStream _createDataOutputWrapper(DataOutput out) {
/*1565*/    return new DataOutputAsStream(out);
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
/*1574*/    if ("file".equals(url.getProtocol())) {
/*1581*/      String host = url.getHost();
/*1582*/      if (host == null || host.length() == 0) {
/*1584*/        String path = url.getPath();
/*1585*/        if (path.indexOf('%') < 0) {
/*1586*/            return new FileInputStream(url.getPath()); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1592*/    return url.openStream();
/*   0*/  }
/*   0*/  
/*   0*/  private final void _requireJSONFactory(String msg) {
/*1616*/    String format = getFormatName();
/*1617*/    if (format != "JSON") {
/*1618*/        throw new UnsupportedOperationException(String.format(msg, new Object[] { format })); 
/*   0*/       }
/*   0*/  }
/*   0*/}
