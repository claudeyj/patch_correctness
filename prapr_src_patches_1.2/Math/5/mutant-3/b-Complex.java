/*   0*/package org.apache.commons.math3.complex;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math3.Field;
/*   0*/import org.apache.commons.math3.FieldElement;
/*   0*/import org.apache.commons.math3.exception.MathArithmeticException;
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.NullArgumentException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/import org.apache.commons.math3.util.MathUtils;
/*   0*/
/*   0*/public class Complex implements FieldElement<Complex>, Serializable {
/*  59*/  public static final Complex I = new Complex(0.0D, 1.0D);
/*   0*/  
/*  62*/  public static final Complex NaN = new Complex(Double.NaN, Double.NaN);
/*   0*/  
/*  65*/  public static final Complex INF = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
/*   0*/  
/*  67*/  public static final Complex ONE = new Complex(1.0D, 0.0D);
/*   0*/  
/*  69*/  public static final Complex ZERO = new Complex(0.0D, 0.0D);
/*   0*/  
/*   0*/  private static final long serialVersionUID = -6195664516687396620L;
/*   0*/  
/*   0*/  private final double imaginary;
/*   0*/  
/*   0*/  private final double real;
/*   0*/  
/*   0*/  private final transient boolean isNaN;
/*   0*/  
/*   0*/  private final transient boolean isInfinite;
/*   0*/  
/*   0*/  public Complex(double real) {
/*  89*/    this(real, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex(double real, double imaginary) {
/*  99*/    this.real = real;
/* 100*/    this.imaginary = imaginary;
/* 102*/    this.isNaN = (Double.isNaN(real) || Double.isNaN(imaginary));
/* 103*/    this.isInfinite = (!this.isNaN && (Double.isInfinite(real) || Double.isInfinite(imaginary)));
/*   0*/  }
/*   0*/  
/*   0*/  public double abs() {
/* 116*/    if (this.isNaN)
/* 117*/      return Double.NaN; 
/* 119*/    if (isInfinite())
/* 120*/      return Double.POSITIVE_INFINITY; 
/* 122*/    if (FastMath.abs(this.real) < FastMath.abs(this.imaginary)) {
/* 123*/      if (this.imaginary == 0.0D)
/* 124*/        return FastMath.abs(this.real); 
/* 126*/      double d = this.real / this.imaginary;
/* 127*/      return FastMath.abs(this.imaginary) * FastMath.sqrt(1.0D + d * d);
/*   0*/    } 
/* 129*/    if (this.real == 0.0D)
/* 130*/      return FastMath.abs(this.imaginary); 
/* 132*/    double q = this.imaginary / this.real;
/* 133*/    return FastMath.abs(this.real) * FastMath.sqrt(1.0D + q * q);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex add(Complex addend) throws NullArgumentException {
/* 157*/    MathUtils.checkNotNull(addend);
/* 158*/    if (this.isNaN || addend.isNaN)
/* 159*/      return NaN; 
/* 162*/    return createComplex(this.real + addend.getReal(), this.imaginary + addend.getImaginary());
/*   0*/  }
/*   0*/  
/*   0*/  public Complex add(double addend) {
/* 175*/    if (this.isNaN || Double.isNaN(addend))
/* 176*/      return NaN; 
/* 179*/    return createComplex(this.real + addend, this.imaginary);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex conjugate() {
/* 197*/    if (this.isNaN)
/* 198*/      return NaN; 
/* 201*/    return createComplex(this.real, -this.imaginary);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex divide(Complex divisor) throws NullArgumentException {
/* 248*/    MathUtils.checkNotNull(divisor);
/* 249*/    if (this.isNaN || divisor.isNaN)
/* 250*/      return NaN; 
/* 253*/    double c = divisor.getReal();
/* 254*/    double d = divisor.getImaginary();
/* 255*/    if (c == 0.0D && d == 0.0D)
/* 256*/      return NaN; 
/* 259*/    if (divisor.isInfinite() && !isInfinite())
/* 260*/      return ZERO; 
/* 263*/    if (FastMath.abs(c) < FastMath.abs(d)) {
/* 264*/      double d1 = c / d;
/* 265*/      double d2 = c * d1 + d;
/* 266*/      return createComplex((this.real * d1 + this.imaginary) / d2, (this.imaginary * d1 - this.real) / d2);
/*   0*/    } 
/* 269*/    double q = d / c;
/* 270*/    double denominator = d * q + c;
/* 271*/    return createComplex((this.imaginary * q + this.real) / denominator, (this.imaginary - this.real * q) / denominator);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex divide(double divisor) {
/* 285*/    if (this.isNaN || Double.isNaN(divisor))
/* 286*/      return NaN; 
/* 288*/    if (divisor == 0.0D)
/* 289*/      return NaN; 
/* 291*/    if (Double.isInfinite(divisor))
/* 292*/      return !isInfinite() ? ZERO : NaN; 
/* 294*/    return createComplex(this.real / divisor, this.imaginary / divisor);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex reciprocal() {
/* 300*/    if (this.isNaN)
/* 301*/      return NaN; 
/* 304*/    if (this.real == 0.0D && this.imaginary == 0.0D)
/* 305*/      return NaN; 
/* 308*/    if (this.isInfinite)
/* 309*/      return ZERO; 
/* 312*/    if (FastMath.abs(this.real) < FastMath.abs(this.imaginary)) {
/* 313*/      double d1 = this.real / this.imaginary;
/* 314*/      double d2 = 1.0D / (this.real * d1 + this.imaginary);
/* 315*/      return createComplex(d2 * d1, -d2);
/*   0*/    } 
/* 317*/    double q = this.imaginary / this.real;
/* 318*/    double scale = 1.0D / (this.imaginary * q + this.real);
/* 319*/    return createComplex(scale, -scale * q);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object other) {
/* 340*/    if (INF == other)
/* 341*/      return true; 
/* 343*/    if (other instanceof Complex) {
/* 344*/      Complex c = (Complex)other;
/* 345*/      if (c.isNaN)
/* 346*/        return this.isNaN; 
/* 348*/      return (this.real == c.real && this.imaginary == c.imaginary);
/*   0*/    } 
/* 351*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 363*/    if (this.isNaN)
/* 364*/      return 7; 
/* 366*/    return 37 * (17 * MathUtils.hash(this.imaginary) + MathUtils.hash(this.real));
/*   0*/  }
/*   0*/  
/*   0*/  public double getImaginary() {
/* 376*/    return this.imaginary;
/*   0*/  }
/*   0*/  
/*   0*/  public double getReal() {
/* 385*/    return this.real;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNaN() {
/* 396*/    return this.isNaN;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInfinite() {
/* 409*/    return this.isInfinite;
/*   0*/  }
/*   0*/  
/*   0*/  public Complex multiply(Complex factor) throws NullArgumentException {
/* 438*/    MathUtils.checkNotNull(factor);
/* 439*/    if (this.isNaN || factor.isNaN)
/* 440*/      return NaN; 
/* 442*/    if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary) || Double.isInfinite(factor.real) || Double.isInfinite(factor.imaginary))
/* 447*/      return INF; 
/* 449*/    return createComplex(this.real * factor.real - this.imaginary * factor.imaginary, this.real * factor.imaginary + this.imaginary * factor.real);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex multiply(int factor) {
/* 462*/    if (this.isNaN)
/* 463*/      return NaN; 
/* 465*/    if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary))
/* 467*/      return INF; 
/* 469*/    return createComplex(this.real * factor, this.imaginary * factor);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex multiply(double factor) {
/* 481*/    if (this.isNaN || Double.isNaN(factor))
/* 482*/      return NaN; 
/* 484*/    if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary) || Double.isInfinite(factor))
/* 488*/      return INF; 
/* 490*/    return createComplex(this.real * factor, this.imaginary * factor);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex negate() {
/* 501*/    if (this.isNaN)
/* 502*/      return NaN; 
/* 505*/    return createComplex(-this.real, -this.imaginary);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex subtract(Complex subtrahend) throws NullArgumentException {
/* 528*/    MathUtils.checkNotNull(subtrahend);
/* 529*/    if (this.isNaN || subtrahend.isNaN)
/* 530*/      return NaN; 
/* 533*/    return createComplex(this.real - subtrahend.getReal(), this.imaginary - subtrahend.getImaginary());
/*   0*/  }
/*   0*/  
/*   0*/  public Complex subtract(double subtrahend) {
/* 546*/    if (this.isNaN || Double.isNaN(subtrahend))
/* 547*/      return NaN; 
/* 549*/    return createComplex(this.real - subtrahend, this.imaginary);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex acos() {
/* 569*/    if (this.isNaN)
/* 570*/      return NaN; 
/* 573*/    return add(sqrt1z().multiply(I)).log().multiply(I.negate());
/*   0*/  }
/*   0*/  
/*   0*/  public Complex asin() {
/* 593*/    if (this.isNaN)
/* 594*/      return NaN; 
/* 597*/    return sqrt1z().add(multiply(I)).log().multiply(I.negate());
/*   0*/  }
/*   0*/  
/*   0*/  public Complex atan() {
/* 617*/    if (this.isNaN)
/* 618*/      return NaN; 
/* 621*/    return add(I).divide(I.subtract(this)).log().multiply(I.divide(createComplex(2.0D, 0.0D)));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex cos() {
/* 658*/    if (this.isNaN)
/* 659*/      return NaN; 
/* 662*/    return createComplex(FastMath.cos(this.real) * FastMath.cosh(this.imaginary), -FastMath.sin(this.real) * FastMath.sinh(this.imaginary));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex cosh() {
/* 698*/    if (this.isNaN)
/* 699*/      return NaN; 
/* 702*/    return createComplex(FastMath.cosh(this.real) * FastMath.cos(this.imaginary), FastMath.sinh(this.real) * FastMath.sin(this.imaginary));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex exp() {
/* 739*/    if (this.isNaN)
/* 740*/      return NaN; 
/* 743*/    double expReal = FastMath.exp(this.real);
/* 744*/    return createComplex(expReal * FastMath.cos(this.imaginary), expReal * FastMath.sin(this.imaginary));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex log() {
/* 784*/    if (this.isNaN)
/* 785*/      return NaN; 
/* 788*/    return createComplex(FastMath.log(abs()), FastMath.atan2(this.imaginary, this.real));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex pow(Complex x) throws NullArgumentException {
/* 814*/    MathUtils.checkNotNull(x);
/* 815*/    return log().multiply(x).exp();
/*   0*/  }
/*   0*/  
/*   0*/  public Complex pow(double x) {
/* 826*/    return log().multiply(x).exp();
/*   0*/  }
/*   0*/  
/*   0*/  public Complex sin() {
/* 862*/    if (this.isNaN)
/* 863*/      return NaN; 
/* 866*/    return createComplex(FastMath.sin(this.real) * FastMath.cosh(this.imaginary), FastMath.cos(this.real) * FastMath.sinh(this.imaginary));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex sinh() {
/* 902*/    if (this.isNaN)
/* 903*/      return NaN; 
/* 906*/    return createComplex(FastMath.sinh(this.real) * FastMath.cos(this.imaginary), FastMath.cosh(this.real) * FastMath.sin(this.imaginary));
/*   0*/  }
/*   0*/  
/*   0*/  public Complex sqrt() {
/* 945*/    if (this.isNaN)
/* 946*/      return NaN; 
/* 949*/    if (this.real == 0.0D && this.imaginary == 0.0D)
/* 950*/      return createComplex(0.0D, 0.0D); 
/* 953*/    double t = FastMath.sqrt((FastMath.abs(this.real) + abs()) / 2.0D);
/* 954*/    if (this.real >= 0.0D)
/* 955*/      return createComplex(t, this.imaginary / 2.0D * t); 
/* 957*/    return createComplex(FastMath.abs(this.imaginary) / 2.0D * t, FastMath.copySign(1.0D, this.imaginary) * t);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex sqrt1z() {
/* 980*/    return createComplex(1.0D, 0.0D).subtract(multiply(this)).sqrt();
/*   0*/  }
/*   0*/  
/*   0*/  public Complex tan() {
/*1016*/    if (this.isNaN || Double.isInfinite(this.real))
/*1017*/      return NaN; 
/*1019*/    if (this.imaginary > 20.0D)
/*1020*/      return createComplex(0.0D, 1.0D); 
/*1022*/    if (this.imaginary < -20.0D)
/*1023*/      return createComplex(0.0D, -1.0D); 
/*1026*/    double real2 = 2.0D * this.real;
/*1027*/    double imaginary2 = 2.0D * this.imaginary;
/*1028*/    double d = FastMath.cos(real2) + FastMath.cosh(imaginary2);
/*1030*/    return createComplex(FastMath.sin(real2) / d, FastMath.sinh(imaginary2) / d);
/*   0*/  }
/*   0*/  
/*   0*/  public Complex tanh() {
/*1067*/    if (this.isNaN || Double.isInfinite(this.imaginary))
/*1068*/      return NaN; 
/*1070*/    if (this.real > 20.0D)
/*1071*/      return createComplex(1.0D, 0.0D); 
/*1073*/    if (this.real < -20.0D)
/*1074*/      return createComplex(-1.0D, 0.0D); 
/*1076*/    double real2 = 2.0D * this.real;
/*1077*/    double imaginary2 = 2.0D * this.imaginary;
/*1078*/    double d = FastMath.cosh(real2) + FastMath.cos(imaginary2);
/*1080*/    return createComplex(FastMath.sinh(real2) / d, FastMath.sin(imaginary2) / d);
/*   0*/  }
/*   0*/  
/*   0*/  public double getArgument() {
/*1104*/    return FastMath.atan2(getImaginary(), getReal());
/*   0*/  }
/*   0*/  
/*   0*/  public List<Complex> nthRoot(int n) throws NotPositiveException {
/*1131*/    if (n <= 0)
/*1132*/      throw new NotPositiveException(LocalizedFormats.CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N, n); 
/*1136*/    List<Complex> result = new ArrayList<Complex>();
/*1138*/    if (this.isNaN) {
/*1139*/      result.add(NaN);
/*1140*/      return result;
/*   0*/    } 
/*1142*/    if (isInfinite()) {
/*1143*/      result.add(INF);
/*1144*/      return result;
/*   0*/    } 
/*1148*/    double nthRootOfAbs = FastMath.pow(abs(), 1.0D / n);
/*1151*/    double nthPhi = getArgument() / n;
/*1152*/    double slice = 6.283185307179586D / n;
/*1153*/    double innerPart = nthPhi;
/*1154*/    for (int k = 0; k < n; k++) {
/*1156*/      double realPart = nthRootOfAbs * FastMath.cos(innerPart);
/*1157*/      double imaginaryPart = nthRootOfAbs * FastMath.sin(innerPart);
/*1158*/      result.add(createComplex(realPart, imaginaryPart));
/*1159*/      innerPart += slice;
/*   0*/    } 
/*1162*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected Complex createComplex(double realPart, double imaginaryPart) {
/*1176*/    return new Complex(realPart, imaginaryPart);
/*   0*/  }
/*   0*/  
/*   0*/  public static Complex valueOf(double realPart, double imaginaryPart) {
/*1188*/    if (Double.isNaN(realPart) || Double.isNaN(imaginaryPart))
/*1190*/      return NaN; 
/*1192*/    return new Complex(realPart, imaginaryPart);
/*   0*/  }
/*   0*/  
/*   0*/  public static Complex valueOf(double realPart) {
/*1202*/    if (Double.isNaN(realPart))
/*1203*/      return NaN; 
/*1205*/    return new Complex(realPart);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object readResolve() {
/*1217*/    return createComplex(this.real, this.imaginary);
/*   0*/  }
/*   0*/  
/*   0*/  public ComplexField getField() {
/*1222*/    return ComplexField.getInstance();
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1228*/    return "(" + this.real + ", " + this.imaginary + ")";
/*   0*/  }
/*   0*/}
