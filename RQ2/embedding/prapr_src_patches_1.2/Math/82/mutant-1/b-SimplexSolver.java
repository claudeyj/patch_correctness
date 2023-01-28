/*   0*/package org.apache.commons.math.optimization.linear;
/*   0*/
/*   0*/import org.apache.commons.math.optimization.OptimizationException;
/*   0*/import org.apache.commons.math.optimization.RealPointValuePair;
/*   0*/import org.apache.commons.math.util.MathUtils;
/*   0*/
/*   0*/public class SimplexSolver extends AbstractLinearOptimizer {
/*   0*/  private static final double DEFAULT_EPSILON = 1.0E-6D;
/*   0*/  
/*   0*/  protected final double epsilon;
/*   0*/  
/*   0*/  public SimplexSolver() {
/*  42*/    this(1.0E-6D);
/*   0*/  }
/*   0*/  
/*   0*/  public SimplexSolver(double epsilon) {
/*  50*/    this.epsilon = epsilon;
/*   0*/  }
/*   0*/  
/*   0*/  private Integer getPivotColumn(SimplexTableau tableau) {
/*  59*/    double minValue = 0.0D;
/*  60*/    Integer minPos = null;
/*  61*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
/*  62*/      if (MathUtils.compareTo(tableau.getEntry(0, i), minValue, this.epsilon) < 0) {
/*  63*/        minValue = tableau.getEntry(0, i);
/*  64*/        minPos = i;
/*   0*/      } 
/*   0*/    } 
/*  67*/    return minPos;
/*   0*/  }
/*   0*/  
/*   0*/  private Integer getPivotRow(int col, SimplexTableau tableau) {
/*  77*/    double minRatio = Double.MAX_VALUE;
/*  78*/    Integer minRatioPos = null;
/*  79*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
/*  80*/      double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
/*  81*/      double entry = tableau.getEntry(i, col);
/*  82*/      this;
/*  82*/      if (MathUtils.compareTo(entry, 0.0D, entry) >= 0) {
/*  83*/        double ratio = rhs / entry;
/*  84*/        if (ratio < minRatio) {
/*  85*/          minRatio = ratio;
/*  86*/          minRatioPos = i;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*  90*/    return minRatioPos;
/*   0*/  }
/*   0*/  
/*   0*/  protected void doIteration(SimplexTableau tableau) throws OptimizationException {
/* 103*/    incrementIterationsCounter();
/* 105*/    Integer pivotCol = getPivotColumn(tableau);
/* 106*/    Integer pivotRow = getPivotRow(pivotCol, tableau);
/* 107*/    if (pivotRow == null)
/* 108*/      throw new UnboundedSolutionException(); 
/* 112*/    double pivotVal = tableau.getEntry(pivotRow, pivotCol);
/* 113*/    tableau.divideRow(pivotRow, pivotVal);
/* 116*/    for (int i = 0; i < tableau.getHeight(); i++) {
/* 117*/      if (i != pivotRow) {
/* 118*/        double multiplier = tableau.getEntry(i, pivotCol);
/* 119*/        tableau.subtractRow(i, pivotRow, multiplier);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isPhase1Solved(SimplexTableau tableau) {
/* 130*/    if (tableau.getNumArtificialVariables() == 0)
/* 131*/      return true; 
/* 133*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
/* 134*/      if (MathUtils.compareTo(tableau.getEntry(0, i), 0.0D, this.epsilon) < 0)
/* 135*/        return false; 
/*   0*/    } 
/* 138*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptimal(SimplexTableau tableau) {
/* 147*/    if (tableau.getNumArtificialVariables() > 0)
/* 148*/      return false; 
/* 150*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
/* 151*/      if (MathUtils.compareTo(tableau.getEntry(0, i), 0.0D, this.epsilon) < 0)
/* 152*/        return false; 
/*   0*/    } 
/* 155*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected void solvePhase1(SimplexTableau tableau) throws OptimizationException {
/* 168*/    if (tableau.getNumArtificialVariables() == 0)
/*   0*/      return; 
/* 172*/    while (!isPhase1Solved(tableau))
/* 173*/      doIteration(tableau); 
/* 177*/    if (!MathUtils.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0D, this.epsilon))
/* 178*/      throw new NoFeasibleSolutionException(); 
/*   0*/  }
/*   0*/  
/*   0*/  public RealPointValuePair doOptimize() throws OptimizationException {
/* 186*/    SimplexTableau tableau = new SimplexTableau(this.f, this.constraints, this.goalType, this.restrictToNonNegative, this.epsilon);
/* 188*/    solvePhase1(tableau);
/* 189*/    tableau.discardArtificialVariables();
/* 190*/    while (!isOptimal(tableau))
/* 191*/      doIteration(tableau); 
/* 193*/    return tableau.getSolution();
/*   0*/  }
/*   0*/}
