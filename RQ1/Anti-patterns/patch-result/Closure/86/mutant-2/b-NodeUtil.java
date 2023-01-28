/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.base.Predicates;
/*   0*/import com.google.common.collect.ImmutableSet;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.Token;
/*   0*/import com.google.javascript.rhino.TokenStream;
/*   0*/import com.google.javascript.rhino.jstype.TernaryValue;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import javax.annotation.Nullable;
/*   0*/
/*   0*/public final class NodeUtil {
/*   0*/  static final String JSC_PROPERTY_NAME_FN = "JSCompiler_renameProperty";
/*   0*/  
/*  52*/  private static final Set<String> CONSTRUCTORS_WITHOUT_SIDE_EFFECTS = new HashSet<String>(Arrays.asList(new String[] { "Array", "Date", "Error", "Object", "RegExp", "XMLHttpRequest" }));
/*   0*/  
/*   0*/  static TernaryValue getExpressionBooleanValue(Node n) {
/*   0*/    TernaryValue value, lhs, trueValue, rhs, falseValue;
/*  71*/    switch (n.getType()) {
/*   0*/      case 85:
/*   0*/      case 86:
/*  75*/        return getExpressionBooleanValue(n.getLastChild());
/*   0*/      case 26:
/*  77*/        value = getExpressionBooleanValue(n.getLastChild());
/*  78*/        return value.not();
/*   0*/      case 101:
/*  80*/        lhs = getExpressionBooleanValue(n.getFirstChild());
/*  81*/        rhs = getExpressionBooleanValue(n.getLastChild());
/*  82*/        return lhs.and(rhs);
/*   0*/      case 100:
/*  85*/        lhs = getExpressionBooleanValue(n.getFirstChild());
/*  86*/        rhs = getExpressionBooleanValue(n.getLastChild());
/*  87*/        return lhs.or(rhs);
/*   0*/      case 98:
/*  90*/        trueValue = getExpressionBooleanValue(n.getFirstChild().getNext());
/*  92*/        falseValue = getExpressionBooleanValue(n.getLastChild());
/*  93*/        if (trueValue.equals(falseValue))
/*  94*/          return trueValue; 
/*  96*/        return TernaryValue.UNKNOWN;
/*   0*/    } 
/* 100*/    return getBooleanValue(n);
/*   0*/  }
/*   0*/  
/*   0*/  static TernaryValue getBooleanValue(Node n) {
/*   0*/    String name;
/* 109*/    switch (n.getType()) {
/*   0*/      case 40:
/* 111*/        return TernaryValue.forBoolean((n.getString().length() > 0));
/*   0*/      case 39:
/* 114*/        return TernaryValue.forBoolean((n.getDouble() != 0.0D));
/*   0*/      case 41:
/*   0*/      case 43:
/*   0*/      case 122:
/* 119*/        return TernaryValue.FALSE;
/*   0*/      case 38:
/* 122*/        name = n.getString();
/* 123*/        if ("undefined".equals(name) || "NaN".equals(name))
/* 127*/          return TernaryValue.FALSE; 
/* 128*/        if ("Infinity".equals(name))
/* 129*/          return TernaryValue.TRUE; 
/*   0*/        break;
/*   0*/      case 44:
/*   0*/      case 47:
/*   0*/      case 63:
/*   0*/      case 64:
/* 137*/        return TernaryValue.TRUE;
/*   0*/    } 
/* 140*/    return TernaryValue.UNKNOWN;
/*   0*/  }
/*   0*/  
/*   0*/  static String getStringValue(Node n) {
/*   0*/    String name;
/*   0*/    double value;
/*   0*/    long longValue;
/* 151*/    switch (n.getType()) {
/*   0*/      case 40:
/* 153*/        return n.getString();
/*   0*/      case 38:
/* 156*/        name = n.getString();
/* 157*/        if ("undefined".equals(name) || "Infinity".equals(name) || "NaN".equals(name))
/* 160*/          return name; 
/*   0*/        break;
/*   0*/      case 39:
/* 165*/        value = n.getDouble();
/* 166*/        longValue = (long)value;
/* 169*/        if (longValue == value)
/* 170*/          return Long.toString(longValue); 
/* 172*/        return Double.toString(n.getDouble());
/*   0*/      case 41:
/*   0*/      case 43:
/*   0*/      case 44:
/* 178*/        return Node.tokenToName(n.getType());
/*   0*/      case 122:
/* 181*/        return "undefined";
/*   0*/    } 
/* 183*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static Double getNumberValue(Node n) {
/*   0*/    String name;
/* 192*/    switch (n.getType()) {
/*   0*/      case 44:
/* 194*/        return 1.0D;
/*   0*/      case 41:
/*   0*/      case 43:
/* 197*/        return 0.0D;
/*   0*/      case 39:
/* 200*/        return n.getDouble();
/*   0*/      case 122:
/* 203*/        return Double.NaN;
/*   0*/      case 38:
/* 206*/        name = n.getString();
/* 207*/        if (name.equals("undefined"))
/* 208*/          return Double.NaN; 
/* 210*/        if (name.equals("NaN"))
/* 211*/          return Double.NaN; 
/* 213*/        if (name.equals("Infinity"))
/* 214*/          return Double.POSITIVE_INFINITY; 
/* 216*/        return null;
/*   0*/    } 
/* 218*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String getFunctionName(Node n) {
/* 237*/    Node parent = n.getParent();
/* 238*/    String name = n.getFirstChild().getString();
/* 239*/    switch (parent.getType()) {
/*   0*/      case 38:
/* 243*/        return parent.getString();
/*   0*/      case 86:
/* 248*/        return parent.getFirstChild().getQualifiedName();
/*   0*/    } 
/* 252*/    return (name != null && name.length() != 0) ? name : null;
/*   0*/  }
/*   0*/  
/*   0*/  static String getNearestFunctionName(Node n) {
/* 272*/    String name = getFunctionName(n);
/* 273*/    if (name != null)
/* 274*/      return name; 
/* 278*/    Node parent = n.getParent();
/* 279*/    switch (parent.getType()) {
/*   0*/      case 40:
/* 282*/        return getStringValue(parent);
/*   0*/    } 
/* 285*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isImmutableValue(Node n) {
/*   0*/    String name;
/* 293*/    switch (n.getType()) {
/*   0*/      case 39:
/*   0*/      case 40:
/*   0*/      case 41:
/*   0*/      case 43:
/*   0*/      case 44:
/* 299*/        return true;
/*   0*/      case 29:
/*   0*/      case 122:
/* 302*/        return isImmutableValue(n.getFirstChild());
/*   0*/      case 38:
/* 304*/        name = n.getString();
/* 307*/        return ("undefined".equals(name) || "Infinity".equals(name) || "NaN".equals(name));
/*   0*/    } 
/* 312*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLiteralValue(Node n, boolean includeFunctions) {
/*   0*/    Node child;
/* 338*/    switch (n.getType()) {
/*   0*/      case 47:
/*   0*/      case 63:
/* 342*/        for (child = n.getFirstChild(); child != null; 
/* 343*/          child = child.getNext()) {
/* 344*/          if (!isLiteralValue(child, includeFunctions))
/* 345*/            return false; 
/*   0*/        } 
/* 348*/        return true;
/*   0*/      case 64:
/* 352*/        for (child = n.getFirstChild(); child != null; 
/* 353*/          child = child.getNext()) {
/* 354*/          if (!isLiteralValue(child.getFirstChild(), includeFunctions))
/* 355*/            return false; 
/*   0*/        } 
/* 358*/        return true;
/*   0*/      case 105:
/* 361*/        return (includeFunctions && !isFunctionDeclaration(n));
/*   0*/    } 
/* 364*/    return isImmutableValue(n);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isValidDefineValue(Node val, Set<String> defines) {
/* 375*/    switch (val.getType()) {
/*   0*/      case 39:
/*   0*/      case 40:
/*   0*/      case 43:
/*   0*/      case 44:
/* 380*/        return true;
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
/* 403*/        return (isValidDefineValue(val.getFirstChild(), defines) && isValidDefineValue(val.getLastChild(), defines));
/*   0*/      case 26:
/*   0*/      case 28:
/*   0*/      case 29:
/* 410*/        return isValidDefineValue(val.getFirstChild(), defines);
/*   0*/      case 33:
/*   0*/      case 38:
/* 415*/        if (val.isQualifiedName())
/* 416*/          return defines.contains(val.getQualifiedName()); 
/*   0*/        break;
/*   0*/    } 
/* 419*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isEmptyBlock(Node block) {
/* 428*/    if (block.getType() != 125)
/* 429*/      return false; 
/* 432*/    for (Node n = block.getFirstChild(); n != null; n = n.getNext()) {
/* 433*/      if (n.getType() != 124)
/* 434*/        return false; 
/*   0*/    } 
/* 437*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSimpleOperator(Node n) {
/* 441*/    return isSimpleOperatorType(n.getType());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSimpleOperatorType(int type) {
/* 450*/    switch (type) {
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
/* 480*/        return true;
/*   0*/    } 
/* 483*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newExpr(Node child) {
/* 494*/    Node expr = new Node(130, child).copyInformationFrom(child);
/* 496*/    return expr;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayEffectMutableState(Node n) {
/* 506*/    return mayEffectMutableState(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayEffectMutableState(Node n, AbstractCompiler compiler) {
/* 510*/    return checkForStateChangeHelper(n, true, compiler);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayHaveSideEffects(Node n) {
/* 517*/    return mayHaveSideEffects(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean mayHaveSideEffects(Node n, AbstractCompiler compiler) {
/* 521*/    return checkForStateChangeHelper(n, false, compiler);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean checkForStateChangeHelper(Node n, boolean checkForNewObjects, AbstractCompiler compiler) {
/* 534*/    switch (n.getType()) {
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
/*   0*/        break;
/*   0*/      case 49:
/* 557*/        return true;
/*   0*/      case 64:
/* 560*/        if (checkForNewObjects)
/* 561*/          return true; 
/* 563*/        for (c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 564*/          if (checkForStateChangeHelper(c.getFirstChild(), checkForNewObjects, compiler))
/* 566*/            return true; 
/*   0*/        } 
/* 569*/        return false;
/*   0*/      case 47:
/*   0*/      case 63:
/* 573*/        if (checkForNewObjects)
/* 574*/          return true; 
/*   0*/        break;
/*   0*/      case 38:
/*   0*/      case 118:
/* 580*/        if (n.getFirstChild() != null)
/* 581*/          return true; 
/*   0*/        break;
/*   0*/      case 105:
/* 589*/        return (checkForNewObjects || !isFunctionExpression(n));
/*   0*/      case 30:
/* 592*/        if (checkForNewObjects)
/* 593*/          return true; 
/* 596*/        if (!constructorCallHasSideEffects(n))
/*   0*/          break; 
/* 601*/        return true;
/*   0*/      case 37:
/* 606*/        if (!functionCallHasSideEffects(n, compiler))
/*   0*/          break; 
/* 611*/        return true;
/*   0*/      default:
/* 614*/        if (isSimpleOperatorType(n.getType()))
/*   0*/          break; 
/* 618*/        if (isAssignmentOp(n)) {
/* 619*/          Node assignTarget = n.getFirstChild();
/* 620*/          if (isName(assignTarget))
/* 621*/            return true; 
/* 628*/          if (checkForStateChangeHelper(n.getFirstChild(), checkForNewObjects, compiler) || checkForStateChangeHelper(n.getLastChild(), checkForNewObjects, compiler))
/* 632*/            return true; 
/* 635*/          if (isGet(assignTarget)) {
/* 640*/            Node current = assignTarget.getFirstChild();
/* 641*/            if (evaluatesToLocalValue(current))
/* 642*/              return false; 
/* 650*/            while (isGet(current))
/* 651*/              current = current.getFirstChild(); 
/* 654*/            return !isLiteralValue(current, true);
/*   0*/          } 
/* 659*/          return !isLiteralValue(assignTarget, true);
/*   0*/        } 
/* 663*/        return true;
/*   0*/    } 
/* 666*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 667*/      if (checkForStateChangeHelper(c, checkForNewObjects, compiler))
/* 668*/        return true; 
/*   0*/    } 
/* 672*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean constructorCallHasSideEffects(Node callNode) {
/* 681*/    return constructorCallHasSideEffects(callNode, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean constructorCallHasSideEffects(Node callNode, AbstractCompiler compiler) {
/* 686*/    if (callNode.getType() != 30)
/* 687*/      throw new IllegalStateException("Expected NEW node, got " + Token.name(callNode.getType())); 
/* 691*/    if (callNode.isNoSideEffectsCall())
/* 692*/      return false; 
/* 695*/    Node nameNode = callNode.getFirstChild();
/* 696*/    if (nameNode.getType() == 38 && CONSTRUCTORS_WITHOUT_SIDE_EFFECTS.contains(nameNode.getString()))
/* 698*/      return false; 
/* 701*/    return true;
/*   0*/  }
/*   0*/  
/* 707*/  private static final Set<String> BUILTIN_FUNCTIONS_WITHOUT_SIDEEFFECTS = (Set<String>)ImmutableSet.of("Object", "Array", "String", "Number", "Boolean", "RegExp", (Object[])new String[] { "Error" });
/*   0*/  
/* 710*/  private static final Set<String> OBJECT_METHODS_WITHOUT_SIDEEFFECTS = (Set<String>)ImmutableSet.of("toString", "valueOf");
/*   0*/  
/* 712*/  private static final Set<String> REGEXP_METHODS = (Set<String>)ImmutableSet.of("test", "exec");
/*   0*/  
/* 714*/  private static final Set<String> STRING_REGEXP_METHODS = (Set<String>)ImmutableSet.of("match", "replace", "search", "split");
/*   0*/  
/*   0*/  static boolean functionCallHasSideEffects(Node callNode) {
/* 724*/    return functionCallHasSideEffects(callNode, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean functionCallHasSideEffects(Node callNode, @Nullable AbstractCompiler compiler) {
/* 736*/    if (callNode.getType() != 37)
/* 737*/      throw new IllegalStateException("Expected CALL node, got " + Token.name(callNode.getType())); 
/* 741*/    if (callNode.isNoSideEffectsCall())
/* 742*/      return false; 
/* 745*/    Node nameNode = callNode.getFirstChild();
/* 748*/    if (nameNode.getType() == 38) {
/* 749*/      String name = nameNode.getString();
/* 750*/      if (BUILTIN_FUNCTIONS_WITHOUT_SIDEEFFECTS.contains(name))
/* 751*/        return false; 
/* 753*/    } else if (nameNode.getType() == 33) {
/* 754*/      if (callNode.hasOneChild() && OBJECT_METHODS_WITHOUT_SIDEEFFECTS.contains(nameNode.getLastChild().getString()))
/* 757*/        return false; 
/* 760*/      if (callNode.isOnlyModifiesThisCall() && evaluatesToLocalValue(nameNode.getFirstChild()))
/* 762*/        return false; 
/* 766*/      if (nameNode.getFirstChild().getType() == 38) {
/* 767*/        String namespaceName = nameNode.getFirstChild().getString();
/* 768*/        if (namespaceName.equals("Math"))
/* 769*/          return false; 
/*   0*/      } 
/* 773*/      if (compiler != null && !compiler.hasRegExpGlobalReferences()) {
/* 774*/        if (nameNode.getFirstChild().getType() == 47 && REGEXP_METHODS.contains(nameNode.getLastChild().getString()))
/* 776*/          return false; 
/* 777*/        if (nameNode.getFirstChild().getType() == 40 && STRING_REGEXP_METHODS.contains(nameNode.getLastChild().getString())) {
/* 780*/          Node param = nameNode.getNext();
/* 781*/          if (param != null && (param.getType() == 40 || param.getType() == 47))
/* 784*/            return false; 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 789*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean callHasLocalResult(Node n) {
/* 796*/    Preconditions.checkState((n.getType() == 37));
/* 797*/    return ((n.getSideEffectFlags() & 0x10) > 0);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean nodeTypeMayHaveSideEffects(Node n) {
/* 808*/    return nodeTypeMayHaveSideEffects(n, null);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean nodeTypeMayHaveSideEffects(Node n, AbstractCompiler compiler) {
/* 812*/    if (isAssignmentOp(n))
/* 813*/      return true; 
/* 816*/    switch (n.getType()) {
/*   0*/      case 31:
/*   0*/      case 49:
/*   0*/      case 102:
/*   0*/      case 103:
/* 821*/        return true;
/*   0*/      case 37:
/* 823*/        return functionCallHasSideEffects(n, compiler);
/*   0*/      case 30:
/* 825*/        return constructorCallHasSideEffects(n, compiler);
/*   0*/      case 38:
/* 828*/        return n.hasChildren();
/*   0*/    } 
/* 830*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean canBeSideEffected(Node n) {
/* 839*/    Set<String> emptySet = Collections.emptySet();
/* 840*/    return canBeSideEffected(n, emptySet);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean canBeSideEffected(Node n, Set<String> knownConstants) {
/* 850*/    switch (n.getType()) {
/*   0*/      case 30:
/*   0*/      case 37:
/* 856*/        return true;
/*   0*/      case 38:
/* 859*/        return (!isConstantName(n) && !knownConstants.contains(n.getString()));
/*   0*/      case 33:
/*   0*/      case 35:
/* 865*/        return true;
/*   0*/      case 105:
/* 870*/        Preconditions.checkState(isFunctionExpression(n));
/* 871*/        return false;
/*   0*/    } 
/* 874*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
/* 875*/      if (canBeSideEffected(c, knownConstants))
/* 876*/        return true; 
/*   0*/    } 
/* 880*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int precedence(int type) {
/* 901*/    switch (type) {
/*   0*/      case 85:
/* 902*/        return 0;
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
/* 914*/        return 1;
/*   0*/      case 98:
/* 915*/        return 2;
/*   0*/      case 100:
/* 916*/        return 3;
/*   0*/      case 101:
/* 917*/        return 4;
/*   0*/      case 9:
/* 918*/        return 5;
/*   0*/      case 10:
/* 919*/        return 6;
/*   0*/      case 11:
/* 920*/        return 7;
/*   0*/      case 12:
/*   0*/      case 13:
/*   0*/      case 45:
/*   0*/      case 46:
/* 924*/        return 8;
/*   0*/      case 14:
/*   0*/      case 15:
/*   0*/      case 16:
/*   0*/      case 17:
/*   0*/      case 51:
/*   0*/      case 52:
/* 930*/        return 9;
/*   0*/      case 18:
/*   0*/      case 19:
/*   0*/      case 20:
/* 933*/        return 10;
/*   0*/      case 21:
/*   0*/      case 22:
/* 935*/        return 11;
/*   0*/      case 23:
/*   0*/      case 24:
/*   0*/      case 25:
/* 938*/        return 12;
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
/* 948*/        return 13;
/*   0*/      case 4:
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
/*   0*/      case 65:
/*   0*/      case 83:
/*   0*/      case 105:
/*   0*/      case 108:
/*   0*/      case 124:
/* 969*/        return 15;
/*   0*/    } 
/* 971*/    throw new Error("Unknown precedence for " + Node.tokenToName(type) + " (type " + type + ")");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isAssociative(int type) {
/* 984*/    switch (type) {
/*   0*/      case 9:
/*   0*/      case 11:
/*   0*/      case 23:
/*   0*/      case 100:
/*   0*/      case 101:
/* 990*/        return true;
/*   0*/    } 
/* 992*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isCommutative(int type) {
/*1004*/    switch (type) {
/*   0*/      case 9:
/*   0*/      case 11:
/*   0*/      case 23:
/*1008*/        return true;
/*   0*/    } 
/*1010*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isAssignmentOp(Node n) {
/*1015*/    switch (n.getType()) {
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
/*1028*/        return true;
/*   0*/    } 
/*1030*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int getOpFromAssignmentOp(Node n) {
/*1034*/    switch (n.getType()) {
/*   0*/      case 87:
/*1036*/        return 9;
/*   0*/      case 88:
/*1038*/        return 10;
/*   0*/      case 89:
/*1040*/        return 11;
/*   0*/      case 90:
/*1042*/        return 18;
/*   0*/      case 91:
/*1044*/        return 19;
/*   0*/      case 92:
/*1046*/        return 20;
/*   0*/      case 93:
/*1048*/        return 21;
/*   0*/      case 94:
/*1050*/        return 22;
/*   0*/      case 95:
/*1052*/        return 23;
/*   0*/      case 96:
/*1054*/        return 24;
/*   0*/      case 97:
/*1056*/        return 25;
/*   0*/    } 
/*1058*/    throw new IllegalArgumentException("Not an assiment op");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExpressionNode(Node n) {
/*1062*/    return (n.getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsFunction(Node n) {
/*1070*/    return containsType(n, 105);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean referencesThis(Node n) {
/*1077*/    return containsType(n, 42, new MatchNotFunction());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isGet(Node n) {
/*1084*/    return (n.getType() == 33 || n.getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isGetProp(Node n) {
/*1092*/    return (n.getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isName(Node n) {
/*1099*/    return (n.getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNew(Node n) {
/*1106*/    return (n.getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isVar(Node n) {
/*1113*/    return (n.getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isVarDeclaration(Node n) {
/*1125*/    return (n.getType() == 38 && n.getParent().getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getAssignedValue(Node n) {
/*1133*/    Preconditions.checkState(isName(n));
/*1134*/    Node parent = n.getParent();
/*1135*/    if (isVar(parent))
/*1136*/      return n.getFirstChild(); 
/*1137*/    if (isAssign(parent) && parent.getFirstChild() == n)
/*1138*/      return n.getNext(); 
/*1140*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isString(Node n) {
/*1148*/    return (n.getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExprAssign(Node n) {
/*1159*/    return (n.getType() == 130 && n.getFirstChild().getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isAssign(Node n) {
/*1167*/    return (n.getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isExprCall(Node n) {
/*1178*/    return (n.getType() == 130 && n.getFirstChild().getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isForIn(Node n) {
/*1186*/    return (n.getType() == 115 && n.getChildCount() == 3);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLoopStructure(Node n) {
/*1194*/    switch (n.getType()) {
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*1198*/        return true;
/*   0*/    } 
/*1200*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getLoopCodeBlock(Node n) {
/*1210*/    switch (n.getType()) {
/*   0*/      case 113:
/*   0*/      case 115:
/*1213*/        return n.getLastChild();
/*   0*/      case 114:
/*1215*/        return n.getFirstChild();
/*   0*/    } 
/*1217*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isWithinLoop(Node n) {
/*1226*/    for (Node parent : (Iterable<Node>)n.getAncestors()) {
/*1227*/      if (isLoopStructure(parent))
/*1228*/        return true; 
/*1231*/      if (isFunction(parent))
/*   0*/        break; 
/*   0*/    } 
/*1235*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isControlStructure(Node n) {
/*1242*/    switch (n.getType()) {
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
/*1254*/        return true;
/*   0*/    } 
/*1256*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isControlStructureCodeBlock(Node parent, Node n) {
/*1265*/    switch (parent.getType()) {
/*   0*/      case 113:
/*   0*/      case 115:
/*   0*/      case 119:
/*   0*/      case 126:
/*1270*/        return (parent.getLastChild() == n);
/*   0*/      case 114:
/*1272*/        return (parent.getFirstChild() == n);
/*   0*/      case 108:
/*1274*/        return (parent.getFirstChild() != n);
/*   0*/      case 77:
/*1276*/        return (parent.getFirstChild() == n || parent.getLastChild() == n);
/*   0*/      case 120:
/*1278*/        return (parent.getLastChild() == n);
/*   0*/      case 110:
/*   0*/      case 111:
/*1281*/        return (parent.getFirstChild() != n);
/*   0*/      case 112:
/*1283*/        return true;
/*   0*/    } 
/*1285*/    Preconditions.checkState(isControlStructure(parent));
/*1286*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getConditionExpression(Node n) {
/*1296*/    switch (n.getType()) {
/*   0*/      case 108:
/*   0*/      case 113:
/*1299*/        return n.getFirstChild();
/*   0*/      case 114:
/*1301*/        return n.getLastChild();
/*   0*/      case 115:
/*1303*/        switch (n.getChildCount()) {
/*   0*/          case 3:
/*1305*/            return null;
/*   0*/          case 4:
/*1307*/            return n.getFirstChild().getNext();
/*   0*/        } 
/*1309*/        throw new IllegalArgumentException("malformed 'for' statement " + n);
/*   0*/      case 111:
/*1311*/        return null;
/*   0*/    } 
/*1313*/    throw new IllegalArgumentException(n + " does not have a condition.");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isStatementBlock(Node n) {
/*1320*/    return (n.getType() == 132 || n.getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isStatement(Node n) {
/*1327*/    Node parent = n.getParent();
/*1331*/    Preconditions.checkState((parent != null));
/*1332*/    switch (parent.getType()) {
/*   0*/      case 125:
/*   0*/      case 126:
/*   0*/      case 132:
/*1336*/        return true;
/*   0*/    } 
/*1338*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSwitchCase(Node n) {
/*1344*/    return (n.getType() == 111 || n.getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isReferenceName(Node n) {
/*1352*/    return (isName(n) && !n.getString().isEmpty());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLabelName(Node n) {
/*1357*/    return (n != null && n.getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isTryFinallyNode(Node parent, Node child) {
/*1362*/    return (parent.getType() == 77 && parent.getChildCount() == 3 && child == parent.getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  static void removeChild(Node parent, Node node) {
/*1369*/    if (isStatementBlock(parent) || isSwitchCase(node) || isTryFinallyNode(parent, node)) {
/*1373*/      parent.removeChild(node);
/*1374*/    } else if (parent.getType() == 118) {
/*1375*/      if (parent.hasMoreThanOneChild()) {
/*1376*/        parent.removeChild(node);
/*   0*/      } else {
/*1379*/        parent.removeChild(node);
/*1381*/        removeChild(parent.getParent(), parent);
/*   0*/      } 
/*1383*/    } else if (node.getType() == 125) {
/*1386*/      node.detachChildren();
/*1387*/    } else if (parent.getType() == 126 && node == parent.getLastChild()) {
/*1390*/      parent.removeChild(node);
/*1392*/      removeChild(parent.getParent(), parent);
/*1393*/    } else if (parent.getType() == 115 && parent.getChildCount() == 4) {
/*1398*/      parent.replaceChild(node, new Node(124));
/*   0*/    } else {
/*1400*/      throw new IllegalStateException("Invalid attempt to remove node: " + node.toString() + " of " + parent.toString());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static boolean tryMergeBlock(Node block) {
/*1410*/    Preconditions.checkState((block.getType() == 125));
/*1411*/    Node parent = block.getParent();
/*1414*/    if (isStatementBlock(parent)) {
/*1415*/      Node previous = block;
/*1416*/      while (block.hasChildren()) {
/*1417*/        Node child = block.removeFirstChild();
/*1418*/        parent.addChildAfter(child, previous);
/*1419*/        previous = child;
/*   0*/      } 
/*1421*/      parent.removeChild(block);
/*1422*/      return true;
/*   0*/    } 
/*1424*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isCall(Node n) {
/*1432*/    return (n.getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isCallOrNew(Node node) {
/*1440*/    return (isCall(node) || isNew(node));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunction(Node n) {
/*1447*/    return (n.getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getFunctionBody(Node fn) {
/*1454*/    Preconditions.checkArgument(isFunction(fn));
/*1455*/    return fn.getLastChild();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isThis(Node node) {
/*1462*/    return (node.getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsCall(Node n) {
/*1469*/    return containsType(n, 37);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionDeclaration(Node n) {
/*1478*/    return (n.getType() == 105 && isStatement(n));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isHoistedFunctionDeclaration(Node n) {
/*1487*/    return (isFunctionDeclaration(n) && (n.getParent().getType() == 132 || n.getParent().getParent().getType() == 105));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionExpression(Node n) {
/*1516*/    return (n.getType() == 105 && !isStatement(n));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isEmptyFunctionExpression(Node node) {
/*1526*/    return (isFunctionExpression(node) && isEmptyBlock(node.getLastChild()));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isVarArgsFunction(Node function) {
/*1534*/    Preconditions.checkArgument(isFunction(function));
/*1535*/    return isNameReferenced(function.getLastChild(), "arguments", new MatchNotFunction());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isObjectCallMethod(Node callNode, String methodName) {
/*1547*/    if (callNode.getType() == 37) {
/*1548*/      Node functionIndentifyingExpression = callNode.getFirstChild();
/*1549*/      if (isGet(functionIndentifyingExpression)) {
/*1550*/        Node last = functionIndentifyingExpression.getLastChild();
/*1551*/        if (last != null && last.getType() == 40) {
/*1552*/          String propName = last.getString();
/*1553*/          return propName.equals(methodName);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1557*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionObjectCall(Node callNode) {
/*1567*/    return isObjectCallMethod(callNode, "call");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionObjectApply(Node callNode) {
/*1576*/    return isObjectCallMethod(callNode, "apply");
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isFunctionObjectCallOrApply(Node callNode) {
/*1588*/    return (isFunctionObjectCall(callNode) || isFunctionObjectApply(callNode));
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSimpleFunctionObjectCall(Node callNode) {
/*1598*/    if (isFunctionObjectCall(callNode) && 
/*1599*/      callNode.getFirstChild().getFirstChild().getType() == 38)
/*1600*/      return true; 
/*1604*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLhs(Node n, Node parent) {
/*1617*/    return ((parent.getType() == 86 && parent.getFirstChild() == n) || parent.getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isObjectLitKey(Node node, Node parent) {
/*1629*/    switch (node.getType()) {
/*   0*/      case 39:
/*   0*/      case 40:
/*1632*/        return (parent.getType() == 64);
/*   0*/      case 147:
/*   0*/      case 148:
/*1635*/        return true;
/*   0*/    } 
/*1637*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isGetOrSetKey(Node node) {
/*1647*/    switch (node.getType()) {
/*   0*/      case 147:
/*   0*/      case 148:
/*1650*/        return true;
/*   0*/    } 
/*1652*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static String opToStr(int operator) {
/*1664*/    switch (operator) {
/*   0*/      case 9:
/*1665*/        return "|";
/*   0*/      case 100:
/*1666*/        return "||";
/*   0*/      case 10:
/*1667*/        return "^";
/*   0*/      case 101:
/*1668*/        return "&&";
/*   0*/      case 11:
/*1669*/        return "&";
/*   0*/      case 45:
/*1670*/        return "===";
/*   0*/      case 12:
/*1671*/        return "==";
/*   0*/      case 26:
/*1672*/        return "!";
/*   0*/      case 13:
/*1673*/        return "!=";
/*   0*/      case 46:
/*1674*/        return "!==";
/*   0*/      case 18:
/*1675*/        return "<<";
/*   0*/      case 51:
/*1676*/        return "in";
/*   0*/      case 15:
/*1677*/        return "<=";
/*   0*/      case 14:
/*1678*/        return "<";
/*   0*/      case 20:
/*1679*/        return ">>>";
/*   0*/      case 19:
/*1680*/        return ">>";
/*   0*/      case 17:
/*1681*/        return ">=";
/*   0*/      case 16:
/*1682*/        return ">";
/*   0*/      case 23:
/*1683*/        return "*";
/*   0*/      case 24:
/*1684*/        return "/";
/*   0*/      case 25:
/*1685*/        return "%";
/*   0*/      case 27:
/*1686*/        return "~";
/*   0*/      case 21:
/*1687*/        return "+";
/*   0*/      case 22:
/*1688*/        return "-";
/*   0*/      case 28:
/*1689*/        return "+";
/*   0*/      case 29:
/*1690*/        return "-";
/*   0*/      case 86:
/*1691*/        return "=";
/*   0*/      case 87:
/*1692*/        return "|=";
/*   0*/      case 88:
/*1693*/        return "^=";
/*   0*/      case 89:
/*1694*/        return "&=";
/*   0*/      case 90:
/*1695*/        return "<<=";
/*   0*/      case 91:
/*1696*/        return ">>=";
/*   0*/      case 92:
/*1697*/        return ">>>=";
/*   0*/      case 93:
/*1698*/        return "+=";
/*   0*/      case 94:
/*1699*/        return "-=";
/*   0*/      case 95:
/*1700*/        return "*=";
/*   0*/      case 96:
/*1701*/        return "/=";
/*   0*/      case 97:
/*1702*/        return "%=";
/*   0*/      case 122:
/*1703*/        return "void";
/*   0*/      case 32:
/*1704*/        return "typeof";
/*   0*/      case 52:
/*1705*/        return "instanceof";
/*   0*/    } 
/*1706*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String opToStrNoFail(int operator) {
/*1719*/    String res = opToStr(operator);
/*1720*/    if (res == null)
/*1721*/      throw new Error("Unknown op " + operator + ": " + Token.name(operator)); 
/*1724*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsType(Node node, int type, Predicate<Node> traverseChildrenPred) {
/*1733*/    return has(node, new MatchNodeType(type), traverseChildrenPred);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean containsType(Node node, int type) {
/*1740*/    return containsType(node, type, Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  static void redeclareVarsInsideBranch(Node branch) {
/*1750*/    Collection<Node> vars = getVarsDeclaredInBranch(branch);
/*1751*/    if (vars.isEmpty())
/*   0*/      return; 
/*1755*/    Node parent = getAddingRoot(branch);
/*1756*/    for (Node nameNode : vars) {
/*1757*/      Node var = new Node(118, Node.newString(38, nameNode.getString()).copyInformationFrom(nameNode)).copyInformationFrom(nameNode);
/*1762*/      copyNameAnnotations(nameNode, var.getFirstChild());
/*1763*/      parent.addChildToFront(var);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static void copyNameAnnotations(Node source, Node destination) {
/*1773*/    if (source.getBooleanProp(42))
/*1774*/      destination.putBooleanProp(42, true); 
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getAddingRoot(Node n) {
/*1783*/    Node addingRoot = null;
/*1784*/    Node ancestor = n;
/*1785*/    while (null != (ancestor = ancestor.getParent())) {
/*1786*/      int type = ancestor.getType();
/*1787*/      if (type == 132) {
/*1788*/        addingRoot = ancestor;
/*   0*/        break;
/*   0*/      } 
/*1790*/      if (type == 105) {
/*1791*/        addingRoot = ancestor.getLastChild();
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*1797*/    Preconditions.checkState((addingRoot.getType() == 125 || addingRoot.getType() == 132));
/*1799*/    Preconditions.checkState((addingRoot.getFirstChild() == null || addingRoot.getFirstChild().getType() != 132));
/*1801*/    return addingRoot;
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newFunctionNode(String name, List<Node> params, Node body, int lineno, int charno) {
/*1807*/    Node parameterParen = new Node(83, lineno, charno);
/*1808*/    for (Node param : params)
/*1809*/      parameterParen.addChildToBack(param); 
/*1811*/    Node function = new Node(105, lineno, charno);
/*1812*/    function.addChildrenToBack(Node.newString(38, name, lineno, charno));
/*1814*/    function.addChildToBack(parameterParen);
/*1815*/    function.addChildToBack(body);
/*1816*/    return function;
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newQualifiedNameNode(CodingConvention convention, String name, int lineno, int charno) {
/*1829*/    int endPos = name.indexOf('.');
/*1830*/    if (endPos == -1)
/*1831*/      return newName(convention, name, lineno, charno); 
/*1833*/    Node node = newName(convention, name.substring(0, endPos), lineno, charno);
/*   0*/    do {
/*1837*/      int startPos = endPos + 1;
/*1838*/      endPos = name.indexOf('.', startPos);
/*1839*/      String part = (endPos == -1) ? name.substring(startPos) : name.substring(startPos, endPos);
/*1842*/      Node propNode = Node.newString(40, part, lineno, charno);
/*1843*/      if (convention.isConstantKey(part))
/*1844*/        propNode.putBooleanProp(42, true); 
/*1846*/      node = new Node(33, node, propNode, lineno, charno);
/*1847*/    } while (endPos != -1);
/*1849*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newQualifiedNameNode(CodingConvention convention, String name, Node basisNode, String originalName) {
/*1868*/    Node node = newQualifiedNameNode(convention, name, -1, -1);
/*1869*/    setDebugInformation(node, basisNode, originalName);
/*1870*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getRootOfQualifiedName(Node qName) {
/*1877*/    Node current = qName;
/*1878*/    for (;; current = current.getFirstChild()) {
/*1879*/      int type = current.getType();
/*1880*/      if (type == 38 || type == 42)
/*1881*/        return current; 
/*1883*/      Preconditions.checkState((type == 33));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static void setDebugInformation(Node node, Node basisNode, String originalName) {
/*1897*/    node.copyInformationFromForTree(basisNode);
/*1898*/    node.putProp(39, originalName);
/*   0*/  }
/*   0*/  
/*   0*/  private static Node newName(CodingConvention convention, String name, int lineno, int charno) {
/*1903*/    Node nameNode = Node.newString(38, name, lineno, charno);
/*1904*/    if (convention.isConstant(name))
/*1905*/      nameNode.putBooleanProp(42, true); 
/*1907*/    return nameNode;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newName(CodingConvention convention, String name, Node basisNode) {
/*1922*/    Node nameNode = Node.newString(38, name);
/*1923*/    if (convention.isConstantKey(name))
/*1924*/      nameNode.putBooleanProp(42, true); 
/*1926*/    nameNode.copyInformationFrom(basisNode);
/*1927*/    return nameNode;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newName(CodingConvention convention, String name, Node basisNode, String originalName) {
/*1946*/    Node nameNode = newName(convention, name, basisNode);
/*1947*/    nameNode.putProp(39, originalName);
/*1948*/    return nameNode;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isLatin(String s) {
/*1966*/    char LARGEST_BASIC_LATIN = '\u007F';
/*1967*/    int len = s.length();
/*1968*/    for (int index = 0; index < len; index++) {
/*1969*/      char c = s.charAt(index);
/*1970*/      if (c > LARGEST_BASIC_LATIN)
/*1971*/        return false; 
/*   0*/    } 
/*1974*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isValidPropertyName(String name) {
/*1982*/    return (TokenStream.isJSIdentifier(name) && !TokenStream.isKeyword(name) && isLatin(name));
/*   0*/  }
/*   0*/  
/*   0*/  private static class VarCollector implements Visitor {
/*1994*/    final Map<String, Node> vars = Maps.newLinkedHashMap();
/*   0*/    
/*   0*/    public void visit(Node n) {
/*1997*/      if (n.getType() == 38) {
/*1998*/        Node parent = n.getParent();
/*1999*/        if (parent != null && parent.getType() == 118) {
/*2000*/          String name = n.getString();
/*2001*/          if (!this.vars.containsKey(name))
/*2002*/            this.vars.put(name, n); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private VarCollector() {}
/*   0*/  }
/*   0*/  
/*   0*/  public static Collection<Node> getVarsDeclaredInBranch(Node root) {
/*2013*/    VarCollector collector = new VarCollector();
/*2014*/    visitPreOrder(root, collector, new MatchNotFunction());
/*2018*/    return collector.vars.values();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isPrototypePropertyDeclaration(Node n) {
/*2026*/    if (!isExprAssign(n))
/*2027*/      return false; 
/*2029*/    return isPrototypeProperty(n.getFirstChild().getFirstChild());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isPrototypeProperty(Node n) {
/*2033*/    String lhsString = n.getQualifiedName();
/*2034*/    if (lhsString == null)
/*2035*/      return false; 
/*2037*/    int prototypeIdx = lhsString.indexOf(".prototype.");
/*2038*/    return (prototypeIdx != -1);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getPrototypeClassName(Node qName) {
/*2045*/    Node cur = qName;
/*2046*/    while (isGetProp(cur)) {
/*2047*/      if (cur.getLastChild().getString().equals("prototype"))
/*2048*/        return cur.getFirstChild(); 
/*2050*/      cur = cur.getFirstChild();
/*   0*/    } 
/*2053*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  static String getPrototypePropertyName(Node qName) {
/*2060*/    String qNameStr = qName.getQualifiedName();
/*2061*/    int prototypeIdx = qNameStr.lastIndexOf(".prototype.");
/*2062*/    int memberIndex = prototypeIdx + ".prototype".length() + 1;
/*2063*/    return qNameStr.substring(memberIndex);
/*   0*/  }
/*   0*/  
/*   0*/  static Node newUndefinedNode(Node srcReferenceNode) {
/*2072*/    Node node = new Node(122, Node.newNumber(0.0D));
/*2073*/    if (srcReferenceNode != null)
/*2074*/      node.copyInformationFromForTree(srcReferenceNode); 
/*2076*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newVarNode(String name, Node value) {
/*2083*/    Node nodeName = Node.newString(38, name);
/*2084*/    if (value != null) {
/*2085*/      Preconditions.checkState((value.getNext() == null));
/*2086*/      nodeName.addChildToBack(value);
/*2087*/      nodeName.copyInformationFrom(value);
/*   0*/    } 
/*2089*/    Node var = new Node(118, nodeName).copyInformationFrom(nodeName);
/*2092*/    return var;
/*   0*/  }
/*   0*/  
/*   0*/  private static class MatchNameNode implements Predicate<Node> {
/*   0*/    final String name;
/*   0*/    
/*   0*/    MatchNameNode(String name) {
/*2102*/      this.name = name;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean apply(Node n) {
/*2106*/      return (n.getType() == 38 && n.getString().equals(this.name));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class MatchNodeType implements Predicate<Node> {
/*   0*/    final int type;
/*   0*/    
/*   0*/    MatchNodeType(int type) {
/*2118*/      this.type = type;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean apply(Node n) {
/*2122*/      return (n.getType() == this.type);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class MatchDeclaration implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*2132*/      return (NodeUtil.isFunctionDeclaration(n) || n.getType() == 118);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class MatchNotFunction implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*2141*/      return !NodeUtil.isFunction(n);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class MatchShallowStatement implements Predicate<Node> {
/*   0*/    public boolean apply(Node n) {
/*2150*/      Node parent = n.getParent();
/*2151*/      return (n.getType() == 125 || (!NodeUtil.isFunction(n) && (parent == null || NodeUtil.isControlStructure(parent) || NodeUtil.isStatementBlock(parent))));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static int getNodeTypeReferenceCount(Node node, int type, Predicate<Node> traverseChildrenPred) {
/*2163*/    return getCount(node, new MatchNodeType(type), traverseChildrenPred);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNameReferenced(Node node, String name, Predicate<Node> traverseChildrenPred) {
/*2172*/    return has(node, new MatchNameNode(name), traverseChildrenPred);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isNameReferenced(Node node, String name) {
/*2179*/    return isNameReferenced(node, name, Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  static int getNameReferenceCount(Node node, String name) {
/*2186*/    return getCount(node, new MatchNameNode(name), Predicates.alwaysTrue());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean has(Node node, Predicate<Node> pred, Predicate<Node> traverseChildrenPred) {
/*2196*/    if (pred.apply(node))
/*2197*/      return true; 
/*2200*/    if (!traverseChildrenPred.apply(node))
/*2201*/      return false; 
/*2204*/    for (Node c = node.getFirstChild(); c != null; c = c.getNext()) {
/*2205*/      if (has(c, pred, traverseChildrenPred))
/*2206*/        return true; 
/*   0*/    } 
/*2210*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static int getCount(Node n, Predicate<Node> pred, Predicate<Node> traverseChildrenPred) {
/*2219*/    int total = 0;
/*2221*/    if (pred.apply(n))
/*2222*/      total++; 
/*2225*/    if (traverseChildrenPred.apply(n))
/*2226*/      for (Node c = n.getFirstChild(); c != null; c = c.getNext())
/*2227*/        total += getCount(c, pred, traverseChildrenPred);  
/*2231*/    return total;
/*   0*/  }
/*   0*/  
/*   0*/  static void visitPreOrder(Node node, Visitor vistor, Predicate<Node> traverseChildrenPred) {
/*2249*/    vistor.visit(node);
/*2251*/    if (traverseChildrenPred.apply(node))
/*2252*/      for (Node c = node.getFirstChild(); c != null; c = c.getNext())
/*2253*/        visitPreOrder(c, vistor, traverseChildrenPred);  
/*   0*/  }
/*   0*/  
/*   0*/  static void visitPostOrder(Node node, Visitor vistor, Predicate<Node> traverseChildrenPred) {
/*2265*/    if (traverseChildrenPred.apply(node))
/*2266*/      for (Node c = node.getFirstChild(); c != null; c = c.getNext())
/*2267*/        visitPostOrder(c, vistor, traverseChildrenPred);  
/*2271*/    vistor.visit(node);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean hasFinally(Node n) {
/*2278*/    Preconditions.checkArgument((n.getType() == 77));
/*2279*/    return (n.getChildCount() == 3);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getCatchBlock(Node n) {
/*2287*/    Preconditions.checkArgument((n.getType() == 77));
/*2288*/    return n.getFirstChild().getNext();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean hasCatchHandler(Node n) {
/*2296*/    Preconditions.checkArgument((n.getType() == 125));
/*2297*/    return (n.hasChildren() && n.getFirstChild().getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getFnParameters(Node fnNode) {
/*2306*/    Preconditions.checkArgument((fnNode.getType() == 105));
/*2307*/    return fnNode.getFirstChild().getNext();
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isConstantName(Node node) {
/*2328*/    return node.getBooleanProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isConstantByConvention(CodingConvention convention, Node node, Node parent) {
/*2334*/    String name = node.getString();
/*2335*/    if (parent.getType() == 33 && node == parent.getLastChild())
/*2337*/      return convention.isConstantKey(name); 
/*2338*/    if (isObjectLitKey(node, parent))
/*2339*/      return convention.isConstantKey(name); 
/*2341*/    return convention.isConstant(name);
/*   0*/  }
/*   0*/  
/*   0*/  static JSDocInfo getInfoForNameNode(Node nameNode) {
/*2350*/    JSDocInfo info = null;
/*2351*/    Node parent = null;
/*2352*/    if (nameNode != null) {
/*2353*/      info = nameNode.getJSDocInfo();
/*2354*/      parent = nameNode.getParent();
/*   0*/    } 
/*2357*/    if (info == null && parent != null && ((parent.getType() == 118 && parent.hasOneChild()) || parent.getType() == 105))
/*2360*/      info = parent.getJSDocInfo(); 
/*2362*/    return info;
/*   0*/  }
/*   0*/  
/*   0*/  static JSDocInfo getFunctionInfo(Node n) {
/*2369*/    Preconditions.checkState((n.getType() == 105));
/*2370*/    JSDocInfo fnInfo = n.getJSDocInfo();
/*2371*/    if (fnInfo == null && isFunctionExpression(n)) {
/*2373*/      Node parent = n.getParent();
/*2374*/      if (parent.getType() == 86) {
/*2376*/        fnInfo = parent.getJSDocInfo();
/*2377*/      } else if (parent.getType() == 38) {
/*2379*/        fnInfo = parent.getParent().getJSDocInfo();
/*   0*/      } 
/*   0*/    } 
/*2382*/    return fnInfo;
/*   0*/  }
/*   0*/  
/*   0*/  static String getSourceName(Node n) {
/*2390*/    String sourceName = null;
/*2391*/    while (sourceName == null && n != null) {
/*2392*/      sourceName = (String)n.getProp(16);
/*2393*/      n = n.getParent();
/*   0*/    } 
/*2395*/    return sourceName;
/*   0*/  }
/*   0*/  
/*   0*/  static Node newCallNode(Node callTarget, Node... parameters) {
/*2402*/    boolean isFreeCall = isName(callTarget);
/*2403*/    Node call = new Node(37, callTarget);
/*2404*/    call.putBooleanProp(49, isFreeCall);
/*2405*/    for (Node parameter : parameters)
/*2406*/      call.addChildToBack(parameter); 
/*2408*/    return call;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean evaluatesToLocalValue(Node value) {
/*2416*/    return evaluatesToLocalValue(value, Predicates.alwaysFalse());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean evaluatesToLocalValue(Node value, Predicate<Node> locals) {
/*2425*/    switch (value.getType()) {
/*   0*/      case 86:
/*2430*/        return (isImmutableValue(value.getLastChild()) || (locals.apply(value) && evaluatesToLocalValue(value.getLastChild(), locals)));
/*   0*/      case 85:
/*2434*/        return evaluatesToLocalValue(value.getLastChild(), locals);
/*   0*/      case 100:
/*   0*/      case 101:
/*2437*/        return (evaluatesToLocalValue(value.getFirstChild(), locals) && evaluatesToLocalValue(value.getLastChild(), locals));
/*   0*/      case 98:
/*2440*/        return (evaluatesToLocalValue(value.getFirstChild().getNext(), locals) && evaluatesToLocalValue(value.getLastChild(), locals));
/*   0*/      case 102:
/*   0*/      case 103:
/*2444*/        if (value.getBooleanProp(31))
/*2445*/          return evaluatesToLocalValue(value.getFirstChild(), locals); 
/*2447*/        return true;
/*   0*/      case 42:
/*2450*/        return locals.apply(value);
/*   0*/      case 38:
/*2452*/        return (isImmutableValue(value) || locals.apply(value));
/*   0*/      case 33:
/*   0*/      case 35:
/*2456*/        return locals.apply(value);
/*   0*/      case 37:
/*2458*/        return (callHasLocalResult(value) || isToStringMethodCall(value) || locals.apply(value));
/*   0*/      case 30:
/*2465*/        return false;
/*   0*/      case 47:
/*   0*/      case 63:
/*   0*/      case 64:
/*   0*/      case 105:
/*2471*/        return true;
/*   0*/      case 51:
/*2474*/        return true;
/*   0*/    } 
/*2479*/    if (isAssignmentOp(value) || isSimpleOperator(value) || isImmutableValue(value))
/*2482*/      return true; 
/*2485*/    throw new IllegalStateException("Unexpected expression node" + value + "\n parent:" + value.getParent());
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getNthSibling(Node first, int index) {
/*2497*/    Node sibling = first;
/*2498*/    while (index != 0 && sibling != null) {
/*2499*/      sibling = sibling.getNext();
/*2500*/      index--;
/*   0*/    } 
/*2502*/    return sibling;
/*   0*/  }
/*   0*/  
/*   0*/  static Node getArgumentForFunction(Node function, int index) {
/*2510*/    Preconditions.checkState(isFunction(function));
/*2511*/    return getNthSibling(function.getFirstChild().getNext().getFirstChild(), index);
/*   0*/  }
/*   0*/  
/*   0*/  static Node getArgumentForCallOrNew(Node call, int index) {
/*2520*/    Preconditions.checkState(isCallOrNew(call));
/*2521*/    return getNthSibling(call.getFirstChild().getNext(), index);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isToStringMethodCall(Node call) {
/*2526*/    Node getNode = call.getFirstChild();
/*2527*/    if (isGet(getNode)) {
/*2528*/      Node propNode = getNode.getLastChild();
/*2529*/      return (isString(propNode) && "toString".equals(propNode.getString()));
/*   0*/    } 
/*2531*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  static interface Visitor {
/*   0*/    void visit(Node param1Node);
/*   0*/  }
/*   0*/}
