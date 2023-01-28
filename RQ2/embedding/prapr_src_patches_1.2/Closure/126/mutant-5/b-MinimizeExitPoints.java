/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.TernaryValue;
/*   0*/
/*   0*/class MinimizeExitPoints extends NodeTraversal.AbstractPostOrderCallback implements CompilerPass {
/*   0*/  AbstractCompiler compiler;
/*   0*/  
/*   0*/  MinimizeExitPoints(AbstractCompiler compiler) {
/*  39*/    this.compiler = compiler;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/*  44*/    NodeTraversal.traverse(this.compiler, root, this);
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/    Node cond;
/*  49*/    switch (n.getType()) {
/*   0*/      case 126:
/*  51*/        tryMinimizeExits(n.getLastChild(), 116, n.getFirstChild().getString());
/*   0*/        break;
/*   0*/      case 113:
/*   0*/      case 115:
/*  57*/        tryMinimizeExits(NodeUtil.getLoopCodeBlock(n), 117, null);
/*   0*/        break;
/*   0*/      case 114:
/*  61*/        tryMinimizeExits(NodeUtil.getLoopCodeBlock(n), 117, null);
/*  63*/        cond = NodeUtil.getConditionExpression(n);
/*  64*/        if (NodeUtil.getImpureBooleanValue(cond) == TernaryValue.FALSE)
/*  68*/          tryMinimizeExits(n.getFirstChild(), 116, null); 
/*   0*/        break;
/*   0*/      case 105:
/*  73*/        tryMinimizeExits(n.getLastChild(), 4, null);
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void tryMinimizeExits(Node n, int exitType, String labelName) {
/* 109*/    if (matchingExitNode(n, exitType, labelName)) {
/* 110*/      NodeUtil.removeChild(n.getParent(), n);
/* 111*/      this.compiler.reportCodeChange();
/*   0*/      return;
/*   0*/    } 
/* 116*/    if (n.isIf()) {
/* 117*/      Node ifBlock = n.getFirstChild().getNext();
/* 118*/      tryMinimizeExits(ifBlock, exitType, labelName);
/* 119*/      Node elseBlock = ifBlock.getNext();
/* 120*/      if (elseBlock != null)
/* 121*/        tryMinimizeExits(elseBlock, exitType, labelName); 
/*   0*/      return;
/*   0*/    } 
/* 127*/    if (n.isTry()) {
/* 128*/      Node tryBlock = n.getFirstChild();
/* 129*/      tryMinimizeExits(tryBlock, exitType, labelName);
/* 130*/      Node allCatchNodes = NodeUtil.getCatchBlock(n);
/* 131*/      if (NodeUtil.hasCatchHandler(allCatchNodes)) {
/* 132*/        Preconditions.checkState(allCatchNodes.hasOneChild());
/* 133*/        Node catchNode = allCatchNodes.getFirstChild();
/* 134*/        Node catchCodeBlock = catchNode.getLastChild();
/* 135*/        tryMinimizeExits(catchCodeBlock, exitType, labelName);
/*   0*/      } 
/* 141*/      if (NodeUtil.hasFinally(n)) {
/* 142*/        Node finallyBlock = n.getLastChild();
/* 143*/        tryMinimizeExits(allCatchNodes, exitType, labelName);
/*   0*/      } 
/*   0*/    } 
/* 148*/    if (n.isLabel()) {
/* 149*/      Node labelBlock = n.getLastChild();
/* 150*/      tryMinimizeExits(labelBlock, exitType, labelName);
/*   0*/    } 
/* 156*/    if (!n.isBlock() || n.getLastChild() == null)
/*   0*/      return; 
/* 164*/    for (Node node : n.children()) {
/* 167*/      if (node.isIf()) {
/* 168*/        Node ifTree = node;
/* 172*/        Node trueBlock = ifTree.getFirstChild().getNext();
/* 173*/        Node falseBlock = trueBlock.getNext();
/* 174*/        tryMinimizeIfBlockExits(trueBlock, falseBlock, ifTree, exitType, labelName);
/* 179*/        trueBlock = ifTree.getFirstChild().getNext();
/* 180*/        falseBlock = trueBlock.getNext();
/* 181*/        if (falseBlock != null)
/* 182*/          tryMinimizeIfBlockExits(falseBlock, trueBlock, ifTree, exitType, labelName); 
/*   0*/      } 
/* 187*/      if (node == n.getLastChild())
/*   0*/        break; 
/*   0*/    } 
/* 194*/    for (Node c = n.getLastChild(); c != null; c = n.getLastChild()) {
/* 195*/      tryMinimizeExits(c, exitType, labelName);
/* 197*/      if (c == n.getLastChild())
/*   0*/        break; 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void tryMinimizeIfBlockExits(Node srcBlock, Node destBlock, Node ifNode, int exitType, String labelName) {
/* 218*/    Node exitNodeParent = null;
/* 219*/    Node exitNode = null;
/* 222*/    if (srcBlock.isBlock()) {
/* 223*/      if (!srcBlock.hasChildren())
/*   0*/        return; 
/* 226*/      exitNodeParent = srcBlock;
/* 227*/      exitNode = exitNodeParent.getLastChild();
/*   0*/    } else {
/* 230*/      exitNodeParent = ifNode;
/* 231*/      exitNode = srcBlock;
/*   0*/    } 
/* 235*/    if (!matchingExitNode(exitNode, exitType, labelName))
/*   0*/      return; 
/* 240*/    if (ifNode.getNext() != null) {
/* 243*/      Node newDestBlock = IR.block().srcref(ifNode);
/* 244*/      if (destBlock == null) {
/* 246*/        ifNode.addChildToBack(newDestBlock);
/* 247*/      } else if (destBlock.isEmpty()) {
/* 249*/        ifNode.replaceChild(destBlock, newDestBlock);
/* 250*/      } else if (destBlock.isBlock()) {
/* 252*/        newDestBlock = destBlock;
/*   0*/      } else {
/* 255*/        ifNode.replaceChild(destBlock, newDestBlock);
/* 256*/        newDestBlock.addChildToBack(destBlock);
/*   0*/      } 
/* 260*/      moveAllFollowing(ifNode, ifNode.getParent(), newDestBlock);
/* 261*/      this.compiler.reportCodeChange();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean matchingExitNode(Node n, int type, String labelName) {
/* 277*/    if (n.getType() == type) {
/* 278*/      if (type == 4)
/* 280*/        return !n.hasChildren(); 
/* 282*/      if (labelName == null)
/* 283*/        return !n.hasChildren(); 
/* 285*/      return (n.hasChildren() && labelName.equals(n.getFirstChild().getString()));
/*   0*/    } 
/* 290*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static void moveAllFollowing(Node start, Node srcParent, Node destParent) {
/* 302*/    for (Node n = start.getNext(); n != null; n = start.getNext()) {
/* 303*/      boolean isFunctionDeclaration = NodeUtil.isFunctionDeclaration(n);
/* 304*/      srcParent.removeChild(n);
/* 305*/      if (isFunctionDeclaration) {
/* 306*/        destParent.addChildToFront(n);
/*   0*/      } else {
/* 308*/        destParent.addChildToBack(n);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/}
