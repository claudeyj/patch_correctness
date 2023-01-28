/*   0*/package com.fasterxml.jackson.databind.introspect;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.util.Annotations;
/*   0*/import java.lang.annotation.Annotation;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/
/*   0*/public final class AnnotationMap implements Annotations {
/*   0*/  protected HashMap<Class<? extends Annotation>, Annotation> _annotations;
/*   0*/  
/*   0*/  public AnnotationMap() {}
/*   0*/  
/*   0*/  private AnnotationMap(HashMap<Class<? extends Annotation>, Annotation> a) {
/*  21*/    this._annotations = a;
/*   0*/  }
/*   0*/  
/*   0*/  public <A extends Annotation> A get(Class<A> cls) {
/*  28*/    if (this._annotations == null) {
/*  29*/        return null; 
/*   0*/       }
/*  31*/    return (A)this._annotations.get(cls);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Annotation> annotations() {
/*  38*/    if (this._annotations == null || this._annotations.size() == 0) {
/*  39*/        return Collections.emptyList(); 
/*   0*/       }
/*  41*/    return this._annotations.values();
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotationMap merge(AnnotationMap primary, AnnotationMap secondary) {
/*  46*/    if (primary == null || primary._annotations == null || primary._annotations.isEmpty()) {
/*  47*/        return secondary; 
/*   0*/       }
/*  49*/    if (secondary == null || secondary._annotations == null || secondary._annotations.isEmpty()) {
/*  50*/        return primary; 
/*   0*/       }
/*  52*/    HashMap<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();
/*  55*/    for (Annotation ann : secondary._annotations.values()) {
/*  56*/        annotations.put(ann.annotationType(), ann); 
/*   0*/       }
/*  59*/    for (Annotation ann : primary._annotations.values()) {
/*  60*/        annotations.put(ann.annotationType(), ann); 
/*   0*/       }
/*  62*/    return new AnnotationMap(annotations);
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/*  67*/    return (this._annotations == null) ? 0 : this._annotations.size();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean addIfNotPresent(Annotation ann) {
/*  76*/    if (this._annotations == null || !this._annotations.containsKey(ann.annotationType())) {
/*  77*/      _add(ann);
/*  78*/      return true;
/*   0*/    } 
/*  80*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean add(Annotation ann) {
/*  90*/    return _add(ann);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*  95*/    if (this._annotations == null) {
/*  96*/        return "[null]"; 
/*   0*/       }
/*  98*/    return this._annotations.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean _add(Annotation ann) {
/* 108*/    if (this._annotations == null) {
/* 109*/        this._annotations = new HashMap<Class<? extends Annotation>, Annotation>(); 
/*   0*/       }
/* 111*/    Annotation previous = this._annotations.put(ann.annotationType(), ann);
/* 112*/    return !((previous != null && previous.equals(ann)));
/*   0*/  }
/*   0*/}
