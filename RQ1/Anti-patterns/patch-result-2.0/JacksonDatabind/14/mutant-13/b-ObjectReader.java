/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.FormatSchema;
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.core.JsonParseException;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.TreeNode;
/*   0*/import com.fasterxml.jackson.core.Version;
/*   0*/import com.fasterxml.jackson.core.Versioned;
/*   0*/import com.fasterxml.jackson.core.type.ResolvedType;
/*   0*/import com.fasterxml.jackson.core.type.TypeReference;
/*   0*/import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*   0*/import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*   0*/import com.fasterxml.jackson.databind.deser.DataFormatReaders;
/*   0*/import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*   0*/import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*   0*/import com.fasterxml.jackson.databind.node.NullNode;
/*   0*/import com.fasterxml.jackson.databind.node.TreeTraversingParser;
/*   0*/import com.fasterxml.jackson.databind.type.SimpleType;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.RootNameLookup;
/*   0*/import java.io.File;
/*   0*/import java.io.FileInputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.Reader;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.net.URL;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.TimeZone;
/*   0*/import java.util.concurrent.ConcurrentHashMap;
/*   0*/
/*   0*/public class ObjectReader extends ObjectCodec implements Versioned, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  43*/  private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
/*   0*/  
/*   0*/  protected final DeserializationConfig _config;
/*   0*/  
/*   0*/  protected final DefaultDeserializationContext _context;
/*   0*/  
/*   0*/  protected final JsonFactory _parserFactory;
/*   0*/  
/*   0*/  protected final boolean _unwrapRoot;
/*   0*/  
/*   0*/  protected final JavaType _valueType;
/*   0*/  
/*   0*/  protected final JsonDeserializer<Object> _rootDeserializer;
/*   0*/  
/*   0*/  protected final Object _valueToUpdate;
/*   0*/  
/*   0*/  protected final FormatSchema _schema;
/*   0*/  
/*   0*/  protected final InjectableValues _injectableValues;
/*   0*/  
/*   0*/  protected final DataFormatReaders _dataFormatReaders;
/*   0*/  
/*   0*/  protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
/*   0*/  
/*   0*/  protected final RootNameLookup _rootNames;
/*   0*/  
/*   0*/  protected ObjectReader(ObjectMapper mapper, DeserializationConfig config) {
/* 162*/    this(mapper, config, null, null, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
/* 173*/    this._config = config;
/* 174*/    this._context = mapper._deserializationContext;
/* 175*/    this._rootDeserializers = mapper._rootDeserializers;
/* 176*/    this._parserFactory = mapper._jsonFactory;
/* 177*/    this._rootNames = mapper._rootNames;
/* 178*/    this._valueType = valueType;
/* 179*/    this._valueToUpdate = valueToUpdate;
/* 180*/    if (valueToUpdate != null && valueType.isArrayType()) {
/* 181*/        throw new IllegalArgumentException("Can not update an array value"); 
/*   0*/       }
/* 183*/    this._schema = schema;
/* 184*/    this._injectableValues = injectableValues;
/* 185*/    this._unwrapRoot = config.useRootWrapping();
/* 187*/    this;
/* 187*/    this._rootDeserializer = null;
/* 188*/    this._dataFormatReaders = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
/* 199*/    this._config = config;
/* 200*/    this._context = base._context;
/* 202*/    this._rootDeserializers = base._rootDeserializers;
/* 203*/    this._parserFactory = base._parserFactory;
/* 204*/    this._rootNames = base._rootNames;
/* 206*/    this._valueType = valueType;
/* 207*/    this._rootDeserializer = rootDeser;
/* 208*/    this._valueToUpdate = valueToUpdate;
/* 209*/    if (valueToUpdate != null && valueType.isArrayType()) {
/* 210*/        throw new IllegalArgumentException("Can not update an array value"); 
/*   0*/       }
/* 212*/    this._schema = schema;
/* 213*/    this._injectableValues = injectableValues;
/* 214*/    this._unwrapRoot = config.useRootWrapping();
/* 215*/    this._dataFormatReaders = dataFormatReaders;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, DeserializationConfig config) {
/* 223*/    this._config = config;
/* 224*/    this._context = base._context;
/* 226*/    this._rootDeserializers = base._rootDeserializers;
/* 227*/    this._parserFactory = base._parserFactory;
/* 228*/    this._rootNames = base._rootNames;
/* 230*/    this._valueType = base._valueType;
/* 231*/    this._rootDeserializer = base._rootDeserializer;
/* 232*/    this._valueToUpdate = base._valueToUpdate;
/* 233*/    this._schema = base._schema;
/* 234*/    this._injectableValues = base._injectableValues;
/* 235*/    this._unwrapRoot = config.useRootWrapping();
/* 236*/    this._dataFormatReaders = base._dataFormatReaders;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, JsonFactory f) {
/* 242*/    this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
/* 244*/    this._context = base._context;
/* 246*/    this._rootDeserializers = base._rootDeserializers;
/* 247*/    this._parserFactory = f;
/* 248*/    this._rootNames = base._rootNames;
/* 250*/    this._valueType = base._valueType;
/* 251*/    this._rootDeserializer = base._rootDeserializer;
/* 252*/    this._valueToUpdate = base._valueToUpdate;
/* 253*/    this._schema = base._schema;
/* 254*/    this._injectableValues = base._injectableValues;
/* 255*/    this._unwrapRoot = base._unwrapRoot;
/* 256*/    this._dataFormatReaders = base._dataFormatReaders;
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 265*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _new(ObjectReader base, JsonFactory f) {
/* 282*/    return new ObjectReader(base, f);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _new(ObjectReader base, DeserializationConfig config) {
/* 291*/    return new ObjectReader(base, config);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
/* 303*/    return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  protected <T> MappingIterator<T> _newIterator(JavaType valueType, JsonParser parser, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged, Object valueToUpdate) {
/* 317*/    return new MappingIterator<T>(valueType, parser, ctxt, deser, parserManaged, valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonToken _initForReading(JsonParser p) throws IOException {
/* 334*/    if (this._schema != null) {
/* 335*/        p.setSchema(this._schema); 
/*   0*/       }
/* 337*/    this._config.initialize(p);
/* 343*/    JsonToken t = p.getCurrentToken();
/* 344*/    if (t == null) {
/* 345*/      t = p.nextToken();
/* 346*/      if (t == null) {
/* 348*/          throw JsonMappingException.from(p, "No content to map due to end-of-input"); 
/*   0*/         }
/*   0*/    } 
/* 351*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _initForMultiRead(JsonParser p) throws IOException {
/* 364*/    if (this._schema != null) {
/* 365*/        p.setSchema(this._schema); 
/*   0*/       }
/* 367*/    this._config.initialize(p);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(DeserializationFeature feature) {
/* 381*/    return _with(this._config.with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(DeserializationFeature first, DeserializationFeature... other) {
/* 391*/    return _with(this._config.with(first, other));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFeatures(DeserializationFeature... features) {
/* 399*/    return _with(this._config.withFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(DeserializationFeature feature) {
/* 407*/    return _with(this._config.without(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(DeserializationFeature first, DeserializationFeature... other) {
/* 416*/    return _with(this._config.without(first, other));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutFeatures(DeserializationFeature... features) {
/* 424*/    return _with(this._config.withoutFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(JsonParser.Feature feature) {
/* 438*/    return _with(this._config.with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFeatures(JsonParser.Feature... features) {
/* 446*/    return _with(this._config.withFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(JsonParser.Feature feature) {
/* 454*/    return _with(this._config.without(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutFeatures(JsonParser.Feature... features) {
/* 462*/    return _with(this._config.withoutFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(DeserializationConfig config) {
/* 472*/    return _with(config);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(InjectableValues injectableValues) {
/* 484*/    if (this._injectableValues == injectableValues) {
/* 485*/        return this; 
/*   0*/       }
/* 487*/    return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(JsonNodeFactory f) {
/* 501*/    return _with(this._config.with(f));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(JsonFactory f) {
/* 516*/    if (f == this._parserFactory) {
/* 517*/        return this; 
/*   0*/       }
/* 519*/    ObjectReader r = _new(this, f);
/* 521*/    if (f.getCodec() == null) {
/* 522*/        f.setCodec(r); 
/*   0*/       }
/* 524*/    return r;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withRootName(String rootName) {
/* 537*/    return _with(this._config.withRootName(rootName));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(FormatSchema schema) {
/* 550*/    if (this._schema == schema) {
/* 551*/        return this; 
/*   0*/       }
/* 553*/    _verifySchemaType(schema);
/* 554*/    return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader forType(JavaType valueType) {
/* 569*/    if (valueType != null && valueType.equals(this._valueType)) {
/* 570*/        return this; 
/*   0*/       }
/* 572*/    JsonDeserializer<Object> rootDeser = _prefetchRootDeserializer(this._config, valueType);
/* 574*/    DataFormatReaders det = this._dataFormatReaders;
/* 575*/    if (det != null) {
/* 576*/        det = det.withType(valueType); 
/*   0*/       }
/* 578*/    return _new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader forType(Class<?> valueType) {
/* 592*/    return forType(this._config.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader forType(TypeReference<?> valueTypeRef) {
/* 605*/    return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(JavaType valueType) {
/* 613*/    return forType(valueType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(Class<?> valueType) {
/* 621*/    return forType(this._config.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(Type valueType) {
/* 629*/    return forType(this._config.getTypeFactory().constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(TypeReference<?> valueTypeRef) {
/* 637*/    return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withValueToUpdate(Object value) {
/*   0*/    JavaType t;
/* 650*/    if (value == this._valueToUpdate) {
/* 650*/        return this; 
/*   0*/       }
/* 651*/    if (value == null) {
/* 652*/        throw new IllegalArgumentException("cat not update null value"); 
/*   0*/       }
/* 660*/    if (this._valueType == null) {
/* 661*/      t = this._config.constructType(value.getClass());
/*   0*/    } else {
/* 663*/      t = this._valueType;
/*   0*/    } 
/* 665*/    return _new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withView(Class<?> activeView) {
/* 677*/    return _with(this._config.withView(activeView));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(Locale l) {
/* 681*/    return _with(this._config.with(l));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(TimeZone tz) {
/* 685*/    return _with(this._config.with(tz));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withHandler(DeserializationProblemHandler h) {
/* 689*/    return _with(this._config.withHandler(h));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(Base64Variant defaultBase64) {
/* 693*/    return _with(this._config.with(defaultBase64));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFormatDetection(ObjectReader... readers) {
/* 719*/    return withFormatDetection(new DataFormatReaders(readers));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFormatDetection(DataFormatReaders readers) {
/* 738*/    return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(ContextAttributes attrs) {
/* 746*/    return _with(this._config.with(attrs));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withAttributes(Map<Object, Object> attrs) {
/* 753*/    return _with(this._config.withAttributes(attrs));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withAttribute(Object key, Object value) {
/* 760*/    return _with(this._config.withAttribute(key, value));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutAttribute(Object key) {
/* 767*/    return _with(this._config.withoutAttribute(key));
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _with(DeserializationConfig newConfig) {
/* 777*/    if (newConfig == this._config) {
/* 778*/        return this; 
/*   0*/       }
/* 780*/    ObjectReader r = _new(this, newConfig);
/* 781*/    if (this._dataFormatReaders != null) {
/* 782*/        r = r.withFormatDetection(this._dataFormatReaders.with(newConfig)); 
/*   0*/       }
/* 784*/    return r;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(DeserializationFeature f) {
/* 794*/    return this._config.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(MapperFeature f) {
/* 798*/    return this._config.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(JsonParser.Feature f) {
/* 802*/    return this._parserFactory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig getConfig() {
/* 809*/    return this._config;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory getFactory() {
/* 817*/    return this._parserFactory;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory getJsonFactory() {
/* 826*/    return this._parserFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory getTypeFactory() {
/* 830*/    return this._config.getTypeFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public ContextAttributes getAttributes() {
/* 837*/    return this._config.getAttributes();
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp) throws IOException, JsonProcessingException {
/* 860*/    return (T)_bind(jp, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, Class<T> valueType) throws IOException, JsonProcessingException {
/* 878*/    return withType(valueType).readValue(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
/* 896*/    return withType(valueTypeRef).readValue(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, ResolvedType valueType) throws IOException, JsonProcessingException {
/* 912*/    return withType((JavaType)valueType).readValue(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, JavaType valueType) throws IOException, JsonProcessingException {
/* 923*/    return withType(valueType).readValue(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser jp, Class<T> valueType) throws IOException, JsonProcessingException {
/* 938*/    return withType(valueType).readValues(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
/* 953*/    return withType(valueTypeRef).readValues(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser jp, ResolvedType valueType) throws IOException, JsonProcessingException {
/* 968*/    return readValues(jp, (JavaType)valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser jp, JavaType valueType) throws IOException, JsonProcessingException {
/* 982*/    return withType(valueType).readValues(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode createArrayNode() {
/* 993*/    return this._config.getNodeFactory().arrayNode();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode createObjectNode() {
/* 998*/    return this._config.getNodeFactory().objectNode();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser treeAsTokens(TreeNode n) {
/*1003*/    return (JsonParser)new TreeTraversingParser((JsonNode)n, this);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends TreeNode> T readTree(JsonParser jp) throws IOException, JsonProcessingException {
/*1022*/    return (T)_bindAsTree(jp);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTree(JsonGenerator jgen, TreeNode rootNode) {
/*1027*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(InputStream src) throws IOException, JsonProcessingException {
/*1046*/    if (this._dataFormatReaders != null) {
/*1047*/        return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(src), false); 
/*   0*/       }
/*1049*/    return (T)_bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(Reader src) throws IOException, JsonProcessingException {
/*1062*/    if (this._dataFormatReaders != null) {
/*1063*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1065*/    return (T)_bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(String src) throws IOException, JsonProcessingException {
/*1078*/    if (this._dataFormatReaders != null) {
/*1079*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1081*/    return (T)_bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src) throws IOException, JsonProcessingException {
/*1094*/    if (this._dataFormatReaders != null) {
/*1095*/        return (T)_detectBindAndClose(src, 0, src.length); 
/*   0*/       }
/*1097*/    return (T)_bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, int offset, int length) throws IOException, JsonProcessingException {
/*1110*/    if (this._dataFormatReaders != null) {
/*1111*/        return (T)_detectBindAndClose(src, offset, length); 
/*   0*/       }
/*1113*/    return (T)_bindAndClose(this._parserFactory.createParser(src, offset, length), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(File src) throws IOException, JsonProcessingException {
/*1120*/    if (this._dataFormatReaders != null) {
/*1121*/        return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true); 
/*   0*/       }
/*1123*/    return (T)_bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(URL src) throws IOException, JsonProcessingException {
/*1136*/    if (this._dataFormatReaders != null) {
/*1137*/        return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true); 
/*   0*/       }
/*1139*/    return (T)_bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonNode src) throws IOException, JsonProcessingException {
/*1153*/    if (this._dataFormatReaders != null) {
/*1154*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1156*/    return (T)_bindAndClose(treeAsTokens(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(InputStream in) throws IOException, JsonProcessingException {
/*1171*/    if (this._dataFormatReaders != null) {
/*1172*/        return _detectBindAndCloseAsTree(in); 
/*   0*/       }
/*1174*/    return _bindAndCloseAsTree(this._parserFactory.createParser(in));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(Reader r) throws IOException, JsonProcessingException {
/*1189*/    if (this._dataFormatReaders != null) {
/*1190*/        _reportUndetectableSource(r); 
/*   0*/       }
/*1192*/    return _bindAndCloseAsTree(this._parserFactory.createParser(r));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(String json) throws IOException, JsonProcessingException {
/*1207*/    if (this._dataFormatReaders != null) {
/*1208*/        _reportUndetectableSource(json); 
/*   0*/       }
/*1210*/    return _bindAndCloseAsTree(this._parserFactory.createParser(json));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(JsonParser jp) throws IOException, JsonProcessingException {
/*1233*/    DeserializationContext ctxt = createDeserializationContext(jp, this._config);
/*1235*/    return _newIterator(this._valueType, jp, ctxt, _findRootDeserializer(ctxt, this._valueType), false, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(InputStream src) throws IOException, JsonProcessingException {
/*1263*/    if (this._dataFormatReaders != null) {
/*1264*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false); 
/*   0*/       }
/*1266*/    return _bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(Reader src) throws IOException, JsonProcessingException {
/*1276*/    if (this._dataFormatReaders != null) {
/*1277*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1279*/    JsonParser p = this._parserFactory.createParser(src);
/*1280*/    _initForMultiRead(p);
/*1281*/    p.nextToken();
/*1282*/    DeserializationContext ctxt = createDeserializationContext(p, this._config);
/*1283*/    return _newIterator(this._valueType, p, ctxt, _findRootDeserializer(ctxt, this._valueType), true, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(String json) throws IOException, JsonProcessingException {
/*1296*/    if (this._dataFormatReaders != null) {
/*1297*/        _reportUndetectableSource(json); 
/*   0*/       }
/*1299*/    JsonParser p = this._parserFactory.createParser(json);
/*1300*/    _initForMultiRead(p);
/*1301*/    p.nextToken();
/*1302*/    DeserializationContext ctxt = createDeserializationContext(p, this._config);
/*1303*/    return _newIterator(this._valueType, p, ctxt, _findRootDeserializer(ctxt, this._valueType), true, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(byte[] src, int offset, int length) throws IOException, JsonProcessingException {
/*1313*/    if (this._dataFormatReaders != null) {
/*1314*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false); 
/*   0*/       }
/*1316*/    return _bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public final <T> MappingIterator<T> readValues(byte[] src) throws IOException, JsonProcessingException {
/*1324*/    return readValues(src, 0, src.length);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(File src) throws IOException, JsonProcessingException {
/*1333*/    if (this._dataFormatReaders != null) {
/*1334*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), false); 
/*   0*/       }
/*1337*/    return _bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(URL src) throws IOException, JsonProcessingException {
/*1348*/    if (this._dataFormatReaders != null) {
/*1349*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), true); 
/*   0*/       }
/*1352*/    return _bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
/*   0*/    try {
/*1365*/      return readValue(treeAsTokens(n), valueType);
/*1366*/    } catch (JsonProcessingException e) {
/*1367*/      throw e;
/*1368*/    } catch (IOException e) {
/*1369*/      throw new IllegalArgumentException(e.getMessage(), e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeValue(JsonGenerator jgen, Object value) throws IOException, JsonProcessingException {
/*1375*/    throw new UnsupportedOperationException("Not implemented for ObjectReader");
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _bind(JsonParser jp, Object valueToUpdate) throws IOException {
/*   0*/    Object result;
/*1393*/    JsonToken t = _initForReading(jp);
/*1394*/    if (t == JsonToken.VALUE_NULL) {
/*1395*/      if (valueToUpdate == null) {
/*1396*/        DeserializationContext ctxt = createDeserializationContext(jp, this._config);
/*1397*/        result = _findRootDeserializer(ctxt, this._valueType).getNullValue();
/*   0*/      } else {
/*1399*/        result = valueToUpdate;
/*   0*/      } 
/*1401*/    } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*1402*/      result = valueToUpdate;
/*   0*/    } else {
/*1404*/      DeserializationContext ctxt = createDeserializationContext(jp, this._config);
/*1405*/      JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, this._valueType);
/*1406*/      if (this._unwrapRoot) {
/*1407*/        result = _unwrapAndDeserialize(jp, ctxt, this._valueType, deser);
/*1409*/      } else if (valueToUpdate == null) {
/*1410*/        result = deser.deserialize(jp, ctxt);
/*   0*/      } else {
/*1412*/        deser.deserialize(jp, ctxt, valueToUpdate);
/*1413*/        result = valueToUpdate;
/*   0*/      } 
/*   0*/    } 
/*1418*/    jp.clearCurrentToken();
/*1419*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _bindAndClose(JsonParser jp, Object valueToUpdate) throws IOException {
/*   0*/    try {
/*   0*/      Object result;
/*1426*/      JsonToken t = _initForReading(jp);
/*1427*/      if (t == JsonToken.VALUE_NULL) {
/*1428*/        if (valueToUpdate == null) {
/*1429*/          DeserializationContext ctxt = createDeserializationContext(jp, this._config);
/*1430*/          result = _findRootDeserializer(ctxt, this._valueType).getNullValue();
/*   0*/        } else {
/*1432*/          result = valueToUpdate;
/*   0*/        } 
/*1434*/      } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*1435*/        result = valueToUpdate;
/*   0*/      } else {
/*1437*/        DeserializationContext ctxt = createDeserializationContext(jp, this._config);
/*1438*/        JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, this._valueType);
/*1439*/        if (this._unwrapRoot) {
/*1440*/          result = _unwrapAndDeserialize(jp, ctxt, this._valueType, deser);
/*1442*/        } else if (valueToUpdate == null) {
/*1443*/          result = deser.deserialize(jp, ctxt);
/*   0*/        } else {
/*1445*/          deser.deserialize(jp, ctxt, valueToUpdate);
/*1446*/          result = valueToUpdate;
/*   0*/        } 
/*   0*/      } 
/*1450*/      return result;
/*   0*/    } finally {
/*   0*/      try {
/*1453*/        jp.close();
/*1454*/      } catch (IOException iOException) {}
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonNode _bindAndCloseAsTree(JsonParser jp) throws IOException {
/*   0*/    try {
/*1460*/      return _bindAsTree(jp);
/*   0*/    } finally {
/*   0*/      try {
/*1463*/        jp.close();
/*1464*/      } catch (IOException iOException) {}
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonNode _bindAsTree(JsonParser jp) throws IOException {
/*   0*/    JsonNode result;
/*1471*/    JsonToken t = _initForReading(jp);
/*1472*/    if (t == JsonToken.VALUE_NULL || t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*1473*/      result = NullNode.instance;
/*   0*/    } else {
/*1475*/      DeserializationContext ctxt = createDeserializationContext(jp, this._config);
/*1476*/      JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, JSON_NODE_TYPE);
/*1477*/      if (this._unwrapRoot) {
/*1478*/        result = (JsonNode)_unwrapAndDeserialize(jp, ctxt, JSON_NODE_TYPE, deser);
/*   0*/      } else {
/*1480*/        result = (JsonNode)deser.deserialize(jp, ctxt);
/*   0*/      } 
/*   0*/    } 
/*1484*/    jp.clearCurrentToken();
/*1485*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p, Object valueToUpdate) throws IOException {
/*1493*/    _initForMultiRead(p);
/*1494*/    p.nextToken();
/*1495*/    DeserializationContext ctxt = createDeserializationContext(p, this._config);
/*1496*/    return _newIterator(this._valueType, p, ctxt, _findRootDeserializer(ctxt, this._valueType), true, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _unwrapAndDeserialize(JsonParser jp, DeserializationContext ctxt, JavaType rootType, JsonDeserializer<Object> deser) throws IOException {
/*   0*/    Object result;
/*1503*/    String expName = this._config.getRootName();
/*1504*/    if (expName == null) {
/*1505*/      PropertyName pname = this._rootNames.findRootName(rootType, this._config);
/*1506*/      expName = pname.getSimpleName();
/*   0*/    } 
/*1508*/    if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
/*1509*/        throw JsonMappingException.from(jp, "Current token not START_OBJECT (needed to unwrap root name '" + expName + "'), but " + jp.getCurrentToken()); 
/*   0*/       }
/*1512*/    if (jp.nextToken() != JsonToken.FIELD_NAME) {
/*1513*/        throw JsonMappingException.from(jp, "Current token not FIELD_NAME (to contain expected root name '" + expName + "'), but " + jp.getCurrentToken()); 
/*   0*/       }
/*1516*/    String actualName = jp.getCurrentName();
/*1517*/    if (!expName.equals(actualName)) {
/*1518*/        throw JsonMappingException.from(jp, "Root name '" + actualName + "' does not match expected ('" + expName + "') for type " + rootType); 
/*   0*/       }
/*1522*/    jp.nextToken();
/*1524*/    if (this._valueToUpdate == null) {
/*1525*/      result = deser.deserialize(jp, ctxt);
/*   0*/    } else {
/*1527*/      deser.deserialize(jp, ctxt, this._valueToUpdate);
/*1528*/      result = this._valueToUpdate;
/*   0*/    } 
/*1531*/    if (jp.nextToken() != JsonToken.END_OBJECT) {
/*1532*/        throw JsonMappingException.from(jp, "Current token not END_OBJECT (to match wrapper object with root name '" + expName + "'), but " + jp.getCurrentToken()); 
/*   0*/       }
/*1535*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws JsonMappingException {
/*1551*/    if (this._rootDeserializer != null) {
/*1552*/        return this._rootDeserializer; 
/*   0*/       }
/*1556*/    if (valueType == null) {
/*1557*/        throw new JsonMappingException("No value type configured for ObjectReader"); 
/*   0*/       }
/*1561*/    JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
/*1562*/    if (deser != null) {
/*1563*/        return deser; 
/*   0*/       }
/*1566*/    deser = ctxt.findRootValueDeserializer(valueType);
/*1567*/    if (deser == null) {
/*1568*/        throw new JsonMappingException("Can not find a deserializer for type " + valueType); 
/*   0*/       }
/*1570*/    this._rootDeserializers.put(valueType, deser);
/*1571*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _prefetchRootDeserializer(DeserializationConfig config, JavaType valueType) {
/*1586*/    if (valueType == null || !this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
/*1587*/        return null; 
/*   0*/       }
/*1590*/    JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
/*1591*/    if (deser == null) {
/*   0*/        try {
/*1594*/          DeserializationContext ctxt = createDeserializationContext(null, this._config);
/*1595*/          deser = ctxt.findRootValueDeserializer(valueType);
/*1596*/          if (deser != null) {
/*1597*/              this._rootDeserializers.put(valueType, deser); 
/*   0*/             }
/*1599*/          return deser;
/*1601*/        } catch (JsonProcessingException jsonProcessingException) {} 
/*   0*/       }
/*1605*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _detectBindAndClose(byte[] src, int offset, int length) throws IOException {
/*1617*/    DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
/*1618*/    if (!match.hasMatch()) {
/*1619*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1621*/    JsonParser jp = match.createParserWithMatch();
/*1622*/    return match.getReader()._bindAndClose(jp, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _detectBindAndClose(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
/*1629*/    if (!match.hasMatch()) {
/*1630*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1632*/    JsonParser p = match.createParserWithMatch();
/*1635*/    if (forceClosing) {
/*1636*/        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE); 
/*   0*/       }
/*1639*/    return match.getReader()._bindAndClose(p, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  protected <T> MappingIterator<T> _detectBindAndReadValues(DataFormatReaders.Match match, boolean forceClosing) throws IOException, JsonProcessingException {
/*1646*/    if (!match.hasMatch()) {
/*1647*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1649*/    JsonParser p = match.createParserWithMatch();
/*1652*/    if (forceClosing) {
/*1653*/        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE); 
/*   0*/       }
/*1656*/    return match.getReader()._bindAndReadValues(p, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonNode _detectBindAndCloseAsTree(InputStream in) throws IOException {
/*1662*/    DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
/*1663*/    if (!match.hasMatch()) {
/*1664*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1666*/    JsonParser p = match.createParserWithMatch();
/*1667*/    p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
/*1668*/    return match.getReader()._bindAndCloseAsTree(p);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUnkownFormat(DataFormatReaders detector, DataFormatReaders.Match match) throws JsonProcessingException {
/*1677*/    throw new JsonParseException("Can not detect format from input, does not look like any of detectable formats " + detector.toString(), JsonLocation.NA);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _verifySchemaType(FormatSchema schema) {
/*1693*/    if (schema != null && 
/*1694*/      !this._parserFactory.canUseSchema(schema)) {
/*1695*/        throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName()); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected DefaultDeserializationContext createDeserializationContext(JsonParser jp, DeserializationConfig cfg) {
/*1709*/    return this._context.createInstance(cfg, jp, this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUndetectableSource(Object src) throws JsonProcessingException {
/*1714*/    throw new JsonParseException("Can not use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based", JsonLocation.NA);
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _inputStream(URL src) throws IOException {
/*1720*/    return src.openStream();
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _inputStream(File f) throws IOException {
/*1724*/    return new FileInputStream(f);
/*   0*/  }
/*   0*/}
