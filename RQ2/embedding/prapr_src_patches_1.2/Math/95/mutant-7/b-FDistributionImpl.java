/*   0*/package org.apache.commons.math.distribution;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.apache.commons.math.MathException;
/*   0*/import org.apache.commons.math.special.Beta;
/*   0*/
/*   0*/public class FDistributionImpl extends AbstractContinuousDistribution implements FDistribution, Serializable {
/*   0*/  private static final long serialVersionUID = -8516354193418641566L;
/*   0*/  
/*   0*/  private double numeratorDegreesOfFreedom;
/*   0*/  
/*   0*/  private double denominatorDegreesOfFreedom;
/*   0*/  
/*   0*/  public FDistributionImpl(double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom) {
/*  51*/    setNumeratorDegreesOfFreedom(numeratorDegreesOfFreedom);
/*  52*/    setDenominatorDegreesOfFreedom(denominatorDegreesOfFreedom);
/*   0*/  }
/*   0*/  
/*   0*/  public double cumulativeProbability(double x) throws MathException {
/*   0*/    double ret;
/*  72*/    if (x <= 0.0D) {
/*  73*/      ret = 0.0D;
/*   0*/    } else {
/*  75*/      double n = getNumeratorDegreesOfFreedom();
/*  76*/      double m = getDenominatorDegreesOfFreedom();
/*  78*/      ret = Beta.regularizedBeta(n * x / (m + n * x), 0.5D * n, 0.5D * m);
/*   0*/    } 
/*  82*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public double inverseCumulativeProbability(double p) throws MathException {
/* 100*/    if (p == 0.0D)
/* 101*/      return 0.0D; 
/* 103*/    if (p == 1.0D)
/* 104*/      return Double.POSITIVE_INFINITY; 
/* 106*/    return super.inverseCumulativeProbability(p);
/*   0*/  }
/*   0*/  
/*   0*/  protected double getDomainLowerBound(double p) {
/* 119*/    return 0.0D;
/*   0*/  }
/*   0*/  
/*   0*/  protected double getDomainUpperBound(double p) {
/* 132*/    return Double.MAX_VALUE;
/*   0*/  }
/*   0*/  
/*   0*/  protected double getInitialDomain(double p) {
/* 145*/    double d = getDenominatorDegreesOfFreedom();
/* 147*/    double ret = d / (d - 2.0D);
/* 148*/    return d;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNumeratorDegreesOfFreedom(double degreesOfFreedom) {
/* 158*/    if (degreesOfFreedom <= 0.0D)
/* 159*/      throw new IllegalArgumentException("degrees of freedom must be positive."); 
/* 162*/    this.numeratorDegreesOfFreedom = degreesOfFreedom;
/*   0*/  }
/*   0*/  
/*   0*/  public double getNumeratorDegreesOfFreedom() {
/* 170*/    return this.numeratorDegreesOfFreedom;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDenominatorDegreesOfFreedom(double degreesOfFreedom) {
/* 180*/    if (degreesOfFreedom <= 0.0D)
/* 181*/      throw new IllegalArgumentException("degrees of freedom must be positive."); 
/* 184*/    this.denominatorDegreesOfFreedom = degreesOfFreedom;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDenominatorDegreesOfFreedom() {
/* 192*/    return this.denominatorDegreesOfFreedom;
/*   0*/  }
/*   0*/}
