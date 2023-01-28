/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.HashMultimap;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Multimap;
/*   0*/import com.google.javascript.jscomp.graph.DiGraph;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.ArrayDeque;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.Deque;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.PriorityQueue;
/*   0*/
/*   0*/final class ControlFlowAnalysis implements NodeTraversal.Callback, CompilerPass {
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private ControlFlowGraph<Node> cfg;
/*   0*/  
/*   0*/  private Map<Node, Integer> astPosition;
/*   0*/  
/*   0*/  private Map<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>, Integer> nodePriorities;
/*   0*/  
/*  70*/  private final Comparator<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>> priorityComparator = new Comparator<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>>() {
/*   0*/      public int compare(DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> a, DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> b) {
/*  75*/        return (Integer)ControlFlowAnalysis.this.astPosition.get(a.getValue()) - (Integer)ControlFlowAnalysis.this.astPosition.get(b.getValue());
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  private int astPositionCounter;
/*   0*/  
/*   0*/  private int priorityCounter;
/*   0*/  
/*   0*/  private final boolean shouldTraverseFunctions;
/*   0*/  
/*   0*/  private final boolean edgeAnnotations;
/*   0*/  
/*   0*/  private Node root;
/*   0*/  
/*  95*/  private final Deque<Node> exceptionHandler = new ArrayDeque<Node>();
/*   0*/  
/* 126*/  private final Multimap<Node, Node> finallyMap = (Multimap<Node, Node>)HashMultimap.create();
/*   0*/  
/*   0*/  ControlFlowAnalysis(AbstractCompiler compiler, boolean shouldTraverseFunctions, boolean edgeAnnotations) {
/* 139*/    this.compiler = compiler;
/* 140*/    this.shouldTraverseFunctions = shouldTraverseFunctions;
/* 141*/    this.edgeAnnotations = edgeAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  ControlFlowGraph<Node> getCfg() {
/* 145*/    return this.cfg;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 150*/    this.root = root;
/* 151*/    this.astPositionCounter = 0;
/* 152*/    this.astPosition = Maps.newHashMap();
/* 153*/    this.nodePriorities = Maps.newHashMap();
/* 154*/    this.cfg = new AstControlFlowGraph(computeFallThrough(root), this.nodePriorities, this.edgeAnnotations);
/* 156*/    NodeTraversal.traverse(this.compiler, root, this);
/* 157*/    this.astPosition.put(null, ++this.astPositionCounter);
/* 161*/    this.priorityCounter = 0;
/* 162*/    DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> entry = this.cfg.getEntry();
/* 163*/    prioritizeFromEntryNode(entry);
/* 165*/    if (this.shouldTraverseFunctions)
/* 168*/      for (DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> candidate : this.cfg.getDirectedGraphNodes()) {
/* 169*/        Node value = candidate.getValue();
/* 170*/        if (value != null && value.isFunction()) {
/* 171*/          Preconditions.checkState((!this.nodePriorities.containsKey(candidate) || candidate == entry));
/* 173*/          prioritizeFromEntryNode(candidate);
/*   0*/        } 
/*   0*/      }  
/* 182*/    for (DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> candidate : this.cfg.getDirectedGraphNodes()) {
/* 183*/      if (!this.nodePriorities.containsKey(candidate))
/* 184*/        this.nodePriorities.put(candidate, ++this.priorityCounter); 
/*   0*/    } 
/* 189*/    this.nodePriorities.put(this.cfg.getImplicitReturn(), ++this.priorityCounter);
/*   0*/  }
/*   0*/  
/*   0*/  private void prioritizeFromEntryNode(DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> entry) {
/* 197*/    PriorityQueue<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>> worklist = new PriorityQueue<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>>(10, this.priorityComparator);
/* 199*/    worklist.add(entry);
/* 201*/    while (!worklist.isEmpty()) {
/* 202*/      DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> current = worklist.remove();
/* 203*/      if (this.nodePriorities.containsKey(current))
/*   0*/        continue; 
/* 207*/      this.nodePriorities.put(current, ++this.priorityCounter);
/* 209*/      List<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>> successors = this.cfg.getDirectedSuccNodes(current);
/* 211*/      for (DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> candidate : successors)
/* 212*/        worklist.add(candidate); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
/* 220*/    this.astPosition.put(n, this.astPositionCounter++);
/* 222*/    switch (n.getType()) {
/*   0*/      case 105:
/* 224*/        if (this.shouldTraverseFunctions || n == this.cfg.getEntry().getValue()) {
/* 225*/          this.exceptionHandler.push(n);
/* 226*/          return true;
/*   0*/        } 
/* 228*/        return false;
/*   0*/      case 77:
/* 230*/        this.exceptionHandler.push(n);
/* 231*/        return true;
/*   0*/    } 
/* 250*/    if (parent != null)
/* 251*/      switch (parent.getType()) {
/*   0*/        case 115:
/* 254*/          return (n == parent.getLastChild());
/*   0*/        case 108:
/*   0*/        case 113:
/*   0*/        case 119:
/* 260*/          return (n != parent.getFirstChild());
/*   0*/        case 114:
/* 262*/          return (n != parent.getFirstChild().getNext());
/*   0*/        case 110:
/*   0*/        case 111:
/*   0*/        case 120:
/*   0*/        case 126:
/* 268*/          return (n != parent.getFirstChild());
/*   0*/        case 105:
/* 270*/          return (n == parent.getFirstChild().getNext().getNext());
/*   0*/        case 4:
/*   0*/        case 49:
/*   0*/        case 116:
/*   0*/        case 117:
/*   0*/        case 118:
/*   0*/        case 130:
/* 277*/          return false;
/*   0*/        case 77:
/* 285*/          if (n == parent.getFirstChild().getNext()) {
/* 286*/            Preconditions.checkState((this.exceptionHandler.peek() == parent));
/* 287*/            this.exceptionHandler.pop();
/*   0*/          } 
/*   0*/          break;
/*   0*/      }  
/* 291*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/* 296*/    switch (n.getType()) {
/*   0*/      case 108:
/* 298*/        handleIf(n);
/*   0*/        return;
/*   0*/      case 113:
/* 301*/        handleWhile(n);
/*   0*/        return;
/*   0*/      case 114:
/* 304*/        handleDo(n);
/*   0*/        return;
/*   0*/      case 115:
/* 307*/        handleFor(n);
/*   0*/        return;
/*   0*/      case 110:
/* 310*/        handleSwitch(n);
/*   0*/        return;
/*   0*/      case 111:
/* 313*/        handleCase(n);
/*   0*/        return;
/*   0*/      case 112:
/* 316*/        handleDefault(n);
/*   0*/        return;
/*   0*/      case 125:
/*   0*/      case 132:
/* 320*/        handleStmtList(n);
/*   0*/        return;
/*   0*/      case 105:
/* 323*/        handleFunction(n);
/*   0*/        return;
/*   0*/      case 130:
/* 326*/        handleExpr(n);
/*   0*/        return;
/*   0*/      case 49:
/* 329*/        handleThrow(n);
/*   0*/        return;
/*   0*/      case 77:
/* 332*/        handleTry(n);
/*   0*/        return;
/*   0*/      case 120:
/* 335*/        handleCatch(n);
/*   0*/        return;
/*   0*/      case 116:
/* 338*/        handleBreak(n);
/*   0*/        return;
/*   0*/      case 117:
/* 341*/        handleContinue(n);
/*   0*/        return;
/*   0*/      case 4:
/* 344*/        handleReturn(n);
/*   0*/        return;
/*   0*/      case 119:
/* 347*/        handleWith(n);
/*   0*/        return;
/*   0*/      case 126:
/*   0*/        return;
/*   0*/    } 
/* 352*/    handleStmt(n);
/*   0*/  }
/*   0*/  
/*   0*/  private void handleIf(Node node) {
/* 358*/    Node thenBlock = node.getFirstChild().getNext();
/* 359*/    Node elseBlock = thenBlock.getNext();
/* 360*/    createEdge(node, ControlFlowGraph.Branch.ON_TRUE, computeFallThrough(thenBlock));
/* 362*/    if (elseBlock == null) {
/* 363*/      createEdge(node, ControlFlowGraph.Branch.ON_FALSE, computeFollowNode(node, this));
/*   0*/    } else {
/* 366*/      createEdge(node, ControlFlowGraph.Branch.ON_FALSE, computeFallThrough(elseBlock));
/*   0*/    } 
/* 368*/    connectToPossibleExceptionHandler(node, NodeUtil.getConditionExpression(node));
/*   0*/  }
/*   0*/  
/*   0*/  private void handleWhile(Node node) {
/* 374*/    createEdge(node, ControlFlowGraph.Branch.ON_TRUE, computeFallThrough(node.getFirstChild().getNext()));
/* 378*/    createEdge(node, ControlFlowGraph.Branch.ON_FALSE, computeFollowNode(node, this));
/* 380*/    connectToPossibleExceptionHandler(node, NodeUtil.getConditionExpression(node));
/*   0*/  }
/*   0*/  
/*   0*/  private void handleDo(Node node) {
/* 387*/    createEdge(node, ControlFlowGraph.Branch.ON_TRUE, computeFallThrough(node.getFirstChild()));
/* 389*/    createEdge(node, ControlFlowGraph.Branch.ON_FALSE, computeFollowNode(node, this));
/* 391*/    connectToPossibleExceptionHandler(node, NodeUtil.getConditionExpression(node));
/*   0*/  }
/*   0*/  
/*   0*/  private void handleFor(Node forNode) {
/* 396*/    if (forNode.getChildCount() == 4) {
/* 398*/      Node init = forNode.getFirstChild();
/* 399*/      Node cond = init.getNext();
/* 400*/      Node iter = cond.getNext();
/* 401*/      Node body = iter.getNext();
/* 404*/      createEdge(init, ControlFlowGraph.Branch.UNCOND, forNode);
/* 406*/      createEdge(forNode, ControlFlowGraph.Branch.ON_TRUE, computeFallThrough(body));
/* 408*/      createEdge(forNode, ControlFlowGraph.Branch.ON_FALSE, computeFollowNode(forNode, this));
/* 414*/      createEdge(iter, ControlFlowGraph.Branch.UNCOND, forNode);
/* 415*/      connectToPossibleExceptionHandler(init, init);
/* 416*/      connectToPossibleExceptionHandler(forNode, cond);
/* 417*/      connectToPossibleExceptionHandler(iter, iter);
/*   0*/    } else {
/* 420*/      Node item = forNode.getFirstChild();
/* 421*/      Node collection = item.getNext();
/* 422*/      Node body = collection.getNext();
/* 424*/      createEdge(collection, ControlFlowGraph.Branch.UNCOND, forNode);
/* 426*/      createEdge(forNode, ControlFlowGraph.Branch.ON_TRUE, computeFallThrough(body));
/* 428*/      createEdge(forNode, ControlFlowGraph.Branch.ON_FALSE, computeFollowNode(forNode, this));
/* 430*/      connectToPossibleExceptionHandler(forNode, collection);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleSwitch(Node node) {
/* 437*/    Node next = getNextSiblingOfType(node.getFirstChild().getNext(), new int[] { 111, 124 });
/* 439*/    if (next != null) {
/* 440*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, next);
/* 442*/    } else if (node.getFirstChild().getNext() != null) {
/* 443*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, node.getFirstChild().getNext());
/*   0*/    } else {
/* 445*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, computeFollowNode(node, this));
/*   0*/    } 
/* 448*/    connectToPossibleExceptionHandler(node, node.getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  private void handleCase(Node node) {
/* 453*/    createEdge(node, ControlFlowGraph.Branch.ON_TRUE, node.getFirstChild().getNext());
/* 456*/    Node next = getNextSiblingOfType(node.getNext(), new int[] { 111 });
/* 457*/    if (next != null) {
/* 458*/      Preconditions.checkState(next.isCase());
/* 459*/      createEdge(node, ControlFlowGraph.Branch.ON_FALSE, next);
/*   0*/    } else {
/* 461*/      Node parent = node.getParent();
/* 462*/      Node deflt = getNextSiblingOfType(parent.getFirstChild().getNext(), new int[] { 112 });
/* 464*/      if (deflt != null) {
/* 465*/        createEdge(node, ControlFlowGraph.Branch.ON_FALSE, deflt);
/*   0*/      } else {
/* 467*/        createEdge(node, ControlFlowGraph.Branch.ON_FALSE, computeFollowNode(node, this));
/*   0*/      } 
/*   0*/    } 
/* 470*/    connectToPossibleExceptionHandler(node, node.getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  private void handleDefault(Node node) {
/* 475*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, node.getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  private void handleWith(Node node) {
/* 480*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, node.getLastChild());
/* 481*/    connectToPossibleExceptionHandler(node, node.getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  private void handleStmtList(Node node) {
/* 485*/    Node parent = node.getParent();
/* 487*/    if (node.isBlock() && parent != null && parent.isTry() && NodeUtil.getCatchBlock(parent) == node && !NodeUtil.hasCatchHandler(node))
/*   0*/      return; 
/* 495*/    Node child = node.getFirstChild();
/* 499*/    while (child != null && child.isFunction())
/* 500*/      child = child.getNext(); 
/* 503*/    if (child != null) {
/* 504*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, computeFallThrough(child));
/*   0*/    } else {
/* 506*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, computeFollowNode(node, this));
/*   0*/    } 
/* 510*/    if (parent != null)
/* 511*/      switch (parent.getType()) {
/*   0*/        case 77:
/*   0*/        case 111:
/*   0*/        case 112:
/*   0*/          break;
/*   0*/        default:
/* 517*/          if (node.isBlock() && node.isSyntheticBlock())
/* 518*/            createEdge(node, ControlFlowGraph.Branch.SYN_BLOCK, computeFollowNode(node, this)); 
/*   0*/          break;
/*   0*/      }  
/*   0*/  }
/*   0*/  
/*   0*/  private void handleFunction(Node node) {
/* 527*/    Preconditions.checkState((node.getChildCount() >= 3));
/* 528*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, computeFallThrough(node.getFirstChild().getNext().getNext()));
/* 530*/    Preconditions.checkState((this.exceptionHandler.peek() == node));
/* 531*/    this.exceptionHandler.pop();
/*   0*/  }
/*   0*/  
/*   0*/  private void handleExpr(Node node) {
/* 535*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, computeFollowNode(node, this));
/* 536*/    connectToPossibleExceptionHandler(node, node);
/*   0*/  }
/*   0*/  
/*   0*/  private void handleThrow(Node node) {
/* 540*/    connectToPossibleExceptionHandler(node, node);
/*   0*/  }
/*   0*/  
/*   0*/  private void handleTry(Node node) {
/* 544*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, node.getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  private void handleCatch(Node node) {
/* 548*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, node.getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  private void handleBreak(Node node) {
/* 552*/    String label = null;
/* 554*/    if (node.hasChildren())
/* 555*/      label = node.getFirstChild().getString(); 
/* 558*/    Node previous = null;
/* 560*/    Node parent = node.getParent();
/* 570*/    Node cur = node, lastJump = node;
/* 571*/    for (; !isBreakTarget(cur, label); 
/* 572*/      cur = parent, parent = parent.getParent()) {
/* 573*/      if (cur.isTry() && NodeUtil.hasFinally(cur) && cur.getLastChild() != previous) {
/* 575*/        if (lastJump == node) {
/* 576*/          createEdge(lastJump, ControlFlowGraph.Branch.UNCOND, computeFallThrough(cur.getLastChild()));
/*   0*/        } else {
/* 579*/          this.finallyMap.put(lastJump, computeFallThrough(cur.getLastChild()));
/*   0*/        } 
/* 581*/        lastJump = cur;
/*   0*/      } 
/* 583*/      if (parent == null) {
/* 584*/        if (this.compiler.isIdeMode())
/*   0*/          return; 
/* 589*/        throw new IllegalStateException("Cannot find break target.");
/*   0*/      } 
/* 592*/      previous = cur;
/*   0*/    } 
/* 594*/    if (lastJump == node) {
/* 595*/      createEdge(lastJump, ControlFlowGraph.Branch.UNCOND, computeFollowNode(cur, this));
/*   0*/    } else {
/* 597*/      this.finallyMap.put(lastJump, computeFollowNode(cur, this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleContinue(Node node) {
/* 602*/    String label = null;
/* 603*/    if (node.hasChildren())
/* 604*/      label = node.getFirstChild().getString(); 
/* 607*/    Node previous = null;
/* 611*/    Node parent = node.getParent();
/* 612*/    Node cur = node, lastJump = node;
/* 613*/    for (; !isContinueTarget(cur, parent, label); 
/* 614*/      cur = parent, parent = parent.getParent()) {
/* 615*/      if (cur.isTry() && NodeUtil.hasFinally(cur) && cur.getLastChild() != previous) {
/* 617*/        if (lastJump == node) {
/* 618*/          createEdge(lastJump, ControlFlowGraph.Branch.UNCOND, cur.getLastChild());
/*   0*/        } else {
/* 620*/          this.finallyMap.put(lastJump, computeFallThrough(cur.getLastChild()));
/*   0*/        } 
/* 622*/        lastJump = cur;
/*   0*/      } 
/* 624*/      Preconditions.checkState((parent != null), "Cannot find continue target.");
/* 625*/      previous = cur;
/*   0*/    } 
/* 627*/    Node iter = cur;
/* 628*/    if (cur.getChildCount() == 4)
/* 629*/      iter = cur.getFirstChild().getNext().getNext(); 
/* 632*/    if (lastJump == node) {
/* 633*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, iter);
/*   0*/    } else {
/* 635*/      this.finallyMap.put(lastJump, iter);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleReturn(Node node) {
/* 640*/    Node lastJump = null;
/* 641*/    for (Node curHandler : this.exceptionHandler) {
/* 643*/      if (curHandler.isFunction())
/*   0*/        break; 
/* 646*/      if (NodeUtil.hasFinally(curHandler)) {
/* 647*/        if (lastJump == null) {
/* 648*/          createEdge(node, ControlFlowGraph.Branch.UNCOND, curHandler.getLastChild());
/*   0*/        } else {
/* 650*/          this.finallyMap.put(lastJump, computeFallThrough(curHandler.getLastChild()));
/*   0*/        } 
/* 653*/        lastJump = curHandler;
/*   0*/      } 
/*   0*/    } 
/* 657*/    if (node.hasChildren())
/* 658*/      connectToPossibleExceptionHandler(node, node.getFirstChild()); 
/* 661*/    if (lastJump == null) {
/* 662*/      createEdge(node, ControlFlowGraph.Branch.UNCOND, null);
/*   0*/    } else {
/* 664*/      this.finallyMap.put(lastJump, null);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void handleStmt(Node node) {
/* 670*/    createEdge(node, ControlFlowGraph.Branch.UNCOND, computeFollowNode(node, this));
/* 671*/    connectToPossibleExceptionHandler(node, node);
/*   0*/  }
/*   0*/  
/*   0*/  static Node computeFollowNode(Node node, ControlFlowAnalysis cfa) {
/* 675*/    return computeFollowNode(node, node, cfa);
/*   0*/  }
/*   0*/  
/*   0*/  static Node computeFollowNode(Node node) {
/* 679*/    return computeFollowNode(node, node, null);
/*   0*/  }
/*   0*/  
/*   0*/  private static Node computeFollowNode(Node fromNode, Node node, ControlFlowAnalysis cfa) {
/* 712*/    Node parent = node.getParent();
/* 713*/    if (parent == null || parent.isFunction() || (cfa != null && node == cfa.root))
/* 715*/      return null; 
/* 719*/    switch (parent.getType()) {
/*   0*/      case 108:
/* 722*/        return computeFollowNode(fromNode, parent, cfa);
/*   0*/      case 111:
/*   0*/      case 112:
/* 727*/        if (parent.getNext() != null) {
/* 728*/          if (parent.getNext().isCase())
/* 729*/            return parent.getNext().getFirstChild().getNext(); 
/* 730*/          if (parent.getNext().isDefaultCase())
/* 731*/            return parent.getNext().getFirstChild(); 
/* 733*/          Preconditions.checkState(false, "Not reachable");
/*   0*/          break;
/*   0*/        } 
/* 736*/        return computeFollowNode(fromNode, parent, cfa);
/*   0*/      case 115:
/* 740*/        if (NodeUtil.isForIn(parent))
/* 741*/          return parent; 
/* 743*/        return parent.getFirstChild().getNext().getNext();
/*   0*/      case 113:
/*   0*/      case 114:
/* 747*/        return parent;
/*   0*/      case 77:
/* 750*/        if (parent.getFirstChild() == node) {
/* 751*/          if (NodeUtil.hasFinally(parent))
/* 752*/            return computeFallThrough(parent.getLastChild()); 
/* 754*/          return computeFollowNode(fromNode, parent, cfa);
/*   0*/        } 
/* 757*/        if (NodeUtil.getCatchBlock(parent) == node) {
/* 758*/          if (NodeUtil.hasFinally(parent))
/* 759*/            return computeFallThrough(node.getNext()); 
/* 761*/          return computeFollowNode(fromNode, parent, cfa);
/*   0*/        } 
/* 764*/        if (parent.getLastChild() == node) {
/* 765*/          if (cfa != null)
/* 766*/            for (Node finallyNode : (Iterable<Node>)cfa.finallyMap.get(parent))
/* 767*/              cfa.createEdge(fromNode, ControlFlowGraph.Branch.ON_EX, finallyNode);  
/* 770*/          return computeFollowNode(fromNode, parent, cfa);
/*   0*/        } 
/*   0*/        break;
/*   0*/    } 
/* 776*/    Node nextSibling = node.getNext();
/* 779*/    while (nextSibling != null && nextSibling.isFunction())
/* 780*/      nextSibling = nextSibling.getNext(); 
/* 783*/    if (nextSibling != null)
/* 784*/      return computeFallThrough(nextSibling); 
/* 787*/    return computeFollowNode(fromNode, parent, cfa);
/*   0*/  }
/*   0*/  
/*   0*/  static Node computeFallThrough(Node n) {
/* 797*/    switch (n.getType()) {
/*   0*/      case 114:
/* 799*/        return computeFallThrough(n.getFirstChild());
/*   0*/      case 115:
/* 801*/        if (NodeUtil.isForIn(n))
/* 802*/          return n.getFirstChild().getNext(); 
/* 804*/        return computeFallThrough(n.getFirstChild());
/*   0*/      case 126:
/* 806*/        return computeFallThrough(n.getLastChild());
/*   0*/    } 
/* 808*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  private void createEdge(Node fromNode, ControlFlowGraph.Branch branch, Node toNode) {
/* 820*/    this.cfg.createNode(fromNode);
/* 821*/    this.cfg.createNode(toNode);
/* 822*/    this.cfg.connectIfNotFound(fromNode, branch, toNode);
/*   0*/  }
/*   0*/  
/*   0*/  private void connectToPossibleExceptionHandler(Node cfgNode, Node target) {
/* 831*/    if (mayThrowException(target) && !this.exceptionHandler.isEmpty()) {
/* 832*/      Node lastJump = cfgNode;
/* 833*/      for (Node handler : this.exceptionHandler) {
/* 834*/        if (handler.isFunction())
/*   0*/          return; 
/* 837*/        Preconditions.checkState(handler.isTry());
/* 838*/        Node catchBlock = NodeUtil.getCatchBlock(handler);
/* 840*/        if (!NodeUtil.hasCatchHandler(catchBlock)) {
/* 841*/          if (lastJump == cfgNode) {
/* 842*/            createEdge(cfgNode, ControlFlowGraph.Branch.ON_EX, handler.getLastChild());
/*   0*/          } else {
/* 844*/            this.finallyMap.put(lastJump, handler.getLastChild());
/*   0*/          } 
/*   0*/        } else {
/* 847*/          if (lastJump == cfgNode) {
/* 848*/            createEdge(cfgNode, ControlFlowGraph.Branch.ON_EX, catchBlock);
/*   0*/            return;
/*   0*/          } 
/* 851*/          this.finallyMap.put(lastJump, catchBlock);
/*   0*/        } 
/* 854*/        lastJump = handler;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getNextSiblingOfType(Node first, int... types) {
/* 863*/    for (Node c = first; c != null; c = c.getNext()) {
/* 864*/      for (int type : types) {
/* 865*/        if (c.getType() == type)
/* 866*/          return c; 
/*   0*/      } 
/*   0*/    } 
/* 870*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBreakTarget(Node target, String label) {
/* 878*/    return (isBreakStructure(target, (label != null)) && matchLabel(target.getParent(), label));
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isContinueTarget(Node target, Node parent, String label) {
/* 888*/    return (isContinueStructure(target) && matchLabel(parent, label));
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean matchLabel(Node target, String label) {
/* 895*/    if (label == null)
/* 896*/      return true; 
/* 898*/    while (target.isLabel()) {
/* 899*/      if (target.getFirstChild().getString().equals(label))
/* 900*/        return true; 
/* 902*/      target = target.getParent();
/*   0*/    } 
/* 904*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean mayThrowException(Node n) {
/* 911*/    switch (n.getType()) {
/*   0*/      case 30:
/*   0*/      case 33:
/*   0*/      case 35:
/*   0*/      case 37:
/*   0*/      case 49:
/*   0*/      case 52:
/*   0*/      case 86:
/*   0*/      case 102:
/*   0*/      case 103:
/* 921*/        return true;
/*   0*/      case 105:
/* 923*/        return false;
/*   0*/    } 
/* 925*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 926*/      if (!ControlFlowGraph.isEnteringNewCfgNode(c) && mayThrowException(c))
/* 927*/        return true; 
/*   0*/    } 
/* 930*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isBreakStructure(Node n, boolean labeled) {
/* 937*/    switch (n.getType()) {
/*   0*/      case 110:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/* 942*/        return true;
/*   0*/      case 77:
/*   0*/      case 108:
/*   0*/      case 125:
/* 946*/        return labeled;
/*   0*/    } 
/* 948*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isContinueStructure(Node n) {
/* 956*/    switch (n.getType()) {
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/* 960*/        return true;
/*   0*/    } 
/* 962*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getExceptionHandler(Node n) {
/* 972*/    Node cur = n;
/* 973*/    for (; !cur.isScript() && !cur.isFunction(); 
/* 974*/      cur = cur.getParent()) {
/* 975*/      Node catchNode = getCatchHandlerForBlock(cur);
/* 976*/      if (catchNode != null)
/* 977*/        return catchNode; 
/*   0*/    } 
/* 980*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getCatchHandlerForBlock(Node block) {
/* 988*/    if (block.isBlock() && block.getParent().isTry() && block.getParent().getFirstChild() == block)
/* 991*/      for (Node s = block.getNext(); s != null; s = s.getNext()) {
/* 992*/        if (NodeUtil.hasCatchHandler(s))
/* 993*/          return s.getFirstChild(); 
/*   0*/      }  
/* 997*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static class AstControlFlowGraph extends ControlFlowGraph<Node> {
/*   0*/    private final Map<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>, Integer> priorities;
/*   0*/    
/*   0*/    private AstControlFlowGraph(Node entry, Map<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>, Integer> priorities, boolean edgeAnnotations) {
/*1016*/      super(entry, true, edgeAnnotations);
/*1018*/      this.priorities = priorities;
/*   0*/    }
/*   0*/    
/*   0*/    public Comparator<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>> getOptionalNodeComparator(boolean isForward) {
/*1029*/      if (isForward)
/*1030*/        return new Comparator<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>>() {
/*   0*/            public int compare(DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> n1, DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> n2) {
/*1034*/              return ControlFlowAnalysis.AstControlFlowGraph.this.getPosition(n1) - ControlFlowAnalysis.AstControlFlowGraph.this.getPosition(n2);
/*   0*/            }
/*   0*/          }; 
/*1038*/      return new Comparator<DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch>>() {
/*   0*/          public int compare(DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> n1, DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> n2) {
/*1042*/            return ControlFlowAnalysis.AstControlFlowGraph.this.getPosition(n2) - ControlFlowAnalysis.AstControlFlowGraph.this.getPosition(n1);
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/    
/*   0*/    private int getPosition(DiGraph.DiGraphNode<Node, ControlFlowGraph.Branch> n) {
/*1053*/      Integer priority = this.priorities.get(n);
/*1054*/      Preconditions.checkNotNull(priority);
/*1055*/      return priority;
/*   0*/    }
/*   0*/  }
/*   0*/}
