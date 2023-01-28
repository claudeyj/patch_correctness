/*   0*/package org.apache.commons.math.optimization.general;
/*   0*/
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MaxEvaluationsExceededException;
/*   0*/import org.apache.commons.math.MaxIterationsExceededException;
/*   0*/import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
/*   0*/import org.apache.commons.math.analysis.MultivariateMatrixFunction;
/*   0*/import org.apache.commons.math.exception.LocalizedFormats;
/*   0*/import org.apache.commons.math.linear.InvalidMatrixException;
/*   0*/import org.apache.commons.math.linear.LUDecompositionImpl;
/*   0*/import org.apache.commons.math.linear.MatrixUtils;
/*   0*/import org.apache.commons.math.linear.RealMatrix;
/*   0*/import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
/*   0*/import org.apache.commons.math.optimization.OptimizationException;
/*   0*/import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
/*   0*/import org.apache.commons.math.optimization.VectorialConvergenceChecker;
/*   0*/import org.apache.commons.math.optimization.VectorialPointValuePair;
/*   0*/
/*   0*/public abstract class AbstractLeastSquaresOptimizer implements DifferentiableMultivariateVectorialOptimizer {
/*   0*/  public static final int DEFAULT_MAX_ITERATIONS = 100;
/*   0*/  
/*   0*/  protected VectorialConvergenceChecker checker;
/*   0*/  
/*   0*/  protected double[][] jacobian;
/*   0*/  
/*   0*/  protected int cols;
/*   0*/  
/*   0*/  protected int rows;
/*   0*/  
/*   0*/  protected double[] targetValues;
/*   0*/  
/*   0*/  protected double[] residualsWeights;
/*   0*/  
/*   0*/  protected double[] point;
/*   0*/  
/*   0*/  protected double[] objective;
/*   0*/  
/*   0*/  protected double[] residuals;
/*   0*/  
/*   0*/  protected double cost;
/*   0*/  
/*   0*/  private int maxIterations;
/*   0*/  
/*   0*/  private int iterations;
/*   0*/  
/*   0*/  private int maxEvaluations;
/*   0*/  
/*   0*/  private int objectiveEvaluations;
/*   0*/  
/*   0*/  private int jacobianEvaluations;
/*   0*/  
/*   0*/  private DifferentiableMultivariateVectorialFunction function;
/*   0*/  
/*   0*/  private MultivariateMatrixFunction jF;
/*   0*/  
/*   0*/  protected AbstractLeastSquaresOptimizer() {
/* 117*/    setConvergenceChecker(new SimpleVectorialValueChecker());
/* 118*/    setMaxIterations(100);
/* 119*/    setMaxEvaluations(Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaxIterations(int maxIterations) {
/* 124*/    this.maxIterations = maxIterations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxIterations() {
/* 129*/    return this.maxIterations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIterations() {
/* 134*/    return this.iterations;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaxEvaluations(int maxEvaluations) {
/* 139*/    this.maxEvaluations = maxEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxEvaluations() {
/* 144*/    return this.maxEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getEvaluations() {
/* 149*/    return this.objectiveEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getJacobianEvaluations() {
/* 154*/    return this.jacobianEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public void setConvergenceChecker(VectorialConvergenceChecker convergenceChecker) {
/* 159*/    this.checker = convergenceChecker;
/*   0*/  }
/*   0*/  
/*   0*/  public VectorialConvergenceChecker getConvergenceChecker() {
/* 164*/    return this.checker;
/*   0*/  }
/*   0*/  
/*   0*/  protected void incrementIterationsCounter() throws OptimizationException {
/* 173*/    if (++this.iterations > this.maxIterations)
/* 174*/      throw new OptimizationException(new MaxIterationsExceededException(this.maxIterations)); 
/*   0*/  }
/*   0*/  
/*   0*/  protected void updateJacobian() throws FunctionEvaluationException {
/* 184*/    this.jacobianEvaluations++;
/* 185*/    this.jacobian = this.jF.value(this.point);
/* 186*/    if (this.jacobian.length != this.rows)
/* 187*/      throw new FunctionEvaluationException(this.point, LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { this.jacobian.length, this.rows }); 
/* 190*/    for (int i = 0; i < this.rows; i++) {
/* 191*/      double[] ji = this.jacobian[i];
/* 192*/      double factor = -Math.sqrt(this.residualsWeights[i]);
/* 193*/      for (int j = 0; j < this.cols; j++)
/* 194*/        ji[j] = ji[j] * factor; 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void updateResidualsAndCost() throws FunctionEvaluationException {
/* 208*/    if (++this.objectiveEvaluations > this.maxEvaluations)
/* 209*/      throw new FunctionEvaluationException(new MaxEvaluationsExceededException(this.maxEvaluations), this.point); 
/* 212*/    this.objective = this.function.value(this.point);
/* 213*/    if (this.objective.length != this.rows)
/* 214*/      throw new FunctionEvaluationException(this.point, LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { this.objective.length, this.rows }); 
/* 217*/    this.cost = 0.0D;
/* 218*/    int index = 0;
/* 219*/    for (int i = 0; i < this.rows; i++) {
/* 220*/      double residual = this.targetValues[i] - this.objective[i];
/* 221*/      this.residuals[i] = residual;
/* 222*/      this.cost += this.residualsWeights[i] * residual * residual;
/* 223*/      index += this.cols;
/*   0*/    } 
/* 225*/    this.cost = Math.sqrt(this.cost);
/*   0*/  }
/*   0*/  
/*   0*/  public double getRMS() {
/* 240*/    double criterion = 0.0D;
/* 241*/    for (int i = 0; i < this.rows; i++) {
/* 242*/      double residual = this.residuals[i];
/* 243*/      criterion += residual * residual * this.residualsWeights[i];
/*   0*/    } 
/* 245*/    return Math.sqrt(criterion / this.rows);
/*   0*/  }
/*   0*/  
/*   0*/  public double getChiSquare() {
/* 255*/    double chiSquare = 0.0D;
/* 256*/    for (int i = 0; i < this.rows; i++) {
/* 257*/      double residual = this.residuals[i];
/* 258*/      chiSquare += residual * residual * this.residualsWeights[i];
/*   0*/    } 
/* 260*/    return chiSquare;
/*   0*/  }
/*   0*/  
/*   0*/  public double[][] getCovariances() throws FunctionEvaluationException, OptimizationException {
/* 275*/    updateJacobian();
/* 278*/    double[][] jTj = new double[this.cols][this.cols];
/* 279*/    for (int i = 0; i < this.cols; i++) {
/* 280*/      for (int j = i; j < this.cols; j++) {
/* 281*/        double sum = 0.0D;
/* 282*/        for (int k = 0; k < this.rows; k++)
/* 283*/          sum += this.jacobian[k][i] * this.jacobian[k][j]; 
/* 285*/        jTj[i][j] = sum;
/* 286*/        jTj[j][i] = sum;
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 292*/      RealMatrix inverse = new LUDecompositionImpl(MatrixUtils.createRealMatrix(jTj)).getSolver().getInverse();
/* 294*/      return inverse.getData();
/* 295*/    } catch (InvalidMatrixException ime) {
/* 296*/      throw new OptimizationException(LocalizedFormats.UNABLE_TO_COMPUTE_COVARIANCE_SINGULAR_PROBLEM, new Object[0]);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double[] guessParametersErrors() throws FunctionEvaluationException, OptimizationException {
/* 312*/    if (this.rows <= this.cols)
/* 313*/      throw new OptimizationException(LocalizedFormats.NO_DEGREES_OF_FREEDOM, new Object[] { this.rows, this.cols }); 
/* 317*/    double[] errors = new double[this.cols];
/* 318*/    double c = Math.sqrt(getChiSquare() / (this.rows - this.cols));
/* 319*/    double[][] covar = getCovariances();
/* 320*/    for (int i = 0; i < errors.length; i++)
/* 321*/      errors[i] = Math.sqrt(covar[i][i]) * c; 
/* 323*/    return errors;
/*   0*/  }
/*   0*/  
/*   0*/  public VectorialPointValuePair optimize(DifferentiableMultivariateVectorialFunction f, double[] target, double[] weights, double[] startPoint) throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {
/* 332*/    if (target.length != weights.length)
/* 333*/      throw new OptimizationException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { target.length, weights.length }); 
/* 338*/    this.iterations = 0;
/* 339*/    this.objectiveEvaluations = 0;
/* 340*/    this.jacobianEvaluations = 0;
/* 343*/    this.function = f;
/* 344*/    this.jF = f.jacobian();
/* 345*/    this.targetValues = (double[])target.clone();
/* 346*/    this.residualsWeights = (double[])weights.clone();
/* 347*/    this.point = (double[])startPoint.clone();
/* 348*/    this.residuals = new double[target.length];
/* 351*/    this.rows = target.length;
/* 352*/    this.cols = this.point.length;
/* 353*/    this.jacobian = new double[this.rows][this.cols];
/* 355*/    this.cost = Double.POSITIVE_INFINITY;
/* 357*/    return doOptimize();
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract VectorialPointValuePair doOptimize() throws FunctionEvaluationException, OptimizationException, IllegalArgumentException;
/*   0*/}
