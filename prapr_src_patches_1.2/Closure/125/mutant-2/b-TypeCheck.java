/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.ImmutableSet;
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
/*   0*/import com.google.javascript.rhino.jstype.TemplateTypeMap;
/*   0*/import com.google.javascript.rhino.jstype.TemplateTypeMapReplacer;
/*   0*/import com.google.javascript.rhino.jstype.TernaryValue;
/*   0*/import com.google.javascript.rhino.jstype.UnionType;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class TypeCheck implements NodeTraversal.Callback, CompilerPass {
/*  63*/  static final DiagnosticType UNEXPECTED_TOKEN = DiagnosticType.error("JSC_INTERNAL_ERROR_UNEXPECTED_TOKEN", "Internal Error: Don't know how to handle {0}");
/*   0*/  
/*   0*/  protected static final String OVERRIDING_PROTOTYPE_WITH_NON_OBJECT = "overriding prototype with non-object";
/*   0*/  
/*  77*/  static final DiagnosticType DETERMINISTIC_TEST = DiagnosticType.warning("JSC_DETERMINISTIC_TEST", "condition always evaluates to {2}\nleft : {0}\nright: {1}");
/*   0*/  
/*  84*/  static final DiagnosticType INEXISTENT_ENUM_ELEMENT = DiagnosticType.warning("JSC_INEXISTENT_ENUM_ELEMENT", "element {0} does not exist on this enum");
/*   0*/  
/*  91*/  static final DiagnosticType INEXISTENT_PROPERTY = DiagnosticType.disabled("JSC_INEXISTENT_PROPERTY", "Property {0} never defined on {1}");
/*   0*/  
/*  96*/  static final DiagnosticType INEXISTENT_PROPERTY_WITH_SUGGESTION = DiagnosticType.disabled("JSC_INEXISTENT_PROPERTY", "Property {0} never defined on {1}. Did you mean {2}?");
/*   0*/  
/* 101*/  protected static final DiagnosticType NOT_A_CONSTRUCTOR = DiagnosticType.warning("JSC_NOT_A_CONSTRUCTOR", "cannot instantiate non-constructor");
/*   0*/  
/* 106*/  static final DiagnosticType BIT_OPERATION = DiagnosticType.warning("JSC_BAD_TYPE_FOR_BIT_OPERATION", "operator {0} cannot be applied to {1}");
/*   0*/  
/* 111*/  static final DiagnosticType NOT_CALLABLE = DiagnosticType.warning("JSC_NOT_FUNCTION_TYPE", "{0} expressions are not callable");
/*   0*/  
/* 116*/  static final DiagnosticType CONSTRUCTOR_NOT_CALLABLE = DiagnosticType.warning("JSC_CONSTRUCTOR_NOT_CALLABLE", "Constructor {0} should be called with the \"new\" keyword");
/*   0*/  
/* 121*/  static final DiagnosticType FUNCTION_MASKS_VARIABLE = DiagnosticType.warning("JSC_FUNCTION_MASKS_VARIABLE", "function {0} masks variable (IE bug)");
/*   0*/  
/* 126*/  static final DiagnosticType MULTIPLE_VAR_DEF = DiagnosticType.warning("JSC_MULTIPLE_VAR_DEF", "declaration of multiple variables with shared type information");
/*   0*/  
/* 130*/  static final DiagnosticType ENUM_DUP = DiagnosticType.error("JSC_ENUM_DUP", "enum element {0} already defined");
/*   0*/  
/* 133*/  static final DiagnosticType ENUM_NOT_CONSTANT = DiagnosticType.warning("JSC_ENUM_NOT_CONSTANT", "enum key {0} must be a syntactic constant");
/*   0*/  
/* 137*/  static final DiagnosticType INVALID_INTERFACE_MEMBER_DECLARATION = DiagnosticType.warning("JSC_INVALID_INTERFACE_MEMBER_DECLARATION", "interface members can only be empty property declarations, empty functions{0}");
/*   0*/  
/* 143*/  static final DiagnosticType INTERFACE_FUNCTION_NOT_EMPTY = DiagnosticType.warning("JSC_INTERFACE_FUNCTION_NOT_EMPTY", "interface member functions must have an empty body");
/*   0*/  
/* 148*/  static final DiagnosticType CONFLICTING_SHAPE_TYPE = DiagnosticType.warning("JSC_CONFLICTING_SHAPE_TYPE", "{1} cannot extend this type; {0}s can only extend {0}s");
/*   0*/  
/* 153*/  static final DiagnosticType CONFLICTING_EXTENDED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_EXTENDED_TYPE", "{1} cannot extend this type; {0}s can only extend {0}s");
/*   0*/  
/* 158*/  static final DiagnosticType CONFLICTING_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_CONFLICTING_IMPLEMENTED_TYPE", "{0} cannot implement this type; an interface can only extend, but not implement interfaces");
/*   0*/  
/* 164*/  static final DiagnosticType BAD_IMPLEMENTED_TYPE = DiagnosticType.warning("JSC_IMPLEMENTS_NON_INTERFACE", "can only implement interfaces");
/*   0*/  
/* 169*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY", "property {0} already defined on superclass {1}; use @override to override it");
/*   0*/  
/* 175*/  static final DiagnosticType HIDDEN_INTERFACE_PROPERTY = DiagnosticType.warning("JSC_HIDDEN_INTERFACE_PROPERTY", "property {0} already defined on interface {1}; use @override to override it");
/*   0*/  
/* 181*/  static final DiagnosticType HIDDEN_SUPERCLASS_PROPERTY_MISMATCH = DiagnosticType.warning("JSC_HIDDEN_SUPERCLASS_PROPERTY_MISMATCH", "mismatch of the {0} property type and the type of the property it overrides from superclass {1}\noriginal: {2}\noverride: {3}");
/*   0*/  
/* 188*/  static final DiagnosticType UNKNOWN_OVERRIDE = DiagnosticType.warning("JSC_UNKNOWN_OVERRIDE", "property {0} not defined on any superclass of {1}");
/*   0*/  
/* 193*/  static final DiagnosticType INTERFACE_METHOD_OVERRIDE = DiagnosticType.warning("JSC_INTERFACE_METHOD_OVERRIDE", "property {0} is already defined by the {1} extended interface");
/*   0*/  
/* 198*/  static final DiagnosticType UNKNOWN_EXPR_TYPE = DiagnosticType.disabled("JSC_UNKNOWN_EXPR_TYPE", "could not determine the type of this expression");
/*   0*/  
/* 202*/  static final DiagnosticType UNRESOLVED_TYPE = DiagnosticType.warning("JSC_UNRESOLVED_TYPE", "could not resolve the name {0} to a type");
/*   0*/  
/* 206*/  static final DiagnosticType WRONG_ARGUMENT_COUNT = DiagnosticType.warning("JSC_WRONG_ARGUMENT_COUNT", "Function {0}: called with {1} argument(s). Function requires at least {2} argument(s){3}.");
/*   0*/  
/* 212*/  static final DiagnosticType ILLEGAL_IMPLICIT_CAST = DiagnosticType.warning("JSC_ILLEGAL_IMPLICIT_CAST", "Illegal annotation on {0}. @implicitCast may only be used in externs.");
/*   0*/  
/* 218*/  static final DiagnosticType INCOMPATIBLE_EXTENDED_PROPERTY_TYPE = DiagnosticType.warning("JSC_INCOMPATIBLE_EXTENDED_PROPERTY_TYPE", "Interface {0} has a property {1} with incompatible types in its super interfaces {2} and {3}");
/*   0*/  
/* 224*/  static final DiagnosticType EXPECTED_THIS_TYPE = DiagnosticType.warning("JSC_EXPECTED_THIS_TYPE", "\"{0}\" must be called with a \"this\" type");
/*   0*/  
/* 229*/  static final DiagnosticType IN_USED_WITH_STRUCT = DiagnosticType.warning("JSC_IN_USED_WITH_STRUCT", "Cannot use the IN operator with structs");
/*   0*/  
/* 233*/  static final DiagnosticType ILLEGAL_PROPERTY_CREATION = DiagnosticType.warning("JSC_ILLEGAL_PROPERTY_CREATION", "Cannot add a property to a struct instance after it is constructed.");
/*   0*/  
/* 238*/  static final DiagnosticType ILLEGAL_OBJLIT_KEY = DiagnosticType.warning("ILLEGAL_OBJLIT_KEY", "Illegal key, the object literal is a {0}");
/*   0*/  
/* 246*/  static final DiagnosticGroup ALL_DIAGNOSTICS = new DiagnosticGroup(new DiagnosticType[] { 
/* 246*/        DETERMINISTIC_TEST, INEXISTENT_ENUM_ELEMENT, INEXISTENT_PROPERTY, NOT_A_CONSTRUCTOR, BIT_OPERATION, NOT_CALLABLE, CONSTRUCTOR_NOT_CALLABLE, FUNCTION_MASKS_VARIABLE, MULTIPLE_VAR_DEF, ENUM_DUP, 
/* 246*/        ENUM_NOT_CONSTANT, INVALID_INTERFACE_MEMBER_DECLARATION, INTERFACE_FUNCTION_NOT_EMPTY, CONFLICTING_SHAPE_TYPE, CONFLICTING_EXTENDED_TYPE, CONFLICTING_IMPLEMENTED_TYPE, BAD_IMPLEMENTED_TYPE, HIDDEN_SUPERCLASS_PROPERTY, HIDDEN_INTERFACE_PROPERTY, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, 
/* 246*/        UNKNOWN_OVERRIDE, INTERFACE_METHOD_OVERRIDE, UNRESOLVED_TYPE, WRONG_ARGUMENT_COUNT, ILLEGAL_IMPLICIT_CAST, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, EXPECTED_THIS_TYPE, IN_USED_WITH_STRUCT, ILLEGAL_PROPERTY_CREATION, ILLEGAL_OBJLIT_KEY, 
/* 246*/        RhinoErrorReporter.TYPE_PARSE_ERROR, TypedScopeCreator.UNKNOWN_LENDS, TypedScopeCreator.LENDS_ON_NON_OBJECT, TypedScopeCreator.CTOR_INITIALIZER, TypedScopeCreator.IFACE_INITIALIZER, FunctionTypeBuilder.THIS_TYPE_NON_OBJECT });
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
/*   0*/  private final boolean reportUnknownTypes;
/*   0*/  
/*   0*/  private boolean reportMissingProperties = true;
/*   0*/  
/* 301*/  private InferJSDocInfo inferJSDocInfo = null;
/*   0*/  
/* 304*/  private int typedCount = 0;
/*   0*/  
/* 305*/  private int nullCount = 0;
/*   0*/  
/* 306*/  private int unknownCount = 0;
/*   0*/  
/*   0*/  private boolean inExterns;
/*   0*/  
/* 311*/  private int noTypeCheckSection = 0;
/*   0*/  
/*   0*/  private Method editDistance;
/*   0*/  
/*   0*/  private static final class SuggestionPair {
/*   0*/    private final String suggestion;
/*   0*/    
/*   0*/    final int distance;
/*   0*/    
/*   0*/    private SuggestionPair(String suggestion, int distance) {
/* 319*/      this.suggestion = suggestion;
/* 320*/      this.distance = distance;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, Scope topScope, MemoizedScopeCreator scopeCreator, CheckLevel reportMissingOverride) {
/* 330*/    this.compiler = compiler;
/* 331*/    this.validator = compiler.getTypeValidator();
/* 332*/    this.reverseInterpreter = reverseInterpreter;
/* 333*/    this.typeRegistry = typeRegistry;
/* 334*/    this.topScope = topScope;
/* 335*/    this.scopeCreator = scopeCreator;
/* 336*/    this.reportMissingOverride = reportMissingOverride;
/* 337*/    this.reportUnknownTypes = ((Compiler)compiler).getOptions().enables(DiagnosticGroups.REPORT_UNKNOWN_TYPES);
/* 339*/    this.inferJSDocInfo = new InferJSDocInfo(compiler);
/* 341*/    ClassLoader classLoader = TypeCheck.class.getClassLoader();
/*   0*/    try {
/* 343*/      Class<?> c = classLoader.loadClass("com.google.common.string.EditDistance");
/* 345*/      this.editDistance = c.getDeclaredMethod("getEditDistance", new Class<?>[] { String.class, String.class, boolean.class });
/* 347*/    } catch (Exception ignored) {
/* 348*/      this.editDistance = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry, CheckLevel reportMissingOverride) {
/* 356*/    this(compiler, reverseInterpreter, typeRegistry, null, null, reportMissingOverride);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck(AbstractCompiler compiler, ReverseAbstractInterpreter reverseInterpreter, JSTypeRegistry typeRegistry) {
/* 363*/    this(compiler, reverseInterpreter, typeRegistry, null, null, CheckLevel.WARNING);
/*   0*/  }
/*   0*/  
/*   0*/  TypeCheck reportMissingProperties(boolean report) {
/* 369*/    this.reportMissingProperties = report;
/* 370*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externsRoot, Node jsRoot) {
/* 382*/    Preconditions.checkNotNull(this.scopeCreator);
/* 383*/    Preconditions.checkNotNull(this.topScope);
/* 385*/    Node externsAndJs = jsRoot.getParent();
/* 386*/    Preconditions.checkState((externsAndJs != null));
/* 387*/    Preconditions.checkState((externsRoot == null || externsAndJs.hasChild(externsRoot)));
/* 390*/    if (externsRoot != null)
/* 391*/      check(externsRoot, true); 
/* 393*/    check(jsRoot, false);
/*   0*/  }
/*   0*/  
/*   0*/  public Scope processForTesting(Node externsRoot, Node jsRoot) {
/* 398*/    Preconditions.checkState((this.scopeCreator == null));
/* 399*/    Preconditions.checkState((this.topScope == null));
/* 401*/    Preconditions.checkState((jsRoot.getParent() != null));
/* 402*/    Node externsAndJsRoot = jsRoot.getParent();
/* 404*/    this.scopeCreator = new MemoizedScopeCreator(new TypedScopeCreator(this.compiler));
/* 405*/    this.topScope = this.scopeCreator.createScope(externsAndJsRoot, null);
/* 407*/    TypeInferencePass inference = new TypeInferencePass(this.compiler, this.reverseInterpreter, this.topScope, this.scopeCreator);
/* 410*/    inference.process(externsRoot, jsRoot);
/* 411*/    process(externsRoot, jsRoot);
/* 413*/    return this.topScope;
/*   0*/  }
/*   0*/  
/*   0*/  public void check(Node node, boolean externs) {
/* 418*/    Preconditions.checkNotNull(node);
/* 420*/    NodeTraversal t = new NodeTraversal(this.compiler, this, this.scopeCreator);
/* 421*/    this.inExterns = externs;
/* 422*/    t.traverseWithScope(node, this.topScope);
/* 423*/    if (externs) {
/* 424*/      this.inferJSDocInfo.process(node, null);
/*   0*/    } else {
/* 426*/      this.inferJSDocInfo.process(null, node);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNoTypeCheckSection(Node n, boolean enterSection) {
/*   0*/    JSDocInfo info;
/* 432*/    switch (n.getType()) {
/*   0*/      case 86:
/*   0*/      case 105:
/*   0*/      case 118:
/*   0*/      case 125:
/*   0*/      case 132:
/* 438*/        info = n.getJSDocInfo();
/* 439*/        if (info != null && info.isNoTypeCheck())
/* 440*/          if (enterSection) {
/* 441*/            this.noTypeCheckSection++;
/*   0*/          } else {
/* 443*/            this.noTypeCheckSection--;
/*   0*/          }  
/* 446*/        this.validator.setShouldReport((this.noTypeCheckSection == 0));
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void report(NodeTraversal t, Node n, DiagnosticType diagnosticType, String... arguments) {
/* 453*/    if (this.noTypeCheckSection == 0)
/* 454*/      t.report(n, diagnosticType, arguments); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
/*   0*/    Scope outerScope;
/*   0*/    String functionPrivateName;
/* 461*/    checkNoTypeCheckSection(n, true);
/* 462*/    switch (n.getType()) {
/*   0*/      case 105:
/* 465*/        outerScope = t.getScope();
/* 466*/        functionPrivateName = n.getFirstChild().getString();
/* 467*/        if (functionPrivateName != null && functionPrivateName.length() > 0 && outerScope.isDeclared(functionPrivateName, false) && !(outerScope.getVar(functionPrivateName).getType() instanceof FunctionType))
/* 474*/          report(t, n, FUNCTION_MASKS_VARIABLE, new String[] { functionPrivateName }); 
/*   0*/        break;
/*   0*/    } 
/* 482*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void visit(NodeTraversal t, Node n, Node parent) {
/*   0*/    JSType childType, leftType, rightType;
/*   0*/    Node left, right, expr;
/*   0*/    JSType exprType, castType, leftTypeRestricted, switchType, rightTypeRestricted, caseType;
/*   0*/    TernaryValue result;
/*   0*/    Node child;
/*   0*/    boolean typeable = true;
/* 503*/    switch (n.getType()) {
/*   0*/      case 155:
/* 505*/        expr = n.getFirstChild();
/* 506*/        exprType = getJSType(expr);
/* 507*/        castType = getJSType(n);
/* 511*/        if (!expr.isObjectLit())
/* 512*/          this.validator.expectCanCast(t, n, castType, exprType); 
/* 514*/        ensureTyped(t, n, castType);
/* 516*/        if (castType.isSubtype(exprType) || expr.isObjectLit())
/* 517*/          expr.setJSType(castType); 
/*   0*/        break;
/*   0*/      case 38:
/* 522*/        typeable = visitName(t, n, parent);
/*   0*/        break;
/*   0*/      case 83:
/* 526*/        typeable = false;
/*   0*/        break;
/*   0*/      case 85:
/* 530*/        ensureTyped(t, n, getJSType(n.getLastChild()));
/*   0*/        break;
/*   0*/      case 43:
/*   0*/      case 44:
/* 535*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 42:
/* 539*/        ensureTyped(t, n, t.getScope().getTypeOfThis());
/*   0*/        break;
/*   0*/      case 41:
/* 543*/        ensureTyped(t, n, JSTypeNative.NULL_TYPE);
/*   0*/        break;
/*   0*/      case 39:
/* 547*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 40:
/* 551*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 154:
/* 555*/        typeable = false;
/*   0*/        break;
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/        break;
/*   0*/      case 63:
/* 564*/        ensureTyped(t, n, JSTypeNative.ARRAY_TYPE);
/*   0*/        break;
/*   0*/      case 47:
/* 568*/        ensureTyped(t, n, JSTypeNative.REGEXP_TYPE);
/*   0*/        break;
/*   0*/      case 33:
/* 572*/        visitGetProp(t, n, parent);
/* 573*/        typeable = (!parent.isAssign() || parent.getFirstChild() != n);
/*   0*/        break;
/*   0*/      case 35:
/* 578*/        visitGetElem(t, n);
/* 582*/        typeable = false;
/*   0*/        break;
/*   0*/      case 118:
/* 586*/        visitVar(t, n);
/* 587*/        typeable = false;
/*   0*/        break;
/*   0*/      case 30:
/* 591*/        visitNew(t, n);
/*   0*/        break;
/*   0*/      case 37:
/* 595*/        visitCall(t, n);
/* 596*/        typeable = !parent.isExprResult();
/*   0*/        break;
/*   0*/      case 4:
/* 600*/        visitReturn(t, n);
/* 601*/        typeable = false;
/*   0*/        break;
/*   0*/      case 102:
/*   0*/      case 103:
/* 606*/        left = n.getFirstChild();
/* 607*/        checkPropCreation(t, left);
/* 608*/        this.validator.expectNumber(t, left, getJSType(left), "increment/decrement");
/* 609*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 26:
/* 613*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 122:
/* 617*/        ensureTyped(t, n, JSTypeNative.VOID_TYPE);
/*   0*/        break;
/*   0*/      case 32:
/* 621*/        ensureTyped(t, n, JSTypeNative.STRING_TYPE);
/*   0*/        break;
/*   0*/      case 27:
/* 625*/        childType = getJSType(n.getFirstChild());
/* 626*/        if (!childType.matchesInt32Context())
/* 627*/          report(t, n, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), childType.toString() }); 
/* 630*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 28:
/*   0*/      case 29:
/* 635*/        left = n.getFirstChild();
/* 636*/        this.validator.expectNumber(t, left, getJSType(left), "sign operator");
/* 637*/        ensureTyped(t, n, JSTypeNative.NUMBER_TYPE);
/*   0*/        break;
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 45:
/*   0*/      case 46:
/* 644*/        left = n.getFirstChild();
/* 645*/        right = n.getLastChild();
/* 647*/        if (left.isTypeOf()) {
/* 648*/          if (right.isString())
/* 649*/            checkTypeofString(t, right, right.getString()); 
/* 651*/        } else if (right.isTypeOf() && left.isString()) {
/* 652*/          checkTypeofString(t, left, left.getString());
/*   0*/        } 
/* 655*/        leftType = getJSType(left);
/* 656*/        rightType = getJSType(right);
/* 667*/        leftTypeRestricted = leftType.restrictByNotNullOrUndefined();
/* 668*/        rightTypeRestricted = rightType.restrictByNotNullOrUndefined();
/* 670*/        result = TernaryValue.UNKNOWN;
/* 671*/        if (n.getType() == 12 || n.getType() == 13) {
/* 672*/          result = leftTypeRestricted.testForEquality(rightTypeRestricted);
/* 673*/          if (n.isNE())
/* 674*/            result = result.not(); 
/* 678*/        } else if (!leftTypeRestricted.canTestForShallowEqualityWith(rightTypeRestricted)) {
/* 680*/          result = (n.getType() == 45) ? TernaryValue.FALSE : TernaryValue.TRUE;
/*   0*/        } 
/* 685*/        if (result != TernaryValue.UNKNOWN)
/* 686*/          report(t, n, DETERMINISTIC_TEST, new String[] { leftType.toString(), rightType.toString(), result.toString() }); 
/* 689*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/* 697*/        leftType = getJSType(n.getFirstChild());
/* 698*/        rightType = getJSType(n.getLastChild());
/* 699*/        if (rightType.isNumber()) {
/* 700*/          this.validator.expectNumber(t, n, leftType, "left side of numeric comparison");
/* 702*/        } else if (leftType.isNumber()) {
/* 703*/          this.validator.expectNumber(t, n, rightType, "right side of numeric comparison");
/* 705*/        } else if (!leftType.matchesNumberContext() || !rightType.matchesNumberContext()) {
/* 712*/          String message = "left side of comparison";
/* 713*/          this.validator.expectString(t, n, leftType, message);
/* 714*/          this.validator.expectNotNullOrUndefined(t, n, leftType, message, getNativeType(JSTypeNative.STRING_TYPE));
/* 716*/          message = "right side of comparison";
/* 717*/          this.validator.expectString(t, n, rightType, message);
/* 718*/          this.validator.expectNotNullOrUndefined(t, n, rightType, message, getNativeType(JSTypeNative.STRING_TYPE));
/*   0*/        } 
/* 721*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 51:
/* 725*/        left = n.getFirstChild();
/* 726*/        right = n.getLastChild();
/* 727*/        rightType = getJSType(right);
/* 728*/        this.validator.expectString(t, left, getJSType(left), "left side of 'in'");
/* 729*/        this.validator.expectObject(t, n, rightType, "'in' requires an object");
/* 730*/        if (rightType.isStruct())
/* 731*/          report(t, right, IN_USED_WITH_STRUCT, new String[0]); 
/* 733*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 52:
/* 737*/        left = n.getFirstChild();
/* 738*/        right = n.getLastChild();
/* 739*/        rightType = getJSType(right).restrictByNotNullOrUndefined();
/* 740*/        this.validator.expectAnyObject(t, left, getJSType(left), "deterministic instanceof yields false");
/* 742*/        this.validator.expectActualObject(t, right, rightType, "instanceof requires an object");
/* 744*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 86:
/* 748*/        visitAssign(t, n);
/* 749*/        typeable = false;
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
/* 763*/        checkPropCreation(t, n.getFirstChild());
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
/* 777*/        visitBinaryOperator(n.getType(), t, n);
/*   0*/        break;
/*   0*/      case 31:
/* 781*/        ensureTyped(t, n, JSTypeNative.BOOLEAN_TYPE);
/*   0*/        break;
/*   0*/      case 111:
/* 785*/        switchType = getJSType(parent.getFirstChild());
/* 786*/        caseType = getJSType(n.getFirstChild());
/* 787*/        this.validator.expectSwitchMatchesCase(t, n, switchType, caseType);
/* 788*/        typeable = false;
/*   0*/        break;
/*   0*/      case 119:
/* 792*/        child = n.getFirstChild();
/* 793*/        childType = getJSType(child);
/* 794*/        this.validator.expectObject(t, child, childType, "with requires an object");
/* 795*/        typeable = false;
/*   0*/        break;
/*   0*/      case 105:
/* 800*/        visitFunction(t, n);
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
/* 818*/        typeable = false;
/*   0*/        break;
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/* 825*/        typeable = false;
/*   0*/        break;
/*   0*/      case 115:
/* 829*/        if (NodeUtil.isForIn(n)) {
/* 830*/          Node obj = n.getChildAtIndex(1);
/* 831*/          if (getJSType(obj).isStruct())
/* 832*/            report(t, obj, IN_USED_WITH_STRUCT, new String[0]); 
/*   0*/        } 
/* 835*/        typeable = false;
/*   0*/        break;
/*   0*/      case 64:
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/* 843*/        if (n.getJSType() != null) {
/* 844*/          ensureTyped(t, n);
/* 847*/        } else if (n.isObjectLit() && parent.getJSType() instanceof EnumType) {
/* 849*/          ensureTyped(t, n, parent.getJSType());
/*   0*/        } else {
/* 851*/          ensureTyped(t, n);
/*   0*/        } 
/* 854*/        if (n.isObjectLit()) {
/* 855*/          JSType typ = getJSType(n);
/* 856*/          for (Node key : n.children())
/* 857*/            visitObjLitKey(t, key, n, typ); 
/*   0*/        } 
/*   0*/        break;
/*   0*/      default:
/* 863*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(n.getType()) });
/* 864*/        ensureTyped(t, n);
/*   0*/        break;
/*   0*/    } 
/* 869*/    typeable = (typeable && !this.inExterns);
/* 871*/    if (typeable)
/* 872*/      doPercentTypedAccounting(t, n); 
/* 875*/    checkNoTypeCheckSection(n, false);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkTypeofString(NodeTraversal t, Node n, String s) {
/* 879*/    if (!s.equals("number") && !s.equals("string") && !s.equals("boolean") && !s.equals("undefined") && !s.equals("function") && !s.equals("object") && !s.equals("unknown"))
/* 882*/      this.validator.expectValidTypeofName(t, n, s); 
/*   0*/  }
/*   0*/  
/*   0*/  private void doPercentTypedAccounting(NodeTraversal t, Node n) {
/* 891*/    JSType type = n.getJSType();
/* 892*/    if (type == null) {
/* 893*/      this.nullCount++;
/* 894*/    } else if (type.isUnknownType()) {
/* 895*/      if (this.reportUnknownTypes)
/* 896*/        this.compiler.report(t.makeError(n, UNKNOWN_EXPR_TYPE, new String[0])); 
/* 898*/      this.unknownCount++;
/*   0*/    } else {
/* 900*/      this.typedCount++;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitAssign(NodeTraversal t, Node assign) {
/* 913*/    JSDocInfo info = assign.getJSDocInfo();
/* 914*/    Node lvalue = assign.getFirstChild();
/* 915*/    Node rvalue = assign.getLastChild();
/* 918*/    if (lvalue.isGetProp()) {
/* 919*/      Node object = lvalue.getFirstChild();
/* 920*/      JSType objectJsType = getJSType(object);
/* 921*/      Node property = lvalue.getLastChild();
/* 922*/      String pname = property.getString();
/* 926*/      if (object.isGetProp()) {
/* 927*/        JSType jsType = getJSType(object.getFirstChild());
/* 928*/        if (jsType.isInterface() && object.getLastChild().getString().equals("prototype"))
/* 930*/          visitInterfaceGetprop(t, assign, object, pname, lvalue, rvalue); 
/*   0*/      } 
/* 934*/      checkEnumAlias(t, info, rvalue);
/* 935*/      checkPropCreation(t, lvalue);
/* 941*/      if (pname.equals("prototype") && 
/* 942*/        objectJsType != null && objectJsType.isFunctionType()) {
/* 943*/        FunctionType functionType = objectJsType.toMaybeFunctionType();
/* 944*/        if (functionType.isConstructor()) {
/* 945*/          JSType rvalueType = rvalue.getJSType();
/* 946*/          this.validator.expectObject(t, rvalue, rvalueType, "overriding prototype with non-object");
/* 949*/          if (functionType.makesStructs() && !rvalueType.isStruct()) {
/* 950*/            String funName = functionType.getTypeOfThis().toString();
/* 951*/            this.compiler.report(t.makeError(assign, CONFLICTING_SHAPE_TYPE, new String[] { "struct", funName }));
/*   0*/          } 
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 962*/      ObjectType type = ObjectType.cast(objectJsType.restrictByNotNullOrUndefined());
/* 964*/      if (type != null && 
/* 965*/        type.hasProperty(pname) && !type.isPropertyTypeInferred(pname) && !propertyIsImplicitCast(type, pname)) {
/* 968*/        JSType expectedType = type.getPropertyType(pname);
/* 969*/        if (!expectedType.isUnknownType()) {
/* 970*/          this.validator.expectCanAssignToPropertyOf(t, assign, getJSType(rvalue), expectedType, object, pname);
/* 973*/          checkPropertyInheritanceOnGetpropAssign(t, assign, object, pname, info, expectedType);
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/* 982*/      checkPropertyInheritanceOnGetpropAssign(t, assign, object, pname, info, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/    } 
/* 991*/    JSType leftType = getJSType(lvalue);
/* 992*/    if (lvalue.isQualifiedName()) {
/* 994*/      Scope.Var var = t.getScope().getVar(lvalue.getQualifiedName());
/* 995*/      if (var != null) {
/* 996*/        if (var.isTypeInferred())
/*   0*/          return; 
/*1000*/        if (NodeUtil.getRootOfQualifiedName(lvalue).isThis() && t.getScope() != var.getScope())
/*   0*/          return; 
/*1006*/        if (var.getType() != null)
/*1007*/          leftType = var.getType(); 
/*   0*/      } 
/*   0*/    } 
/*1013*/    Node rightChild = assign.getLastChild();
/*1014*/    JSType rightType = getJSType(rightChild);
/*1015*/    if (this.validator.expectCanAssignTo(t, assign, rightType, leftType, "assignment")) {
/*1017*/      ensureTyped(t, assign, rightType);
/*   0*/    } else {
/*1019*/      ensureTyped(t, assign);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropCreation(NodeTraversal t, Node lvalue) {
/*1031*/    if (lvalue.isGetProp()) {
/*1032*/      Node obj = lvalue.getFirstChild();
/*1033*/      Node prop = lvalue.getLastChild();
/*1034*/      JSType objType = getJSType(obj);
/*1035*/      String pname = prop.getString();
/*1037*/      if (!objType.isStruct() || objType.hasProperty(pname))
/*   0*/        return; 
/*1040*/      Scope s = t.getScope();
/*1041*/      if (obj.isThis() && getJSType(s.getRootNode()).isConstructor())
/*   0*/        return; 
/*1045*/      Node assgnExp = lvalue.getParent();
/*1046*/      Node assgnStm = assgnExp.getParent();
/*1047*/      if (objType instanceof ObjectType && s.isGlobal() && NodeUtil.isPrototypePropertyDeclaration(assgnStm)) {
/*1050*/        ObjectType instance = objType.toObjectType().getOwnerFunction().getInstanceType();
/*1052*/        String file = lvalue.getSourceFileName();
/*1053*/        Node ctor = instance.getConstructor().getSource();
/*1054*/        if (ctor != null && ctor.getSourceFileName().equals(file)) {
/*1055*/          JSType rvalueType = assgnExp.getLastChild().getJSType();
/*1056*/          instance.defineInferredProperty(pname, rvalueType, lvalue);
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/*1060*/      report(t, prop, ILLEGAL_PROPERTY_CREATION, new String[0]);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyInheritanceOnGetpropAssign(NodeTraversal t, Node assign, Node object, String property, JSDocInfo info, JSType propertyType) {
/*1078*/    if (object.isGetProp()) {
/*1079*/      Node object2 = object.getFirstChild();
/*1080*/      String property2 = NodeUtil.getStringValue(object.getLastChild());
/*1082*/      if ("prototype".equals(property2)) {
/*1083*/        JSType jsType = getJSType(object2);
/*1084*/        if (jsType.isFunctionType()) {
/*1085*/          FunctionType functionType = jsType.toMaybeFunctionType();
/*1086*/          if (functionType.isConstructor() || functionType.isInterface())
/*1087*/            checkDeclaredPropertyInheritance(t, assign, functionType, property, info, propertyType); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitObjLitKey(NodeTraversal t, Node key, Node objlit, JSType litType) {
/*1108*/    if (objlit.isFromExterns()) {
/*1109*/      ensureTyped(t, key);
/*   0*/      return;
/*   0*/    } 
/*1114*/    if (litType.isStruct() && key.isQuotedString()) {
/*1115*/      report(t, key, ILLEGAL_OBJLIT_KEY, new String[] { "struct" });
/*1116*/    } else if (litType.isDict() && !key.isQuotedString()) {
/*1117*/      report(t, key, ILLEGAL_OBJLIT_KEY, new String[] { "dict" });
/*   0*/    } 
/*1125*/    Node rvalue = key.getFirstChild();
/*1126*/    JSType rightType = NodeUtil.getObjectLitKeyTypeFromValueType(key, getJSType(rvalue));
/*1128*/    if (rightType == null)
/*1129*/      rightType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*1132*/    Node owner = objlit;
/*1136*/    JSType keyType = getJSType(key);
/*1138*/    JSType allowedValueType = keyType;
/*1139*/    if (allowedValueType.isEnumElementType())
/*1140*/      allowedValueType = allowedValueType.toMaybeEnumElementType().getPrimitiveType(); 
/*1144*/    boolean valid = this.validator.expectCanAssignToPropertyOf(t, key, rightType, allowedValueType, owner, NodeUtil.getObjectLitKeyName(key));
/*1147*/    if (valid) {
/*1148*/      ensureTyped(t, key, rightType);
/*   0*/    } else {
/*1150*/      ensureTyped(t, key);
/*   0*/    } 
/*1159*/    JSType objlitType = getJSType(objlit);
/*1160*/    ObjectType type = ObjectType.cast(objlitType.restrictByNotNullOrUndefined());
/*1162*/    if (type != null) {
/*1163*/      String property = NodeUtil.getObjectLitKeyName(key);
/*1164*/      if (type.hasProperty(property) && !type.isPropertyTypeInferred(property) && !propertyIsImplicitCast(type, property))
/*1167*/        this.validator.expectCanAssignToPropertyOf(t, key, keyType, type.getPropertyType(property), owner, property); 
/*   0*/      return;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean propertyIsImplicitCast(ObjectType type, String prop) {
/*1180*/    for (; type != null; type = type.getImplicitPrototype()) {
/*1181*/      JSDocInfo docInfo = type.getOwnPropertyJSDocInfo(prop);
/*1182*/      if (docInfo != null && docInfo.isImplicitCast())
/*1183*/        return true; 
/*   0*/    } 
/*1186*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkDeclaredPropertyInheritance(NodeTraversal t, Node n, FunctionType ctorType, String propertyName, JSDocInfo info, JSType propertyType) {
/*1200*/    if (hasUnknownOrEmptySupertype(ctorType))
/*   0*/      return; 
/*1204*/    FunctionType superClass = ctorType.getSuperClassConstructor();
/*1205*/    boolean superClassHasProperty = (superClass != null && superClass.getInstanceType().hasProperty(propertyName));
/*1207*/    boolean superClassHasDeclaredProperty = (superClass != null && superClass.getInstanceType().isPropertyTypeDeclared(propertyName));
/*   0*/    boolean superInterfaceHasProperty = false;
/*   0*/    boolean superInterfaceHasDeclaredProperty = false;
/*1213*/    if (ctorType.isInterface())
/*1214*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1215*/        superInterfaceHasProperty = (superInterfaceHasProperty || interfaceType.hasProperty(propertyName));
/*1218*/        superInterfaceHasDeclaredProperty = (superInterfaceHasDeclaredProperty || interfaceType.isPropertyTypeDeclared(propertyName));
/*   0*/      }  
/*1223*/    boolean declaredOverride = (info != null && info.isOverride());
/*   0*/    boolean foundInterfaceProperty = false;
/*1226*/    if (ctorType.isConstructor())
/*1228*/      for (JSType implementedInterface : ctorType.getAllImplementedInterfaces()) {
/*1229*/        if (implementedInterface.isUnknownType() || implementedInterface.isEmptyType())
/*   0*/          continue; 
/*1233*/        FunctionType interfaceType = implementedInterface.toObjectType().getConstructor();
/*1235*/        Preconditions.checkNotNull(interfaceType);
/*1237*/        boolean interfaceHasProperty = interfaceType.getPrototype().hasProperty(propertyName);
/*1239*/        foundInterfaceProperty = (foundInterfaceProperty || interfaceHasProperty);
/*1241*/        if (this.reportMissingOverride.isOn() && !declaredOverride && interfaceHasProperty)
/*1246*/          this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_INTERFACE_PROPERTY, new String[] { propertyName, interfaceType.getTopMostDefiningType(propertyName).toString() })); 
/*   0*/      }  
/*1253*/    if (!declaredOverride && !superClassHasProperty && !superInterfaceHasProperty)
/*   0*/      return; 
/*1260*/    ObjectType topInstanceType = superClassHasDeclaredProperty ? superClass.getTopMostDefiningType(propertyName) : null;
/*1262*/    boolean declaredLocally = (ctorType.isConstructor() && (ctorType.getPrototype().hasOwnProperty(propertyName) || ctorType.getInstanceType().hasOwnProperty(propertyName)));
/*1266*/    if (this.reportMissingOverride.isOn() && !declaredOverride && superClassHasDeclaredProperty && declaredLocally)
/*1272*/      this.compiler.report(t.makeError(n, this.reportMissingOverride, HIDDEN_SUPERCLASS_PROPERTY, new String[] { propertyName, topInstanceType.toString() })); 
/*1278*/    if (superClassHasDeclaredProperty) {
/*1280*/      JSType superClassPropType = superClass.getInstanceType().getPropertyType(propertyName);
/*1282*/      TemplateTypeMap ctorTypeMap = ctorType.getTypeOfThis().getTemplateTypeMap();
/*1284*/      if (!ctorTypeMap.isEmpty())
/*1285*/        superClassPropType = superClassPropType.<JSType>visit(new TemplateTypeMapReplacer(this.typeRegistry, ctorTypeMap)); 
/*1289*/      if (!propertyType.isSubtype(superClassPropType))
/*1290*/        this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superClassPropType.toString(), propertyType.toString() })); 
/*1295*/    } else if (superInterfaceHasDeclaredProperty) {
/*1297*/      for (ObjectType interfaceType : ctorType.getExtendedInterfaces()) {
/*1298*/        if (interfaceType.hasProperty(propertyName)) {
/*1299*/          JSType superPropertyType = interfaceType.getPropertyType(propertyName);
/*1301*/          if (!propertyType.isSubtype(superPropertyType)) {
/*1302*/            topInstanceType = interfaceType.getConstructor().getTopMostDefiningType(propertyName);
/*1304*/            this.compiler.report(t.makeError(n, HIDDEN_SUPERCLASS_PROPERTY_MISMATCH, new String[] { propertyName, topInstanceType.toString(), superPropertyType.toString(), propertyType.toString() }));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1312*/    } else if (!foundInterfaceProperty && !superClassHasProperty && !superInterfaceHasProperty) {
/*1316*/      this.compiler.report(t.makeError(n, UNKNOWN_OVERRIDE, new String[] { propertyName, ctorType.getInstanceType().toString() }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean hasUnknownOrEmptySupertype(FunctionType ctor) {
/*1327*/    Preconditions.checkArgument((ctor.isConstructor() || ctor.isInterface()));
/*1328*/    Preconditions.checkArgument(!ctor.isUnknownType());
/*   0*/    while (true) {
/*1333*/      ObjectType maybeSuperInstanceType = ctor.getPrototype().getImplicitPrototype();
/*1335*/      if (maybeSuperInstanceType == null)
/*1336*/        return false; 
/*1338*/      if (maybeSuperInstanceType.isUnknownType() || maybeSuperInstanceType.isEmptyType())
/*1340*/        return true; 
/*1342*/      ctor = maybeSuperInstanceType.getConstructor();
/*1343*/      if (ctor == null)
/*1344*/        return false; 
/*1346*/      Preconditions.checkState((ctor.isConstructor() || ctor.isInterface()));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitInterfaceGetprop(NodeTraversal t, Node assign, Node object, String property, Node lvalue, Node rvalue) {
/*1359*/    JSType rvalueType = getJSType(rvalue);
/*1367*/    String abstractMethodName = this.compiler.getCodingConvention().getAbstractMethodName();
/*1369*/    if (!rvalueType.isFunctionType()) {
/*1371*/      String abstractMethodMessage = (abstractMethodName != null) ? (", or " + abstractMethodName) : "";
/*1374*/      this.compiler.report(t.makeError(object, INVALID_INTERFACE_MEMBER_DECLARATION, new String[] { abstractMethodMessage }));
/*   0*/    } 
/*1379*/    if (assign.getLastChild().isFunction() && !NodeUtil.isEmptyBlock(assign.getLastChild().getLastChild()))
/*1381*/      this.compiler.report(t.makeError(object, INTERFACE_FUNCTION_NOT_EMPTY, new String[] { abstractMethodName })); 
/*   0*/  }
/*   0*/  
/*   0*/  boolean visitName(NodeTraversal t, Node n, Node parent) {
/*1404*/    int parentNodeType = parent.getType();
/*1405*/    if (parentNodeType == 105 || parentNodeType == 120 || parentNodeType == 83 || parentNodeType == 118)
/*1409*/      return false; 
/*1412*/    JSType type = n.getJSType();
/*1413*/    if (type == null) {
/*1414*/      type = getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*1415*/      Scope.Var var = t.getScope().getVar(n.getString());
/*1416*/      if (var != null) {
/*1417*/        JSType varType = var.getType();
/*1418*/        if (varType != null)
/*1419*/          type = varType; 
/*   0*/      } 
/*   0*/    } 
/*1423*/    ensureTyped(t, n, type);
/*1424*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetProp(NodeTraversal t, Node n, Node parent) {
/*1440*/    Node property = n.getLastChild();
/*1441*/    Node objNode = n.getFirstChild();
/*1442*/    JSType childType = getJSType(objNode);
/*1444*/    if (childType.isDict()) {
/*1445*/      report(t, property, TypeValidator.ILLEGAL_PROPERTY_ACCESS, new String[] { "'.'", "dict" });
/*1446*/    } else if (this.validator.expectNotNullOrUndefined(t, n, childType, "No properties on this expression", getNativeType(JSTypeNative.OBJECT_TYPE))) {
/*1448*/      checkPropertyAccess(childType, property.getString(), t, n);
/*   0*/    } 
/*1450*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccess(JSType childType, String propName, NodeTraversal t, Node n) {
/*1466*/    JSType propType = getJSType(n);
/*1467*/    if (propType.isEquivalentTo(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE))) {
/*1468*/      childType = childType.autobox();
/*1469*/      ObjectType objectType = ObjectType.cast(childType);
/*1470*/      if (objectType != null) {
/*1474*/        if (!objectType.hasProperty(propName) || objectType.isEquivalentTo(this.typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE)))
/*1477*/          if (objectType instanceof EnumType) {
/*1478*/            report(t, n, INEXISTENT_ENUM_ELEMENT, new String[] { propName });
/*   0*/          } else {
/*1480*/            checkPropertyAccessHelper(objectType, propName, t, n);
/*   0*/          }  
/*   0*/      } else {
/*1485*/        checkPropertyAccessHelper(childType, propName, t, n);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPropertyAccessHelper(JSType objectType, String propName, NodeTraversal t, Node n) {
/*1492*/    if (!objectType.isEmptyType() && this.reportMissingProperties && (!isPropertyTest(n) || objectType.isStruct()))
/*1495*/      if (!this.typeRegistry.canPropertyBeDefined(objectType, propName)) {
/*1496*/        SuggestionPair pair = getClosestPropertySuggestion(objectType, propName);
/*1498*/        if (pair != null && pair.distance * 4 < propName.length()) {
/*1499*/          report(t, n, INEXISTENT_PROPERTY_WITH_SUGGESTION, new String[] { propName, this.validator.getReadableJSTypeName(n.getFirstChild(), true), pair.suggestion });
/*   0*/        } else {
/*1503*/          report(t, n, INEXISTENT_PROPERTY, new String[] { propName, this.validator.getReadableJSTypeName(n.getFirstChild(), true) });
/*   0*/        } 
/*   0*/      }  
/*   0*/  }
/*   0*/  
/*   0*/  private SuggestionPair getClosestPropertySuggestion(JSType objectType, String propName) {
/*1512*/    if (this.editDistance == null)
/*1513*/      return null; 
/*1516*/    String bestSoFar = null;
/*1517*/    int shortest = Integer.MAX_VALUE;
/*1518*/    if (objectType instanceof ObjectType) {
/*1519*/      ObjectType type = (ObjectType)objectType;
/*1520*/      for (String alt : type.getPropertyNames()) {
/*   0*/        int distance;
/*   0*/        try {
/*1523*/          distance = (Integer)this.editDistance.invoke(null, new Object[] { propName, alt, false });
/*1524*/        } catch (Exception e) {
/*1525*/          return null;
/*   0*/        } 
/*1527*/        if (distance <= shortest) {
/*1528*/          if (distance == shortest)
/*1531*/            if (bestSoFar != null && alt.compareToIgnoreCase(bestSoFar) > 0)
/*   0*/              continue;  
/*1535*/          shortest = distance;
/*1536*/          bestSoFar = alt;
/*   0*/        } 
/*   0*/      } 
/*1539*/    } else if (objectType.isUnionType()) {
/*1540*/      UnionType type = (UnionType)objectType;
/*1541*/      for (JSType alt : type.getAlternates()) {
/*1542*/        SuggestionPair pair = getClosestPropertySuggestion(alt, propName);
/*1543*/        if (pair == null || 
/*1544*/          pair.distance > shortest || (
/*1545*/          pair.distance == shortest && 
/*1546*/          bestSoFar != null && pair.suggestion.compareToIgnoreCase(bestSoFar) > 0))
/*   0*/          continue; 
/*1551*/        shortest = pair.distance;
/*1552*/        bestSoFar = pair.suggestion;
/*   0*/      } 
/*   0*/    } 
/*1558*/    if (bestSoFar != null)
/*1559*/      return new SuggestionPair(bestSoFar, shortest); 
/*1562*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isPropertyTest(Node getProp) {
/*1572*/    Node parent = getProp.getParent();
/*1573*/    switch (parent.getType()) {
/*   0*/      case 37:
/*1575*/        return (parent.getFirstChild() != getProp && this.compiler.getCodingConvention().isPropertyTestFunction(parent));
/*   0*/      case 108:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*1582*/        return (NodeUtil.getConditionExpression(parent) == getProp);
/*   0*/      case 32:
/*   0*/      case 52:
/*1586*/        return true;
/*   0*/      case 98:
/*   0*/      case 101:
/*1590*/        return (parent.getFirstChild() == getProp);
/*   0*/      case 26:
/*1593*/        return (parent.getParent().isOr() && parent.getParent().getFirstChild() == parent);
/*   0*/      case 155:
/*1597*/        return isPropertyTest(parent);
/*   0*/    } 
/*1599*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void visitGetElem(NodeTraversal t, Node n) {
/*1610*/    this.validator.expectIndexMatch(t, n, getJSType(n.getFirstChild()), getJSType(n.getLastChild()));
/*1612*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void visitVar(NodeTraversal t, Node n) {
/*1626*/    JSDocInfo varInfo = n.hasOneChild() ? n.getJSDocInfo() : null;
/*1627*/    for (Node name : n.children()) {
/*1628*/      Node value = name.getFirstChild();
/*1630*/      Scope.Var var = t.getScope().getVar(name.getString());
/*1632*/      if (value != null) {
/*1633*/        JSType valueType = getJSType(value);
/*1634*/        JSType nameType = var.getType();
/*1635*/        nameType = (nameType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : nameType;
/*1637*/        JSDocInfo info = name.getJSDocInfo();
/*1638*/        if (info == null)
/*1639*/          info = varInfo; 
/*1642*/        checkEnumAlias(t, info, value);
/*1643*/        if (var.isTypeInferred()) {
/*1644*/          ensureTyped(t, name, valueType);
/*   0*/          continue;
/*   0*/        } 
/*1646*/        this.validator.expectCanAssignTo(t, value, valueType, nameType, "initializing variable");
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitNew(NodeTraversal t, Node n) {
/*1657*/    Node constructor = n.getFirstChild();
/*1658*/    JSType type = getJSType(constructor).restrictByNotNullOrUndefined();
/*1659*/    if (type.isConstructor() || type.isEmptyType() || type.isUnknownType()) {
/*1660*/      FunctionType fnType = type.toMaybeFunctionType();
/*1661*/      if (fnType != null) {
/*1662*/        visitParameterList(t, n, fnType);
/*1663*/        ensureTyped(t, n, fnType.getPrototype());
/*   0*/      } else {
/*1665*/        ensureTyped(t, n);
/*   0*/      } 
/*   0*/    } else {
/*1668*/      report(t, n, NOT_A_CONSTRUCTOR, new String[0]);
/*1669*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkInterfaceConflictProperties(NodeTraversal t, Node n, String functionName, HashMap<String, ObjectType> properties, HashMap<String, ObjectType> currentProperties, ObjectType interfaceType) {
/*   0*/    Set<String> currentPropertyNames;
/*1689*/    ObjectType implicitProto = interfaceType.getImplicitPrototype();
/*1691*/    if (implicitProto == null) {
/*1694*/      ImmutableSet immutableSet = ImmutableSet.of();
/*   0*/    } else {
/*1696*/      currentPropertyNames = implicitProto.getOwnPropertyNames();
/*   0*/    } 
/*1698*/    for (String name : currentPropertyNames) {
/*1699*/      ObjectType oType = properties.get(name);
/*1700*/      if (oType != null && 
/*1701*/        !interfaceType.getPropertyType(name).isEquivalentTo(oType.getPropertyType(name)))
/*1703*/        this.compiler.report(t.makeError(n, INCOMPATIBLE_EXTENDED_PROPERTY_TYPE, new String[] { functionName, name, oType.toString(), interfaceType.toString() })); 
/*1709*/      currentProperties.put(name, interfaceType);
/*   0*/    } 
/*1711*/    for (ObjectType iType : interfaceType.getCtorExtendedInterfaces())
/*1712*/      checkInterfaceConflictProperties(t, n, functionName, properties, currentProperties, iType); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitFunction(NodeTraversal t, Node n) {
/*1725*/    FunctionType functionType = JSType.toMaybeFunctionType(n.getJSType());
/*1726*/    String functionPrivateName = n.getFirstChild().getString();
/*1727*/    if (functionType.isConstructor()) {
/*1728*/      FunctionType baseConstructor = functionType.getSuperClassConstructor();
/*1729*/      if (baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE) && baseConstructor != null && baseConstructor.isInterface()) {
/*1732*/        this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "constructor", functionPrivateName }));
/*   0*/      } else {
/*1736*/        if (baseConstructor != getNativeType(JSTypeNative.OBJECT_FUNCTION_TYPE)) {
/*1737*/          ObjectType proto = functionType.getPrototype();
/*1738*/          if (functionType.makesStructs() && !proto.isStruct()) {
/*1739*/            this.compiler.report(t.makeError(n, CONFLICTING_SHAPE_TYPE, new String[] { "struct", functionPrivateName }));
/*1741*/          } else if (functionType.makesDicts() && !proto.isDict()) {
/*1742*/            this.compiler.report(t.makeError(n, CONFLICTING_SHAPE_TYPE, new String[] { "dict", functionPrivateName }));
/*   0*/          } 
/*   0*/        } 
/*1747*/        for (JSType baseInterface : functionType.getImplementedInterfaces()) {
/*   0*/          boolean badImplementedType = false;
/*1749*/          ObjectType baseInterfaceObj = ObjectType.cast(baseInterface);
/*1750*/          if (baseInterfaceObj != null) {
/*1751*/            FunctionType interfaceConstructor = baseInterfaceObj.getConstructor();
/*1753*/            if (interfaceConstructor != null && !interfaceConstructor.isInterface())
/*1755*/              badImplementedType = true; 
/*   0*/          } else {
/*1758*/            badImplementedType = true;
/*   0*/          } 
/*1760*/          if (badImplementedType)
/*1761*/            report(t, n, BAD_IMPLEMENTED_TYPE, new String[] { functionPrivateName }); 
/*   0*/        } 
/*1765*/        this.validator.expectAllInterfaceProperties(t, n, functionType);
/*   0*/      } 
/*1767*/    } else if (functionType.isInterface()) {
/*1769*/      for (ObjectType extInterface : functionType.getExtendedInterfaces()) {
/*1770*/        if (extInterface.getConstructor() != null && !extInterface.getConstructor().isInterface())
/*1772*/          this.compiler.report(t.makeError(n, CONFLICTING_EXTENDED_TYPE, new String[] { "interface", functionPrivateName })); 
/*   0*/      } 
/*1779*/      if (functionType.getExtendedInterfacesCount() > 1) {
/*1781*/        HashMap<String, ObjectType> properties = new HashMap<String, ObjectType>();
/*1783*/        HashMap<String, ObjectType> currentProperties = new HashMap<String, ObjectType>();
/*1785*/        for (ObjectType interfaceType : functionType.getExtendedInterfaces()) {
/*1786*/          currentProperties.clear();
/*1787*/          checkInterfaceConflictProperties(t, n, functionPrivateName, properties, currentProperties, interfaceType);
/*1789*/          properties.putAll(currentProperties);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitCall(NodeTraversal t, Node n) {
/*1803*/    Node child = n.getFirstChild();
/*1804*/    JSType childType = getJSType(child).restrictByNotNullOrUndefined();
/*1806*/    if (!childType.canBeCalled()) {
/*1807*/      report(t, n, NOT_CALLABLE, new String[] { childType.toString() });
/*1808*/      ensureTyped(t, n);
/*   0*/      return;
/*   0*/    } 
/*1814*/    if (childType.isFunctionType()) {
/*1815*/      FunctionType functionType = childType.toMaybeFunctionType();
/*   0*/      boolean isExtern = false;
/*1818*/      JSDocInfo functionJSDocInfo = functionType.getJSDocInfo();
/*1819*/      if (functionJSDocInfo != null && functionJSDocInfo.getAssociatedNode() != null)
/*1821*/        isExtern = functionJSDocInfo.getAssociatedNode().isFromExterns(); 
/*1827*/      if (functionType.isConstructor() && !functionType.isNativeObjectType() && (functionType.getReturnType().isUnknownType() || functionType.getReturnType().isVoidType() || !isExtern))
/*1832*/        report(t, n, CONSTRUCTOR_NOT_CALLABLE, new String[] { childType.toString() }); 
/*1837*/      if (functionType.isOrdinaryFunction() && !functionType.getTypeOfThis().isUnknownType() && (functionType.getTypeOfThis().toObjectType() == null || !functionType.getTypeOfThis().toObjectType().isNativeObjectType()) && !child.isGetElem() && !child.isGetProp())
/*1843*/        report(t, n, EXPECTED_THIS_TYPE, new String[] { functionType.toString() }); 
/*1846*/      visitParameterList(t, n, functionType);
/*1847*/      ensureTyped(t, n, functionType.getReturnType());
/*   0*/    } else {
/*1849*/      ensureTyped(t, n);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitParameterList(NodeTraversal t, Node call, FunctionType functionType) {
/*1862*/    Iterator<Node> arguments = call.children().iterator();
/*1863*/    arguments.next();
/*1865*/    Iterator<Node> parameters = functionType.getParameters().iterator();
/*1866*/    int ordinal = 0;
/*1867*/    Node parameter = null;
/*1868*/    Node argument = null;
/*1869*/    while (arguments.hasNext() && (parameters.hasNext() || (parameter != null && parameter.isVarArgs()))) {
/*1874*/      if (parameters.hasNext())
/*1875*/        parameter = parameters.next(); 
/*1877*/      argument = arguments.next();
/*1878*/      ordinal++;
/*1880*/      this.validator.expectArgumentMatchesParameter(t, argument, getJSType(argument), getJSType(parameter), call, ordinal);
/*   0*/    } 
/*1884*/    int numArgs = call.getChildCount() - 1;
/*1885*/    int minArgs = functionType.getMinArguments();
/*1886*/    int maxArgs = functionType.getMaxArguments();
/*1887*/    if (minArgs > numArgs || maxArgs < numArgs)
/*1888*/      report(t, call, WRONG_ARGUMENT_COUNT, new String[] { this.validator.getReadableJSTypeName(call.getFirstChild(), false), String.valueOf(numArgs), String.valueOf(minArgs), (maxArgs != Integer.MAX_VALUE) ? (" and no more than " + maxArgs + " argument(s)") : "" }); 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitReturn(NodeTraversal t, Node n) {
/*1904*/    JSType jsType = getJSType(t.getEnclosingFunction());
/*1906*/    if (jsType.isFunctionType()) {
/*   0*/      JSType actualReturnType;
/*1907*/      FunctionType functionType = jsType.toMaybeFunctionType();
/*1909*/      JSType returnType = functionType.getReturnType();
/*1913*/      if (returnType == null)
/*1914*/        returnType = getNativeType(JSTypeNative.VOID_TYPE); 
/*1918*/      Node valueNode = n.getFirstChild();
/*1920*/      if (valueNode == null) {
/*1921*/        actualReturnType = getNativeType(JSTypeNative.VOID_TYPE);
/*1922*/        valueNode = n;
/*   0*/      } else {
/*1924*/        actualReturnType = getJSType(valueNode);
/*   0*/      } 
/*1928*/      this.validator.expectCanAssignTo(t, valueNode, actualReturnType, returnType, "inconsistent return type");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void visitBinaryOperator(int op, NodeTraversal t, Node n) {
/*1944*/    Node left = n.getFirstChild();
/*1945*/    JSType leftType = getJSType(left);
/*1946*/    Node right = n.getLastChild();
/*1947*/    JSType rightType = getJSType(right);
/*1948*/    switch (op) {
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*1955*/        if (!leftType.matchesInt32Context())
/*1956*/          report(t, left, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), leftType.toString() }); 
/*1959*/        if (!rightType.matchesUint32Context())
/*1960*/          report(t, right, BIT_OPERATION, new String[] { NodeUtil.opToStr(n.getType()), rightType.toString() }); 
/*   0*/        break;
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/*1973*/        this.validator.expectNumber(t, left, leftType, "left operand");
/*1974*/        this.validator.expectNumber(t, right, rightType, "right operand");
/*   0*/        break;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*1983*/        this.validator.expectBitwiseable(t, left, leftType, "bad left operand to bitwise operator");
/*1985*/        this.validator.expectBitwiseable(t, right, rightType, "bad right operand to bitwise operator");
/*   0*/        break;
/*   0*/      case 21:
/*   0*/      case 93:
/*   0*/        break;
/*   0*/      default:
/*1994*/        report(t, n, UNEXPECTED_TOKEN, new String[] { Token.name(op) });
/*   0*/        break;
/*   0*/    } 
/*1996*/    ensureTyped(t, n);
/*   0*/  }
/*   0*/  
/*   0*/  private void checkEnumAlias(NodeTraversal t, JSDocInfo declInfo, Node value) {
/*2016*/    if (declInfo == null || !declInfo.hasEnumParameterType())
/*   0*/      return; 
/*2020*/    JSType valueType = getJSType(value);
/*2021*/    if (!valueType.isEnumType())
/*   0*/      return; 
/*2025*/    EnumType valueEnumType = valueType.toMaybeEnumType();
/*2026*/    JSType valueEnumPrimitiveType = valueEnumType.getElementsType().getPrimitiveType();
/*2028*/    this.validator.expectCanAssignTo(t, value, valueEnumPrimitiveType, declInfo.getEnumParameterType().evaluate(t.getScope(), this.typeRegistry), "incompatible enum element types");
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getJSType(Node n) {
/*2038*/    JSType jsType = n.getJSType();
/*2039*/    if (jsType == null)
/*2044*/      return getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*2046*/    return jsType;
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n) {
/*2061*/    ensureTyped(t, n, getNativeType(JSTypeNative.UNKNOWN_TYPE));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSTypeNative type) {
/*2065*/    ensureTyped(t, n, getNativeType(type));
/*   0*/  }
/*   0*/  
/*   0*/  private void ensureTyped(NodeTraversal t, Node n, JSType type) {
/*2088*/    Preconditions.checkState((!n.isFunction() || type.isFunctionType() || type.isUnknownType()));
/*2092*/    JSDocInfo info = n.getJSDocInfo();
/*2093*/    if (info != null && 
/*2094*/      info.isImplicitCast() && !this.inExterns) {
/*2095*/      String propName = n.isGetProp() ? n.getLastChild().getString() : "(missing)";
/*2097*/      this.compiler.report(t.makeError(n, ILLEGAL_IMPLICIT_CAST, new String[] { propName }));
/*   0*/    } 
/*2102*/    if (n.getJSType() == null)
/*2103*/      n.setJSType(type); 
/*   0*/  }
/*   0*/  
/*   0*/  double getTypedPercent() {
/*2112*/    int total = this.nullCount + this.unknownCount + this.typedCount;
/*2113*/    return (total == 0) ? 0.0D : (100.0D * this.typedCount / total);
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeType(JSTypeNative typeId) {
/*2117*/    return this.typeRegistry.getNativeType(typeId);
/*   0*/  }
/*   0*/}
