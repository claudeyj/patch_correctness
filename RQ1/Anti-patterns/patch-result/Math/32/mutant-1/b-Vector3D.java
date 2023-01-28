/*   0*/package org.apache.commons.math3.geometry.euclidean.threed;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.text.NumberFormat;
/*   0*/import org.apache.commons.math3.exception.DimensionMismatchException;
/*   0*/import org.apache.commons.math3.exception.MathArithmeticException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.geometry.Space;
/*   0*/import org.apache.commons.math3.geometry.Vector;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/import org.apache.commons.math3.util.MathArrays;
/*   0*/import org.apache.commons.math3.util.MathUtils;
/*   0*/
/*   0*/public class Vector3D implements Serializable, Vector<Euclidean3D> {
/*  41*/  public static final Vector3D ZERO = new Vector3D(0.0D, 0.0D, 0.0D);
/*   0*/  
/*  44*/  public static final Vector3D PLUS_I = new Vector3D(1.0D, 0.0D, 0.0D);
/*   0*/  
/*  47*/  public static final Vector3D MINUS_I = new Vector3D(-1.0D, 0.0D, 0.0D);
/*   0*/  
/*  50*/  public static final Vector3D PLUS_J = new Vector3D(0.0D, 1.0D, 0.0D);
/*   0*/  
/*  53*/  public static final Vector3D MINUS_J = new Vector3D(0.0D, -1.0D, 0.0D);
/*   0*/  
/*  56*/  public static final Vector3D PLUS_K = new Vector3D(0.0D, 0.0D, 1.0D);
/*   0*/  
/*  59*/  public static final Vector3D MINUS_K = new Vector3D(0.0D, 0.0D, -1.0D);
/*   0*/  
/*  63*/  public static final Vector3D NaN = new Vector3D(Double.NaN, Double.NaN, Double.NaN);
/*   0*/  
/*  67*/  public static final Vector3D POSITIVE_INFINITY = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
/*   0*/  
/*  71*/  public static final Vector3D NEGATIVE_INFINITY = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
/*   0*/  
/*   0*/  private static final long serialVersionUID = 1313493323784566947L;
/*   0*/  
/*   0*/  private final double x;
/*   0*/  
/*   0*/  private final double y;
/*   0*/  
/*   0*/  private final double z;
/*   0*/  
/*   0*/  public Vector3D(double x, double y, double z) {
/*  96*/    this.x = x;
/*  97*/    this.y = y;
/*  98*/    this.z = z;
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D(double[] v) throws DimensionMismatchException {
/* 108*/    if (v.length != 3)
/* 109*/      throw new DimensionMismatchException(v.length, 3); 
/* 111*/    this.x = v[0];
/* 112*/    this.y = v[1];
/* 113*/    this.z = v[2];
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D(double alpha, double delta) {
/* 125*/    double cosDelta = FastMath.cos(delta);
/* 126*/    this.x = FastMath.cos(alpha) * cosDelta;
/* 127*/    this.y = FastMath.sin(alpha) * cosDelta;
/* 128*/    this.z = FastMath.sin(delta);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D(double a, Vector3D u) {
/* 138*/    this.x = a * u.x;
/* 139*/    this.y = a * u.y;
/* 140*/    this.z = a * u.z;
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2) {
/* 152*/    this.x = MathArrays.linearCombination(a1, u1.x, a2, u2.x);
/* 153*/    this.y = MathArrays.linearCombination(a1, u1.y, a2, u2.y);
/* 154*/    this.z = MathArrays.linearCombination(a1, u1.z, a2, u2.z);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2, double a3, Vector3D u3) {
/* 169*/    this.x = MathArrays.linearCombination(a1, u1.x, a2, u2.x, a3, u3.x);
/* 170*/    this.y = MathArrays.linearCombination(a1, u1.y, a2, u2.y, a3, u3.y);
/* 171*/    this.z = MathArrays.linearCombination(a1, u1.z, a2, u2.z, a3, u3.z);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2, double a3, Vector3D u3, double a4, Vector3D u4) {
/* 188*/    this.x = MathArrays.linearCombination(a1, u1.x, a2, u2.x, a3, u3.x, a4, u4.x);
/* 189*/    this.y = MathArrays.linearCombination(a1, u1.y, a2, u2.y, a3, u3.y, a4, u4.y);
/* 190*/    this.z = MathArrays.linearCombination(a1, u1.z, a2, u2.z, a3, u3.z, a4, u4.z);
/*   0*/  }
/*   0*/  
/*   0*/  public double getX() {
/* 198*/    return this.x;
/*   0*/  }
/*   0*/  
/*   0*/  public double getY() {
/* 206*/    return this.y;
/*   0*/  }
/*   0*/  
/*   0*/  public double getZ() {
/* 214*/    return this.z;
/*   0*/  }
/*   0*/  
/*   0*/  public double[] toArray() {
/* 222*/    return new double[] { this.x, this.y, this.z };
/*   0*/  }
/*   0*/  
/*   0*/  public Space getSpace() {
/* 227*/    return Euclidean3D.getInstance();
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D getZero() {
/* 232*/    return ZERO;
/*   0*/  }
/*   0*/  
/*   0*/  public double getNorm1() {
/* 237*/    return FastMath.abs(this.x) + FastMath.abs(this.y) + FastMath.abs(this.z);
/*   0*/  }
/*   0*/  
/*   0*/  public double getNorm() {
/* 243*/    return FastMath.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
/*   0*/  }
/*   0*/  
/*   0*/  public double getNormSq() {
/* 249*/    return this.x * this.x + this.y * this.y + this.z * this.z;
/*   0*/  }
/*   0*/  
/*   0*/  public double getNormInf() {
/* 254*/    return FastMath.max(FastMath.max(FastMath.abs(this.x), FastMath.abs(this.y)), FastMath.abs(this.z));
/*   0*/  }
/*   0*/  
/*   0*/  public double getAlpha() {
/* 262*/    return FastMath.atan2(this.y, this.x);
/*   0*/  }
/*   0*/  
/*   0*/  public double getDelta() {
/* 270*/    return FastMath.asin(this.z / getNorm());
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D add(Vector<Euclidean3D> v) {
/* 275*/    Vector3D v3 = (Vector3D)v;
/* 276*/    return new Vector3D(this.x + v3.x, this.y + v3.y, this.z + v3.z);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D add(double factor, Vector<Euclidean3D> v) {
/* 281*/    return new Vector3D(1.0D, this, factor, (Vector3D)v);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D subtract(Vector<Euclidean3D> v) {
/* 286*/    Vector3D v3 = (Vector3D)v;
/* 287*/    return new Vector3D(this.x - v3.x, this.y - v3.y, this.z - v3.z);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D subtract(double factor, Vector<Euclidean3D> v) {
/* 292*/    return new Vector3D(1.0D, this, -factor, (Vector3D)v);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D normalize() {
/* 297*/    double s = getNorm();
/* 298*/    if (s == 0.0D)
/* 299*/      throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]); 
/* 301*/    return scalarMultiply(1.0D / s);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D orthogonal() {
/* 321*/    double threshold = 0.6D * getNorm();
/* 322*/    if (threshold == 0.0D)
/* 323*/      throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]); 
/* 326*/    if (this.x >= -threshold && this.x <= threshold) {
/* 327*/      double d = 1.0D / FastMath.sqrt(this.y * this.y + this.z * this.z);
/* 328*/      return new Vector3D(0.0D, d * this.z, -d * this.y);
/*   0*/    } 
/* 329*/    if (this.y >= -threshold && this.y <= threshold) {
/* 330*/      double d = 1.0D / FastMath.sqrt(this.x / this.x + this.z * this.z);
/* 331*/      return new Vector3D(-d * this.z, 0.0D, d * this.x);
/*   0*/    } 
/* 333*/    double inverse = 1.0D / FastMath.sqrt(this.x * this.x + this.y * this.y);
/* 334*/    return new Vector3D(inverse * this.y, -inverse * this.x, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static double angle(Vector3D v1, Vector3D v2) {
/* 351*/    double normProduct = v1.getNorm() * v2.getNorm();
/* 352*/    if (normProduct == 0.0D)
/* 353*/      throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]); 
/* 356*/    double dot = v1.dotProduct(v2);
/* 357*/    double threshold = normProduct * 0.9999D;
/* 358*/    if (dot < -threshold || dot > threshold) {
/* 360*/      Vector3D v3 = crossProduct(v1, v2);
/* 361*/      if (dot >= 0.0D)
/* 362*/        return FastMath.asin(v3.getNorm() / normProduct); 
/* 364*/      return Math.PI - FastMath.asin(v3.getNorm() / normProduct);
/*   0*/    } 
/* 368*/    return FastMath.acos(dot / normProduct);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D negate() {
/* 374*/    return new Vector3D(-this.x, -this.y, -this.z);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D scalarMultiply(double a) {
/* 379*/    return new Vector3D(a * this.x, a * this.y, a * this.z);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNaN() {
/* 384*/    return (Double.isNaN(this.x) || Double.isNaN(this.y) || Double.isNaN(this.z));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInfinite() {
/* 389*/    return (!isNaN() && (Double.isInfinite(this.x) || Double.isInfinite(this.y) || Double.isInfinite(this.z)));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object other) {
/* 414*/    if (this == other)
/* 415*/      return true; 
/* 418*/    if (other instanceof Vector3D) {
/* 419*/      Vector3D rhs = (Vector3D)other;
/* 420*/      if (rhs.isNaN())
/* 421*/        return isNaN(); 
/* 424*/      return (this.x == rhs.x && this.y == rhs.y && this.z == rhs.z);
/*   0*/    } 
/* 426*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 438*/    if (isNaN())
/* 439*/      return 642; 
/* 441*/    return 643 * (164 * MathUtils.hash(this.x) + 3 * MathUtils.hash(this.y) + MathUtils.hash(this.z));
/*   0*/  }
/*   0*/  
/*   0*/  public double dotProduct(Vector<Euclidean3D> v) {
/* 453*/    Vector3D v3 = (Vector3D)v;
/* 454*/    return MathArrays.linearCombination(this.x, v3.x, this.y, v3.y, this.z, v3.z);
/*   0*/  }
/*   0*/  
/*   0*/  public Vector3D crossProduct(Vector<Euclidean3D> v) {
/* 462*/    Vector3D v3 = (Vector3D)v;
/* 463*/    return new Vector3D(MathArrays.linearCombination(this.y, v3.z, -this.z, v3.y), MathArrays.linearCombination(this.z, v3.x, -this.x, v3.z), MathArrays.linearCombination(this.x, v3.y, -this.y, v3.x));
/*   0*/  }
/*   0*/  
/*   0*/  public double distance1(Vector<Euclidean3D> v) {
/* 470*/    Vector3D v3 = (Vector3D)v;
/* 471*/    double dx = FastMath.abs(v3.x - this.x);
/* 472*/    double dy = FastMath.abs(v3.y - this.y);
/* 473*/    double dz = FastMath.abs(v3.z - this.z);
/* 474*/    return dx + dy + dz;
/*   0*/  }
/*   0*/  
/*   0*/  public double distance(Vector<Euclidean3D> v) {
/* 479*/    Vector3D v3 = (Vector3D)v;
/* 480*/    double dx = v3.x - this.x;
/* 481*/    double dy = v3.y - this.y;
/* 482*/    double dz = v3.z - this.z;
/* 483*/    return FastMath.sqrt(dx * dx + dy * dy + dz * dz);
/*   0*/  }
/*   0*/  
/*   0*/  public double distanceInf(Vector<Euclidean3D> v) {
/* 488*/    Vector3D v3 = (Vector3D)v;
/* 489*/    double dx = FastMath.abs(v3.x - this.x);
/* 490*/    double dy = FastMath.abs(v3.y - this.y);
/* 491*/    double dz = FastMath.abs(v3.z - this.z);
/* 492*/    return FastMath.max(FastMath.max(dx, dy), dz);
/*   0*/  }
/*   0*/  
/*   0*/  public double distanceSq(Vector<Euclidean3D> v) {
/* 497*/    Vector3D v3 = (Vector3D)v;
/* 498*/    double dx = v3.x - this.x;
/* 499*/    double dy = v3.y - this.y;
/* 500*/    double dz = v3.z - this.z;
/* 501*/    return dx * dx + dy * dy + dz * dz;
/*   0*/  }
/*   0*/  
/*   0*/  public static double dotProduct(Vector3D v1, Vector3D v2) {
/* 510*/    return v1.dotProduct(v2);
/*   0*/  }
/*   0*/  
/*   0*/  public static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
/* 519*/    return v1.crossProduct(v2);
/*   0*/  }
/*   0*/  
/*   0*/  public static double distance1(Vector3D v1, Vector3D v2) {
/* 531*/    return v1.distance1(v2);
/*   0*/  }
/*   0*/  
/*   0*/  public static double distance(Vector3D v1, Vector3D v2) {
/* 543*/    return v1.distance(v2);
/*   0*/  }
/*   0*/  
/*   0*/  public static double distanceInf(Vector3D v1, Vector3D v2) {
/* 555*/    return v1.distanceInf(v2);
/*   0*/  }
/*   0*/  
/*   0*/  public static double distanceSq(Vector3D v1, Vector3D v2) {
/* 567*/    return v1.distanceSq(v2);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 575*/    return Vector3DFormat.getInstance().format(this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(NumberFormat format) {
/* 580*/    return new Vector3DFormat(format).format(this);
/*   0*/  }
/*   0*/}
