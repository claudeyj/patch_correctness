/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collections;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import org.jsoup.SerializationException;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.parser.Parser;
/*   0*/import org.jsoup.select.NodeTraversor;
/*   0*/import org.jsoup.select.NodeVisitor;
/*   0*/
/*   0*/public abstract class Node implements Cloneable {
/*  21*/  private static final List<Node> EMPTY_NODES = Collections.emptyList();
/*   0*/  
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
/*  34*/    Validate.notNull(baseUri);
/*  35*/    Validate.notNull(attributes);
/*  37*/    this.childNodes = EMPTY_NODES;
/*  38*/    this.baseUri = baseUri.trim();
/*  39*/    this.attributes = attributes;
/*   0*/  }
/*   0*/  
/*   0*/  protected Node(String baseUri) {
/*  43*/    this(baseUri, new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  protected Node() {
/*  50*/    this.childNodes = EMPTY_NODES;
/*  51*/    this.attributes = null;
/*   0*/  }
/*   0*/  
/*   0*/  public String attr(String attributeKey) {
/*  76*/    Validate.notNull(attributeKey);
/*  78*/    String val = this.attributes.getIgnoreCase(attributeKey);
/*  79*/    if (val.length() > 0) {
/*  80*/        return val; 
/*   0*/       }
/*  81*/    if (attributeKey.toLowerCase().startsWith("abs:")) {
/*  82*/        return absUrl(attributeKey.substring("abs:".length())); 
/*   0*/       }
/*  83*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public Attributes attributes() {
/*  91*/    return this.attributes;
/*   0*/  }
/*   0*/  
/*   0*/  public Node attr(String attributeKey, String attributeValue) {
/* 101*/    this.attributes.put(attributeKey, attributeValue);
/* 102*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasAttr(String attributeKey) {
/* 111*/    Validate.notNull(attributeKey);
/* 113*/    if (attributeKey.startsWith("abs:")) {
/* 114*/      String key = attributeKey.substring("abs:".length());
/* 115*/      if (this.attributes.hasKeyIgnoreCase(key) && !absUrl(key).equals("")) {
/* 116*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 118*/    return this.attributes.hasKeyIgnoreCase(attributeKey);
/*   0*/  }
/*   0*/  
/*   0*/  public Node removeAttr(String attributeKey) {
/* 127*/    Validate.notNull(attributeKey);
/* 128*/    this.attributes.removeIgnoreCase(attributeKey);
/* 129*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String baseUri() {
/* 137*/    return this.baseUri;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseUri(final String baseUri) {
/* 145*/    Validate.notNull(baseUri);
/* 147*/    traverse(new NodeVisitor() {
/*   0*/          public void head(Node node, int depth) {
/* 149*/            node.baseUri = baseUri;
/*   0*/          }
/*   0*/          
/*   0*/          public void tail(Node node, int depth) {}
/*   0*/        });
/*   0*/  }
/*   0*/  
/*   0*/  public String absUrl(String attributeKey) {
/* 181*/    Validate.notEmpty(attributeKey);
/* 183*/    if (!hasAttr(attributeKey)) {
/* 184*/        return ""; 
/*   0*/       }
/* 186*/    return StringUtil.resolve(this.baseUri, attr(attributeKey));
/*   0*/  }
/*   0*/  
/*   0*/  public Node childNode(int index) {
/* 196*/    return this.childNodes.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public List<Node> childNodes() {
/* 205*/    return Collections.unmodifiableList(this.childNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public List<Node> childNodesCopy() {
/* 214*/    List<Node> children = new ArrayList<Node>(this.childNodes.size());
/* 215*/    for (Node node : this.childNodes) {
/* 216*/        children.add(node.clone()); 
/*   0*/       }
/* 218*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  public final int childNodeSize() {
/* 226*/    return this.childNodes.size();
/*   0*/  }
/*   0*/  
/*   0*/  protected Node[] childNodesAsArray() {
/* 230*/    return this.childNodes.<Node>toArray(new Node[childNodeSize()]);
/*   0*/  }
/*   0*/  
/*   0*/  public Node parent() {
/* 238*/    return this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public final Node parentNode() {
/* 246*/    return this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public Document ownerDocument() {
/* 254*/    if (this instanceof Document) {
/* 255*/        return (Document)this; 
/*   0*/       }
/* 256*/    if (this.parentNode == null) {
/* 257*/        return null; 
/*   0*/       }
/* 259*/    return this.parentNode.ownerDocument();
/*   0*/  }
/*   0*/  
/*   0*/  public void remove() {
/* 266*/    Validate.notNull(this.parentNode);
/* 267*/    this.parentNode.removeChild(this);
/*   0*/  }
/*   0*/  
/*   0*/  public Node before(String html) {
/* 277*/    addSiblingHtml(this.siblingIndex, html);
/* 278*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node before(Node node) {
/* 288*/    Validate.notNull(node);
/* 289*/    Validate.notNull(this.parentNode);
/* 291*/    this.parentNode.addChildren(this.siblingIndex, new Node[] { node });
/* 292*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node after(String html) {
/* 302*/    addSiblingHtml(this.siblingIndex + 1, html);
/* 303*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node after(Node node) {
/* 313*/    Validate.notNull(node);
/* 314*/    Validate.notNull(this.parentNode);
/* 316*/    this.parentNode.addChildren(this.siblingIndex + 1, new Node[] { node });
/* 317*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  private void addSiblingHtml(int index, String html) {
/* 321*/    Validate.notNull(html);
/* 322*/    Validate.notNull(this.parentNode);
/* 324*/    Element context = (parent() instanceof Element) ? (Element)parent() : null;
/* 325*/    List<Node> nodes = Parser.parseFragment(html, context, baseUri());
/* 326*/    this.parentNode.addChildren(index, nodes.<Node>toArray(new Node[nodes.size()]));
/*   0*/  }
/*   0*/  
/*   0*/  public Node wrap(String html) {
/* 335*/    Validate.notEmpty(html);
/* 337*/    Element context = (parent() instanceof Element) ? (Element)parent() : null;
/* 338*/    List<Node> wrapChildren = Parser.parseFragment(html, context, baseUri());
/* 339*/    Node wrapNode = wrapChildren.get(0);
/* 340*/    if (wrapNode == null || !(wrapNode instanceof Element)) {
/* 341*/        return null; 
/*   0*/       }
/* 343*/    Element wrap = (Element)wrapNode;
/* 344*/    Element deepest = getDeepChild(wrap);
/* 345*/    this.parentNode.replaceChild(this, wrap);
/* 346*/    deepest.addChildren(new Node[] { this });
/* 349*/    if (wrapChildren.size() > 0) {
/* 350*/        for (int i = 0; i < wrapChildren.size(); i++) {
/* 351*/          Node remainder = wrapChildren.get(i);
/* 352*/          remainder.parentNode.removeChild(remainder);
/* 353*/          wrap.appendChild(remainder);
/*   0*/        }  
/*   0*/       }
/* 356*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Node unwrap() {
/* 375*/    Validate.notNull(this.parentNode);
/* 377*/    Node firstChild = (this.childNodes.size() > 0) ? this.childNodes.get(0) : null;
/* 378*/    this.parentNode.addChildren(this.siblingIndex, childNodesAsArray());
/* 379*/    remove();
/* 381*/    return firstChild;
/*   0*/  }
/*   0*/  
/*   0*/  private Element getDeepChild(Element el) {
/* 385*/    List<Element> children = el.children();
/* 386*/    if (children.size() > 0) {
/* 387*/        return getDeepChild(children.get(0)); 
/*   0*/       }
/* 389*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceWith(Node in) {
/* 397*/    Validate.notNull(in);
/* 398*/    Validate.notNull(this.parentNode);
/* 399*/    this.parentNode.replaceChild(this, in);
/*   0*/  }
/*   0*/  
/*   0*/  protected void setParentNode(Node parentNode) {
/* 403*/    if (this.parentNode != null) {
/* 404*/        this.parentNode.removeChild(this); 
/*   0*/       }
/* 405*/    this.parentNode = parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  protected void replaceChild(Node out, Node in) {
/* 409*/    Validate.isTrue((out.parentNode == this));
/* 410*/    Validate.notNull(in);
/* 411*/    if (in.parentNode != null) {
/* 412*/        in.parentNode.removeChild(in); 
/*   0*/       }
/* 414*/    int index = out.siblingIndex;
/* 415*/    this.childNodes.set(index, in);
/* 416*/    in.parentNode = this;
/* 417*/    in.setSiblingIndex(index);
/* 418*/    out.parentNode = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected void removeChild(Node out) {
/* 422*/    Validate.isTrue((out.parentNode == this));
/* 423*/    int index = out.siblingIndex;
/* 424*/    this.childNodes.remove(index);
/* 425*/    reindexChildren(index);
/* 426*/    out.parentNode = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected void addChildren(Node... children) {
/* 431*/    for (Node child : children) {
/* 432*/      reparentChild(child);
/* 433*/      ensureChildNodes();
/* 434*/      this.childNodes.add(child);
/* 435*/      child.setSiblingIndex(this.childNodes.size() - 1);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void addChildren(int index, Node... children) {
/* 440*/    Validate.noNullElements((Object[])children);
/* 441*/    ensureChildNodes();
/* 442*/    for (int i = children.length - 1; i >= 0; i--) {
/* 443*/      Node in = children[i];
/* 444*/      reparentChild(in);
/* 445*/      this.childNodes.add(index, in);
/* 446*/      reindexChildren(index);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void ensureChildNodes() {
/* 451*/    if (this.childNodes == EMPTY_NODES) {
/* 452*/        this.childNodes = new ArrayList<Node>(4); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void reparentChild(Node child) {
/* 457*/    if (child.parentNode != null) {
/* 458*/        child.parentNode.removeChild(child); 
/*   0*/       }
/* 459*/    child.setParentNode(this);
/*   0*/  }
/*   0*/  
/*   0*/  private void reindexChildren(int start) {
/* 463*/    for (int i = start; i < this.childNodes.size(); i++) {
/* 464*/        ((Node)this.childNodes.get(i)).setSiblingIndex(i); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public List<Node> siblingNodes() {
/* 474*/    if (this.parentNode == null) {
/* 475*/        return Collections.emptyList(); 
/*   0*/       }
/* 477*/    List<Node> nodes = this.parentNode.childNodes;
/* 478*/    List<Node> siblings = new ArrayList<Node>(nodes.size() - 1);
/* 479*/    for (Node node : nodes) {
/* 480*/      if (node != this) {
/* 481*/          siblings.add(node); 
/*   0*/         }
/*   0*/    } 
/* 482*/    return siblings;
/*   0*/  }
/*   0*/  
/*   0*/  public Node nextSibling() {
/* 490*/    if (this.parentNode == null) {
/* 491*/        return null; 
/*   0*/       }
/* 493*/    List<Node> siblings = this.parentNode.childNodes;
/* 494*/    int index = this.siblingIndex + 1;
/* 495*/    if (siblings.size() > index) {
/* 496*/        return siblings.get(index); 
/*   0*/       }
/* 498*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Node previousSibling() {
/* 506*/    if (this.parentNode == null) {
/* 507*/        return null; 
/*   0*/       }
/* 509*/    if (this.siblingIndex > 0) {
/* 510*/        return this.parentNode.childNodes.get(this.siblingIndex - 1); 
/*   0*/       }
/* 512*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public int siblingIndex() {
/* 522*/    return this.siblingIndex;
/*   0*/  }
/*   0*/  
/*   0*/  protected void setSiblingIndex(int siblingIndex) {
/* 526*/    this.siblingIndex = siblingIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public Node traverse(NodeVisitor nodeVisitor) {
/* 535*/    Validate.notNull(nodeVisitor);
/* 536*/    NodeTraversor traversor = new NodeTraversor(nodeVisitor);
/* 537*/    traversor.traverse(this);
/* 538*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String outerHtml() {
/* 546*/    StringBuilder accum = new StringBuilder(128);
/* 547*/    outerHtml(accum);
/* 548*/    return accum.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected void outerHtml(Appendable accum) {
/* 552*/    new NodeTraversor(new OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
/*   0*/  }
/*   0*/  
/*   0*/  Document.OutputSettings getOutputSettings() {
/* 557*/    return (ownerDocument() != null) ? ownerDocument().outputSettings() : new Document("").outputSettings();
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends Appendable> T html(T appendable) {
/* 576*/    outerHtml((Appendable)appendable);
/* 577*/    return appendable;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 581*/    return outerHtml();
/*   0*/  }
/*   0*/  
/*   0*/  protected void indent(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
/* 585*/    accum.append("\n").append(StringUtil.padding(depth * out.indentAmount()));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 597*/    return (this == o);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasSameValue(Object o) {
/* 608*/    if (this == o) {
/* 608*/        return true; 
/*   0*/       }
/* 609*/    if (o == null || getClass() != o.getClass()) {
/* 609*/        return false; 
/*   0*/       }
/* 611*/    return outerHtml().equals(((Node)o).outerHtml());
/*   0*/  }
/*   0*/  
/*   0*/  public Node clone() {
/* 624*/    Node thisClone = doClone(null);
/* 627*/    LinkedList<Node> nodesToProcess = new LinkedList<Node>();
/* 628*/    nodesToProcess.add(thisClone);
/* 630*/    while (!nodesToProcess.isEmpty()) {
/* 631*/      Node currParent = nodesToProcess.remove();
/* 633*/      for (int i = 0; i < currParent.childNodes.size(); i++) {
/* 634*/        Node childClone = ((Node)currParent.childNodes.get(i)).doClone(currParent);
/* 635*/        currParent.childNodes.set(i, childClone);
/* 636*/        nodesToProcess.add(childClone);
/*   0*/      } 
/*   0*/    } 
/* 640*/    return thisClone;
/*   0*/  }
/*   0*/  
/*   0*/  protected Node doClone(Node parent) {
/*   0*/    Node clone;
/*   0*/    try {
/* 651*/      clone = (Node)super.clone();
/* 652*/    } catch (CloneNotSupportedException e) {
/* 653*/      throw new RuntimeException(e);
/*   0*/    } 
/* 656*/    clone.parentNode = parent;
/* 657*/    clone.siblingIndex = (parent == null) ? 0 : this.siblingIndex;
/* 658*/    clone.attributes = (this.attributes != null) ? this.attributes.clone() : null;
/* 659*/    clone.baseUri = this.baseUri;
/* 660*/    clone.childNodes = new ArrayList<Node>(this.childNodes.size());
/* 662*/    for (Node child : this.childNodes) {
/* 663*/        clone.childNodes.add(child); 
/*   0*/       }
/* 665*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract String nodeName();
/*   0*/  
/*   0*/  abstract void outerHtmlHead(Appendable paramAppendable, int paramInt, Document.OutputSettings paramOutputSettings) throws IOException;
/*   0*/  
/*   0*/  abstract void outerHtmlTail(Appendable paramAppendable, int paramInt, Document.OutputSettings paramOutputSettings) throws IOException;
/*   0*/  
/*   0*/  private static class OuterHtmlVisitor implements NodeVisitor {
/*   0*/    private Appendable accum;
/*   0*/    
/*   0*/    private Document.OutputSettings out;
/*   0*/    
/*   0*/    OuterHtmlVisitor(Appendable accum, Document.OutputSettings out) {
/* 673*/      this.accum = accum;
/* 674*/      this.out = out;
/*   0*/    }
/*   0*/    
/*   0*/    public void head(Node node, int depth) {
/*   0*/      try {
/* 679*/        node.outerHtmlHead(this.accum, depth, this.out);
/* 680*/      } catch (IOException exception) {
/* 681*/        throw new SerializationException(exception);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void tail(Node node, int depth) {
/* 686*/      if (!node.nodeName().equals("#text")) {
/*   0*/          try {
/* 688*/            node.outerHtmlTail(this.accum, depth, this.out);
/* 689*/          } catch (IOException exception) {
/* 690*/            throw new SerializationException(exception);
/*   0*/          }  
/*   0*/         }
/*   0*/    }
/*   0*/  }
/*   0*/}
