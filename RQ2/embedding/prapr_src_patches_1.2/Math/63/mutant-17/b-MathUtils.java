/*   0*/package org.apache.commons.math.util;
/*   0*/
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/import java.util.Arrays;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.exception.NonMonotonousSequenceException;
/*   0*/import org.apache.commons.math.exception.util.Localizable;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/
/*   0*/public final class MathUtils {
/*   0*/  public static final double EPSILON = 1.1102230246251565E-16D;
/*   0*/  
/*   0*/  public static final double SAFE_MIN = 2.2250738585072014E-308D;
/*   0*/  
/*   0*/  public static final double TWO_PI = 6.283185307179586D;
/*   0*/  
/*   0*/  private static final byte NB = -1;
/*   0*/  
/*   0*/  private static final short NS = -1;
/*   0*/  
/*   0*/  private static final byte PB = 1;
/*   0*/  
/*   0*/  private static final short PS = 1;
/*   0*/  
/*   0*/  private static final byte ZB = 0;
/*   0*/  
/*   0*/  private static final short ZS = 0;
/*   0*/  
/*   0*/  private static final int NAN_GAP = 4194304;
/*   0*/  
/*   0*/  private static final long SGN_MASK = -9223372036854775808L;
/*   0*/  
/*  75*/  private static final long[] FACTORIALS = new long[] { 
/*  75*/      1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 
/*  75*/      3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 
/*  75*/      2432902008176640000L };
/*   0*/  
/*   0*/  public static int addAndCheck(int x, int y) {
/* 102*/    long s = x + y;
/* 103*/    if (s < -2147483648L || s > 2147483647L)
/* 104*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { x, y }); 
/* 106*/    return (int)s;
/*   0*/  }
/*   0*/  
/*   0*/  public static long addAndCheck(long a, long b) {
/* 120*/    return addAndCheck(a, b, LocalizedFormats.OVERFLOW_IN_ADDITION);
/*   0*/  }
/*   0*/  
/*   0*/  private static long addAndCheck(long a, long b, Localizable pattern) {
/*   0*/    long ret;
/* 136*/    if (a > b) {
/* 138*/      ret = addAndCheck(b, a, pattern);
/* 142*/    } else if (a < 0L) {
/* 143*/      if (b < 0L) {
/* 145*/        if (Long.MIN_VALUE - b <= a) {
/* 146*/          ret = a + b;
/*   0*/        } else {
/* 148*/          throw MathRuntimeException.createArithmeticException(pattern, new Object[] { a, b });
/*   0*/        } 
/*   0*/      } else {
/* 152*/        ret = a + b;
/*   0*/      } 
/* 159*/    } else if (a <= Long.MAX_VALUE - b) {
/* 160*/      ret = a + b;
/*   0*/    } else {
/* 162*/      throw MathRuntimeException.createArithmeticException(pattern, new Object[] { a, b });
/*   0*/    } 
/* 166*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static long binomialCoefficient(int n, int k) {
/* 195*/    checkBinomial(n, k);
/* 196*/    if (n == k || k == 0)
/* 197*/      return 1L; 
/* 199*/    if (k == 1 || k == n - 1)
/* 200*/      return n; 
/* 203*/    if (k > n / 2)
/* 204*/      return binomialCoefficient(n, n - k); 
/* 211*/    long result = 1L;
/* 212*/    if (n <= 61) {
/* 214*/      int i = n - k + 1;
/* 215*/      for (int j = 1; j <= k; j++) {
/* 216*/        result = result * i / j;
/* 217*/        i++;
/*   0*/      } 
/* 219*/    } else if (n <= 66) {
/* 222*/      int i = n - k + 1;
/* 223*/      for (int j = 1; j <= k; j++) {
/* 230*/        long d = gcd(i, j);
/* 231*/        result = result / j / d * i / d;
/* 232*/        i++;
/*   0*/      } 
/*   0*/    } else {
/* 238*/      int i = n - k + 1;
/* 239*/      for (int j = 1; j <= k; j++) {
/* 240*/        long d = gcd(i, j);
/* 241*/        result = mulAndCheck(result / j / d, i / d);
/* 242*/        i++;
/*   0*/      } 
/*   0*/    } 
/* 245*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double binomialCoefficientDouble(int n, int k) {
/* 271*/    checkBinomial(n, k);
/* 272*/    if (n == k || k == 0)
/* 273*/      return 1.0D; 
/* 275*/    if (k == 1 || k == n - 1)
/* 276*/      return n; 
/* 278*/    if (k > n / 2)
/* 279*/      return binomialCoefficientDouble(n, n - k); 
/* 281*/    if (n < 67)
/* 282*/      return binomialCoefficient(n, k); 
/* 285*/    double result = 1.0D;
/* 286*/    for (int i = 1; i <= k; i++)
/* 287*/      result *= (n - k + i) / i; 
/* 290*/    return FastMath.floor(result + 0.5D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double binomialCoefficientLog(int n, int k) {
/* 312*/    checkBinomial(n, k);
/* 313*/    if (n == k || k == 0)
/* 314*/      return 0.0D; 
/* 316*/    if (k == 1 || k == n - 1)
/* 317*/      return FastMath.log(n); 
/* 324*/    if (n < 67)
/* 325*/      return FastMath.log(binomialCoefficient(n, k)); 
/* 332*/    if (n < 1030)
/* 333*/      return FastMath.log(binomialCoefficientDouble(n, k)); 
/* 336*/    if (k > n / 2)
/* 337*/      return binomialCoefficientLog(n, n - k); 
/* 343*/    double logSum = 0.0D;
/* 346*/    for (int i = n - k + 1; i <= n; i++)
/* 347*/      logSum += FastMath.log(i); 
/* 351*/    for (int j = 2; j <= k; j++)
/* 352*/      logSum -= FastMath.log(j); 
/* 355*/    return logSum;
/*   0*/  }
/*   0*/  
/*   0*/  private static void checkBinomial(int n, int k) throws IllegalArgumentException {
/* 366*/    if (n < k)
/* 367*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER, new Object[] { n, k }); 
/* 371*/    if (n < 0)
/* 372*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.BINOMIAL_NEGATIVE_PARAMETER, new Object[] { n }); 
/*   0*/  }
/*   0*/  
/*   0*/  public static int compareTo(double x, double y, double eps) {
/* 389*/    if (equals(x, y, eps))
/* 390*/      return 0; 
/* 391*/    if (x < y)
/* 392*/      return -1; 
/* 394*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public static double cosh(double x) {
/* 405*/    return (FastMath.exp(x) + FastMath.exp(-x)) / 2.0D;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(double x, double y) {
/* 417*/    return ((Double.isNaN(x) && Double.isNaN(EPSILON)) || x == y);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equalsIncludingNaN(double x, double y) {
/* 429*/    return ((Double.isNaN(x) && Double.isNaN(y)) || equals(x, y, 1));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(double x, double y, double eps) {
/* 442*/    return (equals(x, y, 1) || FastMath.abs(y - x) <= eps);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equalsIncludingNaN(double x, double y, double eps) {
/* 456*/    return (equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(double x, double y, int maxUlps) {
/* 479*/    assert maxUlps > 0 && maxUlps < 4194304;
/* 481*/    long xInt = Double.doubleToLongBits(x);
/* 482*/    long yInt = Double.doubleToLongBits(y);
/* 485*/    if (xInt < 0L)
/* 486*/      xInt = Long.MIN_VALUE - xInt; 
/* 488*/    if (yInt < 0L)
/* 489*/      yInt = Long.MIN_VALUE - yInt; 
/* 492*/    boolean isEqual = (FastMath.abs(xInt - yInt) <= maxUlps);
/* 494*/    return (isEqual && !Double.isNaN(x) && !Double.isNaN(y));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equalsIncludingNaN(double x, double y, int maxUlps) {
/* 509*/    return ((Double.isNaN(x) && Double.isNaN(y)) || equals(x, y, maxUlps));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(double[] x, double[] y) {
/* 523*/    if (x == null || y == null)
/* 524*/      return !(((x == null) ? true : false) ^ ((y == null) ? true : false)); 
/* 526*/    if (x.length != y.length)
/* 527*/      return false; 
/* 529*/    for (int i = 0; i < x.length; i++) {
/* 530*/      if (!equals(x[i], y[i]))
/* 531*/        return false; 
/*   0*/    } 
/* 534*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equalsIncludingNaN(double[] x, double[] y) {
/* 548*/    if (x == null || y == null)
/* 549*/      return !(((x == null) ? true : false) ^ ((y == null) ? true : false)); 
/* 551*/    if (x.length != y.length)
/* 552*/      return false; 
/* 554*/    for (int i = 0; i < x.length; i++) {
/* 555*/      if (!equalsIncludingNaN(x[i], y[i]))
/* 556*/        return false; 
/*   0*/    } 
/* 559*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static long factorial(int n) {
/* 585*/    if (n < 0)
/* 586*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, new Object[] { n }); 
/* 590*/    if (n > 20)
/* 591*/      throw new ArithmeticException("factorial value is too large to fit in a long"); 
/* 594*/    return FACTORIALS[n];
/*   0*/  }
/*   0*/  
/*   0*/  public static double factorialDouble(int n) {
/* 618*/    if (n < 0)
/* 619*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, new Object[] { n }); 
/* 623*/    if (n < 21)
/* 624*/      return factorial(n); 
/* 626*/    return FastMath.floor(FastMath.exp(factorialLog(n)) + 0.5D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double factorialLog(int n) {
/* 643*/    if (n < 0)
/* 644*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, new Object[] { n }); 
/* 648*/    if (n < 21)
/* 649*/      return FastMath.log(factorial(n)); 
/* 651*/    double logSum = 0.0D;
/* 652*/    for (int i = 2; i <= n; i++)
/* 653*/      logSum += FastMath.log(i); 
/* 655*/    return logSum;
/*   0*/  }
/*   0*/  
/*   0*/  public static int gcd(int p, int q) {
/* 688*/    int u = p;
/* 689*/    int v = q;
/* 690*/    if (u == 0 || v == 0) {
/* 691*/      if (u == Integer.MIN_VALUE || v == Integer.MIN_VALUE)
/* 692*/        throw MathRuntimeException.createArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q }); 
/* 696*/      return FastMath.abs(u) + FastMath.abs(v);
/*   0*/    } 
/* 703*/    if (u > 0)
/* 704*/      u = -u; 
/* 706*/    if (v > 0)
/* 707*/      v = -v; 
/* 710*/    int k = 0;
/* 711*/    while ((u & 0x1) == 0 && (v & 0x1) == 0 && k < 31) {
/* 713*/      u /= 2;
/* 714*/      v /= 2;
/* 715*/      k++;
/*   0*/    } 
/* 717*/    if (k == 31)
/* 718*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q }); 
/* 724*/    int t = ((u & 0x1) == 1) ? v : -(u / 2);
/*   0*/    while (true) {
/* 730*/      while ((t & 0x1) == 0)
/* 731*/        t /= 2; 
/* 734*/      if (t > 0) {
/* 735*/        u = -t;
/*   0*/      } else {
/* 737*/        v = t;
/*   0*/      } 
/* 740*/      t = (v - u) / 2;
/* 743*/      if (t == 0)
/* 744*/        return -u * (1 << k); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static long gcd(long p, long q) {
/* 777*/    long u = p;
/* 778*/    long v = q;
/* 779*/    if (u == 0L || v == 0L) {
/* 780*/      if (u == Long.MIN_VALUE || v == Long.MIN_VALUE)
/* 781*/        throw MathRuntimeException.createArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS, new Object[] { p, q }); 
/* 785*/      return FastMath.abs(u) + FastMath.abs(v);
/*   0*/    } 
/* 792*/    if (u > 0L)
/* 793*/      u = -u; 
/* 795*/    if (v > 0L)
/* 796*/      v = -v; 
/* 799*/    int k = 0;
/* 800*/    while ((u & 0x1L) == 0L && (v & 0x1L) == 0L && k < 63) {
/* 802*/      u /= 2L;
/* 803*/      v /= 2L;
/* 804*/      k++;
/*   0*/    } 
/* 806*/    if (k == 63)
/* 807*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS, new Object[] { p, q }); 
/* 813*/    long t = ((u & 0x1L) == 1L) ? v : -(u / 2L);
/*   0*/    while (true) {
/* 819*/      while ((t & 0x1L) == 0L)
/* 820*/        t /= 2L; 
/* 823*/      if (t > 0L) {
/* 824*/        u = -t;
/*   0*/      } else {
/* 826*/        v = t;
/*   0*/      } 
/* 829*/      t = (v - u) / 2L;
/* 832*/      if (t == 0L)
/* 833*/        return -u * (1L << k); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static int hash(double value) {
/* 843*/    return new Double(value).hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public static int hash(double[] value) {
/* 854*/    return Arrays.hashCode(value);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte indicator(byte x) {
/* 865*/    return (x >= 0) ? 1 : -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static double indicator(double x) {
/* 877*/    if (Double.isNaN(x))
/* 878*/      return Double.NaN; 
/* 880*/    return (x >= 0.0D) ? 1.0D : -1.0D;
/*   0*/  }
/*   0*/  
/*   0*/  public static float indicator(float x) {
/* 891*/    if (Float.isNaN(x))
/* 892*/      return Float.NaN; 
/* 894*/    return (x >= 0.0F) ? 1.0F : -1.0F;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indicator(int x) {
/* 904*/    return (x >= 0) ? 1 : -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static long indicator(long x) {
/* 914*/    return (x >= 0L) ? 1L : -1L;
/*   0*/  }
/*   0*/  
/*   0*/  public static short indicator(short x) {
/* 925*/    return (x >= 0) ? 1 : -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int lcm(int a, int b) {
/* 952*/    if (a == 0 || b == 0)
/* 953*/      return 0; 
/* 955*/    int lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
/* 956*/    if (lcm == Integer.MIN_VALUE)
/* 957*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.LCM_OVERFLOW_32_BITS, new Object[] { a, b }); 
/* 961*/    return lcm;
/*   0*/  }
/*   0*/  
/*   0*/  public static long lcm(long a, long b) {
/* 987*/    if (a == 0L || b == 0L)
/* 988*/      return 0L; 
/* 990*/    long lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
/* 991*/    if (lcm == Long.MIN_VALUE)
/* 992*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.LCM_OVERFLOW_64_BITS, new Object[] { a, b }); 
/* 996*/    return lcm;
/*   0*/  }
/*   0*/  
/*   0*/  public static double log(double base, double x) {
/*1016*/    return FastMath.log(x) / FastMath.log(base);
/*   0*/  }
/*   0*/  
/*   0*/  public static int mulAndCheck(int x, int y) {
/*1030*/    long m = x * y;
/*1031*/    if (m < -2147483648L || m > 2147483647L)
/*1032*/      throw new ArithmeticException("overflow: mul"); 
/*1034*/    return (int)m;
/*   0*/  }
/*   0*/  
/*   0*/  public static long mulAndCheck(long a, long b) {
/*   0*/    long ret;
/*1049*/    String msg = "overflow: multiply";
/*1050*/    if (a > b) {
/*1052*/      ret = mulAndCheck(b, a);
/*1054*/    } else if (a < 0L) {
/*1055*/      if (b < 0L) {
/*1057*/        if (a >= Long.MAX_VALUE / b) {
/*1058*/          ret = a * b;
/*   0*/        } else {
/*1060*/          throw new ArithmeticException(msg);
/*   0*/        } 
/*1062*/      } else if (b > 0L) {
/*1064*/        if (Long.MIN_VALUE / b <= a) {
/*1065*/          ret = a * b;
/*   0*/        } else {
/*1067*/          throw new ArithmeticException(msg);
/*   0*/        } 
/*   0*/      } else {
/*1072*/        ret = 0L;
/*   0*/      } 
/*1074*/    } else if (a > 0L) {
/*1079*/      if (a <= Long.MAX_VALUE / b) {
/*1080*/        ret = a * b;
/*   0*/      } else {
/*1082*/        throw new ArithmeticException(msg);
/*   0*/      } 
/*   0*/    } else {
/*1086*/      ret = 0L;
/*   0*/    } 
/*1089*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static double scalb(double d, int scaleFactor) {
/*1104*/    if (d == 0.0D || Double.isNaN(d) || Double.isInfinite(d))
/*1105*/      return d; 
/*1109*/    long bits = Double.doubleToLongBits(d);
/*1110*/    long exponent = bits & 0x7FF0000000000000L;
/*1111*/    long rest = bits & 0x800FFFFFFFFFFFFFL;
/*1114*/    long newBits = rest | exponent + (scaleFactor << 52L);
/*1115*/    return Double.longBitsToDouble(newBits);
/*   0*/  }
/*   0*/  
/*   0*/  public static double normalizeAngle(double a, double center) {
/*1139*/    return a - 6.283185307179586D * FastMath.floor((a + Math.PI - center) / 6.283185307179586D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double[] normalizeArray(double[] values, double normalizedSum) throws ArithmeticException, IllegalArgumentException {
/*1165*/    if (Double.isInfinite(normalizedSum))
/*1166*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NORMALIZE_INFINITE, new Object[0]); 
/*1169*/    if (Double.isNaN(normalizedSum))
/*1170*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NORMALIZE_NAN, new Object[0]); 
/*1173*/    double sum = 0.0D;
/*1174*/    int len = values.length;
/*1175*/    double[] out = new double[len];
/*1176*/    for (int i = 0; i < len; i++) {
/*1177*/      if (Double.isInfinite(values[i]))
/*1178*/        throw MathRuntimeException.createArithmeticException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, new Object[] { values[i], i }); 
/*1181*/      if (!Double.isNaN(values[i]))
/*1182*/        sum += values[i]; 
/*   0*/    } 
/*1185*/    if (sum == 0.0D)
/*1186*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ARRAY_SUMS_TO_ZERO, new Object[0]); 
/*1188*/    for (int j = 0; j < len; j++) {
/*1189*/      if (Double.isNaN(values[j])) {
/*1190*/        out[j] = Double.NaN;
/*   0*/      } else {
/*1192*/        out[j] = values[j] * normalizedSum / sum;
/*   0*/      } 
/*   0*/    } 
/*1195*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public static double round(double x, int scale) {
/*1208*/    return round(x, scale, 4);
/*   0*/  }
/*   0*/  
/*   0*/  public static double round(double x, int scale, int roundingMethod) {
/*   0*/    try {
/*1225*/      return new BigDecimal(Double.toString(x)).setScale(scale, roundingMethod).doubleValue();
/*1229*/    } catch (NumberFormatException ex) {
/*1230*/      if (Double.isInfinite(x))
/*1231*/        return x; 
/*1233*/      return Double.NaN;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static float round(float x, int scale) {
/*1248*/    return round(x, scale, 4);
/*   0*/  }
/*   0*/  
/*   0*/  public static float round(float x, int scale, int roundingMethod) {
/*1264*/    float sign = indicator(x);
/*1265*/    float factor = (float)FastMath.pow(10.0D, scale) * sign;
/*1266*/    return (float)roundUnscaled((x * factor), sign, roundingMethod) / factor;
/*   0*/  }
/*   0*/  
/*   0*/  private static double roundUnscaled(double unscaled, double sign, int roundingMethod) {
/*   0*/    double fraction;
/*1283*/    switch (roundingMethod) {
/*   0*/      case 2:
/*1285*/        if (sign == -1.0D) {
/*1286*/          unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
/*   0*/          break;
/*   0*/        } 
/*1288*/        unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
/*   0*/        break;
/*   0*/      case 1:
/*1292*/        unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
/*   0*/        break;
/*   0*/      case 3:
/*1295*/        if (sign == -1.0D) {
/*1296*/          unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
/*   0*/          break;
/*   0*/        } 
/*1298*/        unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
/*   0*/        break;
/*   0*/      case 5:
/*1302*/        unscaled = FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY);
/*1303*/        fraction = unscaled - FastMath.floor(unscaled);
/*1304*/        if (fraction > 0.5D) {
/*1305*/          unscaled = FastMath.ceil(unscaled);
/*   0*/          break;
/*   0*/        } 
/*1307*/        unscaled = FastMath.floor(unscaled);
/*   0*/        break;
/*   0*/      case 6:
/*1312*/        fraction = unscaled - FastMath.floor(unscaled);
/*1313*/        if (fraction > 0.5D) {
/*1314*/          unscaled = FastMath.ceil(unscaled);
/*   0*/          break;
/*   0*/        } 
/*1315*/        if (fraction < 0.5D) {
/*1316*/          unscaled = FastMath.floor(unscaled);
/*   0*/          break;
/*   0*/        } 
/*1319*/        if (FastMath.floor(unscaled) / 2.0D == FastMath.floor(Math.floor(unscaled) / 2.0D)) {
/*1321*/          unscaled = FastMath.floor(unscaled);
/*   0*/          break;
/*   0*/        } 
/*1323*/        unscaled = FastMath.ceil(unscaled);
/*   0*/        break;
/*   0*/      case 4:
/*1329*/        unscaled = FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY);
/*1330*/        fraction = unscaled - FastMath.floor(unscaled);
/*1331*/        if (fraction >= 0.5D) {
/*1332*/          unscaled = FastMath.ceil(unscaled);
/*   0*/          break;
/*   0*/        } 
/*1334*/        unscaled = FastMath.floor(unscaled);
/*   0*/        break;
/*   0*/      case 7:
/*1339*/        if (unscaled != FastMath.floor(unscaled))
/*1340*/          throw new ArithmeticException("Inexact result from rounding"); 
/*   0*/        break;
/*   0*/      case 0:
/*1344*/        unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
/*   0*/        break;
/*   0*/      default:
/*1347*/        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_ROUNDING_METHOD, new Object[] { 
/*1347*/              roundingMethod, "ROUND_CEILING", 2, "ROUND_DOWN", 1, "ROUND_FLOOR", 3, "ROUND_HALF_DOWN", 5, "ROUND_HALF_EVEN", 
/*1347*/              6, "ROUND_HALF_UP", 4, "ROUND_UNNECESSARY", 7, "ROUND_UP", 0 });
/*   0*/    } 
/*1359*/    return unscaled;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte sign(byte x) {
/*1373*/    return (x == 0) ? 0 : ((x > 0) ? 1 : -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static double sign(double x) {
/*1389*/    if (Double.isNaN(x))
/*1390*/      return Double.NaN; 
/*1392*/    return (x == 0.0D) ? 0.0D : ((x > 0.0D) ? 1.0D : -1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static float sign(float x) {
/*1407*/    if (Float.isNaN(x))
/*1408*/      return Float.NaN; 
/*1410*/    return (x == 0.0F) ? 0.0F : ((x > 0.0F) ? 1.0F : -1.0F);
/*   0*/  }
/*   0*/  
/*   0*/  public static int sign(int x) {
/*1424*/    return (x == 0) ? 0 : ((x > 0) ? 1 : -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static long sign(long x) {
/*1438*/    return (x == 0L) ? 0L : ((x > 0L) ? 1L : -1L);
/*   0*/  }
/*   0*/  
/*   0*/  public static short sign(short x) {
/*1453*/    return (x == 0) ? 0 : ((x > 0) ? 1 : -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static double sinh(double x) {
/*1464*/    return (FastMath.exp(x) - FastMath.exp(-x)) / 2.0D;
/*   0*/  }
/*   0*/  
/*   0*/  public static int subAndCheck(int x, int y) {
/*1478*/    long s = x - y;
/*1479*/    if (s < -2147483648L || s > 2147483647L)
/*1480*/      throw MathRuntimeException.createArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { x, y }); 
/*1482*/    return (int)s;
/*   0*/  }
/*   0*/  
/*   0*/  public static long subAndCheck(long a, long b) {
/*   0*/    long ret;
/*1497*/    String msg = "overflow: subtract";
/*1498*/    if (b == Long.MIN_VALUE) {
/*1499*/      if (a < 0L) {
/*1500*/        ret = a - b;
/*   0*/      } else {
/*1502*/        throw new ArithmeticException(msg);
/*   0*/      } 
/*   0*/    } else {
/*1506*/      ret = addAndCheck(a, -b, LocalizedFormats.OVERFLOW_IN_ADDITION);
/*   0*/    } 
/*1508*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static int pow(int k, int e) throws IllegalArgumentException {
/*1521*/    if (e < 0)
/*1522*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1527*/    int result = 1;
/*1528*/    int k2p = k;
/*1529*/    while (e != 0) {
/*1530*/      if ((e & 0x1) != 0)
/*1531*/        result *= k2p; 
/*1533*/      k2p *= k2p;
/*1534*/      e >>= 1;
/*   0*/    } 
/*1537*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static int pow(int k, long e) throws IllegalArgumentException {
/*1551*/    if (e < 0L)
/*1552*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1557*/    int result = 1;
/*1558*/    int k2p = k;
/*1559*/    while (e != 0L) {
/*1560*/      if ((e & 0x1L) != 0L)
/*1561*/        result *= k2p; 
/*1563*/      k2p *= k2p;
/*1564*/      e >>= 1L;
/*   0*/    } 
/*1567*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static long pow(long k, int e) throws IllegalArgumentException {
/*1581*/    if (e < 0)
/*1582*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1587*/    long result = 1L;
/*1588*/    long k2p = k;
/*1589*/    while (e != 0) {
/*1590*/      if ((e & 0x1) != 0)
/*1591*/        result *= k2p; 
/*1593*/      k2p *= k2p;
/*1594*/      e >>= 1;
/*   0*/    } 
/*1597*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static long pow(long k, long e) throws IllegalArgumentException {
/*1611*/    if (e < 0L)
/*1612*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1617*/    long result = 1L;
/*1618*/    long k2p = k;
/*1619*/    while (e != 0L) {
/*1620*/      if ((e & 0x1L) != 0L)
/*1621*/        result *= k2p; 
/*1623*/      k2p *= k2p;
/*1624*/      e >>= 1L;
/*   0*/    } 
/*1627*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger pow(BigInteger k, int e) throws IllegalArgumentException {
/*1641*/    if (e < 0)
/*1642*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1647*/    return k.pow(e);
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger pow(BigInteger k, long e) throws IllegalArgumentException {
/*1661*/    if (e < 0L)
/*1662*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1667*/    BigInteger result = BigInteger.ONE;
/*1668*/    BigInteger k2p = k;
/*1669*/    while (e != 0L) {
/*1670*/      if ((e & 0x1L) != 0L)
/*1671*/        result = result.multiply(k2p); 
/*1673*/      k2p = k2p.multiply(k2p);
/*1674*/      e >>= 1L;
/*   0*/    } 
/*1677*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger pow(BigInteger k, BigInteger e) throws IllegalArgumentException {
/*1691*/    if (e.compareTo(BigInteger.ZERO) < 0)
/*1692*/      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POWER_NEGATIVE_PARAMETERS, new Object[] { k, e }); 
/*1697*/    BigInteger result = BigInteger.ONE;
/*1698*/    BigInteger k2p = k;
/*1699*/    while (!BigInteger.ZERO.equals(e)) {
/*1700*/      if (e.testBit(0))
/*1701*/        result = result.multiply(k2p); 
/*1703*/      k2p = k2p.multiply(k2p);
/*1704*/      e = e.shiftRight(1);
/*   0*/    } 
/*1707*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double distance1(double[] p1, double[] p2) {
/*1719*/    double sum = 0.0D;
/*1720*/    for (int i = 0; i < p1.length; i++)
/*1721*/      sum += FastMath.abs(p1[i] - p2[i]); 
/*1723*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  public static int distance1(int[] p1, int[] p2) {
/*1734*/    int sum = 0;
/*1735*/    for (int i = 0; i < p1.length; i++)
/*1736*/      sum += FastMath.abs(p1[i] - p2[i]); 
/*1738*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  public static double distance(double[] p1, double[] p2) {
/*1749*/    double sum = 0.0D;
/*1750*/    for (int i = 0; i < p1.length; i++) {
/*1751*/      double dp = p1[i] - p2[i];
/*1752*/      sum += dp * dp;
/*   0*/    } 
/*1754*/    return FastMath.sqrt(sum);
/*   0*/  }
/*   0*/  
/*   0*/  public static double distance(int[] p1, int[] p2) {
/*1765*/    double sum = 0.0D;
/*1766*/    for (int i = 0; i < p1.length; i++) {
/*1767*/      double dp = (p1[i] - p2[i]);
/*1768*/      sum += dp * dp;
/*   0*/    } 
/*1770*/    return FastMath.sqrt(sum);
/*   0*/  }
/*   0*/  
/*   0*/  public static double distanceInf(double[] p1, double[] p2) {
/*1781*/    double max = 0.0D;
/*1782*/    for (int i = 0; i < p1.length; i++)
/*1783*/      max = FastMath.max(max, FastMath.abs(p1[i] - p2[i])); 
/*1785*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public static int distanceInf(int[] p1, int[] p2) {
/*1796*/    int max = 0;
/*1797*/    for (int i = 0; i < p1.length; i++)
/*1798*/      max = FastMath.max(max, FastMath.abs(p1[i] - p2[i])); 
/*1800*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public enum OrderDirection {
/*1808*/    INCREASING, DECREASING;
/*   0*/  }
/*   0*/  
/*   0*/  public static void checkOrder(double[] val, OrderDirection dir, boolean strict) {
/*1822*/    double previous = val[0];
/*   0*/    boolean ok = true;
/*1825*/    int max = val.length;
/*1826*/    for (int i = 1; i < max; i++) {
/*1827*/      switch (dir) {
/*   0*/        case INCREASING:
/*1829*/          if (strict) {
/*1830*/            if (val[i] <= previous)
/*1831*/              ok = false; 
/*   0*/            break;
/*   0*/          } 
/*1834*/          if (val[i] < previous)
/*1835*/            ok = false; 
/*   0*/          break;
/*   0*/        case DECREASING:
/*1840*/          if (strict) {
/*1841*/            if (val[i] >= previous)
/*1842*/              ok = false; 
/*   0*/            break;
/*   0*/          } 
/*1845*/          if (val[i] > previous)
/*1846*/            ok = false; 
/*   0*/          break;
/*   0*/        default:
/*1852*/          throw new IllegalArgumentException();
/*   0*/      } 
/*1855*/      if (!ok)
/*1856*/        throw new NonMonotonousSequenceException(val[i], previous, i, dir, strict); 
/*1858*/      previous = val[i];
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static void checkOrder(double[] val) {
/*1869*/    checkOrder(val, OrderDirection.INCREASING, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static double safeNorm(double[] v) {
/*   0*/    double norm;
/*1932*/    double rdwarf = 3.834E-20D;
/*1933*/    double rgiant = 1.304E19D;
/*1934*/    double s1 = 0.0D;
/*1935*/    double s2 = 0.0D;
/*1936*/    double s3 = 0.0D;
/*1937*/    double x1max = 0.0D;
/*1938*/    double x3max = 0.0D;
/*1939*/    double floatn = v.length;
/*1940*/    double agiant = rgiant / floatn;
/*1941*/    for (int i = 0; i < v.length; i++) {
/*1942*/      double xabs = Math.abs(v[i]);
/*1943*/      if (xabs < rdwarf || xabs > agiant) {
/*1944*/        if (xabs > rdwarf) {
/*1945*/          if (xabs > x1max) {
/*1946*/            double r = x1max / xabs;
/*1947*/            s1 = 1.0D + s1 * r * r;
/*1948*/            x1max = xabs;
/*   0*/          } else {
/*1950*/            double r = xabs / x1max;
/*1951*/            s1 += r * r;
/*   0*/          } 
/*1954*/        } else if (xabs > x3max) {
/*1955*/          double r = x3max / xabs;
/*1956*/          s3 = 1.0D + s3 * r * r;
/*1957*/          x3max = xabs;
/*1959*/        } else if (xabs != 0.0D) {
/*1960*/          double r = xabs / x3max;
/*1961*/          s3 += r * r;
/*   0*/        } 
/*   0*/      } else {
/*1966*/        s2 += xabs * xabs;
/*   0*/      } 
/*   0*/    } 
/*1970*/    if (s1 != 0.0D) {
/*1971*/      norm = x1max * Math.sqrt(s1 + s2 / x1max / x1max);
/*1973*/    } else if (s2 == 0.0D) {
/*1974*/      norm = x3max * Math.sqrt(s3);
/*1976*/    } else if (s2 >= x3max) {
/*1977*/      norm = Math.sqrt(s2 * (1.0D + x3max / s2 * x3max * s3));
/*   0*/    } else {
/*1979*/      norm = Math.sqrt(x3max * (s2 / x3max + x3max * s3));
/*   0*/    } 
/*1983*/    return norm;
/*   0*/  }
/*   0*/}
