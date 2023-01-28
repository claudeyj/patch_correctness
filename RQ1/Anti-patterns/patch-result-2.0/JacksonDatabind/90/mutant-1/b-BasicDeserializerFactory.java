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
/* 318*/    _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
/* 320*/    beanDesc.getType().isConcrete();
/* 321*/    _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
/* 323*/    return creators.constructValueInstantiator(config);
/*   0*/  }
/*   0*/  
/*   0*/  protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 329*/    Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = (Map)Collections.emptyMap();
/* 330*/    for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
/* 331*/      Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
/* 332*/      while (it.hasNext()) {
/* 333*/        AnnotatedParameter param = it.next();
/* 334*/        AnnotatedWithParams owner = param.getOwner();
/* 335*/        BeanPropertyDefinition[] defs = result.get(owner);
/* 336*/        int index = param.getIndex();
/* 338*/        if (defs == null) {
/* 339*/          if (result.isEmpty()) {
/* 340*/              result = (Map)new LinkedHashMap<>(); 
/*   0*/             }
/* 342*/          defs = new BeanPropertyDefinition[owner.getParameterCount()];
/* 343*/          result.put(owner, defs);
/* 345*/        } else if (defs[index] != null) {
/* 346*/          throw new IllegalStateException("Conflict: parameter #" + index + " of " + owner + " bound to more than one property; " + defs[index] + " vs " + propDef);
/*   0*/        } 
/* 350*/        defs[index] = propDef;
/*   0*/      } 
/*   0*/    } 
/* 353*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
/* 360*/    if (instDef == null) {
/* 361*/        return null; 
/*   0*/       }
/* 366*/    if (instDef instanceof ValueInstantiator) {
/* 367*/        return (ValueInstantiator)instDef; 
/*   0*/       }
/* 369*/    if (!(instDef instanceof Class)) {
/* 370*/        throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead"); 
/*   0*/       }
/* 374*/    Class<?> instClass = (Class)instDef;
/* 375*/    if (ClassUtil.isBogusClass(instClass)) {
/* 376*/        return null; 
/*   0*/       }
/* 378*/    if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
/* 379*/        throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>"); 
/*   0*/       }
/* 382*/    HandlerInstantiator hi = config.getHandlerInstantiator();
/* 383*/    if (hi != null) {
/* 384*/      ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
/* 385*/      if (inst != null) {
/* 386*/          return inst; 
/*   0*/         }
/*   0*/    } 
/* 389*/    return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
/* 402*/    AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
/* 403*/    if (defaultCtor != null && (
/* 404*/      !creators.hasDefaultCreator() || intr.hasCreatorAnnotation(defaultCtor))) {
/* 405*/        creators.setDefaultCreator(defaultCtor); 
/*   0*/       }
/* 412*/    boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
/* 413*/    if (isNonStaticInnerClass) {
/*   0*/        return; 
/*   0*/       }
/* 419*/    List<AnnotatedConstructor> implicitCtors = null;
/* 420*/    for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
/* 421*/      boolean isCreator = intr.hasCreatorAnnotation(ctor);
/* 422*/      BeanPropertyDefinition[] propDefs = creatorParams.get(ctor);
/* 423*/      int argCount = ctor.getParameterCount();
/* 426*/      if (argCount == 1) {
/* 427*/        BeanPropertyDefinition argDef = (propDefs == null) ? null : propDefs[0];
/* 428*/        boolean useProps = _checkIfCreatorPropertyBased(intr, ctor, argDef);
/* 430*/        if (useProps) {
/* 431*/          SettableBeanProperty[] arrayOfSettableBeanProperty = new SettableBeanProperty[1];
/* 432*/          PropertyName name = (argDef == null) ? null : argDef.getFullName();
/* 433*/          AnnotatedParameter arg = ctor.getParameter(0);
/* 434*/          arrayOfSettableBeanProperty[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, arg, intr.findInjectableValueId(arg));
/* 436*/          creators.addPropertyCreator(ctor, isCreator, arrayOfSettableBeanProperty);
/*   0*/          continue;
/*   0*/        } 
/* 438*/        _handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, vchecker.isCreatorVisible(ctor));
/* 443*/        if (argDef != null) {
/* 444*/            ((POJOPropertyBuilder)argDef).removeConstructors(); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 455*/      AnnotatedParameter nonAnnotatedParam = null;
/* 456*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 457*/      int explicitNameCount = 0;
/* 458*/      int implicitWithCreatorCount = 0;
/* 459*/      int injectCount = 0;
/* 461*/      for (int i = 0; i < argCount; i++) {
/* 462*/        AnnotatedParameter param = ctor.getParameter(i);
/* 463*/        BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
/* 464*/        Object injectId = intr.findInjectableValueId(param);
/* 465*/        PropertyName name = (propDef == null) ? null : propDef.getFullName();
/* 467*/        if (propDef != null && propDef.isExplicitlyNamed()) {
/* 468*/          explicitNameCount++;
/* 469*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 472*/        } else if (injectId != null) {
/* 473*/          injectCount++;
/* 474*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 477*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 478*/          if (unwrapper != null) {
/* 479*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/* 480*/            explicitNameCount++;
/* 484*/          } else if (isCreator && name != null && !name.isEmpty()) {
/* 485*/            implicitWithCreatorCount++;
/* 486*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 489*/          } else if (nonAnnotatedParam == null) {
/* 490*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 494*/      int namedCount = explicitNameCount + implicitWithCreatorCount;
/* 496*/      if (isCreator || explicitNameCount > 0 || injectCount > 0) {
/* 498*/        if (namedCount + injectCount == argCount) {
/* 499*/          creators.addPropertyCreator(ctor, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 502*/        if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/* 504*/          creators.addDelegatingCreator(ctor, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 510*/        PropertyName impl = _findImplicitParamName(nonAnnotatedParam, intr);
/* 511*/        if (impl == null || impl.isEmpty()) {
/* 513*/          int ix = nonAnnotatedParam.getIndex();
/* 521*/          throw new IllegalArgumentException("Argument #" + ix + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/        } 
/*   0*/      } 
/* 526*/      if (!creators.hasDefaultCreator()) {
/* 527*/        if (implicitCtors == null) {
/* 528*/            implicitCtors = new LinkedList<>(); 
/*   0*/           }
/* 530*/        implicitCtors.add(ctor);
/*   0*/      } 
/*   0*/    } 
/* 535*/    if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
/* 537*/        _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedConstructor> implicitCtors) throws JsonMappingException {
/* 547*/    AnnotatedConstructor found = null;
/* 548*/    SettableBeanProperty[] foundProps = null;
/* 555*/    label33: for (AnnotatedConstructor ctor : implicitCtors) {
/* 556*/      if (!vchecker.isCreatorVisible(ctor)) {
/*   0*/          continue; 
/*   0*/         }
/* 560*/      int argCount = ctor.getParameterCount();
/* 561*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 562*/      for (int i = 0; i < argCount; ) {
/* 563*/        AnnotatedParameter param = ctor.getParameter(i);
/* 564*/        PropertyName name = _findParamName(param, intr);
/* 567*/        if (name != null) {
/* 567*/          if (name.isEmpty()) {
/*   0*/              continue label33; 
/*   0*/             }
/* 570*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
/*   0*/          i++;
/*   0*/        } 
/*   0*/        continue label33;
/*   0*/      } 
/* 573*/      if (found != null) {
/* 574*/        found = null;
/*   0*/        break;
/*   0*/      } 
/* 577*/      found = ctor;
/* 578*/      foundProps = properties;
/*   0*/    } 
/* 581*/    if (found != null) {
/* 582*/      creators.addPropertyCreator(found, false, foundProps);
/* 583*/      BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
/* 585*/      for (SettableBeanProperty prop : foundProps) {
/* 586*/        PropertyName pn = prop.getFullName();
/* 587*/        if (!bbd.hasProperty(pn)) {
/* 588*/          BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
/* 590*/          bbd.addProperty(newDef);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
/* 599*/    JsonCreator.Mode mode = intr.findCreatorBinding(creator);
/* 601*/    if (mode == JsonCreator.Mode.PROPERTIES) {
/* 602*/        return true; 
/*   0*/       }
/* 604*/    if (mode == JsonCreator.Mode.DELEGATING) {
/* 605*/        return false; 
/*   0*/       }
/* 608*/    if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValueId(creator.getParameter(0)) != null) {
/* 610*/        return true; 
/*   0*/       }
/* 612*/    if (propDef != null) {
/* 615*/      String implName = propDef.getName();
/* 616*/      if (implName != null && !implName.isEmpty() && 
/* 617*/        propDef.couldSerialize()) {
/* 618*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 623*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentConstructor(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible) throws JsonMappingException {
/* 633*/    Class<?> type = ctor.getRawParameterType(0);
/* 634*/    if (type == String.class || type == CharSequence.class) {
/* 635*/      if (isCreator || isVisible) {
/* 636*/          creators.addStringCreator(ctor, isCreator); 
/*   0*/         }
/* 638*/      return true;
/*   0*/    } 
/* 640*/    if (type == int.class || type == Integer.class) {
/* 641*/      if (isCreator || isVisible) {
/* 642*/          creators.addIntCreator(ctor, isCreator); 
/*   0*/         }
/* 644*/      return true;
/*   0*/    } 
/* 646*/    if (type == long.class || type == Long.class) {
/* 647*/      if (isCreator || isVisible) {
/* 648*/          creators.addLongCreator(ctor, isCreator); 
/*   0*/         }
/* 650*/      return true;
/*   0*/    } 
/* 652*/    if (type == double.class || type == Double.class) {
/* 653*/      if (isCreator || isVisible) {
/* 654*/          creators.addDoubleCreator(ctor, isCreator); 
/*   0*/         }
/* 656*/      return true;
/*   0*/    } 
/* 658*/    if (type == boolean.class || type == Boolean.class) {
/* 659*/      if (isCreator || isVisible) {
/* 660*/          creators.addBooleanCreator(ctor, isCreator); 
/*   0*/         }
/* 662*/      return true;
/*   0*/    } 
/* 665*/    if (isCreator) {
/* 666*/      creators.addDelegatingCreator(ctor, isCreator, null);
/* 667*/      return true;
/*   0*/    } 
/* 669*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
/* 678*/    DeserializationConfig config = ctxt.getConfig();
/* 679*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 680*/      boolean isCreator = intr.hasCreatorAnnotation(factory);
/* 681*/      int argCount = factory.getParameterCount();
/* 683*/      if (argCount == 0) {
/* 684*/        if (isCreator) {
/* 685*/            creators.setDefaultCreator(factory); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 690*/      BeanPropertyDefinition[] propDefs = creatorParams.get(factory);
/* 692*/      if (argCount == 1) {
/* 693*/        BeanPropertyDefinition argDef = (propDefs == null) ? null : propDefs[0];
/* 694*/        boolean useProps = _checkIfCreatorPropertyBased(intr, factory, argDef);
/* 695*/        if (!useProps) {
/* 696*/          _handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
/* 700*/          if (argDef != null) {
/* 701*/              ((POJOPropertyBuilder)argDef).removeConstructors(); 
/*   0*/             }
/*   0*/          continue;
/*   0*/        } 
/* 708*/      } else if (!isCreator) {
/*   0*/        continue;
/*   0*/      } 
/* 713*/      AnnotatedParameter nonAnnotatedParam = null;
/* 714*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 715*/      int implicitNameCount = 0;
/* 716*/      int explicitNameCount = 0;
/* 717*/      int injectCount = 0;
/* 719*/      for (int i = 0; i < argCount; i++) {
/* 720*/        AnnotatedParameter param = factory.getParameter(i);
/* 721*/        BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
/* 722*/        Object injectId = intr.findInjectableValueId(param);
/* 723*/        PropertyName name = (propDef == null) ? null : propDef.getFullName();
/* 725*/        if (propDef != null && propDef.isExplicitlyNamed()) {
/* 726*/          explicitNameCount++;
/* 727*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 730*/        } else if (injectId != null) {
/* 731*/          injectCount++;
/* 732*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 735*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 736*/          if (unwrapper != null) {
/* 737*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/* 738*/            implicitNameCount++;
/* 742*/          } else if (isCreator && 
/* 743*/            name != null && !name.isEmpty()) {
/* 744*/            implicitNameCount++;
/* 745*/            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 761*/          } else if (nonAnnotatedParam == null) {
/* 762*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 765*/      int namedCount = explicitNameCount + implicitNameCount;
/* 768*/      if (isCreator || explicitNameCount > 0 || injectCount > 0) {
/* 770*/        if (namedCount + injectCount == argCount) {
/* 771*/          creators.addPropertyCreator(factory, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 772*/        if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/* 774*/          creators.addDelegatingCreator(factory, isCreator, properties);
/*   0*/          continue;
/*   0*/        } 
/* 776*/        throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator) throws JsonMappingException {
/* 789*/    Class<?> type = factory.getRawParameterType(0);
/* 791*/    if (type == String.class || type == CharSequence.class) {
/* 792*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 793*/          creators.addStringCreator(factory, isCreator); 
/*   0*/         }
/* 795*/      return true;
/*   0*/    } 
/* 797*/    if (type == int.class || type == Integer.class) {
/* 798*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 799*/          creators.addIntCreator(factory, isCreator); 
/*   0*/         }
/* 801*/      return true;
/*   0*/    } 
/* 803*/    if (type == long.class || type == Long.class) {
/* 804*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 805*/          creators.addLongCreator(factory, isCreator); 
/*   0*/         }
/* 807*/      return true;
/*   0*/    } 
/* 809*/    if (type == double.class || type == Double.class) {
/* 810*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 811*/          creators.addDoubleCreator(factory, isCreator); 
/*   0*/         }
/* 813*/      return true;
/*   0*/    } 
/* 815*/    if (type == boolean.class || type == Boolean.class) {
/* 816*/      if (isCreator || vchecker.isCreatorVisible(factory)) {
/* 817*/          creators.addBooleanCreator(factory, isCreator); 
/*   0*/         }
/* 819*/      return true;
/*   0*/    } 
/* 821*/    if (isCreator) {
/* 822*/      creators.addDelegatingCreator(factory, isCreator, null);
/* 823*/      return true;
/*   0*/    } 
/* 825*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, Object injectableValueId) throws JsonMappingException {
/*   0*/    PropertyMetadata metadata;
/* 839*/    DeserializationConfig config = ctxt.getConfig();
/* 840*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 843*/    if (intr == null) {
/* 844*/      metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
/*   0*/    } else {
/* 846*/      Boolean b = intr.hasRequiredMarker(param);
/* 847*/      String desc = intr.findPropertyDescription(param);
/* 848*/      Integer idx = intr.findPropertyIndex(param);
/* 849*/      String def = intr.findPropertyDefaultValue(param);
/* 850*/      metadata = PropertyMetadata.construct(b, desc, idx, def);
/*   0*/    } 
/* 853*/    JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
/* 854*/    BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), beanDesc.getClassAnnotations(), param, metadata);
/* 858*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/* 860*/    if (typeDeser == null) {
/* 861*/        typeDeser = findTypeDeserializer(config, type); 
/*   0*/       }
/* 865*/    SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
/* 868*/    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
/* 869*/    if (deser == null) {
/* 870*/        deser = type.<JsonDeserializer>getValueHandler(); 
/*   0*/       }
/* 872*/    if (deser != null) {
/* 874*/      deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/* 875*/      prop = prop.withValueDeserializer(deser);
/*   0*/    } 
/* 877*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  protected PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 882*/    if (param != null && intr != null) {
/* 883*/      PropertyName name = intr.findNameForDeserialization(param);
/* 884*/      if (name != null) {
/* 885*/          return name; 
/*   0*/         }
/* 890*/      String str = intr.findImplicitPropertyName(param);
/* 891*/      if (str != null && !str.isEmpty()) {
/* 892*/          return PropertyName.construct(str); 
/*   0*/         }
/*   0*/    } 
/* 895*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected PropertyName _findImplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 900*/    String str = intr.findImplicitPropertyName(param);
/* 901*/    if (str != null && !str.isEmpty()) {
/* 902*/        return PropertyName.construct(str); 
/*   0*/       }
/* 904*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected PropertyName _findExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 910*/    if (param != null && intr != null) {
/* 911*/        return intr.findNameForDeserialization(param); 
/*   0*/       }
/* 913*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected boolean _hasExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/* 919*/    if (param != null && intr != null) {
/* 920*/      PropertyName n = intr.findNameForDeserialization(param);
/* 921*/      return (n != null && n.hasSimpleName());
/*   0*/    } 
/* 923*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
/* 937*/    DeserializationConfig config = ctxt.getConfig();
/* 938*/    JavaType elemType = type.getContentType();
/* 941*/    JsonDeserializer<Object> contentDeser = elemType.<JsonDeserializer<Object>>getValueHandler();
/* 943*/    TypeDeserializer elemTypeDeser = elemType.<TypeDeserializer>getTypeHandler();
/* 945*/    if (elemTypeDeser == null) {
/* 946*/        elemTypeDeser = findTypeDeserializer(config, elemType); 
/*   0*/       }
/* 949*/    JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
/* 951*/    if (deser == null) {
/* 952*/      if (contentDeser == null) {
/* 953*/        Class<?> raw = elemType.getRawClass();
/* 954*/        if (elemType.isPrimitive()) {
/* 955*/            return PrimitiveArrayDeserializers.forType(raw); 
/*   0*/           }
/* 956*/        if (raw == String.class) {
/* 957*/            return StringArrayDeserializer.instance; 
/*   0*/           }
/*   0*/      } 
/* 960*/      deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
/*   0*/    } 
/* 963*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 964*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 965*/            deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/* 968*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
/* 982*/    JavaType contentType = type.getContentType();
/* 984*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/* 985*/    DeserializationConfig config = ctxt.getConfig();
/* 988*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/* 990*/    if (contentTypeDeser == null) {
/* 991*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/* 994*/    JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/* 996*/    if (deser == null) {
/* 997*/      Class<?> collectionClass = type.getRawClass();
/* 998*/      if (contentDeser == null) {
/*1000*/          if (EnumSet.class.isAssignableFrom(collectionClass)) {
/*1001*/              deser = new EnumSetDeserializer(contentType, null); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/*1015*/    if (deser == null) {
/*1016*/      if (type.isInterface() || type.isAbstract()) {
/*1017*/        CollectionType implType = _mapAbstractCollectionType(type, config);
/*1018*/        if (implType == null) {
/*1020*/          if (type.getTypeHandler() == null) {
/*1021*/              throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type); 
/*   0*/             }
/*1023*/          deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/        } else {
/*1025*/          type = implType;
/*1027*/          beanDesc = config.introspectForCreation(type);
/*   0*/        } 
/*   0*/      } 
/*1030*/      if (deser == null) {
/*1031*/        ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1032*/        if (!inst.canCreateUsingDefault()) {
/*1034*/            if (type.getRawClass() == ArrayBlockingQueue.class) {
/*1035*/                return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst); 
/*   0*/               } 
/*   0*/           }
/*1039*/        if (contentType.getRawClass() == String.class) {
/*1041*/          deser = new StringCollectionDeserializer(type, contentDeser, inst);
/*   0*/        } else {
/*1043*/          deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1048*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1049*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1050*/            deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1053*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
/*1058*/    Class<?> collectionClass = type.getRawClass();
/*1059*/    collectionClass = _collectionFallbacks.get(collectionClass.getName());
/*1060*/    if (collectionClass == null) {
/*1061*/        return null; 
/*   0*/       }
/*1063*/    return (CollectionType)config.constructSpecializedType(type, collectionClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1072*/    JavaType contentType = type.getContentType();
/*1074*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1075*/    DeserializationConfig config = ctxt.getConfig();
/*1078*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1080*/    if (contentTypeDeser == null) {
/*1081*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1083*/    JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1085*/    if (deser != null) {
/*1087*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1088*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1089*/                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1093*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
/*1107*/    DeserializationConfig config = ctxt.getConfig();
/*1108*/    JavaType keyType = type.getKeyType();
/*1109*/    JavaType contentType = type.getContentType();
/*1113*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1116*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1118*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1120*/    if (contentTypeDeser == null) {
/*1121*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1125*/    JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1128*/    if (deser == null) {
/*1130*/      Class<?> mapClass = type.getRawClass();
/*1131*/      if (EnumMap.class.isAssignableFrom(mapClass)) {
/*1132*/        Class<?> kt = keyType.getRawClass();
/*1133*/        if (kt == null || !kt.isEnum()) {
/*1134*/            throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available"); 
/*   0*/           }
/*1136*/        deser = new EnumMapDeserializer(type, null, contentDeser, contentTypeDeser);
/*   0*/      } 
/*1150*/      if (deser == null) {
/*1151*/        if (type.isInterface() || type.isAbstract()) {
/*1153*/          Class<? extends Map> fallback = _mapFallbacks.get(mapClass.getName());
/*1154*/          if (fallback != null) {
/*1155*/            mapClass = fallback;
/*1156*/            type = (MapType)config.constructSpecializedType(type, mapClass);
/*1158*/            beanDesc = config.introspectForCreation(type);
/*   0*/          } else {
/*1161*/            if (type.getTypeHandler() == null) {
/*1162*/                throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type); 
/*   0*/               }
/*1164*/            deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/          } 
/*   0*/        } 
/*1167*/        if (deser == null) {
/*1168*/          ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1173*/          MapDeserializer<?> md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
/*1174*/          JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
/*1176*/          Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForDeserialization();
/*1178*/          md.setIgnorableProperties(ignored);
/*1179*/          deser = md;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1183*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1184*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1185*/            deser = mod.modifyMapDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1188*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1197*/    JavaType keyType = type.getKeyType();
/*1198*/    JavaType contentType = type.getContentType();
/*1199*/    DeserializationConfig config = ctxt.getConfig();
/*1203*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1206*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1213*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1215*/    if (contentTypeDeser == null) {
/*1216*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1218*/    JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1220*/    if (deser != null) {
/*1222*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1223*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1224*/                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1228*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1245*/    DeserializationConfig config = ctxt.getConfig();
/*1246*/    Class<?> enumClass = type.getRawClass();
/*1248*/    JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1250*/    if (deser == null) {
/*1251*/      ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/*1252*/      SettableBeanProperty[] creatorProps = (valueInstantiator == null) ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*1255*/      for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1256*/        if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
/*1257*/          if (factory.getParameterCount() == 0) {
/*1258*/            deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
/*   0*/            break;
/*   0*/          } 
/*1261*/          Class<?> returnType = factory.getRawReturnType();
/*1263*/          if (returnType.isAssignableFrom(enumClass)) {
/*1264*/            deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1271*/      if (deser == null) {
/*1272*/          deser = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod())); 
/*   0*/         }
/*   0*/    } 
/*1278*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1279*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1280*/            deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1283*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
/*1292*/    Class<? extends JsonNode> nodeClass = (Class)nodeType.getRawClass();
/*1294*/    JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
/*1296*/    if (custom != null) {
/*1297*/        return custom; 
/*   0*/       }
/*1299*/    return JsonNodeDeserializer.getDeserializer(nodeClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
/*1307*/    JavaType contentType = type.getContentType();
/*1309*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1310*/    DeserializationConfig config = ctxt.getConfig();
/*1312*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1313*/    if (contentTypeDeser == null) {
/*1314*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1316*/    JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1319*/    if (deser == null) {
/*1321*/        if (AtomicReference.class.isAssignableFrom(type.getRawClass())) {
/*1322*/            return new AtomicReferenceDeserializer(type, contentTypeDeser, contentDeser); 
/*   0*/           } 
/*   0*/       }
/*1325*/    if (deser != null) {
/*1327*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1328*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1329*/                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1333*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
/*1347*/    BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/*1348*/    AnnotatedClass ac = bean.getClassInfo();
/*1349*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1350*/    TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
/*1355*/    Collection<NamedType> subtypes = null;
/*1356*/    if (b == null) {
/*1357*/      b = config.getDefaultTyper(baseType);
/*1358*/      if (b == null) {
/*1359*/          return null; 
/*   0*/         }
/*   0*/    } else {
/*1362*/      subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
/*   0*/    } 
/*1366*/    if (b.getDefaultImpl() == null && baseType.isAbstract()) {
/*1367*/      JavaType defaultType = mapAbstractType(config, baseType);
/*1368*/      if (defaultType != null && defaultType.getRawClass() != baseType.getRawClass()) {
/*1369*/          b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass()); 
/*   0*/         }
/*   0*/    } 
/*1372*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1384*/    return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
/*   0*/  }
/*   0*/  
/*   0*/  public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1398*/    DeserializationConfig config = ctxt.getConfig();
/*1399*/    KeyDeserializer deser = null;
/*1400*/    if (this._factoryConfig.hasKeyDeserializers()) {
/*1401*/      BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
/*1402*/      for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
/*1403*/        deser = d.findKeyDeserializer(type, config, beanDesc);
/*1404*/        if (deser != null) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1410*/    if (deser == null) {
/*1411*/        if (type.isEnumType()) {
/*1412*/          deser = _createEnumKeyDeserializer(ctxt, type);
/*   0*/        } else {
/*1414*/          deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
/*   0*/        }  
/*   0*/       }
/*1418*/    if (deser != null && 
/*1419*/      this._factoryConfig.hasDeserializerModifiers()) {
/*1420*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1421*/            deser = mod.modifyKeyDeserializer(config, type, deser); 
/*   0*/           } 
/*   0*/       }
/*1425*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1432*/    DeserializationConfig config = ctxt.getConfig();
/*1433*/    Class<?> enumClass = type.getRawClass();
/*1435*/    BeanDescription beanDesc = config.introspect(type);
/*1437*/    KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1438*/    if (des != null) {
/*1439*/        return des; 
/*   0*/       }
/*1442*/    JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1443*/    if (custom != null) {
/*1444*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom); 
/*   0*/       }
/*1446*/    JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1447*/    if (valueDesForKey != null) {
/*1448*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey); 
/*   0*/       }
/*1451*/    EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod());
/*1453*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1454*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1455*/      if (ai.hasCreatorAnnotation(factory)) {
/*1456*/        int argCount = factory.getParameterCount();
/*1457*/        if (argCount == 1) {
/*1458*/          Class<?> returnType = factory.getRawReturnType();
/*1460*/          if (returnType.isAssignableFrom(enumClass)) {
/*1462*/            if (factory.getRawParameterType(0) != String.class) {
/*1463*/                throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String"); 
/*   0*/               }
/*1465*/            if (config.canOverrideAccessModifiers()) {
/*1466*/                ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/               }
/*1469*/            return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
/*   0*/          } 
/*   0*/        } 
/*1472*/        throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*   0*/      } 
/*   0*/    } 
/*1477*/    return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
/*1503*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1504*/    TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
/*1506*/    if (b == null) {
/*1507*/        return findTypeDeserializer(config, baseType); 
/*   0*/       }
/*1510*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
/*1512*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
/*1530*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1531*/    TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
/*1532*/    JavaType contentType = containerType.getContentType();
/*1534*/    if (b == null) {
/*1535*/        return findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1538*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
/*1540*/    return b.buildTypeDeserializer(config, contentType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1554*/    Class<?> rawType = type.getRawClass();
/*1556*/    if (rawType == CLASS_OBJECT) {
/*   0*/      JavaType lt, mt;
/*1558*/      DeserializationConfig config = ctxt.getConfig();
/*1561*/      if (this._factoryConfig.hasAbstractTypeResolvers()) {
/*1562*/        lt = _findRemappedType(config, List.class);
/*1563*/        mt = _findRemappedType(config, Map.class);
/*   0*/      } else {
/*1565*/        lt = mt = null;
/*   0*/      } 
/*1567*/      return new UntypedObjectDeserializer(lt, mt);
/*   0*/    } 
/*1569*/    if (rawType == CLASS_STRING || rawType == CLASS_CHAR_BUFFER) {
/*1570*/        return StringDeserializer.instance; 
/*   0*/       }
/*1572*/    if (rawType == CLASS_ITERABLE) {
/*1574*/      TypeFactory tf = ctxt.getTypeFactory();
/*1575*/      JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
/*1576*/      JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
/*1577*/      CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
/*1579*/      return createCollectionDeserializer(ctxt, ct, beanDesc);
/*   0*/    } 
/*1581*/    if (rawType == CLASS_MAP_ENTRY) {
/*1583*/      JavaType kt = type.containedType(0);
/*1584*/      if (kt == null) {
/*1585*/          kt = TypeFactory.unknownType(); 
/*   0*/         }
/*1587*/      JavaType vt = type.containedType(1);
/*1588*/      if (vt == null) {
/*1589*/          vt = TypeFactory.unknownType(); 
/*   0*/         }
/*1591*/      TypeDeserializer vts = vt.<TypeDeserializer>getTypeHandler();
/*1592*/      if (vts == null) {
/*1593*/          vts = findTypeDeserializer(ctxt.getConfig(), vt); 
/*   0*/         }
/*1595*/      JsonDeserializer<Object> valueDeser = vt.<JsonDeserializer<Object>>getValueHandler();
/*1596*/      KeyDeserializer keyDes = kt.<KeyDeserializer>getValueHandler();
/*1597*/      return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
/*   0*/    } 
/*1599*/    String clsName = rawType.getName();
/*1600*/    if (rawType.isPrimitive() || clsName.startsWith("java.")) {
/*1602*/      JsonDeserializer<?> jsonDeserializer = NumberDeserializers.find(rawType, clsName);
/*1603*/      if (jsonDeserializer == null) {
/*1604*/          jsonDeserializer = DateDeserializers.find(rawType, clsName); 
/*   0*/         }
/*1606*/      if (jsonDeserializer != null) {
/*1607*/          return jsonDeserializer; 
/*   0*/         }
/*   0*/    } 
/*1611*/    if (rawType == TokenBuffer.class) {
/*1612*/        return new TokenBufferDeserializer(); 
/*   0*/       }
/*1614*/    JsonDeserializer<?> deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
/*1615*/    if (deser != null) {
/*1616*/        return deser; 
/*   0*/       }
/*1618*/    return JdkDeserializers.find(rawType, clsName);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
/*1622*/    JavaType type = mapAbstractType(config, config.constructType(rawType));
/*1623*/    return (type == null || type.hasRawClass(rawType)) ? null : type;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1636*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1637*/      JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
/*1638*/      if (deser != null) {
/*1639*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1642*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
/*1650*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1651*/      JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
/*1653*/      if (deser != null) {
/*1654*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1657*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1665*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1666*/      JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
/*1667*/      if (deser != null) {
/*1668*/          return (JsonDeserializer)deser; 
/*   0*/         }
/*   0*/    } 
/*1671*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1679*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1680*/      JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1682*/      if (deser != null) {
/*1683*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1686*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1694*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1695*/      JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1697*/      if (deser != null) {
/*1698*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1701*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1709*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1710*/      JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1712*/      if (deser != null) {
/*1713*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1716*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1723*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1724*/      JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
/*1725*/      if (deser != null) {
/*1726*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1729*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1738*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1739*/      JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1741*/      if (deser != null) {
/*1742*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1745*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1754*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1755*/      JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1757*/      if (deser != null) {
/*1758*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1761*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*1782*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1783*/    if (intr != null) {
/*1784*/      Object deserDef = intr.findDeserializer(ann);
/*1785*/      if (deserDef != null) {
/*1786*/          return ctxt.deserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*1789*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*1801*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1802*/    if (intr != null) {
/*1803*/      Object deserDef = intr.findKeyDeserializer(ann);
/*1804*/      if (deserDef != null) {
/*1805*/          return ctxt.keyDeserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*1808*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
/*1824*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1825*/    if (intr == null) {
/*1826*/        return type; 
/*   0*/       }
/*1832*/    if (type.isMapLikeType()) {
/*1833*/      JavaType keyType = type.getKeyType();
/*1834*/      if (keyType != null) {
/*1835*/        Object kdDef = intr.findKeyDeserializer(member);
/*1836*/        KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
/*1837*/        if (kd != null) {
/*1838*/          type = ((MapLikeType)type).withKeyValueHandler(kd);
/*1839*/          keyType = type.getKeyType();
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1844*/    if (type.hasContentType()) {
/*1845*/      Object cdDef = intr.findContentDeserializer(member);
/*1846*/      JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
/*1847*/      if (cd != null) {
/*1848*/          type = type.withContentValueHandler(cd); 
/*   0*/         }
/*1850*/      TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
/*1852*/      if (contentTypeDeser != null) {
/*1853*/          type = type.withContentTypeHandler(contentTypeDeser); 
/*   0*/         }
/*   0*/    } 
/*1856*/    TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
/*1858*/    if (valueTypeDeser != null) {
/*1859*/        type = type.withTypeHandler(valueTypeDeser); 
/*   0*/       }
/*1867*/    type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
/*1868*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMethod jsonValueMethod) {
/*1874*/    if (jsonValueMethod != null) {
/*1875*/      Method accessor = jsonValueMethod.getAnnotated();
/*1876*/      if (config.canOverrideAccessModifiers()) {
/*1877*/          ClassUtil.checkAndFixAccess(accessor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/         }
/*1879*/      return EnumResolver.constructUnsafeUsingMethod(enumClass, accessor, config.getAnnotationIntrospector());
/*   0*/    } 
/*1883*/    return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
/*1903*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1904*/    if (intr == null) {
/*1905*/        return type; 
/*   0*/       }
/*1937*/    return intr.refineDeserializationType(ctxt.getConfig(), a, type);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
/*1948*/    return resolveMemberAndTypeAnnotations(ctxt, member, type);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
/*1957*/    if (enumType == null) {
/*1958*/        return null; 
/*   0*/       }
/*1960*/    BeanDescription beanDesc = config.introspect(enumType);
/*1961*/    return beanDesc.findJsonValueMethod();
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
/*   0*/}
