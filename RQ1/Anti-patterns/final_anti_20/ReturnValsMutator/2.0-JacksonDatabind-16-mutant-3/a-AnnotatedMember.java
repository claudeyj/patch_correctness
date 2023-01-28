/*   0*/package com.fasterxml.jackson.databind.introspect;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.annotation.Annotation;
/*   0*/import java.lang.reflect.Member;
/*   0*/import java.util.Collections;
/*   0*/
/*   0*/public abstract class AnnotatedMember extends Annotated implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final transient AnnotatedClass _context;
/*   0*/  
/*   0*/  protected final transient AnnotationMap _annotations;
/*   0*/  
/*   0*/  protected AnnotatedMember(AnnotatedClass ctxt, AnnotationMap annotations) {
/*  44*/    this._context = ctxt;
/*  45*/    this._annotations = annotations;
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedMember(AnnotatedMember base) {
/*  54*/    this._context = base._context;
/*  55*/    this._annotations = base._annotations;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract Class<?> getDeclaringClass();
/*   0*/  
/*   0*/  public abstract Member getMember();
/*   0*/  
/*   0*/  public AnnotatedClass getContextClass() {
/*  83*/    return this._context;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Annotation> annotations() {
/*  88*/    if (this._annotations == null) {
/*  89*/        return Collections.emptyList(); 
/*   0*/       }
/*  91*/    return this._annotations.annotations();
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap getAllAnnotations() {
/*  96*/    return this._annotations;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean addOrOverride(Annotation a) {
/* 105*/    return this._annotations.add(a);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean addIfNotPresent(Annotation a) {
/* 114*/    return this._annotations.addIfNotPresent(a);
/*   0*/  }
/*   0*/  
/*   0*/  public final void fixAccess() {
/* 123*/    ClassUtil.checkAndFixAccess(getMember());
/*   0*/  }
/*   0*/  
/*   0*/  public abstract void setValue(Object paramObject1, Object paramObject2) throws UnsupportedOperationException, IllegalArgumentException;
/*   0*/  
/*   0*/  public abstract Object getValue(Object paramObject) throws UnsupportedOperationException, IllegalArgumentException;
/*   0*/}
