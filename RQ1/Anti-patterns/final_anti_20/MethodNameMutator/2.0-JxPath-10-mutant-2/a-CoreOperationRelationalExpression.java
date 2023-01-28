/*   0*/package org.apache.commons.jxpath.ri.compiler;
/*   0*/
/*   0*/import java.util.Collection;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import org.apache.commons.jxpath.ri.EvalContext;
/*   0*/import org.apache.commons.jxpath.ri.InfoSetUtil;
/*   0*/import org.apache.commons.jxpath.ri.axes.InitialContext;
/*   0*/
/*   0*/public abstract class CoreOperationRelationalExpression extends CoreOperation {
/*   0*/  protected CoreOperationRelationalExpression(Expression[] args) {
/*  38*/    super(args);
/*   0*/  }
/*   0*/  
/*   0*/  public final Object computeValue(EvalContext context) {
/*  42*/    return compute(this.args[0].computeValue(context), this.args[1].computeValue(context)) ? Boolean.TRUE : Boolean.FALSE;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getPrecedence() {
/*  47*/    return 3;
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean isSymmetric() {
/*  51*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract boolean evaluateCompare(int paramInt);
/*   0*/  
/*   0*/  private boolean compute(Object left, Object right) {
/*  57*/    left = reduce(left);
/*  58*/    right = reduce(right);
/*  60*/    if (left instanceof InitialContext) {
/*  61*/        ((InitialContext)left).reset(); 
/*   0*/       }
/*  63*/    if (right instanceof InitialContext) {
/*  64*/        ((InitialContext)right).reset(); 
/*   0*/       }
/*  66*/    if (left instanceof Iterator && right instanceof Iterator) {
/*  67*/        return findMatch((Iterator)left, (Iterator)right); 
/*   0*/       }
/*  69*/    if (left instanceof Iterator) {
/*  70*/        return containsMatch((Iterator)left, right); 
/*   0*/       }
/*  72*/    if (right instanceof Iterator) {
/*  73*/        return containsMatch((Iterator)right, left); 
/*   0*/       }
/*  75*/    double ld = InfoSetUtil.doubleValue(left);
/*  76*/    if (Double.isNaN(ld)) {
/*  77*/        return false; 
/*   0*/       }
/*  79*/    double rd = InfoSetUtil.doubleValue(right);
/*  80*/    if (Double.isNaN(rd)) {
/*  81*/        return false; 
/*   0*/       }
/*  83*/    return evaluateCompare((ld == rd) ? 0 : ((ld < rd) ? -1 : 1));
/*   0*/  }
/*   0*/  
/*   0*/  private Object reduce(Object o) {
/*  87*/    if (o instanceof org.apache.commons.jxpath.ri.axes.SelfContext) {
/*  88*/        o = ((EvalContext)o).getSingleNodePointer(); 
/*   0*/       }
/*  90*/    if (o instanceof Collection) {
/*  91*/        o = ((Collection)o).iterator(); 
/*   0*/       }
/*  93*/    return o;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean containsMatch(Iterator it, Object value) {
/*  97*/    while (it.hasNext()) {
/*  98*/      Object element = it.next();
/*  99*/      if (compute(element, value)) {
/* 100*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 103*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean findMatch(Iterator lit, Iterator rit) {
/* 107*/    HashSet left = new HashSet();
/* 108*/    while (lit.hasNext()) {
/* 109*/        left.add(lit.next()); 
/*   0*/       }
/* 111*/    while (rit.hasNext()) {
/* 112*/      if (containsMatch(left.iterator(), rit.next())) {
/* 113*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 116*/    return false;
/*   0*/  }
/*   0*/}
