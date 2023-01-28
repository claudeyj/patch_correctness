/*   0*/package org.apache.commons.jxpath.util;
/*   0*/
/*   0*/import java.beans.IndexedPropertyDescriptor;
/*   0*/import java.beans.PropertyDescriptor;
/*   0*/import java.lang.reflect.Array;
/*   0*/import java.lang.reflect.InvocationTargetException;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import org.apache.commons.jxpath.Container;
/*   0*/import org.apache.commons.jxpath.DynamicPropertyHandler;
/*   0*/import org.apache.commons.jxpath.JXPathException;
/*   0*/
/*   0*/public class ValueUtils {
/*  44*/  private static Map dynamicPropertyHandlerMap = new HashMap();
/*   0*/  
/*   0*/  private static final int UNKNOWN_LENGTH_MAX_COUNT = 16000;
/*   0*/  
/*   0*/  public static boolean isCollection(Object value) {
/*  53*/    value = getValue(value);
/*  54*/    if (value == null) {
/*  55*/        return false; 
/*   0*/       }
/*  57*/    if (value.getClass().isArray()) {
/*  58*/        return true; 
/*   0*/       }
/*  60*/    if (value instanceof Collection) {
/*  61*/        return true; 
/*   0*/       }
/*  63*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static int getCollectionHint(Class clazz) {
/*  74*/    if (clazz.isArray()) {
/*  75*/        return 1; 
/*   0*/       }
/*  78*/    if (Collection.class.isAssignableFrom(clazz)) {
/*  79*/        return 1; 
/*   0*/       }
/*  82*/    if (clazz.isPrimitive()) {
/*  83*/        return -1; 
/*   0*/       }
/*  86*/    if (clazz.isInterface()) {
/*  87*/        return 0; 
/*   0*/       }
/*  90*/    if (Modifier.isFinal(clazz.getModifiers())) {
/*  91*/        return -1; 
/*   0*/       }
/*  94*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public static int getIndexedPropertyLength(Object object, IndexedPropertyDescriptor pd) {
/* 110*/    if (pd.getReadMethod() != null) {
/* 111*/        return getLength(getValue(object, pd)); 
/*   0*/       }
/* 114*/    Method readMethod = pd.getIndexedReadMethod();
/* 115*/    if (readMethod == null) {
/* 116*/        throw new JXPathException("No indexed read method for property " + pd.getName()); 
/*   0*/       }
/* 120*/    for (int i = 0; i < 16000; i++) {
/*   0*/      try {
/* 122*/        readMethod.invoke(object, new Object[] { new Integer(i) });
/* 124*/      } catch (Throwable t) {
/* 125*/        return i;
/*   0*/      } 
/*   0*/    } 
/* 129*/    throw new JXPathException("Cannot determine the length of the indexed property " + pd.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public static int getLength(Object collection) {
/* 141*/    if (collection == null) {
/* 142*/        return 0; 
/*   0*/       }
/* 144*/    collection = getValue(collection);
/* 145*/    if (collection.getClass().isArray()) {
/* 146*/        return Array.getLength(collection); 
/*   0*/       }
/* 148*/    if (collection instanceof Collection) {
/* 149*/        return ((Collection)collection).size(); 
/*   0*/       }
/* 151*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public static Iterator iterate(Object collection) {
/* 162*/    if (collection == null) {
/* 163*/        return Collections.EMPTY_LIST.iterator(); 
/*   0*/       }
/* 165*/    if (collection.getClass().isArray()) {
/* 166*/      int length = Array.getLength(collection);
/* 167*/      if (length == 0) {
/* 168*/          return Collections.EMPTY_LIST.iterator(); 
/*   0*/         }
/* 170*/      ArrayList<Object> list = new ArrayList();
/* 171*/      for (int i = 0; i < length; i++) {
/* 172*/          list.add(Array.get(collection, i)); 
/*   0*/         }
/* 174*/      return list.iterator();
/*   0*/    } 
/* 176*/    if (collection instanceof Collection) {
/* 177*/        return ((Collection)collection).iterator(); 
/*   0*/       }
/* 179*/    return Collections.<Object>singletonList(collection).iterator();
/*   0*/  }
/*   0*/  
/*   0*/  public static Object expandCollection(Object collection, int size) {
/* 190*/    if (collection == null) {
/* 191*/        return null; 
/*   0*/       }
/* 193*/    if (size < getLength(collection)) {
/* 194*/        throw new JXPathException("adjustment of " + collection + " to size " + size + " is not an expansion"); 
/*   0*/       }
/* 197*/    if (collection.getClass().isArray()) {
/* 198*/      Object bigger = Array.newInstance(collection.getClass().getComponentType(), size);
/* 202*/      System.arraycopy(collection, 0, bigger, 0, Array.getLength(collection));
/* 208*/      return bigger;
/*   0*/    } 
/* 210*/    if (collection instanceof Collection) {
/* 211*/      while (((Collection)collection).size() < size) {
/* 212*/          ((Collection)collection).add(null); 
/*   0*/         }
/* 214*/      return collection;
/*   0*/    } 
/* 216*/    throw new JXPathException("Cannot turn " + collection.getClass().getName() + " into a collection of size " + size);
/*   0*/  }
/*   0*/  
/*   0*/  public static Object remove(Object collection, int index) {
/* 230*/    collection = getValue(collection);
/* 231*/    if (collection == null) {
/* 232*/        return null; 
/*   0*/       }
/* 234*/    if (index >= getLength(collection)) {
/* 235*/        throw new JXPathException("No such element at index " + index); 
/*   0*/       }
/* 237*/    if (collection.getClass().isArray()) {
/* 238*/      int length = Array.getLength(collection);
/* 239*/      Object smaller = Array.newInstance(collection.getClass().getComponentType(), length - 1);
/* 243*/      if (index > 0) {
/* 244*/          System.arraycopy(collection, 0, smaller, 0, index); 
/*   0*/         }
/* 246*/      if (index < length - 1) {
/* 247*/          System.arraycopy(collection, index + 1, smaller, index, length - index - 1); 
/*   0*/         }
/* 254*/      return smaller;
/*   0*/    } 
/* 256*/    if (collection instanceof List) {
/* 257*/      int size = ((List)collection).size();
/* 258*/      if (index < size) {
/* 259*/          ((List)collection).remove(index); 
/*   0*/         }
/* 261*/      return collection;
/*   0*/    } 
/* 263*/    if (collection instanceof Collection) {
/* 264*/      Iterator it = ((Collection)collection).iterator();
/* 265*/      for (int i = 0; i < index && 
/* 266*/        it.hasNext(); i++) {
/* 269*/          it.next(); 
/*   0*/         }
/* 271*/      if (it.hasNext()) {
/* 272*/        it.next();
/* 273*/        it.remove();
/*   0*/      } 
/* 275*/      return collection;
/*   0*/    } 
/* 277*/    throw new JXPathException("Cannot remove " + collection.getClass().getName() + "[" + index + "]");
/*   0*/  }
/*   0*/  
/*   0*/  public static Object getValue(Object collection, int index) {
/* 292*/    collection = getValue(collection);
/* 293*/    Object value = collection;
/* 294*/    if (collection != null) {
/* 295*/        if (collection.getClass().isArray()) {
/* 296*/          if (index < 0 || index >= Array.getLength(collection)) {
/* 297*/              return null; 
/*   0*/             }
/* 299*/          value = Array.get(collection, index);
/* 301*/        } else if (collection instanceof List) {
/* 302*/          if (index < 0 || index >= ((List)collection).size()) {
/* 303*/              return null; 
/*   0*/             }
/* 305*/          value = ((List)collection).get(index);
/* 307*/        } else if (collection instanceof Collection) {
/* 308*/          int i = 0;
/* 309*/          Iterator it = ((Collection)collection).iterator();
/* 310*/          for (; i < index; i++) {
/* 311*/              it.next(); 
/*   0*/             }
/* 313*/          if (it.hasNext()) {
/* 314*/            value = it.next();
/*   0*/          } else {
/* 317*/            value = null;
/*   0*/          } 
/*   0*/        }  
/*   0*/       }
/* 321*/    return value;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setValue(Object collection, int index, Object value) {
/* 332*/    collection = getValue(collection);
/* 333*/    if (collection != null) {
/* 334*/        if (collection.getClass().isArray()) {
/* 335*/          Array.set(collection, index, convert(value, collection.getClass().getComponentType()));
/* 340*/        } else if (collection instanceof List) {
/* 341*/          ((List<Object>)collection).set(index, value);
/* 343*/        } else if (collection instanceof Collection) {
/* 344*/          throw new UnsupportedOperationException("Cannot set value of an element of a " + collection.getClass().getName());
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static Object getValue(Object bean, PropertyDescriptor propertyDescriptor) {
/*   0*/    Object value;
/*   0*/    try {
/* 362*/      Method method = getAccessibleMethod(propertyDescriptor.getReadMethod());
/* 364*/      if (method == null) {
/* 365*/          throw new JXPathException("No read method"); 
/*   0*/         }
/* 367*/      value = method.invoke(bean, new Object[0]);
/* 369*/    } catch (Exception ex) {
/* 370*/      throw new JXPathException("Cannot access property: " + ((bean == null) ? "null" : bean.getClass().getName()) + "." + propertyDescriptor.getName(), ex);
/*   0*/    } 
/* 377*/    return value;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setValue(Object bean, PropertyDescriptor propertyDescriptor, Object value) {
/*   0*/    try {
/* 390*/      Method method = getAccessibleMethod(propertyDescriptor.getWriteMethod());
/* 392*/      if (method == null) {
/* 393*/          throw new JXPathException("No write method"); 
/*   0*/         }
/* 395*/      value = convert(value, propertyDescriptor.getPropertyType());
/* 396*/      method.invoke(bean, new Object[] { value });
/* 398*/    } catch (Exception ex) {
/* 399*/      throw new JXPathException("Cannot modify property: " + ((bean == null) ? "null" : bean.getClass().getName()) + "." + propertyDescriptor.getName(), ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static Object convert(Object value, Class type) {
/*   0*/    try {
/* 416*/      return TypeUtils.convert(value, type);
/* 418*/    } catch (Exception ex) {
/* 419*/      throw new JXPathException("Cannot convert value of class " + ((value == null) ? "null" : value.getClass().getName()) + " to type " + type, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Object getValue(Object bean, PropertyDescriptor propertyDescriptor, int index) {
/* 438*/    if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
/*   0*/        try {
/* 440*/          IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)propertyDescriptor;
/* 442*/          Method method = ipd.getIndexedReadMethod();
/* 443*/          if (method != null) {
/* 444*/              return method.invoke(bean, new Object[] { new Integer(index) }); 
/*   0*/             }
/* 449*/        } catch (InvocationTargetException ex) {
/* 450*/          Throwable t = ex.getTargetException();
/* 451*/          if (t instanceof IndexOutOfBoundsException) {
/* 452*/              return null; 
/*   0*/             }
/* 454*/          throw new JXPathException("Cannot access property: " + propertyDescriptor.getName(), t);
/* 458*/        } catch (Throwable ex) {
/* 459*/          throw new JXPathException("Cannot access property: " + propertyDescriptor.getName(), ex);
/*   0*/        }  
/*   0*/       }
/* 467*/    return getValue(getValue(bean, propertyDescriptor), index);
/*   0*/  }
/*   0*/  
/*   0*/  public static void setValue(Object bean, PropertyDescriptor propertyDescriptor, int index, Object value) {
/* 481*/    if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
/*   0*/        try {
/* 483*/          IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)propertyDescriptor;
/* 485*/          Method method = ipd.getIndexedWriteMethod();
/* 486*/          if (method != null) {
/* 487*/            method.invoke(bean, new Object[] { new Integer(index), convert(value, ipd.getIndexedPropertyType()) });
/*   0*/            return;
/*   0*/          } 
/* 495*/        } catch (Exception ex) {
/* 496*/          throw new RuntimeException("Cannot access property: " + propertyDescriptor.getName() + ", " + ex.getMessage());
/*   0*/        }  
/*   0*/       }
/* 504*/    Object collection = getValue(bean, propertyDescriptor);
/* 505*/    if (isCollection(collection)) {
/* 506*/      setValue(collection, index, value);
/* 508*/    } else if (index == 0) {
/* 509*/      setValue(bean, propertyDescriptor, value);
/*   0*/    } else {
/* 512*/      throw new RuntimeException("Not a collection: " + propertyDescriptor.getName());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Object getValue(Object object) {
/* 524*/    while (object instanceof Container) {
/* 525*/        object = ((Container)object).getValue(); 
/*   0*/       }
/* 527*/    return object;
/*   0*/  }
/*   0*/  
/*   0*/  public static DynamicPropertyHandler getDynamicPropertyHandler(Class clazz) {
/* 537*/    DynamicPropertyHandler handler = (DynamicPropertyHandler)dynamicPropertyHandlerMap.get(clazz);
/* 539*/    if (handler == null) {
/*   0*/      try {
/* 541*/        handler = clazz.newInstance();
/* 543*/      } catch (Exception ex) {
/* 544*/        throw new JXPathException("Cannot allocate dynamic property handler of class " + clazz.getName(), ex);
/*   0*/      } 
/* 549*/      dynamicPropertyHandlerMap.put(clazz, handler);
/*   0*/    } 
/* 551*/    return handler;
/*   0*/  }
/*   0*/  
/*   0*/  public static Method getAccessibleMethod(Method method) {
/* 572*/    if (method == null) {
/* 573*/        return null; 
/*   0*/       }
/* 577*/    if (!Modifier.isPublic(method.getModifiers())) {
/* 578*/        return null; 
/*   0*/       }
/* 582*/    Class<?> clazz = method.getDeclaringClass();
/* 583*/    if (Modifier.isPublic(clazz.getModifiers())) {
/* 584*/        return method; 
/*   0*/       }
/* 587*/    String name = method.getName();
/* 588*/    Class[] parameterTypes = method.getParameterTypes();
/* 589*/    while (clazz != null) {
/* 591*/      Method aMethod = getAccessibleMethodFromInterfaceNest(clazz, name, parameterTypes);
/* 593*/      if (aMethod != null) {
/* 594*/          return aMethod; 
/*   0*/         }
/* 597*/      clazz = clazz.getSuperclass();
/* 598*/      if (clazz != null && Modifier.isPublic(clazz.getModifiers())) {
/*   0*/          try {
/* 600*/            return clazz.getDeclaredMethod(name, parameterTypes);
/* 602*/          } catch (NoSuchMethodException noSuchMethodException) {} 
/*   0*/         }
/*   0*/    } 
/* 607*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static Method getAccessibleMethodFromInterfaceNest(Class clazz, String methodName, Class[] parameterTypes) {
/* 624*/    Method method = null;
/* 627*/    Class[] interfaces = clazz.getInterfaces();
/* 628*/    for (int i = 0; i < interfaces.length; i++) {
/* 631*/      if (Modifier.isPublic(interfaces[i].getModifiers())) {
/*   0*/        try {
/* 637*/          method = interfaces[i].getDeclaredMethod(methodName, parameterTypes);
/* 640*/        } catch (NoSuchMethodException noSuchMethodException) {}
/* 643*/        if (method != null) {
/*   0*/            break; 
/*   0*/           }
/* 648*/        method = getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
/* 653*/        if (method != null) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 659*/    return method;
/*   0*/  }
/*   0*/}
