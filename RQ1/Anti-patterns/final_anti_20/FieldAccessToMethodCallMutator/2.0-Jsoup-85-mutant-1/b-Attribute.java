/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Map;
/*   0*/import org.jsoup.SerializationException;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.internal.StringUtil;
/*   0*/
/*   0*/public class Attribute implements Map.Entry<String, String>, Cloneable {
/*  15*/  private static final String[] booleanAttributes = new String[] { 
/*  15*/      "allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled", "formnovalidate", 
/*  15*/      "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", 
/*  15*/      "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected", "sortable", "truespeed", "typemustmatch" };
/*   0*/  
/*   0*/  private String key;
/*   0*/  
/*   0*/  private String val;
/*   0*/  
/*   0*/  Attributes parent;
/*   0*/  
/*   0*/  public Attribute(String key, String value) {
/*  33*/    this(key, value, null);
/*   0*/  }
/*   0*/  
/*   0*/  public Attribute(String key, String val, Attributes parent) {
/*  43*/    Validate.notNull(key);
/*  44*/    setKey(key.trim());
/*  45*/    Validate.notEmpty(key);
/*  46*/    this.val = val;
/*  47*/    this.parent = parent;
/*   0*/  }
/*   0*/  
/*   0*/  public String getKey() {
/*  55*/    return this.key;
/*   0*/  }
/*   0*/  
/*   0*/  public void setKey(String key) {
/*  63*/    Validate.notNull(key);
/*  64*/    key = key.trim();
/*  65*/    Validate.notEmpty(key);
/*  66*/    if (this.parent != null) {
/*  67*/      int i = this.parent.indexOfKey(this.key);
/*  68*/      if (i != -1) {
/*  69*/          this.parent.keys[i] = key; 
/*   0*/         }
/*   0*/    } 
/*  71*/    this.key = key;
/*   0*/  }
/*   0*/  
/*   0*/  public String getValue() {
/*  79*/    return this.val;
/*   0*/  }
/*   0*/  
/*   0*/  public String setValue(String val) {
/*  87*/    String oldVal = this.parent.get(this.key);
/*  88*/    if (this.parent != null) {
/*  89*/      int i = this.parent.indexOfKey(this.key);
/*  90*/      if (i != -1) {
/*  91*/          this.parent.vals[i] = val; 
/*   0*/         }
/*   0*/    } 
/*  93*/    this.val = val;
/*  94*/    return oldVal;
/*   0*/  }
/*   0*/  
/*   0*/  public String html() {
/* 102*/    StringBuilder sb = StringUtil.borrowBuilder();
/*   0*/    try {
/* 105*/      html(sb, new Document("").outputSettings());
/* 106*/    } catch (IOException exception) {
/* 107*/      throw new SerializationException(exception);
/*   0*/    } 
/* 109*/    return StringUtil.releaseBuilder(sb);
/*   0*/  }
/*   0*/  
/*   0*/  protected static void html(String key, String val, Appendable accum, Document.OutputSettings out) throws IOException {
/* 113*/    accum.append(key);
/* 114*/    if (!shouldCollapseAttribute(key, val, out)) {
/* 115*/      accum.append("=\"");
/* 116*/      Entities.escape(accum, Attributes.checkNotNull(val), out, true, false, false);
/* 117*/      accum.append('"');
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void html(Appendable accum, Document.OutputSettings out) throws IOException {
/* 122*/    html(this.key, this.val, accum, out);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 131*/    return html();
/*   0*/  }
/*   0*/  
/*   0*/  public static Attribute createFromEncoded(String unencodedKey, String encodedValue) {
/* 141*/    String value = Entities.unescape(encodedValue, true);
/* 142*/    return new Attribute(unencodedKey, value, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isDataAttribute() {
/* 146*/    return isDataAttribute(this.key);
/*   0*/  }
/*   0*/  
/*   0*/  protected static boolean isDataAttribute(String key) {
/* 150*/    return (key.startsWith("data-") && key.length() > "data-".length());
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean shouldCollapseAttribute(Document.OutputSettings out) {
/* 160*/    return shouldCollapseAttribute(this.key, this.val, out);
/*   0*/  }
/*   0*/  
/*   0*/  protected static boolean shouldCollapseAttribute(String key, String val, Document.OutputSettings out) {
/* 164*/    return (out.syntax() == Document.OutputSettings.Syntax.html && (val == null || (("".equals(val) || val.equalsIgnoreCase(key)) && isBooleanAttribute(key))));
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isBooleanAttribute() {
/* 173*/    return (Arrays.binarySearch((Object[])booleanAttributes, this.key) >= 0 || this.val == null);
/*   0*/  }
/*   0*/  
/*   0*/  protected static boolean isBooleanAttribute(String key) {
/* 180*/    return (Arrays.binarySearch((Object[])booleanAttributes, key) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 185*/    if (this == o) {
/* 185*/        return true; 
/*   0*/       }
/* 186*/    if (o == null || getClass() != o.getClass()) {
/* 186*/        return false; 
/*   0*/       }
/* 187*/    Attribute attribute = (Attribute)o;
/* 188*/    if ((this.key != null) ? !this.key.equals(attribute.key) : (attribute.key != null)) {
/* 188*/        return false; 
/*   0*/       }
/* 189*/    return (this.val != null) ? this.val.equals(attribute.val) : ((attribute.val == null));
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 194*/    int result = (this.key != null) ? this.key.hashCode() : 0;
/* 195*/    result = 31 * result + ((this.val != null) ? this.val.hashCode() : 0);
/* 196*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Attribute clone() {
/*   0*/    try {
/* 202*/      return (Attribute)super.clone();
/* 203*/    } catch (CloneNotSupportedException e) {
/* 204*/      throw new RuntimeException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/}
