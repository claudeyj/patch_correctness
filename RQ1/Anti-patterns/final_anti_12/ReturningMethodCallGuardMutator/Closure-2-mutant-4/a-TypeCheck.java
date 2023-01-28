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
/*  60*/  static final DiagnosticType UNEXPECTED_TOKEN = DiagnosticType.error("JSC_INTERNAL_ERROR_UNEXPECTED_TOKEN", "Internal Error: Don't know how to handle {0}");
/*   0*/  
/*  69*/  static final DiagnosticType BAD_DELETE = DiagnosticType.warning("JSC_BAD_DELETE_OPERAND", "delete operator needs a reference operand");
/*   0*/  
/*   0*/  protected static final String OVERRIDING_PROTOTYPE_WITH_NON_OBJECT = "overriding prototype with non-object";
/*   0*/  
/*  84*/  static final DiagnosticType DETERMINISTIC_TEST = DiagnosticType.warning("JSC_DETERMINISTIC_TEST", "condition always evaluates to {2}\nleft : {0}\nright: {1}");
/*   0*/  
/*  91*/  static final DiagnosticType DETERMINISTIC_TEST_NO_RESULT = DiagnosticType.warning("JSC_DETERMINISTIC_TEST_NO_RESULT", "condition always evaluates to the same value\nleft : {0}\nright: {1}");
/*   0*/  
/*  98*/  static final DiagnosticType INEXISTENT_ENUM_ELEMENT = DiagnosticType.warning("JSC_INEXISTENT_ENUM_ELEMENT", "element {0} does not exist on this enum");
/*   0*/  
/* 105*/  static final DiagnosticType INEXISTENT_PROPERTY = DiagnosticType.disabled("JSC_INEXISTENT_PROPERTY", "Property {0} never defined on {1}");
/*   0*/  
/* 110*/  protected static final DiagnosticType NOT_A_CONSTRUCTOR = DiagnosticType.warning("JSC_NOT_A_CONSTRUCTOR", "cannot instantiate non-constructor");
/*   0*/  
/* 115*/  static final DiagnosticType BIT_OPERATION = DiagnosticType.warning("JSC_BAD_TYPE_FOR_BIT_OPERATION", "operator {0} cannot be applied to {1}");
/*   0*/  
/* 120*/  static final DiagnosticType NOT_CALLABLE = DiagnosticType.warning("JSC_NOT_FUNCTION_TYPE", "{0} expressions are not callable");
/*   0*/  
/* 125*/  static final DiagnosticType CONSTRUCTOR_NOT_CALLABLE = DiagnosticType.warning("JSC_CONSTRUCTOR_NOT_CALLABLE", "Constructor {0} should be called with the \"new\" keyword");
/*   0*/  
/* 130*/  static final DiagnosticType FUNCTION_MASKS_VARIABLE = DiagnosticType.warning("JSC_FUNCTION_MASKS_VARIABLE", "function {0} masks variable (IE bug)");
/*   0*/  
/* 135*/  static final DiagnosticType MULTIPLE_VAR_DEF = DiagnosticType.warning("JSC_MULTIPLE_VAR_DEF", "declaration of multiple variables with shared type information");
/*   0*/  
/* 139*/  static final DiagnosticType ENUM_DUP = DiagnosticType.error("JSC_ENUM_DUP", "enum element {0} already defined");
/*   0*/  
/* 142*/  static final DiagnosticType ENUM_NOT_CONSTANT = DiagnosticType.warning("JSC_ENUM_NOT_CONSTANT", "enum key {0} must be a syntactic constant");
/*   0*/  
/* 146*/  static final DiagnosticType INVALID_INTERFACE_MEMBER_DECLARATION = DiagnosticType.warning("JSC_INVALID_INTERFACE_MEMBER_DECLARATION", "interface members can only be empty property declarations, empty functions{0}");
/*   0*/  
/* 152*/  static final DiagnosticType INTERFACE_FUNCTION_NOT_EMPTY = DiagnosticType.warning("JSC_INTERFACE_FUNCTION_NOT_EMPTY", "interface member functions must have an empty body");
/*   0*/  
/* 157*/  static final DiagnosticType CONFLICTING_EXTENDED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_EXTENDED_TYPE", "{1} cannot extend this type; {0}s can only extend {0}s");
/*   0*/  
/* 162*/  static final DiagnosticType CONFLICTING_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_IMPLEMENTED_TYPE", "{0} cannot implement this type; an interface can only extend, but not implement interfaces");
/*   0*/  
/* 168*/  static final DiagnosticType BAD_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_IMPLEMENTS_NON_INTERFACE", "can only implement interfaces");
/*   0*/  
/* 173*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY", "property {0} already defined on superclass {1}; use @override to override it");
/*   0*/  
/* 179*/  static final DiagnosticType HIDDEN_INTERFACE_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_INTERFACE_PROPERTY", "property {0} already defined on interface {1}; use @override to override it");
/*   0*/  
/* 185*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY_MISMATCH = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY_MISMATCH", "mismatch of the {0} property type and the type of the property it overrides from superclass {1}\noriginal: {2}\noverride: {3}");
/*   0*/  
/* 192*/  static final DiagnosticType UNKNOWN_OVERRIDE = DiagnosticType.warning("JSC_UNKNOWN_OVERRIDE", "property {0} not defined on any superclass of {1}");
/*   0*/  
/* 197*/  static final DiagnosticType INTERFACE_METHOD_OVERRIDE = DiagnosticType.warning("JSC_INTERFACE_METHOD_OVERRIDE", "property {0} is already defined by the {1} extended interface");
/*   0*/  
/* 202*/  static final DiagnosticType UNKNOWN_EXPR_TYPE = DiagnosticType.warning("JSC_UNKNOWN_EXPR_TYPE", "could not determine the type of this expression");
/*   0*/  
/* 206*/  static final DiagnosticType UNRESOLVED_TYPE = DiagnosticType.warning("JSC_UNRESOLVED_TYPE", "could not resolve the name {0} to a type");
/*   0*/  
/* 210*/  static final DiagnosticType WRONG_ARGUMENT_COUNT = DiagnosticType.warning("JSC_WRONG_ARGUMENT_COUNT", "Function {0}: called with {1} argument(s). Function requires at least {2} argument(s){3}.");
/*   0*/  
/* 216*/  static final DiagnosticType ILLEGAL_IMPLICIT_CAST = DiagnosticType.warning("JSC_ILLEGAL_IMPLICIT_CAST", "Illegal annotation on {0}. @implicitCast may only be used in externs.");
/*   0*/  
/* 222*/  static final DiagnosticType INCOMPATIBLE_EXTENDED_PROPERTY_TYPE = DiagnosticType.warning("JSC_INCOMPATIBLE_EXTENDED_PROPERTY_TYPE", "Interface {0} has a property {1} with incompatible types in its super interfaces {2} and {3}");
/*   0*/  
/* 228*/  static final DiagnosticType EXPECTED_THIS_TYPE = DiagnosticType.warning("JSC_EXPECTED_THIS_TYPE", "\"{0}\" must be called with a \"this\" type");
/*   0*/  
/* 233*/  static final DiagnosticType IN_USED_WITH_STRUCT = DiagnosticType.warning("JSC_IN_USED_WITH_STRUCT", "Cannot use the IN operator with structs");
/*   0*/  
/* 237*/  static final DiagnosticType ILLEGAL_PROPERTY_CREATION = DiagnosticType.warning("JSC_ILLEGAL_PROPERTY_CREATION", "Cannot add a property to a struct instance after it is constructed.");
/*   0*/  
/* 242*/  static final DiagnosticType ILLEGAL_OBJLIT_KEY = DiagnosticType.warning("ILLEGAL_OBJLIT_KEY", "Illegal key, the object literal is a {0}");
/*   0*/  
/* 247*/  static final DiagnosticGroup ALL_DIAGNOSTICS = new DiagnosticGroup(new DiagnosticType[] { 
/* 247*/        DETERMINISTIC_TEST, DETERMINISTIC_TEST_NO_RESULT, INEXISTENT_ENUM_ELEMENT, INEXISTENT_PROPERTY, NOT_A_CONSTRUCTOR, BIT_OPERATION, NOT_CALLABLE, CONSTRUCTOR_NOT_CALLABLE, FUNCTION_MASKS_VARIABLE, MULTIPLE_VAR_DEF, 
/* 247*/        ENUM_DUP, ENUM_NOT_CONSTANT, INVALID_INTERFACE_MEMBER_DECLARATION, INTERFACE_FUNCTION_NOT_EMPTY, CONFLICTING_EXTENDED_TYPE, CONFLICTING_IMPLEMENTED_TYPE, BAD_IMPLEMENTED_TYPE, HIDDEN_SUPERCLASS_PROPERTY, HIDDEN_INTERFACE_PROPERTY, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, 
/* 247*/        UNKNOWN_OVERRIDE, INTERFACE_METHOD_OVERRIDE, UNKNOWN_EXPR_TYPE, UNRESOLVED_TYPE, WRONG_ARGUMENT_COUNT, ILLEGAL_IMPLICIT_CAST, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, EXPECTED_THIS_TYPE, IN_USED_WITH_STRUCT, ILLEGAL_PROPERTY_CREATION, 
/* 247*/        ILLEGAL_OBJLIT_KEY, RhinoErrorReporter.TYPE_PARSE_ERROR, TypedScopeCreator.UNKNOWN_LENDS, TypedScopeCreator.LENDS_ON_NON_OBJECT, TypedScopeCreator.CTOR_INITIALIZER, TypedScopeCreator.IFACE_INITIALIZER, FunctionTypeBuilder.THIS_TYPE_NON_OBJECT });
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
/*   0*/  private MemoizedScopeCreator scopeCreator;
/*   0*/  
/*   0*/  private final CheckLevel reportMissingOverride;
/*   0*/  
/*   0*/  private final CheckLevel reportUnknownTypes;
/*   0*/  
/*   0*/  private boolean reportMissingProperties = true;
/*   0*/  
/* 303*/  private InferJSDocInfo inferJSDocInfo = null;
/*   0*/  
/* 306*/  private int typedCount = 0;
/*   0*/  
/* 307*/  private int nullCount = 0;
/*   0*/  
/* 308*/  private int unknownCount = 0;
/*   0*/  
/*   0*/  private boolean inExterns;
/*   0*/  
/* 313*/  private int noTypeCheckSection = 0;
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, Scope topScope, MemoizedScopeCreator scopeCreator, CheckLevel reportMissingOverride, CheckLevel reportUnknownTypes) {
/* 322*/    this.compiler = compiler;
/* 323*/    this.validator = compiler.getTypeValidator();
/* 324*/    this.reverseInterpreter = reverseInterpreter;
/* 325*/    this.typeRegistry = typeRegistry;
/* 326*/    this.topScope = topScope;
/* 327*/    this.scopeCreator = scopeCreator;
/* 328*/    this.reportMissingOverride = reportMissingOverride;
/* 329*/    this.reportUnknownTypes = reportUnknownTypes;
/* 330*/    this.inferJSDocInfo = new InferJSDocInfo(compiler);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, CheckLevel reportMissingOverride, CheckLevel reportUnknownTypes) {
/* 338*/    this(compiler, reverseInterpreter, typeRegistry, null, null, reportMissingOverride, reportUnknownTypes);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry) {
/* 345*/    this(compiler, reverseInterpreter, typeRegistry, null, null, CheckLevel.WARNING, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck reportMissingProperties(boolean report) {
/* 351*/    this.reportMissingProperties = report;
/* 352*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externsRoot, Node jsRoot) {
/* 364*/    Preconditions.checkNotNull(this.scopeCreator);
/* 365*/    Preconditions.checkNotNull(this.topScope);
/* 367*/    Node externsAndJs = jsRoot.getParent();
/* 368*/    Preconditions.checkState((externsAndJs != null));
/* 369*/    Preconditions.checkState((externsRoot == null || externsAndJs.hasChild(externsRoot)));
/* 372*/    if (externsRoot != null)
/* 373*/      check(externsRoot, true); 
/* 375*/    check(jsRoot, false);
/*   0*/  }
/*   0*/  
/*   0*/  public Scope processForTesting(Node externsRoot, Node jsRoot) {
/* 380*/    Preconditions.checkState((this.scopeCreator == null));
/* 381*/    Preconditions.checkState((this.topScope == null));
/* 383*/    Preconditions.checkState((jsRoot.getParent() != null));
/* 384*/    Node externsAndJsRoot = jsRoot.getParent();
/* 386*/    this.scopeCreator = new MemoizedScopeCreator(new TypedScopeCreator(this.compiler));
/* 387*/    this.topScope = this.scopeCreator.createScope(externsAndJsRoot, null);
/* 389*/    TypeInferencePass inference = new TypeInferencePass(this.compiler, this.reverseInterpreter, this.topScope, this.scopeCreator);
/* 392*/    inference.process(externsRoot, jsRoot);
/* 393*/    process(externsRoot, jsRoot);
/* 395*/    return this.topScope;
/*   0*/  }
/*   0*/  
/*   0*/  public void check(Node node, boolean externs) {
/* 400*/    Preconditions.checkNotNull(node);
/* 402*/    NodeTraversal t = new NodeTraversal(this.compiler, this, this.scopeCreator);
/* 403*/    this.inExterns = externs;
/* 404*/    t.traverseWithScope(node, this.topScope);
/* 405*/    if (externs) {
/* 406*/      this.inferJSDocInfo.process(node, null);
/*   0*/    } else {
/* 408*/      this.inferJSDocInfo.process(null, node);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNoTypeCheckSection(Node n, boolean enterSection) {
/*   0*/    JSDocInfo info;
/* 414*/    switch (n.getType()) {
/*   0*/      case 86:
/*   0*/      case 105:
/*   0*/      case 118:
/*   0*/      case 125:
/*   0*/      case 132:
/* 420*/        info = n.getJSDocInfo();
/* 421*/        if (info != null && info.isNoTypeCheck())
/* 422*/          if (enterSection) {
/* 423*/            this.noTypeCheckSection++;
/*   0*/          } else {
/* 425*/            this.noTypeCheckSection--;
/*   0*/          }  
/* 428*/        this.validator.setShouldReport((this.noTypeCheckSection == 0));
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void report(NodeTraversal t, Node n, DiagnosticType diagnosticType, String... arguments) {
/* 435*/    if (this.noTypeCheckSection == 0)
/* 436*/      t.report(n, diagnosticType, arguments); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/*   0*/    Scope outerScope;
/*   0*/    String functionPrivateName;
/* 443*/    checkNoTypeCheckSection(n, true);
/* 444*/    switch (n.getType()) {
/*   0*/      case 105:
/* 447*/        outerScope = t.getScope();
/* 448*/        functionPrivateName = n.getFirstChild().getString();
/* 449*/        if (functionPrivateName != null && functionPrivateName.length() > 0 && outerScope.isDeclared(functionPrivateName, false) && !(outerScope.getVar(functionPrivateName).getType() instanceof FunctionType))
/* 456*/          report(t, n, FUNCTION_MASKS_VARIABLE, new String[] { functionPrivateName }); 
/*   0*/        break;
/*   0*/    } 
/* 464*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/    JSType childType, leftType, rightType;
/*   0*/    Node left, right, expr;
/*   0*/    JSType castType, exprType, leftTypeRestricted, switchType, rightTypeRestricted, caseType;
/*   0*/    TernaryValue result;
/*   0*/    Node child;
/*   0*/    boolean typeable = true;
/* 485*/    switch (n.getType()) {
/*   0*/      case 155:
/* 487*/        expr = n.getFirstChild();
/* 488*/        ensureTyped(t, n, getJSType(expr));
/* 492*/        castType = getJSType(n);
/* 493*/        exprType = getJSType(expr);
/* 494*/        if (castType.isSubtype(exprType))
/* 495*/          expr.setJSType(castType); 
/*   0*/        break;
/*   0*/      case 38:
/* 500*/        typeable = visitName(t, n, parent);
/*   0*/        break;
/*   0*/      case 83:
/* 504*/        typeable = false;
/*   0*/        break;
/*   0*/      case 85:
/* 508*/        ensureTyped(t, n, getJSType(n.getLastChild()));
/*   0*/        break;
/*   0*/      case 43:
/*   0*/      case 44:
/* 513*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 42:
/* 517*/        ensureTyped(t, n, t.getScope().getTypeOfThis());
/*   0*/        break;
/*   0*/      case 41:
/* 521*/        ensureTyped(t, n, JSTypeNative.NULL_TYPE);
/*   0*/        break;
/*   0*/      case 39:
/* 525*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 40:
/* 529*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 154:
/* 533*/        typeable = false;
/*   0*/        break;
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/        break;
/*   0*/      case 63:
/* 542*/        ensureTyped(t, n, JSTypeNative.ARRAY_TYPE);
/*   0*/        break;
/*   0*/      case 47:
/* 546*/        ensureTyped(t, n, JSTypeNative.REGEXP_TYPE);
/*   0*/        break;
/*   0*/      case 33:
/* 550*/        visitGetProp(t, n, parent);
/* 551*/        typeable = (!parent.isAssign() || parent.getFirstChild() != n);
/*   0*/        break;
/*   0*/      case 35:
/* 556*/        visitGetElem(t, n);
/* 560*/        typeable = false;
/*   0*/        break;
/*   0*/      case 118:
/* 564*/        visitVar(t, n);
/* 565*/        typeable = false;
/*   0*/        break;
/*   0*/      case 30:
/* 569*/        visitNew(t, n);
/*   0*/        break;
/*   0*/      case 37:
/* 573*/        visitCall(t, n);
/* 574*/        typeable = !parent.isExprResult();
/*   0*/        break;
/*   0*/      case 4:
/* 578*/        visitReturn(t, n);
/* 579*/        typeable = false;
/*   0*/        break;
/*   0*/      case 102:
/*   0*/      case 103:
/* 584*/        left = n.getFirstChild();
/* 585*/        checkPropCreation(t, left);
/* 586*/        this.validator.expectNumber(t, left, getJSType(left), "increment/decrement");
/* 587*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 26:
/* 591*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 122:
/* 595*/        ensureTyped(t, n, JSTypeNative.VOID_TYPE);
/*   0*/        break;
/*   0*/      case 32:
/* 599*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 27:
/* 603*/        childType = getJSType(n.getFirstChild());
/* 604*/        if (!childType.matchesInt32Context())
/* 605*/          report(t, n, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), childType.toString() }); 
/* 608*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 28:
/*   0*/      case 29:
/* 613*/        left = n.getFirstChild();
/* 614*/        this.validator.expectNumber(t, left, getJSType(left), "sign operator");
/* 615*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 45:
/*   0*/      case 46:
/* 622*/        left = n.getFirstChild();
/* 623*/        right = n.getLastChild();
/* 625*/        if (left.isTypeOf()) {
/* 626*/          if (right.isString())
/* 627*/            checkTypeofString(t, right, right.getString()); 
/* 629*/        } else if (right.isTypeOf() && left.isString()) {
/* 630*/          checkTypeofString(t, left, left.getString());
/*   0*/        } 
/* 633*/        leftType = getJSType(left);
/* 634*/        rightType = getJSType(right);
/* 645*/        leftTypeRestricted = leftType.restrictByNotNullOrUndefined();
/* 646*/        rightTypeRestricted = rightType.restrictByNotNullOrUndefined();
/* 648*/        result = TernaryValue.UNKNOWN;
/* 649*/        if (n.getType() == 12 || n.getType() == 13) {
/* 650*/          result = leftTypeRestricted.testForEquality(rightTypeRestricted);
/* 651*/          if (n.isNE())
/* 652*/            result = result.not(); 
/* 656*/        } else if (!leftTypeRestricted.canTestForShallowEqualityWith(rightTypeRestricted)) {
/* 658*/          result = (n.getType() == 45) ? TernaryValue.FALSE : TernaryValue.TRUE;
/*   0*/        } 
/* 663*/        if (result != TernaryValue.UNKNOWN)
/* 664*/          report(t, n, DETERMINISTIC_TEST, new String[] { leftType.toString(), rightType.toString(), result.toString() }); 
/* 667*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/* 675*/        leftType = getJSType(n.getFirstChild());
/* 676*/        rightType = getJSType(n.getLastChild());
/* 677*/        if (rightType.isNumber()) {
/* 678*/          this.validator.expectNumber(t, n, leftType, "left side of numeric comparison");
/* 680*/        } else if (leftType.isNumber()) {
/* 681*/          this.validator.expectNumber(t, n, rightType, "right side of numeric comparison");
/* 683*/        } else if (!leftType.matchesNumberContext() || !rightType.matchesNumberContext()) {
/* 690*/          String message = "left side of comparison";
/* 691*/          this.validator.expectString(t, n, leftType, message);
/* 692*/          this.validator.expectNotNullOrUndefined(t, n, leftType, message, getNativeType(JSTypeNative.STRING_TYPE));
/* 694*/          message = "right side of comparison";
/* 695*/          this.validator.expectString(t, n, rightType, message);
/* 696*/          this.validator.expectNotNullOrUndefined(t, n, rightType, message, getNativeType(JSTypeNative.STRING_TYPE));
/*   0*/        } 
/* 699*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 51:
/* 703*/        left = n.getFirstChild();
/* 704*/        right = n.getLastChild();
/* 705*/        rightType = getJSType(right);
/* 706*/        this.validator.expectString(t, left, getJSType(left), "left side of 'in'");
/* 707*/        this.validator.expectObject(t, n, rightType, "'in' requires an object");
/* 708*/        if (rightType.isStruct())
/* 709*/          report(t, right, IN_USED_WITH_STRUCT, new String[0]); 
/* 711*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 52:
/* 715*/        left = n.getFirstChild();
/* 716*/        right = n.getLastChild();
/* 717*/        rightType = getJSType(right).restrictByNotNullOrUndefined();
/* 718*/        this.validator.expectAnyObject(t, left, getJSType(left), "deterministic instanceof yields false");
/* 720*/        this.validator.expectActualObject(t, right, rightType, "instanceof requires an object");
/* 722*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 86:
/* 726*/        visitAssign(t, n);
/* 727*/        typeable = false;
/*   0*/        break;
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
/* 741*/        checkPropCreation(t, n.getFirstChild());
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
/* 755*/        visitBinaryOperator(n.getType(), t, n);
/*   0*/        break;
/*   0*/      case 31:
/* 759*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 111:
/* 763*/        switchType = getJSType(parent.getFirstChild());
/* 764*/        caseType = getJSType(n.getFirstChild());
/* 765*/        this.validator.expectSwitchMatchesCase(t, n, switchType, caseType);
/* 766*/        typeable = false;
/*   0*/        break;
/*   0*/      case 119:
/* 770*/        child = n.getFirstChild();
/* 771*/        childType = getJSType(child);
/* 772*/        this.validator.expectObject(t, child, childType, "with requires an object");
/* 773*/        typeable = false;
/*   0*/        break;
/*   0*/      case 105:
/* 778*/        visitFunction(t, n);
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
/* 796*/        typeable = false;
/*   0*/        break;
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/* 803*/        typeable = false;
/*   0*/        break;
/*   0*/      case 115:
/* 807*/        if (NodeUtil.isForIn(n)) {
/* 808*/          Node obj = n.getChildAtIndex(1);
/* 809*/          if (getJSType(obj).isStruct())
/* 810*/            report(t, obj, IN_USED_WITH_STRUCT, new String[0]); 
/*   0*/        } 
/* 813*/        typeable = false;
/*   0*/        break;
/*   0*/      case 64:
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/* 821*/        if (n.getJSType() != null) {
/* 822*/          ensureTyped(t, n);
/* 825*/        } else if (n.isObjectLit() && parent.getJSType() instanceof EnumType) {
/* 827*/          ensureTyped(t, n, parent.getJSType());
/*   0*/        } else {
/* 829*/          ensureTyped(t, n);
/*   0*/        } 
/* 832*/        if (n.isObjectLit()) {
/* 833*/          JSType typ = getJSType(n);
/* 834*/          for (Node key : n.children())
/* 835*/            visitObjLitKey(t, key, n, typ); 
/*   0*/        } 
/*   0*/        break;
/*   0*/      default:
/* 841*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(n.getType()) });
/* 842*/        ensureTyped(t, n);
/*   0*/        break;
/*   0*/    } 
/* 847*/    typeable = (typeable && !this.inExterns);
/* 849*/    if (typeable)
/* 850*/      doPercentTypedAccounting(t, n); 
/* 853*/    checkNoTypeCheckSection(n, false);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkTypeofString(NodeTraversal t, Node n, String s) {
/* 857*/    if (!s.equals("number") && !s.equals("string") && !s.equals("boolean") && !s.equals("undefined") && !s.equals("function") && !s.equals("object") && !s.equals("unknown"))
/* 860*/      this.validator.expectValidTypeofName(t, n, s); 
/*   0*/  }
/*   0*/  
/*   0*/  private void doPercentTypedAccounting(NodeTraversal t, Node n) {
/* 869*/    JSType type = n.getJSType();
/* 870*/    if (type == null) {
/* 871*/      this.nullCount++;
/* 872*/    } else if (type.isUnknownType()) {
/* 873*/      if (this.reportUnknownTypes.isOn())
/* 874*/        this.compiler.report(t.makeError(n, this.reportUnknownTypes, UNKNOWN_EXPR_TYPE, new String[0])); 
/* 877*/      this.unknownCount++;
/*   0*/    } else {
/* 879*/      this.typedCount++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitAssign(NodeTraversal t, Node assign) {
/* 892*/    JSDocInfo info = assign.getJSDocInfo();
/* 893*/    Node lvalue = assign.getFirstChild();
/* 894*/    Node rvalue = assign.getLastChild();
/* 897*/    if (lvalue.isGetProp()) {
/* 898*/      Node object = lvalue.getFirstChild();
/* 899*/      JSType objectJsType = getJSType(object);
/* 900*/      Node property = lvalue.getLastChild();
/* 901*/      String pname = property.getString();
/* 905*/      if (object.isGetProp()) {
/* 906*/        JSType jsType = getJSType(object.getFirstChild());
/* 907*/        if (jsType.isInterface() && object.getLastChild().getString().equals("prototype"))
/* 909*/          visitInterfaceGetprop(t, assign, object, pname, lvalue, rvalue); 
/*   0*/      } 
/* 913*/      checkEnumAlias(t, info, rvalue);
/* 914*/      checkPropCreation(t, lvalue);
/* 920*/      if (pname.equals("prototype") && 
/* 921*/        objectJsType != null && objectJsType.isFunctionType()) {
/* 922*/        FunctionType functionType = objectJsType.toMaybeFunctionType();
/* 923*/        if (functionType.isConstructor()) {
/* 924*/          JSType rvalueType = rvalue.getJSType();
/* 925*/          this.validator.expectObject(t, rvalue, rvalueType, "overriding prototype with non-object");
/* 928*/          if (functionType.makesStructs() && !rvalueType.isStruct()) {
/* 929*/            String funName = functionType.getTypeOfThis().toString();
/* 930*/            this.compiler.report(t.makeError(assign, CONFLICTING_EXTENDED_TYPE, new String[] { "struct", funName }));
/*   0*/          } 
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 941*/      ObjectType type = ObjectType.cast(objectJsType.restrictByNotNullOrUndefined());
/* 943*/      if (type != null && 
/* 944*/        type.hasProperty(pname) && !type.isPropertyTypeInferred(pname) && !propertyIsImplicitCast(type, pname)) {
/* 947*/        JSType expectedType = type.getPropertyType(pname);
/* 948*/        if (!expectedType.isUnknownType()) {
/* 949*/          this.validator.expectCanAssignToPropertyOf(t, assign, getJSType(rvalue), expectedType, object, pname);
/* 952*/          checkPropertyInheritanceOnGetpropAssign(t, assign, object, pname, info, expectedType);
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 961*/      checkPropertyInheritanceOnGetpropAssign(t, assign, object, pname, info, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/    } 
/* 970*/    JSType leftType = getJSType(lvalue);
/* 971*/    if (lvalue.isQualifiedName()) {
/* 973*/      JSType rvalueType = getJSType(assign.getLastChild());
/* 974*/      Scope.Var var = t.getScope().getVar(lvalue.getQualifiedName());
/* 975*/      if (var != null) {
/* 976*/        if (var.isTypeInferred())
/*   0*/          return; 
/* 980*/        if (NodeUtil.getRootOfQualifiedName(lvalue).isThis() && t.getScope() != var.getScope())
/*   0*/          return; 
/* 986*/        if (var.getType() != null)
/* 987*/          leftType = var.getType(); 
/*   0*/      } 
/*   0*/    } 
/* 993*/    Node rightChild = assign.getLastChild();
/* 994*/    JSType rightType = getJSType(rightChild);
/* 995*/    if (this.validator.expectCanAssignTo(t, assign, rightType, leftType, "assignment")) {
/* 997*/      ensureTyped(t, assign, rightType);
/*   0*/    } else {
/* 999*/      ensureTyped(t, assign);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropCreation(NodeTraversal t, Node lvalue) {
/*1005*/    if (lvalue.isGetProp()) {
/*1006*/      Node obj = lvalue.getFirstChild();
/*1007*/      Node prop = lvalue.getLastChild();
/*1008*/      JSType objType = getJSType(obj);
/*1009*/      String pname = prop.getString();
/*1010*/      if (objType.isStruct() && !objType.hasProperty(pname) && (
/*1011*/        !obj.isThis() || !getJSType(t.getScope().getRootNode()).isConstructor()))
/*1013*/        report(t, prop, ILLEGAL_PROPERTY_CREATION, new String[0]); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyInheritanceOnGetpropAssign(NodeTraversal t, Node assign, Node object, String property, JSDocInfo info, JSType propertyType) {
/*1033*/    if (object.isGetProp()) {
/*1034*/      Node object2 = object.getFirstChild();
/*1035*/      String property2 = NodeUtil.getStringValue(object.getLastChild());
/*1037*/      if ("prototype".equals(property2)) {
/*1038*/        JSType jsType = getJSType(object2);
/*1039*/        if (jsType.isFunctionType()) {
/*1040*/          FunctionType functionType = jsType.toMaybeFunctionType();
/*1041*/          if (functionType.isConstructor() || functionType.isInterface())
/*1042*/            checkDeclaredPropertyInheritance(t, assign, functionType, property, info, propertyType); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitObjLitKey(NodeTraversal t, Node key, Node objlit, JSType litType) {
/*1063*/    if (objlit.isFromExterns()) {
/*1064*/      ensureTyped(t, key);
/*   0*/      return;
/*   0*/    } 
/*1069*/    if (litType.isStruct() && key.isQuotedString()) {
/*1070*/      report(t, key, ILLEGAL_OBJLIT_KEY, new String[] { "struct" });
/*1071*/    } else if (litType.isDict() && !key.isQuotedString()) {
/*1072*/      report(t, key, ILLEGAL_OBJLIT_KEY, new String[] { "dict" });
/*   0*/    } 
/*1080*/    Node rvalue = key.getFirstChild();
/*1081*/    JSType rightType = NodeUtil.getObjectLitKeyTypeFromValueType(key, getJSType(rvalue));
/*1083*/    if (rightType == null)
/*1084*/      rightType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1087*/    Node owner = objlit;
/*1091*/    JSType keyType = getJSType(key);
/*1093*/    JSType allowedValueType = keyType;
/*1094*/    if (allowedValueType.isEnumElementType())
/*1095*/      allowedValueType = allowedValueType.toMaybeEnumElementType().getPrimitiveType(); 
/*1099*/    boolean valid = this.validator.expectCanAssignToPropertyOf(t, key, rightType, allowedValueType, owner, NodeUtil.getObjectLitKeyName(key));
/*1102*/    if (valid) {
/*1103*/      ensureTyped(t, key, rightType);
/*   0*/    } else {
/*1105*/      ensureTyped(t, key);
/*   0*/    } 
/*1114*/    JSType objlitType = getJSType(objlit);
/*1115*/    ObjectType type = ObjectType.cast(objlitType.restrictByNotNullOrUndefined());
/*1117*/    if (type != null) {
/*1118*/      String property = NodeUtil.getObjectLitKeyName(key);
/*1119*/      if (type.hasProperty(property) && !type.isPropertyTypeInferred(property) && !propertyIsImplicitCast(type, property))
/*1122*/        this.validator.expectCanAssignToPropertyOf(t, key, keyType, type.getPropertyType(property), owner, property); 
/*   0*/      return;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean propertyIsImplicitCast(ObjectType type, String prop) {
/*1135*/    for (; type != null; type = type.getImplicitPrototype()) {
/*1136*/      JSDocInfo docInfo = type.getOwnPropertyJSDocInfo(prop);
/*1137*/      if (docInfo != null && docInfo.isImplicitCast())
/*1138*/        return true; 
/*   0*/    } 
/*1141*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkDeclaredPropertyInheritance(NodeTraversal t, Node n, FunctionType ctorType, String propertyName, JSDocInfo info, JSType propertyType) {
/*1155*/    if (hasUnknownOrEmptySupertype(ctorType))
/*   0*/      return; 
/*1159*/    FunctionType superClass = ctorType.getSuperClassConstructor();
/*1160*/    boolean superClassHasProperty = (superClass != null && superClass.getInstanceType().hasProperty(propertyName));
/*1162*/    boolean superClassHasDeclaredProperty = (superClass != null && superClass.getInstanceType().isPropertyTypeDeclared(propertyName));
/*   0*/    boolean superInterfaceHasProperty = false;
/*   0*/    boolean superInterfaceHasDeclaredProperty = false;
/*1168*/    if (ctorType.isInterface())
/*1169*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1170*/        superInterfaceHasProperty = (superInterfaceHasProperty || interfaceType.hasProperty(propertyName));
/*1173*/        superInterfaceHasDeclaredProperty = (superInterfaceHasDeclaredProperty || interfaceType.isPropertyTypeDeclared(propertyName));
/*   0*/      }  
/*1178*/    boolean declaredOverride = (info != null && info.isOverride());
/*   0*/    boolean foundInterfaceProperty = false;
/*1181*/    if (ctorType.isConstructor())
/*1183*/      for (JSType implementedInterface : ctorType.getAllImplementedInterfaces()) {
/*1184*/        if (implementedInterface.isUnknownType() || implementedInterface.isEmptyType())
/*   0*/          continue; 
/*1188*/        FunctionType interfaceType = implementedInterface.toObjectType().getConstructor();
/*1190*/        Preconditions.checkNotNull(interfaceType);
/*1192*/        boolean interfaceHasProperty = interfaceType.getPrototype().hasProperty(propertyName);
/*1194*/        foundInterfaceProperty = (foundInterfaceProperty || interfaceHasProperty);
/*1196*/        if (this.reportMissingOverride.isOn() && !declaredOverride && interfaceHasProperty)
/*1201*/          this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_INTERFACE_PROPERTY, new String[] { propertyName, interfaceType.getTopMostDefiningType(propertyName).toString() })); 
/*   0*/      }  
/*1208*/    if (!declaredOverride && !superClassHasProperty && !superInterfaceHasProperty)
/*   0*/      return; 
/*1215*/    ObjectType topInstanceType = superClassHasDeclaredProperty ? superClass.getTopMostDefiningType(propertyName) : null;
/*1217*/    boolean declaredLocally = (ctorType.isConstructor() && (ctorType.getPrototype().hasOwnProperty(propertyName) || ctorType.getInstanceType().hasOwnProperty(propertyName)));
/*1221*/    if (this.reportMissingOverride.isOn() && !declaredOverride && superClassHasDeclaredProperty && declaredLocally)
/*1227*/      this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_SUPERCLASS_PROPERTY, new String[] { propertyName, topInstanceType.toString() })); 
/*1233*/    if (superClassHasDeclaredProperty) {
/*1235*/      JSType superClassPropType = superClass.getInstanceType().getPropertyType(propertyName);
/*1237*/      if (!propertyType.isSubtype(superClassPropType))
/*1238*/        this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superClassPropType.toString(), propertyType.toString() })); 
/*1243*/    } else if (superInterfaceHasDeclaredProperty) {
/*1245*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1246*/        if (interfaceType.hasProperty(propertyName)) {
/*1247*/          JSType superPropertyType = interfaceType.getPropertyType(propertyName);
/*1249*/          if (!propertyType.isSubtype(superPropertyType)) {
/*1250*/            topInstanceType = interfaceType.getConstructor().getTopMostDefiningType(propertyName);
/*1252*/            this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superPropertyType.toString(), propertyType.toString() }));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1260*/    } else if (!foundInterfaceProperty && !superClassHasProperty && !superInterfaceHasProperty) {
/*1264*/      this.compiler.report(t.makeError(n, UNKNOWN_OVERRIDE, new String[] { propertyName, ctorType.getInstanceType().toString() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean hasUnknownOrEmptySupertype(FunctionType ctor) {
/*1275*/    Preconditions.checkArgument((ctor.isConstructor() || ctor.isInterface()));
/*1276*/    Preconditions.checkArgument(!ctor.isUnknownType());
/*   0*/    while (true) {
/*1281*/      ObjectType maybeSuperInstanceType = ctor.getPrototype().getImplicitPrototype();
/*1283*/      if (maybeSuperInstanceType == null)
/*1284*/        return false; 
/*1286*/      if (maybeSuperInstanceType.isUnknownType() || maybeSuperInstanceType.isEmptyType())
/*1288*/        return true; 
/*1290*/      ctor = maybeSuperInstanceType.getConstructor();
/*1291*/      if (ctor == null)
/*1292*/        return false; 
/*1294*/      Preconditions.checkState((ctor.isConstructor() || ctor.isInterface()));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitInterfaceGetprop(NodeTraversal t, Node assign, Node object, String property, Node lvalue, Node rvalue) {
/*1307*/    JSType rvalueType = getJSType(rvalue);
/*1315*/    String abstractMethodName = this.compiler.getCodingConvention().getAbstractMethodName();
/*1317*/    if (!rvalueType.isFunctionType()) {
/*1319*/      String abstractMethodMessage = (abstractMethodName != null) ? (", or " + abstractMethodName) : "";
/*1322*/      this.compiler.report(t.makeError(object, INVALID_INTERFACE_MEMBER_DECLARATION, new String[] { abstractMethodMessage }));
/*   0*/    } 
/*1327*/    if (assign.getLastChild().isFunction() && !NodeUtil.isEmptyBlock(assign.getLastChild().getLastChild()))
/*1329*/      this.compiler.report(t.makeError(object, INTERFACE_FUNCTION_NOT_EMPTY, new String[] { abstractMethodName })); 
/*   0*/  }
/*   0*/  
/*   0*/  boolean visitName(NodeTraversal t, Node n, Node parent) {
/*1352*/    int parentNodeType = parent.getType();
/*1353*/    if (parentNodeType == 105 || parentNodeType == 120 || parentNodeType == 83 || parentNodeType == 118)
/*1357*/      return false; 
/*1360*/    JSType type = n.getJSType();
/*1361*/    if (type == null) {
/*1362*/      type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*1363*/      Scope.Var var = t.getScope().getVar(n.getString());
/*1364*/      if (var != null) {
/*1365*/        JSType varType = var.getType();
/*1366*/        if (varType != null)
/*1367*/          type = varType; 
/*   0*/      } 
/*   0*/    } 
/*1371*/    ensureTyped(t, n, type);
/*1372*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetProp(NodeTraversal t, Node n, Node parent) {
/*1388*/    Node property = n.getLastChild();
/*1389*/    Node objNode = n.getFirstChild();
/*1390*/    JSType childType = getJSType(objNode);
/*1392*/    if (childType.isDict()) {
/*1393*/      report(t, property, TypeValidator.ILLEGAL_PROPERTY_ACCESS, new String[] { "'.'", "dict" });
/*1394*/    } else if (this.validator.expectNotNullOrUndefined(t, n, childType, "No properties on this expression", getNativeType(JSTypeNative.OBJECT_TYPE))) {
/*1396*/      checkPropertyAccess(childType, property.getString(), t, n);
/*   0*/    } 
/*1398*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccess(JSType childType, String propName, NodeTraversal t, Node n) {
/*1414*/    JSType propType = getJSType(n);
/*1415*/    if (propType.isEquivalentTo(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE))) {
/*1416*/      childType = childType.autobox();
/*1417*/      ObjectType objectType = ObjectType.cast(childType);
/*1418*/      if (objectType != null) {
/*1422*/        if (!objectType.hasProperty(propName) || objectType.isEquivalentTo(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE)))
/*1425*/          if (objectType instanceof EnumType) {
/*1426*/            report(t, n, INEXISTENT_ENUM_ELEMENT, new String[] { propName });
/*   0*/          } else {
/*1428*/            checkPropertyAccessHelper(objectType, propName, t, n);
/*   0*/          }  
/*   0*/      } else {
/*1433*/        checkPropertyAccessHelper(childType, propName, t, n);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccessHelper(JSType objectType, String propName, NodeTraversal t, Node n) {
/*1440*/    if (!objectType.isEmptyType() && this.reportMissingProperties && !isPropertyTest(n))
/*1442*/      if (!this.typeRegistry.canPropertyBeDefined(objectType, propName))
/*1443*/        report(t, n, INEXISTENT_PROPERTY, new String[] { propName, this.validator.getReadableJSTypeName(n.getFirstChild(), true) });  
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isPropertyTest(Node getProp) {
/*1456*/    Node parent = getProp.getParent();
/*1457*/    switch (parent.getType()) {
/*   0*/      case 37:
/*1459*/        return (parent.getFirstChild() != getProp && this.compiler.getCodingConvention().isPropertyTestFunction(parent));
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*1466*/        return (NodeUtil.getConditionExpression(parent) == getProp);
/*   0*/      case 32:
/*   0*/      case 52:
/*1470*/        return true;
/*   0*/      case 98:
/*   0*/      case 101:
/*1474*/        return (parent.getFirstChild() == getProp);
/*   0*/      case 26:
/*1477*/        return (parent.getParent().isOr() && parent.getParent().getFirstChild() == parent);
/*   0*/    } 
/*1480*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetElem(NodeTraversal t, Node n) {
/*1491*/    this.validator.expectIndexMatch(t, n, getJSType(n.getFirstChild()), getJSType(n.getLastChild()));
/*1493*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void visitVar(NodeTraversal t, Node n) {
/*1507*/    JSDocInfo varInfo = n.hasOneChild() ? n.getJSDocInfo() : null;
/*1508*/    for (Node name : n.children()) {
/*1509*/      Node value = name.getFirstChild();
/*1511*/      Scope.Var var = t.getScope().getVar(name.getString());
/*1513*/      if (value != null) {
/*1514*/        JSType valueType = getJSType(value);
/*1515*/        JSType nameType = var.getType();
/*1516*/        nameType = (nameType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : nameType;
/*1518*/        JSDocInfo info = name.getJSDocInfo();
/*1519*/        if (info == null)
/*1520*/          info = varInfo; 
/*1523*/        checkEnumAlias(t, info, value);
/*1524*/        if (var.isTypeInferred()) {
/*1525*/          ensureTyped(t, name, valueType);
/*   0*/          continue;
/*   0*/        } 
/*1527*/        this.validator.expectCanAssignTo(t, value, valueType, nameType, "initializing variable");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitNew(NodeTraversal t, Node n) {
/*1538*/    Node constructor = n.getFirstChild();
/*1539*/    JSType type = getJSType(constructor).restrictByNotNullOrUndefined();
/*1540*/    if (type.isConstructor() || type.isEmptyType() || type.isUnknownType()) {
/*1541*/      FunctionType fnType = type.toMaybeFunctionType();
/*1542*/      if (fnType != null) {
/*1543*/        visitParameterList(t, n, fnType);
/*1544*/        ensureTyped(t, n, fnType.getInstanceType());
/*   0*/      } else {
/*1546*/        ensureTyped(t, n);
/*   0*/      } 
/*   0*/    } else {
/*1549*/      report(t, n, NOT_A_CONSTRUCTOR, new String[0]);
/*1550*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkInterfaceConflictProperties(NodeTraversal t, Node n, String functionName, HashMap<String, ObjectType> properties, HashMap<String, ObjectType> currentProperties, ObjectType interfaceType) {
/*1570*/    ObjectType implicitProto = interfaceType.getImplicitPrototype();
/*1574*/    Set<String> currentPropertyNames = implicitProto.getOwnPropertyNames();
/*1575*/    for (String name : currentPropertyNames) {
/*1576*/      ObjectType oType = properties.get(name);
/*1577*/      if (oType != null && 
/*1578*/        !interfaceType.getPropertyType(name).isEquivalentTo(oType.getPropertyType(name)))
/*1580*/        this.compiler.report(t.makeError(n, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, new String[] { functionName, name, oType.toString(), interfaceType.toString() })); 
/*1586*/      currentProperties.put(name, interfaceType);
/*   0*/    } 
/*1588*/    for (ObjectType iType : interfaceType.getCtorExtendedInterfaces())
/*1589*/      checkInterfaceConflictProperties(t, n, functionName, properties, currentProperties, iType); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitFunction(NodeTraversal t, Node n) {
/*1602*/    FunctionType functionType = JSType.toMaybeFunctionType(n.getJSType());
/*1603*/    String functionPrivateName = n.getFirstChild().getString();
/*1604*/    if (functionType.isConstructor()) {
/*1605*/      FunctionType baseConstructor = functionType.getSuperClassConstructor();
/*1606*/      if (baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE) && baseConstructor != null && baseConstructor.isInterface()) {
/*1609*/        this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "constructor", functionPrivateName }));
/*   0*/      } else {
/*1613*/        if (baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE)) {
/*1614*/          ObjectType proto = functionType.getPrototype();
/*1615*/          if (functionType.makesStructs() && !proto.isStruct()) {
/*1616*/            this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "struct", functionPrivateName }));
/*1618*/          } else if (functionType.makesDicts() && !proto.isDict()) {
/*1619*/            this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "dict", functionPrivateName }));
/*   0*/          } 
/*   0*/        } 
/*1624*/        for (JSType baseInterface : functionType.getImplementedInterfaces()) {
/*   0*/          boolean badImplementedType = false;
/*1626*/          ObjectType baseInterfaceObj = ObjectType.cast(baseInterface);
/*1627*/          if (baseInterfaceObj != null) {
/*1628*/            FunctionType interfaceConstructor = baseInterfaceObj.getConstructor();
/*1630*/            if (interfaceConstructor != null && !interfaceConstructor.isInterface())
/*1632*/              badImplementedType = true; 
/*   0*/          } else {
/*1635*/            badImplementedType = true;
/*   0*/          } 
/*1637*/          if (badImplementedType)
/*1638*/            report(t, n, BAD_IMPLEMENTED_TYPE, new String[] { functionPrivateName }); 
/*   0*/        } 
/*1642*/        this.validator.expectAllInterfaceProperties(t, n, functionType);
/*   0*/      } 
/*1644*/    } else if (functionType.isInterface()) {
/*1646*/      for (ObjectType extInterface : functionType.getExtendedInterfaces()) {
/*1647*/        if (extInterface.getConstructor() != null && !extInterface.getConstructor().isInterface())
/*1649*/          this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "interface", functionPrivateName })); 
/*   0*/      } 
/*1656*/      if (functionType.getExtendedInterfacesCount() > 1) {
/*1658*/        HashMap<String, ObjectType> properties = new HashMap<String, ObjectType>();
/*1660*/        HashMap<String, ObjectType> currentProperties = new HashMap<String, ObjectType>();
/*1662*/        for (ObjectType interfaceType : functionType.getExtendedInterfaces()) {
/*1663*/          currentProperties.clear();
/*1664*/          checkInterfaceConflictProperties(t, n, functionPrivateName, properties, currentProperties, interfaceType);
/*1666*/          properties.putAll(currentProperties);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitCall(NodeTraversal t, Node n) {
/*1680*/    Node child = n.getFirstChild();
/*1681*/    JSType childType = getJSType(child).restrictByNotNullOrUndefined();
/*1683*/    if (!childType.canBeCalled()) {
/*1684*/      report(t, n, NOT_CALLABLE, new String[] { childType.toString() });
/*1685*/      ensureTyped(t, n);
/*   0*/      return;
/*   0*/    } 
/*1691*/    if (childType.isFunctionType()) {
/*1692*/      FunctionType functionType = childType.toMaybeFunctionType();
/*   0*/      boolean isExtern = false;
/*1695*/      JSDocInfo functionJSDocInfo = functionType.getJSDocInfo();
/*1696*/      if (functionJSDocInfo != null && functionJSDocInfo.getAssociatedNode() != null)
/*1698*/        isExtern = functionJSDocInfo.getAssociatedNode().isFromExterns(); 
/*1704*/      if (functionType.isConstructor() && !functionType.isNativeObjectType() && (functionType.getReturnType().isUnknownType() || functionType.getReturnType().isVoidType() || !isExtern))
/*1709*/        report(t, n, CONSTRUCTOR_NOT_CALLABLE, new String[] { childType.toString() }); 
/*1714*/      if (functionType.isOrdinaryFunction() && !functionType.getTypeOfThis().isUnknownType() && (functionType.getTypeOfThis().toObjectType() == null || !functionType.getTypeOfThis().toObjectType().isNativeObjectType()) && !child.isGetElem() && !child.isGetProp())
/*1720*/        report(t, n, EXPECTED_THIS_TYPE, new String[] { functionType.toString() }); 
/*1723*/      visitParameterList(t, n, functionType);
/*1724*/      ensureTyped(t, n, functionType.getReturnType());
/*   0*/    } else {
/*1726*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitParameterList(NodeTraversal t, Node call, FunctionType functionType) {
/*1739*/    Iterator<Node> arguments = call.children().iterator();
/*1740*/    arguments.next();
/*1742*/    Iterator<Node> parameters = functionType.getParameters().iterator();
/*1743*/    int ordinal = 0;
/*1744*/    Node parameter = null;
/*1745*/    Node argument = null;
/*1746*/    while (arguments.hasNext() && (parameters.hasNext() || (parameter != null && parameter.isVarArgs()))) {
/*1751*/      if (parameters.hasNext())
/*1752*/        parameter = parameters.next(); 
/*1754*/      argument = arguments.next();
/*1755*/      ordinal++;
/*1757*/      this.validator.expectArgumentMatchesParameter(t, argument, getJSType(argument), getJSType(parameter), call, ordinal);
/*   0*/    } 
/*1761*/    int numArgs = call.getChildCount() - 1;
/*1762*/    int minArgs = functionType.getMinArguments();
/*1763*/    int maxArgs = functionType.getMaxArguments();
/*1764*/    if (minArgs > numArgs || maxArgs < numArgs)
/*1765*/      report(t, call, WRONG_ARGUMENT_COUNT, new String[] { this.validator.getReadableJSTypeName(call.getFirstChild(), false), String.valueOf(numArgs), String.valueOf(minArgs), (maxArgs != Integer.MAX_VALUE) ? (" and no more than " + maxArgs + " argument(s)") : "" }); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitReturn(NodeTraversal t, Node n) {
/*1781*/    JSType jsType = getJSType(t.getEnclosingFunction());
/*1783*/    if (jsType.isFunctionType()) {
/*   0*/      JSType actualReturnType;
/*1784*/      FunctionType functionType = jsType.toMaybeFunctionType();
/*1786*/      JSType returnType = functionType.getReturnType();
/*1790*/      if (returnType == null)
/*1791*/        returnType = getNativeType(JSTypeNative.VOID_TYPE); 
/*1795*/      Node valueNode = n.getFirstChild();
/*1797*/      if (valueNode == null) {
/*1798*/        actualReturnType = getNativeType(JSTypeNative.VOID_TYPE);
/*1799*/        valueNode = n;
/*   0*/      } else {
/*1801*/        actualReturnType = getJSType(valueNode);
/*   0*/      } 
/*1805*/      this.validator.expectCanAssignTo(t, valueNode, actualReturnType, returnType, "inconsistent return type");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitBinaryOperator(int op, NodeTraversal t, Node n) {
/*1821*/    Node left = n.getFirstChild();
/*1822*/    JSType leftType = getJSType(left);
/*1823*/    Node right = n.getLastChild();
/*1824*/    JSType rightType = getJSType(right);
/*1825*/    switch (op) {
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*1832*/        if (!leftType.matchesInt32Context())
/*1833*/          report(t, left, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), leftType.toString() }); 
/*1836*/        if (!rightType.matchesUint32Context())
/*1837*/          report(t, right, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), rightType.toString() }); 
/*   0*/        break;
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/*1850*/        this.validator.expectNumber(t, left, leftType, "left operand");
/*1851*/        this.validator.expectNumber(t, right, rightType, "right operand");
/*   0*/        break;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*1860*/        this.validator.expectBitwiseable(t, left, leftType, "bad left operand to bitwise operator");
/*1862*/        this.validator.expectBitwiseable(t, right, rightType, "bad right operand to bitwise operator");
/*   0*/        break;
/*   0*/      case 21:
/*   0*/      case 93:
/*   0*/        break;
/*   0*/      default:
/*1871*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(op) });
/*   0*/        break;
/*   0*/    } 
/*1873*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkEnumAlias(NodeTraversal t, JSDocInfo declInfo, Node value) {
/*1893*/    if (declInfo == null || !declInfo.hasEnumParameterType())
/*   0*/      return; 
/*1897*/    JSType valueType = getJSType(value);
/*1898*/    if (!valueType.isEnumType())
/*   0*/      return; 
/*1902*/    EnumType valueEnumType = valueType.toMaybeEnumType();
/*1903*/    JSType valueEnumPrimitiveType = valueEnumType.getElementsType().getPrimitiveType();
/*1905*/    this.validator.expectCanAssignTo(t, value, valueEnumPrimitiveType, declInfo.getEnumParameterType().evaluate(t.getScope(), this.typeRegistry), "incompatible enum element types");
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getJSType(Node n) {
/*1915*/    JSType jsType = n.getJSType();
/*1916*/    if (jsType == null)
/*1921*/      return getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1923*/    return jsType;
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n) {
/*1938*/    ensureTyped(t, n, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSTypeNative type) {
/*1942*/    ensureTyped(t, n, getNativeType(type));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSType type) {
/*1965*/    Preconditions.checkState((!n.isFunction() || type.isFunctionType() || type.isUnknownType()));
/*1968*/    JSDocInfo info = n.getJSDocInfo();
/*1969*/    if (info != null) {
/*1970*/      if (info.hasType()) {
/*1975*/        JSType infoType = info.getType().evaluate(t.getScope(), this.typeRegistry);
/*1976*/        this.validator.expectCanCast(t, n, infoType, type);
/*1977*/        type = infoType;
/*   0*/      } 
/*1980*/      if (info.isImplicitCast() && !this.inExterns) {
/*1981*/        String propName = n.isGetProp() ? n.getLastChild().getString() : "(missing)";
/*1983*/        this.compiler.report(t.makeError(n, ILLEGAL_IMPLICIT_CAST, new String[] { propName }));
/*   0*/      } 
/*   0*/    } 
/*1988*/    if (n.getJSType() == null)
/*1989*/      n.setJSType(type); 
/*   0*/  }
/*   0*/  
/*   0*/  double getTypedPercent() {
/*1998*/    int total = this.nullCount + this.unknownCount + this.typedCount;
/*1999*/    return (total == 0) ? 0.0D : (100.0D * this.typedCount / total);
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative typeId) {
/*2003*/    return this.typeRegistry.getNativeType(typeId);
/*   0*/  }
/*   0*/}
