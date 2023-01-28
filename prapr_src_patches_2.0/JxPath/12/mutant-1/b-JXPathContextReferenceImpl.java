/*   0*/package org.apache.commons.jxpath.ri;
/*   0*/
/*   0*/import java.lang.ref.SoftReference;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.Map;
/*   0*/import java.util.Vector;
/*   0*/import org.apache.commons.jxpath.CompiledExpression;
/*   0*/import org.apache.commons.jxpath.Function;
/*   0*/import org.apache.commons.jxpath.Functions;
/*   0*/import org.apache.commons.jxpath.JXPathContext;
/*   0*/import org.apache.commons.jxpath.JXPathException;
/*   0*/import org.apache.commons.jxpath.JXPathFunctionNotFoundException;
/*   0*/import org.apache.commons.jxpath.JXPathInvalidSyntaxException;
/*   0*/import org.apache.commons.jxpath.JXPathNotFoundException;
/*   0*/import org.apache.commons.jxpath.JXPathTypeConversionException;
/*   0*/import org.apache.commons.jxpath.Pointer;
/*   0*/import org.apache.commons.jxpath.ri.axes.InitialContext;
/*   0*/import org.apache.commons.jxpath.ri.axes.RootContext;
/*   0*/import org.apache.commons.jxpath.ri.compiler.Expression;
/*   0*/import org.apache.commons.jxpath.ri.compiler.LocationPath;
/*   0*/import org.apache.commons.jxpath.ri.compiler.TreeCompiler;
/*   0*/import org.apache.commons.jxpath.ri.model.NodePointer;
/*   0*/import org.apache.commons.jxpath.ri.model.NodePointerFactory;
/*   0*/import org.apache.commons.jxpath.ri.model.VariablePointerFactory;
/*   0*/import org.apache.commons.jxpath.ri.model.beans.BeanPointerFactory;
/*   0*/import org.apache.commons.jxpath.ri.model.beans.CollectionPointerFactory;
/*   0*/import org.apache.commons.jxpath.ri.model.container.ContainerPointerFactory;
/*   0*/import org.apache.commons.jxpath.ri.model.dynamic.DynamicPointerFactory;
/*   0*/import org.apache.commons.jxpath.util.ReverseComparator;
/*   0*/import org.apache.commons.jxpath.util.TypeUtils;
/*   0*/
/*   0*/public class JXPathContextReferenceImpl extends JXPathContext {
/*   0*/  public static final boolean USE_SOFT_CACHE = true;
/*   0*/  
/*  70*/  private static final Compiler COMPILER = new TreeCompiler();
/*   0*/  
/*  71*/  private static Map compiled = new HashMap();
/*   0*/  
/*  72*/  private static int cleanupCount = 0;
/*   0*/  
/*  74*/  private static final Vector nodeFactories = new Vector();
/*   0*/  
/*  75*/  private static NodePointerFactory[] nodeFactoryArray = null;
/*   0*/  
/*   0*/  private Pointer rootPointer;
/*   0*/  
/*   0*/  private Pointer contextPointer;
/*   0*/  
/*   0*/  protected NamespaceResolver namespaceResolver;
/*   0*/  
/*   0*/  private static final int CLEANUP_THRESHOLD = 500;
/*   0*/  
/*   0*/  static {
/*  77*/    nodeFactories.add(new CollectionPointerFactory());
/*  78*/    nodeFactories.add(new BeanPointerFactory());
/*  79*/    nodeFactories.add(new DynamicPointerFactory());
/*  80*/    nodeFactories.add(new VariablePointerFactory());
/*  83*/    Object domFactory = allocateConditionally("org.apache.commons.jxpath.ri.model.dom.DOMPointerFactory", "org.w3c.dom.Node");
/*  86*/    if (domFactory != null) {
/*  87*/        nodeFactories.add(domFactory); 
/*   0*/       }
/*  91*/    Object jdomFactory = allocateConditionally("org.apache.commons.jxpath.ri.model.jdom.JDOMPointerFactory", "org.jdom.Document");
/*  94*/    if (jdomFactory != null) {
/*  95*/        nodeFactories.add(jdomFactory); 
/*   0*/       }
/*  99*/    Object dynaBeanFactory = allocateConditionally("org.apache.commons.jxpath.ri.model.dynabeans.DynaBeanPointerFactory", "org.apache.commons.beanutils.DynaBean");
/* 104*/    if (dynaBeanFactory != null) {
/* 105*/        nodeFactories.add(dynaBeanFactory); 
/*   0*/       }
/* 108*/    nodeFactories.add(new ContainerPointerFactory());
/* 109*/    createNodeFactoryArray();
/*   0*/  }
/*   0*/  
/*   0*/  protected JXPathContextReferenceImpl(JXPathContext parentContext, Object contextBean) {
/* 123*/    this(parentContext, contextBean, null);
/*   0*/  }
/*   0*/  
/*   0*/  public JXPathContextReferenceImpl(JXPathContext parentContext, Object contextBean, Pointer contextPointer) {
/* 131*/    super(parentContext, contextBean);
/* 133*/    synchronized (nodeFactories) {
/* 134*/      createNodeFactoryArray();
/*   0*/    } 
/* 137*/    if (contextPointer != null) {
/* 138*/      this.contextPointer = contextPointer;
/* 139*/      this.rootPointer = NodePointer.newNodePointer(new QName(null, "root"), contextPointer.getRootNode(), getLocale());
/*   0*/    } else {
/* 146*/      this.contextPointer = NodePointer.newNodePointer(new QName(null, "root"), contextBean, getLocale());
/* 151*/      this.rootPointer = this.contextPointer;
/*   0*/    } 
/* 154*/    NamespaceResolver parentNR = null;
/* 155*/    if (parentContext instanceof JXPathContextReferenceImpl) {
/* 156*/        parentNR = ((JXPathContextReferenceImpl)parentContext).getNamespaceResolver(); 
/*   0*/       }
/* 158*/    this.namespaceResolver = new NamespaceResolver(parentNR);
/* 159*/    this.namespaceResolver.setNamespaceContextPointer((NodePointer)this.contextPointer);
/*   0*/  }
/*   0*/  
/*   0*/  private static void createNodeFactoryArray() {
/* 164*/    if (nodeFactoryArray == null) {
/* 165*/      nodeFactoryArray = (NodePointerFactory[])nodeFactories.toArray((Object[])new NodePointerFactory[nodeFactories.size()]);
/* 168*/      Arrays.sort(nodeFactoryArray, new Comparator() {
/*   0*/            public int compare(Object a, Object b) {
/* 170*/              int orderA = ((NodePointerFactory)a).getOrder();
/* 171*/              int orderB = ((NodePointerFactory)b).getOrder();
/* 172*/              return orderA - orderB;
/*   0*/            }
/*   0*/          });
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static void addNodePointerFactory(NodePointerFactory factory) {
/* 184*/    synchronized (nodeFactories) {
/* 185*/      nodeFactories.add(factory);
/* 186*/      nodeFactoryArray = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static NodePointerFactory[] getNodePointerFactories() {
/* 191*/    return nodeFactoryArray;
/*   0*/  }
/*   0*/  
/*   0*/  protected Compiler getCompiler() {
/* 200*/    return COMPILER;
/*   0*/  }
/*   0*/  
/*   0*/  protected CompiledExpression compilePath(String xpath) {
/* 204*/    return new JXPathCompiledExpression(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  private Expression compileExpression(String xpath) {
/* 210*/    synchronized (compiled) {
/* 212*/      expr = null;
/* 213*/      SoftReference<Expression> ref = (SoftReference)compiled.get(xpath);
/* 214*/      if (ref != null) {
/* 215*/          expr = ref.get(); 
/*   0*/         }
/*   0*/    } 
/* 223*/    if (expr != null) {
/* 224*/        return expr; 
/*   0*/       }
/* 227*/    Expression expr = (Expression)Parser.parseExpression(xpath, getCompiler());
/* 229*/    synchronized (compiled) {
/* 231*/      if (cleanupCount++ >= 500) {
/* 232*/        Iterator<Map.Entry> it = compiled.entrySet().iterator();
/* 233*/        while (it.hasNext()) {
/* 234*/          Map.Entry me = it.next();
/* 235*/          if (((SoftReference)me.getValue()).get() == null) {
/* 236*/              it.remove(); 
/*   0*/             }
/*   0*/        } 
/* 239*/        cleanupCount = 0;
/*   0*/      } 
/* 241*/      compiled.put(xpath, new SoftReference<>(expr));
/*   0*/    } 
/* 248*/    return expr;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getValue(String xpath) {
/* 256*/    Expression expression = compileExpression(xpath);
/* 289*/    return getValue(xpath, expression);
/*   0*/  }
/*   0*/  
/*   0*/  public Object getValue(String xpath, Expression expr) {
/* 323*/    Object result = expr.computeValue(getEvalContext());
/* 324*/    if (result == null) {
/* 325*/      if (expr instanceof org.apache.commons.jxpath.ri.compiler.Path && 
/* 326*/        !isLenient()) {
/* 327*/          throw new JXPathNotFoundException("No value for xpath: " + xpath); 
/*   0*/         }
/* 331*/      return null;
/*   0*/    } 
/* 333*/    if (result instanceof EvalContext) {
/* 334*/      EvalContext ctx = (EvalContext)result;
/* 335*/      result = ctx.getSingleNodePointer();
/* 336*/      if (!isLenient() && result == null) {
/* 337*/          throw new JXPathNotFoundException("No value for xpath: " + xpath); 
/*   0*/         }
/*   0*/    } 
/* 341*/    if (result instanceof NodePointer) {
/* 342*/      result = ((NodePointer)result).getValuePointer();
/* 343*/      if (!isLenient() && !((NodePointer)result).isActual()) {
/* 350*/        NodePointer parent = ((NodePointer)result).getImmediateParentPointer();
/* 352*/        if (parent == null || !parent.isContainer() || !parent.isActual()) {
/* 355*/            throw new JXPathNotFoundException("No value for xpath: " + xpath); 
/*   0*/           }
/*   0*/      } 
/* 359*/      result = ((NodePointer)result).getValue();
/*   0*/    } 
/* 361*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getValue(String xpath, Class requiredType) {
/* 369*/    Expression expr = compileExpression(xpath);
/* 370*/    return getValue(xpath, expr, requiredType);
/*   0*/  }
/*   0*/  
/*   0*/  public Object getValue(String xpath, Expression expr, Class requiredType) {
/* 374*/    Object value = getValue(xpath, expr);
/* 375*/    if (value != null && requiredType != null) {
/* 376*/      if (!TypeUtils.canConvert(value, requiredType)) {
/* 377*/          throw new JXPathTypeConversionException("Invalid expression type. '" + xpath + "' returns " + value.getClass().getName() + ". It cannot be converted to " + requiredType.getName()); 
/*   0*/         }
/* 385*/      value = TypeUtils.convert(value, requiredType);
/*   0*/    } 
/* 387*/    return value;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator iterate(String xpath) {
/* 396*/    return iterate(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator iterate(String xpath, Expression expr) {
/* 400*/    return expr.iterate(getEvalContext());
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer getPointer(String xpath) {
/* 404*/    return getPointer(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer getPointer(String xpath, Expression expr) {
/* 408*/    Object result = expr.computeValue(getEvalContext());
/* 409*/    if (result instanceof EvalContext) {
/* 410*/        result = ((EvalContext)result).getSingleNodePointer(); 
/*   0*/       }
/* 412*/    if (result instanceof Pointer) {
/* 413*/      if (!isLenient() && !((NodePointer)result).isActual()) {
/* 414*/          throw new JXPathNotFoundException("No pointer for xpath: " + xpath); 
/*   0*/         }
/* 417*/      return (Pointer)result;
/*   0*/    } 
/* 419*/    return NodePointer.newNodePointer(null, result, getLocale());
/*   0*/  }
/*   0*/  
/*   0*/  public void setValue(String xpath, Object value) {
/* 423*/    setValue(xpath, compileExpression(xpath), value);
/*   0*/  }
/*   0*/  
/*   0*/  public void setValue(String xpath, Expression expr, Object value) {
/*   0*/    try {
/* 428*/      setValue(xpath, expr, value, false);
/* 430*/    } catch (Throwable ex) {
/* 431*/      throw new JXPathException("Exception trying to set value with xpath " + xpath, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer createPath(String xpath) {
/* 437*/    return createPath(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer createPath(String xpath, Expression expr) {
/*   0*/    try {
/* 442*/      Object result = expr.computeValue(getEvalContext());
/* 443*/      Pointer pointer = null;
/* 445*/      if (result instanceof Pointer) {
/* 446*/        pointer = (Pointer)result;
/* 448*/      } else if (result instanceof EvalContext) {
/* 449*/        EvalContext ctx = (EvalContext)result;
/* 450*/        pointer = ctx.getSingleNodePointer();
/*   0*/      } else {
/* 453*/        checkSimplePath(expr);
/* 455*/        throw new JXPathException("Cannot create path:" + xpath);
/*   0*/      } 
/* 457*/      return ((NodePointer)pointer).createPath(this);
/* 459*/    } catch (Throwable ex) {
/* 460*/      throw new JXPathException("Exception trying to create xpath " + xpath, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer createPathAndSetValue(String xpath, Object value) {
/* 467*/    return createPathAndSetValue(xpath, compileExpression(xpath), value);
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer createPathAndSetValue(String xpath, Expression expr, Object value) {
/*   0*/    try {
/* 476*/      return setValue(xpath, expr, value, true);
/* 478*/    } catch (Throwable ex) {
/* 479*/      throw new JXPathException("Exception trying to create xpath " + xpath, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private Pointer setValue(String xpath, Expression expr, Object value, boolean create) {
/* 491*/    Object result = expr.computeValue(getEvalContext());
/* 492*/    Pointer pointer = null;
/* 494*/    if (result instanceof Pointer) {
/* 495*/      pointer = (Pointer)result;
/* 497*/    } else if (result instanceof EvalContext) {
/* 498*/      EvalContext ctx = (EvalContext)result;
/* 499*/      pointer = ctx.getSingleNodePointer();
/*   0*/    } else {
/* 502*/      if (create) {
/* 503*/          checkSimplePath(expr); 
/*   0*/         }
/* 507*/      throw new JXPathException("Cannot set value for xpath: " + xpath);
/*   0*/    } 
/* 509*/    if (create) {
/* 510*/      pointer = ((NodePointer)pointer).createPath(this, value);
/*   0*/    } else {
/* 513*/      pointer.setValue(value);
/*   0*/    } 
/* 515*/    return pointer;
/*   0*/  }
/*   0*/  
/*   0*/  private void checkSimplePath(Expression expr) {
/* 523*/    if (!(expr instanceof LocationPath) || !((LocationPath)expr).isSimplePath()) {
/* 525*/        throw new JXPathInvalidSyntaxException("JXPath can only create a path if it uses exclusively the child:: and attribute:: axes and has no context-dependent predicates"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator iteratePointers(String xpath) {
/* 539*/    return iteratePointers(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator iteratePointers(String xpath, Expression expr) {
/* 543*/    return expr.iteratePointers(getEvalContext());
/*   0*/  }
/*   0*/  
/*   0*/  public void removePath(String xpath) {
/* 547*/    removePath(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  public void removePath(String xpath, Expression expr) {
/*   0*/    try {
/* 552*/      NodePointer pointer = (NodePointer)getPointer(xpath, expr);
/* 553*/      if (pointer != null) {
/* 554*/          pointer.remove(); 
/*   0*/         }
/* 557*/    } catch (Throwable ex) {
/* 558*/      throw new JXPathException("Exception trying to remove xpath " + xpath, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeAll(String xpath) {
/* 565*/    removeAll(xpath, compileExpression(xpath));
/*   0*/  }
/*   0*/  
/*   0*/  public void removeAll(String xpath, Expression expr) {
/*   0*/    try {
/* 570*/      ArrayList<?> list = new ArrayList();
/* 571*/      Iterator<?> it = expr.iteratePointers(getEvalContext());
/* 572*/      while (it.hasNext()) {
/* 573*/          list.add(it.next()); 
/*   0*/         }
/* 575*/      Collections.sort(list, ReverseComparator.INSTANCE);
/* 576*/      it = list.iterator();
/* 577*/      if (it.hasNext()) {
/* 578*/        NodePointer pointer = (NodePointer)it.next();
/* 579*/        pointer.remove();
/* 580*/        while (it.hasNext()) {
/* 581*/            removePath(((NodePointer)it.next()).asPath()); 
/*   0*/           }
/*   0*/      } 
/* 585*/    } catch (Throwable ex) {
/* 586*/      throw new JXPathException("Exception trying to remove all for xpath " + xpath, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JXPathContext getRelativeContext(Pointer pointer) {
/* 593*/    Object contextBean = pointer.getNode();
/* 594*/    if (contextBean == null) {
/* 595*/        throw new JXPathException("Cannot create a relative context for a non-existent node: " + pointer); 
/*   0*/       }
/* 599*/    return new JXPathContextReferenceImpl(this, contextBean, pointer);
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer getContextPointer() {
/* 603*/    return this.contextPointer;
/*   0*/  }
/*   0*/  
/*   0*/  private NodePointer getAbsoluteRootPointer() {
/* 607*/    return (NodePointer)this.rootPointer;
/*   0*/  }
/*   0*/  
/*   0*/  private EvalContext getEvalContext() {
/* 611*/    return new InitialContext(new RootContext(this, (NodePointer)getContextPointer()));
/*   0*/  }
/*   0*/  
/*   0*/  public EvalContext getAbsoluteRootContext() {
/* 616*/    return new InitialContext(new RootContext(this, getAbsoluteRootPointer()));
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer getVariablePointer(QName name) {
/* 621*/    return NodePointer.newNodePointer(name, VariablePointerFactory.contextWrapper(this), getLocale());
/*   0*/  }
/*   0*/  
/*   0*/  public Function getFunction(QName functionName, Object[] parameters) {
/* 626*/    String namespace = functionName.getPrefix();
/* 627*/    String name = functionName.getName();
/* 628*/    JXPathContext funcCtx = this;
/* 629*/    Function func = null;
/* 631*/    while (funcCtx != null) {
/* 632*/      Functions funcs = funcCtx.getFunctions();
/* 633*/      if (funcs != null) {
/* 634*/        func = funcs.getFunction(namespace, name, parameters);
/* 635*/        if (func != null) {
/* 636*/            return func; 
/*   0*/           }
/*   0*/      } 
/* 639*/      funcCtx = funcCtx.getParentContext();
/*   0*/    } 
/* 641*/    throw new JXPathFunctionNotFoundException("Undefined function: " + functionName.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public void registerNamespace(String prefix, String namespaceURI) {
/* 646*/    if (this.namespaceResolver.isSealed()) {
/* 647*/        this.namespaceResolver = (NamespaceResolver)this.namespaceResolver.clone(); 
/*   0*/       }
/* 649*/    this.namespaceResolver.registerNamespace(prefix, namespaceURI);
/*   0*/  }
/*   0*/  
/*   0*/  public String getNamespaceURI(String prefix) {
/* 653*/    return this.namespaceResolver.getNamespaceURI(prefix.toLowerCase());
/*   0*/  }
/*   0*/  
/*   0*/  public String getPrefix(String namespaceURI) {
/* 661*/    return this.namespaceResolver.getPrefix(namespaceURI);
/*   0*/  }
/*   0*/  
/*   0*/  public void setNamespaceContextPointer(Pointer pointer) {
/* 665*/    if (this.namespaceResolver.isSealed()) {
/* 666*/        this.namespaceResolver = (NamespaceResolver)this.namespaceResolver.clone(); 
/*   0*/       }
/* 668*/    this.namespaceResolver.setNamespaceContextPointer((NodePointer)pointer);
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer getNamespaceContextPointer() {
/* 672*/    return this.namespaceResolver.getNamespaceContextPointer();
/*   0*/  }
/*   0*/  
/*   0*/  public NamespaceResolver getNamespaceResolver() {
/* 676*/    this.namespaceResolver.seal();
/* 677*/    return this.namespaceResolver;
/*   0*/  }
/*   0*/  
/*   0*/  public static Object allocateConditionally(String className, String existenceCheckClassName) {
/*   0*/    try {
/*   0*/      try {
/* 690*/        Class.forName(existenceCheckClassName);
/* 692*/      } catch (ClassNotFoundException ex) {
/* 693*/        return null;
/*   0*/      } 
/* 695*/      Class<?> cls = Class.forName(className);
/* 696*/      return cls.newInstance();
/* 698*/    } catch (Exception ex) {
/* 699*/      throw new JXPathException("Cannot allocate " + className, ex);
/*   0*/    } 
/*   0*/  }
/*   0*/}
