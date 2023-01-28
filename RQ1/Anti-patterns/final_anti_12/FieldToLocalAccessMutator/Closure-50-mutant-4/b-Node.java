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
/*   0*/  public static final int STATIC_SOURCE_FILE = 51;
/*   0*/  
/*   0*/  public static final int LENGTH = 52;
/*   0*/  
/*   0*/  public static final int INPUT_ID = 53;
/*   0*/  
/*   0*/  public static final int LAST_PROP = 53;
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
/* 184*/    switch (propType) {
/*   0*/      case -3:
/* 185*/        return "local_block";
/*   0*/      case -2:
/* 186*/        return "object_ids_prop";
/*   0*/      case -1:
/* 187*/        return "catch_scope_prop";
/*   0*/      case 0:
/* 188*/        return "label_id_prop";
/*   0*/      case 1:
/* 189*/        return "target";
/*   0*/      case 41:
/* 190*/        return "braceless_type";
/*   0*/      case 2:
/* 191*/        return "break";
/*   0*/      case 3:
/* 192*/        return "continue";
/*   0*/      case 4:
/* 193*/        return "enum";
/*   0*/      case 5:
/* 194*/        return "function";
/*   0*/      case 6:
/* 195*/        return "temp";
/*   0*/      case 7:
/* 196*/        return "local";
/*   0*/      case 8:
/* 197*/        return "codeoffset";
/*   0*/      case 9:
/* 198*/        return "fixups";
/*   0*/      case 10:
/* 199*/        return "vars";
/*   0*/      case 30:
/* 200*/        return "var_args_name";
/*   0*/      case 11:
/* 201*/        return "uses";
/*   0*/      case 12:
/* 202*/        return "regexp";
/*   0*/      case 13:
/* 203*/        return "cases";
/*   0*/      case 14:
/* 204*/        return "default";
/*   0*/      case 15:
/* 205*/        return "casearray";
/*   0*/      case 16:
/* 206*/        return "sourcename";
/*   0*/      case 17:
/* 207*/        return "type";
/*   0*/      case 18:
/* 208*/        return "special_prop";
/*   0*/      case 19:
/* 209*/        return "label";
/*   0*/      case 20:
/* 210*/        return "finally";
/*   0*/      case 21:
/* 211*/        return "localcount";
/*   0*/      case 22:
/* 213*/        return "targetblock";
/*   0*/      case 23:
/* 214*/        return "variable";
/*   0*/      case 24:
/* 215*/        return "lastuse";
/*   0*/      case 25:
/* 216*/        return "isnumber";
/*   0*/      case 26:
/* 217*/        return "directcall";
/*   0*/      case 27:
/* 219*/        return "specialcall";
/*   0*/      case 28:
/* 220*/        return "debugsource";
/*   0*/      case 29:
/* 222*/        return "jsdoc_info";
/*   0*/      case 31:
/* 224*/        return "skip_indexes";
/*   0*/      case 32:
/* 225*/        return "incrdecr";
/*   0*/      case 33:
/* 226*/        return "member_type";
/*   0*/      case 34:
/* 227*/        return "name";
/*   0*/      case 35:
/* 228*/        return "parenthesized";
/*   0*/      case 36:
/* 229*/        return "quoted";
/*   0*/      case 37:
/* 230*/        return "opt_arg";
/*   0*/      case 38:
/* 232*/        return "synthetic";
/*   0*/      case 39:
/* 233*/        return "empty_block";
/*   0*/      case 40:
/* 234*/        return "originalname";
/*   0*/      case 42:
/* 235*/        return "side_effect_flags";
/*   0*/      case 43:
/* 237*/        return "is_constant_name";
/*   0*/      case 44:
/* 238*/        return "is_optional_param";
/*   0*/      case 45:
/* 239*/        return "is_var_args_param";
/*   0*/      case 46:
/* 240*/        return "is_namespace";
/*   0*/      case 47:
/* 241*/        return "is_dispatcher";
/*   0*/      case 48:
/* 242*/        return "directives";
/*   0*/      case 49:
/* 243*/        return "direct_eval";
/*   0*/      case 50:
/* 244*/        return "free_call";
/*   0*/      case 51:
/* 245*/        return "source_file";
/*   0*/      case 53:
/* 246*/        return "input_id";
/*   0*/      case 52:
/* 247*/        return "length";
/*   0*/    } 
/* 249*/    Kit.codeBug();
/* 251*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private double number;
/*   0*/    
/*   0*/    NumberNode(double number) {
/* 259*/      super(39);
/* 260*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public NumberNode(double number, int lineno, int charno) {
/* 264*/      super(39, lineno, charno);
/* 265*/      this.number = number;
/*   0*/    }
/*   0*/    
/*   0*/    public double getDouble() {
/* 270*/      return this.number;
/*   0*/    }
/*   0*/    
/*   0*/    public void setDouble(double d) {
/* 275*/      this.number = d;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 280*/      return (super.isEquivalentTo(node, compareJsType, recurse) && getDouble() == ((NumberNode)node).getDouble());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringNode extends Node {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    private String str;
/*   0*/    
/*   0*/    StringNode(int type, String str) {
/* 292*/      super(type);
/* 293*/      if (null == str)
/* 294*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 296*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    StringNode(int type, String str, int lineno, int charno) {
/* 300*/      super(type, lineno, charno);
/* 301*/      if (null == str)
/* 302*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 304*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    public String getString() {
/* 313*/      return this.str;
/*   0*/    }
/*   0*/    
/*   0*/    public void setString(String str) {
/* 322*/      if (null == str)
/* 323*/        throw new IllegalArgumentException("StringNode: str is null"); 
/* 325*/      this.str = str;
/*   0*/    }
/*   0*/    
/*   0*/    boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/* 330*/      return (super.isEquivalentTo(node, compareJsType, recurse) && this.str.equals(((StringNode)node).str));
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isQuotedString() {
/* 342*/      return getBooleanProp(36);
/*   0*/    }
/*   0*/    
/*   0*/    public void setQuotedString() {
/* 350*/      putBooleanProp(36, true);
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
/* 373*/      this.propType = propType;
/* 374*/      this.next = next;
/*   0*/    }
/*   0*/    
/*   0*/    public int getType() {
/* 379*/      return this.propType;
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem getNext() {
/* 384*/      return this.next;
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
/* 399*/      super(propType, next);
/* 400*/      this.objectValue = objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 405*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 410*/      return this.objectValue;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 415*/      return (this.objectValue == null) ? "null" : this.objectValue.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 420*/      return new ObjectPropListItem(getType(), this.objectValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class IntPropListItem extends AbstractPropListItem {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    final int intValue;
/*   0*/    
/*   0*/    IntPropListItem(int propType, int intValue, Node.PropListItem next) {
/* 431*/      super(propType, next);
/* 432*/      this.intValue = intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public int getIntValue() {
/* 437*/      return this.intValue;
/*   0*/    }
/*   0*/    
/*   0*/    public Object getObjectValue() {
/* 442*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 447*/      return String.valueOf(this.intValue);
/*   0*/    }
/*   0*/    
/*   0*/    public Node.PropListItem chain(Node.PropListItem next) {
/* 452*/      return new IntPropListItem(getType(), this.intValue, next);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType) {
/* 457*/    this.type = nodeType;
/* 458*/    this.parent = null;
/* 459*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child) {
/* 463*/    Preconditions.checkArgument((child.parent == null), "new child has existing parent");
/* 465*/    Preconditions.checkArgument((child.next == null), "new child has existing sibling");
/* 468*/    this.type = nodeType;
/* 469*/    this.parent = null;
/* 470*/    this.first = this.last = child;
/* 471*/    child.next = null;
/* 472*/    child.parent = this;
/* 473*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right) {
/* 477*/    Preconditions.checkArgument((left.parent == null), "first new child has existing parent");
/* 479*/    Preconditions.checkArgument((left.next == null), "first new child has existing sibling");
/* 481*/    Preconditions.checkArgument((right.parent == null), "second new child has existing parent");
/* 483*/    Preconditions.checkArgument((right.next == null), "second new child has existing sibling");
/* 485*/    this.type = nodeType;
/* 486*/    this.parent = null;
/* 487*/    this.first = left;
/* 488*/    this.last = right;
/* 489*/    left.next = right;
/* 490*/    left.parent = this;
/* 491*/    right.next = null;
/* 492*/    right.parent = this;
/* 493*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right) {
/* 497*/    Preconditions.checkArgument((left.parent == null));
/* 498*/    Preconditions.checkArgument((left.next == null));
/* 499*/    Preconditions.checkArgument((mid.parent == null));
/* 500*/    Preconditions.checkArgument((mid.next == null));
/* 501*/    Preconditions.checkArgument((right.parent == null));
/* 502*/    Preconditions.checkArgument((right.next == null));
/* 503*/    this.type = nodeType;
/* 504*/    this.parent = null;
/* 505*/    this.first = left;
/* 506*/    this.last = right;
/* 507*/    left.next = mid;
/* 508*/    left.parent = this;
/* 509*/    mid.next = right;
/* 510*/    mid.parent = this;
/* 511*/    right.next = null;
/* 512*/    right.parent = this;
/* 513*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right) {
/* 517*/    Preconditions.checkArgument((left.parent == null));
/* 518*/    Preconditions.checkArgument((left.next == null));
/* 519*/    Preconditions.checkArgument((mid.parent == null));
/* 520*/    Preconditions.checkArgument((mid.next == null));
/* 521*/    Preconditions.checkArgument((mid2.parent == null));
/* 522*/    Preconditions.checkArgument((mid2.next == null));
/* 523*/    Preconditions.checkArgument((right.parent == null));
/* 524*/    Preconditions.checkArgument((right.next == null));
/* 525*/    this.type = nodeType;
/* 526*/    this.parent = null;
/* 527*/    this.first = left;
/* 528*/    this.last = right;
/* 529*/    left.next = mid;
/* 530*/    left.parent = this;
/* 531*/    mid.next = mid2;
/* 532*/    mid.parent = this;
/* 533*/    mid2.next = right;
/* 534*/    mid2.parent = this;
/* 535*/    right.next = null;
/* 536*/    right.parent = this;
/* 537*/    this.sourcePosition = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, int lineno, int charno) {
/* 541*/    this.type = nodeType;
/* 542*/    this.parent = null;
/* 543*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node child, int lineno, int charno) {
/* 547*/    this(nodeType, child);
/* 548*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node right, int lineno, int charno) {
/* 552*/    this(nodeType, left, right);
/* 553*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node right, int lineno, int charno) {
/* 558*/    this(nodeType, left, mid, right);
/* 559*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node left, Node mid, Node mid2, Node right, int lineno, int charno) {
/* 564*/    this(nodeType, left, mid, mid2, right);
/* 565*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children, int lineno, int charno) {
/* 569*/    this(nodeType, children);
/* 570*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public Node(int nodeType, Node[] children) {
/* 574*/    this.type = nodeType;
/* 575*/    this.parent = null;
/* 576*/    if (children.length != 0) {
/* 577*/      this.first = children[0];
/* 578*/      this.last = children[children.length - 1];
/* 580*/      for (int i = 1; i < children.length; i++) {
/* 581*/        if (null != (children[i - 1]).next)
/* 583*/          throw new IllegalArgumentException("duplicate child"); 
/* 585*/        (children[i - 1]).next = children[i];
/* 586*/        Preconditions.checkArgument(((children[i - 1]).parent == null));
/* 587*/        (children[i - 1]).parent = this;
/*   0*/      } 
/* 589*/      Preconditions.checkArgument(((children[children.length - 1]).parent == null));
/* 590*/      (children[children.length - 1]).parent = this;
/* 592*/      if (null != this.last.next)
/* 594*/        throw new IllegalArgumentException("duplicate child"); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number) {
/* 600*/    return new NumberNode(number);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newNumber(double number, int lineno, int charno) {
/* 604*/    return new NumberNode(number, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str) {
/* 608*/    return new StringNode(40, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str) {
/* 612*/    return new StringNode(type, str);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(String str, int lineno, int charno) {
/* 616*/    return new StringNode(40, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public static Node newString(int type, String str, int lineno, int charno) {
/* 620*/    return new StringNode(type, str, lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public int getType() {
/* 624*/    return this.type;
/*   0*/  }
/*   0*/  
/*   0*/  public void setType(int type) {
/* 628*/    this.type = type;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChildren() {
/* 632*/    return (this.first != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getFirstChild() {
/* 636*/    return this.first;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastChild() {
/* 640*/    return this.last;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getNext() {
/* 644*/    return this.next;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildBefore(Node child) {
/* 648*/    if (child == this.first)
/* 649*/      return null; 
/* 651*/    Node n = this.first;
/* 652*/    while (n.next != child) {
/* 653*/      n = n.next;
/* 654*/      if (n == null)
/* 655*/        throw new RuntimeException("node is not a child"); 
/*   0*/    } 
/* 658*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getChildAtIndex(int i) {
/* 662*/    Node n = this.first;
/* 663*/    while (i > 0) {
/* 664*/      n = n.next;
/* 665*/      i--;
/*   0*/    } 
/* 667*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOfChild(Node child) {
/* 671*/    Node n = this.first;
/* 672*/    int i = 0;
/* 673*/    while (n != null) {
/* 674*/      if (child == n)
/* 675*/        return i; 
/* 678*/      n = n.next;
/* 679*/      i++;
/*   0*/    } 
/* 681*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getLastSibling() {
/* 685*/    Node n = this;
/* 686*/    while (n.next != null)
/* 687*/      n = n.next; 
/* 689*/    return n;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToFront(Node child) {
/* 693*/    Preconditions.checkArgument((child.parent == null));
/* 694*/    Preconditions.checkArgument((child.next == null));
/* 695*/    child.parent = this;
/* 696*/    child.next = this.first;
/* 697*/    this.first = child;
/* 698*/    if (this.last == null)
/* 699*/      this.last = child; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildToBack(Node child) {
/* 704*/    Preconditions.checkArgument((child.parent == null));
/* 705*/    Preconditions.checkArgument((child.next == null));
/* 706*/    child.parent = this;
/* 707*/    child.next = null;
/* 708*/    if (this.last == null) {
/* 709*/      this.first = this.last = child;
/*   0*/      return;
/*   0*/    } 
/* 712*/    this.last.next = child;
/* 713*/    this.last = child;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToFront(Node children) {
/* 717*/    for (Node child = children; child != null; child = child.next) {
/* 718*/      Preconditions.checkArgument((child.parent == null));
/* 719*/      child.parent = this;
/*   0*/    } 
/* 721*/    Node lastSib = children.getLastSibling();
/* 722*/    lastSib.next = this.first;
/* 723*/    this.first = children;
/* 724*/    if (this.last == null)
/* 725*/      this.last = lastSib; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildrenToBack(Node children) {
/* 730*/    for (Node child = children; child != null; child = child.next) {
/* 731*/      Preconditions.checkArgument((child.parent == null));
/* 732*/      child.parent = this;
/*   0*/    } 
/* 734*/    if (this.last != null)
/* 735*/      this.last.next = children; 
/* 737*/    this.last = children.getLastSibling();
/* 738*/    if (this.first == null)
/* 739*/      this.first = children; 
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildBefore(Node newChild, Node node) {
/* 747*/    Preconditions.checkArgument((node != null), "The existing child node of the parent should not be null.");
/* 749*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 751*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 753*/    if (this.first == node) {
/* 754*/      newChild.parent = this;
/* 755*/      newChild.next = this.first;
/* 756*/      this.first = newChild;
/*   0*/      return;
/*   0*/    } 
/* 759*/    Node prev = getChildBefore(node);
/* 760*/    addChildAfter(newChild, prev);
/*   0*/  }
/*   0*/  
/*   0*/  public void addChildAfter(Node newChild, Node node) {
/* 767*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 769*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 771*/    newChild.parent = this;
/* 772*/    newChild.next = node.next;
/* 773*/    node.next = newChild;
/* 774*/    if (this.last == node)
/* 775*/      this.last = newChild; 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChild(Node child) {
/* 783*/    Node prev = getChildBefore(child);
/* 784*/    if (prev == null) {
/* 785*/      this.first = this.first.next;
/*   0*/    } else {
/* 787*/      prev.next = child.next;
/*   0*/    } 
/* 788*/    if (child == this.last)
/* 788*/      this.last = prev; 
/* 789*/    child.next = null;
/* 790*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChild(Node child, Node newChild) {
/* 797*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 799*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 803*/    newChild.copyInformationFrom(child);
/* 805*/    newChild.next = child.next;
/* 806*/    newChild.parent = this;
/* 807*/    if (child == this.first) {
/* 808*/      this.first = newChild;
/*   0*/    } else {
/* 810*/      Node prev = getChildBefore(child);
/* 811*/      prev.next = newChild;
/*   0*/    } 
/* 813*/    if (child == this.last)
/* 814*/      this.last = newChild; 
/* 815*/    child.next = null;
/* 816*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceChildAfter(Node prevChild, Node newChild) {
/* 820*/    Preconditions.checkArgument((prevChild.parent == this), "prev is not a child of this node.");
/* 823*/    Preconditions.checkArgument((newChild.next == null), "The new child node has siblings.");
/* 825*/    Preconditions.checkArgument((newChild.parent == null), "The new child node already has a parent.");
/* 829*/    newChild.copyInformationFrom(prevChild);
/* 831*/    Node child = prevChild.next;
/* 832*/    newChild.next = child.next;
/* 833*/    newChild.parent = this;
/* 834*/    prevChild.next = newChild;
/* 835*/    if (child == this.last)
/* 836*/      this.last = newChild; 
/* 837*/    child.next = null;
/* 838*/    child.parent = null;
/*   0*/  }
/*   0*/  
/*   0*/  @VisibleForTesting
/*   0*/  PropListItem lookupProperty(int propType) {
/* 843*/    PropListItem x = this.propListHead;
/* 844*/    while (x != null && propType != x.getType())
/* 845*/      x = x.getNext(); 
/* 847*/    return x;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clonePropsFrom(Node other) {
/* 858*/    Preconditions.checkState((this.propListHead == null), "Node has existing properties.");
/* 860*/    this.propListHead = other.propListHead;
/* 861*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProp(int propType) {
/* 865*/    PropListItem result = removeProp(this.propListHead, propType);
/* 866*/    if (result != this.propListHead)
/* 867*/      this.propListHead = result; 
/*   0*/  }
/*   0*/  
/*   0*/  private PropListItem removeProp(PropListItem item, int propType) {
/* 878*/    if (item == null)
/* 879*/      return null; 
/* 880*/    if (item.getType() == propType)
/* 881*/      return item.getNext(); 
/* 883*/    PropListItem result = removeProp(item.getNext(), propType);
/* 884*/    if (result != item.getNext())
/* 885*/      return item.chain(result); 
/* 887*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getProp(int propType) {
/* 893*/    if (propType == 16)
/* 894*/      return getSourceFileName(); 
/* 897*/    PropListItem item = lookupProperty(propType);
/* 898*/    if (item == null)
/* 899*/      return null; 
/* 901*/    return item.getObjectValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanProp(int propType) {
/* 905*/    return (getIntProp(propType) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntProp(int propType) {
/* 913*/    PropListItem item = lookupProperty(propType);
/* 914*/    if (item == null)
/* 915*/      return 0; 
/* 917*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getExistingIntProp(int propType) {
/* 921*/    PropListItem item = lookupProperty(propType);
/* 922*/    if (item == null)
/* 923*/      Kit.codeBug(); 
/* 925*/    return item.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void putProp(int propType, Object value) {
/* 929*/    if (propType == 16) {
/* 930*/      putProp(51, new SimpleSourceFile((String)value, false));
/*   0*/      return;
/*   0*/    } 
/* 935*/    removeProp(propType);
/* 936*/    if (value != null)
/* 937*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  public void putBooleanProp(int propType, boolean value) {
/* 942*/    putIntProp(propType, value ? 1 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void putIntProp(int propType, int value) {
/* 946*/    removeProp(propType);
/* 947*/    if (value != 0)
/* 948*/      this.propListHead = createProp(propType, value, this.propListHead); 
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, Object value, PropListItem next) {
/* 953*/    return new ObjectPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem createProp(int propType, int value, PropListItem next) {
/* 957*/    return new IntPropListItem(propType, value, next);
/*   0*/  }
/*   0*/  
/*   0*/  private int[] getSortedPropTypes() {
/* 962*/    int count = 0;
/* 963*/    for (PropListItem x = this.propListHead; x != null; x = x.getNext())
/* 964*/      count++; 
/* 967*/    int[] keys = new int[count];
/* 968*/    for (PropListItem propListItem1 = this.propListHead; propListItem1 != null; propListItem1 = propListItem1.getNext()) {
/* 969*/      count--;
/* 970*/      keys[count] = propListItem1.getType();
/*   0*/    } 
/* 973*/    Arrays.sort(keys);
/* 974*/    return keys;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDouble() throws UnsupportedOperationException {
/* 979*/    if (getType() == 39)
/* 980*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 983*/    throw new UnsupportedOperationException(this + " is not a number node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setDouble(double s) throws UnsupportedOperationException {
/* 989*/    if (getType() == 39)
/* 990*/      throw new IllegalStateException("Number node not created with Node.newNumber"); 
/* 993*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String getString() throws UnsupportedOperationException {
/* 999*/    if (getType() == 40)
/*1000*/      throw new IllegalStateException("String node not created with Node.newString"); 
/*1003*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public void setString(String s) throws UnsupportedOperationException {
/*1009*/    if (getType() == 40)
/*1010*/      throw new IllegalStateException("String node not created with Node.newString"); 
/*1013*/    throw new UnsupportedOperationException(this + " is not a string node");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1019*/    return toString(true, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(boolean printSource, boolean printAnnotations, boolean printType) {
/*1026*/    if (Token.shouldPrintTrees()) {
/*1027*/      StringBuilder sb = new StringBuilder();
/*1028*/      toString(sb, printSource, printAnnotations, printType);
/*1029*/      return sb.toString();
/*   0*/    } 
/*1031*/    return String.valueOf(this.type);
/*   0*/  }
/*   0*/  
/*   0*/  private void toString(StringBuilder sb, boolean printSource, boolean printAnnotations, boolean printType) {
/*1040*/    sb.append(Token.name(this.type));
/*1041*/    if (this instanceof StringNode) {
/*1042*/      sb.append(' ');
/*1043*/      sb.append(getString());
/*1044*/    } else if (this.type == 105) {
/*1045*/      sb.append(' ');
/*1049*/      if (this.first == null || this.first.getType() != 38) {
/*1050*/        sb.append("<invalid>");
/*   0*/      } else {
/*1052*/        sb.append(this.first.getString());
/*   0*/      } 
/*1054*/    } else if (this instanceof ScriptOrFnNode) {
/*1055*/      ScriptOrFnNode sof = (ScriptOrFnNode)this;
/*1056*/      if (this instanceof FunctionNode) {
/*1057*/        FunctionNode fn = (FunctionNode)this;
/*1058*/        sb.append(' ');
/*1059*/        sb.append(fn.getFunctionName());
/*   0*/      } 
/*1061*/      if (printSource) {
/*1062*/        sb.append(" [source name: ");
/*1063*/        sb.append(sof.getSourceName());
/*1064*/        sb.append("] [encoded source length: ");
/*1065*/        sb.append(sof.getEncodedSourceEnd() - sof.getEncodedSourceStart());
/*1066*/        sb.append("] [base line: ");
/*1067*/        sb.append(sof.getBaseLineno());
/*1068*/        sb.append("] [end line: ");
/*1069*/        sb.append(sof.getEndLineno());
/*1070*/        sb.append(']');
/*   0*/      } 
/*1072*/    } else if (this.type == 39) {
/*1073*/      sb.append(' ');
/*1074*/      sb.append(getDouble());
/*   0*/    } 
/*1076*/    if (printSource) {
/*1077*/      int lineno = getLineno();
/*1078*/      if (lineno != -1) {
/*1079*/        sb.append(' ');
/*1080*/        sb.append(lineno);
/*   0*/      } 
/*   0*/    } 
/*1084*/    if (printAnnotations) {
/*1085*/      int[] keys = getSortedPropTypes();
/*1086*/      for (int i = 0; i < keys.length; i++) {
/*   0*/        String value;
/*1087*/        int type = keys[i];
/*1088*/        PropListItem x = lookupProperty(type);
/*1089*/        sb.append(" [");
/*1090*/        sb.append(propToString(type));
/*1091*/        sb.append(": ");
/*1093*/        switch (type) {
/*   0*/          case 22:
/*1095*/            value = "target block property";
/*   0*/            break;
/*   0*/          case -3:
/*1098*/            value = "last local block";
/*   0*/            break;
/*   0*/          case 25:
/*1101*/            switch (x.getIntValue()) {
/*   0*/              case 0:
/*1103*/                value = "both";
/*   0*/                break;
/*   0*/              case 2:
/*1106*/                value = "right";
/*   0*/                break;
/*   0*/              case 1:
/*1109*/                value = "left";
/*   0*/                break;
/*   0*/            } 
/*1112*/            throw Kit.codeBug();
/*   0*/          case 27:
/*1116*/            switch (x.getIntValue()) {
/*   0*/              case 1:
/*1118*/                value = "eval";
/*   0*/                break;
/*   0*/              case 2:
/*1121*/                value = "with";
/*   0*/                break;
/*   0*/            } 
/*1125*/            throw Kit.codeBug();
/*   0*/          default:
/*1129*/            value = x.toString();
/*   0*/            break;
/*   0*/        } 
/*1132*/        sb.append(value);
/*1133*/        sb.append(']');
/*   0*/      } 
/*   0*/    } 
/*1137*/    if (printType && 
/*1138*/      this.jsType != null) {
/*1139*/      String jsTypeString = this.jsType.toString();
/*1140*/      if (jsTypeString != null) {
/*1141*/        sb.append(" : ");
/*1142*/        sb.append(jsTypeString);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringTree() {
/*1151*/    return toStringTreeImpl();
/*   0*/  }
/*   0*/  
/*   0*/  private String toStringTreeImpl() {
/*   0*/    try {
/*1156*/      StringBuilder s = new StringBuilder();
/*1157*/      appendStringTree(s);
/*1158*/      return s.toString();
/*1159*/    } catch (IOException e) {
/*1160*/      throw new RuntimeException("Should not happen\n" + e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void appendStringTree(Appendable appendable) throws IOException {
/*1165*/    toStringTreeHelper(this, 0, appendable);
/*   0*/  }
/*   0*/  
/*   0*/  private static void toStringTreeHelper(Node n, int level, Appendable sb) throws IOException {
/*1171*/    for (int i = 0; i != level; i++)
/*1172*/      sb.append("    "); 
/*1174*/    sb.append(n.toString());
/*1175*/    sb.append('\n');
/*1176*/    Node cursor = n.getFirstChild();
/*1177*/    for (; cursor != null; 
/*1178*/      cursor = cursor.getNext())
/*1179*/      toStringTreeHelper(cursor, level + 1, sb); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setStaticSourceFile(StaticSourceFile file) {
/*1237*/    putProp(51, file);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceFileForTesting(String name) {
/*1242*/    putProp(51, new SimpleSourceFile(name, false));
/*   0*/  }
/*   0*/  
/*   0*/  public String getSourceFileName() {
/*1246*/    StaticSourceFile file = getStaticSourceFile();
/*1247*/    return (file == null) ? null : file.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public StaticSourceFile getStaticSourceFile() {
/*1252*/    return (StaticSourceFile)getProp(51);
/*   0*/  }
/*   0*/  
/*   0*/  public void setInputId(InputId inputId) {
/*1259*/    putProp(53, inputId);
/*   0*/  }
/*   0*/  
/*   0*/  public InputId getInputId() {
/*1266*/    return (InputId)getProp(53);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFromExterns() {
/*1270*/    StaticSourceFile file = getStaticSourceFile();
/*1271*/    return (file == null) ? false : file.isExtern();
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/*1275*/    return getIntProp(52);
/*   0*/  }
/*   0*/  
/*   0*/  public void setLength(int length) {
/*1279*/    putIntProp(52, length);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLineno() {
/*1283*/    return extractLineno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getCharno() {
/*1287*/    return extractCharno(this.sourcePosition);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourceOffset() {
/*1291*/    StaticSourceFile file = getStaticSourceFile();
/*1292*/    int lineOffset = (file == null) ? Integer.MIN_VALUE : file.getLineOffset(getLineno());
/*1294*/    return lineOffset + getCharno();
/*   0*/  }
/*   0*/  
/*   0*/  public int getSourcePosition() {
/*1298*/    return this.sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLineno(int lineno) {
/*1302*/    int charno = getCharno();
/*1303*/    if (charno == -1)
/*1304*/      charno = 0; 
/*1306*/    this.sourcePosition = mergeLineCharNo(lineno, charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setCharno(int charno) {
/*1310*/    this.sourcePosition = mergeLineCharNo(getLineno(), charno);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPosition(int sourcePosition) {
/*1314*/    this.sourcePosition = sourcePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSourceEncodedPositionForTree(int sourcePosition) {
/*1318*/    this.sourcePosition = sourcePosition;
/*1320*/    Node child = getFirstChild();
/*1321*/    for (; child != null; child = child.getNext())
/*1322*/      child.setSourceEncodedPositionForTree(sourcePosition); 
/*   0*/  }
/*   0*/  
/*   0*/  protected static int mergeLineCharNo(int lineno, int charno) {
/*1333*/    if (lineno < 0 || charno < 0)
/*1334*/      return -1; 
/*1335*/    if ((charno & 0xFFFFF000) != 0)
/*1336*/      return lineno << 12 | 0xFFF; 
/*1338*/    return lineno << 12 | charno & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractLineno(int lineCharNo) {
/*1347*/    if (lineCharNo == -1)
/*1348*/      return -1; 
/*1350*/    return lineCharNo >>> 12;
/*   0*/  }
/*   0*/  
/*   0*/  protected static int extractCharno(int lineCharNo) {
/*1359*/    if (lineCharNo == -1)
/*1360*/      return -1; 
/*1362*/    return lineCharNo & 0xFFF;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> children() {
/*1379*/    if (this.first == null)
/*1380*/      return Collections.emptySet(); 
/*1382*/    return new SiblingNodeIterable(this.first);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Node> siblings() {
/*1396*/    return new SiblingNodeIterable(this);
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
/*1409*/      this.start = start;
/*1410*/      this.current = start;
/*1411*/      this.used = false;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1416*/      if (!this.used) {
/*1417*/        this.used = true;
/*1418*/        return this;
/*   0*/      } 
/*1426*/      return new SiblingNodeIterable(this.start).iterator();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/*1432*/      return (this.current != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Node next() {
/*1437*/      if (this.current == null)
/*1438*/        throw new NoSuchElementException(); 
/*   0*/      try {
/*1441*/        return this.current;
/*   0*/      } finally {
/*1443*/        this.current = this.current.getNext();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/*1449*/      throw new UnsupportedOperationException();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  PropListItem getPropListHeadForTesting() {
/*1457*/    return this.propListHead;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getParent() {
/*1461*/    return this.parent;
/*   0*/  }
/*   0*/  
/*   0*/  public Node getAncestor(int level) {
/*1470*/    Preconditions.checkArgument((level >= 0));
/*1471*/    Node node = this;
/*1472*/    while (node != null && level-- > 0)
/*1473*/      node = node.getParent(); 
/*1475*/    return node;
/*   0*/  }
/*   0*/  
/*   0*/  public AncestorIterable getAncestors() {
/*1482*/    return new AncestorIterable(getParent());
/*   0*/  }
/*   0*/  
/*   0*/  public static class AncestorIterable implements Iterable<Node> {
/*   0*/    private Node cur;
/*   0*/    
/*   0*/    AncestorIterable(Node cur) {
/*1495*/      this.cur = cur;
/*   0*/    }
/*   0*/    
/*   0*/    public Iterator<Node> iterator() {
/*1500*/      return new Iterator<Node>() {
/*   0*/          public boolean hasNext() {
/*1503*/            return (Node.AncestorIterable.this.cur != null);
/*   0*/          }
/*   0*/          
/*   0*/          public Node next() {
/*1508*/            if (!hasNext())
/*1508*/              throw new NoSuchElementException(); 
/*1509*/            Node n = Node.AncestorIterable.this.cur;
/*1510*/            Node.AncestorIterable.this.cur = Node.AncestorIterable.this.cur.getParent();
/*1511*/            return n;
/*   0*/          }
/*   0*/          
/*   0*/          public void remove() {
/*1516*/            throw new UnsupportedOperationException();
/*   0*/          }
/*   0*/        };
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOneChild() {
/*1529*/    return (this.first != null && this.first == this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasMoreThanOneChild() {
/*1539*/    return (this.first != null && this.first != this.last);
/*   0*/  }
/*   0*/  
/*   0*/  public int getChildCount() {
/*1543*/    int c = 0;
/*1544*/    for (Node n = this.first; n != null; n = n.next)
/*1545*/      c++; 
/*1547*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasChild(Node child) {
/*1552*/    for (Node n = this.first; n != null; n = n.getNext()) {
/*1553*/      if (child == n)
/*1554*/        return true; 
/*   0*/    } 
/*1557*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String checkTreeEquals(Node node2) {
/*1565*/    NodeMismatch diff = checkTreeEqualsImpl(node2);
/*1566*/    if (diff != null)
/*1567*/      return "Node tree inequality:\nTree1:\n" + toStringTree() + "\n\nTree2:\n" + node2.toStringTree() + "\n\nSubtree1: " + diff.nodeA.toStringTree() + "\n\nSubtree2: " + diff.nodeB.toStringTree(); 
/*1573*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private static Class getNodeClass(Node n) {
/*1582*/    Class<?> c = n.getClass();
/*1583*/    if (c == FunctionNode.class || c == ScriptOrFnNode.class)
/*1584*/      return Node.class; 
/*1586*/    return c;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeEqualsImpl(Node node2) {
/*1595*/    if (!isEquivalentTo(node2, false, false))
/*1596*/      return new NodeMismatch(this, node2); 
/*1599*/    NodeMismatch res = null;
/*1601*/    Node n = this.first, n2 = node2.first;
/*1602*/    for (; res == null && n != null; 
/*1603*/      n2 = n = n.next) {
/*1604*/      if (node2 == null)
/*1605*/        throw new IllegalStateException(); 
/*1607*/      res = n.checkTreeEqualsImpl(n2);
/*1608*/      if (res != null)
/*1609*/        return res; 
/*   0*/    } 
/*1612*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  NodeMismatch checkTreeTypeAwareEqualsImpl(Node node2) {
/*1622*/    if (!isEquivalentTo(node2, true, false))
/*1623*/      return new NodeMismatch(this, node2); 
/*1626*/    NodeMismatch res = null;
/*1628*/    Node n = this.first, n2 = node2.first;
/*1629*/    for (; res == null && n != null; 
/*1630*/      n = n.next, n2 = n2.next) {
/*1631*/      res = n.checkTreeTypeAwareEqualsImpl(n2);
/*1632*/      if (res != null)
/*1633*/        return res; 
/*   0*/    } 
/*1636*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public static String tokenToName(int token) {
/*1640*/    switch (token) {
/*   0*/      case -1:
/*1641*/        return "error";
/*   0*/      case 0:
/*1642*/        return "eof";
/*   0*/      case 1:
/*1643*/        return "eol";
/*   0*/      case 2:
/*1644*/        return "enterwith";
/*   0*/      case 3:
/*1645*/        return "leavewith";
/*   0*/      case 4:
/*1646*/        return "return";
/*   0*/      case 5:
/*1647*/        return "goto";
/*   0*/      case 6:
/*1648*/        return "ifeq";
/*   0*/      case 7:
/*1649*/        return "ifne";
/*   0*/      case 8:
/*1650*/        return "setname";
/*   0*/      case 9:
/*1651*/        return "bitor";
/*   0*/      case 10:
/*1652*/        return "bitxor";
/*   0*/      case 11:
/*1653*/        return "bitand";
/*   0*/      case 12:
/*1654*/        return "eq";
/*   0*/      case 13:
/*1655*/        return "ne";
/*   0*/      case 14:
/*1656*/        return "lt";
/*   0*/      case 15:
/*1657*/        return "le";
/*   0*/      case 16:
/*1658*/        return "gt";
/*   0*/      case 17:
/*1659*/        return "ge";
/*   0*/      case 18:
/*1660*/        return "lsh";
/*   0*/      case 19:
/*1661*/        return "rsh";
/*   0*/      case 20:
/*1662*/        return "ursh";
/*   0*/      case 21:
/*1663*/        return "add";
/*   0*/      case 22:
/*1664*/        return "sub";
/*   0*/      case 23:
/*1665*/        return "mul";
/*   0*/      case 24:
/*1666*/        return "div";
/*   0*/      case 25:
/*1667*/        return "mod";
/*   0*/      case 27:
/*1668*/        return "bitnot";
/*   0*/      case 29:
/*1669*/        return "neg";
/*   0*/      case 30:
/*1670*/        return "new";
/*   0*/      case 31:
/*1671*/        return "delprop";
/*   0*/      case 32:
/*1672*/        return "typeof";
/*   0*/      case 33:
/*1673*/        return "getprop";
/*   0*/      case 34:
/*1674*/        return "setprop";
/*   0*/      case 35:
/*1675*/        return "getelem";
/*   0*/      case 36:
/*1676*/        return "setelem";
/*   0*/      case 37:
/*1677*/        return "call";
/*   0*/      case 38:
/*1678*/        return "name";
/*   0*/      case 39:
/*1679*/        return "number";
/*   0*/      case 40:
/*1680*/        return "string";
/*   0*/      case 41:
/*1681*/        return "null";
/*   0*/      case 42:
/*1682*/        return "this";
/*   0*/      case 43:
/*1683*/        return "false";
/*   0*/      case 44:
/*1684*/        return "true";
/*   0*/      case 45:
/*1685*/        return "sheq";
/*   0*/      case 46:
/*1686*/        return "shne";
/*   0*/      case 47:
/*1687*/        return "regexp";
/*   0*/      case 28:
/*1688*/        return "pos";
/*   0*/      case 48:
/*1689*/        return "bindname";
/*   0*/      case 49:
/*1690*/        return "throw";
/*   0*/      case 51:
/*1691*/        return "in";
/*   0*/      case 52:
/*1692*/        return "instanceof";
/*   0*/      case 54:
/*1693*/        return "getvar";
/*   0*/      case 55:
/*1694*/        return "setvar";
/*   0*/      case 77:
/*1695*/        return "try";
/*   0*/      case 133:
/*1696*/        return "typeofname";
/*   0*/      case 61:
/*1697*/        return "thisfn";
/*   0*/      case 78:
/*1698*/        return "semi";
/*   0*/      case 79:
/*1699*/        return "lb";
/*   0*/      case 80:
/*1700*/        return "rb";
/*   0*/      case 81:
/*1701*/        return "lc";
/*   0*/      case 82:
/*1702*/        return "rc";
/*   0*/      case 83:
/*1703*/        return "lp";
/*   0*/      case 84:
/*1704*/        return "rp";
/*   0*/      case 85:
/*1705*/        return "comma";
/*   0*/      case 86:
/*1706*/        return "assign";
/*   0*/      case 87:
/*1707*/        return "assign_bitor";
/*   0*/      case 88:
/*1708*/        return "assign_bitxor";
/*   0*/      case 89:
/*1709*/        return "assign_bitand";
/*   0*/      case 90:
/*1710*/        return "assign_lsh";
/*   0*/      case 91:
/*1711*/        return "assign_rsh";
/*   0*/      case 92:
/*1712*/        return "assign_ursh";
/*   0*/      case 93:
/*1713*/        return "assign_add";
/*   0*/      case 94:
/*1714*/        return "assign_sub";
/*   0*/      case 95:
/*1715*/        return "assign_mul";
/*   0*/      case 96:
/*1716*/        return "assign_div";
/*   0*/      case 97:
/*1717*/        return "assign_mod";
/*   0*/      case 98:
/*1718*/        return "hook";
/*   0*/      case 99:
/*1719*/        return "colon";
/*   0*/      case 100:
/*1720*/        return "or";
/*   0*/      case 101:
/*1721*/        return "and";
/*   0*/      case 102:
/*1722*/        return "inc";
/*   0*/      case 103:
/*1723*/        return "dec";
/*   0*/      case 104:
/*1724*/        return "dot";
/*   0*/      case 105:
/*1725*/        return "function";
/*   0*/      case 106:
/*1726*/        return "export";
/*   0*/      case 107:
/*1727*/        return "import";
/*   0*/      case 108:
/*1728*/        return "if";
/*   0*/      case 109:
/*1729*/        return "else";
/*   0*/      case 110:
/*1730*/        return "switch";
/*   0*/      case 111:
/*1731*/        return "case";
/*   0*/      case 112:
/*1732*/        return "default";
/*   0*/      case 113:
/*1733*/        return "while";
/*   0*/      case 114:
/*1734*/        return "do";
/*   0*/      case 115:
/*1735*/        return "for";
/*   0*/      case 116:
/*1736*/        return "break";
/*   0*/      case 117:
/*1737*/        return "continue";
/*   0*/      case 118:
/*1738*/        return "var";
/*   0*/      case 119:
/*1739*/        return "with";
/*   0*/      case 120:
/*1740*/        return "catch";
/*   0*/      case 121:
/*1741*/        return "finally";
/*   0*/      case 123:
/*1742*/        return "reserved";
/*   0*/      case 26:
/*1743*/        return "not";
/*   0*/      case 122:
/*1744*/        return "void";
/*   0*/      case 125:
/*1745*/        return "block";
/*   0*/      case 63:
/*1746*/        return "arraylit";
/*   0*/      case 64:
/*1747*/        return "objectlit";
/*   0*/      case 126:
/*1748*/        return "label";
/*   0*/      case 127:
/*1749*/        return "target";
/*   0*/      case 128:
/*1750*/        return "loop";
/*   0*/      case 129:
/*1751*/        return "expr_void";
/*   0*/      case 130:
/*1752*/        return "expr_result";
/*   0*/      case 131:
/*1753*/        return "jsr";
/*   0*/      case 132:
/*1754*/        return "script";
/*   0*/      case 124:
/*1755*/        return "empty";
/*   0*/      case 65:
/*1756*/        return "get_ref";
/*   0*/      case 69:
/*1757*/        return "ref_special";
/*   0*/    } 
/*1759*/    return "<unknown=" + token + ">";
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(Node node) {
/*1764*/    return isEquivalentTo(node, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentToTyped(Node node) {
/*1772*/    return isEquivalentTo(node, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEquivalentTo(Node node, boolean compareJsType, boolean recurse) {
/*1782*/    if (this.type != node.getType() || getChildCount() != node.getChildCount() || getNodeClass(this) != getNodeClass(node))
/*1785*/      return false; 
/*1788*/    if (compareJsType && !JSType.isEquivalent(this.jsType, node.getJSType()))
/*1789*/      return false; 
/*1792*/    if (this.type == 63) {
/*   0*/      try {
/*1794*/        int[] indices1 = (int[])getProp(31);
/*1795*/        int[] indices2 = (int[])node.getProp(31);
/*1796*/        if (indices1 == null) {
/*1797*/          if (indices2 != null)
/*1798*/            return false; 
/*   0*/        } else {
/*1800*/          if (indices2 == null)
/*1801*/            return false; 
/*1802*/          if (indices1.length != indices2.length)
/*1803*/            return false; 
/*1805*/          for (int i = 0; i < indices1.length; i++) {
/*1806*/            if (indices1[i] != indices2[i])
/*1807*/              return false; 
/*   0*/          } 
/*   0*/        } 
/*1811*/      } catch (Exception e) {
/*1812*/        return false;
/*   0*/      } 
/*1814*/    } else if (this.type == 102 || this.type == 103) {
/*1815*/      int post1 = getIntProp(32);
/*1816*/      int post2 = node.getIntProp(32);
/*1817*/      if (post1 != post2)
/*1818*/        return false; 
/*1820*/    } else if (this.type == 40) {
/*1821*/      int quoted1 = getIntProp(36);
/*1822*/      int quoted2 = node.getIntProp(36);
/*1823*/      if (quoted1 != quoted2)
/*1824*/        return false; 
/*1826*/    } else if (this.type == 37 && 
/*1827*/      getBooleanProp(50) != node.getBooleanProp(50)) {
/*1828*/      return false;
/*   0*/    } 
/*1832*/    if (recurse) {
/*1834*/      Node n = this.first, n2 = node.first;
/*1835*/      for (; n != null; 
/*1836*/        n = n.next, n2 = n2.next) {
/*1837*/        if (!n.isEquivalentTo(n2, compareJsType, true))
/*1838*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1843*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasSideEffects() {
/*1847*/    switch (this.type) {
/*   0*/      case 85:
/*   0*/      case 129:
/*1850*/        if (this.last != null)
/*1851*/          return this.last.hasSideEffects(); 
/*1853*/        return true;
/*   0*/      case 98:
/*1856*/        if (this.first == null || this.first.next == null || this.first.next.next == null)
/*1857*/          Kit.codeBug(); 
/*1859*/        return (this.first.next.hasSideEffects() && this.first.next.next.hasSideEffects());
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
/*1923*/        return true;
/*   0*/    } 
/*1926*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String getQualifiedName() {
/*1939*/    if (this.type == 38)
/*1940*/      return getString(); 
/*1941*/    if (this.type == 33) {
/*1942*/      String left = getFirstChild().getQualifiedName();
/*1943*/      if (left == null)
/*1944*/        return null; 
/*1946*/      return left + "." + getLastChild().getString();
/*   0*/    } 
/*1947*/    if (this.type == 42)
/*1948*/      return "this"; 
/*1950*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQualifiedName() {
/*1959*/    switch (getType()) {
/*   0*/      case 38:
/*   0*/      case 42:
/*1962*/        return true;
/*   0*/      case 33:
/*1964*/        return getFirstChild().isQualifiedName();
/*   0*/    } 
/*1966*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUnscopedQualifiedName() {
/*1976*/    switch (getType()) {
/*   0*/      case 38:
/*1978*/        return true;
/*   0*/      case 33:
/*1980*/        return getFirstChild().isUnscopedQualifiedName();
/*   0*/    } 
/*1982*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Node detachFromParent() {
/*1994*/    Preconditions.checkState((this.parent != null));
/*1995*/    this.parent.removeChild(this);
/*1996*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeFirstChild() {
/*2006*/    Node child = this.first;
/*2007*/    if (child != null)
/*2008*/      removeChild(child); 
/*2010*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildren() {
/*2017*/    Node children = this.first;
/*2018*/    for (Node child = this.first; child != null; child = child.getNext())
/*2019*/      child.parent = null; 
/*2021*/    this.first = null;
/*2022*/    this.last = null;
/*2023*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public void detachChildren() {
/*2031*/    for (Node child = this.first; child != null; ) {
/*2032*/      Node nextChild = child.getNext();
/*2033*/      child.parent = null;
/*2034*/      child.next = null;
/*2035*/      child = nextChild;
/*   0*/    } 
/*2037*/    this.first = null;
/*2038*/    this.last = null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeChildAfter(Node prev) {
/*2042*/    Preconditions.checkArgument((prev.parent == this), "prev is not a child of this node.");
/*2044*/    Preconditions.checkArgument((prev.next != null), "no next sibling.");
/*2047*/    Node child = prev.next;
/*2048*/    prev.next = child.next;
/*2049*/    if (child == this.last)
/*2049*/      this.last = prev; 
/*2050*/    child.next = null;
/*2051*/    child.parent = null;
/*2052*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneNode() {
/*   0*/    Node result;
/*   0*/    try {
/*2061*/      result = (Node)clone();
/*2064*/      result.next = null;
/*2065*/      result.first = null;
/*2066*/      result.last = null;
/*2067*/      result.parent = null;
/*2068*/    } catch (CloneNotSupportedException e) {
/*2069*/      throw new RuntimeException(e.getMessage());
/*   0*/    } 
/*2071*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node cloneTree() {
/*2078*/    Node result = cloneNode();
/*2079*/    for (Node n2 = getFirstChild(); n2 != null; n2 = n2.getNext()) {
/*2080*/      Node n2clone = n2.cloneTree();
/*2081*/      n2clone.parent = result;
/*2082*/      if (result.last != null)
/*2083*/        result.last.next = n2clone; 
/*2085*/      if (result.first == null)
/*2086*/        result.first = n2clone; 
/*2088*/      result.last = n2clone;
/*   0*/    } 
/*2090*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFrom(Node other) {
/*2101*/    if (getProp(40) == null)
/*2102*/      putProp(40, other.getProp(40)); 
/*2105*/    if (getProp(51) == null) {
/*2106*/      putProp(51, other.getProp(51));
/*2107*/      this.sourcePosition = other.sourcePosition;
/*2108*/    } else if (getProp(16) == null) {
/*2109*/      putProp(16, other.getProp(16));
/*2110*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*2113*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node copyInformationFromForTree(Node other) {
/*2123*/    copyInformationFrom(other);
/*2124*/    Node child = getFirstChild();
/*2125*/    for (; child != null; child = child.getNext())
/*2126*/      child.copyInformationFromForTree(other); 
/*2129*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFrom(Node other) {
/*2137*/    putProp(40, other.getProp(40));
/*2138*/    putProp(51, other.getProp(51));
/*2139*/    this.sourcePosition = other.sourcePosition;
/*2140*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoFromForTree(Node other) {
/*2148*/    useSourceInfoFrom(other);
/*2149*/    Node child = getFirstChild();
/*2150*/    for (; child != null; child = child.getNext())
/*2151*/      child.useSourceInfoFromForTree(other); 
/*2154*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFrom(Node other) {
/*2162*/    if (getProp(40) == null)
/*2163*/      putProp(40, other.getProp(40)); 
/*2166*/    if (getProp(51) == null) {
/*2167*/      putProp(51, other.getProp(51));
/*2168*/      this.sourcePosition = other.sourcePosition;
/*   0*/    } 
/*2171*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node useSourceInfoIfMissingFromForTree(Node other) {
/*2179*/    useSourceInfoIfMissingFrom(other);
/*2180*/    Node child = getFirstChild();
/*2181*/    for (; child != null; child = child.getNext())
/*2182*/      child.useSourceInfoIfMissingFromForTree(other); 
/*2185*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getJSType() {
/*2192*/    return this.jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSType(JSType jsType) {
/*2196*/    this.jsType = jsType;
/*   0*/  }
/*   0*/  
/*   0*/  public FileLevelJsDocBuilder getJsDocBuilderForNode() {
/*2200*/    return new FileLevelJsDocBuilder();
/*   0*/  }
/*   0*/  
/*   0*/  public class FileLevelJsDocBuilder {
/*   0*/    public void append(String fileLevelComment) {
/*2213*/      JSDocInfo jsDocInfo = Node.this.getJSDocInfo();
/*2214*/      if (jsDocInfo == null)
/*2217*/        jsDocInfo = new JSDocInfo(false); 
/*2219*/      String license = jsDocInfo.getLicense();
/*2220*/      if (license == null)
/*2221*/        license = ""; 
/*2223*/      jsDocInfo.setLicense(license + fileLevelComment);
/*2224*/      Node.this.setJSDocInfo(jsDocInfo);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getJSDocInfo() {
/*2234*/    return (JSDocInfo)getProp(29);
/*   0*/  }
/*   0*/  
/*   0*/  public void setJSDocInfo(JSDocInfo info) {
/*2241*/    putProp(29, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void setVarArgs(boolean varArgs) {
/*2250*/    putBooleanProp(30, varArgs);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*2259*/    return getBooleanProp(30);
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionalArg(boolean optionalArg) {
/*2268*/    putBooleanProp(37, optionalArg);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOptionalArg() {
/*2277*/    return getBooleanProp(37);
/*   0*/  }
/*   0*/  
/*   0*/  public void setIsSyntheticBlock(boolean val) {
/*2285*/    putBooleanProp(38, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSyntheticBlock() {
/*2293*/    return getBooleanProp(38);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDirectives(Set<String> val) {
/*2300*/    putProp(48, val);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getDirectives() {
/*2308*/    return (Set<String>)getProp(48);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSuppression(String warning) {
/*2316*/    if (getJSDocInfo() == null)
/*2317*/      setJSDocInfo(new JSDocInfo(false)); 
/*2319*/    getJSDocInfo().addSuppression(warning);
/*   0*/  }
/*   0*/  
/*   0*/  public void setWasEmptyNode(boolean val) {
/*2327*/    putBooleanProp(39, val);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean wasEmptyNode() {
/*2335*/    return getBooleanProp(39);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(int flags) {
/*2368*/    Preconditions.checkArgument((getType() == 37 || getType() == 30), "setIsNoSideEffectsCall only supports CALL and NEW nodes, got " + Token.name(getType()));
/*2373*/    putIntProp(42, flags);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSideEffectFlags(SideEffectFlags flags) {
/*2377*/    setSideEffectFlags(flags.valueOf());
/*   0*/  }
/*   0*/  
/*   0*/  public int getSideEffectFlags() {
/*2384*/    return getIntProp(42);
/*   0*/  }
/*   0*/  
/*   0*/  public static class SideEffectFlags {
/*2392*/    private int value = 0;
/*   0*/    
/*   0*/    public SideEffectFlags() {}
/*   0*/    
/*   0*/    public SideEffectFlags(int value) {
/*2398*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int valueOf() {
/*2402*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public void setAllFlags() {
/*2407*/      this.value = 0;
/*   0*/    }
/*   0*/    
/*   0*/    public void clearAllFlags() {
/*2412*/      this.value = 31;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean areAllFlagsSet() {
/*2416*/      return (this.value == 0);
/*   0*/    }
/*   0*/    
/*   0*/    public void clearSideEffectFlags() {
/*2424*/      this.value |= 0xF;
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesGlobalState() {
/*2429*/      removeFlag(1);
/*2430*/      removeFlag(4);
/*2431*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setThrows() {
/*2435*/      removeFlag(8);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesThis() {
/*2439*/      removeFlag(2);
/*   0*/    }
/*   0*/    
/*   0*/    public void setMutatesArguments() {
/*2443*/      removeFlag(4);
/*   0*/    }
/*   0*/    
/*   0*/    public void setReturnsTainted() {
/*2447*/      removeFlag(16);
/*   0*/    }
/*   0*/    
/*   0*/    private void removeFlag(int flag) {
/*2451*/      this.value &= flag ^ 0xFFFFFFFF;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOnlyModifiesThisCall() {
/*2459*/    return areBitFlagsSet(getSideEffectFlags() & 0xF, 13);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNoSideEffectsCall() {
/*2471*/    return areBitFlagsSet(getSideEffectFlags(), 15);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalResultCall() {
/*2480*/    return areBitFlagsSet(getSideEffectFlags(), 16);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean areBitFlagsSet(int value, int flags) {
/*2487*/    return ((value & flags) == flags);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuotedString() {
/*2494*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuotedString() {
/*2501*/    Kit.codeBug();
/*   0*/  }
/*   0*/  
/*   0*/  static class NodeMismatch {
/*   0*/    final Node nodeA;
/*   0*/    
/*   0*/    final Node nodeB;
/*   0*/    
/*   0*/    NodeMismatch(Node nodeA, Node nodeB) {
/*2509*/      this.nodeA = nodeA;
/*2510*/      this.nodeB = nodeB;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object object) {
/*2515*/      if (object instanceof NodeMismatch) {
/*2516*/        NodeMismatch that = (NodeMismatch)object;
/*2517*/        return (that.nodeA.equals(this.nodeA) && that.nodeB.equals(this.nodeB));
/*   0*/      } 
/*2519*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*2524*/      return Objects.hashCode(new Object[] { this.nodeA, this.nodeB });
/*   0*/    }
/*   0*/  }
/*   0*/}
