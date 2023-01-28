/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.LinkedHashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.regex.Pattern;
/*   0*/import java.util.regex.PatternSyntaxException;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.parser.Parser;
/*   0*/import org.jsoup.parser.Tag;
/*   0*/import org.jsoup.select.Collector;
/*   0*/import org.jsoup.select.Elements;
/*   0*/import org.jsoup.select.Evaluator;
/*   0*/import org.jsoup.select.NodeTraversor;
/*   0*/import org.jsoup.select.NodeVisitor;
/*   0*/import org.jsoup.select.Selector;
/*   0*/
/*   0*/public class Element extends Node {
/*   0*/  private Tag tag;
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri, Attributes attributes) {
/*  33*/    super(baseUri, attributes);
/*  35*/    Validate.notNull(tag);
/*  36*/    this.tag = tag;
/*   0*/  }
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri) {
/*  48*/    this(tag, baseUri, new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  public String nodeName() {
/*  53*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public String tagName() {
/*  62*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public Element tagName(String tagName) {
/*  73*/    Validate.notEmpty(tagName, "Tag name must not be empty.");
/*  74*/    this.tag = Tag.valueOf(tagName);
/*  75*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Tag tag() {
/*  84*/    return this.tag;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*  94*/    return this.tag.isBlock();
/*   0*/  }
/*   0*/  
/*   0*/  public String id() {
/* 103*/    String id = attr("id");
/* 104*/    return (id == null) ? "" : id;
/*   0*/  }
/*   0*/  
/*   0*/  public Element attr(String attributeKey, String attributeValue) {
/* 114*/    super.attr(attributeKey, attributeValue);
/* 115*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, String> dataset() {
/* 132*/    return this.attributes.dataset();
/*   0*/  }
/*   0*/  
/*   0*/  public final Element parent() {
/* 137*/    return (Element)this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements parents() {
/* 145*/    Elements parents = new Elements();
/* 146*/    accumulateParents(this, parents);
/* 147*/    return parents;
/*   0*/  }
/*   0*/  
/*   0*/  private static void accumulateParents(Element el, Elements parents) {
/* 151*/    Element parent = el.parent();
/* 152*/    if (parent != null && !parent.tagName().equals("#root")) {
/* 153*/      parents.add(parent);
/* 154*/      accumulateParents(parent, parents);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Element child(int index) {
/* 170*/    return children().get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements children() {
/* 184*/    List<Element> elements = new ArrayList<Element>(this.childNodes.size());
/* 185*/    for (Node node : this.childNodes) {
/* 186*/      if (node instanceof Element) {
/* 187*/          elements.add((Element)node); 
/*   0*/         }
/*   0*/    } 
/* 189*/    return new Elements(elements);
/*   0*/  }
/*   0*/  
/*   0*/  public List<TextNode> textNodes() {
/* 209*/    List<TextNode> textNodes = new ArrayList<TextNode>();
/* 210*/    for (Node node : this.childNodes) {
/* 211*/      if (node instanceof TextNode) {
/* 212*/          textNodes.add((TextNode)node); 
/*   0*/         }
/*   0*/    } 
/* 214*/    return Collections.unmodifiableList(textNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public List<DataNode> dataNodes() {
/* 227*/    List<DataNode> dataNodes = new ArrayList<DataNode>();
/* 228*/    for (Node node : this.childNodes) {
/* 229*/      if (node instanceof DataNode) {
/* 230*/          dataNodes.add((DataNode)node); 
/*   0*/         }
/*   0*/    } 
/* 232*/    return Collections.unmodifiableList(dataNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements select(String cssQuery) {
/* 255*/    return Selector.select(cssQuery, this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendChild(Node child) {
/* 265*/    Validate.notNull(child);
/* 268*/    reparentChild(child);
/* 269*/    this.childNodes.add(child);
/* 270*/    child.setSiblingIndex(this.childNodes.size() - 1);
/* 271*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependChild(Node child) {
/* 281*/    Validate.notNull(child);
/* 283*/    addChildren(0, new Node[] { child });
/* 284*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element insertChildren(int index, Collection<? extends Node> children) {
/* 298*/    Validate.notNull(children, "Children collection to be inserted must not be null.");
/* 299*/    int currentSize = childNodeSize();
/* 300*/    if (index < 0) {
/* 300*/        index += currentSize + 1; 
/*   0*/       }
/* 301*/    Validate.isTrue((index >= 0 && index <= currentSize), "Insert position out of bounds.");
/* 303*/    ArrayList<Node> nodes = new ArrayList<Node>(children);
/* 304*/    Node[] nodeArray = nodes.<Node>toArray(new Node[nodes.size()]);
/* 305*/    addChildren(index, nodeArray);
/* 306*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendElement(String tagName) {
/* 317*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 318*/    appendChild(child);
/* 319*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependElement(String tagName) {
/* 330*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 331*/    prependChild(child);
/* 332*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendText(String text) {
/* 342*/    TextNode node = new TextNode(text, baseUri());
/* 343*/    appendChild(node);
/* 344*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependText(String text) {
/* 354*/    TextNode node = new TextNode(text, baseUri());
/* 355*/    prependChild(node);
/* 356*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element append(String html) {
/* 366*/    Validate.notNull(html);
/* 368*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 369*/    addChildren(nodes.<Node>toArray(new Node[nodes.size()]));
/* 370*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prepend(String html) {
/* 380*/    Validate.notNull(html);
/* 382*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 383*/    addChildren(0, nodes.<Node>toArray(new Node[nodes.size()]));
/* 384*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(String html) {
/* 396*/    return (Element)super.before(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(Node node) {
/* 407*/    return (Element)super.before(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(String html) {
/* 419*/    return (Element)super.after(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(Node node) {
/* 430*/    return (Element)super.after(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element empty() {
/* 438*/    this.childNodes.clear();
/* 439*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element wrap(String html) {
/* 450*/    return (Element)super.wrap(html);
/*   0*/  }
/*   0*/  
/*   0*/  public String cssSelector() {
/* 464*/    if (id().length() > 0) {
/* 465*/        return "#" + id(); 
/*   0*/       }
/* 467*/    StringBuilder selector = new StringBuilder(tagName());
/* 468*/    String classes = StringUtil.join(classNames(), ".");
/* 469*/    if (classes.length() > 0) {
/* 470*/        selector.append('.').append(classes); 
/*   0*/       }
/* 472*/    if (parent() == null || parent() instanceof Document) {
/* 473*/        return selector.toString(); 
/*   0*/       }
/* 475*/    selector.insert(0, " > ");
/* 476*/    if (parent().select(selector.toString()).size() > 1) {
/* 477*/        selector.append(String.format(":nth-child(%d)", new Object[] { elementSiblingIndex() + 1 })); 
/*   0*/       }
/* 480*/    return parent().cssSelector() + selector.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public Elements siblingElements() {
/* 489*/    if (this.parentNode == null) {
/* 490*/        return new Elements(0); 
/*   0*/       }
/* 492*/    List<Element> elements = parent().children();
/* 493*/    Elements siblings = new Elements(elements.size() - 1);
/* 494*/    for (Element el : elements) {
/* 495*/      if (el != this) {
/* 496*/          siblings.add(el); 
/*   0*/         }
/*   0*/    } 
/* 497*/    return siblings;
/*   0*/  }
/*   0*/  
/*   0*/  public Element nextElementSibling() {
/* 510*/    if (this.parentNode == null) {
/* 510*/        return null; 
/*   0*/       }
/* 511*/    List<Element> siblings = parent().children();
/* 512*/    Integer index = indexInList(this, siblings);
/* 513*/    Validate.notNull(index);
/* 514*/    if (siblings.size() > index + 1) {
/* 515*/        return siblings.get(index + 1); 
/*   0*/       }
/* 517*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element previousElementSibling() {
/* 526*/    if (this.parentNode == null) {
/* 526*/        return null; 
/*   0*/       }
/* 527*/    List<Element> siblings = parent().children();
/* 528*/    Integer index = indexInList(this, siblings);
/* 529*/    Validate.notNull(index);
/* 530*/    if (index > 0) {
/* 531*/        return siblings.get(index - 1); 
/*   0*/       }
/* 533*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element firstElementSibling() {
/* 542*/    List<Element> siblings = parent().children();
/* 543*/    return (siblings.size() > 1) ? siblings.get(0) : null;
/*   0*/  }
/*   0*/  
/*   0*/  public Integer elementSiblingIndex() {
/* 552*/    if (parent() == null) {
/* 552*/        return 0; 
/*   0*/       }
/* 553*/    return indexInList(this, parent().children());
/*   0*/  }
/*   0*/  
/*   0*/  public Element lastElementSibling() {
/* 561*/    List<Element> siblings = parent().children();
/* 562*/    return (siblings.size() > 1) ? siblings.get(siblings.size() - 1) : null;
/*   0*/  }
/*   0*/  
/*   0*/  private static <E extends Element> Integer indexInList(Element search, List<E> elements) {
/* 566*/    Validate.notNull(search);
/* 567*/    Validate.notNull(elements);
/* 569*/    for (int i = 0; i < elements.size(); i++) {
/* 570*/      Element element = (Element)elements.get(i);
/* 571*/      if (element.equals(search)) {
/* 572*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 574*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByTag(String tagName) {
/* 585*/    Validate.notEmpty(tagName);
/* 586*/    tagName = tagName.toLowerCase().trim();
/* 588*/    return Collector.collect(new Evaluator.Tag(tagName), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element getElementById(String id) {
/* 601*/    Validate.notEmpty(id);
/* 603*/    Elements elements = Collector.collect(new Evaluator.Id(id), this);
/* 604*/    if (elements.size() > 0) {
/* 605*/        return elements.get(0); 
/*   0*/       }
/* 607*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByClass(String className) {
/* 622*/    Validate.notEmpty(className);
/* 624*/    return Collector.collect(new Evaluator.Class(className), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttribute(String key) {
/* 634*/    Validate.notEmpty(key);
/* 635*/    key = key.trim().toLowerCase();
/* 637*/    return Collector.collect(new Evaluator.Attribute(key), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeStarting(String keyPrefix) {
/* 647*/    Validate.notEmpty(keyPrefix);
/* 648*/    keyPrefix = keyPrefix.trim().toLowerCase();
/* 650*/    return Collector.collect(new Evaluator.AttributeStarting(keyPrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValue(String key, String value) {
/* 661*/    return Collector.collect(new Evaluator.AttributeWithValue(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueNot(String key, String value) {
/* 672*/    return Collector.collect(new Evaluator.AttributeWithValueNot(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
/* 683*/    return Collector.collect(new Evaluator.AttributeWithValueStarting(key, valuePrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
/* 694*/    return Collector.collect(new Evaluator.AttributeWithValueEnding(key, valueSuffix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueContaining(String key, String match) {
/* 705*/    return Collector.collect(new Evaluator.AttributeWithValueContaining(key, match), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
/* 715*/    return Collector.collect(new Evaluator.AttributeWithValueMatching(key, pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 728*/      pattern = Pattern.compile(regex);
/* 729*/    } catch (PatternSyntaxException e) {
/* 730*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 732*/    return getElementsByAttributeValueMatching(key, pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexLessThan(int index) {
/* 741*/    return Collector.collect(new Evaluator.IndexLessThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexGreaterThan(int index) {
/* 750*/    return Collector.collect(new Evaluator.IndexGreaterThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexEquals(int index) {
/* 759*/    return Collector.collect(new Evaluator.IndexEquals(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingText(String searchText) {
/* 770*/    return Collector.collect(new Evaluator.ContainsText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingOwnText(String searchText) {
/* 781*/    return Collector.collect(new Evaluator.ContainsOwnText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(Pattern pattern) {
/* 791*/    return Collector.collect(new Evaluator.Matches(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 803*/      pattern = Pattern.compile(regex);
/* 804*/    } catch (PatternSyntaxException e) {
/* 805*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 807*/    return getElementsMatchingText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(Pattern pattern) {
/* 817*/    return Collector.collect(new Evaluator.MatchesOwn(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 829*/      pattern = Pattern.compile(regex);
/* 830*/    } catch (PatternSyntaxException e) {
/* 831*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 833*/    return getElementsMatchingOwnText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getAllElements() {
/* 842*/    return Collector.collect(new Evaluator.AllElements(), this);
/*   0*/  }
/*   0*/  
/*   0*/  public String text() {
/* 855*/    final StringBuilder accum = new StringBuilder();
/* 856*/    new NodeTraversor(new NodeVisitor() {
/*   0*/          public void head(Node node, int depth) {
/* 858*/            if (node instanceof TextNode) {
/* 859*/              TextNode textNode = (TextNode)node;
/* 860*/              Element.appendNormalisedText(accum, textNode);
/* 861*/            } else if (node instanceof Element) {
/* 862*/              Element element = (Element)node;
/* 863*/              if (accum.length() > 0 && (element.isBlock() || element.tag.getName().equals("br")) && !TextNode.lastCharIsWhitespace(accum)) {
/* 866*/                  accum.append(" "); 
/*   0*/                 }
/*   0*/            } 
/*   0*/          }
/*   0*/          
/*   0*/          public void tail(Node node, int depth) {}
/*   0*/        }).traverse(this);
/* 873*/    return accum.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  public String ownText() {
/* 888*/    StringBuilder sb = new StringBuilder();
/* 889*/    ownText(sb);
/* 890*/    return sb.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  private void ownText(StringBuilder accum) {
/* 894*/    for (Node child : this.childNodes) {
/* 895*/      if (child instanceof TextNode) {
/* 896*/        TextNode textNode = (TextNode)child;
/* 897*/        appendNormalisedText(accum, textNode);
/*   0*/        continue;
/*   0*/      } 
/* 898*/      if (child instanceof Element) {
/* 899*/          appendWhitespaceIfBr((Element)child, accum); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
/* 905*/    String text = textNode.getWholeText();
/* 907*/    if (preserveWhitespace(textNode.parentNode)) {
/* 908*/      accum.append(text);
/*   0*/    } else {
/* 910*/      StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
/* 914*/    if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum)) {
/* 915*/        accum.append(" "); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  static boolean preserveWhitespace(Node node) {
/* 920*/    if (node != null && node instanceof Element) {
/* 921*/      Element element = (Element)node;
/* 922*/      return (element.tag.preserveWhitespace() || (element.parent() != null && (element.parent()).tag.preserveWhitespace()));
/*   0*/    } 
/* 925*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element text(String text) {
/* 934*/    Validate.notNull(text);
/* 936*/    empty();
/* 937*/    TextNode textNode = new TextNode(text, this.baseUri);
/* 938*/    appendChild(textNode);
/* 940*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasText() {
/* 948*/    for (Node child : this.childNodes) {
/* 949*/      if (child instanceof TextNode) {
/* 950*/        TextNode textNode = (TextNode)child;
/* 951*/        if (!textNode.isBlank()) {
/* 952*/            return true; 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 953*/      if (child instanceof Element) {
/* 954*/        Element el = (Element)child;
/* 955*/        if (el.hasText()) {
/* 956*/            return true; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 959*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String data() {
/* 969*/    StringBuilder sb = new StringBuilder();
/* 971*/    for (Node childNode : this.childNodes) {
/* 972*/      if (childNode instanceof DataNode) {
/* 973*/        DataNode data = (DataNode)childNode;
/* 974*/        sb.append(data.getWholeData());
/*   0*/        continue;
/*   0*/      } 
/* 975*/      if (childNode instanceof Element) {
/* 976*/        Element element = (Element)childNode;
/* 977*/        String elementData = element.data();
/* 978*/        sb.append(elementData);
/*   0*/      } 
/*   0*/    } 
/* 981*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String className() {
/* 990*/    return attr("class").trim();
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> classNames() {
/*1000*/    String[] names = className().split("\\s+");
/*1001*/    Set<String> classNames = new LinkedHashSet<String>(Arrays.asList(names));
/*1002*/    classNames.remove("");
/*1004*/    return classNames;
/*   0*/  }
/*   0*/  
/*   0*/  public Element classNames(Set<String> classNames) {
/*1013*/    Validate.notNull(classNames);
/*1014*/    this.attributes.put("class", StringUtil.join(classNames, " "));
/*1015*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasClass(String className) {
/*1024*/    Set<String> classNames = classNames();
/*1025*/    for (String name : classNames) {
/*1026*/      if (className.equalsIgnoreCase(name)) {
/*1027*/          return true; 
/*   0*/         }
/*   0*/    } 
/*1029*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element addClass(String className) {
/*1038*/    Validate.notNull(className);
/*1040*/    Set<String> classes = classNames();
/*1041*/    classes.add(className);
/*1042*/    classNames(classes);
/*1044*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element removeClass(String className) {
/*1053*/    Validate.notNull(className);
/*1055*/    Set<String> classes = classNames();
/*1056*/    classes.remove(className);
/*1057*/    classNames(classes);
/*1059*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element toggleClass(String className) {
/*1068*/    Validate.notNull(className);
/*1070*/    Set<String> classes = classNames();
/*1071*/    if (classes.contains(className)) {
/*1072*/      classes.remove(className);
/*   0*/    } else {
/*1074*/      classes.add(className);
/*   0*/    } 
/*1075*/    classNames(classes);
/*1077*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String val() {
/*1085*/    if (tagName().equals("textarea")) {
/*1086*/        return text(); 
/*   0*/       }
/*1088*/    return attr("value");
/*   0*/  }
/*   0*/  
/*   0*/  public Element val(String value) {
/*1097*/    if (tagName().equals("textarea")) {
/*1098*/      text(value);
/*   0*/    } else {
/*1100*/      attr("value", value);
/*   0*/    } 
/*1101*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/*1105*/    if (accum.length() > 0 && out.prettyPrint() && (this.tag.formatAsBlock() || (parent() != null && parent().tag().formatAsBlock()) || out.outline())) {
/*1106*/        indent(accum, depth, out); 
/*   0*/       }
/*1107*/    accum.append("<").append(tagName());
/*1110*/    this.attributes.html(accum, out);
/*1113*/    if (this.childNodes.isEmpty() && this.tag.isSelfClosing()) {
/*1114*/      if (out.syntax() == Document.OutputSettings.Syntax.html && this.tag.isEmpty()) {
/*1115*/        accum.append('>');
/*   0*/      } else {
/*1117*/        accum.append(" />");
/*   0*/      } 
/*   0*/    } else {
/*1120*/      accum.append(">");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
/*1124*/    if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
/*1125*/      if (out.prettyPrint() && !this.childNodes.isEmpty() && (this.tag.formatAsBlock() || (out.outline() && (this.childNodes.size() > 1 || (this.childNodes.size() == 1 && !(this.childNodes.get(0) instanceof TextNode)))))) {
/*1128*/          indent(accum, depth, out); 
/*   0*/         }
/*1129*/      accum.append("</").append(tagName()).append(">");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String html() {
/*1141*/    StringBuilder accum = new StringBuilder();
/*1142*/    html(accum);
/*1143*/    return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void html(StringBuilder accum) {
/*1147*/    for (Node node : this.childNodes) {
/*1148*/        node.outerHtml(accum); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public Element html(String html) {
/*1158*/    empty();
/*1159*/    append(html);
/*1160*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1164*/    return outerHtml();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/*1169*/    if (this == o) {
/*1169*/        return true; 
/*   0*/       }
/*1170*/    if (o == null || getClass() != o.getClass()) {
/*1170*/        return false; 
/*   0*/       }
/*1171*/    if (!super.equals(o)) {
/*1171*/        return false; 
/*   0*/       }
/*1173*/    Element element = (Element)o;
/*1175*/    return (element == o);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1180*/    int result = super.hashCode();
/*1181*/    result = 31 * result + ((this.tag != null) ? this.tag.hashCode() : 0);
/*1182*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Element clone() {
/*1187*/    return (Element)super.clone();
/*   0*/  }
/*   0*/}
