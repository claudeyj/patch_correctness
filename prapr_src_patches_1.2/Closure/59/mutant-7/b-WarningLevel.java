/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/public enum WarningLevel {
/*  27*/  QUIET, DEFAULT, VERBOSE;
/*   0*/  
/*   0*/  public void setOptionsForWarningLevel(CompilerOptions options) {
/*  34*/    switch (this) {
/*   0*/      case QUIET:
/*  36*/        silenceAllWarnings(options);
/*   0*/        break;
/*   0*/      case DEFAULT:
/*  39*/        addDefaultWarnings(options);
/*   0*/        break;
/*   0*/      case VERBOSE:
/*  42*/        addVerboseWarnings(options);
/*   0*/        break;
/*   0*/      default:
/*  45*/        throw new RuntimeException("Unknown warning level.");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void silenceAllWarnings(CompilerOptions options) {
/*  55*/    options.addWarningsGuard(new ShowByPathWarningsGuard("the_longest_path_that_cannot_be_expressed_as_a_string"));
/*   0*/  }
/*   0*/  
/*   0*/  private static void addDefaultWarnings(CompilerOptions options) {
/*  65*/    options.checkSuspiciousCode = true;
/*  66*/    options.checkUnreachableCode = CheckLevel.WARNING;
/*  67*/    options.checkControlStructures = true;
/*   0*/  }
/*   0*/  
/*   0*/  private static void addVerboseWarnings(CompilerOptions options) {
/*  75*/    addDefaultWarnings(options);
/*  78*/    options.checkSuspiciousCode = true;
/*  79*/    options.checkGlobalThisLevel = CheckLevel.OFF;
/*  80*/    options.checkSymbols = true;
/*  81*/    options.checkMissingReturn = CheckLevel.WARNING;
/*  88*/    options.checkTypes = true;
/*  89*/    options.checkGlobalNamesLevel = CheckLevel.WARNING;
/*  90*/    options.aggressiveVarCheck = CheckLevel.WARNING;
/*  91*/    options.setWarningLevel(DiagnosticGroups.MISSING_PROPERTIES, CheckLevel.WARNING);
/*  93*/    options.setWarningLevel(DiagnosticGroups.DEPRECATED, CheckLevel.WARNING);
/*   0*/  }
/*   0*/}
