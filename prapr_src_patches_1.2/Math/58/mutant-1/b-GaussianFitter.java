/*   0*/package org.apache.commons.math.optimization.fitting;
/*   0*/
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Comparator;
/*   0*/import org.apache.commons.math.analysis.ParametricUnivariateRealFunction;
/*   0*/import org.apache.commons.math.analysis.function.Gaussian;
/*   0*/import org.apache.commons.math.exception.NotStrictlyPositiveException;
/*   0*/import org.apache.commons.math.exception.NullArgumentException;
/*   0*/import org.apache.commons.math.exception.NumberIsTooSmallException;
/*   0*/import org.apache.commons.math.exception.OutOfRangeException;
/*   0*/import org.apache.commons.math.exception.ZeroException;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
/*   0*/
/*   0*/public class GaussianFitter extends CurveFitter {
/*   0*/  public GaussianFitter(DifferentiableMultivariateVectorialOptimizer optimizer) {
/*  68*/    super(optimizer);
/*   0*/  }
/*   0*/  
/*   0*/  public double[] fit(double[] initialGuess) {
/*  84*/    ParametricUnivariateRealFunction f = new ParametricUnivariateRealFunction() {
/*  85*/        private final ParametricUnivariateRealFunction g = new Gaussian.Parametric();
/*   0*/        
/*   0*/        public double value(double x, double[] p) {
/*  88*/          double v = Double.POSITIVE_INFINITY;
/*   0*/          try {
/*  90*/            v = this.g.value(x, p);
/*  91*/          } catch (NotStrictlyPositiveException notStrictlyPositiveException) {}
/*  94*/          return v;
/*   0*/        }
/*   0*/        
/*   0*/        public double[] gradient(double x, double[] p) {
/*  98*/          double[] v = { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
/*   0*/          try {
/* 102*/            v = this.g.gradient(x, p);
/* 103*/          } catch (NotStrictlyPositiveException notStrictlyPositiveException) {}
/* 106*/          return v;
/*   0*/        }
/*   0*/      };
/* 110*/    return fit(f, initialGuess);
/*   0*/  }
/*   0*/  
/*   0*/  public double[] fit() {
/* 120*/    double[] guess = new ParameterGuesser(getObservations()).guess();
/* 121*/    return fit(new Gaussian.Parametric(), guess);
/*   0*/  }
/*   0*/  
/*   0*/  public static class ParameterGuesser {
/*   0*/    private final WeightedObservedPoint[] observations;
/*   0*/    
/*   0*/    private double[] parameters;
/*   0*/    
/*   0*/    public ParameterGuesser(WeightedObservedPoint[] observations) {
/* 141*/      if (observations == null)
/* 142*/        throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY); 
/* 144*/      if (observations.length < 3)
/* 145*/        throw new NumberIsTooSmallException(observations.length, 3, true); 
/* 147*/      this.observations = (WeightedObservedPoint[])observations.clone();
/*   0*/    }
/*   0*/    
/*   0*/    public double[] guess() {
/* 156*/      if (this.parameters == null)
/* 157*/        this.parameters = basicGuess(this.observations); 
/* 159*/      return (double[])this.parameters.clone();
/*   0*/    }
/*   0*/    
/*   0*/    private double[] basicGuess(WeightedObservedPoint[] points) {
/*   0*/      double fwhmApprox;
/* 169*/      Arrays.sort(points, createWeightedObservedPointComparator());
/* 170*/      double[] params = new double[3];
/* 172*/      int maxYIdx = findMaxY(points);
/* 173*/      params[0] = points[maxYIdx].getY();
/* 174*/      params[1] = points[maxYIdx].getX();
/*   0*/      try {
/* 178*/        double halfY = params[0] + (params[1] - params[0]) / 2.0D;
/* 179*/        double fwhmX1 = interpolateXAtY(points, maxYIdx, -1, halfY);
/* 180*/        double fwhmX2 = interpolateXAtY(points, maxYIdx, 1, halfY);
/* 181*/        fwhmApprox = fwhmX2 - fwhmX1;
/* 182*/      } catch (OutOfRangeException e) {
/* 183*/        fwhmApprox = points[points.length - 1].getX() - points[0].getX();
/*   0*/      } 
/* 185*/      params[2] = fwhmApprox / 2.0D * Math.sqrt(2.0D * Math.log(2.0D));
/* 187*/      return params;
/*   0*/    }
/*   0*/    
/*   0*/    private int findMaxY(WeightedObservedPoint[] points) {
/* 197*/      int maxYIdx = 0;
/* 198*/      for (int i = 1; i < points.length; i++) {
/* 199*/        if (points[i].getY() > points[maxYIdx].getY())
/* 200*/          maxYIdx = i; 
/*   0*/      } 
/* 203*/      return maxYIdx;
/*   0*/    }
/*   0*/    
/*   0*/    private double interpolateXAtY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y) throws OutOfRangeException {
/* 223*/      if (idxStep == 0)
/* 224*/        throw new ZeroException(); 
/* 226*/      WeightedObservedPoint[] twoPoints = getInterpolationPointsForY(points, startIdx, idxStep, y);
/* 227*/      WeightedObservedPoint pointA = twoPoints[0];
/* 228*/      WeightedObservedPoint pointB = twoPoints[1];
/* 229*/      if (pointA.getY() == y)
/* 230*/        return pointA.getX(); 
/* 232*/      if (pointB.getY() == y)
/* 233*/        return pointB.getX(); 
/* 235*/      return pointA.getX() + (y - pointA.getY()) * (pointB.getX() - pointA.getX()) / (pointB.getY() - pointA.getY());
/*   0*/    }
/*   0*/    
/*   0*/    private WeightedObservedPoint[] getInterpolationPointsForY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y) throws OutOfRangeException {
/* 258*/      if (idxStep == 0)
/* 259*/        throw new ZeroException(); 
/* 261*/      int i = startIdx;
/* 262*/      for (; (idxStep < 0) ? (i + idxStep >= 0) : (i + idxStep < points.length); 
/* 263*/        i += idxStep) {
/* 264*/        if (isBetween(y, points[i].getY(), points[i + idxStep].getY()))
/* 265*/          return (idxStep < 0) ? new WeightedObservedPoint[] { points[i + idxStep], points[i] } : new WeightedObservedPoint[] { points[i], points[i + idxStep] }; 
/*   0*/      } 
/* 271*/      double minY = Double.POSITIVE_INFINITY;
/* 272*/      double maxY = Double.NEGATIVE_INFINITY;
/* 273*/      for (WeightedObservedPoint point : points) {
/* 274*/        minY = Math.min(minY, point.getY());
/* 275*/        maxY = Math.max(maxY, point.getY());
/*   0*/      } 
/* 277*/      throw new OutOfRangeException(y, minY, maxY);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isBetween(double value, double boundary1, double boundary2) {
/* 291*/      return ((value >= boundary1 && value <= boundary2) || (value >= boundary2 && value <= boundary1));
/*   0*/    }
/*   0*/    
/*   0*/    private Comparator<WeightedObservedPoint> createWeightedObservedPointComparator() {
/* 302*/      return new Comparator<WeightedObservedPoint>() {
/*   0*/          public int compare(WeightedObservedPoint p1, WeightedObservedPoint p2) {
/* 304*/            if (p1 == null && p2 == null)
/* 305*/              return 0; 
/* 307*/            if (p1 == null)
/* 308*/              return -1; 
/* 310*/            if (p2 == null)
/* 311*/              return 1; 
/* 313*/            if (p1.getY() < p2.getX())
/* 314*/              return -1; 
/* 316*/            if (p1.getX() > p2.getX())
/* 317*/              return 1; 
/* 319*/            if (p1.getY() < p2.getY())
/* 320*/              return -1; 
/* 322*/            if (p1.getY() > p2.getY())
/* 323*/              return 1; 
/* 325*/            if (p1.getWeight() < p2.getWeight())
/* 326*/              return -1; 
/* 328*/            if (p1.getWeight() > p2.getWeight())
/* 329*/              return 1; 
/* 331*/            return 0;
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/}
