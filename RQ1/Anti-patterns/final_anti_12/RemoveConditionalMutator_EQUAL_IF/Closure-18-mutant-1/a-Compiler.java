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
/*   0*/import java.util.ResourceBundle;
/*   0*/import java.util.Set;
/*   0*/import java.util.concurrent.Callable;
/*   0*/import java.util.logging.Level;
/*   0*/import java.util.logging.Logger;
/*   0*/import java.util.regex.Matcher;
/*   0*/
/*   0*/public class Compiler extends AbstractCompiler {
/*   0*/  static final String SINGLETON_MODULE_NAME = "[singleton]";
/*   0*/  
/*  81*/  static final DiagnosticType MODULE_DEPENDENCY_ERROR = DiagnosticType.error("JSC_MODULE_DEPENDENCY_ERROR", "Bad dependency: {0} -> {1}. Modules must be listed in dependency order.");
/*   0*/  
/*  86*/  static final DiagnosticType MISSING_ENTRY_ERROR = DiagnosticType.error("JSC_MISSING_ENTRY_ERROR", "required entry point \"{0}\" never provided");
/*   0*/  
/*   0*/  private static final String CONFIG_RESOURCE = "com.google.javascript.jscomp.parsing.ParserConfig";
/*   0*/  
/*  93*/  CompilerOptions options = null;
/*   0*/  
/*  95*/  private PassConfig passes = null;
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
/* 118*/  private final Map<String, Node> injectedLibraries = Maps.newLinkedHashMap();
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
/* 131*/  private String externExports = null;
/*   0*/  
/* 137*/  private int uniqueNameId = 0;
/*   0*/  
/*   0*/  private boolean useThreads = true;
/*   0*/  
/*   0*/  private boolean hasRegExpGlobalReferences = true;
/*   0*/  
/*   0*/  private FunctionInformationMap functionInformationMap;
/*   0*/  
/* 152*/  private final StringBuilder debugLog = new StringBuilder();
/*   0*/  
/* 155*/  CodingConvention defaultCodingConvention = new ClosureCodingConvention();
/*   0*/  
/*   0*/  private JSTypeRegistry typeRegistry;
/*   0*/  
/* 158*/  private Config parserConfig = null;
/*   0*/  
/*   0*/  private ReverseAbstractInterpreter abstractInterpreter;
/*   0*/  
/*   0*/  private TypeValidator typeValidator;
/*   0*/  
/*   0*/  public PerformanceTracker tracker;
/*   0*/  
/* 166*/  private final ErrorReporter oldErrorReporter = RhinoErrorReporter.forOldRhino(this);
/*   0*/  
/* 170*/  private final com.google.javascript.rhino.head.ErrorReporter defaultErrorReporter = RhinoErrorReporter.forNewRhino(this);
/*   0*/  
/* 174*/  public static final DiagnosticType OPTIMIZE_LOOP_ERROR = DiagnosticType.error("JSC_OPTIMIZE_LOOP_ERROR", "Exceeded max number of optimization iterations: {0}");
/*   0*/  
/* 177*/  public static final DiagnosticType MOTION_ITERATIONS_ERROR = DiagnosticType.error("JSC_OPTIMIZE_LOOP_ERROR", "Exceeded max number of code motion iterations: {0}");
/*   0*/  
/*   0*/  private static final long COMPILER_STACK_SIZE = 1048576L;
/*   0*/  
/* 189*/  private static final Logger logger = Logger.getLogger("com.google.javascript.jscomp");
/*   0*/  
/*   0*/  private final PrintStream outStream;
/*   0*/  
/* 194*/  private GlobalVarReferenceMap globalRefMap = null;
/*   0*/  
/* 196*/  private volatile double progress = 0.0D;
/*   0*/  
/*   0*/  public Compiler() {
/* 202*/    this((PrintStream)null);
/*   0*/  }
/*   0*/  
/*   0*/  public Compiler(ErrorManager errorManager) {
/* 218*/    this();
/* 219*/    setErrorManager(errorManager);
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorManager(ErrorManager errorManager) {
/* 228*/    Preconditions.checkNotNull(errorManager, "the error manager cannot be null");
/* 230*/    this.errorManager = errorManager;
/*   0*/  }
/*   0*/  
/*   0*/  private MessageFormatter createMessageFormatter() {
/* 238*/    boolean colorize = this.options.shouldColorizeErrorOutput();
/* 239*/    return this.options.errorFormat.toFormatter(this, colorize);
/*   0*/  }
/*   0*/  
/*   0*/  public void initOptions(CompilerOptions options) {
/* 247*/    this.options = options;
/* 248*/    if (this.errorManager == null)
/* 249*/      if (this.outStream == null) {
/* 250*/        setErrorManager(new LoggerErrorManager(createMessageFormatter(), logger));
/*   0*/      } else {
/* 253*/        PrintStreamErrorManager printer = new PrintStreamErrorManager(createMessageFormatter(), this.outStream);
/* 255*/        printer.setSummaryDetailLevel(options.summaryDetailLevel);
/* 256*/        setErrorManager(printer);
/*   0*/      }  
/* 261*/    if (options.enables(DiagnosticGroups.CHECK_TYPES)) {
/* 262*/      options.checkTypes = true;
/* 263*/    } else if (options.disables(DiagnosticGroups.CHECK_TYPES)) {
/* 264*/      options.checkTypes = false;
/* 265*/    } else if (!options.checkTypes) {
/* 269*/      options.setWarningLevel(DiagnosticGroup.forType(RhinoErrorReporter.TYPE_PARSE_ERROR), CheckLevel.OFF);
/*   0*/    } 
/* 275*/    if (options.checkGlobalThisLevel.isOn() && !options.disables(DiagnosticGroups.GLOBAL_THIS))
/* 277*/      options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, options.checkGlobalThisLevel); 
/* 282*/    if (options.getLanguageIn() == CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT)
/* 283*/      options.setWarningLevel(DiagnosticGroups.ES5_STRICT, CheckLevel.ERROR); 
/* 289*/    List<WarningsGuard> guards = Lists.newArrayList();
/* 290*/    guards.add(new SuppressDocWarningsGuard(getDiagnosticGroups().getRegisteredGroups()));
/* 293*/    guards.add(options.getWarningsGuard());
/* 295*/    ComposeWarningsGuard composedGuards = new ComposeWarningsGuard(guards);
/* 302*/    if (!options.checkSymbols && !composedGuards.enables(DiagnosticGroups.CHECK_VARIABLES))
/* 304*/      composedGuards.addGuard(new DiagnosticGroupWarningsGuard(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF)); 
/* 308*/    this.warningsGuard = composedGuards;
/*   0*/  }
/*   0*/  
/*   0*/  public void init(JSSourceFile[] externs, JSSourceFile[] inputs, CompilerOptions options) {
/* 316*/    init(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])inputs), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T1 extends SourceFile, T2 extends SourceFile> void init(List<T1> externs, List<T2> inputs, CompilerOptions options) {
/* 327*/    JSModule module = new JSModule("[singleton]");
/* 328*/    for (SourceFile input : inputs)
/* 329*/      module.add(input); 
/* 332*/    initModules(externs, Lists.newArrayList((Object[])new JSModule[] { module }), options);
/*   0*/  }
/*   0*/  
/*   0*/  public void init(JSSourceFile[] externs, JSModule[] modules, CompilerOptions options) {
/* 341*/    initModules(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends SourceFile> void initModules(List<T> externs, List<JSModule> modules, CompilerOptions options) {
/* 351*/    initOptions(options);
/* 353*/    checkFirstModule(modules);
/* 354*/    fillEmptyModules(modules);
/* 356*/    this.externs = makeCompilerInput(externs, true);
/* 360*/    this.modules = modules;
/* 361*/    if (modules.size() > 1) {
/*   0*/      try {
/* 363*/        this.moduleGraph = new JSModuleGraph(modules);
/* 364*/      } catch (JSModuleGraph.ModuleDependenceException e) {
/* 367*/        report(JSError.make(MODULE_DEPENDENCY_ERROR, new String[] { e.getModule().getName(), e.getDependentModule().getName() }));
/*   0*/        return;
/*   0*/      } 
/*   0*/    } else {
/* 372*/      this.moduleGraph = null;
/*   0*/    } 
/* 375*/    this.inputs = getAllInputsFromModules(modules);
/* 376*/    initBasedOnOptions();
/* 378*/    initInputsByIdMap();
/*   0*/  }
/*   0*/  
/*   0*/  private void initBasedOnOptions() {
/* 386*/    if (this.options.sourceMapOutputPath != null) {
/* 387*/      this.sourceMap = this.options.sourceMapFormat.getInstance();
/* 388*/      this.sourceMap.setPrefixMappings(this.options.sourceMapLocationMappings);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private <T extends SourceFile> List<CompilerInput> makeCompilerInput(List<T> files, boolean isExtern) {
/* 394*/    List<CompilerInput> inputs = Lists.newArrayList();
/* 395*/    for (SourceFile sourceFile : files)
/* 396*/      inputs.add(new CompilerInput(sourceFile, isExtern)); 
/* 398*/    return inputs;
/*   0*/  }
/*   0*/  
/* 401*/  private static final DiagnosticType EMPTY_MODULE_LIST_ERROR = DiagnosticType.error("JSC_EMPTY_MODULE_LIST_ERROR", "At least one module must be provided");
/*   0*/  
/* 405*/  private static final DiagnosticType EMPTY_ROOT_MODULE_ERROR = DiagnosticType.error("JSC_EMPTY_ROOT_MODULE_ERROR", "Root module '{0}' must contain at least one source code input");
/*   0*/  
/*   0*/  private void checkFirstModule(List<JSModule> modules) {
/* 414*/    if (modules.isEmpty()) {
/* 415*/      report(JSError.make(EMPTY_MODULE_LIST_ERROR, new String[0]));
/* 416*/    } else if (((JSModule)modules.get(0)).getInputs().isEmpty() && modules.size() > 1) {
/* 418*/      report(JSError.make(EMPTY_ROOT_MODULE_ERROR, new String[] { ((JSModule)modules.get(0)).getName() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static String createFillFileName(String moduleName) {
/* 428*/    return "[" + moduleName + "]";
/*   0*/  }
/*   0*/  
/*   0*/  private static void fillEmptyModules(List<JSModule> modules) {
/* 436*/    for (JSModule module : modules) {
/* 437*/      if (module.getInputs().isEmpty())
/* 438*/        module.add(SourceFile.fromCode(createFillFileName(module.getName()), "")); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void rebuildInputsFromModules() {
/* 450*/    this.inputs = getAllInputsFromModules(this.modules);
/* 451*/    initInputsByIdMap();
/*   0*/  }
/*   0*/  
/*   0*/  private static List<CompilerInput> getAllInputsFromModules(List<JSModule> modules) {
/* 460*/    List<CompilerInput> inputs = Lists.newArrayList();
/* 461*/    Map<String, JSModule> inputMap = Maps.newHashMap();
/* 462*/    for (JSModule module : modules) {
/* 463*/      for (CompilerInput input : module.getInputs()) {
/* 464*/        String inputName = input.getName();
/* 469*/        inputs.add(input);
/* 470*/        inputMap.put(inputName, module);
/*   0*/      } 
/*   0*/    } 
/* 473*/    return inputs;
/*   0*/  }
/*   0*/  
/* 476*/  static final DiagnosticType DUPLICATE_INPUT = DiagnosticType.error("JSC_DUPLICATE_INPUT", "Duplicate input: {0}");
/*   0*/  
/* 478*/  static final DiagnosticType DUPLICATE_EXTERN_INPUT = DiagnosticType.error("JSC_DUPLICATE_EXTERN_INPUT", "Duplicate extern input: {0}");
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
/* 487*/    this.inputsById = new HashMap<InputId, CompilerInput>();
/* 488*/    for (CompilerInput input : this.externs) {
/* 489*/      InputId id = input.getInputId();
/* 490*/      CompilerInput previous = putCompilerInput(id, input);
/* 491*/      if (previous != null)
/* 492*/        report(JSError.make(DUPLICATE_EXTERN_INPUT, new String[] { input.getName() })); 
/*   0*/    } 
/* 495*/    for (CompilerInput input : this.inputs) {
/* 496*/      InputId id = input.getInputId();
/* 497*/      CompilerInput previous = putCompilerInput(id, input);
/* 498*/      if (previous != null)
/* 499*/        report(JSError.make(DUPLICATE_INPUT, new String[] { input.getName() })); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(SourceFile extern, SourceFile input, CompilerOptions options) {
/* 506*/    return compile(Lists.newArrayList((Object[])new SourceFile[] { extern }), Lists.newArrayList((Object[])new SourceFile[] { input }), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(SourceFile extern, JSSourceFile[] input, CompilerOptions options) {
/* 511*/    return compile(Lists.newArrayList((Object[])new SourceFile[] { extern }), Lists.newArrayList((Object[])input), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile extern, JSModule[] modules, CompilerOptions options) {
/* 516*/    return compileModules(Lists.newArrayList((Object[])new JSSourceFile[] { extern }), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile[] externs, JSSourceFile[] inputs, CompilerOptions options) {
/* 526*/    return compile(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])inputs), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T1 extends SourceFile, T2 extends SourceFile> Result compile(List<T1> externs, List<T2> inputs, CompilerOptions options) {
/* 537*/    Preconditions.checkState((this.jsRoot == null));
/*   0*/    try {
/* 540*/      init(externs, inputs, options);
/* 541*/      if (hasErrors())
/* 542*/        return getResult(); 
/* 544*/      return compile();
/*   0*/    } finally {
/* 546*/      Tracer t = newTracer("generateReport");
/* 547*/      this.errorManager.generateReport();
/* 548*/      stopTracer(t, "generateReport");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Result compile(JSSourceFile[] externs, JSModule[] modules, CompilerOptions options) {
/* 558*/    return compileModules(Lists.newArrayList((Object[])externs), Lists.newArrayList((Object[])modules), options);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends SourceFile> Result compileModules(List<T> externs, List<JSModule> modules, CompilerOptions options) {
/* 569*/    Preconditions.checkState((this.jsRoot == null));
/*   0*/    try {
/* 572*/      initModules(externs, modules, options);
/* 573*/      if (hasErrors())
/* 574*/        return getResult(); 
/* 576*/      return compile();
/*   0*/    } finally {
/* 578*/      Tracer t = newTracer("generateReport");
/* 579*/      this.errorManager.generateReport();
/* 580*/      stopTracer(t, "generateReport");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private Result compile() {
/* 585*/    return runInCompilerThread(new Callable<Result>() {
/*   0*/          public Result call() throws Exception {
/* 588*/            Compiler.this.compileInternal();
/* 589*/            return Compiler.this.getResult();
/*   0*/          }
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public void disableThreads() {
/* 599*/    this.useThreads = false;
/*   0*/  }
/*   0*/  
/*   0*/  private <T> T runInCompilerThread(Callable<T> callable) {
/* 603*/    return runCallable(callable, this.useThreads, this.options.tracer.isOn());
/*   0*/  }
/*   0*/  
/*   0*/  static <T> T runCallableWithLargeStack(Callable<T> callable) {
/* 607*/    return runCallable(callable, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  static <T> T runCallable(final Callable<T> callable, boolean useLargeStackThread, boolean trace) {
/* 620*/    final boolean dumpTraceReport = trace;
/* 621*/    final Object[] result = new Object[1];
/* 622*/    final Throwable[] exception = new Throwable[1];
/* 623*/    Runnable runnable = new Runnable() {
/*   0*/        public void run() {
/*   0*/          try {
/* 627*/            if (dumpTraceReport)
/* 628*/              Tracer.initCurrentThreadTrace(); 
/* 630*/            result[0] = callable.call();
/* 631*/          } catch (Throwable e) {
/* 632*/            exception[0] = e;
/*   0*/          } finally {
/* 634*/            if (dumpTraceReport)
/* 635*/              Tracer.logAndClearCurrentThreadTrace(); 
/*   0*/          } 
/*   0*/        }
/*   0*/      };
/* 641*/    if (useLargeStackThread) {
/* 642*/      Thread th = new Thread(null, runnable, "jscompiler", 1048576L);
/* 643*/      th.start();
/*   0*/      while (true) {
/*   0*/        try {
/* 646*/          th.join();
/*   0*/          break;
/* 648*/        } catch (InterruptedException interruptedException) {}
/*   0*/      } 
/*   0*/    } else {
/* 653*/      runnable.run();
/*   0*/    } 
/* 657*/    if (exception[0] != null)
/* 658*/      throw new RuntimeException(exception[0]); 
/* 661*/    return (T)result[0];
/*   0*/  }
/*   0*/  
/*   0*/  private void compileInternal() {
/* 665*/    setProgress(0.0D);
/* 666*/    parse();
/* 669*/    setProgress(0.15D);
/* 670*/    if (hasErrors())
/*   0*/      return; 
/* 674*/    if (!precheck())
/*   0*/      return; 
/* 678*/    if (this.options.nameAnonymousFunctionsOnly) {
/* 680*/      check();
/*   0*/      return;
/*   0*/    } 
/* 684*/    if (!this.options.skipAllPasses) {
/* 685*/      check();
/* 686*/      if (hasErrors())
/*   0*/        return; 
/* 690*/      if (this.options.isExternExportsEnabled() || this.options.externExportsPath != null)
/* 692*/        externExports(); 
/* 696*/      if (!this.options.ideMode)
/* 697*/        optimize(); 
/*   0*/    } 
/* 701*/    if (this.options.recordFunctionInformation)
/* 702*/      recordFunctionInformation(); 
/* 705*/    if (this.options.devMode == CompilerOptions.DevMode.START_AND_END)
/* 706*/      runSanityCheck(); 
/* 708*/    setProgress(1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public void parse() {
/* 712*/    parseInputs();
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig getPassConfig() {
/* 716*/    if (this.passes == null)
/* 717*/      this.passes = createPassConfigInternal(); 
/* 719*/    return this.passes;
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig createPassConfigInternal() {
/* 727*/    return new DefaultPassConfig(this.options);
/*   0*/  }
/*   0*/  
/*   0*/  public void setPassConfig(PassConfig passes) {
/* 741*/    Preconditions.checkNotNull(passes);
/* 743*/    if (this.passes != null)
/* 744*/      throw new IllegalStateException("this.passes has already been assigned"); 
/* 746*/    this.passes = passes;
/*   0*/  }
/*   0*/  
/*   0*/  boolean precheck() {
/* 756*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void check() {
/* 760*/    runCustomPasses(CustomPassExecutionTime.BEFORE_CHECKS);
/* 764*/    PhaseOptimizer phaseOptimizer = new PhaseOptimizer(this, this.tracker, new PhaseOptimizer.ProgressRange(getProgress(), 1.0D));
/* 766*/    if (this.options.devMode == CompilerOptions.DevMode.EVERY_PASS)
/* 767*/      phaseOptimizer.setSanityCheck(this.sanityCheck); 
/* 769*/    phaseOptimizer.consume(getPassConfig().getChecks());
/* 770*/    phaseOptimizer.process(this.externsRoot, this.jsRoot);
/* 771*/    if (hasErrors())
/*   0*/      return; 
/* 776*/    if (this.options.nameAnonymousFunctionsOnly)
/*   0*/      return; 
/* 780*/    if (this.options.removeTryCatchFinally)
/* 781*/      removeTryCatchFinally(); 
/* 784*/    if (this.options.getTweakProcessing().shouldStrip() || !this.options.stripTypes.isEmpty() || !this.options.stripNameSuffixes.isEmpty() || !this.options.stripTypePrefixes.isEmpty() || !this.options.stripNamePrefixes.isEmpty())
/* 789*/      stripCode(this.options.stripTypes, this.options.stripNameSuffixes, this.options.stripTypePrefixes, this.options.stripNamePrefixes); 
/* 793*/    runCustomPasses(CustomPassExecutionTime.BEFORE_OPTIMIZATIONS);
/*   0*/  }
/*   0*/  
/*   0*/  private void externExports() {
/* 797*/    logger.fine("Creating extern file for exports");
/* 798*/    startPass("externExports");
/* 800*/    ExternExportsPass pass = new ExternExportsPass(this);
/* 801*/    process(pass);
/* 803*/    this.externExports = pass.getGeneratedExterns();
/* 805*/    endPass();
/*   0*/  }
/*   0*/  
/*   0*/  void process(CompilerPass p) {
/* 810*/    p.process(this.externsRoot, this.jsRoot);
/*   0*/  }
/*   0*/  
/*   0*/  public Compiler(PrintStream stream) {
/* 813*/    this.sanityCheck = new PassFactory("sanityCheck", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 817*/          return new SanityCheck(compiler);
/*   0*/        }
/*   0*/      };
/* 875*/    this.currentTracer = null;
/* 876*/    this.currentPassName = null;
/*1506*/    this.syntheticCodeId = 0;
/*1919*/    this.recentChange = new CodeChangeHandler.RecentChange();
/*1921*/    this.codeChangeHandlers = Lists.newArrayList();
/*1927*/    this.synthesizedExternsInput = null;
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
/*   0*/      hoistExterns(this.externsRoot);
/*   0*/      boolean staleInputs = false;
/*   0*/      if (this.options.dependencyOptions.needsManagement() && this.options.closurePass) {
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
/*   0*/      hoistNoCompileFiles();
/*   0*/      if (staleInputs)
/*   0*/        repartitionInputs(); 
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
/*   0*/  private void hoistExterns(Node externsRoot) {
/*   0*/    boolean staleInputs = false;
/*   0*/    for (CompilerInput input : this.inputs) {
/*   0*/      if (this.options.dependencyOptions.needsManagement() && this.options.closurePass)
/*   0*/        if (!input.getProvides().isEmpty() || !input.getRequires().isEmpty())
/*   0*/          continue;  
/*   0*/      Node n = input.getAstRoot(this);
/*   0*/      if (n == null)
/*   0*/        continue; 
/*   0*/      JSDocInfo info = n.getJSDocInfo();
/*   0*/      if (info != null && info.isExterns()) {
/*   0*/        externsRoot.addChildToBack(n);
/*   0*/        input.setIsExtern(true);
/*   0*/        input.getModule().remove(input);
/*   0*/        this.externs.add(input);
/*   0*/        staleInputs = true;
/*   0*/      } 
/*   0*/    } 
/*   0*/    if (staleInputs)
/*   0*/      repartitionInputs(); 
/*   0*/  }
/*   0*/  
/*   0*/  private void hoistNoCompileFiles() {
/*   0*/    boolean staleInputs = false;
/*   0*/    for (CompilerInput input : this.inputs) {
/*   0*/      Node n = input.getAstRoot(this);
/*   0*/      if (n == null)
/*   0*/        continue; 
/*   0*/      JSDocInfo info = n.getJSDocInfo();
/*   0*/      if (info != null && info.isNoCompile()) {
/*   0*/        input.getModule().remove(input);
/*   0*/        staleInputs = true;
/*   0*/      } 
/*   0*/    } 
/*   0*/    if (staleInputs)
/*   0*/      repartitionInputs(); 
/*   0*/  }
/*   0*/  
/*   0*/  private void repartitionInputs() {
/*   0*/    fillEmptyModules(this.modules);
/*   0*/    rebuildInputsFromModules();
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
/*1931*/    this.codeChangeHandlers.add(handler);
/*   0*/  }
/*   0*/  
/*   0*/  void removeChangeHandler(CodeChangeHandler handler) {
/*1936*/    this.codeChangeHandlers.remove(handler);
/*   0*/  }
/*   0*/  
/*   0*/  public void reportCodeChange() {
/*1946*/    for (CodeChangeHandler handler : this.codeChangeHandlers)
/*1947*/      handler.reportChange(); 
/*   0*/  }
/*   0*/  
/*   0*/  public CodingConvention getCodingConvention() {
/*1953*/    CodingConvention convention = this.options.getCodingConvention();
/*1954*/    convention = (convention != null) ? convention : this.defaultCodingConvention;
/*1955*/    return convention;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIdeMode() {
/*1960*/    return this.options.ideMode;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean acceptEcmaScript5() {
/*1965*/    switch (this.options.getLanguageIn()) {
/*   0*/      case ECMASCRIPT5:
/*   0*/      case ECMASCRIPT5_STRICT:
/*1968*/        return true;
/*   0*/    } 
/*1970*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerOptions.LanguageMode languageMode() {
/*1974*/    return this.options.getLanguageIn();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean acceptConstKeyword() {
/*1979*/    return this.options.acceptConstKeyword;
/*   0*/  }
/*   0*/  
/*   0*/  Config getParserConfig() {
/*1984*/    if (this.parserConfig == null) {
/*   0*/      Config.LanguageMode mode;
/*1986*/      switch (this.options.getLanguageIn()) {
/*   0*/        case ECMASCRIPT3:
/*1988*/          mode = Config.LanguageMode.ECMASCRIPT3;
/*   0*/          break;
/*   0*/        case ECMASCRIPT5:
/*1991*/          mode = Config.LanguageMode.ECMASCRIPT5;
/*   0*/          break;
/*   0*/        case ECMASCRIPT5_STRICT:
/*1994*/          mode = Config.LanguageMode.ECMASCRIPT5_STRICT;
/*   0*/          break;
/*   0*/        default:
/*1997*/          throw new IllegalStateException("unexpected language mode");
/*   0*/      } 
/*2000*/      this.parserConfig = ParserRunner.createConfig(isIdeMode(), mode, acceptConstKeyword(), this.options.extraAnnotationNames);
/*   0*/    } 
/*2006*/    return this.parserConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeCheckingEnabled() {
/*2011*/    return this.options.checkTypes;
/*   0*/  }
/*   0*/  
/*   0*/  protected DiagnosticGroups getDiagnosticGroups() {
/*2024*/    return new DiagnosticGroups();
/*   0*/  }
/*   0*/  
/*   0*/  public void report(JSError error) {
/*2029*/    CheckLevel level = error.getDefaultLevel();
/*2030*/    if (this.warningsGuard != null) {
/*2031*/      CheckLevel newLevel = this.warningsGuard.level(error);
/*2032*/      if (newLevel != null)
/*2033*/        level = newLevel; 
/*   0*/    } 
/*2037*/    if (level.isOn()) {
/*2038*/      if ((getOptions()).errorHandler != null)
/*2039*/        (getOptions()).errorHandler.report(level, error); 
/*2041*/      this.errorManager.report(level, error);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CheckLevel getErrorLevel(JSError error) {
/*2047*/    Preconditions.checkNotNull(this.options);
/*2048*/    return this.warningsGuard.level(error);
/*   0*/  }
/*   0*/  
/*   0*/  void throwInternalError(String message, Exception cause) {
/*2056*/    String finalMessage = "INTERNAL COMPILER ERROR.\nPlease report this problem.\n" + message;
/*2060*/    RuntimeException e = new RuntimeException(finalMessage, cause);
/*2061*/    if (cause != null)
/*2062*/      e.setStackTrace(cause.getStackTrace()); 
/*2064*/    throw e;
/*   0*/  }
/*   0*/  
/*   0*/  public int getErrorCount() {
/*2072*/    return this.errorManager.getErrorCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getWarningCount() {
/*2079*/    return this.errorManager.getWarningCount();
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasHaltingErrors() {
/*2084*/    return (!isIdeMode() && getErrorCount() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasErrors() {
/*2097*/    return hasHaltingErrors();
/*   0*/  }
/*   0*/  
/*   0*/  void addToDebugLog(String str) {
/*2103*/    this.debugLog.append(str);
/*2104*/    this.debugLog.append('\n');
/*2105*/    logger.fine(str);
/*   0*/  }
/*   0*/  
/*   0*/  SourceFile getSourceFileByName(String sourceName) {
/*2112*/    if (sourceName != null) {
/*2113*/      CompilerInput input = this.inputsById.get(new InputId(sourceName));
/*2114*/      if (input != null)
/*2115*/        return input.getSourceFile(); 
/*   0*/    } 
/*2118*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceLine(String sourceName, int lineNumber) {
/*2123*/    if (lineNumber < 1)
/*2124*/      return null; 
/*2126*/    SourceFile input = getSourceFileByName(sourceName);
/*2127*/    if (input != null)
/*2128*/      return input.getLine(lineNumber); 
/*2130*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Region getSourceRegion(String sourceName, int lineNumber) {
/*2135*/    if (lineNumber < 1)
/*2136*/      return null; 
/*2138*/    SourceFile input = getSourceFileByName(sourceName);
/*2139*/    if (input != null)
/*2140*/      return input.getRegion(lineNumber); 
/*2142*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  Node getNodeForCodeInsertion(JSModule module) {
/*2151*/    if (module == null) {
/*2152*/      if (this.inputs.isEmpty())
/*2153*/        throw new IllegalStateException("No inputs"); 
/*2156*/      return ((CompilerInput)this.inputs.get(0)).getAstRoot(this);
/*   0*/    } 
/*2159*/    List<CompilerInput> moduleInputs = module.getInputs();
/*2160*/    if (moduleInputs.size() > 0)
/*2161*/      return ((CompilerInput)moduleInputs.get(0)).getAstRoot(this); 
/*2163*/    throw new IllegalStateException("Root module has no inputs");
/*   0*/  }
/*   0*/  
/*   0*/  public SourceMap getSourceMap() {
/*2167*/    return this.sourceMap;
/*   0*/  }
/*   0*/  
/*   0*/  VariableMap getVariableMap() {
/*2171*/    return (getPassConfig().getIntermediateState()).variableMap;
/*   0*/  }
/*   0*/  
/*   0*/  VariableMap getPropertyMap() {
/*2175*/    return (getPassConfig().getIntermediateState()).propertyMap;
/*   0*/  }
/*   0*/  
/*   0*/  CompilerOptions getOptions() {
/*2179*/    return this.options;
/*   0*/  }
/*   0*/  
/*   0*/  FunctionInformationMap getFunctionalInformationMap() {
/*2183*/    return this.functionInformationMap;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setLoggingLevel(Level level) {
/*2190*/    logger.setLevel(level);
/*   0*/  }
/*   0*/  
/*   0*/  public String getAstDotGraph() throws IOException {
/*2195*/    if (this.jsRoot != null) {
/*2196*/      ControlFlowAnalysis cfa = new ControlFlowAnalysis(this, true, false);
/*2197*/      cfa.process(null, this.jsRoot);
/*2198*/      return DotFormatter.toDot(this.jsRoot, cfa.getCfg());
/*   0*/    } 
/*2200*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public ErrorManager getErrorManager() {
/*2206*/    if (this.options == null)
/*2207*/      initOptions(newCompilerOptions()); 
/*2209*/    return this.errorManager;
/*   0*/  }
/*   0*/  
/*   0*/  List<CompilerInput> getInputsInOrder() {
/*2214*/    return Collections.unmodifiableList(this.inputs);
/*   0*/  }
/*   0*/  
/*   0*/  public Map<InputId, CompilerInput> getInputsById() {
/*2221*/    return Collections.unmodifiableMap(this.inputsById);
/*   0*/  }
/*   0*/  
/*   0*/  List<CompilerInput> getExternsInOrder() {
/*2228*/    return Collections.unmodifiableList(this.externs);
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
/*2262*/    IntermediateState state = new IntermediateState();
/*2263*/    state.externsRoot = this.externsRoot;
/*2264*/    state.jsRoot = this.jsRoot;
/*2265*/    state.externs = this.externs;
/*2266*/    state.inputs = this.inputs;
/*2267*/    state.modules = this.modules;
/*2268*/    state.passConfigState = getPassConfig().getIntermediateState();
/*2269*/    state.typeRegistry = this.typeRegistry;
/*2270*/    state.lifeCycleStage = getLifeCycleStage();
/*2271*/    state.injectedLibraries = Maps.newLinkedHashMap(this.injectedLibraries);
/*2273*/    return state;
/*   0*/  }
/*   0*/  
/*   0*/  public void setState(IntermediateState state) {
/*2281*/    this.externsRoot = state.externsRoot;
/*2282*/    this.jsRoot = state.jsRoot;
/*2283*/    this.externs = state.externs;
/*2284*/    this.inputs = state.inputs;
/*2285*/    this.modules = state.modules;
/*2286*/    this.passes = createPassConfigInternal();
/*2287*/    getPassConfig().setIntermediateState(state.passConfigState);
/*2288*/    this.typeRegistry = state.typeRegistry;
/*2289*/    setLifeCycleStage(state.lifeCycleStage);
/*2291*/    this.injectedLibraries.clear();
/*2292*/    this.injectedLibraries.putAll(state.injectedLibraries);
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  List<CompilerInput> getInputsForTesting() {
/*2297*/    return this.inputs;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  List<CompilerInput> getExternsForTesting() {
/*2302*/    return this.externs;
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasRegExpGlobalReferences() {
/*2307*/    return this.hasRegExpGlobalReferences;
/*   0*/  }
/*   0*/  
/*   0*/  void setHasRegExpGlobalReferences(boolean references) {
/*2312*/    this.hasRegExpGlobalReferences = references;
/*   0*/  }
/*   0*/  
/*   0*/  void updateGlobalVarReferences(Map<Scope.Var, ReferenceCollectingCallback.ReferenceCollection> refMapPatch, Node collectionRoot) {
/*2318*/    Preconditions.checkState((collectionRoot.isScript() || collectionRoot.isBlock()));
/*2320*/    if (this.globalRefMap == null)
/*2321*/      this.globalRefMap = new GlobalVarReferenceMap(getInputsInOrder(), getExternsInOrder()); 
/*2324*/    this.globalRefMap.updateGlobalVarReferences(refMapPatch, collectionRoot);
/*   0*/  }
/*   0*/  
/*   0*/  GlobalVarReferenceMap getGlobalVarReferences() {
/*2329*/    return this.globalRefMap;
/*   0*/  }
/*   0*/  
/*   0*/  CompilerInput getSynthesizedExternsInput() {
/*2334*/    if (this.synthesizedExternsInput == null)
/*2335*/      this.synthesizedExternsInput = newExternInput("{SyntheticVarsDeclar}"); 
/*2337*/    return this.synthesizedExternsInput;
/*   0*/  }
/*   0*/  
/*   0*/  public double getProgress() {
/*2342*/    return this.progress;
/*   0*/  }
/*   0*/  
/*   0*/  void setProgress(double newProgress) {
/*2347*/    if (newProgress > 1.0D) {
/*2348*/      this.progress = 1.0D;
/*2349*/    } else if (newProgress < 0.0D) {
/*2350*/      this.progress = 0.0D;
/*   0*/    } else {
/*2352*/      this.progress = newProgress;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceScript(JsAst ast) {
/*2364*/    CompilerInput input = getInput(ast.getInputId());
/*2365*/    if (!replaceIncrementalSourceAst(ast))
/*   0*/      return; 
/*2368*/    Node originalRoot = input.getAstRoot(this);
/*2370*/    processNewScript(ast, originalRoot);
/*   0*/  }
/*   0*/  
/*   0*/  public void addNewScript(JsAst ast) {
/*2381*/    if (!addNewSourceAst(ast))
/*   0*/      return; 
/*2384*/    Node emptyScript = new Node(132);
/*2385*/    InputId inputId = ast.getInputId();
/*2386*/    emptyScript.setInputId(inputId);
/*2387*/    emptyScript.setStaticSourceFile(SourceFile.fromCode(inputId.getIdName(), ""));
/*2390*/    processNewScript(ast, emptyScript);
/*   0*/  }
/*   0*/  
/*   0*/  private void processNewScript(JsAst ast, Node originalRoot) {
/*2394*/    Node js = ast.getAstRoot(this);
/*2395*/    Preconditions.checkNotNull(js);
/*2397*/    runHotSwap(originalRoot, js, getCleanupPassConfig());
/*2400*/    runHotSwapPass(null, null, (ensureDefaultPassConfig()).garbageCollectChecks);
/*2402*/    getTypeRegistry().clearNamedTypes();
/*2403*/    removeSyntheticVarsInput();
/*2405*/    runHotSwap(originalRoot, js, ensureDefaultPassConfig());
/*   0*/  }
/*   0*/  
/*   0*/  private void runHotSwap(Node originalRoot, Node js, PassConfig passConfig) {
/*2413*/    for (PassFactory passFactory : passConfig.getChecks())
/*2414*/      runHotSwapPass(originalRoot, js, passFactory); 
/*   0*/  }
/*   0*/  
/*   0*/  private void runHotSwapPass(Node originalRoot, Node js, PassFactory passFactory) {
/*2420*/    HotSwapCompilerPass pass = passFactory.getHotSwapPass(this);
/*2421*/    if (pass != null) {
/*2422*/      logger.info("Performing HotSwap for pass " + passFactory.getName());
/*2423*/      pass.hotSwapScript(js, originalRoot);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private PassConfig getCleanupPassConfig() {
/*2428*/    return new CleanupPasses(getOptions());
/*   0*/  }
/*   0*/  
/*   0*/  private void removeSyntheticVarsInput() {
/*2432*/    String sourceName = "{SyntheticVarsDeclar}";
/*2433*/    removeExternInput(new InputId(sourceName));
/*   0*/  }
/*   0*/  
/*   0*/  Node ensureLibraryInjected(String resourceName) {
/*2438*/    if (this.injectedLibraries.containsKey(resourceName))
/*2439*/      return null; 
/*2443*/    boolean isBase = "base".equals(resourceName);
/*2444*/    if (!isBase)
/*2445*/      ensureLibraryInjected("base"); 
/*2448*/    Node firstChild = loadLibraryCode(resourceName).removeChildren();
/*2449*/    Node lastChild = firstChild.getLastSibling();
/*2451*/    Node parent = getNodeForCodeInsertion(null);
/*2452*/    if (isBase) {
/*2453*/      parent.addChildrenToFront(firstChild);
/*   0*/    } else {
/*2455*/      parent.addChildrenAfter(firstChild, this.injectedLibraries.get("base"));
/*   0*/    } 
/*2458*/    reportCodeChange();
/*2460*/    this.injectedLibraries.put(resourceName, lastChild);
/*2461*/    return lastChild;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  Node loadLibraryCode(String resourceName) {
/*   0*/    String originalCode;
/*   0*/    try {
/*2469*/      originalCode = CharStreams.toString(new InputStreamReader(Compiler.class.getResourceAsStream(String.format("js/%s.js", new Object[] { resourceName })), Charsets.UTF_8));
/*2473*/    } catch (IOException e) {
/*2474*/      throw new RuntimeException(e);
/*   0*/    } 
/*2477*/    return Normalize.parseAndNormalizeSyntheticCode(this, originalCode, String.format("jscomp_%s_", new Object[] { resourceName }));
/*   0*/  }
/*   0*/  
/*   0*/  public static String getReleaseVersion() {
/*2484*/    ResourceBundle config = ResourceBundle.getBundle("com.google.javascript.jscomp.parsing.ParserConfig");
/*2485*/    return config.getString("compiler.version");
/*   0*/  }
/*   0*/  
/*   0*/  public static String getReleaseDate() {
/*2490*/    ResourceBundle config = ResourceBundle.getBundle("com.google.javascript.jscomp.parsing.ParserConfig");
/*2491*/    return config.getString("compiler.date");
/*   0*/  }
/*   0*/}
