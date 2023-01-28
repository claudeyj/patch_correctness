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
/* 428*/                javaType = javaType.withHandlersFrom(baseType);
/* 429*/                return javaType;
/*   0*/              } 
/*   0*/            } else if (baseType.isCollectionLikeType()) {
/*   0*/              if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
/*   0*/                javaType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
/*   0*/                javaType = javaType.withHandlersFrom(baseType);
/* 429*/                return javaType;
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
/*   0*/          if (baseType.isInterface()) {
/*   0*/            javaType = baseType.refine(subclass, tb, null, new JavaType[] { baseType });
/*   0*/          } else {
/*   0*/            javaType = baseType.refine(subclass, tb, baseType, NO_TYPES);
/*   0*/          } 
/*   0*/          if (javaType == null) {
/*   0*/              javaType = _fromClass(null, subclass, tb); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    JavaType javaType = javaType.withHandlersFrom(baseType);
/* 429*/    return javaType;
/*   0*/  }
/*   0*/  
/*   0*/  private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass) {
/* 481*/    int baseCount = baseType.containedTypeCount();
/* 482*/    if (baseCount == typeParamCount) {
/* 483*/      if (typeParamCount == 1) {
/* 484*/          return TypeBindings.create(subclass, baseType.containedType(0)); 
/*   0*/         }
/* 486*/      if (typeParamCount == 2) {
/* 487*/          return TypeBindings.create(subclass, baseType.containedType(0), baseType.containedType(1)); 
/*   0*/         }
/* 490*/      List<JavaType> types = new ArrayList<>(baseCount);
/* 491*/      for (int i = 0; i < baseCount; i++) {
/* 492*/          types.add(baseType.containedType(i)); 
/*   0*/         }
/* 494*/      return TypeBindings.create(subclass, types);
/*   0*/    } 
/* 497*/    return TypeBindings.emptyBindings();
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
/* 513*/    Class<?> rawBase = baseType.getRawClass();
/* 514*/    if (rawBase == superClass) {
/* 515*/        return baseType; 
/*   0*/       }
/* 517*/    JavaType superType = baseType.findSuperType(superClass);
/* 518*/    if (superType == null) {
/* 520*/      if (!superClass.isAssignableFrom(rawBase)) {
/* 521*/          throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass.getName(), baseType })); 
/*   0*/         }
/* 525*/      throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass.getName(), baseType }));
/*   0*/    } 
/* 529*/    return superType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
/* 544*/    return this._parser.parse(canonical);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
/* 558*/    JavaType match = type.findSuperType(expType);
/* 559*/    if (match == null) {
/* 560*/        return NO_TYPES; 
/*   0*/       }
/* 562*/    return match.getBindings().typeParameterArray();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
/* 570*/    return findTypeParameters(constructType(clz, bindings), expType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
/* 578*/    return findTypeParameters(constructType(clz), expType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType moreSpecificType(JavaType type1, JavaType type2) {
/* 593*/    if (type1 == null) {
/* 594*/        return type2; 
/*   0*/       }
/* 596*/    if (type2 == null) {
/* 597*/        return type1; 
/*   0*/       }
/* 599*/    Class<?> raw1 = type1.getRawClass();
/* 600*/    Class<?> raw2 = type2.getRawClass();
/* 601*/    if (raw1 == raw2) {
/* 602*/        return type1; 
/*   0*/       }
/* 605*/    if (raw1.isAssignableFrom(raw2)) {
/* 606*/        return type2; 
/*   0*/       }
/* 608*/    return type1;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type) {
/* 618*/    return _fromAny(null, type, EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type, TypeBindings bindings) {
/* 622*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(TypeReference<?> typeRef) {
/* 628*/    return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, Class<?> contextClass) {
/* 654*/    JavaType contextType = (contextClass == null) ? null : constructType(contextClass);
/* 655*/    return constructType(type, contextType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, JavaType contextType) {
/*   0*/    TypeBindings bindings;
/* 664*/    if (contextType == null) {
/* 665*/      bindings = TypeBindings.emptyBindings();
/*   0*/    } else {
/* 667*/      bindings = contextType.getBindings();
/* 672*/      if (type.getClass() != Class.class) {
/* 675*/          while (bindings.isEmpty()) {
/* 676*/            contextType = contextType.getSuperClass();
/* 677*/            if (contextType == null) {
/*   0*/                break; 
/*   0*/               }
/* 680*/            bindings = contextType.getBindings();
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 684*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(Class<?> elementType) {
/* 700*/    return ArrayType.construct(_fromAny(null, elementType, null), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(JavaType elementType) {
/* 710*/    return ArrayType.construct(elementType, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
/* 721*/    return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
/* 735*/    return (CollectionType)_fromClass(null, collectionClass, TypeBindings.create(collectionClass, elementType));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
/* 746*/    return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
/* 757*/    JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
/* 759*/    if (type instanceof CollectionLikeType) {
/* 760*/        return (CollectionLikeType)type; 
/*   0*/       }
/* 762*/    return CollectionLikeType.upgradeFrom(type, elementType);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*   0*/    JavaType kt, vt;
/* 774*/    if (mapClass == Properties.class) {
/* 775*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 777*/      kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/* 778*/      vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*   0*/    } 
/* 780*/    return constructMapType(mapClass, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
/* 790*/    return (MapType)_fromClass(null, mapClass, TypeBindings.create(mapClass, keyType, valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
/* 801*/    return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
/* 815*/    JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/* 817*/    if (type instanceof MapLikeType) {
/* 818*/        return (MapLikeType)type; 
/*   0*/       }
/* 820*/    return MapLikeType.upgradeFrom(type, keyType, valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
/* 829*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
/* 843*/    return constructSimpleType(rawType, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
/* 851*/    return ReferenceType.construct(rawType, null, null, null, referredType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType uncheckedSimpleType(Class<?> cls) {
/* 870*/    return _constructSimple(cls, EMPTY_BINDINGS, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
/* 901*/    int len = parameterClasses.length;
/* 902*/    JavaType[] pt = new JavaType[len];
/* 903*/    for (int i = 0; i < len; i++) {
/* 904*/        pt[i] = _fromClass(null, parameterClasses[i], null); 
/*   0*/       }
/* 906*/    return constructParametricType(parametrized, pt);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
/* 938*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
/* 947*/    return constructParametricType(parametrized, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
/* 956*/    return constructParametricType(parametrized, parameterClasses);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
/* 978*/    return constructCollectionType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
/* 993*/    return constructCollectionLikeType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructRawMapType(Class<? extends Map> mapClass) {
/*1008*/    return constructMapType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
/*1023*/    return constructMapLikeType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType kt, vt;
/*1038*/    if (rawClass == Properties.class) {
/*1039*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/*1041*/      List<JavaType> typeParams = bindings.getTypeParameters();
/*1043*/      switch (typeParams.size()) {
/*   0*/        case 0:
/*1045*/          kt = vt = _unknownType();
/*   0*/          break;
/*   0*/        case 2:
/*1048*/          kt = typeParams.get(0);
/*1049*/          vt = typeParams.get(1);
/*   0*/          break;
/*   0*/        default:
/*1052*/          throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/      } 
/*   0*/    } 
/*1055*/    return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1061*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1064*/    if (typeParams.isEmpty()) {
/*1065*/      ct = _unknownType();
/*1066*/    } else if (typeParams.size() == 1) {
/*1067*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1069*/      throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1071*/    return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1077*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1080*/    if (typeParams.isEmpty()) {
/*1081*/      ct = _unknownType();
/*1082*/    } else if (typeParams.size() == 1) {
/*1083*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1085*/      throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1087*/    return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1101*/    if (bindings.isEmpty()) {
/*1102*/      JavaType result = _findWellKnownSimple(raw);
/*1103*/      if (result != null) {
/*1104*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1107*/    return _newSimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1120*/    return new SimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _unknownType() {
/*1129*/    return CORE_TYPE_OBJECT;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findWellKnownSimple(Class<?> clz) {
/*1140*/    if (clz.isPrimitive()) {
/*1141*/      if (clz == CLS_BOOL) {
/*1141*/          return CORE_TYPE_BOOL; 
/*   0*/         }
/*1142*/      if (clz == CLS_INT) {
/*1142*/          return CORE_TYPE_INT; 
/*   0*/         }
/*1143*/      if (clz == CLS_LONG) {
/*1143*/          return CORE_TYPE_LONG; 
/*   0*/         }
/*   0*/    } else {
/*1145*/      if (clz == CLS_STRING) {
/*1145*/          return CORE_TYPE_STRING; 
/*   0*/         }
/*1146*/      if (clz == CLS_OBJECT) {
/*1146*/          return CORE_TYPE_OBJECT; 
/*   0*/         }
/*   0*/    } 
/*1148*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
/*   0*/    JavaType resultType;
/*1167*/    if (type instanceof Class) {
/*1169*/      resultType = _fromClass(context, (Class)type, EMPTY_BINDINGS);
/*1172*/    } else if (type instanceof ParameterizedType) {
/*1173*/      resultType = _fromParamType(context, (ParameterizedType)type, bindings);
/*   0*/    } else {
/*1175*/      if (type instanceof JavaType) {
/*1177*/          return (JavaType)type; 
/*   0*/         }
/*1179*/      if (type instanceof GenericArrayType) {
/*1180*/        resultType = _fromArrayType(context, (GenericArrayType)type, bindings);
/*1182*/      } else if (type instanceof TypeVariable) {
/*1183*/        resultType = _fromVariable(context, (TypeVariable)type, bindings);
/*1185*/      } else if (type instanceof WildcardType) {
/*1186*/        resultType = _fromWildcard(context, (WildcardType)type, bindings);
/*   0*/      } else {
/*1189*/        throw new IllegalArgumentException("Unrecognized Type: " + ((type == null) ? "[null]" : type.toString()));
/*   0*/      } 
/*   0*/    } 
/*1194*/    if (this._modifiers != null) {
/*1195*/      TypeBindings b = resultType.getBindings();
/*1196*/      if (b == null) {
/*1197*/          b = EMPTY_BINDINGS; 
/*   0*/         }
/*1199*/      for (TypeModifier mod : this._modifiers) {
/*1200*/        JavaType t = mod.modifyType(resultType, type, b, this);
/*1201*/        if (t == null) {
/*1202*/            throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[] { mod, mod.getClass().getName(), resultType })); 
/*   0*/           }
/*1206*/        resultType = t;
/*   0*/      } 
/*   0*/    } 
/*1209*/    return resultType;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
/*   0*/    Object key;
/*1219*/    JavaType result = _findWellKnownSimple(rawType);
/*1220*/    if (result != null) {
/*1221*/        return result; 
/*   0*/       }
/*1225*/    if (bindings == null || bindings.isEmpty()) {
/*1226*/      key = rawType;
/*   0*/    } else {
/*1228*/      key = bindings.asKey(rawType);
/*   0*/    } 
/*1230*/    result = this._typeCache.get(key);
/*1231*/    if (result != null) {
/*1232*/        return result; 
/*   0*/       }
/*1236*/    if (context == null) {
/*1237*/      context = new ClassStack(rawType);
/*   0*/    } else {
/*1239*/      ClassStack prev = context.find(rawType);
/*1240*/      if (prev != null) {
/*1242*/        ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/*1243*/        prev.addSelfReference(selfRef);
/*1244*/        return selfRef;
/*   0*/      } 
/*1247*/      context = context.child(rawType);
/*   0*/    } 
/*1251*/    if (rawType.isArray()) {
/*1252*/      result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*   0*/    } else {
/*   0*/      JavaType superClass, superInterfaces[];
/*1260*/      if (rawType.isInterface()) {
/*1261*/        superClass = null;
/*1262*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } else {
/*1265*/        superClass = _resolveSuperClass(context, rawType, bindings);
/*1266*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } 
/*1270*/      if (rawType == Properties.class) {
/*1271*/        result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*1276*/      } else if (superClass != null) {
/*1277*/        result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*   0*/      } 
/*1280*/      if (result == null) {
/*1281*/        result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/*1282*/        if (result == null) {
/*1283*/          result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/*1284*/          if (result == null) {
/*1286*/              result = _newSimpleType(rawType, bindings, superClass, superInterfaces); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1291*/    context.resolveSelfReferences(result);
/*1294*/    if (!result.hasHandlers()) {
/*1295*/        this._typeCache.putIfAbsent(key, result); 
/*   0*/       }
/*1297*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1302*/    Type parent = ClassUtil.getGenericSuperclass(rawType);
/*1303*/    if (parent == null) {
/*1304*/        return null; 
/*   0*/       }
/*1306*/    return _fromAny(context, parent, parentBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1311*/    Type[] types = ClassUtil.getGenericInterfaces(rawType);
/*1312*/    if (types == null || types.length == 0) {
/*1313*/        return NO_TYPES; 
/*   0*/       }
/*1315*/    int len = types.length;
/*1316*/    JavaType[] resolved = new JavaType[len];
/*1317*/    for (int i = 0; i < len; i++) {
/*1318*/      Type type = types[i];
/*1319*/      resolved[i] = _fromAny(context, type, parentBindings);
/*   0*/    } 
/*1321*/    return resolved;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1332*/    if (bindings == null) {
/*1333*/        bindings = TypeBindings.emptyBindings(); 
/*   0*/       }
/*1337*/    if (rawType == Map.class) {
/*1338*/        return _mapType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1340*/    if (rawType == Collection.class) {
/*1341*/        return _collectionType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1344*/    if (rawType == AtomicReference.class) {
/*1345*/        return _referenceType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1351*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1359*/    int intCount = superInterfaces.length;
/*1361*/    for (int i = 0; i < intCount; i++) {
/*1362*/      JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/*1363*/      if (result != null) {
/*1364*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1367*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
/*   0*/    TypeBindings newBindings;
/*1378*/    Class<?> rawType = (Class)ptype.getRawType();
/*1382*/    if (rawType == CLS_ENUM) {
/*1383*/        return CORE_TYPE_ENUM; 
/*   0*/       }
/*1385*/    if (rawType == CLS_COMPARABLE) {
/*1386*/        return CORE_TYPE_COMPARABLE; 
/*   0*/       }
/*1388*/    if (rawType == CLS_CLASS) {
/*1389*/        return CORE_TYPE_CLASS; 
/*   0*/       }
/*1395*/    Type[] args = ptype.getActualTypeArguments();
/*1396*/    int paramCount = (args == null) ? 0 : args.length;
/*1400*/    if (paramCount == 0) {
/*1401*/      newBindings = EMPTY_BINDINGS;
/*   0*/    } else {
/*1403*/      JavaType[] pt = new JavaType[paramCount];
/*1404*/      for (int i = 0; i < paramCount; i++) {
/*1405*/          pt[i] = _fromAny(context, args[i], parentBindings); 
/*   0*/         }
/*1407*/      newBindings = TypeBindings.create(rawType, pt);
/*   0*/    } 
/*1409*/    return _fromClass(context, rawType, newBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
/*1414*/    JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/*1415*/    return ArrayType.construct(elementType, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
/*1421*/    String name = var.getName();
/*1422*/    JavaType type = bindings.findBoundType(name);
/*1423*/    if (type != null) {
/*1424*/        return type; 
/*   0*/       }
/*1428*/    if (bindings.hasUnbound(name)) {
/*1429*/        return CORE_TYPE_OBJECT; 
/*   0*/       }
/*1431*/    bindings = bindings.withUnboundVariable(name);
/*1433*/    Type[] bounds = var.getBounds();
/*1434*/    return _fromAny(context, bounds[0], bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
/*1444*/    return _fromAny(context, type.getUpperBounds()[0], bindings);
/*   0*/  }
/*   0*/}
