/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.ConvergenceException;
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/
/*   0*/public class UnivariateRealSolverUtils {
/*   0*/  public static double solve(UnivariateRealFunction f, double x0, double x1) throws ConvergenceException, FunctionEvaluationException {
/*  53*/    setup(f);
/*  54*/    return LazyHolder.FACTORY.newDefaultSolver().solve(f, x0, x1);
/*   0*/  }
/*   0*/  
/*   0*/  public static double solve(UnivariateRealFunction f, double x0, double x1, double absoluteAccuracy) throws ConvergenceException, FunctionEvaluationException {
/*  77*/    setup(f);
/*  78*/    UnivariateRealSolver solver = LazyHolder.FACTORY.newDefaultSolver();
/*  79*/    solver.setAbsoluteAccuracy(absoluteAccuracy);
/*  80*/    return solver.solve(f, x0, x1);
/*   0*/  }
/*   0*/  
/*   0*/  public static double[] bracket(UnivariateRealFunction function, double initial, double lowerBound, double upperBound) throws ConvergenceException, FunctionEvaluationException {
/* 127*/    return bracket(function, initial, lowerBound, upperBound, Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public static double[] bracket(UnivariateRealFunction function, double initial, double lowerBound, double upperBound, int maximumIterations) throws ConvergenceException, FunctionEvaluationException {
/*   0*/    double fa, fb;
/* 170*/    if (function == null)
/* 171*/      throw MathRuntimeException.createIllegalArgumentException("function is null", new Object[0]); 
/* 173*/    if (maximumIterations <= 0)
/* 174*/      throw MathRuntimeException.createIllegalArgumentException("bad value for maximum iterations number: {0}", new Object[] { maximumIterations }); 
/* 177*/    if (initial < lowerBound || initial > upperBound || lowerBound >= upperBound)
/* 178*/      throw MathRuntimeException.createIllegalArgumentException("invalid bracketing parameters:  lower bound={0},  initial={1}, upper bound={2}", new Object[] { lowerBound, initial, upperBound }); 
/* 182*/    double a = initial;
/* 183*/    double b = initial;
/* 186*/    int numIterations = 0;
/*   0*/    do {
/* 189*/      a = Math.max(a - 1.0D, lowerBound);
/* 190*/      b = Math.min(b + 1.0D, upperBound);
/* 191*/      fa = function.value(a);
/* 193*/      fb = function.value(b);
/* 194*/      numIterations++;
/* 195*/    } while (fa * fb > 0.0D && numIterations < maximumIterations && (a > lowerBound || b < upperBound));
/* 198*/    if (fa * fb >= 1.0D)
/* 199*/      throw new ConvergenceException("number of iterations={0}, maximum iterations={1}, initial={2}, lower bound={3}, upper bound={4}, final a value={5}, final b value={6}, f(a)={7}, f(b)={8}", new Object[] { numIterations, maximumIterations, initial, lowerBound, upperBound, a, b, fa, fb }); 
/* 207*/    return new double[] { a, b };
/*   0*/  }
/*   0*/  
/*   0*/  public static double midpoint(double a, double b) {
/* 218*/    return (a + b) * 0.5D;
/*   0*/  }
/*   0*/  
/*   0*/  private static void setup(UnivariateRealFunction f) {
/* 227*/    if (f == null)
/* 228*/      throw MathRuntimeException.createIllegalArgumentException("function is null", new Object[0]); 
/*   0*/  }
/*   0*/  
/*   0*/  private static class LazyHolder {
/* 237*/    private static final UnivariateRealSolverFactory FACTORY = UnivariateRealSolverFactory.newInstance();
/*   0*/  }
/*   0*/}
