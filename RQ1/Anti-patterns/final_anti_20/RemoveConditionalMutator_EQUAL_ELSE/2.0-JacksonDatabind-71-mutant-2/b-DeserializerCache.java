/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonFormat;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.KeyDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.type.ArrayType;
/*   0*/import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*   0*/import com.fasterxml.jackson.databind.type.CollectionType;
/*   0*/import com.fasterxml.jackson.databind.type.MapLikeType;
/*   0*/import com.fasterxml.jackson.databind.type.MapType;
/*   0*/import com.fasterxml.jackson.databind.type.ReferenceType;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.Converter;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.concurrent.ConcurrentHashMap;
/*   0*/
/*   0*/public final class DeserializerCache implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  42*/  protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.75F, 4);
/*   0*/  
/*  50*/  protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = new HashMap<JavaType, JsonDeserializer<Object>>(8);
/*   0*/  
/*   0*/  Object writeReplace() {
/*  69*/    this._incompleteDeserializers.clear();
/*  71*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public int cachedDeserializersCount() {
/*  93*/    return this._cachedDeserializers.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void flushCachedDeserializers() {
/* 104*/    this._cachedDeserializers.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType) throws JsonMappingException {
/* 139*/    JsonDeserializer<Object> deser = _findCachedDeserializer(propertyType);
/* 140*/    if (deser == null) {
/* 142*/      deser = _createAndCacheValueDeserializer(ctxt, factory, propertyType);
/* 143*/      if (deser == null) {
/* 148*/          deser = _handleUnknownValueDeserializer(ctxt, propertyType); 
/*   0*/         }
/*   0*/    } 
/* 151*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 166*/    KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
/* 171*/    if (kd instanceof ResolvableDeserializer) {
/* 172*/        ((ResolvableDeserializer)kd).resolve(ctxt); 
/*   0*/       }
/* 174*/    return kd;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 189*/    JsonDeserializer<Object> deser = _findCachedDeserializer(type);
/* 190*/    if (deser == null) {
/* 191*/        deser = _createAndCacheValueDeserializer(ctxt, factory, type); 
/*   0*/       }
/* 193*/    return (deser != null);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
/* 204*/    if (type == null) {
/* 205*/        throw new IllegalArgumentException("Null JavaType passed"); 
/*   0*/       }
/* 207*/    if (_hasCustomValueHandler(type)) {
/* 208*/        return null; 
/*   0*/       }
/* 210*/    return this._cachedDeserializers.get(type);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 228*/    synchronized (this._incompleteDeserializers) {
/* 230*/      JsonDeserializer<Object> deser = _findCachedDeserializer(type);
/* 231*/      if (deser != null) {
/* 232*/          return deser; 
/*   0*/         }
/* 234*/      int count = this._incompleteDeserializers.size();
/* 236*/      if (count > 0) {
/* 237*/        deser = this._incompleteDeserializers.get(type);
/* 238*/        if (deser != null) {
/* 239*/            return deser; 
/*   0*/           }
/*   0*/      } 
/*   0*/      try {
/* 244*/        return _createAndCache2(ctxt, factory, type);
/*   0*/      } finally {
/* 247*/        if (count == 0 && this._incompleteDeserializers.size() > 0) {
/* 248*/            this._incompleteDeserializers.clear(); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/*   0*/    JsonDeserializer<Object> deser;
/*   0*/    try {
/* 264*/      deser = _createDeserializer(ctxt, factory, type);
/* 265*/    } catch (IllegalArgumentException iae) {
/* 269*/      throw JsonMappingException.from(ctxt, iae.getMessage(), iae);
/*   0*/    } 
/* 271*/    if (deser == null) {
/* 272*/        return null; 
/*   0*/       }
/* 278*/    boolean isResolvable = deser instanceof ResolvableDeserializer;
/* 280*/    boolean addToCache = (!_hasCustomValueHandler(type) && deser.isCachable());
/* 294*/    if (isResolvable) {
/* 295*/      this._incompleteDeserializers.put(type, deser);
/* 296*/      ((ResolvableDeserializer)deser).resolve(ctxt);
/* 297*/      this._incompleteDeserializers.remove(type);
/*   0*/    } 
/* 299*/    if (addToCache) {
/* 300*/        this._cachedDeserializers.put(type, deser); 
/*   0*/       }
/* 302*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
/* 321*/    DeserializationConfig config = ctxt.getConfig();
/* 324*/    if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
/* 325*/        type = factory.mapAbstractType(config, type); 
/*   0*/       }
/* 327*/    BeanDescription beanDesc = config.introspect(type);
/* 329*/    JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/* 331*/    if (deser != null) {
/* 332*/        return deser; 
/*   0*/       }
/* 336*/    JavaType newType = modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
/* 337*/    if (newType != type) {
/* 338*/      type = newType;
/* 339*/      beanDesc = config.introspect(newType);
/*   0*/    } 
/* 343*/    Class<?> builder = beanDesc.findPOJOBuilder();
/* 344*/    if (builder != null) {
/* 345*/        return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder); 
/*   0*/       }
/* 350*/    Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
/* 351*/    if (conv == null) {
/* 352*/        return (JsonDeserializer)_createDeserializer2(ctxt, factory, type, beanDesc); 
/*   0*/       }
/* 355*/    JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 357*/    if (!delegateType.hasRawClass(type.getRawClass())) {
/* 358*/        beanDesc = config.introspect(delegateType); 
/*   0*/       }
/* 360*/    return new StdDelegatingDeserializer(conv, delegateType, _createDeserializer2(ctxt, factory, delegateType, beanDesc));
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 368*/    DeserializationConfig config = ctxt.getConfig();
/* 370*/    if (type.isEnumType()) {
/* 371*/        return factory.createEnumDeserializer(ctxt, type, beanDesc); 
/*   0*/       }
/* 373*/    if (type.isContainerType()) {
/* 374*/      if (type.isArrayType()) {
/* 375*/          return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc); 
/*   0*/         }
/* 377*/      if (type.isMapLikeType()) {
/* 378*/        MapLikeType mlt = (MapLikeType)type;
/* 379*/        if (mlt.isTrueMapType()) {
/* 380*/            return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc); 
/*   0*/           }
/* 382*/        return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
/*   0*/      } 
/* 384*/      if (type.isCollectionLikeType()) {
/* 390*/        JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/* 391*/        if (format == null || format.getShape() != JsonFormat.Shape.OBJECT) {
/* 392*/          CollectionLikeType clt = (CollectionLikeType)type;
/* 393*/          if (clt.isTrueCollectionType()) {
/* 394*/              return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc); 
/*   0*/             }
/* 396*/          return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 400*/    if (type.isReferenceType()) {
/* 401*/        return factory.createReferenceDeserializer(ctxt, (ReferenceType)type, beanDesc); 
/*   0*/       }
/* 403*/    if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
/* 404*/        return factory.createTreeDeserializer(config, type, beanDesc); 
/*   0*/       }
/* 406*/    return factory.createBeanDeserializer(ctxt, type, beanDesc);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/* 418*/    Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
/* 419*/    if (deserDef == null) {
/* 420*/        return null; 
/*   0*/       }
/* 422*/    JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
/* 424*/    return findConvertingDeserializer(ctxt, ann, deser);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser) throws JsonMappingException {
/* 437*/    Converter<Object, Object> conv = findConverter(ctxt, a);
/* 438*/    if (conv == null) {
/* 439*/        return deser; 
/*   0*/       }
/* 441*/    JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 442*/    return new StdDelegatingDeserializer(conv, delegateType, deser);
/*   0*/  }
/*   0*/  
/*   0*/  protected Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a) throws JsonMappingException {
/* 449*/    Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
/* 450*/    if (convDef == null) {
/* 451*/        return null; 
/*   0*/       }
/* 453*/    return ctxt.converterInstance(a, convDef);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
/* 475*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 476*/    if (intr == null) {
/* 477*/        return type; 
/*   0*/       }
/* 483*/    if (type.isMapLikeType()) {
/* 484*/      JavaType keyType = type.getKeyType();
/* 488*/      if (keyType != null && keyType.getValueHandler() == null) {
/* 489*/        Object kdDef = intr.findKeyDeserializer(a);
/* 490*/        if (kdDef != null) {
/* 491*/          KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
/* 492*/          if (kd != null) {
/* 493*/            type = ((MapLikeType)type).withKeyValueHandler(kd);
/* 494*/            keyType = type.getKeyType();
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 499*/    JavaType contentType = type.getContentType();
/* 500*/    if (contentType != null && 
/* 501*/      contentType.getValueHandler() == null) {
/* 502*/      Object cdDef = intr.findContentDeserializer(a);
/* 503*/      if (cdDef != null) {
/* 504*/        JsonDeserializer<?> cd = null;
/* 505*/        if (cdDef instanceof JsonDeserializer) {
/* 506*/          cdDef = cdDef;
/*   0*/        } else {
/* 508*/          Class<?> cdClass = _verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
/* 509*/          if (cdClass != null) {
/* 510*/              cd = ctxt.deserializerInstance(a, cdClass); 
/*   0*/             }
/*   0*/        } 
/* 513*/        if (cd != null) {
/* 514*/            type = type.withContentValueHandler(cd); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 522*/    type = intr.refineDeserializationType(ctxt.getConfig(), a, type);
/* 524*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _hasCustomValueHandler(JavaType t) {
/* 540*/    if (t.isContainerType()) {
/* 541*/      JavaType ct = t.getContentType();
/* 542*/      if (ct != null) {
/* 543*/          return (ct.getValueHandler() != null || ct.getTypeHandler() != null); 
/*   0*/         }
/*   0*/    } 
/* 546*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
/* 551*/    if (src == null) {
/* 552*/        return null; 
/*   0*/       }
/* 554*/    if (!(src instanceof Class)) {
/* 555*/        throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead"); 
/*   0*/       }
/* 557*/    Class<?> cls = (Class)src;
/* 558*/    if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
/* 559*/        return null; 
/*   0*/       }
/* 561*/    return cls;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 577*/    Class<?> rawClass = type.getRawClass();
/* 578*/    if (!ClassUtil.isConcrete(rawClass)) {
/* 579*/        throw JsonMappingException.from(ctxt, "Can not find a Value deserializer for abstract type " + type); 
/*   0*/       }
/* 581*/    throw JsonMappingException.from(ctxt, "Can not find a Value deserializer for type " + type);
/*   0*/  }
/*   0*/  
/*   0*/  protected KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/* 587*/    throw JsonMappingException.from(ctxt, "Can not find a (Map) Key deserializer for type " + type);
/*   0*/  }
/*   0*/}
