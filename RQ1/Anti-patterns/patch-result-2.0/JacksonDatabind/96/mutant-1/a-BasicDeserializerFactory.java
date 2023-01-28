/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JacksonInject;
/*   0*/import com.fasterxml.jackson.annotation.JsonCreator;
/*   0*/import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
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
/*   0*/import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers;
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
/*   0*/import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
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
/*   0*/import com.fasterxml.jackson.databind.util.ConstantValueInstantiator;
/*   0*/import com.fasterxml.jackson.databind.util.EnumResolver;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.Serializable;
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
/*  44*/  private static final Class<?> CLASS_OBJECT = Object.class;
/*   0*/  
/*  45*/  private static final Class<?> CLASS_STRING = String.class;
/*   0*/  
/*  46*/  private static final Class<?> CLASS_CHAR_SEQUENCE = CharSequence.class;
/*   0*/  
/*  47*/  private static final Class<?> CLASS_ITERABLE = Iterable.class;
/*   0*/  
/*  48*/  private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
/*   0*/  
/*  54*/  protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
/*   0*/  
/*  61*/  static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap<>();
/*   0*/  
/*   0*/  static {
/*  64*/    _mapFallbacks.put(Map.class.getName(), LinkedHashMap.class);
/*  65*/    _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
/*  66*/    _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
/*  68*/    _mapFallbacks.put(NavigableMap.class.getName(), TreeMap.class);
/*  69*/    _mapFallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
/*   0*/  }
/*   0*/  
/*  78*/  static final HashMap<String, Class<? extends Collection>> _collectionFallbacks = new HashMap<>();
/*   0*/  
/*   0*/  protected final DeserializerFactoryConfig _factoryConfig;
/*   0*/  
/*   0*/  static {
/*  81*/    _collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
/*  82*/    _collectionFallbacks.put(List.class.getName(), ArrayList.class);
/*  83*/    _collectionFallbacks.put(Set.class.getName(), HashSet.class);
/*  84*/    _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
/*  85*/    _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
/*  92*/    _collectionFallbacks.put("java.util.Deque", LinkedList.class);
/*  93*/    _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
/*   0*/  }
/*   0*/  
/*   0*/  protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
/* 115*/    this._factoryConfig = config;
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializerFactoryConfig getFactoryConfig() {
/* 126*/    return this._factoryConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
/* 143*/    return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
/* 152*/    return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
/* 161*/    return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
/* 170*/    return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
/*   0*/  }
/*   0*/  
/*   0*/  public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
/* 179*/    return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
/*   0*/    while (true) {
/* 193*/      JavaType next = _mapAbstractType2(config, type);
/* 194*/      if (next == null) {
/* 195*/          return type; 
/*   0*/         }
/* 199*/      Class<?> prevCls = type.getRawClass();
/* 200*/      Class<?> nextCls = next.getRawClass();
/* 201*/      if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
/* 202*/          throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former"); 
/*   0*/         }
/* 204*/      type = next;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
/* 215*/    Class<?> currClass = type.getRawClass();
/* 216*/    if (this._factoryConfig.hasAbstractTypeResolvers()) {
/* 217*/        for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
/* 218*/          JavaType concrete = resolver.findTypeMapping(config, type);
/* 219*/          if (concrete != null && !concrete.hasRawClass(currClass)) {
/* 220*/              return concrete; 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 224*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 243*/    DeserializationConfig config = ctxt.getConfig();
/* 245*/    ValueInstantiator instantiator = null;
/* 247*/    AnnotatedClass ac = beanDesc.getClassInfo();
/* 248*/    Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
/* 249*/    if (instDef != null) {
/* 250*/        instantiator = _valueInstantiatorInstance(config, ac, instDef); 
/*   0*/       }
/* 252*/    if (instantiator == null) {
/* 255*/      instantiator = _findStdValueInstantiator(config, beanDesc);
/* 256*/      if (instantiator == null) {
/* 257*/          instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc); 
/*   0*/         }
/*   0*/    } 
/* 262*/    if (this._factoryConfig.hasValueInstantiators()) {
/* 263*/        for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
/* 264*/          instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
/* 266*/          if (instantiator == null) {
/* 267*/              ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", new Object[] { insts.getClass().getName() }); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 275*/    if (instantiator.getIncompleteParameter() != null) {
/* 276*/      AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
/* 277*/      AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
/* 278*/      throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*   0*/    } 
/* 282*/    return instantiator;
/*   0*/  }
/*   0*/  
/*   0*/  private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/* 289*/    Class<?> raw = beanDesc.getBeanClass();
/* 290*/    if (raw == JsonLocation.class) {
/* 291*/        return new JsonLocationInstantiator(); 
/*   0*/       }
/* 294*/    if (Collection.class.isAssignableFrom(raw)) {
/* 295*/      if (Collections.EMPTY_SET.getClass() == raw) {
/* 296*/          return new ConstantValueInstantiator(Collections.EMPTY_SET); 
/*   0*/         }
/* 298*/      if (Collections.EMPTY_LIST.getClass() == raw) {
/* 299*/          return new ConstantValueInstantiator(Collections.EMPTY_LIST); 
/*   0*/         }
/* 301*/    } else if (Map.class.isAssignableFrom(raw) && 
/* 302*/      Collections.EMPTY_MAP.getClass() == raw) {
/* 303*/      return new ConstantValueInstantiator(Collections.EMPTY_MAP);
/*   0*/    } 
/* 306*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 317*/    CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
/* 318*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 321*/    DeserializationConfig config = ctxt.getConfig();
/* 322*/    VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
/* 333*/    Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
/* 337*/    _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
/* 339*/    if (beanDesc.getType().isConcrete()) {
/* 340*/        _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs); 
/*   0*/       }
/* 342*/    return creators.constructValueInstantiator(ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
/* 348*/    Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = (Map)Collections.emptyMap();
/* 349*/    for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
/* 350*/      Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
/* 351*/      while (it.hasNext()) {
/* 352*/        AnnotatedParameter param = it.next();
/* 353*/        AnnotatedWithParams owner = param.getOwner();
/* 354*/        BeanPropertyDefinition[] defs = result.get(owner);
/* 355*/        int index = param.getIndex();
/* 357*/        if (defs == null) {
/* 358*/          if (result.isEmpty()) {
/* 359*/              result = (Map)new LinkedHashMap<>(); 
/*   0*/             }
/* 361*/          defs = new BeanPropertyDefinition[owner.getParameterCount()];
/* 362*/          result.put(owner, defs);
/* 364*/        } else if (defs[index] != null) {
/* 365*/          ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", new Object[] { index, owner, defs[index], propDef });
/*   0*/        } 
/* 370*/        defs[index] = propDef;
/*   0*/      } 
/*   0*/    } 
/* 373*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
/* 380*/    if (instDef == null) {
/* 381*/        return null; 
/*   0*/       }
/* 386*/    if (instDef instanceof ValueInstantiator) {
/* 387*/        return (ValueInstantiator)instDef; 
/*   0*/       }
/* 389*/    if (!(instDef instanceof Class)) {
/* 390*/        throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead"); 
/*   0*/       }
/* 394*/    Class<?> instClass = (Class)instDef;
/* 395*/    if (ClassUtil.isBogusClass(instClass)) {
/* 396*/        return null; 
/*   0*/       }
/* 398*/    if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
/* 399*/        throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>"); 
/*   0*/       }
/* 402*/    HandlerInstantiator hi = config.getHandlerInstantiator();
/* 403*/    if (hi != null) {
/* 404*/      ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
/* 405*/      if (inst != null) {
/* 406*/          return inst; 
/*   0*/         }
/*   0*/    } 
/* 409*/    return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
/* 428*/    boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
/* 429*/    if (isNonStaticInnerClass) {
/*   0*/        return; 
/*   0*/       }
/* 437*/    AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
/* 438*/    if (defaultCtor != null && (
/* 439*/      !creators.hasDefaultCreator() || _hasCreatorAnnotation(ctxt, defaultCtor))) {
/* 440*/        creators.setDefaultCreator(defaultCtor); 
/*   0*/       }
/* 444*/    List<CreatorCandidate> nonAnnotated = new LinkedList<>();
/* 445*/    int explCount = 0;
/* 446*/    for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
/* 447*/      JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
/* 448*/      if (JsonCreator.Mode.DISABLED == creatorMode) {
/*   0*/          continue; 
/*   0*/         }
/* 451*/      if (creatorMode == null) {
/* 453*/        if (vchecker.isCreatorVisible(ctor)) {
/* 454*/            nonAnnotated.add(CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor))); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 458*/      switch (creatorMode) {
/*   0*/        case DELEGATING:
/* 460*/          _addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, null));
/*   0*/          break;
/*   0*/        case PROPERTIES:
/* 464*/          _addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
/*   0*/          break;
/*   0*/        default:
/* 468*/          _addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
/*   0*/          break;
/*   0*/      } 
/* 472*/      explCount++;
/*   0*/    } 
/* 475*/    if (explCount > 0) {
/*   0*/        return; 
/*   0*/       }
/* 478*/    List<AnnotatedWithParams> implicitCtors = null;
/* 479*/    for (CreatorCandidate candidate : nonAnnotated) {
/* 480*/      int argCount = candidate.paramCount();
/* 481*/      AnnotatedWithParams ctor = candidate.creator();
/* 484*/      if (argCount == 1) {
/* 485*/        BeanPropertyDefinition propDef = candidate.propertyDef(0);
/* 486*/        boolean useProps = _checkIfCreatorPropertyBased(intr, ctor, propDef);
/* 488*/        if (useProps) {
/* 489*/          SettableBeanProperty[] arrayOfSettableBeanProperty = new SettableBeanProperty[1];
/* 490*/          PropertyName name = candidate.paramName(0);
/* 491*/          arrayOfSettableBeanProperty[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, candidate.parameter(0), candidate.injection(0));
/* 493*/          creators.addPropertyCreator(ctor, false, arrayOfSettableBeanProperty);
/*   0*/          continue;
/*   0*/        } 
/* 495*/        _handleSingleArgumentCreator(creators, ctor, false, vchecker.isCreatorVisible(ctor));
/* 500*/        if (propDef != null) {
/* 501*/            ((POJOPropertyBuilder)propDef).removeConstructors(); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 512*/      int nonAnnotatedParamIndex = -1;
/* 513*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 514*/      int explicitNameCount = 0;
/* 515*/      int implicitWithCreatorCount = 0;
/* 516*/      int injectCount = 0;
/* 518*/      for (int i = 0; i < argCount; i++) {
/* 519*/        AnnotatedParameter param = ctor.getParameter(i);
/* 520*/        BeanPropertyDefinition propDef = candidate.propertyDef(i);
/* 521*/        JacksonInject.Value injectId = intr.findInjectableValue(param);
/* 522*/        PropertyName name = (propDef == null) ? null : propDef.getFullName();
/* 524*/        if (propDef != null && propDef.isExplicitlyNamed()) {
/* 525*/          explicitNameCount++;
/* 526*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/* 529*/        } else if (injectId != null) {
/* 530*/          injectCount++;
/* 531*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/        } else {
/* 534*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 535*/          if (unwrapper != null) {
/* 536*/            _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
/* 551*/          } else if (nonAnnotatedParamIndex < 0) {
/* 552*/            nonAnnotatedParamIndex = i;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 556*/      int namedCount = explicitNameCount + implicitWithCreatorCount;
/* 558*/      if (explicitNameCount > 0 || injectCount > 0) {
/* 560*/        if (namedCount + injectCount == argCount) {
/* 561*/          creators.addPropertyCreator(ctor, false, properties);
/*   0*/          continue;
/*   0*/        } 
/* 564*/        if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/* 566*/          creators.addDelegatingCreator(ctor, false, properties, 0);
/*   0*/          continue;
/*   0*/        } 
/* 572*/        PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
/* 573*/        if (impl == null || impl.isEmpty()) {
/* 582*/            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", new Object[] { nonAnnotatedParamIndex, ctor }); 
/*   0*/           }
/*   0*/      } 
/* 588*/      if (!creators.hasDefaultCreator()) {
/* 589*/        if (implicitCtors == null) {
/* 590*/            implicitCtors = new LinkedList<>(); 
/*   0*/           }
/* 592*/        implicitCtors.add(ctor);
/*   0*/      } 
/*   0*/    } 
/* 597*/    if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
/* 599*/        _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addExplicitDelegatingCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
/* 617*/    int ix = -1;
/* 618*/    int argCount = candidate.paramCount();
/* 619*/    SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 620*/    for (int i = 0; i < argCount; i++) {
/* 621*/      AnnotatedParameter param = candidate.parameter(i);
/* 622*/      JacksonInject.Value injectId = candidate.injection(i);
/* 623*/      if (injectId != null) {
/* 624*/        properties[i] = constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);
/* 627*/      } else if (ix < 0) {
/* 628*/        ix = i;
/*   0*/      } else {
/* 632*/        ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", new Object[] { ix, i, candidate });
/*   0*/      } 
/*   0*/    } 
/* 637*/    if (ix < 0) {
/* 638*/        ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", new Object[] { candidate }); 
/*   0*/       }
/* 643*/    if (argCount == 1) {
/* 644*/      _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
/* 647*/      BeanPropertyDefinition paramDef = candidate.propertyDef(0);
/* 648*/      if (paramDef != null) {
/* 649*/          ((POJOPropertyBuilder)paramDef).removeConstructors(); 
/*   0*/         }
/*   0*/      return;
/*   0*/    } 
/* 653*/    creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addExplicitPropertyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
/* 666*/    int paramCount = candidate.paramCount();
/* 667*/    SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
/* 669*/    for (int i = 0; i < paramCount; i++) {
/* 670*/      JacksonInject.Value injectId = candidate.injection(i);
/* 671*/      AnnotatedParameter param = candidate.parameter(i);
/* 672*/      PropertyName name = candidate.paramName(i);
/* 673*/      if (name == null) {
/* 676*/        NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
/* 677*/        if (unwrapper != null) {
/* 678*/            _reportUnwrappedCreatorProperty(ctxt, beanDesc, param); 
/*   0*/           }
/* 684*/        name = candidate.findImplicitParamName(i);
/* 686*/        if (name == null && injectId == null) {
/* 687*/            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d has no property name, is not Injectable: can not use as Creator %s", new Object[] { i, candidate }); 
/*   0*/           }
/*   0*/      } 
/* 691*/      properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*   0*/    } 
/* 693*/    creators.addPropertyCreator(candidate.creator(), true, properties);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
/* 707*/    if (1 != candidate.paramCount()) {
/* 710*/      int oneNotInjected = candidate.findOnlyParamWithoutInjection();
/* 711*/      if (oneNotInjected >= 0) {
/* 713*/          if (candidate.paramName(oneNotInjected) == null) {
/* 714*/            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
/*   0*/            return;
/*   0*/          }  
/*   0*/         }
/* 718*/      _addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
/*   0*/      return;
/*   0*/    } 
/* 721*/    AnnotatedParameter param = candidate.parameter(0);
/* 722*/    JacksonInject.Value injectId = candidate.injection(0);
/* 723*/    PropertyName paramName = candidate.explicitParamName(0);
/* 724*/    BeanPropertyDefinition paramDef = candidate.propertyDef(0);
/* 727*/    boolean useProps = (paramName != null || injectId != null);
/* 728*/    if (!useProps && paramDef != null) {
/* 735*/      paramName = candidate.findImplicitParamName(0);
/* 736*/      useProps = (paramName != null && paramDef.couldSerialize());
/*   0*/    } 
/* 738*/    if (useProps) {
/* 739*/      SettableBeanProperty[] properties = { constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId) };
/* 742*/      creators.addPropertyCreator(candidate.creator(), true, properties);
/*   0*/      return;
/*   0*/    } 
/* 745*/    _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
/* 749*/    if (paramDef != null) {
/* 750*/        ((POJOPropertyBuilder)paramDef).removeConstructors(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
/* 758*/    if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValue(creator.getParameter(0)) != null) {
/* 760*/        return true; 
/*   0*/       }
/* 762*/    if (propDef != null) {
/* 765*/      String implName = propDef.getName();
/* 766*/      if (implName != null && !implName.isEmpty() && 
/* 767*/        propDef.couldSerialize()) {
/* 768*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 773*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedWithParams> implicitCtors) throws JsonMappingException {
/* 781*/    AnnotatedWithParams found = null;
/* 782*/    SettableBeanProperty[] foundProps = null;
/* 789*/    label33: for (AnnotatedWithParams ctor : implicitCtors) {
/* 790*/      if (!vchecker.isCreatorVisible(ctor)) {
/*   0*/          continue; 
/*   0*/         }
/* 794*/      int argCount = ctor.getParameterCount();
/* 795*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 796*/      for (int i = 0; i < argCount; ) {
/* 797*/        AnnotatedParameter param = ctor.getParameter(i);
/* 798*/        PropertyName name = _findParamName(param, intr);
/* 801*/        if (name != null) {
/* 801*/          if (name.isEmpty()) {
/*   0*/              continue label33; 
/*   0*/             }
/* 804*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
/*   0*/          i++;
/*   0*/        } 
/*   0*/        continue label33;
/*   0*/      } 
/* 807*/      if (found != null) {
/* 808*/        found = null;
/*   0*/        break;
/*   0*/      } 
/* 811*/      found = ctor;
/* 812*/      foundProps = properties;
/*   0*/    } 
/* 815*/    if (found != null) {
/* 816*/      creators.addPropertyCreator(found, false, foundProps);
/* 817*/      BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
/* 819*/      for (SettableBeanProperty prop : foundProps) {
/* 820*/        PropertyName pn = prop.getFullName();
/* 821*/        if (!bbd.hasProperty(pn)) {
/* 822*/          BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
/* 824*/          bbd.addProperty(newDef);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
/* 836*/    List<CreatorCandidate> nonAnnotated = new LinkedList<>();
/* 837*/    int explCount = 0;
/* 840*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 841*/      JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
/* 842*/      int argCount = factory.getParameterCount();
/* 843*/      if (creatorMode == null) {
/* 845*/        if (argCount == 1 && vchecker.isCreatorVisible(factory)) {
/* 846*/            nonAnnotated.add(CreatorCandidate.construct(intr, factory, null)); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 850*/      if (creatorMode == JsonCreator.Mode.DISABLED) {
/*   0*/          continue; 
/*   0*/         }
/* 855*/      if (argCount == 0) {
/* 856*/        creators.setDefaultCreator(factory);
/*   0*/        continue;
/*   0*/      } 
/* 860*/      switch (creatorMode) {
/*   0*/        case DELEGATING:
/* 862*/          _addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, null));
/*   0*/          break;
/*   0*/        case PROPERTIES:
/* 866*/          _addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
/*   0*/          break;
/*   0*/        default:
/* 871*/          _addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
/*   0*/          break;
/*   0*/      } 
/* 875*/      explCount++;
/*   0*/    } 
/* 878*/    if (explCount > 0) {
/*   0*/        return; 
/*   0*/       }
/* 882*/    for (CreatorCandidate candidate : nonAnnotated) {
/* 883*/      int argCount = candidate.paramCount();
/* 884*/      AnnotatedWithParams factory = candidate.creator();
/* 885*/      BeanPropertyDefinition[] propDefs = creatorParams.get(factory);
/* 887*/      if (argCount != 1) {
/*   0*/          continue; 
/*   0*/         }
/* 890*/      BeanPropertyDefinition argDef = candidate.propertyDef(0);
/* 891*/      boolean useProps = _checkIfCreatorPropertyBased(intr, factory, argDef);
/* 892*/      if (!useProps) {
/* 893*/        _handleSingleArgumentCreator(creators, factory, false, vchecker.isCreatorVisible(factory));
/* 897*/        if (argDef != null) {
/* 898*/            ((POJOPropertyBuilder)argDef).removeConstructors(); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 902*/      AnnotatedParameter nonAnnotatedParam = null;
/* 903*/      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/* 904*/      int implicitNameCount = 0;
/* 905*/      int explicitNameCount = 0;
/* 906*/      int injectCount = 0;
/* 908*/      for (int i = 0; i < argCount; i++) {
/* 909*/        AnnotatedParameter param = factory.getParameter(i);
/* 910*/        BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
/* 911*/        JacksonInject.Value injectable = intr.findInjectableValue(param);
/* 912*/        PropertyName name = (propDef == null) ? null : propDef.getFullName();
/* 914*/        if (propDef != null && propDef.isExplicitlyNamed()) {
/* 915*/          explicitNameCount++;
/* 916*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
/* 919*/        } else if (injectable != null) {
/* 920*/          injectCount++;
/* 921*/          properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
/*   0*/        } else {
/* 924*/          NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/* 925*/          if (unwrapper != null) {
/* 926*/            _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
/* 955*/          } else if (nonAnnotatedParam == null) {
/* 956*/            nonAnnotatedParam = param;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 959*/      int namedCount = explicitNameCount + implicitNameCount;
/* 962*/      if (explicitNameCount > 0 || injectCount > 0) {
/* 964*/        if (namedCount + injectCount == argCount) {
/* 965*/          creators.addPropertyCreator(factory, false, properties);
/*   0*/          continue;
/*   0*/        } 
/* 966*/        if (explicitNameCount == 0 && injectCount + 1 == argCount) {
/* 968*/          creators.addDelegatingCreator(factory, false, properties, 0);
/*   0*/          continue;
/*   0*/        } 
/* 970*/        ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", new Object[] { nonAnnotatedParam.getIndex(), factory });
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible) {
/* 982*/    Class<?> type = ctor.getRawParameterType(0);
/* 983*/    if (type == String.class || type == CLASS_CHAR_SEQUENCE) {
/* 984*/      if (isCreator || isVisible) {
/* 985*/          creators.addStringCreator(ctor, isCreator); 
/*   0*/         }
/* 987*/      return true;
/*   0*/    } 
/* 989*/    if (type == int.class || type == Integer.class) {
/* 990*/      if (isCreator || isVisible) {
/* 991*/          creators.addIntCreator(ctor, isCreator); 
/*   0*/         }
/* 993*/      return true;
/*   0*/    } 
/* 995*/    if (type == long.class || type == Long.class) {
/* 996*/      if (isCreator || isVisible) {
/* 997*/          creators.addLongCreator(ctor, isCreator); 
/*   0*/         }
/* 999*/      return true;
/*   0*/    } 
/*1001*/    if (type == double.class || type == Double.class) {
/*1002*/      if (isCreator || isVisible) {
/*1003*/          creators.addDoubleCreator(ctor, isCreator); 
/*   0*/         }
/*1005*/      return true;
/*   0*/    } 
/*1007*/    if (type == boolean.class || type == Boolean.class) {
/*1008*/      if (isCreator || isVisible) {
/*1009*/          creators.addBooleanCreator(ctor, isCreator); 
/*   0*/         }
/*1011*/      return true;
/*   0*/    } 
/*1014*/    if (isCreator) {
/*1015*/      creators.addDelegatingCreator(ctor, isCreator, null, 0);
/*1016*/      return true;
/*   0*/    } 
/*1018*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportUnwrappedCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedParameter param) throws JsonMappingException {
/*1027*/    ctxt.reportBadDefinition(beanDesc.getType(), String.format("Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", new Object[] { param.getIndex() }));
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable) throws JsonMappingException {
/*   0*/    PropertyMetadata metadata;
/*1043*/    DeserializationConfig config = ctxt.getConfig();
/*1044*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1047*/    if (intr == null) {
/*1048*/      metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
/*   0*/    } else {
/*1050*/      Boolean b = intr.hasRequiredMarker(param);
/*1051*/      String desc = intr.findPropertyDescription(param);
/*1052*/      Integer idx = intr.findPropertyIndex(param);
/*1053*/      String def = intr.findPropertyDefaultValue(param);
/*1054*/      metadata = PropertyMetadata.construct(b, desc, idx, def);
/*   0*/    } 
/*1057*/    JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
/*1058*/    BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), param, metadata);
/*1061*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/*1063*/    if (typeDeser == null) {
/*1064*/        typeDeser = findTypeDeserializer(config, type); 
/*   0*/       }
/*1069*/    Object injectableValueId = (injectable == null) ? null : injectable.getId();
/*1071*/    SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
/*1074*/    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
/*1075*/    if (deser == null) {
/*1076*/        deser = type.<JsonDeserializer>getValueHandler(); 
/*   0*/       }
/*1078*/    if (deser != null) {
/*1080*/      deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/*1081*/      prop = prop.withValueDeserializer(deser);
/*   0*/    } 
/*1083*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  private PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
/*1088*/    if (param != null && intr != null) {
/*1089*/      PropertyName name = intr.findNameForDeserialization(param);
/*1090*/      if (name != null) {
/*1091*/          return name; 
/*   0*/         }
/*1096*/      String str = intr.findImplicitPropertyName(param);
/*1097*/      if (str != null && !str.isEmpty()) {
/*1098*/          return PropertyName.construct(str); 
/*   0*/         }
/*   0*/    } 
/*1101*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
/*1115*/    DeserializationConfig config = ctxt.getConfig();
/*1116*/    JavaType elemType = type.getContentType();
/*1119*/    JsonDeserializer<Object> contentDeser = elemType.<JsonDeserializer<Object>>getValueHandler();
/*1121*/    TypeDeserializer elemTypeDeser = elemType.<TypeDeserializer>getTypeHandler();
/*1123*/    if (elemTypeDeser == null) {
/*1124*/        elemTypeDeser = findTypeDeserializer(config, elemType); 
/*   0*/       }
/*1127*/    JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
/*1129*/    if (deser == null) {
/*1130*/      if (contentDeser == null) {
/*1131*/        Class<?> raw = elemType.getRawClass();
/*1132*/        if (elemType.isPrimitive()) {
/*1133*/            return PrimitiveArrayDeserializers.forType(raw); 
/*   0*/           }
/*1135*/        if (raw == String.class) {
/*1136*/            return StringArrayDeserializer.instance; 
/*   0*/           }
/*   0*/      } 
/*1139*/      deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
/*   0*/    } 
/*1142*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1143*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1144*/            deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1147*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
/*1161*/    JavaType contentType = type.getContentType();
/*1163*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1164*/    DeserializationConfig config = ctxt.getConfig();
/*1167*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1169*/    if (contentTypeDeser == null) {
/*1170*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1173*/    JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1175*/    if (deser == null) {
/*1176*/      Class<?> collectionClass = type.getRawClass();
/*1177*/      if (contentDeser == null) {
/*1179*/          if (EnumSet.class.isAssignableFrom(collectionClass)) {
/*1180*/              deser = new EnumSetDeserializer(contentType, null); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/*1194*/    if (deser == null) {
/*1195*/      if (type.isInterface() || type.isAbstract()) {
/*1196*/        CollectionType implType = _mapAbstractCollectionType(type, config);
/*1197*/        if (implType == null) {
/*1199*/          if (type.getTypeHandler() == null) {
/*1200*/              throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type); 
/*   0*/             }
/*1202*/          deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/        } else {
/*1204*/          type = implType;
/*1206*/          beanDesc = config.introspectForCreation(type);
/*   0*/        } 
/*   0*/      } 
/*1209*/      if (deser == null) {
/*1210*/        ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1211*/        if (!inst.canCreateUsingDefault()) {
/*1213*/          if (type.hasRawClass(ArrayBlockingQueue.class)) {
/*1214*/              return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst); 
/*   0*/             }
/*1217*/          deser = JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
/*1218*/          if (deser != null) {
/*1219*/              return deser; 
/*   0*/             }
/*   0*/        } 
/*1223*/        if (contentType.hasRawClass(String.class)) {
/*1225*/          deser = new StringCollectionDeserializer(type, contentDeser, inst);
/*   0*/        } else {
/*1227*/          deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1232*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1233*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1234*/            deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1237*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
/*1242*/    Class<?> collectionClass = type.getRawClass();
/*1243*/    collectionClass = _collectionFallbacks.get(collectionClass.getName());
/*1244*/    if (collectionClass == null) {
/*1245*/        return null; 
/*   0*/       }
/*1247*/    return (CollectionType)config.constructSpecializedType(type, collectionClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1256*/    JavaType contentType = type.getContentType();
/*1258*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1259*/    DeserializationConfig config = ctxt.getConfig();
/*1262*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1264*/    if (contentTypeDeser == null) {
/*1265*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1267*/    JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1269*/    if (deser != null) {
/*1271*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1272*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1273*/                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1277*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
/*1291*/    DeserializationConfig config = ctxt.getConfig();
/*1292*/    JavaType keyType = type.getKeyType();
/*1293*/    JavaType contentType = type.getContentType();
/*1297*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1300*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1302*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1304*/    if (contentTypeDeser == null) {
/*1305*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1309*/    JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1312*/    if (deser == null) {
/*1314*/      Class<?> mapClass = type.getRawClass();
/*1315*/      if (EnumMap.class.isAssignableFrom(mapClass)) {
/*   0*/        ValueInstantiator inst;
/*1320*/        if (mapClass == EnumMap.class) {
/*1321*/          inst = null;
/*   0*/        } else {
/*1323*/          inst = findValueInstantiator(ctxt, beanDesc);
/*   0*/        } 
/*1325*/        Class<?> kt = keyType.getRawClass();
/*1326*/        if (kt == null || !kt.isEnum()) {
/*1327*/            throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available"); 
/*   0*/           }
/*1329*/        deser = new EnumMapDeserializer(type, inst, null, contentDeser, contentTypeDeser, null);
/*   0*/      } 
/*1344*/      if (deser == null) {
/*1345*/        if (type.isInterface() || type.isAbstract()) {
/*1347*/          Class<? extends Map> fallback = _mapFallbacks.get(mapClass.getName());
/*1348*/          if (fallback != null) {
/*1349*/            mapClass = fallback;
/*1350*/            type = (MapType)config.constructSpecializedType(type, mapClass);
/*1352*/            beanDesc = config.introspectForCreation(type);
/*   0*/          } else {
/*1355*/            if (type.getTypeHandler() == null) {
/*1356*/                throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type); 
/*   0*/               }
/*1358*/            deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*   0*/          } 
/*   0*/        } else {
/*1362*/          deser = JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
/*1363*/          if (deser != null) {
/*1364*/              return deser; 
/*   0*/             }
/*   0*/        } 
/*1367*/        if (deser == null) {
/*1368*/          ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*1373*/          MapDeserializer<?> md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
/*1374*/          JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
/*1376*/          Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForDeserialization();
/*1378*/          md.setIgnorableProperties(ignored);
/*1379*/          deser = md;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1383*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1384*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1385*/            deser = mod.modifyMapDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1388*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
/*1397*/    JavaType keyType = type.getKeyType();
/*1398*/    JavaType contentType = type.getContentType();
/*1399*/    DeserializationConfig config = ctxt.getConfig();
/*1403*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1406*/    KeyDeserializer keyDes = keyType.<KeyDeserializer>getValueHandler();
/*1413*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1415*/    if (contentTypeDeser == null) {
/*1416*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1418*/    JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*1420*/    if (deser != null) {
/*1422*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1423*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1424*/                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1428*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1445*/    DeserializationConfig config = ctxt.getConfig();
/*1446*/    Class<?> enumClass = type.getRawClass();
/*1448*/    JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1450*/    if (deser == null) {
/*1451*/      ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/*1452*/      SettableBeanProperty[] creatorProps = (valueInstantiator == null) ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*1455*/      for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1456*/        if (_hasCreatorAnnotation(ctxt, factory)) {
/*1457*/          if (factory.getParameterCount() == 0) {
/*1458*/            deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
/*   0*/            break;
/*   0*/          } 
/*1461*/          Class<?> returnType = factory.getRawReturnType();
/*1463*/          if (returnType.isAssignableFrom(enumClass)) {
/*1464*/            deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1471*/      if (deser == null) {
/*1472*/          deser = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)); 
/*   0*/         }
/*   0*/    } 
/*1479*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/*1480*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1481*/            deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/*1484*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
/*1493*/    Class<? extends JsonNode> nodeClass = (Class)nodeType.getRawClass();
/*1495*/    JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
/*1497*/    if (custom != null) {
/*1498*/        return custom; 
/*   0*/       }
/*1500*/    return JsonNodeDeserializer.getDeserializer(nodeClass);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
/*1508*/    JavaType contentType = type.getContentType();
/*1510*/    JsonDeserializer<Object> contentDeser = contentType.<JsonDeserializer<Object>>getValueHandler();
/*1511*/    DeserializationConfig config = ctxt.getConfig();
/*1513*/    TypeDeserializer contentTypeDeser = contentType.<TypeDeserializer>getTypeHandler();
/*1514*/    if (contentTypeDeser == null) {
/*1515*/        contentTypeDeser = findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1517*/    JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*1520*/    if (deser == null) {
/*1522*/        if (type.isTypeOrSubTypeOf(AtomicReference.class)) {
/*   0*/          ValueInstantiator inst;
/*1523*/          Class<?> rawType = type.getRawClass();
/*1525*/          if (rawType == AtomicReference.class) {
/*1526*/            inst = null;
/*   0*/          } else {
/*1532*/            inst = findValueInstantiator(ctxt, beanDesc);
/*   0*/          } 
/*1534*/          return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
/*   0*/        }  
/*   0*/       }
/*1537*/    if (deser != null) {
/*1539*/        if (this._factoryConfig.hasDeserializerModifiers()) {
/*1540*/            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1541*/                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser); 
/*   0*/               } 
/*   0*/           } 
/*   0*/       }
/*1545*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
/*1559*/    BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/*1560*/    AnnotatedClass ac = bean.getClassInfo();
/*1561*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1562*/    TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
/*1566*/    Collection<NamedType> subtypes = null;
/*1567*/    if (b == null) {
/*1568*/      b = config.getDefaultTyper(baseType);
/*1569*/      if (b == null) {
/*1570*/          return null; 
/*   0*/         }
/*   0*/    } else {
/*1573*/      subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
/*   0*/    } 
/*1577*/    if (b.getDefaultImpl() == null && baseType.isAbstract()) {
/*1578*/      JavaType defaultType = mapAbstractType(config, baseType);
/*1579*/      if (defaultType != null && !defaultType.hasRawClass(baseType.getRawClass())) {
/*1580*/          b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass()); 
/*   0*/         }
/*   0*/    } 
/*   0*/    try {
/*1586*/      return b.buildTypeDeserializer(config, baseType, subtypes);
/*1587*/    } catch (IllegalArgumentException e0) {
/*1588*/      InvalidDefinitionException e = InvalidDefinitionException.from((JsonParser)null, e0.getMessage(), baseType);
/*1590*/      e.initCause(e0);
/*1591*/      throw e;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1604*/    return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
/*   0*/  }
/*   0*/  
/*   0*/  public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1618*/    DeserializationConfig config = ctxt.getConfig();
/*1619*/    KeyDeserializer deser = null;
/*1620*/    if (this._factoryConfig.hasKeyDeserializers()) {
/*1621*/      BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
/*1622*/      for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
/*1623*/        deser = d.findKeyDeserializer(type, config, beanDesc);
/*1624*/        if (deser != null) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1630*/    if (deser == null) {
/*1631*/        if (type.isEnumType()) {
/*1632*/          deser = _createEnumKeyDeserializer(ctxt, type);
/*   0*/        } else {
/*1634*/          deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
/*   0*/        }  
/*   0*/       }
/*1638*/    if (deser != null && 
/*1639*/      this._factoryConfig.hasDeserializerModifiers()) {
/*1640*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*1641*/            deser = mod.modifyKeyDeserializer(config, type, deser); 
/*   0*/           } 
/*   0*/       }
/*1645*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*1652*/    DeserializationConfig config = ctxt.getConfig();
/*1653*/    Class<?> enumClass = type.getRawClass();
/*1655*/    BeanDescription beanDesc = config.introspect(type);
/*1657*/    KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1658*/    if (des != null) {
/*1659*/        return des; 
/*   0*/       }
/*1662*/    JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*1663*/    if (custom != null) {
/*1664*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom); 
/*   0*/       }
/*1666*/    JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*1667*/    if (valueDesForKey != null) {
/*1668*/        return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey); 
/*   0*/       }
/*1671*/    EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
/*1673*/    for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*1674*/      if (_hasCreatorAnnotation(ctxt, factory)) {
/*1675*/        int argCount = factory.getParameterCount();
/*1676*/        if (argCount == 1) {
/*1677*/          Class<?> returnType = factory.getRawReturnType();
/*1679*/          if (returnType.isAssignableFrom(enumClass)) {
/*1681*/            if (factory.getRawParameterType(0) != String.class) {
/*1682*/                throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String"); 
/*   0*/               }
/*1684*/            if (config.canOverrideAccessModifiers()) {
/*1685*/                ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/               }
/*1688*/            return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
/*   0*/          } 
/*   0*/        } 
/*1691*/        throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*   0*/      } 
/*   0*/    } 
/*1696*/    return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
/*1722*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1723*/    TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
/*1725*/    if (b == null) {
/*1726*/        return findTypeDeserializer(config, baseType); 
/*   0*/       }
/*1729*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
/*1731*/    return b.buildTypeDeserializer(config, baseType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
/*1749*/    AnnotationIntrospector ai = config.getAnnotationIntrospector();
/*1750*/    TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
/*1751*/    JavaType contentType = containerType.getContentType();
/*1753*/    if (b == null) {
/*1754*/        return findTypeDeserializer(config, contentType); 
/*   0*/       }
/*1757*/    Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
/*1759*/    return b.buildTypeDeserializer(config, contentType, subtypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*1773*/    Class<?> rawType = type.getRawClass();
/*1775*/    if (rawType == CLASS_OBJECT) {
/*   0*/      JavaType lt, mt;
/*1777*/      DeserializationConfig config = ctxt.getConfig();
/*1780*/      if (this._factoryConfig.hasAbstractTypeResolvers()) {
/*1781*/        lt = _findRemappedType(config, List.class);
/*1782*/        mt = _findRemappedType(config, Map.class);
/*   0*/      } else {
/*1784*/        lt = mt = null;
/*   0*/      } 
/*1786*/      return new UntypedObjectDeserializer(lt, mt);
/*   0*/    } 
/*1788*/    if (rawType == CLASS_STRING || rawType == CLASS_CHAR_SEQUENCE) {
/*1789*/        return StringDeserializer.instance; 
/*   0*/       }
/*1791*/    if (rawType == CLASS_ITERABLE) {
/*1793*/      TypeFactory tf = ctxt.getTypeFactory();
/*1794*/      JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
/*1795*/      JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
/*1796*/      CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
/*1798*/      return createCollectionDeserializer(ctxt, ct, beanDesc);
/*   0*/    } 
/*1800*/    if (rawType == CLASS_MAP_ENTRY) {
/*1802*/      JavaType kt = type.containedTypeOrUnknown(0);
/*1803*/      JavaType vt = type.containedTypeOrUnknown(1);
/*1804*/      TypeDeserializer vts = vt.<TypeDeserializer>getTypeHandler();
/*1805*/      if (vts == null) {
/*1806*/          vts = findTypeDeserializer(ctxt.getConfig(), vt); 
/*   0*/         }
/*1808*/      JsonDeserializer<Object> valueDeser = vt.<JsonDeserializer<Object>>getValueHandler();
/*1809*/      KeyDeserializer keyDes = kt.<KeyDeserializer>getValueHandler();
/*1810*/      return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
/*   0*/    } 
/*1812*/    String clsName = rawType.getName();
/*1813*/    if (rawType.isPrimitive() || clsName.startsWith("java.")) {
/*1815*/      JsonDeserializer<?> jsonDeserializer = NumberDeserializers.find(rawType, clsName);
/*1816*/      if (jsonDeserializer == null) {
/*1817*/          jsonDeserializer = DateDeserializers.find(rawType, clsName); 
/*   0*/         }
/*1819*/      if (jsonDeserializer != null) {
/*1820*/          return jsonDeserializer; 
/*   0*/         }
/*   0*/    } 
/*1824*/    if (rawType == TokenBuffer.class) {
/*1825*/        return new TokenBufferDeserializer(); 
/*   0*/       }
/*1827*/    JsonDeserializer<?> deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
/*1828*/    if (deser != null) {
/*1829*/        return deser; 
/*   0*/       }
/*1831*/    return JdkDeserializers.find(rawType, clsName);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
/*1835*/    JavaType type = mapAbstractType(config, config.constructType(rawType));
/*1836*/    return (type == null || type.hasRawClass(rawType)) ? null : type;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1849*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1850*/      JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
/*1851*/      if (deser != null) {
/*1852*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1855*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
/*1863*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1864*/      JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
/*1866*/      if (deser != null) {
/*1867*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1870*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1878*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1879*/      JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
/*1880*/      if (deser != null) {
/*1881*/          return (JsonDeserializer)deser; 
/*   0*/         }
/*   0*/    } 
/*1884*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1892*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1893*/      JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1895*/      if (deser != null) {
/*1896*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1899*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1907*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1908*/      JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1910*/      if (deser != null) {
/*1911*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1914*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1922*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1923*/      JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*1925*/      if (deser != null) {
/*1926*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1929*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
/*1936*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1937*/      JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
/*1938*/      if (deser != null) {
/*1939*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1942*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1951*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1952*/      JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1954*/      if (deser != null) {
/*1955*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1958*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
/*1967*/    for (Deserializers d : this._factoryConfig.deserializers()) {
/*1968*/      JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*1970*/      if (deser != null) {
/*1971*/          return deser; 
/*   0*/         }
/*   0*/    } 
/*1974*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*1995*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*1996*/    if (intr != null) {
/*1997*/      Object deserDef = intr.findDeserializer(ann);
/*1998*/      if (deserDef != null) {
/*1999*/          return ctxt.deserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*2002*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*2014*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*2015*/    if (intr != null) {
/*2016*/      Object deserDef = intr.findKeyDeserializer(ann);
/*2017*/      if (deserDef != null) {
/*2018*/          return ctxt.keyDeserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*2021*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
/*2031*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*2032*/    if (intr != null) {
/*2033*/      Object deserDef = intr.findContentDeserializer(ann);
/*2034*/      if (deserDef != null) {
/*2035*/          return ctxt.deserializerInstance(ann, deserDef); 
/*   0*/         }
/*   0*/    } 
/*2038*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
/*2054*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*2055*/    if (intr == null) {
/*2056*/        return type; 
/*   0*/       }
/*2062*/    if (type.isMapLikeType()) {
/*2063*/      JavaType keyType = type.getKeyType();
/*2064*/      if (keyType != null) {
/*2065*/        Object kdDef = intr.findKeyDeserializer(member);
/*2066*/        KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
/*2067*/        if (kd != null) {
/*2068*/          type = ((MapLikeType)type).withKeyValueHandler(kd);
/*2069*/          keyType = type.getKeyType();
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2074*/    if (type.hasContentType()) {
/*2075*/      Object cdDef = intr.findContentDeserializer(member);
/*2076*/      JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
/*2077*/      if (cd != null) {
/*2078*/          type = type.withContentValueHandler(cd); 
/*   0*/         }
/*2080*/      TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
/*2082*/      if (contentTypeDeser != null) {
/*2083*/          type = type.withContentTypeHandler(contentTypeDeser); 
/*   0*/         }
/*   0*/    } 
/*2086*/    TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
/*2088*/    if (valueTypeDeser != null) {
/*2089*/        type = type.withTypeHandler(valueTypeDeser); 
/*   0*/       }
/*2097*/    type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
/*2098*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMember jsonValueAccessor) {
/*2104*/    if (jsonValueAccessor != null) {
/*2105*/      if (config.canOverrideAccessModifiers()) {
/*2106*/          ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/         }
/*2109*/      return EnumResolver.constructUnsafeUsingMethod(enumClass, jsonValueAccessor, config.getAnnotationIntrospector());
/*   0*/    } 
/*2114*/    return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _hasCreatorAnnotation(DeserializationContext ctxt, Annotated ann) {
/*2122*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*2123*/    if (intr != null) {
/*2124*/      JsonCreator.Mode mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann);
/*2125*/      return (mode != null && mode != JsonCreator.Mode.DISABLED);
/*   0*/    } 
/*2127*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
/*2147*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*2148*/    if (intr == null) {
/*2149*/        return type; 
/*   0*/       }
/*2151*/    return intr.refineDeserializationType(ctxt.getConfig(), a, type);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
/*2162*/    return resolveMemberAndTypeAnnotations(ctxt, member, type);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
/*2171*/    if (enumType == null) {
/*2172*/        return null; 
/*   0*/       }
/*2174*/    BeanDescription beanDesc = config.introspect(enumType);
/*2175*/    return beanDesc.findJsonValueMethod();
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
/*   0*/}
