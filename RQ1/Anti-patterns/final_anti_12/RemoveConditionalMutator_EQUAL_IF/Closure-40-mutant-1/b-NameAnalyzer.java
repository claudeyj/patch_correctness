/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.collect.ImmutableSet;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.jscomp.graph.DiGraph;
/*   0*/import com.google.javascript.jscomp.graph.FixedPointGraphTraversal;
/*   0*/import com.google.javascript.jscomp.graph.LinkedDirectedGraph;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.Token;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/final class NameAnalyzer implements CompilerPass {
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*  82*/  private final Map<String, JsName> allNames = Maps.newTreeMap();
/*   0*/  
/*  85*/  private DiGraph<JsName, RefType> referenceGraph = LinkedDirectedGraph.createWithoutAnnotations();
/*   0*/  
/*  95*/  private final Map<Node, NameInformation> scopes = Maps.newHashMap();
/*   0*/  
/*   0*/  private static final String PROTOTYPE_SUBSTRING = ".prototype.";
/*   0*/  
/* 100*/  private static final int PROTOTYPE_SUBSTRING_LEN = ".prototype.".length();
/*   0*/  
/* 103*/  private static final int PROTOTYPE_SUFFIX_LEN = ".prototype".length();
/*   0*/  
/*   0*/  private static final String WINDOW = "window";
/*   0*/  
/*   0*/  private static final String FUNCTION = "Function";
/*   0*/  
/* 112*/  static final Set<String> DEFAULT_GLOBAL_NAMES = (Set<String>)ImmutableSet.of("window", "goog.global");
/*   0*/  
/*   0*/  private final boolean removeUnreferenced;
/*   0*/  
/*   0*/  private final Set<String> globalNames;
/*   0*/  
/*   0*/  private final AstChangeProxy changeProxy;
/*   0*/  
/* 125*/  private final Set<String> externalNames = Sets.newHashSet();
/*   0*/  
/* 128*/  private final List<RefNode> refNodes = Lists.newArrayList();
/*   0*/  
/* 134*/  private final Map<String, AliasSet> aliases = Maps.newHashMap();
/*   0*/  
/*   0*/  private static class AliasSet {
/* 145*/    Set<String> names = Sets.newHashSet();
/*   0*/    
/*   0*/    AliasSet(String name1, String name2) {
/* 149*/      this.names.add(name1);
/* 150*/      this.names.add(name2);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private enum RefType {
/* 160*/    REGULAR, INHERITANCE;
/*   0*/  }
/*   0*/  
/*   0*/  private static class ReferencePropagationCallback implements FixedPointGraphTraversal.EdgeCallback<JsName, RefType> {
/*   0*/    private ReferencePropagationCallback() {}
/*   0*/    
/*   0*/    public boolean traverseEdge(NameAnalyzer.JsName from, NameAnalyzer.RefType callSite, NameAnalyzer.JsName to) {
/* 173*/      if (from.referenced && !to.referenced) {
/* 174*/        to.referenced = true;
/* 175*/        return true;
/*   0*/      } 
/* 177*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class NameInformation {
/*   0*/    String name;
/*   0*/    
/*   0*/    private NameInformation() {}
/*   0*/    
/*   0*/    boolean isExternallyReferenceable = false;
/*   0*/    
/*   0*/    boolean isPrototype = false;
/*   0*/    
/* 197*/    String prototypeClass = null;
/*   0*/    
/* 200*/    String prototypeProperty = null;
/*   0*/    
/* 203*/    String superclass = null;
/*   0*/    
/*   0*/    boolean onlyAffectsClassDef = false;
/*   0*/  }
/*   0*/  
/*   0*/  private static class JsName implements Comparable<JsName> {
/*   0*/    String name;
/*   0*/    
/* 217*/    List<String> prototypeNames = Lists.newArrayList();
/*   0*/    
/*   0*/    boolean externallyDefined = false;
/*   0*/    
/*   0*/    boolean referenced = false;
/*   0*/    
/*   0*/    boolean hasWrittenDescendants = false;
/*   0*/    
/*   0*/    boolean hasInstanceOfReference = false;
/*   0*/    
/*   0*/    public String toString() {
/* 238*/      StringBuilder out = new StringBuilder();
/* 239*/      out.append(this.name);
/* 241*/      if (this.prototypeNames.size() > 0) {
/* 242*/        out.append(" (CLASS)\n");
/* 243*/        out.append(" - FUNCTIONS: ");
/* 244*/        Iterator<String> pIter = this.prototypeNames.iterator();
/* 245*/        while (pIter.hasNext()) {
/* 246*/          out.append(pIter.next());
/* 247*/          if (pIter.hasNext())
/* 248*/            out.append(", "); 
/*   0*/        } 
/*   0*/      } 
/* 253*/      return out.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public int compareTo(JsName rhs) {
/* 258*/      return this.name.compareTo(rhs.name);
/*   0*/    }
/*   0*/    
/*   0*/    private JsName() {}
/*   0*/  }
/*   0*/  
/*   0*/  private class JsNameRefNode implements RefNode {
/*   0*/    NameAnalyzer.JsName name;
/*   0*/    
/*   0*/    Node node;
/*   0*/    
/*   0*/    Node parent;
/*   0*/    
/*   0*/    JsNameRefNode(NameAnalyzer.JsName name, Node node) {
/* 300*/      this.name = name;
/* 301*/      this.node = node;
/* 302*/      this.parent = node.getParent();
/*   0*/    }
/*   0*/    
/*   0*/    public NameAnalyzer.JsName name() {
/* 307*/      return this.name;
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/* 314*/      Node containingNode = this.parent.getParent();
/* 315*/      switch (this.parent.getType()) {
/*   0*/        case 118:
/* 317*/          Preconditions.checkState(this.parent.hasOneChild());
/* 318*/          NameAnalyzer.this.replaceWithRhs(containingNode, this.parent);
/*   0*/          break;
/*   0*/        case 105:
/* 321*/          NameAnalyzer.this.replaceWithRhs(containingNode, this.parent);
/*   0*/          break;
/*   0*/        case 86:
/* 324*/          if (containingNode.isExprResult()) {
/* 325*/            NameAnalyzer.this.replaceWithRhs(containingNode.getParent(), containingNode);
/*   0*/            break;
/*   0*/          } 
/* 327*/          NameAnalyzer.this.replaceWithRhs(containingNode, this.parent);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class PrototypeSetNode extends JsNameRefNode {
/*   0*/    PrototypeSetNode(NameAnalyzer.JsName name, Node parent) {
/* 351*/      super(name, parent.getFirstChild());
/* 353*/      Preconditions.checkState(parent.isAssign());
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/* 357*/      Node gramps = this.parent.getParent();
/* 358*/      if (gramps.isExprResult()) {
/* 360*/        NameAnalyzer.this.changeProxy.removeChild(gramps.getParent(), gramps);
/*   0*/      } else {
/* 363*/        NameAnalyzer.this.changeProxy.replaceWith(gramps, this.parent, this.parent.getLastChild().cloneTree());
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private abstract class SpecialReferenceNode implements RefNode {
/*   0*/    NameAnalyzer.JsName name;
/*   0*/    
/*   0*/    Node node;
/*   0*/    
/*   0*/    Node parent;
/*   0*/    
/*   0*/    Node gramps;
/*   0*/    
/*   0*/    SpecialReferenceNode(NameAnalyzer.JsName name, Node node, Node parent, Node gramps) {
/* 395*/      this.name = name;
/* 396*/      this.node = node;
/* 397*/      this.parent = parent;
/* 398*/      this.gramps = gramps;
/*   0*/    }
/*   0*/    
/*   0*/    public NameAnalyzer.JsName name() {
/* 403*/      return this.name;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class ClassDefiningFunctionNode extends SpecialReferenceNode {
/*   0*/    ClassDefiningFunctionNode(NameAnalyzer.JsName name, Node node, Node parent, Node gramps) {
/* 424*/      super(name, node, parent, gramps);
/* 425*/      Preconditions.checkState(node.isCall());
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/* 430*/      Preconditions.checkState(this.node.isCall());
/* 431*/      if (this.parent.isExprResult()) {
/* 432*/        NameAnalyzer.this.changeProxy.removeChild(this.gramps, this.parent);
/*   0*/      } else {
/* 434*/        NameAnalyzer.this.changeProxy.replaceWith(this.parent, this.node, IR.voidNode(IR.number(0.0D)));
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class InstanceOfCheckNode extends SpecialReferenceNode {
/*   0*/    InstanceOfCheckNode(NameAnalyzer.JsName name, Node node, Node parent, Node gramps) {
/* 455*/      super(name, node, parent, gramps);
/* 456*/      Preconditions.checkState(node.isQualifiedName());
/* 457*/      Preconditions.checkState(parent.isInstanceOf());
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/* 462*/      NameAnalyzer.this.changeProxy.replaceWith(this.gramps, this.parent, IR.falseNode());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class ProcessExternals extends NodeTraversal.AbstractPostOrderCallback {
/*   0*/    private ProcessExternals() {}
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 472*/      NameAnalyzer.NameInformation ns = null;
/* 473*/      if (NodeUtil.isVarDeclaration(n)) {
/* 474*/        ns = NameAnalyzer.this.createNameInformation(t, n, parent);
/* 475*/      } else if (NodeUtil.isFunctionDeclaration(n)) {
/* 476*/        ns = NameAnalyzer.this.createNameInformation(t, n.getFirstChild(), n);
/*   0*/      } 
/* 478*/      if (ns != null) {
/* 479*/        NameAnalyzer.JsName jsName = NameAnalyzer.this.getName(ns.name, true);
/* 480*/        jsName.externallyDefined = true;
/* 481*/        NameAnalyzer.this.externalNames.add(ns.name);
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class FindDependencyScopes extends NodeTraversal.AbstractPostOrderCallback {
/*   0*/    private FindDependencyScopes() {}
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 510*/      if (!t.inGlobalScope())
/*   0*/        return; 
/* 514*/      if (n.isAssign()) {
/* 515*/        Node nameNode = n.getFirstChild();
/* 516*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, nameNode, n);
/* 517*/        if (ns != null)
/* 518*/          if (parent.isFor() && !NodeUtil.isForIn(parent)) {
/* 528*/            if (parent.getFirstChild().getNext() != n) {
/* 529*/              recordDepScope(n, ns);
/*   0*/            } else {
/* 531*/              recordDepScope(nameNode, ns);
/*   0*/            } 
/*   0*/          } else {
/* 534*/            recordDepScope(n, ns);
/*   0*/          }  
/* 537*/      } else if (NodeUtil.isVarDeclaration(n)) {
/* 538*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, n, parent);
/* 539*/        recordDepScope(n, ns);
/* 540*/      } else if (NodeUtil.isFunctionDeclaration(n)) {
/* 541*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, n.getFirstChild(), n);
/* 542*/        recordDepScope(n, ns);
/* 543*/      } else if (NodeUtil.isExprCall(n)) {
/* 544*/        Node callNode = n.getFirstChild();
/* 545*/        Node nameNode = callNode.getFirstChild();
/* 546*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, nameNode, callNode);
/* 547*/        if (ns != null && ns.onlyAffectsClassDef)
/* 548*/          recordDepScope(n, ns); 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void recordDepScope(Node node, NameAnalyzer.NameInformation name) {
/* 557*/      NameAnalyzer.this.scopes.put(node, name);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class HoistVariableAndFunctionDeclarations extends NodeTraversal.AbstractShallowCallback {
/*   0*/    private HoistVariableAndFunctionDeclarations() {}
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 572*/      if (NodeUtil.isVarDeclaration(n)) {
/* 573*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, n, parent);
/* 574*/        Preconditions.checkNotNull(ns, "NameInformation is null");
/* 575*/        NameAnalyzer.this.createName(ns.name);
/* 576*/      } else if (NodeUtil.isFunctionDeclaration(n)) {
/* 577*/        Node nameNode = n.getFirstChild();
/* 578*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, nameNode, n);
/* 579*/        Preconditions.checkNotNull(ns, "NameInformation is null");
/* 580*/        NameAnalyzer.this.createName(nameNode.getString());
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class FindDeclarationsAndSetters extends NodeTraversal.AbstractPostOrderCallback {
/*   0*/    private FindDeclarationsAndSetters() {}
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 599*/      if (t.inGlobalScope())
/* 600*/        if (NodeUtil.isVarDeclaration(n)) {
/* 601*/          NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, n, parent);
/* 602*/          Preconditions.checkNotNull(ns);
/* 603*/          recordSet(ns.name, n);
/* 604*/        } else if (NodeUtil.isFunctionDeclaration(n)) {
/* 605*/          Node nameNode = n.getFirstChild();
/* 606*/          NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, nameNode, n);
/* 607*/          if (ns != null) {
/* 608*/            NameAnalyzer.JsName nameInfo = NameAnalyzer.this.getName(nameNode.getString(), true);
/* 609*/            recordSet(nameInfo.name, nameNode);
/*   0*/          } 
/* 611*/        } else if (NodeUtil.isObjectLitKey(n, parent)) {
/* 612*/          NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, n, parent);
/* 613*/          if (ns != null)
/* 614*/            recordSet(ns.name, n); 
/*   0*/        }  
/* 620*/      if (n.isAssign()) {
/* 621*/        Node nameNode = n.getFirstChild();
/* 623*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, nameNode, n);
/* 624*/        if (ns != null)
/* 625*/          if (ns.isPrototype) {
/* 626*/            recordPrototypeSet(ns.prototypeClass, ns.prototypeProperty, n);
/*   0*/          } else {
/* 628*/            recordSet(ns.name, nameNode);
/*   0*/          }  
/* 631*/      } else if (n.isCall()) {
/* 632*/        Node nameNode = n.getFirstChild();
/* 633*/        NameAnalyzer.NameInformation ns = NameAnalyzer.this.createNameInformation(t, nameNode, n);
/* 634*/        if (ns != null && ns.onlyAffectsClassDef) {
/* 635*/          NameAnalyzer.JsName name = NameAnalyzer.this.getName(ns.name, false);
/* 636*/          if (name != null)
/* 637*/            NameAnalyzer.this.refNodes.add(new NameAnalyzer.ClassDefiningFunctionNode(name, n, parent, parent.getParent())); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void recordSet(String name, Node node) {
/* 652*/      NameAnalyzer.JsName jsn = NameAnalyzer.this.getName(name, true);
/* 653*/      NameAnalyzer.JsNameRefNode nameRefNode = new NameAnalyzer.JsNameRefNode(jsn, node);
/* 654*/      NameAnalyzer.this.refNodes.add(nameRefNode);
/* 658*/      if (node.isGetElem()) {
/* 659*/        recordWriteOnProperties(name);
/* 660*/      } else if (name.indexOf('.') != -1) {
/* 661*/        recordWriteOnProperties(name.substring(0, name.lastIndexOf('.')));
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void recordPrototypeSet(String className, String prototypeProperty, Node node) {
/* 675*/      NameAnalyzer.JsName name = NameAnalyzer.this.getName(className, true);
/* 676*/      name.prototypeNames.add(prototypeProperty);
/* 677*/      NameAnalyzer.this.refNodes.add(new NameAnalyzer.PrototypeSetNode(name, node));
/* 678*/      recordWriteOnProperties(className);
/*   0*/    }
/*   0*/    
/*   0*/    private void recordWriteOnProperties(String parentName) {
/*   0*/      while (true) {
/* 686*/        NameAnalyzer.JsName parent = NameAnalyzer.this.getName(parentName, true);
/* 687*/        if (parent.hasWrittenDescendants)
/*   0*/          return; 
/* 692*/        parent.hasWrittenDescendants = true;
/* 695*/        if (parentName.indexOf('.') == -1)
/*   0*/          return; 
/* 698*/        parentName = parentName.substring(0, parentName.lastIndexOf('.'));
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 703*/  private static final Predicate<Node> NON_LOCAL_RESULT_PREDICATE = new Predicate<Node>() {
/*   0*/      public boolean apply(Node input) {
/* 707*/        if (input.isCall())
/* 708*/          return false; 
/* 713*/        return true;
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  private class FindReferences implements NodeTraversal.Callback {
/* 728*/    Set<Node> nodesToKeep = Sets.newHashSet();
/*   0*/    
/*   0*/    private void addAllChildren(Node n) {
/* 732*/      this.nodesToKeep.add(n);
/* 733*/      Node child = n.getFirstChild();
/* 734*/      for (; child != null; 
/* 735*/        child = child.getNext())
/* 736*/        addAllChildren(child); 
/*   0*/    }
/*   0*/    
/*   0*/    private void addSimplifiedChildren(Node n) {
/* 741*/      NodeTraversal.traverse(NameAnalyzer.this.compiler, n, new GatherSideEffectSubexpressionsCallback(NameAnalyzer.this.compiler, new NodeAccumulator()));
/*   0*/    }
/*   0*/    
/*   0*/    private void addSimplifiedExpression(Node n, Node parent) {
/* 748*/      if (parent.isVar()) {
/* 749*/        Node value = n.getFirstChild();
/* 750*/        if (value != null)
/* 751*/          addSimplifiedChildren(value); 
/* 753*/      } else if (n.isAssign() && (parent.isExprResult() || parent.isFor() || parent.isReturn())) {
/* 757*/        for (Node child : n.children())
/* 758*/          addSimplifiedChildren(child); 
/* 760*/      } else if (n.isCall() && parent.isExprResult()) {
/* 762*/        addSimplifiedChildren(n);
/*   0*/      } else {
/* 764*/        addAllChildren(n);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/* 770*/      if (parent == null)
/* 771*/        return true; 
/* 778*/      if (n.isFor())
/* 779*/        if (!NodeUtil.isForIn(n)) {
/* 780*/          Node decl = n.getFirstChild();
/* 781*/          Node pred = decl.getNext();
/* 782*/          Node step = pred.getNext();
/* 783*/          addSimplifiedExpression(decl, n);
/* 784*/          addSimplifiedExpression(pred, n);
/* 785*/          addSimplifiedExpression(step, n);
/*   0*/        } else {
/* 787*/          Node decl = n.getFirstChild();
/* 788*/          Node iter = decl.getNext();
/* 789*/          addAllChildren(decl);
/* 790*/          addAllChildren(iter);
/*   0*/        }  
/* 794*/      if (parent.isVar() || parent.isExprResult() || parent.isReturn() || parent.isThrow())
/* 798*/        addSimplifiedExpression(n, parent); 
/* 801*/      if ((parent.isIf() || parent.isWhile() || parent.isWith() || parent.isSwitch() || parent.isCase()) && parent.getFirstChild() == n)
/* 807*/        addAllChildren(n); 
/* 810*/      if (parent.isDo() && parent.getLastChild() == n)
/* 811*/        addAllChildren(n); 
/* 814*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/* 819*/      if (!n.isName() && (!NodeUtil.isGet(n) || parent.isGetProp()))
/*   0*/        return; 
/* 825*/      NameAnalyzer.NameInformation nameInfo = NameAnalyzer.this.createNameInformation(t, n, parent);
/* 826*/      if (nameInfo == null)
/*   0*/        return; 
/* 831*/      if (nameInfo.onlyAffectsClassDef) {
/* 832*/        if (nameInfo.superclass != null)
/* 833*/          NameAnalyzer.this.recordReference(nameInfo.name, nameInfo.superclass, NameAnalyzer.RefType.INHERITANCE); 
/* 840*/        String nodeName = n.getQualifiedName();
/* 841*/        if (nodeName != null)
/* 842*/          NameAnalyzer.this.recordReference(nameInfo.name, nodeName, NameAnalyzer.RefType.REGULAR); 
/*   0*/        return;
/*   0*/      } 
/* 849*/      if (parent.isInstanceOf() && parent.getLastChild() == n && n.isQualifiedName()) {
/* 853*/        NameAnalyzer.JsName checkedClass = NameAnalyzer.this.getName(nameInfo.name, true);
/* 854*/        NameAnalyzer.this.refNodes.add(new NameAnalyzer.InstanceOfCheckNode(checkedClass, n, parent, parent.getParent()));
/* 857*/        checkedClass.hasInstanceOfReference = true;
/*   0*/        return;
/*   0*/      } 
/* 865*/      NameAnalyzer.NameInformation referring = NameAnalyzer.this.getDependencyScope(n);
/* 866*/      String referringName = "";
/* 867*/      if (referring != null)
/* 868*/        referringName = referring.isPrototype ? referring.prototypeClass : referring.name; 
/* 873*/      String name = nameInfo.name;
/* 879*/      if (maybeHiddenAlias(name, n))
/* 880*/        NameAnalyzer.this.recordAlias(name, "window"); 
/* 885*/      if (nameInfo.isExternallyReferenceable) {
/* 886*/        NameAnalyzer.this.recordReference("window", name, NameAnalyzer.RefType.REGULAR);
/* 887*/        maybeRecordAlias(name, parent, referring, referringName);
/*   0*/        return;
/*   0*/      } 
/* 893*/      if (NodeUtil.isVarOrSimpleAssignLhs(n, parent)) {
/* 894*/        if (referring != null)
/* 895*/          NameAnalyzer.this.recordReference(referringName, name, NameAnalyzer.RefType.REGULAR); 
/*   0*/        return;
/*   0*/      } 
/* 900*/      if (this.nodesToKeep.contains(n)) {
/* 901*/        NameAnalyzer.NameInformation functionScope = NameAnalyzer.this.getEnclosingFunctionDependencyScope(t);
/* 902*/        if (functionScope != null) {
/* 903*/          NameAnalyzer.this.recordReference(functionScope.name, name, NameAnalyzer.RefType.REGULAR);
/*   0*/        } else {
/* 905*/          NameAnalyzer.this.recordReference("window", name, NameAnalyzer.RefType.REGULAR);
/*   0*/        } 
/* 907*/      } else if (referring != null) {
/* 908*/        if (!maybeRecordAlias(name, parent, referring, referringName)) {
/* 909*/          NameAnalyzer.RefType depType = referring.onlyAffectsClassDef ? NameAnalyzer.RefType.INHERITANCE : NameAnalyzer.RefType.REGULAR;
/* 911*/          NameAnalyzer.this.recordReference(referringName, name, depType);
/*   0*/        } 
/*   0*/      } else {
/* 918*/        for (Node ancestor : (Iterable<Node>)n.getAncestors()) {
/* 919*/          if (NodeUtil.isAssignmentOp(ancestor) || ancestor.isFunction()) {
/* 921*/            NameAnalyzer.this.recordReference("window", name, NameAnalyzer.RefType.REGULAR);
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean maybeHiddenAlias(String name, Node n) {
/* 935*/      Node parent = n.getParent();
/* 936*/      if (NodeUtil.isVarOrSimpleAssignLhs(n, parent)) {
/* 937*/        Node rhs = parent.isVar() ? n.getFirstChild() : parent.getLastChild();
/* 939*/        return (rhs != null && !NodeUtil.evaluatesToLocalValue(rhs, NameAnalyzer.NON_LOCAL_RESULT_PREDICATE));
/*   0*/      } 
/* 942*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean maybeRecordAlias(String name, Node parent, NameAnalyzer.NameInformation referring, String referringName) {
/* 951*/      if ((parent.isName() || parent.isAssign()) && referring != null && NameAnalyzer.this.scopes.get(parent) == referring) {
/* 955*/        NameAnalyzer.this.recordAlias(referringName, name);
/* 956*/        return true;
/*   0*/      } 
/* 958*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    private class NodeAccumulator implements GatherSideEffectSubexpressionsCallback.SideEffectAccumulator {
/*   0*/      private NodeAccumulator() {}
/*   0*/      
/*   0*/      public boolean classDefiningCallsHaveSideEffects() {
/* 970*/        return false;
/*   0*/      }
/*   0*/      
/*   0*/      public void keepSubTree(Node original) {
/* 975*/        NameAnalyzer.FindReferences.this.addAllChildren(original);
/*   0*/      }
/*   0*/      
/*   0*/      public void keepSimplifiedShortCircuitExpression(Node original) {
/* 980*/        Node condition = original.getFirstChild();
/* 981*/        Node thenBranch = condition.getNext();
/* 982*/        NameAnalyzer.FindReferences.this.addAllChildren(condition);
/* 983*/        NameAnalyzer.FindReferences.this.addSimplifiedChildren(thenBranch);
/*   0*/      }
/*   0*/      
/*   0*/      public void keepSimplifiedHookExpression(Node hook, boolean thenHasSideEffects, boolean elseHasSideEffects) {
/* 990*/        Node condition = hook.getFirstChild();
/* 991*/        Node thenBranch = condition.getNext();
/* 992*/        Node elseBranch = thenBranch.getNext();
/* 993*/        NameAnalyzer.FindReferences.this.addAllChildren(condition);
/* 994*/        if (thenHasSideEffects)
/* 995*/          NameAnalyzer.FindReferences.this.addSimplifiedChildren(thenBranch); 
/* 997*/        if (elseHasSideEffects)
/* 998*/          NameAnalyzer.FindReferences.this.addSimplifiedChildren(elseBranch); 
/*   0*/      }
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class RemoveListener implements AstChangeProxy.ChangeListener {
/*   0*/    private RemoveListener() {}
/*   0*/    
/*   0*/    public void nodeRemoved(Node n) {
/*1007*/      NameAnalyzer.this.compiler.reportCodeChange();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  NameAnalyzer(AbstractCompiler compiler, boolean removeUnreferenced) {
/*1024*/    this.compiler = compiler;
/*1025*/    this.removeUnreferenced = removeUnreferenced;
/*1026*/    this.globalNames = DEFAULT_GLOBAL_NAMES;
/*1027*/    this.changeProxy = new AstChangeProxy();
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/*1032*/    NodeTraversal.traverse(this.compiler, externs, new ProcessExternals());
/*1033*/    NodeTraversal.traverse(this.compiler, root, new FindDependencyScopes());
/*1034*/    NodeTraversal.traverse(this.compiler, root, new HoistVariableAndFunctionDeclarations());
/*1036*/    NodeTraversal.traverse(this.compiler, root, new FindDeclarationsAndSetters());
/*1037*/    NodeTraversal.traverse(this.compiler, root, new FindReferences());
/*1041*/    referenceParentNames();
/*1045*/    referenceAliases();
/*1047*/    calculateReferences();
/*1049*/    if (this.removeUnreferenced)
/*1050*/      removeUnreferenced(); 
/*   0*/  }
/*   0*/  
/*   0*/  private void recordAlias(String fromName, String toName) {
/*1058*/    recordReference(fromName, toName, RefType.REGULAR);
/*1069*/    AliasSet toNameAliasSet = this.aliases.get(toName);
/*1070*/    AliasSet fromNameAliasSet = this.aliases.get(fromName);
/*1071*/    AliasSet resultSet = null;
/*1072*/    if (toNameAliasSet == null && fromNameAliasSet == null) {
/*1073*/      resultSet = new AliasSet(toName, fromName);
/*1074*/    } else if (toNameAliasSet != null && fromNameAliasSet != null) {
/*1075*/      resultSet = toNameAliasSet;
/*1076*/      resultSet.names.addAll(fromNameAliasSet.names);
/*1077*/      for (String name : fromNameAliasSet.names)
/*1078*/        this.aliases.put(name, resultSet); 
/*1080*/    } else if (toNameAliasSet != null) {
/*1081*/      resultSet = toNameAliasSet;
/*1082*/      resultSet.names.add(fromName);
/*   0*/    } else {
/*1084*/      resultSet = fromNameAliasSet;
/*1085*/      resultSet.names.add(toName);
/*   0*/    } 
/*1087*/    this.aliases.put(fromName, resultSet);
/*1088*/    this.aliases.put(toName, resultSet);
/*   0*/  }
/*   0*/  
/*   0*/  private void recordReference(String fromName, String toName, RefType depType) {
/*1096*/    if (fromName.equals(toName))
/*   0*/      return; 
/*1101*/    JsName from = getName(fromName, true);
/*1102*/    JsName to = getName(toName, true);
/*1103*/    this.referenceGraph.createNode(from);
/*1104*/    this.referenceGraph.createNode(to);
/*1105*/    if (!this.referenceGraph.isConnectedInDirection(from, depType, to))
/*1106*/      this.referenceGraph.connect(from, depType, to); 
/*   0*/  }
/*   0*/  
/*   0*/  void removeUnreferenced() {
/*1114*/    RemoveListener listener = new RemoveListener();
/*1115*/    this.changeProxy.registerListener(listener);
/*1117*/    for (RefNode refNode : this.refNodes) {
/*1118*/      JsName name = refNode.name();
/*1119*/      if (!name.referenced && !name.externallyDefined)
/*1120*/        refNode.remove(); 
/*   0*/    } 
/*1124*/    this.changeProxy.unregisterListener(listener);
/*   0*/  }
/*   0*/  
/*   0*/  String getHtmlReport() {
/*1133*/    StringBuilder sb = new StringBuilder();
/*1134*/    sb.append("<html><body><style type=\"text/css\">body, td, p {font-family: Arial; font-size: 83%} ul {margin-top:2px; margin-left:0px; padding-left:1em;} li {margin-top:3px; margin-left:24px; padding-left:0px;padding-bottom: 4px}</style>");
/*1139*/    sb.append("OVERALL STATS<ul>");
/*1140*/    appendListItem(sb, "Total Names: " + countOf(TriState.BOTH, TriState.BOTH));
/*1141*/    appendListItem(sb, "Total Classes: " + countOf(TriState.TRUE, TriState.BOTH));
/*1143*/    appendListItem(sb, "Total Static Functions: " + countOf(TriState.FALSE, TriState.BOTH));
/*1145*/    appendListItem(sb, "Referenced Names: " + countOf(TriState.BOTH, TriState.TRUE));
/*1147*/    appendListItem(sb, "Referenced Classes: " + countOf(TriState.TRUE, TriState.TRUE));
/*1149*/    appendListItem(sb, "Referenced Functions: " + countOf(TriState.FALSE, TriState.TRUE));
/*1151*/    sb.append("</ul>");
/*1153*/    sb.append("ALL NAMES<ul>\n");
/*1154*/    for (JsName node : this.allNames.values()) {
/*1155*/      sb.append("<li>" + nameAnchor(node.name) + "<ul>");
/*1156*/      if (node.prototypeNames.size() > 0) {
/*1157*/        sb.append("<li>PROTOTYPES: ");
/*1158*/        Iterator<String> protoIter = node.prototypeNames.iterator();
/*1159*/        while (protoIter.hasNext()) {
/*1160*/          sb.append(protoIter.next());
/*1161*/          if (protoIter.hasNext())
/*1162*/            sb.append(", "); 
/*   0*/        } 
/*   0*/      } 
/*1167*/      if (this.referenceGraph.hasNode(node)) {
/*1168*/        List<DiGraph.DiGraphEdge<JsName, RefType>> refersTo = this.referenceGraph.getOutEdges(node);
/*1170*/        if (refersTo.size() > 0) {
/*1171*/          sb.append("<li>REFERS TO: ");
/*1172*/          Iterator<DiGraph.DiGraphEdge<JsName, RefType>> toIter = refersTo.iterator();
/*1173*/          while (toIter.hasNext()) {
/*1174*/            sb.append(nameLink(((JsName)((DiGraph.DiGraphEdge)toIter.next()).getDestination().getValue()).name));
/*1175*/            if (toIter.hasNext())
/*1176*/              sb.append(", "); 
/*   0*/          } 
/*   0*/        } 
/*1181*/        List<DiGraph.DiGraphEdge<JsName, RefType>> referencedBy = this.referenceGraph.getInEdges(node);
/*1183*/        if (referencedBy.size() > 0) {
/*1184*/          sb.append("<li>REFERENCED BY: ");
/*1185*/          Iterator<DiGraph.DiGraphEdge<JsName, RefType>> fromIter = refersTo.iterator();
/*1186*/          while (fromIter.hasNext()) {
/*1187*/            sb.append(nameLink(((JsName)((DiGraph.DiGraphEdge)fromIter.next()).getDestination().getValue()).name));
/*1189*/            if (fromIter.hasNext())
/*1190*/              sb.append(", "); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1195*/      sb.append("</li>");
/*1196*/      sb.append("</ul></li>");
/*   0*/    } 
/*1198*/    sb.append("</ul>");
/*1199*/    sb.append("</body></html>");
/*1201*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void appendListItem(StringBuilder sb, String text) {
/*1205*/    sb.append("<li>" + text + "</li>\n");
/*   0*/  }
/*   0*/  
/*   0*/  private String nameLink(String name) {
/*1209*/    return "<a href=\"#" + name + "\">" + name + "</a>";
/*   0*/  }
/*   0*/  
/*   0*/  private String nameAnchor(String name) {
/*1213*/    return "<a name=\"" + name + "\">" + name + "</a>";
/*   0*/  }
/*   0*/  
/*   0*/  private JsName getName(String name, boolean canCreate) {
/*1227*/    createName(name);
/*1229*/    return this.allNames.get(name);
/*   0*/  }
/*   0*/  
/*   0*/  private void createName(String name) {
/*1239*/    JsName jsn = this.allNames.get(name);
/*1240*/    if (jsn == null) {
/*1241*/      jsn = new JsName();
/*1242*/      jsn.name = name;
/*1243*/      this.allNames.put(name, jsn);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void referenceAliases() {
/*1273*/    for (Map.Entry<String, AliasSet> entry : this.aliases.entrySet()) {
/*1274*/      JsName name = getName(entry.getKey(), false);
/*1275*/      if (name.hasWrittenDescendants || name.hasInstanceOfReference)
/*1276*/        for (String alias : ((AliasSet)entry.getValue()).names)
/*1277*/          recordReference(alias, entry.getKey(), RefType.REGULAR);  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void referenceParentNames() {
/*1290*/    Set<JsName> allNamesCopy = Sets.newHashSet(this.allNames.values());
/*1292*/    for (JsName name : allNamesCopy) {
/*1293*/      String curName = name.name;
/*1294*/      JsName curJsName = name;
/*1295*/      while (curName.indexOf('.') != -1) {
/*1296*/        String parentName = curName.substring(0, curName.lastIndexOf('.'));
/*1297*/        if (!this.globalNames.contains(parentName)) {
/*1299*/          JsName parentJsName = getName(parentName, true);
/*1301*/          recordReference(curJsName.name, parentJsName.name, RefType.REGULAR);
/*1302*/          recordReference(parentJsName.name, curJsName.name, RefType.REGULAR);
/*1304*/          curJsName = parentJsName;
/*   0*/        } 
/*1306*/        curName = parentName;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private NameInformation createNameInformation(NodeTraversal t, Node n, Node parent) {
/*1324*/    String name = "";
/*1325*/    Node rootNameNode = n;
/*   0*/    boolean bNameWasShortened = false;
/*   0*/    while (true) {
/*1328*/      while (NodeUtil.isGet(rootNameNode)) {
/*1329*/        Node prop = rootNameNode.getLastChild();
/*1330*/        if (rootNameNode.isGetProp()) {
/*1331*/          name = "." + prop.getString() + name;
/*   0*/        } else {
/*1334*/          bNameWasShortened = true;
/*1335*/          name = "";
/*   0*/        } 
/*1337*/        rootNameNode = rootNameNode.getFirstChild();
/*   0*/      } 
/*1338*/      if (NodeUtil.isObjectLitKey(rootNameNode, rootNameNode.getParent())) {
/*1340*/        name = "." + rootNameNode.getString() + name;
/*1343*/        Node objLit = rootNameNode.getParent();
/*1344*/        Node objLitParent = objLit.getParent();
/*1345*/        if (objLitParent.isAssign()) {
/*1347*/          rootNameNode = objLitParent.getFirstChild();
/*   0*/          continue;
/*   0*/        } 
/*1348*/        if (objLitParent.isName()) {
/*1350*/          rootNameNode = objLitParent;
/*   0*/          continue;
/*   0*/        } 
/*1351*/        if (objLitParent.isString()) {
/*1353*/          rootNameNode = objLitParent;
/*   0*/          continue;
/*   0*/        } 
/*1355*/        return null;
/*   0*/      } 
/*   0*/      break;
/*   0*/    } 
/*1364*/    if (parent.isCall() && t.inGlobalScope()) {
/*1365*/      CodingConvention convention = this.compiler.getCodingConvention();
/*1366*/      CodingConvention.SubclassRelationship classes = convention.getClassesDefinedByCall(parent);
/*1367*/      if (classes != null) {
/*1368*/        NameInformation nameInfo = new NameInformation();
/*1369*/        nameInfo.name = classes.subclassName;
/*1370*/        nameInfo.onlyAffectsClassDef = true;
/*1371*/        nameInfo.superclass = classes.superclassName;
/*1372*/        return nameInfo;
/*   0*/      } 
/*1375*/      String singletonGetterClass = convention.getSingletonGetterClassName(parent);
/*1377*/      if (singletonGetterClass != null) {
/*1378*/        NameInformation nameInfo = new NameInformation();
/*1379*/        nameInfo.name = singletonGetterClass;
/*1380*/        nameInfo.onlyAffectsClassDef = true;
/*1381*/        return nameInfo;
/*   0*/      } 
/*   0*/    } 
/*1385*/    switch (rootNameNode.getType()) {
/*   0*/      case 38:
/*1389*/        if (!bNameWasShortened && n.isGetProp() && parent.isAssign() && "prototype".equals(n.getLastChild().getString())) {
/*1393*/          if (createNameInformation(t, n.getFirstChild(), n) != null) {
/*1394*/            name = rootNameNode.getString() + name;
/*1395*/            name = name.substring(0, name.length() - PROTOTYPE_SUFFIX_LEN);
/*1396*/            NameInformation nameInfo = new NameInformation();
/*1397*/            nameInfo.name = name;
/*1398*/            return nameInfo;
/*   0*/          } 
/*1400*/          return null;
/*   0*/        } 
/*1403*/        return createNameInformation(rootNameNode.getString() + name, t.getScope(), rootNameNode);
/*   0*/      case 42:
/*1406*/        if (t.inGlobalScope()) {
/*1407*/          NameInformation nameInfo = new NameInformation();
/*1408*/          if (name.indexOf('.') == 0) {
/*1409*/            nameInfo.name = name.substring(1);
/*   0*/          } else {
/*1411*/            nameInfo.name = name;
/*   0*/          } 
/*1413*/          nameInfo.isExternallyReferenceable = true;
/*1414*/          return nameInfo;
/*   0*/        } 
/*1416*/        return null;
/*   0*/    } 
/*1418*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private NameInformation createNameInformation(String name, Scope scope, Node rootNameNode) {
/*1435*/    String rootName = rootNameNode.getString();
/*1436*/    Scope.Var v = scope.getVar(rootName);
/*1437*/    boolean isExtern = (v == null && this.externalNames.contains(rootName));
/*1438*/    boolean isGlobalRef = ((v != null && v.isGlobal()) || isExtern || rootName.equals("window"));
/*1440*/    if (!isGlobalRef)
/*1441*/      return null; 
/*1444*/    NameInformation nameInfo = new NameInformation();
/*1447*/    int idx = name.indexOf(".prototype.");
/*1448*/    if (idx != -1) {
/*1449*/      nameInfo.isPrototype = true;
/*1450*/      nameInfo.prototypeClass = name.substring(0, idx);
/*1451*/      nameInfo.prototypeProperty = name.substring(idx + PROTOTYPE_SUBSTRING_LEN);
/*   0*/    } 
/*1455*/    nameInfo.name = name;
/*1456*/    nameInfo.isExternallyReferenceable = (isExtern || isExternallyReferenceable(scope, name));
/*1458*/    return nameInfo;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isExternallyReferenceable(Scope scope, String name) {
/*1470*/    if (this.compiler.getCodingConvention().isExported(name))
/*1471*/      return true; 
/*1473*/    if (scope.isLocal())
/*1474*/      return false; 
/*1476*/    for (String s : this.globalNames) {
/*1477*/      if (name.startsWith(s))
/*1478*/        return true; 
/*   0*/    } 
/*1481*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private NameInformation getDependencyScope(Node n) {
/*1488*/    for (Node node : (Iterable<Node>)n.getAncestors()) {
/*1489*/      NameInformation ref = this.scopes.get(node);
/*1490*/      if (ref != null)
/*1491*/        return ref; 
/*   0*/    } 
/*1495*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private NameInformation getEnclosingFunctionDependencyScope(NodeTraversal t) {
/*1505*/    Node function = t.getEnclosingFunction();
/*1506*/    if (function == null)
/*1507*/      return null; 
/*1510*/    NameInformation ref = this.scopes.get(function);
/*1511*/    if (ref != null)
/*1512*/      return ref; 
/*1517*/    Node parent = function.getParent();
/*1518*/    if (parent != null) {
/*1521*/      while (parent.isHook())
/*1522*/        parent = parent.getParent(); 
/*1525*/      if (parent.isName())
/*1526*/        return this.scopes.get(parent); 
/*1529*/      if (parent.isAssign())
/*1530*/        return this.scopes.get(parent); 
/*   0*/    } 
/*1534*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private void calculateReferences() {
/*1541*/    JsName window = getName("window", true);
/*1542*/    window.referenced = true;
/*1543*/    JsName function = getName("Function", true);
/*1544*/    function.referenced = true;
/*1547*/    FixedPointGraphTraversal.<JsName, RefType>newTraversal(new ReferencePropagationCallback()).computeFixedPoint(this.referenceGraph);
/*   0*/  }
/*   0*/  
/*   0*/  private enum TriState {
/*1558*/    TRUE, FALSE, BOTH;
/*   0*/  }
/*   0*/  
/*   0*/  private int countOf(TriState isClass, TriState referenced) {
/*1573*/    int count = 0;
/*1574*/    for (JsName name : this.allNames.values()) {
/*1576*/      boolean nodeIsClass = (name.prototypeNames.size() > 0);
/*1578*/      boolean classMatch = (isClass == TriState.BOTH || (nodeIsClass && isClass == TriState.TRUE) || (!nodeIsClass && isClass == TriState.FALSE));
/*1582*/      boolean referenceMatch = (referenced == TriState.BOTH || (name.referenced && referenced == TriState.TRUE) || (!name.referenced && referenced == TriState.FALSE));
/*1586*/      if (classMatch && referenceMatch && !name.externallyDefined)
/*1587*/        count++; 
/*   0*/    } 
/*1590*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private List<Node> getSideEffectNodes(Node n) {
/*1598*/    List<Node> subexpressions = Lists.newArrayList();
/*1599*/    NodeTraversal.traverse(this.compiler, n, new GatherSideEffectSubexpressionsCallback(this.compiler, new GatherSideEffectSubexpressionsCallback.CopySideEffectSubexpressions(this.compiler, subexpressions)));
/*1605*/    List<Node> replacements = Lists.newArrayListWithExpectedSize(subexpressions.size());
/*1607*/    for (Node subexpression : subexpressions)
/*1608*/      replacements.add(NodeUtil.newExpr(subexpression)); 
/*1610*/    return replacements;
/*   0*/  }
/*   0*/  
/*   0*/  private void replaceWithRhs(Node parent, Node n) {
/*1621*/    if (valueConsumedByParent(n, parent)) {
/*1624*/      List<Node> replacements = getRhsSubexpressions(n);
/*1625*/      List<Node> newReplacements = Lists.newArrayList();
/*1626*/      for (int i = 0; i < replacements.size() - 1; i++)
/*1627*/        newReplacements.addAll(getSideEffectNodes(replacements.get(i))); 
/*1629*/      Node valueExpr = replacements.get(replacements.size() - 1);
/*1630*/      valueExpr.detachFromParent();
/*1631*/      newReplacements.add(valueExpr);
/*1632*/      this.changeProxy.replaceWith(parent, n, collapseReplacements(newReplacements));
/*1634*/    } else if (n.isAssign() && !parent.isFor()) {
/*1640*/      Node replacement = n.getLastChild();
/*1641*/      replacement.detachFromParent();
/*1642*/      this.changeProxy.replaceWith(parent, n, replacement);
/*   0*/    } else {
/*1644*/      replaceTopLevelExpressionWithRhs(parent, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void replaceTopLevelExpressionWithRhs(Node parent, Node n) {
/*1654*/    switch (parent.getType()) {
/*   0*/      case 115:
/*   0*/      case 125:
/*   0*/      case 126:
/*   0*/      case 132:
/*   0*/        break;
/*   0*/      default:
/*1661*/        throw new IllegalArgumentException("Unsupported parent node type in replaceWithRhs " + Token.name(parent.getType()));
/*   0*/    } 
/*1666*/    switch (n.getType()) {
/*   0*/      case 105:
/*   0*/      case 118:
/*   0*/      case 130:
/*   0*/        break;
/*   0*/      case 86:
/*1672*/        Preconditions.checkArgument(parent.isFor(), "Unsupported assignment in replaceWithRhs. parent: %s", new Object[] { Token.name(parent.getType()) });
/*   0*/        break;
/*   0*/      default:
/*1676*/        throw new IllegalArgumentException("Unsupported node type in replaceWithRhs " + Token.name(n.getType()));
/*   0*/    } 
/*1682*/    List<Node> replacements = Lists.newArrayList();
/*1683*/    for (Node rhs : getRhsSubexpressions(n))
/*1684*/      replacements.addAll(getSideEffectNodes(rhs)); 
/*1687*/    if (parent.isFor())
/*1689*/      if (replacements.isEmpty()) {
/*1690*/        replacements.add(IR.empty());
/*   0*/      } else {
/*1692*/        Node expr = collapseReplacements(replacements);
/*1693*/        replacements.clear();
/*1694*/        replacements.add(expr);
/*   0*/      }  
/*1698*/    this.changeProxy.replaceWith(parent, n, replacements);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean valueConsumedByParent(Node n, Node parent) {
/*1717*/    if (NodeUtil.isAssignmentOp(parent))
/*1718*/      return (parent.getLastChild() == n); 
/*1721*/    switch (parent.getType()) {
/*   0*/      case 4:
/*   0*/      case 38:
/*1724*/        return true;
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/*1728*/        return (parent.getFirstChild() == n);
/*   0*/      case 115:
/*1730*/        return (parent.getFirstChild().getNext() == n);
/*   0*/      case 108:
/*   0*/      case 113:
/*1733*/        return (parent.getFirstChild() == n);
/*   0*/      case 114:
/*1735*/        return (parent.getLastChild() == n);
/*   0*/    } 
/*1737*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private Node collapseReplacements(List<Node> replacements) {
/*1746*/    Node expr = null;
/*1747*/    for (Node rep : replacements) {
/*1748*/      if (rep.isExprResult()) {
/*1749*/        rep = rep.getFirstChild();
/*1750*/        rep.detachFromParent();
/*   0*/      } 
/*1753*/      if (expr == null) {
/*1754*/        expr = rep;
/*   0*/        continue;
/*   0*/      } 
/*1756*/      expr = IR.comma(expr, rep);
/*   0*/    } 
/*1760*/    return expr;
/*   0*/  }
/*   0*/  
/*   0*/  private List<Node> getRhsSubexpressions(Node n) {
/*   0*/    Node rhs, lhs;
/*   0*/    List<Node> nodes;
/*   0*/    Node node1;
/*1767*/    switch (n.getType()) {
/*   0*/      case 130:
/*1770*/        return getRhsSubexpressions(n.getFirstChild());
/*   0*/      case 105:
/*1773*/        return Collections.emptyList();
/*   0*/      case 38:
/*1777*/        rhs = n.getFirstChild();
/*1778*/        if (rhs != null)
/*1779*/          return Lists.newArrayList((Object[])new Node[] { rhs }); 
/*1781*/        return Collections.emptyList();
/*   0*/      case 86:
/*1787*/        lhs = n.getFirstChild();
/*1788*/        node1 = lhs.getNext();
/*1789*/        return Lists.newArrayList((Object[])new Node[] { lhs, node1 });
/*   0*/      case 118:
/*1794*/        nodes = Lists.newArrayList();
/*1795*/        for (Node child : n.children())
/*1796*/          nodes.addAll(getRhsSubexpressions(child)); 
/*1798*/        return nodes;
/*   0*/    } 
/*1801*/    throw new IllegalArgumentException("AstChangeProxy::getRhs " + n);
/*   0*/  }
/*   0*/  
/*   0*/  static interface RefNode {
/*   0*/    NameAnalyzer.JsName name();
/*   0*/    
/*   0*/    void remove();
/*   0*/  }
/*   0*/}
