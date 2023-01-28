/*   0*/package com.google.javascript.rhino;
/*   0*/
/*   0*/import com.google.common.annotations.VisibleForTesting;
/*   0*/import com.google.common.base.Objects;
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
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
/*   0*/  public static final int LOCAL_BLOCK_PROP = -3;
/*   0*/  
/*   0*/  public static final int OBJECT_IDS_PROP = -2;
/*   0*/  
/*   0*/  public static final int CATCH_SCOPE_PROP = -1;
/*   0*/  
/*   0*/  public static final int LABEL_ID_PROP = 0;
/*   0*/  
/*   0*/  public static final int TARGET_PROP = 1;
/*   0*/  
/*   0*/  public static final int BREAK_PROP = 2;
/*   0*/  
/*   0*/  public static final int CONTINUE_PROP = 3;
/*   0*/  
/*   0*/  public static final int ENUM_PROP = 4;
/*   0*/  
/*   0*/  public static final int FUNCTION_PROP = 5;
/*   0*/  
/*   0*/  public static final int TEMP_PROP = 6;
/*   0*/  
/*   0*/  public static final int LOCAL_PROP = 7;
/*   0*/  
/*   0*/  public static final int CODEOFFSET_PROP = 8;
/*   0*/  
/*   0*/  public static final int FIXUPS_PROP = 9;
/*   0*/  
/*   0*/  public static final int VARS_PROP = 10;
/*   0*/  
/*   0*/  public static final int USES_PROP = 11;
/*   0*/  
/*   0*/  public static final int REGEXP_PROP = 12;
/*   0*/  
/*   0*/  public static final int CASES_PROP = 13;
/*   0*/  
/*   0*/  public static final int DEFAULT_PROP = 14;
/*   0*/  
/*   0*/  public static final int CASEARRAY_PROP = 15;
/*   0*/  
/*   0*/  public static final int SOURCENAME_PROP = 16;
/*   0*/  
/*   0*/  public static final int TYPE_PROP = 17;
/*   0*/  
/*   0*/  public static final int SPECIAL_PROP_PROP = 18;
/*   0*/  
/*   0*/  public static final int LABEL_PROP = 19;
/*   0*/  
/*   0*/  public static final int FINALLY_PROP = 20;
/*   0*/  
/*   0*/  public static final int LOCALCOUNT_PROP = 21;
/*   0*/  
/*   0*/  public static final int TARGETBLOCK_PROP = 22;
/*   0*/  
/*   0*/  public static final int VARIABLE_PROP = 23;
/*   0*/  
/*   0*/  public static final int LASTUSE_PROP = 24;
/*   0*/  
/*   0*/  public static final int ISNUMBER_PROP = 25;
/*   0*/  
/*   0*/  public static final int DIRECTCALL_PROP = 26;
/*   0*/  
/*   0*/  public static final int SPECIALCALL_PROP = 27;
/*   0*/  
/*   0*/  public static final int DEBUGSOURCE_PROP = 28;
/*   0*/  
/*   0*/  public static final int JSDOC_INFO_PROP = 29;
/*   0*/  
/*   0*/  public static final int VAR_ARGS_NAME = 29;
/*   0*/  
/*   0*/  public static final int SKIP_INDEXES_PROP = 30;
/*   0*/  
/*   0*/  public static final int INCRDECR_PROP = 31;
/*   0*/  
/*   0*/  public static final int MEMBER_TYPE_PROP = 32;
/*   0*/  
/*   0*/  public static final int NAME_PROP = 33;
/*   0*/  
/*   0*/  public static final int PARENTHESIZED_PROP = 34;
/*   0*/  
/*   0*/  public static final int QUOTED_PROP = 35;
/*   0*/  
/*   0*/  public static final int OPT_ARG_NAME = 36;
/*   0*/  
/*   0*/  public static final int SYNTHETIC_BLOCK_PROP = 37;
/*   0*/  
/*   0*/  public static final int EMPTY_BLOCK = 38;
/*   0*/  
/*   0*/  public static final int ORIGINALNAME_PROP = 39;
/*   0*/  
/*   0*/  public static final int BRACELESS_TYPE = 40;
/*   0*/  
/*   0*/  public static final int SIDE_EFFECT_FLAGS = 41;
/*   0*/  
/*   0*/  public static final int IS_CONSTANT_NAME = 42;
/*   0*/  
/*   0*/  public static final int IS_OPTIONAL_PARAM = 43;
/*   0*/  
/*   0*/  public static final int IS_VAR_ARGS_PARAM = 44;
/*   0*/  
/*   0*/  public static final int IS_NAMESPACE = 45;
/*   0*/  
/*   0*/  public static final int IS_DISPATCHER = 46;
/*   0*/  
/*   0*/  public static final int DIRECTIVES = 47;
/*   0*/  
/*   0*/  public static final int DIRECT_EVAL = 48;
/*   0*/  
/*   0*/  public static final int FREE_CALL = 49;
/*   0*/  
/*   0*/  public static final int LAST_PROP = 49;
/*   0*/  
/*   0*/  public static final int BOTH = 0;
/*   0*/  
/*   0*/  public static final int LEFT = 1;
/*   0*/  
/*   0*/  public static final int RIGHT = 2;
/*   0*/  
/*   0*/  public static final int NON_SPECIALCALL = 0;
/*   0*/  
/*   0*/  public static final int SPECIALCALL_EVAL = 1;
/*   0*/  
/*   0*/  public static final int SPECIALCALL_WITH = 2;
/*   0*/  
/*   0*/  public static final int DECR_FLAG = 1;
/*   0*/  
/*   0*/  public static final int POST_FLAG = 2;
/*   0*/  
/*   0*/  public static final int PROPERTY_FLAG = 1;
/*   0*/  
/*   0*/  public static final int ATTRIBUTE_FLAG = 2;
/*   0*/  
/*   0*/  public static final int DESCENDANTS_FLAG = 4;
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
/* 175*/    switch (propType) {
/*   0*/      case -3:
/* 176*/        return "local_block";
/*   0*/      case -2:
/* 177*/        return "object_ids_prop";
/*   0*/      case -1:
/* 178*/        return "catch_scope_prop";
/*   0*/      case 0:
/* 179*/        return "label_id_prop";
/*   0*/      case 1:
/* 180*/        return "target";
/*   0*/      case 2:
/* 181*/        return "break";
/*   0*/      case 3:
/* 182*/        return "continue";
/*   0*/      case 4:
/* 183*/        return "enum";
/*   0*/      case 5:
/* 184*/        return "function";
/*   0*/      case 6:
/* 185*/        return "temp";
/*   0*/      case 7:
/* 186*/        return "local";
/*   0*/      case 8:
/* 187*/        return "codeoffset";
/*   0*/      case 9:
/* 188*/        return "fixups";
/*   0*/      case 10:
/* 189*/        return "vars";
/*   0*/      case 11:
/* 190*/        return "uses";
/*   0*/      case 12:
/* 191*/        return "regexp";
/*   0*/      case 13:
/* 192*/        return "cases";
/*   0*/      case 14:
/* 193*/        return "default";
/*   0*/      case 15:
/* 194*/        return "casearray";
/*   0*/      case 16:
/* 195*/        return "sourcename";
/*   0*/      case 17:
/* 196*/        return "type";
/*   0*/      case 18:
/* 197*/        return "special_prop";
/*   0*/      case 19:
/* 198*/        return "label";
/*   0*/      case 20:
/* 199*/        return "finally";
/*   0*/      case 21:
/* 200*/        return "localcount";
/*   0*/      case 22:
/* 202*/        return "targetblock";
/*   0*/      case 23:
/* 203*/        return "variable";
/*   0*/      case 24:
/* 204*/        return "lastuse";
/*   0*/      case 25:
/* 205*/        return "isnumber";
/*   0*/      case 26:
/* 206*/        return "directcall";
/*   0*/      case 27:
/* 208*/        return "specialcall";
/*   0*/      case 28:
/* 209*/        return "debugsource";
/*   0*/      case 29:
/* 211*/        return "jsdoc_info";
/*   0*/      case 30:
/* 213*/        return "skip_indexes";
/*   0*/      case 31:
/* 214*/        return "incrdecr";
/*   0*/      case 32:
/* 215*/        return "member_type";
/*   0*/      case 33:
/* 216*/        return "name";
/*   0*/      case 34:
/* 217*/        return "parenthesized";
/*   0*/      case 35:
/* 218*/        return "quoted";
/*   0*/      case 37:
/* 220*/        return "synthetic";
/*   0*/      case 38:
/* 221*/        return "empty_block";
/*   0*/      case 39:
/* 222*/        return "originalname";
/*   0*/      case 41:
/* 223*/        return "side_effect_flags";
/*   0*/      case 42:
/* 225*/        return "is_constant_name";
/*   0*/      case 43:
/* 226*/        return "is_optional_param";
/*   0*/      case 44:
/* 227*/        return "is_var_args_param";
/*   0*/      case 45:
/* 228*/        return "is_namespace";
/*   0*/      case 46:
/* 229*/        return "is_dispatcher";
/*   0*/      case 47:
/* 230*/        return "directives";
/*   0*/      case 48:
/* 231*/        return "direct_eval";
/*   0*/      case 49:
/* 232*/        return "free_call";
/*   0*/    } 
/* 234*/    Kit.codeBug();
/* 236*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 244*/      super(39);
/* 245*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 249*/      super(39, lineno, charno);
/* 250*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 255*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 260*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 265*/      return (super.isEquivalentTo(node, compareJsType, recurse) && getDouble() == ((NumberNode)node).getDouble());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 277*/      super(type);
/* 278*/      if (null == str)
/* 279*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 281*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 285*/      super(type, lineno, charno);
/* 286*/      if (null == str)
/* 287*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 289*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 298*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 307*/      if (null == str)
/* 308*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 310*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 315*/      return (super.isEquivalentTo(node, compareJsType, recurse) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 327*/      return getBooleanProp(35);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 335*/      putBooleanProp(35, true);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class PropListItem implements Serializable {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final PropListItem next;
/*   0*/    
/*   0*/    final int type;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    final Object objectValue;
/*   0*/    
/*   0*/    PropListItem(int type, int intValue, PropListItem next) {
/* 351*/      this(type, intValue, null, next);
/*   0*/    }
/*   0*/    
/*   0*/    PropListItem(int type, Object objectValue, PropListItem next) {
/* 355*/      this(type, 0, objectValue, next);
/*   0*/    }
/*   0*/    
/*   0*/    PropListItem(int type, int intValue, Object objectValue, PropListItem next) {
/* 360*/      this.type = type;
/* 361*/      this.intValue = intValue;
/* 362*/      this.objectValue = objectValue;
/* 363*/      this.next = next;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 369*/    this.type = nodeType;
/* 370*/    this.parent = null;
/* 371*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 375*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 377*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 380*/    this.type = nodeType;
/* 381*/    this.parent = null;
/* 382*/    this.first = this.last = child;
/* 383*/    child.next = null;
/* 384*/    child.parent = this;
/* 385*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 389*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 391*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 393*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 395*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 397*/    this.type = nodeType;
/* 398*/    this.parent = null;
/* 399*/    this.first = left;
/* 400*/    this.last = right;
/* 401*/    left.next = right;
/* 402*/    left.parent = this;
/* 403*/    right.next = null;
/* 404*/    right.parent = this;
/* 405*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 409*/    Preconditions.checkArgument((left.parent == null));
/* 410*/    Preconditions.checkArgument((left.next == null));
/* 411*/    Preconditions.checkArgument((mid.parent == null));
/* 412*/    Preconditions.checkArgument((mid.next == null));
/* 413*/    Preconditions.checkArgument((right.parent == null));
/* 414*/    Preconditions.checkArgument((right.next == null));
/* 415*/    this.type = nodeType;
/* 416*/    this.parent = null;
/* 417*/    this.first = left;
/* 418*/    this.last = right;
/* 419*/    left.next = mid;
/* 420*/    left.parent = this;
/* 421*/    mid.next = right;
/* 422*/    mid.parent = this;
/* 423*/    right.next = null;
/* 424*/    right.parent = this;
/* 425*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 429*/    Preconditions.checkArgument((left.parent == null));
/* 430*/    Preconditions.checkArgument((left.next == null));
/* 431*/    Preconditions.checkArgument((mid.parent == null));
/* 432*/    Preconditions.checkArgument((mid.next == null));
/* 433*/    Preconditions.checkArgument((mid2.parent == null));
/* 434*/    Preconditions.checkArgument((mid2.next == null));
/* 435*/    Preconditions.checkArgument((right.parent == null));
/* 436*/    Preconditions.checkArgument((right.next == null));
/* 437*/    this.type = nodeType;
/* 438*/    this.parent = null;
/* 439*/    this.first = left;
/* 440*/    this.last = right;
/* 441*/    left.next = mid;
/* 442*/    left.parent = this;
/* 443*/    mid.next = mid2;
/* 444*/    mid.parent = this;
/* 445*/    mid2.next = right;
/* 446*/    mid2.parent = this;
/* 447*/    right.next = null;
/* 448*/    right.parent = this;
/* 449*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 453*/    this.type = nodeType;
/* 454*/    this.parent = null;
/* 455*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 459*/    this(nodeType, child);
/* 460*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 464*/    this(nodeType, left, right);
/* 465*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 470*/    this(nodeType, left, mid, right);
/* 471*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 476*/    this(nodeType, left, mid, mid2, right);
/* 477*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 481*/    this(nodeType, children);
/* 482*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 486*/    this.type = nodeType;
/* 487*/    this.parent = null;
/* 488*/    if (children.length != 0) {
/* 489*/      this.first = children[0];
/* 490*/      this.last = children[children.length - 1];
/* 492*/      for (int i = 1; i < children.length; i++) {
/* 493*/        if (null != (children[i - 1]).next)
/* 495*/          throw new IllegalArgumentException("duplicate child"); 
/* 497*/        (children[i - 1]).next = children[i];
/* 498*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 499*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 501*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 502*/      (children[children.length - 1]).parent = this;
/* 504*/      if (null != this.last.next)
/* 506*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 512*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 516*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 520*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 524*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 528*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 532*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 536*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 540*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 544*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 548*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 552*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 556*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 560*/    if (child == this.first)
/* 561*/      return null; 
/* 563*/    Node n = this.first;
/* 564*/    while (n.next != child) {
/* 565*/      n = n.next;
/* 566*/      if (n == null)
/* 567*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 570*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 574*/    Node n = this.first;
/* 575*/    while (i > 0) {
/* 576*/      n = n.next;
/* 577*/      i--;
/*   0*/    } 
/* 579*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 583*/    Node n = this;
/* 584*/    while (n.next != null)
/* 585*/      n = n.next; 
/* 587*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 591*/    Preconditions.checkArgument((child.parent == null));
/* 592*/    Preconditions.checkArgument((child.next == null));
/* 593*/    child.parent = this;
/* 594*/    child.next = this.first;
/* 595*/    this.first = child;
/* 596*/    if (this.last == null)
/* 597*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 602*/    Preconditions.checkArgument((child.parent == null));
/* 603*/    Preconditions.checkArgument((child.next == null));
/* 604*/    child.parent = this;
/* 605*/    child.next = null;
/* 606*/    if (this.last == null) {
/* 607*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 610*/    this.last.next = child;
/* 611*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 615*/    for (Node child = children; child != null; child = child.next) {
/* 616*/      Preconditions.checkArgument((child.parent == null));
/* 617*/      child.parent = this;
/*   0*/    } 
/* 619*/    Node lastSib = children.getLastSibling();
/* 620*/    lastSib.next = this.first;
/* 621*/    this.first = children;
/* 622*/    if (this.last == null)
/* 623*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 628*/    for (Node child = children; child != null; child = child.next) {
/* 629*/      Preconditions.checkArgument((child.parent == null));
/* 630*/      child.parent = this;
/*   0*/    } 
/* 632*/    if (this.last != null)
/* 633*/      this.last.next = children; 
/* 635*/    this.last = children.getLastSibling();
/* 636*/    if (this.first == null)
/* 637*/      this.first = children; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 645*/    Preconditions.checkArgument((node != null), "The existing child node of the parent should not be null.");
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
/* 667*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 669*/    newChild.parent = this;
/* 670*/    newChild.next = node.next;
/* 671*/    node.next = newChild;
/* 672*/    if (this.last == node)
/* 673*/      this.last = newChild; 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 681*/    Node prev = getChildBefore(child);
/* 682*/    if (prev == null) {
/* 683*/      this.first = this.first.next;
/*   0*/    } else {
/* 685*/      prev.next = child.next;
/*   0*/    } 
/* 686*/    if (child == this.last)
/* 686*/      this.last = prev; 
/* 687*/    child.next = null;
/* 688*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 695*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 697*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 701*/    newChild.copyInformationFrom(child);
/* 703*/    newChild.next = child.next;
/* 704*/    newChild.parent = this;
/* 705*/    if (child == this.first) {
/* 706*/      this.first = newChild;
/*   0*/    } else {
/* 708*/      Node prev = getChildBefore(child);
/* 709*/      prev.next = newChild;
/*   0*/    } 
/* 711*/    if (child == this.last)
/* 712*/      this.last = newChild; 
/* 713*/    child.next = null;
/* 714*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 718*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 721*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 723*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 727*/    newChild.copyInformationFrom(prevChild);
/* 729*/    Node child = prevChild.next;
/* 730*/    newChild.next = child.next;
/* 731*/    newChild.parent = this;
/* 732*/    prevChild.next = newChild;
/* 733*/    if (child == this.last)
/* 734*/      this.last = newChild; 
/* 735*/    child.next = null;
/* 736*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 741*/    PropListItem x = this.propListHead;
/* 742*/    while (x != null && propType != x.type)
/* 743*/      x = x.next; 
/* 745*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 756*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 758*/    this.propListHead = other.propListHead;
/* 759*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 763*/    PropListItem result = removeProp(this.propListHead, propType);
/* 764*/    if (result != this.propListHead)
/* 765*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 776*/    if (item == null)
/* 777*/      return null; 
/* 778*/    if (item.type == propType)
/* 779*/      return item.next; 
/* 781*/    PropListItem result = removeProp(item.next, propType);
/* 782*/    if (result != item.next)
/* 783*/      return new PropListItem(item.type, item.intValue, item.objectValue, result); 
/* 786*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 792*/    PropListItem item = lookupProperty(propType);
/* 793*/    if (item == null)
/* 794*/      return null; 
/* 796*/    return item.objectValue;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 800*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 808*/    PropListItem item = lookupProperty(propType);
/* 809*/    if (item == null)
/* 810*/      return 0; 
/* 812*/    return item.intValue;
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 816*/    PropListItem item = lookupProperty(propType);
/* 817*/    if (item == null)
/* 818*/      Kit.codeBug(); 
/* 820*/    return item.intValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 824*/    removeProp(propType);
/* 825*/    if (value != null)
/* 826*/      this.propListHead = new PropListItem(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 831*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 835*/    removeProp(propType);
/* 836*/    if (value != 0)
/* 837*/      this.propListHead = new PropListItem(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 843*/    int count = 0;
/* 844*/    for (PropListItem x = this.propListHead; x != null; x = x.next)
/* 845*/      count++; 
/* 848*/    int[] keys = new int[count];
/* 849*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.next) {
/* 850*/      count--;
/* 851*/      keys[count] = propListItem1.type;
/*   0*/    } 
/* 854*/    Arrays.sort(keys);
/* 855*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/* 859*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/* 863*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 868*/    if (getType() == 39)
/* 869*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 872*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 878*/    if (getType() == 39)
/* 879*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 882*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 888*/    if (getType() == 40)
/* 889*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 892*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/* 898*/    if (getType() == 40)
/* 899*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 902*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 908*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 916*/    StringBuilder sb = new StringBuilder();
/* 917*/    toString(sb, printSource, printAnnotations, printType);
/* 918*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 929*/    sb.append(Token.name(this.type));
/* 930*/    if (this instanceof StringNode) {
/* 931*/      sb.append(' ');
/* 932*/      sb.append(getString());
/* 933*/    } else if (this.type == 105) {
/* 934*/      sb.append(' ');
/* 938*/      if (this.first.getType() == 40)
/* 939*/        sb.append(this.first.getString()); 
/* 941*/    } else if (this instanceof ScriptOrFnNode) {
/* 942*/      ScriptOrFnNode sof = (ScriptOrFnNode)this;
/* 943*/      if (this instanceof FunctionNode) {
/* 944*/        FunctionNode fn = (FunctionNode)this;
/* 945*/        sb.append(' ');
/* 946*/        sb.append(fn.getFunctionName());
/*   0*/      } 
/* 948*/      if (printSource) {
/* 949*/        sb.append(" [source name: ");
/* 950*/        sb.append(sof.getSourceName());
/* 951*/        sb.append("] [encoded source length: ");
/* 952*/        sb.append(sof.getEncodedSourceEnd() - sof.getEncodedSourceStart());
/* 953*/        sb.append("] [base line: ");
/* 954*/        sb.append(sof.getBaseLineno());
/* 955*/        sb.append("] [end line: ");
/* 956*/        sb.append(sof.getEndLineno());
/* 957*/        sb.append(']');
/*   0*/      } 
/* 959*/    } else if (this.type == 39) {
/* 960*/      sb.append(' ');
/* 961*/      sb.append(getDouble());
/*   0*/    } 
/* 963*/    if (printSource) {
/* 964*/      int lineno = getLineno();
/* 965*/      if (lineno != -1) {
/* 966*/        sb.append(' ');
/* 967*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/* 971*/    if (printAnnotations) {
/* 972*/      int[] keys = getSortedPropTypes();
/* 973*/      for (int i = 0; i < keys.length; i++) {
/*   0*/        String value;
/*   0*/        Object obj;
/* 974*/        int type = keys[i];
/* 975*/        PropListItem x = lookupProperty(type);
/* 976*/        sb.append(" [");
/* 977*/        sb.append(propToString(type));
/* 978*/        sb.append(": ");
/* 980*/        switch (type) {
/*   0*/          case 22:
/* 982*/            value = "target block property";
/*   0*/            break;
/*   0*/          case -3:
/* 985*/            value = "last local block";
/*   0*/            break;
/*   0*/          case 25:
/* 988*/            switch (x.intValue) {
/*   0*/              case 0:
/* 990*/                value = "both";
/*   0*/                break;
/*   0*/              case 2:
/* 993*/                value = "right";
/*   0*/                break;
/*   0*/              case 1:
/* 996*/                value = "left";
/*   0*/                break;
/*   0*/            } 
/* 999*/            throw Kit.codeBug();
/*   0*/          case 27:
/*1003*/            switch (x.intValue) {
/*   0*/              case 1:
/*1005*/                value = "eval";
/*   0*/                break;
/*   0*/              case 2:
/*1008*/                value = "with";
/*   0*/                break;
/*   0*/            } 
/*1012*/            throw Kit.codeBug();
/*   0*/          default:
/*1016*/            obj = x.objectValue;
/*1017*/            if (obj != null) {
/*1018*/              value = obj.toString();
/*   0*/              break;
/*   0*/            } 
/*1020*/            value = String.valueOf(x.intValue);
/*   0*/            break;
/*   0*/        } 
/*1024*/        sb.append(value);
/*1025*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/*1029*/    if (printType && 
/*1030*/      this.jsType != null) {
/*1031*/      String jsTypeString = this.jsType.toString();
/*1032*/      if (jsTypeString != null) {
/*1033*/        sb.append(" : ");
/*1034*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1043*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1048*/      StringBuilder s = new StringBuilder();
/*1049*/      appendStringTree(s);
/*1050*/      return s.toString();
/*1051*/    } catch (IOException e) {
/*1052*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1057*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1063*/    for (int i = 0; i != level; i++)
/*1064*/      sb.append("    "); 
/*1066*/    sb.append(n.toString());
/*1067*/    sb.append('\n');
/*1068*/    Node cursor = n.getFirstChild();
/*1069*/    for (; cursor != null; 
/*1070*/      cursor = cursor.getNext())
/*1071*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1129*/    int charno = getCharno();
/*1130*/    if (charno == -1)
/*1131*/      charno = 0; 
/*1133*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1137*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1147*/    if (lineno < 0 || charno < 0)
/*1148*/      return -1; 
/*1149*/    if ((charno & 0xFFFFF000) != 0)
/*1150*/      return lineno << 12 | 0xFFF; 
/*1152*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1161*/    if (lineCharNo == -1)
/*1162*/      return -1; 
/*1164*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1173*/    if (lineCharNo == -1)
/*1174*/      return -1; 
/*1176*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1193*/    if (this.first == null)
/*1194*/      return Collections.emptySet(); 
/*1196*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1210*/    return new SiblingNodeIterable(this);
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
/*1223*/      this.start = start;
/*1224*/      this.current = start;
/*1225*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1229*/      if (!this.used) {
/*1230*/        this.used = true;
/*1231*/        return this;
/*   0*/      } 
/*1239*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1244*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1248*/      if (this.current == null)
/*1249*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1252*/        return this.current;
/*   0*/      } finally {
/*1254*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1259*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1267*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1276*/    Preconditions.checkArgument((level >= 0));
/*1277*/    Node node = this;
/*1278*/    while (node != null && level-- > 0)
/*1279*/      node = node.getParent(); 
/*1281*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1288*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1301*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1305*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1307*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1311*/            if (!hasNext())
/*1311*/              throw new NoSuchElementException(); 
/*1312*/            Node n = Node.AncestorIterable.this.cur;
/*1313*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1314*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1318*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1331*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1341*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1345*/    int c = 0;
/*1346*/    for (Node n = this.first; n != null; n = n.next)
/*1347*/      c++; 
/*1349*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1354*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1355*/      if (child == n)
/*1356*/        return true; 
/*   0*/    } 
/*1359*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1367*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1368*/    if (diff != null)
/*1369*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1375*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static Class getNodeClass(Node n) {
/*1384*/    Class<?> c = n.getClass();
/*1385*/    if (c == FunctionNode.class || c == ScriptOrFnNode.class)
/*1386*/      return Node.class; 
/*1388*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1397*/    if (!isEquivalentTo(node2, false, false))
/*1398*/      return new NodeMismatch(this, node2); 
/*1401*/    NodeMismatch res = null;
/*1403*/    Node n = this.first, n2 = node2.first;
/*1404*/    while (res == null && n != null) {
/*1406*/      if (node2 == null)
/*1407*/        throw new IllegalStateException(); 
/*1409*/      res = n.checkTreeEqualsImpl(n2);
/*1411*/      return res;
/*   0*/    } 
/*1414*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1424*/    if (!isEquivalentTo(node2, true, false))
/*1425*/      return new NodeMismatch(this, node2); 
/*1428*/    NodeMismatch res = null;
/*1430*/    Node n = this.first, n2 = node2.first;
/*1431*/    for (; res == null && n != null; 
/*1432*/      n = n.next, n2 = n2.next) {
/*1433*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1434*/      if (res != null)
/*1435*/        return res; 
/*   0*/    } 
/*1438*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public static String tokenToName(int token) {
/*1442*/    switch (token) {
/*   0*/      case -1:
/*1443*/        return "error";
/*   0*/      case 0:
/*1444*/        return "eof";
/*   0*/      case 1:
/*1445*/        return "eol";
/*   0*/      case 2:
/*1446*/        return "enterwith";
/*   0*/      case 3:
/*1447*/        return "leavewith";
/*   0*/      case 4:
/*1448*/        return "return";
/*   0*/      case 5:
/*1449*/        return "goto";
/*   0*/      case 6:
/*1450*/        return "ifeq";
/*   0*/      case 7:
/*1451*/        return "ifne";
/*   0*/      case 8:
/*1452*/        return "setname";
/*   0*/      case 9:
/*1453*/        return "bitor";
/*   0*/      case 10:
/*1454*/        return "bitxor";
/*   0*/      case 11:
/*1455*/        return "bitand";
/*   0*/      case 12:
/*1456*/        return "eq";
/*   0*/      case 13:
/*1457*/        return "ne";
/*   0*/      case 14:
/*1458*/        return "lt";
/*   0*/      case 15:
/*1459*/        return "le";
/*   0*/      case 16:
/*1460*/        return "gt";
/*   0*/      case 17:
/*1461*/        return "ge";
/*   0*/      case 18:
/*1462*/        return "lsh";
/*   0*/      case 19:
/*1463*/        return "rsh";
/*   0*/      case 20:
/*1464*/        return "ursh";
/*   0*/      case 21:
/*1465*/        return "add";
/*   0*/      case 22:
/*1466*/        return "sub";
/*   0*/      case 23:
/*1467*/        return "mul";
/*   0*/      case 24:
/*1468*/        return "div";
/*   0*/      case 25:
/*1469*/        return "mod";
/*   0*/      case 27:
/*1470*/        return "bitnot";
/*   0*/      case 29:
/*1471*/        return "neg";
/*   0*/      case 30:
/*1472*/        return "new";
/*   0*/      case 31:
/*1473*/        return "delprop";
/*   0*/      case 32:
/*1474*/        return "typeof";
/*   0*/      case 33:
/*1475*/        return "getprop";
/*   0*/      case 34:
/*1476*/        return "setprop";
/*   0*/      case 35:
/*1477*/        return "getelem";
/*   0*/      case 36:
/*1478*/        return "setelem";
/*   0*/      case 37:
/*1479*/        return "call";
/*   0*/      case 38:
/*1480*/        return "name";
/*   0*/      case 39:
/*1481*/        return "number";
/*   0*/      case 40:
/*1482*/        return "string";
/*   0*/      case 41:
/*1483*/        return "null";
/*   0*/      case 42:
/*1484*/        return "this";
/*   0*/      case 43:
/*1485*/        return "false";
/*   0*/      case 44:
/*1486*/        return "true";
/*   0*/      case 45:
/*1487*/        return "sheq";
/*   0*/      case 46:
/*1488*/        return "shne";
/*   0*/      case 47:
/*1489*/        return "regexp";
/*   0*/      case 28:
/*1490*/        return "pos";
/*   0*/      case 48:
/*1491*/        return "bindname";
/*   0*/      case 49:
/*1492*/        return "throw";
/*   0*/      case 51:
/*1493*/        return "in";
/*   0*/      case 52:
/*1494*/        return "instanceof";
/*   0*/      case 54:
/*1495*/        return "getvar";
/*   0*/      case 55:
/*1496*/        return "setvar";
/*   0*/      case 77:
/*1497*/        return "try";
/*   0*/      case 133:
/*1498*/        return "typeofname";
/*   0*/      case 61:
/*1499*/        return "thisfn";
/*   0*/      case 78:
/*1500*/        return "semi";
/*   0*/      case 79:
/*1501*/        return "lb";
/*   0*/      case 80:
/*1502*/        return "rb";
/*   0*/      case 81:
/*1503*/        return "lc";
/*   0*/      case 82:
/*1504*/        return "rc";
/*   0*/      case 83:
/*1505*/        return "lp";
/*   0*/      case 84:
/*1506*/        return "rp";
/*   0*/      case 85:
/*1507*/        return "comma";
/*   0*/      case 86:
/*1508*/        return "assign";
/*   0*/      case 87:
/*1509*/        return "assign_bitor";
/*   0*/      case 88:
/*1510*/        return "assign_bitxor";
/*   0*/      case 89:
/*1511*/        return "assign_bitand";
/*   0*/      case 90:
/*1512*/        return "assign_lsh";
/*   0*/      case 91:
/*1513*/        return "assign_rsh";
/*   0*/      case 92:
/*1514*/        return "assign_ursh";
/*   0*/      case 93:
/*1515*/        return "assign_add";
/*   0*/      case 94:
/*1516*/        return "assign_sub";
/*   0*/      case 95:
/*1517*/        return "assign_mul";
/*   0*/      case 96:
/*1518*/        return "assign_div";
/*   0*/      case 97:
/*1519*/        return "assign_mod";
/*   0*/      case 98:
/*1520*/        return "hook";
/*   0*/      case 99:
/*1521*/        return "colon";
/*   0*/      case 100:
/*1522*/        return "or";
/*   0*/      case 101:
/*1523*/        return "and";
/*   0*/      case 102:
/*1524*/        return "inc";
/*   0*/      case 103:
/*1525*/        return "dec";
/*   0*/      case 104:
/*1526*/        return "dot";
/*   0*/      case 105:
/*1527*/        return "function";
/*   0*/      case 106:
/*1528*/        return "export";
/*   0*/      case 107:
/*1529*/        return "import";
/*   0*/      case 108:
/*1530*/        return "if";
/*   0*/      case 109:
/*1531*/        return "else";
/*   0*/      case 110:
/*1532*/        return "switch";
/*   0*/      case 111:
/*1533*/        return "case";
/*   0*/      case 112:
/*1534*/        return "default";
/*   0*/      case 113:
/*1535*/        return "while";
/*   0*/      case 114:
/*1536*/        return "do";
/*   0*/      case 115:
/*1537*/        return "for";
/*   0*/      case 116:
/*1538*/        return "break";
/*   0*/      case 117:
/*1539*/        return "continue";
/*   0*/      case 118:
/*1540*/        return "var";
/*   0*/      case 119:
/*1541*/        return "with";
/*   0*/      case 120:
/*1542*/        return "catch";
/*   0*/      case 121:
/*1543*/        return "finally";
/*   0*/      case 123:
/*1544*/        return "reserved";
/*   0*/      case 26:
/*1545*/        return "not";
/*   0*/      case 122:
/*1546*/        return "void";
/*   0*/      case 125:
/*1547*/        return "block";
/*   0*/      case 63:
/*1548*/        return "arraylit";
/*   0*/      case 64:
/*1549*/        return "objectlit";
/*   0*/      case 126:
/*1550*/        return "label";
/*   0*/      case 127:
/*1551*/        return "target";
/*   0*/      case 128:
/*1552*/        return "loop";
/*   0*/      case 129:
/*1553*/        return "expr_void";
/*   0*/      case 130:
/*1554*/        return "expr_result";
/*   0*/      case 131:
/*1555*/        return "jsr";
/*   0*/      case 132:
/*1556*/        return "script";
/*   0*/      case 124:
/*1557*/        return "empty";
/*   0*/      case 65:
/*1558*/        return "get_ref";
/*   0*/      case 69:
/*1559*/        return "ref_special";
/*   0*/    } 
/*1561*/    return "<unknown=" + token + ">";
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1566*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1574*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1584*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getNodeClass(this) != getNodeClass(node))
/*1587*/      return false; 
/*1590*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1591*/      return false; 
/*1594*/    if (this.type == 63) {
/*   0*/      try {
/*1596*/        int[] indices1 = (int[])getProp(30);
/*1597*/        int[] indices2 = (int[])node.getProp(30);
/*1598*/        if (indices1 == null) {
/*1599*/          if (indices2 != null)
/*1600*/            return false; 
/*   0*/        } else {
/*1602*/          if (indices2 == null)
/*1603*/            return false; 
/*1604*/          if (indices1.length != indices2.length)
/*1605*/            return false; 
/*1607*/          for (int i = 0; i < indices1.length; i++) {
/*1608*/            if (indices1[i] != indices2[i])
/*1609*/              return false; 
/*   0*/          } 
/*   0*/        } 
/*1613*/      } catch (Exception e) {
/*1614*/        return false;
/*   0*/      } 
/*1616*/    } else if (this.type == 102 || this.type == 103) {
/*1617*/      int post1 = getIntProp(31);
/*1618*/      int post2 = node.getIntProp(31);
/*1619*/      if (post1 != post2)
/*1620*/        return false; 
/*1622*/    } else if (this.type == 40) {
/*1623*/      int quoted1 = getIntProp(35);
/*1624*/      int quoted2 = node.getIntProp(35);
/*1625*/      if (quoted1 != quoted2)
/*1626*/        return false; 
/*   0*/    } 
/*1630*/    if (recurse) {
/*1632*/      Node n = this.first, n2 = node.first;
/*1633*/      for (; n != null; 
/*1634*/        n = n.next, n2 = n2.next) {
/*1635*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1636*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1641*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasSideEffects() {
/*1645*/    switch (this.type) {
/*   0*/      case 85:
/*   0*/      case 129:
/*1648*/        if (this.last != null)
/*1649*/          return this.last.hasSideEffects(); 
/*1651*/        return true;
/*   0*/      case 98:
/*1654*/        if (this.first == null || this.first.next == null || this.first.next.next == null)
/*1655*/          Kit.codeBug(); 
/*1657*/        return (this.first.next.hasSideEffects() && this.first.next.next.hasSideEffects());
/*   0*/      case -1:
/*   0*/      case 2:
/*   0*/      case 3:
/*   0*/      case 4:
/*   0*/      case 5:
/*   0*/      case 6:
/*   0*/      case 7:
/*   0*/      case 8:
/*   0*/      case 30:
/*   0*/      case 31:
/*   0*/      case 34:
/*   0*/      case 36:
/*   0*/      case 37:
/*   0*/      case 49:
/*   0*/      case 50:
/*   0*/      case 55:
/*   0*/      case 56:
/*   0*/      case 62:
/*   0*/      case 66:
/*   0*/      case 67:
/*   0*/      case 68:
/*   0*/      case 77:
/*   0*/      case 78:
/*   0*/      case 86:
/*   0*/      case 87:
/*   0*/      case 88:
/*   0*/      case 89:
/*   0*/      case 90:
/*   0*/      case 91:
/*   0*/      case 92:
/*   0*/      case 93:
/*   0*/      case 94:
/*   0*/      case 95:
/*   0*/      case 96:
/*   0*/      case 97:
/*   0*/      case 102:
/*   0*/      case 103:
/*   0*/      case 106:
/*   0*/      case 107:
/*   0*/      case 108:
/*   0*/      case 109:
/*   0*/      case 110:
/*   0*/      case 113:
/*   0*/      case 114:
/*   0*/      case 115:
/*   0*/      case 116:
/*   0*/      case 117:
/*   0*/      case 118:
/*   0*/      case 119:
/*   0*/      case 120:
/*   0*/      case 121:
/*   0*/      case 125:
/*   0*/      case 126:
/*   0*/      case 127:
/*   0*/      case 128:
/*   0*/      case 130:
/*   0*/      case 131:
/*   0*/      case 135:
/*   0*/      case 136:
/*   0*/      case 137:
/*   0*/      case 138:
/*   0*/      case 149:
/*1721*/        return true;
/*   0*/    } 
/*1724*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1737*/    if (this.type == 38)
/*1738*/      return getString(); 
/*1739*/    if (this.type == 33) {
/*1740*/      String left = getFirstChild().getQualifiedName();
/*1741*/      if (left == null)
/*1742*/        return null; 
/*1744*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1745*/    if (this.type == 42)
/*1746*/      return "this"; 
/*1748*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1757*/    switch (getType()) {
/*   0*/      case 38:
/*   0*/      case 42:
/*1760*/        return true;
/*   0*/      case 33:
/*1762*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1764*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1774*/    switch (getType()) {
/*   0*/      case 38:
/*1776*/        return true;
/*   0*/      case 33:
/*1778*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1780*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1792*/    Preconditions.checkState((this.parent != null));
/*1793*/    this.parent.removeChild(this);
/*1794*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1804*/    Node child = this.first;
/*1805*/    if (child != null)
/*1806*/      removeChild(child); 
/*1808*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1815*/    Node children = this.first;
/*1816*/    for (Node child = this.first; child != null; child = child.getNext())
/*1817*/      child.parent = null; 
/*1819*/    this.first = null;
/*1820*/    this.last = null;
/*1821*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1829*/    for (Node child = this.first; child != null; ) {
/*1830*/      Node nextChild = child.getNext();
/*1831*/      child.parent = null;
/*1832*/      child.next = null;
/*1833*/      child = nextChild;
/*   0*/    } 
/*1835*/    this.first = null;
/*1836*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1840*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1842*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1845*/    Node child = prev.next;
/*1846*/    prev.next = child.next;
/*1847*/    if (child == this.last)
/*1847*/      this.last = prev; 
/*1848*/    child.next = null;
/*1849*/    child.parent = null;
/*1850*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1859*/      result = (Node)clone();
/*1862*/      result.next = null;
/*1863*/      result.first = null;
/*1864*/      result.last = null;
/*1865*/      result.parent = null;
/*1866*/    } catch (CloneNotSupportedException e) {
/*1867*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1869*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1876*/    Node result = cloneNode();
/*1877*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1878*/      Node n2clone = n2.cloneTree();
/*1879*/      n2clone.parent = result;
/*1880*/      if (result.last != null)
/*1881*/        result.last.next = n2clone; 
/*1883*/      if (result.first == null)
/*1884*/        result.first = n2clone; 
/*1886*/      result.last = n2clone;
/*   0*/    } 
/*1888*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1898*/    if (getProp(39) == null)
/*1899*/      putProp(39, other.getProp(39)); 
/*1902*/    if (getProp(16) == null) {
/*1903*/      putProp(16, other.getProp(16));
/*1904*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1907*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1916*/    copyInformationFrom(other);
/*1917*/    Node child = getFirstChild();
/*1918*/    for (; child != null; child = child.getNext())
/*1919*/      child.copyInformationFromForTree(other); 
/*1922*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1929*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1933*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1937*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1950*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1951*/      if (jsDocInfo == null)
/*1954*/        jsDocInfo = new JSDocInfo(false); 
/*1956*/      String license = jsDocInfo.getLicense();
/*1957*/      if (license == null)
/*1958*/        license = ""; 
/*1960*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1961*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1971*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*1978*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*1987*/    putBooleanProp(29, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*1996*/    return getBooleanProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*2005*/    putBooleanProp(36, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*2014*/    return getBooleanProp(36);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*2022*/    putBooleanProp(37, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*2030*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*2037*/    putProp(47, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*2045*/    return (Set<String>)getProp(47);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*2053*/    if (getJSDocInfo() == null)
/*2054*/      setJSDocInfo(new JSDocInfo(false)); 
/*2056*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*2064*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*2072*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2105*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2110*/    putIntProp(41, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2114*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2121*/    return getIntProp(41);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2129*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2135*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2139*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2144*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2149*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2153*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2161*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2166*/      removeFlag(1);
/*2167*/      removeFlag(4);
/*2168*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2172*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2176*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2180*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2184*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2188*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2196*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2208*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2217*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2224*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2231*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2238*/    Kit.codeBug();
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2246*/      this.nodeA = nodeA;
/*2247*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2252*/      if (object instanceof NodeMismatch) {
/*2253*/        NodeMismatch that = (NodeMismatch)object;
/*2254*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2256*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2261*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/}
