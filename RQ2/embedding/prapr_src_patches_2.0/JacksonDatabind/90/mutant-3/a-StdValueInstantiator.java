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
/*  82*/    this._valueTypeDesc = (valueType == null) ? "UNKNOWN TYPE" : valueType.getName();
/*  83*/    this._valueClass = (valueType == null) ? Object.class : valueType;
/*   0*/  }
/*   0*/  
/*   0*/  public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
/*  87*/    this._valueTypeDesc = (valueType == null) ? "UNKNOWN TYPE" : valueType.toString();
/*  88*/    this._valueClass = (valueType == null) ? Object.class : valueType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  protected StdValueInstantiator(StdValueInstantiator src) {
/*  97*/    this._valueTypeDesc = src._valueTypeDesc;
/*  98*/    this._valueClass = src._valueClass;
/* 100*/    this._defaultCreator = src._defaultCreator;
/* 102*/    this._constructorArguments = src._constructorArguments;
/* 103*/    this._withArgsCreator = src._withArgsCreator;
/* 105*/    this._delegateType = src._delegateType;
/* 106*/    this._delegateCreator = src._delegateCreator;
/* 107*/    this._delegateArguments = src._delegateArguments;
/* 109*/    this._arrayDelegateType = src._arrayDelegateType;
/* 110*/    this._arrayDelegateCreator = src._arrayDelegateCreator;
/* 111*/    this._arrayDelegateArguments = src._arrayDelegateArguments;
/* 113*/    this._fromStringCreator = src._fromStringCreator;
/* 114*/    this._fromIntCreator = src._fromIntCreator;
/* 115*/    this._fromLongCreator = src._fromLongCreator;
/* 116*/    this._fromDoubleCreator = src._fromDoubleCreator;
/* 117*/    this._fromBooleanCreator = src._fromBooleanCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs) {
/* 129*/    this._defaultCreator = defaultCreator;
/* 130*/    this._delegateCreator = delegateCreator;
/* 131*/    this._delegateType = delegateType;
/* 132*/    this._delegateArguments = delegateArgs;
/* 133*/    this._withArgsCreator = withArgsCreator;
/* 134*/    this._constructorArguments = constructorArgs;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs) {
/* 142*/    this._arrayDelegateCreator = arrayDelegateCreator;
/* 143*/    this._arrayDelegateType = arrayDelegateType;
/* 144*/    this._arrayDelegateArguments = arrayDelegateArgs;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromStringCreator(AnnotatedWithParams creator) {
/* 148*/    this._fromStringCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromIntCreator(AnnotatedWithParams creator) {
/* 152*/    this._fromIntCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromLongCreator(AnnotatedWithParams creator) {
/* 156*/    this._fromLongCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromDoubleCreator(AnnotatedWithParams creator) {
/* 160*/    this._fromDoubleCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureFromBooleanCreator(AnnotatedWithParams creator) {
/* 164*/    this._fromBooleanCreator = creator;
/*   0*/  }
/*   0*/  
/*   0*/  public void configureIncompleteParameter(AnnotatedParameter parameter) {
/* 168*/    this._incompleteParameter = parameter;
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueTypeDesc() {
/* 179*/    return this._valueTypeDesc;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getValueClass() {
/* 184*/    return this._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromString() {
/* 189*/    return (this._fromStringCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromInt() {
/* 194*/    return (this._fromIntCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromLong() {
/* 199*/    return (this._fromLongCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromDouble() {
/* 204*/    return (this._fromDoubleCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromBoolean() {
/* 209*/    return (this._fromBooleanCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingDefault() {
/* 214*/    return (this._defaultCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingDelegate() {
/* 219*/    return (this._delegateType != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateUsingArrayDelegate() {
/* 224*/    return (this._arrayDelegateType != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canCreateFromObjectWith() {
/* 229*/    return (this._withArgsCreator != null);
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getDelegateType(DeserializationConfig config) {
/* 235*/    return this._delegateType;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getArrayDelegateType(DeserializationConfig config) {
/* 240*/    return this._arrayDelegateType;
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
/* 245*/    return this._constructorArguments;
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
/* 257*/    if (this._defaultCreator == null) {
/* 258*/        return super.createUsingDefault(ctxt); 
/*   0*/       }
/*   0*/    try {
/* 261*/      return this._defaultCreator.call();
/* 262*/    } catch (Throwable t) {
/* 263*/      return ctxt.handleInstantiationProblem(this._defaultCreator.getDeclaringClass(), null, (Throwable)rewrapCtorProblem(ctxt, t));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
/* 271*/    if (this._withArgsCreator == null) {
/* 272*/        return super.createFromObjectWith(ctxt, args); 
/*   0*/       }
/*   0*/    try {
/* 275*/      return this._withArgsCreator.call(args);
/* 276*/    } catch (Throwable t) {
/* 277*/      return ctxt.handleInstantiationProblem(this._withArgsCreator.getDeclaringClass(), args, (Throwable)rewrapCtorProblem(ctxt, t));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 286*/    if (this._delegateCreator == null && 
/* 287*/      this._arrayDelegateCreator != null) {
/* 288*/        return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate); 
/*   0*/       }
/* 291*/    return _createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
/* 297*/    if (this._arrayDelegateCreator == null && 
/* 298*/      this._delegateCreator != null) {
/* 300*/        return createUsingDelegate(ctxt, delegate); 
/*   0*/       }
/* 303*/    return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
/* 315*/    if (this._fromStringCreator == null) {
/* 316*/        return _createFromStringFallbacks(ctxt, value); 
/*   0*/       }
/*   0*/    try {
/* 319*/      return this._fromStringCreator.call1(value);
/* 320*/    } catch (Throwable t) {
/* 321*/      return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, (Throwable)rewrapCtorProblem(ctxt, t));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
/* 330*/    if (this._fromIntCreator != null) {
/* 331*/      Object arg = value;
/*   0*/      try {
/* 333*/        return this._fromIntCreator.call1(arg);
/* 334*/      } catch (Throwable t0) {
/* 335*/        return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/      } 
/*   0*/    } 
/* 340*/    if (this._fromLongCreator != null) {
/* 341*/      Object arg = value;
/*   0*/      try {
/* 343*/        return this._fromLongCreator.call1(arg);
/* 344*/      } catch (Throwable t0) {
/* 345*/        return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/      } 
/*   0*/    } 
/* 349*/    return super.createFromInt(ctxt, value);
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
/* 355*/    if (this._fromLongCreator == null) {
/* 356*/        return super.createFromLong(ctxt, value); 
/*   0*/       }
/* 358*/    Object arg = value;
/*   0*/    try {
/* 360*/      return this._fromLongCreator.call1(arg);
/* 361*/    } catch (Throwable t0) {
/* 362*/      return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
/* 370*/    if (this._fromDoubleCreator == null) {
/* 371*/        return super.createFromDouble(ctxt, value); 
/*   0*/       }
/* 373*/    Object arg = value;
/*   0*/    try {
/* 375*/      return this._fromDoubleCreator.call1(arg);
/* 376*/    } catch (Throwable t0) {
/* 377*/      return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
/* 385*/    if (this._fromBooleanCreator == null) {
/* 386*/        return super.createFromBoolean(ctxt, value); 
/*   0*/       }
/* 388*/    Boolean arg = value;
/*   0*/    try {
/* 390*/      return this._fromBooleanCreator.call1(arg);
/* 391*/    } catch (Throwable t0) {
/* 392*/      return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, (Throwable)rewrapCtorProblem(ctxt, t0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getDelegateCreator() {
/* 405*/    return this._delegateCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getArrayDelegateCreator() {
/* 410*/    return this._arrayDelegateCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getDefaultCreator() {
/* 415*/    return this._defaultCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedWithParams getWithArgsCreator() {
/* 420*/    return this._withArgsCreator;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedParameter getIncompleteParameter() {
/* 425*/    return this._incompleteParameter;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected JsonMappingException wrapException(Throwable t) {
/* 443*/    for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 444*/      if (curr instanceof JsonMappingException) {
/* 445*/          return (JsonMappingException)curr; 
/*   0*/         }
/*   0*/    } 
/* 448*/    return new JsonMappingException(null, "Instantiation of " + getValueTypeDesc() + " value failed: " + t.getMessage(), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t) {
/* 459*/    for (Throwable curr = t; curr != null; curr = curr.getCause()) {
/* 460*/      if (curr instanceof JsonMappingException) {
/* 461*/          return (JsonMappingException)curr; 
/*   0*/         }
/*   0*/    } 
/* 464*/    return ctxt.instantiationException(getValueClass(), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t) {
/* 474*/    if (t instanceof JsonMappingException) {
/* 475*/        return (JsonMappingException)t; 
/*   0*/       }
/* 477*/    return ctxt.instantiationException(getValueClass(), t);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t) {
/* 488*/    if (t instanceof ExceptionInInitializerError || t instanceof java.lang.reflect.InvocationTargetException) {
/* 491*/      Throwable cause = t.getCause();
/* 492*/      if (cause != null) {
/* 493*/          t = cause; 
/*   0*/         }
/*   0*/    } 
/* 496*/    return wrapAsJsonMappingException(ctxt, t);
/*   0*/  }
/*   0*/  
/*   0*/  private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate) throws IOException {
/* 512*/    if (delegateCreator == null) {
/* 513*/        throw new IllegalStateException("No delegate constructor for " + getValueTypeDesc()); 
/*   0*/       }
/*   0*/    try {
/* 517*/      if (delegateArguments == null) {
/* 518*/          return delegateCreator.call1(delegate); 
/*   0*/         }
/* 521*/      int len = delegateArguments.length;
/* 522*/      Object[] args = new Object[len];
/* 523*/      for (int i = 0; i < len; i++) {
/* 524*/        SettableBeanProperty prop = delegateArguments[i];
/* 525*/        if (prop == null) {
/* 526*/          args[i] = delegate;
/*   0*/        } else {
/* 528*/          args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
/*   0*/        } 
/*   0*/      } 
/* 532*/      return delegateCreator.call(args);
/* 533*/    } catch (Throwable t) {
/* 534*/      throw rewrapCtorProblem(ctxt, t);
/*   0*/    } 
/*   0*/  }
/*   0*/}
