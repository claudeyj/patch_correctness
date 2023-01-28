/*   0*/package com.fasterxml.jackson.databind.util;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonGenerationException;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.core.JsonParseException;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonStreamContext;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.SerializableString;
/*   0*/import com.fasterxml.jackson.core.TreeNode;
/*   0*/import com.fasterxml.jackson.core.Version;
/*   0*/import com.fasterxml.jackson.core.base.ParserMinimalBase;
/*   0*/import com.fasterxml.jackson.core.json.JsonReadContext;
/*   0*/import com.fasterxml.jackson.core.json.JsonWriteContext;
/*   0*/import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/import java.util.TreeMap;
/*   0*/
/*   0*/public class TokenBuffer extends JsonGenerator {
/*  33*/  protected static final int DEFAULT_GENERATOR_FEATURES = JsonGenerator.Feature.collectDefaults();
/*   0*/  
/*   0*/  protected ObjectCodec _objectCodec;
/*   0*/  
/*   0*/  protected int _generatorFeatures;
/*   0*/  
/*   0*/  protected boolean _closed;
/*   0*/  
/*   0*/  protected boolean _hasNativeTypeIds;
/*   0*/  
/*   0*/  protected boolean _hasNativeObjectIds;
/*   0*/  
/*   0*/  protected boolean _mayHaveNativeIds;
/*   0*/  
/*   0*/  protected Segment _first;
/*   0*/  
/*   0*/  protected Segment _last;
/*   0*/  
/*   0*/  protected int _appendAt;
/*   0*/  
/*   0*/  protected Object _typeId;
/*   0*/  
/*   0*/  protected Object _objectId;
/*   0*/  
/*   0*/  protected boolean _hasNativeId = false;
/*   0*/  
/*   0*/  protected JsonWriteContext _writeContext;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public TokenBuffer(ObjectCodec codec) {
/* 136*/    this(codec, false);
/*   0*/  }
/*   0*/  
/*   0*/  public TokenBuffer(ObjectCodec codec, boolean hasNativeIds) {
/* 148*/    this._objectCodec = codec;
/* 149*/    this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
/* 150*/    this._writeContext = JsonWriteContext.createRootContext(null);
/* 152*/    this._first = this._last = new Segment();
/* 153*/    this._appendAt = 0;
/* 154*/    this._hasNativeTypeIds = hasNativeIds;
/* 155*/    this._hasNativeObjectIds = hasNativeIds;
/* 157*/    this._mayHaveNativeIds = this._hasNativeTypeIds | this._hasNativeObjectIds;
/*   0*/  }
/*   0*/  
/*   0*/  public TokenBuffer(JsonParser jp) {
/* 165*/    this._objectCodec = jp.getCodec();
/* 166*/    this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
/* 167*/    this._writeContext = JsonWriteContext.createRootContext(null);
/* 169*/    this._first = this._last = new Segment();
/* 170*/    this._appendAt = 0;
/* 171*/    this._hasNativeTypeIds = jp.canReadTypeId();
/* 172*/    this._hasNativeObjectIds = jp.canReadObjectId();
/* 173*/    this._mayHaveNativeIds = this._hasNativeTypeIds | this._hasNativeObjectIds;
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 178*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser asParser() {
/* 193*/    return asParser(this._objectCodec);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser asParser(ObjectCodec codec) {
/* 211*/    return (JsonParser)new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser asParser(JsonParser src) {
/* 220*/    Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds);
/* 221*/    p.setLocation(src.getTokenLocation());
/* 222*/    return (JsonParser)p;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken firstToken() {
/* 232*/    if (this._first != null) {
/* 233*/        return this._first.type(0); 
/*   0*/       }
/* 235*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public TokenBuffer append(TokenBuffer other) throws IOException {
/* 255*/    if (!this._hasNativeTypeIds) {
/* 256*/        this._hasNativeTypeIds = other.canWriteTypeId(); 
/*   0*/       }
/* 258*/    if (!this._hasNativeObjectIds) {
/* 259*/        this._hasNativeObjectIds = other.canWriteObjectId(); 
/*   0*/       }
/* 261*/    this._mayHaveNativeIds = this._hasNativeTypeIds | this._hasNativeObjectIds;
/* 263*/    JsonParser p = other.asParser();
/* 264*/    while (p.nextToken() != null) {
/* 265*/        copyCurrentStructure(p); 
/*   0*/       }
/* 267*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void serialize(JsonGenerator gen) throws IOException {
/* 282*/    Segment segment = this._first;
/* 283*/    int ptr = -1;
/* 285*/    boolean checkIds = this._mayHaveNativeIds;
/* 286*/    boolean hasIds = (checkIds && segment.hasIds());
/*   0*/    while (true) {
/*   0*/      Object ob, n, value;
/* 289*/      if (++ptr >= 16) {
/* 290*/        ptr = 0;
/* 291*/        segment = segment.next();
/* 292*/        if (segment == null) {
/*   0*/            break; 
/*   0*/           }
/* 293*/        hasIds = (checkIds && segment.hasIds());
/*   0*/      } 
/* 295*/      JsonToken t = segment.type(ptr);
/* 296*/      if (t == null) {
/*   0*/          break; 
/*   0*/         }
/* 298*/      if (hasIds) {
/* 299*/        Object id = segment.findObjectId(ptr);
/* 300*/        if (id != null) {
/* 301*/            gen.writeObjectId(id); 
/*   0*/           }
/* 303*/        id = segment.findTypeId(ptr);
/* 304*/        if (id != null) {
/* 305*/            gen.writeTypeId(id); 
/*   0*/           }
/*   0*/      } 
/* 310*/      switch (t) {
/*   0*/        case START_OBJECT:
/* 312*/          gen.writeStartObject();
/*   0*/          continue;
/*   0*/        case END_OBJECT:
/* 315*/          gen.writeEndObject();
/*   0*/          continue;
/*   0*/        case START_ARRAY:
/* 318*/          gen.writeStartArray();
/*   0*/          continue;
/*   0*/        case END_ARRAY:
/* 321*/          gen.writeEndArray();
/*   0*/          continue;
/*   0*/        case FIELD_NAME:
/* 326*/          ob = segment.get(ptr);
/* 327*/          if (ob instanceof SerializableString) {
/* 328*/            gen.writeFieldName((SerializableString)ob);
/*   0*/            continue;
/*   0*/          } 
/* 330*/          gen.writeFieldName((String)ob);
/*   0*/          continue;
/*   0*/        case VALUE_STRING:
/* 336*/          ob = segment.get(ptr);
/* 337*/          if (ob instanceof SerializableString) {
/* 338*/            gen.writeString((SerializableString)ob);
/*   0*/            continue;
/*   0*/          } 
/* 340*/          gen.writeString((String)ob);
/*   0*/          continue;
/*   0*/        case VALUE_NUMBER_INT:
/* 346*/          n = segment.get(ptr);
/* 347*/          if (n instanceof Integer) {
/* 348*/            gen.writeNumber((Integer)n);
/*   0*/            continue;
/*   0*/          } 
/* 349*/          if (n instanceof BigInteger) {
/* 350*/            gen.writeNumber((BigInteger)n);
/*   0*/            continue;
/*   0*/          } 
/* 351*/          if (n instanceof Long) {
/* 352*/            gen.writeNumber((Long)n);
/*   0*/            continue;
/*   0*/          } 
/* 353*/          if (n instanceof Short) {
/* 354*/            gen.writeNumber((Short)n);
/*   0*/            continue;
/*   0*/          } 
/* 356*/          gen.writeNumber(((Number)n).intValue());
/*   0*/          continue;
/*   0*/        case VALUE_NUMBER_FLOAT:
/* 362*/          n = segment.get(ptr);
/* 363*/          if (n instanceof Double) {
/* 364*/            gen.writeNumber((Double)n);
/*   0*/            continue;
/*   0*/          } 
/* 365*/          if (n instanceof BigDecimal) {
/* 366*/            gen.writeNumber((BigDecimal)n);
/*   0*/            continue;
/*   0*/          } 
/* 367*/          if (n instanceof Float) {
/* 368*/            gen.writeNumber((Float)n);
/*   0*/            continue;
/*   0*/          } 
/* 369*/          if (n == null) {
/* 370*/            gen.writeNull();
/*   0*/            continue;
/*   0*/          } 
/* 371*/          if (n instanceof String) {
/* 372*/            gen.writeNumber((String)n);
/*   0*/            continue;
/*   0*/          } 
/* 374*/          throw new JsonGenerationException("Unrecognized value type for VALUE_NUMBER_FLOAT: " + n.getClass().getName() + ", can not serialize");
/*   0*/        case VALUE_TRUE:
/* 379*/          gen.writeBoolean(true);
/*   0*/          continue;
/*   0*/        case VALUE_FALSE:
/* 382*/          gen.writeBoolean(false);
/*   0*/          continue;
/*   0*/        case VALUE_NULL:
/* 385*/          gen.writeNull();
/*   0*/          continue;
/*   0*/        case VALUE_EMBEDDED_OBJECT:
/* 389*/          value = segment.get(ptr);
/* 390*/          if (value instanceof RawValue) {
/* 391*/            ((RawValue)value).serialize(gen);
/*   0*/            continue;
/*   0*/          } 
/* 393*/          gen.writeObject(value);
/*   0*/          continue;
/*   0*/      } 
/* 398*/      throw new RuntimeException("Internal error: should never end up through this code path");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 410*/    if (p.getCurrentTokenId() != JsonToken.FIELD_NAME.id()) {
/* 411*/      copyCurrentStructure(p);
/* 412*/      return this;
/*   0*/    } 
/* 419*/    writeStartObject();
/*   0*/    JsonToken t;
/*   0*/    do {
/* 421*/      copyCurrentStructure(p);
/* 422*/    } while ((t = p.nextToken()) == JsonToken.FIELD_NAME);
/* 423*/    if (t != JsonToken.END_OBJECT) {
/* 424*/        throw ctxt.mappingException("Expected END_OBJECT after copying contents of a JsonParser into TokenBuffer, got " + t); 
/*   0*/       }
/* 426*/    writeEndObject();
/* 427*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 435*/    int MAX_COUNT = 100;
/* 437*/    StringBuilder sb = new StringBuilder();
/* 438*/    sb.append("[TokenBuffer: ");
/* 445*/    JsonParser jp = asParser();
/* 446*/    int count = 0;
/* 447*/    boolean hasNativeIds = (this._hasNativeTypeIds || this._hasNativeObjectIds);
/*   0*/    while (true) {
/*   0*/      try {
/* 452*/        JsonToken t = jp.nextToken();
/* 453*/        if (t == null) {
/*   0*/            break; 
/*   0*/           }
/* 455*/        if (hasNativeIds) {
/* 456*/            _appendNativeIds(sb); 
/*   0*/           }
/* 459*/        if (count < 100) {
/* 460*/          if (count > 0) {
/* 461*/              sb.append(", "); 
/*   0*/             }
/* 463*/          sb.append(t.toString());
/* 464*/          if (t == JsonToken.FIELD_NAME) {
/* 465*/            sb.append('(');
/* 466*/            sb.append(jp.getCurrentName());
/* 467*/            sb.append(')');
/*   0*/          } 
/*   0*/        } 
/* 470*/      } catch (IOException ioe) {
/* 471*/        throw new IllegalStateException(ioe);
/*   0*/      } 
/* 473*/      count++;
/*   0*/    } 
/* 476*/    if (count >= 100) {
/* 477*/        sb.append(" ... (truncated ").append(count - 100).append(" entries)"); 
/*   0*/       }
/* 479*/    sb.append(']');
/* 480*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private final void _appendNativeIds(StringBuilder sb) {
/* 485*/    Object objectId = this._last.findObjectId(this._appendAt - 1);
/* 486*/    if (objectId != null) {
/* 487*/        sb.append("[objectId=").append(String.valueOf(objectId)).append(']'); 
/*   0*/       }
/* 489*/    Object typeId = this._last.findTypeId(this._appendAt - 1);
/* 490*/    if (typeId != null) {
/* 491*/        sb.append("[typeId=").append(String.valueOf(typeId)).append(']'); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator enable(JsonGenerator.Feature f) {
/* 503*/    this._generatorFeatures |= f.getMask();
/* 504*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator disable(JsonGenerator.Feature f) {
/* 509*/    this._generatorFeatures &= f.getMask() ^ 0xFFFFFFFF;
/* 510*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(JsonGenerator.Feature f) {
/* 517*/    return ((this._generatorFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getFeatureMask() {
/* 522*/    return this._generatorFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator setFeatureMask(int mask) {
/* 527*/    this._generatorFeatures = mask;
/* 528*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator useDefaultPrettyPrinter() {
/* 534*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonGenerator setCodec(ObjectCodec oc) {
/* 539*/    this._objectCodec = oc;
/* 540*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 544*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonWriteContext getOutputContext() {
/* 547*/    return this._writeContext;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canWriteBinaryNatively() {
/* 560*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void flush() throws IOException {}
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 574*/    this._closed = true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isClosed() {
/* 578*/    return this._closed;
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeStartArray() throws IOException {
/* 589*/    _append(JsonToken.START_ARRAY);
/* 590*/    this._writeContext = this._writeContext.createChildArrayContext();
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeEndArray() throws IOException {
/* 596*/    _append(JsonToken.END_ARRAY);
/* 598*/    JsonWriteContext c = this._writeContext.getParent();
/* 599*/    if (c != null) {
/* 600*/        this._writeContext = c; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeStartObject() throws IOException {
/* 607*/    _append(JsonToken.START_OBJECT);
/* 608*/    this._writeContext = this._writeContext.createChildObjectContext();
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeEndObject() throws IOException {
/* 614*/    _append(JsonToken.END_OBJECT);
/* 616*/    JsonWriteContext c = this._writeContext.getParent();
/* 617*/    if (c != null) {
/* 618*/        this._writeContext = c; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeFieldName(String name) throws IOException {
/* 625*/    _append(JsonToken.FIELD_NAME, name);
/* 626*/    this._writeContext.writeFieldName(name);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeFieldName(SerializableString name) throws IOException {
/* 632*/    _append(JsonToken.FIELD_NAME, name);
/* 633*/    this._writeContext.writeFieldName(name.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  public void writeString(String text) throws IOException {
/* 644*/    if (text == null) {
/* 645*/      writeNull();
/*   0*/    } else {
/* 647*/      _append(JsonToken.VALUE_STRING, text);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeString(char[] text, int offset, int len) throws IOException {
/* 653*/    writeString(new String(text, offset, len));
/*   0*/  }
/*   0*/  
/*   0*/  public void writeString(SerializableString text) throws IOException {
/* 658*/    if (text == null) {
/* 659*/      writeNull();
/*   0*/    } else {
/* 661*/      _append(JsonToken.VALUE_STRING, text);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/* 669*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
/* 676*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(String text) throws IOException {
/* 681*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(String text, int offset, int len) throws IOException {
/* 686*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(SerializableString text) throws IOException {
/* 691*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(char[] text, int offset, int len) throws IOException {
/* 696*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(char c) throws IOException {
/* 701*/    _reportUnsupportedOperation();
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(String text) throws IOException {
/* 706*/    _append(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(String text, int offset, int len) throws IOException {
/* 711*/    if (offset > 0 || len != text.length()) {
/* 712*/        text = text.substring(offset, offset + len); 
/*   0*/       }
/* 714*/    _append(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(char[] text, int offset, int len) throws IOException {
/* 719*/    _append(JsonToken.VALUE_EMBEDDED_OBJECT, new String(text, offset, len));
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(short i) throws IOException {
/* 730*/    _append(JsonToken.VALUE_NUMBER_INT, i);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(int i) throws IOException {
/* 735*/    _append(JsonToken.VALUE_NUMBER_INT, i);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(long l) throws IOException {
/* 740*/    _append(JsonToken.VALUE_NUMBER_INT, l);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(double d) throws IOException {
/* 745*/    _append(JsonToken.VALUE_NUMBER_FLOAT, d);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(float f) throws IOException {
/* 750*/    _append(JsonToken.VALUE_NUMBER_FLOAT, f);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(BigDecimal dec) throws IOException {
/* 755*/    if (dec == null) {
/* 756*/      writeNull();
/*   0*/    } else {
/* 758*/      _append(JsonToken.VALUE_NUMBER_FLOAT, dec);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(BigInteger v) throws IOException {
/* 764*/    if (v == null) {
/* 765*/      writeNull();
/*   0*/    } else {
/* 767*/      _append(JsonToken.VALUE_NUMBER_INT, v);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(String encodedValue) throws IOException {
/* 776*/    _append(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeBoolean(boolean state) throws IOException {
/* 781*/    _append(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNull() throws IOException {
/* 786*/    _append(JsonToken.VALUE_NULL);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeObject(Object value) throws IOException {
/* 798*/    if (value == null) {
/* 799*/      writeNull();
/*   0*/      return;
/*   0*/    } 
/* 802*/    Class<?> raw = value.getClass();
/* 803*/    if (raw == byte[].class || value instanceof RawValue) {
/* 804*/      _append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*   0*/      return;
/*   0*/    } 
/* 807*/    if (this._objectCodec == null) {
/* 812*/      _append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*   0*/    } else {
/* 814*/      this._objectCodec.writeValue(this, value);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTree(TreeNode node) throws IOException {
/* 821*/    if (node == null) {
/* 822*/      writeNull();
/*   0*/      return;
/*   0*/    } 
/* 826*/    if (this._objectCodec == null) {
/* 828*/      _append(JsonToken.VALUE_EMBEDDED_OBJECT, node);
/*   0*/    } else {
/* 830*/      this._objectCodec.writeTree(this, node);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
/* 849*/    byte[] copy = new byte[len];
/* 850*/    System.arraycopy(data, offset, copy, 0, len);
/* 851*/    writeObject(copy);
/*   0*/  }
/*   0*/  
/*   0*/  public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) {
/* 862*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canWriteTypeId() {
/* 873*/    return this._hasNativeTypeIds;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canWriteObjectId() {
/* 878*/    return this._hasNativeObjectIds;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTypeId(Object id) {
/* 883*/    this._typeId = id;
/* 884*/    this._hasNativeId = true;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeObjectId(Object id) {
/* 889*/    this._objectId = id;
/* 890*/    this._hasNativeId = true;
/*   0*/  }
/*   0*/  
/*   0*/  public void copyCurrentEvent(JsonParser jp) throws IOException {
/* 902*/    if (this._mayHaveNativeIds) {
/* 903*/        _checkNativeIds(jp); 
/*   0*/       }
/* 905*/    switch (jp.getCurrentToken()) {
/*   0*/      case START_OBJECT:
/* 907*/        writeStartObject();
/*   0*/        break;
/*   0*/      case END_OBJECT:
/* 910*/        writeEndObject();
/*   0*/        break;
/*   0*/      case START_ARRAY:
/* 913*/        writeStartArray();
/*   0*/        break;
/*   0*/      case END_ARRAY:
/* 916*/        writeEndArray();
/*   0*/        break;
/*   0*/      case FIELD_NAME:
/* 919*/        writeFieldName(jp.getCurrentName());
/*   0*/        break;
/*   0*/      case VALUE_STRING:
/* 922*/        if (jp.hasTextCharacters()) {
/* 923*/          writeString(jp.getTextCharacters(), jp.getTextOffset(), jp.getTextLength());
/*   0*/          break;
/*   0*/        } 
/* 925*/        writeString(jp.getText());
/*   0*/        break;
/*   0*/      case VALUE_NUMBER_INT:
/* 929*/        switch (jp.getNumberType()) {
/*   0*/          case INT:
/* 931*/            writeNumber(jp.getIntValue());
/*   0*/            break;
/*   0*/          case BIG_INTEGER:
/* 934*/            writeNumber(jp.getBigIntegerValue());
/*   0*/            break;
/*   0*/        } 
/* 937*/        writeNumber(jp.getLongValue());
/*   0*/        break;
/*   0*/      case VALUE_NUMBER_FLOAT:
/* 941*/        switch (jp.getNumberType()) {
/*   0*/          case BIG_DECIMAL:
/* 943*/            writeNumber(jp.getDecimalValue());
/*   0*/            break;
/*   0*/          case FLOAT:
/* 946*/            writeNumber(jp.getFloatValue());
/*   0*/            break;
/*   0*/        } 
/* 949*/        writeNumber(jp.getDoubleValue());
/*   0*/        break;
/*   0*/      case VALUE_TRUE:
/* 953*/        writeBoolean(true);
/*   0*/        break;
/*   0*/      case VALUE_FALSE:
/* 956*/        writeBoolean(false);
/*   0*/        break;
/*   0*/      case VALUE_NULL:
/* 959*/        writeEndObject();
/*   0*/        break;
/*   0*/      case VALUE_EMBEDDED_OBJECT:
/* 962*/        writeObject(jp.getEmbeddedObject());
/*   0*/        break;
/*   0*/      default:
/* 965*/        throw new RuntimeException("Internal error: should never end up through this code path");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void copyCurrentStructure(JsonParser jp) throws IOException {
/* 972*/    JsonToken t = jp.getCurrentToken();
/* 975*/    if (t == JsonToken.FIELD_NAME) {
/* 976*/      if (this._mayHaveNativeIds) {
/* 977*/          _checkNativeIds(jp); 
/*   0*/         }
/* 979*/      writeFieldName(jp.getCurrentName());
/* 980*/      t = jp.nextToken();
/*   0*/    } 
/* 984*/    if (this._mayHaveNativeIds) {
/* 985*/        _checkNativeIds(jp); 
/*   0*/       }
/* 988*/    switch (t) {
/*   0*/      case START_ARRAY:
/* 990*/        writeStartArray();
/* 991*/        while (jp.nextToken() != JsonToken.END_ARRAY) {
/* 992*/            copyCurrentStructure(jp); 
/*   0*/           }
/* 994*/        writeEndArray();
/*   0*/        break;
/*   0*/      case START_OBJECT:
/* 997*/        writeStartObject();
/* 998*/        while (jp.nextToken() != JsonToken.END_OBJECT) {
/* 999*/            copyCurrentStructure(jp); 
/*   0*/           }
/*1001*/        writeEndObject();
/*   0*/        break;
/*   0*/      default:
/*1004*/        copyCurrentEvent(jp);
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _checkNativeIds(JsonParser jp) throws IOException {
/*1011*/    if ((this._typeId = jp.getTypeId()) != null) {
/*1012*/        this._hasNativeId = true; 
/*   0*/       }
/*1014*/    if ((this._objectId = jp.getObjectId()) != null) {
/*1015*/        this._hasNativeId = true; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _append(JsonToken type) {
/*1027*/    Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
/*1030*/    if (next == null) {
/*1031*/      this._appendAt++;
/*   0*/    } else {
/*1033*/      this._last = next;
/*1034*/      this._appendAt = 1;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _append(JsonToken type, Object value) {
/*1040*/    Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
/*1043*/    if (next == null) {
/*1044*/      this._appendAt++;
/*   0*/    } else {
/*1046*/      this._last = next;
/*1047*/      this._appendAt = 1;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _appendRaw(int rawType, Object value) {
/*1053*/    Segment next = this._hasNativeId ? this._last.appendRaw(this._appendAt, rawType, value, this._objectId, this._typeId) : this._last.appendRaw(this._appendAt, rawType, value);
/*1056*/    if (next == null) {
/*1057*/      this._appendAt++;
/*   0*/    } else {
/*1059*/      this._last = next;
/*1060*/      this._appendAt = 1;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUnsupportedOperation() {
/*1066*/    throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
/*   0*/  }
/*   0*/  
/*   0*/  protected static final class Parser extends ParserMinimalBase {
/*   0*/    protected ObjectCodec _codec;
/*   0*/    
/*   0*/    protected final boolean _hasNativeTypeIds;
/*   0*/    
/*   0*/    protected final boolean _hasNativeObjectIds;
/*   0*/    
/*   0*/    protected final boolean _hasNativeIds;
/*   0*/    
/*   0*/    protected TokenBuffer.Segment _segment;
/*   0*/    
/*   0*/    protected int _segmentPtr;
/*   0*/    
/*   0*/    protected JsonReadContext _parsingContext;
/*   0*/    
/*   0*/    protected boolean _closed;
/*   0*/    
/*   0*/    protected transient ByteArrayBuilder _byteBuilder;
/*   0*/    
/*1124*/    protected JsonLocation _location = null;
/*   0*/    
/*   0*/    public Parser(TokenBuffer.Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds) {
/*1136*/      super(0);
/*1137*/      this._segment = firstSeg;
/*1138*/      this._segmentPtr = -1;
/*1139*/      this._codec = codec;
/*1140*/      this._parsingContext = JsonReadContext.createRootContext(null);
/*1141*/      this._hasNativeTypeIds = hasNativeTypeIds;
/*1142*/      this._hasNativeObjectIds = hasNativeObjectIds;
/*1143*/      this._hasNativeIds = hasNativeTypeIds | hasNativeObjectIds;
/*   0*/    }
/*   0*/    
/*   0*/    public void setLocation(JsonLocation l) {
/*1147*/      this._location = l;
/*   0*/    }
/*   0*/    
/*   0*/    public ObjectCodec getCodec() {
/*1151*/      return this._codec;
/*   0*/    }
/*   0*/    
/*   0*/    public void setCodec(ObjectCodec c) {
/*1154*/      this._codec = c;
/*   0*/    }
/*   0*/    
/*   0*/    public Version version() {
/*1158*/      return PackageVersion.VERSION;
/*   0*/    }
/*   0*/    
/*   0*/    public JsonToken peekNextToken() throws IOException, JsonParseException {
/*1171*/      if (this._closed) {
/*1171*/          return null; 
/*   0*/         }
/*1172*/      TokenBuffer.Segment seg = this._segment;
/*1173*/      int ptr = this._segmentPtr + 1;
/*1174*/      if (ptr >= 16) {
/*1175*/        ptr = 0;
/*1176*/        seg = (seg == null) ? null : seg.next();
/*   0*/      } 
/*1178*/      return (seg == null) ? null : seg.type(ptr);
/*   0*/    }
/*   0*/    
/*   0*/    public void close() throws IOException {
/*1189*/      if (!this._closed) {
/*1190*/          this._closed = true; 
/*   0*/         }
/*   0*/    }
/*   0*/    
/*   0*/    public JsonToken nextToken() throws IOException {
/*1204*/      if (this._closed || this._segment == null) {
/*1204*/          return null; 
/*   0*/         }
/*1207*/      if (++this._segmentPtr >= 16) {
/*1208*/        this._segmentPtr = 0;
/*1209*/        this._segment = this._segment.next();
/*1210*/        if (this._segment == null) {
/*1211*/            return null; 
/*   0*/           }
/*   0*/      } 
/*1214*/      this._currToken = this._segment.type(this._segmentPtr);
/*1216*/      if (this._currToken == JsonToken.FIELD_NAME) {
/*1217*/        Object ob = _currentObject();
/*1218*/        String name = (ob instanceof String) ? (String)ob : ob.toString();
/*1219*/        this._parsingContext.setCurrentName(name);
/*1220*/      } else if (this._currToken == JsonToken.START_OBJECT) {
/*1221*/        this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
/*1222*/      } else if (this._currToken == JsonToken.START_ARRAY) {
/*1223*/        this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
/*1224*/      } else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
/*1227*/        this._parsingContext = this._parsingContext.getParent();
/*1229*/        if (this._parsingContext == null) {
/*1230*/            this._parsingContext = JsonReadContext.createRootContext(null); 
/*   0*/           }
/*   0*/      } 
/*1233*/      return this._currToken;
/*   0*/    }
/*   0*/    
/*   0*/    public String nextFieldName() throws IOException {
/*1240*/      if (this._closed || this._segment == null) {
/*1240*/          return null; 
/*   0*/         }
/*1242*/      int ptr = this._segmentPtr + 1;
/*1243*/      if (ptr < 16 && this._segment.type(ptr) == JsonToken.FIELD_NAME) {
/*1244*/        this._segmentPtr = ptr;
/*1245*/        Object ob = this._segment.get(ptr);
/*1246*/        String name = (ob instanceof String) ? (String)ob : ob.toString();
/*1247*/        this._parsingContext.setCurrentName(name);
/*1248*/        return name;
/*   0*/      } 
/*1250*/      return (nextToken() == JsonToken.FIELD_NAME) ? getCurrentName() : null;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isClosed() {
/*1254*/      return this._closed;
/*   0*/    }
/*   0*/    
/*   0*/    public JsonStreamContext getParsingContext() {
/*1263*/      return (JsonStreamContext)this._parsingContext;
/*   0*/    }
/*   0*/    
/*   0*/    public JsonLocation getTokenLocation() {
/*1266*/      return getCurrentLocation();
/*   0*/    }
/*   0*/    
/*   0*/    public JsonLocation getCurrentLocation() {
/*1270*/      return (this._location == null) ? JsonLocation.NA : this._location;
/*   0*/    }
/*   0*/    
/*   0*/    public String getCurrentName() {
/*1276*/      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/*1277*/        JsonReadContext parent = this._parsingContext.getParent();
/*1278*/        return parent.getCurrentName();
/*   0*/      } 
/*1280*/      return this._parsingContext.getCurrentName();
/*   0*/    }
/*   0*/    
/*   0*/    public void overrideCurrentName(String name) {
/*1287*/      JsonReadContext ctxt = this._parsingContext;
/*1288*/      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/*1289*/          ctxt = ctxt.getParent(); 
/*   0*/         }
/*   0*/      try {
/*1292*/        ctxt.setCurrentName(name);
/*1293*/      } catch (IOException e) {
/*1294*/        throw new RuntimeException(e);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public String getText() {
/*   0*/      Object ob;
/*1308*/      if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
/*1310*/        Object object = _currentObject();
/*1311*/        if (object instanceof String) {
/*1312*/            return (String)object; 
/*   0*/           }
/*1314*/        return (object == null) ? null : object.toString();
/*   0*/      } 
/*1316*/      if (this._currToken == null) {
/*1317*/          return null; 
/*   0*/         }
/*1319*/      switch (this._currToken) {
/*   0*/        case VALUE_NUMBER_INT:
/*   0*/        case VALUE_NUMBER_FLOAT:
/*1322*/          ob = _currentObject();
/*1323*/          return (ob == null) ? null : ob.toString();
/*   0*/      } 
/*1325*/      return this._currToken.asString();
/*   0*/    }
/*   0*/    
/*   0*/    public char[] getTextCharacters() {
/*1331*/      String str = getText();
/*1332*/      return (str == null) ? null : str.toCharArray();
/*   0*/    }
/*   0*/    
/*   0*/    public int getTextLength() {
/*1337*/      String str = getText();
/*1338*/      return (str == null) ? 0 : str.length();
/*   0*/    }
/*   0*/    
/*   0*/    public int getTextOffset() {
/*1342*/      return 0;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasTextCharacters() {
/*1347*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
/*1359*/      Number n = getNumberValue();
/*1360*/      if (n instanceof BigInteger) {
/*1361*/          return (BigInteger)n; 
/*   0*/         }
/*1363*/      if (getNumberType() == JsonParser.NumberType.BIG_DECIMAL) {
/*1364*/          return ((BigDecimal)n).toBigInteger(); 
/*   0*/         }
/*1367*/      return BigInteger.valueOf(n.longValue());
/*   0*/    }
/*   0*/    
/*   0*/    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
/*1373*/      Number n = getNumberValue();
/*1374*/      if (n instanceof BigDecimal) {
/*1375*/          return (BigDecimal)n; 
/*   0*/         }
/*1377*/      switch (getNumberType()) {
/*   0*/        case INT:
/*   0*/        case LONG:
/*1380*/          return BigDecimal.valueOf(n.longValue());
/*   0*/        case BIG_INTEGER:
/*1382*/          return new BigDecimal((BigInteger)n);
/*   0*/      } 
/*1386*/      return BigDecimal.valueOf(n.doubleValue());
/*   0*/    }
/*   0*/    
/*   0*/    public double getDoubleValue() throws IOException, JsonParseException {
/*1391*/      return getNumberValue().doubleValue();
/*   0*/    }
/*   0*/    
/*   0*/    public float getFloatValue() throws IOException, JsonParseException {
/*1396*/      return getNumberValue().floatValue();
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() throws IOException, JsonParseException {
/*1403*/      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*1404*/          return ((Number)_currentObject()).intValue(); 
/*   0*/         }
/*1406*/      return getNumberValue().intValue();
/*   0*/    }
/*   0*/    
/*   0*/    public long getLongValue() throws IOException, JsonParseException {
/*1411*/      return getNumberValue().longValue();
/*   0*/    }
/*   0*/    
/*   0*/    public JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
/*1417*/      Number n = getNumberValue();
/*1418*/      if (n instanceof Integer) {
/*1418*/          return JsonParser.NumberType.INT; 
/*   0*/         }
/*1419*/      if (n instanceof Long) {
/*1419*/          return JsonParser.NumberType.LONG; 
/*   0*/         }
/*1420*/      if (n instanceof Double) {
/*1420*/          return JsonParser.NumberType.DOUBLE; 
/*   0*/         }
/*1421*/      if (n instanceof BigDecimal) {
/*1421*/          return JsonParser.NumberType.BIG_DECIMAL; 
/*   0*/         }
/*1422*/      if (n instanceof BigInteger) {
/*1422*/          return JsonParser.NumberType.BIG_INTEGER; 
/*   0*/         }
/*1423*/      if (n instanceof Float) {
/*1423*/          return JsonParser.NumberType.FLOAT; 
/*   0*/         }
/*1424*/      if (n instanceof Short) {
/*1424*/          return JsonParser.NumberType.INT; 
/*   0*/         }
/*1425*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public final Number getNumberValue() throws IOException, JsonParseException {
/*1430*/      _checkIsNumber();
/*1431*/      Object value = _currentObject();
/*1432*/      if (value instanceof Number) {
/*1433*/          return (Number)value; 
/*   0*/         }
/*1438*/      if (value instanceof String) {
/*1439*/        String str = (String)value;
/*1440*/        if (str.indexOf('.') >= 0) {
/*1441*/            return Double.parseDouble(str); 
/*   0*/           }
/*1443*/        return Long.parseLong(str);
/*   0*/      } 
/*1445*/      if (value == null) {
/*1446*/          return null; 
/*   0*/         }
/*1448*/      throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + value.getClass().getName());
/*   0*/    }
/*   0*/    
/*   0*/    public Object getEmbeddedObject() {
/*1461*/      if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/*1462*/          return _currentObject(); 
/*   0*/         }
/*1464*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
/*1472*/      if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/*1474*/        Object ob = _currentObject();
/*1475*/        if (ob instanceof byte[]) {
/*1476*/            return (byte[])ob; 
/*   0*/           }
/*   0*/      } 
/*1480*/      if (this._currToken != JsonToken.VALUE_STRING) {
/*1481*/          throw _constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), can not access as binary"); 
/*   0*/         }
/*1483*/      String str = getText();
/*1484*/      if (str == null) {
/*1485*/          return null; 
/*   0*/         }
/*1487*/      ByteArrayBuilder builder = this._byteBuilder;
/*1488*/      if (builder == null) {
/*1489*/        this._byteBuilder = builder = new ByteArrayBuilder(100);
/*   0*/      } else {
/*1491*/        this._byteBuilder.reset();
/*   0*/      } 
/*1493*/      _decodeBase64(str, builder, b64variant);
/*1494*/      return builder.toByteArray();
/*   0*/    }
/*   0*/    
/*   0*/    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException, JsonParseException {
/*1501*/      byte[] data = getBinaryValue(b64variant);
/*1502*/      if (data != null) {
/*1503*/        out.write(data, 0, data.length);
/*1504*/        return data.length;
/*   0*/      } 
/*1506*/      return 0;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean canReadObjectId() {
/*1517*/      return this._hasNativeObjectIds;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean canReadTypeId() {
/*1522*/      return this._hasNativeTypeIds;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getTypeId() {
/*1527*/      return this._segment.findTypeId(this._segmentPtr);
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectId() {
/*1532*/      return this._segment.findObjectId(this._segmentPtr);
/*   0*/    }
/*   0*/    
/*   0*/    protected final Object _currentObject() {
/*1542*/      return this._segment.get(this._segmentPtr);
/*   0*/    }
/*   0*/    
/*   0*/    protected final void _checkIsNumber() throws JsonParseException {
/*1547*/      if (this._currToken == null || !this._currToken.isNumeric()) {
/*1548*/          throw _constructError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors"); 
/*   0*/         }
/*   0*/    }
/*   0*/    
/*   0*/    protected void _handleEOF() throws JsonParseException {
/*1554*/      _throwInternal();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  protected static final class Segment {
/*   0*/    public static final int TOKENS_PER_SEGMENT = 16;
/*   0*/    
/*1576*/    private static final JsonToken[] TOKEN_TYPES_BY_INDEX = new JsonToken[16];
/*   0*/    
/*   0*/    protected Segment _next;
/*   0*/    
/*   0*/    protected long _tokenTypes;
/*   0*/    
/*   0*/    static {
/*1577*/      JsonToken[] t = JsonToken.values();
/*1579*/      System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
/*   0*/    }
/*   0*/    
/*1597*/    protected final Object[] _tokens = new Object[16];
/*   0*/    
/*   0*/    protected TreeMap<Integer, Object> _nativeIds;
/*   0*/    
/*   0*/    public JsonToken type(int index) {
/*1610*/      long l = this._tokenTypes;
/*1611*/      if (index > 0) {
/*1612*/          l >>= index << 2; 
/*   0*/         }
/*1614*/      int ix = (int)l & 0xF;
/*1615*/      return TOKEN_TYPES_BY_INDEX[ix];
/*   0*/    }
/*   0*/    
/*   0*/    public int rawType(int index) {
/*1620*/      long l = this._tokenTypes;
/*1621*/      if (index > 0) {
/*1622*/          l >>= index << 2; 
/*   0*/         }
/*1624*/      int ix = (int)l & 0xF;
/*1625*/      return ix;
/*   0*/    }
/*   0*/    
/*   0*/    public Object get(int index) {
/*1629*/      return this._tokens[index];
/*   0*/    }
/*   0*/    
/*   0*/    public Segment next() {
/*1632*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasIds() {
/*1639*/      return (this._nativeIds != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Segment append(int index, JsonToken tokenType) {
/*1646*/      if (index < 16) {
/*1647*/        set(index, tokenType);
/*1648*/        return null;
/*   0*/      } 
/*1650*/      this._next = new Segment();
/*1651*/      this._next.set(0, tokenType);
/*1652*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    public Segment append(int index, JsonToken tokenType, Object objectId, Object typeId) {
/*1658*/      if (index < 16) {
/*1659*/        set(index, tokenType, objectId, typeId);
/*1660*/        return null;
/*   0*/      } 
/*1662*/      this._next = new Segment();
/*1663*/      this._next.set(0, tokenType, objectId, typeId);
/*1664*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    public Segment append(int index, JsonToken tokenType, Object value) {
/*1669*/      if (index < 16) {
/*1670*/        set(index, tokenType, value);
/*1671*/        return null;
/*   0*/      } 
/*1673*/      this._next = new Segment();
/*1674*/      this._next.set(0, tokenType, value);
/*1675*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    public Segment append(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
/*1681*/      if (index < 16) {
/*1682*/        set(index, tokenType, value, objectId, typeId);
/*1683*/        return null;
/*   0*/      } 
/*1685*/      this._next = new Segment();
/*1686*/      this._next.set(0, tokenType, value, objectId, typeId);
/*1687*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    public Segment appendRaw(int index, int rawTokenType, Object value) {
/*1692*/      if (index < 16) {
/*1693*/        set(index, rawTokenType, value);
/*1694*/        return null;
/*   0*/      } 
/*1696*/      this._next = new Segment();
/*1697*/      this._next.set(0, rawTokenType, value);
/*1698*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    public Segment appendRaw(int index, int rawTokenType, Object value, Object objectId, Object typeId) {
/*1704*/      if (index < 16) {
/*1705*/        set(index, rawTokenType, value, objectId, typeId);
/*1706*/        return null;
/*   0*/      } 
/*1708*/      this._next = new Segment();
/*1709*/      this._next.set(0, rawTokenType, value, objectId, typeId);
/*1710*/      return this._next;
/*   0*/    }
/*   0*/    
/*   0*/    private void set(int index, JsonToken tokenType) {
/*1718*/      long typeCode = tokenType.ordinal();
/*1719*/      if (index > 0) {
/*1720*/          typeCode <<= index << 2; 
/*   0*/         }
/*1722*/      this._tokenTypes |= typeCode;
/*   0*/    }
/*   0*/    
/*   0*/    private void set(int index, JsonToken tokenType, Object objectId, Object typeId) {
/*1728*/      long typeCode = tokenType.ordinal();
/*1729*/      if (index > 0) {
/*1730*/          typeCode <<= index << 2; 
/*   0*/         }
/*1732*/      this._tokenTypes |= typeCode;
/*1733*/      assignNativeIds(index, objectId, typeId);
/*   0*/    }
/*   0*/    
/*   0*/    private void set(int index, JsonToken tokenType, Object value) {
/*1738*/      this._tokens[index] = value;
/*1739*/      long typeCode = tokenType.ordinal();
/*1740*/      if (index > 0) {
/*1741*/          typeCode <<= index << 2; 
/*   0*/         }
/*1743*/      this._tokenTypes |= typeCode;
/*   0*/    }
/*   0*/    
/*   0*/    private void set(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
/*1749*/      this._tokens[index] = value;
/*1750*/      long typeCode = tokenType.ordinal();
/*1751*/      if (index > 0) {
/*1752*/          typeCode <<= index << 2; 
/*   0*/         }
/*1754*/      this._tokenTypes |= typeCode;
/*1755*/      assignNativeIds(index, objectId, typeId);
/*   0*/    }
/*   0*/    
/*   0*/    private void set(int index, int rawTokenType, Object value) {
/*1760*/      this._tokens[index] = value;
/*1761*/      long typeCode = rawTokenType;
/*1762*/      if (index > 0) {
/*1763*/          typeCode <<= index << 2; 
/*   0*/         }
/*1765*/      this._tokenTypes |= typeCode;
/*   0*/    }
/*   0*/    
/*   0*/    private void set(int index, int rawTokenType, Object value, Object objectId, Object typeId) {
/*1770*/      this._tokens[index] = value;
/*1771*/      long typeCode = rawTokenType;
/*1772*/      if (index > 0) {
/*1773*/          typeCode <<= index << 2; 
/*   0*/         }
/*1775*/      this._tokenTypes |= typeCode;
/*1776*/      assignNativeIds(index, objectId, typeId);
/*   0*/    }
/*   0*/    
/*   0*/    private final void assignNativeIds(int index, Object objectId, Object typeId) {
/*1781*/      if (this._nativeIds == null) {
/*1782*/          this._nativeIds = new TreeMap<Integer, Object>(); 
/*   0*/         }
/*1784*/      if (objectId != null) {
/*1785*/          this._nativeIds.put(_objectIdIndex(index), objectId); 
/*   0*/         }
/*1787*/      if (typeId != null) {
/*1788*/          this._nativeIds.put(_typeIdIndex(index), typeId); 
/*   0*/         }
/*   0*/    }
/*   0*/    
/*   0*/    public Object findObjectId(int index) {
/*1796*/      return (this._nativeIds == null) ? null : this._nativeIds.get(_objectIdIndex(index));
/*   0*/    }
/*   0*/    
/*   0*/    public Object findTypeId(int index) {
/*1803*/      return (this._nativeIds == null) ? null : this._nativeIds.get(_typeIdIndex(index));
/*   0*/    }
/*   0*/    
/*   0*/    private final int _typeIdIndex(int i) {
/*1806*/      return i + i;
/*   0*/    }
/*   0*/    
/*   0*/    private final int _objectIdIndex(int i) {
/*1807*/      return i + i + 1;
/*   0*/    }
/*   0*/  }
/*   0*/}
