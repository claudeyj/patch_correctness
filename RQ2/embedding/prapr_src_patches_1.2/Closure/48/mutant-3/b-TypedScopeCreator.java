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
/* 416*/    private final List<TypedScopeCreator.StubDeclaration> stubDeclarations = Lists.newArrayList();
/*   0*/    
/* 422*/    private String sourceName = null;
/*   0*/    
/*   0*/    private InputId inputId;
/*   0*/    
/*   0*/    private AbstractScopeBuilder(Scope scope) {
/* 430*/      this.scope = scope;
/*   0*/    }
/*   0*/    
/*   0*/    void setDeferredType(Node node, JSType type) {
/* 434*/      this.deferredSetTypes.add(new TypedScopeCreator.DeferredSetType(node, type));
/*   0*/    }
/*   0*/    
/*   0*/    void resolveTypes() {
/* 439*/      for (TypedScopeCreator.DeferredSetType deferred : this.deferredSetTypes)
/* 440*/        deferred.resolve(this.scope); 
/* 444*/      Iterator<Scope.Var> vars = this.scope.getVars();
/* 445*/      while (vars.hasNext())
/* 446*/        ((Scope.Var)vars.next()).resolveType(TypedScopeCreator.this.typeParsingErrorReporter); 
/* 451*/      TypedScopeCreator.this.typeRegistry.resolveTypesInScope(this.scope);
/*   0*/    }
/*   0*/    
/*   0*/    public final boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/* 457*/      this.inputId = t.getInputId();
/* 458*/      if (n.isFunction() || n.isScript()) {
/* 460*/        Preconditions.checkNotNull(this.inputId);
/* 461*/        this.sourceName = NodeUtil.getSourceName(n);
/*   0*/      } 
/* 466*/      boolean descend = (parent == null || parent.getType() != 105 || n == parent.getFirstChild() || parent == this.scope.getRootNode());
/* 469*/      if (descend)
/* 472*/        if (NodeUtil.isStatementParent(n)) {
/* 473*/          Node child = n.getFirstChild();
/* 474*/          for (; child != null; 
/* 475*/            child = child.getNext()) {
/* 476*/            if (NodeUtil.isHoistedFunctionDeclaration(child))
/* 477*/              defineFunctionLiteral(child, n); 
/*   0*/          } 
/*   0*/        }  
/* 483*/      return descend;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/      Node firstChild;
/* 488*/      this.inputId = t.getInputId();
/* 489*/      attachLiteralTypes(t, n);
/* 491*/      switch (n.getType()) {
/*   0*/        case 37:
/* 493*/          checkForClassDefiningCalls(t, n, parent);
/* 494*/          checkForCallingConventionDefiningCalls(n, TypedScopeCreator.this.delegateCallingConventions);
/*   0*/          break;
/*   0*/        case 105:
/* 498*/          if (t.getInput() == null || !t.getInput().isExtern())
/* 499*/            this.nonExternFunctions.add(n); 
/* 503*/          if (!NodeUtil.isHoistedFunctionDeclaration(n))
/* 504*/            defineFunctionLiteral(n, parent); 
/*   0*/          break;
/*   0*/        case 86:
/* 510*/          firstChild = n.getFirstChild();
/* 511*/          if (firstChild.isGetProp() && firstChild.isQualifiedName())
/* 513*/            maybeDeclareQualifiedName(t, n.getJSDocInfo(), firstChild, n, firstChild.getNext()); 
/*   0*/          break;
/*   0*/        case 120:
/* 519*/          defineCatch(n, parent);
/*   0*/          break;
/*   0*/        case 118:
/* 523*/          defineVar(n, parent);
/*   0*/          break;
/*   0*/        case 33:
/* 528*/          if (parent.isExprResult() && n.isQualifiedName())
/* 530*/            maybeDeclareQualifiedName(t, n.getJSDocInfo(), n, parent, null); 
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void attachLiteralTypes(NodeTraversal t, Node n) {
/* 537*/      switch (n.getType()) {
/*   0*/        case 41:
/* 539*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.NULL_TYPE));
/*   0*/          break;
/*   0*/        case 122:
/* 543*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.VOID_TYPE));
/*   0*/          break;
/*   0*/        case 40:
/* 548*/          if (!NodeUtil.isObjectLitKey(n, n.getParent()))
/* 549*/            n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.STRING_TYPE)); 
/*   0*/          break;
/*   0*/        case 39:
/* 554*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.NUMBER_TYPE));
/*   0*/          break;
/*   0*/        case 43:
/*   0*/        case 44:
/* 559*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.BOOLEAN_TYPE));
/*   0*/          break;
/*   0*/        case 47:
/* 563*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.REGEXP_TYPE));
/*   0*/          break;
/*   0*/        case 64:
/* 567*/          defineObjectLiteral(t, n);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void defineObjectLiteral(NodeTraversal t, Node objectLit) {
/* 577*/      JSType type = null;
/* 578*/      JSDocInfo info = objectLit.getJSDocInfo();
/* 579*/      if (info != null && info.getLendsName() != null) {
/* 581*/        String lendsName = info.getLendsName();
/* 582*/        Scope.Var lendsVar = this.scope.getVar(lendsName);
/* 583*/        if (lendsVar == null) {
/* 584*/          TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, objectLit, TypedScopeCreator.UNKNOWN_LENDS, new String[] { lendsName }));
/*   0*/        } else {
/* 587*/          type = lendsVar.getType();
/* 588*/          if (type == null)
/* 589*/            type = TypedScopeCreator.this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/* 591*/          if (!type.isSubtype(TypedScopeCreator.this.typeRegistry.getNativeType(JSTypeNative.OBJECT_TYPE))) {
/* 592*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, objectLit, TypedScopeCreator.LENDS_ON_NON_OBJECT, new String[] { lendsName, type.toString() }));
/* 595*/            type = null;
/*   0*/          } else {
/* 597*/            objectLit.setJSType(type);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 602*/      info = NodeUtil.getBestJSDocInfo(objectLit);
/* 603*/      Node lValue = NodeUtil.getBestLValue(objectLit);
/* 604*/      String lValueName = NodeUtil.getBestLValueName(lValue);
/*   0*/      boolean createdEnumType = false;
/* 606*/      if (info != null && info.hasEnumParameterType()) {
/* 607*/        type = createEnumTypeFromNodes(objectLit, lValueName, info, lValue);
/* 608*/        createdEnumType = true;
/*   0*/      } 
/* 611*/      if (type == null)
/* 612*/        type = TypedScopeCreator.this.typeRegistry.createAnonymousObjectType(); 
/* 615*/      setDeferredType(objectLit, type);
/* 618*/      processObjectLitProperties(t, objectLit, ObjectType.cast(objectLit.getJSType()), !createdEnumType);
/*   0*/    }
/*   0*/    
/*   0*/    void processObjectLitProperties(NodeTraversal t, Node objLit, ObjectType objLitType, boolean declareOnOwner) {
/* 633*/      for (Node keyNode = objLit.getFirstChild(); keyNode != null; 
/* 634*/        keyNode = keyNode.getNext()) {
/* 635*/        Node value = keyNode.getFirstChild();
/* 636*/        String memberName = NodeUtil.getObjectLitKeyName(keyNode);
/* 637*/        JSDocInfo info = keyNode.getJSDocInfo();
/* 638*/        JSType valueType = getDeclaredType(t.getSourceName(), info, keyNode, value);
/* 640*/        JSType keyType = objLitType.isEnumType() ? objLitType.toMaybeEnumType().getElementsType() : NodeUtil.getObjectLitKeyTypeFromValueType(keyNode, valueType);
/* 643*/        if (keyType != null) {
/* 646*/          String qualifiedName = NodeUtil.getBestLValueName(keyNode);
/* 647*/          if (qualifiedName != null) {
/* 648*/            defineSlot(keyNode, objLit, qualifiedName, keyType, false);
/*   0*/          } else {
/* 650*/            setDeferredType(keyNode, keyType);
/*   0*/          } 
/* 653*/          if (objLitType != null && declareOnOwner) {
/* 655*/            boolean isExtern = (t.getInput() != null && t.getInput().isExtern());
/* 656*/            objLitType.defineDeclaredProperty(memberName, keyType, keyNode);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private JSType getDeclaredTypeInAnnotation(String sourceName, Node node, JSDocInfo info) {
/* 670*/      JSType jsType = null;
/* 671*/      Node objNode = node.isGetProp() ? node.getFirstChild() : (NodeUtil.isObjectLitKey(node, node.getParent()) ? node.getParent() : null);
/* 675*/      if (info != null)
/* 676*/        if (info.hasType()) {
/* 677*/          jsType = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 678*/        } else if (FunctionTypeBuilder.isFunctionTypeDeclaration(info)) {
/* 679*/          String fnName = node.getQualifiedName();
/* 680*/          jsType = createFunctionTypeFromNodes(null, fnName, info, node);
/*   0*/        }  
/* 684*/      return jsType;
/*   0*/    }
/*   0*/    
/*   0*/    void assertDefinitionNode(Node n, int type) {
/* 692*/      Preconditions.checkState((this.sourceName != null));
/* 693*/      Preconditions.checkState((n.getType() == type));
/*   0*/    }
/*   0*/    
/*   0*/    void defineCatch(Node n, Node parent) {
/* 700*/      assertDefinitionNode(n, 120);
/* 701*/      Node catchName = n.getFirstChild();
/* 702*/      defineSlot(catchName, n, null);
/*   0*/    }
/*   0*/    
/*   0*/    void defineVar(Node n, Node parent) {
/* 709*/      assertDefinitionNode(n, 118);
/* 710*/      JSDocInfo info = n.getJSDocInfo();
/* 711*/      if (n.hasMoreThanOneChild()) {
/* 712*/        if (info != null)
/* 714*/          TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, TypeCheck.MULTIPLE_VAR_DEF, new String[0])); 
/* 716*/        for (Node name : n.children())
/* 717*/          defineName(name, n, parent, name.getJSDocInfo()); 
/*   0*/      } else {
/* 720*/        Node name = n.getFirstChild();
/* 721*/        defineName(name, n, parent, (info != null) ? info : name.getJSDocInfo());
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void defineFunctionLiteral(Node n, Node parent) {
/* 730*/      assertDefinitionNode(n, 105);
/* 734*/      Node lValue = NodeUtil.getBestLValue(n);
/* 735*/      JSDocInfo info = NodeUtil.getBestJSDocInfo(n);
/* 736*/      String functionName = NodeUtil.getBestLValueName(lValue);
/* 737*/      FunctionType functionType = createFunctionTypeFromNodes(n, functionName, info, lValue);
/* 741*/      setDeferredType(n, functionType);
/* 746*/      if (NodeUtil.isFunctionDeclaration(n))
/* 747*/        defineSlot(n.getFirstChild(), n, functionType); 
/*   0*/    }
/*   0*/    
/*   0*/    private void defineName(Node name, Node var, Node parent, JSDocInfo info) {
/* 761*/      Node value = name.getFirstChild();
/* 764*/      JSType type = getDeclaredType(this.sourceName, info, name, value);
/* 765*/      if (type == null)
/* 767*/        type = name.isFromExterns() ? TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE) : null; 
/* 770*/      defineSlot(name, var, type);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean shouldUseFunctionLiteralType(FunctionType type, JSDocInfo info, Node lValue) {
/* 782*/      if (info != null)
/* 783*/        return true; 
/* 785*/      if (lValue != null && NodeUtil.isObjectLitKey(lValue, lValue.getParent()))
/* 787*/        return false; 
/* 789*/      return (this.scope.isGlobal() || !type.isReturnTypeInferred());
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType createFunctionTypeFromNodes(@Nullable Node rValue, @Nullable String name, @Nullable JSDocInfo info, @Nullable Node lvalueNode) {
/* 820*/      FunctionType functionType = null;
/* 823*/      if (rValue != null && rValue.isQualifiedName() && this.scope.isGlobal()) {
/* 824*/        Scope.Var var = this.scope.getVar(rValue.getQualifiedName());
/* 825*/        if (var != null && var.getType() != null && var.getType().isFunctionType()) {
/* 827*/          FunctionType aliasedType = var.getType().toMaybeFunctionType();
/* 828*/          if ((aliasedType.isConstructor() || aliasedType.isInterface()) && !aliasedType.isNativeObjectType()) {
/* 830*/            functionType = aliasedType;
/* 832*/            if (name != null && this.scope.isGlobal())
/* 833*/              TypedScopeCreator.this.typeRegistry.declareType(name, functionType.getInstanceType()); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 839*/      if (functionType == null) {
/* 840*/        Node errorRoot = (rValue == null) ? lvalueNode : rValue;
/* 841*/        boolean isFnLiteral = (rValue != null && rValue.isFunction());
/* 843*/        Node fnRoot = isFnLiteral ? rValue : null;
/* 844*/        Node parametersNode = isFnLiteral ? rValue.getFirstChild().getNext() : null;
/* 846*/        Node fnBlock = isFnLiteral ? parametersNode.getNext() : null;
/* 848*/        if (info != null && info.hasType()) {
/* 849*/          JSType type = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 852*/          type = type.restrictByNotNullOrUndefined();
/* 853*/          if (type.isFunctionType()) {
/* 854*/            functionType = type.toMaybeFunctionType();
/* 855*/            functionType.setJSDocInfo(info);
/*   0*/          } 
/*   0*/        } 
/* 859*/        if (functionType == null) {
/* 861*/          Node ownerNode = NodeUtil.getBestLValueOwner(lvalueNode);
/* 862*/          String ownerName = NodeUtil.getBestLValueName(ownerNode);
/* 863*/          Scope.Var ownerVar = null;
/* 864*/          String propName = null;
/* 865*/          ObjectType ownerType = null;
/* 866*/          if (ownerName != null) {
/* 867*/            ownerVar = this.scope.getVar(ownerName);
/* 868*/            if (ownerVar != null)
/* 869*/              ownerType = ObjectType.cast(ownerVar.getType()); 
/* 871*/            if (name != null)
/* 872*/              propName = name.substring(ownerName.length() + 1); 
/*   0*/          } 
/* 876*/          FunctionType overriddenPropType = null;
/* 877*/          if (ownerType != null && propName != null)
/* 878*/            overriddenPropType = findOverriddenFunction(ownerType, propName); 
/* 882*/          FunctionTypeBuilder builder = new FunctionTypeBuilder(name, TypedScopeCreator.this.compiler, errorRoot, this.sourceName, this.scope).setContents(TypedScopeCreator.this.getFunctionAnalysisResults(fnRoot)).inferFromOverriddenFunction(overriddenPropType, parametersNode).inferTemplateTypeName(info).inferReturnType(info).inferInheritance(info);
/*   0*/          boolean searchedForThisType = false;
/* 894*/          if (ownerType != null && ownerType.isFunctionPrototypeType()) {
/* 895*/            builder.inferThisType(info, ownerType.getOwnerFunction().getInstanceType());
/* 897*/            searchedForThisType = true;
/* 898*/          } else if (ownerNode != null && ownerNode.isThis()) {
/* 899*/            builder.inferThisType(info, ownerNode.getJSType());
/* 900*/            searchedForThisType = true;
/*   0*/          } 
/* 903*/          if (!searchedForThisType)
/* 904*/            builder.inferThisType(info); 
/* 907*/          functionType = builder.inferParameterTypes(parametersNode, info).buildAndRegister();
/*   0*/        } 
/*   0*/      } 
/* 914*/      return functionType;
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType findOverriddenFunction(ObjectType ownerType, String propName) {
/* 924*/      JSType propType = ownerType.getPropertyType(propName);
/* 925*/      if (propType != null && propType.isFunctionType())
/* 926*/        return propType.toMaybeFunctionType(); 
/* 931*/      for (ObjectType iface : ownerType.getCtorImplementedInterfaces()) {
/* 932*/        propType = iface.getPropertyType(propName);
/* 933*/        if (propType != null && propType.isFunctionType())
/* 934*/          return propType.toMaybeFunctionType(); 
/*   0*/      } 
/* 939*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    private EnumType createEnumTypeFromNodes(Node rValue, String name, JSDocInfo info, Node lValueNode) {
/* 961*/      Preconditions.checkNotNull(info);
/* 962*/      Preconditions.checkState(info.hasEnumParameterType());
/* 964*/      EnumType enumType = null;
/* 965*/      if (rValue != null && rValue.isQualifiedName()) {
/* 967*/        Scope.Var var = this.scope.getVar(rValue.getQualifiedName());
/* 968*/        if (var != null && var.getType() instanceof EnumType)
/* 969*/          enumType = (EnumType)var.getType(); 
/*   0*/      } 
/* 973*/      if (enumType == null) {
/* 974*/        JSType elementsType = info.getEnumParameterType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 976*/        enumType = TypedScopeCreator.this.typeRegistry.createEnumType(name, rValue, elementsType);
/* 978*/        if (rValue != null && rValue.isObjectLit()) {
/* 980*/          Node key = rValue.getFirstChild();
/* 981*/          while (key != null) {
/* 982*/            String keyName = NodeUtil.getStringValue(key);
/* 983*/            if (keyName == null) {
/* 985*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_NOT_CONSTANT, new String[] { keyName }));
/* 987*/            } else if (!TypedScopeCreator.this.codingConvention.isValidEnumKey(keyName)) {
/* 988*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_NOT_CONSTANT, new String[] { keyName }));
/*   0*/            } else {
/* 991*/              enumType.defineElement(keyName, key);
/*   0*/            } 
/* 993*/            key = key.getNext();
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 998*/      if (name != null && this.scope.isGlobal())
/* 999*/        TypedScopeCreator.this.typeRegistry.declareType(name, enumType.getElementsType()); 
/*1002*/      return enumType;
/*   0*/    }
/*   0*/    
/*   0*/    private void defineSlot(Node name, Node parent, JSType type) {
/*1014*/      defineSlot(name, parent, type, (type == null));
/*   0*/    }
/*   0*/    
/*   0*/    void defineSlot(Node n, Node parent, JSType type, boolean inferred) {
/*1030*/      Preconditions.checkArgument((inferred || type != null));
/*1034*/      if (n.isName()) {
/*1035*/        Preconditions.checkArgument((parent.isFunction() || parent.isVar() || parent.isParamList() || parent.isCatch()));
/*   0*/      } else {
/*1041*/        Preconditions.checkArgument((n.isGetProp() && (parent.isAssign() || parent.isExprResult())));
/*   0*/      } 
/*1046*/      defineSlot(n, parent, n.getQualifiedName(), type, inferred);
/*   0*/    }
/*   0*/    
/*   0*/    void defineSlot(Node n, Node parent, String variableName, JSType type, boolean inferred) {
/*1062*/      Preconditions.checkArgument(!variableName.isEmpty());
/*1064*/      boolean isGlobalVar = (n.isName() && this.scope.isGlobal());
/*1065*/      boolean shouldDeclareOnGlobalThis = (isGlobalVar && (parent.isVar() || parent.isFunction()));
/*1073*/      Scope scopeToDeclareIn = this.scope;
/*1074*/      if (n.isGetProp() && !this.scope.isGlobal() && isQnameRootedInGlobalScope(n)) {
/*1076*/        Scope globalScope = this.scope.getGlobalScope();
/*1080*/        if (!globalScope.isDeclared(variableName, false))
/*1081*/          scopeToDeclareIn = this.scope.getGlobalScope(); 
/*   0*/      } 
/*1086*/      CompilerInput input = TypedScopeCreator.this.compiler.getInput(this.inputId);
/*1087*/      if (scopeToDeclareIn.isDeclared(variableName, false)) {
/*1088*/        Scope.Var oldVar = scopeToDeclareIn.getVar(variableName);
/*1089*/        TypedScopeCreator.this.validator.expectUndeclaredVariable(this.sourceName, input, n, parent, oldVar, variableName, type);
/*   0*/      } else {
/*1092*/        if (!inferred)
/*1093*/          setDeferredType(n, type); 
/*1097*/        boolean isExtern = n.isFromExterns();
/*1098*/        Scope.Var newVar = scopeToDeclareIn.declare(variableName, n, type, input, inferred);
/*1101*/        if (type instanceof EnumType) {
/*1102*/          Node initialValue = newVar.getInitialValue();
/*1103*/          boolean isValidValue = (initialValue != null && (initialValue.isObjectLit() || initialValue.isQualifiedName()));
/*1106*/          if (!isValidValue)
/*1107*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, TypedScopeCreator.ENUM_INITIALIZER, new String[0])); 
/*   0*/        } 
/*1112*/        FunctionType fnType = JSType.toMaybeFunctionType(type);
/*1113*/        if (fnType != null && !type.isEmptyType())
/*1116*/          if ((fnType.isConstructor() || fnType.isInterface()) && !fnType.equals(TypedScopeCreator.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE))) {
/*1119*/            FunctionType superClassCtor = fnType.getSuperClassConstructor();
/*1120*/            ObjectType.Property prototypeSlot = fnType.getSlot("prototype");
/*1122*/            String prototypeName = variableName + ".prototype";
/*1127*/            if (scopeToDeclareIn.getOwnSlot(prototypeName) == null) {
/*1136*/              prototypeSlot.setNode(n);
/*1138*/              scopeToDeclareIn.declare(prototypeName, n, prototypeSlot.getType(), input, (superClassCtor == null || superClassCtor.getInstanceType().equals(TypedScopeCreator.this.getNativeType(JSTypeNative.OBJECT_TYPE))));
/*   0*/            } 
/*1148*/            if (newVar.getInitialValue() == null && !isExtern && variableName.equals(fnType.getInstanceType().getReferenceName()))
/*1158*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, fnType.isConstructor() ? TypedScopeCreator.CTOR_INITIALIZER : TypedScopeCreator.IFACE_INITIALIZER, new String[] { variableName })); 
/*   0*/          }  
/*   0*/      } 
/*1168*/      if (shouldDeclareOnGlobalThis) {
/*1169*/        ObjectType globalThis = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.GLOBAL_THIS);
/*1171*/        if (inferred) {
/*1172*/          globalThis.defineInferredProperty(variableName, (type == null) ? TypedScopeCreator.this.getNativeType(JSTypeNative.NO_TYPE) : type, n);
/*   0*/        } else {
/*1178*/          globalThis.defineDeclaredProperty(variableName, type, n);
/*   0*/        } 
/*   0*/      } 
/*1182*/      if (isGlobalVar && "Window".equals(variableName) && type != null && type.isFunctionType() && type.isConstructor()) {
/*1186*/        FunctionType globalThisCtor = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.GLOBAL_THIS).getConstructor();
/*1188*/        globalThisCtor.getInstanceType().clearCachedValues();
/*1189*/        globalThisCtor.getPrototype().clearCachedValues();
/*1190*/        globalThisCtor.setPrototypeBasedOn(type.toMaybeFunctionType().getInstanceType());
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isQnameRootedInGlobalScope(Node n) {
/*1199*/      Scope scope = getQnameRootScope(n);
/*1200*/      return (scope != null && scope.isGlobal());
/*   0*/    }
/*   0*/    
/*   0*/    private Scope getQnameRootScope(Node n) {
/*1207*/      Node root = NodeUtil.getRootOfQualifiedName(n);
/*1208*/      if (root.isName()) {
/*1209*/        Scope.Var var = this.scope.getVar(root.getString());
/*1210*/        if (var != null)
/*1211*/          return var.getScope(); 
/*   0*/      } 
/*1214*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    private JSType getDeclaredType(String sourceName, JSDocInfo info, Node lValue, @Nullable Node rValue) {
/*1228*/      if (info != null && info.hasType())
/*1229*/        return getDeclaredTypeInAnnotation(sourceName, lValue, info); 
/*1230*/      if (rValue != null && rValue.isFunction() && shouldUseFunctionLiteralType(JSType.toMaybeFunctionType(rValue.getJSType()), info, lValue))
/*1233*/        return rValue.getJSType(); 
/*1234*/      if (info != null) {
/*1235*/        if (info.hasEnumParameterType()) {
/*1236*/          if (rValue != null && rValue.isObjectLit())
/*1237*/            return rValue.getJSType(); 
/*1239*/          return createEnumTypeFromNodes(rValue, lValue.getQualifiedName(), info, lValue);
/*   0*/        } 
/*1242*/        if (info.isConstructor() || info.isInterface())
/*1243*/          return createFunctionTypeFromNodes(rValue, lValue.getQualifiedName(), info, lValue); 
/*1247*/        if (info.isConstant()) {
/*1248*/          JSType knownType = null;
/*1249*/          if (rValue != null) {
/*1250*/            if (rValue.getJSType() != null && !rValue.getJSType().isUnknownType())
/*1252*/              return rValue.getJSType(); 
/*1253*/            if (rValue.isOr()) {
/*1258*/              Node firstClause = rValue.getFirstChild();
/*1259*/              Node secondClause = firstClause.getNext();
/*1260*/              boolean namesMatch = (firstClause.isName() && lValue.isName() && firstClause.getString().equals(lValue.getString()));
/*1263*/              if (namesMatch && secondClause.getJSType() != null && !secondClause.getJSType().isUnknownType())
/*1265*/                return secondClause.getJSType(); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1273*/      return getDeclaredTypeInAnnotation(sourceName, lValue, info);
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType getFunctionType(@Nullable Scope.Var v) {
/*1277*/      JSType t = (v == null) ? null : v.getType();
/*1278*/      ObjectType o = (t == null) ? null : t.dereference();
/*1279*/      return JSType.toMaybeFunctionType(o);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForCallingConventionDefiningCalls(Node n, Map<String, String> delegateCallingConventions) {
/*1287*/      TypedScopeCreator.this.codingConvention.checkForCallingConventionDefiningCalls(n, delegateCallingConventions);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForClassDefiningCalls(NodeTraversal t, Node n, Node parent) {
/*1298*/      CodingConvention.SubclassRelationship relationship = TypedScopeCreator.this.codingConvention.getClassesDefinedByCall(n);
/*1300*/      if (relationship != null) {
/*1301*/        FunctionType superCtor = getFunctionType(this.scope.getVar(relationship.superclassName));
/*1303*/        FunctionType subCtor = getFunctionType(this.scope.getVar(relationship.subclassName));
/*1305*/        if (superCtor != null && superCtor.isConstructor() && subCtor != null && subCtor.isConstructor()) {
/*1307*/          ObjectType superClass = superCtor.getInstanceType();
/*1308*/          ObjectType subClass = subCtor.getInstanceType();
/*1313*/          superCtor = superClass.getConstructor();
/*1314*/          subCtor = subClass.getConstructor();
/*1316*/          if (relationship.type == CodingConvention.SubclassType.INHERITS && !superClass.isEmptyType() && !subClass.isEmptyType())
/*1318*/            TypedScopeCreator.this.validator.expectSuperType(t, n, superClass, subClass); 
/*1321*/          if (superCtor != null && subCtor != null)
/*1322*/            TypedScopeCreator.this.codingConvention.applySubclassRelationship(superCtor, subCtor, relationship.type); 
/*   0*/        } 
/*   0*/      } 
/*1328*/      String singletonGetterClassName = TypedScopeCreator.this.codingConvention.getSingletonGetterClassName(n);
/*1330*/      if (singletonGetterClassName != null) {
/*1331*/        ObjectType objectType = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(singletonGetterClassName));
/*1333*/        if (objectType != null) {
/*1334*/          FunctionType functionType = objectType.getConstructor();
/*1336*/          if (functionType != null) {
/*1337*/            FunctionType getterType = TypedScopeCreator.this.typeRegistry.createFunctionType(objectType, new JSType[0]);
/*1339*/            TypedScopeCreator.this.codingConvention.applySingletonGetter(functionType, getterType, objectType);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1345*/      CodingConvention.DelegateRelationship delegateRelationship = TypedScopeCreator.this.codingConvention.getDelegateRelationship(n);
/*1347*/      if (delegateRelationship != null)
/*1348*/        applyDelegateRelationship(delegateRelationship); 
/*1351*/      CodingConvention.ObjectLiteralCast objectLiteralCast = TypedScopeCreator.this.codingConvention.getObjectLiteralCast(t, n);
/*1353*/      if (objectLiteralCast != null) {
/*1354*/        ObjectType type = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(objectLiteralCast.typeName));
/*1356*/        if (type != null && type.getConstructor() != null) {
/*1357*/          setDeferredType(objectLiteralCast.objectNode, type);
/*   0*/        } else {
/*1359*/          TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), n, TypedScopeCreator.CONSTRUCTOR_EXPECTED, new String[0]));
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void applyDelegateRelationship(CodingConvention.DelegateRelationship delegateRelationship) {
/*1370*/      ObjectType delegatorObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(delegateRelationship.delegator));
/*1372*/      ObjectType delegateBaseObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(delegateRelationship.delegateBase));
/*1374*/      ObjectType delegateSuperObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(TypedScopeCreator.this.codingConvention.getDelegateSuperclassName()));
/*1376*/      if (delegatorObject != null && delegateBaseObject != null && delegateSuperObject != null) {
/*1379*/        FunctionType delegatorCtor = delegatorObject.getConstructor();
/*1380*/        FunctionType delegateBaseCtor = delegateBaseObject.getConstructor();
/*1381*/        FunctionType delegateSuperCtor = delegateSuperObject.getConstructor();
/*1383*/        if (delegatorCtor != null && delegateBaseCtor != null && delegateSuperCtor != null) {
/*1385*/          FunctionParamBuilder functionParamBuilder = new FunctionParamBuilder(TypedScopeCreator.this.typeRegistry);
/*1387*/          functionParamBuilder.addRequiredParams(new JSType[] { TypedScopeCreator.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE) });
/*1389*/          FunctionType findDelegate = TypedScopeCreator.this.typeRegistry.createFunctionType(TypedScopeCreator.this.typeRegistry.createDefaultObjectUnion(delegateBaseObject), functionParamBuilder.build());
/*1393*/          FunctionType delegateProxy = TypedScopeCreator.this.typeRegistry.createConstructorType(delegateBaseObject.getReferenceName() + TypedScopeCreator.DELEGATE_PROXY_SUFFIX, null, null, null);
/*1396*/          delegateProxy.setPrototypeBasedOn(delegateBaseObject);
/*1398*/          TypedScopeCreator.this.codingConvention.applyDelegateRelationship(delegateSuperObject, delegateBaseObject, delegatorObject, delegateProxy, findDelegate);
/*1401*/          TypedScopeCreator.this.delegateProxyPrototypes.add(delegateProxy.getPrototype());
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void maybeDeclareQualifiedName(NodeTraversal t, JSDocInfo info, Node n, Node parent, Node rhsValue) {
/*1418*/      Node ownerNode = n.getFirstChild();
/*1419*/      String ownerName = ownerNode.getQualifiedName();
/*1420*/      String qName = n.getQualifiedName();
/*1421*/      String propName = n.getLastChild().getString();
/*1422*/      Preconditions.checkArgument((qName != null && ownerName != null));
/*1438*/      JSType valueType = getDeclaredType(t.getSourceName(), info, n, rhsValue);
/*1439*/      if (valueType == null && rhsValue != null)
/*1441*/        valueType = parent.getJSType(); 
/*1448*/      if ("prototype".equals(propName)) {
/*1449*/        Scope.Var qVar = this.scope.getVar(qName);
/*1450*/        if (qVar != null) {
/*1456*/          ObjectType qVarType = ObjectType.cast(qVar.getType());
/*1457*/          if (qVarType != null && rhsValue != null && rhsValue.isObjectLit()) {
/*1460*/            TypedScopeCreator.this.typeRegistry.resetImplicitPrototype(rhsValue.getJSType(), qVarType.getImplicitPrototype());
/*1462*/          } else if (!qVar.isTypeInferred()) {
/*   0*/            return;
/*   0*/          } 
/*1470*/          if (qVar.getScope() == this.scope)
/*1471*/            this.scope.undeclare(qVar); 
/*   0*/        } 
/*   0*/      } 
/*1476*/      if (valueType == null) {
/*1477*/        if (parent.isExprResult())
/*1478*/          this.stubDeclarations.add(new TypedScopeCreator.StubDeclaration(n, (t.getInput() != null && t.getInput().isExtern()), ownerName)); 
/*   0*/        return;
/*   0*/      } 
/*   0*/      boolean inferred = true;
/*1512*/      if (info != null)
/*1514*/        inferred = (!info.hasType() && !info.hasEnumParameterType() && (!info.isConstant() || valueType == null || valueType.isUnknownType()) && !FunctionTypeBuilder.isFunctionTypeDeclaration(info)); 
/*1521*/      if (inferred)
/*1523*/        inferred = (rhsValue == null || !rhsValue.isFunction() || (info == null && this.scope.isDeclared(qName, false))); 
/*1528*/      if (!inferred) {
/*1529*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1530*/        if (ownerType != null) {
/*1533*/          boolean isExtern = (t.getInput() != null && t.getInput().isExtern());
/*1534*/          if ((!ownerType.hasOwnProperty(propName) || ownerType.isPropertyTypeInferred(propName)) && ((isExtern && !ownerType.isNativeObjectType()) || !ownerType.isInstanceType()))
/*1539*/            ownerType.defineDeclaredProperty(propName, valueType, n); 
/*   0*/        } 
/*1545*/        defineSlot(n, parent, valueType, inferred);
/*1546*/      } else if (rhsValue != null && rhsValue.isTrue()) {
/*1548*/        FunctionType ownerType = JSType.toMaybeFunctionType(getObjectSlot(ownerName));
/*1550*/        if (ownerType != null) {
/*1551*/          JSType ownerTypeOfThis = ownerType.getTypeOfThis();
/*1552*/          String delegateName = TypedScopeCreator.this.codingConvention.getDelegateSuperclassName();
/*1553*/          JSType delegateType = (delegateName == null) ? null : TypedScopeCreator.this.typeRegistry.getType(delegateName);
/*1555*/          if (delegateType != null && ownerTypeOfThis.isSubtype(delegateType))
/*1557*/            defineSlot(n, parent, TypedScopeCreator.this.getNativeType(JSTypeNative.BOOLEAN_TYPE), true); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private ObjectType getObjectSlot(String slotName) {
/*1569*/      Scope.Var ownerVar = this.scope.getVar(slotName);
/*1570*/      if (ownerVar != null) {
/*1571*/        JSType ownerVarType = ownerVar.getType();
/*1572*/        return ObjectType.cast((ownerVarType == null) ? null : ownerVarType.restrictByNotNullOrUndefined());
/*   0*/      } 
/*1575*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    void resolveStubDeclarations() {
/*1583*/      for (TypedScopeCreator.StubDeclaration stub : this.stubDeclarations) {
/*1584*/        Node n = stub.node;
/*1585*/        Node parent = n.getParent();
/*1586*/        String qName = n.getQualifiedName();
/*1587*/        String propName = n.getLastChild().getString();
/*1588*/        String ownerName = stub.ownerName;
/*1589*/        boolean isExtern = stub.isExtern;
/*1591*/        if (this.scope.isDeclared(qName, false))
/*   0*/          continue; 
/*1597*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1598*/        ObjectType unknownType = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.UNKNOWN_TYPE);
/*1599*/        defineSlot(n, parent, unknownType, true);
/*1601*/        if (ownerType != null && (isExtern || ownerType.isFunctionPrototypeType())) {
/*1605*/          ownerType.defineInferredProperty(propName, unknownType, n);
/*   0*/          continue;
/*   0*/        } 
/*1608*/        TypedScopeCreator.this.typeRegistry.registerPropertyOnType(propName, (ownerType == null) ? unknownType : ownerType);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private final class CollectProperties extends NodeTraversal.AbstractShallowStatementCallback {
/*   0*/      private final ObjectType thisType;
/*   0*/      
/*   0*/      CollectProperties(ObjectType thisType) {
/*1623*/        this.thisType = thisType;
/*   0*/      }
/*   0*/      
/*   0*/      public void visit(NodeTraversal t, Node n, Node parent) {
/*1628*/        if (n.isExprResult()) {
/*1629*/          Node child = n.getFirstChild();
/*1630*/          switch (child.getType()) {
/*   0*/            case 86:
/*1632*/              maybeCollectMember(t, child.getFirstChild(), child, child.getLastChild());
/*   0*/              break;
/*   0*/            case 33:
/*1636*/              maybeCollectMember(t, child, child, null);
/*   0*/              break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      }
/*   0*/      
/*   0*/      private void maybeCollectMember(NodeTraversal t, Node member, Node nodeWithJsDocInfo, @Nullable Node value) {
/*1644*/        JSDocInfo info = nodeWithJsDocInfo.getJSDocInfo();
/*1649*/        if (info == null || member.getType() != 33 || member.getFirstChild().getType() != 42)
/*   0*/          return; 
/*1655*/        member.getFirstChild().setJSType(this.thisType);
/*1656*/        JSType jsType = TypedScopeCreator.AbstractScopeBuilder.this.getDeclaredType(t.getSourceName(), info, member, value);
/*1657*/        Node name = member.getLastChild();
/*1658*/        if (jsType != null && (name.isName() || name.isString()))
/*1660*/          this.thisType.defineDeclaredProperty(name.getString(), jsType, member); 
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
/*   0*/      if (info == null || member.getType() != 33 || member.getFirstChild().getType() != 42)
/*   0*/        return; 
/*   0*/      member.getFirstChild().setJSType(this.thisType);
/*   0*/      JSType jsType = TypedScopeCreator.AbstractScopeBuilder.this.getDeclaredType(t.getSourceName(), info, member, value);
/*   0*/      Node name = member.getLastChild();
/*   0*/      if (jsType != null && (name.isName() || name.isString()))
/*1660*/        this.thisType.defineDeclaredProperty(name.getString(), jsType, member); 
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
/*1678*/      this.node = node;
/*1679*/      this.isExtern = isExtern;
/*1680*/      this.ownerName = ownerName;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class GlobalScopeBuilder extends AbstractScopeBuilder {
/*   0*/    private GlobalScopeBuilder(Scope scope) {
/*1691*/      super(scope);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1703*/      super.visit(t, n, parent);
/*1705*/      switch (n.getType()) {
/*   0*/        case 118:
/*1709*/          if (n.hasOneChild())
/*1710*/            checkForTypedef(t, n.getFirstChild(), n.getJSDocInfo()); 
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void maybeDeclareQualifiedName(NodeTraversal t, JSDocInfo info, Node n, Node parent, Node rhsValue) {
/*1720*/      checkForTypedef(t, n, info);
/*1721*/      super.maybeDeclareQualifiedName(t, info, n, parent, rhsValue);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForTypedef(NodeTraversal t, Node candidate, JSDocInfo info) {
/*1732*/      if (info == null || !info.hasTypedefType())
/*   0*/        return; 
/*1736*/      String typedef = candidate.getQualifiedName();
/*1737*/      if (typedef == null)
/*   0*/        return; 
/*1744*/      TypedScopeCreator.this.typeRegistry.declareType(typedef, TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*1746*/      JSType realType = info.getTypedefType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/*1747*/      if (realType == null)
/*1748*/        TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), candidate, TypedScopeCreator.MALFORMED_TYPEDEF, new String[] { typedef })); 
/*1753*/      TypedScopeCreator.this.typeRegistry.overwriteDeclaredType(typedef, realType);
/*1754*/      if (candidate.isGetProp())
/*1755*/        defineSlot(candidate, candidate.getParent(), TypedScopeCreator.this.getNativeType(JSTypeNative.NO_TYPE), false); 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class LocalScopeBuilder extends AbstractScopeBuilder {
/*   0*/    private LocalScopeBuilder(Scope scope) {
/*1770*/      super(scope);
/*   0*/    }
/*   0*/    
/*   0*/    void build() {
/*1777*/      NodeTraversal.traverse(TypedScopeCreator.this.compiler, this.scope.getRootNode(), this);
/*1779*/      FunctionTypeBuilder.AstFunctionContents contents = TypedScopeCreator.this.getFunctionAnalysisResults(this.scope.getRootNode());
/*1781*/      if (contents != null)
/*1782*/        for (String varName : contents.getEscapedVarNames()) {
/*1783*/          Scope.Var v = this.scope.getVar(varName);
/*1784*/          Preconditions.checkState((v.getScope() == this.scope));
/*1785*/          v.markEscaped();
/*   0*/        }  
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1799*/      if (n == this.scope.getRootNode())
/*   0*/        return; 
/*1801*/      if (n.isParamList() && parent == this.scope.getRootNode()) {
/*1802*/        handleFunctionInputs(parent);
/*   0*/        return;
/*   0*/      } 
/*1806*/      super.visit(t, n, parent);
/*   0*/    }
/*   0*/    
/*   0*/    private void handleFunctionInputs(Node fnNode) {
/*1812*/      Node fnNameNode = fnNode.getFirstChild();
/*1813*/      String fnName = fnNameNode.getString();
/*1814*/      if (!fnName.isEmpty()) {
/*1815*/        Scope.Var fnVar = this.scope.getVar(fnName);
/*1816*/        if (fnVar == null || (fnVar.getNameNode() != null && fnVar.getInitialValue() != fnNode))
/*1824*/          defineSlot(fnNameNode, fnNode, fnNode.getJSType(), false); 
/*   0*/      } 
/*1828*/      declareArguments(fnNode);
/*   0*/    }
/*   0*/    
/*   0*/    private void declareArguments(Node functionNode) {
/*1835*/      Node astParameters = functionNode.getFirstChild().getNext();
/*1836*/      Node body = astParameters.getNext();
/*1837*/      FunctionType functionType = JSType.toMaybeFunctionType(functionNode.getJSType());
/*1839*/      if (functionType != null) {
/*1840*/        Node jsDocParameters = functionType.getParametersNode();
/*1841*/        if (jsDocParameters != null) {
/*1842*/          Node jsDocParameter = jsDocParameters.getFirstChild();
/*1843*/          for (Node astParameter : astParameters.children()) {
/*1844*/            if (jsDocParameter != null) {
/*1845*/              defineSlot(astParameter, functionNode, jsDocParameter.getJSType(), false);
/*1847*/              jsDocParameter = jsDocParameter.getNext();
/*   0*/              continue;
/*   0*/            } 
/*1849*/            defineSlot(astParameter, functionNode, null, true);
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
/*1868*/      this.compiler = compiler;
/*1869*/      this.data = outParam;
/*   0*/    }
/*   0*/    
/*   0*/    public void process(Node externs, Node root) {
/*1873*/      if (externs == null) {
/*1874*/        NodeTraversal.traverse(this.compiler, root, this);
/*   0*/      } else {
/*1876*/        NodeTraversal.traverseRoots(this.compiler, (List<Node>)ImmutableList.of(externs, root), this);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void enterScope(NodeTraversal t) {
/*1882*/      if (!t.inGlobalScope()) {
/*1883*/        Node n = t.getScopeRoot();
/*1884*/        this.data.put(n, new FunctionTypeBuilder.AstFunctionContents(n));
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1889*/      if (t.inGlobalScope())
/*   0*/        return; 
/*1893*/      if (n.isReturn() && n.getFirstChild() != null) {
/*1894*/        ((FunctionTypeBuilder.AstFunctionContents)this.data.get(t.getScopeRoot())).recordNonEmptyReturn();
/*1895*/      } else if (n.isName() && NodeUtil.isLValue(n)) {
/*1896*/        String name = n.getString();
/*1897*/        Scope scope = t.getScope();
/*1898*/        Scope.Var var = scope.getVar(name);
/*1899*/        if (var != null) {
/*1900*/          Scope ownerScope = var.getScope();
/*1901*/          if (scope != ownerScope && ownerScope.isLocal())
/*1902*/            ((FunctionTypeBuilder.AstFunctionContents)this.data.get(ownerScope.getRootNode())).recordEscapedVarName(name); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private FunctionTypeBuilder.AstFunctionContents getFunctionAnalysisResults(@Nullable Node n) {
/*1910*/    if (n == null)
/*1911*/      return null; 
/*1916*/    return this.functionAnalysisResults.get(n);
/*   0*/  }
/*   0*/}
