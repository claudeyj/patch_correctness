/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Multimap;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.SourcePosition;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Collections;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class CompilerOptions implements Serializable, Cloneable {
/*   0*/  private boolean manageClosureDependencies = false;
/*   0*/  
/*   0*/  private static final long serialVersionUID = 7L;
/*   0*/  
/*   0*/  private LanguageMode languageIn;
/*   0*/  
/*   0*/  private LanguageMode languageOut;
/*   0*/  
/*   0*/  boolean acceptConstKeyword;
/*   0*/  
/*   0*/  private boolean assumeStrictThis;
/*   0*/  
/*   0*/  public boolean ideMode;
/*   0*/  
/*   0*/  boolean inferTypes;
/*   0*/  
/*   0*/  boolean skipAllPasses;
/*   0*/  
/*   0*/  boolean nameAnonymousFunctionsOnly;
/*   0*/  
/*   0*/  DevMode devMode;
/*   0*/  
/*   0*/  public enum Reach {
/*  44*/    ALL, LOCAL_ONLY, NONE;
/*   0*/  }
/*   0*/  
/* 117*/  DependencyOptions dependencyOptions = new DependencyOptions();
/*   0*/  
/* 121*/  public transient MessageBundle messageBundle = null;
/*   0*/  
/*   0*/  public boolean checkSymbols;
/*   0*/  
/*   0*/  public CheckLevel aggressiveVarCheck;
/*   0*/  
/*   0*/  public boolean checkSuspiciousCode;
/*   0*/  
/*   0*/  public boolean checkControlStructures;
/*   0*/  
/*   0*/  public boolean checkTypes;
/*   0*/  
/*   0*/  boolean tightenTypes;
/*   0*/  
/*   0*/  public CheckLevel reportMissingOverride;
/*   0*/  
/*   0*/  CheckLevel reportUnknownTypes;
/*   0*/  
/*   0*/  public CheckLevel checkRequires;
/*   0*/  
/*   0*/  public CheckLevel checkProvides;
/*   0*/  
/*   0*/  public CheckLevel checkGlobalNamesLevel;
/*   0*/  
/*   0*/  public CheckLevel brokenClosureRequiresLevel;
/*   0*/  
/*   0*/  public CheckLevel checkGlobalThisLevel;
/*   0*/  
/*   0*/  public CheckLevel checkMissingGetCssNameLevel;
/*   0*/  
/*   0*/  public String checkMissingGetCssNameBlacklist;
/*   0*/  
/*   0*/  boolean checkCaja;
/*   0*/  
/*   0*/  Set<String> extraAnnotationNames;
/*   0*/  
/*   0*/  public boolean foldConstants;
/*   0*/  
/*   0*/  public boolean deadAssignmentElimination;
/*   0*/  
/*   0*/  public boolean inlineConstantVars;
/*   0*/  
/*   0*/  public boolean inlineFunctions;
/*   0*/  
/*   0*/  public boolean inlineLocalFunctions;
/*   0*/  
/*   0*/  boolean assumeClosuresOnlyCaptureReferences;
/*   0*/  
/*   0*/  public boolean crossModuleCodeMotion;
/*   0*/  
/*   0*/  public boolean coalesceVariableNames;
/*   0*/  
/*   0*/  public boolean crossModuleMethodMotion;
/*   0*/  
/*   0*/  public boolean inlineGetters;
/*   0*/  
/*   0*/  public boolean inlineVariables;
/*   0*/  
/*   0*/  boolean inlineLocalVariables;
/*   0*/  
/*   0*/  public boolean flowSensitiveInlineVariables;
/*   0*/  
/*   0*/  public boolean smartNameRemoval;
/*   0*/  
/*   0*/  public boolean removeDeadCode;
/*   0*/  
/*   0*/  public CheckLevel checkUnreachableCode;
/*   0*/  
/*   0*/  public CheckLevel checkMissingReturn;
/*   0*/  
/*   0*/  public boolean extractPrototypeMemberDeclarations;
/*   0*/  
/*   0*/  public boolean removeUnusedPrototypeProperties;
/*   0*/  
/*   0*/  public boolean removeUnusedPrototypePropertiesInExterns;
/*   0*/  
/*   0*/  public boolean removeUnusedVars;
/*   0*/  
/*   0*/  public boolean removeUnusedLocalVars;
/*   0*/  
/*   0*/  public boolean aliasExternals;
/*   0*/  
/*   0*/  String aliasableGlobals;
/*   0*/  
/*   0*/  String unaliasableGlobals;
/*   0*/  
/*   0*/  public boolean collapseVariableDeclarations;
/*   0*/  
/*   0*/  boolean groupVariableDeclarations;
/*   0*/  
/*   0*/  public boolean collapseAnonymousFunctions;
/*   0*/  
/*   0*/  public Set<String> aliasableStrings;
/*   0*/  
/*   0*/  public String aliasStringsBlacklist;
/*   0*/  
/*   0*/  public boolean aliasAllStrings;
/*   0*/  
/*   0*/  boolean outputJsStringUsage;
/*   0*/  
/*   0*/  public boolean convertToDottedProperties;
/*   0*/  
/*   0*/  public boolean rewriteFunctionExpressions;
/*   0*/  
/*   0*/  public boolean optimizeParameters;
/*   0*/  
/*   0*/  public boolean optimizeReturns;
/*   0*/  
/*   0*/  public boolean optimizeCalls;
/*   0*/  
/*   0*/  public boolean optimizeArgumentsArray;
/*   0*/  
/*   0*/  boolean chainCalls;
/*   0*/  
/*   0*/  public VariableRenamingPolicy variableRenaming;
/*   0*/  
/*   0*/  public PropertyRenamingPolicy propertyRenaming;
/*   0*/  
/*   0*/  boolean propertyAffinity;
/*   0*/  
/*   0*/  public boolean labelRenaming;
/*   0*/  
/*   0*/  public boolean reserveRawExports;
/*   0*/  
/*   0*/  boolean shadowVariables;
/*   0*/  
/*   0*/  public boolean generatePseudoNames;
/*   0*/  
/*   0*/  public String renamePrefix;
/*   0*/  
/*   0*/  public String renamePrefixNamespace;
/*   0*/  
/*   0*/  public boolean aliasKeywords;
/*   0*/  
/*   0*/  public boolean collapseProperties;
/*   0*/  
/*   0*/  boolean collapseObjectLiterals;
/*   0*/  
/*   0*/  boolean collapsePropertiesOnExternTypes;
/*   0*/  
/*   0*/  public boolean devirtualizePrototypeMethods;
/*   0*/  
/*   0*/  public boolean computeFunctionSideEffects;
/*   0*/  
/*   0*/  String debugFunctionSideEffectsPath;
/*   0*/  
/*   0*/  public boolean disambiguateProperties;
/*   0*/  
/*   0*/  public boolean ambiguateProperties;
/*   0*/  
/*   0*/  public AnonymousFunctionNamingPolicy anonymousFunctionNaming;
/*   0*/  
/*   0*/  public byte[] inputVariableMapSerialized;
/*   0*/  
/*   0*/  public byte[] inputPropertyMapSerialized;
/*   0*/  
/*   0*/  public boolean exportTestFunctions;
/*   0*/  
/*   0*/  boolean specializeInitialModule;
/*   0*/  
/*   0*/  boolean runtimeTypeCheck;
/*   0*/  
/*   0*/  String runtimeTypeCheckLogFunction;
/*   0*/  
/*   0*/  private CodingConvention codingConvention;
/*   0*/  
/*   0*/  boolean ignoreCajaProperties;
/*   0*/  
/*   0*/  public String syntheticBlockStartMarker;
/*   0*/  
/*   0*/  public String syntheticBlockEndMarker;
/*   0*/  
/*   0*/  public String locale;
/*   0*/  
/*   0*/  public boolean markAsCompiled;
/*   0*/  
/*   0*/  public boolean removeTryCatchFinally;
/*   0*/  
/*   0*/  public boolean closurePass;
/*   0*/  
/*   0*/  public boolean jqueryPass;
/*   0*/  
/*   0*/  boolean rewriteNewDateGoogNow;
/*   0*/  
/*   0*/  boolean removeAbstractMethods;
/*   0*/  
/*   0*/  boolean removeClosureAsserts;
/*   0*/  
/*   0*/  public boolean gatherCssNames;
/*   0*/  
/*   0*/  public Set<String> stripTypes;
/*   0*/  
/*   0*/  public Set<String> stripNameSuffixes;
/*   0*/  
/*   0*/  public Set<String> stripNamePrefixes;
/*   0*/  
/*   0*/  public Set<String> stripTypePrefixes;
/*   0*/  
/*   0*/  public transient Multimap<CustomPassExecutionTime, CompilerPass> customPasses;
/*   0*/  
/*   0*/  public boolean markNoSideEffectCalls;
/*   0*/  
/*   0*/  private Map<String, Object> defineReplacements;
/*   0*/  
/*   0*/  private TweakProcessing tweakProcessing;
/*   0*/  
/*   0*/  private Map<String, Object> tweakReplacements;
/*   0*/  
/*   0*/  public boolean moveFunctionDeclarations;
/*   0*/  
/*   0*/  public String instrumentationTemplate;
/*   0*/  
/*   0*/  String appNameStr;
/*   0*/  
/*   0*/  public boolean recordFunctionInformation;
/*   0*/  
/*   0*/  public boolean generateExports;
/*   0*/  
/*   0*/  public CssRenamingMap cssRenamingMap;
/*   0*/  
/*   0*/  boolean processObjectPropertyString;
/*   0*/  
/*   0*/  public void setAggressiveVarCheck(CheckLevel level) {
/* 134*/    this.aggressiveVarCheck = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTightenTypes(boolean tighten) {
/* 150*/    this.tightenTypes = tighten;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReportMissingOverride(CheckLevel level) {
/* 160*/    this.reportMissingOverride = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReportUnknownTypes(CheckLevel level) {
/* 167*/    this.reportUnknownTypes = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckRequires(CheckLevel level) {
/* 174*/    this.checkRequires = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckProvides(CheckLevel level) {
/* 181*/    this.checkProvides = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckGlobalNamesLevel(CheckLevel level) {
/* 191*/    this.checkGlobalNamesLevel = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBrokenClosureRequiresLevel(CheckLevel level) {
/* 198*/    this.brokenClosureRequiresLevel = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckGlobalThisLevel(CheckLevel level) {
/* 212*/    this.checkGlobalThisLevel = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckMissingGetCssNameLevel(CheckLevel level) {
/* 222*/    this.checkMissingGetCssNameLevel = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckCaja(boolean check) {
/* 234*/    this.checkCaja = check;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckUnreachableCode(CheckLevel level) {
/* 297*/    this.checkUnreachableCode = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckMissingReturn(CheckLevel level) {
/* 304*/    this.checkMissingReturn = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasableGlobals(String names) {
/* 333*/    this.aliasableGlobals = names;
/*   0*/  }
/*   0*/  
/*   0*/  public void setUnaliasableGlobals(String names) {
/* 343*/    this.unaliasableGlobals = names;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCollapseObjectLiterals(boolean enabled) {
/* 457*/    this.collapseObjectLiterals = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSpecializeInitialModule(boolean enabled) {
/* 505*/    this.specializeInitialModule = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setIgnoreCajaProperties(boolean enabled) {
/* 529*/    this.ignoreCajaProperties = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAppNameStr(String appNameStr) {
/* 604*/    this.appNameStr = appNameStr;
/*   0*/  }
/*   0*/  
/*   0*/  boolean replaceIdGenerators = true;
/*   0*/  
/*   0*/  Set<String> idGenerators;
/*   0*/  
/*   0*/  List<String> replaceStringsFunctionDescriptions;
/*   0*/  
/*   0*/  String replaceStringsPlaceholderToken;
/*   0*/  
/*   0*/  Set<String> replaceStringsReservedStrings;
/*   0*/  
/*   0*/  Map<String, CheckLevel> propertyInvalidationErrors;
/*   0*/  
/*   0*/  boolean transformAMDToCJSModules = false;
/*   0*/  
/*   0*/  boolean processCommonJSModules = false;
/*   0*/  
/* 640*/  String commonJSModulePathPrefix = ProcessCommonJSModules.DEFAULT_FILENAME_PREFIX;
/*   0*/  
/*   0*/  public boolean prettyPrint;
/*   0*/  
/*   0*/  public boolean lineBreak;
/*   0*/  
/*   0*/  public boolean preferLineBreakAtEndOfFile;
/*   0*/  
/*   0*/  public boolean printInputDelimiter;
/*   0*/  
/* 661*/  public String inputDelimiter = "// Input %num%";
/*   0*/  
/*   0*/  String reportPath;
/*   0*/  
/*   0*/  TracerMode tracer;
/*   0*/  
/*   0*/  private boolean colorizeErrorOutput;
/*   0*/  
/*   0*/  public ErrorFormat errorFormat;
/*   0*/  
/*   0*/  public void setReportPath(String reportPath) {
/* 667*/    this.reportPath = reportPath;
/*   0*/  }
/*   0*/  
/*   0*/  public TracerMode getTracerMode() {
/* 673*/    return this.tracer;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTracerMode(TracerMode mode) {
/* 677*/    this.tracer = mode;
/*   0*/  }
/*   0*/  
/* 684*/  private ComposeWarningsGuard warningsGuard = new ComposeWarningsGuard(new WarningsGuard[0]);
/*   0*/  
/* 686*/  int summaryDetailLevel = 1;
/*   0*/  
/* 688*/  int lineLengthThreshold = 500;
/*   0*/  
/*   0*/  private boolean externExports;
/*   0*/  
/*   0*/  String externExportsPath;
/*   0*/  
/*   0*/  String nameReferenceReportPath;
/*   0*/  
/*   0*/  String nameReferenceGraphPath;
/*   0*/  
/*   0*/  public String sourceMapOutputPath;
/*   0*/  
/*   0*/  public void setNameReferenceReportPath(String filePath) {
/* 707*/    this.nameReferenceReportPath = filePath;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNameReferenceGraphPath(String filePath) {
/* 714*/    this.nameReferenceGraphPath = filePath;
/*   0*/  }
/*   0*/  
/* 725*/  public SourceMap.DetailLevel sourceMapDetailLevel = SourceMap.DetailLevel.SYMBOLS;
/*   0*/  
/* 729*/  public SourceMap.Format sourceMapFormat = SourceMap.Format.DEFAULT;
/*   0*/  
/* 732*/  public List<SourceMap.LocationMapping> sourceMapLocationMappings = Collections.emptyList();
/*   0*/  
/*   0*/  String outputCharset;
/*   0*/  
/*   0*/  boolean looseTypes;
/*   0*/  
/*   0*/  boolean protectHiddenSideEffects;
/*   0*/  
/*   0*/  private transient AliasTransformationHandler aliasHandler;
/*   0*/  
/*   0*/  transient ErrorHandler errorHandler;
/*   0*/  
/*   0*/  public void setProtectHiddenSideEffects(boolean enable) {
/* 755*/    this.protectHiddenSideEffects = enable;
/*   0*/  }
/*   0*/  
/*   0*/  public CompilerOptions() {
/* 776*/    this.languageIn = LanguageMode.ECMASCRIPT3;
/* 779*/    this.acceptConstKeyword = false;
/* 782*/    this.skipAllPasses = false;
/* 783*/    this.nameAnonymousFunctionsOnly = false;
/* 784*/    this.devMode = DevMode.OFF;
/* 785*/    this.checkSymbols = false;
/* 786*/    this.aggressiveVarCheck = CheckLevel.OFF;
/* 787*/    this.checkSuspiciousCode = false;
/* 788*/    this.checkControlStructures = false;
/* 789*/    this.checkTypes = false;
/* 790*/    this.tightenTypes = false;
/* 791*/    this.reportMissingOverride = CheckLevel.OFF;
/* 792*/    this.reportUnknownTypes = CheckLevel.OFF;
/* 793*/    this.checkRequires = CheckLevel.OFF;
/* 794*/    this.checkProvides = CheckLevel.OFF;
/* 795*/    this.checkGlobalNamesLevel = CheckLevel.OFF;
/* 796*/    this.brokenClosureRequiresLevel = CheckLevel.ERROR;
/* 797*/    this.checkGlobalThisLevel = CheckLevel.OFF;
/* 798*/    this.checkUnreachableCode = CheckLevel.OFF;
/* 799*/    this.checkMissingReturn = CheckLevel.OFF;
/* 800*/    this.checkMissingGetCssNameLevel = CheckLevel.OFF;
/* 801*/    this.checkMissingGetCssNameBlacklist = null;
/* 802*/    this.checkCaja = false;
/* 803*/    this.computeFunctionSideEffects = false;
/* 804*/    this.chainCalls = false;
/* 805*/    this.extraAnnotationNames = null;
/* 808*/    this.foldConstants = false;
/* 809*/    this.coalesceVariableNames = false;
/* 810*/    this.deadAssignmentElimination = false;
/* 811*/    this.inlineConstantVars = false;
/* 812*/    this.inlineFunctions = false;
/* 813*/    this.inlineLocalFunctions = false;
/* 814*/    this.assumeStrictThis = false;
/* 815*/    this.assumeClosuresOnlyCaptureReferences = false;
/* 816*/    this.crossModuleCodeMotion = false;
/* 817*/    this.crossModuleMethodMotion = false;
/* 818*/    this.inlineGetters = false;
/* 819*/    this.inlineVariables = false;
/* 820*/    this.inlineLocalVariables = false;
/* 821*/    this.smartNameRemoval = false;
/* 822*/    this.removeDeadCode = false;
/* 823*/    this.extractPrototypeMemberDeclarations = false;
/* 824*/    this.removeUnusedPrototypeProperties = false;
/* 825*/    this.removeUnusedPrototypePropertiesInExterns = false;
/* 826*/    this.removeUnusedVars = false;
/* 827*/    this.removeUnusedLocalVars = false;
/* 828*/    this.aliasExternals = false;
/* 829*/    this.collapseVariableDeclarations = false;
/* 830*/    this.groupVariableDeclarations = false;
/* 831*/    this.collapseAnonymousFunctions = false;
/* 832*/    this.aliasableStrings = Collections.emptySet();
/* 833*/    this.aliasStringsBlacklist = "";
/* 834*/    this.aliasAllStrings = false;
/* 835*/    this.outputJsStringUsage = false;
/* 836*/    this.convertToDottedProperties = false;
/* 837*/    this.rewriteFunctionExpressions = false;
/* 838*/    this.optimizeParameters = false;
/* 839*/    this.optimizeReturns = false;
/* 842*/    this.variableRenaming = VariableRenamingPolicy.OFF;
/* 843*/    this.propertyRenaming = PropertyRenamingPolicy.OFF;
/* 844*/    this.propertyAffinity = false;
/* 845*/    this.labelRenaming = false;
/* 846*/    this.generatePseudoNames = false;
/* 847*/    this.shadowVariables = false;
/* 848*/    this.renamePrefix = null;
/* 849*/    this.aliasKeywords = false;
/* 850*/    this.collapseProperties = false;
/* 851*/    this.collapsePropertiesOnExternTypes = false;
/* 852*/    this.collapseObjectLiterals = false;
/* 853*/    this.devirtualizePrototypeMethods = false;
/* 854*/    this.disambiguateProperties = false;
/* 855*/    this.ambiguateProperties = false;
/* 856*/    this.anonymousFunctionNaming = AnonymousFunctionNamingPolicy.OFF;
/* 857*/    this.exportTestFunctions = false;
/* 860*/    this.runtimeTypeCheck = false;
/* 861*/    this.runtimeTypeCheckLogFunction = null;
/* 862*/    this.ignoreCajaProperties = false;
/* 863*/    this.syntheticBlockStartMarker = null;
/* 864*/    this.syntheticBlockEndMarker = null;
/* 865*/    this.locale = null;
/* 866*/    this.markAsCompiled = false;
/* 867*/    this.removeTryCatchFinally = false;
/* 868*/    this.closurePass = false;
/* 869*/    this.jqueryPass = false;
/* 870*/    this.rewriteNewDateGoogNow = true;
/* 871*/    this.removeAbstractMethods = true;
/* 872*/    this.removeClosureAsserts = false;
/* 873*/    this.stripTypes = Collections.emptySet();
/* 874*/    this.stripNameSuffixes = Collections.emptySet();
/* 875*/    this.stripNamePrefixes = Collections.emptySet();
/* 876*/    this.stripTypePrefixes = Collections.emptySet();
/* 877*/    this.customPasses = null;
/* 878*/    this.markNoSideEffectCalls = false;
/* 879*/    this.defineReplacements = Maps.newHashMap();
/* 880*/    this.tweakProcessing = TweakProcessing.OFF;
/* 881*/    this.tweakReplacements = Maps.newHashMap();
/* 882*/    this.moveFunctionDeclarations = false;
/* 883*/    this.instrumentationTemplate = null;
/* 884*/    this.appNameStr = "";
/* 885*/    this.recordFunctionInformation = false;
/* 886*/    this.generateExports = false;
/* 887*/    this.cssRenamingMap = null;
/* 888*/    this.processObjectPropertyString = false;
/* 889*/    this.idGenerators = Collections.emptySet();
/* 890*/    this.replaceStringsFunctionDescriptions = Collections.emptyList();
/* 891*/    this.replaceStringsPlaceholderToken = "";
/* 892*/    this.replaceStringsReservedStrings = Collections.emptySet();
/* 893*/    this.propertyInvalidationErrors = Maps.newHashMap();
/* 896*/    this.printInputDelimiter = false;
/* 897*/    this.prettyPrint = false;
/* 898*/    this.lineBreak = false;
/* 899*/    this.preferLineBreakAtEndOfFile = false;
/* 900*/    this.reportPath = null;
/* 901*/    this.tracer = TracerMode.OFF;
/* 902*/    this.colorizeErrorOutput = false;
/* 903*/    this.errorFormat = ErrorFormat.SINGLELINE;
/* 904*/    this.debugFunctionSideEffectsPath = null;
/* 905*/    this.externExports = false;
/* 906*/    this.nameReferenceReportPath = null;
/* 907*/    this.nameReferenceGraphPath = null;
/* 910*/    this.aliasHandler = NULL_ALIAS_TRANSFORMATION_HANDLER;
/* 911*/    this.errorHandler = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, Node> getDefineReplacements() {
/* 918*/    return getReplacementsHelper(this.defineReplacements);
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, Node> getTweakReplacements() {
/* 925*/    return getReplacementsHelper(this.tweakReplacements);
/*   0*/  }
/*   0*/  
/*   0*/  private static Map<String, Node> getReplacementsHelper(Map<String, Object> source) {
/* 933*/    Map<String, Node> map = Maps.newHashMap();
/* 934*/    for (Map.Entry<String, Object> entry : source.entrySet()) {
/* 935*/      String name = entry.getKey();
/* 936*/      Object value = entry.getValue();
/* 937*/      if (value instanceof Boolean) {
/* 938*/        map.put(name, NodeUtil.booleanNode((Boolean)value));
/*   0*/        continue;
/*   0*/      } 
/* 939*/      if (value instanceof Integer) {
/* 940*/        map.put(name, IR.number((Integer)value));
/*   0*/        continue;
/*   0*/      } 
/* 941*/      if (value instanceof Double) {
/* 942*/        map.put(name, IR.number((Double)value));
/*   0*/        continue;
/*   0*/      } 
/* 944*/      Preconditions.checkState(value instanceof String);
/* 945*/      map.put(name, IR.string((String)value));
/*   0*/    } 
/* 948*/    return map;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDefineToBooleanLiteral(String defineName, boolean value) {
/* 956*/    this.defineReplacements.put(defineName, new Boolean(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void setDefineToStringLiteral(String defineName, String value) {
/* 964*/    this.defineReplacements.put(defineName, value);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDefineToNumberLiteral(String defineName, int value) {
/* 972*/    this.defineReplacements.put(defineName, new Integer(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void setDefineToDoubleLiteral(String defineName, double value) {
/* 980*/    this.defineReplacements.put(defineName, new Double(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void setTweakToBooleanLiteral(String tweakId, boolean value) {
/* 988*/    this.tweakReplacements.put(tweakId, new Boolean(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void setTweakToStringLiteral(String tweakId, String value) {
/* 996*/    this.tweakReplacements.put(tweakId, value);
/*   0*/  }
/*   0*/  
/*   0*/  public void setTweakToNumberLiteral(String tweakId, int value) {
/*1004*/    this.tweakReplacements.put(tweakId, new Integer(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void setTweakToDoubleLiteral(String tweakId, double value) {
/*1012*/    this.tweakReplacements.put(tweakId, new Double(value));
/*   0*/  }
/*   0*/  
/*   0*/  public void skipAllCompilerPasses() {
/*1019*/    this.skipAllPasses = true;
/*   0*/  }
/*   0*/  
/*   0*/  boolean enables(DiagnosticGroup type) {
/*1027*/    return this.warningsGuard.enables(type);
/*   0*/  }
/*   0*/  
/*   0*/  boolean disables(DiagnosticGroup type) {
/*1035*/    return this.warningsGuard.disables(type);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWarningLevel(DiagnosticGroup type, CheckLevel level) {
/*1042*/    addWarningsGuard(new DiagnosticGroupWarningsGuard(type, level));
/*   0*/  }
/*   0*/  
/*   0*/  WarningsGuard getWarningsGuard() {
/*1046*/    return this.warningsGuard;
/*   0*/  }
/*   0*/  
/*   0*/  public void resetWarningsGuard() {
/*1053*/    this.warningsGuard = new ComposeWarningsGuard(new WarningsGuard[0]);
/*   0*/  }
/*   0*/  
/*   0*/  void useEmergencyFailSafe() {
/*1061*/    this.warningsGuard = this.warningsGuard.makeEmergencyFailSafeGuard();
/*   0*/  }
/*   0*/  
/*   0*/  public void addWarningsGuard(WarningsGuard guard) {
/*1068*/    this.warningsGuard.addGuard(guard);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenamingPolicy(VariableRenamingPolicy newVariablePolicy, PropertyRenamingPolicy newPropertyPolicy) {
/*1078*/    this.variableRenaming = newVariablePolicy;
/*1079*/    this.propertyRenaming = newPropertyPolicy;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPropertyAffinity(boolean useAffinity) {
/*1083*/    this.propertyAffinity = useAffinity;
/*   0*/  }
/*   0*/  
/*   0*/  public void setShadowVariables(boolean shadow) {
/*1088*/    this.shadowVariables = shadow;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCollapsePropertiesOnExternTypes(boolean collapse) {
/*1097*/    this.collapsePropertiesOnExternTypes = collapse;
/*   0*/  }
/*   0*/  
/*   0*/  public void setProcessObjectPropertyString(boolean process) {
/*1104*/    this.processObjectPropertyString = process;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReplaceIdGenerators(boolean replaceIdGenerators) {
/*1111*/    this.replaceIdGenerators = replaceIdGenerators;
/*   0*/  }
/*   0*/  
/*   0*/  public void setIdGenerators(Set<String> idGenerators) {
/*1118*/    this.idGenerators = Sets.newHashSet(idGenerators);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineFunctions(Reach reach) {
/*1125*/    switch (reach) {
/*   0*/      case ALL:
/*1127*/        this.inlineFunctions = true;
/*1128*/        this.inlineLocalFunctions = true;
/*   0*/        break;
/*   0*/      case LOCAL_ONLY:
/*1131*/        this.inlineFunctions = false;
/*1132*/        this.inlineLocalFunctions = true;
/*   0*/        break;
/*   0*/      case NONE:
/*1135*/        this.inlineFunctions = false;
/*1136*/        this.inlineLocalFunctions = false;
/*   0*/        break;
/*   0*/      default:
/*1139*/        throw new IllegalStateException("unexpected");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineVariables(Reach reach) {
/*1147*/    switch (reach) {
/*   0*/      case ALL:
/*1149*/        this.inlineVariables = true;
/*1150*/        this.inlineLocalVariables = true;
/*   0*/        break;
/*   0*/      case LOCAL_ONLY:
/*1153*/        this.inlineVariables = false;
/*1154*/        this.inlineLocalVariables = true;
/*   0*/        break;
/*   0*/      case NONE:
/*1157*/        this.inlineVariables = false;
/*1158*/        this.inlineLocalVariables = false;
/*   0*/        break;
/*   0*/      default:
/*1161*/        throw new IllegalStateException("unexpected");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void setRemoveUnusedVariable(Reach reach) {
/*1170*/    setRemoveUnusedVariables(reach);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveUnusedVariables(Reach reach) {
/*1177*/    switch (reach) {
/*   0*/      case ALL:
/*1179*/        this.removeUnusedVars = true;
/*1180*/        this.removeUnusedLocalVars = true;
/*   0*/        break;
/*   0*/      case LOCAL_ONLY:
/*1183*/        this.removeUnusedVars = false;
/*1184*/        this.removeUnusedLocalVars = true;
/*   0*/        break;
/*   0*/      case NONE:
/*1187*/        this.removeUnusedVars = false;
/*1188*/        this.removeUnusedLocalVars = false;
/*   0*/        break;
/*   0*/      default:
/*1191*/        throw new IllegalStateException("unexpected");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setReplaceStringsConfiguration(String placeholderToken, List<String> functionDescriptors) {
/*1200*/    this.replaceStringsPlaceholderToken = placeholderToken;
/*1201*/    this.replaceStringsFunctionDescriptions = Lists.newArrayList(functionDescriptors);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRewriteNewDateGoogNow(boolean rewrite) {
/*1206*/    this.rewriteNewDateGoogNow = rewrite;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveAbstractMethods(boolean remove) {
/*1210*/    this.removeAbstractMethods = remove;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveClosureAsserts(boolean remove) {
/*1214*/    this.removeClosureAsserts = remove;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNameAnonymousFunctionsOnly(boolean value) {
/*1221*/    this.nameAnonymousFunctionsOnly = value;
/*   0*/  }
/*   0*/  
/*   0*/  public void setColorizeErrorOutput(boolean colorizeErrorOutput) {
/*1225*/    this.colorizeErrorOutput = colorizeErrorOutput;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldColorizeErrorOutput() {
/*1229*/    return this.colorizeErrorOutput;
/*   0*/  }
/*   0*/  
/*   0*/  public void setChainCalls(boolean value) {
/*1236*/    this.chainCalls = value;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAcceptConstKeyword(boolean value) {
/*1243*/    this.acceptConstKeyword = value;
/*   0*/  }
/*   0*/  
/*   0*/  public void enableRuntimeTypeCheck(String logFunction) {
/*1253*/    this.runtimeTypeCheck = true;
/*1254*/    this.runtimeTypeCheckLogFunction = logFunction;
/*   0*/  }
/*   0*/  
/*   0*/  public void disableRuntimeTypeCheck() {
/*1258*/    this.runtimeTypeCheck = false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGenerateExports(boolean generateExports) {
/*1262*/    this.generateExports = generateExports;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCodingConvention(CodingConvention codingConvention) {
/*1266*/    this.codingConvention = codingConvention;
/*   0*/  }
/*   0*/  
/*   0*/  public CodingConvention getCodingConvention() {
/*1270*/    return this.codingConvention;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDependencyOptions(DependencyOptions options) {
/*1278*/    Preconditions.checkNotNull(options);
/*1279*/    this.dependencyOptions = options;
/*   0*/  }
/*   0*/  
/*   0*/  public void setManageClosureDependencies(boolean newVal) {
/*1287*/    this.dependencyOptions.setDependencySorting((newVal || this.dependencyOptions.shouldSortDependencies()));
/*1289*/    this.dependencyOptions.setDependencyPruning((newVal || this.dependencyOptions.shouldPruneDependencies()));
/*1291*/    this.dependencyOptions.setMoocherDropping(false);
/*1292*/    this.manageClosureDependencies = newVal;
/*   0*/  }
/*   0*/  
/*   0*/  public void setManageClosureDependencies(List<String> entryPoints) {
/*1305*/    Preconditions.checkNotNull(entryPoints);
/*1306*/    setManageClosureDependencies(true);
/*1307*/    this.dependencyOptions.setEntryPoints(entryPoints);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSummaryDetailLevel(int summaryDetailLevel) {
/*1318*/    this.summaryDetailLevel = summaryDetailLevel;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void enableExternExports(boolean enabled) {
/*1326*/    this.externExports = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setExtraAnnotationNames(Set<String> extraAnnotationNames) {
/*1330*/    this.extraAnnotationNames = Sets.newHashSet(extraAnnotationNames);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExternExportsEnabled() {
/*1334*/    return this.externExports;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOutputCharset(String charsetName) {
/*1341*/    this.outputCharset = charsetName;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTweakProcessing(TweakProcessing tweakProcessing) {
/*1348*/    this.tweakProcessing = tweakProcessing;
/*   0*/  }
/*   0*/  
/*   0*/  public TweakProcessing getTweakProcessing() {
/*1352*/    return this.tweakProcessing;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLanguageIn(LanguageMode languageIn) {
/*1359*/    this.languageIn = languageIn;
/*1360*/    this.languageOut = languageIn;
/*   0*/  }
/*   0*/  
/*   0*/  public LanguageMode getLanguageIn() {
/*1364*/    return this.languageIn;
/*   0*/  }
/*   0*/  
/*   0*/  public LanguageMode getLanguageOut() {
/*1368*/    return this.languageOut;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLooseTypes(boolean looseTypes) {
/*1379*/    this.looseTypes = looseTypes;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1384*/    CompilerOptions clone = (CompilerOptions)super.clone();
/*1386*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasTransformationHandler(AliasTransformationHandler changes) {
/*1391*/    this.aliasHandler = changes;
/*   0*/  }
/*   0*/  
/*   0*/  public AliasTransformationHandler getAliasTransformationHandler() {
/*1395*/    return this.aliasHandler;
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorHandler(ErrorHandler handler) {
/*1411*/    this.errorHandler = handler;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInferTypes(boolean enable) {
/*1419*/    this.inferTypes = enable;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getInferTypes() {
/*1427*/    return this.inferTypes;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean assumeStrictThis() {
/*1434*/    return this.assumeStrictThis;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAssumeStrictThis(boolean enable) {
/*1441*/    this.assumeStrictThis = enable;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean assumeClosuresOnlyCaptureReferences() {
/*1448*/    return this.assumeClosuresOnlyCaptureReferences;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAssumeClosuresOnlyCaptureReferences(boolean enable) {
/*1456*/    this.assumeClosuresOnlyCaptureReferences = enable;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPropertyInvalidationErrors(Map<String, CheckLevel> propertyInvalidationErrors) {
/*1465*/    this.propertyInvalidationErrors = Maps.newHashMap(propertyInvalidationErrors);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLanguageOut(LanguageMode languageOut) {
/*1470*/    this.languageOut = languageOut;
/*   0*/  }
/*   0*/  
/*   0*/  public void setIdeMode(boolean ideMode) {
/*1474*/    this.ideMode = ideMode;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSkipAllPasses(boolean skipAllPasses) {
/*1478*/    this.skipAllPasses = skipAllPasses;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDevMode(DevMode devMode) {
/*1482*/    this.devMode = devMode;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMessageBundle(MessageBundle messageBundle) {
/*1486*/    this.messageBundle = messageBundle;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckSymbols(boolean checkSymbols) {
/*1490*/    this.checkSymbols = checkSymbols;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckSuspiciousCode(boolean checkSuspiciousCode) {
/*1494*/    this.checkSuspiciousCode = checkSuspiciousCode;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckControlStructures(boolean checkControlStructures) {
/*1498*/    this.checkControlStructures = checkControlStructures;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckTypes(boolean checkTypes) {
/*1502*/    this.checkTypes = checkTypes;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCheckMissingGetCssNameBlacklist(String blackList) {
/*1506*/    this.checkMissingGetCssNameBlacklist = blackList;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFoldConstants(boolean foldConstants) {
/*1510*/    this.foldConstants = foldConstants;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDeadAssignmentElimination(boolean deadAssignmentElimination) {
/*1514*/    this.deadAssignmentElimination = deadAssignmentElimination;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineConstantVars(boolean inlineConstantVars) {
/*1518*/    this.inlineConstantVars = inlineConstantVars;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineFunctions(boolean inlineFunctions) {
/*1522*/    this.inlineFunctions = inlineFunctions;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineLocalFunctions(boolean inlineLocalFunctions) {
/*1526*/    this.inlineLocalFunctions = inlineLocalFunctions;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCrossModuleCodeMotion(boolean crossModuleCodeMotion) {
/*1530*/    this.crossModuleCodeMotion = crossModuleCodeMotion;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCoalesceVariableNames(boolean coalesceVariableNames) {
/*1534*/    this.coalesceVariableNames = coalesceVariableNames;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCrossModuleMethodMotion(boolean crossModuleMethodMotion) {
/*1538*/    this.crossModuleMethodMotion = crossModuleMethodMotion;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineGetters(boolean inlineGetters) {
/*1542*/    this.inlineGetters = inlineGetters;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineVariables(boolean inlineVariables) {
/*1546*/    this.inlineVariables = inlineVariables;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInlineLocalVariables(boolean inlineLocalVariables) {
/*1550*/    this.inlineLocalVariables = inlineLocalVariables;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFlowSensitiveInlineVariables(boolean enabled) {
/*1554*/    this.flowSensitiveInlineVariables = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSmartNameRemoval(boolean smartNameRemoval) {
/*1558*/    this.smartNameRemoval = smartNameRemoval;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveDeadCode(boolean removeDeadCode) {
/*1562*/    this.removeDeadCode = removeDeadCode;
/*   0*/  }
/*   0*/  
/*   0*/  public void setExtractPrototypeMemberDeclarations(boolean enabled) {
/*1566*/    this.extractPrototypeMemberDeclarations = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveUnusedPrototypeProperties(boolean enabled) {
/*1570*/    this.removeUnusedPrototypeProperties = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveUnusedPrototypePropertiesInExterns(boolean enabled) {
/*1575*/    this.removeUnusedPrototypePropertiesInExterns = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveUnusedVars(boolean removeUnusedVars) {
/*1579*/    this.removeUnusedVars = removeUnusedVars;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveUnusedLocalVars(boolean removeUnusedLocalVars) {
/*1583*/    this.removeUnusedLocalVars = removeUnusedLocalVars;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasExternals(boolean aliasExternals) {
/*1587*/    this.aliasExternals = aliasExternals;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCollapseVariableDeclarations(boolean enabled) {
/*1591*/    this.collapseVariableDeclarations = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroupVariableDeclarations(boolean enabled) {
/*1595*/    this.groupVariableDeclarations = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCollapseAnonymousFunctions(boolean enabled) {
/*1599*/    this.collapseAnonymousFunctions = enabled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasableStrings(Set<String> aliasableStrings) {
/*1603*/    this.aliasableStrings = aliasableStrings;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasStringsBlacklist(String aliasStringsBlacklist) {
/*1607*/    this.aliasStringsBlacklist = aliasStringsBlacklist;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasAllStrings(boolean aliasAllStrings) {
/*1611*/    this.aliasAllStrings = aliasAllStrings;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOutputJsStringUsage(boolean outputJsStringUsage) {
/*1615*/    this.outputJsStringUsage = outputJsStringUsage;
/*   0*/  }
/*   0*/  
/*   0*/  public void setConvertToDottedProperties(boolean convertToDottedProperties) {
/*1619*/    this.convertToDottedProperties = convertToDottedProperties;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRewriteFunctionExpressions(boolean rewriteFunctionExpressions) {
/*1623*/    this.rewriteFunctionExpressions = rewriteFunctionExpressions;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptimizeParameters(boolean optimizeParameters) {
/*1627*/    this.optimizeParameters = optimizeParameters;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptimizeReturns(boolean optimizeReturns) {
/*1631*/    this.optimizeReturns = optimizeReturns;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptimizeCalls(boolean optimizeCalls) {
/*1635*/    this.optimizeCalls = optimizeCalls;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptimizeArgumentsArray(boolean optimizeArgumentsArray) {
/*1639*/    this.optimizeArgumentsArray = optimizeArgumentsArray;
/*   0*/  }
/*   0*/  
/*   0*/  public void setVariableRenaming(VariableRenamingPolicy variableRenaming) {
/*1643*/    this.variableRenaming = variableRenaming;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPropertyRenaming(PropertyRenamingPolicy propertyRenaming) {
/*1647*/    this.propertyRenaming = propertyRenaming;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelRenaming(boolean labelRenaming) {
/*1651*/    this.labelRenaming = labelRenaming;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReserveRawExports(boolean reserveRawExports) {
/*1655*/    this.reserveRawExports = reserveRawExports;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGeneratePseudoNames(boolean generatePseudoNames) {
/*1659*/    this.generatePseudoNames = generatePseudoNames;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenamePrefix(String renamePrefix) {
/*1663*/    this.renamePrefix = renamePrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenamePrefixNamespace(String renamePrefixNamespace) {
/*1667*/    this.renamePrefixNamespace = renamePrefixNamespace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAliasKeywords(boolean aliasKeywords) {
/*1671*/    this.aliasKeywords = aliasKeywords;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCollapseProperties(boolean collapseProperties) {
/*1675*/    this.collapseProperties = collapseProperties;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDevirtualizePrototypeMethods(boolean devirtualizePrototypeMethods) {
/*1679*/    this.devirtualizePrototypeMethods = devirtualizePrototypeMethods;
/*   0*/  }
/*   0*/  
/*   0*/  public void setComputeFunctionSideEffects(boolean computeFunctionSideEffects) {
/*1683*/    this.computeFunctionSideEffects = computeFunctionSideEffects;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDebugFunctionSideEffectsPath(String debugFunctionSideEffectsPath) {
/*1687*/    this.debugFunctionSideEffectsPath = debugFunctionSideEffectsPath;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDisambiguateProperties(boolean disambiguateProperties) {
/*1691*/    this.disambiguateProperties = disambiguateProperties;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAmbiguateProperties(boolean ambiguateProperties) {
/*1695*/    this.ambiguateProperties = ambiguateProperties;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAnonymousFunctionNaming(AnonymousFunctionNamingPolicy anonymousFunctionNaming) {
/*1700*/    this.anonymousFunctionNaming = anonymousFunctionNaming;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputVariableMapSerialized(byte[] inputVariableMapSerialized) {
/*1704*/    this.inputVariableMapSerialized = inputVariableMapSerialized;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputPropertyMapSerialized(byte[] inputPropertyMapSerialized) {
/*1708*/    this.inputPropertyMapSerialized = inputPropertyMapSerialized;
/*   0*/  }
/*   0*/  
/*   0*/  public void setExportTestFunctions(boolean exportTestFunctions) {
/*1712*/    this.exportTestFunctions = exportTestFunctions;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRuntimeTypeCheck(boolean runtimeTypeCheck) {
/*1716*/    this.runtimeTypeCheck = runtimeTypeCheck;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRuntimeTypeCheckLogFunction(String runtimeTypeCheckLogFunction) {
/*1720*/    this.runtimeTypeCheckLogFunction = runtimeTypeCheckLogFunction;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSyntheticBlockStartMarker(String syntheticBlockStartMarker) {
/*1724*/    this.syntheticBlockStartMarker = syntheticBlockStartMarker;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSyntheticBlockEndMarker(String syntheticBlockEndMarker) {
/*1728*/    this.syntheticBlockEndMarker = syntheticBlockEndMarker;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLocale(String locale) {
/*1732*/    this.locale = locale;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMarkAsCompiled(boolean markAsCompiled) {
/*1736*/    this.markAsCompiled = markAsCompiled;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRemoveTryCatchFinally(boolean removeTryCatchFinally) {
/*1740*/    this.removeTryCatchFinally = removeTryCatchFinally;
/*   0*/  }
/*   0*/  
/*   0*/  public void setClosurePass(boolean closurePass) {
/*1744*/    this.closurePass = closurePass;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGatherCssNames(boolean gatherCssNames) {
/*1748*/    this.gatherCssNames = gatherCssNames;
/*   0*/  }
/*   0*/  
/*   0*/  public void setStripTypes(Set<String> stripTypes) {
/*1752*/    this.stripTypes = stripTypes;
/*   0*/  }
/*   0*/  
/*   0*/  public void setStripNameSuffixes(Set<String> stripNameSuffixes) {
/*1756*/    this.stripNameSuffixes = stripNameSuffixes;
/*   0*/  }
/*   0*/  
/*   0*/  public void setStripNamePrefixes(Set<String> stripNamePrefixes) {
/*1760*/    this.stripNamePrefixes = stripNamePrefixes;
/*   0*/  }
/*   0*/  
/*   0*/  public void setStripTypePrefixes(Set<String> stripTypePrefixes) {
/*1764*/    this.stripTypePrefixes = stripTypePrefixes;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCustomPasses(Multimap<CustomPassExecutionTime, CompilerPass> customPasses) {
/*1768*/    this.customPasses = customPasses;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMarkNoSideEffectCalls(boolean markNoSideEffectCalls) {
/*1772*/    this.markNoSideEffectCalls = markNoSideEffectCalls;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDefineReplacements(Map<String, Object> defineReplacements) {
/*1776*/    this.defineReplacements = defineReplacements;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTweakReplacements(Map<String, Object> tweakReplacements) {
/*1780*/    this.tweakReplacements = tweakReplacements;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMoveFunctionDeclarations(boolean moveFunctionDeclarations) {
/*1784*/    this.moveFunctionDeclarations = moveFunctionDeclarations;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInstrumentationTemplate(String instrumentationTemplate) {
/*1788*/    this.instrumentationTemplate = instrumentationTemplate;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRecordFunctionInformation(boolean recordFunctionInformation) {
/*1792*/    this.recordFunctionInformation = recordFunctionInformation;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCssRenamingMap(CssRenamingMap cssRenamingMap) {
/*1796*/    this.cssRenamingMap = cssRenamingMap;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReplaceStringsFunctionDescriptions(List<String> replaceStringsFunctionDescriptions) {
/*1800*/    this.replaceStringsFunctionDescriptions = replaceStringsFunctionDescriptions;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReplaceStringsPlaceholderToken(String replaceStringsPlaceholderToken) {
/*1804*/    this.replaceStringsPlaceholderToken = replaceStringsPlaceholderToken;
/*   0*/  }
/*   0*/  
/*   0*/  public void setReplaceStringsReservedStrings(Set<String> replaceStringsReservedStrings) {
/*1808*/    this.replaceStringsReservedStrings = replaceStringsReservedStrings;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPrettyPrint(boolean prettyPrint) {
/*1812*/    this.prettyPrint = prettyPrint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineBreak(boolean lineBreak) {
/*1816*/    this.lineBreak = lineBreak;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPreferLineBreakAtEndOfFile(boolean lineBreakAtEnd) {
/*1820*/    this.preferLineBreakAtEndOfFile = lineBreakAtEnd;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPrintInputDelimiter(boolean printInputDelimiter) {
/*1824*/    this.printInputDelimiter = printInputDelimiter;
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputDelimiter(String inputDelimiter) {
/*1828*/    this.inputDelimiter = inputDelimiter;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTracer(TracerMode tracer) {
/*1832*/    this.tracer = tracer;
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorFormat(ErrorFormat errorFormat) {
/*1836*/    this.errorFormat = errorFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public void setWarningsGuard(ComposeWarningsGuard warningsGuard) {
/*1840*/    this.warningsGuard = warningsGuard;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineLengthThreshold(int lineLengthThreshold) {
/*1844*/    this.lineLengthThreshold = lineLengthThreshold;
/*   0*/  }
/*   0*/  
/*   0*/  public void setExternExports(boolean externExports) {
/*1848*/    this.externExports = externExports;
/*   0*/  }
/*   0*/  
/*   0*/  public void setExternExportsPath(String externExportsPath) {
/*1852*/    this.externExportsPath = externExportsPath;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceMapOutputPath(String sourceMapOutputPath) {
/*1856*/    this.sourceMapOutputPath = sourceMapOutputPath;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceMapDetailLevel(SourceMap.DetailLevel sourceMapDetailLevel) {
/*1860*/    this.sourceMapDetailLevel = sourceMapDetailLevel;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceMapFormat(SourceMap.Format sourceMapFormat) {
/*1864*/    this.sourceMapFormat = sourceMapFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceMapLocationMappings(List<SourceMap.LocationMapping> sourceMapLocationMappings) {
/*1868*/    this.sourceMapLocationMappings = sourceMapLocationMappings;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTransformAMDToCJSModules(boolean transformAMDToCJSModules) {
/*1875*/    this.transformAMDToCJSModules = transformAMDToCJSModules;
/*   0*/  }
/*   0*/  
/*   0*/  public void setProcessCommonJSModules(boolean processCommonJSModules) {
/*1883*/    this.processCommonJSModules = processCommonJSModules;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCommonJSModulePathPrefix(String commonJSModulePathPrefix) {
/*1890*/    this.commonJSModulePathPrefix = commonJSModulePathPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public enum LanguageMode {
/*1902*/    ECMASCRIPT3, ECMASCRIPT5, ECMASCRIPT5_STRICT;
/*   0*/  }
/*   0*/  
/*   0*/  enum DevMode {
/*1920*/    OFF, START, START_AND_END, EVERY_PASS;
/*   0*/  }
/*   0*/  
/*   0*/  public enum TracerMode {
/*1939*/    ALL, RAW_SIZE, TIMING_ONLY, OFF;
/*   0*/    
/*   0*/    boolean isOn() {
/*1945*/      return (this != OFF);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public enum TweakProcessing {
/*1950*/    OFF, CHECK, STRIP;
/*   0*/    
/*   0*/    public boolean isOn() {
/*1955*/      return (this != OFF);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean shouldStrip() {
/*1959*/      return (this == STRIP);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*2025*/  static final AliasTransformationHandler NULL_ALIAS_TRANSFORMATION_HANDLER = new NullAliasTransformationHandler();
/*   0*/  
/*   0*/  private static class NullAliasTransformationHandler implements AliasTransformationHandler, Serializable {
/*   0*/    private static final long serialVersionUID = 0L;
/*   0*/    
/*   0*/    private NullAliasTransformationHandler() {}
/*   0*/    
/*2032*/    private static final CompilerOptions.AliasTransformation NULL_ALIAS_TRANSFORMATION = new NullAliasTransformation();
/*   0*/    
/*   0*/    public CompilerOptions.AliasTransformation logAliasTransformation(String sourceFile, SourcePosition<CompilerOptions.AliasTransformation> position) {
/*2038*/      position.setItem(NULL_ALIAS_TRANSFORMATION);
/*2039*/      return NULL_ALIAS_TRANSFORMATION;
/*   0*/    }
/*   0*/    
/*   0*/    private static class NullAliasTransformation implements CompilerOptions.AliasTransformation, Serializable {
/*   0*/      private static final long serialVersionUID = 0L;
/*   0*/      
/*   0*/      private NullAliasTransformation() {}
/*   0*/      
/*   0*/      public void addAlias(String alias, String definition) {}
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static interface AliasTransformation {
/*   0*/    void addAlias(String param1String1, String param1String2);
/*   0*/  }
/*   0*/  
/*   0*/  public static interface AliasTransformationHandler {
/*   0*/    CompilerOptions.AliasTransformation logAliasTransformation(String param1String, SourcePosition<CompilerOptions.AliasTransformation> param1SourcePosition);
/*   0*/  }
/*   0*/}
