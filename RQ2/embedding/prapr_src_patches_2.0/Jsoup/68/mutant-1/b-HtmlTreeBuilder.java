/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/import java.io.Reader;
/*   0*/import java.io.StringReader;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.List;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.nodes.Attributes;
/*   0*/import org.jsoup.nodes.Comment;
/*   0*/import org.jsoup.nodes.DataNode;
/*   0*/import org.jsoup.nodes.Document;
/*   0*/import org.jsoup.nodes.Element;
/*   0*/import org.jsoup.nodes.FormElement;
/*   0*/import org.jsoup.nodes.Node;
/*   0*/import org.jsoup.nodes.TextNode;
/*   0*/import org.jsoup.select.Elements;
/*   0*/
/*   0*/public class HtmlTreeBuilder extends TreeBuilder {
/*  26*/  static final String[] TagsSearchInScope = new String[] { "applet", "caption", "html", "marquee", "object", "table", "td", "th" };
/*   0*/  
/*  27*/  static final String[] TagSearchList = new String[] { "ol", "ul" };
/*   0*/  
/*  28*/  static final String[] TagSearchButton = new String[] { "button" };
/*   0*/  
/*  29*/  static final String[] TagSearchTableScope = new String[] { "html", "table" };
/*   0*/  
/*  30*/  static final String[] TagSearchSelectScope = new String[] { "optgroup", "option" };
/*   0*/  
/*  31*/  static final String[] TagSearchEndTags = new String[] { "dd", "dt", "li", "optgroup", "option", "p", "rp", "rt" };
/*   0*/  
/*  32*/  static final String[] TagSearchSpecial = new String[] { 
/*  32*/      "address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", 
/*  32*/      "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", 
/*  32*/      "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", 
/*  32*/      "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", 
/*  32*/      "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", 
/*  32*/      "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", 
/*  32*/      "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", 
/*  32*/      "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp" };
/*   0*/  
/*   0*/  public static final int MaxScopeSearchDepth = 100;
/*   0*/  
/*   0*/  private HtmlTreeBuilderState state;
/*   0*/  
/*   0*/  private HtmlTreeBuilderState originalState;
/*   0*/  
/*   0*/  private boolean baseUriSetFromDoc;
/*   0*/  
/*   0*/  private Element headElement;
/*   0*/  
/*   0*/  private FormElement formElement;
/*   0*/  
/*   0*/  private Element contextElement;
/*   0*/  
/*   0*/  private ArrayList<Element> formattingElements;
/*   0*/  
/*   0*/  private List<String> pendingTableCharacters;
/*   0*/  
/*   0*/  private Token.EndTag emptyEnd;
/*   0*/  
/*   0*/  private boolean framesetOk;
/*   0*/  
/*   0*/  private boolean fosterInserts;
/*   0*/  
/*   0*/  private boolean fragmentParsing;
/*   0*/  
/*   0*/  ParseSettings defaultSettings() {
/*  61*/    return ParseSettings.htmlDefault;
/*   0*/  }
/*   0*/  
/*   0*/  protected void initialiseParse(Reader input, String baseUri, ParseErrorList errors, ParseSettings settings) {
/*  66*/    super.initialiseParse(input, baseUri, errors, settings);
/*  69*/    this.state = HtmlTreeBuilderState.Initial;
/*  70*/    this.originalState = null;
/*  71*/    this.baseUriSetFromDoc = false;
/*  72*/    this.headElement = null;
/*  73*/    this.formElement = null;
/*  74*/    this.contextElement = null;
/*  75*/    this.formattingElements = new ArrayList<>();
/*  76*/    this.pendingTableCharacters = new ArrayList<>();
/*  77*/    this.emptyEnd = new Token.EndTag();
/*  78*/    this.framesetOk = true;
/*  79*/    this.fosterInserts = false;
/*  80*/    this.fragmentParsing = false;
/*   0*/  }
/*   0*/  
/*   0*/  List<Node> parseFragment(String inputFragment, Element context, String baseUri, ParseErrorList errors, ParseSettings settings) {
/*  85*/    this.state = HtmlTreeBuilderState.Initial;
/*  86*/    initialiseParse(new StringReader(inputFragment), baseUri, errors, settings);
/*  87*/    this.contextElement = context;
/*  88*/    this.fragmentParsing = true;
/*  89*/    Element root = null;
/*  91*/    if (context != null) {
/*  92*/      if (context.ownerDocument() != null) {
/*  93*/          this.doc.quirksMode(context.ownerDocument().quirksMode()); 
/*   0*/         }
/*  96*/      String contextTag = context.tagName();
/*  97*/      if (StringUtil.in(contextTag, new String[] { "title", "textarea" })) {
/*  98*/        this.tokeniser.transition(TokeniserState.Rcdata);
/*  99*/      } else if (StringUtil.in(contextTag, new String[] { "iframe", "noembed", "noframes", "style", "xmp" })) {
/* 100*/        this.tokeniser.transition(TokeniserState.Rawtext);
/* 101*/      } else if (contextTag.equals("script")) {
/* 102*/        this.tokeniser.transition(TokeniserState.ScriptData);
/* 103*/      } else if (contextTag.equals("noscript")) {
/* 104*/        this.tokeniser.transition(TokeniserState.Data);
/* 105*/      } else if (contextTag.equals("plaintext")) {
/* 106*/        this.tokeniser.transition(TokeniserState.Data);
/*   0*/      } else {
/* 108*/        this.tokeniser.transition(TokeniserState.Data);
/*   0*/      } 
/* 110*/      root = new Element(Tag.valueOf("html", settings), baseUri);
/* 111*/      this.doc.appendChild(root);
/* 112*/      this.stack.add(root);
/* 113*/      resetInsertionMode();
/* 117*/      Elements contextChain = context.parents();
/* 118*/      contextChain.add(0, context);
/* 119*/      for (Element parent : (Iterable<Element>)contextChain) {
/* 120*/        if (parent instanceof FormElement) {
/* 121*/          this.formElement = (FormElement)parent;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 127*/    runParser();
/* 128*/    if (context != null) {
/* 129*/        return root.childNodes(); 
/*   0*/       }
/* 131*/    return this.doc.childNodes();
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean process(Token token) {
/* 136*/    this.currentToken = token;
/* 137*/    return this.state.process(token, this);
/*   0*/  }
/*   0*/  
/*   0*/  boolean process(Token token, HtmlTreeBuilderState state) {
/* 141*/    this.currentToken = token;
/* 142*/    return state.process(token, this);
/*   0*/  }
/*   0*/  
/*   0*/  void transition(HtmlTreeBuilderState state) {
/* 146*/    this.state = state;
/*   0*/  }
/*   0*/  
/*   0*/  HtmlTreeBuilderState state() {
/* 150*/    return this.state;
/*   0*/  }
/*   0*/  
/*   0*/  void markInsertionMode() {
/* 154*/    this.originalState = this.state;
/*   0*/  }
/*   0*/  
/*   0*/  HtmlTreeBuilderState originalState() {
/* 158*/    return this.originalState;
/*   0*/  }
/*   0*/  
/*   0*/  void framesetOk(boolean framesetOk) {
/* 162*/    this.framesetOk = framesetOk;
/*   0*/  }
/*   0*/  
/*   0*/  boolean framesetOk() {
/* 166*/    return this.framesetOk;
/*   0*/  }
/*   0*/  
/*   0*/  Document getDocument() {
/* 170*/    return this.doc;
/*   0*/  }
/*   0*/  
/*   0*/  String getBaseUri() {
/* 174*/    return this.baseUri;
/*   0*/  }
/*   0*/  
/*   0*/  void maybeSetBaseUri(Element base) {
/* 178*/    if (this.baseUriSetFromDoc) {
/*   0*/        return; 
/*   0*/       }
/* 181*/    String href = base.absUrl("href");
/* 182*/    if (href.length() != 0) {
/* 183*/      this.baseUri = href;
/* 184*/      this.baseUriSetFromDoc = true;
/* 185*/      this.doc.setBaseUri(href);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  boolean isFragmentParsing() {
/* 190*/    return this.fragmentParsing;
/*   0*/  }
/*   0*/  
/*   0*/  void error(HtmlTreeBuilderState state) {
/* 194*/    if (this.errors.canAddError()) {
/* 195*/        this.errors.add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", new Object[] { this.currentToken.tokenType(), state })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  Element insert(Token.StartTag startTag) {
/* 201*/    if (startTag.isSelfClosing()) {
/* 202*/      Element element = insertEmpty(startTag);
/* 203*/      this.stack.add(element);
/* 204*/      this.tokeniser.transition(TokeniserState.Data);
/* 205*/      this.tokeniser.emit(this.emptyEnd.reset().name(element.tagName()));
/* 206*/      return element;
/*   0*/    } 
/* 209*/    Element el = new Element(Tag.valueOf(startTag.name(), this.settings), this.baseUri, this.settings.normalizeAttributes(startTag.attributes));
/* 210*/    insert(el);
/* 211*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  Element insertStartTag(String startTagName) {
/* 215*/    Element el = new Element(Tag.valueOf(startTagName, this.settings), this.baseUri);
/* 216*/    insert(el);
/* 217*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Element el) {
/* 221*/    insertNode(el);
/* 222*/    this.stack.add(el);
/*   0*/  }
/*   0*/  
/*   0*/  Element insertEmpty(Token.StartTag startTag) {
/* 226*/    Tag tag = Tag.valueOf(startTag.name(), this.settings);
/* 227*/    Element el = new Element(tag, this.baseUri, startTag.attributes);
/* 228*/    insertNode(el);
/* 229*/    if (startTag.isSelfClosing()) {
/* 230*/        if (tag.isKnownTag()) {
/* 231*/          if (!tag.isEmpty()) {
/* 232*/              this.tokeniser.error("Tag cannot be self closing; not a void tag"); 
/*   0*/             }
/*   0*/        } else {
/* 235*/          tag.setSelfClosing();
/*   0*/        }  
/*   0*/       }
/* 237*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  FormElement insertForm(Token.StartTag startTag, boolean onStack) {
/* 241*/    Tag tag = Tag.valueOf(startTag.name(), this.settings);
/* 242*/    FormElement el = new FormElement(tag, this.baseUri, startTag.attributes);
/* 243*/    setFormElement(el);
/* 244*/    insertNode(el);
/* 245*/    if (onStack) {
/* 246*/        this.stack.add(el); 
/*   0*/       }
/* 247*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Comment commentToken) {
/* 251*/    Comment comment = new Comment(commentToken.getData());
/* 252*/    insertNode(comment);
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Character characterToken) {
/*   0*/    Node node;
/* 258*/    String tagName = currentElement().tagName();
/* 259*/    if (tagName.equals("script") || tagName.equals("style")) {
/* 260*/      node = new DataNode(characterToken.getData());
/*   0*/    } else {
/* 262*/      node = new TextNode(characterToken.getData());
/*   0*/    } 
/* 263*/    currentElement().appendChild(node);
/*   0*/  }
/*   0*/  
/*   0*/  private void insertNode(Node node) {
/* 268*/    if (this.stack.size() == 0) {
/* 269*/      this.doc.appendChild(node);
/* 270*/    } else if (isFosterInserts()) {
/* 271*/      insertInFosterParent(node);
/*   0*/    } else {
/* 273*/      currentElement().appendChild(node);
/*   0*/    } 
/* 276*/    if (node instanceof Element && ((Element)node).tag().isFormListed() && 
/* 277*/      this.formElement != null) {
/* 278*/        this.formElement.addElement((Element)node); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  Element pop() {
/* 283*/    int size = this.stack.size();
/* 284*/    return this.stack.remove(size - 1);
/*   0*/  }
/*   0*/  
/*   0*/  void push(Element element) {
/* 288*/    this.stack.add(element);
/*   0*/  }
/*   0*/  
/*   0*/  ArrayList<Element> getStack() {
/* 292*/    return this.stack;
/*   0*/  }
/*   0*/  
/*   0*/  boolean onStack(Element el) {
/* 296*/    return isElementInQueue(this.stack, el);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isElementInQueue(ArrayList<Element> queue, Element element) {
/* 300*/    for (int pos = queue.size() - 1; pos >= 0; pos--) {
/* 301*/      Element next = queue.get(pos);
/* 302*/      if (next == element) {
/* 303*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 306*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  Element getFromStack(String elName) {
/* 310*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 311*/      Element next = this.stack.get(pos);
/* 312*/      if (next.nodeName().equals(elName)) {
/* 313*/          return next; 
/*   0*/         }
/*   0*/    } 
/* 316*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  boolean removeFromStack(Element el) {
/* 320*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 321*/      Element next = this.stack.get(pos);
/* 322*/      if (next == el) {
/* 323*/        this.stack.remove(pos);
/* 324*/        return true;
/*   0*/      } 
/*   0*/    } 
/* 327*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  void popStackToClose(String elName) {
/* 331*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 332*/      Element next = this.stack.get(pos);
/* 333*/      this.stack.remove(pos);
/* 334*/      if (next.nodeName().equals(elName)) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void popStackToClose(String... elNames) {
/* 341*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 342*/      Element next = this.stack.get(pos);
/* 343*/      this.stack.remove(pos);
/* 344*/      if (StringUtil.inSorted(next.nodeName(), elNames)) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void popStackToBefore(String elName) {
/* 350*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 351*/      Element next = this.stack.get(pos);
/* 352*/      if (next.nodeName().equals(elName)) {
/*   0*/          break; 
/*   0*/         }
/* 355*/      this.stack.remove(pos);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void clearStackToTableContext() {
/* 361*/    clearStackToContext(new String[] { "table" });
/*   0*/  }
/*   0*/  
/*   0*/  void clearStackToTableBodyContext() {
/* 365*/    clearStackToContext(new String[] { "tbody", "tfoot", "thead", "template" });
/*   0*/  }
/*   0*/  
/*   0*/  void clearStackToTableRowContext() {
/* 369*/    clearStackToContext(new String[] { "tr", "template" });
/*   0*/  }
/*   0*/  
/*   0*/  private void clearStackToContext(String... nodeNames) {
/* 373*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 374*/      Element next = this.stack.get(pos);
/* 375*/      if (StringUtil.in(next.nodeName(), nodeNames) || next.nodeName().equals("html")) {
/*   0*/          break; 
/*   0*/         }
/* 378*/      this.stack.remove(pos);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  Element aboveOnStack(Element el) {
/* 383*/    assert onStack(el);
/* 384*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 385*/      Element next = this.stack.get(pos);
/* 386*/      if (next == el) {
/* 387*/          return this.stack.get(pos - 1); 
/*   0*/         }
/*   0*/    } 
/* 390*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  void insertOnStackAfter(Element after, Element in) {
/* 394*/    int i = this.stack.lastIndexOf(after);
/* 395*/    Validate.isTrue((i != -1));
/* 396*/    this.stack.add(i + 1, in);
/*   0*/  }
/*   0*/  
/*   0*/  void replaceOnStack(Element out, Element in) {
/* 400*/    replaceInQueue(this.stack, out, in);
/*   0*/  }
/*   0*/  
/*   0*/  private void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
/* 404*/    int i = queue.lastIndexOf(out);
/* 405*/    Validate.isTrue((i != -1));
/* 406*/    queue.set(i, in);
/*   0*/  }
/*   0*/  
/*   0*/  void resetInsertionMode() {
/*   0*/    boolean last = false;
/* 411*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 412*/      Element node = this.stack.get(pos);
/* 413*/      if (pos == 0) {
/* 414*/        last = true;
/* 415*/        node = this.contextElement;
/*   0*/      } 
/* 417*/      String name = node.nodeName();
/* 418*/      if ("select".equals(name)) {
/* 419*/        transition(HtmlTreeBuilderState.InSelect);
/*   0*/        break;
/*   0*/      } 
/* 421*/      if ("td".equals(name) || ("th".equals(name) && !last)) {
/* 422*/        transition(HtmlTreeBuilderState.InCell);
/*   0*/        break;
/*   0*/      } 
/* 424*/      if ("tr".equals(name)) {
/* 425*/        transition(HtmlTreeBuilderState.InRow);
/*   0*/        break;
/*   0*/      } 
/* 427*/      if ("tbody".equals(name) || "thead".equals(name) || "tfoot".equals(name)) {
/* 428*/        transition(HtmlTreeBuilderState.InTableBody);
/*   0*/        break;
/*   0*/      } 
/* 430*/      if ("caption".equals(name)) {
/* 431*/        transition(HtmlTreeBuilderState.InCaption);
/*   0*/        break;
/*   0*/      } 
/* 433*/      if ("colgroup".equals(name)) {
/* 434*/        transition(HtmlTreeBuilderState.InColumnGroup);
/*   0*/        break;
/*   0*/      } 
/* 436*/      if ("table".equals(name)) {
/* 437*/        transition(HtmlTreeBuilderState.InTable);
/*   0*/        break;
/*   0*/      } 
/* 439*/      if ("head".equals(name)) {
/* 440*/        transition(HtmlTreeBuilderState.InBody);
/*   0*/        break;
/*   0*/      } 
/* 442*/      if ("body".equals(name)) {
/* 443*/        transition(HtmlTreeBuilderState.InBody);
/*   0*/        break;
/*   0*/      } 
/* 445*/      if ("frameset".equals(name)) {
/* 446*/        transition(HtmlTreeBuilderState.InFrameset);
/*   0*/        break;
/*   0*/      } 
/* 448*/      if ("html".equals(name)) {
/* 449*/        transition(HtmlTreeBuilderState.BeforeHead);
/*   0*/        break;
/*   0*/      } 
/* 451*/      if (last) {
/* 452*/        transition(HtmlTreeBuilderState.InBody);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/* 459*/  private String[] specificScopeTarget = new String[] { null };
/*   0*/  
/*   0*/  private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
/* 462*/    this.specificScopeTarget[0] = targetName;
/* 463*/    return inSpecificScope(this.specificScopeTarget, baseTypes, extraTypes);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
/* 468*/    int bottom = this.stack.size() - 1;
/* 469*/    if (MaxScopeSearchDepth > 100) {
/* 470*/        bottom = 100; 
/*   0*/       }
/* 472*/    int top = (bottom > 100) ? (bottom - 100) : 0;
/* 475*/    for (int pos = bottom; pos >= top; pos--) {
/* 476*/      String elName = ((Element)this.stack.get(pos)).nodeName();
/* 477*/      if (StringUtil.inSorted(elName, targetNames)) {
/* 478*/          return true; 
/*   0*/         }
/* 479*/      if (StringUtil.inSorted(elName, baseTypes)) {
/* 480*/          return false; 
/*   0*/         }
/* 481*/      if (extraTypes != null && StringUtil.inSorted(elName, extraTypes)) {
/* 482*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 485*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean inScope(String[] targetNames) {
/* 489*/    return inSpecificScope(targetNames, TagsSearchInScope, null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inScope(String targetName) {
/* 493*/    return inScope(targetName, null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inScope(String targetName, String[] extras) {
/* 497*/    return inSpecificScope(targetName, TagsSearchInScope, extras);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inListItemScope(String targetName) {
/* 503*/    return inScope(targetName, TagSearchList);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inButtonScope(String targetName) {
/* 507*/    return inScope(targetName, TagSearchButton);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inTableScope(String targetName) {
/* 511*/    return inSpecificScope(targetName, TagSearchTableScope, null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inSelectScope(String targetName) {
/* 515*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 516*/      Element el = this.stack.get(pos);
/* 517*/      String elName = el.nodeName();
/* 518*/      if (elName.equals(targetName)) {
/* 519*/          return true; 
/*   0*/         }
/* 520*/      if (!StringUtil.inSorted(elName, TagSearchSelectScope)) {
/* 521*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 523*/    Validate.fail("Should not be reachable");
/* 524*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  void setHeadElement(Element headElement) {
/* 528*/    this.headElement = headElement;
/*   0*/  }
/*   0*/  
/*   0*/  Element getHeadElement() {
/* 532*/    return this.headElement;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isFosterInserts() {
/* 536*/    return this.fosterInserts;
/*   0*/  }
/*   0*/  
/*   0*/  void setFosterInserts(boolean fosterInserts) {
/* 540*/    this.fosterInserts = fosterInserts;
/*   0*/  }
/*   0*/  
/*   0*/  FormElement getFormElement() {
/* 544*/    return this.formElement;
/*   0*/  }
/*   0*/  
/*   0*/  void setFormElement(FormElement formElement) {
/* 548*/    this.formElement = formElement;
/*   0*/  }
/*   0*/  
/*   0*/  void newPendingTableCharacters() {
/* 552*/    this.pendingTableCharacters = new ArrayList<>();
/*   0*/  }
/*   0*/  
/*   0*/  List<String> getPendingTableCharacters() {
/* 556*/    return this.pendingTableCharacters;
/*   0*/  }
/*   0*/  
/*   0*/  void setPendingTableCharacters(List<String> pendingTableCharacters) {
/* 560*/    this.pendingTableCharacters = pendingTableCharacters;
/*   0*/  }
/*   0*/  
/*   0*/  void generateImpliedEndTags(String excludeTag) {
/* 573*/    while (excludeTag != null && !currentElement().nodeName().equals(excludeTag) && StringUtil.inSorted(currentElement().nodeName(), TagSearchEndTags)) {
/* 575*/        pop(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  void generateImpliedEndTags() {
/* 579*/    generateImpliedEndTags(null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isSpecial(Element el) {
/* 585*/    String name = el.nodeName();
/* 586*/    return StringUtil.inSorted(name, TagSearchSpecial);
/*   0*/  }
/*   0*/  
/*   0*/  Element lastFormattingElement() {
/* 590*/    return (this.formattingElements.size() > 0) ? this.formattingElements.get(this.formattingElements.size() - 1) : null;
/*   0*/  }
/*   0*/  
/*   0*/  Element removeLastFormattingElement() {
/* 594*/    int size = this.formattingElements.size();
/* 595*/    if (size > 0) {
/* 596*/        return this.formattingElements.remove(size - 1); 
/*   0*/       }
/* 598*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  void pushActiveFormattingElements(Element in) {
/* 603*/    int numSeen = 0;
/* 604*/    for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
/* 605*/      Element el = this.formattingElements.get(pos);
/* 606*/      if (el == null) {
/*   0*/          break; 
/*   0*/         }
/* 609*/      if (isSameFormattingElement(in, el)) {
/* 610*/          numSeen++; 
/*   0*/         }
/* 612*/      if (numSeen == 3) {
/* 613*/        this.formattingElements.remove(pos);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/* 617*/    this.formattingElements.add(in);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isSameFormattingElement(Element a, Element b) {
/* 622*/    return (a.nodeName().equals(b.nodeName()) && a.attributes().equals(b.attributes()));
/*   0*/  }
/*   0*/  
/*   0*/  void reconstructFormattingElements() {
/* 629*/    Element last = lastFormattingElement();
/* 630*/    if (last == null || onStack(last)) {
/*   0*/        return; 
/*   0*/       }
/* 633*/    Element entry = last;
/* 634*/    int size = this.formattingElements.size();
/* 635*/    int pos = size - 1;
/*   0*/    boolean skip = false;
/*   0*/    do {
/* 638*/      if (pos == 0) {
/* 639*/        skip = true;
/*   0*/        break;
/*   0*/      } 
/* 642*/      entry = this.formattingElements.get(--pos);
/* 643*/    } while (entry != null && !onStack(entry));
/*   0*/    do {
/* 647*/      if (!skip) {
/* 648*/          entry = this.formattingElements.get(++pos); 
/*   0*/         }
/* 649*/      Validate.notNull(entry);
/* 652*/      skip = false;
/* 653*/      Element newEl = insertStartTag(entry.nodeName());
/* 655*/      newEl.attributes().addAll(entry.attributes());
/* 658*/      this.formattingElements.set(pos, newEl);
/* 661*/    } while (pos != size - 1);
/*   0*/  }
/*   0*/  
/*   0*/  void clearFormattingElementsToLastMarker() {
/* 667*/    while (!this.formattingElements.isEmpty()) {
/* 668*/      Element el = removeLastFormattingElement();
/* 669*/      if (el == null) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void removeFromActiveFormattingElements(Element el) {
/* 675*/    for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
/* 676*/      Element next = this.formattingElements.get(pos);
/* 677*/      if (next == el) {
/* 678*/        this.formattingElements.remove(pos);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  boolean isInActiveFormattingElements(Element el) {
/* 685*/    return isElementInQueue(this.formattingElements, el);
/*   0*/  }
/*   0*/  
/*   0*/  Element getActiveFormattingElement(String nodeName) {
/* 689*/    for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
/* 690*/      Element next = this.formattingElements.get(pos);
/* 691*/      if (next == null) {
/*   0*/          break; 
/*   0*/         }
/* 693*/      if (next.nodeName().equals(nodeName)) {
/* 694*/          return next; 
/*   0*/         }
/*   0*/    } 
/* 696*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  void replaceActiveFormattingElement(Element out, Element in) {
/* 700*/    replaceInQueue(this.formattingElements, out, in);
/*   0*/  }
/*   0*/  
/*   0*/  void insertMarkerToFormattingElements() {
/* 704*/    this.formattingElements.add(null);
/*   0*/  }
/*   0*/  
/*   0*/  void insertInFosterParent(Node in) {
/*   0*/    Element fosterParent;
/* 709*/    Element lastTable = getFromStack("table");
/*   0*/    boolean isLastTableParent = false;
/* 711*/    if (lastTable != null) {
/* 712*/      if (lastTable.parent() != null) {
/* 713*/        fosterParent = lastTable.parent();
/* 714*/        isLastTableParent = true;
/*   0*/      } else {
/* 716*/        fosterParent = aboveOnStack(lastTable);
/*   0*/      } 
/*   0*/    } else {
/* 718*/      fosterParent = this.stack.get(0);
/*   0*/    } 
/* 721*/    if (isLastTableParent) {
/* 722*/      Validate.notNull(lastTable);
/* 723*/      lastTable.before(in);
/*   0*/    } else {
/* 726*/      fosterParent.appendChild(in);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 731*/    return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + this.state + ", currentElement=" + currentElement() + '}';
/*   0*/  }
/*   0*/}
