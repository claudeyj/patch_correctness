/*   0*/package org.jfree.data;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/
/*   0*/public class Range implements Serializable {
/*   0*/  private static final long serialVersionUID = -906333695431863380L;
/*   0*/  
/*   0*/  private double lower;
/*   0*/  
/*   0*/  private double upper;
/*   0*/  
/*   0*/  public strictfp Range(double lower, double upper) {
/*  84*/    if (lower > upper) {
/*  85*/      String msg = "Range(double, double): require lower (" + lower + ") <= upper (" + upper + ").";
/*  87*/      throw new IllegalArgumentException(msg);
/*   0*/    } 
/*  89*/    this.lower = lower;
/*  90*/    this.upper = upper;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp double getLowerBound() {
/*  99*/    return this.lower;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp double getUpperBound() {
/* 108*/    return this.upper;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp double getLength() {
/* 117*/    return this.upper - this.lower;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp double getCentralValue() {
/* 126*/    return this.lower / 2.0D + this.upper / 2.0D;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp boolean contains(double value) {
/* 138*/    return (value >= this.lower && value <= this.upper);
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp boolean intersects(double b0, double b1) {
/* 151*/    if (b0 <= this.lower)
/* 152*/      return (b1 > this.lower); 
/* 155*/    return (b0 < this.upper && b1 >= b0);
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp double constrain(double value) {
/* 168*/    double result = value;
/* 169*/    if (!contains(value))
/* 170*/      if (value > this.upper) {
/* 171*/        result = this.upper;
/* 173*/      } else if (value < this.lower) {
/* 174*/        result = this.lower;
/*   0*/      }  
/* 177*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static strictfp Range combine(Range range1, Range range2) {
/* 197*/    if (range1 == null)
/* 198*/      return range2; 
/* 201*/    if (range2 == null)
/* 202*/      return range1; 
/* 205*/    double l = Math.min(range1.getLowerBound(), range2.getLowerBound());
/* 207*/    double u = Math.max(range1.getUpperBound(), range2.getUpperBound());
/* 209*/    return new Range(l, u);
/*   0*/  }
/*   0*/  
/*   0*/  public static strictfp Range expandToInclude(Range range, double value) {
/* 226*/    if (range == null)
/* 227*/      return new Range(value, value); 
/* 229*/    if (value < range.getLowerBound())
/* 230*/      return new Range(value, range.getUpperBound()); 
/* 232*/    if (value > range.getUpperBound())
/* 233*/      return new Range(range.getLowerBound(), value); 
/* 236*/    return range;
/*   0*/  }
/*   0*/  
/*   0*/  public static strictfp Range expand(Range range, double lowerMargin, double upperMargin) {
/* 253*/    if (range == null)
/* 254*/      throw new IllegalArgumentException("Null 'range' argument."); 
/* 256*/    double length = range.getLength();
/* 257*/    double lower = length * lowerMargin;
/* 258*/    double upper = length * upperMargin;
/* 259*/    return new Range(range.getLowerBound() - lower, range.getUpperBound() + upper);
/*   0*/  }
/*   0*/  
/*   0*/  public static strictfp Range shift(Range base, double delta) {
/* 272*/    return shift(base, delta, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static strictfp Range shift(Range base, double delta, boolean allowZeroCrossing) {
/* 288*/    if (allowZeroCrossing)
/* 289*/      return new Range(base.getLowerBound() + delta, base.getUpperBound() + delta); 
/* 293*/    return new Range(shiftWithNoZeroCrossing(base.getLowerBound(), delta), shiftWithNoZeroCrossing(base.getUpperBound(), delta));
/*   0*/  }
/*   0*/  
/*   0*/  private static strictfp double shiftWithNoZeroCrossing(double value, double delta) {
/* 309*/    if (value > 0.0D)
/* 310*/      return Math.max(value + delta, 0.0D); 
/* 312*/    if (value < 0.0D)
/* 313*/      return Math.min(value + delta, 0.0D); 
/* 316*/    return value + delta;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp boolean equals(Object obj) {
/* 328*/    if (!(obj instanceof Range))
/* 329*/      return false; 
/* 331*/    Range range = (Range)obj;
/* 332*/    if (this.lower != range.lower)
/* 333*/      return false; 
/* 335*/    if (this.upper != range.upper)
/* 336*/      return false; 
/* 338*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp int hashCode() {
/* 349*/    long temp = Double.doubleToLongBits(this.lower);
/* 350*/    int result = (int)(temp ^ temp >>> 32L);
/* 351*/    temp = Double.doubleToLongBits(this.upper);
/* 352*/    result = 29 * result + (int)(temp ^ temp >>> 32L);
/* 353*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public strictfp String toString() {
/* 363*/    return "Range[" + this.lower + "," + this.upper + "]";
/*   0*/  }
/*   0*/}
