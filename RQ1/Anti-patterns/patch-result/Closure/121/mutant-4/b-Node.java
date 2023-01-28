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
/*   0*/  public static final int CHANGE_TIME = 56;
/*   0*/  
/*   0*/  public static final int REFLECTED_OBJECT = 57;
/*   0*/  
/*   0*/  public static final int LAST_PROP = 57;
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
/* 114*/    switch (propType) {
/*   0*/      case 30:
/* 115*/        return "var_args_name";
/*   0*/      case 29:
/* 117*/        return "jsdoc_info";
/*   0*/      case 32:
/* 119*/        return "incrdecr";
/*   0*/      case 36:
/* 120*/        return "quoted";
/*   0*/      case 37:
/* 121*/        return "opt_arg";
/*   0*/      case 38:
/* 123*/        return "synthetic";
/*   0*/      case 39:
/* 124*/        return "empty_block";
/*   0*/      case 40:
/* 125*/        return "originalname";
/*   0*/      case 42:
/* 126*/        return "side_effect_flags";
/*   0*/      case 43:
/* 128*/        return "is_constant_name";
/*   0*/      case 46:
/* 129*/        return "is_namespace";
/*   0*/      case 47:
/* 130*/        return "is_dispatcher";
/*   0*/      case 48:
/* 131*/        return "directives";
/*   0*/      case 49:
/* 132*/        return "direct_eval";
/*   0*/      case 50:
/* 133*/        return "free_call";
/*   0*/      case 51:
/* 134*/        return "source_file";
/*   0*/      case 53:
/* 135*/        return "input_id";
/*   0*/      case 52:
/* 136*/        return "length";
/*   0*/      case 54:
/* 137*/        return "slash_v";
/*   0*/      case 55:
/* 138*/        return "inferred";
/*   0*/      case 56:
/* 139*/        return "change_time";
/*   0*/      case 57:
/* 140*/        return "reflected_object";
/*   0*/    } 
/* 142*/    throw new IllegalStateException("unexpected prop id " + propType);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 151*/      super(39);
/* 152*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 156*/      super(39, lineno, charno);
/* 157*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 162*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 167*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recur, boolean shallow) {
/* 173*/      boolean equiv = super.isEquivalentTo(node, compareJsType, recur, shallow);
/* 174*/      if (equiv) {
/* 175*/        double thisValue = getDouble();
/* 176*/        double thatValue = ((NumberNode)node).getDouble();
/* 177*/        if (thisValue == thatValue)
/* 179*/          return (thisValue != 0.0D || 1.0D / thisValue == 1.0D / thatValue); 
/*   0*/      } 
/* 182*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 193*/      super(type);
/* 194*/      if (null == str)
/* 195*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 197*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 201*/      super(type, lineno, charno);
/* 202*/      if (null == str)
/* 203*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 205*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 214*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 223*/      if (null == str)
/* 224*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 226*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recur, boolean shallow) {
/* 232*/      return (super.isEquivalentTo(node, compareJsType, recur, shallow) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 244*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 252*/      putBooleanProp(36, true);
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
/* 275*/      this.propType = propType;
/* 276*/      this.next = next;
/*   0*/    }
/*   0*/    
/*   0*/    public int getType() {
/* 281*/      return this.propType;
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem getNext() {
/* 286*/      return this.next;
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
/* 301*/      super(propType, next);
/* 302*/      this.objectValue = objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 307*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 312*/      return this.objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 317*/      return (this.objectValue == null) ? "null" : this.objectValue.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 322*/      return new ObjectPropListItem(getType(), this.objectValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class IntPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    IntPropListItem(int propType, int intValue, Node.PropListItem next) {
/* 333*/      super(propType, next);
/* 334*/      this.intValue = intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 339*/      return this.intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 344*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 349*/      return String.valueOf(this.intValue);
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 354*/      return new IntPropListItem(getType(), this.intValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 359*/    this.type = nodeType;
/* 360*/    this.parent = null;
/* 361*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 365*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 367*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 370*/    this.type = nodeType;
/* 371*/    this.parent = null;
/* 372*/    this.first = this.last = child;
/* 373*/    child.next = null;
/* 374*/    child.parent = this;
/* 375*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 379*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 381*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 383*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 385*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 387*/    this.type = nodeType;
/* 388*/    this.parent = null;
/* 389*/    this.first = left;
/* 390*/    this.last = right;
/* 391*/    left.next = right;
/* 392*/    left.parent = this;
/* 393*/    right.next = null;
/* 394*/    right.parent = this;
/* 395*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 399*/    Preconditions.checkArgument((left.parent == null));
/* 400*/    Preconditions.checkArgument((left.next == null));
/* 401*/    Preconditions.checkArgument((mid.parent == null));
/* 402*/    Preconditions.checkArgument((mid.next == null));
/* 403*/    Preconditions.checkArgument((right.parent == null));
/* 404*/    Preconditions.checkArgument((right.next == null));
/* 405*/    this.type = nodeType;
/* 406*/    this.parent = null;
/* 407*/    this.first = left;
/* 408*/    this.last = right;
/* 409*/    left.next = mid;
/* 410*/    left.parent = this;
/* 411*/    mid.next = right;
/* 412*/    mid.parent = this;
/* 413*/    right.next = null;
/* 414*/    right.parent = this;
/* 415*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 419*/    Preconditions.checkArgument((left.parent == null));
/* 420*/    Preconditions.checkArgument((left.next == null));
/* 421*/    Preconditions.checkArgument((mid.parent == null));
/* 422*/    Preconditions.checkArgument((mid.next == null));
/* 423*/    Preconditions.checkArgument((mid2.parent == null));
/* 424*/    Preconditions.checkArgument((mid2.next == null));
/* 425*/    Preconditions.checkArgument((right.parent == null));
/* 426*/    Preconditions.checkArgument((right.next == null));
/* 427*/    this.type = nodeType;
/* 428*/    this.parent = null;
/* 429*/    this.first = left;
/* 430*/    this.last = right;
/* 431*/    left.next = mid;
/* 432*/    left.parent = this;
/* 433*/    mid.next = mid2;
/* 434*/    mid.parent = this;
/* 435*/    mid2.next = right;
/* 436*/    mid2.parent = this;
/* 437*/    right.next = null;
/* 438*/    right.parent = this;
/* 439*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 443*/    this.type = nodeType;
/* 444*/    this.parent = null;
/* 445*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 449*/    this(nodeType, child);
/* 450*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 454*/    this(nodeType, left, right);
/* 455*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 460*/    this(nodeType, left, mid, right);
/* 461*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 466*/    this(nodeType, left, mid, mid2, right);
/* 467*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 471*/    this(nodeType, children);
/* 472*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 476*/    this.type = nodeType;
/* 477*/    this.parent = null;
/* 478*/    if (children.length != 0) {
/* 479*/      this.first = children[0];
/* 480*/      this.last = children[children.length - 1];
/* 482*/      for (int i = 1; i < children.length; i++) {
/* 483*/        if (null != (children[i - 1]).next)
/* 485*/          throw new IllegalArgumentException("duplicate child"); 
/* 487*/        (children[i - 1]).next = children[i];
/* 488*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 489*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 491*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 492*/      (children[children.length - 1]).parent = this;
/* 494*/      if (null != this.last.next)
/* 496*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 502*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 506*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 510*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 514*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 518*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 522*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 526*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 530*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 534*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 538*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 542*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 546*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 550*/    if (child == this.first)
/* 551*/      return null; 
/* 553*/    Node n = this.first;
/* 554*/    while (n.next != child) {
/* 555*/      n = n.next;
/* 556*/      if (n == null)
/* 557*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 560*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 564*/    Node n = this.first;
/* 565*/    while (i > 0) {
/* 566*/      n = n.next;
/* 567*/      i--;
/*   0*/    } 
/* 569*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOfChild(Node child) {
/* 573*/    Node n = this.first;
/* 574*/    int i = 0;
/* 575*/    while (n != null) {
/* 576*/      if (child == n)
/* 577*/        return i; 
/* 580*/      n = n.next;
/* 581*/      i++;
/*   0*/    } 
/* 583*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 587*/    Node n = this;
/* 588*/    while (n.next != null)
/* 589*/      n = n.next; 
/* 591*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 595*/    Preconditions.checkArgument((child.parent == null));
/* 596*/    Preconditions.checkArgument((child.next == null));
/* 597*/    child.parent = this;
/* 598*/    child.next = this.first;
/* 599*/    this.first = child;
/* 600*/    if (this.last == null)
/* 601*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 606*/    Preconditions.checkArgument((child.parent == null));
/* 607*/    Preconditions.checkArgument((child.next == null));
/* 608*/    child.parent = this;
/* 609*/    child.next = null;
/* 610*/    if (this.last == null) {
/* 611*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 614*/    this.last.next = child;
/* 615*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 619*/    for (Node child = children; child != null; child = child.next) {
/* 620*/      Preconditions.checkArgument((child.parent == null));
/* 621*/      child.parent = this;
/*   0*/    } 
/* 623*/    Node lastSib = children.getLastSibling();
/* 624*/    lastSib.next = this.first;
/* 625*/    this.first = children;
/* 626*/    if (this.last == null)
/* 627*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 632*/    addChildrenAfter(children, getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 639*/    Preconditions.checkArgument((node != null && node.parent == this), "The existing child node of the parent should not be null.");
/* 641*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 643*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 645*/    if (this.first == node) {
/* 646*/      newChild.parent = this;
/* 647*/      newChild.next = this.first;
/* 648*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 651*/    Node prev = getChildBefore(node);
/* 652*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 659*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 661*/    addChildrenAfter(newChild, node);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenAfter(Node children, Node node) {
/* 668*/    Preconditions.checkArgument((node == null || node.parent == this));
/* 669*/    for (Node child = children; child != null; child = child.next) {
/* 670*/      Preconditions.checkArgument((child.parent == null));
/* 671*/      child.parent = this;
/*   0*/    } 
/* 674*/    Node lastSibling = children.getLastSibling();
/* 675*/    if (node != null) {
/* 676*/      Node oldNext = node.next;
/* 677*/      node.next = children;
/* 678*/      lastSibling.next = oldNext;
/* 679*/      if (node == this.last)
/* 680*/        this.last = lastSibling; 
/*   0*/    } else {
/* 684*/      if (this.first != null) {
/* 685*/        lastSibling.next = this.first;
/*   0*/      } else {
/* 687*/        this.last = lastSibling;
/*   0*/      } 
/* 689*/      this.first = children;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 697*/    Node prev = getChildBefore(child);
/* 698*/    if (prev == null) {
/* 699*/      this.first = this.first.next;
/*   0*/    } else {
/* 701*/      prev.next = child.next;
/*   0*/    } 
/* 703*/    if (child == this.last)
/* 704*/      this.last = prev; 
/* 706*/    child.next = null;
/* 707*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 714*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 716*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 720*/    newChild.copyInformationFrom(child);
/* 722*/    newChild.next = child.next;
/* 723*/    newChild.parent = this;
/* 724*/    if (child == this.first) {
/* 725*/      this.first = newChild;
/*   0*/    } else {
/* 727*/      Node prev = getChildBefore(child);
/* 728*/      prev.next = newChild;
/*   0*/    } 
/* 730*/    if (child == this.last)
/* 731*/      this.last = newChild; 
/* 733*/    child.next = null;
/* 734*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 738*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 741*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 743*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 747*/    newChild.copyInformationFrom(prevChild);
/* 749*/    Node child = prevChild.next;
/* 750*/    newChild.next = child.next;
/* 751*/    newChild.parent = this;
/* 752*/    prevChild.next = newChild;
/* 753*/    if (child == this.last)
/* 754*/      this.last = newChild; 
/* 756*/    child.next = null;
/* 757*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 762*/    PropListItem x = this.propListHead;
/* 763*/    while (x != null && propType != x.getType())
/* 764*/      x = x.getNext(); 
/* 766*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 777*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 779*/    this.propListHead = other.propListHead;
/* 780*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 784*/    PropListItem result = removeProp(this.propListHead, propType);
/* 785*/    if (result != this.propListHead)
/* 786*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 797*/    if (item == null)
/* 798*/      return null; 
/* 799*/    if (item.getType() == propType)
/* 800*/      return item.getNext(); 
/* 802*/    PropListItem result = removeProp(item.getNext(), propType);
/* 803*/    if (result != item.getNext())
/* 804*/      return item.chain(result); 
/* 806*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 812*/    PropListItem item = lookupProperty(propType);
/* 813*/    if (item == null)
/* 814*/      return null; 
/* 816*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 820*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 828*/    PropListItem item = lookupProperty(propType);
/* 829*/    if (item == null)
/* 830*/      return 0; 
/* 832*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 836*/    PropListItem item = lookupProperty(propType);
/* 837*/    if (item == null)
/* 838*/      throw new IllegalStateException("missing prop: " + propType); 
/* 840*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 844*/    removeProp(propType);
/* 845*/    if (value != null)
/* 846*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 851*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 855*/    removeProp(propType);
/* 856*/    if (value != 0)
/* 857*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 862*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 866*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 871*/    int count = 0;
/* 872*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 873*/      count++; 
/* 876*/    int[] keys = new int[count];
/* 877*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 878*/      count--;
/* 879*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 882*/    Arrays.sort(keys);
/* 883*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 888*/    if (getType() == 39)
/* 889*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 892*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double value) throws UnsupportedOperationException {
/* 901*/    if (getType() == 39)
/* 902*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 905*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 911*/    if (getType() == 40)
/* 912*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 915*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String value) throws UnsupportedOperationException {
/* 924*/    if (getType() == 40 || getType() == 38)
/* 925*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 928*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 934*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 941*/    StringBuilder sb = new StringBuilder();
/* 942*/    toString(sb, printSource, printAnnotations, printType);
/* 943*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 951*/    sb.append(Token.name(this.type));
/* 952*/    if (this instanceof StringNode) {
/* 953*/      sb.append(' ');
/* 954*/      sb.append(getString());
/* 955*/    } else if (this.type == 105) {
/* 956*/      sb.append(' ');
/* 960*/      if (this.first == null || this.first.getType() != 38) {
/* 961*/        sb.append("<invalid>");
/*   0*/      } else {
/* 963*/        sb.append(this.first.getString());
/*   0*/      } 
/* 965*/    } else if (this.type == 39) {
/* 966*/      sb.append(' ');
/* 967*/      sb.append(getDouble());
/*   0*/    } 
/* 969*/    if (printSource) {
/* 970*/      int lineno = getLineno();
/* 971*/      if (lineno != -1) {
/* 972*/        sb.append(' ');
/* 973*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 977*/    if (printAnnotations) {
/* 978*/      int[] keys = getSortedPropTypes();
/* 979*/      for (int i = 0; i < keys.length; i++) {
/* 980*/        int type = keys[i];
/* 981*/        PropListItem x = lookupProperty(type);
/* 982*/        sb.append(" [");
/* 983*/        sb.append(propToString(type));
/* 984*/        sb.append(": ");
/* 986*/        switch (type) {
/*   0*/        
/*   0*/        } 
/* 988*/        String value = x.toString();
/* 991*/        sb.append(value);
/* 992*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/* 996*/    if (printType && 
/* 997*/      this.jsType != null) {
/* 998*/      String jsTypeString = this.jsType.toString();
/* 999*/      if (jsTypeString != null) {
/*1000*/        sb.append(" : ");
/*1001*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1009*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1014*/      StringBuilder s = new StringBuilder();
/*1015*/      appendStringTree(s);
/*1016*/      return s.toString();
/*1017*/    } catch (IOException e) {
/*1018*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1023*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1028*/    for (int i = 0; i != level; i++)
/*1029*/      sb.append("    "); 
/*1031*/    sb.append(n.toString());
/*1032*/    sb.append('\n');
/*1033*/    Node cursor = n.getFirstChild();
/*1034*/    for (; cursor != null; 
/*1035*/      cursor = cursor.getNext())
/*1036*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1093*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1098*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1102*/    StaticSourceFile file = getStaticSourceFile();
/*1103*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1108*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1115*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1122*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1126*/    StaticSourceFile file = getStaticSourceFile();
/*1127*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1131*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1135*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1139*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1143*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1147*/    StaticSourceFile file = getStaticSourceFile();
/*1148*/    if (file == null)
/*1149*/      return -1; 
/*1151*/    int lineno = getLineno();
/*1152*/    if (lineno == -1)
/*1153*/      return -1; 
/*1155*/    return file.getLineOffset(lineno) + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1159*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1163*/    int charno = getCharno();
/*1164*/    if (charno == -1)
/*1165*/      charno = 0; 
/*1167*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1171*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1175*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1179*/    this.sourcePosition = sourcePosition;
/*1181*/    Node child = getFirstChild();
/*1182*/    for (; child != null; child = child.getNext())
/*1183*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1194*/    if (lineno < 0 || charno < 0)
/*1195*/      return -1; 
/*1196*/    if ((charno & 0xFFFFF000) != 0)
/*1197*/      return lineno << 12 | 0xFFF; 
/*1199*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1208*/    if (lineCharNo == -1)
/*1209*/      return -1; 
/*1211*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1220*/    if (lineCharNo == -1)
/*1221*/      return -1; 
/*1223*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1240*/    if (this.first == null)
/*1241*/      return Collections.emptySet(); 
/*1243*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1257*/    return new SiblingNodeIterable(this);
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
/*1270*/      this.start = start;
/*1271*/      this.current = start;
/*1272*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1277*/      if (!this.used) {
/*1278*/        this.used = true;
/*1279*/        return this;
/*   0*/      } 
/*1287*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1293*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1298*/      if (this.current == null)
/*1299*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1302*/        return this.current;
/*   0*/      } finally {
/*1304*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1310*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1318*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1322*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1331*/    Preconditions.checkArgument((level >= 0));
/*1332*/    Node node = this;
/*1333*/    while (node != null && level-- > 0)
/*1334*/      node = node.getParent(); 
/*1336*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1343*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1356*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1361*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1364*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1369*/            if (!hasNext())
/*1370*/              throw new NoSuchElementException(); 
/*1372*/            Node n = Node.AncestorIterable.this.cur;
/*1373*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1374*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1379*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1392*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1402*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1406*/    int c = 0;
/*1407*/    for (Node n = this.first; n != null; n = n.next)
/*1408*/      c++; 
/*1410*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1415*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1416*/      if (child == n)
/*1417*/        return true; 
/*   0*/    } 
/*1420*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1428*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1429*/    if (diff != null)
/*1430*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1436*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1445*/    if (!isEquivalentTo(node2, false, false, false))
/*1446*/      return new NodeMismatch(this, node2); 
/*1449*/    NodeMismatch res = null;
/*1451*/    Node n = this.first, n2 = node2.first;
/*1452*/    for (; res == null && n != null; 
/*1453*/      n = n2.next, n2 = n2.next) {
/*1454*/      if (node2 == null)
/*1455*/        throw new IllegalStateException(); 
/*1457*/      res = n.checkTreeEqualsImpl(n2);
/*1458*/      if (res != null)
/*1459*/        return res; 
/*   0*/    } 
/*1462*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1472*/    if (!isEquivalentTo(node2, true, false, false))
/*1473*/      return new NodeMismatch(this, node2); 
/*1476*/    NodeMismatch res = null;
/*1478*/    Node n = this.first, n2 = node2.first;
/*1479*/    for (; res == null && n != null; 
/*1480*/      n = n.next, n2 = n2.next) {
/*1481*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1482*/      if (res != null)
/*1483*/        return res; 
/*   0*/    } 
/*1486*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1491*/    return isEquivalentTo(node, false, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToShallow(Node node) {
/*1496*/    return isEquivalentTo(node, false, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1504*/    return isEquivalentTo(node, true, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recur, boolean shallow) {
/*1516*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getClass() != node.getClass())
/*1519*/      return false; 
/*1522*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1523*/      return false; 
/*1526*/    if (this.type == 102 || this.type == 103) {
/*1527*/      int post1 = getIntProp(32);
/*1528*/      int post2 = node.getIntProp(32);
/*1529*/      if (post1 != post2)
/*1530*/        return false; 
/*1532*/    } else if (this.type == 40 || this.type == 154) {
/*1533*/      if (this.type == 154) {
/*1534*/        int quoted1 = getIntProp(36);
/*1535*/        int quoted2 = node.getIntProp(36);
/*1536*/        if (quoted1 != quoted2)
/*1537*/          return false; 
/*   0*/      } 
/*1541*/      int slashV1 = getIntProp(54);
/*1542*/      int slashV2 = node.getIntProp(54);
/*1543*/      if (slashV1 != slashV2)
/*1544*/        return false; 
/*1546*/    } else if (this.type == 37 && 
/*1547*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1548*/      return false;
/*   0*/    } 
/*1552*/    if (recur) {
/*1554*/      Node n = this.first, n2 = node.first;
/*1555*/      for (; n != null; 
/*1556*/        n = n.next, n2 = n2.next) {
/*1557*/        if (!n.isEquivalentTo(n2, compareJsType, (!shallow || !n.isFunction()), shallow))
/*1559*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1564*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1576*/    if (this.type == 38) {
/*1577*/      String name = getString();
/*1578*/      return name.isEmpty() ? null : name;
/*   0*/    } 
/*1579*/    if (this.type == 33) {
/*1580*/      String left = getFirstChild().getQualifiedName();
/*1581*/      if (left == null)
/*1582*/        return null; 
/*1584*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1585*/    if (this.type == 42)
/*1586*/      return "this"; 
/*1588*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1597*/    switch (getType()) {
/*   0*/      case 38:
/*1599*/        return !getString().isEmpty();
/*   0*/      case 42:
/*1601*/        return true;
/*   0*/      case 33:
/*1603*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1605*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1615*/    switch (getType()) {
/*   0*/      case 38:
/*1617*/        return !getString().isEmpty();
/*   0*/      case 33:
/*1619*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1621*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1633*/    Preconditions.checkState((this.parent != null));
/*1634*/    this.parent.removeChild(this);
/*1635*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1645*/    Node child = this.first;
/*1646*/    if (child != null)
/*1647*/      removeChild(child); 
/*1649*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1656*/    Node children = this.first;
/*1657*/    for (Node child = this.first; child != null; child = child.getNext())
/*1658*/      child.parent = null; 
/*1660*/    this.first = null;
/*1661*/    this.last = null;
/*1662*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1670*/    for (Node child = this.first; child != null; ) {
/*1671*/      Node nextChild = child.getNext();
/*1672*/      child.parent = null;
/*1673*/      child.next = null;
/*1674*/      child = nextChild;
/*   0*/    } 
/*1676*/    this.first = null;
/*1677*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1681*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1683*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1686*/    Node child = prev.next;
/*1687*/    prev.next = child.next;
/*1688*/    if (child == this.last)
/*1689*/      this.last = prev; 
/*1691*/    child.next = null;
/*1692*/    child.parent = null;
/*1693*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1702*/      result = (Node)clone();
/*1705*/      result.next = null;
/*1706*/      result.first = null;
/*1707*/      result.last = null;
/*1708*/      result.parent = null;
/*1709*/    } catch (CloneNotSupportedException e) {
/*1710*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1712*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1719*/    Node result = cloneNode();
/*1720*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1721*/      Node n2clone = n2.cloneTree();
/*1722*/      n2clone.parent = result;
/*1723*/      if (result.last != null)
/*1724*/        result.last.next = n2clone; 
/*1726*/      if (result.first == null)
/*1727*/        result.first = n2clone; 
/*1729*/      result.last = n2clone;
/*   0*/    } 
/*1731*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1742*/    if (getProp(40) == null)
/*1743*/      putProp(40, other.getProp(40)); 
/*1746*/    if (getProp(51) == null) {
/*1747*/      putProp(51, other.getProp(51));
/*1748*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1751*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1761*/    copyInformationFrom(other);
/*1762*/    Node child = getFirstChild();
/*1763*/    for (; child != null; child = child.getNext())
/*1764*/      child.copyInformationFromForTree(other); 
/*1767*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*1775*/    putProp(40, other.getProp(40));
/*1776*/    putProp(51, other.getProp(51));
/*1777*/    this.sourcePosition = other.sourcePosition;
/*1778*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcref(Node other) {
/*1782*/    return useSourceInfoFrom(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*1790*/    useSourceInfoFrom(other);
/*1791*/    Node child = getFirstChild();
/*1792*/    for (; child != null; child = child.getNext())
/*1793*/      child.useSourceInfoFromForTree(other); 
/*1796*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcrefTree(Node other) {
/*1800*/    return useSourceInfoFromForTree(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*1808*/    if (getProp(40) == null)
/*1809*/      putProp(40, other.getProp(40)); 
/*1812*/    if (getProp(51) == null) {
/*1813*/      putProp(51, other.getProp(51));
/*1814*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1817*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*1825*/    useSourceInfoIfMissingFrom(other);
/*1826*/    Node child = getFirstChild();
/*1827*/    for (; child != null; child = child.getNext())
/*1828*/      child.useSourceInfoIfMissingFromForTree(other); 
/*1831*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1838*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1842*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1846*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1859*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1860*/      if (jsDocInfo == null)
/*1863*/        jsDocInfo = new JSDocInfo(false); 
/*1865*/      String license = jsDocInfo.getLicense();
/*1866*/      if (license == null)
/*1867*/        license = ""; 
/*1869*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1870*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1880*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public Node setJSDocInfo(JSDocInfo info) {
/*1887*/    putProp(29, info);
/*1888*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void setChangeTime(int time) {
/*1893*/    putIntProp(56, time);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChangeTime() {
/*1898*/    return getIntProp(56);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1907*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1916*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*1925*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*1934*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*1942*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*1950*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*1957*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*1965*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*1973*/    if (getJSDocInfo() == null)
/*1974*/      setJSDocInfo(new JSDocInfo(false)); 
/*1976*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*1984*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*1992*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2025*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2030*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2034*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2041*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2049*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2055*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2059*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags setAllFlags() {
/*2064*/      this.value = 0;
/*2065*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags clearAllFlags() {
/*2070*/      this.value = 31;
/*2071*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2075*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2083*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags setMutatesGlobalState() {
/*2088*/      removeFlag(1);
/*2089*/      removeFlag(4);
/*2090*/      removeFlag(2);
/*2091*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags setThrows() {
/*2095*/      removeFlag(8);
/*2096*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags setMutatesThis() {
/*2100*/      removeFlag(2);
/*2101*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags setMutatesArguments() {
/*2105*/      removeFlag(4);
/*2106*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public SideEffectFlags setReturnsTainted() {
/*2110*/      removeFlag(16);
/*2111*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2115*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2123*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesArgumentsCall() {
/*2134*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 11);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2146*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2155*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean mayMutateArguments() {
/*2160*/    return !areBitFlagsSet(getSideEffectFlags(), 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean mayMutateGlobalStateOrThrow() {
/*2165*/    return !areBitFlagsSet(getSideEffectFlags(), 9);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2173*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2180*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2187*/    throw new IllegalStateException("not a StringNode");
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2195*/      this.nodeA = nodeA;
/*2196*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2201*/      if (object instanceof NodeMismatch) {
/*2202*/        NodeMismatch that = (NodeMismatch)object;
/*2203*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2205*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2210*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAdd() {
/*2218*/    return (getType() == 21);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAnd() {
/*2222*/    return (getType() == 101);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayLit() {
/*2226*/    return (getType() == 63);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssign() {
/*2230*/    return (getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssignAdd() {
/*2234*/    return (getType() == 93);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*2238*/    return (getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBreak() {
/*2242*/    return (getType() == 116);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCall() {
/*2246*/    return (getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCase() {
/*2250*/    return (getType() == 111);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCast() {
/*2254*/    return (getType() == 155);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCatch() {
/*2258*/    return (getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isComma() {
/*2262*/    return (getType() == 85);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContinue() {
/*2266*/    return (getType() == 117);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDebugger() {
/*2270*/    return (getType() == 152);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDec() {
/*2274*/    return (getType() == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDefaultCase() {
/*2278*/    return (getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDelProp() {
/*2282*/    return (getType() == 31);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDo() {
/*2286*/    return (getType() == 114);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*2290*/    return (getType() == 124);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExprResult() {
/*2294*/    return (getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFalse() {
/*2298*/    return (getType() == 43);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFor() {
/*2302*/    return (getType() == 115);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFunction() {
/*2306*/    return (getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetterDef() {
/*2310*/    return (getType() == 147);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetElem() {
/*2314*/    return (getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetProp() {
/*2318*/    return (getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isHook() {
/*2322*/    return (getType() == 98);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIf() {
/*2326*/    return (getType() == 108);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIn() {
/*2330*/    return (getType() == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInc() {
/*2334*/    return (getType() == 102);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInstanceOf() {
/*2338*/    return (getType() == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabel() {
/*2342*/    return (getType() == 126);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabelName() {
/*2346*/    return (getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isName() {
/*2350*/    return (getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNE() {
/*2354*/    return (getType() == 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNew() {
/*2358*/    return (getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNot() {
/*2362*/    return (getType() == 26);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*2366*/    return (getType() == 41);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNumber() {
/*2370*/    return (getType() == 39);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isObjectLit() {
/*2374*/    return (getType() == 64);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOr() {
/*2378*/    return (getType() == 100);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isParamList() {
/*2382*/    return (getType() == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRegExp() {
/*2386*/    return (getType() == 47);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isReturn() {
/*2390*/    return (getType() == 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isScript() {
/*2394*/    return (getType() == 132);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSetterDef() {
/*2398*/    return (getType() == 148);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isString() {
/*2402*/    return (getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isStringKey() {
/*2406*/    return (getType() == 154);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSwitch() {
/*2410*/    return (getType() == 110);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThis() {
/*2414*/    return (getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrow() {
/*2418*/    return (getType() == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrue() {
/*2422*/    return (getType() == 44);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTry() {
/*2426*/    return (getType() == 77);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeOf() {
/*2430*/    return (getType() == 32);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVar() {
/*2434*/    return (getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVoid() {
/*2438*/    return (getType() == 122);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWhile() {
/*2442*/    return (getType() == 113);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWith() {
/*2446*/    return (getType() == 119);
/*   0*/  }
/*   0*/}
