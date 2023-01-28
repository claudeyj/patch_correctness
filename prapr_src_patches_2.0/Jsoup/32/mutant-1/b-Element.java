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
/*   0*/import org.jsoup.select.Selector;
/*   0*/
/*   0*/public class Element extends Node {
/*   0*/  private Tag tag;
/*   0*/  
/*   0*/  private Set<String> classNames;
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri, Attributes attributes) {
/*  38*/    super(baseUri, attributes);
/*  40*/    Validate.notNull(tag);
/*  41*/    this.tag = tag;
/*   0*/  }
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri) {
/*  53*/    this(tag, baseUri, new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  public String nodeName() {
/*  58*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public String tagName() {
/*  67*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public Element tagName(String tagName) {
/*  78*/    Validate.notEmpty(tagName, "Tag name must not be empty.");
/*  79*/    this.tag = Tag.valueOf(tagName);
/*  80*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Tag tag() {
/*  89*/    return this.tag;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/*  99*/    return this.tag.isBlock();
/*   0*/  }
/*   0*/  
/*   0*/  public String id() {
/* 108*/    String id = attr("id");
/* 109*/    return (id == null) ? "" : id;
/*   0*/  }
/*   0*/  
/*   0*/  public Element attr(String attributeKey, String attributeValue) {
/* 119*/    super.attr(attributeKey, attributeValue);
/* 120*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, String> dataset() {
/* 137*/    return this.attributes.dataset();
/*   0*/  }
/*   0*/  
/*   0*/  public final Element parent() {
/* 142*/    return (Element)this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements parents() {
/* 150*/    Elements parents = new Elements();
/* 151*/    accumulateParents(this, parents);
/* 152*/    return parents;
/*   0*/  }
/*   0*/  
/*   0*/  private static void accumulateParents(Element el, Elements parents) {
/* 156*/    Element parent = el.parent();
/* 157*/    if (parent != null && !parent.tagName().equals("#root")) {
/* 158*/      parents.add(parent);
/* 159*/      accumulateParents(parent, parents);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Element child(int index) {
/* 174*/    return children().get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements children() {
/* 187*/    List<Element> elements = new ArrayList<Element>();
/* 188*/    for (Node node : this.childNodes) {
/* 189*/      if (node instanceof Element) {
/* 190*/          elements.add((Element)node); 
/*   0*/         }
/*   0*/    } 
/* 192*/    return new Elements(elements);
/*   0*/  }
/*   0*/  
/*   0*/  public List<TextNode> textNodes() {
/* 212*/    List<TextNode> textNodes = new ArrayList<TextNode>();
/* 213*/    for (Node node : this.childNodes) {
/* 214*/      if (node instanceof TextNode) {
/* 215*/          textNodes.add((TextNode)node); 
/*   0*/         }
/*   0*/    } 
/* 217*/    return Collections.unmodifiableList(textNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public List<DataNode> dataNodes() {
/* 229*/    List<DataNode> dataNodes = new ArrayList<DataNode>();
/* 230*/    for (Node node : this.childNodes) {
/* 231*/      if (node instanceof DataNode) {
/* 232*/          dataNodes.add((DataNode)node); 
/*   0*/         }
/*   0*/    } 
/* 234*/    return Collections.unmodifiableList(dataNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements select(String cssQuery) {
/* 255*/    return Selector.select(cssQuery, this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendChild(Node child) {
/* 265*/    Validate.notNull(child);
/* 267*/    addChildren(new Node[] { child });
/* 268*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependChild(Node child) {
/* 278*/    Validate.notNull(child);
/* 280*/    addChildren(0, new Node[] { child });
/* 281*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element insertChildren(int index, Collection<? extends Node> children) {
/* 295*/    Validate.notNull(children, "Children collection to be inserted must not be null.");
/* 296*/    int currentSize = childNodeSize();
/* 297*/    if (index < 0) {
/* 297*/        index += currentSize + 1; 
/*   0*/       }
/* 298*/    Validate.isTrue((index >= 0 && index <= currentSize), "Insert position out of bounds.");
/* 300*/    ArrayList<Node> nodes = new ArrayList<Node>(children);
/* 301*/    Node[] nodeArray = nodes.<Node>toArray(new Node[nodes.size()]);
/* 302*/    addChildren(index, nodeArray);
/* 303*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendElement(String tagName) {
/* 314*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 315*/    appendChild(child);
/* 316*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependElement(String tagName) {
/* 327*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 328*/    prependChild(child);
/* 329*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendText(String text) {
/* 339*/    TextNode node = new TextNode(text, baseUri());
/* 340*/    appendChild(node);
/* 341*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependText(String text) {
/* 351*/    TextNode node = new TextNode(text, baseUri());
/* 352*/    prependChild(node);
/* 353*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element append(String html) {
/* 363*/    Validate.notNull(html);
/* 365*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 366*/    addChildren(nodes.<Node>toArray(new Node[nodes.size()]));
/* 367*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prepend(String html) {
/* 377*/    Validate.notNull(html);
/* 379*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 380*/    addChildren(0, nodes.<Node>toArray(new Node[nodes.size()]));
/* 381*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(String html) {
/* 393*/    return (Element)super.before(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(Node node) {
/* 404*/    return (Element)super.before(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(String html) {
/* 416*/    return (Element)super.after(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(Node node) {
/* 427*/    return (Element)super.after(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element empty() {
/* 435*/    this.childNodes.clear();
/* 436*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element wrap(String html) {
/* 447*/    return (Element)super.wrap(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements siblingElements() {
/* 456*/    if (this.parentNode == null) {
/* 457*/        return new Elements(0); 
/*   0*/       }
/* 459*/    List<Element> elements = parent().children();
/* 460*/    Elements siblings = new Elements(elements.size() - 1);
/* 461*/    for (Element el : elements) {
/* 462*/      if (el != this) {
/* 463*/          siblings.add(el); 
/*   0*/         }
/*   0*/    } 
/* 464*/    return siblings;
/*   0*/  }
/*   0*/  
/*   0*/  public Element nextElementSibling() {
/* 476*/    if (this.parentNode == null) {
/* 476*/        return null; 
/*   0*/       }
/* 477*/    List<Element> siblings = parent().children();
/* 478*/    Integer index = indexInList(this, siblings);
/* 479*/    Validate.notNull(index);
/* 480*/    if (siblings.size() > index + 1) {
/* 481*/        return siblings.get(index + 1); 
/*   0*/       }
/* 483*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element previousElementSibling() {
/* 492*/    if (this.parentNode == null) {
/* 492*/        return null; 
/*   0*/       }
/* 493*/    List<Element> siblings = parent().children();
/* 494*/    Integer index = indexInList(this, siblings);
/* 495*/    Validate.notNull(index);
/* 496*/    if (index > 0) {
/* 497*/        return siblings.get(index - 1); 
/*   0*/       }
/* 499*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element firstElementSibling() {
/* 508*/    List<Element> siblings = parent().children();
/* 509*/    return (siblings.size() > 1) ? siblings.get(0) : null;
/*   0*/  }
/*   0*/  
/*   0*/  public Integer elementSiblingIndex() {
/* 518*/    if (parent() == null) {
/* 518*/        return 0; 
/*   0*/       }
/* 519*/    return indexInList(this, parent().children());
/*   0*/  }
/*   0*/  
/*   0*/  public Element lastElementSibling() {
/* 527*/    List<Element> siblings = parent().children();
/* 528*/    return (siblings.size() > 1) ? siblings.get(siblings.size() - 1) : null;
/*   0*/  }
/*   0*/  
/*   0*/  private static <E extends Element> Integer indexInList(Element search, List<E> elements) {
/* 532*/    Validate.notNull(search);
/* 533*/    Validate.notNull(elements);
/* 535*/    for (int i = 0; i < elements.size(); i++) {
/* 536*/      Element element = (Element)elements.get(i);
/* 537*/      if (element.equals(search)) {
/* 538*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 540*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByTag(String tagName) {
/* 551*/    Validate.notEmpty(tagName);
/* 552*/    tagName = tagName.toLowerCase().trim();
/* 554*/    return Collector.collect(new Evaluator.Tag(tagName), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element getElementById(String id) {
/* 567*/    Validate.notEmpty(id);
/* 569*/    Elements elements = Collector.collect(new Evaluator.Id(id), this);
/* 570*/    if (elements.size() > 0) {
/* 571*/        return elements.get(0); 
/*   0*/       }
/* 573*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByClass(String className) {
/* 588*/    Validate.notEmpty(className);
/* 590*/    return Collector.collect(new Evaluator.Class(className), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttribute(String key) {
/* 600*/    Validate.notEmpty(key);
/* 601*/    key = key.trim().toLowerCase();
/* 603*/    return Collector.collect(new Evaluator.Attribute(key), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeStarting(String keyPrefix) {
/* 613*/    Validate.notEmpty(keyPrefix);
/* 614*/    keyPrefix = keyPrefix.trim().toLowerCase();
/* 616*/    return Collector.collect(new Evaluator.AttributeStarting(keyPrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValue(String key, String value) {
/* 627*/    return Collector.collect(new Evaluator.AttributeWithValue(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueNot(String key, String value) {
/* 638*/    return Collector.collect(new Evaluator.AttributeWithValueNot(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
/* 649*/    return Collector.collect(new Evaluator.AttributeWithValueStarting(key, valuePrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
/* 660*/    return Collector.collect(new Evaluator.AttributeWithValueEnding(key, valueSuffix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueContaining(String key, String match) {
/* 671*/    return Collector.collect(new Evaluator.AttributeWithValueContaining(key, match), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
/* 681*/    return Collector.collect(new Evaluator.AttributeWithValueMatching(key, pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 694*/      pattern = Pattern.compile(regex);
/* 695*/    } catch (PatternSyntaxException e) {
/* 696*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 698*/    return getElementsByAttributeValueMatching(key, pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexLessThan(int index) {
/* 707*/    return Collector.collect(new Evaluator.IndexLessThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexGreaterThan(int index) {
/* 716*/    return Collector.collect(new Evaluator.IndexGreaterThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexEquals(int index) {
/* 725*/    return Collector.collect(new Evaluator.IndexEquals(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingText(String searchText) {
/* 736*/    return Collector.collect(new Evaluator.ContainsText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingOwnText(String searchText) {
/* 747*/    return Collector.collect(new Evaluator.ContainsOwnText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(Pattern pattern) {
/* 757*/    return Collector.collect(new Evaluator.Matches(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 769*/      pattern = Pattern.compile(regex);
/* 770*/    } catch (PatternSyntaxException e) {
/* 771*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 773*/    return getElementsMatchingText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(Pattern pattern) {
/* 783*/    return Collector.collect(new Evaluator.MatchesOwn(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 795*/      pattern = Pattern.compile(regex);
/* 796*/    } catch (PatternSyntaxException e) {
/* 797*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 799*/    return getElementsMatchingOwnText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getAllElements() {
/* 808*/    return Collector.collect(new Evaluator.AllElements(), this);
/*   0*/  }
/*   0*/  
/*   0*/  public String text() {
/* 821*/    StringBuilder sb = new StringBuilder();
/* 822*/    text(sb);
/* 823*/    return sb.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  private void text(StringBuilder accum) {
/* 827*/    appendWhitespaceIfBr(this, accum);
/* 829*/    for (Node child : this.childNodes) {
/* 830*/      if (child instanceof TextNode) {
/* 831*/        TextNode textNode = (TextNode)child;
/* 832*/        appendNormalisedText(accum, textNode);
/*   0*/        continue;
/*   0*/      } 
/* 833*/      if (child instanceof Element) {
/* 834*/        Element element = (Element)child;
/* 835*/        if (accum.length() > 0 && element.isBlock() && !TextNode.lastCharIsWhitespace(accum)) {
/* 836*/            accum.append(" "); 
/*   0*/           }
/* 837*/        element.text(accum);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String ownText() {
/* 854*/    StringBuilder sb = new StringBuilder();
/* 855*/    ownText(sb);
/* 856*/    return sb.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  private void ownText(StringBuilder accum) {
/* 860*/    for (Node child : this.childNodes) {
/* 861*/      if (child instanceof TextNode) {
/* 862*/        TextNode textNode = (TextNode)child;
/* 863*/        appendNormalisedText(accum, textNode);
/*   0*/        continue;
/*   0*/      } 
/* 864*/      if (child instanceof Element) {
/* 865*/          appendWhitespaceIfBr((Element)child, accum); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void appendNormalisedText(StringBuilder accum, TextNode textNode) {
/* 871*/    String text = textNode.getWholeText();
/* 873*/    if (!preserveWhitespace()) {
/* 874*/      text = TextNode.normaliseWhitespace(text);
/* 875*/      if (TextNode.lastCharIsWhitespace(accum)) {
/* 876*/          text = TextNode.stripLeadingWhitespace(text); 
/*   0*/         }
/*   0*/    } 
/* 878*/    accum.append(text);
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
/* 882*/    if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum)) {
/* 883*/        accum.append(" "); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  boolean preserveWhitespace() {
/* 887*/    return (this.tag.preserveWhitespace() || (parent() != null && parent().preserveWhitespace()));
/*   0*/  }
/*   0*/  
/*   0*/  public Element text(String text) {
/* 896*/    Validate.notNull(text);
/* 898*/    empty();
/* 899*/    TextNode textNode = new TextNode(text, this.baseUri);
/* 900*/    appendChild(textNode);
/* 902*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasText() {
/* 910*/    for (Node child : this.childNodes) {
/* 911*/      if (child instanceof TextNode) {
/* 912*/        TextNode textNode = (TextNode)child;
/* 913*/        if (!textNode.isBlank()) {
/* 914*/            return true; 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 915*/      if (child instanceof Element) {
/* 916*/        Element el = (Element)child;
/* 917*/        if (el.hasText()) {
/* 918*/            return true; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 921*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String data() {
/* 931*/    StringBuilder sb = new StringBuilder();
/* 933*/    for (Node childNode : this.childNodes) {
/* 934*/      if (childNode instanceof DataNode) {
/* 935*/        DataNode data = (DataNode)childNode;
/* 936*/        sb.append(data.getWholeData());
/*   0*/        continue;
/*   0*/      } 
/* 937*/      if (childNode instanceof Element) {
/* 938*/        Element element = (Element)childNode;
/* 939*/        String elementData = element.data();
/* 940*/        sb.append(elementData);
/*   0*/      } 
/*   0*/    } 
/* 943*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String className() {
/* 952*/    return attr("class");
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> classNames() {
/* 963*/    String[] names = className().split("\\s+");
/* 964*/    this.classNames = new LinkedHashSet<String>(Arrays.asList(names));
/* 966*/    return this.classNames;
/*   0*/  }
/*   0*/  
/*   0*/  public Element classNames(Set<String> classNames) {
/* 975*/    Validate.notNull(classNames);
/* 976*/    this.attributes.put("class", StringUtil.join(classNames, " "));
/* 977*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasClass(String className) {
/* 986*/    Set<String> classNames = classNames();
/* 987*/    for (String name : classNames) {
/* 988*/      if (className.equalsIgnoreCase(name)) {
/* 989*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 991*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element addClass(String className) {
/*1000*/    Validate.notNull(className);
/*1002*/    Set<String> classes = classNames();
/*1003*/    classes.add(className);
/*1004*/    classNames(classes);
/*1006*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element removeClass(String className) {
/*1015*/    Validate.notNull(className);
/*1017*/    Set<String> classes = classNames();
/*1018*/    classes.remove(className);
/*1019*/    classNames(classes);
/*1021*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element toggleClass(String className) {
/*1030*/    Validate.notNull(className);
/*1032*/    Set<String> classes = classNames();
/*1033*/    if (classes.contains(className)) {
/*1034*/      classes.remove(className);
/*   0*/    } else {
/*1036*/      classes.add(className);
/*   0*/    } 
/*1037*/    classNames(classes);
/*1039*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String val() {
/*1047*/    if (tagName().equals("textarea")) {
/*1048*/        return text(); 
/*   0*/       }
/*1050*/    return attr("value");
/*   0*/  }
/*   0*/  
/*   0*/  public Element val(String value) {
/*1059*/    if (tagName().equals("textarea")) {
/*1060*/      text(value);
/*   0*/    } else {
/*1062*/      attr("value", value);
/*   0*/    } 
/*1063*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/*1067*/    if (accum.length() > 0 && out.prettyPrint() && (this.tag.formatAsBlock() || (parent() != null && parent().tag().formatAsBlock()))) {
/*1068*/        indent(accum, depth, out); 
/*   0*/       }
/*1069*/    accum.append("<").append(tagName());
/*1072*/    this.attributes.html(accum, out);
/*1074*/    if (this.childNodes.isEmpty() && this.tag.isSelfClosing()) {
/*1075*/      accum.append(" />");
/*   0*/    } else {
/*1077*/      accum.append(">");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
/*1081*/    if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
/*1082*/      if (out.prettyPrint() && !this.childNodes.isEmpty() && this.tag.formatAsBlock()) {
/*1083*/          indent(accum, depth, out); 
/*   0*/         }
/*1084*/      accum.append("</").append(tagName()).append(">");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String html() {
/*1096*/    StringBuilder accum = new StringBuilder();
/*1097*/    html(accum);
/*1098*/    return accum.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  private void html(StringBuilder accum) {
/*1102*/    for (Node node : this.childNodes) {
/*1103*/        node.outerHtml(accum); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public Element html(String html) {
/*1113*/    empty();
/*1114*/    append(html);
/*1115*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1119*/    return outerHtml();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/*1124*/    return (this == o);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1130*/    int result = super.hashCode();
/*1131*/    result = 31 * result + ((this.tag != null) ? this.tag.hashCode() : 0);
/*1132*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Element clone() {
/*1137*/    Element clone = (Element)super.clone();
/*1138*/    clone.classNames();
/*1139*/    return clone;
/*   0*/  }
/*   0*/}
