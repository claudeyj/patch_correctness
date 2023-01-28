/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.io.IOException;
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
/*   0*/  private Tag tag;
/*   0*/  
/*  39*/  private static final Pattern classSplit = Pattern.compile("\\s+");
/*   0*/  
/*   0*/  public Element(String tag) {
/*  46*/    this(Tag.valueOf(tag), "", new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri, Attributes attributes) {
/*  59*/    super(baseUri, attributes);
/*  61*/    Validate.notNull(tag);
/*  62*/    this.tag = tag;
/*   0*/  }
/*   0*/  
/*   0*/  public Element(Tag tag, String baseUri) {
/*  74*/    this(tag, baseUri, new Attributes());
/*   0*/  }
/*   0*/  
/*   0*/  public String nodeName() {
/*  79*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public String tagName() {
/*  88*/    return this.tag.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public Element tagName(String tagName) {
/*  99*/    Validate.notEmpty(tagName, "Tag name must not be empty.");
/* 100*/    this.tag = Tag.valueOf(tagName, ParseSettings.preserveCase);
/* 101*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Tag tag() {
/* 110*/    return this.tag;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlock() {
/* 120*/    return this.tag.isBlock();
/*   0*/  }
/*   0*/  
/*   0*/  public String id() {
/* 129*/    return this.attributes.getIgnoreCase("id");
/*   0*/  }
/*   0*/  
/*   0*/  public Element attr(String attributeKey, String attributeValue) {
/* 139*/    super.attr(attributeKey, attributeValue);
/* 140*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element attr(String attributeKey, boolean attributeValue) {
/* 154*/    this.attributes.put(attributeKey, attributeValue);
/* 155*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, String> dataset() {
/* 172*/    return this.attributes.dataset();
/*   0*/  }
/*   0*/  
/*   0*/  public final Element parent() {
/* 177*/    return (Element)this.parentNode;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements parents() {
/* 185*/    Elements parents = new Elements();
/* 186*/    accumulateParents(this, parents);
/* 187*/    return parents;
/*   0*/  }
/*   0*/  
/*   0*/  private static void accumulateParents(Element el, Elements parents) {
/* 191*/    Element parent = el.parent();
/* 192*/    if (parent != null && !parent.tagName().equals("#root")) {
/* 193*/      parents.add(parent);
/* 194*/      accumulateParents(parent, parents);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Element child(int index) {
/* 210*/    return children().get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements children() {
/* 224*/    List<Element> elements = new ArrayList<Element>(this.childNodes.size());
/* 225*/    for (Node node : this.childNodes) {
/* 226*/      if (node instanceof Element) {
/* 227*/          elements.add((Element)node); 
/*   0*/         }
/*   0*/    } 
/* 229*/    return new Elements(elements);
/*   0*/  }
/*   0*/  
/*   0*/  public List<TextNode> textNodes() {
/* 249*/    List<TextNode> textNodes = new ArrayList<TextNode>();
/* 250*/    for (Node node : this.childNodes) {
/* 251*/      if (node instanceof TextNode) {
/* 252*/          textNodes.add((TextNode)node); 
/*   0*/         }
/*   0*/    } 
/* 254*/    return Collections.unmodifiableList(textNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public List<DataNode> dataNodes() {
/* 267*/    List<DataNode> dataNodes = new ArrayList<DataNode>();
/* 268*/    for (Node node : this.childNodes) {
/* 269*/      if (node instanceof DataNode) {
/* 270*/          dataNodes.add((DataNode)node); 
/*   0*/         }
/*   0*/    } 
/* 272*/    return Collections.unmodifiableList(dataNodes);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements select(String cssQuery) {
/* 296*/    return Selector.select(cssQuery, this);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean is(String cssQuery) {
/* 305*/    return is(QueryParser.parse(cssQuery));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean is(Evaluator evaluator) {
/* 314*/    return evaluator.matches((Element)root(), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendChild(Node child) {
/* 324*/    Validate.notNull(child);
/* 327*/    reparentChild(child);
/* 328*/    ensureChildNodes();
/* 329*/    this.childNodes.add(child);
/* 330*/    child.setSiblingIndex(this.childNodes.size() - 1);
/* 331*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependChild(Node child) {
/* 341*/    Validate.notNull(child);
/* 343*/    addChildren(0, new Node[] { child });
/* 344*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element insertChildren(int index, Collection<? extends Node> children) {
/* 358*/    Validate.notNull(children, "Children collection to be inserted must not be null.");
/* 359*/    int currentSize = childNodeSize();
/* 360*/    if (index < 0) {
/* 360*/        index += currentSize + 1; 
/*   0*/       }
/* 361*/    Validate.isTrue((index >= 0 && index <= currentSize), "Insert position out of bounds.");
/* 363*/    ArrayList<Node> nodes = new ArrayList<Node>(children);
/* 364*/    Node[] nodeArray = nodes.<Node>toArray(new Node[nodes.size()]);
/* 365*/    addChildren(index, nodeArray);
/* 366*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendElement(String tagName) {
/* 377*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 378*/    appendChild(child);
/* 379*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependElement(String tagName) {
/* 390*/    Element child = new Element(Tag.valueOf(tagName), baseUri());
/* 391*/    prependChild(child);
/* 392*/    return child;
/*   0*/  }
/*   0*/  
/*   0*/  public Element appendText(String text) {
/* 402*/    Validate.notNull(text);
/* 403*/    TextNode node = new TextNode(text, baseUri());
/* 404*/    appendChild(node);
/* 405*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prependText(String text) {
/* 415*/    Validate.notNull(text);
/* 416*/    TextNode node = new TextNode(text, baseUri());
/* 417*/    prependChild(node);
/* 418*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element append(String html) {
/* 428*/    Validate.notNull(html);
/* 430*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 431*/    addChildren(nodes.<Node>toArray(new Node[nodes.size()]));
/* 432*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element prepend(String html) {
/* 442*/    Validate.notNull(html);
/* 444*/    List<Node> nodes = Parser.parseFragment(html, this, baseUri());
/* 445*/    addChildren(0, nodes.<Node>toArray(new Node[nodes.size()]));
/* 446*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(String html) {
/* 458*/    return (Element)super.before(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element before(Node node) {
/* 469*/    return (Element)super.before(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(String html) {
/* 481*/    return (Element)super.after(html);
/*   0*/  }
/*   0*/  
/*   0*/  public Element after(Node node) {
/* 492*/    return (Element)super.after(node);
/*   0*/  }
/*   0*/  
/*   0*/  public Element empty() {
/* 500*/    this.childNodes.clear();
/* 501*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element wrap(String html) {
/* 512*/    return (Element)super.wrap(html);
/*   0*/  }
/*   0*/  
/*   0*/  public String cssSelector() {
/* 526*/    if (id().length() > 0) {
/* 527*/        return "#" + id(); 
/*   0*/       }
/* 530*/    String tagName = tagName().replace(':', '|');
/* 531*/    StringBuilder selector = new StringBuilder(tagName);
/* 532*/    String classes = StringUtil.join(classNames(), ".");
/* 533*/    if (classes.length() > 0) {
/* 534*/        selector.append('.').append(classes); 
/*   0*/       }
/* 536*/    if (parent() == null || parent() instanceof Document) {
/* 537*/        return selector.toString(); 
/*   0*/       }
/* 539*/    selector.insert(0, " > ");
/* 540*/    if (parent().select(selector.toString()).size() > 1) {
/* 541*/        selector.append(String.format(":nth-child(%d)", new Object[] { elementSiblingIndex() + 1 })); 
/*   0*/       }
/* 544*/    return parent().cssSelector() + selector.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public Elements siblingElements() {
/* 553*/    if (this.parentNode == null) {
/* 554*/        return new Elements(0); 
/*   0*/       }
/* 556*/    List<Element> elements = parent().children();
/* 557*/    Elements siblings = new Elements(elements.size() - 1);
/* 558*/    for (Element el : elements) {
/* 559*/      if (el != this) {
/* 560*/          siblings.add(el); 
/*   0*/         }
/*   0*/    } 
/* 561*/    return siblings;
/*   0*/  }
/*   0*/  
/*   0*/  public Element nextElementSibling() {
/* 574*/    if (this.parentNode == null) {
/* 574*/        return null; 
/*   0*/       }
/* 575*/    List<Element> siblings = parent().children();
/* 576*/    Integer index = indexInList(this, siblings);
/* 577*/    Validate.notNull(index);
/* 578*/    if (siblings.size() > index + 1) {
/* 579*/        return siblings.get(index + 1); 
/*   0*/       }
/* 581*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element previousElementSibling() {
/* 590*/    if (this.parentNode == null) {
/* 590*/        return null; 
/*   0*/       }
/* 591*/    List<Element> siblings = parent().children();
/* 592*/    Integer index = indexInList(this, siblings);
/* 593*/    Validate.notNull(index);
/* 594*/    if (index > 0) {
/* 595*/        return siblings.get(index - 1); 
/*   0*/       }
/* 597*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Element firstElementSibling() {
/* 606*/    List<Element> siblings = parent().children();
/* 607*/    return (siblings.size() > 1) ? siblings.get(0) : null;
/*   0*/  }
/*   0*/  
/*   0*/  public Integer elementSiblingIndex() {
/* 616*/    if (parent() == null) {
/* 616*/        return 0; 
/*   0*/       }
/* 617*/    return indexInList(this, parent().children());
/*   0*/  }
/*   0*/  
/*   0*/  public Element lastElementSibling() {
/* 625*/    List<Element> siblings = parent().children();
/* 626*/    return (siblings.size() > 1) ? siblings.get(siblings.size() - 1) : null;
/*   0*/  }
/*   0*/  
/*   0*/  private static <E extends Element> Integer indexInList(Element search, List<E> elements) {
/* 630*/    Validate.notNull(search);
/* 631*/    Validate.notNull(elements);
/* 633*/    for (int i = 0; i < elements.size(); i++) {
/* 634*/      Element element = (Element)elements.get(i);
/* 635*/      if (element == search) {
/* 636*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 638*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByTag(String tagName) {
/* 649*/    Validate.notEmpty(tagName);
/* 650*/    tagName = tagName.toLowerCase().trim();
/* 652*/    return Collector.collect(new Evaluator.Tag(tagName), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Element getElementById(String id) {
/* 665*/    Validate.notEmpty(id);
/* 667*/    Elements elements = Collector.collect(new Evaluator.Id(id), this);
/* 668*/    if (elements.size() > 0) {
/* 669*/        return elements.get(0); 
/*   0*/       }
/* 671*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByClass(String className) {
/* 686*/    Validate.notEmpty(className);
/* 688*/    return Collector.collect(new Evaluator.Class(className), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttribute(String key) {
/* 698*/    Validate.notEmpty(key);
/* 699*/    key = key.trim();
/* 701*/    return Collector.collect(new Evaluator.Attribute(key), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeStarting(String keyPrefix) {
/* 711*/    Validate.notEmpty(keyPrefix);
/* 712*/    keyPrefix = keyPrefix.trim();
/* 714*/    return Collector.collect(new Evaluator.AttributeStarting(keyPrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValue(String key, String value) {
/* 725*/    return Collector.collect(new Evaluator.AttributeWithValue(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueNot(String key, String value) {
/* 736*/    return Collector.collect(new Evaluator.AttributeWithValueNot(key, value), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
/* 747*/    return Collector.collect(new Evaluator.AttributeWithValueStarting(key, valuePrefix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
/* 758*/    return Collector.collect(new Evaluator.AttributeWithValueEnding(key, valueSuffix), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueContaining(String key, String match) {
/* 769*/    return Collector.collect(new Evaluator.AttributeWithValueContaining(key, match), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
/* 779*/    return Collector.collect(new Evaluator.AttributeWithValueMatching(key, pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByAttributeValueMatching(String key, String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 792*/      pattern = Pattern.compile(regex);
/* 793*/    } catch (PatternSyntaxException e) {
/* 794*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 796*/    return getElementsByAttributeValueMatching(key, pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexLessThan(int index) {
/* 805*/    return Collector.collect(new Evaluator.IndexLessThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexGreaterThan(int index) {
/* 814*/    return Collector.collect(new Evaluator.IndexGreaterThan(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsByIndexEquals(int index) {
/* 823*/    return Collector.collect(new Evaluator.IndexEquals(index), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingText(String searchText) {
/* 834*/    return Collector.collect(new Evaluator.ContainsText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsContainingOwnText(String searchText) {
/* 845*/    return Collector.collect(new Evaluator.ContainsOwnText(searchText), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(Pattern pattern) {
/* 855*/    return Collector.collect(new Evaluator.Matches(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 867*/      pattern = Pattern.compile(regex);
/* 868*/    } catch (PatternSyntaxException e) {
/* 869*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 871*/    return getElementsMatchingText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(Pattern pattern) {
/* 881*/    return Collector.collect(new Evaluator.MatchesOwn(pattern), this);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getElementsMatchingOwnText(String regex) {
/*   0*/    Pattern pattern;
/*   0*/    try {
/* 893*/      pattern = Pattern.compile(regex);
/* 894*/    } catch (PatternSyntaxException e) {
/* 895*/      throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*   0*/    } 
/* 897*/    return getElementsMatchingOwnText(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public Elements getAllElements() {
/* 906*/    return Collector.collect(new Evaluator.AllElements(), this);
/*   0*/  }
/*   0*/  
/*   0*/  public String text() {
/* 919*/    final StringBuilder accum = new StringBuilder();
/* 920*/    new NodeTraversor(new NodeVisitor() {
/*   0*/          public void head(Node node, int depth) {
/* 922*/            if (node instanceof TextNode) {
/* 923*/              TextNode textNode = (TextNode)node;
/* 924*/              Element.appendNormalisedText(accum, textNode);
/* 925*/            } else if (node instanceof Element) {
/* 926*/              Element element = (Element)node;
/* 927*/              if (accum.length() > 0 && (element.isBlock() || element.tag.getName().equals("br")) && !TextNode.lastCharIsWhitespace(accum)) {
/* 930*/                  accum.append(" "); 
/*   0*/                 }
/*   0*/            } 
/*   0*/          }
/*   0*/          
/*   0*/          public void tail(Node node, int depth) {}
/*   0*/        }).traverse(this);
/* 937*/    return accum.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  public String ownText() {
/* 952*/    StringBuilder sb = new StringBuilder();
/* 953*/    ownText(sb);
/* 954*/    return sb.toString().trim();
/*   0*/  }
/*   0*/  
/*   0*/  private void ownText(StringBuilder accum) {
/* 958*/    for (Node child : this.childNodes) {
/* 959*/      if (child instanceof TextNode) {
/* 960*/        TextNode textNode = (TextNode)child;
/* 961*/        appendNormalisedText(accum, textNode);
/*   0*/        continue;
/*   0*/      } 
/* 962*/      if (child instanceof Element) {
/* 963*/          appendWhitespaceIfBr((Element)child, accum); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
/* 969*/    String text = textNode.getWholeText();
/* 971*/    if (preserveWhitespace(textNode.parentNode)) {
/* 972*/      accum.append(text);
/*   0*/    } else {
/* 974*/      StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
/* 978*/    if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum)) {
/* 979*/        accum.append(" "); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  static boolean preserveWhitespace(Node node) {
/* 984*/    if (node != null && node instanceof Element) {
/* 985*/      Element element = (Element)node;
/* 986*/      return (element.tag.preserveWhitespace() || (element.parent() != null && (element.parent()).tag.preserveWhitespace()));
/*   0*/    } 
/* 989*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element text(String text) {
/* 998*/    Validate.notNull(text);
/*1000*/    empty();
/*1001*/    TextNode textNode = new TextNode(text, this.baseUri);
/*1002*/    appendChild(textNode);
/*1004*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasText() {
/*1012*/    for (Node child : this.childNodes) {
/*1013*/      if (child instanceof TextNode) {
/*1014*/        TextNode textNode = (TextNode)child;
/*1015*/        if (!textNode.isBlank()) {
/*1016*/            return true; 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/*1017*/      if (child instanceof Element) {
/*1018*/        Element el = (Element)child;
/*1019*/        if (el.hasText()) {
/*1020*/            return true; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*1023*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String data() {
/*1033*/    StringBuilder sb = new StringBuilder();
/*1035*/    for (Node childNode : this.childNodes) {
/*1036*/      if (childNode instanceof DataNode) {
/*1037*/        DataNode data = (DataNode)childNode;
/*1038*/        sb.append(data.getWholeData());
/*   0*/        continue;
/*   0*/      } 
/*1039*/      if (childNode instanceof Comment) {
/*1040*/        Comment comment = (Comment)childNode;
/*1041*/        sb.append(comment.getData());
/*   0*/        continue;
/*   0*/      } 
/*1042*/      if (childNode instanceof Element) {
/*1043*/        Element element = (Element)childNode;
/*1044*/        String elementData = element.data();
/*1045*/        sb.append(elementData);
/*   0*/      } 
/*   0*/    } 
/*1048*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String className() {
/*1057*/    return attr("class").trim();
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> classNames() {
/*1067*/    String[] names = classSplit.split(className());
/*1068*/    Set<String> classNames = new LinkedHashSet<String>(Arrays.asList(names));
/*1069*/    classNames.remove("");
/*1071*/    return classNames;
/*   0*/  }
/*   0*/  
/*   0*/  public Element classNames(Set<String> classNames) {
/*1080*/    Validate.notNull(classNames);
/*1081*/    this.attributes.put("class", StringUtil.join(classNames, " "));
/*1082*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasClass(String className) {
/*1092*/    String classAttr = this.attributes.get("class");
/*1093*/    int len = classAttr.length();
/*1094*/    int wantLen = className.length();
/*1096*/    if (len == 0 || len < wantLen) {
/*1097*/        return false; 
/*   0*/       }
/*1101*/    if (len == wantLen) {
/*1102*/        return className.equalsIgnoreCase(classAttr); 
/*   0*/       }
/*   0*/    boolean inClass = false;
/*1107*/    int start = 0;
/*1108*/    for (int i = 0; i < len; i++) {
/*1109*/      if (Character.isWhitespace(classAttr.charAt(i))) {
/*1110*/        if (inClass) {
/*1112*/          if (i - start == wantLen && classAttr.regionMatches(true, start, className, 0, wantLen)) {
/*1113*/              return true; 
/*   0*/             }
/*1115*/          inClass = false;
/*   0*/        } 
/*1118*/      } else if (!inClass) {
/*1120*/        inClass = true;
/*1121*/        start = i;
/*   0*/      } 
/*   0*/    } 
/*1127*/    if (inClass && len - start == wantLen) {
/*1128*/        return classAttr.regionMatches(true, start, className, 0, wantLen); 
/*   0*/       }
/*1131*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Element addClass(String className) {
/*1140*/    Validate.notNull(className);
/*1142*/    Set<String> classes = classNames();
/*1143*/    classes.add(className);
/*1144*/    classNames(classes);
/*1146*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element removeClass(String className) {
/*1155*/    Validate.notNull(className);
/*1157*/    Set<String> classes = classNames();
/*1158*/    classes.remove(className);
/*1159*/    classNames(classes);
/*1161*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Element toggleClass(String className) {
/*1170*/    Validate.notNull(className);
/*1172*/    Set<String> classes = classNames();
/*1173*/    if (classes.contains(className)) {
/*1174*/      classes.remove(className);
/*   0*/    } else {
/*1176*/      classes.add(className);
/*   0*/    } 
/*1177*/    classNames(classes);
/*1179*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String val() {
/*1187*/    if (tagName().equals("textarea")) {
/*1188*/        return text(); 
/*   0*/       }
/*1190*/    return attr("value");
/*   0*/  }
/*   0*/  
/*   0*/  public Element val(String value) {
/*1199*/    if (tagName().equals("textarea")) {
/*1200*/      text(value);
/*   0*/    } else {
/*1202*/      attr("value", value);
/*   0*/    } 
/*1203*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
/*1207*/    if (out.prettyPrint() && (this.tag.formatAsBlock() || (parent() != null && parent().tag().formatAsBlock()) || out.outline())) {
/*1208*/        if (accum instanceof StringBuilder) {
/*1209*/          if (((StringBuilder)accum).length() > 0) {
/*1210*/              indent(accum, depth, out); 
/*   0*/             }
/*   0*/        } else {
/*1212*/          indent(accum, depth, out);
/*   0*/        }  
/*   0*/       }
/*1215*/    accum.append("<").append(tagName());
/*1218*/    this.attributes.html(accum, out);
/*1221*/    if (this.childNodes.isEmpty() && this.tag.isSelfClosing()) {
/*1222*/      if (out.syntax() == Document.OutputSettings.Syntax.html && this.tag.isEmpty()) {
/*1223*/        accum.append('>');
/*   0*/      } else {
/*1225*/        accum.append(" />");
/*   0*/      } 
/*   0*/    } else {
/*1228*/      accum.append(">");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
/*1232*/    if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
/*1233*/      if (out.prettyPrint() && !this.childNodes.isEmpty() && (this.tag.formatAsBlock() || (out.outline() && (this.childNodes.size() > 1 || (this.childNodes.size() == 1 && !(this.childNodes.get(0) instanceof TextNode)))))) {
/*1236*/          indent(accum, depth, out); 
/*   0*/         }
/*1237*/      accum.append("</").append(tagName()).append(">");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String html() {
/*1249*/    StringBuilder accum = new StringBuilder();
/*1250*/    html(accum);
/*1251*/    return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void html(StringBuilder accum) {
/*1255*/    for (Node node : this.childNodes) {
/*1256*/        node.outerHtml(accum); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends Appendable> T html(T appendable) {
/*1264*/    for (Node node : this.childNodes) {
/*1265*/        node.outerHtml((Appendable)appendable); 
/*   0*/       }
/*1267*/    return appendable;
/*   0*/  }
/*   0*/  
/*   0*/  public Element html(String html) {
/*1277*/    empty();
/*1278*/    append(html);
/*1279*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1283*/    return outerHtml();
/*   0*/  }
/*   0*/  
/*   0*/  public Element clone() {
/*1288*/    return (Element)super.clone();
/*   0*/  }
/*   0*/}
