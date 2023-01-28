/*   0*/package com.fasterxml.jackson.databind.deser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.util.VersionUtil;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.exc.InvalidFormatException;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import java.io.File;
/*   0*/import java.io.IOException;
/*   0*/import java.net.InetAddress;
/*   0*/import java.net.InetSocketAddress;
/*   0*/import java.net.MalformedURLException;
/*   0*/import java.net.URI;
/*   0*/import java.net.URL;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.util.Currency;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/import java.util.regex.Pattern;
/*   0*/
/*   0*/public abstract class FromStringDeserializer<T> extends StdScalarDeserializer<T> {
/*   0*/  public static Class<?>[] types() {
/*  31*/    return new Class<?>[] { 
/*  31*/        File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, 
/*  31*/        InetAddress.class, InetSocketAddress.class, StringBuilder.class };
/*   0*/  }
/*   0*/  
/*   0*/  protected FromStringDeserializer(Class<?> vc) {
/*  55*/    super(vc);
/*   0*/  }
/*   0*/  
/*   0*/  public static Std findDeserializer(Class<?> rawType) {
/*  64*/    int kind = 0;
/*  65*/    if (rawType == File.class) {
/*  66*/      kind = 1;
/*  67*/    } else if (rawType == URL.class) {
/*  68*/      kind = 2;
/*  69*/    } else if (rawType == URI.class) {
/*  70*/      kind = 3;
/*  71*/    } else if (rawType == Class.class) {
/*  72*/      kind = 4;
/*  73*/    } else if (rawType == JavaType.class) {
/*  74*/      kind = 5;
/*  75*/    } else if (rawType == Currency.class) {
/*  76*/      kind = 6;
/*  77*/    } else if (rawType == Pattern.class) {
/*  78*/      kind = 7;
/*  79*/    } else if (rawType == Locale.class) {
/*  80*/      kind = 8;
/*  81*/    } else if (rawType == Charset.class) {
/*  82*/      kind = 9;
/*  83*/    } else if (rawType == TimeZone.class) {
/*  84*/      kind = 10;
/*  85*/    } else if (rawType == InetAddress.class) {
/*  86*/      kind = 11;
/*  87*/    } else if (rawType == InetSocketAddress.class) {
/*  88*/      kind = 12;
/*  89*/    } else if (rawType == StringBuilder.class) {
/*  90*/      kind = 13;
/*   0*/    } else {
/*  92*/      return null;
/*   0*/    } 
/*  94*/    return new Std(rawType, kind);
/*   0*/  }
/*   0*/  
/*   0*/  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 108*/    String text = p.getValueAsString();
/* 109*/    if (text != null) {
/* 110*/      if (text.length() == 0 || (text = text.trim()).length() == 0) {
/* 112*/          return _deserializeFromEmptyString(); 
/*   0*/         }
/* 114*/      Exception cause = null;
/*   0*/      try {
/* 119*/        if (_deserialize(text, ctxt) != null) {
/* 120*/            return _deserialize(text, ctxt); 
/*   0*/           }
/* 122*/      } catch (IllegalArgumentException iae) {
/* 123*/        cause = iae;
/* 124*/      } catch (MalformedURLException me) {
/* 125*/        cause = me;
/*   0*/      } 
/* 127*/      String msg = "not a valid textual representation";
/* 128*/      if (cause != null) {
/* 129*/        String m2 = cause.getMessage();
/* 130*/        if (m2 != null) {
/* 131*/            msg = msg + ", problem: " + m2; 
/*   0*/           }
/*   0*/      } 
/* 135*/      JsonMappingException e = ctxt.weirdStringException(text, this._valueClass, msg);
/* 136*/      if (cause != null) {
/* 137*/          e.initCause(cause); 
/*   0*/         }
/* 139*/      throw e;
/*   0*/    } 
/* 142*/    JsonToken t = p.getCurrentToken();
/* 144*/    if (t == JsonToken.START_ARRAY) {
/* 145*/        return _deserializeFromArray(p, ctxt); 
/*   0*/       }
/* 147*/    if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 149*/      Object ob = p.getEmbeddedObject();
/* 150*/      if (ob == null) {
/* 151*/          return null; 
/*   0*/         }
/* 153*/      if (this._valueClass.isAssignableFrom(ob.getClass())) {
/* 154*/          return (T)ob; 
/*   0*/         }
/* 156*/      return _deserializeEmbedded(ob, ctxt);
/*   0*/    } 
/* 158*/    return (T)ctxt.handleUnexpectedToken(this._valueClass, p);
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract T _deserialize(String paramString, DeserializationContext paramDeserializationContext) throws IOException;
/*   0*/  
/*   0*/  protected T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
/* 165*/    ctxt.reportMappingException("Don't know how to convert embedded Object of type %s into %s", new Object[] { ob.getClass().getName(), this._valueClass.getName() });
/* 167*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected T _deserializeFromEmptyString() throws IOException {
/* 171*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static class Std extends FromStringDeserializer<Object> {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    public static final int STD_FILE = 1;
/*   0*/    
/*   0*/    public static final int STD_URL = 2;
/*   0*/    
/*   0*/    public static final int STD_URI = 3;
/*   0*/    
/*   0*/    public static final int STD_CLASS = 4;
/*   0*/    
/*   0*/    public static final int STD_JAVA_TYPE = 5;
/*   0*/    
/*   0*/    public static final int STD_CURRENCY = 6;
/*   0*/    
/*   0*/    public static final int STD_PATTERN = 7;
/*   0*/    
/*   0*/    public static final int STD_LOCALE = 8;
/*   0*/    
/*   0*/    public static final int STD_CHARSET = 9;
/*   0*/    
/*   0*/    public static final int STD_TIME_ZONE = 10;
/*   0*/    
/*   0*/    public static final int STD_INET_ADDRESS = 11;
/*   0*/    
/*   0*/    public static final int STD_INET_SOCKET_ADDRESS = 12;
/*   0*/    
/*   0*/    public static final int STD_STRING_BUILDER = 13;
/*   0*/    
/*   0*/    protected final int _kind;
/*   0*/    
/*   0*/    protected Std(Class<?> valueType, int kind) {
/* 207*/      super(valueType);
/* 208*/      this._kind = kind;
/*   0*/    }
/*   0*/    
/*   0*/    protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
/*   0*/      int ix;
/*   0*/      String first, second;
/* 214*/      switch (this._kind) {
/*   0*/        case 1:
/* 216*/          return new File(value);
/*   0*/        case 2:
/* 218*/          return new URL(value);
/*   0*/        case 3:
/* 220*/          return URI.create(value);
/*   0*/        case 4:
/*   0*/          try {
/* 223*/            return ctxt.findClass(value);
/* 224*/          } catch (Exception e) {
/* 225*/            return ctxt.handleInstantiationProblem(this._valueClass, value, ClassUtil.getRootCause(e));
/*   0*/          } 
/*   0*/        case 5:
/* 229*/          return ctxt.getTypeFactory().constructFromCanonical(value);
/*   0*/        case 6:
/* 232*/          return Currency.getInstance(value);
/*   0*/        case 7:
/* 235*/          return Pattern.compile(value);
/*   0*/        case 8:
/* 238*/          ix = _firstHyphenOrUnderscore(value);
/* 239*/          if (ix < 0) {
/* 240*/              return new Locale(value); 
/*   0*/             }
/* 242*/          first = value.substring(0, ix);
/* 243*/          value = value.substring(ix + 1);
/* 244*/          ix = _firstHyphenOrUnderscore(value);
/* 245*/          if (ix < 0) {
/* 246*/              return new Locale(first, value); 
/*   0*/             }
/* 248*/          second = value.substring(0, ix);
/* 249*/          return new Locale(first, second, value.substring(ix + 1));
/*   0*/        case 9:
/* 252*/          return Charset.forName(value);
/*   0*/        case 10:
/* 254*/          return TimeZone.getTimeZone(value);
/*   0*/        case 11:
/* 256*/          return InetAddress.getByName(value);
/*   0*/        case 12:
/* 258*/          if (value.startsWith("[")) {
/* 261*/            int i = value.lastIndexOf(']');
/* 262*/            if (i == -1) {
/* 263*/                throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", value, InetSocketAddress.class); 
/*   0*/               }
/* 268*/            int j = value.indexOf(':', i);
/* 269*/            int port = (j > -1) ? Integer.parseInt(value.substring(j + 1)) : 0;
/* 270*/            return new InetSocketAddress(value.substring(0, i + 1), port);
/*   0*/          } 
/* 272*/          ix = value.indexOf(':');
/* 273*/          if (ix >= 0 && value.indexOf(':', ix + 1) < 0) {
/* 275*/            int port = Integer.parseInt(value.substring(ix + 1));
/* 276*/            return new InetSocketAddress(value.substring(0, ix), port);
/*   0*/          } 
/* 279*/          return new InetSocketAddress(value, 0);
/*   0*/        case 13:
/* 281*/          return new StringBuilder(value);
/*   0*/      } 
/* 283*/      VersionUtil.throwInternal();
/* 284*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    protected Object _deserializeFromEmptyString() throws IOException {
/* 290*/      if (this._kind == 3) {
/* 291*/          return URI.create(""); 
/*   0*/         }
/* 294*/      if (this._kind == 8) {
/* 295*/          return Locale.ROOT; 
/*   0*/         }
/* 297*/      if (this._kind == 13) {
/* 298*/          return new StringBuilder(); 
/*   0*/         }
/* 300*/      return super._deserializeFromEmptyString();
/*   0*/    }
/*   0*/    
/*   0*/    protected int _firstHyphenOrUnderscore(String str) {
/* 305*/      for (int i = 0, end = str.length(); i < end; i++) {
/* 306*/        char c = str.charAt(i);
/* 307*/        if (c == '_' || c == '-') {
/* 308*/            return i; 
/*   0*/           }
/*   0*/      } 
/* 311*/      return -1;
/*   0*/    }
/*   0*/  }
/*   0*/}
