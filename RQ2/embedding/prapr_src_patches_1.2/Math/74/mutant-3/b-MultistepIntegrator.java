/*   0*/package org.apache.commons.math.ode;
/*   0*/
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.linear.Array2DRowRealMatrix;
/*   0*/import org.apache.commons.math.linear.RealMatrix;
/*   0*/import org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator;
/*   0*/import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
/*   0*/import org.apache.commons.math.ode.sampling.StepHandler;
/*   0*/import org.apache.commons.math.ode.sampling.StepInterpolator;
/*   0*/
/*   0*/public abstract class MultistepIntegrator extends AdaptiveStepsizeIntegrator {
/*   0*/  protected double[] scaled;
/*   0*/  
/*   0*/  protected Array2DRowRealMatrix nordsieck;
/*   0*/  
/*   0*/  private FirstOrderIntegrator starter;
/*   0*/  
/*   0*/  private final int nSteps;
/*   0*/  
/*   0*/  private double exp;
/*   0*/  
/*   0*/  private double safety;
/*   0*/  
/*   0*/  private double minReduction;
/*   0*/  
/*   0*/  private double maxGrowth;
/*   0*/  
/*   0*/  protected MultistepIntegrator(String name, int nSteps, int order, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
/* 112*/    super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
/* 114*/    if (nSteps <= 0)
/* 115*/      throw MathRuntimeException.createIllegalArgumentException("{0} method needs at least one previous point", new Object[] { name }); 
/* 120*/    this.starter = new DormandPrince853Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
/* 123*/    this.nSteps = nSteps;
/* 125*/    this.exp = -1.0D / order;
/* 128*/    setSafety(0.9D);
/* 129*/    setMinReduction(0.2D);
/* 130*/    setMaxGrowth(Math.pow(2.0D, -this.exp));
/*   0*/  }
/*   0*/  
/*   0*/  protected MultistepIntegrator(String name, int nSteps, int order, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
/* 158*/    super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
/* 159*/    this.starter = new DormandPrince853Integrator(minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
/* 162*/    this.nSteps = nSteps;
/* 164*/    this.exp = -1.0D / order;
/* 167*/    setSafety(0.9D);
/* 168*/    setMinReduction(0.2D);
/* 169*/    setMaxGrowth(Math.pow(2.0D, -this.exp));
/*   0*/  }
/*   0*/  
/*   0*/  public ODEIntegrator getStarterIntegrator() {
/* 178*/    return this.starter;
/*   0*/  }
/*   0*/  
/*   0*/  public void setStarterIntegrator(FirstOrderIntegrator starterIntegrator) {
/* 189*/    this.starter = starterIntegrator;
/*   0*/  }
/*   0*/  
/*   0*/  protected void start(double t0, double[] y0, double t) throws DerivativeException, IntegratorException {
/* 215*/    this.starter.clearEventHandlers();
/* 216*/    this.starter.clearStepHandlers();
/* 219*/    this.starter.addStepHandler(new NordsieckInitializer(y0.length));
/*   0*/    try {
/* 223*/      this.starter.integrate(new CountingDifferentialEquations(y0.length), t0, y0, t, new double[y0.length]);
/* 225*/    } catch (DerivativeException de) {
/* 226*/      if (!(de instanceof InitializationCompletedMarkerException))
/* 228*/        throw de; 
/*   0*/    } 
/* 233*/    this.starter.clearStepHandlers();
/*   0*/  }
/*   0*/  
/*   0*/  public double getMinReduction() {
/* 250*/    return this.minReduction;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMinReduction(double minReduction) {
/* 257*/    this.minReduction = minReduction;
/*   0*/  }
/*   0*/  
/*   0*/  public double getMaxGrowth() {
/* 264*/    return this.maxGrowth;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaxGrowth(double maxGrowth) {
/* 271*/    this.maxGrowth = maxGrowth;
/*   0*/  }
/*   0*/  
/*   0*/  public double getSafety() {
/* 278*/    return this.safety;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSafety(double safety) {
/* 285*/    this.safety = safety;
/*   0*/  }
/*   0*/  
/*   0*/  protected double computeStepGrowShrinkFactor(double error) {
/* 293*/    return Math.min(this.maxGrowth, Math.max(this.minReduction, this.safety * Math.pow(error, this.exp)));
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract Array2DRowRealMatrix initializeHighOrderDerivatives(double[] paramArrayOfdouble, double[][] paramArrayOfdouble1);
/*   0*/  
/*   0*/  public static interface NordsieckTransformer {
/*   0*/    RealMatrix initializeHighOrderDerivatives(double[] param1ArrayOfdouble, double[][] param1ArrayOfdouble1);
/*   0*/  }
/*   0*/  
/*   0*/  private class NordsieckInitializer implements StepHandler {
/*   0*/    private final int n;
/*   0*/    
/*   0*/    public NordsieckInitializer(int n) {
/* 317*/      this.n = n;
/*   0*/    }
/*   0*/    
/*   0*/    public void handleStep(StepInterpolator interpolator, boolean isLast) throws DerivativeException {
/* 324*/      double prev = interpolator.getPreviousTime();
/* 325*/      double curr = interpolator.getCurrentTime();
/* 326*/      MultistepIntegrator.this.stepStart = prev;
/* 327*/      MultistepIntegrator.this.stepSize = (curr - prev) / (this.n + 1);
/* 330*/      interpolator.setInterpolatedTime(prev);
/* 331*/      MultistepIntegrator.this.scaled = (double[])interpolator.getInterpolatedDerivatives().clone();
/* 332*/      for (int j = 0; j < this.n; j++)
/* 333*/        MultistepIntegrator.this.scaled[j] = MultistepIntegrator.this.scaled[j] * MultistepIntegrator.this.stepSize; 
/* 337*/      double[][] multistep = new double[MultistepIntegrator.this.nSteps][];
/* 338*/      for (int i = 1; i <= MultistepIntegrator.this.nSteps; i++) {
/* 339*/        interpolator.setInterpolatedTime(prev + MultistepIntegrator.this.stepSize * i);
/* 340*/        double[] msI = (double[])interpolator.getInterpolatedDerivatives().clone();
/* 341*/        for (int k = 0; k < this.n; k++)
/* 342*/          msI[k] = msI[k] * MultistepIntegrator.this.stepSize; 
/* 344*/        multistep[i - 1] = msI;
/*   0*/      } 
/* 346*/      MultistepIntegrator.this.nordsieck = MultistepIntegrator.this.initializeHighOrderDerivatives(MultistepIntegrator.this.scaled, multistep);
/* 349*/      throw new MultistepIntegrator.InitializationCompletedMarkerException();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean requiresDenseOutput() {
/* 355*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void reset() {}
/*   0*/  }
/*   0*/  
/*   0*/  private static class InitializationCompletedMarkerException extends DerivativeException {
/*   0*/    private static final long serialVersionUID = -4105805787353488365L;
/*   0*/    
/*   0*/    public InitializationCompletedMarkerException() {
/* 374*/      super(null);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class CountingDifferentialEquations implements FirstOrderDifferentialEquations {
/*   0*/    private final int dimension;
/*   0*/    
/*   0*/    public CountingDifferentialEquations(int dimension) {
/* 389*/      this.dimension = dimension;
/*   0*/    }
/*   0*/    
/*   0*/    public void computeDerivatives(double t, double[] y, double[] dot) throws DerivativeException {
/* 395*/      MultistepIntegrator.this.computeDerivatives(t, y, dot);
/*   0*/    }
/*   0*/    
/*   0*/    public int getDimension() {
/* 400*/      return this.dimension;
/*   0*/    }
/*   0*/  }
/*   0*/}
