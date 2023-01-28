/*   0*/package com.fasterxml.jackson.core.base;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonStreamContext;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.PrettyPrinter;
/*   0*/import com.fasterxml.jackson.core.SerializableString;
/*   0*/import com.fasterxml.jackson.core.TreeNode;
/*   0*/import com.fasterxml.jackson.core.Version;
/*   0*/import com.fasterxml.jackson.core.json.DupDetector;
/*   0*/import com.fasterxml.jackson.core.json.JsonWriteContext;
/*   0*/import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*   0*/import com.fasterxml.jackson.core.util.VersionUtil;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/
/*   0*/public abstract class GeneratorBase extends JsonGenerator {
/*   0*/  public static final int SURR1_FIRST = 55296;
/*   0*/  
/*   0*/  public static final int SURR1_LAST = 56319;
/*   0*/  
/*   0*/  public static final int SURR2_FIRST = 56320;
/*   0*/  
/*   0*/  public static final int SURR2_LAST = 57343;
/*   0*/  
/*  29*/  protected static final int DERIVED_FEATURES_MASK = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII.getMask() | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.getMask();
/*   0*/  
/*   0*/  protected static final String WRITE_BINARY = "write a binary value";
/*   0*/  
/*   0*/  protected static final String WRITE_BOOLEAN = "write a boolean value";
/*   0*/  
/*   0*/  protected static final String WRITE_NULL = "write a null";
/*   0*/  
/*   0*/  protected static final String WRITE_NUMBER = "write a number";
/*   0*/  
/*   0*/  protected static final String WRITE_RAW = "write a raw (unencoded) value";
/*   0*/  
/*   0*/  protected static final String WRITE_STRING = "write a string";
/*   0*/  
/*   0*/  protected ObjectCodec _objectCodec;
/*   0*/  
/*   0*/  protected int _features;
/*   0*/  
/*   0*/  protected boolean _cfgNumbersAsStrings;
/*   0*/  
/*   0*/  protected JsonWriteContext _writeContext;
/*   0*/  
/*   0*/  protected boolean _closed;
/*   0*/  
/*   0*/  protected GeneratorBase(int features, ObjectCodec codec) {
/*  93*/    this._features = features;
/*  94*/    this._objectCodec = codec;
/*  95*/    DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
/*  97*/    this._writeContext = JsonWriteContext.createRootContext(dups);
/*  98*/    this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*   0*/  }
/*   0*/  
/*   0*/  protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt) {
/* 106*/    this._features = features;
/* 107*/    this._objectCodec = codec;
/* 108*/    this._writeContext = ctxt;
/* 109*/    this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 117*/    return VersionUtil.versionFor(getClass());
/*   0*/  }
/*   0*/  
/*   0*/  public Object getCurrentValue() {
/* 121*/    return this._writeContext.getCurrentValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void setCurrentValue(Object v) {
/* 126*/    this._writeContext.setCurrentValue(v);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f) {
/* 136*/    return ((this._features & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getFeatureMask() {
/* 137*/    return this._features;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator enable(JsonGenerator.Feature f) {
/* 143*/    int mask = f.getMask();
/* 144*/    this._features |= mask;
/* 145*/    if ((mask & DERIVED_FEATURES_MASK) != 0) {
/* 147*/        if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 148*/          this._cfgNumbersAsStrings = true;
/* 149*/        } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 150*/          setHighestNonEscapedChar(127);
/* 151*/        } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION && 
/* 152*/          this._writeContext.getDupDetector() == null) {
/* 153*/          this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this));
/*   0*/        }  
/*   0*/       }
/* 157*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator disable(JsonGenerator.Feature f) {
/* 162*/    int mask = f.getMask();
/* 163*/    this._features &= mask ^ 0xFFFFFFFF;
/* 164*/    if ((mask & DERIVED_FEATURES_MASK) != 0) {
/* 165*/        if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 166*/          this._cfgNumbersAsStrings = false;
/* 167*/        } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 168*/          setHighestNonEscapedChar(0);
/* 169*/        } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
/* 170*/          this._writeContext = this._writeContext.withDupDetector(null);
/*   0*/        }  
/*   0*/       }
/* 173*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonGenerator setFeatureMask(int newMask) {
/* 179*/    int changed = newMask ^ this._features;
/* 180*/    this._features = newMask;
/* 181*/    if (changed != 0) {
/* 182*/        _checkStdFeatureChanges(newMask, changed); 
/*   0*/       }
/* 184*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator overrideStdFeatures(int values, int mask) {
/* 189*/    int oldState = this._features;
/* 190*/    int newState = oldState & (mask ^ 0xFFFFFFFF) | values & mask;
/* 191*/    int changed = oldState ^ newState;
/* 192*/    if (changed != 0) {
/* 193*/      this._features = newState;
/* 194*/      _checkStdFeatureChanges(newState, changed);
/*   0*/    } 
/* 196*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
/* 210*/    if ((changedFeatures & DERIVED_FEATURES_MASK) == 0) {
/*   0*/        return; 
/*   0*/       }
/* 213*/    this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
/* 214*/    if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
/* 215*/        if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
/* 216*/          setHighestNonEscapedChar(127);
/*   0*/        } else {
/* 218*/          setHighestNonEscapedChar(0);
/*   0*/        }  
/*   0*/       }
/* 221*/    if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
/* 222*/        if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
/* 223*/          if (this._writeContext.getDupDetector() == null) {
/* 224*/              this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector(this)); 
/*   0*/             }
/*   0*/        } else {
/* 227*/          this._writeContext = this._writeContext.withDupDetector(null);
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator useDefaultPrettyPrinter() {
/* 234*/    if (getPrettyPrinter() != null) {
/* 235*/        return this; 
/*   0*/       }
/* 237*/    return setPrettyPrinter(_constructDefaultPrettyPrinter());
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator setCodec(ObjectCodec oc) {
/* 241*/    this._objectCodec = oc;
/* 242*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 245*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonWriteContext getOutputContext() {
/* 256*/    return this._writeContext;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeFieldName(SerializableString name) throws IOException {
/* 276*/    writeFieldName(name.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  public void writeString(SerializableString text) throws IOException {
/* 289*/    writeString(text.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(String text) throws IOException {
/* 293*/    _verifyValueWrite("write raw value");
/* 294*/    writeRaw(text);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(String text, int offset, int len) throws IOException {
/* 298*/    _verifyValueWrite("write raw value");
/* 299*/    writeRaw(text, offset, offset);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 303*/    _verifyValueWrite("write raw value");
/* 304*/    writeRaw(text, offset, len);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(SerializableString text) throws IOException {
/* 308*/    _verifyValueWrite("write raw value");
/* 309*/    writeRaw(text);
/*   0*/  }
/*   0*/  
/*   0*/  public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
/* 315*/    _reportUnsupportedOperation();
/* 316*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeObject(Object value) throws IOException {
/* 345*/    if (value == null) {
/* 347*/      writeNull();
/*   0*/    } else {
/* 354*/      if (this._objectCodec != null) {
/* 355*/        this._objectCodec.writeValue(this, value);
/*   0*/        return;
/*   0*/      } 
/* 358*/      _writeSimpleObject(value);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTree(TreeNode rootNode) throws IOException {
/* 365*/    if (rootNode == null) {
/* 366*/      writeNull();
/*   0*/    } else {
/* 368*/      if (this._objectCodec == null) {
/* 369*/          throw new IllegalStateException("No ObjectCodec defined"); 
/*   0*/         }
/* 371*/      this._objectCodec.writeValue(this, rootNode);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 382*/    this._closed = true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isClosed() {
/* 383*/    return this._closed;
/*   0*/  }
/*   0*/  
/*   0*/  protected PrettyPrinter _constructDefaultPrettyPrinter() {
/* 414*/    return new DefaultPrettyPrinter();
/*   0*/  }
/*   0*/  
/*   0*/  protected final int _decodeSurrogate(int surr1, int surr2) throws IOException {
/* 429*/    if (surr2 < 56320 || surr2 > 57343) {
/* 430*/      String msg = "Incomplete surrogate pair: first char 0x" + Integer.toHexString(surr1) + ", second 0x" + Integer.toHexString(surr2);
/* 431*/      _reportError(msg);
/*   0*/    } 
/* 433*/    int c = 65536 + (surr1 - 55296 << 10) + surr2 - 56320;
/* 434*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract void flush() throws IOException;
/*   0*/  
/*   0*/  protected abstract void _releaseBuffers();
/*   0*/  
/*   0*/  protected abstract void _verifyValueWrite(String paramString) throws IOException;
/*   0*/}
