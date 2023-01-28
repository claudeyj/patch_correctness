/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Charsets;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.common.io.Files;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.io.File;
/*   0*/import java.io.FileReader;
/*   0*/import java.io.IOException;
/*   0*/import java.text.ParseException;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.regex.Pattern;
/*   0*/
/*   0*/public class DefaultPassConfig extends PassConfig {
/*   0*/  private static final String COMPILED_CONSTANT_NAME = "COMPILED";
/*   0*/  
/*   0*/  private static final String CLOSURE_LOCALE_CONSTANT_NAME = "goog.LOCALE";
/*   0*/  
/*  60*/  static final DiagnosticType TIGHTEN_TYPES_WITHOUT_TYPE_CHECK = DiagnosticType.error("JSC_TIGHTEN_TYPES_WITHOUT_TYPE_CHECK", "TightenTypes requires type checking. Please use --check_types.");
/*   0*/  
/*  64*/  static final DiagnosticType CANNOT_USE_PROTOTYPE_AND_VAR = DiagnosticType.error("JSC_CANNOT_USE_PROTOTYPE_AND_VAR", "Rename prototypes and inline variables cannot be used together");
/*   0*/  
/*  69*/  static final DiagnosticType REPORT_PATH_IO_ERROR = DiagnosticType.error("JSC_REPORT_PATH_IO_ERROR", "Error writing compiler report to {0}");
/*   0*/  
/*  73*/  private static final DiagnosticType INPUT_MAP_PROP_PARSE = DiagnosticType.error("JSC_INPUT_MAP_PROP_PARSE", "Input property map parse error: {0}");
/*   0*/  
/*  77*/  private static final DiagnosticType INPUT_MAP_VAR_PARSE = DiagnosticType.error("JSC_INPUT_MAP_VAR_PARSE", "Input variable map parse error: {0}");
/*   0*/  
/*  81*/  private static final DiagnosticType NAME_REF_GRAPH_FILE_ERROR = DiagnosticType.error("JSC_NAME_REF_GRAPH_FILE_ERROR", "Error \"{1}\" writing name reference graph to \"{0}\".");
/*   0*/  
/*  85*/  private static final DiagnosticType NAME_REF_REPORT_FILE_ERROR = DiagnosticType.error("JSC_NAME_REF_REPORT_FILE_ERROR", "Error \"{1}\" writing name reference report to \"{0}\".");
/*   0*/  
/*  89*/  private static final Pattern GLOBAL_SYMBOL_NAMESPACE_PATTERN = Pattern.compile("^[a-zA-Z0-9$_]+$");
/*   0*/  
/*  95*/  private GlobalNamespace namespaceForChecks = null;
/*   0*/  
/* 101*/  private PreprocessorSymbolTable preprocessorSymbolTable = null;
/*   0*/  
/* 106*/  private TightenTypes tightenTypes = null;
/*   0*/  
/* 109*/  private Set<String> exportedNames = null;
/*   0*/  
/* 115*/  private CrossModuleMethodMotion.IdGenerator crossModuleIdGenerator = new CrossModuleMethodMotion.IdGenerator();
/*   0*/  
/* 122*/  private Map<String, Integer> cssNames = null;
/*   0*/  
/* 125*/  private VariableMap variableMap = null;
/*   0*/  
/* 128*/  private VariableMap propertyMap = null;
/*   0*/  
/* 131*/  private VariableMap anonymousFunctionNameMap = null;
/*   0*/  
/* 134*/  private FunctionNames functionNames = null;
/*   0*/  
/* 137*/  private VariableMap stringMap = null;
/*   0*/  
/* 140*/  private String idGeneratorMap = null;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkSideEffects;
/*   0*/  
/*   0*/  final PassFactory stripSideEffectProtection;
/*   0*/  
/*   0*/  final HotSwapPassFactory suspiciousCode;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkControlStructures;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkRequires;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkProvides;
/*   0*/  
/*   0*/  public DefaultPassConfig(CompilerOptions options) {
/* 143*/    super(options);
/* 742*/    this.checkSideEffects = new HotSwapPassFactory("checkSideEffects", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 751*/          boolean protectHiddenSideEffects = (DefaultPassConfig.this.options.protectHiddenSideEffects && !DefaultPassConfig.this.options.ideMode);
/* 753*/          return new CheckSideEffects(compiler, DefaultPassConfig.this.options.checkSuspiciousCode ? CheckLevel.WARNING : CheckLevel.OFF, protectHiddenSideEffects);
/*   0*/        }
/*   0*/      };
/* 762*/    this.stripSideEffectProtection = new PassFactory("stripSideEffectProtection", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 768*/          return new CheckSideEffects.StripProtection(compiler);
/*   0*/        }
/*   0*/      };
/* 776*/    this.suspiciousCode = new HotSwapPassFactory("suspiciousCode", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 782*/          List<NodeTraversal.Callback> sharedCallbacks = Lists.newArrayList();
/* 783*/          if (DefaultPassConfig.this.options.checkSuspiciousCode)
/* 784*/            sharedCallbacks.add(new CheckAccidentalSemicolon(CheckLevel.WARNING)); 
/* 787*/          if (DefaultPassConfig.this.options.enables(DiagnosticGroups.GLOBAL_THIS))
/* 788*/            sharedCallbacks.add(new CheckGlobalThis(compiler)); 
/* 791*/          if (DefaultPassConfig.this.options.enables(DiagnosticGroups.DEBUGGER_STATEMENT_PRESENT))
/* 792*/            sharedCallbacks.add(new CheckDebuggerStatement(compiler)); 
/* 795*/          return DefaultPassConfig.combineChecks(compiler, sharedCallbacks);
/*   0*/        }
/*   0*/      };
/* 815*/    this.checkControlStructures = new HotSwapPassFactory("checkControlStructures", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 819*/          return new ControlStructureCheck(compiler);
/*   0*/        }
/*   0*/      };
/* 824*/    this.checkRequires = new HotSwapPassFactory("checkRequires", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 828*/          return new CheckRequiresForConstructors(compiler, DefaultPassConfig.this.options.checkRequires);
/*   0*/        }
/*   0*/      };
/* 833*/    this.checkProvides = new HotSwapPassFactory("checkProvides", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 837*/          return new CheckProvides(compiler, DefaultPassConfig.this.options.checkProvides);
/*   0*/        }
/*   0*/      };
/* 848*/    this.generateExports = new PassFactory("generateExports", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 852*/          CodingConvention convention = compiler.getCodingConvention();
/* 853*/          if (convention.getExportSymbolFunction() != null && convention.getExportPropertyFunction() != null)
/* 855*/            return new GenerateExports(compiler, convention.getExportSymbolFunction(), convention.getExportPropertyFunction()); 
/* 859*/          return new ErrorPass(compiler, DefaultPassConfig.GENERATE_EXPORTS_ERROR);
/*   0*/        }
/*   0*/      };
/* 865*/    this.exportTestFunctions = new PassFactory("exportTestFunctions", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 869*/          CodingConvention convention = compiler.getCodingConvention();
/* 870*/          if (convention.getExportSymbolFunction() != null)
/* 871*/            return new ExportTestFunctions(compiler, convention.getExportSymbolFunction(), convention.getExportPropertyFunction()); 
/* 875*/          return new ErrorPass(compiler, DefaultPassConfig.GENERATE_EXPORTS_ERROR);
/*   0*/        }
/*   0*/      };
/* 881*/    this.gatherRawExports = new PassFactory("gatherRawExports", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 885*/          final GatherRawExports pass = new GatherRawExports(compiler);
/* 888*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/* 891*/                pass.process(externs, root);
/* 892*/                if (DefaultPassConfig.this.exportedNames == null)
/* 893*/                  DefaultPassConfig.this.exportedNames = Sets.newHashSet(); 
/* 895*/                DefaultPassConfig.this.exportedNames.addAll(pass.getExportedVariableNames());
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/* 902*/    this.closurePrimitives = new HotSwapPassFactory("processProvidesAndRequires", false) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 907*/          DefaultPassConfig.this.maybeInitializePreprocessorSymbolTable(compiler);
/* 908*/          final ProcessClosurePrimitives pass = new ProcessClosurePrimitives(compiler, DefaultPassConfig.this.preprocessorSymbolTable, DefaultPassConfig.this.options.brokenClosureRequiresLevel, DefaultPassConfig.this.options.rewriteNewDateGoogNow);
/* 914*/          return new HotSwapCompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/* 917*/                pass.process(externs, root);
/* 918*/                DefaultPassConfig.this.exportedNames = pass.getExportedVariableNames();
/*   0*/              }
/*   0*/              
/*   0*/              public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/* 922*/                pass.hotSwapScript(scriptRoot, originalRoot);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/* 929*/    this.jqueryAliases = new PassFactory("jqueryAliases", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 933*/          return new ExpandJqueryAliases(compiler);
/*   0*/        }
/*   0*/      };
/* 942*/    this.replaceMessages = new PassFactory("replaceMessages", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 946*/          return new ReplaceMessages(compiler, DefaultPassConfig.this.options.messageBundle, true, JsMessage.Style.getFromParams(true, false), false);
/*   0*/        }
/*   0*/      };
/* 958*/    this.closureGoogScopeAliases = new HotSwapPassFactory("processGoogScopeAliases", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/* 962*/          DefaultPassConfig.this.maybeInitializePreprocessorSymbolTable(compiler);
/* 963*/          return new ScopedAliases(compiler, DefaultPassConfig.this.preprocessorSymbolTable, DefaultPassConfig.this.options.getAliasTransformationHandler());
/*   0*/        }
/*   0*/      };
/* 971*/    this.closureCheckGetCssName = new PassFactory("checkMissingGetCssName", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/* 975*/          String blacklist = DefaultPassConfig.this.options.checkMissingGetCssNameBlacklist;
/* 976*/          Preconditions.checkState((blacklist != null && !blacklist.isEmpty()), "Not checking use of goog.getCssName because of empty blacklist.");
/* 978*/          return new CheckMissingGetCssName(compiler, DefaultPassConfig.this.options.checkMissingGetCssNameLevel, blacklist);
/*   0*/        }
/*   0*/      };
/* 988*/    this.closureReplaceGetCssName = new PassFactory("renameCssNames", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/* 992*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/* 995*/                Map<String, Integer> newCssNames = null;
/* 996*/                if (DefaultPassConfig.this.options.gatherCssNames)
/* 997*/                  newCssNames = Maps.newHashMap(); 
/* 999*/                new ReplaceCssNames(compiler, newCssNames).process(externs, jsRoot);
/*1001*/                DefaultPassConfig.this.cssNames = newCssNames;
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1011*/    this.createSyntheticBlocks = new PassFactory("createSyntheticBlocks", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1015*/          return new CreateSyntheticBlocks(compiler, DefaultPassConfig.this.options.syntheticBlockStartMarker, DefaultPassConfig.this.options.syntheticBlockEndMarker);
/*   0*/        }
/*   0*/      };
/*1022*/    this.peepholeOptimizations = new PassFactory("peepholeOptimizations", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*   0*/          boolean late = false;
/*1027*/          return new PeepholeOptimizationsPass(compiler, new AbstractPeepholeOptimization[] { new PeepholeSubstituteAlternateSyntax(false), new PeepholeReplaceKnownMethods(false), new PeepholeRemoveDeadCode(), new PeepholeFoldConstants(false), new PeepholeCollectPropertyAssignments() });
/*   0*/        }
/*   0*/      };
/*1037*/    this.latePeepholeOptimizations = new PassFactory("latePeepholeOptimizations", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*   0*/          boolean late = true;
/*1042*/          return new PeepholeOptimizationsPass(compiler, new AbstractPeepholeOptimization[] { new StatementFusion(), new PeepholeRemoveDeadCode(), new PeepholeSubstituteAlternateSyntax(true), new PeepholeReplaceKnownMethods(true), new PeepholeFoldConstants(true), new ReorderConstantExpression() });
/*   0*/        }
/*   0*/      };
/*1053*/    this.checkVars = new HotSwapPassFactory("checkVars", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/*1057*/          return new VarCheck(compiler);
/*   0*/        }
/*   0*/      };
/*1062*/    this.checkRegExp = new PassFactory("checkRegExp", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1066*/          final CheckRegExp pass = new CheckRegExp(compiler);
/*1068*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1071*/                pass.process(externs, root);
/*1072*/                compiler.setHasRegExpGlobalReferences(pass.isGlobalRegExpPropertiesUsed());
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1080*/    this.checkVariableReferences = new HotSwapPassFactory("checkVariableReferences", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/*1084*/          return new VariableReferenceCheck(compiler, DefaultPassConfig.this.options.aggressiveVarCheck);
/*   0*/        }
/*   0*/      };
/*1090*/    this.objectPropertyStringPreprocess = new PassFactory("ObjectPropertyStringPreprocess", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1094*/          return new ObjectPropertyStringPreprocess(compiler);
/*   0*/        }
/*   0*/      };
/*1099*/    this.resolveTypes = new HotSwapPassFactory("resolveTypes", false) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/*1103*/          return new DefaultPassConfig.GlobalTypeResolver(compiler);
/*   0*/        }
/*   0*/      };
/*1108*/    this.inferTypes = new HotSwapPassFactory("inferTypes", false) {
/*   0*/        protected HotSwapCompilerPass createInternal(final AbstractCompiler compiler) {
/*1113*/          return new HotSwapCompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1116*/                Preconditions.checkNotNull(DefaultPassConfig.this.topScope);
/*1117*/                Preconditions.checkNotNull(DefaultPassConfig.this.getTypedScopeCreator());
/*1119*/                DefaultPassConfig.this.makeTypeInference(compiler).process(externs, root);
/*   0*/              }
/*   0*/              
/*   0*/              public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/*1123*/                DefaultPassConfig.this.makeTypeInference(compiler).inferTypes(scriptRoot);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1129*/    this.inferJsDocInfo = new HotSwapPassFactory("inferJsDocInfo", false) {
/*   0*/        protected HotSwapCompilerPass createInternal(final AbstractCompiler compiler) {
/*1134*/          return new HotSwapCompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1137*/                Preconditions.checkNotNull(DefaultPassConfig.this.topScope);
/*1138*/                Preconditions.checkNotNull(DefaultPassConfig.this.getTypedScopeCreator());
/*1140*/                DefaultPassConfig.this.makeInferJsDocInfo(compiler).process(externs, root);
/*   0*/              }
/*   0*/              
/*   0*/              public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/*1144*/                DefaultPassConfig.this.makeInferJsDocInfo(compiler).hotSwapScript(scriptRoot, originalRoot);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1151*/    this.checkTypes = new HotSwapPassFactory("checkTypes", false) {
/*   0*/        protected HotSwapCompilerPass createInternal(final AbstractCompiler compiler) {
/*1156*/          return new HotSwapCompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1159*/                Preconditions.checkNotNull(DefaultPassConfig.this.topScope);
/*1160*/                Preconditions.checkNotNull(DefaultPassConfig.this.getTypedScopeCreator());
/*1162*/                TypeCheck check = DefaultPassConfig.this.makeTypeCheck(compiler);
/*1163*/                check.process(externs, root);
/*1164*/                compiler.getErrorManager().setTypedPercent(check.getTypedPercent());
/*   0*/              }
/*   0*/              
/*   0*/              public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/*1168*/                DefaultPassConfig.this.makeTypeCheck(compiler).check(scriptRoot, false);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1178*/    this.checkControlFlow = new HotSwapPassFactory("checkControlFlow", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/*1182*/          List<NodeTraversal.Callback> callbacks = Lists.newArrayList();
/*1183*/          if (DefaultPassConfig.this.options.checkUnreachableCode.isOn())
/*1184*/            callbacks.add(new CheckUnreachableCode(compiler, DefaultPassConfig.this.options.checkUnreachableCode)); 
/*1187*/          if (DefaultPassConfig.this.options.checkMissingReturn.isOn() && DefaultPassConfig.this.options.checkTypes)
/*1188*/            callbacks.add(new CheckMissingReturn(compiler, DefaultPassConfig.this.options.checkMissingReturn)); 
/*1191*/          return DefaultPassConfig.combineChecks(compiler, callbacks);
/*   0*/        }
/*   0*/      };
/*1196*/    this.checkAccessControls = new HotSwapPassFactory("checkAccessControls", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/*1200*/          return new CheckAccessControls(compiler);
/*   0*/        }
/*   0*/      };
/*1235*/    this.checkGlobalNames = new PassFactory("checkGlobalNames", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1239*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/*1245*/                DefaultPassConfig.this.namespaceForChecks = new GlobalNamespace(compiler, jsRoot);
/*1246*/                new CheckGlobalNames(compiler, DefaultPassConfig.this.options.checkGlobalNamesLevel).injectNamespace(DefaultPassConfig.this.namespaceForChecks).process(externs, jsRoot);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1254*/    this.checkStrictMode = new PassFactory("checkStrictMode", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1258*/          return new StrictModeCheck(compiler, !DefaultPassConfig.this.options.checkSymbols, !DefaultPassConfig.this.options.checkCaja);
/*   0*/        }
/*   0*/      };
/*1265*/    this.processTweaks = new PassFactory("processTweaks", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1268*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/*1271*/                new ProcessTweaks(compiler, DefaultPassConfig.this.options.getTweakProcessing().shouldStrip(), DefaultPassConfig.this.options.getTweakReplacements()).process(externs, jsRoot);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1280*/    this.processDefines = new PassFactory("processDefines", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1284*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/*1287*/                Map<String, Node> replacements = DefaultPassConfig.getAdditionalReplacements(DefaultPassConfig.this.options);
/*1288*/                replacements.putAll(DefaultPassConfig.this.options.getDefineReplacements());
/*1290*/                new ProcessDefines(compiler, replacements).injectNamespace(DefaultPassConfig.this.namespaceForChecks).process(externs, jsRoot);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1298*/    this.garbageCollectChecks = new HotSwapPassFactory("garbageCollectChecks", true) {
/*   0*/        protected HotSwapCompilerPass createInternal(AbstractCompiler compiler) {
/*1302*/          return new HotSwapCompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/*1307*/                DefaultPassConfig.this.namespaceForChecks = null;
/*   0*/              }
/*   0*/              
/*   0*/              public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/*1312*/                process(null, null);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1319*/    this.checkConsts = new PassFactory("checkConsts", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1323*/          return new ConstCheck(compiler);
/*   0*/        }
/*   0*/      };
/*1328*/    this.computeFunctionNames = new PassFactory("computeFunctionNames", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1332*/          return DefaultPassConfig.this.functionNames = new FunctionNames(compiler);
/*   0*/        }
/*   0*/      };
/*1337*/    this.ignoreCajaProperties = new PassFactory("ignoreCajaProperties", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1341*/          return new IgnoreCajaProperties(compiler);
/*   0*/        }
/*   0*/      };
/*1346*/    this.runtimeTypeCheck = new PassFactory("runtimeTypeCheck", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1350*/          return new RuntimeTypeCheck(compiler, DefaultPassConfig.this.options.runtimeTypeCheckLogFunction);
/*   0*/        }
/*   0*/      };
/*1356*/    this.replaceIdGenerators = new PassFactory("replaceIdGenerators", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1360*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1362*/                ReplaceIdGenerators pass = new ReplaceIdGenerators(compiler, DefaultPassConfig.this.options.idGenerators);
/*1364*/                pass.process(externs, root);
/*1365*/                DefaultPassConfig.this.idGeneratorMap = pass.getIdGeneratorMap();
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1372*/    this.replaceStrings = new PassFactory("replaceStrings", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1376*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1378*/                ReplaceStrings pass = new ReplaceStrings(compiler, DefaultPassConfig.this.options.replaceStringsPlaceholderToken, DefaultPassConfig.this.options.replaceStringsFunctionDescriptions, DefaultPassConfig.this.options.replaceStringsReservedStrings);
/*1383*/                pass.process(externs, root);
/*1384*/                DefaultPassConfig.this.stringMap = pass.getStringMap();
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1391*/    this.optimizeArgumentsArray = new PassFactory("optimizeArgumentsArray", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1395*/          return new OptimizeArgumentsArray(compiler);
/*   0*/        }
/*   0*/      };
/*1400*/    this.closureCodeRemoval = new PassFactory("closureCodeRemoval", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1404*/          return new ClosureCodeRemoval(compiler, DefaultPassConfig.this.options.removeAbstractMethods, DefaultPassConfig.this.options.removeClosureAsserts);
/*   0*/        }
/*   0*/      };
/*1410*/    this.closureOptimizePrimitives = new PassFactory("closureOptimizePrimitives", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1414*/          return new ClosureOptimizePrimitives(compiler);
/*   0*/        }
/*   0*/      };
/*1419*/    this.rescopeGlobalSymbols = new PassFactory("rescopeGlobalSymbols", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1423*/          return new RescopeGlobalSymbols(compiler, DefaultPassConfig.this.options.renamePrefixNamespace);
/*   0*/        }
/*   0*/      };
/*1428*/    this.collapseProperties = new PassFactory("collapseProperties", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1432*/          return new CollapseProperties(compiler, DefaultPassConfig.this.options.collapsePropertiesOnExternTypes, !DefaultPassConfig.this.isInliningForbidden());
/*   0*/        }
/*   0*/      };
/*1439*/    this.collapseObjectLiterals = new PassFactory("collapseObjectLiterals", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1443*/          return new InlineObjectLiterals(compiler, compiler.getUniqueNameIdSupplier());
/*   0*/        }
/*   0*/      };
/*1452*/    this.tightenTypesBuilder = new PassFactory("tightenTypes", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1456*/          if (!DefaultPassConfig.this.options.checkTypes)
/*1457*/            return new ErrorPass(compiler, DefaultPassConfig.TIGHTEN_TYPES_WITHOUT_TYPE_CHECK); 
/*1459*/          DefaultPassConfig.this.tightenTypes = new TightenTypes(compiler);
/*1460*/          return DefaultPassConfig.this.tightenTypes;
/*   0*/        }
/*   0*/      };
/*1465*/    this.disambiguateProperties = new PassFactory("disambiguateProperties", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1469*/          if (DefaultPassConfig.this.tightenTypes == null)
/*1470*/            return DisambiguateProperties.forJSTypeSystem(compiler, DefaultPassConfig.this.options.propertyInvalidationErrors); 
/*1473*/          return DisambiguateProperties.forConcreteTypeSystem(compiler, DefaultPassConfig.this.tightenTypes, DefaultPassConfig.this.options.propertyInvalidationErrors);
/*   0*/        }
/*   0*/      };
/*1482*/    this.chainCalls = new PassFactory("chainCalls", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1486*/          return new ChainCalls(compiler);
/*   0*/        }
/*   0*/      };
/*1494*/    this.devirtualizePrototypeMethods = new PassFactory("devirtualizePrototypeMethods", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1498*/          return new DevirtualizePrototypeMethods(compiler);
/*   0*/        }
/*   0*/      };
/*1506*/    this.optimizeCallsAndRemoveUnusedVars = new PassFactory("optimizeCalls_and_removeUnusedVars", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1510*/          OptimizeCalls passes = new OptimizeCalls(compiler);
/*1511*/          if (DefaultPassConfig.this.options.optimizeReturns)
/*1513*/            passes.addPass(new OptimizeReturns(compiler)); 
/*1516*/          if (DefaultPassConfig.this.options.optimizeParameters)
/*1518*/            passes.addPass(new OptimizeParameters(compiler)); 
/*1521*/          if (DefaultPassConfig.this.options.optimizeCalls) {
/*1522*/            boolean removeOnlyLocals = (DefaultPassConfig.this.options.removeUnusedLocalVars && !DefaultPassConfig.this.options.removeUnusedVars);
/*1524*/            boolean preserveAnonymousFunctionNames = (DefaultPassConfig.this.options.anonymousFunctionNaming != AnonymousFunctionNamingPolicy.OFF);
/*1527*/            passes.addPass(new RemoveUnusedVars(compiler, !removeOnlyLocals, preserveAnonymousFunctionNames, true));
/*   0*/          } 
/*1531*/          return passes;
/*   0*/        }
/*   0*/      };
/*1539*/    this.markPureFunctions = new PassFactory("markPureFunctions", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1543*/          return new PureFunctionIdentifier.Driver(compiler, DefaultPassConfig.this.options.debugFunctionSideEffectsPath, false);
/*   0*/        }
/*   0*/      };
/*1552*/    this.markNoSideEffectCalls = new PassFactory("markNoSideEffectCalls", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1556*/          return new MarkNoSideEffectCalls(compiler);
/*   0*/        }
/*   0*/      };
/*1561*/    this.inlineVariables = new PassFactory("inlineVariables", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*   0*/          InlineVariables.Mode mode;
/*1565*/          if (DefaultPassConfig.this.isInliningForbidden())
/*1569*/            return new ErrorPass(compiler, DefaultPassConfig.CANNOT_USE_PROTOTYPE_AND_VAR); 
/*1572*/          if (DefaultPassConfig.this.options.inlineVariables) {
/*1573*/            mode = InlineVariables.Mode.ALL;
/*1574*/          } else if (DefaultPassConfig.this.options.inlineLocalVariables) {
/*1575*/            mode = InlineVariables.Mode.LOCALS_ONLY;
/*   0*/          } else {
/*1577*/            throw new IllegalStateException("No variable inlining option set.");
/*   0*/          } 
/*1580*/          return new InlineVariables(compiler, mode, true);
/*   0*/        }
/*   0*/      };
/*1586*/    this.inlineConstants = new PassFactory("inlineConstants", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1590*/          return new InlineVariables(compiler, InlineVariables.Mode.CONSTANTS_ONLY, true);
/*   0*/        }
/*   0*/      };
/*1598*/    this.minimizeExitPoints = new PassFactory("minimizeExitPoints", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1602*/          return new MinimizeExitPoints(compiler);
/*   0*/        }
/*   0*/      };
/*1609*/    this.removeUnreachableCode = new PassFactory("removeUnreachableCode", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1613*/          return new UnreachableCodeElimination(compiler, true);
/*   0*/        }
/*   0*/      };
/*1620*/    this.removeUnusedPrototypeProperties = new PassFactory("removeUnusedPrototypeProperties", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1624*/          return new RemoveUnusedPrototypeProperties(compiler, DefaultPassConfig.this.options.removeUnusedPrototypePropertiesInExterns, !DefaultPassConfig.this.options.removeUnusedVars);
/*   0*/        }
/*   0*/      };
/*1633*/    this.removeUnusedClassProperties = new PassFactory("removeUnusedClassProperties", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1637*/          return new RemoveUnusedClassProperties(compiler);
/*   0*/        }
/*   0*/      };
/*1645*/    this.smartNamePass = new PassFactory("smartNamePass", false) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1649*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1652*/                NameAnalyzer na = new NameAnalyzer(compiler, false);
/*1653*/                na.process(externs, root);
/*1655*/                String reportPath = DefaultPassConfig.this.options.reportPath;
/*1656*/                if (reportPath != null)
/*   0*/                  try {
/*1658*/                    Files.write(na.getHtmlReport(), new File(reportPath), Charsets.UTF_8);
/*1660*/                  } catch (IOException e) {
/*1661*/                    compiler.report(JSError.make(DefaultPassConfig.REPORT_PATH_IO_ERROR, new String[] { reportPath }));
/*   0*/                  }  
/*1665*/                if (DefaultPassConfig.this.options.smartNameRemoval)
/*1666*/                  na.removeUnreferenced(); 
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1677*/    this.smartNamePass2 = new PassFactory("smartNamePass", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1681*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1684*/                NameAnalyzer na = new NameAnalyzer(compiler, false);
/*1685*/                na.process(externs, root);
/*1686*/                na.removeUnreferenced();
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1693*/    this.inlineSimpleMethods = new PassFactory("inlineSimpleMethods", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1697*/          return new InlineSimpleMethods(compiler);
/*   0*/        }
/*   0*/      };
/*1702*/    this.deadAssignmentsElimination = new PassFactory("deadAssignmentsElimination", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1706*/          return new DeadAssignmentsElimination(compiler);
/*   0*/        }
/*   0*/      };
/*1711*/    this.inlineFunctions = new PassFactory("inlineFunctions", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1715*/          boolean enableBlockInlining = !DefaultPassConfig.this.isInliningForbidden();
/*1716*/          return new InlineFunctions(compiler, compiler.getUniqueNameIdSupplier(), DefaultPassConfig.this.options.inlineFunctions, DefaultPassConfig.this.options.inlineLocalFunctions, enableBlockInlining, (DefaultPassConfig.this.options.assumeStrictThis() || DefaultPassConfig.this.options.getLanguageIn() == CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT), DefaultPassConfig.this.options.assumeClosuresOnlyCaptureReferences);
/*   0*/        }
/*   0*/      };
/*1729*/    this.removeUnusedVars = new PassFactory("removeUnusedVars", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1733*/          boolean removeOnlyLocals = (DefaultPassConfig.this.options.removeUnusedLocalVars && !DefaultPassConfig.this.options.removeUnusedVars);
/*1735*/          boolean preserveAnonymousFunctionNames = (DefaultPassConfig.this.options.anonymousFunctionNaming != AnonymousFunctionNamingPolicy.OFF);
/*1737*/          return new RemoveUnusedVars(compiler, !removeOnlyLocals, preserveAnonymousFunctionNames, false);
/*   0*/        }
/*   0*/      };
/*1748*/    this.crossModuleCodeMotion = new PassFactory("crossModuleCodeMotion", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1752*/          return new CrossModuleCodeMotion(compiler, compiler.getModuleGraph());
/*   0*/        }
/*   0*/      };
/*1759*/    this.crossModuleMethodMotion = new PassFactory("crossModuleMethodMotion", false) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1763*/          return new CrossModuleMethodMotion(compiler, DefaultPassConfig.this.crossModuleIdGenerator, DefaultPassConfig.this.options.removeUnusedPrototypePropertiesInExterns);
/*   0*/        }
/*   0*/      };
/*1774*/    this.specializeInitialModule = new PassFactory("specializeInitialModule", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1778*/          return new SpecializeModule(compiler, new PassFactory[] { DefaultPassConfig.this.devirtualizePrototypeMethods, DefaultPassConfig.this.inlineFunctions, DefaultPassConfig.this.removeUnusedPrototypeProperties });
/*   0*/        }
/*   0*/      };
/*1784*/    this.flowSensitiveInlineVariables = new PassFactory("flowSensitiveInlineVariables", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1788*/          return new FlowSensitiveInlineVariables(compiler);
/*   0*/        }
/*   0*/      };
/*1793*/    this.coalesceVariableNames = new PassFactory("coalesceVariableNames", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1797*/          return new CoalesceVariableNames(compiler, DefaultPassConfig.this.options.generatePseudoNames);
/*   0*/        }
/*   0*/      };
/*1805*/    this.exploitAssign = new PassFactory("exploitAssign", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1809*/          return new PeepholeOptimizationsPass(compiler, new AbstractPeepholeOptimization[] { new ExploitAssigns() });
/*   0*/        }
/*   0*/      };
/*1818*/    this.collapseVariableDeclarations = new PassFactory("collapseVariableDeclarations", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1822*/          return new CollapseVariableDeclarations(compiler);
/*   0*/        }
/*   0*/      };
/*1829*/    this.groupVariableDeclarations = new PassFactory("groupVariableDeclarations", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1833*/          return new GroupVariableDeclarations(compiler);
/*   0*/        }
/*   0*/      };
/*1840*/    this.extractPrototypeMemberDeclarations = new PassFactory("extractPrototypeMemberDeclarations", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1844*/          return new ExtractPrototypeMemberDeclarations(compiler, ExtractPrototypeMemberDeclarations.Pattern.USE_GLOBAL_TEMP);
/*   0*/        }
/*   0*/      };
/*1850*/    this.rewriteFunctionExpressions = new PassFactory("rewriteFunctionExpressions", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1854*/          return new FunctionRewriter(compiler);
/*   0*/        }
/*   0*/      };
/*1859*/    this.collapseAnonymousFunctions = new PassFactory("collapseAnonymousFunctions", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1863*/          return new CollapseAnonymousFunctions(compiler);
/*   0*/        }
/*   0*/      };
/*1868*/    this.moveFunctionDeclarations = new PassFactory("moveFunctionDeclarations", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1872*/          return new MoveFunctionDeclarations(compiler);
/*   0*/        }
/*   0*/      };
/*1876*/    this.nameUnmappedAnonymousFunctions = new PassFactory("nameAnonymousFunctions", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1880*/          return new NameAnonymousFunctions(compiler);
/*   0*/        }
/*   0*/      };
/*1884*/    this.nameMappedAnonymousFunctions = new PassFactory("nameAnonymousFunctions", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1888*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1890*/                NameAnonymousFunctionsMapped naf = new NameAnonymousFunctionsMapped(compiler);
/*1892*/                naf.process(externs, root);
/*1893*/                DefaultPassConfig.this.anonymousFunctionNameMap = naf.getFunctionMap();
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1900*/    this.aliasExternals = new PassFactory("aliasExternals", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1904*/          return new AliasExternals(compiler, compiler.getModuleGraph(), DefaultPassConfig.this.options.unaliasableGlobals, DefaultPassConfig.this.options.aliasableGlobals);
/*   0*/        }
/*   0*/      };
/*1913*/    this.aliasStrings = new PassFactory("aliasStrings", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1917*/          return new AliasStrings(compiler, compiler.getModuleGraph(), DefaultPassConfig.this.options.aliasAllStrings ? null : DefaultPassConfig.this.options.aliasableStrings, DefaultPassConfig.this.options.aliasStringsBlacklist, DefaultPassConfig.this.options.outputJsStringUsage);
/*   0*/        }
/*   0*/      };
/*1927*/    this.aliasKeywords = new PassFactory("aliasKeywords", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1931*/          return new AliasKeywords(compiler);
/*   0*/        }
/*   0*/      };
/*1936*/    this.objectPropertyStringPostprocess = new PassFactory("ObjectPropertyStringPostprocess", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1940*/          return new ObjectPropertyStringPostprocess(compiler);
/*   0*/        }
/*   0*/      };
/*1948*/    this.ambiguateProperties = new PassFactory("ambiguateProperties", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1952*/          return new AmbiguateProperties(compiler, DefaultPassConfig.this.options.anonymousFunctionNaming.getReservedCharacters());
/*   0*/        }
/*   0*/      };
/*1960*/    this.markUnnormalized = new PassFactory("markUnnormalized", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1964*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*1966*/                compiler.setLifeCycleStage(AbstractCompiler.LifeCycleStage.RAW);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*1973*/    this.denormalize = new PassFactory("denormalize", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1977*/          return new Denormalize(compiler);
/*   0*/        }
/*   0*/      };
/*1982*/    this.invertContextualRenaming = new PassFactory("invertNames", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*1986*/          return MakeDeclaredNamesUnique.getContextualRenameInverter(compiler);
/*   0*/        }
/*   0*/      };
/*1993*/    this.renameProperties = new PassFactory("renameProperties", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*1997*/          VariableMap map = null;
/*1998*/          if (DefaultPassConfig.this.options.inputPropertyMapSerialized != null)
/*   0*/            try {
/*2000*/              map = VariableMap.fromBytes(DefaultPassConfig.this.options.inputPropertyMapSerialized);
/*2001*/            } catch (ParseException e) {
/*2002*/              return new ErrorPass(compiler, JSError.make(DefaultPassConfig.INPUT_MAP_PROP_PARSE, new String[] { e.getMessage() }));
/*   0*/            }  
/*2007*/          final VariableMap prevPropertyMap = map;
/*2008*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*2010*/                DefaultPassConfig.this.propertyMap = DefaultPassConfig.this.runPropertyRenaming(compiler, prevPropertyMap, externs, root);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*2049*/    this.renameVars = new PassFactory("renameVars", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*2053*/          VariableMap map = null;
/*2054*/          if (DefaultPassConfig.this.options.inputVariableMapSerialized != null)
/*   0*/            try {
/*2056*/              map = VariableMap.fromBytes(DefaultPassConfig.this.options.inputVariableMapSerialized);
/*2057*/            } catch (ParseException e) {
/*2058*/              return new ErrorPass(compiler, JSError.make(DefaultPassConfig.INPUT_MAP_VAR_PARSE, new String[] { e.getMessage() }));
/*   0*/            }  
/*2063*/          final VariableMap prevVariableMap = map;
/*2064*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*2066*/                DefaultPassConfig.this.variableMap = DefaultPassConfig.this.runVariableRenaming(compiler, prevVariableMap, externs, root);
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*2095*/    this.renameLabels = new PassFactory("renameLabels", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*2099*/          return new RenameLabels(compiler);
/*   0*/        }
/*   0*/      };
/*2104*/    this.convertToDottedProperties = new PassFactory("convertToDottedProperties", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*2108*/          return new ConvertToDottedProperties(compiler);
/*   0*/        }
/*   0*/      };
/*2113*/    this.sanityCheckAst = new PassFactory("sanityCheckAst", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*2117*/          return new AstValidator();
/*   0*/        }
/*   0*/      };
/*2122*/    this.sanityCheckVars = new PassFactory("sanityCheckVars", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*2126*/          return new VarCheck(compiler, true);
/*   0*/        }
/*   0*/      };
/*2131*/    this.instrumentFunctions = new PassFactory("instrumentFunctions", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*2135*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node root) {
/*   0*/                try {
/*2138*/                  FileReader templateFile = new FileReader(DefaultPassConfig.this.options.instrumentationTemplate);
/*2140*/                  new InstrumentFunctions(compiler, DefaultPassConfig.this.functionNames, DefaultPassConfig.this.options.instrumentationTemplate, DefaultPassConfig.this.options.appNameStr, templateFile).process(externs, root);
/*2145*/                } catch (IOException e) {
/*2146*/                  compiler.report(JSError.make(AbstractCompiler.READ_ERROR, new String[] { DefaultPassConfig.this.options.instrumentationTemplate }));
/*   0*/                } 
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*2224*/    this.printNameReferenceGraph = new PassFactory("printNameReferenceGraph", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*2228*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/*2231*/                NameReferenceGraphConstruction gc = new NameReferenceGraphConstruction(compiler);
/*2233*/                gc.process(externs, jsRoot);
/*2234*/                String graphFileName = DefaultPassConfig.this.options.nameReferenceGraphPath;
/*   0*/                try {
/*2236*/                  Files.write(DotFormatter.toDot(gc.getNameReferenceGraph()), new File(graphFileName), Charsets.UTF_8);
/*2239*/                } catch (IOException e) {
/*2240*/                  compiler.report(JSError.make(DefaultPassConfig.NAME_REF_GRAPH_FILE_ERROR, new String[] { e.getMessage(), graphFileName }));
/*   0*/                } 
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*2249*/    this.printNameReferenceReport = new PassFactory("printNameReferenceReport", true) {
/*   0*/        protected CompilerPass createInternal(final AbstractCompiler compiler) {
/*2253*/          return new CompilerPass() {
/*   0*/              public void process(Node externs, Node jsRoot) {
/*2256*/                NameReferenceGraphConstruction gc = new NameReferenceGraphConstruction(compiler);
/*2258*/                String reportFileName = DefaultPassConfig.this.options.nameReferenceReportPath;
/*   0*/                try {
/*2260*/                  NameReferenceGraphReport report = new NameReferenceGraphReport(gc.getNameReferenceGraph());
/*2262*/                  Files.write(report.getHtmlReport(), new File(reportFileName), Charsets.UTF_8);
/*2265*/                } catch (IOException e) {
/*2266*/                  compiler.report(JSError.make(DefaultPassConfig.NAME_REF_REPORT_FILE_ERROR, new String[] { e.getMessage(), reportFileName }));
/*   0*/                } 
/*   0*/              }
/*   0*/            };
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  PassConfig.State getIntermediateState() {
/*   0*/    return new PassConfig.State((this.cssNames == null) ? null : Maps.newHashMap(this.cssNames), (this.exportedNames == null) ? null : Collections.<String>unmodifiableSet(this.exportedNames), this.crossModuleIdGenerator, this.variableMap, this.propertyMap, this.anonymousFunctionNameMap, this.stringMap, this.functionNames, this.idGeneratorMap);
/*   0*/  }
/*   0*/  
/*   0*/  void setIntermediateState(PassConfig.State state) {
/*   0*/    this.cssNames = (state.cssNames == null) ? null : Maps.newHashMap(state.cssNames);
/*   0*/    this.exportedNames = (state.exportedNames == null) ? null : Sets.newHashSet(state.exportedNames);
/*   0*/    this.crossModuleIdGenerator = state.crossModuleIdGenerator;
/*   0*/    this.variableMap = state.variableMap;
/*   0*/    this.propertyMap = state.propertyMap;
/*   0*/    this.anonymousFunctionNameMap = state.anonymousFunctionNameMap;
/*   0*/    this.stringMap = state.stringMap;
/*   0*/    this.functionNames = state.functionNames;
/*   0*/    this.idGeneratorMap = state.idGeneratorMap;
/*   0*/  }
/*   0*/  
/*   0*/  GlobalNamespace getGlobalNamespace() {
/*   0*/    return this.namespaceForChecks;
/*   0*/  }
/*   0*/  
/*   0*/  PreprocessorSymbolTable getPreprocessorSymbolTable() {
/*   0*/    return this.preprocessorSymbolTable;
/*   0*/  }
/*   0*/  
/*   0*/  void maybeInitializePreprocessorSymbolTable(AbstractCompiler compiler) {
/*   0*/    if (this.options.ideMode) {
/*   0*/      Node root = compiler.getRoot();
/*   0*/      if (this.preprocessorSymbolTable == null || this.preprocessorSymbolTable.getRootNode() != root)
/*   0*/        this.preprocessorSymbolTable = new PreprocessorSymbolTable(root); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected List<PassFactory> getChecks() {
/*   0*/    List<PassFactory> checks = Lists.newArrayList();
/*   0*/    if (this.options.closurePass)
/*   0*/      checks.add(this.closureGoogScopeAliases); 
/*   0*/    if (this.options.nameAnonymousFunctionsOnly) {
/*   0*/      if (this.options.anonymousFunctionNaming == AnonymousFunctionNamingPolicy.MAPPED) {
/*   0*/        checks.add(this.nameMappedAnonymousFunctions);
/*   0*/      } else if (this.options.anonymousFunctionNaming == AnonymousFunctionNamingPolicy.UNMAPPED) {
/*   0*/        checks.add(this.nameUnmappedAnonymousFunctions);
/*   0*/      } 
/*   0*/      return checks;
/*   0*/    } 
/*   0*/    checks.add(this.checkSideEffects);
/*   0*/    if (this.options.checkSuspiciousCode || this.options.enables(DiagnosticGroups.GLOBAL_THIS) || this.options.enables(DiagnosticGroups.DEBUGGER_STATEMENT_PRESENT))
/*   0*/      checks.add(this.suspiciousCode); 
/*   0*/    if (this.options.checkControlStructures || this.options.enables(DiagnosticGroups.ES5_STRICT))
/*   0*/      checks.add(this.checkControlStructures); 
/*   0*/    if (this.options.checkRequires.isOn())
/*   0*/      checks.add(this.checkRequires); 
/*   0*/    if (this.options.checkProvides.isOn())
/*   0*/      checks.add(this.checkProvides); 
/*   0*/    if (this.options.generateExports)
/*   0*/      checks.add(this.generateExports); 
/*   0*/    if (this.options.exportTestFunctions)
/*   0*/      checks.add(this.exportTestFunctions); 
/*   0*/    if (this.options.closurePass)
/*   0*/      checks.add(this.closurePrimitives.makeOneTimePass()); 
/*   0*/    if (this.options.jqueryPass)
/*   0*/      checks.add(this.jqueryAliases.makeOneTimePass()); 
/*   0*/    if (this.options.closurePass && this.options.checkMissingGetCssNameLevel.isOn())
/*   0*/      checks.add(this.closureCheckGetCssName); 
/*   0*/    if (this.options.syntheticBlockStartMarker != null)
/*   0*/      checks.add(this.createSyntheticBlocks); 
/*   0*/    checks.add(this.checkVars);
/*   0*/    if (this.options.computeFunctionSideEffects)
/*   0*/      checks.add(this.checkRegExp); 
/*   0*/    if (this.options.aggressiveVarCheck.isOn())
/*   0*/      checks.add(this.checkVariableReferences); 
/*   0*/    if (this.options.processObjectPropertyString)
/*   0*/      checks.add(this.objectPropertyStringPreprocess); 
/*   0*/    if (this.options.checkTypes || this.options.inferTypes) {
/*   0*/      checks.add(this.resolveTypes.makeOneTimePass());
/*   0*/      checks.add(this.inferTypes.makeOneTimePass());
/*   0*/      if (this.options.checkTypes) {
/*   0*/        checks.add(this.checkTypes.makeOneTimePass());
/*   0*/      } else {
/*   0*/        checks.add(this.inferJsDocInfo.makeOneTimePass());
/*   0*/      } 
/*   0*/    } 
/*   0*/    if (this.options.checkUnreachableCode.isOn() || (this.options.checkTypes && this.options.checkMissingReturn.isOn()))
/*   0*/      checks.add(this.checkControlFlow); 
/*   0*/    if (this.options.checkTypes && (this.options.enables(DiagnosticGroups.ACCESS_CONTROLS) || this.options.enables(DiagnosticGroups.CONSTANT_PROPERTY)))
/*   0*/      checks.add(this.checkAccessControls); 
/*   0*/    if (this.options.checkGlobalNamesLevel.isOn())
/*   0*/      checks.add(this.checkGlobalNames); 
/*   0*/    if (this.options.enables(DiagnosticGroups.ES5_STRICT) || this.options.checkCaja)
/*   0*/      checks.add(this.checkStrictMode); 
/*   0*/    if (this.options.closurePass)
/*   0*/      checks.add(this.closureReplaceGetCssName); 
/*   0*/    checks.add((this.options.messageBundle != null) ? this.replaceMessages : createEmptyPass("replaceMessages"));
/*   0*/    if (this.options.getTweakProcessing().isOn())
/*   0*/      checks.add(this.processTweaks); 
/*   0*/    checks.add(this.processDefines);
/*   0*/    if (this.options.instrumentationTemplate != null || this.options.recordFunctionInformation)
/*   0*/      checks.add(this.computeFunctionNames); 
/*   0*/    if (this.options.nameReferenceGraphPath != null && !this.options.nameReferenceGraphPath.isEmpty())
/*   0*/      checks.add(this.printNameReferenceGraph); 
/*   0*/    if (this.options.nameReferenceReportPath != null && !this.options.nameReferenceReportPath.isEmpty())
/*   0*/      checks.add(this.printNameReferenceReport); 
/*   0*/    assertAllOneTimePasses(checks);
/*   0*/    return checks;
/*   0*/  }
/*   0*/  
/*   0*/  protected List<PassFactory> getOptimizations() {
/*   0*/    List<PassFactory> passes = Lists.newArrayList();
/*   0*/    passes.add(this.garbageCollectChecks);
/*   0*/    if (this.options.runtimeTypeCheck)
/*   0*/      passes.add(this.runtimeTypeCheck); 
/*   0*/    passes.add(createEmptyPass("beforeStandardOptimizations"));
/*   0*/    if (this.options.replaceIdGenerators)
/*   0*/      passes.add(this.replaceIdGenerators); 
/*   0*/    if (this.options.optimizeArgumentsArray)
/*   0*/      passes.add(this.optimizeArgumentsArray); 
/*   0*/    if (this.options.closurePass && (this.options.removeAbstractMethods || this.options.removeClosureAsserts))
/*   0*/      passes.add(this.closureCodeRemoval); 
/*   0*/    if (this.options.collapseProperties)
/*   0*/      passes.add(this.collapseProperties); 
/*   0*/    if (!this.options.replaceStringsFunctionDescriptions.isEmpty())
/*   0*/      passes.add(this.replaceStrings); 
/*   0*/    if (this.options.tightenTypes)
/*   0*/      passes.add(this.tightenTypesBuilder); 
/*   0*/    if (this.options.disambiguateProperties)
/*   0*/      passes.add(this.disambiguateProperties); 
/*   0*/    if (this.options.computeFunctionSideEffects) {
/*   0*/      passes.add(this.markPureFunctions);
/*   0*/    } else if (this.options.markNoSideEffectCalls) {
/*   0*/      passes.add(this.markNoSideEffectCalls);
/*   0*/    } 
/*   0*/    if (this.options.chainCalls)
/*   0*/      passes.add(this.chainCalls); 
/*   0*/    if (this.options.inlineConstantVars)
/*   0*/      passes.add(this.checkConsts); 
/*   0*/    if (this.options.ignoreCajaProperties)
/*   0*/      passes.add(this.ignoreCajaProperties); 
/*   0*/    assertAllOneTimePasses(passes);
/*   0*/    if (this.options.smartNameRemoval || this.options.reportPath != null) {
/*   0*/      passes.addAll(getCodeRemovingPasses());
/*   0*/      passes.add(this.smartNamePass);
/*   0*/    } 
/*   0*/    if (this.options.closurePass)
/*   0*/      passes.add(this.closureOptimizePrimitives); 
/*   0*/    if (this.options.crossModuleCodeMotion)
/*   0*/      passes.add(this.crossModuleCodeMotion); 
/*   0*/    if (this.options.devirtualizePrototypeMethods)
/*   0*/      passes.add(this.devirtualizePrototypeMethods); 
/*   0*/    if (this.options.customPasses != null)
/*   0*/      passes.add(getCustomPasses(CustomPassExecutionTime.BEFORE_OPTIMIZATION_LOOP)); 
/*   0*/    passes.add(createEmptyPass("beforeMainOptimizations"));
/*   0*/    if (this.options.specializeInitialModule) {
/*   0*/      passes.addAll(getMainOptimizationLoop());
/*   0*/      if (this.options.crossModuleCodeMotion)
/*   0*/        passes.add(this.crossModuleCodeMotion); 
/*   0*/      if (this.options.crossModuleMethodMotion)
/*   0*/        passes.add(this.crossModuleMethodMotion); 
/*   0*/      passes.add(this.specializeInitialModule.makeOneTimePass());
/*   0*/    } 
/*   0*/    passes.addAll(getMainOptimizationLoop());
/*   0*/    passes.add(createEmptyPass("beforeModuleMotion"));
/*   0*/    if (this.options.crossModuleCodeMotion)
/*   0*/      passes.add(this.crossModuleCodeMotion); 
/*   0*/    if (this.options.crossModuleMethodMotion)
/*   0*/      passes.add(this.crossModuleMethodMotion); 
/*   0*/    passes.add(createEmptyPass("afterModuleMotion"));
/*   0*/    if (this.options.customPasses != null)
/*   0*/      passes.add(getCustomPasses(CustomPassExecutionTime.AFTER_OPTIMIZATION_LOOP)); 
/*   0*/    if (this.options.flowSensitiveInlineVariables) {
/*   0*/      passes.add(this.flowSensitiveInlineVariables);
/*   0*/      if (this.options.removeUnusedVars || this.options.removeUnusedLocalVars)
/*   0*/        passes.add(this.removeUnusedVars); 
/*   0*/    } 
/*   0*/    if (this.options.smartNameRemoval)
/*   0*/      passes.add(this.smartNamePass2); 
/*   0*/    if (this.options.collapseAnonymousFunctions)
/*   0*/      passes.add(this.collapseAnonymousFunctions); 
/*   0*/    if (this.options.moveFunctionDeclarations || this.options.renamePrefixNamespace != null)
/*   0*/      passes.add(this.moveFunctionDeclarations); 
/*   0*/    if (this.options.anonymousFunctionNaming == AnonymousFunctionNamingPolicy.MAPPED)
/*   0*/      passes.add(this.nameMappedAnonymousFunctions); 
/*   0*/    if (this.options.extractPrototypeMemberDeclarations && this.options.propertyRenaming != PropertyRenamingPolicy.HEURISTIC && this.options.propertyRenaming != PropertyRenamingPolicy.AGGRESSIVE_HEURISTIC)
/*   0*/      passes.add(this.extractPrototypeMemberDeclarations); 
/*   0*/    if (this.options.ambiguateProperties && this.options.propertyRenaming == PropertyRenamingPolicy.ALL_UNQUOTED)
/*   0*/      passes.add(this.ambiguateProperties); 
/*   0*/    if (this.options.propertyRenaming != PropertyRenamingPolicy.OFF)
/*   0*/      passes.add(this.renameProperties); 
/*   0*/    if (this.options.reserveRawExports)
/*   0*/      passes.add(this.gatherRawExports); 
/*   0*/    if (this.options.convertToDottedProperties)
/*   0*/      passes.add(this.convertToDottedProperties); 
/*   0*/    if (this.options.rewriteFunctionExpressions)
/*   0*/      passes.add(this.rewriteFunctionExpressions); 
/*   0*/    if (!this.options.aliasableStrings.isEmpty() || this.options.aliasAllStrings)
/*   0*/      passes.add(this.aliasStrings); 
/*   0*/    if (this.options.aliasExternals)
/*   0*/      passes.add(this.aliasExternals); 
/*   0*/    if (this.options.aliasKeywords)
/*   0*/      passes.add(this.aliasKeywords); 
/*   0*/    passes.add(this.markUnnormalized);
/*   0*/    if (this.options.coalesceVariableNames) {
/*   0*/      passes.add(this.coalesceVariableNames);
/*   0*/      if (this.options.foldConstants)
/*   0*/        passes.add(this.peepholeOptimizations); 
/*   0*/    } 
/*   0*/    if (this.options.collapseVariableDeclarations) {
/*   0*/      passes.add(this.exploitAssign);
/*   0*/      passes.add(this.collapseVariableDeclarations);
/*   0*/    } 
/*   0*/    passes.add(this.denormalize);
/*   0*/    if (this.options.instrumentationTemplate != null)
/*   0*/      passes.add(this.instrumentFunctions); 
/*   0*/    if (this.options.variableRenaming != VariableRenamingPolicy.ALL)
/*   0*/      passes.add(this.invertContextualRenaming); 
/*   0*/    if (this.options.variableRenaming != VariableRenamingPolicy.OFF)
/*   0*/      passes.add(this.renameVars); 
/*   0*/    if (this.options.groupVariableDeclarations)
/*   0*/      passes.add(this.groupVariableDeclarations); 
/*   0*/    if (this.options.processObjectPropertyString)
/*   0*/      passes.add(this.objectPropertyStringPostprocess); 
/*   0*/    if (this.options.labelRenaming)
/*   0*/      passes.add(this.renameLabels); 
/*   0*/    if (this.options.foldConstants)
/*   0*/      passes.add(this.latePeepholeOptimizations); 
/*   0*/    if (this.options.anonymousFunctionNaming == AnonymousFunctionNamingPolicy.UNMAPPED)
/*   0*/      passes.add(this.nameUnmappedAnonymousFunctions); 
/*   0*/    if (this.options.renamePrefixNamespace != null) {
/*   0*/      if (!GLOBAL_SYMBOL_NAMESPACE_PATTERN.matcher(this.options.renamePrefixNamespace).matches())
/*   0*/        throw new IllegalArgumentException("Illegal character in renamePrefixNamespace name: " + this.options.renamePrefixNamespace); 
/*   0*/      passes.add(this.rescopeGlobalSymbols);
/*   0*/    } 
/*   0*/    passes.add(this.stripSideEffectProtection);
/*   0*/    passes.add(this.sanityCheckAst);
/*   0*/    passes.add(this.sanityCheckVars);
/*   0*/    return passes;
/*   0*/  }
/*   0*/  
/*   0*/  private List<PassFactory> getMainOptimizationLoop() {
/*   0*/    List<PassFactory> passes = Lists.newArrayList();
/*   0*/    if (this.options.inlineGetters)
/*   0*/      passes.add(this.inlineSimpleMethods); 
/*   0*/    passes.addAll(getCodeRemovingPasses());
/*   0*/    if (this.options.inlineFunctions || this.options.inlineLocalFunctions)
/*   0*/      passes.add(this.inlineFunctions); 
/*   0*/    boolean runOptimizeCalls = (this.options.optimizeCalls || this.options.optimizeParameters || this.options.optimizeReturns);
/*   0*/    if (this.options.removeUnusedVars || this.options.removeUnusedLocalVars) {
/*   0*/      if (this.options.deadAssignmentElimination)
/*   0*/        passes.add(this.deadAssignmentsElimination); 
/*   0*/      if (!runOptimizeCalls)
/*   0*/        passes.add(this.removeUnusedVars); 
/*   0*/    } 
/*   0*/    if (runOptimizeCalls)
/*   0*/      passes.add(this.optimizeCallsAndRemoveUnusedVars); 
/*   0*/    assertAllLoopablePasses(passes);
/*   0*/    return passes;
/*   0*/  }
/*   0*/  
/*   0*/  private List<PassFactory> getCodeRemovingPasses() {
/*   0*/    List<PassFactory> passes = Lists.newArrayList();
/*   0*/    if (this.options.collapseObjectLiterals && !isInliningForbidden())
/*   0*/      passes.add(this.collapseObjectLiterals); 
/*   0*/    if (this.options.inlineVariables || this.options.inlineLocalVariables) {
/*   0*/      passes.add(this.inlineVariables);
/*   0*/    } else if (this.options.inlineConstantVars) {
/*   0*/      passes.add(this.inlineConstants);
/*   0*/    } 
/*   0*/    if (this.options.foldConstants) {
/*   0*/      passes.add(this.minimizeExitPoints);
/*   0*/      passes.add(this.peepholeOptimizations);
/*   0*/    } 
/*   0*/    if (this.options.removeDeadCode)
/*   0*/      passes.add(this.removeUnreachableCode); 
/*   0*/    if (this.options.removeUnusedPrototypeProperties) {
/*   0*/      passes.add(this.removeUnusedPrototypeProperties);
/*   0*/      passes.add(this.removeUnusedClassProperties);
/*   0*/    } 
/*   0*/    assertAllLoopablePasses(passes);
/*   0*/    return passes;
/*   0*/  }
/*   0*/  
/*   0*/  private void assertAllOneTimePasses(List<PassFactory> passes) {
/*   0*/    for (PassFactory pass : passes)
/*   0*/      Preconditions.checkState(pass.isOneTimePass()); 
/*   0*/  }
/*   0*/  
/*   0*/  private void assertAllLoopablePasses(List<PassFactory> passes) {
/*   0*/    for (PassFactory pass : passes)
/*   0*/      Preconditions.checkState(!pass.isOneTimePass()); 
/*   0*/  }
/*   0*/  
/*   0*/  private static final DiagnosticType GENERATE_EXPORTS_ERROR = DiagnosticType.error("JSC_GENERATE_EXPORTS_ERROR", "Exports can only be generated if export symbol/property functions are set.");
/*   0*/  
/*   0*/  final PassFactory generateExports;
/*   0*/  
/*   0*/  final PassFactory exportTestFunctions;
/*   0*/  
/*   0*/  final PassFactory gatherRawExports;
/*   0*/  
/*   0*/  final HotSwapPassFactory closurePrimitives;
/*   0*/  
/*   0*/  final PassFactory jqueryAliases;
/*   0*/  
/*   0*/  final PassFactory replaceMessages;
/*   0*/  
/*   0*/  final HotSwapPassFactory closureGoogScopeAliases;
/*   0*/  
/*   0*/  final PassFactory closureCheckGetCssName;
/*   0*/  
/*   0*/  final PassFactory closureReplaceGetCssName;
/*   0*/  
/*   0*/  final PassFactory createSyntheticBlocks;
/*   0*/  
/*   0*/  final PassFactory peepholeOptimizations;
/*   0*/  
/*   0*/  final PassFactory latePeepholeOptimizations;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkVars;
/*   0*/  
/*   0*/  final PassFactory checkRegExp;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkVariableReferences;
/*   0*/  
/*   0*/  final PassFactory objectPropertyStringPreprocess;
/*   0*/  
/*   0*/  final HotSwapPassFactory resolveTypes;
/*   0*/  
/*   0*/  final HotSwapPassFactory inferTypes;
/*   0*/  
/*   0*/  final HotSwapPassFactory inferJsDocInfo;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkTypes;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkControlFlow;
/*   0*/  
/*   0*/  final HotSwapPassFactory checkAccessControls;
/*   0*/  
/*   0*/  final PassFactory checkGlobalNames;
/*   0*/  
/*   0*/  final PassFactory checkStrictMode;
/*   0*/  
/*   0*/  final PassFactory processTweaks;
/*   0*/  
/*   0*/  final PassFactory processDefines;
/*   0*/  
/*   0*/  final PassFactory garbageCollectChecks;
/*   0*/  
/*   0*/  final PassFactory checkConsts;
/*   0*/  
/*   0*/  final PassFactory computeFunctionNames;
/*   0*/  
/*   0*/  final PassFactory ignoreCajaProperties;
/*   0*/  
/*   0*/  final PassFactory runtimeTypeCheck;
/*   0*/  
/*   0*/  final PassFactory replaceIdGenerators;
/*   0*/  
/*   0*/  final PassFactory replaceStrings;
/*   0*/  
/*   0*/  final PassFactory optimizeArgumentsArray;
/*   0*/  
/*   0*/  final PassFactory closureCodeRemoval;
/*   0*/  
/*   0*/  final PassFactory closureOptimizePrimitives;
/*   0*/  
/*   0*/  final PassFactory rescopeGlobalSymbols;
/*   0*/  
/*   0*/  final PassFactory collapseProperties;
/*   0*/  
/*   0*/  final PassFactory collapseObjectLiterals;
/*   0*/  
/*   0*/  final PassFactory tightenTypesBuilder;
/*   0*/  
/*   0*/  final PassFactory disambiguateProperties;
/*   0*/  
/*   0*/  final PassFactory chainCalls;
/*   0*/  
/*   0*/  final PassFactory devirtualizePrototypeMethods;
/*   0*/  
/*   0*/  final PassFactory optimizeCallsAndRemoveUnusedVars;
/*   0*/  
/*   0*/  final PassFactory markPureFunctions;
/*   0*/  
/*   0*/  final PassFactory markNoSideEffectCalls;
/*   0*/  
/*   0*/  final PassFactory inlineVariables;
/*   0*/  
/*   0*/  final PassFactory inlineConstants;
/*   0*/  
/*   0*/  final PassFactory minimizeExitPoints;
/*   0*/  
/*   0*/  final PassFactory removeUnreachableCode;
/*   0*/  
/*   0*/  final PassFactory removeUnusedPrototypeProperties;
/*   0*/  
/*   0*/  final PassFactory removeUnusedClassProperties;
/*   0*/  
/*   0*/  final PassFactory smartNamePass;
/*   0*/  
/*   0*/  final PassFactory smartNamePass2;
/*   0*/  
/*   0*/  final PassFactory inlineSimpleMethods;
/*   0*/  
/*   0*/  final PassFactory deadAssignmentsElimination;
/*   0*/  
/*   0*/  final PassFactory inlineFunctions;
/*   0*/  
/*   0*/  final PassFactory removeUnusedVars;
/*   0*/  
/*   0*/  final PassFactory crossModuleCodeMotion;
/*   0*/  
/*   0*/  final PassFactory crossModuleMethodMotion;
/*   0*/  
/*   0*/  final PassFactory specializeInitialModule;
/*   0*/  
/*   0*/  final PassFactory flowSensitiveInlineVariables;
/*   0*/  
/*   0*/  final PassFactory coalesceVariableNames;
/*   0*/  
/*   0*/  final PassFactory exploitAssign;
/*   0*/  
/*   0*/  final PassFactory collapseVariableDeclarations;
/*   0*/  
/*   0*/  final PassFactory groupVariableDeclarations;
/*   0*/  
/*   0*/  final PassFactory extractPrototypeMemberDeclarations;
/*   0*/  
/*   0*/  final PassFactory rewriteFunctionExpressions;
/*   0*/  
/*   0*/  final PassFactory collapseAnonymousFunctions;
/*   0*/  
/*   0*/  final PassFactory moveFunctionDeclarations;
/*   0*/  
/*   0*/  final PassFactory nameUnmappedAnonymousFunctions;
/*   0*/  
/*   0*/  final PassFactory nameMappedAnonymousFunctions;
/*   0*/  
/*   0*/  final PassFactory aliasExternals;
/*   0*/  
/*   0*/  final PassFactory aliasStrings;
/*   0*/  
/*   0*/  final PassFactory aliasKeywords;
/*   0*/  
/*   0*/  final PassFactory objectPropertyStringPostprocess;
/*   0*/  
/*   0*/  final PassFactory ambiguateProperties;
/*   0*/  
/*   0*/  final PassFactory markUnnormalized;
/*   0*/  
/*   0*/  final PassFactory denormalize;
/*   0*/  
/*   0*/  final PassFactory invertContextualRenaming;
/*   0*/  
/*   0*/  final PassFactory renameProperties;
/*   0*/  
/*   0*/  final PassFactory renameVars;
/*   0*/  
/*   0*/  final PassFactory renameLabels;
/*   0*/  
/*   0*/  final PassFactory convertToDottedProperties;
/*   0*/  
/*   0*/  final PassFactory sanityCheckAst;
/*   0*/  
/*   0*/  final PassFactory sanityCheckVars;
/*   0*/  
/*   0*/  final PassFactory instrumentFunctions;
/*   0*/  
/*   0*/  final PassFactory printNameReferenceGraph;
/*   0*/  
/*   0*/  final PassFactory printNameReferenceReport;
/*   0*/  
/*   0*/  private static HotSwapCompilerPass combineChecks(AbstractCompiler compiler, List<NodeTraversal.Callback> callbacks) {
/*   0*/    Preconditions.checkArgument((callbacks.size() > 0));
/*   0*/    NodeTraversal.Callback[] array = callbacks.<NodeTraversal.Callback>toArray(new NodeTraversal.Callback[callbacks.size()]);
/*   0*/    return new CombinedCompilerPass(compiler, array);
/*   0*/  }
/*   0*/  
/*   0*/  class GlobalTypeResolver implements HotSwapCompilerPass {
/*   0*/    private final AbstractCompiler compiler;
/*   0*/    
/*   0*/    GlobalTypeResolver(AbstractCompiler compiler) {
/*   0*/      this.compiler = compiler;
/*   0*/    }
/*   0*/    
/*   0*/    public void process(Node externs, Node root) {
/*   0*/      if (DefaultPassConfig.this.topScope == null) {
/*   0*/        DefaultPassConfig.this.regenerateGlobalTypedScope(this.compiler, root.getParent());
/*   0*/      } else {
/*   0*/        this.compiler.getTypeRegistry().resolveTypesInScope(DefaultPassConfig.this.topScope);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/*   0*/      DefaultPassConfig.this.patchGlobalTypedScope(this.compiler, scriptRoot);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private VariableMap runPropertyRenaming(AbstractCompiler compiler, VariableMap prevPropertyMap, Node externs, Node root) {
/*   0*/    RenamePrototypes rproto, rproto2;
/*   0*/    RenameProperties rprop;
/*   0*/    char[] reservedChars = this.options.anonymousFunctionNaming.getReservedCharacters();
/*   0*/    switch (this.options.propertyRenaming) {
/*   0*/      case HEURISTIC:
/*   0*/        rproto = new RenamePrototypes(compiler, false, reservedChars, prevPropertyMap);
/*   0*/        rproto.process(externs, root);
/*   0*/        return rproto.getPropertyMap();
/*   0*/      case AGGRESSIVE_HEURISTIC:
/*   0*/        rproto2 = new RenamePrototypes(compiler, true, reservedChars, prevPropertyMap);
/*   0*/        rproto2.process(externs, root);
/*   0*/        return rproto2.getPropertyMap();
/*   0*/      case ALL_UNQUOTED:
/*   0*/        rprop = new RenameProperties(compiler, this.options.propertyAffinity, this.options.generatePseudoNames, prevPropertyMap, reservedChars);
/*   0*/        rprop.process(externs, root);
/*   0*/        return rprop.getPropertyMap();
/*   0*/    } 
/*   0*/    throw new IllegalStateException("Unrecognized property renaming policy");
/*   0*/  }
/*   0*/  
/*   0*/  private VariableMap runVariableRenaming(AbstractCompiler compiler, VariableMap prevVariableMap, Node externs, Node root) {
/*   0*/    char[] reservedChars = this.options.anonymousFunctionNaming.getReservedCharacters();
/*   0*/    boolean preserveAnonymousFunctionNames = (this.options.anonymousFunctionNaming != AnonymousFunctionNamingPolicy.OFF);
/*   0*/    RenameVars rn = new RenameVars(compiler, this.options.renamePrefix, (this.options.variableRenaming == VariableRenamingPolicy.LOCAL), preserveAnonymousFunctionNames, this.options.generatePseudoNames, this.options.shadowVariables, prevVariableMap, reservedChars, this.exportedNames);
/*   0*/    rn.process(externs, root);
/*   0*/    return rn.getVariableMap();
/*   0*/  }
/*   0*/  
/*   0*/  static PassFactory createEmptyPass(String name) {
/*   0*/    return new PassFactory(name, true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*   0*/          return DefaultPassConfig.runInSerial(new CompilerPass[0]);
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  private PassFactory getCustomPasses(final CustomPassExecutionTime executionTime) {
/*   0*/    return new PassFactory("runCustomPasses", true) {
/*   0*/        protected CompilerPass createInternal(AbstractCompiler compiler) {
/*   0*/          return DefaultPassConfig.runInSerial(DefaultPassConfig.this.options.customPasses.get(executionTime));
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isInliningForbidden() {
/*   0*/    return (this.options.propertyRenaming == PropertyRenamingPolicy.HEURISTIC || this.options.propertyRenaming == PropertyRenamingPolicy.AGGRESSIVE_HEURISTIC);
/*   0*/  }
/*   0*/  
/*   0*/  private static CompilerPass runInSerial(CompilerPass... passes) {
/*   0*/    return runInSerial(Lists.newArrayList((Object[])passes));
/*   0*/  }
/*   0*/  
/*   0*/  private static CompilerPass runInSerial(final Collection<CompilerPass> passes) {
/*   0*/    return new CompilerPass() {
/*   0*/        public void process(Node externs, Node root) {
/*   0*/          for (CompilerPass pass : (Iterable<CompilerPass>)passes)
/*   0*/            pass.process(externs, root); 
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  static Map<String, Node> getAdditionalReplacements(CompilerOptions options) {
/*   0*/    Map<String, Node> additionalReplacements = Maps.newHashMap();
/*   0*/    if (options.markAsCompiled || options.closurePass)
/*   0*/      additionalReplacements.put("COMPILED", IR.trueNode()); 
/*   0*/    if (options.closurePass && options.locale != null)
/*   0*/      additionalReplacements.put("goog.LOCALE", IR.string(options.locale)); 
/*   0*/    return additionalReplacements;
/*   0*/  }
/*   0*/  
/*   0*/  static abstract class HotSwapPassFactory extends PassFactory {
/*   0*/    HotSwapPassFactory(String name, boolean isOneTimePass) {
/*2283*/      super(name, isOneTimePass);
/*   0*/    }
/*   0*/    
/*   0*/    HotSwapCompilerPass getHotSwapPass(AbstractCompiler compiler) {
/*2292*/      return createInternal(compiler);
/*   0*/    }
/*   0*/    
/*   0*/    protected abstract HotSwapCompilerPass createInternal(AbstractCompiler param1AbstractCompiler);
/*   0*/  }
/*   0*/}
