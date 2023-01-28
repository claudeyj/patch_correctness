/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
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
/*  67*/  static final DiagnosticType BAD_DELETE = DiagnosticType.warning("JSC_BAD_DELETE_OPERAND", "delete operator needs a reference operand");
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
/* 156*/  static final DiagnosticType CONFLICTING_EXTENDED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_EXTENDED_TYPE", "{0} cannot extend this type; a constructor can only extend objects and an interface can only extend interfaces");
/*   0*/  
/* 163*/  static final DiagnosticType CONFLICTING_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_IMPLEMENTED_TYPE", "{0} cannot implement this type; an interface can only extend, but not implement interfaces");
/*   0*/  
/* 169*/  static final DiagnosticType BAD_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_IMPLEMENTS_NON_INTERFACE", "can only implement interfaces");
/*   0*/  
/* 174*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY", "property {0} already defined on superclass {1}; use @override to override it");
/*   0*/  
/* 180*/  static final DiagnosticType HIDDEN_INTERFACE_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_INTERFACE_PROPERTY", "property {0} already defined on interface {1}; use @override to override it");
/*   0*/  
/* 186*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY_MISMATCH = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY_MISMATCH", "mismatch of the {0} property type and the type of the property it overrides from superclass {1}\noriginal: {2}\noverride: {3}");
/*   0*/  
/* 193*/  static final DiagnosticType UNKNOWN_OVERRIDE = DiagnosticType.warning("JSC_UNKNOWN_OVERRIDE", "property {0} not defined on any superclass of {1}");
/*   0*/  
/* 198*/  static final DiagnosticType INTERFACE_METHOD_OVERRIDE = DiagnosticType.warning("JSC_INTERFACE_METHOD_OVERRIDE", "property {0} is already defined by the {1} extended interface");
/*   0*/  
/* 203*/  static final DiagnosticType UNKNOWN_EXPR_TYPE = DiagnosticType.warning("JSC_UNKNOWN_EXPR_TYPE", "could not determine the type of this expression");
/*   0*/  
/* 207*/  static final DiagnosticType UNRESOLVED_TYPE = DiagnosticType.warning("JSC_UNRESOLVED_TYPE", "could not resolve the name {0} to a type");
/*   0*/  
/* 211*/  static final DiagnosticType WRONG_ARGUMENT_COUNT = DiagnosticType.warning("JSC_WRONG_ARGUMENT_COUNT", "Function {0}: called with {1} argument(s). Function requires at least {2} argument(s){3}.");
/*   0*/  
/* 217*/  static final DiagnosticType ILLEGAL_IMPLICIT_CAST = DiagnosticType.warning("JSC_ILLEGAL_IMPLICIT_CAST", "Illegal annotation on {0}. @implicitCast may only be used in externs.");
/*   0*/  
/* 223*/  static final DiagnosticType INCOMPATIBLE_EXTENDED_PROPERTY_TYPE = DiagnosticType.warning("JSC_INCOMPATIBLE_EXTENDED_PROPERTY_TYPE", "Interface {0} has a property {1} with incompatible types in its super interfaces {2} and {3}");
/*   0*/  
/* 229*/  static final DiagnosticType EXPECTED_THIS_TYPE = DiagnosticType.warning("JSC_EXPECTED_THIS_TYPE", "\"{0}\" must be called with a \"this\" type");
/*   0*/  
/* 234*/  static final DiagnosticGroup ALL_DIAGNOSTICS = new DiagnosticGroup(new DiagnosticType[] { 
/* 234*/        DETERMINISTIC_TEST, DETERMINISTIC_TEST_NO_RESULT, INEXISTENT_ENUM_ELEMENT, INEXISTENT_PROPERTY, NOT_A_CONSTRUCTOR, BIT_OPERATION, NOT_CALLABLE, CONSTRUCTOR_NOT_CALLABLE, FUNCTION_MASKS_VARIABLE, MULTIPLE_VAR_DEF, 
/* 234*/        ENUM_DUP, ENUM_NOT_CONSTANT, INVALID_INTERFACE_MEMBER_DECLARATION, INTERFACE_FUNCTION_NOT_EMPTY, CONFLICTING_EXTENDED_TYPE, CONFLICTING_IMPLEMENTED_TYPE, BAD_IMPLEMENTED_TYPE, HIDDEN_SUPERCLASS_PROPERTY, HIDDEN_INTERFACE_PROPERTY, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, 
/* 234*/        UNKNOWN_OVERRIDE, INTERFACE_METHOD_OVERRIDE, UNKNOWN_EXPR_TYPE, UNRESOLVED_TYPE, WRONG_ARGUMENT_COUNT, ILLEGAL_IMPLICIT_CAST, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, EXPECTED_THIS_TYPE, RhinoErrorReporter.TYPE_PARSE_ERROR, TypedScopeCreator.UNKNOWN_LENDS, 
/* 234*/        TypedScopeCreator.LENDS_ON_NON_OBJECT, TypedScopeCreator.CTOR_INITIALIZER, TypedScopeCreator.IFACE_INITIALIZER, FunctionTypeBuilder.THIS_TYPE_NON_OBJECT });
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
/* 287*/  private InferJSDocInfo inferJSDocInfo = null;
/*   0*/  
/* 290*/  private int typedCount = 0;
/*   0*/  
/* 291*/  private int nullCount = 0;
/*   0*/  
/* 292*/  private int unknownCount = 0;
/*   0*/  
/*   0*/  private boolean inExterns;
/*   0*/  
/* 297*/  private int noTypeCheckSection = 0;
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, Scope topScope, ScopeCreator scopeCreator, CheckLevel reportMissingOverride, CheckLevel reportUnknownTypes) {
/* 306*/    this.compiler = compiler;
/* 307*/    this.validator = compiler.getTypeValidator();
/* 308*/    this.reverseInterpreter = reverseInterpreter;
/* 309*/    this.typeRegistry = typeRegistry;
/* 310*/    this.topScope = topScope;
/* 311*/    this.scopeCreator = scopeCreator;
/* 312*/    this.reportMissingOverride = reportMissingOverride;
/* 313*/    this.reportUnknownTypes = reportUnknownTypes;
/* 314*/    this.inferJSDocInfo = new InferJSDocInfo(compiler);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, CheckLevel reportMissingOverride, CheckLevel reportUnknownTypes) {
/* 322*/    this(compiler, reverseInterpreter, typeRegistry, null, null, reportMissingOverride, reportUnknownTypes);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry) {
/* 329*/    this(compiler, reverseInterpreter, typeRegistry, null, null, CheckLevel.WARNING, CheckLevel.OFF);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck reportMissingProperties(boolean report) {
/* 335*/    this.reportMissingProperties = report;
/* 336*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externsRoot, Node jsRoot) {
/* 347*/    Preconditions.checkNotNull(this.scopeCreator);
/* 348*/    Preconditions.checkNotNull(this.topScope);
/* 350*/    Node externsAndJs = jsRoot.getParent();
/* 351*/    Preconditions.checkState((externsAndJs != null));
/* 352*/    Preconditions.checkState((externsRoot == null || externsAndJs.hasChild(externsRoot)));
/* 355*/    if (externsRoot != null)
/* 356*/      check(externsRoot, true); 
/* 358*/    check(jsRoot, false);
/*   0*/  }
/*   0*/  
/*   0*/  public Scope processForTesting(Node externsRoot, Node jsRoot) {
/* 363*/    Preconditions.checkState((this.scopeCreator == null));
/* 364*/    Preconditions.checkState((this.topScope == null));
/* 366*/    Preconditions.checkState((jsRoot.getParent() != null));
/* 367*/    Node externsAndJsRoot = jsRoot.getParent();
/* 369*/    this.scopeCreator = new MemoizedScopeCreator(new TypedScopeCreator(this.compiler));
/* 370*/    this.topScope = this.scopeCreator.createScope(externsAndJsRoot, null);
/* 372*/    TypeInferencePass inference = new TypeInferencePass(this.compiler, this.reverseInterpreter, this.topScope, this.scopeCreator);
/* 375*/    inference.process(externsRoot, jsRoot);
/* 376*/    process(externsRoot, jsRoot);
/* 378*/    return this.topScope;
/*   0*/  }
/*   0*/  
/*   0*/  public void check(Node node, boolean externs) {
/* 383*/    Preconditions.checkNotNull(node);
/* 385*/    NodeTraversal t = new NodeTraversal(this.compiler, this, this.scopeCreator);
/* 386*/    this.inExterns = externs;
/* 387*/    t.traverseWithScope(node, this.topScope);
/* 388*/    if (externs) {
/* 389*/      this.inferJSDocInfo.process(node, null);
/*   0*/    } else {
/* 391*/      this.inferJSDocInfo.process(null, node);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNoTypeCheckSection(Node n, boolean enterSection) {
/*   0*/    JSDocInfo info;
/* 397*/    switch (n.getType()) {
/*   0*/      case 86:
/*   0*/      case 105:
/*   0*/      case 118:
/*   0*/      case 125:
/*   0*/      case 132:
/* 403*/        info = n.getJSDocInfo();
/* 404*/        if (info != null && info.isNoTypeCheck())
/* 405*/          if (enterSection) {
/* 406*/            this.noTypeCheckSection++;
/*   0*/          } else {
/* 408*/            this.noTypeCheckSection--;
/*   0*/          }  
/* 411*/        this.validator.setShouldReport((this.noTypeCheckSection == 0));
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void report(NodeTraversal t, Node n, DiagnosticType diagnosticType, String... arguments) {
/* 418*/    if (this.noTypeCheckSection == 0)
/* 419*/      t.report(n, diagnosticType, arguments); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/*   0*/    TypeCheck outerThis;
/*   0*/    Scope outerScope;
/*   0*/    FunctionType functionType;
/*   0*/    String functionPrivateName;
/* 425*/    checkNoTypeCheckSection(n, true);
/* 426*/    switch (n.getType()) {
/*   0*/      case 105:
/* 429*/        outerThis = this;
/* 430*/        outerScope = t.getScope();
/* 431*/        functionType = (FunctionType)n.getJSType();
/* 432*/        functionPrivateName = n.getFirstChild().getString();
/* 433*/        if (functionPrivateName != null && functionPrivateName.length() > 0 && outerScope.isDeclared(functionPrivateName, false) && !(outerScope.getVar(functionPrivateName).getType() instanceof FunctionType))
/* 440*/          report(t, n, FUNCTION_MASKS_VARIABLE, new String[] { functionPrivateName }); 
/*   0*/        break;
/*   0*/    } 
/* 448*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/    JSType childType, leftType, rightType;
/*   0*/    Node left, right;
/*   0*/    JSType leftTypeRestricted, switchType, rightTypeRestricted, caseType;
/*   0*/    TernaryValue result;
/*   0*/    Node child;
/*   0*/    boolean typeable = true;
/* 468*/    switch (n.getType()) {
/*   0*/      case 38:
/* 470*/        typeable = visitName(t, n, parent);
/*   0*/        break;
/*   0*/      case 83:
/* 476*/        if (parent.getType() != 105) {
/* 477*/          ensureTyped(t, n, getJSType(n.getFirstChild()));
/*   0*/          break;
/*   0*/        } 
/* 479*/        typeable = false;
/*   0*/        break;
/*   0*/      case 85:
/* 484*/        ensureTyped(t, n, getJSType(n.getLastChild()));
/*   0*/        break;
/*   0*/      case 43:
/*   0*/      case 44:
/* 489*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 42:
/* 493*/        ensureTyped(t, n, t.getScope().getTypeOfThis());
/*   0*/        break;
/*   0*/      case 69:
/* 497*/        ensureTyped(t, n);
/*   0*/        break;
/*   0*/      case 65:
/* 501*/        ensureTyped(t, n, getJSType(n.getFirstChild()));
/*   0*/        break;
/*   0*/      case 41:
/* 505*/        ensureTyped(t, n, JSTypeNative.NULL_TYPE);
/*   0*/        break;
/*   0*/      case 39:
/* 509*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 40:
/* 514*/        if (!NodeUtil.isObjectLitKey(n, n.getParent()))
/* 515*/          ensureTyped(t, n, JSTypeNative.STRING_TYPE); 
/*   0*/        break;
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/        break;
/*   0*/      case 63:
/* 526*/        ensureTyped(t, n, JSTypeNative.ARRAY_TYPE);
/*   0*/        break;
/*   0*/      case 47:
/* 530*/        ensureTyped(t, n, JSTypeNative.REGEXP_TYPE);
/*   0*/        break;
/*   0*/      case 33:
/* 534*/        visitGetProp(t, n, parent);
/* 535*/        typeable = (parent.getType() != 86 || parent.getFirstChild() != n);
/*   0*/        break;
/*   0*/      case 35:
/* 540*/        visitGetElem(t, n);
/* 544*/        typeable = false;
/*   0*/        break;
/*   0*/      case 118:
/* 548*/        visitVar(t, n);
/* 549*/        typeable = false;
/*   0*/        break;
/*   0*/      case 30:
/* 553*/        visitNew(t, n);
/* 554*/        typeable = true;
/*   0*/        break;
/*   0*/      case 37:
/* 558*/        visitCall(t, n);
/* 559*/        typeable = !NodeUtil.isExpressionNode(parent);
/*   0*/        break;
/*   0*/      case 4:
/* 563*/        visitReturn(t, n);
/* 564*/        typeable = false;
/*   0*/        break;
/*   0*/      case 102:
/*   0*/      case 103:
/* 569*/        left = n.getFirstChild();
/* 570*/        this.validator.expectNumber(t, left, getJSType(left), "increment/decrement");
/* 572*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 26:
/* 576*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 122:
/* 580*/        ensureTyped(t, n, JSTypeNative.VOID_TYPE);
/*   0*/        break;
/*   0*/      case 32:
/* 584*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 27:
/* 588*/        childType = getJSType(n.getFirstChild());
/* 589*/        if (!childType.matchesInt32Context())
/* 590*/          report(t, n, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), childType.toString() }); 
/* 593*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 28:
/*   0*/      case 29:
/* 598*/        left = n.getFirstChild();
/* 599*/        this.validator.expectNumber(t, left, getJSType(left), "sign operator");
/* 600*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 12:
/*   0*/      case 13:
/* 605*/        leftType = getJSType(n.getFirstChild());
/* 606*/        rightType = getJSType(n.getLastChild());
/* 608*/        leftTypeRestricted = leftType.restrictByNotNullOrUndefined();
/* 609*/        rightTypeRestricted = rightType.restrictByNotNullOrUndefined();
/* 610*/        result = leftTypeRestricted.testForEquality(rightTypeRestricted);
/* 612*/        if (result != TernaryValue.UNKNOWN) {
/* 613*/          if (n.getType() == 13)
/* 614*/            result = result.not(); 
/* 616*/          report(t, n, DETERMINISTIC_TEST, new String[] { leftType.toString(), rightType.toString(), result.toString() });
/*   0*/        } 
/* 619*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 45:
/*   0*/      case 46:
/* 625*/        leftType = getJSType(n.getFirstChild());
/* 626*/        rightType = getJSType(n.getLastChild());
/* 628*/        leftTypeRestricted = leftType.restrictByNotNullOrUndefined();
/* 629*/        rightTypeRestricted = rightType.restrictByNotNullOrUndefined();
/* 630*/        if (!leftTypeRestricted.canTestForShallowEqualityWith(rightTypeRestricted))
/* 632*/          report(t, n, DETERMINISTIC_TEST_NO_RESULT, new String[] { leftType.toString(), rightType.toString() }); 
/* 635*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/* 643*/        leftType = getJSType(n.getFirstChild());
/* 644*/        rightType = getJSType(n.getLastChild());
/* 645*/        if (rightType.isNumber()) {
/* 646*/          this.validator.expectNumber(t, n, leftType, "left side of numeric comparison");
/* 648*/        } else if (leftType.isNumber()) {
/* 649*/          this.validator.expectNumber(t, n, rightType, "right side of numeric comparison");
/* 651*/        } else if (!leftType.matchesNumberContext() || !rightType.matchesNumberContext()) {
/* 658*/          String message = "left side of comparison";
/* 659*/          this.validator.expectString(t, n, leftType, message);
/* 660*/          this.validator.expectNotNullOrUndefined(t, n, leftType, message, getNativeType(JSTypeNative.STRING_TYPE));
/* 662*/          message = "right side of comparison";
/* 663*/          this.validator.expectString(t, n, rightType, message);
/* 664*/          this.validator.expectNotNullOrUndefined(t, n, rightType, message, getNativeType(JSTypeNative.STRING_TYPE));
/*   0*/        } 
/* 667*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 51:
/* 671*/        left = n.getFirstChild();
/* 672*/        right = n.getLastChild();
/* 673*/        leftType = getJSType(left);
/* 674*/        rightType = getJSType(right);
/* 675*/        this.validator.expectObject(t, n, rightType, "'in' requires an object");
/* 676*/        this.validator.expectString(t, left, leftType, "left side of 'in'");
/* 677*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 52:
/* 681*/        left = n.getFirstChild();
/* 682*/        right = n.getLastChild();
/* 683*/        leftType = getJSType(left);
/* 684*/        rightType = getJSType(right).restrictByNotNullOrUndefined();
/* 686*/        this.validator.expectAnyObject(t, left, leftType, "deterministic instanceof yields false");
/* 688*/        this.validator.expectActualObject(t, right, rightType, "instanceof requires an object");
/* 690*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 86:
/* 694*/        visitAssign(t, n);
/* 695*/        typeable = false;
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
/* 720*/        visitBinaryOperator(n.getType(), t, n);
/*   0*/        break;
/*   0*/      case 31:
/* 724*/        if (!isReference(n.getFirstChild()))
/* 725*/          report(t, n, BAD_DELETE, new String[0]); 
/* 727*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 111:
/* 731*/        switchType = getJSType(parent.getFirstChild());
/* 732*/        caseType = getJSType(n.getFirstChild());
/* 733*/        this.validator.expectSwitchMatchesCase(t, n, switchType, caseType);
/* 734*/        typeable = false;
/*   0*/        break;
/*   0*/      case 119:
/* 738*/        child = n.getFirstChild();
/* 739*/        childType = getJSType(child);
/* 740*/        this.validator.expectObject(t, child, childType, "with requires an object");
/* 742*/        typeable = false;
/*   0*/        break;
/*   0*/      case 105:
/* 747*/        visitFunction(t, n);
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
/* 765*/        typeable = false;
/*   0*/        break;
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/* 773*/        typeable = false;
/*   0*/        break;
/*   0*/      case 64:
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/* 781*/        if (n.getJSType() != null) {
/* 782*/          ensureTyped(t, n);
/* 785*/        } else if (n.getType() == 64 && parent.getJSType() instanceof EnumType) {
/* 787*/          ensureTyped(t, n, parent.getJSType());
/*   0*/        } else {
/* 789*/          ensureTyped(t, n);
/*   0*/        } 
/* 792*/        if (n.getType() == 64)
/* 793*/          for (Node key : n.children())
/* 794*/            visitObjLitKey(t, key, n);  
/*   0*/        break;
/*   0*/      default:
/* 800*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(n.getType()) });
/* 801*/        ensureTyped(t, n);
/*   0*/        break;
/*   0*/    } 
/* 806*/    typeable = (typeable && !this.inExterns);
/* 808*/    if (typeable)
/* 809*/      doPercentTypedAccounting(t, n); 
/* 812*/    checkNoTypeCheckSection(n, false);
/*   0*/  }
/*   0*/  
/*   0*/  private void doPercentTypedAccounting(NodeTraversal t, Node n) {
/* 820*/    JSType type = n.getJSType();
/* 821*/    if (type == null) {
/* 822*/      this.nullCount += 0;
/* 823*/    } else if (type.isUnknownType()) {
/* 824*/      if (this.reportUnknownTypes.isOn())
/* 825*/        this.compiler.report(t.makeError(n, this.reportUnknownTypes, UNKNOWN_EXPR_TYPE, new String[0])); 
/* 828*/      this.unknownCount++;
/*   0*/    } else {
/* 830*/      this.typedCount++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitAssign(NodeTraversal t, Node assign) {
/* 843*/    JSDocInfo info = assign.getJSDocInfo();
/* 844*/    Node lvalue = assign.getFirstChild();
/* 845*/    Node rvalue = assign.getLastChild();
/* 847*/    if (lvalue.getType() == 33) {
/* 848*/      Node object = lvalue.getFirstChild();
/* 849*/      JSType objectJsType = getJSType(object);
/* 850*/      String property = lvalue.getLastChild().getString();
/* 854*/      if (object.getType() == 33) {
/* 855*/        JSType jsType = getJSType(object.getFirstChild());
/* 856*/        if (jsType.isInterface() && object.getLastChild().getString().equals("prototype"))
/* 858*/          visitInterfaceGetprop(t, assign, object, property, lvalue, rvalue); 
/*   0*/      } 
/* 863*/      if (info != null && info.hasType()) {
/* 864*/        visitAnnotatedAssignGetprop(t, assign, info.getType().evaluate(t.getScope(), this.typeRegistry), object, property, rvalue);
/*   0*/        return;
/*   0*/      } 
/* 871*/      if (info != null && info.hasEnumParameterType()) {
/* 872*/        checkEnumInitializer(t, rvalue, info.getEnumParameterType().evaluate(t.getScope(), this.typeRegistry));
/*   0*/        return;
/*   0*/      } 
/* 879*/      if (property.equals("prototype")) {
/* 880*/        if (objectJsType instanceof FunctionType) {
/* 881*/          FunctionType functionType = (FunctionType)objectJsType;
/* 882*/          if (functionType.isConstructor()) {
/* 883*/            JSType rvalueType = rvalue.getJSType();
/* 884*/            this.validator.expectObject(t, rvalue, rvalueType, "overriding prototype with non-object");
/*   0*/          } 
/*   0*/        } 
/*   0*/        return;
/*   0*/      } 
/* 894*/      if (object.getType() == 33) {
/* 895*/        Node object2 = object.getFirstChild();
/* 896*/        String property2 = NodeUtil.getStringValue(object.getLastChild());
/* 898*/        if ("prototype".equals(property2)) {
/* 899*/          JSType jsType = object2.getJSType();
/* 900*/          if (jsType instanceof FunctionType) {
/* 901*/            FunctionType functionType = (FunctionType)jsType;
/* 902*/            if (functionType.isConstructor() || functionType.isInterface())
/* 903*/              checkDeclaredPropertyInheritance(t, assign, functionType, property, info, getJSType(rvalue)); 
/*   0*/          } 
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 914*/      ObjectType type = ObjectType.cast(objectJsType.restrictByNotNullOrUndefined());
/* 916*/      if (type != null) {
/* 917*/        if (type.hasProperty(property) && !type.isPropertyTypeInferred(property) && !propertyIsImplicitCast(type, property))
/* 920*/          this.validator.expectCanAssignToPropertyOf(t, assign, getJSType(rvalue), type.getPropertyType(property), object, property); 
/*   0*/        return;
/*   0*/      } 
/* 926*/    } else if (lvalue.getType() == 38) {
/* 928*/      JSType rvalueType = getJSType(assign.getLastChild());
/* 929*/      Scope.Var var = t.getScope().getVar(lvalue.getString());
/* 930*/      if (var != null && 
/* 931*/        var.isTypeInferred())
/*   0*/        return; 
/*   0*/    } 
/* 938*/    JSType leftType = getJSType(lvalue);
/* 939*/    Node rightChild = assign.getLastChild();
/* 940*/    JSType rightType = getJSType(rightChild);
/* 941*/    if (this.validator.expectCanAssignTo(t, assign, rightType, leftType, "assignment")) {
/* 943*/      ensureTyped(t, assign, rightType);
/*   0*/    } else {
/* 945*/      ensureTyped(t, assign);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitObjLitKey(NodeTraversal t, Node key, Node objlit) {
/* 964*/    Node rvalue = key.getFirstChild();
/* 965*/    JSType rightType = NodeUtil.getObjectLitKeyTypeFromValueType(key, getJSType(rvalue));
/* 967*/    if (rightType == null)
/* 968*/      rightType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/* 971*/    Node owner = objlit;
/* 975*/    JSType keyType = getJSType(key);
/* 976*/    boolean valid = this.validator.expectCanAssignToPropertyOf(t, key, rightType, keyType, owner, NodeUtil.getObjectLitKeyName(key));
/* 979*/    if (valid) {
/* 980*/      ensureTyped(t, key, rightType);
/*   0*/    } else {
/* 982*/      ensureTyped(t, key);
/*   0*/    } 
/* 991*/    JSType objlitType = getJSType(objlit);
/* 992*/    ObjectType type = ObjectType.cast(objlitType.restrictByNotNullOrUndefined());
/* 994*/    if (type != null) {
/* 995*/      String property = NodeUtil.getObjectLitKeyName(key);
/* 996*/      if (type.hasProperty(property) && !type.isPropertyTypeInferred(property) && !propertyIsImplicitCast(type, property))
/* 999*/        this.validator.expectCanAssignToPropertyOf(t, key, keyType, type.getPropertyType(property), owner, property); 
/*   0*/      return;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean propertyIsImplicitCast(ObjectType type, String prop) {
/*1012*/    for (; type != null; type = type.getImplicitPrototype()) {
/*1013*/      JSDocInfo docInfo = type.getOwnPropertyJSDocInfo(prop);
/*1014*/      if (docInfo != null && docInfo.isImplicitCast())
/*1015*/        return true; 
/*   0*/    } 
/*1018*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkDeclaredPropertyInheritance(NodeTraversal t, Node n, FunctionType ctorType, String propertyName, JSDocInfo info, JSType propertyType) {
/*1032*/    if (hasUnknownOrEmptySupertype(ctorType))
/*   0*/      return; 
/*1036*/    FunctionType superClass = ctorType.getSuperClassConstructor();
/*1037*/    boolean superClassHasProperty = (superClass != null && superClass.getPrototype().hasProperty(propertyName));
/*   0*/    boolean superInterfacesHasProperty = false;
/*1041*/    if (ctorType.isInterface())
/*1042*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces())
/*1043*/        superInterfacesHasProperty = (superInterfacesHasProperty || interfaceType.hasProperty(propertyName));  
/*1047*/    boolean declaredOverride = (info != null && info.isOverride());
/*   0*/    boolean foundInterfaceProperty = false;
/*1050*/    if (ctorType.isConstructor())
/*1052*/      for (JSType implementedInterface : ctorType.getAllImplementedInterfaces()) {
/*1053*/        if (implementedInterface.isUnknownType() || implementedInterface.isEmptyType())
/*   0*/          continue; 
/*1057*/        FunctionType interfaceType = implementedInterface.toObjectType().getConstructor();
/*1059*/        Preconditions.checkNotNull(interfaceType);
/*1060*/        boolean interfaceHasProperty = interfaceType.getPrototype().hasProperty(propertyName);
/*1062*/        foundInterfaceProperty = (foundInterfaceProperty || interfaceHasProperty);
/*1063*/        if (this.reportMissingOverride.isOn() && !declaredOverride && interfaceHasProperty)
/*1067*/          this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_INTERFACE_PROPERTY, new String[] { propertyName, interfaceType.getTopMostDefiningType(propertyName).toString() })); 
/*   0*/      }  
/*1074*/    if (!declaredOverride && !superClassHasProperty && !superInterfacesHasProperty)
/*   0*/      return; 
/*1080*/    JSType topInstanceType = superClassHasProperty ? superClass.getTopMostDefiningType(propertyName) : null;
/*1082*/    if (this.reportMissingOverride.isOn() && ctorType.isConstructor() && !declaredOverride && superClassHasProperty)
/*1086*/      this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_SUPERCLASS_PROPERTY, new String[] { propertyName, topInstanceType.toString() })); 
/*1090*/    if (!declaredOverride)
/*   0*/      return; 
/*1095*/    if (superClassHasProperty) {
/*1097*/      JSType superClassPropType = superClass.getPrototype().getPropertyType(propertyName);
/*1099*/      if (!propertyType.canAssignTo(superClassPropType))
/*1100*/        this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superClassPropType.toString(), propertyType.toString() })); 
/*1105*/    } else if (superInterfacesHasProperty) {
/*1107*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1108*/        if (interfaceType.hasProperty(propertyName)) {
/*1109*/          JSType superPropertyType = interfaceType.getPropertyType(propertyName);
/*1111*/          if (!propertyType.canAssignTo(superPropertyType)) {
/*1112*/            topInstanceType = interfaceType.getConstructor().getTopMostDefiningType(propertyName);
/*1114*/            this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superPropertyType.toString(), propertyType.toString() }));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1122*/    } else if (!foundInterfaceProperty) {
/*1124*/      this.compiler.report(t.makeError(n, UNKNOWN_OVERRIDE, new String[] { propertyName, ctorType.getInstanceType().toString() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean hasUnknownOrEmptySupertype(FunctionType ctor) {
/*1135*/    Preconditions.checkArgument((ctor.isConstructor() || ctor.isInterface()));
/*1136*/    Preconditions.checkArgument(!ctor.isUnknownType());
/*   0*/    while (true) {
/*1141*/      ObjectType maybeSuperInstanceType = ctor.getPrototype().getImplicitPrototype();
/*1143*/      if (maybeSuperInstanceType == null)
/*1144*/        return false; 
/*1146*/      if (maybeSuperInstanceType.isUnknownType() || maybeSuperInstanceType.isEmptyType())
/*1148*/        return true; 
/*1150*/      ctor = maybeSuperInstanceType.getConstructor();
/*1151*/      if (ctor == null)
/*1152*/        return false; 
/*1154*/      Preconditions.checkState((ctor.isConstructor() || ctor.isInterface()));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitInterfaceGetprop(NodeTraversal t, Node assign, Node object, String property, Node lvalue, Node rvalue) {
/*1167*/    JSType rvalueType = getJSType(rvalue);
/*1175*/    String abstractMethodName = this.compiler.getCodingConvention().getAbstractMethodName();
/*1177*/    if (!rvalueType.isOrdinaryFunction() && (!rvalue.isQualifiedName() || !rvalue.getQualifiedName().equals(abstractMethodName))) {
/*1181*/      String abstractMethodMessage = (abstractMethodName != null) ? (", or " + abstractMethodName) : "";
/*1184*/      this.compiler.report(t.makeError(object, INVALID_INTERFACE_MEMBER_DECLARATION, new String[] { abstractMethodMessage }));
/*   0*/    } 
/*1189*/    if (assign.getLastChild().getType() == 105 && !NodeUtil.isEmptyBlock(assign.getLastChild().getLastChild()))
/*1191*/      this.compiler.report(t.makeError(object, INTERFACE_FUNCTION_NOT_EMPTY, new String[] { abstractMethodName })); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitAnnotatedAssignGetprop(NodeTraversal t, Node assign, JSType type, Node object, String property, Node rvalue) {
/*1207*/    this.validator.expectCanAssignToPropertyOf(t, assign, getJSType(rvalue), type, object, property);
/*   0*/  }
/*   0*/  
/*   0*/  boolean visitName(NodeTraversal t, Node n, Node parent) {
/*1228*/    int parentNodeType = parent.getType();
/*1229*/    if (parentNodeType == 105 || parentNodeType == 120 || parentNodeType == 83 || parentNodeType == 118)
/*1233*/      return false; 
/*1236*/    JSType type = n.getJSType();
/*1237*/    if (type == null) {
/*1238*/      type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*1239*/      Scope.Var var = t.getScope().getVar(n.getString());
/*1240*/      if (var != null) {
/*1241*/        JSType varType = var.getType();
/*1242*/        if (varType != null)
/*1243*/          type = varType; 
/*   0*/      } 
/*   0*/    } 
/*1247*/    ensureTyped(t, n, type);
/*1248*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetProp(NodeTraversal t, Node n, Node parent) {
/*1263*/    if (n.getJSType() != null && parent.getType() == 86)
/*   0*/      return; 
/*1271*/    Node property = n.getLastChild();
/*1272*/    Node objNode = n.getFirstChild();
/*1273*/    JSType childType = getJSType(objNode);
/*1277*/    if (!this.validator.expectNotNullOrUndefined(t, n, childType, childType + " has no properties", getNativeType(JSTypeNative.OBJECT_TYPE))) {
/*1279*/      ensureTyped(t, n);
/*   0*/      return;
/*   0*/    } 
/*1283*/    checkPropertyAccess(childType, property.getString(), t, n);
/*1284*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccess(JSType childType, String propName, NodeTraversal t, Node n) {
/*1292*/    ObjectType objectType = childType.dereference();
/*1293*/    if (objectType != null) {
/*1294*/      JSType propType = getJSType(n);
/*1295*/      if ((!objectType.hasProperty(propName) || objectType.equals(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE))) && propType.equals(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE)))
/*1298*/        if (objectType instanceof EnumType) {
/*1299*/          report(t, n, INEXISTENT_ENUM_ELEMENT, new String[] { propName });
/*1300*/        } else if (!objectType.isEmptyType() && this.reportMissingProperties && !isPropertyTest(n)) {
/*1302*/          if (!this.typeRegistry.canPropertyBeDefined(objectType, propName))
/*1303*/            report(t, n, INEXISTENT_PROPERTY, new String[] { propName, this.validator.getReadableJSTypeName(n.getFirstChild(), true) }); 
/*   0*/        }  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isPropertyTest(Node getProp) {
/*1321*/    Node parent = getProp.getParent();
/*1322*/    switch (parent.getType()) {
/*   0*/      case 37:
/*1324*/        return (parent.getFirstChild() != getProp && this.compiler.getCodingConvention().isPropertyTestFunction(parent));
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*1331*/        return (NodeUtil.getConditionExpression(parent) == getProp);
/*   0*/      case 32:
/*   0*/      case 52:
/*1335*/        return true;
/*   0*/      case 98:
/*   0*/      case 101:
/*1339*/        return (parent.getFirstChild() == getProp);
/*   0*/      case 26:
/*1342*/        return (parent.getParent().getType() == 100 && parent.getParent().getFirstChild() == parent);
/*   0*/    } 
/*1345*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetElem(NodeTraversal t, Node n) {
/*1356*/    Node left = n.getFirstChild();
/*1357*/    Node right = n.getLastChild();
/*1358*/    this.validator.expectIndexMatch(t, n, getJSType(left), getJSType(right));
/*1359*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void visitVar(NodeTraversal t, Node n) {
/*1373*/    JSDocInfo varInfo = n.hasOneChild() ? n.getJSDocInfo() : null;
/*1374*/    for (Node name : n.children()) {
/*1375*/      Node value = name.getFirstChild();
/*1377*/      Scope.Var var = t.getScope().getVar(name.getString());
/*1379*/      if (value != null) {
/*1380*/        JSType valueType = getJSType(value);
/*1381*/        JSType nameType = var.getType();
/*1382*/        nameType = (nameType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : nameType;
/*1384*/        JSDocInfo info = name.getJSDocInfo();
/*1385*/        if (info == null)
/*1386*/          info = varInfo; 
/*1388*/        if (info != null && info.hasEnumParameterType()) {
/*1391*/          checkEnumInitializer(t, value, info.getEnumParameterType().evaluate(t.getScope(), this.typeRegistry));
/*   0*/          continue;
/*   0*/        } 
/*1394*/        if (var.isTypeInferred()) {
/*1395*/          ensureTyped(t, name, valueType);
/*   0*/          continue;
/*   0*/        } 
/*1397*/        this.validator.expectCanAssignTo(t, value, valueType, nameType, "initializing variable");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitNew(NodeTraversal t, Node n) {
/*1408*/    Node constructor = n.getFirstChild();
/*1409*/    FunctionType type = getFunctionType(constructor);
/*1410*/    if (type != null && type.isConstructor()) {
/*1411*/      visitParameterList(t, n, type);
/*1412*/      ensureTyped(t, n, type.getInstanceType());
/*   0*/    } else {
/*1415*/      if (constructor.getType() != 33) {
/*   0*/        Node line;
/*1420*/        if (constructor.getLineno() < 0 || constructor.getCharno() < 0) {
/*1421*/          line = n;
/*   0*/        } else {
/*1423*/          line = constructor;
/*   0*/        } 
/*1425*/        report(t, line, NOT_A_CONSTRUCTOR, new String[0]);
/*   0*/      } 
/*1427*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkInterfaceConflictProperties(NodeTraversal t, Node n, String functionName, HashMap<String, ObjectType> properties, HashMap<String, ObjectType> currentProperties, ObjectType interfaceType) {
/*1447*/    Set<String> currentPropertyNames = interfaceType.getPropertyNames();
/*1448*/    for (String name : currentPropertyNames) {
/*1449*/      ObjectType oType = properties.get(name);
/*1450*/      if (oType != null && 
/*1451*/        !interfaceType.getPropertyType(name).isEquivalentTo(oType.getPropertyType(name)))
/*1453*/        this.compiler.report(t.makeError(n, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, new String[] { functionName, name, oType.toString(), interfaceType.toString() })); 
/*1459*/      currentProperties.put(name, interfaceType);
/*   0*/    } 
/*1461*/    for (ObjectType iType : interfaceType.getCtorExtendedInterfaces())
/*1462*/      checkInterfaceConflictProperties(t, n, functionName, properties, currentProperties, iType); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitFunction(NodeTraversal t, Node n) {
/*1475*/    FunctionType functionType = (FunctionType)n.getJSType();
/*1476*/    String functionPrivateName = n.getFirstChild().getString();
/*1477*/    if (functionType.isConstructor()) {
/*1478*/      FunctionType baseConstructor = functionType.getPrototype().getImplicitPrototype().getConstructor();
/*1480*/      if (baseConstructor != null && baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE) && baseConstructor.isInterface() && functionType.isConstructor()) {
/*1483*/        this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { functionPrivateName }));
/*   0*/      } else {
/*1487*/        for (JSType baseInterface : functionType.getImplementedInterfaces()) {
/*   0*/          boolean badImplementedType = false;
/*1489*/          ObjectType baseInterfaceObj = ObjectType.cast(baseInterface);
/*1490*/          if (baseInterfaceObj != null) {
/*1491*/            FunctionType interfaceConstructor = baseInterfaceObj.getConstructor();
/*1493*/            if (interfaceConstructor != null && !interfaceConstructor.isInterface())
/*1495*/              badImplementedType = true; 
/*   0*/          } else {
/*1498*/            badImplementedType = true;
/*   0*/          } 
/*1500*/          if (badImplementedType)
/*1501*/            report(t, n, BAD_IMPLEMENTED_TYPE, new String[] { functionPrivateName }); 
/*   0*/        } 
/*1505*/        this.validator.expectAllInterfaceProperties(t, n, functionType);
/*   0*/      } 
/*1507*/    } else if (functionType.isInterface()) {
/*1509*/      for (ObjectType extInterface : functionType.getExtendedInterfaces()) {
/*1510*/        if (extInterface.getConstructor() != null && !extInterface.getConstructor().isInterface())
/*1512*/          this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { functionPrivateName })); 
/*   0*/      } 
/*1517*/      if (functionType.hasImplementedInterfaces())
/*1518*/        this.compiler.report(t.makeError(n, CONFLICTING_IMPLEMENTED_TYPE, new String[] { functionPrivateName })); 
/*1522*/      if (functionType.getExtendedInterfacesCount() > 1) {
/*1524*/        HashMap<String, ObjectType> properties = new HashMap<String, ObjectType>();
/*1526*/        HashMap<String, ObjectType> currentProperties = new HashMap<String, ObjectType>();
/*1528*/        for (ObjectType interfaceType : functionType.getExtendedInterfaces()) {
/*1529*/          currentProperties.clear();
/*1530*/          checkInterfaceConflictProperties(t, n, functionPrivateName, properties, currentProperties, interfaceType);
/*1532*/          properties.putAll(currentProperties);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitCall(NodeTraversal t, Node n) {
/*1546*/    Node child = n.getFirstChild();
/*1547*/    JSType childType = getJSType(child).restrictByNotNullOrUndefined();
/*1549*/    if (!childType.canBeCalled()) {
/*1550*/      report(t, n, NOT_CALLABLE, new String[] { childType.toString() });
/*1551*/      ensureTyped(t, n);
/*   0*/      return;
/*   0*/    } 
/*1557*/    if (childType instanceof FunctionType) {
/*1558*/      FunctionType functionType = (FunctionType)childType;
/*   0*/      boolean isExtern = false;
/*1561*/      JSDocInfo functionJSDocInfo = functionType.getJSDocInfo();
/*1562*/      if (functionJSDocInfo != null) {
/*1563*/        String sourceName = functionJSDocInfo.getSourceName();
/*1564*/        CompilerInput functionSource = this.compiler.getInput(sourceName);
/*1565*/        isExtern = functionSource.isExtern();
/*   0*/      } 
/*1571*/      if (functionType.isConstructor() && !functionType.isNativeObjectType() && (functionType.getReturnType().isUnknownType() || functionType.getReturnType().isVoidType() || !isExtern))
/*1576*/        report(t, n, CONSTRUCTOR_NOT_CALLABLE, new String[] { childType.toString() }); 
/*1581*/      if (functionType.isOrdinaryFunction() && !functionType.getTypeOfThis().isUnknownType() && !functionType.getTypeOfThis().isNativeObjectType() && child.getType() != 35 && child.getType() != 33)
/*1586*/        report(t, n, EXPECTED_THIS_TYPE, new String[] { functionType.toString() }); 
/*1589*/      visitParameterList(t, n, functionType);
/*1590*/      ensureTyped(t, n, functionType.getReturnType());
/*   0*/    } else {
/*1592*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitParameterList(NodeTraversal t, Node call, FunctionType functionType) {
/*1605*/    Iterator<Node> arguments = call.children().iterator();
/*1606*/    arguments.next();
/*1608*/    Iterator<Node> parameters = functionType.getParameters().iterator();
/*1609*/    int ordinal = 0;
/*1610*/    Node parameter = null;
/*1611*/    Node argument = null;
/*1612*/    while (arguments.hasNext() && (parameters.hasNext() || (parameter != null && parameter.isVarArgs()))) {
/*1617*/      if (parameters.hasNext())
/*1618*/        parameter = parameters.next(); 
/*1620*/      argument = arguments.next();
/*1621*/      ordinal++;
/*1623*/      this.validator.expectArgumentMatchesParameter(t, argument, getJSType(argument), getJSType(parameter), call, ordinal);
/*   0*/    } 
/*1627*/    int numArgs = call.getChildCount() - 1;
/*1628*/    int minArgs = functionType.getMinArguments();
/*1629*/    int maxArgs = functionType.getMaxArguments();
/*1630*/    if (minArgs > numArgs || maxArgs < numArgs)
/*1631*/      report(t, call, WRONG_ARGUMENT_COUNT, new String[] { this.validator.getReadableJSTypeName(call.getFirstChild(), false), String.valueOf(numArgs), String.valueOf(minArgs), (maxArgs != Integer.MAX_VALUE) ? (" and no more than " + maxArgs + " argument(s)") : "" }); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitReturn(NodeTraversal t, Node n) {
/*1647*/    Node function = t.getEnclosingFunction();
/*1651*/    if (function == null)
/*   0*/      return; 
/*1654*/    JSType jsType = getJSType(function);
/*1656*/    if (jsType instanceof FunctionType) {
/*   0*/      JSType actualReturnType;
/*1657*/      FunctionType functionType = (FunctionType)jsType;
/*1659*/      JSType returnType = functionType.getReturnType();
/*1663*/      if (returnType == null)
/*1664*/        returnType = getNativeType(JSTypeNative.VOID_TYPE); 
/*1668*/      Node valueNode = n.getFirstChild();
/*1670*/      if (valueNode == null) {
/*1671*/        actualReturnType = getNativeType(JSTypeNative.VOID_TYPE);
/*1672*/        valueNode = n;
/*   0*/      } else {
/*1674*/        actualReturnType = getJSType(valueNode);
/*   0*/      } 
/*1678*/      this.validator.expectCanAssignTo(t, valueNode, actualReturnType, returnType, "inconsistent return type");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitBinaryOperator(int op, NodeTraversal t, Node n) {
/*1694*/    Node left = n.getFirstChild();
/*1695*/    JSType leftType = getJSType(left);
/*1696*/    Node right = n.getLastChild();
/*1697*/    JSType rightType = getJSType(right);
/*1698*/    switch (op) {
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*1705*/        if (!leftType.matchesInt32Context())
/*1706*/          report(t, left, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), leftType.toString() }); 
/*1709*/        if (!rightType.matchesUint32Context())
/*1710*/          report(t, right, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), rightType.toString() }); 
/*   0*/        break;
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/*1723*/        this.validator.expectNumber(t, left, leftType, "left operand");
/*1724*/        this.validator.expectNumber(t, right, rightType, "right operand");
/*   0*/        break;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*1733*/        this.validator.expectBitwiseable(t, left, leftType, "bad left operand to bitwise operator");
/*1735*/        this.validator.expectBitwiseable(t, right, rightType, "bad right operand to bitwise operator");
/*   0*/        break;
/*   0*/      case 21:
/*   0*/      case 93:
/*   0*/        break;
/*   0*/      default:
/*1744*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Node.tokenToName(op) });
/*   0*/        break;
/*   0*/    } 
/*1746*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkEnumInitializer(NodeTraversal t, Node value, JSType primitiveType) {
/*1767*/    if (value.getType() == 64) {
/*1768*/      Node key = value.getFirstChild();
/*1769*/      for (; key != null; key = key.getNext()) {
/*1770*/        Node propValue = key.getFirstChild();
/*1773*/        this.validator.expectCanAssignTo(t, propValue, getJSType(propValue), primitiveType, "element type must match enum's type");
/*   0*/      } 
/*1777*/    } else if (value.getJSType() instanceof EnumType) {
/*1783*/      EnumType valueEnumType = (EnumType)value.getJSType();
/*1784*/      JSType valueEnumPrimitiveType = valueEnumType.getElementsType().getPrimitiveType();
/*1786*/      this.validator.expectCanAssignTo(t, value, valueEnumPrimitiveType, primitiveType, "incompatible enum element types");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isReference(Node n) {
/*1802*/    switch (n.getType()) {
/*   0*/      case 33:
/*   0*/      case 35:
/*   0*/      case 38:
/*1806*/        return true;
/*   0*/    } 
/*1809*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getJSType(Node n) {
/*1819*/    JSType jsType = n.getJSType();
/*1820*/    if (jsType == null)
/*1825*/      return getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1827*/    return jsType;
/*   0*/  }
/*   0*/  
/*   0*/  private FunctionType getFunctionType(Node n) {
/*1836*/    JSType type = getJSType(n).restrictByNotNullOrUndefined();
/*1837*/    if (type.isUnknownType())
/*1838*/      return this.typeRegistry.getNativeFunctionType(JSTypeNative.U2U_CONSTRUCTOR_TYPE); 
/*1839*/    if (type instanceof FunctionType)
/*1840*/      return (FunctionType)type; 
/*1842*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n) {
/*1857*/    ensureTyped(t, n, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSTypeNative type) {
/*1861*/    ensureTyped(t, n, getNativeType(type));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSType type) {
/*1884*/    Preconditions.checkState((n.getType() != 105 || type instanceof FunctionType || type.isUnknownType()));
/*1887*/    JSDocInfo info = n.getJSDocInfo();
/*1888*/    if (info != null) {
/*1889*/      if (info.hasType()) {
/*1890*/        JSType infoType = info.getType().evaluate(t.getScope(), this.typeRegistry);
/*1891*/        this.validator.expectCanCast(t, n, infoType, type);
/*1892*/        type = infoType;
/*   0*/      } 
/*1895*/      if (info.isImplicitCast() && !this.inExterns) {
/*1896*/        String propName = (n.getType() == 33) ? n.getLastChild().getString() : "(missing)";
/*1898*/        this.compiler.report(t.makeError(n, ILLEGAL_IMPLICIT_CAST, new String[] { propName }));
/*   0*/      } 
/*   0*/    } 
/*1903*/    if (n.getJSType() == null)
/*1904*/      n.setJSType(type); 
/*   0*/  }
/*   0*/  
/*   0*/  double getTypedPercent() {
/*1913*/    int total = this.nullCount + this.unknownCount + this.typedCount;
/*1914*/    if (total == 0)
/*1915*/      return 0.0D; 
/*1917*/    return 100.0D * this.typedCount / total;
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative typeId) {
/*1922*/    return this.typeRegistry.getNativeType(typeId);
/*   0*/  }
/*   0*/}
