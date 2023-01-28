/*   0*/package com.fasterxml.jackson.databind.cfg;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*   0*/import com.fasterxml.jackson.annotation.PropertyAccessor;
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
/*   0*/import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*   0*/import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.StdDateFormat;
/*   0*/import java.io.Serializable;
/*   0*/import java.text.DateFormat;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/
/*   0*/public final class BaseSettings implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final ClassIntrospector _classIntrospector;
/*   0*/  
/*   0*/  protected final AnnotationIntrospector _annotationIntrospector;
/*   0*/  
/*   0*/  protected final VisibilityChecker<?> _visibilityChecker;
/*   0*/  
/*   0*/  protected final PropertyNamingStrategy _propertyNamingStrategy;
/*   0*/  
/*   0*/  protected final TypeFactory _typeFactory;
/*   0*/  
/*   0*/  protected final TypeResolverBuilder<?> _typeResolverBuilder;
/*   0*/  
/*   0*/  protected final DateFormat _dateFormat;
/*   0*/  
/*   0*/  protected final HandlerInstantiator _handlerInstantiator;
/*   0*/  
/*   0*/  protected final Locale _locale;
/*   0*/  
/*   0*/  protected final TimeZone _timeZone;
/*   0*/  
/*   0*/  protected final Base64Variant _defaultBase64;
/*   0*/  
/*   0*/  public BaseSettings(ClassIntrospector ci, AnnotationIntrospector ai, VisibilityChecker<?> vc, PropertyNamingStrategy pns, TypeFactory tf, TypeResolverBuilder<?> typer, DateFormat dateFormat, HandlerInstantiator hi, Locale locale, TimeZone tz, Base64Variant defaultBase64) {
/* 141*/    this._classIntrospector = ci;
/* 142*/    this._annotationIntrospector = ai;
/* 143*/    this._visibilityChecker = vc;
/* 144*/    this._propertyNamingStrategy = pns;
/* 145*/    this._typeFactory = tf;
/* 146*/    this._typeResolverBuilder = typer;
/* 147*/    this._dateFormat = dateFormat;
/* 148*/    this._handlerInstantiator = hi;
/* 149*/    this._locale = locale;
/* 150*/    this._timeZone = tz;
/* 151*/    this._defaultBase64 = defaultBase64;
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withClassIntrospector(ClassIntrospector ci) {
/* 161*/    if (this._classIntrospector == ci) {
/* 162*/        return this; 
/*   0*/       }
/* 164*/    return new BaseSettings(ci, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withAnnotationIntrospector(AnnotationIntrospector ai) {
/* 170*/    if (this._annotationIntrospector == ai) {
/* 171*/        return this; 
/*   0*/       }
/* 173*/    return new BaseSettings(this._classIntrospector, ai, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 179*/    return withAnnotationIntrospector(AnnotationIntrospectorPair.create(ai, this._annotationIntrospector));
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 183*/    return withAnnotationIntrospector(AnnotationIntrospectorPair.create(this._annotationIntrospector, ai));
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withVisibilityChecker(VisibilityChecker<?> vc) {
/* 187*/    if (this._visibilityChecker == vc) {
/* 188*/        return this; 
/*   0*/       }
/* 190*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, vc, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
/* 196*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, (VisibilityChecker<?>)this._visibilityChecker.withVisibility(forMethod, visibility), this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withPropertyNamingStrategy(PropertyNamingStrategy pns) {
/* 204*/    if (this._propertyNamingStrategy == pns) {
/* 205*/        return this; 
/*   0*/       }
/* 207*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, pns, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withTypeFactory(TypeFactory tf) {
/* 213*/    if (this._typeFactory == tf) {
/* 214*/        return this; 
/*   0*/       }
/* 216*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, tf, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withTypeResolverBuilder(TypeResolverBuilder<?> typer) {
/* 222*/    if (this._typeResolverBuilder == typer) {
/* 223*/        return this; 
/*   0*/       }
/* 225*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, typer, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withDateFormat(DateFormat df) {
/* 231*/    if (this._dateFormat == df) {
/* 232*/        return this; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings withHandlerInstantiator(HandlerInstantiator hi) {
/* 241*/    if (this._handlerInstantiator == hi) {
/* 242*/        return this; 
/*   0*/       }
/* 244*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, hi, this._locale, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings with(Locale l) {
/* 250*/    if (this._locale == l) {
/* 251*/        return this; 
/*   0*/       }
/* 253*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, l, this._timeZone, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings with(TimeZone tz) {
/* 265*/    if (tz == null) {
/* 266*/        throw new IllegalArgumentException(); 
/*   0*/       }
/* 268*/    DateFormat df = this._dateFormat;
/* 269*/    if (df instanceof StdDateFormat) {
/* 270*/      df = ((StdDateFormat)df).withTimeZone(tz);
/*   0*/    } else {
/* 273*/      df = (DateFormat)df.clone();
/* 274*/      df.setTimeZone(tz);
/*   0*/    } 
/* 276*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, tz, this._defaultBase64);
/*   0*/  }
/*   0*/  
/*   0*/  public BaseSettings with(Base64Variant base64) {
/* 286*/    if (base64 == this._defaultBase64) {
/* 287*/        return this; 
/*   0*/       }
/* 289*/    return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, base64);
/*   0*/  }
/*   0*/  
/*   0*/  public ClassIntrospector getClassIntrospector() {
/* 302*/    return this._classIntrospector;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotationIntrospector getAnnotationIntrospector() {
/* 306*/    return this._annotationIntrospector;
/*   0*/  }
/*   0*/  
/*   0*/  public VisibilityChecker<?> getVisibilityChecker() {
/* 310*/    return this._visibilityChecker;
/*   0*/  }
/*   0*/  
/*   0*/  public PropertyNamingStrategy getPropertyNamingStrategy() {
/* 314*/    return this._propertyNamingStrategy;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory getTypeFactory() {
/* 318*/    return this._typeFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeResolverBuilder<?> getTypeResolverBuilder() {
/* 322*/    return this._typeResolverBuilder;
/*   0*/  }
/*   0*/  
/*   0*/  public DateFormat getDateFormat() {
/* 326*/    return this._dateFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public HandlerInstantiator getHandlerInstantiator() {
/* 330*/    return this._handlerInstantiator;
/*   0*/  }
/*   0*/  
/*   0*/  public Locale getLocale() {
/* 334*/    return this._locale;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeZone getTimeZone() {
/* 338*/    return this._timeZone;
/*   0*/  }
/*   0*/  
/*   0*/  public Base64Variant getBase64Variant() {
/* 342*/    return this._defaultBase64;
/*   0*/  }
/*   0*/}
