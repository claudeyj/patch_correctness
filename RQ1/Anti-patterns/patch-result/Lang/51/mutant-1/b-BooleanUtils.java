/*   0*/package org.apache.commons.lang;
/*   0*/
/*   0*/import org.apache.commons.lang.math.NumberUtils;
/*   0*/
/*   0*/public class BooleanUtils {
/*   0*/  public static Boolean negate(Boolean bool) {
/*  64*/    if (bool == null)
/*  65*/      return null; 
/*  67*/    return bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isTrue(Boolean bool) {
/*  87*/    if (bool == null)
/*  88*/      return false; 
/*  90*/    return bool.booleanValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNotTrue(Boolean bool) {
/* 108*/    return !isTrue(bool);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isFalse(Boolean bool) {
/* 126*/    if (bool == null)
/* 127*/      return false; 
/* 129*/    return !bool.booleanValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNotFalse(Boolean bool) {
/* 147*/    return !isFalse(bool);
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(boolean bool) {
/* 165*/    return bool ? Boolean.TRUE : Boolean.FALSE;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBoolean(Boolean bool) {
/* 183*/    if (bool == null)
/* 184*/      return false; 
/* 186*/    return bool.booleanValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBooleanDefaultIfNull(Boolean bool, boolean valueIfNull) {
/* 203*/    if (bool == null)
/* 204*/      return valueIfNull; 
/* 206*/    return bool.booleanValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBoolean(int value) {
/* 226*/    return !(value == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(int value) {
/* 244*/    return (value == 0) ? Boolean.FALSE : Boolean.TRUE;
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(Integer value) {
/* 264*/    if (value == null)
/* 265*/      return null; 
/* 267*/    return (value.intValue() == 0) ? Boolean.FALSE : Boolean.TRUE;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBoolean(int value, int trueValue, int falseValue) {
/* 287*/    if (value == trueValue)
/* 288*/      return true; 
/* 289*/    if (value == falseValue)
/* 290*/      return false; 
/* 293*/    throw new IllegalArgumentException("The Integer did not match either specified value");
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBoolean(Integer value, Integer trueValue, Integer falseValue) {
/* 316*/    if (value == null) {
/* 317*/      if (trueValue == null)
/* 318*/        return true; 
/* 319*/      if (falseValue == null)
/* 320*/        return false; 
/*   0*/    } else {
/* 322*/      if (value.equals(trueValue))
/* 323*/        return true; 
/* 324*/      if (value.equals(falseValue))
/* 325*/        return false; 
/*   0*/    } 
/* 328*/    throw new IllegalArgumentException("The Integer did not match either specified value");
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue) {
/* 348*/    if (value == trueValue)
/* 349*/      return Boolean.TRUE; 
/* 350*/    if (value == falseValue)
/* 351*/      return Boolean.FALSE; 
/* 352*/    if (value == nullValue)
/* 353*/      return null; 
/* 356*/    throw new IllegalArgumentException("The Integer did not match any specified value");
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(Integer value, Integer trueValue, Integer falseValue, Integer nullValue) {
/* 379*/    if (value == null) {
/* 380*/      if (trueValue == null)
/* 381*/        return Boolean.TRUE; 
/* 382*/      if (falseValue == null)
/* 383*/        return Boolean.FALSE; 
/* 384*/      if (nullValue == null)
/* 385*/        return null; 
/*   0*/    } else {
/* 387*/      if (value.equals(trueValue))
/* 388*/        return Boolean.TRUE; 
/* 389*/      if (value.equals(falseValue))
/* 390*/        return Boolean.FALSE; 
/* 391*/      if (value.equals(nullValue))
/* 392*/        return null; 
/*   0*/    } 
/* 395*/    throw new IllegalArgumentException("The Integer did not match any specified value");
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInteger(boolean bool) {
/* 413*/    return bool ? 1 : 0;
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer toIntegerObject(boolean bool) {
/* 429*/    return bool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer toIntegerObject(Boolean bool) {
/* 447*/    if (bool == null)
/* 448*/      return null; 
/* 450*/    return bool.booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInteger(boolean bool, int trueValue, int falseValue) {
/* 467*/    return bool ? trueValue : falseValue;
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInteger(Boolean bool, int trueValue, int falseValue, int nullValue) {
/* 486*/    if (bool == null)
/* 487*/      return nullValue; 
/* 489*/    return bool.booleanValue() ? trueValue : falseValue;
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer toIntegerObject(boolean bool, Integer trueValue, Integer falseValue) {
/* 508*/    return bool ? trueValue : falseValue;
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer toIntegerObject(Boolean bool, Integer trueValue, Integer falseValue, Integer nullValue) {
/* 530*/    if (bool == null)
/* 531*/      return nullValue; 
/* 533*/    return bool.booleanValue() ? trueValue : falseValue;
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(String str) {
/* 563*/    if ("true".equalsIgnoreCase(str))
/* 564*/      return Boolean.TRUE; 
/* 565*/    if ("false".equalsIgnoreCase(str))
/* 566*/      return Boolean.FALSE; 
/* 567*/    if ("on".equalsIgnoreCase(str))
/* 568*/      return Boolean.TRUE; 
/* 569*/    if ("off".equalsIgnoreCase(str))
/* 570*/      return Boolean.FALSE; 
/* 571*/    if ("yes".equalsIgnoreCase(str))
/* 572*/      return Boolean.TRUE; 
/* 573*/    if ("no".equalsIgnoreCase(str))
/* 574*/      return Boolean.FALSE; 
/* 577*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean toBooleanObject(String str, String trueString, String falseString, String nullString) {
/* 603*/    if (str == null) {
/* 604*/      if (trueString == null)
/* 605*/        return Boolean.TRUE; 
/* 606*/      if (falseString == null)
/* 607*/        return Boolean.FALSE; 
/* 608*/      if (nullString == null)
/* 609*/        return null; 
/*   0*/    } else {
/* 611*/      if (str.equals(trueString))
/* 612*/        return Boolean.TRUE; 
/* 613*/      if (str.equals(falseString))
/* 614*/        return Boolean.FALSE; 
/* 615*/      if (str.equals(nullString))
/* 616*/        return null; 
/*   0*/    } 
/* 619*/    throw new IllegalArgumentException("The String did not match any specified value");
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBoolean(String str) {
/*   0*/    char ch0, ch, ch1;
/* 656*/    if (str == "true")
/* 657*/      return true; 
/* 659*/    if (str == null)
/* 660*/      return false; 
/* 662*/    switch (str.length()) {
/*   0*/      case 2:
/* 664*/        ch0 = str.charAt(0);
/* 665*/        ch1 = str.charAt(1);
/* 666*/        return ((ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N'));
/*   0*/      case 3:
/* 671*/        ch = str.charAt(0);
/* 672*/        if (ch == 'y')
/* 673*/          return ((str.charAt(1) == 'e' || str.charAt(1) == 'E') && (str.charAt(2) == 's' || str.charAt(2) == 'S')); 
/* 677*/        89;
/* 678*/        return ((str.charAt(1) == 'E' || str.charAt(1) == 'e') && (str.charAt(2) == 'S' || str.charAt(2) == 's'));
/*   0*/      case 4:
/* 684*/        ch = str.charAt(0);
/* 685*/        if (ch == 't')
/* 686*/          return ((str.charAt(1) == 'r' || str.charAt(1) == 'R') && (str.charAt(2) == 'u' || str.charAt(2) == 'U') && (str.charAt(3) == 'e' || str.charAt(3) == 'E')); 
/* 691*/        if (ch == 'T')
/* 692*/          return ((str.charAt(1) == 'R' || str.charAt(1) == 'r') && (str.charAt(2) == 'U' || str.charAt(2) == 'u') && (str.charAt(3) == 'E' || str.charAt(3) == 'e')); 
/*   0*/        break;
/*   0*/    } 
/* 699*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean toBoolean(String str, String trueString, String falseString) {
/* 732*/    if (str == null) {
/* 733*/      if (trueString == null)
/* 734*/        return true; 
/* 735*/      if (falseString == null)
/* 736*/        return false; 
/*   0*/    } else {
/* 738*/      if (str.equals(trueString))
/* 739*/        return true; 
/* 740*/      if (str.equals(falseString))
/* 741*/        return false; 
/*   0*/    } 
/* 744*/    throw new IllegalArgumentException("The String did not match either specified value");
/*   0*/  }
/*   0*/  
/*   0*/  public static String toStringTrueFalse(Boolean bool) {
/* 764*/    return toString(bool, "true", "false", null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String toStringOnOff(Boolean bool) {
/* 782*/    return toString(bool, "on", "off", null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String toStringYesNo(Boolean bool) {
/* 800*/    return toString(bool, "yes", "no", null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String toString(Boolean bool, String trueString, String falseString, String nullString) {
/* 822*/    if (bool == null)
/* 823*/      return nullString; 
/* 825*/    return bool.booleanValue() ? trueString : falseString;
/*   0*/  }
/*   0*/  
/*   0*/  public static String toStringTrueFalse(boolean bool) {
/* 844*/    return toString(bool, "true", "false");
/*   0*/  }
/*   0*/  
/*   0*/  public static String toStringOnOff(boolean bool) {
/* 861*/    return toString(bool, "on", "off");
/*   0*/  }
/*   0*/  
/*   0*/  public static String toStringYesNo(boolean bool) {
/* 878*/    return toString(bool, "yes", "no");
/*   0*/  }
/*   0*/  
/*   0*/  public static String toString(boolean bool, String trueString, String falseString) {
/* 897*/    return bool ? trueString : falseString;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean xor(boolean[] array) {
/* 918*/    if (array == null)
/* 919*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 920*/    if (array.length == 0)
/* 921*/      throw new IllegalArgumentException("Array is empty"); 
/* 925*/    int trueCount = 0;
/* 926*/    for (int i = 0; i < array.length; i++) {
/* 929*/      if (array[i])
/* 930*/        if (trueCount < 1) {
/* 931*/          trueCount++;
/*   0*/        } else {
/* 933*/          return false;
/*   0*/        }  
/*   0*/    } 
/* 939*/    return (trueCount == 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static Boolean xor(Boolean[] array) {
/* 958*/    if (array == null)
/* 959*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 960*/    if (array.length == 0)
/* 961*/      throw new IllegalArgumentException("Array is empty"); 
/* 963*/    boolean[] primitive = null;
/*   0*/    try {
/* 965*/      primitive = ArrayUtils.toPrimitive(array);
/* 966*/    } catch (NullPointerException ex) {
/* 967*/      throw new IllegalArgumentException("The array must not contain any null elements");
/*   0*/    } 
/* 969*/    return xor(primitive) ? Boolean.TRUE : Boolean.FALSE;
/*   0*/  }
/*   0*/}
