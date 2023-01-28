/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/
/*   0*/abstract class CodeConsumer {
/*   0*/  boolean statementNeedsEnded = false;
/*   0*/  
/*   0*/  boolean statementStarted = false;
/*   0*/  
/*   0*/  boolean sawFunction = false;
/*   0*/  
/*   0*/  void startSourceMapping(Node node) {}
/*   0*/  
/*   0*/  void endSourceMapping(Node node) {}
/*   0*/  
/*   0*/  boolean continueProcessing() {
/*  53*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  abstract char getLastChar();
/*   0*/  
/*   0*/  void addIdentifier(String identifier) {
/*  62*/    add(identifier);
/*   0*/  }
/*   0*/  
/*   0*/  abstract void append(String paramString);
/*   0*/  
/*   0*/  void appendBlockStart() {
/*  77*/    append("{");
/*   0*/  }
/*   0*/  
/*   0*/  void appendBlockEnd() {
/*  81*/    append("}");
/*   0*/  }
/*   0*/  
/*   0*/  void startNewLine() {}
/*   0*/  
/*   0*/  void maybeLineBreak() {
/*  88*/    maybeCutLine();
/*   0*/  }
/*   0*/  
/*   0*/  void maybeCutLine() {}
/*   0*/  
/*   0*/  void endLine() {}
/*   0*/  
/*   0*/  void notePreferredLineBreak() {}
/*   0*/  
/*   0*/  void beginBlock() {
/* 101*/    if (this.statementNeedsEnded) {
/* 102*/      append(";");
/* 103*/      maybeLineBreak();
/*   0*/    } 
/* 105*/    appendBlockStart();
/* 107*/    endLine();
/* 108*/    this.statementNeedsEnded = false;
/*   0*/  }
/*   0*/  
/*   0*/  void endBlock() {
/* 112*/    endBlock(false);
/*   0*/  }
/*   0*/  
/*   0*/  void endBlock(boolean shouldEndLine) {
/* 116*/    appendBlockEnd();
/* 117*/    if (shouldEndLine)
/* 118*/      endLine(); 
/* 120*/    this.statementNeedsEnded = false;
/*   0*/  }
/*   0*/  
/*   0*/  void listSeparator() {
/* 124*/    add(",");
/* 125*/    maybeLineBreak();
/*   0*/  }
/*   0*/  
/*   0*/  void endStatement() {
/* 135*/    endStatement(false);
/*   0*/  }
/*   0*/  
/*   0*/  void endStatement(boolean needSemiColon) {
/* 139*/    if (needSemiColon) {
/* 140*/      append(";");
/* 141*/      maybeLineBreak();
/* 142*/      this.statementNeedsEnded = false;
/* 143*/    } else if (this.statementStarted) {
/* 144*/      this.statementNeedsEnded = true;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void maybeEndStatement() {
/* 154*/    if (this.statementNeedsEnded) {
/* 155*/      append(";");
/* 156*/      maybeLineBreak();
/* 157*/      endLine();
/* 158*/      this.statementNeedsEnded = false;
/*   0*/    } 
/* 160*/    this.statementStarted = true;
/*   0*/  }
/*   0*/  
/*   0*/  void endFunction() {
/* 164*/    endFunction(false);
/*   0*/  }
/*   0*/  
/*   0*/  void endFunction(boolean statementContext) {
/* 168*/    this.sawFunction = true;
/* 169*/    if (statementContext)
/* 170*/      endLine(); 
/*   0*/  }
/*   0*/  
/*   0*/  void beginCaseBody() {
/* 175*/    append(":");
/*   0*/  }
/*   0*/  
/*   0*/  void endCaseBody() {}
/*   0*/  
/*   0*/  void add(String newcode) {
/* 182*/    maybeEndStatement();
/* 184*/    if (newcode.length() == 0)
/*   0*/      return; 
/* 188*/    char c = newcode.charAt(0);
/* 189*/    if ((isWordChar(c) || c == '\\') && isWordChar(getLastChar())) {
/* 193*/      append(" ");
/* 194*/    } else if (c == '/' && getLastChar() == '/') {
/* 200*/      append(" ");
/*   0*/    } 
/* 203*/    append(newcode);
/*   0*/  }
/*   0*/  
/*   0*/  void appendOp(String op, boolean binOp) {
/* 207*/    append(op);
/*   0*/  }
/*   0*/  
/*   0*/  void addOp(String op, boolean binOp) {
/* 211*/    maybeEndStatement();
/* 213*/    char first = op.charAt(0);
/* 214*/    char prev = getLastChar();
/* 216*/    if ((first == '+' || first == '-') && prev == first) {
/* 219*/      append(" ");
/* 220*/    } else if (Character.isLetter(first) && isWordChar(prev)) {
/* 223*/      append(" ");
/* 224*/    } else if (prev == '-' && first == '>') {
/* 226*/      append(" ");
/*   0*/    } 
/* 230*/    appendOp(op, binOp);
/* 235*/    if (binOp)
/* 236*/      maybeCutLine(); 
/*   0*/  }
/*   0*/  
/*   0*/  void addNumber(double x) {
/* 243*/    char prev = getLastChar();
/* 244*/    boolean negativeZero = isNegativeZero(x);
/* 245*/    x cmp 0.0D;
/* 245*/    if (prev == '-')
/* 246*/      add(" "); 
/* 249*/    if ((long)x == x && !negativeZero) {
/* 250*/      long value = (long)x;
/* 251*/      long mantissa = value;
/* 252*/      int exp = 0;
/* 253*/      if (Math.abs(x) >= 100.0D)
/* 254*/        while ((mantissa / 10L) * Math.pow(10.0D, (exp + 1)) == value) {
/* 255*/          mantissa /= 10L;
/* 256*/          exp++;
/*   0*/        }  
/* 259*/      if (exp > 2) {
/* 260*/        add(Long.toString(mantissa) + "E" + Integer.toString(exp));
/*   0*/      } else {
/* 262*/        add(Long.toString(value));
/*   0*/      } 
/*   0*/    } else {
/* 265*/      add(String.valueOf(x));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNegativeZero(double x) {
/* 270*/    return (x == 0.0D && Math.copySign(1.0D, x) == -1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isWordChar(char ch) {
/* 274*/    return (ch == '_' || ch == '$' || Character.isLetterOrDigit(ch));
/*   0*/  }
/*   0*/  
/*   0*/  boolean shouldPreserveExtraBlocks() {
/* 288*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean breakAfterBlockFor(Node n, boolean statementContext) {
/* 295*/    return statementContext;
/*   0*/  }
/*   0*/  
/*   0*/  void endFile() {}
/*   0*/}
