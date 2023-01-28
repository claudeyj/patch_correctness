/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.JSDocInfoBuilder;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.Token;
/*   0*/import java.util.List;
/*   0*/
/*   0*/final class CheckSideEffects extends NodeTraversal.AbstractPostOrderCallback implements HotSwapCompilerPass {
/*  43*/  static final DiagnosticType USELESS_CODE_ERROR = DiagnosticType.warning("JSC_USELESS_CODE", "Suspicious code. {0}");
/*   0*/  
/*   0*/  static final String PROTECTOR_FN = "JSCOMPILER_PRESERVE";
/*   0*/  
/*   0*/  private final CheckLevel level;
/*   0*/  
/*  51*/  private final List<Node> problemNodes = Lists.newArrayList();
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final boolean protectSideEffectFreeCode;
/*   0*/  
/*   0*/  CheckSideEffects(AbstractCompiler compiler, CheckLevel level, boolean protectSideEffectFreeCode) {
/*  59*/    this.compiler = compiler;
/*  60*/    this.level = level;
/*  61*/    this.protectSideEffectFreeCode = protectSideEffectFreeCode;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/*  66*/    NodeTraversal.traverse(this.compiler, root, this);
/*  73*/    if (this.protectSideEffectFreeCode)
/*  74*/      protectSideEffects(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void hotSwapScript(Node scriptRoot, Node originalRoot) {
/*  80*/    NodeTraversal.traverse(this.compiler, scriptRoot, this);
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*  89*/    if (n.isEmpty() || n.isComma())
/*   0*/      return; 
/*  94*/    if (parent == null)
/*   0*/      return; 
/* 101*/    if (n.isExprResult())
/*   0*/      return; 
/* 107*/    if (n.isQualifiedName() && n.getJSDocInfo() != null)
/*   0*/      return; 
/* 111*/    boolean isResultUsed = NodeUtil.isExpressionResultUsed(n);
/* 112*/    boolean isSimpleOp = NodeUtil.isSimpleOperatorType(n.getType());
/* 113*/    if (parent.getType() == 85) {
/* 114*/      if (isResultUsed)
/*   0*/        return; 
/* 117*/      if (n == null)
/* 118*/        for (Node an : (Iterable<Node>)parent.getAncestors()) {
/* 119*/          int ancestorType = an.getType();
/* 120*/          if (ancestorType == 85)
/*   0*/            continue; 
/* 121*/          if (ancestorType != 130 && ancestorType != 125)
/*   0*/            return; 
/*   0*/        }  
/* 125*/    } else if (parent.getType() != 130 && parent.getType() != 125 && (
/* 126*/      parent.getType() != 115 || parent.getChildCount() != 4 || (n != parent.getFirstChild() && n != parent.getFirstChild().getNext().getNext()))) {
/*   0*/      return;
/*   0*/    } 
/* 130*/    if (isSimpleOp || !NodeUtil.mayHaveSideEffects(n, t.getCompiler())) {
/* 132*/      String msg = "This code lacks side-effects. Is there a bug?";
/* 133*/      if (n.isString()) {
/* 134*/        msg = "Is there a missing '+' on the previous line?";
/* 135*/      } else if (isSimpleOp) {
/* 136*/        msg = "The result of the '" + Token.name(n.getType()).toLowerCase() + "' operator is not being used.";
/*   0*/      } 
/* 140*/      t.getCompiler().report(t.makeError(n, this.level, USELESS_CODE_ERROR, new String[] { msg }));
/* 144*/      if (!NodeUtil.isStatement(n))
/* 145*/        this.problemNodes.add(n); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void protectSideEffects() {
/* 156*/    if (!this.problemNodes.isEmpty()) {
/* 157*/      addExtern();
/* 158*/      for (Node n : this.problemNodes) {
/* 159*/        Node name = IR.name("JSCOMPILER_PRESERVE").srcref(n);
/* 160*/        name.putBooleanProp(43, true);
/* 161*/        Node replacement = IR.call(name, new Node[0]).srcref(n);
/* 162*/        replacement.putBooleanProp(50, true);
/* 163*/        n.getParent().replaceChild(n, replacement);
/* 164*/        replacement.addChildToBack(n);
/*   0*/      } 
/* 166*/      this.compiler.reportCodeChange();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void addExtern() {
/* 171*/    Node name = IR.name("JSCOMPILER_PRESERVE");
/* 172*/    name.putBooleanProp(43, true);
/* 173*/    Node var = IR.var(name);
/* 175*/    JSDocInfoBuilder builder = new JSDocInfoBuilder(false);
/* 176*/    builder.recordNoAlias();
/* 177*/    var.setJSDocInfo(builder.build(var));
/* 178*/    CompilerInput input = this.compiler.getSynthesizedExternsInput();
/* 179*/    input.getAstRoot(this.compiler).addChildrenToBack(var);
/* 180*/    this.compiler.reportCodeChange();
/*   0*/  }
/*   0*/  
/*   0*/  static class StripProtection extends NodeTraversal.AbstractPostOrderCallback implements CompilerPass {
/*   0*/    private final AbstractCompiler compiler;
/*   0*/    
/*   0*/    StripProtection(AbstractCompiler compiler) {
/* 191*/      this.compiler = compiler;
/*   0*/    }
/*   0*/    
/*   0*/    public void process(Node externs, Node root) {
/* 196*/      NodeTraversal.traverse(this.compiler, root, this);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 201*/      if (n.isCall()) {
/* 202*/        Node target = n.getFirstChild();
/* 205*/        if (target.isName() && target.getString().equals("JSCOMPILER_PRESERVE")) {
/* 206*/          Node expr = n.getLastChild();
/* 207*/          n.detachChildren();
/* 208*/          parent.replaceChild(n, expr);
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/}
