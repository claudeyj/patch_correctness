/*   0*/package org.apache.commons.math3.optimization.direct;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math3.analysis.MultivariateFunction;
/*   0*/import org.apache.commons.math3.exception.DimensionMismatchException;
/*   0*/import org.apache.commons.math3.exception.MathUnsupportedOperationException;
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.OutOfRangeException;
/*   0*/import org.apache.commons.math3.exception.TooManyEvaluationsException;
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
/*  99*/  public static final RandomGenerator DEFAULT_RANDOMGENERATOR = new MersenneTwister();
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
/* 145*/  private int diagonalOnly = 0;
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
/* 228*/  private List<Double> statisticsSigmaHistory = new ArrayList<Double>();
/*   0*/  
/* 230*/  private List<RealMatrix> statisticsMeanHistory = new ArrayList<RealMatrix>();
/*   0*/  
/* 232*/  private List<Double> statisticsFitnessHistory = new ArrayList<Double>();
/*   0*/  
/* 234*/  private List<RealMatrix> statisticsDHistory = new ArrayList<RealMatrix>();
/*   0*/  
/*   0*/  public CMAESOptimizer() {
/* 240*/    this(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CMAESOptimizer(int lambda) {
/* 247*/    this(lambda, null, 30000, 0.0D, true, 0, 0, DEFAULT_RANDOMGENERATOR, false);
/*   0*/  }
/*   0*/  
/*   0*/  public CMAESOptimizer(int lambda, double[] inputSigma) {
/* 257*/    this(lambda, inputSigma, 30000, 0.0D, true, 0, 0, DEFAULT_RANDOMGENERATOR, false);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public CMAESOptimizer(int lambda, double[] inputSigma, int maxIterations, double stopFitness, boolean isActiveCMA, int diagonalOnly, int checkFeasableCount, RandomGenerator random, boolean generateStatistics) {
/* 282*/    this(lambda, inputSigma, maxIterations, stopFitness, isActiveCMA, diagonalOnly, checkFeasableCount, random, generateStatistics, new SimpleValueChecker());
/*   0*/  }
/*   0*/  
/*   0*/  public CMAESOptimizer(int lambda, double[] inputSigma, int maxIterations, double stopFitness, boolean isActiveCMA, int diagonalOnly, int checkFeasableCount, RandomGenerator random, boolean generateStatistics, ConvergenceChecker<PointValuePair> checker) {
/* 307*/    super(checker);
/* 308*/    this.lambda = lambda;
/* 309*/    this.inputSigma = (inputSigma == null) ? null : (double[])inputSigma.clone();
/* 310*/    this.maxIterations = maxIterations;
/* 311*/    this.stopFitness = stopFitness;
/* 312*/    this.isActiveCMA = isActiveCMA;
/* 313*/    this.diagonalOnly = diagonalOnly;
/* 314*/    this.checkFeasableCount = checkFeasableCount;
/* 315*/    this.random = random;
/* 316*/    this.generateStatistics = generateStatistics;
/*   0*/  }
/*   0*/  
/*   0*/  public List<Double> getStatisticsSigmaHistory() {
/* 323*/    return this.statisticsSigmaHistory;
/*   0*/  }
/*   0*/  
/*   0*/  public List<RealMatrix> getStatisticsMeanHistory() {
/* 330*/    return this.statisticsMeanHistory;
/*   0*/  }
/*   0*/  
/*   0*/  public List<Double> getStatisticsFitnessHistory() {
/* 337*/    return this.statisticsFitnessHistory;
/*   0*/  }
/*   0*/  
/*   0*/  public List<RealMatrix> getStatisticsDHistory() {
/* 344*/    return this.statisticsDHistory;
/*   0*/  }
/*   0*/  
/*   0*/  protected PointValuePair doOptimize() {
/* 350*/    checkParameters();
/* 352*/    this.isMinimize = getGoalType().equals(GoalType.MINIMIZE);
/* 353*/    FitnessFunction fitfun = new FitnessFunction();
/* 354*/    double[] guess = fitfun.encode(getStartPoint());
/* 356*/    this.dimension = guess.length;
/* 357*/    initializeCMA(guess);
/* 358*/    this.iterations = 0;
/* 359*/    double bestValue = fitfun.value(guess);
/* 360*/    push(this.fitnessHistory, bestValue);
/* 361*/    PointValuePair optimum = new PointValuePair(getStartPoint(), this.isMinimize ? bestValue : -bestValue);
/* 363*/    PointValuePair lastResult = null;
/* 368*/    label101: for (this.iterations = 1; this.iterations <= this.maxIterations; this.iterations++) {
/* 370*/      RealMatrix arz = randn1(this.dimension, this.lambda);
/* 371*/      RealMatrix arx = zeros(this.dimension, this.lambda);
/* 372*/      double[] fitness = new double[this.lambda];
/* 374*/      for (int k = 0; k < this.lambda; k++) {
/* 375*/        RealMatrix arxk = null;
/* 376*/        for (int m = 0; m < this.checkFeasableCount + 1; m++) {
/* 377*/          if (this.diagonalOnly <= 0) {
/* 378*/            arxk = this.xmean.add(this.BD.multiply(arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
/*   0*/          } else {
/* 381*/            arxk = this.xmean.add(times(this.diagD, arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
/*   0*/          } 
/* 384*/          if (m >= this.checkFeasableCount || fitfun.isFeasible(arxk.getColumn(0)))
/*   0*/            break; 
/* 388*/          arz.setColumn(k, randn(this.dimension));
/*   0*/        } 
/* 390*/        copyColumn(arxk, 0, arx, k);
/*   0*/        try {
/* 392*/          fitness[k] = fitfun.value(arx.getColumn(k));
/* 393*/        } catch (TooManyEvaluationsException e) {
/*   0*/          break label101;
/*   0*/        } 
/*   0*/      } 
/* 398*/      int[] arindex = sortedIndices(fitness);
/* 400*/      RealMatrix xold = this.xmean;
/* 401*/      RealMatrix bestArx = selectColumns(arx, MathArrays.copyOf(arindex, this.mu));
/* 402*/      this.xmean = bestArx.multiply(this.weights);
/* 403*/      RealMatrix bestArz = selectColumns(arz, MathArrays.copyOf(arindex, this.mu));
/* 404*/      RealMatrix zmean = bestArz.multiply(this.weights);
/* 405*/      boolean hsig = updateEvolutionPaths(zmean, xold);
/* 406*/      if (this.diagonalOnly <= 0) {
/* 407*/        updateCovariance(hsig, bestArx, arz, arindex, xold);
/*   0*/      } else {
/* 409*/        updateCovarianceDiagonalOnly(hsig, bestArz, xold);
/*   0*/      } 
/* 412*/      this.sigma *= Math.exp(Math.min(1.0D, (this.normps / this.chiN - 1.0D) * this.cs / this.damps));
/* 413*/      double bestFitness = fitness[arindex[0]];
/* 414*/      double worstFitness = fitness[arindex[arindex.length - 1]];
/* 415*/      if (bestValue > bestFitness) {
/* 416*/        bestValue = bestFitness;
/* 417*/        lastResult = optimum;
/* 418*/        optimum = new PointValuePair(fitfun.repairAndDecode(bestArx.getColumn(0)), this.isMinimize ? bestFitness : -bestFitness);
/* 421*/        if (getConvergenceChecker() != null && lastResult != null && 
/* 422*/          getConvergenceChecker().converged(this.iterations, optimum, lastResult))
/*   0*/          break; 
/*   0*/      } 
/* 429*/      if (this.stopFitness != 0.0D)
/* 430*/        if (bestFitness < (this.isMinimize ? this.stopFitness : -this.stopFitness))
/*   0*/          break;  
/* 434*/      double[] sqrtDiagC = sqrt(this.diagC).getColumn(0);
/* 435*/      double[] pcCol = this.pc.getColumn(0);
/* 436*/      for (int i = 0; i < this.dimension && 
/* 437*/        this.sigma * Math.max(Math.abs(pcCol[i]), sqrtDiagC[i]) <= this.stopTolX; i++) {
/* 440*/        if (i >= this.dimension - 1)
/*   0*/          break label101; 
/*   0*/      } 
/* 444*/      for (int j = 0; j < this.dimension; j++) {
/* 445*/        if (this.sigma * sqrtDiagC[j] > this.stopTolUpX)
/*   0*/          break label101; 
/*   0*/      } 
/* 449*/      double historyBest = min(this.fitnessHistory);
/* 450*/      double historyWorst = max(this.fitnessHistory);
/* 451*/      if (this.iterations > 2 && Math.max(historyWorst, worstFitness) - Math.min(historyBest, bestFitness) < this.stopTolFun)
/*   0*/        break; 
/* 455*/      if (this.iterations > this.fitnessHistory.length && historyWorst - historyBest < this.stopTolHistFun)
/*   0*/        break; 
/* 460*/      if (max(this.diagD) / min(this.diagD) > 1.0E7D)
/*   0*/        break; 
/* 464*/      if (getConvergenceChecker() != null) {
/* 465*/        PointValuePair current = new PointValuePair(bestArx.getColumn(0), this.isMinimize ? bestFitness : -bestFitness);
/* 468*/        if (lastResult != null && getConvergenceChecker().converged(this.iterations, current, lastResult))
/*   0*/          break; 
/* 472*/        lastResult = current;
/*   0*/      } 
/* 475*/      if (bestValue == fitness[arindex[(int)(0.1D + this.lambda / 4.0D)]])
/* 476*/        this.sigma *= Math.exp(0.2D + this.cs / this.damps); 
/* 478*/      if (this.iterations > 2 && Math.max(historyWorst, bestFitness) - Math.min(historyBest, bestFitness) == 0.0D)
/* 480*/        this.sigma *= Math.exp(0.2D + this.cs / this.damps); 
/* 483*/      push(this.fitnessHistory, bestFitness);
/* 484*/      fitfun.setValueRange(worstFitness - bestFitness);
/* 485*/      if (this.generateStatistics) {
/* 486*/        this.statisticsSigmaHistory.add(this.sigma);
/* 487*/        this.statisticsFitnessHistory.add(bestFitness);
/* 488*/        this.statisticsMeanHistory.add(this.xmean.transpose());
/* 489*/        this.statisticsDHistory.add(this.diagD.transpose().scalarMultiply(100000.0D));
/*   0*/      } 
/*   0*/    } 
/* 492*/    return optimum;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkParameters() {
/* 499*/    double[] init = getStartPoint();
/* 500*/    double[] lB = getLowerBound();
/* 501*/    double[] uB = getUpperBound();
/*   0*/    boolean hasFiniteBounds = false;
/* 505*/    for (int i = 0; i < lB.length; i++) {
/* 506*/      if (!Double.isInfinite(lB[i]) || !Double.isInfinite(uB[i])) {
/* 508*/        hasFiniteBounds = true;
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/    boolean hasInfiniteBounds = false;
/* 514*/    if (hasFiniteBounds) {
/* 515*/      for (int j = 0; j < lB.length; j++) {
/* 516*/        if (Double.isInfinite(lB[j]) || Double.isInfinite(uB[j])) {
/* 518*/          hasInfiniteBounds = true;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/* 523*/      if (hasInfiniteBounds)
/* 526*/        throw new MathUnsupportedOperationException(); 
/* 529*/      this.boundaries = new double[2][];
/* 530*/      this.boundaries[0] = lB;
/* 531*/      this.boundaries[1] = uB;
/*   0*/    } else {
/* 535*/      this.boundaries = null;
/*   0*/    } 
/* 538*/    if (this.inputSigma != null) {
/* 539*/      if (this.inputSigma.length != init.length)
/* 540*/        throw new DimensionMismatchException(this.inputSigma.length, init.length); 
/* 542*/      for (int j = 0; j < init.length; j++) {
/* 543*/        if (this.inputSigma[j] < 0.0D)
/* 544*/          throw new NotPositiveException(this.inputSigma[j]); 
/* 546*/        if (this.boundaries != null && 
/* 547*/          this.inputSigma[j] > this.boundaries[1][j] - this.boundaries[0][j])
/* 548*/          throw new OutOfRangeException(this.inputSigma[j], 0, this.boundaries[1][j] - this.boundaries[0][j]); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void initializeCMA(double[] guess) {
/* 561*/    if (this.lambda <= 0)
/* 562*/      this.lambda = 4 + (int)(3.0D * Math.log(this.dimension)); 
/* 565*/    double[][] sigmaArray = new double[guess.length][1];
/* 566*/    for (int i = 0; i < guess.length; i++) {
/* 567*/      double range = (this.boundaries == null) ? 1.0D : (this.boundaries[1][i] - this.boundaries[0][i]);
/* 568*/      sigmaArray[i][0] = ((this.inputSigma == null) ? 0.3D : this.inputSigma[i]) / range;
/*   0*/    } 
/* 570*/    RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
/* 571*/    this.sigma = max(insigma);
/* 574*/    this.stopTolUpX = 1000.0D * max(insigma);
/* 575*/    this.stopTolX = 1.0E-11D * max(insigma);
/* 576*/    this.stopTolFun = 1.0E-12D;
/* 577*/    this.stopTolHistFun = 1.0E-13D;
/* 580*/    this.mu = this.lambda / 2;
/* 581*/    this.logMu2 = Math.log(this.mu + 0.5D);
/* 582*/    this.weights = log(sequence(1.0D, this.mu, 1.0D)).scalarMultiply(-1.0D).scalarAdd(this.logMu2);
/* 583*/    double sumw = 0.0D;
/* 584*/    double sumwq = 0.0D;
/* 585*/    for (int k = 0; k < this.mu; k++) {
/* 586*/      double w = this.weights.getEntry(k, 0);
/* 587*/      sumw += w;
/* 588*/      sumwq += w * w;
/*   0*/    } 
/* 590*/    this.weights = this.weights.scalarMultiply(1.0D / sumw);
/* 591*/    this.mueff = sumw * sumw / sumwq;
/* 594*/    this.cc = (4.0D + this.mueff / this.dimension) / (this.dimension + 4.0D + 2.0D * this.mueff / this.dimension);
/* 596*/    this.cs = (this.mueff + 2.0D) / (this.dimension + this.mueff + 3.0D);
/* 597*/    this;
/* 597*/    this.damps = (1.0D + 2.0D * Math.max(0.0D, Math.sqrt((sumwq - 1.0D) / (this.dimension + 1.0D)) - 1.0D)) * Math.max(0.3D, 1.0D - this.dimension / (1.0E-6D + Math.min(this.maxIterations, getMaxEvaluations() / this.lambda))) + this.cs;
/* 602*/    this.ccov1 = 2.0D / ((this.dimension + 1.3D) * (this.dimension + 1.3D) + this.mueff);
/* 603*/    this.ccovmu = Math.min(1.0D - this.ccov1, 2.0D * (this.mueff - 2.0D + 1.0D / this.mueff) / ((this.dimension + 2.0D) * (this.dimension + 2.0D) + this.mueff));
/* 605*/    this.ccov1Sep = Math.min(1.0D, this.ccov1 * (this.dimension + 1.5D) / 3.0D);
/* 606*/    this.ccovmuSep = Math.min(1.0D - this.ccov1, this.ccovmu * (this.dimension + 1.5D) / 3.0D);
/* 607*/    this.chiN = Math.sqrt(this.dimension) * (1.0D - 1.0D / 4.0D * this.dimension + 1.0D / 21.0D * this.dimension * this.dimension);
/* 610*/    this.xmean = MatrixUtils.createColumnRealMatrix(guess);
/* 612*/    this.diagD = insigma.scalarMultiply(1.0D / this.sigma);
/* 613*/    this.diagC = square(this.diagD);
/* 614*/    this.pc = zeros(this.dimension, 1);
/* 615*/    this.ps = zeros(this.dimension, 1);
/* 616*/    this.normps = this.ps.getFrobeniusNorm();
/* 618*/    this.B = eye(this.dimension, this.dimension);
/* 619*/    this.D = ones(this.dimension, 1);
/* 620*/    this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
/* 621*/    this.C = this.B.multiply(diag(square(this.D)).multiply(this.B.transpose()));
/* 622*/    this.historySize = 10 + (int)(30.0D * this.dimension / this.lambda);
/* 623*/    this.fitnessHistory = new double[this.historySize];
/* 624*/    for (int j = 0; j < this.historySize; j++)
/* 625*/      this.fitnessHistory[j] = Double.MAX_VALUE; 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean updateEvolutionPaths(RealMatrix zmean, RealMatrix xold) {
/* 638*/    this.ps = this.ps.scalarMultiply(1.0D - this.cs).add(this.B.multiply(zmean).scalarMultiply(Math.sqrt(this.cs * (2.0D - this.cs) * this.mueff)));
/* 641*/    this.normps = this.ps.getFrobeniusNorm();
/* 642*/    boolean hsig = (this.normps / Math.sqrt(1.0D - Math.pow(1.0D - this.cs, 2.0D * this.iterations)) / this.chiN < 1.4D + 2.0D / (this.dimension + 1.0D));
/* 645*/    this.pc = this.pc.scalarMultiply(1.0D - this.cc);
/* 646*/    if (hsig)
/* 647*/      this.pc = this.pc.add(this.xmean.subtract(xold).scalarMultiply(Math.sqrt(this.cc * (2.0D - this.cc) * this.mueff) / this.sigma)); 
/* 650*/    return hsig;
/*   0*/  }
/*   0*/  
/*   0*/  private void updateCovarianceDiagonalOnly(boolean hsig, RealMatrix bestArz, RealMatrix xold) {
/* 665*/    double oldFac = hsig ? 0.0D : (this.ccov1Sep * this.cc * (2.0D - this.cc));
/* 666*/    oldFac += 1.0D - this.ccov1Sep - this.ccovmuSep;
/* 667*/    this.diagC = this.diagC.scalarMultiply(oldFac).add(square(this.pc).scalarMultiply(this.ccov1Sep)).add(times(this.diagC, square(bestArz).multiply(this.weights)).scalarMultiply(this.ccovmuSep));
/* 673*/    this.diagD = sqrt(this.diagC);
/* 674*/    if (this.diagonalOnly > 1 && this.iterations > this.diagonalOnly) {
/* 676*/      this.diagonalOnly = 0;
/* 677*/      this.B = eye(this.dimension, this.dimension);
/* 678*/      this.BD = diag(this.diagD);
/* 679*/      this.C = diag(this.diagC);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateCovariance(boolean hsig, RealMatrix bestArx, RealMatrix arz, int[] arindex, RealMatrix xold) {
/* 696*/    double negccov = 0.0D;
/* 697*/    if (this.ccov1 + this.ccovmu > 0.0D) {
/* 698*/      RealMatrix arpos = bestArx.subtract(repmat(xold, 1, this.mu)).scalarMultiply(1.0D / this.sigma);
/* 700*/      RealMatrix roneu = this.pc.multiply(this.pc.transpose()).scalarMultiply(this.ccov1);
/* 703*/      double oldFac = hsig ? 0.0D : (this.ccov1 * this.cc * (2.0D - this.cc));
/* 704*/      oldFac += 1.0D - this.ccov1 - this.ccovmu;
/* 705*/      if (this.isActiveCMA) {
/* 707*/        negccov = (1.0D - this.ccovmu) * 0.25D * this.mueff / (Math.pow(this.dimension + 2.0D, 1.5D) + 2.0D * this.mueff);
/* 709*/        double negminresidualvariance = 0.66D;
/* 712*/        double negalphaold = 0.5D;
/* 715*/        int[] arReverseIndex = reverse(arindex);
/* 716*/        RealMatrix arzneg = selectColumns(arz, MathArrays.copyOf(arReverseIndex, this.mu));
/* 718*/        RealMatrix arnorms = sqrt(sumRows(square(arzneg)));
/* 719*/        int[] idxnorms = sortedIndices(arnorms.getRow(0));
/* 720*/        RealMatrix arnormsSorted = selectColumns(arnorms, idxnorms);
/* 721*/        int[] idxReverse = reverse(idxnorms);
/* 722*/        RealMatrix arnormsReverse = selectColumns(arnorms, idxReverse);
/* 723*/        arnorms = divide(arnormsReverse, arnormsSorted);
/* 724*/        int[] idxInv = inverse(idxnorms);
/* 725*/        RealMatrix arnormsInv = selectColumns(arnorms, idxInv);
/* 727*/        double negcovMax = (1.0D - negminresidualvariance) / square(arnormsInv).multiply(this.weights).getEntry(0, 0);
/* 729*/        if (negccov > negcovMax)
/* 730*/          negccov = negcovMax; 
/* 732*/        arzneg = times(arzneg, repmat(arnormsInv, this.dimension, 1));
/* 733*/        RealMatrix artmp = this.BD.multiply(arzneg);
/* 734*/        RealMatrix Cneg = artmp.multiply(diag(this.weights)).multiply(artmp.transpose());
/* 736*/        oldFac += negalphaold * negccov;
/* 737*/        this.C = this.C.scalarMultiply(oldFac).add(roneu).add(arpos.scalarMultiply(this.ccovmu + (1.0D - negalphaold) * negccov).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose()))).subtract(Cneg.scalarMultiply(negccov));
/*   0*/      } else {
/* 750*/        this.C = this.C.scalarMultiply(oldFac).add(roneu).add(arpos.scalarMultiply(this.ccovmu).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())));
/*   0*/      } 
/*   0*/    } 
/* 759*/    updateBD(negccov);
/*   0*/  }
/*   0*/  
/*   0*/  private void updateBD(double negccov) {
/* 768*/    if (this.ccov1 + this.ccovmu + negccov > 0.0D && this.iterations % 1.0D / (this.ccov1 + this.ccovmu + negccov) / this.dimension / 10.0D < 1.0D) {
/* 771*/      this.C = triu(this.C, 0).add(triu(this.C, 1).transpose());
/* 773*/      EigenDecomposition eig = new EigenDecomposition(this.C);
/* 774*/      this.B = eig.getV();
/* 775*/      this.D = eig.getD();
/* 776*/      this.diagD = diag(this.D);
/* 777*/      if (min(this.diagD) <= 0.0D) {
/* 778*/        for (int i = 0; i < this.dimension; i++) {
/* 779*/          if (this.diagD.getEntry(i, 0) < 0.0D)
/* 780*/            this.diagD.setEntry(i, 0, 0.0D); 
/*   0*/        } 
/* 783*/        double tfac = max(this.diagD) / 1.0E14D;
/* 784*/        this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
/* 785*/        this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
/*   0*/      } 
/* 787*/      if (max(this.diagD) > 1.0E14D * min(this.diagD)) {
/* 788*/        double tfac = max(this.diagD) / 1.0E14D - min(this.diagD);
/* 789*/        this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
/* 790*/        this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
/*   0*/      } 
/* 792*/      this.diagC = diag(this.C);
/* 793*/      this.diagD = sqrt(this.diagD);
/* 794*/      this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void push(double[] vals, double val) {
/* 805*/    for (int i = vals.length - 1; i > 0; i--)
/* 806*/      vals[i] = vals[i - 1]; 
/* 808*/    vals[0] = val;
/*   0*/  }
/*   0*/  
/*   0*/  private int[] sortedIndices(double[] doubles) {
/* 818*/    DoubleIndex[] dis = new DoubleIndex[doubles.length];
/* 819*/    for (int i = 0; i < doubles.length; i++)
/* 820*/      dis[i] = new DoubleIndex(doubles[i], i); 
/* 822*/    Arrays.sort((Object[])dis);
/* 823*/    int[] indices = new int[doubles.length];
/* 824*/    for (int j = 0; j < doubles.length; j++)
/* 825*/      indices[j] = (dis[j]).index; 
/* 827*/    return indices;
/*   0*/  }
/*   0*/  
/*   0*/  private static class DoubleIndex implements Comparable<DoubleIndex> {
/*   0*/    private double value;
/*   0*/    
/*   0*/    private int index;
/*   0*/    
/*   0*/    DoubleIndex(double value, int index) {
/* 845*/      this.value = value;
/* 846*/      this.index = index;
/*   0*/    }
/*   0*/    
/*   0*/    public int compareTo(DoubleIndex o) {
/* 851*/      return Double.compare(this.value, o.value);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object other) {
/* 858*/      if (this == other)
/* 859*/        return true; 
/* 862*/      if (other instanceof DoubleIndex)
/* 863*/        return (Double.compare(this.value, ((DoubleIndex)other).value) == 0); 
/* 866*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 873*/      long bits = Double.doubleToLongBits(this.value);
/* 874*/      return (int)((0x15F34EL ^ bits >>> 32L ^ bits) & 0xFFFFFFFFFFFFFFFFL);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class FitnessFunction {
/* 896*/    private double valueRange = 1.0D;
/*   0*/    
/*   0*/    private boolean isRepairMode = true;
/*   0*/    
/*   0*/    public double[] encode(double[] x) {
/* 905*/      if (CMAESOptimizer.this.boundaries == null)
/* 906*/        return x; 
/* 908*/      double[] res = new double[x.length];
/* 909*/      for (int i = 0; i < x.length; i++) {
/* 910*/        double diff = CMAESOptimizer.this.boundaries[1][i] - CMAESOptimizer.this.boundaries[0][i];
/* 911*/        res[i] = (x[i] - CMAESOptimizer.this.boundaries[0][i]) / diff;
/*   0*/      } 
/* 913*/      return res;
/*   0*/    }
/*   0*/    
/*   0*/    public double[] repairAndDecode(double[] x) {
/* 921*/      return decode(x);
/*   0*/    }
/*   0*/    
/*   0*/    public double[] decode(double[] x) {
/* 930*/      if (CMAESOptimizer.this.boundaries == null)
/* 931*/        return x; 
/* 933*/      double[] res = new double[x.length];
/* 934*/      for (int i = 0; i < x.length; i++) {
/* 935*/        double diff = CMAESOptimizer.this.boundaries[1][i] - CMAESOptimizer.this.boundaries[0][i];
/* 936*/        res[i] = diff * x[i] + CMAESOptimizer.this.boundaries[0][i];
/*   0*/      } 
/* 938*/      return res;
/*   0*/    }
/*   0*/    
/*   0*/    public double value(double[] point) {
/*   0*/      double value;
/* 947*/      if (CMAESOptimizer.this.boundaries != null && this.isRepairMode) {
/* 948*/        double[] repaired = repair(point);
/* 949*/        value = CMAESOptimizer.this.computeObjectiveValue(decode(repaired)) + penalty(point, repaired);
/*   0*/      } else {
/* 953*/        value = CMAESOptimizer.this.computeObjectiveValue(decode(point));
/*   0*/      } 
/* 956*/      return CMAESOptimizer.this.isMinimize ? value : -value;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isFeasible(double[] x) {
/* 964*/      if (CMAESOptimizer.this.boundaries == null)
/* 965*/        return true; 
/* 967*/      for (int i = 0; i < x.length; i++) {
/* 968*/        if (x[i] < 0.0D)
/* 969*/          return false; 
/* 971*/        if (x[i] > 1.0D)
/* 972*/          return false; 
/*   0*/      } 
/* 975*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void setValueRange(double valueRange) {
/* 982*/      this.valueRange = valueRange;
/*   0*/    }
/*   0*/    
/*   0*/    private double[] repair(double[] x) {
/* 990*/      double[] repaired = new double[x.length];
/* 991*/      for (int i = 0; i < x.length; i++) {
/* 992*/        if (x[i] < 0.0D) {
/* 993*/          repaired[i] = 0.0D;
/* 994*/        } else if (x[i] > 1.0D) {
/* 995*/          repaired[i] = 1.0D;
/*   0*/        } else {
/* 997*/          repaired[i] = x[i];
/*   0*/        } 
/*   0*/      } 
/*1000*/      return repaired;
/*   0*/    }
/*   0*/    
/*   0*/    private double penalty(double[] x, double[] repaired) {
/*1009*/      double penalty = 0.0D;
/*1010*/      for (int i = 0; i < x.length; i++) {
/*1011*/        double diff = Math.abs(x[i] - repaired[i]);
/*1012*/        penalty += diff * this.valueRange;
/*   0*/      } 
/*1014*/      return CMAESOptimizer.this.isMinimize ? penalty : -penalty;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix log(RealMatrix m) {
/*1025*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1026*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1027*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1028*/        d[r][c] = Math.log(m.getEntry(r, c)); 
/*   0*/    } 
/*1031*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix sqrt(RealMatrix m) {
/*1040*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1041*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1042*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1043*/        d[r][c] = Math.sqrt(m.getEntry(r, c)); 
/*   0*/    } 
/*1046*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix square(RealMatrix m) {
/*1054*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1055*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1056*/      for (int c = 0; c < m.getColumnDimension(); c++) {
/*1057*/        double e = m.getEntry(r, c);
/*1058*/        d[r][c] = e * e;
/*   0*/      } 
/*   0*/    } 
/*1061*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix times(RealMatrix m, RealMatrix n) {
/*1070*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1071*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1072*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1073*/        d[r][c] = m.getEntry(r, c) * n.getEntry(r, c); 
/*   0*/    } 
/*1076*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix divide(RealMatrix m, RealMatrix n) {
/*1085*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1086*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1087*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1088*/        d[r][c] = m.getEntry(r, c) / n.getEntry(r, c); 
/*   0*/    } 
/*1091*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix selectColumns(RealMatrix m, int[] cols) {
/*1100*/    double[][] d = new double[m.getRowDimension()][cols.length];
/*1101*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1102*/      for (int c = 0; c < cols.length; c++)
/*1103*/        d[r][c] = m.getEntry(r, cols[c]); 
/*   0*/    } 
/*1106*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix triu(RealMatrix m, int k) {
/*1115*/    double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
/*1116*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1117*/      for (int c = 0; c < m.getColumnDimension(); c++)
/*1118*/        d[r][c] = (r <= c - k) ? m.getEntry(r, c) : 0.0D; 
/*   0*/    } 
/*1121*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix sumRows(RealMatrix m) {
/*1129*/    double[][] d = new double[1][m.getColumnDimension()];
/*1130*/    for (int c = 0; c < m.getColumnDimension(); c++) {
/*1131*/      double sum = 0.0D;
/*1132*/      for (int r = 0; r < m.getRowDimension(); r++)
/*1133*/        sum += m.getEntry(r, c); 
/*1135*/      d[0][c] = sum;
/*   0*/    } 
/*1137*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix diag(RealMatrix m) {
/*1146*/    if (m.getColumnDimension() == 1) {
/*1147*/      double[][] arrayOfDouble = new double[m.getRowDimension()][m.getRowDimension()];
/*1148*/      for (int j = 0; j < m.getRowDimension(); j++)
/*1149*/        arrayOfDouble[j][j] = m.getEntry(j, 0); 
/*1151*/      return new Array2DRowRealMatrix(arrayOfDouble, false);
/*   0*/    } 
/*1153*/    double[][] d = new double[m.getRowDimension()][1];
/*1154*/    for (int i = 0; i < m.getColumnDimension(); i++)
/*1155*/      d[i][0] = m.getEntry(i, i); 
/*1157*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static void copyColumn(RealMatrix m1, int col1, RealMatrix m2, int col2) {
/*1170*/    for (int i = 0; i < m1.getRowDimension(); i++)
/*1171*/      m2.setEntry(i, col2, m1.getEntry(i, col1)); 
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix ones(int n, int m) {
/*1181*/    double[][] d = new double[n][m];
/*1182*/    for (int r = 0; r < n; r++)
/*1183*/      Arrays.fill(d[r], 1.0D); 
/*1185*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix eye(int n, int m) {
/*1194*/    double[][] d = new double[n][m];
/*1195*/    for (int r = 0; r < n; r++) {
/*1196*/      if (r < m)
/*1197*/        d[r][r] = 1.0D; 
/*   0*/    } 
/*1200*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix zeros(int n, int m) {
/*1209*/    return new Array2DRowRealMatrix(n, m);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix repmat(RealMatrix mat, int n, int m) {
/*1219*/    int rd = mat.getRowDimension();
/*1220*/    int cd = mat.getColumnDimension();
/*1221*/    double[][] d = new double[n * rd][m * cd];
/*1222*/    for (int r = 0; r < n * rd; r++) {
/*1223*/      for (int c = 0; c < m * cd; c++)
/*1224*/        d[r][c] = mat.getEntry(r % rd, c % cd); 
/*   0*/    } 
/*1227*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static RealMatrix sequence(double start, double end, double step) {
/*1237*/    int size = (int)((end - start) / step + 1.0D);
/*1238*/    double[][] d = new double[size][1];
/*1239*/    double value = start;
/*1240*/    for (int r = 0; r < size; r++) {
/*1241*/      d[r][0] = value;
/*1242*/      value += step;
/*   0*/    } 
/*1244*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static double max(RealMatrix m) {
/*1252*/    double max = -1.7976931348623157E308D;
/*1253*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1254*/      for (int c = 0; c < m.getColumnDimension(); c++) {
/*1255*/        double e = m.getEntry(r, c);
/*1256*/        if (max < e)
/*1257*/          max = e; 
/*   0*/      } 
/*   0*/    } 
/*1261*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  private static double min(RealMatrix m) {
/*1269*/    double min = Double.MAX_VALUE;
/*1270*/    for (int r = 0; r < m.getRowDimension(); r++) {
/*1271*/      for (int c = 0; c < m.getColumnDimension(); c++) {
/*1272*/        double e = m.getEntry(r, c);
/*1273*/        if (min > e)
/*1274*/          min = e; 
/*   0*/      } 
/*   0*/    } 
/*1278*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  private static double max(double[] m) {
/*1286*/    double max = -1.7976931348623157E308D;
/*1287*/    for (int r = 0; r < m.length; r++) {
/*1288*/      if (max < m[r])
/*1289*/        max = m[r]; 
/*   0*/    } 
/*1292*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  private static double min(double[] m) {
/*1300*/    double min = Double.MAX_VALUE;
/*1301*/    for (int r = 0; r < m.length; r++) {
/*1302*/      if (min > m[r])
/*1303*/        min = m[r]; 
/*   0*/    } 
/*1306*/    return min;
/*   0*/  }
/*   0*/  
/*   0*/  private static int[] inverse(int[] indices) {
/*1314*/    int[] inverse = new int[indices.length];
/*1315*/    for (int i = 0; i < indices.length; i++)
/*1316*/      inverse[indices[i]] = i; 
/*1318*/    return inverse;
/*   0*/  }
/*   0*/  
/*   0*/  private static int[] reverse(int[] indices) {
/*1326*/    int[] reverse = new int[indices.length];
/*1327*/    for (int i = 0; i < indices.length; i++)
/*1328*/      reverse[i] = indices[indices.length - i - 1]; 
/*1330*/    return reverse;
/*   0*/  }
/*   0*/  
/*   0*/  private double[] randn(int size) {
/*1338*/    double[] randn = new double[size];
/*1339*/    for (int i = 0; i < size; i++)
/*1340*/      randn[i] = this.random.nextGaussian(); 
/*1342*/    return randn;
/*   0*/  }
/*   0*/  
/*   0*/  private RealMatrix randn1(int size, int popSize) {
/*1351*/    double[][] d = new double[size][popSize];
/*1352*/    for (int r = 0; r < size; r++) {
/*1353*/      for (int c = 0; c < popSize; c++)
/*1354*/        d[r][c] = this.random.nextGaussian(); 
/*   0*/    } 
/*1357*/    return new Array2DRowRealMatrix(d, false);
/*   0*/  }
/*   0*/}
