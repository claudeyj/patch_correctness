/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.type.ResolvedType;
/*   0*/import com.fasterxml.jackson.databind.type.TypeBindings;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.util.List;
/*   0*/
/*   0*/public abstract class JavaType extends ResolvedType implements Serializable, Type {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final Class<?> _class;
/*   0*/  
/*   0*/  protected final int _hash;
/*   0*/  
/*   0*/  protected final Object _valueHandler;
/*   0*/  
/*   0*/  protected final Object _typeHandler;
/*   0*/  
/*   0*/  protected final boolean _asStatic;
/*   0*/  
/*   0*/  protected JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  79*/    this._class = raw;
/*  80*/    this._hash = raw.toString().hashCode() + additionalHash;
/*  81*/    this._valueHandler = valueHandler;
/*  82*/    this._typeHandler = typeHandler;
/*  83*/    this._asStatic = asStatic;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType(JavaType base) {
/*  93*/    this._class = base._class;
/*  94*/    this._hash = base._hash;
/*  95*/    this._valueHandler = base._valueHandler;
/*  96*/    this._typeHandler = base._typeHandler;
/*  97*/    this._asStatic = base._asStatic;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JavaType forcedNarrowBy(Class<?> subclass) {
/* 199*/    if (subclass == this._class) {
/* 200*/        return this; 
/*   0*/       }
/* 202*/    JavaType result = _narrow(subclass);
/* 204*/    if (this._valueHandler != result.getValueHandler()) {
/* 205*/        result = result.withValueHandler(this._valueHandler); 
/*   0*/       }
/* 207*/    if (this._typeHandler != result.getTypeHandler()) {
/* 208*/        result = result.withTypeHandler(this._typeHandler); 
/*   0*/       }
/* 210*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public final Class<?> getRawClass() {
/* 223*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasRawClass(Class<?> clz) {
/* 231*/    return (this._class == clz);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasContentType() {
/* 241*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isTypeOrSubTypeOf(Class<?> clz) {
/* 248*/    return (this._class == clz || clz.isAssignableFrom(this._class));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAbstract() {
/* 253*/    return Modifier.isAbstract(this._class.getModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isConcrete() {
/* 263*/    int mod = this._class.getModifiers();
/* 264*/    if ((mod & 0x600) == 0) {
/* 265*/        return true; 
/*   0*/       }
/* 270*/    return this._class.isPrimitive();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrowable() {
/* 274*/    return Throwable.class.isAssignableFrom(this._class);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayType() {
/* 277*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnumType() {
/* 280*/    return this._class.isEnum();
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isInterface() {
/* 283*/    return this._class.isInterface();
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isPrimitive() {
/* 286*/    return this._class.isPrimitive();
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isFinal() {
/* 289*/    return Modifier.isFinal(this._class.getModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCollectionLikeType() {
/* 304*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isMapLikeType() {
/* 312*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isJavaLangObject() {
/* 323*/    return (this._class == Object.class);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean useStaticType() {
/* 333*/    return this._asStatic;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasGenericTypes() {
/* 342*/    return (containedTypeCount() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getKeyType() {
/* 345*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getContentType() {
/* 348*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getReferencedType() {
/* 351*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> getParameterSource() {
/* 366*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType containedTypeOrUnknown(int index) {
/* 392*/    JavaType t = containedType(index);
/* 393*/    return (t == null) ? TypeFactory.unknownType() : t;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T getValueHandler() {
/* 446*/    return (T)this._valueHandler;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T getTypeHandler() {
/* 452*/    return (T)this._typeHandler;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getContentValueHandler() {
/* 457*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getContentTypeHandler() {
/* 462*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasValueHandler() {
/* 467*/    return (this._valueHandler != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasHandlers() {
/* 478*/    return (this._typeHandler != null || this._valueHandler != null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getGenericSignature() {
/* 498*/    StringBuilder sb = new StringBuilder(40);
/* 499*/    getGenericSignature(sb);
/* 500*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String getErasedSignature() {
/* 519*/    StringBuilder sb = new StringBuilder(40);
/* 520*/    getErasedSignature(sb);
/* 521*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public final int hashCode() {
/* 550*/    return this._hash;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract JavaType withTypeHandler(Object paramObject);
/*   0*/  
/*   0*/  public abstract JavaType withContentTypeHandler(Object paramObject);
/*   0*/  
/*   0*/  public abstract JavaType withValueHandler(Object paramObject);
/*   0*/  
/*   0*/  public abstract JavaType withContentValueHandler(Object paramObject);
/*   0*/  
/*   0*/  public abstract JavaType withContentType(JavaType paramJavaType);
/*   0*/  
/*   0*/  public abstract JavaType withStaticTyping();
/*   0*/  
/*   0*/  public abstract JavaType refine(Class<?> paramClass, TypeBindings paramTypeBindings, JavaType paramJavaType, JavaType[] paramArrayOfJavaType);
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected abstract JavaType _narrow(Class<?> paramClass);
/*   0*/  
/*   0*/  public abstract boolean isContainerType();
/*   0*/  
/*   0*/  public abstract int containedTypeCount();
/*   0*/  
/*   0*/  public abstract JavaType containedType(int paramInt);
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public abstract String containedTypeName(int paramInt);
/*   0*/  
/*   0*/  public abstract TypeBindings getBindings();
/*   0*/  
/*   0*/  public abstract JavaType findSuperType(Class<?> paramClass);
/*   0*/  
/*   0*/  public abstract JavaType getSuperClass();
/*   0*/  
/*   0*/  public abstract List<JavaType> getInterfaces();
/*   0*/  
/*   0*/  public abstract JavaType[] findTypeParameters(Class<?> paramClass);
/*   0*/  
/*   0*/  public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
/*   0*/  
/*   0*/  public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
/*   0*/  
/*   0*/  public abstract String toString();
/*   0*/  
/*   0*/  public abstract boolean equals(Object paramObject);
/*   0*/}
