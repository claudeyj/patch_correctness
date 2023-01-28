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
/*   0*/  public static final int LAST_PROP = 56;
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
/* 113*/    switch (propType) {
/*   0*/      case 30:
/* 114*/        return "var_args_name";
/*   0*/      case 29:
/* 116*/        return "jsdoc_info";
/*   0*/      case 32:
/* 118*/        return "incrdecr";
/*   0*/      case 36:
/* 119*/        return "quoted";
/*   0*/      case 37:
/* 120*/        return "opt_arg";
/*   0*/      case 38:
/* 122*/        return "synthetic";
/*   0*/      case 39:
/* 123*/        return "empty_block";
/*   0*/      case 40:
/* 124*/        return "originalname";
/*   0*/      case 42:
/* 125*/        return "side_effect_flags";
/*   0*/      case 43:
/* 127*/        return "is_constant_name";
/*   0*/      case 46:
/* 128*/        return "is_namespace";
/*   0*/      case 47:
/* 129*/        return "is_dispatcher";
/*   0*/      case 48:
/* 130*/        return "directives";
/*   0*/      case 49:
/* 131*/        return "direct_eval";
/*   0*/      case 50:
/* 132*/        return "free_call";
/*   0*/      case 51:
/* 133*/        return "source_file";
/*   0*/      case 53:
/* 134*/        return "input_id";
/*   0*/      case 52:
/* 135*/        return "length";
/*   0*/      case 54:
/* 136*/        return "slash_v";
/*   0*/      case 55:
/* 137*/        return "inferred";
/*   0*/      case 56:
/* 138*/        return "change_time";
/*   0*/    } 
/* 140*/    throw new IllegalStateException("unexpected prop id " + propType);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 149*/      super(39);
/* 150*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 154*/      super(39, lineno, charno);
/* 155*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 160*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 165*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recur, boolean shallow) {
/* 171*/      boolean equiv = super.isEquivalentTo(node, compareJsType, recur, shallow);
/* 172*/      if (equiv) {
/* 173*/        double thisValue = getDouble();
/* 174*/        double thatValue = ((NumberNode)node).getDouble();
/* 175*/        if (thisValue == thatValue)
/* 177*/          return (thisValue != 0.0D || 1.0D / thisValue == 1.0D / thatValue); 
/*   0*/      } 
/* 180*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 191*/      super(type);
/* 192*/      if (null == str)
/* 193*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 195*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 199*/      super(type, lineno, charno);
/* 200*/      if (null == str)
/* 201*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 203*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 212*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 221*/      if (null == str)
/* 222*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 224*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recur, boolean shallow) {
/* 230*/      return (super.isEquivalentTo(node, compareJsType, recur, shallow) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 242*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 250*/      putBooleanProp(36, true);
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
/* 273*/      this.propType = propType;
/* 274*/      this.next = next;
/*   0*/    }
/*   0*/    
/*   0*/    public int getType() {
/* 279*/      return this.propType;
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem getNext() {
/* 284*/      return this.next;
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
/* 299*/      super(propType, next);
/* 300*/      this.objectValue = objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 305*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 310*/      return this.objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 315*/      return (this.objectValue == null) ? "null" : this.objectValue.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 320*/      return new ObjectPropListItem(getType(), this.objectValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class IntPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    IntPropListItem(int propType, int intValue, Node.PropListItem next) {
/* 331*/      super(propType, next);
/* 332*/      this.intValue = intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 337*/      return this.intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 342*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 347*/      return String.valueOf(this.intValue);
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 352*/      return new IntPropListItem(getType(), this.intValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 357*/    this.type = nodeType;
/* 358*/    this.parent = null;
/* 359*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 363*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 365*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 368*/    this.type = nodeType;
/* 369*/    this.parent = null;
/* 370*/    this.first = this.last = child;
/* 371*/    child.next = null;
/* 372*/    child.parent = this;
/* 373*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 377*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 379*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 381*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 383*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 385*/    this.type = nodeType;
/* 386*/    this.parent = null;
/* 387*/    this.first = left;
/* 388*/    this.last = right;
/* 389*/    left.next = right;
/* 390*/    left.parent = this;
/* 391*/    right.next = null;
/* 392*/    right.parent = this;
/* 393*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 397*/    Preconditions.checkArgument((left.parent == null));
/* 398*/    Preconditions.checkArgument((left.next == null));
/* 399*/    Preconditions.checkArgument((mid.parent == null));
/* 400*/    Preconditions.checkArgument((mid.next == null));
/* 401*/    Preconditions.checkArgument((right.parent == null));
/* 402*/    Preconditions.checkArgument((right.next == null));
/* 403*/    this.type = nodeType;
/* 404*/    this.parent = null;
/* 405*/    this.first = left;
/* 406*/    this.last = right;
/* 407*/    left.next = mid;
/* 408*/    left.parent = this;
/* 409*/    mid.next = right;
/* 410*/    mid.parent = this;
/* 411*/    right.next = null;
/* 412*/    right.parent = this;
/* 413*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 417*/    Preconditions.checkArgument((left.parent == null));
/* 418*/    Preconditions.checkArgument((left.next == null));
/* 419*/    Preconditions.checkArgument((mid.parent == null));
/* 420*/    Preconditions.checkArgument((mid.next == null));
/* 421*/    Preconditions.checkArgument((mid2.parent == null));
/* 422*/    Preconditions.checkArgument((mid2.next == null));
/* 423*/    Preconditions.checkArgument((right.parent == null));
/* 424*/    Preconditions.checkArgument((right.next == null));
/* 425*/    this.type = nodeType;
/* 426*/    this.parent = null;
/* 427*/    this.first = left;
/* 428*/    this.last = right;
/* 429*/    left.next = mid;
/* 430*/    left.parent = this;
/* 431*/    mid.next = mid2;
/* 432*/    mid.parent = this;
/* 433*/    mid2.next = right;
/* 434*/    mid2.parent = this;
/* 435*/    right.next = null;
/* 436*/    right.parent = this;
/* 437*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 441*/    this.type = nodeType;
/* 442*/    this.parent = null;
/* 443*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 447*/    this(nodeType, child);
/* 448*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 452*/    this(nodeType, left, right);
/* 453*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 458*/    this(nodeType, left, mid, right);
/* 459*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 464*/    this(nodeType, left, mid, mid2, right);
/* 465*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 469*/    this(nodeType, children);
/* 470*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 474*/    this.type = nodeType;
/* 475*/    this.parent = null;
/* 476*/    if (children.length != 0) {
/* 477*/      this.first = children[0];
/* 478*/      this.last = children[children.length - 1];
/* 480*/      for (int i = 1; i < children.length; i++) {
/* 481*/        if (null != (children[i - 1]).next)
/* 483*/          throw new IllegalArgumentException("duplicate child"); 
/* 485*/        (children[i - 1]).next = children[i];
/* 486*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 487*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 489*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 490*/      (children[children.length - 1]).parent = this;
/* 492*/      if (null != this.last.next)
/* 494*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 500*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 504*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 508*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 512*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 516*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 520*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 524*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 528*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 532*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 536*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 540*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 544*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 548*/    if (child == this.first)
/* 549*/      return null; 
/* 551*/    Node n = this.first;
/* 552*/    while (n.next != child) {
/* 553*/      n = n.next;
/* 554*/      if (n == null)
/* 555*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 558*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 562*/    Node n = this.first;
/* 563*/    while (i > 0) {
/* 564*/      n = n.next;
/* 565*/      i--;
/*   0*/    } 
/* 567*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOfChild(Node child) {
/* 571*/    Node n = this.first;
/* 572*/    int i = 0;
/* 573*/    while (n != null) {
/* 574*/      if (child == n)
/* 575*/        return i; 
/* 578*/      n = n.next;
/* 579*/      i++;
/*   0*/    } 
/* 581*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 585*/    Node n = this;
/* 586*/    while (n.next != null)
/* 587*/      n = n.next; 
/* 589*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 593*/    Preconditions.checkArgument((child.parent == null));
/* 594*/    Preconditions.checkArgument((child.next == null));
/* 595*/    child.parent = this;
/* 596*/    child.next = this.first;
/* 597*/    this.first = child;
/* 598*/    if (this.last == null)
/* 599*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 604*/    Preconditions.checkArgument((child.parent == null));
/* 605*/    Preconditions.checkArgument((child.next == null));
/* 606*/    child.parent = this;
/* 607*/    child.next = null;
/* 608*/    if (this.last == null) {
/* 609*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 612*/    this.last.next = child;
/* 613*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 617*/    for (Node child = children; child != null; child = child.next) {
/* 618*/      Preconditions.checkArgument((child.parent == null));
/* 619*/      child.parent = this;
/*   0*/    } 
/* 621*/    Node lastSib = children.getLastSibling();
/* 622*/    lastSib.next = this.first;
/* 623*/    this.first = children;
/* 624*/    if (this.last == null)
/* 625*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 630*/    addChildrenAfter(children, getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 637*/    Preconditions.checkArgument((node != null && node.parent == this), "The existing child node of the parent should not be null.");
/* 639*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 641*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 643*/    if (this.first == node) {
/* 644*/      newChild.parent = this;
/* 645*/      newChild.next = this.first;
/* 646*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 649*/    Node prev = getChildBefore(node);
/* 650*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 657*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 659*/    addChildrenAfter(newChild, node);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenAfter(Node children, Node node) {
/* 666*/    Preconditions.checkArgument((node == null || node.parent == this));
/* 667*/    for (Node child = children; child != null; child = child.next) {
/* 668*/      Preconditions.checkArgument((child.parent == null));
/* 669*/      child.parent = this;
/*   0*/    } 
/* 672*/    Node lastSibling = children.getLastSibling();
/* 673*/    if (node != null) {
/* 674*/      Node oldNext = node.next;
/* 675*/      node.next = children;
/* 676*/      lastSibling.next = oldNext;
/* 677*/      if (node == this.last)
/* 678*/        this.last = lastSibling; 
/*   0*/    } else {
/* 682*/      if (this.first != null) {
/* 683*/        lastSibling.next = this.first;
/*   0*/      } else {
/* 685*/        this.last = lastSibling;
/*   0*/      } 
/* 687*/      this.first = children;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 695*/    Node prev = getChildBefore(child);
/* 696*/    if (prev == null) {
/* 697*/      this.first = this.first.next;
/*   0*/    } else {
/* 699*/      prev.next = child.next;
/*   0*/    } 
/* 701*/    if (child == this.last)
/* 702*/      this.last = prev; 
/* 704*/    child.next = null;
/* 705*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 712*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 714*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 718*/    newChild.copyInformationFrom(child);
/* 720*/    newChild.next = child.next;
/* 721*/    newChild.parent = this;
/* 722*/    if (child == this.first) {
/* 723*/      this.first = newChild;
/*   0*/    } else {
/* 725*/      Node prev = getChildBefore(child);
/* 726*/      prev.next = newChild;
/*   0*/    } 
/* 728*/    if (child == this.last)
/* 729*/      this.last = newChild; 
/* 731*/    child.next = null;
/* 732*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 736*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 739*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 741*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 745*/    newChild.copyInformationFrom(prevChild);
/* 747*/    Node child = prevChild.next;
/* 748*/    newChild.next = child.next;
/* 749*/    newChild.parent = this;
/* 750*/    prevChild.next = newChild;
/* 751*/    if (child == this.last)
/* 752*/      this.last = newChild; 
/* 754*/    child.next = null;
/* 755*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 760*/    PropListItem x = this.propListHead;
/* 761*/    while (x != null && propType != x.getType())
/* 762*/      x = x.getNext(); 
/* 764*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 775*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 777*/    this.propListHead = other.propListHead;
/* 778*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 782*/    PropListItem result = removeProp(this.propListHead, propType);
/* 783*/    if (result != this.propListHead)
/* 784*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 795*/    if (item == null)
/* 796*/      return null; 
/* 797*/    if (item.getType() == propType)
/* 798*/      return item.getNext(); 
/* 800*/    PropListItem result = removeProp(item.getNext(), propType);
/* 801*/    if (result != item.getNext())
/* 802*/      return item.chain(result); 
/* 804*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 810*/    PropListItem item = lookupProperty(propType);
/* 811*/    if (item == null)
/* 812*/      return null; 
/* 814*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 818*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 826*/    PropListItem item = lookupProperty(propType);
/* 827*/    if (item == null)
/* 828*/      return 0; 
/* 830*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 834*/    PropListItem item = lookupProperty(propType);
/* 835*/    if (item == null)
/* 836*/      throw new IllegalStateException("missing prop: " + propType); 
/* 838*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 842*/    removeProp(propType);
/* 843*/    if (value != null)
/* 844*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 849*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 853*/    removeProp(propType);
/* 854*/    if (value != 0)
/* 855*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 860*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 864*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 869*/    int count = 0;
/* 870*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 871*/      count++; 
/* 874*/    int[] keys = new int[count];
/* 875*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 876*/      count--;
/* 877*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 880*/    Arrays.sort(keys);
/* 881*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 886*/    if (getType() == 39)
/* 887*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 890*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double value) throws UnsupportedOperationException {
/* 899*/    if (getType() == 39)
/* 900*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 903*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 909*/    if (getType() == 40)
/* 910*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 913*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String value) throws UnsupportedOperationException {
/* 922*/    if (getType() == 40 || getType() == 38)
/* 923*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 926*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 932*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 939*/    StringBuilder sb = new StringBuilder();
/* 940*/    toString(sb, printSource, printAnnotations, printType);
/* 941*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 949*/    sb.append(Token.name(this.type));
/* 950*/    if (this instanceof StringNode) {
/* 951*/      sb.append(' ');
/* 952*/      sb.append(getString());
/* 953*/    } else if (this.type == 105) {
/* 954*/      sb.append(' ');
/* 958*/      if (this.first == null || this.first.getType() != 38) {
/* 959*/        sb.append("<invalid>");
/*   0*/      } else {
/* 961*/        sb.append(this.first.getString());
/*   0*/      } 
/* 963*/    } else if (this.type == 39) {
/* 964*/      sb.append(' ');
/* 965*/      sb.append(getDouble());
/*   0*/    } 
/* 967*/    if (printSource) {
/* 968*/      int lineno = getLineno();
/* 969*/      if (lineno != -1) {
/* 970*/        sb.append(' ');
/* 971*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 975*/    if (printAnnotations) {
/* 976*/      int[] keys = getSortedPropTypes();
/* 977*/      for (int i = 0; i < keys.length; i++) {
/* 978*/        int type = keys[i];
/* 979*/        PropListItem x = lookupProperty(type);
/* 980*/        sb.append(" [");
/* 981*/        sb.append(propToString(type));
/* 982*/        sb.append(": ");
/* 984*/        switch (type) {
/*   0*/        
/*   0*/        } 
/* 986*/        String value = x.toString();
/* 989*/        sb.append(value);
/* 990*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/* 994*/    if (printType && 
/* 995*/      this.jsType != null) {
/* 996*/      String jsTypeString = this.jsType.toString();
/* 997*/      if (jsTypeString != null) {
/* 998*/        sb.append(" : ");
/* 999*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1007*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1012*/      StringBuilder s = new StringBuilder();
/*1013*/      appendStringTree(s);
/*1014*/      return s.toString();
/*1015*/    } catch (IOException e) {
/*1016*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1021*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1026*/    for (int i = 0; i != level; i++)
/*1027*/      sb.append("    "); 
/*1029*/    sb.append(n.toString());
/*1030*/    sb.append('\n');
/*1031*/    Node cursor = n.getFirstChild();
/*1032*/    for (; cursor != null; 
/*1033*/      cursor = cursor.getNext())
/*1034*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1091*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1096*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1100*/    StaticSourceFile file = getStaticSourceFile();
/*1101*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1106*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1113*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1120*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1124*/    StaticSourceFile file = getStaticSourceFile();
/*1125*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1129*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1133*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1137*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1141*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1145*/    StaticSourceFile file = getStaticSourceFile();
/*1146*/    if (file == null)
/*1147*/      return -1; 
/*1149*/    int lineno = getLineno();
/*1150*/    if (lineno == -1)
/*1151*/      return -1; 
/*1153*/    return file.getLineOffset(lineno) + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1157*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1161*/    int charno = getCharno();
/*1162*/    if (charno == -1)
/*1163*/      charno = 0; 
/*1165*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1169*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1173*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1177*/    this.sourcePosition = sourcePosition;
/*1179*/    Node child = getFirstChild();
/*1180*/    for (; child != null; child = child.getNext())
/*1181*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1192*/    if (lineno < 0 || charno < 0)
/*1193*/      return -1; 
/*1194*/    if ((charno & 0xFFFFF000) != 0)
/*1195*/      return lineno << 12 | 0xFFF; 
/*1197*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1206*/    if (lineCharNo == -1)
/*1207*/      return -1; 
/*1209*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1218*/    if (lineCharNo == -1)
/*1219*/      return -1; 
/*1221*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1238*/    if (this.first == null)
/*1239*/      return Collections.emptySet(); 
/*1241*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1255*/    return new SiblingNodeIterable(this);
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
/*1268*/      this.start = start;
/*1269*/      this.current = start;
/*1270*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1275*/      if (!this.used) {
/*1276*/        this.used = true;
/*1277*/        return this;
/*   0*/      } 
/*1285*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1291*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1296*/      if (this.current == null)
/*1297*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1300*/        return this.current;
/*   0*/      } finally {
/*1302*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1308*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1316*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1320*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1329*/    Preconditions.checkArgument((level >= 0));
/*1330*/    Node node = this;
/*1331*/    while (node != null && level-- > 0)
/*1332*/      node = node.getParent(); 
/*1334*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1341*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1354*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1359*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1362*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1367*/            if (!hasNext())
/*1368*/              throw new NoSuchElementException(); 
/*1370*/            Node n = Node.AncestorIterable.this.cur;
/*1371*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1372*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1377*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1390*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1400*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1404*/    int c = 0;
/*1405*/    for (Node n = this.first; n != null; n = n.next)
/*1406*/      c++; 
/*1408*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1413*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1414*/      if (child == n)
/*1415*/        return true; 
/*   0*/    } 
/*1418*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1426*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1427*/    if (diff != null)
/*1428*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1434*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1443*/    if (!isEquivalentTo(node2, false, false, false))
/*1444*/      return new NodeMismatch(this, node2); 
/*1447*/    NodeMismatch res = null;
/*1449*/    Node n = this.first, n2 = node2.first;
/*1450*/    for (; res == null && n != null; 
/*1451*/      n = n.next, n2 = n2.next) {
/*1452*/      if (node2 == null)
/*1453*/        throw new IllegalStateException(); 
/*1455*/      res = n.checkTreeEqualsImpl(n2);
/*1456*/      if (res != null)
/*1457*/        return res; 
/*   0*/    } 
/*1460*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1470*/    if (!isEquivalentTo(node2, true, false, false))
/*1471*/      return new NodeMismatch(this, node2); 
/*1474*/    NodeMismatch res = null;
/*1476*/    Node n = this.first, n2 = node2.first;
/*1477*/    for (; res == null && n != null; 
/*1478*/      n = n.next, n2 = n2.next) {
/*1479*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1480*/      if (res != null)
/*1481*/        return res; 
/*   0*/    } 
/*1484*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1489*/    return isEquivalentTo(node, false, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToShallow(Node node) {
/*1494*/    return isEquivalentTo(node, false, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1502*/    return isEquivalentTo(node, true, true, false);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recur, boolean shallow) {
/*1514*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getClass() != node.getClass())
/*1517*/      return false; 
/*1520*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1521*/      return false; 
/*1524*/    if (this.type == 102 || this.type == 103) {
/*1525*/      int post1 = getIntProp(32);
/*1526*/      int post2 = node.getIntProp(32);
/*1527*/      if (post1 != post2)
/*1528*/        return false; 
/*1530*/    } else if (this.type == 40 || this.type == 154) {
/*1531*/      if (this.type == 154) {
/*1532*/        int quoted1 = getIntProp(36);
/*1533*/        int quoted2 = node.getIntProp(36);
/*1534*/        if (quoted1 != quoted2)
/*1535*/          return false; 
/*   0*/      } 
/*1539*/      int slashV1 = getIntProp(54);
/*1540*/      int slashV2 = node.getIntProp(54);
/*1541*/      if (slashV1 != slashV2)
/*1542*/        return false; 
/*1544*/    } else if (getLength() == 37 && 
/*1545*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1546*/      return false;
/*   0*/    } 
/*1550*/    if (recur) {
/*1552*/      Node n = this.first, n2 = node.first;
/*1553*/      for (; n != null; 
/*1554*/        n = n.next, n2 = n2.next) {
/*1555*/        if (!n.isEquivalentTo(n2, compareJsType, (!shallow || !n.isFunction()), shallow))
/*1557*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1562*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1574*/    if (this.type == 38) {
/*1575*/      String name = getString();
/*1576*/      return name.isEmpty() ? null : name;
/*   0*/    } 
/*1577*/    if (this.type == 33) {
/*1578*/      String left = getFirstChild().getQualifiedName();
/*1579*/      if (left == null)
/*1580*/        return null; 
/*1582*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1583*/    if (this.type == 42)
/*1584*/      return "this"; 
/*1586*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1595*/    switch (getType()) {
/*   0*/      case 38:
/*1597*/        return !getString().isEmpty();
/*   0*/      case 42:
/*1599*/        return true;
/*   0*/      case 33:
/*1601*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1603*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1613*/    switch (getType()) {
/*   0*/      case 38:
/*1615*/        return !getString().isEmpty();
/*   0*/      case 33:
/*1617*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1619*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1631*/    Preconditions.checkState((this.parent != null));
/*1632*/    this.parent.removeChild(this);
/*1633*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1643*/    Node child = this.first;
/*1644*/    if (child != null)
/*1645*/      removeChild(child); 
/*1647*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1654*/    Node children = this.first;
/*1655*/    for (Node child = this.first; child != null; child = child.getNext())
/*1656*/      child.parent = null; 
/*1658*/    this.first = null;
/*1659*/    this.last = null;
/*1660*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1668*/    for (Node child = this.first; child != null; ) {
/*1669*/      Node nextChild = child.getNext();
/*1670*/      child.parent = null;
/*1671*/      child.next = null;
/*1672*/      child = nextChild;
/*   0*/    } 
/*1674*/    this.first = null;
/*1675*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1679*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1681*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1684*/    Node child = prev.next;
/*1685*/    prev.next = child.next;
/*1686*/    if (child == this.last)
/*1687*/      this.last = prev; 
/*1689*/    child.next = null;
/*1690*/    child.parent = null;
/*1691*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1700*/      result = (Node)clone();
/*1703*/      result.next = null;
/*1704*/      result.first = null;
/*1705*/      result.last = null;
/*1706*/      result.parent = null;
/*1707*/    } catch (CloneNotSupportedException e) {
/*1708*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1710*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1717*/    Node result = cloneNode();
/*1718*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1719*/      Node n2clone = n2.cloneTree();
/*1720*/      n2clone.parent = result;
/*1721*/      if (result.last != null)
/*1722*/        result.last.next = n2clone; 
/*1724*/      if (result.first == null)
/*1725*/        result.first = n2clone; 
/*1727*/      result.last = n2clone;
/*   0*/    } 
/*1729*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1740*/    if (getProp(40) == null)
/*1741*/      putProp(40, other.getProp(40)); 
/*1744*/    if (getProp(51) == null) {
/*1745*/      putProp(51, other.getProp(51));
/*1746*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1749*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1759*/    copyInformationFrom(other);
/*1760*/    Node child = getFirstChild();
/*1761*/    for (; child != null; child = child.getNext())
/*1762*/      child.copyInformationFromForTree(other); 
/*1765*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*1773*/    putProp(40, other.getProp(40));
/*1774*/    putProp(51, other.getProp(51));
/*1775*/    this.sourcePosition = other.sourcePosition;
/*1776*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcref(Node other) {
/*1780*/    return useSourceInfoFrom(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*1788*/    useSourceInfoFrom(other);
/*1789*/    Node child = getFirstChild();
/*1790*/    for (; child != null; child = child.getNext())
/*1791*/      child.useSourceInfoFromForTree(other); 
/*1794*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcrefTree(Node other) {
/*1798*/    return useSourceInfoFromForTree(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*1806*/    if (getProp(40) == null)
/*1807*/      putProp(40, other.getProp(40)); 
/*1810*/    if (getProp(51) == null) {
/*1811*/      putProp(51, other.getProp(51));
/*1812*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1815*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*1823*/    useSourceInfoIfMissingFrom(other);
/*1824*/    Node child = getFirstChild();
/*1825*/    for (; child != null; child = child.getNext())
/*1826*/      child.useSourceInfoIfMissingFromForTree(other); 
/*1829*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1836*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1840*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1844*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1857*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1858*/      if (jsDocInfo == null)
/*1861*/        jsDocInfo = new JSDocInfo(false); 
/*1863*/      String license = jsDocInfo.getLicense();
/*1864*/      if (license == null)
/*1865*/        license = ""; 
/*1867*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1868*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1878*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public Node setJSDocInfo(JSDocInfo info) {
/*1885*/    putProp(29, info);
/*1886*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void setChangeTime(int time) {
/*1891*/    putIntProp(56, time);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChangeTime() {
/*1896*/    return getIntProp(56);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1905*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1914*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*1923*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*1932*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*1940*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*1948*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*1955*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*1963*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*1971*/    if (getJSDocInfo() == null)
/*1972*/      setJSDocInfo(new JSDocInfo(false)); 
/*1974*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*1982*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*1990*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2023*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2028*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2032*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2039*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2047*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2053*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2057*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2062*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2067*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2071*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2079*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2084*/      removeFlag(1);
/*2085*/      removeFlag(4);
/*2086*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2090*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2094*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2098*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2102*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2106*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2114*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2126*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2135*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2142*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2149*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2156*/    throw new IllegalStateException("not a StringNode");
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2164*/      this.nodeA = nodeA;
/*2165*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2170*/      if (object instanceof NodeMismatch) {
/*2171*/        NodeMismatch that = (NodeMismatch)object;
/*2172*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2174*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2179*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAdd() {
/*2187*/    return (getType() == 21);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAnd() {
/*2191*/    return (getType() == 101);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayLit() {
/*2195*/    return (getType() == 63);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssign() {
/*2199*/    return (getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssignAdd() {
/*2203*/    return (getType() == 93);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*2207*/    return (getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBreak() {
/*2211*/    return (getType() == 116);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCall() {
/*2215*/    return (getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCase() {
/*2219*/    return (getType() == 111);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCast() {
/*2223*/    return (getType() == 155);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCatch() {
/*2227*/    return (getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isComma() {
/*2231*/    return (getType() == 85);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContinue() {
/*2235*/    return (getType() == 117);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDebugger() {
/*2239*/    return (getType() == 152);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDec() {
/*2243*/    return (getType() == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDefaultCase() {
/*2247*/    return (getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDelProp() {
/*2251*/    return (getType() == 31);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDo() {
/*2255*/    return (getType() == 114);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*2259*/    return (getType() == 124);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExprResult() {
/*2263*/    return (getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFalse() {
/*2267*/    return (getType() == 43);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFor() {
/*2271*/    return (getType() == 115);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFunction() {
/*2275*/    return (getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetterDef() {
/*2279*/    return (getType() == 147);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetElem() {
/*2283*/    return (getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetProp() {
/*2287*/    return (getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isHook() {
/*2291*/    return (getType() == 98);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIf() {
/*2295*/    return (getType() == 108);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIn() {
/*2299*/    return (getType() == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInc() {
/*2303*/    return (getType() == 102);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInstanceOf() {
/*2307*/    return (getType() == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabel() {
/*2311*/    return (getType() == 126);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabelName() {
/*2315*/    return (getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isName() {
/*2319*/    return (getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNE() {
/*2323*/    return (getType() == 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNew() {
/*2327*/    return (getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNot() {
/*2331*/    return (getType() == 26);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*2335*/    return (getType() == 41);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNumber() {
/*2339*/    return (getType() == 39);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isObjectLit() {
/*2343*/    return (getType() == 64);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOr() {
/*2347*/    return (getType() == 100);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isParamList() {
/*2351*/    return (getType() == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRegExp() {
/*2355*/    return (getType() == 47);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isReturn() {
/*2359*/    return (getType() == 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isScript() {
/*2363*/    return (getType() == 132);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSetterDef() {
/*2367*/    return (getType() == 148);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isString() {
/*2371*/    return (getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isStringKey() {
/*2375*/    return (getType() == 154);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSwitch() {
/*2379*/    return (getType() == 110);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThis() {
/*2383*/    return (getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrow() {
/*2387*/    return (getType() == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrue() {
/*2391*/    return (getType() == 44);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTry() {
/*2395*/    return (getType() == 77);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeOf() {
/*2399*/    return (getType() == 32);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVar() {
/*2403*/    return (getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVoid() {
/*2407*/    return (getType() == 122);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWhile() {
/*2411*/    return (getType() == 113);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWith() {
/*2415*/    return (getType() == 119);
/*   0*/  }
/*   0*/}
