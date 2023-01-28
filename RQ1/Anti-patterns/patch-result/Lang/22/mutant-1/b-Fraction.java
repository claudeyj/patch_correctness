/*   0*/package org.apache.commons.lang3.math;
/*   0*/
/*   0*/import java.math.BigInteger;
/*   0*/
/*   0*/public final class Fraction extends Number implements Comparable<Fraction> {
/*   0*/  private static final long serialVersionUID = 65382027393090L;
/*   0*/  
/*  52*/  public static final Fraction ZERO = new Fraction(0, 1);
/*   0*/  
/*  56*/  public static final Fraction ONE = new Fraction(1, 1);
/*   0*/  
/*  60*/  public static final Fraction ONE_HALF = new Fraction(1, 2);
/*   0*/  
/*  64*/  public static final Fraction ONE_THIRD = new Fraction(1, 3);
/*   0*/  
/*  68*/  public static final Fraction TWO_THIRDS = new Fraction(2, 3);
/*   0*/  
/*  72*/  public static final Fraction ONE_QUARTER = new Fraction(1, 4);
/*   0*/  
/*  76*/  public static final Fraction TWO_QUARTERS = new Fraction(2, 4);
/*   0*/  
/*  80*/  public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
/*   0*/  
/*  84*/  public static final Fraction ONE_FIFTH = new Fraction(1, 5);
/*   0*/  
/*  88*/  public static final Fraction TWO_FIFTHS = new Fraction(2, 5);
/*   0*/  
/*  92*/  public static final Fraction THREE_FIFTHS = new Fraction(3, 5);
/*   0*/  
/*  96*/  public static final Fraction FOUR_FIFTHS = new Fraction(4, 5);
/*   0*/  
/*   0*/  private final int numerator;
/*   0*/  
/*   0*/  private final int denominator;
/*   0*/  
/* 111*/  private transient int hashCode = 0;
/*   0*/  
/* 115*/  private transient String toString = null;
/*   0*/  
/* 119*/  private transient String toProperString = null;
/*   0*/  
/*   0*/  private Fraction(int numerator, int denominator) {
/* 130*/    this.numerator = numerator;
/* 131*/    this.denominator = denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public static Fraction getFraction(int numerator, int denominator) {
/* 147*/    if (denominator == 0)
/* 148*/      throw new ArithmeticException("The denominator must not be zero"); 
/* 150*/    if (denominator < 0) {
/* 151*/      if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE)
/* 153*/        throw new ArithmeticException("overflow: can't negate"); 
/* 155*/      numerator = -numerator;
/* 156*/      denominator = -denominator;
/*   0*/    } 
/* 158*/    return new Fraction(numerator, denominator);
/*   0*/  }
/*   0*/  
/*   0*/  public static Fraction getFraction(int whole, int numerator, int denominator) {
/*   0*/    long numeratorValue;
/* 178*/    if (denominator == 0)
/* 179*/      throw new ArithmeticException("The denominator must not be zero"); 
/* 181*/    if (denominator < 0)
/* 182*/      throw new ArithmeticException("The denominator must not be negative"); 
/* 184*/    if (numerator < 0)
/* 185*/      throw new ArithmeticException("The numerator must not be negative"); 
/* 188*/    if (whole < 0) {
/* 189*/      numeratorValue = whole * denominator - numerator;
/*   0*/    } else {
/* 191*/      numeratorValue = whole * denominator + numerator;
/*   0*/    } 
/* 193*/    if (numeratorValue < -2147483648L || numeratorValue > 2147483647L)
/* 195*/      throw new ArithmeticException("Numerator too large to represent as an Integer."); 
/* 197*/    return new Fraction((int)numeratorValue, denominator);
/*   0*/  }
/*   0*/  
/*   0*/  public static Fraction getReducedFraction(int numerator, int denominator) {
/* 215*/    if (denominator == 0)
/* 216*/      throw new ArithmeticException("The denominator must not be zero"); 
/* 218*/    if (numerator == 0)
/* 219*/      return ZERO; 
/* 222*/    if (denominator == Integer.MIN_VALUE && (numerator & 0x1) == 0) {
/* 223*/      numerator /= 2;
/* 223*/      denominator /= 2;
/*   0*/    } 
/* 225*/    if (denominator < 0) {
/* 226*/      if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE)
/* 228*/        throw new ArithmeticException("overflow: can't negate"); 
/* 230*/      numerator = -numerator;
/* 231*/      denominator = -denominator;
/*   0*/    } 
/* 234*/    int gcd = greatestCommonDivisor(numerator, denominator);
/* 235*/    numerator /= gcd;
/* 236*/    denominator /= gcd;
/* 237*/    return new Fraction(numerator, denominator);
/*   0*/  }
/*   0*/  
/*   0*/  public static Fraction getFraction(double value) {
/*   0*/    double delta1;
/* 255*/    int sign = (value < 0.0D) ? -1 : 1;
/* 256*/    value = Math.abs(value);
/* 257*/    if (value > 2.147483647E9D || Double.isNaN(value))
/* 258*/      throw new ArithmeticException("The value must not be greater than Integer.MAX_VALUE or NaN"); 
/* 261*/    int wholeNumber = (int)value;
/* 262*/    value -= wholeNumber;
/* 264*/    int numer0 = 0;
/* 265*/    int denom0 = 1;
/* 266*/    int numer1 = 1;
/* 267*/    int denom1 = 0;
/* 268*/    int numer2 = 0;
/* 269*/    int denom2 = 0;
/* 270*/    int a1 = (int)value;
/* 271*/    int a2 = 0;
/* 272*/    double x1 = 1.0D;
/* 273*/    double x2 = 0.0D;
/* 274*/    double y1 = value - a1;
/* 275*/    double y2 = 0.0D;
/* 276*/    double delta2 = Double.MAX_VALUE;
/* 278*/    int i = 1;
/*   0*/    do {
/* 281*/      delta1 = delta2;
/* 282*/      a2 = (int)(x1 / y1);
/* 283*/      x2 = y1;
/* 284*/      y2 = x1 - a2 * y1;
/* 285*/      numer2 = a1 * numer1 + numer0;
/* 286*/      denom2 = a1 * denom1 + denom0;
/* 287*/      double fraction = numer2 / denom2;
/* 288*/      delta2 = Math.abs(value - fraction);
/* 290*/      a1 = a2;
/* 291*/      x1 = x2;
/* 292*/      y1 = y2;
/* 293*/      numer0 = numer1;
/* 294*/      denom0 = denom1;
/* 295*/      numer1 = numer2;
/* 296*/      denom1 = denom2;
/* 297*/      i++;
/* 299*/    } while (delta1 > delta2 && denom2 <= 10000 && denom2 > 0 && i < 25);
/* 300*/    if (i == 25)
/* 301*/      throw new ArithmeticException("Unable to convert double to fraction"); 
/* 303*/    return getReducedFraction((numer0 + wholeNumber * denom0) * sign, denom0);
/*   0*/  }
/*   0*/  
/*   0*/  public static Fraction getFraction(String str) {
/* 325*/    if (str == null)
/* 326*/      throw new IllegalArgumentException("The string must not be null"); 
/* 329*/    int pos = str.indexOf('.');
/* 330*/    if (pos >= 0)
/* 331*/      return getFraction(Double.parseDouble(str)); 
/* 335*/    pos = str.indexOf(' ');
/* 336*/    if (pos > 0) {
/* 337*/      int whole = Integer.parseInt(str.substring(0, pos));
/* 338*/      str = str.substring(pos + 1);
/* 339*/      pos = str.indexOf('/');
/* 340*/      if (pos < 0)
/* 341*/        throw new NumberFormatException("The fraction could not be parsed as the format X Y/Z"); 
/* 343*/      int i = Integer.parseInt(str.substring(0, pos));
/* 344*/      int j = Integer.parseInt(str.substring(pos + 1));
/* 345*/      return getFraction(whole, i, j);
/*   0*/    } 
/* 350*/    pos = str.indexOf('/');
/* 351*/    if (pos < 0)
/* 353*/      return getFraction(Integer.parseInt(str), 1); 
/* 355*/    int numer = Integer.parseInt(str.substring(0, pos));
/* 356*/    int denom = Integer.parseInt(str.substring(pos + 1));
/* 357*/    return getFraction(numer, denom);
/*   0*/  }
/*   0*/  
/*   0*/  public int getNumerator() {
/* 373*/    return this.numerator;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDenominator() {
/* 382*/    return this.denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public int getProperNumerator() {
/* 397*/    return Math.abs(this.numerator % this.denominator);
/*   0*/  }
/*   0*/  
/*   0*/  public int getProperWhole() {
/* 412*/    return this.numerator / this.denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public int intValue() {
/* 426*/    return this.numerator / this.denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public long longValue() {
/* 437*/    return this.numerator / this.denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public float floatValue() {
/* 448*/    return this.numerator / this.denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public double doubleValue() {
/* 459*/    return this.numerator / this.denominator;
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction reduce() {
/* 475*/    if (this.numerator == 0)
/* 476*/      return equals(ZERO) ? this : ZERO; 
/* 478*/    int gcd = greatestCommonDivisor(Math.abs(this.numerator), this.denominator);
/* 479*/    if (gcd == 1)
/* 480*/      return this; 
/* 482*/    return getFraction(this.numerator / gcd, this.denominator / gcd);
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction invert() {
/* 495*/    if (this.numerator == 0)
/* 496*/      throw new ArithmeticException("Unable to invert zero."); 
/* 498*/    if (this.numerator == Integer.MIN_VALUE)
/* 499*/      throw new ArithmeticException("overflow: can't negate numerator"); 
/* 501*/    if (this.numerator < 0)
/* 502*/      return new Fraction(-this.denominator, -this.numerator); 
/* 504*/    return new Fraction(this.denominator, this.numerator);
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction negate() {
/* 517*/    if (this.numerator == Integer.MIN_VALUE)
/* 518*/      throw new ArithmeticException("overflow: too large to negate"); 
/* 520*/    return new Fraction(-this.numerator, this.denominator);
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction abs() {
/* 533*/    if (this.numerator >= 0)
/* 534*/      return this; 
/* 536*/    return negate();
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction pow(int power) {
/* 552*/    if (power == 1)
/* 553*/      return this; 
/* 554*/    if (power == 0)
/* 555*/      return ONE; 
/* 556*/    if (power < 0) {
/* 557*/      if (power == Integer.MIN_VALUE)
/* 558*/        return invert().pow(2).pow(-(power / 2)); 
/* 560*/      return invert().pow(-power);
/*   0*/    } 
/* 562*/    Fraction f = multiplyBy(this);
/* 563*/    if (power % 2 == 0)
/* 564*/      return f.pow(power / 2); 
/* 566*/    return f.pow(power / 2).multiplyBy(this);
/*   0*/  }
/*   0*/  
/*   0*/  private static int greatestCommonDivisor(int u, int v) {
/* 584*/    true;
/* 584*/    if (Math.abs(v) <= 1)
/* 585*/      return 1; 
/* 591*/    if (u > 0)
/* 591*/      u = -u; 
/* 592*/    if (v > 0)
/* 592*/      v = -v; 
/* 594*/    int k = 0;
/* 595*/    while ((u & 0x1) == 0 && (v & 0x1) == 0 && k < 31) {
/* 596*/      u /= 2;
/* 596*/      v /= 2;
/* 596*/      k++;
/*   0*/    } 
/* 598*/    if (k == 31)
/* 599*/      throw new ArithmeticException("overflow: gcd is 2^31"); 
/* 603*/    int t = ((u & 0x1) == 1) ? v : -(u / 2);
/*   0*/    while (true) {
/* 609*/      while ((t & 0x1) == 0)
/* 610*/        t /= 2; 
/* 613*/      if (t > 0) {
/* 614*/        u = -t;
/*   0*/      } else {
/* 616*/        v = t;
/*   0*/      } 
/* 619*/      t = (v - u) / 2;
/* 622*/      if (t == 0)
/* 623*/        return -u * (1 << k); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static int mulAndCheck(int x, int y) {
/* 639*/    long m = x * y;
/* 640*/    if (m < -2147483648L || m > 2147483647L)
/* 642*/      throw new ArithmeticException("overflow: mul"); 
/* 644*/    return (int)m;
/*   0*/  }
/*   0*/  
/*   0*/  private static int mulPosAndCheck(int x, int y) {
/* 658*/    long m = x * y;
/* 659*/    if (m > 2147483647L)
/* 660*/      throw new ArithmeticException("overflow: mulPos"); 
/* 662*/    return (int)m;
/*   0*/  }
/*   0*/  
/*   0*/  private static int addAndCheck(int x, int y) {
/* 675*/    long s = x + y;
/* 676*/    if (s < -2147483648L || s > 2147483647L)
/* 678*/      throw new ArithmeticException("overflow: add"); 
/* 680*/    return (int)s;
/*   0*/  }
/*   0*/  
/*   0*/  private static int subAndCheck(int x, int y) {
/* 693*/    long s = x - y;
/* 694*/    if (s < -2147483648L || s > 2147483647L)
/* 696*/      throw new ArithmeticException("overflow: add"); 
/* 698*/    return (int)s;
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction add(Fraction fraction) {
/* 712*/    return addSub(fraction, true);
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction subtract(Fraction fraction) {
/* 726*/    return addSub(fraction, false);
/*   0*/  }
/*   0*/  
/*   0*/  private Fraction addSub(Fraction fraction, boolean isAdd) {
/* 740*/    if (fraction == null)
/* 741*/      throw new IllegalArgumentException("The fraction must not be null"); 
/* 744*/    if (this.numerator == 0)
/* 745*/      return isAdd ? fraction : fraction.negate(); 
/* 747*/    if (fraction.numerator == 0)
/* 748*/      return this; 
/* 752*/    int d1 = greatestCommonDivisor(this.denominator, fraction.denominator);
/* 753*/    if (d1 == 1) {
/* 755*/      int i = mulAndCheck(this.numerator, fraction.denominator);
/* 756*/      int j = mulAndCheck(fraction.numerator, this.denominator);
/* 757*/      return new Fraction(isAdd ? addAndCheck(i, j) : subAndCheck(i, j), mulPosAndCheck(this.denominator, fraction.denominator));
/*   0*/    } 
/* 764*/    BigInteger uvp = BigInteger.valueOf(this.numerator).multiply(BigInteger.valueOf((fraction.denominator / d1)));
/* 766*/    BigInteger upv = BigInteger.valueOf(fraction.numerator).multiply(BigInteger.valueOf((this.denominator / d1)));
/* 768*/    BigInteger t = isAdd ? uvp.add(upv) : uvp.subtract(upv);
/* 771*/    int tmodd1 = t.mod(BigInteger.valueOf(d1)).intValue();
/* 772*/    int d2 = (tmodd1 == 0) ? d1 : greatestCommonDivisor(tmodd1, d1);
/* 775*/    BigInteger w = t.divide(BigInteger.valueOf(d2));
/* 776*/    if (w.bitLength() > 31)
/* 777*/      throw new ArithmeticException("overflow: numerator too large after multiply"); 
/* 780*/    return new Fraction(w.intValue(), mulPosAndCheck(this.denominator / d1, fraction.denominator / d2));
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction multiplyBy(Fraction fraction) {
/* 796*/    if (fraction == null)
/* 797*/      throw new IllegalArgumentException("The fraction must not be null"); 
/* 799*/    if (this.numerator == 0 || fraction.numerator == 0)
/* 800*/      return ZERO; 
/* 804*/    int d1 = greatestCommonDivisor(this.numerator, fraction.denominator);
/* 805*/    int d2 = greatestCommonDivisor(fraction.numerator, this.denominator);
/* 806*/    return getReducedFraction(mulAndCheck(this.numerator / d1, fraction.numerator / d2), mulPosAndCheck(this.denominator / d2, fraction.denominator / d1));
/*   0*/  }
/*   0*/  
/*   0*/  public Fraction divideBy(Fraction fraction) {
/* 822*/    if (fraction == null)
/* 823*/      throw new IllegalArgumentException("The fraction must not be null"); 
/* 825*/    if (fraction.numerator == 0)
/* 826*/      throw new ArithmeticException("The fraction to divide by must not be zero"); 
/* 828*/    return multiplyBy(fraction.invert());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 844*/    if (obj == this)
/* 845*/      return true; 
/* 847*/    if (!(obj instanceof Fraction))
/* 848*/      return false; 
/* 850*/    Fraction other = (Fraction)obj;
/* 851*/    return (getNumerator() == other.getNumerator() && getDenominator() == other.getDenominator());
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 862*/    if (this.hashCode == 0)
/* 864*/      this.hashCode = 37 * (629 + getNumerator()) + getDenominator(); 
/* 866*/    return this.hashCode;
/*   0*/  }
/*   0*/  
/*   0*/  public int compareTo(Fraction other) {
/* 882*/    if (this == other)
/* 883*/      return 0; 
/* 885*/    if (this.numerator == other.numerator && this.denominator == other.denominator)
/* 886*/      return 0; 
/* 890*/    long first = this.numerator * other.denominator;
/* 891*/    long second = other.numerator * this.denominator;
/* 892*/    if (first == second)
/* 893*/      return 0; 
/* 894*/    if (first < second)
/* 895*/      return -1; 
/* 897*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 910*/    if (this.toString == null)
/* 911*/      this.toString = new StringBuilder(32).append(getNumerator()).append('/').append(getDenominator()).toString(); 
/* 916*/    return this.toString;
/*   0*/  }
/*   0*/  
/*   0*/  public String toProperString() {
/* 929*/    if (this.toProperString == null)
/* 930*/      if (this.numerator == 0) {
/* 931*/        this.toProperString = "0";
/* 932*/      } else if (this.numerator == this.denominator) {
/* 933*/        this.toProperString = "1";
/* 934*/      } else if (this.numerator == -1 * this.denominator) {
/* 935*/        this.toProperString = "-1";
/* 936*/      } else if (((this.numerator > 0) ? -this.numerator : this.numerator) < -this.denominator) {
/* 941*/        int properNumerator = getProperNumerator();
/* 942*/        if (properNumerator == 0) {
/* 943*/          this.toProperString = Integer.toString(getProperWhole());
/*   0*/        } else {
/* 945*/          this.toProperString = new StringBuilder(32).append(getProperWhole()).append(' ').append(properNumerator).append('/').append(getDenominator()).toString();
/*   0*/        } 
/*   0*/      } else {
/* 951*/        this.toProperString = new StringBuilder(32).append(getNumerator()).append('/').append(getDenominator()).toString();
/*   0*/      }  
/* 956*/    return this.toProperString;
/*   0*/  }
/*   0*/}
