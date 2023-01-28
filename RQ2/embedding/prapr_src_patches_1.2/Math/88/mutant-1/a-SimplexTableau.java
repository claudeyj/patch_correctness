/*   0*/package org.apache.commons.math.optimization.linear;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.math.linear.MatrixUtils;
/*   0*/import org.apache.commons.math.linear.RealMatrix;
/*   0*/import org.apache.commons.math.linear.RealMatrixImpl;
/*   0*/import org.apache.commons.math.linear.RealVector;
/*   0*/import org.apache.commons.math.optimization.GoalType;
/*   0*/import org.apache.commons.math.optimization.RealPointValuePair;
/*   0*/import org.apache.commons.math.util.MathUtils;
/*   0*/
/*   0*/class SimplexTableau implements Serializable {
/*   0*/  private static final long serialVersionUID = -1369660067587938365L;
/*   0*/  
/*   0*/  private final LinearObjectiveFunction f;
/*   0*/  
/*   0*/  private final Collection<LinearConstraint> constraints;
/*   0*/  
/*   0*/  private final boolean restrictToNonNegative;
/*   0*/  
/*   0*/  protected transient RealMatrix tableau;
/*   0*/  
/*   0*/  protected final int numDecisionVariables;
/*   0*/  
/*   0*/  protected final int numSlackVariables;
/*   0*/  
/*   0*/  protected int numArtificialVariables;
/*   0*/  
/*   0*/  protected final double epsilon;
/*   0*/  
/*   0*/  SimplexTableau(LinearObjectiveFunction f, Collection<LinearConstraint> constraints, GoalType goalType, boolean restrictToNonNegative, double epsilon) {
/* 105*/    this.f = f;
/* 106*/    this.constraints = constraints;
/* 107*/    this.restrictToNonNegative = restrictToNonNegative;
/* 108*/    this.epsilon = epsilon;
/* 109*/    this.numDecisionVariables = getNumVariables() + (restrictToNonNegative ? 0 : 1);
/* 110*/    this.numSlackVariables = getConstraintTypeCounts(Relationship.LEQ) + getConstraintTypeCounts(Relationship.GEQ);
/* 112*/    this.numArtificialVariables = getConstraintTypeCounts(Relationship.EQ) + getConstraintTypeCounts(Relationship.GEQ);
/* 114*/    this.tableau = new RealMatrixImpl(createTableau((goalType == GoalType.MAXIMIZE)));
/* 115*/    initialize();
/*   0*/  }
/*   0*/  
/*   0*/  protected double[][] createTableau(boolean maximize) {
/* 126*/    List<LinearConstraint> constraints = getNormalizedConstraints();
/* 127*/    int width = this.numDecisionVariables + this.numSlackVariables + this.numArtificialVariables + getNumObjectiveFunctions() + 1;
/* 129*/    int height = constraints.size() + getNumObjectiveFunctions();
/* 130*/    double[][] matrix = new double[height][width];
/* 133*/    if (getNumObjectiveFunctions() == 2)
/* 134*/      matrix[0][0] = -1.0D; 
/* 136*/    int zIndex = (getNumObjectiveFunctions() == 1) ? 0 : 1;
/* 137*/    matrix[zIndex][zIndex] = maximize ? 1.0D : -1.0D;
/* 138*/    RealVector objectiveCoefficients = maximize ? this.f.getCoefficients().mapMultiply(-1.0D) : this.f.getCoefficients();
/* 140*/    copyArray(objectiveCoefficients.getData(), matrix[zIndex], getNumObjectiveFunctions());
/* 141*/    matrix[zIndex][width - 1] = maximize ? this.f.getConstantTerm() : (-1.0D * this.f.getConstantTerm());
/* 144*/    if (!this.restrictToNonNegative)
/* 145*/      matrix[zIndex][getSlackVariableOffset() - 1] = getInvertedCoeffiecientSum(objectiveCoefficients); 
/* 150*/    int slackVar = 0;
/* 151*/    int artificialVar = 0;
/* 152*/    for (int i = 0; i < constraints.size(); i++) {
/* 153*/      LinearConstraint constraint = constraints.get(i);
/* 154*/      int row = getNumObjectiveFunctions() + i;
/* 157*/      copyArray(constraint.getCoefficients().getData(), matrix[row], 1);
/* 160*/      if (!this.restrictToNonNegative)
/* 161*/        matrix[row][getSlackVariableOffset() - 1] = getInvertedCoeffiecientSum(constraint.getCoefficients()); 
/* 166*/      matrix[row][width - 1] = constraint.getValue();
/* 169*/      if (constraint.getRelationship() == Relationship.LEQ) {
/* 170*/        matrix[row][getSlackVariableOffset() + slackVar++] = 1.0D;
/* 171*/      } else if (constraint.getRelationship() == Relationship.GEQ) {
/* 172*/        matrix[row][getSlackVariableOffset() + slackVar++] = -1.0D;
/*   0*/      } 
/* 176*/      if (constraint.getRelationship() == Relationship.EQ || constraint.getRelationship() == Relationship.GEQ) {
/* 178*/        matrix[0][getArtificialVariableOffset() + artificialVar] = 1.0D;
/* 179*/        matrix[row][getArtificialVariableOffset() + artificialVar++] = 1.0D;
/*   0*/      } 
/*   0*/    } 
/* 183*/    return matrix;
/*   0*/  }
/*   0*/  
/*   0*/  public int getNumVariables() {
/* 190*/    return this.f.getCoefficients().getDimension();
/*   0*/  }
/*   0*/  
/*   0*/  public List<LinearConstraint> getNormalizedConstraints() {
/* 198*/    List<LinearConstraint> normalized = new ArrayList<LinearConstraint>();
/* 199*/    for (LinearConstraint constraint : this.constraints)
/* 200*/      normalized.add(normalize(constraint)); 
/* 202*/    return normalized;
/*   0*/  }
/*   0*/  
/*   0*/  private LinearConstraint normalize(LinearConstraint constraint) {
/* 211*/    if (constraint.getValue() < 0.0D)
/* 212*/      return new LinearConstraint(constraint.getCoefficients().mapMultiply(-1.0D), constraint.getRelationship().oppositeRelationship(), -1.0D * constraint.getValue()); 
/* 216*/    return new LinearConstraint(constraint.getCoefficients(), constraint.getRelationship(), constraint.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumObjectiveFunctions() {
/* 225*/    return (this.numArtificialVariables > 0) ? 2 : 1;
/*   0*/  }
/*   0*/  
/*   0*/  private int getConstraintTypeCounts(Relationship relationship) {
/* 234*/    int count = 0;
/* 235*/    for (LinearConstraint constraint : this.constraints) {
/* 236*/      if (constraint.getRelationship() == relationship)
/* 237*/        count++; 
/*   0*/    } 
/* 240*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private void initialize() {
/* 248*/    for (int artificialVar = 0; artificialVar < this.numArtificialVariables; artificialVar++) {
/* 249*/      int row = getBasicRow(getArtificialVariableOffset() + artificialVar);
/* 250*/      subtractRow(0, row, 1.0D);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected static double getInvertedCoeffiecientSum(RealVector coefficients) {
/* 260*/    double sum = 0.0D;
/* 261*/    for (double coefficient : coefficients.getData())
/* 262*/      sum -= coefficient; 
/* 264*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  private Integer getBasicRow(int col) {
/* 273*/    Integer row = null;
/* 274*/    for (int i = getNumObjectiveFunctions(); i < getHeight(); i++) {
/* 275*/      if (!MathUtils.equals(getEntry(i, col), 0.0D, this.epsilon))
/* 276*/        if (row == null) {
/* 277*/          row = i;
/*   0*/        } else {
/* 279*/          return null;
/*   0*/        }  
/*   0*/    } 
/* 283*/    return row;
/*   0*/  }
/*   0*/  
/*   0*/  protected void discardArtificialVariables() {
/* 290*/    if (this.numArtificialVariables == 0)
/*   0*/      return; 
/* 293*/    int width = getWidth() - this.numArtificialVariables - 1;
/* 294*/    int height = getHeight() - 1;
/* 295*/    double[][] matrix = new double[height][width];
/* 296*/    for (int i = 0; i < height; i++) {
/* 297*/      for (int j = 0; j < width - 1; j++)
/* 298*/        matrix[i][j] = getEntry(i + 1, j + 1); 
/* 300*/      matrix[i][width - 1] = getEntry(i + 1, getRhsOffset());
/*   0*/    } 
/* 302*/    this.tableau = new RealMatrixImpl(matrix);
/* 303*/    this.numArtificialVariables = 0;
/*   0*/  }
/*   0*/  
/*   0*/  private void copyArray(double[] src, double[] dest, int destPos) {
/* 314*/    System.arraycopy(src, 0, dest, getNumObjectiveFunctions(), src.length);
/*   0*/  }
/*   0*/  
/*   0*/  protected RealPointValuePair getSolution() {
/* 325*/    double[] coefficients = new double[getOriginalNumDecisionVariables()];
/* 326*/    Integer basicRow = getBasicRow(getNumObjectiveFunctions() + getOriginalNumDecisionVariables());
/* 328*/    double mostNegative = (basicRow == null) ? 0.0D : getEntry(basicRow, getRhsOffset());
/* 329*/    for (int i = 0; i < coefficients.length; i++) {
/* 330*/      basicRow = getBasicRow(getNumObjectiveFunctions() + i);
/* 333*/      coefficients[i] = ((basicRow == null) ? 0.0D : getEntry(basicRow, getRhsOffset())) - (this.restrictToNonNegative ? 0.0D : mostNegative);
/* 336*/      if (basicRow != null)
/* 337*/        for (int j = getNumObjectiveFunctions(); j < getNumObjectiveFunctions() + i; j++) {
/* 338*/          if (this.tableau.getEntry(basicRow, j) == 1.0D)
/* 339*/            coefficients[i] = 0.0D; 
/*   0*/        }  
/*   0*/    } 
/* 344*/    return new RealPointValuePair(coefficients, this.f.getValue(coefficients));
/*   0*/  }
/*   0*/  
/*   0*/  protected void divideRow(int dividendRow, double divisor) {
/* 357*/    for (int j = 0; j < getWidth(); j++)
/* 358*/      this.tableau.setEntry(dividendRow, j, this.tableau.getEntry(dividendRow, j) / divisor); 
/*   0*/  }
/*   0*/  
/*   0*/  protected void subtractRow(int minuendRow, int subtrahendRow, double multiple) {
/* 374*/    for (int j = 0; j < getWidth(); j++)
/* 375*/      this.tableau.setEntry(minuendRow, j, this.tableau.getEntry(minuendRow, j) - multiple * this.tableau.getEntry(subtrahendRow, j)); 
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getWidth() {
/* 385*/    return this.tableau.getColumnDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getHeight() {
/* 393*/    return this.tableau.getRowDimension();
/*   0*/  }
/*   0*/  
/*   0*/  protected final double getEntry(int row, int column) {
/* 402*/    return this.tableau.getEntry(row, column);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void setEntry(int row, int column, double value) {
/* 412*/    this.tableau.setEntry(row, column, value);
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getSlackVariableOffset() {
/* 420*/    return getNumObjectiveFunctions() + this.numDecisionVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getArtificialVariableOffset() {
/* 428*/    return getNumObjectiveFunctions() + this.numDecisionVariables + this.numSlackVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getRhsOffset() {
/* 436*/    return getWidth() - 1;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumDecisionVariables() {
/* 450*/    return this.numDecisionVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getOriginalNumDecisionVariables() {
/* 459*/    return this.restrictToNonNegative ? this.numDecisionVariables : (this.numDecisionVariables - 1);
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumSlackVariables() {
/* 467*/    return this.numSlackVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getNumArtificialVariables() {
/* 475*/    return this.numArtificialVariables;
/*   0*/  }
/*   0*/  
/*   0*/  protected final double[][] getData() {
/* 483*/    return this.tableau.getData();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object other) {
/* 490*/    if (this == other)
/* 491*/      return true; 
/* 494*/    if (other == null)
/* 495*/      return false; 
/*   0*/    try {
/* 500*/      SimplexTableau rhs = (SimplexTableau)other;
/* 501*/      return (this.restrictToNonNegative == rhs.restrictToNonNegative && this.numDecisionVariables == rhs.numDecisionVariables && this.numSlackVariables == rhs.numSlackVariables && this.numArtificialVariables == rhs.numArtificialVariables && this.epsilon == rhs.epsilon && this.f.equals(rhs.f) && this.constraints.equals(rhs.constraints) && this.tableau.equals(rhs.tableau));
/* 510*/    } catch (ClassCastException ex) {
/* 512*/      return false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 520*/    return this.restrictToNonNegative.hashCode() ^ this.numDecisionVariables ^ this.numSlackVariables ^ this.numArtificialVariables ^ this.epsilon.hashCode() ^ this.f.hashCode() ^ this.constraints.hashCode() ^ this.tableau.hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream oos) throws IOException {
/* 536*/    oos.defaultWriteObject();
/* 537*/    MatrixUtils.serializeRealMatrix(this.tableau, oos);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
/* 547*/    ois.defaultReadObject();
/* 548*/    MatrixUtils.deserializeRealMatrix(this, "tableau", ois);
/*   0*/  }
/*   0*/}
