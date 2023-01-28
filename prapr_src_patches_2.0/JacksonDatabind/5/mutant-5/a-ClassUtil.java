/*   0*/package com.fasterxml.jackson.databind.util;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
/*   0*/import com.fasterxml.jackson.databind.annotation.NoClass;
/*   0*/import java.lang.reflect.AccessibleObject;
/*   0*/import java.lang.reflect.Constructor;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.Member;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.EnumMap;
/*   0*/import java.util.EnumSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public final class ClassUtil {
/*   0*/  public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
/*  29*/    return findSuperTypes(cls, endBefore, new ArrayList<Class<?>>(8));
/*   0*/  }
/*   0*/  
/*   0*/  public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
/*  33*/    _addSuperTypes(cls, endBefore, result, false);
/*  34*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private static void _addSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
/*  38*/    if (cls == endBefore || cls == null || cls == Object.class) {
/*   0*/        return; 
/*   0*/       }
/*  39*/    if (addClassItself) {
/*  40*/      if (result.contains(cls)) {
/*   0*/          return; 
/*   0*/         }
/*  43*/      result.add(cls);
/*   0*/    } 
/*  45*/    for (Class<?> intCls : cls.getInterfaces()) {
/*  46*/        _addSuperTypes(intCls, endBefore, result, true); 
/*   0*/       }
/*  48*/    _addSuperTypes(cls.getSuperclass(), endBefore, result, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String canBeABeanType(Class<?> type) {
/*  64*/    if (type.isAnnotation()) {
/*  65*/        return "annotation"; 
/*   0*/       }
/*  67*/    if (type.isArray()) {
/*  68*/        return "array"; 
/*   0*/       }
/*  70*/    if (type.isEnum()) {
/*  71*/        return "enum"; 
/*   0*/       }
/*  73*/    if (type.isPrimitive()) {
/*  74*/        return "primitive"; 
/*   0*/       }
/*  78*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static String isLocalType(Class<?> type, boolean allowNonStatic) {
/*   0*/    try {
/*  89*/      if (type.getEnclosingMethod() != null) {
/*  90*/          return "local/anonymous"; 
/*   0*/         }
/*  97*/      if (!allowNonStatic && 
/*  98*/        type.getEnclosingClass() != null && 
/*  99*/        !Modifier.isStatic(type.getModifiers())) {
/* 100*/          return "non-static member class"; 
/*   0*/         }
/* 105*/    } catch (SecurityException securityException) {
/*   0*/    
/* 106*/    } catch (NullPointerException nullPointerException) {}
/* 107*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> getOuterClass(Class<?> type) {
/*   0*/    try {
/* 118*/      if (type.getEnclosingMethod() != null) {
/* 119*/          return null; 
/*   0*/         }
/* 121*/      if (!Modifier.isStatic(type.getModifiers())) {
/* 122*/          return type.getEnclosingClass(); 
/*   0*/         }
/* 124*/    } catch (SecurityException securityException) {
/*   0*/    
/* 125*/    } catch (NullPointerException nullPointerException) {}
/* 126*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isProxyType(Class<?> type) {
/* 144*/    String name = type.getName();
/* 146*/    if (name.startsWith("net.sf.cglib.proxy.") || name.startsWith("org.hibernate.proxy.")) {
/* 148*/        return true; 
/*   0*/       }
/* 151*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isConcrete(Class<?> type) {
/* 160*/    int mod = type.getModifiers();
/* 161*/    return ((mod & 0x600) == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isConcrete(Member member) {
/* 166*/    int mod = member.getModifiers();
/* 167*/    return ((mod & 0x600) == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isCollectionMapOrArray(Class<?> type) {
/* 172*/    if (type.isArray()) {
/* 172*/        return true; 
/*   0*/       }
/* 173*/    if (Collection.class.isAssignableFrom(type)) {
/* 173*/        return true; 
/*   0*/       }
/* 174*/    if (Map.class.isAssignableFrom(type)) {
/* 174*/        return true; 
/*   0*/       }
/* 175*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getClassDescription(Object classOrInstance) {
/* 191*/    if (classOrInstance == null) {
/* 192*/        return "unknown"; 
/*   0*/       }
/* 194*/    Class<?> cls = (classOrInstance instanceof Class) ? (Class)classOrInstance : classOrInstance.getClass();
/* 196*/    return cls.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> findClass(String className) throws ClassNotFoundException {
/* 208*/    if (className.indexOf('.') < 0) {
/* 209*/      if ("int".equals(className)) {
/* 209*/          return int.class; 
/*   0*/         }
/* 210*/      if ("long".equals(className)) {
/* 210*/          return long.class; 
/*   0*/         }
/* 211*/      if ("float".equals(className)) {
/* 211*/          return float.class; 
/*   0*/         }
/* 212*/      if ("double".equals(className)) {
/* 212*/          return double.class; 
/*   0*/         }
/* 213*/      if ("boolean".equals(className)) {
/* 213*/          return boolean.class; 
/*   0*/         }
/* 214*/      if ("byte".equals(className)) {
/* 214*/          return byte.class; 
/*   0*/         }
/* 215*/      if ("char".equals(className)) {
/* 215*/          return char.class; 
/*   0*/         }
/* 216*/      if ("short".equals(className)) {
/* 216*/          return short.class; 
/*   0*/         }
/* 217*/      if ("void".equals(className)) {
/* 217*/          return void.class; 
/*   0*/         }
/*   0*/    } 
/* 220*/    Throwable prob = null;
/* 221*/    ClassLoader loader = Thread.currentThread().getContextClassLoader();
/* 223*/    if (loader != null) {
/*   0*/        try {
/* 225*/          return Class.forName(className, true, loader);
/* 226*/        } catch (Exception e) {
/* 227*/          prob = getRootCause(e);
/*   0*/        }  
/*   0*/       }
/*   0*/    try {
/* 231*/      return Class.forName(className);
/* 232*/    } catch (Exception e) {
/* 233*/      if (prob == null) {
/* 234*/          prob = getRootCause(e); 
/*   0*/         }
/* 237*/      if (prob instanceof RuntimeException) {
/* 238*/          throw (RuntimeException)prob; 
/*   0*/         }
/* 240*/      throw new ClassNotFoundException(prob.getMessage(), prob);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean hasGetterSignature(Method m) {
/* 252*/    if (Modifier.isStatic(m.getModifiers())) {
/* 253*/        return false; 
/*   0*/       }
/* 256*/    Class<?>[] pts = m.getParameterTypes();
/* 257*/    if (pts != null && pts.length != 0) {
/* 258*/        return false; 
/*   0*/       }
/* 261*/    if (void.class == m.getReturnType()) {
/* 262*/        return false; 
/*   0*/       }
/* 265*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static Throwable getRootCause(Throwable t) {
/* 280*/    while (t.getCause() != null) {
/* 281*/        t = t.getCause(); 
/*   0*/       }
/* 283*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  public static void throwRootCause(Throwable t) throws Exception {
/* 294*/    t = getRootCause(t);
/* 295*/    if (t instanceof Exception) {
/* 296*/        throw (Exception)t; 
/*   0*/       }
/* 298*/    throw (Error)t;
/*   0*/  }
/*   0*/  
/*   0*/  public static void throwAsIAE(Throwable t) {
/* 307*/    throwAsIAE(t, t.getMessage());
/*   0*/  }
/*   0*/  
/*   0*/  public static void throwAsIAE(Throwable t, String msg) {
/* 317*/    if (t instanceof RuntimeException) {
/* 318*/        throw (RuntimeException)t; 
/*   0*/       }
/* 320*/    if (t instanceof Error) {
/* 321*/        throw (Error)t; 
/*   0*/       }
/* 323*/    throw new IllegalArgumentException(msg, t);
/*   0*/  }
/*   0*/  
/*   0*/  public static void unwrapAndThrowAsIAE(Throwable t) {
/* 333*/    throwAsIAE(getRootCause(t));
/*   0*/  }
/*   0*/  
/*   0*/  public static void unwrapAndThrowAsIAE(Throwable t, String msg) {
/* 343*/    throwAsIAE(getRootCause(t), msg);
/*   0*/  }
/*   0*/  
/*   0*/  public static <T> T createInstance(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
/* 368*/    Constructor<T> ctor = findConstructor(cls, canFixAccess);
/* 369*/    if (ctor == null) {
/* 370*/        throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor"); 
/*   0*/       }
/*   0*/    try {
/* 373*/      return ctor.newInstance(new Object[0]);
/* 374*/    } catch (Exception e) {
/* 375*/      unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage());
/* 376*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static <T> Constructor<T> findConstructor(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
/*   0*/    try {
/* 384*/      Constructor<T> ctor = cls.getDeclaredConstructor(new Class<?>[0]);
/* 385*/      if (canFixAccess) {
/* 386*/        checkAndFixAccess(ctor);
/* 389*/      } else if (!Modifier.isPublic(ctor.getModifiers())) {
/* 390*/        throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: can not instantiate type");
/*   0*/      } 
/* 393*/      return ctor;
/* 394*/    } catch (NoSuchMethodException noSuchMethodException) {
/*   0*/    
/* 396*/    } catch (Exception e) {
/* 397*/      unwrapAndThrowAsIAE(e, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e.getMessage());
/*   0*/    } 
/* 399*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static Object defaultValue(Class<?> cls) {
/* 414*/    if (cls == int.class) {
/* 415*/        return 0; 
/*   0*/       }
/* 417*/    if (cls == long.class) {
/* 418*/        return 0L; 
/*   0*/       }
/* 420*/    if (cls == boolean.class) {
/* 421*/        return Boolean.FALSE; 
/*   0*/       }
/* 423*/    if (cls == double.class) {
/* 424*/        return 0.0D; 
/*   0*/       }
/* 426*/    if (cls == float.class) {
/* 427*/        return 0.0F; 
/*   0*/       }
/* 429*/    if (cls == byte.class) {
/* 430*/        return (byte)0; 
/*   0*/       }
/* 432*/    if (cls == short.class) {
/* 433*/        return (short)0; 
/*   0*/       }
/* 435*/    if (cls == char.class) {
/* 436*/        return Character.MIN_VALUE; 
/*   0*/       }
/* 438*/    throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> wrapperType(Class<?> primitiveType) {
/* 447*/    if (primitiveType == int.class) {
/* 448*/        return Integer.class; 
/*   0*/       }
/* 450*/    if (primitiveType == long.class) {
/* 451*/        return Long.class; 
/*   0*/       }
/* 453*/    if (primitiveType == boolean.class) {
/* 454*/        return Boolean.class; 
/*   0*/       }
/* 456*/    if (primitiveType == double.class) {
/* 457*/        return Double.class; 
/*   0*/       }
/* 459*/    if (primitiveType == float.class) {
/* 460*/        return Float.class; 
/*   0*/       }
/* 462*/    if (primitiveType == byte.class) {
/* 463*/        return Byte.class; 
/*   0*/       }
/* 465*/    if (primitiveType == short.class) {
/* 466*/        return Short.class; 
/*   0*/       }
/* 468*/    if (primitiveType == char.class) {
/* 469*/        return Character.class; 
/*   0*/       }
/* 471*/    throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
/*   0*/  }
/*   0*/  
/*   0*/  public static void checkAndFixAccess(Member member) {
/* 489*/    AccessibleObject ao = (AccessibleObject)member;
/*   0*/    try {
/* 497*/      ao.setAccessible(true);
/* 498*/    } catch (SecurityException se) {
/* 503*/      if (!ao.isAccessible()) {
/* 504*/        Class<?> declClass = member.getDeclaringClass();
/* 505*/        throw new IllegalArgumentException("Can not access " + member + " (from class " + declClass.getName() + "; failed to set access: " + se.getMessage());
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s) {
/* 526*/    if (!s.isEmpty()) {
/* 527*/        return findEnumType((Enum)s.iterator().next()); 
/*   0*/       }
/* 530*/    return EnumTypeLocator.instance.enumTypeFor(s);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<? extends Enum<?>> findEnumType(EnumMap<?, ?> m) {
/* 541*/    if (!m.isEmpty()) {
/* 542*/        return findEnumType((Enum)m.keySet().iterator().next()); 
/*   0*/       }
/* 545*/    return EnumTypeLocator.instance.enumTypeFor(m);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<? extends Enum<?>> findEnumType(Enum<?> en) {
/* 558*/    Class<?> ec = en.getClass();
/* 559*/    if (ec.getSuperclass() != Enum.class) {
/* 560*/        ec = ec.getSuperclass(); 
/*   0*/       }
/* 562*/    return (Class)ec;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
/* 575*/    if (cls.getSuperclass() != Enum.class) {
/* 576*/        cls = cls.getSuperclass(); 
/*   0*/       }
/* 578*/    return (Class)cls;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isJacksonStdImpl(Object impl) {
/* 595*/    return (impl != null && isJacksonStdImpl(impl.getClass()));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isJacksonStdImpl(Class<?> implClass) {
/* 599*/    return (implClass.getAnnotation(JacksonStdImpl.class) != null);
/*   0*/  }
/*   0*/  
/*   0*/  public static final boolean isBogusClass(Class<?> cls) {
/* 604*/    return (cls == Void.class || cls == void.class || cls == NoClass.class);
/*   0*/  }
/*   0*/  
/*   0*/  private static class EnumTypeLocator {
/* 620*/    static final EnumTypeLocator instance = new EnumTypeLocator();
/*   0*/    
/* 629*/    private final Field enumSetTypeField = locateField(EnumSet.class, "elementType", Class.class);
/*   0*/    
/* 630*/    private final Field enumMapTypeField = locateField(EnumMap.class, "elementType", Class.class);
/*   0*/    
/*   0*/    public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set) {
/* 636*/      if (this.enumSetTypeField != null) {
/* 637*/          return (Class<? extends Enum<?>>)get(set, this.enumSetTypeField); 
/*   0*/         }
/* 639*/      throw new IllegalStateException("Can not figure out type for EnumSet (odd JDK platform?)");
/*   0*/    }
/*   0*/    
/*   0*/    public Class<? extends Enum<?>> enumTypeFor(EnumMap<?, ?> set) {
/* 645*/      if (this.enumMapTypeField != null) {
/* 646*/          return (Class<? extends Enum<?>>)get(set, this.enumMapTypeField); 
/*   0*/         }
/* 648*/      throw new IllegalStateException("Can not figure out type for EnumMap (odd JDK platform?)");
/*   0*/    }
/*   0*/    
/*   0*/    private Object get(Object bean, Field field) {
/*   0*/      try {
/* 654*/        return field.get(bean);
/* 655*/      } catch (Exception e) {
/* 656*/        throw new IllegalArgumentException(e);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type) {
/* 662*/      Field found = null;
/* 664*/      Field[] fields = fromClass.getDeclaredFields();
/* 665*/      for (Field f : fields) {
/* 666*/        if (expectedName.equals(f.getName()) && f.getType() == type) {
/* 667*/          found = f;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/* 672*/      if (found == null) {
/* 673*/          for (Field f : fields) {
/* 674*/            if (f.getType() == type) {
/* 676*/              if (found != null) {
/* 676*/                  return null; 
/*   0*/                 }
/* 677*/              found = f;
/*   0*/            } 
/*   0*/          }  
/*   0*/         }
/* 681*/      if (found != null) {
/*   0*/          try {
/* 683*/            found.setAccessible(true);
/* 684*/          } catch (Throwable throwable) {} 
/*   0*/         }
/* 686*/      return found;
/*   0*/    }
/*   0*/  }
/*   0*/}
