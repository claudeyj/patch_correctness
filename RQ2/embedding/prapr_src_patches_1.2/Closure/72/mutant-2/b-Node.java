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
/*   0*/  public static final int VAR_ARGS_NAME = 30;
/*   0*/  
/*   0*/  public static final int SKIP_INDEXES_PROP = 31;
/*   0*/  
/*   0*/  public static final int INCRDECR_PROP = 32;
/*   0*/  
/*   0*/  public static final int MEMBER_TYPE_PROP = 33;
/*   0*/  
/*   0*/  public static final int NAME_PROP = 34;
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
/*   0*/  public static final int LAST_PROP = 50;
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
/* 173*/    switch (propType) {
/*   0*/      case -3:
/* 174*/        return "local_block";
/*   0*/      case -2:
/* 175*/        return "object_ids_prop";
/*   0*/      case -1:
/* 176*/        return "catch_scope_prop";
/*   0*/      case 0:
/* 177*/        return "label_id_prop";
/*   0*/      case 1:
/* 178*/        return "target";
/*   0*/      case 2:
/* 179*/        return "break";
/*   0*/      case 3:
/* 180*/        return "continue";
/*   0*/      case 4:
/* 181*/        return "enum";
/*   0*/      case 5:
/* 182*/        return "function";
/*   0*/      case 6:
/* 183*/        return "temp";
/*   0*/      case 7:
/* 184*/        return "local";
/*   0*/      case 8:
/* 185*/        return "codeoffset";
/*   0*/      case 9:
/* 186*/        return "fixups";
/*   0*/      case 10:
/* 187*/        return "vars";
/*   0*/      case 11:
/* 188*/        return "uses";
/*   0*/      case 12:
/* 189*/        return "regexp";
/*   0*/      case 13:
/* 190*/        return "cases";
/*   0*/      case 14:
/* 191*/        return "default";
/*   0*/      case 15:
/* 192*/        return "casearray";
/*   0*/      case 16:
/* 193*/        return "sourcename";
/*   0*/      case 17:
/* 194*/        return "type";
/*   0*/      case 18:
/* 195*/        return "special_prop";
/*   0*/      case 19:
/* 196*/        return "label";
/*   0*/      case 20:
/* 197*/        return "finally";
/*   0*/      case 21:
/* 198*/        return "localcount";
/*   0*/      case 22:
/* 200*/        return "targetblock";
/*   0*/      case 23:
/* 201*/        return "variable";
/*   0*/      case 24:
/* 202*/        return "lastuse";
/*   0*/      case 25:
/* 203*/        return "isnumber";
/*   0*/      case 26:
/* 204*/        return "directcall";
/*   0*/      case 27:
/* 206*/        return "specialcall";
/*   0*/      case 28:
/* 207*/        return "debugsource";
/*   0*/      case 29:
/* 209*/        return "jsdoc_info";
/*   0*/      case 31:
/* 211*/        return "skip_indexes";
/*   0*/      case 32:
/* 212*/        return "incrdecr";
/*   0*/      case 33:
/* 213*/        return "member_type";
/*   0*/      case 34:
/* 214*/        return "name";
/*   0*/      case 35:
/* 215*/        return "parenthesized";
/*   0*/      case 36:
/* 216*/        return "quoted";
/*   0*/      case 38:
/* 218*/        return "synthetic";
/*   0*/      case 39:
/* 219*/        return "empty_block";
/*   0*/      case 40:
/* 220*/        return "originalname";
/*   0*/      case 42:
/* 221*/        return "side_effect_flags";
/*   0*/      case 43:
/* 223*/        return "is_constant_name";
/*   0*/      case 44:
/* 224*/        return "is_optional_param";
/*   0*/      case 45:
/* 225*/        return "is_var_args_param";
/*   0*/      case 46:
/* 226*/        return "is_namespace";
/*   0*/      case 47:
/* 227*/        return "is_dispatcher";
/*   0*/      case 48:
/* 228*/        return "directives";
/*   0*/      case 49:
/* 229*/        return "direct_eval";
/*   0*/      case 50:
/* 230*/        return "free_call";
/*   0*/    } 
/* 232*/    Kit.codeBug();
/* 234*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 242*/      super(39);
/* 243*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 247*/      super(39, lineno, charno);
/* 248*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 253*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 258*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 263*/      return (super.isEquivalentTo(node, compareJsType, recurse) && getDouble() == ((NumberNode)node).getDouble());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 275*/      super(type);
/* 276*/      if (null == str)
/* 277*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 279*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 283*/      super(type, lineno, charno);
/* 284*/      if (null == str)
/* 285*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 287*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 296*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 305*/      if (null == str)
/* 306*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 308*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 313*/      return (super.isEquivalentTo(node, compareJsType, recurse) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 325*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 333*/      putBooleanProp(36, true);
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
/* 349*/      this(type, intValue, null, next);
/*   0*/    }
/*   0*/    
/*   0*/    PropListItem(int type, Object objectValue, PropListItem next) {
/* 353*/      this(type, 0, objectValue, next);
/*   0*/    }
/*   0*/    
/*   0*/    PropListItem(int type, int intValue, Object objectValue, PropListItem next) {
/* 358*/      this.type = type;
/* 359*/      this.intValue = intValue;
/* 360*/      this.objectValue = objectValue;
/* 361*/      this.next = next;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 367*/    this.type = nodeType;
/* 368*/    this.parent = null;
/* 369*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 373*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 375*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 378*/    this.type = nodeType;
/* 379*/    this.parent = null;
/* 380*/    this.first = this.last = child;
/* 381*/    child.next = null;
/* 382*/    child.parent = this;
/* 383*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 387*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 389*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 391*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 393*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 395*/    this.type = nodeType;
/* 396*/    this.parent = null;
/* 397*/    this.first = left;
/* 398*/    this.last = right;
/* 399*/    left.next = right;
/* 400*/    left.parent = this;
/* 401*/    right.next = null;
/* 402*/    right.parent = this;
/* 403*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 407*/    Preconditions.checkArgument((left.parent == null));
/* 408*/    Preconditions.checkArgument((left.next == null));
/* 409*/    Preconditions.checkArgument((mid.parent == null));
/* 410*/    Preconditions.checkArgument((mid.next == null));
/* 411*/    Preconditions.checkArgument((right.parent == null));
/* 412*/    Preconditions.checkArgument((right.next == null));
/* 413*/    this.type = nodeType;
/* 414*/    this.parent = null;
/* 415*/    this.first = left;
/* 416*/    this.last = right;
/* 417*/    left.next = mid;
/* 418*/    left.parent = this;
/* 419*/    mid.next = right;
/* 420*/    mid.parent = this;
/* 421*/    right.next = null;
/* 422*/    right.parent = this;
/* 423*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 427*/    Preconditions.checkArgument((left.parent == null));
/* 428*/    Preconditions.checkArgument((left.next == null));
/* 429*/    Preconditions.checkArgument((mid.parent == null));
/* 430*/    Preconditions.checkArgument((mid.next == null));
/* 431*/    Preconditions.checkArgument((mid2.parent == null));
/* 432*/    Preconditions.checkArgument((mid2.next == null));
/* 433*/    Preconditions.checkArgument((right.parent == null));
/* 434*/    Preconditions.checkArgument((right.next == null));
/* 435*/    this.type = nodeType;
/* 436*/    this.parent = null;
/* 437*/    this.first = left;
/* 438*/    this.last = right;
/* 439*/    left.next = mid;
/* 440*/    left.parent = this;
/* 441*/    mid.next = mid2;
/* 442*/    mid.parent = this;
/* 443*/    mid2.next = right;
/* 444*/    mid2.parent = this;
/* 445*/    right.next = null;
/* 446*/    right.parent = this;
/* 447*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 451*/    this.type = nodeType;
/* 452*/    this.parent = null;
/* 453*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 457*/    this(nodeType, child);
/* 458*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 462*/    this(nodeType, left, right);
/* 463*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 468*/    this(nodeType, left, mid, right);
/* 469*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 474*/    this(nodeType, left, mid, mid2, right);
/* 475*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 479*/    this(nodeType, children);
/* 480*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 484*/    this.type = nodeType;
/* 485*/    this.parent = null;
/* 486*/    if (children.length != 0) {
/* 487*/      this.first = children[0];
/* 488*/      this.last = children[children.length - 1];
/* 490*/      for (int i = 1; i < children.length; i++) {
/* 491*/        if (null != (children[i - 1]).next)
/* 493*/          throw new IllegalArgumentException("duplicate child"); 
/* 495*/        (children[i - 1]).next = children[i];
/* 496*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 497*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 499*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 500*/      (children[children.length - 1]).parent = this;
/* 502*/      if (null != this.last.next)
/* 504*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 510*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 514*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 518*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 522*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 526*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 530*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 534*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 538*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 542*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 546*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 550*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 554*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 558*/    if (child == this.first)
/* 559*/      return null; 
/* 561*/    Node n = this.first;
/* 562*/    while (n.next != child) {
/* 563*/      n = n.next;
/* 564*/      if (n == null)
/* 565*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 568*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 572*/    Node n = this.first;
/* 573*/    while (i > 0) {
/* 574*/      n = n.next;
/* 575*/      i--;
/*   0*/    } 
/* 577*/    return n;
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
/* 740*/    while (x != null && propType != x.type)
/* 741*/      x = x.next; 
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
/* 776*/    if (item.type == propType)
/* 777*/      return item.next; 
/* 779*/    PropListItem result = removeProp(item.next, propType);
/* 780*/    if (result != item.next)
/* 781*/      return new PropListItem(item.type, item.intValue, item.objectValue, result); 
/* 784*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 790*/    PropListItem item = lookupProperty(propType);
/* 791*/    if (item == null)
/* 792*/      return null; 
/* 794*/    return item.objectValue;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 798*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 806*/    PropListItem item = lookupProperty(propType);
/* 807*/    if (item == null)
/* 808*/      return 0; 
/* 810*/    return item.intValue;
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 814*/    PropListItem item = lookupProperty(propType);
/* 815*/    if (item == null)
/* 816*/      Kit.codeBug(); 
/* 818*/    return item.intValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 822*/    removeProp(propType);
/* 823*/    if (value != null)
/* 824*/      this.propListHead = new PropListItem(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 829*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 833*/    removeProp(propType);
/* 834*/    if (value != 0)
/* 835*/      this.propListHead = new PropListItem(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 841*/    int count = 0;
/* 842*/    for (PropListItem x = this.propListHead; x != null; x = x.next)
/* 843*/      count++; 
/* 846*/    int[] keys = new int[count];
/* 847*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.next) {
/* 848*/      count--;
/* 849*/      keys[count] = propListItem1.type;
/*   0*/    } 
/* 852*/    Arrays.sort(keys);
/* 853*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/* 857*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/* 861*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/* 865*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 870*/    if (getType() == 39)
/* 871*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 874*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 880*/    if (getType() == 39)
/* 881*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 884*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 890*/    if (getType() == 40)
/* 891*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 894*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/* 900*/    if (getType() == 40)
/* 901*/      throw new IllegalStateException("String node not created with Node.newString"); 
/* 904*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 910*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/* 918*/    StringBuilder sb = new StringBuilder();
/* 919*/    toString(sb, printSource, printAnnotations, printType);
/* 920*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/* 931*/    sb.append(Token.name(this.type));
/* 932*/    if (this instanceof StringNode) {
/* 933*/      sb.append(' ');
/* 934*/      sb.append(getString());
/* 935*/    } else if (this.type == 105) {
/* 936*/      sb.append(' ');
/* 940*/      if (this.first == null || this.first.getType() != 38) {
/* 941*/        sb.append("<invalid>");
/*   0*/      } else {
/* 943*/        sb.append(this.first.getString());
/*   0*/      } 
/* 945*/    } else if (this instanceof ScriptOrFnNode) {
/* 946*/      ScriptOrFnNode sof = (ScriptOrFnNode)this;
/* 947*/      if (this instanceof FunctionNode) {
/* 948*/        FunctionNode fn = (FunctionNode)this;
/* 949*/        sb.append(' ');
/* 950*/        sb.append(fn.getFunctionName());
/*   0*/      } 
/* 952*/      if (printSource) {
/* 953*/        sb.append(" [source name: ");
/* 954*/        sb.append(sof.getSourceName());
/* 955*/        sb.append("] [encoded source length: ");
/* 956*/        sb.append(sof.getEncodedSourceEnd() - sof.getEncodedSourceStart());
/* 957*/        sb.append("] [base line: ");
/* 958*/        sb.append(sof.getBaseLineno());
/* 959*/        sb.append("] [end line: ");
/* 960*/        sb.append(sof.getEndLineno());
/* 961*/        sb.append(']');
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
/*   0*/        String value;
/*   0*/        Object obj;
/* 978*/        int type = keys[i];
/* 979*/        PropListItem x = lookupProperty(type);
/* 980*/        sb.append(" [");
/* 981*/        sb.append(propToString(type));
/* 982*/        sb.append(": ");
/* 984*/        switch (type) {
/*   0*/          case 22:
/* 986*/            value = "target block property";
/*   0*/            break;
/*   0*/          case -3:
/* 989*/            value = "last local block";
/*   0*/            break;
/*   0*/          case 25:
/* 992*/            switch (x.intValue) {
/*   0*/              case 0:
/* 994*/                value = "both";
/*   0*/                break;
/*   0*/              case 2:
/* 997*/                value = "right";
/*   0*/                break;
/*   0*/              case 1:
/*1000*/                value = "left";
/*   0*/                break;
/*   0*/            } 
/*1003*/            throw Kit.codeBug();
/*   0*/          case 27:
/*1007*/            switch (x.intValue) {
/*   0*/              case 1:
/*1009*/                value = "eval";
/*   0*/                break;
/*   0*/              case 2:
/*1012*/                value = "with";
/*   0*/                break;
/*   0*/            } 
/*1016*/            throw Kit.codeBug();
/*   0*/          default:
/*1020*/            obj = x.objectValue;
/*1021*/            if (obj != null) {
/*1022*/              value = obj.toString();
/*   0*/              break;
/*   0*/            } 
/*1024*/            value = String.valueOf(x.intValue);
/*   0*/            break;
/*   0*/        } 
/*1028*/        sb.append(value);
/*1029*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/*1033*/    if (printType && 
/*1034*/      this.jsType != null) {
/*1035*/      String jsTypeString = this.jsType.toString();
/*1036*/      if (jsTypeString != null) {
/*1037*/        sb.append(" : ");
/*1038*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1047*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1052*/      StringBuilder s = new StringBuilder();
/*1053*/      appendStringTree(s);
/*1054*/      return s.toString();
/*1055*/    } catch (IOException e) {
/*1056*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1061*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1067*/    for (int i = 0; i != level; i++)
/*1068*/      sb.append("    "); 
/*1070*/    sb.append(n.toString());
/*1071*/    sb.append('\n');
/*1072*/    Node cursor = n.getFirstChild();
/*1073*/    for (; cursor != null; 
/*1074*/      cursor = cursor.getNext())
/*1075*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1133*/    int charno = getCharno();
/*1134*/    if (charno == -1)
/*1135*/      charno = 0; 
/*1137*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1141*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourcePositionForTree(int sourcePosition) {
/*1145*/    this.sourcePosition = sourcePosition;
/*1147*/    Node child = getFirstChild();
/*1148*/    for (; child != null; child = child.getNext())
/*1149*/      child.setSourcePositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1160*/    if (lineno < 0 || charno < 0)
/*1161*/      return -1; 
/*1162*/    if ((charno & 0xFFFFF000) != 0)
/*1163*/      return lineno << 12 | 0xFFF; 
/*1165*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1174*/    if (lineCharNo == -1)
/*1175*/      return -1; 
/*1177*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1186*/    if (lineCharNo == -1)
/*1187*/      return -1; 
/*1189*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1206*/    if (this.first == null)
/*1207*/      return Collections.emptySet(); 
/*1209*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1223*/    return new SiblingNodeIterable(this);
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
/*1236*/      this.start = start;
/*1237*/      this.current = start;
/*1238*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1242*/      if (!this.used) {
/*1243*/        this.used = true;
/*1244*/        return this;
/*   0*/      } 
/*1252*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1257*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1261*/      if (this.current == null)
/*1262*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1265*/        return this.current;
/*   0*/      } finally {
/*1267*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1272*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1280*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1289*/    Preconditions.checkArgument((level >= 0));
/*1290*/    Node node = this;
/*1291*/    while (node != null && level-- > 0)
/*1292*/      node = node.getParent(); 
/*1294*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1301*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1314*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1318*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1320*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1324*/            if (!hasNext())
/*1324*/              throw new NoSuchElementException(); 
/*1325*/            Node n = Node.AncestorIterable.this.cur;
/*1326*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1327*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1331*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1344*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1354*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1358*/    int c = 0;
/*1359*/    for (Node n = this.first; n != null; n = n.next)
/*1360*/      c++; 
/*1362*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1367*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1368*/      if (child == n)
/*1369*/        return true; 
/*   0*/    } 
/*1372*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1380*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1381*/    if (diff != null)
/*1382*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1388*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static Class getNodeClass(Node n) {
/*1397*/    Class<?> c = n.getClass();
/*1398*/    if (c == FunctionNode.class || c == ScriptOrFnNode.class)
/*1399*/      return Node.class; 
/*1401*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1410*/    if (!isEquivalentTo(node2, false, false))
/*1411*/      return new NodeMismatch(this, node2); 
/*1414*/    NodeMismatch res = null;
/*1416*/    Node n = this.first, n2 = node2.first;
/*1417*/    for (; res == null && n != null; 
/*1418*/      n = n2.next, n2 = n2.next) {
/*1419*/      if (node2 == null)
/*1420*/        throw new IllegalStateException(); 
/*1422*/      res = n.checkTreeEqualsImpl(n2);
/*1423*/      if (res != null)
/*1424*/        return res; 
/*   0*/    } 
/*1427*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1437*/    if (!isEquivalentTo(node2, true, false))
/*1438*/      return new NodeMismatch(this, node2); 
/*1441*/    NodeMismatch res = null;
/*1443*/    Node n = this.first, n2 = node2.first;
/*1444*/    for (; res == null && n != null; 
/*1445*/      n = n.next, n2 = n2.next) {
/*1446*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1447*/      if (res != null)
/*1448*/        return res; 
/*   0*/    } 
/*1451*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public static String tokenToName(int token) {
/*1455*/    switch (token) {
/*   0*/      case -1:
/*1456*/        return "error";
/*   0*/      case 0:
/*1457*/        return "eof";
/*   0*/      case 1:
/*1458*/        return "eol";
/*   0*/      case 2:
/*1459*/        return "enterwith";
/*   0*/      case 3:
/*1460*/        return "leavewith";
/*   0*/      case 4:
/*1461*/        return "return";
/*   0*/      case 5:
/*1462*/        return "goto";
/*   0*/      case 6:
/*1463*/        return "ifeq";
/*   0*/      case 7:
/*1464*/        return "ifne";
/*   0*/      case 8:
/*1465*/        return "setname";
/*   0*/      case 9:
/*1466*/        return "bitor";
/*   0*/      case 10:
/*1467*/        return "bitxor";
/*   0*/      case 11:
/*1468*/        return "bitand";
/*   0*/      case 12:
/*1469*/        return "eq";
/*   0*/      case 13:
/*1470*/        return "ne";
/*   0*/      case 14:
/*1471*/        return "lt";
/*   0*/      case 15:
/*1472*/        return "le";
/*   0*/      case 16:
/*1473*/        return "gt";
/*   0*/      case 17:
/*1474*/        return "ge";
/*   0*/      case 18:
/*1475*/        return "lsh";
/*   0*/      case 19:
/*1476*/        return "rsh";
/*   0*/      case 20:
/*1477*/        return "ursh";
/*   0*/      case 21:
/*1478*/        return "add";
/*   0*/      case 22:
/*1479*/        return "sub";
/*   0*/      case 23:
/*1480*/        return "mul";
/*   0*/      case 24:
/*1481*/        return "div";
/*   0*/      case 25:
/*1482*/        return "mod";
/*   0*/      case 27:
/*1483*/        return "bitnot";
/*   0*/      case 29:
/*1484*/        return "neg";
/*   0*/      case 30:
/*1485*/        return "new";
/*   0*/      case 31:
/*1486*/        return "delprop";
/*   0*/      case 32:
/*1487*/        return "typeof";
/*   0*/      case 33:
/*1488*/        return "getprop";
/*   0*/      case 34:
/*1489*/        return "setprop";
/*   0*/      case 35:
/*1490*/        return "getelem";
/*   0*/      case 36:
/*1491*/        return "setelem";
/*   0*/      case 37:
/*1492*/        return "call";
/*   0*/      case 38:
/*1493*/        return "name";
/*   0*/      case 39:
/*1494*/        return "number";
/*   0*/      case 40:
/*1495*/        return "string";
/*   0*/      case 41:
/*1496*/        return "null";
/*   0*/      case 42:
/*1497*/        return "this";
/*   0*/      case 43:
/*1498*/        return "false";
/*   0*/      case 44:
/*1499*/        return "true";
/*   0*/      case 45:
/*1500*/        return "sheq";
/*   0*/      case 46:
/*1501*/        return "shne";
/*   0*/      case 47:
/*1502*/        return "regexp";
/*   0*/      case 28:
/*1503*/        return "pos";
/*   0*/      case 48:
/*1504*/        return "bindname";
/*   0*/      case 49:
/*1505*/        return "throw";
/*   0*/      case 51:
/*1506*/        return "in";
/*   0*/      case 52:
/*1507*/        return "instanceof";
/*   0*/      case 54:
/*1508*/        return "getvar";
/*   0*/      case 55:
/*1509*/        return "setvar";
/*   0*/      case 77:
/*1510*/        return "try";
/*   0*/      case 133:
/*1511*/        return "typeofname";
/*   0*/      case 61:
/*1512*/        return "thisfn";
/*   0*/      case 78:
/*1513*/        return "semi";
/*   0*/      case 79:
/*1514*/        return "lb";
/*   0*/      case 80:
/*1515*/        return "rb";
/*   0*/      case 81:
/*1516*/        return "lc";
/*   0*/      case 82:
/*1517*/        return "rc";
/*   0*/      case 83:
/*1518*/        return "lp";
/*   0*/      case 84:
/*1519*/        return "rp";
/*   0*/      case 85:
/*1520*/        return "comma";
/*   0*/      case 86:
/*1521*/        return "assign";
/*   0*/      case 87:
/*1522*/        return "assign_bitor";
/*   0*/      case 88:
/*1523*/        return "assign_bitxor";
/*   0*/      case 89:
/*1524*/        return "assign_bitand";
/*   0*/      case 90:
/*1525*/        return "assign_lsh";
/*   0*/      case 91:
/*1526*/        return "assign_rsh";
/*   0*/      case 92:
/*1527*/        return "assign_ursh";
/*   0*/      case 93:
/*1528*/        return "assign_add";
/*   0*/      case 94:
/*1529*/        return "assign_sub";
/*   0*/      case 95:
/*1530*/        return "assign_mul";
/*   0*/      case 96:
/*1531*/        return "assign_div";
/*   0*/      case 97:
/*1532*/        return "assign_mod";
/*   0*/      case 98:
/*1533*/        return "hook";
/*   0*/      case 99:
/*1534*/        return "colon";
/*   0*/      case 100:
/*1535*/        return "or";
/*   0*/      case 101:
/*1536*/        return "and";
/*   0*/      case 102:
/*1537*/        return "inc";
/*   0*/      case 103:
/*1538*/        return "dec";
/*   0*/      case 104:
/*1539*/        return "dot";
/*   0*/      case 105:
/*1540*/        return "function";
/*   0*/      case 106:
/*1541*/        return "export";
/*   0*/      case 107:
/*1542*/        return "import";
/*   0*/      case 108:
/*1543*/        return "if";
/*   0*/      case 109:
/*1544*/        return "else";
/*   0*/      case 110:
/*1545*/        return "switch";
/*   0*/      case 111:
/*1546*/        return "case";
/*   0*/      case 112:
/*1547*/        return "default";
/*   0*/      case 113:
/*1548*/        return "while";
/*   0*/      case 114:
/*1549*/        return "do";
/*   0*/      case 115:
/*1550*/        return "for";
/*   0*/      case 116:
/*1551*/        return "break";
/*   0*/      case 117:
/*1552*/        return "continue";
/*   0*/      case 118:
/*1553*/        return "var";
/*   0*/      case 119:
/*1554*/        return "with";
/*   0*/      case 120:
/*1555*/        return "catch";
/*   0*/      case 121:
/*1556*/        return "finally";
/*   0*/      case 123:
/*1557*/        return "reserved";
/*   0*/      case 26:
/*1558*/        return "not";
/*   0*/      case 122:
/*1559*/        return "void";
/*   0*/      case 125:
/*1560*/        return "block";
/*   0*/      case 63:
/*1561*/        return "arraylit";
/*   0*/      case 64:
/*1562*/        return "objectlit";
/*   0*/      case 126:
/*1563*/        return "label";
/*   0*/      case 127:
/*1564*/        return "target";
/*   0*/      case 128:
/*1565*/        return "loop";
/*   0*/      case 129:
/*1566*/        return "expr_void";
/*   0*/      case 130:
/*1567*/        return "expr_result";
/*   0*/      case 131:
/*1568*/        return "jsr";
/*   0*/      case 132:
/*1569*/        return "script";
/*   0*/      case 124:
/*1570*/        return "empty";
/*   0*/      case 65:
/*1571*/        return "get_ref";
/*   0*/      case 69:
/*1572*/        return "ref_special";
/*   0*/    } 
/*1574*/    return "<unknown=" + token + ">";
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1579*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1587*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1597*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getNodeClass(this) != getNodeClass(node))
/*1600*/      return false; 
/*1603*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1604*/      return false; 
/*1607*/    if (this.type == 63) {
/*   0*/      try {
/*1609*/        int[] indices1 = (int[])getProp(31);
/*1610*/        int[] indices2 = (int[])node.getProp(31);
/*1611*/        if (indices1 == null) {
/*1612*/          if (indices2 != null)
/*1613*/            return false; 
/*   0*/        } else {
/*1615*/          if (indices2 == null)
/*1616*/            return false; 
/*1617*/          if (indices1.length != indices2.length)
/*1618*/            return false; 
/*1620*/          for (int i = 0; i < indices1.length; i++) {
/*1621*/            if (indices1[i] != indices2[i])
/*1622*/              return false; 
/*   0*/          } 
/*   0*/        } 
/*1626*/      } catch (Exception e) {
/*1627*/        return false;
/*   0*/      } 
/*1629*/    } else if (this.type == 102 || this.type == 103) {
/*1630*/      int post1 = getIntProp(32);
/*1631*/      int post2 = node.getIntProp(32);
/*1632*/      if (post1 != post2)
/*1633*/        return false; 
/*1635*/    } else if (this.type == 40) {
/*1636*/      int quoted1 = getIntProp(36);
/*1637*/      int quoted2 = node.getIntProp(36);
/*1638*/      if (quoted1 != quoted2)
/*1639*/        return false; 
/*   0*/    } 
/*1643*/    if (recurse) {
/*1645*/      Node n = this.first, n2 = node.first;
/*1646*/      for (; n != null; 
/*1647*/        n = n.next, n2 = n2.next) {
/*1648*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1649*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1654*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasSideEffects() {
/*1658*/    switch (this.type) {
/*   0*/      case 85:
/*   0*/      case 129:
/*1661*/        if (this.last != null)
/*1662*/          return this.last.hasSideEffects(); 
/*1664*/        return true;
/*   0*/      case 98:
/*1667*/        if (this.first == null || this.first.next == null || this.first.next.next == null)
/*1668*/          Kit.codeBug(); 
/*1670*/        return (this.first.next.hasSideEffects() && this.first.next.next.hasSideEffects());
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
/*1734*/        return true;
/*   0*/    } 
/*1737*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1750*/    if (this.type == 38)
/*1751*/      return getString(); 
/*1752*/    if (this.type == 33) {
/*1753*/      String left = getFirstChild().getQualifiedName();
/*1754*/      if (left == null)
/*1755*/        return null; 
/*1757*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1758*/    if (this.type == 42)
/*1759*/      return "this"; 
/*1761*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1770*/    switch (getType()) {
/*   0*/      case 38:
/*   0*/      case 42:
/*1773*/        return true;
/*   0*/      case 33:
/*1775*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1777*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1787*/    switch (getType()) {
/*   0*/      case 38:
/*1789*/        return true;
/*   0*/      case 33:
/*1791*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1793*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1805*/    Preconditions.checkState((this.parent != null));
/*1806*/    this.parent.removeChild(this);
/*1807*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*1817*/    Node child = this.first;
/*1818*/    if (child != null)
/*1819*/      removeChild(child); 
/*1821*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*1828*/    Node children = this.first;
/*1829*/    for (Node child = this.first; child != null; child = child.getNext())
/*1830*/      child.parent = null; 
/*1832*/    this.first = null;
/*1833*/    this.last = null;
/*1834*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*1842*/    for (Node child = this.first; child != null; ) {
/*1843*/      Node nextChild = child.getNext();
/*1844*/      child.parent = null;
/*1845*/      child.next = null;
/*1846*/      child = nextChild;
/*   0*/    } 
/*1848*/    this.first = null;
/*1849*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*1853*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*1855*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*1858*/    Node child = prev.next;
/*1859*/    prev.next = child.next;
/*1860*/    if (child == this.last)
/*1860*/      this.last = prev; 
/*1861*/    child.next = null;
/*1862*/    child.parent = null;
/*1863*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*1872*/      result = (Node)clone();
/*1875*/      result.next = null;
/*1876*/      result.first = null;
/*1877*/      result.last = null;
/*1878*/      result.parent = null;
/*1879*/    } catch (CloneNotSupportedException e) {
/*1880*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*1882*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*1889*/    Node result = cloneNode();
/*1890*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*1891*/      Node n2clone = n2.cloneTree();
/*1892*/      n2clone.parent = result;
/*1893*/      if (result.last != null)
/*1894*/        result.last.next = n2clone; 
/*1896*/      if (result.first == null)
/*1897*/        result.first = n2clone; 
/*1899*/      result.last = n2clone;
/*   0*/    } 
/*1901*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*1911*/    if (getProp(40) == null)
/*1912*/      putProp(40, other.getProp(40)); 
/*1915*/    if (getProp(16) == null) {
/*1916*/      putProp(16, other.getProp(16));
/*1917*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*1920*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*1929*/    copyInformationFrom(other);
/*1930*/    Node child = getFirstChild();
/*1931*/    for (; child != null; child = child.getNext())
/*1932*/      child.copyInformationFromForTree(other); 
/*1935*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*1942*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*1946*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*1950*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*1963*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*1964*/      if (jsDocInfo == null)
/*1967*/        jsDocInfo = new JSDocInfo(false); 
/*1969*/      String license = jsDocInfo.getLicense();
/*1970*/      if (license == null)
/*1971*/        license = ""; 
/*1973*/      jsDocInfo.setLicense(license + fileLevelComment);
/*1974*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*1984*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*1991*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*2000*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*2009*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*2018*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*2027*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*2035*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*2043*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*2050*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*2058*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*2066*/    if (getJSDocInfo() == null)
/*2067*/      setJSDocInfo(new JSDocInfo(false)); 
/*2069*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*2077*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*2085*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2118*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2123*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2127*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2134*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2142*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2148*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2152*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2157*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2162*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2166*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2174*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2179*/      removeFlag(1);
/*2180*/      removeFlag(4);
/*2181*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2185*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2189*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2193*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2197*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2201*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2209*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2221*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2230*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2237*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2244*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2251*/    Kit.codeBug();
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2259*/      this.nodeA = nodeA;
/*2260*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2265*/      if (object instanceof NodeMismatch) {
/*2266*/        NodeMismatch that = (NodeMismatch)object;
/*2267*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2269*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2274*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/}
