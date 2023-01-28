/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.HashMultiset;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Multiset;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.SourcePosition;
/*   0*/import java.util.Collection;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import javax.annotation.Nullable;
/*   0*/
/*   0*/class ScopedAliases implements HotSwapCompilerPass {
/*   0*/  static final String SCOPING_METHOD_NAME = "goog.scope";
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final PreprocessorSymbolTable preprocessorSymbolTable;
/*   0*/  
/*   0*/  private final CompilerOptions.AliasTransformationHandler transformationHandler;
/*   0*/  
/*  82*/  static final DiagnosticType GOOG_SCOPE_USED_IMPROPERLY = DiagnosticType.error("JSC_GOOG_SCOPE_USED_IMPROPERLY", "The call to goog.scope must be alone in a single statement.");
/*   0*/  
/*  86*/  static final DiagnosticType GOOG_SCOPE_HAS_BAD_PARAMETERS = DiagnosticType.error("JSC_GOOG_SCOPE_HAS_BAD_PARAMETERS", "The call to goog.scope must take only a single parameter.  It must be an anonymous function that itself takes no parameters.");
/*   0*/  
/*  92*/  static final DiagnosticType GOOG_SCOPE_REFERENCES_THIS = DiagnosticType.error("JSC_GOOG_SCOPE_REFERENCES_THIS", "The body of a goog.scope function cannot reference 'this'.");
/*   0*/  
/*  96*/  static final DiagnosticType GOOG_SCOPE_USES_RETURN = DiagnosticType.error("JSC_GOOG_SCOPE_USES_RETURN", "The body of a goog.scope function cannot use 'return'.");
/*   0*/  
/* 100*/  static final DiagnosticType GOOG_SCOPE_USES_THROW = DiagnosticType.error("JSC_GOOG_SCOPE_USES_THROW", "The body of a goog.scope function cannot use 'throw'.");
/*   0*/  
/* 104*/  static final DiagnosticType GOOG_SCOPE_ALIAS_REDEFINED = DiagnosticType.error("JSC_GOOG_SCOPE_ALIAS_REDEFINED", "The alias {0} is assigned a value more than once.");
/*   0*/  
/* 108*/  static final DiagnosticType GOOG_SCOPE_ALIAS_CYCLE = DiagnosticType.error("JSC_GOOG_SCOPE_ALIAS_CYCLE", "The aliases {0} has a cycle.");
/*   0*/  
/* 112*/  static final DiagnosticType GOOG_SCOPE_NON_ALIAS_LOCAL = DiagnosticType.error("JSC_GOOG_SCOPE_NON_ALIAS_LOCAL", "The local variable {0} is in a goog.scope and is not an alias.");
/*   0*/  
/* 116*/  private Multiset<String> scopedAliasNames = (Multiset<String>)HashMultiset.create();
/*   0*/  
/*   0*/  ScopedAliases(AbstractCompiler compiler, @Nullable PreprocessorSymbolTable preprocessorSymbolTable, CompilerOptions.AliasTransformationHandler transformationHandler) {
/* 121*/    this.compiler = compiler;
/* 122*/    this.preprocessorSymbolTable = preprocessorSymbolTable;
/* 123*/    this.transformationHandler = transformationHandler;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 128*/    hotSwapScript(root, null);
/*   0*/  }
/*   0*/  
/*   0*/  public void hotSwapScript(Node root, Node originalRoot) {
/* 133*/    Traversal traversal = new Traversal();
/* 134*/    NodeTraversal.traverse(this.compiler, root, traversal);
/* 136*/    if (!traversal.hasErrors()) {
/* 139*/      List<AliasUsage> aliasWorkQueue = Lists.newArrayList(traversal.getAliasUsages());
/* 141*/      while (!aliasWorkQueue.isEmpty()) {
/* 142*/        List<AliasUsage> newQueue = Lists.newArrayList();
/* 143*/        for (AliasUsage aliasUsage : aliasWorkQueue) {
/* 144*/          if (aliasUsage.referencesOtherAlias()) {
/* 145*/            newQueue.add(aliasUsage);
/*   0*/            continue;
/*   0*/          } 
/* 147*/          aliasUsage.applyAlias();
/*   0*/        } 
/* 152*/        if (newQueue.size() == aliasWorkQueue.size()) {
/* 153*/          Scope.Var cycleVar = ((AliasUsage)newQueue.get(0)).aliasVar;
/* 154*/          this.compiler.report(JSError.make(cycleVar.getNode(), GOOG_SCOPE_ALIAS_CYCLE, new String[] { cycleVar.getName() }));
/*   0*/          break;
/*   0*/        } 
/* 158*/        aliasWorkQueue = newQueue;
/*   0*/      } 
/* 163*/      for (Node aliasDefinition : traversal.getAliasDefinitionsInOrder()) {
/* 164*/        if (aliasDefinition.getParent().isVar() && aliasDefinition.getParent().hasOneChild()) {
/* 166*/          aliasDefinition.getParent().detachFromParent();
/*   0*/          continue;
/*   0*/        } 
/* 168*/        aliasDefinition.detachFromParent();
/*   0*/      } 
/* 173*/      for (Node scopeCall : traversal.getScopeCalls()) {
/* 174*/        Node expressionWithScopeCall = scopeCall.getParent();
/* 175*/        Node scopeClosureBlock = scopeCall.getLastChild().getLastChild();
/* 176*/        scopeClosureBlock.detachFromParent();
/* 177*/        expressionWithScopeCall.getParent().replaceChild(expressionWithScopeCall, scopeClosureBlock);
/* 180*/        NodeUtil.tryMergeBlock(scopeClosureBlock);
/*   0*/      } 
/* 183*/      if (traversal.getAliasUsages().size() > 0 || traversal.getAliasDefinitionsInOrder().size() > 0 || traversal.getScopeCalls().size() > 0)
/* 186*/        this.compiler.reportCodeChange(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private abstract class AliasUsage {
/*   0*/    final Scope.Var aliasVar;
/*   0*/    
/*   0*/    final Node aliasReference;
/*   0*/    
/*   0*/    AliasUsage(Scope.Var aliasVar, Node aliasReference) {
/* 196*/      this.aliasVar = aliasVar;
/* 197*/      this.aliasReference = aliasReference;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean referencesOtherAlias() {
/* 202*/      Node aliasDefinition = this.aliasVar.getInitialValue();
/* 203*/      Node root = NodeUtil.getRootOfQualifiedName(aliasDefinition);
/* 204*/      Scope.Var otherAliasVar = this.aliasVar.getScope().getOwnSlot(root.getString());
/* 205*/      return (otherAliasVar != null);
/*   0*/    }
/*   0*/    
/*   0*/    public abstract void applyAlias();
/*   0*/  }
/*   0*/  
/*   0*/  private class AliasedNode extends AliasUsage {
/*   0*/    AliasedNode(Scope.Var aliasVar, Node aliasReference) {
/* 213*/      super(aliasVar, aliasReference);
/*   0*/    }
/*   0*/    
/*   0*/    public void applyAlias() {
/* 218*/      Node aliasDefinition = this.aliasVar.getInitialValue();
/* 219*/      this.aliasReference.getParent().replaceChild(this.aliasReference, aliasDefinition.cloneTree());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class AliasedTypeNode extends AliasUsage {
/*   0*/    AliasedTypeNode(Scope.Var aliasVar, Node aliasReference) {
/* 226*/      super(aliasVar, aliasReference);
/*   0*/    }
/*   0*/    
/*   0*/    public void applyAlias() {
/* 231*/      Node aliasDefinition = this.aliasVar.getInitialValue();
/* 232*/      String aliasName = this.aliasVar.getName();
/* 233*/      String typeName = this.aliasReference.getString();
/* 234*/      String aliasExpanded = (String)Preconditions.checkNotNull(aliasDefinition.getQualifiedName());
/* 236*/      Preconditions.checkState(typeName.startsWith(aliasName));
/* 237*/      String replacement = aliasExpanded + typeName.substring(aliasName.length());
/* 239*/      this.aliasReference.setString(replacement);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class Traversal implements NodeTraversal.ScopedCallback {
/* 249*/    private final List<Node> aliasDefinitionsInOrder = Lists.newArrayList();
/*   0*/    
/* 251*/    private final List<Node> scopeCalls = Lists.newArrayList();
/*   0*/    
/* 253*/    private final List<ScopedAliases.AliasUsage> aliasUsages = Lists.newArrayList();
/*   0*/    
/* 256*/    private final Map<String, Scope.Var> aliases = Maps.newHashMap();
/*   0*/    
/* 270*/    private final Set<String> forbiddenLocals = Sets.newHashSet((Object[])new String[] { "$jscomp" });
/*   0*/    
/*   0*/    private boolean hasNamespaceShadows = false;
/*   0*/    
/*   0*/    private boolean hasErrors = false;
/*   0*/    
/* 275*/    private CompilerOptions.AliasTransformation transformation = null;
/*   0*/    
/*   0*/    Collection<Node> getAliasDefinitionsInOrder() {
/* 278*/      return this.aliasDefinitionsInOrder;
/*   0*/    }
/*   0*/    
/*   0*/    private List<ScopedAliases.AliasUsage> getAliasUsages() {
/* 282*/      return this.aliasUsages;
/*   0*/    }
/*   0*/    
/*   0*/    List<Node> getScopeCalls() {
/* 286*/      return this.scopeCalls;
/*   0*/    }
/*   0*/    
/*   0*/    boolean hasErrors() {
/* 290*/      return this.hasErrors;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isCallToScopeMethod(Node n) {
/* 294*/      return (n.isCall() && "goog.scope".equals(n.getFirstChild().getQualifiedName()));
/*   0*/    }
/*   0*/    
/*   0*/    public void enterScope(NodeTraversal t) {
/* 300*/      Node n = t.getCurrentNode().getParent();
/* 301*/      if (n != null && isCallToScopeMethod(n)) {
/* 302*/        this.transformation = ScopedAliases.this.transformationHandler.logAliasTransformation(n.getSourceFileName(), getSourceRegion(n));
/* 304*/        findAliases(t);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void exitScope(NodeTraversal t) {
/* 310*/      if (t.getScopeDepth() > 2)
/* 311*/        findNamespaceShadows(t); 
/* 314*/      if (t.getScopeDepth() == 2) {
/* 315*/        renameNamespaceShadows(t);
/* 316*/        this.aliases.clear();
/* 317*/        this.forbiddenLocals.clear();
/* 318*/        this.transformation = null;
/* 319*/        this.hasNamespaceShadows = false;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public final boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/* 325*/      if (n.isFunction() && t.inGlobalScope())
/* 327*/        if (parent == null || !isCallToScopeMethod(parent))
/* 328*/          return false;  
/* 331*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private SourcePosition<CompilerOptions.AliasTransformation> getSourceRegion(Node n) {
/* 335*/      Node testNode = n;
/* 336*/      Node next = null;
/* 337*/      while (next != null || testNode.isScript()) {
/* 338*/        next = testNode.getNext();
/* 339*/        testNode = testNode.getParent();
/*   0*/      } 
/* 342*/      int endLine = (next == null) ? Integer.MAX_VALUE : next.getLineno();
/* 343*/      int endChar = (next == null) ? Integer.MAX_VALUE : next.getCharno();
/* 344*/      SourcePosition<CompilerOptions.AliasTransformation> pos = new SourcePosition<CompilerOptions.AliasTransformation>() {
/*   0*/        
/*   0*/        };
/* 346*/      pos.setPositionInformation(n.getLineno(), n.getCharno(), endLine, endChar);
/* 348*/      return pos;
/*   0*/    }
/*   0*/    
/*   0*/    private void report(NodeTraversal t, Node n, DiagnosticType error, String... arguments) {
/* 353*/      ScopedAliases.this.compiler.report(t.makeError(n, error, arguments));
/* 354*/      this.hasErrors = true;
/*   0*/    }
/*   0*/    
/*   0*/    private void findAliases(NodeTraversal t) {
/* 358*/      Scope scope = t.getScope();
/* 359*/      for (Scope.Var v : scope.getVarIterable()) {
/* 360*/        Node n = v.getNode();
/* 361*/        Node parent = n.getParent();
/* 362*/        boolean isVar = parent.isVar();
/* 363*/        boolean isFunctionDecl = NodeUtil.isFunctionDeclaration(parent);
/* 364*/        if (isVar && n.getFirstChild() != null && n.getFirstChild().isQualifiedName()) {
/* 365*/          recordAlias(v);
/*   0*/          continue;
/*   0*/        } 
/* 366*/        if (v.isBleedingFunction())
/*   0*/          continue; 
/* 369*/        if (parent.getType() == 83)
/*   0*/          continue; 
/* 372*/        if (isVar || isFunctionDecl) {
/* 373*/          boolean isHoisted = NodeUtil.isHoistedFunctionDeclaration(parent);
/* 374*/          Node grandparent = parent.getParent();
/* 375*/          Node value = (v.getInitialValue() != null) ? v.getInitialValue() : null;
/* 378*/          Node varNode = null;
/* 380*/          String name = n.getString();
/* 381*/          int nameCount = ScopedAliases.this.scopedAliasNames.count(name);
/* 382*/          ScopedAliases.this.scopedAliasNames.add(name);
/* 383*/          String globalName = "$jscomp.scope." + name + ((nameCount == 0) ? "" : ("$" + nameCount));
/* 386*/          ScopedAliases.this.compiler.ensureLibraryInjected("base");
/* 390*/          if (isFunctionDecl) {
/* 392*/            Node existingName = v.getNameNode();
/* 401*/            Node newName = IR.name("").useSourceInfoFrom(existingName);
/* 402*/            value.replaceChild(existingName, newName);
/* 404*/            varNode = IR.var(existingName).useSourceInfoFrom(existingName);
/* 405*/            grandparent.replaceChild(parent, varNode);
/*   0*/          } else {
/* 407*/            if (value != null)
/* 410*/              value.detachFromParent(); 
/* 412*/            varNode = parent;
/*   0*/          } 
/* 417*/          if (value != null || v.getJSDocInfo() != null) {
/* 418*/            Node newDecl = NodeUtil.newQualifiedNameNodeDeclaration(ScopedAliases.this.compiler.getCodingConvention(), globalName, value, new JSDocInfo()).useSourceInfoIfMissingFromForTree(n);
/* 424*/            NodeUtil.setDebugInformation(newDecl.getFirstChild().getFirstChild(), n, name);
/* 427*/            if (isHoisted) {
/* 428*/              grandparent.addChildToFront(newDecl);
/*   0*/            } else {
/* 430*/              grandparent.addChildBefore(newDecl, varNode);
/*   0*/            } 
/*   0*/          } 
/* 435*/          v.getNameNode().addChildToFront(NodeUtil.newQualifiedNameNode(ScopedAliases.this.compiler.getCodingConvention(), globalName, n, name));
/* 439*/          recordAlias(v);
/*   0*/          continue;
/*   0*/        } 
/* 442*/        report(t, n, ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL, new String[] { n.getString() });
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void recordAlias(Scope.Var aliasVar) {
/* 448*/      String name = aliasVar.getName();
/* 449*/      this.aliases.put(name, aliasVar);
/* 451*/      String qualifiedName = aliasVar.getInitialValue().getQualifiedName();
/* 453*/      this.transformation.addAlias(name, qualifiedName);
/* 455*/      int rootIndex = qualifiedName.indexOf(".");
/* 456*/      if (rootIndex != -1) {
/* 457*/        String qNameRoot = qualifiedName.substring(0, rootIndex);
/* 458*/        if (!this.aliases.containsKey(qNameRoot))
/* 459*/          this.forbiddenLocals.add(qNameRoot); 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void findNamespaceShadows(NodeTraversal t) {
/* 466*/      if (this.hasNamespaceShadows)
/*   0*/        return; 
/* 470*/      Scope scope = t.getScope();
/* 471*/      for (Scope.Var v : scope.getVarIterable()) {
/* 472*/        if (this.forbiddenLocals.contains(v.getName())) {
/* 473*/          this.hasNamespaceShadows = true;
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void renameNamespaceShadows(NodeTraversal t) {
/* 485*/      if (this.hasNamespaceShadows) {
/* 486*/        MakeDeclaredNamesUnique.Renamer renamer = new MakeDeclaredNamesUnique.WhitelistedRenamer(new MakeDeclaredNamesUnique.ContextualRenamer(), this.forbiddenLocals);
/* 490*/        for (String s : this.forbiddenLocals)
/* 491*/          renamer.addDeclaredName(s); 
/* 493*/        MakeDeclaredNamesUnique uniquifier = new MakeDeclaredNamesUnique(renamer);
/* 495*/        NodeTraversal.traverse(ScopedAliases.this.compiler, t.getScopeRoot(), uniquifier);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void validateScopeCall(NodeTraversal t, Node n, Node parent) {
/* 500*/      if (ScopedAliases.this.preprocessorSymbolTable != null)
/* 501*/        ScopedAliases.this.preprocessorSymbolTable.addReference(n.getFirstChild()); 
/* 503*/      if (!parent.isExprResult())
/* 504*/        report(t, n, ScopedAliases.GOOG_SCOPE_USED_IMPROPERLY, new String[0]); 
/* 506*/      if (n.getChildCount() != 2) {
/* 509*/        report(t, n, ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS, new String[0]);
/*   0*/      } else {
/* 511*/        Node anonymousFnNode = n.getChildAtIndex(1);
/* 512*/        if (!anonymousFnNode.isFunction() || NodeUtil.getFunctionName(anonymousFnNode) != null || NodeUtil.getFunctionParameters(anonymousFnNode).hasChildren()) {
/* 515*/          report(t, anonymousFnNode, ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS, new String[0]);
/*   0*/        } else {
/* 517*/          this.scopeCalls.add(n);
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 524*/      if (isCallToScopeMethod(n))
/* 525*/        validateScopeCall(t, n, n.getParent()); 
/* 528*/      if (t.getScopeDepth() < 2)
/*   0*/        return; 
/* 532*/      int type = n.getType();
/* 533*/      Scope.Var aliasVar = null;
/* 534*/      if (type == 38) {
/* 535*/        String name = n.getString();
/* 536*/        Scope.Var lexicalVar = t.getScope().getVar(n.getString());
/* 537*/        if (lexicalVar != null && lexicalVar == this.aliases.get(name))
/* 538*/          aliasVar = lexicalVar; 
/*   0*/      } 
/* 543*/      if (t.getScopeDepth() == 2) {
/* 544*/        if (aliasVar != null && NodeUtil.isLValue(n)) {
/* 545*/          if (aliasVar.getNode() == n) {
/* 546*/            this.aliasDefinitionsInOrder.add(n);
/*   0*/            return;
/*   0*/          } 
/* 552*/          report(t, n, ScopedAliases.GOOG_SCOPE_ALIAS_REDEFINED, new String[] { n.getString() });
/*   0*/        } 
/* 556*/        if (type == 4) {
/* 557*/          report(t, n, ScopedAliases.GOOG_SCOPE_USES_RETURN, new String[0]);
/* 558*/        } else if (type == 42) {
/* 559*/          report(t, n, ScopedAliases.GOOG_SCOPE_REFERENCES_THIS, new String[0]);
/* 560*/        } else if (type == 49) {
/* 561*/          report(t, n, ScopedAliases.GOOG_SCOPE_USES_THROW, new String[0]);
/*   0*/        } 
/*   0*/      } 
/* 566*/      if (t.getScopeDepth() >= 2) {
/* 568*/        if (aliasVar != null)
/* 575*/          this.aliasUsages.add(new ScopedAliases.AliasedNode(aliasVar, n)); 
/* 580*/        JSDocInfo info = n.getJSDocInfo();
/* 581*/        if (info != null)
/* 582*/          for (Node node : info.getTypeNodes())
/* 583*/            fixTypeNode(node);  
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void fixTypeNode(Node typeNode) {
/* 592*/      if (typeNode.isString()) {
/* 593*/        String name = typeNode.getString();
/* 594*/        int endIndex = name.indexOf('.');
/* 595*/        if (endIndex == -1)
/* 596*/          endIndex = name.length(); 
/* 598*/        String baseName = name.substring(0, endIndex);
/* 599*/        Scope.Var aliasVar = this.aliases.get(baseName);
/* 600*/        if (aliasVar != null)
/* 601*/          this.aliasUsages.add(new ScopedAliases.AliasedTypeNode(aliasVar, typeNode)); 
/*   0*/      } 
/* 605*/      for (Node child = typeNode.getFirstChild(); child != null; 
/* 606*/        child = child.getNext())
/* 607*/        fixTypeNode(child); 
/*   0*/    }
/*   0*/    
/*   0*/    private Traversal() {}
/*   0*/  }
/*   0*/}
