/*   0*/package org.apache.commons.math3.distribution;
/*   0*/
/*   0*/import java.lang.reflect.Array;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math3.exception.MathArithmeticException;
/*   0*/import org.apache.commons.math3.exception.MathIllegalArgumentException;
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.NotStrictlyPositiveException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.random.RandomGenerator;
/*   0*/import org.apache.commons.math3.random.Well19937c;
/*   0*/import org.apache.commons.math3.util.MathArrays;
/*   0*/import org.apache.commons.math3.util.Pair;
/*   0*/
/*   0*/public class DiscreteDistribution<T> {
/*   0*/  protected final RandomGenerator random;
/*   0*/  
/*   0*/  private final List<T> singletons;
/*   0*/  
/*   0*/  private final double[] probabilities;
/*   0*/  
/*   0*/  public DiscreteDistribution(List<Pair<T, Double>> samples) throws NotPositiveException, MathArithmeticException, MathIllegalArgumentException {
/*  69*/    this(new Well19937c(), samples);
/*   0*/  }
/*   0*/  
/*   0*/  public DiscreteDistribution(RandomGenerator rng, List<Pair<T, Double>> samples) throws NotPositiveException, MathArithmeticException, MathIllegalArgumentException {
/*  87*/    this.random = rng;
/*  89*/    this.singletons = new ArrayList<T>(samples.size());
/*  90*/    double[] probs = new double[samples.size()];
/*  92*/    for (int i = 0; i < samples.size(); i++) {
/*  93*/      Pair<T, Double> sample = samples.get(i);
/*  94*/      this.singletons.add(sample.getKey());
/*  95*/      if ((Double)sample.getValue() < 0.0D)
/*  96*/        throw new NotPositiveException((Number)sample.getValue()); 
/*  98*/      probs[i] = (Double)sample.getValue();
/*   0*/    } 
/* 101*/    this.probabilities = MathArrays.normalizeArray(probs, 1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public void reseedRandomGenerator(long seed) {
/* 110*/    this.random.setSeed(seed);
/*   0*/  }
/*   0*/  
/*   0*/  double probability(T x) {
/* 123*/    double probability = 0.0D;
/* 125*/    for (int i = 0; i < this.probabilities.length; i++) {
/* 126*/      if ((x == null && this.singletons.get(i) == null) || (x != null && x.equals(this.singletons.get(i))))
/* 128*/        probability += this.probabilities[i]; 
/*   0*/    } 
/* 132*/    return probability;
/*   0*/  }
/*   0*/  
/*   0*/  public List<Pair<T, Double>> getSamples() {
/* 142*/    List<Pair<T, Double>> samples = new ArrayList<Pair<T, Double>>(this.probabilities.length);
/* 144*/    for (int i = 0; i < this.probabilities.length; i++)
/* 145*/      samples.add(new Pair<T, Double>(this.singletons.get(i), this.probabilities[i])); 
/* 148*/    return samples;
/*   0*/  }
/*   0*/  
/*   0*/  public T sample() {
/* 157*/    double randomValue = this.random.nextDouble();
/* 158*/    double sum = 0.0D;
/* 160*/    for (int i = 0; i < this.probabilities.length; i++) {
/* 161*/      sum += this.probabilities[i];
/* 162*/      if (randomValue < sum)
/* 163*/        return this.singletons.get(i); 
/*   0*/    } 
/* 170*/    return this.singletons.get(this.singletons.size() - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public T[] sample(int sampleSize) throws NotStrictlyPositiveException {
/* 182*/    if (sampleSize <= 0)
/* 183*/      throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize); 
/* 187*/    T[] out = (T[])Array.newInstance(this.singletons.get(0).getClass(), sampleSize);
/* 189*/    for (int i = 0; i < sampleSize; i++)
/* 190*/      out[i] = sample(); 
/* 193*/    return out;
/*   0*/  }
/*   0*/}
