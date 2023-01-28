/*   0*/package com.fasterxml.jackson.databind.deser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.io.NumberInput;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.KeyDeserializer;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.Converter;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Date;
/*   0*/
/*   0*/public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final Class<?> _valueClass;
/*   0*/  
/*   0*/  protected StdDeserializer(Class<?> vc) {
/*  36*/    this._valueClass = vc;
/*   0*/  }
/*   0*/  
/*   0*/  protected StdDeserializer(JavaType valueType) {
/*  40*/    this._valueClass = (valueType == null) ? null : valueType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  protected StdDeserializer(StdDeserializer<?> src) {
/*  50*/    this._valueClass = src._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> handledType() {
/*  60*/    return this._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public final Class<?> getValueClass() {
/*  72*/    return this._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getValueType() {
/*  79*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isDefaultDeserializer(JsonDeserializer<?> deserializer) {
/*  88*/    return ClassUtil.isJacksonStdImpl(deserializer);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
/*  92*/    return ClassUtil.isJacksonStdImpl(keyDeser);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 108*/    return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _parseBooleanPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 121*/    JsonToken t = jp.getCurrentToken();
/* 122*/    if (t == JsonToken.VALUE_TRUE) {
/* 122*/        return true; 
/*   0*/       }
/* 123*/    if (t == JsonToken.VALUE_FALSE) {
/* 123*/        return false; 
/*   0*/       }
/* 124*/    if (t == JsonToken.VALUE_NULL) {
/* 124*/        return false; 
/*   0*/       }
/* 127*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 129*/      if (jp.getNumberType() == JsonParser.NumberType.INT) {
/* 130*/          return (jp.getIntValue() != 0); 
/*   0*/         }
/* 132*/      return _parseBooleanFromNumber(jp, ctxt);
/*   0*/    } 
/* 135*/    if (t == JsonToken.VALUE_STRING) {
/* 136*/      String text = jp.getText().trim();
/* 138*/      if ("true".equals(text) || "True".equals(text)) {
/* 139*/          return true; 
/*   0*/         }
/* 141*/      if ("false".equals(text) || "False".equals(text) || text.length() == 0) {
/* 142*/          return false; 
/*   0*/         }
/* 144*/      if (_hasTextualNull(text)) {
/* 145*/          return false; 
/*   0*/         }
/* 147*/      throw ctxt.weirdStringException(text, this._valueClass, "only \"true\" or \"false\" recognized");
/*   0*/    } 
/* 150*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 151*/      jp.nextToken();
/* 152*/      boolean parsed = _parseBooleanPrimitive(jp, ctxt);
/* 153*/      t = jp.nextToken();
/* 154*/      if (t != JsonToken.END_ARRAY) {
/* 155*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'boolean' value but there was more than a single value in the array"); 
/*   0*/         }
/* 158*/      return parsed;
/*   0*/    } 
/* 161*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Boolean _parseBoolean(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 167*/    JsonToken t = jp.getCurrentToken();
/* 168*/    if (t == JsonToken.VALUE_TRUE) {
/* 169*/        return Boolean.TRUE; 
/*   0*/       }
/* 171*/    if (t == JsonToken.VALUE_FALSE) {
/* 172*/        return Boolean.FALSE; 
/*   0*/       }
/* 175*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 177*/      if (jp.getNumberType() == JsonParser.NumberType.INT) {
/* 178*/          return (jp.getIntValue() == 0) ? Boolean.FALSE : Boolean.TRUE; 
/*   0*/         }
/* 180*/      return _parseBooleanFromNumber(jp, ctxt);
/*   0*/    } 
/* 182*/    if (t == JsonToken.VALUE_NULL) {
/* 183*/        return (Boolean)getNullValue(); 
/*   0*/       }
/* 186*/    if (t == JsonToken.VALUE_STRING) {
/* 187*/      String text = jp.getText().trim();
/* 189*/      if ("true".equals(text) || "True".equals(text)) {
/* 190*/          return Boolean.TRUE; 
/*   0*/         }
/* 192*/      if ("false".equals(text) || "False".equals(text)) {
/* 193*/          return Boolean.FALSE; 
/*   0*/         }
/* 195*/      if (text.length() == 0) {
/* 196*/          return (Boolean)getEmptyValue(); 
/*   0*/         }
/* 198*/      if (_hasTextualNull(text)) {
/* 199*/          return (Boolean)getNullValue(); 
/*   0*/         }
/* 201*/      throw ctxt.weirdStringException(text, this._valueClass, "only \"true\" or \"false\" recognized");
/*   0*/    } 
/* 204*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 205*/      jp.nextToken();
/* 206*/      Boolean parsed = _parseBoolean(jp, ctxt);
/* 207*/      t = jp.nextToken();
/* 208*/      if (t != JsonToken.END_ARRAY) {
/* 209*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Boolean' value but there was more than a single value in the array"); 
/*   0*/         }
/* 212*/      return parsed;
/*   0*/    } 
/* 215*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _parseBooleanFromNumber(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 221*/    if (jp.getNumberType() == JsonParser.NumberType.LONG) {
/* 222*/        return (jp.getLongValue() == 0L) ? Boolean.FALSE : Boolean.TRUE; 
/*   0*/       }
/* 225*/    String str = jp.getText();
/* 226*/    if ("0.0".equals(str) || "0".equals(str)) {
/* 227*/        return Boolean.FALSE; 
/*   0*/       }
/* 229*/    return Boolean.TRUE;
/*   0*/  }
/*   0*/  
/*   0*/  protected Byte _parseByte(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 235*/    JsonToken t = jp.getCurrentToken();
/* 236*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 237*/        return jp.getByteValue(); 
/*   0*/       }
/* 239*/    if (t == JsonToken.VALUE_STRING) {
/*   0*/      int value;
/* 240*/      String text = jp.getText().trim();
/* 241*/      if (_hasTextualNull(text)) {
/* 242*/          return (Byte)getNullValue(); 
/*   0*/         }
/*   0*/      try {
/* 246*/        int len = text.length();
/* 247*/        if (len == 0) {
/* 248*/            return (Byte)getEmptyValue(); 
/*   0*/           }
/* 250*/        value = NumberInput.parseInt(text);
/* 251*/      } catch (IllegalArgumentException iae) {
/* 252*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Byte value");
/*   0*/      } 
/* 256*/      if (value < -128 || value > 255) {
/* 257*/          throw ctxt.weirdStringException(text, this._valueClass, "overflow, value can not be represented as 8-bit value"); 
/*   0*/         }
/* 259*/      return (byte)value;
/*   0*/    } 
/* 261*/    if (t == JsonToken.VALUE_NULL) {
/* 262*/        return (Byte)getNullValue(); 
/*   0*/       }
/* 265*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 266*/      jp.nextToken();
/* 267*/      Byte parsed = _parseByte(jp, ctxt);
/* 268*/      t = jp.nextToken();
/* 269*/      if (t != JsonToken.END_ARRAY) {
/* 270*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array"); 
/*   0*/         }
/* 273*/      return parsed;
/*   0*/    } 
/* 275*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected Short _parseShort(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 281*/    JsonToken t = jp.getCurrentToken();
/* 282*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 283*/        return jp.getShortValue(); 
/*   0*/       }
/* 285*/    if (t == JsonToken.VALUE_STRING) {
/*   0*/      int value;
/* 286*/      String text = jp.getText().trim();
/*   0*/      try {
/* 289*/        int len = text.length();
/* 290*/        if (len == 0) {
/* 291*/            return (Short)getEmptyValue(); 
/*   0*/           }
/* 293*/        if (_hasTextualNull(text)) {
/* 294*/            return (Short)getNullValue(); 
/*   0*/           }
/* 296*/        value = NumberInput.parseInt(text);
/* 297*/      } catch (IllegalArgumentException iae) {
/* 298*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Short value");
/*   0*/      } 
/* 301*/      if (value < -32768 || value > 32767) {
/* 302*/          throw ctxt.weirdStringException(text, this._valueClass, "overflow, value can not be represented as 16-bit value"); 
/*   0*/         }
/* 304*/      return (short)value;
/*   0*/    } 
/* 306*/    if (t == JsonToken.VALUE_NULL) {
/* 307*/        return (Short)getNullValue(); 
/*   0*/       }
/* 310*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 311*/      jp.nextToken();
/* 312*/      Short parsed = _parseShort(jp, ctxt);
/* 313*/      t = jp.nextToken();
/* 314*/      if (t != JsonToken.END_ARRAY) {
/* 315*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Short' value but there was more than a single value in the array"); 
/*   0*/         }
/* 318*/      return parsed;
/*   0*/    } 
/* 320*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final short _parseShortPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 326*/    int value = _parseIntPrimitive(jp, ctxt);
/* 328*/    if (value < -32768 || value > 32767) {
/* 329*/        throw ctxt.weirdStringException(String.valueOf(value), this._valueClass, "overflow, value can not be represented as 16-bit value"); 
/*   0*/       }
/* 332*/    return (short)value;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int _parseIntPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 338*/    JsonToken t = jp.getCurrentToken();
/* 341*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 342*/        return jp.getIntValue(); 
/*   0*/       }
/* 344*/    if (t == JsonToken.VALUE_STRING) {
/* 345*/      String text = jp.getText().trim();
/* 346*/      if (_hasTextualNull(text)) {
/* 347*/          return 0; 
/*   0*/         }
/*   0*/      try {
/* 350*/        int len = text.length();
/* 351*/        if (len > 9) {
/* 352*/          long l = Long.parseLong(text);
/* 353*/          if (l < -2147483648L || l > 2147483647L) {
/* 354*/              throw ctxt.weirdStringException(text, this._valueClass, "Overflow: numeric value (" + text + ") out of range of int (" + -2147483648 + " - " + 2147483647 + ")"); 
/*   0*/             }
/* 357*/          return (int)l;
/*   0*/        } 
/* 359*/        if (len == 0) {
/* 360*/            return 0; 
/*   0*/           }
/* 362*/        return NumberInput.parseInt(text);
/* 363*/      } catch (IllegalArgumentException iae) {
/* 364*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid int value");
/*   0*/      } 
/*   0*/    } 
/* 367*/    if (t == JsonToken.VALUE_NULL) {
/* 368*/        return 0; 
/*   0*/       }
/* 371*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 372*/      jp.nextToken();
/* 373*/      int parsed = _parseIntPrimitive(jp, ctxt);
/* 374*/      t = jp.nextToken();
/* 375*/      if (t != JsonToken.END_ARRAY) {
/* 376*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'int' value but there was more than a single value in the array"); 
/*   0*/         }
/* 379*/      return parsed;
/*   0*/    } 
/* 382*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Integer _parseInteger(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 388*/    JsonToken t = jp.getCurrentToken();
/* 389*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 390*/        return jp.getIntValue(); 
/*   0*/       }
/* 392*/    if (t == JsonToken.VALUE_STRING) {
/* 393*/      String text = jp.getText().trim();
/*   0*/      try {
/* 395*/        int len = text.length();
/* 396*/        if (_hasTextualNull(text)) {
/* 397*/            return (Integer)getNullValue(); 
/*   0*/           }
/* 399*/        if (len > 9) {
/* 400*/          long l = Long.parseLong(text);
/* 401*/          if (l < -2147483648L || l > 2147483647L) {
/* 402*/              throw ctxt.weirdStringException(text, this._valueClass, "Overflow: numeric value (" + text + ") out of range of Integer (" + -2147483648 + " - " + 2147483647 + ")"); 
/*   0*/             }
/* 405*/          return (int)l;
/*   0*/        } 
/* 407*/        if (len == 0) {
/* 408*/            return (Integer)getEmptyValue(); 
/*   0*/           }
/* 410*/        return NumberInput.parseInt(text);
/* 411*/      } catch (IllegalArgumentException iae) {
/* 412*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Integer value");
/*   0*/      } 
/*   0*/    } 
/* 415*/    if (t == JsonToken.VALUE_NULL) {
/* 416*/      (Integer)getNullValue();
/* 416*/      return 0;
/*   0*/    } 
/* 419*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 420*/      jp.nextToken();
/* 421*/      Integer parsed = _parseInteger(jp, ctxt);
/* 422*/      t = jp.nextToken();
/* 423*/      if (t != JsonToken.END_ARRAY) {
/* 424*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Integer' value but there was more than a single value in the array"); 
/*   0*/         }
/* 427*/      return parsed;
/*   0*/    } 
/* 430*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Long _parseLong(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 435*/    JsonToken t = jp.getCurrentToken();
/* 438*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 439*/        return jp.getLongValue(); 
/*   0*/       }
/* 442*/    if (t == JsonToken.VALUE_STRING) {
/* 444*/      String text = jp.getText().trim();
/* 445*/      if (text.length() == 0) {
/* 446*/          return (Long)getEmptyValue(); 
/*   0*/         }
/* 448*/      if (_hasTextualNull(text)) {
/* 449*/          return (Long)getNullValue(); 
/*   0*/         }
/*   0*/      try {
/* 452*/        return NumberInput.parseLong(text);
/* 453*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 454*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Long value");
/*   0*/      } 
/*   0*/    } 
/* 456*/    if (t == JsonToken.VALUE_NULL) {
/* 457*/        return (Long)getNullValue(); 
/*   0*/       }
/* 460*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 461*/      jp.nextToken();
/* 462*/      Long parsed = _parseLong(jp, ctxt);
/* 463*/      t = jp.nextToken();
/* 464*/      if (t != JsonToken.END_ARRAY) {
/* 465*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Long' value but there was more than a single value in the array"); 
/*   0*/         }
/* 468*/      return parsed;
/*   0*/    } 
/* 471*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final long _parseLongPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 477*/    JsonToken t = jp.getCurrentToken();
/* 478*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 479*/        return jp.getLongValue(); 
/*   0*/       }
/* 481*/    if (t == JsonToken.VALUE_STRING) {
/* 482*/      String text = jp.getText().trim();
/* 483*/      if (text.length() == 0 || _hasTextualNull(text)) {
/* 484*/          return 0L; 
/*   0*/         }
/*   0*/      try {
/* 487*/        return NumberInput.parseLong(text);
/* 488*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 489*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid long value");
/*   0*/      } 
/*   0*/    } 
/* 491*/    if (t == JsonToken.VALUE_NULL) {
/* 492*/        return 0L; 
/*   0*/       }
/* 495*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 496*/      jp.nextToken();
/* 497*/      long parsed = _parseLongPrimitive(jp, ctxt);
/* 498*/      t = jp.nextToken();
/* 499*/      if (t != JsonToken.END_ARRAY) {
/* 500*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'long' value but there was more than a single value in the array"); 
/*   0*/         }
/* 503*/      return parsed;
/*   0*/    } 
/* 505*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Float _parseFloat(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 512*/    JsonToken t = jp.getCurrentToken();
/* 514*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 515*/        return jp.getFloatValue(); 
/*   0*/       }
/* 518*/    if (t == JsonToken.VALUE_STRING) {
/* 519*/      String text = jp.getText().trim();
/* 520*/      if (text.length() == 0) {
/* 521*/          return (Float)getEmptyValue(); 
/*   0*/         }
/* 523*/      if (_hasTextualNull(text)) {
/* 524*/          return (Float)getNullValue(); 
/*   0*/         }
/* 526*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 528*/          if (_isPosInf(text)) {
/* 529*/              return Float.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 533*/          if (_isNaN(text)) {
/* 534*/              return Float.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 538*/          if (_isNegInf(text)) {
/* 539*/              return Float.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 544*/        return Float.parseFloat(text);
/* 545*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 546*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Float value");
/*   0*/      } 
/*   0*/    } 
/* 548*/    if (t == JsonToken.VALUE_NULL) {
/* 549*/        return (Float)getNullValue(); 
/*   0*/       }
/* 552*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 553*/      jp.nextToken();
/* 554*/      Float parsed = _parseFloat(jp, ctxt);
/* 555*/      t = jp.nextToken();
/* 556*/      if (t != JsonToken.END_ARRAY) {
/* 557*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array"); 
/*   0*/         }
/* 560*/      return parsed;
/*   0*/    } 
/* 563*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final float _parseFloatPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 569*/    JsonToken t = jp.getCurrentToken();
/* 571*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 572*/        return jp.getFloatValue(); 
/*   0*/       }
/* 574*/    if (t == JsonToken.VALUE_STRING) {
/* 575*/      String text = jp.getText().trim();
/* 576*/      if (text.length() == 0 || _hasTextualNull(text)) {
/* 577*/          return 0.0F; 
/*   0*/         }
/* 579*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 581*/          if (_isPosInf(text)) {
/* 582*/              return Float.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 586*/          if (_isNaN(text)) {
/* 586*/              return Float.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 589*/          if (_isNegInf(text)) {
/* 590*/              return Float.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 595*/        return Float.parseFloat(text);
/* 596*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 597*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid float value");
/*   0*/      } 
/*   0*/    } 
/* 599*/    if (t == JsonToken.VALUE_NULL) {
/* 600*/        return 0.0F; 
/*   0*/       }
/* 603*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 604*/      jp.nextToken();
/* 605*/      float parsed = _parseFloatPrimitive(jp, ctxt);
/* 606*/      t = jp.nextToken();
/* 607*/      if (t != JsonToken.END_ARRAY) {
/* 608*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'float' value but there was more than a single value in the array"); 
/*   0*/         }
/* 611*/      return parsed;
/*   0*/    } 
/* 614*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Double _parseDouble(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 620*/    JsonToken t = jp.getCurrentToken();
/* 622*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 623*/        return jp.getDoubleValue(); 
/*   0*/       }
/* 625*/    if (t == JsonToken.VALUE_STRING) {
/* 626*/      String text = jp.getText().trim();
/* 627*/      if (text.length() == 0) {
/* 628*/          return (Double)getEmptyValue(); 
/*   0*/         }
/* 630*/      if (_hasTextualNull(text)) {
/* 631*/          return (Double)getNullValue(); 
/*   0*/         }
/* 633*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 635*/          if (_isPosInf(text)) {
/* 636*/              return Double.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 640*/          if (_isNaN(text)) {
/* 641*/              return Double.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 645*/          if (_isNegInf(text)) {
/* 646*/              return Double.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 651*/        return parseDouble(text);
/* 652*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 653*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Double value");
/*   0*/      } 
/*   0*/    } 
/* 655*/    if (t == JsonToken.VALUE_NULL) {
/* 656*/        return (Double)getNullValue(); 
/*   0*/       }
/* 659*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 660*/      jp.nextToken();
/* 661*/      Double parsed = _parseDouble(jp, ctxt);
/* 662*/      t = jp.nextToken();
/* 663*/      if (t != JsonToken.END_ARRAY) {
/* 664*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Double' value but there was more than a single value in the array"); 
/*   0*/         }
/* 667*/      return parsed;
/*   0*/    } 
/* 670*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final double _parseDoublePrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 677*/    JsonToken t = jp.getCurrentToken();
/* 679*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 680*/        return jp.getDoubleValue(); 
/*   0*/       }
/* 683*/    if (t == JsonToken.VALUE_STRING) {
/* 684*/      String text = jp.getText().trim();
/* 685*/      if (text.length() == 0 || _hasTextualNull(text)) {
/* 686*/          return 0.0D; 
/*   0*/         }
/* 688*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 690*/          if (_isPosInf(text)) {
/* 691*/              return Double.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 695*/          if (_isNaN(text)) {
/* 696*/              return Double.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 700*/          if (_isNegInf(text)) {
/* 701*/              return Double.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 706*/        return parseDouble(text);
/* 707*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 708*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid double value");
/*   0*/      } 
/*   0*/    } 
/* 710*/    if (t == JsonToken.VALUE_NULL) {
/* 711*/        return 0.0D; 
/*   0*/       }
/* 714*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 715*/      jp.nextToken();
/* 716*/      double parsed = _parseDoublePrimitive(jp, ctxt);
/* 717*/      t = jp.nextToken();
/* 718*/      if (t != JsonToken.END_ARRAY) {
/* 719*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array"); 
/*   0*/         }
/* 722*/      return parsed;
/*   0*/    } 
/* 725*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected Date _parseDate(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 731*/    JsonToken t = jp.getCurrentToken();
/* 732*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 733*/        return new Date(jp.getLongValue()); 
/*   0*/       }
/* 735*/    if (t == JsonToken.VALUE_NULL) {
/* 736*/        return (Date)getNullValue(); 
/*   0*/       }
/* 738*/    if (t == JsonToken.VALUE_STRING) {
/* 739*/      String value = null;
/*   0*/      try {
/* 742*/        value = jp.getText().trim();
/* 743*/        if (value.length() == 0) {
/* 744*/            return (Date)getEmptyValue(); 
/*   0*/           }
/* 746*/        if (_hasTextualNull(value)) {
/* 747*/            return (Date)getNullValue(); 
/*   0*/           }
/* 749*/        return ctxt.parseDate(value);
/* 750*/      } catch (IllegalArgumentException iae) {
/* 751*/        throw ctxt.weirdStringException(value, this._valueClass, "not a valid representation (error: " + iae.getMessage() + ")");
/*   0*/      } 
/*   0*/    } 
/* 756*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 757*/      jp.nextToken();
/* 758*/      Date parsed = _parseDate(jp, ctxt);
/* 759*/      t = jp.nextToken();
/* 760*/      if (t != JsonToken.END_ARRAY) {
/* 761*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'java.util.Date' value but there was more than a single value in the array"); 
/*   0*/         }
/* 764*/      return parsed;
/*   0*/    } 
/* 766*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected static final double parseDouble(String numStr) throws NumberFormatException {
/* 777*/    if ("2.2250738585072012e-308".equals(numStr)) {
/* 778*/        return Double.MIN_VALUE; 
/*   0*/       }
/* 780*/    return Double.parseDouble(numStr);
/*   0*/  }
/*   0*/  
/*   0*/  protected final String _parseString(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 791*/    JsonToken t = jp.getCurrentToken();
/* 792*/    if (t == JsonToken.VALUE_STRING) {
/* 793*/        return jp.getText(); 
/*   0*/       }
/* 797*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 798*/      jp.nextToken();
/* 799*/      String parsed = _parseString(jp, ctxt);
/* 800*/      if (jp.nextToken() != JsonToken.END_ARRAY) {
/* 801*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'String' value but there was more than a single value in the array"); 
/*   0*/         }
/* 804*/      return parsed;
/*   0*/    } 
/* 806*/    String value = jp.getValueAsString();
/* 807*/    if (value != null) {
/* 808*/        return value; 
/*   0*/       }
/* 810*/    throw ctxt.mappingException(String.class, jp.getCurrentToken());
/*   0*/  }
/*   0*/  
/*   0*/  protected T _deserializeFromEmpty(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 822*/    JsonToken t = jp.getCurrentToken();
/* 823*/    if (t == JsonToken.START_ARRAY) {
/* 824*/      if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
/* 825*/        t = jp.nextToken();
/* 826*/        if (t == JsonToken.END_ARRAY) {
/* 827*/            return null; 
/*   0*/           }
/* 829*/        throw ctxt.mappingException(handledType(), JsonToken.START_ARRAY);
/*   0*/      } 
/* 831*/    } else if (t == JsonToken.VALUE_STRING && 
/* 832*/      ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 833*/      String str = jp.getText().trim();
/* 834*/      if (str.isEmpty()) {
/* 835*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 839*/    throw ctxt.mappingException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _hasTextualNull(String value) {
/* 850*/    return "null".equals(value);
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _isNegInf(String text) {
/* 854*/    return ("-Infinity".equals(text) || "-INF".equals(text));
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _isPosInf(String text) {
/* 858*/    return ("Infinity".equals(text) || "INF".equals(text));
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _isNaN(String text) {
/* 861*/    return "NaN".equals(text);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property) throws JsonMappingException {
/* 882*/    return ctxt.findContextualValueDeserializer(type, property);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
/* 905*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 906*/    if (intr != null && prop != null) {
/* 907*/      AnnotatedMember member = prop.getMember();
/* 908*/      if (member != null) {
/* 909*/        Object convDef = intr.findDeserializationContentConverter(member);
/* 910*/        if (convDef != null) {
/* 911*/          Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
/* 912*/          JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 913*/          if (existingDeserializer == null) {
/* 914*/              existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop); 
/*   0*/             }
/* 916*/          return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 920*/    return existingDeserializer;
/*   0*/  }
/*   0*/  
/*   0*/  protected void handleUnknownProperty(JsonParser jp, DeserializationContext ctxt, Object<?> instanceOrClass, String propName) throws IOException {
/* 950*/    if (instanceOrClass == null) {
/* 951*/        instanceOrClass = (Object<?>)handledType(); 
/*   0*/       }
/* 954*/    if (ctxt.handleUnknownProperty(jp, this, instanceOrClass, propName)) {
/*   0*/        return; 
/*   0*/       }
/* 958*/    ctxt.reportUnknownProperty(instanceOrClass, propName, this);
/* 963*/    jp.skipChildren();
/*   0*/  }
/*   0*/}
