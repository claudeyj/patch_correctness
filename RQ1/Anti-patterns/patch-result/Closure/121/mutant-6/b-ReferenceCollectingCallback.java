/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.base.Predicates;
/*   0*/import com.google.common.collect.ImmutableSet;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.rhino.InputId;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.StaticReference;
/*   0*/import com.google.javascript.rhino.jstype.StaticScope;
/*   0*/import com.google.javascript.rhino.jstype.StaticSlot;
/*   0*/import com.google.javascript.rhino.jstype.StaticSourceFile;
/*   0*/import com.google.javascript.rhino.jstype.StaticSymbolTable;
/*   0*/import java.util.ArrayDeque;
/*   0*/import java.util.Deque;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class ReferenceCollectingCallback implements NodeTraversal.ScopedCallback, HotSwapCompilerPass, StaticSymbolTable<Scope.Var, ReferenceCollectingCallback.Reference> {
/*  61*/  private final Map<Scope.Var, ReferenceCollection> referenceMap = Maps.newHashMap();
/*   0*/  
/*  67*/  private final Deque<BasicBlock> blockStack = new ArrayDeque<BasicBlock>();
/*   0*/  
/*   0*/  private final Behavior behavior;
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final Predicate<Scope.Var> varFilter;
/*   0*/  
/*   0*/  ReferenceCollectingCallback(AbstractCompiler compiler, Behavior behavior) {
/*  88*/    this(compiler, behavior, Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  ReferenceCollectingCallback(AbstractCompiler compiler, Behavior behavior, Predicate<Scope.Var> varFilter) {
/*  99*/    this.compiler = compiler;
/* 100*/    this.behavior = behavior;
/* 101*/    this.varFilter = varFilter;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 110*/    NodeTraversal.traverseRoots(this.compiler, Lists.newArrayList((Object[])new Node[] { externs, root }), this);
/*   0*/  }
/*   0*/  
/*   0*/  public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/* 119*/    NodeTraversal.traverse(this.compiler, scriptRoot, this);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Scope.Var> getAllSymbols() {
/* 127*/    return this.referenceMap.keySet();
/*   0*/  }
/*   0*/  
/*   0*/  public Scope getScope(Scope.Var var) {
/* 132*/    return var.scope;
/*   0*/  }
/*   0*/  
/*   0*/  public ReferenceCollection getReferences(Scope.Var v) {
/* 140*/    return this.referenceMap.get(v);
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/* 149*/    if (n.isName()) {
/*   0*/      Scope.Var v;
/* 151*/      if (n.getString().equals("arguments")) {
/* 152*/        v = t.getScope().getArgumentsVar();
/*   0*/      } else {
/* 154*/        v = t.getScope().getVar(n.getString());
/*   0*/      } 
/* 156*/      if (v != null && this.varFilter.apply(v))
/* 157*/        addReference(v, new Reference(n, t, this.blockStack.peek())); 
/*   0*/    } 
/* 161*/    if (isBlockBoundary(n, parent))
/* 162*/      this.blockStack.pop(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void enterScope(NodeTraversal t) {
/* 171*/    Node n = t.getScope().getRootNode();
/* 172*/    BasicBlock parent = this.blockStack.isEmpty() ? null : this.blockStack.peek();
/* 173*/    this.blockStack.push(new BasicBlock(parent, n));
/*   0*/  }
/*   0*/  
/*   0*/  public void exitScope(NodeTraversal t) {
/* 181*/    this.blockStack.pop();
/* 182*/    if (t.getScope().isGlobal()) {
/* 184*/      this.compiler.updateGlobalVarReferences(this.referenceMap, t.getScopeRoot());
/* 185*/      this.behavior.afterExitScope(t, this.compiler.getGlobalVarReferences());
/*   0*/    } else {
/* 187*/      this.behavior.afterExitScope(t, new ReferenceMapWrapper(this.referenceMap));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
/* 198*/    if (isBlockBoundary(n, parent))
/* 199*/      this.blockStack.push(new BasicBlock(this.blockStack.peek(), n)); 
/* 201*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isBlockBoundary(Node n, Node parent) {
/* 208*/    if (parent != null)
/* 209*/      switch (parent.getType()) {
/*   0*/        case 77:
/*   0*/        case 113:
/*   0*/        case 114:
/*   0*/        case 115:
/*   0*/        case 119:
/* 224*/          return true;
/*   0*/        case 98:
/*   0*/        case 100:
/*   0*/        case 101:
/*   0*/        case 108:
/* 231*/          return (n != parent.getFirstChild());
/*   0*/      }  
/* 236*/    return n.isCase();
/*   0*/  }
/*   0*/  
/*   0*/  private void addReference(Scope.Var v, Reference reference) {
/* 241*/    ReferenceCollection referenceInfo = this.referenceMap.get(v);
/* 242*/    if (referenceInfo == null) {
/* 243*/      referenceInfo = new ReferenceCollection();
/* 244*/      this.referenceMap.put(v, referenceInfo);
/*   0*/    } 
/* 248*/    referenceInfo.add(reference);
/*   0*/  }
/*   0*/  
/*   0*/  private static class ReferenceMapWrapper implements ReferenceMap {
/*   0*/    private final Map<Scope.Var, ReferenceCollectingCallback.ReferenceCollection> referenceMap;
/*   0*/    
/*   0*/    public ReferenceMapWrapper(Map<Scope.Var, ReferenceCollectingCallback.ReferenceCollection> referenceMap) {
/* 259*/      this.referenceMap = referenceMap;
/*   0*/    }
/*   0*/    
/*   0*/    public ReferenceCollectingCallback.ReferenceCollection getReferences(Scope.Var var) {
/* 264*/      return this.referenceMap.get(var);
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 279*/  static final Behavior DO_NOTHING_BEHAVIOR = new Behavior() {
/*   0*/      public void afterExitScope(NodeTraversal t, ReferenceCollectingCallback.ReferenceMap referenceMap) {}
/*   0*/    };
/*   0*/  
/*   0*/  static class ReferenceCollection implements Iterable<Reference> {
/* 290*/    List<ReferenceCollectingCallback.Reference> references = Lists.newArrayList();
/*   0*/    
/*   0*/    public Iterator<ReferenceCollectingCallback.Reference> iterator() {
/* 294*/      return this.references.iterator();
/*   0*/    }
/*   0*/    
/*   0*/    void add(ReferenceCollectingCallback.Reference reference) {
/* 298*/      this.references.add(reference);
/*   0*/    }
/*   0*/    
/*   0*/    protected boolean isWellDefined() {
/* 311*/      int size = this.references.size();
/* 312*/      if (size == 0)
/* 313*/        return false; 
/* 318*/      ReferenceCollectingCallback.Reference init = getInitializingReference();
/* 319*/      if (init == null)
/* 320*/        return false; 
/* 323*/      Preconditions.checkState(((ReferenceCollectingCallback.Reference)this.references.get(0)).isDeclaration());
/* 324*/      ReferenceCollectingCallback.BasicBlock initBlock = init.getBasicBlock();
/* 325*/      for (int i = 0; i < size; i++) {
/* 326*/        if (!initBlock.provablyExecutesBefore(((ReferenceCollectingCallback.Reference)this.references.get(i)).getBasicBlock()))
/* 328*/          return false; 
/*   0*/      } 
/* 332*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEscaped() {
/* 339*/      Scope scope = null;
/* 340*/      for (ReferenceCollectingCallback.Reference ref : this.references) {
/* 341*/        if (scope == null) {
/* 342*/          scope = ref.scope;
/*   0*/          continue;
/*   0*/        } 
/* 343*/        if (scope != ref.scope)
/* 344*/          return true; 
/*   0*/      } 
/* 347*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isInitializingDeclarationAt(int index) {
/* 358*/      ReferenceCollectingCallback.Reference maybeInit = this.references.get(index);
/* 359*/      if (maybeInit.isInitializingDeclaration())
/* 363*/        return true; 
/* 365*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isInitializingAssignmentAt(int index) {
/* 375*/      if (index < this.references.size() && index > 0) {
/* 376*/        ReferenceCollectingCallback.Reference maybeDecl = this.references.get(index - 1);
/* 377*/        if (maybeDecl.isVarDeclaration()) {
/* 378*/          Preconditions.checkState(!maybeDecl.isInitializingDeclaration());
/* 379*/          ReferenceCollectingCallback.Reference maybeInit = this.references.get(index);
/* 380*/          if (maybeInit.isSimpleAssignmentToName())
/* 381*/            return true; 
/*   0*/        } 
/*   0*/      } 
/* 385*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    ReferenceCollectingCallback.Reference getInitializingReference() {
/* 396*/      if (isInitializingDeclarationAt(0))
/* 397*/        return this.references.get(0); 
/* 398*/      if (isInitializingAssignmentAt(1))
/* 399*/        return this.references.get(1); 
/* 401*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    ReferenceCollectingCallback.Reference getInitializingReferenceForConstants() {
/* 408*/      int size = this.references.size();
/* 409*/      for (int i = 0; i < size; i++) {
/* 410*/        if (isInitializingDeclarationAt(i) || isInitializingAssignmentAt(i))
/* 411*/          return this.references.get(i); 
/*   0*/      } 
/* 414*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isAssignedOnceInLifetime() {
/* 422*/      ReferenceCollectingCallback.Reference ref = getOneAndOnlyAssignment();
/* 423*/      if (ref == null)
/* 424*/        return false; 
/* 428*/      ReferenceCollectingCallback.BasicBlock block = ref.getBasicBlock();
/* 429*/      for (; block != null && 
/* 430*/        !block.isFunction; block = block.getParent()) {
/* 432*/        if (block.isLoop)
/* 433*/          return false; 
/*   0*/      } 
/* 437*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isOnlyAssignmentSameScopeAsDeclaration() {
/* 445*/      ReferenceCollectingCallback.Reference ref = getOneAndOnlyAssignment();
/* 446*/      Preconditions.checkNotNull(ref);
/* 451*/      ReferenceCollectingCallback.BasicBlock block = ref.getBasicBlock();
/* 452*/      for (; block != null; block = block.getParent()) {
/* 453*/        if (block.isFunction && 
/* 454*/          ref.getSymbol().getScope() != ref.scope)
/* 455*/          return false; 
/*   0*/      } 
/* 460*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private ReferenceCollectingCallback.Reference getOneAndOnlyAssignment() {
/* 468*/      ReferenceCollectingCallback.Reference assignment = null;
/* 469*/      int size = this.references.size();
/* 470*/      for (int i = 0; i < size; i++) {
/* 471*/        ReferenceCollectingCallback.Reference ref = this.references.get(i);
/* 472*/        if (ref.isLvalue() || ref.isInitializingDeclaration())
/* 473*/          if (assignment == null) {
/* 474*/            assignment = ref;
/*   0*/          } else {
/* 476*/            return null;
/*   0*/          }  
/*   0*/      } 
/* 480*/      return assignment;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isNeverAssigned() {
/* 487*/      int size = this.references.size();
/* 488*/      for (int i = 0; i < size; i++) {
/* 489*/        ReferenceCollectingCallback.Reference ref = this.references.get(i);
/* 490*/        if (ref.isLvalue() || ref.isInitializingDeclaration())
/* 491*/          return false; 
/*   0*/      } 
/* 494*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean firstReferenceIsAssigningDeclaration() {
/* 498*/      int size = this.references.size();
/* 499*/      if (size > 0 && ((ReferenceCollectingCallback.Reference)this.references.get(0)).isInitializingDeclaration())
/* 500*/        return true; 
/* 502*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class Reference implements StaticReference<JSType> {
/* 511*/    private static final Set<Integer> DECLARATION_PARENTS = (Set<Integer>)ImmutableSet.of(118, 105, 120);
/*   0*/    
/*   0*/    private final Node nameNode;
/*   0*/    
/*   0*/    private final ReferenceCollectingCallback.BasicBlock basicBlock;
/*   0*/    
/*   0*/    private final Scope scope;
/*   0*/    
/*   0*/    private final InputId inputId;
/*   0*/    
/*   0*/    private final StaticSourceFile sourceFile;
/*   0*/    
/*   0*/    Reference(Node nameNode, NodeTraversal t, ReferenceCollectingCallback.BasicBlock basicBlock) {
/* 522*/      this(nameNode, basicBlock, t.getScope(), t.getInput().getInputId());
/*   0*/    }
/*   0*/    
/*   0*/    static Reference newBleedingFunction(NodeTraversal t, ReferenceCollectingCallback.BasicBlock basicBlock, Node func) {
/* 529*/      return new Reference(func.getFirstChild(), basicBlock, t.getScope(), t.getInput().getInputId());
/*   0*/    }
/*   0*/    
/*   0*/    @VisibleForTesting
/*   0*/    static Reference createRefForTest(CompilerInput input) {
/* 540*/      return new Reference(new Node(38), null, null, input.getInputId());
/*   0*/    }
/*   0*/    
/*   0*/    private Reference(Node nameNode, ReferenceCollectingCallback.BasicBlock basicBlock, Scope scope, InputId inputId) {
/* 546*/      this.nameNode = nameNode;
/* 547*/      this.basicBlock = basicBlock;
/* 548*/      this.scope = scope;
/* 549*/      this.inputId = inputId;
/* 550*/      this.sourceFile = nameNode.getStaticSourceFile();
/*   0*/    }
/*   0*/    
/*   0*/    Reference cloneWithNewScope(Scope newScope) {
/* 557*/      return new Reference(this.nameNode, this.basicBlock, newScope, this.inputId);
/*   0*/    }
/*   0*/    
/*   0*/    public Scope.Var getSymbol() {
/* 562*/      return this.scope.getVar(this.nameNode.getString());
/*   0*/    }
/*   0*/    
/*   0*/    public Node getNode() {
/* 567*/      return this.nameNode;
/*   0*/    }
/*   0*/    
/*   0*/    public InputId getInputId() {
/* 571*/      return this.inputId;
/*   0*/    }
/*   0*/    
/*   0*/    public StaticSourceFile getSourceFile() {
/* 576*/      return this.sourceFile;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isDeclaration() {
/* 580*/      Node parent = getParent();
/* 581*/      Node grandparent = parent.getParent();
/* 582*/      return (DECLARATION_PARENTS.contains(parent.getType()) || (parent.isParamList() && grandparent.isFunction()));
/*   0*/    }
/*   0*/    
/*   0*/    boolean isVarDeclaration() {
/* 588*/      return getParent().isVar();
/*   0*/    }
/*   0*/    
/*   0*/    boolean isHoistedFunction() {
/* 592*/      return NodeUtil.isHoistedFunctionDeclaration(getParent());
/*   0*/    }
/*   0*/    
/*   0*/    boolean isInitializingDeclaration() {
/* 601*/      return ((isDeclaration() && !getParent().isVar()) || this.nameNode.getFirstChild() != null);
/*   0*/    }
/*   0*/    
/*   0*/    Node getAssignedValue() {
/* 611*/      Node parent = getParent();
/* 612*/      return parent.isFunction() ? parent : NodeUtil.getAssignedValue(this.nameNode);
/*   0*/    }
/*   0*/    
/*   0*/    ReferenceCollectingCallback.BasicBlock getBasicBlock() {
/* 617*/      return this.basicBlock;
/*   0*/    }
/*   0*/    
/*   0*/    Node getParent() {
/* 621*/      return getNode().getParent();
/*   0*/    }
/*   0*/    
/*   0*/    Node getGrandparent() {
/* 625*/      Node parent = getParent();
/* 626*/      return (parent == null) ? null : parent.getParent();
/*   0*/    }
/*   0*/    
/*   0*/    private static boolean isLhsOfForInExpression(Node n) {
/* 630*/      Node parent = n.getParent();
/* 631*/      if (parent.isVar())
/* 632*/        return isLhsOfForInExpression(parent); 
/* 634*/      return (NodeUtil.isForIn(parent) && parent.getFirstChild() == n);
/*   0*/    }
/*   0*/    
/*   0*/    boolean isSimpleAssignmentToName() {
/* 638*/      Node parent = getParent();
/* 639*/      return (parent.isAssign() && parent.getFirstChild() == this.nameNode);
/*   0*/    }
/*   0*/    
/*   0*/    boolean isLvalue() {
/* 644*/      Node parent = getParent();
/* 645*/      int parentType = parent.getType();
/* 646*/      return ((parentType == 118 && this.nameNode.getFirstChild() != null) || parentType == 102 || parentType == 103 || (NodeUtil.isAssignmentOp(parent) && parent.getFirstChild() == this.nameNode) || isLhsOfForInExpression(this.nameNode));
/*   0*/    }
/*   0*/    
/*   0*/    Scope getScope() {
/* 655*/      return this.scope;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class BasicBlock {
/*   0*/    private final BasicBlock parent;
/*   0*/    
/*   0*/    private final boolean isHoisted;
/*   0*/    
/*   0*/    private final boolean isFunction;
/*   0*/    
/*   0*/    private final boolean isLoop;
/*   0*/    
/*   0*/    BasicBlock(BasicBlock parent, Node root) {
/* 689*/      this.parent = parent;
/* 692*/      this.isHoisted = NodeUtil.isHoistedFunctionDeclaration(root);
/* 694*/      this.isFunction = root.isFunction();
/* 696*/      if (root.getParent() != null) {
/* 697*/        int pType = root.getParent().getType();
/* 698*/        this.isLoop = (pType == 114 || pType == 113 || pType == 115);
/*   0*/      } else {
/* 702*/        this.isLoop = false;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    BasicBlock getParent() {
/* 707*/      return this.parent;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isGlobalScopeBlock() {
/* 719*/      return (getParent() == null);
/*   0*/    }
/*   0*/    
/*   0*/    boolean provablyExecutesBefore(BasicBlock thatBlock) {
/* 730*/      BasicBlock currentBlock = thatBlock;
/* 731*/      for (; currentBlock != null && currentBlock != this; 
/* 732*/        currentBlock = currentBlock.getParent()) {
/* 733*/        if (currentBlock.isHoisted)
/* 734*/          return false; 
/*   0*/      } 
/* 738*/      if (currentBlock == this)
/* 739*/        return true; 
/* 741*/      if (isGlobalScopeBlock() && thatBlock.isGlobalScopeBlock())
/* 742*/        return true; 
/* 744*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static interface Behavior {
/*   0*/    void afterExitScope(NodeTraversal param1NodeTraversal, ReferenceCollectingCallback.ReferenceMap param1ReferenceMap);
/*   0*/  }
/*   0*/  
/*   0*/  static interface ReferenceMap {
/*   0*/    ReferenceCollectingCallback.ReferenceCollection getReferences(Scope.Var param1Var);
/*   0*/  }
/*   0*/}
