/*   0*/package org.apache.commons.lang;
/*   0*/
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/
/*   0*/public final class NumberUtils {
/*   0*/  public static int stringToInt(String str) {
/*  61*/    return stringToInt(str, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static int stringToInt(String str, int defaultValue) {
/*   0*/    try {
/*  74*/      return Integer.parseInt(str);
/*  75*/    } catch (NumberFormatException nfe) {
/*  76*/      return defaultValue;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Number createNumber(String val) throws NumberFormatException {
/*   0*/    String mant, dec, exp;
/* 139*/    if (val == null)
/* 140*/      return null; 
/* 142*/    if (val.length() == 0)
/* 143*/      throw new NumberFormatException("\"\" is not a valid number."); 
/* 145*/    if (val.startsWith("--"))
/* 150*/      return null; 
/* 152*/    if (val.startsWith("0x") || val.startsWith("-0x"))
/* 153*/      return createInteger(val); 
/* 155*/    char lastChar = val.charAt(val.length() - 1);
/* 159*/    int decPos = val.indexOf('.');
/* 160*/    int expPos = val.indexOf('e') + val.indexOf('E') + 1;
/* 162*/    if (decPos > -1) {
/* 164*/      if (expPos > -1) {
/* 165*/        if (expPos < decPos)
/* 166*/          throw new NumberFormatException(val + " is not a valid number."); 
/* 168*/        dec = val.substring(decPos + 1, expPos);
/*   0*/      } else {
/* 170*/        dec = val.substring(decPos + 1);
/*   0*/      } 
/* 172*/      mant = val.substring(0, decPos);
/*   0*/    } else {
/* 174*/      if (expPos > -1) {
/* 175*/        mant = val.substring(0, expPos);
/*   0*/      } else {
/* 177*/        mant = val;
/*   0*/      } 
/* 179*/      dec = null;
/*   0*/    } 
/* 181*/    if (!Character.isDigit(lastChar)) {
/* 182*/      if (expPos > -1 && expPos < val.length() - 1) {
/* 183*/        exp = val.substring(expPos + 1, val.length() - 1);
/*   0*/      } else {
/* 185*/        exp = null;
/*   0*/      } 
/* 188*/      String numeric = val.substring(0, val.length() - 1);
/* 189*/      boolean bool = (isAllZeros(mant) && isAllZeros(exp));
/* 190*/      switch (lastChar) {
/*   0*/        case 'L':
/*   0*/        case 'l':
/* 193*/          if (dec == null && exp == null) {
/* 193*/            false;
/* 193*/            if ((lastChar == '-' && isDigits(numeric.substring(1))) || isDigits(numeric))
/*   0*/              try {
/* 197*/                return createLong(numeric);
/* 198*/              } catch (NumberFormatException numberFormatException) {
/* 201*/                return createBigInteger(numeric);
/*   0*/              }  
/*   0*/          } 
/* 204*/          throw new NumberFormatException(val + " is not a valid number.");
/*   0*/        case 'F':
/*   0*/        case 'f':
/*   0*/          try {
/* 208*/            Float f = createFloat(numeric);
/* 209*/            if (!f.isInfinite() && (f.floatValue() != 0.0F || bool))
/* 212*/              return f; 
/* 215*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/        case 'D':
/*   0*/        case 'd':
/*   0*/          try {
/* 222*/            Double d = createDouble(numeric);
/* 223*/            if (!d.isInfinite() && (d.floatValue() != 0.0D || bool))
/* 224*/              return d; 
/* 226*/          } catch (NumberFormatException numberFormatException) {}
/*   0*/          try {
/* 230*/            return createBigDecimal(numeric);
/* 231*/          } catch (NumberFormatException numberFormatException) {
/*   0*/            break;
/*   0*/          } 
/*   0*/      } 
/* 236*/      throw new NumberFormatException(val + " is not a valid number.");
/*   0*/    } 
/* 242*/    if (expPos > -1 && expPos < val.length() - 1) {
/* 243*/      exp = val.substring(expPos + 1, val.length());
/*   0*/    } else {
/* 245*/      exp = null;
/*   0*/    } 
/* 247*/    if (dec == null && exp == null)
/*   0*/      try {
/* 250*/        return createInteger(val);
/* 251*/      } catch (NumberFormatException numberFormatException) {
/*   0*/        try {
/* 255*/          return createLong(val);
/* 256*/        } catch (NumberFormatException numberFormatException1) {
/* 259*/          return createBigInteger(val);
/*   0*/        } 
/*   0*/      }  
/* 263*/    boolean allZeros = (isAllZeros(mant) && isAllZeros(exp));
/*   0*/    try {
/* 265*/      Float f = createFloat(val);
/* 266*/      if (!f.isInfinite() && (f.floatValue() != 0.0F || allZeros))
/* 267*/        return f; 
/* 269*/    } catch (NumberFormatException numberFormatException) {}
/*   0*/    try {
/* 273*/      Double d = createDouble(val);
/* 274*/      if (!d.isInfinite() && (d.doubleValue() != 0.0D || allZeros))
/* 275*/        return d; 
/* 277*/    } catch (NumberFormatException numberFormatException) {}
/* 281*/    return createBigDecimal(val);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isAllZeros(String s) {
/* 297*/    if (s == null)
/* 298*/      return true; 
/* 300*/    for (int i = s.length() - 1; i >= 0; i--) {
/* 301*/      if (s.charAt(i) != '0')
/* 302*/        return false; 
/*   0*/    } 
/* 305*/    return (s.length() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Float createFloat(String val) {
/* 318*/    return Float.valueOf(val);
/*   0*/  }
/*   0*/  
/*   0*/  public static Double createDouble(String val) {
/* 329*/    return Double.valueOf(val);
/*   0*/  }
/*   0*/  
/*   0*/  public static Integer createInteger(String val) {
/* 342*/    return Integer.decode(val);
/*   0*/  }
/*   0*/  
/*   0*/  public static Long createLong(String val) {
/* 353*/    return Long.valueOf(val);
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger createBigInteger(String val) {
/* 364*/    BigInteger bi = new BigInteger(val);
/* 365*/    return bi;
/*   0*/  }
/*   0*/  
/*   0*/  public static BigDecimal createBigDecimal(String val) {
/* 376*/    BigDecimal bd = new BigDecimal(val);
/* 377*/    return bd;
/*   0*/  }
/*   0*/  
/*   0*/  public static long minimum(long a, long b, long c) {
/* 391*/    if (b < a)
/* 392*/      a = b; 
/* 394*/    if (c < a)
/* 395*/      a = c; 
/* 397*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int minimum(int a, int b, int c) {
/* 409*/    if (b < a)
/* 410*/      a = b; 
/* 412*/    if (c < a)
/* 413*/      a = c; 
/* 415*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static long maximum(long a, long b, long c) {
/* 427*/    if (b > a)
/* 428*/      a = b; 
/* 430*/    if (c > a)
/* 431*/      a = c; 
/* 433*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int maximum(int a, int b, int c) {
/* 445*/    if (b > a)
/* 446*/      a = b; 
/* 448*/    if (c > a)
/* 449*/      a = c; 
/* 451*/    return a;
/*   0*/  }
/*   0*/  
/*   0*/  public static int compare(double lhs, double rhs) {
/* 491*/    if (lhs < rhs)
/* 492*/      return -1; 
/* 494*/    if (lhs > rhs)
/* 495*/      return 1; 
/* 501*/    long lhsBits = Double.doubleToLongBits(lhs);
/* 502*/    long rhsBits = Double.doubleToLongBits(rhs);
/* 503*/    if (lhsBits == rhsBits)
/* 504*/      return 0; 
/* 512*/    if (lhsBits < rhsBits)
/* 513*/      return -1; 
/* 515*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int compare(float lhs, float rhs) {
/* 552*/    if (lhs < rhs)
/* 553*/      return -1; 
/* 555*/    if (lhs > rhs)
/* 556*/      return 1; 
/* 562*/    int lhsBits = Float.floatToIntBits(lhs);
/* 563*/    int rhsBits = Float.floatToIntBits(rhs);
/* 564*/    if (lhsBits == rhsBits)
/* 565*/      return 0; 
/* 573*/    if (lhsBits < rhsBits)
/* 574*/      return -1; 
/* 576*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isDigits(String str) {
/* 593*/    if (str == null || str.length() == 0)
/* 594*/      return false; 
/* 596*/    for (int i = 0; i < str.length(); i++) {
/* 597*/      if (!Character.isDigit(str.charAt(i)))
/* 598*/        return false; 
/*   0*/    } 
/* 601*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumber(String str) {
/* 618*/    if (StringUtils.isEmpty(str))
/* 619*/      return false; 
/* 621*/    char[] chars = str.toCharArray();
/* 622*/    int sz = chars.length;
/*   0*/    boolean hasExp = false;
/*   0*/    boolean hasDecPoint = false;
/*   0*/    boolean allowSigns = false;
/*   0*/    boolean foundDigit = false;
/* 628*/    int start = (chars[0] == '-') ? 1 : 0;
/* 629*/    if (sz > start + 1 && 
/* 630*/      chars[start] == '0' && chars[start + 1] == 'x') {
/* 631*/      int j = start + 2;
/* 632*/      if (j == sz)
/* 633*/        return false; 
/* 636*/      for (; j < chars.length; j++) {
/* 637*/        if ((chars[j] < '0' || chars[j] > '9') && (chars[j] < 'a' || chars[j] > 'f') && (chars[j] < 'A' || chars[j] > 'F'))
/* 640*/          return false; 
/*   0*/      } 
/* 643*/      return true;
/*   0*/    } 
/* 646*/    sz--;
/* 648*/    int i = start;
/* 651*/    while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
/* 652*/      if (chars[i] >= '0' && chars[i] <= '9') {
/* 653*/        foundDigit = true;
/* 654*/        allowSigns = false;
/* 656*/      } else if (chars[i] == '.') {
/* 657*/        if (hasDecPoint || hasExp)
/* 659*/          return false; 
/* 661*/        hasDecPoint = true;
/* 662*/      } else if (chars[i] == 'e' || chars[i] == 'E') {
/* 664*/        if (hasExp)
/* 666*/          return false; 
/* 668*/        if (!foundDigit)
/* 669*/          return false; 
/* 671*/        hasExp = true;
/* 672*/        allowSigns = true;
/* 673*/      } else if (chars[i] == '+' || chars[i] == '-') {
/* 674*/        if (!allowSigns)
/* 675*/          return false; 
/* 677*/        allowSigns = false;
/* 678*/        foundDigit = false;
/*   0*/      } else {
/* 680*/        return false;
/*   0*/      } 
/* 682*/      i++;
/*   0*/    } 
/* 684*/    if (i < chars.length) {
/* 685*/      if (chars[i] >= '0' && chars[i] <= '9')
/* 687*/        return true; 
/* 689*/      if (chars[i] == 'e' || chars[i] == 'E')
/* 691*/        return false; 
/* 693*/      if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F'))
/* 698*/        return foundDigit; 
/* 700*/      if (chars[i] == 'l' || chars[i] == 'L')
/* 703*/        return (foundDigit && !hasExp); 
/* 706*/      return false;
/*   0*/    } 
/* 710*/    return (!allowSigns && foundDigit);
/*   0*/  }
/*   0*/}
