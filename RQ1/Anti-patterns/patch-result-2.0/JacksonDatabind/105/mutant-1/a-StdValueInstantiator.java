/*   0*/package com.fasterxml.jackson.databind.deser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*   0*/import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.ValueInstantiator;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/
/*   0*/@JacksonStdImpl
/*   0*/public class StdValueInstantiator extends ValueInstantiator implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final String _valueTypeDesc;
/*   0*/  
/*   0*/  protected final Class<?> _valueClass;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _defaultCreator;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _withArgsCreator;
/*   0*/  
/*   0*/  protected SettableBeanProperty[] _constructorArguments;
/*   0*/  
/*   0*/  protected JavaType _delegateType;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _delegateCreator;
/*   0*/  
/*   0*/  protected SettableBeanProperty[] _delegateArguments;
/*   0*/  
/*   0*/  protected JavaType _arrayDelegateType;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _arrayDelegateCreator;
/*   0*/  
/*   0*/  protected SettableBeanProperty[] _arrayDelegateArguments;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _fromStringCreator;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _fromIntCreator;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _fromLongCreator;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _fromDoubleCreator;
/*   0*/  
/*   0*/  protected AnnotatedWithParams _fromBooleanCreator;
/*   0*/  
/*   0*/  protected AnnotatedParameter _incompleteParameter;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public StdValueInstantiator(DeserializationConfig config, Class<?> valueType) {
/*  83*/    this._valueTypeDesc = ClassUtil.nameOf(valueType);
/*  84*/    this._valueClass = (valueType == null) ? Object.class : valueType;
/*   0*/  }
/*   0*/  
/*   0*/  public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
/*  88*/    this._valueTypeDesc = (valueType == null) ? "UNKNOWN TYPE" : valueType.toString();
/*  89*/    this._valueClass = (valueType == null) ? Object.class : valueType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  protected StdValueInstantiator(StdValueInstantiator src) {
/*  98*/    this._valueTypeDesc = src._valueTypeDesc;
/*  99*/    this._valueClass = src._valueClass;
/* 101*/    this._defaultCreator = src._defaultCreator;
/* 103*/    this._constructorArguments = src._constructorArguments;
/* 104*/    this._withArgsCreator = src._withArgsCreator;
/* 106*/    this._delegateType = src._delegateType;
/* 107*/    this._delegateCreator = src._delegateCreator;
/* 108*/    this._delegateArguments = src._delegateArguments;
/* 110*/    this._arrayDelegateType = src._arrayDelegateType;
/* 111*/    this._arrayDelegateCreator = src._arrayDelegateCreator;
/* 112*/    this._arrayDelegateArguments = src._arrayDelegateArguments;
/* 114*/    this._fromStringCreator = src._fromStringCreator;
/* 115*/    this._fromIntCreator = src._fromIntCreator;
/* 116*/    this._fromLongCreator = src._fromLongCreator;
/* 117*/    this._fromDoubleCreator = src._fromDoubleCreator;
/* 118*/    this._fromBooleanCreator = src._fromBooleanCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs) {
/* 130*/    this._defaultCreator = defaultCreator;
/* 131*/    this._delegateCreator = delegateCreator;
/* 132*/    this._delegateType = delegateType;
/* 133*/    this._delegateArguments = delegateArgs;
/* 134*/    this._withArgsCreator = withArgsCreator;
/* 135*/    this._constructorArguments = constructorArgs;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs) {
/* 143*/    this._arrayDelegateCreator = arrayDelegateCreator;
/* 144*/    this._arrayDelegateType = arrayDelegateType;
/* 145*/    this._arrayDelegateArguments = arrayDelegateArgs;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromStringCreator(AnnotatedWithParams creator) {
/* 149*/    this._fromStringCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromIntCreator(AnnotatedWithParams creator) {
/* 153*/    this._fromIntCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromLongCreator(AnnotatedWithParams creator) {
/* 157*/    this._fromLongCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromDoubleCreator(AnnotatedWithParams creator) {
/* 161*/    this._fromDoubleCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromBooleanCreator(AnnotatedWithParams creator) {
/* 165*/    this._fromBooleanCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureIncompleteParameter(AnnotatedParameter parameter) {
/* 169*/    this._incompleteParameter = parameter;
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueTypeDesc() {
/* 180*/    return this._valueTypeDesc;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getValueClass() {
/* 185*/    return this._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromString() {
/* 190*/    return (this._fromStringCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromInt() {
/* 195*/    return (this._fromIntCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromLong() {
/* 200*/    return (this._fromLongCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromDouble() {
/* 205*/    return (this._fromDoubleCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromBoolean() {
/* 210*/    return (this._fromBooleanCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingDefault() {
/* 215*/    return (this._defaultCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingDelegate() {
/* 220*/    return (this._delegateType != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingArrayDelegate() {
/* 225*/    return (this._arrayDelegateType != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromObjectWith() {
/* 230*/    return (this._withArgsCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canInstantiate() {
/* 235*/    return (canCreateUsingDefault() || canCreateUsingDelegate() || canCreateUsingArrayDelegate() || canCreateFromObjectWith() || canCreateFromString() || canCreateFromInt() || canCreateFromLong() || canCreateFromDouble() || canCreateFromBoolean());
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getDelegateType(DeserializationConfig config) {
/* 244*/    return this._delegateType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getArrayDelegateType(DeserializationConfig config) {
/* 249*/    return this._arrayDelegateType;
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 254*/    return this._constructorArguments;
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
/* 266*/    if (this._defaultCreator == null) {
/* 267*/        return super.createUsingDefault(ctxt); 
/*   0*/       }
/*   0*/    try {
/* 270*/      return this._defaultCreator.call();
/* 271*/    } catch (Exception e) {
/* 272*/      return ctxt.handleInstantiationProblem(this._valueClass, null, (Throwable)rewrapCtorProblem(ctxt, e));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
/* 279*/    if (this._withArgsCreator == null) {
/* 280*/        return super.createFromObjectWith(ctxt, args); 
/*   0*/       }
/*   0*/    try {
/* 283*/      return this._withArgsCreator.call(args);
/* 284*/    } catch (Exception e) {
/* 285*/      return ctxt.handleInstantiationProblem(this._valueClass, args, (Throwable)rewrapCtorProblem(ctxt, e));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 293*/    if (this._delegateCreator == null && 
/* 294*/      this._arrayDelegateCreator != null) {
/* 295*/        return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate); 
/*   0*/       }
/* 298*/    return _createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 304*/    if (this._arrayDelegateCreator == null && 
/* 305*/      this._delegateCreator != null) {
/* 307*/        return createUsingDelegate(ctxt, delegate); 
/*   0*/       }
/* 310*/    return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
/* 322*/    if (this._fromStringCreator == null) {
/* 323*/        return _createFromStringFallbacks(ctxt, value); 
/*   0*/       }
/*   0*/    try {
/* 326*/      return this._fromStringCreator.call1(value);
/* 327*/    } catch (Throwable t) {
/* 328*/      return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, (Throwable)rewrapCtorProblem(ctxt, t));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 337*/    if (this._fromIntCreator != null) {
/* 338*/      Object arg = value;
/*   0*/      try {
/* 340*/        return this._fromIntCreator.call1(arg);
/* 341*/      } catch (Throwable t0) {
/* 342*/        return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/      } 
/*   0*/    } 
/* 347*/    if (this._fromLongCreator != null) {
/* 348*/      Object arg = value;
/*   0*/      try {
/* 350*/        return this._fromLongCreator.call1(arg);
/* 351*/      } catch (Throwable t0) {
/* 352*/        return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/      } 
/*   0*/    } 
/* 356*/    return super.createFromInt(ctxt, value);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
/* 362*/    if (this._fromLongCreator == null) {
/* 363*/        return super.createFromLong(ctxt, value); 
/*   0*/       }
/* 365*/    Object arg = value;
/*   0*/    try {
/* 367*/      return this._fromLongCreator.call1(arg);
/* 368*/    } catch (Throwable t0) {
/* 369*/      return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
/* 377*/    if (this._fromDoubleCreator == null) {
/* 378*/        return super.createFromDouble(ctxt, value); 
/*   0*/       }
/* 380*/    Object arg = value;
/*   0*/    try {
/* 382*/      return this._fromDoubleCreator.call1(arg);
/* 383*/    } catch (Throwable t0) {
/* 384*/      return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
/* 392*/    if (this._fromBooleanCreator == null) {
/* 393*/        return super.createFromBoolean(ctxt, value); 
/*   0*/       }
/* 395*/    Boolean arg = value;
/*   0*/    try {
/* 397*/      return this._fromBooleanCreator.call1(arg);
/* 398*/    } catch (Throwable t0) {
/* 399*/      return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getDelegateCreator() {
/* 412*/    return this._delegateCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getArrayDelegateCreator() {
/* 417*/    return this._arrayDelegateCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getDefaultCreator() {
/* 422*/    return this._defaultCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getWithArgsCreator() {
/* 427*/    return this._withArgsCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedParameter getIncompleteParameter() {
/* 432*/    return this._incompleteParameter;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JsonMappingException wrapException(Throwable t) {
/* 450*/    for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 451*/      if (curr instanceof JsonMappingException) {
/* 452*/          return (JsonMappingException)curr; 
/*   0*/         }
/*   0*/    } 
/* 455*/    return new JsonMappingException(null, "Instantiation of " + getValueTypeDesc() + " value failed: " + ClassUtil.exceptionMessage(t), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t) {
/* 466*/    for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 467*/      if (curr instanceof JsonMappingException) {
/* 468*/          return (JsonMappingException)curr; 
/*   0*/         }
/*   0*/    } 
/* 471*/    return ctxt.instantiationException(getValueClass(), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t) {
/* 481*/    if (t instanceof JsonMappingException) {
/* 482*/        return (JsonMappingException)t; 
/*   0*/       }
/* 484*/    return ctxt.instantiationException(getValueClass(), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t) {
/* 495*/    if (t instanceof ExceptionInInitializerError || t instanceof java.lang.reflect.InvocationTargetException) {
/* 498*/      Throwable cause = t.getCause();
/* 499*/      if (cause != null) {
/* 500*/          t = cause; 
/*   0*/         }
/*   0*/    } 
/* 503*/    return wrapAsJsonMappingException(ctxt, t);
/*   0*/  }
/*   0*/  
/*   0*/  private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate) throws IOException {
/* 518*/    if (delegateCreator == null) {
/* 519*/        throw new IllegalStateException("No delegate constructor for " + getValueTypeDesc()); 
/*   0*/       }
/*   0*/    try {
/* 523*/      if (delegateArguments == null) {
/* 524*/          return delegateCreator.call1(delegate); 
/*   0*/         }
/* 527*/      int len = delegateArguments.length;
/* 528*/      Object[] args = new Object[len];
/* 529*/      for (int i = 0; i < len; i++) {
/* 530*/        SettableBeanProperty prop = delegateArguments[i];
/* 531*/        if (prop == null) {
/* 532*/          args[i] = delegate;
/*   0*/        } else {
/* 534*/          args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
/*   0*/        } 
/*   0*/      } 
/* 538*/      return delegateCreator.call(args);
/* 539*/    } catch (Throwable t) {
/* 540*/      throw rewrapCtorProblem(ctxt, t);
/*   0*/    } 
/*   0*/  }
/*   0*/}
