/*   0*/package com.fasterxml.jackson.databind.deser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.io.NumberInput;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.KeyDeserializer;
/*   0*/import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.EnumResolver;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Constructor;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.net.MalformedURLException;
/*   0*/import java.net.URI;
/*   0*/import java.net.URL;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Currency;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import java.util.UUID;
/*   0*/
/*   0*/@JacksonStdImpl
/*   0*/public class StdKeyDeserializer extends KeyDeserializer implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public static final int TYPE_BOOLEAN = 1;
/*   0*/  
/*   0*/  public static final int TYPE_BYTE = 2;
/*   0*/  
/*   0*/  public static final int TYPE_SHORT = 3;
/*   0*/  
/*   0*/  public static final int TYPE_CHAR = 4;
/*   0*/  
/*   0*/  public static final int TYPE_INT = 5;
/*   0*/  
/*   0*/  public static final int TYPE_LONG = 6;
/*   0*/  
/*   0*/  public static final int TYPE_FLOAT = 7;
/*   0*/  
/*   0*/  public static final int TYPE_DOUBLE = 8;
/*   0*/  
/*   0*/  public static final int TYPE_LOCALE = 9;
/*   0*/  
/*   0*/  public static final int TYPE_DATE = 10;
/*   0*/  
/*   0*/  public static final int TYPE_CALENDAR = 11;
/*   0*/  
/*   0*/  public static final int TYPE_UUID = 12;
/*   0*/  
/*   0*/  public static final int TYPE_URI = 13;
/*   0*/  
/*   0*/  public static final int TYPE_URL = 14;
/*   0*/  
/*   0*/  public static final int TYPE_CLASS = 15;
/*   0*/  
/*   0*/  public static final int TYPE_CURRENCY = 16;
/*   0*/  
/*   0*/  public static final int TYPE_BYTE_ARRAY = 17;
/*   0*/  
/*   0*/  protected final int _kind;
/*   0*/  
/*   0*/  protected final Class<?> _keyClass;
/*   0*/  
/*   0*/  protected final FromStringDeserializer<?> _deser;
/*   0*/  
/*   0*/  protected StdKeyDeserializer(int kind, Class<?> cls) {
/*  61*/    this(kind, cls, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected StdKeyDeserializer(int kind, Class<?> cls, FromStringDeserializer<?> deser) {
/*  65*/    this._kind = kind;
/*  66*/    this._keyClass = cls;
/*  67*/    this._deser = deser;
/*   0*/  }
/*   0*/  
/*   0*/  public static StdKeyDeserializer forType(Class<?> raw) {
/*   0*/    int kind;
/*  75*/    if (raw == String.class || raw == Object.class || raw == CharSequence.class) {
/*  76*/        return StringKD.forType(raw); 
/*   0*/       }
/*  77*/    if (raw == UUID.class) {
/*  78*/      kind = 12;
/*  79*/    } else if (raw == Integer.class) {
/*  80*/      kind = 5;
/*  81*/    } else if (raw == Long.class) {
/*  82*/      kind = 6;
/*  83*/    } else if (raw == Date.class) {
/*  84*/      kind = 10;
/*  85*/    } else if (raw == Calendar.class) {
/*  86*/      kind = 11;
/*  88*/    } else if (raw == Boolean.class) {
/*  89*/      kind = 1;
/*  90*/    } else if (raw == Byte.class) {
/*  91*/      kind = 2;
/*  92*/    } else if (raw == Character.class) {
/*  93*/      kind = 4;
/*  94*/    } else if (raw == Short.class) {
/*  95*/      kind = 3;
/*  96*/    } else if (raw == Float.class) {
/*  97*/      kind = 7;
/*  98*/    } else if (raw == Double.class) {
/*  99*/      kind = 8;
/* 100*/    } else if (raw == URI.class) {
/* 101*/      kind = 13;
/* 102*/    } else if (raw == URL.class) {
/* 103*/      kind = 14;
/* 104*/    } else if (raw == Class.class) {
/* 105*/      kind = 15;
/*   0*/    } else {
/* 106*/      if (raw == Locale.class) {
/* 107*/        FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Locale.class);
/* 108*/        return new StdKeyDeserializer(9, raw, deser);
/*   0*/      } 
/* 109*/      if (raw == Currency.class) {
/* 110*/        FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Currency.class);
/* 111*/        return new StdKeyDeserializer(16, raw, deser);
/*   0*/      } 
/* 112*/      if (raw == byte[].class) {
/* 113*/        kind = 17;
/*   0*/      } else {
/* 115*/        return null;
/*   0*/      } 
/*   0*/    } 
/* 117*/    return new StdKeyDeserializer(kind, raw);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
/* 124*/    if (key == null) {
/* 125*/        return null; 
/*   0*/       }
/*   0*/    try {
/* 128*/      Object result = _parse(key, ctxt);
/* 129*/      if (result != null) {
/* 130*/          return result; 
/*   0*/         }
/* 132*/    } catch (Exception re) {
/* 133*/      return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation, problem: (%s) %s", new Object[] { re.getClass().getName(), re.getMessage() });
/*   0*/    } 
/* 137*/    if (this._keyClass.isEnum() && ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 138*/        return null; 
/*   0*/       }
/* 140*/    return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getKeyClass() {
/* 143*/    return this._keyClass;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _parse(String key, DeserializationContext ctxt) throws Exception {
/*   0*/    int value;
/* 147*/    switch (this._kind) {
/*   0*/      case 1:
/* 149*/        if ("true".equals(key)) {
/* 150*/            return Boolean.TRUE; 
/*   0*/           }
/* 152*/        if ("false".equals(key)) {
/* 153*/            return Boolean.FALSE; 
/*   0*/           }
/* 155*/        return ctxt.handleWeirdKey(this._keyClass, key, "value not 'true' or 'false'", new Object[0]);
/*   0*/      case 2:
/* 158*/        value = _parseInt(key);
/* 160*/        if (value < -128 || value > 255) {
/* 161*/            return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 8-bit value", new Object[0]); 
/*   0*/           }
/* 163*/        return (byte)value;
/*   0*/      case 3:
/* 167*/        value = _parseInt(key);
/* 168*/        if (value < -32768 || value > 32767) {
/* 169*/            return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 16-bit value", new Object[0]); 
/*   0*/           }
/* 172*/        return (short)value;
/*   0*/      case 4:
/* 175*/        if (key.length() == 1) {
/* 176*/            return key.charAt(0); 
/*   0*/           }
/* 178*/        return ctxt.handleWeirdKey(this._keyClass, key, "can only convert 1-character Strings", new Object[0]);
/*   0*/      case 5:
/* 180*/        return _parseInt(key);
/*   0*/      case 6:
/* 183*/        return _parseLong(key);
/*   0*/      case 7:
/* 187*/        return (float)_parseDouble(key);
/*   0*/      case 8:
/* 189*/        return _parseDouble(key);
/*   0*/      case 9:
/*   0*/        try {
/* 192*/          return this._deser._deserialize(key, ctxt);
/* 193*/        } catch (IllegalArgumentException e) {
/* 194*/          return _weirdKey(ctxt, key, e);
/*   0*/        } 
/*   0*/      case 16:
/*   0*/        try {
/* 198*/          return this._deser._deserialize(key, ctxt);
/* 199*/        } catch (IllegalArgumentException e) {
/* 200*/          return _weirdKey(ctxt, key, e);
/*   0*/        } 
/*   0*/      case 10:
/* 203*/        return ctxt.parseDate(key);
/*   0*/      case 11:
/* 205*/        return ctxt.constructCalendar(ctxt.parseDate(key));
/*   0*/      case 12:
/*   0*/        try {
/* 208*/          return UUID.fromString(key);
/* 209*/        } catch (Exception e) {
/* 210*/          return _weirdKey(ctxt, key, e);
/*   0*/        } 
/*   0*/      case 13:
/*   0*/        try {
/* 214*/          return URI.create(key);
/* 215*/        } catch (Exception e) {
/* 216*/          return _weirdKey(ctxt, key, e);
/*   0*/        } 
/*   0*/      case 14:
/*   0*/        try {
/* 220*/          return new URL(key);
/* 221*/        } catch (MalformedURLException e) {
/* 222*/          return _weirdKey(ctxt, key, e);
/*   0*/        } 
/*   0*/      case 15:
/*   0*/        try {
/* 226*/          return ctxt.findClass(key);
/* 227*/        } catch (Exception e) {
/* 228*/          return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as Class", new Object[0]);
/*   0*/        } 
/*   0*/      case 17:
/*   0*/        try {
/* 232*/          return ctxt.getConfig().getBase64Variant().decode(key);
/* 233*/        } catch (IllegalArgumentException e) {
/* 234*/          return _weirdKey(ctxt, key, e);
/*   0*/        } 
/*   0*/    } 
/* 237*/    throw new IllegalStateException("Internal error: unknown key type " + this._keyClass);
/*   0*/  }
/*   0*/  
/*   0*/  protected int _parseInt(String key) throws IllegalArgumentException {
/* 248*/    return Integer.parseInt(key);
/*   0*/  }
/*   0*/  
/*   0*/  protected long _parseLong(String key) throws IllegalArgumentException {
/* 252*/    return Long.parseLong(key);
/*   0*/  }
/*   0*/  
/*   0*/  protected double _parseDouble(String key) throws IllegalArgumentException {
/* 256*/    return NumberInput.parseDouble(key);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _weirdKey(DeserializationContext ctxt, String key, Exception e) throws IOException {
/* 261*/    return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", new Object[] { e.getMessage() });
/*   0*/  }
/*   0*/  
/*   0*/  @JacksonStdImpl
/*   0*/  static final class StringKD extends StdKeyDeserializer {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/* 275*/    private static final StringKD sString = new StringKD(String.class);
/*   0*/    
/* 276*/    private static final StringKD sObject = new StringKD(Object.class);
/*   0*/    
/*   0*/    private StringKD(Class<?> nominalType) {
/* 278*/      super(-1, nominalType);
/*   0*/    }
/*   0*/    
/*   0*/    public static StringKD forType(Class<?> nominalType) {
/* 282*/      if (nominalType == String.class) {
/* 283*/          return sString; 
/*   0*/         }
/* 285*/      if (nominalType == Object.class) {
/* 286*/          return sObject; 
/*   0*/         }
/* 288*/      return new StringKD(nominalType);
/*   0*/    }
/*   0*/    
/*   0*/    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/* 293*/      return key;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class DelegatingKD extends KeyDeserializer implements Serializable {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    protected final Class<?> _keyClass;
/*   0*/    
/*   0*/    protected final JsonDeserializer<?> _delegate;
/*   0*/    
/*   0*/    protected DelegatingKD(Class<?> cls, JsonDeserializer<?> deser) {
/* 319*/      this._keyClass = cls;
/* 320*/      this._delegate = deser;
/*   0*/    }
/*   0*/    
/*   0*/    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
/* 328*/      if (key == null) {
/* 329*/          return null; 
/*   0*/         }
/* 331*/      TokenBuffer tb = new TokenBuffer(ctxt.getParser(), ctxt);
/* 332*/      tb.writeString(key);
/*   0*/      try {
/* 335*/        JsonParser p = tb.asParser();
/* 336*/        p.nextToken();
/* 337*/        Object result = this._delegate.deserialize(p, ctxt);
/* 338*/        if (result != null) {
/* 339*/            return result; 
/*   0*/           }
/* 341*/        return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
/* 342*/      } catch (Exception re) {
/* 343*/        return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation: %s", new Object[] { re.getMessage() });
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> getKeyClass() {
/* 347*/      return this._keyClass;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  @JacksonStdImpl
/*   0*/  static final class EnumKD extends StdKeyDeserializer {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    protected final EnumResolver _byNameResolver;
/*   0*/    
/*   0*/    protected final AnnotatedMethod _factory;
/*   0*/    
/*   0*/    protected EnumResolver _byToStringResolver;
/*   0*/    
/*   0*/    protected final Enum<?> _enumDefaultValue;
/*   0*/    
/*   0*/    protected EnumKD(EnumResolver er, AnnotatedMethod factory) {
/* 370*/      super(-1, er.getEnumClass());
/* 371*/      this._byNameResolver = er;
/* 372*/      this._factory = factory;
/* 373*/      this._enumDefaultValue = er.getDefaultValue();
/*   0*/    }
/*   0*/    
/*   0*/    public Object _parse(String key, DeserializationContext ctxt) throws IOException {
/* 379*/      if (this._factory != null) {
/*   0*/          try {
/* 381*/            return this._factory.call1(key);
/* 382*/          } catch (Exception exception) {
/* 383*/            ClassUtil.unwrapAndThrowAsIAE(exception);
/*   0*/          }  
/*   0*/         }
/* 386*/      EnumResolver res = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? _getToStringResolver(ctxt) : this._byNameResolver;
/* 388*/      Enum<?> e = res.findEnum(key);
/* 389*/      if (e == null) {
/* 390*/          if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
/* 392*/            e = this._enumDefaultValue;
/* 393*/          } else if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
/* 394*/            new Object[1][0] = res.getEnumIds();
/* 394*/            new Object[1];
/* 394*/            "not one of values excepted for Enum class: %s";
/* 394*/            return null;
/*   0*/          }  
/*   0*/         }
/* 399*/      return e;
/*   0*/    }
/*   0*/    
/*   0*/    private EnumResolver _getToStringResolver(DeserializationContext ctxt) {
/* 404*/      EnumResolver res = this._byToStringResolver;
/* 405*/      if (res == null) {
/* 406*/          synchronized (this) {
/* 407*/            res = EnumResolver.constructUnsafeUsingToString(this._byNameResolver.getEnumClass(), ctxt.getAnnotationIntrospector());
/*   0*/          }  
/*   0*/         }
/* 411*/      return res;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class StringCtorKeyDeserializer extends StdKeyDeserializer {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    protected final Constructor<?> _ctor;
/*   0*/    
/*   0*/    public StringCtorKeyDeserializer(Constructor<?> ctor) {
/* 426*/      super(-1, ctor.getDeclaringClass());
/* 427*/      this._ctor = ctor;
/*   0*/    }
/*   0*/    
/*   0*/    public Object _parse(String key, DeserializationContext ctxt) throws Exception {
/* 433*/      return this._ctor.newInstance(new Object[] { key });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class StringFactoryKeyDeserializer extends StdKeyDeserializer {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final Method _factoryMethod;
/*   0*/    
/*   0*/    public StringFactoryKeyDeserializer(Method fm) {
/* 448*/      super(-1, fm.getDeclaringClass());
/* 449*/      this._factoryMethod = fm;
/*   0*/    }
/*   0*/    
/*   0*/    public Object _parse(String key, DeserializationContext ctxt) throws Exception {
/* 455*/      return this._factoryMethod.invoke(null, new Object[] { key });
/*   0*/    }
/*   0*/  }
/*   0*/}
