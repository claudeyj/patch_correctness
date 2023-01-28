/*   0*/package org.apache.commons.jxpath.ri.compiler;
/*   0*/
/*   0*/import java.util.Collection;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import org.apache.commons.jxpath.Pointer;
/*   0*/import org.apache.commons.jxpath.ri.EvalContext;
/*   0*/import org.apache.commons.jxpath.ri.InfoSetUtil;
/*   0*/
/*   0*/public abstract class CoreOperationCompare extends CoreOperation {
/*   0*/  public CoreOperationCompare(Expression arg1, Expression arg2) {
/*  39*/    super(new Expression[] { arg1, arg2 });
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean equal(EvalContext context, Expression left, Expression right) {
/*  50*/    Object l = left.compute(context);
/*  51*/    Object r = right.compute(context);
/*  57*/    l instanceof org.apache.commons.jxpath.ri.axes.InitialContext;
/*  57*/    if (l instanceof org.apache.commons.jxpath.ri.axes.SelfContext) {
/*  58*/        l = ((EvalContext)l).getSingleNodePointer(); 
/*   0*/       }
/*  61*/    if (r instanceof org.apache.commons.jxpath.ri.axes.InitialContext || r instanceof org.apache.commons.jxpath.ri.axes.SelfContext) {
/*  62*/        r = ((EvalContext)r).getSingleNodePointer(); 
/*   0*/       }
/*  65*/    if (l instanceof Collection) {
/*  66*/        l = ((Collection)l).iterator(); 
/*   0*/       }
/*  69*/    if (r instanceof Collection) {
/*  70*/        r = ((Collection)r).iterator(); 
/*   0*/       }
/*  73*/    if (l instanceof Iterator && !(r instanceof Iterator)) {
/*  74*/        return contains((Iterator)l, r); 
/*   0*/       }
/*  76*/    if (!(l instanceof Iterator) && r instanceof Iterator) {
/*  77*/        return contains((Iterator)r, l); 
/*   0*/       }
/*  79*/    if (l instanceof Iterator && r instanceof Iterator) {
/*  80*/        return findMatch((Iterator)l, (Iterator)r); 
/*   0*/       }
/*  82*/    return equal(l, r);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean contains(Iterator it, Object value) {
/*  86*/    while (it.hasNext()) {
/*  87*/      Object element = it.next();
/*  88*/      if (equal(element, value)) {
/*  89*/          return true; 
/*   0*/         }
/*   0*/    } 
/*  92*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean findMatch(Iterator lit, Iterator rit) {
/*  96*/    HashSet left = new HashSet();
/*  97*/    while (lit.hasNext()) {
/*  98*/        left.add(lit.next()); 
/*   0*/       }
/* 100*/    while (rit.hasNext()) {
/* 101*/      if (contains(left.iterator(), rit.next())) {
/* 102*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 105*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean equal(Object l, Object r) {
/* 109*/    if (l instanceof Pointer && r instanceof Pointer && 
/* 110*/      l.equals(r)) {
/* 111*/        return true; 
/*   0*/       }
/* 115*/    if (l instanceof Pointer) {
/* 116*/        l = ((Pointer)l).getValue(); 
/*   0*/       }
/* 119*/    if (r instanceof Pointer) {
/* 120*/        r = ((Pointer)r).getValue(); 
/*   0*/       }
/* 123*/    if (l == r) {
/* 124*/        return true; 
/*   0*/       }
/* 128*/    if (l instanceof Boolean || r instanceof Boolean) {
/* 129*/        return (InfoSetUtil.booleanValue(l) == InfoSetUtil.booleanValue(r)); 
/*   0*/       }
/* 131*/    if (l instanceof Number || r instanceof Number) {
/* 132*/        return (InfoSetUtil.doubleValue(l) == InfoSetUtil.doubleValue(r)); 
/*   0*/       }
/* 134*/    if (l instanceof String || r instanceof String) {
/* 135*/        return InfoSetUtil.stringValue(l).equals(InfoSetUtil.stringValue(r)); 
/*   0*/       }
/* 138*/    return (l != null && l.equals(r));
/*   0*/  }
/*   0*/}
