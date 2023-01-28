/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Enumeration;
/*   0*/import java.util.List;
/*   0*/import java.util.Properties;
/*   0*/
/*   0*/public class DefaultParser implements CommandLineParser {
/*   0*/  protected CommandLine cmd;
/*   0*/  
/*   0*/  protected Options options;
/*   0*/  
/*   0*/  protected boolean stopAtNonOption;
/*   0*/  
/*   0*/  protected String currentToken;
/*   0*/  
/*   0*/  protected Option currentOption;
/*   0*/  
/*   0*/  protected boolean skipParsing;
/*   0*/  
/*   0*/  protected List expectedOpts;
/*   0*/  
/*   0*/  public CommandLine parse(Options options, String[] arguments) throws ParseException {
/*  60*/    return parse(options, arguments, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CommandLine parse(Options options, String[] arguments, Properties properties) throws ParseException {
/*  76*/    return parse(options, arguments, properties, false);
/*   0*/  }
/*   0*/  
/*   0*/  public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
/*  81*/    return parse(options, arguments, null, stopAtNonOption);
/*   0*/  }
/*   0*/  
/*   0*/  public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) throws ParseException {
/* 102*/    this.options = options;
/* 103*/    this.stopAtNonOption = stopAtNonOption;
/* 104*/    this.skipParsing = false;
/* 105*/    this.currentOption = null;
/* 106*/    this.expectedOpts = new ArrayList(options.getRequiredOptions());
/* 109*/    for (OptionGroup group : options.getOptionGroups()) {
/* 111*/        group.setSelected(null); 
/*   0*/       }
/* 114*/    this.cmd = new CommandLine();
/* 116*/    if (arguments != null) {
/* 118*/        for (String argument : arguments) {
/* 120*/            handleToken(argument); 
/*   0*/           } 
/*   0*/       }
/* 125*/    checkRequiredArgs();
/* 128*/    handleProperties(properties);
/* 130*/    checkRequiredOptions();
/* 132*/    return this.cmd;
/*   0*/  }
/*   0*/  
/*   0*/  private void handleProperties(Properties properties) throws ParseException {
/* 142*/    if (properties == null) {
/*   0*/        return; 
/*   0*/       }
/* 147*/    for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
/* 149*/      String option = e.nextElement().toString();
/* 151*/      Option opt = this.options.getOption(option);
/* 152*/      if (opt == null) {
/* 154*/          throw new UnrecognizedOptionException("Default option wasn't defined", option); 
/*   0*/         }
/* 158*/      OptionGroup group = this.options.getOptionGroup(opt);
/* 159*/      boolean selected = (group != null && group.getSelected() != null);
/* 161*/      if (!this.cmd.hasOption(option) && !selected) {
/* 164*/        String value = properties.getProperty(option);
/* 166*/        if (opt.hasArg()) {
/* 168*/          if (opt.getValues() == null || (opt.getValues()).length == 0) {
/* 170*/              opt.addValueForProcessing(value); 
/*   0*/             }
/* 173*/        } else if (!"yes".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"1".equalsIgnoreCase(value)) {
/*   0*/          continue;
/*   0*/        } 
/* 181*/        handleOption(opt);
/* 182*/        this.currentOption = null;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkRequiredOptions() throws MissingOptionException {
/* 197*/    if (!this.expectedOpts.isEmpty()) {
/* 199*/        throw new MissingOptionException(this.expectedOpts); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void checkRequiredArgs() throws ParseException {
/* 209*/    if (this.currentOption != null && this.currentOption.requiresArg()) {
/* 211*/        throw new MissingArgumentException(this.currentOption); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void handleToken(String token) throws ParseException {
/* 223*/    this.currentToken = token;
/* 225*/    if (this.skipParsing) {
/* 227*/      this.cmd.addArg(token);
/* 229*/    } else if ("--".equals(token)) {
/* 231*/      this.skipParsing = true;
/* 233*/    } else if (this.currentOption != null && this.currentOption.acceptsArg() && isArgument(token)) {
/* 235*/      this.currentOption.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(token));
/* 237*/    } else if (token.startsWith("--")) {
/* 239*/      handleLongOption(token);
/* 241*/    } else if (token.startsWith("-") && !"-".equals(token)) {
/* 243*/      handleShortAndLongOption(token);
/*   0*/    } else {
/* 247*/      handleUnknownToken(token);
/*   0*/    } 
/* 250*/    if (this.currentOption != null && !this.currentOption.acceptsArg()) {
/* 252*/        this.currentOption = null; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isArgument(String token) {
/* 263*/    return (!isOption(token) || isNegativeNumber(token));
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isNegativeNumber(String token) {
/*   0*/    try {
/* 275*/      Double.parseDouble(token);
/* 276*/      return true;
/* 278*/    } catch (NumberFormatException e) {
/* 280*/      return false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isOption(String token) {
/* 291*/    return (isLongOption(token) || isShortOption(token));
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isShortOption(String token) {
/* 302*/    return (token.startsWith("-") && token.length() >= 2 && this.options.hasShortOption(token.substring(1, 2)));
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isLongOption(String token) {
/* 314*/    if (!token.startsWith("-") || token.length() == 1) {
/* 316*/        return false; 
/*   0*/       }
/* 319*/    int pos = token.indexOf("=");
/* 320*/    String t = (pos == -1) ? token : token.substring(0, pos);
/* 322*/    if (!this.options.getMatchingOptions(t).isEmpty()) {
/* 325*/        return true; 
/*   0*/       }
/* 327*/    if (getLongPrefix(token) != null && !token.startsWith("--")) {
/* 330*/        return true; 
/*   0*/       }
/* 333*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void handleUnknownToken(String token) throws ParseException {
/* 347*/    if (token.startsWith("-") && token.length() > 1 && !this.stopAtNonOption) {
/* 349*/        throw new UnrecognizedOptionException("Unrecognized option: " + token, token); 
/*   0*/       }
/* 352*/    this.cmd.addArg(token);
/* 353*/    if (this.stopAtNonOption) {
/* 355*/        this.skipParsing = true; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void handleLongOption(String token) throws ParseException {
/* 371*/    if (token.indexOf('=') == -1) {
/* 373*/      handleLongOptionWithoutEqual(token);
/*   0*/    } else {
/* 377*/      handleLongOptionWithEqual(token);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleLongOptionWithoutEqual(String token) throws ParseException {
/* 393*/    List<String> matchingOpts = this.options.getMatchingOptions(token);
/* 394*/    if (matchingOpts.isEmpty()) {
/* 396*/      handleUnknownToken(this.currentToken);
/*   0*/    } else {
/* 398*/      if (matchingOpts.size() > 1) {
/* 400*/          throw new AmbiguousOptionException(token, matchingOpts); 
/*   0*/         }
/* 404*/      handleOption(this.options.getOption(matchingOpts.get(0)));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleLongOptionWithEqual(String token) throws ParseException {
/* 420*/    int pos = token.indexOf('=');
/* 422*/    String value = token.substring(pos + 1);
/* 424*/    String opt = token.substring(0, pos);
/* 426*/    List<String> matchingOpts = this.options.getMatchingOptions(opt);
/* 427*/    if (matchingOpts.isEmpty()) {
/* 429*/      handleUnknownToken(this.currentToken);
/*   0*/    } else {
/* 431*/      if (matchingOpts.size() > 1) {
/* 433*/          throw new AmbiguousOptionException(opt, matchingOpts); 
/*   0*/         }
/* 437*/      Option option = this.options.getOption(matchingOpts.get(0));
/* 439*/      if (option.acceptsArg()) {
/* 441*/        handleOption(option);
/* 442*/        this.currentOption.addValueForProcessing(value);
/* 443*/        this.currentOption = null;
/*   0*/      } else {
/* 447*/        handleUnknownToken(this.currentToken);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleShortAndLongOption(String token) throws ParseException {
/* 473*/    String t = Util.stripLeadingHyphens(token);
/* 475*/    int pos = t.indexOf('=');
/* 477*/    if (t.length() == 1) {
/* 480*/      if (this.options.hasShortOption(t)) {
/* 482*/        handleOption(this.options.getOption(t));
/*   0*/      } else {
/* 486*/        handleUnknownToken(token);
/*   0*/      } 
/* 489*/    } else if (pos == -1) {
/* 492*/      if (this.options.hasShortOption(t)) {
/* 494*/        handleOption(this.options.getOption(t));
/* 496*/      } else if (!this.options.getMatchingOptions(t).isEmpty()) {
/* 499*/        handleLongOptionWithoutEqual(token);
/*   0*/      } else {
/* 504*/        String opt = getLongPrefix(t);
/* 506*/        if (opt != null && this.options.getOption(opt).acceptsArg()) {
/* 508*/          handleOption(this.options.getOption(opt));
/* 509*/          this.currentOption.addValueForProcessing(t.substring(opt.length()));
/* 510*/          this.currentOption = null;
/* 512*/        } else if (isJavaProperty(t)) {
/* 515*/          handleOption(this.options.getOption(t.substring(0, 1)));
/* 516*/          this.currentOption.addValueForProcessing(t.substring(1));
/* 517*/          this.currentOption = null;
/*   0*/        } else {
/* 522*/          handleConcatenatedOptions(token);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/* 529*/      String opt = t.substring(0, pos);
/* 530*/      String value = t.substring(pos + 1);
/* 532*/      if (opt.length() == 1) {
/* 535*/        Option option = this.options.getOption(opt);
/* 536*/        if (option != null && option.acceptsArg()) {
/* 538*/          handleOption(option);
/* 539*/          this.currentOption.addValueForProcessing(value);
/* 540*/          this.currentOption = null;
/*   0*/        } else {
/* 544*/          handleUnknownToken(token);
/*   0*/        } 
/* 547*/      } else if (isJavaProperty(opt)) {
/* 550*/        handleOption(this.options.getOption(opt.substring(0, 1)));
/* 551*/        this.currentOption.addValueForProcessing(opt.substring(1));
/* 552*/        this.currentOption.addValueForProcessing(value);
/* 553*/        this.currentOption = null;
/*   0*/      } else {
/* 558*/        handleLongOptionWithEqual(token);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private String getLongPrefix(String token) {
/* 570*/    String t = Util.stripLeadingHyphens(token);
/* 573*/    String opt = null;
/* 574*/    for (int i = t.length() - 2; i > 1; i--) {
/* 576*/      String prefix = t.substring(0, i);
/* 577*/      if (this.options.hasLongOption(prefix)) {
/* 579*/        opt = prefix;
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/* 584*/    return opt;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isJavaProperty(String token) {
/* 592*/    String opt = token.substring(0, 1);
/* 593*/    Option option = this.options.getOption(opt);
/* 595*/    return (option != null && (option.getArgs() >= 2 || option.getArgs() == -2));
/*   0*/  }
/*   0*/  
/*   0*/  private void handleOption(Option option) throws ParseException {
/* 601*/    checkRequiredArgs();
/* 603*/    option = (Option)option.clone();
/* 605*/    updateRequiredOptions(option);
/* 607*/    this.cmd.addOption(option);
/* 609*/    if (option.hasArg()) {
/* 611*/      this.currentOption = option;
/*   0*/    } else {
/* 615*/      this.currentOption = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateRequiredOptions(Option option) throws AlreadySelectedException {
/* 626*/    if (option.isRequired()) {
/* 628*/        this.expectedOpts.remove(option.getKey()); 
/*   0*/       }
/* 632*/    if (this.options.getOptionGroup(option) != null) {
/* 634*/      OptionGroup group = this.options.getOptionGroup(option);
/* 636*/      if (group.isRequired()) {
/* 638*/          this.expectedOpts.remove(group); 
/*   0*/         }
/* 641*/      group.setSelected(option);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void handleConcatenatedOptions(String token) throws ParseException {
/* 674*/    for (int i = 1; i < token.length(); i++) {
/* 676*/      String ch = String.valueOf(token.charAt(i));
/* 678*/      if (this.options.hasOption(ch)) {
/* 680*/        handleOption(this.options.getOption(ch));
/* 682*/        if (this.currentOption != null && token.length() != i + 1) {
/* 685*/          this.currentOption.addValueForProcessing(token.substring(i + 1));
/*   0*/          break;
/*   0*/        } 
/*   0*/      } else {
/* 691*/        handleUnknownToken((this.stopAtNonOption && i > 1) ? token.substring(i) : token);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/}
