/*   0*/package org.apache.commons.math.ode.nonstiff;
/*   0*/
/*   0*/import java.util.Arrays;
/*   0*/import org.apache.commons.math.linear.Array2DRowRealMatrix;
/*   0*/import org.apache.commons.math.linear.MatrixVisitorException;
/*   0*/import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
/*   0*/import org.apache.commons.math.ode.DerivativeException;
/*   0*/import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
/*   0*/import org.apache.commons.math.ode.IntegratorException;
/*   0*/import org.apache.commons.math.ode.events.CombinedEventsManager;
/*   0*/import org.apache.commons.math.ode.sampling.NordsieckStepInterpolator;
/*   0*/import org.apache.commons.math.ode.sampling.StepHandler;
/*   0*/
/*   0*/public class AdamsMoultonIntegrator extends AdamsIntegrator {
/*   0*/  public AdamsMoultonIntegrator(int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws IllegalArgumentException {
/* 176*/    super("Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
/*   0*/  }
/*   0*/  
/*   0*/  public AdamsMoultonIntegrator(int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) throws IllegalArgumentException {
/* 196*/    super("Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
/*   0*/  }
/*   0*/  
/*   0*/  public double integrate(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y) throws DerivativeException, IntegratorException {
/* 208*/    int n = y0.length;
/* 209*/    sanityChecks(equations, t0, y0, t, y);
/* 210*/    setEquations(equations);
/* 211*/    resetEvaluations();
/* 212*/    boolean forward = (t > t0);
/* 215*/    if (y != y0)
/* 216*/      System.arraycopy(y0, 0, y, 0, n); 
/* 218*/    double[] yDot = new double[y0.length];
/* 219*/    double[] yTmp = new double[y0.length];
/* 222*/    NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
/* 223*/    interpolator.reinitialize(y, forward);
/* 224*/    NordsieckStepInterpolator interpolatorTmp = new NordsieckStepInterpolator();
/* 225*/    interpolatorTmp.reinitialize(yTmp, forward);
/* 228*/    for (StepHandler handler : this.stepHandlers)
/* 229*/      handler.reset(); 
/* 231*/    CombinedEventsManager manager = addEndTimeChecker(t0, t, this.eventsHandlersManager);
/* 235*/    start(t0, y, t);
/* 236*/    interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
/* 237*/    interpolator.storeTime(this.stepStart);
/* 239*/    this;
/* 239*/    double hNew = t;
/* 240*/    interpolator.rescale(hNew);
/*   0*/    boolean lastStep = false;
/* 243*/    while (!lastStep) {
/* 246*/      interpolator.shift();
/* 248*/      double error = 0.0D;
/* 249*/      for (boolean loop = true; loop; ) {
/* 251*/        this.stepSize = hNew;
/* 254*/        double stepEnd = this.stepStart + this.stepSize;
/* 255*/        interpolator.setInterpolatedTime(stepEnd);
/* 256*/        System.arraycopy(interpolator.getInterpolatedState(), 0, yTmp, 0, y0.length);
/* 259*/        computeDerivatives(stepEnd, yTmp, yDot);
/* 262*/        double[] predictedScaled = new double[y0.length];
/* 263*/        for (int j = 0; j < y0.length; j++)
/* 264*/          predictedScaled[j] = this.stepSize * yDot[j]; 
/* 266*/        Array2DRowRealMatrix nordsieckTmp = updateHighOrderDerivativesPhase1(this.nordsieck);
/* 267*/        updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, nordsieckTmp);
/* 270*/        error = nordsieckTmp.walkInOptimizedOrder(new Corrector(y, predictedScaled, yTmp));
/* 272*/        if (error <= 1.0D) {
/* 275*/          computeDerivatives(stepEnd, yTmp, yDot);
/* 278*/          double[] correctedScaled = new double[y0.length];
/* 279*/          for (int i = 0; i < y0.length; i++)
/* 280*/            correctedScaled[i] = this.stepSize * yDot[i]; 
/* 282*/          updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, nordsieckTmp);
/* 285*/          interpolatorTmp.reinitialize(stepEnd, this.stepSize, correctedScaled, nordsieckTmp);
/* 286*/          interpolatorTmp.storeTime(this.stepStart);
/* 287*/          interpolatorTmp.shift();
/* 288*/          interpolatorTmp.storeTime(stepEnd);
/* 289*/          if (manager.evaluateStep(interpolatorTmp)) {
/* 290*/            double dt = manager.getEventTime() - this.stepStart;
/* 291*/            if (Math.abs(dt) <= Math.ulp(this.stepStart)) {
/* 293*/              loop = false;
/*   0*/              continue;
/*   0*/            } 
/* 296*/            hNew = dt;
/* 297*/            interpolator.rescale(hNew);
/*   0*/            continue;
/*   0*/          } 
/* 301*/          this.scaled = correctedScaled;
/* 302*/          this.nordsieck = nordsieckTmp;
/* 303*/          interpolator.reinitialize(stepEnd, this.stepSize, this.scaled, this.nordsieck);
/* 304*/          loop = false;
/*   0*/          continue;
/*   0*/        } 
/* 309*/        double factor = computeStepGrowShrinkFactor(error);
/* 310*/        hNew = filterStep(this.stepSize * factor, forward, false);
/* 311*/        interpolator.rescale(hNew);
/*   0*/      } 
/* 317*/      double nextStep = this.stepStart + this.stepSize;
/* 318*/      System.arraycopy(yTmp, 0, y, 0, n);
/* 319*/      interpolator.storeTime(nextStep);
/* 320*/      manager.stepAccepted(nextStep, y);
/* 321*/      lastStep = manager.stop();
/* 324*/      for (StepHandler handler : this.stepHandlers) {
/* 325*/        interpolator.setInterpolatedTime(nextStep);
/* 326*/        handler.handleStep(interpolator, lastStep);
/*   0*/      } 
/* 328*/      this.stepStart = nextStep;
/* 330*/      if (!lastStep && manager.reset(this.stepStart, y)) {
/* 334*/        start(this.stepStart, y, t);
/* 335*/        interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
/*   0*/      } 
/* 339*/      if (!lastStep) {
/* 343*/        this.stepSize = filterStep(this.stepSize, forward, true);
/* 346*/        double factor = computeStepGrowShrinkFactor(error);
/* 347*/        double scaledH = this.stepSize * factor;
/* 348*/        double nextT = this.stepStart + scaledH;
/* 349*/        boolean nextIsLast = forward ? ((nextT >= t)) : ((nextT <= t));
/* 350*/        hNew = filterStep(scaledH, forward, nextIsLast);
/* 351*/        interpolator.rescale(hNew);
/*   0*/      } 
/*   0*/    } 
/* 356*/    double stopTime = this.stepStart;
/* 357*/    this.stepStart = Double.NaN;
/* 358*/    this.stepSize = Double.NaN;
/* 359*/    return stopTime;
/*   0*/  }
/*   0*/  
/*   0*/  private class Corrector implements RealMatrixPreservingVisitor {
/*   0*/    private final double[] previous;
/*   0*/    
/*   0*/    private final double[] scaled;
/*   0*/    
/*   0*/    private final double[] before;
/*   0*/    
/*   0*/    private final double[] after;
/*   0*/    
/*   0*/    public Corrector(double[] previous, double[] scaled, double[] state) {
/* 391*/      this.previous = previous;
/* 392*/      this.scaled = scaled;
/* 393*/      this.after = state;
/* 394*/      this.before = (double[])state.clone();
/*   0*/    }
/*   0*/    
/*   0*/    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
/* 400*/      Arrays.fill(this.after, 0.0D);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(int row, int column, double value) throws MatrixVisitorException {
/* 406*/      if ((row & 0x1) == 0) {
/* 407*/        this.after[column] = this.after[column] - value;
/*   0*/      } else {
/* 409*/        this.after[column] = this.after[column] + value;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public double end() {
/* 424*/      double error = 0.0D;
/* 425*/      for (int i = 0; i < this.after.length; i++) {
/* 426*/        this.after[i] = this.after[i] + this.previous[i] + this.scaled[i];
/* 427*/        double yScale = Math.max(Math.abs(this.previous[i]), Math.abs(this.after[i]));
/* 428*/        double tol = (AdamsMoultonIntegrator.this.vecAbsoluteTolerance == null) ? (AdamsMoultonIntegrator.this.scalAbsoluteTolerance + AdamsMoultonIntegrator.this.scalRelativeTolerance * yScale) : (AdamsMoultonIntegrator.this.vecAbsoluteTolerance[i] + AdamsMoultonIntegrator.this.vecRelativeTolerance[i] * yScale);
/* 431*/        double ratio = (this.after[i] - this.before[i]) / tol;
/* 432*/        error += ratio * ratio;
/*   0*/      } 
/* 435*/      return Math.sqrt(error / this.after.length);
/*   0*/    }
/*   0*/  }
/*   0*/}
