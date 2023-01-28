/*   0*/package org.apache.commons.lang3;
/*   0*/
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.LinkedHashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class ClassUtils {
/*   0*/  public static final char PACKAGE_SEPARATOR_CHAR = '.';
/*   0*/  
/*  58*/  public static final String PACKAGE_SEPARATOR = String.valueOf('.');
/*   0*/  
/*   0*/  public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
/*   0*/  
/*  68*/  public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');
/*   0*/  
/*  73*/  private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
/*   0*/  
/*   0*/  static {
/*  75*/    primitiveWrapperMap.put(boolean.class, Boolean.class);
/*  76*/    primitiveWrapperMap.put(byte.class, Byte.class);
/*  77*/    primitiveWrapperMap.put(char.class, Character.class);
/*  78*/    primitiveWrapperMap.put(short.class, Short.class);
/*  79*/    primitiveWrapperMap.put(int.class, Integer.class);
/*  80*/    primitiveWrapperMap.put(long.class, Long.class);
/*  81*/    primitiveWrapperMap.put(double.class, Double.class);
/*  82*/    primitiveWrapperMap.put(float.class, Float.class);
/*  83*/    primitiveWrapperMap.put(void.class, void.class);
/*   0*/  }
/*   0*/  
/*  89*/  private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();
/*   0*/  
/*   0*/  static {
/*  91*/    for (Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
/*  92*/      Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
/*  93*/      if (!primitiveClass.equals(wrapperClass))
/*  94*/        wrapperPrimitiveMap.put(wrapperClass, primitiveClass); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/* 102*/  private static final Map<String, String> abbreviationMap = new HashMap<String, String>();
/*   0*/  
/* 107*/  private static final Map<String, String> reverseAbbreviationMap = new HashMap<String, String>();
/*   0*/  
/*   0*/  private static void addAbbreviation(String primitive, String abbreviation) {
/* 116*/    abbreviationMap.put(primitive, abbreviation);
/* 117*/    reverseAbbreviationMap.put(abbreviation, primitive);
/*   0*/  }
/*   0*/  
/*   0*/  static {
/* 124*/    addAbbreviation("int", "I");
/* 125*/    addAbbreviation("boolean", "Z");
/* 126*/    addAbbreviation("float", "F");
/* 127*/    addAbbreviation("long", "J");
/* 128*/    addAbbreviation("short", "S");
/* 129*/    addAbbreviation("byte", "B");
/* 130*/    addAbbreviation("double", "D");
/* 131*/    addAbbreviation("char", "C");
/*   0*/  }
/*   0*/  
/*   0*/  public static String getShortClassName(Object object, String valueIfNull) {
/* 156*/    if (object == null)
/* 157*/      return valueIfNull; 
/* 159*/    return getShortClassName(object.getClass());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getShortClassName(Class<?> cls) {
/* 169*/    if (cls == null)
/* 170*/      return ""; 
/* 172*/    return getShortClassName(cls.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getShortClassName(String className) {
/* 184*/    if (className == null)
/* 185*/      return ""; 
/* 187*/    if (className.length() == 0)
/* 188*/      return ""; 
/* 191*/    StringBuilder arrayPrefix = new StringBuilder();
/* 194*/    if (className.startsWith("[")) {
/* 195*/      while (className.charAt(0) == '[') {
/* 196*/        className = className.substring(1);
/* 197*/        arrayPrefix.append("[]");
/*   0*/      } 
/* 200*/      if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';')
/* 201*/        className = className.substring(1, className.length() - 1); 
/*   0*/    } 
/* 205*/    if (reverseAbbreviationMap.containsKey(className))
/* 206*/      className = reverseAbbreviationMap.get(className); 
/* 209*/    int lastDotIdx = className.lastIndexOf('.');
/* 210*/    int innerIdx = className.indexOf('$', (lastDotIdx == -1) ? 0 : (lastDotIdx + 1));
/* 212*/    String out = className.substring(lastDotIdx + 1);
/* 213*/    if (innerIdx != -1)
/* 214*/      out = out.replace('$', '.'); 
/* 216*/    return out + arrayPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPackageName(Object object, String valueIfNull) {
/* 229*/    if (object == null)
/* 230*/      return valueIfNull; 
/* 232*/    return getPackageName(object.getClass());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPackageName(Class<?> cls) {
/* 242*/    if (cls == null)
/* 243*/      return ""; 
/* 245*/    return getPackageName(cls.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPackageName(String className) {
/* 258*/    if (className == null || className.length() == 0)
/* 259*/      return ""; 
/* 263*/    while (className.charAt(0) == '[')
/* 264*/      className = className.substring(1); 
/* 267*/    if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';')
/* 268*/      className = className.substring(1); 
/* 271*/    int i = className.lastIndexOf('.');
/* 272*/    if (i == -1)
/* 273*/      return ""; 
/* 275*/    return className.substring(0, i);
/*   0*/  }
/*   0*/  
/*   0*/  public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
/* 288*/    if (cls == null)
/* 289*/      return null; 
/* 291*/    List<Class<?>> classes = new ArrayList<Class<?>>();
/* 292*/    Class<?> superclass = cls.getSuperclass();
/* 293*/    while (superclass != null) {
/* 294*/      classes.add(superclass);
/* 295*/      superclass = superclass.getSuperclass();
/*   0*/    } 
/* 297*/    return classes;
/*   0*/  }
/*   0*/  
/*   0*/  public static List<Class<?>> getAllInterfaces(Class<?> cls) {
/* 314*/    if (cls == null)
/* 315*/      return null; 
/* 318*/    LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
/* 319*/    getAllInterfaces(cls, interfacesFound);
/* 321*/    return new ArrayList<Class<?>>(interfacesFound);
/*   0*/  }
/*   0*/  
/*   0*/  private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
/* 331*/    while (cls != null) {
/* 332*/      Class<?>[] interfaces = cls.getInterfaces();
/* 334*/      for (Class<?> i : interfaces) {
/* 335*/        if (interfacesFound.add(i))
/* 336*/          getAllInterfaces(i, interfacesFound); 
/*   0*/      } 
/* 340*/      cls = cls.getSuperclass();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
/* 359*/    if (classNames == null)
/* 360*/      return null; 
/* 362*/    List<Class<?>> classes = new ArrayList<Class<?>>(classNames.size());
/* 363*/    for (String className : classNames) {
/*   0*/      try {
/* 365*/        classes.add(Class.forName(className));
/* 366*/      } catch (Exception ex) {
/* 367*/        classes.add(null);
/*   0*/      } 
/*   0*/    } 
/* 370*/    return classes;
/*   0*/  }
/*   0*/  
/*   0*/  public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
/* 386*/    if (classes == null)
/* 387*/      return null; 
/* 389*/    List<String> classNames = new ArrayList<String>(classes.size());
/* 390*/    for (Class<?> cls : classes) {
/* 391*/      if (cls == null) {
/* 392*/        classNames.add(null);
/*   0*/        continue;
/*   0*/      } 
/* 394*/      classNames.add(cls.getName());
/*   0*/    } 
/* 397*/    return classNames;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray) {
/* 439*/    return isAssignable(classArray, toClassArray, SystemUtils.isJavaVersionAtLeast(1.5F));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, boolean autoboxing) {
/* 475*/    if (!ArrayUtils.isSameLength((Object[])classArray, (Object[])toClassArray))
/* 476*/      return false; 
/* 478*/    if (classArray == null)
/* 479*/      classArray = ArrayUtils.EMPTY_CLASS_ARRAY; 
/* 481*/    if (toClassArray == null)
/* 482*/      toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY; 
/* 484*/    for (int i = 0; i < classArray.length; i++) {
/* 485*/      if (!isAssignable(classArray[i], toClassArray[i], autoboxing))
/* 486*/        return false; 
/*   0*/    } 
/* 489*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
/* 524*/    return isAssignable(cls, toClass, SystemUtils.isJavaVersionAtLeast(1.5F));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAssignable(Class<?> cls, Class<?> toClass, boolean autoboxing) {
/* 555*/    if (toClass == null)
/* 556*/      return false; 
/* 559*/    if (cls == null)
/* 560*/      return !toClass.isPrimitive(); 
/* 563*/    if (autoboxing) {
/* 564*/      if (cls.isPrimitive() && !toClass.isPrimitive()) {
/* 565*/        cls = primitiveToWrapper(cls);
/* 566*/        if (cls == null)
/* 567*/          return false; 
/*   0*/      } 
/* 570*/      if (toClass.isPrimitive() && !cls.isPrimitive()) {
/* 571*/        cls = wrapperToPrimitive(cls);
/* 572*/        if (cls == null)
/* 573*/          return false; 
/*   0*/      } 
/*   0*/    } 
/* 577*/    if (cls.equals(toClass))
/* 578*/      return true; 
/* 580*/    if (cls.isPrimitive()) {
/* 581*/      if (!toClass.isPrimitive())
/* 582*/        return false; 
/* 584*/      if (int.class.equals(cls))
/* 585*/        return (long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass)); 
/* 589*/      if (long.class.equals(cls))
/* 590*/        return (float.class.equals(toClass) || double.class.equals(toClass)); 
/* 593*/      if (boolean.class.equals(cls))
/* 594*/        return false; 
/* 596*/      if (double.class.equals(cls))
/* 597*/        return false; 
/* 599*/      if (float.class.equals(cls))
/* 600*/        return double.class.equals(toClass); 
/* 602*/      if (char.class.equals(cls))
/* 603*/        return (int.class.equals(toClass) || long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass)); 
/* 608*/      if (short.class.equals(cls))
/* 609*/        return (int.class.equals(toClass) || long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass)); 
/* 614*/      if (byte.class.equals(cls))
/* 615*/        return (short.class.equals(toClass) || int.class.equals(toClass) || long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass)); 
/* 622*/      return false;
/*   0*/    } 
/* 624*/    return toClass.isAssignableFrom(cls);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> primitiveToWrapper(Class<?> cls) {
/* 640*/    Class<?> convertedClass = cls;
/* 641*/    if (cls != null && cls.isPrimitive())
/* 642*/      convertedClass = primitiveWrapperMap.get(cls); 
/* 644*/    return convertedClass;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?>[] primitivesToWrappers(Class<?>[] classes) {
/* 658*/    if (classes == null)
/* 659*/      return null; 
/* 662*/    if (classes.length == 0)
/* 663*/      return classes; 
/* 666*/    Class<?>[] convertedClasses = new Class<?>[classes.length];
/* 667*/    for (int i = 0; i < classes.length; i++)
/* 668*/      convertedClasses[i] = primitiveToWrapper(classes[i]); 
/* 670*/    return convertedClasses;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> wrapperToPrimitive(Class<?> cls) {
/* 690*/    return wrapperPrimitiveMap.get(cls);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?>[] wrappersToPrimitives(Class<?>[] classes) {
/* 708*/    if (classes == null)
/* 709*/      return null; 
/* 712*/    if (classes.length == 0)
/* 713*/      return classes; 
/* 716*/    Class<?>[] convertedClasses = new Class<?>[classes.length];
/* 717*/    for (int i = 0; i < classes.length; i++)
/* 718*/      convertedClasses[i] = wrapperToPrimitive(classes[i]); 
/* 720*/    return convertedClasses;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isInnerClass(Class<?> cls) {
/* 733*/    if (cls == null)
/* 734*/      return false; 
/* 736*/    return (cls.getName().indexOf('$') >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> getClass(ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
/*   0*/    Class<?> clazz;
/* 755*/    if (abbreviationMap.containsKey(className)) {
/* 756*/      String clsName = "[" + (String)abbreviationMap.get(className);
/* 757*/      clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
/*   0*/    } else {
/* 759*/      clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
/*   0*/    } 
/* 761*/    return clazz;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
/* 776*/    return getClass(classLoader, className, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> getClass(String className) throws ClassNotFoundException {
/* 790*/    return getClass(className, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> getClass(String className, boolean initialize) throws ClassNotFoundException {
/* 805*/    ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
/* 806*/    ClassLoader loader = (contextCL == null) ? ClassUtils.class.getClassLoader() : contextCL;
/* 807*/    return getClass(loader, className, initialize);
/*   0*/  }
/*   0*/  
/*   0*/  public static Method getPublicMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) throws SecurityException, NoSuchMethodException {
/* 836*/    Method declaredMethod = cls.getMethod(methodName, parameterTypes);
/* 837*/    if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers()))
/* 838*/      return declaredMethod; 
/* 841*/    List<Class<?>> candidateClasses = new ArrayList<Class<?>>();
/* 842*/    candidateClasses.addAll(getAllInterfaces(cls));
/* 843*/    candidateClasses.addAll(getAllSuperclasses(cls));
/* 845*/    for (Class<?> candidateClass : candidateClasses) {
/*   0*/      Method candidateMethod;
/* 846*/      if (!Modifier.isPublic(candidateClass.getModifiers()))
/*   0*/        continue; 
/*   0*/      try {
/* 851*/        candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
/* 852*/      } catch (NoSuchMethodException ex) {
/*   0*/        continue;
/*   0*/      } 
/* 855*/      if (Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers()))
/* 856*/        return candidateMethod; 
/*   0*/    } 
/* 860*/    throw new NoSuchMethodException("Can't find a public method for " + methodName + " " + ArrayUtils.toString(parameterTypes));
/*   0*/  }
/*   0*/  
/*   0*/  private static String toCanonicalName(String className) {
/* 872*/    className = StringUtils.deleteWhitespace(className);
/* 873*/    if (className == null)
/* 874*/      throw new NullPointerException("className must not be null."); 
/* 875*/    if (className.endsWith("[]")) {
/* 876*/      StringBuilder classNameBuffer = new StringBuilder();
/* 877*/      while (className.endsWith("[]")) {
/* 878*/        className = className.substring(0, className.length() - 2);
/* 879*/        classNameBuffer.append("[");
/*   0*/      } 
/* 881*/      String abbreviation = abbreviationMap.get(className);
/* 882*/      if (abbreviation != null) {
/* 883*/        classNameBuffer.append(abbreviation);
/*   0*/      } else {
/* 885*/        classNameBuffer.append("L").append(className).append(";");
/*   0*/      } 
/* 887*/      className = classNameBuffer.toString();
/*   0*/    } 
/* 889*/    return className;
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?>[] toClass(Object[] array) {
/* 903*/    if (array == null)
/* 904*/      return null; 
/* 905*/    if (array.length == 0)
/* 906*/      return ArrayUtils.EMPTY_CLASS_ARRAY; 
/* 908*/    Class<?>[] classes = new Class<?>[array.length];
/* 909*/    for (int i = 0; i < array.length; i++) {
/* 910*/      array[i];
/* 910*/      i[array[i]] = (array[i] == null) ? null : array[i].getClass();
/*   0*/    } 
/* 912*/    return classes;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getShortCanonicalName(Object object, String valueIfNull) {
/* 926*/    if (object == null)
/* 927*/      return valueIfNull; 
/* 929*/    return getShortCanonicalName(object.getClass().getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getShortCanonicalName(Class<?> cls) {
/* 940*/    if (cls == null)
/* 941*/      return ""; 
/* 943*/    return getShortCanonicalName(cls.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getShortCanonicalName(String canonicalName) {
/* 956*/    return getShortClassName(getCanonicalName(canonicalName));
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPackageCanonicalName(Object object, String valueIfNull) {
/* 970*/    if (object == null)
/* 971*/      return valueIfNull; 
/* 973*/    return getPackageCanonicalName(object.getClass().getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPackageCanonicalName(Class<?> cls) {
/* 984*/    if (cls == null)
/* 985*/      return ""; 
/* 987*/    return getPackageCanonicalName(cls.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPackageCanonicalName(String canonicalName) {
/*1001*/    return getPackageName(getCanonicalName(canonicalName));
/*   0*/  }
/*   0*/  
/*   0*/  private static String getCanonicalName(String className) {
/*1021*/    className = StringUtils.deleteWhitespace(className);
/*1022*/    if (className == null)
/*1023*/      return null; 
/*1025*/    int dim = 0;
/*1026*/    while (className.startsWith("[")) {
/*1027*/      dim++;
/*1028*/      className = className.substring(1);
/*   0*/    } 
/*1030*/    if (dim < 1)
/*1031*/      return className; 
/*1033*/    if (className.startsWith("L")) {
/*1034*/      className = className.substring(1, className.endsWith(";") ? (className.length() - 1) : className.length());
/*1040*/    } else if (className.length() > 0) {
/*1041*/      className = reverseAbbreviationMap.get(className.substring(0, 1));
/*   0*/    } 
/*1044*/    StringBuilder canonicalClassNameBuffer = new StringBuilder(className);
/*1045*/    for (int i = 0; i < dim; i++)
/*1046*/      canonicalClassNameBuffer.append("[]"); 
/*1048*/    return canonicalClassNameBuffer.toString();
/*   0*/  }
/*   0*/}
