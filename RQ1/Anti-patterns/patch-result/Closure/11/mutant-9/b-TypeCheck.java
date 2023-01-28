/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.javascript.jscomp.type.ReverseAbstractInterpreter;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.Token;
/*   0*/import com.google.javascript.rhino.jstype.EnumType;
/*   0*/import com.google.javascript.rhino.jstype.FunctionType;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeNative;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import com.google.javascript.rhino.jstype.ObjectType;
/*   0*/import com.google.javascript.rhino.jstype.TernaryValue;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class TypeCheck implements NodeTraversal.Callback, CompilerPass {
/*  59*/  static final DiagnosticType UNEXPECTED_TOKEN = DiagnosticType.error("JSC_INTERNAL_ERROR_UNEXPECTED_TOKEN", "Internal Error: Don't know how to handle {0}");
/*   0*/  
/*  68*/  static final DiagnosticType BAD_DELETE = DiagnosticType.warning("JSC_BAD_DELETE_OPERAND", "delete operator needs a reference operand");
/*   0*/  
/*   0*/  protected static final String OVERRIDING_PROTOTYPE_WITH_NON_OBJECT = "overriding prototype with non-object";
/*   0*/  
/*  83*/  static final DiagnosticType DETERMINISTIC_TEST = DiagnosticType.warning("JSC_DETERMINISTIC_TEST", "condition always evaluates to {2}\nleft : {0}\nright: {1}");
/*   0*/  
/*  90*/  static final DiagnosticType DETERMINISTIC_TEST_NO_RESULT = DiagnosticType.warning("JSC_DETERMINISTIC_TEST_NO_RESULT", "condition always evaluates to the same value\nleft : {0}\nright: {1}");
/*   0*/  
/*  97*/  static final DiagnosticType INEXISTENT_ENUM_ELEMENT = DiagnosticType.warning("JSC_INEXISTENT_ENUM_ELEMENT", "element {0} does not exist on this enum");
/*   0*/  
/* 104*/  static final DiagnosticType INEXISTENT_PROPERTY = DiagnosticType.disabled("JSC_INEXISTENT_PROPERTY", "Property {0} never defined on {1}");
/*   0*/  
/* 109*/  protected static final DiagnosticType NOT_A_CONSTRUCTOR = DiagnosticType.warning("JSC_NOT_A_CONSTRUCTOR", "cannot instantiate non-constructor");
/*   0*/  
/* 114*/  static final DiagnosticType BIT_OPERATION = DiagnosticType.warning("JSC_BAD_TYPE_FOR_BIT_OPERATION", "operator {0} cannot be applied to {1}");
/*   0*/  
/* 119*/  static final DiagnosticType NOT_CALLABLE = DiagnosticType.warning("JSC_NOT_FUNCTION_TYPE", "{0} expressions are not callable");
/*   0*/  
/* 124*/  static final DiagnosticType CONSTRUCTOR_NOT_CALLABLE = DiagnosticType.warning("JSC_CONSTRUCTOR_NOT_CALLABLE", "Constructor {0} should be called with the \"new\" keyword");
/*   0*/  
/* 129*/  static final DiagnosticType FUNCTION_MASKS_VARIABLE = DiagnosticType.warning("JSC_FUNCTION_MASKS_VARIABLE", "function {0} masks variable (IE bug)");
/*   0*/  
/* 134*/  static final DiagnosticType MULTIPLE_VAR_DEF = DiagnosticType.warning("JSC_MULTIPLE_VAR_DEF", "declaration of multiple variables with shared type information");
/*   0*/  
/* 138*/  static final DiagnosticType ENUM_DUP = DiagnosticType.error("JSC_ENUM_DUP", "enum element {0} already defined");
/*   0*/  
/* 141*/  static final DiagnosticType ENUM_NOT_CONSTANT = DiagnosticType.warning("JSC_ENUM_NOT_CONSTANT", "enum key {0} must be a syntactic constant");
/*   0*/  
/* 145*/  static final DiagnosticType INVALID_INTERFACE_MEMBER_DECLARATION = DiagnosticType.warning("JSC_INVALID_INTERFACE_MEMBER_DECLARATION", "interface members can only be empty property declarations, empty functions{0}");
/*   0*/  
/* 151*/  static final DiagnosticType INTERFACE_FUNCTION_NOT_EMPTY = DiagnosticType.warning("JSC_INTERFACE_FUNCTION_NOT_EMPTY", "interface member functions must have an empty body");
/*   0*/  
/* 156*/  static final DiagnosticType CONFLICTING_EXTENDED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_EXTENDED_TYPE", "{1} cannot extend this type; {0}s can only extend {0}s");
/*   0*/  
/* 161*/  static final DiagnosticType CONFLICTING_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_IMPLEMENTED_TYPE", "{0} cannot implement this type; an interface can only extend, but not implement interfaces");
/*   0*/  
/* 167*/  static final DiagnosticType BAD_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_IMPLEMENTS_NON_INTERFACE", "can only implement interfaces");
/*   0*/  
/* 172*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY", "property {0} already defined on superclass {1}; use @override to override it");
/*   0*/  
/* 178*/  static final DiagnosticType HIDDEN_INTERFACE_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_INTERFACE_PROPERTY", "property {0} already defined on interface {1}; use @override to override it");
/*   0*/  
/* 184*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY_MISMATCH = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY_MISMATCH", "mismatch of the {0} property type and the type of the property it overrides from superclass {1}\noriginal: {2}\noverride: {3}");
/*   0*/  
/* 191*/  static final DiagnosticType UNKNOWN_OVERRIDE = DiagnosticType.warning("JSC_UNKNOWN_OVERRIDE", "property {0} not defined on any superclass of {1}");
/*   0*/  
/* 196*/  static final DiagnosticType INTERFACE_METHOD_OVERRIDE = DiagnosticType.warning("JSC_INTERFACE_METHOD_OVERRIDE", "property {0} is already defined by the {1} extended interface");
/*   0*/  
/* 201*/  static final DiagnosticType UNKNOWN_EXPR_TYPE = DiagnosticType.warning("JSC_UNKNOWN_EXPR_TYPE", "could not determine the type of this expression");
/*   0*/  
/* 205*/  static final DiagnosticType UNRESOLVED_TYPE = DiagnosticType.warning("JSC_UNRESOLVED_TYPE", "could not resolve the name {0} to a type");
/*   0*/  
/* 209*/  static final DiagnosticType WRONG_ARGUMENT_COUNT = DiagnosticType.warning("JSC_WRONG_ARGUMENT_COUNT", "Function {0}: called with {1} argument(s). Function requires at least {2} argument(s){3}.");
/*   0*/  
/* 215*/  static final DiagnosticType ILLEGAL_IMPLICIT_CAST = DiagnosticType.warning("JSC_ILLEGAL_IMPLICIT_CAST", "Illegal annotation on {0}. @implicitCast may only be used in externs.");
/*   0*/  
/* 221*/  static final DiagnosticType INCOMPATIBLE_EXTENDED_PROPERTY_TYPE = DiagnosticType.warning("JSC_INCOMPATIBLE_EXTENDED_PROPERTY_TYPE", "Interface {0} has a property {1} with incompatible types in its super interfaces {2} and {3}");
/*   0*/  
/* 227*/  static final DiagnosticType EXPECTED_THIS_TYPE = DiagnosticType.warning("JSC_EXPECTED_THIS_TYPE", "\"{0}\" must be called with a \"this\" type");
/*   0*/  
/* 232*/  static final DiagnosticGroup ALL_DIAGNOSTICS = new DiagnosticGroup(new DiagnosticType[] { 
/* 232*/        DETERMINISTIC_TEST, DETERMINISTIC_TEST_NO_RESULT, INEXISTENT_ENUM_ELEMENT, INEXISTENT_PROPERTY, NOT_A_CONSTRUCTOR, BIT_OPERATION, NOT_CALLABLE, CONSTRUCTOR_NOT_CALLABLE, FUNCTION_MASKS_VARIABLE, MULTIPLE_VAR_DEF, 
/* 232*/        ENUM_DUP, ENUM_NOT_CONSTANT, INVALID_INTERFACE_MEMBER_DECLARATION, INTERFACE_FUNCTION_NOT_EMPTY, CONFLICTING_EXTENDED_TYPE, CONFLICTING_IMPLEMENTED_TYPE, BAD_IMPLEMENTED_TYPE, HIDDEN_SUPERCLASS_PROPERTY, HIDDEN_INTERFACE_PROPERTY, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, 
/* 232*/        UNKNOWN_OVERRIDE, INTERFACE_METHOD_OVERRIDE, UNKNOWN_EXPR_TYPE, UNRESOLVED_TYPE, WRONG_ARGUMENT_COUNT, ILLEGAL_IMPLICIT_CAST, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, EXPECTED_THIS_TYPE, RhinoErrorReporter.TYPE_PARSE_ERROR, TypedScopeCreator.UNKNOWN_LENDS, 
/* 232*/        TypedScopeCreator.LENDS_ON_NON_OBJECT, TypedScopeCreator.CTOR_INITIALIZER, TypedScopeCreator.IFACE_INITIALIZER, FunctionTypeBuilder.THIS_TYPE_NON_OBJECT });
/*   0*/  
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final TypeValidator validator;
/*   0*/  
/*   0*/  private final ReverseAbstractInterpreter reverseInterpreter;
/*   0*/  
/*   0*/  private final JSTypeRegistry typeRegistry;
/*   0*/  
/*   0*/  private Scope topScope;
/*   0*/  
/*   0*/  private ScopeCreator scopeCreator;
/*   0*/  
/*   0*/  private final CheckLevel reportMissingOverride;
/*   0*/  
/*   0*/  private final CheckLevel reportUnknownTypes;
/*   0*/  
/*   0*/  private boolean reportMissingProperties = true;
/*   0*/  
/* 285*/  private InferJSDocInfo inferJSDocInfo = null;
/*   0*/  
/* 288*/  private int typedCount = 0;
/*   0*/  
/* 289*/  private int nullCount = 0;
/*   0*/  
/* 290*/  private int unknownCount = 0;
/*   0*/  
/*   0*/  private boolean inExterns;
/*   0*/  
/* 295*/  private int noTypeCheckSection = 0;
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, Scope topScope, ScopeCreator scopeCreator, CheckLevel reportMissingOverride, CheckLevel reportUnknownTypes) {
/* 304*/    this.compiler = compiler;
/* 305*/    this.validator = compiler.getTypeValidator();
/* 306*/    this.reverseInterpreter = reverseInterpreter;
/* 307*/    this.typeRegistry = typeRegistry;
/* 308*/    this.topScope = topScope;
/* 309*/    this.scopeCreator = scopeCreator;
/* 310*/    this.reportMissingOverride = reportMissingOverride;
/* 311*/    this.reportUnknownTypes = reportUnknownTypes;
/* 312*/    this.inferJSDocInfo = new InferJSDocInfo(compiler);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, CheckLevel reportMissingOverride, CheckLevel reportUnknownTypes) {
/* 320*/    this(compiler, reverseInterpreter, typeRegistry, null, null, reportMissingOverride, reportUnknownTypes);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry) {
/* 327*/    this(compiler, reverseInterpreter, typeRegistry, null, null, CheckLevel.WARNING, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck reportMissingProperties(boolean report) {
/* 333*/    this.reportMissingProperties = report;
/* 334*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externsRoot, Node jsRoot) {
/* 346*/    Preconditions.checkNotNull(this.scopeCreator);
/* 347*/    Preconditions.checkNotNull(this.topScope);
/* 349*/    Node externsAndJs = jsRoot.getParent();
/* 350*/    Preconditions.checkState((externsAndJs != null));
/* 351*/    Preconditions.checkState((externsRoot == null || externsAndJs.hasChild(externsRoot)));
/* 354*/    if (externsRoot != null)
/* 355*/      check(externsRoot, true); 
/* 357*/    check(jsRoot, false);
/*   0*/  }
/*   0*/  
/*   0*/  public Scope processForTesting(Node externsRoot, Node jsRoot) {
/* 362*/    Preconditions.checkState((this.scopeCreator == null));
/* 363*/    Preconditions.checkState((this.topScope == null));
/* 365*/    Preconditions.checkState((jsRoot.getParent() != null));
/* 366*/    Node externsAndJsRoot = jsRoot.getParent();
/* 368*/    this.scopeCreator = new MemoizedScopeCreator(new TypedScopeCreator(this.compiler));
/* 369*/    this.topScope = this.scopeCreator.createScope(externsAndJsRoot, null);
/* 371*/    TypeInferencePass inference = new TypeInferencePass(this.compiler, this.reverseInterpreter, this.topScope, this.scopeCreator);
/* 374*/    inference.process(externsRoot, jsRoot);
/* 375*/    process(externsRoot, jsRoot);
/* 377*/    return this.topScope;
/*   0*/  }
/*   0*/  
/*   0*/  public void check(Node node, boolean externs) {
/* 382*/    Preconditions.checkNotNull(node);
/* 384*/    NodeTraversal t = new NodeTraversal(this.compiler, this, this.scopeCreator);
/* 385*/    this.inExterns = externs;
/* 386*/    t.traverseWithScope(node, this.topScope);
/* 387*/    if (externs) {
/* 388*/      this.inferJSDocInfo.process(node, null);
/*   0*/    } else {
/* 390*/      this.inferJSDocInfo.process(null, node);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNoTypeCheckSection(Node n, boolean enterSection) {
/*   0*/    JSDocInfo info;
/* 396*/    switch (n.getType()) {
/*   0*/      case 86:
/*   0*/      case 105:
/*   0*/      case 118:
/*   0*/      case 125:
/*   0*/      case 132:
/* 402*/        info = n.getJSDocInfo();
/* 403*/        if (info != null && info.isNoTypeCheck())
/* 404*/          if (enterSection) {
/* 405*/            this.noTypeCheckSection++;
/*   0*/          } else {
/* 407*/            this.noTypeCheckSection--;
/*   0*/          }  
/* 410*/        this.validator.setShouldReport((this.noTypeCheckSection == 0));
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void report(NodeTraversal t, Node n, DiagnosticType diagnosticType, String... arguments) {
/* 417*/    if (this.noTypeCheckSection == 0)
/* 418*/      t.report(n, diagnosticType, arguments); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/*   0*/    Scope outerScope;
/*   0*/    String functionPrivateName;
/* 425*/    checkNoTypeCheckSection(n, true);
/* 426*/    switch (n.getType()) {
/*   0*/      case 105:
/* 429*/        outerScope = t.getScope();
/* 430*/        functionPrivateName = n.getFirstChild().getString();
/* 431*/        if (functionPrivateName != null && functionPrivateName.length() > 0 && outerScope.isDeclared(functionPrivateName, false) && !(outerScope.getVar(functionPrivateName).getType() instanceof FunctionType))
/* 438*/          report(t, n, FUNCTION_MASKS_VARIABLE, new String[] { functionPrivateName }); 
/*   0*/        break;
/*   0*/    } 
/* 446*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/    JSType childType, leftType, rightType;
/*   0*/    Node left, right;
/*   0*/    JSType leftTypeRestricted, switchType, rightTypeRestricted, caseType;
/*   0*/    TernaryValue result;
/*   0*/    Node child;
/*   0*/    boolean typeable = true;
/* 467*/    switch (n.getType()) {
/*   0*/      case 38:
/* 469*/        typeable = visitName(t, n, parent);
/*   0*/        break;
/*   0*/      case 83:
/* 473*/        typeable = false;
/*   0*/        break;
/*   0*/      case 85:
/* 477*/        ensureTyped(t, n, getJSType(n.getLastChild()));
/*   0*/        break;
/*   0*/      case 43:
/*   0*/      case 44:
/* 482*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 42:
/* 486*/        ensureTyped(t, n, t.getScope().getTypeOfThis());
/*   0*/        break;
/*   0*/      case 41:
/* 490*/        ensureTyped(t, n, JSTypeNative.NULL_TYPE);
/*   0*/        break;
/*   0*/      case 39:
/* 494*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 40:
/* 498*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 154:
/* 502*/        typeable = false;
/*   0*/        break;
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/        break;
/*   0*/      case 63:
/* 511*/        ensureTyped(t, n, JSTypeNative.ARRAY_TYPE);
/*   0*/        break;
/*   0*/      case 47:
/* 515*/        ensureTyped(t, n, JSTypeNative.REGEXP_TYPE);
/*   0*/        break;
/*   0*/      case 33:
/* 519*/        visitGetProp(t, n, parent);
/* 520*/        typeable = (!parent.isAssign() || parent.getFirstChild() != n);
/*   0*/        break;
/*   0*/      case 35:
/* 525*/        visitGetElem(t, n);
/* 529*/        typeable = false;
/*   0*/        break;
/*   0*/      case 118:
/* 533*/        visitVar(t, n);
/* 534*/        typeable = false;
/*   0*/        break;
/*   0*/      case 30:
/* 538*/        visitNew(t, n);
/*   0*/        break;
/*   0*/      case 37:
/* 542*/        visitCall(t, n);
/* 543*/        typeable = !parent.isExprResult();
/*   0*/        break;
/*   0*/      case 4:
/* 547*/        visitReturn(t, n);
/* 548*/        typeable = false;
/*   0*/        break;
/*   0*/      case 102:
/*   0*/      case 103:
/* 553*/        left = n.getFirstChild();
/* 554*/        this.validator.expectNumber(t, left, getJSType(left), "increment/decrement");
/* 556*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 26:
/* 560*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 122:
/* 564*/        ensureTyped(t, n, JSTypeNative.VOID_TYPE);
/*   0*/        break;
/*   0*/      case 32:
/* 568*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 27:
/* 572*/        childType = getJSType(n.getFirstChild());
/* 573*/        if (!childType.matchesInt32Context())
/* 574*/          report(t, n, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), childType.toString() }); 
/* 577*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 28:
/*   0*/      case 29:
/* 582*/        left = n.getFirstChild();
/* 583*/        this.validator.expectNumber(t, left, getJSType(left), "sign operator");
/* 584*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 45:
/*   0*/      case 46:
/* 591*/        left = n.getFirstChild();
/* 592*/        right = n.getLastChild();
/* 594*/        if (left.isTypeOf()) {
/* 595*/          if (right.isString())
/* 596*/            checkTypeofString(t, right, right.getString()); 
/* 598*/        } else if (right.isTypeOf() && left.isString()) {
/* 599*/          checkTypeofString(t, left, left.getString());
/*   0*/        } 
/* 602*/        leftType = getJSType(left);
/* 603*/        rightType = getJSType(right);
/* 614*/        leftTypeRestricted = leftType.restrictByNotNullOrUndefined();
/* 615*/        rightTypeRestricted = rightType.restrictByNotNullOrUndefined();
/* 617*/        result = TernaryValue.UNKNOWN;
/* 618*/        if (n.getType() == 12 || n.getType() == 13) {
/* 619*/          result = leftTypeRestricted.testForEquality(rightTypeRestricted);
/* 620*/          if (n.isNE())
/* 621*/            result = result.not(); 
/* 625*/        } else if (!leftTypeRestricted.canTestForShallowEqualityWith(rightTypeRestricted)) {
/* 627*/          result = (n.getType() == 45) ? TernaryValue.FALSE : TernaryValue.TRUE;
/*   0*/        } 
/* 632*/        if (result != TernaryValue.UNKNOWN)
/* 633*/          report(t, n, DETERMINISTIC_TEST, new String[] { leftType.toString(), rightType.toString(), result.toString() }); 
/* 636*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/* 644*/        leftType = getJSType(n.getFirstChild());
/* 645*/        rightType = getJSType(n.getLastChild());
/* 646*/        if (rightType.isNumber()) {
/* 647*/          this.validator.expectNumber(t, n, leftType, "left side of numeric comparison");
/* 649*/        } else if (leftType.isNumber()) {
/* 650*/          this.validator.expectNumber(t, n, rightType, "right side of numeric comparison");
/* 652*/        } else if (!leftType.matchesNumberContext() || !rightType.matchesNumberContext()) {
/* 659*/          String message = "left side of comparison";
/* 660*/          this.validator.expectString(t, n, leftType, message);
/* 661*/          this.validator.expectNotNullOrUndefined(t, n, leftType, message, getNativeType(JSTypeNative.STRING_TYPE));
/* 663*/          message = "right side of comparison";
/* 664*/          this.validator.expectString(t, n, rightType, message);
/* 665*/          this.validator.expectNotNullOrUndefined(t, n, rightType, message, getNativeType(JSTypeNative.STRING_TYPE));
/*   0*/        } 
/* 668*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 51:
/* 672*/        this.validator.expectObject(t, n, getJSType(n.getLastChild()), "'in' requires an object");
/* 674*/        left = n.getFirstChild();
/* 675*/        this.validator.expectString(t, left, getJSType(left), "left side of 'in'");
/* 676*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 52:
/* 680*/        left = n.getFirstChild();
/* 681*/        right = n.getLastChild();
/* 682*/        rightType = getJSType(right).restrictByNotNullOrUndefined();
/* 683*/        this.validator.expectAnyObject(t, left, getJSType(left), "deterministic instanceof yields false");
/* 685*/        this.validator.expectActualObject(t, right, rightType, "instanceof requires an object");
/* 687*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 86:
/* 691*/        visitAssign(t, n);
/* 692*/        typeable = false;
/*   0*/        break;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 21:
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*   0*/      case 93:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/* 717*/        visitBinaryOperator(n.getType(), t, n);
/*   0*/        break;
/*   0*/      case 31:
/* 721*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 111:
/* 725*/        switchType = getJSType(parent.getFirstChild());
/* 726*/        caseType = getJSType(n.getFirstChild());
/* 727*/        this.validator.expectSwitchMatchesCase(t, n, switchType, caseType);
/* 728*/        typeable = false;
/*   0*/        break;
/*   0*/      case 119:
/* 732*/        child = n.getFirstChild();
/* 733*/        childType = getJSType(child);
/* 734*/        this.validator.expectObject(t, child, childType, "with requires an object");
/* 735*/        typeable = false;
/*   0*/        break;
/*   0*/      case 105:
/* 740*/        visitFunction(t, n);
/*   0*/        break;
/*   0*/      case 49:
/*   0*/      case 77:
/*   0*/      case 110:
/*   0*/      case 112:
/*   0*/      case 116:
/*   0*/      case 117:
/*   0*/      case 120:
/*   0*/      case 124:
/*   0*/      case 125:
/*   0*/      case 126:
/*   0*/      case 130:
/*   0*/      case 132:
/*   0*/      case 152:
/*   0*/      case 153:
/* 758*/        typeable = false;
/*   0*/        break;
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/* 766*/        typeable = false;
/*   0*/        break;
/*   0*/      case 64:
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/* 774*/        if (n.getJSType() != null) {
/* 775*/          ensureTyped(t, n);
/* 778*/        } else if (n.isObjectLit() && parent.getJSType() instanceof EnumType) {
/* 780*/          ensureTyped(t, n, parent.getJSType());
/*   0*/        } else {
/* 782*/          ensureTyped(t, n);
/*   0*/        } 
/* 785*/        if (n.isObjectLit())
/* 786*/          for (Node key : n.children())
/* 787*/            visitObjLitKey(t, key, n);  
/*   0*/        break;
/*   0*/      default:
/* 793*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(n.getType()) });
/* 794*/        ensureTyped(t, n);
/*   0*/        break;
/*   0*/    } 
/* 799*/    typeable = (typeable && !this.inExterns);
/* 801*/    if (typeable)
/* 802*/      doPercentTypedAccounting(t, n); 
/* 805*/    checkNoTypeCheckSection(n, false);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkTypeofString(NodeTraversal t, Node n, String s) {
/* 809*/    if (!s.equals("number") && !s.equals("string") && !s.equals("boolean") && !s.equals("undefined") && !s.equals("function") && !s.equals("object") && !s.equals("unknown"))
/* 812*/      this.validator.expectValidTypeofName(t, n, s); 
/*   0*/  }
/*   0*/  
/*   0*/  private void doPercentTypedAccounting(NodeTraversal t, Node n) {
/* 821*/    JSType type = n.getJSType();
/* 822*/    if (type == null) {
/* 823*/      this.nullCount++;
/* 824*/    } else if (type.isUnknownType()) {
/* 825*/      if (this.reportUnknownTypes.isOn())
/* 826*/        this.compiler.report(t.makeError(n, this.reportUnknownTypes, UNKNOWN_EXPR_TYPE, new String[0])); 
/* 829*/      this.unknownCount++;
/*   0*/    } else {
/* 831*/      this.typedCount++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitAssign(NodeTraversal t, Node assign) {
/* 844*/    JSDocInfo info = assign.getJSDocInfo();
/* 845*/    Node lvalue = assign.getFirstChild();
/* 846*/    Node rvalue = assign.getLastChild();
/* 849*/    if (lvalue.isGetProp()) {
/* 850*/      Node object = lvalue.getFirstChild();
/* 851*/      JSType objectJsType = getJSType(object);
/* 852*/      String property = lvalue.getLastChild().getString();
/* 856*/      if (object.isGetProp()) {
/* 857*/        JSType jsType = getJSType(object.getFirstChild());
/* 858*/        if (jsType.isInterface() && object.getLastChild().getString().equals("prototype"))
/* 860*/          visitInterfaceGetprop(t, assign, object, property, lvalue, rvalue); 
/*   0*/      } 
/* 864*/      checkEnumAlias(t, info, rvalue);
/* 870*/      if (property.equals("prototype") && 
/* 871*/        objectJsType != null && objectJsType.isFunctionType()) {
/* 872*/        FunctionType functionType = objectJsType.toMaybeFunctionType();
/* 873*/        if (functionType.isConstructor()) {
/* 874*/          JSType rvalueType = rvalue.getJSType();
/* 875*/          this.validator.expectObject(t, rvalue, rvalueType, "overriding prototype with non-object");
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 885*/      ObjectType type = ObjectType.cast(objectJsType.restrictByNotNullOrUndefined());
/* 887*/      if (type != null && 
/* 888*/        type.hasProperty(property) && !type.isPropertyTypeInferred(property) && !propertyIsImplicitCast(type, property)) {
/* 891*/        JSType expectedType = type.getPropertyType(property);
/* 892*/        if (!expectedType.isUnknownType()) {
/* 893*/          this.validator.expectCanAssignToPropertyOf(t, assign, getJSType(rvalue), expectedType, object, property);
/* 896*/          checkPropertyInheritanceOnGetpropAssign(t, assign, object, property, info, expectedType);
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 905*/      checkPropertyInheritanceOnGetpropAssign(t, assign, object, property, info, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/    } 
/* 914*/    JSType leftType = getJSType(lvalue);
/* 915*/    if (lvalue.isQualifiedName()) {
/* 917*/      JSType rvalueType = getJSType(assign.getLastChild());
/* 918*/      Scope.Var var = t.getScope().getVar(lvalue.getQualifiedName());
/* 919*/      if (var != null) {
/* 920*/        if (var.isTypeInferred())
/*   0*/          return; 
/* 924*/        if (NodeUtil.getRootOfQualifiedName(lvalue).isThis() && t.getScope() != var.getScope())
/*   0*/          return; 
/* 930*/        if (var.getType() != null)
/* 931*/          leftType = var.getType(); 
/*   0*/      } 
/*   0*/    } 
/* 937*/    Node rightChild = assign.getLastChild();
/* 938*/    JSType rightType = getJSType(rightChild);
/* 939*/    if (this.validator.expectCanAssignTo(t, assign, rightType, leftType, "assignment")) {
/* 941*/      ensureTyped(t, assign, rightType);
/*   0*/    } else {
/* 943*/      ensureTyped(t, assign);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyInheritanceOnGetpropAssign(NodeTraversal t, Node assign, Node object, String property, JSDocInfo info, JSType propertyType) {
/* 961*/    if (object.isGetProp()) {
/* 962*/      Node object2 = object.getFirstChild();
/* 963*/      String property2 = NodeUtil.getStringValue(object.getLastChild());
/* 965*/      if ("prototype".equals(property2)) {
/* 966*/        JSType jsType = getJSType(object2);
/* 967*/        if (jsType.isFunctionType()) {
/* 968*/          FunctionType functionType = jsType.toMaybeFunctionType();
/* 969*/          if (functionType.isConstructor() || functionType.isInterface())
/* 970*/            checkDeclaredPropertyInheritance(t, assign, functionType, property, info, propertyType); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitObjLitKey(NodeTraversal t, Node key, Node objlit) {
/* 990*/    if (objlit.isFromExterns()) {
/* 991*/      ensureTyped(t, key);
/*   0*/      return;
/*   0*/    } 
/*1000*/    Node rvalue = key.getFirstChild();
/*1001*/    JSType rightType = NodeUtil.getObjectLitKeyTypeFromValueType(key, getJSType(rvalue));
/*1003*/    if (rightType == null)
/*1004*/      rightType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1007*/    Node owner = objlit;
/*1011*/    JSType keyType = getJSType(key);
/*1013*/    JSType allowedValueType = keyType;
/*1014*/    if (allowedValueType.isEnumElementType())
/*1015*/      allowedValueType = allowedValueType.toMaybeEnumElementType().getPrimitiveType(); 
/*1019*/    boolean valid = this.validator.expectCanAssignToPropertyOf(t, key, rightType, allowedValueType, owner, NodeUtil.getObjectLitKeyName(key));
/*1022*/    if (valid) {
/*1023*/      ensureTyped(t, key, rightType);
/*   0*/    } else {
/*1025*/      ensureTyped(t, key);
/*   0*/    } 
/*1034*/    JSType objlitType = getJSType(objlit);
/*1035*/    ObjectType type = ObjectType.cast(objlitType.restrictByNotNullOrUndefined());
/*1037*/    if (type != null) {
/*1038*/      String property = NodeUtil.getObjectLitKeyName(key);
/*1039*/      if (type.hasProperty(property) && !type.isPropertyTypeInferred(property) && !propertyIsImplicitCast(type, property))
/*1042*/        this.validator.expectCanAssignToPropertyOf(t, key, keyType, type.getPropertyType(property), owner, property); 
/*   0*/      return;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean propertyIsImplicitCast(ObjectType type, String prop) {
/*1055*/    for (; type != null; type = type.getImplicitPrototype()) {
/*1056*/      JSDocInfo docInfo = type.getOwnPropertyJSDocInfo(prop);
/*1057*/      if (docInfo != null && docInfo.isImplicitCast())
/*1058*/        return true; 
/*   0*/    } 
/*1061*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkDeclaredPropertyInheritance(NodeTraversal t, Node n, FunctionType ctorType, String propertyName, JSDocInfo info, JSType propertyType) {
/*1075*/    if (hasUnknownOrEmptySupertype(ctorType))
/*   0*/      return; 
/*1079*/    FunctionType superClass = ctorType.getSuperClassConstructor();
/*1080*/    boolean superClassHasProperty = (superClass != null && superClass.getInstanceType().hasProperty(propertyName));
/*1082*/    boolean superClassHasDeclaredProperty = (superClass != null && superClass.getInstanceType().isPropertyTypeDeclared(propertyName));
/*   0*/    boolean superInterfaceHasProperty = false;
/*   0*/    boolean superInterfaceHasDeclaredProperty = false;
/*1088*/    if (ctorType.isInterface())
/*1089*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1090*/        superInterfaceHasProperty = (superInterfaceHasProperty || interfaceType.hasProperty(propertyName));
/*1093*/        superInterfaceHasDeclaredProperty = (superInterfaceHasDeclaredProperty || interfaceType.isPropertyTypeDeclared(propertyName));
/*   0*/      }  
/*1098*/    boolean declaredOverride = (info != null && info.isOverride());
/*   0*/    boolean foundInterfaceProperty = false;
/*1101*/    if (ctorType.isConstructor())
/*1103*/      for (JSType implementedInterface : ctorType.getAllImplementedInterfaces()) {
/*1104*/        if (implementedInterface.isUnknownType() || implementedInterface.isEmptyType())
/*   0*/          continue; 
/*1108*/        FunctionType interfaceType = implementedInterface.toObjectType().getConstructor();
/*1110*/        Preconditions.checkNotNull(interfaceType);
/*1112*/        boolean interfaceHasProperty = interfaceType.getPrototype().hasProperty(propertyName);
/*1114*/        foundInterfaceProperty = (foundInterfaceProperty || interfaceHasProperty);
/*1116*/        if (this.reportMissingOverride.isOn() && !declaredOverride && interfaceHasProperty)
/*1121*/          this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_INTERFACE_PROPERTY, new String[] { propertyName, interfaceType.getTopMostDefiningType(propertyName).toString() })); 
/*   0*/      }  
/*1128*/    if (!declaredOverride && !superClassHasProperty && !superInterfaceHasProperty)
/*   0*/      return; 
/*1135*/    ObjectType topInstanceType = superClassHasDeclaredProperty ? superClass.getTopMostDefiningType(propertyName) : null;
/*1137*/    boolean declaredLocally = (ctorType.isConstructor() && (ctorType.getPrototype().hasOwnProperty(propertyName) || ctorType.getInstanceType().hasOwnProperty(propertyName)));
/*1141*/    if (this.reportMissingOverride.isOn() && !declaredOverride && superClassHasDeclaredProperty && declaredLocally)
/*1147*/      this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_SUPERCLASS_PROPERTY, new String[] { propertyName, topInstanceType.toString() })); 
/*1153*/    if (superClassHasDeclaredProperty) {
/*1155*/      JSType superClassPropType = superClass.getInstanceType().getPropertyType(propertyName);
/*1157*/      if (!propertyType.canAssignTo(superClassPropType))
/*1158*/        this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superClassPropType.toString(), propertyType.toString() })); 
/*1163*/    } else if (superInterfaceHasDeclaredProperty) {
/*1165*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1166*/        if (interfaceType.hasProperty(propertyName)) {
/*1167*/          JSType superPropertyType = interfaceType.getPropertyType(propertyName);
/*1169*/          if (!propertyType.canAssignTo(superPropertyType)) {
/*1170*/            topInstanceType = interfaceType.getConstructor().getTopMostDefiningType(propertyName);
/*1172*/            this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superPropertyType.toString(), propertyType.toString() }));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1180*/    } else if (!foundInterfaceProperty && !superClassHasProperty && !superInterfaceHasProperty) {
/*1184*/      this.compiler.report(t.makeError(n, UNKNOWN_OVERRIDE, new String[] { propertyName, ctorType.getInstanceType().toString() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean hasUnknownOrEmptySupertype(FunctionType ctor) {
/*1195*/    Preconditions.checkArgument((ctor.isConstructor() || ctor.isInterface()));
/*1196*/    Preconditions.checkArgument(!ctor.isUnknownType());
/*   0*/    while (true) {
/*1201*/      ObjectType maybeSuperInstanceType = ctor.getPrototype().getImplicitPrototype();
/*1203*/      if (maybeSuperInstanceType == null)
/*1204*/        return false; 
/*1206*/      if (maybeSuperInstanceType.isUnknownType() || maybeSuperInstanceType.isEmptyType())
/*1208*/        return true; 
/*1210*/      ctor = maybeSuperInstanceType.getConstructor();
/*1211*/      if (ctor == null)
/*1212*/        return false; 
/*1214*/      Preconditions.checkState((ctor.isConstructor() || ctor.isInterface()));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitInterfaceGetprop(NodeTraversal t, Node assign, Node object, String property, Node lvalue, Node rvalue) {
/*1227*/    JSType rvalueType = getJSType(rvalue);
/*1235*/    String abstractMethodName = this.compiler.getCodingConvention().getAbstractMethodName();
/*1237*/    if (!rvalueType.isFunctionType()) {
/*1239*/      String abstractMethodMessage = (abstractMethodName != null) ? (", or " + abstractMethodName) : "";
/*1242*/      this.compiler.report(t.makeError(object, INVALID_INTERFACE_MEMBER_DECLARATION, new String[] { abstractMethodMessage }));
/*   0*/    } 
/*1247*/    if (assign.getLastChild().isFunction() && !NodeUtil.isEmptyBlock(assign.getLastChild().getLastChild()))
/*1249*/      this.compiler.report(t.makeError(object, INTERFACE_FUNCTION_NOT_EMPTY, new String[] { abstractMethodName })); 
/*   0*/  }
/*   0*/  
/*   0*/  boolean visitName(NodeTraversal t, Node n, Node parent) {
/*1272*/    int parentNodeType = parent.getType();
/*1273*/    if (parentNodeType == 105 || parentNodeType == 120 || parentNodeType == 83 || parentNodeType == 118)
/*1277*/      return false; 
/*1280*/    JSType type = n.getJSType();
/*1281*/    if (type == null) {
/*1282*/      type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*1283*/      Scope.Var var = t.getScope().getVar(n.getString());
/*1284*/      if (var != null) {
/*1285*/        JSType varType = var.getType();
/*1286*/        if (varType != null)
/*1287*/          type = varType; 
/*   0*/      } 
/*   0*/    } 
/*1291*/    ensureTyped(t, n, type);
/*1292*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetProp(NodeTraversal t, Node n, Node parent) {
/*1308*/    Node property = n.getLastChild();
/*1309*/    Node objNode = n.getFirstChild();
/*1310*/    JSType childType = getJSType(objNode);
/*1312*/    if (childType.isDict()) {
/*1313*/      report(t, property, TypeValidator.ILLEGAL_PROPERTY_ACCESS, new String[] { "'.'", "dict" });
/*   0*/    } else {
/*1314*/      if (n.getJSType() != null)
/*   0*/        if (false)
/*   0*/          return;  
/*1316*/      if (this.validator.expectNotNullOrUndefined(t, n, childType, "No properties on this expression", getNativeType(JSTypeNative.OBJECT_TYPE)))
/*1318*/        checkPropertyAccess(childType, property.getString(), t, n); 
/*   0*/    } 
/*1320*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccess(JSType childType, String propName, NodeTraversal t, Node n) {
/*1336*/    JSType propType = getJSType(n);
/*1337*/    if (propType.isEquivalentTo(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE))) {
/*1338*/      childType = childType.autobox();
/*1339*/      ObjectType objectType = ObjectType.cast(childType);
/*1340*/      if (objectType != null) {
/*1344*/        if (!objectType.hasProperty(propName) || objectType.isEquivalentTo(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE)))
/*1347*/          if (objectType instanceof EnumType) {
/*1348*/            report(t, n, INEXISTENT_ENUM_ELEMENT, new String[] { propName });
/*   0*/          } else {
/*1350*/            checkPropertyAccessHelper(objectType, propName, t, n);
/*   0*/          }  
/*   0*/      } else {
/*1355*/        checkPropertyAccessHelper(childType, propName, t, n);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccessHelper(JSType objectType, String propName, NodeTraversal t, Node n) {
/*1362*/    if (!objectType.isEmptyType() && this.reportMissingProperties && !isPropertyTest(n))
/*1364*/      if (!this.typeRegistry.canPropertyBeDefined(objectType, propName))
/*1365*/        report(t, n, INEXISTENT_PROPERTY, new String[] { propName, this.validator.getReadableJSTypeName(n.getFirstChild(), true) });  
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isPropertyTest(Node getProp) {
/*1378*/    Node parent = getProp.getParent();
/*1379*/    switch (parent.getType()) {
/*   0*/      case 37:
/*1381*/        return (parent.getFirstChild() != getProp && this.compiler.getCodingConvention().isPropertyTestFunction(parent));
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*1388*/        return (NodeUtil.getConditionExpression(parent) == getProp);
/*   0*/      case 32:
/*   0*/      case 52:
/*1392*/        return true;
/*   0*/      case 98:
/*   0*/      case 101:
/*1396*/        return (parent.getFirstChild() == getProp);
/*   0*/      case 26:
/*1399*/        return (parent.getParent().isOr() && parent.getParent().getFirstChild() == parent);
/*   0*/    } 
/*1402*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetElem(NodeTraversal t, Node n) {
/*1413*/    this.validator.expectIndexMatch(t, n, getJSType(n.getFirstChild()), getJSType(n.getLastChild()));
/*1415*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void visitVar(NodeTraversal t, Node n) {
/*1429*/    JSDocInfo varInfo = n.hasOneChild() ? n.getJSDocInfo() : null;
/*1430*/    for (Node name : n.children()) {
/*1431*/      Node value = name.getFirstChild();
/*1433*/      Scope.Var var = t.getScope().getVar(name.getString());
/*1435*/      if (value != null) {
/*1436*/        JSType valueType = getJSType(value);
/*1437*/        JSType nameType = var.getType();
/*1438*/        nameType = (nameType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : nameType;
/*1440*/        JSDocInfo info = name.getJSDocInfo();
/*1441*/        if (info == null)
/*1442*/          info = varInfo; 
/*1445*/        checkEnumAlias(t, info, value);
/*1446*/        if (var.isTypeInferred()) {
/*1447*/          ensureTyped(t, name, valueType);
/*   0*/          continue;
/*   0*/        } 
/*1449*/        this.validator.expectCanAssignTo(t, value, valueType, nameType, "initializing variable");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitNew(NodeTraversal t, Node n) {
/*1460*/    Node constructor = n.getFirstChild();
/*1461*/    JSType type = getJSType(constructor).restrictByNotNullOrUndefined();
/*1462*/    if (type.isConstructor() || type.isEmptyType() || type.isUnknownType()) {
/*1463*/      FunctionType fnType = type.toMaybeFunctionType();
/*1464*/      if (fnType != null) {
/*1465*/        visitParameterList(t, n, fnType);
/*1466*/        ensureTyped(t, n, fnType.getInstanceType());
/*   0*/      } else {
/*1468*/        ensureTyped(t, n);
/*   0*/      } 
/*   0*/    } else {
/*1471*/      report(t, n, NOT_A_CONSTRUCTOR, new String[0]);
/*1472*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkInterfaceConflictProperties(NodeTraversal t, Node n, String functionName, HashMap<String, ObjectType> properties, HashMap<String, ObjectType> currentProperties, ObjectType interfaceType) {
/*1492*/    Set<String> currentPropertyNames = interfaceType.getPropertyNames();
/*1493*/    for (String name : currentPropertyNames) {
/*1494*/      ObjectType oType = properties.get(name);
/*1495*/      if (oType != null && 
/*1496*/        !interfaceType.getPropertyType(name).isEquivalentTo(oType.getPropertyType(name)))
/*1498*/        this.compiler.report(t.makeError(n, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, new String[] { functionName, name, oType.toString(), interfaceType.toString() })); 
/*1504*/      currentProperties.put(name, interfaceType);
/*   0*/    } 
/*1506*/    for (ObjectType iType : interfaceType.getCtorExtendedInterfaces())
/*1507*/      checkInterfaceConflictProperties(t, n, functionName, properties, currentProperties, iType); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitFunction(NodeTraversal t, Node n) {
/*1520*/    FunctionType functionType = JSType.toMaybeFunctionType(n.getJSType());
/*1521*/    String functionPrivateName = n.getFirstChild().getString();
/*1522*/    if (functionType.isConstructor()) {
/*1523*/      FunctionType baseConstructor = functionType.getSuperClassConstructor();
/*1524*/      if (baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE) && baseConstructor != null && baseConstructor.isInterface() && functionType.isConstructor()) {
/*1527*/        this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "constructor", functionPrivateName }));
/*   0*/      } else {
/*1531*/        if (baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE) && baseConstructor != null)
/*1533*/          if (functionType.makesStructs() && !baseConstructor.makesStructs()) {
/*1534*/            this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "struct", functionPrivateName }));
/*1536*/          } else if (functionType.makesDicts() && !baseConstructor.makesDicts()) {
/*1538*/            this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "dict", functionPrivateName }));
/*   0*/          }  
/*1543*/        for (JSType baseInterface : functionType.getImplementedInterfaces()) {
/*   0*/          boolean badImplementedType = false;
/*1545*/          ObjectType baseInterfaceObj = ObjectType.cast(baseInterface);
/*1546*/          if (baseInterfaceObj != null) {
/*1547*/            FunctionType interfaceConstructor = baseInterfaceObj.getConstructor();
/*1549*/            if (interfaceConstructor != null && !interfaceConstructor.isInterface())
/*1551*/              badImplementedType = true; 
/*   0*/          } else {
/*1554*/            badImplementedType = true;
/*   0*/          } 
/*1556*/          if (badImplementedType)
/*1557*/            report(t, n, BAD_IMPLEMENTED_TYPE, new String[] { functionPrivateName }); 
/*   0*/        } 
/*1561*/        this.validator.expectAllInterfaceProperties(t, n, functionType);
/*   0*/      } 
/*1563*/    } else if (functionType.isInterface()) {
/*1565*/      for (ObjectType extInterface : functionType.getExtendedInterfaces()) {
/*1566*/        if (extInterface.getConstructor() != null && !extInterface.getConstructor().isInterface())
/*1568*/          this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "interface", functionPrivateName })); 
/*   0*/      } 
/*1574*/      if (functionType.hasImplementedInterfaces())
/*1575*/        this.compiler.report(t.makeError(n, CONFLICTING_IMPLEMENTED_TYPE, new String[] { functionPrivateName })); 
/*1579*/      if (functionType.getExtendedInterfacesCount() > 1) {
/*1581*/        HashMap<String, ObjectType> properties = new HashMap<String, ObjectType>();
/*1583*/        HashMap<String, ObjectType> currentProperties = new HashMap<String, ObjectType>();
/*1585*/        for (ObjectType interfaceType : functionType.getExtendedInterfaces()) {
/*1586*/          currentProperties.clear();
/*1587*/          checkInterfaceConflictProperties(t, n, functionPrivateName, properties, currentProperties, interfaceType);
/*1589*/          properties.putAll(currentProperties);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitCall(NodeTraversal t, Node n) {
/*1603*/    Node child = n.getFirstChild();
/*1604*/    JSType childType = getJSType(child).restrictByNotNullOrUndefined();
/*1606*/    if (!childType.canBeCalled()) {
/*1607*/      report(t, n, NOT_CALLABLE, new String[] { childType.toString() });
/*1608*/      ensureTyped(t, n);
/*   0*/      return;
/*   0*/    } 
/*1614*/    if (childType.isFunctionType()) {
/*1615*/      FunctionType functionType = childType.toMaybeFunctionType();
/*   0*/      boolean isExtern = false;
/*1618*/      JSDocInfo functionJSDocInfo = functionType.getJSDocInfo();
/*1619*/      if (functionJSDocInfo != null && functionJSDocInfo.getAssociatedNode() != null)
/*1621*/        isExtern = functionJSDocInfo.getAssociatedNode().isFromExterns(); 
/*1627*/      if (functionType.isConstructor() && !functionType.isNativeObjectType() && (functionType.getReturnType().isUnknownType() || functionType.getReturnType().isVoidType() || !isExtern))
/*1632*/        report(t, n, CONSTRUCTOR_NOT_CALLABLE, new String[] { childType.toString() }); 
/*1637*/      if (functionType.isOrdinaryFunction() && !functionType.getTypeOfThis().isUnknownType() && !functionType.getTypeOfThis().isNativeObjectType() && !child.isGetElem() && !child.isGetProp())
/*1642*/        report(t, n, EXPECTED_THIS_TYPE, new String[] { functionType.toString() }); 
/*1645*/      visitParameterList(t, n, functionType);
/*1646*/      ensureTyped(t, n, functionType.getReturnType());
/*   0*/    } else {
/*1648*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitParameterList(NodeTraversal t, Node call, FunctionType functionType) {
/*1661*/    Iterator<Node> arguments = call.children().iterator();
/*1662*/    arguments.next();
/*1664*/    Iterator<Node> parameters = functionType.getParameters().iterator();
/*1665*/    int ordinal = 0;
/*1666*/    Node parameter = null;
/*1667*/    Node argument = null;
/*1668*/    while (arguments.hasNext() && (parameters.hasNext() || (parameter != null && parameter.isVarArgs()))) {
/*1673*/      if (parameters.hasNext())
/*1674*/        parameter = parameters.next(); 
/*1676*/      argument = arguments.next();
/*1677*/      ordinal++;
/*1679*/      this.validator.expectArgumentMatchesParameter(t, argument, getJSType(argument), getJSType(parameter), call, ordinal);
/*   0*/    } 
/*1683*/    int numArgs = call.getChildCount() - 1;
/*1684*/    int minArgs = functionType.getMinArguments();
/*1685*/    int maxArgs = functionType.getMaxArguments();
/*1686*/    if (minArgs > numArgs || maxArgs < numArgs)
/*1687*/      report(t, call, WRONG_ARGUMENT_COUNT, new String[] { this.validator.getReadableJSTypeName(call.getFirstChild(), false), String.valueOf(numArgs), String.valueOf(minArgs), (maxArgs != Integer.MAX_VALUE) ? (" and no more than " + maxArgs + " argument(s)") : "" }); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitReturn(NodeTraversal t, Node n) {
/*1703*/    JSType jsType = getJSType(t.getEnclosingFunction());
/*1705*/    if (jsType.isFunctionType()) {
/*   0*/      JSType actualReturnType;
/*1706*/      FunctionType functionType = jsType.toMaybeFunctionType();
/*1708*/      JSType returnType = functionType.getReturnType();
/*1712*/      if (returnType == null)
/*1713*/        returnType = getNativeType(JSTypeNative.VOID_TYPE); 
/*1717*/      Node valueNode = n.getFirstChild();
/*1719*/      if (valueNode == null) {
/*1720*/        actualReturnType = getNativeType(JSTypeNative.VOID_TYPE);
/*1721*/        valueNode = n;
/*   0*/      } else {
/*1723*/        actualReturnType = getJSType(valueNode);
/*   0*/      } 
/*1727*/      this.validator.expectCanAssignTo(t, valueNode, actualReturnType, returnType, "inconsistent return type");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitBinaryOperator(int op, NodeTraversal t, Node n) {
/*1743*/    Node left = n.getFirstChild();
/*1744*/    JSType leftType = getJSType(left);
/*1745*/    Node right = n.getLastChild();
/*1746*/    JSType rightType = getJSType(right);
/*1747*/    switch (op) {
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*1754*/        if (!leftType.matchesInt32Context())
/*1755*/          report(t, left, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), leftType.toString() }); 
/*1758*/        if (!rightType.matchesUint32Context())
/*1759*/          report(t, right, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), rightType.toString() }); 
/*   0*/        break;
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/*1772*/        this.validator.expectNumber(t, left, leftType, "left operand");
/*1773*/        this.validator.expectNumber(t, right, rightType, "right operand");
/*   0*/        break;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*1782*/        this.validator.expectBitwiseable(t, left, leftType, "bad left operand to bitwise operator");
/*1784*/        this.validator.expectBitwiseable(t, right, rightType, "bad right operand to bitwise operator");
/*   0*/        break;
/*   0*/      case 21:
/*   0*/      case 93:
/*   0*/        break;
/*   0*/      default:
/*1793*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(op) });
/*   0*/        break;
/*   0*/    } 
/*1795*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkEnumAlias(NodeTraversal t, JSDocInfo declInfo, Node value) {
/*1815*/    if (declInfo == null || !declInfo.hasEnumParameterType())
/*   0*/      return; 
/*1819*/    JSType valueType = getJSType(value);
/*1820*/    if (!valueType.isEnumType())
/*   0*/      return; 
/*1824*/    EnumType valueEnumType = valueType.toMaybeEnumType();
/*1825*/    JSType valueEnumPrimitiveType = valueEnumType.getElementsType().getPrimitiveType();
/*1827*/    this.validator.expectCanAssignTo(t, value, valueEnumPrimitiveType, declInfo.getEnumParameterType().evaluate(t.getScope(), this.typeRegistry), "incompatible enum element types");
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getJSType(Node n) {
/*1837*/    JSType jsType = n.getJSType();
/*1838*/    if (jsType == null)
/*1843*/      return getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1845*/    return jsType;
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n) {
/*1860*/    ensureTyped(t, n, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSTypeNative type) {
/*1864*/    ensureTyped(t, n, getNativeType(type));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSType type) {
/*1887*/    Preconditions.checkState((!n.isFunction() || type.isFunctionType() || type.isUnknownType()));
/*1890*/    JSDocInfo info = n.getJSDocInfo();
/*1891*/    if (info != null) {
/*1892*/      if (info.hasType()) {
/*1893*/        JSType infoType = info.getType().evaluate(t.getScope(), this.typeRegistry);
/*1894*/        this.validator.expectCanCast(t, n, infoType, type);
/*1895*/        type = infoType;
/*   0*/      } 
/*1898*/      if (info.isImplicitCast() && !this.inExterns) {
/*1899*/        String propName = n.isGetProp() ? n.getLastChild().getString() : "(missing)";
/*1901*/        this.compiler.report(t.makeError(n, ILLEGAL_IMPLICIT_CAST, new String[] { propName }));
/*   0*/      } 
/*   0*/    } 
/*1906*/    if (n.getJSType() == null)
/*1907*/      n.setJSType(type); 
/*   0*/  }
/*   0*/  
/*   0*/  double getTypedPercent() {
/*1916*/    int total = this.nullCount + this.unknownCount + this.typedCount;
/*1917*/    return (total == 0) ? 0.0D : (100.0D * this.typedCount / total);
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative typeId) {
/*1921*/    return this.typeRegistry.getNativeType(typeId);
/*   0*/  }
/*   0*/}
