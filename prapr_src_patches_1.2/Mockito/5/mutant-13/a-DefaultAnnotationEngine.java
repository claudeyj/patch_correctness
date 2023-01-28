/*   0*/package org.mockito.internal.configuration;
/*   0*/
/*   0*/import java.lang.annotation.Annotation;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Map;
/*   0*/import org.mockito.Captor;
/*   0*/import org.mockito.Mock;
/*   0*/import org.mockito.MockitoAnnotations;
/*   0*/import org.mockito.configuration.AnnotationEngine;
/*   0*/import org.mockito.exceptions.Reporter;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.util.reflection.FieldSetter;
/*   0*/
/*   0*/public class DefaultAnnotationEngine implements AnnotationEngine {
/*  30*/  private final Map<Class<? extends Annotation>, FieldAnnotationProcessor<?>> annotationProcessorMap = new HashMap<Class<? extends Annotation>, FieldAnnotationProcessor<?>>();
/*   0*/  
/*   0*/  public DefaultAnnotationEngine() {
/*  33*/    registerAnnotationProcessor(Mock.class, new MockAnnotationProcessor());
/*  34*/    registerAnnotationProcessor(MockitoAnnotations.Mock.class, new MockitoAnnotationsMockAnnotationProcessor());
/*  35*/    registerAnnotationProcessor(Captor.class, new CaptorAnnotationProcessor());
/*   0*/  }
/*   0*/  
/*   0*/  public Object createMockFor(Annotation annotation, Field field) {
/*  43*/    return forAnnotation(annotation).process(annotation, field);
/*   0*/  }
/*   0*/  
/*   0*/  private <A extends Annotation> FieldAnnotationProcessor<A> forAnnotation(A annotation) {
/*  47*/    if (this.annotationProcessorMap.containsKey(annotation.annotationType()))
/*  48*/      return (FieldAnnotationProcessor<A>)this.annotationProcessorMap.get(annotation.annotationType()); 
/*  50*/    return new FieldAnnotationProcessor<A>() {
/*   0*/        public Object process(A annotation, Field field) {
/*  52*/          return null;
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  private <A extends Annotation> void registerAnnotationProcessor(Class<A> annotationClass, FieldAnnotationProcessor<A> fieldAnnotationProcessor) {
/*  58*/    this.annotationProcessorMap.put(annotationClass, fieldAnnotationProcessor);
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Class<?> clazz, Object testInstance) {
/*  62*/    Field[] fields = clazz.getDeclaredFields();
/*  63*/    for (Field field : fields) {
/*   0*/      boolean alreadyAssigned = false;
/*  65*/      for (Annotation annotation : field.getAnnotations()) {
/*  66*/        Object mock = createMockFor(annotation, field);
/*  67*/        if (mock != null) {
/*  68*/          throwIfAlreadyAssigned(field, alreadyAssigned);
/*  69*/          alreadyAssigned = true;
/*   0*/          try {
/*  71*/            new FieldSetter(testInstance, field).set(mock);
/*  72*/          } catch (Exception e) {
/*  73*/            throw new MockitoException("Problems setting field " + field.getName() + " annotated with " + annotation, e);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void throwIfAlreadyAssigned(Field field, boolean alreadyAssigned) {
/*  82*/    if (alreadyAssigned)
/*  83*/      new Reporter().moreThanOneAnnotationNotAllowed(field.getName()); 
/*   0*/  }
/*   0*/}
