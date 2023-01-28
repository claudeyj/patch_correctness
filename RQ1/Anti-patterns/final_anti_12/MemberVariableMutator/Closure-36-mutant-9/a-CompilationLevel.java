/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/public enum CompilationLevel {
/*  32*/  WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS;
/*   0*/  
/*   0*/  public void setOptionsForCompilationLevel(CompilerOptions options) {
/*  54*/    switch (this) {
/*   0*/      case WHITESPACE_ONLY:
/*  56*/        applyBasicCompilationOptions(options);
/*   0*/        break;
/*   0*/      case SIMPLE_OPTIMIZATIONS:
/*  59*/        applySafeCompilationOptions(options);
/*   0*/        break;
/*   0*/      case ADVANCED_OPTIMIZATIONS:
/*  62*/        applyFullCompilationOptions(options);
/*   0*/        break;
/*   0*/      default:
/*  65*/        throw new RuntimeException("Unknown compilation level.");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setDebugOptionsForCompilationLevel(CompilerOptions options) {
/*  70*/    options.anonymousFunctionNaming = AnonymousFunctionNamingPolicy.UNMAPPED;
/*  71*/    options.generatePseudoNames = true;
/*  72*/    options.removeClosureAsserts = false;
/*  74*/    options.shadowVariables = false;
/*   0*/  }
/*   0*/  
/*   0*/  private static void applyBasicCompilationOptions(CompilerOptions options) {
/*  82*/    options.skipAllCompilerPasses();
/*  85*/    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  private static void applySafeCompilationOptions(CompilerOptions options) {
/*  97*/    options.replaceIdGenerators = false;
/* 101*/    options.closurePass = true;
/* 102*/    options.setRenamingPolicy(VariableRenamingPolicy.LOCAL, PropertyRenamingPolicy.OFF);
/* 104*/    options.shadowVariables = true;
/* 105*/    options.setInlineVariables(CompilerOptions.Reach.LOCAL_ONLY);
/* 106*/    options.flowSensitiveInlineVariables = true;
/* 107*/    options.setInlineFunctions(CompilerOptions.Reach.LOCAL_ONLY);
/* 108*/    options.checkGlobalThisLevel = CheckLevel.OFF;
/* 109*/    options.foldConstants = true;
/* 110*/    options.coalesceVariableNames = true;
/* 111*/    options.deadAssignmentElimination = true;
/* 112*/    options.collapseVariableDeclarations = true;
/* 113*/    options.convertToDottedProperties = true;
/* 114*/    options.labelRenaming = true;
/* 115*/    options.removeDeadCode = true;
/* 116*/    options.optimizeArgumentsArray = true;
/* 117*/    options.setRemoveUnusedVariables(CompilerOptions.Reach.LOCAL_ONLY);
/* 118*/    options.collapseObjectLiterals = true;
/* 119*/    options.protectHiddenSideEffects = true;
/* 122*/    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  private static void applyFullCompilationOptions(CompilerOptions options) {
/* 136*/    options.closurePass = true;
/* 137*/    options.foldConstants = true;
/* 138*/    options.coalesceVariableNames = true;
/* 139*/    options.deadAssignmentElimination = true;
/* 140*/    options.extractPrototypeMemberDeclarations = true;
/* 141*/    options.collapseVariableDeclarations = true;
/* 142*/    options.convertToDottedProperties = true;
/* 143*/    options.rewriteFunctionExpressions = true;
/* 144*/    options.labelRenaming = true;
/* 145*/    options.removeDeadCode = true;
/* 146*/    options.optimizeArgumentsArray = true;
/* 147*/    options.collapseObjectLiterals = true;
/* 148*/    options.protectHiddenSideEffects = true;
/* 151*/    options.removeClosureAsserts = true;
/* 152*/    options.aliasKeywords = true;
/* 153*/    options.reserveRawExports = true;
/* 154*/    options.setRenamingPolicy(VariableRenamingPolicy.ALL, PropertyRenamingPolicy.ALL_UNQUOTED);
/* 156*/    options.shadowVariables = true;
/* 157*/    options.removeUnusedPrototypeProperties = true;
/* 158*/    options.removeUnusedPrototypePropertiesInExterns = true;
/* 159*/    options.collapseAnonymousFunctions = true;
/* 160*/    options.collapseProperties = true;
/* 161*/    options.checkGlobalThisLevel = CheckLevel.WARNING;
/* 162*/    options.rewriteFunctionExpressions = true;
/* 163*/    options.smartNameRemoval = true;
/* 164*/    options.inlineConstantVars = true;
/* 165*/    options.setInlineFunctions(CompilerOptions.Reach.ALL);
/* 166*/    options.inlineGetters = true;
/* 167*/    options.setInlineVariables(CompilerOptions.Reach.ALL);
/* 168*/    options.flowSensitiveInlineVariables = true;
/* 169*/    options.computeFunctionSideEffects = true;
/* 172*/    options.setRemoveUnusedVariables(CompilerOptions.Reach.ALL);
/* 175*/    options.crossModuleCodeMotion = true;
/* 176*/    options.crossModuleMethodMotion = true;
/* 179*/    options.devirtualizePrototypeMethods = true;
/* 180*/    options.optimizeParameters = true;
/* 181*/    options.optimizeReturns = true;
/* 182*/    options.optimizeCalls = true;
/* 185*/    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.WARNING);
/*   0*/  }
/*   0*/}
