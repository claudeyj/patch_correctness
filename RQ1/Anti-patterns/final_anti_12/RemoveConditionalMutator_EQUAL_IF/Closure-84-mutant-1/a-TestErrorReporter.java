/*   0*/package com.google.javascript.jscomp.testing;
/*   0*/
/*   0*/import com.google.javascript.jscomp.mozilla.rhino.ErrorReporter;
/*   0*/import com.google.javascript.jscomp.mozilla.rhino.EvaluatorException;
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
/*  50*/    if (this.errors != null && this.errorsIndex < this.errors.length) {
/*  51*/      assertEquals(this.errors[this.errorsIndex++], message);
/*   0*/    } else {
/*  53*/      fail("extra error: " + message);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
/*  59*/    if (this.warnings != null && this.warningsIndex < this.warnings.length) {
/*  60*/      assertEquals(this.warnings[this.warningsIndex++], message);
/*   0*/    } else {
/*  62*/      fail("extra warning: " + message);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
/*  68*/    return new EvaluatorException("JSCompiler test code: " + message);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasEncounteredAllWarnings() {
/*  75*/    return (this.warnings == null) ? ((this.warningsIndex == 0)) : ((this.warnings.length == this.warningsIndex));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasEncounteredAllErrors() {
/*  84*/    return (this.errors == null) ? ((this.errorsIndex == 0)) : ((this.errors.length == this.errorsIndex));
/*   0*/  }
/*   0*/}
