/*   0*/package org.mockito.internal.util.reflection;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/
/*   0*/public class Whitebox {
/*   0*/  public static Object getInternalState(Object target, String field) {
/*  12*/    Class<?> c = target.getClass();
/*   0*/    try {
/*  14*/      Field f = getFieldFromHierarchy(c, field);
/*  15*/      f.setAccessible(true);
/*  16*/      return f.get(target);
/*  17*/    } catch (Throwable e) {
/*  18*/      throw new RuntimeException("Unable to get internal state on a private field. Please report to mockito mailing list.", e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static void setInternalState(Object target, String field, Object value) {
/*  23*/    Class<?> c = target.getClass();
/*   0*/    try {
/*  25*/      Field f = getFieldFromHierarchy(c, field);
/*  26*/      f.setAccessible(true);
/*  27*/      f.set(target, value);
/*  28*/    } catch (Throwable e) {
/*  29*/      throw new RuntimeException("Unable to set internal state on a private field. Please report to mockito mailing list.", e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static Field getFieldFromHierarchy(Class<?> clazz, String field) {
/*  34*/    Field f = getField(clazz, field);
/*  35*/    while (f == null && clazz != Object.class) {
/*  36*/      clazz = clazz.getSuperclass();
/*  37*/      f = getField(clazz, field);
/*   0*/    } 
/*  39*/    if (f == null)
/*  40*/      throw new RuntimeException("You want me to get this field: '" + field + "' on this class: '" + clazz.getSimpleName() + "' but this field is not declared withing hierarchy of this class!"); 
/*  45*/    return f;
/*   0*/  }
/*   0*/  
/*   0*/  private static Field getField(Class<?> clazz, String field) {
/*   0*/    try {
/*  50*/      return clazz.getDeclaredField(field);
/*  51*/    } catch (ReflectiveOperationException e) {
/*  52*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/}
