/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Charsets;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Supplier;
/*   0*/import com.google.common.base.Throwables;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.io.CharStreams;
/*   0*/import com.google.javascript.jscomp.deps.SortedDependencies;
/*   0*/import com.google.javascript.jscomp.parsing.Config;
/*   0*/import com.google.javascript.jscomp.parsing.ParserRunner;
/*   0*/import com.google.javascript.jscomp.type.ChainableReverseAbstractInterpreter;
/*   0*/import com.google.javascript.jscomp.type.ClosureReverseAbstractInterpreter;
/*   0*/import com.google.javascript.jscomp.type.ReverseAbstractInterpreter;
/*   0*/import com.google.javascript.jscomp.type.SemanticReverseAbstractInterpreter;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.InputId;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import com.google.javascript.rhino.jstype.StaticReference;
/*   0*/import com.google.javascript.rhino.jstype.StaticSlot;
/*   0*/import com.google.javascript.rhino.jstype.StaticSymbolTable;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStreamReader;
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
/*   0*/import java.util.regex.Matcher;
/*   0*/
/*   0*/public class Compiler extends AbstractCompiler {
/*   0*/  static final String SINGLETON_MODULE_NAME = "[singleton]";
/*   0*/  
/*  80*/  static final DiagnosticType MODULE_DEPENDENCY_ERROR = DiagnosticType.error("JSC_MODULE_DEPENDENCY_ERROR", "Bad dependency: {0} -> {1}. Modules must be listed in dependency order.");
/*   0*/  
/*  85*/  static final DiagnosticType MISSING_ENTRY_ERROR = DiagnosticType.error("JSC_MISSING_ENTRY_ERROR", "required entry point \"{0}\" never provided");
/*   0*/  
/*  89*/  CompilerOptions options = null;
/*   0*/  
/*  91*/  private PassConfig passes = null;
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
/* 114*/  private final Map<String, Node> injectedLibraries = Maps.newLinkedHashMap();
/*   0*/  
/*   0*/  Node externsRoot;
/*   0*/  
/*   0*/  Node jsRoot;
/*   0*/  
/*   0*/  Node externAndJsRoot;
/*   0*/  
/*   0*/  private Map<InputId, CompilerInput> inputsById;
/*   0*/  
/*   0*/  private SourceMap sourceMap;
/*   0*/  
/* 127*/  private String externExports = null;
/*   0*/  
/* 133*/  private int uniqueNameId = 0;
/*   0*/  
/*   0*/  private boolean useThreads = true;
/*   0*/  
/*   0*/  private boolean hasRegExpGlobalReferences = true;
/*   0*/  
/*   0*/  private FunctionInformationMap functionInformationMap;
/*   0*/  
/* 148*/  private final StringBuilder debugLog = new StringBuilder();
/*   0*/  
/* 151*/  CodingConvention defaultCodingConvention = new ClosureCodingConvention();
/*   0*/  
/*   0*/  private JSTypeRegistry typeRegistry;
/*   0*/  
/* 154*/  private Config parserConfig = null;
/*   0*/  
/*   0*/  private ReverseAbstractInterpreter abstractInterpreter;
/*   0*/  
/*   0*/  private TypeValidator typeValidator;
/*   0*/  
/*   0*/  public PerformanceTracker tracker;
/*   0*/  
/* 162*/  private final ErrorReporter oldErrorReporter = RhinoErrorReporter.forOldRhino(this);
/*   0*/  
/* 166*/  private final com.google.javascript.rhino.head.ErrorReporter defaultErrorReporter = RhinoErrorReporter.forNewRhino(this);
/*   0*/  
/* 170*/  public static final DiagnosticType OPTIMIZE_LOOP_ERROR = DiagnosticType.error("JSC_OPTIMIZE_LOOP_ERROR", "Exceeded max number of optimization iterations: {0}");
/*   0*/  
/* 173*/  public static final DiagnosticType MOTION_ITERATIONS_ERROR = DiagnosticType.error("JSC_OPTIMIZE_LOOP_ERROR", "Exceeded max number of code motion iterations: {0}");
/*   0*/  
/*   0*/  private static final long COMPILER_STACK_SIZE = 1048576L;
/*   0*/  
/* 185*/  private static final Logger logger = Logger.getLogger("com.google.javascript.jscomp");
/*   0*/  
/*   0*/  private final PrintStream outStream;
/*   0*/  
/* 190*/  private GlobalVarReferenceMap globalRefMap = null;
/*   0*/  
/* 192*/  private volatile double progress = 0.0D;
/*   0*/  
/*   0*/  public Compiler() {
/* 198*/    this((PrintStream)null);
/*   0*/  }
/*   0*/  
/*   0*/  public Compiler(ErrorManager errorManager) {
/* 214*/    this();
/* 215*/    setErrorManager(errorManager);
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorManager(ErrorManager errorManager) {
/* 224*/    Preconditions.checkNotNull(errorManager, "the error manager cannot be null");
/* 226*/    this.errorManager = errorManager;
/*   0*/  }
/*   0*/  
/*   0*/  private MessageFormatter createMessageFormatter() {
/* 234*/    boolean colorize = this.options.shouldColorizeErrorOutput();
/* 235*/    return this.options.errorFormat.toFormatter(this, colorize);
/*   0*/  }
/*   0*/  
/*   0*/  public void initOptions(CompilerOptions options) {
/* 243*/    this.options = options;
/* 244*/    if (this.errorManager == null)
/* 245*/      if (this.outStream == null) {
/* 246*/        setErrorManager(new LoggerErrorManager(createMessageFormatter(), logger));
/*   0*/      } else {
/* 249*/        PrintStreamErrorManager printer = new PrintStreamErrorManager(createMessageFormatter(), this.outStream);
/* 251*/        printer.setSummaryDetailLevel(options.summaryDetailLevel);
/* 252*/        setErrorManager(printer);
/*   0*/      }  
/* 257*/    if (options.enables(DiagnosticGroups.CHECK_TYPES)) {
/* 258*/      options.checkTypes = true;
/* 259*/    } else if (options.disables(DiagnosticGroups.CHECK_TYPES)) {
/* 260*/      options.checkTypes = false;
/* 261*/    } else if (!options.checkTypes) {
/* 265*/      options.setWarningLevel(DiagnosticGroup.forType(RhinoErrorReporter.TYPE_PARSE_ERROR), CheckLevel.OFF);
/*   0*/    } 
/* 271*/    if (options.checkGlobalThisLevel.isOn() && !options.disables(DiagnosticGroups.GLOBAL_THIS))
/* 273*/      options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, options.checkGlobalThisLevel); 
/* 278*/    if (options.getLanguageIn() == CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT)
/* 279*/      options.setWarningLevel(DiagnosticGroups.ES5_STRICT, CheckLevel.ERROR); 
/* 285*/    List<WarningsGuard> guards = Lists.newArrayList();
/* 286*/    guards.add(new SuppressDocWarningsGuard(getDiagnosticGroups().getRegisteredGroups()));
/* 289*/    guards.add(options.getWarningsGuard());
/* 291*/    ComposeWarningsGuard composedGuards = new ComposeWarningsGuard(guards);
/* 298*/    if (!options.checkSymbols && !composedGuards.enables(DiagnosticGroups.CHECK_VARIABLES))
/* 300*/      composedGuards.addGuard(new DiagnosticGroupWarningsGuard(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF)); 
/* 304*/    this.warningsGuard = composedGuards;
/*   0*/  }
/*   0*/  
/*   0*/  public void init(JSSourceFile[] externs, JSSourceFile[] inputs, CompilerOptions options) {
/* 312*/    init(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])inputs), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T1 extends SourceFile, T2 extends SourceFile> void init(List<T1> externs, List<T2> inputs, CompilerOptions options) {
/* 323*/    JSModule module = new JSModule("[singleton]");
/* 324*/    for (SourceFile input : inputs)
/* 325*/      module.add(input); 
/* 328*/    initModules(externs, Lists.newArrayList((Object[])new JSModule[] { module }), options);
/*   0*/  }
/*   0*/  
/*   0*/  public void init(JSSourceFile[] externs, JSModule[] modules, CompilerOptions options) {
/* 337*/    initModules(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends SourceFile> void initModules(List<T> externs, List<JSModule> modules, CompilerOptions options) {
/* 347*/    initOptions(options);
/* 349*/    checkFirstModule(modules);
/* 350*/    fillEmptyModules(modules);
/* 352*/    this.externs = makeCompilerInput(externs, true);
/* 356*/    this.modules = modules;
/* 357*/    if (modules.size() > 1) {
/*   0*/      try {
/* 359*/        this.moduleGraph = new JSModuleGraph(modules);
/* 360*/      } catch (JSModuleGraph.ModuleDependenceException e) {
/* 363*/        report(JSError.make(MODULE_DEPENDENCY_ERROR, new String[] { e.getModule().getName(), e.getDependentModule().getName() }));
/*   0*/        return;
/*   0*/      } 
/*   0*/    } else {
/* 368*/      this.moduleGraph = null;
/*   0*/    } 
/* 371*/    this.inputs = getAllInputsFromModules(modules);
/* 372*/    initBasedOnOptions();
/* 374*/    initInputsByIdMap();
/*   0*/  }
/*   0*/  
/*   0*/  private void initBasedOnOptions() {
/* 382*/    if (this.options.sourceMapOutputPath != null) {
/* 383*/      this.sourceMap = this.options.sourceMapFormat.getInstance();
/* 384*/      this.sourceMap.setPrefixMappings(this.options.sourceMapLocationMappings);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private <T extends SourceFile> List<CompilerInput> makeCompilerInput(List<T> files, boolean isExtern) {
/* 390*/    List<CompilerInput> inputs = Lists.newArrayList();
/* 391*/    for (SourceFile sourceFile : files)
/* 392*/      inputs.add(new CompilerInput(sourceFile, isExtern)); 
/* 394*/    return inputs;
/*   0*/  }
/*   0*/  
/* 397*/  private static final DiagnosticType EMPTY_MODULE_LIST_ERROR = DiagnosticType.error("JSC_EMPTY_MODULE_LIST_ERROR", "At least one module must be provided");
/*   0*/  
/* 401*/  private static final DiagnosticType EMPTY_ROOT_MODULE_ERROR = DiagnosticType.error("JSC_EMPTY_ROOT_MODULE_ERROR", "Root module '{0}' must contain at least one source code input");
/*   0*/  
/*   0*/  private void checkFirstModule(List<JSModule> modules) {
/* 410*/    if (modules.isEmpty()) {
/* 411*/      report(JSError.make(EMPTY_MODULE_LIST_ERROR, new String[0]));
/* 412*/    } else if (((JSModule)modules.get(0)).getInputs().isEmpty() && modules.size() > 1) {
/* 414*/      report(JSError.make(EMPTY_ROOT_MODULE_ERROR, new String[] { ((JSModule)modules.get(0)).getName() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static String createFillFileName(String moduleName) {
/* 424*/    return "[" + moduleName + "]";
/*   0*/  }
/*   0*/  
/*   0*/  private static void fillEmptyModules(List<JSModule> modules) {
/* 432*/    for (JSModule module : modules) {
/* 433*/      if (module.getInputs().isEmpty())
/* 434*/        module.add(SourceFile.fromCode(createFillFileName(module.getName()), "")); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void rebuildInputsFromModules() {
/* 446*/    this.inputs = getAllInputsFromModules(this.modules);
/* 447*/    initInputsByIdMap();
/*   0*/  }
/*   0*/  
/*   0*/  private static List<CompilerInput> getAllInputsFromModules(List<JSModule> modules) {
/* 456*/    List<CompilerInput> inputs = Lists.newArrayList();
/* 457*/    Map<String, JSModule> inputMap = Maps.newHashMap();
/* 458*/    for (JSModule module : modules) {
/* 459*/      for (CompilerInput input : module.getInputs()) {
/* 460*/        String inputName = input.getName();
/* 465*/        inputs.add(input);
/* 466*/        inputMap.put(inputName, module);
/*   0*/      } 
/*   0*/    } 
/* 469*/    return inputs;
/*   0*/  }
/*   0*/  
/* 472*/  static final DiagnosticType DUPLICATE_INPUT = DiagnosticType.error("JSC_DUPLICATE_INPUT", "Duplicate input: {0}");
/*   0*/  
/* 474*/  static final DiagnosticType DUPLICATE_EXTERN_INPUT = DiagnosticType.error("JSC_DUPLICATE_EXTERN_INPUT", "Duplicate extern input: {0}");
/*   0*/  
/*   0*/  private final PassFactory sanityCheck;
/*   0*/  
/*   0*/  private Tracer currentTracer;
/*   0*/  
/*   0*/  private String currentPassName;
/*   0*/  
/*   0*/  private int syntheticCodeId;
/*   0*/  
/*   0*/  protected final CodeChangeHandler.RecentChange recentChange;
/*   0*/  
/*   0*/  private final List<CodeChangeHandler> codeChangeHandlers;
/*   0*/  
/*   0*/  static final String SYNTHETIC_EXTERNS = "{SyntheticVarsDeclar}";
/*   0*/  
/*   0*/  private CompilerInput synthesizedExternsInput;
/*   0*/  
/*   0*/  void initInputsByIdMap() {
/* 483*/    this.inputsById = new HashMap<InputId, CompilerInput>();
/* 484*/    for (CompilerInput input : this.externs) {
/* 485*/      InputId id = input.getInputId();
/* 486*/      CompilerInput previous = putCompilerInput(id, input);
/* 487*/      if (previous != null)
/* 488*/        report(JSError.make(DUPLICATE_EXTERN_INPUT, new String[] { input.getName() })); 
/*   0*/    } 
/* 491*/    for (CompilerInput input : this.inputs) {
/* 492*/      InputId id = input.getInputId();
/* 493*/      CompilerInput previous = putCompilerInput(id, input);
/* 494*/      if (previous != null)
/* 495*/        report(JSError.make(DUPLICATE_INPUT, new String[] { input.getName() })); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(SourceFile extern, SourceFile input, CompilerOptions options) {
/* 502*/    return compile(Lists.newArrayList((Object[])new SourceFile[] { extern }), Lists.newArrayList((Object[])new SourceFile[] { input }), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(SourceFile extern, JSSourceFile[] input, CompilerOptions options) {
/* 507*/    return compile(Lists.newArrayList((Object[])new SourceFile[] { extern }), Lists.newArrayList((Object[])input), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile extern, JSModule[] modules, CompilerOptions options) {
/* 512*/    return compileModules(Lists.newArrayList((Object[])new JSSourceFile[] { extern }), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile[] externs, JSSourceFile[] inputs, CompilerOptions options) {
/* 522*/    return compile(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])inputs), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T1 extends SourceFile, T2 extends SourceFile> Result compile(List<T1> externs, List<T2> inputs, CompilerOptions options) {
/* 533*/    Preconditions.checkState((this.jsRoot == null));
/*   0*/    try {
/* 536*/      init(externs, inputs, options);
/* 537*/      if (hasErrors())
/* 538*/        return getResult(); 
/* 540*/      return compile();
/*   0*/    } finally {
/* 542*/      Tracer t = newTracer("generateReport");
/* 543*/      this.errorManager.generateReport();
/* 544*/      stopTracer(t, "generateReport");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile[] externs, JSModule[] modules, CompilerOptions options) {
/* 554*/    return compileModules(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends SourceFile> Result compileModules(List<T> externs, List<JSModule> modules, CompilerOptions options) {
/* 565*/    Preconditions.checkState((this.jsRoot == null));
/*   0*/    try {
/* 568*/      initModules(externs, modules, options);
/* 569*/      if (hasErrors())
/* 570*/        return getResult(); 
/* 572*/      return compile();
/*   0*/    } finally {
/* 574*/      Tracer t = newTracer("generateReport");
/* 575*/      this.errorManager.generateReport();
/* 576*/      stopTracer(t, "generateReport");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private Result compile() {
/* 581*/    return runInCompilerThread(new Callable<Result>() {
/*   0*/          public Result call() throws Exception {
/* 584*/            Compiler.this.compileInternal();
/* 585*/            return Compiler.this.getResult();
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public void disableThreads() {
/* 595*/    this.useThreads = false;
/*   0*/  }
/*   0*/  
/*   0*/  private <T> T runInCompilerThread(Callable<T> callable) {
/* 599*/    return runCallable(callable, this.useThreads, this.options.tracer.isOn());
/*   0*/  }
/*   0*/  
/*   0*/  static <T> T runCallableWithLargeStack(Callable<T> callable) {
/* 603*/    return runCallable(callable, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  static <T> T runCallable(final Callable<T> callable, boolean useLargeStackThread, boolean trace) {
/* 616*/    final boolean dumpTraceReport = trace;
/* 617*/    final Object[] result = new Object[1];
/* 618*/    final Throwable[] exception = new Throwable[1];
/* 619*/    Runnable runnable = new Runnable() {
/*   0*/        public void run() {
/*   0*/          try {
/* 623*/            if (dumpTraceReport)
/* 624*/              Tracer.initCurrentThreadTrace(); 
/* 626*/            result[0] = callable.call();
/* 627*/          } catch (Throwable e) {
/* 628*/            exception[0] = e;
/*   0*/          } finally {
/* 630*/            if (dumpTraceReport)
/* 631*/              Tracer.logAndClearCurrentThreadTrace(); 
/*   0*/          } 
/*   0*/        }
/*   0*/      };
/* 637*/    if (useLargeStackThread) {
/* 638*/      Thread th = new Thread(null, runnable, "jscompiler", 1048576L);
/* 639*/      th.start();
/*   0*/      while (true) {
/*   0*/        try {
/* 642*/          th.join();
/*   0*/          break;
/* 644*/        } catch (InterruptedException interruptedException) {}
/*   0*/      } 
/*   0*/    } else {
/* 649*/      runnable.run();
/*   0*/    } 
/* 653*/    if (exception[0] != null)
/* 654*/      throw new RuntimeException(exception[0]); 
/* 657*/    return (T)result[0];
/*   0*/  }
/*   0*/  
/*   0*/  private void compileInternal() {
/* 661*/    setProgress(0.0D);
/* 662*/    parse();
/* 665*/    setProgress(0.15D);
/* 666*/    if (hasErrors())
/*   0*/      return; 
/* 670*/    if (!precheck())
/*   0*/      return; 
/* 674*/    if (this.options.nameAnonymousFunctionsOnly) {
/* 676*/      check();
/*   0*/      return;
/*   0*/    } 
/* 680*/    if (!this.options.skipAllPasses) {
/* 681*/      check();
/* 682*/      if (hasErrors())
/*   0*/        return; 
/* 686*/      if (this.options.isExternExportsEnabled() || this.options.externExportsPath != null)
/* 688*/        externExports(); 
/* 692*/      if (!this.options.ideMode)
/* 693*/        optimize(); 
/*   0*/    } 
/* 697*/    if (this.options.recordFunctionInformation)
/* 698*/      recordFunctionInformation(); 
/* 701*/    if (this.options.devMode == CompilerOptions.DevMode.START_AND_END)
/* 702*/      runSanityCheck(); 
/* 704*/    setProgress(1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public void parse() {
/* 708*/    parseInputs();
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig getPassConfig() {
/* 712*/    if (this.passes == null)
/* 713*/      this.passes = createPassConfigInternal(); 
/* 715*/    return this.passes;
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig createPassConfigInternal() {
/* 723*/    return new DefaultPassConfig(this.options);
/*   0*/  }
/*   0*/  
/*   0*/  public void setPassConfig(PassConfig passes) {
/* 737*/    Preconditions.checkNotNull(passes);
/* 739*/    if (this.passes != null)
/* 740*/      throw new IllegalStateException("this.passes has already been assigned"); 
/* 742*/    this.passes = passes;
/*   0*/  }
/*   0*/  
/*   0*/  boolean precheck() {
/* 752*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void check() {
/* 756*/    runCustomPasses(CustomPassExecutionTime.BEFORE_CHECKS);
/* 760*/    PhaseOptimizer phaseOptimizer = new PhaseOptimizer(this, this.tracker, new PhaseOptimizer.ProgressRange(getProgress(), 1.0D));
/* 762*/    if (this.options.devMode == CompilerOptions.DevMode.EVERY_PASS)
/* 763*/      phaseOptimizer.setSanityCheck(this.sanityCheck); 
/* 765*/    phaseOptimizer.consume(getPassConfig().getChecks());
/* 766*/    phaseOptimizer.process(this.externsRoot, this.jsRoot);
/* 767*/    if (hasErrors())
/*   0*/      return; 
/* 772*/    if (this.options.nameAnonymousFunctionsOnly)
/*   0*/      return; 
/* 776*/    if (this.options.removeTryCatchFinally)
/* 777*/      removeTryCatchFinally(); 
/* 780*/    if (this.options.getTweakProcessing().shouldStrip() || !this.options.stripTypes.isEmpty() || !this.options.stripNameSuffixes.isEmpty() || !this.options.stripTypePrefixes.isEmpty() || !this.options.stripNamePrefixes.isEmpty())
/* 785*/      stripCode(this.options.stripTypes, this.options.stripNameSuffixes, this.options.stripTypePrefixes, this.options.stripNamePrefixes); 
/* 789*/    runCustomPasses(CustomPassExecutionTime.BEFORE_OPTIMIZATIONS);
/*   0*/  }
/*   0*/  
/*   0*/  private void externExports() {
/* 793*/    logger.fine("Creating extern file for exports");
/* 794*/    startPass("externExports");
/* 796*/    ExternExportsPass pass = new ExternExportsPass(this);
/* 797*/    process(pass);
/* 799*/    this.externExports = pass.getGeneratedExterns();
/* 801*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void process(CompilerPass p) {
/* 806*/    p.process(this.externsRoot, this.jsRoot);
/*   0*/  }
/*   0*/  
/*   0*/  public Compiler(PrintStream stream) {
/* 809*/    this.sanityCheck = new PassFactory("sanityCheck", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 813*/          return new SanityCheck(compiler);
/*   0*/        }
/*   0*/      };
/* 871*/    this.currentTracer = null;
/* 872*/    this.currentPassName = null;
/*1459*/    this.syntheticCodeId = 0;
/*1872*/    this.recentChange = new CodeChangeHandler.RecentChange();
/*1874*/    this.codeChangeHandlers = Lists.newArrayList();
/*1880*/    this.synthesizedExternsInput = null;
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
/*   0*/    logger.fine("Remove try/catch/finally");
/*   0*/    startPass("removeTryCatchFinally");
/*   0*/    RemoveTryCatch r = new RemoveTryCatch(this);
/*   0*/    process(r);
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void stripCode(Set<String> stripTypes, Set<String> stripNameSuffixes, Set<String> stripTypePrefixes, Set<String> stripNamePrefixes) {
/*   0*/    logger.fine("Strip code");
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
/*   0*/  public CompilerInput getInput(InputId id) {
/*   0*/    return this.inputsById.get(id);
/*   0*/  }
/*   0*/  
/*   0*/  protected void removeExternInput(InputId id) {
/*   0*/    CompilerInput input = getInput(id);
/*   0*/    if (input == null)
/*   0*/      return; 
/*   0*/    Preconditions.checkState(input.isExtern(), "Not an extern input: %s", new Object[] { input.getName() });
/*   0*/    this.inputsById.remove(id);
/*   0*/    this.externs.remove(input);
/*   0*/    Node root = input.getAstRoot(this);
/*   0*/    if (root != null)
/*   0*/      root.detachFromParent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerInput newExternInput(String name) {
/*   0*/    SourceAst ast = new SyntheticAst(name);
/*   0*/    if (this.inputsById.containsKey(ast.getInputId()))
/*   0*/      throw new IllegalArgumentException("Conflicting externs name: " + name); 
/*   0*/    CompilerInput input = new CompilerInput(ast, true);
/*   0*/    putCompilerInput(input.getInputId(), input);
/*   0*/    this.externsRoot.addChildToFront(ast.getAstRoot(this));
/*   0*/    this.externs.add(0, input);
/*   0*/    return input;
/*   0*/  }
/*   0*/  
/*   0*/  private CompilerInput putCompilerInput(InputId id, CompilerInput input) {
/*   0*/    input.setCompiler(this);
/*   0*/    return this.inputsById.put(id, input);
/*   0*/  }
/*   0*/  
/*   0*/  void addIncrementalSourceAst(JsAst ast) {
/*   0*/    InputId id = ast.getInputId();
/*   0*/    Preconditions.checkState((getInput(id) == null), "Duplicate input %s", new Object[] { id.getIdName() });
/*   0*/    putCompilerInput(id, new CompilerInput(ast));
/*   0*/  }
/*   0*/  
/*   0*/  boolean replaceIncrementalSourceAst(JsAst ast) {
/*   0*/    CompilerInput oldInput = getInput(ast.getInputId());
/*   0*/    Preconditions.checkNotNull(oldInput, "No input to replace: %s", new Object[] { ast.getInputId().getIdName() });
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
/*   0*/    putCompilerInput(ast.getInputId(), newInput);
/*   0*/    JSModule module = oldInput.getModule();
/*   0*/    if (module != null) {
/*   0*/      module.addAfter(newInput, oldInput);
/*   0*/      module.remove(oldInput);
/*   0*/    } 
/*   0*/    Preconditions.checkState(newInput.getInputId().equals(oldInput.getInputId()));
/*   0*/    InputId inputIdOnAst = newInput.getAstRoot(this).getInputId();
/*   0*/    Preconditions.checkState(newInput.getInputId().equals(inputIdOnAst));
/*   0*/    this.inputs.remove(oldInput);
/*   0*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  boolean addNewSourceAst(JsAst ast) {
/*   0*/    CompilerInput oldInput = getInput(ast.getInputId());
/*   0*/    if (oldInput != null)
/*   0*/      throw new IllegalStateException("Input already exists: " + ast.getInputId().getIdName()); 
/*   0*/    Node newRoot = ast.getAstRoot(this);
/*   0*/    if (newRoot == null)
/*   0*/      return false; 
/*   0*/    getRoot().getLastChild().addChildToBack(newRoot);
/*   0*/    CompilerInput newInput = new CompilerInput(ast);
/*   0*/    if (this.moduleGraph == null && !this.modules.isEmpty())
/*   0*/      ((JSModule)this.modules.get(0)).add(newInput); 
/*   0*/    putCompilerInput(ast.getInputId(), newInput);
/*   0*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  JSModuleGraph getModuleGraph() {
/*   0*/    return this.moduleGraph;
/*   0*/  }
/*   0*/  
/*   0*/  JSModuleGraph getDegenerateModuleGraph() {
/*   0*/    return (this.moduleGraph == null) ? new JSModuleGraph(this.modules) : this.moduleGraph;
/*   0*/  }
/*   0*/  
/*   0*/  public JSTypeRegistry getTypeRegistry() {
/*   0*/    if (this.typeRegistry == null)
/*   0*/      this.typeRegistry = new JSTypeRegistry(this.oldErrorReporter, this.options.looseTypes); 
/*   0*/    return this.typeRegistry;
/*   0*/  }
/*   0*/  
/*   0*/  public MemoizedScopeCreator getTypedScopeCreator() {
/*   0*/    return getPassConfig().getTypedScopeCreator();
/*   0*/  }
/*   0*/  
/*   0*/  DefaultPassConfig ensureDefaultPassConfig() {
/*   0*/    PassConfig passes = getPassConfig().getBasePassConfig();
/*   0*/    Preconditions.checkState(passes instanceof DefaultPassConfig, "PassConfigs must eventually delegate to the DefaultPassConfig");
/*   0*/    return (DefaultPassConfig)passes;
/*   0*/  }
/*   0*/  
/*   0*/  public SymbolTable buildKnownSymbolTable() {
/*   0*/    SymbolTable symbolTable = new SymbolTable(getTypeRegistry());
/*   0*/    MemoizedScopeCreator typedScopeCreator = getTypedScopeCreator();
/*   0*/    if (typedScopeCreator != null) {
/*   0*/      symbolTable.addScopes(typedScopeCreator.getAllMemoizedScopes());
/*   0*/      symbolTable.addSymbolsFrom((StaticSymbolTable<StaticSlot<JSType>, StaticReference<JSType>>)typedScopeCreator);
/*   0*/    } else {
/*   0*/      symbolTable.findScopes(this, this.externsRoot, this.jsRoot);
/*   0*/    } 
/*   0*/    GlobalNamespace globalNamespace = ensureDefaultPassConfig().getGlobalNamespace();
/*   0*/    if (globalNamespace != null)
/*   0*/      symbolTable.addSymbolsFrom((StaticSymbolTable<StaticSlot<JSType>, StaticReference<JSType>>)globalNamespace); 
/*   0*/    ReferenceCollectingCallback refCollector = new ReferenceCollectingCallback(this, ReferenceCollectingCallback.DO_NOTHING_BEHAVIOR);
/*   0*/    NodeTraversal.traverse(this, getRoot(), refCollector);
/*   0*/    symbolTable.addSymbolsFrom((StaticSymbolTable<StaticSlot<JSType>, StaticReference<JSType>>)refCollector);
/*   0*/    PreprocessorSymbolTable preprocessorSymbolTable = ensureDefaultPassConfig().getPreprocessorSymbolTable();
/*   0*/    if (preprocessorSymbolTable != null)
/*   0*/      symbolTable.addSymbolsFrom((StaticSymbolTable<StaticSlot<JSType>, StaticReference<JSType>>)preprocessorSymbolTable); 
/*   0*/    symbolTable.fillNamespaceReferences();
/*   0*/    symbolTable.fillPropertyScopes();
/*   0*/    symbolTable.fillThisReferences(this, this.externsRoot, this.jsRoot);
/*   0*/    symbolTable.fillPropertySymbols(this, this.externsRoot, this.jsRoot);
/*   0*/    symbolTable.fillJSDocInfo(this, this.externsRoot, this.jsRoot);
/*   0*/    return symbolTable;
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
/*   0*/    this.jsRoot = IR.block();
/*   0*/    this.jsRoot.setIsSyntheticBlock(true);
/*   0*/    this.externsRoot = IR.block();
/*   0*/    this.externsRoot.setIsSyntheticBlock(true);
/*   0*/    this.externAndJsRoot = IR.block(new Node[] { this.externsRoot, this.jsRoot });
/*   0*/    this.externAndJsRoot.setIsSyntheticBlock(true);
/*   0*/    if (this.options.tracer.isOn()) {
/*   0*/      this.tracker = new PerformanceTracker(this.jsRoot, this.options.tracer);
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
/*   0*/      if (this.options.transformAMDToCJSModules || this.options.processCommonJSModules)
/*   0*/        processAMDAndCommonJSModules(); 
/*   0*/      boolean staleInputs = false;
/*   0*/      if (this.options.dependencyOptions.needsManagement() && !(newCompilerOptions()).skipAllPasses && this.options.closurePass) {
/*   0*/        for (CompilerInput input : this.inputs) {
/*   0*/          for (String provide : input.getProvides())
/*   0*/            getTypeRegistry().forwardDeclareType(provide); 
/*   0*/        } 
/*   0*/        try {
/*   0*/          this.inputs = ((this.moduleGraph == null) ? new JSModuleGraph(this.modules) : this.moduleGraph).manageDependencies(this.options.dependencyOptions, this.inputs);
/*   0*/          staleInputs = true;
/*   0*/        } catch (SortedDependencies.CircularDependencyException e) {
/*   0*/          report(JSError.make(JSModule.CIRCULAR_DEPENDENCY_ERROR, new String[] { e.getMessage() }));
/*   0*/          if (hasErrors())
/*   0*/            return null; 
/*   0*/        } catch (SortedDependencies.MissingProvideException e) {
/*   0*/          report(JSError.make(MISSING_ENTRY_ERROR, new String[] { e.getMessage() }));
/*   0*/          if (hasErrors())
/*   0*/            return null; 
/*   0*/        } 
/*   0*/      } 
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
/*   0*/  void processAMDAndCommonJSModules() {
/*   0*/    Map<String, JSModule> modulesByName = Maps.newLinkedHashMap();
/*   0*/    Map<CompilerInput, JSModule> modulesByInput = Maps.newLinkedHashMap();
/*   0*/    for (CompilerInput input : this.inputs) {
/*   0*/      input.setCompiler(this);
/*   0*/      Node root = input.getAstRoot(this);
/*   0*/      if (root == null)
/*   0*/        continue; 
/*   0*/      if (this.options.transformAMDToCJSModules)
/*   0*/        new TransformAMDToCJSModule(this).process(null, root); 
/*   0*/      if (this.options.processCommonJSModules) {
/*   0*/        ProcessCommonJSModules cjs = new ProcessCommonJSModules(this, this.options.commonJSModulePathPrefix);
/*   0*/        cjs.process(null, root);
/*   0*/        JSModule m = cjs.getModule();
/*   0*/        if (m != null) {
/*   0*/          modulesByName.put(m.getName(), m);
/*   0*/          modulesByInput.put(input, m);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    if (this.options.processCommonJSModules) {
/*   0*/      List<JSModule> modules = Lists.newArrayList(modulesByName.values());
/*   0*/      if (!modules.isEmpty()) {
/*   0*/        this.modules = modules;
/*   0*/        this.moduleGraph = new JSModuleGraph(this.modules);
/*   0*/      } 
/*   0*/      for (JSModule module : modules) {
/*   0*/        for (CompilerInput input : module.getInputs()) {
/*   0*/          for (String require : input.getRequires()) {
/*   0*/            JSModule dependency = modulesByName.get(require);
/*   0*/            if (dependency == null) {
/*   0*/              report(JSError.make(MISSING_ENTRY_ERROR, new String[] { require }));
/*   0*/              continue;
/*   0*/            } 
/*   0*/            module.addDependency(dependency);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/      try {
/*   0*/        modules = Lists.newArrayList();
/*   0*/        for (CompilerInput input : this.moduleGraph.manageDependencies(this.options.dependencyOptions, this.inputs))
/*   0*/          modules.add(modulesByInput.get(input)); 
/*   0*/        this.modules = modules;
/*   0*/        this.moduleGraph = new JSModuleGraph(modules);
/*   0*/      } catch (Exception e) {
/*   0*/        Throwables.propagate(e);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Node parse(SourceFile file) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    addToDebugLog("Parsing: " + file.getName());
/*   0*/    return new JsAst(file).getAstRoot(this);
/*   0*/  }
/*   0*/  
/*   0*/  Node parseSyntheticCode(String js) {
/*   0*/    CompilerInput input = new CompilerInput(SourceFile.fromCode(" [synthetic:" + ++this.syntheticCodeId + "] ", js));
/*   0*/    putCompilerInput(input.getInputId(), input);
/*   0*/    return input.getAstRoot(this);
/*   0*/  }
/*   0*/  
/*   0*/  protected CompilerOptions newCompilerOptions() {
/*   0*/    return new CompilerOptions();
/*   0*/  }
/*   0*/  
/*   0*/  void initCompilerOptionsIfTesting() {
/*   0*/    if (this.options == null)
/*   0*/      initOptions(newCompilerOptions()); 
/*   0*/  }
/*   0*/  
/*   0*/  Node parseSyntheticCode(String fileName, String js) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    return parse(SourceFile.fromCode(fileName, js));
/*   0*/  }
/*   0*/  
/*   0*/  Node parseTestCode(String js) {
/*   0*/    initCompilerOptionsIfTesting();
/*   0*/    CompilerInput input = new CompilerInput(SourceFile.fromCode("[testcode]", js));
/*   0*/    if (this.inputsById == null)
/*   0*/      this.inputsById = Maps.newHashMap(); 
/*   0*/    putCompilerInput(input.getInputId(), input);
/*   0*/    return input.getAstRoot(this);
/*   0*/  }
/*   0*/  
/*   0*/  com.google.javascript.rhino.head.ErrorReporter getDefaultErrorReporter() {
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
/*   0*/                Node scriptNode = Compiler.this.jsRoot.getFirstChild();
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
/*   0*/              Preconditions.checkState(root.isScript());
/*   0*/              String delimiter = Compiler.this.options.inputDelimiter;
/*   0*/              String inputName = root.getInputId().getIdName();
/*   0*/              String sourceName = root.getSourceFileName();
/*   0*/              Preconditions.checkState((sourceName != null));
/*   0*/              Preconditions.checkState(!sourceName.isEmpty());
/*   0*/              delimiter = delimiter.replaceAll("%name%", Matcher.quoteReplacement(inputName)).replaceAll("%num%", String.valueOf(inputSeqNum));
/*   0*/              cb.append(delimiter).append("\n");
/*   0*/            } 
/*   0*/            if (root.getJSDocInfo() != null && root.getJSDocInfo().getLicense() != null)
/*   0*/              cb.append("/*\n").append(root.getJSDocInfo().getLicense()).append("*/\n"); 
/*   0*/            if (Compiler.this.options.sourceMapOutputPath != null)
/*   0*/              Compiler.this.sourceMap.setStartingPosition(cb.getLineIndex(), cb.getColumnIndex()); 
/*   0*/            String code = Compiler.this.toSource(root, Compiler.this.sourceMap, (inputSeqNum == 0));
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
/*   0*/    return toSource(n, null, true);
/*   0*/  }
/*   0*/  
/*   0*/  private String toSource(Node n, SourceMap sourceMap, boolean firstOutput) {
/*   0*/    CodePrinter.Builder builder = new CodePrinter.Builder(n);
/*   0*/    builder.setPrettyPrint(this.options.prettyPrint);
/*   0*/    builder.setLineBreak(this.options.lineBreak);
/*   0*/    builder.setPreferLineBreakAtEndOfFile(this.options.preferLineBreakAtEndOfFile);
/*   0*/    builder.setSourceMap(sourceMap);
/*   0*/    builder.setSourceMapDetailLevel(this.options.sourceMapDetailLevel);
/*   0*/    builder.setTagAsStrict((firstOutput && this.options.getLanguageOut() == CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT));
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
/*   0*/    PhaseOptimizer phaseOptimizer = new PhaseOptimizer(this, this.tracker, null);
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
/*   0*/    logger.fine("Computing Control Flow Graph");
/*   0*/    Tracer tracer = newTracer("computeCFG");
/*   0*/    ControlFlowAnalysis cfa = new ControlFlowAnalysis(this, true, false);
/*   0*/    process(cfa);
/*   0*/    stopTracer(tracer, "computeCFG");
/*   0*/    return cfa.getCfg();
/*   0*/  }
/*   0*/  
/*   0*/  public void normalize() {
/*   0*/    logger.fine("Normalizing");
/*   0*/    startPass("normalize");
/*   0*/    process(new Normalize(this, false));
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void prepareAst(Node root) {
/*   0*/    CompilerPass pass = new PrepareAst(this);
/*   0*/    pass.process(null, root);
/*   0*/  }
/*   0*/  
/*   0*/  void recordFunctionInformation() {
/*   0*/    logger.fine("Recording function information");
/*   0*/    startPass("recordFunctionInformation");
/*   0*/    RecordFunctionInformation recordFunctionInfoPass = new RecordFunctionInformation(this, (getPassConfig().getIntermediateState()).functionNames);
/*   0*/    process(recordFunctionInfoPass);
/*   0*/    this.functionInformationMap = recordFunctionInfoPass.getMap();
/*   0*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void addChangeHandler(CodeChangeHandler handler) {
/*1884*/    this.codeChangeHandlers.add(handler);
/*   0*/  }
/*   0*/  
/*   0*/  void removeChangeHandler(CodeChangeHandler handler) {
/*1889*/    this.codeChangeHandlers.remove(handler);
/*   0*/  }
/*   0*/  
/*   0*/  public void reportCodeChange() {
/*1899*/    for (CodeChangeHandler handler : this.codeChangeHandlers)
/*1900*/      handler.reportChange(); 
/*   0*/  }
/*   0*/  
/*   0*/  public CodingConvention getCodingConvention() {
/*1906*/    CodingConvention convention = this.options.getCodingConvention();
/*1907*/    convention = (convention != null) ? convention : this.defaultCodingConvention;
/*1908*/    return convention;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIdeMode() {
/*1913*/    return this.options.ideMode;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean acceptEcmaScript5() {
/*1918*/    switch (this.options.getLanguageIn()) {
/*   0*/      case ECMASCRIPT5:
/*   0*/      case ECMASCRIPT5_STRICT:
/*1921*/        return true;
/*   0*/    } 
/*1923*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerOptions.LanguageMode languageMode() {
/*1927*/    return this.options.getLanguageIn();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean acceptConstKeyword() {
/*1932*/    return this.options.acceptConstKeyword;
/*   0*/  }
/*   0*/  
/*   0*/  Config getParserConfig() {
/*1937*/    if (this.parserConfig == null) {
/*   0*/      Config.LanguageMode mode;
/*1939*/      switch (this.options.getLanguageIn()) {
/*   0*/        case ECMASCRIPT3:
/*1941*/          mode = Config.LanguageMode.ECMASCRIPT3;
/*   0*/          break;
/*   0*/        case ECMASCRIPT5:
/*1944*/          mode = Config.LanguageMode.ECMASCRIPT5;
/*   0*/          break;
/*   0*/        case ECMASCRIPT5_STRICT:
/*1947*/          mode = Config.LanguageMode.ECMASCRIPT5_STRICT;
/*   0*/          break;
/*   0*/        default:
/*1950*/          throw new IllegalStateException("unexpected language mode");
/*   0*/      } 
/*1953*/      this.parserConfig = ParserRunner.createConfig(isIdeMode(), mode, acceptConstKeyword(), this.options.extraAnnotationNames);
/*   0*/    } 
/*1959*/    return this.parserConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeCheckingEnabled() {
/*1964*/    return this.options.checkTypes;
/*   0*/  }
/*   0*/  
/*   0*/  protected DiagnosticGroups getDiagnosticGroups() {
/*1977*/    return new DiagnosticGroups();
/*   0*/  }
/*   0*/  
/*   0*/  public void report(JSError error) {
/*1982*/    CheckLevel level = error.getDefaultLevel();
/*1983*/    if (this.warningsGuard != null) {
/*1984*/      CheckLevel newLevel = this.warningsGuard.level(error);
/*1985*/      if (newLevel != null)
/*1986*/        level = newLevel; 
/*   0*/    } 
/*1990*/    if (level.isOn()) {
/*1991*/      if ((getOptions()).errorHandler != null)
/*1992*/        (getOptions()).errorHandler.report(level, error); 
/*1994*/      this.errorManager.report(level, error);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CheckLevel getErrorLevel(JSError error) {
/*2000*/    Preconditions.checkNotNull(this.options);
/*2001*/    return this.warningsGuard.level(error);
/*   0*/  }
/*   0*/  
/*   0*/  void throwInternalError(String message, Exception cause) {
/*2009*/    String finalMessage = "INTERNAL COMPILER ERROR.\nPlease report this problem.\n" + message;
/*2013*/    RuntimeException e = new RuntimeException(finalMessage, cause);
/*2014*/    if (cause != null)
/*2015*/      e.setStackTrace(cause.getStackTrace()); 
/*2017*/    throw e;
/*   0*/  }
/*   0*/  
/*   0*/  public int getErrorCount() {
/*2025*/    return this.errorManager.getErrorCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getWarningCount() {
/*2032*/    return this.errorManager.getWarningCount();
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasHaltingErrors() {
/*2037*/    return (!isIdeMode() && getErrorCount() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasErrors() {
/*2050*/    return hasHaltingErrors();
/*   0*/  }
/*   0*/  
/*   0*/  void addToDebugLog(String str) {
/*2056*/    this.debugLog.append(str);
/*2057*/    this.debugLog.append('\n');
/*2058*/    logger.fine(str);
/*   0*/  }
/*   0*/  
/*   0*/  private SourceFile getSourceFileByName(String sourceName) {
/*2064*/    if (sourceName != null) {
/*2065*/      CompilerInput input = this.inputsById.get(new InputId(sourceName));
/*2066*/      if (input != null)
/*2067*/        return input.getSourceFile(); 
/*   0*/    } 
/*2070*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceLine(String sourceName, int lineNumber) {
/*2075*/    if (lineNumber < 1)
/*2076*/      return null; 
/*2078*/    SourceFile input = getSourceFileByName(sourceName);
/*2079*/    if (input != null)
/*2080*/      return input.getLine(lineNumber); 
/*2082*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Region getSourceRegion(String sourceName, int lineNumber) {
/*2087*/    if (lineNumber < 1)
/*2088*/      return null; 
/*2090*/    SourceFile input = getSourceFileByName(sourceName);
/*2091*/    if (input != null)
/*2092*/      return input.getRegion(lineNumber); 
/*2094*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  Node getNodeForCodeInsertion(JSModule module) {
/*2103*/    if (module == null) {
/*2104*/      if (this.inputs.isEmpty())
/*2105*/        throw new IllegalStateException("No inputs"); 
/*2108*/      return ((CompilerInput)this.inputs.get(0)).getAstRoot(this);
/*   0*/    } 
/*2111*/    List<CompilerInput> moduleInputs = module.getInputs();
/*2112*/    if (moduleInputs.size() > 0)
/*2113*/      return ((CompilerInput)moduleInputs.get(0)).getAstRoot(this); 
/*2115*/    throw new IllegalStateException("Root module has no inputs");
/*   0*/  }
/*   0*/  
/*   0*/  public SourceMap getSourceMap() {
/*2119*/    return this.sourceMap;
/*   0*/  }
/*   0*/  
/*   0*/  VariableMap getVariableMap() {
/*2123*/    return (getPassConfig().getIntermediateState()).variableMap;
/*   0*/  }
/*   0*/  
/*   0*/  VariableMap getPropertyMap() {
/*2127*/    return (getPassConfig().getIntermediateState()).propertyMap;
/*   0*/  }
/*   0*/  
/*   0*/  CompilerOptions getOptions() {
/*2131*/    return this.options;
/*   0*/  }
/*   0*/  
/*   0*/  FunctionInformationMap getFunctionalInformationMap() {
/*2135*/    return this.functionInformationMap;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setLoggingLevel(Level level) {
/*2142*/    logger.setLevel(level);
/*   0*/  }
/*   0*/  
/*   0*/  public String getAstDotGraph() throws IOException {
/*2147*/    if (this.jsRoot != null) {
/*2148*/      ControlFlowAnalysis cfa = new ControlFlowAnalysis(this, true, false);
/*2149*/      cfa.process(null, this.jsRoot);
/*2150*/      return DotFormatter.toDot(this.jsRoot, cfa.getCfg());
/*   0*/    } 
/*2152*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public ErrorManager getErrorManager() {
/*2158*/    if (this.options == null)
/*2159*/      initOptions(newCompilerOptions()); 
/*2161*/    return this.errorManager;
/*   0*/  }
/*   0*/  
/*   0*/  List<CompilerInput> getInputsInOrder() {
/*2166*/    return Collections.unmodifiableList(this.inputs);
/*   0*/  }
/*   0*/  
/*   0*/  public Map<InputId, CompilerInput> getInputsById() {
/*2173*/    return Collections.unmodifiableMap(this.inputsById);
/*   0*/  }
/*   0*/  
/*   0*/  List<CompilerInput> getExternsInOrder() {
/*2180*/    return Collections.unmodifiableList(this.externs);
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
/*   0*/    private Map<String, Node> injectedLibraries;
/*   0*/    
/*   0*/    private IntermediateState() {}
/*   0*/  }
/*   0*/  
/*   0*/  public IntermediateState getState() {
/*2214*/    IntermediateState state = new IntermediateState();
/*2215*/    state.externsRoot = this.externsRoot;
/*2216*/    state.jsRoot = this.jsRoot;
/*2217*/    state.externs = this.externs;
/*2218*/    state.inputs = this.inputs;
/*2219*/    state.modules = this.modules;
/*2220*/    state.passConfigState = getPassConfig().getIntermediateState();
/*2221*/    state.typeRegistry = this.typeRegistry;
/*2222*/    state.lifeCycleStage = getLifeCycleStage();
/*2223*/    state.injectedLibraries = Maps.newLinkedHashMap(this.injectedLibraries);
/*2225*/    return state;
/*   0*/  }
/*   0*/  
/*   0*/  public void setState(IntermediateState state) {
/*2233*/    this.externsRoot = state.externsRoot;
/*2234*/    this.jsRoot = state.jsRoot;
/*2235*/    this.externs = state.externs;
/*2236*/    this.inputs = state.inputs;
/*2237*/    this.modules = state.modules;
/*2238*/    this.passes = createPassConfigInternal();
/*2239*/    getPassConfig().setIntermediateState(state.passConfigState);
/*2240*/    this.typeRegistry = state.typeRegistry;
/*2241*/    setLifeCycleStage(state.lifeCycleStage);
/*2243*/    this.injectedLibraries.clear();
/*2244*/    this.injectedLibraries.putAll(state.injectedLibraries);
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  List<CompilerInput> getInputsForTesting() {
/*2249*/    return this.inputs;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  List<CompilerInput> getExternsForTesting() {
/*2254*/    return this.externs;
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasRegExpGlobalReferences() {
/*2259*/    return this.hasRegExpGlobalReferences;
/*   0*/  }
/*   0*/  
/*   0*/  void setHasRegExpGlobalReferences(boolean references) {
/*2264*/    this.hasRegExpGlobalReferences = references;
/*   0*/  }
/*   0*/  
/*   0*/  void updateGlobalVarReferences(Map<Scope.Var, ReferenceCollectingCallback.ReferenceCollection> refMapPatch, Node collectionRoot) {
/*2270*/    Preconditions.checkState((collectionRoot.isScript() || collectionRoot.isBlock()));
/*2272*/    if (this.globalRefMap == null)
/*2273*/      this.globalRefMap = new GlobalVarReferenceMap(getInputsInOrder(), getExternsInOrder()); 
/*2276*/    this.globalRefMap.updateGlobalVarReferences(refMapPatch, collectionRoot);
/*   0*/  }
/*   0*/  
/*   0*/  GlobalVarReferenceMap getGlobalVarReferences() {
/*2281*/    return this.globalRefMap;
/*   0*/  }
/*   0*/  
/*   0*/  CompilerInput getSynthesizedExternsInput() {
/*2286*/    if (this.synthesizedExternsInput == null)
/*2287*/      this.synthesizedExternsInput = newExternInput("{SyntheticVarsDeclar}"); 
/*2289*/    return this.synthesizedExternsInput;
/*   0*/  }
/*   0*/  
/*   0*/  public double getProgress() {
/*2294*/    return this.progress;
/*   0*/  }
/*   0*/  
/*   0*/  void setProgress(double newProgress) {
/*2299*/    if (newProgress > 1.0D) {
/*2300*/      this.progress = 1.0D;
/*2301*/    } else if (newProgress < 0.0D) {
/*2302*/      this.progress = 0.0D;
/*   0*/    } else {
/*2304*/      this.progress = newProgress;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceScript(JsAst ast) {
/*2316*/    CompilerInput input = getInput(ast.getInputId());
/*2317*/    if (!replaceIncrementalSourceAst(ast))
/*   0*/      return; 
/*2320*/    Node originalRoot = input.getAstRoot(this);
/*2322*/    processNewScript(ast, originalRoot);
/*   0*/  }
/*   0*/  
/*   0*/  public void addNewScript(JsAst ast) {
/*2333*/    if (!addNewSourceAst(ast))
/*   0*/      return; 
/*2336*/    Node emptyScript = new Node(132);
/*2337*/    InputId inputId = ast.getInputId();
/*2338*/    emptyScript.setInputId(inputId);
/*2339*/    emptyScript.setStaticSourceFile(SourceFile.fromCode(inputId.getIdName(), ""));
/*2342*/    processNewScript(ast, emptyScript);
/*   0*/  }
/*   0*/  
/*   0*/  private void processNewScript(JsAst ast, Node originalRoot) {
/*2346*/    Node js = ast.getAstRoot(this);
/*2347*/    Preconditions.checkNotNull(js);
/*2349*/    runHotSwap(originalRoot, js, getCleanupPassConfig());
/*2352*/    runHotSwapPass(null, null, (ensureDefaultPassConfig()).garbageCollectChecks);
/*2354*/    getTypeRegistry().clearNamedTypes();
/*2355*/    removeSyntheticVarsInput();
/*2357*/    runHotSwap(originalRoot, js, ensureDefaultPassConfig());
/*   0*/  }
/*   0*/  
/*   0*/  private void runHotSwap(Node originalRoot, Node js, PassConfig passConfig) {
/*2365*/    for (PassFactory passFactory : passConfig.getChecks())
/*2366*/      runHotSwapPass(originalRoot, js, passFactory); 
/*   0*/  }
/*   0*/  
/*   0*/  private void runHotSwapPass(Node originalRoot, Node js, PassFactory passFactory) {
/*2372*/    HotSwapCompilerPass pass = passFactory.getHotSwapPass(this);
/*2373*/    if (pass != null) {
/*2374*/      logger.info("Performing HotSwap for pass " + passFactory.getName());
/*2375*/      pass.hotSwapScript(js, originalRoot);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private PassConfig getCleanupPassConfig() {
/*2380*/    return new CleanupPasses(getOptions());
/*   0*/  }
/*   0*/  
/*   0*/  private void removeSyntheticVarsInput() {
/*2384*/    String sourceName = "{SyntheticVarsDeclar}";
/*2385*/    removeExternInput(new InputId(sourceName));
/*   0*/  }
/*   0*/  
/*   0*/  Node ensureLibraryInjected(String resourceName) {
/*2390*/    if (this.injectedLibraries.containsKey(resourceName))
/*2391*/      return null; 
/*2395*/    boolean isBase = "base".equals(resourceName);
/*2396*/    if (!isBase)
/*2397*/      ensureLibraryInjected("base"); 
/*2400*/    Node firstChild = loadLibraryCode(resourceName).removeChildren();
/*2401*/    Node lastChild = firstChild.getLastSibling();
/*2403*/    Node parent = getNodeForCodeInsertion(null);
/*2404*/    if (isBase) {
/*2405*/      parent.addChildrenToFront(firstChild);
/*   0*/    } else {
/*2407*/      parent.addChildrenAfter(firstChild, this.injectedLibraries.get("base"));
/*   0*/    } 
/*2410*/    reportCodeChange();
/*2412*/    this.injectedLibraries.put(resourceName, lastChild);
/*2413*/    return lastChild;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  Node loadLibraryCode(String resourceName) {
/*   0*/    String originalCode;
/*   0*/    try {
/*2421*/      originalCode = CharStreams.toString(new InputStreamReader(Compiler.class.getResourceAsStream(String.format("js/%s.js", new Object[] { resourceName })), Charsets.UTF_8));
/*2425*/    } catch (IOException e) {
/*2426*/      throw new RuntimeException(e);
/*   0*/    } 
/*2429*/    return Normalize.parseAndNormalizeSyntheticCode(this, originalCode, String.format("jscomp_%s_", new Object[] { resourceName }));
/*   0*/  }
/*   0*/}
