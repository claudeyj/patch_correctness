/*   0*/package org.apache.commons.math.optimization.direct;
/*   0*/
/*   0*/import java.util.Comparator;
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.optimization.OptimizationException;
/*   0*/import org.apache.commons.math.optimization.RealPointValuePair;
/*   0*/
/*   0*/public class MultiDirectional extends DirectSearchOptimizer {
/*   0*/  private final double khi;
/*   0*/  
/*   0*/  private final double gamma;
/*   0*/  
/*   0*/  public MultiDirectional() {
/*  46*/    this.khi = 2.0D;
/*  47*/    this.gamma = 0.5D;
/*   0*/  }
/*   0*/  
/*   0*/  public MultiDirectional(double khi, double gamma) {
/*  55*/    this.khi = khi;
/*  56*/    this.gamma = gamma;
/*   0*/  }
/*   0*/  
/*   0*/  protected void iterateSimplex(Comparator<RealPointValuePair> comparator) throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {
/*  66*/    incrementIterationsCounter();
/*  69*/    RealPointValuePair[] original = this.simplex;
/*  70*/    RealPointValuePair best = original[0];
/*  73*/    RealPointValuePair reflected = evaluateNewSimplex(original, 1.0D, comparator);
/*  74*/    if (comparator.compare(reflected, best) < 0) {
/*  77*/      RealPointValuePair[] reflectedSimplex = this.simplex;
/*  78*/      RealPointValuePair expanded = evaluateNewSimplex(original, this.khi, comparator);
/*  79*/      if (comparator.compare(reflected, expanded) <= 0)
/*  81*/        this.simplex = reflectedSimplex; 
/*   0*/      return;
/*   0*/    } 
/*  89*/    RealPointValuePair contracted = evaluateNewSimplex(original, this.gamma, comparator);
/*  90*/    comparator.compare(contracted, best);
/*   0*/  }
/*   0*/  
/*   0*/  private RealPointValuePair evaluateNewSimplex(RealPointValuePair[] original, double coeff, Comparator<RealPointValuePair> comparator) throws FunctionEvaluationException, OptimizationException {
/* 115*/    double[] xSmallest = original[0].getPointRef();
/* 116*/    int n = xSmallest.length;
/* 119*/    this.simplex = new RealPointValuePair[n + 1];
/* 120*/    this.simplex[0] = original[0];
/* 121*/    for (int i = 1; i <= n; i++) {
/* 122*/      double[] xOriginal = original[i].getPointRef();
/* 123*/      double[] xTransformed = new double[n];
/* 124*/      for (int j = 0; j < n; j++)
/* 125*/        xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]); 
/* 127*/      this.simplex[i] = new RealPointValuePair(xTransformed, Double.NaN, false);
/*   0*/    } 
/* 131*/    evaluateSimplex(comparator);
/* 132*/    return this.simplex[0];
/*   0*/  }
/*   0*/}
