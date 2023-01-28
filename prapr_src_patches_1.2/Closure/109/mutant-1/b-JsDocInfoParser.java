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
/*   0*/import com.google.javascript.rhino.SimpleErrorReporter;
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
/*  64*/      JsDocInfoParser.this.errorReporter.warning(SimpleErrorReporter.getMessage1(messageId, messageArg), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/    
/*   0*/    void addParserWarning(String messageId, int lineno, int charno) {
/*  70*/      JsDocInfoParser.this.errorReporter.warning(SimpleErrorReporter.getMessage0(messageId), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/    
/*   0*/    void addTypeWarning(String messageId, String messageArg, int lineno, int charno) {
/*  76*/      JsDocInfoParser.this.errorReporter.warning("Bad type annotation. " + SimpleErrorReporter.getMessage1(messageId, messageArg), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/    
/*   0*/    void addTypeWarning(String messageId, int lineno, int charno) {
/*  83*/      JsDocInfoParser.this.errorReporter.warning("Bad type annotation. " + SimpleErrorReporter.getMessage0(messageId), JsDocInfoParser.this.getSourceName(), lineno, null, charno);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*  91*/  private JSDocInfo fileOverviewJSDocInfo = null;
/*   0*/  
/*   0*/  private State state;
/*   0*/  
/*   0*/  private final Map<String, Annotation> annotationNames;
/*   0*/  
/*   0*/  private final Set<String> suppressionNames;
/*   0*/  
/*  96*/  private static final Set<String> modifiesAnnotationKeywords = (Set<String>)ImmutableSet.of("this", "arguments");
/*   0*/  
/*  98*/  private static final Set<String> idGeneratorAnnotationKeywords = (Set<String>)ImmutableSet.of("unique", "consistent", "stable", "mapped");
/*   0*/  
/*   0*/  private Node.FileLevelJsDocBuilder fileLevelJsDocBuilder;
/*   0*/  
/*   0*/  void setFileLevelJsDocBuilder(Node.FileLevelJsDocBuilder fileLevelJsDocBuilder) {
/* 112*/    this.fileLevelJsDocBuilder = fileLevelJsDocBuilder;
/*   0*/  }
/*   0*/  
/*   0*/  void setFileOverviewJSDocInfo(JSDocInfo fileOverviewJSDocInfo) {
/* 120*/    this.fileOverviewJSDocInfo = fileOverviewJSDocInfo;
/*   0*/  }
/*   0*/  
/*   0*/  private enum State {
/* 124*/    SEARCHING_ANNOTATION, SEARCHING_NEWLINE, NEXT_IS_ANNOTATION;
/*   0*/  }
/*   0*/  
/*   0*/  private String getSourceName() {
/* 154*/    return (this.sourceFile == null) ? null : this.sourceFile.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo parseInlineTypeDoc() {
/* 161*/    skipEOLs();
/* 163*/    JsDocToken token = next();
/* 164*/    int lineno = this.stream.getLineno();
/* 165*/    int startCharno = this.stream.getCharno();
/* 166*/    Node typeAst = parseTypeExpression(token);
/* 167*/    recordTypeNode(lineno, startCharno, typeAst, (token == JsDocToken.LC));
/* 169*/    JSTypeExpression expr = createJSTypeExpression(typeAst);
/* 170*/    if (expr != null) {
/* 171*/      this.jsdocBuilder.recordType(expr);
/* 172*/      return retrieveAndResetParsedJSDocInfo();
/*   0*/    } 
/* 174*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private void recordTypeNode(int lineno, int startCharno, Node typeAst, boolean matchingLC) {
/* 179*/    if (typeAst != null) {
/* 180*/      int endLineno = this.stream.getLineno();
/* 181*/      int endCharno = this.stream.getCharno();
/* 182*/      this.jsdocBuilder.markTypeNode(typeAst, lineno, startCharno, endLineno, endCharno, matchingLC);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node parseTypeString(String typeString) {
/* 192*/    Config config = new Config(Sets.newHashSet(), Sets.newHashSet(), false, Config.LanguageMode.ECMASCRIPT3, false);
/* 198*/    JsDocInfoParser parser = new JsDocInfoParser(new JsDocTokenStream(typeString), null, null, config, NullErrorReporter.forNewRhino());
/* 205*/    return parser.parseTopLevelTypeExpression(parser.next());
/*   0*/  }
/*   0*/  
/*   0*/  boolean parse() {
/* 217*/    this.state = State.SEARCHING_ANNOTATION;
/* 218*/    skipEOLs();
/* 220*/    JsDocToken token = next();
/* 223*/    if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 224*/      ExtractionInfo blockInfo = extractBlockComment(token);
/* 225*/      token = blockInfo.token;
/* 226*/      if (!blockInfo.string.isEmpty())
/* 227*/        this.jsdocBuilder.recordBlockDescription(blockInfo.string); 
/* 230*/    } else if (token != JsDocToken.ANNOTATION && token != JsDocToken.EOC) {
/* 234*/      this.jsdocBuilder.recordBlockDescription("");
/*   0*/    } 
/* 238*/    return parseHelperLoop(token, Lists.newArrayList());
/*   0*/  }
/*   0*/  
/*   0*/  private boolean parseHelperLoop(JsDocToken token, List<ExtendedTypeInfo> extendedTypes) {
/*   0*/    while (true) {
/* 244*/      switch (token) {
/*   0*/        case ANNOTATION:
/* 246*/          if (this.state == State.SEARCHING_ANNOTATION) {
/* 247*/            this.state = State.SEARCHING_NEWLINE;
/* 248*/            token = parseAnnotation(token, extendedTypes);
/*   0*/            continue;
/*   0*/          } 
/* 250*/          token = next();
/*   0*/          continue;
/*   0*/        case EOC:
/* 255*/          if (hasParsedFileOverviewDocInfo())
/* 256*/            this.fileOverviewJSDocInfo = retrieveAndResetParsedJSDocInfo(); 
/* 258*/          checkExtendedTypes(extendedTypes);
/* 259*/          return true;
/*   0*/        case EOF:
/* 263*/          this.jsdocBuilder.build(null);
/* 264*/          this.parser.addParserWarning("msg.unexpected.eof", this.stream.getLineno(), this.stream.getCharno());
/* 266*/          checkExtendedTypes(extendedTypes);
/* 267*/          return false;
/*   0*/        case EOL:
/* 270*/          if (this.state == State.SEARCHING_NEWLINE)
/* 271*/            this.state = State.SEARCHING_ANNOTATION; 
/* 273*/          token = next();
/*   0*/          continue;
/*   0*/      } 
/* 277*/      if (token == JsDocToken.STAR && this.state == State.SEARCHING_ANNOTATION) {
/* 278*/        token = next();
/*   0*/        continue;
/*   0*/      } 
/* 280*/      this.state = State.SEARCHING_NEWLINE;
/* 281*/      token = eatTokensUntilEOL();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken parseAnnotation(JsDocToken token, List<ExtendedTypeInfo> extendedTypes) {
/* 292*/    int lineno = this.stream.getLineno();
/* 293*/    int charno = this.stream.getCharno();
/* 295*/    String annotationName = this.stream.getString();
/* 296*/    Annotation annotation = this.annotationNames.get(annotationName);
/* 297*/    if (annotation == null) {
/* 298*/      this.parser.addParserWarning("msg.bad.jsdoc.tag", annotationName, this.stream.getLineno(), this.stream.getCharno());
/*   0*/    } else {
/*   0*/      JSTypeExpression type;
/*   0*/      ExtractionInfo reasonInfo;
/*   0*/      String reason;
/*   0*/      ExtractionInfo descriptionInfo;
/*   0*/      String fileOverview, description;
/*   0*/      ExtractionInfo preserveInfo;
/*   0*/      String preserve;
/*   0*/      boolean matchingRc;
/*   0*/      ExtractionInfo meaningInfo;
/*   0*/      String meaning;
/*   0*/      boolean isAnnotationNext;
/*   0*/      String name;
/*   0*/      boolean isBracketedParam;
/*   0*/      ExtractionInfo templateInfo, versionInfo;
/*   0*/      List<String> names;
/*   0*/      String version;
/*   0*/      Node typeNode;
/*   0*/      boolean hasType, isAlternateTypeAnnotation, canSkipTypeAnnotation, hasError;
/* 302*/      this.jsdocBuilder.markAnnotation(annotationName, lineno, charno);
/* 304*/      switch (annotation) {
/*   0*/        case NG_INJECT:
/* 306*/          if (this.jsdocBuilder.isNgInjectRecorded()) {
/* 307*/            this.parser.addParserWarning("msg.jsdoc.nginject.extra", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } else {
/* 310*/            this.jsdocBuilder.recordNgInject(true);
/*   0*/          } 
/* 312*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case JAGGER_INJECT:
/* 315*/          if (this.jsdocBuilder.isJaggerInjectRecorded()) {
/* 316*/            this.parser.addParserWarning("msg.jsdoc.jaggerInject.extra", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } else {
/* 319*/            this.jsdocBuilder.recordJaggerInject(true);
/*   0*/          } 
/* 321*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case JAGGER_MODULE:
/* 324*/          if (this.jsdocBuilder.isJaggerModuleRecorded()) {
/* 325*/            this.parser.addParserWarning("msg.jsdoc.jaggerModule.extra", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } else {
/* 328*/            this.jsdocBuilder.recordJaggerModule(true);
/*   0*/          } 
/* 330*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case JAGGER_PROVIDE:
/* 333*/          if (this.jsdocBuilder.isJaggerProvideRecorded()) {
/* 334*/            this.parser.addParserWarning("msg.jsdoc.jaggerProvide.extra", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } else {
/* 337*/            this.jsdocBuilder.recordJaggerProvide(true);
/*   0*/          } 
/* 339*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case AUTHOR:
/* 342*/          if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 343*/            ExtractionInfo authorInfo = extractSingleLineBlock();
/* 344*/            String author = authorInfo.string;
/* 346*/            if (author.length() == 0) {
/* 347*/              this.parser.addParserWarning("msg.jsdoc.authormissing", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            } else {
/* 350*/              this.jsdocBuilder.addAuthor(author);
/*   0*/            } 
/* 352*/            token = authorInfo.token;
/*   0*/          } else {
/* 354*/            token = eatUntilEOLIfNotAnnotation();
/*   0*/          } 
/* 356*/          return token;
/*   0*/        case CONSISTENTIDGENERATOR:
/* 359*/          if (!this.jsdocBuilder.recordConsistentIdGenerator())
/* 360*/            this.parser.addParserWarning("msg.jsdoc.consistidgen", this.stream.getLineno(), this.stream.getCharno()); 
/* 363*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case STRUCT:
/* 366*/          if (!this.jsdocBuilder.recordStruct())
/* 367*/            this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno()); 
/* 371*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case DICT:
/* 374*/          if (!this.jsdocBuilder.recordDict())
/* 375*/            this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno()); 
/* 379*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case CONSTRUCTOR:
/* 382*/          if (!this.jsdocBuilder.recordConstructor())
/* 383*/            if (this.jsdocBuilder.isInterfaceRecorded()) {
/* 384*/              this.parser.addTypeWarning("msg.jsdoc.interface.constructor", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            } else {
/* 387*/              this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            }  
/* 391*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case DEPRECATED:
/* 394*/          if (!this.jsdocBuilder.recordDeprecated())
/* 395*/            this.parser.addParserWarning("msg.jsdoc.deprecated", this.stream.getLineno(), this.stream.getCharno()); 
/* 400*/          reasonInfo = extractMultilineTextualBlock(token);
/* 403*/          reason = reasonInfo.string;
/* 405*/          if (reason.length() > 0)
/* 406*/            this.jsdocBuilder.recordDeprecationReason(reason); 
/* 409*/          token = reasonInfo.token;
/* 410*/          return token;
/*   0*/        case INTERFACE:
/* 413*/          if (!this.jsdocBuilder.recordInterface())
/* 414*/            if (this.jsdocBuilder.isConstructorRecorded()) {
/* 415*/              this.parser.addTypeWarning("msg.jsdoc.interface.constructor", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            } else {
/* 418*/              this.parser.addTypeWarning("msg.jsdoc.incompat.type", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            }  
/* 422*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case DESC:
/* 425*/          if (this.jsdocBuilder.isDescriptionRecorded()) {
/* 426*/            this.parser.addParserWarning("msg.jsdoc.desc.extra", this.stream.getLineno(), this.stream.getCharno());
/* 428*/            return eatUntilEOLIfNotAnnotation();
/*   0*/          } 
/* 430*/          descriptionInfo = extractMultilineTextualBlock(token);
/* 433*/          description = descriptionInfo.string;
/* 435*/          this.jsdocBuilder.recordDescription(description);
/* 436*/          token = descriptionInfo.token;
/* 437*/          return token;
/*   0*/        case FILE_OVERVIEW:
/* 441*/          fileOverview = "";
/* 442*/          if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 443*/            ExtractionInfo fileOverviewInfo = extractMultilineTextualBlock(token, WhitespaceOption.TRIM);
/* 447*/            fileOverview = fileOverviewInfo.string;
/* 449*/            token = fileOverviewInfo.token;
/*   0*/          } else {
/* 451*/            token = eatTokensUntilEOL(token);
/*   0*/          } 
/* 454*/          if (!this.jsdocBuilder.recordFileOverview(fileOverview))
/* 455*/            this.parser.addParserWarning("msg.jsdoc.fileoverview.extra", this.stream.getLineno(), this.stream.getCharno()); 
/* 458*/          return token;
/*   0*/        case LICENSE:
/*   0*/        case PRESERVE:
/* 462*/          preserveInfo = extractMultilineTextualBlock(token, WhitespaceOption.PRESERVE);
/* 466*/          preserve = preserveInfo.string;
/* 468*/          if (preserve.length() > 0 && 
/* 469*/            this.fileLevelJsDocBuilder != null)
/* 470*/            this.fileLevelJsDocBuilder.append(preserve); 
/* 474*/          token = preserveInfo.token;
/* 475*/          return token;
/*   0*/        case ENUM:
/* 478*/          token = next();
/* 479*/          lineno = this.stream.getLineno();
/* 480*/          charno = this.stream.getCharno();
/* 482*/          type = null;
/* 483*/          if (token != JsDocToken.EOL && token != JsDocToken.EOC)
/* 484*/            type = createJSTypeExpression(parseAndRecordTypeNode(token)); 
/* 488*/          if (type == null)
/* 489*/            type = createJSTypeExpression(newStringNode("number")); 
/* 491*/          if (!this.jsdocBuilder.recordEnumParameterType(type))
/* 492*/            this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno); 
/* 495*/          token = eatUntilEOLIfNotAnnotation(token);
/* 496*/          return token;
/*   0*/        case EXPORT:
/* 499*/          if (!this.jsdocBuilder.recordExport())
/* 500*/            this.parser.addParserWarning("msg.jsdoc.export", this.stream.getLineno(), this.stream.getCharno()); 
/* 503*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case EXPOSE:
/* 506*/          if (!this.jsdocBuilder.recordExpose())
/* 507*/            this.parser.addParserWarning("msg.jsdoc.expose", this.stream.getLineno(), this.stream.getCharno()); 
/* 510*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case EXTERNS:
/* 513*/          if (!this.jsdocBuilder.recordExterns())
/* 514*/            this.parser.addParserWarning("msg.jsdoc.externs", this.stream.getLineno(), this.stream.getCharno()); 
/* 517*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case JAVA_DISPATCH:
/* 520*/          if (!this.jsdocBuilder.recordJavaDispatch())
/* 521*/            this.parser.addParserWarning("msg.jsdoc.javadispatch", this.stream.getLineno(), this.stream.getCharno()); 
/* 524*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case EXTENDS:
/*   0*/        case IMPLEMENTS:
/* 528*/          skipEOLs();
/* 529*/          token = next();
/* 530*/          lineno = this.stream.getLineno();
/* 531*/          charno = this.stream.getCharno();
/* 532*/          matchingRc = false;
/* 534*/          if (token == JsDocToken.LC) {
/* 535*/            token = next();
/* 536*/            matchingRc = true;
/*   0*/          } 
/* 539*/          if (token == JsDocToken.STRING) {
/* 540*/            Node node = parseAndRecordTypeNameNode(token, lineno, charno, matchingRc);
/* 543*/            lineno = this.stream.getLineno();
/* 544*/            charno = this.stream.getCharno();
/* 546*/            node = wrapNode(306, node);
/* 547*/            type = createJSTypeExpression(node);
/* 549*/            if (annotation == Annotation.EXTENDS) {
/* 551*/              extendedTypes.add(new ExtendedTypeInfo(type, this.stream.getLineno(), this.stream.getCharno()));
/*   0*/            } else {
/* 554*/              Preconditions.checkState((annotation == Annotation.IMPLEMENTS));
/* 556*/              if (!this.jsdocBuilder.recordImplementedInterface(type))
/* 557*/                this.parser.addTypeWarning("msg.jsdoc.implements.duplicate", lineno, charno); 
/*   0*/            } 
/* 561*/            token = next();
/* 562*/            if (matchingRc) {
/* 563*/              if (token != JsDocToken.RC) {
/* 564*/                this.parser.addTypeWarning("msg.jsdoc.missing.rc", this.stream.getLineno(), this.stream.getCharno());
/*   0*/              } else {
/* 567*/                token = next();
/*   0*/              } 
/* 569*/            } else if (token != JsDocToken.EOL && token != JsDocToken.EOF && token != JsDocToken.EOC) {
/* 571*/              this.parser.addTypeWarning("msg.end.annotation.expected", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            } 
/*   0*/          } else {
/* 575*/            this.parser.addTypeWarning("msg.no.type.name", lineno, charno);
/*   0*/          } 
/* 577*/          token = eatUntilEOLIfNotAnnotation(token);
/* 578*/          return token;
/*   0*/        case HIDDEN:
/* 581*/          if (!this.jsdocBuilder.recordHiddenness())
/* 582*/            this.parser.addParserWarning("msg.jsdoc.hidden", this.stream.getLineno(), this.stream.getCharno()); 
/* 585*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case LENDS:
/* 588*/          skipEOLs();
/* 590*/          matchingRc = false;
/* 591*/          if (match(JsDocToken.LC)) {
/* 592*/            token = next();
/* 593*/            matchingRc = true;
/*   0*/          } 
/* 596*/          if (match(JsDocToken.STRING)) {
/* 597*/            token = next();
/* 598*/            if (!this.jsdocBuilder.recordLends(this.stream.getString()))
/* 599*/              this.parser.addTypeWarning("msg.jsdoc.lends.incompatible", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/          } else {
/* 603*/            this.parser.addTypeWarning("msg.jsdoc.lends.missing", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } 
/* 607*/          if (matchingRc && !match(JsDocToken.RC))
/* 608*/            this.parser.addTypeWarning("msg.jsdoc.missing.rc", this.stream.getLineno(), this.stream.getCharno()); 
/* 611*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case MEANING:
/* 614*/          meaningInfo = extractMultilineTextualBlock(token);
/* 616*/          meaning = meaningInfo.string;
/* 617*/          token = meaningInfo.token;
/* 618*/          if (!this.jsdocBuilder.recordMeaning(meaning))
/* 619*/            this.parser.addParserWarning("msg.jsdoc.meaning.extra", this.stream.getLineno(), this.stream.getCharno()); 
/* 622*/          return token;
/*   0*/        case NO_ALIAS:
/* 625*/          if (!this.jsdocBuilder.recordNoAlias())
/* 626*/            this.parser.addParserWarning("msg.jsdoc.noalias", this.stream.getLineno(), this.stream.getCharno()); 
/* 629*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case NO_COMPILE:
/* 632*/          if (!this.jsdocBuilder.recordNoCompile())
/* 633*/            this.parser.addParserWarning("msg.jsdoc.nocompile", this.stream.getLineno(), this.stream.getCharno()); 
/* 636*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case NO_TYPE_CHECK:
/* 639*/          if (!this.jsdocBuilder.recordNoTypeCheck())
/* 640*/            this.parser.addParserWarning("msg.jsdoc.nocheck", this.stream.getLineno(), this.stream.getCharno()); 
/* 643*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case NOT_IMPLEMENTED:
/* 646*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case INHERIT_DOC:
/*   0*/        case OVERRIDE:
/* 650*/          if (!this.jsdocBuilder.recordOverride())
/* 651*/            this.parser.addTypeWarning("msg.jsdoc.override", this.stream.getLineno(), this.stream.getCharno()); 
/* 654*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case THROWS:
/* 657*/          skipEOLs();
/* 658*/          token = next();
/* 659*/          lineno = this.stream.getLineno();
/* 660*/          charno = this.stream.getCharno();
/* 661*/          type = null;
/* 663*/          if (token == JsDocToken.LC) {
/* 664*/            type = createJSTypeExpression(parseAndRecordTypeNode(token));
/* 667*/            if (type == null)
/* 670*/              return eatUntilEOLIfNotAnnotation(); 
/*   0*/          } 
/* 675*/          token = current();
/* 678*/          this.jsdocBuilder.recordThrowType(type);
/* 680*/          isAnnotationNext = lookAheadForAnnotation();
/* 683*/          if (this.jsdocBuilder.shouldParseDocumentation() && !isAnnotationNext) {
/* 684*/            ExtractionInfo extractionInfo = extractMultilineTextualBlock(token);
/* 687*/            String str = extractionInfo.string;
/* 689*/            if (str.length() > 0)
/* 690*/              this.jsdocBuilder.recordThrowDescription(type, str); 
/* 693*/            token = extractionInfo.token;
/*   0*/          } else {
/* 695*/            token = eatUntilEOLIfNotAnnotation();
/*   0*/          } 
/* 697*/          return token;
/*   0*/        case PARAM:
/* 701*/          skipEOLs();
/* 702*/          token = next();
/* 703*/          lineno = this.stream.getLineno();
/* 704*/          charno = this.stream.getCharno();
/* 705*/          type = null;
/* 707*/          if (token == JsDocToken.LC) {
/* 708*/            type = createJSTypeExpression(parseAndRecordParamTypeNode(token));
/* 711*/            if (type == null)
/* 714*/              return eatUntilEOLIfNotAnnotation(); 
/* 716*/            skipEOLs();
/* 717*/            token = next();
/* 718*/            lineno = this.stream.getLineno();
/* 719*/            charno = this.stream.getCharno();
/*   0*/          } 
/* 722*/          name = null;
/* 723*/          isBracketedParam = (JsDocToken.LB == token);
/* 724*/          if (isBracketedParam)
/* 725*/            token = next(); 
/* 728*/          if (JsDocToken.STRING != token) {
/* 729*/            this.parser.addTypeWarning("msg.missing.variable.name", lineno, charno);
/*   0*/          } else {
/* 732*/            name = this.stream.getString();
/* 734*/            if (isBracketedParam) {
/* 735*/              token = next();
/* 740*/              if (JsDocToken.EQUALS == token) {
/* 741*/                token = next();
/* 742*/                if (JsDocToken.STRING == token)
/* 743*/                  token = next(); 
/*   0*/              } 
/* 747*/              if (JsDocToken.RB != token) {
/* 748*/                reportTypeSyntaxWarning("msg.jsdoc.missing.rb");
/* 749*/              } else if (type != null) {
/* 752*/                type = JSTypeExpression.makeOptionalArg(type);
/*   0*/              } 
/*   0*/            } 
/* 759*/            if (name.indexOf('.') > -1) {
/* 760*/              name = null;
/* 761*/            } else if (!this.jsdocBuilder.recordParameter(name, type)) {
/* 762*/              if (this.jsdocBuilder.hasParameter(name)) {
/* 763*/                this.parser.addTypeWarning("msg.dup.variable.name", name, lineno, charno);
/*   0*/              } else {
/* 766*/                this.parser.addTypeWarning("msg.jsdoc.incompat.type", name, lineno, charno);
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/* 772*/          if (name == null) {
/* 773*/            token = eatUntilEOLIfNotAnnotation(token);
/* 774*/            return token;
/*   0*/          } 
/* 777*/          this.jsdocBuilder.markName(name, this.sourceFile, lineno, charno);
/* 780*/          if (this.jsdocBuilder.shouldParseDocumentation() && token != JsDocToken.ANNOTATION) {
/* 782*/            ExtractionInfo paramDescriptionInfo = extractMultilineTextualBlock(token);
/* 785*/            String paramDescription = paramDescriptionInfo.string;
/* 787*/            if (paramDescription.length() > 0)
/* 788*/              this.jsdocBuilder.recordParameterDescription(name, paramDescription); 
/* 792*/            token = paramDescriptionInfo.token;
/* 793*/          } else if (token != JsDocToken.EOC && token != JsDocToken.EOF) {
/* 794*/            token = eatUntilEOLIfNotAnnotation();
/*   0*/          } 
/* 796*/          return token;
/*   0*/        case PRESERVE_TRY:
/* 799*/          if (!this.jsdocBuilder.recordPreserveTry())
/* 800*/            this.parser.addParserWarning("msg.jsdoc.preservertry", this.stream.getLineno(), this.stream.getCharno()); 
/* 803*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case NO_SHADOW:
/* 806*/          if (!this.jsdocBuilder.recordNoShadow())
/* 807*/            this.parser.addParserWarning("msg.jsdoc.noshadow", this.stream.getLineno(), this.stream.getCharno()); 
/* 810*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case NO_SIDE_EFFECTS:
/* 813*/          if (!this.jsdocBuilder.recordNoSideEffects())
/* 814*/            this.parser.addParserWarning("msg.jsdoc.nosideeffects", this.stream.getLineno(), this.stream.getCharno()); 
/* 817*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case MODIFIES:
/* 820*/          token = parseModifiesTag(next());
/* 821*/          return token;
/*   0*/        case IMPLICIT_CAST:
/* 824*/          if (!this.jsdocBuilder.recordImplicitCast())
/* 825*/            this.parser.addTypeWarning("msg.jsdoc.implicitcast", this.stream.getLineno(), this.stream.getCharno()); 
/* 828*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case SEE:
/* 831*/          if (this.jsdocBuilder.shouldParseDocumentation()) {
/* 832*/            ExtractionInfo referenceInfo = extractSingleLineBlock();
/* 833*/            String reference = referenceInfo.string;
/* 835*/            if (reference.length() == 0) {
/* 836*/              this.parser.addParserWarning("msg.jsdoc.seemissing", this.stream.getLineno(), this.stream.getCharno());
/*   0*/            } else {
/* 839*/              this.jsdocBuilder.addReference(reference);
/*   0*/            } 
/* 842*/            token = referenceInfo.token;
/*   0*/          } else {
/* 844*/            token = eatUntilEOLIfNotAnnotation();
/*   0*/          } 
/* 846*/          return token;
/*   0*/        case STABLEIDGENERATOR:
/* 849*/          if (!this.jsdocBuilder.recordStableIdGenerator())
/* 850*/            this.parser.addParserWarning("msg.jsdoc.stableidgen", this.stream.getLineno(), this.stream.getCharno()); 
/* 853*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case SUPPRESS:
/* 856*/          token = parseSuppressTag(next());
/* 857*/          return token;
/*   0*/        case TEMPLATE:
/* 860*/          templateInfo = extractSingleLineBlock();
/* 861*/          names = Lists.newArrayList(Splitter.on(',').trimResults().split(templateInfo.string));
/* 866*/          if (names.size() == 0 || ((String)names.get(0)).length() == 0) {
/* 867*/            this.parser.addTypeWarning("msg.jsdoc.templatemissing", this.stream.getLineno(), this.stream.getCharno());
/* 869*/          } else if (!this.jsdocBuilder.recordTemplateTypeNames(names)) {
/* 870*/            this.parser.addTypeWarning("msg.jsdoc.template.at.most.once", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } 
/* 874*/          token = templateInfo.token;
/* 875*/          return token;
/*   0*/        case IDGENERATOR:
/* 879*/          token = parseIdGeneratorTag(next());
/* 880*/          return token;
/*   0*/        case WIZACTION:
/* 883*/          if (!this.jsdocBuilder.recordWizaction())
/* 884*/            this.parser.addParserWarning("msg.jsdoc.wizaction", this.stream.getLineno(), this.stream.getCharno()); 
/* 887*/          return eatUntilEOLIfNotAnnotation();
/*   0*/        case DISPOSES:
/* 890*/          templateInfo = extractSingleLineBlock();
/* 891*/          names = Lists.newArrayList(Splitter.on(',').trimResults().split(templateInfo.string));
/* 896*/          if (names.size() == 0 || ((String)names.get(0)).length() == 0) {
/* 897*/            this.parser.addTypeWarning("msg.jsdoc.disposeparameter.missing", this.stream.getLineno(), this.stream.getCharno());
/* 899*/          } else if (!this.jsdocBuilder.recordDisposesParameter(names)) {
/* 900*/            this.parser.addTypeWarning("msg.jsdoc.disposeparameter.error", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } 
/* 904*/          token = templateInfo.token;
/* 905*/          return token;
/*   0*/        case VERSION:
/* 909*/          versionInfo = extractSingleLineBlock();
/* 910*/          version = versionInfo.string;
/* 912*/          if (version.length() == 0) {
/* 913*/            this.parser.addParserWarning("msg.jsdoc.versionmissing", this.stream.getLineno(), this.stream.getCharno());
/* 916*/          } else if (!this.jsdocBuilder.recordVersion(version)) {
/* 917*/            this.parser.addParserWarning("msg.jsdoc.extraversion", this.stream.getLineno(), this.stream.getCharno());
/*   0*/          } 
/* 922*/          token = versionInfo.token;
/* 923*/          return token;
/*   0*/        case CONSTANT:
/*   0*/        case DEFINE:
/*   0*/        case PRIVATE:
/*   0*/        case PROTECTED:
/*   0*/        case PUBLIC:
/*   0*/        case RETURN:
/*   0*/        case THIS:
/*   0*/        case TYPEDEF:
/*   0*/        case TYPE:
/* 934*/          lineno = this.stream.getLineno();
/* 935*/          charno = this.stream.getCharno();
/* 937*/          typeNode = null;
/* 938*/          hasType = lookAheadForType();
/* 939*/          isAlternateTypeAnnotation = (annotation == Annotation.PRIVATE || annotation == Annotation.PROTECTED || annotation == Annotation.PUBLIC || annotation == Annotation.CONSTANT);
/* 944*/          canSkipTypeAnnotation = (isAlternateTypeAnnotation || annotation == Annotation.RETURN);
/* 947*/          type = null;
/* 948*/          if (hasType || !canSkipTypeAnnotation) {
/* 949*/            skipEOLs();
/* 950*/            token = next();
/* 951*/            typeNode = parseAndRecordTypeNode(token);
/* 953*/            if (annotation == Annotation.THIS)
/* 954*/              typeNode = wrapNode(306, typeNode); 
/* 956*/            type = createJSTypeExpression(typeNode);
/*   0*/          } 
/* 961*/          hasError = (type == null && !canSkipTypeAnnotation);
/* 962*/          if (!hasError) {
/*   0*/            boolean bool;
/* 970*/            if ((type != null && isAlternateTypeAnnotation) || annotation == Annotation.TYPE)
/* 972*/              if (!this.jsdocBuilder.recordType(type))
/* 973*/                this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno);  
/* 978*/            switch (annotation) {
/*   0*/              case CONSTANT:
/* 980*/                if (!this.jsdocBuilder.recordConstancy())
/* 981*/                  this.parser.addParserWarning("msg.jsdoc.const", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/                break;
/*   0*/              case DEFINE:
/* 987*/                if (!this.jsdocBuilder.recordDefineType(type))
/* 988*/                  this.parser.addParserWarning("msg.jsdoc.define", lineno, charno); 
/* 991*/                return recordDescription(token);
/*   0*/              case PRIVATE:
/* 994*/                if (!this.jsdocBuilder.recordVisibility(JSDocInfo.Visibility.PRIVATE))
/* 995*/                  this.parser.addParserWarning("msg.jsdoc.visibility.private", lineno, charno); 
/* 999*/                return recordDescription(token);
/*   0*/              case PROTECTED:
/*1002*/                if (!this.jsdocBuilder.recordVisibility(JSDocInfo.Visibility.PROTECTED))
/*1003*/                  this.parser.addParserWarning("msg.jsdoc.visibility.protected", lineno, charno); 
/*1007*/                return recordDescription(token);
/*   0*/              case PUBLIC:
/*1010*/                if (!this.jsdocBuilder.recordVisibility(JSDocInfo.Visibility.PUBLIC))
/*1011*/                  this.parser.addParserWarning("msg.jsdoc.visibility.public", lineno, charno); 
/*1015*/                return recordDescription(token);
/*   0*/              case RETURN:
/*1018*/                if (type == null)
/*1019*/                  type = createJSTypeExpression(newNode(304)); 
/*1022*/                if (!this.jsdocBuilder.recordReturnType(type)) {
/*1023*/                  this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno);
/*   0*/                  break;
/*   0*/                } 
/*1033*/                bool = lookAheadForAnnotation();
/*1036*/                if (this.jsdocBuilder.shouldParseDocumentation() && !bool) {
/*1038*/                  ExtractionInfo returnDescriptionInfo = extractMultilineTextualBlock(token);
/*1041*/                  String returnDescription = returnDescriptionInfo.string;
/*1044*/                  if (returnDescription.length() > 0)
/*1045*/                    this.jsdocBuilder.recordReturnDescription(returnDescription); 
/*1049*/                  token = returnDescriptionInfo.token;
/*   0*/                } else {
/*1051*/                  token = eatUntilEOLIfNotAnnotation();
/*   0*/                } 
/*1053*/                return token;
/*   0*/              case THIS:
/*1056*/                if (!this.jsdocBuilder.recordThisType(type))
/*1057*/                  this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno); 
/*   0*/                break;
/*   0*/              case TYPEDEF:
/*1063*/                if (!this.jsdocBuilder.recordTypedef(type))
/*1064*/                  this.parser.addTypeWarning("msg.jsdoc.incompat.type", lineno, charno); 
/*   0*/                break;
/*   0*/            } 
/*   0*/          } 
/*1071*/          return eatUntilEOLIfNotAnnotation();
/*   0*/      } 
/*   0*/    } 
/*1075*/    return next();
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken recordDescription(JsDocToken token) {
/*1084*/    if (this.jsdocBuilder.shouldParseDocumentation()) {
/*1085*/      ExtractionInfo descriptionInfo = extractMultilineTextualBlock(token);
/*1086*/      token = descriptionInfo.token;
/*   0*/    } else {
/*1088*/      token = eatTokensUntilEOL(token);
/*   0*/    } 
/*1090*/    return token;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkExtendedTypes(List<ExtendedTypeInfo> extendedTypes) {
/*1094*/    for (ExtendedTypeInfo typeInfo : extendedTypes) {
/*1096*/      if (this.jsdocBuilder.isInterfaceRecorded()) {
/*1097*/        if (!this.jsdocBuilder.recordExtendedInterface(typeInfo.type))
/*1098*/          this.parser.addParserWarning("msg.jsdoc.extends.duplicate", typeInfo.lineno, typeInfo.charno); 
/*   0*/        continue;
/*   0*/      } 
/*1102*/      if (!this.jsdocBuilder.recordBaseType(typeInfo.type))
/*1103*/        this.parser.addTypeWarning("msg.jsdoc.incompat.type", typeInfo.lineno, typeInfo.charno); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken parseSuppressTag(JsDocToken token) {
/*1117*/    if (token == JsDocToken.LC) {
/*1118*/      Set<String> suppressions = new HashSet<String>();
/*   0*/      while (true) {
/*1120*/        if (match(JsDocToken.STRING)) {
/*1121*/          String name = this.stream.getString();
/*1122*/          if (!this.suppressionNames.contains(name))
/*1123*/            this.parser.addParserWarning("msg.jsdoc.suppress.unknown", name, this.stream.getLineno(), this.stream.getCharno()); 
/*1127*/          suppressions.add(this.stream.getString());
/*1128*/          token = next();
/*   0*/        } else {
/*1130*/          this.parser.addParserWarning("msg.jsdoc.suppress", this.stream.getLineno(), this.stream.getCharno());
/*1132*/          return token;
/*   0*/        } 
/*1135*/        if (match(JsDocToken.PIPE, JsDocToken.COMMA)) {
/*1136*/          token = next();
/*   0*/          continue;
/*   0*/        } 
/*   0*/        break;
/*   0*/      } 
/*1142*/      if (!match(JsDocToken.RC)) {
/*1143*/        this.parser.addParserWarning("msg.jsdoc.suppress", this.stream.getLineno(), this.stream.getCharno());
/*   0*/      } else {
/*1146*/        token = next();
/*1147*/        if (!this.jsdocBuilder.recordSuppressions(suppressions))
/*1148*/          this.parser.addParserWarning("msg.jsdoc.suppress.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/      } 
/*   0*/    } 
/*1153*/    return token;
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken parseModifiesTag(JsDocToken token) {
/*1163*/    if (token == JsDocToken.LC) {
/*1164*/      Set<String> modifies = new HashSet<String>();
/*   0*/      while (true) {
/*1166*/        if (match(JsDocToken.STRING)) {
/*1167*/          String name = this.stream.getString();
/*1168*/          if (!modifiesAnnotationKeywords.contains(name) && !this.jsdocBuilder.hasParameter(name))
/*1170*/            this.parser.addParserWarning("msg.jsdoc.modifies.unknown", name, this.stream.getLineno(), this.stream.getCharno()); 
/*1174*/          modifies.add(this.stream.getString());
/*1175*/          token = next();
/*   0*/        } else {
/*1177*/          this.parser.addParserWarning("msg.jsdoc.modifies", this.stream.getLineno(), this.stream.getCharno());
/*1179*/          return token;
/*   0*/        } 
/*1182*/        if (match(JsDocToken.PIPE)) {
/*1183*/          token = next();
/*   0*/          continue;
/*   0*/        } 
/*   0*/        break;
/*   0*/      } 
/*1189*/      if (!match(JsDocToken.RC)) {
/*1190*/        this.parser.addParserWarning("msg.jsdoc.modifies", this.stream.getLineno(), this.stream.getCharno());
/*   0*/      } else {
/*1193*/        token = next();
/*1194*/        if (!this.jsdocBuilder.recordModifies(modifies))
/*1195*/          this.parser.addParserWarning("msg.jsdoc.modifies.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*   0*/      } 
/*   0*/    } 
/*1200*/    return token;
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken parseIdGeneratorTag(JsDocToken token) {
/*1211*/    String idgenKind = "unique";
/*1212*/    if (token == JsDocToken.LC) {
/*1213*/      if (match(JsDocToken.STRING)) {
/*1214*/        String name = this.stream.getString();
/*1215*/        if (!idGeneratorAnnotationKeywords.contains(name) && !this.jsdocBuilder.hasParameter(name))
/*1217*/          this.parser.addParserWarning("msg.jsdoc.idgen.unknown", name, this.stream.getLineno(), this.stream.getCharno()); 
/*1221*/        idgenKind = name;
/*1222*/        token = next();
/*   0*/      } else {
/*1224*/        this.parser.addParserWarning("msg.jsdoc.idgen.bad", this.stream.getLineno(), this.stream.getCharno());
/*1226*/        return token;
/*   0*/      } 
/*1229*/      if (!match(JsDocToken.RC)) {
/*1230*/        this.parser.addParserWarning("msg.jsdoc.idgen.bad", this.stream.getLineno(), this.stream.getCharno());
/*   0*/      } else {
/*1233*/        token = next();
/*   0*/      } 
/*   0*/    } 
/*1237*/    if (idgenKind.equals("unique")) {
/*1238*/      if (!this.jsdocBuilder.recordIdGenerator())
/*1239*/        this.parser.addParserWarning("msg.jsdoc.idgen.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*1242*/    } else if (idgenKind.equals("consistent")) {
/*1243*/      if (!this.jsdocBuilder.recordConsistentIdGenerator())
/*1244*/        this.parser.addParserWarning("msg.jsdoc.idgen.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*1247*/    } else if (idgenKind.equals("stable")) {
/*1248*/      if (!this.jsdocBuilder.recordStableIdGenerator())
/*1249*/        this.parser.addParserWarning("msg.jsdoc.idgen.duplicate", this.stream.getLineno(), this.stream.getCharno()); 
/*1252*/    } else if (idgenKind.equals("mapped") && 
/*1253*/      !this.jsdocBuilder.recordMappedIdGenerator()) {
/*1254*/      this.parser.addParserWarning("msg.jsdoc.idgen.duplicate", this.stream.getLineno(), this.stream.getCharno());
/*   0*/    } 
/*1259*/    return token;
/*   0*/  }
/*   0*/  
/*   0*/  Node parseAndRecordTypeNode(JsDocToken token) {
/*1270*/    return parseAndRecordTypeNode(token, this.stream.getLineno(), this.stream.getCharno(), (token == JsDocToken.LC), false);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordTypeNameNode(JsDocToken token, int lineno, int startCharno, boolean matchingLC) {
/*1286*/    return parseAndRecordTypeNode(token, lineno, startCharno, matchingLC, true);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordParamTypeNode(JsDocToken token) {
/*1303*/    Preconditions.checkArgument((token == JsDocToken.LC));
/*1304*/    int lineno = this.stream.getLineno();
/*1305*/    int startCharno = this.stream.getCharno();
/*1307*/    Node typeNode = parseParamTypeExpressionAnnotation(token);
/*1308*/    recordTypeNode(lineno, startCharno, typeNode, true);
/*1309*/    return typeNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseAndRecordTypeNode(JsDocToken token, int lineno, int startCharno, boolean matchingLC, boolean onlyParseSimpleNames) {
/*1329*/    Node typeNode = null;
/*1331*/    if (onlyParseSimpleNames) {
/*1332*/      typeNode = parseTypeNameAnnotation(token);
/*   0*/    } else {
/*1334*/      typeNode = parseTypeExpressionAnnotation(token);
/*   0*/    } 
/*1337*/    recordTypeNode(lineno, startCharno, typeNode, matchingLC);
/*1338*/    return typeNode;
/*   0*/  }
/*   0*/  
/*   0*/  private String toString(JsDocToken token) {
/*1345*/    switch (token) {
/*   0*/      case ANNOTATION:
/*1347*/        return "@" + this.stream.getString();
/*   0*/      case BANG:
/*1350*/        return "!";
/*   0*/      case COMMA:
/*1353*/        return ",";
/*   0*/      case COLON:
/*1356*/        return ":";
/*   0*/      case GT:
/*1359*/        return ">";
/*   0*/      case LB:
/*1362*/        return "[";
/*   0*/      case LC:
/*1365*/        return "{";
/*   0*/      case LP:
/*1368*/        return "(";
/*   0*/      case LT:
/*1371*/        return ".<";
/*   0*/      case QMARK:
/*1374*/        return "?";
/*   0*/      case PIPE:
/*1377*/        return "|";
/*   0*/      case RB:
/*1380*/        return "]";
/*   0*/      case RC:
/*1383*/        return "}";
/*   0*/      case RP:
/*1386*/        return ")";
/*   0*/      case STAR:
/*1389*/        return "*";
/*   0*/      case ELLIPSIS:
/*1392*/        return "...";
/*   0*/      case EQUALS:
/*1395*/        return "=";
/*   0*/      case STRING:
/*1398*/        return this.stream.getString();
/*   0*/    } 
/*1401*/    throw new IllegalStateException(token.toString());
/*   0*/  }
/*   0*/  
/*   0*/  JSTypeExpression createJSTypeExpression(Node n) {
/*1410*/    return (n == null) ? null : new JSTypeExpression(n, getSourceName());
/*   0*/  }
/*   0*/  
/*   0*/  private static class ExtractionInfo {
/*   0*/    private final String string;
/*   0*/    
/*   0*/    private final JsDocToken token;
/*   0*/    
/*   0*/    public ExtractionInfo(String string, JsDocToken token) {
/*1424*/      this.string = string;
/*1425*/      this.token = token;
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
/*1438*/      this.type = type;
/*1439*/      this.lineno = lineno;
/*1440*/      this.charno = charno;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractSingleLineBlock() {
/*1454*/    this.stream.update();
/*1455*/    int lineno = this.stream.getLineno();
/*1456*/    int charno = this.stream.getCharno() + 1;
/*1458*/    String line = getRemainingJSDocLine().trim();
/*1461*/    if (line.length() > 0)
/*1462*/      this.jsdocBuilder.markText(line, lineno, charno, lineno, charno + line.length()); 
/*1466*/    return new ExtractionInfo(line, next());
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractMultilineTextualBlock(JsDocToken token) {
/*1470*/    return extractMultilineTextualBlock(token, WhitespaceOption.SINGLE_LINE);
/*   0*/  }
/*   0*/  
/*   0*/  private enum WhitespaceOption {
/*1478*/    PRESERVE, TRIM, SINGLE_LINE;
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractMultilineTextualBlock(JsDocToken token, WhitespaceOption option) {
/*1504*/    if (token == JsDocToken.EOC || token == JsDocToken.EOL || token == JsDocToken.EOF)
/*1506*/      return new ExtractionInfo("", token); 
/*1509*/    this.stream.update();
/*1510*/    int startLineno = this.stream.getLineno();
/*1511*/    int startCharno = this.stream.getCharno() + 1;
/*1514*/    String line = getRemainingJSDocLine();
/*1515*/    if (option != WhitespaceOption.PRESERVE)
/*1516*/      line = line.trim(); 
/*1519*/    StringBuilder builder = new StringBuilder();
/*1520*/    builder.append(line);
/*1522*/    this.state = State.SEARCHING_ANNOTATION;
/*1523*/    token = next();
/*   0*/    boolean ignoreStar = false;
/*1530*/    int lineStartChar = -1;
/*   0*/    while (true) {
/*1533*/      switch (token) {
/*   0*/        case STAR:
/*1535*/          if (ignoreStar) {
/*1537*/            lineStartChar = this.stream.getCharno() + 1;
/*   0*/          } else {
/*1540*/            if (builder.length() > 0)
/*1541*/              builder.append(' '); 
/*1544*/            builder.append('*');
/*   0*/          } 
/*1547*/          token = next();
/*   0*/          continue;
/*   0*/        case EOL:
/*1551*/          if (option != WhitespaceOption.SINGLE_LINE)
/*1552*/            builder.append("\n"); 
/*1555*/          ignoreStar = true;
/*1556*/          lineStartChar = 0;
/*1557*/          token = next();
/*   0*/          continue;
/*   0*/      } 
/*1561*/      ignoreStar = false;
/*1562*/      this.state = State.SEARCHING_ANNOTATION;
/*1564*/      boolean isEOC = (token == JsDocToken.EOC);
/*1565*/      if (!isEOC)
/*1566*/        if (lineStartChar != -1 && option == WhitespaceOption.PRESERVE) {
/*1567*/          int numSpaces = this.stream.getCharno() - lineStartChar;
/*1568*/          for (int i = 0; i < numSpaces; i++)
/*1569*/            builder.append(' '); 
/*1571*/          lineStartChar = -1;
/*1572*/        } else if (builder.length() > 0) {
/*1574*/          builder.append(' ');
/*   0*/        }  
/*1578*/      if (token == JsDocToken.EOC || token == JsDocToken.EOF || (token == JsDocToken.ANNOTATION && option != WhitespaceOption.PRESERVE)) {
/*1584*/        String multilineText = builder.toString();
/*1586*/        if (option != WhitespaceOption.PRESERVE)
/*1587*/          multilineText = multilineText.trim(); 
/*1590*/        int endLineno = this.stream.getLineno();
/*1591*/        int endCharno = this.stream.getCharno();
/*1593*/        if (multilineText.length() > 0)
/*1594*/          this.jsdocBuilder.markText(multilineText, startLineno, startCharno, endLineno, endCharno); 
/*1598*/        return new ExtractionInfo(multilineText, token);
/*   0*/      } 
/*1601*/      builder.append(toString(token));
/*1603*/      line = getRemainingJSDocLine();
/*1605*/      if (option != WhitespaceOption.PRESERVE)
/*1606*/        line = trimEnd(line); 
/*1609*/      builder.append(line);
/*1610*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private ExtractionInfo extractBlockComment(JsDocToken token) {
/*1628*/    StringBuilder builder = new StringBuilder();
/*   0*/    boolean ignoreStar = true;
/*   0*/    while (true) {
/*1633*/      switch (token) {
/*   0*/        case ANNOTATION:
/*   0*/        case EOC:
/*   0*/        case EOF:
/*1637*/          return new ExtractionInfo(builder.toString().trim(), token);
/*   0*/        case STAR:
/*1640*/          if (!ignoreStar) {
/*1641*/            if (builder.length() > 0)
/*1642*/              builder.append(' '); 
/*1645*/            builder.append('*');
/*   0*/          } 
/*1648*/          token = next();
/*   0*/          continue;
/*   0*/        case EOL:
/*1652*/          ignoreStar = true;
/*1653*/          builder.append('\n');
/*1654*/          token = next();
/*   0*/          continue;
/*   0*/      } 
/*1658*/      if (!ignoreStar && builder.length() > 0)
/*1659*/        builder.append(' '); 
/*1662*/      ignoreStar = false;
/*1664*/      builder.append(toString(token));
/*1666*/      String line = getRemainingJSDocLine();
/*1667*/      line = trimEnd(line);
/*1668*/      builder.append(line);
/*1669*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static String trimEnd(String s) {
/*1685*/    int trimCount = 0;
/*1686*/    while (trimCount < s.length()) {
/*1687*/      char ch = s.charAt(s.length() - trimCount - 1);
/*1688*/      if (Character.isWhitespace(ch))
/*1689*/        trimCount++; 
/*   0*/    } 
/*1695*/    if (trimCount == 0)
/*1696*/      return s; 
/*1698*/    return s.substring(0, s.length() - trimCount);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeExpressionAnnotation(JsDocToken token) {
/*1715*/    if (token == JsDocToken.LC) {
/*1716*/      skipEOLs();
/*1717*/      Node typeNode = parseTopLevelTypeExpression(next());
/*1718*/      if (typeNode != null) {
/*1719*/        skipEOLs();
/*1720*/        if (!match(JsDocToken.RC)) {
/*1721*/          reportTypeSyntaxWarning("msg.jsdoc.missing.rc");
/*   0*/        } else {
/*1723*/          next();
/*   0*/        } 
/*   0*/      } 
/*1727*/      return typeNode;
/*   0*/    } 
/*1729*/    return parseTypeExpression(token);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseParamTypeExpressionAnnotation(JsDocToken token) {
/*1743*/    Preconditions.checkArgument((token == JsDocToken.LC));
/*1745*/    skipEOLs();
/*   0*/    boolean restArg = false;
/*1748*/    token = next();
/*1749*/    if (token == JsDocToken.ELLIPSIS) {
/*1750*/      token = next();
/*1751*/      if (token == JsDocToken.RC)
/*1753*/        return wrapNode(305, IR.empty()); 
/*1755*/      restArg = true;
/*   0*/    } 
/*1758*/    Node typeNode = parseTopLevelTypeExpression(token);
/*1759*/    if (typeNode != null) {
/*1760*/      skipEOLs();
/*1761*/      if (restArg) {
/*1762*/        typeNode = wrapNode(305, typeNode);
/*1763*/      } else if (match(JsDocToken.EQUALS)) {
/*1764*/        next();
/*1765*/        skipEOLs();
/*1766*/        typeNode = wrapNode(307, typeNode);
/*   0*/      } 
/*1769*/      if (!match(JsDocToken.RC)) {
/*1770*/        reportTypeSyntaxWarning("msg.jsdoc.missing.rc");
/*   0*/      } else {
/*1772*/        next();
/*   0*/      } 
/*   0*/    } 
/*1776*/    return typeNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeNameAnnotation(JsDocToken token) {
/*1783*/    if (token == JsDocToken.LC) {
/*1784*/      skipEOLs();
/*1785*/      Node typeNode = parseTypeName(next());
/*1786*/      if (typeNode != null) {
/*1787*/        skipEOLs();
/*1788*/        if (!match(JsDocToken.RC)) {
/*1789*/          reportTypeSyntaxWarning("msg.jsdoc.missing.rc");
/*   0*/        } else {
/*1791*/          next();
/*   0*/        } 
/*   0*/      } 
/*1795*/      return typeNode;
/*   0*/    } 
/*1797*/    return parseTypeName(token);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTopLevelTypeExpression(JsDocToken token) {
/*1808*/    Node typeExpr = parseTypeExpression(token);
/*1809*/    if (typeExpr != null)
/*1811*/      if (match(JsDocToken.PIPE)) {
/*1812*/        next();
/*1813*/        if (match(JsDocToken.PIPE))
/*1815*/          next(); 
/*1817*/        skipEOLs();
/*1818*/        token = next();
/*1819*/        return parseUnionTypeWithAlternate(token, typeExpr);
/*   0*/      }  
/*1822*/    return typeExpr;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeExpressionList(JsDocToken token) {
/*1830*/    Node typeExpr = parseTopLevelTypeExpression(token);
/*1831*/    if (typeExpr == null)
/*1832*/      return null; 
/*1834*/    Node typeList = IR.block();
/*1835*/    typeList.addChildToBack(typeExpr);
/*1836*/    while (match(JsDocToken.COMMA)) {
/*1837*/      next();
/*1838*/      skipEOLs();
/*1839*/      typeExpr = parseTopLevelTypeExpression(next());
/*1840*/      if (typeExpr == null)
/*1841*/        return null; 
/*1843*/      typeList.addChildToBack(typeExpr);
/*   0*/    } 
/*1845*/    return typeList;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeExpression(JsDocToken token) {
/*1857*/    if (token == JsDocToken.QMARK) {
/*1871*/      token = next();
/*1872*/      if (token == JsDocToken.COMMA || token == JsDocToken.EQUALS || token == JsDocToken.RB || token == JsDocToken.RC || token == JsDocToken.RP || token == JsDocToken.PIPE || token == JsDocToken.GT || token == JsDocToken.EOC) {
/*1880*/        restoreLookAhead(token);
/*1881*/        return newNode(304);
/*   0*/      } 
/*1884*/      return wrapNode(304, parseBasicTypeExpression(token));
/*   0*/    } 
/*1885*/    if (token == JsDocToken.BANG)
/*1886*/      return wrapNode(306, parseBasicTypeExpression(next())); 
/*1888*/    Node basicTypeExpr = parseBasicTypeExpression(token);
/*1889*/    if (basicTypeExpr != null) {
/*1890*/      if (match(JsDocToken.QMARK)) {
/*1891*/        next();
/*1892*/        return wrapNode(304, basicTypeExpr);
/*   0*/      } 
/*1893*/      if (match(JsDocToken.BANG)) {
/*1894*/        next();
/*1895*/        return wrapNode(306, basicTypeExpr);
/*   0*/      } 
/*   0*/    } 
/*1899*/    return basicTypeExpr;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseContextTypeExpression(JsDocToken token) {
/*1908*/    return parseTypeName(token);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseBasicTypeExpression(JsDocToken token) {
/*1916*/    if (token == JsDocToken.STAR)
/*1917*/      return newNode(302); 
/*1918*/    if (token == JsDocToken.LB) {
/*1919*/      skipEOLs();
/*1920*/      return parseArrayType(next());
/*   0*/    } 
/*1921*/    if (token == JsDocToken.LC) {
/*1922*/      skipEOLs();
/*1923*/      return parseRecordType(next());
/*   0*/    } 
/*1924*/    if (token == JsDocToken.LP) {
/*1925*/      skipEOLs();
/*1926*/      return parseUnionType(next());
/*   0*/    } 
/*1927*/    if (token == JsDocToken.STRING) {
/*1928*/      String string = this.stream.getString();
/*1929*/      if ("function".equals(string)) {
/*1930*/        skipEOLs();
/*1931*/        return parseFunctionType(next());
/*   0*/      } 
/*1932*/      if ("null".equals(string) || "undefined".equals(string))
/*1933*/        return newStringNode(string); 
/*1935*/      return parseTypeName(token);
/*   0*/    } 
/*1939*/    restoreLookAhead(token);
/*1940*/    return reportGenericTypeSyntaxWarning();
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseTypeName(JsDocToken token) {
/*1948*/    if (token != JsDocToken.STRING)
/*1949*/      return reportGenericTypeSyntaxWarning(); 
/*1952*/    String typeName = this.stream.getString();
/*1953*/    int lineno = this.stream.getLineno();
/*1954*/    int charno = this.stream.getCharno();
/*1955*/    while (match(JsDocToken.EOL) && typeName.charAt(typeName.length() - 1) == '.') {
/*1957*/      skipEOLs();
/*1958*/      if (match(JsDocToken.STRING)) {
/*1959*/        next();
/*1960*/        typeName = typeName + this.stream.getString();
/*   0*/      } 
/*   0*/    } 
/*1964*/    Node typeNameNode = newStringNode(typeName, lineno, charno);
/*1966*/    if (match(JsDocToken.LT)) {
/*1967*/      next();
/*1968*/      skipEOLs();
/*1969*/      Node memberType = parseTypeExpressionList(next());
/*1970*/      if (memberType != null) {
/*1971*/        typeNameNode.addChildToFront(memberType);
/*1973*/        skipEOLs();
/*1974*/        if (!match(JsDocToken.GT))
/*1975*/          return reportTypeSyntaxWarning("msg.jsdoc.missing.gt"); 
/*1978*/        next();
/*   0*/      } 
/*   0*/    } 
/*1981*/    return typeNameNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFunctionType(JsDocToken token) {
/*1992*/    if (token != JsDocToken.LP) {
/*1993*/      restoreLookAhead(token);
/*1994*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.lp");
/*   0*/    } 
/*1997*/    Node functionType = newNode(105);
/*1998*/    Node parameters = null;
/*1999*/    skipEOLs();
/*2000*/    if (!match(JsDocToken.RP)) {
/*2001*/      token = next();
/*   0*/      boolean hasParams = true;
/*2004*/      if (token == JsDocToken.STRING) {
/*2005*/        String tokenStr = this.stream.getString();
/*2006*/        boolean isThis = "this".equals(tokenStr);
/*2007*/        boolean isNew = "new".equals(tokenStr);
/*2008*/        if (isThis || isNew) {
/*2009*/          if (match(JsDocToken.COLON)) {
/*2010*/            next();
/*2011*/            skipEOLs();
/*2012*/            Node contextType = wrapNode(isThis ? 42 : 30, parseTypeExpression(next()));
/*2015*/            if (contextType == null)
/*2016*/              return null; 
/*2019*/            functionType.addChildToFront(contextType);
/*   0*/          } else {
/*2021*/            return reportTypeSyntaxWarning("msg.jsdoc.missing.colon");
/*   0*/          } 
/*2024*/          if (match(JsDocToken.COMMA)) {
/*2025*/            next();
/*2026*/            skipEOLs();
/*2027*/            token = next();
/*   0*/          } else {
/*2029*/            hasParams = false;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*2034*/      if (hasParams) {
/*2035*/        parameters = parseParametersType(token);
/*2036*/        if (parameters == null)
/*2037*/          return null; 
/*   0*/      } 
/*   0*/    } 
/*2042*/    if (parameters != null)
/*2043*/      functionType.addChildToBack(parameters); 
/*2046*/    skipEOLs();
/*2047*/    if (!match(JsDocToken.RP))
/*2048*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.rp"); 
/*2051*/    skipEOLs();
/*2052*/    Node resultType = parseResultType(next());
/*2053*/    if (resultType == null)
/*2054*/      return null; 
/*2056*/    functionType.addChildToBack(resultType);
/*2058*/    return functionType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseParametersType(JsDocToken token) {
/*2084*/    Node paramsType = newNode(83);
/*   0*/    boolean isVarArgs = false;
/*2086*/    Node paramType = null;
/*2087*/    if (token != JsDocToken.RP)
/*   0*/      do {
/*2089*/        if (paramType != null) {
/*2091*/          next();
/*2092*/          skipEOLs();
/*2093*/          token = next();
/*   0*/        } 
/*2096*/        if (token == JsDocToken.ELLIPSIS) {
/*2100*/          skipEOLs();
/*2101*/          if (match(JsDocToken.RP)) {
/*2102*/            paramType = newNode(305);
/*   0*/          } else {
/*2104*/            skipEOLs();
/*2105*/            if (!match(JsDocToken.LB))
/*2106*/              return reportTypeSyntaxWarning("msg.jsdoc.missing.lb"); 
/*2109*/            next();
/*2110*/            skipEOLs();
/*2111*/            paramType = wrapNode(305, parseTypeExpression(next()));
/*2112*/            skipEOLs();
/*2113*/            if (!match(JsDocToken.RB))
/*2114*/              return reportTypeSyntaxWarning("msg.jsdoc.missing.rb"); 
/*2116*/            skipEOLs();
/*2117*/            next();
/*   0*/          } 
/*2120*/          isVarArgs = true;
/*   0*/        } else {
/*2122*/          paramType = parseTypeExpression(token);
/*2123*/          if (match(JsDocToken.EQUALS)) {
/*2124*/            skipEOLs();
/*2125*/            next();
/*2126*/            paramType = wrapNode(307, paramType);
/*   0*/          } 
/*   0*/        } 
/*2130*/        if (paramType == null)
/*2131*/          return null; 
/*2133*/        paramsType.addChildToBack(paramType);
/*2134*/        if (isVarArgs)
/*   0*/          break; 
/*2137*/      } while (match(JsDocToken.COMMA)); 
/*2140*/    if (isVarArgs && match(JsDocToken.COMMA))
/*2141*/      return reportTypeSyntaxWarning("msg.jsdoc.function.varargs"); 
/*2146*/    return paramsType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseResultType(JsDocToken token) {
/*2153*/    skipEOLs();
/*2154*/    if (!match(JsDocToken.COLON))
/*2155*/      return newNode(124); 
/*2158*/    token = next();
/*2159*/    skipEOLs();
/*2160*/    if (match(JsDocToken.STRING) && "void".equals(this.stream.getString())) {
/*2161*/      next();
/*2162*/      return newNode(122);
/*   0*/    } 
/*2164*/    return parseTypeExpression(next());
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseUnionType(JsDocToken token) {
/*2175*/    return parseUnionTypeWithAlternate(token, null);
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseUnionTypeWithAlternate(JsDocToken token, Node alternate) {
/*2183*/    Node union = newNode(301);
/*2184*/    if (alternate != null)
/*2185*/      union.addChildToBack(alternate); 
/*2188*/    Node expr = null;
/*   0*/    do {
/*2190*/      if (expr != null) {
/*2191*/        skipEOLs();
/*2192*/        token = next();
/*2193*/        Preconditions.checkState((token == JsDocToken.PIPE || token == JsDocToken.COMMA));
/*2196*/        boolean isPipe = (token == JsDocToken.PIPE);
/*2197*/        if (isPipe && match(JsDocToken.PIPE))
/*2199*/          next(); 
/*2201*/        skipEOLs();
/*2202*/        token = next();
/*   0*/      } 
/*2204*/      expr = parseTypeExpression(token);
/*2205*/      if (expr == null)
/*2206*/        return null; 
/*2209*/      union.addChildToBack(expr);
/*2211*/    } while (match(JsDocToken.PIPE, JsDocToken.COMMA));
/*2213*/    if (alternate == null) {
/*2214*/      skipEOLs();
/*2215*/      if (!match(JsDocToken.RP))
/*2216*/        return reportTypeSyntaxWarning("msg.jsdoc.missing.rp"); 
/*2218*/      next();
/*   0*/    } 
/*2220*/    return union;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseArrayType(JsDocToken token) {
/*2229*/    Node array = newNode(308);
/*2230*/    Node arg = null;
/*   0*/    boolean hasVarArgs = false;
/*   0*/    do {
/*2234*/      if (arg != null) {
/*2235*/        next();
/*2236*/        skipEOLs();
/*2237*/        token = next();
/*   0*/      } 
/*2239*/      if (token == JsDocToken.ELLIPSIS) {
/*2240*/        arg = wrapNode(305, parseTypeExpression(next()));
/*2241*/        hasVarArgs = true;
/*   0*/      } else {
/*2243*/        arg = parseTypeExpression(token);
/*   0*/      } 
/*2246*/      if (arg == null)
/*2247*/        return null; 
/*2250*/      array.addChildToBack(arg);
/*2251*/      if (hasVarArgs)
/*   0*/        break; 
/*2254*/      skipEOLs();
/*2255*/    } while (match(JsDocToken.COMMA));
/*2257*/    if (!match(JsDocToken.RB))
/*2258*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.rb"); 
/*2260*/    next();
/*2261*/    return array;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseRecordType(JsDocToken token) {
/*2268*/    Node recordType = newNode(309);
/*2269*/    Node fieldTypeList = parseFieldTypeList(token);
/*2271*/    if (fieldTypeList == null)
/*2272*/      return reportGenericTypeSyntaxWarning(); 
/*2275*/    skipEOLs();
/*2276*/    if (!match(JsDocToken.RC))
/*2277*/      return reportTypeSyntaxWarning("msg.jsdoc.missing.rc"); 
/*2280*/    next();
/*2282*/    recordType.addChildToBack(fieldTypeList);
/*2283*/    return recordType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFieldTypeList(JsDocToken token) {
/*2290*/    Node fieldTypeList = newNode(308);
/*   0*/    while (true) {
/*2293*/      Node fieldType = parseFieldType(token);
/*2295*/      if (fieldType == null)
/*2296*/        return null; 
/*2299*/      fieldTypeList.addChildToBack(fieldType);
/*2301*/      skipEOLs();
/*2302*/      if (!match(JsDocToken.COMMA))
/*   0*/        break; 
/*2307*/      next();
/*2310*/      skipEOLs();
/*2311*/      token = next();
/*   0*/    } 
/*2314*/    return fieldTypeList;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFieldType(JsDocToken token) {
/*2321*/    Node fieldName = parseFieldName(token);
/*2323*/    if (fieldName == null)
/*2324*/      return null; 
/*2327*/    skipEOLs();
/*2328*/    if (!match(JsDocToken.COLON))
/*2329*/      return fieldName; 
/*2333*/    next();
/*2337*/    skipEOLs();
/*2338*/    Node typeExpression = parseTypeExpression(next());
/*2340*/    if (typeExpression == null)
/*2341*/      return null; 
/*2344*/    Node fieldType = newNode(310);
/*2345*/    fieldType.addChildToBack(fieldName);
/*2346*/    fieldType.addChildToBack(typeExpression);
/*2347*/    return fieldType;
/*   0*/  }
/*   0*/  
/*   0*/  private Node parseFieldName(JsDocToken token) {
/*   0*/    String string;
/*2355*/    switch (token) {
/*   0*/      case STRING:
/*2357*/        string = this.stream.getString();
/*2358*/        return newStringNode(string);
/*   0*/    } 
/*2361*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private Node wrapNode(int type, Node n) {
/*2366*/    return (n == null) ? null : new Node(type, n, this.stream.getLineno(), this.stream.getCharno()).clonePropsFrom(this.templateNode);
/*   0*/  }
/*   0*/  
/*   0*/  private Node newNode(int type) {
/*2372*/    return new Node(type, this.stream.getLineno(), this.stream.getCharno()).clonePropsFrom(this.templateNode);
/*   0*/  }
/*   0*/  
/*   0*/  private Node newStringNode(String s) {
/*2377*/    return newStringNode(s, this.stream.getLineno(), this.stream.getCharno());
/*   0*/  }
/*   0*/  
/*   0*/  private Node newStringNode(String s, int lineno, int charno) {
/*2381*/    Node n = Node.newString(s, lineno, charno).clonePropsFrom(this.templateNode);
/*2382*/    n.setLength(s.length());
/*2383*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  private Node createTemplateNode() {
/*2390*/    Node templateNode = IR.script();
/*2391*/    templateNode.setStaticSourceFile((this.associatedNode != null) ? this.associatedNode.getStaticSourceFile() : null);
/*2395*/    return templateNode;
/*   0*/  }
/*   0*/  
/*   0*/  private Node reportTypeSyntaxWarning(String warning) {
/*2399*/    this.parser.addTypeWarning(warning, this.stream.getLineno(), this.stream.getCharno());
/*2400*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private Node reportGenericTypeSyntaxWarning() {
/*2404*/    return reportTypeSyntaxWarning("msg.jsdoc.type.syntax");
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken eatUntilEOLIfNotAnnotation() {
/*2408*/    return eatUntilEOLIfNotAnnotation(next());
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken eatUntilEOLIfNotAnnotation(JsDocToken token) {
/*2412*/    if (token == JsDocToken.ANNOTATION) {
/*2413*/      this.state = State.SEARCHING_ANNOTATION;
/*2414*/      return token;
/*   0*/    } 
/*2416*/    return eatTokensUntilEOL(token);
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken eatTokensUntilEOL() {
/*2424*/    return eatTokensUntilEOL(next());
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken eatTokensUntilEOL(JsDocToken token) {
/*   0*/    while (true) {
/*2433*/      if (token == JsDocToken.EOL || token == JsDocToken.EOC || token == JsDocToken.EOF) {
/*2435*/        this.state = State.SEARCHING_ANNOTATION;
/*2436*/        return token;
/*   0*/      } 
/*2438*/      token = next();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*2445*/  private static final JsDocToken NO_UNREAD_TOKEN = null;
/*   0*/  
/*   0*/  private JsDocToken unreadToken;
/*   0*/  
/*   0*/  JsDocInfoParser(JsDocTokenStream stream, Comment commentNode, Node associatedNode, Config config, ErrorReporter errorReporter) {
/*2450*/    this.unreadToken = NO_UNREAD_TOKEN;
/*   0*/    this.stream = stream;
/*   0*/    this.associatedNode = associatedNode;
/*   0*/    this.sourceFile = (associatedNode == null) ? null : associatedNode.getStaticSourceFile();
/*   0*/    this.jsdocBuilder = new JSDocInfoBuilder(config.parseJsDocDocumentation);
/*   0*/    if (commentNode != null) {
/*   0*/      this.jsdocBuilder.recordOriginalCommentString(commentNode.getValue());
/*   0*/      this.jsdocBuilder.recordOriginalCommentPosition(commentNode.getPosition());
/*   0*/    } 
/*   0*/    this.annotationNames = config.annotationNames;
/*   0*/    this.suppressionNames = config.suppressionNames;
/*   0*/    this.errorReporter = errorReporter;
/*   0*/    this.templateNode = createTemplateNode();
/*   0*/  }
/*   0*/  
/*   0*/  private void restoreLookAhead(JsDocToken token) {
/*2454*/    this.unreadToken = token;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean match(JsDocToken token) {
/*2462*/    this.unreadToken = next();
/*2463*/    return (this.unreadToken == token);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean match(JsDocToken token1, JsDocToken token2) {
/*2471*/    this.unreadToken = next();
/*2472*/    return (this.unreadToken == token1 || this.unreadToken == token2);
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken next() {
/*2480*/    if (this.unreadToken == NO_UNREAD_TOKEN)
/*2481*/      return this.stream.getJsDocToken(); 
/*2483*/    return current();
/*   0*/  }
/*   0*/  
/*   0*/  private JsDocToken current() {
/*2491*/    JsDocToken t = this.unreadToken;
/*2492*/    this.unreadToken = NO_UNREAD_TOKEN;
/*2493*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  private void skipEOLs() {
/*2501*/    while (match(JsDocToken.EOL)) {
/*2502*/      next();
/*2503*/      if (match(JsDocToken.STAR))
/*2504*/        next(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private String getRemainingJSDocLine() {
/*2513*/    String result = this.stream.getRemainingJSDocLine();
/*2514*/    this.unreadToken = NO_UNREAD_TOKEN;
/*2515*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean hasParsedFileOverviewDocInfo() {
/*2523*/    return this.jsdocBuilder.isPopulatedWithFileOverview();
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasParsedJSDocInfo() {
/*2527*/    return this.jsdocBuilder.isPopulated();
/*   0*/  }
/*   0*/  
/*   0*/  JSDocInfo retrieveAndResetParsedJSDocInfo() {
/*2531*/    return this.jsdocBuilder.build(this.associatedNode);
/*   0*/  }
/*   0*/  
/*   0*/  JSDocInfo getFileOverviewJSDocInfo() {
/*2538*/    return this.fileOverviewJSDocInfo;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean lookAheadForType() {
/*2550*/    return lookAheadFor('{');
/*   0*/  }
/*   0*/  
/*   0*/  private boolean lookAheadForAnnotation() {
/*2554*/    return lookAheadFor('@');
/*   0*/  }
/*   0*/  
/*   0*/  private boolean lookAheadFor(char expect) {
/*   0*/    int c;
/*   0*/    boolean matched = false;
/*   0*/    while (true) {
/*2566*/      c = this.stream.getChar();
/*2567*/      if (c == 32)
/*   0*/        continue; 
/*   0*/      break;
/*   0*/    } 
/*2569*/    if (c == expect)
/*2570*/      matched = true; 
/*2576*/    this.stream.ungetChar(c);
/*2577*/    return matched;
/*   0*/  }
/*   0*/}
