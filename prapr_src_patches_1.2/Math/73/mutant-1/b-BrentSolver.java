/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/
/*   0*/public class BrentSolver extends UnivariateRealSolverImpl {
/*   0*/  private static final String NON_BRACKETING_MESSAGE = "function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]";
/*   0*/  
/*   0*/  private static final long serialVersionUID = 7694577816772532779L;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public BrentSolver(UnivariateRealFunction f) {
/*  54*/    super(f, 100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  public BrentSolver() {
/*  61*/    super(100, 1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  68*/    return solve(this.f, min, max);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double solve(double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/*  75*/    return solve(this.f, min, max, initial);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max, double initial) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 102*/    clearResult();
/* 103*/    verifySequence(min, initial, max);
/* 106*/    double yInitial = f.value(initial);
/* 107*/    if (Math.abs(yInitial) <= this.functionValueAccuracy) {
/* 108*/      setResult(initial, 0);
/* 109*/      return this.result;
/*   0*/    } 
/* 113*/    double yMin = f.value(min);
/* 114*/    if (Math.abs(yMin) <= this.functionValueAccuracy) {
/* 115*/      setResult(yMin, 0);
/* 116*/      return this.result;
/*   0*/    } 
/* 120*/    if (yInitial * yMin < 0.0D)
/* 121*/      return solve(f, min, yMin, initial, yInitial, min, yMin); 
/* 125*/    double yMax = f.value(max);
/* 126*/    if (Math.abs(yMax) <= this.functionValueAccuracy) {
/* 127*/      setResult(yMax, 0);
/* 128*/      return this.result;
/*   0*/    } 
/* 132*/    if (yInitial * yMax < 0.0D)
/* 133*/      return solve(f, initial, yInitial, max, yMax, initial, yInitial); 
/* 138*/    double d6 = yInitial, d5 = initial, d4 = yMax, d3 = max, d2 = yMin, d1 = min;
/* 138*/    UnivariateRealFunction univariateRealFunction = f;
/* 138*/    return solve(univariateRealFunction, d1, d2);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(UnivariateRealFunction f, double min, double max) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 164*/    clearResult();
/* 165*/    verifyInterval(min, max);
/* 167*/    double ret = Double.NaN;
/* 169*/    double yMin = f.value(min);
/* 170*/    double yMax = f.value(max);
/* 173*/    double sign = yMin * yMax;
/* 174*/    if (sign > 0.0D) {
/* 176*/      if (Math.abs(yMin) <= this.functionValueAccuracy) {
/* 177*/        setResult(min, 0);
/* 178*/        ret = min;
/* 179*/      } else if (Math.abs(yMax) <= this.functionValueAccuracy) {
/* 180*/        setResult(max, 0);
/* 181*/        ret = max;
/*   0*/      } else {
/* 184*/        throw MathRuntimeException.createIllegalArgumentException("function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]", new Object[] { min, max, yMin, yMax });
/*   0*/      } 
/* 187*/    } else if (sign < 0.0D) {
/* 189*/      ret = solve(f, min, yMin, max, yMax, min, yMin);
/* 192*/    } else if (yMin == 0.0D) {
/* 193*/      ret = min;
/*   0*/    } else {
/* 195*/      ret = max;
/*   0*/    } 
/* 199*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private double solve(UnivariateRealFunction f, double x0, double y0, double x1, double y1, double x2, double y2) throws MaxIterationsExceededException, FunctionEvaluationException {
/* 225*/    double delta = x1 - x0;
/* 226*/    double oldDelta = delta;
/* 228*/    int i = 0;
/* 229*/    while (i < this.maximalIterationCount) {
/* 230*/      if (Math.abs(y2) < Math.abs(y1)) {
/* 232*/        x0 = x1;
/* 233*/        x1 = x2;
/* 234*/        x2 = x0;
/* 235*/        y0 = y1;
/* 236*/        y1 = y2;
/* 237*/        y2 = y0;
/*   0*/      } 
/* 239*/      if (Math.abs(y1) <= this.functionValueAccuracy) {
/* 243*/        setResult(x1, i);
/* 244*/        return this.result;
/*   0*/      } 
/* 246*/      double dx = x2 - x1;
/* 247*/      double tolerance = Math.max(this.relativeAccuracy * Math.abs(x1), this.absoluteAccuracy);
/* 249*/      if (Math.abs(dx) <= tolerance) {
/* 250*/        setResult(x1, i);
/* 251*/        return this.result;
/*   0*/      } 
/* 253*/      if (Math.abs(oldDelta) < tolerance || Math.abs(y0) <= Math.abs(y1)) {
/* 256*/        delta = 0.5D * dx;
/* 257*/        oldDelta = delta;
/*   0*/      } else {
/*   0*/        double p, p1;
/* 259*/        double r3 = y1 / y0;
/* 265*/        if (x0 == x2) {
/* 267*/          p = dx * r3;
/* 268*/          p1 = 1.0D - r3;
/*   0*/        } else {
/* 271*/          double r1 = y0 / y2;
/* 272*/          double r2 = y1 / y2;
/* 273*/          p = r3 * (dx * r1 * (r1 - r2) - (x1 - x0) * (r2 - 1.0D));
/* 274*/          p1 = (r1 - 1.0D) * (r2 - 1.0D) * (r3 - 1.0D);
/*   0*/        } 
/* 276*/        if (p > 0.0D) {
/* 277*/          p1 = -p1;
/*   0*/        } else {
/* 279*/          p = -p;
/*   0*/        } 
/* 281*/        if (2.0D * p >= 1.5D * dx * p1 - Math.abs(tolerance * p1) || p >= Math.abs(0.5D * oldDelta * p1)) {
/* 286*/          delta = 0.5D * dx;
/* 287*/          oldDelta = delta;
/*   0*/        } else {
/* 289*/          oldDelta = delta;
/* 290*/          delta = p / p1;
/*   0*/        } 
/*   0*/      } 
/* 294*/      x0 = x1;
/* 295*/      y0 = y1;
/* 297*/      if (Math.abs(delta) > tolerance) {
/* 298*/        x1 += delta;
/* 299*/      } else if (dx > 0.0D) {
/* 300*/        x1 += 0.5D * tolerance;
/* 301*/      } else if (dx <= 0.0D) {
/* 302*/        x1 -= 0.5D * tolerance;
/*   0*/      } 
/* 304*/      y1 = f.value(x1);
/* 305*/      if (((y1 > 0.0D) ? true : false) == ((y2 > 0.0D) ? true : false)) {
/* 306*/        x2 = x0;
/* 307*/        y2 = y0;
/* 308*/        delta = x1 - x0;
/* 309*/        oldDelta = delta;
/*   0*/      } 
/* 311*/      i++;
/*   0*/    } 
/* 313*/    throw new MaxIterationsExceededException(this.maximalIterationCount);
/*   0*/  }
/*   0*/}
