/*   0*/package org.mockito;
/*   0*/
/*   0*/import java.lang.annotation.Annotation;
/*   0*/import java.lang.annotation.ElementType;
/*   0*/import java.lang.annotation.Retention;
/*   0*/import java.lang.annotation.RetentionPolicy;
/*   0*/import java.lang.annotation.Target;
/*   0*/import java.lang.reflect.Field;
/*   0*/import org.mockito.configuration.AnnotationEngine;
/*   0*/import org.mockito.configuration.DefaultMockitoConfiguration;
/*   0*/import org.mockito.exceptions.Reporter;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.configuration.GlobalConfiguration;
/*   0*/import org.mockito.internal.util.reflection.FieldSetter;
/*   0*/
/*   0*/public class MockitoAnnotations {
/*   0*/  public static void initMocks(Object testClass) {
/*  90*/    if (testClass == null)
/*  91*/      throw new MockitoException("testClass cannot be null. For info how to use @Mock annotations see examples in javadoc for MockitoAnnotations class"); 
/*  94*/    AnnotationEngine annotationEngine = new GlobalConfiguration().getAnnotationEngine();
/*  95*/    Class<?> clazz = testClass.getClass();
/*  98*/    if (annotationEngine.getClass() != new DefaultMockitoConfiguration().getAnnotationEngine().getClass())
/* 101*/      while (clazz != Object.class) {
/* 102*/        scanDeprecatedWay(annotationEngine, testClass, clazz);
/* 103*/        clazz = clazz.getSuperclass();
/*   0*/      }  
/* 108*/    annotationEngine.process(testClass.getClass(), testClass);
/*   0*/  }
/*   0*/  
/*   0*/  static void scanDeprecatedWay(AnnotationEngine annotationEngine, Object testClass, Class<?> clazz) {
/* 112*/    Field[] fields = clazz.getDeclaredFields();
/* 114*/    for (Field field : fields)
/* 115*/      processAnnotationDeprecatedWay(annotationEngine, testClass, field); 
/*   0*/  }
/*   0*/  
/*   0*/  static void processAnnotationDeprecatedWay(AnnotationEngine annotationEngine, Object testClass, Field field) {
/*   0*/    boolean alreadyAssigned = false;
/* 122*/    for (Annotation annotation : field.getAnnotations()) {
/* 123*/      Object mock = annotationEngine.createMockFor(annotation, field);
/* 124*/      if (mock != null) {
/* 125*/        throwIfAlreadyAssigned(field, alreadyAssigned);
/* 126*/        alreadyAssigned = true;
/*   0*/        try {
/* 128*/          new FieldSetter(testClass, field).set(mock);
/* 129*/        } catch (Throwable e) {
/* 130*/          throw new MockitoException("Problems setting field " + field.getName() + " annotated with " + annotation, e);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static void throwIfAlreadyAssigned(Field field, boolean alreadyAssigned) {
/* 138*/    if (alreadyAssigned)
/* 139*/      new Reporter().moreThanOneAnnotationNotAllowed(field.getName()); 
/*   0*/  }
/*   0*/  
/*   0*/  @Target({ElementType.FIELD})
/*   0*/  @Retention(RetentionPolicy.RUNTIME)
/*   0*/  @Deprecated
/*   0*/  public static @interface Mock {}
/*   0*/}
