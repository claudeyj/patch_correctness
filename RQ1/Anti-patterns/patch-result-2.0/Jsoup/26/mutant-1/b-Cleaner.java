/*   0*/package org.jsoup.safety;
/*   0*/
/*   0*/import java.util.List;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/import org.jsoup.nodes.Attribute;
/*   0*/import org.jsoup.nodes.Attributes;
/*   0*/import org.jsoup.nodes.Document;
/*   0*/import org.jsoup.nodes.Element;
/*   0*/import org.jsoup.nodes.Node;
/*   0*/import org.jsoup.nodes.TextNode;
/*   0*/import org.jsoup.parser.Tag;
/*   0*/
/*   0*/public class Cleaner {
/*   0*/  private Whitelist whitelist;
/*   0*/  
/*   0*/  public Cleaner(Whitelist whitelist) {
/*  29*/    Validate.notNull(whitelist);
/*  30*/    this.whitelist = whitelist;
/*   0*/  }
/*   0*/  
/*   0*/  public Document clean(Document dirtyDocument) {
/*  40*/    Validate.notNull(dirtyDocument);
/*  42*/    Document clean = Document.createShell(dirtyDocument.baseUri());
/*  43*/    copySafeNodes(dirtyDocument.normalise().body(), clean.body());
/*  45*/    return clean;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isValid(Document dirtyDocument) {
/*  59*/    Validate.notNull(dirtyDocument);
/*  61*/    Document clean = Document.createShell(dirtyDocument.baseUri());
/*  62*/    int numDiscarded = copySafeNodes(dirtyDocument.body(), clean.body());
/*  63*/    return (numDiscarded == 0);
/*   0*/  }
/*   0*/  
/*   0*/  private int copySafeNodes(Element source, Element dest) {
/*  73*/    List<Node> sourceChildren = source.childNodes();
/*  74*/    int numDiscarded = 0;
/*  76*/    for (Node sourceChild : sourceChildren) {
/*  77*/      if (sourceChild instanceof Element) {
/*  78*/        Element sourceEl = (Element)sourceChild;
/*  80*/        if (this.whitelist.isSafeTag(sourceEl.tagName())) {
/*  81*/          ElementMeta meta = createSafeElement(sourceEl);
/*  82*/          Element destChild = meta.el;
/*  83*/          dest.appendChild(destChild);
/*  85*/          numDiscarded += meta.numAttribsDiscarded;
/*  86*/          numDiscarded += copySafeNodes(sourceEl, destChild);
/*   0*/          continue;
/*   0*/        } 
/*  88*/        numDiscarded++;
/*  89*/        numDiscarded += copySafeNodes(sourceEl, dest);
/*   0*/        continue;
/*   0*/      } 
/*  91*/      if (sourceChild instanceof TextNode) {
/*  92*/        TextNode sourceText = (TextNode)sourceChild;
/*  93*/        TextNode destText = new TextNode(sourceText.getWholeText(), sourceChild.baseUri());
/*  94*/        dest.appendChild(destText);
/*   0*/      } 
/*   0*/    } 
/*  97*/    return numDiscarded;
/*   0*/  }
/*   0*/  
/*   0*/  private ElementMeta createSafeElement(Element sourceEl) {
/* 101*/    String sourceTag = sourceEl.tagName();
/* 102*/    Attributes destAttrs = new Attributes();
/* 103*/    Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
/* 104*/    int numDiscarded = 0;
/* 106*/    Attributes sourceAttrs = sourceEl.attributes();
/* 107*/    for (Attribute sourceAttr : (Iterable<Attribute>)sourceAttrs) {
/* 108*/      if (this.whitelist.isSafeAttribute(sourceTag, sourceEl, sourceAttr)) {
/* 109*/        destAttrs.put(sourceAttr);
/*   0*/        continue;
/*   0*/      } 
/* 111*/      numDiscarded++;
/*   0*/    } 
/* 113*/    Attributes enforcedAttrs = this.whitelist.getEnforcedAttributes(sourceTag);
/* 114*/    destAttrs.addAll(enforcedAttrs);
/* 116*/    return new ElementMeta(dest, numDiscarded);
/*   0*/  }
/*   0*/  
/*   0*/  private static class ElementMeta {
/*   0*/    Element el;
/*   0*/    
/*   0*/    int numAttribsDiscarded;
/*   0*/    
/*   0*/    ElementMeta(Element el, int numAttribsDiscarded) {
/* 124*/      this.el = el;
/* 125*/      this.numAttribsDiscarded = numAttribsDiscarded;
/*   0*/    }
/*   0*/  }
/*   0*/}
