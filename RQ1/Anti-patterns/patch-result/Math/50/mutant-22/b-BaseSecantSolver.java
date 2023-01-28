/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/import org.apache.commons.math.exception.MathInternalError;
/*   0*/import org.apache.commons.math.util.FastMath;
/*   0*/
/*   0*/public abstract class BaseSecantSolver extends AbstractUnivariateRealSolver implements BracketedUnivariateRealSolver<UnivariateRealFunction> {
/*   0*/  protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6D;
/*   0*/  
/*   0*/  private AllowedSolution allowed;
/*   0*/  
/*   0*/  private final Method method;
/*   0*/  
/*   0*/  protected BaseSecantSolver(double absoluteAccuracy, Method method) {
/*  68*/    super(absoluteAccuracy);
/*  69*/    this.allowed = AllowedSolution.ANY_SIDE;
/*  70*/    this.method = method;
/*   0*/  }
/*   0*/  
/*   0*/  protected BaseSecantSolver(double relativeAccuracy, double absoluteAccuracy, Method method) {
/*  83*/    super(relativeAccuracy, absoluteAccuracy);
/*  84*/    this.allowed = AllowedSolution.ANY_SIDE;
/*  85*/    this.method = method;
/*   0*/  }
/*   0*/  
/*   0*/  protected BaseSecantSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy, Method method) {
/* 100*/    super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
/* 101*/    this.allowed = AllowedSolution.ANY_SIDE;
/* 102*/    this.method = method;
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, AllowedSolution allowedSolution) {
/* 109*/    return solve(maxEval, f, min, max, min + 0.5D * (max - min), allowedSolution);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, double startValue, AllowedSolution allowedSolution) {
/* 116*/    this.allowed = allowedSolution;
/* 117*/    return super.solve(maxEval, f, min, max, startValue);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, double startValue) {
/* 124*/    return solve(maxEval, f, min, max, startValue, AllowedSolution.ANY_SIDE);
/*   0*/  }
/*   0*/  
/*   0*/  protected final double doSolve() {
/* 130*/    double x0 = getMin();
/* 131*/    double x1 = getMax();
/* 132*/    double f0 = computeObjectiveValue(x0);
/* 133*/    double f1 = computeObjectiveValue(x1);
/* 138*/    if (f0 == 0.0D)
/* 139*/      return x0; 
/* 141*/    if (f1 == 0.0D)
/* 142*/      return x1; 
/* 146*/    verifyBracketing(x0, x1);
/* 149*/    double ftol = getFunctionValueAccuracy();
/* 150*/    double atol = getAbsoluteAccuracy();
/* 151*/    double rtol = getRelativeAccuracy();
/*   0*/    boolean inverted = false;
/*   0*/    do {
/* 160*/      double x = x1 - f1 * (x1 - x0) / (f1 - f0);
/* 161*/      double fx = computeObjectiveValue(x);
/* 166*/      if (fx == 0.0D)
/* 167*/        return x; 
/* 171*/      if (f1 * fx < 0.0D) {
/* 174*/        x0 = x1;
/* 175*/        f0 = f1;
/* 176*/        inverted = !inverted;
/*   0*/      } else {
/* 178*/        switch (this.method) {
/*   0*/          case ILLINOIS:
/* 180*/            f0 *= 0.5D;
/*   0*/            break;
/*   0*/          case PEGASUS:
/* 183*/            f0 *= f1 / (f1 + fx);
/*   0*/            break;
/*   0*/          case REGULA_FALSI:
/* 187*/            if (x == x1) {
/* 188*/              x0 = 0.5D * (x0 + x1 - FastMath.max(rtol * FastMath.abs(x1), rtol));
/* 189*/              f0 = computeObjectiveValue(x0);
/*   0*/            } 
/*   0*/            break;
/*   0*/          default:
/* 194*/            throw new MathInternalError();
/*   0*/        } 
/*   0*/      } 
/* 198*/      x1 = x;
/* 199*/      f1 = fx;
/* 204*/      if (FastMath.abs(f1) > ftol)
/*   0*/        continue; 
/* 205*/      switch (this.allowed) {
/*   0*/        case ANY_SIDE:
/* 207*/          return x1;
/*   0*/        case LEFT_SIDE:
/* 209*/          if (inverted)
/* 210*/            return x1; 
/*   0*/          break;
/*   0*/        case RIGHT_SIDE:
/* 214*/          if (!inverted)
/* 215*/            return x1; 
/*   0*/          break;
/*   0*/        case BELOW_SIDE:
/* 219*/          if (f1 <= 0.0D)
/* 220*/            return x1; 
/*   0*/          break;
/*   0*/        case ABOVE_SIDE:
/* 224*/          if (f1 >= 0.0D)
/* 225*/            return x1; 
/*   0*/          break;
/*   0*/        default:
/* 229*/          throw new MathInternalError();
/*   0*/      } 
/* 235*/    } while (FastMath.abs(x1 - x0) >= FastMath.max(rtol * FastMath.abs(x1), atol));
/* 237*/    switch (this.allowed) {
/*   0*/      case ANY_SIDE:
/* 239*/        return x1;
/*   0*/      case LEFT_SIDE:
/* 241*/        return inverted ? x1 : x0;
/*   0*/      case RIGHT_SIDE:
/* 243*/        return inverted ? x0 : x1;
/*   0*/      case BELOW_SIDE:
/* 245*/        return (f1 <= 0.0D) ? x1 : x0;
/*   0*/      case ABOVE_SIDE:
/* 247*/        return (f1 >= 0.0D) ? x1 : x0;
/*   0*/    } 
/* 249*/    throw new MathInternalError();
/*   0*/  }
/*   0*/  
/*   0*/  protected enum Method {
/* 262*/    REGULA_FALSI, ILLINOIS, PEGASUS;
/*   0*/  }
/*   0*/}
