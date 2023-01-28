/*   0*/package com.fasterxml.jackson.databind.type;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import java.util.ArrayList;
/*   0*/
/*   0*/public final class ClassStack {
/*   0*/  protected final ClassStack _parent;
/*   0*/  
/*   0*/  protected final Class<?> _current;
/*   0*/  
/*   0*/  private ArrayList<ResolvedRecursiveType> _selfRefs;
/*   0*/  
/*   0*/  public ClassStack(Class<?> rootType) {
/*  21*/    this(null, rootType);
/*   0*/  }
/*   0*/  
/*   0*/  private ClassStack(ClassStack parent, Class<?> curr) {
/*  25*/    this._parent = parent;
/*  26*/    this._current = curr;
/*   0*/  }
/*   0*/  
/*   0*/  public ClassStack child(Class<?> cls) {
/*  33*/    return new ClassStack(this, cls);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSelfReference(ResolvedRecursiveType ref) {
/*  42*/    if (this._selfRefs == null) {
/*  43*/        this._selfRefs = new ArrayList<ResolvedRecursiveType>(); 
/*   0*/       }
/*  45*/    this._selfRefs.add(ref);
/*   0*/  }
/*   0*/  
/*   0*/  public void resolveSelfReferences(JavaType resolved) {
/*  55*/    if (this._selfRefs != null) {
/*  56*/        for (ResolvedRecursiveType ref : this._selfRefs) {
/*  57*/            ref.setReference(resolved); 
/*   0*/           } 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public ClassStack find(Class<?> cls) {
/*  64*/    if (this._current == cls) {
/*  64*/        return this; 
/*   0*/       }
/*  65*/    if (this == null) {
/*  65*/        return null; 
/*   0*/       }
/*  65*/    for (ClassStack curr = this._parent; curr != null; curr = curr._parent) {
/*  66*/      if (curr._current == cls) {
/*  67*/          return curr; 
/*   0*/         }
/*   0*/    } 
/*  70*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*  75*/    StringBuilder sb = new StringBuilder();
/*  76*/    sb.append("[ClassStack (self-refs: ").append((this._selfRefs == null) ? "0" : String.valueOf(this._selfRefs.size())).append(')');
/*  80*/    for (ClassStack curr = this; curr != null; curr = curr._parent) {
/*  81*/        sb.append(' ').append(curr._current.getName()); 
/*   0*/       }
/*  83*/    sb.append(']');
/*  84*/    return sb.toString();
/*   0*/  }
/*   0*/}
