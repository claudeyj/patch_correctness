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
/*   0*/import perf.ManualWritePerfWithAllTypes;
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
/*   0*/    JavaType newType;
/* 345*/    Class<?> rawBase = baseType.getRawClass();
/* 346*/    if (rawBase == subclass) {
/* 347*/        return baseType; 
/*   0*/       }
/* 354*/    if (rawBase == Object.class) {
/* 355*/      newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/    } else {
/* 358*/      if (!rawBase.isAssignableFrom(subclass)) {
/* 359*/          throw new IllegalArgumentException(String.format("Class %s not subtype of %s", new Object[] { subclass.getName(), baseType })); 
/*   0*/         }
/* 365*/      if (baseType.getBindings().isEmpty()) {
/* 366*/        newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/      } else {
/* 370*/        if (baseType.isContainerType()) {
/* 371*/            if (baseType.isMapLikeType()) {
/* 372*/              if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
/* 376*/                newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
/* 428*/                return newType;
/*   0*/              } 
/*   0*/            } else if (baseType.isCollectionLikeType()) {
/*   0*/              if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
/*   0*/                newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
/* 428*/                return newType;
/*   0*/              } 
/*   0*/              if (rawBase == EnumSet.class) {
/*   0*/                  return baseType; 
/*   0*/                 }
/*   0*/            }  
/*   0*/           }
/*   0*/        int typeParamCount = (subclass.getTypeParameters()).length;
/*   0*/        if (typeParamCount == 0) {
/*   0*/          newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/        } else {
/*   0*/          TypeBindings tb = _bindingsForSubtype(baseType, typeParamCount, subclass);
/*   0*/          if (baseType.isInterface()) {
/*   0*/            newType = baseType.refine(subclass, tb, null, new JavaType[] { baseType });
/*   0*/          } else {
/*   0*/            newType = baseType.refine(subclass, tb, baseType, NO_TYPES);
/*   0*/          } 
/*   0*/          if (newType == null) {
/*   0*/              newType = _fromClass(null, subclass, tb); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 428*/    return newType;
/*   0*/  }
/*   0*/  
/*   0*/  private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass) {
/* 480*/    int baseCount = baseType.containedTypeCount();
/* 481*/    if (baseCount == typeParamCount) {
/* 482*/      if (typeParamCount == 1) {
/* 483*/          return TypeBindings.create(subclass, baseType.containedType(0)); 
/*   0*/         }
/* 485*/      if (typeParamCount == 2) {
/* 486*/          return TypeBindings.create(subclass, baseType.containedType(0), baseType.containedType(1)); 
/*   0*/         }
/* 489*/      List<JavaType> types = new ArrayList<>(baseCount);
/* 490*/      for (int i = 0; i < baseCount; i++) {
/* 491*/          types.add(baseType.containedType(i)); 
/*   0*/         }
/* 493*/      return TypeBindings.create(subclass, types);
/*   0*/    } 
/* 496*/    return TypeBindings.emptyBindings();
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
/* 512*/    Class<?> rawBase = baseType.getRawClass();
/* 513*/    if (rawBase == superClass) {
/* 514*/        return baseType; 
/*   0*/       }
/* 516*/    JavaType superType = baseType.findSuperType(superClass);
/* 517*/    if (superType == null) {
/* 519*/      if (!superClass.isAssignableFrom(rawBase)) {
/* 520*/          throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass.getName(), baseType })); 
/*   0*/         }
/* 524*/      throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass.getName(), baseType }));
/*   0*/    } 
/* 528*/    return superType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
/* 543*/    return this._parser.parse(canonical);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
/* 557*/    JavaType match = type.findSuperType(expType);
/* 558*/    if (match == null) {
/* 559*/        return NO_TYPES; 
/*   0*/       }
/* 561*/    return match.getBindings().typeParameterArray();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
/* 569*/    return findTypeParameters(constructType(clz, bindings), expType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
/* 577*/    return findTypeParameters(constructType(clz), expType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType moreSpecificType(JavaType type1, JavaType type2) {
/* 592*/    if (type1 == null) {
/* 593*/        return type2; 
/*   0*/       }
/* 595*/    if (type2 == null) {
/* 596*/        return type1; 
/*   0*/       }
/* 598*/    Class<?> raw1 = type1.getRawClass();
/* 599*/    Class<?> raw2 = type2.getRawClass();
/* 600*/    if (raw1 == raw2) {
/* 601*/        return type1; 
/*   0*/       }
/* 604*/    if (raw1.isAssignableFrom(raw2)) {
/* 605*/        return type2; 
/*   0*/       }
/* 607*/    return type1;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type) {
/* 617*/    return _fromAny(null, type, EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type, TypeBindings bindings) {
/* 621*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(TypeReference<?> typeRef) {
/* 627*/    return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, Class<?> contextClass) {
/* 653*/    TypeBindings bindings = (contextClass == null) ? TypeBindings.emptyBindings() : constructType(contextClass).getBindings();
/* 655*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, JavaType contextType) {
/* 663*/    TypeBindings bindings = (contextType == null) ? TypeBindings.emptyBindings() : contextType.getBindings();
/* 665*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(Class<?> elementType) {
/* 681*/    return ArrayType.construct(_fromAny(null, elementType, null), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(JavaType elementType) {
/* 691*/    return ArrayType.construct(elementType, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
/* 701*/    return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
/* 714*/    return (CollectionType)_fromClass(null, collectionClass, TypeBindings.create(collectionClass, elementType));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
/* 725*/    return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
/* 736*/    JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
/* 738*/    if (type instanceof CollectionLikeType) {
/* 739*/        return (CollectionLikeType)type; 
/*   0*/       }
/* 741*/    return CollectionLikeType.upgradeFrom(type, elementType);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*   0*/    JavaType kt, vt;
/* 752*/    if (mapClass == Properties.class) {
/* 753*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 755*/      kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/* 756*/      vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*   0*/    } 
/* 758*/    return constructMapType(mapClass, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
/* 768*/    return (MapType)_fromClass(null, mapClass, TypeBindings.create(mapClass, new JavaType[] { keyType, valueType }));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
/* 781*/    return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
/* 795*/    JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/* 797*/    if (type instanceof MapLikeType) {
/* 798*/        return (MapLikeType)type; 
/*   0*/       }
/* 800*/    return MapLikeType.upgradeFrom(type, keyType, valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
/* 809*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
/* 823*/    return constructSimpleType(rawType, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
/* 831*/    return ReferenceType.construct(rawType, null, null, null, referredType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType uncheckedSimpleType(Class<?> cls) {
/* 850*/    return _constructSimple(cls, EMPTY_BINDINGS, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
/* 881*/    int len = parameterClasses.length;
/* 882*/    JavaType[] pt = new JavaType[len];
/* 883*/    for (int i = 0; i < len; i++) {
/* 884*/        pt[i] = _fromClass(null, parameterClasses[i], null); 
/*   0*/       }
/* 886*/    return constructParametricType(parametrized, pt);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
/* 918*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
/* 927*/    return constructParametricType(parametrized, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
/* 936*/    return constructParametricType(parametrized, parameterClasses);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
/* 958*/    return constructCollectionType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
/* 973*/    return constructCollectionLikeType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructRawMapType(Class<? extends Map> mapClass) {
/* 988*/    return constructMapType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
/*1003*/    return constructMapLikeType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType vt, kt;
/*1018*/    if (rawClass == Properties.class) {
/*1019*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/*1021*/      List<JavaType> typeParams = bindings.getTypeParameters();
/*1023*/      switch (typeParams.size()) {
/*   0*/        case 0:
/*1025*/          kt = vt = _unknownType();
/*   0*/          break;
/*   0*/        case 2:
/*1028*/          kt = typeParams.get(0);
/*1029*/          vt = typeParams.get(1);
/*   0*/          break;
/*   0*/        default:
/*1032*/          throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/      } 
/*   0*/    } 
/*1035*/    return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1041*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1044*/    if (typeParams.isEmpty()) {
/*1045*/      ct = _unknownType();
/*1046*/    } else if (typeParams.size() == 1) {
/*1047*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1049*/      throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1051*/    return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1057*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1060*/    if (typeParams.isEmpty()) {
/*1061*/      ct = _unknownType();
/*1062*/    } else if (typeParams.size() == 1) {
/*1063*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1065*/      throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1067*/    return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1081*/    if (bindings.isEmpty()) {
/*1082*/      JavaType result = _findWellKnownSimple(raw);
/*1083*/      if (result != null) {
/*1084*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1087*/    return _newSimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1100*/    return new SimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _unknownType() {
/*1109*/    return CORE_TYPE_OBJECT;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findWellKnownSimple(Class<?> clz) {
/*1120*/    if (clz.isPrimitive()) {
/*1121*/      if (clz == CLS_BOOL) {
/*1121*/          return CORE_TYPE_BOOL; 
/*   0*/         }
/*1122*/      if (clz == CLS_INT) {
/*1122*/          return CORE_TYPE_INT; 
/*   0*/         }
/*1123*/      if (clz == CLS_LONG) {
/*1123*/          return CORE_TYPE_LONG; 
/*   0*/         }
/*   0*/    } else {
/*1125*/      if (clz == CLS_STRING) {
/*1125*/          return CORE_TYPE_STRING; 
/*   0*/         }
/*1126*/      if (clz == CLS_OBJECT) {
/*1126*/          return CORE_TYPE_OBJECT; 
/*   0*/         }
/*   0*/    } 
/*1128*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
/*   0*/    JavaType resultType;
/*1147*/    if (type instanceof Class) {
/*1149*/      resultType = _fromClass(context, (Class)type, EMPTY_BINDINGS);
/*1152*/    } else if (type instanceof ParameterizedType) {
/*1153*/      resultType = _fromParamType(context, (ParameterizedType)type, bindings);
/*   0*/    } else {
/*1155*/      if (type instanceof JavaType) {
/*1157*/          return (JavaType)type; 
/*   0*/         }
/*1159*/      if (type instanceof GenericArrayType) {
/*1160*/        resultType = _fromArrayType(context, (GenericArrayType)type, bindings);
/*1162*/      } else if (type instanceof TypeVariable) {
/*1163*/        resultType = _fromVariable(context, (TypeVariable)type, bindings);
/*1165*/      } else if (type instanceof WildcardType) {
/*1166*/        resultType = _fromWildcard(context, (WildcardType)type, bindings);
/*   0*/      } else {
/*1169*/        throw new IllegalArgumentException("Unrecognized Type: " + ((type == null) ? "[null]" : type.toString()));
/*   0*/      } 
/*   0*/    } 
/*1174*/    if (this._modifiers != null) {
/*1175*/      TypeBindings b = resultType.getBindings();
/*1176*/      if (b == null) {
/*1177*/          b = EMPTY_BINDINGS; 
/*   0*/         }
/*1179*/      for (TypeModifier mod : this._modifiers) {
/*1180*/        JavaType t = mod.modifyType(resultType, type, b, this);
/*1181*/        if (t == null) {
/*1182*/            throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[] { mod, mod.getClass().getName(), resultType })); 
/*   0*/           }
/*1186*/        resultType = t;
/*   0*/      } 
/*   0*/    } 
/*1189*/    return resultType;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
/*   0*/    Object key;
/*1199*/    JavaType result = _findWellKnownSimple(rawType);
/*1200*/    if (result != null) {
/*1201*/        return result; 
/*   0*/       }
/*1205*/    if (bindings == null || bindings.isEmpty()) {
/*1206*/      key = rawType;
/*   0*/    } else {
/*1208*/      Class<?> clazz = rawType;
/*1208*/      key = new ManualWritePerfWithAllTypes.AllTypes();
/*   0*/    } 
/*1210*/    result = this._typeCache.get(key);
/*1211*/    if (result != null) {
/*1212*/        return result; 
/*   0*/       }
/*1216*/    if (context == null) {
/*1217*/      context = new ClassStack(rawType);
/*   0*/    } else {
/*1219*/      ClassStack prev = context.find(rawType);
/*1220*/      if (prev != null) {
/*1222*/        ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/*1223*/        prev.addSelfReference(selfRef);
/*1224*/        return selfRef;
/*   0*/      } 
/*1227*/      context = context.child(rawType);
/*   0*/    } 
/*1231*/    if (rawType.isArray()) {
/*1232*/      result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*   0*/    } else {
/*   0*/      JavaType superClass, superInterfaces[];
/*1240*/      if (rawType.isInterface()) {
/*1241*/        superClass = null;
/*1242*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } else {
/*1245*/        superClass = _resolveSuperClass(context, rawType, bindings);
/*1246*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } 
/*1250*/      if (rawType == Properties.class) {
/*1251*/        result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*1256*/      } else if (superClass != null) {
/*1257*/        result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*   0*/      } 
/*1260*/      if (result == null) {
/*1261*/        result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/*1262*/        if (result == null) {
/*1263*/          result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/*1264*/          if (result == null) {
/*1266*/              result = _newSimpleType(rawType, bindings, superClass, superInterfaces); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1271*/    context.resolveSelfReferences(result);
/*1274*/    if (!result.hasHandlers()) {
/*1275*/        this._typeCache.putIfAbsent(key, result); 
/*   0*/       }
/*1277*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1282*/    Type parent = ClassUtil.getGenericSuperclass(rawType);
/*1283*/    if (parent == null) {
/*1284*/        return null; 
/*   0*/       }
/*1286*/    return _fromAny(context, parent, parentBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1291*/    Type[] types = ClassUtil.getGenericInterfaces(rawType);
/*1292*/    if (types == null || types.length == 0) {
/*1293*/        return NO_TYPES; 
/*   0*/       }
/*1295*/    int len = types.length;
/*1296*/    JavaType[] resolved = new JavaType[len];
/*1297*/    for (int i = 0; i < len; i++) {
/*1298*/      Type type = types[i];
/*1299*/      resolved[i] = _fromAny(context, type, parentBindings);
/*   0*/    } 
/*1301*/    return resolved;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1312*/    if (bindings == null) {
/*1313*/        bindings = TypeBindings.emptyBindings(); 
/*   0*/       }
/*1317*/    if (rawType == Map.class) {
/*1318*/        return _mapType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1320*/    if (rawType == Collection.class) {
/*1321*/        return _collectionType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1324*/    if (rawType == AtomicReference.class) {
/*1325*/        return _referenceType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1331*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1339*/    int intCount = superInterfaces.length;
/*1341*/    for (int i = 0; i < intCount; i++) {
/*1342*/      JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/*1343*/      if (result != null) {
/*1344*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1347*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
/*   0*/    TypeBindings newBindings;
/*1358*/    Class<?> rawType = (Class)ptype.getRawType();
/*1362*/    if (rawType == CLS_ENUM) {
/*1363*/        return CORE_TYPE_ENUM; 
/*   0*/       }
/*1365*/    if (rawType == CLS_COMPARABLE) {
/*1366*/        return CORE_TYPE_COMPARABLE; 
/*   0*/       }
/*1368*/    if (rawType == CLS_CLASS) {
/*1369*/        return CORE_TYPE_CLASS; 
/*   0*/       }
/*1375*/    Type[] args = ptype.getActualTypeArguments();
/*1376*/    int paramCount = (args == null) ? 0 : args.length;
/*1380*/    if (paramCount == 0) {
/*1381*/      newBindings = EMPTY_BINDINGS;
/*   0*/    } else {
/*1383*/      JavaType[] pt = new JavaType[paramCount];
/*1384*/      for (int i = 0; i < paramCount; i++) {
/*1385*/          pt[i] = _fromAny(context, args[i], parentBindings); 
/*   0*/         }
/*1387*/      newBindings = TypeBindings.create(rawType, pt);
/*   0*/    } 
/*1389*/    return _fromClass(context, rawType, newBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
/*1394*/    JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/*1395*/    return ArrayType.construct(elementType, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
/*1401*/    String name = var.getName();
/*1402*/    JavaType type = bindings.findBoundType(name);
/*1403*/    if (type != null) {
/*1404*/        return type; 
/*   0*/       }
/*1408*/    if (bindings.hasUnbound(name)) {
/*1409*/        return CORE_TYPE_OBJECT; 
/*   0*/       }
/*1411*/    bindings = bindings.withUnboundVariable(name);
/*1413*/    Type[] bounds = var.getBounds();
/*1414*/    return _fromAny(context, bounds[0], bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
/*1424*/    return _fromAny(context, type.getUpperBounds()[0], bindings);
/*   0*/  }
/*   0*/}
