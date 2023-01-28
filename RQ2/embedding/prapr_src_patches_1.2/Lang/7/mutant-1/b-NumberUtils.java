/*   0*/package org.apache.commons.lang3.math;
/*   0*/
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/import org.apache.commons.lang3.StringUtils;
/*   0*/
/*   0*/public class NumberUtils {
/*  33*/  public static final Long LONG_ZERO = 0L;
/*   0*/  
/*  35*/  public static final Long LONG_ONE = 1L;
/*   0*/  
/*  37*/  public static final Long LONG_MINUS_ONE = -1L;
/*   0*/  
/*  39*/  public static final Integer INTEGER_ZERO = 0;
/*   0*/  
/*  41*/  public static final Integer INTEGER_ONE = 1;
/*   0*/  
/*  43*/  public static final Integer INTEGER_MINUS_ONE = -1;
/*   0*/  
/*  45*/  public static final Short SHORT_ZERO = (short)0;
/*   0*/  
/*  47*/  public static final Short SHORT_ONE = (short)1;
/*   0*/  
/*  49*/  public static final Short SHORT_MINUS_ONE = (short)-1;
/*   0*/  
/*  51*/  public static final Byte BYTE_ZERO = (byte)0;
/*   0*/  
/*  53*/  public static final Byte BYTE_ONE = (byte)1;
/*   0*/  
/*  55*/  public static final Byte BYTE_MINUS_ONE = (byte)-1;
/*   0*/  
/*  57*/  public static final Double DOUBLE_ZERO = 0.0D;
/*   0*/  
/*  59*/  public static final Double DOUBLE_ONE = 1.0D;
/*   0*/  
/*  61*/  public static final Double DOUBLE_MINUS_ONE = -1.0D;
/*   0*/  
/*  63*/  public static final Float FLOAT_ZERO = 0.0F;
/*   0*/  
/*  65*/  public static final Float FLOAT_ONE = 1.0F;
/*   0*/  
/*  67*/  public static final Float FLOAT_MINUS_ONE = -1.0F;
/*   0*/  
/*   0*/  public static int toInt(String str) {
/*  99*/    return toInt(str, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInt(String str, int defaultValue) {
/* 120*/    if (str == null)
/* 121*/      return defaultValue; 
/*   0*/    try {
/* 124*/      return Integer.parseInt(str);
/* 125*/    } catch (NumberFormatException nfe) {
/* 126*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static long toLong(String str) {
/* 148*/    return toLong(str, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  public static long toLong(String str, long defaultValue) {
/* 169*/    if (str == null)
/* 170*/      return defaultValue; 
/*   0*/    try {
/* 173*/      return Long.parseLong(str);
/* 174*/    } catch (NumberFormatException nfe) {
/* 175*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static float toFloat(String str) {
/* 198*/    return toFloat(str, 0.0F);
/*   0*/  }
/*   0*/  
/*   0*/  public static float toFloat(String str, float defaultValue) {
/* 221*/    if (str == null)
/* 222*/      return defaultValue; 
/*   0*/    try {
/* 225*/      return Float.parseFloat(str);
/* 226*/    } catch (NumberFormatException nfe) {
/* 227*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDouble(String str) {
/* 250*/    return toDouble(str, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDouble(String str, double defaultValue) {
/* 273*/    if (str == null)
/* 274*/      return defaultValue; 
/*   0*/    try {
/* 277*/      return Double.parseDouble(str);
/* 278*/    } catch (NumberFormatException nfe) {
/* 279*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static byte toByte(String str) {
/* 302*/    return toByte(str, (byte)0);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte toByte(String str, byte defaultValue) {
/* 323*/    if (str == null)
/* 324*/      return defaultValue; 
/*   0*/    try {
/* 327*/      return Byte.parseByte(str);
/* 328*/    } catch (NumberFormatException nfe) {
/* 329*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static short toShort(String str) {
/* 351*/    return toShort(str, (short)0);
/*   0*/  }
/*   0*/  
/*   0*/  public static short toShort(String str, short defaultValue) {
/* 372*/    if (str == null)
/* 373*/      return defaultValue; 
/*   0*/    try {
/* 376*/      return Short.parseShort(str);
/* 377*/    } catch (NumberFormatException nfe) {
/* 378*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Number createNumber(String str) throws NumberFormatException {
/*   0*/    String mant, dec, exp;
/* 446*/    if (str == null)
/* 447*/      return null; 
/* 449*/    if (StringUtils.isBlank(str))
/* 450*/      throw new NumberFormatException("A blank string is not a valid number"); 
/* 452*/    if (str.equalsIgnoreCase("--"))
/* 453*/      return null; 
/* 455*/    if (str.startsWith("0x") || str.startsWith("-0x") || str.startsWith("0X") || str.startsWith("-0X")) {
/* 456*/      int hexDigits = str.length() - 2;
/* 457*/      if (str.startsWith("-"))
/* 458*/        hexDigits--; 
/* 460*/      if (hexDigits > 8)
/* 461*/        return createLong(str); 
/* 463*/      return createInteger(str);
/*   0*/    } 
/* 465*/    char lastChar = str.charAt(str.length() - 1);
/* 469*/    int decPos = str.indexOf('.');
/* 470*/    int expPos = str.indexOf('e') + str.indexOf('E') + 1;
/* 472*/    if (decPos > -1) {
/* 474*/      if (expPos > -1) {
/* 475*/        if (expPos < decPos || expPos > str.length())
/* 476*/          throw new NumberFormatException(str + " is not a valid number."); 
/* 478*/        dec = str.substring(decPos + 1, expPos);
/*   0*/      } else {
/* 480*/        dec = str.substring(decPos + 1);
/*   0*/      } 
/* 482*/      mant = str.substring(0, decPos);
/*   0*/    } else {
/* 484*/      if (expPos > -1) {
/* 485*/        if (expPos > str.length())
/* 486*/          throw new NumberFormatException(str + " is not a valid number."); 
/* 488*/        mant = str.substring(0, expPos);
/*   0*/      } else {
/* 490*/        mant = str;
/*   0*/      } 
/* 492*/      dec = null;
/*   0*/    } 
/* 494*/    if (!Character.isDigit(lastChar) && lastChar != '.') {
/* 495*/      if (expPos > -1 && expPos < str.length() - 1) {
/* 496*/        exp = str.substring(expPos + 1, str.length() - 1);
/*   0*/      } else {
/* 498*/        exp = null;
/*   0*/      } 
/* 501*/      String numeric = str.substring(0, str.length() - 1);
/* 502*/      boolean bool = (isAllZeros(mant) && isAllZeros(exp));
/* 503*/      switch (lastChar) {
/*   0*/        case 'L':
/*   0*/        case 'l':
/* 506*/          if (dec == null && exp == null && ((numeric.charAt(0) == '-' && isDigits(numeric.substring(1))) || isDigits(numeric)))
/*   0*/            try {
/* 510*/              return createLong(numeric);
/* 511*/            } catch (NumberFormatException numberFormatException) {
/* 514*/              return createBigInteger(numeric);
/*   0*/            }  
/* 517*/          throw new NumberFormatException(str + " is not a valid number.");
/*   0*/        case 'F':
/*   0*/        case 'f':
/*   0*/          try {
/* 521*/            Float f = createFloat(numeric);
/* 522*/            if (!f.isInfinite() && (f != 0.0F || bool))
/* 525*/              return f; 
/* 528*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/        case 'D':
/*   0*/        case 'd':
/*   0*/          try {
/* 535*/            Double d = createDouble(numeric);
/* 536*/            if (!d.isInfinite() && (d.floatValue() != 0.0D || bool))
/* 537*/              return d; 
/* 539*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/          try {
/* 543*/            return createBigDecimal(numeric);
/* 544*/          } catch (NumberFormatException numberFormatException) {
/*   0*/            break;
/*   0*/          } 
/*   0*/      } 
/* 549*/      throw new NumberFormatException(str + " is not a valid number.");
/*   0*/    } 
/* 555*/    if (expPos > -1 && expPos < str.length() - 1) {
/* 556*/      exp = str.substring(expPos + 1, str.length());
/*   0*/    } else {
/* 558*/      exp = null;
/*   0*/    } 
/* 560*/    if (dec == null && exp == null)
/*   0*/      try {
/* 563*/        return createInteger(str);
/* 564*/      } catch (NumberFormatException numberFormatException) {
/*   0*/        try {
/* 568*/          return createLong(str);
/* 569*/        } catch (NumberFormatException numberFormatException1) {
/* 572*/          return createBigInteger(str);
/*   0*/        } 
/*   0*/      }  
/* 576*/    boolean allZeros = (isAllZeros(mant) && isAllZeros(exp));
/*   0*/    try {
/* 578*/      Float f = createFloat(str);
/* 579*/      if (!f.isInfinite() && (f != 0.0F || allZeros))
/* 580*/        return f; 
/* 582*/    } catch (NumberFormatException numberFormatException) {}
/*   0*/    try {
/* 586*/      Double d = createDouble(str);
/* 587*/      if (!d.isInfinite() && (d != 0.0D || allZeros))
/* 588*/        return d; 
/* 590*/    } catch (NumberFormatException numberFormatException) {}
/* 594*/    return createBigDecimal(str);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isAllZeros(String str) {
/* 609*/    if (str == null)
/* 610*/      return true; 
/* 612*/    for (int i = str.length() - 1; i >= 0; i--) {
/* 613*/      if (str.charAt(i) != '0')
/* 614*/        return false; 
/*   0*/    } 
/* 617*/    return (str.length() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Float createFloat(String str) {
/* 631*/    if (str == null)
/* 632*/      return null; 
/* 634*/    return Float.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Double createDouble(String str) {
/* 647*/    if (str == null)
/* 648*/      return null; 
/* 650*/    return Double.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer createInteger(String str) {
/* 664*/    if (str == null)
/* 665*/      return null; 
/* 668*/    return Integer.decode(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Long createLong(String str) {
/* 682*/    if (str == null)
/* 683*/      return null; 
/* 685*/    return Long.decode(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger createBigInteger(String str) {
/* 698*/    if (str == null)
/* 699*/      return null; 
/* 701*/    return new BigInteger(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static BigDecimal createBigDecimal(String str) {
/* 714*/    if (str == null)
/* 715*/      return null; 
/* 718*/    if (StringUtils.isBlank(str))
/* 719*/      throw new NumberFormatException("A blank string is not a valid number"); 
/* 725*/    return new BigDecimal(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long[] array) {
/* 740*/    if (array == null)
/* 741*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 742*/    if (array.length == 0)
/* 743*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 747*/    long min = array[0];
/* 748*/    for (int i = 1; i < array.length; i++) {
/* 749*/      if (array[i] < min)
/* 750*/        min = array[i]; 
/*   0*/    } 
/* 754*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int[] array) {
/* 767*/    if (array == null)
/* 768*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 769*/    if (array.length == 0)
/* 770*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 774*/    int min = array[0];
/* 775*/    for (int j = 1; j < array.length; j++) {
/* 776*/      if (array[j] < min)
/* 777*/        min = array[j]; 
/*   0*/    } 
/* 781*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static short min(short[] array) {
/* 794*/    if (array == null)
/* 795*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 796*/    if (array.length == 0)
/* 797*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 801*/    short min = array[0];
/* 802*/    for (int i = 1; i < array.length; i++) {
/* 803*/      if (array[i] < min)
/* 804*/        min = array[i]; 
/*   0*/    } 
/* 808*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte min(byte[] array) {
/* 821*/    if (array == null)
/* 822*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 823*/    if (array.length == 0)
/* 824*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 828*/    byte min = array[0];
/* 829*/    for (int i = 1; i < array.length; i++) {
/* 830*/      if (array[i] < min)
/* 831*/        min = array[i]; 
/*   0*/    } 
/* 835*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double[] array) {
/* 849*/    if (array == null)
/* 850*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 851*/    if (array.length == 0)
/* 852*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 856*/    double min = array[0];
/* 857*/    for (int i = 1; i < array.length; i++) {
/* 858*/      if (Double.isNaN(array[i]))
/* 859*/        return Double.NaN; 
/* 861*/      if (array[i] < min)
/* 862*/        min = array[i]; 
/*   0*/    } 
/* 866*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float[] array) {
/* 880*/    if (array == null)
/* 881*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 882*/    if (array.length == 0)
/* 883*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 887*/    float min = array[0];
/* 888*/    for (int i = 1; i < array.length; i++) {
/* 889*/      if (Float.isNaN(array[i]))
/* 890*/        return Float.NaN; 
/* 892*/      if (array[i] < min)
/* 893*/        min = array[i]; 
/*   0*/    } 
/* 897*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long[] array) {
/* 912*/    if (array == null)
/* 913*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 914*/    if (array.length == 0)
/* 915*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 919*/    long max = array[0];
/* 920*/    for (int j = 1; j < array.length; j++) {
/* 921*/      if (array[j] > max)
/* 922*/        max = array[j]; 
/*   0*/    } 
/* 926*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int[] array) {
/* 939*/    if (array == null)
/* 940*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 941*/    if (array.length == 0)
/* 942*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 946*/    int max = array[0];
/* 947*/    for (int j = 1; j < array.length; j++) {
/* 948*/      if (array[j] > max)
/* 949*/        max = array[j]; 
/*   0*/    } 
/* 953*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static short max(short[] array) {
/* 966*/    if (array == null)
/* 967*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 968*/    if (array.length == 0)
/* 969*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 973*/    short max = array[0];
/* 974*/    for (int i = 1; i < array.length; i++) {
/* 975*/      if (array[i] > max)
/* 976*/        max = array[i]; 
/*   0*/    } 
/* 980*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte max(byte[] array) {
/* 993*/    if (array == null)
/* 994*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 995*/    if (array.length == 0)
/* 996*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1000*/    byte max = array[0];
/*1001*/    for (int i = 1; i < array.length; i++) {
/*1002*/      if (array[i] > max)
/*1003*/        max = array[i]; 
/*   0*/    } 
/*1007*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double[] array) {
/*1021*/    if (array == null)
/*1022*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1023*/    if (array.length == 0)
/*1024*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1028*/    double max = array[0];
/*1029*/    for (int j = 1; j < array.length; j++) {
/*1030*/      if (Double.isNaN(array[j]))
/*1031*/        return Double.NaN; 
/*1033*/      if (array[j] > max)
/*1034*/        max = array[j]; 
/*   0*/    } 
/*1038*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float[] array) {
/*1052*/    if (array == null)
/*1053*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1054*/    if (array.length == 0)
/*1055*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1059*/    float max = array[0];
/*1060*/    for (int j = 1; j < array.length; j++) {
/*1061*/      if (Float.isNaN(array[j]))
/*1062*/        return Float.NaN; 
/*1064*/      if (array[j] > max)
/*1065*/        max = array[j]; 
/*   0*/    } 
/*1069*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long a, long b, long c) {
/*1083*/    if (b < a)
/*1084*/      a = b; 
/*1086*/    if (c < a)
/*1087*/      a = c; 
/*1089*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int a, int b, int c) {
/*1101*/    if (b < a)
/*1102*/      a = b; 
/*1104*/    if (c < a)
/*1105*/      a = c; 
/*1107*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static short min(short a, short b, short c) {
/*1119*/    if (b < a)
/*1120*/      a = b; 
/*1122*/    if (c < a)
/*1123*/      a = c; 
/*1125*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte min(byte a, byte b, byte c) {
/*1137*/    if (b < a)
/*1138*/      a = b; 
/*1140*/    if (c < a)
/*1141*/      a = c; 
/*1143*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double a, double b, double c) {
/*1159*/    return Math.min(Math.min(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float a, float b, float c) {
/*1175*/    return Math.min(Math.min(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long a, long b, long c) {
/*1189*/    if (b > a)
/*1190*/      a = b; 
/*1192*/    if (c > a)
/*1193*/      a = c; 
/*1195*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int a, int b, int c) {
/*1207*/    if (b > a)
/*1208*/      a = b; 
/*1210*/    if (c > a)
/*1211*/      a = c; 
/*1213*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static short max(short a, short b, short c) {
/*1225*/    if (b > a)
/*1226*/      a = b; 
/*1228*/    if (c > a)
/*1229*/      a = c; 
/*1231*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte max(byte a, byte b, byte c) {
/*1243*/    if (b > a)
/*1244*/      a = b; 
/*1246*/    if (c > a)
/*1247*/      a = c; 
/*1249*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double a, double b, double c) {
/*1265*/    return Math.max(Math.max(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float a, float b, float c) {
/*1281*/    return Math.max(Math.max(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isDigits(String str) {
/*1296*/    if (StringUtils.isEmpty(str))
/*1297*/      return false; 
/*1299*/    for (int i = 0; i < str.length(); i++) {
/*1300*/      if (!Character.isDigit(str.charAt(i)))
/*1301*/        return false; 
/*   0*/    } 
/*1304*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumber(String str) {
/*1321*/    if (StringUtils.isEmpty(str))
/*1322*/      return false; 
/*1324*/    char[] chars = str.toCharArray();
/*1325*/    int sz = chars.length;
/*   0*/    boolean hasExp = false;
/*   0*/    boolean hasDecPoint = false;
/*   0*/    boolean allowSigns = false;
/*   0*/    boolean foundDigit = false;
/*1331*/    int start = (chars[0] == '-') ? 1 : 0;
/*1332*/    if (sz > start + 1 && chars[start] == '0' && chars[start + 1] == 'x') {
/*1333*/      int j = start + 2;
/*1334*/      if (j == sz)
/*1335*/        return false; 
/*1338*/      for (; j < chars.length; j++) {
/*1339*/        if ((chars[j] < '0' || chars[j] > '9') && (chars[j] < 'a' || chars[j] > 'f') && (chars[j] < 'A' || chars[j] > 'F'))
/*1342*/          return false; 
/*   0*/      } 
/*1345*/      return true;
/*   0*/    } 
/*1347*/    sz--;
/*1349*/    int i = start;
/*1352*/    while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
/*1353*/      if (chars[i] >= '0' && chars[i] <= '9') {
/*1354*/        foundDigit = true;
/*1355*/        allowSigns = false;
/*1357*/      } else if (chars[i] == '.') {
/*1358*/        if (hasDecPoint || hasExp)
/*1360*/          return false; 
/*1362*/        hasDecPoint = true;
/*1363*/      } else if (chars[i] == 'e' || chars[i] == 'E') {
/*1365*/        if (hasExp)
/*1367*/          return false; 
/*1369*/        if (!foundDigit)
/*1370*/          return false; 
/*1372*/        hasExp = true;
/*1373*/        allowSigns = true;
/*1374*/      } else if (chars[i] == '+' || chars[i] == '-') {
/*1375*/        if (!allowSigns)
/*1376*/          return false; 
/*1378*/        allowSigns = false;
/*1379*/        foundDigit = false;
/*   0*/      } else {
/*1381*/        return false;
/*   0*/      } 
/*1383*/      i++;
/*   0*/    } 
/*1385*/    if (i < chars.length) {
/*1386*/      if (chars[i] >= '0' && chars[i] <= '9')
/*1388*/        return true; 
/*1390*/      if (chars[i] == 'e' || chars[i] == 'E')
/*1392*/        return false; 
/*1394*/      if (chars[i] == '.') {
/*1395*/        if (hasDecPoint || hasExp)
/*1397*/          return false; 
/*1400*/        return foundDigit;
/*   0*/      } 
/*1402*/      if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F'))
/*1407*/        return foundDigit; 
/*1409*/      if (chars[i] == 'l' || chars[i] == 'L')
/*1412*/        return (foundDigit && !hasExp && !hasDecPoint); 
/*1415*/      return false;
/*   0*/    } 
/*1419*/    return (!allowSigns && foundDigit);
/*   0*/  }
/*   0*/}
