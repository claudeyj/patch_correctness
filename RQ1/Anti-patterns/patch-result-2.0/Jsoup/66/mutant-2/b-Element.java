/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.lang.ref.WeakReference;
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
/*   0*/import org.jsoup.helper.ChangeNotifyingArrayList;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.internal.Normalizer;
/*   0*/import org.jsoup.parser.ParseSettings;
/*   0*/import org.jsoup.parser.Parser;
/*   0*/import org.jsoup.parser.Tag;
/*   0*/import org.jsoup.select.Collector;
/*   0*/import org.jsoup.select.Elements;
/*   0*/import org.jsoup.select.Evaluator;
/*   0*/import org.jsoup.select.NodeTraversor;
/*   0*/import org.jsoup.select.NodeVisitor;
/*   0*/import org.jsoup.select.QueryParser;
/*   0*/import org.jsoup.select.Selector;
/*   0*/
/*   0*/public class Element extends Node {
/*  41*/  private static final List<Node> EMPTY_NODES = Collections.emptyList();
/*   0*/  
/*  42*/  private static final Pattern classSplit = Pattern.compile("\\s+");
/*   0*/  
/*   0*/  private Tag tag;
/*   0*/  
/*   0*/  private WeakReference<List<Element>> shadowChildrenRef;
/*   0*/  
/*   0*/  List<Node> childNodes;
/*   0*/  
/*   0*/  private Attributes attributes;
/*   0*/  
/*   0*/  private String baseUri;
/*   0*/  
/*   0*/  public Element(String tag) {
/*  54*/    this(Tag.valueOf(tag), "", new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri, Attributes attributes) {
/*  67*/    Validate.notNull(tag);
/*  68*/    Validate.notNull(baseUri);
/*  69*/    this.childNodes = EMPTY_NODES;
/*  70*/    this.baseUri = baseUri;
/*  71*/    this.attributes = attributes;
/*  72*/    this.tag = tag;
/*   0*/  }
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri) {
/*  84*/    this(tag, baseUri, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected List<Node> ensureChildNodes() {
/*  88*/    if (this.childNodes == EMPTY_NODES) {
/*  89*/        this.childNodes = new NodeList(4); 
/*   0*/       }
/*  91*/    return this.childNodes;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean hasAttributes() {
/*  96*/    return (this.attributes != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Attributes attributes() {
/* 101*/    if (!hasAttributes()) {
/* 102*/        this.attributes = new Attributes(); 
/*   0*/       }
/* 103*/    return this.attributes;
/*   0*/  }
/*   0*/  
/*   0*/  public String baseUri() {
/* 108*/    return this.baseUri;
/*   0*/  }
/*   0*/  
/*   0*/  protected void doSetBaseUri(String baseUri) {
/* 113*/    this.baseUri = baseUri;
/*   0*/  }
/*   0*/  
/*   0*/  public int childNodeSize() {
/* 118*/    return this.childNodes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public String nodeName() {
/* 123*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public String tagName() {
/* 132*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public Element tagName(String tagName) {
/* 143*/    Validate.notEmpty(tagName, "Tag name must not be empty.");
/* 144*/    this.tag = Tag.valueOf(tagName, ParseSettings.preserveCase);
/* 145*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Tag tag() {
/* 154*/    return this.tag;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/* 164*/    return this.tag.isBlock();
/*   0*/  }
/*   0*/  
/*   0*/  public String id() {
/* 173*/    return attributes().getIgnoreCase("id");
/*   0*/  }
/*   0*/  
/*   0*/  public Element attr(String attributeKey, String attributeValue) {
/* 183*/    super.attr(attributeKey, attributeValue);
/* 184*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element attr(String attributeKey, boolean attributeValue) {
/* 198*/    attributes().put(attributeKey, attributeValue);
/* 199*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, String> dataset() {
/* 216*/    return attributes().dataset();
/*   0*/  }
/*   0*/  
/*   0*/  public final Element parent() {
/* 221*/    return (Element)this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements parents() {
/* 229*/    Elements parents = new Elements();
/* 230*/    accumulateParents(this, parents);
/* 231*/    return parents;
/*   0*/  }
/*   0*/  
/*   0*/  private static void accumulateParents(Element el, Elements parents) {
/* 235*/    Element parent = el.parent();
/* 236*/    if (parent != null && !parent.tagName().equals("#root")) {
/* 237*/      parents.add(parent);
/* 238*/      accumulateParents(parent, parents);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Element child(int index) {
/* 254*/    return childElementsList().get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements children() {
/* 266*/    return new Elements(childElementsList());
/*   0*/  }
/*   0*/  
/*   0*/  private List<Element> childElementsList() {
/*   0*/    List<Element> children;
/* 276*/    if (this.shadowChildrenRef == null || (children = this.shadowChildrenRef.get()) == null) {
/* 277*/      int size = this.childNodes.size();
/* 278*/      children = new ArrayList<>(size);
/* 280*/      for (int i = 0; i < size; i++) {
/* 281*/        Node node = this.childNodes.get(i);
/* 282*/        if (node instanceof Element) {
/* 283*/            children.add((Element)node); 
/*   0*/           }
/*   0*/      } 
/* 285*/      this.shadowChildrenRef = new WeakReference<>(children);
/*   0*/    } 
/* 287*/    return children;
/*   0*/  }
/*   0*/  
/*   0*/  void nodelistChanged() {
/* 295*/    super.nodelistChanged();
/* 296*/    this.shadowChildrenRef = null;
/*   0*/  }
/*   0*/  
/*   0*/  public List<TextNode> textNodes() {
/* 316*/    List<TextNode> textNodes = new ArrayList<>();
/* 317*/    for (Node node : this.childNodes) {
/* 318*/      if (node instanceof TextNode) {
/* 319*/          textNodes.add((TextNode)node); 
/*   0*/         }
/*   0*/    } 
/* 321*/    return Collections.unmodifiableList(textNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public List<DataNode> dataNodes() {
/* 334*/    List<DataNode> dataNodes = new ArrayList<>();
/* 335*/    for (Node node : this.childNodes) {
/* 336*/      if (node instanceof DataNode) {
/* 337*/          dataNodes.add((DataNode)node); 
/*   0*/         }
/*   0*/    } 
/* 339*/    return Collections.unmodifiableList(dataNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements select(String cssQuery) {
/* 363*/    return Selector.select(cssQuery, this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element selectFirst(String cssQuery) {
/* 374*/    return Selector.selectFirst(cssQuery, this);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean is(String cssQuery) {
/* 383*/    return is(QueryParser.parse(cssQuery));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean is(Evaluator evaluator) {
/* 392*/    return evaluator.matches((Element)root(), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendChild(Node child) {
/* 402*/    Validate.notNull(child);
/* 405*/    reparentChild(child);
/* 406*/    ensureChildNodes();
/* 407*/    this.childNodes.add(child);
/* 408*/    child.setSiblingIndex(this.childNodes.size() - 1);
/* 409*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendTo(Element parent) {
/* 419*/    Validate.notNull(parent);
/* 420*/    parent.appendChild(this);
/* 421*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependChild(Node child) {
/* 431*/    Validate.notNull(child);
/* 433*/    addChildren(0, new Node[] { child });
/* 434*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element insertChildren(int index, Collection<? extends Node> children) {
/* 448*/    Validate.notNull(children, "Children collection to be inserted must not be null.");
/* 449*/    int currentSize = childNodeSize();
/* 450*/    if (index < 0) {
/* 450*/        index += currentSize + 1; 
/*   0*/       }
/* 451*/    Validate.isTrue((index >= 0 && index <= currentSize), "Insert position out of bounds.");
/* 453*/    ArrayList<Node> nodes = new ArrayList<>(children);
/* 454*/    Node[] nodeArray = nodes.<Node>toArray(new Node[nodes.size()]);
/* 455*/    addChildren(index, nodeArray);
/* 456*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element insertChildren(int index, Node... children) {
/* 469*/    Validate.notNull(children, "Children collection to be inserted must not be null.");
/* 470*/    int currentSize = childNodeSize();
/* 471*/    if (index < 0) {
/* 471*/        index += currentSize + 1; 
/*   0*/       }
/* 472*/    Validate.isTrue((index >= 0 && index <= currentSize), "Insert position out of bounds.");
/* 474*/    addChildren(index, children);
/* 475*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendElement(String tagName) {
/* 486*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 487*/    appendChild(child);
/* 488*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependElement(String tagName) {
/* 499*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 500*/    prependChild(child);
/* 501*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendText(String text) {
/* 511*/    Validate.notNull(text);
/* 512*/    TextNode node = new TextNode(text);
/* 513*/    appendChild(node);
/* 514*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependText(String text) {
/* 524*/    Validate.notNull(text);
/* 525*/    TextNode node = new TextNode(text);
/* 526*/    prependChild(node);
/* 527*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element append(String html) {
/* 537*/    Validate.notNull(html);
/* 539*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 540*/    addChildren(nodes.<Node>toArray(new Node[nodes.size()]));
/* 541*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prepend(String html) {
/* 551*/    Validate.notNull(html);
/* 553*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 554*/    addChildren(0, nodes.<Node>toArray(new Node[nodes.size()]));
/* 555*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(String html) {
/* 567*/    return (Element)super.before(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(Node node) {
/* 578*/    return (Element)super.before(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(String html) {
/* 590*/    return (Element)super.after(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(Node node) {
/* 601*/    return (Element)super.after(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element empty() {
/* 609*/    this.childNodes.clear();
/* 610*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element wrap(String html) {
/* 621*/    return (Element)super.wrap(html);
/*   0*/  }
/*   0*/  
/*   0*/  public String cssSelector() {
/* 635*/    if (id().length() > 0) {
/* 636*/        return "#" + id(); 
/*   0*/       }
/* 639*/    String tagName = tagName().replace(':', '|');
/* 640*/    StringBuilder selector = new StringBuilder(tagName);
/* 641*/    String classes = StringUtil.join(classNames(), ".");
/* 642*/    if (classes.length() > 0) {
/* 643*/        selector.append('.').append(classes); 
/*   0*/       }
/* 645*/    if (parent() == null || parent() instanceof Document) {
/* 646*/        return selector.toString(); 
/*   0*/       }
/* 648*/    selector.insert(0, " > ");
/* 649*/    if (parent().select(selector.toString()).size() > 1) {
/* 650*/        selector.append(String.format(":nth-child(%d)", new Object[] { elementSiblingIndex() + 1 })); 
/*   0*/       }
/* 653*/    return parent().cssSelector() + selector.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public Elements siblingElements() {
/* 662*/    if (this.parentNode == null) {
/* 663*/        return new Elements(0); 
/*   0*/       }
/* 665*/    List<Element> elements = parent().childElementsList();
/* 666*/    Elements siblings = new Elements(elements.size() - 1);
/* 667*/    for (Element el : elements) {
/* 668*/      if (el != this) {
/* 669*/          siblings.add(el); 
/*   0*/         }
/*   0*/    } 
/* 670*/    return siblings;
/*   0*/  }
/*   0*/  
/*   0*/  public Element nextElementSibling() {
/* 683*/    if (this.parentNode == null) {
/* 683*/        return null; 
/*   0*/       }
/* 684*/    List<Element> siblings = (List)parent().ensureChildNodes();
/* 685*/    Integer index = indexInList(this, siblings);
/* 686*/    Validate.notNull(index);
/* 687*/    if (siblings.size() > index + 1) {
/* 688*/        return siblings.get(index + 1); 
/*   0*/       }
/* 690*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element previousElementSibling() {
/* 699*/    if (this.parentNode == null) {
/* 699*/        return null; 
/*   0*/       }
/* 700*/    List<Element> siblings = parent().childElementsList();
/* 701*/    Integer index = indexInList(this, siblings);
/* 702*/    Validate.notNull(index);
/* 703*/    if (index > 0) {
/* 704*/        return siblings.get(index - 1); 
/*   0*/       }
/* 706*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element firstElementSibling() {
/* 715*/    List<Element> siblings = parent().childElementsList();
/* 716*/    return (siblings.size() > 1) ? siblings.get(0) : null;
/*   0*/  }
/*   0*/  
/*   0*/  public int elementSiblingIndex() {
/* 725*/    if (parent() == null) {
/* 725*/        return 0; 
/*   0*/       }
/* 726*/    return indexInList(this, parent().childElementsList());
/*   0*/  }
/*   0*/  
/*   0*/  public Element lastElementSibling() {
/* 734*/    List<Element> siblings = parent().childElementsList();
/* 735*/    return (siblings.size() > 1) ? siblings.get(siblings.size() - 1) : null;
/*   0*/  }
/*   0*/  
/*   0*/  private static <E extends Element> int indexInList(Element search, List<E> elements) {
/* 739*/    for (int i = 0; i < elements.size(); i++) {
/* 740*/      if (elements.get(i) == search) {
/* 741*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 743*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByTag(String tagName) {
/* 754*/    Validate.notEmpty(tagName);
/* 755*/    tagName = Normalizer.normalize(tagName);
/* 757*/    return Collector.collect(new Evaluator.Tag(tagName), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element getElementById(String id) {
/* 770*/    Validate.notEmpty(id);
/* 772*/    Elements elements = Collector.collect(new Evaluator.Id(id), this);
/* 773*/    if (elements.size() > 0) {
/* 774*/        return elements.get(0); 
/*   0*/       }
/* 776*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByClass(String className) {
/* 791*/    Validate.notEmpty(className);
/* 793*/    return Collector.collect(new Evaluator.Class(className), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttribute(String key) {
/* 803*/    Validate.notEmpty(key);
/* 804*/    key = key.trim();
/* 806*/    return Collector.collect(new Evaluator.Attribute(key), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeStarting(String keyPrefix) {
/* 816*/    Validate.notEmpty(keyPrefix);
/* 817*/    keyPrefix = keyPrefix.trim();
/* 819*/    return Collector.collect(new Evaluator.AttributeStarting(keyPrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValue(String key, String value) {
/* 830*/    return Collector.collect(new Evaluator.AttributeWithValue(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueNot(String key, String value) {
/* 841*/    return Collector.collect(new Evaluator.AttributeWithValueNot(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
/* 852*/    return Collector.collect(new Evaluator.AttributeWithValueStarting(key, valuePrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
/* 863*/    return Collector.collect(new Evaluator.AttributeWithValueEnding(key, valueSuffix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueContaining(String key, String match) {
/* 874*/    return Collector.collect(new Evaluator.AttributeWithValueContaining(key, match), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
/* 884*/    return Collector.collect(new Evaluator.AttributeWithValueMatching(key, pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 897*/      pattern = Pattern.compile(regex);
/* 898*/    } catch (PatternSyntaxException e) {
/* 899*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 901*/    return getElementsByAttributeValueMatching(key, pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexLessThan(int index) {
/* 910*/    return Collector.collect(new Evaluator.IndexLessThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexGreaterThan(int index) {
/* 919*/    return Collector.collect(new Evaluator.IndexGreaterThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexEquals(int index) {
/* 928*/    return Collector.collect(new Evaluator.IndexEquals(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingText(String searchText) {
/* 939*/    return Collector.collect(new Evaluator.ContainsText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingOwnText(String searchText) {
/* 950*/    return Collector.collect(new Evaluator.ContainsOwnText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(Pattern pattern) {
/* 960*/    return Collector.collect(new Evaluator.Matches(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 972*/      pattern = Pattern.compile(regex);
/* 973*/    } catch (PatternSyntaxException e) {
/* 974*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 976*/    return getElementsMatchingText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(Pattern pattern) {
/* 986*/    return Collector.collect(new Evaluator.MatchesOwn(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 998*/      pattern = Pattern.compile(regex);
/* 999*/    } catch (PatternSyntaxException e) {
/*1000*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/*1002*/    return getElementsMatchingOwnText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getAllElements() {
/*1011*/    return Collector.collect(new Evaluator.AllElements(), this);
/*   0*/  }
/*   0*/  
/*   0*/  public String text() {
/*1024*/    final StringBuilder accum = new StringBuilder();
/*1025*/    NodeTraversor.traverse(new NodeVisitor() {
/*   0*/          public void head(Node node, int depth) {
/*1027*/            if (node instanceof TextNode) {
/*1028*/              TextNode textNode = (TextNode)node;
/*1029*/              Element.appendNormalisedText(accum, textNode);
/*1030*/            } else if (node instanceof Element) {
/*1031*/              Element element = (Element)node;
/*1032*/              if (accum.length() > 0 && (element.isBlock() || element.tag.getName().equals("br")) && !TextNode.lastCharIsWhitespace(accum)) {
/*1035*/                  accum.append(' '); 
/*   0*/                 }
/*   0*/            } 
/*   0*/          }
/*   0*/          
/*   0*/          public void tail(Node node, int depth) {}
/*   0*/        },  this);
/*1042*/    return accum.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  public String ownText() {
/*1057*/    StringBuilder sb = new StringBuilder();
/*1058*/    ownText(sb);
/*1059*/    return sb.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  private void ownText(StringBuilder accum) {
/*1063*/    for (Node child : this.childNodes) {
/*1064*/      if (child instanceof TextNode) {
/*1065*/        TextNode textNode = (TextNode)child;
/*1066*/        appendNormalisedText(accum, textNode);
/*   0*/        continue;
/*   0*/      } 
/*1067*/      if (child instanceof Element) {
/*1068*/          appendWhitespaceIfBr((Element)child, accum); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
/*1074*/    String text = textNode.getWholeText();
/*1076*/    if (preserveWhitespace(textNode.parentNode)) {
/*1077*/      accum.append(text);
/*   0*/    } else {
/*1079*/      StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
/*1083*/    if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum)) {
/*1084*/        accum.append(" "); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  static boolean preserveWhitespace(Node node) {
/*1089*/    if (node != null && node instanceof Element) {
/*1090*/      Element element = (Element)node;
/*1091*/      return (element.tag.preserveWhitespace() || (element.parent() != null && (element.parent()).tag.preserveWhitespace()));
/*   0*/    } 
/*1094*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element text(String text) {
/*1103*/    Validate.notNull(text);
/*1105*/    empty();
/*1106*/    TextNode textNode = new TextNode(text);
/*1107*/    appendChild(textNode);
/*1109*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasText() {
/*1117*/    for (Node child : this.childNodes) {
/*1118*/      if (child instanceof TextNode) {
/*1119*/        TextNode textNode = (TextNode)child;
/*1120*/        if (!textNode.isBlank()) {
/*1121*/            return true; 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/*1122*/      if (child instanceof Element) {
/*1123*/        Element el = (Element)child;
/*1124*/        if (el.hasText()) {
/*1125*/            return true; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1128*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String data() {
/*1141*/    StringBuilder sb = new StringBuilder();
/*1143*/    for (Node childNode : this.childNodes) {
/*1144*/      if (childNode instanceof DataNode) {
/*1145*/        DataNode data = (DataNode)childNode;
/*1146*/        sb.append(data.getWholeData());
/*   0*/        continue;
/*   0*/      } 
/*1147*/      if (childNode instanceof Comment) {
/*1148*/        Comment comment = (Comment)childNode;
/*1149*/        sb.append(comment.getData());
/*   0*/        continue;
/*   0*/      } 
/*1150*/      if (childNode instanceof Element) {
/*1151*/        Element element = (Element)childNode;
/*1152*/        String elementData = element.data();
/*1153*/        sb.append(elementData);
/*   0*/      } 
/*   0*/    } 
/*1156*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String className() {
/*1165*/    return attr("class").trim();
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> classNames() {
/*1175*/    String[] names = classSplit.split(className());
/*1176*/    Set<String> classNames = new LinkedHashSet<>(Arrays.asList(names));
/*1177*/    classNames.remove("");
/*1179*/    return classNames;
/*   0*/  }
/*   0*/  
/*   0*/  public Element classNames(Set<String> classNames) {
/*1188*/    Validate.notNull(classNames);
/*1189*/    attributes().put("class", StringUtil.join(classNames, " "));
/*1190*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasClass(String className) {
/*1200*/    String classAttr = attributes().getIgnoreCase("class");
/*1201*/    int len = classAttr.length();
/*1202*/    int wantLen = className.length();
/*1204*/    if (len == 0 || len < wantLen) {
/*1205*/        return false; 
/*   0*/       }
/*1209*/    if (len == wantLen) {
/*1210*/        return className.equalsIgnoreCase(classAttr); 
/*   0*/       }
/*   0*/    boolean inClass = false;
/*1215*/    int start = 0;
/*1216*/    for (int i = 0; i < len; i++) {
/*1217*/      if (Character.isWhitespace(classAttr.charAt(i))) {
/*1218*/        if (inClass) {
/*1220*/          if (i - start == wantLen && classAttr.regionMatches(true, start, className, 0, wantLen)) {
/*1221*/              return true; 
/*   0*/             }
/*1223*/          inClass = false;
/*   0*/        } 
/*1226*/      } else if (!inClass) {
/*1228*/        inClass = true;
/*1229*/        start = i;
/*   0*/      } 
/*   0*/    } 
/*1235*/    if (inClass && len - start == wantLen) {
/*1236*/        return classAttr.regionMatches(true, start, className, 0, wantLen); 
/*   0*/       }
/*1239*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element addClass(String className) {
/*1248*/    Validate.notNull(className);
/*1250*/    Set<String> classes = classNames();
/*1251*/    classes.add(className);
/*1252*/    classNames(classes);
/*1254*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element removeClass(String className) {
/*1263*/    Validate.notNull(className);
/*1265*/    Set<String> classes = classNames();
/*1266*/    classes.remove(className);
/*1267*/    classNames(classes);
/*1269*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element toggleClass(String className) {
/*1278*/    Validate.notNull(className);
/*1280*/    Set<String> classes = classNames();
/*1281*/    if (classes.contains(className)) {
/*1282*/      classes.remove(className);
/*   0*/    } else {
/*1284*/      classes.add(className);
/*   0*/    } 
/*1285*/    classNames(classes);
/*1287*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String val() {
/*1295*/    if (tagName().equals("textarea")) {
/*1296*/        return text(); 
/*   0*/       }
/*1298*/    return attr("value");
/*   0*/  }
/*   0*/  
/*   0*/  public Element val(String value) {
/*1307*/    if (tagName().equals("textarea")) {
/*1308*/      text(value);
/*   0*/    } else {
/*1310*/      attr("value", value);
/*   0*/    } 
/*1311*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
/*1315*/    if (out.prettyPrint() && (this.tag.formatAsBlock() || (parent() != null && parent().tag().formatAsBlock()) || out.outline())) {
/*1316*/        if (accum instanceof StringBuilder) {
/*1317*/          if (((StringBuilder)accum).length() > 0) {
/*1318*/              indent(accum, depth, out); 
/*   0*/             }
/*   0*/        } else {
/*1320*/          indent(accum, depth, out);
/*   0*/        }  
/*   0*/       }
/*1323*/    accum.append('<').append(tagName());
/*1324*/    if (this.attributes != null) {
/*1324*/        this.attributes.html(accum, out); 
/*   0*/       }
/*1327*/    if (this.childNodes.isEmpty() && this.tag.isSelfClosing()) {
/*1328*/      if (out.syntax() == Document.OutputSettings.Syntax.html && this.tag.isEmpty()) {
/*1329*/        accum.append('>');
/*   0*/      } else {
/*1331*/        accum.append(" />");
/*   0*/      } 
/*   0*/    } else {
/*1334*/      accum.append('>');
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
/*1338*/    if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
/*1339*/      if (out.prettyPrint() && !this.childNodes.isEmpty() && (this.tag.formatAsBlock() || (out.outline() && (this.childNodes.size() > 1 || (this.childNodes.size() == 1 && !(this.childNodes.get(0) instanceof TextNode)))))) {
/*1342*/          indent(accum, depth, out); 
/*   0*/         }
/*1343*/      accum.append("</").append(tagName()).append('>');
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String html() {
/*1355*/    StringBuilder accum = StringUtil.stringBuilder();
/*1356*/    html(accum);
/*1357*/    return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void html(StringBuilder accum) {
/*1361*/    for (Node node : this.childNodes) {
/*1362*/        node.outerHtml(accum); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends Appendable> T html(T appendable) {
/*1370*/    for (Node node : this.childNodes) {
/*1371*/        node.outerHtml((Appendable)appendable); 
/*   0*/       }
/*1373*/    return appendable;
/*   0*/  }
/*   0*/  
/*   0*/  public Element html(String html) {
/*1383*/    empty();
/*1384*/    append(html);
/*1385*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1389*/    return outerHtml();
/*   0*/  }
/*   0*/  
/*   0*/  public Element clone() {
/*1394*/    return (Element)super.clone();
/*   0*/  }
/*   0*/  
/*   0*/  protected Element doClone(Node parent) {
/*1399*/    Element clone = (Element)super.doClone(parent);
/*1400*/    clone.attributes = (this.attributes != null) ? this.attributes.clone() : null;
/*1401*/    clone.baseUri = this.baseUri;
/*1402*/    clone.childNodes = new NodeList(this.childNodes.size());
/*1403*/    clone.childNodes.addAll(this.childNodes);
/*1405*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  private final class NodeList extends ChangeNotifyingArrayList<Node> {
/*   0*/    NodeList(int initialCapacity) {
/*1410*/      super(initialCapacity);
/*   0*/    }
/*   0*/    
/*   0*/    public void onContentsChanged() {
/*1414*/      Element.this.nodelistChanged();
/*   0*/    }
/*   0*/  }
/*   0*/}
