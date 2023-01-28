/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
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
/*   0*/import javax.annotation.Nullable;
/*   0*/
/*   0*/final class TypedScopeCreator implements ScopeCreator {
/*  92*/  static final String DELEGATE_PROXY_SUFFIX = ObjectType.createDelegateSuffix("Proxy");
/*   0*/  
/*   0*/  private static final String LEGACY_TYPEDEF = "goog.typedef";
/*   0*/  
/*  97*/  static final DiagnosticType MALFORMED_TYPEDEF = DiagnosticType.warning("JSC_MALFORMED_TYPEDEF", "Typedef for {0} does not have any type information");
/*   0*/  
/* 102*/  static final DiagnosticType ENUM_INITIALIZER = DiagnosticType.warning("JSC_ENUM_INITIALIZER_NOT_ENUM", "enum initializer must be an object literal or an enum");
/*   0*/  
/* 107*/  static final DiagnosticType CTOR_INITIALIZER = DiagnosticType.warning("JSC_CTOR_INITIALIZER_NOT_CTOR", "Constructor {0} must be initialized at declaration");
/*   0*/  
/* 112*/  static final DiagnosticType IFACE_INITIALIZER = DiagnosticType.warning("JSC_IFACE_INITIALIZER_NOT_IFACE", "Interface {0} must be initialized at declaration");
/*   0*/  
/* 117*/  static final DiagnosticType CONSTRUCTOR_EXPECTED = DiagnosticType.warning("JSC_REFLECT_CONSTRUCTOR_EXPECTED", "Constructor expected as first argument");
/*   0*/  
/* 122*/  static final DiagnosticType UNKNOWN_LENDS = DiagnosticType.warning("JSC_UNKNOWN_LENDS", "Variable {0} not declared before @lends annotation.");
/*   0*/  
/* 127*/  static final DiagnosticType LENDS_ON_NON_OBJECT = DiagnosticType.warning("JSC_LENDS_ON_NON_OBJECT", "May only lend properties to object types. {0} has type {1}.");
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
/* 137*/  private final List<ObjectType> delegateProxyPrototypes = Lists.newArrayList();
/*   0*/  
/*   0*/  private class DeferredSetType {
/*   0*/    final Node node;
/*   0*/    
/*   0*/    final JSType type;
/*   0*/    
/*   0*/    DeferredSetType(Node node, JSType type) {
/* 148*/      Preconditions.checkNotNull(node);
/* 149*/      Preconditions.checkNotNull(type);
/* 150*/      this.node = node;
/* 151*/      this.type = type;
/* 155*/      node.setJSType(type);
/*   0*/    }
/*   0*/    
/*   0*/    void resolve(Scope scope) {
/* 159*/      this.node.setJSType(this.type.resolve(TypedScopeCreator.this.typeParsingErrorReporter, scope));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  TypedScopeCreator(AbstractCompiler compiler) {
/* 164*/    this(compiler, compiler.getCodingConvention());
/*   0*/  }
/*   0*/  
/*   0*/  TypedScopeCreator(AbstractCompiler compiler, CodingConvention codingConvention) {
/* 169*/    this.compiler = compiler;
/* 170*/    this.validator = compiler.getTypeValidator();
/* 171*/    this.codingConvention = codingConvention;
/* 172*/    this.typeRegistry = compiler.getTypeRegistry();
/* 173*/    this.typeParsingErrorReporter = this.typeRegistry.getErrorReporter();
/*   0*/  }
/*   0*/  
/*   0*/  public Scope createScope(Node root, Scope parent) {
/* 184*/    Scope newScope = null;
/* 185*/    AbstractScopeBuilder scopeBuilder = null;
/* 186*/    if (parent == null) {
/* 188*/      newScope = createInitialScope(root);
/* 190*/      GlobalScopeBuilder globalScopeBuilder = new GlobalScopeBuilder(newScope);
/* 191*/      scopeBuilder = globalScopeBuilder;
/* 192*/      NodeTraversal.traverse(this.compiler, root, scopeBuilder);
/*   0*/    } else {
/* 194*/      newScope = new Scope(parent, root);
/* 195*/      LocalScopeBuilder localScopeBuilder = new LocalScopeBuilder(newScope);
/* 196*/      scopeBuilder = localScopeBuilder;
/* 197*/      localScopeBuilder.build();
/*   0*/    } 
/* 200*/    scopeBuilder.resolveStubDeclarations();
/* 201*/    scopeBuilder.resolveTypes();
/* 206*/    for (Node functionNode : scopeBuilder.nonExternFunctions) {
/* 207*/      JSType type = functionNode.getJSType();
/* 208*/      if (type != null && type instanceof FunctionType) {
/* 209*/        FunctionType fnType = (FunctionType)type;
/* 210*/        ObjectType fnThisType = fnType.getTypeOfThis();
/* 211*/        if (!fnThisType.isUnknownType()) {
/* 212*/          scopeBuilder.getClass();
/* 212*/          NodeTraversal.traverse(this.compiler, functionNode.getLastChild(), new AbstractScopeBuilder.CollectProperties(fnThisType));
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 218*/    if (parent == null)
/* 219*/      this.codingConvention.defineDelegateProxyPrototypeProperties(this.typeRegistry, newScope, this.delegateProxyPrototypes); 
/* 222*/    return newScope;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  Scope createInitialScope(Node root) {
/* 232*/    NodeTraversal.traverse(this.compiler, root, new DiscoverEnumsAndTypedefs(this.typeRegistry));
/* 235*/    Scope s = new Scope(root, this.compiler);
/* 236*/    declareNativeFunctionType(s, JSTypeNative.ARRAY_FUNCTION_TYPE);
/* 237*/    declareNativeFunctionType(s, JSTypeNative.BOOLEAN_OBJECT_FUNCTION_TYPE);
/* 238*/    declareNativeFunctionType(s, JSTypeNative.DATE_FUNCTION_TYPE);
/* 239*/    declareNativeFunctionType(s, JSTypeNative.ERROR_FUNCTION_TYPE);
/* 240*/    declareNativeFunctionType(s, JSTypeNative.EVAL_ERROR_FUNCTION_TYPE);
/* 241*/    declareNativeFunctionType(s, JSTypeNative.FUNCTION_FUNCTION_TYPE);
/* 242*/    declareNativeFunctionType(s, JSTypeNative.NUMBER_OBJECT_FUNCTION_TYPE);
/* 243*/    declareNativeFunctionType(s, JSTypeNative.OBJECT_FUNCTION_TYPE);
/* 244*/    declareNativeFunctionType(s, JSTypeNative.RANGE_ERROR_FUNCTION_TYPE);
/* 245*/    declareNativeFunctionType(s, JSTypeNative.REFERENCE_ERROR_FUNCTION_TYPE);
/* 246*/    declareNativeFunctionType(s, JSTypeNative.REGEXP_FUNCTION_TYPE);
/* 247*/    declareNativeFunctionType(s, JSTypeNative.STRING_OBJECT_FUNCTION_TYPE);
/* 248*/    declareNativeFunctionType(s, JSTypeNative.SYNTAX_ERROR_FUNCTION_TYPE);
/* 249*/    declareNativeFunctionType(s, JSTypeNative.TYPE_ERROR_FUNCTION_TYPE);
/* 250*/    declareNativeFunctionType(s, JSTypeNative.URI_ERROR_FUNCTION_TYPE);
/* 251*/    declareNativeValueType(s, "undefined", JSTypeNative.VOID_TYPE);
/* 256*/    declareNativeValueType(s, "goog.typedef", JSTypeNative.NO_TYPE);
/* 261*/    declareNativeValueType(s, "ActiveXObject", JSTypeNative.NO_OBJECT_TYPE);
/* 263*/    return s;
/*   0*/  }
/*   0*/  
/*   0*/  private void declareNativeFunctionType(Scope scope, JSTypeNative tId) {
/* 267*/    FunctionType t = this.typeRegistry.getNativeFunctionType(tId);
/* 268*/    declareNativeType(scope, t.getInstanceType().getReferenceName(), t);
/* 269*/    declareNativeType(scope, t.getPrototype().getReferenceName(), t.getPrototype());
/*   0*/  }
/*   0*/  
/*   0*/  private void declareNativeValueType(Scope scope, String name, JSTypeNative tId) {
/* 275*/    declareNativeType(scope, name, this.typeRegistry.getNativeType(tId));
/*   0*/  }
/*   0*/  
/*   0*/  private void declareNativeType(Scope scope, String name, JSType t) {
/* 279*/    scope.declare(name, null, t, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static class DiscoverEnumsAndTypedefs extends NodeTraversal.AbstractShallowStatementCallback {
/*   0*/    private final JSTypeRegistry registry;
/*   0*/    
/*   0*/    DiscoverEnumsAndTypedefs(JSTypeRegistry registry) {
/* 287*/      this.registry = registry;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node node, Node parent) {
/*   0*/      Node child, firstChild;
/* 292*/      Node nameNode = null;
/* 293*/      switch (node.getType()) {
/*   0*/        case 118:
/* 295*/          child = node.getFirstChild();
/* 296*/          for (; child != null; child = child.getNext())
/* 297*/            identifyNameNode(child, child.getFirstChild(), NodeUtil.getInfoForNameNode(child)); 
/*   0*/          break;
/*   0*/        case 130:
/* 303*/          firstChild = node.getFirstChild();
/* 304*/          if (firstChild.getType() == 86) {
/* 305*/            identifyNameNode(firstChild.getFirstChild(), firstChild.getLastChild(), firstChild.getJSDocInfo());
/*   0*/            break;
/*   0*/          } 
/* 309*/          identifyNameNode(firstChild, null, firstChild.getJSDocInfo());
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void identifyNameNode(Node nameNode, Node valueNode, JSDocInfo info) {
/* 318*/      if (nameNode.isQualifiedName()) {
/* 319*/        if (info != null)
/* 320*/          if (info.hasEnumParameterType()) {
/* 321*/            this.registry.identifyNonNullableName(nameNode.getQualifiedName());
/* 322*/          } else if (info.hasTypedefType()) {
/* 323*/            this.registry.identifyNonNullableName(nameNode.getQualifiedName());
/*   0*/          }  
/* 327*/        if (valueNode != null && "goog.typedef".equals(valueNode.getQualifiedName()))
/* 329*/          this.registry.identifyNonNullableName(nameNode.getQualifiedName()); 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getPrototypePropertyOwner(Node n) {
/* 341*/    if (n.getType() == 33) {
/* 342*/      Node firstChild = n.getFirstChild();
/* 343*/      if (firstChild.getType() == 33 && firstChild.getLastChild().getString().equals("prototype")) {
/* 345*/        Node maybeOwner = firstChild.getFirstChild();
/* 346*/        if (maybeOwner.isQualifiedName())
/* 347*/          return maybeOwner; 
/*   0*/      } 
/*   0*/    } 
/* 351*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative nativeType) {
/* 355*/    return this.typeRegistry.getNativeType(nativeType);
/*   0*/  }
/*   0*/  
/*   0*/  private abstract class AbstractScopeBuilder implements NodeTraversal.Callback {
/*   0*/    final Scope scope;
/*   0*/    
/* 366*/    private final List<TypedScopeCreator.DeferredSetType> deferredSetTypes = Lists.newArrayList();
/*   0*/    
/* 372*/    private final List<Node> nonExternFunctions = Lists.newArrayList();
/*   0*/    
/* 380*/    private final List<TypedScopeCreator.StubDeclaration> stubDeclarations = Lists.newArrayList();
/*   0*/    
/* 386*/    private String sourceName = null;
/*   0*/    
/*   0*/    private AbstractScopeBuilder(Scope scope) {
/* 389*/      this.scope = scope;
/*   0*/    }
/*   0*/    
/*   0*/    void setDeferredType(Node node, JSType type) {
/* 393*/      this.deferredSetTypes.add(new TypedScopeCreator.DeferredSetType(node, type));
/*   0*/    }
/*   0*/    
/*   0*/    void resolveTypes() {
/* 398*/      for (TypedScopeCreator.DeferredSetType deferred : this.deferredSetTypes)
/* 399*/        deferred.resolve(this.scope); 
/* 403*/      Iterator<Scope.Var> vars = this.scope.getVars();
/* 404*/      while (vars.hasNext())
/* 405*/        ((Scope.Var)vars.next()).resolveType(TypedScopeCreator.this.typeParsingErrorReporter); 
/* 410*/      TypedScopeCreator.this.typeRegistry.resolveTypesInScope(this.scope);
/*   0*/    }
/*   0*/    
/*   0*/    public final boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
/* 416*/      if (n.getType() == 105 || n.getType() == 132)
/* 418*/        this.sourceName = NodeUtil.getSourceName(n); 
/* 423*/      boolean descend = (parent == null || parent.getType() != 105 || n == parent.getFirstChild() || parent == this.scope.getRootNode());
/* 426*/      if (descend)
/* 429*/        if (NodeUtil.isStatementParent(n)) {
/* 430*/          Node child = n.getFirstChild();
/* 431*/          for (; child != null; 
/* 432*/            child = child.getNext()) {
/* 433*/            if (NodeUtil.isHoistedFunctionDeclaration(child))
/* 434*/              defineFunctionLiteral(child, n); 
/*   0*/          } 
/*   0*/        }  
/* 440*/      return descend;
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/      Node firstChild;
/* 445*/      attachLiteralTypes(t, n);
/* 447*/      switch (n.getType()) {
/*   0*/        case 37:
/* 449*/          checkForClassDefiningCalls(t, n, parent);
/*   0*/          break;
/*   0*/        case 105:
/* 453*/          if (t.getInput() == null || !t.getInput().isExtern())
/* 454*/            this.nonExternFunctions.add(n); 
/* 458*/          if (!NodeUtil.isHoistedFunctionDeclaration(n))
/* 459*/            defineFunctionLiteral(n, parent); 
/*   0*/          break;
/*   0*/        case 86:
/* 465*/          firstChild = n.getFirstChild();
/* 466*/          if (firstChild.getType() == 33 && firstChild.isQualifiedName())
/* 468*/            maybeDeclareQualifiedName(t, n.getJSDocInfo(), firstChild, n, firstChild.getNext()); 
/*   0*/          break;
/*   0*/        case 120:
/* 474*/          defineCatch(n, parent);
/*   0*/          break;
/*   0*/        case 118:
/* 478*/          defineVar(n, parent);
/*   0*/          break;
/*   0*/        case 33:
/* 483*/          if (parent.getType() == 130 && n.isQualifiedName())
/* 485*/            maybeDeclareQualifiedName(t, n.getJSDocInfo(), n, parent, null); 
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void attachLiteralTypes(NodeTraversal t, Node n) {
/* 492*/      switch (n.getType()) {
/*   0*/        case 41:
/* 494*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.NULL_TYPE));
/*   0*/          break;
/*   0*/        case 122:
/* 498*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.VOID_TYPE));
/*   0*/          break;
/*   0*/        case 40:
/* 503*/          if (!NodeUtil.isObjectLitKey(n, n.getParent()))
/* 504*/            n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.STRING_TYPE)); 
/*   0*/          break;
/*   0*/        case 39:
/* 509*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.NUMBER_TYPE));
/*   0*/          break;
/*   0*/        case 43:
/*   0*/        case 44:
/* 514*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.BOOLEAN_TYPE));
/*   0*/          break;
/*   0*/        case 47:
/* 518*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.REGEXP_TYPE));
/*   0*/          break;
/*   0*/        case 69:
/* 522*/          n.setJSType(TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/          break;
/*   0*/        case 64:
/* 526*/          defineObjectLiteral(t, n);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void defineObjectLiteral(NodeTraversal t, Node objectLit) {
/* 536*/      JSType type = null;
/* 537*/      JSDocInfo info = objectLit.getJSDocInfo();
/* 538*/      if (info != null && info.getLendsName() != null) {
/* 540*/        String lendsName = info.getLendsName();
/* 541*/        Scope.Var lendsVar = this.scope.getVar(lendsName);
/* 542*/        if (lendsVar == null) {
/* 543*/          TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, objectLit, TypedScopeCreator.UNKNOWN_LENDS, new String[] { lendsName }));
/*   0*/        } else {
/* 546*/          type = lendsVar.getType();
/* 547*/          if (type == null)
/* 548*/            type = TypedScopeCreator.this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/* 550*/          if (!type.isSubtype(TypedScopeCreator.this.typeRegistry.getNativeType(JSTypeNative.OBJECT_TYPE))) {
/* 551*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, objectLit, TypedScopeCreator.LENDS_ON_NON_OBJECT, new String[] { lendsName, type.toString() }));
/* 554*/            type = null;
/*   0*/          } else {
/* 556*/            objectLit.setJSType(type);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 561*/      info = TypedScopeCreator.getBestJSDocInfo(objectLit);
/* 562*/      Node lValue = TypedScopeCreator.getBestLValue(objectLit);
/* 563*/      String lValueName = TypedScopeCreator.getBestLValueName(lValue);
/*   0*/      boolean createdEnumType = false;
/* 565*/      if (info != null && info.hasEnumParameterType()) {
/* 566*/        type = createEnumTypeFromNodes(objectLit, lValueName, info, lValue);
/* 567*/        createdEnumType = true;
/*   0*/      } 
/* 570*/      if (type == null)
/* 571*/        type = TypedScopeCreator.this.typeRegistry.createAnonymousObjectType(); 
/* 574*/      setDeferredType(objectLit, type);
/* 577*/      if (!createdEnumType)
/* 578*/        processObjectLitProperties(t, objectLit, ObjectType.cast(objectLit.getJSType())); 
/*   0*/    }
/*   0*/    
/*   0*/    void processObjectLitProperties(NodeTraversal t, Node objLit, ObjectType objLitType) {
/* 591*/      for (Node keyNode = objLit.getFirstChild(); keyNode != null; 
/* 592*/        keyNode = keyNode.getNext()) {
/* 593*/        Node value = keyNode.getFirstChild();
/* 594*/        String memberName = NodeUtil.getObjectLitKeyName(keyNode);
/* 595*/        JSDocInfo info = keyNode.getJSDocInfo();
/* 596*/        JSType valueType = getDeclaredType(t.getSourceName(), info, keyNode, value);
/* 598*/        JSType keyType = NodeUtil.getObjectLitKeyTypeFromValueType(keyNode, valueType);
/* 600*/        if (keyType != null) {
/* 603*/          String qualifiedName = TypedScopeCreator.getBestLValueName(keyNode);
/* 604*/          if (qualifiedName != null) {
/* 605*/            defineSlot(keyNode, objLit, qualifiedName, keyType, false);
/*   0*/          } else {
/* 607*/            setDeferredType(keyNode, keyType);
/*   0*/          } 
/* 610*/          if (objLitType != null) {
/* 612*/            boolean isExtern = (t.getInput() != null && t.getInput().isExtern());
/* 613*/            objLitType.defineDeclaredProperty(memberName, keyType, isExtern, keyNode);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private JSType getDeclaredTypeInAnnotation(String sourceName, Node node, JSDocInfo info) {
/* 628*/      JSType jsType = null;
/* 629*/      Node objNode = (node.getType() == 33) ? node.getFirstChild() : (NodeUtil.isObjectLitKey(node, node.getParent()) ? node.getParent() : null);
/* 633*/      if (info != null)
/* 634*/        if (info.hasType()) {
/* 635*/          jsType = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 636*/        } else if (FunctionTypeBuilder.isFunctionTypeDeclaration(info)) {
/* 637*/          String fnName = node.getQualifiedName();
/* 638*/          jsType = createFunctionTypeFromNodes(null, fnName, info, node);
/*   0*/        }  
/* 642*/      return jsType;
/*   0*/    }
/*   0*/    
/*   0*/    void assertDefinitionNode(Node n, int type) {
/* 650*/      Preconditions.checkState((this.sourceName != null));
/* 651*/      Preconditions.checkState((n.getType() == type));
/*   0*/    }
/*   0*/    
/*   0*/    void defineCatch(Node n, Node parent) {
/* 658*/      assertDefinitionNode(n, 120);
/* 659*/      Node catchName = n.getFirstChild();
/* 660*/      defineSlot(catchName, n, null);
/*   0*/    }
/*   0*/    
/*   0*/    void defineVar(Node n, Node parent) {
/* 667*/      assertDefinitionNode(n, 118);
/* 668*/      JSDocInfo info = n.getJSDocInfo();
/* 669*/      if (n.hasMoreThanOneChild()) {
/* 670*/        if (info != null)
/* 672*/          TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, TypeCheck.MULTIPLE_VAR_DEF, new String[0])); 
/* 674*/        for (Node name : n.children())
/* 675*/          defineName(name, n, parent, name.getJSDocInfo()); 
/*   0*/      } else {
/* 678*/        Node name = n.getFirstChild();
/* 679*/        defineName(name, n, parent, (info != null) ? info : name.getJSDocInfo());
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void defineFunctionLiteral(Node n, Node parent) {
/* 688*/      assertDefinitionNode(n, 105);
/* 692*/      Node lValue = TypedScopeCreator.getBestLValue(n);
/* 693*/      JSDocInfo info = TypedScopeCreator.getBestJSDocInfo(n);
/* 694*/      String functionName = TypedScopeCreator.getBestLValueName(lValue);
/* 695*/      FunctionType functionType = createFunctionTypeFromNodes(n, functionName, info, lValue);
/* 699*/      setDeferredType(n, functionType);
/* 704*/      if (NodeUtil.isFunctionDeclaration(n))
/* 705*/        defineSlot(n.getFirstChild(), n, functionType); 
/*   0*/    }
/*   0*/    
/*   0*/    private void defineName(Node name, Node var, Node parent, JSDocInfo info) {
/* 719*/      Node value = name.getFirstChild();
/* 722*/      JSType type = getDeclaredType(this.sourceName, info, name, value);
/* 723*/      if (type == null) {
/* 725*/        CompilerInput input = TypedScopeCreator.this.compiler.getInput(this.sourceName);
/* 726*/        Preconditions.checkNotNull(input, this.sourceName);
/* 727*/        type = input.isExtern() ? TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE) : null;
/*   0*/      } 
/* 730*/      defineSlot(name, var, type);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean shouldUseFunctionLiteralType(FunctionType type, JSDocInfo info, Node lValue) {
/* 742*/      if (info != null)
/* 743*/        return true; 
/* 745*/      if (lValue != null && NodeUtil.isObjectLitKey(lValue, lValue.getParent()))
/* 747*/        return false; 
/* 749*/      return (this.scope.isGlobal() || !type.isReturnTypeInferred());
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType createFunctionTypeFromNodes(@Nullable Node rValue, @Nullable String name, @Nullable JSDocInfo info, @Nullable Node lvalueNode) {
/* 780*/      FunctionType functionType = null;
/* 783*/      if (rValue != null && rValue.isQualifiedName() && this.scope.isGlobal()) {
/* 784*/        Scope.Var var = this.scope.getVar(rValue.getQualifiedName());
/* 785*/        if (var != null && var.getType() instanceof FunctionType) {
/* 786*/          FunctionType aliasedType = (FunctionType)var.getType();
/* 787*/          if ((aliasedType.isConstructor() || aliasedType.isInterface()) && !aliasedType.isNativeObjectType()) {
/* 789*/            functionType = aliasedType;
/* 791*/            if (name != null && this.scope.isGlobal())
/* 792*/              TypedScopeCreator.this.typeRegistry.declareType(name, functionType.getInstanceType()); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 798*/      if (functionType == null) {
/* 799*/        Node errorRoot = (rValue == null) ? lvalueNode : rValue;
/* 800*/        boolean isFnLiteral = (rValue != null && rValue.getType() == 105);
/* 802*/        Node fnRoot = isFnLiteral ? rValue : null;
/* 803*/        Node parametersNode = isFnLiteral ? rValue.getFirstChild().getNext() : null;
/* 805*/        Node fnBlock = isFnLiteral ? parametersNode.getNext() : null;
/* 807*/        if (info != null && info.hasType()) {
/* 808*/          JSType type = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 811*/          type = type.restrictByNotNullOrUndefined();
/* 812*/          if (type.isFunctionType()) {
/* 813*/            functionType = (FunctionType)type;
/* 814*/            functionType.setJSDocInfo(info);
/*   0*/          } 
/*   0*/        } 
/* 818*/        if (functionType == null) {
/* 820*/          FunctionType overriddenPropType = null;
/* 821*/          if (lvalueNode != null && lvalueNode.getType() == 33 && lvalueNode.isQualifiedName()) {
/* 824*/            Scope.Var var = this.scope.getVar(lvalueNode.getFirstChild().getQualifiedName());
/* 826*/            if (var != null) {
/* 827*/              ObjectType ownerType = ObjectType.cast(var.getType());
/* 828*/              if (ownerType != null) {
/* 829*/                String propName = lvalueNode.getLastChild().getString();
/* 830*/                overriddenPropType = findOverriddenFunction(ownerType, propName);
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/* 836*/          FunctionTypeBuilder builder = new FunctionTypeBuilder(name, TypedScopeCreator.this.compiler, errorRoot, this.sourceName, this.scope).setSourceNode(fnRoot).inferFromOverriddenFunction(overriddenPropType, parametersNode).inferTemplateTypeName(info).inferReturnType(info).inferInheritance(info);
/*   0*/          boolean searchedForThisType = false;
/* 847*/          if (lvalueNode != null && lvalueNode.getType() == 33) {
/* 849*/            Node objNode = lvalueNode.getFirstChild();
/* 850*/            if (objNode.getType() == 33 && objNode.getLastChild().getString().equals("prototype")) {
/* 852*/              builder.inferThisType(info, objNode.getFirstChild());
/* 853*/              searchedForThisType = true;
/* 854*/            } else if (objNode.getType() == 42) {
/* 855*/              builder.inferThisType(info, objNode.getJSType());
/* 856*/              searchedForThisType = true;
/*   0*/            } 
/*   0*/          } 
/* 860*/          if (!searchedForThisType)
/* 861*/            builder.inferThisType(info, (Node)null); 
/* 864*/          functionType = builder.inferParameterTypes(parametersNode, info).inferReturnStatementsAsLastResort(fnBlock).buildAndRegister();
/*   0*/        } 
/*   0*/      } 
/* 872*/      return functionType;
/*   0*/    }
/*   0*/    
/*   0*/    private FunctionType findOverriddenFunction(ObjectType ownerType, String propName) {
/* 882*/      JSType propType = ownerType.getPropertyType(propName);
/* 883*/      if (propType instanceof FunctionType)
/* 884*/        return (FunctionType)propType; 
/* 889*/      for (ObjectType iface : ownerType.getCtorImplementedInterfaces()) {
/* 890*/        propType = iface.getPropertyType(propName);
/* 891*/        if (propType instanceof FunctionType)
/* 892*/          return (FunctionType)propType; 
/*   0*/      } 
/* 897*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    private EnumType createEnumTypeFromNodes(Node rValue, String name, JSDocInfo info, Node lValueNode) {
/* 919*/      Preconditions.checkNotNull(info);
/* 920*/      Preconditions.checkState(info.hasEnumParameterType());
/* 922*/      EnumType enumType = null;
/* 923*/      if (rValue != null && rValue.isQualifiedName()) {
/* 925*/        Scope.Var var = this.scope.getVar(rValue.getQualifiedName());
/* 926*/        if (var != null && var.getType() instanceof EnumType)
/* 927*/          enumType = (EnumType)var.getType(); 
/*   0*/      } 
/* 931*/      if (enumType == null) {
/* 932*/        JSType elementsType = info.getEnumParameterType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/* 934*/        enumType = TypedScopeCreator.this.typeRegistry.createEnumType(name, elementsType);
/* 936*/        if (rValue != null && rValue.getType() == 64) {
/* 938*/          Node key = rValue.getFirstChild();
/* 939*/          while (key != null) {
/* 940*/            String keyName = NodeUtil.getStringValue(key);
/* 941*/            if (keyName == null) {
/* 943*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_NOT_CONSTANT, new String[] { keyName }));
/* 945*/            } else if (enumType.hasOwnProperty(keyName)) {
/* 946*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_DUP, new String[] { keyName }));
/* 947*/            } else if (!TypedScopeCreator.this.codingConvention.isValidEnumKey(keyName)) {
/* 948*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, key, TypeCheck.ENUM_NOT_CONSTANT, new String[] { keyName }));
/*   0*/            } else {
/* 951*/              enumType.defineElement(keyName, key);
/*   0*/            } 
/* 953*/            key = key.getNext();
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 958*/      if (name != null && this.scope.isGlobal())
/* 959*/        TypedScopeCreator.this.typeRegistry.declareType(name, enumType.getElementsType()); 
/* 962*/      return enumType;
/*   0*/    }
/*   0*/    
/*   0*/    private void defineSlot(Node name, Node parent, JSType type) {
/* 974*/      defineSlot(name, parent, type, (type == null));
/*   0*/    }
/*   0*/    
/*   0*/    void defineSlot(Node n, Node parent, JSType type, boolean inferred) {
/* 990*/      Preconditions.checkArgument((inferred || type != null));
/* 994*/      if (n.getType() == 38) {
/* 995*/        Preconditions.checkArgument((parent.getType() == 105 || parent.getType() == 118 || parent.getType() == 83 || parent.getType() == 120));
/*   0*/      } else {
/*1001*/        Preconditions.checkArgument((n.getType() == 33 && (parent.getType() == 86 || parent.getType() == 130)));
/*   0*/      } 
/*1006*/      defineSlot(n, parent, n.getQualifiedName(), type, inferred);
/*   0*/    }
/*   0*/    
/*   0*/    void defineSlot(Node n, Node parent, String variableName, JSType type, boolean inferred) {
/*1022*/      Preconditions.checkArgument(!variableName.isEmpty());
/*1024*/      boolean isGlobalVar = (n.getType() == 38 && this.scope.isGlobal());
/*1025*/      boolean shouldDeclareOnGlobalThis = (isGlobalVar && (parent.getType() == 118 || parent.getType() == 105));
/*1033*/      Scope scopeToDeclareIn = this.scope;
/*1034*/      if (n.getType() == 33 && !this.scope.isGlobal() && isQnameRootedInGlobalScope(n)) {
/*1036*/        Scope globalScope = this.scope.getGlobalScope();
/*1040*/        if (!globalScope.isDeclared(variableName, false))
/*1041*/          scopeToDeclareIn = this.scope.getGlobalScope(); 
/*   0*/      } 
/*1046*/      if (scopeToDeclareIn.isDeclared(variableName, false)) {
/*1047*/        Scope.Var oldVar = scopeToDeclareIn.getVar(variableName);
/*1048*/        TypedScopeCreator.this.validator.expectUndeclaredVariable(this.sourceName, n, parent, oldVar, variableName, type);
/*   0*/      } else {
/*1051*/        if (!inferred)
/*1052*/          setDeferredType(n, type); 
/*1054*/        CompilerInput input = TypedScopeCreator.this.compiler.getInput(this.sourceName);
/*1055*/        boolean isExtern = input.isExtern();
/*1056*/        Scope.Var newVar = scopeToDeclareIn.declare(variableName, n, type, input, inferred);
/*1059*/        if (shouldDeclareOnGlobalThis) {
/*1060*/          ObjectType globalThis = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.GLOBAL_THIS);
/*1062*/          if (inferred) {
/*1063*/            globalThis.defineInferredProperty(variableName, (type == null) ? TypedScopeCreator.this.getNativeType(JSTypeNative.NO_TYPE) : type, isExtern, n);
/*   0*/          } else {
/*1069*/            globalThis.defineDeclaredProperty(variableName, type, isExtern, n);
/*   0*/          } 
/*   0*/        } 
/*1073*/        if (type instanceof EnumType) {
/*1074*/          Node initialValue = newVar.getInitialValue();
/*1075*/          boolean isValidValue = (initialValue != null && (initialValue.getType() == 64 || initialValue.isQualifiedName()));
/*1078*/          if (!isValidValue)
/*1079*/            TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, TypedScopeCreator.ENUM_INITIALIZER, new String[0])); 
/*   0*/        } 
/*1084*/        if (type instanceof FunctionType && !type.isEmptyType()) {
/*1087*/          FunctionType fnType = (FunctionType)type;
/*1088*/          if ((fnType.isConstructor() || fnType.isInterface()) && !fnType.equals(TypedScopeCreator.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE))) {
/*1091*/            FunctionType superClassCtor = fnType.getSuperClassConstructor();
/*1092*/            scopeToDeclareIn.declare(variableName + ".prototype", n, fnType.getPrototype(), input, (superClassCtor == null || superClassCtor.getInstanceType().equals(TypedScopeCreator.this.getNativeType(JSTypeNative.OBJECT_TYPE))));
/*1101*/            if (newVar.getInitialValue() == null && !isExtern && variableName.equals(fnType.getInstanceType().getReferenceName()))
/*1111*/              TypedScopeCreator.this.compiler.report(JSError.make(this.sourceName, n, fnType.isConstructor() ? TypedScopeCreator.CTOR_INITIALIZER : TypedScopeCreator.IFACE_INITIALIZER, new String[] { variableName })); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1121*/      if (isGlobalVar && "Window".equals(variableName) && type instanceof FunctionType && type.isConstructor()) {
/*1124*/        FunctionType globalThisCtor = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.GLOBAL_THIS).getConstructor();
/*1126*/        globalThisCtor.getInstanceType().clearCachedValues();
/*1127*/        globalThisCtor.getPrototype().clearCachedValues();
/*1128*/        globalThisCtor.setPrototypeBasedOn(((FunctionType)type).getInstanceType());
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean isQnameRootedInGlobalScope(Node n) {
/*1137*/      Node root = NodeUtil.getRootOfQualifiedName(n);
/*1138*/      if (root.getType() == 38) {
/*1139*/        Scope.Var var = this.scope.getVar(root.getString());
/*1140*/        if (var != null)
/*1141*/          return var.isGlobal(); 
/*   0*/      } 
/*1144*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    private JSType getDeclaredType(String sourceName, JSDocInfo info, Node lValue, @Nullable Node rValue) {
/*1158*/      if (info != null && info.hasType())
/*1159*/        return getDeclaredTypeInAnnotation(sourceName, lValue, info); 
/*1160*/      if (rValue != null && rValue.getType() == 105 && shouldUseFunctionLiteralType((FunctionType)rValue.getJSType(), info, lValue))
/*1163*/        return rValue.getJSType(); 
/*1164*/      if (info != null) {
/*1165*/        if (info.hasEnumParameterType()) {
/*1166*/          if (rValue != null && rValue.getType() == 64)
/*1167*/            return rValue.getJSType(); 
/*1169*/          return createEnumTypeFromNodes(rValue, lValue.getQualifiedName(), info, lValue);
/*   0*/        } 
/*1172*/        if (info.isConstructor() || info.isInterface())
/*1173*/          return createFunctionTypeFromNodes(rValue, lValue.getQualifiedName(), info, lValue); 
/*1177*/        if (info.isConstant()) {
/*1178*/          JSType knownType = null;
/*1179*/          if (rValue != null) {
/*1180*/            if (rValue.getJSType() != null && !rValue.getJSType().isUnknownType())
/*1182*/              return rValue.getJSType(); 
/*1183*/            if (rValue.getType() == 100) {
/*1188*/              Node firstClause = rValue.getFirstChild();
/*1189*/              Node secondClause = firstClause.getNext();
/*1190*/              boolean namesMatch = (firstClause.getType() == 38 && lValue.getType() == 38 && firstClause.getString().equals(lValue.getString()));
/*1193*/              if (namesMatch && secondClause.getJSType() != null && !secondClause.getJSType().isUnknownType())
/*1195*/                return secondClause.getJSType(); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1203*/      return getDeclaredTypeInAnnotation(sourceName, lValue, info);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForClassDefiningCalls(NodeTraversal t, Node n, Node parent) {
/*1213*/      CodingConvention.SubclassRelationship relationship = TypedScopeCreator.this.codingConvention.getClassesDefinedByCall(n);
/*1215*/      if (relationship != null) {
/*1216*/        ObjectType superClass = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(relationship.superclassName));
/*1218*/        ObjectType subClass = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(relationship.subclassName));
/*1220*/        if (superClass != null && subClass != null) {
/*1221*/          FunctionType superCtor = superClass.getConstructor();
/*1222*/          FunctionType subCtor = subClass.getConstructor();
/*1224*/          if (relationship.type == CodingConvention.SubclassType.INHERITS)
/*1225*/            TypedScopeCreator.this.validator.expectSuperType(t, n, superClass, subClass); 
/*1228*/          if (superCtor != null && subCtor != null)
/*1229*/            TypedScopeCreator.this.codingConvention.applySubclassRelationship(superCtor, subCtor, relationship.type); 
/*   0*/        } 
/*   0*/      } 
/*1235*/      String singletonGetterClassName = TypedScopeCreator.this.codingConvention.getSingletonGetterClassName(n);
/*1237*/      if (singletonGetterClassName != null) {
/*1238*/        ObjectType objectType = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(singletonGetterClassName));
/*1240*/        if (objectType != null) {
/*1241*/          FunctionType functionType = objectType.getConstructor();
/*1243*/          if (functionType != null) {
/*1244*/            FunctionType getterType = TypedScopeCreator.this.typeRegistry.createFunctionType(objectType, new JSType[0]);
/*1246*/            TypedScopeCreator.this.codingConvention.applySingletonGetter(functionType, getterType, objectType);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1252*/      CodingConvention.DelegateRelationship delegateRelationship = TypedScopeCreator.this.codingConvention.getDelegateRelationship(n);
/*1254*/      if (delegateRelationship != null)
/*1255*/        applyDelegateRelationship(delegateRelationship); 
/*1258*/      CodingConvention.ObjectLiteralCast objectLiteralCast = TypedScopeCreator.this.codingConvention.getObjectLiteralCast(t, n);
/*1260*/      if (objectLiteralCast != null) {
/*1261*/        ObjectType type = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(objectLiteralCast.typeName));
/*1263*/        if (type != null && type.getConstructor() != null) {
/*1264*/          setDeferredType(objectLiteralCast.objectNode, type);
/*   0*/        } else {
/*1266*/          TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), n, TypedScopeCreator.CONSTRUCTOR_EXPECTED, new String[0]));
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void applyDelegateRelationship(CodingConvention.DelegateRelationship delegateRelationship) {
/*1277*/      ObjectType delegatorObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(delegateRelationship.delegator));
/*1279*/      ObjectType delegateBaseObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(delegateRelationship.delegateBase));
/*1281*/      ObjectType delegateSuperObject = ObjectType.cast(TypedScopeCreator.this.typeRegistry.getType(TypedScopeCreator.this.codingConvention.getDelegateSuperclassName()));
/*1283*/      if (delegatorObject != null && delegateBaseObject != null && delegateSuperObject != null) {
/*1286*/        FunctionType delegatorCtor = delegatorObject.getConstructor();
/*1287*/        FunctionType delegateBaseCtor = delegateBaseObject.getConstructor();
/*1288*/        FunctionType delegateSuperCtor = delegateSuperObject.getConstructor();
/*1290*/        if (delegatorCtor != null && delegateBaseCtor != null && delegateSuperCtor != null) {
/*1292*/          FunctionParamBuilder functionParamBuilder = new FunctionParamBuilder(TypedScopeCreator.this.typeRegistry);
/*1294*/          functionParamBuilder.addRequiredParams(new JSType[] { TypedScopeCreator.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE) });
/*1296*/          FunctionType findDelegate = TypedScopeCreator.this.typeRegistry.createFunctionType(TypedScopeCreator.this.typeRegistry.createDefaultObjectUnion(delegateBaseObject), functionParamBuilder.build());
/*1300*/          FunctionType delegateProxy = TypedScopeCreator.this.typeRegistry.createConstructorType(delegateBaseObject.getReferenceName() + TypedScopeCreator.DELEGATE_PROXY_SUFFIX, null, null, null);
/*1303*/          delegateProxy.setPrototypeBasedOn(delegateBaseObject);
/*1305*/          TypedScopeCreator.this.codingConvention.applyDelegateRelationship(delegateSuperObject, delegateBaseObject, delegatorObject, delegateProxy, findDelegate);
/*1308*/          TypedScopeCreator.this.delegateProxyPrototypes.add(delegateProxy.getPrototype());
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void maybeDeclareQualifiedName(NodeTraversal t, JSDocInfo info, Node n, Node parent, Node rhsValue) {
/*1325*/      Node ownerNode = n.getFirstChild();
/*1326*/      String ownerName = ownerNode.getQualifiedName();
/*1327*/      String qName = n.getQualifiedName();
/*1328*/      String propName = n.getLastChild().getString();
/*1329*/      Preconditions.checkArgument((qName != null && ownerName != null));
/*1336*/      if ("prototype".equals(propName)) {
/*1337*/        Scope.Var qVar = this.scope.getVar(qName);
/*1338*/        if (qVar != null) {
/*1339*/          if (!qVar.isTypeInferred())
/*   0*/            return; 
/*1343*/          if (qVar.getScope() == this.scope)
/*1344*/            this.scope.undeclare(qVar); 
/*   0*/        } 
/*   0*/      } 
/*1363*/      JSType valueType = getDeclaredType(t.getSourceName(), info, n, rhsValue);
/*1364*/      if (valueType == null && rhsValue != null)
/*1366*/        valueType = rhsValue.getJSType(); 
/*1369*/      if (valueType == null) {
/*1370*/        if (parent.getType() == 130)
/*1371*/          this.stubDeclarations.add(new TypedScopeCreator.StubDeclaration(n, (t.getInput() != null && t.getInput().isExtern()), ownerName)); 
/*   0*/        return;
/*   0*/      } 
/*   0*/      boolean inferred = true;
/*1381*/      if (info != null)
/*1383*/        inferred = (!info.hasType() && !info.hasEnumParameterType() && (!info.isConstant() || valueType == null || valueType.isUnknownType()) && !FunctionTypeBuilder.isFunctionTypeDeclaration(info)); 
/*1390*/      if (inferred)
/*1392*/        inferred = (rhsValue == null || rhsValue.getType() != 105 || this.scope.isDeclared(qName, false)); 
/*1397*/      if (!inferred) {
/*1398*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1399*/        if (ownerType != null) {
/*1402*/          boolean isExtern = (t.getInput() != null && t.getInput().isExtern());
/*1403*/          if ((!ownerType.hasOwnProperty(propName) || ownerType.isPropertyTypeInferred(propName)) && ((isExtern && !ownerType.isNativeObjectType()) || !ownerType.isInstanceType()))
/*1408*/            ownerType.defineDeclaredProperty(propName, valueType, isExtern, n); 
/*   0*/        } 
/*1414*/        defineSlot(n, parent, valueType, inferred);
/*1415*/      } else if (rhsValue != null && rhsValue.getType() == 44) {
/*1418*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1419*/        if (ownerType instanceof FunctionType) {
/*1420*/          JSType ownerTypeOfThis = ((FunctionType)ownerType).getTypeOfThis();
/*1421*/          String delegateName = TypedScopeCreator.this.codingConvention.getDelegateSuperclassName();
/*1422*/          JSType delegateType = (delegateName == null) ? null : TypedScopeCreator.this.typeRegistry.getType(delegateName);
/*1424*/          if (delegateType != null && ownerTypeOfThis.isSubtype(delegateType))
/*1426*/            defineSlot(n, parent, TypedScopeCreator.this.getNativeType(JSTypeNative.BOOLEAN_TYPE), true); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private ObjectType getObjectSlot(String slotName) {
/*1439*/      Scope.Var ownerVar = this.scope.getVar(slotName);
/*1440*/      if (ownerVar != null) {
/*1441*/        JSType ownerVarType = ownerVar.getType();
/*1442*/        return ObjectType.cast((ownerVarType == null) ? null : ownerVarType.restrictByNotNullOrUndefined());
/*   0*/      } 
/*1445*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    void resolveStubDeclarations() {
/*1453*/      for (TypedScopeCreator.StubDeclaration stub : this.stubDeclarations) {
/*1454*/        Node n = stub.node;
/*1455*/        Node parent = n.getParent();
/*1456*/        String qName = n.getQualifiedName();
/*1457*/        String propName = n.getLastChild().getString();
/*1458*/        String ownerName = stub.ownerName;
/*1459*/        boolean isExtern = stub.isExtern;
/*1461*/        if (this.scope.isDeclared(qName, false))
/*   0*/          continue; 
/*1467*/        ObjectType ownerType = getObjectSlot(ownerName);
/*1468*/        ObjectType unknownType = TypedScopeCreator.this.typeRegistry.getNativeObjectType(JSTypeNative.UNKNOWN_TYPE);
/*1469*/        defineSlot(n, parent, unknownType, true);
/*1471*/        if (ownerType != null && (isExtern || ownerType.isFunctionPrototypeType())) {
/*1475*/          ownerType.defineInferredProperty(propName, unknownType, isExtern, n);
/*   0*/          continue;
/*   0*/        } 
/*1478*/        TypedScopeCreator.this.typeRegistry.registerPropertyOnType(propName, (ownerType == null) ? unknownType : ownerType);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private final class CollectProperties extends NodeTraversal.AbstractShallowStatementCallback {
/*   0*/      private final ObjectType thisType;
/*   0*/      
/*   0*/      CollectProperties(ObjectType thisType) {
/*1493*/        this.thisType = thisType;
/*   0*/      }
/*   0*/      
/*   0*/      public void visit(NodeTraversal t, Node n, Node parent) {
/*1497*/        if (n.getType() == 130) {
/*1498*/          Node child = n.getFirstChild();
/*1499*/          switch (child.getType()) {
/*   0*/            case 86:
/*1501*/              maybeCollectMember(t, child.getFirstChild(), child, child.getLastChild());
/*   0*/              break;
/*   0*/            case 33:
/*1505*/              maybeCollectMember(t, child, child, null);
/*   0*/              break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      }
/*   0*/      
/*   0*/      private void maybeCollectMember(NodeTraversal t, Node member, Node nodeWithJsDocInfo, @Nullable Node value) {
/*1513*/        JSDocInfo info = nodeWithJsDocInfo.getJSDocInfo();
/*1518*/        if (info == null || member.getType() != 33 || member.getFirstChild().getType() != 42)
/*   0*/          return; 
/*1524*/        member.getFirstChild().setJSType(this.thisType);
/*1525*/        JSType jsType = TypedScopeCreator.AbstractScopeBuilder.this.getDeclaredType(t.getSourceName(), info, member, value);
/*1526*/        Node name = member.getLastChild();
/*1527*/        if (jsType != null && (name.getType() == 38 || name.getType() == 40))
/*1529*/          this.thisType.defineDeclaredProperty(name.getString(), jsType, false, member); 
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
/*   0*/      if (n.getType() == 130) {
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
/*   0*/      if (jsType != null && (name.getType() == 38 || name.getType() == 40))
/*1529*/        this.thisType.defineDeclaredProperty(name.getString(), jsType, false, member); 
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
/*1548*/      this.node = node;
/*1549*/      this.isExtern = isExtern;
/*1550*/      this.ownerName = ownerName;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class GlobalScopeBuilder extends AbstractScopeBuilder {
/*   0*/    private GlobalScopeBuilder(Scope scope) {
/*1561*/      super(scope);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1573*/      super.visit(t, n, parent);
/*1575*/      switch (n.getType()) {
/*   0*/        case 86:
/*1579*/          checkForOldStyleTypedef(t, n);
/*   0*/          break;
/*   0*/        case 118:
/*1584*/          if (n.hasOneChild()) {
/*1585*/            checkForOldStyleTypedef(t, n);
/*1586*/            checkForTypedef(t, n.getFirstChild(), n.getJSDocInfo());
/*   0*/          } 
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    void maybeDeclareQualifiedName(NodeTraversal t, JSDocInfo info, Node n, Node parent, Node rhsValue) {
/*1596*/      checkForTypedef(t, n, info);
/*1597*/      super.maybeDeclareQualifiedName(t, info, n, parent, rhsValue);
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForTypedef(NodeTraversal t, Node candidate, JSDocInfo info) {
/*1608*/      if (info == null || !info.hasTypedefType())
/*   0*/        return; 
/*1612*/      String typedef = candidate.getQualifiedName();
/*1613*/      if (typedef == null)
/*   0*/        return; 
/*1620*/      TypedScopeCreator.this.typeRegistry.declareType(typedef, TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*1622*/      JSType realType = info.getTypedefType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry);
/*1623*/      if (realType == null)
/*1624*/        TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), candidate, TypedScopeCreator.MALFORMED_TYPEDEF, new String[] { typedef })); 
/*1629*/      TypedScopeCreator.this.typeRegistry.overwriteDeclaredType(typedef, realType);
/*1630*/      if (candidate.getType() == 33)
/*1631*/        defineSlot(candidate, candidate.getParent(), TypedScopeCreator.this.getNativeType(JSTypeNative.NO_TYPE), false); 
/*   0*/    }
/*   0*/    
/*   0*/    private void checkForOldStyleTypedef(NodeTraversal t, Node candidate) {
/*1644*/      String typedef = TypedScopeCreator.this.codingConvention.identifyTypeDefAssign(candidate);
/*1645*/      if (typedef != null) {
/*1649*/        TypedScopeCreator.this.typeRegistry.declareType(typedef, TypedScopeCreator.this.getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*1651*/        JSDocInfo info = candidate.getJSDocInfo();
/*1652*/        JSType realType = null;
/*1653*/        if (info != null && info.getType() != null)
/*1654*/          realType = info.getType().evaluate(this.scope, TypedScopeCreator.this.typeRegistry); 
/*1657*/        if (realType == null)
/*1658*/          TypedScopeCreator.this.compiler.report(JSError.make(t.getSourceName(), candidate, TypedScopeCreator.MALFORMED_TYPEDEF, new String[] { typedef })); 
/*1663*/        TypedScopeCreator.this.typeRegistry.overwriteDeclaredType(typedef, realType);
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private final class LocalScopeBuilder extends AbstractScopeBuilder {
/*   0*/    private LocalScopeBuilder(Scope scope) {
/*1680*/      super(scope);
/*   0*/    }
/*   0*/    
/*   0*/    void build() {
/*1687*/      NodeTraversal.traverse(TypedScopeCreator.this.compiler, this.scope.getRootNode(), this);
/*   0*/    }
/*   0*/    
/*   0*/    public void visit(NodeTraversal t, Node n, Node parent) {
/*1699*/      if (n == this.scope.getRootNode())
/*   0*/        return; 
/*1701*/      if (n.getType() == 83 && parent == this.scope.getRootNode()) {
/*1702*/        handleFunctionInputs(parent);
/*   0*/        return;
/*   0*/      } 
/*1706*/      super.visit(t, n, parent);
/*   0*/    }
/*   0*/    
/*   0*/    private void handleFunctionInputs(Node fnNode) {
/*1712*/      Node fnNameNode = fnNode.getFirstChild();
/*1713*/      String fnName = fnNameNode.getString();
/*1714*/      if (!fnName.isEmpty()) {
/*1715*/        Scope.Var fnVar = this.scope.getVar(fnName);
/*1716*/        if (fnVar == null || (fnVar.getNameNode() != null && fnVar.getInitialValue() != fnNode))
/*1724*/          defineSlot(fnNameNode, fnNode, fnNode.getJSType(), false); 
/*   0*/      } 
/*1728*/      declareArguments(fnNode);
/*   0*/    }
/*   0*/    
/*   0*/    private void declareArguments(Node functionNode) {
/*1735*/      Node astParameters = functionNode.getFirstChild().getNext();
/*1736*/      Node body = astParameters.getNext();
/*1737*/      FunctionType functionType = (FunctionType)functionNode.getJSType();
/*1738*/      if (functionType != null) {
/*1739*/        Node jsDocParameters = functionType.getParametersNode();
/*1740*/        if (jsDocParameters != null) {
/*1741*/          Node jsDocParameter = jsDocParameters.getFirstChild();
/*1742*/          for (Node astParameter : astParameters.children()) {
/*1743*/            if (jsDocParameter != null) {
/*1744*/              defineSlot(astParameter, functionNode, jsDocParameter.getJSType(), false);
/*1746*/              jsDocParameter = jsDocParameter.getNext();
/*   0*/              continue;
/*   0*/            } 
/*1748*/            defineSlot(astParameter, functionNode, null, true);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static JSDocInfo getBestJSDocInfo(Node n) {
/*1759*/    JSDocInfo info = n.getJSDocInfo();
/*1760*/    if (info == null) {
/*1761*/      Node parent = n.getParent();
/*1762*/      int parentType = parent.getType();
/*1763*/      if (parentType == 38) {
/*1764*/        info = parent.getJSDocInfo();
/*1765*/        if (info == null && parent.getParent().hasOneChild())
/*1766*/          info = parent.getParent().getJSDocInfo(); 
/*1768*/      } else if (parentType == 86) {
/*1769*/        info = parent.getJSDocInfo();
/*1770*/      } else if (NodeUtil.isObjectLitKey(parent, parent.getParent())) {
/*1771*/        info = parent.getJSDocInfo();
/*   0*/      } 
/*   0*/    } 
/*1774*/    return info;
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getBestLValue(Node n) {
/*1779*/    Node parent = n.getParent();
/*1780*/    int parentType = parent.getType();
/*1781*/    boolean isFunctionDeclaration = NodeUtil.isFunctionDeclaration(n);
/*1782*/    if (isFunctionDeclaration)
/*1783*/      return n.getFirstChild(); 
/*1784*/    if (parentType == 38)
/*1785*/      return parent; 
/*1786*/    if (parentType == 86)
/*1787*/      return parent.getFirstChild(); 
/*1788*/    if (NodeUtil.isObjectLitKey(parent, parent.getParent()))
/*1789*/      return parent; 
/*1791*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static String getBestLValueName(@Nullable Node lValue) {
/*1796*/    if (lValue == null || lValue.getParent() == null)
/*1797*/      return null; 
/*1799*/    if (NodeUtil.isObjectLitKey(lValue, lValue.getParent())) {
/*1800*/      Node owner = getBestLValue(lValue.getParent());
/*1801*/      if (owner != null) {
/*1802*/        String ownerName = getBestLValueName(owner);
/*1803*/        if (ownerName != null)
/*1804*/          return ownerName + "." + NodeUtil.getObjectLitKeyName(lValue); 
/*   0*/      } 
/*1807*/      return null;
/*   0*/    } 
/*1809*/    return lValue.getQualifiedName();
/*   0*/  }
/*   0*/}
