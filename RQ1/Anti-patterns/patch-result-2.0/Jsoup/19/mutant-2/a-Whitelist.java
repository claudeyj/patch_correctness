/*   0*/package org.jsoup.safety;
/*   0*/
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.nodes.Attribute;
/*   0*/import org.jsoup.nodes.Attributes;
/*   0*/import org.jsoup.nodes.Element;
/*   0*/
/*   0*/public class Whitelist {
/*   0*/  private Set<TagName> tagNames;
/*   0*/  
/*   0*/  private Map<TagName, Set<AttributeKey>> attributes;
/*   0*/  
/*   0*/  private Map<TagName, Map<AttributeKey, AttributeValue>> enforcedAttributes;
/*   0*/  
/*   0*/  private Map<TagName, Map<AttributeKey, Set<Protocol>>> protocols;
/*   0*/  
/*   0*/  private boolean preserveRelativeLinks;
/*   0*/  
/*   0*/  public static Whitelist none() {
/*  63*/    return new Whitelist();
/*   0*/  }
/*   0*/  
/*   0*/  public static Whitelist simpleText() {
/*  73*/    return new Whitelist().addTags(new String[] { "b", "em", "i", "strong", "u" });
/*   0*/  }
/*   0*/  
/*   0*/  public static Whitelist basic() {
/*  90*/    return new Whitelist().addTags(new String[] { 
/*  90*/          "a", "b", "blockquote", "br", "cite", "code", "dd", "dl", "dt", "em", 
/*  90*/          "i", "li", "ol", "p", "pre", "q", "small", "strike", "strong", "sub", 
/*  90*/          "sup", "u", "ul" }).addAttributes("a", new String[] { "href" }).addAttributes("blockquote", new String[] { "cite" }).addAttributes("q", new String[] { "cite" }).addProtocols("a", "href", new String[] { "ftp", "http", "https", "mailto" }).addProtocols("blockquote", "cite", new String[] { "http", "https" }).addProtocols("cite", "cite", new String[] { "http", "https" }).addEnforcedAttribute("a", "rel", "nofollow");
/*   0*/  }
/*   0*/  
/*   0*/  public static Whitelist basicWithImages() {
/* 116*/    return basic().addTags(new String[] { "img" }).addAttributes("img", new String[] { "align", "alt", "height", "src", "title", "width" }).addProtocols("img", "src", new String[] { "http", "https" });
/*   0*/  }
/*   0*/  
/*   0*/  public static Whitelist relaxed() {
/* 133*/    return new Whitelist().addTags(new String[] { 
/* 133*/          "a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", 
/* 133*/          "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", 
/* 133*/          "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong", 
/* 133*/          "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", 
/* 133*/          "ul" }).addAttributes("a", new String[] { "href", "title" }).addAttributes("blockquote", new String[] { "cite" }).addAttributes("col", new String[] { "span", "width" }).addAttributes("colgroup", new String[] { "span", "width" }).addAttributes("img", new String[] { "align", "alt", "height", "src", "title", "width" }).addAttributes("ol", new String[] { "start", "type" }).addAttributes("q", new String[] { "cite" }).addAttributes("table", new String[] { "summary", "width" }).addAttributes("td", new String[] { "abbr", "axis", "colspan", "rowspan", "width" }).addAttributes("th", new String[] { "abbr", "axis", "colspan", "rowspan", "scope", "width" }).addAttributes("ul", new String[] { "type" }).addProtocols("a", "href", new String[] { "ftp", "http", "https", "mailto" }).addProtocols("blockquote", "cite", new String[] { "http", "https" }).addProtocols("img", "src", new String[] { "http", "https" }).addProtocols("q", "cite", new String[] { "http", "https" });
/*   0*/  }
/*   0*/  
/*   0*/  public Whitelist() {
/* 171*/    this.tagNames = new HashSet<TagName>();
/* 172*/    this.attributes = new HashMap<TagName, Set<AttributeKey>>();
/* 173*/    this.enforcedAttributes = new HashMap<TagName, Map<AttributeKey, AttributeValue>>();
/* 174*/    this.protocols = new HashMap<TagName, Map<AttributeKey, Set<Protocol>>>();
/* 175*/    this.preserveRelativeLinks = false;
/*   0*/  }
/*   0*/  
/*   0*/  public Whitelist addTags(String... tags) {
/* 185*/    Validate.notNull(tags);
/* 187*/    for (String tagName : tags) {
/* 188*/      Validate.notEmpty(tagName);
/* 189*/      this.tagNames.add(TagName.valueOf(tagName));
/*   0*/    } 
/* 191*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Whitelist addAttributes(String tag, String... keys) {
/* 205*/    Validate.notEmpty(tag);
/* 206*/    Validate.notNull(keys);
/* 208*/    TagName tagName = TagName.valueOf(tag);
/* 209*/    Set<AttributeKey> attributeSet = new HashSet<AttributeKey>();
/* 210*/    for (String key : keys) {
/* 211*/      Validate.notEmpty(key);
/* 212*/      attributeSet.add(AttributeKey.valueOf(key));
/*   0*/    } 
/* 214*/    if (this.attributes.containsKey(tagName)) {
/* 215*/      Set<AttributeKey> currentSet = this.attributes.get(tagName);
/* 216*/      currentSet.addAll(attributeSet);
/*   0*/    } else {
/* 218*/      this.attributes.put(tagName, attributeSet);
/*   0*/    } 
/* 220*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Whitelist addEnforcedAttribute(String tag, String key, String value) {
/* 236*/    Validate.notEmpty(tag);
/* 237*/    Validate.notEmpty(key);
/* 238*/    Validate.notEmpty(value);
/* 240*/    TagName tagName = TagName.valueOf(tag);
/* 241*/    AttributeKey attrKey = AttributeKey.valueOf(key);
/* 242*/    AttributeValue attrVal = AttributeValue.valueOf(value);
/* 244*/    if (this.enforcedAttributes.containsKey(tagName)) {
/* 245*/      ((Map<AttributeKey, AttributeValue>)this.enforcedAttributes.get(tagName)).put(attrKey, attrVal);
/*   0*/    } else {
/* 247*/      Map<AttributeKey, AttributeValue> attrMap = new HashMap<AttributeKey, AttributeValue>();
/* 248*/      attrMap.put(attrKey, attrVal);
/* 249*/      this.enforcedAttributes.put(tagName, attrMap);
/*   0*/    } 
/* 251*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Whitelist preserveRelativeLinks(boolean preserve) {
/* 269*/    this.preserveRelativeLinks = preserve;
/* 270*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Whitelist addProtocols(String tag, String key, String... protocols) {
/*   0*/    Map<AttributeKey, Set<Protocol>> attrMap;
/*   0*/    Set<Protocol> protSet;
/* 285*/    Validate.notEmpty(tag);
/* 286*/    Validate.notEmpty(key);
/* 287*/    Validate.notNull(protocols);
/* 289*/    TagName tagName = TagName.valueOf(tag);
/* 290*/    AttributeKey attrKey = AttributeKey.valueOf(key);
/* 294*/    if (this.protocols.containsKey(tagName)) {
/* 295*/      attrMap = this.protocols.get(tagName);
/*   0*/    } else {
/* 297*/      attrMap = new HashMap<AttributeKey, Set<Protocol>>();
/* 298*/      this.protocols.put(tagName, attrMap);
/*   0*/    } 
/* 300*/    if (attrMap.containsKey(attrKey)) {
/* 301*/      protSet = attrMap.get(attrKey);
/*   0*/    } else {
/* 303*/      protSet = new HashSet<Protocol>();
/* 304*/      attrMap.put(attrKey, protSet);
/*   0*/    } 
/* 306*/    for (String protocol : protocols) {
/* 307*/      Validate.notEmpty(protocol);
/* 308*/      Protocol prot = Protocol.valueOf(protocol);
/* 309*/      protSet.add(prot);
/*   0*/    } 
/* 311*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isSafeTag(String tag) {
/* 315*/    return this.tagNames.contains(TagName.valueOf(tag));
/*   0*/  }
/*   0*/  
/*   0*/  boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
/* 319*/    TagName tag = TagName.valueOf(tagName);
/* 320*/    AttributeKey key = AttributeKey.valueOf(attr.getKey());
/* 322*/    if (this.attributes.containsKey(tag)) {
/* 323*/      if (((Set)this.attributes.get(tag)).contains(key)) {
/* 324*/        if (this.protocols.containsKey(tag)) {
/* 325*/          Map<AttributeKey, Set<Protocol>> attrProts = this.protocols.get(tag);
/* 327*/          return (!attrProts.containsKey(key) || testValidProtocol(el, attr, attrProts.get(key)));
/*   0*/        } 
/* 329*/        return true;
/*   0*/      } 
/*   0*/    } else {
/* 333*/      return (!tagName.equals(":all") && isSafeAttribute(":all", el, attr));
/*   0*/    } 
/* 335*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean testValidProtocol(Element el, Attribute attr, Set<Protocol> protocols) {
/* 341*/    String value = el.absUrl(attr.getKey());
/* 342*/    if (!this.preserveRelativeLinks) {
/* 343*/        attr.setValue(value); 
/*   0*/       }
/* 345*/    for (Protocol protocol : protocols) {
/* 346*/      String prot = protocol.toString() + ":";
/* 347*/      if (value.toLowerCase().startsWith(prot)) {
/* 348*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 351*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  Attributes getEnforcedAttributes(String tagName) {
/* 355*/    Attributes attrs = new Attributes();
/* 356*/    TagName tag = TagName.valueOf(tagName);
/* 357*/    if (this.enforcedAttributes.containsKey(tag)) {
/* 358*/      Map<AttributeKey, AttributeValue> keyVals = this.enforcedAttributes.get(tag);
/* 359*/      for (Map.Entry<AttributeKey, AttributeValue> entry : keyVals.entrySet()) {
/* 360*/          attrs.put(((AttributeKey)entry.getKey()).toString(), ((AttributeValue)entry.getValue()).toString()); 
/*   0*/         }
/*   0*/    } 
/* 363*/    return attrs;
/*   0*/  }
/*   0*/  
/*   0*/  static class TagName extends TypedValue {
/*   0*/    TagName(String value) {
/* 370*/      super(value);
/*   0*/    }
/*   0*/    
/*   0*/    static TagName valueOf(String value) {
/* 374*/      return new TagName(value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class AttributeKey extends TypedValue {
/*   0*/    AttributeKey(String value) {
/* 380*/      super(value);
/*   0*/    }
/*   0*/    
/*   0*/    static AttributeKey valueOf(String value) {
/* 384*/      return new AttributeKey(value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class AttributeValue extends TypedValue {
/*   0*/    AttributeValue(String value) {
/* 390*/      super(value);
/*   0*/    }
/*   0*/    
/*   0*/    static AttributeValue valueOf(String value) {
/* 394*/      return new AttributeValue(value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class Protocol extends TypedValue {
/*   0*/    Protocol(String value) {
/* 400*/      super(value);
/*   0*/    }
/*   0*/    
/*   0*/    static Protocol valueOf(String value) {
/* 404*/      return new Protocol(value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static abstract class TypedValue {
/*   0*/    private String value;
/*   0*/    
/*   0*/    TypedValue(String value) {
/* 412*/      Validate.notNull(value);
/* 413*/      this.value = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 418*/      int prime = 31;
/* 419*/      int result = 1;
/* 420*/      result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
/* 421*/      return result;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object obj) {
/* 426*/      if (this == obj) {
/* 426*/          return true; 
/*   0*/         }
/* 427*/      if (obj == null) {
/* 427*/          return false; 
/*   0*/         }
/* 428*/      if (getClass() != obj.getClass()) {
/* 428*/          return false; 
/*   0*/         }
/* 429*/      TypedValue other = (TypedValue)obj;
/* 430*/      if (this.value == null) {
/* 431*/        if (other.value != null) {
/* 431*/            return false; 
/*   0*/           }
/* 432*/      } else if (!this.value.equals(other.value)) {
/* 432*/        return false;
/*   0*/      } 
/* 433*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 438*/      return this.value;
/*   0*/    }
/*   0*/  }
/*   0*/}
