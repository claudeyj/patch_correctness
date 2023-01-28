/*   0*/package org.apache.commons.math.special;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.apache.commons.math.MathException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.util.ContinuedFraction;
/*   0*/
/*   0*/public class Gamma implements Serializable {
/*   0*/  private static final long serialVersionUID = -6587513359895466954L;
/*   0*/  
/*   0*/  private static final double DEFAULT_EPSILON = 1.0E-8D;
/*   0*/  
/*  40*/  private static double[] lanczos = new double[] { 
/*  40*/      0.9999999999999971D, 57.15623566586292D, -59.59796035547549D, 14.136097974741746D, -0.4919138160976202D, 3.399464998481189E-5D, 4.652362892704858E-5D, -9.837447530487956E-5D, 1.580887032249125E-4D, -2.1026444172410488E-4D, 
/*  40*/      2.1743961811521265E-4D, -1.643181065367639E-4D, 8.441822398385275E-5D, -2.6190838401581408E-5D, 3.6899182659531625E-6D };
/*   0*/  
/*  60*/  private static final double HALF_LOG_2_PI = 0.5D * Math.log(6.283185307179586D);
/*   0*/  
/*   0*/  public static double logGamma(double x) {
/*   0*/    double ret;
/*  90*/    if (Double.isNaN(x) || x <= 0.0D) {
/*  91*/      ret = Double.NaN;
/*   0*/    } else {
/*  93*/      double g = 4.7421875D;
/*  95*/      double sum = 0.0D;
/*  96*/      for (int i = lanczos.length - 1; i > 0; i--)
/*  97*/        sum += lanczos[i] / (x + i); 
/*  99*/      sum += lanczos[0];
/* 101*/      double tmp = x + g + 0.5D;
/* 102*/      ret = (x + 0.5D) * Math.log(tmp) - tmp + HALF_LOG_2_PI + Math.log(sum / x);
/*   0*/    } 
/* 106*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static double regularizedGammaP(double a, double x) throws MathException {
/* 120*/    return regularizedGammaP(a, x, 1.0E-8D, Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public static double regularizedGammaP(double a, double x, double epsilon, int maxIterations) throws MathException {
/*   0*/    double ret;
/* 158*/    if (Double.isNaN(a) || Double.isNaN(x) || a <= 0.0D || x < 0.0D) {
/* 159*/      ret = Double.NaN;
/* 160*/    } else if (x == 0.0D) {
/* 161*/      ret = 0.0D;
/* 162*/    } else if (a >= 1.0D && x >= a) {
/* 165*/      ret = 1.0D - regularizedGammaQ(a, x, epsilon, maxIterations);
/*   0*/    } else {
/* 168*/      double n = 0.0D;
/* 169*/      double an = 1.0D / a;
/* 170*/      double sum = an;
/* 171*/      while (Math.abs(an) > epsilon && n < maxIterations) {
/* 173*/        n++;
/* 174*/        an *= x / (a + n);
/* 177*/        sum += an;
/*   0*/      } 
/* 179*/      if (n >= maxIterations)
/* 180*/        throw new MaxIterationsExceededException(maxIterations); 
/* 182*/      ret = Math.exp(-x + a * Math.log(x) - logGamma(a)) * sum;
/*   0*/    } 
/* 186*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static double regularizedGammaQ(double a, double x) throws MathException {
/* 200*/    return regularizedGammaQ(a, x, 1.0E-8D, Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public static double regularizedGammaQ(double a, double x, double epsilon, int maxIterations) throws MathException {
/*   0*/    double ret;
/* 233*/    if (Double.isNaN(a) || Double.isNaN(x) || a <= 0.0D || x < 0.0D) {
/* 234*/      ret = Double.NaN;
/* 235*/    } else if (x == 0.0D) {
/* 236*/      ret = 1.0D;
/* 237*/    } else if (x < a || a < 1.0D) {
/* 240*/      ret = 1.0D - regularizedGammaP(a, x, epsilon, maxIterations);
/*   0*/    } else {
/* 243*/      ContinuedFraction cf = new ContinuedFraction(a) {
/*   0*/          private static final long serialVersionUID = 5378525034886164398L;
/*   0*/          
/*   0*/          private final double val$a;
/*   0*/          
/*   0*/          {
/* 243*/            this.val$a = param1Double;
/*   0*/          }
/*   0*/          
/*   0*/          protected double getA(int n, double x) {
/* 248*/            return 2.0D * n + 1.0D - this.val$a + x;
/*   0*/          }
/*   0*/          
/*   0*/          protected double getB(int n, double x) {
/* 252*/            return n * (this.val$a - n);
/*   0*/          }
/*   0*/        };
/* 256*/      ret = 1.0D / cf.evaluate(x, epsilon, maxIterations);
/* 257*/      ret = Math.exp(-x + a * Math.log(x) - logGamma(a)) * ret;
/*   0*/    } 
/* 260*/    return ret;
/*   0*/  }
/*   0*/}
