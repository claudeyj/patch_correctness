/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.collect.ImmutableList;
/*   0*/import com.google.common.collect.ImmutableSet;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.TokenStream;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeNative;
/*   0*/import com.google.javascript.rhino.jstype.StaticReference;
/*   0*/import com.google.javascript.rhino.jstype.StaticScope;
/*   0*/import com.google.javascript.rhino.jstype.StaticSlot;
/*   0*/import com.google.javascript.rhino.jstype.StaticSourceFile;
/*   0*/import com.google.javascript.rhino.jstype.StaticSymbolTable;
/*   0*/import java.io.PrintStream;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class GlobalNamespace implements StaticScope<JSType>, StaticSymbolTable<GlobalNamespace.Name, GlobalNamespace.Ref> {
/*   0*/  private AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final Node root;
/*   0*/  
/*   0*/  private final Node externsRoot;
/*   0*/  
/*   0*/  private boolean inExterns;
/*   0*/  
/*   0*/  private Scope externsScope;
/*   0*/  
/*   0*/  private boolean generated = false;
/*   0*/  
/*  67*/  private int currentPreOrderIndex = 0;
/*   0*/  
/*  70*/  private List<Name> globalNames = new ArrayList<Name>();
/*   0*/  
/*  73*/  private Map<String, Name> nameMap = new HashMap<String, Name>();
/*   0*/  
/*   0*/  GlobalNamespace(AbstractCompiler compiler, Node root) {
/*  82*/    this(compiler, null, root);
/*   0*/  }
/*   0*/  
/*   0*/  GlobalNamespace(AbstractCompiler compiler, Node externsRoot, Node root) {
/*  97*/    this.compiler = compiler;
/*  98*/    this.externsRoot = externsRoot;
/*  99*/    this.root = root;
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasExternsRoot() {
/* 103*/    return (this.externsRoot != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getRootNode() {
/* 108*/    return this.root.getParent();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticScope<JSType> getParentScope() {
/* 113*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Name getSlot(String name) {
/* 118*/    return getOwnSlot(name);
/*   0*/  }
/*   0*/  
/*   0*/  public Name getOwnSlot(String name) {
/* 123*/    ensureGenerated();
/* 124*/    return this.nameMap.get(name);
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getTypeOfThis() {
/* 129*/    return this.compiler.getTypeRegistry().getNativeObjectType(JSTypeNative.GLOBAL_THIS);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Ref> getReferences(Name slot) {
/* 134*/    ensureGenerated();
/* 135*/    return Collections.unmodifiableList(slot.getRefs());
/*   0*/  }
/*   0*/  
/*   0*/  public StaticScope<JSType> getScope(Name slot) {
/* 140*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Name> getAllSymbols() {
/* 145*/    ensureGenerated();
/* 146*/    return Collections.unmodifiableCollection(getNameIndex().values());
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureGenerated() {
/* 150*/    if (!this.generated)
/* 151*/      process(); 
/*   0*/  }
/*   0*/  
/*   0*/  List<Name> getNameForest() {
/* 160*/    ensureGenerated();
/* 161*/    return this.globalNames;
/*   0*/  }
/*   0*/  
/*   0*/  Map<String, Name> getNameIndex() {
/* 169*/    ensureGenerated();
/* 170*/    return this.nameMap;
/*   0*/  }
/*   0*/  
/*   0*/  void scanNewNodes(Scope scope, Set<Node> newNodes) {
/* 180*/    NodeTraversal t = new NodeTraversal(this.compiler, new BuildGlobalNamespace(new NodeFilter(newNodes)));
/* 182*/    t.traverseAtScope(scope);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NodeFilter implements Predicate<Node> {
/*   0*/    private final Set<Node> newNodes;
/*   0*/    
/*   0*/    NodeFilter(Set<Node> newNodes) {
/* 193*/      this.newNodes = newNodes;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean apply(Node n) {
/* 198*/      if (!n.isQualifiedName())
/* 199*/        return false; 
/* 203*/      Node current = n;
/* 204*/      for (; current.isGetProp(); 
/* 205*/        current = current.getFirstChild()) {
/* 206*/        if (this.newNodes.contains(current))
/* 207*/          return true; 
/*   0*/      } 
/* 211*/      return (current.isName() && this.newNodes.contains(current));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private void process() {
/* 219*/    if (this.externsRoot != null) {
/* 220*/      this.inExterns = true;
/* 221*/      NodeTraversal.traverse(this.compiler, this.externsRoot, new BuildGlobalNamespace());
/*   0*/    } 
/* 223*/    this.inExterns = false;
/* 225*/    NodeTraversal.traverse(this.compiler, this.root, new BuildGlobalNamespace());
/* 226*/    this.generated = true;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isGlobalNameReference(String name, Scope s) {
/* 238*/    String topVarName = getTopVarName(name);
/* 239*/    return isGlobalVarReference(topVarName, s);
/*   0*/  }
/*   0*/  
/*   0*/  private String getTopVarName(String name) {
/* 249*/    int firstDotIndex = name.indexOf('.');
/* 250*/    return (firstDotIndex == -1) ? name : name.substring(0, firstDotIndex);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isGlobalVarReference(String name, Scope s) {
/* 262*/    Scope.Var v = s.getVar(name);
/* 263*/    if (v == null && this.externsScope != null)
/* 264*/      v = this.externsScope.getVar(name); 
/* 266*/    return (v != null && !v.isLocal());
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isGlobalScope(Scope s) {
/* 276*/    return (s.getParent() == null);
/*   0*/  }
/*   0*/  
/*   0*/  private class BuildGlobalNamespace implements NodeTraversal.Callback {
/*   0*/    private final Predicate<Node> nodeFilter;
/*   0*/    
/*   0*/    BuildGlobalNamespace() {
/* 289*/      this(null);
/*   0*/    }
/*   0*/    
/*   0*/    BuildGlobalNamespace(Predicate<Node> nodeFilter) {
/* 297*/      this.nodeFilter = nodeFilter;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {}
/*   0*/    
/*   0*/    public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/* 306*/      collect(t, n, parent);
/* 307*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void collect(NodeTraversal t, Node n, Node parent) {
/*   0*/      String name;
/* 311*/      if (this.nodeFilter != null && !this.nodeFilter.apply(n))
/*   0*/        return; 
/* 317*/      if (GlobalNamespace.this.externsRoot != null && n == GlobalNamespace.this.externsRoot)
/* 318*/        GlobalNamespace.this.externsScope = t.getScope(); 
/*   0*/      boolean isSet = false;
/* 323*/      GlobalNamespace.Name.Type type = GlobalNamespace.Name.Type.OTHER;
/*   0*/      boolean isPropAssign = false;
/* 326*/      switch (n.getType()) {
/*   0*/        case 147:
/*   0*/        case 148:
/*   0*/        case 154:
/* 331*/          name = null;
/* 332*/          if (parent != null && parent.isObjectLit())
/* 333*/            name = getNameForObjLitKey(n); 
/* 335*/          if (name == null)
/*   0*/            return; 
/* 336*/          isSet = true;
/* 337*/          switch (n.getType()) {
/*   0*/            case 154:
/* 339*/              type = getValueType(n.getFirstChild());
/*   0*/              break;
/*   0*/            case 147:
/* 342*/              type = GlobalNamespace.Name.Type.GET;
/*   0*/              break;
/*   0*/            case 148:
/* 345*/              type = GlobalNamespace.Name.Type.SET;
/*   0*/              break;
/*   0*/          } 
/* 348*/          throw new IllegalStateException("unexpected:" + n);
/*   0*/        case 38:
/* 353*/          if (parent != null) {
/*   0*/            Node rvalue, gramps;
/* 354*/            switch (parent.getType()) {
/*   0*/              case 118:
/* 356*/                isSet = true;
/* 357*/                rvalue = n.getFirstChild();
/* 358*/                type = (rvalue == null) ? GlobalNamespace.Name.Type.GET : getValueType(rvalue);
/*   0*/                break;
/*   0*/              case 86:
/* 361*/                if (parent.getFirstChild() == n) {
/* 362*/                  isSet = true;
/* 363*/                  type = getValueType(n.getNext());
/*   0*/                } 
/*   0*/                break;
/*   0*/              case 33:
/*   0*/                return;
/*   0*/              case 105:
/* 369*/                gramps = parent.getParent();
/* 370*/                if (gramps == null || NodeUtil.isFunctionExpression(parent))
/*   0*/                  return; 
/* 372*/                isSet = true;
/* 373*/                type = GlobalNamespace.Name.Type.FUNCTION;
/*   0*/                break;
/*   0*/              case 102:
/*   0*/              case 103:
/* 377*/                isSet = true;
/* 378*/                type = GlobalNamespace.Name.Type.OTHER;
/*   0*/                break;
/*   0*/              default:
/* 381*/                if (NodeUtil.isAssignmentOp(parent) && parent.getFirstChild() == n) {
/* 383*/                  isSet = true;
/* 384*/                  type = GlobalNamespace.Name.Type.OTHER;
/*   0*/                } 
/*   0*/                break;
/*   0*/            } 
/*   0*/          } 
/* 388*/          name = n.getString();
/*   0*/          break;
/*   0*/        case 33:
/* 392*/          if (parent != null)
/* 393*/            switch (parent.getType()) {
/*   0*/              case 86:
/* 395*/                if (parent.getFirstChild() == n) {
/* 396*/                  isSet = true;
/* 397*/                  type = getValueType(n.getNext());
/* 398*/                  isPropAssign = true;
/*   0*/                } 
/*   0*/                break;
/*   0*/              case 102:
/*   0*/              case 103:
/* 403*/                isSet = true;
/* 404*/                type = GlobalNamespace.Name.Type.OTHER;
/*   0*/                break;
/*   0*/              case 33:
/*   0*/                return;
/*   0*/              default:
/* 409*/                if (NodeUtil.isAssignmentOp(parent) && parent.getFirstChild() == n) {
/* 411*/                  isSet = true;
/* 412*/                  type = GlobalNamespace.Name.Type.OTHER;
/*   0*/                } 
/*   0*/                break;
/*   0*/            }  
/* 416*/          name = n.getQualifiedName();
/* 417*/          if (name == null)
/*   0*/            return; 
/*   0*/          break;
/*   0*/        default:
/*   0*/          return;
/*   0*/      } 
/* 424*/      Scope scope = t.getScope();
/* 425*/      if (!GlobalNamespace.this.isGlobalNameReference(name, scope))
/*   0*/        return; 
/* 429*/      if (isSet) {
/* 430*/        if (GlobalNamespace.this.isGlobalScope(scope)) {
/* 431*/          handleSetFromGlobal(t, n, parent, name, isPropAssign, type);
/*   0*/        } else {
/* 433*/          handleSetFromLocal(t, n, parent, name);
/*   0*/        } 
/*   0*/      } else {
/* 436*/        handleGet(t, n, parent, name);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    String getNameForObjLitKey(Node n) {
/*   0*/      String name;
/*   0*/      Node lvalue;
/* 457*/      Node parent = n.getParent();
/* 458*/      Preconditions.checkState(parent.isObjectLit());
/* 460*/      Node gramps = parent.getParent();
/* 461*/      if (gramps == null)
/* 462*/        return null; 
/* 465*/      Node greatGramps = gramps.getParent();
/* 467*/      switch (gramps.getType()) {
/*   0*/        case 38:
/* 473*/          if (greatGramps == null || !greatGramps.isVar())
/* 474*/            return null; 
/* 476*/          name = gramps.getString();
/*   0*/          break;
/*   0*/        case 86:
/* 483*/          lvalue = gramps.getFirstChild();
/* 484*/          name = lvalue.getQualifiedName();
/*   0*/          break;
/*   0*/        case 154:
/* 491*/          if (greatGramps != null && greatGramps.isObjectLit()) {
/* 493*/            name = getNameForObjLitKey(gramps);
/*   0*/            break;
/*   0*/          } 
/* 495*/          return null;
/*   0*/        default:
/* 499*/          return null;
/*   0*/      } 
/* 501*/      if (name != null) {
/* 502*/        String key = n.getString();
/* 503*/        if (TokenStream.isJSIdentifier(key))
/* 504*/          return name + '.' + key; 
/*   0*/      } 
/* 507*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    GlobalNamespace.Name.Type getValueType(Node n) {
/*   0*/      Node second;
/*   0*/      GlobalNamespace.Name.Type t;
/*   0*/      Node third;
/* 517*/      switch (n.getType()) {
/*   0*/        case 64:
/* 519*/          return GlobalNamespace.Name.Type.OBJECTLIT;
/*   0*/        case 105:
/* 521*/          return GlobalNamespace.Name.Type.FUNCTION;
/*   0*/        case 100:
/* 528*/          return getValueType(n.getLastChild());
/*   0*/        case 98:
/* 531*/          second = n.getFirstChild().getNext();
/* 532*/          t = getValueType(second);
/* 533*/          if (t != GlobalNamespace.Name.Type.OTHER)
/* 533*/            return t; 
/* 534*/          third = second.getNext();
/* 535*/          return getValueType(third);
/*   0*/      } 
/* 537*/      return GlobalNamespace.Name.Type.OTHER;
/*   0*/    }
/*   0*/    
/*   0*/    void handleSetFromGlobal(NodeTraversal t, Node n, Node parent, String name, boolean isPropAssign, GlobalNamespace.Name.Type type) {
/* 554*/      if (maybeHandlePrototypePrefix(t, n, parent, name))
/*   0*/        return; 
/* 556*/      GlobalNamespace.Name nameObj = getOrCreateName(name);
/* 557*/      nameObj.type = type;
/* 559*/      GlobalNamespace.Ref set = new GlobalNamespace.Ref(t, n, nameObj, GlobalNamespace.Ref.Type.SET_FROM_GLOBAL, GlobalNamespace.this.currentPreOrderIndex++);
/* 561*/      nameObj.addRef(set);
/* 563*/      if (isNestedAssign(parent)) {
/* 565*/        GlobalNamespace.Ref get = new GlobalNamespace.Ref(t, n, nameObj, GlobalNamespace.Ref.Type.ALIASING_GET, GlobalNamespace.this.currentPreOrderIndex++);
/* 567*/        nameObj.addRef(get);
/* 568*/        GlobalNamespace.Ref.markTwins(set, get);
/* 569*/      } else if (isTypeDeclaration(n, parent)) {
/* 571*/        nameObj.setDeclaredType();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isTypeDeclaration(Node n, Node parent) {
/* 586*/      Node valueNode = NodeUtil.getRValueOfLValue(n);
/* 587*/      JSDocInfo info = NodeUtil.getBestJSDocInfo(n);
/* 589*/      return (info != null && valueNode != null && ((info.isConstructor() && valueNode.isFunction()) || (info.isInterface() && valueNode.isFunction()) || (info.hasEnumParameterType() && valueNode.isObjectLit())));
/*   0*/    }
/*   0*/    
/*   0*/    void handleSetFromLocal(NodeTraversal t, Node n, Node parent, String name) {
/* 606*/      if (maybeHandlePrototypePrefix(t, n, parent, name))
/*   0*/        return; 
/* 608*/      GlobalNamespace.Name nameObj = getOrCreateName(name);
/* 609*/      GlobalNamespace.Ref set = new GlobalNamespace.Ref(t, n, nameObj, GlobalNamespace.Ref.Type.SET_FROM_LOCAL, GlobalNamespace.this.currentPreOrderIndex++);
/* 611*/      nameObj.addRef(set);
/* 613*/      if (isNestedAssign(parent)) {
/* 615*/        GlobalNamespace.Ref get = new GlobalNamespace.Ref(t, n, nameObj, GlobalNamespace.Ref.Type.ALIASING_GET, GlobalNamespace.this.currentPreOrderIndex++);
/* 617*/        nameObj.addRef(get);
/* 618*/        GlobalNamespace.Ref.markTwins(set, get);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void handleGet(NodeTraversal t, Node n, Node parent, String name) {
/* 632*/      if (maybeHandlePrototypePrefix(t, n, parent, name))
/*   0*/        return; 
/* 634*/      GlobalNamespace.Ref.Type type = GlobalNamespace.Ref.Type.DIRECT_GET;
/* 635*/      if (parent != null)
/* 636*/        switch (parent.getType()) {
/*   0*/          case 26:
/*   0*/          case 27:
/*   0*/          case 28:
/*   0*/          case 29:
/*   0*/          case 32:
/*   0*/          case 108:
/*   0*/          case 122:
/*   0*/            break;
/*   0*/          case 37:
/* 646*/            type = (n == parent.getFirstChild()) ? GlobalNamespace.Ref.Type.CALL_GET : GlobalNamespace.Ref.Type.ALIASING_GET;
/*   0*/            break;
/*   0*/          case 30:
/* 651*/            type = (n == parent.getFirstChild()) ? GlobalNamespace.Ref.Type.DIRECT_GET : GlobalNamespace.Ref.Type.ALIASING_GET;
/*   0*/            break;
/*   0*/          case 100:
/*   0*/          case 101:
/* 661*/            type = determineGetTypeForHookOrBooleanExpr(t, parent, name);
/*   0*/            break;
/*   0*/          case 98:
/* 664*/            if (n != parent.getFirstChild())
/* 668*/              type = determineGetTypeForHookOrBooleanExpr(t, parent, name); 
/*   0*/            break;
/*   0*/          case 31:
/* 672*/            type = GlobalNamespace.Ref.Type.DELETE_PROP;
/*   0*/            break;
/*   0*/          default:
/* 675*/            type = GlobalNamespace.Ref.Type.ALIASING_GET;
/*   0*/            break;
/*   0*/        }  
/* 680*/      handleGet(t, n, parent, name, type);
/*   0*/    }
/*   0*/    
/*   0*/    GlobalNamespace.Ref.Type determineGetTypeForHookOrBooleanExpr(NodeTraversal t, Node parent, String name) {
/* 697*/      Node prev = parent;
/* 698*/      for (Node anc : (Iterable<Node>)parent.getAncestors()) {
/* 699*/        switch (anc.getType()) {
/*   0*/          case 26:
/*   0*/          case 27:
/*   0*/          case 28:
/*   0*/          case 29:
/*   0*/          case 32:
/*   0*/          case 108:
/*   0*/          case 113:
/*   0*/          case 115:
/*   0*/          case 118:
/*   0*/          case 122:
/*   0*/          case 130:
/* 711*/            return GlobalNamespace.Ref.Type.DIRECT_GET;
/*   0*/          case 98:
/* 713*/            if (anc.getFirstChild() == prev)
/* 714*/              return GlobalNamespace.Ref.Type.DIRECT_GET; 
/*   0*/            break;
/*   0*/          case 86:
/* 718*/            if (!name.equals(anc.getFirstChild().getQualifiedName()))
/* 719*/              return GlobalNamespace.Ref.Type.ALIASING_GET; 
/*   0*/            break;
/*   0*/          case 38:
/* 723*/            if (!name.equals(anc.getString()))
/* 724*/              return GlobalNamespace.Ref.Type.ALIASING_GET; 
/*   0*/            break;
/*   0*/          case 37:
/* 728*/            if (anc.getFirstChild() != prev)
/* 729*/              return GlobalNamespace.Ref.Type.ALIASING_GET; 
/*   0*/            break;
/*   0*/          case 31:
/* 733*/            return GlobalNamespace.Ref.Type.DELETE_PROP;
/*   0*/        } 
/* 735*/        prev = anc;
/*   0*/      } 
/* 737*/      return GlobalNamespace.Ref.Type.ALIASING_GET;
/*   0*/    }
/*   0*/    
/*   0*/    void handleGet(NodeTraversal t, Node n, Node parent, String name, GlobalNamespace.Ref.Type type) {
/* 752*/      GlobalNamespace.Name nameObj = getOrCreateName(name);
/* 755*/      nameObj.addRef(new GlobalNamespace.Ref(t, n, nameObj, type, GlobalNamespace.this.currentPreOrderIndex++));
/*   0*/    }
/*   0*/    
/*   0*/    boolean maybeHandlePrototypePrefix(NodeTraversal t, Node n, Node parent, String name) {
/*   0*/      int numLevelsToRemove;
/*   0*/      String prefix;
/* 777*/      if (name.endsWith(".prototype")) {
/* 778*/        numLevelsToRemove = 1;
/* 779*/        prefix = name.substring(0, name.length() - 10);
/*   0*/      } else {
/* 781*/        int j = name.indexOf(".prototype.");
/* 782*/        if (j == -1)
/* 783*/          return false; 
/* 785*/        prefix = name.substring(0, j);
/* 786*/        numLevelsToRemove = 2;
/* 787*/        j = name.indexOf('.', j + 11);
/* 788*/        while (j >= 0) {
/* 789*/          numLevelsToRemove++;
/* 790*/          j = name.indexOf('.', j + 1);
/*   0*/        } 
/*   0*/      } 
/* 794*/      if (parent != null && NodeUtil.isObjectLitKey(n))
/* 797*/        return true; 
/* 800*/      for (int i = 0; i < numLevelsToRemove; i++) {
/* 801*/        parent = n;
/* 802*/        n = n.getFirstChild();
/*   0*/      } 
/* 805*/      handleGet(t, n, parent, prefix, GlobalNamespace.Ref.Type.PROTOTYPE_GET);
/* 806*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isNestedAssign(Node parent) {
/* 818*/      return (parent.isAssign() && !parent.getParent().isExprResult());
/*   0*/    }
/*   0*/    
/*   0*/    GlobalNamespace.Name getOrCreateName(String name) {
/* 830*/      GlobalNamespace.Name node = GlobalNamespace.this.nameMap.get(name);
/* 831*/      if (node == null) {
/* 832*/        int i = name.lastIndexOf('.');
/* 833*/        if (i >= 0) {
/* 834*/          String parentName = name.substring(0, i);
/* 835*/          GlobalNamespace.Name parent = getOrCreateName(parentName);
/* 836*/          node = parent.addProperty(name.substring(i + 1), GlobalNamespace.this.inExterns);
/*   0*/        } else {
/* 838*/          node = new GlobalNamespace.Name(name, null, GlobalNamespace.this.inExterns);
/* 839*/          GlobalNamespace.this.globalNames.add(node);
/*   0*/        } 
/* 841*/        GlobalNamespace.this.nameMap.put(name, node);
/*   0*/      } 
/* 843*/      return node;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class Name implements StaticSlot<JSType> {
/*   0*/    private final String baseName;
/*   0*/    
/*   0*/    final Name parent;
/*   0*/    
/*   0*/    List<Name> props;
/*   0*/    
/*   0*/    private GlobalNamespace.Ref declaration;
/*   0*/    
/*   0*/    private List<GlobalNamespace.Ref> refs;
/*   0*/    
/*   0*/    Type type;
/*   0*/    
/*   0*/    enum Type {
/* 857*/      OBJECTLIT, FUNCTION, GET, SET, OTHER;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean declaredType = false;
/*   0*/    
/*   0*/    private boolean hasDeclaredTypeDescendant = false;
/*   0*/    
/* 877*/    int globalSets = 0;
/*   0*/    
/* 878*/    int localSets = 0;
/*   0*/    
/* 879*/    int aliasingGets = 0;
/*   0*/    
/* 880*/    int totalGets = 0;
/*   0*/    
/* 881*/    int callGets = 0;
/*   0*/    
/* 882*/    int deleteProps = 0;
/*   0*/    
/*   0*/    final boolean inExterns;
/*   0*/    
/* 885*/    JSDocInfo docInfo = null;
/*   0*/    
/*   0*/    Name(String name, Name parent, boolean inExterns) {
/* 888*/      this.baseName = name;
/* 889*/      this.parent = parent;
/* 890*/      this.type = Type.OTHER;
/* 891*/      this.inExterns = inExterns;
/*   0*/    }
/*   0*/    
/*   0*/    Name addProperty(String name, boolean inExterns) {
/* 895*/      if (this.props == null)
/* 896*/        this.props = new ArrayList<Name>(); 
/* 898*/      Name node = new Name(name, this, inExterns);
/* 899*/      this.props.add(node);
/* 900*/      return node;
/*   0*/    }
/*   0*/    
/*   0*/    String getBaseName() {
/* 904*/      return this.baseName;
/*   0*/    }
/*   0*/    
/*   0*/    public String getName() {
/* 909*/      return getFullName();
/*   0*/    }
/*   0*/    
/*   0*/    String getFullName() {
/* 913*/      return (this.parent == null) ? this.baseName : (this.parent.getFullName() + '.' + this.baseName);
/*   0*/    }
/*   0*/    
/*   0*/    public GlobalNamespace.Ref getDeclaration() {
/* 918*/      return this.declaration;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isTypeInferred() {
/* 923*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType getType() {
/* 928*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    void addRef(GlobalNamespace.Ref ref) {
/* 932*/      addRefInternal(ref);
/* 933*/      switch (ref.type) {
/*   0*/        case SET_FROM_GLOBAL:
/* 935*/          if (this.declaration == null) {
/* 936*/            this.declaration = ref;
/* 937*/            this.docInfo = getDocInfoForDeclaration(ref);
/*   0*/          } 
/* 939*/          this.globalSets++;
/*   0*/          break;
/*   0*/        case SET_FROM_LOCAL:
/* 942*/          this.localSets++;
/*   0*/          break;
/*   0*/        case PROTOTYPE_GET:
/*   0*/        case DIRECT_GET:
/* 946*/          this.totalGets++;
/*   0*/          break;
/*   0*/        case ALIASING_GET:
/* 949*/          this.aliasingGets++;
/* 950*/          this.totalGets++;
/*   0*/          break;
/*   0*/        case CALL_GET:
/* 953*/          this.callGets++;
/* 954*/          this.totalGets++;
/*   0*/          break;
/*   0*/        case DELETE_PROP:
/* 957*/          this.deleteProps++;
/*   0*/          break;
/*   0*/        default:
/* 960*/          throw new IllegalStateException();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void removeRef(GlobalNamespace.Ref ref) {
/* 965*/      if (this.refs != null && this.refs.remove(ref)) {
/* 966*/        if (ref == this.declaration) {
/* 967*/          this.declaration = null;
/* 968*/          if (this.refs != null)
/* 969*/            for (GlobalNamespace.Ref maybeNewDecl : this.refs) {
/* 970*/              if (maybeNewDecl.type == GlobalNamespace.Ref.Type.SET_FROM_GLOBAL) {
/* 971*/                this.declaration = maybeNewDecl;
/*   0*/                break;
/*   0*/              } 
/*   0*/            }  
/*   0*/        } 
/* 978*/        switch (ref.type) {
/*   0*/          case SET_FROM_GLOBAL:
/* 980*/            this.globalSets--;
/*   0*/            break;
/*   0*/          case SET_FROM_LOCAL:
/* 983*/            this.localSets--;
/*   0*/            break;
/*   0*/          case PROTOTYPE_GET:
/*   0*/          case DIRECT_GET:
/* 987*/            this.totalGets--;
/*   0*/            break;
/*   0*/          case ALIASING_GET:
/* 990*/            this.aliasingGets--;
/* 991*/            this.totalGets--;
/*   0*/            break;
/*   0*/          case CALL_GET:
/* 994*/            this.callGets--;
/* 995*/            this.totalGets--;
/*   0*/            break;
/*   0*/          case DELETE_PROP:
/* 998*/            this.deleteProps--;
/*   0*/            break;
/*   0*/          default:
/*1001*/            throw new IllegalStateException();
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    List<GlobalNamespace.Ref> getRefs() {
/*1007*/      return (this.refs == null) ? (List<GlobalNamespace.Ref>)ImmutableList.of() : this.refs;
/*   0*/    }
/*   0*/    
/*   0*/    void addRefInternal(GlobalNamespace.Ref ref) {
/*1011*/      if (this.refs == null)
/*1012*/        this.refs = Lists.newArrayList(); 
/*1014*/      this.refs.add(ref);
/*   0*/    }
/*   0*/    
/*   0*/    boolean canEliminate() {
/*1018*/      if (!canCollapseUnannotatedChildNames() || this.totalGets > 0)
/*1019*/        return false; 
/*1022*/      if (this.props != null)
/*1023*/        for (Name n : this.props) {
/*1024*/          if (!n.canCollapse())
/*1025*/            return false; 
/*   0*/        }  
/*1029*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isSimpleStubDeclaration() {
/*1033*/      if (getRefs().size() == 1) {
/*1034*/        GlobalNamespace.Ref ref = this.refs.get(0);
/*1035*/        if (ref.node.getParent() != null && ref.node.getParent().isExprResult())
/*1037*/          return true; 
/*   0*/      } 
/*1040*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    boolean canCollapse() {
/*1044*/      return (!this.inExterns && !isGetOrSetDefinition() && (this.declaredType || ((this.parent == null || this.parent.canCollapseUnannotatedChildNames()) && (this.globalSets > 0 || this.localSets > 0) && this.deleteProps == 0)));
/*   0*/    }
/*   0*/    
/*   0*/    boolean isGetOrSetDefinition() {
/*1051*/      return (this.type == Type.GET || this.type == Type.SET);
/*   0*/    }
/*   0*/    
/*   0*/    boolean canCollapseUnannotatedChildNames() {
/*1055*/      if (this.type == Type.OTHER || isGetOrSetDefinition() || this.globalSets != 1 || this.localSets != 0 || this.deleteProps != 0)
/*1057*/        return false; 
/*1063*/      Preconditions.checkNotNull(this.declaration);
/*1064*/      if (this.declaration.getTwin() != null)
/*1065*/        return false; 
/*1068*/      if (this.declaredType)
/*1069*/        return true; 
/*1074*/      if (this.parent != null && this.parent.shouldKeepKeys())
/*1075*/        return false; 
/*1079*/      if (this.aliasingGets > 0)
/*1080*/        return false; 
/*1083*/      return (this.parent == null || this.parent.canCollapseUnannotatedChildNames());
/*   0*/    }
/*   0*/    
/*   0*/    boolean shouldKeepKeys() {
/*1088*/      return (this.type == Type.OBJECTLIT && this.aliasingGets > 0);
/*   0*/    }
/*   0*/    
/*   0*/    boolean needsToBeStubbed() {
/*1092*/      return (this.globalSets == 0 && this.localSets > 0);
/*   0*/    }
/*   0*/    
/*   0*/    void setDeclaredType() {
/*1096*/      this.declaredType = true;
/*1097*/      for (Name ancestor = this.parent; ancestor != null; 
/*1098*/        ancestor = ancestor.parent)
/*1099*/        ancestor.hasDeclaredTypeDescendant = true; 
/*   0*/    }
/*   0*/    
/*   0*/    boolean isDeclaredType() {
/*1104*/      return this.declaredType;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isNamespace() {
/*1116*/      return (this.hasDeclaredTypeDescendant && this.type == Type.OBJECTLIT);
/*   0*/    }
/*   0*/    
/*   0*/    boolean isSimpleName() {
/*1124*/      return (this.parent == null);
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/*1128*/      return getFullName() + " (" + this.type + "): globalSets=" + this.globalSets + ", localSets=" + this.localSets + ", totalGets=" + this.totalGets + ", aliasingGets=" + this.aliasingGets + ", callGets=" + this.callGets;
/*   0*/    }
/*   0*/    
/*   0*/    public JSDocInfo getJSDocInfo() {
/*1135*/      return this.docInfo;
/*   0*/    }
/*   0*/    
/*   0*/    private static JSDocInfo getDocInfoForDeclaration(GlobalNamespace.Ref ref) {
/*1142*/      if (ref.node != null) {
/*1143*/        Node refParent = ref.node.getParent();
/*1144*/        switch (refParent.getType()) {
/*   0*/          case 86:
/*   0*/          case 105:
/*1147*/            return refParent.getJSDocInfo();
/*   0*/          case 118:
/*1149*/            return (ref.node == refParent.getFirstChild()) ? refParent.getJSDocInfo() : ref.node.getJSDocInfo();
/*   0*/        } 
/*   0*/      } 
/*1154*/      return null;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class Ref implements StaticReference<JSType> {
/*   0*/    Node node;
/*   0*/    
/*   0*/    final JSModule module;
/*   0*/    
/*   0*/    final StaticSourceFile source;
/*   0*/    
/*   0*/    final GlobalNamespace.Name name;
/*   0*/    
/*   0*/    final Type type;
/*   0*/    
/*   0*/    final Scope scope;
/*   0*/    
/*   0*/    final int preOrderIndex;
/*   0*/    
/*   0*/    enum Type {
/*1166*/      SET_FROM_GLOBAL, SET_FROM_LOCAL, PROTOTYPE_GET, ALIASING_GET, DIRECT_GET, CALL_GET, DELETE_PROP;
/*   0*/    }
/*   0*/    
/*1190*/    private Ref twin = null;
/*   0*/    
/*   0*/    Ref(NodeTraversal t, Node node, GlobalNamespace.Name name, Type type, int index) {
/*1196*/      this.node = node;
/*1197*/      this.name = name;
/*1198*/      this.module = (t.getInput() == null) ? null : t.getInput().getModule();
/*1199*/      this.source = node.getStaticSourceFile();
/*1200*/      this.type = type;
/*1201*/      this.scope = t.getScope();
/*1202*/      this.preOrderIndex = index;
/*   0*/    }
/*   0*/    
/*   0*/    private Ref(Ref original, Type type, int index) {
/*1206*/      this.node = original.node;
/*1207*/      this.name = original.name;
/*1208*/      this.module = original.module;
/*1209*/      this.source = original.source;
/*1210*/      this.type = type;
/*1211*/      this.scope = original.scope;
/*1212*/      this.preOrderIndex = index;
/*   0*/    }
/*   0*/    
/*   0*/    private Ref(Type type, int index) {
/*1216*/      this.type = type;
/*1217*/      this.module = null;
/*1218*/      this.source = null;
/*1219*/      this.scope = null;
/*1220*/      this.name = null;
/*1221*/      this.preOrderIndex = index;
/*   0*/    }
/*   0*/    
/*   0*/    public Node getNode() {
/*1226*/      return this.node;
/*   0*/    }
/*   0*/    
/*   0*/    public StaticSourceFile getSourceFile() {
/*1231*/      return this.source;
/*   0*/    }
/*   0*/    
/*   0*/    public StaticSlot<JSType> getSymbol() {
/*1236*/      return this.name;
/*   0*/    }
/*   0*/    
/*   0*/    JSModule getModule() {
/*1240*/      return this.module;
/*   0*/    }
/*   0*/    
/*   0*/    String getSourceName() {
/*1244*/      return (this.source == null) ? "" : this.source.getName();
/*   0*/    }
/*   0*/    
/*   0*/    Ref getTwin() {
/*1248*/      return this.twin;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isSet() {
/*1252*/      return (this.type == Type.SET_FROM_GLOBAL || this.type == Type.SET_FROM_LOCAL);
/*   0*/    }
/*   0*/    
/*   0*/    static void markTwins(Ref a, Ref b) {
/*1256*/      Preconditions.checkArgument(((a.type == Type.ALIASING_GET || b.type == Type.ALIASING_GET) && (a.type == Type.SET_FROM_GLOBAL || a.type == Type.SET_FROM_LOCAL || b.type == Type.SET_FROM_GLOBAL || b.type == Type.SET_FROM_LOCAL)));
/*1260*/      a.twin = b;
/*1261*/      b.twin = a;
/*   0*/    }
/*   0*/    
/*   0*/    Ref cloneAndReclassify(Type type) {
/*1269*/      return new Ref(this, type, this.preOrderIndex);
/*   0*/    }
/*   0*/    
/*   0*/    static Ref createRefForTesting(Type type) {
/*1273*/      return new Ref(type, -1);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class Tracker implements CompilerPass {
/*   0*/    private final AbstractCompiler compiler;
/*   0*/    
/*   0*/    private final PrintStream stream;
/*   0*/    
/*   0*/    private final Predicate<String> isInterestingSymbol;
/*   0*/    
/*1290*/    private Set<String> previousSymbolsInTree = (Set<String>)ImmutableSet.of();
/*   0*/    
/*   0*/    Tracker(AbstractCompiler compiler, PrintStream stream, Predicate<String> isInterestingSymbol) {
/*1299*/      this.compiler = compiler;
/*1300*/      this.stream = stream;
/*1301*/      this.isInterestingSymbol = isInterestingSymbol;
/*   0*/    }
/*   0*/    
/*   0*/    public void process(Node externs, Node root) {
/*1305*/      GlobalNamespace namespace = new GlobalNamespace(this.compiler, externs, root);
/*1307*/      Set<String> currentSymbols = Sets.newTreeSet();
/*1308*/      for (String name : namespace.getNameIndex().keySet()) {
/*1309*/        if (this.isInterestingSymbol.apply(name))
/*1310*/          currentSymbols.add(name); 
/*   0*/      } 
/*1314*/      String passName = this.compiler.getLastPassName();
/*1315*/      if (passName == null)
/*1316*/        passName = "[Unknown pass]"; 
/*1319*/      for (String sym : currentSymbols) {
/*1320*/        if (!this.previousSymbolsInTree.contains(sym))
/*1321*/          this.stream.println(String.format("%s: Added by %s", new Object[] { sym, passName })); 
/*   0*/      } 
/*1325*/      for (String sym : this.previousSymbolsInTree) {
/*1326*/        if (!currentSymbols.contains(sym))
/*1327*/          this.stream.println(String.format("%s: Removed by %s", new Object[] { sym, passName })); 
/*   0*/      } 
/*1331*/      this.previousSymbolsInTree = currentSymbols;
/*   0*/    }
/*   0*/  }
/*   0*/}
