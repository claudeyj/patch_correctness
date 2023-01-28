/*   0*/package com.google.javascript.rhino;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Objects;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.SimpleSourceFile;
/*   0*/import com.google.javascript.rhino.jstype.StaticSourceFile;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.NoSuchElementException;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class Node implements Cloneable, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public static final int JSDOC_INFO_PROP = 29;
/*   0*/  
/*   0*/  public static final int VAR_ARGS_NAME = 30;
/*   0*/  
/*   0*/  public static final int INCRDECR_PROP = 32;
/*   0*/  
/*   0*/  public static final int QUOTED_PROP = 36;
/*   0*/  
/*   0*/  public static final int OPT_ARG_NAME = 37;
/*   0*/  
/*   0*/  public static final int SYNTHETIC_BLOCK_PROP = 38;
/*   0*/  
/*   0*/  public static final int EMPTY_BLOCK = 39;
/*   0*/  
/*   0*/  public static final int ORIGINALNAME_PROP = 40;
/*   0*/  
/*   0*/  public static final int SIDE_EFFECT_FLAGS = 42;
/*   0*/  
/*   0*/  public static final int IS_CONSTANT_NAME = 43;
/*   0*/  
/*   0*/  public static final int IS_NAMESPACE = 46;
/*   0*/  
/*   0*/  public static final int IS_DISPATCHER = 47;
/*   0*/  
/*   0*/  public static final int DIRECTIVES = 48;
/*   0*/  
/*   0*/  public static final int DIRECT_EVAL = 49;
/*   0*/  
/*   0*/  public static final int FREE_CALL = 50;
/*   0*/  
/*   0*/  public static final int STATIC_SOURCE_FILE = 51;
/*   0*/  
/*   0*/  public static final int LENGTH = 52;
/*   0*/  
/*   0*/  public static final int INPUT_ID = 53;
/*   0*/  
/*   0*/  public static final int SLASH_V = 54;
/*   0*/  
/*   0*/  public static final int INFERRED_FUNCTION = 55;
/*   0*/  
/*   0*/  public static final int LAST_PROP = 55;
/*   0*/  
/*   0*/  public static final int DECR_FLAG = 1;
/*   0*/  
/*   0*/  public static final int POST_FLAG = 2;
/*   0*/  
/*   0*/  int type;
/*   0*/  
/*   0*/  Node next;
/*   0*/  
/*   0*/  private Node first;
/*   0*/  
/*   0*/  private Node last;
/*   0*/  
/*   0*/  private PropListItem propListHead;
/*   0*/  
/*   0*/  public static final int COLUMN_BITS = 12;
/*   0*/  
/*   0*/  public static final int MAX_COLUMN_NUMBER = 4095;
/*   0*/  
/*   0*/  public static final int COLUMN_MASK = 4095;
/*   0*/  
/*   0*/  private int sourcePosition;
/*   0*/  
/*   0*/  private JSType jsType;
/*   0*/  
/*   0*/  private Node parent;
/*   0*/  
/*   0*/  public static final int FLAG_GLOBAL_STATE_UNMODIFIED = 1;
/*   0*/  
/*   0*/  public static final int FLAG_THIS_UNMODIFIED = 2;
/*   0*/  
/*   0*/  public static final int FLAG_ARGUMENTS_UNMODIFIED = 4;
/*   0*/  
/*   0*/  public static final int FLAG_NO_THROWS = 8;
/*   0*/  
/*   0*/  public static final int FLAG_LOCAL_RESULTS = 16;
/*   0*/  
/*   0*/  public static final int SIDE_EFFECTS_FLAGS_MASK = 31;
/*   0*/  
/*   0*/  public static final int SIDE_EFFECTS_ALL = 0;
/*   0*/  
/*   0*/  public static final int NO_SIDE_EFFECTS = 15;
/*   0*/  
/*   0*/  private static final String propToString(int propType) {
/* 112*/    switch (propType) {
/*   0*/      case 30:
/* 113*/        return "var_args_name";
/*   0*/      case 29:
/* 115*/        return "jsdoc_info";
/*   0*/      case 32:
/* 117*/        return "incrdecr";
/*   0*/      case 36:
/* 118*/        return "quoted";
/*   0*/      case 37:
/* 119*/        return "opt_arg";
/*   0*/      case 38:
/* 121*/        return "synthetic";
/*   0*/      case 39:
/* 122*/        return "empty_block";
/*   0*/      case 40:
/* 123*/        return "originalname";
/*   0*/      case 42:
/* 124*/        return "side_effect_flags";
/*   0*/      case 43:
/* 126*/        return "is_constant_name";
/*   0*/      case 46:
/* 127*/        return "is_namespace";
/*   0*/      case 47:
/* 128*/        return "is_dispatcher";
/*   0*/      case 48:
/* 129*/        return "directives";
/*   0*/      case 49:
/* 130*/        return "direct_eval";
/*   0*/      case 50:
/* 131*/        return "free_call";
/*   0*/      case 51:
/* 132*/        return "source_file";
/*   0*/      case 53:
/* 133*/        return "input_id";
/*   0*/      case 52:
/* 134*/        return "length";
/*   0*/      case 54:
/* 135*/        return "slash_v";
/*   0*/      case 55:
/* 136*/        return "inferred";
/*   0*/    } 
/* 138*/    throw new IllegalStateException("unexpect prop id " + propType);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 147*/      super(39);
/* 148*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 152*/      super(39, lineno, charno);
/* 153*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 158*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 163*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 168*/      boolean equivalent = super.isEquivalentTo(node, compareJsType, recurse);
/* 169*/      if (equivalent) {
/* 170*/        double thisValue = getDouble();
/* 171*/        double thatValue = ((NumberNode)node).getDouble();
/* 172*/        if (thisValue == thatValue)
/* 174*/          return (thisValue != 0.0D || 1.0D / thisValue == 1.0D / thatValue); 
/*   0*/      } 
/* 177*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 188*/      super(type);
/* 189*/      if (null == str)
/* 190*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 192*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 196*/      super(type, lineno, charno);
/* 197*/      if (null == str)
/* 198*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 200*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 209*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 218*/      if (null == str)
/* 219*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 221*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 226*/      return (super.isEquivalentTo(node, compareJsType, recurse) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 238*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 246*/      putBooleanProp(36, true);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static interface PropListItem {
/*   0*/    int getType();
/*   0*/    
/*   0*/    PropListItem getNext();
/*   0*/    
/*   0*/    PropListItem chain(PropListItem param1PropListItem);
/*   0*/    
/*   0*/    Object getObjectValue();
/*   0*/    
/*   0*/    int getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  private static abstract class AbstractPropListItem implements PropListItem, Serializable {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private final Node.PropListItem next;
/*   0*/    
/*   0*/    private final int propType;
/*   0*/    
/*   0*/    AbstractPropListItem(int propType, Node.PropListItem next) {
/* 269*/      this.propType = propType;
/* 270*/      this.next = next;
/*   0*/    }
/*   0*/    
/*   0*/    public int getType() {
/* 275*/      return this.propType;
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem getNext() {
/* 280*/      return this.next;
/*   0*/    }
/*   0*/    
/*   0*/    public abstract Node.PropListItem chain(Node.PropListItem param1PropListItem);
/*   0*/  }
/*   0*/  
/*   0*/  private static class ObjectPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private final Object objectValue;
/*   0*/    
/*   0*/    ObjectPropListItem(int propType, Object objectValue, Node.PropListItem next) {
/* 295*/      super(propType, next);
/* 296*/      this.objectValue = objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 301*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 306*/      return this.objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 311*/      return (this.objectValue == null) ? "null" : this.objectValue.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 316*/      return new ObjectPropListItem(getType(), this.objectValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class IntPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    IntPropListItem(int propType, int intValue, Node.PropListItem next) {
/* 327*/      super(propType, next);
/* 328*/      this.intValue = intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 333*/      return this.intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 338*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 343*/      return String.valueOf(this.intValue);
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 348*/      return new IntPropListItem(getType(), this.intValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 353*/    this.type = nodeType;
/* 354*/    this.parent = null;
/* 355*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 359*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 361*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 364*/    this.type = nodeType;
/* 365*/    this.parent = null;
/* 366*/    this.first = this.last = child;
/* 367*/    child.next = null;
/* 368*/    child.parent = this;
/* 369*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 373*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 375*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 377*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 379*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 381*/    this.type = nodeType;
/* 382*/    this.parent = null;
/* 383*/    this.first = left;
/* 384*/    this.last = right;
/* 385*/    left.next = right;
/* 386*/    left.parent = this;
/* 387*/    right.next = null;
/* 388*/    right.parent = this;
/* 389*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 393*/    Preconditions.checkArgument((left.parent == null));
/* 394*/    Preconditions.checkArgument((left.next == null));
/* 395*/    Preconditions.checkArgument((mid.parent == null));
/* 396*/    Preconditions.checkArgument((mid.next == null));
/* 397*/    Preconditions.checkArgument((right.parent == null));
/* 398*/    Preconditions.checkArgument((right.next == null));
/* 399*/    this.type = nodeType;
/* 400*/    this.parent = null;
/* 401*/    this.first = left;
/* 402*/    this.last = right;
/* 403*/    left.next = mid;
/* 404*/    left.parent = this;
/* 405*/    mid.next = right;
/* 406*/    mid.parent = this;
/* 407*/    right.next = null;
/* 408*/    right.parent = this;
/* 409*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 413*/    Preconditions.checkArgument((left.parent == null));
/* 414*/    Preconditions.checkArgument((left.next == null));
/* 415*/    Preconditions.checkArgument((mid.parent == null));
/* 416*/    Preconditions.checkArgument((mid.next == null));
/* 417*/    Preconditions.checkArgument((mid2.parent == null));
/* 418*/    Preconditions.checkArgument((mid2.next == null));
/* 419*/    Preconditions.checkArgument((right.parent == null));
/* 420*/    Preconditions.checkArgument((right.next == null));
/* 421*/    this.type = nodeType;
/* 422*/    this.parent = null;
/* 423*/    this.first = left;
/* 424*/    this.last = right;
/* 425*/    left.next = mid;
/* 426*/    left.parent = this;
/* 427*/    mid.next = mid2;
/* 428*/    mid.parent = this;
/* 429*/    mid2.next = right;
/* 430*/    mid2.parent = this;
/* 431*/    right.next = null;
/* 432*/    right.parent = this;
/* 433*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 437*/    this.type = nodeType;
/* 438*/    this.parent = null;
/* 439*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 443*/    this(nodeType, child);
/* 444*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 448*/    this(nodeType, left, right);
/* 449*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 454*/    this(nodeType, left, mid, right);
/* 455*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 460*/    this(nodeType, left, mid, mid2, right);
/* 461*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 465*/    this(nodeType, children);
/* 466*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 470*/    this.type = nodeType;
/* 471*/    this.parent = null;
/* 472*/    if (children.length != 0) {
/* 473*/      this.first = children[0];
/* 474*/      this.last = children[children.length - 1];
/* 476*/      for (int i = 1; i < children.length; i++) {
/* 477*/        if (null != (children[i - 1]).next)
/* 479*/          throw new IllegalArgumentException("duplicate child"); 
/* 481*/        (children[i - 1]).next = children[i];
/* 482*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 483*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 485*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 486*/      (children[children.length - 1]).parent = this;
/* 488*/      if (null != this.last.next)
/* 490*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 496*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 500*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 504*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 508*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 512*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 516*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 520*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 524*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 528*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 532*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 536*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 540*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 544*/    if (child == this.first)
/* 545*/      return null; 
/* 547*/    Node n = this.first;
/* 548*/    while (n.next != child) {
/* 549*/      n = n.next;
/* 550*/      if (n == null)
/* 551*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 554*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 558*/    Node n = this.first;
/* 559*/    while (i > 0) {
/* 560*/      n = n.next;
/* 561*/      i--;
/*   0*/    } 
/* 563*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOfChild(Node child) {
/* 567*/    Node n = this.first;
/* 568*/    int i = 0;
/* 569*/    while (n != null) {
/* 570*/      if (child == n)
/* 571*/        return i; 
/* 574*/      n = n.next;
/* 575*/      i++;
/*   0*/    } 
/* 577*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 581*/    Node n = this;
/* 582*/    while (n.next != null)
/* 583*/      n = n.next; 
/* 585*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 589*/    Preconditions.checkArgument((child.parent == null));
/* 590*/    Preconditions.checkArgument((child.next == null));
/* 591*/    child.parent = this;
/* 592*/    child.next = this.first;
/* 593*/    this.first = child;
/* 594*/    if (this.last == null)
/* 595*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 600*/    Preconditions.checkArgument((child.parent == null));
/* 601*/    Preconditions.checkArgument((child.next == null));
/* 602*/    child.parent = this;
/* 603*/    child.next = null;
/* 604*/    if (this.last == null) {
/* 605*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 608*/    this.last.next = child;
/* 609*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 613*/    for (Node child = children; child != null; child = child.next) {
/* 614*/      Preconditions.checkArgument((child.parent == null));
/* 615*/      child.parent = this;
/*   0*/    } 
/* 617*/    Node lastSib = children.getLastSibling();
/* 618*/    lastSib.next = this.first;
/* 619*/    this.first = children;
/* 620*/    if (this.last == null)
/* 621*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 626*/    addChildrenAfter(children, getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 633*/    Preconditions.checkArgument((node != null && node.parent == this), "The existing child node of the parent should not be null.");
/* 635*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 637*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 639*/    if (this.first == node) {
/* 640*/      newChild.parent = this;
/* 641*/      newChild.next = this.first;
/* 642*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 645*/    Node prev = getChildBefore(node);
/* 646*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 653*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 655*/    addChildrenAfter(newChild, node);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenAfter(Node children, Node node) {
/* 662*/    Preconditions.checkArgument((node == null || node.parent == this));
/* 663*/    for (Node child = children; child != null; child = child.next) {
/* 664*/      Preconditions.checkArgument((child.parent == null));
/* 665*/      child.parent = this;
/*   0*/    } 
/* 668*/    Node lastSibling = children.getLastSibling();
/* 669*/    if (node != null) {
/* 670*/      Node oldNext = node.next;
/* 671*/      node.next = children;
/* 672*/      lastSibling.next = oldNext;
/* 673*/      if (node == this.last)
/* 674*/        this.last = lastSibling; 
/*   0*/    } else {
/* 678*/      if (this.first != null) {
/* 679*/        lastSibling.next = this.first;
/*   0*/      } else {
/* 681*/        this.last = lastSibling;
/*   0*/      } 
/* 683*/      this.first = children;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 691*/    Node prev = getChildBefore(child);
/* 692*/    if (prev == null) {
/* 693*/      this.first = this.first.next;
/*   0*/    } else {
/* 695*/      prev.next = child.next;
/*   0*/    } 
/* 696*/    if (child == this.last)
/* 696*/      this.last = prev; 
/* 697*/    child.next = null;
/* 698*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 705*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 707*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 711*/    newChild.copyInformationFrom(child);
/* 713*/    newChild.next = child.next;
/* 714*/    newChild.parent = this;
/* 715*/    if (child == this.first) {
/* 716*/      this.first = newChild;
/*   0*/    } else {
/* 718*/      Node prev = getChildBefore(child);
/* 719*/      prev.next = newChild;
/*   0*/    } 
/* 721*/    if (child == this.last)
/* 722*/      this.last = newChild; 
/* 723*/    child.next = null;
/* 724*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 728*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 731*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 733*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 737*/    newChild.copyInformationFrom(prevChild);
/* 739*/    Node child = prevChild.next;
/* 740*/    newChild.next = child.next;
/* 741*/    newChild.parent = this;
/* 742*/    prevChild.next = newChild;
/* 743*/    if (child == this.last)
/* 744*/      this.last = newChild; 
/* 745*/    child.next = null;
/* 746*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 751*/    PropListItem x = this.propListHead;
/* 752*/    while (x != null && propType != x.getType())
/* 753*/      x = x.getNext(); 
/* 755*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 766*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 768*/    this.propListHead = other.propListHead;
/* 769*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 773*/    PropListItem result = removeProp(this.propListHead, propType);
/* 774*/    if (result != this.propListHead)
/* 775*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 786*/    if (item == null)
/* 787*/      return null; 
/* 788*/    if (item.getType() == propType)
/* 789*/      return item.getNext(); 
/* 791*/    PropListItem result = removeProp(item.getNext(), propType);
/* 792*/    if (result != item.getNext())
/* 793*/      return item.chain(result); 
/* 795*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 801*/    PropListItem item = lookupProperty(propType);
/* 802*/    if (item == null)
/* 803*/      return null; 
/* 805*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 809*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 817*/    PropListItem item = lookupProperty(propType);
/* 818*/    if (item == null)
/* 819*/      return 0; 
/* 821*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 825*/    PropListItem item = lookupProperty(propType);
/* 826*/    if (item == null)
/* 827*/      throw new IllegalStateException("missing prop: " + propType); 
/* 829*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 833*/    removeProp(propType);
/* 834*/    if (value != null)
/* 835*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 840*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 844*/    removeProp(propType);
/* 845*/    if (value != 0)
/* 846*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 851*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 855*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 860*/    int count = 0;
/* 861*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 862*/      count++; 
/* 865*/    int[] keys = new int[count];
/* 866*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 867*/      count--;
/* 868*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 871*/    Arrays.sort(keys);
/* 872*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 877*/    if (getType() == 39)
/* 878*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 881*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 887*/    if (getType() == 39)
/* 888*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 891*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 897*/    if (getType() == 40)
/* 898*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 901*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/* 907*/    if (getType() == 40)
/* 908*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 911*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 917*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 924*/    StringBuilder sb = new StringBuilder();
/* 925*/    toString(sb, printSource, printAnnotations, printType);
/* 926*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 934*/    sb.append(Token.name(this.type));
/* 935*/    if (this instanceof StringNode) {
/* 936*/      sb.append(' ');
/* 937*/      sb.append(getString());
/* 938*/    } else if (this.type == 105) {
/* 939*/      sb.append(' ');
/* 943*/      if (this.first == null || this.first.getType() != 38) {
/* 944*/        sb.append("<invalid>");
/*   0*/      } else {
/* 946*/        sb.append(this.first.getString());
/*   0*/      } 
/* 948*/    } else if (this.type == 39) {
/* 949*/      sb.append(' ');
/* 950*/      sb.append(getDouble());
/*   0*/    } 
/* 952*/    if (printSource) {
/* 953*/      int lineno = getLineno();
/* 954*/      if (lineno != -1) {
/* 955*/        sb.append(' ');
/* 956*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 960*/    if (printAnnotations) {
/* 961*/      int[] keys = getSortedPropTypes();
/* 962*/      for (int i = 0; i < keys.length; i++) {
/* 963*/        int type = keys[i];
/* 964*/        PropListItem x = lookupProperty(type);
/* 965*/        sb.append(" [");
/* 966*/        sb.append(propToString(type));
/* 967*/        sb.append(": ");
/* 969*/        switch (type) {
/*   0*/        
/*   0*/        } 
/* 971*/        String value = x.toString();
/* 974*/        sb.append(value);
/* 975*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/* 979*/    if (printType && 
/* 980*/      this.jsType != null) {
/* 981*/      String jsTypeString = this.jsType.toString();
/* 982*/      if (jsTypeString != null) {
/* 983*/        sb.append(" : ");
/* 984*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/* 992*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/* 997*/      StringBuilder s = new StringBuilder();
/* 998*/      appendStringTree(s);
/* 999*/      return s.toString();
/*1000*/    } catch (IOException e) {
/*1001*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1006*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1011*/    for (int i = 0; i != level; i++)
/*1012*/      sb.append("    "); 
/*1014*/    sb.append(n.toString());
/*1015*/    sb.append('\n');
/*1016*/    Node cursor = n.getFirstChild();
/*1017*/    for (; cursor != null; 
/*1018*/      cursor = cursor.getNext())
/*1019*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1076*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1081*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1085*/    StaticSourceFile file = getStaticSourceFile();
/*1086*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1091*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1098*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1105*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1109*/    StaticSourceFile file = getStaticSourceFile();
/*1110*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1114*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1118*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1122*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1126*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1130*/    StaticSourceFile file = getStaticSourceFile();
/*1131*/    if (file == null)
/*1132*/      return -1; 
/*1134*/    int lineno = getLineno();
/*1135*/    if (lineno == -1)
/*1136*/      return -1; 
/*1138*/    return file.getLineOffset(lineno) + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1142*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1146*/    int charno = getCharno();
/*1147*/    if (charno == -1)
/*1148*/      charno = 0; 
/*1150*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1154*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1158*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1162*/    this.sourcePosition = sourcePosition;
/*1164*/    Node child = getFirstChild();
/*1165*/    for (; child != null; child = child.getNext())
/*1166*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1177*/    if (lineno < 0 || charno < 0)
/*1178*/      return -1; 
/*1179*/    if ((charno & 0xFFFFF000) != 0)
/*1180*/      return lineno << 12 | 0xFFF; 
/*1182*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1191*/    if (lineCharNo == -1)
/*1192*/      return -1; 
/*1194*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1203*/    if (lineCharNo == -1)
/*1204*/      return -1; 
/*1206*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1223*/    if (this.first == null)
/*1224*/      return Collections.emptySet(); 
/*1226*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1240*/    return new SiblingNodeIterable(this);
/*   0*/  }
/*   0*/  
/*   0*/  private static final class SiblingNodeIterable implements Iterable<Node>, Iterator<Node> {
/*   0*/    private final Node start;
/*   0*/    
/*   0*/    private Node current;
/*   0*/    
/*   0*/    private boolean used;
/*   0*/    
/*   0*/    SiblingNodeIterable(Node start) {
/*1253*/      this.start = start;
/*1254*/      this.current = start;
/*1255*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1260*/      if (!this.used) {
/*1261*/        this.used = true;
/*1262*/        return this;
/*   0*/      } 
/*1270*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1276*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1281*/      if (this.current == null)
/*1282*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1285*/        return this.current;
/*   0*/      } finally {
/*1287*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1293*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1301*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1305*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1314*/    Preconditions.checkArgument((level >= 0));
/*1315*/    Node node = this;
/*1316*/    while (node != null && level-- > 0)
/*1317*/      node = node.getParent(); 
/*1319*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1326*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1339*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1344*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1347*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1352*/            if (!hasNext())
/*1352*/              throw new NoSuchElementException(); 
/*1353*/            Node n = Node.AncestorIterable.this.cur;
/*1354*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1355*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1360*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1373*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1383*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1387*/    int c = 0;
/*1388*/    for (Node n = this.first; n != null; n = n.next)
/*1389*/      c++; 
/*1391*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1396*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1397*/      if (child == n)
/*1398*/        return true; 
/*   0*/    } 
/*1401*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1409*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1410*/    if (diff != null)
/*1411*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1417*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1426*/    if (!isEquivalentTo(node2, false, false))
/*1427*/      return new NodeMismatch(this, node2); 
/*1430*/    NodeMismatch res = null;
/*1432*/    Node n = this.first, n2 = node2.first;
/*1433*/    for (; res == null && n != null; 
/*1434*/      n = n.next, n2 = n2.next) {
/*1435*/      if (node2 == null)
/*1436*/        throw new IllegalStateException(); 
/*1438*/      res = n.checkTreeEqualsImpl(n2);
/*1439*/      if (res == null)
/*1440*/        return res; 
/*   0*/    } 
/*1443*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1453*/    if (!isEquivalentTo(node2, true, false))
/*1454*/      return new NodeMismatch(this, node2); 
/*1457*/    NodeMismatch res = null;
/*1459*/    Node n = this.first, n2 = node2.first;
/*1460*/    for (; res == null && n != null; 
/*1461*/      n = n.next, n2 = n2.next) {
/*1462*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1463*/      if (res != null)
/*1464*/        return res; 
/*   0*/    } 
/*1467*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1472*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1480*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1490*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getClass() != node.getClass())
/*1493*/      return false; 
/*1496*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1497*/      return false; 
/*1500*/    if (this.type == 102 || this.type == 103) {
/*1501*/      int post1 = getIntProp(32);
/*1502*/      int post2 = node.getIntProp(32);
/*1503*/      if (post1 != post2)
/*1504*/        return false; 
/*1506*/    } else if (this.type == 40 || this.type == 154) {
/*1507*/      if (this.type == 154) {
/*1508*/        int quoted1 = getIntProp(36);
/*1509*/        int quoted2 = node.getIntProp(36);
/*1510*/        if (quoted1 != quoted2)
/*1511*/          return false; 
/*   0*/      } 
/*1515*/      int slashV1 = getIntProp(54);
/*1516*/      int slashV2 = node.getIntProp(54);
/*1517*/      if (slashV1 != slashV2)
/*1518*/        return false; 
/*1520*/    } else if (this.type == 37 && 
/*1521*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1522*/      return false;
/*   0*/    } 
/*1526*/    if (recurse) {
/*1528*/      Node n = this.first, n2 = node.first;
/*1529*/      for (; n != null; 
/*1530*/        n = n.next, n2 = n2.next) {
/*1531*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1532*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1537*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1549*/    if (this.type == 38) {
/*1550*/      String name = getString();
/*1551*/      return name.isEmpty() ? null : name;
/*   0*/    } 
/*1552*/    if (this.type == 33) {
/*1553*/      String left = getFirstChild().getQualifiedName();
/*1554*/      if (left == null)
/*1555*/        return null; 
/*1557*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1558*/    if (this.type == 42)
/*1559*/      return "this"; 
/*1561*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1570*/    switch (getType()) {
/*   0*/      case 38:
/*1572*/        return !getString().isEmpty();
/*   0*/      case 42:
/*1574*/        return true;
/*   0*/      case 33:
/*1576*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1578*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1588*/    switch (getType()) {
/*   0*/      case 38:
/*1590*/        return !getString().isEmpty();
/*   0*/      case 33:
/*1592*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1594*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1606*/    Preconditions.checkState((this.parent != null));
/*1607*/    this.parent.removeChild(this);
/*1608*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1618*/    Node child = this.first;
/*1619*/    if (child != null)
/*1620*/      removeChild(child); 
/*1622*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1629*/    Node children = this.first;
/*1630*/    for (Node child = this.first; child != null; child = child.getNext())
/*1631*/      child.parent = null; 
/*1633*/    this.first = null;
/*1634*/    this.last = null;
/*1635*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1643*/    for (Node child = this.first; child != null; ) {
/*1644*/      Node nextChild = child.getNext();
/*1645*/      child.parent = null;
/*1646*/      child.next = null;
/*1647*/      child = nextChild;
/*   0*/    } 
/*1649*/    this.first = null;
/*1650*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1654*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1656*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1659*/    Node child = prev.next;
/*1660*/    prev.next = child.next;
/*1661*/    if (child == this.last)
/*1661*/      this.last = prev; 
/*1662*/    child.next = null;
/*1663*/    child.parent = null;
/*1664*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1673*/      result = (Node)clone();
/*1676*/      result.next = null;
/*1677*/      result.first = null;
/*1678*/      result.last = null;
/*1679*/      result.parent = null;
/*1680*/    } catch (CloneNotSupportedException e) {
/*1681*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1683*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1690*/    Node result = cloneNode();
/*1691*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1692*/      Node n2clone = n2.cloneTree();
/*1693*/      n2clone.parent = result;
/*1694*/      if (result.last != null)
/*1695*/        result.last.next = n2clone; 
/*1697*/      if (result.first == null)
/*1698*/        result.first = n2clone; 
/*1700*/      result.last = n2clone;
/*   0*/    } 
/*1702*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1713*/    if (getProp(40) == null)
/*1714*/      putProp(40, other.getProp(40)); 
/*1717*/    if (getProp(51) == null) {
/*1718*/      putProp(51, other.getProp(51));
/*1719*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1722*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1732*/    copyInformationFrom(other);
/*1733*/    Node child = getFirstChild();
/*1734*/    for (; child != null; child = child.getNext())
/*1735*/      child.copyInformationFromForTree(other); 
/*1738*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*1746*/    putProp(40, other.getProp(40));
/*1747*/    putProp(51, other.getProp(51));
/*1748*/    this.sourcePosition = other.sourcePosition;
/*1749*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcref(Node other) {
/*1753*/    return useSourceInfoFrom(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*1761*/    useSourceInfoFrom(other);
/*1762*/    Node child = getFirstChild();
/*1763*/    for (; child != null; child = child.getNext())
/*1764*/      child.useSourceInfoFromForTree(other); 
/*1767*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcrefTree(Node other) {
/*1771*/    return useSourceInfoFromForTree(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*1779*/    if (getProp(40) == null)
/*1780*/      putProp(40, other.getProp(40)); 
/*1783*/    if (getProp(51) == null) {
/*1784*/      putProp(51, other.getProp(51));
/*1785*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1788*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*1796*/    useSourceInfoIfMissingFrom(other);
/*1797*/    Node child = getFirstChild();
/*1798*/    for (; child != null; child = child.getNext())
/*1799*/      child.useSourceInfoIfMissingFromForTree(other); 
/*1802*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1809*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1813*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1817*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1830*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1831*/      if (jsDocInfo == null)
/*1834*/        jsDocInfo = new JSDocInfo(false); 
/*1836*/      String license = jsDocInfo.getLicense();
/*1837*/      if (license == null)
/*1838*/        license = ""; 
/*1840*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1841*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1851*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*1858*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1867*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1876*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*1885*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*1894*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*1902*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*1910*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*1917*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*1925*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*1933*/    if (getJSDocInfo() == null)
/*1934*/      setJSDocInfo(new JSDocInfo(false)); 
/*1936*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*1944*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*1952*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*1985*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*1990*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*1994*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2001*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2009*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2015*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2019*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2024*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2029*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2033*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2041*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2046*/      removeFlag(1);
/*2047*/      removeFlag(4);
/*2048*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2052*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2056*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2060*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2064*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2068*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2076*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2088*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2097*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2104*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2111*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2118*/    throw new IllegalStateException("not a StringNode");
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2126*/      this.nodeA = nodeA;
/*2127*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2132*/      if (object instanceof NodeMismatch) {
/*2133*/        NodeMismatch that = (NodeMismatch)object;
/*2134*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2136*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2141*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAdd() {
/*2149*/    return (getType() == 21);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAnd() {
/*2153*/    return (getType() == 101);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayLit() {
/*2157*/    return (getType() == 63);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssign() {
/*2161*/    return (getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssignAdd() {
/*2165*/    return (getType() == 93);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*2169*/    return (getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBreak() {
/*2173*/    return (getType() == 116);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCall() {
/*2177*/    return (getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCase() {
/*2181*/    return (getType() == 111);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCatch() {
/*2185*/    return (getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isComma() {
/*2189*/    return (getType() == 85);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContinue() {
/*2193*/    return (getType() == 117);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDebugger() {
/*2197*/    return (getType() == 152);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDec() {
/*2201*/    return (getType() == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDefaultCase() {
/*2205*/    return (getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDelProp() {
/*2209*/    return (getType() == 31);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDo() {
/*2213*/    return (getType() == 114);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*2217*/    return (getType() == 124);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExprResult() {
/*2221*/    return (getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFalse() {
/*2225*/    return (getType() == 43);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFor() {
/*2229*/    return (getType() == 115);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFunction() {
/*2233*/    return (getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetterDef() {
/*2237*/    return (getType() == 147);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetElem() {
/*2241*/    return (getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetProp() {
/*2245*/    return (getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isHook() {
/*2249*/    return (getType() == 98);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIf() {
/*2253*/    return (getType() == 108);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIn() {
/*2257*/    return (getType() == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInc() {
/*2261*/    return (getType() == 102);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInstanceOf() {
/*2265*/    return (getType() == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabel() {
/*2269*/    return (getType() == 126);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabelName() {
/*2273*/    return (getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isName() {
/*2277*/    return (getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNE() {
/*2281*/    return (getType() == 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNew() {
/*2285*/    return (getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNot() {
/*2289*/    return (getType() == 26);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*2293*/    return (getType() == 41);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNumber() {
/*2297*/    return (getType() == 39);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isObjectLit() {
/*2301*/    return (getType() == 64);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOr() {
/*2305*/    return (getType() == 100);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isParamList() {
/*2309*/    return (getType() == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRegExp() {
/*2313*/    return (getType() == 47);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isReturn() {
/*2317*/    return (getType() == 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isScript() {
/*2321*/    return (getType() == 132);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSetterDef() {
/*2325*/    return (getType() == 148);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isString() {
/*2329*/    return (getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isStringKey() {
/*2333*/    return (getType() == 154);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSwitch() {
/*2337*/    return (getType() == 110);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThis() {
/*2341*/    return (getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrow() {
/*2345*/    return (getType() == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrue() {
/*2349*/    return (getType() == 44);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTry() {
/*2353*/    return (getType() == 77);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeOf() {
/*2357*/    return (getType() == 32);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVar() {
/*2361*/    return (getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVoid() {
/*2365*/    return (getType() == 122);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWhile() {
/*2369*/    return (getType() == 113);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWith() {
/*2373*/    return (getType() == 119);
/*   0*/  }
/*   0*/}
