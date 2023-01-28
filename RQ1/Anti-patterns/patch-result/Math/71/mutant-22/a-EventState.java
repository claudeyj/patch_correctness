/*   0*/package org.apache.commons.math.ode.events;
/*   0*/
/*   0*/import org.apache.commons.math.ConvergenceException;
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.analysis.UnivariateRealFunction;
/*   0*/import org.apache.commons.math.analysis.solvers.BrentSolver;
/*   0*/import org.apache.commons.math.ode.DerivativeException;
/*   0*/import org.apache.commons.math.ode.sampling.StepInterpolator;
/*   0*/
/*   0*/public class EventState {
/*   0*/  private final EventHandler handler;
/*   0*/  
/*   0*/  private final double maxCheckInterval;
/*   0*/  
/*   0*/  private final double convergence;
/*   0*/  
/*   0*/  private final int maxIterationCount;
/*   0*/  
/*   0*/  private double t0;
/*   0*/  
/*   0*/  private double g0;
/*   0*/  
/*   0*/  private boolean g0Positive;
/*   0*/  
/*   0*/  private boolean pendingEvent;
/*   0*/  
/*   0*/  private double pendingEventTime;
/*   0*/  
/*   0*/  private double previousEventTime;
/*   0*/  
/*   0*/  private boolean forward;
/*   0*/  
/*   0*/  private boolean increasing;
/*   0*/  
/*   0*/  private int nextAction;
/*   0*/  
/*   0*/  public EventState(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount) {
/*  96*/    this.handler = handler;
/*  97*/    this.maxCheckInterval = maxCheckInterval;
/*  98*/    this.convergence = Math.abs(convergence);
/*  99*/    this.maxIterationCount = maxIterationCount;
/* 102*/    this.t0 = Double.NaN;
/* 103*/    this.g0 = Double.NaN;
/* 104*/    this.g0Positive = true;
/* 105*/    this.pendingEvent = false;
/* 106*/    this.pendingEventTime = Double.NaN;
/* 107*/    this.previousEventTime = Double.NaN;
/* 108*/    this.increasing = true;
/* 109*/    this.nextAction = 3;
/*   0*/  }
/*   0*/  
/*   0*/  public EventHandler getEventHandler() {
/* 117*/    return this.handler;
/*   0*/  }
/*   0*/  
/*   0*/  public double getMaxCheckInterval() {
/* 124*/    return this.maxCheckInterval;
/*   0*/  }
/*   0*/  
/*   0*/  public double getConvergence() {
/* 131*/    return this.convergence;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxIterationCount() {
/* 138*/    return this.maxIterationCount;
/*   0*/  }
/*   0*/  
/*   0*/  public void reinitializeBegin(double tStart, double[] yStart) throws EventException {
/* 151*/    this.t0 = tStart;
/* 152*/    this.g0 = this.handler.g(tStart, yStart);
/* 153*/    this.g0Positive = (this.g0 >= 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean evaluateStep(final StepInterpolator interpolator) throws DerivativeException, EventException, ConvergenceException {
/*   0*/    try {
/* 172*/      this.forward = interpolator.isForward();
/* 173*/      double t1 = interpolator.getCurrentTime();
/* 174*/      int n = Math.max(1, (int)Math.ceil(Math.abs(t1 - this.t0) / this.maxCheckInterval));
/* 175*/      double h = (t1 - this.t0) / n;
/* 177*/      double ta = this.t0;
/* 178*/      double ga = this.g0;
/* 179*/      double tb = this.t0 + (interpolator.isForward() ? this.convergence : -this.convergence);
/* 180*/      for (int i = 0; i < n; i++) {
/* 183*/        tb += h;
/* 184*/        interpolator.setInterpolatedTime(tb);
/* 185*/        double gb = this.handler.g(tb, interpolator.getInterpolatedState());
/* 188*/        if (this.g0Positive ^ ((gb >= 0.0D))) {
/* 191*/          if (ga * gb > 0.0D) {
/* 199*/            double epsilon = (this.forward ? 0.25D : -0.25D) * this.convergence;
/* 200*/            for (int k = 0; k < 4 && ga * gb > 0.0D; k++) {
/* 201*/              ta += epsilon;
/* 202*/              interpolator.setInterpolatedTime(ta);
/* 203*/              ga = this.handler.g(ta, interpolator.getInterpolatedState());
/*   0*/            } 
/* 205*/            if (ga * gb > 0.0D)
/* 207*/              throw MathRuntimeException.createInternalError(null); 
/*   0*/          } 
/* 212*/          this.increasing = (gb >= ga);
/* 214*/          UnivariateRealFunction f = new UnivariateRealFunction() {
/*   0*/              public double value(double t) throws FunctionEvaluationException {
/*   0*/                try {
/* 217*/                  interpolator.setInterpolatedTime(t);
/* 218*/                  return EventState.this.handler.g(t, interpolator.getInterpolatedState());
/* 219*/                } catch (DerivativeException e) {
/* 220*/                  throw new FunctionEvaluationException(e, t);
/* 221*/                } catch (EventException e) {
/* 222*/                  throw new FunctionEvaluationException(e, t);
/*   0*/                } 
/*   0*/              }
/*   0*/            };
/* 226*/          BrentSolver solver = new BrentSolver();
/* 227*/          solver.setAbsoluteAccuracy(this.convergence);
/* 228*/          solver.setMaximalIterationCount(this.maxIterationCount);
/* 229*/          double root = (ta <= tb) ? solver.solve(f, ta, tb) : solver.solve(f, tb, ta);
/* 230*/          if (Math.abs(root - ta) <= this.convergence && Math.abs(root - this.previousEventTime) <= this.convergence) {
/* 233*/            ta = tb;
/* 234*/            ga = gb;
/* 235*/          } else if (Double.isNaN(this.previousEventTime) || Math.abs(this.previousEventTime - root) > this.convergence) {
/* 237*/            this.pendingEventTime = root;
/* 238*/            if (this.pendingEvent && Math.abs(t1 - this.pendingEventTime) <= this.convergence)
/* 243*/              return false; 
/* 247*/            this.pendingEvent = true;
/* 248*/            return true;
/*   0*/          } 
/*   0*/        } else {
/* 253*/          ta = tb;
/* 254*/          ga = gb;
/*   0*/        } 
/*   0*/      } 
/* 260*/      this.pendingEvent = false;
/* 261*/      this.pendingEventTime = Double.NaN;
/* 262*/      return false;
/* 264*/    } catch (FunctionEvaluationException e) {
/* 265*/      Throwable cause = e.getCause();
/* 266*/      if (cause != null && cause instanceof DerivativeException)
/* 267*/        throw (DerivativeException)cause; 
/* 268*/      if (cause != null && cause instanceof EventException)
/* 269*/        throw (EventException)cause; 
/* 271*/      throw new EventException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getEventTime() {
/* 282*/    return this.pendingEventTime;
/*   0*/  }
/*   0*/  
/*   0*/  public void stepAccepted(double t, double[] y) throws EventException {
/* 296*/    this.t0 = t;
/* 297*/    this.g0 = this.handler.g(t, y);
/* 299*/    if (this.pendingEvent) {
/* 301*/      this.previousEventTime = t;
/* 302*/      this.g0Positive = this.increasing;
/* 303*/      this.nextAction = this.handler.eventOccurred(t, y, !(this.increasing ^ this.forward));
/*   0*/    } else {
/* 305*/      this.g0Positive = (this.g0 >= 0.0D);
/* 306*/      this.nextAction = 3;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean stop() {
/* 315*/    return (this.nextAction == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean reset(double t, double[] y) throws EventException {
/* 330*/    if (!this.pendingEvent)
/* 331*/      return false; 
/* 334*/    if (this.nextAction == 1)
/* 335*/      this.handler.resetState(t, y); 
/* 337*/    this.pendingEvent = false;
/* 338*/    this.pendingEventTime = Double.NaN;
/* 340*/    return (this.nextAction == 1 || this.nextAction == 2);
/*   0*/  }
/*   0*/}
