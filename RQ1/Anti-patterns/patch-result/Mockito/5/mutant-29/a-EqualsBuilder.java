/*   0*/package org.mockito.internal.matchers.apachecommons;
/*   0*/
/*   0*/import java.lang.reflect.AccessibleObject;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.math.BigDecimal;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.List;
/*   0*/
/*   0*/class EqualsBuilder {
/*   0*/  private boolean isEquals = true;
/*   0*/  
/*   0*/  public static boolean reflectionEquals(Object lhs, Object rhs) {
/* 116*/    return reflectionEquals(lhs, rhs, false, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean reflectionEquals(Object lhs, Object rhs, String[] excludeFields) {
/* 139*/    return reflectionEquals(lhs, rhs, false, null, excludeFields);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients) {
/* 163*/    return reflectionEquals(lhs, rhs, testTransients, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class reflectUpToClass) {
/* 192*/    return reflectionEquals(lhs, rhs, testTransients, reflectUpToClass, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class reflectUpToClass, String[] excludeFields) {
/*   0*/    Class<?> testClass;
/* 223*/    if (lhs == rhs)
/* 224*/      return true; 
/* 226*/    if (lhs == null || rhs == null)
/* 227*/      return false; 
/* 233*/    Class<?> lhsClass = lhs.getClass();
/* 234*/    Class<?> rhsClass = rhs.getClass();
/* 236*/    if (lhsClass.isInstance(rhs)) {
/* 237*/      testClass = lhsClass;
/* 238*/      if (!rhsClass.isInstance(lhs))
/* 240*/        testClass = rhsClass; 
/* 242*/    } else if (rhsClass.isInstance(lhs)) {
/* 243*/      testClass = rhsClass;
/* 244*/      if (!lhsClass.isInstance(rhs))
/* 246*/        testClass = lhsClass; 
/*   0*/    } else {
/* 250*/      return false;
/*   0*/    } 
/* 252*/    EqualsBuilder equalsBuilder = new EqualsBuilder();
/*   0*/    try {
/* 254*/      reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
/* 255*/      while (testClass.getSuperclass() != null && testClass != reflectUpToClass) {
/* 256*/        testClass = testClass.getSuperclass();
/* 257*/        reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
/*   0*/      } 
/* 259*/    } catch (IllegalArgumentException e) {
/* 265*/      return false;
/*   0*/    } 
/* 267*/    return equalsBuilder.isEquals();
/*   0*/  }
/*   0*/  
/*   0*/  private static void reflectionAppend(Object lhs, Object rhs, Class clazz, EqualsBuilder builder, boolean useTransients, String[] excludeFields) {
/* 288*/    Field[] fields = clazz.getDeclaredFields();
/* 289*/    List excludedFieldList = (excludeFields != null) ? Arrays.<String>asList(excludeFields) : Collections.EMPTY_LIST;
/* 290*/    AccessibleObject.setAccessible((AccessibleObject[])fields, true);
/* 291*/    for (int i = 0; i < fields.length && builder.isEquals; i++) {
/* 292*/      Field f = fields[i];
/* 293*/      if (!excludedFieldList.contains(f.getName()) && f.getName().indexOf('$') == -1 && (useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()))
/*   0*/        try {
/* 298*/          builder.append(f.get(lhs), f.get(rhs));
/* 299*/        } catch (IllegalAccessException e) {
/* 302*/          throw new InternalError("Unexpected IllegalAccessException");
/*   0*/        }  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder appendSuper(boolean superEquals) {
/* 318*/    this.isEquals &= superEquals;
/* 319*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(Object lhs, Object rhs) {
/* 333*/    if (!this.isEquals)
/* 334*/      return this; 
/* 336*/    if (lhs == rhs)
/* 337*/      return this; 
/* 339*/    if (lhs == null || rhs == null) {
/* 340*/      setEquals(false);
/* 341*/      return this;
/*   0*/    } 
/* 343*/    Class<?> lhsClass = lhs.getClass();
/* 344*/    if (!lhsClass.isArray()) {
/* 345*/      if (lhs instanceof BigDecimal && rhs instanceof BigDecimal) {
/* 346*/        this.isEquals = (((BigDecimal)lhs).compareTo((BigDecimal)rhs) == 0);
/*   0*/      } else {
/* 349*/        this.isEquals = lhs.equals(rhs);
/*   0*/      } 
/* 351*/    } else if (lhs.getClass() != rhs.getClass()) {
/* 353*/      setEquals(false);
/* 357*/    } else if (lhs instanceof long[]) {
/* 358*/      append((long[])lhs, (long[])rhs);
/* 359*/    } else if (lhs instanceof int[]) {
/* 360*/      append((int[])lhs, (int[])rhs);
/* 361*/    } else if (lhs instanceof short[]) {
/* 362*/      append((short[])lhs, (short[])rhs);
/* 363*/    } else if (lhs instanceof char[]) {
/* 364*/      append((char[])lhs, (char[])rhs);
/* 365*/    } else if (lhs instanceof byte[]) {
/* 366*/      append((byte[])lhs, (byte[])rhs);
/* 367*/    } else if (lhs instanceof double[]) {
/* 368*/      append((double[])lhs, (double[])rhs);
/* 369*/    } else if (lhs instanceof float[]) {
/* 370*/      append((float[])lhs, (float[])rhs);
/* 371*/    } else if (lhs instanceof boolean[]) {
/* 372*/      append((boolean[])lhs, (boolean[])rhs);
/*   0*/    } else {
/* 375*/      append((Object[])lhs, (Object[])rhs);
/*   0*/    } 
/* 377*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(long lhs, long rhs) {
/* 392*/    this.isEquals &= (lhs == rhs) ? true : false;
/* 393*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(int lhs, int rhs) {
/* 404*/    this.isEquals &= (lhs == rhs) ? true : false;
/* 405*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(short lhs, short rhs) {
/* 416*/    this.isEquals &= (lhs == rhs) ? true : false;
/* 417*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(char lhs, char rhs) {
/* 428*/    this.isEquals &= (lhs == rhs) ? true : false;
/* 429*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(byte lhs, byte rhs) {
/* 440*/    this.isEquals &= (lhs == rhs) ? true : false;
/* 441*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(double lhs, double rhs) {
/* 458*/    if (!this.isEquals)
/* 459*/      return this; 
/* 461*/    return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(float lhs, float rhs) {
/* 478*/    if (!this.isEquals)
/* 479*/      return this; 
/* 481*/    return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(boolean lhs, boolean rhs) {
/* 492*/    this.isEquals &= (lhs == rhs) ? true : false;
/* 493*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(Object[] lhs, Object[] rhs) {
/* 507*/    if (!this.isEquals)
/* 508*/      return this; 
/* 510*/    if (lhs == rhs)
/* 511*/      return this; 
/* 513*/    if (lhs == null || rhs == null) {
/* 514*/      setEquals(false);
/* 515*/      return this;
/*   0*/    } 
/* 517*/    if (lhs.length != rhs.length) {
/* 518*/      setEquals(false);
/* 519*/      return this;
/*   0*/    } 
/* 521*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 522*/      append(lhs[i], rhs[i]); 
/* 524*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(long[] lhs, long[] rhs) {
/* 538*/    if (!this.isEquals)
/* 539*/      return this; 
/* 541*/    if (lhs == rhs)
/* 542*/      return this; 
/* 544*/    if (lhs == null || rhs == null) {
/* 545*/      setEquals(false);
/* 546*/      return this;
/*   0*/    } 
/* 548*/    if (lhs.length != rhs.length) {
/* 549*/      setEquals(false);
/* 550*/      return this;
/*   0*/    } 
/* 552*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 553*/      append(lhs[i], rhs[i]); 
/* 555*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(int[] lhs, int[] rhs) {
/* 569*/    if (!this.isEquals)
/* 570*/      return this; 
/* 572*/    if (lhs == rhs)
/* 573*/      return this; 
/* 575*/    if (lhs == null || rhs == null) {
/* 576*/      setEquals(false);
/* 577*/      return this;
/*   0*/    } 
/* 579*/    if (lhs.length != rhs.length) {
/* 580*/      setEquals(false);
/* 581*/      return this;
/*   0*/    } 
/* 583*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 584*/      append(lhs[i], rhs[i]); 
/* 586*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(short[] lhs, short[] rhs) {
/* 600*/    if (!this.isEquals)
/* 601*/      return this; 
/* 603*/    if (lhs == rhs)
/* 604*/      return this; 
/* 606*/    if (lhs == null || rhs == null) {
/* 607*/      setEquals(false);
/* 608*/      return this;
/*   0*/    } 
/* 610*/    if (lhs.length != rhs.length) {
/* 611*/      setEquals(false);
/* 612*/      return this;
/*   0*/    } 
/* 614*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 615*/      append(lhs[i], rhs[i]); 
/* 617*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(char[] lhs, char[] rhs) {
/* 631*/    if (!this.isEquals)
/* 632*/      return this; 
/* 634*/    if (lhs == rhs)
/* 635*/      return this; 
/* 637*/    if (lhs == null || rhs == null) {
/* 638*/      setEquals(false);
/* 639*/      return this;
/*   0*/    } 
/* 641*/    if (lhs.length != rhs.length) {
/* 642*/      setEquals(false);
/* 643*/      return this;
/*   0*/    } 
/* 645*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 646*/      append(lhs[i], rhs[i]); 
/* 648*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(byte[] lhs, byte[] rhs) {
/* 662*/    if (!this.isEquals)
/* 663*/      return this; 
/* 665*/    if (lhs == rhs)
/* 666*/      return this; 
/* 668*/    if (lhs == null || rhs == null) {
/* 669*/      setEquals(false);
/* 670*/      return this;
/*   0*/    } 
/* 672*/    if (lhs.length != rhs.length) {
/* 673*/      setEquals(false);
/* 674*/      return this;
/*   0*/    } 
/* 676*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 677*/      append(lhs[i], rhs[i]); 
/* 679*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(double[] lhs, double[] rhs) {
/* 693*/    if (!this.isEquals)
/* 694*/      return this; 
/* 696*/    if (lhs == rhs)
/* 697*/      return this; 
/* 699*/    if (lhs == null || rhs == null) {
/* 700*/      setEquals(false);
/* 701*/      return this;
/*   0*/    } 
/* 703*/    if (lhs.length != rhs.length) {
/* 704*/      setEquals(false);
/* 705*/      return this;
/*   0*/    } 
/* 707*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 708*/      append(lhs[i], rhs[i]); 
/* 710*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(float[] lhs, float[] rhs) {
/* 724*/    if (!this.isEquals)
/* 725*/      return this; 
/* 727*/    if (lhs == rhs)
/* 728*/      return this; 
/* 730*/    if (lhs == null || rhs == null) {
/* 731*/      setEquals(false);
/* 732*/      return this;
/*   0*/    } 
/* 734*/    if (lhs.length != rhs.length) {
/* 735*/      setEquals(false);
/* 736*/      return this;
/*   0*/    } 
/* 738*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 739*/      append(lhs[i], rhs[i]); 
/* 741*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {
/* 755*/    if (!this.isEquals)
/* 756*/      return this; 
/* 758*/    if (lhs == rhs)
/* 759*/      return this; 
/* 761*/    if (lhs == null || rhs == null) {
/* 762*/      setEquals(false);
/* 763*/      return this;
/*   0*/    } 
/* 765*/    if (lhs.length != rhs.length) {
/* 766*/      setEquals(false);
/* 767*/      return this;
/*   0*/    } 
/* 769*/    for (int i = 0; i < lhs.length && this.isEquals; i++)
/* 770*/      append(lhs[i], rhs[i]); 
/* 772*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquals() {
/* 782*/    return this.isEquals;
/*   0*/  }
/*   0*/  
/*   0*/  protected void setEquals(boolean isEquals) {
/* 792*/    this.isEquals = isEquals;
/*   0*/  }
/*   0*/}
