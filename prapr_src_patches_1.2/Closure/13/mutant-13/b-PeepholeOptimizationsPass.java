/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.ArrayList;
/*   0*/
/*   0*/class PeepholeOptimizationsPass implements CompilerPass {
/*   0*/  private AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final AbstractPeepholeOptimization[] peepholeOptimizations;
/*   0*/  
/*  41*/  private StateStack traversalState = new StateStack();
/*   0*/  
/*   0*/  private static class ScopeState {
/*   0*/    boolean changed;
/*   0*/    
/*   0*/    boolean traverseChildScopes;
/*   0*/    
/*   0*/    ScopeState() {
/*  47*/      reset();
/*   0*/    }
/*   0*/    
/*   0*/    void reset() {
/*  51*/      this.changed = false;
/*  52*/      this.traverseChildScopes = true;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StateStack {
/*  57*/    private ArrayList<PeepholeOptimizationsPass.ScopeState> states = Lists.newArrayList();
/*   0*/    
/*  58*/    private int currentDepth = 0;
/*   0*/    
/*   0*/    StateStack() {
/*  61*/      this.states.add(new PeepholeOptimizationsPass.ScopeState());
/*   0*/    }
/*   0*/    
/*   0*/    PeepholeOptimizationsPass.ScopeState peek() {
/*  65*/      return this.states.get(this.currentDepth);
/*   0*/    }
/*   0*/    
/*   0*/    void push() {
/*  69*/      this.currentDepth++;
/*  70*/      if (this.states.size() <= this.currentDepth) {
/*  71*/        this.states.add(new PeepholeOptimizationsPass.ScopeState());
/*   0*/      } else {
/*  73*/        ((PeepholeOptimizationsPass.ScopeState)this.states.get(this.currentDepth)).reset();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void pop() {
/*  78*/      this.currentDepth--;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class PeepholeChangeHandler implements CodeChangeHandler {
/*   0*/    private PeepholeChangeHandler() {}
/*   0*/    
/*   0*/    public void reportChange() {
/*  85*/      (PeepholeOptimizationsPass.this.traversalState.peek()).changed = true;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PeepholeOptimizationsPass(AbstractCompiler compiler, AbstractPeepholeOptimization... optimizations) {
/*  95*/    this.compiler = compiler;
/*  96*/    this.peepholeOptimizations = optimizations;
/*   0*/  }
/*   0*/  
/*   0*/  public AbstractCompiler getCompiler() {
/* 100*/    return this.compiler;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 105*/    PeepholeChangeHandler handler = new PeepholeChangeHandler();
/* 106*/    this.compiler.addChangeHandler(handler);
/* 107*/    beginTraversal();
/* 108*/    traverse(root);
/* 109*/    endTraversal();
/* 110*/    this.compiler.removeChangeHandler(handler);
/*   0*/  }
/*   0*/  
/*   0*/  private void traverse(Node node) {
/* 118*/    if (!shouldVisit(node))
/*   0*/      return; 
/* 122*/    int visits = 0;
/*   0*/    do {
/* 124*/      Node c = node.getFirstChild();
/* 125*/      while (c != null) {
/* 126*/        traverse(c);
/* 127*/        Node next = c.getNext();
/* 128*/        c = next;
/*   0*/      } 
/* 131*/      visit(node);
/* 132*/      visits++;
/* 134*/      Preconditions.checkState((visits < 10000), "too many interations");
/* 135*/    } while (shouldRetraverse(node));
/* 137*/    exitNode(node);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean shouldRetraverse(Node node) {
/* 141*/    if ((node.getParent() != null && node.isFunction()) || node.isScript()) {
/* 142*/      ScopeState state = this.traversalState.peek();
/* 143*/      if (state.changed) {
/* 148*/        state.changed = false;
/* 149*/        state.traverseChildScopes = true;
/* 150*/        return true;
/*   0*/      } 
/*   0*/    } 
/* 153*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean shouldVisit(Node node) {
/* 157*/    if (node.isFunction() || node.isScript()) {
/* 158*/      ScopeState previous = this.traversalState.peek();
/* 159*/      if (!previous.traverseChildScopes)
/* 160*/        return false; 
/* 162*/      this.traversalState.push();
/*   0*/    } 
/* 164*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void exitNode(Node node) {
/* 168*/    if (node.isFunction() || node.isScript())
/* 169*/      this.traversalState.pop(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(Node n) {
/* 174*/    Node currentVersionOfNode = n;
/*   0*/    boolean somethingChanged = false;
/*   0*/    do {
/* 178*/      somethingChanged = false;
/* 179*/      for (AbstractPeepholeOptimization optimization : this.peepholeOptimizations) {
/* 180*/        Node newVersionOfNode = optimization.optimizeSubtree(currentVersionOfNode);
/* 183*/        if (newVersionOfNode != currentVersionOfNode) {
/* 184*/          somethingChanged = true;
/* 186*/          currentVersionOfNode = newVersionOfNode;
/*   0*/        } 
/* 189*/        if (currentVersionOfNode == null)
/*   0*/          return; 
/*   0*/      } 
/* 193*/    } while (somethingChanged);
/*   0*/  }
/*   0*/  
/*   0*/  private void beginTraversal() {
/* 201*/    for (AbstractPeepholeOptimization optimization : this.peepholeOptimizations)
/* 202*/      optimization.beginTraversal(this.compiler); 
/*   0*/  }
/*   0*/  
/*   0*/  private void endTraversal() {
/* 207*/    for (AbstractPeepholeOptimization optimization : this.peepholeOptimizations)
/* 208*/      optimization.endTraversal(this.compiler); 
/*   0*/  }
/*   0*/}
