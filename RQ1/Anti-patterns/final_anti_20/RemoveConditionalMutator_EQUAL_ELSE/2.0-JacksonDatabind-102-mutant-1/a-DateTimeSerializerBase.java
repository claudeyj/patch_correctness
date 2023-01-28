/*   0*/package com.fasterxml.jackson.databind.ser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonFormat;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.JsonSerializer;
/*   0*/import com.fasterxml.jackson.databind.SerializationFeature;
/*   0*/import com.fasterxml.jackson.databind.SerializerProvider;
/*   0*/import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*   0*/import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*   0*/import com.fasterxml.jackson.databind.ser.ContextualSerializer;
/*   0*/import com.fasterxml.jackson.databind.util.StdDateFormat;
/*   0*/import java.io.IOException;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.text.DateFormat;
/*   0*/import java.text.SimpleDateFormat;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public abstract class DateTimeSerializerBase<T> extends StdScalarSerializer<T> implements ContextualSerializer {
/*   0*/  protected final Boolean _useTimestamp;
/*   0*/  
/*   0*/  protected final DateFormat _customFormat;
/*   0*/  
/*   0*/  protected final AtomicReference<DateFormat> _reusedCustomFormat;
/*   0*/  
/*   0*/  protected DateTimeSerializerBase(Class<T> type, Boolean useTimestamp, DateFormat customFormat) {
/*  53*/    super(type);
/*  54*/    this._useTimestamp = useTimestamp;
/*  55*/    this._customFormat = customFormat;
/*  56*/    this._reusedCustomFormat = (customFormat == null) ? null : new AtomicReference<>();
/*   0*/  }
/*   0*/  
/*   0*/  public abstract DateTimeSerializerBase<T> withFormat(Boolean paramBoolean, DateFormat paramDateFormat);
/*   0*/  
/*   0*/  public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
/*  67*/    if (property == null) {
/*  68*/        return this; 
/*   0*/       }
/*  70*/    JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/*  71*/    if (format == null) {
/*  72*/        return this; 
/*   0*/       }
/*  75*/    JsonFormat.Shape shape = format.getShape();
/*  76*/    if (shape.isNumeric()) {
/*  77*/        return withFormat(Boolean.TRUE, null); 
/*   0*/       }
/*  82*/    if (format.hasPattern()) {
/*  83*/      Locale loc = format.hasLocale() ? format.getLocale() : serializers.getLocale();
/*  86*/      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format.getPattern(), loc);
/*  87*/      TimeZone tz = format.hasTimeZone() ? format.getTimeZone() : serializers.getTimeZone();
/*  89*/      simpleDateFormat.setTimeZone(tz);
/*  90*/      return withFormat(Boolean.FALSE, simpleDateFormat);
/*   0*/    } 
/*  94*/    boolean hasLocale = format.hasLocale();
/*  95*/    boolean hasTZ = format.hasTimeZone();
/*  96*/    boolean asString = (shape == JsonFormat.Shape.STRING);
/*  98*/    if (!hasLocale && !hasTZ && !asString) {
/*  99*/        return this; 
/*   0*/       }
/* 102*/    DateFormat df0 = serializers.getConfig().getDateFormat();
/* 104*/    if (df0 instanceof StdDateFormat) {
/* 105*/      StdDateFormat std = (StdDateFormat)df0;
/* 106*/      if (format.hasLocale()) {
/* 107*/          std = std.withLocale(format.getLocale()); 
/*   0*/         }
/* 109*/      if (format.hasTimeZone()) {
/* 110*/          std = std.withTimeZone(format.getTimeZone()); 
/*   0*/         }
/* 112*/      return withFormat(Boolean.FALSE, std);
/*   0*/    } 
/* 118*/    if (!(df0 instanceof SimpleDateFormat)) {
/* 119*/        serializers.reportBadDefinition(handledType(), String.format("Configured `DateFormat` (%s) not a `SimpleDateFormat`; cannot configure `Locale` or `TimeZone`", new Object[] { df0.getClass().getName() })); 
/*   0*/       }
/* 123*/    SimpleDateFormat df = (SimpleDateFormat)df0;
/* 124*/    if (hasLocale) {
/* 126*/      df = new SimpleDateFormat(df.toPattern(), format.getLocale());
/*   0*/    } else {
/* 128*/      df = (SimpleDateFormat)df.clone();
/*   0*/    } 
/* 130*/    TimeZone newTz = format.getTimeZone();
/* 131*/    boolean changeTZ = (newTz != null && !newTz.equals(df.getTimeZone()));
/* 132*/    if (changeTZ) {
/* 133*/        df.setTimeZone(newTz); 
/*   0*/       }
/* 135*/    return withFormat(Boolean.FALSE, df);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty(SerializerProvider serializers, T value) {
/* 149*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract long _timestamp(T paramT);
/*   0*/  
/*   0*/  public JsonNode getSchema(SerializerProvider serializers, Type typeHint) {
/* 157*/    return createSchemaNode(_asTimestamp(serializers) ? "number" : "string", true);
/*   0*/  }
/*   0*/  
/*   0*/  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
/* 163*/    _acceptJsonFormatVisitor(visitor, typeHint, _asTimestamp(visitor.getProvider()));
/*   0*/  }
/*   0*/  
/*   0*/  public abstract void serialize(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws IOException;
/*   0*/  
/*   0*/  protected boolean _asTimestamp(SerializerProvider serializers) {
/* 184*/    if (this._useTimestamp != null) {
/* 185*/        return this._useTimestamp; 
/*   0*/       }
/* 187*/    if (this._customFormat == null) {
/* 188*/      if (serializers != null) {
/* 189*/          return serializers.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
/*   0*/         }
/* 192*/      throw new IllegalArgumentException("Null SerializerProvider passed for " + handledType().getName());
/*   0*/    } 
/* 194*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint, boolean asNumber) throws JsonMappingException {
/* 200*/    if (asNumber) {
/* 201*/      visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG, JsonValueFormat.UTC_MILLISEC);
/*   0*/    } else {
/* 204*/      visitStringFormat(visitor, typeHint, JsonValueFormat.DATE_TIME);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _serializeAsString(Date value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 213*/    if (this._customFormat == null) {
/* 214*/      provider.defaultSerializeDateValue(value, g);
/*   0*/      return;
/*   0*/    } 
/* 225*/    DateFormat f = this._reusedCustomFormat.getAndSet(null);
/* 226*/    if (f == null) {
/* 227*/        f = (DateFormat)this._customFormat.clone(); 
/*   0*/       }
/* 229*/    g.writeString(f.format(value));
/* 230*/    this._reusedCustomFormat.compareAndSet(null, f);
/*   0*/  }
/*   0*/}
