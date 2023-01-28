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
/* 117*/  protected final LRUMap<Class<?>, JavaType> _typeCache = new LRUMap<Class<?>, JavaType>(16, 100);
/*   0*/  
/*   0*/  protected final TypeModifier[] _modifiers;
/*   0*/  
/*   0*/  protected final TypeParser _parser;
/*   0*/  
/*   0*/  protected final ClassLoader _classLoader;
/*   0*/  
/*   0*/  private TypeFactory() {
/* 145*/    this._parser = new TypeParser(this);
/* 146*/    this._modifiers = null;
/* 147*/    this._classLoader = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected TypeFactory(TypeParser p, TypeModifier[] mods) {
/* 151*/    this(p, mods, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected TypeFactory(TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
/* 156*/    this._parser = p.withFactory(this);
/* 157*/    this._modifiers = mods;
/* 158*/    this._classLoader = classLoader;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory withModifier(TypeModifier mod) {
/* 163*/    if (mod == null) {
/* 164*/        return new TypeFactory(this._parser, this._modifiers, this._classLoader); 
/*   0*/       }
/* 166*/    if (this._modifiers == null) {
/* 167*/        return new TypeFactory(this._parser, new TypeModifier[] { mod }, this._classLoader); 
/*   0*/       }
/* 169*/    return new TypeFactory(this._parser, ArrayBuilders.<TypeModifier>insertInListNoDup(this._modifiers, mod), this._classLoader);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory withClassLoader(ClassLoader classLoader) {
/* 173*/    return new TypeFactory(this._parser, this._modifiers, classLoader);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeFactory defaultInstance() {
/* 181*/    return instance;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearCache() {
/* 194*/    this._typeCache.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public ClassLoader getClassLoader() {
/* 198*/    return this._classLoader;
/*   0*/  }
/*   0*/  
/*   0*/  public static JavaType unknownType() {
/* 213*/    return defaultInstance()._unknownType();
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> rawClass(Type t) {
/* 223*/    if (t instanceof Class) {
/* 224*/        return (Class)t; 
/*   0*/       }
/* 227*/    return defaultInstance().constructType(t).getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> findClass(String className) throws ClassNotFoundException {
/* 244*/    if (className.indexOf('.') < 0) {
/* 245*/      Class<?> prim = _findPrimitive(className);
/* 246*/      if (prim != null) {
/* 247*/          return prim; 
/*   0*/         }
/*   0*/    } 
/* 251*/    Throwable prob = null;
/* 252*/    ClassLoader loader = getClassLoader();
/* 253*/    if (loader == null) {
/* 254*/        loader = Thread.currentThread().getContextClassLoader(); 
/*   0*/       }
/* 256*/    if (loader != null) {
/*   0*/        try {
/* 258*/          return classForName(className, true, loader);
/* 259*/        } catch (Exception e) {
/* 260*/          prob = ClassUtil.getRootCause(e);
/*   0*/        }  
/*   0*/       }
/*   0*/    try {
/* 264*/      return classForName(className);
/* 265*/    } catch (Exception e) {
/* 266*/      if (prob == null) {
/* 267*/          prob = ClassUtil.getRootCause(e); 
/*   0*/         }
/* 270*/      if (prob instanceof RuntimeException) {
/* 271*/          throw (RuntimeException)prob; 
/*   0*/         }
/* 273*/      throw new ClassNotFoundException(prob.getMessage(), prob);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
/* 278*/    return Class.forName(name, true, loader);
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> classForName(String name) throws ClassNotFoundException {
/* 282*/    return Class.forName(name);
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> _findPrimitive(String className) {
/* 287*/    if ("int".equals(className)) {
/* 287*/        return int.class; 
/*   0*/       }
/* 288*/    if ("long".equals(className)) {
/* 288*/        return long.class; 
/*   0*/       }
/* 289*/    if ("float".equals(className)) {
/* 289*/        return float.class; 
/*   0*/       }
/* 290*/    if ("double".equals(className)) {
/* 290*/        return double.class; 
/*   0*/       }
/* 291*/    if ("boolean".equals(className)) {
/* 291*/        return boolean.class; 
/*   0*/       }
/* 292*/    if ("byte".equals(className)) {
/* 292*/        return byte.class; 
/*   0*/       }
/* 293*/    if ("char".equals(className)) {
/* 293*/        return char.class; 
/*   0*/       }
/* 294*/    if ("short".equals(className)) {
/* 294*/        return short.class; 
/*   0*/       }
/* 295*/    if ("void".equals(className)) {
/* 295*/        return void.class; 
/*   0*/       }
/* 296*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
/*   0*/    JavaType newType;
/* 315*/    Class<?> rawBase = baseType.getRawClass();
/* 316*/    if (rawBase == subclass) {
/* 317*/        return baseType; 
/*   0*/       }
/* 324*/    if (rawBase == Object.class) {
/* 325*/      newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/    } else {
/* 328*/      if (!rawBase.isAssignableFrom(subclass)) {
/* 329*/          throw new IllegalArgumentException(String.format("Class %s not subtype of %s", new Object[] { subclass.getName(), baseType })); 
/*   0*/         }
/* 335*/      if (baseType.getBindings().isEmpty()) {
/* 336*/        newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/      } else {
/* 340*/        if (baseType.isContainerType()) {
/* 341*/          if (baseType.isMapLikeType()) {
/* 342*/            if (subclass != HashMap.class && subclass != LinkedHashMap.class && subclass != EnumMap.class) {
/* 342*/                TreeMap.class; 
/*   0*/               }
/* 346*/            newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
/* 408*/            return newType;
/*   0*/          } 
/*   0*/          if (baseType.isCollectionLikeType()) {
/*   0*/            if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
/*   0*/              newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
/* 408*/              return newType;
/*   0*/            } 
/*   0*/            if (rawBase == EnumSet.class) {
/*   0*/                return baseType; 
/*   0*/               }
/*   0*/          } 
/*   0*/        } 
/*   0*/        int typeParamCount = (subclass.getTypeParameters()).length;
/*   0*/        if (typeParamCount == 0) {
/*   0*/          newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/        } else {
/*   0*/          if (baseType.isInterface()) {
/*   0*/            newType = baseType.refine(subclass, TypeBindings.emptyBindings(), null, new JavaType[] { baseType });
/*   0*/          } else {
/*   0*/            newType = baseType.refine(subclass, TypeBindings.emptyBindings(), baseType, NO_TYPES);
/*   0*/          } 
/*   0*/          if (newType == null) {
/*   0*/            TypeBindings tb = null;
/*   0*/            if (baseType.containedTypeCount() == typeParamCount) {
/*   0*/                if (typeParamCount == 1) {
/*   0*/                  tb = TypeBindings.create(subclass, baseType.containedType(0));
/*   0*/                } else if (typeParamCount == 2) {
/*   0*/                  tb = TypeBindings.create(subclass, baseType.containedType(0), baseType.containedType(1));
/*   0*/                }  
/*   0*/               }
/*   0*/            newType = _fromClass(null, subclass, (tb == null) ? TypeBindings.emptyBindings() : tb);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 408*/    return newType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
/* 473*/    Class<?> rawBase = baseType.getRawClass();
/* 474*/    if (rawBase == superClass) {
/* 475*/        return baseType; 
/*   0*/       }
/* 477*/    JavaType superType = baseType.findSuperType(superClass);
/* 478*/    if (superType == null) {
/* 480*/      if (!superClass.isAssignableFrom(rawBase)) {
/* 481*/          throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass.getName(), baseType })); 
/*   0*/         }
/* 485*/      throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass.getName(), baseType }));
/*   0*/    } 
/* 489*/    return superType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
/* 504*/    return this._parser.parse(canonical);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
/* 518*/    JavaType match = type.findSuperType(expType);
/* 519*/    if (match == null) {
/* 520*/        return NO_TYPES; 
/*   0*/       }
/* 522*/    return match.getBindings().typeParameterArray();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
/* 530*/    return findTypeParameters(constructType(clz, bindings), expType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
/* 538*/    return findTypeParameters(constructType(clz), expType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType moreSpecificType(JavaType type1, JavaType type2) {
/* 553*/    if (type1 == null) {
/* 554*/        return type2; 
/*   0*/       }
/* 556*/    if (type2 == null) {
/* 557*/        return type1; 
/*   0*/       }
/* 559*/    Class<?> raw1 = type1.getRawClass();
/* 560*/    Class<?> raw2 = type2.getRawClass();
/* 561*/    if (raw1 == raw2) {
/* 562*/        return type1; 
/*   0*/       }
/* 565*/    if (raw1.isAssignableFrom(raw2)) {
/* 566*/        return type2; 
/*   0*/       }
/* 568*/    return type1;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type) {
/* 578*/    return _fromAny(null, type, EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type, TypeBindings bindings) {
/* 582*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(TypeReference<?> typeRef) {
/* 588*/    return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, Class<?> contextClass) {
/* 614*/    TypeBindings bindings = (contextClass == null) ? TypeBindings.emptyBindings() : constructType(contextClass).getBindings();
/* 616*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, JavaType contextType) {
/* 624*/    TypeBindings bindings = (contextType == null) ? TypeBindings.emptyBindings() : contextType.getBindings();
/* 626*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(Class<?> elementType) {
/* 642*/    return ArrayType.construct(_fromAny(null, elementType, null), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(JavaType elementType) {
/* 652*/    return ArrayType.construct(elementType, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
/* 662*/    return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
/* 675*/    return (CollectionType)_fromClass(null, collectionClass, TypeBindings.create(collectionClass, elementType));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
/* 686*/    return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
/* 697*/    JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
/* 699*/    if (type instanceof CollectionLikeType) {
/* 700*/        return (CollectionLikeType)type; 
/*   0*/       }
/* 702*/    return CollectionLikeType.upgradeFrom(type, elementType);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*   0*/    JavaType kt, vt;
/* 713*/    if (mapClass == Properties.class) {
/* 714*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 716*/      kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/* 717*/      vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*   0*/    } 
/* 719*/    return constructMapType(mapClass, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
/* 729*/    return (MapType)_fromClass(null, mapClass, TypeBindings.create(mapClass, new JavaType[] { keyType, valueType }));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
/* 742*/    return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
/* 756*/    JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/* 758*/    if (type instanceof MapLikeType) {
/* 759*/        return (MapLikeType)type; 
/*   0*/       }
/* 761*/    return MapLikeType.upgradeFrom(type, keyType, valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
/* 770*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
/* 784*/    return constructSimpleType(rawType, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
/* 792*/    return ReferenceType.construct(rawType, null, null, null, referredType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType uncheckedSimpleType(Class<?> cls) {
/* 806*/    return _constructSimple(cls, EMPTY_BINDINGS, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
/* 837*/    int len = parameterClasses.length;
/* 838*/    JavaType[] pt = new JavaType[len];
/* 839*/    for (int i = 0; i < len; i++) {
/* 840*/        pt[i] = _fromClass(null, parameterClasses[i], null); 
/*   0*/       }
/* 842*/    return constructParametricType(parametrized, pt);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
/* 874*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
/* 883*/    return constructParametricType(parametrized, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
/* 892*/    return constructParametricType(parametrized, parameterClasses);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
/* 914*/    return constructCollectionType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
/* 929*/    return constructCollectionLikeType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructRawMapType(Class<? extends Map> mapClass) {
/* 944*/    return constructMapType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
/* 959*/    return constructMapLikeType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType kt, vt;
/* 974*/    if (rawClass == Properties.class) {
/* 975*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 977*/      List<JavaType> typeParams = bindings.getTypeParameters();
/* 979*/      switch (typeParams.size()) {
/*   0*/        case 0:
/* 981*/          kt = vt = _unknownType();
/*   0*/          break;
/*   0*/        case 2:
/* 984*/          kt = typeParams.get(0);
/* 985*/          vt = typeParams.get(1);
/*   0*/          break;
/*   0*/        default:
/* 988*/          throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/      } 
/*   0*/    } 
/* 991*/    return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/* 997*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1000*/    if (typeParams.isEmpty()) {
/*1001*/      ct = _unknownType();
/*1002*/    } else if (typeParams.size() == 1) {
/*1003*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1005*/      throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1007*/    return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/*1013*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1016*/    if (typeParams.isEmpty()) {
/*1017*/      ct = _unknownType();
/*1018*/    } else if (typeParams.size() == 1) {
/*1019*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1021*/      throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1023*/    return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1037*/    if (bindings.isEmpty()) {
/*1038*/      JavaType result = _findWellKnownSimple(raw);
/*1039*/      if (result != null) {
/*1040*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1043*/    return _newSimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1056*/    return new SimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _unknownType() {
/*1065*/    return CORE_TYPE_OBJECT;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findWellKnownSimple(Class<?> clz) {
/*1076*/    if (clz.isPrimitive()) {
/*1077*/      if (clz == CLS_BOOL) {
/*1077*/          return CORE_TYPE_BOOL; 
/*   0*/         }
/*1078*/      if (clz == CLS_INT) {
/*1078*/          return CORE_TYPE_INT; 
/*   0*/         }
/*1079*/      if (clz == CLS_LONG) {
/*1079*/          return CORE_TYPE_LONG; 
/*   0*/         }
/*   0*/    } else {
/*1081*/      if (clz == CLS_STRING) {
/*1081*/          return CORE_TYPE_STRING; 
/*   0*/         }
/*1082*/      if (clz == CLS_OBJECT) {
/*1082*/          return CORE_TYPE_OBJECT; 
/*   0*/         }
/*   0*/    } 
/*1084*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
/*   0*/    JavaType resultType;
/*1103*/    if (type instanceof Class) {
/*1105*/      resultType = _fromClass(context, (Class)type, EMPTY_BINDINGS);
/*1108*/    } else if (type instanceof ParameterizedType) {
/*1109*/      resultType = _fromParamType(context, (ParameterizedType)type, bindings);
/*   0*/    } else {
/*1111*/      if (type instanceof JavaType) {
/*1113*/          return (JavaType)type; 
/*   0*/         }
/*1115*/      if (type instanceof GenericArrayType) {
/*1116*/        resultType = _fromArrayType(context, (GenericArrayType)type, bindings);
/*1118*/      } else if (type instanceof TypeVariable) {
/*1119*/        resultType = _fromVariable(context, (TypeVariable)type, bindings);
/*1121*/      } else if (type instanceof WildcardType) {
/*1122*/        resultType = _fromWildcard(context, (WildcardType)type, bindings);
/*   0*/      } else {
/*1125*/        throw new IllegalArgumentException("Unrecognized Type: " + ((type == null) ? "[null]" : type.toString()));
/*   0*/      } 
/*   0*/    } 
/*1130*/    if (this._modifiers != null) {
/*1131*/      TypeBindings b = resultType.getBindings();
/*1132*/      if (b == null) {
/*1133*/          b = EMPTY_BINDINGS; 
/*   0*/         }
/*1135*/      for (TypeModifier mod : this._modifiers) {
/*1136*/        JavaType t = mod.modifyType(resultType, type, b, this);
/*1137*/        if (t == null) {
/*1138*/            throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[] { mod, mod.getClass().getName(), resultType })); 
/*   0*/           }
/*1142*/        resultType = t;
/*   0*/      } 
/*   0*/    } 
/*1145*/    return resultType;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
/*1155*/    JavaType result = _findWellKnownSimple(rawType);
/*1156*/    if (result != null) {
/*1157*/        return result; 
/*   0*/       }
/*1160*/    boolean cachable = (bindings == null || bindings.isEmpty());
/*1161*/    if (cachable) {
/*1162*/      result = this._typeCache.get(rawType);
/*1163*/      if (result != null) {
/*1164*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1169*/    if (context == null) {
/*1170*/      context = new ClassStack(rawType);
/*   0*/    } else {
/*1172*/      ClassStack prev = context.find(rawType);
/*1173*/      if (prev != null) {
/*1175*/        ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/*1176*/        prev.addSelfReference(selfRef);
/*1177*/        return selfRef;
/*   0*/      } 
/*1180*/      context = context.child(rawType);
/*   0*/    } 
/*1184*/    if (rawType.isArray()) {
/*1185*/      result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*   0*/    } else {
/*   0*/      JavaType superClass, superInterfaces[];
/*1193*/      if (rawType.isInterface()) {
/*1194*/        superClass = null;
/*1195*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } else {
/*1198*/        superClass = _resolveSuperClass(context, rawType, bindings);
/*1199*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } 
/*1203*/      if (rawType == Properties.class) {
/*1204*/        result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*1209*/      } else if (superClass != null) {
/*1210*/        result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*   0*/      } 
/*1213*/      if (result == null) {
/*1214*/        result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/*1215*/        if (result == null) {
/*1216*/          result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/*1217*/          if (result == null) {
/*1219*/              result = _newSimpleType(rawType, bindings, superClass, superInterfaces); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1224*/    context.resolveSelfReferences(result);
/*1225*/    if (cachable) {
/*1226*/        this._typeCache.putIfAbsent(rawType, result); 
/*   0*/       }
/*1228*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1233*/    Type parent = ClassUtil.getGenericSuperclass(rawType);
/*1234*/    if (parent == null) {
/*1235*/        return null; 
/*   0*/       }
/*1237*/    return _fromAny(context, parent, parentBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1242*/    Type[] types = ClassUtil.getGenericInterfaces(rawType);
/*1243*/    if (types == null || types.length == 0) {
/*1244*/        return NO_TYPES; 
/*   0*/       }
/*1246*/    int len = types.length;
/*1247*/    JavaType[] resolved = new JavaType[len];
/*1248*/    for (int i = 0; i < len; i++) {
/*1249*/      Type type = types[i];
/*1250*/      resolved[i] = _fromAny(context, type, parentBindings);
/*   0*/    } 
/*1252*/    return resolved;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1264*/    if (rawType == Map.class) {
/*1265*/        return _mapType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1267*/    if (rawType == Collection.class) {
/*1268*/        return _collectionType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1271*/    if (rawType == AtomicReference.class) {
/*1272*/        return _referenceType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1278*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1286*/    int intCount = superInterfaces.length;
/*1288*/    for (int i = 0; i < intCount; i++) {
/*1289*/      JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/*1290*/      if (result != null) {
/*1291*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1294*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
/*   0*/    TypeBindings newBindings;
/*1305*/    Class<?> rawType = (Class)ptype.getRawType();
/*1309*/    if (rawType == CLS_ENUM) {
/*1310*/        return CORE_TYPE_ENUM; 
/*   0*/       }
/*1312*/    if (rawType == CLS_COMPARABLE) {
/*1313*/        return CORE_TYPE_COMPARABLE; 
/*   0*/       }
/*1315*/    if (rawType == CLS_CLASS) {
/*1316*/        return CORE_TYPE_CLASS; 
/*   0*/       }
/*1322*/    Type[] args = ptype.getActualTypeArguments();
/*1323*/    int paramCount = (args == null) ? 0 : args.length;
/*1327*/    if (paramCount == 0) {
/*1328*/      newBindings = EMPTY_BINDINGS;
/*   0*/    } else {
/*1330*/      JavaType[] pt = new JavaType[paramCount];
/*1331*/      for (int i = 0; i < paramCount; i++) {
/*1332*/          pt[i] = _fromAny(context, args[i], parentBindings); 
/*   0*/         }
/*1334*/      newBindings = TypeBindings.create(rawType, pt);
/*   0*/    } 
/*1336*/    return _fromClass(context, rawType, newBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
/*1341*/    JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/*1342*/    return ArrayType.construct(elementType, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
/*1348*/    String name = var.getName();
/*1349*/    JavaType type = bindings.findBoundType(name);
/*1350*/    if (type != null) {
/*1351*/        return type; 
/*   0*/       }
/*1355*/    if (bindings.hasUnbound(name)) {
/*1356*/        return CORE_TYPE_OBJECT; 
/*   0*/       }
/*1358*/    bindings = bindings.withUnboundVariable(name);
/*1360*/    Type[] bounds = var.getBounds();
/*1361*/    return _fromAny(context, bounds[0], bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
/*1371*/    return _fromAny(context, type.getUpperBounds()[0], bindings);
/*   0*/  }
/*   0*/}
