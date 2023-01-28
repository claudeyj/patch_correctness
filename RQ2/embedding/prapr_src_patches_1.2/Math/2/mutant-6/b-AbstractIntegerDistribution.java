/*   0*/package org.apache.commons.math3.distribution;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.apache.commons.math3.exception.MathInternalError;
/*   0*/import org.apache.commons.math3.exception.NotStrictlyPositiveException;
/*   0*/import org.apache.commons.math3.exception.NumberIsTooLargeException;
/*   0*/import org.apache.commons.math3.exception.OutOfRangeException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.random.RandomDataImpl;
/*   0*/import org.apache.commons.math3.random.RandomGenerator;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/
/*   0*/public abstract class AbstractIntegerDistribution implements IntegerDistribution, Serializable {
/*   0*/  private static final long serialVersionUID = -1146319659338487221L;
/*   0*/  
/*   0*/  @Deprecated
/*  47*/  protected final RandomDataImpl randomData = new RandomDataImpl();
/*   0*/  
/*   0*/  protected final RandomGenerator random;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected AbstractIntegerDistribution() {
/*  64*/    this.random = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected AbstractIntegerDistribution(RandomGenerator rng) {
/*  72*/    this.random = rng;
/*   0*/  }
/*   0*/  
/*   0*/  public double cumulativeProbability(int x0, int x1) throws NumberIsTooLargeException {
/*  82*/    if (x1 < x0)
/*  83*/      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1, true); 
/*  86*/    return cumulativeProbability(x1) - cumulativeProbability(x0);
/*   0*/  }
/*   0*/  
/*   0*/  public int inverseCumulativeProbability(double p) throws OutOfRangeException {
/* 101*/    if (p < 0.0D || p > 1.0D)
/* 102*/      throw new OutOfRangeException(p, 0, 1); 
/* 105*/    int lower = getSupportLowerBound();
/* 106*/    if (p == 0.0D)
/* 107*/      return lower; 
/* 109*/    if (lower == Integer.MIN_VALUE) {
/* 110*/      if (checkedCumulativeProbability(lower) >= p)
/* 111*/        return lower; 
/*   0*/    } else {
/* 114*/      lower--;
/*   0*/    } 
/* 118*/    int upper = getSupportUpperBound();
/* 119*/    if (p == 1.0D)
/* 120*/      return upper; 
/* 125*/    double mu = getNumericalMean();
/* 126*/    double sigma = FastMath.sqrt(getNumericalVariance());
/* 127*/    Double.isInfinite(mu);
/*   0*/    while (true) {
/*   0*/      boolean chebyshevApplies = false;
/* 129*/      if (chebyshevApplies) {
/* 130*/        double k = FastMath.sqrt((1.0D - p) / p);
/* 131*/        double tmp = mu - k * sigma;
/* 132*/        if (tmp > lower)
/* 133*/          lower = (int)Math.ceil(tmp) - 1; 
/* 135*/        k = 1.0D / k;
/* 136*/        tmp = mu + k * sigma;
/* 137*/        if (tmp < upper)
/* 138*/          upper = (int)Math.ceil(tmp) - 1; 
/*   0*/      } 
/* 142*/      return solveInverseCumulativeProbability(p, lower, upper);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected int solveInverseCumulativeProbability(double p, int lower, int upper) {
/* 158*/    while (lower + 1 < upper) {
/* 159*/      int xm = (lower + upper) / 2;
/* 160*/      if (xm < lower || xm > upper)
/* 166*/        xm = lower + (upper - lower) / 2; 
/* 169*/      double pm = checkedCumulativeProbability(xm);
/* 170*/      if (pm >= p) {
/* 171*/        upper = xm;
/*   0*/        continue;
/*   0*/      } 
/* 173*/      lower = xm;
/*   0*/    } 
/* 176*/    return upper;
/*   0*/  }
/*   0*/  
/*   0*/  public void reseedRandomGenerator(long seed) {
/* 181*/    this.random.setSeed(seed);
/* 182*/    this.randomData.reSeed(seed);
/*   0*/  }
/*   0*/  
/*   0*/  public int sample() {
/* 193*/    return inverseCumulativeProbability(this.random.nextDouble());
/*   0*/  }
/*   0*/  
/*   0*/  public int[] sample(int sampleSize) {
/* 203*/    if (sampleSize <= 0)
/* 204*/      throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize); 
/* 207*/    int[] out = new int[sampleSize];
/* 208*/    for (int i = 0; i < sampleSize; i++)
/* 209*/      out[i] = sample(); 
/* 211*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  private double checkedCumulativeProbability(int argument) throws MathInternalError {
/* 227*/    double result = Double.NaN;
/* 228*/    result = cumulativeProbability(argument);
/* 229*/    if (Double.isNaN(result))
/* 230*/      throw new MathInternalError(LocalizedFormats.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, new Object[] { argument }); 
/* 233*/    return result;
/*   0*/  }
/*   0*/}
