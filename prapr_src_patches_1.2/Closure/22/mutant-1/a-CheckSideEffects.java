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
/*  98*/    int pt = parent.getType();
/*  99*/    if (pt == 85) {
/* 100*/      Node gramps = parent.getParent();
/* 101*/      if (gramps.isCall() && parent == gramps.getFirstChild())
/* 107*/        if (n == parent.getFirstChild() && parent.getChildCount() == 2 && n.getNext().isName() && "eval".equals(n.getNext().getString()))
/*   0*/          return;  
/* 115*/      if (n == parent.getLastChild())
/* 116*/        for (Node an : (Iterable<Node>)parent.getAncestors()) {
/* 117*/          int ancestorType = an.getType();
/* 118*/          if (ancestorType == 85)
/*   0*/            continue; 
/* 120*/          if (ancestorType != 130 && ancestorType != 125)
/*   0*/            return; 
/*   0*/        }  
/* 127*/    } else if (pt != 130 && pt != 125 && (
/* 128*/      pt != 115 || parent.getChildCount() != 4 || (n != parent.getFirstChild() && n != parent.getFirstChild().getNext().getNext()))) {
/*   0*/      return;
/*   0*/    } 
/* 138*/    boolean isSimpleOp = NodeUtil.isSimpleOperatorType(n.getType());
/* 139*/    if (isSimpleOp || !NodeUtil.mayHaveSideEffects(n, t.getCompiler())) {
/* 141*/      if (n.isQualifiedName() && n.getJSDocInfo() != null)
/*   0*/        return; 
/* 145*/      if (n.isExprResult())
/*   0*/        return; 
/* 150*/      String msg = "This code lacks side-effects. Is there a bug?";
/* 151*/      if (n.isString()) {
/* 152*/        msg = "Is there a missing '+' on the previous line?";
/* 153*/      } else if (isSimpleOp) {
/* 154*/        msg = "The result of the '" + Token.name(n.getType()).toLowerCase() + "' operator is not being used.";
/*   0*/      } 
/* 158*/      t.getCompiler().report(t.makeError(n, this.level, USELESS_CODE_ERROR, new String[] { msg }));
/* 162*/      if (!NodeUtil.isStatement(n))
/* 163*/        this.problemNodes.add(n); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void protectSideEffects() {
/* 174*/    if (!this.problemNodes.isEmpty()) {
/* 175*/      addExtern();
/* 176*/      for (Node n : this.problemNodes) {
/* 177*/        Node name = IR.name("JSCOMPILER_PRESERVE").srcref(n);
/* 178*/        name.putBooleanProp(43, true);
/* 179*/        Node replacement = IR.call(name, new Node[0]).srcref(n);
/* 180*/        replacement.putBooleanProp(50, true);
/* 181*/        n.getParent().replaceChild(n, replacement);
/* 182*/        replacement.addChildToBack(n);
/*   0*/      } 
/* 184*/      this.compiler.reportCodeChange();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void addExtern() {
/* 189*/    Node name = IR.name("JSCOMPILER_PRESERVE");
/* 190*/    name.putBooleanProp(43, true);
/* 191*/    Node var = IR.var(name);
/* 193*/    JSDocInfoBuilder builder = new JSDocInfoBuilder(false);
/* 194*/    builder.recordNoAlias();
/* 195*/    var.setJSDocInfo(builder.build(var));
/* 196*/    CompilerInput input = this.compiler.getSynthesizedExternsInput();
/* 197*/    input.getAstRoot(this.compiler).addChildrenToBack(var);
/* 198*/    this.compiler.reportCodeChange();
/*   0*/  }
/*   0*/  
/*   0*/  static class StripProtection extends NodeTraversal.AbstractPostOrderCallback implements CompilerPass {
/*   0*/    private final AbstractCompiler compiler;
/*   0*/    
/*   0*/    StripProtection(AbstractCompiler compiler) {
/* 209*/      this.compiler = compiler;
/*   0*/    }
/*   0*/    
/*   0*/    public void process(Node externs, Node root) {
/* 214*/      NodeTraversal.traverse(this.compiler, root, this);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 219*/      if (n.isCall()) {
/* 220*/        Node target = n.getFirstChild();
/* 223*/        if (target.isName() && target.getString().equals("JSCOMPILER_PRESERVE")) {
/* 224*/          Node expr = n.getLastChild();
/* 225*/          n.detachChildren();
/* 226*/          parent.replaceChild(n, expr);
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/}
