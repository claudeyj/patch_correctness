/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.ImmutableList;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.InputId;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.EnumType;
/*   0*/import com.google.javascript.rhino.jstype.FunctionParamBuilder;
/*   0*/import com.google.javascript.rhino.jstype.FunctionType;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeNative;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import com.google.javascript.rhino.jstype.ObjectType;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import javax.annotation.Nullable;
/*   0*/
/*   0*/final class TypedScopeCreator implements ScopeCreator {
/*  99*/  static final String DELEGATE_PROXY_SUFFIX = ObjectType.createDelegateSuffix("Proxy");
/*   0*/  
/* 102*/  static final DiagnosticType MALFORMED_TYPEDEF = DiagnosticType.warning("JSC_MALFORMED_TYPEDEF", "Typedef for {0} does not have any type information");
/*   0*/  
/* 107*/  static final DiagnosticType ENUM_INITIALIZER = DiagnosticType.warning("JSC_ENUM_INITIALIZER_NOT_ENUM", "enum initializer must be an object literal or an enum");
/*   0*/  
/* 112*/  static final DiagnosticType CTOR_INITIALIZER = DiagnosticType.warning("JSC_CTOR_INITIALIZER_NOT_CTOR", "Constructor {0} must be initialized at declaration");
/*   0*/  
/* 117*/  static final DiagnosticType IFACE_INITIALIZER = DiagnosticType.warning("JSC_IFACE_INITIALIZER_NOT_IFACE", "Interface {0} must be initialized at declaration");
/*   0*/  
/* 122*/  static final DiagnosticType CONSTRUCTOR_EXPECTED = DiagnosticType.warning("JSC_REFLECT_CONSTRUCTOR_EXPECTED", "Constructor expected as first argument");
/*   0*/  
/* 127*/  static final DiagnosticType UNKNOWN_LENDS = DiagnosticType.warning("JSC_UNKNOWN_LENDS", "Variable {0} not declared before @lends annotation.");
/*   0*/  
/* 132*/  static final DiagnosticType LENDS_ON_NON_OBJECT = DiagnosticType.warning("JSC_LENDS_ON_NON_OBJECT", "May only lend properties to object types. {0} has type {1}.");
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final ErrorReporter typeParsingErrorReporter;
/*   0*/  
/*   0*/  private final TypeValidator validator;
/*   0*/  
/*   0*/  private final CodingConvention codingConvention;
/*   0*/  
/*   0*/  private final JSTypeRegistry typeRegistry;
/*   0*/  
/* 142*/  private final List<ObjectType> delegateProxyPrototypes = Lists.newArrayList();
/*   0*/  
/* 143*/  private final Map<String, String> delegateCallingConventions = Maps.newHashMap();
/*   0*/  
/* 147*/  private final Map<Node, FunctionTypeBuilder.AstFunctionContents> functionAnalysisResults = Maps.newHashMap();
/*   0*/  
/*   0*/  private class DeferredSetType {
/*   0*/    final Node node;
/*   0*/    
/*   0*/    final JSType type;
/*   0*/    
/*   0*/    DeferredSetType(Node node, JSType type) {
/* 159*/      Preconditions.checkNotNull(node);
/* 160*/      Preconditions.checkNotNull(type);
/* 161*/      this.node = node;
/* 162*/      this.type = type;
/* 166*/      node.setJSType(type);
/*   0*/    }
/*   0*/    
/*   0*/    void resolve(Scope scope) {
/* 170*/      this.node.setJSType(this.type.resolve(TypedScopeCreator.this.typeParsingErrorReporter, scope));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  TypedScopeCreator(AbstractCompiler compiler) {
/* 175*/    this(compiler, compiler.getCodingConvention());
/*   0*/  }
/*   0*/  
/*   0*/  TypedScopeCreator(AbstractCompiler compiler, CodingConvention codingConvention) {
/* 180*/    this.compiler = compiler;
/* 181*/    this.validator = compiler.getTypeValidator();
/* 182*/    this.codingConvention = codingConvention;
/* 183*/    this.typeRegistry = compiler.getTypeRegistry();
/* 184*/    this.typeParsingErrorReporter = this.typeRegistry.getErrorReporter();
/*   0*/  }
/*   0*/  
/*   0*/  public Scope createScope(Node root, Scope parent) {
/* 196*/    Scope newScope = null;
/* 197*/    AbstractScopeBuilder scopeBuilder = null;
/* 198*/    if (parent == null) {
/* 200*/      new FirstOrderFunctionAnalyzer(this.compiler, this.functionAnalysisResults).process(root.getFirstChild(), root.getLastChild());
/* 204*/      newScope = createInitialScope(root);
/* 206*/      GlobalScopeBuilder globalScopeBuilder = new GlobalScopeBuilder(newScope);
/* 207*/      scopeBuilder = globalScopeBuilder;
/* 208*/      NodeTraversal.traverse(this.compiler, root, scopeBuilder);
/*   0*/    } else {
/* 210*/      newScope = new Scope(parent, root);
/* 211*/      LocalScopeBuilder localScopeBuilder = new LocalScopeBuilder(newScope);
/* 212*/      scopeBuilder = localScopeBuilder;
/* 213*/      localScopeBuilder.build();
/*   0*/    } 
/* 216*/    scopeBuilder.resolveStubDeclarations();
/* 217*/    scopeBuilder.resolveTypes();
/* 222*/    for (Node functionNode : scopeBuilder.nonExternFunctions) {
/* 223*/      JSType type = functionNode.getJSType();
/* 224*/      if (type != null && type.isFunctionType()) {
/* 225*/        FunctionType fnType = type.toMaybeFunctionType();
/* 226*/        ObjectType fnThisType = fnType.getTypeOfThis();
/* 227*/        if (!fnThisType.isUnknownType()) {
/* 228*/          scopeBuilder.getClass();
/* 228*/          NodeTraversal.traverse(this.compiler, functionNode.getLastChild(), new AbstractScopeBuilder.CollectProperties(fnThisType));
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 234*/    if (parent == null)
/* 235*/      this.codingConvention.defineDelegateProxyPrototypeProperties(this.typeRegistry, newScope, this.delegateProxyPrototypes, this.delegateCallingConventions); 
/* 239*/    return newScope;
/*   0*/  }
/*   0*/  
/*   0*/  void patchGlobalScope(Scope globalScope, Node scriptRoot) {
/* 252*/    Preconditions.checkState(scriptRoot.isScript());
/* 253*/    Preconditions.checkNotNull(globalScope);
/* 254*/    Preconditions.checkState(globalScope.isGlobal());
/* 256*/    String scriptName = NodeUtil.getSourceName(scriptRoot);
/* 257*/    Preconditions.checkNotNull(scriptName);
/* 258*/    for (Node node : (Iterable<Node>)ImmutableList.copyOf(this.functionAnalysisResults.keySet())) {
/* 259*/      if (scriptName.equals(NodeUtil.getSourceName(node)))
/* 260*/        this.functionAnalysisResults.remove(node); 
/*   0*/    } 
/* 264*/    new FirstOrderFunctionAnalyzer(this.compiler, this.functionAnalysisResults).process(null, scriptRoot);
/* 272*/    Iterator<Scope.Var> varIter = globalScope.getVars();
/* 273*/    List<Scope.Var> varsToRemove = Lists.newArrayList();
/* 274*/    while (varIter.hasNext()) {
/* 275*/      Scope.Var oldVar = varIter.next();
/* 276*/      if (scriptName.equals(oldVar.getInputName()))
/* 277*/        varsToRemove.add(oldVar); 
/*   0*/    } 
/* 280*/    for (Scope.Var var : varsToRemove) {
/* 281*/      globalScope.undeclare(var);
/* 282*/      globalScope.getTypeOfThis().removeProperty(var.getName());
/*   0*/    } 
/* 286*/    GlobalScopeBuilder scopeBuilder = new GlobalScopeBuilder(globalScope);
/* 287*/    NodeTraversal.traverse(this.compiler, scriptRoot, scopeBuilder);
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  Scope createInitialScope(Node root) {
/* 297*/    NodeTraversal.traverse(this.compiler, root, new DiscoverEnumsAndTypedefs(this.typeRegistry));
/* 300*/    Scope s = new Scope(root, this.compiler);
/* 301*/    declareNativeFunctionType(s, JSTypeNative.ARRAY_FUNCTION_TYPE);
/* 302*/    declareNativeFunctionType(s, JSTypeNative.BOOLEAN_OBJECT_FUNCTION_TYPE);
/* 303*/    declareNativeFunctionType(s, JSTypeNative.DATE_FUNCTION_TYPE);
/* 304*/    declareNativeFunctionType(s, JSTypeNative.ERROR_FUNCTION_TYPE);
/* 305*/    declareNativeFunctionType(s, JSTypeNative.EVAL_ERROR_FUNCTION_TYPE);
/* 306*/    declareNativeFunctionType(s, JSTypeNative.FUNCTION_FUNCTION_TYPE);
/* 307*/    declareNativeFunctionType(s, JSTypeNative.NUMBER_OBJECT_FUNCTION_TYPE);
/* 308*/    declareNativeFunctionType(s, JSTypeNative.OBJECT_FUNCTION_TYPE);
/* 309*/    declareNativeFunctionType(s, JSTypeNative.RANGE_ERROR_FUNCTION_TYPE);
/* 310*/    declareNativeFunctionType(s, JSTypeNative.REFERENCE_ERROR_FUNCTION_TYPE);
/* 311*/    declareNativeFunctionType(s, JSTypeNative.REGEXP_FUNCTION_TYPE);
/* 312*/    declareNativeFunctionType(s, JSTypeNative.STRING_OBJECT_FUNCTION_TYPE);
/* 313*/    declareNativeFunctionType(s, JSTypeNative.SYNTAX_ERROR_FUNCTION_TYPE);
/* 314*/    declareNativeFunctionType(s, JSTypeNative.TYPE_ERROR_FUNCTION_TYPE);
/* 315*/    declareNativeFunctionType(s, JSTypeNative.URI_ERROR_FUNCTION_TYPE);
/* 316*/    declareNativeValueType(s, "undefined", JSTypeNative.VOID_TYPE);
/* 321*/    declareNativeValueType(s, "ActiveXObject", JSTypeNative.NO_OBJECT_TYPE);
/* 323*/    return s;
/*   0*/  }
/*   0*/  
/*   0*/  private void declareNativeFunctionType(Scope scope, JSTypeNative tId) {
/* 327*/    FunctionType t = this.typeRegistry.getNativeFunctionType(tId);
/* 328*/    declareNativeType(scope, t.getInstanceType().getReferenceName(), t);
/* 329*/    declareNativeType(scope, t.getPrototype().getReferenceName(), t.getPrototype());
/*   0*/  }
/*   0*/  
/*   0*/  private void declareNativeValueType(Scope scope, String name, JSTypeNative tId) {
/* 335*/    declareNativeType(scope, name, this.typeRegistry.getNativeType(tId));
/*   0*/  }
/*   0*/  
/*   0*/  private void declareNativeType(Scope scope, String name, JSType t) {
/* 339*/    scope.declare(name, null, t, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static class DiscoverEnumsAndTypedefs extends NodeTraversal.AbstractShallowStatementCallback {
/*   0*/    private final JSTypeRegistry registry;
/*   0*/    
/*   0*/    DiscoverEnumsAndTypedefs(JSTypeRegistry registry) {
/* 347*/      this.registry = registry;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node node, Node parent) {
/*   0*/      Node child, firstChild;
/* 352*/      Node nameNode = null;
/* 353*/      switch (node.getType()) {
/*   0*/        case 118:
/* 355*/          child = node.getFirstChild();
/* 356*/          for (; child != null; child = child.getNext())
/* 357*/            identifyNameNode(child, child.getFirstChild(), NodeUtil.getBestJSDocInfo(child)); 
/*   0*/          break;
/*   0*/        case 130:
/* 363*/          firstChild = node.getFirstChild();
/* 364*/          if (firstChild.isAssign()) {
/* 365*/            identifyNameNode(firstChild.getFirstChild(), firstChild.getLastChild(), firstChild.getJSDocInfo());
/*   0*/            break;
/*   0*/          } 
/* 369*/          identifyNameNode(firstChild, null, firstChild.getJSDocInfo());
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void identifyNameNode(Node nameNode, Node valueNode, JSDocInfo info) {
/* 378*/      if (nameNode.isQualifiedName() && 
/* 379*/        info != null)
/* 380*/        if (info.hasEnumParameterType()) {
/* 381*/          this.registry.identifyNonNullableName(nameNode.getQualifiedName());
/* 382*/        } else if (info.hasTypedefType()) {
/* 383*/          this.registry.identifyNonNullableName(nameNode.getQualifiedName());
/*   0*/        }  
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative nativeType) {
/* 391*/    return this.typeRegistry.getNativeType(nativeType);
/*   0*/  }
/*   0*/  
/*   0*/  private abstract class AbstractScopeBuilder implements NodeTraversal.Callback {
/*   0*/    final Scope scope;
/*   0*/    
/* 402*/    private final List<TypedScopeCreator.DeferredSetType> deferredSetTypes = Lists.newArrayList();
/*   0*/    
/* 408*/    private final List<Node> nonExternFunctions = Lists.newArrayList();
/*   0*/    
/* 420*/    private List<Node> lentObjectLiterals = null;
/*   0*/    
/* 428*/    private final List<TypedScopeCreator.StubDeclaration> stubDeclarations = Lists.newArrayList();
/*   0*/    
/* 434*/    private String sourceName = null;
/*   0*/    
/*   0*/    private InputId inputId;
/*   0*/    
/*   0*/    private AbstractScopeBuilder(Scope scope) {
/* 442*/      this.scope = scope;
/*   0*/    }
/*   0*/    
/*   0*/    void setDeferredType(Node node, JSType type) {
/* 446*/      this.deferredSetTypes.add(new TypedScopeCreator.DeferredSetType(node, type));
/*   0*/    }
/*   0*/    
/*   0*/    void resolveTypes() {
/* 451*/      for (TypedScopeCreator.DeferredSetType deferred : this.deferredSetTypes)
/* 452*/        deferred.resolve(this.scope); 
/* 456*/      Iterator<Scope.Var> vars = this.scope.getVars();
/* 457*/      while (vars.hasNext())
/* 458*/        ((Scope.Var)vars.next()).resolveType(TypedScopeCreator.this.typeParsingErrorReporter); 
/* 463*/      TypedScopeCreator.this.typeRegistry.resolveTypesInScope(this.scope);
/*   0*/    }
/*   0*/    
/*   0*/    public final boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/* 469*/      this.inputId = t.getInputId();
/* 470*/      if (n.isFunction() || n.isScript()) {
/* 472*/        Preconditions.checkNotNull(this.inputId);
/* 473*/        this.sourceName = NodeUtil.getSourceName(n);
/*   0*/      } 
/* 478*/      boolean descend = (parent == null || !parent.isFunction() || n == parent.getFirstChild() || parent == this.scope.getRootNode());
/* 481*/      if (descend)
/* 484*/        if (NodeUtil.isStatementParent(n)) {
/* 485*/          Node child = n.getFirstChild();
/* 486*/          for (; child != null; 
/* 487*/            child = child.getNext()) {
/* 488*/            if (NodeUtil.isHoistedFunctionDeclaration(child))
/* 489*/              defineFunctionLiteral(child, n); 
/*   0*/          } 
/*   0*/        }  
/* 495*/      return descend;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/      Node firstChild;
/* 500*/      this.inputId = t.getInputId();
/* 501*/      attachLiteralTypes(t, n);
/* 503*/      switch (n.getType()) {
/*   0*/        case 37:
/* 505*/          checkForClassDefiningCalls(t, n, parent);
/* 506*/          checkForCallingConventionDefiningCalls(n, TypedScopeCreator.this.delegateCallingConventions);
/*   0*/          break;
/*   0*/        case 105:
/* 510*/          if (t.getInput() == null || !t.getInput().isExtern())
/* 511*/            this.nonExternFunctions.add(n); 
/* 515*/          if (!NodeUtil.isHoistedFunctionDeclaration(n))
/* 516*/            defineFunctionLiteral(n, parent); 
/*   0*/          break;
/*   0*/        case 86:
/* 522*/          firstChild = n.getFirstChild();
/* 523*/          if (firstChild.isGetProp() && firstChild.isQualifiedName())
/* 525*/            maybeDeclareQualifiedName(t, n.getJSDocInfo(), firstChild, n, firstChild.getNext()); 
/*   0*/          break;
/*   0*/        case 120:
/* 531*/          defineCatch(n, parent);
/*   0*/          break;
/*   0*/        case 118:
/* 535*/          defineVar(n, parent);
/*   0*/          break;
/*   0*/        case 33:
/* 540*/          if (parent.isExprResult() && n.isQualifiedName())
/* 542*/            maybeDeclareQualifiedName(t, n.getJSDocInfo(), n, parent, null); 
/*   0*/          break;
/*   0*/      } 
/* 548*/      if (n.getParent() != null && NodeUtil.isStatement(n) && this.lentObjectLiterals != null) {
/* 550*/        for (Node objLit : this.lentObjectLiterals)
/* 551*/          defineObjectLiteral(objLit); 
/* 553*/        this.lentObjectLiterals.clear();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void attachLiteralTypes(NodeTraversal t, Node n) {
/*   0*/      JSDocInfo info;
/* 558*/      switch (n.getType()) {
/*   0*/        case 41:
/* 560*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.NULL_TYPE));
/*   0*/          break;
/*   0*/        case 122:
/* 564*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.VOID_TYPE));
/*   0*/          break;
/*   0*/        case 40:
/* 568*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.STRING_TYPE));
/*   0*/          break;
/*   0*/        case 39:
/* 572*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.NUMBER_TYPE));
/*   0*/          break;
/*   0*/        case 43:
/*   0*/        case 44:
/* 577*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.BOOLEAN_TYPE));
/*   0*/          break;
/*   0*/        case 47:
/* 581*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.REGEXP_TYPE));
/*   0*/          break;
/*   0*/        case 64:
/* 585*/          info = n.getJSDocInfo();
/* 586*/          if (info != null)
/* 586*/            if (this.sourceName != null) {
/* 588*/              if (this.lentObjectLiterals == null)
/* 589*/                this.lentObjectLiterals = Lists.newArrayList(); 
/* 591*/              this.lentObjectLiterals.add(n);
/*   0*/              break;
/*   0*/            }  
/* 593*/          defineObjectLiteral(n);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void defineObjectLiteral(Node objectLit) {
/* 604*/      JSType type = null;
/* 605*/      JSDocInfo info = objectLit.getJSDocInfo();
/* 606*/      if (info != null && info.getLendsName() != null) {
/* 608*/        String lendsName = info.getLendsName();
/* 609*/        Scope.Var lendsVar = this.scope.getVar(lendsName);
/* 610*/        if (lendsVar == null) {
/* 611*/          TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, objectLit, TypedScopeCreator.UNKNOWN_LENDS, new String[] { lendsName }));
/*   0*/        } else {
/* 614*/          type = lendsVar.getType();
/* 615*/          if (type == null)
/* 616*/            type = TypedScopeCreator.this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/* 618*/          if (!type.isSubtype(TypedScopeCreator.this.typeRegistry.getNativeType(JSTypeNative.OBJECT_TYPE))) {
/* 619*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, objectLit, TypedScopeCreator.LENDS_ON_NON_OBJECT, new String[] { lendsName, type.toString() }));
/* 622*/            type = null;
/*   0*/          } else {
/* 624*/            objectLit.setJSType(type);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 629*/      info = NodeUtil.getBestJSDocInfo(objectLit);
/* 630*/      Node lValue = NodeUtil.getBestLValue(objectLit);
/* 631*/      String lValueName = NodeUtil.getBestLValueName(lValue);
/*   0*/      boolean createdEnumType = false;
/* 633*/      if (info != null && info.hasEnumParameterType()) {
/* 634*/        type = createEnumTypeFromNodes(objectLit, lValueName, info, lValue);
/* 635*/        createdEnumType = true;
/*   0*/      } 
/* 638*/      if (type == null)
/* 639*/        type = TypedScopeCreator.this.typeRegistry.createAnonymousObjectType(); 
/* 642*/      setDeferredType(objectLit, type);
/* 645*/      processObjectLitProperties(objectLit, ObjectType.cast(objectLit.getJSType()), !createdEnumType);
/*   0*/    }
/*   0*/    
/*   0*/    void processObjectLitProperties(Node objLit, ObjectType objLitType, boolean declareOnOwner) {
/* 660*/      for (Node keyNode = objLit.getFirstChild(); keyNode != null; 
/* 661*/        keyNode = keyNode.getNext()) {
/* 662*/        Node value = keyNode.getFirstChild();
/* 663*/        String memberName = NodeUtil.getObjectLitKeyName(keyNode);
/* 664*/        JSDocInfo info = keyNode.getJSDocInfo();
/* 665*/        JSType valueType = getDeclaredType(keyNode.getSourceFileName(), info, keyNode, value);
/* 667*/        JSType keyType = objLitType.isEnumType() ? objLitType.toMaybeEnumType().getElementsType() : NodeUtil.getObjectLitKeyTypeFromValueType(keyNode, valueType);
/* 673*/        String qualifiedName = NodeUtil.getBestLValueName(keyNode);
/* 674*/        if (qualifiedName != null) {
/* 675*/          boolean inferred = (keyType == null);
/* 676*/          defineSlot(keyNode, objLit, qualifiedName, keyType, inferred);
/* 677*/        } else if (keyType != null) {
/* 678*/          setDeferredType(keyNode, keyType);
/*   0*/        } 
/* 681*/        if (keyType != null && objLitType != null && declareOnOwner) {
/* 683*/          boolean isExtern = keyNode.isFromExterns();
/* 684*/          objLitType.defineDeclaredProperty(memberName, keyType, keyNode);
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private JSType getDeclaredTypeInAnnotation(String sourceName, Node node, JSDocInfo info) {
/* 697*/      JSType jsType = null;
/* 698*/      Node objNode = node.isGetProp() ? node.getFirstChild() : (NodeUtil.isObjectLitKey(node, node.getParent()) ? node.getParent() : null);
/* 702*/      if (info != null)
/* 703*/        if (info.hasType()) {
/* 704*/          jsType = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 705*/        } else if (FunctionTypeBuilder.isFunctionTypeDeclaration(info)) {
/* 706*/          String fnName = node.getQualifiedName();
/* 707*/          jsType = createFunctionTypeFromNodes(null, fnName, info, node);
/*   0*/        }  
/* 711*/      return jsType;
/*   0*/    }
/*   0*/    
/*   0*/    void assertDefinitionNode(Node n, int type) {
/* 719*/      Preconditions.checkState((this.sourceName != null));
/* 720*/      Preconditions.checkState((n.getType() == type));
/*   0*/    }
/*   0*/    
/*   0*/    void defineCatch(Node n, Node parent) {
/* 727*/      assertDefinitionNode(n, 120);
/* 728*/      Node catchName = n.getFirstChild();
/* 729*/      defineSlot(catchName, n, null);
/*   0*/    }
/*   0*/    
/*   0*/    void defineVar(Node n, Node parent) {
/* 736*/      assertDefinitionNode(n, 118);
/* 737*/      JSDocInfo info = n.getJSDocInfo();
/* 738*/      if (n.hasMoreThanOneChild()) {
/* 739*/        if (info != null)
/* 741*/          TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, TypeCheck.MULTIPLE_VAR_DEF, new String[0])); 
/* 743*/        for (Node name : n.children())
/* 744*/          defineName(name, n, parent, name.getJSDocInfo()); 
/*   0*/      } else {
/* 747*/        Node name = n.getFirstChild();
/* 748*/        defineName(name, n, parent, (info != null) ? info : name.getJSDocInfo());
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void defineFunctionLiteral(Node n, Node parent) {
/* 757*/      assertDefinitionNode(n, 105);
/* 761*/      Node lValue = NodeUtil.getBestLValue(n);
/* 762*/      JSDocInfo info = NodeUtil.getBestJSDocInfo(n);
/* 763*/      String functionName = NodeUtil.getBestLValueName(lValue);
/* 764*/      FunctionType functionType = createFunctionTypeFromNodes(n, functionName, info, lValue);
/* 768*/      setDeferredType(n, functionType);
/* 773*/      if (NodeUtil.isFunctionDeclaration(n))
/* 774*/        defineSlot(n.getFirstChild(), n, functionType); 
/*   0*/    }
/*   0*/    
/*   0*/    private void defineName(Node name, Node var, Node parent, JSDocInfo info) {
/* 788*/      Node value = name.getFirstChild();
/* 791*/      JSType type = getDeclaredType(this.sourceName, info, name, value);
/* 792*/      if (type == null)
/* 794*/        type = name.isFromExterns() ? TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE) : null; 
/* 797*/      defineSlot(name, var, type);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean shouldUseFunctionLiteralType(FunctionType type, JSDocInfo info, Node lValue) {
/* 809*/      if (info != null)
/* 810*/        return true; 
/* 812*/      if (lValue != null && NodeUtil.isObjectLitKey(lValue, lValue.getParent()))
/* 814*/        return false; 
/* 816*/      return (this.scope.isGlobal() || !type.isReturnTypeInferred());
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType createFunctionTypeFromNodes(@Nullable Node rValue, @Nullable String name, @Nullable JSDocInfo info, @Nullable Node lvalueNode) {
/* 847*/      FunctionType functionType = null;
/* 850*/      if (rValue != null && rValue.isQualifiedName() && this.scope.isGlobal()) {
/* 851*/        Scope.Var var = this.scope.getVar(rValue.getQualifiedName());
/* 852*/        if (var != null && var.getType() != null && var.getType().isFunctionType()) {
/* 854*/          FunctionType aliasedType = var.getType().toMaybeFunctionType();
/* 855*/          if ((aliasedType.isConstructor() || aliasedType.isInterface()) && !aliasedType.isNativeObjectType()) {
/* 857*/            functionType = aliasedType;
/* 859*/            if (name != null && this.scope.isGlobal())
/* 860*/              TypedScopeCreator.this.typeRegistry.declareType(name, functionType.getInstanceType()); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 866*/      if (functionType == null) {
/* 867*/        Node errorRoot = (rValue == null) ? lvalueNode : rValue;
/* 868*/        boolean isFnLiteral = (rValue != null && rValue.isFunction());
/* 870*/        Node fnRoot = isFnLiteral ? rValue : null;
/* 871*/        Node parametersNode = isFnLiteral ? rValue.getFirstChild().getNext() : null;
/* 873*/        Node fnBlock = isFnLiteral ? parametersNode.getNext() : null;
/* 875*/        if (info != null && info.hasType()) {
/* 876*/          JSType type = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 879*/          type = type.restrictByNotNullOrUndefined();
/* 880*/          if (type.isFunctionType()) {
/* 881*/            functionType = type.toMaybeFunctionType();
/* 882*/            functionType.setJSDocInfo(info);
/*   0*/          } 
/*   0*/        } 
/* 886*/        if (functionType == null) {
/* 888*/          Node ownerNode = NodeUtil.getBestLValueOwner(lvalueNode);
/* 889*/          String ownerName = NodeUtil.getBestLValueName(ownerNode);
/* 890*/          Scope.Var ownerVar = null;
/* 891*/          String propName = null;
/* 892*/          ObjectType ownerType = null;
/* 893*/          if (ownerName != null) {
/* 894*/            ownerVar = this.scope.getVar(ownerName);
/* 895*/            if (ownerVar != null)
/* 896*/              ownerType = ObjectType.cast(ownerVar.getType()); 
/* 898*/            if (name != null)
/* 899*/              propName = name.substring(ownerName.length() + 1); 
/*   0*/          } 
/* 903*/          FunctionType overriddenPropType = null;
/* 904*/          if (ownerType != null && propName != null)
/* 905*/            overriddenPropType = findOverriddenFunction(ownerType, propName); 
/* 909*/          FunctionTypeBuilder builder = new FunctionTypeBuilder(name, TypedScopeCreator.this.compiler, errorRoot, this.sourceName, this.scope).setContents(TypedScopeCreator.this.getFunctionAnalysisResults(fnRoot)).inferFromOverriddenFunction(overriddenPropType, parametersNode).inferTemplateTypeName(info).inferReturnType(info).inferInheritance(info);
/*   0*/          boolean searchedForThisType = false;
/* 921*/          if (ownerType != null && ownerType.isFunctionPrototypeType()) {
/* 922*/            builder.inferThisType(info, ownerType.getOwnerFunction().getInstanceType());
/* 924*/            searchedForThisType = true;
/* 925*/          } else if (ownerNode != null && ownerNode.isThis()) {
/* 930*/            JSType injectedThisType = ownerNode.getJSType();
/* 931*/            builder.inferThisType(info, (injectedThisType == null) ? this.scope.getTypeOfThis() : injectedThisType);
/* 935*/            searchedForThisType = true;
/*   0*/          } 
/* 938*/          if (!searchedForThisType)
/* 939*/            builder.inferThisType(info); 
/* 942*/          functionType = builder.inferParameterTypes(parametersNode, info).buildAndRegister();
/*   0*/        } 
/*   0*/      } 
/* 949*/      return functionType;
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType findOverriddenFunction(ObjectType ownerType, String propName) {
/* 959*/      JSType propType = ownerType.getPropertyType(propName);
/* 960*/      if (propType != null && propType.isFunctionType())
/* 961*/        return propType.toMaybeFunctionType(); 
/* 966*/      for (ObjectType iface : ownerType.getCtorImplementedInterfaces()) {
/* 967*/        propType = iface.getPropertyType(propName);
/* 968*/        if (propType != null && propType.isFunctionType())
/* 969*/          return propType.toMaybeFunctionType(); 
/*   0*/      } 
/* 974*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    private EnumType createEnumTypeFromNodes(Node rValue, String name, JSDocInfo info, Node lValueNode) {
/* 996*/      Preconditions.checkNotNull(info);
/* 997*/      Preconditions.checkState(info.hasEnumParameterType());
/* 999*/      EnumType enumType = null;
/*1000*/      if (rValue != null && rValue.isQualifiedName()) {
/*1002*/        Scope.Var var = this.scope.getVar(rValue.getQualifiedName());
/*1003*/        if (var != null && var.getType() instanceof EnumType)
/*1004*/          enumType = (EnumType)var.getType(); 
/*   0*/      } 
/*1008*/      if (enumType == null) {
/*1009*/        JSType elementsType = info.getEnumParameterType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/*1011*/        enumType = TypedScopeCreator.this.typeRegistry.createEnumType(name, rValue, elementsType);
/*1013*/        if (rValue != null && rValue.isObjectLit()) {
/*1015*/          Node key = rValue.getFirstChild();
/*1016*/          while (key != null) {
/*1017*/            String keyName = NodeUtil.getStringValue(key);
/*1018*/            if (keyName == null) {
/*1020*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_NOT_CONSTANT, new String[] { keyName }));
/*1022*/            } else if (!TypedScopeCreator.this.codingConvention.isValidEnumKey(keyName)) {
/*1023*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_NOT_CONSTANT, new String[] { keyName }));
/*   0*/            } else {
/*1026*/              enumType.defineElement(keyName, key);
/*   0*/            } 
/*1028*/            key = key.getNext();
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1033*/      if (name != null && this.scope.isGlobal())
/*1034*/        TypedScopeCreator.this.typeRegistry.declareType(name, enumType.getElementsType()); 
/*1037*/      return enumType;
/*   0*/    }
/*   0*/    
/*   0*/    private void defineSlot(Node name, Node parent, JSType type) {
/*1049*/      defineSlot(name, parent, type, (type == null));
/*   0*/    }
/*   0*/    
/*   0*/    void defineSlot(Node n, Node parent, JSType type, boolean inferred) {
/*1065*/      Preconditions.checkArgument((inferred || type != null));
/*1069*/      if (n.isName()) {
/*1070*/        Preconditions.checkArgument((parent.isFunction() || parent.isVar() || parent.isParamList() || parent.isCatch()));
/*   0*/      } else {
/*1076*/        Preconditions.checkArgument((n.isGetProp() && (parent.isAssign() || parent.isExprResult())));
/*   0*/      } 
/*1081*/      defineSlot(n, parent, n.getQualifiedName(), type, inferred);
/*   0*/    }
/*   0*/    
/*   0*/    void defineSlot(Node n, Node parent, String variableName, JSType type, boolean inferred) {
/*1097*/      Preconditions.checkArgument(!variableName.isEmpty());
/*1099*/      boolean isGlobalVar = (n.isName() && this.scope.isGlobal());
/*1100*/      boolean shouldDeclareOnGlobalThis = (isGlobalVar && (parent.isVar() || parent.isFunction()));
/*1108*/      Scope scopeToDeclareIn = this.scope;
/*1109*/      if (n.isGetProp() && !this.scope.isGlobal() && isQnameRootedInGlobalScope(n)) {
/*1111*/        Scope globalScope = this.scope.getGlobalScope();
/*1115*/        if (!globalScope.isDeclared(variableName, false))
/*1116*/          scopeToDeclareIn = this.scope.getGlobalScope(); 
/*   0*/      } 
/*1122*/      boolean isExtern = n.isFromExterns();
/*1123*/      Scope.Var newVar = null;
/*1126*/      CompilerInput input = TypedScopeCreator.this.compiler.getInput(this.inputId);
/*1127*/      if (scopeToDeclareIn.isDeclared(variableName, false)) {
/*1128*/        Scope.Var oldVar = scopeToDeclareIn.getVar(variableName);
/*1129*/        newVar = TypedScopeCreator.this.validator.expectUndeclaredVariable(this.sourceName, input, n, parent, oldVar, variableName, type);
/*   0*/      } else {
/*1132*/        if (!inferred)
/*1133*/          setDeferredType(n, type); 
/*1136*/        newVar = scopeToDeclareIn.declare(variableName, n, type, input, inferred);
/*1139*/        if (type instanceof EnumType) {
/*1140*/          Node initialValue = newVar.getInitialValue();
/*1141*/          boolean isValidValue = (initialValue != null && (initialValue.isObjectLit() || initialValue.isQualifiedName()));
/*1144*/          if (!isValidValue)
/*1145*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, TypedScopeCreator.ENUM_INITIALIZER, new String[0])); 
/*   0*/        } 
/*   0*/      } 
/*1151*/      FunctionType fnType = JSType.toMaybeFunctionType(type);
/*1152*/      if (fnType != null && !type.isEmptyType())
/*1155*/        if ((fnType.isConstructor() || fnType.isInterface()) && !fnType.equals(TypedScopeCreator.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE))) {
/*1158*/          FunctionType superClassCtor = fnType.getSuperClassConstructor();
/*1159*/          ObjectType.Property prototypeSlot = fnType.getSlot("prototype");
/*1169*/          prototypeSlot.setNode(n);
/*1171*/          String prototypeName = variableName + ".prototype";
/*1176*/          Scope.Var prototypeVar = scopeToDeclareIn.getVar(prototypeName);
/*1177*/          if (prototypeVar != null && prototypeVar.scope == scopeToDeclareIn)
/*1178*/            scopeToDeclareIn.undeclare(prototypeVar); 
/*1181*/          scopeToDeclareIn.declare(prototypeName, n, prototypeSlot.getType(), input, (superClassCtor == null || superClassCtor.getInstanceType().equals(TypedScopeCreator.this.getNativeType(JSTypeNative.OBJECT_TYPE))));
/*1190*/          if (newVar.getInitialValue() == null && !isExtern && variableName.equals(fnType.getInstanceType().getReferenceName()))
/*1200*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, fnType.isConstructor() ? TypedScopeCreator.CTOR_INITIALIZER : TypedScopeCreator.IFACE_INITIALIZER, new String[] { variableName })); 
/*   0*/        }  
/*1209*/      if (shouldDeclareOnGlobalThis) {
/*1210*/        ObjectType globalThis = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.GLOBAL_THIS);
/*1212*/        if (inferred) {
/*1213*/          globalThis.defineInferredProperty(variableName, (type == null) ? TypedScopeCreator.this.getNativeType(JSTypeNative.NO_TYPE) : type, n);
/*   0*/        } else {
/*1219*/          globalThis.defineDeclaredProperty(variableName, type, n);
/*   0*/        } 
/*   0*/      } 
/*1223*/      if (isGlobalVar && "Window".equals(variableName) && type != null && type.isFunctionType() && type.isConstructor()) {
/*1227*/        FunctionType globalThisCtor = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.GLOBAL_THIS).getConstructor();
/*1229*/        globalThisCtor.getInstanceType().clearCachedValues();
/*1230*/        globalThisCtor.getPrototype().clearCachedValues();
/*1231*/        globalThisCtor.setPrototypeBasedOn(type.toMaybeFunctionType().getInstanceType());
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isQnameRootedInGlobalScope(Node n) {
/*1240*/      Scope scope = getQnameRootScope(n);
/*1241*/      return (scope != null && scope.isGlobal());
/*   0*/    }
/*   0*/    
/*   0*/    private Scope getQnameRootScope(Node n) {
/*1248*/      Node root = NodeUtil.getRootOfQualifiedName(n);
/*1249*/      if (root.isName()) {
/*1250*/        Scope.Var var = this.scope.getVar(root.getString());
/*1251*/        if (var != null)
/*1252*/          return var.getScope(); 
/*   0*/      } 
/*1255*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    private JSType getDeclaredType(String sourceName, JSDocInfo info, Node lValue, @Nullable Node rValue) {
/*1269*/      if (info != null && info.hasType())
/*1270*/        return getDeclaredTypeInAnnotation(sourceName, lValue, info); 
/*1271*/      if (rValue != null && rValue.isFunction() && shouldUseFunctionLiteralType(JSType.toMaybeFunctionType(rValue.getJSType()), info, lValue))
/*1274*/        return rValue.getJSType(); 
/*1275*/      if (info != null) {
/*1276*/        if (info.hasEnumParameterType()) {
/*1277*/          if (rValue != null && rValue.isObjectLit())
/*1278*/            return rValue.getJSType(); 
/*1280*/          return createEnumTypeFromNodes(rValue, lValue.getQualifiedName(), info, lValue);
/*   0*/        } 
/*1283*/        if (info.isConstructor() || info.isInterface())
/*1284*/          return createFunctionTypeFromNodes(rValue, lValue.getQualifiedName(), info, lValue); 
/*1288*/        if (info.isConstant()) {
/*1289*/          JSType knownType = null;
/*1290*/          if (rValue != null) {
/*1291*/            if (rValue.getJSType() != null && !rValue.getJSType().isUnknownType())
/*1295*/              return rValue.getJSType(); 
/*1296*/            if (rValue.isOr()) {
/*1301*/              Node firstClause = rValue.getFirstChild();
/*1302*/              Node secondClause = firstClause.getNext();
/*1303*/              boolean namesMatch = (firstClause.isName() && lValue.isName() && firstClause.getString().equals(lValue.getString()));
/*1306*/              if (namesMatch && secondClause.getJSType() != null && !secondClause.getJSType().isUnknownType())
/*1308*/                return secondClause.getJSType(); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1316*/      return getDeclaredTypeInAnnotation(sourceName, lValue, info);
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType getFunctionType(@Nullable Scope.Var v) {
/*1320*/      JSType t = (v == null) ? null : v.getType();
/*1321*/      ObjectType o = (t == null) ? null : t.dereference();
/*1322*/      return JSType.toMaybeFunctionType(o);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForCallingConventionDefiningCalls(Node n, Map<String, String> delegateCallingConventions) {
/*1330*/      TypedScopeCreator.this.codingConvention.checkForCallingConventionDefiningCalls(n, delegateCallingConventions);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForClassDefiningCalls(NodeTraversal t, Node n, Node parent) {
/*1341*/      CodingConvention.SubclassRelationship relationship = TypedScopeCreator.this.codingConvention.getClassesDefinedByCall(n);
/*1343*/      if (relationship != null) {
/*1344*/        FunctionType superCtor = getFunctionType(this.scope.getVar(relationship.superclassName));
/*1346*/        FunctionType subCtor = getFunctionType(this.scope.getVar(relationship.subclassName));
/*1348*/        if (superCtor != null && superCtor.isConstructor() && subCtor != null && subCtor.isConstructor()) {
/*1350*/          ObjectType superClass = superCtor.getInstanceType();
/*1351*/          ObjectType subClass = subCtor.getInstanceType();
/*1356*/          superCtor = superClass.getConstructor();
/*1357*/          subCtor = subClass.getConstructor();
/*1359*/          if (relationship.type == CodingConvention.SubclassType.INHERITS && !superClass.isEmptyType() && !subClass.isEmptyType())
/*1361*/            TypedScopeCreator.this.validator.expectSuperType(t, n, superClass, subClass); 
/*1364*/          if (superCtor != null && subCtor != null)
/*1365*/            TypedScopeCreator.this.codingConvention.applySubclassRelationship(superCtor, subCtor, relationship.type); 
/*   0*/        } 
/*   0*/      } 
/*1371*/      String singletonGetterClassName = TypedScopeCreator.this.codingConvention.getSingletonGetterClassName(n);
/*1373*/      if (singletonGetterClassName != null) {
/*1374*/        ObjectType objectType = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(singletonGetterClassName));
/*1376*/        if (objectType != null) {
/*1377*/          FunctionType functionType = objectType.getConstructor();
/*1379*/          if (functionType != null) {
/*1380*/            FunctionType getterType = TypedScopeCreator.this.typeRegistry.createFunctionType(objectType, new JSType[0]);
/*1382*/            TypedScopeCreator.this.codingConvention.applySingletonGetter(functionType, getterType, objectType);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1388*/      CodingConvention.DelegateRelationship delegateRelationship = TypedScopeCreator.this.codingConvention.getDelegateRelationship(n);
/*1390*/      if (delegateRelationship != null)
/*1391*/        applyDelegateRelationship(delegateRelationship); 
/*1394*/      CodingConvention.ObjectLiteralCast objectLiteralCast = TypedScopeCreator.this.codingConvention.getObjectLiteralCast(n);
/*1396*/      if (objectLiteralCast != null)
/*1397*/        if (objectLiteralCast.diagnosticType == null) {
/*1398*/          ObjectType type = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(objectLiteralCast.typeName));
/*1400*/          if (type != null && type.getConstructor() != null) {
/*1401*/            setDeferredType(objectLiteralCast.objectNode, type);
/*   0*/          } else {
/*1403*/            TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), n, TypedScopeCreator.CONSTRUCTOR_EXPECTED, new String[0]));
/*   0*/          } 
/*   0*/        } else {
/*1407*/          TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), n, objectLiteralCast.diagnosticType, new String[0]));
/*   0*/        }  
/*   0*/    }
/*   0*/    
/*   0*/    private void applyDelegateRelationship(CodingConvention.DelegateRelationship delegateRelationship) {
/*1418*/      ObjectType delegatorObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(delegateRelationship.delegator));
/*1420*/      ObjectType delegateBaseObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(delegateRelationship.delegateBase));
/*1422*/      ObjectType delegateSuperObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(TypedScopeCreator.this.codingConvention.getDelegateSuperclassName()));
/*1424*/      if (delegatorObject != null && delegateBaseObject != null && delegateSuperObject != null) {
/*1427*/        FunctionType delegatorCtor = delegatorObject.getConstructor();
/*1428*/        FunctionType delegateBaseCtor = delegateBaseObject.getConstructor();
/*1429*/        FunctionType delegateSuperCtor = delegateSuperObject.getConstructor();
/*1431*/        if (delegatorCtor != null && delegateBaseCtor != null && delegateSuperCtor != null) {
/*1433*/          FunctionParamBuilder functionParamBuilder = new FunctionParamBuilder(TypedScopeCreator.this.typeRegistry);
/*1435*/          functionParamBuilder.addRequiredParams(new JSType[] { TypedScopeCreator.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE) });
/*1437*/          FunctionType findDelegate = TypedScopeCreator.this.typeRegistry.createFunctionType(TypedScopeCreator.this.typeRegistry.createDefaultObjectUnion(delegateBaseObject), functionParamBuilder.build());
/*1441*/          FunctionType delegateProxy = TypedScopeCreator.this.typeRegistry.createConstructorType(delegateBaseObject.getReferenceName() + TypedScopeCreator.DELEGATE_PROXY_SUFFIX, null, null, null);
/*1444*/          delegateProxy.setPrototypeBasedOn(delegateBaseObject);
/*1446*/          TypedScopeCreator.this.codingConvention.applyDelegateRelationship(delegateSuperObject, delegateBaseObject, delegatorObject, delegateProxy, findDelegate);
/*1449*/          TypedScopeCreator.this.delegateProxyPrototypes.add(delegateProxy.getPrototype());
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void maybeDeclareQualifiedName(NodeTraversal t, JSDocInfo info, Node n, Node parent, Node rhsValue) {
/*1466*/      Node ownerNode = n.getFirstChild();
/*1467*/      String ownerName = ownerNode.getQualifiedName();
/*1468*/      String qName = n.getQualifiedName();
/*1469*/      String propName = n.getLastChild().getString();
/*1470*/      Preconditions.checkArgument((qName != null && ownerName != null));
/*1486*/      JSType valueType = getDeclaredType(t.getSourceName(), info, n, rhsValue);
/*1487*/      if (valueType == null && rhsValue != null)
/*1489*/        valueType = rhsValue.getJSType(); 
/*1497*/      if ("prototype".equals(propName)) {
/*1498*/        Scope.Var qVar = this.scope.getVar(qName);
/*1499*/        if (qVar != null) {
/*1505*/          ObjectType qVarType = ObjectType.cast(qVar.getType());
/*1506*/          if (qVarType != null && rhsValue != null && rhsValue.isObjectLit()) {
/*1509*/            TypedScopeCreator.this.typeRegistry.resetImplicitPrototype(rhsValue.getJSType(), qVarType.getImplicitPrototype());
/*1511*/          } else if (!qVar.isTypeInferred()) {
/*   0*/            return;
/*   0*/          } 
/*1519*/          if (qVar.getScope() == this.scope)
/*1520*/            this.scope.undeclare(qVar); 
/*   0*/        } 
/*   0*/      } 
/*1525*/      if (valueType == null) {
/*1526*/        if (parent.isExprResult())
/*1527*/          this.stubDeclarations.add(new TypedScopeCreator.StubDeclaration(n, (t.getInput() != null && t.getInput().isExtern()), ownerName)); 
/*   0*/        return;
/*   0*/      } 
/*1536*/      boolean inferred = isQualifiedNameInferred(qName, n, info, rhsValue, valueType);
/*1538*/      if (!inferred) {
/*1539*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1540*/        if (ownerType != null) {
/*1543*/          boolean isExtern = (t.getInput() != null && t.getInput().isExtern());
/*1544*/          if ((!ownerType.hasOwnProperty(propName) || ownerType.isPropertyTypeInferred(propName)) && ((isExtern && !ownerType.isNativeObjectType()) || !ownerType.isInstanceType()))
/*1549*/            ownerType.defineDeclaredProperty(propName, valueType, n); 
/*   0*/        } 
/*1555*/        defineSlot(n, parent, valueType, inferred);
/*1556*/      } else if (rhsValue != null && rhsValue.isTrue()) {
/*1558*/        FunctionType ownerType = JSType.toMaybeFunctionType(getObjectSlot(ownerName));
/*1560*/        if (ownerType != null) {
/*1561*/          JSType ownerTypeOfThis = ownerType.getTypeOfThis();
/*1562*/          String delegateName = TypedScopeCreator.this.codingConvention.getDelegateSuperclassName();
/*1563*/          JSType delegateType = (delegateName == null) ? null : TypedScopeCreator.this.typeRegistry.getType(delegateName);
/*1565*/          if (delegateType != null && ownerTypeOfThis.isSubtype(delegateType))
/*1567*/            defineSlot(n, parent, TypedScopeCreator.this.getNativeType(JSTypeNative.BOOLEAN_TYPE), true); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isQualifiedNameInferred(String qName, Node n, JSDocInfo info, Node rhsValue, JSType valueType) {
/*1603*/      if (valueType == null)
/*1604*/        return true; 
/*   0*/      boolean inferred = true;
/*1608*/      if (info != null)
/*1609*/        inferred = (!info.hasType() && !info.hasEnumParameterType() && (!info.isConstant() || valueType == null || valueType.isUnknownType()) && !FunctionTypeBuilder.isFunctionTypeDeclaration(info)); 
/*1616*/      if (inferred && rhsValue != null && rhsValue.isFunction()) {
/*1617*/        if (info != null)
/*1618*/          return false; 
/*1619*/        if (!this.scope.isDeclared(qName, false) && n.isUnscopedQualifiedName()) {
/*1624*/          Node current = n.getParent();
/*1625*/          for (; !current.isScript() && !current.isFunction(); 
/*1626*/            current = current.getParent()) {
/*1627*/            if (NodeUtil.isControlStructure(current))
/*1628*/              return true; 
/*   0*/          } 
/*1634*/          FunctionTypeBuilder.AstFunctionContents contents = TypedScopeCreator.this.getFunctionAnalysisResults(this.scope.getRootNode());
/*1636*/          if (contents == null || !contents.getEscapedQualifiedNames().contains(qName))
/*1638*/            return false; 
/*   0*/        } 
/*   0*/      } 
/*1642*/      return inferred;
/*   0*/    }
/*   0*/    
/*   0*/    private ObjectType getObjectSlot(String slotName) {
/*1651*/      Scope.Var ownerVar = this.scope.getVar(slotName);
/*1652*/      if (ownerVar != null) {
/*1653*/        JSType ownerVarType = ownerVar.getType();
/*1654*/        return ObjectType.cast((ownerVarType == null) ? null : ownerVarType.restrictByNotNullOrUndefined());
/*   0*/      } 
/*1657*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    void resolveStubDeclarations() {
/*1665*/      for (TypedScopeCreator.StubDeclaration stub : this.stubDeclarations) {
/*1666*/        Node n = stub.node;
/*1667*/        Node parent = n.getParent();
/*1668*/        String qName = n.getQualifiedName();
/*1669*/        String propName = n.getLastChild().getString();
/*1670*/        String ownerName = stub.ownerName;
/*1671*/        boolean isExtern = stub.isExtern;
/*1673*/        if (this.scope.isDeclared(qName, false))
/*   0*/          continue; 
/*1679*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1680*/        ObjectType unknownType = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.UNKNOWN_TYPE);
/*1681*/        defineSlot(n, parent, unknownType, true);
/*1683*/        if (ownerType != null && (isExtern || ownerType.isFunctionPrototypeType())) {
/*1687*/          ownerType.defineInferredProperty(propName, unknownType, n);
/*   0*/          continue;
/*   0*/        } 
/*1690*/        TypedScopeCreator.this.typeRegistry.registerPropertyOnType(propName, (ownerType == null) ? unknownType : ownerType);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private final class CollectProperties extends NodeTraversal.AbstractShallowStatementCallback {
/*   0*/      private final ObjectType thisType;
/*   0*/      
/*   0*/      CollectProperties(ObjectType thisType) {
/*1705*/        this.thisType = thisType;
/*   0*/      }
/*   0*/      
/*   0*/      public void visit(NodeTraversal t, Node n, Node parent) {
/*1710*/        if (n.isExprResult()) {
/*1711*/          Node child = n.getFirstChild();
/*1712*/          switch (child.getType()) {
/*   0*/            case 86:
/*1714*/              maybeCollectMember(t, child.getFirstChild(), child, child.getLastChild());
/*   0*/              break;
/*   0*/            case 33:
/*1718*/              maybeCollectMember(t, child, child, null);
/*   0*/              break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      }
/*   0*/      
/*   0*/      private void maybeCollectMember(NodeTraversal t, Node member, Node nodeWithJsDocInfo, @Nullable Node value) {
/*1726*/        JSDocInfo info = nodeWithJsDocInfo.getJSDocInfo();
/*1731*/        if (info == null || !member.isGetProp() || !member.getFirstChild().isThis())
/*   0*/          return; 
/*1737*/        member.getFirstChild().setJSType(this.thisType);
/*1738*/        JSType jsType = TypedScopeCreator.AbstractScopeBuilder.this.getDeclaredType(t.getSourceName(), info, member, value);
/*1739*/        Node name = member.getLastChild();
/*1740*/        if (jsType != null && (name.isName() || name.isString()))
/*1742*/          this.thisType.defineDeclaredProperty(name.getString(), jsType, member); 
/*   0*/      }
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class CollectProperties extends NodeTraversal.AbstractShallowStatementCallback {
/*   0*/    private final ObjectType thisType;
/*   0*/    
/*   0*/    CollectProperties(ObjectType thisType) {
/*   0*/      this.thisType = thisType;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/      if (n.isExprResult()) {
/*   0*/        Node child = n.getFirstChild();
/*   0*/        switch (child.getType()) {
/*   0*/          case 86:
/*   0*/            maybeCollectMember(t, child.getFirstChild(), child, child.getLastChild());
/*   0*/            break;
/*   0*/          case 33:
/*   0*/            maybeCollectMember(t, child, child, null);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void maybeCollectMember(NodeTraversal t, Node member, Node nodeWithJsDocInfo, @Nullable Node value) {
/*   0*/      JSDocInfo info = nodeWithJsDocInfo.getJSDocInfo();
/*   0*/      if (info == null || !member.isGetProp() || !member.getFirstChild().isThis())
/*   0*/        return; 
/*   0*/      member.getFirstChild().setJSType(this.thisType);
/*   0*/      JSType jsType = TypedScopeCreator.AbstractScopeBuilder.this.getDeclaredType(t.getSourceName(), info, member, value);
/*   0*/      Node name = member.getLastChild();
/*   0*/      if (jsType != null && (name.isName() || name.isString()))
/*1742*/        this.thisType.defineDeclaredProperty(name.getString(), jsType, member); 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class StubDeclaration {
/*   0*/    private final Node node;
/*   0*/    
/*   0*/    private final boolean isExtern;
/*   0*/    
/*   0*/    private final String ownerName;
/*   0*/    
/*   0*/    private StubDeclaration(Node node, boolean isExtern, String ownerName) {
/*1760*/      this.node = node;
/*1761*/      this.isExtern = isExtern;
/*1762*/      this.ownerName = ownerName;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class GlobalScopeBuilder extends AbstractScopeBuilder {
/*   0*/    private GlobalScopeBuilder(Scope scope) {
/*1773*/      super(scope);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1785*/      super.visit(t, n, parent);
/*1787*/      switch (n.getType()) {
/*   0*/        case 118:
/*1791*/          if (n.hasOneChild())
/*1792*/            checkForTypedef(t, n.getFirstChild(), n.getJSDocInfo()); 
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void maybeDeclareQualifiedName(NodeTraversal t, JSDocInfo info, Node n, Node parent, Node rhsValue) {
/*1802*/      checkForTypedef(t, n, info);
/*1803*/      super.maybeDeclareQualifiedName(t, info, n, parent, rhsValue);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForTypedef(NodeTraversal t, Node candidate, JSDocInfo info) {
/*1814*/      if (info == null || !info.hasTypedefType())
/*   0*/        return; 
/*1818*/      String typedef = candidate.getQualifiedName();
/*1819*/      if (typedef == null)
/*   0*/        return; 
/*1826*/      TypedScopeCreator.this.typeRegistry.declareType(typedef, TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*1828*/      JSType realType = info.getTypedefType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/*1829*/      if (realType == null)
/*1830*/        TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), candidate, TypedScopeCreator.MALFORMED_TYPEDEF, new String[] { typedef })); 
/*1835*/      TypedScopeCreator.this.typeRegistry.overwriteDeclaredType(typedef, realType);
/*1836*/      if (candidate.isGetProp())
/*1837*/        defineSlot(candidate, candidate.getParent(), TypedScopeCreator.this.getNativeType(JSTypeNative.NO_TYPE), false); 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class LocalScopeBuilder extends AbstractScopeBuilder {
/*   0*/    private LocalScopeBuilder(Scope scope) {
/*1852*/      super(scope);
/*   0*/    }
/*   0*/    
/*   0*/    void build() {
/*1859*/      NodeTraversal.traverse(TypedScopeCreator.this.compiler, this.scope.getRootNode(), this);
/*1861*/      FunctionTypeBuilder.AstFunctionContents contents = TypedScopeCreator.this.getFunctionAnalysisResults(this.scope.getRootNode());
/*1863*/      if (contents != null)
/*1864*/        for (String varName : contents.getEscapedVarNames()) {
/*1865*/          Scope.Var v = this.scope.getVar(varName);
/*1866*/          Preconditions.checkState((v.getScope() == this.scope));
/*1867*/          v.markEscaped();
/*   0*/        }  
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1881*/      if (n == this.scope.getRootNode())
/*   0*/        return; 
/*1883*/      if (n.isParamList() && parent == this.scope.getRootNode()) {
/*1884*/        handleFunctionInputs(parent);
/*   0*/        return;
/*   0*/      } 
/*1888*/      super.visit(t, n, parent);
/*   0*/    }
/*   0*/    
/*   0*/    private void handleFunctionInputs(Node fnNode) {
/*1894*/      Node fnNameNode = fnNode.getFirstChild();
/*1895*/      String fnName = fnNameNode.getString();
/*1896*/      if (!fnName.isEmpty()) {
/*1897*/        Scope.Var fnVar = this.scope.getVar(fnName);
/*1898*/        if (fnVar == null || (fnVar.getNameNode() != null && fnVar.getInitialValue() != fnNode))
/*1906*/          defineSlot(fnNameNode, fnNode, fnNode.getJSType(), false); 
/*   0*/      } 
/*1910*/      declareArguments(fnNode);
/*   0*/    }
/*   0*/    
/*   0*/    private void declareArguments(Node functionNode) {
/*1917*/      Node astParameters = functionNode.getFirstChild().getNext();
/*1918*/      Node body = astParameters.getNext();
/*1919*/      FunctionType functionType = JSType.toMaybeFunctionType(functionNode.getJSType());
/*1921*/      if (functionType != null) {
/*1922*/        Node jsDocParameters = functionType.getParametersNode();
/*1923*/        if (jsDocParameters != null) {
/*1924*/          Node jsDocParameter = jsDocParameters.getFirstChild();
/*1925*/          for (Node astParameter : astParameters.children()) {
/*1926*/            if (jsDocParameter != null) {
/*1927*/              defineSlot(astParameter, functionNode, jsDocParameter.getJSType(), false);
/*1929*/              jsDocParameter = jsDocParameter.getNext();
/*   0*/              continue;
/*   0*/            } 
/*1931*/            defineSlot(astParameter, functionNode, null, true);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class FirstOrderFunctionAnalyzer extends NodeTraversal.AbstractScopedCallback implements CompilerPass {
/*   0*/    private final AbstractCompiler compiler;
/*   0*/    
/*   0*/    private final Map<Node, FunctionTypeBuilder.AstFunctionContents> data;
/*   0*/    
/*   0*/    FirstOrderFunctionAnalyzer(AbstractCompiler compiler, Map<Node, FunctionTypeBuilder.AstFunctionContents> outParam) {
/*1950*/      this.compiler = compiler;
/*1951*/      this.data = outParam;
/*   0*/    }
/*   0*/    
/*   0*/    public void process(Node externs, Node root) {
/*1955*/      if (externs == null) {
/*1956*/        NodeTraversal.traverse(this.compiler, root, this);
/*   0*/      } else {
/*1958*/        NodeTraversal.traverseRoots(this.compiler, (List<Node>)ImmutableList.of(externs, root), this);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void enterScope(NodeTraversal t) {
/*1964*/      if (!t.inGlobalScope()) {
/*1965*/        Node n = t.getScopeRoot();
/*1966*/        this.data.put(n, new FunctionTypeBuilder.AstFunctionContents(n));
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1971*/      if (t.inGlobalScope())
/*   0*/        return; 
/*1975*/      if (n.isReturn() && n.getFirstChild() != null)
/*1976*/        ((FunctionTypeBuilder.AstFunctionContents)this.data.get(t.getScopeRoot())).recordNonEmptyReturn(); 
/*1979*/      if (t.getScopeDepth() <= 2)
/*   0*/        return; 
/*1987*/      if (n.isName() && NodeUtil.isLValue(n)) {
/*1988*/        String name = n.getString();
/*1989*/        Scope scope = t.getScope();
/*1990*/        Scope.Var var = scope.getVar(name);
/*1991*/        if (var != null) {
/*1992*/          Scope ownerScope = var.getScope();
/*1993*/          if (scope != ownerScope && ownerScope.isLocal())
/*1994*/            ((FunctionTypeBuilder.AstFunctionContents)this.data.get(ownerScope.getRootNode())).recordEscapedVarName(name); 
/*   0*/        } 
/*1997*/      } else if (n.isGetProp() && n.isUnscopedQualifiedName() && NodeUtil.isLValue(n)) {
/*1999*/        String name = NodeUtil.getRootOfQualifiedName(n).getString();
/*2000*/        Scope scope = t.getScope();
/*2001*/        Scope.Var var = scope.getVar(name);
/*2002*/        if (var != null) {
/*2003*/          Scope ownerScope = var.getScope();
/*2004*/          if (scope != ownerScope && ownerScope.isLocal())
/*2005*/            ((FunctionTypeBuilder.AstFunctionContents)this.data.get(ownerScope.getRootNode())).recordEscapedQualifiedName(n.getQualifiedName()); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private FunctionTypeBuilder.AstFunctionContents getFunctionAnalysisResults(@Nullable Node n) {
/*2014*/    if (n == null)
/*2015*/      return null; 
/*2020*/    return this.functionAnalysisResults.get(n);
/*   0*/  }
/*   0*/}
