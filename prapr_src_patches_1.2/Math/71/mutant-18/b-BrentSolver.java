/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/
/*   0*/public class BrentSolver extends UnivariateRealSolverImpl {
/*   0*/  public static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6D;
/*   0*/  
/*   0*/  public static final int DEFAULT_MAXIMUM_ITERATIONS = 100;
/*   0*/  
/*   0*/  private static final String NON_BRACKETING_MESSAGE = "function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]";
/*   0*/  
/*   0*/  private static final long serialVersionUID = 7694577816772532779L;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public BrentSolver(UnivariateRealFunction f) {
/*  65*/    super(f, 100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  public BrentSolver() {
/*  72*/    super(100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  public BrentSolver(double absoluteAccuracy) {
/*  82*/    super(100, absoluteAccuracy);
/*   0*/  }
/*   0*/  
/*   0*/  public BrentSolver(int maximumIterations, double absoluteAccuracy) {
/*  93*/    super(maximumIterations, absoluteAccuracy);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 100*/    return solve(this.f, min, max);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 107*/    return solve(this.f, min, max, initial);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 134*/    clearResult();
/* 135*/    if (initial < min || initial > max)
/* 136*/      throw MathRuntimeException.createIllegalArgumentException("invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}", new Object[] { min, initial, max }); 
/* 142*/    double yInitial = f.value(initial);
/* 143*/    if (Math.abs(yInitial) <= this.functionValueAccuracy) {
/* 144*/      setResult(initial, 0);
/* 145*/      return this.result;
/*   0*/    } 
/* 149*/    double yMin = f.value(min);
/* 150*/    if (Math.abs(yMin) <= this.functionValueAccuracy) {
/* 151*/      setResult(min, 0);
/* 152*/      return this.result;
/*   0*/    } 
/* 156*/    if (yInitial * yMin < 0.0D)
/* 157*/      return solve(f, min, yMin, initial, yInitial, min, yMin); 
/* 161*/    double yMax = f.value(max);
/* 162*/    if (Math.abs(yMax) <= this.functionValueAccuracy) {
/* 163*/      setResult(max, 0);
/* 164*/      return this.result;
/*   0*/    } 
/* 168*/    if (yInitial * yMax < 0.0D)
/* 169*/      return solve(f, initial, yInitial, max, yMax, initial, yInitial); 
/* 172*/    throw MathRuntimeException.createIllegalArgumentException("function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]", new Object[] { min, max, yMin, yMax });
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 199*/    clearResult();
/* 200*/    verifyInterval(min, max);
/* 202*/    double ret = Double.NaN;
/* 204*/    double yMin = f.value(min);
/* 205*/    double yMax = f.value(max);
/* 208*/    double sign = yMin * yMax;
/* 209*/    if (sign > 0.0D) {
/* 211*/      if (Math.abs(yMin) <= this.functionValueAccuracy) {
/* 212*/        setResult(min, 0);
/* 213*/        ret = min;
/* 214*/      } else if (Math.abs(yMax) <= this.functionValueAccuracy) {
/* 215*/        setResult(max, 0);
/* 216*/        ret = max;
/*   0*/      } else {
/* 219*/        throw MathRuntimeException.createIllegalArgumentException("function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]", new Object[] { min, max, yMin, yMax });
/*   0*/      } 
/* 222*/    } else if (sign < 0.0D) {
/* 224*/      ret = solve(f, min, yMin, max, yMax, min, yMin);
/* 227*/    } else if (yMin == 0.0D) {
/* 228*/      ret = min;
/*   0*/    } else {
/* 230*/      ret = max;
/*   0*/    } 
/* 234*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private double solve(UnivariateRealFunction f, double x0, double y0, double x1, double y1, double x2, double y2) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 260*/    double delta = x1 - x0;
/* 261*/    double oldDelta = delta;
/* 263*/    int i = 0;
/* 264*/    while (i < this.maximalIterationCount) {
/* 265*/      if (Math.abs(y2) < Math.abs(y1)) {
/* 267*/        x0 = x1;
/* 268*/        x1 = x2;
/* 269*/        x2 = x0;
/* 270*/        y0 = y1;
/* 271*/        y1 = y2;
/* 272*/        y2 = y0;
/*   0*/      } 
/* 274*/      if (Math.abs(y1) <= this.functionValueAccuracy) {
/* 278*/        setResult(x1, i);
/* 279*/        return this.result;
/*   0*/      } 
/* 281*/      double dx = x2 - x1;
/* 282*/      double tolerance = Math.max(this.relativeAccuracy * DEFAULT_ABSOLUTE_ACCURACY, this.absoluteAccuracy);
/* 284*/      if (Math.abs(dx) <= tolerance) {
/* 285*/        setResult(x1, i);
/* 286*/        return this.result;
/*   0*/      } 
/* 288*/      if (Math.abs(oldDelta) < tolerance || Math.abs(y0) <= Math.abs(y1)) {
/* 291*/        delta = 0.5D * dx;
/* 292*/        oldDelta = delta;
/*   0*/      } else {
/*   0*/        double p, p1;
/* 294*/        double r3 = y1 / y0;
/* 300*/        if (x0 == x2) {
/* 302*/          p = dx * r3;
/* 303*/          p1 = 1.0D - r3;
/*   0*/        } else {
/* 306*/          double r1 = y0 / y2;
/* 307*/          double r2 = y1 / y2;
/* 308*/          p = r3 * (dx * r1 * (r1 - r2) - (x1 - x0) * (r2 - 1.0D));
/* 309*/          p1 = (r1 - 1.0D) * (r2 - 1.0D) * (r3 - 1.0D);
/*   0*/        } 
/* 311*/        if (p > 0.0D) {
/* 312*/          p1 = -p1;
/*   0*/        } else {
/* 314*/          p = -p;
/*   0*/        } 
/* 316*/        if (2.0D * p >= 1.5D * dx * p1 - Math.abs(tolerance * p1) || p >= Math.abs(0.5D * oldDelta * p1)) {
/* 321*/          delta = 0.5D * dx;
/* 322*/          oldDelta = delta;
/*   0*/        } else {
/* 324*/          oldDelta = delta;
/* 325*/          delta = p / p1;
/*   0*/        } 
/*   0*/      } 
/* 329*/      x0 = x1;
/* 330*/      y0 = y1;
/* 332*/      if (Math.abs(delta) > tolerance) {
/* 333*/        x1 += delta;
/* 334*/      } else if (dx > 0.0D) {
/* 335*/        x1 += 0.5D * tolerance;
/* 336*/      } else if (dx <= 0.0D) {
/* 337*/        x1 -= 0.5D * tolerance;
/*   0*/      } 
/* 339*/      y1 = f.value(x1);
/* 340*/      if (((y1 > 0.0D) ? true : false) == ((y2 > 0.0D) ? true : false)) {
/* 341*/        x2 = x0;
/* 342*/        y2 = y0;
/* 343*/        delta = x1 - x0;
/* 344*/        oldDelta = delta;
/*   0*/      } 
/* 346*/      i++;
/*   0*/    } 
/* 348*/    throw new MaxIterationsExceededException(this.maximalIterationCount);
/*   0*/  }
/*   0*/}
