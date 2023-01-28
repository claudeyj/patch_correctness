/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.javascript.jscomp.graph.DiGraph;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.BooleanLiteralSet;
/*   0*/import com.google.javascript.rhino.jstype.FunctionType;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeNative;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import com.google.javascript.rhino.jstype.ObjectType;
/*   0*/import com.google.javascript.rhino.jstype.StaticSlot;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/class TypeInference extends DataFlowAnalysis.BranchedForwardDataFlowAnalysis<Node, FlowScope> {
/*  58*/  static final DiagnosticType TEMPLATE_TYPE_NOT_OBJECT_TYPE = DiagnosticType.warning("JSC_TEMPLATE_TYPE_NOT_OBJECT_TYPE", "The template type must be an object type.\nActual: {0}");
/*   0*/  
/*  63*/  static final DiagnosticType TEMPLATE_TYPE_OF_THIS_EXPECTED = DiagnosticType.warning("JSC_TEMPLATE_TYPE_OF_THIS_EXPECTED", "A function type with the template type as the type of this must be a parameter type");
/*   0*/  
/*  69*/  static final DiagnosticType FUNCTION_LITERAL_UNDEFINED_THIS = DiagnosticType.warning("JSC_FUNCTION_LITERAL_UNDEFINED_THIS", "Function literal argument refers to undefined this argument");
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final JSTypeRegistry registry;
/*   0*/  
/*   0*/  private final ReverseAbstractInterpreter reverseInterpreter;
/*   0*/  
/*   0*/  private final Scope syntacticScope;
/*   0*/  
/*   0*/  private final FlowScope functionScope;
/*   0*/  
/*   0*/  private final FlowScope bottomScope;
/*   0*/  
/*   0*/  private final Map<String, CodingConvention.AssertionFunctionSpec> assertionFunctionsMap;
/*   0*/  
/*   0*/  TypeInference(AbstractCompiler compiler, ControlFlowGraph<Node> cfg, ReverseAbstractInterpreter reverseInterpreter, Scope functionScope, Map<String, CodingConvention.AssertionFunctionSpec> assertionFunctionsMap) {
/*  86*/    super(cfg, new LinkedFlowScope.FlowScopeJoinOp());
/*  87*/    this.compiler = compiler;
/*  88*/    this.registry = compiler.getTypeRegistry();
/*  89*/    this.reverseInterpreter = reverseInterpreter;
/*  90*/    this.syntacticScope = functionScope;
/*  91*/    this.functionScope = LinkedFlowScope.createEntryLattice(functionScope);
/*  92*/    this.assertionFunctionsMap = assertionFunctionsMap;
/*  96*/    Iterator<Scope.Var> varIt = functionScope.getDeclarativelyUnboundVarsWithoutTypes();
/*  98*/    while (varIt.hasNext()) {
/*  99*/      Scope.Var var = varIt.next();
/* 100*/      if (isUnflowable(var))
/*   0*/        continue; 
/* 104*/      this.functionScope.inferSlotType(var.getName(), getNativeType(JSTypeNative.VOID_TYPE));
/*   0*/    } 
/* 108*/    this.bottomScope = LinkedFlowScope.createEntryLattice(new Scope(functionScope.getRootNode(), functionScope.getTypeOfThis()));
/*   0*/  }
/*   0*/  
/*   0*/  FlowScope createInitialEstimateLattice() {
/* 114*/    return this.bottomScope;
/*   0*/  }
/*   0*/  
/*   0*/  FlowScope createEntryLattice() {
/* 119*/    return this.functionScope;
/*   0*/  }
/*   0*/  
/*   0*/  FlowScope flowThrough(Node n, FlowScope input) {
/* 126*/    if (input == this.bottomScope)
/* 127*/      return input; 
/* 130*/    FlowScope output = input.createChildFlowScope();
/* 131*/    output = traverse(n, output);
/* 132*/    return output;
/*   0*/  }
/*   0*/  
/*   0*/  List<FlowScope> branchedFlowThrough(Node source, FlowScope input) {
/* 143*/    FlowScope output = flowThrough(source, input);
/* 144*/    Node condition = null;
/* 145*/    FlowScope conditionFlowScope = null;
/* 146*/    BooleanOutcomePair conditionOutcomes = null;
/* 148*/    List<DiGraph.DiGraphEdge<Node, ControlFlowGraph.Branch>> branchEdges = getCfg().getOutEdges(source);
/* 149*/    List<FlowScope> result = Lists.newArrayListWithCapacity(branchEdges.size());
/* 150*/    for (DiGraph.DiGraphEdge<Node, ControlFlowGraph.Branch> branchEdge : branchEdges) {
/* 151*/      ControlFlowGraph.Branch branch = branchEdge.getValue();
/* 152*/      FlowScope newScope = output;
/* 154*/      switch (branch) {
/*   0*/        case ON_TRUE:
/* 156*/          if (NodeUtil.isForIn(source)) {
/* 158*/            Node item = source.getFirstChild();
/* 159*/            Node obj = item.getNext();
/* 161*/            FlowScope informed = traverse(obj, output.createChildFlowScope());
/* 163*/            if (item.isVar())
/* 164*/              item = item.getFirstChild(); 
/* 166*/            if (item.isName()) {
/* 167*/              JSType iterKeyType = getNativeType(JSTypeNative.STRING_TYPE);
/* 168*/              ObjectType objType = getJSType(obj).dereference();
/* 169*/              JSType objIndexType = (objType == null) ? null : objType.getIndexType();
/* 171*/              if (objIndexType != null && !objIndexType.isUnknownType()) {
/* 172*/                JSType narrowedKeyType = iterKeyType.getGreatestSubtype(objIndexType);
/* 174*/                if (!narrowedKeyType.isEmptyType())
/* 175*/                  iterKeyType = narrowedKeyType; 
/*   0*/              } 
/* 178*/              redeclareSimpleVar(informed, item, iterKeyType);
/*   0*/            } 
/* 180*/            newScope = informed;
/*   0*/            break;
/*   0*/          } 
/*   0*/        case ON_FALSE:
/* 187*/          if (condition == null) {
/* 188*/            condition = NodeUtil.getConditionExpression(source);
/* 189*/            if (condition == null && source.isCase()) {
/* 190*/              condition = source;
/* 194*/              if (conditionFlowScope == null)
/* 195*/                conditionFlowScope = traverse(condition.getFirstChild(), output.createChildFlowScope()); 
/*   0*/            } 
/*   0*/          } 
/* 201*/          if (condition != null) {
/* 202*/            if (condition.isAnd() || condition.isOr()) {
/* 218*/              if (conditionOutcomes == null)
/* 219*/                conditionOutcomes = condition.isAnd() ? traverseAnd(condition, output.createChildFlowScope()) : traverseOr(condition, output.createChildFlowScope()); 
/* 223*/              newScope = this.reverseInterpreter.getPreciserScopeKnowingConditionOutcome(condition, conditionOutcomes.getOutcomeFlowScope(condition.getType(), (branch == ControlFlowGraph.Branch.ON_TRUE)), (branch == ControlFlowGraph.Branch.ON_TRUE));
/*   0*/              break;
/*   0*/            } 
/* 232*/            if (conditionFlowScope == null)
/* 233*/              conditionFlowScope = traverse(condition, output.createChildFlowScope()); 
/* 236*/            newScope = this.reverseInterpreter.getPreciserScopeKnowingConditionOutcome(condition, conditionFlowScope, (branch == ControlFlowGraph.Branch.ON_TRUE));
/*   0*/          } 
/*   0*/          break;
/*   0*/      } 
/* 244*/      result.add(newScope.optimize());
/*   0*/    } 
/* 246*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverse(Node n, FlowScope scope) {
/* 250*/    switch (n.getType()) {
/*   0*/      case 86:
/* 252*/        scope = traverseAssign(n, scope);
/*   0*/        break;
/*   0*/      case 38:
/* 256*/        scope = traverseName(n, scope);
/*   0*/        break;
/*   0*/      case 33:
/* 260*/        scope = traverseGetProp(n, scope);
/*   0*/        break;
/*   0*/      case 101:
/* 264*/        scope = traverseAnd(n, scope).getJoinedFlowScope().createChildFlowScope();
/*   0*/        break;
/*   0*/      case 100:
/* 269*/        scope = traverseOr(n, scope).getJoinedFlowScope().createChildFlowScope();
/*   0*/        break;
/*   0*/      case 98:
/* 274*/        scope = traverseHook(n, scope);
/*   0*/        break;
/*   0*/      case 64:
/* 278*/        scope = traverseObjectLiteral(n, scope);
/*   0*/        break;
/*   0*/      case 37:
/* 282*/        scope = traverseCall(n, scope);
/*   0*/        break;
/*   0*/      case 30:
/* 286*/        scope = traverseNew(n, scope);
/*   0*/        break;
/*   0*/      case 21:
/*   0*/      case 93:
/* 291*/        scope = traverseAdd(n, scope);
/*   0*/        break;
/*   0*/      case 28:
/*   0*/      case 29:
/* 296*/        scope = traverse(n.getFirstChild(), scope);
/* 297*/        n.setJSType(getNativeType(JSTypeNative.NUMBER_TYPE));
/*   0*/        break;
/*   0*/      case 63:
/* 301*/        scope = traverseArrayLiteral(n, scope);
/*   0*/        break;
/*   0*/      case 42:
/* 305*/        n.setJSType(scope.getTypeOfThis());
/*   0*/        break;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 27:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/*   0*/      case 102:
/*   0*/      case 103:
/* 331*/        scope = traverseChildren(n, scope);
/* 332*/        n.setJSType(getNativeType(JSTypeNative.NUMBER_TYPE));
/*   0*/        break;
/*   0*/      case 83:
/* 336*/        scope = traverse(n.getFirstChild(), scope);
/* 337*/        n.setJSType(getJSType(n.getFirstChild()));
/*   0*/        break;
/*   0*/      case 85:
/* 341*/        scope = traverseChildren(n, scope);
/* 342*/        n.setJSType(getJSType(n.getLastChild()));
/*   0*/        break;
/*   0*/      case 32:
/* 346*/        scope = traverseChildren(n, scope);
/* 347*/        n.setJSType(getNativeType(JSTypeNative.STRING_TYPE));
/*   0*/        break;
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/*   0*/      case 26:
/*   0*/      case 31:
/*   0*/      case 45:
/*   0*/      case 46:
/*   0*/      case 51:
/*   0*/      case 52:
/* 362*/        scope = traverseChildren(n, scope);
/* 363*/        n.setJSType(getNativeType(JSTypeNative.BOOLEAN_TYPE));
/*   0*/        break;
/*   0*/      case 35:
/* 367*/        scope = traverseGetElem(n, scope);
/*   0*/        break;
/*   0*/      case 130:
/* 371*/        scope = traverseChildren(n, scope);
/* 372*/        if (n.getFirstChild().isGetProp())
/* 373*/          ensurePropertyDeclared(n.getFirstChild()); 
/*   0*/        break;
/*   0*/      case 110:
/* 378*/        scope = traverse(n.getFirstChild(), scope);
/*   0*/        break;
/*   0*/      case 4:
/* 382*/        scope = traverseReturn(n, scope);
/*   0*/        break;
/*   0*/      case 49:
/*   0*/      case 118:
/* 387*/        scope = traverseChildren(n, scope);
/*   0*/        break;
/*   0*/      case 120:
/* 391*/        scope = traverseCatch(n, scope);
/*   0*/        break;
/*   0*/    } 
/* 394*/    if (!n.isFunction()) {
/* 395*/      JSDocInfo info = n.getJSDocInfo();
/* 396*/      if (info != null && info.hasType()) {
/* 397*/        JSType castType = info.getType().evaluate(this.syntacticScope, this.registry);
/* 402*/        if (n.isQualifiedName() && n.getParent().isExprResult())
/* 404*/          updateScopeForTypeChange(scope, n, n.getJSType(), castType); 
/* 407*/        n.setJSType(castType);
/*   0*/      } 
/*   0*/    } 
/* 411*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseReturn(Node n, FlowScope scope) {
/* 418*/    scope = traverseChildren(n, scope);
/* 420*/    Node retValue = n.getFirstChild();
/* 421*/    if (retValue != null) {
/* 422*/      JSType type = this.functionScope.getRootNode().getJSType();
/* 423*/      if (type != null) {
/* 424*/        FunctionType fnType = type.toMaybeFunctionType();
/* 425*/        if (fnType != null)
/* 426*/          inferPropertyTypesToMatchConstraint(retValue.getJSType(), fnType.getReturnType()); 
/*   0*/      } 
/*   0*/    } 
/* 431*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseCatch(Node n, FlowScope scope) {
/* 439*/    Node name = n.getFirstChild();
/* 440*/    JSType type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/* 441*/    name.setJSType(type);
/* 442*/    redeclareSimpleVar(scope, name, type);
/* 443*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseAssign(Node n, FlowScope scope) {
/* 447*/    Node left = n.getFirstChild();
/* 448*/    Node right = n.getLastChild();
/* 449*/    scope = traverseChildren(n, scope);
/* 451*/    JSType leftType = left.getJSType();
/* 452*/    JSType rightType = getJSType(right);
/* 453*/    n.setJSType(rightType);
/* 455*/    updateScopeForTypeChange(scope, left, leftType, rightType);
/* 456*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private void updateScopeForTypeChange(FlowScope scope, Node left, JSType leftType, JSType resultType) {
/*   0*/    String varName;
/*   0*/    Scope.Var var;
/*   0*/    boolean isVarDeclaration;
/*   0*/    String qualifiedName;
/* 465*/    Preconditions.checkNotNull(resultType);
/* 466*/    switch (left.getType()) {
/*   0*/      case 38:
/* 468*/        varName = left.getString();
/* 469*/        var = this.syntacticScope.getVar(varName);
/* 481*/        isVarDeclaration = left.hasChildren();
/* 482*/        if (!isVarDeclaration || var == null || var.isTypeInferred())
/* 483*/          redeclareSimpleVar(scope, left, resultType); 
/* 485*/        left.setJSType((isVarDeclaration || leftType == null) ? resultType : null);
/* 488*/        if (var != null && var.isTypeInferred()) {
/* 489*/          JSType oldType = var.getType();
/* 490*/          var.setType((oldType == null) ? resultType : oldType.getLeastSupertype(resultType));
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 33:
/* 495*/        qualifiedName = left.getQualifiedName();
/* 496*/        if (qualifiedName != null)
/* 497*/          scope.inferQualifiedSlot(left, qualifiedName, (leftType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : leftType, resultType); 
/* 502*/        left.setJSType(resultType);
/* 503*/        ensurePropertyDefined(left, resultType);
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void ensurePropertyDefined(Node getprop, JSType rightType) {
/* 512*/    String propName = getprop.getLastChild().getString();
/* 513*/    JSType nodeType = getJSType(getprop.getFirstChild());
/* 514*/    ObjectType objectType = ObjectType.cast(nodeType.restrictByNotNullOrUndefined());
/* 516*/    if (objectType == null) {
/* 517*/      this.registry.registerPropertyOnType(propName, nodeType);
/*   0*/    } else {
/* 519*/      if (ensurePropertyDeclaredHelper(getprop, objectType))
/*   0*/        return; 
/* 523*/      if (!objectType.isPropertyTypeDeclared(propName))
/* 536*/        if (objectType.hasProperty(propName) || !objectType.isInstanceType()) {
/* 538*/          if ("prototype".equals(propName)) {
/* 539*/            objectType.defineDeclaredProperty(propName, rightType, getprop);
/*   0*/          } else {
/* 542*/            objectType.defineInferredProperty(propName, rightType, getprop);
/*   0*/          } 
/* 546*/        } else if (getprop.getFirstChild().isThis() && getJSType(this.syntacticScope.getRootNode()).isConstructor()) {
/* 548*/          objectType.defineInferredProperty(propName, rightType, getprop);
/*   0*/        } else {
/* 551*/          this.registry.registerPropertyOnType(propName, objectType);
/*   0*/        }  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void ensurePropertyDeclared(Node getprop) {
/* 566*/    ObjectType ownerType = ObjectType.cast(getJSType(getprop.getFirstChild()).restrictByNotNullOrUndefined());
/* 568*/    if (ownerType != null)
/* 569*/      ensurePropertyDeclaredHelper(getprop, ownerType); 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean ensurePropertyDeclaredHelper(Node getprop, ObjectType objectType) {
/* 579*/    String propName = getprop.getLastChild().getString();
/* 580*/    String qName = getprop.getQualifiedName();
/* 581*/    if (qName != null) {
/* 582*/      Scope.Var var = this.syntacticScope.getVar(qName);
/* 583*/      if (var != null && !var.isTypeInferred())
/* 585*/        if (propName.equals("prototype") || (!objectType.hasOwnProperty(propName) && (!objectType.isInstanceType() || (var.isExtern() && !objectType.isNativeObjectType()))))
/* 590*/          return objectType.defineDeclaredProperty(propName, var.getType(), getprop);  
/*   0*/    } 
/* 595*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseName(Node n, FlowScope scope) {
/* 599*/    String varName = n.getString();
/* 600*/    Node value = n.getFirstChild();
/* 601*/    JSType type = n.getJSType();
/* 602*/    if (value != null) {
/* 603*/      scope = traverse(value, scope);
/* 604*/      updateScopeForTypeChange(scope, n, n.getJSType(), getJSType(value));
/* 606*/      return scope;
/*   0*/    } 
/* 608*/    StaticSlot<JSType> var = scope.getSlot(varName);
/* 609*/    if (var != null) {
/* 615*/      boolean isInferred = var.isTypeInferred();
/* 616*/      boolean unflowable = (isInferred && isUnflowable(this.syntacticScope.getVar(varName)));
/* 622*/      boolean nonLocalInferredSlot = (isInferred && this.syntacticScope.getParent() != null && var == this.syntacticScope.getParent().getSlot(varName));
/* 627*/      if (!unflowable && !nonLocalInferredSlot) {
/* 628*/        type = var.getType();
/* 629*/        if (type == null)
/* 630*/          type = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*   0*/      } 
/*   0*/    } 
/* 635*/    n.setJSType(type);
/* 636*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseArrayLiteral(Node n, FlowScope scope) {
/* 641*/    scope = traverseChildren(n, scope);
/* 642*/    n.setJSType(getNativeType(JSTypeNative.ARRAY_TYPE));
/* 643*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseObjectLiteral(Node n, FlowScope scope) {
/* 647*/    JSType type = n.getJSType();
/* 648*/    Preconditions.checkNotNull(type);
/* 650*/    for (Node name = n.getFirstChild(); name != null; name = name.getNext())
/* 651*/      scope = traverse(name.getFirstChild(), scope); 
/* 660*/    ObjectType objectType = ObjectType.cast(type);
/* 661*/    if (objectType == null)
/* 662*/      return scope; 
/* 665*/    boolean hasLendsName = (n.getJSDocInfo() != null && n.getJSDocInfo().getLendsName() != null);
/* 667*/    if (objectType.hasReferenceName() && !hasLendsName)
/* 668*/      return scope; 
/* 671*/    String qObjName = NodeUtil.getBestLValueName(NodeUtil.getBestLValue(n));
/* 673*/    for (Node node1 = n.getFirstChild(); node1 != null; 
/* 674*/      node1 = node1.getNext()) {
/* 675*/      Node value = node1.getFirstChild();
/* 676*/      String memberName = NodeUtil.getObjectLitKeyName(node1);
/* 677*/      if (memberName != null) {
/* 678*/        JSType rawValueType = node1.getFirstChild().getJSType();
/* 679*/        JSType valueType = NodeUtil.getObjectLitKeyTypeFromValueType(node1, rawValueType);
/* 681*/        if (valueType == null)
/* 682*/          valueType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/* 684*/        objectType.defineInferredProperty(memberName, valueType, node1);
/* 687*/        if (qObjName != null && node1.isString()) {
/* 688*/          String qKeyName = qObjName + "." + memberName;
/* 689*/          Scope.Var var = this.syntacticScope.getVar(qKeyName);
/* 690*/          JSType oldType = (var == null) ? null : var.getType();
/* 691*/          if (var != null && var.isTypeInferred())
/* 692*/            var.setType((oldType == null) ? valueType : oldType.getLeastSupertype(oldType)); 
/* 696*/          scope.inferQualifiedSlot(node1, qKeyName, (oldType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : oldType, valueType);
/*   0*/        } 
/*   0*/      } else {
/* 701*/        n.setJSType(getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/      } 
/*   0*/    } 
/* 704*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseAdd(Node n, FlowScope scope) {
/* 708*/    Node left = n.getFirstChild();
/* 709*/    Node right = left.getNext();
/* 710*/    scope = traverseChildren(n, scope);
/* 712*/    JSType leftType = left.getJSType();
/* 713*/    JSType rightType = right.getJSType();
/* 715*/    JSType type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/* 716*/    if (leftType != null && rightType != null) {
/* 717*/      boolean leftIsUnknown = leftType.isUnknownType();
/* 718*/      boolean rightIsUnknown = rightType.isUnknownType();
/* 719*/      if (leftIsUnknown && rightIsUnknown) {
/* 720*/        type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/* 721*/      } else if ((!leftIsUnknown && leftType.isString()) || (!rightIsUnknown && rightType.isString())) {
/* 723*/        type = getNativeType(JSTypeNative.STRING_TYPE);
/* 724*/      } else if (leftIsUnknown || rightIsUnknown) {
/* 725*/        type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/* 726*/      } else if (isAddedAsNumber(leftType) && isAddedAsNumber(rightType)) {
/* 727*/        type = getNativeType(JSTypeNative.NUMBER_TYPE);
/*   0*/      } else {
/* 729*/        type = this.registry.createUnionType(new JSTypeNative[] { JSTypeNative.STRING_TYPE, JSTypeNative.NUMBER_TYPE });
/*   0*/      } 
/*   0*/    } 
/* 732*/    n.setJSType(type);
/* 734*/    if (n.isAssignAdd())
/* 735*/      updateScopeForTypeChange(scope, left, leftType, type); 
/* 738*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isAddedAsNumber(JSType type) {
/* 742*/    return type.isSubtype(this.registry.createUnionType(new JSTypeNative[] { JSTypeNative.VOID_TYPE, JSTypeNative.NULL_TYPE, JSTypeNative.NUMBER_VALUE_OR_OBJECT_TYPE, JSTypeNative.BOOLEAN_TYPE, JSTypeNative.BOOLEAN_OBJECT_TYPE }));
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseHook(Node n, FlowScope scope) {
/* 747*/    Node condition = n.getFirstChild();
/* 748*/    Node trueNode = condition.getNext();
/* 749*/    Node falseNode = n.getLastChild();
/* 752*/    scope = traverse(condition, scope);
/* 755*/    FlowScope trueScope = this.reverseInterpreter.getPreciserScopeKnowingConditionOutcome(condition, scope, true);
/* 758*/    FlowScope falseScope = this.reverseInterpreter.getPreciserScopeKnowingConditionOutcome(condition, scope, false);
/* 763*/    traverse(trueNode, trueScope.createChildFlowScope());
/* 766*/    traverse(falseNode, falseScope.createChildFlowScope());
/* 769*/    JSType trueType = trueNode.getJSType();
/* 770*/    JSType falseType = falseNode.getJSType();
/* 771*/    if (trueType != null && falseType != null) {
/* 772*/      n.setJSType(trueType.getLeastSupertype(falseType));
/*   0*/    } else {
/* 774*/      n.setJSType(null);
/*   0*/    } 
/* 777*/    return scope.createChildFlowScope();
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseCall(Node n, FlowScope scope) {
/* 781*/    scope = traverseChildren(n, scope);
/* 783*/    Node left = n.getFirstChild();
/* 784*/    JSType functionType = getJSType(left).restrictByNotNullOrUndefined();
/* 785*/    if (functionType != null)
/* 786*/      if (functionType.isFunctionType()) {
/* 787*/        FunctionType fnType = functionType.toMaybeFunctionType();
/* 788*/        n.setJSType(fnType.getReturnType());
/* 789*/        backwardsInferenceFromCallSite(n, fnType);
/* 790*/      } else if (functionType.equals(getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE))) {
/* 791*/        n.setJSType(getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE));
/*   0*/      }  
/* 795*/    scope = tightenTypesAfterAssertions(scope, n);
/* 796*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope tightenTypesAfterAssertions(FlowScope scope, Node callNode) {
/* 801*/    Node left = callNode.getFirstChild();
/* 802*/    Node firstParam = left.getNext();
/* 803*/    CodingConvention.AssertionFunctionSpec assertionFunctionSpec = this.assertionFunctionsMap.get(left.getQualifiedName());
/* 805*/    if (assertionFunctionSpec == null || firstParam == null)
/* 806*/      return scope; 
/* 808*/    Node assertedNode = assertionFunctionSpec.getAssertedParam(firstParam);
/* 809*/    if (assertedNode == null)
/* 810*/      return scope; 
/* 812*/    JSTypeNative assertedType = assertionFunctionSpec.getAssertedType();
/* 813*/    String assertedNodeName = assertedNode.getQualifiedName();
/* 815*/    if (assertedType == null) {
/* 816*/      if (assertedNodeName != null) {
/* 817*/        JSType type = getJSType(assertedNode);
/* 818*/        JSType narrowed = type.restrictByNotNullOrUndefined();
/* 819*/        if (type != narrowed) {
/* 820*/          scope = narrowScope(scope, assertedNode, narrowed);
/* 821*/          callNode.setJSType(narrowed);
/*   0*/        } 
/* 823*/      } else if (assertedNode.isAnd() || assertedNode.isOr()) {
/* 825*/        BooleanOutcomePair conditionOutcomes = traverseWithinShortCircuitingBinOp(assertedNode, scope);
/* 827*/        scope = this.reverseInterpreter.getPreciserScopeKnowingConditionOutcome(assertedNode, conditionOutcomes.getOutcomeFlowScope(assertedNode.getType(), true), true);
/*   0*/      } 
/* 831*/    } else if (assertedNodeName != null) {
/* 833*/      JSType type = getJSType(assertedNode);
/* 834*/      JSType narrowed = type.getGreatestSubtype(getNativeType(assertedType));
/* 835*/      if (type != narrowed) {
/* 836*/        scope = narrowScope(scope, assertedNode, narrowed);
/* 837*/        callNode.setJSType(narrowed);
/*   0*/      } 
/*   0*/    } 
/* 840*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope narrowScope(FlowScope scope, Node node, JSType narrowed) {
/* 844*/    scope = scope.createChildFlowScope();
/* 845*/    if (node.isGetProp()) {
/* 846*/      scope.inferQualifiedSlot(node, node.getQualifiedName(), getJSType(node), narrowed);
/*   0*/    } else {
/* 849*/      redeclareSimpleVar(scope, node, narrowed);
/*   0*/    } 
/* 851*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private void backwardsInferenceFromCallSite(Node n, FunctionType fnType) {
/* 880*/    updateTypeOfParameters(n, fnType);
/* 881*/    updateTypeOfThisOnClosure(n, fnType);
/* 882*/    updateBind(n, fnType);
/*   0*/  }
/*   0*/  
/*   0*/  private void updateBind(Node n, FunctionType fnType) {
/* 892*/    Node calledFn = n.getFirstChild();
/* 893*/    boolean looksLikeBind = (calledFn.isGetProp() && calledFn.getLastChild().getString().equals("bind"));
/* 895*/    if (!looksLikeBind)
/*   0*/      return; 
/* 899*/    Node callTarget = calledFn.getFirstChild();
/* 900*/    FunctionType callTargetFn = getJSType(callTarget).restrictByNotNullOrUndefined().toMaybeFunctionType();
/* 902*/    if (callTargetFn == null)
/*   0*/      return; 
/* 906*/    n.setJSType(callTargetFn.getBindReturnType(n.getChildCount() - 1));
/*   0*/  }
/*   0*/  
/*   0*/  private void updateTypeOfParameters(Node n, FunctionType fnType) {
/* 914*/    int i = 0;
/* 915*/    int childCount = n.getChildCount();
/* 916*/    for (Node iParameter : fnType.getParameters()) {
/* 917*/      if (i + 1 >= childCount)
/*   0*/        return; 
/* 922*/      JSType iParameterType = getJSType(iParameter);
/* 923*/      Node iArgument = n.getChildAtIndex(i + 1);
/* 924*/      JSType iArgumentType = getJSType(iArgument);
/* 925*/      inferPropertyTypesToMatchConstraint(iArgumentType, iParameterType);
/* 927*/      if (iParameterType.isFunctionType()) {
/* 928*/        FunctionType iParameterFnType = iParameterType.toMaybeFunctionType();
/* 930*/        if (iArgument.isFunction() && iArgumentType.isFunctionType() && iArgument.getJSDocInfo() == null)
/* 933*/          iArgument.setJSType(iParameterFnType); 
/*   0*/      } 
/* 936*/      i++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateTypeOfThisOnClosure(Node n, FunctionType fnType) {
/* 948*/    if (fnType.getTemplateTypeName() == null)
/*   0*/      return; 
/* 952*/    int i = 0;
/* 953*/    int childCount = n.getChildCount();
/* 955*/    for (Node iParameter : fnType.getParameters()) {
/* 956*/      JSType iParameterType = getJSType(iParameter).restrictByNotNullOrUndefined();
/* 958*/      if (iParameterType.isTemplateType()) {
/* 960*/        ObjectType iArgumentType = null;
/* 961*/        if (i + 1 < childCount) {
/* 962*/          Node iArgument = n.getChildAtIndex(i + 1);
/* 963*/          iArgumentType = getJSType(iArgument).restrictByNotNullOrUndefined().collapseUnion().toObjectType();
/* 967*/          if (iArgumentType == null) {
/* 968*/            this.compiler.report(JSError.make(NodeUtil.getSourceName(iArgument), iArgument, TEMPLATE_TYPE_NOT_OBJECT_TYPE, new String[] { getJSType(iArgument).toString() }));
/*   0*/            return;
/*   0*/          } 
/*   0*/        } 
/*   0*/        boolean foundTemplateTypeOfThisParameter = false;
/* 978*/        int j = 0;
/* 979*/        for (Node jParameter : fnType.getParameters()) {
/* 980*/          JSType jParameterType = getJSType(jParameter).restrictByNotNullOrUndefined();
/* 982*/          if (jParameterType.isFunctionType()) {
/* 983*/            FunctionType jParameterFnType = jParameterType.toMaybeFunctionType();
/* 984*/            if (jParameterFnType.getTypeOfThis().equals(iParameterType)) {
/* 985*/              foundTemplateTypeOfThisParameter = true;
/* 987*/              if (j + 1 >= childCount)
/*   0*/                return; 
/* 991*/              Node jArgument = n.getChildAtIndex(j + 1);
/* 992*/              JSType jArgumentType = getJSType(jArgument);
/* 993*/              if (jArgument.isFunction() && jArgumentType.isFunctionType())
/* 995*/                if (iArgumentType != null && !iArgumentType.isNoType()) {
/*1000*/                  FunctionType jArgumentFnType = jArgumentType.toMaybeFunctionType();
/*1001*/                  if (jArgumentFnType.getTypeOfThis().isUnknownType())
/*1004*/                    jArgument.setJSType(this.registry.createFunctionTypeWithNewThisType(jArgumentFnType, iArgumentType)); 
/*1010*/                } else if (NodeUtil.referencesThis(NodeUtil.getFunctionBody(jArgument))) {
/*1012*/                  this.compiler.report(JSError.make(NodeUtil.getSourceName(n), n, FUNCTION_LITERAL_UNDEFINED_THIS, new String[0]));
/*   0*/                }  
/*   0*/            } 
/*   0*/          } 
/*1021*/          j++;
/*   0*/        } 
/*1024*/        if (!foundTemplateTypeOfThisParameter) {
/*1025*/          this.compiler.report(JSError.make(NodeUtil.getSourceName(n), n, TEMPLATE_TYPE_OF_THIS_EXPECTED, new String[0]));
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/*1030*/      i++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseNew(Node n, FlowScope scope) {
/*1035*/    Node constructor = n.getFirstChild();
/*1036*/    scope = traverse(constructor, scope);
/*1038*/    JSType constructorType = constructor.getJSType();
/*1039*/    JSType type = null;
/*1040*/    if (constructorType != null) {
/*1041*/      constructorType = constructorType.restrictByNotNullOrUndefined();
/*1042*/      if (constructorType.isUnknownType()) {
/*1043*/        type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*   0*/      } else {
/*1045*/        FunctionType ct = constructorType.toMaybeFunctionType();
/*1046*/        if (ct == null && constructorType instanceof FunctionType)
/*1050*/          ct = (FunctionType)constructorType; 
/*1052*/        if (ct != null && ct.isConstructor())
/*1053*/          type = ct.getInstanceType(); 
/*   0*/      } 
/*   0*/    } 
/*1057*/    n.setJSType(type);
/*1059*/    for (Node arg = constructor.getNext(); arg != null; arg = arg.getNext())
/*1060*/      scope = traverse(arg, scope); 
/*1062*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private BooleanOutcomePair traverseAnd(Node n, FlowScope scope) {
/*1066*/    return traverseShortCircuitingBinOp(n, scope, true);
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseChildren(Node n, FlowScope scope) {
/*1070*/    for (Node el = n.getFirstChild(); el != null; el = el.getNext())
/*1071*/      scope = traverse(el, scope); 
/*1073*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseGetElem(Node n, FlowScope scope) {
/*1077*/    scope = traverseChildren(n, scope);
/*1078*/    ObjectType objType = ObjectType.cast(getJSType(n.getFirstChild()).restrictByNotNullOrUndefined());
/*1080*/    if (objType != null) {
/*1081*/      JSType type = objType.getParameterType();
/*1082*/      if (type != null)
/*1083*/        n.setJSType(type); 
/*   0*/    } 
/*1086*/    return dereferencePointer(n.getFirstChild(), scope);
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope traverseGetProp(Node n, FlowScope scope) {
/*1090*/    Node objNode = n.getFirstChild();
/*1091*/    Node property = n.getLastChild();
/*1092*/    scope = traverseChildren(n, scope);
/*1093*/    n.setJSType(getPropertyType(objNode.getJSType(), property.getString(), n, scope));
/*1096*/    return dereferencePointer(n.getFirstChild(), scope);
/*   0*/  }
/*   0*/  
/*   0*/  private void inferPropertyTypesToMatchConstraint(JSType type, JSType constraint) {
/*1115*/    if (type == null || constraint == null)
/*   0*/      return; 
/*1119*/    ObjectType constraintObj = ObjectType.cast(constraint.restrictByNotNullOrUndefined());
/*1121*/    if (constraintObj != null)
/*1122*/      type.matchConstraint(constraintObj); 
/*   0*/  }
/*   0*/  
/*   0*/  private FlowScope dereferencePointer(Node n, FlowScope scope) {
/*1131*/    if (n.isQualifiedName()) {
/*1132*/      JSType type = getJSType(n);
/*1133*/      JSType narrowed = type.restrictByNotNullOrUndefined();
/*1134*/      if (type != narrowed)
/*1135*/        scope = narrowScope(scope, n, narrowed); 
/*   0*/    } 
/*1138*/    return scope;
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getPropertyType(JSType objType, String propName, Node n, FlowScope scope) {
/*1144*/    String qualifiedName = n.getQualifiedName();
/*1145*/    StaticSlot<JSType> var = scope.getSlot(qualifiedName);
/*1146*/    if (var != null) {
/*1147*/      JSType varType = var.getType();
/*1148*/      if (varType != null) {
/*1149*/        if (varType.equals(getNativeType(JSTypeNative.UNKNOWN_TYPE)) && var != this.syntacticScope.getSlot(qualifiedName))
/*1153*/          return getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE); 
/*1155*/        return varType;
/*   0*/      } 
/*   0*/    } 
/*1160*/    JSType propertyType = null;
/*1161*/    if (objType != null)
/*1162*/      propertyType = objType.findPropertyType(propName); 
/*1165*/    if ((propertyType == null || propertyType.isUnknownType()) && qualifiedName != null) {
/*1168*/      ObjectType regType = ObjectType.cast(this.registry.getType(qualifiedName));
/*1169*/      if (regType != null)
/*1170*/        propertyType = regType.getConstructor(); 
/*   0*/    } 
/*1174*/    return propertyType;
/*   0*/  }
/*   0*/  
/*   0*/  private BooleanOutcomePair traverseOr(Node n, FlowScope scope) {
/*1178*/    return traverseShortCircuitingBinOp(n, scope, false);
/*   0*/  }
/*   0*/  
/*   0*/  private BooleanOutcomePair traverseShortCircuitingBinOp(Node n, FlowScope scope, boolean condition) {
/*   0*/    JSType type;
/*   0*/    BooleanOutcomePair literals;
/*1183*/    Node left = n.getFirstChild();
/*1184*/    Node right = n.getLastChild();
/*1187*/    BooleanOutcomePair leftLiterals = traverseWithinShortCircuitingBinOp(left, scope.createChildFlowScope());
/*1190*/    JSType leftType = left.getJSType();
/*1194*/    FlowScope rightScope = this.reverseInterpreter.getPreciserScopeKnowingConditionOutcome(left, leftLiterals.getOutcomeFlowScope(left.getType(), condition), condition);
/*1200*/    BooleanOutcomePair rightLiterals = traverseWithinShortCircuitingBinOp(right, rightScope.createChildFlowScope());
/*1203*/    JSType rightType = right.getJSType();
/*1207*/    if (leftType != null && rightType != null) {
/*1208*/      leftType = leftType.getRestrictedTypeGivenToBooleanOutcome(!condition);
/*1209*/      if (leftLiterals.toBooleanOutcomes == BooleanLiteralSet.get(!condition)) {
/*1213*/        type = leftType;
/*1214*/        literals = leftLiterals;
/*   0*/      } else {
/*1218*/        type = leftType.getLeastSupertype(rightType);
/*1219*/        literals = getBooleanOutcomePair(leftLiterals, rightLiterals, condition);
/*   0*/      } 
/*1225*/      if (literals.booleanValues == BooleanLiteralSet.EMPTY && getNativeType(JSTypeNative.BOOLEAN_TYPE).isSubtype(type))
/*1228*/        if (type.isUnionType())
/*1229*/          type = type.toMaybeUnionType().getRestrictedUnion(getNativeType(JSTypeNative.BOOLEAN_TYPE));  
/*   0*/    } else {
/*1234*/      type = null;
/*1235*/      literals = new BooleanOutcomePair(BooleanLiteralSet.BOTH, BooleanLiteralSet.BOTH, leftLiterals.getJoinedFlowScope(), rightLiterals.getJoinedFlowScope());
/*   0*/    } 
/*1240*/    n.setJSType(type);
/*1242*/    return literals;
/*   0*/  }
/*   0*/  
/*   0*/  private BooleanOutcomePair traverseWithinShortCircuitingBinOp(Node n, FlowScope scope) {
/*1247*/    switch (n.getType()) {
/*   0*/      case 101:
/*1249*/        return traverseAnd(n, scope);
/*   0*/      case 100:
/*1252*/        return traverseOr(n, scope);
/*   0*/    } 
/*1255*/    scope = traverse(n, scope);
/*1256*/    return newBooleanOutcomePair(n.getJSType(), scope);
/*   0*/  }
/*   0*/  
/*   0*/  BooleanOutcomePair getBooleanOutcomePair(BooleanOutcomePair left, BooleanOutcomePair right, boolean condition) {
/*1267*/    return new BooleanOutcomePair(getBooleanOutcomes(left.toBooleanOutcomes, right.toBooleanOutcomes, condition), getBooleanOutcomes(left.booleanValues, right.booleanValues, condition), left.getJoinedFlowScope(), right.getJoinedFlowScope());
/*   0*/  }
/*   0*/  
/*   0*/  static BooleanLiteralSet getBooleanOutcomes(BooleanLiteralSet left, BooleanLiteralSet right, boolean condition) {
/*1288*/    return right.union(left.intersection(BooleanLiteralSet.get(!condition)));
/*   0*/  }
/*   0*/  
/*   0*/  private final class BooleanOutcomePair {
/*   0*/    final BooleanLiteralSet toBooleanOutcomes;
/*   0*/    
/*   0*/    final BooleanLiteralSet booleanValues;
/*   0*/    
/*   0*/    final FlowScope leftScope;
/*   0*/    
/*   0*/    final FlowScope rightScope;
/*   0*/    
/*1308*/    FlowScope joinedScope = null;
/*   0*/    
/*   0*/    BooleanOutcomePair(BooleanLiteralSet toBooleanOutcomes, BooleanLiteralSet booleanValues, FlowScope leftScope, FlowScope rightScope) {
/*1313*/      this.toBooleanOutcomes = toBooleanOutcomes;
/*1314*/      this.booleanValues = booleanValues;
/*1315*/      this.leftScope = leftScope;
/*1316*/      this.rightScope = rightScope;
/*   0*/    }
/*   0*/    
/*   0*/    FlowScope getJoinedFlowScope() {
/*1324*/      if (this.joinedScope == null)
/*1325*/        if (this.leftScope == this.rightScope) {
/*1326*/          this.joinedScope = this.rightScope;
/*   0*/        } else {
/*1328*/          this.joinedScope = TypeInference.this.join(this.leftScope, this.rightScope);
/*   0*/        }  
/*1331*/      return this.joinedScope;
/*   0*/    }
/*   0*/    
/*   0*/    FlowScope getOutcomeFlowScope(int nodeType, boolean outcome) {
/*1339*/      if ((nodeType == 101 && outcome) || (nodeType == 100 && !outcome))
/*1342*/        return this.rightScope; 
/*1344*/      return getJoinedFlowScope();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private BooleanOutcomePair newBooleanOutcomePair(JSType jsType, FlowScope flowScope) {
/*1351*/    if (jsType == null)
/*1352*/      return new BooleanOutcomePair(BooleanLiteralSet.BOTH, BooleanLiteralSet.BOTH, flowScope, flowScope); 
/*1355*/    return new BooleanOutcomePair(jsType.getPossibleToBooleanOutcomes(), this.registry.getNativeType(JSTypeNative.BOOLEAN_TYPE).isSubtype(jsType) ? BooleanLiteralSet.BOTH : BooleanLiteralSet.EMPTY, flowScope, flowScope);
/*   0*/  }
/*   0*/  
/*   0*/  private void redeclareSimpleVar(FlowScope scope, Node nameNode, JSType varType) {
/*1363*/    Preconditions.checkState(nameNode.isName());
/*1364*/    String varName = nameNode.getString();
/*1365*/    if (varType == null)
/*1366*/      varType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1368*/    if (isUnflowable(this.syntacticScope.getVar(varName)))
/*   0*/      return; 
/*1371*/    scope.inferSlotType(varName, varType);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isUnflowable(Scope.Var v) {
/*1375*/    return (v != null && v.isLocal() && v.isMarkedEscaped() && v.getScope() == this.syntacticScope);
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getJSType(Node n) {
/*1385*/    JSType jsType = n.getJSType();
/*1386*/    if (jsType == null)
/*1391*/      return getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1393*/    return jsType;
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative typeId) {
/*1398*/    return this.registry.getNativeType(typeId);
/*   0*/  }
/*   0*/}
