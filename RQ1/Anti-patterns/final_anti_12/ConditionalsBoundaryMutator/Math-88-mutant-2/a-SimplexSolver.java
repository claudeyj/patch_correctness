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
/*  81*/      if (MathUtils.compareTo(tableau.getEntry(i, col), 0.0D, this.epsilon) >= 0) {
/*  82*/        double ratio = rhs / tableau.getEntry(i, col);
/*  83*/        if (ratio < minRatio) {
/*  84*/          minRatio = ratio;
/*  85*/          minRatioPos = i;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*  89*/    return minRatioPos;
/*   0*/  }
/*   0*/  
/*   0*/  protected void doIteration(SimplexTableau tableau) throws OptimizationException {
/* 102*/    incrementIterationsCounter();
/* 104*/    Integer pivotCol = getPivotColumn(tableau);
/* 105*/    Integer pivotRow = getPivotRow(pivotCol, tableau);
/* 106*/    if (pivotRow == null)
/* 107*/      throw new UnboundedSolutionException(); 
/* 111*/    double pivotVal = tableau.getEntry(pivotRow, pivotCol);
/* 112*/    tableau.divideRow(pivotRow, pivotVal);
/* 115*/    for (int i = 0; i < tableau.getHeight(); i++) {
/* 116*/      if (i != pivotRow) {
/* 117*/        double multiplier = tableau.getEntry(i, pivotCol);
/* 118*/        tableau.subtractRow(i, pivotRow, multiplier);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isPhase1Solved(SimplexTableau tableau) {
/* 129*/    if (tableau.getNumArtificialVariables() == 0)
/* 130*/      return true; 
/* 132*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
/* 133*/      if (MathUtils.compareTo(tableau.getEntry(0, i), 0.0D, this.epsilon) < 0)
/* 134*/        return false; 
/*   0*/    } 
/* 137*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptimal(SimplexTableau tableau) {
/* 146*/    if (tableau.getNumArtificialVariables() > 0)
/* 147*/      return false; 
/* 149*/    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
/* 150*/      if (MathUtils.compareTo(tableau.getEntry(0, i), 0.0D, this.epsilon) < 0)
/* 151*/        return false; 
/*   0*/    } 
/* 154*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected void solvePhase1(SimplexTableau tableau) throws OptimizationException {
/* 167*/    if (tableau.getNumArtificialVariables() == 0)
/*   0*/      return; 
/* 171*/    while (!isPhase1Solved(tableau))
/* 172*/      doIteration(tableau); 
/* 176*/    if (!MathUtils.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0D, this.epsilon))
/* 177*/      throw new NoFeasibleSolutionException(); 
/*   0*/  }
/*   0*/  
/*   0*/  public RealPointValuePair doOptimize() throws OptimizationException {
/* 185*/    SimplexTableau tableau = new SimplexTableau(this.f, this.constraints, this.goalType, this.restrictToNonNegative, this.epsilon);
/* 187*/    solvePhase1(tableau);
/* 188*/    tableau.discardArtificialVariables();
/* 189*/    while (!isOptimal(tableau))
/* 190*/      doIteration(tableau); 
/* 192*/    return tableau.getSolution();
/*   0*/  }
/*   0*/}
