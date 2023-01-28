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
/*   0*/  public static final int LAST_PROP = 54;
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
/* 117*/    switch (propType) {
/*   0*/      case 41:
/* 118*/        return "braceless_type";
/*   0*/      case 30:
/* 119*/        return "var_args_name";
/*   0*/      case 16:
/* 120*/        return "sourcename";
/*   0*/      case 29:
/* 122*/        return "jsdoc_info";
/*   0*/      case 32:
/* 124*/        return "incrdecr";
/*   0*/      case 35:
/* 125*/        return "parenthesized";
/*   0*/      case 36:
/* 126*/        return "quoted";
/*   0*/      case 37:
/* 127*/        return "opt_arg";
/*   0*/      case 38:
/* 129*/        return "synthetic";
/*   0*/      case 39:
/* 130*/        return "empty_block";
/*   0*/      case 40:
/* 131*/        return "originalname";
/*   0*/      case 42:
/* 132*/        return "side_effect_flags";
/*   0*/      case 43:
/* 134*/        return "is_constant_name";
/*   0*/      case 44:
/* 135*/        return "is_optional_param";
/*   0*/      case 45:
/* 136*/        return "is_var_args_param";
/*   0*/      case 46:
/* 137*/        return "is_namespace";
/*   0*/      case 47:
/* 138*/        return "is_dispatcher";
/*   0*/      case 48:
/* 139*/        return "directives";
/*   0*/      case 49:
/* 140*/        return "direct_eval";
/*   0*/      case 50:
/* 141*/        return "free_call";
/*   0*/      case 51:
/* 142*/        return "source_file";
/*   0*/      case 53:
/* 143*/        return "input_id";
/*   0*/      case 52:
/* 144*/        return "length";
/*   0*/    } 
/* 146*/    throw new IllegalStateException("unexpect prop id " + propType);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 155*/      super(39);
/* 156*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 160*/      super(39, lineno, charno);
/* 161*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 166*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 171*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 176*/      return (super.isEquivalentTo(node, compareJsType, recurse) && getDouble() == ((NumberNode)node).getDouble());
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
/* 626*/    for (Node child = children; child != null; child = child.next) {
/* 627*/      Preconditions.checkArgument((child.parent == null));
/* 628*/      child.parent = this;
/*   0*/    } 
/* 630*/    if (this.last != null)
/* 631*/      this.last.next = children; 
/* 633*/    this.last = children.getLastSibling();
/* 634*/    if (this.first == null)
/* 635*/      this.first = children; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 643*/    Preconditions.checkArgument((node != null), "The existing child node of the parent should not be null.");
/* 645*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 647*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 649*/    if (this.first == node) {
/* 650*/      newChild.parent = this;
/* 651*/      newChild.next = this.first;
/* 652*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 655*/    Node prev = getChildBefore(node);
/* 656*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 663*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 665*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 667*/    newChild.parent = this;
/* 668*/    newChild.next = node.next;
/* 669*/    node.next = newChild;
/* 670*/    if (this.last == node)
/* 671*/      this.last = newChild; 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 679*/    Node prev = getChildBefore(child);
/* 680*/    if (prev == null) {
/* 681*/      this.first = this.first.next;
/*   0*/    } else {
/* 683*/      prev.next = child.next;
/*   0*/    } 
/* 684*/    if (child == this.last)
/* 684*/      this.last = prev; 
/* 685*/    child.next = null;
/* 686*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 693*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 695*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 699*/    newChild.copyInformationFrom(child);
/* 701*/    newChild.next = child.next;
/* 702*/    newChild.parent = this;
/* 703*/    if (child == this.first) {
/* 704*/      this.first = newChild;
/*   0*/    } else {
/* 706*/      Node prev = getChildBefore(child);
/* 707*/      prev.next = newChild;
/*   0*/    } 
/* 709*/    if (child == this.last)
/* 710*/      this.last = newChild; 
/* 711*/    child.next = null;
/* 712*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 716*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 719*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 721*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 725*/    newChild.copyInformationFrom(prevChild);
/* 727*/    Node child = prevChild.next;
/* 728*/    newChild.next = child.next;
/* 729*/    newChild.parent = this;
/* 730*/    prevChild.next = newChild;
/* 731*/    if (child == this.last)
/* 732*/      this.last = newChild; 
/* 733*/    child.next = null;
/* 734*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 739*/    PropListItem x = this.propListHead;
/* 740*/    while (x != null && propType != x.getType())
/* 741*/      x = x.getNext(); 
/* 743*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 754*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 756*/    this.propListHead = other.propListHead;
/* 757*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 761*/    PropListItem result = removeProp(this.propListHead, propType);
/* 762*/    if (result != this.propListHead)
/* 763*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 774*/    if (item == null)
/* 775*/      return null; 
/* 776*/    if (item.getType() == propType)
/* 777*/      return item.getNext(); 
/* 779*/    PropListItem result = removeProp(item.getNext(), propType);
/* 780*/    if (result != item.getNext())
/* 781*/      return item.chain(result); 
/* 783*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 789*/    if (propType == 16)
/* 790*/      return getSourceFileName(); 
/* 793*/    PropListItem item = lookupProperty(propType);
/* 794*/    if (item == null)
/* 795*/      return null; 
/* 797*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 801*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 809*/    PropListItem item = lookupProperty(propType);
/* 810*/    if (item == null)
/* 811*/      return 0; 
/* 813*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 817*/    PropListItem item = lookupProperty(propType);
/* 818*/    if (item == null)
/* 819*/      throw new IllegalStateException("missing prop: " + propType); 
/* 821*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 825*/    if (propType == 16) {
/* 826*/      putProp(51, new SimpleSourceFile((String)value, false));
/*   0*/      return;
/*   0*/    } 
/* 831*/    removeProp(propType);
/* 832*/    if (value != null)
/* 833*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 838*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 842*/    removeProp(propType);
/* 843*/    if (value != 0)
/* 844*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 849*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 853*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 858*/    int count = 0;
/* 859*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 860*/      count++; 
/* 863*/    int[] keys = new int[count];
/* 864*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 865*/      count--;
/* 866*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 869*/    Arrays.sort(keys);
/* 870*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 875*/    if (getType() == 39)
/* 876*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 879*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 885*/    if (getType() == 39)
/* 886*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 889*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 895*/    if (getType() == 40)
/* 896*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 899*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/* 905*/    if (getType() == 40)
/* 906*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 909*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 915*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 922*/    StringBuilder sb = new StringBuilder();
/* 923*/    toString(sb, printSource, printAnnotations, printType);
/* 924*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 932*/    sb.append(Token.name(this.type));
/* 933*/    if (this instanceof StringNode) {
/* 934*/      sb.append(' ');
/* 935*/      sb.append(getString());
/* 936*/    } else if (this.type == 105) {
/* 937*/      sb.append(' ');
/* 941*/      if (this.first == null || this.first.getType() != 38) {
/* 942*/        sb.append("<invalid>");
/*   0*/      } else {
/* 944*/        sb.append(this.first.getString());
/*   0*/      } 
/* 946*/    } else if (this.type == 39) {
/* 947*/      sb.append(' ');
/* 948*/      sb.append(getDouble());
/*   0*/    } 
/* 950*/    if (printSource) {
/* 951*/      int lineno = getLineno();
/* 952*/      if (lineno != -1) {
/* 953*/        sb.append(' ');
/* 954*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 958*/    if (printAnnotations) {
/* 959*/      int[] keys = getSortedPropTypes();
/* 960*/      for (int i = 0; i < keys.length; i++) {
/* 961*/        int type = keys[i];
/* 962*/        PropListItem x = lookupProperty(type);
/* 963*/        sb.append(" [");
/* 964*/        sb.append(propToString(type));
/* 965*/        sb.append(": ");
/* 967*/        switch (type) {
/*   0*/        
/*   0*/        } 
/* 969*/        String value = x.toString();
/* 972*/        sb.append(value);
/* 973*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/* 977*/    if (printType && 
/* 978*/      this.jsType != null) {
/* 979*/      String jsTypeString = this.jsType.toString();
/* 980*/      if (jsTypeString != null) {
/* 981*/        sb.append(" : ");
/* 982*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/* 990*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/* 995*/      StringBuilder s = new StringBuilder();
/* 996*/      appendStringTree(s);
/* 997*/      return s.toString();
/* 998*/    } catch (IOException e) {
/* 999*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1004*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1009*/    for (int i = 0; i != level; i++)
/*1010*/      sb.append("    "); 
/*1012*/    sb.append(n.toString());
/*1013*/    sb.append('\n');
/*1014*/    Node cursor = n.getFirstChild();
/*1015*/    for (; cursor != null; 
/*1016*/      cursor = cursor.getNext())
/*1017*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1074*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1079*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1083*/    StaticSourceFile file = getStaticSourceFile();
/*1084*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1089*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1096*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1103*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1107*/    StaticSourceFile file = getStaticSourceFile();
/*1108*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1112*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1116*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1120*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1124*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1128*/    StaticSourceFile file = getStaticSourceFile();
/*1129*/    int lineOffset = (file == null) ? Integer.MIN_VALUE : file.getLineOffset(getLineno());
/*1131*/    return lineOffset + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1135*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1139*/    int charno = getCharno();
/*1140*/    if (charno == -1)
/*1141*/      charno = 0; 
/*1143*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1147*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1151*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1155*/    this.sourcePosition = sourcePosition;
/*1157*/    Node child = getFirstChild();
/*1158*/    for (; child != null; child = child.getNext())
/*1159*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1170*/    if (lineno < 0 || charno < 0)
/*1171*/      return -1; 
/*1172*/    if ((charno & 0xFFFFF000) != 0)
/*1173*/      return lineno << 12 | 0xFFF; 
/*1175*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1184*/    if (lineCharNo == -1)
/*1185*/      return -1; 
/*1187*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1196*/    if (lineCharNo == -1)
/*1197*/      return -1; 
/*1199*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1216*/    if (this.first == null)
/*1217*/      return Collections.emptySet(); 
/*1219*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1233*/    return new SiblingNodeIterable(this);
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
/*1246*/      this.start = start;
/*1247*/      this.current = start;
/*1248*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1253*/      if (!this.used) {
/*1254*/        this.used = true;
/*1255*/        return this;
/*   0*/      } 
/*1263*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1269*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1274*/      if (this.current == null)
/*1275*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1278*/        return this.current;
/*   0*/      } finally {
/*1280*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1286*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1294*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1298*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1307*/    Preconditions.checkArgument((level >= 0));
/*1308*/    Node node = this;
/*1309*/    while (node != null && level-- > 0)
/*1310*/      node = node.getParent(); 
/*1312*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1319*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1332*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1337*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1340*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1345*/            if (!hasNext())
/*1345*/              throw new NoSuchElementException(); 
/*1346*/            Node n = Node.AncestorIterable.this.cur;
/*1347*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1348*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1353*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1366*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1376*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1380*/    int c = 0;
/*1381*/    for (Node n = this.first; n != null; n = n.next)
/*1382*/      c++; 
/*1384*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1389*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1390*/      if (child == n)
/*1391*/        return true; 
/*   0*/    } 
/*1394*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1402*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1403*/    if (diff != null)
/*1404*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1410*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1419*/    if (!isEquivalentTo(node2, false, false))
/*1420*/      return new NodeMismatch(this, node2); 
/*1423*/    NodeMismatch res = null;
/*1425*/    Node n = this.first, n2 = node2.first;
/*1426*/    for (; res == null && n != null; 
/*1427*/      n = n.next, n2 = n2.next) {
/*1428*/      if (node2 == null)
/*1429*/        throw new IllegalStateException(); 
/*1431*/      res = n.checkTreeEqualsImpl(n2);
/*1432*/      if (res == null)
/*1433*/        return res; 
/*   0*/    } 
/*1436*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1446*/    if (!isEquivalentTo(node2, true, false))
/*1447*/      return new NodeMismatch(this, node2); 
/*1450*/    NodeMismatch res = null;
/*1452*/    Node n = this.first, n2 = node2.first;
/*1453*/    for (; res == null && n != null; 
/*1454*/      n = n.next, n2 = n2.next) {
/*1455*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1456*/      if (res != null)
/*1457*/        return res; 
/*   0*/    } 
/*1460*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1465*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1473*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1483*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getClass() != node.getClass())
/*1486*/      return false; 
/*1489*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1490*/      return false; 
/*1493*/    if (this.type == 102 || this.type == 103) {
/*1494*/      int post1 = getIntProp(32);
/*1495*/      int post2 = node.getIntProp(32);
/*1496*/      if (post1 != post2)
/*1497*/        return false; 
/*1499*/    } else if (this.type == 40) {
/*1500*/      int quoted1 = getIntProp(36);
/*1501*/      int quoted2 = node.getIntProp(36);
/*1502*/      if (quoted1 != quoted2)
/*1503*/        return false; 
/*1506*/      int slashV1 = getIntProp(54);
/*1507*/      int slashV2 = node.getIntProp(54);
/*1508*/      if (slashV1 != slashV2)
/*1509*/        return false; 
/*1511*/    } else if (this.type == 37 && 
/*1512*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1513*/      return false;
/*   0*/    } 
/*1517*/    if (recurse) {
/*1519*/      Node n = this.first, n2 = node.first;
/*1520*/      for (; n != null; 
/*1521*/        n = n.next, n2 = n2.next) {
/*1522*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1523*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1528*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1540*/    if (this.type == 38)
/*1541*/      return getString(); 
/*1542*/    if (this.type == 33) {
/*1543*/      String left = getFirstChild().getQualifiedName();
/*1544*/      if (left == null)
/*1545*/        return null; 
/*1547*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1548*/    if (this.type == 42)
/*1549*/      return "this"; 
/*1551*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1560*/    switch (getType()) {
/*   0*/      case 38:
/*   0*/      case 42:
/*1563*/        return true;
/*   0*/      case 33:
/*1565*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1567*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1577*/    switch (getType()) {
/*   0*/      case 38:
/*1579*/        return true;
/*   0*/      case 33:
/*1581*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1583*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1595*/    Preconditions.checkState((this.parent != null));
/*1596*/    this.parent.removeChild(this);
/*1597*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1607*/    Node child = this.first;
/*1608*/    if (child != null)
/*1609*/      removeChild(child); 
/*1611*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1618*/    Node children = this.first;
/*1619*/    for (Node child = this.first; child != null; child = child.getNext())
/*1620*/      child.parent = null; 
/*1622*/    this.first = null;
/*1623*/    this.last = null;
/*1624*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1632*/    for (Node child = this.first; child != null; ) {
/*1633*/      Node nextChild = child.getNext();
/*1634*/      child.parent = null;
/*1635*/      child.next = null;
/*1636*/      child = nextChild;
/*   0*/    } 
/*1638*/    this.first = null;
/*1639*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1643*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1645*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1648*/    Node child = prev.next;
/*1649*/    prev.next = child.next;
/*1650*/    if (child == this.last)
/*1650*/      this.last = prev; 
/*1651*/    child.next = null;
/*1652*/    child.parent = null;
/*1653*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1662*/      result = (Node)clone();
/*1665*/      result.next = null;
/*1666*/      result.first = null;
/*1667*/      result.last = null;
/*1668*/      result.parent = null;
/*1669*/    } catch (CloneNotSupportedException e) {
/*1670*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1672*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1679*/    Node result = cloneNode();
/*1680*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1681*/      Node n2clone = n2.cloneTree();
/*1682*/      n2clone.parent = result;
/*1683*/      if (result.last != null)
/*1684*/        result.last.next = n2clone; 
/*1686*/      if (result.first == null)
/*1687*/        result.first = n2clone; 
/*1689*/      result.last = n2clone;
/*   0*/    } 
/*1691*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1702*/    if (getProp(40) == null)
/*1703*/      putProp(40, other.getProp(40)); 
/*1706*/    if (getProp(51) == null) {
/*1707*/      putProp(51, other.getProp(51));
/*1708*/      this.sourcePosition = other.sourcePosition;
/*1709*/    } else if (getProp(16) == null) {
/*1710*/      putProp(16, other.getProp(16));
/*1711*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1714*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1724*/    copyInformationFrom(other);
/*1725*/    Node child = getFirstChild();
/*1726*/    for (; child != null; child = child.getNext())
/*1727*/      child.copyInformationFromForTree(other); 
/*1730*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*1738*/    putProp(40, other.getProp(40));
/*1739*/    putProp(51, other.getProp(51));
/*1740*/    this.sourcePosition = other.sourcePosition;
/*1741*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcref(Node other) {
/*1745*/    return useSourceInfoFrom(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*1753*/    useSourceInfoFrom(other);
/*1754*/    Node child = getFirstChild();
/*1755*/    for (; child != null; child = child.getNext())
/*1756*/      child.useSourceInfoFromForTree(other); 
/*1759*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcrefTree(Node other) {
/*1763*/    return useSourceInfoFromForTree(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*1771*/    if (getProp(40) == null)
/*1772*/      putProp(40, other.getProp(40)); 
/*1775*/    if (getProp(51) == null) {
/*1776*/      putProp(51, other.getProp(51));
/*1777*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1780*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*1788*/    useSourceInfoIfMissingFrom(other);
/*1789*/    Node child = getFirstChild();
/*1790*/    for (; child != null; child = child.getNext())
/*1791*/      child.useSourceInfoIfMissingFromForTree(other); 
/*1794*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1801*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1805*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1809*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1822*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1823*/      if (jsDocInfo == null)
/*1826*/        jsDocInfo = new JSDocInfo(false); 
/*1828*/      String license = jsDocInfo.getLicense();
/*1829*/      if (license == null)
/*1830*/        license = ""; 
/*1832*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1833*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1843*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*1850*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1859*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1868*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*1877*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*1886*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*1894*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*1902*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*1909*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*1917*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*1925*/    if (getJSDocInfo() == null)
/*1926*/      setJSDocInfo(new JSDocInfo(false)); 
/*1928*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*1936*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*1944*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*1977*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*1982*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*1986*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*1993*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2001*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2007*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2011*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2016*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2021*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2025*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2033*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2038*/      removeFlag(1);
/*2039*/      removeFlag(4);
/*2040*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2044*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2048*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2052*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2056*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2060*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2068*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2080*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2089*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2096*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2103*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2110*/    throw new IllegalStateException("not a StringNode");
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2118*/      this.nodeA = nodeA;
/*2119*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2124*/      if (object instanceof NodeMismatch) {
/*2125*/        NodeMismatch that = (NodeMismatch)object;
/*2126*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2128*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2133*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAdd() {
/*2141*/    return (getType() == 21);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAnd() {
/*2145*/    return (getType() == 101);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayLit() {
/*2149*/    return (getType() == 63);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssign() {
/*2153*/    return (getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssignAdd() {
/*2157*/    return (getType() == 93);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*2161*/    return (getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBreak() {
/*2165*/    return (getType() == 116);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCall() {
/*2169*/    return (getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCase() {
/*2173*/    return (getType() == 111);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCatch() {
/*2177*/    return (getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isComma() {
/*2181*/    return (getType() == 85);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContinue() {
/*2185*/    return (getType() == 117);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDebugger() {
/*2189*/    return (getType() == 152);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDec() {
/*2193*/    return (getType() == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDefaultCase() {
/*2197*/    return (getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDelProp() {
/*2201*/    return (getType() == 31);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDo() {
/*2205*/    return (getType() == 114);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*2209*/    return (getType() == 124);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExprResult() {
/*2213*/    return (getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFalse() {
/*2217*/    return (getType() == 43);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFor() {
/*2221*/    return (getType() == 115);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFunction() {
/*2225*/    return (getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetterDef() {
/*2229*/    return (getType() == 147);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetElem() {
/*2233*/    return (getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetProp() {
/*2237*/    return (getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isHook() {
/*2241*/    return (getType() == 98);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIf() {
/*2245*/    return (getType() == 108);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIn() {
/*2249*/    return (getType() == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInc() {
/*2253*/    return (getType() == 102);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInstanceOf() {
/*2257*/    return (getType() == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabel() {
/*2261*/    return (getType() == 126);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabelName() {
/*2265*/    return (getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isName() {
/*2269*/    return (getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNE() {
/*2273*/    return (getType() == 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNew() {
/*2277*/    return (getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNot() {
/*2281*/    return (getType() == 26);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*2285*/    return (getType() == 41);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNumber() {
/*2289*/    return (getType() == 39);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isObjectLit() {
/*2293*/    return (getType() == 64);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOr() {
/*2297*/    return (getType() == 100);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isParamList() {
/*2301*/    return (getType() == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRegExp() {
/*2305*/    return (getType() == 47);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isReturn() {
/*2309*/    return (getType() == 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isScript() {
/*2313*/    return (getType() == 132);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSetterDef() {
/*2317*/    return (getType() == 148);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isString() {
/*2321*/    return (getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSwitch() {
/*2325*/    return (getType() == 110);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThis() {
/*2329*/    return (getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrow() {
/*2333*/    return (getType() == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrue() {
/*2337*/    return (getType() == 44);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTry() {
/*2341*/    return (getType() == 77);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeOf() {
/*2345*/    return (getType() == 32);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVar() {
/*2349*/    return (getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVoid() {
/*2353*/    return (getType() == 122);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWhile() {
/*2357*/    return (getType() == 113);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWith() {
/*2361*/    return (getType() == 119);
/*   0*/  }
/*   0*/}
