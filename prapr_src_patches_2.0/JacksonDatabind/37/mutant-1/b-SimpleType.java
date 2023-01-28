/*   0*/package com.fasterxml.jackson.databind.type;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class SimpleType extends TypeBase {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected SimpleType(Class<?> cls) {
/*  33*/    this(cls, TypeBindings.emptyBindings(), null, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected SimpleType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts) {
/*  38*/    this(cls, bindings, superClass, superInts, null, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  protected SimpleType(TypeBase base) {
/*  48*/    super(base);
/*   0*/  }
/*   0*/  
/*   0*/  protected SimpleType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  55*/    super(cls, bindings, superClass, superInts, 0, valueHandler, typeHandler, asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  protected SimpleType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int extraHash, Object valueHandler, Object typeHandler, boolean asStatic) {
/*  68*/    super(cls, bindings, superClass, superInts, extraHash, valueHandler, typeHandler, asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public static SimpleType constructUnsafe(Class<?> raw) {
/*  82*/    return new SimpleType(raw, null, null, null, null, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public static SimpleType construct(Class<?> cls) {
/* 106*/    if (Map.class.isAssignableFrom(cls)) {
/* 107*/        throw new IllegalArgumentException("Can not construct SimpleType for a Map (class: " + cls.getName() + ")"); 
/*   0*/       }
/* 109*/    if (Collection.class.isAssignableFrom(cls)) {
/* 110*/        throw new IllegalArgumentException("Can not construct SimpleType for a Collection (class: " + cls.getName() + ")"); 
/*   0*/       }
/* 113*/    if (cls.isArray()) {
/* 114*/        throw new IllegalArgumentException("Can not construct SimpleType for an array (class: " + cls.getName() + ")"); 
/*   0*/       }
/* 116*/    return new SimpleType(cls);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType _narrow(Class<?> subclass) {
/* 122*/    if (this._class == subclass) {
/* 123*/        return this; 
/*   0*/       }
/* 135*/    return new SimpleType(subclass, this._bindings, withStaticTyping(), this._superInterfaces, this._valueHandler, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType withContentType(JavaType contentType) {
/* 141*/    throw new IllegalArgumentException("Simple types have no content types; can not call withContentType()");
/*   0*/  }
/*   0*/  
/*   0*/  public SimpleType withTypeHandler(Object h) {
/* 146*/    if (this._typeHandler == h) {
/* 147*/        return this; 
/*   0*/       }
/* 149*/    return new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, this._valueHandler, h, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType withContentTypeHandler(Object h) {
/* 155*/    throw new IllegalArgumentException("Simple types have no content types; can not call withContenTypeHandler()");
/*   0*/  }
/*   0*/  
/*   0*/  public SimpleType withValueHandler(Object h) {
/* 160*/    if (h == this._valueHandler) {
/* 161*/        return this; 
/*   0*/       }
/* 163*/    return new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, h, this._typeHandler, this._asStatic);
/*   0*/  }
/*   0*/  
/*   0*/  public SimpleType withContentValueHandler(Object h) {
/* 169*/    throw new IllegalArgumentException("Simple types have no content types; can not call withContenValueHandler()");
/*   0*/  }
/*   0*/  
/*   0*/  public SimpleType withStaticTyping() {
/* 174*/    return this._asStatic ? this : new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, this._valueHandler, this._typeHandler, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
/* 182*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected String buildCanonicalName() {
/* 188*/    StringBuilder sb = new StringBuilder();
/* 189*/    sb.append(this._class.getName());
/* 191*/    int count = this._bindings.size();
/* 192*/    if (count > 0) {
/* 193*/      sb.append('<');
/* 194*/      for (int i = 0; i < count; i++) {
/* 195*/        JavaType t = containedType(i);
/* 196*/        if (i > 0) {
/* 197*/            sb.append(','); 
/*   0*/           }
/* 199*/        sb.append(t.toCanonical());
/*   0*/      } 
/* 201*/      sb.append('>');
/*   0*/    } 
/* 203*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContainerType() {
/* 213*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuilder getErasedSignature(StringBuilder sb) {
/* 217*/    return _classSignature(this._class, sb, true);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuilder getGenericSignature(StringBuilder sb) {
/* 223*/    _classSignature(this._class, sb, false);
/* 225*/    int count = this._bindings.size();
/* 226*/    if (count > 0) {
/* 227*/      sb.append('<');
/* 228*/      for (int i = 0; i < count; i++) {
/* 229*/          sb = containedType(i).getGenericSignature(sb); 
/*   0*/         }
/* 231*/      sb.append('>');
/*   0*/    } 
/* 233*/    sb.append(';');
/* 234*/    return sb;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 246*/    StringBuilder sb = new StringBuilder(40);
/* 247*/    sb.append("[simple type, class ").append(buildCanonicalName()).append(']');
/* 248*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 254*/    if (o == this) {
/* 254*/        return true; 
/*   0*/       }
/* 255*/    if (o == null) {
/* 255*/        return false; 
/*   0*/       }
/* 256*/    if (o.getClass() != getClass()) {
/* 256*/        return false; 
/*   0*/       }
/* 258*/    SimpleType other = (SimpleType)o;
/* 261*/    if (other._class != this._class) {
/* 261*/        return false; 
/*   0*/       }
/* 264*/    TypeBindings b1 = this._bindings;
/* 265*/    TypeBindings b2 = other._bindings;
/* 266*/    return b1.equals(b2);
/*   0*/  }
/*   0*/}
