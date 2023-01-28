/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonFormat;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*   0*/import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializerCache;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializerFactory;
/*   0*/import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
/*   0*/import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
/*   0*/import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
/*   0*/import com.fasterxml.jackson.databind.exc.InvalidFormatException;
/*   0*/import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
/*   0*/import com.fasterxml.jackson.databind.exc.MismatchedInputException;
/*   0*/import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*   0*/import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.LinkedNode;
/*   0*/import com.fasterxml.jackson.databind.util.ObjectBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.text.DateFormat;
/*   0*/import java.text.ParseException;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public abstract class DeserializationContext extends DatabindContext implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final DeserializerCache _cache;
/*   0*/  
/*   0*/  protected final DeserializerFactory _factory;
/*   0*/  
/*   0*/  protected final DeserializationConfig _config;
/*   0*/  
/*   0*/  protected final int _featureFlags;
/*   0*/  
/*   0*/  protected final Class<?> _view;
/*   0*/  
/*   0*/  protected transient JsonParser _parser;
/*   0*/  
/*   0*/  protected final InjectableValues _injectableValues;
/*   0*/  
/*   0*/  protected transient ArrayBuilders _arrayBuilders;
/*   0*/  
/*   0*/  protected transient ObjectBuffer _objectBuffer;
/*   0*/  
/*   0*/  protected transient DateFormat _dateFormat;
/*   0*/  
/*   0*/  protected transient ContextAttributes _attributes;
/*   0*/  
/*   0*/  protected LinkedNode<JavaType> _currentType;
/*   0*/  
/*   0*/  protected DeserializationContext(DeserializerFactory df) {
/* 150*/    this(df, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationContext(DeserializerFactory df, DeserializerCache cache) {
/* 156*/    if (df == null) {
/* 157*/        throw new IllegalArgumentException("Cannot pass null DeserializerFactory"); 
/*   0*/       }
/* 159*/    this._factory = df;
/* 160*/    if (cache == null) {
/* 161*/        cache = new DeserializerCache(); 
/*   0*/       }
/* 163*/    this._cache = cache;
/* 164*/    this._featureFlags = 0;
/* 165*/    this._config = null;
/* 166*/    this._injectableValues = null;
/* 167*/    this._view = null;
/* 168*/    this._attributes = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationContext(DeserializationContext src, DeserializerFactory factory) {
/* 174*/    this._cache = src._cache;
/* 175*/    this._factory = factory;
/* 177*/    this._config = src._config;
/* 178*/    this._featureFlags = src._featureFlags;
/* 179*/    this._view = src._view;
/* 180*/    this._parser = src._parser;
/* 181*/    this._injectableValues = src._injectableValues;
/* 182*/    this._attributes = src._attributes;
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationContext(DeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues injectableValues) {
/* 192*/    this._cache = src._cache;
/* 193*/    this._factory = src._factory;
/* 195*/    this._config = config;
/* 196*/    this._featureFlags = config.getDeserializationFeatures();
/* 197*/    this._view = config.getActiveView();
/* 198*/    this._parser = p;
/* 199*/    this._injectableValues = injectableValues;
/* 200*/    this._attributes = config.getAttributes();
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationContext(DeserializationContext src) {
/* 207*/    this._cache = new DeserializerCache();
/* 208*/    this._factory = src._factory;
/* 210*/    this._config = src._config;
/* 211*/    this._featureFlags = src._featureFlags;
/* 212*/    this._view = src._view;
/* 213*/    this._injectableValues = null;
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig getConfig() {
/* 223*/    return this._config;
/*   0*/  }
/*   0*/  
/*   0*/  public final Class<?> getActiveView() {
/* 226*/    return this._view;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean canOverrideAccessModifiers() {
/* 230*/    return this._config.canOverrideAccessModifiers();
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(MapperFeature feature) {
/* 235*/    return this._config.isEnabled(feature);
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType) {
/* 240*/    return this._config.getDefaultPropertyFormat(baseType);
/*   0*/  }
/*   0*/  
/*   0*/  public final AnnotationIntrospector getAnnotationIntrospector() {
/* 245*/    return this._config.getAnnotationIntrospector();
/*   0*/  }
/*   0*/  
/*   0*/  public final TypeFactory getTypeFactory() {
/* 250*/    return this._config.getTypeFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public Locale getLocale() {
/* 261*/    return this._config.getLocale();
/*   0*/  }
/*   0*/  
/*   0*/  public TimeZone getTimeZone() {
/* 272*/    return this._config.getTimeZone();
/*   0*/  }
/*   0*/  
/*   0*/  public Object getAttribute(Object key) {
/* 283*/    return this._attributes.getAttribute(key);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationContext setAttribute(Object key, Object value) {
/* 289*/    this._attributes = this._attributes.withPerCallAttribute(key, value);
/* 290*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getContextualType() {
/* 307*/    return (this._currentType == null) ? null : this._currentType.value();
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializerFactory getFactory() {
/* 320*/    return this._factory;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(DeserializationFeature feat) {
/* 331*/    return ((this._featureFlags & feat.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final int getDeserializationFeatures() {
/* 341*/    return this._featureFlags;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasDeserializationFeatures(int featureMask) {
/* 351*/    return ((this._featureFlags & featureMask) == featureMask);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasSomeOfFeatures(int featureMask) {
/* 361*/    return ((this._featureFlags & featureMask) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonParser getParser() {
/* 372*/    return this._parser;
/*   0*/  }
/*   0*/  
/*   0*/  public final Object findInjectableValue(Object valueId, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
/* 378*/    if (this._injectableValues == null) {
/* 379*/        reportBadDefinition(ClassUtil.classOf(valueId), String.format("No 'injectableValues' configured, cannot inject value with id [%s]", new Object[] { valueId })); 
/*   0*/       }
/* 382*/    return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
/*   0*/  }
/*   0*/  
/*   0*/  public final Base64Variant getBase64Variant() {
/* 394*/    return this._config.getBase64Variant();
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonNodeFactory getNodeFactory() {
/* 404*/    return this._config.getNodeFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasValueDeserializerFor(JavaType type, AtomicReference<Throwable> cause) {
/*   0*/    try {
/* 422*/      return this._cache.hasValueDeserializerFor(this, this._factory, type);
/* 423*/    } catch (JsonMappingException e) {
/* 424*/      if (cause != null) {
/* 425*/          cause.set(e); 
/*   0*/         }
/* 427*/    } catch (RuntimeException e) {
/* 428*/      if (cause == null) {
/* 429*/          throw e; 
/*   0*/         }
/* 431*/      cause.set(e);
/*   0*/    } 
/* 433*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonDeserializer<Object> findContextualValueDeserializer(JavaType type, BeanProperty prop) throws JsonMappingException {
/* 444*/    JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
/* 445*/    if (deser != null) {
/* 446*/        deser = (JsonDeserializer)handleSecondaryContextualization(deser, prop, type); 
/*   0*/       }
/* 448*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonDeserializer<Object> findNonContextualValueDeserializer(JavaType type) throws JsonMappingException {
/* 467*/    return this._cache.findValueDeserializer(this, this._factory, type);
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonDeserializer<Object> findRootValueDeserializer(JavaType type) throws JsonMappingException {
/* 477*/    JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
/* 479*/    if (deser == null) {
/* 480*/        return null; 
/*   0*/       }
/* 482*/    deser = (JsonDeserializer)handleSecondaryContextualization(deser, null, type);
/* 483*/    TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
/* 484*/    if (typeDeser != null) {
/* 486*/      typeDeser = typeDeser.forProperty(null);
/* 487*/      return new TypeWrappedDeserializer(typeDeser, deser);
/*   0*/    } 
/* 489*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public final KeyDeserializer findKeyDeserializer(JavaType keyType, BeanProperty prop) throws JsonMappingException {
/* 500*/    KeyDeserializer kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
/* 503*/    if (kd instanceof ContextualKeyDeserializer) {
/* 504*/        kd = ((ContextualKeyDeserializer)kd).createContextual(this, prop); 
/*   0*/       }
/* 506*/    return kd;
/*   0*/  }
/*   0*/  
/*   0*/  public final JavaType constructType(Class<?> cls) {
/* 543*/    return (cls == null) ? null : this._config.constructType(cls);
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> findClass(String className) throws ClassNotFoundException {
/* 557*/    return getTypeFactory().findClass(className);
/*   0*/  }
/*   0*/  
/*   0*/  public final ObjectBuffer leaseObjectBuffer() {
/* 574*/    ObjectBuffer buf = this._objectBuffer;
/* 575*/    if (buf == null) {
/* 576*/      buf = new ObjectBuffer();
/*   0*/    } else {
/* 578*/      this._objectBuffer = null;
/*   0*/    } 
/* 580*/    return buf;
/*   0*/  }
/*   0*/  
/*   0*/  public final void returnObjectBuffer(ObjectBuffer buf) {
/* 594*/    if (this._objectBuffer == null || buf.initialCapacity() >= this._objectBuffer.initialCapacity()) {
/* 596*/        this._objectBuffer = buf; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public final ArrayBuilders getArrayBuilders() {
/* 606*/    if (this._arrayBuilders == null) {
/* 607*/        this._arrayBuilders = new ArrayBuilders(); 
/*   0*/       }
/* 609*/    return this._arrayBuilders;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
/* 648*/    if (deser instanceof ContextualDeserializer) {
/* 649*/      this._currentType = new LinkedNode<>(type, this._currentType);
/*   0*/      try {
/* 651*/        deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*   0*/      } finally {
/* 653*/        this._currentType = this._currentType.next();
/*   0*/      } 
/*   0*/    } 
/* 656*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
/* 679*/    if (deser instanceof ContextualDeserializer) {
/* 680*/      this._currentType = new LinkedNode<>(type, this._currentType);
/*   0*/      try {
/* 682*/        deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*   0*/      } finally {
/* 684*/        this._currentType = this._currentType.next();
/*   0*/      } 
/*   0*/    } 
/* 687*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public Date parseDate(String dateStr) throws IllegalArgumentException {
/*   0*/    try {
/* 709*/      DateFormat df = getDateFormat();
/* 710*/      return df.parse(dateStr);
/* 711*/    } catch (ParseException e) {
/* 712*/      throw new IllegalArgumentException(String.format("Failed to parse Date value '%s': %s", new Object[] { dateStr, ClassUtil.exceptionMessage(e) }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Calendar constructCalendar(Date d) {
/* 724*/    Calendar c = Calendar.getInstance(getTimeZone());
/* 725*/    c.setTime(d);
/* 726*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p, Class<T> type) throws IOException {
/* 747*/    return readValue(p, getTypeFactory().constructType(type));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser p, JavaType type) throws IOException {
/* 755*/    JsonDeserializer<Object> deser = findRootValueDeserializer(type);
/* 756*/    if (deser == null) {
/* 757*/        reportBadDefinition(type, "Could not find JsonDeserializer for type " + type); 
/*   0*/       }
/* 760*/    return (T)deser.deserialize(p, this);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readPropertyValue(JsonParser p, BeanProperty prop, Class<T> type) throws IOException {
/* 772*/    return readPropertyValue(p, prop, getTypeFactory().constructType(type));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readPropertyValue(JsonParser p, BeanProperty prop, JavaType type) throws IOException {
/* 780*/    JsonDeserializer<Object> deser = findContextualValueDeserializer(type, prop);
/* 781*/    if (deser == null) {
/* 782*/        return reportBadDefinition(type, String.format("Could not find JsonDeserializer for type %s (via property %s)", new Object[] { type, ClassUtil.nameOf(prop) })); 
/*   0*/       }
/* 786*/    return (T)deser.deserialize(p, this);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean handleUnknownProperty(JsonParser p, JsonDeserializer<?> deser, Object instanceOrClass, String propName) throws IOException {
/* 808*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 809*/    while (h != null) {
/* 811*/      if (((DeserializationProblemHandler)h.value()).handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
/* 812*/          return true; 
/*   0*/         }
/* 814*/      h = h.next();
/*   0*/    } 
/* 817*/    if (!isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 818*/      p.skipChildren();
/* 819*/      return true;
/*   0*/    } 
/* 822*/    Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
/* 823*/    throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, propName, propIds);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleWeirdKey(Class<?> keyClass, String keyValue, String msg, Object... msgArgs) throws IOException {
/* 851*/    msg = _format(msg, msgArgs);
/* 852*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 853*/    while (h != null) {
/* 855*/      Object key = ((DeserializationProblemHandler)h.value()).handleWeirdKey(this, keyClass, keyValue, msg);
/* 856*/      if (key != DeserializationProblemHandler.NOT_HANDLED) {
/* 858*/        if (key == null || keyClass.isInstance(key)) {
/* 859*/            return key; 
/*   0*/           }
/* 861*/        throw weirdStringException(keyValue, keyClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[] { keyClass, key.getClass() }));
/*   0*/      } 
/* 865*/      h = h.next();
/*   0*/    } 
/* 867*/    throw weirdKeyException(keyClass, keyValue, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleWeirdStringValue(Class<?> targetClass, String value, String msg, Object... msgArgs) throws IOException {
/* 895*/    msg = _format(msg, msgArgs);
/* 896*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 897*/    while (h != null) {
/* 899*/      Object instance = ((DeserializationProblemHandler)h.value()).handleWeirdStringValue(this, targetClass, value, msg);
/* 900*/      if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/* 902*/        if (_isCompatible(targetClass, instance)) {
/* 903*/            return instance; 
/*   0*/           }
/* 905*/        throw weirdStringException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[] { targetClass, instance.getClass() }));
/*   0*/      } 
/* 909*/      h = h.next();
/*   0*/    } 
/* 911*/    throw weirdStringException(value, targetClass, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleWeirdNumberValue(Class<?> targetClass, Number value, String msg, Object... msgArgs) throws IOException {
/* 938*/    msg = _format(msg, msgArgs);
/* 939*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 940*/    while (h != null) {
/* 942*/      Object key = ((DeserializationProblemHandler)h.value()).handleWeirdNumberValue(this, targetClass, value, msg);
/* 943*/      if (key != DeserializationProblemHandler.NOT_HANDLED) {
/* 945*/        if (_isCompatible(targetClass, key)) {
/* 946*/            return key; 
/*   0*/           }
/* 948*/        throw weirdNumberException(value, targetClass, _format("DeserializationProblemHandler.handleWeirdNumberValue() for type %s returned value of type %s", new Object[] { targetClass, key.getClass() }));
/*   0*/      } 
/* 952*/      h = h.next();
/*   0*/    } 
/* 954*/    throw weirdNumberException(value, targetClass, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleWeirdNativeValue(JavaType targetType, Object badValue, JsonParser p) throws IOException {
/* 961*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 962*/    Class<?> raw = targetType.getRawClass();
/* 963*/    for (; h != null; h = h.next()) {
/* 965*/      Object goodValue = ((DeserializationProblemHandler)h.value()).handleWeirdNativeValue(this, targetType, badValue, p);
/* 966*/      if (goodValue != DeserializationProblemHandler.NOT_HANDLED) {
/* 968*/        if (goodValue == null || raw.isInstance(goodValue)) {
/* 969*/            return goodValue; 
/*   0*/           }
/* 971*/        throw JsonMappingException.from(p, _format("DeserializationProblemHandler.handleWeirdNativeValue() for type %s returned value of type %s", new Object[] { targetType, goodValue.getClass() }));
/*   0*/      } 
/*   0*/    } 
/* 976*/    throw weirdNativeValueException(badValue, raw);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleMissingInstantiator(Class<?> instClass, ValueInstantiator valueInst, JsonParser p, String msg, Object... msgArgs) throws IOException {
/*1001*/    if (p == null) {
/*1002*/        p = getParser(); 
/*   0*/       }
/*1004*/    msg = _format(msg, msgArgs);
/*1005*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*1006*/    while (h != null) {
/*1008*/      Object instance = ((DeserializationProblemHandler)h.value()).handleMissingInstantiator(this, instClass, valueInst, p, msg);
/*1010*/      if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/*1012*/        if (_isCompatible(instClass, instance)) {
/*1013*/            return instance; 
/*   0*/           }
/*1015*/        reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleMissingInstantiator() for type %s returned value of type %s", new Object[] { instClass, ClassUtil.classNameOf(instance) }));
/*   0*/      } 
/*1019*/      h = h.next();
/*   0*/    } 
/*1025*/    if (valueInst != null && !valueInst.canInstantiate()) {
/*1026*/      msg = String.format("Cannot construct instance of %s (no Creators, like default construct, exist): %s", new Object[] { ClassUtil.nameOf(instClass), msg });
/*1028*/      return reportBadDefinition(constructType(instClass), msg);
/*   0*/    } 
/*1030*/    msg = String.format("Cannot construct instance of %s (although at least one Creator exists): %s", new Object[] { ClassUtil.nameOf(instClass), msg });
/*1032*/    return reportInputMismatch(instClass, msg, new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleInstantiationProblem(Class<?> instClass, Object argument, Throwable t) throws IOException {
/*1056*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*1057*/    while (h != null) {
/*1059*/      Object instance = ((DeserializationProblemHandler)h.value()).handleInstantiationProblem(this, instClass, argument, t);
/*1060*/      if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/*1062*/        if (_isCompatible(instClass, instance)) {
/*1063*/            return instance; 
/*   0*/           }
/*1065*/        reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleInstantiationProblem() for type %s returned value of type %s", new Object[] { instClass, ClassUtil.classNameOf(instance) }));
/*   0*/      } 
/*1069*/      h = h.next();
/*   0*/    } 
/*1072*/    ClassUtil.throwIfIOE(t);
/*1073*/    throw instantiationException(instClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleUnexpectedToken(Class<?> instClass, JsonParser p) throws IOException {
/*1093*/    return handleUnexpectedToken(instClass, p.getCurrentToken(), p, null, new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Object handleUnexpectedToken(Class<?> instClass, JsonToken t, JsonParser p, String msg, Object... msgArgs) throws IOException {
/*1115*/    msg = _format(msg, msgArgs);
/*1116*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*1117*/    while (h != null) {
/*1118*/      Object instance = ((DeserializationProblemHandler)h.value()).handleUnexpectedToken(this, instClass, t, p, msg);
/*1120*/      if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/*1121*/        if (_isCompatible(instClass, instance)) {
/*1122*/            return instance; 
/*   0*/           }
/*1124*/        reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s", new Object[] { ClassUtil.nameOf(instClass), ClassUtil.classNameOf(instance) }));
/*   0*/      } 
/*1128*/      h = h.next();
/*   0*/    } 
/*1130*/    if (msg == null) {
/*1131*/        if (t == null) {
/*1132*/          msg = String.format("Unexpected end-of-input when binding data into %s", new Object[] { ClassUtil.nameOf(instClass) });
/*   0*/        } else {
/*1135*/          msg = String.format("Cannot deserialize instance of %s out of %s token", new Object[] { ClassUtil.nameOf(instClass), t });
/*   0*/        }  
/*   0*/       }
/*1139*/    reportInputMismatch(instClass, msg, new Object[0]);
/*1140*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType handleUnknownTypeId(JavaType baseType, String id, TypeIdResolver idResolver, String extraDesc) throws IOException {
/*1166*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*1167*/    while (h != null) {
/*1169*/      JavaType type = ((DeserializationProblemHandler)h.value()).handleUnknownTypeId(this, baseType, id, idResolver, extraDesc);
/*1170*/      if (type != null) {
/*1171*/        if (baseType.hasRawClass(Void.class)) {
/*1172*/            return null; 
/*   0*/           }
/*1175*/        if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
/*1176*/            return type; 
/*   0*/           }
/*1178*/        throw invalidTypeIdException(baseType, id, "problem handler tried to resolve into non-subtype: " + type);
/*   0*/      } 
/*1181*/      h = h.next();
/*   0*/    } 
/*1184*/    if (!isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
/*1185*/        return null; 
/*   0*/       }
/*1187*/    throw invalidTypeIdException(baseType, id, extraDesc);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType handleMissingTypeId(JavaType baseType, TypeIdResolver idResolver, String extraDesc) throws IOException {
/*1196*/    LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*1197*/    while (h != null) {
/*1199*/      JavaType type = ((DeserializationProblemHandler)h.value()).handleMissingTypeId(this, baseType, idResolver, extraDesc);
/*1200*/      if (type != null) {
/*1201*/        if (type.hasRawClass(Void.class)) {
/*1202*/            return null; 
/*   0*/           }
/*1205*/        if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
/*1206*/            return type; 
/*   0*/           }
/*1208*/        throw invalidTypeIdException(baseType, null, "problem handler tried to resolve into non-subtype: " + type);
/*   0*/      } 
/*1211*/      h = h.next();
/*   0*/    } 
/*1218*/    throw missingTypeIdException(baseType, extraDesc);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _isCompatible(Class<?> target, Object value) {
/*1226*/    if (value == null || target.isInstance(value)) {
/*1227*/        return true; 
/*   0*/       }
/*1230*/    return (target.isPrimitive() && ClassUtil.wrapperType(target).isInstance(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void reportWrongTokenException(JsonDeserializer<?> deser, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/*1255*/    msg = _format(msg, msgArgs);
/*1256*/    throw wrongTokenException(getParser(), deser.handledType(), expToken, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public void reportWrongTokenException(JavaType targetType, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/*1273*/    msg = _format(msg, msgArgs);
/*1274*/    throw wrongTokenException(getParser(), targetType, expToken, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public void reportWrongTokenException(Class<?> targetType, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/*1291*/    msg = _format(msg, msgArgs);
/*1292*/    throw wrongTokenException(getParser(), targetType, expToken, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportUnresolvedObjectId(ObjectIdReader oidReader, Object bean) throws JsonMappingException {
/*1301*/    String msg = String.format("No Object Id found for an instance of %s, to assign to property '%s'", new Object[] { ClassUtil.classNameOf(bean), oidReader.propertyName });
/*1303*/    return reportInputMismatch(oidReader.idProperty, msg, new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportInputMismatch(BeanProperty prop, String msg, Object... msgArgs) throws JsonMappingException {
/*1315*/    msg = _format(msg, msgArgs);
/*1316*/    JavaType type = (prop == null) ? null : prop.getType();
/*1317*/    throw MismatchedInputException.from(getParser(), type, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportInputMismatch(JsonDeserializer<?> src, String msg, Object... msgArgs) throws JsonMappingException {
/*1329*/    msg = _format(msg, msgArgs);
/*1330*/    throw MismatchedInputException.from(getParser(), src.handledType(), msg);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportInputMismatch(Class<?> targetType, String msg, Object... msgArgs) throws JsonMappingException {
/*1342*/    msg = _format(msg, msgArgs);
/*1343*/    throw MismatchedInputException.from(getParser(), targetType, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportInputMismatch(JavaType targetType, String msg, Object... msgArgs) throws JsonMappingException {
/*1355*/    msg = _format(msg, msgArgs);
/*1356*/    throw MismatchedInputException.from(getParser(), targetType, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportTrailingTokens(Class<?> targetType, JsonParser p, JsonToken trailingToken) throws JsonMappingException {
/*1362*/    throw MismatchedInputException.from(p, targetType, String.format("Trailing token (of type %s) found after value (bound as %s): not allowed as per `DeserializationFeature.FAIL_ON_TRAILING_TOKENS`", new Object[] { trailingToken, ClassUtil.nameOf(targetType) }));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void reportWrongTokenException(JsonParser p, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
/*1373*/    msg = _format(msg, msgArgs);
/*1374*/    throw wrongTokenException(p, expToken, msg);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void reportUnknownProperty(Object instanceOrClass, String fieldName, JsonDeserializer<?> deser) throws JsonMappingException {
/*1393*/    if (isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/*1395*/      Collection<Object> propIds = (deser == null) ? null : deser.getKnownPropertyNames();
/*1396*/      throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, propIds);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void reportMissingContent(String msg, Object... msgArgs) throws JsonMappingException {
/*1408*/    throw MismatchedInputException.from(getParser(), (JavaType)null, "No content to map due to end-of-input");
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportBadTypeDefinition(BeanDescription bean, String msg, Object... msgArgs) throws JsonMappingException {
/*1427*/    msg = _format(msg, msgArgs);
/*1428*/    String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
/*1429*/    msg = String.format("Invalid type definition for type %s: %s", new Object[] { beanDesc, msg });
/*1430*/    throw InvalidDefinitionException.from(this._parser, msg, bean, null);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String msg, Object... msgArgs) throws JsonMappingException {
/*1442*/    msg = _format(msg, msgArgs);
/*1443*/    String propName = ClassUtil.nameOf(prop);
/*1444*/    String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
/*1445*/    msg = String.format("Invalid definition for property %s (of type %s): %s", new Object[] { propName, beanDesc, msg });
/*1447*/    throw InvalidDefinitionException.from(this._parser, msg, bean, prop);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportBadDefinition(JavaType type, String msg) throws JsonMappingException {
/*1452*/    throw InvalidDefinitionException.from(this._parser, msg, type);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T reportBadMerge(JsonDeserializer<?> deser) throws JsonMappingException {
/*1468*/    if (isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
/*1469*/        return null; 
/*   0*/       }
/*1471*/    JavaType type = constructType(deser.handledType());
/*1472*/    String msg = String.format("Invalid configuration: values of type %s cannot be merged", new Object[] { type });
/*1473*/    throw InvalidDefinitionException.from(getParser(), msg, type);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException wrongTokenException(JsonParser p, JavaType targetType, JsonToken expToken, String extra) {
/*1496*/    String msg = String.format("Unexpected token (%s), expected %s", new Object[] { p.getCurrentToken(), expToken });
/*1498*/    msg = _colonConcat(msg, extra);
/*1499*/    return MismatchedInputException.from(p, targetType, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException wrongTokenException(JsonParser p, Class<?> targetType, JsonToken expToken, String extra) {
/*1505*/    String msg = String.format("Unexpected token (%s), expected %s", new Object[] { p.getCurrentToken(), expToken });
/*1507*/    msg = _colonConcat(msg, extra);
/*1508*/    return MismatchedInputException.from(p, targetType, msg);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException wrongTokenException(JsonParser p, JsonToken expToken, String msg) {
/*1515*/    return wrongTokenException(p, (JavaType)null, expToken, msg);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException weirdKeyException(Class<?> keyClass, String keyValue, String msg) {
/*1528*/    return InvalidFormatException.from(this._parser, String.format("Cannot deserialize Map key of type %s from String %s: %s", new Object[] { ClassUtil.nameOf(keyClass), _quotedString(keyValue), msg }), keyValue, keyClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException weirdStringException(String value, Class<?> instClass, String msg) {
/*1549*/    return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from String %s: %s", new Object[] { ClassUtil.nameOf(instClass), _quotedString(value), msg }), value, instClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException weirdNumberException(Number value, Class<?> instClass, String msg) {
/*1564*/    return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from number %s: %s", new Object[] { ClassUtil.nameOf(instClass), String.valueOf(value), msg }), value, instClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException weirdNativeValueException(Object value, Class<?> instClass) {
/*1582*/    return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from native value (`JsonToken.VALUE_EMBEDDED_OBJECT`) of type %s: incompatible types", new Object[] { ClassUtil.nameOf(instClass), ClassUtil.classNameOf(value) }), value, instClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException instantiationException(Class<?> instClass, Throwable cause) {
/*   0*/    String excMsg;
/*1599*/    JavaType type = constructType(instClass);
/*1601*/    if (cause == null) {
/*1602*/      excMsg = "N/A";
/*1603*/    } else if ((excMsg = ClassUtil.exceptionMessage(cause)) == null) {
/*1604*/      excMsg = ClassUtil.nameOf(cause.getClass());
/*   0*/    } 
/*1606*/    String msg = String.format("Cannot construct instance of %s, problem: %s", new Object[] { ClassUtil.nameOf(instClass), excMsg });
/*1608*/    InvalidDefinitionException e = InvalidDefinitionException.from(this._parser, msg, type);
/*1609*/    e.initCause(cause);
/*1610*/    return e;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException instantiationException(Class<?> instClass, String msg0) {
/*1624*/    JavaType type = constructType(instClass);
/*1625*/    String msg = String.format("Cannot construct instance of %s: %s", new Object[] { ClassUtil.nameOf(instClass), msg0 });
/*1627*/    return InvalidDefinitionException.from(this._parser, msg, type);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException invalidTypeIdException(JavaType baseType, String typeId, String extraDesc) {
/*1633*/    String msg = String.format("Could not resolve type id '%s' as a subtype of %s", new Object[] { typeId, baseType });
/*1635*/    return InvalidTypeIdException.from(this._parser, _colonConcat(msg, extraDesc), baseType, typeId);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonMappingException missingTypeIdException(JavaType baseType, String extraDesc) {
/*1643*/    String msg = String.format("Missing type id when trying to resolve subtype of %s", new Object[] { baseType });
/*1645*/    return InvalidTypeIdException.from(this._parser, _colonConcat(msg, extraDesc), baseType, null);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException unknownTypeException(JavaType type, String id, String extraDesc) {
/*1663*/    String msg = String.format("Could not resolve type id '%s' into a subtype of %s", new Object[] { id, type });
/*1665*/    msg = _colonConcat(msg, extraDesc);
/*1666*/    return MismatchedInputException.from(this._parser, type, msg);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException endOfInputException(Class<?> instClass) {
/*1677*/    return MismatchedInputException.from(this._parser, instClass, "Unexpected end-of-input when trying to deserialize a " + instClass.getName());
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void reportMappingException(String msg, Object... msgArgs) throws JsonMappingException {
/*1702*/    throw JsonMappingException.from(getParser(), _format(msg, msgArgs));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException mappingException(String message) {
/*1718*/    return JsonMappingException.from(getParser(), message);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException mappingException(String msg, Object... msgArgs) {
/*1734*/    return JsonMappingException.from(getParser(), _format(msg, msgArgs));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException mappingException(Class<?> targetClass) {
/*1744*/    return mappingException(targetClass, this._parser.getCurrentToken());
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonMappingException mappingException(Class<?> targetClass, JsonToken token) {
/*1752*/    return JsonMappingException.from(this._parser, String.format("Cannot deserialize instance of %s out of %s token", new Object[] { ClassUtil.nameOf(targetClass), token }));
/*   0*/  }
/*   0*/  
/*   0*/  protected DateFormat getDateFormat() {
/*1765*/    if (this._dateFormat != null) {
/*1766*/        return this._dateFormat; 
/*   0*/       }
/*1773*/    DateFormat df = this._config.getDateFormat();
/*1774*/    this._dateFormat = df = (DateFormat)df.clone();
/*1775*/    return df;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract ReadableObjectId findObjectId(Object paramObject, ObjectIdGenerator<?> paramObjectIdGenerator, ObjectIdResolver paramObjectIdResolver);
/*   0*/  
/*   0*/  public abstract void checkUnresolvedObjectId() throws UnresolvedForwardReference;
/*   0*/  
/*   0*/  public abstract JsonDeserializer<Object> deserializerInstance(Annotated paramAnnotated, Object paramObject) throws JsonMappingException;
/*   0*/  
/*   0*/  public abstract KeyDeserializer keyDeserializerInstance(Annotated paramAnnotated, Object paramObject) throws JsonMappingException;
/*   0*/}
