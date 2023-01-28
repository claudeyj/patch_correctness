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
/*  42*/    super(args);
/*   0*/  }
/*   0*/  
/*   0*/  public final Object computeValue(EvalContext context) {
/*  46*/    return compute(this.args[0].compute(context), this.args[1].compute(context)) ? Boolean.TRUE : Boolean.FALSE;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int getPrecedence() {
/*  51*/    return 3;
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean isSymmetric() {
/*  55*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract boolean evaluateCompare(int paramInt);
/*   0*/  
/*   0*/  private boolean compute(Object left, Object right) {
/*  72*/    left = reduce(left);
/*  73*/    right = reduce(right);
/*  75*/    if (left instanceof InitialContext) {
/*  76*/        ((InitialContext)left).reset(); 
/*   0*/       }
/*  78*/    if (right instanceof InitialContext) {
/*  79*/        ((InitialContext)right).reset(); 
/*   0*/       }
/*  81*/    if (left instanceof Iterator && right instanceof Iterator) {
/*  82*/        return findMatch((Iterator)left, (Iterator)right); 
/*   0*/       }
/*  84*/    if (left instanceof Iterator) {
/*  85*/        return containsMatch((Iterator)left, right); 
/*   0*/       }
/*  87*/    if (left instanceof Iterator) {
/*  88*/        return containsMatch((Iterator)right, left); 
/*   0*/       }
/*  90*/    double ld = InfoSetUtil.doubleValue(left);
/*  91*/    if (Double.isNaN(ld)) {
/*  92*/        return false; 
/*   0*/       }
/*  94*/    double rd = InfoSetUtil.doubleValue(right);
/*  95*/    if (Double.isNaN(rd)) {
/*  96*/        return false; 
/*   0*/       }
/*  98*/    return evaluateCompare((ld == rd) ? 0 : ((ld < rd) ? -1 : 1));
/*   0*/  }
/*   0*/  
/*   0*/  private Object reduce(Object o) {
/* 107*/    if (o instanceof org.apache.commons.jxpath.ri.axes.SelfContext) {
/* 108*/        o = ((EvalContext)o).getSingleNodePointer(); 
/*   0*/       }
/* 110*/    if (o instanceof Collection) {
/* 111*/        o = ((Collection)o).iterator(); 
/*   0*/       }
/* 113*/    return o;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean containsMatch(Iterator it, Object value) {
/* 123*/    while (it.hasNext()) {
/* 124*/      Object element = it.next();
/* 125*/      if (compute(element, value)) {
/* 126*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 129*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean findMatch(Iterator lit, Iterator rit) {
/* 146*/    HashSet left = new HashSet();
/* 147*/    while (lit.hasNext()) {
/* 148*/        left.add(lit.next()); 
/*   0*/       }
/* 150*/    while (rit.hasNext()) {
/* 151*/      if (containsMatch(left.iterator(), rit.next())) {
/* 152*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 155*/    return false;
/*   0*/  }
/*   0*/}
