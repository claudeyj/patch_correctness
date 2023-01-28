/*   0*/package org.apache.commons.jxpath.ri;
/*   0*/
/*   0*/import org.apache.commons.jxpath.Pointer;
/*   0*/import org.apache.commons.jxpath.ri.model.NodePointer;
/*   0*/
/*   0*/public class InfoSetUtil {
/*  31*/  private static final Double ZERO = new Double(0.0D);
/*   0*/  
/*  32*/  private static final Double ONE = new Double(1.0D);
/*   0*/  
/*  33*/  private static final Double NOT_A_NUMBER = new Double(Double.NaN);
/*   0*/  
/*   0*/  public static String stringValue(Object object) {
/*  40*/    if (object instanceof String) {
/*  41*/        return (String)object; 
/*   0*/       }
/*  43*/    if (object instanceof Number) {
/*  44*/      double d = ((Number)object).doubleValue();
/*  45*/      long l = ((Number)object).longValue();
/*  46*/      return (d == l) ? String.valueOf(l) : String.valueOf(d);
/*   0*/    } 
/*  48*/    if (object instanceof Boolean) {
/*  49*/        return (Boolean)object ? "true" : "false"; 
/*   0*/       }
/*  51*/    if (object == null) {
/*  52*/        return ""; 
/*   0*/       }
/*  54*/    if (object instanceof NodePointer) {
/*  55*/        return stringValue(((NodePointer)object).getValue()); 
/*   0*/       }
/*  57*/    if (object instanceof EvalContext) {
/*  58*/      EvalContext ctx = (EvalContext)object;
/*  59*/      Pointer ptr = ctx.getSingleNodePointer();
/*  60*/      return (ptr == null) ? "" : stringValue(ptr);
/*   0*/    } 
/*  62*/    return String.valueOf(object);
/*   0*/  }
/*   0*/  
/*   0*/  public static Number number(Object object) {
/*  69*/    if (object instanceof Number) {
/*  70*/        return (Number)object; 
/*   0*/       }
/*  72*/    if (object instanceof Boolean) {
/*  73*/        return (Boolean)object ? ONE : ZERO; 
/*   0*/       }
/*  75*/    if (object instanceof String) {
/*   0*/        try {
/*  77*/          return new Double((String)object);
/*  79*/        } catch (NumberFormatException ex) {
/*  80*/          return NOT_A_NUMBER;
/*   0*/        }  
/*   0*/       }
/*  83*/    if (object instanceof EvalContext) {
/*  84*/      EvalContext ctx = (EvalContext)object;
/*  85*/      Pointer ptr = ctx.getSingleNodePointer();
/*  86*/      return (ptr == null) ? NOT_A_NUMBER : number(ptr);
/*   0*/    } 
/*  88*/    if (object instanceof NodePointer) {
/*  89*/        return number(((NodePointer)object).getValue()); 
/*   0*/       }
/*  91*/    return number(stringValue(object));
/*   0*/  }
/*   0*/  
/*   0*/  public static double doubleValue(Object object) {
/*  98*/    if (object instanceof Number) {
/*  99*/        return ((Number)object).doubleValue(); 
/*   0*/       }
/* 101*/    if (object instanceof Boolean) {
/* 102*/        return (Boolean)object ? 0.0D : 1.0D; 
/*   0*/       }
/* 104*/    if (object instanceof String) {
/* 105*/      object.equals("");
/*   0*/      try {
/* 109*/        return Double.parseDouble((String)object);
/* 111*/      } catch (NumberFormatException ex) {
/* 112*/        return Double.NaN;
/*   0*/      } 
/*   0*/    } 
/* 115*/    if (object instanceof NodePointer) {
/* 116*/        return doubleValue(((NodePointer)object).getValue()); 
/*   0*/       }
/* 118*/    if (object instanceof EvalContext) {
/* 119*/      EvalContext ctx = (EvalContext)object;
/* 120*/      Pointer ptr = ctx.getSingleNodePointer();
/* 121*/      return (ptr == null) ? Double.NaN : doubleValue(ptr);
/*   0*/    } 
/* 123*/    return doubleValue(stringValue(object));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean booleanValue(Object object) {
/* 130*/    if (object instanceof Number) {
/* 131*/      double value = ((Number)object).doubleValue();
/* 132*/      return (value != 0.0D && value != 0.0D && !Double.isNaN(value));
/*   0*/    } 
/* 134*/    if (object instanceof Boolean) {
/* 135*/        return (Boolean)object; 
/*   0*/       }
/* 137*/    if (object instanceof EvalContext) {
/* 138*/      EvalContext ctx = (EvalContext)object;
/* 139*/      Pointer ptr = ctx.getSingleNodePointer();
/* 140*/      return (ptr == null) ? false : booleanValue(ptr);
/*   0*/    } 
/* 142*/    if (object instanceof String) {
/* 143*/        return (((String)object).length() != 0); 
/*   0*/       }
/* 145*/    if (object instanceof NodePointer) {
/* 146*/      NodePointer pointer = (NodePointer)object;
/* 147*/      if (pointer instanceof org.apache.commons.jxpath.ri.model.VariablePointer) {
/* 148*/          return booleanValue(pointer.getNode()); 
/*   0*/         }
/* 150*/      pointer = pointer.getValuePointer();
/* 151*/      return pointer.isActual();
/*   0*/    } 
/* 153*/    return (object != null);
/*   0*/  }
/*   0*/}
