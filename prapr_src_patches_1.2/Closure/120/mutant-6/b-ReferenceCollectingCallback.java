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
/*   0*/    private ReferenceCollectingCallback.Reference getOneAndOnlyAssignment() {
/* 445*/      ReferenceCollectingCallback.Reference assignment = null;
/* 446*/      int size = this.references.size();
/* 447*/      for (int i = 0; i < size; i++) {
/* 448*/        ReferenceCollectingCallback.Reference ref = this.references.get(i);
/* 449*/        if (ref.isLvalue() || ref.isInitializingDeclaration())
/* 450*/          if (assignment == null) {
/* 451*/            assignment = ref;
/*   0*/          } else {
/* 453*/            return null;
/*   0*/          }  
/*   0*/      } 
/* 457*/      return assignment;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isNeverAssigned() {
/* 464*/      int size = this.references.size();
/* 465*/      for (int i = 0; i < size; i++) {
/* 466*/        ReferenceCollectingCallback.Reference ref = this.references.get(i);
/* 467*/        if (ref.isLvalue() || ref.isInitializingDeclaration())
/* 468*/          return false; 
/*   0*/      } 
/* 471*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean firstReferenceIsAssigningDeclaration() {
/* 475*/      int size = this.references.size();
/* 476*/      if (size > 0 && ((ReferenceCollectingCallback.Reference)this.references.get(0)).isInitializingDeclaration())
/* 477*/        return true; 
/* 479*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class Reference implements StaticReference<JSType> {
/* 488*/    private static final Set<Integer> DECLARATION_PARENTS = (Set<Integer>)ImmutableSet.of(118, 105, 120);
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
/* 499*/      this(nameNode, basicBlock, t.getScope(), t.getInput().getInputId());
/*   0*/    }
/*   0*/    
/*   0*/    static Reference newBleedingFunction(NodeTraversal t, ReferenceCollectingCallback.BasicBlock basicBlock, Node func) {
/* 506*/      return new Reference(func.getFirstChild(), basicBlock, t.getScope(), t.getInput().getInputId());
/*   0*/    }
/*   0*/    
/*   0*/    @VisibleForTesting
/*   0*/    static Reference createRefForTest(CompilerInput input) {
/* 517*/      return new Reference(new Node(38), null, null, input.getInputId());
/*   0*/    }
/*   0*/    
/*   0*/    private Reference(Node nameNode, ReferenceCollectingCallback.BasicBlock basicBlock, Scope scope, InputId inputId) {
/* 523*/      this.nameNode = nameNode;
/* 524*/      this.basicBlock = basicBlock;
/* 525*/      this.scope = scope;
/* 526*/      this.inputId = inputId;
/* 527*/      this.sourceFile = nameNode.getStaticSourceFile();
/*   0*/    }
/*   0*/    
/*   0*/    Reference cloneWithNewScope(Scope newScope) {
/* 534*/      return new Reference(this.nameNode, this.basicBlock, newScope, this.inputId);
/*   0*/    }
/*   0*/    
/*   0*/    public Scope.Var getSymbol() {
/* 539*/      return this.scope.getVar(this.nameNode.getString());
/*   0*/    }
/*   0*/    
/*   0*/    public Node getNode() {
/* 544*/      return this.nameNode;
/*   0*/    }
/*   0*/    
/*   0*/    public InputId getInputId() {
/* 548*/      return this.inputId;
/*   0*/    }
/*   0*/    
/*   0*/    public StaticSourceFile getSourceFile() {
/* 553*/      return this.sourceFile;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isDeclaration() {
/* 557*/      Node parent = getParent();
/* 558*/      Node grandparent = parent.getParent();
/* 559*/      return (DECLARATION_PARENTS.contains(parent.getType()) || (parent.isParamList() && grandparent.isFunction()));
/*   0*/    }
/*   0*/    
/*   0*/    boolean isVarDeclaration() {
/* 565*/      return getParent().isVar();
/*   0*/    }
/*   0*/    
/*   0*/    boolean isHoistedFunction() {
/* 569*/      return NodeUtil.isHoistedFunctionDeclaration(getParent());
/*   0*/    }
/*   0*/    
/*   0*/    boolean isInitializingDeclaration() {
/* 578*/      return ((isDeclaration() && !getParent().isVar()) || this.nameNode.getFirstChild() != null);
/*   0*/    }
/*   0*/    
/*   0*/    Node getAssignedValue() {
/* 588*/      Node parent = getParent();
/* 589*/      return parent.isFunction() ? parent : NodeUtil.getAssignedValue(this.nameNode);
/*   0*/    }
/*   0*/    
/*   0*/    ReferenceCollectingCallback.BasicBlock getBasicBlock() {
/* 594*/      return this.basicBlock;
/*   0*/    }
/*   0*/    
/*   0*/    Node getParent() {
/* 598*/      return getNode().getParent();
/*   0*/    }
/*   0*/    
/*   0*/    Node getGrandparent() {
/* 602*/      Node parent = getParent();
/* 603*/      return (parent == null) ? null : parent.getParent();
/*   0*/    }
/*   0*/    
/*   0*/    private static boolean isLhsOfForInExpression(Node n) {
/* 607*/      Node parent = n.getParent();
/* 608*/      if (parent.isVar())
/* 609*/        return isLhsOfForInExpression(parent); 
/* 611*/      return (NodeUtil.isForIn(parent) && parent.getFirstChild() == n);
/*   0*/    }
/*   0*/    
/*   0*/    boolean isSimpleAssignmentToName() {
/* 615*/      Node parent = getParent();
/* 616*/      return (parent.isAssign() && parent.getFirstChild() == this.nameNode);
/*   0*/    }
/*   0*/    
/*   0*/    boolean isLvalue() {
/* 621*/      Node parent = getParent();
/* 622*/      int parentType = parent.getType();
/* 623*/      return ((parentType == 118 && this.nameNode.getFirstChild() != null) || parentType == 102 || parentType == 103 || (NodeUtil.isAssignmentOp(parent) && parent.getFirstChild() == this.nameNode) || isLhsOfForInExpression(this.nameNode));
/*   0*/    }
/*   0*/    
/*   0*/    Scope getScope() {
/* 632*/      return this.scope;
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
/* 666*/      this.parent = parent;
/* 669*/      this.isHoisted = NodeUtil.isHoistedFunctionDeclaration(root);
/* 671*/      this.isFunction = root.isFunction();
/* 673*/      if (root.getParent() != null) {
/* 674*/        int pType = root.getParent().getType();
/* 675*/        this.isLoop = (pType == 114 || pType == 113 || pType == 115);
/*   0*/      } else {
/* 679*/        this.isLoop = false;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    BasicBlock getParent() {
/* 684*/      return this.parent;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isGlobalScopeBlock() {
/* 696*/      return (getParent() == null);
/*   0*/    }
/*   0*/    
/*   0*/    boolean provablyExecutesBefore(BasicBlock thatBlock) {
/* 707*/      BasicBlock currentBlock = thatBlock;
/* 708*/      for (; currentBlock != null && currentBlock != this; 
/* 709*/        currentBlock = currentBlock.getParent()) {
/* 710*/        if (currentBlock.isHoisted)
/* 711*/          return false; 
/*   0*/      } 
/* 715*/      if (currentBlock == this)
/* 716*/        return true; 
/* 718*/      if (isGlobalScopeBlock() && thatBlock.isGlobalScopeBlock())
/* 719*/        return true; 
/* 721*/      return false;
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
