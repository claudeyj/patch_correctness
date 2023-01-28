/*   0*/package org.apache.commons.math3.optimization.linear;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math3.exception.MaxCountExceededException;
/*   0*/import org.apache.commons.math3.optimization.PointValuePair;
/*   0*/import org.apache.commons.math3.util.Precision;
/*   0*/
/*   0*/public class SimplexSolver extends AbstractLinearOptimizer {
/*   0*/  private static final double DEFAULT_EPSILON = 1.0E-6D;
/*   0*/  
/*   0*/  private static final int DEFAULT_ULPS = 10;
/*   0*/  
/*   0*/  private final double epsilon;
/*   0*/  
/*   0*/  private final int maxUlps;
/*   0*/  
/*   0*/  public SimplexSolver() {
/*  51*/    this(1.0E-6D, 10);
/*   0*/  }
/*   0*/  
/*   0*/  public SimplexSolver(double epsilon, int maxUlps) {
/*  60*/    this.epsilon = epsilon;
/*  61*/    this.maxUlps = maxUlps;
/*   0*/  }
/*   0*/  
/*   0*/  private Integer getPivotColumn(SimplexTableau tableau) {
/*  70*/    double minValue = 0.0D;
/*  71*/    Integer minPos = null;
/*  72*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
/*  73*/      double entry = tableau.getEntry(0, i);
/*  76*/      if (entry < minValue) {
/*  77*/        minValue = entry;
/*  78*/        minPos = i;
/*   0*/      } 
/*   0*/    } 
/*  81*/    return minPos;
/*   0*/  }
/*   0*/  
/*   0*/  private Integer getPivotRow(SimplexTableau tableau, int col) {
/*  92*/    List<Integer> minRatioPositions = new ArrayList<Integer>();
/*  93*/    double minRatio = Double.MAX_VALUE;
/*  94*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
/*  95*/      double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
/*  96*/      double entry = tableau.getEntry(i, col);
/*  98*/      if (Precision.compareTo(entry, 0.0D, this.maxUlps) > 0) {
/*  99*/        double ratio = rhs / entry;
/* 102*/        int cmp = Double.compare(ratio, minRatio);
/* 103*/        if (this.maxUlps == 0) {
/* 104*/          minRatioPositions.add(i);
/* 105*/        } else if (cmp < 0) {
/* 106*/          minRatio = ratio;
/* 107*/          minRatioPositions = new ArrayList<Integer>();
/* 108*/          minRatioPositions.add(i);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 113*/    if (minRatioPositions.size() == 0)
/* 114*/      return null; 
/* 115*/    if (minRatioPositions.size() > 1) {
/* 119*/      for (Integer row : minRatioPositions) {
/* 120*/        for (int j = 0; j < tableau.getNumArtificialVariables(); j++) {
/* 121*/          int column = j + tableau.getArtificialVariableOffset();
/* 122*/          double entry = tableau.getEntry(row, column);
/* 123*/          if (Precision.equals(entry, 1.0D, this.maxUlps) && row.equals(tableau.getBasicRow(column)))
/* 124*/            return row; 
/*   0*/        } 
/*   0*/      } 
/* 138*/      Integer minRow = null;
/* 139*/      int minIndex = tableau.getWidth();
/* 140*/      for (Integer row : minRatioPositions) {
/* 141*/        int j = tableau.getNumObjectiveFunctions();
/* 142*/        for (; j < tableau.getWidth() - 1 && minRow != row; j++) {
/* 143*/          if (row == tableau.getBasicRow(j) && 
/* 144*/            j < minIndex) {
/* 145*/            minIndex = j;
/* 146*/            minRow = row;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 151*/      return minRow;
/*   0*/    } 
/* 153*/    return minRatioPositions.get(0);
/*   0*/  }
/*   0*/  
/*   0*/  protected void doIteration(SimplexTableau tableau) throws MaxCountExceededException, UnboundedSolutionException {
/* 165*/    incrementIterationsCounter();
/* 167*/    Integer pivotCol = getPivotColumn(tableau);
/* 168*/    Integer pivotRow = getPivotRow(tableau, pivotCol);
/* 169*/    if (pivotRow == null)
/* 170*/      throw new UnboundedSolutionException(); 
/* 174*/    double pivotVal = tableau.getEntry(pivotRow, pivotCol);
/* 175*/    tableau.divideRow(pivotRow, pivotVal);
/* 178*/    for (int i = 0; i < tableau.getHeight(); i++) {
/* 179*/      if (i != pivotRow) {
/* 180*/        double multiplier = tableau.getEntry(i, pivotCol);
/* 181*/        tableau.subtractRow(i, pivotRow, multiplier);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void solvePhase1(SimplexTableau tableau) throws MaxCountExceededException, UnboundedSolutionException, NoFeasibleSolutionException {
/* 197*/    if (tableau.getNumArtificialVariables() == 0)
/*   0*/      return; 
/* 201*/    while (!tableau.isOptimal())
/* 202*/      doIteration(tableau); 
/* 206*/    if (!Precision.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0D, this.epsilon))
/* 207*/      throw new NoFeasibleSolutionException(); 
/*   0*/  }
/*   0*/  
/*   0*/  public PointValuePair doOptimize() throws MaxCountExceededException, UnboundedSolutionException, NoFeasibleSolutionException {
/* 215*/    SimplexTableau tableau = new SimplexTableau(getFunction(), getConstraints(), getGoalType(), restrictToNonNegative(), this.epsilon, this.maxUlps);
/* 223*/    solvePhase1(tableau);
/* 224*/    tableau.dropPhase1Objective();
/* 226*/    while (!tableau.isOptimal())
/* 227*/      doIteration(tableau); 
/* 229*/    return tableau.getSolution();
/*   0*/  }
/*   0*/}
