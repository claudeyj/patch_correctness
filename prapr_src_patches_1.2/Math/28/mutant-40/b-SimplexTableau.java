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
/*   0*/import java.util.TreeSet;
/*   0*/import org.apache.commons.math3.linear.Array2DRowRealMatrix;
/*   0*/import org.apache.commons.math3.linear.MatrixUtils;
/*   0*/import org.apache.commons.math3.linear.RealMatrix;
/*   0*/import org.apache.commons.math3.linear.RealVector;
/*   0*/import org.apache.commons.math3.optimization.GoalType;
/*   0*/import org.apache.commons.math3.optimization.PointValuePair;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/import org.apache.commons.math3.util.Precision;
/*   0*/
/*   0*/class SimplexTableau implements Serializable {
/*   0*/  private static final String NEGATIVE_VAR_COLUMN_LABEL = "x-";
/*   0*/  
/*   0*/  private static final int DEFAULT_ULPS = 10;
/*   0*/  
/*   0*/  private static final double CUTOFF_THRESHOLD = 1.0E-12D;
/*   0*/  
/*   0*/  private static final long serialVersionUID = -1369660067587938365L;
/*   0*/  
/*   0*/  private final LinearObjectiveFunction f;
/*   0*/  
/*   0*/  private final List<LinearConstraint> constraints;
/*   0*/  
/*   0*/  private final boolean restrictToNonNegative;
/*   0*/  
/*  89*/  private final List<String> columnLabels = new ArrayList<String>();
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
/* 122*/    this(f, constraints, goalType, restrictToNonNegative, epsilon, 10);
/*   0*/  }
/*   0*/  
/*   0*/  SimplexTableau(LinearObjectiveFunction f, Collection<LinearConstraint> constraints, GoalType goalType, boolean restrictToNonNegative, double epsilon, int maxUlps) {
/* 140*/    this.f = f;
/* 141*/    this.constraints = normalizeConstraints(constraints);
/* 142*/    this.restrictToNonNegative = restrictToNonNegative;
/* 143*/    this.epsilon = epsilon;
/* 144*/    this.maxUlps = maxUlps;
/* 145*/    this.numDecisionVariables = f.getCoefficients().getDimension() + (restrictToNonNegative ? 0 : 1);
/* 147*/    this.numSlackVariables = getConstraintTypeCounts(Relationship.LEQ) + getConstraintTypeCounts(Relationship.GEQ);
/* 149*/    this.numArtificialVariables = getConstraintTypeCounts(Relationship.EQ) + getConstraintTypeCounts(Relationship.GEQ);
/* 151*/    this.tableau = createTableau((goalType == GoalType.MAXIMIZE));
/* 152*/    initializeColumnLabels();
/*   0*/  }
/*   0*/  
/*   0*/  protected void initializeColumnLabels() {
/* 159*/    if (getNumObjectiveFunctions() == 2)
/* 160*/      this.columnLabels.add("W"); 
/* 162*/    this.columnLabels.add("Z");
/* 163*/    for (int i = 0; i < getOriginalNumDecisionVariables(); i++)
/* 164*/      this.columnLabels.add("x" + i); 
/* 166*/    if (!this.restrictToNonNegative)
/* 167*/      this.columnLabels.add("x-"); 
/* 169*/    for (int k = 0; k < getNumSlackVariables(); k++)
/* 170*/      this.columnLabels.add("s" + k); 
/* 172*/    for (int j = 0; j < getNumArtificialVariables(); j++)
/* 173*/      this.columnLabels.add("a" + j); 
/* 175*/    this.columnLabels.add("RHS");
/*   0*/  }
/*   0*/  
/*   0*/  protected RealMatrix createTableau(boolean maximize) {
/* 186*/    int width = this.numDecisionVariables + this.numSlackVariables + this.numArtificialVariables + getNumObjectiveFunctions() + 1;
/* 188*/    int height = this.constraints.size() + getNumObjectiveFunctions();
/* 189*/    Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(height, width);
/* 192*/    if (getNumObjectiveFunctions() == 2)
/* 193*/      matrix.setEntry(0, 0, -1.0D); 
/* 195*/    int zIndex = (getNumObjectiveFunctions() == 1) ? 0 : 1;
/* 196*/    matrix.setEntry(zIndex, zIndex, maximize ? 1.0D : -1.0D);
/* 197*/    RealVector objectiveCoefficients = maximize ? this.f.getCoefficients().mapMultiply(-1.0D) : this.f.getCoefficients();
/* 199*/    copyArray(objectiveCoefficients.toArray(), matrix.getDataRef()[zIndex]);
/* 200*/    matrix.setEntry(zIndex, width - 1, maximize ? this.f.getConstantTerm() : (-1.0D * this.f.getConstantTerm()));
/* 203*/    if (!this.restrictToNonNegative)
/* 204*/      matrix.setEntry(zIndex, getSlackVariableOffset() - 1, getInvertedCoefficientSum(objectiveCoefficients)); 
/* 209*/    int slackVar = 0;
/* 210*/    int artificialVar = 0;
/* 211*/    for (int i = 0; i < this.constraints.size(); i++) {
/* 212*/      LinearConstraint constraint = this.constraints.get(i);
/* 213*/      int row = getNumObjectiveFunctions() + i;
/* 216*/      copyArray(constraint.getCoefficients().toArray(), matrix.getDataRef()[row]);
/* 219*/      if (!this.restrictToNonNegative)
/* 220*/        matrix.setEntry(row, getSlackVariableOffset() - 1, getInvertedCoefficientSum(constraint.getCoefficients())); 
/* 225*/      matrix.setEntry(row, width - 1, constraint.getValue());
/* 228*/      if (constraint.getRelationship() == Relationship.LEQ) {
/* 229*/        matrix.setEntry(row, getSlackVariableOffset() + slackVar++, 1.0D);
/* 230*/      } else if (constraint.getRelationship() == Relationship.GEQ) {
/* 231*/        matrix.setEntry(row, getSlackVariableOffset() + slackVar++, -1.0D);
/*   0*/      } 
/* 235*/      if (constraint.getRelationship() == Relationship.EQ || constraint.getRelationship() == Relationship.GEQ) {
/* 237*/        matrix.setEntry(0, getArtificialVariableOffset() + artificialVar, 1.0D);
/* 238*/        matrix.setEntry(row, getArtificialVariableOffset() + artificialVar++, 1.0D);
/* 239*/        matrix.setRowVector(0, matrix.getRowVector(0).subtract(matrix.getRowVector(row)));
/*   0*/      } 
/*   0*/    } 
/* 243*/    return matrix;
/*   0*/  }
/*   0*/  
/*   0*/  public List<LinearConstraint> normalizeConstraints(Collection<LinearConstraint> originalConstraints) {
/* 252*/    List<LinearConstraint> normalized = new ArrayList<LinearConstraint>();
/* 253*/    for (LinearConstraint constraint : originalConstraints)
/* 254*/      normalized.add(normalize(constraint)); 
/* 256*/    return normalized;
/*   0*/  }
/*   0*/  
/*   0*/  private LinearConstraint normalize(LinearConstraint constraint) {
/* 265*/    if (constraint.getValue() < 1.0D)
/* 266*/      return new LinearConstraint(constraint.getCoefficients().mapMultiply(-1.0D), constraint.getRelationship().oppositeRelationship(), -1.0D * constraint.getValue()); 
/* 270*/    return new LinearConstraint(constraint.getCoefficients(), constraint.getRelationship(), constraint.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumObjectiveFunctions() {
/* 279*/    return (this.numArtificialVariables > 0) ? 2 : 1;
/*   0*/  }
/*   0*/  
/*   0*/  private int getConstraintTypeCounts(Relationship relationship) {
/* 288*/    int count = 0;
/* 289*/    for (LinearConstraint constraint : this.constraints) {
/* 290*/      if (constraint.getRelationship() == relationship)
/* 291*/        count++; 
/*   0*/    } 
/* 294*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  protected static double getInvertedCoefficientSum(RealVector coefficients) {
/* 303*/    double sum = 0.0D;
/* 304*/    for (double coefficient : coefficients.toArray())
/* 305*/      sum -= coefficient; 
/* 307*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  protected Integer getBasicRow(int col) {
/* 316*/    Integer row = null;
/* 317*/    for (int i = 0; i < getHeight(); i++) {
/* 318*/      double entry = getEntry(i, col);
/* 319*/      if (Precision.equals(entry, 1.0D, this.maxUlps) && row == null) {
/* 320*/        row = i;
/* 321*/      } else if (!Precision.equals(entry, 0.0D, this.maxUlps)) {
/* 322*/        return null;
/*   0*/      } 
/*   0*/    } 
/* 325*/    return row;
/*   0*/  }
/*   0*/  
/*   0*/  protected void dropPhase1Objective() {
/* 333*/    if (getNumObjectiveFunctions() == 1)
/*   0*/      return; 
/* 337*/    Set<Integer> columnsToDrop = new TreeSet<Integer>();
/* 338*/    columnsToDrop.add(0);
/* 341*/    for (int i = getNumObjectiveFunctions(); i < getArtificialVariableOffset(); i++) {
/* 342*/      double entry = this.tableau.getEntry(0, i);
/* 343*/      if (Precision.compareTo(entry, 0.0D, this.epsilon) > 0)
/* 344*/        columnsToDrop.add(i); 
/*   0*/    } 
/* 349*/    for (int j = 0; j < getNumArtificialVariables(); j++) {
/* 350*/      int col = j + getArtificialVariableOffset();
/* 351*/      if (getBasicRow(col) == null)
/* 352*/        columnsToDrop.add(col); 
/*   0*/    } 
/* 356*/    double[][] matrix = new double[getHeight() - 1][getWidth() - columnsToDrop.size()];
/* 357*/    for (int k = 1; k < getHeight(); k++) {
/* 358*/      int col = 0;
/* 359*/      for (int n = 0; n < getWidth(); n++) {
/* 360*/        if (!columnsToDrop.contains(n))
/* 361*/          matrix[k - 1][col++] = this.tableau.getEntry(k, n); 
/*   0*/      } 
/*   0*/    } 
/* 367*/    Integer[] drop = columnsToDrop.<Integer>toArray(new Integer[columnsToDrop.size()]);
/* 368*/    for (int m = drop.length - 1; m >= 0; m--)
/* 369*/      this.columnLabels.remove(drop[m]); 
/* 372*/    this.tableau = new Array2DRowRealMatrix(matrix);
/* 373*/    this.numArtificialVariables = 0;
/*   0*/  }
/*   0*/  
/*   0*/  private void copyArray(double[] src, double[] dest) {
/* 381*/    System.arraycopy(src, 0, dest, getNumObjectiveFunctions(), src.length);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isOptimal() {
/* 389*/    for (int i = getNumObjectiveFunctions(); i < getWidth() - 1; i++) {
/* 390*/      double entry = this.tableau.getEntry(0, i);
/* 391*/      if (Precision.compareTo(entry, 0.0D, this.epsilon) < 0)
/* 392*/        return false; 
/*   0*/    } 
/* 395*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected PointValuePair getSolution() {
/* 404*/    int negativeVarColumn = this.columnLabels.indexOf("x-");
/* 405*/    Integer negativeVarBasicRow = (negativeVarColumn > 0) ? getBasicRow(negativeVarColumn) : null;
/* 406*/    double mostNegative = (negativeVarBasicRow == null) ? 0.0D : getEntry(negativeVarBasicRow, getRhsOffset());
/* 408*/    Set<Integer> basicRows = new HashSet<Integer>();
/* 409*/    double[] coefficients = new double[getOriginalNumDecisionVariables()];
/* 410*/    for (int i = 0; i < coefficients.length; i++) {
/* 411*/      int colIndex = this.columnLabels.indexOf("x" + i);
/* 412*/      if (colIndex < 0) {
/* 413*/        coefficients[i] = 0.0D;
/*   0*/      } else {
/* 416*/        Integer basicRow = getBasicRow(colIndex);
/* 417*/        if (basicRow != null && basicRow == 0) {
/* 421*/          coefficients[i] = 0.0D;
/* 422*/        } else if (basicRows.contains(basicRow)) {
/* 425*/          coefficients[i] = 0.0D - (this.restrictToNonNegative ? 0.0D : mostNegative);
/*   0*/        } else {
/* 427*/          basicRows.add(basicRow);
/* 428*/          coefficients[i] = ((basicRow == null) ? 0.0D : getEntry(basicRow, getRhsOffset())) - (this.restrictToNonNegative ? 0.0D : mostNegative);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 433*/    return new PointValuePair(coefficients, this.f.getValue(coefficients));
/*   0*/  }
/*   0*/  
/*   0*/  protected void divideRow(int dividendRow, double divisor) {
/* 446*/    for (int j = 0; j < getWidth(); j++)
/* 447*/      this.tableau.setEntry(dividendRow, j, this.tableau.getEntry(dividendRow, j) / divisor); 
/*   0*/  }
/*   0*/  
/*   0*/  protected void subtractRow(int minuendRow, int subtrahendRow, double multiple) {
/* 463*/    for (int i = 0; i < getWidth(); i++) {
/* 464*/      double result = this.tableau.getEntry(minuendRow, i) - this.tableau.getEntry(subtrahendRow, i) * multiple;
/* 466*/      if (FastMath.abs(result) < 1.0E-12D)
/* 467*/        result = 0.0D; 
/* 469*/      this.tableau.setEntry(minuendRow, i, result);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getWidth() {
/* 478*/    return this.tableau.getColumnDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getHeight() {
/* 486*/    return this.tableau.getRowDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final double getEntry(int row, int column) {
/* 495*/    return this.tableau.getEntry(row, column);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void setEntry(int row, int column, double value) {
/* 505*/    this.tableau.setEntry(row, column, value);
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getSlackVariableOffset() {
/* 513*/    return getNumObjectiveFunctions() + this.numDecisionVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getArtificialVariableOffset() {
/* 521*/    return getNumObjectiveFunctions() + this.numDecisionVariables + this.numSlackVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getRhsOffset() {
/* 529*/    return getWidth() - 1;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumDecisionVariables() {
/* 543*/    return this.numDecisionVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getOriginalNumDecisionVariables() {
/* 552*/    return this.f.getCoefficients().getDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumSlackVariables() {
/* 560*/    return this.numSlackVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumArtificialVariables() {
/* 568*/    return this.numArtificialVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final double[][] getData() {
/* 576*/    return this.tableau.getData();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object other) {
/* 583*/    if (this == other)
/* 584*/      return true; 
/* 587*/    if (other instanceof SimplexTableau) {
/* 588*/      SimplexTableau rhs = (SimplexTableau)other;
/* 589*/      return (this.restrictToNonNegative == rhs.restrictToNonNegative && this.numDecisionVariables == rhs.numDecisionVariables && this.numSlackVariables == rhs.numSlackVariables && this.numArtificialVariables == rhs.numArtificialVariables && this.epsilon == rhs.epsilon && this.maxUlps == rhs.maxUlps && this.f.equals(rhs.f) && this.constraints.equals(rhs.constraints) && this.tableau.equals(rhs.tableau));
/*   0*/    } 
/* 599*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 605*/    return this.restrictToNonNegative.hashCode() ^ this.numDecisionVariables ^ this.numSlackVariables ^ this.numArtificialVariables ^ this.epsilon.hashCode() ^ this.maxUlps ^ this.f.hashCode() ^ this.constraints.hashCode() ^ this.tableau.hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream oos) throws IOException {
/* 622*/    oos.defaultWriteObject();
/* 623*/    MatrixUtils.serializeRealMatrix(this.tableau, oos);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
/* 633*/    ois.defaultReadObject();
/* 634*/    MatrixUtils.deserializeRealMatrix(this, "tableau", ois);
/*   0*/  }
/*   0*/}
