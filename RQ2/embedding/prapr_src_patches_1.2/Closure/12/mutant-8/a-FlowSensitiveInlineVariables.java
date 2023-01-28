/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.base.Predicates;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.jscomp.graph.DiGraph;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.Collection;
/*   0*/import java.util.List;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class FlowSensitiveInlineVariables extends NodeTraversal.AbstractPostOrderCallback implements CompilerPass, NodeTraversal.ScopedCallback {
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*  77*/  private final Set<Scope.Var> inlinedNewDependencies = Sets.newHashSet();
/*   0*/  
/*   0*/  private ControlFlowGraph<Node> cfg;
/*   0*/  
/*   0*/  private List<Candidate> candidates;
/*   0*/  
/*   0*/  private MustBeReachingVariableDef reachingDef;
/*   0*/  
/*   0*/  private MaybeReachingVariableUse reachingUses;
/*   0*/  
/*  86*/  private static final Predicate<Node> SIDE_EFFECT_PREDICATE = new Predicate<Node>() {
/*   0*/      public boolean apply(Node n) {
/*  92*/        if (n == null)
/*  93*/          return false; 
/*  99*/        if (n.isCall() && NodeUtil.functionCallHasSideEffects(n))
/* 100*/          return true; 
/* 103*/        if (n.isNew() && NodeUtil.constructorCallHasSideEffects(n))
/* 104*/          return true; 
/* 107*/        if (n.isDelProp())
/* 108*/          return true; 
/* 111*/        for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 112*/          if (!ControlFlowGraph.isEnteringNewCfgNode(c) && apply(c))
/* 113*/            return true; 
/*   0*/        } 
/* 116*/        return false;
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  public FlowSensitiveInlineVariables(AbstractCompiler compiler) {
/* 121*/    this.compiler = compiler;
/*   0*/  }
/*   0*/  
/*   0*/  public void enterScope(NodeTraversal t) {
/* 127*/    if (t.inGlobalScope())
/*   0*/      return; 
/* 131*/    if (100 < t.getScope().getVarCount())
/*   0*/      return; 
/* 137*/    ControlFlowAnalysis cfa = new ControlFlowAnalysis(this.compiler, false, true);
/* 139*/    Preconditions.checkState(t.getScopeRoot().isFunction());
/* 140*/    cfa.process(null, t.getScopeRoot().getLastChild());
/* 141*/    this.cfg = cfa.getCfg();
/* 142*/    this.reachingDef = new MustBeReachingVariableDef(this.cfg, t.getScope(), this.compiler);
/* 143*/    this.reachingDef.analyze();
/* 144*/    this.candidates = Lists.newLinkedList();
/* 148*/    new NodeTraversal(this.compiler, new GatherCandiates()).traverse(t.getScopeRoot().getLastChild());
/* 152*/    this.reachingUses = new MaybeReachingVariableUse(this.cfg, t.getScope(), this.compiler);
/* 153*/    this.reachingUses.analyze();
/* 154*/    for (Candidate c : this.candidates) {
/* 155*/      if (c.canInline()) {
/* 156*/        c.inlineVariable();
/* 165*/        if (!c.defMetadata.depends.isEmpty())
/* 166*/          this.inlinedNewDependencies.add(t.getScope().getVar(c.varName)); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void exitScope(NodeTraversal t) {}
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 177*/    new NodeTraversal(this.compiler, this).traverseRoots(new Node[] { externs, root });
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {}
/*   0*/  
/*   0*/  private class GatherCandiates extends NodeTraversal.AbstractShallowCallback {
/*   0*/    private GatherCandiates() {}
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 199*/      DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> graphNode = FlowSensitiveInlineVariables.this.cfg.getDirectedGraphNode(n);
/* 200*/      if (graphNode == null)
/*   0*/        return; 
/* 204*/      DataFlowAnalysis.FlowState<MustBeReachingVariableDef.MustDef> state = (DataFlowAnalysis.FlowState<MustBeReachingVariableDef.MustDef>)graphNode.getAnnotation();
/* 205*/      MustBeReachingVariableDef.MustDef defs = state.getIn();
/* 206*/      final Node cfgNode = n;
/* 207*/      ControlFlowGraph.AbstractCfgNodeTraversalCallback gatherCb = new ControlFlowGraph.AbstractCfgNodeTraversalCallback() {
/*   0*/          public void visit(NodeTraversal t, Node n, Node parent) {
/* 212*/            if (n.isName()) {
/* 216*/              if (parent == null)
/*   0*/                return; 
/* 221*/              if ((NodeUtil.isAssignmentOp(parent) && parent.getFirstChild() == n) || parent.isVar() || parent.isInc() || parent.isDec() || parent.isParamList() || parent.isCatch())
/*   0*/                return; 
/* 227*/              String name = n.getString();
/* 228*/              if (FlowSensitiveInlineVariables.this.compiler.getCodingConvention().isExported(name))
/*   0*/                return; 
/* 232*/              MustBeReachingVariableDef.Definition def = FlowSensitiveInlineVariables.this.reachingDef.getDef(name, cfgNode);
/* 235*/              if (def != null && !FlowSensitiveInlineVariables.this.reachingDef.dependsOnOuterScopeVars(def))
/* 237*/                FlowSensitiveInlineVariables.this.candidates.add(new FlowSensitiveInlineVariables.Candidate(name, def, n, cfgNode)); 
/*   0*/            } 
/*   0*/          }
/*   0*/        };
/* 243*/      NodeTraversal.traverse(FlowSensitiveInlineVariables.this.compiler, cfgNode, gatherCb);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class Candidate {
/*   0*/    private final String varName;
/*   0*/    
/*   0*/    private Node def;
/*   0*/    
/*   0*/    private final MustBeReachingVariableDef.Definition defMetadata;
/*   0*/    
/*   0*/    private final Node use;
/*   0*/    
/*   0*/    private final Node useCfgNode;
/*   0*/    
/*   0*/    private int numUseWithinUseCfgNode;
/*   0*/    
/*   0*/    Candidate(String varName, MustBeReachingVariableDef.Definition defMetadata, Node use, Node useCfgNode) {
/* 269*/      Preconditions.checkArgument(use.isName());
/* 270*/      this.varName = varName;
/* 271*/      this.defMetadata = defMetadata;
/* 272*/      this.use = use;
/* 273*/      this.useCfgNode = useCfgNode;
/*   0*/    }
/*   0*/    
/*   0*/    private Node getDefCfgNode() {
/* 277*/      return this.defMetadata.node;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean canInline() {
/* 282*/      if (getDefCfgNode().isFunction())
/* 283*/        return false; 
/* 289*/      for (Scope.Var dependency : this.defMetadata.depends) {
/* 290*/        if (FlowSensitiveInlineVariables.this.inlinedNewDependencies.contains(dependency))
/* 291*/          return false; 
/*   0*/      } 
/* 295*/      getDefinition(getDefCfgNode(), null);
/* 296*/      getNumUseInUseCfgNode(this.useCfgNode, null);
/* 299*/      if (this.def == null)
/* 300*/        return false; 
/* 305*/      if (this.def.isAssign() && !NodeUtil.isExprAssign(this.def.getParent()))
/* 306*/        return false; 
/* 312*/      if (FlowSensitiveInlineVariables.checkRightOf(this.def, getDefCfgNode(), FlowSensitiveInlineVariables.SIDE_EFFECT_PREDICATE))
/* 313*/        return false; 
/* 319*/      if (FlowSensitiveInlineVariables.checkLeftOf(this.use, this.useCfgNode, FlowSensitiveInlineVariables.SIDE_EFFECT_PREDICATE))
/* 320*/        return false; 
/* 326*/      if (NodeUtil.mayHaveSideEffects(this.def.getLastChild()))
/* 327*/        return false; 
/* 335*/      if (this.numUseWithinUseCfgNode != 1)
/* 336*/        return false; 
/* 340*/      if (NodeUtil.isWithinLoop(this.use))
/* 341*/        return false; 
/* 345*/      Collection<Node> uses = FlowSensitiveInlineVariables.this.reachingUses.getUses(this.varName, getDefCfgNode());
/* 347*/      if (uses.size() != 1)
/* 348*/        return false; 
/* 358*/      if (NodeUtil.has(this.def.getLastChild(), new Predicate<Node>() {
/*   0*/            public boolean apply(Node input) {
/* 362*/              switch (input.getType()) {
/*   0*/                case 30:
/*   0*/                case 33:
/*   0*/                case 35:
/*   0*/                case 47:
/*   0*/                case 63:
/*   0*/                case 64:
/* 369*/                  return true;
/*   0*/              } 
/* 371*/              return false;
/*   0*/            }
/*   0*/          },  new Predicate<Node>() {
/*   0*/            public boolean apply(Node input) {
/* 378*/              return !input.isFunction();
/*   0*/            }
/*   0*/          }))
/* 381*/        return false; 
/* 386*/      if (NodeUtil.isStatementBlock(getDefCfgNode().getParent()) && getDefCfgNode().getNext() != this.useCfgNode) {
/* 392*/        CheckPathsBetweenNodes<Node, ControlFlowGraph.Branch> pathCheck = new CheckPathsBetweenNodes<Node, ControlFlowGraph.Branch>(FlowSensitiveInlineVariables.this.cfg, FlowSensitiveInlineVariables.this.cfg.getDirectedGraphNode(getDefCfgNode()), FlowSensitiveInlineVariables.this.cfg.getDirectedGraphNode(this.useCfgNode), FlowSensitiveInlineVariables.SIDE_EFFECT_PREDICATE, Predicates.alwaysTrue(), false);
/* 400*/        if (pathCheck.somePathsSatisfyPredicate())
/* 401*/          return false; 
/*   0*/      } 
/* 405*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private void inlineVariable() {
/* 412*/      Node defParent = this.def.getParent();
/* 413*/      Node useParent = this.use.getParent();
/* 414*/      if (this.def.isAssign()) {
/* 415*/        Node rhs = this.def.getLastChild();
/* 416*/        rhs.detachFromParent();
/* 418*/        Preconditions.checkState(defParent.isExprResult());
/* 419*/        while (defParent.getParent().isLabel())
/* 420*/          defParent = defParent.getParent(); 
/* 422*/        defParent.detachFromParent();
/* 423*/        useParent.replaceChild(this.use, rhs);
/* 424*/      } else if (defParent.isVar()) {
/* 425*/        Node rhs = this.def.getLastChild();
/* 426*/        this.def.removeChild(rhs);
/* 427*/        useParent.replaceChild(this.use, rhs);
/*   0*/      } else {
/* 429*/        Preconditions.checkState(false, "No other definitions can be inlined.");
/*   0*/      } 
/* 431*/      FlowSensitiveInlineVariables.this.compiler.reportCodeChange();
/*   0*/    }
/*   0*/    
/*   0*/    private void getDefinition(Node n, Node parent) {
/* 440*/      ControlFlowGraph.AbstractCfgNodeTraversalCallback gatherCb = new ControlFlowGraph.AbstractCfgNodeTraversalCallback() {
/*   0*/          public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/            Node lhs;
/* 445*/            switch (n.getType()) {
/*   0*/              case 38:
/* 447*/                if (n.getString().equals(FlowSensitiveInlineVariables.Candidate.this.varName) && n.hasChildren())
/* 448*/                  FlowSensitiveInlineVariables.Candidate.this.def = n; 
/*   0*/                return;
/*   0*/              case 86:
/* 453*/                lhs = n.getFirstChild();
/* 454*/                if (lhs.isName() && lhs.getString().equals(FlowSensitiveInlineVariables.Candidate.this.varName))
/* 455*/                  FlowSensitiveInlineVariables.Candidate.this.def = n; 
/*   0*/                return;
/*   0*/            } 
/*   0*/          }
/*   0*/        };
/* 461*/      NodeTraversal.traverse(FlowSensitiveInlineVariables.this.compiler, n, gatherCb);
/*   0*/    }
/*   0*/    
/*   0*/    private void getNumUseInUseCfgNode(Node n, Node parant) {
/* 470*/      ControlFlowGraph.AbstractCfgNodeTraversalCallback gatherCb = new ControlFlowGraph.AbstractCfgNodeTraversalCallback() {
/*   0*/          public void visit(NodeTraversal t, Node n, Node parent) {
/* 475*/            if (n.isName() && n.getString().equals(FlowSensitiveInlineVariables.Candidate.this.varName) && (!parent.isAssign() || parent.getFirstChild() != n))
/* 479*/              FlowSensitiveInlineVariables.Candidate.this.numUseWithinUseCfgNode++; 
/*   0*/          }
/*   0*/        };
/* 484*/      NodeTraversal.traverse(FlowSensitiveInlineVariables.this.compiler, n, gatherCb);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean checkRightOf(Node n, Node expressionRoot, Predicate<Node> predicate) {
/* 498*/    for (Node p = n; p != expressionRoot; p = p.getParent()) {
/* 499*/      for (Node cur = p.getNext(); cur != null; cur = cur.getNext()) {
/* 500*/        if (predicate.apply(cur))
/* 501*/          return true; 
/*   0*/      } 
/*   0*/    } 
/* 505*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean checkLeftOf(Node n, Node expressionRoot, Predicate<Node> predicate) {
/* 518*/    for (Node p = n.getParent(); p != expressionRoot; p = p.getParent()) {
/* 519*/      for (Node cur = p.getParent().getFirstChild(); cur != p; 
/* 520*/        cur = cur.getNext()) {
/* 521*/        if (predicate.apply(cur))
/* 522*/          return true; 
/*   0*/      } 
/*   0*/    } 
/* 526*/    return false;
/*   0*/  }
/*   0*/}
