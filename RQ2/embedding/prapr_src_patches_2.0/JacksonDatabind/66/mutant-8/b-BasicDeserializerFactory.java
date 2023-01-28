/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonCreator;
/*   0*/import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.databind.AbstractTypeResolver;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.KeyDeserializer;
/*   0*/import com.fasterxml.jackson.databind.MapperFeature;
/*   0*/import com.fasterxml.jackson.databind.PropertyMetadata;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
/*   0*/import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
/*   0*/import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
/*   0*/import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
/*   0*/import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*   0*/import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
/*   0*/import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*   0*/import com.fasterxml.jackson.databind.jsontype.NamedType;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.type.ArrayType;
/*   0*/import com.fasterxml.jackson.databind.type.CollectionLikeType;
/*   0*/import com.fasterxml.jackson.databind.type.CollectionType;
/*   0*/import com.fasterxml.jackson.databind.type.MapLikeType;
/*   0*/import com.fasterxml.jackson.databind.type.MapType;
/*   0*/import com.fasterxml.jackson.databind.type.ReferenceType;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.EnumResolver;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.EnumMap;
/*   0*/import java.util.EnumSet;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.NavigableMap;
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
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
/*  39*/  private static final Class<?> CLASS_OBJECT = Object.class;
/*   0*/  
/*  40*/  private static final Class<?> CLASS_STRING = String.class;
/*   0*/  
/*  41*/  private static final Class<?> CLASS_CHAR_BUFFER = CharSequence.class;
/*   0*/  
/*  42*/  private static final Class<?> CLASS_ITERABLE = Iterable.class;
/*   0*/  
/*  43*/  private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
/*   0*/  
/*  49*/  protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
/*   0*/  
/*  56*/  static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap<>();
/*   0*/  
/*   0*/  static {
/*  59*/    _mapFallbacks.put(Map.class.getName(), LinkedHashMap.class);
/*  60*/    _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
/*  61*/    _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
/*  63*/    _mapFallbacks.put(NavigableMap.class.getName(), TreeMap.class);
/*  64*/    _mapFallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
/*   0*/  }
/*   0*/  
/*  73*/  static final HashMap<String, Class<? extends Collection>> _collectionFallbacks = new HashMap<>();
/*   0*/  
/*   0*/  protected final DeserializerFactoryConfig _factoryConfig;
/*   0*/  
/*   0*/  static {
/*  76*/    _collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
/*  77*/    _collectionFallbacks.put(List.class.getName(), ArrayList.class);
/*  78*/    _collectionFallbacks.put(Set.class.getName(), HashSet.class);
/*  79*/    _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
/*  80*/    _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
/*  87*/    _collectionFallbacks.put("java.util.Deque", LinkedList.class);
/*  88*/    _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
/*   0*/  }
/*   0*/  
/*   0*/  protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
/* 110*/    this._factoryConfig = config;
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializerFactoryConfig getFactoryConfig() {
/* 121*/    return this._factoryConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
/* 138*/    return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
/* 147*/    return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
/* 156*/    return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
/* 165*/    return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
/* 174*/    return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
/*   0*/    while (true) {
/* 188*/      JavaType next = _mapAbstractType2(config, type);
/* 189*/      if (next == null) {
/* 190*/          return type; 
/*   0*/         }
/* 194*/      Class<?> prevCls = type.getRawClass();
/* 195*/      Class<?> nextCls = next.getRawClass();
/* 196*/      if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
/* 197*/          throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former"); 
/*   0*/         }
/* 199*/      type = next;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
/* 210*/    Class<?> currClass = type.getRawClass();
/* 211*/    if (this._factoryConfig.hasAbstractTypeResolvers()) {
/* 212*/        for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
/* 213*/          JavaType concrete = resolver.findTypeMapping(config, type);
/* 214*/          if (concrete != null && concrete.getRawClass() != currClass) {
/* 215*/              return concrete; 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 219*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 238*/    DeserializationConfig config = ctxt.getConfig();
/* 240*/    ValueInstantiator instantiator = null;
/* 242*/    AnnotatedClass ac = beanDesc.getClassInfo();
/* 243*/    Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
/* 244*/    if (instDef != null) {
/* 245*/        instantiator = _valueInstantiatorInstance(config, ac, instDef); 
/*   0*/       }
/* 247*/    if (instantiator == null) {
/* 251*/      instantiator = _findStdValueInstantiator(config, beanDesc);
/* 252*/      if (instantiator == null) {
/* 253*/          instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc); 
/*   0*/         }
/*   0*/    } 
/* 258*/    if (this._factoryConfig.hasValueInstantiators()) {
/* 259*/        for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
/* 260*/          instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
/* 262*/          if (instantiator == null) {
/* 263*/              ctxt.reportMappingException("Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", new Object[] { insts.getClass().getName() }); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 271*/    if (instantiator.getIncompleteParameter() != null) {
/* 272*/      AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
/* 273*/      AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
/* 274*/      throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/    } 
/* 277*/    return instantiator;
/*   0*/  }
/*   0*/  
/*   0*/  private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 284*/    if (beanDesc.getBeanClass() == JsonLocation.class) {
/* 285*/        return new JsonLocationInstantiator(); 
/*   0*/       }
/* 287*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 298*/    CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
/* 299*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 302*/    DeserializationConfig config = ctxt.getConfig();
/* 303*/    VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker();
/* 304*/    vchecker = intr.findAutoDetectVisibility(beanDesc.getClassInfo(), vchecker);
/* 314*/    Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
/* 320*/    _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
/* 322*/    if (beanDesc.getType().isConcrete()) {
/* 323*/        _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs); 
/*   0*/       }
/* 325*/    return creators.constructValueInstantiator(config);
/*   0*/  }
/*   0*/  
/*   0*/  protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 331*/    Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = (Map)Collections.emptyMap();
/* 332*/    for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
/* 333*/      Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
/* 334*/      while (it.hasNext()) {
/* 335*/        AnnotatedParameter param = it.next();
/* 336*/        AnnotatedWithParams owner = param.getOwner();
/* 337*/        BeanPropertyDefinition[] defs = result.get(owner);
/* 338*/        int index = param.getIndex();
/* 340*/        if (defs == null) {
/* 341*/          if (result.isEmpty()) {
/* 342*/              result = (Map)new LinkedHashMap<>(); 
/*   0*/             }
/* 344*/          defs = new BeanPropertyDefinition[owner.getParameterCount()];
/* 345*/          result.put(owner, defs);
/* 347*/        } else if (defs[index] != null) {
/* 348*/          throw new IllegalStateException("Conflict: parameter #" + index + " of " + owner + " bound to more than one property; " + defs[index] + " vs " + propDef);
/*   0*/        } 
/* 352*/        defs[index] = propDef;
/*   0*/      } 
/*   0*/    } 
/* 355*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
/* 362*/    if (instDef == null) {
/* 363*/        return null; 
/*   0*/       }
/* 368*/    if (instDef instanceof ValueInstantiator) {
/* 369*/        return (ValueInstantiator)instDef; 
/*   0*/       }
/* 371*/    if (!(instDef instanceof Class)) {
/* 372*/        throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead"); 
/*   0*/       }
/* 376*/    Class<?> instClass = (Class)instDef;
/* 377*/    if (ClassUtil.isBogusClass(instClass)) {
/* 378*/        return null; 
/*   0*/       }
/* 380*/    if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
/* 381*/        throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>"); 
/*   0*/       }
/* 384*/    HandlerInstantiator hi = config.getHandlerInstantiator();
/* 385*/    if (hi != null) {
/* 386*/      ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
/* 387*/      if (inst != null) {
/* 388*/          return inst; 
/*   0*/         }
/*   0*/    } 
/* 391*/    return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
/* 404*/    AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
/* 405*/    if (defaultCtor != null && (
/* 406*/      !creators.hasDefaultCreator() || intr.hasCreatorAnnotation(defaultCtor))) {
/* 407*/        creators.setDefaultCreator(defaultCtor); 
/*   0*/       }
/* 412*/    List<AnnotatedConstructor> implicitCtors = null;
/* 413*/    for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
/* 414*/      boolean isCreator = intr.hasCreatorAnnotation(ctor);
/* 415*/      BeanPropertyDefinition[] propDefs = creatorParams.get(ctor);
/* 416*/      int argCount = ctor.getParameterCount();
/* 419*/      if (argCount == 1) {
/* 420*/        BeanPropertyDefinition argDef = (propDefs == null) ? null : propDefs[0];
/* 421*/        boolean useProps = _checkIfCreatorPropertyBased(intr, ctor, argDef);
/* 423*/        if (useProps) {
/* 424*/          SettableBeanProperty[] arrayOfSettableBeanProperty = new SettableBeanProperty[1];
/* 425*/          PropertyName name = (argDef == null) ? null : argDef.getFullName();
/* 426*/          AnnotatedParameter arg = ctor.getParameter(0);
/* 427*/          arrayOfSettableBeanProperty[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, arg, intr.findInjectableValueId(arg));
/* 429*/          creators.addPropertyCreator(ctor, isCreator, arrayOfSettableBeanProperty);
/*   0*/          continue;
/*   0*/        } 
/* 431*/        _handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, vchecker.isCreatorVisible(ctor));
/* 436*/        if (argDef != null) {
/* 437*/            ((POJOPropertyBuilder)argDef).removeConstructors(); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 448*/      AnnotatedParameter nonAnnotatedParam = null;
/* 449*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 450*/      int explicitNameCount = 0;
/* 451*/      int implicitWithCreatorCount = 0;
/* 452*/      int injectCount = 0;
/* 454*/      for (int i = 0; i < argCount; i++) {
/* 455*/        AnnotatedParameter param = ctor.getParameter(i);
/* 456*/        BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
/* 457*/        Object injectId = intr.findInjectableValueId(param);
/* 458*/        PropertyName name = (propDef == null) ? null : propDef.getFullName();
/* 460*/        if (propDef != null && propDef.isExplicitlyNamed()) {
/* 461*/          explicitNameCount++;
/* 462*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 465*/        } else if (injectId != null) {
/* 466*/          injectCount++;
/* 467*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 470*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 471*/          if (unwrapper != null) {
/* 472*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/* 473*/            explicitNameCount++;
/* 477*/          } else if (isCreator && name != null && !name.isEmpty()) {
/* 478*/            implicitWithCreatorCount++;
/* 479*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 482*/          } else if (nonAnnotatedParam == null) {
/* 483*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 487*/      int namedCount = explicitNameCount + implicitWithCreatorCount;
/* 489*/      if (isCreator || explicitNameCount > 0 || injectCount > 0) {
/* 491*/        if (namedCount + injectCount == argCount) {
/* 492*/          creators.addPropertyCreator(ctor, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 495*/        if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/* 497*/          creators.addDelegatingCreator(ctor, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 503*/        PropertyName impl = _findImplicitParamName(nonAnnotatedParam, intr);
/* 504*/        if (impl == null || impl.isEmpty()) {
/* 506*/          int ix = nonAnnotatedParam.getIndex();
/* 507*/          if (ix == 0 && ClassUtil.isNonStaticInnerClass(ctor.getDeclaringClass())) {
/* 508*/              throw new IllegalArgumentException("Non-static inner classes like " + ctor.getDeclaringClass().getName() + " can not use @JsonCreator for constructors"); 
/*   0*/             }
/* 511*/          throw new IllegalArgumentException("Argument #" + ix + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/        } 
/*   0*/      } 
/* 516*/      if (!creators.hasDefaultCreator()) {
/* 517*/        if (implicitCtors == null) {
/* 518*/            implicitCtors = new LinkedList<>(); 
/*   0*/           }
/* 520*/        implicitCtors.add(ctor);
/*   0*/      } 
/*   0*/    } 
/* 525*/    if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
/* 527*/        _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedConstructor> implicitCtors) throws JsonMappingException {
/* 537*/    AnnotatedConstructor found = null;
/* 538*/    SettableBeanProperty[] foundProps = null;
/* 545*/    label33: for (AnnotatedConstructor ctor : implicitCtors) {
/* 546*/      if (!vchecker.isCreatorVisible(ctor)) {
/*   0*/          continue; 
/*   0*/         }
/* 550*/      int argCount = ctor.getParameterCount();
/* 551*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 552*/      for (int i = 0; i < argCount; ) {
/* 553*/        AnnotatedParameter param = ctor.getParameter(i);
/* 554*/        PropertyName name = _findParamName(param, intr);
/* 557*/        if (name != null) {
/* 557*/          if (name.isEmpty()) {
/*   0*/              continue label33; 
/*   0*/             }
/* 560*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
/*   0*/          i++;
/*   0*/        } 
/*   0*/        continue label33;
/*   0*/      } 
/* 563*/      if (found != null) {
/* 564*/        found = null;
/*   0*/        break;
/*   0*/      } 
/* 567*/      found = ctor;
/* 568*/      foundProps = properties;
/*   0*/    } 
/* 571*/    if (found != null) {
/* 572*/      creators.addPropertyCreator(found, false, foundProps);
/* 573*/      BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
/* 575*/      for (SettableBeanProperty prop : foundProps) {
/* 576*/        PropertyName pn = prop.getFullName();
/* 577*/        if (!bbd.hasProperty(pn)) {
/* 578*/          BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
/* 580*/          bbd.addProperty(newDef);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
/* 589*/    JsonCreator.Mode mode = intr.findCreatorBinding(creator);
/* 591*/    if (mode == JsonCreator.Mode.PROPERTIES) {
/* 592*/        return true; 
/*   0*/       }
/* 594*/    if (mode == JsonCreator.Mode.DELEGATING) {
/* 595*/        return false; 
/*   0*/       }
/* 598*/    if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValueId(creator.getParameter(0)) != null) {
/* 600*/        return true; 
/*   0*/       }
/* 602*/    if (propDef != null) {
/* 605*/      String implName = propDef.getName();
/* 606*/      if (implName != null && !implName.isEmpty() && 
/* 607*/        propDef.couldSerialize()) {
/* 608*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 613*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentConstructor(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible) throws JsonMappingException {
/* 623*/    Class<?> type = ctor.getRawParameterType(0);
/* 624*/    if (type == String.class || type == CharSequence.class) {
/* 625*/      if (isCreator || isVisible) {
/* 626*/          creators.addStringCreator(ctor, isCreator); 
/*   0*/         }
/* 628*/      return true;
/*   0*/    } 
/* 630*/    if (type == int.class || type == Integer.class) {
/* 631*/      if (isCreator || isVisible) {
/* 632*/          creators.addIntCreator(ctor, isCreator); 
/*   0*/         }
/* 634*/      return true;
/*   0*/    } 
/* 636*/    if (type == long.class || type == Long.class) {
/* 637*/      if (isCreator || isVisible) {
/* 638*/          creators.addLongCreator(ctor, isCreator); 
/*   0*/         }
/* 640*/      return true;
/*   0*/    } 
/* 642*/    if (type == double.class || type == Double.class) {
/* 643*/      if (isCreator || isVisible) {
/* 644*/          creators.addDoubleCreator(ctor, isCreator); 
/*   0*/         }
/* 646*/      return true;
/*   0*/    } 
/* 648*/    if (type == boolean.class || type == Boolean.class) {
/* 649*/      if (isCreator || isVisible) {
/* 650*/          creators.addBooleanCreator(ctor, isCreator); 
/*   0*/         }
/* 652*/      return true;
/*   0*/    } 
/* 655*/    if (isCreator) {
/* 656*/      creators.addDelegatingCreator(ctor, isCreator, null);
/* 657*/      return true;
/*   0*/    } 
/* 659*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
/* 668*/    DeserializationConfig config = ctxt.getConfig();
/* 669*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 670*/      boolean isCreator = intr.hasCreatorAnnotation(factory);
/* 671*/      int argCount = factory.getParameterCount();
/* 673*/      if (argCount == 0) {
/* 674*/        if (isCreator) {
/* 675*/            creators.setDefaultCreator(factory); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 680*/      BeanPropertyDefinition[] propDefs = creatorParams.get(factory);
/* 682*/      if (argCount == 1) {
/* 683*/        BeanPropertyDefinition argDef = (propDefs == null) ? null : propDefs[0];
/* 684*/        boolean useProps = _checkIfCreatorPropertyBased(intr, factory, argDef);
/* 685*/        if (!useProps) {
/* 686*/          _handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
/* 690*/          if (argDef != null) {
/* 691*/              ((POJOPropertyBuilder)argDef).removeConstructors(); 
/*   0*/             }
/*   0*/          continue;
/*   0*/        } 
/* 698*/      } else if (!isCreator) {
/*   0*/        continue;
/*   0*/      } 
/* 703*/      AnnotatedParameter nonAnnotatedParam = null;
/* 704*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 705*/      int implicitNameCount = 0;
/* 706*/      int explicitNameCount = 0;
/* 707*/      int injectCount = 0;
/* 709*/      for (int i = 0; i < argCount; i++) {
/* 710*/        AnnotatedParameter param = factory.getParameter(i);
/* 711*/        BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
/* 712*/        Object injectId = intr.findInjectableValueId(param);
/* 713*/        PropertyName name = (propDef == null) ? null : propDef.getFullName();
/* 715*/        if (propDef != null && propDef.isExplicitlyNamed()) {
/* 716*/          explicitNameCount++;
/* 717*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 720*/        } else if (injectId != null) {
/* 721*/          injectCount++;
/* 722*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 725*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 726*/          if (unwrapper != null) {
/* 727*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/* 728*/            implicitNameCount++;
/* 732*/          } else if (isCreator && 
/* 733*/            name != null && !name.isEmpty()) {
/* 734*/            implicitNameCount++;
/* 735*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 751*/          } else if (nonAnnotatedParam == null) {
/* 752*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 755*/      int namedCount = explicitNameCount + implicitNameCount;
/* 758*/      if (isCreator || explicitNameCount > 0 || injectCount > 0) {
/* 760*/        if (namedCount + injectCount == argCount) {
/* 761*/          creators.addPropertyCreator(factory, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 762*/        if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/* 764*/          creators.addDelegatingCreator(factory, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 766*/        throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator) throws JsonMappingException {
/* 779*/    Class<?> type = factory.getRawParameterType(0);
/* 781*/    if (type == String.class || type == CharSequence.class) {
/* 782*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 783*/          creators.addStringCreator(factory, isCreator); 
/*   0*/         }
/* 785*/      return true;
/*   0*/    } 
/* 787*/    if (type == int.class || type == Integer.class) {
/* 788*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 789*/          creators.addIntCreator(factory, isCreator); 
/*   0*/         }
/* 791*/      return true;
/*   0*/    } 
/* 793*/    if (type == long.class || type == Long.class) {
/* 794*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 795*/          creators.addLongCreator(factory, isCreator); 
/*   0*/         }
/* 797*/      return true;
/*   0*/    } 
/* 799*/    if (type == double.class || type == Double.class) {
/* 800*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 801*/          creators.addDoubleCreator(factory, isCreator); 
/*   0*/         }
/* 803*/      return true;
/*   0*/    } 
/* 805*/    if (type == boolean.class || type == Boolean.class) {
/* 806*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 807*/          creators.addBooleanCreator(factory, isCreator); 
/*   0*/         }
/* 809*/      return true;
/*   0*/    } 
/* 811*/    if (isCreator) {
/* 812*/      creators.addDelegatingCreator(factory, isCreator, null);
/* 813*/      return true;
/*   0*/    } 
/* 815*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, Object injectableValueId) throws JsonMappingException {
/*   0*/    PropertyMetadata metadata;
/* 829*/    DeserializationConfig config = ctxt.getConfig();
/* 830*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 833*/    if (intr == null) {
/* 834*/      metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
/*   0*/    } else {
/* 836*/      Boolean b = intr.hasRequiredMarker(param);
/* 837*/      boolean req = (b != null && b);
/* 838*/      String desc = intr.findPropertyDescription(param);
/* 839*/      Integer idx = intr.findPropertyIndex(param);
/* 840*/      String def = intr.findPropertyDefaultValue(param);
/* 841*/      metadata = PropertyMetadata.construct(req, desc, idx, def);
/*   0*/    } 
/* 844*/    JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
/* 845*/    BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), beanDesc.getClassAnnotations(), param, metadata);
/* 849*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/* 851*/    if (typeDeser == null) {
/* 852*/        typeDeser = findTypeDeserializer(config, type); 
/*   0*/       }
/* 856*/    SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
/* 859*/    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
/* 860*/    if (deser == null) {
/* 861*/        deser = type.<JsonDeserializer>getValueHandler(); 
/*   0*/       }
/* 863*/    if (deser != null) {
/* 865*/      deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/* 866*/      prop = prop.withValueDeserializer(deser);
/*   0*/    } 
/* 868*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  protected PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 873*/    if (param != null && intr != null) {
/* 874*/      PropertyName name = intr.findNameForDeserialization(param);
/* 875*/      if (name != null) {
/* 876*/          return name; 
/*   0*/         }
/* 881*/      String str = intr.findImplicitPropertyName(param);
/* 882*/      if (str != null && !str.isEmpty()) {
/* 883*/          return PropertyName.construct(str); 
/*   0*/         }
/*   0*/    } 
/* 886*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected PropertyName _findImplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 891*/    String str = intr.findImplicitPropertyName(param);
/* 892*/    if (str != null && !str.isEmpty()) {
/* 893*/        return PropertyName.construct(str); 
/*   0*/       }
/* 895*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected PropertyName _findExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 901*/    if (param != null && intr != null) {
/* 902*/        return intr.findNameForDeserialization(param); 
/*   0*/       }
/* 904*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected boolean _hasExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 910*/    if (param != null && intr != null) {
/* 911*/      PropertyName n = intr.findNameForDeserialization(param);
/* 912*/      return (n != null && n.hasSimpleName());
/*   0*/    } 
/* 914*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
/* 928*/    DeserializationConfig config = ctxt.getConfig();
/* 929*/    JavaType elemType = type.getContentType();
/* 932*/    JsonDeserializer<Object> contentDeser = elemType.<JsonDeserializer<Object>>getValueHandler();
/* 934*/    TypeDeserializer elemTypeDeser = elemType.<TypeDeserializer>getTypeHandler();
/* 936*/    if (elemTypeDeser == null) {
/* 937*/        elemTypeDeser = findTypeDeserializer(config, elemType); 
/*   0*/       }
/* 940*/    JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
/* 942*/    if (deser == null) {
/* 943*/      if (contentDeser == null) {
/* 944*/        Class<?> raw = elemType.getRawClass();
/* 945*/        if (elemType.isPrimitive()) {
/* 946*/            return PrimitiveArrayDeserializers.forType(raw); 
/*   0*/           }
/* 947*/        if (raw == String.class) {
/* 948*/            return StringArrayDeserializer.instance; 
/*   0*/           }
/*   0*/      } 
/* 951*/      deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
/*   0*/    } 
/* 954*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 955*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 956*/            deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/* 959*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
/* 973*/    JavaType contentType = type.getContentType();
/* 975*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/* 976*/    DeserializationConfig config = ctxt.getConfig();
/* 979*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/* 981*/    if (contentTypeDeser == null) {
/* 982*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/* 985*/    JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/* 987*/    if (deser == null) {
/* 988*/      Class<?> collectionClass = type.getRawClass();
/* 989*/      if (contentDeser == null) {
/* 991*/          if (EnumSet.class.isAssignableFrom(collectionClass)) {
/* 992*/              deser = new EnumSetDeserializer(contentType, null); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/*1006*/    if (deser == null) {
/*1007*/      if (type.isInterface() || type.isAbstract()) {
/*1008*/        CollectionType implType = _mapAbstractCollectionType(type, config);
/*1009*/        if (implType == null) {
/*1011*/          if (type.getTypeHandler() == null) {
/*1012*/              throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type); 
/*   0*/             }
/*1014*/          deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/        } else {
/*1016*/          type = implType;
/*1018*/          beanDesc = config.introspectForCreation(type);
/*   0*/        } 
/*   0*/      } 
/*1021*/      if (deser == null) {
/*1022*/        ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1023*/        if (!inst.canCreateUsingDefault()) {
/*1025*/            if (type.getRawClass() == ArrayBlockingQueue.class) {
/*1026*/                return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst); 
/*   0*/               } 
/*   0*/           }
/*1030*/        if (contentType.getRawClass() == String.class) {
/*1032*/          deser = new StringCollectionDeserializer(type, contentDeser, inst);
/*   0*/        } else {
/*1034*/          deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1039*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1040*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1041*/            deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1044*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
/*1049*/    Class<?> collectionClass = type.getRawClass();
/*1050*/    collectionClass = _collectionFallbacks.get(collectionClass.getName());
/*1051*/    if (collectionClass == null) {
/*1052*/        return null; 
/*   0*/       }
/*1054*/    return (CollectionType)config.constructSpecializedType(type, collectionClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1063*/    JavaType contentType = type.getContentType();
/*1065*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1066*/    DeserializationConfig config = ctxt.getConfig();
/*1069*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1071*/    if (contentTypeDeser == null) {
/*1072*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1074*/    JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1076*/    if (deser != null) {
/*1078*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1079*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1080*/                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1084*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
/*1098*/    DeserializationConfig config = ctxt.getConfig();
/*1099*/    JavaType keyType = type.getKeyType();
/*1100*/    JavaType contentType = type.getContentType();
/*1104*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1107*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1109*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1111*/    if (contentTypeDeser == null) {
/*1112*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1116*/    JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1119*/    if (deser == null) {
/*1121*/      Class<?> mapClass = type.getRawClass();
/*1122*/      if (EnumMap.class.isAssignableFrom(mapClass)) {
/*1123*/        Class<?> kt = keyType.getRawClass();
/*1124*/        if (kt == null || !kt.isEnum()) {
/*1125*/            throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available"); 
/*   0*/           }
/*1127*/        deser = new EnumMapDeserializer(type, null, contentDeser, contentTypeDeser);
/*   0*/      } 
/*1141*/      if (deser == null) {
/*1142*/        if (type.isInterface() || type.isAbstract()) {
/*1144*/          Class<? extends Map> fallback = _mapFallbacks.get(mapClass.getName());
/*1145*/          if (fallback != null) {
/*1146*/            mapClass = fallback;
/*1147*/            type = (MapType)config.constructSpecializedType(type, mapClass);
/*1149*/            beanDesc = config.introspectForCreation(type);
/*   0*/          } else {
/*1152*/            if (type.getTypeHandler() == null) {
/*1153*/                throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type); 
/*   0*/               }
/*1155*/            deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/          } 
/*   0*/        } 
/*1158*/        if (deser == null) {
/*1159*/          ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1164*/          MapDeserializer<?> md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
/*1165*/          JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
/*1167*/          Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForDeserialization();
/*1169*/          md.setIgnorableProperties(ignored);
/*1170*/          deser = md;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1174*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1175*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1176*/            deser = mod.modifyMapDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1179*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1188*/    JavaType keyType = type.getKeyType();
/*1189*/    JavaType contentType = type.getContentType();
/*1190*/    DeserializationConfig config = ctxt.getConfig();
/*1194*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1197*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1204*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1206*/    if (contentTypeDeser == null) {
/*1207*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1209*/    JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1211*/    if (deser != null) {
/*1213*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1214*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1215*/                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1219*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1236*/    DeserializationConfig config = ctxt.getConfig();
/*1237*/    Class<?> enumClass = type.getRawClass();
/*1239*/    JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1241*/    if (deser == null) {
/*1242*/      ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/*1243*/      SettableBeanProperty[] creatorProps = (valueInstantiator == null) ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*1246*/      for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1247*/        if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
/*1248*/          if (factory.getParameterCount() == 0) {
/*1249*/            deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
/*   0*/            break;
/*   0*/          } 
/*1252*/          Class<?> returnType = factory.getRawReturnType();
/*1254*/          if (returnType.isAssignableFrom(enumClass)) {
/*1255*/            deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1262*/      if (deser == null) {
/*1263*/          deser = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod())); 
/*   0*/         }
/*   0*/    } 
/*1269*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1270*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1271*/            deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1274*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
/*1283*/    Class<? extends JsonNode> nodeClass = (Class)nodeType.getRawClass();
/*1285*/    JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
/*1287*/    if (custom != null) {
/*1288*/        return custom; 
/*   0*/       }
/*1290*/    return JsonNodeDeserializer.getDeserializer(nodeClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
/*1298*/    JavaType contentType = type.getContentType();
/*1300*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1301*/    DeserializationConfig config = ctxt.getConfig();
/*1303*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1304*/    if (contentTypeDeser == null) {
/*1305*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1307*/    JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1310*/    if (deser == null) {
/*1312*/        if (AtomicReference.class.isAssignableFrom(type.getRawClass())) {
/*1313*/            return new AtomicReferenceDeserializer(type, contentTypeDeser, contentDeser); 
/*   0*/           } 
/*   0*/       }
/*1316*/    if (deser != null) {
/*1318*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1319*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1320*/                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1324*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
/*1338*/    BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/*1339*/    AnnotatedClass ac = bean.getClassInfo();
/*1340*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1341*/    TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
/*1346*/    Collection<NamedType> subtypes = null;
/*1347*/    if (b == null) {
/*1348*/      b = config.getDefaultTyper(baseType);
/*1349*/      if (b == null) {
/*1350*/          return null; 
/*   0*/         }
/*   0*/    } else {
/*1353*/      subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
/*   0*/    } 
/*1357*/    if (b.getDefaultImpl() == null && baseType.isAbstract()) {
/*1358*/      JavaType defaultType = mapAbstractType(config, baseType);
/*1359*/      if (defaultType != null && defaultType.getRawClass() != baseType.getRawClass()) {
/*1360*/          b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass()); 
/*   0*/         }
/*   0*/    } 
/*1363*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1375*/    return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
/*   0*/  }
/*   0*/  
/*   0*/  public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1389*/    DeserializationConfig config = ctxt.getConfig();
/*1390*/    KeyDeserializer deser = null;
/*1391*/    if (this._factoryConfig.hasKeyDeserializers()) {
/*1392*/      BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
/*1393*/      for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
/*1394*/        deser = d.findKeyDeserializer(type, config, beanDesc);
/*1395*/        if (deser != null) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1401*/    if (deser == null) {
/*1402*/      if (type.isEnumType()) {
/*1403*/          return _createEnumKeyDeserializer(ctxt, type); 
/*   0*/         }
/*1405*/      deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
/*   0*/    } 
/*1409*/    if (deser != null && 
/*1410*/      this._factoryConfig.hasDeserializerModifiers()) {
/*1411*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1412*/            deser = mod.modifyKeyDeserializer(config, type, deser); 
/*   0*/           } 
/*   0*/       }
/*1416*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1423*/    DeserializationConfig config = ctxt.getConfig();
/*1424*/    Class<?> enumClass = type.getRawClass();
/*1426*/    BeanDescription beanDesc = config.introspect(type);
/*1428*/    KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1429*/    if (des != null) {
/*1430*/        return des; 
/*   0*/       }
/*1433*/    JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1437*/    JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1438*/    if (valueDesForKey != null) {
/*1439*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey); 
/*   0*/       }
/*1442*/    EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod());
/*1444*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1445*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1446*/      if (ai.hasCreatorAnnotation(factory)) {
/*1447*/        int argCount = factory.getParameterCount();
/*1448*/        if (argCount == 1) {
/*1449*/          Class<?> returnType = factory.getRawReturnType();
/*1451*/          if (returnType.isAssignableFrom(enumClass)) {
/*1453*/            if (factory.getRawParameterType(0) != String.class) {
/*1454*/                throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String"); 
/*   0*/               }
/*1456*/            if (config.canOverrideAccessModifiers()) {
/*1457*/                ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/               }
/*1460*/            return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
/*   0*/          } 
/*   0*/        } 
/*1463*/        throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*   0*/      } 
/*   0*/    } 
/*1468*/    return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
/*1494*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1495*/    TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
/*1497*/    if (b == null) {
/*1498*/        return findTypeDeserializer(config, baseType); 
/*   0*/       }
/*1501*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
/*1503*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
/*1521*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1522*/    TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
/*1523*/    JavaType contentType = containerType.getContentType();
/*1525*/    if (b == null) {
/*1526*/        return findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1529*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
/*1531*/    return b.buildTypeDeserializer(config, contentType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1545*/    Class<?> rawType = type.getRawClass();
/*1547*/    if (rawType == CLASS_OBJECT) {
/*   0*/      JavaType lt, mt;
/*1549*/      DeserializationConfig config = ctxt.getConfig();
/*1552*/      if (this._factoryConfig.hasAbstractTypeResolvers()) {
/*1553*/        lt = _findRemappedType(config, List.class);
/*1554*/        mt = _findRemappedType(config, Map.class);
/*   0*/      } else {
/*1556*/        lt = mt = null;
/*   0*/      } 
/*1558*/      return new UntypedObjectDeserializer(lt, mt);
/*   0*/    } 
/*1560*/    if (rawType == CLASS_STRING || rawType == CLASS_CHAR_BUFFER) {
/*1561*/        return StringDeserializer.instance; 
/*   0*/       }
/*1563*/    if (rawType == CLASS_ITERABLE) {
/*1565*/      TypeFactory tf = ctxt.getTypeFactory();
/*1566*/      JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
/*1567*/      JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
/*1568*/      CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
/*1570*/      return createCollectionDeserializer(ctxt, ct, beanDesc);
/*   0*/    } 
/*1572*/    if (rawType == CLASS_MAP_ENTRY) {
/*1574*/      JavaType kt = type.containedType(0);
/*1575*/      if (kt == null) {
/*1576*/          kt = TypeFactory.unknownType(); 
/*   0*/         }
/*1578*/      JavaType vt = type.containedType(1);
/*1579*/      if (vt == null) {
/*1580*/          vt = TypeFactory.unknownType(); 
/*   0*/         }
/*1582*/      TypeDeserializer vts = vt.<TypeDeserializer>getTypeHandler();
/*1583*/      if (vts == null) {
/*1584*/          vts = findTypeDeserializer(ctxt.getConfig(), vt); 
/*   0*/         }
/*1586*/      JsonDeserializer<Object> valueDeser = vt.<JsonDeserializer<Object>>getValueHandler();
/*1587*/      KeyDeserializer keyDes = kt.<KeyDeserializer>getValueHandler();
/*1588*/      return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
/*   0*/    } 
/*1590*/    String clsName = rawType.getName();
/*1591*/    if (rawType.isPrimitive() || clsName.startsWith("java.")) {
/*1593*/      JsonDeserializer<?> jsonDeserializer = NumberDeserializers.find(rawType, clsName);
/*1594*/      if (jsonDeserializer == null) {
/*1595*/          jsonDeserializer = DateDeserializers.find(rawType, clsName); 
/*   0*/         }
/*1597*/      if (jsonDeserializer != null) {
/*1598*/          return jsonDeserializer; 
/*   0*/         }
/*   0*/    } 
/*1602*/    if (rawType == TokenBuffer.class) {
/*1603*/        return new TokenBufferDeserializer(); 
/*   0*/       }
/*1605*/    JsonDeserializer<?> deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
/*1606*/    if (deser != null) {
/*1607*/        return deser; 
/*   0*/       }
/*1609*/    return JdkDeserializers.find(rawType, clsName);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
/*1613*/    JavaType type = mapAbstractType(config, config.constructType(rawType));
/*1614*/    return (type == null || type.hasRawClass(rawType)) ? null : type;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1627*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1628*/      JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
/*1629*/      if (deser != null) {
/*1630*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1633*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
/*1641*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1642*/      JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
/*1644*/      if (deser != null) {
/*1645*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1648*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1656*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1657*/      JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
/*1658*/      if (deser != null) {
/*1659*/          return (JsonDeserializer)deser; 
/*   0*/         }
/*   0*/    } 
/*1662*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1670*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1671*/      JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1673*/      if (deser != null) {
/*1674*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1677*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1685*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1686*/      JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1688*/      if (deser != null) {
/*1689*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1692*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1700*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1701*/      JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1703*/      if (deser != null) {
/*1704*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1707*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1714*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1715*/      JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
/*1716*/      if (deser != null) {
/*1717*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1720*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1729*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1730*/      JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1732*/      if (deser != null) {
/*1733*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1736*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1745*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1746*/      JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1748*/      if (deser != null) {
/*1749*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1752*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*1773*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1774*/    if (intr != null) {
/*1775*/      Object deserDef = intr.findDeserializer(ann);
/*1776*/      if (deserDef != null) {
/*1777*/          return ctxt.deserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*1780*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*1792*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1793*/    if (intr != null) {
/*1794*/      Object deserDef = intr.findKeyDeserializer(ann);
/*1795*/      if (deserDef != null) {
/*1796*/          return ctxt.keyDeserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*1799*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
/*1815*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1816*/    if (intr == null) {
/*1817*/        return type; 
/*   0*/       }
/*1823*/    if (type.isMapLikeType()) {
/*1824*/      JavaType keyType = type.getKeyType();
/*1825*/      if (keyType != null) {
/*1826*/        Object kdDef = intr.findKeyDeserializer(member);
/*1827*/        KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
/*1828*/        if (kd != null) {
/*1829*/          type = ((MapLikeType)type).withKeyValueHandler(kd);
/*1830*/          keyType = type.getKeyType();
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1835*/    if (type.hasContentType()) {
/*1836*/      Object cdDef = intr.findContentDeserializer(member);
/*1837*/      JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
/*1838*/      if (cd != null) {
/*1839*/          type = type.withContentValueHandler(cd); 
/*   0*/         }
/*1841*/      TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
/*1843*/      if (contentTypeDeser != null) {
/*1844*/          type = type.withContentTypeHandler(contentTypeDeser); 
/*   0*/         }
/*   0*/    } 
/*1847*/    TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
/*1849*/    if (valueTypeDeser != null) {
/*1850*/        type = type.withTypeHandler(valueTypeDeser); 
/*   0*/       }
/*1858*/    type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
/*1859*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMethod jsonValueMethod) {
/*1865*/    if (jsonValueMethod != null) {
/*1866*/      Method accessor = jsonValueMethod.getAnnotated();
/*1867*/      if (config.canOverrideAccessModifiers()) {
/*1868*/          ClassUtil.checkAndFixAccess(accessor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/         }
/*1870*/      return EnumResolver.constructUnsafeUsingMethod(enumClass, accessor, config.getAnnotationIntrospector());
/*   0*/    } 
/*1874*/    return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
/*1894*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1895*/    if (intr == null) {
/*1896*/        return type; 
/*   0*/       }
/*1928*/    return intr.refineDeserializationType(ctxt.getConfig(), a, type);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
/*1939*/    return resolveMemberAndTypeAnnotations(ctxt, member, type);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
/*1948*/    if (enumType == null) {
/*1949*/        return null; 
/*   0*/       }
/*1951*/    BeanDescription beanDesc = config.introspect(enumType);
/*1952*/    return beanDesc.findJsonValueMethod();
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
/*   0*/}
