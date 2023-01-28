/*   0*/package org.apache.commons.math.ode.events;
/*   0*/
/*   0*/import org.apache.commons.math.ConvergenceException;
/*   0*/import org.apache.commons.math.FunctionEvaluationException;
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
/* 174*/      int n = Math.max(1, (int)Math.exp(Math.abs(t1 - this.t0) / this.maxCheckInterval));
/* 175*/      double h = (t1 - this.t0) / n;
/* 177*/      double ta = this.t0;
/* 178*/      double ga = this.g0;
/* 179*/      double tb = this.t0 + (interpolator.isForward() ? this.convergence : -this.convergence);
/* 180*/      for (int i = 0; i < n; i++) {
/* 183*/        tb += h;
/* 184*/        interpolator.setInterpolatedTime(tb);
/* 185*/        double gb = this.handler.g(tb, interpolator.getInterpolatedState());
/* 188*/        if (this.g0Positive ^ ((gb >= 0.0D))) {
/* 201*/          this.increasing = (gb >= ga);
/* 203*/          UnivariateRealFunction f = new UnivariateRealFunction() {
/*   0*/              public double value(double t) throws FunctionEvaluationException {
/*   0*/                try {
/* 206*/                  interpolator.setInterpolatedTime(t);
/* 207*/                  return EventState.this.handler.g(t, interpolator.getInterpolatedState());
/* 208*/                } catch (DerivativeException e) {
/* 209*/                  throw new FunctionEvaluationException(e, t);
/* 210*/                } catch (EventException e) {
/* 211*/                  throw new FunctionEvaluationException(e, t);
/*   0*/                } 
/*   0*/              }
/*   0*/            };
/* 215*/          BrentSolver solver = new BrentSolver();
/* 216*/          solver.setAbsoluteAccuracy(this.convergence);
/* 217*/          solver.setMaximalIterationCount(this.maxIterationCount);
/* 218*/          double root = (ta <= tb) ? solver.solve(f, ta, tb) : solver.solve(f, tb, ta);
/* 219*/          if (Math.abs(root - ta) <= this.convergence && Math.abs(root - this.previousEventTime) <= this.convergence) {
/* 222*/            ta = tb;
/* 223*/            ga = gb;
/* 224*/          } else if (Double.isNaN(this.previousEventTime) || Math.abs(this.previousEventTime - root) > this.convergence) {
/* 226*/            this.pendingEventTime = root;
/* 227*/            if (this.pendingEvent && Math.abs(t1 - this.pendingEventTime) <= this.convergence)
/* 232*/              return false; 
/* 236*/            this.pendingEvent = true;
/* 237*/            return true;
/*   0*/          } 
/*   0*/        } else {
/* 242*/          ta = tb;
/* 243*/          ga = gb;
/*   0*/        } 
/*   0*/      } 
/* 249*/      this.pendingEvent = false;
/* 250*/      this.pendingEventTime = Double.NaN;
/* 251*/      return false;
/* 253*/    } catch (FunctionEvaluationException e) {
/* 254*/      Throwable cause = e.getCause();
/* 255*/      if (cause != null && cause instanceof DerivativeException)
/* 256*/        throw (DerivativeException)cause; 
/* 257*/      if (cause != null && cause instanceof EventException)
/* 258*/        throw (EventException)cause; 
/* 260*/      throw new EventException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getEventTime() {
/* 271*/    return this.pendingEventTime;
/*   0*/  }
/*   0*/  
/*   0*/  public void stepAccepted(double t, double[] y) throws EventException {
/* 285*/    this.t0 = t;
/* 286*/    this.g0 = this.handler.g(t, y);
/* 288*/    if (this.pendingEvent) {
/* 290*/      this.previousEventTime = t;
/* 291*/      this.g0Positive = this.increasing;
/* 292*/      this.nextAction = this.handler.eventOccurred(t, y, !(this.increasing ^ this.forward));
/*   0*/    } else {
/* 294*/      this.g0Positive = (this.g0 >= 0.0D);
/* 295*/      this.nextAction = 3;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean stop() {
/* 304*/    return (this.nextAction == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean reset(double t, double[] y) throws EventException {
/* 319*/    if (!this.pendingEvent)
/* 320*/      return false; 
/* 323*/    if (this.nextAction == 1)
/* 324*/      this.handler.resetState(t, y); 
/* 326*/    this.pendingEvent = false;
/* 327*/    this.pendingEventTime = Double.NaN;
/* 329*/    return (this.nextAction == 1 || this.nextAction == 2);
/*   0*/  }
/*   0*/}
