/*   0*/package org.apache.commons.math.optimization.univariate;
/*   0*/
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Comparator;
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/import org.apache.commons.math.exception.ConvergenceException;
/*   0*/import org.apache.commons.math.exception.MathIllegalStateException;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math.optimization.ConvergenceChecker;
/*   0*/import org.apache.commons.math.optimization.GoalType;
/*   0*/import org.apache.commons.math.random.RandomGenerator;
/*   0*/import org.apache.commons.math.util.FastMath;
/*   0*/
/*   0*/public class MultiStartUnivariateRealOptimizer<FUNC extends UnivariateRealFunction> implements BaseUnivariateRealOptimizer<FUNC> {
/*   0*/  private final BaseUnivariateRealOptimizer<FUNC> optimizer;
/*   0*/  
/*   0*/  private int maxEvaluations;
/*   0*/  
/*   0*/  private int totalEvaluations;
/*   0*/  
/*   0*/  private int starts;
/*   0*/  
/*   0*/  private RandomGenerator generator;
/*   0*/  
/*   0*/  private UnivariateRealPointValuePair[] optima;
/*   0*/  
/*   0*/  public MultiStartUnivariateRealOptimizer(BaseUnivariateRealOptimizer<FUNC> optimizer, int starts, RandomGenerator generator) {
/*  73*/    this.optimizer = optimizer;
/*  74*/    this.starts = starts;
/*  75*/    this.generator = generator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setConvergenceChecker(ConvergenceChecker<UnivariateRealPointValuePair> checker) {
/*  82*/    this.optimizer.setConvergenceChecker(checker);
/*   0*/  }
/*   0*/  
/*   0*/  public ConvergenceChecker<UnivariateRealPointValuePair> getConvergenceChecker() {
/*  89*/    return this.optimizer.getConvergenceChecker();
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxEvaluations() {
/*  94*/    return this.maxEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getEvaluations() {
/*  99*/    return this.totalEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaxEvaluations(int maxEvaluations) {
/* 104*/    this.maxEvaluations = maxEvaluations;
/* 105*/    this.optimizer.setMaxEvaluations(maxEvaluations);
/*   0*/  }
/*   0*/  
/*   0*/  public UnivariateRealPointValuePair[] getOptima() {
/* 135*/    if (this.optima == null)
/* 136*/      throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]); 
/* 138*/    return (UnivariateRealPointValuePair[])this.optima.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public UnivariateRealPointValuePair optimize(FUNC f, GoalType goal, double min, double max) throws FunctionEvaluationException {
/* 146*/    return optimize(f, goal, min, max, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public UnivariateRealPointValuePair optimize(FUNC f, GoalType goal, double min, double max, double startValue) throws FunctionEvaluationException {
/* 154*/    this.optima = new UnivariateRealPointValuePair[this.starts];
/* 155*/    this.totalEvaluations = 0;
/* 158*/    for (int i = 0; i < this.starts; i++) {
/*   0*/      try {
/* 160*/        double bound1 = (i == 0) ? min : (min + this.generator.nextDouble() * (max - min));
/* 161*/        double bound2 = (i == 0) ? max : (min + this.generator.nextDouble() * (max - min));
/* 162*/        this.optima[i] = this.optimizer.optimize(f, goal, FastMath.min(min, bound2), FastMath.max(bound1, bound2));
/* 163*/      } catch (FunctionEvaluationException fee) {
/* 164*/        this.optima[i] = null;
/* 165*/      } catch (ConvergenceException ce) {
/* 166*/        this.optima[i] = null;
/*   0*/      } 
/* 169*/      int usedEvaluations = this.optimizer.getEvaluations();
/* 170*/      this.optimizer.setMaxEvaluations(this.optimizer.getMaxEvaluations() - usedEvaluations);
/* 171*/      this.totalEvaluations += usedEvaluations;
/*   0*/    } 
/* 174*/    sortPairs(goal);
/* 176*/    if (this.optima[0] == null)
/* 177*/      throw new ConvergenceException(LocalizedFormats.NO_CONVERGENCE_WITH_ANY_START_POINT, new Object[] { this.starts }); 
/* 182*/    return this.optima[0];
/*   0*/  }
/*   0*/  
/*   0*/  private void sortPairs(final GoalType goal) {
/* 191*/    Arrays.sort(this.optima, new Comparator<UnivariateRealPointValuePair>() {
/*   0*/          public int compare(UnivariateRealPointValuePair o1, UnivariateRealPointValuePair o2) {
/* 194*/            if (o1 == null)
/* 195*/              return (o2 == null) ? 0 : 1; 
/* 196*/            if (o2 == null)
/* 197*/              return -1; 
/* 199*/            double v1 = o1.getValue();
/* 200*/            double v2 = o2.getValue();
/* 201*/            return (goal == GoalType.MINIMIZE) ? Double.compare(v1, v2) : Double.compare(v2, v1);
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/}
