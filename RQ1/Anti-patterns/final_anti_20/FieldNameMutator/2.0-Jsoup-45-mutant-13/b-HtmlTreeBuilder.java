/*   0*/package org.jsoup.parser;
/*   0*/
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
/*  16*/  private static final String[] TagsScriptStyle = new String[] { "script", "style" };
/*   0*/  
/*  17*/  public static final String[] TagsSearchInScope = new String[] { "applet", "caption", "html", "table", "td", "th", "marquee", "object" };
/*   0*/  
/*  18*/  private static final String[] TagSearchList = new String[] { "ol", "ul" };
/*   0*/  
/*  19*/  private static final String[] TagSearchButton = new String[] { "button" };
/*   0*/  
/*  20*/  private static final String[] TagSearchTableScope = new String[] { "html", "table" };
/*   0*/  
/*  21*/  private static final String[] TagSearchSelectScope = new String[] { "optgroup", "option" };
/*   0*/  
/*  22*/  private static final String[] TagSearchEndTags = new String[] { "dd", "dt", "li", "option", "optgroup", "p", "rp", "rt" };
/*   0*/  
/*  23*/  private static final String[] TagSearchSpecial = new String[] { 
/*  23*/      "address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", 
/*  23*/      "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", 
/*  23*/      "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", 
/*  23*/      "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", 
/*  23*/      "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", 
/*  23*/      "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", 
/*  23*/      "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", 
/*  23*/      "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp" };
/*   0*/  
/*   0*/  private HtmlTreeBuilderState state;
/*   0*/  
/*   0*/  private HtmlTreeBuilderState originalState;
/*   0*/  
/*   0*/  private boolean baseUriSetFromDoc = false;
/*   0*/  
/*   0*/  private Element headElement;
/*   0*/  
/*   0*/  private FormElement formElement;
/*   0*/  
/*   0*/  private Element contextElement;
/*   0*/  
/*  39*/  private ArrayList<Element> formattingElements = new ArrayList<Element>();
/*   0*/  
/*  40*/  private List<String> pendingTableCharacters = new ArrayList<String>();
/*   0*/  
/*  41*/  private Token.EndTag emptyEnd = new Token.EndTag();
/*   0*/  
/*   0*/  private boolean framesetOk = true;
/*   0*/  
/*   0*/  private boolean fosterInserts = false;
/*   0*/  
/*   0*/  private boolean fragmentParsing = false;
/*   0*/  
/*   0*/  private String[] specificScopeTarget;
/*   0*/  
/*   0*/  Document parse(String input, String baseUri, ParseErrorList errors) {
/*  51*/    this.state = HtmlTreeBuilderState.Initial;
/*  52*/    this.baseUriSetFromDoc = false;
/*  53*/    return super.parse(input, baseUri, errors);
/*   0*/  }
/*   0*/  
/*   0*/  List<Node> parseFragment(String inputFragment, Element context, String baseUri, ParseErrorList errors) {
/*  58*/    this.state = HtmlTreeBuilderState.Initial;
/*  59*/    initialiseParse(inputFragment, baseUri, errors);
/*  60*/    this.contextElement = context;
/*  61*/    this.fragmentParsing = true;
/*  62*/    Element root = null;
/*  64*/    if (context != null) {
/*  65*/      if (context.ownerDocument() != null) {
/*  66*/          this.doc.quirksMode(context.ownerDocument().quirksMode()); 
/*   0*/         }
/*  69*/      String contextTag = context.tagName();
/*  70*/      if (StringUtil.in(contextTag, new String[] { "title", "textarea" })) {
/*  71*/        this.tokeniser.transition(TokeniserState.Rcdata);
/*  72*/      } else if (StringUtil.in(contextTag, new String[] { "iframe", "noembed", "noframes", "style", "xmp" })) {
/*  73*/        this.tokeniser.transition(TokeniserState.Rawtext);
/*  74*/      } else if (contextTag.equals("script")) {
/*  75*/        this.tokeniser.transition(TokeniserState.ScriptData);
/*  76*/      } else if (contextTag.equals("noscript")) {
/*  77*/        this.tokeniser.transition(TokeniserState.Data);
/*  78*/      } else if (contextTag.equals("plaintext")) {
/*  79*/        this.tokeniser.transition(TokeniserState.Data);
/*   0*/      } else {
/*  81*/        this.tokeniser.transition(TokeniserState.Data);
/*   0*/      } 
/*  83*/      root = new Element(Tag.valueOf("html"), baseUri);
/*  84*/      this.doc.appendChild(root);
/*  85*/      this.stack.add(root);
/*  86*/      resetInsertionMode();
/*  90*/      Elements contextChain = context.parents();
/*  91*/      contextChain.add(0, context);
/*  92*/      for (Element parent : (Iterable<Element>)contextChain) {
/*  93*/        if (parent instanceof FormElement) {
/*  94*/          this.formElement = (FormElement)parent;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 100*/    runParser();
/* 101*/    if (context != null && root != null) {
/* 102*/        return root.childNodes(); 
/*   0*/       }
/* 104*/    return this.doc.childNodes();
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean process(Token token) {
/* 109*/    this.currentToken = token;
/* 110*/    return this.state.process(token, this);
/*   0*/  }
/*   0*/  
/*   0*/  boolean process(Token token, HtmlTreeBuilderState state) {
/* 114*/    this.currentToken = token;
/* 115*/    return state.process(token, this);
/*   0*/  }
/*   0*/  
/*   0*/  void transition(HtmlTreeBuilderState state) {
/* 119*/    this.state = state;
/*   0*/  }
/*   0*/  
/*   0*/  HtmlTreeBuilderState state() {
/* 123*/    return this.state;
/*   0*/  }
/*   0*/  
/*   0*/  void markInsertionMode() {
/* 127*/    this.originalState = this.state;
/*   0*/  }
/*   0*/  
/*   0*/  HtmlTreeBuilderState originalState() {
/* 131*/    return this.originalState;
/*   0*/  }
/*   0*/  
/*   0*/  void framesetOk(boolean framesetOk) {
/* 135*/    this.framesetOk = framesetOk;
/*   0*/  }
/*   0*/  
/*   0*/  boolean framesetOk() {
/* 139*/    return this.framesetOk;
/*   0*/  }
/*   0*/  
/*   0*/  Document getDocument() {
/* 143*/    return this.doc;
/*   0*/  }
/*   0*/  
/*   0*/  String getBaseUri() {
/* 147*/    return this.baseUri;
/*   0*/  }
/*   0*/  
/*   0*/  void maybeSetBaseUri(Element base) {
/* 151*/    if (this.baseUriSetFromDoc) {
/*   0*/        return; 
/*   0*/       }
/* 154*/    String href = base.absUrl("href");
/* 155*/    if (href.length() != 0) {
/* 156*/      this.baseUri = href;
/* 157*/      this.baseUriSetFromDoc = true;
/* 158*/      this.doc.setBaseUri(href);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  boolean isFragmentParsing() {
/* 163*/    return this.fragmentParsing;
/*   0*/  }
/*   0*/  
/*   0*/  void error(HtmlTreeBuilderState state) {
/* 167*/    if (this.errors.canAddError()) {
/* 168*/        this.errors.add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", new Object[] { this.currentToken.tokenType(), state })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  Element insert(Token.StartTag startTag) {
/* 174*/    if (startTag.isSelfClosing()) {
/* 175*/      Element element = insertEmpty(startTag);
/* 176*/      this.stack.add(element);
/* 177*/      this.tokeniser.transition(TokeniserState.Data);
/* 178*/      this.tokeniser.emit(this.emptyEnd.reset().name(element.tagName()));
/* 179*/      return element;
/*   0*/    } 
/* 182*/    Element el = new Element(Tag.valueOf(startTag.name()), this.baseUri, startTag.attributes);
/* 183*/    insert(el);
/* 184*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  Element insertStartTag(String startTagName) {
/* 188*/    Element el = new Element(Tag.valueOf(startTagName), this.baseUri);
/* 189*/    insert(el);
/* 190*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Element el) {
/* 194*/    insertNode(el);
/* 195*/    this.stack.add(el);
/*   0*/  }
/*   0*/  
/*   0*/  Element insertEmpty(Token.StartTag startTag) {
/* 199*/    Tag tag = Tag.valueOf(startTag.name());
/* 200*/    Element el = new Element(tag, this.baseUri, startTag.attributes);
/* 201*/    insertNode(el);
/* 202*/    if (startTag.isSelfClosing()) {
/* 203*/        if (tag.isKnownTag()) {
/* 204*/          if (tag.isSelfClosing()) {
/* 204*/              this.tokeniser.acknowledgeSelfClosingFlag(); 
/*   0*/             }
/*   0*/        } else {
/* 207*/          tag.setSelfClosing();
/* 208*/          this.tokeniser.acknowledgeSelfClosingFlag();
/*   0*/        }  
/*   0*/       }
/* 211*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  FormElement insertForm(Token.StartTag startTag, boolean onStack) {
/* 215*/    Tag tag = Tag.valueOf(startTag.name());
/* 216*/    FormElement el = new FormElement(tag, this.baseUri, startTag.attributes);
/* 217*/    setFormElement(el);
/* 218*/    insertNode(el);
/* 219*/    if (onStack) {
/* 220*/        this.stack.add(el); 
/*   0*/       }
/* 221*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Comment commentToken) {
/* 225*/    Comment comment = new Comment(commentToken.getData(), this.baseUri);
/* 226*/    insertNode(comment);
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Character characterToken) {
/*   0*/    Node node;
/* 232*/    String tagName = currentElement().tagName();
/* 233*/    if (tagName.equals("script") || tagName.equals("style")) {
/* 234*/      node = new DataNode(characterToken.getData(), this.baseUri);
/*   0*/    } else {
/* 236*/      node = new TextNode(characterToken.getData(), this.baseUri);
/*   0*/    } 
/* 237*/    currentElement().appendChild(node);
/*   0*/  }
/*   0*/  
/*   0*/  private void insertNode(Node node) {
/* 242*/    if (this.stack.size() == 0) {
/* 243*/      this.doc.appendChild(node);
/* 244*/    } else if (isFosterInserts()) {
/* 245*/      insertInFosterParent(node);
/*   0*/    } else {
/* 247*/      currentElement().appendChild(node);
/*   0*/    } 
/* 250*/    if (node instanceof Element && ((Element)node).tag().isFormListed() && 
/* 251*/      this.formElement != null) {
/* 252*/        this.formElement.addElement((Element)node); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  Element pop() {
/* 257*/    int size = this.stack.size();
/* 258*/    return this.stack.remove(size - 1);
/*   0*/  }
/*   0*/  
/*   0*/  void push(Element element) {
/* 262*/    this.stack.add(element);
/*   0*/  }
/*   0*/  
/*   0*/  ArrayList<Element> getStack() {
/* 266*/    return this.stack;
/*   0*/  }
/*   0*/  
/*   0*/  boolean onStack(Element el) {
/* 270*/    return isElementInQueue(this.stack, el);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isElementInQueue(ArrayList<Element> queue, Element element) {
/* 274*/    for (int pos = queue.size() - 1; pos >= 0; pos--) {
/* 275*/      Element next = queue.get(pos);
/* 276*/      if (next == element) {
/* 277*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 280*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  Element getFromStack(String elName) {
/* 284*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 285*/      Element next = this.stack.get(pos);
/* 286*/      if (next.nodeName().equals(elName)) {
/* 287*/          return next; 
/*   0*/         }
/*   0*/    } 
/* 290*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  boolean removeFromStack(Element el) {
/* 294*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 295*/      Element next = this.stack.get(pos);
/* 296*/      if (next == el) {
/* 297*/        this.stack.remove(pos);
/* 298*/        return true;
/*   0*/      } 
/*   0*/    } 
/* 301*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  void popStackToClose(String elName) {
/* 305*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 306*/      Element next = this.stack.get(pos);
/* 307*/      this.stack.remove(pos);
/* 308*/      if (next.nodeName().equals(elName)) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void popStackToClose(String... elNames) {
/* 314*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 315*/      Element next = this.stack.get(pos);
/* 316*/      this.stack.remove(pos);
/* 317*/      if (StringUtil.in(next.nodeName(), elNames)) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void popStackToBefore(String elName) {
/* 323*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 324*/      Element next = this.stack.get(pos);
/* 325*/      if (next.nodeName().equals(elName)) {
/*   0*/          break; 
/*   0*/         }
/* 328*/      this.stack.remove(pos);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void clearStackToTableContext() {
/* 334*/    clearStackToContext(new String[] { "table" });
/*   0*/  }
/*   0*/  
/*   0*/  void clearStackToTableBodyContext() {
/* 338*/    clearStackToContext(new String[] { "tbody", "tfoot", "thead" });
/*   0*/  }
/*   0*/  
/*   0*/  void clearStackToTableRowContext() {
/* 342*/    clearStackToContext(new String[] { "tr" });
/*   0*/  }
/*   0*/  
/*   0*/  private void clearStackToContext(String... nodeNames) {
/* 346*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 347*/      Element next = this.stack.get(pos);
/* 348*/      if (StringUtil.in(next.nodeName(), nodeNames) || next.nodeName().equals("html")) {
/*   0*/          break; 
/*   0*/         }
/* 351*/      this.stack.remove(pos);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  Element aboveOnStack(Element el) {
/* 356*/    assert onStack(el);
/* 357*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 358*/      Element next = this.stack.get(pos);
/* 359*/      if (next == el) {
/* 360*/          return this.stack.get(pos - 1); 
/*   0*/         }
/*   0*/    } 
/* 363*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  void insertOnStackAfter(Element after, Element in) {
/* 367*/    int i = this.stack.lastIndexOf(after);
/* 368*/    Validate.isTrue((i != -1));
/* 369*/    this.stack.add(i + 1, in);
/*   0*/  }
/*   0*/  
/*   0*/  void replaceOnStack(Element out, Element in) {
/* 373*/    replaceInQueue(this.stack, out, in);
/*   0*/  }
/*   0*/  
/*   0*/  private void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
/* 377*/    int i = queue.lastIndexOf(out);
/* 378*/    Validate.isTrue((i != -1));
/* 379*/    queue.set(i, in);
/*   0*/  }
/*   0*/  
/*   0*/  void resetInsertionMode() {
/*   0*/    boolean last = false;
/* 384*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 385*/      Element node = this.stack.get(pos);
/* 386*/      if (pos == 0) {
/* 387*/        last = true;
/* 388*/        node = this.contextElement;
/*   0*/      } 
/* 390*/      String name = node.nodeName();
/* 391*/      if ("select".equals(name)) {
/* 392*/        transition(HtmlTreeBuilderState.InSelect);
/*   0*/        break;
/*   0*/      } 
/* 394*/      if ("td".equals(name) || ("td".equals(name) && !last)) {
/* 395*/        transition(HtmlTreeBuilderState.InCell);
/*   0*/        break;
/*   0*/      } 
/* 397*/      if ("tr".equals(name)) {
/* 398*/        transition(HtmlTreeBuilderState.Initial);
/*   0*/        break;
/*   0*/      } 
/* 400*/      if ("tbody".equals(name) || "thead".equals(name) || "tfoot".equals(name)) {
/* 401*/        transition(HtmlTreeBuilderState.InTableBody);
/*   0*/        break;
/*   0*/      } 
/* 403*/      if ("caption".equals(name)) {
/* 404*/        transition(HtmlTreeBuilderState.InCaption);
/*   0*/        break;
/*   0*/      } 
/* 406*/      if ("colgroup".equals(name)) {
/* 407*/        transition(HtmlTreeBuilderState.InColumnGroup);
/*   0*/        break;
/*   0*/      } 
/* 409*/      if ("table".equals(name)) {
/* 410*/        transition(HtmlTreeBuilderState.InTable);
/*   0*/        break;
/*   0*/      } 
/* 412*/      if ("head".equals(name)) {
/* 413*/        transition(HtmlTreeBuilderState.InBody);
/*   0*/        break;
/*   0*/      } 
/* 415*/      if ("body".equals(name)) {
/* 416*/        transition(HtmlTreeBuilderState.InBody);
/*   0*/        break;
/*   0*/      } 
/* 418*/      if ("frameset".equals(name)) {
/* 419*/        transition(HtmlTreeBuilderState.InFrameset);
/*   0*/        break;
/*   0*/      } 
/* 421*/      if ("html".equals(name)) {
/* 422*/        transition(HtmlTreeBuilderState.BeforeHead);
/*   0*/        break;
/*   0*/      } 
/* 424*/      if (last) {
/* 425*/        transition(HtmlTreeBuilderState.InBody);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  HtmlTreeBuilder() {
/* 432*/    this.specificScopeTarget = new String[] { null };
/*   0*/  }
/*   0*/  
/*   0*/  private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
/* 435*/    this.specificScopeTarget[0] = targetName;
/* 436*/    return inSpecificScope(this.specificScopeTarget, baseTypes, extraTypes);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
/* 440*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 441*/      Element el = this.stack.get(pos);
/* 442*/      String elName = el.nodeName();
/* 443*/      if (StringUtil.in(elName, targetNames)) {
/* 444*/          return true; 
/*   0*/         }
/* 445*/      if (StringUtil.in(elName, baseTypes)) {
/* 446*/          return false; 
/*   0*/         }
/* 447*/      if (extraTypes != null && StringUtil.in(elName, extraTypes)) {
/* 448*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 450*/    Validate.fail("Should not be reachable");
/* 451*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean inScope(String[] targetNames) {
/* 455*/    return inSpecificScope(targetNames, TagsSearchInScope, null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inScope(String targetName) {
/* 459*/    return inScope(targetName, null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inScope(String targetName, String[] extras) {
/* 463*/    return inSpecificScope(targetName, TagsSearchInScope, extras);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inListItemScope(String targetName) {
/* 469*/    return inScope(targetName, TagSearchList);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inButtonScope(String targetName) {
/* 473*/    return inScope(targetName, TagSearchButton);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inTableScope(String targetName) {
/* 477*/    return inSpecificScope(targetName, TagSearchTableScope, null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean inSelectScope(String targetName) {
/* 481*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 482*/      Element el = this.stack.get(pos);
/* 483*/      String elName = el.nodeName();
/* 484*/      if (elName.equals(targetName)) {
/* 485*/          return true; 
/*   0*/         }
/* 486*/      if (!StringUtil.in(elName, TagSearchSelectScope)) {
/* 487*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 489*/    Validate.fail("Should not be reachable");
/* 490*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  void setHeadElement(Element headElement) {
/* 494*/    this.headElement = headElement;
/*   0*/  }
/*   0*/  
/*   0*/  Element getHeadElement() {
/* 498*/    return this.headElement;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isFosterInserts() {
/* 502*/    return this.fosterInserts;
/*   0*/  }
/*   0*/  
/*   0*/  void setFosterInserts(boolean fosterInserts) {
/* 506*/    this.fosterInserts = fosterInserts;
/*   0*/  }
/*   0*/  
/*   0*/  FormElement getFormElement() {
/* 510*/    return this.formElement;
/*   0*/  }
/*   0*/  
/*   0*/  void setFormElement(FormElement formElement) {
/* 514*/    this.formElement = formElement;
/*   0*/  }
/*   0*/  
/*   0*/  void newPendingTableCharacters() {
/* 518*/    this.pendingTableCharacters = new ArrayList<String>();
/*   0*/  }
/*   0*/  
/*   0*/  List<String> getPendingTableCharacters() {
/* 522*/    return this.pendingTableCharacters;
/*   0*/  }
/*   0*/  
/*   0*/  void setPendingTableCharacters(List<String> pendingTableCharacters) {
/* 526*/    this.pendingTableCharacters = pendingTableCharacters;
/*   0*/  }
/*   0*/  
/*   0*/  void generateImpliedEndTags(String excludeTag) {
/* 539*/    while (excludeTag != null && !currentElement().nodeName().equals(excludeTag) && StringUtil.in(currentElement().nodeName(), TagSearchEndTags)) {
/* 541*/        pop(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  void generateImpliedEndTags() {
/* 545*/    generateImpliedEndTags(null);
/*   0*/  }
/*   0*/  
/*   0*/  boolean isSpecial(Element el) {
/* 551*/    String name = el.nodeName();
/* 552*/    return StringUtil.in(name, TagSearchSpecial);
/*   0*/  }
/*   0*/  
/*   0*/  Element lastFormattingElement() {
/* 556*/    return (this.formattingElements.size() > 0) ? this.formattingElements.get(this.formattingElements.size() - 1) : null;
/*   0*/  }
/*   0*/  
/*   0*/  Element removeLastFormattingElement() {
/* 560*/    int size = this.formattingElements.size();
/* 561*/    if (size > 0) {
/* 562*/        return this.formattingElements.remove(size - 1); 
/*   0*/       }
/* 564*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  void pushActiveFormattingElements(Element in) {
/* 569*/    int numSeen = 0;
/* 570*/    for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
/* 571*/      Element el = this.formattingElements.get(pos);
/* 572*/      if (el == null) {
/*   0*/          break; 
/*   0*/         }
/* 575*/      if (isSameFormattingElement(in, el)) {
/* 576*/          numSeen++; 
/*   0*/         }
/* 578*/      if (numSeen == 3) {
/* 579*/        this.formattingElements.remove(pos);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/* 583*/    this.formattingElements.add(in);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isSameFormattingElement(Element a, Element b) {
/* 588*/    return (a.nodeName().equals(b.nodeName()) && a.attributes().equals(b.attributes()));
/*   0*/  }
/*   0*/  
/*   0*/  void reconstructFormattingElements() {
/* 595*/    Element last = lastFormattingElement();
/* 596*/    if (last == null || onStack(last)) {
/*   0*/        return; 
/*   0*/       }
/* 599*/    Element entry = last;
/* 600*/    int size = this.formattingElements.size();
/* 601*/    int pos = size - 1;
/*   0*/    boolean skip = false;
/*   0*/    do {
/* 604*/      if (pos == 0) {
/* 605*/        skip = true;
/*   0*/        break;
/*   0*/      } 
/* 608*/      entry = this.formattingElements.get(--pos);
/* 609*/    } while (entry != null && !onStack(entry));
/*   0*/    do {
/* 613*/      if (!skip) {
/* 614*/          entry = this.formattingElements.get(++pos); 
/*   0*/         }
/* 615*/      Validate.notNull(entry);
/* 618*/      skip = false;
/* 619*/      Element newEl = insertStartTag(entry.nodeName());
/* 621*/      newEl.attributes().addAll(entry.attributes());
/* 624*/      this.formattingElements.set(pos, newEl);
/* 627*/    } while (pos != size - 1);
/*   0*/  }
/*   0*/  
/*   0*/  void clearFormattingElementsToLastMarker() {
/* 633*/    while (!this.formattingElements.isEmpty()) {
/* 634*/      Element el = removeLastFormattingElement();
/* 635*/      if (el == null) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void removeFromActiveFormattingElements(Element el) {
/* 641*/    for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
/* 642*/      Element next = this.formattingElements.get(pos);
/* 643*/      if (next == el) {
/* 644*/        this.formattingElements.remove(pos);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  boolean isInActiveFormattingElements(Element el) {
/* 651*/    return isElementInQueue(this.formattingElements, el);
/*   0*/  }
/*   0*/  
/*   0*/  Element getActiveFormattingElement(String nodeName) {
/* 655*/    for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
/* 656*/      Element next = this.formattingElements.get(pos);
/* 657*/      if (next == null) {
/*   0*/          break; 
/*   0*/         }
/* 659*/      if (next.nodeName().equals(nodeName)) {
/* 660*/          return next; 
/*   0*/         }
/*   0*/    } 
/* 662*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  void replaceActiveFormattingElement(Element out, Element in) {
/* 666*/    replaceInQueue(this.formattingElements, out, in);
/*   0*/  }
/*   0*/  
/*   0*/  void insertMarkerToFormattingElements() {
/* 670*/    this.formattingElements.add(null);
/*   0*/  }
/*   0*/  
/*   0*/  void insertInFosterParent(Node in) {
/*   0*/    Element fosterParent;
/* 675*/    Element lastTable = getFromStack("table");
/*   0*/    boolean isLastTableParent = false;
/* 677*/    if (lastTable != null) {
/* 678*/      if (lastTable.parent() != null) {
/* 679*/        fosterParent = lastTable.parent();
/* 680*/        isLastTableParent = true;
/*   0*/      } else {
/* 682*/        fosterParent = aboveOnStack(lastTable);
/*   0*/      } 
/*   0*/    } else {
/* 684*/      fosterParent = this.stack.get(0);
/*   0*/    } 
/* 687*/    if (isLastTableParent) {
/* 688*/      Validate.notNull(lastTable);
/* 689*/      lastTable.before(in);
/*   0*/    } else {
/* 692*/      fosterParent.appendChild(in);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 697*/    return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + this.state + ", currentElement=" + currentElement() + '}';
/*   0*/  }
/*   0*/}
