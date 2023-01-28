/*   0*/package org.apache.commons.math3.optim;
/*   0*/
/*   0*/import org.apache.commons.math3.exception.TooManyEvaluationsException;
/*   0*/import org.apache.commons.math3.exception.TooManyIterationsException;
/*   0*/import org.apache.commons.math3.util.Incrementor;
/*   0*/
/*   0*/public abstract class BaseOptimizer<PAIR> {
/*   0*/  protected final Incrementor evaluations;
/*   0*/  
/*   0*/  protected final Incrementor iterations;
/*   0*/  
/*   0*/  private ConvergenceChecker<PAIR> checker;
/*   0*/  
/*   0*/  protected BaseOptimizer(ConvergenceChecker<PAIR> checker) {
/*  48*/    this.checker = checker;
/*  50*/    this.evaluations = new Incrementor(0, new MaxEvalCallback());
/*  51*/    this.iterations = new Incrementor(0, new MaxIterCallback());
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxEvaluations() {
/*  60*/    return this.evaluations.getMaximalCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getEvaluations() {
/*  72*/    return this.evaluations.getCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxIterations() {
/*  81*/    return this.iterations.getMaximalCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getIterations() {
/*  93*/    return this.iterations.getCount();
/*   0*/  }
/*   0*/  
/*   0*/  public ConvergenceChecker<PAIR> getConvergenceChecker() {
/* 102*/    return this.checker;
/*   0*/  }
/*   0*/  
/*   0*/  public PAIR optimize(OptimizationData... optData) throws TooManyEvaluationsException, TooManyIterationsException {
/* 137*/    parseOptimizationData(optData);
/* 140*/    this.evaluations.resetCount();
/* 141*/    this.iterations.resetCount();
/* 143*/    return doOptimize();
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract PAIR doOptimize();
/*   0*/  
/*   0*/  protected void incrementEvaluationCount() throws TooManyEvaluationsException {
/* 162*/    this.evaluations.incrementCount();
/*   0*/  }
/*   0*/  
/*   0*/  protected void incrementIterationCount() throws TooManyIterationsException {
/* 173*/    this.iterations.incrementCount();
/*   0*/  }
/*   0*/  
/*   0*/  protected void parseOptimizationData(OptimizationData... optData) {
/* 190*/    for (OptimizationData data : optData) {
/* 191*/      if (data instanceof MaxEval) {
/* 192*/        this.evaluations.setMaximalCount(((MaxEval)data).getMaxEval());
/* 195*/      } else if (data instanceof MaxIter) {
/* 196*/        this.iterations.setMaximalCount(((MaxIter)data).getMaxIter());
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static class MaxEvalCallback implements Incrementor.MaxCountExceededCallback {
/*   0*/    private MaxEvalCallback() {}
/*   0*/    
/*   0*/    public void trigger(int max) {
/* 213*/      throw new TooManyEvaluationsException(max);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class MaxIterCallback implements Incrementor.MaxCountExceededCallback {
/*   0*/    private MaxIterCallback() {}
/*   0*/    
/*   0*/    public void trigger(int max) {
/* 228*/      throw new TooManyIterationsException(max);
/*   0*/    }
/*   0*/  }
/*   0*/}
