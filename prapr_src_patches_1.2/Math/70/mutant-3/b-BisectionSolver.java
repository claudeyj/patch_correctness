/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/
/*   0*/public class BisectionSolver extends UnivariateRealSolverImpl {
/*   0*/  @Deprecated
/*   0*/  public BisectionSolver(UnivariateRealFunction f) {
/*  44*/    super(f, 100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  public BisectionSolver() {
/*  52*/    super(100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  59*/    return solve(this.f, min, max);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  66*/    return solve(this.f, min, max);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  72*/    double d2 = max, d1 = min;
/*  72*/    return solve(f, d1, d2);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  79*/    clearResult();
/*  80*/    verifyInterval(min, max);
/*  85*/    int i = 0;
/*  86*/    while (i < this.maximalIterationCount) {
/*  87*/      double m = UnivariateRealSolverUtils.midpoint(min, max);
/*  88*/      double fmin = f.value(min);
/*  89*/      double fm = f.value(m);
/*  91*/      if (fm * fmin > 0.0D) {
/*  93*/        min = m;
/*   0*/      } else {
/*  96*/        max = m;
/*   0*/      } 
/*  99*/      if (Math.abs(max - min) <= this.absoluteAccuracy) {
/* 100*/        m = UnivariateRealSolverUtils.midpoint(min, max);
/* 101*/        setResult(m, i);
/* 102*/        return m;
/*   0*/      } 
/* 104*/      i++;
/*   0*/    } 
/* 107*/    throw new MaxIterationsExceededException(this.maximalIterationCount);
/*   0*/  }
/*   0*/}
