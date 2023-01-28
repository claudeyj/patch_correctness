/*   0*/package org.apache.commons.math.analysis.solvers;
/*   0*/
/*   0*/import org.apache.commons.math.analysis.UnivariateFunction;
/*   0*/import org.apache.commons.math.exception.MathInternalError;
/*   0*/import org.apache.commons.math.exception.NoBracketingException;
/*   0*/import org.apache.commons.math.exception.NumberIsTooSmallException;
/*   0*/import org.apache.commons.math.util.FastMath;
/*   0*/import org.apache.commons.math.util.Precision;
/*   0*/
/*   0*/public class BracketingNthOrderBrentSolver extends AbstractUnivariateRealSolver implements BracketedUnivariateRealSolver<UnivariateFunction> {
/*   0*/  private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6D;
/*   0*/  
/*   0*/  private static final int DEFAULT_MAXIMAL_ORDER = 5;
/*   0*/  
/*   0*/  private static final int MAXIMAL_AGING = 2;
/*   0*/  
/*   0*/  private static final double REDUCTION_FACTOR = 0.0625D;
/*   0*/  
/*   0*/  private final int maximalOrder;
/*   0*/  
/*   0*/  private AllowedSolution allowed;
/*   0*/  
/*   0*/  public BracketingNthOrderBrentSolver() {
/*  69*/    this(1.0E-6D, 5);
/*   0*/  }
/*   0*/  
/*   0*/  public BracketingNthOrderBrentSolver(double absoluteAccuracy, int maximalOrder) throws NumberIsTooSmallException {
/*  82*/    super(absoluteAccuracy);
/*  83*/    if (maximalOrder < 2)
/*  84*/      throw new NumberIsTooSmallException(maximalOrder, 2, true); 
/*  86*/    this.maximalOrder = maximalOrder;
/*  87*/    this.allowed = AllowedSolution.ANY_SIDE;
/*   0*/  }
/*   0*/  
/*   0*/  public BracketingNthOrderBrentSolver(double relativeAccuracy, double absoluteAccuracy, int maximalOrder) throws NumberIsTooSmallException {
/* 102*/    super(relativeAccuracy, absoluteAccuracy);
/* 103*/    if (maximalOrder < 2)
/* 104*/      throw new NumberIsTooSmallException(maximalOrder, 2, true); 
/* 106*/    this.maximalOrder = maximalOrder;
/* 107*/    this.allowed = AllowedSolution.ANY_SIDE;
/*   0*/  }
/*   0*/  
/*   0*/  public BracketingNthOrderBrentSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy, int maximalOrder) throws NumberIsTooSmallException {
/* 124*/    super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
/* 125*/    if (maximalOrder < 2)
/* 126*/      throw new NumberIsTooSmallException(maximalOrder, 2, true); 
/* 128*/    this.maximalOrder = maximalOrder;
/* 129*/    this.allowed = AllowedSolution.ANY_SIDE;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximalOrder() {
/* 136*/    return this.maximalOrder;
/*   0*/  }
/*   0*/  
/*   0*/  protected double doSolve() {
/*   0*/    int nbPoints, signChangeIndex;
/* 146*/    double[] x = new double[this.maximalOrder + 1];
/* 147*/    double[] y = new double[this.maximalOrder + 1];
/* 148*/    x[0] = getMin();
/* 149*/    x[1] = getStartValue();
/* 150*/    x[2] = getMax();
/* 151*/    verifySequence(x[0], x[1], x[2]);
/* 154*/    y[1] = computeObjectiveValue(x[1]);
/* 155*/    if (Precision.equals(y[1], 0.0D, 1))
/* 157*/      return x[1]; 
/* 161*/    y[0] = computeObjectiveValue(x[0]);
/* 162*/    if (Precision.equals(y[0], 0.0D, 1))
/* 164*/      return x[0]; 
/* 169*/    if (y[0] * y[1] < 0.0D) {
/* 172*/      nbPoints = 2;
/* 173*/      signChangeIndex = 1;
/*   0*/    } else {
/* 178*/      y[2] = computeObjectiveValue(x[2]);
/* 179*/      if (Precision.equals(y[2], 0.0D, 1))
/* 181*/        return x[2]; 
/* 184*/      if (y[1] * y[2] < 0.0D) {
/* 186*/        nbPoints = 3;
/* 187*/        signChangeIndex = 2;
/*   0*/      } else {
/* 189*/        throw new NoBracketingException(x[0], x[2], y[0], y[2]);
/*   0*/      } 
/*   0*/    } 
/* 195*/    double[] tmpX = new double[x.length];
/* 198*/    double xA = x[signChangeIndex - 1];
/* 199*/    double yA = y[signChangeIndex - 1];
/* 200*/    double absYA = FastMath.abs(yA);
/* 201*/    int agingA = 0;
/* 202*/    double xB = x[signChangeIndex];
/* 203*/    double yB = y[signChangeIndex];
/* 204*/    double absYB = FastMath.abs(yB);
/* 205*/    int agingB = 0;
/*   0*/    while (true) {
/*   0*/      double targetY, nextX;
/* 211*/      double xTol = getAbsoluteAccuracy() + getRelativeAccuracy() * FastMath.max(FastMath.abs(xA), FastMath.abs(xB));
/* 213*/      if (xB - xA <= xTol || FastMath.max(absYA, absYB) < getFunctionValueAccuracy()) {
/* 214*/        switch (this.allowed) {
/*   0*/          case ANY_SIDE:
/* 216*/            return (absYA < absYB) ? xA : xB;
/*   0*/          case LEFT_SIDE:
/* 218*/            return xA;
/*   0*/          case RIGHT_SIDE:
/* 220*/            return xB;
/*   0*/          case BELOW_SIDE:
/* 222*/            return (yA <= 0.0D) ? xA : xB;
/*   0*/          case ABOVE_SIDE:
/* 224*/            return (yA < 0.0D) ? xB : xA;
/*   0*/        } 
/* 227*/        throw new MathInternalError(null);
/*   0*/      } 
/* 233*/      if (agingA >= 2) {
/* 235*/        targetY = -0.0625D * yB;
/* 236*/      } else if (agingB >= 2) {
/* 238*/        targetY = -0.0625D * yA;
/*   0*/      } else {
/* 241*/        targetY = 0.0D;
/*   0*/      } 
/* 246*/      int start = 0;
/* 247*/      int end = nbPoints;
/*   0*/      do {
/* 251*/        System.arraycopy(x, start, tmpX, start, end - start);
/* 252*/        nextX = guessX(targetY, tmpX, y, start, end);
/* 254*/        if (nextX > xA && nextX < xB)
/*   0*/          continue; 
/* 260*/        if (MAXIMAL_AGING - start >= end - signChangeIndex) {
/* 262*/          start++;
/*   0*/        } else {
/* 265*/          end--;
/*   0*/        } 
/* 269*/        nextX = Double.NaN;
/* 273*/      } while (Double.isNaN(nextX) && end - start > 1);
/* 275*/      if (Double.isNaN(nextX)) {
/* 277*/        nextX = xA + 0.5D * (xB - xA);
/* 278*/        start = signChangeIndex - 1;
/* 279*/        end = signChangeIndex;
/*   0*/      } 
/* 283*/      double nextY = computeObjectiveValue(nextX);
/* 284*/      if (Precision.equals(nextY, 0.0D, 1))
/* 287*/        return nextX; 
/* 290*/      if (nbPoints > 2 && end - start != nbPoints) {
/* 294*/        nbPoints = end - start;
/* 295*/        System.arraycopy(x, start, x, 0, nbPoints);
/* 296*/        System.arraycopy(y, start, y, 0, nbPoints);
/* 297*/        signChangeIndex -= start;
/* 299*/      } else if (nbPoints == x.length) {
/* 302*/        nbPoints--;
/* 305*/        if (signChangeIndex >= (x.length + 1) / 2) {
/* 307*/          System.arraycopy(x, 1, x, 0, nbPoints);
/* 308*/          System.arraycopy(y, 1, y, 0, nbPoints);
/* 309*/          signChangeIndex--;
/*   0*/        } 
/*   0*/      } 
/* 316*/      System.arraycopy(x, signChangeIndex, x, signChangeIndex + 1, nbPoints - signChangeIndex);
/* 317*/      x[signChangeIndex] = nextX;
/* 318*/      System.arraycopy(y, signChangeIndex, y, signChangeIndex + 1, nbPoints - signChangeIndex);
/* 319*/      y[signChangeIndex] = nextY;
/* 320*/      nbPoints++;
/* 323*/      if (nextY * yA <= 0.0D) {
/* 325*/        xB = nextX;
/* 326*/        yB = nextY;
/* 327*/        absYB = FastMath.abs(yB);
/* 328*/        agingA++;
/* 329*/        agingB = 0;
/*   0*/        continue;
/*   0*/      } 
/* 332*/      xA = nextX;
/* 333*/      yA = nextY;
/* 334*/      absYA = FastMath.abs(yA);
/* 335*/      agingA = 0;
/* 336*/      agingB++;
/* 339*/      signChangeIndex++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private double guessX(double targetY, double[] x, double[] y, int start, int end) {
/* 365*/    for (int i = start; i < end - 1; i++) {
/* 366*/      int delta = i + 1 - start;
/* 367*/      for (int k = end - 1; k > i; k--)
/* 368*/        x[k] = (x[k] - x[k - 1]) / (y[k] - y[k - delta]); 
/*   0*/    } 
/* 373*/    double x0 = 0.0D;
/* 374*/    for (int j = end - 1; j >= start; j--)
/* 375*/      x0 = x[j] + x0 * (targetY - y[j]); 
/* 378*/    return x0;
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(int maxEval, UnivariateFunction f, double min, double max, AllowedSolution allowedSolution) {
/* 385*/    this.allowed = allowedSolution;
/* 386*/    return solve(maxEval, f, min, max);
/*   0*/  }
/*   0*/  
/*   0*/  public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue, AllowedSolution allowedSolution) {
/* 393*/    this.allowed = allowedSolution;
/* 394*/    return solve(maxEval, f, min, max, startValue);
/*   0*/  }
/*   0*/}
