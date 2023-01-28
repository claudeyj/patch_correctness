/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/public enum CompilationLevel {
/*  30*/  WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS;
/*   0*/  
/*   0*/  public void setOptionsForCompilationLevel(CompilerOptions options) {
/*  52*/    switch (this) {
/*   0*/      case WHITESPACE_ONLY:
/*  54*/        applyBasicCompilationOptions(options);
/*   0*/        break;
/*   0*/      case SIMPLE_OPTIMIZATIONS:
/*  57*/        applySafeCompilationOptions(options);
/*   0*/        break;
/*   0*/      case ADVANCED_OPTIMIZATIONS:
/*  60*/        applyFullCompilationOptions(options);
/*   0*/        break;
/*   0*/      default:
/*  63*/        throw new RuntimeException("Unknown compilation level.");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setDebugOptionsForCompilationLevel(CompilerOptions options) {
/*  68*/    options.anonymousFunctionNaming = AnonymousFunctionNamingPolicy.UNMAPPED;
/*  69*/    options.generatePseudoNames = true;
/*   0*/  }
/*   0*/  
/*   0*/  private static void applyBasicCompilationOptions(CompilerOptions options) {
/*  77*/    options.skipAllCompilerPasses();
/*  78*/    options.checkGlobalThisLevel = CheckLevel.OFF;
/*  81*/    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  private static void applySafeCompilationOptions(CompilerOptions options) {
/*  94*/    options.checkSuspiciousCode = true;
/*  95*/    options.variableRenaming = VariableRenamingPolicy.LOCAL;
/*  96*/    options.inlineLocalVariables = true;
/*  97*/    options.checkGlobalThisLevel = CheckLevel.OFF;
/*  98*/    options.foldConstants = true;
/*  99*/    options.removeConstantExpressions = true;
/* 100*/    options.coalesceVariableNames = true;
/* 101*/    options.deadAssignmentElimination = true;
/* 102*/    options.extractPrototypeMemberDeclarations = true;
/* 103*/    options.collapseVariableDeclarations = true;
/* 104*/    options.convertToDottedProperties = true;
/* 105*/    options.labelRenaming = true;
/* 106*/    options.removeDeadCode = true;
/* 107*/    options.optimizeArgumentsArray = true;
/* 108*/    options.removeUnusedVars = true;
/* 109*/    options.removeUnusedVarsInGlobalScope = false;
/* 112*/    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  private static void applyFullCompilationOptions(CompilerOptions options) {
/* 126*/    options.closurePass = true;
/* 127*/    options.checkGlobalThisLevel = CheckLevel.OFF;
/* 128*/    options.foldConstants = true;
/* 129*/    options.removeConstantExpressions = true;
/* 130*/    options.coalesceVariableNames = true;
/* 131*/    options.deadAssignmentElimination = true;
/* 132*/    options.extractPrototypeMemberDeclarations = true;
/* 133*/    options.collapseVariableDeclarations = true;
/* 134*/    options.convertToDottedProperties = true;
/* 135*/    options.rewriteFunctionExpressions = true;
/* 136*/    options.labelRenaming = true;
/* 137*/    options.removeDeadCode = true;
/* 138*/    options.optimizeArgumentsArray = true;
/* 141*/    options.reserveRawExports = true;
/* 142*/    options.variableRenaming = VariableRenamingPolicy.ALL;
/* 143*/    options.propertyRenaming = PropertyRenamingPolicy.ALL_UNQUOTED;
/* 144*/    options.removeUnusedPrototypeProperties = true;
/* 145*/    options.removeUnusedPrototypePropertiesInExterns = true;
/* 146*/    options.collapseAnonymousFunctions = true;
/* 147*/    options.collapseProperties = true;
/* 148*/    options.rewriteFunctionExpressions = true;
/* 149*/    options.devirtualizePrototypeMethods = true;
/* 150*/    options.smartNameRemoval = true;
/* 151*/    options.inlineConstantVars = true;
/* 152*/    options.inlineFunctions = true;
/* 153*/    options.inlineLocalFunctions = true;
/* 154*/    options.inlineAnonymousFunctionExpressions = true;
/* 155*/    options.inlineGetters = true;
/* 156*/    options.inlineVariables = true;
/* 157*/    options.removeConstantExpressions = true;
/* 158*/    options.computeFunctionSideEffects = true;
/* 161*/    options.removeUnusedVars = true;
/* 162*/    options.removeUnusedVarsInGlobalScope = true;
/* 165*/    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.WARNING);
/*   0*/  }
/*   0*/}
