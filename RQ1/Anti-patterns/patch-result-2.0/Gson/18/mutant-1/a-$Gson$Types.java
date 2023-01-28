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
/* 279*/    $Gson$Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
/* 280*/    return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
/*   0*/  }
/*   0*/  
/*   0*/  public static Type getArrayComponentType(Type array) {
/* 289*/    return (array instanceof GenericArrayType) ? ((GenericArrayType)array).getGenericComponentType() : ((Class)array).getComponentType();
/*   0*/  }
/*   0*/  
/*   0*/  public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
/* 299*/    Type collectionType = getSupertype(context, contextRawType, Collection.class);
/* 301*/    if (collectionType instanceof WildcardType) {
/* 302*/        collectionType = ((WildcardType)collectionType).getUpperBounds()[0]; 
/*   0*/       }
/* 304*/    if (collectionType instanceof ParameterizedType) {
/* 305*/        return ((ParameterizedType)collectionType).getActualTypeArguments()[0]; 
/*   0*/       }
/* 307*/    return Object.class;
/*   0*/  }
/*   0*/  
/*   0*/  public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
/* 320*/    if (context == Properties.class) {
/* 321*/        return new Type[] { String.class, String.class }; 
/*   0*/       }
/* 324*/    Type mapType = getSupertype(context, contextRawType, Map.class);
/* 326*/    if (mapType instanceof ParameterizedType) {
/* 327*/      ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
/* 328*/      return mapParameterizedType.getActualTypeArguments();
/*   0*/    } 
/* 330*/    return new Type[] { Object.class, Object.class };
/*   0*/  }
/*   0*/  
/*   0*/  public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
/* 334*/    return resolve(context, contextRawType, toResolve, new HashSet<TypeVariable>());
/*   0*/  }
/*   0*/  
/*   0*/  private static Type resolve(Type context, Class<?> contextRawType, Type toResolve, Collection<TypeVariable> visitedTypeVariables) {
/* 341*/    while (toResolve instanceof TypeVariable) {
/* 342*/      TypeVariable<?> typeVariable = (TypeVariable)toResolve;
/* 343*/      if (visitedTypeVariables.contains(typeVariable)) {
/* 345*/          return toResolve; 
/*   0*/         }
/* 347*/      visitedTypeVariables.add(typeVariable);
/* 349*/      toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
/* 350*/      if (toResolve == typeVariable) {
/* 351*/          return toResolve; 
/*   0*/         }
/*   0*/    } 
/* 354*/    if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
/* 355*/      Class<?> original = (Class)toResolve;
/* 356*/      Type<?> componentType = original.getComponentType();
/* 357*/      Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
/* 358*/      return (componentType == newComponentType) ? original : arrayOf(newComponentType);
/*   0*/    } 
/* 362*/    if (toResolve instanceof GenericArrayType) {
/* 363*/      GenericArrayType original = (GenericArrayType)toResolve;
/* 364*/      Type componentType = original.getGenericComponentType();
/* 365*/      Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
/* 366*/      return (componentType == newComponentType) ? original : arrayOf(newComponentType);
/*   0*/    } 
/* 370*/    if (toResolve instanceof ParameterizedType) {
/* 371*/      ParameterizedType original = (ParameterizedType)toResolve;
/* 372*/      Type ownerType = original.getOwnerType();
/* 373*/      Type newOwnerType = resolve(context, contextRawType, ownerType, visitedTypeVariables);
/* 374*/      boolean changed = (newOwnerType != ownerType);
/* 376*/      Type[] args = original.getActualTypeArguments();
/* 377*/      for (int t = 0, length = args.length; t < length; t++) {
/* 378*/        Type resolvedTypeArgument = resolve(context, contextRawType, args[t], visitedTypeVariables);
/* 379*/        if (resolvedTypeArgument != args[t]) {
/* 380*/          if (!changed) {
/* 381*/            args = (Type[])args.clone();
/* 382*/            changed = true;
/*   0*/          } 
/* 384*/          args[t] = resolvedTypeArgument;
/*   0*/        } 
/*   0*/      } 
/* 388*/      return changed ? newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
/*   0*/    } 
/* 392*/    if (toResolve instanceof WildcardType) {
/* 393*/      WildcardType original = (WildcardType)toResolve;
/* 394*/      Type[] originalLowerBound = original.getLowerBounds();
/* 395*/      Type[] originalUpperBound = original.getUpperBounds();
/* 397*/      if (originalLowerBound.length == 1) {
/* 398*/        Type lowerBound = resolve(context, contextRawType, originalLowerBound[0], visitedTypeVariables);
/* 399*/        if (lowerBound != originalLowerBound[0]) {
/* 400*/            return supertypeOf(lowerBound); 
/*   0*/           }
/* 402*/      } else if (originalUpperBound.length == 1) {
/* 403*/        Type upperBound = resolve(context, contextRawType, originalUpperBound[0], visitedTypeVariables);
/* 404*/        if (upperBound != originalUpperBound[0]) {
/* 405*/            return subtypeOf(upperBound); 
/*   0*/           }
/*   0*/      } 
/* 408*/      return original;
/*   0*/    } 
/* 411*/    return toResolve;
/*   0*/  }
/*   0*/  
/*   0*/  static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
/* 417*/    Class<?> declaredByRaw = declaringClassOf(unknown);
/* 420*/    if (declaredByRaw == null) {
/* 421*/        return unknown; 
/*   0*/       }
/* 424*/    Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
/* 425*/    if (declaredBy instanceof ParameterizedType) {
/* 426*/      int index = indexOf((Object[])declaredByRaw.getTypeParameters(), unknown);
/* 427*/      return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
/*   0*/    } 
/* 430*/    return unknown;
/*   0*/  }
/*   0*/  
/*   0*/  private static int indexOf(Object[] array, Object toFind) {
/* 434*/    for (int i = 0, length = array.length; i < length; i++) {
/* 435*/      if (toFind.equals(array[i])) {
/* 436*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 439*/    throw new NoSuchElementException();
/*   0*/  }
/*   0*/  
/*   0*/  private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
/* 447*/    GenericDeclaration genericDeclaration = (GenericDeclaration)typeVariable.getGenericDeclaration();
/* 448*/    return (genericDeclaration instanceof Class) ? (Class)genericDeclaration : null;
/*   0*/  }
/*   0*/  
/*   0*/  static void checkNotPrimitive(Type type) {
/* 454*/    $Gson$Preconditions.checkArgument((!(type instanceof Class) || !((Class)type).isPrimitive()));
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
/* 464*/      if (rawType instanceof Class) {
/* 465*/        Class<?> rawTypeAsClass = (Class)rawType;
/* 466*/        boolean isStaticOrTopLevelClass = (Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null);
/* 468*/        $Gson$Preconditions.checkArgument((ownerType != null || isStaticOrTopLevelClass));
/*   0*/      } 
/* 471*/      this.ownerType = (ownerType == null) ? null : $Gson$Types.canonicalize(ownerType);
/* 472*/      this.rawType = $Gson$Types.canonicalize(rawType);
/* 473*/      this.typeArguments = (Type[])typeArguments.clone();
/* 474*/      for (int t = 0, length = this.typeArguments.length; t < length; t++) {
/* 475*/        $Gson$Preconditions.checkNotNull(this.typeArguments[t]);
/* 476*/        $Gson$Types.checkNotPrimitive(this.typeArguments[t]);
/* 477*/        this.typeArguments[t] = $Gson$Types.canonicalize(this.typeArguments[t]);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] getActualTypeArguments() {
/* 482*/      return (Type[])this.typeArguments.clone();
/*   0*/    }
/*   0*/    
/*   0*/    public Type getRawType() {
/* 486*/      return this.rawType;
/*   0*/    }
/*   0*/    
/*   0*/    public Type getOwnerType() {
/* 490*/      return this.ownerType;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object other) {
/* 494*/      return (other instanceof ParameterizedType && $Gson$Types.equals(this, (ParameterizedType)other));
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 499*/      return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType.hashCode() ^ $Gson$Types.hashCodeOrZero(this.ownerType);
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 505*/      int length = this.typeArguments.length;
/* 506*/      if (length == 0) {
/* 507*/          return $Gson$Types.typeToString(this.rawType); 
/*   0*/         }
/* 510*/      StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
/* 511*/      stringBuilder.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
/* 512*/      for (int i = 1; i < length; i++) {
/* 513*/          stringBuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[i])); 
/*   0*/         }
/* 515*/      return stringBuilder.append(">").toString();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
/*   0*/    private final Type componentType;
/*   0*/    
/*   0*/    private static final long serialVersionUID = 0L;
/*   0*/    
/*   0*/    public GenericArrayTypeImpl(Type componentType) {
/* 525*/      this.componentType = $Gson$Types.canonicalize(componentType);
/*   0*/    }
/*   0*/    
/*   0*/    public Type getGenericComponentType() {
/* 529*/      return this.componentType;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object o) {
/* 533*/      return (o instanceof GenericArrayType && $Gson$Types.equals(this, (GenericArrayType)o));
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 538*/      return this.componentType.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 542*/      return $Gson$Types.typeToString(this.componentType) + "[]";
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
/* 558*/      $Gson$Preconditions.checkArgument((lowerBounds.length <= 1));
/* 559*/      $Gson$Preconditions.checkArgument((upperBounds.length == 1));
/* 561*/      if (lowerBounds.length == 1) {
/* 562*/        $Gson$Preconditions.checkNotNull(lowerBounds[0]);
/* 563*/        $Gson$Types.checkNotPrimitive(lowerBounds[0]);
/* 564*/        $Gson$Preconditions.checkArgument((upperBounds[0] == Object.class));
/* 565*/        this.lowerBound = $Gson$Types.canonicalize(lowerBounds[0]);
/* 566*/        this.upperBound = Object.class;
/*   0*/      } else {
/* 569*/        $Gson$Preconditions.checkNotNull(upperBounds[0]);
/* 570*/        $Gson$Types.checkNotPrimitive(upperBounds[0]);
/* 571*/        this.lowerBound = null;
/* 572*/        this.upperBound = $Gson$Types.canonicalize(upperBounds[0]);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] getUpperBounds() {
/* 577*/      return new Type[] { this.upperBound };
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] getLowerBounds() {
/* 581*/      return (this.lowerBound != null) ? new Type[] { this.lowerBound } : $Gson$Types.EMPTY_TYPE_ARRAY;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object other) {
/* 585*/      return (other instanceof WildcardType && $Gson$Types.equals(this, (WildcardType)other));
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 591*/      return ((this.lowerBound != null) ? (31 + this.lowerBound.hashCode()) : 1) ^ 31 + this.upperBound.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 596*/      if (this.lowerBound != null) {
/* 597*/          return "? super " + $Gson$Types.typeToString(this.lowerBound); 
/*   0*/         }
/* 598*/      if (this.upperBound == Object.class) {
/* 599*/          return "?"; 
/*   0*/         }
/* 601*/      return "? extends " + $Gson$Types.typeToString(this.upperBound);
/*   0*/    }
/*   0*/  }
/*   0*/}
