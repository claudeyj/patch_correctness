/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.base.Predicate;
/*   0*/import com.google.common.base.Predicates;
/*   0*/import com.google.common.base.Supplier;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class FunctionInjector {
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final Supplier<String> safeNameIdSupplier;
/*   0*/  
/*   0*/  private final boolean allowDecomposition;
/*   0*/  
/*  43*/  private Set<String> knownConstants = Sets.newHashSet();
/*   0*/  
/*   0*/  private final boolean assumeStrictThis;
/*   0*/  
/*   0*/  private final boolean assumeMinimumCapture;
/*   0*/  
/*   0*/  public FunctionInjector(AbstractCompiler compiler, Supplier<String> safeNameIdSupplier, boolean allowDecomposition, boolean assumeStrictThis, boolean assumeMinimumCapture) {
/*  58*/    Preconditions.checkNotNull(compiler);
/*  59*/    Preconditions.checkNotNull(safeNameIdSupplier);
/*  60*/    this.compiler = compiler;
/*  61*/    this.safeNameIdSupplier = safeNameIdSupplier;
/*  62*/    this.allowDecomposition = allowDecomposition;
/*  63*/    this.assumeStrictThis = assumeStrictThis;
/*  64*/    this.assumeMinimumCapture = assumeMinimumCapture;
/*   0*/  }
/*   0*/  
/*   0*/  enum InliningMode {
/*  73*/    DIRECT, BLOCK;
/*   0*/  }
/*   0*/  
/*   0*/  static class Reference {
/*   0*/    final Node callNode;
/*   0*/    
/*   0*/    final JSModule module;
/*   0*/    
/*   0*/    final FunctionInjector.InliningMode mode;
/*   0*/    
/*   0*/    Reference(Node callNode, JSModule module, FunctionInjector.InliningMode mode) {
/*  89*/      this.callNode = callNode;
/*  90*/      this.module = module;
/*  91*/      this.mode = mode;
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 101*/  private static final int NAME_COST_ESTIMATE = InlineCostEstimator.ESTIMATED_IDENTIFIER_COST;
/*   0*/  
/*   0*/  private static final int COMMA_COST = 1;
/*   0*/  
/*   0*/  private static final int PAREN_COST = 2;
/*   0*/  
/*   0*/  boolean doesFunctionMeetMinimumRequirements(final String fnName, Node fnNode) {
/* 121*/    Node block = NodeUtil.getFunctionBody(fnNode);
/* 131*/    if (!this.compiler.getCodingConvention().isInlinableFunction(fnNode))
/* 132*/      return false; 
/* 135*/    final String fnRecursionName = fnNode.getFirstChild().getString();
/* 136*/    Preconditions.checkState((fnRecursionName != null));
/* 139*/    boolean referencesArguments = NodeUtil.isNameReferenced(block, "arguments", NodeUtil.MATCH_NOT_FUNCTION);
/* 143*/    Predicate<Node> p = new Predicate<Node>() {
/*   0*/        public boolean apply(Node n) {
/* 146*/          if (n.isName())
/* 147*/            return (n.getString().equals("eval") || (!fnName.isEmpty() && n.getString().equals(fnName)) || (!fnRecursionName.isEmpty() && n.getString().equals(fnRecursionName))); 
/* 153*/          return false;
/*   0*/        }
/*   0*/      };
/* 157*/    return (!referencesArguments && !NodeUtil.has(block, p, Predicates.alwaysTrue()));
/*   0*/  }
/*   0*/  
/*   0*/  CanInlineResult canInlineReferenceToFunction(NodeTraversal t, Node callNode, Node fnNode, Set<String> needAliases, InliningMode mode, boolean referencesThis, boolean containsFunctions) {
/* 180*/    if (!isSupportedCallType(callNode))
/* 181*/      return CanInlineResult.NO; 
/* 188*/    if (containsFunctions) {
/* 189*/      if (!this.assumeMinimumCapture && !t.inGlobalScope())
/* 192*/        return CanInlineResult.NO; 
/* 193*/      if (NodeUtil.isWithinLoop(callNode))
/* 196*/        return CanInlineResult.NO; 
/*   0*/    } 
/* 201*/    if (referencesThis && !NodeUtil.isFunctionObjectCall(callNode))
/* 204*/      return CanInlineResult.NO; 
/* 207*/    if (mode == InliningMode.DIRECT)
/* 208*/      return canInlineReferenceDirectly(callNode, fnNode); 
/* 210*/    return canInlineReferenceAsStatementBlock(t, callNode, fnNode, needAliases);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isSupportedCallType(Node callNode) {
/* 221*/    if (!callNode.getFirstChild().isName())
/* 222*/      if (NodeUtil.isFunctionObjectCall(callNode)) {
/* 223*/        if (!this.assumeStrictThis) {
/* 224*/          Node thisValue = callNode.getFirstChild().getNext();
/* 225*/          if (thisValue == null || !thisValue.isThis())
/* 226*/            return false; 
/*   0*/        } 
/* 229*/      } else if (NodeUtil.isFunctionObjectApply(callNode)) {
/* 230*/        return false;
/*   0*/      }  
/* 234*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  Node inline(Node callNode, String fnName, Node fnNode, InliningMode mode) {
/* 242*/    Preconditions.checkState(this.compiler.getLifeCycleStage().isNormalized());
/* 244*/    if (mode == InliningMode.DIRECT)
/* 245*/      return inlineReturnValue(callNode, fnNode); 
/* 247*/    return inlineFunction(callNode, fnNode, fnName);
/*   0*/  }
/*   0*/  
/*   0*/  private Node inlineReturnValue(Node callNode, Node fnNode) {
/*   0*/    Node newExpression;
/* 257*/    Node block = fnNode.getLastChild();
/* 258*/    Node callParentNode = callNode.getParent();
/* 265*/    Map<String, Node> argMap = FunctionArgumentInjector.getFunctionCallParameterMap(fnNode, callNode, this.safeNameIdSupplier);
/* 270*/    if (!block.hasChildren()) {
/* 271*/      Node srcLocation = block;
/* 272*/      newExpression = NodeUtil.newUndefinedNode(srcLocation);
/*   0*/    } else {
/* 274*/      Node returnNode = block.getFirstChild();
/* 275*/      Preconditions.checkArgument(returnNode.isReturn());
/* 278*/      Node safeReturnNode = returnNode.cloneTree();
/* 279*/      Node inlineResult = FunctionArgumentInjector.inject(null, safeReturnNode, null, argMap);
/* 281*/      Preconditions.checkArgument((safeReturnNode == inlineResult));
/* 282*/      newExpression = safeReturnNode.removeFirstChild();
/*   0*/    } 
/* 285*/    callParentNode.replaceChild(callNode, newExpression);
/* 286*/    return newExpression;
/*   0*/  }
/*   0*/  
/*   0*/  private enum CallSiteType {
/* 298*/    UNSUPPORTED {
/*   0*/      public void prepare(FunctionInjector injector, Node callNode) {
/* 301*/        throw new IllegalStateException("unexpected");
/*   0*/      }
/*   0*/    },
/* 310*/    SIMPLE_CALL {
/*   0*/      public void prepare(FunctionInjector injector, Node callNode) {}
/*   0*/    },
/* 325*/    SIMPLE_ASSIGNMENT {
/*   0*/      public void prepare(FunctionInjector injector, Node callNode) {}
/*   0*/    },
/* 340*/    VAR_DECL_SIMPLE_ASSIGNMENT {
/*   0*/      public void prepare(FunctionInjector injector, Node callNode) {}
/*   0*/    },
/* 359*/    EXPRESSION {
/*   0*/      public void prepare(FunctionInjector injector, Node callNode) {
/* 362*/        injector.getDecomposer().moveExpression(callNode);
/* 365*/        CallSiteType callSiteType = injector.classifyCallSite(callNode);
/* 366*/        Preconditions.checkState((this != callSiteType));
/* 367*/        callSiteType.prepare(injector, callNode);
/*   0*/      }
/*   0*/    },
/* 376*/    DECOMPOSABLE_EXPRESSION {
/*   0*/      public void prepare(FunctionInjector injector, Node callNode) {
/* 379*/        injector.getDecomposer().maybeExposeExpression(callNode);
/* 382*/        CallSiteType callSiteType = injector.classifyCallSite(callNode);
/* 383*/        Preconditions.checkState((this != callSiteType));
/* 384*/        callSiteType.prepare(injector, callNode);
/*   0*/      }
/*   0*/    };
/*   0*/    
/*   0*/    public abstract void prepare(FunctionInjector param1FunctionInjector, Node param1Node);
/*   0*/  }
/*   0*/  
/*   0*/  private CallSiteType classifyCallSite(Node callNode) {
/* 395*/    Node parent = callNode.getParent();
/* 396*/    Node grandParent = parent.getParent();
/* 399*/    if (NodeUtil.isExprCall(parent))
/* 401*/      return CallSiteType.SIMPLE_CALL; 
/* 402*/    if (NodeUtil.isExprAssign(grandParent) && !NodeUtil.isVarOrSimpleAssignLhs(callNode, parent) && parent.getFirstChild().isName() && !NodeUtil.isConstantName(parent.getFirstChild()))
/* 407*/      return CallSiteType.SIMPLE_ASSIGNMENT; 
/* 408*/    if (parent.isName() && !NodeUtil.isConstantName(parent) && grandParent.isVar() && grandParent.hasOneChild())
/* 415*/      return CallSiteType.VAR_DECL_SIMPLE_ASSIGNMENT; 
/* 417*/    Node expressionRoot = ExpressionDecomposer.findExpressionRoot(callNode);
/* 418*/    if (expressionRoot != null) {
/* 419*/      ExpressionDecomposer decomposer = new ExpressionDecomposer(this.compiler, this.safeNameIdSupplier, this.knownConstants);
/* 421*/      ExpressionDecomposer.DecompositionType type = decomposer.canExposeExpression(callNode);
/* 423*/      if (type == ExpressionDecomposer.DecompositionType.MOVABLE)
/* 424*/        return CallSiteType.EXPRESSION; 
/* 425*/      if (type == ExpressionDecomposer.DecompositionType.DECOMPOSABLE)
/* 426*/        return CallSiteType.DECOMPOSABLE_EXPRESSION; 
/* 428*/      Preconditions.checkState((type == ExpressionDecomposer.DecompositionType.UNDECOMPOSABLE));
/*   0*/    } 
/* 433*/    return CallSiteType.UNSUPPORTED;
/*   0*/  }
/*   0*/  
/*   0*/  private ExpressionDecomposer getDecomposer() {
/* 437*/    return new ExpressionDecomposer(this.compiler, this.safeNameIdSupplier, this.knownConstants);
/*   0*/  }
/*   0*/  
/*   0*/  void maybePrepareCall(Node callNode) {
/* 446*/    CallSiteType callSiteType = classifyCallSite(callNode);
/* 447*/    callSiteType.prepare(this, callNode);
/*   0*/  }
/*   0*/  
/*   0*/  private Node inlineFunction(Node callNode, Node fnNode, String fnName) {
/* 457*/    Node parent = callNode.getParent();
/* 458*/    Node grandParent = parent.getParent();
/* 462*/    CallSiteType callSiteType = classifyCallSite(callNode);
/* 463*/    Preconditions.checkArgument((callSiteType != CallSiteType.UNSUPPORTED));
/* 465*/    boolean isCallInLoop = NodeUtil.isWithinLoop(callNode);
/* 470*/    String resultName = null;
/*   0*/    boolean needsDefaultReturnResult = true;
/* 472*/    switch (callSiteType) {
/*   0*/      case SIMPLE_ASSIGNMENT:
/* 474*/        resultName = parent.getFirstChild().getString();
/*   0*/        break;
/*   0*/      case VAR_DECL_SIMPLE_ASSIGNMENT:
/* 478*/        resultName = parent.getString();
/*   0*/        break;
/*   0*/      case SIMPLE_CALL:
/* 482*/        resultName = null;
/* 483*/        needsDefaultReturnResult = false;
/*   0*/        break;
/*   0*/      case EXPRESSION:
/* 487*/        throw new IllegalStateException("Movable expressions must be moved before inlining.");
/*   0*/      case DECOMPOSABLE_EXPRESSION:
/* 491*/        throw new IllegalStateException("Decomposable expressions must be decomposed before inlining.");
/*   0*/      default:
/* 495*/        throw new IllegalStateException("Unexpected call site type.");
/*   0*/    } 
/* 498*/    FunctionToBlockMutator mutator = new FunctionToBlockMutator(this.compiler, this.safeNameIdSupplier);
/* 501*/    Node newBlock = mutator.mutate(fnName, fnNode, callNode, resultName, needsDefaultReturnResult, isCallInLoop);
/* 508*/    Node greatGrandParent = grandParent.getParent();
/* 509*/    switch (callSiteType) {
/*   0*/      case VAR_DECL_SIMPLE_ASSIGNMENT:
/* 512*/        parent.removeChild(parent.getFirstChild());
/* 513*/        Preconditions.checkState((parent.getFirstChild() == null));
/* 515*/        greatGrandParent.addChildAfter(newBlock, grandParent);
/*   0*/        break;
/*   0*/      case SIMPLE_ASSIGNMENT:
/* 521*/        Preconditions.checkState(grandParent.isExprResult());
/* 522*/        greatGrandParent.replaceChild(grandParent, newBlock);
/*   0*/        break;
/*   0*/      case SIMPLE_CALL:
/* 527*/        Preconditions.checkState(parent.isExprResult());
/* 528*/        grandParent.replaceChild(parent, newBlock);
/*   0*/        break;
/*   0*/      default:
/* 532*/        throw new IllegalStateException("Unexpected call site type.");
/*   0*/    } 
/* 535*/    return newBlock;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isDirectCallNodeReplacementPossible(Node fnNode) {
/* 544*/    Node block = NodeUtil.getFunctionBody(fnNode);
/* 548*/    if (!block.hasChildren())
/* 550*/      return true; 
/* 551*/    if (block.hasOneChild())
/* 553*/      if (block.getFirstChild().isReturn() && block.getFirstChild().getFirstChild() != null)
/* 555*/        return true;  
/* 559*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  enum CanInlineResult {
/* 563*/    YES, AFTER_PREPARATION, NO;
/*   0*/  }
/*   0*/  
/*   0*/  private CanInlineResult canInlineReferenceAsStatementBlock(NodeTraversal t, Node callNode, Node fnNode, Set<String> namesToAlias) {
/* 581*/    CallSiteType callSiteType = classifyCallSite(callNode);
/* 582*/    if (callSiteType == CallSiteType.UNSUPPORTED)
/* 583*/      return CanInlineResult.NO; 
/* 586*/    if (!this.allowDecomposition && (callSiteType == CallSiteType.DECOMPOSABLE_EXPRESSION || callSiteType == CallSiteType.EXPRESSION))
/* 589*/      return CanInlineResult.NO; 
/* 592*/    if (!callMeetsBlockInliningRequirements(t, callNode, fnNode, namesToAlias))
/* 594*/      return CanInlineResult.NO; 
/* 597*/    if (callSiteType == CallSiteType.DECOMPOSABLE_EXPRESSION || callSiteType == CallSiteType.EXPRESSION)
/* 599*/      return CanInlineResult.AFTER_PREPARATION; 
/* 601*/    return CanInlineResult.YES;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean callMeetsBlockInliningRequirements(NodeTraversal t, Node callNode, final Node fnNode, Set<String> namesToAlias) {
/* 613*/    final boolean assumeMinimumCapture = this.assumeMinimumCapture;
/* 625*/    boolean fnContainsVars = NodeUtil.has(NodeUtil.getFunctionBody(fnNode), new NodeUtil.MatchDeclaration(), new NodeUtil.MatchShallowStatement());
/*   0*/    boolean forbidTemps = false;
/* 630*/    if (!t.inGlobalScope()) {
/* 631*/      Node fnCaller = t.getScopeRoot();
/* 632*/      Node fnCallerBody = fnCaller.getLastChild();
/* 636*/      Predicate<Node> match = new Predicate<Node>() {
/*   0*/          public boolean apply(Node n) {
/* 639*/            if (n.isName())
/* 640*/              return n.getString().equals("eval"); 
/* 642*/            if (!assumeMinimumCapture && n.isFunction())
/* 643*/              return (n != fnNode); 
/* 645*/            return false;
/*   0*/          }
/*   0*/        };
/* 648*/      forbidTemps = NodeUtil.has(fnCallerBody, match, NodeUtil.MATCH_NOT_FUNCTION);
/*   0*/    } 
/* 652*/    if (fnContainsVars && forbidTemps)
/* 653*/      return false; 
/* 658*/    if (forbidTemps) {
/* 659*/      Map<String, Node> args = FunctionArgumentInjector.getFunctionCallParameterMap(fnNode, callNode, this.safeNameIdSupplier);
/* 662*/      boolean hasArgs = !args.isEmpty();
/* 663*/      if (hasArgs) {
/* 665*/        Set<String> allNamesToAlias = Sets.newHashSet(namesToAlias);
/* 666*/        FunctionArgumentInjector.maybeAddTempsForCallArguments(fnNode, args, allNamesToAlias, this.compiler.getCodingConvention());
/* 668*/        if (!allNamesToAlias.isEmpty())
/* 669*/          return false; 
/*   0*/      } 
/*   0*/    } 
/* 674*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private CanInlineResult canInlineReferenceDirectly(Node callNode, Node fnNode) {
/* 691*/    if (!isDirectCallNodeReplacementPossible(fnNode))
/* 692*/      return CanInlineResult.NO; 
/* 695*/    Node block = fnNode.getLastChild();
/*   0*/    boolean hasSideEffects = false;
/* 698*/    if (block.hasChildren()) {
/* 699*/      Preconditions.checkState(block.hasOneChild());
/* 700*/      Node stmt = block.cloneNode();
/* 701*/      if (stmt.isReturn())
/* 702*/        hasSideEffects = NodeUtil.mayHaveSideEffects(stmt.getFirstChild(), this.compiler); 
/*   0*/    } 
/* 706*/    Node cArg = callNode.getFirstChild().getNext();
/* 711*/    if (!callNode.getFirstChild().isName())
/* 712*/      if (NodeUtil.isFunctionObjectCall(callNode)) {
/* 714*/        if (cArg == null || !cArg.isThis())
/* 715*/          return CanInlineResult.NO; 
/* 717*/        cArg = cArg.getNext();
/*   0*/      } else {
/* 720*/        Preconditions.checkState(!NodeUtil.isFunctionObjectApply(callNode));
/*   0*/      }  
/* 725*/    Node fnParam = NodeUtil.getFunctionParameters(fnNode).getFirstChild();
/* 726*/    while (cArg != null || fnParam != null) {
/* 728*/      if (fnParam != null) {
/* 729*/        if (cArg != null) {
/* 730*/          if (hasSideEffects && NodeUtil.canBeSideEffected(cArg))
/* 731*/            return CanInlineResult.NO; 
/* 736*/          if (NodeUtil.mayEffectMutableState(cArg, this.compiler) && NodeUtil.getNameReferenceCount(block, fnParam.getString()) > 1)
/* 739*/            return CanInlineResult.NO; 
/*   0*/        } 
/* 744*/        fnParam = fnParam.getNext();
/*   0*/      } 
/* 749*/      if (cArg != null) {
/* 750*/        if (NodeUtil.mayHaveSideEffects(cArg, this.compiler))
/* 751*/          return CanInlineResult.NO; 
/* 753*/        cArg = cArg.getNext();
/*   0*/      } 
/*   0*/    } 
/* 757*/    return CanInlineResult.YES;
/*   0*/  }
/*   0*/  
/*   0*/  boolean inliningLowersCost(JSModule fnModule, Node fnNode, Collection<? extends Reference> refs, Set<String> namesToAlias, boolean isRemovable, boolean referencesThis) {
/* 767*/    int referenceCount = refs.size();
/* 768*/    if (referenceCount == 0)
/* 769*/      return true; 
/* 772*/    int referencesUsingBlockInlining = 0;
/* 774*/    boolean checkModules = (isRemovable && fnModule != null);
/* 775*/    JSModuleGraph moduleGraph = this.compiler.getModuleGraph();
/* 777*/    for (Reference ref : refs) {
/* 778*/      if (ref.mode == InliningMode.BLOCK)
/* 779*/        referencesUsingBlockInlining++; 
/* 783*/      if (checkModules && ref.module != null && 
/* 784*/        ref.module != fnModule && !moduleGraph.dependsOn(ref.module, fnModule)) {
/* 788*/        isRemovable = false;
/* 789*/        checkModules = false;
/*   0*/      } 
/*   0*/    } 
/* 794*/    int referencesUsingDirectInlining = referenceCount - referencesUsingBlockInlining;
/* 803*/    if (referenceCount == 1 && isRemovable && referencesUsingDirectInlining == 1)
/* 805*/      return true; 
/* 808*/    int callCost = estimateCallCost(fnNode, referencesThis);
/* 809*/    int overallCallCost = callCost * referenceCount;
/* 811*/    int costDeltaDirect = inlineCostDelta(fnNode, namesToAlias, InliningMode.DIRECT);
/* 813*/    int costDeltaBlock = inlineCostDelta(fnNode, namesToAlias, InliningMode.BLOCK);
/* 816*/    return doesLowerCost(fnNode, overallCallCost, referencesUsingDirectInlining, costDeltaDirect, referencesUsingBlockInlining, costDeltaBlock, isRemovable);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean doesLowerCost(Node fnNode, int callCost, int directInlines, int costDeltaDirect, int blockInlines, int costDeltaBlock, boolean removable) {
/* 836*/    int fnInstanceCount = directInlines + blockInlines - (removable ? 1 : 0);
/* 838*/    if (fnInstanceCount == 0) {
/* 842*/      if (blockInlines > 0 && costDeltaBlock > 0)
/* 843*/        return false; 
/* 845*/      return true;
/*   0*/    } 
/* 848*/    int costDelta = directInlines * costDeltaDirect + blockInlines * costDeltaBlock;
/* 850*/    int threshold = (callCost - costDelta) / fnInstanceCount;
/* 852*/    return (InlineCostEstimator.getCost(fnNode, threshold + 1) <= threshold);
/*   0*/  }
/*   0*/  
/*   0*/  private static int estimateCallCost(Node fnNode, boolean referencesThis) {
/* 861*/    Node argsNode = NodeUtil.getFunctionParameters(fnNode);
/* 862*/    int numArgs = argsNode.getChildCount();
/* 864*/    int callCost = NAME_COST_ESTIMATE + 2;
/* 865*/    if (numArgs > 0)
/* 866*/      callCost += numArgs * NAME_COST_ESTIMATE + (numArgs - 1) * 1; 
/* 869*/    if (referencesThis)
/* 874*/      callCost += 10; 
/* 877*/    return callCost;
/*   0*/  }
/*   0*/  
/*   0*/  private static int inlineCostDelta(Node fnNode, Set<String> namesToAlias, InliningMode mode) {
/* 888*/    int paramCount = NodeUtil.getFunctionParameters(fnNode).getChildCount();
/* 889*/    int commaCount = (paramCount > 1) ? (paramCount - 1) : 0;
/* 890*/    int costDeltaFunctionOverhead = 15 + commaCount + paramCount * InlineCostEstimator.ESTIMATED_IDENTIFIER_COST;
/* 893*/    Node block = fnNode.getLastChild();
/* 894*/    if (!block.hasChildren())
/* 896*/      return -costDeltaFunctionOverhead; 
/* 899*/    if (mode == InliningMode.DIRECT)
/* 902*/      return -(costDeltaFunctionOverhead + 7); 
/* 904*/    int aliasCount = namesToAlias.size();
/* 915*/    int inlineBlockOverhead = 4;
/* 916*/    int perReturnOverhead = 2;
/* 917*/    int perReturnResultOverhead = 3;
/* 918*/    int perAliasOverhead = 3;
/* 923*/    int returnCount = NodeUtil.getNodeTypeReferenceCount(block, 4, new NodeUtil.MatchShallowStatement());
/* 925*/    int resultCount = (returnCount > 0) ? (returnCount - 1) : 0;
/* 926*/    int baseOverhead = (returnCount > 0) ? 4 : 0;
/* 928*/    int overhead = baseOverhead + returnCount * 2 + resultCount * 3 + aliasCount * 3;
/* 933*/    return overhead - costDeltaFunctionOverhead;
/*   0*/  }
/*   0*/  
/*   0*/  public void setKnownConstants(Set<String> knownConstants) {
/* 944*/    Preconditions.checkState(this.knownConstants.isEmpty());
/* 945*/    this.knownConstants = knownConstants;
/*   0*/  }
/*   0*/}
