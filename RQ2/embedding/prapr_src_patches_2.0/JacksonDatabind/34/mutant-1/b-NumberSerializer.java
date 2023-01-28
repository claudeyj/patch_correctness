/*   0*/package com.fasterxml.jackson.databind.ser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.SerializerProvider;
/*   0*/import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*   0*/import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*   0*/import java.io.IOException;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/
/*   0*/@JacksonStdImpl
/*   0*/public class NumberSerializer extends StdScalarSerializer<Number> {
/*  27*/  public static final NumberSerializer instance = new NumberSerializer(Number.class);
/*   0*/  
/*   0*/  protected final boolean _isInt;
/*   0*/  
/*   0*/  public NumberSerializer(Class<? extends Number> rawType) {
/*  35*/    super(rawType, false);
/*  37*/    this._isInt = (rawType == BigInteger.class);
/*   0*/  }
/*   0*/  
/*   0*/  public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
/*  44*/    if (value instanceof BigDecimal) {
/*  45*/      g.writeNumber((BigDecimal)value);
/*  46*/    } else if (value instanceof BigInteger) {
/*  47*/      g.writeNumber((BigInteger)value);
/*  52*/    } else if (value instanceof Integer) {
/*  53*/      g.writeNumber(value.intValue());
/*  54*/    } else if (value instanceof Long) {
/*  55*/      g.writeNumber(value.longValue());
/*  56*/    } else if (value instanceof Double) {
/*  57*/      g.writeNumber(value.doubleValue());
/*  58*/    } else if (value instanceof Float) {
/*  59*/      g.writeNumber(value.floatValue());
/*  60*/    } else if (value instanceof Byte || value instanceof Short) {
/*  61*/      g.writeNumber(value.intValue());
/*   0*/    } else {
/*  64*/      g.writeNumber(value.toString());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
/*  70*/    return createSchemaNode(this._isInt ? "integer" : "number", true);
/*   0*/  }
/*   0*/  
/*   0*/  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/*  76*/    if (this._isInt) {
/*  77*/      visitIntFormat(visitor, typeHint, JsonParser.NumberType.BIG_INTEGER);
/*   0*/    } else {
/*  79*/      Class<?> h = handledType();
/*  80*/      if (h == BigDecimal.class) {
/*  81*/        visitFloatFormat(visitor, typeHint, JsonParser.NumberType.BIG_DECIMAL);
/*   0*/      } else {
/*  84*/        visitor.expectNumberFormat(typeHint);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/}
