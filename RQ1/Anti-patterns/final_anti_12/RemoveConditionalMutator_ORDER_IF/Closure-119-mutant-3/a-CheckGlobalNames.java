/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class CheckGlobalNames implements CompilerPass {
/*   0*/  private final AbstractCompiler compiler;
/*   0*/  
/*   0*/  private final CodingConvention convention;
/*   0*/  
/*   0*/  private final CheckLevel level;
/*   0*/  
/*  39*/  private GlobalNamespace namespace = null;
/*   0*/  
/*  40*/  private final Set<String> objectPrototypeProps = Sets.newHashSet();
/*   0*/  
/*  41*/  private final Set<String> functionPrototypeProps = Sets.newHashSet();
/*   0*/  
/*  44*/  static final DiagnosticType UNDEFINED_NAME_WARNING = DiagnosticType.warning("JSC_UNDEFINED_NAME", "{0} is never defined");
/*   0*/  
/*  48*/  static final DiagnosticType NAME_DEFINED_LATE_WARNING = DiagnosticType.warning("JSC_NAME_DEFINED_LATE", "{0} defined before its owner. {1} is defined at {2}:{3}");
/*   0*/  
/*  53*/  static final DiagnosticType STRICT_MODULE_DEP_QNAME = DiagnosticType.disabled("JSC_STRICT_MODULE_DEP_QNAME", "module {0} cannot reference {2}, defined in module {1}");
/*   0*/  
/*   0*/  CheckGlobalNames(AbstractCompiler compiler, CheckLevel level) {
/*  63*/    this.compiler = compiler;
/*  64*/    this.convention = compiler.getCodingConvention();
/*  65*/    this.level = level;
/*   0*/  }
/*   0*/  
/*   0*/  CheckGlobalNames injectNamespace(GlobalNamespace namespace) {
/*  73*/    Preconditions.checkArgument(namespace.hasExternsRoot());
/*  74*/    this.namespace = namespace;
/*  75*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void process(Node externs, Node root) {
/*  80*/    if (this.namespace == null)
/*  81*/      this.namespace = new GlobalNamespace(this.compiler, externs, root); 
/*  85*/    Preconditions.checkState(this.namespace.hasExternsRoot());
/*  86*/    findPrototypeProps("Object", this.objectPrototypeProps);
/*  87*/    findPrototypeProps("Function", this.functionPrototypeProps);
/*  88*/    this.objectPrototypeProps.addAll(this.convention.getIndirectlyDeclaredProperties());
/*  91*/    for (GlobalNamespace.Name name : this.namespace.getNameForest()) {
/*  97*/      if (name.inExterns)
/*   0*/        continue; 
/* 101*/      checkDescendantNames(name, (name.globalSets + name.localSets > 0));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void findPrototypeProps(String type, Set<String> props) {
/* 106*/    GlobalNamespace.Name slot = this.namespace.getSlot(type);
/* 107*/    if (slot != null)
/* 108*/      for (GlobalNamespace.Ref ref : slot.getRefs()) {
/* 109*/        if (ref.type == GlobalNamespace.Ref.Type.PROTOTYPE_GET) {
/* 110*/          Node fullName = ref.getNode().getParent().getParent();
/* 111*/          if (fullName.isGetProp())
/* 112*/            props.add(fullName.getLastChild().getString()); 
/*   0*/        } 
/*   0*/      }  
/*   0*/  }
/*   0*/  
/*   0*/  private void checkDescendantNames(GlobalNamespace.Name name, boolean nameIsDefined) {
/* 128*/    if (name.props != null)
/* 129*/      for (GlobalNamespace.Name prop : name.props) {
/*   0*/        boolean propIsDefined = false;
/* 133*/        if (nameIsDefined)
/* 136*/          propIsDefined = (!propertyMustBeInitializedByFullName(prop) || prop.globalSets + prop.localSets > 0); 
/* 140*/        validateName(prop, propIsDefined);
/* 141*/        checkDescendantNames(prop, propIsDefined);
/*   0*/      }  
/*   0*/  }
/*   0*/  
/*   0*/  private void validateName(GlobalNamespace.Name name, boolean isDefined) {
/* 149*/    GlobalNamespace.Ref declaration = name.getDeclaration();
/* 150*/    GlobalNamespace.Name parent = name.parent;
/* 152*/    JSModuleGraph moduleGraph = this.compiler.getModuleGraph();
/* 153*/    for (GlobalNamespace.Ref ref : name.getRefs()) {
/* 155*/      boolean isGlobalExpr = ref.getNode().getParent().isExprResult();
/* 157*/      if (!isDefined && !isTypedef(ref)) {
/* 158*/        if (!isGlobalExpr)
/* 159*/          reportRefToUndefinedName(name, ref); 
/*   0*/        continue;
/*   0*/      } 
/* 161*/      if (declaration != null && ref.getModule() != declaration.getModule() && !moduleGraph.dependsOn(ref.getModule(), declaration.getModule())) {
/* 165*/        reportBadModuleReference(name, ref);
/*   0*/        continue;
/*   0*/      } 
/* 168*/      if (ref.scope.isGlobal()) {
/* 171*/        boolean isPrototypeGet = (ref.type == GlobalNamespace.Ref.Type.PROTOTYPE_GET);
/* 172*/        GlobalNamespace.Name owner = isPrototypeGet ? name : parent;
/* 173*/        boolean singleGlobalParentDecl = (owner != null && owner.getDeclaration() != null && owner.localSets == 0);
/* 178*/        if (singleGlobalParentDecl && (owner.getDeclaration()).preOrderIndex > ref.preOrderIndex) {
/* 180*/          String refName = isPrototypeGet ? (name.getFullName() + ".prototype") : name.getFullName();
/* 183*/          this.compiler.report(JSError.make(ref.source.getName(), ref.node, NAME_DEFINED_LATE_WARNING, new String[] { refName, owner.getFullName(), (owner.getDeclaration()).source.getName(), String.valueOf((owner.getDeclaration()).node.getLineno()) }));
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isTypedef(GlobalNamespace.Ref ref) {
/* 198*/    Node parent = ref.node.getParent();
/* 199*/    if (parent.isExprResult()) {
/* 200*/      JSDocInfo info = ref.node.getJSDocInfo();
/* 201*/      if (info != null && info.hasTypedefType())
/* 202*/        return true; 
/*   0*/    } 
/* 205*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private void reportBadModuleReference(GlobalNamespace.Name name, GlobalNamespace.Ref ref) {
/* 209*/    this.compiler.report(JSError.make(ref.source.getName(), ref.node, STRICT_MODULE_DEP_QNAME, new String[] { ref.getModule().getName(), name.getDeclaration().getModule().getName(), name.getFullName() }));
/*   0*/  }
/*   0*/  
/*   0*/  private void reportRefToUndefinedName(GlobalNamespace.Name name, GlobalNamespace.Ref ref) {
/* 218*/    while (name.parent != null && name.parent.globalSets + name.parent.localSets == 0)
/* 220*/      name = name.parent; 
/* 223*/    this.compiler.report(JSError.make(ref.getSourceName(), ref.node, this.level, UNDEFINED_NAME_WARNING, new String[] { name.getFullName() }));
/*   0*/  }
/*   0*/  
/*   0*/  private boolean propertyMustBeInitializedByFullName(GlobalNamespace.Name name) {
/* 243*/    if (name.parent == null)
/* 244*/      return false; 
/*   0*/    boolean parentIsAliased = false;
/* 248*/    if (name.parent.aliasingGets > 0)
/* 249*/      for (GlobalNamespace.Ref ref : name.parent.getRefs()) {
/* 250*/        if (ref.type == GlobalNamespace.Ref.Type.ALIASING_GET) {
/* 251*/          Node aliaser = ref.getNode().getParent();
/* 256*/          boolean isKnownAlias = (aliaser.isCall() && (this.convention.getClassesDefinedByCall(aliaser) != null || this.convention.getSingletonGetterClassName(aliaser) != null));
/* 260*/          if (!isKnownAlias)
/* 261*/            parentIsAliased = true; 
/*   0*/        } 
/*   0*/      }  
/* 267*/    if (parentIsAliased)
/* 268*/      return false; 
/* 271*/    if (this.objectPrototypeProps.contains(name.getBaseName()))
/* 272*/      return false; 
/* 275*/    if (name.parent.type == GlobalNamespace.Name.Type.OBJECTLIT)
/* 276*/      return true; 
/* 279*/    if (name.parent.type == GlobalNamespace.Name.Type.FUNCTION && name.parent.isDeclaredType() && !this.functionPrototypeProps.contains(name.getBaseName()))
/* 282*/      return true; 
/* 285*/    return false;
/*   0*/  }
/*   0*/}
