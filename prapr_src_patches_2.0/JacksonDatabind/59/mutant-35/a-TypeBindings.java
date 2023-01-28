/*   0*/package com.fasterxml.jackson.databind.type;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.TypeVariable;
/*   0*/import java.util.AbstractList;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class TypeBindings implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  16*/  private static final String[] NO_STRINGS = new String[0];
/*   0*/  
/*  18*/  private static final JavaType[] NO_TYPES = new JavaType[0];
/*   0*/  
/*  20*/  private static final TypeBindings EMPTY = new TypeBindings(NO_STRINGS, NO_TYPES, null);
/*   0*/  
/*   0*/  private final String[] _names;
/*   0*/  
/*   0*/  private final JavaType[] _types;
/*   0*/  
/*   0*/  private final String[] _unboundVariables;
/*   0*/  
/*   0*/  private final int _hashCode;
/*   0*/  
/*   0*/  private TypeBindings(String[] names, JavaType[] types, String[] uvars) {
/*  53*/    this._names = (names == null) ? NO_STRINGS : names;
/*  54*/    this._types = (types == null) ? NO_TYPES : types;
/*  55*/    if (this._names.length != this._types.length) {
/*  56*/        throw new IllegalArgumentException("Mismatching names (" + this._names.length + "), types (" + this._types.length + ")"); 
/*   0*/       }
/*  58*/    int h = 1;
/*  59*/    for (int i = 0, len = this._types.length; i < len; i++) {
/*  60*/        h += this._types[i].hashCode(); 
/*   0*/       }
/*  62*/    this._unboundVariables = uvars;
/*  63*/    this._hashCode = h;
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings emptyBindings() {
/*  67*/    return EMPTY;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object readResolve() {
/*  72*/    if (this._names == null || this._names.length == 0) {
/*  73*/        return EMPTY; 
/*   0*/       }
/*  75*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings create(Class<?> erasedType, List<JavaType> typeList) {
/*  84*/    JavaType[] types = (typeList == null || typeList.isEmpty()) ? NO_TYPES : typeList.<JavaType>toArray(new JavaType[typeList.size()]);
/*  86*/    return create(erasedType, types);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings create(Class<?> erasedType, JavaType[] types) {
/*   0*/    String[] names;
/*  91*/    if (types == null) {
/*  92*/      types = NO_TYPES;
/*   0*/    } else {
/*  93*/      switch (types.length) {
/*   0*/        case 1:
/*  95*/          return create(erasedType, types[0]);
/*   0*/        case 2:
/*  97*/          return create(erasedType, types[0], types[1]);
/*   0*/      } 
/*   0*/    } 
/*  99*/    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])erasedType.getTypeParameters();
/* 101*/    if (arrayOfTypeVariable == null || arrayOfTypeVariable.length == 0) {
/* 102*/      names = NO_STRINGS;
/*   0*/    } else {
/* 104*/      int len = arrayOfTypeVariable.length;
/* 105*/      names = new String[len];
/* 106*/      for (int i = 0; i < len; i++) {
/* 107*/          names[i] = arrayOfTypeVariable[i].getName(); 
/*   0*/         }
/*   0*/    } 
/* 111*/    if (names.length != types.length) {
/* 112*/        throw new IllegalArgumentException("Can not create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + ((types.length == 1) ? "" : "s") + ": class expects " + names.length); 
/*   0*/       }
/* 116*/    return new TypeBindings(names, types, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings create(Class<?> erasedType, JavaType typeArg1) {
/* 122*/    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])TypeParamStash.paramsFor1(erasedType);
/* 123*/    int varLen = (arrayOfTypeVariable == null) ? 0 : arrayOfTypeVariable.length;
/* 124*/    if (varLen != 1) {
/* 125*/        throw new IllegalArgumentException("Can not create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen); 
/*   0*/       }
/* 128*/    return new TypeBindings(new String[] { arrayOfTypeVariable[0].getName() }, new JavaType[] { typeArg1 }, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings create(Class<?> erasedType, JavaType typeArg1, JavaType typeArg2) {
/* 135*/    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])TypeParamStash.paramsFor2(erasedType);
/* 136*/    int varLen = (arrayOfTypeVariable == null) ? 0 : arrayOfTypeVariable.length;
/* 137*/    if (varLen != 2) {
/* 138*/        throw new IllegalArgumentException("Can not create TypeBindings for class " + erasedType.getName() + " with 2 type parameters: class expects " + varLen); 
/*   0*/       }
/* 141*/    return new TypeBindings(new String[] { arrayOfTypeVariable[0].getName(), arrayOfTypeVariable[1].getName() }, new JavaType[] { typeArg1, typeArg2 }, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType typeArg1) {
/* 152*/    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])erasedType.getTypeParameters();
/* 153*/    int varLen = (arrayOfTypeVariable == null) ? 0 : arrayOfTypeVariable.length;
/* 154*/    if (varLen == 0) {
/* 155*/        return EMPTY; 
/*   0*/       }
/* 157*/    if (varLen != 1) {
/* 158*/        throw new IllegalArgumentException("Can not create TypeBindings for class " + erasedType.getName() + " with 1 type parameter: class expects " + varLen); 
/*   0*/       }
/* 161*/    return new TypeBindings(new String[] { arrayOfTypeVariable[0].getName() }, new JavaType[] { typeArg1 }, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static TypeBindings createIfNeeded(Class<?> erasedType, JavaType[] types) {
/* 172*/    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])erasedType.getTypeParameters();
/* 173*/    if (arrayOfTypeVariable == null || arrayOfTypeVariable.length == 0) {
/* 174*/        return EMPTY; 
/*   0*/       }
/* 176*/    if (types == null) {
/* 177*/        types = NO_TYPES; 
/*   0*/       }
/* 179*/    int len = arrayOfTypeVariable.length;
/* 180*/    String[] names = new String[len];
/* 181*/    for (int i = 0; i < len; i++) {
/* 182*/        names[i] = arrayOfTypeVariable[i].getName(); 
/*   0*/       }
/* 185*/    if (names.length != types.length) {
/* 186*/        throw new IllegalArgumentException("Can not create TypeBindings for class " + erasedType.getName() + " with " + types.length + " type parameter" + ((types.length == 1) ? "" : "s") + ": class expects " + names.length); 
/*   0*/       }
/* 190*/    return new TypeBindings(names, types, null);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeBindings withUnboundVariable(String name) {
/* 200*/    int len = (this._unboundVariables == null) ? 0 : this._unboundVariables.length;
/* 201*/    String[] names = (len == 0) ? new String[1] : Arrays.<String>copyOf(this._unboundVariables, len + 1);
/* 203*/    names[len] = name;
/* 204*/    return new TypeBindings(this._names, this._types, names);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType findBoundType(String name) {
/* 218*/    for (int i = 0, len = this._names.length; i < len; i++) {
/* 219*/      if (name.equals(this._names[i])) {
/* 220*/        JavaType t = this._types[i];
/* 221*/        if (t instanceof ResolvedRecursiveType) {
/* 222*/          ResolvedRecursiveType rrt = (ResolvedRecursiveType)t;
/* 223*/          JavaType t2 = rrt.getSelfReferencedType();
/* 224*/          if (t2 != null) {
/* 225*/              t = t2; 
/*   0*/             }
/*   0*/        } 
/* 238*/        return t;
/*   0*/      } 
/*   0*/    } 
/* 241*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/* 245*/    return (this._types.length == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 252*/    return this._types.length;
/*   0*/  }
/*   0*/  
/*   0*/  public String getBoundName(int index) {
/* 257*/    if (index < 0 || index >= this._names.length) {
/* 258*/        return null; 
/*   0*/       }
/* 260*/    return this._names[index];
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getBoundType(int index) {
/* 265*/    if (index < 0 || index >= this._types.length) {
/* 266*/        return null; 
/*   0*/       }
/* 268*/    return this._types[index];
/*   0*/  }
/*   0*/  
/*   0*/  public List<JavaType> getTypeParameters() {
/* 276*/    if (this._types.length == 0) {
/* 277*/        return Collections.emptyList(); 
/*   0*/       }
/* 279*/    return Arrays.asList(this._types);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasUnbound(String name) {
/* 286*/    if (this._unboundVariables != null) {
/* 287*/        for (int i = this._unboundVariables.length; --i >= 0;) {
/* 288*/          if (name.equals(this._unboundVariables[i])) {
/* 289*/              return true; 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 293*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Object asKey(Class<?> rawBase) {
/* 305*/    return new AsKey(rawBase, this._types, this._hashCode);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 316*/    if (this._types.length == 0) {
/* 317*/        return "<>"; 
/*   0*/       }
/* 319*/    StringBuilder sb = new StringBuilder();
/* 320*/    sb.append('<');
/* 321*/    for (int i = 0, len = this._types.length; i < len; i++) {
/* 322*/      if (i > 0) {
/* 323*/          sb.append(','); 
/*   0*/         }
/* 326*/      String sig = this._types[i].getGenericSignature();
/* 327*/      sb.append(sig);
/*   0*/    } 
/* 329*/    sb.append('>');
/* 330*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 333*/    return this._hashCode;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 337*/    if (o == this) {
/* 337*/        return true; 
/*   0*/       }
/* 338*/    if (o == null || o.getClass() != getClass()) {
/* 338*/        return false; 
/*   0*/       }
/* 339*/    TypeBindings other = (TypeBindings)o;
/* 340*/    int len = this._types.length;
/* 341*/    if (len != other.size()) {
/* 342*/        return false; 
/*   0*/       }
/* 344*/    JavaType[] otherTypes = other._types;
/* 345*/    for (int i = 0; i < len; i++) {
/* 346*/      if (!otherTypes[i].equals(this._types[i])) {
/* 347*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 350*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType[] typeParameterArray() {
/* 360*/    return this._types;
/*   0*/  }
/*   0*/  
/*   0*/  static class TypeParamStash {
/* 381*/    private static final TypeVariable<?>[] VARS_ABSTRACT_LIST = (TypeVariable<?>[])AbstractList.class.getTypeParameters();
/*   0*/    
/* 382*/    private static final TypeVariable<?>[] VARS_COLLECTION = (TypeVariable<?>[])Collection.class.getTypeParameters();
/*   0*/    
/* 383*/    private static final TypeVariable<?>[] VARS_ITERABLE = (TypeVariable<?>[])Iterable.class.getTypeParameters();
/*   0*/    
/* 384*/    private static final TypeVariable<?>[] VARS_LIST = (TypeVariable<?>[])List.class.getTypeParameters();
/*   0*/    
/* 385*/    private static final TypeVariable<?>[] VARS_ARRAY_LIST = (TypeVariable<?>[])ArrayList.class.getTypeParameters();
/*   0*/    
/* 387*/    private static final TypeVariable<?>[] VARS_MAP = (TypeVariable<?>[])Map.class.getTypeParameters();
/*   0*/    
/* 388*/    private static final TypeVariable<?>[] VARS_HASH_MAP = (TypeVariable<?>[])HashMap.class.getTypeParameters();
/*   0*/    
/* 389*/    private static final TypeVariable<?>[] VARS_LINKED_HASH_MAP = (TypeVariable<?>[])LinkedHashMap.class.getTypeParameters();
/*   0*/    
/*   0*/    public static TypeVariable<?>[] paramsFor1(Class<?> erasedType) {
/* 393*/      if (erasedType == Collection.class) {
/* 394*/          return VARS_COLLECTION; 
/*   0*/         }
/* 396*/      if (erasedType == List.class) {
/* 397*/          return VARS_LIST; 
/*   0*/         }
/* 399*/      if (erasedType == ArrayList.class) {
/* 400*/          return VARS_ARRAY_LIST; 
/*   0*/         }
/* 402*/      if (erasedType == AbstractList.class) {
/* 403*/          return VARS_ABSTRACT_LIST; 
/*   0*/         }
/* 405*/      if (erasedType == Iterable.class) {
/* 406*/          return VARS_ITERABLE; 
/*   0*/         }
/* 408*/      return (TypeVariable<?>[])erasedType.getTypeParameters();
/*   0*/    }
/*   0*/    
/*   0*/    public static TypeVariable<?>[] paramsFor2(Class<?> erasedType) {
/* 413*/      if (erasedType == Map.class) {
/* 414*/          return VARS_MAP; 
/*   0*/         }
/* 416*/      if (erasedType == HashMap.class) {
/* 417*/          return VARS_HASH_MAP; 
/*   0*/         }
/* 419*/      if (erasedType == LinkedHashMap.class) {
/* 420*/          return VARS_LINKED_HASH_MAP; 
/*   0*/         }
/* 422*/      return (TypeVariable<?>[])erasedType.getTypeParameters();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class AsKey {
/*   0*/    private final Class<?> _raw;
/*   0*/    
/*   0*/    private final JavaType[] _params;
/*   0*/    
/*   0*/    private final int _hash;
/*   0*/    
/*   0*/    public AsKey(Class<?> raw, JavaType[] params, int hash) {
/* 437*/      this._raw = raw;
/* 438*/      this._params = params;
/* 439*/      this._hash = hash;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 443*/      return this._hash;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object o) {
/* 447*/      if (o == this) {
/* 447*/          return true; 
/*   0*/         }
/* 448*/      if (o == null) {
/* 448*/          return false; 
/*   0*/         }
/* 449*/      if (o.getClass() != getClass()) {
/* 449*/          return false; 
/*   0*/         }
/* 450*/      AsKey other = (AsKey)o;
/* 452*/      if (this._hash == other._hash && this._raw == other._raw) {
/* 453*/        JavaType[] otherParams = other._params;
/* 454*/        int len = this._params.length;
/* 456*/        if (len == otherParams.length) {
/* 457*/          for (int i = 0; i < len; i++) {
/* 458*/            if (!this._params[i].equals(otherParams[i])) {
/* 459*/                return false; 
/*   0*/               }
/*   0*/          } 
/* 462*/          return true;
/*   0*/        } 
/*   0*/      } 
/* 465*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 470*/      return this._raw.getName() + "<>";
/*   0*/    }
/*   0*/  }
/*   0*/}
