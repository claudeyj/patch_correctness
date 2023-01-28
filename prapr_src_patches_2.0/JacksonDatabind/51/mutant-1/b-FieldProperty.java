/*   0*/package com.fasterxml.jackson.databind.deser.impl;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedField;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.util.Annotations;
/*   0*/import java.io.IOException;
/*   0*/import java.lang.reflect.Field;
/*   0*/
/*   0*/public final class FieldProperty extends SettableBeanProperty {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final AnnotatedField _annotated;
/*   0*/  
/*   0*/  protected final transient Field _field;
/*   0*/  
/*   0*/  public FieldProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedField field) {
/*  37*/    super(propDef, type, typeDeser, contextAnnotations);
/*  38*/    this._annotated = field;
/*  39*/    this._field = field.getAnnotated();
/*   0*/  }
/*   0*/  
/*   0*/  protected FieldProperty(FieldProperty src, JsonDeserializer<?> deser) {
/*  43*/    super(src, deser);
/*  44*/    this._annotated = src._annotated;
/*  45*/    this._field = src._field;
/*   0*/  }
/*   0*/  
/*   0*/  protected FieldProperty(FieldProperty src, PropertyName newName) {
/*  49*/    super(src, newName);
/*  50*/    this._annotated = src._annotated;
/*  51*/    this._field = src._field;
/*   0*/  }
/*   0*/  
/*   0*/  protected FieldProperty(FieldProperty src) {
/*  59*/    super(src);
/*  60*/    this._annotated = src._annotated;
/*  61*/    Field f = this._annotated.getAnnotated();
/*  62*/    if (f == null) {
/*  63*/        throw new IllegalArgumentException("Missing field (broken JDK (de)serialization?)"); 
/*   0*/       }
/*  65*/    this._field = f;
/*   0*/  }
/*   0*/  
/*   0*/  public FieldProperty withName(PropertyName newName) {
/*  70*/    return new FieldProperty(this, newName);
/*   0*/  }
/*   0*/  
/*   0*/  public FieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
/*  75*/    return new FieldProperty(this, deser);
/*   0*/  }
/*   0*/  
/*   0*/  public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> acls) {
/*  86*/    return (this._annotated == null) ? null : this._annotated.<A>getAnnotation(acls);
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMember getMember() {
/*  89*/    return this._annotated;
/*   0*/  }
/*   0*/  
/*   0*/  public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
/* 101*/    Object value = deserialize(p, ctxt);
/*   0*/    try {
/* 103*/      this._field.set(instance, value);
/* 104*/    } catch (Exception e) {
/* 105*/      this;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
/* 113*/    Object value = deserialize(p, ctxt);
/*   0*/    try {
/* 115*/      this._field.set(instance, value);
/* 116*/    } catch (Exception e) {
/* 117*/      _throwAsIOE(p, e, value);
/*   0*/    } 
/* 119*/    return instance;
/*   0*/  }
/*   0*/  
/*   0*/  public final void set(Object instance, Object value) throws IOException {
/*   0*/    try {
/* 126*/      this._field.set(instance, value);
/* 127*/    } catch (Exception e) {
/* 129*/      _throwAsIOE(e, value);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object setAndReturn(Object instance, Object value) throws IOException {
/*   0*/    try {
/* 137*/      this._field.set(instance, value);
/* 138*/    } catch (Exception e) {
/* 140*/      _throwAsIOE(e, value);
/*   0*/    } 
/* 142*/    return instance;
/*   0*/  }
/*   0*/  
/*   0*/  Object readResolve() {
/* 152*/    return new FieldProperty(this);
/*   0*/  }
/*   0*/}
