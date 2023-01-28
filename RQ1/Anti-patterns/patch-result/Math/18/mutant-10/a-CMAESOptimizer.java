/*   0*/package org.apache.commons.math3.optimization.direct;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math3.analysis.MultivariateFunction;
/*   0*/import org.apache.commons.math3.exception.DimensionMismatchException;
/*   0*/import org.apache.commons.math3.exception.MathUnsupportedOperationException;
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.NumberIsTooLargeException;
/*   0*/import org.apache.commons.math3.exception.OutOfRangeException;
/*   0*/import org.apache.commons.math3.exception.TooManyEvaluationsException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.linear.Array2DRowRealMatrix;
/*   0*/import org.apache.commons.math3.linear.EigenDecomposition;
/*   0*/import org.apache.commons.math3.linear.MatrixUtils;
/*   0*/import org.apache.commons.math3.linear.RealMatrix;
/*   0*/import org.apache.commons.math3.optimization.ConvergenceChecker;
/*   0*/import org.apache.commons.math3.optimization.GoalType;
/*   0*/import org.apache.commons.math3.optimization.MultivariateOptimizer;
/*   0*/import org.apache.commons.math3.optimization.PointValuePair;
/*   0*/import org.apache.commons.math3.optimization.SimpleValueChecker;
/*   0*/import org.apache.commons.math3.random.MersenneTwister;
/*   0*/import org.apache.commons.math3.random.RandomGenerator;
/*   0*/import org.apache.commons.math3.util.MathArrays;
/*   0*/
/*   0*/public class CMAESOptimizer extends BaseAbstractMultivariateSimpleBoundsOptimizer<MultivariateFunction> implements MultivariateOptimizer {
/*   0*/  public static final int DEFAULT_CHECKFEASABLECOUNT = 0;
/*   0*/  
/*   0*/  public static final double DEFAULT_STOPFITNESS = 0.0D;
/*   0*/  
/*   0*/  public static final boolean DEFAULT_ISACTIVECMA = true;
/*   0*/  
/*   0*/  public static final int DEFAULT_MAXITERATIONS = 30000;
/*   0*/  
/*   0*/  public static final int DEFAULT_DIAGONALONLY = 0;
/*   0*/  
/* 105*/  public static final RandomGenerator DEFAULT_RANDOMGENERATOR = new MersenneTwister();
/*   0*/  
/*   0*/  private int lambda;
/*   0*/  
/*   0*/  private boolean isActiveCMA;
/*   0*/  
/*   0*/  private int checkFeasableCount;
/*   0*/  
/*   0*/  private double[][] boundaries;
/*   0*/  
/*   0*/  private double[] inputSigma;
/*   0*/  
/*   0*/  private int dimension;
/*   0*/  
/* 151*/  private int diagonalOnly = 0;
/*   0*/  
/*   0*/  private boolean isMinimize = true;
/*   0*/  
/*   0*/  private boolean generateStatistics = false;
/*   0*/  
/*   0*/  private int maxIterations;
/*   0*/  
/*   0*/  private double stopFitness;
/*   0*/  
/*   0*/  private double stopTolUpX;
/*   0*/  
/*   0*/  private double stopTolX;
/*   0*/  
/*   0*/  private double stopTolFun;
/*   0*/  
/*   0*/  private double stopTolHistFun;
/*   0*/  
/*   0*/  private int mu;
/*   0*/  
/*   0*/  private double logMu2;
/*   0*/  
/*   0*/  private RealMatrix weights;
/*   0*/  
/*   0*/  private double mueff;
/*   0*/  
/*   0*/  private double sigma;
/*   0*/  
/*   0*/  private double cc;
/*   0*/  
/*   0*/  private double cs;
/*   0*/  
/*   0*/  private double damps;
/*   0*/  
/*   0*/  private double ccov1;
/*   0*/  
/*   0*/  private double ccovmu;
/*   0*/  
/*   0*/  private double chiN;
/*   0*/  
/*   0*/  private double ccov1Sep;
/*   0*/  
/*   0*/  private double ccovmuSep;
/*   0*/  
/*   0*/  private RealMatrix xmean;
/*   0*/  
/*   0*/  private RealMatrix pc;
/*   0*/  
/*   0*/  private RealMatrix ps;
/*   0*/  
/*   0*/  private double normps;
/*   0*/  
/*   0*/  private RealMatrix B;
/*   0*/  
/*   0*/  private RealMatrix D;
/*   0*/  
/*   0*/  private RealMatrix BD;
/*   0*/  
/*   0*/  private RealMatrix diagD;
/*   0*/  
/*   0*/  private RealMatrix C;
/*   0*/  
/*   0*/  private RealMatrix diagC;
/*   0*/  
/*   0*/  private int iterations;
/*   0*/  
/*   0*/  private double[] fitnessHistory;
/*   0*/  
/*   0*/  private int historySize;
/*   0*/  
/*   0*/  private RandomGenerator random;
/*   0*/  
/* 234*/  private List<Double> statisticsSigmaHistory = new ArrayList<Double>();
/*   0*/  
/* 236*/  private List<RealMatrix> statisticsMeanHistory = new ArrayList<RealMatrix>();
/*   0*/  
/* 238*/  private List<Double> statisticsFitnessHistory = new ArrayList<Double>();
/*   0*/  
/* 240*/  private List<RealMatrix> statisticsDHistory = new ArrayList<RealMatrix>();
/*   0*/  
/*   0*/  public CMAESOptimizer() {
/* 246*/    this(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CMAESOptimizer(int lambda) {
/* 253*/    this(lambda, null, 30000, 0.0D, true, 0, 0, DEFAULT_RANDOMGENERATOR, false);
/*   0*/  }
/*   0*/  
/*   0*/  public CMAESOptimizer(int lambda, double[] inputSigma) {
/* 263*/    this(lambda, inputSigma, 30000, 0.0D, true, 0, 0, DEFAULT_RANDOMGENERATOR, false);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public CMAESOptimizer(int lambda, double[] inputSigma, int maxIterations, double stopFitness, boolean isActiveCMA, int diagonalOnly, int checkFeasableCount, RandomGenerator random, boolean generateStatistics) {
/* 288*/    this(lambda, inputSigma, maxIterations, stopFitness, isActiveCMA, diagonalOnly, checkFeasableCount, random, generateStatistics, new SimpleValueChecker());
/*   0*/  }
/*   0*/  
/*   0*/  public CMAESOptimizer(int lambda, double[] inputSigma, int maxIterations, double stopFitness, boolean isActiveCMA, int diagonalOnly, int checkFeasableCount, RandomGenerator random, boolean generateStatistics, ConvergenceChecker<PointValuePair> checker) {
/* 313*/    super(checker);
/* 314*/    this.lambda = lambda;
/* 315*/    this.inputSigma = (inputSigma == null) ? null : (double[])inputSigma.clone();
/* 316*/    this.maxIterations = maxIterations;
/* 317*/    this.stopFitness = stopFitness;
/* 318*/    this.isActiveCMA = isActiveCMA;
/* 319*/    this.diagonalOnly = diagonalOnly;
/* 320*/    this.checkFeasableCount = checkFeasableCount;
/* 321*/    this.random = random;
/* 322*/    this.generateStatistics = generateStatistics;
/*   0*/  }
/*   0*/  
/*   0*/  public List<Double> getStatisticsSigmaHistory() {
/* 329*/    return this.statisticsSigmaHistory;
/*   0*/  }
/*   0*/  
/*   0*/  public List<RealMatrix> getStatisticsMeanHistory() {
/* 336*/    return this.statisticsMeanHistory;
/*   0*/  }
/*   0*/  
/*   0*/  public List<Double> getStatisticsFitnessHistory() {
/* 343*/    return this.statisticsFitnessHistory;
/*   0*/  }
/*   0*/  
/*   0*/  public List<RealMatrix> getStatisticsDHistory() {
/* 350*/    return this.statisticsDHistory;
/*   0*/  }
/*   0*/  
/*   0*/  protected PointValuePair doOptimize() {
/* 356*/    checkParameters();
/* 358*/    this.isMinimize = getGoalType().equals(GoalType.MINIMIZE);
/* 359*/    FitnessFunction fitfun = new FitnessFunction();
/* 360*/    double[] guess = fitfun.encode(getStartPoint());
/* 362*/    this.dimension = guess.length;
/* 363*/    initializeCMA(guess);
/* 364*/    this.iterations = 0;
/* 365*/    double bestValue = fitfun.value(guess);
/* 366*/    push(this.fitnessHistory, bestValue);
/* 367*/    PointValuePair optimum = new PointValuePair(getStartPoint(), this.isMinimize ? bestValue : -bestValue);
/* 369*/    PointValuePair lastResult = null;
/* 374*/    label101: for (this.iterations = 1; this.iterations <= this.maxIterations; this.iterations++) {
/* 376*/      RealMatrix arz = randn1(this.dimension, this.lambda);
/* 377*/      RealMatrix arx = zeros(this.dimension, this.lambda);
/* 378*/      double[] fitness = new double[this.lambda];
/* 380*/      for (int k = 0; k < this.lambda; k++) {
/* 381*/        RealMatrix arxk = null;
/* 382*/        for (int m = 0; m < this.checkFeasableCount + 1; m++) {
/* 383*/          if (this.diagonalOnly <= 0) {
/* 384*/            arxk = this.xmean.add(this.BD.multiply(arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
/*   0*/          } else {
/* 387*/            arxk = this.xmean.add(times(this.diagD, arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
/*   0*/          } 
/* 390*/          if (m >= this.checkFeasableCount || fitfun.isFeasible(arxk.getColumn(0)))
/*   0*/            break; 
/* 394*/          arz.setColumn(k, randn(this.dimension));
/*   0*/        } 
/* 396*/        copyColumn(arxk, 0, arx, k);
/*   0*/        try {
/* 398*/          fitness[k] = fitfun.value(arx.getColumn(k));
/* 399*/        } catch (TooManyEvaluationsException e) {
/*   0*/          break label101;
/*   0*/        } 
/*   0*/      } 
/* 404*/      int[] arindex = sortedIndices(fitness);
/* 406*/      RealMatrix xold = this.xmean;
/* 407*/      RealMatrix bestArx = selectColumns(arx, MathArrays.copyOf(arindex, this.mu));
/* 408*/      this.xmean = bestArx.multiply(this.weights);
/* 409*/      RealMatrix bestArz = selectColumns(arz, MathArrays.copyOf(arindex, this.mu));
/* 410*/      RealMatrix zmean = bestArz.multiply(this.weights);
/* 411*/      boolean hsig = updateEvolutionPaths(zmean, xold);
/* 412*/      if (this.diagonalOnly <= 0) {
/* 413*/        updateCovariance(hsig, bestArx, arz, arindex, xold);
/*   0*/      } else {
/* 415*/        updateCovarianceDiagonalOnly(hsig, bestArz, xold);
/*   0*/      } 
/* 418*/      this.sigma *= Math.exp(Math.min(1.0D, (this.normps / this.chiN - 1.0D) * this.cs / this.damps));
/* 419*/      double bestFitness = fitness[arindex[0]];
/* 420*/      double worstFitness = fitness[arindex[arindex.length - 1]];
/* 421*/      if (bestValue > bestFitness) {
/* 422*/        bestValue = bestFitness;
/* 423*/        lastResult = optimum;
/* 424*/        optimum = new PointValuePair(fitfun.repairAndDecode(bestArx.getColumn(0)), this.isMinimize ? bestFitness : -bestFitness);
/* 427*/        if (getConvergenceChecker() != null && lastResult != null && 
/* 428*/          getConvergenceChecker().converged(this.iterations, optimum, lastResult))
/*   0*/          break; 
/*   0*/      } 
/* 435*/      if (this.stopFitness != 0.0D)
/* 436*/        if (bestFitness < (this.isMinimize ? this.stopFitness : -this.stopFitness))
/*   0*/          break;  
/* 440*/      double[] sqrtDiagC = sqrt(this.diagC).getColumn(0);
/* 441*/      double[] pcCol = this.pc.getColumn(0);
/* 442*/      for (int i = 0; i < this.dimension && 
/* 443*/        this.sigma * Math.max(Math.abs(pcCol[i]), sqrtDiagC[i]) <= this.stopTolX; i++) {
/* 446*/        if (i >= this.dimension - 1)
/*   0*/          break label101; 
/*   0*/      } 
/* 450*/      for (int j = 0; j < this.dimension; j++) {
/* 451*/        if (this.sigma * sqrtDiagC[j] > this.stopTolUpX)
/*   0*/          break label101; 
/*   0*/      } 
/* 455*/      double historyBest = min(this.fitnessHistory);
/* 456*/      double historyWorst = max(this.fitnessHistory);
/* 457*/      if (this.iterations > 2 && Math.max(historyWorst, worstFitness) - Math.min(historyBest, bestFitness) < this.stopTolFun)
/*   0*/        break; 
/* 461*/      if (this.iterations > this.fitnessHistory.length && historyWorst - historyBest < this.stopTolHistFun)
/*   0*/        break; 
/* 466*/      if (max(this.diagD) / min(this.diagD) > 1.0E7D)
/*   0*/        break; 
/* 470*/      if (getConvergenceChecker() != null) {
/* 471*/        PointValuePair current = new PointValuePair(bestArx.getColumn(0), this.isMinimize ? bestFitness : -bestFitness);
/* 474*/        if (lastResult != null && getConvergenceChecker().converged(this.iterations, current, lastResult))
/*   0*/          break; 
/* 478*/        lastResult = current;
/*   0*/      } 
/* 481*/      if (bestValue == fitness[arindex[(int)(0.1D + this.lambda / 4.0D)]])
/* 482*/        this.sigma *= Math.exp(0.2D + this.cs / this.damps); 
/* 484*/      if (this.iterations > 2 && Math.max(historyWorst, bestFitness) - Math.min(historyBest, bestFitness) == 0.0D)
/* 486*/        this.sigma *= Math.exp(0.2D + this.cs / this.damps); 
/* 489*/      push(this.fitnessHistory, bestFitness);
/* 490*/      fitfun.setValueRange(worstFitness - bestFitness);
/* 491*/      if (this.generateStatistics) {
/* 492*/        this.statisticsSigmaHistory.add(this.sigma);
/* 493*/        this.statisticsFitnessHistory.add(bestFitness);
/* 494*/        this.statisticsMeanHistory.add(this.xmean.transpose());
/* 495*/        this.statisticsDHistory.add(this.diagD.transpose().scalarMultiply(100000.0D));
/*   0*/      } 
/*   0*/    } 
/* 498*/    return optimum;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkParameters() {
/* 505*/    double[] init = getStartPoint();
/* 506*/    double[] lB = getLowerBound();
/* 507*/    double[] uB = getUpperBound();
/*   0*/    boolean hasFiniteBounds = false;
/* 511*/    for (int i = 0; i < lB.length; i++) {
/* 512*/      if (!Double.isInfinite(lB[i]) || !Double.isInfinite(uB[i])) {
/* 514*/        hasFiniteBounds = true;
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/    boolean hasInfiniteBounds = false;
/* 520*/    if (hasFiniteBounds) {
/* 521*/      for (int k = 0; k < lB.length; k++) {
/* 522*/        if (Double.isInfinite(lB[k]) || Double.isInfinite(uB[k])) {
/* 524*/          hasInfiniteBounds = true;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/* 529*/      if (hasInfiniteBounds)
/* 532*/        throw new MathUnsupportedOperationException(); 
/* 535*/      this.boundaries = new double[2][];
/* 536*/      this.boundaries[0] = lB;
/* 537*/      this.boundaries[1] = uB;
/* 540*/      for (int j = 0; j < lB.length; j++) {
/* 541*/        if (Double.isInfinite(this.boundaries[1][j] - this.boundaries[0][j])) {
/* 542*/          double max = Double.MAX_VALUE + this.boundaries[0][j];
/* 543*/          NumberIsTooLargeException e = new NumberIsTooLargeException(this.boundaries[1][j], max, true);
/* 547*/          e.getContext().addMessage(LocalizedFormats.OVERFLOW, new Object[0]);
/* 548*/          e.getContext().addMessage(LocalizedFormats.INDEX, new Object[] { j });
/* 550*/          throw e;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/* 556*/      this.boundaries = null;
/*   0*/    } 
/* 559*/    if (this.inputSigma != null) {
/* 560*/      if (this.inputSigma.length != init.length)
/* 561*/        throw new DimensionMismatchException(this.inputSigma.length, init.length); 
/* 563*/      for (int j = 0; j < init.length; j++) {
/* 564*/        if (this.inputSigma[j] < 0.0D)
/* 565*/          throw new NotPositiveException(this.inputSigma[j]); 
/* 567*/        if (this.boundaries != null && 
/* 568*/          this.inputSigma[j] > this.boundaries[1][j] - this.boundaries[0][j])
/* 569*/          throw new OutOfRangeException(this.inputSigma[j], 0, this.boundaries[1][j] - this.boundaries[0][j]); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void initializeCMA(double[] guess) {
/* 582*/    if (this.lambda <= 0)
/* 583*/      this.lambda = 4 + (int)(3.0D * Math.log(this.dimension)); 
/* 586*/    double[][] sigmaArray = new double[guess.length][1];
/* 587*/    for (int i = 0; i < guess.length; i++) {
/* 588*/      double range = (this.boundaries == null) ? 1.0D : (this.boundaries[1][i] - this.boundaries[0][i]);
/* 589*/      sigmaArray[i][0] = ((this.inputSigma == null) ? 0.3D : this.inputSigma[i]) / range;
/*   0*/    } 
/* 591*/    RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
/* 592*/    this.sigma = max(insigma);
/* 595*/    this.stopTolUpX = 1000.0D * max(insigma);
/* 596*/    this.stopTolX = 1.0E-11D * max(insigma);
/* 597*/    this.stopTolFun = 1.0E-12D;
/* 598*/    this.stopTolHistFun = 1.0E-13D;
/* 601*/    this.mu = this.lambda / 2;
/* 602*/    this.logMu2 = Math.log(this.mu + 0.5D);
/* 603*/    this.weights = log(sequence(1.0D, this.mu, 1.0D)).scalarMultiply(-1.0D).scalarAdd(this.logMu2);
/* 604*/    double sumw = 0.0D;
/* 605*/    double sumwq = 0.0D;
/* 606*/    for (int k = 0; k < this.mu; k++) {
/* 607*/      double w = this.weights.getEntry(k, 0);
/* 608*/      sumw += w;
/* 609*/      sumwq += w * w;
/*   0*/    } 
/* 611*/    this.weights = this.weights.scalarMultiply(1.0D / sumw);
/* 612*/    this.mueff = sumw * sumw / sumwq;
/* 615*/    this.cc = (4.0D + this.mueff / this.dimension) / (this.dimension + 4.0D + 2.0D * this.mueff / this.dimension);
/* 617*/    this.cs = (this.mueff + 2.0D) / (this.dimension + this.mueff + 3.0D);
/* 618*/    this.damps = (1.0D + 2.0D * Math.max(0.0D, Math.sqrt((this.mueff - 1.0D) / (this.dimension + 1.0D)) - 1.0D)) * Math.max(0.3D, 1.0D - this.dimension / (1.0E-6D + Math.min(this.maxIterations, getMaxEvaluations() / this.lambda))) + this.cs;
/* 623*/    this.ccov1 = 2.0D / ((this.dimension + 1.3D) * (this.dimension + 1.3D) + this.mueff);
/* 624*/    this.ccovmu = Math.min(1.0D - this.ccov1, 2.0D * (this.mueff - 2.0D + 1.0D / this.mueff) / ((this.dimension + 2.0D) * (this.dimension + 2.0D) + this.mueff));
/* 626*/    this.ccov1Sep = Math.min(1.0D, this.ccov1 * (this.dimension + 1.5D) / 3.0D);
/* 627*/    this.ccovmuSep = Math.min(1.0D - this.ccov1, this.ccovmu * (this.dimension + 1.5D) / 3.0D);
/* 628*/    this.chiN = Math.sqrt(this.dimension) * (1.0D - 1.0D / 4.0D * this.dimension + 1.0D / 21.0D * this.dimension * this.dimension);
/* 631*/    this.xmean = MatrixUtils.createColumnRealMatrix(guess);
/* 633*/    this.diagD = insigma.scalarMultiply(1.0D / this.sigma);
/* 634*/    this.diagC = square(this.diagD);
/* 635*/    this.pc = zeros(this.dimension, 1);
/* 636*/    this.ps = zeros(this.dimension, 1);
/* 637*/    this.normps = this.ps.getFrobeniusNorm();
/* 639*/    this.B = eye(this.dimension, this.dimension);
/* 640*/    this.D = ones(this.dimension, 1);
/* 641*/    this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
/* 642*/    this.C = this.B.multiply(diag(square(this.D)).multiply(this.B.transpose()));
/* 643*/    this.historySize = 10 + (int)(30.0D * this.dimension / this.lambda);
/* 644*/    this.fitnessHistory = new double[this.historySize];
/* 645*/    for (int j = 0; j < this.historySize; j++)
/* 646*/      this.fitnessHistory[j] = Double.MAX_VALUE; 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean updateEvolutionPaths(RealMatrix zmean, RealMatrix xold) {
/* 659*/    this.ps = this.ps.scalarMultiply(1.0D - this.cs).add(this.B.multiply(zmean).scalarMultiply(Math.sqrt(this.cs * (2.0D - this.cs) * this.mueff)));
/* 662*/    this.normps = this.ps.getFrobeniusNorm();
/* 663*/    boolean hsig = (this.normps / Math.sqrt(1.0D - Math.pow(1.0D - this.cs, 2.0D * this.iterations)) / this.chiN < 1.4D + 2.0D / (this.dimension + 1.0D));
/* 666*/    this.pc = this.pc.scalarMultiply(1.0D - this.cc);
/* 667*/    if (hsig)
/* 668*/      this.pc = this.pc.add(this.xmean.subtract(xold).scalarMultiply(Math.sqrt(this.cc * (2.0D - this.cc) * this.mueff) / this.sigma)); 
/* 671*/    return hsig;
/*   0*/  }
/*   0*/  
/*   0*/  private void updateCovarianceDiagonalOnly(boolean hsig, RealMatrix bestArz, RealMatrix xold) {
/* 686*/    double oldFac = hsig ? 0.0D : (this.ccov1Sep * this.cc * (2.0D - this.cc));
/* 687*/    oldFac += 1.0D - this.ccov1Sep - this.ccovmuSep;
/* 688*/    this.diagC = this.diagC.scalarMultiply(oldFac).add(square(this.pc).scalarMultiply(this.ccov1Sep)).add(times(this.diagC, square(bestArz).multiply(this.weights)).scalarMultiply(this.ccovmuSep));
/* 694*/    this.diagD = sqrt(this.diagC);
/* 695*/    if (this.diagonalOnly > 1 && this.iterations > this.diagonalOnly) {
/* 697*/      this.diagonalOnly = 0;
/* 698*/      this.B = eye(this.dimension, this.dimension);
/* 699*/      this.BD = diag(this.diagD);
/* 700*/      this.C = diag(this.diagC);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateCovariance(boolean hsig, RealMatrix bestArx, RealMatrix arz, int[] arindex, RealMatrix xold) {
/* 717*/    double negccov = 0.0D;
/* 718*/    if (this.ccov1 + this.ccovmu > 0.0D) {
/* 719*/      RealMatrix arpos = bestArx.subtract(repmat(xold, 1, this.mu)).scalarMultiply(1.0D / this.sigma);
/* 721*/      RealMatrix roneu = this.pc.multiply(this.pc.transpose()).scalarMultiply(this.ccov1);
/* 724*/      double oldFac = hsig ? 0.0D : (this.ccov1 * this.cc * (2.0D - this.cc));
/* 725*/      oldFac += 1.0D - this.ccov1 - this.ccovmu;
/* 726*/      if (this.isActiveCMA) {
/* 728*/        negccov = (1.0D - this.ccovmu) * 0.25D * this.mueff / (Math.pow(this.dimension + 2.0D, 1.5D) + 2.0D * this.mueff);
/* 730*/        double negminresidualvariance = 0.66D;
/* 733*/        double negalphaold = 0.5D;
/* 736*/        int[] arReverseIndex = reverse(arindex);
/* 737*/        RealMatrix arzneg = selectColumns(arz, MathArrays.copyOf(arReverseIndex, this.mu));
/* 739*/        RealMatrix arnorms = sqrt(sumRows(square(arzneg)));
/* 740*/        int[] idxnorms = sortedIndices(arnorms.getRow(0));
/* 741*/        RealMatrix arnormsSorted = selectColumns(arnorms, idxnorms);
/* 742*/        int[] idxReverse = reverse(idxnorms);
/* 743*/        RealMatrix arnormsReverse = selectColumns(arnorms, idxReverse);
/* 744*/        arnorms = divide(arnormsReverse, arnormsSorted);
/* 745*/        int[] idxInv = inverse(idxnorms);
/* 746*/        RealMatrix arnormsInv = selectColumns(arnorms, idxInv);
/* 748*/        double negcovMax = (1.0D - negminresidualvariance) / square(arnormsInv).multiply(this.weights).getEntry(0, 0);
/* 750*/        if (negccov > negcovMax)
/* 751*/          negccov = negcovMax; 
/* 753*/        arzneg = times(arzneg, repmat(arnormsInv, this.dimension, 1));
/* 754*/        RealMatrix artmp = this.BD.multiply(arzneg);
/* 755*/        RealMatrix Cneg = artmp.multiply(diag(this.weights)).multiply(artmp.transpose());
/* 757*/        oldFac += negalphaold * negccov;
/* 758*/        this.C = this.C.scalarMultiply(oldFac).add(roneu).add(arpos.scalarMultiply(this.ccovmu + (1.0D - negalphaold) * negccov).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose()))).subtract(Cneg.scalarMultiply(negccov));
/*   0*/      } else {
/* 771*/        this.C = this.C.scalarMultiply(oldFac).add(roneu).add(arpos.scalarMultiply(this.ccovmu).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())));
/*   0*/      } 
/*   0*/    } 
/* 780*/    updateBD(negccov);
/*   0*/  }
/*   0*/  
/*   0*/  private void updateBD(double negccov) {
/* 789*/    if (this.ccov1 + this.ccovmu + negccov > 0.0D && this.iterations % 1.0D / (this.ccov1 + this.ccovmu + negccov) / this.dimension / 10.0D < 1.0D) {
/* 792*/      this.C = triu(this.C, 0).add(triu(this.C, 1).transpose());
/* 794*/      EigenDecomposition eig = new EigenDecomposition(this.C);
/* 795*/      this.B = eig.getV();
/* 796*/      this.D = eig.getD();
/* 797*/      this.diagD = diag(this.D);
/* 798*/      if (min(this.diagD) <= 0.0D) {
/* 799*/        for (int i = 0; i < this.dimension; i++) {
/* 800*/          if (this.diagD.getEntry(i, 0) < 0.0D)
/* 801*/            this.diagD.setEntry(i, 0, 0.0D); 
/*   0*/        } 
/* 804*/        double tfac = max(this.diagD) / 1.0E14D;
/* 805*/        this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
/* 806*/        this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
/*   0*/      } 
/* 808*/      if (max(this.diagD) > 1.0E14D * min(this.diagD)) {
/* 809*/        double tfac = max(this.diagD) / 1.0E14D - min(this.diagD);
/* 810*/        this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
/* 811*/        this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
/*   0*/      } 
/* 813*/      this.diagC = diag(this.C);
/* 814*/      this.diagD = sqrt(this.diagD);
/* 815*/      this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void push(double[] vals, double val) {
/* 826*/    for (int i = vals.length - 1; i > 0; i--)
/* 827*/      vals[i] = vals[i - 1]; 
/* 829*/    vals[0] = val;
/*   0*/  }
/*   0*/  
/*   0*/  private int[] sortedIndices(double[] doubles) {
/* 839*/    DoubleIndex[] dis = new DoubleIndex[doubles.length];
/* 840*/    for (int i = 0; i < doubles.length; i++)
/* 841*/      dis[i] = new DoubleIndex(doubles[i], i); 
/* 843*/    Arrays.sort((Object[])dis);
/* 844*/    int[] indices = new int[doubles.length];
/* 845*/    for (int j = 0; j < doubles.length; j++)
/* 846*/      indices[j] = (dis[j]).index; 
/* 848*/    return indices;
/*   0*/  }
/*   0*/  
/*   0*/  private static class DoubleIndex implements Comparable<DoubleIndex> {
/*   0*/    private double value;
/*   0*/    
/*   0*/    private int index;
/*   0*/    
/*   0*/    DoubleIndex(double value, int index) {
/* 866*/      this.value = value;
/* 867*/      this.index = index;
/*   0*/    }
/*   0*/    
/*   0*/    public int compareTo(DoubleIndex o) {
/* 872*/      return Double.compare(this.value, o.value);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object other) {
/* 879*/      if (this == other)
/* 880*/        return true; 
/* 883*/      if (other instanceof DoubleIndex)
/* 884*/        return (Double.compare(this.value, ((DoubleIndex)other).value) == 0); 
/* 887*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 894*/      long bits = Double.doubleToLongBits(this.value);
/* 895*/      return (int)((0x15F34EL ^ bits >>> 32L ^ bits) & 0xFFFFFFFFFFFFFFFFL);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class FitnessFunction {
/* 917*/    private double valueRange = 1.0D;
/*   0*/    
/*   0*/    private boolean isRepairMode = true;
/*   0*/    
/*   0*/    public double[] encode(double[] x) {
/* 926*/      if (CMAESOptimizer.this.boundaries == null)
/* 927*/        return x; 
/* 929*/      double[] res = new double[x.length];
/* 930*/      for (int i = 0; i < x.length; i++) {
/* 931*/        double diff = CMAESOptimizer.this.boundaries[1][i] - CMAESOptimizer.this.boundaries[0][i];
/* 932*/        res[i] = (x[i] - CMAESOptimizer.this.boundaries[0][i]) / diff;
/*   0*/      } 
/* 934*/      return res;
/*   0*/    }
/*   0*/    
/*   0*/    public double[] repairAndDecode(double[] x) {
/* 942*/      return (CMAESOptimizer.this.boundaries != null && this.isRepairMode) ? decode(repair(x)) : decode(x);
/*   0*/    }
/*   0*/    
/*   0*/    public double[] decode(double[] x) {
/* 952*/      if (CMAESOptimizer.this.boundaries == null)
/* 953*/        return x; 
/* 955*/      double[] res = new double[x.length];
/* 956*/      for (int i = 0; i < x.length; i++) {
/* 957*/        double diff = CMAESOptimizer.this.boundaries[1][i] - CMAESOptimizer.this.boundaries[0][i];
/* 958*/        res[i] = diff * x[i] + CMAESOptimizer.this.boundaries[0][i];
/*   0*/      } 
/* 960*/      return res;
/*   0*/    }
/*   0*/    
/*   0*/    public double value(double[] point) {
/*   0*/      double value;
/* 969*/      if (CMAESOptimizer.this.boundaries != null && this.isRepairMode) {
/* 970*/        double[] repaired = repair(point);
/* 971*/        value = CMAESOptimizer.this.computeObjectiveValue(decode(repaired)) + penalty(point, repaired);
/*   0*/      } else {
/* 975*/        value = CMAESOptimizer.this.computeObjectiveValue(decode(point));
/*   0*/      } 
/* 978*/      return CMAESOptimizer.this.isMinimize ? value : -value;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isFeasible(double[] x) {
/* 986*/      if (CMAESOptimizer.this.boundaries == null)
/* 987*/        return true; 
/* 991*/      for (int i = 0; i < x.length; i++) {
/* 992*/        if (x[i] < 0.0D)
/* 993*/          return false; 
/* 995*/        if (x[i] > 1.0D)
/* 996*/          return false; 
/*   0*/      } 
/* 999*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void setValueRange(double valueRange) {
/*1006*/      this.valueRange = valueRange;
/*   0*/    }
/*   0*/    
/*   0*/    private double[] repair(double[] x) {
/*1014*/      double[] repaired = new double[x.length];
/*1015*/      for (int i = 0; i < x.length; i++) {
/*1016*/        if (x[i] < 0.0D) {
/*1017*/          repaired[i] = 0.0D;
/*1018*/        } else if (x[i] > 1.0D) {
/*1019*/          repaired[i] = 1.0D;
/*   0*/        } else {
/*1021*/          repaired[i] = x[i];
/*   0*/        } 
/*   0*/      } 
/*1024*/      return repaired;
/*   0*/    }
/*   0*/    
/*   0*/    private double penalty(double[] x, double[] repaired) {
/*1033*/      double penalty = 0.0D;
/*1034*/      for (int i = 0; i < x.length; i++) {
/*1035*/        double diff = Math.abs(x[i] - repaired[i]);
/*1036*/        penalty += diff * this.valueRange;
/*   0*/      } 
/*1038*/      return CMAESOptimizer.this.isMinimize ? penalty : -penalty;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix log(RealMatrix m) {
/*1049*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1050*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1051*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1052*/        d[r][c] = Math.log(m.getEntry(r, c)); 
/*   0*/    } 
/*1055*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix sqrt(RealMatrix m) {
/*1064*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1065*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1066*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1067*/        d[r][c] = Math.sqrt(m.getEntry(r, c)); 
/*   0*/    } 
/*1070*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix square(RealMatrix m) {
/*1078*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1079*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1080*/      for (int c = 0; c < m.getColumnDimension(); c++) {
/*1081*/        double e = m.getEntry(r, c);
/*1082*/        d[r][c] = e * e;
/*   0*/      } 
/*   0*/    } 
/*1085*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix times(RealMatrix m, RealMatrix n) {
/*1094*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1095*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1096*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1097*/        d[r][c] = m.getEntry(r, c) * n.getEntry(r, c); 
/*   0*/    } 
/*1100*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix divide(RealMatrix m, RealMatrix n) {
/*1109*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1110*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1111*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1112*/        d[r][c] = m.getEntry(r, c) / n.getEntry(r, c); 
/*   0*/    } 
/*1115*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix selectColumns(RealMatrix m, int[] cols) {
/*1124*/    double[][] d = new double[m.getRowDimension()][cols.length];
/*1125*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1126*/      for (int c = 0; c < cols.length; c++)
/*1127*/        d[r][c] = m.getEntry(r, cols[c]); 
/*   0*/    } 
/*1130*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix triu(RealMatrix m, int k) {
/*1139*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1140*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1141*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1142*/        d[r][c] = (r <= c - k) ? m.getEntry(r, c) : 0.0D; 
/*   0*/    } 
/*1145*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix sumRows(RealMatrix m) {
/*1153*/    double[][] d = new double[1][m.getColumnDimension()];
/*1154*/    for (int c = 0; c < m.getColumnDimension(); c++) {
/*1155*/      double sum = 0.0D;
/*1156*/      for (int r = 0; r < m.getRowDimension(); r++)
/*1157*/        sum += m.getEntry(r, c); 
/*1159*/      d[0][c] = sum;
/*   0*/    } 
/*1161*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix diag(RealMatrix m) {
/*1170*/    if (m.getColumnDimension() == 1) {
/*1171*/      double[][] arrayOfDouble = new double[m.getRowDimension()][m.getRowDimension()];
/*1172*/      for (int j = 0; j < m.getRowDimension(); j++)
/*1173*/        arrayOfDouble[j][j] = m.getEntry(j, 0); 
/*1175*/      return new Array2DRowRealMatrix(arrayOfDouble, false);
/*   0*/    } 
/*1177*/    double[][] d = new double[m.getRowDimension()][1];
/*1178*/    for (int i = 0; i < m.getColumnDimension(); i++)
/*1179*/      d[i][0] = m.getEntry(i, i); 
/*1181*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static void copyColumn(RealMatrix m1, int col1, RealMatrix m2, int col2) {
/*1194*/    for (int i = 0; i < m1.getRowDimension(); i++)
/*1195*/      m2.setEntry(i, col2, m1.getEntry(i, col1)); 
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix ones(int n, int m) {
/*1205*/    double[][] d = new double[n][m];
/*1206*/    for (int r = 0; r < n; r++)
/*1207*/      Arrays.fill(d[r], 1.0D); 
/*1209*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix eye(int n, int m) {
/*1218*/    double[][] d = new double[n][m];
/*1219*/    for (int r = 0; r < n; r++) {
/*1220*/      if (r < m)
/*1221*/        d[r][r] = 1.0D; 
/*   0*/    } 
/*1224*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix zeros(int n, int m) {
/*1233*/    return new Array2DRowRealMatrix(n, m);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix repmat(RealMatrix mat, int n, int m) {
/*1243*/    int rd = mat.getRowDimension();
/*1244*/    int cd = mat.getColumnDimension();
/*1245*/    double[][] d = new double[n * rd][m * cd];
/*1246*/    for (int r = 0; r < n * rd; r++) {
/*1247*/      for (int c = 0; c < m * cd; c++)
/*1248*/        d[r][c] = mat.getEntry(r % rd, c % cd); 
/*   0*/    } 
/*1251*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix sequence(double start, double end, double step) {
/*1261*/    int size = (int)((end - start) / step + 1.0D);
/*1262*/    double[][] d = new double[size][1];
/*1263*/    double value = start;
/*1264*/    for (int r = 0; r < size; r++) {
/*1265*/      d[r][0] = value;
/*1266*/      value += step;
/*   0*/    } 
/*1268*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static double max(RealMatrix m) {
/*1276*/    double max = -1.7976931348623157E308D;
/*1277*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1278*/      for (int c = 0; c < m.getColumnDimension(); c++) {
/*1279*/        double e = m.getEntry(r, c);
/*1280*/        if (max < e)
/*1281*/          max = e; 
/*   0*/      } 
/*   0*/    } 
/*1285*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  private static double min(RealMatrix m) {
/*1293*/    double min = Double.MAX_VALUE;
/*1294*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1295*/      for (int c = 0; c < m.getColumnDimension(); c++) {
/*1296*/        double e = m.getEntry(r, c);
/*1297*/        if (min > e)
/*1298*/          min = e; 
/*   0*/      } 
/*   0*/    } 
/*1302*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  private static double max(double[] m) {
/*1310*/    double max = -1.7976931348623157E308D;
/*1311*/    for (int r = 0; r < m.length; r++) {
/*1312*/      if (max < m[r])
/*1313*/        max = m[r]; 
/*   0*/    } 
/*1316*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  private static double min(double[] m) {
/*1324*/    double min = Double.MAX_VALUE;
/*1325*/    for (int r = 0; r < m.length; r++) {
/*1326*/      if (min > m[r])
/*1327*/        min = m[r]; 
/*   0*/    } 
/*1330*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  private static int[] inverse(int[] indices) {
/*1338*/    int[] inverse = new int[indices.length];
/*1339*/    for (int i = 0; i < indices.length; i++)
/*1340*/      inverse[indices[i]] = i; 
/*1342*/    return inverse;
/*   0*/  }
/*   0*/  
/*   0*/  private static int[] reverse(int[] indices) {
/*1350*/    int[] reverse = new int[indices.length];
/*1351*/    for (int i = 0; i < indices.length; i++)
/*1352*/      reverse[i] = indices[indices.length - i - 1]; 
/*1354*/    return reverse;
/*   0*/  }
/*   0*/  
/*   0*/  private double[] randn(int size) {
/*1362*/    double[] randn = new double[size];
/*1363*/    for (int i = 0; i < size; i++)
/*1364*/      randn[i] = this.random.nextGaussian(); 
/*1366*/    return randn;
/*   0*/  }
/*   0*/  
/*   0*/  private RealMatrix randn1(int size, int popSize) {
/*1375*/    double[][] d = new double[size][popSize];
/*1376*/    for (int r = 0; r < size; r++) {
/*1377*/      for (int c = 0; c < popSize; c++)
/*1378*/        d[r][c] = this.random.nextGaussian(); 
/*   0*/    } 
/*1381*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/}
