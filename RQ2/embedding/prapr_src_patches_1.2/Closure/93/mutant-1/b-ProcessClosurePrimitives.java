/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class ProcessClosurePrimitives extends NodeTraversal.AbstractPostOrderCallback implements CompilerPass {
/*  40*/  static final DiagnosticType NULL_ARGUMENT_ERROR = DiagnosticType.error("JSC_NULL_ARGUMENT_ERROR", "method \"{0}\" called without an argument");
/*   0*/  
/*  44*/  static final DiagnosticType INVALID_ARGUMENT_ERROR = DiagnosticType.error("JSC_INVALID_ARGUMENT_ERROR", "method \"{0}\" called with a non-string argument");
/*   0*/  
/*  48*/  static final DiagnosticType TOO_MANY_ARGUMENTS_ERROR = DiagnosticType.error("JSC_TOO_MANY_ARGUMENTS_ERROR", "method \"{0}\" called with more than one argument");
/*   0*/  
/*  52*/  static final DiagnosticType DUPLICATE_NAMESPACE_ERROR = DiagnosticType.error("JSC_DUPLICATE_NAMESPACE_ERROR", "namespace \"{0}\" cannot be provided twice");
/*   0*/  
/*  56*/  static final DiagnosticType FUNCTION_NAMESPACE_ERROR = DiagnosticType.error("JSC_FUNCTION_NAMESPACE_ERROR", "\"{0}\" cannot be both provided and declared as a function");
/*   0*/  
/*  60*/  static final DiagnosticType MISSING_PROVIDE_ERROR = DiagnosticType.error("JSC_MISSING_PROVIDE_ERROR", "required \"{0}\" namespace never provided");
/*   0*/  
/*  64*/  static final DiagnosticType LATE_PROVIDE_ERROR = DiagnosticType.error("JSC_LATE_PROVIDE_ERROR", "required \"{0}\" namespace not provided yet");
/*   0*/  
/*  68*/  static final DiagnosticType INVALID_PROVIDE_ERROR = DiagnosticType.error("JSC_INVALID_PROVIDE_ERROR", "\"{0}\" is not a valid JS property name");
/*   0*/  
/*  72*/  static final DiagnosticType XMODULE_REQUIRE_ERROR = DiagnosticType.warning("JSC_XMODULE_REQUIRE_ERROR", "namespace \"{0}\" provided in module {1} but required in module {2}");
/*   0*/  
/*  77*/  static final DiagnosticType NON_STRING_PASSED_TO_SET_CSS_NAME_MAPPING_ERROR = DiagnosticType.error("JSC_NON_STRING_PASSED_TO_SET_CSS_NAME_MAPPING_ERROR", "goog.setCssNameMapping only takes an object literal with string values");
/*   0*/  
/*  82*/  static final DiagnosticType BASE_CLASS_ERROR = DiagnosticType.error("JSC_BASE_CLASS_ERROR", "incorrect use of goog.base: {0}");
/*   0*/  
/*   0*/  static final String GOOG = "goog";
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final JSModuleGraph moduleGraph;
/*   0*/  
/*  93*/  private final Map<String, ProvidedName> providedNames = Maps.newTreeMap();
/*   0*/  
/*  96*/  private final List<UnrecognizedRequire> unrecognizedRequires = Lists.newArrayList();
/*   0*/  
/*  98*/  private final Set<String> exportedVariables = Sets.newHashSet();
/*   0*/  
/*   0*/  private final CheckLevel requiresLevel;
/*   0*/  
/*   0*/  private final boolean rewriteNewDateGoogNow;
/*   0*/  
/*   0*/  ProcessClosurePrimitives(AbstractCompiler compiler, CheckLevel requiresLevel, boolean rewriteNewDateGoogNow) {
/* 105*/    this.compiler = compiler;
/* 106*/    this.moduleGraph = compiler.getModuleGraph();
/* 107*/    this.requiresLevel = requiresLevel;
/* 108*/    this.rewriteNewDateGoogNow = rewriteNewDateGoogNow;
/* 111*/    this.providedNames.put("goog", new ProvidedName("goog", null, null, false));
/*   0*/  }
/*   0*/  
/*   0*/  Set<String> getExportedVariableNames() {
/* 116*/    return this.exportedVariables;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 121*/    new NodeTraversal(this.compiler, this).traverse(root);
/* 123*/    for (ProvidedName pn : this.providedNames.values())
/* 124*/      pn.replace(); 
/* 127*/    if (this.requiresLevel.isOn())
/* 128*/      for (UnrecognizedRequire r : this.unrecognizedRequires) {
/*   0*/        DiagnosticType error;
/* 130*/        ProvidedName expectedName = this.providedNames.get(r.namespace);
/* 131*/        if (expectedName != null && expectedName.firstNode != null) {
/* 133*/          error = LATE_PROVIDE_ERROR;
/*   0*/        } else {
/* 135*/          error = MISSING_PROVIDE_ERROR;
/*   0*/        } 
/* 138*/        this.compiler.report(JSError.make(r.inputName, r.requireNode, this.requiresLevel, error, new String[] { r.namespace }));
/*   0*/      }  
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/    boolean isExpr;
/*   0*/    Node left;
/* 146*/    switch (n.getType()) {
/*   0*/      case 37:
/* 148*/        isExpr = (parent.getType() == 130);
/* 149*/        left = n.getFirstChild();
/* 150*/        if (left.getType() == 33) {
/* 151*/          Node name = left.getFirstChild();
/* 152*/          if (name.getType() == 38 && "goog".equals(name.getString())) {
/* 157*/            String methodName = name.getNext().getString();
/* 158*/            if ("base".equals(methodName)) {
/* 159*/              processBaseClassCall(t, n);
/*   0*/              break;
/*   0*/            } 
/* 160*/            if (!isExpr)
/*   0*/              break; 
/* 163*/            if ("require".equals(methodName)) {
/* 164*/              processRequireCall(t, n, parent);
/*   0*/              break;
/*   0*/            } 
/* 165*/            if ("provide".equals(methodName)) {
/* 166*/              processProvideCall(t, n, parent);
/*   0*/              break;
/*   0*/            } 
/* 167*/            if ("exportSymbol".equals(methodName)) {
/* 168*/              Node arg = left.getNext();
/* 169*/              if (arg.getType() == 40) {
/* 170*/                int dot = arg.getString().indexOf('.');
/* 171*/                if (dot == -1) {
/* 172*/                  this.exportedVariables.add(arg.getString());
/*   0*/                  break;
/*   0*/                } 
/* 174*/                this.exportedVariables.add(arg.getString().substring(0, dot));
/*   0*/              } 
/*   0*/              break;
/*   0*/            } 
/* 177*/            if ("addDependency".equals(methodName)) {
/* 178*/              CodingConvention convention = this.compiler.getCodingConvention();
/* 179*/              List<String> typeDecls = convention.identifyTypeDeclarationCall(n);
/* 181*/              if (typeDecls != null)
/* 182*/                for (String typeDecl : typeDecls)
/* 183*/                  this.compiler.getTypeRegistry().forwardDeclareType(typeDecl);  
/* 189*/              parent.replaceChild(n, Node.newNumber(0.0D));
/* 190*/              this.compiler.reportCodeChange();
/*   0*/              break;
/*   0*/            } 
/* 191*/            if ("setCssNameMapping".equals(methodName))
/* 192*/              processSetCssNameMapping(t, n, parent); 
/*   0*/          } 
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 38:
/*   0*/      case 86:
/* 201*/        handleCandidateProvideDefinition(t, n, parent);
/*   0*/        break;
/*   0*/      case 105:
/* 206*/        if (t.inGlobalScope() && !NodeUtil.isFunctionExpression(n)) {
/* 208*/          String name = n.getFirstChild().getString();
/* 209*/          ProvidedName pn = this.providedNames.get(name);
/* 210*/          if (pn != null)
/* 211*/            this.compiler.report(t.makeError(n, FUNCTION_NAMESPACE_ERROR, new String[] { name })); 
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 30:
/* 217*/        trySimplifyNewDate(t, n, parent);
/*   0*/        break;
/*   0*/      case 33:
/* 221*/        if (n.getFirstChild().getType() == 38 && parent.getType() != 37 && parent.getType() != 86 && "goog.base".equals(n.getQualifiedName()))
/* 225*/          reportBadBaseClassUse(t, n, "May only be called directly."); 
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void processRequireCall(NodeTraversal t, Node n, Node parent) {
/* 235*/    Node left = n.getFirstChild();
/* 236*/    Node arg = left.getNext();
/* 237*/    if (verifyArgument(t, left, arg)) {
/* 238*/      String ns = arg.getString();
/* 239*/      ProvidedName provided = this.providedNames.get(ns);
/* 240*/      if (provided == null || !provided.isExplicitlyProvided()) {
/* 241*/        this.unrecognizedRequires.add(new UnrecognizedRequire(n, ns, t.getSourceName()));
/*   0*/      } else {
/* 244*/        JSModule providedModule = provided.explicitModule;
/* 247*/        Preconditions.checkNotNull(providedModule);
/* 249*/        JSModule module = t.getModule();
/* 250*/        if (this.moduleGraph != null && module != providedModule && !this.moduleGraph.dependsOn(module, providedModule))
/* 253*/          this.compiler.report(t.makeError(n, XMODULE_REQUIRE_ERROR, new String[] { ns, providedModule.getName(), module.getName() })); 
/*   0*/      } 
/* 266*/      if (provided != null || this.requiresLevel.isOn()) {
/* 267*/        parent.detachFromParent();
/* 268*/        this.compiler.reportCodeChange();
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void processProvideCall(NodeTraversal t, Node n, Node parent) {
/* 277*/    Node left = n.getFirstChild();
/* 278*/    Node arg = left.getNext();
/* 279*/    if (verifyProvide(t, left, arg)) {
/* 280*/      String ns = arg.getString();
/* 281*/      if (this.providedNames.containsKey(ns)) {
/* 282*/        ProvidedName previouslyProvided = this.providedNames.get(ns);
/* 283*/        if (!previouslyProvided.isExplicitlyProvided()) {
/* 284*/          previouslyProvided.addProvide(parent, t.getModule(), true);
/*   0*/        } else {
/* 286*/          this.compiler.report(t.makeError(n, DUPLICATE_NAMESPACE_ERROR, new String[] { ns }));
/*   0*/        } 
/*   0*/      } else {
/* 290*/        registerAnyProvidedPrefixes(ns, parent, t.getModule());
/* 291*/        this.providedNames.put(ns, new ProvidedName(ns, parent, t.getModule(), true));
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleCandidateProvideDefinition(NodeTraversal t, Node n, Node parent) {
/* 302*/    if (t.inGlobalScope()) {
/* 303*/      String name = null;
/* 304*/      if (n.getType() == 38 && parent.getType() == 118) {
/* 305*/        name = n.getString();
/* 306*/      } else if (n.getType() == 86 && parent.getType() == 130) {
/* 308*/        name = n.getFirstChild().getQualifiedName();
/*   0*/      } 
/* 311*/      if (name != null)
/* 312*/        if (parent.getBooleanProp(45)) {
/* 313*/          processProvideFromPreviousPass(t, name, parent);
/*   0*/        } else {
/* 315*/          ProvidedName pn = this.providedNames.get(name);
/* 316*/          if (pn != null)
/* 317*/            pn.addDefinition(parent, t.getModule()); 
/*   0*/        }  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void processBaseClassCall(NodeTraversal t, Node n) {
/* 355*/    Node callee = n.getFirstChild();
/* 356*/    Node thisArg = callee.getNext();
/* 357*/    if (thisArg == null || thisArg.getType() != 42) {
/* 358*/      reportBadBaseClassUse(t, n, "First argument must be 'this'.");
/*   0*/      return;
/*   0*/    } 
/* 362*/    Node enclosingFnNameNode = getEnclosingDeclNameNode(t);
/* 363*/    if (enclosingFnNameNode == null) {
/* 364*/      reportBadBaseClassUse(t, n, "Could not find enclosing method.");
/*   0*/      return;
/*   0*/    } 
/* 368*/    String enclosingQname = enclosingFnNameNode.getQualifiedName();
/* 369*/    if (enclosingQname.indexOf(".prototype.") == -1) {
/* 371*/      Node enclosingParent = enclosingFnNameNode.getParent();
/* 372*/      Node maybeInheritsExpr = ((enclosingParent.getType() == 86) ? enclosingParent.getParent() : enclosingParent).getNext();
/* 374*/      Node baseClassNode = null;
/* 375*/      if (maybeInheritsExpr != null && maybeInheritsExpr.getType() == 130 && maybeInheritsExpr.getFirstChild().getType() == 37) {
/* 378*/        Node callNode = maybeInheritsExpr.getFirstChild();
/* 379*/        if ("goog.inherits".equals(callNode.getFirstChild().getQualifiedName()) && callNode.getLastChild().isQualifiedName())
/* 382*/          baseClassNode = callNode.getLastChild(); 
/*   0*/      } 
/* 386*/      if (baseClassNode == null) {
/* 387*/        reportBadBaseClassUse(t, n, "Could not find goog.inherits for base class");
/*   0*/        return;
/*   0*/      } 
/* 393*/      n.replaceChild(callee, NodeUtil.newQualifiedNameNode(String.format("%s.call", new Object[] { baseClassNode.getQualifiedName() }), callee, "goog.base"));
/* 398*/      this.compiler.reportCodeChange();
/*   0*/    } else {
/* 401*/      Node methodNameNode = thisArg.getNext();
/* 402*/      if (methodNameNode == null || methodNameNode.getType() != 40) {
/* 403*/        reportBadBaseClassUse(t, n, "Second argument must name a method.");
/*   0*/        return;
/*   0*/      } 
/* 407*/      String methodName = methodNameNode.getString();
/* 408*/      String ending = ".prototype." + methodName;
/* 409*/      if (enclosingQname == null || !enclosingQname.endsWith(ending)) {
/* 411*/        reportBadBaseClassUse(t, n, "Enclosing method does not match " + methodName);
/*   0*/        return;
/*   0*/      } 
/* 417*/      Node className = enclosingFnNameNode.getFirstChild().getFirstChild();
/* 419*/      n.replaceChild(callee, NodeUtil.newQualifiedNameNode(String.format("%s.superClass_.%s.call", new Object[] { className.getQualifiedName(), methodName }), callee, "goog.base"));
/* 425*/      n.removeChild(methodNameNode);
/* 426*/      this.compiler.reportCodeChange();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private Node getEnclosingDeclNameNode(NodeTraversal t) {
/* 435*/    Node scopeRoot = t.getScopeRoot();
/* 436*/    if (NodeUtil.isFunctionDeclaration(scopeRoot))
/* 438*/      return scopeRoot.getFirstChild(); 
/* 440*/    Node parent = scopeRoot.getParent();
/* 441*/    if (parent != null) {
/* 442*/      if (parent.getType() == 86 || (parent.getLastChild() == scopeRoot && parent.getFirstChild().isQualifiedName()))
/* 446*/        return parent.getFirstChild(); 
/* 447*/      if (parent.getType() == 38)
/* 449*/        return parent; 
/*   0*/    } 
/* 454*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private void reportBadBaseClassUse(NodeTraversal t, Node n, String extraMessage) {
/* 460*/    this.compiler.report(t.makeError(n, BASE_CLASS_ERROR, new String[] { extraMessage }));
/*   0*/  }
/*   0*/  
/*   0*/  private void processProvideFromPreviousPass(NodeTraversal t, String name, Node parent) {
/* 470*/    if (!this.providedNames.containsKey(name)) {
/* 473*/      Node expr = new Node(130);
/* 474*/      expr.copyInformationFromForTree(parent);
/* 475*/      parent.getParent().addChildBefore(expr, parent);
/* 476*/      this.compiler.reportCodeChange();
/* 478*/      JSModule module = t.getModule();
/* 479*/      registerAnyProvidedPrefixes(name, expr, module);
/* 481*/      ProvidedName provided = new ProvidedName(name, expr, module, true);
/* 482*/      this.providedNames.put(name, provided);
/* 483*/      provided.addDefinition(parent, module);
/* 487*/    } else if (isNamespacePlaceholder(parent)) {
/* 488*/      parent.getParent().removeChild(parent);
/* 489*/      this.compiler.reportCodeChange();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void processSetCssNameMapping(NodeTraversal t, Node n, Node parent) {
/* 502*/    Node left = n.getFirstChild();
/* 503*/    Node arg = left.getNext();
/* 504*/    if (verifyArgument(t, left, arg, 64)) {
/* 507*/      final Map<String, String> cssNames = Maps.newHashMap();
/* 508*/      JSError error = null;
/* 509*/      for (Node key = arg.getFirstChild(); key != null; 
/* 510*/        key = key.getNext().getNext()) {
/* 511*/        Node value = key.getNext();
/* 512*/        if (key.getType() != 40 || value == null || value.getType() != 40)
/* 515*/          error = t.makeError(n, NON_STRING_PASSED_TO_SET_CSS_NAME_MAPPING_ERROR, new String[0]); 
/* 518*/        if (error != null) {
/* 519*/          this.compiler.report(error);
/*   0*/          break;
/*   0*/        } 
/* 522*/        cssNames.put(key.getString(), value.getString());
/*   0*/      } 
/* 527*/      if (error == null) {
/* 528*/        CssRenamingMap cssRenamingMap = new CssRenamingMap() {
/*   0*/            public String get(String value) {
/* 530*/              if (cssNames.containsKey(value))
/* 531*/                return (String)cssNames.get(value); 
/* 533*/              return value;
/*   0*/            }
/*   0*/          };
/* 537*/        this.compiler.setCssRenamingMap(cssRenamingMap);
/* 538*/        parent.getParent().removeChild(parent);
/* 539*/        this.compiler.reportCodeChange();
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void trySimplifyNewDate(NodeTraversal t, Node n, Node parent) {
/* 548*/    if (!this.rewriteNewDateGoogNow)
/*   0*/      return; 
/* 551*/    Preconditions.checkArgument((n.getType() == 30));
/* 552*/    Node date = n.getFirstChild();
/* 553*/    if (!NodeUtil.isName(date) || !"Date".equals(date.getString()))
/*   0*/      return; 
/* 556*/    Node callGoogNow = date.getNext();
/* 557*/    if (callGoogNow == null || !NodeUtil.isCall(callGoogNow) || callGoogNow.getNext() != null)
/*   0*/      return; 
/* 561*/    Node googNow = callGoogNow.getFirstChild();
/* 562*/    String googNowQName = googNow.getQualifiedName();
/* 563*/    if (googNowQName == null || !"goog.now".equals(googNowQName) || googNow.getNext() != null)
/*   0*/      return; 
/* 567*/    n.removeChild(callGoogNow);
/* 568*/    this.compiler.reportCodeChange();
/*   0*/  }
/*   0*/  
/*   0*/  private boolean verifyProvide(NodeTraversal t, Node methodName, Node arg) {
/* 579*/    if (!verifyArgument(t, methodName, arg))
/* 580*/      return false; 
/* 583*/    for (String part : arg.getString().split("\\.")) {
/* 584*/      if (!NodeUtil.isValidPropertyName(part)) {
/* 585*/        this.compiler.report(t.makeError(arg, INVALID_PROVIDE_ERROR, new String[] { part }));
/* 586*/        return false;
/*   0*/      } 
/*   0*/    } 
/* 589*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean verifyArgument(NodeTraversal t, Node methodName, Node arg) {
/* 599*/    return verifyArgument(t, methodName, arg, 40);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean verifyArgument(NodeTraversal t, Node methodName, Node arg, int desiredType) {
/* 610*/    DiagnosticType diagnostic = null;
/* 611*/    if (arg == null) {
/* 612*/      diagnostic = NULL_ARGUMENT_ERROR;
/* 613*/    } else if (arg.getType() != desiredType) {
/* 614*/      diagnostic = INVALID_ARGUMENT_ERROR;
/* 615*/    } else if (arg.getNext() != null) {
/* 616*/      diagnostic = TOO_MANY_ARGUMENTS_ERROR;
/*   0*/    } 
/* 618*/    if (diagnostic != null) {
/* 619*/      this.compiler.report(t.makeError(methodName, diagnostic, new String[] { methodName.getQualifiedName() }));
/* 622*/      return false;
/*   0*/    } 
/* 624*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void registerAnyProvidedPrefixes(String ns, Node node, JSModule module) {
/* 638*/    int pos = ns.indexOf('.');
/* 639*/    while (pos != -1) {
/* 640*/      String prefixNs = ns.substring(0, pos);
/* 641*/      pos = ns.indexOf('.', pos + 1);
/* 642*/      if (this.providedNames.containsKey(prefixNs)) {
/* 643*/        ((ProvidedName)this.providedNames.get(prefixNs)).addProvide(node, module, false);
/*   0*/        continue;
/*   0*/      } 
/* 646*/      this.providedNames.put(prefixNs, new ProvidedName(prefixNs, node, module, false));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private class ProvidedName {
/*   0*/    private final String namespace;
/*   0*/    
/*   0*/    private final Node firstNode;
/*   0*/    
/*   0*/    private final JSModule firstModule;
/*   0*/    
/* 668*/    private Node explicitNode = null;
/*   0*/    
/* 669*/    private JSModule explicitModule = null;
/*   0*/    
/* 672*/    private Node candidateDefinition = null;
/*   0*/    
/* 675*/    private JSModule minimumModule = null;
/*   0*/    
/* 678*/    private Node replacementNode = null;
/*   0*/    
/*   0*/    ProvidedName(String namespace, Node node, JSModule module, boolean explicit) {
/* 682*/      Preconditions.checkArgument((node == null || NodeUtil.isExpressionNode(node)));
/* 685*/      this.namespace = namespace;
/* 686*/      this.firstNode = node;
/* 687*/      this.firstModule = module;
/* 689*/      addProvide(node, module, explicit);
/*   0*/    }
/*   0*/    
/*   0*/    void addProvide(Node node, JSModule module, boolean explicit) {
/* 696*/      if (explicit) {
/* 697*/        Preconditions.checkState((this.explicitNode == null));
/* 698*/        Preconditions.checkArgument(NodeUtil.isExpressionNode(node));
/* 699*/        this.explicitNode = node;
/* 700*/        this.explicitModule = module;
/*   0*/      } 
/* 702*/      updateMinimumModule(module);
/*   0*/    }
/*   0*/    
/*   0*/    boolean isExplicitlyProvided() {
/* 706*/      return (this.explicitNode != null);
/*   0*/    }
/*   0*/    
/*   0*/    void addDefinition(Node node, JSModule module) {
/* 716*/      Preconditions.checkArgument((NodeUtil.isExpressionNode(node) || NodeUtil.isFunction(node) || NodeUtil.isVar(node)));
/* 719*/      Preconditions.checkArgument((this.explicitNode != node));
/* 720*/      if (this.candidateDefinition == null || !NodeUtil.isExpressionNode(node)) {
/* 721*/        this.candidateDefinition = node;
/* 722*/        updateMinimumModule(module);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void updateMinimumModule(JSModule newModule) {
/* 727*/      if (this.minimumModule == null) {
/* 728*/        this.minimumModule = newModule;
/* 729*/      } else if (ProcessClosurePrimitives.this.moduleGraph != null) {
/* 730*/        this.minimumModule = ProcessClosurePrimitives.this.moduleGraph.getDeepestCommonDependencyInclusive(this.minimumModule, newModule);
/*   0*/      } else {
/* 735*/        Preconditions.checkState((newModule == this.minimumModule), "Missing module graph");
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void replace() {
/* 748*/      if (this.firstNode == null) {
/* 750*/        this.replacementNode = this.candidateDefinition;
/*   0*/        return;
/*   0*/      } 
/* 756*/      if (this.candidateDefinition != null && this.explicitNode != null) {
/* 757*/        this.explicitNode.detachFromParent();
/* 758*/        ProcessClosurePrimitives.this.compiler.reportCodeChange();
/* 761*/        this.replacementNode = this.candidateDefinition;
/* 762*/        if (NodeUtil.isExpressionNode(this.candidateDefinition)) {
/* 763*/          this.candidateDefinition.putBooleanProp(45, true);
/* 764*/          Node assignNode = this.candidateDefinition.getFirstChild();
/* 765*/          Node nameNode = assignNode.getFirstChild();
/* 766*/          if (nameNode.getType() == 38) {
/* 768*/            Node valueNode = nameNode.getNext();
/* 769*/            assignNode.removeChild(nameNode);
/* 770*/            assignNode.removeChild(valueNode);
/* 771*/            nameNode.addChildToFront(valueNode);
/* 772*/            Node varNode = new Node(118, nameNode);
/* 773*/            varNode.copyInformationFrom(this.candidateDefinition);
/* 774*/            this.candidateDefinition.getParent().replaceChild(this.candidateDefinition, varNode);
/* 776*/            nameNode.setJSDocInfo(assignNode.getJSDocInfo());
/* 777*/            ProcessClosurePrimitives.this.compiler.reportCodeChange();
/* 778*/            this.replacementNode = varNode;
/*   0*/          } 
/*   0*/        } 
/*   0*/        return;
/*   0*/      } 
/* 783*/      this.replacementNode = createDeclarationNode();
/* 784*/      if (this.firstModule == this.minimumModule) {
/* 785*/        this.firstNode.getParent().addChildBefore(this.replacementNode, this.firstNode);
/*   0*/      } else {
/* 789*/        int indexOfDot = this.namespace.indexOf('.');
/* 790*/        -1;
/* 792*/        ProcessClosurePrimitives.this.compiler.getNodeForCodeInsertion(this.minimumModule).addChildToBack(this.replacementNode);
/*   0*/      } 
/*   0*/      while (true) {
/* 804*/        if (this.explicitNode != null)
/* 805*/          this.explicitNode.detachFromParent(); 
/* 807*/        ProcessClosurePrimitives.this.compiler.reportCodeChange();
/*   0*/        return;
/*   0*/      } 
/* 807*/      ProcessClosurePrimitives.this.compiler.reportCodeChange();
/*   0*/    }
/*   0*/    
/*   0*/    private Node createDeclarationNode() {
/* 816*/      if (this.namespace.indexOf('.') == -1)
/* 817*/        return makeVarDeclNode(this.namespace, this.firstNode); 
/* 819*/      return makeAssignmentExprNode(this.namespace, this.firstNode);
/*   0*/    }
/*   0*/    
/*   0*/    private Node makeVarDeclNode(String namespace, Node sourceNode) {
/* 831*/      Node name = Node.newString(38, namespace);
/* 832*/      name.addChildToFront(createNamespaceLiteral());
/* 834*/      Node decl = new Node(118, name);
/* 835*/      decl.putBooleanProp(45, true);
/* 838*/      if (ProcessClosurePrimitives.this.compiler.getCodingConvention().isConstant(namespace))
/* 839*/        name.putBooleanProp(42, true); 
/* 842*/      Preconditions.checkState(ProcessClosurePrimitives.isNamespacePlaceholder(decl));
/* 843*/      decl.copyInformationFromForTree(sourceNode);
/* 844*/      return decl;
/*   0*/    }
/*   0*/    
/*   0*/    private Node createNamespaceLiteral() {
/* 853*/      Node objlit = new Node(64);
/* 854*/      objlit.setJSType(ProcessClosurePrimitives.this.compiler.getTypeRegistry().createAnonymousObjectType());
/* 856*/      return objlit;
/*   0*/    }
/*   0*/    
/*   0*/    private Node makeAssignmentExprNode(String namespace, Node node) {
/* 867*/      Node decl = new Node(130, new Node(86, NodeUtil.newQualifiedNameNode(namespace, node, namespace), createNamespaceLiteral()));
/* 871*/      decl.putBooleanProp(45, true);
/* 872*/      Preconditions.checkState(ProcessClosurePrimitives.isNamespacePlaceholder(decl));
/* 873*/      decl.copyInformationFromForTree(node);
/* 874*/      return decl;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isNamespacePlaceholder(Node n) {
/* 882*/    if (!n.getBooleanProp(45))
/* 883*/      return false; 
/* 886*/    Node value = null;
/* 887*/    if (n.getType() == 130) {
/* 888*/      Node assign = n.getFirstChild();
/* 889*/      value = assign.getLastChild();
/* 890*/    } else if (n.getType() == 118) {
/* 891*/      Node name = n.getFirstChild();
/* 892*/      value = name.getFirstChild();
/*   0*/    } 
/* 895*/    return (value != null && value.getType() == 64 && !value.hasChildren());
/*   0*/  }
/*   0*/  
/*   0*/  private class UnrecognizedRequire {
/*   0*/    final Node requireNode;
/*   0*/    
/*   0*/    final String namespace;
/*   0*/    
/*   0*/    final String inputName;
/*   0*/    
/*   0*/    UnrecognizedRequire(Node requireNode, String namespace, String inputName) {
/* 911*/      this.requireNode = requireNode;
/* 912*/      this.namespace = namespace;
/* 913*/      this.inputName = inputName;
/*   0*/    }
/*   0*/  }
/*   0*/}
