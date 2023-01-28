/*   0*/package org.apache.commons.jxpath.ri.model.dom;
/*   0*/
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import org.apache.commons.jxpath.JXPathAbstractFactoryException;
/*   0*/import org.apache.commons.jxpath.JXPathContext;
/*   0*/import org.apache.commons.jxpath.JXPathException;
/*   0*/import org.apache.commons.jxpath.Pointer;
/*   0*/import org.apache.commons.jxpath.ri.NamespaceResolver;
/*   0*/import org.apache.commons.jxpath.ri.QName;
/*   0*/import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
/*   0*/import org.apache.commons.jxpath.ri.compiler.NodeTest;
/*   0*/import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
/*   0*/import org.apache.commons.jxpath.ri.compiler.ProcessingInstructionTest;
/*   0*/import org.apache.commons.jxpath.ri.model.NodeIterator;
/*   0*/import org.apache.commons.jxpath.ri.model.NodePointer;
/*   0*/import org.apache.commons.jxpath.ri.model.beans.NullPointer;
/*   0*/import org.apache.commons.jxpath.util.TypeUtils;
/*   0*/import org.w3c.dom.Attr;
/*   0*/import org.w3c.dom.Comment;
/*   0*/import org.w3c.dom.Document;
/*   0*/import org.w3c.dom.Element;
/*   0*/import org.w3c.dom.NamedNodeMap;
/*   0*/import org.w3c.dom.Node;
/*   0*/import org.w3c.dom.NodeList;
/*   0*/import org.w3c.dom.ProcessingInstruction;
/*   0*/
/*   0*/public class DOMNodePointer extends NodePointer {
/*   0*/  private static final long serialVersionUID = -8751046933894857319L;
/*   0*/  
/*   0*/  private Node node;
/*   0*/  
/*   0*/  private Map namespaces;
/*   0*/  
/*   0*/  private String defaultNamespace;
/*   0*/  
/*   0*/  private String id;
/*   0*/  
/*   0*/  private NamespaceResolver localNamespaceResolver;
/*   0*/  
/*   0*/  public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
/*   0*/  
/*   0*/  public static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
/*   0*/  
/*   0*/  public DOMNodePointer(Node node, Locale locale) {
/*  78*/    super(null, locale);
/*  79*/    this.node = node;
/*   0*/  }
/*   0*/  
/*   0*/  public DOMNodePointer(Node node, Locale locale, String id) {
/*  89*/    super(null, locale);
/*  90*/    this.node = node;
/*  91*/    this.id = id;
/*   0*/  }
/*   0*/  
/*   0*/  public DOMNodePointer(NodePointer parent, Node node) {
/* 100*/    super(parent);
/* 101*/    this.node = node;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean testNode(NodeTest test) {
/* 105*/    return testNode(this.node, test);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean testNode(Node node, NodeTest test) {
/* 115*/    if (test == null) {
/* 116*/        return true; 
/*   0*/       }
/* 118*/    if (test instanceof NodeNameTest) {
/* 119*/      if (node.getNodeType() != 1) {
/* 120*/          return false; 
/*   0*/         }
/* 123*/      NodeNameTest nodeNameTest = (NodeNameTest)test;
/* 124*/      QName testName = nodeNameTest.getNodeName();
/* 125*/      String namespaceURI = nodeNameTest.getNamespaceURI();
/* 126*/      boolean wildcard = nodeNameTest.isWildcard();
/* 127*/      String testPrefix = testName.getPrefix();
/* 128*/      if (wildcard && testPrefix == null) {
/* 129*/          return true; 
/*   0*/         }
/* 131*/      if (wildcard || testName.getName().equals(getLocalName(node))) {
/* 134*/        String nodeNS = getNamespaceURI(node);
/* 135*/        return (equalStrings(namespaceURI, nodeNS) || (nodeNS == null && equalStrings(testPrefix, getPrefix(node))));
/*   0*/      } 
/* 138*/      return false;
/*   0*/    } 
/* 140*/    if (test instanceof NodeTypeTest) {
/* 141*/      int nodeType = node.getNodeType();
/* 142*/      switch (((NodeTypeTest)test).getNodeType()) {
/*   0*/        case 1:
/* 144*/          return true;
/*   0*/        case 2:
/* 146*/          return (nodeType == 4 || nodeType == 3);
/*   0*/        case 3:
/* 149*/          return (nodeType == 8);
/*   0*/        case 4:
/* 151*/          return (nodeType == 7);
/*   0*/      } 
/* 153*/      return false;
/*   0*/    } 
/* 156*/    if (test instanceof ProcessingInstructionTest && node.getNodeType() == 7) {
/* 158*/      String testPI = ((ProcessingInstructionTest)test).getTarget();
/* 159*/      String nodePI = ((ProcessingInstruction)node).getTarget();
/* 160*/      return testPI.equals(nodePI);
/*   0*/    } 
/* 162*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean equalStrings(String s1, String s2) {
/* 172*/    if (s1 == s2) {
/* 173*/        return true; 
/*   0*/       }
/* 175*/    s1 = (s1 == null) ? "" : s1.trim();
/* 176*/    s2 = (s2 == null) ? "" : s2.trim();
/* 177*/    return s1.equals(s2);
/*   0*/  }
/*   0*/  
/*   0*/  public QName getName() {
/* 181*/    String ln = null;
/* 182*/    String ns = null;
/* 183*/    int type = this.node.getNodeType();
/* 184*/    if (type == 1) {
/* 185*/      ns = getPrefix(this.node);
/* 186*/      ln = getLocalName(this.node);
/* 188*/    } else if (type == 7) {
/* 189*/      ln = ((ProcessingInstruction)this.node).getTarget();
/*   0*/    } 
/* 191*/    return new QName(ns, ln);
/*   0*/  }
/*   0*/  
/*   0*/  public String getNamespaceURI() {
/* 195*/    return getNamespaceURI(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
/* 200*/    return new DOMNodeIterator(this, test, reverse, startWith);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator attributeIterator(QName name) {
/* 204*/    return new DOMAttributeIterator(this, name);
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer namespacePointer(String prefix) {
/* 208*/    return new NamespacePointer(this, prefix);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator namespaceIterator() {
/* 212*/    return new DOMNamespaceIterator(this);
/*   0*/  }
/*   0*/  
/*   0*/  public synchronized NamespaceResolver getNamespaceResolver() {
/* 216*/    if (this.localNamespaceResolver == null) {
/* 217*/      this.localNamespaceResolver = new NamespaceResolver(super.getNamespaceResolver());
/* 218*/      this.localNamespaceResolver.setNamespaceContextPointer(this);
/*   0*/    } 
/* 220*/    return this.localNamespaceResolver;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNamespaceURI(String prefix) {
/* 224*/    if (prefix == null || prefix.equals("")) {
/* 225*/        return getDefaultNamespaceURI(); 
/*   0*/       }
/* 228*/    if (prefix.equals("xml")) {
/* 229*/        return "http://www.w3.org/XML/1998/namespace"; 
/*   0*/       }
/* 232*/    if (prefix.equals("xmlns")) {
/* 233*/        return "http://www.w3.org/2000/xmlns/"; 
/*   0*/       }
/* 236*/    String namespace = null;
/* 237*/    if (this.namespaces == null) {
/* 238*/      this.namespaces = new HashMap();
/*   0*/    } else {
/* 241*/      namespace = (String)this.namespaces.get(prefix);
/*   0*/    } 
/* 244*/    if (namespace == null) {
/* 245*/      String qname = "xmlns:" + prefix;
/* 246*/      Node aNode = this.node;
/* 247*/      if (aNode instanceof Document) {
/* 248*/          aNode = ((Document)aNode).getDocumentElement(); 
/*   0*/         }
/* 250*/      while (aNode != null) {
/* 251*/        if (aNode.getNodeType() == 1) {
/* 252*/          Attr attr = ((Element)aNode).getAttributeNode(qname);
/* 253*/          if (attr != null) {
/* 254*/            namespace = attr.getValue();
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/* 258*/        aNode = aNode.getParentNode();
/*   0*/      } 
/* 260*/      if (namespace == null || namespace.equals("")) {
/* 261*/          namespace = "<<unknown namespace>>"; 
/*   0*/         }
/*   0*/    } 
/* 265*/    this.namespaces.put(prefix, namespace);
/* 266*/    if (namespace == "<<unknown namespace>>") {
/* 267*/        return null; 
/*   0*/       }
/* 271*/    return namespace;
/*   0*/  }
/*   0*/  
/*   0*/  public String getDefaultNamespaceURI() {
/* 275*/    if (this.defaultNamespace == null) {
/* 276*/      Node aNode = this.node;
/* 277*/      if (aNode instanceof Document) {
/* 278*/          aNode = ((Document)aNode).getDocumentElement(); 
/*   0*/         }
/* 280*/      while (aNode != null) {
/* 281*/        if (aNode.getNodeType() == 1) {
/* 282*/          Attr attr = ((Element)aNode).getAttributeNode("xmlns");
/* 283*/          if (attr != null) {
/* 284*/            this.defaultNamespace = attr.getValue();
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/* 288*/        aNode = aNode.getParentNode();
/*   0*/      } 
/*   0*/    } 
/* 291*/    if (this.defaultNamespace == null) {
/* 292*/        this.defaultNamespace = ""; 
/*   0*/       }
/* 295*/    return this.defaultNamespace.equals("") ? null : this.defaultNamespace;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getBaseValue() {
/* 299*/    return this.node;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getImmediateNode() {
/* 303*/    return this.node;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isActual() {
/* 307*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCollection() {
/* 311*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/* 315*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLeaf() {
/* 319*/    return !this.node.hasChildNodes();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLanguage(String lang) {
/* 330*/    String current = getLanguage();
/* 331*/    return (current == null) ? super.isLanguage(lang) : current.toUpperCase(Locale.ENGLISH).startsWith(lang.toUpperCase(Locale.ENGLISH));
/*   0*/  }
/*   0*/  
/*   0*/  protected static String findEnclosingAttribute(Node n, String attrName) {
/* 343*/    while (n != null) {
/* 344*/      if (n.getNodeType() == 1) {
/* 345*/        Element e = (Element)n;
/* 346*/        String attr = e.getAttribute(attrName);
/* 347*/        if (attr != null && !attr.equals("")) {
/* 348*/            return attr; 
/*   0*/           }
/*   0*/      } 
/* 351*/      n = n.getParentNode();
/*   0*/    } 
/* 353*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected String getLanguage() {
/* 361*/    return findEnclosingAttribute(this.node, "xml:lang");
/*   0*/  }
/*   0*/  
/*   0*/  public void setValue(Object value) {
/* 372*/    if (this.node.getNodeType() == 3 || this.node.getNodeType() == 4) {
/* 374*/      String string = (String)TypeUtils.convert(value, String.class);
/* 375*/      if (string != null && !string.equals("")) {
/* 376*/        this.node.setNodeValue(string);
/*   0*/      } else {
/* 379*/        this.node.getParentNode().removeChild(this.node);
/*   0*/      } 
/*   0*/    } else {
/* 383*/      NodeList children = this.node.getChildNodes();
/* 384*/      int count = children.getLength();
/* 385*/      for (int i = count; --i >= 0; ) {
/* 386*/        Node child = children.item(i);
/* 387*/        this.node.removeChild(child);
/*   0*/      } 
/* 390*/      if (value instanceof Node) {
/* 391*/        Node valueNode = (Node)value;
/* 392*/        if (valueNode instanceof Element || valueNode instanceof Document) {
/* 394*/          children = valueNode.getChildNodes();
/* 395*/          for (int j = 0; j < children.getLength(); j++) {
/* 396*/            Node child = children.item(j);
/* 397*/            this.node.appendChild(child.cloneNode(true));
/*   0*/          } 
/*   0*/        } else {
/* 401*/          this.node.appendChild(valueNode.cloneNode(true));
/*   0*/        } 
/*   0*/      } else {
/* 405*/        String string = (String)TypeUtils.convert(value, String.class);
/* 406*/        if (string != null && !string.equals("")) {
/* 407*/          Node textNode = this.node.getOwnerDocument().createTextNode(string);
/* 409*/          this.node.appendChild(textNode);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer createChild(JXPathContext context, QName name, int index) {
/* 416*/    if (index == Integer.MIN_VALUE) {
/* 417*/        index = 0; 
/*   0*/       }
/* 419*/    boolean success = getAbstractFactory(context).createObject(context, this, this.node, name.toString(), index);
/* 426*/    if (success) {
/* 428*/      String prefix = name.getPrefix();
/* 429*/      String namespaceURI = (prefix == null) ? null : context.getNamespaceURI(prefix);
/* 431*/      NodeTest nodeTest = new NodeNameTest(name, namespaceURI);
/* 433*/      NodeIterator it = childIterator(nodeTest, false, null);
/* 434*/      if (it != null && it.setPosition(index + 1)) {
/* 435*/          return it.getNodePointer(); 
/*   0*/         }
/*   0*/    } 
/* 438*/    throw new JXPathAbstractFactoryException("Factory could not create a child node for path: " + asPath() + "/" + name + "[" + (index + 1) + "]");
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer createChild(JXPathContext context, QName name, int index, Object value) {
/* 445*/    NodePointer ptr = createChild(context, name, index);
/* 446*/    ptr.setValue(value);
/* 447*/    return ptr;
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer createAttribute(JXPathContext context, QName name) {
/* 451*/    if (!(this.node instanceof Element)) {
/* 452*/        return super.createAttribute(context, name); 
/*   0*/       }
/* 454*/    Element element = (Element)this.node;
/* 455*/    String prefix = name.getPrefix();
/* 456*/    if (prefix != null) {
/* 457*/      String ns = null;
/* 458*/      NamespaceResolver nsr = getNamespaceResolver();
/* 459*/      if (nsr != null) {
/* 460*/          ns = nsr.getNamespaceURI(prefix); 
/*   0*/         }
/* 462*/      if (ns == null) {
/* 463*/          throw new JXPathException("Unknown namespace prefix: " + prefix); 
/*   0*/         }
/* 466*/      element.setAttributeNS(ns, name.toString(), "");
/* 469*/    } else if (!element.hasAttribute(name.getName())) {
/* 470*/      element.setAttribute(name.getName(), "");
/*   0*/    } 
/* 473*/    NodeIterator it = attributeIterator(name);
/* 474*/    it.setPosition(1);
/* 475*/    return it.getNodePointer();
/*   0*/  }
/*   0*/  
/*   0*/  public void remove() {
/* 479*/    Node parent = this.node.getParentNode();
/* 480*/    if (parent == null) {
/* 481*/        throw new JXPathException("Cannot remove root DOM node"); 
/*   0*/       }
/* 483*/    parent.removeChild(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  public String asPath() {
/* 487*/    if (this.id != null) {
/* 488*/        return "id('" + escape(this.id) + "')"; 
/*   0*/       }
/* 491*/    StringBuffer buffer = new StringBuffer();
/* 492*/    if (this.parent != null) {
/* 493*/        buffer.append(this.parent.asPath()); 
/*   0*/       }
/* 495*/    switch (this.node.getNodeType()) {
/*   0*/      case 1:
/* 500*/        if (this.parent instanceof DOMNodePointer) {
/* 501*/          if (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '/') {
/* 503*/              buffer.append('/'); 
/*   0*/             }
/* 505*/          String ln = getLocalName(this.node);
/* 506*/          String nsURI = getNamespaceURI();
/* 507*/          if (nsURI == null) {
/* 508*/            buffer.append(ln);
/* 509*/            buffer.append('[');
/* 510*/            buffer.append(getRelativePositionByQName()).append(']');
/*   0*/            break;
/*   0*/          } 
/* 513*/          String prefix = getNamespaceResolver().getPrefix(nsURI);
/* 514*/          if (prefix != null) {
/* 515*/            buffer.append(prefix);
/* 516*/            buffer.append(':');
/* 517*/            buffer.append(ln);
/* 518*/            buffer.append('[');
/* 519*/            buffer.append(getRelativePositionByQName());
/* 520*/            buffer.append(']');
/*   0*/            break;
/*   0*/          } 
/* 523*/          buffer.append("node()");
/* 524*/          buffer.append('[');
/* 525*/          buffer.append(getRelativePositionOfElement());
/* 526*/          buffer.append(']');
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 3:
/*   0*/      case 4:
/* 533*/        buffer.append("/text()");
/* 534*/        buffer.append('[');
/* 535*/        buffer.append(getRelativePositionOfTextNode()).append(']');
/*   0*/        break;
/*   0*/      case 7:
/* 538*/        buffer.append("/processing-instruction('");
/* 539*/        buffer.append(((ProcessingInstruction)this.node).getTarget()).append("')");
/* 540*/        buffer.append('[');
/* 541*/        buffer.append(getRelativePositionOfPI()).append(']');
/*   0*/        break;
/*   0*/    } 
/* 549*/    return buffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionByQName() {
/* 557*/    int count = 1;
/* 558*/    Node n = this.node.getPreviousSibling();
/* 559*/    while (n != null) {
/* 560*/      if (n.getNodeType() == 1 && matchesQName(n)) {
/* 561*/          count++; 
/*   0*/         }
/* 563*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 565*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean matchesQName(Node n) {
/* 569*/    if (getNamespaceURI() != null) {
/* 570*/        return (equalStrings(getNamespaceURI(n), getNamespaceURI()) && equalStrings(this.node.getLocalName(), n.getLocalName())); 
/*   0*/       }
/* 573*/    return equalStrings(this.node.getNodeName(), n.getNodeName());
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionOfElement() {
/* 581*/    int count = 1;
/* 582*/    Node n = this.node.getPreviousSibling();
/* 583*/    while (n != null) {
/* 584*/      if (n.getNodeType() == 1) {
/* 585*/          count++; 
/*   0*/         }
/* 587*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 589*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionOfTextNode() {
/* 597*/    int count = 1;
/* 598*/    Node n = this.node.getPreviousSibling();
/* 599*/    while (n != null) {
/* 600*/      if (n.getNodeType() == 3 || n.getNodeType() == 4) {
/* 602*/          count++; 
/*   0*/         }
/* 604*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 606*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionOfPI() {
/* 614*/    int count = 1;
/* 615*/    String target = ((ProcessingInstruction)this.node).getTarget();
/* 616*/    Node n = this.node.getPreviousSibling();
/* 617*/    while (n != null) {
/* 618*/      if (n.getNodeType() == 7 && ((ProcessingInstruction)n).getTarget().equals(target)) {
/* 620*/          count++; 
/*   0*/         }
/* 622*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 624*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 628*/    return this.node.hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object object) {
/* 632*/    return (object == this || (object instanceof DOMNodePointer && this.node == ((DOMNodePointer)object).node));
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPrefix(Node node) {
/* 641*/    String prefix = node.getPrefix();
/* 642*/    if (prefix != null) {
/* 643*/        return prefix; 
/*   0*/       }
/* 646*/    String name = node.getNodeName();
/* 647*/    int index = name.lastIndexOf(':');
/* 648*/    return (index < 0) ? null : name.substring(0, index);
/*   0*/  }
/*   0*/  
/*   0*/  public static String getLocalName(Node node) {
/* 657*/    String localName = node.getLocalName();
/* 658*/    if (localName != null) {
/* 659*/        return localName; 
/*   0*/       }
/* 662*/    String name = node.getNodeName();
/* 663*/    int index = name.lastIndexOf(':');
/* 664*/    return (index < 0) ? name : name.substring(index + 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String getNamespaceURI(Node node) {
/* 673*/    if (node instanceof Document) {
/* 674*/        node = ((Document)node).getDocumentElement(); 
/*   0*/       }
/* 677*/    Element element = (Element)node;
/* 679*/    String uri = element.getNamespaceURI();
/* 680*/    if (uri == null) {
/* 681*/      String prefix = getPrefix(node);
/* 682*/      String qname = (prefix == null) ? "xmlns" : ("xmlns:" + prefix);
/* 684*/      Node aNode = node;
/* 685*/      while (aNode != null) {
/* 686*/        if (aNode.getNodeType() == 1) {
/* 687*/          Attr attr = ((Element)aNode).getAttributeNode(XMLNS_NAMESPACE_URI);
/* 688*/          if (attr != null) {
/* 689*/              return attr.getValue(); 
/*   0*/             }
/*   0*/        } 
/* 692*/        aNode = aNode.getParentNode();
/*   0*/      } 
/* 694*/      return null;
/*   0*/    } 
/* 696*/    return uri;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getValue() {
/* 700*/    if (this.node.getNodeType() == 8) {
/* 701*/      String text = ((Comment)this.node).getData();
/* 702*/      return (text == null) ? "" : text.trim();
/*   0*/    } 
/* 704*/    return stringValue(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  private String stringValue(Node node) {
/* 713*/    int nodeType = node.getNodeType();
/* 714*/    if (nodeType == 8) {
/* 715*/        return ""; 
/*   0*/       }
/* 717*/    boolean trim = !"preserve".equals(findEnclosingAttribute(node, "xml:space"));
/* 718*/    if (nodeType == 3 || nodeType == 4) {
/* 719*/      String text = node.getNodeValue();
/* 720*/      return (text == null) ? "" : (trim ? text.trim() : text);
/*   0*/    } 
/* 722*/    if (nodeType == 7) {
/* 723*/      String text = ((ProcessingInstruction)node).getData();
/* 724*/      return (text == null) ? "" : (trim ? text.trim() : text);
/*   0*/    } 
/* 726*/    NodeList list = node.getChildNodes();
/* 727*/    StringBuffer buf = new StringBuffer();
/* 728*/    for (int i = 0; i < list.getLength(); i++) {
/* 729*/      Node child = list.item(i);
/* 730*/      buf.append(stringValue(child));
/*   0*/    } 
/* 732*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer getPointerByID(JXPathContext context, String id) {
/* 742*/    Document document = (this.node.getNodeType() == 9) ? (Document)this.node : this.node.getOwnerDocument();
/* 744*/    Element element = document.getElementById(id);
/* 745*/    return (element == null) ? new NullPointer(getLocale(), id) : new DOMNodePointer(element, getLocale(), id);
/*   0*/  }
/*   0*/  
/*   0*/  public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2) {
/* 751*/    Node node1 = (Node)pointer1.getBaseValue();
/* 752*/    Node node2 = (Node)pointer2.getBaseValue();
/* 753*/    if (node1 == node2) {
/* 754*/        return 0; 
/*   0*/       }
/* 757*/    int t1 = node1.getNodeType();
/* 758*/    int t2 = node2.getNodeType();
/* 759*/    if (t1 == 2 && t2 != 2) {
/* 760*/        return -1; 
/*   0*/       }
/* 762*/    if (t1 != 2 && t2 == 2) {
/* 763*/        return 1; 
/*   0*/       }
/* 765*/    if (t1 == 2 && t2 == 2) {
/* 766*/      NamedNodeMap map = ((Node)getNode()).getAttributes();
/* 767*/      int length = map.getLength();
/* 768*/      for (int i = 0; i < length; i++) {
/* 769*/        Node n = map.item(i);
/* 770*/        if (n == node1) {
/* 771*/            return -1; 
/*   0*/           }
/* 773*/        if (n == node2) {
/* 774*/            return 1; 
/*   0*/           }
/*   0*/      } 
/* 777*/      return 0;
/*   0*/    } 
/* 780*/    Node current = this.node.getFirstChild();
/* 781*/    while (current != null) {
/* 782*/      if (current == node1) {
/* 783*/          return -1; 
/*   0*/         }
/* 785*/      if (current == node2) {
/* 786*/          return 1; 
/*   0*/         }
/* 788*/      current = current.getNextSibling();
/*   0*/    } 
/* 790*/    return 0;
/*   0*/  }
/*   0*/}
