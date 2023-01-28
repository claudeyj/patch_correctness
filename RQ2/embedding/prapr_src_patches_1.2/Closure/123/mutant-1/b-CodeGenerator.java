/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Charsets;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.TokenStream;
/*   0*/import java.io.IOException;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.nio.charset.CharsetEncoder;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/class CodeGenerator {
/*   0*/  private static final String LT_ESCAPED = "\\x3c";
/*   0*/  
/*   0*/  private static final String GT_ESCAPED = "\\x3e";
/*   0*/  
/*  42*/  private final Map<String, String> escapedJsStrings = Maps.newHashMap();
/*   0*/  
/*  44*/  private static final char[] HEX_CHARS = new char[] { 
/*  44*/      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
/*  44*/      'a', 'b', 'c', 'd', 'e', 'f' };
/*   0*/  
/*   0*/  private final CodeConsumer cc;
/*   0*/  
/*   0*/  private final CharsetEncoder outputCharsetEncoder;
/*   0*/  
/*   0*/  private final boolean preferSingleQuotes;
/*   0*/  
/*   0*/  private final boolean trustedStrings;
/*   0*/  
/*   0*/  private final CompilerOptions.LanguageMode languageMode;
/*   0*/  
/*   0*/  private CodeGenerator(CodeConsumer consumer) {
/*  57*/    this.cc = consumer;
/*  58*/    this.outputCharsetEncoder = null;
/*  59*/    this.preferSingleQuotes = false;
/*  60*/    this.trustedStrings = true;
/*  61*/    this.languageMode = CompilerOptions.LanguageMode.ECMASCRIPT5;
/*   0*/  }
/*   0*/  
/*   0*/  static CodeGenerator forCostEstimation(CodeConsumer consumer) {
/*  65*/    return new CodeGenerator(consumer);
/*   0*/  }
/*   0*/  
/*   0*/  CodeGenerator(CodeConsumer consumer, CompilerOptions options) {
/*  71*/    this.cc = consumer;
/*  73*/    Charset outputCharset = options.getOutputCharset();
/*  74*/    if (outputCharset == null || outputCharset == Charsets.US_ASCII) {
/*  79*/      this.outputCharsetEncoder = null;
/*   0*/    } else {
/*  81*/      this.outputCharsetEncoder = outputCharset.newEncoder();
/*   0*/    } 
/*  83*/    this.preferSingleQuotes = options.preferSingleQuotes;
/*  84*/    this.trustedStrings = options.trustedStrings;
/*  85*/    this.languageMode = options.getLanguageOut();
/*   0*/  }
/*   0*/  
/*   0*/  public void tagAsStrict() {
/*  92*/    add("'use strict';");
/*   0*/  }
/*   0*/  
/*   0*/  void add(String str) {
/*  96*/    this.cc.add(str);
/*   0*/  }
/*   0*/  
/*   0*/  private void addIdentifier(String identifier) {
/* 100*/    this.cc.addIdentifier(identifierEscape(identifier));
/*   0*/  }
/*   0*/  
/*   0*/  void add(Node n) {
/* 104*/    add(n, Context.OTHER);
/*   0*/  }
/*   0*/  
/*   0*/  void add(Node n, Context context) {
/*   0*/    Node catchblock;
/*   0*/    int p;
/*   0*/    String regexp;
/*   0*/    Context rhsContext;
/*   0*/    boolean funcNeedsParens;
/*   0*/    String name;
/*   0*/    Node fn, parameters, body;
/*   0*/    boolean preserveBlock, needsParens;
/*   0*/    String o;
/*   0*/    boolean hasElse, preferLineBreaks;
/*   0*/    int postProp;
/*   0*/    boolean ambiguousElseClause;
/*   0*/    Node c;
/*   0*/    int precedence;
/*   0*/    Node next;
/*   0*/    boolean bool1;
/*   0*/    Node node1;
/* 108*/    if (!this.cc.continueProcessing())
/*   0*/      return; 
/* 112*/    int type = n.getType();
/* 113*/    String opstr = NodeUtil.opToStr(type);
/* 114*/    int childCount = n.getChildCount();
/* 115*/    Node first = n.getFirstChild();
/* 116*/    Node last = n.getLastChild();
/* 119*/    if (opstr != null && first != last) {
/* 120*/      Preconditions.checkState((childCount == 2), "Bad binary operator \"%s\": expected 2 arguments but got %s", new Object[] { opstr, childCount });
/* 124*/      int i = NodeUtil.precedence(type);
/* 128*/      Context context1 = getContextForNoInOperator(context);
/* 133*/      if (last.getType() == type && NodeUtil.isAssociative(type)) {
/* 135*/        addExpr(first, i, context);
/* 136*/        this.cc.addOp(opstr, true);
/* 137*/        addExpr(last, i, context1);
/* 138*/      } else if (NodeUtil.isAssignmentOp(n) && NodeUtil.isAssignmentOp(last)) {
/* 140*/        addExpr(first, i, context);
/* 141*/        this.cc.addOp(opstr, true);
/* 142*/        addExpr(last, i, context1);
/*   0*/      } else {
/* 144*/        unrollBinaryOperator(n, type, opstr, context, context1, i, i + 1);
/*   0*/      } 
/*   0*/      return;
/*   0*/    } 
/* 149*/    this.cc.startSourceMapping(n);
/* 151*/    switch (type) {
/*   0*/      case 77:
/* 153*/        Preconditions.checkState((first.getNext().isBlock() && !first.getNext().hasMoreThanOneChild()));
/* 155*/        Preconditions.checkState((childCount >= 2 && childCount <= 3));
/* 157*/        add("try");
/* 158*/        add(first, Context.PRESERVE_BLOCK);
/* 162*/        catchblock = first.getNext().getFirstChild();
/* 163*/        if (catchblock != null)
/* 164*/          add(catchblock); 
/* 167*/        if (childCount == 3) {
/* 168*/          add("finally");
/* 169*/          add(last, Context.PRESERVE_BLOCK);
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 120:
/* 175*/        Preconditions.checkState((childCount == 2));
/* 176*/        add("catch(");
/* 177*/        add(first);
/* 178*/        add(")");
/* 179*/        add(last, Context.PRESERVE_BLOCK);
/*   0*/        break;
/*   0*/      case 49:
/* 183*/        Preconditions.checkState((childCount == 1));
/* 184*/        add("throw");
/* 185*/        add(first);
/* 189*/        this.cc.endStatement(true);
/*   0*/        break;
/*   0*/      case 4:
/* 193*/        add("return");
/* 194*/        if (childCount == 1) {
/* 195*/          add(first);
/*   0*/        } else {
/* 197*/          Preconditions.checkState((childCount == 0));
/*   0*/        } 
/* 199*/        this.cc.endStatement();
/*   0*/        break;
/*   0*/      case 118:
/* 203*/        if (first != null) {
/* 204*/          add("var ");
/* 205*/          addList(first, false, getContextForNoInOperator(context));
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 153:
/* 210*/        Preconditions.checkState(!n.getString().isEmpty());
/* 211*/        addIdentifier(n.getString());
/*   0*/        break;
/*   0*/      case 38:
/* 215*/        if (first == null || first.isEmpty()) {
/* 216*/          addIdentifier(n.getString());
/*   0*/          break;
/*   0*/        } 
/* 218*/        Preconditions.checkState((childCount == 1));
/* 219*/        addIdentifier(n.getString());
/* 220*/        this.cc.addOp("=", true);
/* 221*/        if (first.isComma()) {
/* 222*/          addExpr(first, NodeUtil.precedence(86), Context.OTHER);
/*   0*/          break;
/*   0*/        } 
/* 226*/        addExpr(first, 0, getContextForNoInOperator(context));
/*   0*/        break;
/*   0*/      case 63:
/* 232*/        add("[");
/* 233*/        addArrayList(first);
/* 234*/        add("]");
/*   0*/        break;
/*   0*/      case 83:
/* 238*/        add("(");
/* 239*/        addList(first);
/* 240*/        add(")");
/*   0*/        break;
/*   0*/      case 85:
/* 244*/        Preconditions.checkState((childCount == 2));
/* 245*/        unrollBinaryOperator(n, 85, ",", context, getContextForNoInOperator(context), 0, 0);
/*   0*/        break;
/*   0*/      case 39:
/* 250*/        Preconditions.checkState((childCount == 0));
/* 251*/        this.cc.addNumber(n.getDouble());
/*   0*/        break;
/*   0*/      case 26:
/*   0*/      case 27:
/*   0*/      case 28:
/*   0*/      case 32:
/*   0*/      case 122:
/* 260*/        Preconditions.checkState((childCount == 1));
/* 261*/        this.cc.addOp(NodeUtil.opToStrNoFail(type), false);
/* 262*/        addExpr(first, NodeUtil.precedence(type), Context.OTHER);
/*   0*/        break;
/*   0*/      case 29:
/* 267*/        Preconditions.checkState((childCount == 1));
/* 272*/        if (n.getFirstChild().isNumber()) {
/* 273*/          this.cc.addNumber(-n.getFirstChild().getDouble());
/*   0*/          break;
/*   0*/        } 
/* 275*/        this.cc.addOp(NodeUtil.opToStrNoFail(type), false);
/* 276*/        addExpr(first, NodeUtil.precedence(type), Context.OTHER);
/*   0*/        break;
/*   0*/      case 98:
/* 283*/        Preconditions.checkState((childCount == 3));
/* 284*/        p = NodeUtil.precedence(type);
/* 285*/        rhsContext = Context.IN_FOR_INIT_CLAUSE;
/* 286*/        addExpr(first, p + 1, context);
/* 287*/        this.cc.addOp("?", true);
/* 288*/        addExpr(first.getNext(), 1, rhsContext);
/* 289*/        this.cc.addOp(":", true);
/* 290*/        addExpr(last, 1, rhsContext);
/*   0*/        break;
/*   0*/      case 47:
/* 295*/        if (!first.isString() || !last.isString())
/* 297*/          throw new Error("Expected children to be strings"); 
/* 300*/        regexp = regexpEscape(first.getString(), this.outputCharsetEncoder);
/* 303*/        if (childCount == 2) {
/* 304*/          add(regexp + last.getString());
/*   0*/          break;
/*   0*/        } 
/* 306*/        Preconditions.checkState((childCount == 1));
/* 307*/        add(regexp);
/*   0*/        break;
/*   0*/      case 105:
/* 312*/        if (n.getClass() != Node.class)
/* 313*/          throw new Error("Unexpected Node subclass."); 
/* 315*/        Preconditions.checkState((childCount == 3));
/* 316*/        funcNeedsParens = (context == Context.START_OF_EXPR);
/* 317*/        if (funcNeedsParens)
/* 318*/          add("("); 
/* 321*/        add("function");
/* 322*/        add(first);
/* 324*/        add(first.getNext());
/* 325*/        add(last, Context.PRESERVE_BLOCK);
/* 326*/        this.cc.endFunction((context == Context.STATEMENT));
/* 328*/        if (funcNeedsParens)
/* 329*/          add(")"); 
/*   0*/        break;
/*   0*/      case 147:
/*   0*/      case 148:
/* 335*/        Preconditions.checkState(n.getParent().isObjectLit());
/* 336*/        Preconditions.checkState((childCount == 1));
/* 337*/        Preconditions.checkState(first.isFunction());
/* 340*/        Preconditions.checkState(first.getFirstChild().getString().isEmpty());
/* 341*/        if (type == 147) {
/* 343*/          Preconditions.checkState(!first.getChildAtIndex(1).hasChildren());
/* 344*/          add("get ");
/*   0*/        } else {
/* 347*/          Preconditions.checkState(first.getChildAtIndex(1).hasOneChild());
/* 348*/          add("set ");
/*   0*/        } 
/* 352*/        name = n.getString();
/* 353*/        fn = first;
/* 354*/        parameters = fn.getChildAtIndex(1);
/* 355*/        body = fn.getLastChild();
/* 358*/        if (!n.isQuotedString() && TokenStream.isJSIdentifier(name) && NodeUtil.isLatin(name)) {
/* 363*/          add(name);
/*   0*/        } else {
/* 366*/          double d = getSimpleNumber(name);
/* 367*/          if (!Double.isNaN(d)) {
/* 368*/            this.cc.addNumber(d);
/*   0*/          } else {
/* 370*/            addJsString(n);
/*   0*/          } 
/*   0*/        } 
/* 374*/        add(parameters);
/* 375*/        add(body, Context.PRESERVE_BLOCK);
/*   0*/        break;
/*   0*/      case 125:
/*   0*/      case 132:
/* 380*/        if (n.getClass() != Node.class)
/* 381*/          throw new Error("Unexpected Node subclass."); 
/* 383*/        preserveBlock = (context == Context.PRESERVE_BLOCK);
/* 384*/        if (preserveBlock)
/* 385*/          this.cc.beginBlock(); 
/* 388*/        preferLineBreaks = (type == 132 || (type == 125 && !preserveBlock && n.getParent() != null && n.getParent().isScript()));
/* 394*/        for (c = first; c != null; c = c.getNext()) {
/* 395*/          add(c, Context.STATEMENT);
/* 398*/          if (c.isVar())
/* 399*/            this.cc.endStatement(); 
/* 402*/          if (c.isFunction())
/* 403*/            this.cc.maybeLineBreak(); 
/* 408*/          if (preferLineBreaks)
/* 409*/            this.cc.notePreferredLineBreak(); 
/*   0*/        } 
/* 412*/        if (preserveBlock)
/* 413*/          this.cc.endBlock(this.cc.breakAfterBlockFor(n, (context == Context.STATEMENT))); 
/*   0*/        break;
/*   0*/      case 115:
/* 419*/        if (childCount == 4) {
/* 420*/          add("for(");
/* 421*/          if (first.isVar()) {
/* 422*/            add(first, Context.IN_FOR_INIT_CLAUSE);
/*   0*/          } else {
/* 424*/            addExpr(first, 0, Context.IN_FOR_INIT_CLAUSE);
/*   0*/          } 
/* 426*/          add(";");
/* 427*/          add(first.getNext());
/* 428*/          add(";");
/* 429*/          add(first.getNext().getNext());
/* 430*/          add(")");
/* 431*/          addNonEmptyStatement(last, getContextForNonEmptyExpression(context), false);
/*   0*/          break;
/*   0*/        } 
/* 434*/        Preconditions.checkState((childCount == 3));
/* 435*/        add("for(");
/* 436*/        add(first);
/* 437*/        add("in");
/* 438*/        add(first.getNext());
/* 439*/        add(")");
/* 440*/        addNonEmptyStatement(last, getContextForNonEmptyExpression(context), false);
/*   0*/        break;
/*   0*/      case 114:
/* 446*/        Preconditions.checkState((childCount == 2));
/* 447*/        add("do");
/* 448*/        addNonEmptyStatement(first, Context.OTHER, false);
/* 449*/        add("while(");
/* 450*/        add(last);
/* 451*/        add(")");
/* 452*/        this.cc.endStatement();
/*   0*/        break;
/*   0*/      case 113:
/* 456*/        Preconditions.checkState((childCount == 2));
/* 457*/        add("while(");
/* 458*/        add(first);
/* 459*/        add(")");
/* 460*/        addNonEmptyStatement(last, getContextForNonEmptyExpression(context), false);
/*   0*/        break;
/*   0*/      case 124:
/* 465*/        Preconditions.checkState((childCount == 0));
/*   0*/        break;
/*   0*/      case 33:
/* 469*/        Preconditions.checkState((childCount == 2), "Bad GETPROP: expected 2 children, but got %s", new Object[] { childCount });
/* 472*/        Preconditions.checkState(last.isString(), "Bad GETPROP: RHS should be STRING");
/* 475*/        needsParens = first.isNumber();
/* 476*/        if (needsParens)
/* 477*/          add("("); 
/* 479*/        addExpr(first, NodeUtil.precedence(type), context);
/* 480*/        if (needsParens)
/* 481*/          add(")"); 
/* 483*/        if (this.languageMode == CompilerOptions.LanguageMode.ECMASCRIPT3 && TokenStream.isKeyword(last.getString())) {
/* 486*/          add("[");
/* 487*/          add(last);
/* 488*/          add("]");
/*   0*/          break;
/*   0*/        } 
/* 490*/        add(".");
/* 491*/        addIdentifier(last.getString());
/*   0*/        break;
/*   0*/      case 35:
/* 497*/        Preconditions.checkState((childCount == 2), "Bad GETELEM: expected 2 children but got %s", new Object[] { childCount });
/* 500*/        addExpr(first, NodeUtil.precedence(type), context);
/* 501*/        add("[");
/* 502*/        add(first.getNext());
/* 503*/        add("]");
/*   0*/        break;
/*   0*/      case 119:
/* 507*/        Preconditions.checkState((childCount == 2));
/* 508*/        add("with(");
/* 509*/        add(first);
/* 510*/        add(")");
/* 511*/        addNonEmptyStatement(last, getContextForNonEmptyExpression(context), false);
/*   0*/        break;
/*   0*/      case 102:
/*   0*/      case 103:
/* 517*/        Preconditions.checkState((childCount == 1));
/* 518*/        o = (type == 102) ? "++" : "--";
/* 519*/        postProp = n.getIntProp(32);
/* 522*/        if (postProp != 0) {
/* 523*/          addExpr(first, NodeUtil.precedence(type), context);
/* 524*/          this.cc.addOp(o, false);
/*   0*/          break;
/*   0*/        } 
/* 526*/        this.cc.addOp(o, false);
/* 527*/        add(first);
/*   0*/        break;
/*   0*/      case 37:
/* 542*/        if (isIndirectEval(first) || (n.getBooleanProp(50) && NodeUtil.isGet(first))) {
/* 544*/          add("(0,");
/* 545*/          addExpr(first, NodeUtil.precedence(85), Context.OTHER);
/* 546*/          add(")");
/*   0*/        } else {
/* 548*/          addExpr(first, NodeUtil.precedence(type), context);
/*   0*/        } 
/* 550*/        add("(");
/* 551*/        addList(first.getNext());
/* 552*/        add(")");
/*   0*/        break;
/*   0*/      case 108:
/* 556*/        hasElse = (childCount == 3);
/* 557*/        ambiguousElseClause = (context == Context.BEFORE_DANGLING_ELSE && !hasElse);
/* 559*/        if (ambiguousElseClause)
/* 560*/          this.cc.beginBlock(); 
/* 563*/        add("if(");
/* 564*/        add(first);
/* 565*/        add(")");
/* 567*/        if (hasElse) {
/* 568*/          addNonEmptyStatement(first.getNext(), Context.BEFORE_DANGLING_ELSE, false);
/* 570*/          add("else");
/* 571*/          addNonEmptyStatement(last, getContextForNonEmptyExpression(context), false);
/*   0*/        } else {
/* 574*/          addNonEmptyStatement(first.getNext(), Context.OTHER, false);
/* 575*/          Preconditions.checkState((childCount == 2));
/*   0*/        } 
/* 578*/        if (ambiguousElseClause)
/* 579*/          this.cc.endBlock(); 
/*   0*/        break;
/*   0*/      case 41:
/* 584*/        Preconditions.checkState((childCount == 0));
/* 585*/        this.cc.addConstant("null");
/*   0*/        break;
/*   0*/      case 42:
/* 589*/        Preconditions.checkState((childCount == 0));
/* 590*/        add("this");
/*   0*/        break;
/*   0*/      case 43:
/* 594*/        Preconditions.checkState((childCount == 0));
/* 595*/        this.cc.addConstant("false");
/*   0*/        break;
/*   0*/      case 44:
/* 599*/        Preconditions.checkState((childCount == 0));
/* 600*/        this.cc.addConstant("true");
/*   0*/        break;
/*   0*/      case 117:
/* 604*/        Preconditions.checkState((childCount <= 1));
/* 605*/        add("continue");
/* 606*/        if (childCount == 1) {
/* 607*/          if (!first.isLabelName())
/* 608*/            throw new Error("Unexpected token type. Should be LABEL_NAME."); 
/* 610*/          add(" ");
/* 611*/          add(first);
/*   0*/        } 
/* 613*/        this.cc.endStatement();
/*   0*/        break;
/*   0*/      case 152:
/* 617*/        Preconditions.checkState((childCount == 0));
/* 618*/        add("debugger");
/* 619*/        this.cc.endStatement();
/*   0*/        break;
/*   0*/      case 116:
/* 623*/        Preconditions.checkState((childCount <= 1));
/* 624*/        add("break");
/* 625*/        if (childCount == 1) {
/* 626*/          if (!first.isLabelName())
/* 627*/            throw new Error("Unexpected token type. Should be LABEL_NAME."); 
/* 629*/          add(" ");
/* 630*/          add(first);
/*   0*/        } 
/* 632*/        this.cc.endStatement();
/*   0*/        break;
/*   0*/      case 130:
/* 636*/        Preconditions.checkState((childCount == 1));
/* 637*/        add(first, Context.START_OF_EXPR);
/* 638*/        this.cc.endStatement();
/*   0*/        break;
/*   0*/      case 30:
/* 642*/        add("new ");
/* 643*/        precedence = NodeUtil.precedence(type);
/* 648*/        if (NodeUtil.containsType(first, 37, NodeUtil.MATCH_NOT_FUNCTION))
/* 650*/          precedence = NodeUtil.precedence(first.getType()) + 1; 
/* 652*/        addExpr(first, precedence, Context.OTHER);
/* 655*/        next = first.getNext();
/* 656*/        if (next != null) {
/* 657*/          add("(");
/* 658*/          addList(next);
/* 659*/          add(")");
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 154:
/* 664*/        Preconditions.checkState((childCount == 1), "Object lit key must have 1 child");
/* 666*/        addJsString(n);
/*   0*/        break;
/*   0*/      case 40:
/* 670*/        Preconditions.checkState((childCount == 0), "A string may not have children");
/* 672*/        addJsString(n);
/*   0*/        break;
/*   0*/      case 31:
/* 676*/        Preconditions.checkState((childCount == 1));
/* 677*/        add("delete ");
/* 678*/        add(first);
/*   0*/        break;
/*   0*/      case 64:
/* 682*/        bool1 = (context == Context.START_OF_EXPR);
/* 683*/        if (bool1)
/* 684*/          add("("); 
/* 686*/        add("{");
/* 687*/        for (node1 = first; node1 != null; node1 = node1.getNext()) {
/* 688*/          if (node1 != first)
/* 689*/            this.cc.listSeparator(); 
/* 692*/          if (node1.isGetterDef() || node1.isSetterDef()) {
/* 693*/            add(node1);
/*   0*/          } else {
/* 695*/            Preconditions.checkState(node1.isStringKey());
/* 696*/            String key = node1.getString();
/* 699*/            if (!node1.isQuotedString() && (this.languageMode != CompilerOptions.LanguageMode.ECMASCRIPT3 || !TokenStream.isKeyword(key)) && TokenStream.isJSIdentifier(key) && NodeUtil.isLatin(key)) {
/* 706*/              add(key);
/*   0*/            } else {
/* 709*/              double d = getSimpleNumber(key);
/* 710*/              if (!Double.isNaN(d)) {
/* 711*/                this.cc.addNumber(d);
/*   0*/              } else {
/* 713*/                addExpr(node1, 1, Context.OTHER);
/*   0*/              } 
/*   0*/            } 
/* 716*/            add(":");
/* 717*/            addExpr(node1.getFirstChild(), 1, Context.OTHER);
/*   0*/          } 
/*   0*/        } 
/* 720*/        add("}");
/* 721*/        if (bool1)
/* 722*/          add(")"); 
/*   0*/        break;
/*   0*/      case 110:
/* 728*/        add("switch(");
/* 729*/        add(first);
/* 730*/        add(")");
/* 731*/        this.cc.beginBlock();
/* 732*/        addAllSiblings(first.getNext());
/* 733*/        this.cc.endBlock((context == Context.STATEMENT));
/*   0*/        break;
/*   0*/      case 111:
/* 737*/        Preconditions.checkState((childCount == 2));
/* 738*/        add("case ");
/* 739*/        add(first);
/* 740*/        addCaseBody(last);
/*   0*/        break;
/*   0*/      case 112:
/* 744*/        Preconditions.checkState((childCount == 1));
/* 745*/        add("default");
/* 746*/        addCaseBody(first);
/*   0*/        break;
/*   0*/      case 126:
/* 750*/        Preconditions.checkState((childCount == 2));
/* 751*/        if (!first.isLabelName())
/* 752*/          throw new Error("Unexpected token type. Should be LABEL_NAME."); 
/* 754*/        add(first);
/* 755*/        add(":");
/* 756*/        addNonEmptyStatement(last, getContextForNonEmptyExpression(context), true);
/*   0*/        break;
/*   0*/      case 155:
/* 761*/        add("(");
/* 762*/        add(first);
/* 763*/        add(")");
/*   0*/        break;
/*   0*/      default:
/* 767*/        throw new Error("Unknown type " + type + "\n" + n.toStringTree());
/*   0*/    } 
/* 770*/    this.cc.endSourceMapping(n);
/*   0*/  }
/*   0*/  
/*   0*/  private void unrollBinaryOperator(Node n, int op, String opStr, Context context, Context rhsContext, int leftPrecedence, int rightPrecedence) {
/* 783*/    Node firstNonOperator = n.getFirstChild();
/* 784*/    while (firstNonOperator.getType() == op)
/* 785*/      firstNonOperator = firstNonOperator.getFirstChild(); 
/* 788*/    addExpr(firstNonOperator, leftPrecedence, context);
/* 790*/    Node current = firstNonOperator;
/*   0*/    do {
/* 792*/      current = current.getParent();
/* 793*/      this.cc.addOp(opStr, true);
/* 794*/      addExpr(current.getFirstChild().getNext(), rightPrecedence, rhsContext);
/* 795*/    } while (current != n);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSimpleNumber(String s) {
/* 799*/    int len = s.length();
/* 800*/    if (len == 0)
/* 801*/      return false; 
/* 803*/    for (int index = 0; index < len; index++) {
/* 804*/      char c = s.charAt(index);
/* 805*/      if (c < '0' || c > '9')
/* 806*/        return false; 
/*   0*/    } 
/* 809*/    return (len == 1 || s.charAt(0) != '0');
/*   0*/  }
/*   0*/  
/*   0*/  static double getSimpleNumber(String s) {
/* 813*/    if (isSimpleNumber(s))
/*   0*/      try {
/* 815*/        long l = Long.parseLong(s);
/* 816*/        if (l < NodeUtil.MAX_POSITIVE_INTEGER_NUMBER)
/* 817*/          return l; 
/* 819*/      } catch (NumberFormatException numberFormatException) {} 
/* 823*/    return Double.NaN;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isIndirectEval(Node n) {
/* 830*/    return (n.isName() && "eval".equals(n.getString()) && !n.getBooleanProp(49));
/*   0*/  }
/*   0*/  
/*   0*/  private void addNonEmptyStatement(Node n, Context context, boolean allowNonBlockChild) {
/* 843*/    Node nodeToProcess = n;
/* 845*/    if (!allowNonBlockChild && !n.isBlock())
/* 846*/      throw new Error("Missing BLOCK child."); 
/* 851*/    if (n.isBlock()) {
/* 852*/      int count = getNonEmptyChildCount(n, 2);
/* 853*/      if (count == 0) {
/* 854*/        if (this.cc.shouldPreserveExtraBlocks()) {
/* 855*/          this.cc.beginBlock();
/* 856*/          this.cc.endBlock(this.cc.breakAfterBlockFor(n, (context == Context.STATEMENT)));
/*   0*/        } else {
/* 858*/          this.cc.endStatement(true);
/*   0*/        } 
/*   0*/        return;
/*   0*/      } 
/* 863*/      if (count == 1) {
/* 867*/        Node firstAndOnlyChild = getFirstNonEmptyChild(n);
/* 868*/        boolean alwaysWrapInBlock = this.cc.shouldPreserveExtraBlocks();
/* 869*/        if (alwaysWrapInBlock || isOneExactlyFunctionOrDo(firstAndOnlyChild)) {
/* 870*/          this.cc.beginBlock();
/* 871*/          add(firstAndOnlyChild, Context.STATEMENT);
/* 872*/          this.cc.maybeLineBreak();
/* 873*/          this.cc.endBlock(this.cc.breakAfterBlockFor(n, (context == Context.STATEMENT)));
/*   0*/          return;
/*   0*/        } 
/* 877*/        nodeToProcess = firstAndOnlyChild;
/*   0*/      } 
/* 881*/      if (count > 1)
/* 882*/        context = Context.PRESERVE_BLOCK; 
/*   0*/    } 
/* 886*/    if (nodeToProcess.isEmpty()) {
/* 887*/      this.cc.endStatement(true);
/*   0*/    } else {
/* 889*/      add(nodeToProcess, context);
/* 893*/      if (nodeToProcess.isVar())
/* 894*/        this.cc.endStatement(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isOneExactlyFunctionOrDo(Node n) {
/* 904*/    if (n.isLabel()) {
/* 905*/      Node labeledStatement = n.getLastChild();
/* 906*/      if (!labeledStatement.isBlock())
/* 907*/        return isOneExactlyFunctionOrDo(labeledStatement); 
/* 912*/      if (getNonEmptyChildCount(n, 2) == 1)
/* 913*/        return isOneExactlyFunctionOrDo(getFirstNonEmptyChild(n)); 
/* 917*/      return false;
/*   0*/    } 
/* 921*/    return (n.isFunction() || n.isDo());
/*   0*/  }
/*   0*/  
/*   0*/  private void addExpr(Node n, int minPrecedence, Context context) {
/* 926*/    if (NodeUtil.precedence(n.getType()) < minPrecedence || (context == Context.IN_FOR_INIT_CLAUSE && n.isIn())) {
/* 928*/      add("(");
/* 929*/      add(n, Context.OTHER);
/* 930*/      add(")");
/*   0*/    } else {
/* 932*/      add(n, context);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void addList(Node firstInList) {
/* 937*/    addList(firstInList, true, Context.OTHER);
/*   0*/  }
/*   0*/  
/*   0*/  void addList(Node firstInList, boolean isArrayOrFunctionArgument) {
/* 941*/    addList(firstInList, isArrayOrFunctionArgument, Context.OTHER);
/*   0*/  }
/*   0*/  
/*   0*/  void addList(Node firstInList, boolean isArrayOrFunctionArgument, Context lhsContext) {
/* 946*/    for (Node n = firstInList; n != null; n = n.getNext()) {
/* 947*/      boolean isFirst = (n == firstInList);
/* 948*/      if (isFirst) {
/* 949*/        addExpr(n, isArrayOrFunctionArgument ? 1 : 0, lhsContext);
/*   0*/      } else {
/* 951*/        this.cc.listSeparator();
/* 952*/        addExpr(n, isArrayOrFunctionArgument ? 1 : 0, getContextForNoInOperator(lhsContext));
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void addArrayList(Node firstInList) {
/*   0*/    boolean lastWasEmpty = false;
/* 968*/    for (Node n = firstInList; n != null; n = n.getNext()) {
/* 969*/      if (n != firstInList)
/* 970*/        this.cc.listSeparator(); 
/* 972*/      addExpr(n, 1, Context.OTHER);
/* 973*/      lastWasEmpty = n.isEmpty();
/*   0*/    } 
/* 976*/    if (lastWasEmpty)
/* 977*/      this.cc.listSeparator(); 
/*   0*/  }
/*   0*/  
/*   0*/  void addCaseBody(Node caseBody) {
/* 982*/    this.cc.beginCaseBody();
/* 983*/    add(caseBody);
/* 984*/    this.cc.endCaseBody();
/*   0*/  }
/*   0*/  
/*   0*/  void addAllSiblings(Node n) {
/* 988*/    for (Node c = n; c != null; c = c.getNext())
/* 989*/      add(c); 
/*   0*/  }
/*   0*/  
/*   0*/  private void addJsString(Node n) {
/* 995*/    String s = n.getString();
/* 996*/    boolean useSlashV = n.getBooleanProp(54);
/* 997*/    if (useSlashV) {
/* 998*/      add(jsString(n.getString(), useSlashV));
/*   0*/    } else {
/*1000*/      String cached = this.escapedJsStrings.get(s);
/*1001*/      if (cached == null) {
/*1002*/        cached = jsString(n.getString(), useSlashV);
/*1003*/        this.escapedJsStrings.put(s, cached);
/*   0*/      } 
/*1005*/      add(cached);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private String jsString(String s, boolean useSlashV) {
/*   0*/    String doublequote, singlequote;
/*   0*/    char quote;
/*1010*/    int singleq = 0, doubleq = 0;
/*1013*/    for (int i = 0; i < s.length(); i++) {
/*1014*/      switch (s.charAt(i)) {
/*   0*/        case '"':
/*1015*/          doubleq++;
/*   0*/          break;
/*   0*/        case '\'':
/*1016*/          singleq++;
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/*1022*/    if (this.preferSingleQuotes ? (singleq <= doubleq) : (singleq < doubleq)) {
/*1025*/      quote = '\'';
/*1026*/      doublequote = "\"";
/*1027*/      singlequote = "\\'";
/*   0*/    } else {
/*1030*/      quote = '"';
/*1031*/      doublequote = "\\\"";
/*1032*/      singlequote = "'";
/*   0*/    } 
/*1035*/    return strEscape(s, quote, doublequote, singlequote, "\\\\", this.outputCharsetEncoder, useSlashV, false);
/*   0*/  }
/*   0*/  
/*   0*/  String regexpEscape(String s, CharsetEncoder outputCharsetEncoder) {
/*1041*/    return strEscape(s, '/', "\"", "'", "\\", outputCharsetEncoder, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  String escapeToDoubleQuotedJsString(String s) {
/*1048*/    return strEscape(s, '"', "\\\"", "'", "\\\\", null, false, false);
/*   0*/  }
/*   0*/  
/*   0*/  String regexpEscape(String s) {
/*1055*/    return regexpEscape(s, null);
/*   0*/  }
/*   0*/  
/*   0*/  private String strEscape(String s, char quote, String doublequoteEscape, String singlequoteEscape, String backslashEscape, CharsetEncoder outputCharsetEncoder, boolean useSlashV, boolean isRegexp) {
/*1068*/    StringBuilder sb = new StringBuilder(s.length() + 2);
/*1069*/    sb.append(quote);
/*1070*/    for (int i = 0; i < s.length(); i++) {
/*   0*/      String endScript, startComment;
/*1071*/      char c = s.charAt(i);
/*1072*/      switch (c) {
/*   0*/        case '\000':
/*1073*/          sb.append("\\x00");
/*   0*/          break;
/*   0*/        case '\013':
/*1075*/          if (useSlashV) {
/*1076*/            sb.append("\\v");
/*   0*/            break;
/*   0*/          } 
/*1078*/          sb.append("\\x0B");
/*   0*/          break;
/*   0*/        case '\b':
/*1082*/          sb.append("\\b");
/*   0*/          break;
/*   0*/        case '\f':
/*1083*/          sb.append("\\f");
/*   0*/          break;
/*   0*/        case '\n':
/*1084*/          sb.append("\\n");
/*   0*/          break;
/*   0*/        case '\r':
/*1085*/          sb.append("\\r");
/*   0*/          break;
/*   0*/        case '\t':
/*1086*/          sb.append("\\t");
/*   0*/          break;
/*   0*/        case '\\':
/*1087*/          sb.append(backslashEscape);
/*   0*/          break;
/*   0*/        case '"':
/*1088*/          sb.append(doublequoteEscape);
/*   0*/          break;
/*   0*/        case '\'':
/*1089*/          sb.append(singlequoteEscape);
/*   0*/          break;
/*   0*/        case ' ':
/*1092*/          sb.append("\\u2028");
/*   0*/          break;
/*   0*/        case ' ':
/*1093*/          sb.append("\\u2029");
/*   0*/          break;
/*   0*/        case '=':
/*1097*/          if (this.trustedStrings || isRegexp) {
/*1098*/            sb.append(c);
/*   0*/            break;
/*   0*/          } 
/*1100*/          sb.append("\\x3d");
/*   0*/          break;
/*   0*/        case '&':
/*1105*/          if (this.trustedStrings || isRegexp) {
/*1106*/            sb.append(c);
/*   0*/            break;
/*   0*/          } 
/*1108*/          sb.append("\\x26");
/*   0*/          break;
/*   0*/        case '>':
/*1113*/          if (!this.trustedStrings && !isRegexp) {
/*1114*/            sb.append("\\x3e");
/*   0*/            break;
/*   0*/          } 
/*1123*/          if (i >= 2 && ((s.charAt(i - 1) == '-' && s.charAt(i - 2) == '-') || (s.charAt(i - 1) == ']' && s.charAt(i - 2) == ']'))) {
/*1126*/            sb.append("\\x3e");
/*   0*/            break;
/*   0*/          } 
/*1128*/          sb.append(c);
/*   0*/          break;
/*   0*/        case '<':
/*1132*/          if (!this.trustedStrings && !isRegexp) {
/*1133*/            sb.append("\\x3c");
/*   0*/            break;
/*   0*/          } 
/*1140*/          endScript = "/script";
/*1143*/          startComment = "!--";
/*1145*/          if (s.regionMatches(true, i + 1, "/script", 0, "/script".length())) {
/*1147*/            sb.append("\\x3c");
/*   0*/            break;
/*   0*/          } 
/*1148*/          if (s.regionMatches(false, i + 1, "!--", 0, "!--".length())) {
/*1150*/            sb.append("\\x3c");
/*   0*/            break;
/*   0*/          } 
/*1152*/          sb.append(c);
/*   0*/          break;
/*   0*/        default:
/*1158*/          if (outputCharsetEncoder != null) {
/*1159*/            if (outputCharsetEncoder.canEncode(c)) {
/*1160*/              sb.append(c);
/*   0*/              break;
/*   0*/            } 
/*1163*/            appendHexJavaScriptRepresentation(sb, c);
/*   0*/            break;
/*   0*/          } 
/*1169*/          if (c > '\037' && c < '\u007F') {
/*1170*/            sb.append(c);
/*   0*/            break;
/*   0*/          } 
/*1175*/          appendHexJavaScriptRepresentation(sb, c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/*1180*/    sb.append(quote);
/*1181*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  static String identifierEscape(String s) {
/*1186*/    if (NodeUtil.isLatin(s))
/*1187*/      return s; 
/*1191*/    StringBuilder sb = new StringBuilder();
/*1192*/    for (int i = 0; i < s.length(); i++) {
/*1193*/      char c = s.charAt(i);
/*1197*/      if (c > '\037' && c < '\u007F') {
/*1198*/        sb.append(c);
/*   0*/      } else {
/*1200*/        appendHexJavaScriptRepresentation(sb, c);
/*   0*/      } 
/*   0*/    } 
/*1203*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private static int getNonEmptyChildCount(Node n, int maxCount) {
/*1211*/    int i = 0;
/*1212*/    Node c = n.getFirstChild();
/*1213*/    for (; c != null && i < maxCount; c = c.getNext()) {
/*1214*/      if (c.isBlock()) {
/*1215*/        i += getNonEmptyChildCount(c, maxCount - i);
/*1216*/      } else if (!c.isEmpty()) {
/*1217*/        i++;
/*   0*/      } 
/*   0*/    } 
/*1220*/    return i;
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getFirstNonEmptyChild(Node n) {
/*1225*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/*1226*/      if (c.isBlock()) {
/*1227*/        Node result = getFirstNonEmptyChild(c);
/*1228*/        if (result != null)
/*1229*/          return result; 
/*1231*/      } else if (!c.isEmpty()) {
/*1232*/        return c;
/*   0*/      } 
/*   0*/    } 
/*1235*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  enum Context {
/*1242*/    STATEMENT, BEFORE_DANGLING_ELSE, START_OF_EXPR, PRESERVE_BLOCK, IN_FOR_INIT_CLAUSE, OTHER;
/*   0*/  }
/*   0*/  
/*   0*/  private Context getContextForNonEmptyExpression(Context currentContext) {
/*1254*/    return (currentContext == Context.BEFORE_DANGLING_ELSE) ? Context.BEFORE_DANGLING_ELSE : Context.OTHER;
/*   0*/  }
/*   0*/  
/*   0*/  private Context getContextForNoInOperator(Context context) {
/*1263*/    return (context == Context.IN_FOR_INIT_CLAUSE) ? Context.IN_FOR_INIT_CLAUSE : Context.OTHER;
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendHexJavaScriptRepresentation(StringBuilder sb, char c) {
/*   0*/    try {
/*1273*/      appendHexJavaScriptRepresentation(c, sb);
/*1274*/    } catch (IOException ex) {
/*1276*/      throw new RuntimeException(ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendHexJavaScriptRepresentation(int codePoint, Appendable out) throws IOException {
/*1290*/    if (Character.isSupplementaryCodePoint(codePoint)) {
/*1295*/      char[] surrogates = Character.toChars(codePoint);
/*1296*/      appendHexJavaScriptRepresentation(surrogates[0], out);
/*1297*/      appendHexJavaScriptRepresentation(surrogates[1], out);
/*   0*/      return;
/*   0*/    } 
/*1300*/    out.append("\\u").append(HEX_CHARS[codePoint >>> 12 & 0xF]).append(HEX_CHARS[codePoint >>> 8 & 0xF]).append(HEX_CHARS[codePoint >>> 4 & 0xF]).append(HEX_CHARS[codePoint & 0xF]);
/*   0*/  }
/*   0*/}
