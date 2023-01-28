/*   0*/package org.apache.commons.math.optimization.general;
/*   0*/
/*   0*/import java.util.Arrays;
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math.optimization.OptimizationException;
/*   0*/import org.apache.commons.math.optimization.VectorialPointValuePair;
/*   0*/
/*   0*/public class LevenbergMarquardtOptimizer extends AbstractLeastSquaresOptimizer {
/*   0*/  private int solvedCols;
/*   0*/  
/*   0*/  private double[] diagR;
/*   0*/  
/*   0*/  private double[] jacNorm;
/*   0*/  
/*   0*/  private double[] beta;
/*   0*/  
/*   0*/  private int[] permutation;
/*   0*/  
/*   0*/  private int rank;
/*   0*/  
/*   0*/  private double lmPar;
/*   0*/  
/*   0*/  private double[] lmDir;
/*   0*/  
/*   0*/  private double initialStepBoundFactor;
/*   0*/  
/*   0*/  private double costRelativeTolerance;
/*   0*/  
/*   0*/  private double parRelativeTolerance;
/*   0*/  
/*   0*/  private double orthoTolerance;
/*   0*/  
/*   0*/  private double qrRankingThreshold;
/*   0*/  
/*   0*/  public LevenbergMarquardtOptimizer() {
/* 169*/    setMaxIterations(1000);
/* 172*/    setConvergenceChecker(null);
/* 173*/    setInitialStepBoundFactor(100.0D);
/* 174*/    setCostRelativeTolerance(1.0E-10D);
/* 175*/    setParRelativeTolerance(1.0E-10D);
/* 176*/    setOrthoTolerance(1.0E-10D);
/* 177*/    setQRRankingThreshold(2.2250738585072014E-308D);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInitialStepBoundFactor(double initialStepBoundFactor) {
/* 191*/    this.initialStepBoundFactor = initialStepBoundFactor;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCostRelativeTolerance(double costRelativeTolerance) {
/* 201*/    this.costRelativeTolerance = costRelativeTolerance;
/*   0*/  }
/*   0*/  
/*   0*/  public void setParRelativeTolerance(double parRelativeTolerance) {
/* 212*/    this.parRelativeTolerance = parRelativeTolerance;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOrthoTolerance(double orthoTolerance) {
/* 223*/    this.orthoTolerance = orthoTolerance;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQRRankingThreshold(double threshold) {
/* 236*/    this.qrRankingThreshold = threshold;
/*   0*/  }
/*   0*/  
/*   0*/  protected VectorialPointValuePair doOptimize() throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {
/* 245*/    this.solvedCols = Math.min(this.rows, this.cols);
/* 246*/    this.diagR = new double[this.cols];
/* 247*/    this.jacNorm = new double[this.cols];
/* 248*/    this.beta = new double[this.cols];
/* 249*/    this.permutation = new int[this.cols];
/* 250*/    this.lmDir = new double[this.cols];
/* 253*/    double delta = 0.0D;
/* 254*/    double xNorm = 0.0D;
/* 255*/    double[] diag = new double[this.cols];
/* 256*/    double[] oldX = new double[this.cols];
/* 257*/    double[] oldRes = new double[this.rows];
/* 258*/    double[] work1 = new double[this.cols];
/* 259*/    double[] work2 = new double[this.cols];
/* 260*/    double[] work3 = new double[this.cols];
/* 263*/    updateResidualsAndCost();
/* 266*/    this.lmPar = 0.0D;
/*   0*/    boolean firstIteration = true;
/* 268*/    VectorialPointValuePair current = new VectorialPointValuePair(this.point, this.objective);
/*   0*/    while (true) {
/* 270*/      incrementIterationsCounter();
/* 273*/      VectorialPointValuePair previous = current;
/* 274*/      updateJacobian();
/* 275*/      qrDecomposition();
/* 278*/      qTy(this.residuals);
/* 281*/      for (int k = 0; k < this.solvedCols; k++) {
/* 282*/        int pk = this.permutation[k];
/* 283*/        this.jacobian[k][pk] = this.diagR[pk];
/*   0*/      } 
/* 286*/      if (firstIteration) {
/* 290*/        xNorm = 0.0D;
/* 291*/        for (int i = 0; i < this.cols; i++) {
/* 292*/          double dk = this.jacNorm[i];
/* 293*/          if (dk == 0.0D)
/* 294*/            dk = 1.0D; 
/* 296*/          double xk = dk * this.point[i];
/* 297*/          xNorm += xk * xk;
/* 298*/          diag[i] = dk;
/*   0*/        } 
/* 300*/        xNorm = Math.sqrt(xNorm);
/* 303*/        delta = (xNorm == 0.0D) ? this.initialStepBoundFactor : (this.initialStepBoundFactor * xNorm);
/*   0*/      } 
/* 308*/      double maxCosine = 0.0D;
/* 309*/      if (this.cost != 0.0D)
/* 310*/        for (int i = 0; i < this.solvedCols; i++) {
/* 311*/          int pj = this.permutation[i];
/* 312*/          double s = this.jacNorm[pj];
/* 313*/          if (s != 0.0D) {
/* 314*/            double sum = 0.0D;
/* 315*/            for (int m = 0; m <= i; m++)
/* 316*/              sum += this.jacobian[m][pj] * this.residuals[m]; 
/* 318*/            maxCosine = Math.max(maxCosine, Math.abs(sum) / s * this.cost);
/*   0*/          } 
/*   0*/        }  
/* 322*/      if (maxCosine <= this.orthoTolerance)
/* 324*/        return current; 
/* 328*/      for (int j = 0; j < this.cols; j++)
/* 329*/        diag[j] = Math.max(diag[j], this.jacNorm[j]); 
/* 333*/      for (double ratio = 0.0D; ratio < 1.0E-4D; ) {
/* 336*/        for (int i = 0; i < this.solvedCols; i++) {
/* 337*/          int pj = this.permutation[i];
/* 338*/          oldX[pj] = this.point[pj];
/*   0*/        } 
/* 340*/        double previousCost = this.cost;
/* 341*/        double[] tmpVec = this.residuals;
/* 342*/        this.residuals = oldRes;
/* 343*/        oldRes = tmpVec;
/* 346*/        determineLMParameter(oldRes, delta, diag, work1, work2, work3);
/* 349*/        double lmNorm = 0.0D;
/* 350*/        for (int m = 0; m < this.solvedCols; m++) {
/* 351*/          int pj = this.permutation[m];
/* 352*/          this.lmDir[pj] = -this.lmDir[pj];
/* 353*/          this.point[pj] = oldX[pj] + this.lmDir[pj];
/* 354*/          double s = diag[pj] * this.lmDir[pj];
/* 355*/          lmNorm += s * s;
/*   0*/        } 
/* 357*/        lmNorm = Math.sqrt(lmNorm);
/* 359*/        if (firstIteration)
/* 360*/          delta = Math.min(delta, lmNorm); 
/* 364*/        updateResidualsAndCost();
/* 365*/        current = new VectorialPointValuePair(this.point, this.objective);
/* 368*/        double actRed = -1.0D;
/* 369*/        if (0.1D * this.cost < previousCost) {
/* 370*/          double r = this.cost / previousCost;
/* 371*/          actRed = 1.0D - r * r;
/*   0*/        } 
/* 376*/        for (int n = 0; n < this.solvedCols; n++) {
/* 377*/          int pj = this.permutation[n];
/* 378*/          double dirJ = this.lmDir[pj];
/* 379*/          work1[n] = 0.0D;
/* 380*/          for (int i2 = 0; i2 <= n; i2++)
/* 381*/            work1[i2] = work1[i2] + this.jacobian[i2][pj] * dirJ; 
/*   0*/        } 
/* 384*/        double coeff1 = 0.0D;
/* 385*/        for (int i1 = 0; i1 < this.solvedCols; i1++)
/* 386*/          coeff1 += work1[i1] * work1[i1]; 
/* 388*/        double pc2 = previousCost * previousCost;
/* 389*/        coeff1 /= pc2;
/* 390*/        double coeff2 = this.lmPar * lmNorm * lmNorm / pc2;
/* 391*/        double preRed = coeff1 + 2.0D * coeff2;
/* 392*/        double dirDer = -(coeff1 + coeff2);
/* 395*/        ratio = (preRed == 0.0D) ? 0.0D : (actRed / preRed);
/* 398*/        if (ratio <= 0.25D) {
/* 399*/          double tmp = (actRed < 0.0D) ? (0.5D * dirDer / (dirDer + 0.5D * actRed)) : 0.5D;
/* 401*/          if (0.1D * this.cost >= previousCost || tmp < 0.1D)
/* 402*/            tmp = 0.1D; 
/* 404*/          delta = tmp * Math.min(delta, 10.0D * lmNorm);
/* 405*/          this.lmPar /= tmp;
/* 406*/        } else if (this.lmPar == 0.0D || ratio >= 0.75D) {
/* 407*/          delta = 2.0D * lmNorm;
/* 408*/          this.lmPar *= 0.5D;
/*   0*/        } 
/* 412*/        if (ratio >= 1.0E-4D) {
/* 414*/          firstIteration = false;
/* 415*/          xNorm = 0.0D;
/* 416*/          for (int i2 = 0; i2 < this.cols; i2++) {
/* 417*/            double xK = diag[i2] * this.point[i2];
/* 418*/            xNorm += xK * xK;
/*   0*/          } 
/* 421*/          xNorm = Math.sqrt(xNorm);
/*   0*/        } else {
/* 426*/          this.cost = previousCost;
/* 427*/          for (int i2 = 0; i2 < this.solvedCols; i2++) {
/* 428*/            int pj = this.permutation[i2];
/* 429*/            this.point[pj] = oldX[pj];
/*   0*/          } 
/* 431*/          tmpVec = this.residuals;
/* 432*/          this.residuals = oldRes;
/* 433*/          oldRes = tmpVec;
/*   0*/        } 
/* 435*/        if (this.checker == null) {
/* 436*/          if ((Math.abs(actRed) <= this.costRelativeTolerance && preRed <= this.costRelativeTolerance && ratio <= 2.0D) || delta <= this.parRelativeTolerance * xNorm)
/* 440*/            return current; 
/* 443*/        } else if (this.checker.converged(getIterations(), previous, current)) {
/* 444*/          return current;
/*   0*/        } 
/* 449*/        if (Math.abs(actRed) <= 2.2204E-16D && preRed <= 2.2204E-16D && ratio <= 2.0D)
/* 450*/          throw new OptimizationException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE, new Object[] { this.costRelativeTolerance }); 
/* 452*/        if (delta <= 2.2204E-16D * xNorm)
/* 453*/          throw new OptimizationException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, new Object[] { this.parRelativeTolerance }); 
/* 455*/        if (maxCosine <= 2.2204E-16D)
/* 456*/          throw new OptimizationException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE, new Object[] { this.orthoTolerance }); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void determineLMParameter(double[] qy, double delta, double[] diag, double[] work1, double[] work2, double[] work3) {
/* 493*/    for (int j = 0; j < this.rank; j++)
/* 494*/      this.lmDir[this.permutation[j]] = qy[j]; 
/* 496*/    for (int i = this.rank; i < this.cols; i++)
/* 497*/      this.lmDir[this.permutation[i]] = 0.0D; 
/* 499*/    for (int k = this.rank - 1; k >= 0; k--) {
/* 500*/      int pk = this.permutation[k];
/* 501*/      double ypk = this.lmDir[pk] / this.diagR[pk];
/* 502*/      for (int i1 = 0; i1 < k; i1++)
/* 503*/        this.lmDir[this.permutation[i1]] = this.lmDir[this.permutation[i1]] - ypk * this.jacobian[i1][pk]; 
/* 505*/      this.lmDir[pk] = ypk;
/*   0*/    } 
/* 510*/    double dxNorm = 0.0D;
/* 511*/    for (int m = 0; m < this.solvedCols; m++) {
/* 512*/      int pj = this.permutation[m];
/* 513*/      double s = diag[pj] * this.lmDir[pj];
/* 514*/      work1[pj] = s;
/* 515*/      dxNorm += s * s;
/*   0*/    } 
/* 517*/    dxNorm = Math.sqrt(dxNorm);
/* 518*/    double fp = dxNorm - delta;
/* 519*/    if (fp <= 0.1D * delta) {
/* 520*/      this.lmPar = 0.0D;
/*   0*/      return;
/*   0*/    } 
/* 528*/    double parl = 0.0D;
/* 529*/    if (this.rank == this.solvedCols) {
/* 530*/      for (int i2 = 0; i2 < this.solvedCols; i2++) {
/* 531*/        int pj = this.permutation[i2];
/* 532*/        work1[pj] = work1[pj] * diag[pj] / dxNorm;
/*   0*/      } 
/* 534*/      double d = 0.0D;
/* 535*/      for (int i1 = 0; i1 < this.solvedCols; i1++) {
/* 536*/        int pj = this.permutation[i1];
/* 537*/        double sum = 0.0D;
/* 538*/        for (int i3 = 0; i3 < i1; i3++)
/* 539*/          sum += this.jacobian[i3][pj] * work1[this.permutation[i3]]; 
/* 541*/        double s = (work1[pj] - sum) / this.diagR[pj];
/* 542*/        work1[pj] = s;
/* 543*/        d += s * s;
/*   0*/      } 
/* 545*/      parl = fp / delta * d;
/*   0*/    } 
/* 549*/    double sum2 = 0.0D;
/* 550*/    for (int n = 0; n < this.solvedCols; n++) {
/* 551*/      int pj = this.permutation[n];
/* 552*/      double sum = 0.0D;
/* 553*/      for (int i1 = 0; i1 <= n; i1++)
/* 554*/        sum += this.jacobian[i1][pj] * qy[i1]; 
/* 556*/      sum /= diag[pj];
/* 557*/      sum2 += sum * sum;
/*   0*/    } 
/* 559*/    double gNorm = Math.sqrt(sum2);
/* 560*/    double paru = gNorm / delta;
/* 561*/    if (paru == 0.0D)
/* 563*/      paru = 2.2251E-308D / Math.min(delta, 0.1D); 
/* 568*/    this.lmPar = Math.min(paru, Math.max(this.lmPar, parl));
/* 569*/    if (this.lmPar == 0.0D)
/* 570*/      this.lmPar = gNorm / dxNorm; 
/* 573*/    for (int countdown = 10; countdown >= 0; countdown--) {
/* 576*/      if (this.lmPar == 0.0D)
/* 577*/        this.lmPar = Math.max(2.2251E-308D, 0.001D * paru); 
/* 579*/      double sPar = Math.sqrt(this.lmPar);
/* 580*/      for (int i2 = 0; i2 < this.solvedCols; i2++) {
/* 581*/        int pj = this.permutation[i2];
/* 582*/        work1[pj] = sPar * diag[pj];
/*   0*/      } 
/* 584*/      determineLMDirection(qy, work1, work2, work3);
/* 586*/      dxNorm = 0.0D;
/* 587*/      for (int i1 = 0; i1 < this.solvedCols; i1++) {
/* 588*/        int pj = this.permutation[i1];
/* 589*/        double s = diag[pj] * this.lmDir[pj];
/* 590*/        work3[pj] = s;
/* 591*/        dxNorm += s * s;
/*   0*/      } 
/* 593*/      dxNorm = Math.sqrt(dxNorm);
/* 594*/      double previousFP = fp;
/* 595*/      fp = dxNorm - delta;
/* 599*/      if (Math.abs(fp) <= 0.1D * delta || (parl == 0.0D && fp <= previousFP && previousFP < 0.0D))
/*   0*/        return; 
/* 605*/      for (int i5 = 0; i5 < this.solvedCols; i5++) {
/* 606*/        int pj = this.permutation[i5];
/* 607*/        work1[pj] = work3[pj] * diag[pj] / dxNorm;
/*   0*/      } 
/* 609*/      for (int i4 = 0; i4 < this.solvedCols; i4++) {
/* 610*/        int pj = this.permutation[i4];
/* 611*/        work1[pj] = work1[pj] / work2[i4];
/* 612*/        double tmp = work1[pj];
/* 613*/        for (int i6 = i4 + 1; i6 < this.solvedCols; i6++)
/* 614*/          work1[this.permutation[i6]] = work1[this.permutation[i6]] - this.jacobian[i6][pj] * tmp; 
/*   0*/      } 
/* 617*/      sum2 = 0.0D;
/* 618*/      for (int i3 = 0; i3 < this.solvedCols; i3++) {
/* 619*/        double s = work1[this.permutation[i3]];
/* 620*/        sum2 += s * s;
/*   0*/      } 
/* 622*/      double correction = fp / delta * sum2;
/* 625*/      if (fp > 0.0D) {
/* 626*/        parl = Math.max(parl, this.lmPar);
/* 627*/      } else if (fp < 0.0D) {
/* 628*/        paru = Math.min(paru, this.lmPar);
/*   0*/      } 
/* 632*/      this.lmPar = Math.max(parl, this.lmPar + correction);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void determineLMDirection(double[] qy, double[] diag, double[] lmDiag, double[] work) {
/* 662*/    for (int j = 0; j < this.solvedCols; j++) {
/* 663*/      int pj = this.permutation[j];
/* 664*/      for (int n = j + 1; n < this.solvedCols; n++)
/* 665*/        this.jacobian[n][pj] = this.jacobian[j][this.permutation[n]]; 
/* 667*/      this.lmDir[j] = this.diagR[pj];
/* 668*/      work[j] = qy[j];
/*   0*/    } 
/* 672*/    for (int i = 0; i < this.solvedCols; i++) {
/* 676*/      int pj = this.permutation[i];
/* 677*/      double dpj = diag[pj];
/* 678*/      if (dpj != 0.0D)
/* 679*/        Arrays.fill(lmDiag, i + 1, lmDiag.length, 0.0D); 
/* 681*/      lmDiag[i] = dpj;
/* 686*/      double qtbpj = 0.0D;
/* 687*/      for (int n = i; n < this.solvedCols; n++) {
/* 688*/        int pk = this.permutation[n];
/* 692*/        if (lmDiag[n] != 0.0D) {
/*   0*/          double sin, cos;
/* 696*/          double rkk = this.jacobian[n][pk];
/* 697*/          if (Math.abs(rkk) < Math.abs(lmDiag[n])) {
/* 698*/            double cotan = rkk / lmDiag[n];
/* 699*/            sin = 1.0D / Math.sqrt(1.0D + cotan * cotan);
/* 700*/            cos = sin * cotan;
/*   0*/          } else {
/* 702*/            double tan = lmDiag[n] / rkk;
/* 703*/            cos = 1.0D / Math.sqrt(1.0D + tan * tan);
/* 704*/            sin = cos * tan;
/*   0*/          } 
/* 709*/          this.jacobian[n][pk] = cos * rkk + sin * lmDiag[n];
/* 710*/          double temp = cos * work[n] + sin * qtbpj;
/* 711*/          qtbpj = -sin * work[n] + cos * qtbpj;
/* 712*/          work[n] = temp;
/* 715*/          for (int i1 = n + 1; i1 < this.solvedCols; i1++) {
/* 716*/            double rik = this.jacobian[i1][pk];
/* 717*/            double temp2 = cos * rik + sin * lmDiag[i1];
/* 718*/            lmDiag[i1] = -sin * rik + cos * lmDiag[i1];
/* 719*/            this.jacobian[i1][pk] = temp2;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 727*/      lmDiag[i] = this.jacobian[i][this.permutation[i]];
/* 728*/      this.jacobian[i][this.permutation[i]] = this.lmDir[i];
/*   0*/    } 
/* 734*/    int nSing = this.solvedCols;
/* 735*/    for (int m = 0; m < this.solvedCols; m++) {
/* 736*/      if (lmDiag[m] == 0.0D && nSing == this.solvedCols)
/* 737*/        nSing = m; 
/* 739*/      if (nSing < this.solvedCols)
/* 740*/        work[m] = 0.0D; 
/*   0*/    } 
/* 743*/    if (nSing > 0)
/* 744*/      for (int n = nSing - 1; n >= 0; n--) {
/* 745*/        int pj = this.permutation[n];
/* 746*/        double sum = 0.0D;
/* 747*/        for (int i1 = n + 1; i1 < nSing; i1++)
/* 748*/          sum += this.jacobian[i1][pj] * work[i1]; 
/* 750*/        work[n] = (work[n] - sum) / lmDiag[n];
/*   0*/      }  
/* 755*/    for (int k = 0; k < this.lmDir.length; k++)
/* 756*/      this.lmDir[this.permutation[k]] = work[k]; 
/*   0*/  }
/*   0*/  
/*   0*/  private void qrDecomposition() throws OptimizationException {
/* 786*/    for (int k = 0; k < this.cols; k++) {
/* 787*/      this.permutation[k] = k;
/* 788*/      double norm2 = 0.0D;
/* 789*/      for (int j = 0; j < this.jacobian.length; j++) {
/* 790*/        double akk = this.jacobian[j][k];
/* 791*/        norm2 += akk * akk;
/*   0*/      } 
/* 793*/      this.jacNorm[k] = Math.sqrt(norm2);
/*   0*/    } 
/* 797*/    for (int i = 0; i < this.cols; i++) {
/* 800*/      int nextColumn = -1;
/* 801*/      double ak2 = Double.NEGATIVE_INFINITY;
/* 802*/      for (int j = i; j < this.cols; j++) {
/* 803*/        double norm2 = 0.0D;
/* 804*/        for (int m = i; m < this.jacobian.length; m++) {
/* 805*/          double aki = this.jacobian[m][this.permutation[j]];
/* 806*/          norm2 += aki * aki;
/*   0*/        } 
/* 808*/        if (Double.isInfinite(norm2) || Double.isNaN(norm2))
/* 809*/          throw new OptimizationException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, new Object[] { this.rows, this.cols }); 
/* 812*/        if (norm2 > ak2) {
/* 813*/          nextColumn = j;
/* 814*/          ak2 = norm2;
/*   0*/        } 
/*   0*/      } 
/* 817*/      if (ak2 <= this.qrRankingThreshold) {
/* 818*/        this.rank = i;
/*   0*/        return;
/*   0*/      } 
/* 821*/      int pk = this.permutation[nextColumn];
/* 822*/      this.permutation[nextColumn] = this.permutation[i];
/* 823*/      this.permutation[i] = pk;
/* 826*/      double akk = this.jacobian[i][pk];
/* 827*/      double alpha = (akk > 0.0D) ? -Math.sqrt(ak2) : Math.sqrt(ak2);
/* 828*/      double betak = 1.0D / (ak2 - akk * alpha);
/* 829*/      this.beta[pk] = betak;
/* 832*/      this.diagR[pk] = alpha;
/* 833*/      this.jacobian[i][pk] = this.jacobian[i][pk] - alpha;
/* 836*/      for (int dk = this.cols - 1 - i; dk > 0; dk--) {
/* 837*/        double gamma = 0.0D;
/* 838*/        for (int n = i; n < this.jacobian.length; n++)
/* 839*/          gamma += this.jacobian[n][pk] * this.jacobian[n][this.permutation[i + dk]]; 
/* 841*/        gamma *= betak;
/* 842*/        for (int m = i; m < this.jacobian.length; m++)
/* 843*/          this.jacobian[m][this.permutation[i + dk]] = this.jacobian[m][this.permutation[i + dk]] - gamma * this.jacobian[m][pk]; 
/*   0*/      } 
/*   0*/    } 
/* 849*/    this.rank = this.solvedCols;
/*   0*/  }
/*   0*/  
/*   0*/  private void qTy(double[] y) {
/* 859*/    for (int k = 0; k < this.cols; k++) {
/* 860*/      int pk = this.permutation[k];
/* 861*/      double gamma = 0.0D;
/* 862*/      for (int i = k; i < this.rows; i++)
/* 863*/        gamma += this.jacobian[i][pk] * y[i]; 
/* 865*/      gamma *= this.beta[pk];
/* 866*/      for (int j = k; j < this.rows; j++)
/* 867*/        y[j] = y[j] - gamma * this.jacobian[j][pk]; 
/*   0*/    } 
/*   0*/  }
/*   0*/}
