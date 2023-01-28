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
/*  35*/    this._valueClass = vc;
/*   0*/  }
/*   0*/  
/*   0*/  protected StdDeserializer(JavaType valueType) {
/*  39*/    this._valueClass = (valueType == null) ? null : valueType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> handledType() {
/*  49*/    return this._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public final Class<?> getValueClass() {
/*  61*/    return this._valueClass;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getValueType() {
/*  68*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isDefaultDeserializer(JsonDeserializer<?> deserializer) {
/*  77*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
/*  81*/    return ClassUtil.isJacksonStdImpl(keyDeser);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/*  97*/    return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _parseBooleanPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 110*/    JsonToken t = jp.getCurrentToken();
/* 111*/    if (t == JsonToken.VALUE_TRUE) {
/* 111*/        return true; 
/*   0*/       }
/* 112*/    if (t == JsonToken.VALUE_FALSE) {
/* 112*/        return false; 
/*   0*/       }
/* 113*/    if (t == JsonToken.VALUE_NULL) {
/* 113*/        return false; 
/*   0*/       }
/* 116*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 118*/      if (jp.getNumberType() == JsonParser.NumberType.INT) {
/* 119*/          return (jp.getIntValue() != 0); 
/*   0*/         }
/* 121*/      return _parseBooleanFromNumber(jp, ctxt);
/*   0*/    } 
/* 124*/    if (t == JsonToken.VALUE_STRING) {
/* 125*/      String text = jp.getText().trim();
/* 127*/      if ("true".equals(text) || "True".equals(text)) {
/* 128*/          return true; 
/*   0*/         }
/* 130*/      if ("false".equals(text) || "False".equals(text) || text.length() == 0) {
/* 131*/          return false; 
/*   0*/         }
/* 133*/      if (_hasTextualNull(text)) {
/* 134*/          return false; 
/*   0*/         }
/* 136*/      throw ctxt.weirdStringException(text, this._valueClass, "only \"true\" or \"false\" recognized");
/*   0*/    } 
/* 139*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 140*/      jp.nextToken();
/* 141*/      boolean parsed = _parseBooleanPrimitive(jp, ctxt);
/* 142*/      t = jp.nextToken();
/* 143*/      if (t != JsonToken.END_ARRAY) {
/* 144*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'boolean' value but there was more than a single value in the array"); 
/*   0*/         }
/* 147*/      return parsed;
/*   0*/    } 
/* 150*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Boolean _parseBoolean(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 156*/    JsonToken t = jp.getCurrentToken();
/* 157*/    if (t == JsonToken.VALUE_TRUE) {
/* 158*/        return Boolean.TRUE; 
/*   0*/       }
/* 160*/    if (t == JsonToken.VALUE_FALSE) {
/* 161*/        return Boolean.FALSE; 
/*   0*/       }
/* 164*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 166*/      if (jp.getNumberType() == JsonParser.NumberType.INT) {
/* 167*/          return (jp.getIntValue() == 0) ? Boolean.FALSE : Boolean.TRUE; 
/*   0*/         }
/* 169*/      return _parseBooleanFromNumber(jp, ctxt);
/*   0*/    } 
/* 171*/    if (t == JsonToken.VALUE_NULL) {
/* 172*/        return (Boolean)getNullValue(); 
/*   0*/       }
/* 175*/    if (t == JsonToken.VALUE_STRING) {
/* 176*/      String text = jp.getText().trim();
/* 178*/      if ("true".equals(text) || "True".equals(text)) {
/* 179*/          return Boolean.TRUE; 
/*   0*/         }
/* 181*/      if ("false".equals(text) || "False".equals(text)) {
/* 182*/          return Boolean.FALSE; 
/*   0*/         }
/* 184*/      if (text.length() == 0) {
/* 185*/          return (Boolean)getEmptyValue(); 
/*   0*/         }
/* 187*/      if (_hasTextualNull(text)) {
/* 188*/          return (Boolean)getNullValue(); 
/*   0*/         }
/* 190*/      throw ctxt.weirdStringException(text, this._valueClass, "only \"true\" or \"false\" recognized");
/*   0*/    } 
/* 193*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 194*/      jp.nextToken();
/* 195*/      Boolean parsed = _parseBoolean(jp, ctxt);
/* 196*/      t = jp.nextToken();
/* 197*/      if (t != JsonToken.END_ARRAY) {
/* 198*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Boolean' value but there was more than a single value in the array"); 
/*   0*/         }
/* 201*/      return parsed;
/*   0*/    } 
/* 204*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _parseBooleanFromNumber(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 210*/    if (jp.getNumberType() == JsonParser.NumberType.LONG) {
/* 211*/        return (jp.getLongValue() == 0L) ? Boolean.FALSE : Boolean.TRUE; 
/*   0*/       }
/* 214*/    String str = jp.getText();
/* 215*/    if ("0.0".equals(str) || "0".equals(str)) {
/* 216*/        return Boolean.FALSE; 
/*   0*/       }
/* 218*/    return Boolean.TRUE;
/*   0*/  }
/*   0*/  
/*   0*/  protected Byte _parseByte(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 224*/    JsonToken t = jp.getCurrentToken();
/* 225*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 226*/        return jp.getByteValue(); 
/*   0*/       }
/* 228*/    if (t == JsonToken.VALUE_STRING) {
/*   0*/      int value;
/* 229*/      String text = jp.getText().trim();
/* 230*/      if (_hasTextualNull(text)) {
/* 231*/          return (Byte)getNullValue(); 
/*   0*/         }
/*   0*/      try {
/* 235*/        int len = text.length();
/* 236*/        if (len == 0) {
/* 237*/            return (Byte)getEmptyValue(); 
/*   0*/           }
/* 239*/        value = NumberInput.parseInt(text);
/* 240*/      } catch (IllegalArgumentException iae) {
/* 241*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Byte value");
/*   0*/      } 
/* 245*/      if (value < -128 || value > 255) {
/* 246*/          throw ctxt.weirdStringException(text, this._valueClass, "overflow, value can not be represented as 8-bit value"); 
/*   0*/         }
/* 248*/      return (byte)value;
/*   0*/    } 
/* 250*/    if (t == JsonToken.VALUE_NULL) {
/* 251*/        return (Byte)getNullValue(); 
/*   0*/       }
/* 254*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 255*/      jp.nextToken();
/* 256*/      Byte parsed = _parseByte(jp, ctxt);
/* 257*/      t = jp.nextToken();
/* 258*/      if (t != JsonToken.END_ARRAY) {
/* 259*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array"); 
/*   0*/         }
/* 262*/      return parsed;
/*   0*/    } 
/* 264*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected Short _parseShort(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 270*/    JsonToken t = jp.getCurrentToken();
/* 271*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 272*/        return jp.getShortValue(); 
/*   0*/       }
/* 274*/    if (t == JsonToken.VALUE_STRING) {
/*   0*/      int value;
/* 275*/      String text = jp.getText().trim();
/*   0*/      try {
/* 278*/        int len = text.length();
/* 279*/        if (len == 0) {
/* 280*/            return (Short)getEmptyValue(); 
/*   0*/           }
/* 282*/        if (_hasTextualNull(text)) {
/* 283*/            return (Short)getNullValue(); 
/*   0*/           }
/* 285*/        value = NumberInput.parseInt(text);
/* 286*/      } catch (IllegalArgumentException iae) {
/* 287*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Short value");
/*   0*/      } 
/* 290*/      if (value < -32768 || value > 32767) {
/* 291*/          throw ctxt.weirdStringException(text, this._valueClass, "overflow, value can not be represented as 16-bit value"); 
/*   0*/         }
/* 293*/      return (short)value;
/*   0*/    } 
/* 295*/    if (t == JsonToken.VALUE_NULL) {
/* 296*/        return (Short)getNullValue(); 
/*   0*/       }
/* 299*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 300*/      jp.nextToken();
/* 301*/      Short parsed = _parseShort(jp, ctxt);
/* 302*/      t = jp.nextToken();
/* 303*/      if (t != JsonToken.END_ARRAY) {
/* 304*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Short' value but there was more than a single value in the array"); 
/*   0*/         }
/* 307*/      return parsed;
/*   0*/    } 
/* 309*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final short _parseShortPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 315*/    int value = _parseIntPrimitive(jp, ctxt);
/* 317*/    if (value < -32768 || value > 32767) {
/* 318*/        throw ctxt.weirdStringException(String.valueOf(value), this._valueClass, "overflow, value can not be represented as 16-bit value"); 
/*   0*/       }
/* 321*/    return (short)value;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int _parseIntPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 327*/    JsonToken t = jp.getCurrentToken();
/* 330*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 331*/        return jp.getIntValue(); 
/*   0*/       }
/* 333*/    if (t == JsonToken.VALUE_STRING) {
/* 334*/      String text = jp.getText().trim();
/* 335*/      if (_hasTextualNull(text)) {
/* 336*/          return 0; 
/*   0*/         }
/*   0*/      try {
/* 339*/        int len = text.length();
/* 340*/        if (len > 9) {
/* 341*/          long l = Long.parseLong(text);
/* 342*/          if (l < -2147483648L || l > 2147483647L) {
/* 343*/              throw ctxt.weirdStringException(text, this._valueClass, "Overflow: numeric value (" + text + ") out of range of int (" + -2147483648 + " - " + 2147483647 + ")"); 
/*   0*/             }
/* 346*/          return (int)l;
/*   0*/        } 
/* 348*/        if (len == 0) {
/* 349*/            return 0; 
/*   0*/           }
/* 351*/        return NumberInput.parseInt(text);
/* 352*/      } catch (IllegalArgumentException iae) {
/* 353*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid int value");
/*   0*/      } 
/*   0*/    } 
/* 356*/    if (t == JsonToken.VALUE_NULL) {
/* 357*/        return 0; 
/*   0*/       }
/* 360*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 361*/      jp.nextToken();
/* 362*/      int parsed = _parseIntPrimitive(jp, ctxt);
/* 363*/      t = jp.nextToken();
/* 364*/      if (t != JsonToken.END_ARRAY) {
/* 365*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'int' value but there was more than a single value in the array"); 
/*   0*/         }
/* 368*/      return parsed;
/*   0*/    } 
/* 371*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Integer _parseInteger(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 377*/    JsonToken t = jp.getCurrentToken();
/* 378*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 379*/        return jp.getIntValue(); 
/*   0*/       }
/* 381*/    if (t == JsonToken.VALUE_STRING) {
/* 382*/      String text = jp.getText().trim();
/*   0*/      try {
/* 384*/        int len = text.length();
/* 385*/        if (_hasTextualNull(text)) {
/* 386*/            return (Integer)getNullValue(); 
/*   0*/           }
/* 388*/        if (len > 9) {
/* 389*/          long l = Long.parseLong(text);
/* 390*/          if (l < -2147483648L || l > 2147483647L) {
/* 391*/              throw ctxt.weirdStringException(text, this._valueClass, "Overflow: numeric value (" + text + ") out of range of Integer (" + -2147483648 + " - " + 2147483647 + ")"); 
/*   0*/             }
/* 394*/          return (int)l;
/*   0*/        } 
/* 396*/        if (len == 0) {
/* 397*/            return (Integer)getEmptyValue(); 
/*   0*/           }
/* 399*/        return NumberInput.parseInt(text);
/* 400*/      } catch (IllegalArgumentException iae) {
/* 401*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Integer value");
/*   0*/      } 
/*   0*/    } 
/* 404*/    if (t == JsonToken.VALUE_NULL) {
/* 405*/        return (Integer)getNullValue(); 
/*   0*/       }
/* 408*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 409*/      jp.nextToken();
/* 410*/      Integer parsed = _parseInteger(jp, ctxt);
/* 411*/      t = jp.nextToken();
/* 412*/      if (t != JsonToken.END_ARRAY) {
/* 413*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Integer' value but there was more than a single value in the array"); 
/*   0*/         }
/* 416*/      return parsed;
/*   0*/    } 
/* 419*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Long _parseLong(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 424*/    JsonToken t = jp.getCurrentToken();
/* 427*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 428*/        return jp.getLongValue(); 
/*   0*/       }
/* 431*/    if (t == JsonToken.VALUE_STRING) {
/* 433*/      String text = jp.getText().trim();
/* 434*/      if (text.length() == 0) {
/* 435*/          return (Long)getEmptyValue(); 
/*   0*/         }
/* 437*/      if (_hasTextualNull(text)) {
/* 438*/          return (Long)getNullValue(); 
/*   0*/         }
/*   0*/      try {
/* 441*/        return NumberInput.parseLong(text);
/* 442*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 443*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Long value");
/*   0*/      } 
/*   0*/    } 
/* 445*/    if (t == JsonToken.VALUE_NULL) {
/* 446*/        return (Long)getNullValue(); 
/*   0*/       }
/* 449*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 450*/      jp.nextToken();
/* 451*/      Long parsed = _parseLong(jp, ctxt);
/* 452*/      t = jp.nextToken();
/* 453*/      if (t != JsonToken.END_ARRAY) {
/* 454*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Long' value but there was more than a single value in the array"); 
/*   0*/         }
/* 457*/      return parsed;
/*   0*/    } 
/* 460*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final long _parseLongPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 466*/    JsonToken t = jp.getCurrentToken();
/* 467*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 468*/        return jp.getLongValue(); 
/*   0*/       }
/* 470*/    if (t == JsonToken.VALUE_STRING) {
/* 471*/      String text = jp.getText().trim();
/* 472*/      if (text.length() == 0 || _hasTextualNull(text)) {
/* 473*/          return 0L; 
/*   0*/         }
/*   0*/      try {
/* 476*/        return NumberInput.parseLong(text);
/* 477*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 478*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid long value");
/*   0*/      } 
/*   0*/    } 
/* 480*/    if (t == JsonToken.VALUE_NULL) {
/* 481*/        return 0L; 
/*   0*/       }
/* 484*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 485*/      jp.nextToken();
/* 486*/      long parsed = _parseLongPrimitive(jp, ctxt);
/* 487*/      t = jp.nextToken();
/* 488*/      if (t != JsonToken.END_ARRAY) {
/* 489*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'long' value but there was more than a single value in the array"); 
/*   0*/         }
/* 492*/      return parsed;
/*   0*/    } 
/* 494*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Float _parseFloat(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 501*/    JsonToken t = jp.getCurrentToken();
/* 503*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 504*/        return jp.getFloatValue(); 
/*   0*/       }
/* 507*/    if (t == JsonToken.VALUE_STRING) {
/* 508*/      String text = jp.getText().trim();
/* 509*/      if (text.length() == 0) {
/* 510*/          return (Float)getEmptyValue(); 
/*   0*/         }
/* 512*/      if (_hasTextualNull(text)) {
/* 513*/          return (Float)getNullValue(); 
/*   0*/         }
/* 515*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 517*/          if (_isPosInf(text)) {
/* 518*/              return Float.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 522*/          if (_isNaN(text)) {
/* 523*/              return Float.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 527*/          if (_isNegInf(text)) {
/* 528*/              return Float.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 533*/        return Float.parseFloat(text);
/* 534*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 535*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Float value");
/*   0*/      } 
/*   0*/    } 
/* 537*/    if (t == JsonToken.VALUE_NULL) {
/* 538*/        return (Float)getNullValue(); 
/*   0*/       }
/* 541*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 542*/      jp.nextToken();
/* 543*/      Float parsed = _parseFloat(jp, ctxt);
/* 544*/      t = jp.nextToken();
/* 545*/      if (t != JsonToken.END_ARRAY) {
/* 546*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array"); 
/*   0*/         }
/* 549*/      return parsed;
/*   0*/    } 
/* 552*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final float _parseFloatPrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 558*/    JsonToken t = jp.getCurrentToken();
/* 560*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 561*/        return jp.getFloatValue(); 
/*   0*/       }
/* 563*/    if (t == JsonToken.VALUE_STRING) {
/* 564*/      String text = jp.getText().trim();
/* 565*/      if (text.length() == 0 || _hasTextualNull(text)) {
/* 566*/          return 0.0F; 
/*   0*/         }
/* 568*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 570*/          if (_isPosInf(text)) {
/* 571*/              return Float.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 575*/          if (_isNaN(text)) {
/* 575*/              return Float.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 578*/          if (_isNegInf(text)) {
/* 579*/              return Float.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 584*/        return Float.parseFloat(text);
/* 585*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 586*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid float value");
/*   0*/      } 
/*   0*/    } 
/* 588*/    if (t == JsonToken.VALUE_NULL) {
/* 589*/        return 0.0F; 
/*   0*/       }
/* 592*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 593*/      jp.nextToken();
/* 594*/      float parsed = _parseFloatPrimitive(jp, ctxt);
/* 595*/      t = jp.nextToken();
/* 596*/      if (t != JsonToken.END_ARRAY) {
/* 597*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'float' value but there was more than a single value in the array"); 
/*   0*/         }
/* 600*/      return parsed;
/*   0*/    } 
/* 603*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Double _parseDouble(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 609*/    JsonToken t = jp.getCurrentToken();
/* 611*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 612*/        return jp.getDoubleValue(); 
/*   0*/       }
/* 614*/    if (t == JsonToken.VALUE_STRING) {
/* 615*/      String text = jp.getText().trim();
/* 616*/      if (text.length() == 0) {
/* 617*/          return (Double)getEmptyValue(); 
/*   0*/         }
/* 619*/      if (_hasTextualNull(text)) {
/* 620*/          return (Double)getNullValue(); 
/*   0*/         }
/* 622*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 624*/          if (_isPosInf(text)) {
/* 625*/              return Double.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 629*/          if (_isNaN(text)) {
/* 630*/              return Double.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 634*/          if (_isNegInf(text)) {
/* 635*/              return Double.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 640*/        return parseDouble(text);
/* 641*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 642*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid Double value");
/*   0*/      } 
/*   0*/    } 
/* 644*/    if (t == JsonToken.VALUE_NULL) {
/* 645*/        return (Double)getNullValue(); 
/*   0*/       }
/* 648*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 649*/      jp.nextToken();
/* 650*/      Double parsed = _parseDouble(jp, ctxt);
/* 651*/      t = jp.nextToken();
/* 652*/      if (t != JsonToken.END_ARRAY) {
/* 653*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Double' value but there was more than a single value in the array"); 
/*   0*/         }
/* 656*/      return parsed;
/*   0*/    } 
/* 659*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final double _parseDoublePrimitive(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 666*/    JsonToken t = jp.getCurrentToken();
/* 668*/    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
/* 669*/        return jp.getDoubleValue(); 
/*   0*/       }
/* 672*/    if (t == JsonToken.VALUE_STRING) {
/* 673*/      String text = jp.getText().trim();
/* 674*/      if (text.length() == 0 || _hasTextualNull(text)) {
/* 675*/          return 0.0D; 
/*   0*/         }
/* 677*/      switch (text.charAt(0)) {
/*   0*/        case 'I':
/* 679*/          if (_isPosInf(text)) {
/* 680*/              return Double.POSITIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case 'N':
/* 684*/          if (_isNaN(text)) {
/* 685*/              return Double.NaN; 
/*   0*/             }
/*   0*/          break;
/*   0*/        case '-':
/* 689*/          if (_isNegInf(text)) {
/* 690*/              return Double.NEGATIVE_INFINITY; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/*   0*/      try {
/* 695*/        return parseDouble(text);
/* 696*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 697*/        throw ctxt.weirdStringException(text, this._valueClass, "not a valid double value");
/*   0*/      } 
/*   0*/    } 
/* 699*/    if (t == JsonToken.VALUE_NULL) {
/* 700*/        return 0.0D; 
/*   0*/       }
/* 703*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 704*/      jp.nextToken();
/* 705*/      double parsed = _parseDoublePrimitive(jp, ctxt);
/* 706*/      t = jp.nextToken();
/* 707*/      if (t != JsonToken.END_ARRAY) {
/* 708*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'Byte' value but there was more than a single value in the array"); 
/*   0*/         }
/* 711*/      return parsed;
/*   0*/    } 
/* 714*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected Date _parseDate(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 720*/    JsonToken t = jp.getCurrentToken();
/* 721*/    if (t == JsonToken.VALUE_NUMBER_INT) {
/* 722*/        return new Date(jp.getLongValue()); 
/*   0*/       }
/* 724*/    if (t == JsonToken.VALUE_NULL) {
/* 725*/        return (Date)getNullValue(); 
/*   0*/       }
/* 727*/    if (t == JsonToken.VALUE_STRING) {
/* 728*/      String value = null;
/*   0*/      try {
/* 731*/        value = jp.getText().trim();
/* 732*/        if (value.length() == 0) {
/* 733*/            return (Date)getEmptyValue(); 
/*   0*/           }
/* 735*/        if (_hasTextualNull(value)) {
/* 736*/            return (Date)getNullValue(); 
/*   0*/           }
/* 738*/        return ctxt.parseDate(value);
/* 739*/      } catch (IllegalArgumentException iae) {
/* 740*/        throw ctxt.weirdStringException(value, this._valueClass, "not a valid representation (error: " + iae.getMessage() + ")");
/*   0*/      } 
/*   0*/    } 
/* 745*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 746*/      jp.nextToken();
/* 747*/      Date parsed = _parseDate(jp, ctxt);
/* 748*/      t = jp.nextToken();
/* 749*/      if (t != JsonToken.END_ARRAY) {
/* 750*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'java.util.Date' value but there was more than a single value in the array"); 
/*   0*/         }
/* 753*/      return parsed;
/*   0*/    } 
/* 755*/    throw ctxt.mappingException(this._valueClass, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected static final double parseDouble(String numStr) throws NumberFormatException {
/* 766*/    if ("2.2250738585072012e-308".equals(numStr)) {
/* 767*/        return Double.MIN_VALUE; 
/*   0*/       }
/* 769*/    return Double.parseDouble(numStr);
/*   0*/  }
/*   0*/  
/*   0*/  protected final String _parseString(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 780*/    JsonToken t = jp.getCurrentToken();
/* 781*/    if (t == JsonToken.VALUE_STRING) {
/* 782*/        return jp.getText(); 
/*   0*/       }
/* 786*/    if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 787*/      jp.nextToken();
/* 788*/      String parsed = _parseString(jp, ctxt);
/* 789*/      if (jp.nextToken() != JsonToken.END_ARRAY) {
/* 790*/          throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'String' value but there was more than a single value in the array"); 
/*   0*/         }
/* 793*/      return parsed;
/*   0*/    } 
/* 795*/    String value = jp.getValueAsString();
/* 796*/    if (value != null) {
/* 797*/        return value; 
/*   0*/       }
/* 799*/    throw ctxt.mappingException(String.class, jp.getCurrentToken());
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _hasTextualNull(String value) {
/* 810*/    return "null".equals(value);
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _isNegInf(String text) {
/* 814*/    return ("-Infinity".equals(text) || "-INF".equals(text));
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _isPosInf(String text) {
/* 818*/    return ("Infinity".equals(text) || "INF".equals(text));
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _isNaN(String text) {
/* 821*/    return "NaN".equals(text);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property) throws JsonMappingException {
/* 842*/    return ctxt.findContextualValueDeserializer(type, property);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
/* 865*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 866*/    if (intr != null && prop != null) {
/* 867*/      Object convDef = intr.findDeserializationContentConverter(prop.getMember());
/* 868*/      if (convDef != null) {
/* 869*/        Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
/* 870*/        JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 871*/        if (existingDeserializer == null) {
/* 872*/            existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop); 
/*   0*/           }
/* 874*/        return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
/*   0*/      } 
/*   0*/    } 
/* 877*/    return existingDeserializer;
/*   0*/  }
/*   0*/  
/*   0*/  protected void handleUnknownProperty(JsonParser jp, DeserializationContext ctxt, Object<?> instanceOrClass, String propName) throws IOException {
/* 907*/    if (instanceOrClass == null) {
/* 908*/        instanceOrClass = (Object<?>)handledType(); 
/*   0*/       }
/* 911*/    if (ctxt.handleUnknownProperty(jp, this, instanceOrClass, propName)) {
/*   0*/        return; 
/*   0*/       }
/* 915*/    ctxt.reportUnknownProperty(instanceOrClass, propName, this);
/* 920*/    jp.skipChildren();
/*   0*/  }
/*   0*/}
