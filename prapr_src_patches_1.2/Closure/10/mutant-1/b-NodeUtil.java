/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.base.Predicates;
/*   0*/import com.google.common.collect.ImmutableSet;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.InputId;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.Token;
/*   0*/import com.google.javascript.rhino.TokenStream;
/*   0*/import com.google.javascript.rhino.jstype.FunctionType;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.StaticSourceFile;
/*   0*/import com.google.javascript.rhino.jstype.TernaryValue;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import javax.annotation.Nullable;
/*   0*/
/*   0*/public final class NodeUtil {
/*  50*/  static final long MAX_POSITIVE_INTEGER_NUMBER = (long)Math.pow(2.0D, 53.0D);
/*   0*/  
/*   0*/  static final String JSC_PROPERTY_NAME_FN = "JSCompiler_renameProperty";
/*   0*/  
/*   0*/  static final char LARGEST_BASIC_LATIN = '\u007F';
/*   0*/  
/*  57*/  private static final Set<String> CONSTRUCTORS_WITHOUT_SIDE_EFFECTS = new HashSet<String>(Arrays.asList(new String[] { "Array", "Date", "Error", "Object", "RegExp", "XMLHttpRequest" }));
/*   0*/  
/*   0*/  static TernaryValue getImpureBooleanValue(Node n) {
/*   0*/    TernaryValue value, lhs, trueValue, rhs, falseValue;
/*  76*/    switch (n.getType()) {
/*   0*/      case 85:
/*   0*/      case 86:
/*  80*/        return getImpureBooleanValue(n.getLastChild());
/*   0*/      case 26:
/*  82*/        value = getImpureBooleanValue(n.getLastChild());
/*  83*/        return value.not();
/*   0*/      case 101:
/*  85*/        lhs = getImpureBooleanValue(n.getFirstChild());
/*  86*/        rhs = getImpureBooleanValue(n.getLastChild());
/*  87*/        return lhs.and(rhs);
/*   0*/      case 100:
/*  90*/        lhs = getImpureBooleanValue(n.getFirstChild());
/*  91*/        rhs = getImpureBooleanValue(n.getLastChild());
/*  92*/        return lhs.or(rhs);
/*   0*/      case 98:
/*  95*/        trueValue = getImpureBooleanValue(n.getFirstChild().getNext());
/*  97*/        falseValue = getImpureBooleanValue(n.getLastChild());
/*  98*/        if (trueValue.equals(falseValue))
/*  99*/          return trueValue; 
/* 101*/        return TernaryValue.UNKNOWN;
/*   0*/      case 63:
/*   0*/      case 64:
/* 107*/        return TernaryValue.TRUE;
/*   0*/      case 122:
/* 110*/        return TernaryValue.FALSE;
/*   0*/    } 
/* 113*/    return getPureBooleanValue(n);
/*   0*/  }
/*   0*/  
/*   0*/  static TernaryValue getPureBooleanValue(Node n) {
/*   0*/    String name;
/* 124*/    switch (n.getType()) {
/*   0*/      case 40:
/* 126*/        return TernaryValue.forBoolean((n.getString().length() > 0));
/*   0*/      case 39:
/* 129*/        return TernaryValue.forBoolean((n.getDouble() != 0.0D));
/*   0*/      case 26:
/* 132*/        return getPureBooleanValue(n.getLastChild()).not();
/*   0*/      case 41:
/*   0*/      case 43:
/* 136*/        return TernaryValue.FALSE;
/*   0*/      case 122:
/* 139*/        if (!mayHaveSideEffects(n.getFirstChild()))
/* 140*/          return TernaryValue.FALSE; 
/*   0*/        break;
/*   0*/      case 38:
/* 145*/        name = n.getString();
/* 146*/        if ("undefined".equals(name) || "NaN".equals(name))
/* 150*/          return TernaryValue.FALSE; 
/* 151*/        if ("Infinity".equals(name))
/* 152*/          return TernaryValue.TRUE; 
/*   0*/        break;
/*   0*/      case 44:
/*   0*/      case 47:
/* 158*/        return TernaryValue.TRUE;
/*   0*/      case 63:
/*   0*/      case 64:
/* 162*/        if (!mayHaveSideEffects(n))
/* 163*/          return TernaryValue.TRUE; 
/*   0*/        break;
/*   0*/    } 
/* 168*/    return TernaryValue.UNKNOWN;
/*   0*/  }
/*   0*/  
/*   0*/  static String getStringValue(Node n) {
/*   0*/    String name;
/*   0*/    TernaryValue child;
/* 178*/    switch (n.getType()) {
/*   0*/      case 40:
/*   0*/      case 154:
/* 181*/        return n.getString();
/*   0*/      case 38:
/* 184*/        name = n.getString();
/* 185*/        if ("undefined".equals(name) || "Infinity".equals(name) || "NaN".equals(name))
/* 188*/          return name; 
/*   0*/        break;
/*   0*/      case 39:
/* 193*/        return getStringValue(n.getDouble());
/*   0*/      case 43:
/* 196*/        return "false";
/*   0*/      case 44:
/* 199*/        return "true";
/*   0*/      case 41:
/* 202*/        return "null";
/*   0*/      case 122:
/* 205*/        return "undefined";
/*   0*/      case 26:
/* 208*/        child = getPureBooleanValue(n.getFirstChild());
/* 209*/        if (child != TernaryValue.UNKNOWN)
/* 210*/          return child.toBoolean(true) ? "false" : "true"; 
/*   0*/        break;
/*   0*/      case 63:
/* 215*/        return arrayToString(n);
/*   0*/      case 64:
/* 218*/        return "[object Object]";
/*   0*/    } 
/* 220*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String getStringValue(double value) {
/* 224*/    long longValue = (long)value;
/* 227*/    if (longValue == value)
/* 228*/      return Long.toString(longValue); 
/* 230*/    return Double.toString(value);
/*   0*/  }
/*   0*/  
/*   0*/  static String getArrayElementStringValue(Node n) {
/* 243*/    return (isNullOrUndefined(n) || n.isEmpty()) ? "" : getStringValue(n);
/*   0*/  }
/*   0*/  
/*   0*/  static String arrayToString(Node literal) {
/* 248*/    Node first = literal.getFirstChild();
/* 249*/    StringBuilder result = new StringBuilder();
/* 250*/    int nextSlot = 0;
/* 251*/    int nextSkipSlot = 0;
/* 252*/    for (Node n = first; n != null; n = n.getNext()) {
/* 253*/      String childValue = getArrayElementStringValue(n);
/* 254*/      if (childValue == null)
/* 255*/        return null; 
/* 257*/      if (n != first)
/* 258*/        result.append(','); 
/* 260*/      result.append(childValue);
/* 262*/      nextSlot++;
/*   0*/    } 
/* 264*/    return result.toString();
/*   0*/  }
/*   0*/  
/*   0*/  static Double getNumberValue(Node n) {
/*   0*/    String name;
/*   0*/    TernaryValue child;
/*   0*/    String value;
/* 273*/    switch (n.getType()) {
/*   0*/      case 44:
/* 275*/        return 1.0D;
/*   0*/      case 41:
/*   0*/      case 43:
/* 279*/        return 0.0D;
/*   0*/      case 39:
/* 282*/        return n.getDouble();
/*   0*/      case 122:
/* 285*/        if (mayHaveSideEffects(n.getFirstChild()))
/* 286*/          return null; 
/* 288*/        return Double.NaN;
/*   0*/      case 38:
/* 293*/        name = n.getString();
/* 294*/        if (name.equals("undefined"))
/* 295*/          return Double.NaN; 
/* 297*/        if (name.equals("NaN"))
/* 298*/          return Double.NaN; 
/* 300*/        if (name.equals("Infinity"))
/* 301*/          return Double.POSITIVE_INFINITY; 
/* 303*/        return null;
/*   0*/      case 29:
/* 306*/        if (n.getChildCount() == 1 && n.getFirstChild().isName() && n.getFirstChild().getString().equals("Infinity"))
/* 308*/          return Double.NEGATIVE_INFINITY; 
/* 310*/        return null;
/*   0*/      case 26:
/* 313*/        child = getPureBooleanValue(n.getFirstChild());
/* 314*/        if (child != TernaryValue.UNKNOWN)
/* 315*/          return child.toBoolean(true) ? 0.0D : 1.0D; 
/*   0*/        break;
/*   0*/      case 40:
/* 320*/        return getStringNumberValue(n.getString());
/*   0*/      case 63:
/*   0*/      case 64:
/* 324*/        value = getStringValue(n);
/* 325*/        return (value != null) ? getStringNumberValue(value) : null;
/*   0*/    } 
/* 328*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static Double getStringNumberValue(String rawJsString) {
/* 332*/    if (rawJsString.contains("\013"))
/* 334*/      return null; 
/* 337*/    String s = trimJsWhiteSpace(rawJsString);
/* 339*/    if (s.length() == 0)
/* 340*/      return 0.0D; 
/* 343*/    if (s.length() > 2 && s.charAt(0) == '0' && (s.charAt(1) == 'x' || s.charAt(1) == 'X'))
/*   0*/      try {
/* 348*/        return Integer.parseInt(s.substring(2), 16);
/* 349*/      } catch (NumberFormatException e) {
/* 350*/        return Double.NaN;
/*   0*/      }  
/* 354*/    if (s.length() > 3 && (s.charAt(0) == '-' || s.charAt(0) == '+') && s.charAt(1) == '0' && (s.charAt(2) == 'x' || s.charAt(2) == 'X'))
/* 359*/      return null; 
/* 364*/    if (s.equals("infinity") || s.equals("-infinity") || s.equals("+infinity"))
/* 367*/      return null; 
/*   0*/    try {
/* 371*/      return Double.parseDouble(s);
/* 372*/    } catch (NumberFormatException e) {
/* 373*/      return Double.NaN;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static String trimJsWhiteSpace(String s) {
/* 378*/    int start = 0;
/* 379*/    int end = s.length();
/* 381*/    while (end > 0 && isStrWhiteSpaceChar(s.charAt(end - 1)) == TernaryValue.TRUE)
/* 382*/      end--; 
/* 385*/    while (start < end && isStrWhiteSpaceChar(s.charAt(start)) == TernaryValue.TRUE)
/* 386*/      start++; 
/* 388*/    return s.substring(start, end);
/*   0*/  }
/*   0*/  
/*   0*/  public static TernaryValue isStrWhiteSpaceChar(int c) {
/* 395*/    switch (c) {
/*   0*/      case 11:
/* 397*/        return TernaryValue.UNKNOWN;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 32:
/*   0*/      case 160:
/*   0*/      case 8232:
/*   0*/      case 8233:
/*   0*/      case 65279:
/* 407*/        return TernaryValue.TRUE;
/*   0*/    } 
/* 409*/    return (Character.getType(c) == 12) ? TernaryValue.TRUE : TernaryValue.FALSE;
/*   0*/  }
/*   0*/  
/*   0*/  static String getFunctionName(Node n) {
/* 430*/    Preconditions.checkState(n.isFunction());
/* 431*/    Node parent = n.getParent();
/* 432*/    switch (parent.getType()) {
/*   0*/      case 38:
/* 436*/        return parent.getQualifiedName();
/*   0*/      case 86:
/* 441*/        return parent.getFirstChild().getQualifiedName();
/*   0*/    } 
/* 445*/    String name = n.getFirstChild().getQualifiedName();
/* 446*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getNearestFunctionName(Node n) {
/* 466*/    if (!n.isFunction())
/* 467*/      return null; 
/* 470*/    String name = getFunctionName(n);
/* 471*/    if (name != null)
/* 472*/      return name; 
/* 476*/    Node parent = n.getParent();
/* 477*/    switch (parent.getType()) {
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/      case 154:
/* 482*/        return parent.getString();
/*   0*/      case 39:
/* 484*/        return getStringValue(parent);
/*   0*/    } 
/* 487*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isImmutableValue(Node n) {
/*   0*/    String name;
/* 495*/    switch (n.getType()) {
/*   0*/      case 39:
/*   0*/      case 40:
/*   0*/      case 41:
/*   0*/      case 43:
/*   0*/      case 44:
/* 501*/        return true;
/*   0*/      case 26:
/* 503*/        return isImmutableValue(n.getFirstChild());
/*   0*/      case 29:
/*   0*/      case 122:
/* 506*/        return isImmutableValue(n.getFirstChild());
/*   0*/      case 38:
/* 508*/        name = n.getString();
/* 511*/        return ("undefined".equals(name) || "Infinity".equals(name) || "NaN".equals(name));
/*   0*/    } 
/* 516*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSymmetricOperation(Node n) {
/* 523*/    switch (n.getType()) {
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 23:
/*   0*/      case 45:
/*   0*/      case 46:
/* 530*/        return true;
/*   0*/    } 
/* 532*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isRelationalOperation(Node n) {
/* 540*/    switch (n.getType()) {
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/* 545*/        return true;
/*   0*/    } 
/* 547*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int getInverseOperator(int type) {
/* 555*/    switch (type) {
/*   0*/      case 16:
/* 557*/        return 14;
/*   0*/      case 14:
/* 559*/        return 16;
/*   0*/      case 17:
/* 561*/        return 15;
/*   0*/      case 15:
/* 563*/        return 17;
/*   0*/    } 
/* 565*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLiteralValue(Node n, boolean includeFunctions) {
/*   0*/    Node child;
/* 591*/    switch (n.getType()) {
/*   0*/      case 63:
/* 593*/        for (child = n.getFirstChild(); child != null; 
/* 594*/          child = child.getNext()) {
/* 595*/          if (!child.isEmpty() && !isLiteralValue(child, includeFunctions))
/* 596*/            return false; 
/*   0*/        } 
/* 599*/        return true;
/*   0*/      case 47:
/* 603*/        for (child = n.getFirstChild(); child != null; 
/* 604*/          child = child.getNext()) {
/* 605*/          if (!isLiteralValue(child, includeFunctions))
/* 606*/            return false; 
/*   0*/        } 
/* 609*/        return true;
/*   0*/      case 64:
/* 613*/        for (child = n.getFirstChild(); child != null; 
/* 614*/          child = child.getNext()) {
/* 615*/          if (!isLiteralValue(child.getFirstChild(), includeFunctions))
/* 616*/            return false; 
/*   0*/        } 
/* 619*/        return true;
/*   0*/      case 105:
/* 622*/        return (includeFunctions && !isFunctionDeclaration(n));
/*   0*/    } 
/* 625*/    return isImmutableValue(n);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isValidDefineValue(Node val, Set<String> defines) {
/* 636*/    switch (val.getType()) {
/*   0*/      case 39:
/*   0*/      case 40:
/*   0*/      case 43:
/*   0*/      case 44:
/* 641*/        return true;
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 21:
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 27:
/*   0*/      case 45:
/*   0*/      case 46:
/* 664*/        return (isValidDefineValue(val.getFirstChild(), defines) && isValidDefineValue(val.getLastChild(), defines));
/*   0*/      case 26:
/*   0*/      case 28:
/*   0*/      case 29:
/* 671*/        return isValidDefineValue(val.getFirstChild(), defines);
/*   0*/      case 33:
/*   0*/      case 38:
/* 676*/        if (val.isQualifiedName())
/* 677*/          return defines.contains(val.getQualifiedName()); 
/*   0*/        break;
/*   0*/    } 
/* 680*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isEmptyBlock(Node block) {
/* 689*/    if (!block.isBlock())
/* 690*/      return false; 
/* 693*/    for (Node n = block.getFirstChild(); n != null; n = n.getNext()) {
/* 694*/      if (!n.isEmpty())
/* 695*/        return false; 
/*   0*/    } 
/* 698*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSimpleOperator(Node n) {
/* 702*/    return isSimpleOperatorType(n.getType());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSimpleOperatorType(int type) {
/* 711*/    switch (type) {
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*   0*/      case 21:
/*   0*/      case 22:
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*   0*/      case 26:
/*   0*/      case 27:
/*   0*/      case 28:
/*   0*/      case 29:
/*   0*/      case 32:
/*   0*/      case 33:
/*   0*/      case 35:
/*   0*/      case 45:
/*   0*/      case 46:
/*   0*/      case 52:
/*   0*/      case 85:
/*   0*/      case 122:
/* 741*/        return true;
/*   0*/    } 
/* 744*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newExpr(Node child) {
/* 755*/    return IR.exprResult(child).srcref(child);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayEffectMutableState(Node n) {
/* 765*/    return mayEffectMutableState(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayEffectMutableState(Node n, AbstractCompiler compiler) {
/* 769*/    return checkForStateChangeHelper(n, true, compiler);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayHaveSideEffects(Node n) {
/* 776*/    return mayHaveSideEffects(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayHaveSideEffects(Node n, AbstractCompiler compiler) {
/* 780*/    return checkForStateChangeHelper(n, false, compiler);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean checkForStateChangeHelper(Node n, boolean checkForNewObjects, AbstractCompiler compiler) {
/* 793*/    switch (n.getType()) {
/*   0*/      case 39:
/*   0*/      case 40:
/*   0*/      case 41:
/*   0*/      case 42:
/*   0*/      case 43:
/*   0*/      case 44:
/*   0*/      case 51:
/*   0*/      case 77:
/*   0*/      case 83:
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/*   0*/      case 108:
/*   0*/      case 110:
/*   0*/      case 124:
/*   0*/      case 125:
/*   0*/      case 130:
/*   0*/      case 154:
/*   0*/        break;
/*   0*/      case 49:
/* 817*/        return true;
/*   0*/      case 64:
/* 820*/        if (checkForNewObjects)
/* 821*/          return true; 
/* 823*/        for (c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 824*/          if (checkForStateChangeHelper(c.getFirstChild(), checkForNewObjects, compiler))
/* 826*/            return true; 
/*   0*/        } 
/* 829*/        return false;
/*   0*/      case 47:
/*   0*/      case 63:
/* 833*/        if (checkForNewObjects)
/* 834*/          return true; 
/*   0*/        break;
/*   0*/      case 38:
/*   0*/      case 118:
/* 840*/        if (n.getFirstChild() != null)
/* 841*/          return true; 
/*   0*/        break;
/*   0*/      case 105:
/* 849*/        return (checkForNewObjects || !isFunctionExpression(n));
/*   0*/      case 30:
/* 852*/        if (checkForNewObjects)
/* 853*/          return true; 
/* 856*/        if (!constructorCallHasSideEffects(n))
/*   0*/          break; 
/* 861*/        return true;
/*   0*/      case 37:
/* 866*/        if (!functionCallHasSideEffects(n, compiler))
/*   0*/          break; 
/* 871*/        return true;
/*   0*/      default:
/* 874*/        if (isSimpleOperator(n))
/*   0*/          break; 
/* 878*/        if (isAssignmentOp(n)) {
/* 879*/          Node assignTarget = n.getFirstChild();
/* 880*/          if (assignTarget.isName())
/* 881*/            return true; 
/* 888*/          if (checkForStateChangeHelper(n.getFirstChild(), checkForNewObjects, compiler) || checkForStateChangeHelper(n.getLastChild(), checkForNewObjects, compiler))
/* 892*/            return true; 
/* 895*/          if (isGet(assignTarget)) {
/* 900*/            Node current = assignTarget.getFirstChild();
/* 901*/            if (evaluatesToLocalValue(current))
/* 902*/              return false; 
/* 910*/            while (isGet(current))
/* 911*/              current = current.getFirstChild(); 
/* 914*/            return !isLiteralValue(current, true);
/*   0*/          } 
/* 919*/          return !isLiteralValue(assignTarget, true);
/*   0*/        } 
/* 923*/        return true;
/*   0*/    } 
/* 926*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 927*/      if (checkForStateChangeHelper(c, checkForNewObjects, compiler))
/* 928*/        return true; 
/*   0*/    } 
/* 932*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean constructorCallHasSideEffects(Node callNode) {
/* 941*/    return constructorCallHasSideEffects(callNode, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean constructorCallHasSideEffects(Node callNode, AbstractCompiler compiler) {
/* 946*/    if (!callNode.isNew())
/* 947*/      throw new IllegalStateException("Expected NEW node, got " + Token.name(callNode.getType())); 
/* 951*/    if (callNode.isNoSideEffectsCall())
/* 952*/      return false; 
/* 955*/    Node nameNode = callNode.getFirstChild();
/* 956*/    if (nameNode.isName() && CONSTRUCTORS_WITHOUT_SIDE_EFFECTS.contains(nameNode.getString()))
/* 958*/      return false; 
/* 961*/    return true;
/*   0*/  }
/*   0*/  
/* 967*/  private static final Set<String> BUILTIN_FUNCTIONS_WITHOUT_SIDEEFFECTS = (Set<String>)ImmutableSet.of("Object", "Array", "String", "Number", "Boolean", "RegExp", (Object[])new String[] { "Error" });
/*   0*/  
/* 970*/  private static final Set<String> OBJECT_METHODS_WITHOUT_SIDEEFFECTS = (Set<String>)ImmutableSet.of("toString", "valueOf");
/*   0*/  
/* 972*/  private static final Set<String> REGEXP_METHODS = (Set<String>)ImmutableSet.of("test", "exec");
/*   0*/  
/* 974*/  private static final Set<String> STRING_REGEXP_METHODS = (Set<String>)ImmutableSet.of("match", "replace", "search", "split");
/*   0*/  
/*   0*/  static boolean functionCallHasSideEffects(Node callNode) {
/* 983*/    return functionCallHasSideEffects(callNode, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean functionCallHasSideEffects(Node callNode, @Nullable AbstractCompiler compiler) {
/* 995*/    if (!callNode.isCall())
/* 996*/      throw new IllegalStateException("Expected CALL node, got " + Token.name(callNode.getType())); 
/*1000*/    if (callNode.isNoSideEffectsCall())
/*1001*/      return false; 
/*1004*/    Node nameNode = callNode.getFirstChild();
/*1007*/    if (nameNode.isName()) {
/*1008*/      String name = nameNode.getString();
/*1009*/      if (BUILTIN_FUNCTIONS_WITHOUT_SIDEEFFECTS.contains(name))
/*1010*/        return false; 
/*1012*/    } else if (nameNode.isGetProp()) {
/*1013*/      if (callNode.hasOneChild() && OBJECT_METHODS_WITHOUT_SIDEEFFECTS.contains(nameNode.getLastChild().getString()))
/*1016*/        return false; 
/*1019*/      if (callNode.isOnlyModifiesThisCall() && evaluatesToLocalValue(nameNode.getFirstChild()))
/*1021*/        return false; 
/*1027*/      if (nameNode.getFirstChild().isName() && 
/*1028*/        "Math.floor".equals(nameNode.getQualifiedName()))
/*1029*/        return false; 
/*1033*/      if (compiler != null && !compiler.hasRegExpGlobalReferences()) {
/*1034*/        if (nameNode.getFirstChild().isRegExp() && REGEXP_METHODS.contains(nameNode.getLastChild().getString()))
/*1036*/          return false; 
/*1037*/        if (nameNode.getFirstChild().isString() && STRING_REGEXP_METHODS.contains(nameNode.getLastChild().getString())) {
/*1040*/          Node param = nameNode.getNext();
/*1041*/          if (param != null && (param.isString() || param.isRegExp()))
/*1043*/            return false; 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1049*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean callHasLocalResult(Node n) {
/*1056*/    Preconditions.checkState(n.isCall());
/*1057*/    return ((n.getSideEffectFlags() & 0x10) > 0);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean newHasLocalResult(Node n) {
/*1064*/    Preconditions.checkState(n.isNew());
/*1065*/    return n.isOnlyModifiesThisCall();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean nodeTypeMayHaveSideEffects(Node n) {
/*1076*/    return nodeTypeMayHaveSideEffects(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean nodeTypeMayHaveSideEffects(Node n, AbstractCompiler compiler) {
/*1080*/    if (isAssignmentOp(n))
/*1081*/      return true; 
/*1084*/    switch (n.getType()) {
/*   0*/      case 31:
/*   0*/      case 49:
/*   0*/      case 102:
/*   0*/      case 103:
/*1089*/        return true;
/*   0*/      case 37:
/*1091*/        return functionCallHasSideEffects(n, compiler);
/*   0*/      case 30:
/*1093*/        return constructorCallHasSideEffects(n, compiler);
/*   0*/      case 38:
/*1096*/        return n.hasChildren();
/*   0*/    } 
/*1098*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean canBeSideEffected(Node n) {
/*1107*/    Set<String> emptySet = Collections.emptySet();
/*1108*/    return canBeSideEffected(n, emptySet);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean canBeSideEffected(Node n, Set<String> knownConstants) {
/*1118*/    switch (n.getType()) {
/*   0*/      case 30:
/*   0*/      case 37:
/*1124*/        return true;
/*   0*/      case 38:
/*1127*/        return (!isConstantName(n) && !knownConstants.contains(n.getString()));
/*   0*/      case 33:
/*   0*/      case 35:
/*1133*/        return true;
/*   0*/      case 105:
/*1138*/        Preconditions.checkState(isFunctionExpression(n));
/*1139*/        return false;
/*   0*/    } 
/*1142*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/*1143*/      if (canBeSideEffected(c, knownConstants))
/*1144*/        return true; 
/*   0*/    } 
/*1148*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int precedence(int type) {
/*1169*/    switch (type) {
/*   0*/      case 85:
/*1170*/        return 0;
/*   0*/      case 86:
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
/*1182*/        return 1;
/*   0*/      case 98:
/*1183*/        return 2;
/*   0*/      case 100:
/*1184*/        return 3;
/*   0*/      case 101:
/*1185*/        return 4;
/*   0*/      case 9:
/*1186*/        return 5;
/*   0*/      case 10:
/*1187*/        return 6;
/*   0*/      case 11:
/*1188*/        return 7;
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 45:
/*   0*/      case 46:
/*1192*/        return 8;
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/*   0*/      case 51:
/*   0*/      case 52:
/*1198*/        return 9;
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/*1201*/        return 10;
/*   0*/      case 21:
/*   0*/      case 22:
/*1203*/        return 11;
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/*1206*/        return 12;
/*   0*/      case 26:
/*   0*/      case 27:
/*   0*/      case 28:
/*   0*/      case 29:
/*   0*/      case 30:
/*   0*/      case 31:
/*   0*/      case 32:
/*   0*/      case 102:
/*   0*/      case 103:
/*   0*/      case 122:
/*1216*/        return 13;
/*   0*/      case 33:
/*   0*/      case 35:
/*   0*/      case 37:
/*   0*/      case 38:
/*   0*/      case 39:
/*   0*/      case 40:
/*   0*/      case 41:
/*   0*/      case 42:
/*   0*/      case 43:
/*   0*/      case 44:
/*   0*/      case 47:
/*   0*/      case 63:
/*   0*/      case 64:
/*   0*/      case 105:
/*   0*/      case 124:
/*   0*/      case 154:
/*1235*/        return 15;
/*   0*/    } 
/*1237*/    throw new Error("Unknown precedence for " + Token.name(type) + " (type " + type + ")");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isUndefined(Node n) {
/*1244*/    switch (n.getType()) {
/*   0*/      case 122:
/*1246*/        return true;
/*   0*/      case 38:
/*1248*/        return n.getString().equals("undefined");
/*   0*/    } 
/*1250*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNullOrUndefined(Node n) {
/*1254*/    return (n.isNull() || isUndefined(n));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean allResultsMatch(Node n, Predicate<Node> p) {
/*1262*/    switch (n.getType()) {
/*   0*/      case 85:
/*   0*/      case 86:
/*1265*/        return allResultsMatch(n.getLastChild(), p);
/*   0*/      case 100:
/*   0*/      case 101:
/*1268*/        return (allResultsMatch(n.getFirstChild(), p) && allResultsMatch(n.getLastChild(), p));
/*   0*/      case 98:
/*1271*/        return (allResultsMatch(n.getFirstChild().getNext(), p) && allResultsMatch(n.getLastChild(), p));
/*   0*/    } 
/*1274*/    return p.apply(n);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean anyResultsMatch(Node n, Predicate<Node> p) {
/*1283*/    switch (n.getType()) {
/*   0*/      case 85:
/*   0*/      case 86:
/*1286*/        return anyResultsMatch(n.getLastChild(), p);
/*   0*/      case 100:
/*   0*/      case 101:
/*1289*/        return (anyResultsMatch(n.getFirstChild(), p) || anyResultsMatch(n.getLastChild(), p));
/*   0*/      case 98:
/*1292*/        return (anyResultsMatch(n.getFirstChild().getNext(), p) || anyResultsMatch(n.getLastChild(), p));
/*   0*/    } 
/*1295*/    return p.apply(n);
/*   0*/  }
/*   0*/  
/*   0*/  static class NumbericResultPredicate implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*1302*/      return NodeUtil.isNumericResultHelper(n);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*1306*/  static final NumbericResultPredicate NUMBERIC_RESULT_PREDICATE = new NumbericResultPredicate();
/*   0*/  
/*   0*/  static boolean isNumericResult(Node n) {
/*1313*/    return allResultsMatch(n, NUMBERIC_RESULT_PREDICATE);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNumericResultHelper(Node n) {
/*   0*/    String name;
/*1317*/    switch (n.getType()) {
/*   0*/      case 21:
/*1319*/        return (!mayBeString(n.getFirstChild()) && !mayBeString(n.getLastChild()));
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
/*   0*/      case 28:
/*   0*/      case 29:
/*   0*/      case 39:
/*   0*/      case 102:
/*   0*/      case 103:
/*1337*/        return true;
/*   0*/      case 38:
/*1339*/        name = n.getString();
/*1340*/        if (name.equals("NaN"))
/*1341*/          return true; 
/*1343*/        if (name.equals("Infinity"))
/*1344*/          return true; 
/*1346*/        return false;
/*   0*/    } 
/*1348*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static class BooleanResultPredicate implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*1355*/      return NodeUtil.isBooleanResultHelper(n);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*1359*/  static final BooleanResultPredicate BOOLEAN_RESULT_PREDICATE = new BooleanResultPredicate();
/*   0*/  
/*   0*/  static boolean isBooleanResult(Node n) {
/*1366*/    return allResultsMatch(n, BOOLEAN_RESULT_PREDICATE);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isBooleanResultHelper(Node n) {
/*1370*/    switch (n.getType()) {
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/*   0*/      case 26:
/*   0*/      case 31:
/*   0*/      case 43:
/*   0*/      case 44:
/*   0*/      case 45:
/*   0*/      case 46:
/*   0*/      case 51:
/*   0*/      case 52:
/*1390*/        return true;
/*   0*/    } 
/*1392*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static class MayBeStringResultPredicate implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*1401*/      return NodeUtil.mayBeStringHelper(n);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*1405*/  static final MayBeStringResultPredicate MAY_BE_STRING_PREDICATE = new MayBeStringResultPredicate();
/*   0*/  
/*   0*/  static boolean mayBeString(Node n) {
/*1412*/    return mayBeString(n, true);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayBeString(Node n, boolean recurse) {
/*1416*/    if (recurse)
/*1417*/      return anyResultsMatch(n, MAY_BE_STRING_PREDICATE); 
/*1419*/    return mayBeStringHelper(n);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayBeStringHelper(Node n) {
/*1424*/    return (!isNumericResult(n) && !isBooleanResult(n) && !isUndefined(n) && !n.isNull());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isAssociative(int type) {
/*1435*/    switch (type) {
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 23:
/*   0*/      case 100:
/*   0*/      case 101:
/*1442*/        return true;
/*   0*/    } 
/*1444*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isCommutative(int type) {
/*1456*/    switch (type) {
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 11:
/*   0*/      case 23:
/*1461*/        return true;
/*   0*/    } 
/*1463*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isAssignmentOp(Node n) {
/*1468*/    switch (n.getType()) {
/*   0*/      case 86:
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
/*1481*/        return true;
/*   0*/    } 
/*1483*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int getOpFromAssignmentOp(Node n) {
/*1487*/    switch (n.getType()) {
/*   0*/      case 87:
/*1489*/        return 9;
/*   0*/      case 88:
/*1491*/        return 10;
/*   0*/      case 89:
/*1493*/        return 11;
/*   0*/      case 90:
/*1495*/        return 18;
/*   0*/      case 91:
/*1497*/        return 19;
/*   0*/      case 92:
/*1499*/        return 20;
/*   0*/      case 93:
/*1501*/        return 21;
/*   0*/      case 94:
/*1503*/        return 22;
/*   0*/      case 95:
/*1505*/        return 23;
/*   0*/      case 96:
/*1507*/        return 24;
/*   0*/      case 97:
/*1509*/        return 25;
/*   0*/    } 
/*1511*/    throw new IllegalArgumentException("Not an assignment op:" + n);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsFunction(Node n) {
/*1519*/    return containsType(n, 105);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean referencesThis(Node n) {
/*1526*/    Node start = n.isFunction() ? n.getLastChild() : n;
/*1527*/    return containsType(start, 42, MATCH_NOT_FUNCTION);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isGet(Node n) {
/*1534*/    return (n.isGetProp() || n.isGetElem());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isVarDeclaration(Node n) {
/*1546*/    return (n.isName() && n.getParent().isVar());
/*   0*/  }
/*   0*/  
/*   0*/  static Node getAssignedValue(Node n) {
/*1554*/    Preconditions.checkState(n.isName());
/*1555*/    Node parent = n.getParent();
/*1556*/    if (parent.isVar())
/*1557*/      return n.getFirstChild(); 
/*1558*/    if (parent.isAssign() && parent.getFirstChild() == n)
/*1559*/      return n.getNext(); 
/*1561*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExprAssign(Node n) {
/*1573*/    return (n.isExprResult() && n.getFirstChild().isAssign());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExprCall(Node n) {
/*1585*/    return (n.isExprResult() && n.getFirstChild().isCall());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isForIn(Node n) {
/*1593*/    return (n.isFor() && n.getChildCount() == 3);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLoopStructure(Node n) {
/*1601*/    switch (n.getType()) {
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*1605*/        return true;
/*   0*/    } 
/*1607*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getLoopCodeBlock(Node n) {
/*1617*/    switch (n.getType()) {
/*   0*/      case 113:
/*   0*/      case 115:
/*1620*/        return n.getLastChild();
/*   0*/      case 114:
/*1622*/        return n.getFirstChild();
/*   0*/    } 
/*1624*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isWithinLoop(Node n) {
/*1633*/    for (Node parent : (Iterable<Node>)n.getAncestors()) {
/*1634*/      if (isLoopStructure(parent))
/*1635*/        return true; 
/*1638*/      if (parent.isFunction())
/*   0*/        break; 
/*   0*/    } 
/*1642*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isControlStructure(Node n) {
/*1649*/    switch (n.getType()) {
/*   0*/      case 77:
/*   0*/      case 108:
/*   0*/      case 110:
/*   0*/      case 111:
/*   0*/      case 112:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*   0*/      case 119:
/*   0*/      case 120:
/*   0*/      case 126:
/*1661*/        return true;
/*   0*/    } 
/*1663*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isControlStructureCodeBlock(Node parent, Node n) {
/*1672*/    switch (parent.getType()) {
/*   0*/      case 113:
/*   0*/      case 115:
/*   0*/      case 119:
/*   0*/      case 126:
/*1677*/        return (parent.getLastChild() == n);
/*   0*/      case 114:
/*1679*/        return (parent.getFirstChild() == n);
/*   0*/      case 108:
/*1681*/        return (parent.getFirstChild() != n);
/*   0*/      case 77:
/*1683*/        return (parent.getFirstChild() == n || parent.getLastChild() == n);
/*   0*/      case 120:
/*1685*/        return (parent.getLastChild() == n);
/*   0*/      case 110:
/*   0*/      case 111:
/*1688*/        return (parent.getFirstChild() != n);
/*   0*/      case 112:
/*1690*/        return true;
/*   0*/    } 
/*1692*/    Preconditions.checkState(isControlStructure(parent));
/*1693*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getConditionExpression(Node n) {
/*1703*/    switch (n.getType()) {
/*   0*/      case 108:
/*   0*/      case 113:
/*1706*/        return n.getFirstChild();
/*   0*/      case 114:
/*1708*/        return n.getLastChild();
/*   0*/      case 115:
/*1710*/        switch (n.getChildCount()) {
/*   0*/          case 3:
/*1712*/            return null;
/*   0*/          case 4:
/*1714*/            return n.getFirstChild().getNext();
/*   0*/        } 
/*1716*/        throw new IllegalArgumentException("malformed 'for' statement " + n);
/*   0*/      case 111:
/*1718*/        return null;
/*   0*/    } 
/*1720*/    throw new IllegalArgumentException(n + " does not have a condition.");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isStatementBlock(Node n) {
/*1727*/    return (n.isScript() || n.isBlock());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isStatement(Node n) {
/*1734*/    return isStatementParent(n.getParent());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isStatementParent(Node parent) {
/*1741*/    Preconditions.checkState((parent != null));
/*1742*/    switch (parent.getType()) {
/*   0*/      case 125:
/*   0*/      case 126:
/*   0*/      case 132:
/*1746*/        return true;
/*   0*/    } 
/*1748*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSwitchCase(Node n) {
/*1754*/    return (n.isCase() || n.isDefaultCase());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isReferenceName(Node n) {
/*1762*/    return (n.isName() && !n.getString().isEmpty());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isTryFinallyNode(Node parent, Node child) {
/*1767*/    return (parent.isTry() && parent.getChildCount() == 3 && child == parent.getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isTryCatchNodeContainer(Node n) {
/*1773*/    Node parent = n.getParent();
/*1774*/    return (parent.isTry() && parent.getFirstChild().getNext() == n);
/*   0*/  }
/*   0*/  
/*   0*/  static void removeChild(Node parent, Node node) {
/*1780*/    if (isTryFinallyNode(parent, node)) {
/*1781*/      if (hasCatchHandler(getCatchBlock(parent))) {
/*1783*/        parent.removeChild(node);
/*   0*/      } else {
/*1786*/        node.detachChildren();
/*   0*/      } 
/*1788*/    } else if (node.isCatch()) {
/*1790*/      Node tryNode = node.getParent().getParent();
/*1791*/      Preconditions.checkState(hasFinally(tryNode));
/*1792*/      node.detachFromParent();
/*1793*/    } else if (isTryCatchNodeContainer(node)) {
/*1796*/      Node tryNode = node.getParent();
/*1797*/      Preconditions.checkState(hasFinally(tryNode));
/*1798*/      node.detachChildren();
/*1799*/    } else if (node.isBlock()) {
/*1802*/      node.detachChildren();
/*1803*/    } else if (isStatementBlock(parent) || isSwitchCase(node)) {
/*1806*/      parent.removeChild(node);
/*1807*/    } else if (parent.isVar()) {
/*1808*/      if (parent.hasMoreThanOneChild()) {
/*1809*/        parent.removeChild(node);
/*   0*/      } else {
/*1812*/        parent.removeChild(node);
/*1814*/        removeChild(parent.getParent(), parent);
/*   0*/      } 
/*1816*/    } else if (parent.isLabel() && node == parent.getLastChild()) {
/*1819*/      parent.removeChild(node);
/*1821*/      removeChild(parent.getParent(), parent);
/*1822*/    } else if (parent.isFor() && parent.getChildCount() == 4) {
/*1827*/      parent.replaceChild(node, IR.empty());
/*   0*/    } else {
/*1829*/      throw new IllegalStateException("Invalid attempt to remove node: " + node.toString() + " of " + parent.toString());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static void maybeAddFinally(Node tryNode) {
/*1838*/    Preconditions.checkState(tryNode.isTry());
/*1839*/    if (!hasFinally(tryNode))
/*1840*/      tryNode.addChildrenToBack(IR.block().srcref(tryNode)); 
/*   0*/  }
/*   0*/  
/*   0*/  static boolean tryMergeBlock(Node block) {
/*1849*/    Preconditions.checkState(block.isBlock());
/*1850*/    Node parent = block.getParent();
/*1853*/    if (isStatementBlock(parent)) {
/*1854*/      Node previous = block;
/*1855*/      while (block.hasChildren()) {
/*1856*/        Node child = block.removeFirstChild();
/*1857*/        parent.addChildAfter(child, previous);
/*1858*/        previous = child;
/*   0*/      } 
/*1860*/      parent.removeChild(block);
/*1861*/      return true;
/*   0*/    } 
/*1863*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isCallOrNew(Node node) {
/*1872*/    return (node.isCall() || node.isNew());
/*   0*/  }
/*   0*/  
/*   0*/  static Node getFunctionBody(Node fn) {
/*1879*/    Preconditions.checkArgument(fn.isFunction());
/*1880*/    return fn.getLastChild();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionDeclaration(Node n) {
/*1889*/    return (n.isFunction() && isStatement(n));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isHoistedFunctionDeclaration(Node n) {
/*1898*/    return (isFunctionDeclaration(n) && (n.getParent().isScript() || n.getParent().getParent().isFunction()));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionExpression(Node n) {
/*1927*/    return (n.isFunction() && !isStatement(n));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isBleedingFunctionName(Node n) {
/*1935*/    return (n.isName() && !n.getString().isEmpty() && isFunctionExpression(n.getParent()));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isEmptyFunctionExpression(Node node) {
/*1946*/    return (isFunctionExpression(node) && isEmptyBlock(node.getLastChild()));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isVarArgsFunction(Node function) {
/*1955*/    Preconditions.checkArgument(function.isFunction());
/*1956*/    return isNameReferenced(function.getLastChild(), "arguments", MATCH_NOT_FUNCTION);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isObjectCallMethod(Node callNode, String methodName) {
/*1968*/    if (callNode.isCall()) {
/*1969*/      Node functionIndentifyingExpression = callNode.getFirstChild();
/*1970*/      if (isGet(functionIndentifyingExpression)) {
/*1971*/        Node last = functionIndentifyingExpression.getLastChild();
/*1972*/        if (last != null && last.isString()) {
/*1973*/          String propName = last.getString();
/*1974*/          return propName.equals(methodName);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1978*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionObjectCall(Node callNode) {
/*1988*/    return isObjectCallMethod(callNode, "call");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionObjectApply(Node callNode) {
/*1997*/    return isObjectCallMethod(callNode, "apply");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isVarOrSimpleAssignLhs(Node n, Node parent) {
/*2010*/    return ((parent.isAssign() && parent.getFirstChild() == n) || parent.isVar());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isLValue(Node n) {
/*2028*/    Preconditions.checkArgument((n.isName() || n.isGetProp() || n.isGetElem()));
/*2030*/    Node parent = n.getParent();
/*2031*/    if (parent == null)
/*2032*/      return false; 
/*2034*/    return ((isAssignmentOp(parent) && parent.getFirstChild() == n) || (isForIn(parent) && parent.getFirstChild() == n) || parent.isVar() || (parent.isFunction() && parent.getFirstChild() == n) || parent.isDec() || parent.isInc() || parent.isParamList() || parent.isCatch());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isObjectLitKey(Node node, Node parent) {
/*2052*/    switch (node.getType()) {
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/      case 154:
/*2056*/        return true;
/*   0*/    } 
/*2058*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static String getObjectLitKeyName(Node key) {
/*2067*/    switch (key.getType()) {
/*   0*/      case 147:
/*   0*/      case 148:
/*   0*/      case 154:
/*2071*/        return key.getString();
/*   0*/    } 
/*2073*/    throw new IllegalStateException("Unexpected node type: " + key);
/*   0*/  }
/*   0*/  
/*   0*/  static JSType getObjectLitKeyTypeFromValueType(Node key, JSType valueType) {
/*2081*/    if (valueType != null)
/*2082*/      switch (key.getType()) {
/*   0*/        case 147:
/*2085*/          if (valueType.isFunctionType()) {
/*2086*/            FunctionType fntype = valueType.toMaybeFunctionType();
/*2087*/            valueType = fntype.getReturnType();
/*   0*/            break;
/*   0*/          } 
/*2089*/          return null;
/*   0*/        case 148:
/*2093*/          if (valueType.isFunctionType()) {
/*2095*/            FunctionType fntype = valueType.toMaybeFunctionType();
/*2096*/            Node param = fntype.getParametersNode().getFirstChild();
/*2098*/            valueType = param.getJSType();
/*   0*/            break;
/*   0*/          } 
/*2100*/          return null;
/*   0*/      }  
/*2105*/    return valueType;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isGetOrSetKey(Node node) {
/*2115*/    switch (node.getType()) {
/*   0*/      case 147:
/*   0*/      case 148:
/*2118*/        return true;
/*   0*/    } 
/*2120*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static String opToStr(int operator) {
/*2132*/    switch (operator) {
/*   0*/      case 9:
/*2133*/        return "|";
/*   0*/      case 100:
/*2134*/        return "||";
/*   0*/      case 10:
/*2135*/        return "^";
/*   0*/      case 101:
/*2136*/        return "&&";
/*   0*/      case 11:
/*2137*/        return "&";
/*   0*/      case 45:
/*2138*/        return "===";
/*   0*/      case 12:
/*2139*/        return "==";
/*   0*/      case 26:
/*2140*/        return "!";
/*   0*/      case 13:
/*2141*/        return "!=";
/*   0*/      case 46:
/*2142*/        return "!==";
/*   0*/      case 18:
/*2143*/        return "<<";
/*   0*/      case 51:
/*2144*/        return "in";
/*   0*/      case 15:
/*2145*/        return "<=";
/*   0*/      case 14:
/*2146*/        return "<";
/*   0*/      case 20:
/*2147*/        return ">>>";
/*   0*/      case 19:
/*2148*/        return ">>";
/*   0*/      case 17:
/*2149*/        return ">=";
/*   0*/      case 16:
/*2150*/        return ">";
/*   0*/      case 23:
/*2151*/        return "*";
/*   0*/      case 24:
/*2152*/        return "/";
/*   0*/      case 25:
/*2153*/        return "%";
/*   0*/      case 27:
/*2154*/        return "~";
/*   0*/      case 21:
/*2155*/        return "+";
/*   0*/      case 22:
/*2156*/        return "-";
/*   0*/      case 28:
/*2157*/        return "+";
/*   0*/      case 29:
/*2158*/        return "-";
/*   0*/      case 86:
/*2159*/        return "=";
/*   0*/      case 87:
/*2160*/        return "|=";
/*   0*/      case 88:
/*2161*/        return "^=";
/*   0*/      case 89:
/*2162*/        return "&=";
/*   0*/      case 90:
/*2163*/        return "<<=";
/*   0*/      case 91:
/*2164*/        return ">>=";
/*   0*/      case 92:
/*2165*/        return ">>>=";
/*   0*/      case 93:
/*2166*/        return "+=";
/*   0*/      case 94:
/*2167*/        return "-=";
/*   0*/      case 95:
/*2168*/        return "*=";
/*   0*/      case 96:
/*2169*/        return "/=";
/*   0*/      case 97:
/*2170*/        return "%=";
/*   0*/      case 122:
/*2171*/        return "void";
/*   0*/      case 32:
/*2172*/        return "typeof";
/*   0*/      case 52:
/*2173*/        return "instanceof";
/*   0*/    } 
/*2174*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String opToStrNoFail(int operator) {
/*2187*/    String res = opToStr(operator);
/*2188*/    if (res == null)
/*2189*/      throw new Error("Unknown op " + operator + ": " + Token.name(operator)); 
/*2192*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsType(Node node, int type, Predicate<Node> traverseChildrenPred) {
/*2201*/    return has(node, new MatchNodeType(type), traverseChildrenPred);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsType(Node node, int type) {
/*2208*/    return containsType(node, type, Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  static void redeclareVarsInsideBranch(Node branch) {
/*2218*/    Collection<Node> vars = getVarsDeclaredInBranch(branch);
/*2219*/    if (vars.isEmpty())
/*   0*/      return; 
/*2223*/    Node parent = getAddingRoot(branch);
/*2224*/    for (Node nameNode : vars) {
/*2225*/      Node var = IR.var(IR.name(nameNode.getString()).srcref(nameNode)).srcref(nameNode);
/*2229*/      copyNameAnnotations(nameNode, var.getFirstChild());
/*2230*/      parent.addChildToFront(var);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static void copyNameAnnotations(Node source, Node destination) {
/*2240*/    if (source.getBooleanProp(43))
/*2241*/      destination.putBooleanProp(43, true); 
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getAddingRoot(Node n) {
/*2250*/    Node addingRoot = null;
/*2251*/    Node ancestor = n;
/*2252*/    while (null != (ancestor = ancestor.getParent())) {
/*2253*/      int type = ancestor.getType();
/*2254*/      if (type == 132) {
/*2255*/        addingRoot = ancestor;
/*   0*/        break;
/*   0*/      } 
/*2257*/      if (type == 105) {
/*2258*/        addingRoot = ancestor.getLastChild();
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*2264*/    Preconditions.checkState((addingRoot.isBlock() || addingRoot.isScript()));
/*2266*/    Preconditions.checkState((addingRoot.getFirstChild() == null || !addingRoot.getFirstChild().isScript()));
/*2268*/    return addingRoot;
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newQualifiedNameNode(CodingConvention convention, String name) {
/*2279*/    int endPos = name.indexOf('.');
/*2280*/    if (endPos == -1)
/*2281*/      return newName(convention, name); 
/*2283*/    Node node = newName(convention, name.substring(0, endPos));
/*   0*/    do {
/*2286*/      int startPos = endPos + 1;
/*2287*/      endPos = name.indexOf('.', startPos);
/*2288*/      String part = (endPos == -1) ? name.substring(startPos) : name.substring(startPos, endPos);
/*2291*/      Node propNode = IR.string(part);
/*2292*/      if (convention.isConstantKey(part))
/*2293*/        propNode.putBooleanProp(43, true); 
/*2295*/      node = IR.getprop(node, propNode);
/*2296*/    } while (endPos != -1);
/*2298*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newQualifiedNameNode(CodingConvention convention, String name, Node basisNode, String originalName) {
/*2317*/    Node node = newQualifiedNameNode(convention, name);
/*2318*/    setDebugInformation(node, basisNode, originalName);
/*2319*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getRootOfQualifiedName(Node qName) {
/*2326*/    Node current = qName;
/*2327*/    for (;; current = current.getFirstChild()) {
/*2328*/      if (current.isName() || current.isThis())
/*2329*/        return current; 
/*2331*/      Preconditions.checkState(current.isGetProp());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static void setDebugInformation(Node node, Node basisNode, String originalName) {
/*2345*/    node.copyInformationFromForTree(basisNode);
/*2346*/    node.putProp(40, originalName);
/*   0*/  }
/*   0*/  
/*   0*/  private static Node newName(CodingConvention convention, String name) {
/*2351*/    Node nameNode = IR.name(name);
/*2352*/    if (convention.isConstant(name))
/*2353*/      nameNode.putBooleanProp(43, true); 
/*2355*/    return nameNode;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newName(CodingConvention convention, String name, Node srcref) {
/*2369*/    return newName(convention, name).srcref(srcref);
/*   0*/  }
/*   0*/  
/*   0*/  static Node newName(CodingConvention convention, String name, Node basisNode, String originalName) {
/*2388*/    Node nameNode = newName(convention, name, basisNode);
/*2389*/    nameNode.putProp(40, originalName);
/*2390*/    return nameNode;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLatin(String s) {
/*2407*/    int len = s.length();
/*2408*/    for (int index = 0; index < len; index++) {
/*2409*/      char c = s.charAt(index);
/*2410*/      if (c > '\u007F')
/*2411*/        return false; 
/*   0*/    } 
/*2414*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isValidSimpleName(String name) {
/*2421*/    return (TokenStream.isJSIdentifier(name) && !TokenStream.isKeyword(name) && isLatin(name));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isValidQualifiedName(String name) {
/*2438*/    if (name.endsWith(".") || name.startsWith("."))
/*2439*/      return false; 
/*2441*/    String[] parts = name.split("\\.");
/*2442*/    for (String part : parts) {
/*2443*/      if (!isValidSimpleName(part))
/*2444*/        return false; 
/*   0*/    } 
/*2447*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isValidPropertyName(String name) {
/*2455*/    return isValidSimpleName(name);
/*   0*/  }
/*   0*/  
/*   0*/  private static class VarCollector implements Visitor {
/*2459*/    final Map<String, Node> vars = Maps.newLinkedHashMap();
/*   0*/    
/*   0*/    public void visit(Node n) {
/*2463*/      if (n.isName()) {
/*2464*/        Node parent = n.getParent();
/*2465*/        if (parent != null && parent.isVar()) {
/*2466*/          String name = n.getString();
/*2467*/          if (!this.vars.containsKey(name))
/*2468*/            this.vars.put(name, n); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private VarCollector() {}
/*   0*/  }
/*   0*/  
/*   0*/  static Collection<Node> getVarsDeclaredInBranch(Node root) {
/*2479*/    VarCollector collector = new VarCollector();
/*2480*/    visitPreOrder(root, collector, MATCH_NOT_FUNCTION);
/*2484*/    return collector.vars.values();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isPrototypePropertyDeclaration(Node n) {
/*2492*/    if (!isExprAssign(n))
/*2493*/      return false; 
/*2495*/    return isPrototypeProperty(n.getFirstChild().getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isPrototypeProperty(Node n) {
/*2502*/    String lhsString = n.getQualifiedName();
/*2503*/    if (lhsString == null)
/*2504*/      return false; 
/*2506*/    int prototypeIdx = lhsString.indexOf(".prototype.");
/*2507*/    return (prototypeIdx != -1);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getPrototypeClassName(Node qName) {
/*2514*/    Node cur = qName;
/*2515*/    while (cur.isGetProp()) {
/*2516*/      if (cur.getLastChild().getString().equals("prototype"))
/*2517*/        return cur.getFirstChild(); 
/*2519*/      cur = cur.getFirstChild();
/*   0*/    } 
/*2522*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String getPrototypePropertyName(Node qName) {
/*2529*/    String qNameStr = qName.getQualifiedName();
/*2530*/    int prototypeIdx = qNameStr.lastIndexOf(".prototype.");
/*2531*/    int memberIndex = prototypeIdx + ".prototype".length() + 1;
/*2532*/    return qNameStr.substring(memberIndex);
/*   0*/  }
/*   0*/  
/*   0*/  static Node newUndefinedNode(Node srcReferenceNode) {
/*2540*/    Node node = IR.voidNode(IR.number(0.0D));
/*2541*/    if (srcReferenceNode != null)
/*2542*/      node.copyInformationFromForTree(srcReferenceNode); 
/*2544*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newVarNode(String name, Node value) {
/*2551*/    Node nodeName = IR.name(name);
/*2552*/    if (value != null) {
/*2553*/      Preconditions.checkState((value.getNext() == null));
/*2554*/      nodeName.addChildToBack(value);
/*2555*/      nodeName.srcref(value);
/*   0*/    } 
/*2557*/    Node var = IR.var(nodeName).srcref(nodeName);
/*2559*/    return var;
/*   0*/  }
/*   0*/  
/*   0*/  private static class MatchNameNode implements Predicate<Node> {
/*   0*/    final String name;
/*   0*/    
/*   0*/    MatchNameNode(String name) {
/*2569*/      this.name = name;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean apply(Node n) {
/*2574*/      return (n.isName() && n.getString().equals(this.name));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class MatchNodeType implements Predicate<Node> {
/*   0*/    final int type;
/*   0*/    
/*   0*/    MatchNodeType(int type) {
/*2585*/      this.type = type;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean apply(Node n) {
/*2590*/      return (n.getType() == this.type);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class MatchDeclaration implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*2601*/      return (NodeUtil.isFunctionDeclaration(n) || n.isVar());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class MatchNotFunction implements Predicate<Node> {
/*   0*/    private MatchNotFunction() {}
/*   0*/    
/*   0*/    public boolean apply(Node n) {
/*2611*/      return !n.isFunction();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*2615*/  static final Predicate<Node> MATCH_NOT_FUNCTION = new MatchNotFunction();
/*   0*/  
/*   0*/  static class MatchShallowStatement implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*2623*/      Node parent = n.getParent();
/*2624*/      return (n.isBlock() || (!n.isFunction() && (parent == null || NodeUtil.isControlStructure(parent) || NodeUtil.isStatementBlock(parent))));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static int getNodeTypeReferenceCount(Node node, int type, Predicate<Node> traverseChildrenPred) {
/*2636*/    return getCount(node, new MatchNodeType(type), traverseChildrenPred);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNameReferenced(Node node, String name, Predicate<Node> traverseChildrenPred) {
/*2645*/    return has(node, new MatchNameNode(name), traverseChildrenPred);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNameReferenced(Node node, String name) {
/*2652*/    return isNameReferenced(node, name, Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  static int getNameReferenceCount(Node node, String name) {
/*2659*/    return getCount(node, new MatchNameNode(name), Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean has(Node node, Predicate<Node> pred, Predicate<Node> traverseChildrenPred) {
/*2669*/    if (pred.apply(node))
/*2670*/      return true; 
/*2673*/    if (!traverseChildrenPred.apply(node))
/*2674*/      return false; 
/*2677*/    for (Node c = node.getFirstChild(); c != null; c = c.getNext()) {
/*2678*/      if (has(c, pred, traverseChildrenPred))
/*2679*/        return true; 
/*   0*/    } 
/*2683*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int getCount(Node n, Predicate<Node> pred, Predicate<Node> traverseChildrenPred) {
/*2692*/    int total = 0;
/*2694*/    if (pred.apply(n))
/*2695*/      total++; 
/*2698*/    if (traverseChildrenPred.apply(n))
/*2699*/      for (Node c = n.getFirstChild(); c != null; c = c.getNext())
/*2700*/        total += getCount(c, pred, traverseChildrenPred);  
/*2704*/    return total;
/*   0*/  }
/*   0*/  
/*   0*/  static void visitPreOrder(Node node, Visitor visitor, Predicate<Node> traverseChildrenPred) {
/*2722*/    visitor.visit(node);
/*2724*/    if (traverseChildrenPred.apply(node))
/*2725*/      for (Node c = node.getFirstChild(); c != null; c = c.getNext())
/*2726*/        visitPreOrder(c, visitor, traverseChildrenPred);  
/*   0*/  }
/*   0*/  
/*   0*/  static void visitPostOrder(Node node, Visitor visitor, Predicate<Node> traverseChildrenPred) {
/*2738*/    if (traverseChildrenPred.apply(node))
/*2739*/      for (Node c = node.getFirstChild(); c != null; c = c.getNext())
/*2740*/        visitPostOrder(c, visitor, traverseChildrenPred);  
/*2744*/    visitor.visit(node);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean hasFinally(Node n) {
/*2751*/    Preconditions.checkArgument(n.isTry());
/*2752*/    return (n.getChildCount() == 3);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getCatchBlock(Node n) {
/*2760*/    Preconditions.checkArgument(n.isTry());
/*2761*/    return n.getFirstChild().getNext();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean hasCatchHandler(Node n) {
/*2769*/    Preconditions.checkArgument(n.isBlock());
/*2770*/    return (n.hasChildren() && n.getFirstChild().isCatch());
/*   0*/  }
/*   0*/  
/*   0*/  public static Node getFunctionParameters(Node fnNode) {
/*2779*/    Preconditions.checkArgument(fnNode.isFunction());
/*2780*/    return fnNode.getFirstChild().getNext();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isConstantName(Node node) {
/*2801*/    return node.getBooleanProp(43);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isConstantByConvention(CodingConvention convention, Node node, Node parent) {
/*2807*/    String name = node.getString();
/*2808*/    if (parent.isGetProp() && node == parent.getLastChild())
/*2810*/      return convention.isConstantKey(name); 
/*2811*/    if (isObjectLitKey(node, parent))
/*2812*/      return convention.isConstantKey(name); 
/*2814*/    return convention.isConstant(name);
/*   0*/  }
/*   0*/  
/*   0*/  public static JSDocInfo getFunctionJSDocInfo(Node n) {
/*2822*/    Preconditions.checkState(n.isFunction());
/*2823*/    JSDocInfo fnInfo = n.getJSDocInfo();
/*2824*/    if (fnInfo == null && isFunctionExpression(n)) {
/*2826*/      Node parent = n.getParent();
/*2827*/      if (parent.isAssign()) {
/*2829*/        fnInfo = parent.getJSDocInfo();
/*2830*/      } else if (parent.isName()) {
/*2832*/        fnInfo = parent.getParent().getJSDocInfo();
/*   0*/      } 
/*   0*/    } 
/*2835*/    return fnInfo;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getSourceName(Node n) {
/*2843*/    String sourceName = null;
/*2844*/    while (sourceName == null && n != null) {
/*2845*/      sourceName = n.getSourceFileName();
/*2846*/      n = n.getParent();
/*   0*/    } 
/*2848*/    return sourceName;
/*   0*/  }
/*   0*/  
/*   0*/  public static StaticSourceFile getSourceFile(Node n) {
/*2856*/    StaticSourceFile sourceName = null;
/*2857*/    while (sourceName == null && n != null) {
/*2858*/      sourceName = n.getStaticSourceFile();
/*2859*/      n = n.getParent();
/*   0*/    } 
/*2861*/    return sourceName;
/*   0*/  }
/*   0*/  
/*   0*/  public static InputId getInputId(Node n) {
/*2869*/    while (n != null && !n.isScript())
/*2870*/      n = n.getParent(); 
/*2873*/    return (n != null && n.isScript()) ? n.getInputId() : null;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newCallNode(Node callTarget, Node... parameters) {
/*2880*/    boolean isFreeCall = !isGet(callTarget);
/*2881*/    Node call = IR.call(callTarget, new Node[0]);
/*2882*/    call.putBooleanProp(50, isFreeCall);
/*2883*/    for (Node parameter : parameters)
/*2884*/      call.addChildToBack(parameter); 
/*2886*/    return call;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean evaluatesToLocalValue(Node value) {
/*2894*/    return evaluatesToLocalValue(value, Predicates.alwaysFalse());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean evaluatesToLocalValue(Node value, Predicate<Node> locals) {
/*2903*/    switch (value.getType()) {
/*   0*/      case 86:
/*2908*/        return (isImmutableValue(value.getLastChild()) || (locals.apply(value) && evaluatesToLocalValue(value.getLastChild(), locals)));
/*   0*/      case 85:
/*2912*/        return evaluatesToLocalValue(value.getLastChild(), locals);
/*   0*/      case 100:
/*   0*/      case 101:
/*2915*/        return (evaluatesToLocalValue(value.getFirstChild(), locals) && evaluatesToLocalValue(value.getLastChild(), locals));
/*   0*/      case 98:
/*2918*/        return (evaluatesToLocalValue(value.getFirstChild().getNext(), locals) && evaluatesToLocalValue(value.getLastChild(), locals));
/*   0*/      case 102:
/*   0*/      case 103:
/*2922*/        if (value.getBooleanProp(32))
/*2923*/          return evaluatesToLocalValue(value.getFirstChild(), locals); 
/*2925*/        return true;
/*   0*/      case 42:
/*2928*/        return locals.apply(value);
/*   0*/      case 38:
/*2930*/        return (isImmutableValue(value) || locals.apply(value));
/*   0*/      case 33:
/*   0*/      case 35:
/*2934*/        return locals.apply(value);
/*   0*/      case 37:
/*2936*/        return (callHasLocalResult(value) || isToStringMethodCall(value) || locals.apply(value));
/*   0*/      case 30:
/*2940*/        return (newHasLocalResult(value) || locals.apply(value));
/*   0*/      case 47:
/*   0*/      case 63:
/*   0*/      case 64:
/*   0*/      case 105:
/*2947*/        return true;
/*   0*/      case 31:
/*   0*/      case 51:
/*2951*/        return true;
/*   0*/    } 
/*2956*/    if (isAssignmentOp(value) || isSimpleOperator(value) || isImmutableValue(value))
/*2959*/      return true; 
/*2962*/    throw new IllegalStateException("Unexpected expression node" + value + "\n parent:" + value.getParent());
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getNthSibling(Node first, int index) {
/*2974*/    Node sibling = first;
/*2975*/    while (index != 0 && sibling != null) {
/*2976*/      sibling = sibling.getNext();
/*2977*/      index--;
/*   0*/    } 
/*2979*/    return sibling;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getArgumentForFunction(Node function, int index) {
/*2987*/    Preconditions.checkState(function.isFunction());
/*2988*/    return getNthSibling(function.getFirstChild().getNext().getFirstChild(), index);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getArgumentForCallOrNew(Node call, int index) {
/*2997*/    Preconditions.checkState(isCallOrNew(call));
/*2998*/    return getNthSibling(call.getFirstChild().getNext(), index);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isToStringMethodCall(Node call) {
/*3003*/    Node getNode = call.getFirstChild();
/*3004*/    if (isGet(getNode)) {
/*3005*/      Node propNode = getNode.getLastChild();
/*3006*/      return (propNode.isString() && "toString".equals(propNode.getString()));
/*   0*/    } 
/*3008*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static JSDocInfo getBestJSDocInfo(Node n) {
/*3013*/    JSDocInfo info = n.getJSDocInfo();
/*3014*/    if (info == null) {
/*3015*/      Node parent = n.getParent();
/*3016*/      if (parent == null)
/*3017*/        return null; 
/*3020*/      if (parent.isName())
/*3021*/        return getBestJSDocInfo(parent); 
/*3022*/      if (parent.isAssign())
/*3023*/        return parent.getJSDocInfo(); 
/*3024*/      if (isObjectLitKey(parent, parent.getParent()))
/*3025*/        return parent.getJSDocInfo(); 
/*3026*/      if (parent.isFunction())
/*3027*/        return parent.getJSDocInfo(); 
/*3028*/      if (parent.isVar() && parent.hasOneChild())
/*3029*/        return parent.getJSDocInfo(); 
/*3030*/      if ((parent.isHook() && parent.getFirstChild() != n) || parent.isOr() || parent.isAnd() || (parent.isComma() && parent.getFirstChild() != n))
/*3034*/        return getBestJSDocInfo(parent); 
/*   0*/    } 
/*3037*/    return info;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getBestLValue(Node n) {
/*3042*/    Node parent = n.getParent();
/*3043*/    boolean isFunctionDeclaration = isFunctionDeclaration(n);
/*3044*/    if (isFunctionDeclaration)
/*3045*/      return n.getFirstChild(); 
/*3046*/    if (parent.isName())
/*3047*/      return parent; 
/*3048*/    if (parent.isAssign())
/*3049*/      return parent.getFirstChild(); 
/*3050*/    if (isObjectLitKey(parent, parent.getParent()))
/*3051*/      return parent; 
/*3052*/    if ((parent.isHook() && parent.getFirstChild() != n) || parent.isOr() || parent.isAnd() || (parent.isComma() && parent.getFirstChild() != n))
/*3057*/      return getBestLValue(parent); 
/*3059*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getRValueOfLValue(Node n) {
/*3064*/    Node parent = n.getParent();
/*3065*/    switch (parent.getType()) {
/*   0*/      case 86:
/*3067*/        return n.getNext();
/*   0*/      case 118:
/*3069*/        return n.getFirstChild();
/*   0*/      case 105:
/*3071*/        return parent;
/*   0*/    } 
/*3073*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getBestLValueOwner(@Nullable Node lValue) {
/*3078*/    if (lValue == null || lValue.getParent() == null)
/*3079*/      return null; 
/*3081*/    if (isObjectLitKey(lValue, lValue.getParent()))
/*3082*/      return getBestLValue(lValue.getParent()); 
/*3083*/    if (isGet(lValue))
/*3084*/      return lValue.getFirstChild(); 
/*3087*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String getBestLValueName(@Nullable Node lValue) {
/*3092*/    if (lValue == null || lValue.getParent() == null)
/*3093*/      return null; 
/*3095*/    if (isObjectLitKey(lValue, lValue.getParent())) {
/*3096*/      Node owner = getBestLValue(lValue.getParent());
/*3097*/      if (owner != null) {
/*3098*/        String ownerName = getBestLValueName(owner);
/*3099*/        if (ownerName != null)
/*3100*/          return ownerName + "." + getObjectLitKeyName(lValue); 
/*   0*/      } 
/*3103*/      return null;
/*   0*/    } 
/*3105*/    return lValue.getQualifiedName();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExpressionResultUsed(Node expr) {
/*   0*/    Node gramps;
/*3113*/    Node parent = expr.getParent();
/*3114*/    switch (parent.getType()) {
/*   0*/      case 125:
/*   0*/      case 130:
/*3117*/        return false;
/*   0*/      case 98:
/*   0*/      case 100:
/*   0*/      case 101:
/*3121*/        return (expr == parent.getFirstChild()) ? true : isExpressionResultUsed(parent);
/*   0*/      case 85:
/*3124*/        gramps = parent.getParent();
/*3125*/        if (gramps.isCall() && parent == gramps.getFirstChild())
/*3131*/          if (expr == parent.getFirstChild() && parent.getChildCount() == 2 && expr.getNext().isName() && "eval".equals(expr.getNext().getString()))
/*3135*/            return true;  
/*3139*/        return (expr == parent.getFirstChild()) ? false : isExpressionResultUsed(parent);
/*   0*/      case 115:
/*3142*/        if (!isForIn(parent))
/*3145*/          return (parent.getChildAtIndex(1) == expr); 
/*   0*/        break;
/*   0*/    } 
/*3149*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExecutedExactlyOnce(Node n) {
/*   0*/    do {
/*3159*/      Node parent = n.getParent();
/*3160*/      switch (parent.getType()) {
/*   0*/        case 98:
/*   0*/        case 100:
/*   0*/        case 101:
/*   0*/        case 108:
/*3165*/          if (parent.getFirstChild() != n)
/*3166*/            return false; 
/*   0*/          break;
/*   0*/        case 115:
/*3171*/          if (isForIn(parent)) {
/*3172*/            if (parent.getChildAtIndex(1) != n)
/*3173*/              return false; 
/*   0*/            break;
/*   0*/          } 
/*3176*/          if (parent.getFirstChild() != n)
/*3177*/            return false; 
/*   0*/          break;
/*   0*/        case 113:
/*   0*/        case 114:
/*3184*/          return false;
/*   0*/        case 77:
/*3187*/          if (!hasFinally(parent) || parent.getLastChild() != n)
/*3188*/            return false; 
/*   0*/          break;
/*   0*/        case 111:
/*   0*/        case 112:
/*3193*/          return false;
/*   0*/        case 105:
/*   0*/        case 132:
/*   0*/          break;
/*   0*/      } 
/*3199*/    } while ((n = n.getParent()) != null);
/*3200*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static Node booleanNode(boolean value) {
/*3207*/    return value ? IR.trueNode() : IR.falseNode();
/*   0*/  }
/*   0*/  
/*   0*/  static Node numberNode(double value, Node srcref) {
/*   0*/    Node result;
/*3215*/    if (Double.isNaN(value)) {
/*3216*/      result = IR.name("NaN");
/*3217*/    } else if (value == Double.POSITIVE_INFINITY) {
/*3218*/      result = IR.name("Infinity");
/*3219*/    } else if (value == Double.NEGATIVE_INFINITY) {
/*3220*/      result = IR.neg(IR.name("Infinity"));
/*   0*/    } else {
/*3222*/      result = IR.number(value);
/*   0*/    } 
/*3224*/    if (srcref != null)
/*3225*/      result.srcrefTree(srcref); 
/*3227*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  static interface Visitor {
/*   0*/    void visit(Node param1Node);
/*   0*/  }
/*   0*/}
