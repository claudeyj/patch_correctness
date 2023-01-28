/*   0*/package com.google.javascript.jscomp.testing;
/*   0*/
/*   0*/import com.google.javascript.rhino.head.ErrorReporter;
/*   0*/import com.google.javascript.rhino.head.EvaluatorException;
/*   0*/import junit.framework.Assert;
/*   0*/
/*   0*/public final class TestErrorReporter extends Assert implements ErrorReporter {
/*   0*/  private final String[] errors;
/*   0*/  
/*   0*/  private final String[] warnings;
/*   0*/  
/*  40*/  private int errorsIndex = 0;
/*   0*/  
/*  41*/  private int warningsIndex = 0;
/*   0*/  
/*   0*/  public TestErrorReporter(String[] errors, String[] warnings) {
/*  44*/    this.errors = errors;
/*  45*/    this.warnings = warnings;
/*   0*/  }
/*   0*/  
/*   0*/  public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
/*  51*/    if (this.errors != null && this.errorsIndex < this.errors.length) {
/*  52*/      assertEquals(this.errors[this.errorsIndex++], message);
/*   0*/    } else {
/*  54*/      fail("extra error: " + message);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
/*  61*/    if (this.warnings != null && this.warningsIndex < this.warnings.length) {
/*  62*/      assertEquals(this.warnings[this.warningsIndex++], message);
/*   0*/    } else {
/*  64*/      fail("extra warning: " + message);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
/*  71*/    return new EvaluatorException("JSCompiler test code: " + message);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasEncounteredAllWarnings() {
/*   0*/    while (true)
/*  78*/      return (this.warnings == null) ? ((this.warningsIndex == 0)) : true; 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasEncounteredAllErrors() {
/*  87*/    return (this.errors == null) ? ((this.errorsIndex == 0)) : ((this.errors.length == this.errorsIndex));
/*   0*/  }
/*   0*/}
