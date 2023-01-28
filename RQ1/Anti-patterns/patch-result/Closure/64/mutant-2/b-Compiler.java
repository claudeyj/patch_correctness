/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Supplier;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.jscomp.deps.SortedDependencies;
/*   0*/import com.google.javascript.jscomp.parsing.Config;
/*   0*/import com.google.javascript.jscomp.parsing.ParserRunner;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import java.io.IOException;
/*   0*/import java.io.PrintStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.concurrent.Callable;
/*   0*/import java.util.logging.Level;
/*   0*/import java.util.logging.Logger;
/*   0*/
/*   0*/public class Compiler extends AbstractCompiler {
/*  70*/  static final DiagnosticType MODULE_DEPENDENCY_ERROR = DiagnosticType.error("JSC_MODULE_DEPENDENCY_ERROR", "Bad dependency: {0} -> {1}. Modules must be listed in dependency order.");
/*   0*/  
/*  75*/  static final DiagnosticType MISSING_ENTRY_ERROR = DiagnosticType.error("JSC_MISSING_ENTRY_ERROR", "required entry point \"{0}\" never provided");
/*   0*/  
/*  79*/  CompilerOptions options = null;
/*   0*/  
/*  81*/  private PassConfig passes = null;
/*   0*/  
/*   0*/  private List<CompilerInput> externs;
/*   0*/  
/*   0*/  private List<JSModule> modules;
/*   0*/  
/*   0*/  private JSModuleGraph moduleGraph;
/*   0*/  
/*   0*/  private List<CompilerInput> inputs;
/*   0*/  
/*   0*/  private ErrorManager errorManager;
/*   0*/  
/*   0*/  private WarningsGuard warningsGuard;
/*   0*/  
/*   0*/  Node externsRoot;
/*   0*/  
/*   0*/  Node jsRoot;
/*   0*/  
/*   0*/  Node externAndJsRoot;
/*   0*/  
/*   0*/  private Map<String, CompilerInput> inputsByName;
/*   0*/  
/*   0*/  private SourceMap sourceMap;
/*   0*/  
/* 113*/  private String externExports = null;
/*   0*/  
/* 119*/  private int uniqueNameId = 0;
/*   0*/  
/*   0*/  private boolean useThreads = true;
/*   0*/  
/*   0*/  private boolean hasRegExpGlobalReferences = true;
/*   0*/  
/*   0*/  private FunctionInformationMap functionInformationMap;
/*   0*/  
/* 134*/  private final StringBuilder debugLog = new StringBuilder();
/*   0*/  
/* 137*/  CodingConvention defaultCodingConvention = new ClosureCodingConvention();
/*   0*/  
/*   0*/  private JSTypeRegistry typeRegistry;
/*   0*/  
/* 140*/  private Config parserConfig = null;
/*   0*/  
/*   0*/  private ReverseAbstractInterpreter abstractInterpreter;
/*   0*/  
/*   0*/  private TypeValidator typeValidator;
/*   0*/  
/*   0*/  public PerformanceTracker tracker;
/*   0*/  
/* 148*/  private final ErrorReporter oldErrorReporter = RhinoErrorReporter.forOldRhino(this);
/*   0*/  
/* 152*/  private final com.google.javascript.jscomp.mozilla.rhino.ErrorReporter defaultErrorReporter = RhinoErrorReporter.forNewRhino(this);
/*   0*/  
/* 156*/  public static final DiagnosticType OPTIMIZE_LOOP_ERROR = DiagnosticType.error("JSC_OPTIMIZE_LOOP_ERROR", "Exceeded max number of optimization iterations: {0}");
/*   0*/  
/* 159*/  public static final DiagnosticType MOTION_ITERATIONS_ERROR = DiagnosticType.error("JSC_OPTIMIZE_LOOP_ERROR", "Exceeded max number of code motion iterations: {0}");
/*   0*/  
/*   0*/  private static final long COMPILER_STACK_SIZE = 1048576L;
/*   0*/  
/* 171*/  private static final Logger logger = Logger.getLogger("com.google.javascript.jscomp");
/*   0*/  
/*   0*/  private final PrintStream outStream;
/*   0*/  
/* 176*/  private GlobalVarReferenceMap globalRefMap = null;
/*   0*/  
/*   0*/  public Compiler() {
/* 182*/    this((PrintStream)null);
/*   0*/  }
/*   0*/  
/*   0*/  public Compiler(ErrorManager errorManager) {
/* 198*/    this();
/* 199*/    setErrorManager(errorManager);
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorManager(ErrorManager errorManager) {
/* 208*/    Preconditions.checkNotNull(errorManager, "the error manager cannot be null");
/* 210*/    this.errorManager = errorManager;
/*   0*/  }
/*   0*/  
/*   0*/  private MessageFormatter createMessageFormatter() {
/* 218*/    boolean colorize = this.options.shouldColorizeErrorOutput();
/* 219*/    return this.options.errorFormat.toFormatter(this, colorize);
/*   0*/  }
/*   0*/  
/*   0*/  public void initOptions(CompilerOptions options) {
/* 227*/    this.options = options;
/* 228*/    if (this.errorManager == null)
/* 229*/      if (this.outStream == null) {
/* 230*/        setErrorManager(new LoggerErrorManager(createMessageFormatter(), logger));
/*   0*/      } else {
/* 233*/        PrintStreamErrorManager printer = new PrintStreamErrorManager(createMessageFormatter(), this.outStream);
/* 235*/        printer.setSummaryDetailLevel(options.summaryDetailLevel);
/* 236*/        setErrorManager(printer);
/*   0*/      }  
/* 241*/    if (options.enables(DiagnosticGroups.CHECK_TYPES)) {
/* 242*/      options.checkTypes = true;
/* 243*/    } else if (options.disables(DiagnosticGroups.CHECK_TYPES)) {
/* 244*/      options.checkTypes = false;
/* 245*/    } else if (!options.checkTypes) {
/* 249*/      options.setWarningLevel(DiagnosticGroup.forType(RhinoErrorReporter.TYPE_PARSE_ERROR), CheckLevel.OFF);
/*   0*/    } 
/* 255*/    if (options.checkGlobalThisLevel.isOn())
/* 256*/      options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, options.checkGlobalThisLevel); 
/* 262*/    List<WarningsGuard> guards = Lists.newArrayList();
/* 263*/    guards.add(new SuppressDocWarningsGuard(getDiagnosticGroups().getRegisteredGroups()));
/* 266*/    guards.add(options.getWarningsGuard());
/* 268*/    ComposeWarningsGuard composedGuards = new ComposeWarningsGuard(guards);
/* 275*/    if (!options.checkSymbols && !composedGuards.enables(DiagnosticGroups.CHECK_VARIABLES))
/* 277*/      composedGuards.addGuard(new DiagnosticGroupWarningsGuard(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF)); 
/* 281*/    this.warningsGuard = composedGuards;
/*   0*/  }
/*   0*/  
/*   0*/  public void init(JSSourceFile[] externs, JSSourceFile[] inputs, CompilerOptions options) {
/* 289*/    init(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])inputs), options);
/*   0*/  }
/*   0*/  
/*   0*/  public void init(List<JSSourceFile> externs, List<JSSourceFile> inputs, CompilerOptions options) {
/* 298*/    JSModule module = new JSModule("[singleton]");
/* 299*/    for (JSSourceFile input : inputs)
/* 300*/      module.add(input); 
/* 303*/    initModules(externs, Lists.newArrayList((Object[])new JSModule[] { module }), options);
/*   0*/  }
/*   0*/  
/*   0*/  public void init(JSSourceFile[] externs, JSModule[] modules, CompilerOptions options) {
/* 312*/    initModules(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public void initModules(List<JSSourceFile> externs, List<JSModule> modules, CompilerOptions options) {
/* 323*/    initOptions(options);
/* 325*/    checkFirstModule(modules);
/* 326*/    fillEmptyModules(modules);
/* 328*/    this.externs = makeCompilerInput(externs, true);
/* 332*/    this.modules = modules;
/* 333*/    if (modules.size() > 1) {
/*   0*/      try {
/* 335*/        this.moduleGraph = new JSModuleGraph(modules);
/* 336*/      } catch (JSModuleGraph.ModuleDependenceException e) {
/* 339*/        report(JSError.make(MODULE_DEPENDENCY_ERROR, new String[] { e.getModule().getName(), e.getDependentModule().getName() }));
/*   0*/        return;
/*   0*/      } 
/*   0*/    } else {
/* 344*/      this.moduleGraph = null;
/*   0*/    } 
/* 347*/    this.inputs = getAllInputsFromModules(modules);
/* 348*/    initBasedOnOptions();
/* 350*/    initInputsByNameMap();
/*   0*/  }
/*   0*/  
/*   0*/  private void initBasedOnOptions() {
/* 358*/    if (this.options.sourceMapOutputPath != null)
/* 359*/      this.sourceMap = this.options.sourceMapFormat.getInstance(); 
/*   0*/  }
/*   0*/  
/*   0*/  private List<CompilerInput> makeCompilerInput(List<JSSourceFile> files, boolean isExtern) {
/* 365*/    List<CompilerInput> inputs = Lists.newArrayList();
/* 366*/    for (JSSourceFile file : files)
/* 367*/      inputs.add(new CompilerInput(file, isExtern)); 
/* 369*/    return inputs;
/*   0*/  }
/*   0*/  
/* 372*/  private static final DiagnosticType EMPTY_MODULE_LIST_ERROR = DiagnosticType.error("JSC_EMPTY_MODULE_LIST_ERROR", "At least one module must be provided");
/*   0*/  
/* 376*/  private static final DiagnosticType EMPTY_ROOT_MODULE_ERROR = DiagnosticType.error("JSC_EMPTY_ROOT_MODULE_ERROR", "Root module '{0}' must contain at least one source code input");
/*   0*/  
/*   0*/  private void checkFirstModule(List<JSModule> modules) {
/* 385*/    if (modules.isEmpty()) {
/* 386*/      report(JSError.make(EMPTY_MODULE_LIST_ERROR, new String[0]));
/* 387*/    } else if (((JSModule)modules.get(0)).getInputs().isEmpty() && modules.size() > 1) {
/* 389*/      report(JSError.make(EMPTY_ROOT_MODULE_ERROR, new String[] { ((JSModule)modules.get(0)).getName() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void fillEmptyModules(List<JSModule> modules) {
/* 399*/    for (JSModule module : modules) {
/* 400*/      if (module.getInputs().isEmpty())
/* 401*/        module.add(JSSourceFile.fromCode("[" + module.getName() + "]", "")); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void rebuildInputsFromModules() {
/* 412*/    this.inputs = getAllInputsFromModules(this.modules);
/* 413*/    initInputsByNameMap();
/*   0*/  }
/*   0*/  
/*   0*/  private static List<CompilerInput> getAllInputsFromModules(List<JSModule> modules) {
/* 422*/    List<CompilerInput> inputs = Lists.newArrayList();
/* 423*/    Map<String, JSModule> inputMap = Maps.newHashMap();
/* 424*/    for (JSModule module : modules) {
/* 425*/      for (CompilerInput input : module.getInputs()) {
/* 426*/        String inputName = input.getName();
/* 431*/        inputs.add(input);
/* 432*/        inputMap.put(inputName, module);
/*   0*/      } 
/*   0*/    } 
/* 435*/    return inputs;
/*   0*/  }
/*   0*/  
/* 438*/  static final DiagnosticType DUPLICATE_INPUT = DiagnosticType.error("JSC_DUPLICATE_INPUT", "Duplicate input: {0}");
/*   0*/  
/* 440*/  static final DiagnosticType DUPLICATE_EXTERN_INPUT = DiagnosticType.error("JSC_DUPLICATE_EXTERN_INPUT", "Duplicate extern input: {0}");
/*   0*/  
/*   0*/  private final PassFactory sanityCheck;
/*   0*/  
/*   0*/  private Tracer currentTracer;
/*   0*/  
/*   0*/  private String currentPassName;
/*   0*/  
/*   0*/  protected final CodeChangeHandler.RecentChange recentChange;
/*   0*/  
/*   0*/  private final List<CodeChangeHandler> codeChangeHandlers;
/*   0*/  
/*   0*/  void initInputsByNameMap() {
/* 449*/    this.inputsByName = new HashMap<String, CompilerInput>();
/* 450*/    for (CompilerInput input : this.externs) {
/* 451*/      String name = input.getName();
/* 452*/      if (!this.inputsByName.containsKey(name)) {
/* 453*/        this.inputsByName.put(name, input);
/*   0*/        continue;
/*   0*/      } 
/* 455*/      report(JSError.make(DUPLICATE_EXTERN_INPUT, new String[] { name }));
/*   0*/    } 
/* 458*/    for (CompilerInput input : this.inputs) {
/* 459*/      String name = input.getName();
/* 460*/      if (!this.inputsByName.containsKey(name)) {
/* 461*/        this.inputsByName.put(name, input);
/*   0*/        continue;
/*   0*/      } 
/* 463*/      report(JSError.make(DUPLICATE_INPUT, new String[] { name }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile extern, JSSourceFile input, CompilerOptions options) {
/* 470*/    return compile(extern, new JSSourceFile[] { input }, options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile extern, JSSourceFile[] input, CompilerOptions options) {
/* 475*/    return compile(new JSSourceFile[] { extern }, input, options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile extern, JSModule[] modules, CompilerOptions options) {
/* 480*/    return compile(new JSSourceFile[] { extern }, modules, options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile[] externs, JSSourceFile[] inputs, CompilerOptions options) {
/* 489*/    return compile(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])inputs), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(List<JSSourceFile> externs, List<JSSourceFile> inputs, CompilerOptions options) {
/* 500*/    Preconditions.checkState((this.jsRoot == null));
/*   0*/    try {
/* 503*/      init(externs, inputs, options);
/* 504*/      if (hasErrors())
/* 505*/        return getResult(); 
/* 507*/      return compile();
/*   0*/    } finally {
/* 509*/      Tracer t = newTracer("generateReport");
/* 510*/      this.errorManager.generateReport();
/* 511*/      stopTracer(t, "generateReport");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile[] externs, JSModule[] modules, CompilerOptions options) {
/* 521*/    return compileModules(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compileModules(List<JSSourceFile> externs, List<JSModule> modules, CompilerOptions options) {
/* 532*/    Preconditions.checkState((this.jsRoot == null));
/*   0*/    try {
/* 535*/      initModules(externs, modules, options);
/* 536*/      if (hasErrors())
/* 537*/        return getResult(); 
/* 539*/      return compile();
/*   0*/    } finally {
/* 541*/      Tracer t = newTracer("generateReport");
/* 542*/      this.errorManager.generateReport();
/* 543*/      stopTracer(t, "generateReport");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private Result compile() {
/* 548*/    return runInCompilerThread(new Callable<Result>() {
/*   0*/          public Result call() throws Exception {
/* 550*/            Compiler.this.compileInternal();
/* 551*/            return Compiler.this.getResult();
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public void disableThreads() {
/* 561*/    this.useThreads = false;
/*   0*/  }
/*   0*/  
/*   0*/  private <T> T runInCompilerThread(Callable<T> callable) {
/* 565*/    return runCallable(callable, this.useThreads, this.options.tracer.isOn());
/*   0*/  }
/*   0*/  
/*   0*/  static <T> T runCallableWithLargeStack(Callable<T> callable) {
/* 569*/    return runCallable(callable, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  static <T> T runCallable(final Callable<T> callable, boolean useLargeStackThread, boolean trace) {
/* 582*/    final boolean dumpTraceReport = trace;
/* 583*/    final Object[] result = new Object[1];
/* 584*/    final Throwable[] exception = new Throwable[1];
/* 585*/    Runnable runnable = new Runnable() {
/*   0*/        public void run() {
/*   0*/          try {
/* 588*/            if (dumpTraceReport)
/* 589*/              Tracer.initCurrentThreadTrace(); 
/* 591*/            result[0] = callable.call();
/* 592*/          } catch (Throwable e) {
/* 593*/            exception[0] = e;
/*   0*/          } finally {
/* 595*/            if (dumpTraceReport)
/* 596*/              Tracer.logAndClearCurrentThreadTrace(); 
/*   0*/          } 
/*   0*/        }
/*   0*/      };
/* 602*/    if (useLargeStackThread) {
/* 603*/      Thread th = new Thread(null, runnable, "jscompiler", 1048576L);
/* 604*/      th.start();
/*   0*/      while (true) {
/*   0*/        try {
/* 607*/          th.join();
/*   0*/          break;
/* 609*/        } catch (InterruptedException interruptedException) {}
/*   0*/      } 
/*   0*/    } else {
/* 614*/      runnable.run();
/*   0*/    } 
/* 618*/    if (exception[0] != null)
/* 619*/      throw new RuntimeException(exception[0]); 
/* 622*/    return (T)result[0];
/*   0*/  }
/*   0*/  
/*   0*/  private void compileInternal() {
/* 626*/    parse();
/* 627*/    if (hasErrors())
/*   0*/      return; 
/* 631*/    if (!precheck())
/*   0*/      return; 
/* 635*/    if (this.options.nameAnonymousFunctionsOnly) {
/* 637*/      check();
/*   0*/      return;
/*   0*/    } 
/* 641*/    if (!this.options.skipAllPasses) {
/* 642*/      check();
/* 643*/      if (hasErrors())
/*   0*/        return; 
/* 647*/      if (this.options.isExternExportsEnabled() || this.options.externExportsPath != null)
/* 649*/        externExports(); 
/* 653*/      if (!this.options.ideMode)
/* 654*/        optimize(); 
/*   0*/    } 
/* 658*/    if (this.options.recordFunctionInformation)
/* 659*/      recordFunctionInformation(); 
/* 662*/    if (this.options.devMode == CompilerOptions.DevMode.START_AND_END)
/* 663*/      runSanityCheck(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void parse() {
/* 668*/    parseInputs();
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig getPassConfig() {
/* 672*/    if (this.passes == null)
/* 673*/      this.passes = createPassConfigInternal(); 
/* 675*/    return this.passes;
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig createPassConfigInternal() {
/* 683*/    return new DefaultPassConfig(this.options);
/*   0*/  }
/*   0*/  
/*   0*/  public void setPassConfig(PassConfig passes) {
/* 697*/    Preconditions.checkNotNull(passes);
/* 699*/    if (this.passes != null)
/* 700*/      throw new IllegalStateException("this.passes has already been assigned"); 
/* 702*/    this.passes = passes;
/*   0*/  }
/*   0*/  
/*   0*/  boolean precheck() {
/* 712*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void check() {
/* 716*/    runCustomPasses(CustomPassExecutionTime.BEFORE_CHECKS);
/* 718*/    PhaseOptimizer phaseOptimizer = new PhaseOptimizer(this, this.tracker);
/* 719*/    if (this.options.devMode == CompilerOptions.DevMode.EVERY_PASS)
/* 720*/      phaseOptimizer.setSanityCheck(this.sanityCheck); 
/* 722*/    phaseOptimizer.consume(getPassConfig().getChecks());
/* 723*/    phaseOptimizer.process(this.externsRoot, this.jsRoot);
/* 724*/    if (hasErrors())
/*   0*/      return; 
/* 729*/    if (this.options.nameAnonymousFunctionsOnly)
/*   0*/      return; 
/* 733*/    if (this.options.removeTryCatchFinally)
/* 734*/      removeTryCatchFinally(); 
/* 737*/    if (this.options.getTweakProcessing().shouldStrip() || !this.options.stripTypes.isEmpty() || !this.options.stripNameSuffixes.isEmpty() || !this.options.stripTypePrefixes.isEmpty() || !this.options.stripNamePrefixes.isEmpty())
/* 742*/      stripCode(this.options.stripTypes, this.options.stripNameSuffixes, this.options.stripTypePrefixes, this.options.stripNamePrefixes); 
/* 746*/    runCustomPasses(CustomPassExecutionTime.BEFORE_OPTIMIZATIONS);
/*   0*/  }
/*   0*/  
/*   0*/  private void externExports() {
/* 750*/    logger.info("Creating extern file for exports");
/* 751*/    startPass("externExports");
/* 753*/    ExternExportsPass pass = new ExternExportsPass(this);
/* 754*/    process(pass);
/* 756*/    this.externExports = pass.getGeneratedExterns();
/* 758*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void process(CompilerPass p) {
/* 763*/    p.process(this.externsRoot, this.jsRoot);
/*   0*/  }
/*   0*/  
/*   0*/  public Compiler(PrintStream stream) {
/* 766*/    this.sanityCheck = new PassFactory("sanityCheck", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 770*/          return new SanityCheck(compiler);
/*   0*/        }
/*   0*/      };
/* 828*/    this.currentTracer = null;
/* 829*/    this.currentPassName = null;
/*1635*/    this.recentChange = new CodeChangeHandler.RecentChange();
/*1637*/    this.codeChangeHandlers = Lists.newArrayList();
/*   0*/    addChangeHandler(this.recentChange);
/*   0*/    this.outStream = stream;
/*   0*/  }
/*   0*/  
/*   0*/  private void maybeSanityCheck() {
/*   0*/    if (this.options.devMode == CompilerOptions.DevMode.EVERY_PASS)
/*   0*/      runSanityCheck(); 
/*   0*/  }
/*   0*/  
/*   0*/  private void runSanityCheck() {
/*   0*/    this.sanityCheck.create(this).process(this.externsRoot, this.jsRoot);
/*   0*/  }
/*   0*/  
/*   0*/  void removeTryCatchFinally() {
/*   0*/    logger.info("Remove try/catch/finally");
/*   0*/    startPass("removeTryCatchFinally");
/*   0*/    RemoveTryCatch r = new RemoveTryCatch(this);
/*   0*/    process(r);
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void stripCode(Set<String> stripTypes, Set<String> stripNameSuffixes, Set<String> stripTypePrefixes, Set<String> stripNamePrefixes) {
/*   0*/    logger.info("Strip code");
/*   0*/    startPass("stripCode");
/*   0*/    StripCode r = new StripCode(this, stripTypes, stripNameSuffixes, stripTypePrefixes, stripNamePrefixes);
/*   0*/    if (this.options.getTweakProcessing().shouldStrip())
/*   0*/      r.enableTweakStripping(); 
/*   0*/    process(r);
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  private void runCustomPasses(CustomPassExecutionTime executionTime) {
/*   0*/    if (this.options.customPasses != null) {
/*   0*/      Tracer t = newTracer("runCustomPasses");
/*   0*/      try {
/*   0*/        for (CompilerPass p : (Iterable<CompilerPass>)this.options.customPasses.get(executionTime))
/*   0*/          process(p); 
/*   0*/      } finally {
/*   0*/        stopTracer(t, "runCustomPasses");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void startPass(String passName) {
/*   0*/    Preconditions.checkState((this.currentTracer == null));
/*   0*/    this.currentPassName = passName;
/*   0*/    this.currentTracer = newTracer(passName);
/*   0*/  }
/*   0*/  
/*   0*/  void endPass() {
/*   0*/    Preconditions.checkState((this.currentTracer != null), "Tracer should not be null at the end of a pass.");
/*   0*/    stopTracer(this.currentTracer, this.currentPassName);
/*   0*/    String passToCheck = this.currentPassName;
/*   0*/    this.currentPassName = null;
/*   0*/    this.currentTracer = null;
/*   0*/    maybeSanityCheck();
/*   0*/  }
/*   0*/  
/*   0*/  Tracer newTracer(String passName) {
/*   0*/    String comment = passName + (this.recentChange.hasCodeChanged() ? " on recently changed AST" : "");
/*   0*/    if (this.options.tracer.isOn())
/*   0*/      this.tracker.recordPassStart(passName); 
/*   0*/    return new Tracer("Compiler", comment);
/*   0*/  }
/*   0*/  
/*   0*/  void stopTracer(Tracer t, String passName) {
/*   0*/    long result = t.stop();
/*   0*/    if (this.options.tracer.isOn())
/*   0*/      this.tracker.recordPassStop(passName, result); 
/*   0*/  }
/*   0*/  
/*   0*/  public Result getResult() {
/*   0*/    PassConfig.State state = getPassConfig().getIntermediateState();
/*   0*/    return new Result(getErrors(), getWarnings(), this.debugLog.toString(), state.variableMap, state.propertyMap, state.anonymousFunctionNameMap, state.stringMap, this.functionInformationMap, this.sourceMap, this.externExports, state.cssNames, state.idGeneratorMap);
/*   0*/  }
/*   0*/  
/*   0*/  public JSError[] getMessages() {
/*   0*/    return getErrors();
/*   0*/  }
/*   0*/  
/*   0*/  public JSError[] getErrors() {
/*   0*/    return this.errorManager.getErrors();
/*   0*/  }
/*   0*/  
/*   0*/  public JSError[] getWarnings() {
/*   0*/    return this.errorManager.getWarnings();
/*   0*/  }
/*   0*/  
/*   0*/  public Node getRoot() {
/*   0*/    return this.externAndJsRoot;
/*   0*/  }
/*   0*/  
/*   0*/  private int nextUniqueNameId() {
/*   0*/    return this.uniqueNameId++;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  void resetUniqueNameId() {
/*   0*/    this.uniqueNameId = 0;
/*   0*/  }
/*   0*/  
/*   0*/  Supplier<String> getUniqueNameIdSupplier() {
/*   0*/    final Compiler self = this;
/*   0*/    return new Supplier<String>() {
/*   0*/        public String get() {
/*   0*/          return String.valueOf(self.nextUniqueNameId());
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  boolean areNodesEqualForInlining(Node n1, Node n2) {
/*   0*/    if (this.options.ambiguateProperties || this.options.disambiguateProperties)
/*   0*/      return n1.isEquivalentToTyped(n2); 
/*   0*/    return n1.isEquivalentTo(n2);
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerInput getInput(String name) {
/*   0*/    return this.inputsByName.get(name);
/*   0*/  }
/*   0*/  
/*   0*/  protected void removeInput(String name) {
/*   0*/    CompilerInput input = getInput(name);
/*   0*/    if (input == null)
/*   0*/      return; 
/*   0*/    this.inputsByName.remove(name);
/*   0*/    Node root = input.getAstRoot(this);
/*   0*/    if (root != null)
/*   0*/      root.detachFromParent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerInput newExternInput(String name) {
/*   0*/    if (this.inputsByName.containsKey(name))
/*   0*/      throw new IllegalArgumentException("Conflicting externs name: " + name); 
/*   0*/    SourceAst ast = new SyntheticAst(name);
/*   0*/    CompilerInput input = new CompilerInput(ast, name, true);
/*   0*/    this.inputsByName.put(name, input);
/*   0*/    this.externsRoot.addChildToFront(ast.getAstRoot(this));
/*   0*/    return input;
/*   0*/  }
/*   0*/  
/*   0*/  void addIncrementalSourceAst(JsAst ast) {
/*   0*/    String sourceName = ast.getSourceFile().getName();
/*   0*/    Preconditions.checkState((getInput(sourceName) == null), "Duplicate input of name " + sourceName);
/*   0*/    this.inputsByName.put(sourceName, new CompilerInput(ast));
/*   0*/  }
/*   0*/  
/*   0*/  boolean replaceIncrementalSourceAst(JsAst ast) {
/*   0*/    String sourceName = ast.getSourceFile().getName();
/*   0*/    CompilerInput oldInput = (CompilerInput)Preconditions.checkNotNull(getInput(sourceName), "No input to replace: " + sourceName);
/*   0*/    Node newRoot = ast.getAstRoot(this);
/*   0*/    if (newRoot == null)
/*   0*/      return false; 
/*   0*/    Node oldRoot = oldInput.getAstRoot(this);
/*   0*/    if (oldRoot != null) {
/*   0*/      oldRoot.getParent().replaceChild(oldRoot, newRoot);
/*   0*/    } else {
/*   0*/      getRoot().getLastChild().addChildToBack(newRoot);
/*   0*/    } 
/*   0*/    CompilerInput newInput = new CompilerInput(ast);
/*   0*/    this.inputsByName.put(sourceName, newInput);
/*   0*/    JSModule module = oldInput.getModule();
/*   0*/    if (module != null) {
/*   0*/      module.addAfter(newInput, oldInput);
/*   0*/      module.remove(oldInput);
/*   0*/    } 
/*   0*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  JSModuleGraph getModuleGraph() {
/*   0*/    return this.moduleGraph;
/*   0*/  }
/*   0*/  
/*   0*/  public JSTypeRegistry getTypeRegistry() {
/*   0*/    if (this.typeRegistry == null)
/*   0*/      this.typeRegistry = new JSTypeRegistry(this.oldErrorReporter, this.options.looseTypes); 
/*   0*/    return this.typeRegistry;
/*   0*/  }
/*   0*/  
/*   0*/  ScopeCreator getTypedScopeCreator() {
/*   0*/    return getPassConfig().getTypedScopeCreator();
/*   0*/  }
/*   0*/  
/*   0*/  public Scope getTopScope() {
/*   0*/    return getPassConfig().getTopScope();
/*   0*/  }
/*   0*/  
/*   0*/  public ReverseAbstractInterpreter getReverseAbstractInterpreter() {
/*   0*/    if (this.abstractInterpreter == null) {
/*   0*/      ChainableReverseAbstractInterpreter interpreter = new SemanticReverseAbstractInterpreter(getCodingConvention(), getTypeRegistry());
/*   0*/      if (this.options.closurePass)
/*   0*/        interpreter = new ClosureReverseAbstractInterpreter(getCodingConvention(), getTypeRegistry()).append(interpreter).getFirst(); 
/*   0*/      this.abstractInterpreter = interpreter;
/*   0*/    } 
/*   0*/    return this.abstractInterpreter;
/*   0*/  }
/*   0*/  
/*   0*/  TypeValidator getTypeValidator() {
/*   0*/    if (this.typeValidator == null)
/*   0*/      this.typeValidator = new TypeValidator(this); 
/*   0*/    return this.typeValidator;
/*   0*/  }
/*   0*/  
/*   0*/  Node parseInputs() {
/*   0*/    boolean devMode = (this.options.devMode != CompilerOptions.DevMode.OFF);
/*   0*/    if (this.externsRoot != null)
/*   0*/      this.externsRoot.detachChildren(); 
/*   0*/    if (this.jsRoot != null)
/*   0*/      this.jsRoot.detachChildren(); 
/*   0*/    this.jsRoot = new Node(125);
/*   0*/    this.jsRoot.setIsSyntheticBlock(true);
/*   0*/    this.externsRoot = new Node(125);
/*   0*/    this.externsRoot.setIsSyntheticBlock(true);
/*   0*/    this.externAndJsRoot = new Node(125, this.externsRoot, this.jsRoot);
/*   0*/    this.externAndJsRoot.setIsSyntheticBlock(true);
/*   0*/    if (this.options.tracer.isOn()) {
/*   0*/      this.tracker = new PerformanceTracker(this.jsRoot, (this.options.tracer == CompilerOptions.TracerMode.ALL));
/*   0*/      addChangeHandler(this.tracker.getCodeChangeHandler());
/*   0*/    } 
/*   0*/    Tracer tracer = newTracer("parseInputs");
/*   0*/    try {
/*   0*/      for (CompilerInput input : this.externs) {
/*   0*/        Node n = input.getAstRoot(this);
/*   0*/        if (hasErrors())
/*   0*/          return null; 
/*   0*/        this.externsRoot.addChildToBack(n);
/*   0*/      } 
/*   0*/      if (this.options.manageClosureDependencies) {
/*   0*/        for (CompilerInput input : this.inputs) {
/*   0*/          input.setCompiler(this);
/*   0*/          for (String provide : input.getProvides())
/*   0*/            getTypeRegistry().forwardDeclareType(provide); 
/*   0*/        } 
/*   0*/        try {
/*   0*/          this.inputs = ((this.moduleGraph == null) ? new JSModuleGraph(this.modules) : this.moduleGraph).manageDependencies(this.options.manageClosureDependenciesEntryPoints, this.inputs);
/*   0*/        } catch (SortedDependencies.CircularDependencyException e) {
/*   0*/          report(JSError.make(JSModule.CIRCULAR_DEPENDENCY_ERROR, new String[] { e.getMessage() }));
/*   0*/          return null;
/*   0*/        } catch (SortedDependencies.MissingProvideException e) {
/*   0*/          report(JSError.make(MISSING_ENTRY_ERROR, new String[] { e.getMessage() }));
/*   0*/          return null;
/*   0*/        } 
/*   0*/      } 
/*   0*/      boolean staleInputs = false;
/*   0*/      for (CompilerInput input : this.inputs) {
/*   0*/        Node n = input.getAstRoot(this);
/*   0*/        if (n == null)
/*   0*/          continue; 
/*   0*/        if (n.getJSDocInfo() != null) {
/*   0*/          JSDocInfo info = n.getJSDocInfo();
/*   0*/          if (info.isExterns()) {
/*   0*/            this.externsRoot.addChildToBack(n);
/*   0*/            input.setIsExtern(true);
/*   0*/            input.getModule().remove(input);
/*   0*/            this.externs.add(input);
/*   0*/            staleInputs = true;
/*   0*/            continue;
/*   0*/          } 
/*   0*/          if (info.isNoCompile()) {
/*   0*/            input.getModule().remove(input);
/*   0*/            staleInputs = true;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/      if (staleInputs) {
/*   0*/        fillEmptyModules(this.modules);
/*   0*/        rebuildInputsFromModules();
/*   0*/      } 
/*   0*/      for (CompilerInput input : this.inputs) {
/*   0*/        Node n = input.getAstRoot(this);
/*   0*/        if (n == null)
/*   0*/          continue; 
/*   0*/        if (devMode) {
/*   0*/          runSanityCheck();
/*   0*/          if (hasErrors())
/*   0*/            return null; 
/*   0*/        } 
/*   0*/        if (this.options.sourceMapOutputPath != null || this.options.nameReferenceReportPath != null) {
/*   0*/          SourceInformationAnnotator sia = new SourceInformationAnnotator(input.getName(), (this.options.devMode != CompilerOptions.DevMode.OFF));
/*   0*/          NodeTraversal.traverse(this, n, sia);
/*   0*/        } 
/*   0*/        this.jsRoot.addChildToBack(n);
/*   0*/      } 
/*   0*/      if (hasErrors())
/*   0*/        return null; 
/*   0*/      return this.externAndJsRoot;
/*   0*/    } finally {
/*   0*/      stopTracer(tracer, "parseInputs");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Node parse(JSSourceFile file) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    addToDebugLog("Parsing: " + file.getName());
/*   0*/    return new JsAst(file).getAstRoot(this);
/*   0*/  }
/*   0*/  
/*   0*/  Node parseSyntheticCode(String js) {
/*   0*/    CompilerInput input = new CompilerInput(JSSourceFile.fromCode(" [synthetic] ", js));
/*   0*/    this.inputsByName.put(input.getName(), input);
/*   0*/    return input.getAstRoot(this);
/*   0*/  }
/*   0*/  
/*   0*/  void initCompilerOptionsIfTesting() {
/*   0*/    if (this.options == null)
/*   0*/      initOptions(new CompilerOptions()); 
/*   0*/  }
/*   0*/  
/*   0*/  Node parseSyntheticCode(String fileName, String js) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    return parse(JSSourceFile.fromCode(fileName, js));
/*   0*/  }
/*   0*/  
/*   0*/  Node parseTestCode(String js) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    CompilerInput input = new CompilerInput(JSSourceFile.fromCode(" [testcode] ", js));
/*   0*/    if (this.inputsByName == null)
/*   0*/      this.inputsByName = Maps.newHashMap(); 
/*   0*/    this.inputsByName.put(input.getName(), input);
/*   0*/    return input.getAstRoot(this);
/*   0*/  }
/*   0*/  
/*   0*/  com.google.javascript.jscomp.mozilla.rhino.ErrorReporter getDefaultErrorReporter() {
/*   0*/    return this.defaultErrorReporter;
/*   0*/  }
/*   0*/  
/*   0*/  public String toSource() {
/*   0*/    return runInCompilerThread(new Callable<String>() {
/*   0*/          public String call() throws Exception {
/*   0*/            Tracer tracer = Compiler.this.newTracer("toSource");
/*   0*/            try {
/*   0*/              Compiler.CodeBuilder cb = new Compiler.CodeBuilder();
/*   0*/              if (Compiler.this.jsRoot != null) {
/*   0*/                int i = 0;
/*   0*/                Node scriptNode = Compiler.this.jsRoot.getLastChild();
/*   0*/                for (; scriptNode != null; scriptNode = scriptNode.getNext())
/*   0*/                  Compiler.this.toSource(cb, i++, scriptNode); 
/*   0*/              } 
/*   0*/              return cb.toString();
/*   0*/            } finally {
/*   0*/              Compiler.this.stopTracer(tracer, "toSource");
/*   0*/            } 
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public String[] toSourceArray() {
/*   0*/    return runInCompilerThread((Callable)new Callable<String[]>() {
/*   0*/          public String[] call() throws Exception {
/*   0*/            Tracer tracer = Compiler.this.newTracer("toSourceArray");
/*   0*/            try {
/*   0*/              int numInputs = Compiler.this.inputs.size();
/*   0*/              String[] sources = new String[numInputs];
/*   0*/              Compiler.CodeBuilder cb = new Compiler.CodeBuilder();
/*   0*/              for (int i = 0; i < numInputs; i++) {
/*   0*/                Node scriptNode = ((CompilerInput)Compiler.this.inputs.get(i)).getAstRoot(Compiler.this);
/*   0*/                cb.reset();
/*   0*/                Compiler.this.toSource(cb, i, scriptNode);
/*   0*/                sources[i] = cb.toString();
/*   0*/              } 
/*   0*/              return sources;
/*   0*/            } finally {
/*   0*/              Compiler.this.stopTracer(tracer, "toSourceArray");
/*   0*/            } 
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public String toSource(final JSModule module) {
/*   0*/    return runInCompilerThread(new Callable<String>() {
/*   0*/          public String call() throws Exception {
/*   0*/            List<CompilerInput> inputs = module.getInputs();
/*   0*/            int numInputs = inputs.size();
/*   0*/            if (numInputs == 0)
/*   0*/              return ""; 
/*   0*/            Compiler.CodeBuilder cb = new Compiler.CodeBuilder();
/*   0*/            for (int i = 0; i < numInputs; i++) {
/*   0*/              Node scriptNode = ((CompilerInput)inputs.get(i)).getAstRoot(Compiler.this);
/*   0*/              if (scriptNode == null)
/*   0*/                throw new IllegalArgumentException("Bad module: " + module.getName()); 
/*   0*/              Compiler.this.toSource(cb, i, scriptNode);
/*   0*/            } 
/*   0*/            return cb.toString();
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public String[] toSourceArray(final JSModule module) {
/*   0*/    return runInCompilerThread((Callable)new Callable<String[]>() {
/*   0*/          public String[] call() throws Exception {
/*   0*/            List<CompilerInput> inputs = module.getInputs();
/*   0*/            int numInputs = inputs.size();
/*   0*/            if (numInputs == 0)
/*   0*/              return new String[0]; 
/*   0*/            String[] sources = new String[numInputs];
/*   0*/            Compiler.CodeBuilder cb = new Compiler.CodeBuilder();
/*   0*/            for (int i = 0; i < numInputs; i++) {
/*   0*/              Node scriptNode = ((CompilerInput)inputs.get(i)).getAstRoot(Compiler.this);
/*   0*/              if (scriptNode == null)
/*   0*/                throw new IllegalArgumentException("Bad module input: " + ((CompilerInput)inputs.get(i)).getName()); 
/*   0*/              cb.reset();
/*   0*/              Compiler.this.toSource(cb, i, scriptNode);
/*   0*/              sources[i] = cb.toString();
/*   0*/            } 
/*   0*/            return sources;
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public void toSource(final CodeBuilder cb, final int inputSeqNum, final Node root) {
/*   0*/    runInCompilerThread(new Callable<Void>() {
/*   0*/          public Void call() throws Exception {
/*   0*/            if (Compiler.this.options.printInputDelimiter) {
/*   0*/              if (cb.getLength() > 0 && !cb.endsWith("\n"))
/*   0*/                cb.append("\n"); 
/*   0*/              Preconditions.checkState((root.getType() == 132));
/*   0*/              String delimiter = Compiler.this.options.inputDelimiter;
/*   0*/              String sourceName = (String)root.getProp(16);
/*   0*/              Preconditions.checkState((sourceName != null));
/*   0*/              Preconditions.checkState(!sourceName.isEmpty());
/*   0*/              delimiter = delimiter.replaceAll("%name%", sourceName).replaceAll("%num%", String.valueOf(inputSeqNum));
/*   0*/              cb.append(delimiter).append("\n");
/*   0*/            } 
/*   0*/            if (root.getJSDocInfo() != null && root.getJSDocInfo().getLicense() != null)
/*   0*/              cb.append("/*\n").append(root.getJSDocInfo().getLicense()).append("*/\n"); 
/*   0*/            if (Compiler.this.options.sourceMapOutputPath != null)
/*   0*/              Compiler.this.sourceMap.setStartingPosition(cb.getLineIndex(), cb.getColumnIndex()); 
/*   0*/            String code = Compiler.this.toSource(root, Compiler.this.sourceMap);
/*   0*/            if (!code.isEmpty()) {
/*   0*/              cb.append(code);
/*   0*/              int length = code.length();
/*   0*/              char lastChar = code.charAt(length - 1);
/*   0*/              char secondLastChar = (length >= 2) ? code.charAt(length - 2) : Character.MIN_VALUE;
/*   0*/              boolean hasSemiColon = (lastChar == ';' || (lastChar == '\n' && secondLastChar == ';'));
/*   0*/              if (!hasSemiColon)
/*   0*/                cb.append(";"); 
/*   0*/            } 
/*   0*/            return null;
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  String toSource(Node n) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    return toSource(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  private String toSource(Node n, SourceMap sourceMap) {
/*   0*/    CodePrinter.Builder builder = new CodePrinter.Builder(n);
/*   0*/    builder.setPrettyPrint(this.options.prettyPrint);
/*   0*/    builder.setLineBreak(this.options.lineBreak);
/*   0*/    builder.setSourceMap(sourceMap);
/*   0*/    builder.setSourceMapDetailLevel(this.options.sourceMapDetailLevel);
/*   0*/    builder.setTagAsStrict((this.options.getLanguageOut() == CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT));
/*   0*/    builder.setLineLengthThreshold(this.options.lineLengthThreshold);
/*   0*/    Charset charset = (this.options.outputCharset != null) ? Charset.forName(this.options.outputCharset) : null;
/*   0*/    builder.setOutputCharset(charset);
/*   0*/    return builder.build();
/*   0*/  }
/*   0*/  
/*   0*/  public static class CodeBuilder {
/*   0*/    private final StringBuilder sb = new StringBuilder();
/*   0*/    
/*   0*/    private int lineCount = 0;
/*   0*/    
/*   0*/    private int colCount = 0;
/*   0*/    
/*   0*/    void reset() {
/*   0*/      this.sb.setLength(0);
/*   0*/    }
/*   0*/    
/*   0*/    CodeBuilder append(String str) {
/*   0*/      this.sb.append(str);
/*   0*/      int index = -1;
/*   0*/      int lastIndex = index;
/*   0*/      while ((index = str.indexOf('\n', index + 1)) >= 0) {
/*   0*/        this.lineCount++;
/*   0*/        lastIndex = index;
/*   0*/      } 
/*   0*/      if (lastIndex == -1) {
/*   0*/        this.colCount += str.length();
/*   0*/      } else {
/*   0*/        this.colCount = str.length() - lastIndex + 1;
/*   0*/      } 
/*   0*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/*   0*/      return this.sb.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public int getLength() {
/*   0*/      return this.sb.length();
/*   0*/    }
/*   0*/    
/*   0*/    int getLineIndex() {
/*   0*/      return this.lineCount;
/*   0*/    }
/*   0*/    
/*   0*/    int getColumnIndex() {
/*   0*/      return this.colCount;
/*   0*/    }
/*   0*/    
/*   0*/    boolean endsWith(String suffix) {
/*   0*/      return (this.sb.length() > suffix.length() && suffix.equals(this.sb.substring(this.sb.length() - suffix.length())));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public void optimize() {
/*   0*/    normalize();
/*   0*/    PhaseOptimizer phaseOptimizer = new PhaseOptimizer(this, this.tracker);
/*   0*/    if (this.options.devMode == CompilerOptions.DevMode.EVERY_PASS)
/*   0*/      phaseOptimizer.setSanityCheck(this.sanityCheck); 
/*   0*/    phaseOptimizer.consume(getPassConfig().getOptimizations());
/*   0*/    phaseOptimizer.process(this.externsRoot, this.jsRoot);
/*   0*/    if (hasErrors())
/*   0*/      return; 
/*   0*/  }
/*   0*/  
/*   0*/  void setCssRenamingMap(CssRenamingMap map) {
/*   0*/    this.options.cssRenamingMap = map;
/*   0*/  }
/*   0*/  
/*   0*/  CssRenamingMap getCssRenamingMap() {
/*   0*/    return this.options.cssRenamingMap;
/*   0*/  }
/*   0*/  
/*   0*/  public void processDefines() {
/*   0*/    new DefaultPassConfig(this.options).processDefines.create(this).process(this.externsRoot, this.jsRoot);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isInliningForbidden() {
/*   0*/    return (this.options.propertyRenaming == PropertyRenamingPolicy.HEURISTIC || this.options.propertyRenaming == PropertyRenamingPolicy.AGGRESSIVE_HEURISTIC);
/*   0*/  }
/*   0*/  
/*   0*/  ControlFlowGraph<Node> computeCFG() {
/*   0*/    logger.info("Computing Control Flow Graph");
/*   0*/    Tracer tracer = newTracer("computeCFG");
/*   0*/    ControlFlowAnalysis cfa = new ControlFlowAnalysis(this, true, false);
/*   0*/    process(cfa);
/*   0*/    stopTracer(tracer, "computeCFG");
/*   0*/    return cfa.getCfg();
/*   0*/  }
/*   0*/  
/*   0*/  public void normalize() {
/*   0*/    logger.info("Normalizing");
/*   0*/    startPass("normalize");
/*   0*/    process(new Normalize(this, false));
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void prepareAst(Node root) {
/*   0*/    Tracer tracer = newTracer("prepareAst");
/*   0*/    CompilerPass pass = new PrepareAst(this);
/*   0*/    pass.process(null, root);
/*   0*/    stopTracer(tracer, "prepareAst");
/*   0*/  }
/*   0*/  
/*   0*/  void recordFunctionInformation() {
/*   0*/    logger.info("Recording function information");
/*   0*/    startPass("recordFunctionInformation");
/*   0*/    RecordFunctionInformation recordFunctionInfoPass = new RecordFunctionInformation(this, (getPassConfig().getIntermediateState()).functionNames);
/*   0*/    process(recordFunctionInfoPass);
/*   0*/    this.functionInformationMap = recordFunctionInfoPass.getMap();
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void addChangeHandler(CodeChangeHandler handler) {
/*1642*/    this.codeChangeHandlers.add(handler);
/*   0*/  }
/*   0*/  
/*   0*/  void removeChangeHandler(CodeChangeHandler handler) {
/*1647*/    this.codeChangeHandlers.remove(handler);
/*   0*/  }
/*   0*/  
/*   0*/  public void reportCodeChange() {
/*1657*/    for (CodeChangeHandler handler : this.codeChangeHandlers)
/*1658*/      handler.reportChange(); 
/*   0*/  }
/*   0*/  
/*   0*/  public CodingConvention getCodingConvention() {
/*1664*/    CodingConvention convention = this.options.getCodingConvention();
/*1665*/    convention = (convention != null) ? convention : this.defaultCodingConvention;
/*1666*/    return convention;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIdeMode() {
/*1671*/    return this.options.ideMode;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean acceptEcmaScript5() {
/*1676*/    switch (this.options.getLanguageIn()) {
/*   0*/      case ECMASCRIPT5:
/*   0*/      case ECMASCRIPT5_STRICT:
/*1679*/        return true;
/*   0*/    } 
/*1681*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerOptions.LanguageMode languageMode() {
/*1685*/    return this.options.getLanguageIn();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean acceptConstKeyword() {
/*1690*/    return this.options.acceptConstKeyword;
/*   0*/  }
/*   0*/  
/*   0*/  Config getParserConfig() {
/*1695*/    if (this.parserConfig == null) {
/*   0*/      Config.LanguageMode mode;
/*1697*/      switch (this.options.getLanguageIn()) {
/*   0*/        case ECMASCRIPT3:
/*1699*/          mode = Config.LanguageMode.ECMASCRIPT3;
/*   0*/          break;
/*   0*/        case ECMASCRIPT5:
/*1702*/          mode = Config.LanguageMode.ECMASCRIPT5;
/*   0*/          break;
/*   0*/        case ECMASCRIPT5_STRICT:
/*1705*/          mode = Config.LanguageMode.ECMASCRIPT5_STRICT;
/*   0*/          break;
/*   0*/        default:
/*1708*/          throw new IllegalStateException("unexpected language mode");
/*   0*/      } 
/*1711*/      this.parserConfig = ParserRunner.createConfig(isIdeMode(), mode, acceptConstKeyword());
/*   0*/    } 
/*1716*/    return this.parserConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeCheckingEnabled() {
/*1721*/    return this.options.checkTypes;
/*   0*/  }
/*   0*/  
/*   0*/  protected DiagnosticGroups getDiagnosticGroups() {
/*1734*/    return new DiagnosticGroups();
/*   0*/  }
/*   0*/  
/*   0*/  public void report(JSError error) {
/*1739*/    CheckLevel level = error.level;
/*1740*/    if (this.warningsGuard != null) {
/*1741*/      CheckLevel newLevel = this.warningsGuard.level(error);
/*1742*/      if (newLevel != null)
/*1743*/        level = newLevel; 
/*   0*/    } 
/*1747*/    if (level.isOn())
/*1748*/      this.errorManager.report(level, error); 
/*   0*/  }
/*   0*/  
/*   0*/  public CheckLevel getErrorLevel(JSError error) {
/*1754*/    Preconditions.checkNotNull(this.options);
/*1755*/    return this.warningsGuard.level(error);
/*   0*/  }
/*   0*/  
/*   0*/  void throwInternalError(String message, Exception cause) {
/*1763*/    String finalMessage = "INTERNAL COMPILER ERROR.\nPlease report this problem.\n" + message;
/*1767*/    RuntimeException e = new RuntimeException(finalMessage, cause);
/*1768*/    if (cause != null)
/*1769*/      e.setStackTrace(cause.getStackTrace()); 
/*1771*/    throw e;
/*   0*/  }
/*   0*/  
/*   0*/  public int getErrorCount() {
/*1779*/    return this.errorManager.getErrorCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getWarningCount() {
/*1786*/    return this.errorManager.getWarningCount();
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasHaltingErrors() {
/*1791*/    return (!isIdeMode() && getErrorCount() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasErrors() {
/*1804*/    return hasHaltingErrors();
/*   0*/  }
/*   0*/  
/*   0*/  void addToDebugLog(String str) {
/*1810*/    this.debugLog.append(str);
/*1811*/    this.debugLog.append('\n');
/*1812*/    logger.fine(str);
/*   0*/  }
/*   0*/  
/*   0*/  private SourceFile getSourceFileByName(String sourceName) {
/*1816*/    if (this.inputsByName.containsKey(sourceName))
/*1817*/      return ((CompilerInput)this.inputsByName.get(sourceName)).getSourceFile(); 
/*1819*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceLine(String sourceName, int lineNumber) {
/*1823*/    if (lineNumber < 1)
/*1824*/      return null; 
/*1826*/    SourceFile input = getSourceFileByName(sourceName);
/*1827*/    if (input != null)
/*1828*/      return input.getLine(lineNumber); 
/*1830*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Region getSourceRegion(String sourceName, int lineNumber) {
/*1834*/    if (lineNumber < 1)
/*1835*/      return null; 
/*1837*/    SourceFile input = getSourceFileByName(sourceName);
/*1838*/    if (input != null)
/*1839*/      return input.getRegion(lineNumber); 
/*1841*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  Node getNodeForCodeInsertion(JSModule module) {
/*1850*/    if (module == null) {
/*1851*/      if (this.inputs.isEmpty())
/*1852*/        throw new IllegalStateException("No inputs"); 
/*1855*/      return ((CompilerInput)this.inputs.get(0)).getAstRoot(this);
/*   0*/    } 
/*1858*/    List<CompilerInput> moduleInputs = module.getInputs();
/*1859*/    if (moduleInputs.size() > 0)
/*1860*/      return ((CompilerInput)moduleInputs.get(0)).getAstRoot(this); 
/*1862*/    throw new IllegalStateException("Root module has no inputs");
/*   0*/  }
/*   0*/  
/*   0*/  public SourceMap getSourceMap() {
/*1866*/    return this.sourceMap;
/*   0*/  }
/*   0*/  
/*   0*/  VariableMap getVariableMap() {
/*1870*/    return (getPassConfig().getIntermediateState()).variableMap;
/*   0*/  }
/*   0*/  
/*   0*/  VariableMap getPropertyMap() {
/*1874*/    return (getPassConfig().getIntermediateState()).propertyMap;
/*   0*/  }
/*   0*/  
/*   0*/  CompilerOptions getOptions() {
/*1878*/    return this.options;
/*   0*/  }
/*   0*/  
/*   0*/  FunctionInformationMap getFunctionalInformationMap() {
/*1882*/    return this.functionInformationMap;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setLoggingLevel(Level level) {
/*1889*/    logger.setLevel(level);
/*   0*/  }
/*   0*/  
/*   0*/  public String getAstDotGraph() throws IOException {
/*1894*/    if (this.jsRoot != null) {
/*1895*/      ControlFlowAnalysis cfa = new ControlFlowAnalysis(this, true, false);
/*1896*/      cfa.process(null, this.jsRoot);
/*1897*/      return DotFormatter.toDot(this.jsRoot, cfa.getCfg());
/*   0*/    } 
/*1899*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public ErrorManager getErrorManager() {
/*1905*/    if (this.options == null)
/*1906*/      initOptions(new CompilerOptions()); 
/*1908*/    return this.errorManager;
/*   0*/  }
/*   0*/  
/*   0*/  List<CompilerInput> getInputsInOrder() {
/*1913*/    return Collections.unmodifiableList(this.inputs);
/*   0*/  }
/*   0*/  
/*   0*/  public static class IntermediateState implements Serializable {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    Node externsRoot;
/*   0*/    
/*   0*/    private Node jsRoot;
/*   0*/    
/*   0*/    private List<CompilerInput> externs;
/*   0*/    
/*   0*/    private List<CompilerInput> inputs;
/*   0*/    
/*   0*/    private List<JSModule> modules;
/*   0*/    
/*   0*/    private PassConfig.State passConfigState;
/*   0*/    
/*   0*/    private JSTypeRegistry typeRegistry;
/*   0*/    
/*   0*/    private AbstractCompiler.LifeCycleStage lifeCycleStage;
/*   0*/    
/*   0*/    private IntermediateState() {}
/*   0*/  }
/*   0*/  
/*   0*/  public IntermediateState getState() {
/*1946*/    IntermediateState state = new IntermediateState();
/*1947*/    state.externsRoot = this.externsRoot;
/*1948*/    state.jsRoot = this.jsRoot;
/*1949*/    state.externs = this.externs;
/*1950*/    state.inputs = this.inputs;
/*1951*/    state.modules = this.modules;
/*1952*/    state.passConfigState = getPassConfig().getIntermediateState();
/*1953*/    state.typeRegistry = this.typeRegistry;
/*1954*/    state.lifeCycleStage = getLifeCycleStage();
/*1956*/    return state;
/*   0*/  }
/*   0*/  
/*   0*/  public void setState(IntermediateState state) {
/*1964*/    this.externsRoot = state.externsRoot;
/*1965*/    this.jsRoot = state.jsRoot;
/*1966*/    this.externs = state.externs;
/*1967*/    this.inputs = state.inputs;
/*1968*/    this.modules = state.modules;
/*1969*/    this.passes = createPassConfigInternal();
/*1970*/    getPassConfig().setIntermediateState(state.passConfigState);
/*1971*/    this.typeRegistry = state.typeRegistry;
/*1972*/    setLifeCycleStage(state.lifeCycleStage);
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  List<CompilerInput> getInputsForTesting() {
/*1977*/    return this.inputs;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  List<CompilerInput> getExternsForTesting() {
/*1982*/    return this.externs;
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasRegExpGlobalReferences() {
/*1987*/    return this.hasRegExpGlobalReferences;
/*   0*/  }
/*   0*/  
/*   0*/  void setHasRegExpGlobalReferences(boolean references) {
/*1992*/    this.hasRegExpGlobalReferences = references;
/*   0*/  }
/*   0*/  
/*   0*/  void updateGlobalVarReferences(Map<Scope.Var, ReferenceCollectingCallback.ReferenceCollection> refMapPatch, Node collectionRoot) {
/*1998*/    Preconditions.checkState((collectionRoot.getType() == 132 || collectionRoot.getType() == 125));
/*2000*/    if (this.globalRefMap == null)
/*2001*/      this.globalRefMap = new GlobalVarReferenceMap(getInputsInOrder()); 
/*2003*/    this.globalRefMap.updateGlobalVarReferences(refMapPatch, collectionRoot);
/*   0*/  }
/*   0*/  
/*   0*/  ReferenceCollectingCallback.ReferenceMap getGlobalVarReferences() {
/*2008*/    return this.globalRefMap;
/*   0*/  }
/*   0*/}
