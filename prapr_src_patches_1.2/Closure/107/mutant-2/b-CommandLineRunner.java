/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.ImmutableList;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.common.io.ByteStreams;
/*   0*/import com.google.common.io.Files;
/*   0*/import java.io.BufferedInputStream;
/*   0*/import java.io.File;
/*   0*/import java.io.FileInputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.PrintStream;
/*   0*/import java.lang.reflect.AnnotatedElement;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.logging.Level;
/*   0*/import java.util.regex.Matcher;
/*   0*/import java.util.regex.Pattern;
/*   0*/import java.util.zip.ZipEntry;
/*   0*/import java.util.zip.ZipInputStream;
/*   0*/import org.kohsuke.args4j.Argument;
/*   0*/import org.kohsuke.args4j.CmdLineException;
/*   0*/import org.kohsuke.args4j.CmdLineParser;
/*   0*/import org.kohsuke.args4j.Option;
/*   0*/import org.kohsuke.args4j.OptionDef;
/*   0*/import org.kohsuke.args4j.spi.FieldSetter;
/*   0*/import org.kohsuke.args4j.spi.OptionHandler;
/*   0*/import org.kohsuke.args4j.spi.Parameters;
/*   0*/import org.kohsuke.args4j.spi.Setter;
/*   0*/import org.kohsuke.args4j.spi.StringOptionHandler;
/*   0*/
/*   0*/public class CommandLineRunner extends AbstractCommandLineRunner<Compiler, CompilerOptions> {
/*   0*/  private static class GuardLevel {
/*   0*/    final String name;
/*   0*/    
/*   0*/    final CheckLevel level;
/*   0*/    
/*   0*/    GuardLevel(String name, CheckLevel level) {
/*  98*/      this.name = name;
/*  99*/      this.level = level;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class Flags {
/* 106*/    private static List<CommandLineRunner.GuardLevel> guardLevels = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--help", handler = BooleanOptionHandler.class, usage = "Displays this message")
/*   0*/    private boolean displayHelp = false;
/*   0*/    
/*   0*/    @Option(name = "--print_tree", handler = BooleanOptionHandler.class, usage = "Prints out the parse tree and exits")
/*   0*/    private boolean printTree = false;
/*   0*/    
/*   0*/    @Option(name = "--print_ast", handler = BooleanOptionHandler.class, usage = "Prints a dot file describing the internal abstract syntax tree and exits")
/*   0*/    private boolean printAst = false;
/*   0*/    
/*   0*/    @Option(name = "--print_pass_graph", handler = BooleanOptionHandler.class, usage = "Prints a dot file describing the passes that will get run and exits")
/*   0*/    private boolean printPassGraph = false;
/*   0*/    
/*   0*/    @Option(name = "--jscomp_dev_mode", aliases = {"--dev_mode"})
/* 132*/    private CompilerOptions.DevMode jscompDevMode = CompilerOptions.DevMode.OFF;
/*   0*/    
/*   0*/    @Option(name = "--logging_level", usage = "The logging level (standard java.util.logging.Level values) for Compiler progress. Does not control errors or warnings for the JavaScript code under compilation")
/* 138*/    private String loggingLevel = Level.WARNING.getName();
/*   0*/    
/*   0*/    @Option(name = "--externs", usage = "The file containing JavaScript externs. You may specify multiple")
/* 144*/    private List<String> externs = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--js", usage = "The JavaScript filename. You may specify multiple")
/* 149*/    private List<String> js = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--js_output_file", usage = "Primary output filename. If not specified, output is written to stdout")
/* 153*/    private String jsOutputFile = "";
/*   0*/    
/*   0*/    @Option(name = "--module", usage = "A JavaScript module specification. The format is <name>:<num-js-files>[:[<dep>,...][:]]]. Module names must be unique. Each dep is the name of a module that this module depends on. Modules must be listed in dependency order, and JS source files must be listed in the corresponding order. Where --module flags occur in relation to --js flags is unimportant. Provide the value 'auto' to trigger module creation from CommonJSmodules.")
/* 158*/    private List<String> module = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--variable_map_input_file", usage = "File containing the serialized version of the variable renaming map produced by a previous compilation")
/* 169*/    private String variableMapInputFile = "";
/*   0*/    
/*   0*/    @Option(name = "--property_map_input_file", usage = "File containing the serialized version of the property renaming map produced by a previous compilation")
/* 174*/    private String propertyMapInputFile = "";
/*   0*/    
/*   0*/    @Option(name = "--variable_map_output_file", usage = "File where the serialized version of the variable renaming map produced should be saved")
/* 179*/    private String variableMapOutputFile = "";
/*   0*/    
/*   0*/    @Option(name = "--create_name_map_files", handler = BooleanOptionHandler.class, usage = "If true, variable renaming and property renaming map files will be produced as {binary name}_vars_map.out and {binary name}_props_map.out. Note that this flag cannot be used in conjunction with either variableMapOutputFile or property_map_output_file")
/*   0*/    private boolean createNameMapFiles = false;
/*   0*/    
/*   0*/    @Option(name = "--property_map_output_file", usage = "File where the serialized version of the property renaming map produced should be saved")
/* 193*/    private String propertyMapOutputFile = "";
/*   0*/    
/*   0*/    @Option(name = "--third_party", handler = BooleanOptionHandler.class, usage = "Check source validity but do not enforce Closure style rules and conventions")
/*   0*/    private boolean thirdParty = false;
/*   0*/    
/*   0*/    @Option(name = "--summary_detail_level", usage = "Controls how detailed the compilation summary is. Values: 0 (never print summary), 1 (print summary only if there are errors or warnings), 2 (print summary if the 'checkTypes' diagnostic  group is enabled, see --jscomp_warning), 3 (always print summary). The default level is 1")
/* 204*/    private int summaryDetailLevel = 1;
/*   0*/    
/*   0*/    @Option(name = "--output_wrapper", usage = "Interpolate output into this string at the place denoted by the marker token %output%. Use marker token %output|jsstring% to do js string escaping on the output.")
/* 212*/    private String outputWrapper = "";
/*   0*/    
/*   0*/    @Option(name = "--module_wrapper", usage = "An output wrapper for a JavaScript module (optional). The format is <name>:<wrapper>. The module name must correspond with a module specified using --module. The wrapper must contain %s as the code placeholder. The %basename% placeholder can also be used to substitute the base name of the module output file.")
/* 218*/    private List<String> moduleWrapper = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--module_output_path_prefix", usage = "Prefix for filenames of compiled JS modules. <module-name>.js will be appended to this prefix. Directories will be created as needed. Use with --module")
/* 226*/    private String moduleOutputPathPrefix = "./";
/*   0*/    
/*   0*/    @Option(name = "--create_source_map", usage = "If specified, a source map file mapping the generated source files back to the original source file will be output to the specified path. The %outname% placeholder will expand to the name of the output file that the source map corresponds to.")
/* 232*/    private String createSourceMap = "";
/*   0*/    
/*   0*/    @Option(name = "--source_map_format", usage = "The source map format to produce. Options: V1, V2, V3, DEFAULT. DEFAULT produces V2.")
/* 240*/    private SourceMap.Format sourceMapFormat = SourceMap.Format.DEFAULT;
/*   0*/    
/*   0*/    @Option(name = "--jscomp_error", handler = WarningGuardErrorOptionHandler.class, usage = "Make the named class of warnings an error. Options:accessControls, ambiguousFunctionDecl, checkEventfulObjectDisposal, checkRegExp, checkStructDictInheritance, checkTypes, checkVars, const, constantProperty, deprecated, duplicateMessage, es3, es5Strict, externsValidation, fileoverviewTags, globalThis, internetExplorerChecks, invalidCasts, misplacedTypeAnnotation, missingProperties, missingProvide, missingRequire, missingReturn,nonStandardJsDocs, reportUnknownTypes, suspiciousCode, strictModuleDepCheck, typeInvalidation, undefinedNames, undefinedVars, unknownDefines, uselessCode, visibility")
/* 246*/    private List<String> jscompError = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--jscomp_warning", handler = WarningGuardWarningOptionHandler.class, usage = "Make the named class of warnings a normal warning. Options:accessControls, ambiguousFunctionDecl, checkEventfulObjectDisposal, checkRegExp, checkStructDictInheritance, checkTypes, checkVars, const, constantProperty, deprecated, duplicateMessage, es3, es5Strict, externsValidation, fileoverviewTags, globalThis, internetExplorerChecks, invalidCasts, misplacedTypeAnnotation, missingProperties, missingProvide, missingRequire, missingReturn,nonStandardJsDocs, reportUnknownTypes, suspiciousCode, strictModuleDepCheck, typeInvalidation, undefinedNames, undefinedVars, unknownDefines, uselessCode, visibility")
/* 254*/    private List<String> jscompWarning = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--jscomp_off", handler = WarningGuardOffOptionHandler.class, usage = "Turn off the named class of warnings. Options:accessControls, ambiguousFunctionDecl, checkEventfulObjectDisposal, checkRegExp, checkStructDictInheritance, checkTypes, checkVars, const, constantProperty, deprecated, duplicateMessage, es3, es5Strict, externsValidation, fileoverviewTags, globalThis, internetExplorerChecks, invalidCasts, misplacedTypeAnnotation, missingProperties, missingProvide, missingRequire, missingReturn,nonStandardJsDocs, reportUnknownTypes, suspiciousCode, strictModuleDepCheck, typeInvalidation, undefinedNames, undefinedVars, unknownDefines, uselessCode, visibility")
/* 262*/    private List<String> jscompOff = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--define", aliases = {"--D", "-D"}, usage = "Override the value of a variable annotated @define. The format is <name>[=<val>], where <name> is the name of a @define variable and <val> is a boolean, number, or a single-quoted string that contains no single quotes. If [=<val>] is omitted, the variable is marked true")
/* 269*/    private List<String> define = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--charset", usage = "Input and output charset for all files. By default, we accept UTF-8 as input and output US_ASCII")
/* 278*/    private String charset = "";
/*   0*/    
/*   0*/    @Option(name = "--compilation_level", usage = "Specifies the compilation level to use. Options: WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS")
/* 283*/    private CompilationLevel compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
/*   0*/    
/*   0*/    @Option(name = "--use_types_for_optimization", usage = "Experimental: perform additional optimizations based on available information.  Inaccurate type annotations may result in incorrect results.")
/*   0*/    private boolean useTypesForOptimization = false;
/*   0*/    
/*   0*/    @Option(name = "--warning_level", usage = "Specifies the warning level to use. Options: QUIET, DEFAULT, VERBOSE")
/* 295*/    private WarningLevel warningLevel = WarningLevel.DEFAULT;
/*   0*/    
/*   0*/    @Option(name = "--use_only_custom_externs", handler = BooleanOptionHandler.class, usage = "Specifies whether the default externs should be excluded")
/*   0*/    private boolean useOnlyCustomExterns = false;
/*   0*/    
/*   0*/    @Option(name = "--debug", handler = BooleanOptionHandler.class, usage = "Enable debugging options")
/*   0*/    private boolean debug = false;
/*   0*/    
/*   0*/    @Option(name = "--generate_exports", handler = BooleanOptionHandler.class, usage = "Generates export code for those marked with @export")
/*   0*/    private boolean generateExports = false;
/*   0*/    
/*   0*/    @Option(name = "--formatting", usage = "Specifies which formatting options, if any, should be applied to the output JS. Options: PRETTY_PRINT, PRINT_INPUT_DELIMITER, SINGLE_QUOTES")
/* 315*/    private List<CommandLineRunner.FormattingOption> formatting = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--process_common_js_modules", usage = "Process CommonJS modules to a concatenable form.")
/*   0*/    private boolean processCommonJsModules = false;
/*   0*/    
/*   0*/    @Option(name = "--common_js_module_path_prefix", usage = "Path prefix to be removed from CommonJS module names.")
/* 325*/    private String commonJsPathPrefix = "./";
/*   0*/    
/*   0*/    @Option(name = "--common_js_entry_module", usage = "Root of your common JS dependency hierarchy. Your main script.")
/*   0*/    private String commonJsEntryModule;
/*   0*/    
/*   0*/    @Option(name = "--transform_amd_modules", usage = "Transform AMD to CommonJS modules.")
/*   0*/    private boolean transformAmdModules = false;
/*   0*/    
/*   0*/    @Option(name = "--process_closure_primitives", handler = BooleanOptionHandler.class, usage = "Processes built-ins from the Closure library, such as goog.require(), goog.provide(), and goog.exportSymbol()")
/*   0*/    private boolean processClosurePrimitives = true;
/*   0*/    
/*   0*/    @Option(name = "--manage_closure_dependencies", handler = BooleanOptionHandler.class, usage = "Automatically sort dependencies so that a file that goog.provides symbol X will always come before a file that goog.requires symbol X. If an input provides symbols, and those symbols are never required, then that input will not be included in the compilation.")
/*   0*/    private boolean manageClosureDependencies = false;
/*   0*/    
/*   0*/    @Option(name = "--only_closure_dependencies", handler = BooleanOptionHandler.class, usage = "Only include files in the transitive dependency of the entry points (specified by closure_entry_point). Files that do not provide dependencies will be removed. This supersedesmanage_closure_dependencies")
/*   0*/    private boolean onlyClosureDependencies = false;
/*   0*/    
/*   0*/    @Option(name = "--closure_entry_point", usage = "Entry points to the program. Must be goog.provide'd symbols. Any goog.provide'd symbols that are not a transitive dependency of the entry points will be removed. Files without goog.provides, and their dependencies, will always be left in. If any entry points are specified, then the manage_closure_dependencies option will be set to true and all files will be sorted in dependency order.")
/* 362*/    private List<String> closureEntryPoint = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--process_jquery_primitives", handler = BooleanOptionHandler.class, usage = "Processes built-ins from the Jquery library, such as jQuery.fn and jQuery.extend()")
/*   0*/    private boolean processJqueryPrimitives = false;
/*   0*/    
/*   0*/    @Option(name = "--angular_pass", handler = BooleanOptionHandler.class, usage = "Generate $inject properties for AngularJS for functions annotated with @ngInject")
/*   0*/    private boolean angularPass = false;
/*   0*/    
/*   0*/    @Option(name = "--output_manifest", usage = "Prints out a list of all the files in the compilation. If --manage_closure_dependencies is on, this will not include files that got dropped because they were not required. The %outname% placeholder expands to the JS output file. If you're using modularization, using %outname% will create a manifest for each module.")
/* 384*/    private String outputManifest = "";
/*   0*/    
/*   0*/    @Option(name = "--output_module_dependencies", usage = "Prints out a JSON file of dependencies between modules.")
/* 393*/    private String outputModuleDependencies = "";
/*   0*/    
/*   0*/    @Option(name = "--accept_const_keyword", usage = "Allows usage of const keyword.")
/*   0*/    private boolean acceptConstKeyword = false;
/*   0*/    
/*   0*/    @Option(name = "--language_in", usage = "Sets what language spec that input sources conform. Options: ECMASCRIPT3 (default), ECMASCRIPT5, ECMASCRIPT5_STRICT")
/* 401*/    private String languageIn = "ECMASCRIPT3";
/*   0*/    
/*   0*/    @Option(name = "--version", handler = BooleanOptionHandler.class, usage = "Prints the compiler version to stderr.")
/*   0*/    private boolean version = false;
/*   0*/    
/*   0*/    @Option(name = "--translations_file", usage = "Source of translated messages. Currently only supports XTB.")
/* 411*/    private String translationsFile = "";
/*   0*/    
/*   0*/    @Option(name = "--translations_project", usage = "Scopes all translations to the specified project.When specified, we will use different message ids so that messages in different projects can have different translations.")
/* 415*/    private String translationsProject = null;
/*   0*/    
/*   0*/    @Option(name = "--flagfile", usage = "A file containing additional command-line options.")
/* 421*/    private String flagFile = "";
/*   0*/    
/*   0*/    @Option(name = "--warnings_whitelist_file", usage = "A file containing warnings to suppress. Each line should be of the form\n<file-name>:<line-number>?  <warning-description>")
/* 425*/    private String warningsWhitelistFile = "";
/*   0*/    
/*   0*/    @Option(name = "--extra_annotation_name", usage = "A whitelist of tag names in JSDoc. You may specify multiple")
/* 431*/    private List<String> extraAnnotationName = Lists.newArrayList();
/*   0*/    
/*   0*/    @Option(name = "--tracer_mode", usage = "Shows the duration of each compiler pass and the impact to the compiled output size. Options: ALL, RAW_SIZE, TIMING_ONLY, OFF")
/* 435*/    private CompilerOptions.TracerMode tracerMode = CompilerOptions.TracerMode.OFF;
/*   0*/    
/*   0*/    @Argument
/* 441*/    private List<String> arguments = Lists.newArrayList();
/*   0*/    
/*   0*/    List<String> getJsFiles() {
/* 464*/      List<String> allJsInputs = Lists.newArrayListWithCapacity(this.js.size() + this.arguments.size());
/* 466*/      allJsInputs.addAll(this.js);
/* 467*/      allJsInputs.addAll(this.arguments);
/* 468*/      return allJsInputs;
/*   0*/    }
/*   0*/    
/*   0*/    public static class BooleanOptionHandler extends OptionHandler<Boolean> {
/* 474*/      private static final Set<String> TRUES = Sets.newHashSet((Object[])new String[] { "true", "on", "yes", "1" });
/*   0*/      
/* 476*/      private static final Set<String> FALSES = Sets.newHashSet((Object[])new String[] { "false", "off", "no", "0" });
/*   0*/      
/*   0*/      public BooleanOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Boolean> setter) {
/* 482*/        super(parser, option, setter);
/*   0*/      }
/*   0*/      
/*   0*/      public int parseArguments(Parameters params) throws CmdLineException {
/* 487*/        String param = null;
/*   0*/        try {
/* 489*/          param = params.getParameter(0);
/* 490*/        } catch (CmdLineException e) {
/* 491*/          param = null;
/*   0*/        } 
/* 494*/        if (param == null) {
/* 495*/          this.setter.addValue(true);
/* 496*/          return 0;
/*   0*/        } 
/* 498*/        String lowerParam = param.toLowerCase();
/* 499*/        if (TRUES.contains(lowerParam)) {
/* 500*/          this.setter.addValue(true);
/* 501*/        } else if (FALSES.contains(lowerParam)) {
/* 502*/          this.setter.addValue(false);
/*   0*/        } else {
/* 504*/          this.setter.addValue(true);
/* 505*/          return 0;
/*   0*/        } 
/* 507*/        return 1;
/*   0*/      }
/*   0*/      
/*   0*/      public String getDefaultMetaVariable() {
/* 513*/        return null;
/*   0*/      }
/*   0*/    }
/*   0*/    
/*   0*/    public static class WarningGuardErrorOptionHandler extends StringOptionHandler {
/*   0*/      public WarningGuardErrorOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super String> setter) {
/* 524*/        super(parser, option, new CommandLineRunner.Flags.WarningGuardSetter(setter, CheckLevel.ERROR, null));
/*   0*/      }
/*   0*/    }
/*   0*/    
/*   0*/    public static class WarningGuardWarningOptionHandler extends StringOptionHandler {
/*   0*/      public WarningGuardWarningOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super String> setter) {
/* 533*/        super(parser, option, new CommandLineRunner.Flags.WarningGuardSetter(setter, CheckLevel.WARNING, null));
/*   0*/      }
/*   0*/    }
/*   0*/    
/*   0*/    public static class WarningGuardOffOptionHandler extends StringOptionHandler {
/*   0*/      public WarningGuardOffOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super String> setter) {
/* 543*/        super(parser, option, new CommandLineRunner.Flags.WarningGuardSetter(setter, CheckLevel.OFF, null));
/*   0*/      }
/*   0*/    }
/*   0*/    
/*   0*/    private static class WarningGuardSetter implements Setter<String> {
/*   0*/      private final Setter<? super String> proxy;
/*   0*/      
/*   0*/      private final CheckLevel level;
/*   0*/      
/*   0*/      private WarningGuardSetter(Setter<? super String> proxy, CheckLevel level) {
/* 553*/        this.proxy = proxy;
/* 554*/        this.level = level;
/*   0*/      }
/*   0*/      
/*   0*/      public boolean isMultiValued() {
/* 558*/        return this.proxy.isMultiValued();
/*   0*/      }
/*   0*/      
/*   0*/      public Class<String> getType() {
/* 562*/        return this.proxy.getType();
/*   0*/      }
/*   0*/      
/*   0*/      public void addValue(String value) throws CmdLineException {
/* 566*/        this.proxy.addValue(value);
/* 567*/        CommandLineRunner.Flags.guardLevels.add(new CommandLineRunner.GuardLevel(value, this.level));
/*   0*/      }
/*   0*/      
/*   0*/      public FieldSetter asFieldSetter() {
/* 571*/        return this.proxy.asFieldSetter();
/*   0*/      }
/*   0*/      
/*   0*/      public AnnotatedElement asAnnotatedElement() {
/* 575*/        return this.proxy.asAnnotatedElement();
/*   0*/      }
/*   0*/    }
/*   0*/    
/*   0*/    public static AbstractCommandLineRunner.WarningGuardSpec getWarningGuardSpec() {
/* 580*/      AbstractCommandLineRunner.WarningGuardSpec spec = new AbstractCommandLineRunner.WarningGuardSpec();
/* 581*/      for (CommandLineRunner.GuardLevel guardLevel : guardLevels)
/* 582*/        spec.add(guardLevel.level, guardLevel.name); 
/* 584*/      return spec;
/*   0*/    }
/*   0*/    
/*   0*/    private Flags() {}
/*   0*/  }
/*   0*/  
/*   0*/  private enum FormattingOption {
/* 592*/    PRETTY_PRINT, PRINT_INPUT_DELIMITER, SINGLE_QUOTES;
/*   0*/    
/*   0*/    private void applyToOptions(CompilerOptions options) {
/* 598*/      switch (this) {
/*   0*/        case PRETTY_PRINT:
/* 600*/          options.prettyPrint = true;
/*   0*/          break;
/*   0*/        case PRINT_INPUT_DELIMITER:
/* 603*/          options.printInputDelimiter = true;
/*   0*/          break;
/*   0*/        case SINGLE_QUOTES:
/* 606*/          options.setPreferSingleQuotes(true);
/*   0*/          break;
/*   0*/        default:
/* 609*/          throw new RuntimeException("Unknown formatting option: " + this);
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 614*/  private final Flags flags = new Flags();
/*   0*/  
/*   0*/  private boolean isConfigValid = false;
/*   0*/  
/*   0*/  protected CommandLineRunner(String[] args) {
/* 625*/    initConfigFromFlags(args, System.err);
/*   0*/  }
/*   0*/  
/*   0*/  protected CommandLineRunner(String[] args, PrintStream out, PrintStream err) {
/* 629*/    super(out, err);
/* 630*/    initConfigFromFlags(args, err);
/*   0*/  }
/*   0*/  
/*   0*/  private List<String> tokenizeKeepingQuotedStrings(List<String> lines) {
/* 644*/    List<String> tokens = Lists.newArrayList();
/* 645*/    Pattern tokenPattern = Pattern.compile("(?:[^ \t\f\\x0B'\"]|(?:'[^']*'|\"[^\"]*\"))+");
/* 648*/    for (String line : lines) {
/* 649*/      Matcher matcher = tokenPattern.matcher(line);
/* 650*/      while (matcher.find())
/* 651*/        tokens.add(matcher.group(0)); 
/*   0*/    } 
/* 654*/    return tokens;
/*   0*/  }
/*   0*/  
/*   0*/  private List<String> processArgs(String[] args) {
/* 661*/    Pattern argPattern = Pattern.compile("(--[a-zA-Z_]+)=(.*)");
/* 662*/    Pattern quotesPattern = Pattern.compile("^['\"](.*)['\"]$");
/* 663*/    List<String> processedArgs = Lists.newArrayList();
/* 665*/    for (String arg : args) {
/* 666*/      Matcher matcher = argPattern.matcher(arg);
/* 667*/      if (matcher.matches()) {
/* 668*/        processedArgs.add(matcher.group(1));
/* 670*/        String value = matcher.group(2);
/* 671*/        Matcher quotesMatcher = quotesPattern.matcher(value);
/* 672*/        if (quotesMatcher.matches()) {
/* 673*/          processedArgs.add(quotesMatcher.group(1));
/*   0*/        } else {
/* 675*/          processedArgs.add(value);
/*   0*/        } 
/*   0*/      } else {
/* 678*/        processedArgs.add(arg);
/*   0*/      } 
/*   0*/    } 
/* 682*/    return processedArgs;
/*   0*/  }
/*   0*/  
/*   0*/  private void processFlagFile(PrintStream err) throws CmdLineException, IOException {
/* 687*/    File flagFileInput = new File(this.flags.flagFile);
/* 688*/    List<String> argsInFile = tokenizeKeepingQuotedStrings(Files.readLines(flagFileInput, Charset.defaultCharset()));
/* 691*/    this.flags.flagFile = "";
/* 692*/    List<String> processedFileArgs = processArgs(argsInFile.<String>toArray(new String[0]));
/* 694*/    CmdLineParser parserFileArgs = new CmdLineParser(this.flags);
/* 697*/    List<GuardLevel> previous = Lists.newArrayList(Flags.guardLevels);
/* 698*/    Flags.guardLevels.clear();
/* 699*/    parserFileArgs.parseArgument(processedFileArgs.<String>toArray(new String[0]));
/* 700*/    Flags.guardLevels.addAll(previous);
/* 703*/    if (!this.flags.flagFile.equals("")) {
/* 704*/      err.println("ERROR - Arguments in the file cannot contain --flagfile option.");
/* 706*/      this.isConfigValid = false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void initConfigFromFlags(String[] args, PrintStream err) {
/* 712*/    List<String> processedArgs = processArgs(args);
/* 714*/    CmdLineParser parser = new CmdLineParser(this.flags);
/* 715*/    Flags.guardLevels.clear();
/* 716*/    this.isConfigValid = true;
/*   0*/    try {
/* 718*/      parser.parseArgument(processedArgs.<String>toArray(new String[0]));
/* 720*/      if (!this.flags.flagFile.equals(""))
/* 721*/        processFlagFile(err); 
/* 723*/    } catch (CmdLineException e) {
/* 724*/      err.println(e.getMessage());
/* 725*/      this.isConfigValid = false;
/* 726*/    } catch (IOException ioErr) {
/* 727*/      err.println("ERROR - " + this.flags.flagFile + " read error.");
/* 728*/      this.isConfigValid = false;
/*   0*/    } 
/* 731*/    if (this.flags.version) {
/* 732*/      err.println("Closure Compiler (http://code.google.com/closure/compiler)\nVersion: " + Compiler.getReleaseVersion() + "\n" + "Built on: " + Compiler.getReleaseDate());
/* 736*/      err.flush();
/*   0*/    } 
/* 739*/    if (this.flags.processCommonJsModules) {
/* 740*/      this.flags.processClosurePrimitives = true;
/* 741*/      this.flags.manageClosureDependencies = true;
/* 742*/      if (this.flags.commonJsEntryModule == null) {
/* 743*/        err.println("Please specify --common_js_entry_module.");
/* 744*/        err.flush();
/* 745*/        this.isConfigValid = false;
/*   0*/      } 
/* 747*/      this.flags.closureEntryPoint = Lists.newArrayList((Object[])new String[] { ProcessCommonJSModules.toModuleName(this.flags.commonJsEntryModule) });
/*   0*/    } 
/* 751*/    if (!this.isConfigValid || this.flags.displayHelp) {
/* 752*/      this.isConfigValid = false;
/* 753*/      parser.printUsage(err);
/*   0*/    } else {
/*   0*/      CodingConvention conv;
/* 756*/      if (this.flags.thirdParty) {
/* 757*/        conv = CodingConventions.getDefault();
/* 758*/      } else if (this.flags.processJqueryPrimitives) {
/* 759*/        conv = new JqueryCodingConvention();
/*   0*/      } else {
/* 761*/        conv = new ClosureCodingConvention();
/*   0*/      } 
/* 764*/      getCommandLineConfig().setPrintTree(this.flags.printTree).setPrintAst(this.flags.printAst).setPrintPassGraph(this.flags.printPassGraph).setJscompDevMode(this.flags.jscompDevMode).setLoggingLevel(this.flags.loggingLevel).setExterns(this.flags.externs).setJs(this.flags.getJsFiles()).setJsOutputFile(this.flags.jsOutputFile).setModule(this.flags.module).setVariableMapInputFile(this.flags.variableMapInputFile).setPropertyMapInputFile(this.flags.propertyMapInputFile).setVariableMapOutputFile(this.flags.variableMapOutputFile).setCreateNameMapFiles(this.flags.createNameMapFiles).setPropertyMapOutputFile(this.flags.propertyMapOutputFile).setCodingConvention(conv).setSummaryDetailLevel(this.flags.summaryDetailLevel).setOutputWrapper(this.flags.outputWrapper).setModuleWrapper(this.flags.moduleWrapper).setModuleOutputPathPrefix(this.flags.moduleOutputPathPrefix).setCreateSourceMap(this.flags.createSourceMap).setSourceMapFormat(this.flags.sourceMapFormat).setWarningGuardSpec(Flags.getWarningGuardSpec()).setDefine(this.flags.define).setCharset(this.flags.charset).setManageClosureDependencies(this.flags.manageClosureDependencies).setOnlyClosureDependencies(this.flags.onlyClosureDependencies).setClosureEntryPoints(this.flags.closureEntryPoint).setOutputManifest((List<String>)ImmutableList.of(this.flags.outputManifest)).setOutputModuleDependencies(this.flags.outputModuleDependencies).setAcceptConstKeyword(this.flags.acceptConstKeyword).setLanguageIn(this.flags.languageIn).setProcessCommonJSModules(this.flags.processCommonJsModules).setCommonJSModulePathPrefix(this.flags.commonJsPathPrefix).setTransformAMDToCJSModules(this.flags.transformAmdModules).setWarningsWhitelistFile(this.flags.warningsWhitelistFile).setAngularPass(this.flags.angularPass).setTracerMode(this.flags.tracerMode);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected CompilerOptions createOptions() {
/* 807*/    CompilerOptions options = new CompilerOptions();
/* 808*/    if (this.flags.processJqueryPrimitives) {
/* 809*/      options.setCodingConvention(new JqueryCodingConvention());
/*   0*/    } else {
/* 811*/      options.setCodingConvention(new ClosureCodingConvention());
/*   0*/    } 
/* 814*/    options.setExtraAnnotationNames(this.flags.extraAnnotationName);
/* 816*/    CompilationLevel level = this.flags.compilationLevel;
/* 817*/    level.setOptionsForCompilationLevel(options);
/* 819*/    if (this.flags.debug)
/* 820*/      level.setDebugOptionsForCompilationLevel(options); 
/* 823*/    if (this.flags.useTypesForOptimization)
/* 824*/      level.setTypeBasedOptimizationOptions(options); 
/* 827*/    if (this.flags.generateExports)
/* 828*/      options.setGenerateExports(this.flags.generateExports); 
/* 831*/    WarningLevel wLevel = this.flags.warningLevel;
/* 832*/    wLevel.setOptionsForWarningLevel(options);
/* 833*/    for (FormattingOption formattingOption : this.flags.formatting)
/* 834*/      formattingOption.applyToOptions(options); 
/* 837*/    options.closurePass = this.flags.processClosurePrimitives;
/* 839*/    options.jqueryPass = (CompilationLevel.ADVANCED_OPTIMIZATIONS == level && this.flags.processJqueryPrimitives);
/* 842*/    options.angularPass = this.flags.angularPass;
/* 844*/    if (!this.flags.translationsFile.isEmpty()) {
/*   0*/      try {
/* 846*/        options.messageBundle = new XtbMessageBundle(new FileInputStream(this.flags.translationsFile), this.flags.translationsProject);
/* 849*/      } catch (IOException e) {
/* 850*/        throw new RuntimeException("Reading XTB file", e);
/*   0*/      } 
/* 852*/    } else if (CompilationLevel.ADVANCED_OPTIMIZATIONS == level) {
/* 861*/      new EmptyMessageBundle();
/* 861*/      new EmptyMessageBundle();
/* 861*/      options.messageBundle = null;
/*   0*/    } 
/* 864*/    return options;
/*   0*/  }
/*   0*/  
/*   0*/  protected Compiler createCompiler() {
/* 869*/    return new Compiler(getErrorPrintStream());
/*   0*/  }
/*   0*/  
/*   0*/  protected List<SourceFile> createExterns() throws AbstractCommandLineRunner.FlagUsageException, IOException {
/* 875*/    List<SourceFile> externs = super.createExterns();
/* 876*/    if (this.flags.useOnlyCustomExterns || isInTestMode())
/* 877*/      return externs; 
/* 879*/    List<SourceFile> defaultExterns = getDefaultExterns();
/* 880*/    defaultExterns.addAll(externs);
/* 881*/    return defaultExterns;
/*   0*/  }
/*   0*/  
/* 886*/  private static final List<String> DEFAULT_EXTERNS_NAMES = (List<String>)ImmutableList.of("es3.js", "es5.js", "w3c_event.js", "w3c_event3.js", "gecko_event.js", "ie_event.js", "webkit_event.js", "w3c_device_sensor_event.js", "w3c_dom1.js", "w3c_dom2.js", "w3c_dom3.js", "gecko_dom.js", (Object[])new String[] { 
/* 886*/        "ie_dom.js", "webkit_dom.js", "w3c_css.js", "gecko_css.js", "ie_css.js", "webkit_css.js", "google.js", "chrome.js", "deprecated.js", "fileapi.js", 
/* 886*/        "flash.js", "gears_symbols.js", "gears_types.js", "gecko_xml.js", "html5.js", "ie_vml.js", "iphone.js", "webstorage.js", "w3c_anim_timing.js", "w3c_css3d.js", 
/* 886*/        "w3c_elementtraversal.js", "w3c_geolocation.js", "w3c_indexeddb.js", "w3c_navigation_timing.js", "w3c_range.js", "w3c_selectors.js", "w3c_xml.js", "window.js", "webkit_notifications.js", "webgl.js" });
/*   0*/  
/*   0*/  public static List<SourceFile> getDefaultExterns() throws IOException {
/* 946*/    InputStream input = CommandLineRunner.class.getResourceAsStream("/externs.zip");
/* 948*/    if (input == null)
/* 950*/      input = CommandLineRunner.class.getResourceAsStream("externs.zip"); 
/* 952*/    Preconditions.checkNotNull(input);
/* 954*/    ZipInputStream zip = new ZipInputStream(input);
/* 955*/    Map<String, SourceFile> externsMap = Maps.newHashMap();
/* 956*/    for (ZipEntry entry = null; (entry = zip.getNextEntry()) != null; ) {
/* 957*/      BufferedInputStream entryStream = new BufferedInputStream(ByteStreams.limit(zip, entry.getSize()));
/* 959*/      externsMap.put(entry.getName(), SourceFile.fromInputStream("externs.zip//" + entry.getName(), entryStream));
/*   0*/    } 
/* 967*/    Preconditions.checkState(externsMap.keySet().equals(Sets.newHashSet(DEFAULT_EXTERNS_NAMES)), "Externs zip must match our hard-coded list of externs.");
/* 973*/    List<SourceFile> externs = Lists.newArrayList();
/* 974*/    for (String key : DEFAULT_EXTERNS_NAMES)
/* 975*/      externs.add(externsMap.get(key)); 
/* 978*/    return externs;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldRunCompiler() {
/* 985*/    return this.isConfigValid;
/*   0*/  }
/*   0*/  
/*   0*/  public static void main(String[] args) {
/* 992*/    CommandLineRunner runner = new CommandLineRunner(args);
/* 993*/    if (runner.shouldRunCompiler()) {
/* 994*/      runner.run();
/*   0*/    } else {
/* 996*/      System.exit(-1);
/*   0*/    } 
/*   0*/  }
/*   0*/}
