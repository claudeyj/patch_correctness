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
/*   0*/public class JsonFactory implements Versioned, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public static final String FORMAT_NAME_JSON = "JSON";
/*   0*/  
/*   0*/  public enum Feature {
/*  78*/    INTERN_FIELD_NAMES(true),
/*  88*/    CANONICALIZE_FIELD_NAMES(true),
/* 104*/    FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
/* 121*/    USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true);
/*   0*/    
/*   0*/    private final boolean _defaultState;
/*   0*/    
/*   0*/    public static int collectDefaults() {
/* 135*/      int flags = 0;
/* 136*/      for (Feature f : values()) {
/* 137*/        if (f.enabledByDefault()) {
/* 137*/            flags |= f.getMask(); 
/*   0*/           }
/*   0*/      } 
/* 139*/      return flags;
/*   0*/    }
/*   0*/    
/*   0*/    Feature(boolean defaultState) {
/* 142*/      this._defaultState = defaultState;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean enabledByDefault() {
/* 144*/      return this._defaultState;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean enabledIn(int flags) {
/* 145*/      return ((flags & getMask()) != 0);
/*   0*/    }
/*   0*/    
/*   0*/    public int getMask() {
/* 146*/      return 1 << ordinal();
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 164*/  protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = Feature.collectDefaults();
/*   0*/  
/* 170*/  protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
/*   0*/  
/* 176*/  protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
/*   0*/  
/* 178*/  private static final SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
/*   0*/  
/* 191*/  protected final transient CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
/*   0*/  
/* 202*/  protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
/*   0*/  
/*   0*/  protected ObjectCodec _objectCodec;
/*   0*/  
/* 222*/  protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
/*   0*/  
/* 227*/  protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*   0*/  
/* 232*/  protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
/*   0*/  
/*   0*/  protected CharacterEscapes _characterEscapes;
/*   0*/  
/*   0*/  protected InputDecorator _inputDecorator;
/*   0*/  
/*   0*/  protected OutputDecorator _outputDecorator;
/*   0*/  
/* 260*/  protected SerializableString _rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
/*   0*/  
/*   0*/  public JsonFactory() {
/* 278*/    this(null, objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory(ObjectCodec oc) {
/* 280*/    this._objectCodec = oc;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonFactory(JsonFactory src, ObjectCodec codec) {
/* 289*/    this._objectCodec = codec;
/* 290*/    this._factoryFeatures = src._factoryFeatures;
/* 291*/    this._parserFeatures = src._parserFeatures;
/* 292*/    this._generatorFeatures = src._generatorFeatures;
/* 293*/    this._characterEscapes = src._characterEscapes;
/* 294*/    this._inputDecorator = src._inputDecorator;
/* 295*/    this._outputDecorator = src._outputDecorator;
/* 296*/    this._rootValueSeparator = src._rootValueSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory copy() {
/* 320*/    _checkInvalidCopy(JsonFactory.class);
/* 322*/    return new JsonFactory(this, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkInvalidCopy(Class<?> exp) {
/* 331*/    if (getClass() != exp) {
/* 332*/        throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected Object readResolve() {
/* 349*/    return new JsonFactory(this, this._objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresPropertyOrdering() {
/* 373*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canHandleBinaryNatively() {
/* 387*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseCharArrays() {
/* 401*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canParseAsync() {
/* 414*/    return _isJSONFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatReadFeatureType() {
/* 425*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<? extends FormatFeature> getFormatWriteFeatureType() {
/* 436*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canUseSchema(FormatSchema schema) {
/* 455*/    if (schema == null) {
/* 456*/        return false; 
/*   0*/       }
/* 458*/    String ourFormat = getFormatName();
/* 459*/    return (ourFormat != null && ourFormat.equals(schema.getSchemaType()));
/*   0*/  }
/*   0*/  
/*   0*/  public String getFormatName() {
/* 475*/    if (getClass() == JsonFactory.class) {
/* 476*/        return "JSON"; 
/*   0*/       }
/* 478*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public MatchStrength hasFormat(InputAccessor acc) throws IOException {
/* 488*/    if (getClass() == JsonFactory.class) {
/* 489*/        return hasJSONFormat(acc); 
/*   0*/       }
/* 491*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean requiresCustomCodec() {
/* 508*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
/* 517*/    return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 528*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(Feature f, boolean state) {
/* 542*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(Feature f) {
/* 550*/    this._factoryFeatures |= f.getMask();
/* 551*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(Feature f) {
/* 559*/    this._factoryFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 560*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(Feature f) {
/* 567*/    return ((this._factoryFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonParser.Feature f, boolean state) {
/* 581*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonParser.Feature f) {
/* 589*/    this._parserFeatures |= f.getMask();
/* 590*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonParser.Feature f) {
/* 598*/    this._parserFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 599*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonParser.Feature f) {
/* 606*/    return ((this._parserFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public InputDecorator getInputDecorator() {
/* 614*/    return this._inputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setInputDecorator(InputDecorator d) {
/* 621*/    this._inputDecorator = d;
/* 622*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
/* 636*/    return state ? enable(f) : disable(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory enable(JsonGenerator.Feature f) {
/* 645*/    this._generatorFeatures |= f.getMask();
/* 646*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory disable(JsonGenerator.Feature f) {
/* 654*/    this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 655*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f) {
/* 662*/    return ((this._generatorFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterEscapes getCharacterEscapes() {
/* 669*/    return this._characterEscapes;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
/* 676*/    this._characterEscapes = esc;
/* 677*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public OutputDecorator getOutputDecorator() {
/* 685*/    return this._outputDecorator;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setOutputDecorator(OutputDecorator d) {
/* 692*/    this._outputDecorator = d;
/* 693*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setRootValueSeparator(String sep) {
/* 706*/    this._rootValueSeparator = (sep == null) ? null : new SerializedString(sep);
/* 707*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRootValueSeparator() {
/* 714*/    return (this._rootValueSeparator == null) ? null : this._rootValueSeparator.getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory setCodec(ObjectCodec oc) {
/* 731*/    this._objectCodec = oc;
/* 732*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 735*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(File f) throws IOException, JsonParseException {
/* 765*/    IOContext ctxt = _createContext(f, true);
/* 766*/    InputStream in = new FileInputStream(f);
/* 767*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(URL url) throws IOException, JsonParseException {
/* 792*/    IOContext ctxt = _createContext(url, true);
/* 793*/    InputStream in = _optimizedStreamFromURL(url);
/* 794*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
/* 819*/    IOContext ctxt = _createContext(in, false);
/* 820*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(Reader r) throws IOException, JsonParseException {
/* 839*/    IOContext ctxt = _createContext(r, false);
/* 840*/    return _createParser(_decorate(r, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
/* 850*/    IOContext ctxt = _createContext(data, true);
/* 851*/    if (this._inputDecorator != null) {
/* 852*/      InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 853*/      if (in != null) {
/* 854*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 857*/    return _createParser(data, 0, data.length, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/* 871*/    IOContext ctxt = _createContext(data, true);
/* 873*/    if (this._inputDecorator != null) {
/* 874*/      InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 875*/      if (in != null) {
/* 876*/          return _createParser(in, ctxt); 
/*   0*/         }
/*   0*/    } 
/* 879*/    return _createParser(data, offset, len, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(String content) throws IOException, JsonParseException {
/* 889*/    int strLen = content.length();
/* 891*/    if (this._inputDecorator != null || strLen > 32768 || !canUseCharArrays()) {
/* 894*/        return createParser(new StringReader(content)); 
/*   0*/       }
/* 896*/    IOContext ctxt = _createContext(content, true);
/* 897*/    char[] buf = ctxt.allocTokenBuffer(strLen);
/* 898*/    content.getChars(0, strLen, buf, 0);
/* 899*/    return _createParser(buf, 0, strLen, ctxt, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content) throws IOException {
/* 909*/    return createParser(content, 0, content.length);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(char[] content, int offset, int len) throws IOException {
/* 918*/    if (this._inputDecorator != null) {
/* 919*/        return createParser(new CharArrayReader(content, offset, len)); 
/*   0*/       }
/* 921*/    return _createParser(content, offset, len, _createContext(content, true), false);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createParser(DataInput in) throws IOException {
/* 936*/    IOContext ctxt = _createContext(in, false);
/* 937*/    return _createParser(_decorate(in, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser createNonBlockingByteArrayParser() throws IOException {
/* 962*/    _requireJSONFactory("Non-blocking source not (yet?) support for this format (%s)");
/* 963*/    IOContext ctxt = _createNonBlockingContext(null);
/* 964*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/* 965*/    return new NonBlockingJsonParser(ctxt, this._parserFeatures, can);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(File f) throws IOException, JsonParseException {
/* 995*/    return createParser(f);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(URL url) throws IOException, JsonParseException {
/*1020*/    return createParser(url);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(InputStream in) throws IOException, JsonParseException {
/*1046*/    return createParser(in);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(Reader r) throws IOException, JsonParseException {
/*1065*/    return createParser(r);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data) throws IOException, JsonParseException {
/*1075*/    return createParser(data);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException, JsonParseException {
/*1090*/    return createParser(data, offset, len);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonParser createJsonParser(String content) throws IOException, JsonParseException {
/*1101*/    return createParser(content);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1136*/    IOContext ctxt = _createContext(out, false);
/*1137*/    ctxt.setEncoding(enc);
/*1138*/    if (enc == JsonEncoding.UTF8) {
/*1139*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1141*/    Writer w = _createWriter(out, enc, ctxt);
/*1142*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(OutputStream out) throws IOException {
/*1154*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(Writer w) throws IOException {
/*1173*/    IOContext ctxt = _createContext(w, false);
/*1174*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
/*1195*/    OutputStream out = new FileOutputStream(f);
/*1197*/    IOContext ctxt = _createContext(out, true);
/*1198*/    ctxt.setEncoding(enc);
/*1199*/    if (enc == JsonEncoding.UTF8) {
/*1200*/        return _createUTF8Generator(_decorate(out, ctxt), ctxt); 
/*   0*/       }
/*1202*/    Writer w = _createWriter(out, enc, ctxt);
/*1203*/    return _createGenerator(_decorate(w, ctxt), ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out, JsonEncoding enc) throws IOException {
/*1213*/    return createGenerator(_createDataOutputWrapper(out), enc);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator createGenerator(DataOutput out) throws IOException {
/*1225*/    return createGenerator(_createDataOutputWrapper(out), JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc) throws IOException {
/*1258*/    return createGenerator(out, enc);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(Writer out) throws IOException {
/*1278*/    return createGenerator(out);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator createJsonGenerator(OutputStream out) throws IOException {
/*1291*/    return createGenerator(out, JsonEncoding.UTF8);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
/*1315*/    return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
/*1332*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures));
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
/*1344*/    return new ReaderBasedJsonParser(ctxt, this._parserFeatures, null, this._objectCodec, this._rootCharSymbols.makeChild(this._factoryFeatures), data, offset, offset + len, recyclable);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
/*1362*/    return new ByteSourceJsonBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._byteSymbolCanonicalizer, this._rootCharSymbols, this._factoryFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _createParser(DataInput input, IOContext ctxt) throws IOException {
/*1375*/    _requireJSONFactory("InputData source not (yet?) support for this format (%s)");
/*1378*/    int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
/*1379*/    ByteQuadsCanonicalizer can = this._byteSymbolCanonicalizer.makeChild(this._factoryFeatures);
/*1380*/    return new UTF8DataInputJsonParser(ctxt, this._parserFeatures, input, this._objectCodec, can, firstByte);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
/*1403*/    WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1405*/    if (this._characterEscapes != null) {
/*1406*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1408*/    SerializableString rootSep = this._rootValueSeparator;
/*1409*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1410*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1412*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
/*1426*/    UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/*1428*/    if (this._characterEscapes != null) {
/*1429*/        gen.setCharacterEscapes(this._characterEscapes); 
/*   0*/       }
/*1431*/    SerializableString rootSep = this._rootValueSeparator;
/*1432*/    if (rootSep != DEFAULT_ROOT_VALUE_SEPARATOR) {
/*1433*/        gen.setRootValueSeparator(rootSep); 
/*   0*/       }
/*1435*/    return gen;
/*   0*/  }
/*   0*/  
/*   0*/  protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
/*1441*/    if (enc == JsonEncoding.UTF8) {
/*1442*/        return new UTF8Writer(ctxt, out); 
/*   0*/       }
/*1445*/    return new OutputStreamWriter(out, enc.getJavaName());
/*   0*/  }
/*   0*/  
/*   0*/  protected final InputStream _decorate(InputStream in, IOContext ctxt) throws IOException {
/*1458*/    if (this._inputDecorator != null) {
/*1459*/      InputStream in2 = this._inputDecorator.decorate(ctxt, in);
/*1460*/      if (in2 != null) {
/*1461*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1464*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Reader _decorate(Reader in, IOContext ctxt) throws IOException {
/*1471*/    if (this._inputDecorator != null) {
/*1472*/      Reader in2 = this._inputDecorator.decorate(ctxt, in);
/*1473*/      if (in2 != null) {
/*1474*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1477*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final DataInput _decorate(DataInput in, IOContext ctxt) throws IOException {
/*1484*/    if (this._inputDecorator != null) {
/*1485*/      DataInput in2 = this._inputDecorator.decorate(ctxt, in);
/*1486*/      if (in2 != null) {
/*1487*/          return in2; 
/*   0*/         }
/*   0*/    } 
/*1490*/    return in;
/*   0*/  }
/*   0*/  
/*   0*/  protected final OutputStream _decorate(OutputStream out, IOContext ctxt) throws IOException {
/*1497*/    if (this._outputDecorator != null) {
/*1498*/      OutputStream out2 = this._outputDecorator.decorate(ctxt, out);
/*1499*/      if (out2 != null) {
/*1500*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1503*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Writer _decorate(Writer out, IOContext ctxt) throws IOException {
/*1510*/    if (this._outputDecorator != null) {
/*1511*/      Writer out2 = this._outputDecorator.decorate(ctxt, out);
/*1512*/      if (out2 != null) {
/*1513*/          return out2; 
/*   0*/         }
/*   0*/    } 
/*1516*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferRecycler _getBufferRecycler() {
/*1537*/    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(this._factoryFeatures)) {
/*1538*/        return BufferRecyclers.getBufferRecycler(); 
/*   0*/       }
/*1540*/    return new BufferRecycler();
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
/*1548*/    return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
/*   0*/  }
/*   0*/  
/*   0*/  protected IOContext _createNonBlockingContext(Object srcRef) {
/*1560*/    BufferRecycler recycler = new BufferRecycler();
/*1561*/    return new IOContext(recycler, srcRef, false);
/*   0*/  }
/*   0*/  
/*   0*/  protected OutputStream _createDataOutputWrapper(DataOutput out) {
/*1568*/    return new DataOutputAsStream(out);
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
/*1577*/    if ("file".equals(url.getProtocol())) {
/*1584*/      String host = url.getHost();
/*1585*/      if (host == null || host.length() == 0) {
/*1587*/        String path = url.getPath();
/*1588*/        if (path.indexOf('%') < 0) {
/*1589*/            return new FileInputStream(url.getPath()); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1595*/    return url.openStream();
/*   0*/  }
/*   0*/  
/*   0*/  private final void _requireJSONFactory(String msg) {
/*1617*/    if (!_isJSONFactory()) {
/*1618*/        throw new UnsupportedOperationException(String.format(msg, new Object[] { getFormatName() })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final boolean _isJSONFactory() {
/*1625*/    return (getFormatName() == "JSON");
/*   0*/  }
/*   0*/}
