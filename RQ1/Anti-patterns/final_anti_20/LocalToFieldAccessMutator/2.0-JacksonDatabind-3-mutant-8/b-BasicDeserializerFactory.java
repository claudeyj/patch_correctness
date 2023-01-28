/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.databind.AbstractTypeResolver;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.KeyDeserializer;
/*   0*/import com.fasterxml.jackson.databind.PropertyMetadata;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
/*   0*/import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
/*   0*/import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
/*   0*/import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*   0*/import com.fasterxml.jackson.databind.jsontype.NamedType;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.type.ArrayType;
/*   0*/import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*   0*/import com.fasterxml.jackson.databind.type.CollectionType;
/*   0*/import com.fasterxml.jackson.databind.type.MapLikeType;
/*   0*/import com.fasterxml.jackson.databind.type.MapType;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.EnumResolver;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.EnumMap;
/*   0*/import java.util.EnumSet;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Queue;
/*   0*/import java.util.Set;
/*   0*/import java.util.SortedMap;
/*   0*/import java.util.SortedSet;
/*   0*/import java.util.TreeMap;
/*   0*/import java.util.TreeSet;
/*   0*/import java.util.concurrent.ArrayBlockingQueue;
/*   0*/import java.util.concurrent.ConcurrentHashMap;
/*   0*/import java.util.concurrent.ConcurrentMap;
/*   0*/import java.util.concurrent.ConcurrentNavigableMap;
/*   0*/import java.util.concurrent.ConcurrentSkipListMap;
/*   0*/
/*   0*/public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
/*  38*/  private static final Class<?> CLASS_OBJECT = Object.class;
/*   0*/  
/*  39*/  private static final Class<?> CLASS_STRING = String.class;
/*   0*/  
/*  40*/  private static final Class<?> CLASS_CHAR_BUFFER = CharSequence.class;
/*   0*/  
/*  41*/  private static final Class<?> CLASS_ITERABLE = Iterable.class;
/*   0*/  
/*  47*/  protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
/*   0*/  
/*  54*/  static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap<String, Class<? extends Map>>();
/*   0*/  
/*   0*/  static {
/*  57*/    _mapFallbacks.put(Map.class.getName(), LinkedHashMap.class);
/*  58*/    _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
/*  59*/    _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
/*  66*/    _mapFallbacks.put("java.util.NavigableMap", TreeMap.class);
/*   0*/    try {
/*  68*/      Class<?> key = ConcurrentNavigableMap.class;
/*  69*/      Class<?> value = ConcurrentSkipListMap.class;
/*  71*/      Class<? extends Map<?, ?>> mapValue = (Class)value;
/*  72*/      _mapFallbacks.put(key.getName(), mapValue);
/*  73*/    } catch (Throwable e) {
/*  74*/      System.err.println("Problems with (optional) types: " + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*  83*/  static final HashMap<String, Class<? extends Collection>> _collectionFallbacks = new HashMap<String, Class<? extends Collection>>();
/*   0*/  
/*   0*/  protected final DeserializerFactoryConfig _factoryConfig;
/*   0*/  
/*   0*/  static {
/*  86*/    _collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
/*  87*/    _collectionFallbacks.put(List.class.getName(), ArrayList.class);
/*  88*/    _collectionFallbacks.put(Set.class.getName(), HashSet.class);
/*  89*/    _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
/*  90*/    _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
/*  97*/    _collectionFallbacks.put("java.util.Deque", LinkedList.class);
/*  98*/    _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
/*   0*/  }
/*   0*/  
/*   0*/  protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
/* 120*/    this._factoryConfig = config;
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializerFactoryConfig getFactoryConfig() {
/* 131*/    return this._factoryConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
/* 148*/    return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
/* 157*/    return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
/* 166*/    return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
/* 175*/    return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
/* 184*/    return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
/*   0*/    while (true) {
/* 199*/      JavaType next = _mapAbstractType2(config, type);
/* 200*/      if (next == null) {
/* 201*/          return type; 
/*   0*/         }
/* 207*/      Class<?> prevCls = type.getRawClass();
/* 208*/      Class<?> nextCls = next.getRawClass();
/* 209*/      if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
/* 210*/          throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former"); 
/*   0*/         }
/* 212*/      type = next;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
/* 223*/    Class<?> currClass = type.getRawClass();
/* 224*/    if (this._factoryConfig.hasAbstractTypeResolvers()) {
/* 225*/        for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
/* 226*/          JavaType concrete = resolver.findTypeMapping(config, type);
/* 227*/          if (concrete != null && concrete.getRawClass() != currClass) {
/* 228*/              return concrete; 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 232*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 251*/    DeserializationConfig config = ctxt.getConfig();
/* 253*/    ValueInstantiator instantiator = null;
/* 255*/    AnnotatedClass ac = beanDesc.getClassInfo();
/* 256*/    Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
/* 257*/    if (instDef != null) {
/* 258*/        instantiator = _valueInstantiatorInstance(config, ac, instDef); 
/*   0*/       }
/* 260*/    if (instantiator == null) {
/* 264*/      instantiator = _findStdValueInstantiator(config, beanDesc);
/* 265*/      if (instantiator == null) {
/* 266*/          instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc); 
/*   0*/         }
/*   0*/    } 
/* 271*/    if (this._factoryConfig.hasValueInstantiators()) {
/* 272*/        for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
/* 273*/          instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
/* 275*/          if (instantiator == null) {
/* 276*/              throw new JsonMappingException("Broken registered ValueInstantiators (of type " + insts.getClass().getName() + "): returned null ValueInstantiator"); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 283*/    if (instantiator.getIncompleteParameter() != null) {
/* 284*/      AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
/* 285*/      AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
/* 286*/      throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/    } 
/* 289*/    return instantiator;
/*   0*/  }
/*   0*/  
/*   0*/  private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 296*/    if (beanDesc.getBeanClass() == JsonLocation.class) {
/* 297*/        return new JsonLocationInstantiator(); 
/*   0*/       }
/* 299*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 310*/    boolean fixAccess = ctxt.canOverrideAccessModifiers();
/* 311*/    CreatorCollector creators = new CreatorCollector(beanDesc, fixAccess);
/* 312*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 315*/    DeserializationConfig config = ctxt.getConfig();
/* 316*/    VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker();
/* 317*/    vchecker = intr.findAutoDetectVisibility(beanDesc.getClassInfo(), vchecker);
/* 322*/    _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators);
/* 324*/    if (beanDesc.getType().isConcrete()) {
/* 325*/        _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators); 
/*   0*/       }
/* 327*/    return creators.constructValueInstantiator(config);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
/* 334*/    if (instDef == null) {
/* 335*/        return null; 
/*   0*/       }
/* 340*/    if (instDef instanceof ValueInstantiator) {
/* 341*/        return (ValueInstantiator)instDef; 
/*   0*/       }
/* 343*/    if (!(instDef instanceof Class)) {
/* 344*/        throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead"); 
/*   0*/       }
/* 348*/    Class<?> instClass = (Class)instDef;
/* 349*/    if (ClassUtil.isBogusClass(instClass)) {
/* 350*/        return null; 
/*   0*/       }
/* 352*/    if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
/* 353*/        throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>"); 
/*   0*/       }
/* 356*/    HandlerInstantiator hi = config.getHandlerInstantiator();
/* 357*/    if (hi != null) {
/* 358*/      ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
/* 359*/      if (inst != null) {
/* 360*/          return inst; 
/*   0*/         }
/*   0*/    } 
/* 363*/    return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators) throws JsonMappingException {
/* 376*/    AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
/* 377*/    if (defaultCtor != null && (
/* 378*/      !creators.hasDefaultCreator() || intr.hasCreatorAnnotation(defaultCtor))) {
/* 379*/        creators.setDefaultCreator(defaultCtor); 
/*   0*/       }
/* 383*/    PropertyName[] ctorPropNames = null;
/* 384*/    AnnotatedConstructor propertyCtor = null;
/* 385*/    for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
/* 386*/      if (propDef.getConstructorParameter() != null) {
/* 387*/        AnnotatedParameter param = propDef.getConstructorParameter();
/* 388*/        AnnotatedWithParams owner = param.getOwner();
/* 389*/        if (owner instanceof AnnotatedConstructor) {
/* 390*/          if (propertyCtor == null) {
/* 391*/            propertyCtor = (AnnotatedConstructor)owner;
/* 392*/            ctorPropNames = new PropertyName[propertyCtor.getParameterCount()];
/*   0*/          } 
/* 394*/          ctorPropNames[param.getIndex()] = propDef.getFullName();
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 399*/    for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
/* 400*/      int argCount = ctor.getParameterCount();
/* 401*/      boolean isCreator = (intr.hasCreatorAnnotation(ctor) || ctor == propertyCtor);
/* 402*/      boolean isVisible = vchecker.isCreatorVisible(ctor);
/* 404*/      if (argCount == 1) {
/* 405*/        PropertyName name = (ctor == propertyCtor) ? ctorPropNames[0] : null;
/* 406*/        _handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, isVisible, name);
/*   0*/        continue;
/*   0*/      } 
/* 410*/      if (!isCreator && !isVisible) {
/*   0*/          continue; 
/*   0*/         }
/* 420*/      AnnotatedParameter nonAnnotatedParam = null;
/* 421*/      int namedCount = 0;
/* 422*/      int injectCount = 0;
/* 423*/      CreatorProperty[] properties = new CreatorProperty[argCount];
/* 424*/      for (int i = 0; i < argCount; i++) {
/* 425*/        AnnotatedParameter param = ctor.getParameter(i);
/* 426*/        PropertyName name = null;
/* 427*/        if (ctor == propertyCtor) {
/* 428*/            name = ctorPropNames[i]; 
/*   0*/           }
/* 430*/        if (name == null) {
/* 431*/            name = _findParamName(param, intr); 
/*   0*/           }
/* 433*/        Object injectId = intr.findInjectableValueId(param);
/* 434*/        if (name != null && name.hasSimpleName()) {
/* 435*/          namedCount++;
/* 436*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 437*/        } else if (injectId != null) {
/* 438*/          injectCount++;
/* 439*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 441*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 442*/          if (unwrapper != null) {
/* 443*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/* 445*/            namedCount++;
/* 447*/          } else if (nonAnnotatedParam == null) {
/* 448*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 455*/      if (isCreator || namedCount > 0 || injectCount > 0) {
/* 457*/        if (namedCount + injectCount == argCount) {
/* 458*/          creators.addPropertyCreator(ctor, properties);
/*   0*/          continue;
/*   0*/        } 
/* 459*/        if (namedCount == 0 && injectCount + 1 == argCount) {
/* 461*/          creators.addDelegatingCreator(ctor, properties);
/*   0*/          continue;
/*   0*/        } 
/* 463*/        creators.addIncompeteParameter(nonAnnotatedParam);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentConstructor(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible, PropertyName name) throws JsonMappingException {
/* 477*/    AnnotatedParameter param = ctor.getParameter(0);
/* 478*/    if (name == null) {
/* 479*/        name = _findParamName(param, intr); 
/*   0*/       }
/* 481*/    Object injectId = intr.findInjectableValueId(param);
/* 483*/    if (injectId != null || (name != null && name.hasSimpleName())) {
/* 485*/      CreatorProperty[] properties = new CreatorProperty[1];
/* 486*/      properties[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, param, injectId);
/* 487*/      creators.addPropertyCreator(ctor, properties);
/* 488*/      return true;
/*   0*/    } 
/* 492*/    Class<?> type = ctor.getRawParameterType(0);
/* 493*/    if (type == String.class) {
/* 494*/      if (isCreator || isVisible) {
/* 495*/          creators.addStringCreator(ctor); 
/*   0*/         }
/* 497*/      return true;
/*   0*/    } 
/* 499*/    if (type == int.class || type == Integer.class) {
/* 500*/      if (isCreator || isVisible) {
/* 501*/          creators.addIntCreator(ctor); 
/*   0*/         }
/* 503*/      return true;
/*   0*/    } 
/* 505*/    if (type == long.class || type == Long.class) {
/* 506*/      if (isCreator || isVisible) {
/* 507*/          creators.addLongCreator(ctor); 
/*   0*/         }
/* 509*/      return true;
/*   0*/    } 
/* 511*/    if (type == double.class || type == Double.class) {
/* 512*/      if (isCreator || isVisible) {
/* 513*/          creators.addDoubleCreator(ctor); 
/*   0*/         }
/* 515*/      return true;
/*   0*/    } 
/* 517*/    if (type == boolean.class || type == Boolean.class) {
/* 518*/      if (isCreator || isVisible) {
/* 519*/          creators.addBooleanCreator(ctor); 
/*   0*/         }
/* 521*/      return true;
/*   0*/    } 
/* 524*/    if (isCreator) {
/* 525*/      creators.addDelegatingCreator(ctor, null);
/* 526*/      return true;
/*   0*/    } 
/* 528*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators) throws JsonMappingException {
/* 536*/    DeserializationConfig config = ctxt.getConfig();
/* 537*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 538*/      boolean isCreator = intr.hasCreatorAnnotation(factory);
/* 539*/      int argCount = factory.getParameterCount();
/* 541*/      if (argCount == 0) {
/* 542*/        if (isCreator) {
/* 543*/            creators.setDefaultCreator(factory); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 548*/      if (argCount == 1) {
/* 549*/        AnnotatedParameter param = factory.getParameter(0);
/* 550*/        PropertyName pn = _findParamName(param, intr);
/* 551*/        String name = (pn == null) ? null : pn.getSimpleName();
/* 552*/        Object injectId = intr.findInjectableValueId(param);
/* 554*/        if (injectId == null && (name == null || name.length() == 0)) {
/* 555*/          _handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
/*   0*/          continue;
/*   0*/        } 
/* 563*/      } else if (!intr.hasCreatorAnnotation(factory)) {
/*   0*/        continue;
/*   0*/      } 
/* 568*/      AnnotatedParameter nonAnnotatedParam = null;
/* 569*/      CreatorProperty[] properties = new CreatorProperty[argCount];
/* 570*/      int namedCount = 0;
/* 571*/      int injectCount = 0;
/* 572*/      for (int i = 0; i < argCount; i++) {
/* 573*/        AnnotatedParameter param = factory.getParameter(i);
/* 574*/        PropertyName name = _findParamName(param, intr);
/* 575*/        Object injectId = intr.findInjectableValueId(param);
/* 576*/        if (name != null && name.hasSimpleName()) {
/* 577*/          namedCount++;
/* 578*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 579*/        } else if (injectId != null) {
/* 580*/          injectCount++;
/* 581*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 583*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 584*/          if (unwrapper != null) {
/* 585*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/* 586*/            namedCount++;
/* 588*/          } else if (nonAnnotatedParam == null) {
/* 589*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 596*/      if (isCreator || namedCount > 0 || injectCount > 0) {
/* 598*/        if (namedCount + injectCount == argCount) {
/* 599*/          creators.addPropertyCreator(factory, properties);
/*   0*/          continue;
/*   0*/        } 
/* 600*/        if (namedCount == 0 && injectCount + 1 == argCount) {
/* 602*/          creators.addDelegatingCreator(factory, properties);
/*   0*/          continue;
/*   0*/        } 
/* 604*/        throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator) throws JsonMappingException {
/* 617*/    Class<?> type = factory.getRawParameterType(0);
/* 619*/    if (type == String.class) {
/* 620*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 621*/          creators.addStringCreator(factory); 
/*   0*/         }
/* 623*/      return true;
/*   0*/    } 
/* 625*/    if (type == int.class || type == Integer.class) {
/* 626*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 627*/          creators.addIntCreator(factory); 
/*   0*/         }
/* 629*/      return true;
/*   0*/    } 
/* 631*/    if (type == long.class || type == Long.class) {
/* 632*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 633*/          creators.addLongCreator(factory); 
/*   0*/         }
/* 635*/      return true;
/*   0*/    } 
/* 637*/    if (type == double.class || type == Double.class) {
/* 638*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 639*/          creators.addDoubleCreator(factory); 
/*   0*/         }
/* 641*/      return true;
/*   0*/    } 
/* 643*/    if (type == boolean.class || type == Boolean.class) {
/* 644*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 645*/          creators.addBooleanCreator(factory); 
/*   0*/         }
/* 647*/      return true;
/*   0*/    } 
/* 649*/    if (intr.hasCreatorAnnotation(factory)) {
/* 650*/      creators.addDelegatingCreator(factory, null);
/* 651*/      return true;
/*   0*/    } 
/* 653*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected CreatorProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, Object injectableValueId) throws JsonMappingException {
/* 667*/    DeserializationConfig config = ctxt.getConfig();
/* 668*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 672*/    Boolean b = (intr == null) ? null : intr.hasRequiredMarker(param);
/* 673*/    boolean req = (b != null && b);
/* 674*/    String desc = (intr == null) ? null : intr.findPropertyDescription(param);
/* 675*/    Integer idx = (intr == null) ? null : intr.findPropertyIndex(param);
/* 676*/    PropertyMetadata metadata = PropertyMetadata.construct(req, desc, idx);
/* 679*/    JavaType t0 = config.getTypeFactory().constructType(param.getParameterType(), beanDesc.bindingsForBeanType());
/* 680*/    BeanProperty.Std property = new BeanProperty.Std(name, t0, intr.findWrapperName(param), beanDesc.getClassAnnotations(), param, metadata);
/* 683*/    JavaType type = resolveType(ctxt, beanDesc, t0, param);
/* 684*/    if (type != t0) {
/* 685*/        property = property.withType(type); 
/*   0*/       }
/* 688*/    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
/* 691*/    type = modifyTypeByAnnotation(ctxt, param, type);
/* 694*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/* 696*/    if (typeDeser == null) {
/* 697*/        typeDeser = findTypeDeserializer(config, type); 
/*   0*/       }
/* 701*/    CreatorProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
/* 704*/    if (deser != null) {
/* 706*/      deser = ctxt.handlePrimaryContextualization(deser, prop);
/* 707*/      prop = prop.withValueDeserializer(deser);
/*   0*/    } 
/* 709*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  protected PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 714*/    if (param != null && intr != null) {
/* 715*/      PropertyName name = intr.findNameForDeserialization(param);
/* 716*/      if (name != null) {
/* 717*/          return name; 
/*   0*/         }
/* 722*/      String str = intr.findImplicitPropertyName(param);
/* 723*/      if (str != null && !str.isEmpty()) {
/* 724*/          return new PropertyName(str); 
/*   0*/         }
/*   0*/    } 
/* 727*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
/* 741*/    DeserializationConfig config = ctxt.getConfig();
/* 742*/    JavaType elemType = type.getContentType();
/* 745*/    JsonDeserializer<Object> contentDeser = elemType.<JsonDeserializer<Object>>getValueHandler();
/* 747*/    TypeDeserializer elemTypeDeser = elemType.<TypeDeserializer>getTypeHandler();
/* 749*/    if (elemTypeDeser == null) {
/* 750*/        elemTypeDeser = findTypeDeserializer(config, elemType); 
/*   0*/       }
/* 753*/    JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
/* 755*/    if (deser == null) {
/* 756*/      if (contentDeser == null) {
/* 757*/        Class<?> raw = elemType.getRawClass();
/* 758*/        if (elemType.isPrimitive()) {
/* 759*/            return PrimitiveArrayDeserializers.forType(raw); 
/*   0*/           }
/* 760*/        if (CLASS_ITERABLE == String.class) {
/* 761*/            return StringArrayDeserializer.instance; 
/*   0*/           }
/*   0*/      } 
/* 764*/      deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
/*   0*/    } 
/* 767*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 768*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 769*/            deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/* 772*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 780*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/* 781*/      JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/* 783*/      if (deser != null) {
/* 784*/          return deser; 
/*   0*/         }
/*   0*/    } 
/* 787*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
/* 801*/    JavaType contentType = type.getContentType();
/* 803*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/* 804*/    DeserializationConfig config = ctxt.getConfig();
/* 807*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/* 809*/    if (contentTypeDeser == null) {
/* 810*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/* 814*/    JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/* 816*/    if (deser == null) {
/* 817*/      Class<?> collectionClass = type.getRawClass();
/* 818*/      if (contentDeser == null) {
/* 820*/          if (EnumSet.class.isAssignableFrom(collectionClass)) {
/* 821*/              deser = new EnumSetDeserializer(contentType, null); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/* 835*/    if (deser == null) {
/* 836*/      if (type.isInterface() || type.isAbstract()) {
/* 837*/        CollectionType implType = _mapAbstractCollectionType(type, config);
/* 838*/        if (implType == null) {
/* 840*/          if (type.getTypeHandler() == null) {
/* 841*/              throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type); 
/*   0*/             }
/* 843*/          deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/        } else {
/* 845*/          type = implType;
/* 847*/          beanDesc = config.introspectForCreation(type);
/*   0*/        } 
/*   0*/      } 
/* 850*/      if (deser == null) {
/* 851*/        ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/* 852*/        if (!inst.canCreateUsingDefault()) {
/* 854*/            if (type.getRawClass() == ArrayBlockingQueue.class) {
/* 855*/                return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst, null); 
/*   0*/               } 
/*   0*/           }
/* 859*/        if (contentType.getRawClass() == String.class) {
/* 861*/          deser = new StringCollectionDeserializer(type, contentDeser, inst);
/*   0*/        } else {
/* 863*/          deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 868*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 869*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 870*/            deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/* 873*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
/* 878*/    Class<?> collectionClass = type.getRawClass();
/* 879*/    collectionClass = _collectionFallbacks.get(collectionClass.getName());
/* 880*/    if (collectionClass == null) {
/* 881*/        return null; 
/*   0*/       }
/* 883*/    return (CollectionType)config.constructSpecializedType(type, collectionClass);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 891*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/* 892*/      JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/* 894*/      if (deser != null) {
/* 895*/          return deser; 
/*   0*/         }
/*   0*/    } 
/* 898*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/* 907*/    JavaType contentType = type.getContentType();
/* 909*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/* 910*/    DeserializationConfig config = ctxt.getConfig();
/* 913*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/* 915*/    if (contentTypeDeser == null) {
/* 916*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/* 918*/    JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/* 920*/    if (deser != null) {
/* 922*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/* 923*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 924*/                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/* 928*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/* 936*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/* 937*/      JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/* 939*/      if (deser != null) {
/* 940*/          return deser; 
/*   0*/         }
/*   0*/    } 
/* 943*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
/* 957*/    DeserializationConfig config = ctxt.getConfig();
/* 958*/    JavaType keyType = type.getKeyType();
/* 959*/    JavaType contentType = type.getContentType();
/* 963*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/* 966*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/* 968*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/* 970*/    if (contentTypeDeser == null) {
/* 971*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/* 975*/    JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/* 978*/    if (deser == null) {
/* 980*/      Class<?> mapClass = type.getRawClass();
/* 981*/      if (EnumMap.class.isAssignableFrom(mapClass)) {
/* 982*/        Class<?> kt = keyType.getRawClass();
/* 983*/        if (kt == null || !kt.isEnum()) {
/* 984*/            throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available"); 
/*   0*/           }
/* 986*/        deser = new EnumMapDeserializer(type, null, contentDeser, contentTypeDeser);
/*   0*/      } 
/*1000*/      if (deser == null) {
/*1001*/        if (type.isInterface() || type.isAbstract()) {
/*1003*/          Class<? extends Map> fallback = _mapFallbacks.get(mapClass.getName());
/*1004*/          if (fallback != null) {
/*1005*/            mapClass = fallback;
/*1006*/            type = (MapType)config.constructSpecializedType(type, mapClass);
/*1008*/            beanDesc = config.introspectForCreation(type);
/*   0*/          } else {
/*1011*/            if (type.getTypeHandler() == null) {
/*1012*/                throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type); 
/*   0*/               }
/*1014*/            deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/          } 
/*   0*/        } 
/*1017*/        if (deser == null) {
/*1018*/          ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1019*/          MapDeserializer<?> md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
/*1020*/          md.setIgnorableProperties(config.getAnnotationIntrospector().findPropertiesToIgnore(beanDesc.getClassInfo()));
/*1021*/          deser = md;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1026*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1027*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1028*/            deser = mod.modifyMapDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1031*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1040*/    JavaType keyType = type.getKeyType();
/*1041*/    JavaType contentType = type.getContentType();
/*1042*/    DeserializationConfig config = ctxt.getConfig();
/*1046*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1049*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1056*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1058*/    if (contentTypeDeser == null) {
/*1059*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1061*/    JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1063*/    if (deser != null) {
/*1065*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1066*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1067*/                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1071*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1080*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1081*/      JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1083*/      if (deser != null) {
/*1084*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1087*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1096*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1097*/      JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1099*/      if (deser != null) {
/*1100*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1103*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1120*/    DeserializationConfig config = ctxt.getConfig();
/*1121*/    Class<?> enumClass = type.getRawClass();
/*1123*/    JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1124*/    if (deser == null) {
/*1126*/      for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1127*/        if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
/*1128*/          int argCount = factory.getParameterCount();
/*1129*/          if (argCount == 1) {
/*1130*/            Class<?> returnType = factory.getRawReturnType();
/*1132*/            if (returnType.isAssignableFrom(enumClass)) {
/*1133*/              deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory);
/*   0*/              break;
/*   0*/            } 
/*   0*/          } 
/*1137*/          throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*   0*/        } 
/*   0*/      } 
/*1142*/      if (deser == null) {
/*1143*/          deser = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod())); 
/*   0*/         }
/*   0*/    } 
/*1148*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1149*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1150*/            deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1153*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1160*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1161*/      JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
/*1162*/      if (deser != null) {
/*1163*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1166*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
/*1181*/    Class<? extends JsonNode> nodeClass = (Class)nodeType.getRawClass();
/*1183*/    JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
/*1185*/    if (custom != null) {
/*1186*/        return custom; 
/*   0*/       }
/*1188*/    return JsonNodeDeserializer.getDeserializer(nodeClass);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1195*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1196*/      JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
/*1197*/      if (deser != null) {
/*1198*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1201*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
/*1215*/    BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/*1216*/    AnnotatedClass ac = bean.getClassInfo();
/*1217*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1218*/    TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
/*1223*/    Collection<NamedType> subtypes = null;
/*1224*/    if (b == null) {
/*1225*/      b = config.getDefaultTyper(baseType);
/*1226*/      if (b == null) {
/*1227*/          return null; 
/*   0*/         }
/*   0*/    } else {
/*1230*/      subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(ac, config, ai);
/*   0*/    } 
/*1234*/    if (b.getDefaultImpl() == null && baseType.isAbstract()) {
/*1235*/      JavaType defaultType = mapAbstractType(config, baseType);
/*1236*/      if (defaultType != null && defaultType.getRawClass() != baseType.getRawClass()) {
/*1237*/          b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass()); 
/*   0*/         }
/*   0*/    } 
/*1240*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1254*/    DeserializationConfig config = ctxt.getConfig();
/*1255*/    KeyDeserializer deser = null;
/*1256*/    if (this._factoryConfig.hasKeyDeserializers()) {
/*1257*/      BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
/*1258*/      for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
/*1259*/        deser = d.findKeyDeserializer(type, config, beanDesc);
/*1260*/        if (deser != null) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1266*/    if (deser == null) {
/*1267*/      if (type.isEnumType()) {
/*1268*/          return _createEnumKeyDeserializer(ctxt, type); 
/*   0*/         }
/*1270*/      deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
/*   0*/    } 
/*1274*/    if (deser != null && 
/*1275*/      this._factoryConfig.hasDeserializerModifiers()) {
/*1276*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1277*/            deser = mod.modifyKeyDeserializer(config, type, deser); 
/*   0*/           } 
/*   0*/       }
/*1281*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1288*/    DeserializationConfig config = ctxt.getConfig();
/*1289*/    BeanDescription beanDesc = config.introspect(type);
/*1290*/    JsonDeserializer<?> des = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1291*/    if (des != null) {
/*1292*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, des); 
/*   0*/       }
/*1294*/    Class<?> enumClass = type.getRawClass();
/*1296*/    JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1297*/    if (custom != null) {
/*1298*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom); 
/*   0*/       }
/*1301*/    EnumResolver<?> enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod());
/*1303*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1304*/      if (config.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
/*1305*/        int argCount = factory.getParameterCount();
/*1306*/        if (argCount == 1) {
/*1307*/          Class<?> returnType = factory.getRawReturnType();
/*1309*/          if (returnType.isAssignableFrom(enumClass)) {
/*1311*/            if (factory.getGenericParameterType(0) != String.class) {
/*1312*/                throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String"); 
/*   0*/               }
/*1314*/            if (config.canOverrideAccessModifiers()) {
/*1315*/                ClassUtil.checkAndFixAccess(factory.getMember()); 
/*   0*/               }
/*1317*/            return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
/*   0*/          } 
/*   0*/        } 
/*1320*/        throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*   0*/      } 
/*   0*/    } 
/*1325*/    return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
/*1351*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1352*/    TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
/*1354*/    if (b == null) {
/*1355*/        return findTypeDeserializer(config, baseType); 
/*   0*/       }
/*1358*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(annotated, config, ai, baseType);
/*1360*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
/*1378*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1379*/    TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
/*1380*/    JavaType contentType = containerType.getContentType();
/*1382*/    if (b == null) {
/*1383*/        return findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1386*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(propertyEntity, config, ai, contentType);
/*1388*/    return b.buildTypeDeserializer(config, contentType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1402*/    Class<?> rawType = type.getRawClass();
/*1404*/    if (rawType == CLASS_OBJECT) {
/*1405*/        return new UntypedObjectDeserializer(); 
/*   0*/       }
/*1407*/    if (rawType == CLASS_STRING || rawType == CLASS_CHAR_BUFFER) {
/*1408*/        return StringDeserializer.instance; 
/*   0*/       }
/*1410*/    if (rawType == CLASS_ITERABLE) {
/*1412*/      TypeFactory tf = ctxt.getTypeFactory();
/*1413*/      JavaType elemType = (type.containedTypeCount() > 0) ? type.containedType(0) : TypeFactory.unknownType();
/*1414*/      CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
/*1416*/      return createCollectionDeserializer(ctxt, ct, beanDesc);
/*   0*/    } 
/*1418*/    String clsName = rawType.getName();
/*1419*/    if (rawType.isPrimitive() || clsName.startsWith("java.")) {
/*1421*/      JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
/*1422*/      if (deser == null) {
/*1423*/          deser = DateDeserializers.find(rawType, clsName); 
/*   0*/         }
/*1425*/      if (deser != null) {
/*1426*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1430*/    if (rawType == TokenBuffer.class) {
/*1431*/        return new TokenBufferDeserializer(); 
/*   0*/       }
/*1433*/    return JdkDeserializers.find(rawType, clsName);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*1451*/    Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
/*1452*/    if (deserDef == null) {
/*1453*/        return null; 
/*   0*/       }
/*1455*/    return ctxt.deserializerInstance(ann, deserDef);
/*   0*/  }
/*   0*/  
/*   0*/  protected <T extends JavaType> T modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, T type) throws JsonMappingException {
/*   0*/    JavaType javaType;
/*1480*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1481*/    Class<?> subclass = intr.findDeserializationType(a, (JavaType)type);
/*1482*/    if (subclass != null) {
/*   0*/        try {
/*1484*/          javaType = type.narrowBy(subclass);
/*1485*/        } catch (IllegalArgumentException iae) {
/*1486*/          throw new JsonMappingException("Failed to narrow type " + javaType + " with concrete-type annotation (value " + subclass.getName() + "), method '" + a.getName() + "': " + iae.getMessage(), null, iae);
/*   0*/        }  
/*   0*/       }
/*1491*/    if (javaType.isContainerType()) {
/*1492*/      Class<?> keyClass = intr.findDeserializationKeyType(a, javaType.getKeyType());
/*1493*/      if (keyClass != null) {
/*1495*/        if (!(javaType instanceof MapLikeType)) {
/*1496*/            throw new JsonMappingException("Illegal key-type annotation: type " + javaType + " is not a Map(-like) type"); 
/*   0*/           }
/*   0*/        try {
/*1499*/          javaType = ((MapLikeType)javaType).narrowKey(keyClass);
/*1500*/        } catch (IllegalArgumentException iae) {
/*1501*/          throw new JsonMappingException("Failed to narrow key type " + javaType + " with key-type annotation (" + keyClass.getName() + "): " + iae.getMessage(), null, iae);
/*   0*/        } 
/*   0*/      } 
/*1504*/      JavaType keyType = javaType.getKeyType();
/*1509*/      if (keyType != null && keyType.getValueHandler() == null) {
/*1510*/        Object kdDef = intr.findKeyDeserializer(a);
/*1511*/        KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
/*1512*/        if (kd != null) {
/*1513*/          javaType = ((MapLikeType)javaType).withKeyValueHandler(kd);
/*1514*/          keyType = javaType.getKeyType();
/*   0*/        } 
/*   0*/      } 
/*1519*/      Class<?> cc = intr.findDeserializationContentType(a, javaType.getContentType());
/*1520*/      if (cc != null) {
/*   0*/          try {
/*1522*/            javaType = javaType.narrowContentsBy(cc);
/*1523*/          } catch (IllegalArgumentException iae) {
/*1524*/            throw new JsonMappingException("Failed to narrow content type " + javaType + " with content-type annotation (" + cc.getName() + "): " + iae.getMessage(), null, iae);
/*   0*/          }  
/*   0*/         }
/*1528*/      JavaType contentType = javaType.getContentType();
/*1529*/      if (contentType.getValueHandler() == null) {
/*1530*/        Object cdDef = intr.findContentDeserializer(a);
/*1531*/        JsonDeserializer<?> cd = ctxt.deserializerInstance(a, cdDef);
/*1532*/        if (cd != null) {
/*1533*/            javaType = javaType.withContentValueHandler(cd); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1537*/    return (T)javaType;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
/*   0*/    TypeDeserializer valueTypeDeser;
/*1552*/    if (type.isContainerType()) {
/*1553*/      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1554*/      JavaType keyType = type.getKeyType();
/*1555*/      if (keyType != null) {
/*1556*/        Object kdDef = intr.findKeyDeserializer(member);
/*1557*/        KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
/*1558*/        if (kd != null) {
/*1559*/          type = ((MapLikeType)type).withKeyValueHandler(kd);
/*1560*/          keyType = type.getKeyType();
/*   0*/        } 
/*   0*/      } 
/*1564*/      Object cdDef = intr.findContentDeserializer(member);
/*1565*/      JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
/*1566*/      if (cd != null) {
/*1567*/          type = type.withContentValueHandler(cd); 
/*   0*/         }
/*1574*/      if (member instanceof AnnotatedMember) {
/*1575*/        TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
/*1577*/        if (contentTypeDeser != null) {
/*1578*/            type = type.withContentTypeHandler(contentTypeDeser); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1584*/    if (member instanceof AnnotatedMember) {
/*1585*/      valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
/*   0*/    } else {
/*1589*/      valueTypeDeser = findTypeDeserializer(ctxt.getConfig(), type);
/*   0*/    } 
/*1591*/    if (valueTypeDeser != null) {
/*1592*/        type = type.withTypeHandler(valueTypeDeser); 
/*   0*/       }
/*1594*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  protected EnumResolver<?> constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMethod jsonValueMethod) {
/*1600*/    if (jsonValueMethod != null) {
/*1601*/      Method accessor = jsonValueMethod.getAnnotated();
/*1602*/      if (config.canOverrideAccessModifiers()) {
/*1603*/          ClassUtil.checkAndFixAccess(accessor); 
/*   0*/         }
/*1605*/      return EnumResolver.constructUnsafeUsingMethod(enumClass, accessor);
/*   0*/    } 
/*1608*/    if (config.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING)) {
/*1609*/        return EnumResolver.constructUnsafeUsingToString(enumClass); 
/*   0*/       }
/*1611*/    return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
/*1616*/    if (enumType == null) {
/*1617*/        return null; 
/*   0*/       }
/*1619*/    BeanDescription beanDesc = config.introspect(enumType);
/*1620*/    return beanDesc.findJsonValueMethod();
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
/*   0*/}
