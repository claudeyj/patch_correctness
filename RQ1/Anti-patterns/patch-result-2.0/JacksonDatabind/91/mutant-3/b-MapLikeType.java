/*   0*/package com.fasterxml.jackson.databind.type;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.type.ResolvedType;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import java.lang.reflect.TypeVariable;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class MapLikeType extends TypeBase {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final JavaType _keyType;
/*   0*/  
/*   0*/  protected final JavaType _valueType;
/*   0*/  
/*   0*/  protected MapLikeType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  39*/    super(mapType, bindings, superClass, superInts, keyT.containedTypeCount() ^ valueT.hashCode(), valueHandler, typeHandler, asStatic);
/*  41*/    this._keyType = keyT;
/*  42*/    this._valueType = valueT;
/*   0*/  }
/*   0*/  
/*   0*/  protected MapLikeType(TypeBase base, JavaType keyT, JavaType valueT) {
/*  49*/    super(base);
/*  50*/    this._keyType = keyT;
/*  51*/    this._valueType = valueT;
/*   0*/  }
/*   0*/  
/*   0*/  public static MapLikeType upgradeFrom(JavaType baseType, JavaType keyT, JavaType valueT) {
/*  65*/    if (baseType instanceof TypeBase) {
/*  66*/        return new MapLikeType((TypeBase)baseType, keyT, valueT); 
/*   0*/       }
/*  68*/    throw new IllegalArgumentException("Can not upgrade from an instance of " + baseType.getClass());
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public static MapLikeType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
/*   0*/    TypeBindings bindings;
/*  78*/    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])rawType.getTypeParameters();
/*  80*/    if (arrayOfTypeVariable == null || arrayOfTypeVariable.length != 2) {
/*  81*/      bindings = TypeBindings.emptyBindings();
/*   0*/    } else {
/*  83*/      bindings = TypeBindings.create(rawType, keyT, valueT);
/*   0*/    } 
/*  85*/    return new MapLikeType(rawType, bindings, _bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JavaType _narrow(Class<?> subclass) {
/*  93*/    return new MapLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withKeyType(JavaType keyType) {
/* 102*/    if (keyType == this._keyType) {
/* 103*/        return this; 
/*   0*/       }
/* 105*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType withContentType(JavaType contentType) {
/* 112*/    if (this._valueType == contentType) {
/* 113*/        return this; 
/*   0*/       }
/* 115*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withTypeHandler(Object h) {
/* 122*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withContentTypeHandler(Object h) {
/* 129*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withValueHandler(Object h) {
/* 136*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withContentValueHandler(Object h) {
/* 143*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType withHandlersFrom(JavaType src) {
/* 150*/    JavaType type = super.withHandlersFrom(src);
/* 151*/    JavaType srcKeyType = src.getKeyType();
/* 153*/    if (type instanceof MapLikeType && 
/* 154*/      srcKeyType != null) {
/* 155*/      JavaType ct = this._keyType.withHandlersFrom(srcKeyType);
/* 156*/      if (ct != this._keyType) {
/* 157*/          type = ((MapLikeType)type).withKeyType(ct); 
/*   0*/         }
/*   0*/    } 
/* 161*/    JavaType srcCt = src.getContentType();
/* 162*/    if (srcCt != null) {
/* 163*/      JavaType ct = this._valueType.withHandlersFrom(srcCt);
/* 164*/      if (ct != this._valueType) {
/* 165*/          type = type.withContentType(ct); 
/*   0*/         }
/*   0*/    } 
/* 168*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withStaticTyping() {
/* 173*/    if (this._asStatic) {
/* 174*/        return this; 
/*   0*/       }
/* 176*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 184*/    return new MapLikeType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  protected String buildCanonicalName() {
/* 190*/    StringBuilder sb = new StringBuilder();
/* 191*/    sb.append(this._class.getName());
/* 192*/    if (this._keyType != null) {
/* 193*/      sb.append('<');
/* 194*/      sb.append(this._keyType.toCanonical());
/* 195*/      sb.append(',');
/* 196*/      sb.append(this._valueType.toCanonical());
/* 197*/      sb.append('>');
/*   0*/    } 
/* 199*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContainerType() {
/* 210*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isMapLikeType() {
/* 215*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getKeyType() {
/* 220*/    return this._keyType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getContentType() {
/* 225*/    return this._valueType;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getContentValueHandler() {
/* 230*/    return this._valueType.getValueHandler();
/*   0*/  }
/*   0*/  
/*   0*/  public Object getContentTypeHandler() {
/* 235*/    return this._valueType.getTypeHandler();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasHandlers() {
/* 240*/    return (super.hasHandlers() || this._valueType.hasHandlers() || this._keyType.hasHandlers());
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuilder getErasedSignature(StringBuilder sb) {
/* 246*/    return _classSignature(this._class, sb, true);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuilder getGenericSignature(StringBuilder sb) {
/* 251*/    _classSignature(this._class, sb, false);
/* 252*/    sb.append('<');
/* 253*/    this._keyType.getGenericSignature(sb);
/* 254*/    this._valueType.getGenericSignature(sb);
/* 255*/    sb.append(">;");
/* 256*/    return sb;
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withKeyTypeHandler(Object h) {
/* 266*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public MapLikeType withKeyValueHandler(Object h) {
/* 272*/    return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrueMapType() {
/* 283*/    return Map.class.isAssignableFrom(this._class);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 294*/    return String.format("[map-like type; class %s, %s -> %s]", new Object[] { this._class.getName(), this._keyType, this._valueType });
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 300*/    if (o == this) {
/* 300*/        return true; 
/*   0*/       }
/* 301*/    if (o == null) {
/* 301*/        return false; 
/*   0*/       }
/* 302*/    if (o.getClass() != getClass()) {
/* 302*/        return false; 
/*   0*/       }
/* 304*/    MapLikeType other = (MapLikeType)o;
/* 305*/    return (this._class == other._class && this._keyType.equals(other._keyType) && this._valueType.equals(other._valueType));
/*   0*/  }
/*   0*/}
