/*   0*/package org.mockito.internal.util.reflection;
/*   0*/
/*   0*/import java.lang.reflect.Constructor;
/*   0*/
/*   0*/public abstract class Constructors {
/*   0*/  public static Constructor<?> noArgConstructorOf(Class<?> classToMock) {
/*   0*/    try {
/*  15*/      return classToMock.getDeclaredConstructor(new Class<?>[0]);
/*  16*/    } catch (ReflectiveOperationException e) {
/*  17*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/}
