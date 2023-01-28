/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/import java.io.Reader;
/*   0*/import java.io.StringReader;
/*   0*/import java.util.List;
/*   0*/import org.jsoup.Jsoup;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.nodes.Attributes;
/*   0*/import org.jsoup.nodes.CDataNode;
/*   0*/import org.jsoup.nodes.Comment;
/*   0*/import org.jsoup.nodes.Document;
/*   0*/import org.jsoup.nodes.DocumentType;
/*   0*/import org.jsoup.nodes.Element;
/*   0*/import org.jsoup.nodes.Node;
/*   0*/import org.jsoup.nodes.TextNode;
/*   0*/import org.jsoup.nodes.XmlDeclaration;
/*   0*/
/*   0*/public class XmlTreeBuilder extends TreeBuilder {
/*   0*/  ParseSettings defaultSettings() {
/*  20*/    return ParseSettings.preserveCase;
/*   0*/  }
/*   0*/  
/*   0*/  Document parse(Reader input, String baseUri) {
/*  24*/    return parse(input, baseUri, ParseErrorList.noTracking(), ParseSettings.preserveCase);
/*   0*/  }
/*   0*/  
/*   0*/  Document parse(String input, String baseUri) {
/*  28*/    return parse(new StringReader(input), baseUri, ParseErrorList.noTracking(), ParseSettings.preserveCase);
/*   0*/  }
/*   0*/  
/*   0*/  protected void initialiseParse(Reader input, String baseUri, ParseErrorList errors, ParseSettings settings) {
/*  33*/    super.initialiseParse(input, baseUri, errors, settings);
/*  34*/    this.stack.add(this.doc);
/*  35*/    this.doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean process(Token token) {
/*  41*/    switch (token.type) {
/*   0*/      case StartTag:
/*  43*/        insert(token.asStartTag());
/*   0*/        break;
/*   0*/      case EndTag:
/*  46*/        popStackToClose(token.asEndTag());
/*   0*/        break;
/*   0*/      case Comment:
/*  49*/        insert(token.asComment());
/*   0*/        break;
/*   0*/      case Character:
/*  52*/        insert(token.asCharacter());
/*   0*/        break;
/*   0*/      case Doctype:
/*  55*/        insert(token.asDoctype());
/*   0*/        break;
/*   0*/      case EOF:
/*   0*/        break;
/*   0*/      default:
/*  60*/        Validate.fail("Unexpected token type: " + token.type);
/*   0*/        break;
/*   0*/    } 
/*  62*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void insertNode(Node node) {
/*  66*/    currentElement().appendChild(node);
/*   0*/  }
/*   0*/  
/*   0*/  Element insert(Token.StartTag startTag) {
/*  70*/    Tag tag = Tag.valueOf(startTag.name(), this.settings);
/*  72*/    Element el = new Element(tag, this.baseUri, this.settings.normalizeAttributes(startTag.attributes));
/*  73*/    insertNode(el);
/*  74*/    if (startTag.isSelfClosing()) {
/*  75*/      if (!tag.isKnownTag()) {
/*  76*/          tag.setSelfClosing(); 
/*   0*/         }
/*   0*/    } else {
/*  78*/      this.stack.add(el);
/*   0*/    } 
/*  80*/    return el;
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Comment commentToken) {
/*  84*/    Comment comment = new Comment(commentToken.getData());
/*  85*/    Node insert = comment;
/*  86*/    if (commentToken.bogus) {
/*  88*/      String data = comment.getData();
/*  89*/      if (data.length() > 1 && (data.startsWith("!") || data.startsWith("?"))) {
/*  90*/        Document doc = Jsoup.parse("<" + data.substring(1, data.length() - 1) + ">", this.baseUri, Parser.xmlParser());
/*  91*/        Element el = doc.normalise().child(0);
/*  92*/        insert = new XmlDeclaration(this.settings.normalizeTag(el.tagName()), data.startsWith("!"));
/*  93*/        insert.attributes().addAll(el.attributes());
/*   0*/      } 
/*   0*/    } 
/*  96*/    insertNode(insert);
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Character token) {
/* 100*/    String data = token.getData();
/* 101*/    insertNode(token.isCData() ? new CDataNode(data) : new TextNode(data));
/*   0*/  }
/*   0*/  
/*   0*/  void insert(Token.Doctype d) {
/* 105*/    DocumentType doctypeNode = new DocumentType(this.settings.normalizeTag(d.getName()), d.getPublicIdentifier(), d.getSystemIdentifier());
/* 106*/    doctypeNode.setPubSysKey(d.getPubSysKey());
/* 107*/    insertNode(doctypeNode);
/*   0*/  }
/*   0*/  
/*   0*/  private void popStackToClose(Token.EndTag endTag) {
/* 117*/    String elName = this.settings.normalizeTag(endTag.tagName);
/* 118*/    Element firstFound = null;
/* 120*/    for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
/* 121*/      Element next = this.stack.get(pos);
/* 122*/      if (next.nodeName().equals(elName)) {
/* 123*/        firstFound = next;
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/* 127*/    if (firstFound == null) {
/*   0*/        return; 
/*   0*/       }
/* 130*/    for (int i = this.stack.size() - 1; i >= 0; i--) {
/* 131*/      Element next = this.stack.get(i);
/* 132*/      this.stack.remove(i);
/* 133*/      if (next == firstFound) {
/*   0*/          break; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  List<Node> parseFragment(String inputFragment, String baseUri, ParseErrorList errors, ParseSettings settings) {
/* 139*/    initialiseParse(new StringReader(inputFragment), baseUri, errors, settings);
/* 140*/    runParser();
/* 141*/    return this.doc.childNodes();
/*   0*/  }
/*   0*/}
