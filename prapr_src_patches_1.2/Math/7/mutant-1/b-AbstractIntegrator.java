/*   0*/package org.apache.commons.math3.ode;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.SortedSet;
/*   0*/import java.util.TreeSet;
/*   0*/import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
/*   0*/import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
/*   0*/import org.apache.commons.math3.exception.DimensionMismatchException;
/*   0*/import org.apache.commons.math3.exception.MaxCountExceededException;
/*   0*/import org.apache.commons.math3.exception.NoBracketingException;
/*   0*/import org.apache.commons.math3.exception.NumberIsTooSmallException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.ode.events.EventHandler;
/*   0*/import org.apache.commons.math3.ode.events.EventState;
/*   0*/import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
/*   0*/import org.apache.commons.math3.ode.sampling.StepHandler;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/import org.apache.commons.math3.util.Incrementor;
/*   0*/import org.apache.commons.math3.util.Precision;
/*   0*/
/*   0*/public abstract class AbstractIntegrator implements FirstOrderIntegrator {
/*   0*/  protected Collection<StepHandler> stepHandlers;
/*   0*/  
/*   0*/  protected double stepStart;
/*   0*/  
/*   0*/  protected double stepSize;
/*   0*/  
/*   0*/  protected boolean isLastStep;
/*   0*/  
/*   0*/  protected boolean resetOccurred;
/*   0*/  
/*   0*/  private Collection<EventState> eventsStates;
/*   0*/  
/*   0*/  private boolean statesInitialized;
/*   0*/  
/*   0*/  private final String name;
/*   0*/  
/*   0*/  private Incrementor evaluations;
/*   0*/  
/*   0*/  private transient ExpandableStatefulODE expandable;
/*   0*/  
/*   0*/  public AbstractIntegrator(String name) {
/*  85*/    this.name = name;
/*  86*/    this.stepHandlers = new ArrayList<StepHandler>();
/*  87*/    this.stepStart = Double.NaN;
/*  88*/    this.stepSize = Double.NaN;
/*  89*/    this.eventsStates = new ArrayList<EventState>();
/*  90*/    this.statesInitialized = false;
/*  91*/    this.evaluations = new Incrementor();
/*  92*/    setMaxEvaluations(-1);
/*  93*/    this.evaluations.resetCount();
/*   0*/  }
/*   0*/  
/*   0*/  protected AbstractIntegrator() {
/*  99*/    this(null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/* 104*/    return this.name;
/*   0*/  }
/*   0*/  
/*   0*/  public void addStepHandler(StepHandler handler) {
/* 109*/    this.stepHandlers.add(handler);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<StepHandler> getStepHandlers() {
/* 114*/    return Collections.unmodifiableCollection(this.stepHandlers);
/*   0*/  }
/*   0*/  
/*   0*/  public void clearStepHandlers() {
/* 119*/    this.stepHandlers.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public void addEventHandler(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount) {
/* 127*/    addEventHandler(handler, this.stepStart, convergence, maxIterationCount, new BracketingNthOrderBrentSolver(convergence, 5));
/*   0*/  }
/*   0*/  
/*   0*/  public void addEventHandler(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount, UnivariateSolver solver) {
/* 138*/    this.eventsStates.add(new EventState(handler, maxCheckInterval, convergence, maxIterationCount, solver));
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<EventHandler> getEventHandlers() {
/* 144*/    List<EventHandler> list = new ArrayList<EventHandler>();
/* 145*/    for (EventState state : this.eventsStates)
/* 146*/      list.add(state.getEventHandler()); 
/* 148*/    return Collections.unmodifiableCollection(list);
/*   0*/  }
/*   0*/  
/*   0*/  public void clearEventHandlers() {
/* 153*/    this.eventsStates.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public double getCurrentStepStart() {
/* 158*/    return this.stepStart;
/*   0*/  }
/*   0*/  
/*   0*/  public double getCurrentSignedStepsize() {
/* 163*/    return this.stepSize;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaxEvaluations(int maxEvaluations) {
/* 168*/    this.evaluations.setMaximalCount((maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations);
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxEvaluations() {
/* 173*/    return this.evaluations.getMaximalCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getEvaluations() {
/* 178*/    return this.evaluations.getCount();
/*   0*/  }
/*   0*/  
/*   0*/  protected void initIntegration(double t0, double[] y0, double t) {
/* 188*/    this.evaluations.resetCount();
/* 190*/    for (EventState state : this.eventsStates)
/* 191*/      state.getEventHandler().init(t0, y0, t); 
/* 194*/    for (StepHandler handler : this.stepHandlers)
/* 195*/      handler.init(t0, y0, t); 
/* 198*/    setStateInitialized(false);
/*   0*/  }
/*   0*/  
/*   0*/  protected void setEquations(ExpandableStatefulODE equations) {
/* 206*/    this.expandable = equations;
/*   0*/  }
/*   0*/  
/*   0*/  public double integrate(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
/* 215*/    if (y0.length != equations.getDimension())
/* 216*/      throw new DimensionMismatchException(y0.length, equations.getDimension()); 
/* 218*/    if (y.length != equations.getDimension())
/* 219*/      throw new DimensionMismatchException(y.length, equations.getDimension()); 
/* 223*/    ExpandableStatefulODE expandableODE = new ExpandableStatefulODE(equations);
/* 224*/    expandableODE.setTime(t0);
/* 225*/    expandableODE.setPrimaryState(y0);
/* 228*/    integrate(expandableODE, t);
/* 231*/    System.arraycopy(expandableODE.getPrimaryState(), 0, y, 0, y.length);
/* 232*/    return expandableODE.getTime();
/*   0*/  }
/*   0*/  
/*   0*/  public abstract void integrate(ExpandableStatefulODE paramExpandableStatefulODE, double paramDouble) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;
/*   0*/  
/*   0*/  public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
/* 268*/    this.evaluations.incrementCount();
/* 269*/    this.expandable.computeDerivatives(t, y, yDot);
/*   0*/  }
/*   0*/  
/*   0*/  protected void setStateInitialized(boolean stateInitialized) {
/* 280*/    this.statesInitialized = stateInitialized;
/*   0*/  }
/*   0*/  
/*   0*/  protected double acceptStep(AbstractStepInterpolator interpolator, double[] y, double[] yDot, double tEnd) throws MaxCountExceededException, DimensionMismatchException, NoBracketingException {
/* 300*/    double previousT = interpolator.getGlobalPreviousTime();
/* 301*/    double currentT = interpolator.getGlobalCurrentTime();
/* 304*/    if (!this.statesInitialized) {
/* 305*/      for (EventState state : this.eventsStates)
/* 306*/        state.reinitializeBegin(interpolator); 
/* 308*/      this.statesInitialized = true;
/*   0*/    } 
/* 312*/    final int orderingSign = interpolator.isForward() ? 1 : -1;
/* 313*/    SortedSet<EventState> occuringEvents = new TreeSet<EventState>(new Comparator<EventState>() {
/*   0*/          public int compare(EventState es0, EventState es1) {
/* 317*/            return orderingSign * Double.compare(es0.getEventTime(), es1.getEventTime());
/*   0*/          }
/*   0*/        });
/* 322*/    for (EventState state : this.eventsStates) {
/* 323*/      if (state.evaluateStep(interpolator))
/* 325*/        occuringEvents.add(state); 
/*   0*/    } 
/* 329*/    while (!occuringEvents.isEmpty()) {
/* 332*/      Iterator<EventState> iterator = occuringEvents.iterator();
/* 333*/      EventState currentEvent = iterator.next();
/* 334*/      iterator.remove();
/* 337*/      double eventT = currentEvent.getEventTime();
/* 338*/      interpolator.setSoftPreviousTime(previousT);
/* 339*/      interpolator.setSoftCurrentTime(eventT);
/* 342*/      interpolator.setInterpolatedTime(eventT);
/* 343*/      double[] eventY = (double[])interpolator.getInterpolatedState().clone();
/* 346*/      currentEvent.stepAccepted(eventT, eventY);
/* 347*/      this.isLastStep = currentEvent.stop();
/* 350*/      for (StepHandler handler : this.stepHandlers)
/* 351*/        handler.handleStep(interpolator, this.isLastStep); 
/* 354*/      if (this.isLastStep) {
/* 356*/        System.arraycopy(eventY, 0, y, 0, y.length);
/* 357*/        for (EventState remaining : occuringEvents)
/* 358*/          remaining.stepAccepted(eventT, eventY); 
/* 360*/        return eventT;
/*   0*/      } 
/* 363*/      boolean needReset = currentEvent.reset(eventT, eventY);
/* 364*/      if (needReset) {
/* 367*/        System.arraycopy(eventY, 0, y, 0, y.length);
/* 368*/        computeDerivatives(eventT, y, yDot);
/* 369*/        this.resetOccurred = true;
/* 370*/        for (EventState remaining : occuringEvents)
/* 371*/          remaining.stepAccepted(eventT, eventY); 
/* 373*/        return eventT;
/*   0*/      } 
/* 377*/      previousT = eventT;
/* 378*/      interpolator.setSoftPreviousTime(eventT);
/* 379*/      interpolator.setSoftCurrentTime(currentT);
/* 382*/      if (currentEvent.evaluateStep(interpolator))
/* 384*/        occuringEvents.add(currentEvent); 
/*   0*/    } 
/* 390*/    interpolator.setInterpolatedTime(currentT);
/* 391*/    double[] currentY = interpolator.getInterpolatedState();
/* 392*/    for (EventState state : this.eventsStates) {
/* 393*/      state.stepAccepted(currentT, currentY);
/* 394*/      this.isLastStep = (this.isLastStep || state.stop());
/*   0*/    } 
/* 396*/    this.isLastStep = (this.isLastStep || Precision.equals(currentT, tEnd, 1));
/* 399*/    for (StepHandler handler : this.stepHandlers)
/* 400*/      handler.handleStep(interpolator, this.isLastStep); 
/* 403*/    return currentT;
/*   0*/  }
/*   0*/  
/*   0*/  protected void sanityChecks(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException {
/* 417*/    double threshold = 1000.0D * FastMath.ulp(FastMath.max(FastMath.abs(equations.getTime()), FastMath.abs(t)));
/* 419*/    double dt = FastMath.abs(equations.getTime() - t);
/* 420*/    if (dt <= threshold)
/* 421*/      throw new NumberIsTooSmallException(LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL, dt, threshold, false); 
/*   0*/  }
/*   0*/}
