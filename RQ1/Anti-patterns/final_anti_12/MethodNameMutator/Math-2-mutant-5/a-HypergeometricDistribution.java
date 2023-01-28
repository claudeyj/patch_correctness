/*   0*/package org.apache.commons.math3.distribution;
/*   0*/
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.NotStrictlyPositiveException;
/*   0*/import org.apache.commons.math3.exception.NumberIsTooLargeException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.random.RandomGenerator;
/*   0*/import org.apache.commons.math3.random.Well19937c;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/
/*   0*/public class HypergeometricDistribution extends AbstractIntegerDistribution {
/*   0*/  private static final long serialVersionUID = -436928820673516179L;
/*   0*/  
/*   0*/  private final int numberOfSuccesses;
/*   0*/  
/*   0*/  private final int populationSize;
/*   0*/  
/*   0*/  private final int sampleSize;
/*   0*/  
/*  45*/  private double numericalVariance = Double.NaN;
/*   0*/  
/*   0*/  private boolean numericalVarianceIsCalculated = false;
/*   0*/  
/*   0*/  public HypergeometricDistribution(int populationSize, int numberOfSuccesses, int sampleSize) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
/*  63*/    this(new Well19937c(), populationSize, numberOfSuccesses, sampleSize);
/*   0*/  }
/*   0*/  
/*   0*/  public HypergeometricDistribution(RandomGenerator rng, int populationSize, int numberOfSuccesses, int sampleSize) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
/*  84*/    super(rng);
/*  86*/    if (populationSize <= 0)
/*  87*/      throw new NotStrictlyPositiveException(LocalizedFormats.POPULATION_SIZE, populationSize); 
/*  90*/    if (numberOfSuccesses < 0)
/*  91*/      throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SUCCESSES, numberOfSuccesses); 
/*  94*/    if (sampleSize < 0)
/*  95*/      throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize); 
/*  99*/    if (numberOfSuccesses > populationSize)
/* 100*/      throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, numberOfSuccesses, populationSize, true); 
/* 103*/    if (sampleSize > populationSize)
/* 104*/      throw new NumberIsTooLargeException(LocalizedFormats.SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE, sampleSize, populationSize, true); 
/* 108*/    this.numberOfSuccesses = numberOfSuccesses;
/* 109*/    this.populationSize = populationSize;
/* 110*/    this.sampleSize = sampleSize;
/*   0*/  }
/*   0*/  
/*   0*/  public double cumulativeProbability(int x) {
/*   0*/    double ret;
/* 117*/    int[] domain = getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
/* 118*/    if (x < domain[0]) {
/* 119*/      ret = 0.0D;
/* 120*/    } else if (x >= domain[1]) {
/* 121*/      ret = 1.0D;
/*   0*/    } else {
/* 123*/      ret = innerCumulativeProbability(domain[0], x, 1);
/*   0*/    } 
/* 126*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getDomain(int n, int m, int k) {
/* 139*/    return new int[] { getLowerDomain(n, m, k), getUpperDomain(m, k) };
/*   0*/  }
/*   0*/  
/*   0*/  private int getLowerDomain(int n, int m, int k) {
/* 152*/    return FastMath.max(0, m - n - k);
/*   0*/  }
/*   0*/  
/*   0*/  public int getNumberOfSuccesses() {
/* 161*/    return this.numberOfSuccesses;
/*   0*/  }
/*   0*/  
/*   0*/  public int getPopulationSize() {
/* 170*/    return this.populationSize;
/*   0*/  }
/*   0*/  
/*   0*/  public int getSampleSize() {
/* 179*/    return this.sampleSize;
/*   0*/  }
/*   0*/  
/*   0*/  private int getUpperDomain(int m, int k) {
/* 191*/    return FastMath.min(k, m);
/*   0*/  }
/*   0*/  
/*   0*/  public double probability(int x) {
/*   0*/    double ret;
/* 198*/    int[] domain = getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
/* 199*/    if (x < domain[0] || x > domain[1]) {
/* 200*/      ret = 0.0D;
/*   0*/    } else {
/* 202*/      double p = this.sampleSize / this.populationSize;
/* 203*/      double q = (this.populationSize - this.sampleSize) / this.populationSize;
/* 204*/      double p1 = SaddlePointExpansion.logBinomialProbability(x, this.numberOfSuccesses, p, q);
/* 206*/      double p2 = SaddlePointExpansion.logBinomialProbability(this.sampleSize - x, this.populationSize - this.numberOfSuccesses, p, q);
/* 209*/      double p3 = SaddlePointExpansion.logBinomialProbability(this.sampleSize, this.populationSize, p, q);
/* 211*/      ret = FastMath.exp(p1 + p2 - p3);
/*   0*/    } 
/* 214*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public double upperCumulativeProbability(int x) {
/*   0*/    double ret;
/* 227*/    int[] domain = getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
/* 228*/    if (x <= domain[0]) {
/* 229*/      ret = 1.0D;
/* 230*/    } else if (x > domain[1]) {
/* 231*/      ret = 0.0D;
/*   0*/    } else {
/* 233*/      ret = innerCumulativeProbability(domain[1], x, -1);
/*   0*/    } 
/* 236*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private double innerCumulativeProbability(int x0, int x1, int dx) {
/* 253*/    double ret = probability(x0);
/* 254*/    while (x0 != x1) {
/* 255*/      x0 += dx;
/* 256*/      ret += probability(x0);
/*   0*/    } 
/* 258*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public double getNumericalMean() {
/* 268*/    return (getSampleSize() * getNumberOfSuccesses()) / getPopulationSize();
/*   0*/  }
/*   0*/  
/*   0*/  public double getNumericalVariance() {
/* 279*/    if (!this.numericalVarianceIsCalculated) {
/* 280*/      this.numericalVariance = calculateNumericalVariance();
/* 281*/      this.numericalVarianceIsCalculated = true;
/*   0*/    } 
/* 283*/    return this.numericalVariance;
/*   0*/  }
/*   0*/  
/*   0*/  protected double calculateNumericalVariance() {
/* 292*/    double N = getPopulationSize();
/* 293*/    double m = getNumberOfSuccesses();
/* 294*/    double n = getSampleSize();
/* 295*/    return n * m * (N - n) * (N - m) / N * N * (N - 1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSupportLowerBound() {
/* 308*/    return FastMath.max(0, getSampleSize() + getNumberOfSuccesses() - getPopulationSize());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSupportUpperBound() {
/* 321*/    return FastMath.min(getNumberOfSuccesses(), getSampleSize());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSupportConnected() {
/* 332*/    return true;
/*   0*/  }
/*   0*/}
