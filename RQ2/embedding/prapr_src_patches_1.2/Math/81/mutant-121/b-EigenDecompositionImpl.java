/*   0*/package org.apache.commons.math.linear;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/
/*   0*/public class EigenDecompositionImpl implements EigenDecomposition {
/*   0*/  private static final double TOLERANCE = 1.1102230246251565E-14D;
/*   0*/  
/*   0*/  private static final double TOLERANCE_2 = 1.232595164407831E-28D;
/*   0*/  
/*   0*/  private double splitTolerance;
/*   0*/  
/*   0*/  private double[] main;
/*   0*/  
/*   0*/  private double[] secondary;
/*   0*/  
/*   0*/  private double[] squaredSecondary;
/*   0*/  
/*   0*/  private TriDiagonalTransformer transformer;
/*   0*/  
/*   0*/  private double lowerSpectra;
/*   0*/  
/*   0*/  private double upperSpectra;
/*   0*/  
/*   0*/  private double minPivot;
/*   0*/  
/*   0*/  private double sigma;
/*   0*/  
/*   0*/  private double sigmaLow;
/*   0*/  
/*   0*/  private double tau;
/*   0*/  
/*   0*/  private double[] work;
/*   0*/  
/*   0*/  private int pingPong;
/*   0*/  
/*   0*/  private double qMax;
/*   0*/  
/*   0*/  private double eMin;
/*   0*/  
/*   0*/  private int tType;
/*   0*/  
/*   0*/  private double dMin;
/*   0*/  
/*   0*/  private double dMin1;
/*   0*/  
/*   0*/  private double dMin2;
/*   0*/  
/*   0*/  private double dN;
/*   0*/  
/*   0*/  private double dN1;
/*   0*/  
/*   0*/  private double dN2;
/*   0*/  
/*   0*/  private double g;
/*   0*/  
/*   0*/  private double[] realEigenvalues;
/*   0*/  
/*   0*/  private double[] imagEigenvalues;
/*   0*/  
/*   0*/  private ArrayRealVector[] eigenvectors;
/*   0*/  
/*   0*/  private RealMatrix cachedV;
/*   0*/  
/*   0*/  private RealMatrix cachedD;
/*   0*/  
/*   0*/  private RealMatrix cachedVt;
/*   0*/  
/*   0*/  public EigenDecompositionImpl(RealMatrix matrix, double splitTolerance) throws InvalidMatrixException {
/* 168*/    if (isSymmetric(matrix)) {
/* 169*/      this.splitTolerance = splitTolerance;
/* 170*/      transformToTridiagonal(matrix);
/* 171*/      decompose();
/*   0*/    } else {
/* 175*/      throw new InvalidMatrixException("eigen decomposition of assymetric matrices not supported yet", new Object[0]);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public EigenDecompositionImpl(double[] main, double[] secondary, double splitTolerance) throws InvalidMatrixException {
/* 193*/    this.main = (double[])main.clone();
/* 194*/    this.secondary = (double[])secondary.clone();
/* 195*/    this.transformer = null;
/* 198*/    this.squaredSecondary = new double[secondary.length];
/* 199*/    for (int i = 0; i < this.squaredSecondary.length; i++) {
/* 200*/      double s = secondary[i];
/* 201*/      this.squaredSecondary[i] = s * s;
/*   0*/    } 
/* 204*/    this.splitTolerance = splitTolerance;
/* 205*/    decompose();
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isSymmetric(RealMatrix matrix) {
/* 215*/    int rows = matrix.getRowDimension();
/* 216*/    int columns = matrix.getColumnDimension();
/* 217*/    double eps = (10 * rows * columns) * 1.1102230246251565E-16D;
/* 218*/    for (int i = 0; i < rows; i++) {
/* 219*/      for (int j = i + 1; j < columns; j++) {
/* 220*/        double mij = matrix.getEntry(i, j);
/* 221*/        double mji = matrix.getEntry(j, i);
/* 222*/        if (Math.abs(mij - mji) > Math.max(Math.abs(mij), Math.abs(mji)) * eps)
/* 223*/          return false; 
/*   0*/      } 
/*   0*/    } 
/* 227*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void decompose() {
/* 237*/    this.cachedV = null;
/* 238*/    this.cachedD = null;
/* 239*/    this.cachedVt = null;
/* 240*/    this.work = new double[6 * this.main.length];
/* 243*/    computeGershgorinCircles();
/* 246*/    findEigenvalues();
/* 249*/    this.eigenvectors = null;
/*   0*/  }
/*   0*/  
/*   0*/  public RealMatrix getV() throws InvalidMatrixException {
/* 257*/    if (this.cachedV == null) {
/* 259*/      if (this.eigenvectors == null)
/* 260*/        findEigenVectors(); 
/* 263*/      int m = this.eigenvectors.length;
/* 264*/      this.cachedV = MatrixUtils.createRealMatrix(m, m);
/* 265*/      for (int k = 0; k < m; k++)
/* 266*/        this.cachedV.setColumnVector(k, this.eigenvectors[k]); 
/*   0*/    } 
/* 272*/    return this.cachedV;
/*   0*/  }
/*   0*/  
/*   0*/  public RealMatrix getD() throws InvalidMatrixException {
/* 279*/    if (this.cachedD == null)
/* 281*/      this.cachedD = MatrixUtils.createRealDiagonalMatrix(this.realEigenvalues); 
/* 283*/    return this.cachedD;
/*   0*/  }
/*   0*/  
/*   0*/  public RealMatrix getVT() throws InvalidMatrixException {
/* 290*/    if (this.cachedVt == null) {
/* 292*/      if (this.eigenvectors == null)
/* 293*/        findEigenVectors(); 
/* 296*/      int m = this.eigenvectors.length;
/* 297*/      this.cachedVt = MatrixUtils.createRealMatrix(m, m);
/* 298*/      for (int k = 0; k < m; k++)
/* 299*/        this.cachedVt.setRowVector(k, this.eigenvectors[k]); 
/*   0*/    } 
/* 305*/    return this.cachedVt;
/*   0*/  }
/*   0*/  
/*   0*/  public double[] getRealEigenvalues() throws InvalidMatrixException {
/* 312*/    return (double[])this.realEigenvalues.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public double getRealEigenvalue(int i) throws InvalidMatrixException, ArrayIndexOutOfBoundsException {
/* 318*/    return this.realEigenvalues[i];
/*   0*/  }
/*   0*/  
/*   0*/  public double[] getImagEigenvalues() throws InvalidMatrixException {
/* 324*/    return (double[])this.imagEigenvalues.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public double getImagEigenvalue(int i) throws InvalidMatrixException, ArrayIndexOutOfBoundsException {
/* 330*/    return this.imagEigenvalues[i];
/*   0*/  }
/*   0*/  
/*   0*/  public RealVector getEigenvector(int i) throws InvalidMatrixException, ArrayIndexOutOfBoundsException {
/* 336*/    if (this.eigenvectors == null)
/* 337*/      findEigenVectors(); 
/* 339*/    return this.eigenvectors[i].copy();
/*   0*/  }
/*   0*/  
/*   0*/  public double getDeterminant() {
/* 347*/    double determinant = 1.0D;
/* 348*/    for (double lambda : this.realEigenvalues)
/* 349*/      determinant *= lambda; 
/* 351*/    return determinant;
/*   0*/  }
/*   0*/  
/*   0*/  public DecompositionSolver getSolver() {
/* 356*/    if (this.eigenvectors == null)
/* 357*/      findEigenVectors(); 
/* 359*/    return new Solver(this.realEigenvalues, this.imagEigenvalues, this.eigenvectors);
/*   0*/  }
/*   0*/  
/*   0*/  private static class Solver implements DecompositionSolver {
/*   0*/    private double[] realEigenvalues;
/*   0*/    
/*   0*/    private double[] imagEigenvalues;
/*   0*/    
/*   0*/    private final ArrayRealVector[] eigenvectors;
/*   0*/    
/*   0*/    private Solver(double[] realEigenvalues, double[] imagEigenvalues, ArrayRealVector[] eigenvectors) {
/* 382*/      this.realEigenvalues = realEigenvalues;
/* 383*/      this.imagEigenvalues = imagEigenvalues;
/* 384*/      this.eigenvectors = eigenvectors;
/*   0*/    }
/*   0*/    
/*   0*/    public double[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
/* 398*/      if (!isNonSingular())
/* 399*/        throw new SingularMatrixException(); 
/* 402*/      int m = this.realEigenvalues.length;
/* 403*/      if (b.length != m)
/* 404*/        throw MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", new Object[] { b.length, m }); 
/* 409*/      double[] bp = new double[m];
/* 410*/      for (int i = 0; i < m; i++) {
/* 411*/        ArrayRealVector v = this.eigenvectors[i];
/* 412*/        double[] vData = v.getDataRef();
/* 413*/        double s = v.dotProduct(b) / this.realEigenvalues[i];
/* 414*/        for (int j = 0; j < m; j++)
/* 415*/          bp[j] = bp[j] + s * vData[j]; 
/*   0*/      } 
/* 419*/      return bp;
/*   0*/    }
/*   0*/    
/*   0*/    public RealVector solve(RealVector b) throws IllegalArgumentException, InvalidMatrixException {
/* 434*/      if (!isNonSingular())
/* 435*/        throw new SingularMatrixException(); 
/* 438*/      int m = this.realEigenvalues.length;
/* 439*/      if (b.getDimension() != m)
/* 440*/        throw MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", new Object[] { b.getDimension(), m }); 
/* 445*/      double[] bp = new double[m];
/* 446*/      for (int i = 0; i < m; i++) {
/* 447*/        ArrayRealVector v = this.eigenvectors[i];
/* 448*/        double[] vData = v.getDataRef();
/* 449*/        double s = v.dotProduct(b) / this.realEigenvalues[i];
/* 450*/        for (int j = 0; j < m; j++)
/* 451*/          bp[j] = bp[j] + s * vData[j]; 
/*   0*/      } 
/* 455*/      return new ArrayRealVector(bp, false);
/*   0*/    }
/*   0*/    
/*   0*/    public RealMatrix solve(RealMatrix b) throws IllegalArgumentException, InvalidMatrixException {
/* 470*/      if (!isNonSingular())
/* 471*/        throw new SingularMatrixException(); 
/* 474*/      int m = this.realEigenvalues.length;
/* 475*/      if (b.getRowDimension() != m)
/* 476*/        throw MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", new Object[] { b.getRowDimension(), b.getColumnDimension(), m, "n" }); 
/* 481*/      int nColB = b.getColumnDimension();
/* 482*/      double[][] bp = new double[m][nColB];
/* 483*/      for (int k = 0; k < nColB; k++) {
/* 484*/        for (int i = 0; i < m; i++) {
/* 485*/          ArrayRealVector v = this.eigenvectors[i];
/* 486*/          double[] vData = v.getDataRef();
/* 487*/          double s = 0.0D;
/* 488*/          for (int j = 0; j < m; j++)
/* 489*/            s += v.getEntry(j) * b.getEntry(j, k); 
/* 491*/          s /= this.realEigenvalues[i];
/* 492*/          for (int n = 0; n < m; n++)
/* 493*/            bp[n][k] = bp[n][k] + s * vData[n]; 
/*   0*/        } 
/*   0*/      } 
/* 498*/      return MatrixUtils.createRealMatrix(bp);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isNonSingular() {
/* 507*/      for (int i = 0; i < this.realEigenvalues.length; i++) {
/* 508*/        if (this.realEigenvalues[i] == 0.0D && this.imagEigenvalues[i] == 0.0D)
/* 509*/          return false; 
/*   0*/      } 
/* 512*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public RealMatrix getInverse() throws InvalidMatrixException {
/* 522*/      if (!isNonSingular())
/* 523*/        throw new SingularMatrixException(); 
/* 526*/      int m = this.realEigenvalues.length;
/* 527*/      double[][] invData = new double[m][m];
/* 529*/      for (int i = 0; i < m; i++) {
/* 530*/        double[] invI = invData[i];
/* 531*/        for (int j = 0; j < m; j++) {
/* 532*/          double invIJ = 0.0D;
/* 533*/          for (int k = 0; k < m; k++) {
/* 534*/            double[] vK = this.eigenvectors[k].getDataRef();
/* 535*/            invIJ += vK[i] * vK[j] / this.realEigenvalues[k];
/*   0*/          } 
/* 537*/          invI[j] = invIJ;
/*   0*/        } 
/*   0*/      } 
/* 540*/      return MatrixUtils.createRealMatrix(invData);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private void transformToTridiagonal(RealMatrix matrix) {
/* 553*/    this.transformer = new TriDiagonalTransformer(matrix);
/* 554*/    this.main = this.transformer.getMainDiagonalRef();
/* 555*/    this.secondary = this.transformer.getSecondaryDiagonalRef();
/* 558*/    this.squaredSecondary = new double[this.secondary.length];
/* 559*/    for (int i = 0; i < this.squaredSecondary.length; i++) {
/* 560*/      double s = this.secondary[i];
/* 561*/      this.squaredSecondary[i] = s * s;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void computeGershgorinCircles() {
/* 571*/    int m = this.main.length;
/* 572*/    int lowerStart = 4 * m;
/* 573*/    int upperStart = 5 * m;
/* 574*/    this.lowerSpectra = Double.POSITIVE_INFINITY;
/* 575*/    this.upperSpectra = Double.NEGATIVE_INFINITY;
/* 576*/    double eMax = 0.0D;
/* 578*/    double eCurrent = 0.0D;
/* 579*/    for (int i = 0; i < m - 1; i++) {
/* 581*/      double d1 = this.main[i];
/* 582*/      double ePrevious = eCurrent;
/* 583*/      eCurrent = Math.abs(this.secondary[i]);
/* 584*/      eMax = Math.max(eMax, eCurrent);
/* 585*/      double radius = ePrevious + eCurrent;
/* 587*/      double d2 = d1 - radius;
/* 588*/      this.work[lowerStart + i] = d2;
/* 589*/      this.lowerSpectra = Math.min(this.lowerSpectra, d2);
/* 591*/      double d3 = d1 + radius;
/* 592*/      this.work[upperStart + i] = d3;
/* 593*/      this.upperSpectra = Math.max(this.upperSpectra, d3);
/*   0*/    } 
/* 597*/    double dCurrent = this.main[m - 1];
/* 598*/    double lower = dCurrent - eCurrent;
/* 599*/    this.work[lowerStart + m - 1] = lower;
/* 600*/    this.lowerSpectra = Math.min(this.lowerSpectra, lower);
/* 601*/    double upper = dCurrent + eCurrent;
/* 602*/    this.work[upperStart + m - 1] = upper;
/* 603*/    this.minPivot = 2.2250738585072014E-308D * Math.max(1.0D, eMax * eMax);
/*   0*/  }
/*   0*/  
/*   0*/  private void findEigenvalues() throws InvalidMatrixException {
/* 615*/    List<Integer> splitIndices = computeSplits();
/* 618*/    this.realEigenvalues = new double[this.main.length];
/* 619*/    this.imagEigenvalues = new double[this.main.length];
/* 620*/    int begin = 0;
/* 621*/    for (Iterator<Integer> i$ = splitIndices.iterator(); i$.hasNext(); ) {
/*   0*/      double range[], oneFourth;
/*   0*/      int oneFourthCount;
/*   0*/      double threeFourth;
/*   0*/      int threeFourthCount;
/*   0*/      boolean chooseLeft;
/*   0*/      double lambda;
/*   0*/      int k;
/* 621*/      int end = (Integer)i$.next();
/* 622*/      int n = end - begin;
/* 623*/      switch (n) {
/*   0*/        case 1:
/* 627*/          process1RowBlock(begin);
/*   0*/          break;
/*   0*/        case 2:
/* 632*/          process2RowsBlock(begin);
/*   0*/          break;
/*   0*/        case 3:
/* 637*/          process3RowsBlock(begin);
/*   0*/          break;
/*   0*/        default:
/* 643*/          range = eigenvaluesRange(begin, n);
/* 644*/          oneFourth = 0.25D * (3.0D * range[0] + range[1]);
/* 645*/          oneFourthCount = countEigenValues(oneFourth, begin, n);
/* 646*/          threeFourth = 0.25D * (range[0] + 3.0D * range[1]);
/* 647*/          threeFourthCount = countEigenValues(threeFourth, begin, n);
/* 648*/          chooseLeft = (oneFourthCount - 1 >= n - threeFourthCount);
/* 649*/          lambda = chooseLeft ? range[0] : range[1];
/* 651*/          this.tau = (range[1] - range[0]) * 1.1102230246251565E-16D * n + 2.0D * this.minPivot;
/* 654*/          ldlTDecomposition(lambda, begin, n);
/* 657*/          processGeneralBlock(n);
/* 660*/          if (chooseLeft) {
/* 661*/            for (int m = 0; m < n; m++)
/* 662*/              this.realEigenvalues[begin + m] = lambda + this.work[4 * m]; 
/*   0*/            break;
/*   0*/          } 
/* 665*/          for (k = 0; k < n; k++)
/* 666*/            this.realEigenvalues[begin + k] = lambda - this.work[4 * k]; 
/*   0*/          break;
/*   0*/      } 
/* 671*/      begin = end;
/*   0*/    } 
/* 675*/    Arrays.sort(this.realEigenvalues);
/* 676*/    int j = this.realEigenvalues.length - 1;
/* 677*/    for (int i = 0; i < j; i++) {
/* 678*/      double tmp = this.realEigenvalues[i];
/* 679*/      this.realEigenvalues[i] = this.realEigenvalues[j];
/* 680*/      this.realEigenvalues[j] = tmp;
/* 681*/      j--;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private List<Integer> computeSplits() {
/* 692*/    List<Integer> list = new ArrayList<Integer>();
/* 695*/    double absDCurrent = Math.abs(this.main[0]);
/* 696*/    for (int i = 0; i < this.secondary.length; i++) {
/* 697*/      double absDPrevious = absDCurrent;
/* 698*/      absDCurrent = Math.abs(this.main[i + 1]);
/* 699*/      double max = this.splitTolerance * Math.sqrt(absDPrevious * absDCurrent);
/* 700*/      if (Math.abs(this.secondary[i]) <= max) {
/* 701*/        list.add(i + 1);
/* 702*/        this.secondary[i] = 0.0D;
/* 703*/        this.squaredSecondary[i] = 0.0D;
/*   0*/      } 
/*   0*/    } 
/* 707*/    list.add(this.secondary.length + 1);
/* 708*/    return list;
/*   0*/  }
/*   0*/  
/*   0*/  private void process1RowBlock(int index) {
/* 718*/    this.realEigenvalues[index] = this.main[index];
/*   0*/  }
/*   0*/  
/*   0*/  private void process2RowsBlock(int index) throws InvalidMatrixException {
/* 732*/    double q0 = this.main[index];
/* 733*/    double q1 = this.main[index + 1];
/* 734*/    double e12 = this.squaredSecondary[index];
/* 736*/    double s = q0 + q1;
/* 737*/    double p = q0 * q1 - e12;
/* 738*/    double delta = s * s - 4.0D * p;
/* 739*/    if (delta < 0.0D)
/* 740*/      throw new InvalidMatrixException("cannot solve degree {0} equation", new Object[] { 2 }); 
/* 743*/    double largestRoot = 0.5D * (s + Math.sqrt(delta));
/* 744*/    this.realEigenvalues[index] = largestRoot;
/* 745*/    this.realEigenvalues[index + 1] = p / largestRoot;
/*   0*/  }
/*   0*/  
/*   0*/  private void process3RowsBlock(int index) throws InvalidMatrixException {
/* 760*/    double q0 = this.main[index];
/* 761*/    double q1 = this.main[index + 1];
/* 762*/    double q2 = this.main[index + 2];
/* 763*/    double e12 = this.squaredSecondary[index];
/* 764*/    double q1q2Me22 = q1 * q2 - this.squaredSecondary[index + 1];
/* 767*/    double b = -(q0 + q1 + q2);
/* 768*/    double c = q0 * q1 + q0 * q2 + q1q2Me22 - e12;
/* 769*/    double d = q2 * e12 - q0 * q1q2Me22;
/* 772*/    double b2 = b * b;
/* 773*/    double q = (3.0D * c - b2) / 9.0D;
/* 774*/    double r = ((9.0D * c - 2.0D * b2) * b - 27.0D * d) / 54.0D;
/* 775*/    double delta = q * q * q + r * r;
/* 776*/    if (delta >= 0.0D)
/* 780*/      throw new InvalidMatrixException("cannot solve degree {0} equation", new Object[] { 3 }); 
/* 782*/    double sqrtMq = Math.sqrt(-q);
/* 783*/    double theta = Math.acos(r / -q * sqrtMq);
/* 784*/    double alpha = 2.0D * sqrtMq;
/* 785*/    double beta = b / 3.0D;
/* 787*/    double z0 = alpha * Math.cos(theta / 3.0D) - beta;
/* 788*/    double z1 = alpha * Math.cos((theta + 6.283185307179586D) / 3.0D) - beta;
/* 789*/    double z2 = alpha * Math.cos((theta + 12.566370614359172D) / 3.0D) - beta;
/* 790*/    if (z0 < z1) {
/* 791*/      double t = z0;
/* 792*/      z0 = z1;
/* 793*/      z1 = t;
/*   0*/    } 
/* 795*/    if (z1 < z2) {
/* 796*/      double t = z1;
/* 797*/      z1 = z2;
/* 798*/      z2 = t;
/*   0*/    } 
/* 800*/    if (z0 < z1) {
/* 801*/      double t = z0;
/* 802*/      z0 = z1;
/* 803*/      z1 = t;
/*   0*/    } 
/* 805*/    this.realEigenvalues[index] = z0;
/* 806*/    this.realEigenvalues[index + 1] = z1;
/* 807*/    this.realEigenvalues[index + 2] = z2;
/*   0*/  }
/*   0*/  
/*   0*/  private void processGeneralBlock(int n) throws InvalidMatrixException {
/* 826*/    double sumOffDiag = 0.0D;
/* 827*/    for (int i = 0; i < n - 1; i++) {
/* 828*/      int fourI = 4 * i;
/* 829*/      double ei = this.work[fourI + 2];
/* 830*/      sumOffDiag += ei;
/*   0*/    } 
/* 833*/    if (sumOffDiag == 0.0D)
/*   0*/      return; 
/* 839*/    flipIfWarranted(n, 2);
/* 842*/    initialSplits(n);
/* 845*/    this.tType = 0;
/* 846*/    this.dMin1 = 0.0D;
/* 847*/    this.dMin2 = 0.0D;
/* 848*/    this.dN = 0.0D;
/* 849*/    this.dN1 = 0.0D;
/* 850*/    this.dN2 = 0.0D;
/* 851*/    this.tau = 0.0D;
/* 854*/    int i0 = 0;
/* 855*/    int n0 = n;
/* 856*/    while (n0 > 0) {
/* 859*/      this.sigma = (n0 == n) ? 0.0D : -this.work[4 * n0 - 2];
/* 860*/      this.sigmaLow = 0.0D;
/* 863*/      double offDiagMin = (i0 == n0) ? 0.0D : this.work[4 * n0 - 6];
/* 864*/      double offDiagMax = 0.0D;
/* 865*/      double diagMax = this.work[4 * n0 - 4];
/* 866*/      double diagMin = diagMax;
/* 867*/      i0 = 0;
/* 868*/      for (int j = 4 * (n0 - 2); j >= 0; j -= 4) {
/* 869*/        if (this.work[j + 2] <= 0.0D) {
/* 870*/          i0 = 1 + j / 4;
/*   0*/          break;
/*   0*/        } 
/* 873*/        if (diagMin >= 4.0D * offDiagMax) {
/* 874*/          diagMin = Math.min(diagMin, this.work[j + 4]);
/* 875*/          offDiagMax = Math.max(offDiagMax, this.work[j + 2]);
/*   0*/        } 
/* 877*/        diagMax = Math.max(diagMax, this.work[j] + this.work[j + 2]);
/* 878*/        offDiagMin = Math.min(offDiagMin, this.work[j + 2]);
/*   0*/      } 
/* 880*/      this.work[4 * n0 - 2] = offDiagMin;
/* 883*/      this.dMin = -Math.max(0.0D, diagMin - 2.0D * Math.sqrt(diagMin * offDiagMax));
/* 885*/      this.pingPong = 0;
/* 886*/      int maxIter = 30 * (n0 - i0);
/* 887*/      for (int k = 0; i0 < n0; k++) {
/* 888*/        if (k >= maxIter)
/* 889*/          throw new InvalidMatrixException(new MaxIterationsExceededException(maxIter)); 
/* 893*/        n0 = goodStep(i0, n0);
/* 894*/        this.pingPong = 1 - this.pingPong;
/* 898*/        if (this.pingPong == 0 && n0 - i0 > 3 && this.work[4 * n0 - 1] <= 1.232595164407831E-28D * diagMax && this.work[4 * n0 - 2] <= 1.232595164407831E-28D * this.sigma) {
/* 901*/          int split = i0 - 1;
/* 902*/          diagMax = this.work[4 * i0];
/* 903*/          offDiagMin = this.work[4 * i0 + 2];
/* 904*/          double previousEMin = this.work[4 * i0 + 3];
/* 905*/          for (int m = 4 * i0; m < 4 * n0 - 11; m += 4) {
/* 906*/            if (this.work[m + 3] <= 1.232595164407831E-28D * this.work[m] && this.work[m + 2] <= 1.232595164407831E-28D * this.sigma) {
/* 909*/              this.work[m + 2] = -this.sigma;
/* 910*/              split = m / 4;
/* 911*/              diagMax = 0.0D;
/* 912*/              offDiagMin = this.work[m + 6];
/* 913*/              previousEMin = this.work[m + 7];
/*   0*/            } else {
/* 915*/              diagMax = Math.max(diagMax, this.work[m + 4]);
/* 916*/              offDiagMin = Math.min(offDiagMin, this.work[m + 2]);
/* 917*/              previousEMin = Math.min(previousEMin, this.work[m + 3]);
/*   0*/            } 
/*   0*/          } 
/* 920*/          this.work[4 * n0 - 2] = offDiagMin;
/* 921*/          this.work[4 * n0 - 1] = previousEMin;
/* 922*/          i0 = split + 1;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void initialSplits(int n) {
/* 936*/    this.pingPong = 0;
/* 937*/    for (int k = 0; k < 2; k++) {
/* 940*/      double d = this.work[4 * (n - 1) + this.pingPong];
/* 941*/      for (int i = 4 * (n - 2) + this.pingPong; i >= 0; i -= 4) {
/* 942*/        if (this.work[i + 2] <= 1.232595164407831E-28D * d) {
/* 943*/          this.work[i + 2] = -0.0D;
/* 944*/          d = this.work[i];
/*   0*/        } else {
/* 946*/          d *= this.work[i] / (d + this.work[i + 2]);
/*   0*/        } 
/*   0*/      } 
/* 951*/      d = this.work[this.pingPong];
/* 952*/      for (int j = 2 + this.pingPong; j < 4 * n - 2; j += 4) {
/* 953*/        int m = j - 2 * this.pingPong - 1;
/* 954*/        this.work[m] = d + this.work[j];
/* 955*/        if (this.work[j] <= 1.232595164407831E-28D * d) {
/* 956*/          this.work[j] = -0.0D;
/* 957*/          this.work[m] = d;
/* 958*/          this.work[m + 2] = 0.0D;
/* 959*/          d = this.work[j + 2];
/* 960*/        } else if (2.2250738585072014E-308D * this.work[j + 2] < this.work[m] && 2.2250738585072014E-308D * this.work[m] < this.work[j + 2]) {
/* 962*/          double tmp = this.work[j + 2] / this.work[m];
/* 963*/          this.work[m + 2] = this.work[j] * tmp;
/* 964*/          d *= tmp;
/*   0*/        } else {
/* 966*/          this.work[m + 2] = this.work[j + 2] * this.work[j] / this.work[m];
/* 967*/          d *= this.work[j + 2] / this.work[m];
/*   0*/        } 
/*   0*/      } 
/* 970*/      this.work[4 * n - 3 - this.pingPong] = d;
/* 973*/      this.pingPong = 1 - this.pingPong;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private int goodStep(int start, int end) {
/* 992*/    this.g = 0.0D;
/* 995*/    int deflatedEnd = end;
/* 996*/    for (boolean deflating = true; deflating; ) {
/* 998*/      if (start >= deflatedEnd)
/*1000*/        return deflatedEnd; 
/*1003*/      int k = 4 * deflatedEnd + this.pingPong - 1;
/*1005*/      if (start == deflatedEnd - 1 || (start != deflatedEnd - 2 && (this.work[k - 5] <= 1.232595164407831E-28D * (this.sigma + this.work[k - 3]) || this.work[k - 2 * this.pingPong - 4] <= 1.232595164407831E-28D * this.work[k - 7]))) {
/*1011*/        this.work[4 * deflatedEnd - 4] = this.sigma + this.work[4 * deflatedEnd - 4 + this.pingPong];
/*1012*/        deflatedEnd--;
/*   0*/        continue;
/*   0*/      } 
/*1014*/      if (start == deflatedEnd - 2 || this.work[k - 9] <= 1.232595164407831E-28D * this.sigma || this.work[k - 2 * this.pingPong - 8] <= 1.232595164407831E-28D * this.work[k - 11]) {
/*1019*/        if (this.work[k - 3] > this.work[k - 7]) {
/*1020*/          double tmp = this.work[k - 3];
/*1021*/          this.work[k - 3] = this.work[k - 7];
/*1022*/          this.work[k - 7] = tmp;
/*   0*/        } 
/*1025*/        if (this.work[k - 5] > 1.232595164407831E-28D * this.work[k - 3]) {
/*1026*/          double t = 0.5D * (this.work[k - 7] - this.work[k - 3] + this.work[k - 5]);
/*1027*/          double s = this.work[k - 3] * this.work[k - 5] / t;
/*1028*/          if (s <= t) {
/*1029*/            s = this.work[k - 3] * this.work[k - 5] / t * (1.0D + Math.sqrt(1.0D + s / t));
/*   0*/          } else {
/*1031*/            s = this.work[k - 3] * this.work[k - 5] / (t + Math.sqrt(t * (t + s)));
/*   0*/          } 
/*1033*/          t = this.work[k - 7] + s + this.work[k - 5];
/*1034*/          this.work[k - 3] = this.work[k - 3] * this.work[k - 7] / t;
/*1035*/          this.work[k - 7] = t;
/*   0*/        } 
/*1037*/        this.work[4 * deflatedEnd - 8] = this.sigma + this.work[k - 7];
/*1038*/        this.work[4 * deflatedEnd - 4] = this.sigma + this.work[k - 3];
/*1039*/        deflatedEnd -= 2;
/*   0*/        continue;
/*   0*/      } 
/*1043*/      deflating = false;
/*   0*/    } 
/*1049*/    int l = 4 * deflatedEnd + this.pingPong - 1;
/*1052*/    if ((this.dMin <= 0.0D || deflatedEnd < end) && 
/*1053*/      flipIfWarranted(deflatedEnd, 1)) {
/*1054*/      this.dMin2 = Math.min(this.dMin2, this.work[l - 1]);
/*1055*/      this.work[l - 1] = Math.min(this.work[l - 1], Math.min(this.work[3 + this.pingPong], this.work[7 + this.pingPong]));
/*1058*/      this.work[l - 2 * this.pingPong] = Math.min(this.work[l - 2 * this.pingPong], Math.min(this.work[6 + this.pingPong], this.work[6 + this.pingPong]));
/*1061*/      this.qMax = Math.max(this.qMax, Math.max(this.work[3 + this.pingPong], this.work[7 + this.pingPong]));
/*1062*/      this.dMin = -0.0D;
/*   0*/    } 
/*1066*/    if (this.dMin < 0.0D || 2.2250738585072014E-308D * this.qMax < Math.min(this.work[l - 1], Math.min(this.work[l - 9], this.dMin2 + this.work[l - 2 * this.pingPong]))) {
/*1071*/      computeShiftIncrement(start, deflatedEnd, end - deflatedEnd);
/*1074*/      for (boolean loop = true; loop; ) {
/*1077*/        dqds(start, deflatedEnd);
/*1080*/        if (this.dMin >= 0.0D && this.dMin1 > 0.0D) {
/*1082*/          updateSigma(this.tau);
/*1083*/          return deflatedEnd;
/*   0*/        } 
/*1084*/        if (this.dMin < 0.0D && this.dMin1 > 0.0D && this.work[4 * deflatedEnd - 5 - this.pingPong] < 1.1102230246251565E-14D * (this.sigma + this.dN1) && Math.abs(this.dN) < 1.1102230246251565E-14D * this.sigma) {
/*1089*/          this.work[4 * deflatedEnd - 3 - this.pingPong] = 0.0D;
/*1090*/          this.dMin = 0.0D;
/*1091*/          updateSigma(this.tau);
/*1092*/          return deflatedEnd;
/*   0*/        } 
/*1093*/        if (this.dMin < 0.0D) {
/*1095*/          if (this.tType < -22) {
/*1097*/            this.tau = 0.0D;
/*   0*/            continue;
/*   0*/          } 
/*1098*/          if (this.dMin1 > 0.0D) {
/*1100*/            this.tau = (this.tau + this.dMin) * 0.9999999999999998D;
/*1101*/            this.tType -= 11;
/*   0*/            continue;
/*   0*/          } 
/*1104*/          this.tau *= 0.25D;
/*1105*/          this.tType -= 12;
/*   0*/          continue;
/*   0*/        } 
/*1107*/        if (Double.isNaN(this.dMin)) {
/*1108*/          this.tau = 0.0D;
/*   0*/          continue;
/*   0*/        } 
/*1111*/        loop = false;
/*   0*/      } 
/*   0*/    } 
/*1118*/    dqd(start, deflatedEnd);
/*1120*/    return deflatedEnd;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean flipIfWarranted(int n, int step) {
/*1132*/    if (1.5D * this.work[this.pingPong] < this.work[4 * (n - 1) + this.pingPong]) {
/*1134*/      int j = 4 * n - 1;
/*1135*/      for (int i = 0; i < j; i += 4) {
/*1136*/        for (int k = 0; k < 4; k += step) {
/*1137*/          double tmp = this.work[i + k];
/*1138*/          this.work[i + k] = this.work[j - k];
/*1139*/          this.work[j - k] = tmp;
/*   0*/        } 
/*1141*/        j -= 4;
/*   0*/      } 
/*1143*/      return true;
/*   0*/    } 
/*1145*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private double[] eigenvaluesRange(int index, int n) {
/*1157*/    int lowerStart = 4 * this.main.length;
/*1158*/    int upperStart = 5 * this.main.length;
/*1159*/    double lower = Double.POSITIVE_INFINITY;
/*1160*/    double upper = Double.NEGATIVE_INFINITY;
/*1161*/    for (int i = 0; i < n; i++) {
/*1162*/      lower = Math.min(lower, this.work[lowerStart + index + i]);
/*1163*/      upper = Math.max(upper, this.work[upperStart + index + i]);
/*   0*/    } 
/*1167*/    double tNorm = Math.max(Math.abs(lower), Math.abs(upper));
/*1168*/    double relativeTolerance = Math.sqrt(1.1102230246251565E-16D);
/*1169*/    double absoluteTolerance = 4.0D * this.minPivot;
/*1170*/    int maxIter = 2 + (int)((Math.log(tNorm + this.minPivot) - Math.log(this.minPivot)) / Math.log(2.0D));
/*1172*/    double margin = 2.0D * (tNorm * 1.1102230246251565E-16D * n + 2.0D * this.minPivot);
/*1175*/    double left = lower - margin;
/*1176*/    double right = upper + margin;
/*1177*/    for (int k = 0; k < maxIter; k++) {
/*1179*/      double range = right - left;
/*1180*/      if (range < absoluteTolerance || range < relativeTolerance * Math.max(Math.abs(left), Math.abs(right)))
/*   0*/        break; 
/*1186*/      double middle = 0.5D * (left + right);
/*1187*/      if (countEigenValues(middle, index, n) >= 1) {
/*1188*/        right = middle;
/*   0*/      } else {
/*1190*/        left = middle;
/*   0*/      } 
/*   0*/    } 
/*1194*/    lower = Math.max(lower, left - 1.1102230246251565E-14D * Math.abs(left));
/*1197*/    left = lower - margin;
/*1198*/    right = upper + margin;
/*1199*/    for (int j = 0; j < maxIter; j++) {
/*1201*/      double range = right - left;
/*1202*/      if (range < absoluteTolerance || range < relativeTolerance * Math.max(Math.abs(left), Math.abs(right)))
/*   0*/        break; 
/*1208*/      double middle = 0.5D * (left + right);
/*1209*/      if (countEigenValues(middle, index, n) >= n) {
/*1210*/        right = middle;
/*   0*/      } else {
/*1212*/        left = middle;
/*   0*/      } 
/*   0*/    } 
/*1216*/    upper = Math.min(upper, right + 1.1102230246251565E-14D * Math.abs(right));
/*1218*/    return new double[] { lower, upper };
/*   0*/  }
/*   0*/  
/*   0*/  private int countEigenValues(double t, int index, int n) {
/*1230*/    double ratio = this.main[index] - t;
/*1231*/    int count = (ratio > 0.0D) ? 0 : 1;
/*1232*/    for (int i = 1; i < n; i++) {
/*1233*/      ratio = this.main[index + i] - this.squaredSecondary[index + i - 1] / ratio - t;
/*1234*/      if (ratio <= 0.0D)
/*1235*/        count++; 
/*   0*/    } 
/*1238*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private void ldlTDecomposition(double lambda, int index, int n) {
/*1253*/    double di = this.main[index] - lambda;
/*1254*/    this.work[0] = Math.abs(di);
/*1255*/    for (int i = 1; i < n; i++) {
/*1256*/      int fourI = 4 * i;
/*1257*/      double eiM1 = this.secondary[index + i - 1];
/*1258*/      double ratio = eiM1 / di;
/*1259*/      this.work[fourI - 2] = ratio * ratio * Math.abs(di);
/*1260*/      di = this.main[index + i] - lambda - eiM1 * ratio;
/*1261*/      this.work[fourI] = Math.abs(di);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void dqds(int start, int end) {
/*1273*/    this.eMin = this.work[4 * start + this.pingPong + 4];
/*1274*/    double d = this.work[4 * start + this.pingPong] - this.tau;
/*1275*/    this.dMin = d;
/*1276*/    this.dMin1 = -this.work[4 * start + this.pingPong];
/*1278*/    if (this.pingPong == 0) {
/*1279*/      for (int i = 4 * start + 3; i <= 4 * (end - 3); i += 4) {
/*1280*/        this.work[i - 2] = d + this.work[i - 1];
/*1281*/        double tmp = this.work[i + 1] / this.work[i - 2];
/*1282*/        d = d * tmp - this.tau;
/*1283*/        this.dMin = Math.min(this.dMin, d);
/*1284*/        this.work[i] = this.work[i - 1] * tmp;
/*1285*/        this.eMin = Math.min(this.work[i], this.eMin);
/*   0*/      } 
/*   0*/    } else {
/*1288*/      for (int i = 4 * start + 3; i <= 4 * (end - 3); i += 4) {
/*1289*/        this.work[i - 3] = d + this.work[i];
/*1290*/        double tmp = this.work[i + 2] / this.work[i - 3];
/*1291*/        d = d * tmp - this.tau;
/*1292*/        this.dMin = Math.min(this.dMin, d);
/*1293*/        this.work[i - 1] = this.work[i] * tmp;
/*1294*/        this.eMin = Math.min(this.work[i - 1], this.eMin);
/*   0*/      } 
/*   0*/    } 
/*1299*/    this.dN2 = d;
/*1300*/    this.dMin2 = this.dMin;
/*1301*/    int j4 = 4 * (end - 2) - this.pingPong - 1;
/*1302*/    int j4p2 = j4 + 2 * this.pingPong - 1;
/*1303*/    this.work[j4 - 2] = this.dN2 + this.work[j4p2];
/*1304*/    this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1305*/    this.dN1 = this.work[j4p2 + 2] * this.dN2 / this.work[j4 - 2] - this.tau;
/*1306*/    this.dMin = Math.min(this.dMin, this.dN1);
/*1308*/    this.dMin1 = this.dMin;
/*1309*/    j4 += 4;
/*1310*/    j4p2 = j4 + 2 * this.pingPong - 1;
/*1311*/    this.work[j4 - 2] = this.dN1 + this.work[j4p2];
/*1312*/    this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1313*/    this.dN = this.work[j4p2 + 2] * this.dN1 / this.work[j4 - 2] - this.tau;
/*1314*/    this.dMin = this.dN;
/*1316*/    this.work[j4 + 2] = this.dN;
/*1317*/    this.work[4 * end - this.pingPong - 1] = this.eMin;
/*   0*/  }
/*   0*/  
/*   0*/  private void dqd(int start, int end) {
/*1330*/    this.eMin = this.work[4 * start + this.pingPong + 4];
/*1331*/    double d = this.work[4 * start + this.pingPong];
/*1332*/    this.dMin = d;
/*1334*/    if (this.pingPong == 0) {
/*1335*/      for (int i = 4 * start + 3; i < 4 * (end - 3); i += 4) {
/*1336*/        this.work[i - 2] = d + this.work[i - 1];
/*1337*/        if (this.work[i - 2] == 0.0D) {
/*1338*/          this.work[i] = 0.0D;
/*1339*/          d = this.work[i + 1];
/*1340*/          this.dMin = d;
/*1341*/          this.eMin = 0.0D;
/*1342*/        } else if (2.2250738585072014E-308D * this.work[i + 1] < this.work[i - 2] && 2.2250738585072014E-308D * this.work[i - 2] < this.work[i + 1]) {
/*1344*/          double tmp = this.work[i + 1] / this.work[i - 2];
/*1345*/          this.work[i] = this.work[i - 1] * tmp;
/*1346*/          d *= tmp;
/*   0*/        } else {
/*1348*/          this.work[i] = this.work[i + 1] * this.work[i - 1] / this.work[i - 2];
/*1349*/          d *= this.work[i + 1] / this.work[i - 2];
/*   0*/        } 
/*1351*/        this.dMin = Math.min(this.dMin, d);
/*1352*/        this.eMin = Math.min(this.eMin, this.work[i]);
/*   0*/      } 
/*   0*/    } else {
/*1355*/      for (int i = 4 * start + 3; i < 4 * (end - 3); i += 4) {
/*1356*/        this.work[i - 3] = d + this.work[i];
/*1357*/        if (this.work[i - 3] == 0.0D) {
/*1358*/          this.work[i - 1] = 0.0D;
/*1359*/          d = this.work[i + 2];
/*1360*/          this.dMin = d;
/*1361*/          this.eMin = 0.0D;
/*1362*/        } else if (2.2250738585072014E-308D * this.work[i + 2] < this.work[i - 3] && 2.2250738585072014E-308D * this.work[i - 3] < this.work[i + 2]) {
/*1364*/          double tmp = this.work[i + 2] / this.work[i - 3];
/*1365*/          this.work[i - 1] = this.work[i] * tmp;
/*1366*/          d *= tmp;
/*   0*/        } else {
/*1368*/          this.work[i - 1] = this.work[i + 2] * this.work[i] / this.work[i - 3];
/*1369*/          d *= this.work[i + 2] / this.work[i - 3];
/*   0*/        } 
/*1371*/        this.dMin = Math.min(this.dMin, d);
/*1372*/        this.eMin = Math.min(this.eMin, this.work[i - 1]);
/*   0*/      } 
/*   0*/    } 
/*1377*/    this.dN2 = d;
/*1378*/    this.dMin2 = this.dMin;
/*1379*/    int j4 = 4 * (end - 2) - this.pingPong - 1;
/*1380*/    int j4p2 = j4 + 2 * this.pingPong - 1;
/*1381*/    this.work[j4 - 2] = this.dN2 + this.work[j4p2];
/*1382*/    if (this.work[j4 - 2] == 0.0D) {
/*1383*/      this.work[j4] = 0.0D;
/*1384*/      this.dN1 = this.work[j4p2 + 2];
/*1385*/      this.dMin = this.dN1;
/*1386*/      this.eMin = 0.0D;
/*1387*/    } else if (2.2250738585072014E-308D * this.work[j4p2 + 2] < this.work[j4 - 2] && 2.2250738585072014E-308D * this.work[j4 - 2] < this.work[j4p2 + 2]) {
/*1389*/      double tmp = this.work[j4p2 + 2] / this.work[j4 - 2];
/*1390*/      this.work[j4] = this.work[j4p2] * tmp;
/*1391*/      this.dN1 = this.dN2 * tmp;
/*   0*/    } else {
/*1393*/      this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1394*/      this.dN1 = this.work[j4p2 + 2] * this.dN2 / this.work[j4 - 2];
/*   0*/    } 
/*1396*/    this.dMin = Math.min(this.dMin, this.dN1);
/*1398*/    this.dMin1 = this.dMin;
/*1399*/    j4 += 4;
/*1400*/    j4p2 = j4 + 2 * this.pingPong - 1;
/*1401*/    this.work[j4 - 2] = this.dN1 + this.work[j4p2];
/*1402*/    if (this.work[j4 - 2] == 0.0D) {
/*1403*/      this.work[j4] = 0.0D;
/*1404*/      this.dN = this.work[j4p2 + 2];
/*1405*/      this.dMin = this.dN;
/*1406*/      this.eMin = 0.0D;
/*1407*/    } else if (2.2250738585072014E-308D * this.work[j4p2 + 2] < this.work[j4 - 2] && 2.2250738585072014E-308D * this.work[j4 - 2] < this.work[j4p2 + 2]) {
/*1409*/      double tmp = this.work[j4p2 + 2] / this.work[j4 - 2];
/*1410*/      this.work[j4] = this.work[j4p2] * tmp;
/*1411*/      this.dN = this.dN1 * tmp;
/*   0*/    } else {
/*1413*/      this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1414*/      this.dN = this.work[j4p2 + 2] * this.dN1 / this.work[j4 - 2];
/*   0*/    } 
/*1416*/    this.dMin = Math.min(this.dMin, this.dN);
/*1418*/    this.work[j4 + 2] = this.dN;
/*1419*/    this.work[4 * end - this.pingPong - 1] = this.eMin;
/*   0*/  }
/*   0*/  
/*   0*/  private void computeShiftIncrement(int start, int end, int deflated) {
/*1432*/    double cnst1 = 0.563D;
/*1433*/    double cnst2 = 1.01D;
/*1434*/    double cnst3 = 1.05D;
/*1438*/    if (this.dMin <= 0.0D) {
/*1439*/      this.tau = -this.dMin;
/*1440*/      this.tType = -1;
/*   0*/      return;
/*   0*/    } 
/*1444*/    int nn = 4 * end + this.pingPong - 1;
/*1445*/    switch (deflated) {
/*   0*/      case 0:
/*1448*/        if (this.dMin == this.dN || this.dMin == this.dN1) {
/*   0*/          double gam;
/*   0*/          int np;
/*1450*/          double b1 = Math.sqrt(this.work[nn - 3]) * Math.sqrt(this.work[nn - 5]);
/*1451*/          double b2 = Math.sqrt(this.work[nn - 7]) * Math.sqrt(this.work[nn - 9]);
/*1452*/          double a2 = this.work[nn - 7] + this.work[nn - 5];
/*1454*/          if (this.dMin == this.dN && this.dMin1 == this.dN1) {
/*1456*/            double gap2 = this.dMin2 - a2 - this.dMin2 * 0.25D;
/*1457*/            double gap1 = a2 - this.dN - ((gap2 > 0.0D && gap2 > b2) ? (b2 / gap2 * b2) : (b1 + b2));
/*1458*/            if (gap1 > 0.0D && gap1 > b1) {
/*1459*/              this.tau = Math.max(this.dN - b1 / gap1 * b1, 0.5D * this.dMin);
/*1460*/              this.tType = -2;
/*   0*/              break;
/*   0*/            } 
/*1462*/            double d1 = 0.0D;
/*1463*/            if (this.dN > b1)
/*1464*/              d1 = this.dN - b1; 
/*1466*/            if (a2 > b1 + b2)
/*1467*/              d1 = Math.min(d1, a2 - b1 + b2); 
/*1469*/            this.tau = Math.max(d1, 0.333D * this.dMin);
/*1470*/            this.tType = -3;
/*   0*/            break;
/*   0*/          } 
/*1474*/          this.tType = -4;
/*1475*/          double s = 0.25D * this.dMin;
/*1478*/          if (this.dMin == this.dN) {
/*1479*/            gam = this.dN;
/*1480*/            a2 = 0.0D;
/*1481*/            if (this.work[nn - 5] > this.work[nn - 7])
/*   0*/              return; 
/*1484*/            b2 = this.work[nn - 5] / this.work[nn - 7];
/*1485*/            np = nn - 9;
/*   0*/          } else {
/*1487*/            np = nn - 2 * this.pingPong;
/*1488*/            b2 = this.work[np - 2];
/*1489*/            gam = this.dN1;
/*1490*/            if (this.work[np - 4] > this.work[np - 2])
/*   0*/              return; 
/*1493*/            a2 = this.work[np - 4] / this.work[np - 2];
/*1494*/            if (this.work[nn - 9] > this.work[nn - 11])
/*   0*/              return; 
/*1497*/            b2 = this.work[nn - 9] / this.work[nn - 11];
/*1498*/            np = nn - 13;
/*   0*/          } 
/*1502*/          a2 += b2;
/*1503*/          for (int i4 = np; i4 >= 4 * start + 2 + this.pingPong && 
/*1504*/            b2 != 0.0D; i4 -= 4) {
/*1507*/            b1 = b2;
/*1508*/            if (this.work[i4] > this.work[i4 - 2])
/*   0*/              return; 
/*1511*/            b2 *= this.work[i4] / this.work[i4 - 2];
/*1512*/            a2 += b2;
/*1513*/            if (100.0D * Math.max(b2, b1) < a2 || 0.563D < a2)
/*   0*/              break; 
/*   0*/          } 
/*1517*/          a2 = 1.05D * a2;
/*1520*/          if (a2 < 0.563D)
/*1521*/            s = gam * (1.0D - Math.sqrt(a2)) / (1.0D + a2); 
/*1523*/          this.tau = s;
/*   0*/          break;
/*   0*/        } 
/*1526*/        if (this.dMin == this.dN2) {
/*1529*/          this.tType = -5;
/*1530*/          double s = 0.25D * this.dMin;
/*1533*/          int np = nn - 2 * this.pingPong;
/*1534*/          double b1 = this.work[np - 2];
/*1535*/          double b2 = this.work[np - 6];
/*1536*/          double gam = this.dN2;
/*1537*/          if (this.work[np - 8] > b2 || this.work[np - 4] > b1)
/*   0*/            return; 
/*1540*/          double a2 = this.work[np - 8] / b2 * (1.0D + this.work[np - 4] / b1);
/*1543*/          if (end - start > 2) {
/*1544*/            b2 = this.work[nn - 13] / this.work[nn - 15];
/*1545*/            a2 += b2;
/*1546*/            for (int i4 = nn - 17; i4 >= 4 * start + 2 + this.pingPong && 
/*1547*/              b2 != 0.0D; i4 -= 4) {
/*1550*/              b1 = b2;
/*1551*/              if (this.work[i4] > this.work[i4 - 2])
/*   0*/                return; 
/*1554*/              b2 *= this.work[i4] / this.work[i4 - 2];
/*1555*/              a2 += b2;
/*1556*/              if (100.0D * Math.max(b2, b1) < a2 || 0.563D < a2)
/*   0*/                break; 
/*   0*/            } 
/*1560*/            a2 = 1.05D * a2;
/*   0*/          } 
/*1563*/          if (a2 < 0.563D) {
/*1564*/            this.tau = gam * (1.0D - Math.sqrt(a2)) / (1.0D + a2);
/*   0*/            break;
/*   0*/          } 
/*1566*/          this.tau = s;
/*   0*/          break;
/*   0*/        } 
/*1572*/        if (this.tType == -6) {
/*1573*/          this.g += 0.333D * (1.0D - this.g);
/*1574*/        } else if (this.tType == -18) {
/*1575*/          this.g = 0.08325D;
/*   0*/        } else {
/*1577*/          this.g = 0.25D;
/*   0*/        } 
/*1579*/        this.tau = this.g * this.dMin;
/*1580*/        this.tType = -6;
/*   0*/        break;
/*   0*/      case 1:
/*1586*/        if (this.dMin1 == this.dN1 && this.dMin2 == this.dN2) {
/*1589*/          this.tType = -7;
/*1590*/          double s = 0.333D * this.dMin1;
/*1591*/          if (this.work[nn - 5] > this.work[nn - 7])
/*   0*/            return; 
/*1594*/          double b1 = this.work[nn - 5] / this.work[nn - 7];
/*1595*/          double b2 = b1;
/*1596*/          if (b2 != 0.0D)
/*1597*/            for (int i4 = 4 * end - 10 + this.pingPong; i4 >= 4 * start + 2 + this.pingPong; i4 -= 4) {
/*1598*/              double oldB1 = b1;
/*1599*/              if (this.work[i4] > this.work[i4 - 2])
/*   0*/                return; 
/*1602*/              b1 *= this.work[i4] / this.work[i4 - 2];
/*1603*/              b2 += b1;
/*1604*/              if (100.0D * Math.max(b1, oldB1) < b2)
/*   0*/                break; 
/*   0*/            }  
/*1609*/          b2 = Math.sqrt(1.05D * b2);
/*1610*/          double a2 = this.dMin1 / (1.0D + b2 * b2);
/*1611*/          double gap2 = 0.5D * this.dMin2 - a2;
/*1612*/          if (gap2 > 0.0D && gap2 > b2 * a2) {
/*1613*/            this.tau = Math.max(s, a2 * (1.0D - 1.01D * a2 * b2 / gap2 * b2));
/*   0*/            break;
/*   0*/          } 
/*1615*/          this.tau = Math.max(s, a2 * (1.0D - 1.01D * b2));
/*1616*/          this.tType = -8;
/*   0*/          break;
/*   0*/        } 
/*1621*/        this.tau = 0.25D * this.dMin1;
/*1622*/        if (this.dMin1 == this.dN1)
/*1623*/          this.tau = 0.5D * this.dMin1; 
/*1625*/        this.tType = -9;
/*   0*/        break;
/*   0*/      case 2:
/*1632*/        if (this.dMin2 == this.dN2 && 2.0D * this.work[nn - 5] < this.work[nn - 7]) {
/*1633*/          this.tType = -10;
/*1634*/          double s = 0.333D * this.dMin2;
/*1635*/          if (this.work[nn - 5] > this.work[nn - 7])
/*   0*/            return; 
/*1638*/          double b1 = this.work[nn - 5] / this.work[nn - 7];
/*1639*/          double b2 = b1;
/*1640*/          if (b2 != 0.0D)
/*1641*/            for (int i4 = 4 * end - 9 + this.pingPong; i4 >= 4 * start + 2 + this.pingPong; i4 -= 4) {
/*1642*/              if (this.work[i4] > this.work[i4 - 2])
/*   0*/                return; 
/*1645*/              b1 *= this.work[i4] / this.work[i4 - 2];
/*1646*/              b2 += b1;
/*1647*/              if (100.0D * b1 < b2)
/*   0*/                break; 
/*   0*/            }  
/*1652*/          b2 = Math.sqrt(1.05D * b2);
/*1653*/          double a2 = this.dMin2 / (1.0D + b2 * b2);
/*1654*/          double gap2 = this.work[nn - 7] + this.work[nn - 9] - Math.sqrt(this.work[nn - 11]) * Math.sqrt(this.work[nn - 9]) - a2;
/*1656*/          if (gap2 > 0.0D && gap2 > b2 * a2) {
/*1657*/            this.tau = Math.max(s, a2 * (1.0D - 1.01D * a2 * b2 / gap2 * b2));
/*   0*/            break;
/*   0*/          } 
/*1659*/          this.tau = Math.max(s, a2 * (1.0D - 1.01D * b2));
/*   0*/          break;
/*   0*/        } 
/*1662*/        this.tau = 0.25D * this.dMin2;
/*1663*/        this.tType = -11;
/*   0*/        break;
/*   0*/      default:
/*1668*/        this.tau = 0.0D;
/*1669*/        this.tType = -12;
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateSigma(double shift) {
/*1682*/    if (shift < this.sigma) {
/*1683*/      this.sigmaLow += shift;
/*1684*/      double t = this.sigma + this.sigmaLow;
/*1685*/      this.sigmaLow -= t - this.sigma;
/*1686*/      this.sigma = t;
/*   0*/    } else {
/*1688*/      double t = this.sigma + shift;
/*1689*/      this.sigmaLow += this.sigma - t - shift;
/*1690*/      this.sigma = t;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void findEigenVectors() {
/*1699*/    int m = this.main.length;
/*1700*/    this.eigenvectors = new ArrayRealVector[m];
/*1703*/    double[] d = new double[m];
/*1704*/    double[] l = new double[m - 1];
/*1706*/    double mu = (this.realEigenvalues[m - 1] <= 0.0D && this.realEigenvalues[0] > 0.0D) ? (0.5D - this.realEigenvalues[m - 1]) : 0.0D;
/*1707*/    double di = this.main[0] + mu;
/*1708*/    d[0] = di;
/*1709*/    for (int i = 1; i < m; i++) {
/*1710*/      double eiM1 = this.secondary[i - 1];
/*1711*/      double ratio = eiM1 / di;
/*1712*/      di = this.main[i] - eiM1 * ratio + mu;
/*1713*/      l[i - 1] = ratio;
/*1714*/      d[i] = di;
/*   0*/    } 
/*1718*/    for (int j = 0; j < m; j++)
/*1719*/      this.eigenvectors[j] = findEigenvector(this.realEigenvalues[j] + mu, d, l); 
/*   0*/  }
/*   0*/  
/*   0*/  private ArrayRealVector findEigenvector(double eigenvalue, double[] d, double[] l) {
/*1738*/    int m = this.main.length;
/*1739*/    stationaryQuotientDifferenceWithShift(d, l, eigenvalue);
/*1740*/    progressiveQuotientDifferenceWithShift(d, l, eigenvalue);
/*1744*/    int r = m - 1;
/*1745*/    double minG = Math.abs(this.work[6 * r] + this.work[6 * r + 3] + eigenvalue);
/*1746*/    int sixI = 0;
/*1747*/    for (int i = 0; i < m - 1; i++) {
/*1748*/      double absG = Math.abs(this.work[sixI] + d[i] * this.work[sixI + 9] / this.work[sixI + 10]);
/*1749*/      if (absG < minG) {
/*1750*/        r = i;
/*1751*/        minG = absG;
/*   0*/      } 
/*1753*/      sixI += 6;
/*   0*/    } 
/*1758*/    double[] eigenvector = new double[m];
/*1759*/    double n2 = 1.0D;
/*1760*/    eigenvector[r] = 1.0D;
/*1761*/    double z = 1.0D;
/*1762*/    for (int k = r - 1; k >= 0; k--) {
/*1763*/      z *= -this.work[6 * k + 2];
/*1764*/      eigenvector[k] = z;
/*1765*/      n2 += z * z;
/*   0*/    } 
/*1767*/    z = 1.0D;
/*1768*/    for (int j = r + 1; j < m; j++) {
/*1769*/      z *= -this.work[6 * j - 1];
/*1770*/      eigenvector[j] = z;
/*1771*/      n2 += z * z;
/*   0*/    } 
/*1775*/    double inv = 1.0D / Math.sqrt(n2);
/*1776*/    for (int n = 0; n < m; n++)
/*1777*/      eigenvector[n] = eigenvector[n] * inv; 
/*1780*/    return (this.transformer == null) ? new ArrayRealVector(eigenvector, false) : new ArrayRealVector(this.transformer.getQ().operate(eigenvector), false);
/*   0*/  }
/*   0*/  
/*   0*/  private void stationaryQuotientDifferenceWithShift(double[] d, double[] l, double lambda) {
/*1796*/    int nM1 = d.length - 1;
/*1797*/    double si = -lambda;
/*1798*/    int sixI = 0;
/*1799*/    for (int i = 0; i < nM1; i++) {
/*1800*/      double di = d[i];
/*1801*/      double li = l[i];
/*1802*/      double diP1 = di + si;
/*1803*/      double liP1 = li * di / diP1;
/*1804*/      this.work[sixI] = si;
/*1805*/      this.work[sixI + 1] = diP1;
/*1806*/      this.work[sixI + 2] = liP1;
/*1807*/      si = li * liP1 * si - lambda;
/*1808*/      sixI += 6;
/*   0*/    } 
/*1810*/    this.work[6 * nM1 + 1] = d[nM1] + si;
/*1811*/    this.work[6 * nM1] = si;
/*   0*/  }
/*   0*/  
/*   0*/  private void progressiveQuotientDifferenceWithShift(double[] d, double[] l, double lambda) {
/*1824*/    int nM1 = d.length - 1;
/*1825*/    double pi = d[nM1] - lambda;
/*1826*/    int sixI = 6 * (nM1 - 1);
/*1827*/    for (int i = nM1 - 1; i >= 0; i--) {
/*1828*/      double di = d[i];
/*1829*/      double li = l[i];
/*1830*/      double diP1 = di * li * li + pi;
/*1831*/      double t = di / diP1;
/*1832*/      this.work[sixI + 9] = pi;
/*1833*/      this.work[sixI + 10] = diP1;
/*1834*/      this.work[sixI + 5] = li * t;
/*1835*/      pi = pi * t - lambda;
/*1836*/      sixI -= 6;
/*   0*/    } 
/*1838*/    this.work[3] = pi;
/*1839*/    this.work[4] = pi;
/*   0*/  }
/*   0*/}
