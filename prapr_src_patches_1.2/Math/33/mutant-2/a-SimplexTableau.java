/*   0*/package org.apache.commons.math3.optimization.linear;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Set;
/*   0*/import org.apache.commons.math3.linear.Array2DRowRealMatrix;
/*   0*/import org.apache.commons.math3.linear.MatrixUtils;
/*   0*/import org.apache.commons.math3.linear.RealMatrix;
/*   0*/import org.apache.commons.math3.linear.RealVector;
/*   0*/import org.apache.commons.math3.optimization.GoalType;
/*   0*/import org.apache.commons.math3.optimization.PointValuePair;
/*   0*/import org.apache.commons.math3.util.Precision;
/*   0*/
/*   0*/class SimplexTableau implements Serializable {
/*   0*/  private static final String NEGATIVE_VAR_COLUMN_LABEL = "x-";
/*   0*/  
/*   0*/  private static final int DEFAULT_ULPS = 10;
/*   0*/  
/*   0*/  private static final long serialVersionUID = -1369660067587938365L;
/*   0*/  
/*   0*/  private final LinearObjectiveFunction f;
/*   0*/  
/*   0*/  private final List<LinearConstraint> constraints;
/*   0*/  
/*   0*/  private final boolean restrictToNonNegative;
/*   0*/  
/*  84*/  private final List<String> columnLabels = new ArrayList<String>();
/*   0*/  
/*   0*/  private transient RealMatrix tableau;
/*   0*/  
/*   0*/  private final int numDecisionVariables;
/*   0*/  
/*   0*/  private final int numSlackVariables;
/*   0*/  
/*   0*/  private int numArtificialVariables;
/*   0*/  
/*   0*/  private final double epsilon;
/*   0*/  
/*   0*/  private final int maxUlps;
/*   0*/  
/*   0*/  SimplexTableau(LinearObjectiveFunction f, Collection<LinearConstraint> constraints, GoalType goalType, boolean restrictToNonNegative, double epsilon) {
/* 117*/    this(f, constraints, goalType, restrictToNonNegative, epsilon, 10);
/*   0*/  }
/*   0*/  
/*   0*/  SimplexTableau(LinearObjectiveFunction f, Collection<LinearConstraint> constraints, GoalType goalType, boolean restrictToNonNegative, double epsilon, int maxUlps) {
/* 135*/    this.f = f;
/* 136*/    this.constraints = normalizeConstraints(constraints);
/* 137*/    this.restrictToNonNegative = restrictToNonNegative;
/* 138*/    this.epsilon = epsilon;
/* 139*/    this.maxUlps = maxUlps;
/* 140*/    this.numDecisionVariables = f.getCoefficients().getDimension() + (restrictToNonNegative ? 0 : 1);
/* 142*/    this.numSlackVariables = getConstraintTypeCounts(Relationship.LEQ) + getConstraintTypeCounts(Relationship.GEQ);
/* 144*/    this.numArtificialVariables = getConstraintTypeCounts(Relationship.EQ) + getConstraintTypeCounts(Relationship.GEQ);
/* 146*/    this.tableau = createTableau((goalType == GoalType.MAXIMIZE));
/* 147*/    initializeColumnLabels();
/*   0*/  }
/*   0*/  
/*   0*/  protected void initializeColumnLabels() {
/* 154*/    if (getNumObjectiveFunctions() == 2)
/* 155*/      this.columnLabels.add("W"); 
/* 157*/    this.columnLabels.add("Z");
/* 158*/    for (int i = 0; i < getOriginalNumDecisionVariables(); i++)
/* 159*/      this.columnLabels.add("x" + i); 
/* 161*/    if (!this.restrictToNonNegative)
/* 162*/      this.columnLabels.add("x-"); 
/* 164*/    for (int k = 0; k < getNumSlackVariables(); k++)
/* 165*/      this.columnLabels.add("s" + k); 
/* 167*/    for (int j = 0; j < getNumArtificialVariables(); j++)
/* 168*/      this.columnLabels.add("a" + j); 
/* 170*/    this.columnLabels.add("RHS");
/*   0*/  }
/*   0*/  
/*   0*/  protected RealMatrix createTableau(boolean maximize) {
/* 181*/    int width = this.numDecisionVariables + this.numSlackVariables + this.numArtificialVariables + getNumObjectiveFunctions() + 1;
/* 183*/    int height = this.constraints.size() + getNumObjectiveFunctions();
/* 184*/    Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(height, width);
/* 187*/    if (getNumObjectiveFunctions() == 2)
/* 188*/      matrix.setEntry(0, 0, -1.0D); 
/* 190*/    int zIndex = (getNumObjectiveFunctions() == 1) ? 0 : 1;
/* 191*/    matrix.setEntry(zIndex, zIndex, maximize ? 1.0D : -1.0D);
/* 192*/    RealVector objectiveCoefficients = maximize ? this.f.getCoefficients().mapMultiply(-1.0D) : this.f.getCoefficients();
/* 194*/    copyArray(objectiveCoefficients.toArray(), matrix.getDataRef()[zIndex]);
/* 195*/    matrix.setEntry(zIndex, width - 1, maximize ? this.f.getConstantTerm() : (-1.0D * this.f.getConstantTerm()));
/* 198*/    if (!this.restrictToNonNegative)
/* 199*/      matrix.setEntry(zIndex, getSlackVariableOffset() - 1, getInvertedCoefficientSum(objectiveCoefficients)); 
/* 204*/    int slackVar = 0;
/* 205*/    int artificialVar = 0;
/* 206*/    for (int i = 0; i < this.constraints.size(); i++) {
/* 207*/      LinearConstraint constraint = this.constraints.get(i);
/* 208*/      int row = getNumObjectiveFunctions() + i;
/* 211*/      copyArray(constraint.getCoefficients().toArray(), matrix.getDataRef()[row]);
/* 214*/      if (!this.restrictToNonNegative)
/* 215*/        matrix.setEntry(row, getSlackVariableOffset() - 1, getInvertedCoefficientSum(constraint.getCoefficients())); 
/* 220*/      matrix.setEntry(row, width - 1, constraint.getValue());
/* 223*/      if (constraint.getRelationship() == Relationship.LEQ) {
/* 224*/        matrix.setEntry(row, getSlackVariableOffset() + slackVar++, 1.0D);
/* 225*/      } else if (constraint.getRelationship() == Relationship.GEQ) {
/* 226*/        matrix.setEntry(row, getSlackVariableOffset() + slackVar++, -1.0D);
/*   0*/      } 
/* 230*/      if (constraint.getRelationship() == Relationship.EQ || constraint.getRelationship() == Relationship.GEQ) {
/* 232*/        matrix.setEntry(0, getArtificialVariableOffset() + artificialVar, 1.0D);
/* 233*/        matrix.setEntry(row, getArtificialVariableOffset() + artificialVar++, 1.0D);
/* 234*/        matrix.setRowVector(0, matrix.getRowVector(0).subtract(matrix.getRowVector(row)));
/*   0*/      } 
/*   0*/    } 
/* 238*/    return matrix;
/*   0*/  }
/*   0*/  
/*   0*/  public List<LinearConstraint> normalizeConstraints(Collection<LinearConstraint> originalConstraints) {
/* 247*/    List<LinearConstraint> normalized = new ArrayList<LinearConstraint>();
/* 248*/    for (LinearConstraint constraint : originalConstraints)
/* 249*/      normalized.add(normalize(constraint)); 
/* 251*/    return normalized;
/*   0*/  }
/*   0*/  
/*   0*/  private LinearConstraint normalize(LinearConstraint constraint) {
/* 260*/    if (constraint.getValue() < 0.0D)
/* 261*/      return new LinearConstraint(constraint.getCoefficients().mapMultiply(-1.0D), constraint.getRelationship().oppositeRelationship(), -1.0D * constraint.getValue()); 
/* 265*/    return new LinearConstraint(constraint.getCoefficients(), constraint.getRelationship(), constraint.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumObjectiveFunctions() {
/* 274*/    return (this.numArtificialVariables > 0) ? 2 : 1;
/*   0*/  }
/*   0*/  
/*   0*/  private int getConstraintTypeCounts(Relationship relationship) {
/* 283*/    int count = 0;
/* 284*/    for (LinearConstraint constraint : this.constraints) {
/* 285*/      if (constraint.getRelationship() == relationship)
/* 286*/        count++; 
/*   0*/    } 
/* 289*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  protected static double getInvertedCoefficientSum(RealVector coefficients) {
/* 298*/    double sum = 0.0D;
/* 299*/    for (double coefficient : coefficients.toArray())
/* 300*/      sum -= coefficient; 
/* 302*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  protected Integer getBasicRow(int col) {
/* 311*/    Integer row = null;
/* 312*/    for (int i = 0; i < getHeight(); i++) {
/* 313*/      double entry = getEntry(i, col);
/* 314*/      if (Precision.equals(entry, 1.0D, this.maxUlps) && row == null) {
/* 315*/        row = i;
/* 316*/      } else if (!Precision.equals(entry, 0.0D, this.maxUlps)) {
/* 317*/        return null;
/*   0*/      } 
/*   0*/    } 
/* 320*/    return row;
/*   0*/  }
/*   0*/  
/*   0*/  protected void dropPhase1Objective() {
/* 328*/    if (getNumObjectiveFunctions() == 1)
/*   0*/      return; 
/* 332*/    List<Integer> columnsToDrop = new ArrayList<Integer>();
/* 333*/    columnsToDrop.add(0);
/* 336*/    for (int i = getNumObjectiveFunctions(); i < getArtificialVariableOffset(); i++) {
/* 337*/      double entry = this.tableau.getEntry(0, i);
/* 338*/      if (Precision.compareTo(entry, 0.0D, this.maxUlps) > 0)
/* 339*/        columnsToDrop.add(i); 
/*   0*/    } 
/* 344*/    for (int j = 0; j < getNumArtificialVariables(); j++) {
/* 345*/      int col = j + getArtificialVariableOffset();
/* 346*/      if (getBasicRow(col) == null)
/* 347*/        columnsToDrop.add(col); 
/*   0*/    } 
/* 351*/    double[][] matrix = new double[getHeight() - 1][getWidth() - columnsToDrop.size()];
/* 352*/    for (int m = 1; m < getHeight(); m++) {
/* 353*/      int col = 0;
/* 354*/      for (int n = 0; n < getWidth(); n++) {
/* 355*/        if (!columnsToDrop.contains(n))
/* 356*/          matrix[m - 1][col++] = this.tableau.getEntry(m, n); 
/*   0*/      } 
/*   0*/    } 
/* 361*/    for (int k = columnsToDrop.size() - 1; k >= 0; k--)
/* 362*/      this.columnLabels.remove((Integer)columnsToDrop.get(k)); 
/* 365*/    this.tableau = new Array2DRowRealMatrix(matrix);
/* 366*/    this.numArtificialVariables = 0;
/*   0*/  }
/*   0*/  
/*   0*/  private void copyArray(double[] src, double[] dest) {
/* 374*/    System.arraycopy(src, 0, dest, getNumObjectiveFunctions(), src.length);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isOptimal() {
/* 382*/    for (int i = getNumObjectiveFunctions(); i < getWidth() - 1; i++) {
/* 383*/      double entry = this.tableau.getEntry(0, i);
/* 384*/      if (Precision.compareTo(entry, 0.0D, this.epsilon) < 0)
/* 385*/        return false; 
/*   0*/    } 
/* 388*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected PointValuePair getSolution() {
/* 397*/    int negativeVarColumn = this.columnLabels.indexOf("x-");
/* 398*/    Integer negativeVarBasicRow = (negativeVarColumn > 0) ? getBasicRow(negativeVarColumn) : null;
/* 399*/    double mostNegative = (negativeVarBasicRow == null) ? 0.0D : getEntry(negativeVarBasicRow, getRhsOffset());
/* 401*/    Set<Integer> basicRows = new HashSet<Integer>();
/* 402*/    double[] coefficients = new double[getOriginalNumDecisionVariables()];
/* 403*/    for (int i = 0; i < coefficients.length; i++) {
/* 404*/      int colIndex = this.columnLabels.indexOf("x" + i);
/* 405*/      if (colIndex < 0) {
/* 406*/        coefficients[i] = 0.0D;
/*   0*/      } else {
/* 409*/        Integer basicRow = getBasicRow(colIndex);
/* 410*/        if (basicRow != null && basicRow == 0) {
/* 414*/          coefficients[i] = 0.0D;
/* 415*/        } else if (basicRows.contains(basicRow)) {
/* 418*/          coefficients[i] = 0.0D - (this.restrictToNonNegative ? 0.0D : mostNegative);
/*   0*/        } else {
/* 420*/          basicRows.add(basicRow);
/* 421*/          coefficients[i] = ((basicRow == null) ? 0.0D : getEntry(basicRow, getRhsOffset())) - (this.restrictToNonNegative ? 0.0D : mostNegative);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 426*/    return new PointValuePair(coefficients, this.f.getValue(coefficients));
/*   0*/  }
/*   0*/  
/*   0*/  protected void divideRow(int dividendRow, double divisor) {
/* 439*/    for (int j = 0; j < getWidth(); j++)
/* 440*/      this.tableau.setEntry(dividendRow, j, this.tableau.getEntry(dividendRow, j) / divisor); 
/*   0*/  }
/*   0*/  
/*   0*/  protected void subtractRow(int minuendRow, int subtrahendRow, double multiple) {
/* 456*/    this.tableau.setRowVector(minuendRow, this.tableau.getRowVector(minuendRow).subtract(this.tableau.getRowVector(subtrahendRow).mapMultiply(multiple)));
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getWidth() {
/* 465*/    return this.tableau.getColumnDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getHeight() {
/* 473*/    return this.tableau.getRowDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final double getEntry(int row, int column) {
/* 482*/    return this.tableau.getEntry(row, column);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void setEntry(int row, int column, double value) {
/* 492*/    this.tableau.setEntry(row, column, value);
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getSlackVariableOffset() {
/* 500*/    return getNumObjectiveFunctions() + this.numDecisionVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getArtificialVariableOffset() {
/* 508*/    return getNumObjectiveFunctions() + this.numDecisionVariables + this.numSlackVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getRhsOffset() {
/* 516*/    return getWidth() - 1;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumDecisionVariables() {
/* 530*/    return this.numDecisionVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getOriginalNumDecisionVariables() {
/* 539*/    return this.f.getCoefficients().getDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumSlackVariables() {
/* 547*/    return this.numSlackVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumArtificialVariables() {
/* 555*/    return this.numArtificialVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final double[][] getData() {
/* 563*/    return this.tableau.getData();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object other) {
/* 570*/    if (this == other)
/* 571*/      return true; 
/* 574*/    if (other instanceof SimplexTableau) {
/* 575*/      SimplexTableau rhs = (SimplexTableau)other;
/* 576*/      return (this.restrictToNonNegative == rhs.restrictToNonNegative && this.numDecisionVariables == rhs.numDecisionVariables && this.numSlackVariables == rhs.numSlackVariables && this.numArtificialVariables == rhs.numArtificialVariables && this.epsilon == rhs.epsilon && this.maxUlps == rhs.maxUlps && this.f.equals(rhs.f) && this.constraints.equals(rhs.constraints) && this.tableau.equals(rhs.tableau));
/*   0*/    } 
/* 586*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 592*/    return this.restrictToNonNegative.hashCode() ^ this.numDecisionVariables ^ this.numSlackVariables ^ this.numArtificialVariables ^ this.epsilon.hashCode() ^ this.maxUlps ^ this.f.hashCode() ^ this.constraints.hashCode() ^ this.tableau.hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream oos) throws IOException {
/* 609*/    oos.defaultWriteObject();
/* 610*/    MatrixUtils.serializeRealMatrix(this.tableau, oos);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
/* 620*/    ois.defaultReadObject();
/* 621*/    MatrixUtils.deserializeRealMatrix(this, "tableau", ois);
/*   0*/  }
/*   0*/}
