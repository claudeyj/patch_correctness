/*   0*/package com.fasterxml.jackson.databind.deser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*   0*/import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.util.ObjectBuffer;
/*   0*/import java.io.IOException;
/*   0*/
/*   0*/@JacksonStdImpl
/*   0*/public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer {
/*   0*/  private static final long serialVersionUID = -7589512013334920693L;
/*   0*/  
/*  25*/  public static final StringArrayDeserializer instance = new StringArrayDeserializer();
/*   0*/  
/*   0*/  protected JsonDeserializer<String> _elementDeserializer;
/*   0*/  
/*   0*/  public StringArrayDeserializer() {
/*  33*/    super(String[].class);
/*  34*/    this._elementDeserializer = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected StringArrayDeserializer(JsonDeserializer<?> deser) {
/*  39*/    super(String[].class);
/*  40*/    this._elementDeserializer = (JsonDeserializer)deser;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*  47*/    if (!jp.isExpectedStartArrayToken()) {
/*  48*/        return handleNonArray(jp, ctxt); 
/*   0*/       }
/*  50*/    if (this._elementDeserializer != null) {
/*  51*/        return _deserializeCustom(jp, ctxt); 
/*   0*/       }
/*  54*/    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*  55*/    Object[] chunk = buffer.resetAndStart();
/*  57*/    int ix = 0;
/*   0*/    JsonToken t;
/*  60*/    while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
/*   0*/      String value;
/*  63*/      if (t == JsonToken.VALUE_STRING) {
/*  64*/        value = jp.getText();
/*  65*/      } else if (t == JsonToken.VALUE_NULL) {
/*  66*/        value = this._elementDeserializer.getNullValue();
/*   0*/      } else {
/*  68*/        value = _parseString(jp, ctxt);
/*   0*/      } 
/*  70*/      if (ix >= chunk.length) {
/*  71*/        chunk = buffer.appendCompletedChunk(chunk);
/*  72*/        ix = 0;
/*   0*/      } 
/*  74*/      chunk[ix++] = value;
/*   0*/    } 
/*  76*/    String[] result = buffer.<String>completeAndClearBuffer(chunk, ix, String.class);
/*  77*/    ctxt.returnObjectBuffer(buffer);
/*  78*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected final String[] _deserializeCustom(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*  86*/    ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/*  87*/    Object[] chunk = buffer.resetAndStart();
/*  88*/    JsonDeserializer<String> deser = this._elementDeserializer;
/*  90*/    int ix = 0;
/*   0*/    JsonToken t;
/*  93*/    while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
/*  95*/      String value = (t == JsonToken.VALUE_NULL) ? null : deser.deserialize(jp, ctxt);
/*  96*/      if (ix >= chunk.length) {
/*  97*/        chunk = buffer.appendCompletedChunk(chunk);
/*  98*/        ix = 0;
/*   0*/      } 
/* 100*/      chunk[ix++] = value;
/*   0*/    } 
/* 102*/    String[] result = buffer.<String>completeAndClearBuffer(chunk, ix, String.class);
/* 103*/    ctxt.returnObjectBuffer(buffer);
/* 104*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 109*/    return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  private final String[] handleNonArray(JsonParser jp, DeserializationContext ctxt) throws IOException {
/* 115*/    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
/* 117*/      if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
/* 119*/        String str = jp.getText();
/* 120*/        if (str.length() == 0) {
/* 121*/            return null; 
/*   0*/           }
/*   0*/      } 
/* 124*/      throw ctxt.mappingException(this._valueClass);
/*   0*/    } 
/* 126*/    return new String[] { (jp.getCurrentToken() == JsonToken.VALUE_NULL) ? null : _parseString(jp, ctxt) };
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 136*/    JsonDeserializer<?> deser = this._elementDeserializer;
/* 138*/    deser = findConvertingContentDeserializer(ctxt, property, deser);
/* 139*/    if (deser == null) {
/* 140*/      deser = ctxt.findContextualValueDeserializer(ctxt.constructType(String.class), property);
/*   0*/    } else {
/* 142*/      deser = ctxt.handleSecondaryContextualization(deser, property);
/*   0*/    } 
/* 145*/    if (this._elementDeserializer != null && isDefaultDeserializer(deser)) {
/* 146*/        deser = null; 
/*   0*/       }
/* 148*/    if (this._elementDeserializer != deser) {
/* 149*/        return new StringArrayDeserializer(deser); 
/*   0*/       }
/* 151*/    return this;
/*   0*/  }
/*   0*/}
