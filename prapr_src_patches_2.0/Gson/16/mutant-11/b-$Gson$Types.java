/*   0*/package com.google.gson.internal;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Array;
/*   0*/import java.lang.reflect.GenericArrayType;
/*   0*/import java.lang.reflect.GenericDeclaration;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.lang.reflect.ParameterizedType;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.lang.reflect.TypeVariable;
/*   0*/import java.lang.reflect.WildcardType;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Map;
/*   0*/import java.util.NoSuchElementException;
/*   0*/import java.util.Properties;
/*   0*/
/*   0*/public final class $Gson$Types {
/*  40*/  static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
/*   0*/  
/*   0*/  private $Gson$Types() {
/*  43*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type... typeArguments) {
/*  54*/    return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
/*   0*/  }
/*   0*/  
/*   0*/  public static GenericArrayType arrayOf(Type componentType) {
/*  64*/    return new GenericArrayTypeImpl(componentType);
/*   0*/  }
/*   0*/  
/*   0*/  public static WildcardType subtypeOf(Type bound) {
/*   0*/    Type[] upperBounds;
/*  75*/    if (bound instanceof WildcardType) {
/*  76*/      upperBounds = ((WildcardType)bound).getUpperBounds();
/*   0*/    } else {
/*  78*/      upperBounds = new Type[] { bound };
/*   0*/    } 
/*  80*/    return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
/*   0*/  }
/*   0*/  
/*   0*/  public static WildcardType supertypeOf(Type bound) {
/*   0*/    Type[] lowerBounds;
/*  90*/    if (bound instanceof WildcardType) {
/*  91*/      lowerBounds = ((WildcardType)bound).getLowerBounds();
/*   0*/    } else {
/*  93*/      lowerBounds = new Type[] { bound };
/*   0*/    } 
/*  95*/    return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
/*   0*/  }
/*   0*/  
/*   0*/  public static Type canonicalize(Type type) {
/* 104*/    if (type instanceof Class) {
/* 105*/      Class<?> c = (Class)type;
/* 106*/      return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
/*   0*/    } 
/* 108*/    if (type instanceof ParameterizedType) {
/* 109*/      ParameterizedType p = (ParameterizedType)type;
/* 110*/      return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
/*   0*/    } 
/* 113*/    if (type instanceof GenericArrayType) {
/* 114*/      GenericArrayType g = (GenericArrayType)type;
/* 115*/      return new GenericArrayTypeImpl(g.getGenericComponentType());
/*   0*/    } 
/* 117*/    if (type instanceof WildcardType) {
/* 118*/      WildcardType w = (WildcardType)type;
/* 119*/      return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
/*   0*/    } 
/* 123*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> getRawType(Type type) {
/* 128*/    if (type instanceof Class) {
/* 130*/        return (Class)type; 
/*   0*/       }
/* 132*/    if (type instanceof ParameterizedType) {
/* 133*/      ParameterizedType parameterizedType = (ParameterizedType)type;
/* 138*/      Type rawType = parameterizedType.getRawType();
/* 139*/      $Gson$Preconditions.checkArgument(rawType instanceof Class);
/* 140*/      return (Class)rawType;
/*   0*/    } 
/* 142*/    if (type instanceof GenericArrayType) {
/* 143*/      Type componentType = ((GenericArrayType)type).getGenericComponentType();
/* 144*/      return Array.newInstance(getRawType(componentType), 0).getClass();
/*   0*/    } 
/* 146*/    if (type instanceof TypeVariable) {
/* 149*/        return Object.class; 
/*   0*/       }
/* 151*/    if (type instanceof WildcardType) {
/* 152*/        return getRawType(((WildcardType)type).getUpperBounds()[0]); 
/*   0*/       }
/* 155*/    String className = (type == null) ? "null" : type.getClass().getName();
/* 156*/    throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean equal(Object a, Object b) {
/* 162*/    return (a == b || (a != null && a.equals(b)));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(Type a, Type b) {
/* 169*/    if (a == b) {
/* 171*/        return true; 
/*   0*/       }
/* 173*/    if (a instanceof Class) {
/* 175*/        return a.equals(b); 
/*   0*/       }
/* 177*/    if (a instanceof ParameterizedType) {
/* 178*/      if (!(b instanceof ParameterizedType)) {
/* 179*/          return false; 
/*   0*/         }
/* 183*/      ParameterizedType pa = (ParameterizedType)a;
/* 184*/      ParameterizedType pb = (ParameterizedType)b;
/* 185*/      return (equal(pa.getOwnerType(), pb.getOwnerType()) && pa.getRawType().equals(pb.getRawType()) && Arrays.equals((Object[])pa.getActualTypeArguments(), (Object[])pb.getActualTypeArguments()));
/*   0*/    } 
/* 189*/    if (a instanceof GenericArrayType) {
/* 190*/      if (!(b instanceof GenericArrayType)) {
/* 191*/          return false; 
/*   0*/         }
/* 194*/      GenericArrayType ga = (GenericArrayType)a;
/* 195*/      GenericArrayType gb = (GenericArrayType)b;
/* 196*/      return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
/*   0*/    } 
/* 198*/    if (a instanceof WildcardType) {
/* 199*/      if (!(b instanceof WildcardType)) {
/* 200*/          return false; 
/*   0*/         }
/* 203*/      WildcardType wa = (WildcardType)a;
/* 204*/      WildcardType wb = (WildcardType)b;
/* 205*/      return (Arrays.equals((Object[])wa.getUpperBounds(), (Object[])wb.getUpperBounds()) && Arrays.equals((Object[])wa.getLowerBounds(), (Object[])wb.getLowerBounds()));
/*   0*/    } 
/* 208*/    if (a instanceof TypeVariable) {
/* 209*/      if (!(b instanceof TypeVariable)) {
/* 210*/          return false; 
/*   0*/         }
/* 212*/      TypeVariable<?> va = (TypeVariable)a;
/* 213*/      TypeVariable<?> vb = (TypeVariable)b;
/* 214*/      return (va.getGenericDeclaration() == vb.getGenericDeclaration() && va.getName().equals(vb.getName()));
/*   0*/    } 
/* 219*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int hashCodeOrZero(Object o) {
/* 224*/    return (o != null) ? o.hashCode() : 0;
/*   0*/  }
/*   0*/  
/*   0*/  public static String typeToString(Type type) {
/* 228*/    return (type instanceof Class) ? ((Class)type).getName() : type.toString();
/*   0*/  }
/*   0*/  
/*   0*/  static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
/* 237*/    if (toResolve == rawType) {
/* 238*/        return context; 
/*   0*/       }
/* 242*/    if (toResolve.isInterface()) {
/* 243*/      Class<?>[] interfaces = rawType.getInterfaces();
/* 244*/      for (int i = 0, length = interfaces.length; i < length; i++) {
/* 245*/        if (interfaces[i] == toResolve) {
/* 246*/            return rawType.getGenericInterfaces()[i]; 
/*   0*/           }
/* 247*/        if (toResolve.isAssignableFrom(interfaces[i])) {
/* 248*/            return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 254*/    if (!rawType.isInterface()) {
/* 255*/        while (rawType != Object.class) {
/* 256*/          Class<?> rawSupertype = rawType.getSuperclass();
/* 257*/          if (rawSupertype == toResolve) {
/* 258*/              return rawType.getGenericSuperclass(); 
/*   0*/             }
/* 259*/          if (toResolve.isAssignableFrom(rawSupertype)) {
/* 260*/              return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve); 
/*   0*/             }
/* 262*/          rawType = rawSupertype;
/*   0*/        }  
/*   0*/       }
/* 267*/    return toResolve;
/*   0*/  }
/*   0*/  
/*   0*/  static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
/* 278*/    $Gson$Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
/* 279*/    return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
/*   0*/  }
/*   0*/  
/*   0*/  public static Type getArrayComponentType(Type array) {
/* 288*/    return (array instanceof GenericArrayType) ? ((GenericArrayType)array).getGenericComponentType() : ((Class)array).getComponentType();
/*   0*/  }
/*   0*/  
/*   0*/  public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
/* 298*/    Type collectionType = getSupertype(context, contextRawType, Collection.class);
/* 300*/    if (collectionType instanceof WildcardType) {
/* 301*/        collectionType = ((WildcardType)collectionType).getUpperBounds()[0]; 
/*   0*/       }
/* 303*/    if (collectionType instanceof ParameterizedType) {
/* 304*/        return ((ParameterizedType)collectionType).getActualTypeArguments()[0]; 
/*   0*/       }
/* 306*/    return Object.class;
/*   0*/  }
/*   0*/  
/*   0*/  public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
/* 319*/    if (context == Properties.class) {
/* 320*/        return new Type[] { String.class, String.class }; 
/*   0*/       }
/* 323*/    Type mapType = getSupertype(context, contextRawType, Map.class);
/* 325*/    if (mapType instanceof ParameterizedType) {
/* 326*/      ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
/* 327*/      return mapParameterizedType.getActualTypeArguments();
/*   0*/    } 
/* 329*/    return new Type[] { Object.class, Object.class };
/*   0*/  }
/*   0*/  
/*   0*/  public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
/* 333*/    return resolve(context, contextRawType, toResolve, new HashSet<TypeVariable>());
/*   0*/  }
/*   0*/  
/*   0*/  private static Type resolve(Type context, Class<?> contextRawType, Type toResolve, Collection<TypeVariable> visitedTypeVariables) {
/* 340*/    while (toResolve instanceof TypeVariable) {
/* 341*/      TypeVariable<?> typeVariable = (TypeVariable)toResolve;
/* 343*/      toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
/* 344*/      if (toResolve == typeVariable) {
/* 345*/          return toResolve; 
/*   0*/         }
/*   0*/    } 
/* 348*/    if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
/* 349*/      Class<?> original = (Class)toResolve;
/* 350*/      Type<?> componentType = original.getComponentType();
/* 351*/      Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
/* 352*/      return (componentType == newComponentType) ? original : arrayOf(newComponentType);
/*   0*/    } 
/* 356*/    if (toResolve instanceof GenericArrayType) {
/* 357*/      GenericArrayType original = (GenericArrayType)toResolve;
/* 358*/      Type componentType = original.getGenericComponentType();
/* 359*/      Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
/* 360*/      return (componentType == newComponentType) ? original : arrayOf(newComponentType);
/*   0*/    } 
/* 364*/    if (toResolve instanceof ParameterizedType) {
/* 365*/      ParameterizedType original = (ParameterizedType)toResolve;
/* 366*/      Type ownerType = original.getOwnerType();
/* 367*/      Type newOwnerType = resolve(context, contextRawType, ownerType, visitedTypeVariables);
/* 368*/      boolean changed = (newOwnerType != ownerType);
/* 370*/      Type[] args = original.getActualTypeArguments();
/* 371*/      for (int t = 0, length = args.length; t < length; t++) {
/* 372*/        Type resolvedTypeArgument = resolve(context, contextRawType, args[t], visitedTypeVariables);
/* 373*/        if (resolvedTypeArgument != args[t]) {
/* 374*/          if (!changed) {
/* 375*/            args = (Type[])args.clone();
/* 376*/            changed = true;
/*   0*/          } 
/* 378*/          args[t] = resolvedTypeArgument;
/*   0*/        } 
/*   0*/      } 
/* 382*/      return changed ? newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
/*   0*/    } 
/* 386*/    if (toResolve instanceof WildcardType) {
/* 387*/      WildcardType original = (WildcardType)toResolve;
/* 388*/      Type[] originalLowerBound = original.getLowerBounds();
/* 389*/      Type[] originalUpperBound = original.getUpperBounds();
/* 391*/      if (originalLowerBound.length == 1) {
/* 392*/        Type lowerBound = resolve(context, contextRawType, originalLowerBound[0], visitedTypeVariables);
/* 393*/        if (lowerBound != originalLowerBound[0]) {
/* 394*/            return supertypeOf(lowerBound); 
/*   0*/           }
/* 396*/      } else if (originalUpperBound.length == 1) {
/* 397*/        Type upperBound = resolve(context, contextRawType, originalUpperBound[0], visitedTypeVariables);
/* 398*/        if (upperBound != originalUpperBound[0]) {
/* 399*/            return subtypeOf(upperBound); 
/*   0*/           }
/*   0*/      } 
/* 402*/      return original;
/*   0*/    } 
/* 405*/    return toResolve;
/*   0*/  }
/*   0*/  
/*   0*/  static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
/* 411*/    Class<?> declaredByRaw = declaringClassOf(unknown);
/* 414*/    if (declaredByRaw == null) {
/* 415*/        return unknown; 
/*   0*/       }
/* 418*/    Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
/* 419*/    if (declaredBy instanceof ParameterizedType) {
/* 420*/      int index = indexOf((Object[])declaredByRaw.getTypeParameters(), unknown);
/* 421*/      return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
/*   0*/    } 
/* 424*/    return unknown;
/*   0*/  }
/*   0*/  
/*   0*/  private static int indexOf(Object[] array, Object toFind) {
/* 428*/    for (int i = 0, length = array.length; i < length; i++) {
/* 429*/      if (toFind.equals(array[i])) {
/* 430*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 433*/    throw new NoSuchElementException();
/*   0*/  }
/*   0*/  
/*   0*/  private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
/* 441*/    GenericDeclaration genericDeclaration = (GenericDeclaration)typeVariable.getGenericDeclaration();
/* 442*/    return (genericDeclaration instanceof Class) ? (Class)genericDeclaration : null;
/*   0*/  }
/*   0*/  
/*   0*/  static void checkNotPrimitive(Type type) {
/* 448*/    $Gson$Preconditions.checkArgument((!(type instanceof Class) || !((Class)type).isPrimitive()));
/*   0*/  }
/*   0*/  
/*   0*/  private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
/*   0*/    private final Type ownerType;
/*   0*/    
/*   0*/    private final Type rawType;
/*   0*/    
/*   0*/    private final Type[] typeArguments;
/*   0*/    
/*   0*/    private static final long serialVersionUID = 0L;
/*   0*/    
/*   0*/    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
/* 458*/      if (rawType instanceof Class) {
/* 459*/        Class<?> rawTypeAsClass = (Class)rawType;
/* 460*/        boolean isStaticOrTopLevelClass = (Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null);
/* 462*/        $Gson$Preconditions.checkArgument((ownerType != null || isStaticOrTopLevelClass));
/*   0*/      } 
/* 465*/      this.ownerType = (ownerType == null) ? null : $Gson$Types.canonicalize(ownerType);
/* 466*/      this.rawType = $Gson$Types.canonicalize(rawType);
/* 467*/      this.typeArguments = (Type[])typeArguments.clone();
/* 468*/      for (int t = 0, length = this.typeArguments.length; t < length; t++) {
/* 469*/        $Gson$Preconditions.checkNotNull(this.typeArguments[t]);
/* 470*/        $Gson$Types.checkNotPrimitive(this.typeArguments[t]);
/* 471*/        this.typeArguments[t] = $Gson$Types.canonicalize(this.typeArguments[t]);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] getActualTypeArguments() {
/* 476*/      return (Type[])this.typeArguments.clone();
/*   0*/    }
/*   0*/    
/*   0*/    public Type getRawType() {
/* 480*/      return this.rawType;
/*   0*/    }
/*   0*/    
/*   0*/    public Type getOwnerType() {
/* 484*/      return this.ownerType;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object other) {
/* 488*/      return (other instanceof ParameterizedType && $Gson$Types.equals(this, (ParameterizedType)other));
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 493*/      return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType.hashCode() ^ $Gson$Types.hashCodeOrZero(this.ownerType);
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 499*/      int length = this.typeArguments.length;
/* 500*/      if (length == 0) {
/* 501*/          return $Gson$Types.typeToString(this.rawType); 
/*   0*/         }
/* 504*/      StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
/* 505*/      stringBuilder.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
/* 506*/      for (int i = 1; i < length; i++) {
/* 507*/          stringBuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[i])); 
/*   0*/         }
/* 509*/      return stringBuilder.append(">").toString();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
/*   0*/    private final Type componentType;
/*   0*/    
/*   0*/    private static final long serialVersionUID = 0L;
/*   0*/    
/*   0*/    public GenericArrayTypeImpl(Type componentType) {
/* 519*/      this.componentType = $Gson$Types.canonicalize(componentType);
/*   0*/    }
/*   0*/    
/*   0*/    public Type getGenericComponentType() {
/* 523*/      return this.componentType;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object o) {
/* 527*/      return (o instanceof GenericArrayType && $Gson$Types.equals(this, (GenericArrayType)o));
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 532*/      return this.componentType.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 536*/      return $Gson$Types.typeToString(this.componentType) + "[]";
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class WildcardTypeImpl implements WildcardType, Serializable {
/*   0*/    private final Type upperBound;
/*   0*/    
/*   0*/    private final Type lowerBound;
/*   0*/    
/*   0*/    private static final long serialVersionUID = 0L;
/*   0*/    
/*   0*/    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
/* 552*/      $Gson$Preconditions.checkArgument((lowerBounds.length <= 1));
/* 553*/      $Gson$Preconditions.checkArgument((upperBounds.length == 1));
/* 555*/      if (lowerBounds.length == 1) {
/* 556*/        $Gson$Preconditions.checkNotNull(lowerBounds[0]);
/* 557*/        $Gson$Types.checkNotPrimitive(lowerBounds[0]);
/* 558*/        $Gson$Preconditions.checkArgument((upperBounds[0] == Object.class));
/* 559*/        $Gson$Types.canonicalize(lowerBounds[0]);
/* 559*/        this;
/* 560*/        this.upperBound = Object.class;
/*   0*/      } else {
/* 563*/        $Gson$Preconditions.checkNotNull(upperBounds[0]);
/* 564*/        $Gson$Types.checkNotPrimitive(upperBounds[0]);
/* 565*/        this.lowerBound = null;
/* 566*/        this.upperBound = $Gson$Types.canonicalize(upperBounds[0]);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] getUpperBounds() {
/* 571*/      return new Type[] { this.upperBound };
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] getLowerBounds() {
/* 575*/      return (this.lowerBound != null) ? new Type[] { this.lowerBound } : $Gson$Types.EMPTY_TYPE_ARRAY;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object other) {
/* 579*/      return (other instanceof WildcardType && $Gson$Types.equals(this, (WildcardType)other));
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 585*/      return ((this.lowerBound != null) ? (31 + this.lowerBound.hashCode()) : 1) ^ 31 + this.upperBound.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 590*/      if (this.lowerBound != null) {
/* 591*/          return "? super " + $Gson$Types.typeToString(this.lowerBound); 
/*   0*/         }
/* 592*/      if (this.upperBound == Object.class) {
/* 593*/          return "?"; 
/*   0*/         }
/* 595*/      return "? extends " + $Gson$Types.typeToString(this.upperBound);
/*   0*/    }
/*   0*/  }
/*   0*/}
