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
/* 201*/    return this._classLoader;
/*   0*/  }
/*   0*/  
/*   0*/  public static JavaType unknownType() {
/* 216*/    return defaultInstance()._unknownType();
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> rawClass(Type t) {
/* 226*/    if (t instanceof Class) {
/* 227*/        return (Class)t; 
/*   0*/       }
/* 230*/    return defaultInstance().constructType(t).getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> findClass(String className) throws ClassNotFoundException {
/* 247*/    if (className.indexOf('.') < 0) {
/* 248*/      Class<?> prim = _findPrimitive(className);
/* 249*/      if (prim != null) {
/* 250*/          return prim; 
/*   0*/         }
/*   0*/    } 
/* 254*/    Throwable prob = null;
/* 255*/    ClassLoader loader = getClassLoader();
/* 256*/    if (loader == null) {
/* 257*/        loader = Thread.currentThread().getContextClassLoader(); 
/*   0*/       }
/* 259*/    if (loader != null) {
/*   0*/        try {
/* 261*/          return classForName(className, true, loader);
/* 262*/        } catch (Exception e) {
/* 263*/          prob = ClassUtil.getRootCause(e);
/*   0*/        }  
/*   0*/       }
/*   0*/    try {
/* 267*/      return classForName(className);
/* 268*/    } catch (Exception e) {
/* 269*/      if (prob == null) {
/* 270*/          prob = ClassUtil.getRootCause(e); 
/*   0*/         }
/* 273*/      if (prob instanceof RuntimeException) {
/* 274*/          throw (RuntimeException)prob; 
/*   0*/         }
/* 276*/      throw new ClassNotFoundException(prob.getMessage(), prob);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
/* 281*/    return Class.forName(name, true, loader);
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> classForName(String name) throws ClassNotFoundException {
/* 285*/    return Class.forName(name);
/*   0*/  }
/*   0*/  
/*   0*/  protected Class<?> _findPrimitive(String className) {
/* 290*/    if ("int".equals(className)) {
/* 290*/        return int.class; 
/*   0*/       }
/* 291*/    if ("long".equals(className)) {
/* 291*/        return long.class; 
/*   0*/       }
/* 292*/    if ("float".equals(className)) {
/* 292*/        return float.class; 
/*   0*/       }
/* 293*/    if ("double".equals(className)) {
/* 293*/        return double.class; 
/*   0*/       }
/* 294*/    if ("boolean".equals(className)) {
/* 294*/        return boolean.class; 
/*   0*/       }
/* 295*/    if ("byte".equals(className)) {
/* 295*/        return byte.class; 
/*   0*/       }
/* 296*/    if ("char".equals(className)) {
/* 296*/        return char.class; 
/*   0*/       }
/* 297*/    if ("short".equals(className)) {
/* 297*/        return short.class; 
/*   0*/       }
/* 298*/    if ("void".equals(className)) {
/* 298*/        return void.class; 
/*   0*/       }
/* 299*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
/*   0*/    JavaType newType;
/* 318*/    Class<?> rawBase = baseType.getRawClass();
/* 319*/    if (rawBase == subclass) {
/* 320*/        return baseType; 
/*   0*/       }
/* 327*/    if (rawBase == Object.class) {
/* 328*/      newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/    } else {
/* 331*/      if (!rawBase.isAssignableFrom(subclass)) {
/* 332*/          throw new IllegalArgumentException(String.format("Class %s not subtype of %s", new Object[] { subclass.getName(), baseType })); 
/*   0*/         }
/* 338*/      if (baseType.getBindings().isEmpty()) {
/* 339*/        newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/      } else {
/* 343*/        if (baseType.isContainerType()) {
/* 344*/            if (baseType.isMapLikeType()) {
/* 345*/              if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
/* 349*/                newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
/* 400*/                return newType;
/*   0*/              } 
/*   0*/            } else if (baseType.isCollectionLikeType()) {
/*   0*/              if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
/*   0*/                newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
/* 400*/                return newType;
/*   0*/              } 
/*   0*/              if (rawBase == EnumSet.class) {
/*   0*/                  return baseType; 
/*   0*/                 }
/*   0*/            }  
/*   0*/           }
/*   0*/        if ((subclass.getTypeParameters()).length == 0) {
/*   0*/          newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*   0*/        } else {
/*   0*/          if (baseType.isInterface()) {
/*   0*/            newType = baseType.refine(subclass, TypeBindings.emptyBindings(), null, new JavaType[] { baseType });
/*   0*/          } else {
/*   0*/            newType = baseType.refine(subclass, TypeBindings.emptyBindings(), baseType, NO_TYPES);
/*   0*/          } 
/*   0*/          if (newType == null) {
/*   0*/              newType = _fromClass(null, subclass, TypeBindings.emptyBindings()); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 400*/    return newType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
/* 461*/    Class<?> rawBase = baseType.getRawClass();
/* 462*/    if (rawBase == superClass) {
/* 463*/        return baseType; 
/*   0*/       }
/* 465*/    JavaType superType = baseType.findSuperType(superClass);
/* 466*/    if (superType == null) {
/* 468*/      if (!superClass.isAssignableFrom(rawBase)) {
/* 469*/          throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass.getName(), baseType })); 
/*   0*/         }
/* 473*/      throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass.getName(), baseType }));
/*   0*/    } 
/* 477*/    return superType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
/* 492*/    return this._parser.parse(canonical);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
/* 506*/    JavaType match = type.findSuperType(expType);
/* 507*/    if (match == null) {
/* 508*/        return NO_TYPES; 
/*   0*/       }
/* 510*/    return match.getBindings().typeParameterArray();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
/* 518*/    return findTypeParameters(constructType(clz, bindings), expType);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
/* 526*/    return findTypeParameters(constructType(clz), expType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType moreSpecificType(JavaType type1, JavaType type2) {
/* 541*/    if (type1 == null) {
/* 542*/        return type2; 
/*   0*/       }
/* 544*/    if (type2 == null) {
/* 545*/        return type1; 
/*   0*/       }
/* 547*/    Class<?> raw1 = type1.getRawClass();
/* 548*/    Class<?> raw2 = type2.getRawClass();
/* 549*/    if (raw1 == raw2) {
/* 550*/        return type1; 
/*   0*/       }
/* 553*/    if (raw1.isAssignableFrom(raw2)) {
/* 554*/        return type2; 
/*   0*/       }
/* 556*/    return type1;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type) {
/* 566*/    return _fromAny(null, type, EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type type, TypeBindings bindings) {
/* 570*/    return _fromAny(null, type, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(TypeReference<?> typeRef) {
/* 576*/    return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, Class<?> contextClass) {
/* 602*/    return constructType(type, constructType(CLS_OBJECT));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructType(Type type, JavaType contextType) {
/* 610*/    return _fromAny(null, type, contextType.getBindings());
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(Class<?> elementType) {
/* 626*/    return ArrayType.construct(_fromAny(null, elementType, null), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayType constructArrayType(JavaType elementType) {
/* 636*/    return ArrayType.construct(elementType, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
/* 646*/    return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
/* 659*/    return (CollectionType)_fromClass(null, collectionClass, TypeBindings.create(collectionClass, elementType));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
/* 670*/    return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
/* 681*/    JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
/* 683*/    if (type instanceof CollectionLikeType) {
/* 684*/        return (CollectionLikeType)type; 
/*   0*/       }
/* 686*/    return CollectionLikeType.upgradeFrom(type, elementType);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
/*   0*/    JavaType kt, vt;
/* 697*/    if (mapClass == Properties.class) {
/* 698*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 700*/      kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/* 701*/      vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*   0*/    } 
/* 703*/    return constructMapType(mapClass, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
/* 713*/    return (MapType)_fromClass(null, mapClass, TypeBindings.create(mapClass, new JavaType[] { keyType, valueType }));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
/* 726*/    return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
/* 740*/    JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/* 742*/    if (type instanceof MapLikeType) {
/* 743*/        return (MapLikeType)type; 
/*   0*/       }
/* 745*/    return MapLikeType.upgradeFrom(type, keyType, valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
/* 754*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
/* 768*/    return constructSimpleType(rawType, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
/* 776*/    return ReferenceType.construct(rawType, null, null, null, referredType);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType uncheckedSimpleType(Class<?> cls) {
/* 790*/    return _constructSimple(cls, EMPTY_BINDINGS, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
/* 821*/    int len = parameterClasses.length;
/* 822*/    JavaType[] pt = new JavaType[len];
/* 823*/    for (int i = 0; i < len; i++) {
/* 824*/        pt[i] = _fromClass(null, parameterClasses[i], null); 
/*   0*/       }
/* 826*/    return constructParametricType(parametrized, pt);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
/* 858*/    return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
/* 867*/    return constructParametricType(parametrized, parameterTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
/* 876*/    return constructParametricType(parametrized, parameterClasses);
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
/* 898*/    return constructCollectionType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
/* 913*/    return constructCollectionLikeType(collectionClass, unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapType constructRawMapType(Class<? extends Map> mapClass) {
/* 928*/    return constructMapType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
/* 943*/    return constructMapLikeType(mapClass, unknownType(), unknownType());
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType kt, vt;
/* 958*/    if (rawClass == Properties.class) {
/* 959*/      kt = vt = CORE_TYPE_STRING;
/*   0*/    } else {
/* 961*/      List<JavaType> typeParams = bindings.getTypeParameters();
/* 963*/      switch (typeParams.size()) {
/*   0*/        case 0:
/* 965*/          kt = vt = _unknownType();
/*   0*/          break;
/*   0*/        case 2:
/* 968*/          kt = typeParams.get(0);
/* 969*/          vt = typeParams.get(1);
/*   0*/          break;
/*   0*/        default:
/* 972*/          throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/      } 
/*   0*/    } 
/* 975*/    return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/* 981*/    List<JavaType> typeParams = bindings.getTypeParameters();
/* 984*/    if (typeParams.isEmpty()) {
/* 985*/      ct = _unknownType();
/* 986*/    } else if (typeParams.size() == 1) {
/* 987*/      ct = typeParams.get(0);
/*   0*/    } else {
/* 989*/      throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/* 991*/    return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*   0*/    JavaType ct;
/* 997*/    List<JavaType> typeParams = bindings.getTypeParameters();
/*1000*/    if (typeParams.isEmpty()) {
/*1001*/      ct = _unknownType();
/*1002*/    } else if (typeParams.size() == 1) {
/*1003*/      ct = typeParams.get(0);
/*   0*/    } else {
/*1005*/      throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters");
/*   0*/    } 
/*1007*/    return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1021*/    if (bindings.isEmpty()) {
/*1022*/      JavaType result = _findWellKnownSimple(raw);
/*1023*/      if (result != null) {
/*1024*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1027*/    return _newSimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1040*/    return new SimpleType(raw, bindings, superClass, superInterfaces);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _unknownType() {
/*1049*/    return CORE_TYPE_OBJECT;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _findWellKnownSimple(Class<?> clz) {
/*1060*/    if (clz.isPrimitive()) {
/*1061*/      if (clz == CLS_BOOL) {
/*1061*/          return CORE_TYPE_BOOL; 
/*   0*/         }
/*1062*/      if (clz == CLS_INT) {
/*1062*/          return CORE_TYPE_INT; 
/*   0*/         }
/*1063*/      if (clz == CLS_LONG) {
/*1063*/          return CORE_TYPE_LONG; 
/*   0*/         }
/*   0*/    } else {
/*1065*/      if (clz == CLS_STRING) {
/*1065*/          return CORE_TYPE_STRING; 
/*   0*/         }
/*1066*/      if (clz == CLS_OBJECT) {
/*1066*/          return CORE_TYPE_OBJECT; 
/*   0*/         }
/*   0*/    } 
/*1068*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
/*   0*/    JavaType resultType;
/*1087*/    if (type instanceof Class) {
/*1089*/      resultType = _fromClass(context, (Class)type, EMPTY_BINDINGS);
/*1092*/    } else if (type instanceof ParameterizedType) {
/*1093*/      resultType = _fromParamType(context, (ParameterizedType)type, bindings);
/*   0*/    } else {
/*1095*/      if (type instanceof JavaType) {
/*1097*/          return (JavaType)type; 
/*   0*/         }
/*1099*/      if (type instanceof GenericArrayType) {
/*1100*/        resultType = _fromArrayType(context, (GenericArrayType)type, bindings);
/*1102*/      } else if (type instanceof TypeVariable) {
/*1103*/        resultType = _fromVariable(context, (TypeVariable)type, bindings);
/*1105*/      } else if (type instanceof WildcardType) {
/*1106*/        resultType = _fromWildcard(context, (WildcardType)type, bindings);
/*   0*/      } else {
/*1109*/        throw new IllegalArgumentException("Unrecognized Type: " + ((type == null) ? "[null]" : type.toString()));
/*   0*/      } 
/*   0*/    } 
/*1115*/    if (this._modifiers != null && !resultType.isContainerType()) {
/*1116*/      TypeBindings b = resultType.getBindings();
/*1117*/      if (b == null) {
/*1118*/          b = EMPTY_BINDINGS; 
/*   0*/         }
/*1120*/      for (TypeModifier mod : this._modifiers) {
/*1121*/          resultType = mod.modifyType(resultType, type, b, this); 
/*   0*/         }
/*   0*/    } 
/*1124*/    return resultType;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
/*1134*/    JavaType result = _findWellKnownSimple(rawType);
/*1135*/    if (result != null) {
/*1136*/        return result; 
/*   0*/       }
/*1141*/    boolean cachable = (bindings == null || bindings.isEmpty());
/*1142*/    if (cachable) {
/*1143*/      result = this._typeCache.get(rawType);
/*1144*/      if (result != null) {
/*1145*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1150*/    if (context == null) {
/*1151*/      context = new ClassStack(rawType);
/*   0*/    } else {
/*1153*/      ClassStack prev = context.find(rawType);
/*1154*/      if (prev != null) {
/*1156*/        ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/*1157*/        prev.addSelfReference(selfRef);
/*1158*/        return selfRef;
/*   0*/      } 
/*1161*/      context = context.child(rawType);
/*   0*/    } 
/*1165*/    if (rawType.isArray()) {
/*1166*/      result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*   0*/    } else {
/*   0*/      JavaType superClass, superInterfaces[];
/*1174*/      if (rawType.isInterface()) {
/*1175*/        superClass = null;
/*1176*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } else {
/*1179*/        superClass = _resolveSuperClass(context, rawType, bindings);
/*1180*/        superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*   0*/      } 
/*1184*/      if (rawType == Properties.class) {
/*1185*/        result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*1190*/      } else if (superClass != null) {
/*1191*/        result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*   0*/      } 
/*1194*/      if (result == null) {
/*1195*/        result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/*1196*/        if (result == null) {
/*1197*/          result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/*1198*/          if (result == null) {
/*1200*/              result = _newSimpleType(rawType, bindings, superClass, superInterfaces); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1205*/    context.resolveSelfReferences(result);
/*1207*/    if (cachable) {
/*1208*/        this._typeCache.putIfAbsent(rawType, result); 
/*   0*/       }
/*1210*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1215*/    Type parent = ClassUtil.getGenericSuperclass(rawType);
/*1216*/    if (parent == null) {
/*1217*/        return null; 
/*   0*/       }
/*1219*/    return _fromAny(context, parent, parentBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
/*1224*/    Type[] types = ClassUtil.getGenericInterfaces(rawType);
/*1225*/    if (types == null || types.length == 0) {
/*1226*/        return NO_TYPES; 
/*   0*/       }
/*1228*/    int len = types.length;
/*1229*/    JavaType[] resolved = new JavaType[len];
/*1230*/    for (int i = 0; i < len; i++) {
/*1231*/      Type type = types[i];
/*1232*/      resolved[i] = _fromAny(context, type, parentBindings);
/*   0*/    } 
/*1234*/    return resolved;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1246*/    if (rawType == Map.class) {
/*1247*/        return _mapType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1249*/    if (rawType == Collection.class) {
/*1250*/        return _collectionType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1253*/    if (rawType == AtomicReference.class) {
/*1254*/        return _referenceType(rawType, bindings, superClass, superInterfaces); 
/*   0*/       }
/*1260*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/*1268*/    int intCount = superInterfaces.length;
/*1270*/    for (int i = 0; i < intCount; i++) {
/*1271*/      JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/*1272*/      if (result != null) {
/*1273*/          return result; 
/*   0*/         }
/*   0*/    } 
/*1276*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
/*   0*/    TypeBindings newBindings;
/*1287*/    Class<?> rawType = (Class)ptype.getRawType();
/*1291*/    if (rawType == CLS_ENUM) {
/*1292*/        return CORE_TYPE_ENUM; 
/*   0*/       }
/*1294*/    if (rawType == CLS_COMPARABLE) {
/*1295*/        return CORE_TYPE_COMPARABLE; 
/*   0*/       }
/*1297*/    if (rawType == CLS_CLASS) {
/*1298*/        return CORE_TYPE_CLASS; 
/*   0*/       }
/*1304*/    Type[] args = ptype.getActualTypeArguments();
/*1305*/    int paramCount = (args == null) ? 0 : args.length;
/*1309*/    if (paramCount == 0) {
/*1310*/      newBindings = EMPTY_BINDINGS;
/*   0*/    } else {
/*1312*/      JavaType[] pt = new JavaType[paramCount];
/*1313*/      for (int i = 0; i < paramCount; i++) {
/*1314*/          pt[i] = _fromAny(context, args[i], parentBindings); 
/*   0*/         }
/*1316*/      newBindings = TypeBindings.create(rawType, pt);
/*   0*/    } 
/*1318*/    return _fromClass(context, rawType, newBindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
/*1323*/    JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/*1324*/    return ArrayType.construct(elementType, bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
/*1330*/    String name = var.getName();
/*1331*/    JavaType type = bindings.findBoundType(name);
/*1332*/    if (type != null) {
/*1333*/        return type; 
/*   0*/       }
/*1337*/    if (bindings.hasUnbound(name)) {
/*1338*/        return CORE_TYPE_OBJECT; 
/*   0*/       }
/*1340*/    bindings = bindings.withUnboundVariable(name);
/*1342*/    Type[] bounds = var.getBounds();
/*1343*/    return _fromAny(context, bounds[0], bindings);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
/*1353*/    return _fromAny(context, type.getUpperBounds()[0], bindings);
/*   0*/  }
/*   0*/}
