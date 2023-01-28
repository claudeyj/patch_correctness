/*   0*/package org.apache.commons.math.distribution;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.apache.commons.math.ConvergenceException;
/*   0*/import org.apache.commons.math.MathException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.exception.NotStrictlyPositiveException;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math.special.Erf;
/*   0*/import org.apache.commons.math.util.FastMath;
/*   0*/
/*   0*/public class NormalDistributionImpl extends AbstractContinuousDistribution implements NormalDistribution, Serializable {
/*   0*/  public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9D;
/*   0*/  
/*   0*/  private static final long serialVersionUID = 8589540077390120676L;
/*   0*/  
/*  45*/  private static final double SQRT2PI = FastMath.sqrt(6.283185307179586D);
/*   0*/  
/*   0*/  private final double mean;
/*   0*/  
/*   0*/  private final double standardDeviation;
/*   0*/  
/*   0*/  private final double solverAbsoluteAccuracy;
/*   0*/  
/*   0*/  public NormalDistributionImpl(double mean, double sd) {
/*  60*/    this(mean, sd, 1.0E-9D);
/*   0*/  }
/*   0*/  
/*   0*/  public NormalDistributionImpl(double mean, double sd, double inverseCumAccuracy) {
/*  74*/    if (sd <= 0.0D)
/*  75*/      throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, sd); 
/*  78*/    this.mean = mean;
/*  79*/    this.standardDeviation = sd;
/*  80*/    this.solverAbsoluteAccuracy = inverseCumAccuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public NormalDistributionImpl() {
/*  88*/    this(0.0D, 1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public double getMean() {
/*  95*/    return this.mean;
/*   0*/  }
/*   0*/  
/*   0*/  public double getStandardDeviation() {
/* 102*/    return this.standardDeviation;
/*   0*/  }
/*   0*/  
/*   0*/  public double density(double x) {
/* 110*/    double x0 = x - this.mean;
/* 111*/    double x1 = x0 / this.standardDeviation;
/* 112*/    return FastMath.exp(-0.5D * x1 * x1) / this.standardDeviation * SQRT2PI;
/*   0*/  }
/*   0*/  
/*   0*/  public double cumulativeProbability(double x) throws MathException {
/* 125*/    double dev = x - this.mean;
/*   0*/    try {
/* 127*/      return 0.5D * (1.0D + Erf.erf(dev / this.standardDeviation * FastMath.sqrt(2.0D)));
/* 129*/    } catch (ConvergenceException ex) {
/* 130*/      if (x < this.mean - 20.0D * this.standardDeviation)
/* 131*/        return 0.0D; 
/* 132*/      if (x > this.mean + 20.0D * this.standardDeviation)
/* 133*/        return 1.0D; 
/* 135*/      throw ex;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected double getSolverAbsoluteAccuracy() {
/* 149*/    return this.solverAbsoluteAccuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public double inverseCumulativeProbability(double p) throws MathException {
/* 168*/    if (p == 0.0D)
/* 169*/      return Double.NEGATIVE_INFINITY; 
/* 171*/    if (p == 1.0D)
/* 172*/      return Double.POSITIVE_INFINITY; 
/* 174*/    return super.inverseCumulativeProbability(p);
/*   0*/  }
/*   0*/  
/*   0*/  public double sample() throws MathException {
/* 186*/    return this.randomData.nextGaussian(this.mean, this.standardDeviation);
/*   0*/  }
/*   0*/  
/*   0*/  protected double getDomainLowerBound(double p) {
/*   0*/    double ret;
/* 201*/    if (p < 0.5D) {
/* 202*/      ret = -1.7976931348623157E308D;
/*   0*/    } else {
/* 204*/      ret = this.mean;
/*   0*/    } 
/* 207*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  protected double getDomainUpperBound(double p) {
/*   0*/    double ret;
/* 222*/    if (p < 0.5D) {
/* 223*/      ret = this.mean;
/*   0*/    } else {
/* 225*/      ret = Double.MAX_VALUE;
/*   0*/    } 
/* 228*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  protected double getInitialDomain(double p) {
/*   0*/    double ret;
/* 243*/    if (p < 0.5D) {
/* 244*/      ret = this.mean - this.standardDeviation;
/* 245*/    } else if (p > 0.5D) {
/* 246*/      ret = this.mean + this.standardDeviation;
/*   0*/    } else {
/* 248*/      ret = this.mean;
/*   0*/    } 
/* 251*/    return ret;
/*   0*/  }
/*   0*/}
