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
/*   0*/      case 54:
/* 145*/        return "slash_v";
/*   0*/    } 
/* 147*/    throw new IllegalStateException("unexpect prop id " + propType);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 156*/      super(39);
/* 157*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 161*/      super(39, lineno, charno);
/* 162*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 167*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 172*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 177*/      boolean equivalent = super.isEquivalentTo(node, compareJsType, recurse);
/* 178*/      if (equivalent) {
/* 179*/        double thisValue = getDouble();
/* 180*/        double thatValue = ((NumberNode)node).getDouble();
/* 181*/        if (thisValue == thatValue)
/* 183*/          return (thisValue != 0.0D || 1.0D / thisValue == 1.0D / thatValue); 
/*   0*/      } 
/* 186*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 197*/      super(type);
/* 198*/      if (null == str)
/* 199*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 201*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 205*/      super(type, lineno, charno);
/* 206*/      if (null == str)
/* 207*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 209*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 218*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 227*/      if (null == str)
/* 228*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 230*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 235*/      return (super.isEquivalentTo(node, compareJsType, recurse) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 247*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 255*/      putBooleanProp(36, true);
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
/* 278*/      this.propType = propType;
/* 279*/      this.next = next;
/*   0*/    }
/*   0*/    
/*   0*/    public int getType() {
/* 284*/      return this.propType;
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem getNext() {
/* 289*/      return this.next;
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
/* 304*/      super(propType, next);
/* 305*/      this.objectValue = objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 310*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 315*/      return this.objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 320*/      return (this.objectValue == null) ? "null" : this.objectValue.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 325*/      return new ObjectPropListItem(getType(), this.objectValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class IntPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    IntPropListItem(int propType, int intValue, Node.PropListItem next) {
/* 336*/      super(propType, next);
/* 337*/      this.intValue = intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 342*/      return this.intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 347*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 352*/      return String.valueOf(this.intValue);
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 357*/      return new IntPropListItem(getType(), this.intValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 362*/    this.type = nodeType;
/* 363*/    this.parent = null;
/* 364*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 368*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 370*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 373*/    this.type = nodeType;
/* 374*/    this.parent = null;
/* 375*/    this.first = this.last = child;
/* 376*/    child.next = null;
/* 377*/    child.parent = this;
/* 378*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 382*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 384*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 386*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 388*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 390*/    this.type = nodeType;
/* 391*/    this.parent = null;
/* 392*/    this.first = left;
/* 393*/    this.last = right;
/* 394*/    left.next = right;
/* 395*/    left.parent = this;
/* 396*/    right.next = null;
/* 397*/    right.parent = this;
/* 398*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 402*/    Preconditions.checkArgument((left.parent == null));
/* 403*/    Preconditions.checkArgument((left.next == null));
/* 404*/    Preconditions.checkArgument((mid.parent == null));
/* 405*/    Preconditions.checkArgument((mid.next == null));
/* 406*/    Preconditions.checkArgument((right.parent == null));
/* 407*/    Preconditions.checkArgument((right.next == null));
/* 408*/    this.type = nodeType;
/* 409*/    this.parent = null;
/* 410*/    this.first = left;
/* 411*/    this.last = right;
/* 412*/    left.next = mid;
/* 413*/    left.parent = this;
/* 414*/    mid.next = right;
/* 415*/    mid.parent = this;
/* 416*/    right.next = null;
/* 417*/    right.parent = this;
/* 418*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 422*/    Preconditions.checkArgument((left.parent == null));
/* 423*/    Preconditions.checkArgument((left.next == null));
/* 424*/    Preconditions.checkArgument((mid.parent == null));
/* 425*/    Preconditions.checkArgument((mid.next == null));
/* 426*/    Preconditions.checkArgument((mid2.parent == null));
/* 427*/    Preconditions.checkArgument((mid2.next == null));
/* 428*/    Preconditions.checkArgument((right.parent == null));
/* 429*/    Preconditions.checkArgument((right.next == null));
/* 430*/    this.type = nodeType;
/* 431*/    this.parent = null;
/* 432*/    this.first = left;
/* 433*/    this.last = right;
/* 434*/    left.next = mid;
/* 435*/    left.parent = this;
/* 436*/    mid.next = mid2;
/* 437*/    mid.parent = this;
/* 438*/    mid2.next = right;
/* 439*/    mid2.parent = this;
/* 440*/    right.next = null;
/* 441*/    right.parent = this;
/* 442*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 446*/    this.type = nodeType;
/* 447*/    this.parent = null;
/* 448*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 452*/    this(nodeType, child);
/* 453*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 457*/    this(nodeType, left, right);
/* 458*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 463*/    this(nodeType, left, mid, right);
/* 464*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 469*/    this(nodeType, left, mid, mid2, right);
/* 470*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 474*/    this(nodeType, children);
/* 475*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 479*/    this.type = nodeType;
/* 480*/    this.parent = null;
/* 481*/    if (children.length != 0) {
/* 482*/      this.first = children[0];
/* 483*/      this.last = children[children.length - 1];
/* 485*/      for (int i = 1; i < children.length; i++) {
/* 486*/        if (null != (children[i - 1]).next)
/* 488*/          throw new IllegalArgumentException("duplicate child"); 
/* 490*/        (children[i - 1]).next = children[i];
/* 491*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 492*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 494*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 495*/      (children[children.length - 1]).parent = this;
/* 497*/      if (null != this.last.next)
/* 499*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 505*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 509*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 513*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 517*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 521*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 525*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 529*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 533*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 537*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 541*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 545*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 549*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 553*/    if (child == this.first)
/* 554*/      return null; 
/* 556*/    Node n = this.first;
/* 557*/    while (n.next != child) {
/* 558*/      n = n.next;
/* 559*/      if (n == null)
/* 560*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 563*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 567*/    Node n = this.first;
/* 568*/    while (i > 0) {
/* 569*/      n = n.next;
/* 570*/      i--;
/*   0*/    } 
/* 572*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOfChild(Node child) {
/* 576*/    Node n = this.first;
/* 577*/    int i = 0;
/* 578*/    while (n != null) {
/* 579*/      if (child == n)
/* 580*/        return i; 
/* 583*/      n = n.next;
/* 584*/      i++;
/*   0*/    } 
/* 586*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 590*/    Node n = this;
/* 591*/    while (n.next != null)
/* 592*/      n = n.next; 
/* 594*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 598*/    Preconditions.checkArgument((child.parent == null));
/* 599*/    Preconditions.checkArgument((child.next == null));
/* 600*/    child.parent = this;
/* 601*/    child.next = this.first;
/* 602*/    this.first = child;
/* 603*/    if (this.last == null)
/* 604*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 609*/    Preconditions.checkArgument((child.parent == null));
/* 610*/    Preconditions.checkArgument((child.next == null));
/* 611*/    child.parent = this;
/* 612*/    child.next = null;
/* 613*/    if (this.last == null) {
/* 614*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 617*/    this.last.next = child;
/* 618*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 622*/    for (Node child = children; child != null; child = child.next) {
/* 623*/      Preconditions.checkArgument((child.parent == null));
/* 624*/      child.parent = this;
/*   0*/    } 
/* 626*/    Node lastSib = children.getLastSibling();
/* 627*/    lastSib.next = this.first;
/* 628*/    this.first = children;
/* 629*/    if (this.last == null)
/* 630*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 635*/    addChildrenAfter(children, getLastChild());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 642*/    Preconditions.checkArgument((node != null && node.parent == this), "The existing child node of the parent should not be null.");
/* 644*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 646*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 648*/    if (this.first == node) {
/* 649*/      newChild.parent = this;
/* 650*/      newChild.next = this.first;
/* 651*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 654*/    Node prev = getChildBefore(node);
/* 655*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 662*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 664*/    addChildrenAfter(newChild, node);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenAfter(Node children, Node node) {
/* 671*/    Preconditions.checkArgument((node == null || node.parent == this));
/* 672*/    for (Node child = children; child != null; child = child.next) {
/* 673*/      Preconditions.checkArgument((child.parent == null));
/* 674*/      child.parent = this;
/*   0*/    } 
/* 677*/    Node lastSibling = children.getLastSibling();
/* 678*/    if (node != null) {
/* 679*/      Node oldNext = node.next;
/* 680*/      node.next = children;
/* 681*/      lastSibling.next = oldNext;
/* 682*/      if (node == this.last)
/* 683*/        this.last = lastSibling; 
/*   0*/    } else {
/* 687*/      if (this.first != null) {
/* 688*/        lastSibling.next = this.first;
/*   0*/      } else {
/* 690*/        this.last = lastSibling;
/*   0*/      } 
/* 692*/      this.first = children;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 700*/    Node prev = getChildBefore(child);
/* 701*/    if (prev == null) {
/* 702*/      this.first = this.first.next;
/*   0*/    } else {
/* 704*/      prev.next = child.next;
/*   0*/    } 
/* 705*/    if (child == this.last)
/* 705*/      this.last = prev; 
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
/* 732*/    child.next = null;
/* 733*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 737*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 740*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 742*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 746*/    newChild.copyInformationFrom(prevChild);
/* 748*/    Node child = prevChild.next;
/* 749*/    newChild.next = child.next;
/* 750*/    newChild.parent = this;
/* 751*/    prevChild.next = newChild;
/* 752*/    if (child == this.last)
/* 753*/      this.last = newChild; 
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
/* 810*/    if (propType == 16)
/* 811*/      return getSourceFileName(); 
/* 814*/    PropListItem item = lookupProperty(propType);
/* 815*/    if (item == null)
/* 816*/      return null; 
/* 818*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 822*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 830*/    PropListItem item = lookupProperty(propType);
/* 831*/    if (item == null)
/* 832*/      return 0; 
/* 834*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 838*/    PropListItem item = lookupProperty(propType);
/* 839*/    if (item == null)
/* 840*/      throw new IllegalStateException("missing prop: " + propType); 
/* 842*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 846*/    if (propType == 16) {
/* 847*/      putProp(51, new SimpleSourceFile((String)value, false));
/*   0*/      return;
/*   0*/    } 
/* 852*/    removeProp(propType);
/* 853*/    if (value != null)
/* 854*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 859*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 863*/    removeProp(propType);
/* 864*/    if (value != 0)
/* 865*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 870*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 874*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 879*/    int count = 0;
/* 880*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 881*/      count++; 
/* 884*/    int[] keys = new int[count];
/* 885*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 886*/      count--;
/* 887*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 890*/    Arrays.sort(keys);
/* 891*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 896*/    if (getType() == 39)
/* 897*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 900*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 906*/    if (getType() == 39)
/* 907*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 910*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 916*/    if (getType() == 40)
/* 917*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 920*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/* 926*/    if (getType() == 40)
/* 927*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 930*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 936*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 943*/    StringBuilder sb = new StringBuilder();
/* 944*/    toString(sb, printSource, printAnnotations, printType);
/* 945*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 953*/    sb.append(Token.name(this.type));
/* 954*/    if (this instanceof StringNode) {
/* 955*/      sb.append(' ');
/* 956*/      sb.append(getString());
/* 957*/    } else if (this.type == 105) {
/* 958*/      sb.append(' ');
/* 962*/      if (this.first == null || this.first.getType() != 38) {
/* 963*/        sb.append("<invalid>");
/*   0*/      } else {
/* 965*/        sb.append(this.first.getString());
/*   0*/      } 
/* 967*/    } else if (this.type == 39) {
/* 968*/      sb.append(' ');
/* 969*/      sb.append(getDouble());
/*   0*/    } 
/* 971*/    if (printSource) {
/* 972*/      int lineno = getLineno();
/* 973*/      if (lineno != -1) {
/* 974*/        sb.append(' ');
/* 975*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 979*/    if (printAnnotations) {
/* 980*/      int[] keys = getSortedPropTypes();
/* 981*/      for (int i = 0; i < keys.length; i++) {
/* 982*/        int type = keys[i];
/* 983*/        PropListItem x = lookupProperty(type);
/* 984*/        sb.append(" [");
/* 985*/        sb.append(propToString(type));
/* 986*/        sb.append(": ");
/* 988*/        switch (type) {
/*   0*/        
/*   0*/        } 
/* 990*/        String value = x.toString();
/* 993*/        sb.append(value);
/* 994*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/* 998*/    if (printType && 
/* 999*/      this.jsType != null) {
/*1000*/      String jsTypeString = this.jsType.toString();
/*1001*/      if (jsTypeString != null) {
/*1002*/        sb.append(" : ");
/*1003*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1011*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1016*/      StringBuilder s = new StringBuilder();
/*1017*/      appendStringTree(s);
/*1018*/      return s.toString();
/*1019*/    } catch (IOException e) {
/*1020*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1025*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1030*/    for (int i = 0; i != level; i++)
/*1031*/      sb.append("    "); 
/*1033*/    sb.append(n.toString());
/*1034*/    sb.append('\n');
/*1035*/    Node cursor = n.getFirstChild();
/*1036*/    for (; cursor != null; 
/*1037*/      cursor = cursor.getNext())
/*1038*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1095*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1100*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1104*/    StaticSourceFile file = getStaticSourceFile();
/*1105*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1110*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1117*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1124*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1128*/    StaticSourceFile file = getStaticSourceFile();
/*1129*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1133*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1137*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1141*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1145*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1149*/    StaticSourceFile file = getStaticSourceFile();
/*1150*/    if (file == null)
/*1151*/      return -1; 
/*1153*/    int lineno = getLineno();
/*1154*/    if (lineno == -1)
/*1155*/      return -1; 
/*1157*/    return file.getLineOffset(lineno) + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1161*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1165*/    int charno = getCharno();
/*1166*/    if (charno == -1)
/*1167*/      charno = 0; 
/*1169*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1173*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1177*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1181*/    this.sourcePosition = sourcePosition;
/*1183*/    Node child = getFirstChild();
/*1184*/    for (; child != null; child = child.getNext())
/*1185*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1196*/    if (lineno < 0 || charno < 0)
/*1197*/      return -1; 
/*1198*/    if ((charno & 0xFFFFF000) != 0)
/*1199*/      return lineno << 12 | 0xFFF; 
/*1201*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1210*/    if (lineCharNo == -1)
/*1211*/      return -1; 
/*1213*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1222*/    if (lineCharNo == -1)
/*1223*/      return -1; 
/*1225*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1242*/    if (this.first == null)
/*1243*/      return Collections.emptySet(); 
/*1245*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1259*/    return new SiblingNodeIterable(this);
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
/*1272*/      this.start = start;
/*1273*/      this.current = start;
/*1274*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1279*/      if (!this.used) {
/*1280*/        this.used = true;
/*1281*/        return this;
/*   0*/      } 
/*1289*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1295*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1300*/      if (this.current == null)
/*1301*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1304*/        return this.current;
/*   0*/      } finally {
/*1306*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1312*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1320*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1324*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1333*/    Preconditions.checkArgument((level >= 0));
/*1334*/    Node node = this;
/*1335*/    while (node != null && level-- > 0)
/*1336*/      node = node.getParent(); 
/*1338*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1345*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1358*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1363*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1366*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1371*/            if (!hasNext())
/*1371*/              throw new NoSuchElementException(); 
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
/*1445*/    if (!isEquivalentTo(node2, false, false))
/*1446*/      return new NodeMismatch(this, node2); 
/*1449*/    NodeMismatch res = null;
/*1451*/    Node n = this.first, n2 = node2.first;
/*1452*/    for (; res == null && n != null; 
/*1453*/      n2 = n = n.next) {
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
/*1472*/    if (!isEquivalentTo(node2, true, false))
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
/*1491*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1499*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1509*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getClass() != node.getClass())
/*1512*/      return false; 
/*1515*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1516*/      return false; 
/*1519*/    if (this.type == 102 || this.type == 103) {
/*1520*/      int post1 = getIntProp(32);
/*1521*/      int post2 = node.getIntProp(32);
/*1522*/      if (post1 != post2)
/*1523*/        return false; 
/*1525*/    } else if (this.type == 40) {
/*1526*/      int quoted1 = getIntProp(36);
/*1527*/      int quoted2 = node.getIntProp(36);
/*1528*/      if (quoted1 != quoted2)
/*1529*/        return false; 
/*1532*/      int slashV1 = getIntProp(54);
/*1533*/      int slashV2 = node.getIntProp(54);
/*1534*/      if (slashV1 != slashV2)
/*1535*/        return false; 
/*1537*/    } else if (this.type == 37 && 
/*1538*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1539*/      return false;
/*   0*/    } 
/*1543*/    if (recurse) {
/*1545*/      Node n = this.first, n2 = node.first;
/*1546*/      for (; n != null; 
/*1547*/        n = n.next, n2 = n2.next) {
/*1548*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1549*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1554*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1566*/    if (this.type == 38)
/*1567*/      return getString(); 
/*1568*/    if (this.type == 33) {
/*1569*/      String left = getFirstChild().getQualifiedName();
/*1570*/      if (left == null)
/*1571*/        return null; 
/*1573*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1574*/    if (this.type == 42)
/*1575*/      return "this"; 
/*1577*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1586*/    switch (getType()) {
/*   0*/      case 38:
/*   0*/      case 42:
/*1589*/        return true;
/*   0*/      case 33:
/*1591*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1593*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1603*/    switch (getType()) {
/*   0*/      case 38:
/*1605*/        return true;
/*   0*/      case 33:
/*1607*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1609*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1621*/    Preconditions.checkState((this.parent != null));
/*1622*/    this.parent.removeChild(this);
/*1623*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1633*/    Node child = this.first;
/*1634*/    if (child != null)
/*1635*/      removeChild(child); 
/*1637*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1644*/    Node children = this.first;
/*1645*/    for (Node child = this.first; child != null; child = child.getNext())
/*1646*/      child.parent = null; 
/*1648*/    this.first = null;
/*1649*/    this.last = null;
/*1650*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1658*/    for (Node child = this.first; child != null; ) {
/*1659*/      Node nextChild = child.getNext();
/*1660*/      child.parent = null;
/*1661*/      child.next = null;
/*1662*/      child = nextChild;
/*   0*/    } 
/*1664*/    this.first = null;
/*1665*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1669*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1671*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1674*/    Node child = prev.next;
/*1675*/    prev.next = child.next;
/*1676*/    if (child == this.last)
/*1676*/      this.last = prev; 
/*1677*/    child.next = null;
/*1678*/    child.parent = null;
/*1679*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1688*/      result = (Node)clone();
/*1691*/      result.next = null;
/*1692*/      result.first = null;
/*1693*/      result.last = null;
/*1694*/      result.parent = null;
/*1695*/    } catch (CloneNotSupportedException e) {
/*1696*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1698*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1705*/    Node result = cloneNode();
/*1706*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1707*/      Node n2clone = n2.cloneTree();
/*1708*/      n2clone.parent = result;
/*1709*/      if (result.last != null)
/*1710*/        result.last.next = n2clone; 
/*1712*/      if (result.first == null)
/*1713*/        result.first = n2clone; 
/*1715*/      result.last = n2clone;
/*   0*/    } 
/*1717*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1728*/    if (getProp(40) == null)
/*1729*/      putProp(40, other.getProp(40)); 
/*1732*/    if (getProp(51) == null) {
/*1733*/      putProp(51, other.getProp(51));
/*1734*/      this.sourcePosition = other.sourcePosition;
/*1735*/    } else if (getProp(16) == null) {
/*1736*/      putProp(16, other.getProp(16));
/*1737*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1740*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1750*/    copyInformationFrom(other);
/*1751*/    Node child = getFirstChild();
/*1752*/    for (; child != null; child = child.getNext())
/*1753*/      child.copyInformationFromForTree(other); 
/*1756*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*1764*/    putProp(40, other.getProp(40));
/*1765*/    putProp(51, other.getProp(51));
/*1766*/    this.sourcePosition = other.sourcePosition;
/*1767*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcref(Node other) {
/*1771*/    return useSourceInfoFrom(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*1779*/    useSourceInfoFrom(other);
/*1780*/    Node child = getFirstChild();
/*1781*/    for (; child != null; child = child.getNext())
/*1782*/      child.useSourceInfoFromForTree(other); 
/*1785*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node srcrefTree(Node other) {
/*1789*/    return useSourceInfoFromForTree(other);
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*1797*/    if (getProp(40) == null)
/*1798*/      putProp(40, other.getProp(40)); 
/*1801*/    if (getProp(51) == null) {
/*1802*/      putProp(51, other.getProp(51));
/*1803*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1806*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*1814*/    useSourceInfoIfMissingFrom(other);
/*1815*/    Node child = getFirstChild();
/*1816*/    for (; child != null; child = child.getNext())
/*1817*/      child.useSourceInfoIfMissingFromForTree(other); 
/*1820*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1827*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1831*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1835*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1848*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1849*/      if (jsDocInfo == null)
/*1852*/        jsDocInfo = new JSDocInfo(false); 
/*1854*/      String license = jsDocInfo.getLicense();
/*1855*/      if (license == null)
/*1856*/        license = ""; 
/*1858*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1859*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1869*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*1876*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1885*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1894*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*1903*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*1912*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*1920*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*1928*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*1935*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*1943*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*1951*/    if (getJSDocInfo() == null)
/*1952*/      setJSDocInfo(new JSDocInfo(false)); 
/*1954*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*1962*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*1970*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2003*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2008*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2012*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2019*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2027*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2033*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2037*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2042*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2047*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2051*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2059*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2064*/      removeFlag(1);
/*2065*/      removeFlag(4);
/*2066*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2070*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2074*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2078*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2082*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2086*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2094*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2106*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2115*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2122*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2129*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2136*/    throw new IllegalStateException("not a StringNode");
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2144*/      this.nodeA = nodeA;
/*2145*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2150*/      if (object instanceof NodeMismatch) {
/*2151*/        NodeMismatch that = (NodeMismatch)object;
/*2152*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2154*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2159*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAdd() {
/*2167*/    return (getType() == 21);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAnd() {
/*2171*/    return (getType() == 101);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isArrayLit() {
/*2175*/    return (getType() == 63);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssign() {
/*2179*/    return (getType() == 86);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAssignAdd() {
/*2183*/    return (getType() == 93);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*2187*/    return (getType() == 125);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBreak() {
/*2191*/    return (getType() == 116);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCall() {
/*2195*/    return (getType() == 37);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCase() {
/*2199*/    return (getType() == 111);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCatch() {
/*2203*/    return (getType() == 120);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isComma() {
/*2207*/    return (getType() == 85);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isContinue() {
/*2211*/    return (getType() == 117);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDebugger() {
/*2215*/    return (getType() == 152);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDec() {
/*2219*/    return (getType() == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDefaultCase() {
/*2223*/    return (getType() == 112);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDelProp() {
/*2227*/    return (getType() == 31);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDo() {
/*2231*/    return (getType() == 114);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*2235*/    return (getType() == 124);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExprResult() {
/*2239*/    return (getType() == 130);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFalse() {
/*2243*/    return (getType() == 43);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFor() {
/*2247*/    return (getType() == 115);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFunction() {
/*2251*/    return (getType() == 105);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetterDef() {
/*2255*/    return (getType() == 147);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetElem() {
/*2259*/    return (getType() == 35);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGetProp() {
/*2263*/    return (getType() == 33);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isHook() {
/*2267*/    return (getType() == 98);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIf() {
/*2271*/    return (getType() == 108);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isIn() {
/*2275*/    return (getType() == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInc() {
/*2279*/    return (getType() == 102);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInstanceOf() {
/*2283*/    return (getType() == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabel() {
/*2287*/    return (getType() == 126);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLabelName() {
/*2291*/    return (getType() == 153);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isName() {
/*2295*/    return (getType() == 38);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNE() {
/*2299*/    return (getType() == 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNew() {
/*2303*/    return (getType() == 30);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNot() {
/*2307*/    return (getType() == 26);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*2311*/    return (getType() == 41);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNumber() {
/*2315*/    return (getType() == 39);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isObjectLit() {
/*2319*/    return (getType() == 64);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOr() {
/*2323*/    return (getType() == 100);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isParamList() {
/*2327*/    return (getType() == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRegExp() {
/*2331*/    return (getType() == 47);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isReturn() {
/*2335*/    return (getType() == 4);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isScript() {
/*2339*/    return (getType() == 132);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSetterDef() {
/*2343*/    return (getType() == 148);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isString() {
/*2347*/    return (getType() == 40);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSwitch() {
/*2351*/    return (getType() == 110);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThis() {
/*2355*/    return (getType() == 42);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isThrow() {
/*2359*/    return (getType() == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTrue() {
/*2363*/    return (getType() == 44);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTry() {
/*2367*/    return (getType() == 77);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeOf() {
/*2371*/    return (getType() == 32);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVar() {
/*2375*/    return (getType() == 118);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVoid() {
/*2379*/    return (getType() == 122);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWhile() {
/*2383*/    return (getType() == 113);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isWith() {
/*2387*/    return (getType() == 119);
/*   0*/  }
/*   0*/}
