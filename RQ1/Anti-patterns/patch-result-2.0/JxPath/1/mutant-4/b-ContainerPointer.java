/*   0*/package org.apache.commons.jxpath.ri.model.container;
/*   0*/
/*   0*/import java.util.Locale;
/*   0*/import org.apache.commons.jxpath.Container;
/*   0*/import org.apache.commons.jxpath.ri.QName;
/*   0*/import org.apache.commons.jxpath.ri.compiler.NodeTest;
/*   0*/import org.apache.commons.jxpath.ri.model.NodeIterator;
/*   0*/import org.apache.commons.jxpath.ri.model.NodePointer;
/*   0*/import org.apache.commons.jxpath.util.ValueUtils;
/*   0*/
/*   0*/public class ContainerPointer extends NodePointer {
/*   0*/  private Container container;
/*   0*/  
/*   0*/  private NodePointer valuePointer;
/*   0*/  
/*   0*/  public ContainerPointer(Container container, Locale locale) {
/*  41*/    super(null, locale);
/*  42*/    this.container = container;
/*   0*/  }
/*   0*/  
/*   0*/  public ContainerPointer(NodePointer parent, Container container) {
/*  46*/    super(parent);
/*  47*/    this.container = container;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContainer() {
/*  54*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public QName getName() {
/*  58*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getBaseValue() {
/*  62*/    return this.container;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCollection() {
/*  66*/    Object value = getBaseValue();
/*  67*/    return (value != null && ValueUtils.isCollection(value));
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*  71*/    Object value = getBaseValue();
/*  72*/    if (value == null) {
/*  73*/        return 1; 
/*   0*/       }
/*  75*/    return ValueUtils.getLength(value);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLeaf() {
/*  79*/    return getValuePointer().isLeaf();
/*   0*/  }
/*   0*/  
/*   0*/  public Object getImmediateNode() {
/*  83*/    Object value = getBaseValue();
/*  84*/    if (this.index != Integer.MIN_VALUE) {
/*  85*/      if (this.index >= 0 && this.index < getLength()) {
/*  86*/          return ValueUtils.getValue(value, this.index); 
/*   0*/         }
/*  89*/      return null;
/*   0*/    } 
/*  93*/    return ValueUtils.getValue(value);
/*   0*/  }
/*   0*/  
/*   0*/  public void setValue(Object value) {
/*  99*/    this.container.setValue(value);
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer getImmediateValuePointer() {
/* 103*/    if (this.valuePointer == null) {
/* 104*/      Object value = getImmediateNode();
/* 105*/      this.valuePointer = NodePointer.newChildNodePointer(this, getName(), value);
/*   0*/    } 
/* 108*/    return this.valuePointer;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 112*/    return System.identityHashCode(this.container) + this.index;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object object) {
/* 116*/    if (object == this) {
/* 117*/        return true; 
/*   0*/       }
/* 120*/    if (!(object instanceof ContainerPointer)) {
/* 121*/        return false; 
/*   0*/       }
/* 124*/    ContainerPointer other = (ContainerPointer)object;
/* 125*/    return (this.container == other.container && this.index == other.index);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
/* 133*/    return getValuePointer().childIterator(test, reverse, startWith);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator attributeIterator(QName name) {
/* 137*/    return getValuePointer().attributeIterator(name);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator namespaceIterator() {
/* 141*/    return getValuePointer().namespaceIterator();
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer namespacePointer(String namespace) {
/* 145*/    return getValuePointer().namespacePointer(namespace);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean testNode(NodeTest nodeTest) {
/* 149*/    return !getValuePointer().testNode(nodeTest);
/*   0*/  }
/*   0*/  
/*   0*/  public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2) {
/* 156*/    return pointer1.getIndex() - pointer2.getIndex();
/*   0*/  }
/*   0*/  
/*   0*/  public String getNamespaceURI(String prefix) {
/* 160*/    return getValuePointer().getNamespaceURI(prefix);
/*   0*/  }
/*   0*/  
/*   0*/  public String asPath() {
/* 164*/    if (this.parent != null) {
/* 165*/        return this.parent.asPath(); 
/*   0*/       }
/* 167*/    return "/";
/*   0*/  }
/*   0*/}
