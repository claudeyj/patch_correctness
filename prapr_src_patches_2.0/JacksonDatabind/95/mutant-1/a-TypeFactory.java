/*   0*/package com.fasterxml.jackson.databind.type;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.type.TypeReference;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.LRUMap;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.GenericArrayType;
/*   0*/import java.lang.reflect.ParameterizedType;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.lang.reflect.TypeVariable;
/*   0*/import java.lang.reflect.WildcardType;
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
/*   0*/import java.util.Properties;
/*   0*/import java.util.TreeMap;
/*   0*/import java.util.TreeSet;
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public final class TypeFactory implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  39*/  private static final JavaType[] NO_TYPES = new JavaType[0];
/*   0*/  
/*  46*/  protected static final TypeFactory instance = new TypeFactory();
/*   0*/  
/*  48*/  protected static final TypeBindings EMPTY_BINDINGS = TypeBindings.emptyBindings();
/*   0*/  
/*  60*/  private static final Class<?> CLS_STRING = String.class;
/*   0*/  
/*  61*/  private static final Class<?> CLS_OBJECT = Object.class;
/*   0*/  
/*  63*/  private static final Class<?> CLS_COMPARABLE = Comparable.class;
/*   0*/  
/*  64*/  private static final Class<?> CLS_CLASS = Class.class;
/*   0*/  
/*  65*/  private static final Class<?> CLS_ENUM = Enum.class;
/*   0*/  
/*  67*/  private static final Class<?> CLS_BOOL = boolean.class;
/*   0*/  
/*  68*/  private static final Class<?> CLS_INT = int.class;
/*   0*/  
/*  69*/  private static final Class<?> CLS_LONG = long.class;
/*   0*/  
/*  78*/  protected static final SimpleType CORE_TYPE_BOOL = new SimpleType(CLS_BOOL);
/*   0*/  
/*  79*/  protected static final SimpleType CORE_TYPE_INT = new SimpleType(CLS_INT);
/*   0*/  
/*  80*/  protected static final SimpleType CORE_TYPE_LONG = new SimpleType(CLS_LONG);
/*   0*/  
/*  83*/  protected static final SimpleType CORE_TYPE_STRING = new SimpleType(CLS_STRING);
/*   0*/  
/*  86*/  protected static final SimpleType CORE_TYPE_OBJECT = new SimpleType(CLS_OBJECT);
/*   0*/  
/*  94*/  protected static final SimpleType CORE_TYPE_COMPARABLE = new SimpleType(CLS_COMPARABLE);
/*   0*/  
/* 102*/  protected static final SimpleType CORE_TYPE_ENUM = new SimpleType(CLS_ENUM);
/*   0*/  
/* 110*/  protected static final SimpleType CORE_TYPE_CLASS = new SimpleType(CLS_CLASS);
/*   0*/  
/*   0*/  protected final LRUMap<Object, JavaType> _typeCache;
/*   0*/  
/*   0*/  protected final TypeModifier[] _modifiers;
/*   0*/  
/*   0*/  protected final TypeParser _parser;
/*   0*/  
/*   0*/  protected final ClassLoader _classLoader;
/*   0*/  
/*   0*/  private TypeFactory() {
/* 145*/    this(null);
/*   0*/  }
/*   0*/  
/*   0*/  protected TypeFactory(LRUMap<Object, JavaType> typeCache) {
/* 152*/    if (typeCache == null) {
/* 153*/        typeCache = new LRUMap<>(16, 200); 
/*   0*/       }
/* 155*/    this._typeCache = typeCache;
/* 156*/    this._parser = new TypeParser(this);
/* 157*/    this._modifiers = null;
/* 158*/    this._classLoader = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected TypeFactory(LRUMap<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
/* 164*/    if (typeCache == null) {
/* 165*/        typeCache = new LRUMap<>(16, 200); 
/*   0*/       }
/* 167*/    this._typeCache = typeCache;
/* 169*/    this._parser = p.withFactory(this);
/* 170*/    this._modifiers = mods;
/* 171*/    this._classLoader = classLoader;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory withModifier(TypeModifier mod) {
/*   0*/    TypeModifier[] mods;
/* 176*/    LRUMap<Object, JavaType> typeCache = this._typeCache;
/* 178*/    if (mod == null) {
/* 179*/      mods = null;
/* 182*/      typeCache = null;
/* 183*/    } else if (this._modifiers == null) {
/* 184*/      mods = new TypeModifier[] { mod };
/*   0*/    } else {
/* 186*/      mods = ArrayBuilders.<TypeModifier>insertInListNoDup(this._modifiers, mod);
/*   0*/    } 
/* 188*/    return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory withClassLoader(ClassLoader classLoader) {
/* 192*/    return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory withCache(LRUMap<Object, JavaType> cache) {
/* 203*/    return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeFactory defaultInstance() {
/* 211*/    return instance;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearCache() {
/* 224*/    this._typeCache.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public ClassLoader getClassLoader() {
/* 228*/    return this._classLoader;
/*   0*/  }
/*   0*/  
/*   0*/  public static JavaType unknownType() {
/* 243*/    return defaultInstance()._unknownType();
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> rawClass(Type t) {
/* 253*/    if (t instanceof Class) {
/* 254*/        return (Class)t; 
/*   0*/       }
/* 257*/    return defaultInstance().constructType(t).getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> findClass(String className) throws ClassNotFoundException {
/* 274*/    if (className.indexOf('.') < 0) {
/* 275*/      Class<?> prim = _findPrimitive(className);
/* 276*/      if (prim != null) {
/* 277*/          return prim; 
/*   0*/         }
/*   0*/    } 
/* 281*/    Throwable prob = null;
/* 282*/    ClassLoader loader = getClassLoader();
/* 283*/    if (loader == null) {
/* 284*/        loader = Thread.currentThread().getContextClassLoader(); 
/*   0*/       }
/* 286*/    if (loader != null) {
/*   0*/        try {
/* 288*/          return classForName(className, true, loader);
/* 289*/        } catch (Exception e) {
/* 290*/          prob = ClassUtil.getRootCause(e);
/*   0*/        }  
/*   0*/       }
/*   0*/    try {
/* 294*/      return classForName(className);
/* 295*/    } catch (Exception e) {
/* 296*/      if (prob == null) {
/* 297*/          prob = ClassUtil.getRootCause(e); 
/*   0*/         }
/* 300*/      if (prob instanceof RuntimeException) {
/* 301*/          throw (RuntimeException)prob; 
/*   0*/         }
/* 303*/      throw new ClassNotFoundException(prob.getMessage(), prob);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
/* 308*/    return Class.forName(name, true, loader);
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> classForName(String name) throws ClassNotFoundException {
/* 312*/    return Class.forName(name);
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> _findPrimitive(String className) {
/* 317*/    if ("int".equals(className)) {
/* 317*/        return int.class; 
/*   0*/       }
/* 318*/    if ("long".equals(className)) {
/* 318*/        return long.class; 
/*   0*/       }
/* 319*/    if ("float".equals(className)) {
/* 319*/        return float.class; 
/*   0*/       }
/* 320*/    if ("double".equals(className)) {
/* 320*/        return double.class; 
/*   0*/       }
/* 321*/    if ("boolean".equals(className)) {
/* 321*/        return boolean.class; 
/*   0*/       }
/* 322*/    if ("byte".equals(className)) {
/* 322*/        return byte.class; 
/*   0*/       }
/* 323*/    if ("char".equals(className)) {
/* 323*/        return char.class; 
/*   0*/       }
/* 324*/    if ("short".equals(className)) {
/* 324*/        return short.class; 
/*   0*/       }
/* 325*/    if ("void".equals(className)) {
/* 325*/        return void.class; 
/*   0*/       }
/* 326*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
/* 345*/    Class<?> rawBase = baseType.getRawClass();
/* 346*/    if (rawBase == subclass) {
/* 347*/        return baseType; 
/*   0*/       }
/* 354*/    if (rawBase == Object.class) {
/* 355*/      javaType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/    } else {
/* 358*/      if (!rawBase.isAssignableFrom(subclass)) {
/* 359*/          throw new IllegalArgumentException(String.format("Class %s not subtype of %s", new Object[] { subclass.getName(), baseType })); 
/*   0*/         }
/* 365*/      if (baseType.getBindings().isEmpty()) {
/* 366*/        javaType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/      } else {
/* 370*/        if (baseType.isContainerType()) {
/* 371*/            if (baseType.isMapLikeType()) {
/* 372*/              if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
/* 376*/                javaType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
/* 410*/                javaType = javaType.withHandlersFrom(baseType);
/* 411*/                return javaType;
/*   0*/              } 
/*   0*/            } else if (baseType.isCollectionLikeType()) {
/*   0*/              if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
/*   0*/                javaType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
/*   0*/                javaType = javaType.withHandlersFrom(baseType);
/* 411*/                return javaType;
/*   0*/              } 
/*   0*/              if (rawBase == EnumSet.class) {
/*   0*/                  return baseType; 
/*   0*/                 }
/*   0*/            }  
/*   0*/           }
/*   0*/        int typeParamCount = (subclass.getTypeParameters()).length;
/*   0*/        if (typeParamCount == 0) {
/*   0*/          javaType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/        } else {
/*   0*/          TypeBindings tb = _bindingsForSubtype(baseType, typeParamCount, subclass);
/*   0*/          javaType = _fromClass(null, subclass, tb);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    JavaType javaType = javaType.withHandlersFrom(baseType);
/* 411*/    return javaType;
/*   0*/  }
/*   0*/  
/*   0*/  private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass) {
/* 416*/    PlaceholderForType[] placeholders = new PlaceholderForType[typeParamCount];
/* 417*/    for (int i = 0; i < typeParamCount; i++) {
/* 418*/        placeholders[i] = new PlaceholderForType(i); 
/*   0*/       }
/* 420*/    TypeBindings b = TypeBindings.create(subclass, (JavaType[])placeholders);
/* 422*/    JavaType tmpSub = _fromClass(null, subclass, b);
/* 424*/    JavaType baseWithPlaceholders = tmpSub.findSuperType(baseType.getRawClass());
/* 425*/    if (baseWithPlaceholders == null) {
/* 426*/        throw new IllegalArgumentException(String.format("Internal error: unable to locate supertype (%s) from resolved subtype %s", new Object[] { baseType.getRawClass().getName(), subclass.getName() })); 
/*   0*/       }
/* 431*/    String error = _resolveTypePlaceholders(baseType, baseWithPlaceholders);
/* 432*/    if (error != null) {
/* 433*/        throw new IllegalArgumentException("Failed to specialize base type " + baseType.toCanonical() + " as " + subclass.getName() + ", problem: " + error); 
/*   0*/       }
/* 437*/    JavaType[] typeParams = new JavaType[typeParamCount];
/* 438*/    for (int j = 0; j < typeParamCount; j++) {
/* 439*/      JavaType t = placeholders[j].actualType();
/* 443*/      if (t == null) {
/* 444*/          t = unknownType(); 
/*   0*/         }
/* 446*/      typeParams[j] = t;
/*   0*/    } 
/* 448*/    return TypeBindings.create(subclass, typeParams);
/*   0*/  }
/*   0*/  
/*   0*/  private String _resolveTypePlaceholders(JavaType sourceType, JavaType actualType) throws IllegalArgumentException {
/* 454*/    List<JavaType> expectedTypes = sourceType.getBindings().getTypeParameters();
/* 455*/    List<JavaType> actualTypes = actualType.getBindings().getTypeParameters();
/* 456*/    for (int i = 0, len = expectedTypes.size(); i < len; i++) {
/* 457*/      JavaType exp = expectedTypes.get(i);
/* 458*/      JavaType act = actualTypes.get(i);
/* 459*/      if (!_verifyAndResolvePlaceholders(exp, act)) {
/* 460*/          return String.format("Type parameter #%d/%d differs; can not specialize %s with %s", new Object[] { i + 1, len, exp.toCanonical(), act.toCanonical() }); 
/*   0*/         }
/*   0*/    } 
/* 464*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyAndResolvePlaceholders(JavaType exp, JavaType act) {
/* 470*/    if (act instanceof PlaceholderForType) {
/* 471*/      ((PlaceholderForType)act).actualType(exp);
/* 472*/      return true;
/*   0*/    } 
/* 476*/    if (exp.getRawClass() != act.getRawClass()) {
/* 477*/        return false; 
/*   0*/       }
/* 480*/    List<JavaType> expectedTypes = exp.getBindings().getTypeParameters();
/* 481*/    List<JavaType> actualTypes = act.getBindings().getTypeParameters();
/* 482*/    for (int i = 0, len = expectedTypes.size(); i < len; i++) {
/* 483*/      JavaType exp2 = expectedTypes.get(i);
/* 484*/      JavaType act2 = actualTypes.get(i);
/* 485*/      if (!_verifyAndResolvePlaceholders(exp2, act2)) {
/* 486*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 489*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
/* 505*/    Class<?> rawBase = baseType.getRawClass();
/* 506*/    if (rawBase == superClass) {
/* 507*/        return baseType; 
/*   0*/       }
/* 509*/    JavaType superType = baseType.findSuperType(superClass);
/* 510*/    if (superType == null) {
/* 512*/      if (!superClass.isAssignableFrom(rawBase)) {
/* 513*/          throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass.getName(), baseType })); 
/*   0*/         }
/* 517*/      throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass.getName(), baseType }));
/*   0*/    } 
/* 521*/    return superType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
/* 536*/    return this._parser.parse(canonical);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
/* 550*/    JavaType match = type.findSuperType(expType);
/* 551*/    if (match == null) {
/* 552*/        return NO_TYPES; 
/*   0*/       }
/* 554*/    return match.getBindings().typeParameterArray();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
/* 562*/    return findTypeParameters(constructType(clz, bindings), expType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
/* 570*/    return findTypeParameters(constructType(clz), expType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType moreSpecificType(JavaType type1, JavaType type2) {
/* 585*/    if (type1 == null) {
/* 586*/        return type2; 
/*   0*/       }
/* 588*/    if (type2 == null) {
/* 589*/        return type1; 
/*   0*/       }
/* 591*/    Class<?> raw1 = type1.getRawClass();
/* 592*/    Class<?> raw2 = type2.getRawClass();
/* 593*/    if (raw1 == raw2) {
/* 594*/        return type1; 
/*   0*/       }
/* 597*/    if (raw1.isAssignableFrom(raw2)) {
/* 598*/        return type2; 
/*   0*/       }
/* 600*/    return type1;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type) {
/* 610*/    return _fromAny(null, type, EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type, TypeBindings bindings) {
/* 614*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(TypeReference<?> typeRef) {
/* 620*/    return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, Class<?> contextClass) {
/* 646*/    JavaType contextType = (contextClass == null) ? null : constructType(contextClass);
/* 647*/    return constructType(type, contextType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, JavaType contextType) {
/*   0*/    TypeBindings bindings;
/* 656*/    if (contextType == null) {
/* 657*/      bindings = TypeBindings.emptyBindings();
/*   0*/    } else {
/* 659*/      bindings = contextType.getBindings();
/* 664*/      if (type.getClass() != Class.class) {
/* 667*/          while (bindings.isEmpty()) {
/* 668*/            contextType = contextType.getSuperClass();
/* 669*/            if (contextType == null) {
/*   0*/                break; 
/*   0*/               }
/* 672*/            bindings = contextType.getBindings();
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 676*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(Class<?> elementType) {
/* 692*/    return ArrayType.construct(_fromAny(null, elementType, null), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(JavaType elementType) {
/* 702*/    return ArrayType.construct(elementType, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
/* 713*/    return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
/* 727*/    return (CollectionType)_fromClass(null, collectionClass, TypeBindings.create(collectionClass, elementType));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
/* 738*/    return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
/* 749*/    JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
/* 751*/    if (type instanceof CollectionLikeType) {
/* 752*/        return (CollectionLikeType)type; 
/*   0*/       }
/* 754*/    return CollectionLikeType.upgradeFrom(type, elementType);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*   0*/    JavaType kt, vt;
/* 766*/    if (mapClass == Properties.class) {
/* 767*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 769*/      kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/* 770*/      vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*   0*/    } 
/* 772*/    return constructMapType(mapClass, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
/* 782*/    return (MapType)_fromClass(null, mapClass, TypeBindings.create(mapClass, keyType, valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
/* 793*/    return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
/* 807*/    JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/* 809*/    if (type instanceof MapLikeType) {
/* 810*/        return (MapLikeType)type; 
/*   0*/       }
/* 812*/    return MapLikeType.upgradeFrom(type, keyType, valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
/* 821*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
/* 835*/    return constructSimpleType(rawType, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
/* 843*/    return ReferenceType.construct(rawType, null, null, null, referredType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType uncheckedSimpleType(Class<?> cls) {
/* 862*/    return _constructSimple(cls, EMPTY_BINDINGS, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
/* 893*/    int len = parameterClasses.length;
/* 894*/    JavaType[] pt = new JavaType[len];
/* 895*/    for (int i = 0; i < len; i++) {
/* 896*/        pt[i] = _fromClass(null, parameterClasses[i], null); 
/*   0*/       }
/* 898*/    return constructParametricType(parametrized, pt);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
/* 930*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
/* 939*/    return constructParametricType(parametrized, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
/* 948*/    return constructParametricType(parametrized, parameterClasses);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
/* 970*/    return constructCollectionType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
/* 985*/    return constructCollectionLikeType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructRawMapType(Class<? extends Map> mapClass) {
/*1000*/    return constructMapType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
/*1015*/    return constructMapLikeType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType kt, vt;
/*1030*/    if (rawClass == Properties.class) {
/*1031*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/*1033*/      List<JavaType> typeParams = bindings.getTypeParameters();
/*1035*/      switch (typeParams.size()) {
/*   0*/        case 0:
/*1037*/          kt = vt = _unknownType();
/*   0*/          break;
/*   0*/        case 2:
/*1040*/          kt = typeParams.get(0);
/*1041*/          vt = typeParams.get(1);
/*   0*/          break;
/*   0*/        default:
/*1044*/          throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/      } 
/*   0*/    } 
/*1047*/    return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1053*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1056*/    if (typeParams.isEmpty()) {
/*1057*/      ct = _unknownType();
/*1058*/    } else if (typeParams.size() == 1) {
/*1059*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1061*/      throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1063*/    return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1069*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1072*/    if (typeParams.isEmpty()) {
/*1073*/      ct = _unknownType();
/*1074*/    } else if (typeParams.size() == 1) {
/*1075*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1077*/      throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1079*/    return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1093*/    if (bindings.isEmpty()) {
/*1094*/      JavaType result = _findWellKnownSimple(raw);
/*1095*/      if (result != null) {
/*1096*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1099*/    return _newSimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1112*/    return new SimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _unknownType() {
/*1121*/    return CORE_TYPE_OBJECT;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findWellKnownSimple(Class<?> clz) {
/*1132*/    if (clz.isPrimitive()) {
/*1133*/      if (clz == CLS_BOOL) {
/*1133*/          return CORE_TYPE_BOOL; 
/*   0*/         }
/*1134*/      if (clz == CLS_INT) {
/*1134*/          return CORE_TYPE_INT; 
/*   0*/         }
/*1135*/      if (clz == CLS_LONG) {
/*1135*/          return CORE_TYPE_LONG; 
/*   0*/         }
/*   0*/    } else {
/*1137*/      if (clz == CLS_STRING) {
/*1137*/          return CORE_TYPE_STRING; 
/*   0*/         }
/*1138*/      if (clz == CLS_OBJECT) {
/*1138*/          return CORE_TYPE_OBJECT; 
/*   0*/         }
/*   0*/    } 
/*1140*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
/*   0*/    JavaType resultType;
/*1159*/    if (type instanceof Class) {
/*1161*/      resultType = _fromClass(context, (Class)type, EMPTY_BINDINGS);
/*1164*/    } else if (type instanceof ParameterizedType) {
/*1165*/      resultType = _fromParamType(context, (ParameterizedType)type, bindings);
/*   0*/    } else {
/*1167*/      if (type instanceof JavaType) {
/*1169*/          return (JavaType)type; 
/*   0*/         }
/*1171*/      if (type instanceof GenericArrayType) {
/*1172*/        resultType = _fromArrayType(context, (GenericArrayType)type, bindings);
/*1174*/      } else if (type instanceof TypeVariable) {
/*1175*/        resultType = _fromVariable(context, (TypeVariable)type, bindings);
/*1177*/      } else if (type instanceof WildcardType) {
/*1178*/        resultType = _fromWildcard(context, (WildcardType)type, bindings);
/*   0*/      } else {
/*1181*/        throw new IllegalArgumentException("Unrecognized Type: " + ((type == null) ? "[null]" : type.toString()));
/*   0*/      } 
/*   0*/    } 
/*1186*/    if (this._modifiers != null) {
/*1187*/      TypeBindings b = resultType.getBindings();
/*1188*/      if (b == null) {
/*1189*/          b = EMPTY_BINDINGS; 
/*   0*/         }
/*1191*/      for (TypeModifier mod : this._modifiers) {
/*1192*/        JavaType t = mod.modifyType(resultType, type, b, this);
/*1193*/        if (t == null) {
/*1194*/            throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[] { mod, mod.getClass().getName(), resultType })); 
/*   0*/           }
/*1198*/        resultType = t;
/*   0*/      } 
/*   0*/    } 
/*1201*/    return resultType;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
/*   0*/    Object key;
/*1211*/    JavaType result = _findWellKnownSimple(rawType);
/*1212*/    if (result != null) {
/*1213*/        return result; 
/*   0*/       }
/*1217*/    if (bindings == null || bindings.isEmpty()) {
/*1218*/      key = rawType;
/*   0*/    } else {
/*1220*/      key = bindings.asKey(rawType);
/*   0*/    } 
/*1222*/    result = this._typeCache.get(key);
/*1223*/    if (result != null) {
/*1224*/        return result; 
/*   0*/       }
/*1228*/    if (context == null) {
/*1229*/      context = new ClassStack(rawType);
/*   0*/    } else {
/*1231*/      ClassStack prev = context.find(rawType);
/*1232*/      if (prev != null) {
/*1234*/        ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/*1235*/        prev.addSelfReference(selfRef);
/*1236*/        return selfRef;
/*   0*/      } 
/*1239*/      context = context.child(rawType);
/*   0*/    } 
/*1243*/    if (rawType.isArray()) {
/*1244*/      result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*   0*/    } else {
/*   0*/      JavaType superClass, superInterfaces[];
/*1252*/      if (rawType.isInterface()) {
/*1253*/        superClass = null;
/*1254*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } else {
/*1257*/        superClass = _resolveSuperClass(context, rawType, bindings);
/*1258*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } 
/*1262*/      if (rawType == Properties.class) {
/*1263*/        result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*1268*/      } else if (superClass != null) {
/*1269*/        result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*   0*/      } 
/*1272*/      if (result == null) {
/*1273*/        result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/*1274*/        if (result == null) {
/*1275*/          result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/*1276*/          if (result == null) {
/*1278*/              result = _newSimpleType(rawType, bindings, superClass, superInterfaces); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1283*/    context.resolveSelfReferences(result);
/*1286*/    if (!result.hasHandlers()) {
/*1287*/        this._typeCache.putIfAbsent(key, result); 
/*   0*/       }
/*1289*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1294*/    Type parent = ClassUtil.getGenericSuperclass(rawType);
/*1295*/    if (parent == null) {
/*1296*/        return null; 
/*   0*/       }
/*1298*/    return _fromAny(context, parent, parentBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1303*/    Type[] types = ClassUtil.getGenericInterfaces(rawType);
/*1304*/    if (types == null || types.length == 0) {
/*1305*/        return NO_TYPES; 
/*   0*/       }
/*1307*/    int len = types.length;
/*1308*/    JavaType[] resolved = new JavaType[len];
/*1309*/    for (int i = 0; i < len; i++) {
/*1310*/      Type type = types[i];
/*1311*/      resolved[i] = _fromAny(context, type, parentBindings);
/*   0*/    } 
/*1313*/    return resolved;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1324*/    if (bindings == null) {
/*1325*/        bindings = TypeBindings.emptyBindings(); 
/*   0*/       }
/*1329*/    if (rawType == Map.class) {
/*1330*/        return _mapType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1332*/    if (rawType == Collection.class) {
/*1333*/        return _collectionType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1336*/    if (rawType == AtomicReference.class) {
/*1337*/        return _referenceType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1343*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1351*/    int intCount = superInterfaces.length;
/*1353*/    for (int i = 0; i < intCount; i++) {
/*1354*/      JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/*1355*/      if (result != null) {
/*1356*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1359*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
/*   0*/    TypeBindings newBindings;
/*1370*/    Class<?> rawType = (Class)ptype.getRawType();
/*1374*/    if (rawType == CLS_ENUM) {
/*1375*/        return CORE_TYPE_ENUM; 
/*   0*/       }
/*1377*/    if (rawType == CLS_COMPARABLE) {
/*1378*/        return CORE_TYPE_COMPARABLE; 
/*   0*/       }
/*1380*/    if (rawType == CLS_CLASS) {
/*1381*/        return CORE_TYPE_CLASS; 
/*   0*/       }
/*1387*/    Type[] args = ptype.getActualTypeArguments();
/*1388*/    int paramCount = (args == null) ? 0 : args.length;
/*1391*/    if (paramCount == 0) {
/*1392*/      newBindings = EMPTY_BINDINGS;
/*   0*/    } else {
/*1394*/      JavaType[] pt = new JavaType[paramCount];
/*1395*/      for (int i = 0; i < paramCount; i++) {
/*1396*/          pt[i] = _fromAny(context, args[i], parentBindings); 
/*   0*/         }
/*1398*/      newBindings = TypeBindings.create(rawType, pt);
/*   0*/    } 
/*1400*/    return _fromClass(context, rawType, newBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
/*1405*/    JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/*1406*/    return ArrayType.construct(elementType, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
/*1412*/    String name = var.getName();
/*1413*/    JavaType type = bindings.findBoundType(name);
/*1414*/    if (type != null) {
/*1415*/        return type; 
/*   0*/       }
/*1419*/    if (bindings.hasUnbound(name)) {
/*1420*/        return CORE_TYPE_OBJECT; 
/*   0*/       }
/*1422*/    bindings = bindings.withUnboundVariable(name);
/*1424*/    Type[] bounds = var.getBounds();
/*1425*/    return _fromAny(context, bounds[0], bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
/*1435*/    return _fromAny(context, type.getUpperBounds()[0], bindings);
/*   0*/  }
/*   0*/}
