/*   0*/package org.apache.commons.jxpath.ri.model.dom;
/*   0*/
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import org.apache.commons.jxpath.AbstractFactory;
/*   0*/import org.apache.commons.jxpath.JXPathAbstractFactoryException;
/*   0*/import org.apache.commons.jxpath.JXPathContext;
/*   0*/import org.apache.commons.jxpath.JXPathException;
/*   0*/import org.apache.commons.jxpath.Pointer;
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
/*   0*/  public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
/*   0*/  
/*   0*/  public static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
/*   0*/  
/*   0*/  public DOMNodePointer(Node node, Locale locale) {
/*  68*/    super(null, locale);
/*  69*/    this.node = node;
/*   0*/  }
/*   0*/  
/*   0*/  public DOMNodePointer(Node node, Locale locale, String id) {
/*  73*/    super(null, locale);
/*  74*/    this.node = node;
/*  75*/    this.id = id;
/*   0*/  }
/*   0*/  
/*   0*/  public DOMNodePointer(NodePointer parent, Node node) {
/*  79*/    super(parent);
/*  80*/    this.node = node;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean testNode(NodeTest test) {
/*  84*/    return testNode(this.node, test);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean testNode(Node node, NodeTest test) {
/*  88*/    if (test == null) {
/*  89*/        return true; 
/*   0*/       }
/*  91*/    if (test instanceof NodeNameTest) {
/*  92*/      if (node.getNodeType() != 1) {
/*  93*/          return false; 
/*   0*/         }
/*  96*/      NodeNameTest nodeNameTest = (NodeNameTest)test;
/*  97*/      QName testName = nodeNameTest.getNodeName();
/*  98*/      String namespaceURI = nodeNameTest.getNamespaceURI();
/*  99*/      boolean wildcard = nodeNameTest.isWildcard();
/* 100*/      String testPrefix = testName.getPrefix();
/* 101*/      if (wildcard && testPrefix == null) {
/* 102*/          return true; 
/*   0*/         }
/* 104*/      if (wildcard || testName.getName().equals(getLocalName(node))) {
/* 107*/        String nodeNS = getNamespaceURI(node);
/* 108*/        return (equalStrings(namespaceURI, nodeNS) || (nodeNS == null && equalStrings(testPrefix, getPrefix(node))));
/*   0*/      } 
/* 111*/      return false;
/*   0*/    } 
/* 113*/    if (test instanceof NodeTypeTest) {
/* 114*/      int nodeType = node.getNodeType();
/* 115*/      switch (((NodeTypeTest)test).getNodeType()) {
/*   0*/        case 1:
/* 117*/          return (nodeType == 1 || nodeType == 9);
/*   0*/        case 2:
/* 120*/          return (nodeType == 4 || nodeType == 3);
/*   0*/        case 3:
/* 123*/          return (nodeType == 8);
/*   0*/        case 4:
/* 125*/          return (nodeType == 7);
/*   0*/      } 
/* 127*/      return false;
/*   0*/    } 
/* 129*/    if (test instanceof ProcessingInstructionTest && 
/* 130*/      node.getNodeType() == 7) {
/* 131*/      String testPI = ((ProcessingInstructionTest)test).getTarget();
/* 132*/      String nodePI = ((ProcessingInstruction)node).getTarget();
/* 133*/      return testPI.equals(nodePI);
/*   0*/    } 
/* 136*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean equalStrings(String s1, String s2) {
/* 140*/    if (s1 == s2) {
/* 141*/        return true; 
/*   0*/       }
/* 143*/    s1 = (s1 == null) ? "" : s1.trim();
/* 144*/    s2 = (s2 == null) ? "" : s2.trim();
/* 145*/    return s1.equals(s2);
/*   0*/  }
/*   0*/  
/*   0*/  public QName getName() {
/* 149*/    String ln = null;
/* 150*/    String ns = null;
/* 151*/    int type = this.node.getNodeType();
/* 152*/    if (type == 1) {
/* 153*/      ns = getPrefix(this.node);
/* 154*/      ln = getLocalName(this.node);
/* 156*/    } else if (type == 7) {
/* 157*/      ln = ((ProcessingInstruction)this.node).getTarget();
/*   0*/    } 
/* 159*/    return new QName(ns, ln);
/*   0*/  }
/*   0*/  
/*   0*/  public String getNamespaceURI() {
/* 163*/    return getNamespaceURI(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
/* 171*/    return new DOMNodeIterator(this, test, reverse, startWith);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator attributeIterator(QName name) {
/* 175*/    return new DOMAttributeIterator(this, name);
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer namespacePointer(String prefix) {
/* 179*/    return new NamespacePointer(this, prefix);
/*   0*/  }
/*   0*/  
/*   0*/  public NodeIterator namespaceIterator() {
/* 183*/    return new DOMNamespaceIterator(this);
/*   0*/  }
/*   0*/  
/*   0*/  public String getNamespaceURI(String prefix) {
/* 191*/    if (prefix == null || prefix.equals("")) {
/* 192*/        return getDefaultNamespaceURI(); 
/*   0*/       }
/* 195*/    if (prefix.equals("xml")) {
/* 196*/        return "http://www.w3.org/XML/1998/namespace"; 
/*   0*/       }
/* 199*/    if (prefix.equals("xmlns")) {
/* 200*/        return "http://www.w3.org/2000/xmlns/"; 
/*   0*/       }
/* 203*/    String namespace = null;
/* 204*/    if (this.namespaces == null) {
/* 205*/      this.namespaces = new HashMap();
/*   0*/    } else {
/* 208*/      namespace = (String)this.namespaces.get(prefix);
/*   0*/    } 
/* 211*/    if (namespace == null) {
/* 212*/      String qname = "xmlns:" + prefix;
/* 213*/      Node aNode = this.node;
/* 214*/      if (aNode instanceof Document) {
/* 215*/          aNode = ((Document)aNode).getDocumentElement(); 
/*   0*/         }
/* 217*/      while (aNode != null) {
/* 218*/        if (aNode.getNodeType() == 1) {
/* 219*/          Attr attr = ((Element)aNode).getAttributeNode(qname);
/* 220*/          if (attr != null) {
/* 221*/            namespace = attr.getValue();
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/* 225*/        aNode = aNode.getParentNode();
/*   0*/      } 
/* 227*/      if (namespace == null || namespace.equals("")) {
/* 228*/          namespace = "<<unknown namespace>>"; 
/*   0*/         }
/*   0*/    } 
/* 232*/    this.namespaces.put(prefix, namespace);
/* 233*/    if (namespace == "<<unknown namespace>>") {
/* 234*/        return null; 
/*   0*/       }
/* 238*/    return namespace;
/*   0*/  }
/*   0*/  
/*   0*/  public String getDefaultNamespaceURI() {
/* 242*/    if (this.defaultNamespace == null) {
/* 243*/      Node aNode = this.node;
/* 244*/      if (aNode instanceof Document) {
/* 245*/          aNode = ((Document)aNode).getDocumentElement(); 
/*   0*/         }
/* 247*/      while (aNode != null) {
/* 248*/        if (aNode.getNodeType() == 1) {
/* 249*/          Attr attr = ((Element)aNode).getAttributeNode("xmlns");
/* 250*/          if (attr != null) {
/* 251*/            this.defaultNamespace = attr.getValue();
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/* 255*/        aNode = aNode.getParentNode();
/*   0*/      } 
/*   0*/    } 
/* 258*/    if (this.defaultNamespace == null) {
/* 259*/        this.defaultNamespace = ""; 
/*   0*/       }
/* 262*/    return this.defaultNamespace.equals("") ? null : this.defaultNamespace;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getBaseValue() {
/* 266*/    return this.node;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getImmediateNode() {
/* 270*/    return this.node;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isActual() {
/* 274*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCollection() {
/* 278*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLength() {
/* 282*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLeaf() {
/* 286*/    return !this.node.hasChildNodes();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLanguage(String lang) {
/* 295*/    String current = getLanguage();
/* 296*/    return (current == null) ? super.isLanguage(lang) : current.toUpperCase().startsWith(lang.toUpperCase());
/*   0*/  }
/*   0*/  
/*   0*/  protected static String findEnclosingAttribute(Node n, String attrName) {
/* 301*/    while (n != null) {
/* 302*/      if (n.getNodeType() == 1) {
/* 303*/        Element e = (Element)n;
/* 304*/        String attr = e.getAttribute(attrName);
/* 305*/        if (attr != null && !attr.equals("")) {
/* 306*/            return attr; 
/*   0*/           }
/*   0*/      } 
/* 309*/      n = n.getParentNode();
/*   0*/    } 
/* 311*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected String getLanguage() {
/* 315*/    return findEnclosingAttribute(this.node, "xml:lang");
/*   0*/  }
/*   0*/  
/*   0*/  public void setValue(Object value) {
/* 325*/    if (this.node.getNodeType() == 3 || this.node.getNodeType() == 4) {
/* 327*/      String string = (String)TypeUtils.convert(value, String.class);
/* 328*/      if (string != null && !string.equals("")) {
/* 329*/        this.node.setNodeValue(string);
/*   0*/      } else {
/* 332*/        this.node.getParentNode().removeChild(this.node);
/*   0*/      } 
/*   0*/    } else {
/* 336*/      NodeList children = this.node.getChildNodes();
/* 337*/      int count = children.getLength();
/* 338*/      for (int i = count; --i >= 0; ) {
/* 339*/        Node child = children.item(i);
/* 340*/        this.node.removeChild(child);
/*   0*/      } 
/* 343*/      if (value instanceof Node) {
/* 344*/        Node valueNode = (Node)value;
/* 345*/        if (valueNode instanceof Element || valueNode instanceof Document) {
/* 347*/          children = valueNode.getChildNodes();
/* 348*/          for (int j = 0; j < children.getLength(); j++) {
/* 349*/            Node child = children.item(j);
/* 350*/            this.node.appendChild(child.cloneNode(true));
/*   0*/          } 
/*   0*/        } else {
/* 354*/          this.node.appendChild(valueNode.cloneNode(true));
/*   0*/        } 
/*   0*/      } else {
/* 358*/        String string = (String)TypeUtils.convert(value, String.class);
/* 359*/        if (string != null && !string.equals("")) {
/* 360*/          Node textNode = this.node.getOwnerDocument().createTextNode(string);
/* 362*/          this.node.appendChild(textNode);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer createChild(JXPathContext context, QName name, int index) {
/* 373*/    if (index == Integer.MIN_VALUE) {
/* 374*/        index = 0; 
/*   0*/       }
/* 376*/    boolean success = getAbstractFactory(context).createObject(context, this, this.node, name.toString(), index);
/* 383*/    if (success) {
/* 385*/      String prefix = name.getPrefix();
/* 386*/      String namespaceURI = (prefix == null) ? null : context.getNamespaceURI(prefix);
/* 388*/      NodeTest nodeTest = new NodeNameTest(name, namespaceURI);
/* 390*/      NodeIterator it = childIterator(nodeTest, false, null);
/* 391*/      if (it != null && it.setPosition(index + 1)) {
/* 392*/          return it.getNodePointer(); 
/*   0*/         }
/*   0*/    } 
/* 395*/    throw new JXPathAbstractFactoryException("Factory could not create a child node for path: " + asPath() + "/" + name + "[" + (index + 1) + "]");
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer createChild(JXPathContext context, QName name, int index, Object value) {
/* 403*/    NodePointer ptr = createChild(context, name, index);
/* 404*/    ptr.setValue(value);
/* 405*/    return ptr;
/*   0*/  }
/*   0*/  
/*   0*/  public NodePointer createAttribute(JXPathContext context, QName name) {
/* 409*/    if (!(this.node instanceof Element)) {
/* 410*/        return super.createAttribute(context, name); 
/*   0*/       }
/* 412*/    Element element = (Element)this.node;
/* 413*/    String prefix = name.getPrefix();
/* 414*/    if (prefix != null) {
/* 415*/      this;
/* 415*/      String ns = prefix;
/* 416*/      if (ns == null) {
/* 417*/          throw new JXPathException("Unknown namespace prefix: " + prefix); 
/*   0*/         }
/* 420*/      element.setAttributeNS(ns, name.toString(), "");
/* 423*/    } else if (!element.hasAttribute(name.getName())) {
/* 424*/      element.setAttribute(name.getName(), "");
/*   0*/    } 
/* 427*/    NodeIterator it = attributeIterator(name);
/* 428*/    it.setPosition(1);
/* 429*/    return it.getNodePointer();
/*   0*/  }
/*   0*/  
/*   0*/  public void remove() {
/* 433*/    Node parent = this.node.getParentNode();
/* 434*/    if (parent == null) {
/* 435*/        throw new JXPathException("Cannot remove root DOM node"); 
/*   0*/       }
/* 437*/    parent.removeChild(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  public String asPath() {
/*   0*/    String target;
/* 441*/    if (this.id != null) {
/* 442*/        return "id('" + escape(this.id) + "')"; 
/*   0*/       }
/* 445*/    StringBuffer buffer = new StringBuffer();
/* 446*/    if (this.parent != null) {
/* 447*/        buffer.append(this.parent.asPath()); 
/*   0*/       }
/* 449*/    switch (this.node.getNodeType()) {
/*   0*/      case 1:
/* 454*/        if (this.parent instanceof DOMNodePointer) {
/* 455*/          if (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '/') {
/* 457*/              buffer.append('/'); 
/*   0*/             }
/* 459*/          String ln = getLocalName(this.node);
/* 460*/          String nsURI = getNamespaceURI();
/* 461*/          if (nsURI == null) {
/* 462*/            buffer.append(ln);
/* 463*/            buffer.append('[');
/* 464*/            buffer.append(getRelativePositionByName()).append(']');
/*   0*/            break;
/*   0*/          } 
/* 467*/          String prefix = getNamespaceResolver().getPrefix(nsURI);
/* 468*/          if (prefix != null) {
/* 469*/            buffer.append(prefix);
/* 470*/            buffer.append(':');
/* 471*/            buffer.append(ln);
/* 472*/            buffer.append('[');
/* 473*/            buffer.append(getRelativePositionByName());
/* 474*/            buffer.append(']');
/*   0*/            break;
/*   0*/          } 
/* 477*/          buffer.append("node()");
/* 478*/          buffer.append('[');
/* 479*/          buffer.append(getRelativePositionOfElement());
/* 480*/          buffer.append(']');
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 3:
/*   0*/      case 4:
/* 487*/        buffer.append("/text()");
/* 488*/        buffer.append('[');
/* 489*/        buffer.append(getRelativePositionOfTextNode()).append(']');
/*   0*/        break;
/*   0*/      case 7:
/* 492*/        target = ((ProcessingInstruction)this.node).getTarget();
/* 493*/        buffer.append("/processing-instruction('");
/* 494*/        buffer.append(target).append("')");
/* 495*/        buffer.append('[');
/* 496*/        buffer.append(getRelativePositionOfPI(target)).append(']');
/*   0*/        break;
/*   0*/    } 
/* 501*/    return buffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private String escape(String string) {
/* 505*/    int index = string.indexOf('\'');
/* 506*/    while (index != -1) {
/* 507*/      string = string.substring(0, index) + "&apos;" + string.substring(index + 1);
/* 511*/      index = string.indexOf('\'');
/*   0*/    } 
/* 513*/    index = string.indexOf('"');
/* 514*/    while (index != -1) {
/* 515*/      string = string.substring(0, index) + "&quot;" + string.substring(index + 1);
/* 519*/      index = string.indexOf('"');
/*   0*/    } 
/* 521*/    return string;
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionByName() {
/* 525*/    int count = 1;
/* 526*/    Node n = this.node.getPreviousSibling();
/* 527*/    while (n != null) {
/* 528*/      if (n.getNodeType() == 1) {
/* 529*/        String nm = n.getNodeName();
/* 530*/        if (nm.equals(this.node.getNodeName())) {
/* 531*/            count++; 
/*   0*/           }
/*   0*/      } 
/* 534*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 536*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionOfElement() {
/* 540*/    int count = 1;
/* 541*/    Node n = this.node.getPreviousSibling();
/* 542*/    while (n != null) {
/* 543*/      if (n.getNodeType() == 1) {
/* 544*/          count++; 
/*   0*/         }
/* 546*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 548*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionOfTextNode() {
/* 552*/    int count = 1;
/* 553*/    Node n = this.node.getPreviousSibling();
/* 554*/    while (n != null) {
/* 555*/      if (n.getNodeType() == 3 || n.getNodeType() == 4) {
/* 557*/          count++; 
/*   0*/         }
/* 559*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 561*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private int getRelativePositionOfPI(String target) {
/* 565*/    int count = 1;
/* 566*/    Node n = this.node.getPreviousSibling();
/* 567*/    while (n != null) {
/* 568*/      if (n.getNodeType() == 7 && ((ProcessingInstruction)n).getTarget().equals(target)) {
/* 570*/          count++; 
/*   0*/         }
/* 572*/      n = n.getPreviousSibling();
/*   0*/    } 
/* 574*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 578*/    return System.identityHashCode(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object object) {
/* 582*/    return (object == this || (object instanceof DOMNodePointer && this.node == ((DOMNodePointer)object).node));
/*   0*/  }
/*   0*/  
/*   0*/  public static String getPrefix(Node node) {
/* 586*/    String prefix = node.getPrefix();
/* 587*/    if (prefix != null) {
/* 588*/        return prefix; 
/*   0*/       }
/* 591*/    String name = node.getNodeName();
/* 592*/    int index = name.lastIndexOf(':');
/* 593*/    return (index < 0) ? null : name.substring(0, index);
/*   0*/  }
/*   0*/  
/*   0*/  public static String getLocalName(Node node) {
/* 597*/    String localName = node.getLocalName();
/* 598*/    if (localName != null) {
/* 599*/        return localName; 
/*   0*/       }
/* 602*/    String name = node.getNodeName();
/* 603*/    int index = name.lastIndexOf(':');
/* 604*/    return (index < 0) ? name : name.substring(index + 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String getNamespaceURI(Node node) {
/* 608*/    if (node instanceof Document) {
/* 609*/        node = ((Document)node).getDocumentElement(); 
/*   0*/       }
/* 612*/    Element element = (Element)node;
/* 614*/    String uri = element.getNamespaceURI();
/* 615*/    if (uri != null) {
/* 616*/        return uri; 
/*   0*/       }
/* 619*/    String prefix = getPrefix(node);
/* 620*/    String qname = (prefix == null) ? "xmlns" : ("xmlns:" + prefix);
/* 622*/    Node aNode = node;
/* 623*/    while (aNode != null) {
/* 624*/      if (aNode.getNodeType() == 1) {
/* 625*/        Attr attr = ((Element)aNode).getAttributeNode(qname);
/* 626*/        if (attr != null) {
/* 627*/            return attr.getValue(); 
/*   0*/           }
/*   0*/      } 
/* 630*/      aNode = aNode.getParentNode();
/*   0*/    } 
/* 632*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getValue() {
/* 636*/    if (this.node.getNodeType() == 8) {
/* 637*/      String text = ((Comment)this.node).getData();
/* 638*/      return (text == null) ? "" : text.trim();
/*   0*/    } 
/* 640*/    return stringValue(this.node);
/*   0*/  }
/*   0*/  
/*   0*/  private String stringValue(Node node) {
/* 644*/    int nodeType = node.getNodeType();
/* 645*/    if (nodeType == 8) {
/* 646*/        return ""; 
/*   0*/       }
/* 648*/    boolean trim = !"preserve".equals(findEnclosingAttribute(node, "xml:space"));
/* 649*/    if (nodeType == 3 || nodeType == 4) {
/* 650*/      String text = node.getNodeValue();
/* 651*/      return (text == null) ? "" : (trim ? text.trim() : text);
/*   0*/    } 
/* 653*/    if (nodeType == 7) {
/* 654*/      String text = ((ProcessingInstruction)node).getData();
/* 655*/      return (text == null) ? "" : (trim ? text.trim() : text);
/*   0*/    } 
/* 657*/    NodeList list = node.getChildNodes();
/* 658*/    StringBuffer buf = new StringBuffer(16);
/* 659*/    for (int i = 0; i < list.getLength(); i++) {
/* 660*/      Node child = list.item(i);
/* 661*/      buf.append(stringValue(child));
/*   0*/    } 
/* 663*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public Pointer getPointerByID(JXPathContext context, String id) {
/* 670*/    Document document = (this.node.getNodeType() == 9) ? (Document)this.node : this.node.getOwnerDocument();
/* 672*/    Element element = document.getElementById(id);
/* 673*/    return (element == null) ? new NullPointer(getLocale(), id) : new DOMNodePointer(element, getLocale(), id);
/*   0*/  }
/*   0*/  
/*   0*/  private AbstractFactory getAbstractFactory(JXPathContext context) {
/* 678*/    AbstractFactory factory = context.getFactory();
/* 679*/    if (factory == null) {
/* 680*/        throw new JXPathException("Factory is not set on the JXPathContext - cannot create path: " + asPath()); 
/*   0*/       }
/* 685*/    return factory;
/*   0*/  }
/*   0*/  
/*   0*/  public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2) {
/* 691*/    Node node1 = (Node)pointer1.getBaseValue();
/* 692*/    Node node2 = (Node)pointer2.getBaseValue();
/* 693*/    if (node1 == node2) {
/* 694*/        return 0; 
/*   0*/       }
/* 697*/    int t1 = node1.getNodeType();
/* 698*/    int t2 = node2.getNodeType();
/* 699*/    if (t1 == 2 && t2 != 2) {
/* 700*/        return -1; 
/*   0*/       }
/* 702*/    if (t1 != 2 && t2 == 2) {
/* 703*/        return 1; 
/*   0*/       }
/* 705*/    if (t1 == 2 && t2 == 2) {
/* 706*/      NamedNodeMap map = ((Node)getNode()).getAttributes();
/* 707*/      int length = map.getLength();
/* 708*/      for (int i = 0; i < length; i++) {
/* 709*/        Node n = map.item(i);
/* 710*/        if (n == node1) {
/* 711*/            return -1; 
/*   0*/           }
/* 713*/        if (n == node2) {
/* 714*/            return 1; 
/*   0*/           }
/*   0*/      } 
/* 717*/      return 0;
/*   0*/    } 
/* 720*/    Node current = this.node.getFirstChild();
/* 721*/    while (current != null) {
/* 722*/      if (current == node1) {
/* 723*/          return -1; 
/*   0*/         }
/* 725*/      if (current == node2) {
/* 726*/          return 1; 
/*   0*/         }
/* 728*/      current = current.getNextSibling();
/*   0*/    } 
/* 730*/    return 0;
/*   0*/  }
/*   0*/}
