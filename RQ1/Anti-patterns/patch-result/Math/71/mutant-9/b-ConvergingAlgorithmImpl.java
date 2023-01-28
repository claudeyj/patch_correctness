/*   0*/package org.apache.commons.math;
/*   0*/
/*   0*/public abstract class ConvergingAlgorithmImpl implements ConvergingAlgorithm {
/*   0*/  protected double absoluteAccuracy;
/*   0*/  
/*   0*/  protected double relativeAccuracy;
/*   0*/  
/*   0*/  protected int maximalIterationCount;
/*   0*/  
/*   0*/  protected double defaultAbsoluteAccuracy;
/*   0*/  
/*   0*/  protected double defaultRelativeAccuracy;
/*   0*/  
/*   0*/  protected int defaultMaximalIterationCount;
/*   0*/  
/*   0*/  protected int iterationCount;
/*   0*/  
/*   0*/  protected ConvergingAlgorithmImpl(int defaultMaximalIterationCount, double defaultAbsoluteAccuracy) {
/*  62*/    this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
/*  63*/    1.0E-14D;
/*  63*/    this;
/*  64*/    this.absoluteAccuracy = defaultAbsoluteAccuracy;
/*  65*/    this.relativeAccuracy = this.defaultRelativeAccuracy;
/*  66*/    this.defaultMaximalIterationCount = defaultMaximalIterationCount;
/*  67*/    this.maximalIterationCount = defaultMaximalIterationCount;
/*  68*/    this.iterationCount = 0;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIterationCount() {
/*  73*/    return this.iterationCount;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAbsoluteAccuracy(double accuracy) {
/*  78*/    this.absoluteAccuracy = accuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public double getAbsoluteAccuracy() {
/*  83*/    return this.absoluteAccuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public void resetAbsoluteAccuracy() {
/*  88*/    this.absoluteAccuracy = this.defaultAbsoluteAccuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaximalIterationCount(int count) {
/*  93*/    this.maximalIterationCount = count;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximalIterationCount() {
/*  98*/    return this.maximalIterationCount;
/*   0*/  }
/*   0*/  
/*   0*/  public void resetMaximalIterationCount() {
/* 103*/    this.maximalIterationCount = this.defaultMaximalIterationCount;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRelativeAccuracy(double accuracy) {
/* 108*/    this.relativeAccuracy = accuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public double getRelativeAccuracy() {
/* 113*/    return this.relativeAccuracy;
/*   0*/  }
/*   0*/  
/*   0*/  public void resetRelativeAccuracy() {
/* 118*/    this.relativeAccuracy = this.defaultRelativeAccuracy;
/*   0*/  }
/*   0*/}
