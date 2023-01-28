/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*   0*/import java.io.IOException;
/*   0*/
/*   0*/public abstract class ValueInstantiator {
/*   0*/  public Class<?> getValueClass() {
/*  50*/    return Object.class;
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueTypeDesc() {
/*  58*/    Class<?> cls = getValueClass();
/*  59*/    if (cls == null) {
/*  60*/        return "UNKNOWN"; 
/*   0*/       }
/*  62*/    return cls.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canInstantiate() {
/*  71*/    return (canCreateUsingDefault() || canCreateUsingDelegate() || canCreateUsingArrayDelegate() || canCreateFromObjectWith() || canCreateFromString() || canCreateFromInt() || canCreateFromLong() || canCreateFromDouble() || canCreateFromBoolean());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromString() {
/*  82*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromInt() {
/*  88*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromLong() {
/*  94*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromDouble() {
/* 100*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromBoolean() {
/* 106*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingDefault() {
/* 113*/    return (getDefaultCreator() != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingDelegate() {
/* 120*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingArrayDelegate() {
/* 129*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromObjectWith() {
/* 136*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 149*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getDelegateType(DeserializationConfig config) {
/* 159*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getArrayDelegateType(DeserializationConfig config) {
/* 170*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
/* 189*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no default no-arguments constructor found", new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
/* 203*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no creator with arguments specified", new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
/* 229*/    return createFromObjectWith(ctxt, buffer.getParameters(props));
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 237*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no delegate creator specified", new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 246*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no array delegate creator specified", new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
/* 258*/    return _createFromStringFallbacks(ctxt, value);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 262*/    new Object[1][0] = value;
/* 262*/    new Object[1];
/* 262*/    "no int/Int-argument constructor/factory method to deserialize from Number value (%s)";
/* 262*/    null;
/* 262*/    this;
/* 262*/    getValueClass();
/* 262*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
/* 268*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { value });
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
/* 274*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", new Object[] { value });
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
/* 280*/    return ctxt.handleMissingInstantiator(getValueClass(), this, null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", new Object[] { value });
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getDefaultCreator() {
/* 301*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getDelegateCreator() {
/* 311*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getArrayDelegateCreator() {
/* 321*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getWithArgsCreator() {
/* 332*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedParameter getIncompleteParameter() {
/* 338*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _createFromStringFallbacks(DeserializationContext ctxt, String value) throws IOException {
/* 356*/    if (canCreateFromBoolean()) {
/* 357*/      String str = value.trim();
/* 358*/      if ("true".equals(str)) {
/* 359*/          return createFromBoolean(ctxt, true); 
/*   0*/         }
/* 361*/      if ("false".equals(str)) {
/* 362*/          return createFromBoolean(ctxt, false); 
/*   0*/         }
/*   0*/    } 
/* 366*/    if (value.length() == 0 && 
/* 367*/      ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 368*/        return null; 
/*   0*/       }
/* 371*/    return ctxt.handleMissingInstantiator(getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", new Object[] { value });
/*   0*/  }
/*   0*/  
/*   0*/  public static class Base extends ValueInstantiator {
/*   0*/    protected final Class<?> _valueType;
/*   0*/    
/*   0*/    public Base(Class<?> type) {
/* 404*/      this._valueType = type;
/*   0*/    }
/*   0*/    
/*   0*/    public Base(JavaType type) {
/* 408*/      this._valueType = type.getRawClass();
/*   0*/    }
/*   0*/    
/*   0*/    public String getValueTypeDesc() {
/* 413*/      return this._valueType.getName();
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> getValueClass() {
/* 418*/      return this._valueType;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static interface Gettable {
/*   0*/    ValueInstantiator getValueInstantiator();
/*   0*/  }
/*   0*/}
