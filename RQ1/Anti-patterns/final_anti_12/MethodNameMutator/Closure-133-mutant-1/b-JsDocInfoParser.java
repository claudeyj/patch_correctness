/*   0*/package com.google.javascript.jscomp.parsing;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Splitter;
/*   0*/import com.google.common.collect.ImmutableSet;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.JSDocInfoBuilder;
/*   0*/import com.google.javascript.rhino.JSTypeExpression;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.ScriptRuntime;
/*   0*/import com.google.javascript.rhino.head.ErrorReporter;
/*   0*/import com.google.javascript.rhino.head.ast.Comment;
/*   0*/import com.google.javascript.rhino.jstype.StaticSourceFile;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public final class JsDocInfoParser {
/*   0*/  private final JsDocTokenStream stream;
/*   0*/  
/*   0*/  private final JSDocInfoBuilder jsdocBuilder;
/*   0*/  
/*   0*/  private final StaticSourceFile sourceFile;
/*   0*/  
/*   0*/  private final Node associatedNode;
/*   0*/  
/*   0*/  private final ErrorReporter errorReporter;
/*   0*/  
/*  55*/  private final ErrorReporterParser parser = new ErrorReporterParser();
/*   0*/  
/*   0*/  private final Node templateNode;
/*   0*/  
/*   0*/  private class ErrorReporterParser {
/*   0*/    private ErrorReporterParser() {}
/*   0*/    
/*   0*/    void addParserWarning(String messageId, String messageArg, int lineno, int charno) {
/*  64*/      JsDocInfoParser.this.errorReporter.warning(ScriptRuntime.getMessage1(messageId, messageArg), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/    
/*   0*/    void addParserWarning(String messageId, int lineno, int charno) {
/*  69*/      JsDocInfoParser.this.errorReporter.warning(ScriptRuntime.getMessage0(messageId), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/    
/*   0*/    void addTypeWarning(String messageId, String messageArg, int lineno, int charno) {
/*  75*/      JsDocInfoParser.this.errorReporter.warning("Bad type annotation. " + ScriptRuntime.getMessage1(messageId, messageArg), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/    
/*   0*/    void addTypeWarning(String messageId, int lineno, int charno) {
/*  82*/      JsDocInfoParser.this.errorReporter.warning("Bad type annotation. " + ScriptRuntime.getMessage0(messageId), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*  90*/  private JSDocInfo fileOverviewJSDocInfo = null;
/*   0*/  
/*   0*/  private State state;
/*   0*/  
/*   0*/  private final Map<String, Annotation> annotationNames;
/*   0*/  
/*   0*/  private final Set<String> suppressionNames;
/*   0*/  
/*  95*/  private static final Set<String> modifiesAnnotationKeywords = (Set<String>)ImmutableSet.of("this", "arguments");
/*   0*/  
/*   0*/  private Node.FileLevelJsDocBuilder fileLevelJsDocBuilder;
/*   0*/  
/*   0*/  void setFileLevelJsDocBuilder(Node.FileLevelJsDocBuilder fileLevelJsDocBuilder) {
/* 109*/    this.fileLevelJsDocBuilder = fileLevelJsDocBuilder;
/*   0*/  }
/*   0*/  
/*   0*/  void setFileOverviewJSDocInfo(JSDocInfo fileOverviewJSDocInfo) {
/* 117*/    this.fileOverviewJSDocInfo = fileOverviewJSDocInfo;
/*   0*/  }
/*   0*/  
/*   0*/  private enum State {
/* 121*/    SEARCHING_ANNOTATION, SEARCHING_NEWLINE, NEXT_IS_ANNOTATION;
/*   0*/  }
/*   0*/  
/*   0*/  private String getSourceName() {
/* 150*/    return (this.sourceFile == null) ? null : this.sourceFile.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo parseInlineTypeDoc() {
/* 154*/    Node typeAst = parseAndRecordTypeNode(next());
/* 155*/    JSTypeExpression expr = createJSTypeExpression(typeAst);
/* 156*/    if (expr != null) {
/* 157*/      this.jsdocBuilder.recordType(expr);
/* 158*/      return retrieveAndResetParsedJSDocInfo();
/*   0*/    } 
/* 160*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static Node parseTypeString(String typeString) {
/* 168*/    Config config = new Config(Sets.newHashSet(), Sets.newHashSet(), false, Config.LanguageMode.ECMASCRIPT3, false);
/* 174*/    JsDocInfoParser parser = new JsDocInfoParser(new JsDocTokenStream(typeString), null, null, config, NullErrorReporter.forNewRhino());
/* 181*/    return parser.parseTopLevelTypeExpression(parser.next());
/*   0*/  }
/*   0*/  
/*   0*/  boolean parse() {
/* 200*/    this.state = State.SEARCHING_ANNOTATION;
/* 201*/    skipEOLs();
/* 203*/    JsDocToken token = next();
/* 205*/    List<ExtendedTypeInfo> extendedTypes = Lists.newArrayList();
/* 208*/    if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 209*/      ExtractionInfo blockInfo = extractBlockComment(token);
/* 210*/      token = blockInfo.token;
/* 211*/      if (!blockInfo.string.isEmpty())
/* 212*/        this.jsdocBuilder.recordBlockDescription(blockInfo.string); 
/* 215*/    } else if (token != JsDocToken.ANNOTATION && token != JsDocToken.EOC) {
/* 219*/      this.jsdocBuilder.recordBlockDescription("");
/*   0*/    } 
/*   0*/    while (true) {
/* 225*/      switch (token) {
/*   0*/        case ANNOTATION:
/* 227*/          if (this.state == State.SEARCHING_ANNOTATION) {
/*   0*/            JSTypeExpression type;
/*   0*/            ExtractionInfo reasonInfo;
/*   0*/            String reason;
/*   0*/            ExtractionInfo descriptionInfo;
/*   0*/            String fileOverview, description;
/*   0*/            ExtractionInfo preserveInfo;
/*   0*/            String preserve;
/*   0*/            boolean matchingRc;
/*   0*/            ExtractionInfo meaningInfo;
/*   0*/            String meaning, name;
/*   0*/            boolean isBracketedParam;
/*   0*/            ExtractionInfo templateInfo, classTemplateInfo, versionInfo;
/*   0*/            List<String> names;
/*   0*/            String version;
/*   0*/            Node typeNode;
/*   0*/            boolean hasType, isAlternateTypeAnnotation, canSkipTypeAnnotation, hasError;
/* 228*/            this.state = State.SEARCHING_NEWLINE;
/* 229*/            int lineno = this.stream.getLineno();
/* 230*/            int charno = this.stream.getCharno();
/* 232*/            String annotationName = this.stream.getString();
/* 233*/            Annotation annotation = this.annotationNames.get(annotationName);
/* 234*/            if (annotation == null) {
/* 235*/              this.parser.addParserWarning("msg.bad.jsdoc.tag", annotationName, this.stream.getLineno(), this.stream.getCharno());
/*   0*/              break;
/*   0*/            } 
/* 239*/            this.jsdocBuilder.markAnnotation(annotationName, lineno, charno);
/* 241*/            switch (annotation) {
/*   0*/              case NG_INJECT:
/* 243*/                if (this.jsdocBuilder.isNgInjectRecorded()) {
/* 244*/                  this.parser.addParserWarning("msg.jsdoc.nginject.extra", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                } else {
/* 247*/                  this.jsdocBuilder.recordNgInject(true);
/*   0*/                } 
/* 249*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case AUTHOR:
/* 253*/                if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 254*/                  ExtractionInfo authorInfo = extractSingleLineBlock();
/* 255*/                  String author = authorInfo.string;
/* 257*/                  if (author.length() == 0) {
/* 258*/                    this.parser.addParserWarning("msg.jsdoc.authormissing", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  } else {
/* 261*/                    this.jsdocBuilder.addAuthor(author);
/*   0*/                  } 
/* 263*/                  token = authorInfo.token;
/*   0*/                  continue;
/*   0*/                } 
/* 265*/                token = eatTokensUntilEOL(token);
/*   0*/                continue;
/*   0*/              case CONSISTENTIDGENERATOR:
/* 270*/                if (!this.jsdocBuilder.recordConsistentIdGenerator())
/* 271*/                  this.parser.addParserWarning("msg.jsdoc.consistidgen", this.stream.getLineno(), this.stream.getCharno()); 
/* 274*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case STRUCT:
/* 278*/                if (!this.jsdocBuilder.recordStruct())
/* 279*/                  this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno()); 
/* 283*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case DICT:
/* 287*/                if (!this.jsdocBuilder.recordDict())
/* 288*/                  this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno()); 
/* 292*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case CONSTRUCTOR:
/* 296*/                if (!this.jsdocBuilder.recordConstructor())
/* 297*/                  if (this.jsdocBuilder.isInterfaceRecorded()) {
/* 298*/                    this.parser.addTypeWarning("msg.jsdoc.interface.constructor", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  } else {
/* 301*/                    this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  }  
/* 305*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case DEPRECATED:
/* 309*/                if (!this.jsdocBuilder.recordDeprecated())
/* 310*/                  this.parser.addParserWarning("msg.jsdoc.deprecated", this.stream.getLineno(), this.stream.getCharno()); 
/* 315*/                reasonInfo = extractMultilineTextualBlock(token);
/* 318*/                reason = reasonInfo.string;
/* 320*/                if (reason.length() > 0)
/* 321*/                  this.jsdocBuilder.recordDeprecationReason(reason); 
/* 324*/                token = reasonInfo.token;
/*   0*/                continue;
/*   0*/              case INTERFACE:
/* 328*/                if (!this.jsdocBuilder.recordInterface())
/* 329*/                  if (this.jsdocBuilder.isConstructorRecorded()) {
/* 330*/                    this.parser.addTypeWarning("msg.jsdoc.interface.constructor", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  } else {
/* 333*/                    this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  }  
/* 337*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case DESC:
/* 341*/                if (this.jsdocBuilder.isDescriptionRecorded()) {
/* 342*/                  this.parser.addParserWarning("msg.jsdoc.desc.extra", this.stream.getLineno(), this.stream.getCharno());
/* 344*/                  token = eatTokensUntilEOL();
/*   0*/                  continue;
/*   0*/                } 
/* 347*/                descriptionInfo = extractMultilineTextualBlock(token);
/* 350*/                description = descriptionInfo.string;
/* 352*/                this.jsdocBuilder.recordDescription(description);
/* 353*/                token = descriptionInfo.token;
/*   0*/                continue;
/*   0*/              case FILE_OVERVIEW:
/* 358*/                fileOverview = "";
/* 359*/                if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 360*/                  ExtractionInfo fileOverviewInfo = extractMultilineTextualBlock(token, WhitespaceOption.TRIM);
/* 364*/                  fileOverview = fileOverviewInfo.string;
/* 366*/                  token = fileOverviewInfo.token;
/*   0*/                } else {
/* 368*/                  token = eatTokensUntilEOL(token);
/*   0*/                } 
/* 371*/                if (!this.jsdocBuilder.recordFileOverview(fileOverview))
/* 372*/                  this.parser.addParserWarning("msg.jsdoc.fileoverview.extra", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/                continue;
/*   0*/              case LICENSE:
/*   0*/              case PRESERVE:
/* 379*/                preserveInfo = extractMultilineTextualBlock(token, WhitespaceOption.PRESERVE);
/* 383*/                preserve = preserveInfo.string;
/* 385*/                if (preserve.length() > 0 && 
/* 386*/                  this.fileLevelJsDocBuilder != null)
/* 387*/                  this.fileLevelJsDocBuilder.append(preserve); 
/* 391*/                token = preserveInfo.token;
/*   0*/                continue;
/*   0*/              case ENUM:
/* 395*/                token = next();
/* 396*/                lineno = this.stream.getLineno();
/* 397*/                charno = this.stream.getCharno();
/* 399*/                type = null;
/* 400*/                if (token != JsDocToken.EOL && token != JsDocToken.EOC)
/* 401*/                  type = createJSTypeExpression(parseAndRecordTypeNode(token)); 
/* 405*/                if (type == null)
/* 406*/                  type = createJSTypeExpression(newStringNode("number")); 
/* 408*/                if (!this.jsdocBuilder.recordEnumParameterType(type))
/* 409*/                  this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno); 
/* 412*/                token = eatTokensUntilEOL(token);
/*   0*/                continue;
/*   0*/              case EXPORT:
/* 416*/                if (!this.jsdocBuilder.recordExport())
/* 417*/                  this.parser.addParserWarning("msg.jsdoc.export", this.stream.getLineno(), this.stream.getCharno()); 
/* 420*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case EXPOSE:
/* 424*/                if (!this.jsdocBuilder.recordExpose())
/* 425*/                  this.parser.addParserWarning("msg.jsdoc.expose", this.stream.getLineno(), this.stream.getCharno()); 
/* 428*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case EXTERNS:
/* 432*/                if (!this.jsdocBuilder.recordExterns())
/* 433*/                  this.parser.addParserWarning("msg.jsdoc.externs", this.stream.getLineno(), this.stream.getCharno()); 
/* 436*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case JAVA_DISPATCH:
/* 440*/                if (!this.jsdocBuilder.recordJavaDispatch())
/* 441*/                  this.parser.addParserWarning("msg.jsdoc.javadispatch", this.stream.getLineno(), this.stream.getCharno()); 
/* 444*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case EXTENDS:
/*   0*/              case IMPLEMENTS:
/* 449*/                skipEOLs();
/* 450*/                token = next();
/* 451*/                lineno = this.stream.getLineno();
/* 452*/                charno = this.stream.getCharno();
/* 453*/                matchingRc = false;
/* 455*/                if (token == JsDocToken.LC) {
/* 456*/                  token = next();
/* 457*/                  matchingRc = true;
/*   0*/                } 
/* 460*/                if (token == JsDocToken.STRING) {
/* 461*/                  Node node = parseAndRecordTypeNameNode(token, lineno, charno, matchingRc);
/* 464*/                  lineno = this.stream.getLineno();
/* 465*/                  charno = this.stream.getCharno();
/* 467*/                  node = wrapNode(306, node);
/* 468*/                  type = createJSTypeExpression(node);
/* 470*/                  if (annotation == Annotation.EXTENDS) {
/* 472*/                    extendedTypes.add(new ExtendedTypeInfo(type, this.stream.getLineno(), this.stream.getCharno()));
/*   0*/                  } else {
/* 475*/                    Preconditions.checkState((annotation == Annotation.IMPLEMENTS));
/* 477*/                    if (!this.jsdocBuilder.recordImplementedInterface(type))
/* 478*/                      this.parser.addTypeWarning("msg.jsdoc.implements.duplicate", lineno, charno); 
/*   0*/                  } 
/* 482*/                  token = next();
/* 483*/                  if (matchingRc) {
/* 484*/                    if (token != JsDocToken.RC)
/* 485*/                      this.parser.addTypeWarning("msg.jsdoc.missing.rc", this.stream.getLineno(), this.stream.getCharno()); 
/* 488*/                  } else if (token != JsDocToken.EOL && token != JsDocToken.EOF && token != JsDocToken.EOC) {
/* 490*/                    this.parser.addTypeWarning("msg.end.annotation.expected", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  } 
/*   0*/                } else {
/* 494*/                  this.parser.addTypeWarning("msg.no.type.name", lineno, charno);
/*   0*/                } 
/* 496*/                token = eatTokensUntilEOL(token);
/*   0*/                continue;
/*   0*/              case HIDDEN:
/* 500*/                if (!this.jsdocBuilder.recordHiddenness())
/* 501*/                  this.parser.addParserWarning("msg.jsdoc.hidden", this.stream.getLineno(), this.stream.getCharno()); 
/* 504*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case LENDS:
/* 508*/                skipEOLs();
/* 510*/                matchingRc = false;
/* 511*/                if (match(JsDocToken.LC)) {
/* 512*/                  token = next();
/* 513*/                  matchingRc = true;
/*   0*/                } 
/* 516*/                if (match(JsDocToken.STRING)) {
/* 517*/                  token = next();
/* 518*/                  if (!this.jsdocBuilder.recordLends(this.stream.getString()))
/* 519*/                    this.parser.addTypeWarning("msg.jsdoc.lends.incompatible", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/                } else {
/* 523*/                  this.parser.addTypeWarning("msg.jsdoc.lends.missing", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                } 
/* 527*/                if (matchingRc && !match(JsDocToken.RC))
/* 528*/                  this.parser.addTypeWarning("msg.jsdoc.missing.rc", this.stream.getLineno(), this.stream.getCharno()); 
/* 531*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case MEANING:
/* 535*/                meaningInfo = extractMultilineTextualBlock(token);
/* 537*/                meaning = meaningInfo.string;
/* 538*/                token = meaningInfo.token;
/* 539*/                if (!this.jsdocBuilder.recordMeaning(meaning))
/* 540*/                  this.parser.addParserWarning("msg.jsdoc.meaning.extra", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/                continue;
/*   0*/              case NO_ALIAS:
/* 546*/                if (!this.jsdocBuilder.recordNoAlias())
/* 547*/                  this.parser.addParserWarning("msg.jsdoc.noalias", this.stream.getLineno(), this.stream.getCharno()); 
/* 550*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case NO_COMPILE:
/* 554*/                if (!this.jsdocBuilder.recordNoCompile())
/* 555*/                  this.parser.addParserWarning("msg.jsdoc.nocompile", this.stream.getLineno(), this.stream.getCharno()); 
/* 558*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case NO_TYPE_CHECK:
/* 562*/                if (!this.jsdocBuilder.recordNoTypeCheck())
/* 563*/                  this.parser.addParserWarning("msg.jsdoc.nocheck", this.stream.getLineno(), this.stream.getCharno()); 
/* 566*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case NOT_IMPLEMENTED:
/* 570*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case INHERIT_DOC:
/*   0*/              case OVERRIDE:
/* 575*/                if (!this.jsdocBuilder.recordOverride())
/* 576*/                  this.parser.addTypeWarning("msg.jsdoc.override", this.stream.getLineno(), this.stream.getCharno()); 
/* 579*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case THROWS:
/* 583*/                skipEOLs();
/* 584*/                token = next();
/* 585*/                lineno = this.stream.getLineno();
/* 586*/                charno = this.stream.getCharno();
/* 587*/                type = null;
/* 589*/                if (token == JsDocToken.LC) {
/* 590*/                  type = createJSTypeExpression(parseAndRecordTypeNode(token));
/* 593*/                  if (type == null) {
/* 596*/                    token = eatTokensUntilEOL();
/*   0*/                    continue;
/*   0*/                  } 
/*   0*/                } 
/* 602*/                token = current();
/* 605*/                this.jsdocBuilder.recordThrowType(type);
/* 608*/                if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 609*/                  ExtractionInfo extractionInfo = extractMultilineTextualBlock(token);
/* 612*/                  String str = extractionInfo.string;
/* 614*/                  if (str.length() > 0)
/* 615*/                    this.jsdocBuilder.recordThrowDescription(type, str); 
/* 618*/                  token = extractionInfo.token;
/*   0*/                  continue;
/*   0*/                } 
/* 620*/                token = eatTokensUntilEOL(token);
/*   0*/                continue;
/*   0*/              case PARAM:
/* 625*/                skipEOLs();
/* 626*/                token = next();
/* 627*/                lineno = this.stream.getLineno();
/* 628*/                charno = this.stream.getCharno();
/* 629*/                type = null;
/* 631*/                if (token == JsDocToken.LC) {
/* 632*/                  type = createJSTypeExpression(parseAndRecordParamTypeNode(token));
/* 635*/                  if (type == null) {
/* 638*/                    token = eatTokensUntilEOL();
/*   0*/                    continue;
/*   0*/                  } 
/* 641*/                  skipEOLs();
/* 642*/                  token = next();
/* 643*/                  lineno = this.stream.getLineno();
/* 644*/                  charno = this.stream.getCharno();
/*   0*/                } 
/* 647*/                name = null;
/* 648*/                isBracketedParam = (JsDocToken.LB == token);
/* 649*/                if (isBracketedParam)
/* 650*/                  token = next(); 
/* 653*/                if (JsDocToken.STRING != token) {
/* 654*/                  this.parser.addTypeWarning("msg.missing.variable.name", lineno, charno);
/*   0*/                } else {
/* 657*/                  name = this.stream.getString();
/* 659*/                  if (isBracketedParam) {
/* 660*/                    token = next();
/* 665*/                    if (JsDocToken.EQUALS == token) {
/* 666*/                      token = next();
/* 667*/                      if (JsDocToken.STRING == token)
/* 668*/                        token = next(); 
/*   0*/                    } 
/* 672*/                    if (JsDocToken.RB != token) {
/* 673*/                      reportTypeSyntaxWarning("msg.jsdoc.missing.rb");
/* 674*/                    } else if (type != null) {
/* 677*/                      type = JSTypeExpression.makeOptionalArg(type);
/*   0*/                    } 
/*   0*/                  } 
/* 684*/                  if (name.indexOf('.') > -1) {
/* 685*/                    name = null;
/* 686*/                  } else if (!this.jsdocBuilder.recordParameter(name, type)) {
/* 687*/                    if (this.jsdocBuilder.hasParameter(name)) {
/* 688*/                      this.parser.addTypeWarning("msg.dup.variable.name", name, lineno, charno);
/*   0*/                    } else {
/* 691*/                      this.parser.addTypeWarning("msg.jsdoc.incompat.type", name, lineno, charno);
/*   0*/                    } 
/*   0*/                  } 
/*   0*/                } 
/* 697*/                if (name == null) {
/* 698*/                  token = eatTokensUntilEOL(token);
/*   0*/                  continue;
/*   0*/                } 
/* 702*/                this.jsdocBuilder.markName(name, this.sourceFile, lineno, charno);
/* 705*/                if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 706*/                  ExtractionInfo paramDescriptionInfo = extractMultilineTextualBlock(token);
/* 709*/                  String paramDescription = paramDescriptionInfo.string;
/* 711*/                  if (paramDescription.length() > 0)
/* 712*/                    this.jsdocBuilder.recordParameterDescription(name, paramDescription); 
/* 716*/                  token = paramDescriptionInfo.token;
/*   0*/                  continue;
/*   0*/                } 
/* 718*/                token = eatTokensUntilEOL(token);
/*   0*/                continue;
/*   0*/              case PRESERVE_TRY:
/* 723*/                if (!this.jsdocBuilder.recordPreserveTry())
/* 724*/                  this.parser.addParserWarning("msg.jsdoc.preservertry", this.stream.getLineno(), this.stream.getCharno()); 
/* 727*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case NO_SHADOW:
/* 731*/                if (!this.jsdocBuilder.recordNoShadow())
/* 732*/                  this.parser.addParserWarning("msg.jsdoc.noshadow", this.stream.getLineno(), this.stream.getCharno()); 
/* 735*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case NO_SIDE_EFFECTS:
/* 739*/                if (!this.jsdocBuilder.recordNoSideEffects())
/* 740*/                  this.parser.addParserWarning("msg.jsdoc.nosideeffects", this.stream.getLineno(), this.stream.getCharno()); 
/* 743*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case MODIFIES:
/* 747*/                token = parseModifiesTag(next());
/*   0*/                continue;
/*   0*/              case IMPLICIT_CAST:
/* 751*/                if (!this.jsdocBuilder.recordImplicitCast())
/* 752*/                  this.parser.addTypeWarning("msg.jsdoc.implicitcast", this.stream.getLineno(), this.stream.getCharno()); 
/* 755*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case SEE:
/* 759*/                if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 760*/                  ExtractionInfo referenceInfo = extractSingleLineBlock();
/* 761*/                  String reference = referenceInfo.string;
/* 763*/                  if (reference.length() == 0) {
/* 764*/                    this.parser.addParserWarning("msg.jsdoc.seemissing", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                  } else {
/* 767*/                    this.jsdocBuilder.addReference(reference);
/*   0*/                  } 
/* 770*/                  token = referenceInfo.token;
/*   0*/                  continue;
/*   0*/                } 
/* 772*/                token = eatTokensUntilEOL(token);
/*   0*/                continue;
/*   0*/              case STABLEIDGENERATOR:
/* 777*/                if (!this.jsdocBuilder.recordStableIdGenerator())
/* 778*/                  this.parser.addParserWarning("msg.jsdoc.stableidgen", this.stream.getLineno(), this.stream.getCharno()); 
/* 781*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case SUPPRESS:
/* 785*/                token = parseSuppressTag(next());
/*   0*/                continue;
/*   0*/              case TEMPLATE:
/* 789*/                templateInfo = extractSingleLineBlock();
/* 790*/                names = Lists.newArrayList(Splitter.on(',').trimResults().split(templateInfo.string));
/* 795*/                if (names.size() == 0 || ((String)names.get(0)).length() == 0) {
/* 796*/                  this.parser.addTypeWarning("msg.jsdoc.templatemissing", this.stream.getLineno(), this.stream.getCharno());
/* 798*/                } else if (!this.jsdocBuilder.recordTemplateTypeNames(names)) {
/* 799*/                  this.parser.addTypeWarning("msg.jsdoc.template.at.most.once", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                } 
/* 803*/                token = templateInfo.token;
/*   0*/                continue;
/*   0*/              case CLASS_TEMPLATE:
/* 808*/                classTemplateInfo = extractSingleLineBlock();
/* 809*/                names = Lists.newArrayList(Splitter.on(',').trimResults().split(classTemplateInfo.string));
/* 814*/                if (names.size() == 0 || ((String)names.get(0)).length() == 0) {
/* 815*/                  this.parser.addTypeWarning("msg.jsdoc.classtemplate.missing.type.name", this.stream.getLineno(), this.stream.getCharno());
/* 818*/                } else if (!this.jsdocBuilder.recordClassTemplateTypeNames(names)) {
/* 819*/                  this.parser.addTypeWarning("msg.jsdoc.classtemplate.at.most.once", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                } 
/* 824*/                token = classTemplateInfo.token;
/*   0*/                continue;
/*   0*/              case IDGENERATOR:
/* 829*/                if (!this.jsdocBuilder.recordIdGenerator())
/* 830*/                  this.parser.addParserWarning("msg.jsdoc.idgen", this.stream.getLineno(), this.stream.getCharno()); 
/* 833*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/              case VERSION:
/* 837*/                versionInfo = extractSingleLineBlock();
/* 838*/                version = versionInfo.string;
/* 840*/                if (version.length() == 0) {
/* 841*/                  this.parser.addParserWarning("msg.jsdoc.versionmissing", this.stream.getLineno(), this.stream.getCharno());
/* 844*/                } else if (!this.jsdocBuilder.recordVersion(version)) {
/* 845*/                  this.parser.addParserWarning("msg.jsdoc.extraversion", this.stream.getLineno(), this.stream.getCharno());
/*   0*/                } 
/* 850*/                token = versionInfo.token;
/*   0*/                continue;
/*   0*/              case CONSTANT:
/*   0*/              case DEFINE:
/*   0*/              case PRIVATE:
/*   0*/              case PROTECTED:
/*   0*/              case PUBLIC:
/*   0*/              case RETURN:
/*   0*/              case THIS:
/*   0*/              case TYPEDEF:
/*   0*/              case TYPE:
/* 862*/                lineno = this.stream.getLineno();
/* 863*/                charno = this.stream.getCharno();
/* 865*/                typeNode = null;
/* 866*/                hasType = lookAheadForTypeAnnotation();
/* 867*/                isAlternateTypeAnnotation = (annotation == Annotation.PRIVATE || annotation == Annotation.PROTECTED || annotation == Annotation.PUBLIC || annotation == Annotation.CONSTANT);
/* 872*/                canSkipTypeAnnotation = (isAlternateTypeAnnotation || annotation == Annotation.RETURN);
/* 875*/                type = null;
/* 876*/                if (hasType || !canSkipTypeAnnotation) {
/* 877*/                  skipEOLs();
/* 878*/                  token = next();
/* 879*/                  typeNode = parseAndRecordTypeNode(token);
/* 881*/                  if (annotation == Annotation.THIS)
/* 882*/                    typeNode = wrapNode(306, typeNode); 
/* 884*/                  type = createJSTypeExpression(typeNode);
/*   0*/                } 
/* 889*/                hasError = (type == null && !canSkipTypeAnnotation);
/* 890*/                if (!hasError) {
/* 898*/                  if ((type != null && isAlternateTypeAnnotation) || annotation == Annotation.TYPE)
/* 900*/                    if (!this.jsdocBuilder.recordType(type))
/* 901*/                      this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno);  
/* 906*/                  switch (annotation) {
/*   0*/                    case CONSTANT:
/* 908*/                      if (!this.jsdocBuilder.recordConstancy())
/* 909*/                        this.parser.addParserWarning("msg.jsdoc.const", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/                      break;
/*   0*/                    case DEFINE:
/* 915*/                      if (!this.jsdocBuilder.recordDefineType(type))
/* 916*/                        this.parser.addParserWarning("msg.jsdoc.define", lineno, charno); 
/*   0*/                      break;
/*   0*/                    case PRIVATE:
/* 922*/                      if (!this.jsdocBuilder.recordVisibility(JSDocInfo.Visibility.PRIVATE))
/* 923*/                        this.parser.addParserWarning("msg.jsdoc.visibility.private", lineno, charno); 
/*   0*/                      break;
/*   0*/                    case PROTECTED:
/* 930*/                      if (!this.jsdocBuilder.recordVisibility(JSDocInfo.Visibility.PROTECTED))
/* 931*/                        this.parser.addParserWarning("msg.jsdoc.visibility.protected", lineno, charno); 
/*   0*/                      break;
/*   0*/                    case PUBLIC:
/* 938*/                      if (!this.jsdocBuilder.recordVisibility(JSDocInfo.Visibility.PUBLIC))
/* 939*/                        this.parser.addParserWarning("msg.jsdoc.visibility.public", lineno, charno); 
/*   0*/                      break;
/*   0*/                    case RETURN:
/* 946*/                      if (type == null)
/* 947*/                        type = createJSTypeExpression(newNode(304)); 
/* 950*/                      if (!this.jsdocBuilder.recordReturnType(type)) {
/* 951*/                        this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno);
/*   0*/                        break;
/*   0*/                      } 
/* 957*/                      if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 958*/                        ExtractionInfo returnDescriptionInfo = extractMultilineTextualBlock(token);
/* 961*/                        String returnDescription = returnDescriptionInfo.string;
/* 964*/                        if (returnDescription.length() > 0)
/* 965*/                          this.jsdocBuilder.recordReturnDescription(returnDescription); 
/* 969*/                        token = returnDescriptionInfo.token;
/*   0*/                        continue;
/*   0*/                      } 
/* 971*/                      token = eatTokensUntilEOL(token);
/*   0*/                      continue;
/*   0*/                    case THIS:
/* 976*/                      if (!this.jsdocBuilder.recordThisType(type))
/* 977*/                        this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno); 
/*   0*/                      break;
/*   0*/                    case TYPEDEF:
/* 983*/                      if (!this.jsdocBuilder.recordTypedef(type))
/* 984*/                        this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno); 
/*   0*/                      break;
/*   0*/                  } 
/*   0*/                } 
/* 991*/                token = eatTokensUntilEOL();
/*   0*/                continue;
/*   0*/            } 
/*   0*/          } 
/*   0*/          break;
/*   0*/        case EOC:
/* 999*/          if (hasParsedFileOverviewDocInfo())
/*1000*/            this.fileOverviewJSDocInfo = retrieveAndResetParsedJSDocInfo(); 
/*1002*/          checkExtendedTypes(extendedTypes);
/*1003*/          return true;
/*   0*/        case EOF:
/*1007*/          this.jsdocBuilder.build(null);
/*1008*/          this.parser.addParserWarning("msg.unexpected.eof", this.stream.getLineno(), this.stream.getCharno());
/*1010*/          checkExtendedTypes(extendedTypes);
/*1011*/          return false;
/*   0*/        case EOL:
/*1014*/          if (this.state == State.SEARCHING_NEWLINE)
/*1015*/            this.state = State.SEARCHING_ANNOTATION; 
/*1017*/          token = next();
/*   0*/          continue;
/*   0*/        default:
/*1021*/          if (token == JsDocToken.STAR && this.state == State.SEARCHING_ANNOTATION) {
/*1022*/            token = next();
/*   0*/            continue;
/*   0*/          } 
/*1025*/          this.state = State.SEARCHING_NEWLINE;
/*1026*/          token = eatTokensUntilEOL();
/*   0*/          continue;
/*   0*/      } 
/*1032*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkExtendedTypes(List<ExtendedTypeInfo> extendedTypes) {
/*1037*/    for (ExtendedTypeInfo typeInfo : extendedTypes) {
/*1039*/      if (this.jsdocBuilder.isInterfaceRecorded()) {
/*1040*/        if (!this.jsdocBuilder.recordExtendedInterface(typeInfo.type))
/*1041*/          this.parser.addParserWarning("msg.jsdoc.extends.duplicate", typeInfo.lineno, typeInfo.charno); 
/*   0*/        continue;
/*   0*/      } 
/*1045*/      if (!this.jsdocBuilder.recordBaseType(typeInfo.type))
/*1046*/        this.parser.addTypeWarning("msg.jsdoc.incompat.type", typeInfo.lineno, typeInfo.charno); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken parseSuppressTag(JsDocToken token) {
/*1060*/    if (token == JsDocToken.LC) {
/*1061*/      Set<String> suppressions = new HashSet<String>();
/*   0*/      while (true) {
/*1063*/        if (match(JsDocToken.STRING)) {
/*1064*/          String name = this.stream.getString();
/*1065*/          if (!this.suppressionNames.contains(name))
/*1066*/            this.parser.addParserWarning("msg.jsdoc.suppress.unknown", name, this.stream.getLineno(), this.stream.getCharno()); 
/*1070*/          suppressions.add(this.stream.getString());
/*1071*/          token = next();
/*   0*/        } else {
/*1073*/          this.parser.addParserWarning("msg.jsdoc.suppress", this.stream.getLineno(), this.stream.getCharno());
/*1075*/          return token;
/*   0*/        } 
/*1078*/        if (match(JsDocToken.PIPE)) {
/*1079*/          token = next();
/*   0*/          continue;
/*   0*/        } 
/*   0*/        break;
/*   0*/      } 
/*1085*/      if (!match(JsDocToken.RC)) {
/*1086*/        this.parser.addParserWarning("msg.jsdoc.suppress", this.stream.getLineno(), this.stream.getCharno());
/*   0*/      } else {
/*1089*/        token = next();
/*1090*/        if (!this.jsdocBuilder.recordSuppressions(suppressions))
/*1091*/          this.parser.addParserWarning("msg.jsdoc.suppress.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/      } 
/*   0*/    } 
/*1096*/    return token;
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken parseModifiesTag(JsDocToken token) {
/*1106*/    if (token == JsDocToken.LC) {
/*1107*/      Set<String> modifies = new HashSet<String>();
/*   0*/      while (true) {
/*1109*/        if (match(JsDocToken.STRING)) {
/*1110*/          String name = this.stream.getString();
/*1111*/          if (!modifiesAnnotationKeywords.contains(name) && !this.jsdocBuilder.hasParameter(name))
/*1113*/            this.parser.addParserWarning("msg.jsdoc.modifies.unknown", name, this.stream.getLineno(), this.stream.getCharno()); 
/*1117*/          modifies.add(this.stream.getString());
/*1118*/          token = next();
/*   0*/        } else {
/*1120*/          this.parser.addParserWarning("msg.jsdoc.modifies", this.stream.getLineno(), this.stream.getCharno());
/*1122*/          return token;
/*   0*/        } 
/*1125*/        if (match(JsDocToken.PIPE)) {
/*1126*/          token = next();
/*   0*/          continue;
/*   0*/        } 
/*   0*/        break;
/*   0*/      } 
/*1132*/      if (!match(JsDocToken.RC)) {
/*1133*/        this.parser.addParserWarning("msg.jsdoc.modifies", this.stream.getLineno(), this.stream.getCharno());
/*   0*/      } else {
/*1136*/        token = next();
/*1137*/        if (!this.jsdocBuilder.recordModifies(modifies))
/*1138*/          this.parser.addParserWarning("msg.jsdoc.modifies.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/      } 
/*   0*/    } 
/*1143*/    return token;
/*   0*/  }
/*   0*/  
/*   0*/  Node parseAndRecordTypeNode(JsDocToken token) {
/*1154*/    return parseAndRecordTypeNode(token, (token == JsDocToken.LC));
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordTypeNode(JsDocToken token, boolean matchingLC) {
/*1166*/    return parseAndRecordTypeNode(token, this.stream.getLineno(), this.stream.getCharno(), matchingLC, false);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordTypeNameNode(JsDocToken token, int lineno, int startCharno, boolean matchingLC) {
/*1182*/    return parseAndRecordTypeNode(token, lineno, startCharno, matchingLC, true);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordParamTypeNode(JsDocToken token) {
/*1199*/    Preconditions.checkArgument((token == JsDocToken.LC));
/*1200*/    int lineno = this.stream.getLineno();
/*1201*/    int startCharno = this.stream.getCharno();
/*1203*/    Node typeNode = parseParamTypeExpressionAnnotation(token);
/*1204*/    if (typeNode != null) {
/*1205*/      int endLineno = this.stream.getLineno();
/*1206*/      int endCharno = this.stream.getCharno();
/*1208*/      this.jsdocBuilder.markTypeNode(typeNode, lineno, startCharno, endLineno, endCharno, true);
/*   0*/    } 
/*1211*/    return typeNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordTypeNode(JsDocToken token, int lineno, int startCharno, boolean matchingLC, boolean onlyParseSimpleNames) {
/*1231*/    Node typeNode = null;
/*1233*/    if (onlyParseSimpleNames) {
/*1234*/      typeNode = parseTypeNameAnnotation(token);
/*   0*/    } else {
/*1236*/      typeNode = parseTypeExpressionAnnotation(token);
/*   0*/    } 
/*1239*/    if (typeNode != null) {
/*1240*/      int endLineno = this.stream.getLineno();
/*1241*/      int endCharno = this.stream.getCharno();
/*1243*/      this.jsdocBuilder.markTypeNode(typeNode, lineno, startCharno, endLineno, endCharno, matchingLC);
/*   0*/    } 
/*1247*/    return typeNode;
/*   0*/  }
/*   0*/  
/*   0*/  private String toString(JsDocToken token) {
/*1254*/    switch (token) {
/*   0*/      case ANNOTATION:
/*1256*/        return "@" + this.stream.getString();
/*   0*/      case BANG:
/*1259*/        return "!";
/*   0*/      case COMMA:
/*1262*/        return ",";
/*   0*/      case COLON:
/*1265*/        return ":";
/*   0*/      case GT:
/*1268*/        return ">";
/*   0*/      case LB:
/*1271*/        return "[";
/*   0*/      case LC:
/*1274*/        return "{";
/*   0*/      case LP:
/*1277*/        return "(";
/*   0*/      case LT:
/*1280*/        return ".<";
/*   0*/      case QMARK:
/*1283*/        return "?";
/*   0*/      case PIPE:
/*1286*/        return "|";
/*   0*/      case RB:
/*1289*/        return "]";
/*   0*/      case RC:
/*1292*/        return "}";
/*   0*/      case RP:
/*1295*/        return ")";
/*   0*/      case STAR:
/*1298*/        return "*";
/*   0*/      case ELLIPSIS:
/*1301*/        return "...";
/*   0*/      case EQUALS:
/*1304*/        return "=";
/*   0*/      case STRING:
/*1307*/        return this.stream.getString();
/*   0*/    } 
/*1310*/    throw new IllegalStateException(token.toString());
/*   0*/  }
/*   0*/  
/*   0*/  JSTypeExpression createJSTypeExpression(Node n) {
/*1319*/    return (n == null) ? null : new JSTypeExpression(n, getSourceName());
/*   0*/  }
/*   0*/  
/*   0*/  private static class ExtractionInfo {
/*   0*/    private final String string;
/*   0*/    
/*   0*/    private final JsDocToken token;
/*   0*/    
/*   0*/    public ExtractionInfo(String string, JsDocToken token) {
/*1333*/      this.string = string;
/*1334*/      this.token = token;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class ExtendedTypeInfo {
/*   0*/    final JSTypeExpression type;
/*   0*/    
/*   0*/    final int lineno;
/*   0*/    
/*   0*/    final int charno;
/*   0*/    
/*   0*/    public ExtendedTypeInfo(JSTypeExpression type, int lineno, int charno) {
/*1347*/      this.type = type;
/*1348*/      this.lineno = lineno;
/*1349*/      this.charno = charno;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractSingleLineBlock() {
/*1363*/    this.stream.update();
/*1364*/    int lineno = this.stream.getLineno();
/*1365*/    int charno = this.stream.getCharno() + 1;
/*1367*/    String line = getRemainingJSDocLine().trim();
/*1370*/    if (line.length() > 0)
/*1371*/      this.jsdocBuilder.markText(line, lineno, charno, lineno, charno + line.length()); 
/*1375*/    return new ExtractionInfo(line, next());
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractMultilineTextualBlock(JsDocToken token) {
/*1379*/    return extractMultilineTextualBlock(token, WhitespaceOption.SINGLE_LINE);
/*   0*/  }
/*   0*/  
/*   0*/  private enum WhitespaceOption {
/*1387*/    PRESERVE, TRIM, SINGLE_LINE;
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractMultilineTextualBlock(JsDocToken token, WhitespaceOption option) {
/*1413*/    if (token == JsDocToken.EOC || token == JsDocToken.EOL || token == JsDocToken.EOF)
/*1415*/      return new ExtractionInfo("", token); 
/*1418*/    this.stream.update();
/*1419*/    int startLineno = this.stream.getLineno();
/*1420*/    int startCharno = this.stream.getCharno() + 1;
/*1423*/    String line = getRemainingJSDocLine();
/*1424*/    if (option != WhitespaceOption.PRESERVE)
/*1425*/      line = line.trim(); 
/*1428*/    StringBuilder builder = new StringBuilder();
/*1429*/    builder.append(line);
/*1431*/    this.state = State.SEARCHING_ANNOTATION;
/*1432*/    token = eatTokensUntilEOL();
/*   0*/    boolean ignoreStar = false;
/*1439*/    int lineStartChar = -1;
/*   0*/    while (true) {
/*1442*/      switch (token) {
/*   0*/        case STAR:
/*1444*/          if (ignoreStar) {
/*1446*/            lineStartChar = this.stream.getCharno() + 1;
/*   0*/          } else {
/*1449*/            if (builder.length() > 0)
/*1450*/              builder.append(' '); 
/*1453*/            builder.append('*');
/*   0*/          } 
/*1456*/          token = next();
/*   0*/          continue;
/*   0*/        case EOL:
/*1460*/          if (option != WhitespaceOption.SINGLE_LINE)
/*1461*/            builder.append("\n"); 
/*1464*/          ignoreStar = true;
/*1465*/          lineStartChar = 0;
/*1466*/          token = next();
/*   0*/          continue;
/*   0*/      } 
/*1470*/      ignoreStar = false;
/*1471*/      this.state = State.SEARCHING_ANNOTATION;
/*1473*/      boolean isEOC = (token == JsDocToken.EOC);
/*1474*/      if (!isEOC)
/*1475*/        if (lineStartChar != -1 && option == WhitespaceOption.PRESERVE) {
/*1476*/          int numSpaces = this.stream.getCharno() - lineStartChar;
/*1477*/          for (int i = 0; i < numSpaces; i++)
/*1478*/            builder.append(' '); 
/*1480*/          lineStartChar = -1;
/*1481*/        } else if (builder.length() > 0) {
/*1483*/          builder.append(' ');
/*   0*/        }  
/*1487*/      if (token == JsDocToken.EOC || token == JsDocToken.EOF || (token == JsDocToken.ANNOTATION && option != WhitespaceOption.PRESERVE)) {
/*1493*/        String multilineText = builder.toString();
/*1495*/        if (option != WhitespaceOption.PRESERVE)
/*1496*/          multilineText = multilineText.trim(); 
/*1499*/        int endLineno = this.stream.getLineno();
/*1500*/        int endCharno = this.stream.getCharno();
/*1502*/        if (multilineText.length() > 0)
/*1503*/          this.jsdocBuilder.markText(multilineText, startLineno, startCharno, endLineno, endCharno); 
/*1507*/        return new ExtractionInfo(multilineText, token);
/*   0*/      } 
/*1510*/      builder.append(toString(token));
/*1512*/      line = getRemainingJSDocLine();
/*1514*/      if (option != WhitespaceOption.PRESERVE)
/*1515*/        line = trimEnd(line); 
/*1518*/      builder.append(line);
/*1519*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractBlockComment(JsDocToken token) {
/*1537*/    StringBuilder builder = new StringBuilder();
/*   0*/    boolean ignoreStar = true;
/*   0*/    while (true) {
/*1542*/      switch (token) {
/*   0*/        case ANNOTATION:
/*   0*/        case EOC:
/*   0*/        case EOF:
/*1546*/          return new ExtractionInfo(builder.toString().trim(), token);
/*   0*/        case STAR:
/*1549*/          if (!ignoreStar) {
/*1550*/            if (builder.length() > 0)
/*1551*/              builder.append(' '); 
/*1554*/            builder.append('*');
/*   0*/          } 
/*1557*/          token = next();
/*   0*/          continue;
/*   0*/        case EOL:
/*1561*/          ignoreStar = true;
/*1562*/          builder.append('\n');
/*1563*/          token = next();
/*   0*/          continue;
/*   0*/      } 
/*1567*/      if (!ignoreStar && builder.length() > 0)
/*1568*/        builder.append(' '); 
/*1571*/      ignoreStar = false;
/*1573*/      builder.append(toString(token));
/*1575*/      String line = getRemainingJSDocLine();
/*1576*/      line = trimEnd(line);
/*1577*/      builder.append(line);
/*1578*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static String trimEnd(String s) {
/*1594*/    int trimCount = 0;
/*1595*/    while (trimCount < s.length()) {
/*1596*/      char ch = s.charAt(s.length() - trimCount - 1);
/*1597*/      if (Character.isWhitespace(ch))
/*1598*/        trimCount++; 
/*   0*/    } 
/*1604*/    if (trimCount == 0)
/*1605*/      return s; 
/*1607*/    return s.substring(0, s.length() - trimCount);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeExpressionAnnotation(JsDocToken token) {
/*1624*/    if (token == JsDocToken.LC) {
/*1625*/      skipEOLs();
/*1626*/      Node typeNode = parseTopLevelTypeExpression(next());
/*1627*/      if (typeNode != null) {
/*1628*/        skipEOLs();
/*1629*/        if (!match(JsDocToken.RC)) {
/*1630*/          reportTypeSyntaxWarning("msg.jsdoc.missing.rc");
/*   0*/        } else {
/*1632*/          next();
/*   0*/        } 
/*   0*/      } 
/*1636*/      return typeNode;
/*   0*/    } 
/*1638*/    return parseTypeExpression(token);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseParamTypeExpressionAnnotation(JsDocToken token) {
/*1652*/    Preconditions.checkArgument((token == JsDocToken.LC));
/*1654*/    skipEOLs();
/*   0*/    boolean restArg = false;
/*1657*/    token = next();
/*1658*/    if (token == JsDocToken.ELLIPSIS) {
/*1659*/      token = next();
/*1660*/      if (token == JsDocToken.RC)
/*1662*/        return wrapNode(305, IR.empty()); 
/*1664*/      restArg = true;
/*   0*/    } 
/*1667*/    Node typeNode = parseTopLevelTypeExpression(token);
/*1668*/    if (typeNode != null) {
/*1669*/      skipEOLs();
/*1670*/      if (restArg) {
/*1671*/        typeNode = wrapNode(305, typeNode);
/*1672*/      } else if (match(JsDocToken.EQUALS)) {
/*1673*/        next();
/*1674*/        skipEOLs();
/*1675*/        typeNode = wrapNode(307, typeNode);
/*   0*/      } 
/*1678*/      if (!match(JsDocToken.RC)) {
/*1679*/        reportTypeSyntaxWarning("msg.jsdoc.missing.rc");
/*   0*/      } else {
/*1681*/        next();
/*   0*/      } 
/*   0*/    } 
/*1685*/    return typeNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeNameAnnotation(JsDocToken token) {
/*1692*/    if (token == JsDocToken.LC) {
/*1693*/      skipEOLs();
/*1694*/      Node typeNode = parseTypeName(next());
/*1695*/      if (typeNode != null) {
/*1696*/        skipEOLs();
/*1697*/        if (!match(JsDocToken.RC)) {
/*1698*/          reportTypeSyntaxWarning("msg.jsdoc.missing.rc");
/*   0*/        } else {
/*1700*/          next();
/*   0*/        } 
/*   0*/      } 
/*1704*/      return typeNode;
/*   0*/    } 
/*1706*/    return parseTypeName(token);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTopLevelTypeExpression(JsDocToken token) {
/*1717*/    Node typeExpr = parseTypeExpression(token);
/*1718*/    if (typeExpr != null)
/*1720*/      if (match(JsDocToken.PIPE)) {
/*1721*/        next();
/*1722*/        if (match(JsDocToken.PIPE))
/*1724*/          next(); 
/*1726*/        skipEOLs();
/*1727*/        token = next();
/*1728*/        return parseUnionTypeWithAlternate(token, typeExpr);
/*   0*/      }  
/*1731*/    return typeExpr;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeExpressionList(JsDocToken token) {
/*1739*/    Node typeExpr = parseTopLevelTypeExpression(token);
/*1740*/    if (typeExpr == null)
/*1741*/      return null; 
/*1743*/    Node typeList = IR.block();
/*1744*/    typeList.addChildToBack(typeExpr);
/*1745*/    while (match(JsDocToken.COMMA)) {
/*1746*/      next();
/*1747*/      skipEOLs();
/*1748*/      typeExpr = parseTopLevelTypeExpression(next());
/*1749*/      if (typeExpr == null)
/*1750*/        return null; 
/*1752*/      typeList.addChildToBack(typeExpr);
/*   0*/    } 
/*1754*/    return typeList;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeExpression(JsDocToken token) {
/*1766*/    if (token == JsDocToken.QMARK) {
/*1779*/      token = next();
/*1780*/      if (token == JsDocToken.COMMA || token == JsDocToken.EQUALS || token == JsDocToken.RB || token == JsDocToken.RC || token == JsDocToken.RP || token == JsDocToken.PIPE || token == JsDocToken.GT) {
/*1787*/        restoreLookAhead(token);
/*1788*/        return newNode(304);
/*   0*/      } 
/*1791*/      return wrapNode(304, parseBasicTypeExpression(token));
/*   0*/    } 
/*1792*/    if (token == JsDocToken.BANG)
/*1793*/      return wrapNode(306, parseBasicTypeExpression(next())); 
/*1795*/    Node basicTypeExpr = parseBasicTypeExpression(token);
/*1796*/    if (basicTypeExpr != null) {
/*1797*/      if (match(JsDocToken.QMARK)) {
/*1798*/        next();
/*1799*/        return wrapNode(304, basicTypeExpr);
/*   0*/      } 
/*1800*/      if (match(JsDocToken.BANG)) {
/*1801*/        next();
/*1802*/        return wrapNode(306, basicTypeExpr);
/*   0*/      } 
/*   0*/    } 
/*1806*/    return basicTypeExpr;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseBasicTypeExpression(JsDocToken token) {
/*1815*/    if (token == JsDocToken.STAR)
/*1816*/      return newNode(302); 
/*1817*/    if (token == JsDocToken.LB) {
/*1818*/      skipEOLs();
/*1819*/      return parseArrayType(next());
/*   0*/    } 
/*1820*/    if (token == JsDocToken.LC) {
/*1821*/      skipEOLs();
/*1822*/      return parseRecordType(next());
/*   0*/    } 
/*1823*/    if (token == JsDocToken.LP) {
/*1824*/      skipEOLs();
/*1825*/      return parseUnionType(next());
/*   0*/    } 
/*1826*/    if (token == JsDocToken.STRING) {
/*1827*/      String string = this.stream.getString();
/*1828*/      if ("function".equals(string)) {
/*1829*/        skipEOLs();
/*1830*/        return parseFunctionType(next());
/*   0*/      } 
/*1831*/      if ("null".equals(string) || "undefined".equals(string))
/*1832*/        return newStringNode(string); 
/*1834*/      return parseTypeName(token);
/*   0*/    } 
/*1838*/    restoreLookAhead(token);
/*1839*/    return reportGenericTypeSyntaxWarning();
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeName(JsDocToken token) {
/*1847*/    if (token != JsDocToken.STRING)
/*1848*/      return reportGenericTypeSyntaxWarning(); 
/*1851*/    String typeName = this.stream.getString();
/*1852*/    int lineno = this.stream.getLineno();
/*1853*/    int charno = this.stream.getCharno();
/*1854*/    while (match(JsDocToken.EOL) && typeName.charAt(typeName.length() - 1) == '.') {
/*1856*/      skipEOLs();
/*1857*/      if (match(JsDocToken.STRING)) {
/*1858*/        next();
/*1859*/        typeName = typeName + this.stream.getString();
/*   0*/      } 
/*   0*/    } 
/*1863*/    Node typeNameNode = newStringNode(typeName, lineno, charno);
/*1865*/    if (match(JsDocToken.LT)) {
/*1866*/      next();
/*1867*/      skipEOLs();
/*1868*/      Node memberType = parseTypeExpressionList(next());
/*1869*/      if (memberType != null) {
/*1870*/        typeNameNode.addChildToFront(memberType);
/*1872*/        skipEOLs();
/*1873*/        if (!match(JsDocToken.GT))
/*1874*/          return reportTypeSyntaxWarning("msg.jsdoc.missing.gt"); 
/*1877*/        next();
/*   0*/      } 
/*   0*/    } 
/*1880*/    return typeNameNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFunctionType(JsDocToken token) {
/*1891*/    if (token != JsDocToken.LP) {
/*1892*/      restoreLookAhead(token);
/*1893*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.lp");
/*   0*/    } 
/*1896*/    Node functionType = newNode(105);
/*1897*/    Node parameters = null;
/*1898*/    skipEOLs();
/*1899*/    if (!match(JsDocToken.RP)) {
/*1900*/      token = next();
/*   0*/      boolean hasParams = true;
/*1903*/      if (token == JsDocToken.STRING) {
/*1904*/        String tokenStr = this.stream.getString();
/*1905*/        boolean isThis = "this".equals(tokenStr);
/*1906*/        boolean isNew = "new".equals(tokenStr);
/*1907*/        if (isThis || isNew) {
/*1908*/          if (match(JsDocToken.COLON)) {
/*1909*/            next();
/*1910*/            skipEOLs();
/*1911*/            Node contextType = wrapNode(isThis ? 42 : 30, parseTypeName(next()));
/*1914*/            if (contextType == null)
/*1915*/              return null; 
/*1918*/            functionType.addChildToFront(contextType);
/*   0*/          } else {
/*1920*/            return reportTypeSyntaxWarning("msg.jsdoc.missing.colon");
/*   0*/          } 
/*1923*/          if (match(JsDocToken.COMMA)) {
/*1924*/            next();
/*1925*/            skipEOLs();
/*1926*/            token = next();
/*   0*/          } else {
/*1928*/            hasParams = false;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1933*/      if (hasParams) {
/*1934*/        parameters = parseParametersType(token);
/*1935*/        if (parameters == null)
/*1936*/          return null; 
/*   0*/      } 
/*   0*/    } 
/*1941*/    if (parameters != null)
/*1942*/      functionType.addChildToBack(parameters); 
/*1945*/    skipEOLs();
/*1946*/    if (!match(JsDocToken.RP))
/*1947*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.rp"); 
/*1950*/    skipEOLs();
/*1951*/    Node resultType = parseResultType(next());
/*1952*/    if (resultType == null)
/*1953*/      return null; 
/*1955*/    functionType.addChildToBack(resultType);
/*1957*/    return functionType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseParametersType(JsDocToken token) {
/*1983*/    Node paramsType = newNode(83);
/*   0*/    boolean isVarArgs = false;
/*1985*/    Node paramType = null;
/*1986*/    if (token != JsDocToken.RP)
/*   0*/      do {
/*1988*/        if (paramType != null) {
/*1990*/          next();
/*1991*/          skipEOLs();
/*1992*/          token = next();
/*   0*/        } 
/*1995*/        if (token == JsDocToken.ELLIPSIS) {
/*1999*/          skipEOLs();
/*2000*/          if (match(JsDocToken.RP)) {
/*2001*/            paramType = newNode(305);
/*   0*/          } else {
/*2003*/            skipEOLs();
/*2004*/            if (!match(JsDocToken.LB))
/*2005*/              return reportTypeSyntaxWarning("msg.jsdoc.missing.lb"); 
/*2008*/            next();
/*2009*/            skipEOLs();
/*2010*/            paramType = wrapNode(305, parseTypeExpression(next()));
/*2011*/            skipEOLs();
/*2012*/            if (!match(JsDocToken.RB))
/*2013*/              return reportTypeSyntaxWarning("msg.jsdoc.missing.rb"); 
/*2015*/            skipEOLs();
/*2016*/            next();
/*   0*/          } 
/*2019*/          isVarArgs = true;
/*   0*/        } else {
/*2021*/          paramType = parseTypeExpression(token);
/*2022*/          if (match(JsDocToken.EQUALS)) {
/*2023*/            skipEOLs();
/*2024*/            next();
/*2025*/            paramType = wrapNode(307, paramType);
/*   0*/          } 
/*   0*/        } 
/*2029*/        if (paramType == null)
/*2030*/          return null; 
/*2032*/        paramsType.addChildToBack(paramType);
/*2033*/        if (isVarArgs)
/*   0*/          break; 
/*2036*/      } while (match(JsDocToken.COMMA)); 
/*2039*/    if (isVarArgs && match(JsDocToken.COMMA))
/*2040*/      return reportTypeSyntaxWarning("msg.jsdoc.function.varargs"); 
/*2045*/    return paramsType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseResultType(JsDocToken token) {
/*2052*/    skipEOLs();
/*2053*/    if (!match(JsDocToken.COLON))
/*2054*/      return newNode(124); 
/*2057*/    token = next();
/*2058*/    skipEOLs();
/*2059*/    if (match(JsDocToken.STRING) && "void".equals(this.stream.getString())) {
/*2060*/      next();
/*2061*/      return newNode(122);
/*   0*/    } 
/*2063*/    return parseTypeExpression(next());
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseUnionType(JsDocToken token) {
/*2074*/    return parseUnionTypeWithAlternate(token, null);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseUnionTypeWithAlternate(JsDocToken token, Node alternate) {
/*2082*/    Node union = newNode(301);
/*2083*/    if (alternate != null)
/*2084*/      union.addChildToBack(alternate); 
/*2087*/    Node expr = null;
/*   0*/    do {
/*2089*/      if (expr != null) {
/*2090*/        skipEOLs();
/*2091*/        token = next();
/*2092*/        Preconditions.checkState((token == JsDocToken.PIPE || token == JsDocToken.COMMA));
/*2095*/        boolean isPipe = (token == JsDocToken.PIPE);
/*2096*/        if (isPipe && match(JsDocToken.PIPE))
/*2098*/          next(); 
/*2100*/        skipEOLs();
/*2101*/        token = next();
/*   0*/      } 
/*2103*/      expr = parseTypeExpression(token);
/*2104*/      if (expr == null)
/*2105*/        return null; 
/*2108*/      union.addChildToBack(expr);
/*2110*/    } while (match(JsDocToken.PIPE, JsDocToken.COMMA));
/*2112*/    if (alternate == null) {
/*2113*/      skipEOLs();
/*2114*/      if (!match(JsDocToken.RP))
/*2115*/        return reportTypeSyntaxWarning("msg.jsdoc.missing.rp"); 
/*2117*/      next();
/*   0*/    } 
/*2119*/    return union;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseArrayType(JsDocToken token) {
/*2128*/    Node array = newNode(308);
/*2129*/    Node arg = null;
/*   0*/    boolean hasVarArgs = false;
/*   0*/    do {
/*2133*/      if (arg != null) {
/*2134*/        next();
/*2135*/        skipEOLs();
/*2136*/        token = next();
/*   0*/      } 
/*2138*/      if (token == JsDocToken.ELLIPSIS) {
/*2139*/        arg = wrapNode(305, parseTypeExpression(next()));
/*2140*/        hasVarArgs = true;
/*   0*/      } else {
/*2142*/        arg = parseTypeExpression(token);
/*   0*/      } 
/*2145*/      if (arg == null)
/*2146*/        return null; 
/*2149*/      array.addChildToBack(arg);
/*2150*/      if (hasVarArgs)
/*   0*/        break; 
/*2153*/      skipEOLs();
/*2154*/    } while (match(JsDocToken.COMMA));
/*2156*/    if (!match(JsDocToken.RB))
/*2157*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.rb"); 
/*2159*/    next();
/*2160*/    return array;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseRecordType(JsDocToken token) {
/*2167*/    Node recordType = newNode(309);
/*2168*/    Node fieldTypeList = parseFieldTypeList(token);
/*2170*/    if (fieldTypeList == null)
/*2171*/      return reportGenericTypeSyntaxWarning(); 
/*2174*/    skipEOLs();
/*2175*/    if (!match(JsDocToken.RC))
/*2176*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.rc"); 
/*2179*/    next();
/*2181*/    recordType.addChildToBack(fieldTypeList);
/*2182*/    return recordType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFieldTypeList(JsDocToken token) {
/*2189*/    Node fieldTypeList = newNode(308);
/*   0*/    while (true) {
/*2192*/      Node fieldType = parseFieldType(token);
/*2194*/      if (fieldType == null)
/*2195*/        return null; 
/*2198*/      fieldTypeList.addChildToBack(fieldType);
/*2200*/      skipEOLs();
/*2201*/      if (!match(JsDocToken.COMMA))
/*   0*/        break; 
/*2206*/      next();
/*2209*/      skipEOLs();
/*2210*/      token = next();
/*   0*/    } 
/*2213*/    return fieldTypeList;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFieldType(JsDocToken token) {
/*2220*/    Node fieldName = parseFieldName(token);
/*2222*/    if (fieldName == null)
/*2223*/      return null; 
/*2226*/    skipEOLs();
/*2227*/    if (!match(JsDocToken.COLON))
/*2228*/      return fieldName; 
/*2232*/    next();
/*2236*/    skipEOLs();
/*2237*/    Node typeExpression = parseTypeExpression(next());
/*2239*/    if (typeExpression == null)
/*2240*/      return null; 
/*2243*/    Node fieldType = newNode(310);
/*2244*/    fieldType.addChildToBack(fieldName);
/*2245*/    fieldType.addChildToBack(typeExpression);
/*2246*/    return fieldType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFieldName(JsDocToken token) {
/*   0*/    String string;
/*2254*/    switch (token) {
/*   0*/      case STRING:
/*2256*/        string = this.stream.getString();
/*2257*/        return newStringNode(string);
/*   0*/    } 
/*2260*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private Node wrapNode(int type, Node n) {
/*2265*/    return (n == null) ? null : new Node(type, n, this.stream.getLineno(), this.stream.getCharno()).clonePropsFrom(this.templateNode);
/*   0*/  }
/*   0*/  
/*   0*/  private Node newNode(int type) {
/*2271*/    return new Node(type, this.stream.getLineno(), this.stream.getCharno()).clonePropsFrom(this.templateNode);
/*   0*/  }
/*   0*/  
/*   0*/  private Node newStringNode(String s) {
/*2276*/    return newStringNode(s, this.stream.getLineno(), this.stream.getCharno());
/*   0*/  }
/*   0*/  
/*   0*/  private Node newStringNode(String s, int lineno, int charno) {
/*2280*/    Node n = Node.newString(s, lineno, charno).clonePropsFrom(this.templateNode);
/*2281*/    n.setLength(s.length());
/*2282*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  private Node createTemplateNode() {
/*2289*/    Node templateNode = IR.script();
/*2290*/    templateNode.setStaticSourceFile((this.associatedNode != null) ? this.associatedNode.getStaticSourceFile() : null);
/*2294*/    return templateNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node reportTypeSyntaxWarning(String warning) {
/*2298*/    this.parser.addTypeWarning(warning, this.stream.getLineno(), this.stream.getCharno());
/*2299*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private Node reportGenericTypeSyntaxWarning() {
/*2303*/    return reportTypeSyntaxWarning("msg.jsdoc.type.syntax");
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken eatTokensUntilEOL() {
/*2311*/    return eatTokensUntilEOL(next());
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken eatTokensUntilEOL(JsDocToken token) {
/*   0*/    while (true) {
/*2320*/      if (token == JsDocToken.EOL || token == JsDocToken.EOC || token == JsDocToken.EOF) {
/*2322*/        this.state = State.SEARCHING_ANNOTATION;
/*2323*/        return token;
/*   0*/      } 
/*2325*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*2332*/  private static final JsDocToken NO_UNREAD_TOKEN = null;
/*   0*/  
/*   0*/  private JsDocToken unreadToken;
/*   0*/  
/*   0*/  JsDocInfoParser(JsDocTokenStream stream, Comment commentNode, Node associatedNode, Config config, ErrorReporter errorReporter) {
/*2337*/    this.unreadToken = NO_UNREAD_TOKEN;
/*   0*/    this.stream = stream;
/*   0*/    this.associatedNode = associatedNode;
/*   0*/    this.sourceFile = (associatedNode == null) ? null : associatedNode.getStaticSourceFile();
/*   0*/    this.jsdocBuilder = new JSDocInfoBuilder(config.parseJsDocDocumentation);
/*   0*/    if (commentNode != null)
/*   0*/      this.jsdocBuilder.recordOriginalCommentString(commentNode.getValue()); 
/*   0*/    this.annotationNames = config.annotationNames;
/*   0*/    this.suppressionNames = config.suppressionNames;
/*   0*/    this.errorReporter = errorReporter;
/*   0*/    this.templateNode = createTemplateNode();
/*   0*/  }
/*   0*/  
/*   0*/  private void restoreLookAhead(JsDocToken token) {
/*2341*/    this.unreadToken = token;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean match(JsDocToken token) {
/*2349*/    this.unreadToken = next();
/*2350*/    return (this.unreadToken == token);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean match(JsDocToken token1, JsDocToken token2) {
/*2358*/    this.unreadToken = next();
/*2359*/    return (this.unreadToken == token1 || this.unreadToken == token2);
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken next() {
/*2367*/    if (this.unreadToken == NO_UNREAD_TOKEN)
/*2368*/      return this.stream.getJsDocToken(); 
/*2370*/    return current();
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken current() {
/*2378*/    JsDocToken t = this.unreadToken;
/*2379*/    this.unreadToken = NO_UNREAD_TOKEN;
/*2380*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  private void skipEOLs() {
/*2388*/    while (match(JsDocToken.EOL)) {
/*2389*/      next();
/*2390*/      if (match(JsDocToken.STAR))
/*2391*/        next(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private String getRemainingJSDocLine() {
/*2400*/    String result = this.stream.getRemainingJSDocLine();
/*2401*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean hasParsedFileOverviewDocInfo() {
/*2409*/    return this.jsdocBuilder.isPopulatedWithFileOverview();
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasParsedJSDocInfo() {
/*2413*/    return this.jsdocBuilder.isPopulated();
/*   0*/  }
/*   0*/  
/*   0*/  JSDocInfo retrieveAndResetParsedJSDocInfo() {
/*2417*/    return this.jsdocBuilder.build(this.associatedNode);
/*   0*/  }
/*   0*/  
/*   0*/  JSDocInfo getFileOverviewJSDocInfo() {
/*2424*/    return this.fileOverviewJSDocInfo;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean lookAheadForTypeAnnotation() {
/*   0*/    int c;
/*   0*/    boolean matchedLc = false;
/*   0*/    while (true) {
/*2439*/      c = this.stream.getChar();
/*2440*/      if (c == 32)
/*   0*/        continue; 
/*   0*/      break;
/*   0*/    } 
/*2442*/    if (c == 123)
/*2443*/      matchedLc = true; 
/*2449*/    this.stream.ungetChar(c);
/*2450*/    return matchedLc;
/*   0*/  }
/*   0*/}
