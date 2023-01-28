/*   0*/package com.google.gson;
/*   0*/
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.sql.Timestamp;
/*   0*/import java.text.DateFormat;
/*   0*/import java.text.SimpleDateFormat;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/
/*   0*/final class DefaultDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
/*   0*/  private final DateFormat enUsFormat;
/*   0*/  
/*   0*/  private final DateFormat localFormat;
/*   0*/  
/*   0*/  DefaultDateTypeAdapter() {
/*  46*/    this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
/*   0*/  }
/*   0*/  
/*   0*/  DefaultDateTypeAdapter(String datePattern) {
/*  51*/    this(new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
/*   0*/  }
/*   0*/  
/*   0*/  DefaultDateTypeAdapter(int style) {
/*  55*/    this(DateFormat.getDateInstance(style, Locale.US), DateFormat.getDateInstance(style));
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
/*  59*/    this(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US), DateFormat.getDateTimeInstance(dateStyle, timeStyle));
/*   0*/  }
/*   0*/  
/*   0*/  DefaultDateTypeAdapter(DateFormat enUsFormat, DateFormat localFormat) {
/*  64*/    this.enUsFormat = enUsFormat;
/*  65*/    this.localFormat = localFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
/*  72*/    synchronized (this.localFormat) {
/*  73*/      String dateFormatAsString = this.enUsFormat.format(src);
/*  74*/      return new JsonPrimitive(dateFormatAsString);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
/*  81*/    if (!(json instanceof JsonPrimitive)) {
/*  82*/        throw new JsonParseException("The date should be a string value"); 
/*   0*/       }
/*  84*/    Date date = deserializeToDate(json);
/*  85*/    if (typeOfT == Date.class) {
/*  86*/        return date; 
/*   0*/       }
/*  87*/    if (typeOfT == Timestamp.class) {
/*  88*/        return new Timestamp(date.getTime()); 
/*   0*/       }
/*  89*/    if (typeOfT == java.sql.Date.class) {
/*  90*/        return new java.sql.Date(date.getTime()); 
/*   0*/       }
/*  92*/    throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
/*   0*/  }
/*   0*/  
/*   0*/  private Date deserializeToDate(JsonElement json) {
/*  97*/    synchronized (this.localFormat) {
/*  99*/      return this.localFormat.parse(json.getAsString());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 114*/    StringBuilder sb = new StringBuilder();
/* 115*/    sb.append(DefaultDateTypeAdapter.class.getSimpleName());
/* 116*/    sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
/* 117*/    return sb.toString();
/*   0*/  }
/*   0*/}
