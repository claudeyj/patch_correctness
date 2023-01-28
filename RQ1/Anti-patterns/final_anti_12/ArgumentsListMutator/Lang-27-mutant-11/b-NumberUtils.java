/*   0*/package org.apache.commons.lang3.math;
/*   0*/
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/import org.apache.commons.lang3.StringUtils;
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
/*  59*/  public static final Byte BYTE_ZERO = (byte)0;
/*   0*/  
/*  61*/  public static final Byte BYTE_ONE = (byte)1;
/*   0*/  
/*  63*/  public static final Byte BYTE_MINUS_ONE = (byte)-1;
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
/*   0*/  public static int toInt(String str) {
/* 107*/    return toInt(str, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static int toInt(String str, int defaultValue) {
/* 128*/    if (str == null)
/* 129*/      return defaultValue; 
/*   0*/    try {
/* 132*/      return Integer.parseInt(str);
/* 133*/    } catch (NumberFormatException nfe) {
/* 134*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static long toLong(String str) {
/* 156*/    return toLong(str, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  public static long toLong(String str, long defaultValue) {
/* 177*/    if (str == null)
/* 178*/      return defaultValue; 
/*   0*/    try {
/* 181*/      return Long.parseLong(str);
/* 182*/    } catch (NumberFormatException nfe) {
/* 183*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static float toFloat(String str) {
/* 206*/    return toFloat(str, 0.0F);
/*   0*/  }
/*   0*/  
/*   0*/  public static float toFloat(String str, float defaultValue) {
/* 229*/    if (str == null)
/* 230*/      return defaultValue; 
/*   0*/    try {
/* 233*/      return Float.parseFloat(str);
/* 234*/    } catch (NumberFormatException nfe) {
/* 235*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDouble(String str) {
/* 258*/    return toDouble(str, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDouble(String str, double defaultValue) {
/* 281*/    if (str == null)
/* 282*/      return defaultValue; 
/*   0*/    try {
/* 285*/      return Double.parseDouble(str);
/* 286*/    } catch (NumberFormatException nfe) {
/* 287*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static byte toByte(String str) {
/* 310*/    return toByte(str, (byte)0);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte toByte(String str, byte defaultValue) {
/* 331*/    if (str == null)
/* 332*/      return defaultValue; 
/*   0*/    try {
/* 335*/      return Byte.parseByte(str);
/* 336*/    } catch (NumberFormatException nfe) {
/* 337*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static short toShort(String str) {
/* 359*/    return toShort(str, (short)0);
/*   0*/  }
/*   0*/  
/*   0*/  public static short toShort(String str, short defaultValue) {
/* 380*/    if (str == null)
/* 381*/      return defaultValue; 
/*   0*/    try {
/* 384*/      return Short.parseShort(str);
/* 385*/    } catch (NumberFormatException nfe) {
/* 386*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Number createNumber(String str) throws NumberFormatException {
/*   0*/    String dec, mant, exp;
/* 453*/    if (str == null)
/* 454*/      return null; 
/* 456*/    if (StringUtils.isBlank(str))
/* 457*/      throw new NumberFormatException("A blank string is not a valid number"); 
/* 459*/    if (str.startsWith("--"))
/* 464*/      return null; 
/* 466*/    if (str.startsWith("0x") || str.startsWith("-0x"))
/* 467*/      return createInteger(str); 
/* 469*/    char lastChar = str.charAt(str.length() - 1);
/* 473*/    int decPos = str.indexOf('.');
/* 474*/    int expPos = str.indexOf('e') + str.indexOf('E') + 1;
/* 476*/    if (decPos > -1) {
/* 478*/      if (expPos > -1) {
/* 479*/        if (expPos < decPos)
/* 480*/          throw new NumberFormatException(str + " is not a valid number."); 
/* 482*/        dec = str.substring(decPos + 1, expPos);
/*   0*/      } else {
/* 484*/        dec = str.substring(decPos + 1);
/*   0*/      } 
/* 486*/      mant = str.substring(0, decPos);
/*   0*/    } else {
/* 488*/      if (expPos > -1) {
/* 489*/        int j = expPos, i = 0;
/* 489*/        mant = str.substring(i);
/*   0*/      } else {
/* 491*/        mant = str;
/*   0*/      } 
/* 493*/      dec = null;
/*   0*/    } 
/* 495*/    if (!Character.isDigit(lastChar) && lastChar != '.') {
/* 496*/      if (expPos > -1 && expPos < str.length() - 1) {
/* 497*/        exp = str.substring(expPos + 1, str.length() - 1);
/*   0*/      } else {
/* 499*/        exp = null;
/*   0*/      } 
/* 502*/      String numeric = str.substring(0, str.length() - 1);
/* 503*/      boolean bool = (isAllZeros(mant) && isAllZeros(exp));
/* 504*/      switch (lastChar) {
/*   0*/        case 'L':
/*   0*/        case 'l':
/* 507*/          if (dec == null && exp == null && ((numeric.charAt(0) == '-' && isDigits(numeric.substring(1))) || isDigits(numeric)))
/*   0*/            try {
/* 511*/              return createLong(numeric);
/* 512*/            } catch (NumberFormatException numberFormatException) {
/* 515*/              return createBigInteger(numeric);
/*   0*/            }  
/* 518*/          throw new NumberFormatException(str + " is not a valid number.");
/*   0*/        case 'F':
/*   0*/        case 'f':
/*   0*/          try {
/* 522*/            Float f = createFloat(numeric);
/* 523*/            if (!f.isInfinite() && (f != 0.0F || bool))
/* 526*/              return f; 
/* 529*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/        case 'D':
/*   0*/        case 'd':
/*   0*/          try {
/* 536*/            Double d = createDouble(numeric);
/* 537*/            if (!d.isInfinite() && (d.floatValue() != 0.0D || bool))
/* 538*/              return d; 
/* 540*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/          try {
/* 544*/            return createBigDecimal(numeric);
/* 545*/          } catch (NumberFormatException numberFormatException) {
/*   0*/            break;
/*   0*/          } 
/*   0*/      } 
/* 550*/      throw new NumberFormatException(str + " is not a valid number.");
/*   0*/    } 
/* 556*/    if (expPos > -1 && expPos < str.length() - 1) {
/* 557*/      exp = str.substring(expPos + 1, str.length());
/*   0*/    } else {
/* 559*/      exp = null;
/*   0*/    } 
/* 561*/    if (dec == null && exp == null)
/*   0*/      try {
/* 564*/        return createInteger(str);
/* 565*/      } catch (NumberFormatException numberFormatException) {
/*   0*/        try {
/* 569*/          return createLong(str);
/* 570*/        } catch (NumberFormatException numberFormatException1) {
/* 573*/          return createBigInteger(str);
/*   0*/        } 
/*   0*/      }  
/* 577*/    boolean allZeros = (isAllZeros(mant) && isAllZeros(exp));
/*   0*/    try {
/* 579*/      Float f = createFloat(str);
/* 580*/      if (!f.isInfinite() && (f != 0.0F || allZeros))
/* 581*/        return f; 
/* 583*/    } catch (NumberFormatException numberFormatException) {}
/*   0*/    try {
/* 587*/      Double d = createDouble(str);
/* 588*/      if (!d.isInfinite() && (d != 0.0D || allZeros))
/* 589*/        return d; 
/* 591*/    } catch (NumberFormatException numberFormatException) {}
/* 595*/    return createBigDecimal(str);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isAllZeros(String str) {
/* 610*/    if (str == null)
/* 611*/      return true; 
/* 613*/    for (int i = str.length() - 1; i >= 0; i--) {
/* 614*/      if (str.charAt(i) != '0')
/* 615*/        return false; 
/*   0*/    } 
/* 618*/    return (str.length() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Float createFloat(String str) {
/* 632*/    if (str == null)
/* 633*/      return null; 
/* 635*/    return Float.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Double createDouble(String str) {
/* 648*/    if (str == null)
/* 649*/      return null; 
/* 651*/    return Double.valueOf(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer createInteger(String str) {
/* 665*/    if (str == null)
/* 666*/      return null; 
/* 669*/    return Integer.decode(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Long createLong(String str) {
/* 682*/    if (str == null)
/* 683*/      return null; 
/* 685*/    return Long.valueOf(str);
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
/* 721*/    return new BigDecimal(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long[] array) {
/* 736*/    if (array == null)
/* 737*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 738*/    if (array.length == 0)
/* 739*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 743*/    long min = array[0];
/* 744*/    for (int i = 1; i < array.length; i++) {
/* 745*/      if (array[i] < min)
/* 746*/        min = array[i]; 
/*   0*/    } 
/* 750*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int[] array) {
/* 763*/    if (array == null)
/* 764*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 765*/    if (array.length == 0)
/* 766*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 770*/    int min = array[0];
/* 771*/    for (int j = 1; j < array.length; j++) {
/* 772*/      if (array[j] < min)
/* 773*/        min = array[j]; 
/*   0*/    } 
/* 777*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static short min(short[] array) {
/* 790*/    if (array == null)
/* 791*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 792*/    if (array.length == 0)
/* 793*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 797*/    short min = array[0];
/* 798*/    for (int i = 1; i < array.length; i++) {
/* 799*/      if (array[i] < min)
/* 800*/        min = array[i]; 
/*   0*/    } 
/* 804*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte min(byte[] array) {
/* 817*/    if (array == null)
/* 818*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 819*/    if (array.length == 0)
/* 820*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 824*/    byte min = array[0];
/* 825*/    for (int i = 1; i < array.length; i++) {
/* 826*/      if (array[i] < min)
/* 827*/        min = array[i]; 
/*   0*/    } 
/* 831*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double[] array) {
/* 845*/    if (array == null)
/* 846*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 847*/    if (array.length == 0)
/* 848*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 852*/    double min = array[0];
/* 853*/    for (int i = 1; i < array.length; i++) {
/* 854*/      if (Double.isNaN(array[i]))
/* 855*/        return Double.NaN; 
/* 857*/      if (array[i] < min)
/* 858*/        min = array[i]; 
/*   0*/    } 
/* 862*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float[] array) {
/* 876*/    if (array == null)
/* 877*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 878*/    if (array.length == 0)
/* 879*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 883*/    float min = array[0];
/* 884*/    for (int i = 1; i < array.length; i++) {
/* 885*/      if (Float.isNaN(array[i]))
/* 886*/        return Float.NaN; 
/* 888*/      if (array[i] < min)
/* 889*/        min = array[i]; 
/*   0*/    } 
/* 893*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long[] array) {
/* 908*/    if (array == null)
/* 909*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 910*/    if (array.length == 0)
/* 911*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 915*/    long max = array[0];
/* 916*/    for (int j = 1; j < array.length; j++) {
/* 917*/      if (array[j] > max)
/* 918*/        max = array[j]; 
/*   0*/    } 
/* 922*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int[] array) {
/* 935*/    if (array == null)
/* 936*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 937*/    if (array.length == 0)
/* 938*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 942*/    int max = array[0];
/* 943*/    for (int j = 1; j < array.length; j++) {
/* 944*/      if (array[j] > max)
/* 945*/        max = array[j]; 
/*   0*/    } 
/* 949*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static short max(short[] array) {
/* 962*/    if (array == null)
/* 963*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 964*/    if (array.length == 0)
/* 965*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 969*/    short max = array[0];
/* 970*/    for (int i = 1; i < array.length; i++) {
/* 971*/      if (array[i] > max)
/* 972*/        max = array[i]; 
/*   0*/    } 
/* 976*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte max(byte[] array) {
/* 989*/    if (array == null)
/* 990*/      throw new IllegalArgumentException("The Array must not be null"); 
/* 991*/    if (array.length == 0)
/* 992*/      throw new IllegalArgumentException("Array cannot be empty."); 
/* 996*/    byte max = array[0];
/* 997*/    for (int i = 1; i < array.length; i++) {
/* 998*/      if (array[i] > max)
/* 999*/        max = array[i]; 
/*   0*/    } 
/*1003*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double[] array) {
/*1017*/    if (array == null)
/*1018*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1019*/    if (array.length == 0)
/*1020*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1024*/    double max = array[0];
/*1025*/    for (int j = 1; j < array.length; j++) {
/*1026*/      if (Double.isNaN(array[j]))
/*1027*/        return Double.NaN; 
/*1029*/      if (array[j] > max)
/*1030*/        max = array[j]; 
/*   0*/    } 
/*1034*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float[] array) {
/*1048*/    if (array == null)
/*1049*/      throw new IllegalArgumentException("The Array must not be null"); 
/*1050*/    if (array.length == 0)
/*1051*/      throw new IllegalArgumentException("Array cannot be empty."); 
/*1055*/    float max = array[0];
/*1056*/    for (int j = 1; j < array.length; j++) {
/*1057*/      if (Float.isNaN(array[j]))
/*1058*/        return Float.NaN; 
/*1060*/      if (array[j] > max)
/*1061*/        max = array[j]; 
/*   0*/    } 
/*1065*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long a, long b, long c) {
/*1079*/    if (b < a)
/*1080*/      a = b; 
/*1082*/    if (c < a)
/*1083*/      a = c; 
/*1085*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int a, int b, int c) {
/*1097*/    if (b < a)
/*1098*/      a = b; 
/*1100*/    if (c < a)
/*1101*/      a = c; 
/*1103*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static short min(short a, short b, short c) {
/*1115*/    if (b < a)
/*1116*/      a = b; 
/*1118*/    if (c < a)
/*1119*/      a = c; 
/*1121*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte min(byte a, byte b, byte c) {
/*1133*/    if (b < a)
/*1134*/      a = b; 
/*1136*/    if (c < a)
/*1137*/      a = c; 
/*1139*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double a, double b, double c) {
/*1155*/    return Math.min(Math.min(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float a, float b, float c) {
/*1171*/    return Math.min(Math.min(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long a, long b, long c) {
/*1185*/    if (b > a)
/*1186*/      a = b; 
/*1188*/    if (c > a)
/*1189*/      a = c; 
/*1191*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int a, int b, int c) {
/*1203*/    if (b > a)
/*1204*/      a = b; 
/*1206*/    if (c > a)
/*1207*/      a = c; 
/*1209*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static short max(short a, short b, short c) {
/*1221*/    if (b > a)
/*1222*/      a = b; 
/*1224*/    if (c > a)
/*1225*/      a = c; 
/*1227*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte max(byte a, byte b, byte c) {
/*1239*/    if (b > a)
/*1240*/      a = b; 
/*1242*/    if (c > a)
/*1243*/      a = c; 
/*1245*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double a, double b, double c) {
/*1261*/    return Math.max(Math.max(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float a, float b, float c) {
/*1277*/    return Math.max(Math.max(a, b), c);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isDigits(String str) {
/*1292*/    if (StringUtils.isEmpty(str))
/*1293*/      return false; 
/*1295*/    for (int i = 0; i < str.length(); i++) {
/*1296*/      if (!Character.isDigit(str.charAt(i)))
/*1297*/        return false; 
/*   0*/    } 
/*1300*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumber(String str) {
/*1317*/    if (StringUtils.isEmpty(str))
/*1318*/      return false; 
/*1320*/    char[] chars = str.toCharArray();
/*1321*/    int sz = chars.length;
/*   0*/    boolean hasExp = false;
/*   0*/    boolean hasDecPoint = false;
/*   0*/    boolean allowSigns = false;
/*   0*/    boolean foundDigit = false;
/*1327*/    int start = (chars[0] == '-') ? 1 : 0;
/*1328*/    if (sz > start + 1 && 
/*1329*/      chars[start] == '0' && chars[start + 1] == 'x') {
/*1330*/      int j = start + 2;
/*1331*/      if (j == sz)
/*1332*/        return false; 
/*1335*/      for (; j < chars.length; j++) {
/*1336*/        if ((chars[j] < '0' || chars[j] > '9') && (chars[j] < 'a' || chars[j] > 'f') && (chars[j] < 'A' || chars[j] > 'F'))
/*1339*/          return false; 
/*   0*/      } 
/*1342*/      return true;
/*   0*/    } 
/*1345*/    sz--;
/*1347*/    int i = start;
/*1350*/    while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
/*1351*/      if (chars[i] >= '0' && chars[i] <= '9') {
/*1352*/        foundDigit = true;
/*1353*/        allowSigns = false;
/*1355*/      } else if (chars[i] == '.') {
/*1356*/        if (hasDecPoint || hasExp)
/*1358*/          return false; 
/*1360*/        hasDecPoint = true;
/*1361*/      } else if (chars[i] == 'e' || chars[i] == 'E') {
/*1363*/        if (hasExp)
/*1365*/          return false; 
/*1367*/        if (!foundDigit)
/*1368*/          return false; 
/*1370*/        hasExp = true;
/*1371*/        allowSigns = true;
/*1372*/      } else if (chars[i] == '+' || chars[i] == '-') {
/*1373*/        if (!allowSigns)
/*1374*/          return false; 
/*1376*/        allowSigns = false;
/*1377*/        foundDigit = false;
/*   0*/      } else {
/*1379*/        return false;
/*   0*/      } 
/*1381*/      i++;
/*   0*/    } 
/*1383*/    if (i < chars.length) {
/*1384*/      if (chars[i] >= '0' && chars[i] <= '9')
/*1386*/        return true; 
/*1388*/      if (chars[i] == 'e' || chars[i] == 'E')
/*1390*/        return false; 
/*1392*/      if (chars[i] == '.') {
/*1393*/        if (hasDecPoint || hasExp)
/*1395*/          return false; 
/*1398*/        return foundDigit;
/*   0*/      } 
/*1400*/      if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F'))
/*1405*/        return foundDigit; 
/*1407*/      if (chars[i] == 'l' || chars[i] == 'L')
/*1410*/        return (foundDigit && !hasExp); 
/*1413*/      return false;
/*   0*/    } 
/*1417*/    return (!allowSigns && foundDigit);
/*   0*/  }
/*   0*/}
