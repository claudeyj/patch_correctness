/*   0*/package org.apache.commons.math.ode;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import org.apache.commons.math.MaxEvaluationsExceededException;
/*   0*/import org.apache.commons.math.ode.events.CombinedEventsManager;
/*   0*/import org.apache.commons.math.ode.events.EventHandler;
/*   0*/import org.apache.commons.math.ode.events.EventState;
/*   0*/import org.apache.commons.math.ode.sampling.StepHandler;
/*   0*/
/*   0*/public abstract class AbstractIntegrator implements FirstOrderIntegrator {
/*   0*/  protected Collection<StepHandler> stepHandlers;
/*   0*/  
/*   0*/  protected double stepStart;
/*   0*/  
/*   0*/  protected double stepSize;
/*   0*/  
/*   0*/  protected CombinedEventsManager eventsHandlersManager;
/*   0*/  
/*   0*/  private final String name;
/*   0*/  
/*   0*/  private int maxEvaluations;
/*   0*/  
/*   0*/  private int evaluations;
/*   0*/  
/*   0*/  private transient FirstOrderDifferentialEquations equations;
/*   0*/  
/*   0*/  public AbstractIntegrator(String name) {
/*  65*/    this.name = name;
/*  66*/    this.stepHandlers = new ArrayList<StepHandler>();
/*  67*/    this.stepStart = Double.NaN;
/*  68*/    this.stepSize = Double.NaN;
/*  69*/    this.eventsHandlersManager = new CombinedEventsManager();
/*  70*/    setMaxEvaluations(-1);
/*  71*/    resetEvaluations();
/*   0*/  }
/*   0*/  
/*   0*/  protected AbstractIntegrator() {
/*  77*/    this(null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/*  82*/    return this.name;
/*   0*/  }
/*   0*/  
/*   0*/  public void addStepHandler(StepHandler handler) {
/*  87*/    this.stepHandlers.add(handler);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<StepHandler> getStepHandlers() {
/*  92*/    return Collections.unmodifiableCollection(this.stepHandlers);
/*   0*/  }
/*   0*/  
/*   0*/  public void clearStepHandlers() {
/*  97*/    this.stepHandlers.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public void addEventHandler(EventHandler function, double maxCheckInterval, double convergence, int maxIterationCount) {
/* 105*/    this.eventsHandlersManager.addEventHandler(function, maxCheckInterval, convergence, maxIterationCount);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<EventHandler> getEventHandlers() {
/* 111*/    return this.eventsHandlersManager.getEventsHandlers();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearEventHandlers() {
/* 116*/    this.eventsHandlersManager.clearEventsHandlers();
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean requiresDenseOutput() {
/* 123*/    for (StepHandler handler : this.stepHandlers) {
/* 124*/      if (handler.requiresDenseOutput())
/* 125*/        return true; 
/*   0*/    } 
/* 128*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public double getCurrentStepStart() {
/* 133*/    return this.stepStart;
/*   0*/  }
/*   0*/  
/*   0*/  public double getCurrentSignedStepsize() {
/* 138*/    return this.stepSize;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaxEvaluations(int maxEvaluations) {
/* 143*/    this.maxEvaluations = (maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxEvaluations() {
/* 148*/    return this.maxEvaluations;
/*   0*/  }
/*   0*/  
/*   0*/  public int getEvaluations() {
/* 153*/    return this.evaluations;
/*   0*/  }
/*   0*/  
/*   0*/  protected void resetEvaluations() {
/* 159*/    this.evaluations = 0;
/*   0*/  }
/*   0*/  
/*   0*/  protected void setEquations(FirstOrderDifferentialEquations equations) {
/* 167*/    this.equations = equations;
/*   0*/  }
/*   0*/  
/*   0*/  public void computeDerivatives(double t, double[] y, double[] yDot) throws DerivativeException {
/* 179*/    if (++this.evaluations > this.maxEvaluations)
/* 180*/      throw new DerivativeException(new MaxEvaluationsExceededException(this.maxEvaluations)); 
/* 182*/    this.equations.computeDerivatives(t, y, yDot);
/*   0*/  }
/*   0*/  
/*   0*/  protected void sanityChecks(FirstOrderDifferentialEquations ode, double t0, double[] y0, double t, double[] y) throws IntegratorException {
/* 198*/    if (ode.getDimension() != y0.length)
/* 199*/      throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0}, initial state vector has dimension {1}", new Object[] { ode.getDimension(), y0.length }); 
/* 205*/    if (ode.getDimension() != y.length)
/* 206*/      throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0}, final state vector has dimension {1}", new Object[] { ode.getDimension(), y.length }); 
/* 212*/    if (Math.abs(t - t0) <= 1.0E-12D * Math.max(Math.abs(t0), Math.abs(t)))
/* 213*/      throw new IntegratorException("too small integration interval: length = {0}", new Object[] { Math.abs(t - t0) }); 
/*   0*/  }
/*   0*/  
/*   0*/  protected CombinedEventsManager addEndTimeChecker(double startTime, double endTime, CombinedEventsManager manager) {
/* 233*/    CombinedEventsManager newManager = new CombinedEventsManager();
/* 234*/    for (EventState state : manager.getEventsStates())
/* 235*/      newManager.addEventHandler(state.getEventHandler(), state.getMaxCheckInterval(), state.getConvergence(), state.getMaxIterationCount()); 
/* 240*/    newManager.addEventHandler(new EndTimeChecker(endTime), Double.POSITIVE_INFINITY, Math.ulp(Math.max(Math.abs(startTime), Math.abs(endTime))), 100);
/* 244*/    return newManager;
/*   0*/  }
/*   0*/  
/*   0*/  private static class EndTimeChecker implements EventHandler {
/*   0*/    private final double endTime;
/*   0*/    
/*   0*/    public EndTimeChecker(double endTime) {
/* 257*/      this.endTime = endTime;
/*   0*/    }
/*   0*/    
/*   0*/    public int eventOccurred(double t, double[] y, boolean increasing) {
/* 262*/      return 0;
/*   0*/    }
/*   0*/    
/*   0*/    public double g(double t, double[] y) {
/* 267*/      return t - this.endTime;
/*   0*/    }
/*   0*/    
/*   0*/    public void resetState(double t, double[] y) {}
/*   0*/  }
/*   0*/}
