/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/
/*   0*/public class BrentSolver extends UnivariateRealSolverImpl {
/*   0*/  private static final long serialVersionUID = 7694577816772532779L;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public BrentSolver(UnivariateRealFunction f) {
/*  49*/    super(f, 100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  public BrentSolver() {
/*  56*/    super(100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  63*/    return solve(this.f, min, max);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  70*/    return solve(this.f, min, max, initial);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  97*/    clearResult();
/*  98*/    verifySequence(min, initial, max);
/* 101*/    double yInitial = f.value(initial);
/* 102*/    if (Math.abs(yInitial) <= this.functionValueAccuracy) {
/* 103*/      setResult(initial, 0);
/* 104*/      return this.result;
/*   0*/    } 
/* 108*/    double yMin = f.value(min);
/* 109*/    if (Math.abs(yMin) <= this.functionValueAccuracy) {
/* 110*/      setResult(yMin, 0);
/* 111*/      return this.result;
/*   0*/    } 
/* 115*/    if (yInitial * yMin < 0.0D)
/* 116*/      return solve(f, min, yMin, initial, yInitial, min, yMin); 
/* 120*/    double yMax = f.value(max);
/* 121*/    if (Math.abs(yMax) <= this.functionValueAccuracy) {
/* 122*/      setResult(yMax, 0);
/* 123*/      return this.result;
/*   0*/    } 
/* 127*/    if (yInitial * yMax < 0.0D)
/* 128*/      return solve(f, initial, yInitial, max, yMax, initial, yInitial); 
/* 132*/    return solve(f, min, yMin, max, yMax, initial, yInitial);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 158*/    clearResult();
/* 159*/    verifyInterval(min, max);
/* 161*/    double ret = Double.NaN;
/* 163*/    double yMin = f.value(min);
/* 164*/    double yMax = f.value(max);
/* 167*/    double sign = yMin * yMax;
/* 168*/    if (sign > 0.0D) {
/* 170*/      if (Math.abs(yMin) <= this.functionValueAccuracy) {
/* 171*/        setResult(min, 0);
/* 172*/        ret = min;
/* 173*/      } else if (Math.abs(yMax) <= this.functionValueAccuracy) {
/* 174*/        setResult(max, 0);
/* 175*/        ret = max;
/*   0*/      } else {
/* 178*/        throw MathRuntimeException.createIllegalArgumentException("function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]", new Object[] { min, max, yMin, yMax });
/*   0*/      } 
/* 183*/    } else if (sign < 0.0D) {
/* 185*/      ret = solve(f, min, yMin, max, yMax, min, yMin);
/* 188*/    } else if (yMin == 0.0D) {
/* 189*/      ret = min;
/*   0*/    } else {
/* 191*/      ret = max;
/*   0*/    } 
/* 195*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private double solve(UnivariateRealFunction f, double x0, double y0, double x1, double y1, double x2, double y2) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 221*/    double delta = x1 - x0;
/* 222*/    double oldDelta = delta;
/* 224*/    int i = 0;
/* 225*/    while (i < this.maximalIterationCount) {
/* 226*/      if (Math.abs(y2) < Math.abs(y1)) {
/* 228*/        x0 = x1;
/* 229*/        x1 = x2;
/* 230*/        x2 = x0;
/* 231*/        y0 = y1;
/* 232*/        y1 = y2;
/* 233*/        y2 = y0;
/*   0*/      } 
/* 235*/      if (Math.abs(y1) <= this.functionValueAccuracy) {
/* 239*/        setResult(x1, i);
/* 240*/        return this.result;
/*   0*/      } 
/* 242*/      double dx = x2 - x1;
/* 243*/      double tolerance = Math.max(this.relativeAccuracy * Math.abs(x1), this.absoluteAccuracy);
/* 245*/      if (Math.abs(dx) <= tolerance) {
/* 246*/        setResult(x1, i);
/* 247*/        return this.result;
/*   0*/      } 
/* 249*/      if (Math.abs(oldDelta) < tolerance || Math.abs(y0) <= Math.abs(y1)) {
/* 252*/        delta = 0.5D * dx;
/* 253*/        oldDelta = delta;
/*   0*/      } else {
/*   0*/        double p, p1;
/* 255*/        double r3 = y1 / y0;
/* 261*/        if (x0 == x2) {
/* 263*/          p = dx * r3;
/* 264*/          p1 = 1.0D - r3;
/*   0*/        } else {
/* 267*/          double r1 = y0 / y2;
/* 268*/          double r2 = y1 / y2;
/* 269*/          p = r3 * (dx * r1 * (r1 - r2) - (x1 - x0) * (r2 - 1.0D));
/* 270*/          p1 = (r1 - 1.0D) * (r2 - 1.0D) * (r3 - 1.0D);
/*   0*/        } 
/* 272*/        if (p > 0.0D) {
/* 273*/          p1 = -p1;
/*   0*/        } else {
/* 275*/          p = -p;
/*   0*/        } 
/* 277*/        if (2.0D * p >= 1.5D * dx * p1 - Math.abs(tolerance * p1) || p >= Math.abs(0.5D * oldDelta * p1)) {
/* 282*/          delta = 0.5D * dx;
/* 283*/          oldDelta = delta;
/*   0*/        } else {
/* 285*/          oldDelta = delta;
/* 286*/          delta = p / p1;
/*   0*/        } 
/*   0*/      } 
/* 290*/      x0 = x1;
/* 291*/      y0 = y1;
/* 293*/      if (Math.abs(delta) > tolerance) {
/* 294*/        x1 += delta;
/* 295*/      } else if (dx > 0.0D) {
/* 296*/        x1 += 0.5D * tolerance;
/* 297*/      } else if (dx <= 0.0D) {
/* 298*/        x1 -= 0.5D * tolerance;
/*   0*/      } 
/* 300*/      y1 = f.value(x1);
/* 301*/      if (((y1 > 0.0D) ? true : false) == ((y2 > 0.0D) ? true : false)) {
/* 302*/        x2 = x0;
/* 303*/        y2 = y0;
/* 304*/        delta = x1 - x0;
/* 305*/        oldDelta = delta;
/*   0*/      } 
/* 307*/      i++;
/*   0*/    } 
/* 309*/    throw new MaxIterationsExceededException(this.maximalIterationCount);
/*   0*/  }
/*   0*/}
