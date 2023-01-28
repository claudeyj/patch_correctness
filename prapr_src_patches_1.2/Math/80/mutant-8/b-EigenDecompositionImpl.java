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
/* 603*/    this.upperSpectra = Math.max(this.upperSpectra, upper);
/* 604*/    this.minPivot = 2.2250738585072014E-308D * Math.max(1.0D, eMax * eMax);
/*   0*/  }
/*   0*/  
/*   0*/  private void findEigenvalues() throws InvalidMatrixException {
/* 616*/    List<Integer> splitIndices = computeSplits();
/* 619*/    this.realEigenvalues = new double[this.main.length];
/* 620*/    this.imagEigenvalues = new double[this.main.length];
/* 621*/    int begin = 0;
/* 622*/    for (Iterator<Integer> i$ = splitIndices.iterator(); i$.hasNext(); ) {
/*   0*/      double range[], oneFourth;
/*   0*/      int oneFourthCount;
/*   0*/      double threeFourth;
/*   0*/      int threeFourthCount;
/*   0*/      boolean chooseLeft;
/*   0*/      double lambda;
/*   0*/      int k;
/* 622*/      int end = (Integer)i$.next();
/* 623*/      int n = end - begin;
/* 624*/      switch (n) {
/*   0*/        case 1:
/* 628*/          process1RowBlock(begin);
/*   0*/          break;
/*   0*/        case 2:
/* 633*/          process2RowsBlock(begin);
/*   0*/          break;
/*   0*/        case 3:
/* 638*/          process3RowsBlock(begin);
/*   0*/          break;
/*   0*/        default:
/* 644*/          range = eigenvaluesRange(begin, n);
/* 645*/          oneFourth = 0.25D * (3.0D * range[0] + range[1]);
/* 646*/          oneFourthCount = countEigenValues(oneFourth, begin, n);
/* 647*/          threeFourth = 0.25D * (range[0] + 3.0D * range[1]);
/* 648*/          threeFourthCount = countEigenValues(threeFourth, begin, n);
/* 649*/          chooseLeft = (oneFourthCount - 0 >= n - threeFourthCount);
/* 650*/          lambda = chooseLeft ? range[0] : range[1];
/* 652*/          this.tau = (range[1] - range[0]) * 1.1102230246251565E-16D * n + 2.0D * this.minPivot;
/* 655*/          ldlTDecomposition(lambda, begin, n);
/* 658*/          processGeneralBlock(n);
/* 661*/          if (chooseLeft) {
/* 662*/            for (int m = 0; m < n; m++)
/* 663*/              this.realEigenvalues[begin + m] = lambda + this.work[4 * m]; 
/*   0*/            break;
/*   0*/          } 
/* 666*/          for (k = 0; k < n; k++)
/* 667*/            this.realEigenvalues[begin + k] = lambda - this.work[4 * k]; 
/*   0*/          break;
/*   0*/      } 
/* 672*/      begin = end;
/*   0*/    } 
/* 676*/    Arrays.sort(this.realEigenvalues);
/* 677*/    int j = this.realEigenvalues.length - 1;
/* 678*/    for (int i = 0; i < j; i++) {
/* 679*/      double tmp = this.realEigenvalues[i];
/* 680*/      this.realEigenvalues[i] = this.realEigenvalues[j];
/* 681*/      this.realEigenvalues[j] = tmp;
/* 682*/      j--;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private List<Integer> computeSplits() {
/* 693*/    List<Integer> list = new ArrayList<Integer>();
/* 696*/    double absDCurrent = Math.abs(this.main[0]);
/* 697*/    for (int i = 0; i < this.secondary.length; i++) {
/* 698*/      double absDPrevious = absDCurrent;
/* 699*/      absDCurrent = Math.abs(this.main[i + 1]);
/* 700*/      double max = this.splitTolerance * Math.sqrt(absDPrevious * absDCurrent);
/* 701*/      if (Math.abs(this.secondary[i]) <= max) {
/* 702*/        list.add(i + 1);
/* 703*/        this.secondary[i] = 0.0D;
/* 704*/        this.squaredSecondary[i] = 0.0D;
/*   0*/      } 
/*   0*/    } 
/* 708*/    list.add(this.secondary.length + 1);
/* 709*/    return list;
/*   0*/  }
/*   0*/  
/*   0*/  private void process1RowBlock(int index) {
/* 719*/    this.realEigenvalues[index] = this.main[index];
/*   0*/  }
/*   0*/  
/*   0*/  private void process2RowsBlock(int index) throws InvalidMatrixException {
/* 733*/    double q0 = this.main[index];
/* 734*/    double q1 = this.main[index + 1];
/* 735*/    double e12 = this.squaredSecondary[index];
/* 737*/    double s = q0 + q1;
/* 738*/    double p = q0 * q1 - e12;
/* 739*/    double delta = s * s - 4.0D * p;
/* 740*/    if (delta < 0.0D)
/* 741*/      throw new InvalidMatrixException("cannot solve degree {0} equation", new Object[] { 2 }); 
/* 744*/    double largestRoot = 0.5D * (s + Math.sqrt(delta));
/* 745*/    this.realEigenvalues[index] = largestRoot;
/* 746*/    this.realEigenvalues[index + 1] = p / largestRoot;
/*   0*/  }
/*   0*/  
/*   0*/  private void process3RowsBlock(int index) throws InvalidMatrixException {
/* 761*/    double q0 = this.main[index];
/* 762*/    double q1 = this.main[index + 1];
/* 763*/    double q2 = this.main[index + 2];
/* 764*/    double e12 = this.squaredSecondary[index];
/* 765*/    double q1q2Me22 = q1 * q2 - this.squaredSecondary[index + 1];
/* 768*/    double b = -(q0 + q1 + q2);
/* 769*/    double c = q0 * q1 + q0 * q2 + q1q2Me22 - e12;
/* 770*/    double d = q2 * e12 - q0 * q1q2Me22;
/* 773*/    double b2 = b * b;
/* 774*/    double q = (3.0D * c - b2) / 9.0D;
/* 775*/    double r = ((9.0D * c - 2.0D * b2) * b - 27.0D * d) / 54.0D;
/* 776*/    double delta = q * q * q + r * r;
/* 777*/    if (delta >= 0.0D)
/* 781*/      throw new InvalidMatrixException("cannot solve degree {0} equation", new Object[] { 3 }); 
/* 783*/    double sqrtMq = Math.sqrt(-q);
/* 784*/    double theta = Math.acos(r / -q * sqrtMq);
/* 785*/    double alpha = 2.0D * sqrtMq;
/* 786*/    double beta = b / 3.0D;
/* 788*/    double z0 = alpha * Math.cos(theta / 3.0D) - beta;
/* 789*/    double z1 = alpha * Math.cos((theta + 6.283185307179586D) / 3.0D) - beta;
/* 790*/    double z2 = alpha * Math.cos((theta + 12.566370614359172D) / 3.0D) - beta;
/* 791*/    if (z0 < z1) {
/* 792*/      double t = z0;
/* 793*/      z0 = z1;
/* 794*/      z1 = t;
/*   0*/    } 
/* 796*/    if (z1 < z2) {
/* 797*/      double t = z1;
/* 798*/      z1 = z2;
/* 799*/      z2 = t;
/*   0*/    } 
/* 801*/    if (z0 < z1) {
/* 802*/      double t = z0;
/* 803*/      z0 = z1;
/* 804*/      z1 = t;
/*   0*/    } 
/* 806*/    this.realEigenvalues[index] = z0;
/* 807*/    this.realEigenvalues[index + 1] = z1;
/* 808*/    this.realEigenvalues[index + 2] = z2;
/*   0*/  }
/*   0*/  
/*   0*/  private void processGeneralBlock(int n) throws InvalidMatrixException {
/* 827*/    double sumOffDiag = 0.0D;
/* 828*/    for (int i = 0; i < n - 1; i++) {
/* 829*/      int fourI = 4 * i;
/* 830*/      double ei = this.work[fourI + 2];
/* 831*/      sumOffDiag += ei;
/*   0*/    } 
/* 834*/    if (sumOffDiag == 0.0D)
/*   0*/      return; 
/* 840*/    flipIfWarranted(n, 2);
/* 843*/    initialSplits(n);
/* 846*/    this.tType = 0;
/* 847*/    this.dMin1 = 0.0D;
/* 848*/    this.dMin2 = 0.0D;
/* 849*/    this.dN = 0.0D;
/* 850*/    this.dN1 = 0.0D;
/* 851*/    this.dN2 = 0.0D;
/* 852*/    this.tau = 0.0D;
/* 855*/    int i0 = 0;
/* 856*/    int n0 = n;
/* 857*/    while (n0 > 0) {
/* 860*/      this.sigma = (n0 == n) ? 0.0D : -this.work[4 * n0 - 2];
/* 861*/      this.sigmaLow = 0.0D;
/* 864*/      double offDiagMin = (i0 == n0) ? 0.0D : this.work[4 * n0 - 6];
/* 865*/      double offDiagMax = 0.0D;
/* 866*/      double diagMax = this.work[4 * n0 - 4];
/* 867*/      double diagMin = diagMax;
/* 868*/      i0 = 0;
/* 869*/      for (int j = 4 * (n0 - 2); j >= 0; j -= 4) {
/* 870*/        if (this.work[j + 2] <= 0.0D) {
/* 871*/          i0 = 1 + j / 4;
/*   0*/          break;
/*   0*/        } 
/* 874*/        if (diagMin >= 4.0D * offDiagMax) {
/* 875*/          diagMin = Math.min(diagMin, this.work[j + 4]);
/* 876*/          offDiagMax = Math.max(offDiagMax, this.work[j + 2]);
/*   0*/        } 
/* 878*/        diagMax = Math.max(diagMax, this.work[j] + this.work[j + 2]);
/* 879*/        offDiagMin = Math.min(offDiagMin, this.work[j + 2]);
/*   0*/      } 
/* 881*/      this.work[4 * n0 - 2] = offDiagMin;
/* 884*/      this.dMin = -Math.max(0.0D, diagMin - 2.0D * Math.sqrt(diagMin * offDiagMax));
/* 886*/      this.pingPong = 0;
/* 887*/      int maxIter = 30 * (n0 - i0);
/* 888*/      for (int k = 0; i0 < n0; k++) {
/* 889*/        if (k >= maxIter)
/* 890*/          throw new InvalidMatrixException(new MaxIterationsExceededException(maxIter)); 
/* 894*/        n0 = goodStep(i0, n0);
/* 895*/        this.pingPong = 1 - this.pingPong;
/* 899*/        if (this.pingPong == 0 && n0 - i0 > 3 && this.work[4 * n0 - 1] <= 1.232595164407831E-28D * diagMax && this.work[4 * n0 - 2] <= 1.232595164407831E-28D * this.sigma) {
/* 902*/          int split = i0 - 1;
/* 903*/          diagMax = this.work[4 * i0];
/* 904*/          offDiagMin = this.work[4 * i0 + 2];
/* 905*/          double previousEMin = this.work[4 * i0 + 3];
/* 906*/          for (int m = 4 * i0; m < 4 * n0 - 16; m += 4) {
/* 907*/            if (this.work[m + 3] <= 1.232595164407831E-28D * this.work[m] || this.work[m + 2] <= 1.232595164407831E-28D * this.sigma) {
/* 910*/              this.work[m + 2] = -this.sigma;
/* 911*/              split = m / 4;
/* 912*/              diagMax = 0.0D;
/* 913*/              offDiagMin = this.work[m + 6];
/* 914*/              previousEMin = this.work[m + 7];
/*   0*/            } else {
/* 916*/              diagMax = Math.max(diagMax, this.work[m + 4]);
/* 917*/              offDiagMin = Math.min(offDiagMin, this.work[m + 2]);
/* 918*/              previousEMin = Math.min(previousEMin, this.work[m + 3]);
/*   0*/            } 
/*   0*/          } 
/* 921*/          this.work[4 * n0 - 2] = offDiagMin;
/* 922*/          this.work[4 * n0 - 1] = previousEMin;
/* 923*/          i0 = split + 1;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void initialSplits(int n) {
/* 937*/    this.pingPong = 0;
/* 938*/    for (int k = 0; k < 2; k++) {
/* 941*/      double d = this.work[4 * (n - 1) + this.pingPong];
/* 942*/      for (int i = 4 * (n - 2) + this.pingPong; i >= 0; i -= 4) {
/* 943*/        if (this.work[i + 2] <= 1.232595164407831E-28D * d) {
/* 944*/          this.work[i + 2] = 0.0D;
/* 945*/          d = this.work[i];
/*   0*/        } else {
/* 947*/          d *= this.work[i] / (d + this.work[i + 2]);
/*   0*/        } 
/*   0*/      } 
/* 952*/      d = this.work[this.pingPong];
/* 953*/      for (int j = 2 + this.pingPong; j < 4 * n - 2; j += 4) {
/* 954*/        int m = j - 2 * this.pingPong - 1;
/* 955*/        this.work[m] = d + this.work[j];
/* 956*/        if (this.work[j] <= 1.232595164407831E-28D * d) {
/* 957*/          this.work[j] = 0.0D;
/* 958*/          this.work[m] = d;
/* 959*/          this.work[m + 2] = 0.0D;
/* 960*/          d = this.work[j + 2];
/* 961*/        } else if (2.2250738585072014E-308D * this.work[j + 2] < this.work[m] && 2.2250738585072014E-308D * this.work[m] < this.work[j + 2]) {
/* 963*/          double tmp = this.work[j + 2] / this.work[m];
/* 964*/          this.work[m + 2] = this.work[j] * tmp;
/* 965*/          d *= tmp;
/*   0*/        } else {
/* 967*/          this.work[m + 2] = this.work[j + 2] * this.work[j] / this.work[m];
/* 968*/          d *= this.work[j + 2] / this.work[m];
/*   0*/        } 
/*   0*/      } 
/* 971*/      this.work[4 * n - 3 - this.pingPong] = d;
/* 974*/      this.pingPong = 1 - this.pingPong;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private int goodStep(int start, int end) {
/* 993*/    this.g = 0.0D;
/* 996*/    int deflatedEnd = end;
/* 997*/    for (boolean deflating = true; deflating; ) {
/* 999*/      if (start >= deflatedEnd)
/*1001*/        return deflatedEnd; 
/*1004*/      int k = 4 * deflatedEnd + this.pingPong - 1;
/*1006*/      if (start == deflatedEnd - 1 || (start != deflatedEnd - 2 && (this.work[k - 5] <= 1.232595164407831E-28D * (this.sigma + this.work[k - 3]) || this.work[k - 2 * this.pingPong - 4] <= 1.232595164407831E-28D * this.work[k - 7]))) {
/*1012*/        this.work[4 * deflatedEnd - 4] = this.sigma + this.work[4 * deflatedEnd - 4 + this.pingPong];
/*1013*/        deflatedEnd--;
/*   0*/        continue;
/*   0*/      } 
/*1015*/      if (start == deflatedEnd - 2 || this.work[k - 9] <= 1.232595164407831E-28D * this.sigma || this.work[k - 2 * this.pingPong - 8] <= 1.232595164407831E-28D * this.work[k - 11]) {
/*1020*/        if (this.work[k - 3] > this.work[k - 7]) {
/*1021*/          double tmp = this.work[k - 3];
/*1022*/          this.work[k - 3] = this.work[k - 7];
/*1023*/          this.work[k - 7] = tmp;
/*   0*/        } 
/*1026*/        if (this.work[k - 5] > 1.232595164407831E-28D * this.work[k - 3]) {
/*1027*/          double t = 0.5D * (this.work[k - 7] - this.work[k - 3] + this.work[k - 5]);
/*1028*/          double s = this.work[k - 3] * this.work[k - 5] / t;
/*1029*/          if (s <= t) {
/*1030*/            s = this.work[k - 3] * this.work[k - 5] / t * (1.0D + Math.sqrt(1.0D + s / t));
/*   0*/          } else {
/*1032*/            s = this.work[k - 3] * this.work[k - 5] / (t + Math.sqrt(t * (t + s)));
/*   0*/          } 
/*1034*/          t = this.work[k - 7] + s + this.work[k - 5];
/*1035*/          this.work[k - 3] = this.work[k - 3] * this.work[k - 7] / t;
/*1036*/          this.work[k - 7] = t;
/*   0*/        } 
/*1038*/        this.work[4 * deflatedEnd - 8] = this.sigma + this.work[k - 7];
/*1039*/        this.work[4 * deflatedEnd - 4] = this.sigma + this.work[k - 3];
/*1040*/        deflatedEnd -= 2;
/*   0*/        continue;
/*   0*/      } 
/*1044*/      deflating = false;
/*   0*/    } 
/*1050*/    int l = 4 * deflatedEnd + this.pingPong - 1;
/*1053*/    if ((this.dMin <= 0.0D || deflatedEnd < end) && 
/*1054*/      flipIfWarranted(deflatedEnd, 1)) {
/*1055*/      this.dMin2 = Math.min(this.dMin2, this.work[l - 1]);
/*1056*/      this.work[l - 1] = Math.min(this.work[l - 1], Math.min(this.work[3 + this.pingPong], this.work[7 + this.pingPong]));
/*1059*/      this.work[l - 2 * this.pingPong] = Math.min(this.work[l - 2 * this.pingPong], Math.min(this.work[6 + this.pingPong], this.work[6 + this.pingPong]));
/*1062*/      this.qMax = Math.max(this.qMax, Math.max(this.work[3 + this.pingPong], this.work[7 + this.pingPong]));
/*1063*/      this.dMin = 0.0D;
/*   0*/    } 
/*1067*/    if (this.dMin < 0.0D || 2.2250738585072014E-308D * this.qMax < Math.min(this.work[l - 1], Math.min(this.work[l - 9], this.dMin2 + this.work[l - 2 * this.pingPong]))) {
/*1072*/      computeShiftIncrement(start, deflatedEnd, end - deflatedEnd);
/*1075*/      for (boolean loop = true; loop; ) {
/*1078*/        dqds(start, deflatedEnd);
/*1081*/        if (this.dMin >= 0.0D && this.dMin1 > 0.0D) {
/*1083*/          updateSigma(this.tau);
/*1084*/          return deflatedEnd;
/*   0*/        } 
/*1085*/        if (this.dMin < 0.0D && this.dMin1 > 0.0D && this.work[4 * deflatedEnd - 5 - this.pingPong] < 1.1102230246251565E-14D * (this.sigma + this.dN1) && Math.abs(this.dN) < 1.1102230246251565E-14D * this.sigma) {
/*1090*/          this.work[4 * deflatedEnd - 3 - this.pingPong] = 0.0D;
/*1091*/          this.dMin = 0.0D;
/*1092*/          updateSigma(this.tau);
/*1093*/          return deflatedEnd;
/*   0*/        } 
/*1094*/        if (this.dMin < 0.0D) {
/*1096*/          if (this.tType < -22) {
/*1098*/            this.tau = 0.0D;
/*   0*/            continue;
/*   0*/          } 
/*1099*/          if (this.dMin1 > 0.0D) {
/*1101*/            this.tau = (this.tau + this.dMin) * 0.9999999999999998D;
/*1102*/            this.tType -= 11;
/*   0*/            continue;
/*   0*/          } 
/*1105*/          this.tau *= 0.25D;
/*1106*/          this.tType -= 12;
/*   0*/          continue;
/*   0*/        } 
/*1108*/        if (Double.isNaN(this.dMin)) {
/*1109*/          this.tau = 0.0D;
/*   0*/          continue;
/*   0*/        } 
/*1112*/        loop = false;
/*   0*/      } 
/*   0*/    } 
/*1119*/    dqd(start, deflatedEnd);
/*1121*/    return deflatedEnd;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean flipIfWarranted(int n, int step) {
/*1133*/    if (1.5D * this.work[this.pingPong] < this.work[4 * (n - 1) + this.pingPong]) {
/*1135*/      int j = 4 * n - 1;
/*1136*/      for (int i = 0; i < j; i += 4) {
/*1137*/        for (int k = 0; k < 4; k += step) {
/*1138*/          double tmp = this.work[i + k];
/*1139*/          this.work[i + k] = this.work[j - k];
/*1140*/          this.work[j - k] = tmp;
/*   0*/        } 
/*1142*/        j -= 4;
/*   0*/      } 
/*1144*/      return true;
/*   0*/    } 
/*1146*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private double[] eigenvaluesRange(int index, int n) {
/*1158*/    int lowerStart = 4 * this.main.length;
/*1159*/    int upperStart = 5 * this.main.length;
/*1160*/    double lower = Double.POSITIVE_INFINITY;
/*1161*/    double upper = Double.NEGATIVE_INFINITY;
/*1162*/    for (int i = 0; i < n; i++) {
/*1163*/      lower = Math.min(lower, this.work[lowerStart + index + i]);
/*1164*/      upper = Math.max(upper, this.work[upperStart + index + i]);
/*   0*/    } 
/*1168*/    double tNorm = Math.max(Math.abs(lower), Math.abs(upper));
/*1169*/    double relativeTolerance = Math.sqrt(1.1102230246251565E-16D);
/*1170*/    double absoluteTolerance = 4.0D * this.minPivot;
/*1171*/    int maxIter = 2 + (int)((Math.log(tNorm + this.minPivot) - Math.log(this.minPivot)) / Math.log(2.0D));
/*1173*/    double margin = 2.0D * (tNorm * 1.1102230246251565E-16D * n + 2.0D * this.minPivot);
/*1176*/    double left = lower - margin;
/*1177*/    double right = upper + margin;
/*1178*/    for (int k = 0; k < maxIter; k++) {
/*1180*/      double range = right - left;
/*1181*/      if (range < absoluteTolerance || range < relativeTolerance * Math.max(Math.abs(left), Math.abs(right)))
/*   0*/        break; 
/*1187*/      double middle = 0.5D * (left + right);
/*1188*/      if (countEigenValues(middle, index, n) >= 1) {
/*1189*/        right = middle;
/*   0*/      } else {
/*1191*/        left = middle;
/*   0*/      } 
/*   0*/    } 
/*1195*/    lower = Math.max(lower, left - 1.1102230246251565E-14D * Math.abs(left));
/*1198*/    left = lower - margin;
/*1199*/    right = upper + margin;
/*1200*/    for (int j = 0; j < maxIter; j++) {
/*1202*/      double range = right - left;
/*1203*/      if (range < absoluteTolerance || range < relativeTolerance * Math.max(Math.abs(left), Math.abs(right)))
/*   0*/        break; 
/*1209*/      double middle = 0.5D * (left + right);
/*1210*/      if (countEigenValues(middle, index, n) >= n) {
/*1211*/        right = middle;
/*   0*/      } else {
/*1213*/        left = middle;
/*   0*/      } 
/*   0*/    } 
/*1217*/    upper = Math.min(upper, right + 1.1102230246251565E-14D * Math.abs(right));
/*1219*/    return new double[] { lower, upper };
/*   0*/  }
/*   0*/  
/*   0*/  private int countEigenValues(double t, int index, int n) {
/*1231*/    double ratio = this.main[index] - t;
/*1232*/    int count = (ratio > 0.0D) ? 0 : 1;
/*1233*/    for (int i = 1; i < n; i++) {
/*1234*/      ratio = this.main[index + i] - this.squaredSecondary[index + i - 1] / ratio - t;
/*1235*/      if (ratio <= 0.0D)
/*1236*/        count++; 
/*   0*/    } 
/*1239*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private void ldlTDecomposition(double lambda, int index, int n) {
/*1254*/    double di = this.main[index] - lambda;
/*1255*/    this.work[0] = Math.abs(di);
/*1256*/    for (int i = 1; i < n; i++) {
/*1257*/      int fourI = 4 * i;
/*1258*/      double eiM1 = this.secondary[index + i - 1];
/*1259*/      double ratio = eiM1 / di;
/*1260*/      this.work[fourI - 2] = ratio * ratio * Math.abs(di);
/*1261*/      di = this.main[index + i] - lambda - eiM1 * ratio;
/*1262*/      this.work[fourI] = Math.abs(di);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void dqds(int start, int end) {
/*1274*/    this.eMin = this.work[4 * start + this.pingPong + 4];
/*1275*/    double d = this.work[4 * start + this.pingPong] - this.tau;
/*1276*/    this.dMin = d;
/*1277*/    this.dMin1 = -this.work[4 * start + this.pingPong];
/*1279*/    if (this.pingPong == 0) {
/*1280*/      for (int i = 4 * start + 3; i <= 4 * (end - 3); i += 4) {
/*1281*/        this.work[i - 2] = d + this.work[i - 1];
/*1282*/        double tmp = this.work[i + 1] / this.work[i - 2];
/*1283*/        d = d * tmp - this.tau;
/*1284*/        this.dMin = Math.min(this.dMin, d);
/*1285*/        this.work[i] = this.work[i - 1] * tmp;
/*1286*/        this.eMin = Math.min(this.work[i], this.eMin);
/*   0*/      } 
/*   0*/    } else {
/*1289*/      for (int i = 4 * start + 3; i <= 4 * (end - 3); i += 4) {
/*1290*/        this.work[i - 3] = d + this.work[i];
/*1291*/        double tmp = this.work[i + 2] / this.work[i - 3];
/*1292*/        d = d * tmp - this.tau;
/*1293*/        this.dMin = Math.min(this.dMin, d);
/*1294*/        this.work[i - 1] = this.work[i] * tmp;
/*1295*/        this.eMin = Math.min(this.work[i - 1], this.eMin);
/*   0*/      } 
/*   0*/    } 
/*1300*/    this.dN2 = d;
/*1301*/    this.dMin2 = this.dMin;
/*1302*/    int j4 = 4 * (end - 2) - this.pingPong - 1;
/*1303*/    int j4p2 = j4 + 2 * this.pingPong - 1;
/*1304*/    this.work[j4 - 2] = this.dN2 + this.work[j4p2];
/*1305*/    this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1306*/    this.dN1 = this.work[j4p2 + 2] * this.dN2 / this.work[j4 - 2] - this.tau;
/*1307*/    this.dMin = Math.min(this.dMin, this.dN1);
/*1309*/    this.dMin1 = this.dMin;
/*1310*/    j4 += 4;
/*1311*/    j4p2 = j4 + 2 * this.pingPong - 1;
/*1312*/    this.work[j4 - 2] = this.dN1 + this.work[j4p2];
/*1313*/    this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1314*/    this.dN = this.work[j4p2 + 2] * this.dN1 / this.work[j4 - 2] - this.tau;
/*1315*/    this.dMin = Math.min(this.dMin, this.dN);
/*1317*/    this.work[j4 + 2] = this.dN;
/*1318*/    this.work[4 * end - this.pingPong - 1] = this.eMin;
/*   0*/  }
/*   0*/  
/*   0*/  private void dqd(int start, int end) {
/*1331*/    this.eMin = this.work[4 * start + this.pingPong + 4];
/*1332*/    double d = this.work[4 * start + this.pingPong];
/*1333*/    this.dMin = d;
/*1335*/    if (this.pingPong == 0) {
/*1336*/      for (int i = 4 * start + 3; i < 4 * (end - 3); i += 4) {
/*1337*/        this.work[i - 2] = d + this.work[i - 1];
/*1338*/        if (this.work[i - 2] == 0.0D) {
/*1339*/          this.work[i] = 0.0D;
/*1340*/          d = this.work[i + 1];
/*1341*/          this.dMin = d;
/*1342*/          this.eMin = 0.0D;
/*1343*/        } else if (2.2250738585072014E-308D * this.work[i + 1] < this.work[i - 2] && 2.2250738585072014E-308D * this.work[i - 2] < this.work[i + 1]) {
/*1345*/          double tmp = this.work[i + 1] / this.work[i - 2];
/*1346*/          this.work[i] = this.work[i - 1] * tmp;
/*1347*/          d *= tmp;
/*   0*/        } else {
/*1349*/          this.work[i] = this.work[i + 1] * this.work[i - 1] / this.work[i - 2];
/*1350*/          d *= this.work[i + 1] / this.work[i - 2];
/*   0*/        } 
/*1352*/        this.dMin = Math.min(this.dMin, d);
/*1353*/        this.eMin = Math.min(this.eMin, this.work[i]);
/*   0*/      } 
/*   0*/    } else {
/*1356*/      for (int i = 4 * start + 3; i < 4 * (end - 3); i += 4) {
/*1357*/        this.work[i - 3] = d + this.work[i];
/*1358*/        if (this.work[i - 3] == 0.0D) {
/*1359*/          this.work[i - 1] = 0.0D;
/*1360*/          d = this.work[i + 2];
/*1361*/          this.dMin = d;
/*1362*/          this.eMin = 0.0D;
/*1363*/        } else if (2.2250738585072014E-308D * this.work[i + 2] < this.work[i - 3] && 2.2250738585072014E-308D * this.work[i - 3] < this.work[i + 2]) {
/*1365*/          double tmp = this.work[i + 2] / this.work[i - 3];
/*1366*/          this.work[i - 1] = this.work[i] * tmp;
/*1367*/          d *= tmp;
/*   0*/        } else {
/*1369*/          this.work[i - 1] = this.work[i + 2] * this.work[i] / this.work[i - 3];
/*1370*/          d *= this.work[i + 2] / this.work[i - 3];
/*   0*/        } 
/*1372*/        this.dMin = Math.min(this.dMin, d);
/*1373*/        this.eMin = Math.min(this.eMin, this.work[i - 1]);
/*   0*/      } 
/*   0*/    } 
/*1378*/    this.dN2 = d;
/*1379*/    this.dMin2 = this.dMin;
/*1380*/    int j4 = 4 * (end - 2) - this.pingPong - 1;
/*1381*/    int j4p2 = j4 + 2 * this.pingPong - 1;
/*1382*/    this.work[j4 - 2] = this.dN2 + this.work[j4p2];
/*1383*/    if (this.work[j4 - 2] == 0.0D) {
/*1384*/      this.work[j4] = 0.0D;
/*1385*/      this.dN1 = this.work[j4p2 + 2];
/*1386*/      this.dMin = this.dN1;
/*1387*/      this.eMin = 0.0D;
/*1388*/    } else if (2.2250738585072014E-308D * this.work[j4p2 + 2] < this.work[j4 - 2] && 2.2250738585072014E-308D * this.work[j4 - 2] < this.work[j4p2 + 2]) {
/*1390*/      double tmp = this.work[j4p2 + 2] / this.work[j4 - 2];
/*1391*/      this.work[j4] = this.work[j4p2] * tmp;
/*1392*/      this.dN1 = this.dN2 * tmp;
/*   0*/    } else {
/*1394*/      this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1395*/      this.dN1 = this.work[j4p2 + 2] * this.dN2 / this.work[j4 - 2];
/*   0*/    } 
/*1397*/    this.dMin = Math.min(this.dMin, this.dN1);
/*1399*/    this.dMin1 = this.dMin;
/*1400*/    j4 += 4;
/*1401*/    j4p2 = j4 + 2 * this.pingPong - 1;
/*1402*/    this.work[j4 - 2] = this.dN1 + this.work[j4p2];
/*1403*/    if (this.work[j4 - 2] == 0.0D) {
/*1404*/      this.work[j4] = 0.0D;
/*1405*/      this.dN = this.work[j4p2 + 2];
/*1406*/      this.dMin = this.dN;
/*1407*/      this.eMin = 0.0D;
/*1408*/    } else if (2.2250738585072014E-308D * this.work[j4p2 + 2] < this.work[j4 - 2] && 2.2250738585072014E-308D * this.work[j4 - 2] < this.work[j4p2 + 2]) {
/*1410*/      double tmp = this.work[j4p2 + 2] / this.work[j4 - 2];
/*1411*/      this.work[j4] = this.work[j4p2] * tmp;
/*1412*/      this.dN = this.dN1 * tmp;
/*   0*/    } else {
/*1414*/      this.work[j4] = this.work[j4p2 + 2] * this.work[j4p2] / this.work[j4 - 2];
/*1415*/      this.dN = this.work[j4p2 + 2] * this.dN1 / this.work[j4 - 2];
/*   0*/    } 
/*1417*/    this.dMin = Math.min(this.dMin, this.dN);
/*1419*/    this.work[j4 + 2] = this.dN;
/*1420*/    this.work[4 * end - this.pingPong - 1] = this.eMin;
/*   0*/  }
/*   0*/  
/*   0*/  private void computeShiftIncrement(int start, int end, int deflated) {
/*1433*/    double cnst1 = 0.563D;
/*1434*/    double cnst2 = 1.01D;
/*1435*/    double cnst3 = 1.05D;
/*1439*/    if (this.dMin <= 0.0D) {
/*1440*/      this.tau = -this.dMin;
/*1441*/      this.tType = -1;
/*   0*/      return;
/*   0*/    } 
/*1445*/    int nn = 4 * end + this.pingPong - 1;
/*1446*/    switch (deflated) {
/*   0*/      case 0:
/*1449*/        if (this.dMin == this.dN || this.dMin == this.dN1) {
/*   0*/          double gam;
/*   0*/          int np;
/*1451*/          double b1 = Math.sqrt(this.work[nn - 3]) * Math.sqrt(this.work[nn - 5]);
/*1452*/          double b2 = Math.sqrt(this.work[nn - 7]) * Math.sqrt(this.work[nn - 9]);
/*1453*/          double a2 = this.work[nn - 7] + this.work[nn - 5];
/*1455*/          if (this.dMin == this.dN && this.dMin1 == this.dN1) {
/*1457*/            double gap2 = this.dMin2 - a2 - this.dMin2 * 0.25D;
/*1458*/            double gap1 = a2 - this.dN - ((gap2 > 0.0D && gap2 > b2) ? (b2 / gap2 * b2) : (b1 + b2));
/*1459*/            if (gap1 > 0.0D && gap1 > b1) {
/*1460*/              this.tau = Math.max(this.dN - b1 / gap1 * b1, 0.5D * this.dMin);
/*1461*/              this.tType = -2;
/*   0*/              break;
/*   0*/            } 
/*1463*/            double d1 = 0.0D;
/*1464*/            if (this.dN > b1)
/*1465*/              d1 = this.dN - b1; 
/*1467*/            if (a2 > b1 + b2)
/*1468*/              d1 = Math.min(d1, a2 - b1 + b2); 
/*1470*/            this.tau = Math.max(d1, 0.333D * this.dMin);
/*1471*/            this.tType = -3;
/*   0*/            break;
/*   0*/          } 
/*1475*/          this.tType = -4;
/*1476*/          double s = 0.25D * this.dMin;
/*1479*/          if (this.dMin == this.dN) {
/*1480*/            gam = this.dN;
/*1481*/            a2 = 0.0D;
/*1482*/            if (this.work[nn - 5] > this.work[nn - 7])
/*   0*/              return; 
/*1485*/            b2 = this.work[nn - 5] / this.work[nn - 7];
/*1486*/            np = nn - 9;
/*   0*/          } else {
/*1488*/            np = nn - 2 * this.pingPong;
/*1489*/            b2 = this.work[np - 2];
/*1490*/            gam = this.dN1;
/*1491*/            if (this.work[np - 4] > this.work[np - 2])
/*   0*/              return; 
/*1494*/            a2 = this.work[np - 4] / this.work[np - 2];
/*1495*/            if (this.work[nn - 9] > this.work[nn - 11])
/*   0*/              return; 
/*1498*/            b2 = this.work[nn - 9] / this.work[nn - 11];
/*1499*/            np = nn - 13;
/*   0*/          } 
/*1503*/          a2 += b2;
/*1504*/          for (int i4 = np; i4 >= 4 * start + 2 + this.pingPong && 
/*1505*/            b2 != 0.0D; i4 -= 4) {
/*1508*/            b1 = b2;
/*1509*/            if (this.work[i4] > this.work[i4 - 2])
/*   0*/              return; 
/*1512*/            b2 *= this.work[i4] / this.work[i4 - 2];
/*1513*/            a2 += b2;
/*1514*/            if (100.0D * Math.max(b2, b1) < a2 || 0.563D < a2)
/*   0*/              break; 
/*   0*/          } 
/*1518*/          a2 = 1.05D * a2;
/*1521*/          if (a2 < 0.563D)
/*1522*/            s = gam * (1.0D - Math.sqrt(a2)) / (1.0D + a2); 
/*1524*/          this.tau = s;
/*   0*/          break;
/*   0*/        } 
/*1527*/        if (this.dMin == this.dN2) {
/*1530*/          this.tType = -5;
/*1531*/          double s = 0.25D * this.dMin;
/*1534*/          int np = nn - 2 * this.pingPong;
/*1535*/          double b1 = this.work[np - 2];
/*1536*/          double b2 = this.work[np - 6];
/*1537*/          double gam = this.dN2;
/*1538*/          if (this.work[np - 8] > b2 || this.work[np - 4] > b1)
/*   0*/            return; 
/*1541*/          double a2 = this.work[np - 8] / b2 * (1.0D + this.work[np - 4] / b1);
/*1544*/          if (end - start > 3) {
/*1545*/            b2 = this.work[nn - 13] / this.work[nn - 15];
/*1546*/            a2 += b2;
/*1547*/            for (int i4 = nn - 17; i4 >= 4 * start + 2 + this.pingPong && 
/*1548*/              b2 != 0.0D; i4 -= 4) {
/*1551*/              b1 = b2;
/*1552*/              if (this.work[i4] > this.work[i4 - 2])
/*   0*/                return; 
/*1555*/              b2 *= this.work[i4] / this.work[i4 - 2];
/*1556*/              a2 += b2;
/*1557*/              if (100.0D * Math.max(b2, b1) < a2 || 0.563D < a2)
/*   0*/                break; 
/*   0*/            } 
/*1561*/            a2 = 1.05D * a2;
/*   0*/          } 
/*1564*/          if (a2 < 0.563D) {
/*1565*/            this.tau = gam * (1.0D - Math.sqrt(a2)) / (1.0D + a2);
/*   0*/            break;
/*   0*/          } 
/*1567*/          this.tau = s;
/*   0*/          break;
/*   0*/        } 
/*1573*/        if (this.tType == -6) {
/*1574*/          this.g += 0.333D * (1.0D - this.g);
/*1575*/        } else if (this.tType == -18) {
/*1576*/          this.g = 0.08325D;
/*   0*/        } else {
/*1578*/          this.g = 0.25D;
/*   0*/        } 
/*1580*/        this.tau = this.g * this.dMin;
/*1581*/        this.tType = -6;
/*   0*/        break;
/*   0*/      case 1:
/*1587*/        if (this.dMin1 == this.dN1 && this.dMin2 == this.dN2) {
/*1590*/          this.tType = -7;
/*1591*/          double s = 0.333D * this.dMin1;
/*1592*/          if (this.work[nn - 5] > this.work[nn - 7])
/*   0*/            return; 
/*1595*/          double b1 = this.work[nn - 5] / this.work[nn - 7];
/*1596*/          double b2 = b1;
/*1597*/          if (b2 != 0.0D)
/*1598*/            for (int i4 = 4 * end - 10 + this.pingPong; i4 >= 4 * start + 2 + this.pingPong; i4 -= 4) {
/*1599*/              double oldB1 = b1;
/*1600*/              if (this.work[i4] > this.work[i4 - 2])
/*   0*/                return; 
/*1603*/              b1 *= this.work[i4] / this.work[i4 - 2];
/*1604*/              b2 += b1;
/*1605*/              if (100.0D * Math.max(b1, oldB1) < b2)
/*   0*/                break; 
/*   0*/            }  
/*1610*/          b2 = Math.sqrt(1.05D * b2);
/*1611*/          double a2 = this.dMin1 / (1.0D + b2 * b2);
/*1612*/          double gap2 = 0.5D * this.dMin2 - a2;
/*1613*/          if (gap2 > 0.0D && gap2 > b2 * a2) {
/*1614*/            this.tau = Math.max(s, a2 * (1.0D - 1.01D * a2 * b2 / gap2 * b2));
/*   0*/            break;
/*   0*/          } 
/*1616*/          this.tau = Math.max(s, a2 * (1.0D - 1.01D * b2));
/*1617*/          this.tType = -8;
/*   0*/          break;
/*   0*/        } 
/*1622*/        this.tau = 0.25D * this.dMin1;
/*1623*/        if (this.dMin1 == this.dN1)
/*1624*/          this.tau = 0.5D * this.dMin1; 
/*1626*/        this.tType = -9;
/*   0*/        break;
/*   0*/      case 2:
/*1633*/        if (this.dMin2 == this.dN2 && 2.0D * this.work[nn - 5] < this.work[nn - 7]) {
/*1634*/          this.tType = -10;
/*1635*/          double s = 0.333D * this.dMin2;
/*1636*/          if (this.work[nn - 5] > this.work[nn - 7])
/*   0*/            return; 
/*1639*/          double b1 = this.work[nn - 5] / this.work[nn - 7];
/*1640*/          double b2 = b1;
/*1641*/          if (b2 != 0.0D)
/*1642*/            for (int i4 = 4 * end - 9 + this.pingPong; i4 >= 4 * start + 2 + this.pingPong; i4 -= 4) {
/*1643*/              if (this.work[i4] > this.work[i4 - 2])
/*   0*/                return; 
/*1646*/              b1 *= this.work[i4] / this.work[i4 - 2];
/*1647*/              b2 += b1;
/*1648*/              if (100.0D * b1 < b2)
/*   0*/                break; 
/*   0*/            }  
/*1653*/          b2 = Math.sqrt(1.05D * b2);
/*1654*/          double a2 = this.dMin2 / (1.0D + b2 * b2);
/*1655*/          double gap2 = this.work[nn - 7] + this.work[nn - 9] - Math.sqrt(this.work[nn - 11]) * Math.sqrt(this.work[nn - 9]) - a2;
/*1657*/          if (gap2 > 0.0D && gap2 > b2 * a2) {
/*1658*/            this.tau = Math.max(s, a2 * (1.0D - 1.01D * a2 * b2 / gap2 * b2));
/*   0*/            break;
/*   0*/          } 
/*1660*/          this.tau = Math.max(s, a2 * (1.0D - 1.01D * b2));
/*   0*/          break;
/*   0*/        } 
/*1663*/        this.tau = 0.25D * this.dMin2;
/*1664*/        this.tType = -11;
/*   0*/        break;
/*   0*/      default:
/*1669*/        this.tau = 0.0D;
/*1670*/        this.tType = -12;
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateSigma(double shift) {
/*1683*/    if (shift < this.sigma) {
/*1684*/      this.sigmaLow += shift;
/*1685*/      double t = this.sigma + this.sigmaLow;
/*1686*/      this.sigmaLow -= t - this.sigma;
/*1687*/      this.sigma = t;
/*   0*/    } else {
/*1689*/      double t = this.sigma + shift;
/*1690*/      this.sigmaLow += this.sigma - t - shift;
/*1691*/      this.sigma = t;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void findEigenVectors() {
/*1700*/    int m = this.main.length;
/*1701*/    this.eigenvectors = new ArrayRealVector[m];
/*1704*/    double[] d = new double[m];
/*1705*/    double[] l = new double[m - 1];
/*1707*/    double mu = (this.realEigenvalues[m - 1] <= 0.0D && this.realEigenvalues[0] > 0.0D) ? (0.5D - this.realEigenvalues[m - 1]) : 0.0D;
/*1708*/    double di = this.main[0] + mu;
/*1709*/    d[0] = di;
/*1710*/    for (int i = 1; i < m; i++) {
/*1711*/      double eiM1 = this.secondary[i - 1];
/*1712*/      double ratio = eiM1 / di;
/*1713*/      di = this.main[i] - eiM1 * ratio + mu;
/*1714*/      l[i - 1] = ratio;
/*1715*/      d[i] = di;
/*   0*/    } 
/*1719*/    for (int j = 0; j < m; j++)
/*1720*/      this.eigenvectors[j] = findEigenvector(this.realEigenvalues[j] + mu, d, l); 
/*   0*/  }
/*   0*/  
/*   0*/  private ArrayRealVector findEigenvector(double eigenvalue, double[] d, double[] l) {
/*1739*/    int m = this.main.length;
/*1740*/    stationaryQuotientDifferenceWithShift(d, l, eigenvalue);
/*1741*/    progressiveQuotientDifferenceWithShift(d, l, eigenvalue);
/*1745*/    int r = m - 1;
/*1746*/    double minG = Math.abs(this.work[6 * r] + this.work[6 * r + 3] + eigenvalue);
/*1747*/    int sixI = 0;
/*1748*/    for (int i = 0; i < m - 1; i++) {
/*1749*/      double absG = Math.abs(this.work[sixI] + d[i] * this.work[sixI + 9] / this.work[sixI + 10]);
/*1750*/      if (absG < minG) {
/*1751*/        r = i;
/*1752*/        minG = absG;
/*   0*/      } 
/*1754*/      sixI += 6;
/*   0*/    } 
/*1759*/    double[] eigenvector = new double[m];
/*1760*/    double n2 = 1.0D;
/*1761*/    eigenvector[r] = 1.0D;
/*1762*/    double z = 1.0D;
/*1763*/    for (int k = r - 1; k >= 0; k--) {
/*1764*/      z *= -this.work[6 * k + 2];
/*1765*/      eigenvector[k] = z;
/*1766*/      n2 += z * z;
/*   0*/    } 
/*1768*/    z = 1.0D;
/*1769*/    for (int j = r + 1; j < m; j++) {
/*1770*/      z *= -this.work[6 * j - 1];
/*1771*/      eigenvector[j] = z;
/*1772*/      n2 += z * z;
/*   0*/    } 
/*1776*/    double inv = 1.0D / Math.sqrt(n2);
/*1777*/    for (int n = 0; n < m; n++)
/*1778*/      eigenvector[n] = eigenvector[n] * inv; 
/*1781*/    return (this.transformer == null) ? new ArrayRealVector(eigenvector, false) : new ArrayRealVector(this.transformer.getQ().operate(eigenvector), false);
/*   0*/  }
/*   0*/  
/*   0*/  private void stationaryQuotientDifferenceWithShift(double[] d, double[] l, double lambda) {
/*1797*/    int nM1 = d.length - 1;
/*1798*/    double si = -lambda;
/*1799*/    int sixI = 0;
/*1800*/    for (int i = 0; i < nM1; i++) {
/*1801*/      double di = d[i];
/*1802*/      double li = l[i];
/*1803*/      double diP1 = di + si;
/*1804*/      double liP1 = li * di / diP1;
/*1805*/      this.work[sixI] = si;
/*1806*/      this.work[sixI + 1] = diP1;
/*1807*/      this.work[sixI + 2] = liP1;
/*1808*/      si = li * liP1 * si - lambda;
/*1809*/      sixI += 6;
/*   0*/    } 
/*1811*/    this.work[6 * nM1 + 1] = d[nM1] + si;
/*1812*/    this.work[6 * nM1] = si;
/*   0*/  }
/*   0*/  
/*   0*/  private void progressiveQuotientDifferenceWithShift(double[] d, double[] l, double lambda) {
/*1825*/    int nM1 = d.length - 1;
/*1826*/    double pi = d[nM1] - lambda;
/*1827*/    int sixI = 6 * (nM1 - 1);
/*1828*/    for (int i = nM1 - 1; i >= 0; i--) {
/*1829*/      double di = d[i];
/*1830*/      double li = l[i];
/*1831*/      double diP1 = di * li * li + pi;
/*1832*/      double t = di / diP1;
/*1833*/      this.work[sixI + 9] = pi;
/*1834*/      this.work[sixI + 10] = diP1;
/*1835*/      this.work[sixI + 5] = li * t;
/*1836*/      pi = pi * t - lambda;
/*1837*/      sixI -= 6;
/*   0*/    } 
/*1839*/    this.work[3] = pi;
/*1840*/    this.work[4] = pi;
/*   0*/  }
/*   0*/}
