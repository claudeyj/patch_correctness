/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.ArrayListMultimap;
/*   0*/import com.google.common.collect.Lists;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Multimap;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.IR;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class RemoveUnusedVars implements CompilerPass, OptimizeCalls.CallGraphCompilerPass {
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final CodingConvention codingConvention;
/*   0*/  
/*   0*/  private final boolean removeGlobals;
/*   0*/  
/*   0*/  private boolean preserveFunctionExpressionNames;
/*   0*/  
/*  83*/  private final Set<Scope.Var> referenced = Sets.newHashSet();
/*   0*/  
/*  88*/  private final List<Scope.Var> maybeUnreferenced = Lists.newArrayList();
/*   0*/  
/*  93*/  private final List<Scope> allFunctionScopes = Lists.newArrayList();
/*   0*/  
/*  98*/  private final Multimap<Scope.Var, Assign> assignsByVar = (Multimap<Scope.Var, Assign>)ArrayListMultimap.create();
/*   0*/  
/* 104*/  private final Map<Node, Assign> assignsByNode = Maps.newHashMap();
/*   0*/  
/* 109*/  private final Multimap<Scope.Var, Node> inheritsCalls = (Multimap<Scope.Var, Node>)ArrayListMultimap.create();
/*   0*/  
/* 116*/  private final Multimap<Scope.Var, Continuation> continuations = (Multimap<Scope.Var, Continuation>)ArrayListMultimap.create();
/*   0*/  
/*   0*/  private boolean modifyCallSites;
/*   0*/  
/*   0*/  private CallSiteOptimizer callSiteOptimizer;
/*   0*/  
/*   0*/  RemoveUnusedVars(AbstractCompiler compiler, boolean removeGlobals, boolean preserveFunctionExpressionNames, boolean modifyCallSites) {
/* 128*/    this.compiler = compiler;
/* 129*/    this.codingConvention = compiler.getCodingConvention();
/* 130*/    this.removeGlobals = removeGlobals;
/* 131*/    this.preserveFunctionExpressionNames = preserveFunctionExpressionNames;
/* 132*/    this.modifyCallSites = modifyCallSites;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/* 141*/    Preconditions.checkState(this.compiler.getLifeCycleStage().isNormalized());
/* 142*/    SimpleDefinitionFinder defFinder = null;
/* 144*/    if (this.modifyCallSites) {
/* 146*/      defFinder = new SimpleDefinitionFinder(this.compiler);
/* 147*/      defFinder.process(externs, root);
/*   0*/    } 
/* 149*/    process(externs, root, defFinder);
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root, SimpleDefinitionFinder defFinder) {
/* 155*/    if (this.modifyCallSites) {
/* 156*/      Preconditions.checkNotNull(defFinder);
/* 157*/      this.callSiteOptimizer = new CallSiteOptimizer(this.compiler, defFinder);
/*   0*/    } 
/* 159*/    traverseAndRemoveUnusedReferences(root);
/* 160*/    if (this.callSiteOptimizer != null)
/* 161*/      this.callSiteOptimizer.applyChanges(); 
/*   0*/  }
/*   0*/  
/*   0*/  private void traverseAndRemoveUnusedReferences(Node root) {
/* 169*/    Scope scope = new SyntacticScopeCreator(this.compiler).createScope(root, null);
/* 170*/    traverseNode(root, null, scope);
/* 172*/    if (this.removeGlobals)
/* 173*/      collectMaybeUnreferencedVars(scope); 
/* 176*/    interpretAssigns();
/* 177*/    removeUnreferencedVars();
/* 178*/    for (Scope fnScope : this.allFunctionScopes)
/* 179*/      removeUnreferencedFunctionArgs(fnScope); 
/*   0*/  }
/*   0*/  
/*   0*/  private void traverseNode(Node n, Node parent, Scope scope) {
/*   0*/    Assign maybeAssign;
/*   0*/    CodingConvention.SubclassRelationship subclassRelationship;
/* 193*/    int type = n.getType();
/* 194*/    Scope.Var var = null;
/* 195*/    switch (type) {
/*   0*/      case 105:
/* 199*/        if (NodeUtil.isFunctionDeclaration(n))
/* 200*/          var = scope.getVar(n.getFirstChild().getString()); 
/* 203*/        if (var != null && isRemovableVar(var)) {
/* 204*/          this.continuations.put(var, new Continuation(n, scope));
/*   0*/        } else {
/* 206*/          traverseFunction(n, scope);
/*   0*/        } 
/*   0*/        return;
/*   0*/      case 86:
/* 211*/        maybeAssign = Assign.maybeCreateAssign(n);
/* 212*/        if (maybeAssign != null) {
/* 215*/          var = scope.getVar(maybeAssign.nameNode.getString());
/* 216*/          if (var != null) {
/* 217*/            this.assignsByVar.put(var, maybeAssign);
/* 218*/            this.assignsByNode.put(maybeAssign.nameNode, maybeAssign);
/* 220*/            if (isRemovableVar(var) && !maybeAssign.mayHaveSecondarySideEffects) {
/* 225*/              this.continuations.put(var, new Continuation(n, scope));
/*   0*/              return;
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 37:
/* 234*/        subclassRelationship = this.codingConvention.getClassesDefinedByCall(n);
/* 236*/        if (subclassRelationship != null) {
/* 237*/          Scope.Var subclassVar = scope.getVar(subclassRelationship.subclassName);
/* 242*/          if (subclassVar != null && subclassVar.isGlobal() && !this.referenced.contains(subclassVar)) {
/* 245*/            this.inheritsCalls.put(subclassVar, parent);
/* 246*/            this.continuations.put(subclassVar, new Continuation(n, scope));
/*   0*/            return;
/*   0*/          } 
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 38:
/* 253*/        var = scope.getVar(n.getString());
/* 254*/        if (parent.isVar()) {
/* 255*/          Node value = n.getFirstChild();
/* 256*/          if (value != null && var != null && isRemovableVar(var) && !NodeUtil.mayHaveSideEffects(value)) {
/* 261*/            this.continuations.put(var, new Continuation(n, scope));
/*   0*/            return;
/*   0*/          } 
/*   0*/          break;
/*   0*/        } 
/* 268*/        if ("arguments".equals(n.getString()) && scope.isLocal()) {
/* 269*/          Node lp = scope.getRootNode().getFirstChild().getNext();
/* 270*/          for (Node a = lp.getFirstChild(); a != null; a = a.getNext())
/* 271*/            markReferencedVar(scope.getVar(a.getString())); 
/*   0*/        } 
/* 277*/        if (var != null) {
/* 281*/          if (isRemovableVar(var)) {
/* 282*/            if (!this.assignsByNode.containsKey(n))
/* 283*/              markReferencedVar(var); 
/*   0*/            break;
/*   0*/          } 
/* 286*/          markReferencedVar(var);
/*   0*/        } 
/*   0*/        break;
/*   0*/    } 
/* 293*/    for (Node c = n.getFirstChild(); c != null; c = c.getNext())
/* 294*/      traverseNode(c, n, scope); 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isRemovableVar(Scope.Var var) {
/* 300*/    if (!this.removeGlobals && var.isGlobal())
/* 301*/      return false; 
/* 305*/    if (this.referenced.contains(var))
/* 306*/      return false; 
/* 310*/    if (this.codingConvention.isExported(var.getName()))
/* 311*/      return false; 
/* 314*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void traverseFunction(Node n, Scope parentScope) {
/* 326*/    Preconditions.checkState((n.getChildCount() == 3));
/* 327*/    Preconditions.checkState(n.isFunction());
/* 329*/    Node body = n.getLastChild();
/* 330*/    Preconditions.checkState((body.getNext() == null && body.isBlock()));
/* 333*/    Scope fnScope = new SyntacticScopeCreator(this.compiler).createScope(n, parentScope);
/* 335*/    traverseNode(body, n, fnScope);
/* 337*/    collectMaybeUnreferencedVars(fnScope);
/* 338*/    this.allFunctionScopes.add(fnScope);
/*   0*/  }
/*   0*/  
/*   0*/  private void collectMaybeUnreferencedVars(Scope scope) {
/* 346*/    for (Iterator<Scope.Var> it = scope.getVars(); it.hasNext(); ) {
/* 347*/      Scope.Var var = it.next();
/* 348*/      if (isRemovableVar(var))
/* 349*/        this.maybeUnreferenced.add(var); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void removeUnreferencedFunctionArgs(Scope fnScope) {
/* 363*/    Node function = fnScope.getRootNode();
/* 365*/    Preconditions.checkState(function.isFunction());
/* 366*/    if (NodeUtil.isGetOrSetKey(function.getParent()))
/*   0*/      return; 
/* 371*/    Node argList = getFunctionArgList(function);
/* 372*/    boolean modifyCallers = (this.modifyCallSites && this.callSiteOptimizer.canModifyCallers(function));
/* 374*/    if (!modifyCallers) {
/*   0*/      Node lastArg;
/* 377*/      while ((lastArg = argList.getLastChild()) != null) {
/* 378*/        Scope.Var var = fnScope.getVar(lastArg.getString());
/* 379*/        if (!this.referenced.contains(var)) {
/* 380*/          argList.removeChild(lastArg);
/* 381*/          this.compiler.reportCodeChange();
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/* 387*/      this.callSiteOptimizer.optimize(fnScope, this.referenced);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static Node getFunctionArgList(Node function) {
/* 396*/    return function.getFirstChild().getNext();
/*   0*/  }
/*   0*/  
/*   0*/  private static class CallSiteOptimizer {
/*   0*/    private final AbstractCompiler compiler;
/*   0*/    
/*   0*/    private final SimpleDefinitionFinder defFinder;
/*   0*/    
/* 402*/    private final List<Node> toRemove = Lists.newArrayList();
/*   0*/    
/* 403*/    private final List<Node> toReplaceWithZero = Lists.newArrayList();
/*   0*/    
/*   0*/    CallSiteOptimizer(AbstractCompiler compiler, SimpleDefinitionFinder defFinder) {
/* 408*/      this.compiler = compiler;
/* 409*/      this.defFinder = defFinder;
/*   0*/    }
/*   0*/    
/*   0*/    public void optimize(Scope fnScope, Set<Scope.Var> referenced) {
/* 413*/      Node function = fnScope.getRootNode();
/* 414*/      Preconditions.checkState(function.isFunction());
/* 415*/      Node argList = RemoveUnusedVars.getFunctionArgList(function);
/* 419*/      boolean changeCallSignature = canChangeSignature(function);
/* 420*/      markUnreferencedFunctionArgs(fnScope, function, referenced, argList.getFirstChild(), 0, changeCallSignature);
/*   0*/    }
/*   0*/    
/*   0*/    public void applyChanges() {
/* 429*/      for (Node n : this.toRemove) {
/* 430*/        n.getParent().removeChild(n);
/* 431*/        this.compiler.reportCodeChange();
/*   0*/      } 
/* 433*/      for (Node n : this.toReplaceWithZero) {
/* 434*/        n.getParent().replaceChild(n, IR.number(0.0D).srcref(n));
/* 435*/        this.compiler.reportCodeChange();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean markUnreferencedFunctionArgs(Scope scope, Node function, Set<Scope.Var> referenced, Node param, int paramIndex, boolean canChangeSignature) {
/* 456*/      if (param != null) {
/* 458*/        boolean hasFollowing = markUnreferencedFunctionArgs(scope, function, referenced, param.getNext(), paramIndex + 1, canChangeSignature);
/* 462*/        Scope.Var var = scope.getVar(param.getString());
/* 463*/        if (!referenced.contains(var)) {
/* 464*/          Preconditions.checkNotNull(var);
/* 468*/          boolean modifyAllCallSites = (canChangeSignature || !hasFollowing);
/* 469*/          if (modifyAllCallSites)
/* 470*/            modifyAllCallSites = canRemoveArgFromCallSites(function, paramIndex); 
/* 474*/          tryRemoveArgFromCallSites(function, paramIndex, modifyAllCallSites);
/* 478*/          if (modifyAllCallSites || !hasFollowing) {
/* 479*/            this.toRemove.add(param);
/* 480*/            return hasFollowing;
/*   0*/          } 
/*   0*/        } 
/* 483*/        return true;
/*   0*/      } 
/* 487*/      tryRemoveAllFollowingArgs(function, paramIndex - 1);
/* 488*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean canRemoveArgFromCallSites(Node function, int argIndex) {
/* 498*/      DefinitionsRemover.Definition definition = getFunctionDefinition(function);
/* 501*/      for (UseSite site : this.defFinder.getUseSites(definition)) {
/* 502*/        if (isModifiableCallSite(site)) {
/* 503*/          Node arg = getArgumentForCallOrNewOrDotCall(site, argIndex);
/* 506*/          if (arg != null && NodeUtil.mayHaveSideEffects(arg, this.compiler))
/* 507*/            return false; 
/*   0*/          continue;
/*   0*/        } 
/* 510*/        return false;
/*   0*/      } 
/* 514*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private void tryRemoveArgFromCallSites(Node function, int argIndex, boolean canModifyAllSites) {
/* 523*/      DefinitionsRemover.Definition definition = getFunctionDefinition(function);
/* 525*/      for (UseSite site : this.defFinder.getUseSites(definition)) {
/* 526*/        if (isModifiableCallSite(site)) {
/* 527*/          Node arg = getArgumentForCallOrNewOrDotCall(site, argIndex);
/* 528*/          if (arg != null) {
/* 529*/            Node argParent = arg.getParent();
/* 532*/            if (canModifyAllSites || (arg.getNext() == null && !NodeUtil.mayHaveSideEffects(arg, this.compiler))) {
/* 535*/              this.toRemove.add(arg);
/*   0*/              continue;
/*   0*/            } 
/* 538*/            if (!NodeUtil.mayHaveSideEffects(arg, this.compiler) && (!arg.isNumber() || arg.getDouble() != 0.0D))
/* 540*/              this.toReplaceWithZero.add(arg); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void tryRemoveAllFollowingArgs(Node function, int argIndex) {
/* 552*/      DefinitionsRemover.Definition definition = getFunctionDefinition(function);
/* 553*/      for (UseSite site : this.defFinder.getUseSites(definition)) {
/* 554*/        if (!isModifiableCallSite(site))
/*   0*/          continue; 
/* 557*/        Node arg = getArgumentForCallOrNewOrDotCall(site, argIndex + 1);
/* 558*/        while (arg != null) {
/* 559*/          if (!NodeUtil.mayHaveSideEffects(arg))
/* 560*/            this.toRemove.add(arg); 
/* 562*/          arg = arg.getNext();
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private static Node getArgumentForCallOrNewOrDotCall(UseSite site, int argIndex) {
/* 573*/      int adjustedArgIndex = argIndex;
/* 574*/      Node parent = site.node.getParent();
/* 575*/      if (NodeUtil.isFunctionObjectCall(parent))
/* 576*/        adjustedArgIndex++; 
/* 578*/      return NodeUtil.getArgumentForCallOrNew(parent, adjustedArgIndex);
/*   0*/    }
/*   0*/    
/*   0*/    boolean canModifyCallers(Node function) {
/* 586*/      if (NodeUtil.isVarArgsFunction(function))
/* 587*/        return false; 
/* 590*/      DefinitionSite defSite = this.defFinder.getDefinitionForFunction(function);
/* 591*/      if (defSite == null)
/* 592*/        return false; 
/* 595*/      DefinitionsRemover.Definition definition = defSite.definition;
/* 599*/      if (!SimpleDefinitionFinder.isSimpleFunctionDeclaration(function))
/* 600*/        return false; 
/* 603*/      return this.defFinder.canModifyDefinition(definition);
/*   0*/    }
/*   0*/    
/*   0*/    private static boolean isModifiableCallSite(UseSite site) {
/* 611*/      return (SimpleDefinitionFinder.isCallOrNewSite(site) && !NodeUtil.isFunctionObjectApply(site.node.getParent()));
/*   0*/    }
/*   0*/    
/*   0*/    private boolean canChangeSignature(Node function) {
/* 620*/      DefinitionsRemover.Definition definition = getFunctionDefinition(function);
/* 621*/      CodingConvention convention = this.compiler.getCodingConvention();
/* 623*/      Preconditions.checkState(!definition.isExtern());
/* 625*/      Collection<UseSite> useSites = this.defFinder.getUseSites(definition);
/* 626*/      for (UseSite site : useSites) {
/* 627*/        Node parent = site.node.getParent();
/* 633*/        if (parent == null)
/*   0*/          continue; 
/* 638*/        if (parent.isCall() && convention.getClassesDefinedByCall(parent) != null)
/*   0*/          continue; 
/* 644*/        if (!SimpleDefinitionFinder.isCallOrNewSite(site) && (
/* 645*/          !parent.isGetProp() || !NodeUtil.isFunctionObjectCall(parent.getParent())))
/* 647*/          return false; 
/* 651*/        if (NodeUtil.isFunctionObjectApply(parent))
/* 652*/          return false; 
/* 659*/        Node nameNode = site.node;
/* 660*/        Collection<DefinitionsRemover.Definition> singleSiteDefinitions = this.defFinder.getDefinitionsReferencedAt(nameNode);
/* 662*/        Preconditions.checkState((singleSiteDefinitions.size() == 1));
/* 663*/        Preconditions.checkState(singleSiteDefinitions.contains(definition));
/*   0*/      } 
/* 666*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private DefinitionsRemover.Definition getFunctionDefinition(Node function) {
/* 674*/      DefinitionSite definitionSite = this.defFinder.getDefinitionForFunction(function);
/* 676*/      Preconditions.checkNotNull(definitionSite);
/* 677*/      DefinitionsRemover.Definition definition = definitionSite.definition;
/* 678*/      Preconditions.checkState(!definitionSite.inExterns);
/* 679*/      Preconditions.checkState((definition.getRValue() == function));
/* 680*/      return definition;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private void interpretAssigns() {
/*   0*/    boolean changes = false;
/*   0*/    do {
/* 707*/      changes = false;
/* 712*/      for (int current = 0; current < this.maybeUnreferenced.size(); current++) {
/* 713*/        Scope.Var var = this.maybeUnreferenced.get(current);
/* 714*/        if (this.referenced.contains(var)) {
/* 715*/          this.maybeUnreferenced.remove(current);
/* 716*/          current--;
/*   0*/        } else {
/*   0*/          boolean assignedToUnknownValue = false;
/*   0*/          boolean hasPropertyAssign = false;
/* 721*/          if (var.getParentNode().isVar() && !NodeUtil.isForIn(var.getParentNode().getParent())) {
/* 723*/            Node value = var.getInitialValue();
/* 724*/            assignedToUnknownValue = (value != null && !NodeUtil.isLiteralValue(value, true));
/*   0*/          } else {
/* 729*/            assignedToUnknownValue = true;
/*   0*/          } 
/* 732*/          for (Assign assign : (Iterable<Assign>)this.assignsByVar.get(var)) {
/* 733*/            if (assign.isPropertyAssign) {
/* 734*/              hasPropertyAssign = true;
/*   0*/              continue;
/*   0*/            } 
/* 735*/            if (!NodeUtil.isLiteralValue(assign.assignNode.cloneNode(), true))
/* 737*/              assignedToUnknownValue = true; 
/*   0*/          } 
/* 741*/          if (assignedToUnknownValue && hasPropertyAssign) {
/* 742*/            changes = (markReferencedVar(var) || changes);
/* 743*/            this.maybeUnreferenced.remove(current);
/* 744*/            current--;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 748*/    } while (changes);
/*   0*/  }
/*   0*/  
/*   0*/  private void removeAllAssigns(Scope.Var var) {
/* 755*/    for (Assign assign : (Iterable<Assign>)this.assignsByVar.get(var)) {
/* 756*/      assign.remove();
/* 757*/      this.compiler.reportCodeChange();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean markReferencedVar(Scope.Var var) {
/* 767*/    if (this.referenced.add(var)) {
/* 768*/      for (Continuation c : (Iterable<Continuation>)this.continuations.get(var))
/* 769*/        c.apply(); 
/* 771*/      return true;
/*   0*/    } 
/* 773*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void removeUnreferencedVars() {
/* 781*/    CodingConvention convention = this.codingConvention;
/* 783*/    for (Scope.Var var : this.maybeUnreferenced) {
/* 788*/      for (Node exprCallNode : (Iterable<Node>)this.inheritsCalls.get(var)) {
/* 789*/        NodeUtil.removeChild(exprCallNode.getParent(), exprCallNode);
/* 790*/        this.compiler.reportCodeChange();
/*   0*/      } 
/* 796*/      removeAllAssigns(var);
/* 798*/      this.compiler.addToDebugLog("Unreferenced var: " + var.name);
/* 799*/      Node nameNode = var.nameNode;
/* 800*/      Node toRemove = nameNode.getParent();
/* 801*/      Node parent = toRemove.getParent();
/* 803*/      Preconditions.checkState((toRemove.isVar() || toRemove.isFunction() || (toRemove.isParamList() && parent.isFunction())), "We should only declare vars and functions and function args");
/* 810*/      if (toRemove.isParamList() && parent.isFunction())
/*   0*/        continue; 
/* 814*/      if (NodeUtil.isFunctionExpression(toRemove)) {
/* 815*/        if (!this.preserveFunctionExpressionNames) {
/* 816*/          toRemove.getFirstChild().setString("");
/* 817*/          this.compiler.reportCodeChange();
/*   0*/        } 
/*   0*/        continue;
/*   0*/      } 
/* 820*/      if (parent != null && parent.isFor() && parent.getChildCount() < 4)
/*   0*/        continue; 
/* 824*/      if (toRemove.isVar() && nameNode.hasChildren() && NodeUtil.mayHaveSideEffects(nameNode.getFirstChild())) {
/* 830*/        if (toRemove.getChildCount() == 1) {
/* 831*/          parent.replaceChild(toRemove, IR.exprResult(nameNode.removeFirstChild()));
/* 833*/          this.compiler.reportCodeChange();
/*   0*/        } 
/*   0*/        continue;
/*   0*/      } 
/* 835*/      if (toRemove.isVar() && toRemove.getChildCount() > 1) {
/* 839*/        toRemove.removeChild(nameNode);
/* 840*/        this.compiler.reportCodeChange();
/*   0*/        continue;
/*   0*/      } 
/* 841*/      if (parent != null) {
/* 842*/        NodeUtil.removeChild(parent, toRemove);
/* 843*/        this.compiler.reportCodeChange();
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private class Continuation {
/*   0*/    private final Node node;
/*   0*/    
/*   0*/    private final Scope scope;
/*   0*/    
/*   0*/    Continuation(Node node, Scope scope) {
/* 858*/      this.node = node;
/* 859*/      this.scope = scope;
/*   0*/    }
/*   0*/    
/*   0*/    void apply() {
/* 863*/      if (NodeUtil.isFunctionDeclaration(this.node)) {
/* 864*/        RemoveUnusedVars.this.traverseFunction(this.node, this.scope);
/*   0*/      } else {
/* 866*/        Node child = this.node.getFirstChild();
/* 867*/        for (; child != null; child = child.getNext())
/* 868*/          RemoveUnusedVars.this.traverseNode(child, this.node, this.scope); 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class Assign {
/*   0*/    final Node assignNode;
/*   0*/    
/*   0*/    final Node nameNode;
/*   0*/    
/*   0*/    final boolean isPropertyAssign;
/*   0*/    
/*   0*/    final boolean mayHaveSecondarySideEffects;
/*   0*/    
/*   0*/    final boolean maybeAliased;
/*   0*/    
/*   0*/    Assign(Node assignNode, Node nameNode, boolean isPropertyAssign) {
/* 899*/      Preconditions.checkState(NodeUtil.isAssignmentOp(assignNode));
/* 900*/      this.assignNode = assignNode;
/* 901*/      this.nameNode = nameNode;
/* 902*/      this.isPropertyAssign = isPropertyAssign;
/* 904*/      this.maybeAliased = !assignNode.getParent().isExprResult();
/* 905*/      this.mayHaveSecondarySideEffects = (this.maybeAliased || NodeUtil.mayHaveSideEffects(assignNode.getFirstChild()) || NodeUtil.mayHaveSideEffects(assignNode.getLastChild()));
/*   0*/    }
/*   0*/    
/*   0*/    static Assign maybeCreateAssign(Node assignNode) {
/* 916*/      Preconditions.checkState(NodeUtil.isAssignmentOp(assignNode));
/*   0*/      boolean isPropAssign = false;
/* 924*/      Node current = assignNode.getFirstChild();
/* 925*/      if (NodeUtil.isGet(current)) {
/* 926*/        current = current.getFirstChild();
/* 927*/        isPropAssign = true;
/* 929*/        if (current.isGetProp() && current.getLastChild().getString().equals("prototype"))
/* 933*/          current = current.getFirstChild(); 
/*   0*/      } 
/* 937*/      if (current.isName())
/* 938*/        return new Assign(assignNode, current, isPropAssign); 
/* 940*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    void remove() {
/* 947*/      Node parent = this.assignNode.getParent();
/* 948*/      if (this.mayHaveSecondarySideEffects) {
/* 949*/        Node replacement = this.assignNode.getLastChild().detachFromParent();
/* 952*/        Node current = this.assignNode.getFirstChild();
/* 953*/        for (; !current.isName(); 
/* 954*/          current = current.getFirstChild()) {
/* 955*/          if (current.isGetElem()) {
/* 956*/            replacement = IR.comma(current.getLastChild().detachFromParent(), replacement);
/* 958*/            replacement.copyInformationFrom(current);
/*   0*/          } 
/*   0*/        } 
/* 962*/        parent.replaceChild(this.assignNode, replacement);
/*   0*/      } else {
/* 964*/        Node gramps = parent.getParent();
/* 965*/        if (parent.isExprResult()) {
/* 966*/          gramps.removeChild(parent);
/*   0*/        } else {
/* 968*/          parent.replaceChild(this.assignNode, this.assignNode.getLastChild().detachFromParent());
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/}
