/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.net.MalformedURLException;
/*   0*/import java.net.URL;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collections;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.parser.Parser;
/*   0*/import org.jsoup.select.NodeTraversor;
/*   0*/import org.jsoup.select.NodeVisitor;
/*   0*/
/*   0*/public abstract class Node implements Cloneable {
/*   0*/  Node parentNode;
/*   0*/  
/*   0*/  List<Node> childNodes;
/*   0*/  
/*   0*/  Attributes attributes;
/*   0*/  
/*   0*/  String baseUri;
/*   0*/  
/*   0*/  int siblingIndex;
/*   0*/  
/*   0*/  protected Node(String baseUri, Attributes attributes) {
/*  33*/    Validate.notNull(baseUri);
/*  34*/    Validate.notNull(attributes);
/*  36*/    this.childNodes = new ArrayList<Node>(4);
/*  37*/    this.baseUri = baseUri.trim();
/*  38*/    this.attributes = attributes;
/*   0*/  }
/*   0*/  
/*   0*/  protected Node(String baseUri) {
/*  42*/    this(baseUri, new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  protected Node() {
/*  49*/    this.childNodes = Collections.emptyList();
/*  50*/    this.attributes = null;
/*   0*/  }
/*   0*/  
/*   0*/  public String attr(String attributeKey) {
/*  75*/    Validate.notNull(attributeKey);
/*  77*/    if (this.attributes.hasKey(attributeKey)) {
/*  78*/        return this.attributes.get(attributeKey); 
/*   0*/       }
/*  79*/    if (attributeKey.toLowerCase().startsWith("abs:")) {
/*  80*/        return absUrl(attributeKey.substring("abs:".length())); 
/*   0*/       }
/*  81*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public Attributes attributes() {
/*  89*/    return this.attributes;
/*   0*/  }
/*   0*/  
/*   0*/  public Node attr(String attributeKey, String attributeValue) {
/*  99*/    this.attributes.put(attributeKey, attributeValue);
/* 100*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasAttr(String attributeKey) {
/* 109*/    Validate.notNull(attributeKey);
/* 111*/    if (attributeKey.startsWith("abs:")) {
/* 112*/      String key = attributeKey.substring("abs:".length());
/* 113*/      if (this.attributes.hasKey(key) && !absUrl(key).equals("")) {
/* 114*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 116*/    return this.attributes.hasKey(attributeKey);
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeAttr(String attributeKey) {
/* 125*/    Validate.notNull(attributeKey);
/* 126*/    this.attributes.remove(attributeKey);
/* 127*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String baseUri() {
/* 135*/    return this.baseUri;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseUri(final String baseUri) {
/* 143*/    Validate.notNull(baseUri);
/* 145*/    traverse(new NodeVisitor() {
/*   0*/          public void head(Node node, int depth) {
/* 147*/            node.baseUri = baseUri;
/*   0*/          }
/*   0*/          
/*   0*/          public void tail(Node node, int depth) {}
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public String absUrl(String attributeKey) {
/* 179*/    Validate.notEmpty(attributeKey);
/* 181*/    String relUrl = attr(attributeKey);
/* 182*/    if (!hasAttr(attributeKey)) {
/* 183*/        return ""; 
/*   0*/       }
/*   0*/    try {
/*   0*/      URL base;
/*   0*/      try {
/* 188*/        base = new URL(this.baseUri);
/* 189*/      } catch (MalformedURLException e) {
/* 191*/        URL uRL = new URL(relUrl);
/* 192*/        return uRL.toExternalForm();
/*   0*/      } 
/* 195*/      if (relUrl.startsWith("?")) {
/* 196*/          relUrl = base.getPath() + relUrl; 
/*   0*/         }
/* 197*/      URL abs = new URL(base, relUrl);
/* 198*/      return abs.toExternalForm();
/* 199*/    } catch (MalformedURLException e) {
/* 200*/      return "";
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Node childNode(int index) {
/* 211*/    return this.childNodes.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public List<Node> childNodes() {
/* 220*/    return Collections.unmodifiableList(this.childNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public List<Node> childNodesCopy() {
/* 229*/    List<Node> children = new ArrayList<Node>(this.childNodes.size());
/* 230*/    for (Node node : this.childNodes) {
/* 231*/        children.add(node.clone()); 
/*   0*/       }
/* 233*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public final int childNodeSize() {
/* 241*/    return this.childNodes.size();
/*   0*/  }
/*   0*/  
/*   0*/  protected Node[] childNodesAsArray() {
/* 245*/    return this.childNodes.<Node>toArray(new Node[childNodeSize()]);
/*   0*/  }
/*   0*/  
/*   0*/  public Node parent() {
/* 253*/    return this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public final Node parentNode() {
/* 261*/    return this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public Document ownerDocument() {
/* 269*/    if (this instanceof Document) {
/* 270*/        return (Document)this; 
/*   0*/       }
/* 271*/    if (this.parentNode == null) {
/* 272*/        return null; 
/*   0*/       }
/* 274*/    return this.parentNode.ownerDocument();
/*   0*/  }
/*   0*/  
/*   0*/  public void remove() {
/* 281*/    Validate.notNull(this.parentNode);
/* 282*/    this.parentNode.removeChild(this);
/*   0*/  }
/*   0*/  
/*   0*/  public Node before(String html) {
/* 292*/    addSiblingHtml(this.siblingIndex, html);
/* 293*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node before(Node node) {
/* 303*/    Validate.notNull(node);
/* 304*/    Validate.notNull(this.parentNode);
/* 306*/    this.parentNode.addChildren(this.siblingIndex, new Node[] { node });
/* 307*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node after(String html) {
/* 317*/    addSiblingHtml(this.siblingIndex + 1, html);
/* 318*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node after(Node node) {
/* 328*/    Validate.notNull(node);
/* 329*/    Validate.notNull(this.parentNode);
/* 331*/    this.parentNode.addChildren(this.siblingIndex + 1, new Node[] { node });
/* 332*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  private void addSiblingHtml(int index, String html) {
/* 336*/    Validate.notNull(html);
/* 337*/    Validate.notNull(this.parentNode);
/* 339*/    Element context = (parent() instanceof Element) ? (Element)parent() : null;
/* 340*/    List<Node> nodes = Parser.parseFragment(html, context, baseUri());
/* 341*/    this.parentNode.addChildren(index, nodes.<Node>toArray(new Node[nodes.size()]));
/*   0*/  }
/*   0*/  
/*   0*/  public Node wrap(String html) {
/* 350*/    Validate.notEmpty(html);
/* 352*/    Element context = (parent() instanceof Element) ? (Element)parent() : null;
/* 353*/    List<Node> wrapChildren = Parser.parseFragment(html, context, baseUri());
/* 354*/    Node wrapNode = wrapChildren.get(0);
/* 355*/    if (wrapNode == null || !(wrapNode instanceof Element)) {
/* 356*/        return null; 
/*   0*/       }
/* 358*/    Element wrap = (Element)wrapNode;
/* 359*/    Element deepest = getDeepChild(wrap);
/* 360*/    this.parentNode.replaceChild(this, wrap);
/* 361*/    deepest.addChildren(new Node[] { this });
/* 364*/    if (wrapChildren.size() > 0) {
/* 365*/        for (int i = 0; i < wrapChildren.size(); i++) {
/* 366*/          Node remainder = wrapChildren.get(i);
/* 367*/          remainder.parentNode.removeChild(remainder);
/* 368*/          wrap.appendChild(remainder);
/*   0*/        }  
/*   0*/       }
/* 371*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node unwrap() {
/* 390*/    Validate.notNull(this.parentNode);
/* 392*/    Node firstChild = (this.childNodes.size() > 0) ? this.childNodes.get(0) : null;
/* 393*/    this.parentNode.addChildren(this.siblingIndex, childNodesAsArray());
/* 394*/    remove();
/* 396*/    return firstChild;
/*   0*/  }
/*   0*/  
/*   0*/  private Element getDeepChild(Element el) {
/* 400*/    List<Element> children = el.children();
/* 401*/    if (children.size() > 0) {
/* 402*/        return getDeepChild(children.get(0)); 
/*   0*/       }
/* 404*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceWith(Node in) {
/* 412*/    Validate.notNull(in);
/* 413*/    Validate.notNull(this.parentNode);
/* 414*/    this.parentNode.replaceChild(this, in);
/*   0*/  }
/*   0*/  
/*   0*/  protected void setParentNode(Node parentNode) {
/* 418*/    if (this.parentNode != null) {
/* 419*/        this.parentNode.removeChild(this); 
/*   0*/       }
/* 420*/    this.parentNode = parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  protected void replaceChild(Node out, Node in) {
/* 424*/    Validate.isTrue((out.parentNode == this));
/* 425*/    Validate.notNull(in);
/* 426*/    if (in.parentNode != null) {
/* 427*/        in.parentNode.removeChild(in); 
/*   0*/       }
/* 429*/    int index = out.siblingIndex;
/* 430*/    this.childNodes.set(index, in);
/* 431*/    in.parentNode = this;
/* 432*/    in.setSiblingIndex(index);
/* 433*/    out.parentNode = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected void removeChild(Node out) {
/* 437*/    Validate.isTrue((out.parentNode == this));
/* 438*/    int index = out.siblingIndex;
/* 439*/    this.childNodes.remove(index);
/* 440*/    reindexChildren(index);
/* 441*/    out.parentNode = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected void addChildren(Node... children) {
/* 446*/    for (Node child : children) {
/* 447*/      reparentChild(child);
/* 448*/      this.childNodes.add(child);
/* 449*/      child.setSiblingIndex(this.childNodes.size() - 1);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void addChildren(int index, Node... children) {
/* 454*/    Validate.noNullElements((Object[])children);
/* 455*/    for (int i = children.length - 1; i >= 0; i--) {
/* 456*/      Node in = children[i];
/* 457*/      reparentChild(in);
/* 458*/      this.childNodes.add(index, in);
/*   0*/    } 
/* 460*/    reindexChildren(index);
/*   0*/  }
/*   0*/  
/*   0*/  protected void reparentChild(Node child) {
/* 464*/    if (child.parentNode != null) {
/* 465*/        child.parentNode.removeChild(child); 
/*   0*/       }
/* 466*/    child.setParentNode(this);
/*   0*/  }
/*   0*/  
/*   0*/  private void reindexChildren(int start) {
/* 470*/    for (int i = start; i < this.childNodes.size(); i++) {
/* 471*/        ((Node)this.childNodes.get(i)).setSiblingIndex(i); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public List<Node> siblingNodes() {
/* 481*/    if (this.parentNode == null) {
/* 482*/        return Collections.emptyList(); 
/*   0*/       }
/* 484*/    List<Node> nodes = this.parentNode.childNodes;
/* 485*/    List<Node> siblings = new ArrayList<Node>(nodes.size() - 1);
/* 486*/    for (Node node : nodes) {
/* 487*/      if (node != this) {
/* 488*/          siblings.add(node); 
/*   0*/         }
/*   0*/    } 
/* 489*/    return siblings;
/*   0*/  }
/*   0*/  
/*   0*/  public Node nextSibling() {
/* 497*/    if (this.parentNode == null) {
/* 498*/        return null; 
/*   0*/       }
/* 500*/    List<Node> siblings = this.parentNode.childNodes;
/* 501*/    int index = this.siblingIndex + 1;
/* 502*/    if (siblings.size() > index) {
/* 503*/        return siblings.get(index); 
/*   0*/       }
/* 505*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node previousSibling() {
/* 513*/    if (this.parentNode == null) {
/* 514*/        return null; 
/*   0*/       }
/* 516*/    if (this.siblingIndex > 0) {
/* 517*/        return this.parentNode.childNodes.get(this.siblingIndex - 1); 
/*   0*/       }
/* 519*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public int siblingIndex() {
/* 529*/    return this.siblingIndex;
/*   0*/  }
/*   0*/  
/*   0*/  protected void setSiblingIndex(int siblingIndex) {
/* 533*/    this.siblingIndex = siblingIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public Node traverse(NodeVisitor nodeVisitor) {
/* 542*/    Validate.notNull(nodeVisitor);
/* 543*/    NodeTraversor traversor = new NodeTraversor(nodeVisitor);
/* 544*/    traversor.traverse(this);
/* 545*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String outerHtml() {
/* 553*/    StringBuilder accum = new StringBuilder(128);
/* 554*/    outerHtml(accum);
/* 555*/    return accum.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected void outerHtml(StringBuilder accum) {
/* 559*/    new NodeTraversor(new OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
/*   0*/  }
/*   0*/  
/*   0*/  Document.OutputSettings getOutputSettings() {
/* 564*/    return (ownerDocument() != null) ? ownerDocument().outputSettings() : new Document("").outputSettings();
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 577*/    return outerHtml();
/*   0*/  }
/*   0*/  
/*   0*/  protected void indent(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 581*/    accum.append("\n").append(StringUtil.padding(depth * out.indentAmount()));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 592*/    if (this == o) {
/* 592*/        return true; 
/*   0*/       }
/* 593*/    if (o == null || getClass() != o.getClass()) {
/* 593*/        return false; 
/*   0*/       }
/* 595*/    Node node = (Node)o;
/* 597*/    if ((this.childNodes != null) ? !this.childNodes.equals(node.childNodes) : (node.childNodes != null)) {
/* 597*/        return false; 
/*   0*/       }
/* 598*/    if ((this.attributes != null) ? !this.attributes.equals(node.attributes) : (node.attributes != null)) {
/* 598*/        return false; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 610*/    int result = (this.childNodes != null) ? this.childNodes.hashCode() : 0;
/* 611*/    result = 31 * result + ((this.attributes != null) ? this.attributes.hashCode() : 0);
/* 612*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Node clone() {
/* 625*/    Node thisClone = doClone(null);
/* 628*/    LinkedList<Node> nodesToProcess = new LinkedList<Node>();
/* 629*/    nodesToProcess.add(thisClone);
/* 631*/    while (!nodesToProcess.isEmpty()) {
/* 632*/      Node currParent = nodesToProcess.remove();
/* 634*/      for (int i = 0; i < currParent.childNodes.size(); i++) {
/* 635*/        Node childClone = ((Node)currParent.childNodes.get(i)).doClone(currParent);
/* 636*/        currParent.childNodes.set(i, childClone);
/* 637*/        nodesToProcess.add(childClone);
/*   0*/      } 
/*   0*/    } 
/* 641*/    return thisClone;
/*   0*/  }
/*   0*/  
/*   0*/  protected Node doClone(Node parent) {
/*   0*/    Node clone;
/*   0*/    try {
/* 652*/      clone = (Node)super.clone();
/* 653*/    } catch (CloneNotSupportedException e) {
/* 654*/      throw new RuntimeException(e);
/*   0*/    } 
/* 657*/    clone.parentNode = parent;
/* 658*/    clone.siblingIndex = (parent == null) ? 0 : this.siblingIndex;
/* 659*/    clone.attributes = (this.attributes != null) ? this.attributes.clone() : null;
/* 660*/    clone.baseUri = this.baseUri;
/* 661*/    clone.childNodes = new ArrayList<Node>(this.childNodes.size());
/* 663*/    for (Node child : this.childNodes) {
/* 664*/        clone.childNodes.add(child); 
/*   0*/       }
/* 666*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract String nodeName();
/*   0*/  
/*   0*/  abstract void outerHtmlHead(StringBuilder paramStringBuilder, int paramInt, Document.OutputSettings paramOutputSettings);
/*   0*/  
/*   0*/  abstract void outerHtmlTail(StringBuilder paramStringBuilder, int paramInt, Document.OutputSettings paramOutputSettings);
/*   0*/  
/*   0*/  private static class OuterHtmlVisitor implements NodeVisitor {
/*   0*/    private StringBuilder accum;
/*   0*/    
/*   0*/    private Document.OutputSettings out;
/*   0*/    
/*   0*/    OuterHtmlVisitor(StringBuilder accum, Document.OutputSettings out) {
/* 674*/      this.accum = accum;
/* 675*/      this.out = out;
/*   0*/    }
/*   0*/    
/*   0*/    public void head(Node node, int depth) {
/* 679*/      node.outerHtmlHead(this.accum, depth, this.out);
/*   0*/    }
/*   0*/    
/*   0*/    public void tail(Node node, int depth) {
/* 683*/      if (!node.nodeName().equals("#text")) {
/* 684*/          node.outerHtmlTail(this.accum, depth, this.out); 
/*   0*/         }
/*   0*/    }
/*   0*/  }
/*   0*/}
