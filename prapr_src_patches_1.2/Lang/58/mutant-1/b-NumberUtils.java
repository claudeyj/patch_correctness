/*   0*/package org.apache.commons.lang.math;
/*   0*/
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/import org.apache.commons.lang.StringUtils;
/*   0*/
/*   0*/public class NumberUtils {
/*  41*/  public static final Long LONG_ZERO = new Long(0L);
/*   0*/  
/*  43*/  public static final Long LONG_ONE = new Long(1L);
/*   0*/  
/*  45*/  public static final Long LONG_MINUS_ONE = new Long(-1L);
/*   0*/  
/*  47*/  public static final Integer INTEGER_ZERO = new Integer(0);
/*   0*/  
/*  49*/  public static final Integer INTEGER_ONE = new Integer(1);
/*   0*/  
/*  51*/  public static final Integer INTEGER_MINUS_ONE = new Integer(-1);
/*   0*/  
/*  53*/  public static final Short SHORT_ZERO = new Short((short)0);
/*   0*/  
/*  55*/  public static final Short SHORT_ONE = new Short((short)1);
/*   0*/  
/*  57*/  public static final Short SHORT_MINUS_ONE = new Short((short)-1);
/*   0*/  
/*  59*/  public static final Byte BYTE_ZERO = new Byte((byte)0);
/*   0*/  
/*  61*/  public static final Byte BYTE_ONE = new Byte((byte)1);
/*   0*/  
/*  63*/  public static final Byte BYTE_MINUS_ONE = new Byte((byte)-1);
/*   0*/  
/*  65*/  public static final Double DOUBLE_ZERO = new Double(0.0D);
/*   0*/  
/*  67*/  public static final Double DOUBLE_ONE = new Double(1.0D);
/*   0*/  
/*  69*/  public static final Double DOUBLE_MINUS_ONE = new Double(-1.0D);
/*   0*/  
/*  71*/  public static final Float FLOAT_ZERO = new Float(0.0F);
/*   0*/  
/*  73*/  public static final Float FLOAT_ONE = new Float(1.0F);
/*   0*/  
/*  75*/  public static final Float FLOAT_MINUS_ONE = new Float(-1.0F);
/*   0*/  
/*   0*/  public static int stringToInt(String str) {
/* 108*/    return toInt(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInt(String str) {
/* 129*/    return toInt(str, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static int stringToInt(String str, int defaultValue) {
/* 151*/    return toInt(str, defaultValue);
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInt(String str, int defaultValue) {
/* 172*/    if (str == null)
/* 173*/      return defaultValue; 
/*   0*/    try {
/* 176*/      return Integer.parseInt(str);
/* 177*/    } catch (NumberFormatException nfe) {
/* 178*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static long toLong(String str) {
/* 200*/    return toLong(str, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  public static long toLong(String str, long defaultValue) {
/* 221*/    if (str == null)
/* 222*/      return defaultValue; 
/*   0*/    try {
/* 225*/      return Long.parseLong(str);
/* 226*/    } catch (NumberFormatException nfe) {
/* 227*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static float toFloat(String str) {
/* 250*/    return toFloat(str, 0.0F);
/*   0*/  }
/*   0*/  
/*   0*/  public static float toFloat(String str, float defaultValue) {
/* 273*/    if (str == null)
/* 274*/      return defaultValue; 
/*   0*/    try {
/* 277*/      return Float.parseFloat(str);
/* 278*/    } catch (NumberFormatException nfe) {
/* 279*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDouble(String str) {
/* 302*/    return toDouble(str, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDouble(String str, double defaultValue) {
/* 325*/    if (str == null)
/* 326*/      return defaultValue; 
/*   0*/    try {
/* 329*/      return Double.parseDouble(str);
/* 330*/    } catch (NumberFormatException nfe) {
/* 331*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Number createNumber(String str) throws NumberFormatException {
/*   0*/    String mant, dec, exp;
/* 398*/    if (str == null)
/* 399*/      return null; 
/* 401*/    if (StringUtils.isBlank(str))
/* 402*/      throw new NumberFormatException("A blank string is not a valid number"); 
/* 404*/    if (str.startsWith("--"))
/* 409*/      return null; 
/* 411*/    if (str.startsWith("0x") || str.startsWith("-0x"))
/* 412*/      return createInteger(str); 
/* 414*/    char lastChar = str.charAt(str.length() - 1);
/* 418*/    int decPos = str.indexOf('.');
/* 419*/    int expPos = str.indexOf('e') + str.indexOf('E') + 1;
/* 421*/    if (decPos > -1) {
/* 423*/      if (expPos > -1) {
/* 424*/        if (expPos < decPos)
/* 425*/          throw new NumberFormatException(str + " is not a valid number."); 
/* 427*/        dec = str.substring(decPos + 1, expPos);
/*   0*/      } else {
/* 429*/        dec = str.substring(decPos + 1);
/*   0*/      } 
/* 431*/      mant = str.substring(0, decPos);
/*   0*/    } else {
/* 433*/      if (expPos > -1) {
/* 434*/        mant = str.substring(0, expPos);
/*   0*/      } else {
/* 436*/        mant = str;
/*   0*/      } 
/* 438*/      dec = null;
/*   0*/    } 
/* 440*/    if (!Character.isDigit(lastChar)) {
/* 441*/      if (expPos > -1 && expPos < str.length() - 1) {
/* 442*/        exp = str.substring(expPos + 1, str.length() - 1);
/*   0*/      } else {
/* 444*/        exp = null;
/*   0*/      } 
/* 447*/      String numeric = str.substring(0, str.length() - 1);
/* 448*/      boolean bool = (isAllZeros(mant) && isAllZeros(exp));
/* 449*/      switch (lastChar) {
/*   0*/        case 'L':
/*   0*/        case 'l':
/* 452*/          if (dec == null && exp == null) {
/* 452*/            isDigits(numeric.substring(1));
/* 452*/            if (numeric.charAt(0) == '-' || Character.isDigit(numeric.charAt(0)))
/*   0*/              try {
/* 457*/                return createLong(numeric);
/* 458*/              } catch (NumberFormatException numberFormatException) {
/* 461*/                return createBigInteger(numeric);
/*   0*/              }  
/*   0*/          } 
/* 464*/          throw new NumberFormatException(str + " is not a valid number.");
/*   0*/        case 'F':
/*   0*/        case 'f':
/*   0*/          try {
/* 468*/            Float f = createFloat(numeric);
/* 469*/            if (!f.isInfinite() && (f.floatValue() != 0.0F || bool))
/* 472*/              return f; 
/* 475*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/        case 'D':
/*   0*/        case 'd':
/*   0*/          try {
/* 482*/            Double d = createDouble(numeric);
/* 483*/            if (!d.isInfinite() && (d.floatValue() != 0.0D || bool))
/* 484*/              return d; 
/* 486*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/          try {
/* 490*/            return createBigDecimal(numeric);
/* 491*/          } catch (NumberFormatException numberFormatException) {
/*   0*/            break;
/*   0*/          } 
/*   0*/      } 
/* 496*/      throw new NumberFormatException(str + " is not a valid number.");
/*   0*/    } 
/* 502*/    if (expPos > -1 && expPos < str.length() - 1) {
/* 503*/      exp = str.substring(expPos + 1, str.length());
/*   0*/    } else {
/* 505*/      exp = null;
/*   0*/    } 
/* 507*/    if (dec == null && exp == null)
/*   0*/      try {
/* 510*/        return createInteger(str);
/* 511*/      } catch (NumberFormatException numberFormatException) {
/*   0*/        try {
/* 515*/          return createLong(str);
/* 516*/        } catch (NumberFormatException numberFormatException1) {
/* 519*/          return createBigInteger(str);
/*   0*/        } 
/*   0*/      }  
/* 523*/    boolean allZeros = (isAllZeros(mant) && isAllZeros(exp));
/*   0*/    try {
/* 525*/      Float f = createFloat(str);
/* 526*/      if (!f.isInfinite() && (f.floatValue() != 0.0F || allZeros))
/* 527*/        return f; 
/* 529*/    } catch (NumberFormatException numberFormatException) {}
/*   0*/    try {
/* 533*/      Double d = createDouble(str);
/* 534*/      if (!d.isInfinite() && (d.doubleValue() != 0.0D || allZeros))
/* 535*/        return d; 
/* 537*/    } catch (NumberFormatException numberFormatException) {}
/* 541*/    return createBigDecimal(str);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isAllZeros(String str) {
/* 556*/    if (str == null)
/* 557*/      return true; 
/* 559*/    for (int i = str.length() - 1; i >= 0; i--) {
/* 560*/      if (str.charAt(i) != '0')
/* 561*/        return false; 
/*   0*/    } 
/* 564*/    return (str.length() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Float createFloat(String str) {
/* 578*/    if (str == null)
/* 579*/      return null; 
/* 581*/    return Float.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Double createDouble(String str) {
/* 594*/    if (str == null)
/* 595*/      return null; 
/* 597*/    return Double.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer createInteger(String str) {
/* 611*/    if (str == null)
/* 612*/      return null; 
/* 615*/    return Integer.decode(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Long createLong(String str) {
/* 628*/    if (str == null)
/* 629*/      return null; 
/* 631*/    return Long.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger createBigInteger(String str) {
/* 644*/    if (str == null)
/* 645*/      return null; 
/* 647*/    return new BigInteger(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static BigDecimal createBigDecimal(String str) {
/* 660*/    if (str == null)
/* 661*/      return null; 
/* 664*/    if (StringUtils.isBlank(str))
/* 665*/      throw new NumberFormatException("A blank string is not a valid number"); 
/* 667*/    return new BigDecimal(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(byte[] array1, byte[] array2) {
/* 680*/    if (array1 == array2)
/* 681*/      return true; 
/* 683*/    if (array1 == null || array2 == null)
/* 684*/      return false; 
/* 686*/    if (array1.length != array2.length)
/* 687*/      return false; 
/* 690*/    for (int i = 0; i < array1.length; i++) {
/* 691*/      if (array1[i] != array2[i])
/* 692*/        return false; 
/*   0*/    } 
/* 696*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(short[] array1, short[] array2) {
/* 707*/    if (array1 == array2)
/* 708*/      return true; 
/* 710*/    if (array1 == null || array2 == null)
/* 711*/      return false; 
/* 713*/    if (array1.length != array2.length)
/* 714*/      return false; 
/* 717*/    for (int i = 0; i < array1.length; i++) {
/* 718*/      if (array1[i] != array2[i])
/* 719*/        return false; 
/*   0*/    } 
/* 723*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(int[] array1, int[] array2) {
/* 734*/    if (array1 == array2)
/* 735*/      return true; 
/* 737*/    if (array1 == null || array2 == null)
/* 738*/      return false; 
/* 740*/    if (array1.length != array2.length)
/* 741*/      return false; 
/* 744*/    for (int i = 0; i < array1.length; i++) {
/* 745*/      if (array1[i] != array2[i])
/* 746*/        return false; 
/*   0*/    } 
/* 750*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(long[] array1, long[] array2) {
/* 761*/    if (array1 == array2)
/* 762*/      return true; 
/* 764*/    if (array1 == null || array2 == null)
/* 765*/      return false; 
/* 767*/    if (array1.length != array2.length)
/* 768*/      return false; 
/* 771*/    for (int i = 0; i < array1.length; i++) {
/* 772*/      if (array1[i] != array2[i])
/* 773*/        return false; 
/*   0*/    } 
/* 777*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(float[] array1, float[] array2) {
/* 788*/    if (array1 == array2)
/* 789*/      return true; 
/* 791*/    if (array1 == null || array2 == null)
/* 792*/      return false; 
/* 794*/    if (array1.length != array2.length)
/* 795*/      return false; 
/* 798*/    for (int i = 0; i < array1.length; i++) {
/* 799*/      if (compare(array1[i], array2[i]) != 0)
/* 800*/        return false; 
/*   0*/    } 
/* 804*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(double[] array1, double[] array2) {
/* 815*/    if (array1 == array2)
/* 816*/      return true; 
/* 818*/    if (array1 == null || array2 == null)
/* 819*/      return false; 
/* 821*/    if (array1.length != array2.length)
/* 822*/      return false; 
/* 825*/    for (int i = 0; i < array1.length; i++) {
/* 826*/      if (compare(array1[i], array2[i]) != 0)
/* 827*/        return false; 
/*   0*/    } 
/* 831*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long[] array) {
/* 846*/    if (array == null)
/* 847*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 848*/    if (array.length == 0)
/* 849*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 853*/    long min = array[0];
/* 854*/    for (int i = 1; i < array.length; i++) {
/* 855*/      if (array[i] < min)
/* 856*/        min = array[i]; 
/*   0*/    } 
/* 860*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int[] array) {
/* 873*/    if (array == null)
/* 874*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 875*/    if (array.length == 0)
/* 876*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 880*/    int min = array[0];
/* 881*/    for (int j = 1; j < array.length; j++) {
/* 882*/      if (array[j] < min)
/* 883*/        min = array[j]; 
/*   0*/    } 
/* 887*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static short min(short[] array) {
/* 900*/    if (array == null)
/* 901*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 902*/    if (array.length == 0)
/* 903*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 907*/    short min = array[0];
/* 908*/    for (int i = 1; i < array.length; i++) {
/* 909*/      if (array[i] < min)
/* 910*/        min = array[i]; 
/*   0*/    } 
/* 914*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte min(byte[] array) {
/* 927*/    if (array == null)
/* 928*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 929*/    if (array.length == 0)
/* 930*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 934*/    byte min = array[0];
/* 935*/    for (int i = 1; i < array.length; i++) {
/* 936*/      if (array[i] < min)
/* 937*/        min = array[i]; 
/*   0*/    } 
/* 941*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double[] array) {
/* 954*/    if (array == null)
/* 955*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 956*/    if (array.length == 0)
/* 957*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 961*/    double min = array[0];
/* 962*/    for (int i = 1; i < array.length; i++) {
/* 963*/      if (array[i] < min)
/* 964*/        min = array[i]; 
/*   0*/    } 
/* 968*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float[] array) {
/* 981*/    if (array == null)
/* 982*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 983*/    if (array.length == 0)
/* 984*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 988*/    float min = array[0];
/* 989*/    for (int i = 1; i < array.length; i++) {
/* 990*/      if (array[i] < min)
/* 991*/        min = array[i]; 
/*   0*/    } 
/* 995*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long[] array) {
/*1010*/    if (array == null)
/*1011*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1012*/    if (array.length == 0)
/*1013*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1017*/    long max = array[0];
/*1018*/    for (int j = 1; j < array.length; j++) {
/*1019*/      if (array[j] > max)
/*1020*/        max = array[j]; 
/*   0*/    } 
/*1024*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int[] array) {
/*1037*/    if (array == null)
/*1038*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1039*/    if (array.length == 0)
/*1040*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1044*/    int max = array[0];
/*1045*/    for (int j = 1; j < array.length; j++) {
/*1046*/      if (array[j] > max)
/*1047*/        max = array[j]; 
/*   0*/    } 
/*1051*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static short max(short[] array) {
/*1064*/    if (array == null)
/*1065*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1066*/    if (array.length == 0)
/*1067*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1071*/    short max = array[0];
/*1072*/    for (int i = 1; i < array.length; i++) {
/*1073*/      if (array[i] > max)
/*1074*/        max = array[i]; 
/*   0*/    } 
/*1078*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte max(byte[] array) {
/*1091*/    if (array == null)
/*1092*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1093*/    if (array.length == 0)
/*1094*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1098*/    byte max = array[0];
/*1099*/    for (int i = 1; i < array.length; i++) {
/*1100*/      if (array[i] > max)
/*1101*/        max = array[i]; 
/*   0*/    } 
/*1105*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double[] array) {
/*1118*/    if (array == null)
/*1119*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1120*/    if (array.length == 0)
/*1121*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1125*/    double max = array[0];
/*1126*/    for (int j = 1; j < array.length; j++) {
/*1127*/      if (array[j] > max)
/*1128*/        max = array[j]; 
/*   0*/    } 
/*1132*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float[] array) {
/*1145*/    if (array == null)
/*1146*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1147*/    if (array.length == 0)
/*1148*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1152*/    float max = array[0];
/*1153*/    for (int j = 1; j < array.length; j++) {
/*1154*/      if (array[j] > max)
/*1155*/        max = array[j]; 
/*   0*/    } 
/*1159*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long a, long b, long c) {
/*1173*/    if (b < a)
/*1174*/      a = b; 
/*1176*/    if (c < a)
/*1177*/      a = c; 
/*1179*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int a, int b, int c) {
/*1191*/    if (b < a)
/*1192*/      a = b; 
/*1194*/    if (c < a)
/*1195*/      a = c; 
/*1197*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static short min(short a, short b, short c) {
/*1209*/    if (b < a)
/*1210*/      a = b; 
/*1212*/    if (c < a)
/*1213*/      a = c; 
/*1215*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte min(byte a, byte b, byte c) {
/*1227*/    if (b < a)
/*1228*/      a = b; 
/*1230*/    if (c < a)
/*1231*/      a = c; 
/*1233*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double a, double b, double c) {
/*1248*/    return Math.min(Math.min(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float a, float b, float c) {
/*1263*/    return Math.min(Math.min(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long a, long b, long c) {
/*1277*/    if (b > a)
/*1278*/      a = b; 
/*1280*/    if (c > a)
/*1281*/      a = c; 
/*1283*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int a, int b, int c) {
/*1295*/    if (b > a)
/*1296*/      a = b; 
/*1298*/    if (c > a)
/*1299*/      a = c; 
/*1301*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static short max(short a, short b, short c) {
/*1313*/    if (b > a)
/*1314*/      a = b; 
/*1316*/    if (c > a)
/*1317*/      a = c; 
/*1319*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte max(byte a, byte b, byte c) {
/*1331*/    if (b > a)
/*1332*/      a = b; 
/*1334*/    if (c > a)
/*1335*/      a = c; 
/*1337*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double a, double b, double c) {
/*1352*/    return Math.max(Math.max(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float a, float b, float c) {
/*1367*/    return Math.max(Math.max(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static int compare(double lhs, double rhs) {
/*1406*/    if (lhs < rhs)
/*1407*/      return -1; 
/*1409*/    if (lhs > rhs)
/*1410*/      return 1; 
/*1416*/    long lhsBits = Double.doubleToLongBits(lhs);
/*1417*/    long rhsBits = Double.doubleToLongBits(rhs);
/*1418*/    if (lhsBits == rhsBits)
/*1419*/      return 0; 
/*1427*/    if (lhsBits < rhsBits)
/*1428*/      return -1; 
/*1430*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int compare(float lhs, float rhs) {
/*1467*/    if (lhs < rhs)
/*1468*/      return -1; 
/*1470*/    if (lhs > rhs)
/*1471*/      return 1; 
/*1477*/    int lhsBits = Float.floatToIntBits(lhs);
/*1478*/    int rhsBits = Float.floatToIntBits(rhs);
/*1479*/    if (lhsBits == rhsBits)
/*1480*/      return 0; 
/*1488*/    if (lhsBits < rhsBits)
/*1489*/      return -1; 
/*1491*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isDigits(String str) {
/*1507*/    if (StringUtils.isEmpty(str))
/*1508*/      return false; 
/*1510*/    for (int i = 0; i < str.length(); i++) {
/*1511*/      if (!Character.isDigit(str.charAt(i)))
/*1512*/        return false; 
/*   0*/    } 
/*1515*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumber(String str) {
/*1532*/    if (StringUtils.isEmpty(str))
/*1533*/      return false; 
/*1535*/    char[] chars = str.toCharArray();
/*1536*/    int sz = chars.length;
/*   0*/    boolean hasExp = false;
/*   0*/    boolean hasDecPoint = false;
/*   0*/    boolean allowSigns = false;
/*   0*/    boolean foundDigit = false;
/*1542*/    int start = (chars[0] == '-') ? 1 : 0;
/*1543*/    if (sz > start + 1 && 
/*1544*/      chars[start] == '0' && chars[start + 1] == 'x') {
/*1545*/      int j = start + 2;
/*1546*/      if (j == sz)
/*1547*/        return false; 
/*1550*/      for (; j < chars.length; j++) {
/*1551*/        if ((chars[j] < '0' || chars[j] > '9') && (chars[j] < 'a' || chars[j] > 'f') && (chars[j] < 'A' || chars[j] > 'F'))
/*1554*/          return false; 
/*   0*/      } 
/*1557*/      return true;
/*   0*/    } 
/*1560*/    sz--;
/*1562*/    int i = start;
/*1565*/    while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
/*1566*/      if (chars[i] >= '0' && chars[i] <= '9') {
/*1567*/        foundDigit = true;
/*1568*/        allowSigns = false;
/*1570*/      } else if (chars[i] == '.') {
/*1571*/        if (hasDecPoint || hasExp)
/*1573*/          return false; 
/*1575*/        hasDecPoint = true;
/*1576*/      } else if (chars[i] == 'e' || chars[i] == 'E') {
/*1578*/        if (hasExp)
/*1580*/          return false; 
/*1582*/        if (!foundDigit)
/*1583*/          return false; 
/*1585*/        hasExp = true;
/*1586*/        allowSigns = true;
/*1587*/      } else if (chars[i] == '+' || chars[i] == '-') {
/*1588*/        if (!allowSigns)
/*1589*/          return false; 
/*1591*/        allowSigns = false;
/*1592*/        foundDigit = false;
/*   0*/      } else {
/*1594*/        return false;
/*   0*/      } 
/*1596*/      i++;
/*   0*/    } 
/*1598*/    if (i < chars.length) {
/*1599*/      if (chars[i] >= '0' && chars[i] <= '9')
/*1601*/        return true; 
/*1603*/      if (chars[i] == 'e' || chars[i] == 'E')
/*1605*/        return false; 
/*1607*/      if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F'))
/*1612*/        return foundDigit; 
/*1614*/      if (chars[i] == 'l' || chars[i] == 'L')
/*1617*/        return (foundDigit && !hasExp); 
/*1620*/      return false;
/*   0*/    } 
/*1624*/    return (!allowSigns && foundDigit);
/*   0*/  }
/*   0*/}
