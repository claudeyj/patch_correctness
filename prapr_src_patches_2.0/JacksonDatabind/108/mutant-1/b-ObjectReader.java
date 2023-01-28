/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.FormatFeature;
/*   0*/import com.fasterxml.jackson.core.FormatSchema;
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonParseException;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonPointer;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.TreeNode;
/*   0*/import com.fasterxml.jackson.core.Version;
/*   0*/import com.fasterxml.jackson.core.Versioned;
/*   0*/import com.fasterxml.jackson.core.filter.FilteringParserDelegate;
/*   0*/import com.fasterxml.jackson.core.filter.JsonPointerBasedFilter;
/*   0*/import com.fasterxml.jackson.core.filter.TokenFilter;
/*   0*/import com.fasterxml.jackson.core.type.ResolvedType;
/*   0*/import com.fasterxml.jackson.core.type.TypeReference;
/*   0*/import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*   0*/import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*   0*/import com.fasterxml.jackson.databind.deser.DataFormatReaders;
/*   0*/import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*   0*/import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*   0*/import com.fasterxml.jackson.databind.node.TreeTraversingParser;
/*   0*/import com.fasterxml.jackson.databind.type.SimpleType;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import java.io.DataInput;
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
/*   0*/  private static final long serialVersionUID = 2L;
/*   0*/  
/*  49*/  private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
/*   0*/  
/*   0*/  protected final DeserializationConfig _config;
/*   0*/  
/*   0*/  protected final DefaultDeserializationContext _context;
/*   0*/  
/*   0*/  protected final JsonFactory _parserFactory;
/*   0*/  
/*   0*/  protected final boolean _unwrapRoot;
/*   0*/  
/*   0*/  private final TokenFilter _filter;
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
/*   0*/  protected ObjectReader(ObjectMapper mapper, DeserializationConfig config) {
/* 168*/    this(mapper, config, null, null, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
/* 179*/    this._config = config;
/* 180*/    this._context = mapper._deserializationContext;
/* 181*/    this._rootDeserializers = mapper._rootDeserializers;
/* 182*/    this._parserFactory = mapper._jsonFactory;
/* 183*/    this._valueType = valueType;
/* 184*/    this._valueToUpdate = valueToUpdate;
/* 185*/    this._schema = schema;
/* 186*/    this._injectableValues = injectableValues;
/* 187*/    this._unwrapRoot = config.useRootWrapping();
/* 189*/    this._rootDeserializer = _prefetchRootDeserializer(valueType);
/* 190*/    this._dataFormatReaders = null;
/* 191*/    this._filter = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
/* 202*/    this._config = config;
/* 203*/    this._context = base._context;
/* 205*/    this._rootDeserializers = base._rootDeserializers;
/* 206*/    this._parserFactory = base._parserFactory;
/* 208*/    this._valueType = valueType;
/* 209*/    this._rootDeserializer = rootDeser;
/* 210*/    this._valueToUpdate = valueToUpdate;
/* 211*/    this._schema = schema;
/* 212*/    this._injectableValues = injectableValues;
/* 213*/    this._unwrapRoot = config.useRootWrapping();
/* 214*/    this._dataFormatReaders = dataFormatReaders;
/* 215*/    this._filter = base._filter;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, DeserializationConfig config) {
/* 223*/    this._config = config;
/* 224*/    this._context = base._context;
/* 226*/    this._rootDeserializers = base._rootDeserializers;
/* 227*/    this._parserFactory = base._parserFactory;
/* 229*/    this._valueType = base._valueType;
/* 230*/    this._rootDeserializer = base._rootDeserializer;
/* 231*/    this._valueToUpdate = base._valueToUpdate;
/* 232*/    this._schema = base._schema;
/* 233*/    this._injectableValues = base._injectableValues;
/* 234*/    this._unwrapRoot = config.useRootWrapping();
/* 235*/    this._dataFormatReaders = base._dataFormatReaders;
/* 236*/    this._filter = base._filter;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, JsonFactory f) {
/* 242*/    this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
/* 244*/    this._context = base._context;
/* 246*/    this._rootDeserializers = base._rootDeserializers;
/* 247*/    this._parserFactory = f;
/* 249*/    this._valueType = base._valueType;
/* 250*/    this._rootDeserializer = base._rootDeserializer;
/* 251*/    this._valueToUpdate = base._valueToUpdate;
/* 252*/    this._schema = base._schema;
/* 253*/    this._injectableValues = base._injectableValues;
/* 254*/    this._unwrapRoot = base._unwrapRoot;
/* 255*/    this._dataFormatReaders = base._dataFormatReaders;
/* 256*/    this._filter = base._filter;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader(ObjectReader base, TokenFilter filter) {
/* 260*/    this._config = base._config;
/* 261*/    this._context = base._context;
/* 262*/    this._rootDeserializers = base._rootDeserializers;
/* 263*/    this._parserFactory = base._parserFactory;
/* 264*/    this._valueType = base._valueType;
/* 265*/    this._rootDeserializer = base._rootDeserializer;
/* 266*/    this._valueToUpdate = base._valueToUpdate;
/* 267*/    this._schema = base._schema;
/* 268*/    this._injectableValues = base._injectableValues;
/* 269*/    this._unwrapRoot = base._unwrapRoot;
/* 270*/    this._dataFormatReaders = base._dataFormatReaders;
/* 271*/    this._filter = filter;
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 280*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _new(ObjectReader base, JsonFactory f) {
/* 297*/    return new ObjectReader(base, f);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _new(ObjectReader base, DeserializationConfig config) {
/* 306*/    return new ObjectReader(base, config);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
/* 318*/    return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  protected <T> MappingIterator<T> _newIterator(JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged) {
/* 331*/    return new MappingIterator<>(this._valueType, p, ctxt, deser, parserManaged, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonToken _initForReading(DeserializationContext ctxt, JsonParser p) throws IOException {
/* 344*/    if (this._schema != null) {
/* 345*/        p.setSchema(this._schema); 
/*   0*/       }
/* 347*/    this._config.initialize(p);
/* 353*/    JsonToken t = p.getCurrentToken();
/* 354*/    if (t == null) {
/* 355*/      t = p.nextToken();
/* 356*/      if (t == null) {
/* 358*/          ctxt.reportInputMismatch(this._valueType, "No content to map due to end-of-input", new Object[0]); 
/*   0*/         }
/*   0*/    } 
/* 362*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _initForMultiRead(DeserializationContext ctxt, JsonParser p) throws IOException {
/* 377*/    if (this._schema != null) {
/* 378*/        p.setSchema(this._schema); 
/*   0*/       }
/* 380*/    this._config.initialize(p);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(DeserializationFeature feature) {
/* 394*/    return _with(this._config.with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(DeserializationFeature first, DeserializationFeature... other) {
/* 404*/    return _with(this._config.with(first, other));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFeatures(DeserializationFeature... features) {
/* 412*/    return _with(this._config.withFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(DeserializationFeature feature) {
/* 420*/    return _with(this._config.without(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(DeserializationFeature first, DeserializationFeature... other) {
/* 429*/    return _with(this._config.without(first, other));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutFeatures(DeserializationFeature... features) {
/* 437*/    return _with(this._config.withoutFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(JsonParser.Feature feature) {
/* 451*/    return _with(this._config.with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFeatures(JsonParser.Feature... features) {
/* 459*/    return _with(this._config.withFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(JsonParser.Feature feature) {
/* 467*/    return _with(this._config.without(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutFeatures(JsonParser.Feature... features) {
/* 475*/    return _with(this._config.withoutFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(FormatFeature feature) {
/* 491*/    return _with(this._config.with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFeatures(FormatFeature... features) {
/* 501*/    return _with(this._config.withFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader without(FormatFeature feature) {
/* 511*/    return _with(this._config.without(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutFeatures(FormatFeature... features) {
/* 521*/    return _with(this._config.withoutFeatures(features));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader at(String value) {
/* 536*/    return new ObjectReader(this, (TokenFilter)new JsonPointerBasedFilter(value));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader at(JsonPointer pointer) {
/* 545*/    return new ObjectReader(this, (TokenFilter)new JsonPointerBasedFilter(pointer));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(DeserializationConfig config) {
/* 556*/    return _with(config);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(InjectableValues injectableValues) {
/* 568*/    if (this._injectableValues == injectableValues) {
/* 569*/        return this; 
/*   0*/       }
/* 571*/    return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(JsonNodeFactory f) {
/* 585*/    return _with(this._config.with(f));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(JsonFactory f) {
/* 600*/    if (f == this._parserFactory) {
/* 601*/        return this; 
/*   0*/       }
/* 603*/    ObjectReader r = _new(this, f);
/* 605*/    if (f.getCodec() == null) {
/* 606*/        f.setCodec(r); 
/*   0*/       }
/* 608*/    return r;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withRootName(String rootName) {
/* 621*/    return _with(this._config.withRootName(rootName));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withRootName(PropertyName rootName) {
/* 628*/    return _with(this._config.withRootName(rootName));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutRootName() {
/* 642*/    return _with(this._config.withRootName(PropertyName.NO_NAME));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(FormatSchema schema) {
/* 655*/    if (this._schema == schema) {
/* 656*/        return this; 
/*   0*/       }
/* 658*/    _verifySchemaType(schema);
/* 659*/    return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader forType(JavaType valueType) {
/* 674*/    if (valueType != null && valueType.equals(this._valueType)) {
/* 675*/        return this; 
/*   0*/       }
/* 677*/    JsonDeserializer<Object> rootDeser = _prefetchRootDeserializer(valueType);
/* 679*/    DataFormatReaders det = this._dataFormatReaders;
/* 680*/    if (det != null) {
/* 681*/        det = det.withType(valueType); 
/*   0*/       }
/* 683*/    return _new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader forType(Class<?> valueType) {
/* 697*/    return forType(this._config.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader forType(TypeReference<?> valueTypeRef) {
/* 710*/    return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(JavaType valueType) {
/* 718*/    return forType(valueType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(Class<?> valueType) {
/* 726*/    return forType(this._config.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(Type valueType) {
/* 734*/    return forType(this._config.getTypeFactory().constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public ObjectReader withType(TypeReference<?> valueTypeRef) {
/* 742*/    return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withValueToUpdate(Object value) {
/*   0*/    JavaType t;
/* 755*/    if (value == this._valueToUpdate) {
/* 755*/        return this; 
/*   0*/       }
/* 756*/    if (value == null) {
/* 759*/        return _new(this, this._config, this._valueType, this._rootDeserializer, null, this._schema, this._injectableValues, this._dataFormatReaders); 
/*   0*/       }
/* 768*/    if (this._valueType == null) {
/* 769*/      t = this._config.constructType(value.getClass());
/*   0*/    } else {
/* 771*/      t = this._valueType;
/*   0*/    } 
/* 773*/    return _new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withView(Class<?> activeView) {
/* 785*/    return _with(this._config.withView(activeView));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(Locale l) {
/* 789*/    return _with(this._config.with(l));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(TimeZone tz) {
/* 793*/    return _with(this._config.with(tz));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withHandler(DeserializationProblemHandler h) {
/* 797*/    return _with(this._config.withHandler(h));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(Base64Variant defaultBase64) {
/* 801*/    return _with(this._config.with(defaultBase64));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFormatDetection(ObjectReader... readers) {
/* 827*/    return withFormatDetection(new DataFormatReaders(readers));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withFormatDetection(DataFormatReaders readers) {
/* 846*/    return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader with(ContextAttributes attrs) {
/* 854*/    return _with(this._config.with(attrs));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withAttributes(Map<?, ?> attrs) {
/* 861*/    return _with(this._config.withAttributes(attrs));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withAttribute(Object key, Object value) {
/* 868*/    return _with(this._config.withAttribute(key, value));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader withoutAttribute(Object key) {
/* 875*/    return _with(this._config.withoutAttribute(key));
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectReader _with(DeserializationConfig newConfig) {
/* 885*/    if (newConfig == this._config) {
/* 886*/        return this; 
/*   0*/       }
/* 888*/    ObjectReader r = _new(this, newConfig);
/* 889*/    if (this._dataFormatReaders != null) {
/* 890*/        r = r.withFormatDetection(this._dataFormatReaders.with(newConfig)); 
/*   0*/       }
/* 892*/    return r;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(DeserializationFeature f) {
/* 902*/    return this._config.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(MapperFeature f) {
/* 906*/    return this._config.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(JsonParser.Feature f) {
/* 910*/    return this._parserFactory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig getConfig() {
/* 917*/    return this._config;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory getFactory() {
/* 925*/    return this._parserFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory getTypeFactory() {
/* 929*/    return this._config.getTypeFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public ContextAttributes getAttributes() {
/* 936*/    return this._config.getAttributes();
/*   0*/  }
/*   0*/  
/*   0*/  public InjectableValues getInjectableValues() {
/* 943*/    return this._injectableValues;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p) throws IOException {
/* 965*/    return (T)_bind(p, this._valueToUpdate);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException {
/* 982*/    return forType(valueType).readValue(p);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p, TypeReference<T> valueTypeRef) throws IOException {
/* 999*/    return forType(valueTypeRef).readValue(p);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException {
/*1015*/    return forType((JavaType)valueType).readValue(p);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p, JavaType valueType) throws IOException {
/*1026*/    return forType(valueType).readValue(p);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException {
/*1050*/    return forType(valueType).readValues(p);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser p, TypeReference<T> valueTypeRef) throws IOException {
/*1074*/    return forType(valueTypeRef).readValues(p);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException {
/*1098*/    return readValues(p, (JavaType)valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> Iterator<T> readValues(JsonParser p, JavaType valueType) throws IOException {
/*1121*/    return forType(valueType).readValues(p);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode createArrayNode() {
/*1132*/    return this._config.getNodeFactory().arrayNode();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode createObjectNode() {
/*1137*/    return this._config.getNodeFactory().objectNode();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser treeAsTokens(TreeNode n) {
/*1144*/    ObjectReader codec = withValueToUpdate(null);
/*1145*/    return (JsonParser)new TreeTraversingParser((JsonNode)n, codec);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
/*1169*/    if (_bindAsTree(p) == null) {
/*1169*/        throw new RuntimeException(); 
/*   0*/       }
/*1169*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTree(JsonGenerator g, TreeNode rootNode) {
/*1174*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(InputStream src) throws IOException {
/*1192*/    if (this._dataFormatReaders != null) {
/*1193*/        return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(src), false); 
/*   0*/       }
/*1195*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(Reader src) throws IOException {
/*1207*/    if (this._dataFormatReaders != null) {
/*1208*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1210*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(String src) throws IOException {
/*1222*/    if (this._dataFormatReaders != null) {
/*1223*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1226*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src) throws IOException {
/*1238*/    if (this._dataFormatReaders != null) {
/*1239*/        return (T)_detectBindAndClose(src, 0, src.length); 
/*   0*/       }
/*1241*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, int offset, int length) throws IOException {
/*1254*/    if (this._dataFormatReaders != null) {
/*1255*/        return (T)_detectBindAndClose(src, offset, length); 
/*   0*/       }
/*1257*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src, offset, length), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(File src) throws IOException {
/*1265*/    if (this._dataFormatReaders != null) {
/*1266*/        return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true); 
/*   0*/       }
/*1269*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(URL src) throws IOException {
/*1282*/    if (this._dataFormatReaders != null) {
/*1283*/        return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true); 
/*   0*/       }
/*1285*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonNode src) throws IOException {
/*1299*/    if (this._dataFormatReaders != null) {
/*1300*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1302*/    return (T)_bindAndClose(_considerFilter(treeAsTokens(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(DataInput src) throws IOException {
/*1311*/    if (this._dataFormatReaders != null) {
/*1312*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1314*/    return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(InputStream in) throws IOException {
/*1342*/    if (this._dataFormatReaders != null) {
/*1343*/        return _detectBindAndCloseAsTree(in); 
/*   0*/       }
/*1345*/    return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(in), false));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(Reader r) throws IOException {
/*1354*/    if (this._dataFormatReaders != null) {
/*1355*/        _reportUndetectableSource(r); 
/*   0*/       }
/*1357*/    return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(r), false));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(String json) throws IOException {
/*1366*/    if (this._dataFormatReaders != null) {
/*1367*/        _reportUndetectableSource(json); 
/*   0*/       }
/*1369*/    return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(json), false));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(byte[] json) throws IOException {
/*1378*/    if (this._dataFormatReaders != null) {
/*1379*/        _reportUndetectableSource(json); 
/*   0*/       }
/*1381*/    return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(json), false));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(byte[] json, int offset, int len) throws IOException {
/*1390*/    if (this._dataFormatReaders != null) {
/*1391*/        _reportUndetectableSource(json); 
/*   0*/       }
/*1393*/    return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(json, offset, len), false));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(DataInput src) throws IOException {
/*1402*/    if (this._dataFormatReaders != null) {
/*1403*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1405*/    return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(src), false));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(JsonParser p) throws IOException {
/*1428*/    DeserializationContext ctxt = createDeserializationContext(p);
/*1430*/    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), false);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(InputStream src) throws IOException {
/*1456*/    if (this._dataFormatReaders != null) {
/*1457*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false); 
/*   0*/       }
/*1460*/    return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(Reader src) throws IOException {
/*1470*/    if (this._dataFormatReaders != null) {
/*1471*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1473*/    JsonParser p = _considerFilter(this._parserFactory.createParser(src), true);
/*1474*/    DeserializationContext ctxt = createDeserializationContext(p);
/*1475*/    _initForMultiRead(ctxt, p);
/*1476*/    p.nextToken();
/*1477*/    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(String json) throws IOException {
/*1489*/    if (this._dataFormatReaders != null) {
/*1490*/        _reportUndetectableSource(json); 
/*   0*/       }
/*1492*/    JsonParser p = _considerFilter(this._parserFactory.createParser(json), true);
/*1493*/    DeserializationContext ctxt = createDeserializationContext(p);
/*1494*/    _initForMultiRead(ctxt, p);
/*1495*/    p.nextToken();
/*1496*/    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(byte[] src, int offset, int length) throws IOException {
/*1505*/    if (this._dataFormatReaders != null) {
/*1506*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false); 
/*   0*/       }
/*1508*/    return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src, offset, length), true));
/*   0*/  }
/*   0*/  
/*   0*/  public final <T> MappingIterator<T> readValues(byte[] src) throws IOException {
/*1517*/    return readValues(src, 0, src.length);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(File src) throws IOException {
/*1526*/    if (this._dataFormatReaders != null) {
/*1527*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), false); 
/*   0*/       }
/*1530*/    return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(URL src) throws IOException {
/*1541*/    if (this._dataFormatReaders != null) {
/*1542*/        return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), true); 
/*   0*/       }
/*1545*/    return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(DataInput src) throws IOException {
/*1553*/    if (this._dataFormatReaders != null) {
/*1554*/        _reportUndetectableSource(src); 
/*   0*/       }
/*1556*/    return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
/*   0*/    try {
/*1569*/      return readValue(treeAsTokens(n), valueType);
/*1570*/    } catch (JsonProcessingException e) {
/*1571*/      throw e;
/*1572*/    } catch (IOException e) {
/*1573*/      throw JsonMappingException.fromUnexpectedIOE(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeValue(JsonGenerator gen, Object value) throws IOException {
/*1579*/    throw new UnsupportedOperationException("Not implemented for ObjectReader");
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _bind(JsonParser p, Object valueToUpdate) throws IOException {
/*   0*/    Object result;
/*1597*/    DeserializationContext ctxt = createDeserializationContext(p);
/*1598*/    JsonToken t = _initForReading(ctxt, p);
/*1599*/    if (t == JsonToken.VALUE_NULL) {
/*1600*/      if (valueToUpdate == null) {
/*1601*/        result = _findRootDeserializer(ctxt).getNullValue(ctxt);
/*   0*/      } else {
/*1603*/        result = valueToUpdate;
/*   0*/      } 
/*1605*/    } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*1606*/      result = valueToUpdate;
/*   0*/    } else {
/*1608*/      JsonDeserializer<Object> deser = _findRootDeserializer(ctxt);
/*1609*/      if (this._unwrapRoot) {
/*1610*/        result = _unwrapAndDeserialize(p, ctxt, this._valueType, deser);
/*1612*/      } else if (valueToUpdate == null) {
/*1613*/        result = deser.deserialize(p, ctxt);
/*   0*/      } else {
/*1617*/        result = deser.deserialize(p, ctxt, valueToUpdate);
/*   0*/      } 
/*   0*/    } 
/*1622*/    p.clearCurrentToken();
/*1623*/    if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/*1624*/        _verifyNoTrailingTokens(p, ctxt, this._valueType); 
/*   0*/       }
/*1626*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _bindAndClose(JsonParser p0) throws IOException {
/*1631*/    try (JsonParser p = p0) {
/*   0*/      Object result;
/*1634*/      DeserializationContext ctxt = createDeserializationContext(p);
/*1635*/      JsonToken t = _initForReading(ctxt, p);
/*1636*/      if (t == JsonToken.VALUE_NULL) {
/*1637*/        if (this._valueToUpdate == null) {
/*1638*/          result = _findRootDeserializer(ctxt).getNullValue(ctxt);
/*   0*/        } else {
/*1640*/          result = this._valueToUpdate;
/*   0*/        } 
/*1642*/      } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*1643*/        result = this._valueToUpdate;
/*   0*/      } else {
/*1645*/        JsonDeserializer<Object> deser = _findRootDeserializer(ctxt);
/*1646*/        if (this._unwrapRoot) {
/*1647*/          result = _unwrapAndDeserialize(p, ctxt, this._valueType, deser);
/*1649*/        } else if (this._valueToUpdate == null) {
/*1650*/          result = deser.deserialize(p, ctxt);
/*   0*/        } else {
/*1652*/          deser.deserialize(p, ctxt, this._valueToUpdate);
/*1653*/          result = this._valueToUpdate;
/*   0*/        } 
/*   0*/      } 
/*1657*/      if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/*1658*/          _verifyNoTrailingTokens(p, ctxt, this._valueType); 
/*   0*/         }
/*1660*/      return result;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonNode _bindAndCloseAsTree(JsonParser p0) throws IOException {
/*1665*/    try (JsonParser p = p0) {
/*1666*/      return _bindAsTree(p);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonNode _bindAsTree(JsonParser p) throws IOException {
/*   0*/    JsonNode resultNode;
/*1673*/    this._config.initialize(p);
/*1674*/    if (this._schema != null) {
/*1675*/        p.setSchema(this._schema); 
/*   0*/       }
/*1678*/    JsonToken t = p.getCurrentToken();
/*1679*/    if (t == null) {
/*1680*/      t = p.nextToken();
/*1681*/      if (t == null) {
/*1682*/          return this._config.getNodeFactory().missingNode(); 
/*   0*/         }
/*   0*/    } 
/*1686*/    if (t == JsonToken.VALUE_NULL) {
/*1687*/      resultNode = this._config.getNodeFactory().nullNode();
/*   0*/    } else {
/*1689*/      DeserializationContext ctxt = createDeserializationContext(p);
/*1690*/      JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
/*1691*/      if (this._unwrapRoot) {
/*1692*/        resultNode = (JsonNode)_unwrapAndDeserialize(p, ctxt, JSON_NODE_TYPE, deser);
/*   0*/      } else {
/*1694*/        resultNode = (JsonNode)deser.deserialize(p, ctxt);
/*1695*/        if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/*1696*/            _verifyNoTrailingTokens(p, ctxt, JSON_NODE_TYPE); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1700*/    return resultNode;
/*   0*/  }
/*   0*/  
/*   0*/  protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p) throws IOException {
/*1713*/    DeserializationContext ctxt = createDeserializationContext(p);
/*1714*/    _initForMultiRead(ctxt, p);
/*1715*/    p.nextToken();
/*1716*/    return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, JavaType rootType, JsonDeserializer<Object> deser) throws IOException {
/*   0*/    Object result;
/*1722*/    PropertyName expRootName = this._config.findRootName(rootType);
/*1724*/    String expSimpleName = expRootName.getSimpleName();
/*1726*/    if (p.getCurrentToken() != JsonToken.START_OBJECT) {
/*1727*/        ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() }); 
/*   0*/       }
/*1731*/    if (p.nextToken() != JsonToken.FIELD_NAME) {
/*1732*/        ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() }); 
/*   0*/       }
/*1736*/    String actualName = p.getCurrentName();
/*1737*/    if (!expSimpleName.equals(actualName)) {
/*1738*/        ctxt.reportInputMismatch(rootType, "Root name '%s' does not match expected ('%s') for type %s", new Object[] { actualName, expSimpleName, rootType }); 
/*   0*/       }
/*1743*/    p.nextToken();
/*1745*/    if (this._valueToUpdate == null) {
/*1746*/      result = deser.deserialize(p, ctxt);
/*   0*/    } else {
/*1748*/      deser.deserialize(p, ctxt, this._valueToUpdate);
/*1749*/      result = this._valueToUpdate;
/*   0*/    } 
/*1752*/    if (p.nextToken() != JsonToken.END_OBJECT) {
/*1753*/        ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() }); 
/*   0*/       }
/*1757*/    if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
/*1758*/        _verifyNoTrailingTokens(p, ctxt, this._valueType); 
/*   0*/       }
/*1760*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonParser _considerFilter(JsonParser p, boolean multiValue) {
/*1769*/    return (this._filter == null || FilteringParserDelegate.class.isInstance(p)) ? p : (JsonParser)new FilteringParserDelegate(p, this._filter, false, multiValue);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
/*1780*/    JsonToken t = p.nextToken();
/*1781*/    if (t != null) {
/*1782*/      Class<?> bt = ClassUtil.rawClass(bindType);
/*1783*/      if (bt == null && 
/*1784*/        this._valueToUpdate != null) {
/*1785*/          bt = this._valueToUpdate.getClass(); 
/*   0*/         }
/*1788*/      ctxt.reportTrailingTokens(bt, p, t);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _detectBindAndClose(byte[] src, int offset, int length) throws IOException {
/*1801*/    DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
/*1802*/    if (!match.hasMatch()) {
/*1803*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1805*/    JsonParser p = match.createParserWithMatch();
/*1806*/    return match.getReader()._bindAndClose(p);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _detectBindAndClose(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
/*1813*/    if (!match.hasMatch()) {
/*1814*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1816*/    JsonParser p = match.createParserWithMatch();
/*1819*/    if (forceClosing) {
/*1820*/        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE); 
/*   0*/       }
/*1823*/    return match.getReader()._bindAndClose(p);
/*   0*/  }
/*   0*/  
/*   0*/  protected <T> MappingIterator<T> _detectBindAndReadValues(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
/*1830*/    if (!match.hasMatch()) {
/*1831*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1833*/    JsonParser p = match.createParserWithMatch();
/*1836*/    if (forceClosing) {
/*1837*/        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE); 
/*   0*/       }
/*1840*/    return match.getReader()._bindAndReadValues(p);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonNode _detectBindAndCloseAsTree(InputStream in) throws IOException {
/*1846*/    DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
/*1847*/    if (!match.hasMatch()) {
/*1848*/        _reportUnkownFormat(this._dataFormatReaders, match); 
/*   0*/       }
/*1850*/    JsonParser p = match.createParserWithMatch();
/*1851*/    p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
/*1852*/    return match.getReader()._bindAndCloseAsTree(p);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUnkownFormat(DataFormatReaders detector, DataFormatReaders.Match match) throws JsonProcessingException {
/*1863*/    throw new JsonParseException(null, "Cannot detect format from input, does not look like any of detectable formats " + detector.toString());
/*   0*/  }
/*   0*/  
/*   0*/  protected void _verifySchemaType(FormatSchema schema) {
/*1878*/    if (schema != null && 
/*1879*/      !this._parserFactory.canUseSchema(schema)) {
/*1880*/        throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName()); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected DefaultDeserializationContext createDeserializationContext(JsonParser p) {
/*1892*/    return this._context.createInstance(this._config, p, this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _inputStream(URL src) throws IOException {
/*1896*/    return src.openStream();
/*   0*/  }
/*   0*/  
/*   0*/  protected InputStream _inputStream(File f) throws IOException {
/*1900*/    return new FileInputStream(f);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUndetectableSource(Object src) throws JsonProcessingException {
/*1906*/    throw new JsonParseException(null, "Cannot use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based");
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt) throws JsonMappingException {
/*1922*/    if (this._rootDeserializer != null) {
/*1923*/        return this._rootDeserializer; 
/*   0*/       }
/*1927*/    JavaType t = this._valueType;
/*1928*/    if (t == null) {
/*1929*/        ctxt.reportBadDefinition((JavaType)null, "No value type configured for ObjectReader"); 
/*   0*/       }
/*1933*/    JsonDeserializer<Object> deser = this._rootDeserializers.get(t);
/*1934*/    if (deser != null) {
/*1935*/        return deser; 
/*   0*/       }
/*1938*/    deser = ctxt.findRootValueDeserializer(t);
/*1939*/    if (deser == null) {
/*1940*/        ctxt.reportBadDefinition(t, "Cannot find a deserializer for type " + t); 
/*   0*/       }
/*1942*/    this._rootDeserializers.put(t, deser);
/*1943*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt) throws JsonMappingException {
/*1952*/    JsonDeserializer<Object> deser = this._rootDeserializers.get(JSON_NODE_TYPE);
/*1953*/    if (deser == null) {
/*1955*/      deser = ctxt.findRootValueDeserializer(JSON_NODE_TYPE);
/*1956*/      if (deser == null) {
/*1957*/          ctxt.reportBadDefinition(JSON_NODE_TYPE, "Cannot find a deserializer for type " + JSON_NODE_TYPE); 
/*   0*/         }
/*1960*/      this._rootDeserializers.put(JSON_NODE_TYPE, deser);
/*   0*/    } 
/*1962*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _prefetchRootDeserializer(JavaType valueType) {
/*1972*/    if (valueType == null || !this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
/*1973*/        return null; 
/*   0*/       }
/*1976*/    JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
/*1977*/    if (deser == null) {
/*   0*/        try {
/*1980*/          DeserializationContext ctxt = createDeserializationContext(null);
/*1981*/          deser = ctxt.findRootValueDeserializer(valueType);
/*1982*/          if (deser != null) {
/*1983*/              this._rootDeserializers.put(valueType, deser); 
/*   0*/             }
/*1985*/          return deser;
/*1986*/        } catch (JsonProcessingException jsonProcessingException) {} 
/*   0*/       }
/*1990*/    return deser;
/*   0*/  }
/*   0*/}
