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
/*   0*/  public static final int SOURCENAME_PROP = 16;
/*   0*/  
/*   0*/  public static final int JSDOC_INFO_PROP = 29;
/*   0*/  
/*   0*/  public static final int VAR_ARGS_NAME = 30;
/*   0*/  
/*   0*/  public static final int INCRDECR_PROP = 32;
/*   0*/  
/*   0*/  public static final int PARENTHESIZED_PROP = 35;
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
/*   0*/  public static final int BRACELESS_TYPE = 41;
/*   0*/  
/*   0*/  public static final int SIDE_EFFECT_FLAGS = 42;
/*   0*/  
/*   0*/  public static final int IS_CONSTANT_NAME = 43;
/*   0*/  
/*   0*/  public static final int IS_OPTIONAL_PARAM = 44;
/*   0*/  
/*   0*/  public static final int IS_VAR_ARGS_PARAM = 45;
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
/* 119*/    switch (propType) {
/*   0*/      case 41:
/* 120*/        return "braceless_type";
/*   0*/      case 30:
/* 121*/        return "var_args_name";
/*   0*/      case 16:
/* 122*/        return "sourcename";
/*   0*/      case 29:
/* 124*/        return "jsdoc_info";
/*   0*/      case 32:
/* 126*/        return "incrdecr";
/*   0*/      case 35:
/* 127*/        return "parenthesized";
/*   0*/      case 36:
/* 128*/        return "quoted";
/*   0*/      case 37:
/* 129*/        return "opt_arg";
/*   0*/      case 38:
/* 131*/        return "synthetic";
/*   0*/      case 39:
/* 132*/        return "empty_block";
/*   0*/      case 40:
/* 133*/        return "originalname";
/*   0*/      case 42:
/* 134*/        return "side_effect_flags";
/*   0*/      case 43:
/* 136*/        return "is_constant_name";
/*   0*/      case 44:
/* 137*/        return "is_optional_param";
/*   0*/      case 45:
/* 138*/        return "is_var_args_param";
/*   0*/      case 46:
/* 139*/        return "is_namespace";
/*   0*/      case 47:
/* 140*/        return "is_dispatcher";
/*   0*/      case 48:
/* 141*/        return "directives";
/*   0*/      case 49:
/* 142*/        return "direct_eval";
/*   0*/      case 50:
/* 143*/        return "free_call";
/*   0*/      case 51:
/* 144*/        return "source_file";
/*   0*/      case 53:
/* 145*/        return "input_id";
/*   0*/      case 52:
/* 146*/        return "length";
/*   0*/      case 54:
/* 147*/        return "slash_v";
/*   0*/      case 55:
/* 148*/        return "inferred";
/*   0*/    } 
/* 150*/    throw new IllegalStateException("unexpect prop id " + propType);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 159*/      super(39);
/* 160*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 164*/      super(39, lineno, charno);
/* 165*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 170*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 175*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 180*/      boolean equivalent = super.isEquivalentTo(node, compareJsType, recurse);
/* 181*/      if (equivalent) {
/* 182*/        double thisValue = getDouble();
/* 183*/        double thatValue = ((NumberNode)node).getDouble();
/* 184*/        if (thisValue == thatValue)
/* 186*/          return (thisValue != 0.0D || 1.0D / thisValue == 1.0D / thatValue); 
/*   0*/      } 
/* 189*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 200*/      super(type);
/* 201*/      if (null == str)
/* 202*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 204*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 208*/      super(type, lineno, charno);
/* 209*/      if (null == str)
/* 210*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 212*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 221*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 230*/      if (null == str)
/* 231*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 233*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 238*/      return (super.isEquivalentTo(node, compareJsType, recurse) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 250*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 258*/      putBooleanProp(36, true);
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
/* 281*/      this.propType = propType;
/* 282*/      this.next = next;
/*   0*/    }
/*   0*/    
/*   0*/    public int getType() {
/* 287*/      return this.propType;
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem getNext() {
/* 292*/      return this.next;
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
/* 307*/      super(propType, next);
/* 308*/      this.objectValue = objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 313*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 318*/      return this.objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 323*/      return (this.objectValue == null) ? "null" : this.objectValue.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 328*/      return new ObjectPropListItem(getType(), this.objectValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class IntPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    IntPropListItem(int propType, int intValue, Node.PropListItem next) {
/* 339*/      super(propType, next);
/* 340*/      this.intValue = intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 345*/      return this.intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 350*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 355*/      return String.valueOf(this.intValue);
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 360*/      return new IntPropListItem(getType(), this.intValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 365*/    this.type = nodeType;
/* 366*/    this.parent = null;
/* 367*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 371*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 373*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 376*/    this.type = nodeType;
/* 377*/    this.parent = null;
/* 378*/    this.first = this.last = child;
/* 379*/    child.next = null;
/* 380*/    child.parent = this;
/* 381*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 385*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 387*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 389*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 391*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 393*/    this.type = nodeType;
/* 394*/    this.parent = null;
/* 395*/    this.first = left;
/* 396*/    this.last = right;
/* 397*/    left.next = right;
/* 398*/    left.parent = this;
/* 399*/    right.next = null;
/* 400*/    right.parent = this;
/* 401*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 405*/    Preconditions.checkArgument((left.parent == null));
/* 406*/    Preconditions.checkArgument((left.next == null));
/* 407*/    Preconditions.checkArgument((mid.parent == null));
/* 408*/    Preconditions.checkArgument((mid.next == null));
/* 409*/    Preconditions.checkArgument((right.parent == null));
/* 410*/    Preconditions.checkArgument((right.next == null));
/* 411*/    this.type = nodeType;
/* 412*/    this.parent = null;
/* 413*/    this.first = left;
/* 414*/    this.last = right;
/* 415*/    left.next = mid;
/* 416*/    left.parent = this;
/* 417*/    mid.next = right;
/* 418*/    mid.parent = this;
/* 419*/    right.next = null;
/* 420*/    right.parent = this;
/* 421*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 425*/    Preconditions.checkArgument((left.parent == null));
/* 426*/    Preconditions.checkArgument((left.next == null));
/* 427*/    Preconditions.checkArgument((mid.parent == null));
/* 428*/    Preconditions.checkArgument((mid.next == null));
/* 429*/    Preconditions.checkArgument((mid2.parent == null));
/* 430*/    Preconditions.checkArgument((mid2.next == null));
/* 431*/    Preconditions.checkArgument((right.parent == null));
/* 432*/    Preconditions.checkArgument((right.next == null));
/* 433*/    this.type = nodeType;
/* 434*/    this.parent = null;
/* 435*/    this.first = left;
/* 436*/    this.last = right;
/* 437*/    left.next = mid;
/* 438*/    left.parent = this;
/* 439*/    mid.next = mid2;
/* 440*/    mid.parent = this;
/* 441*/    mid2.next = right;
/* 442*/    mid2.parent = this;
/* 443*/    right.next = null;
/* 444*/    right.parent = this;
/* 445*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 449*/    this.type = nodeType;
/* 450*/    this.parent = null;
/* 451*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 455*/    this(nodeType, child);
/* 456*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 460*/    this(nodeType, left, right);
/* 461*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 466*/    this(nodeType, left, mid, right);
/* 467*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 472*/    this(nodeType, left, mid, mid2, right);
/* 473*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 477*/    this(nodeType, children);
/* 478*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 482*/    this.type = nodeType;
/* 483*/    this.parent = null;
/* 484*/    if (children.length != 0) {
/* 485*/      this.first = children[0];
/* 486*/      this.last = children[children.length - 1];
/* 488*/      for (int i = 1; i < children.length; i++) {
/* 489*/        if (null != (children[i - 1]).next)
/* 491*/          throw new IllegalArgumentException("duplicate child"); 
/* 493*/        (children[i - 1]).next = children[i];
/* 494*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 495*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 497*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 498*/      (children[children.length - 1]).parent = this;
/* 500*/      if (null != this.last.next)
/* 502*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 508*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 512*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 516*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 520*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 524*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 528*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 532*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 536*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 540*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 544*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 548*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 552*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 556*/    if (child == this.first)
/* 557*/      return null; 
/* 559*/    Node n = this.first;
/* 560*/    while (n.next != child) {
/* 561*/      n = n.next;
/* 562*/      if (n == null)
/* 563*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 566*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 570*/    Node n = this.first;
/* 571*/    while (i > 0) {
/* 572*/      n = n.next;
/* 573*/      i--;
/*   0*/    } 
/* 575*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOfChild(Node child) {
/* 579*/    Node n = this.first;
/* 580*/    int i = 0;
/* 581*/    while (n != null) {
/* 582*/      if (child == n)
/* 583*/        return i; 
/* 586*/      n = n.next;
/* 587*/      i++;
/*   0*/    } 
/* 589*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 593*/    Node n = this;
/* 594*/    while (n.next != null)
/* 595*/      n = n.next; 
/* 597*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 601*/    Preconditions.checkArgument((child.parent == null));
/* 602*/    Preconditions.checkArgument((child.next == null));
/* 603*/    child.parent = this;
/* 604*/    child.next = this.first;
/* 605*/    this.first = child;
/* 606*/    if (this.last == null)
/* 607*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 612*/    Preconditions.checkArgument((child.parent == null));
/* 613*/    Preconditions.checkArgument((child.next == null));
/* 614*/    child.parent = this;
/* 615*/    child.next = null;
/* 616*/    if (this.last == null) {
/* 617*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 620*/    this.last.next = child;
/* 621*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 625*/    for (Node child = children; child != null; child = child.next) {
/* 626*/      Preconditions.checkArgument((child.parent == null));
/* 627*/      child.parent = this;
/*   0*/    } 
/* 629*/    Node lastSib = children.getLastSibling();
/* 630*/    lastSib.next = this.first;
/* 631*/    this.first = children;
/* 632*/    if (this.last == null)
/* 633*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 638*/    addChildrenAfter(children, getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 645*/    Preconditions.checkArgument((node != null && node.parent == this), "The existing child node of the parent should not be null.");
/* 647*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 649*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 651*/    if (this.first == node) {
/* 652*/      newChild.parent = this;
/* 653*/      newChild.next = this.first;
/* 654*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 657*/    Node prev = getChildBefore(node);
/* 658*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 665*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 667*/    addChildrenAfter(newChild, node);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenAfter(Node children, Node node) {
/* 674*/    Preconditions.checkArgument((node == null || node.parent == this));
/* 675*/    for (Node child = children; child != null; child = child.next) {
/* 676*/      Preconditions.checkArgument((child.parent == null));
/* 677*/      child.parent = this;
/*   0*/    } 
/* 680*/    Node lastSibling = children.getLastSibling();
/* 681*/    if (node != null) {
/* 682*/      Node oldNext = node.next;
/* 683*/      node.next = children;
/* 684*/      lastSibling.next = oldNext;
/* 685*/      if (node == this.last)
/* 686*/        this.last = lastSibling; 
/*   0*/    } else {
/* 690*/      if (this.first != null) {
/* 691*/        lastSibling.next = this.first;
/*   0*/      } else {
/* 693*/        this.last = lastSibling;
/*   0*/      } 
/* 695*/      this.first = children;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 703*/    Node prev = getChildBefore(child);
/* 704*/    if (prev == null) {
/* 705*/      this.first = this.first.next;
/*   0*/    } else {
/* 707*/      prev.next = child.next;
/*   0*/    } 
/* 708*/    if (child == this.last)
/* 708*/      this.last = prev; 
/* 709*/    child.next = null;
/* 710*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 717*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 719*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 723*/    newChild.copyInformationFrom(child);
/* 725*/    newChild.next = child.next;
/* 726*/    newChild.parent = this;
/* 727*/    if (child == this.first) {
/* 728*/      this.first = newChild;
/*   0*/    } else {
/* 730*/      Node prev = getChildBefore(child);
/* 731*/      prev.next = newChild;
/*   0*/    } 
/* 733*/    if (child == this.last)
/* 734*/      this.last = newChild; 
/* 735*/    child.next = null;
/* 736*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 740*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 743*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 745*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 749*/    newChild.copyInformationFrom(prevChild);
/* 751*/    Node child = prevChild.next;
/* 752*/    newChild.next = child.next;
/* 753*/    newChild.parent = this;
/* 754*/    prevChild.next = newChild;
/* 755*/    if (child == this.last)
/* 756*/      this.last = newChild; 
/* 757*/    child.next = null;
/* 758*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 763*/    PropListItem x = this.propListHead;
/* 764*/    while (x != null && propType != x.getType())
/* 765*/      x = x.getNext(); 
/* 767*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 778*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 780*/    this.propListHead = other.propListHead;
/* 781*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 785*/    PropListItem result = removeProp(this.propListHead, propType);
/* 786*/    if (result != this.propListHead)
/* 787*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 798*/    if (item == null)
/* 799*/      return null; 
/* 800*/    if (item.getType() == propType)
/* 801*/      return item.getNext(); 
/* 803*/    PropListItem result = removeProp(item.getNext(), propType);
/* 804*/    if (result != item.getNext())
/* 805*/      return item.chain(result); 
/* 807*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 813*/    if (propType == 16)
/* 814*/      return getSourceFileName(); 
/* 817*/    PropListItem item = lookupProperty(propType);
/* 818*/    if (item == null)
/* 819*/      return null; 
/* 821*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 825*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 833*/    PropListItem item = lookupProperty(propType);
/* 834*/    if (item == null)
/* 835*/      return 0; 
/* 837*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 841*/    PropListItem item = lookupProperty(propType);
/* 842*/    if (item == null)
/* 843*/      throw new IllegalStateException("missing prop: " + propType); 
/* 845*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 849*/    if (propType == 16) {
/* 850*/      putProp(51, new SimpleSourceFile((String)value, false));
/*   0*/      return;
/*   0*/    } 
/* 855*/    removeProp(propType);
/* 856*/    if (value != null)
/* 857*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 862*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 866*/    removeProp(propType);
/* 867*/    if (value != 0)
/* 868*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 873*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 877*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 882*/    int count = 0;
/* 883*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 884*/      count++; 
/* 887*/    int[] keys = new int[count];
/* 888*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 889*/      count--;
/* 890*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 893*/    Arrays.sort(keys);
/* 894*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 899*/    if (getType() == 39)
/* 900*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 903*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 909*/    if (getType() == 39)
/* 910*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 913*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 919*/    if (getType() == 40)
/* 920*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 923*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/* 929*/    if (getType() == 40)
/* 930*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 933*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 939*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 946*/    StringBuilder sb = new StringBuilder();
/* 947*/    toString(sb, printSource, printAnnotations, printType);
/* 948*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 956*/    sb.append(Token.name(this.type));
/* 957*/    if (this instanceof StringNode) {
/* 958*/      sb.append(' ');
/* 959*/      sb.append(getString());
/* 960*/    } else if (this.type == 105) {
/* 961*/      sb.append(' ');
/* 965*/      if (this.first == null || this.first.getType() != 38) {
/* 966*/        sb.append("<invalid>");
/*   0*/      } else {
/* 968*/        sb.append(this.first.getString());
/*   0*/      } 
/* 970*/    } else if (this.type == 39) {
/* 971*/      sb.append(' ');
/* 972*/      sb.append(getDouble());
/*   0*/    } 
/* 974*/    if (printSource) {
/* 975*/      int lineno = getLineno();
/* 976*/      if (lineno != -1) {
/* 977*/        sb.append(' ');
/* 978*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 982*/    if (printAnnotations) {
/* 983*/      int[] keys = getSortedPropTypes();
/* 984*/      for (int i = 0; i < keys.length; i++) {
/* 985*/        int type = keys[i];
/* 986*/        PropListItem x = lookupProperty(type);
/* 987*/        sb.append(" [");
/* 988*/        sb.append(propToString(type));
/* 989*/        sb.append(": ");
/* 991*/        switch (type) {
/*   0*/        
/*   0*/        } 
/* 993*/        String value = x.toString();
/* 996*/        sb.append(value);
/* 997*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/*1001*/    if (printType && 
/*1002*/      this.jsType != null) {
/*1003*/      String jsTypeString = this.jsType.toString();
/*1004*/      if (jsTypeString != null) {
/*1005*/        sb.append(" : ");
/*1006*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1014*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1019*/      StringBuilder s = new StringBuilder();
/*1020*/      appendStringTree(s);
/*1021*/      return s.toString();
/*1022*/    } catch (IOException e) {
/*1023*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1028*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1033*/    for (int i = 0; i != level; i++)
/*1034*/      sb.append("    "); 
/*1036*/    sb.append(n.toString());
/*1037*/    sb.append('\n');
/*1038*/    Node cursor = n.getFirstChild();
/*1039*/    for (; cursor != null; 
/*1040*/      cursor = cursor.getNext())
/*1041*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1098*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1103*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1107*/    StaticSourceFile file = getStaticSourceFile();
/*1108*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1113*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1120*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1127*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1131*/    StaticSourceFile file = getStaticSourceFile();
/*1132*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1136*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1140*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1144*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1148*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1152*/    StaticSourceFile file = getStaticSourceFile();
/*1153*/    if (file == null)
/*1154*/      return -1; 
/*1156*/    int lineno = getLineno();
/*1157*/    if (lineno == -1)
/*1158*/      return -1; 
/*1160*/    return file.getLineOffset(lineno) + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1164*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1168*/    int charno = getCharno();
/*1169*/    if (charno == -1)
/*1170*/      charno = 0; 
/*1172*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1176*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1180*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1184*/    this.sourcePosition = sourcePosition;
/*1186*/    Node child = getFirstChild();
/*1187*/    for (; child != null; child = child.getNext())
/*1188*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1199*/    if (lineno < 0 || charno < 0)
/*1200*/      return -1; 
/*1201*/    if ((charno & 0xFFFFF000) != 0)
/*1202*/      return lineno << 12 | 0xFFF; 
/*1204*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1213*/    if (lineCharNo == -1)
/*1214*/      return -1; 
/*1216*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1225*/    if (lineCharNo == -1)
/*1226*/      return -1; 
/*1228*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1245*/    if (this.first == null)
/*1246*/      return Collections.emptySet(); 
/*1248*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1262*/    return new SiblingNodeIterable(this);
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
/*1275*/      this.start = start;
/*1276*/      this.current = start;
/*1277*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1282*/      if (!this.used) {
/*1283*/        this.used = true;
/*1284*/        return this;
/*   0*/      } 
/*1292*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1298*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1303*/      if (this.current == null)
/*1304*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1307*/        return this.current;
/*   0*/      } finally {
/*1309*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1315*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1323*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1327*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1336*/    Preconditions.checkArgument((level >= 0));
/*1337*/    Node node = this;
/*1338*/    while (node != null && level-- > 0)
/*1339*/      node = node.getParent(); 
/*1341*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1348*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1361*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1366*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1369*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1374*/            if (!hasNext())
/*1374*/              throw new NoSuchElementException(); 
/*1375*/            Node n = Node.AncestorIterable.this.cur;
/*1376*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1377*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1382*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1395*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1405*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1409*/    int c = 0;
/*1410*/    for (Node n = this.first; n != null; n = n.next)
/*1411*/      c++; 
/*1413*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1418*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1419*/      if (child == n)
/*1420*/        return true; 
/*   0*/    } 
/*1423*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1431*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1432*/    if (diff != null)
/*1433*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1439*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1448*/    if (!isEquivalentTo(node2, false, false))
/*1449*/      return new NodeMismatch(this, node2); 
/*1452*/    NodeMismatch res = null;
/*1454*/    Node n = this.first, n2 = node2.first;
/*1455*/    for (; res == null && n != null; 
/*1456*/      n2 = n = n.next) {
/*1457*/      if (node2 == null)
/*1458*/        throw new IllegalStateException(); 
/*1460*/      res = n.checkTreeEqualsImpl(n2);
/*1461*/      if (res != null)
/*1462*/        return res; 
/*   0*/    } 
/*1465*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1475*/    if (!isEquivalentTo(node2, true, false))
/*1476*/      return new NodeMismatch(this, node2); 
/*1479*/    NodeMismatch res = null;
/*1481*/    Node n = this.first, n2 = node2.first;
/*1482*/    for (; res == null && n != null; 
/*1483*/      n = n.next, n2 = n2.next) {
/*1484*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1485*/      if (res != null)
/*1486*/        return res; 
/*   0*/    } 
/*1489*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1494*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1502*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1512*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getClass() != node.getClass())
/*1515*/      return false; 
/*1518*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1519*/      return false; 
/*1522*/    if (this.type == 102 || this.type == 103) {
/*1523*/      int post1 = getIntProp(32);
/*1524*/      int post2 = node.getIntProp(32);
/*1525*/      if (post1 != post2)
/*1526*/        return false; 
/*1528*/    } else if (this.type == 40 || this.type == 154) {
/*1529*/      if (this.type == 154) {
/*1530*/        int quoted1 = getIntProp(36);
/*1531*/        int quoted2 = node.getIntProp(36);
/*1532*/        if (quoted1 != quoted2)
/*1533*/          return false; 
/*   0*/      } 
/*1537*/      int slashV1 = getIntProp(54);
/*1538*/      int slashV2 = node.getIntProp(54);
/*1539*/      if (slashV1 != slashV2)
/*1540*/        return false; 
/*1542*/    } else if (this.type == 37 && 
/*1543*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1544*/      return false;
/*   0*/    } 
/*1548*/    if (recurse) {
/*1550*/      Node n = this.first, n2 = node.first;
/*1551*/      for (; n != null; 
/*1552*/        n = n.next, n2 = n2.next) {
/*1553*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1554*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1559*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1571*/    if (this.type == 38) {
/*1572*/      String name = getString();
/*1573*/      return name.isEmpty() ? null : name;
/*   0*/    } 
/*1574*/    if (this.type == 33) {
/*1575*/      String left = getFirstChild().getQualifiedName();
/*1576*/      if (left == null)
/*1577*/        return null; 
/*1579*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1580*/    if (this.type == 42)
/*1581*/      return "this"; 
/*1583*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1592*/    switch (getType()) {
/*   0*/      case 38:
/*1594*/        return !getString().isEmpty();
/*   0*/      case 42:
/*1596*/        return true;
/*   0*/      case 33:
/*1598*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1600*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1610*/    switch (getType()) {
/*   0*/      case 38:
/*1612*/        return !getString().isEmpty();
/*   0*/      case 33:
/*1614*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1616*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1628*/    Preconditions.checkState((this.parent != null));
/*1629*/    this.parent.removeChild(this);
/*1630*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1640*/    Node child = this.first;
/*1641*/    if (child != null)
/*1642*/      removeChild(child); 
/*1644*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1651*/    Node children = this.first;
/*1652*/    for (Node child = this.first; child != null; child = child.getNext())
/*1653*/      child.parent = null; 
/*1655*/    this.first = null;
/*1656*/    this.last = null;
/*1657*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1665*/    for (Node child = this.first; child != null; ) {
/*1666*/      Node nextChild = child.getNext();
/*1667*/      child.parent = null;
/*1668*/      child.next = null;
/*1669*/      child = nextChild;
/*   0*/    } 
/*1671*/    this.first = null;
/*1672*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1676*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1678*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1681*/    Node child = prev.next;
/*1682*/    prev.next = child.next;
/*1683*/    if (child == this.last)
/*1683*/      this.last = prev; 
/*1684*/    child.next = null;
/*1685*/    child.parent = null;
/*1686*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1695*/      result = (Node)clone();
/*1698*/      result.next = null;
/*1699*/      result.first = null;
/*1700*/      result.last = null;
/*1701*/      result.parent = null;
/*1702*/    } catch (CloneNotSupportedException e) {
/*1703*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1705*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1712*/    Node result = cloneNode();
/*1713*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1714*/      Node n2clone = n2.cloneTree();
/*1715*/      n2clone.parent = result;
/*1716*/      if (result.last != null)
/*1717*/        result.last.next = n2clone; 
/*1719*/      if (result.first == null)
/*1720*/        result.first = n2clone; 
/*1722*/      result.last = n2clone;
/*   0*/    } 
/*1724*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1735*/    if (getProp(40) == null)
/*1736*/      putProp(40, other.getProp(40)); 
/*1739*/    if (getProp(51) == null) {
/*1740*/      putProp(51, other.getProp(51));
/*1741*/      this.sourcePosition = other.sourcePosition;
/*1742*/    } else if (getProp(16) == null) {
/*1743*/      putProp(16, other.getProp(16));
/*1744*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1747*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1757*/    copyInformationFrom(other);
/*1758*/    Node child = getFirstChild();
/*1759*/    for (; child != null; child = child.getNext())
/*1760*/      child.copyInformationFromForTree(other); 
/*1763*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*1771*/    putProp(40, other.getProp(40));
/*1772*/    putProp(51, other.getProp(51));
/*1773*/    this.sourcePosition = other.sourcePosition;
/*1774*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcref(Node other) {
/*1778*/    return useSourceInfoFrom(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*1786*/    useSourceInfoFrom(other);
/*1787*/    Node child = getFirstChild();
/*1788*/    for (; child != null; child = child.getNext())
/*1789*/      child.useSourceInfoFromForTree(other); 
/*1792*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcrefTree(Node other) {
/*1796*/    return useSourceInfoFromForTree(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*1804*/    if (getProp(40) == null)
/*1805*/      putProp(40, other.getProp(40)); 
/*1808*/    if (getProp(51) == null) {
/*1809*/      putProp(51, other.getProp(51));
/*1810*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1813*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*1821*/    useSourceInfoIfMissingFrom(other);
/*1822*/    Node child = getFirstChild();
/*1823*/    for (; child != null; child = child.getNext())
/*1824*/      child.useSourceInfoIfMissingFromForTree(other); 
/*1827*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1834*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1838*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1842*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1855*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1856*/      if (jsDocInfo == null)
/*1859*/        jsDocInfo = new JSDocInfo(false); 
/*1861*/      String license = jsDocInfo.getLicense();
/*1862*/      if (license == null)
/*1863*/        license = ""; 
/*1865*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1866*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1876*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*1883*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1892*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1901*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*1910*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*1919*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*1927*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*1935*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*1942*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*1950*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*1958*/    if (getJSDocInfo() == null)
/*1959*/      setJSDocInfo(new JSDocInfo(false)); 
/*1961*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*1969*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*1977*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2010*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2015*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2019*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2026*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2034*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2040*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2044*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2049*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2054*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2058*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2066*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2071*/      removeFlag(1);
/*2072*/      removeFlag(4);
/*2073*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2077*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2081*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2085*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2089*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2093*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2101*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2113*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2122*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2129*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2136*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2143*/    throw new IllegalStateException("not a StringNode");
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2151*/      this.nodeA = nodeA;
/*2152*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2157*/      if (object instanceof NodeMismatch) {
/*2158*/        NodeMismatch that = (NodeMismatch)object;
/*2159*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2161*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2166*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAdd() {
/*2174*/    return (getType() == 21);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAnd() {
/*2178*/    return (getType() == 101);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayLit() {
/*2182*/    return (getType() == 63);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssign() {
/*2186*/    return (getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssignAdd() {
/*2190*/    return (getType() == 93);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*2194*/    return (getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBreak() {
/*2198*/    return (getType() == 116);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCall() {
/*2202*/    return (getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCase() {
/*2206*/    return (getType() == 111);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCatch() {
/*2210*/    return (getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isComma() {
/*2214*/    return (getType() == 85);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContinue() {
/*2218*/    return (getType() == 117);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDebugger() {
/*2222*/    return (getType() == 152);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDec() {
/*2226*/    return (getType() == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDefaultCase() {
/*2230*/    return (getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDelProp() {
/*2234*/    return (getType() == 31);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDo() {
/*2238*/    return (getType() == 114);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*2242*/    return (getType() == 124);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExprResult() {
/*2246*/    return (getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFalse() {
/*2250*/    return (getType() == 43);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFor() {
/*2254*/    return (getType() == 115);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFunction() {
/*2258*/    return (getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetterDef() {
/*2262*/    return (getType() == 147);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetElem() {
/*2266*/    return (getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetProp() {
/*2270*/    return (getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isHook() {
/*2274*/    return (getType() == 98);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIf() {
/*2278*/    return (getType() == 108);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIn() {
/*2282*/    return (getType() == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInc() {
/*2286*/    return (getType() == 102);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInstanceOf() {
/*2290*/    return (getType() == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabel() {
/*2294*/    return (getType() == 126);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabelName() {
/*2298*/    return (getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isName() {
/*2302*/    return (getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNE() {
/*2306*/    return (getType() == 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNew() {
/*2310*/    return (getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNot() {
/*2314*/    return (getType() == 26);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*2318*/    return (getType() == 41);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNumber() {
/*2322*/    return (getType() == 39);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isObjectLit() {
/*2326*/    return (getType() == 64);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOr() {
/*2330*/    return (getType() == 100);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isParamList() {
/*2334*/    return (getType() == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRegExp() {
/*2338*/    return (getType() == 47);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isReturn() {
/*2342*/    return (getType() == 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isScript() {
/*2346*/    return (getType() == 132);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSetterDef() {
/*2350*/    return (getType() == 148);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isString() {
/*2354*/    return (getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isStringKey() {
/*2358*/    return (getType() == 154);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSwitch() {
/*2362*/    return (getType() == 110);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThis() {
/*2366*/    return (getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrow() {
/*2370*/    return (getType() == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrue() {
/*2374*/    return (getType() == 44);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTry() {
/*2378*/    return (getType() == 77);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeOf() {
/*2382*/    return (getType() == 32);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVar() {
/*2386*/    return (getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVoid() {
/*2390*/    return (getType() == 122);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWhile() {
/*2394*/    return (getType() == 113);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWith() {
/*2398*/    return (getType() == 119);
/*   0*/  }
/*   0*/}
