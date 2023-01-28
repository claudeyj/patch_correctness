/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.lang.Validate;
/*   0*/import org.jsoup.parser.Tag;
/*   0*/
/*   0*/public class Document extends Element {
/*   0*/  public Document(String baseUri) {
/*  22*/    super(Tag.valueOf("#root"), baseUri);
/*   0*/  }
/*   0*/  
/*   0*/  public static Document createShell(String baseUri) {
/*  31*/    Validate.notNull(baseUri);
/*  33*/    Document doc = new Document(baseUri);
/*  34*/    Element html = doc.appendElement("html");
/*  35*/    html.appendElement("head");
/*  36*/    html.appendElement("body");
/*  38*/    return doc;
/*   0*/  }
/*   0*/  
/*   0*/  public Element head() {
/*  46*/    return getElementsByTag("head").first();
/*   0*/  }
/*   0*/  
/*   0*/  public Element body() {
/*  54*/    return getElementsByTag("body").first();
/*   0*/  }
/*   0*/  
/*   0*/  public String title() {
/*  62*/    Element titleEl = getElementsByTag("title").first();
/*  63*/    return (titleEl != null) ? titleEl.text().trim() : "";
/*   0*/  }
/*   0*/  
/*   0*/  public void title(String title) {
/*  72*/    Validate.notNull(title);
/*  73*/    Element titleEl = getElementsByTag("title").first();
/*  74*/    if (titleEl == null) {
/*  75*/      head().appendElement("title").text(title);
/*   0*/    } else {
/*  77*/      titleEl.text(title);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Element createElement(String tagName) {
/*  87*/    return new Element(Tag.valueOf(tagName), baseUri());
/*   0*/  }
/*   0*/  
/*   0*/  public Document normalise() {
/*  96*/    if (select("html").isEmpty()) {
/*  97*/        appendElement("html"); 
/*   0*/       }
/*  98*/    if (head() == null) {
/*  99*/        select("html").first().prependElement("head"); 
/*   0*/       }
/* 100*/    if (body() == null) {
/* 101*/        select("html").first().appendElement("body"); 
/*   0*/       }
/* 105*/    normalise(head());
/* 106*/    normalise(select("html").first());
/* 107*/    normalise(this);
/* 109*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  private void normalise(Element element) {
/* 114*/    List<Node> toMove = new ArrayList<Node>();
/* 115*/    for (Node node : element.childNodes) {
/* 116*/      if (node.parent() instanceof TextNode) {
/* 117*/        TextNode tn = (TextNode)node;
/* 118*/        if (!tn.isBlank()) {
/* 119*/            toMove.add(tn); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 123*/    for (Node node : toMove) {
/* 124*/      element.removeChild(node);
/* 125*/      body().appendChild(new TextNode(" ", ""));
/* 126*/      body().appendChild(node);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String outerHtml() {
/* 132*/    return html();
/*   0*/  }
/*   0*/  
/*   0*/  public Element text(String text) {
/* 142*/    body().text(text);
/* 143*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String nodeName() {
/* 148*/    return "#document";
/*   0*/  }
/*   0*/}
