/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/import java.util.Iterator;
/*   0*/import java.util.LinkedList;
/*   0*/import org.jsoup.helper.DescendableLinkedList;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.nodes.Attribute;
/*   0*/import org.jsoup.nodes.Attributes;
/*   0*/import org.jsoup.nodes.Document;
/*   0*/import org.jsoup.nodes.DocumentType;
/*   0*/import org.jsoup.nodes.Element;
/*   0*/import org.jsoup.nodes.Node;
/*   0*/
/*   0*/enum TreeBuilderState {
/*  14*/  Initial {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*  16*/      if (isWhitespace(t)) {
/*  17*/          return true; 
/*   0*/         }
/*  18*/      if (t.isComment()) {
/*  19*/        tb.insert(t.asComment());
/*  20*/      } else if (t.isDoctype()) {
/*  23*/        Token.Doctype d = t.asDoctype();
/*  24*/        DocumentType doctype = new DocumentType(d.getName(), d.getPublicIdentifier(), d.getSystemIdentifier(), tb.getBaseUri());
/*  25*/        tb.getDocument().appendChild(doctype);
/*  26*/        if (d.isForceQuirks()) {
/*  27*/            tb.getDocument().quirksMode(Document.QuirksMode.quirks); 
/*   0*/           }
/*  28*/        tb.transition(BeforeHtml);
/*   0*/      } else {
/*  31*/        tb.transition(BeforeHtml);
/*  32*/        return tb.process(t);
/*   0*/      } 
/*  34*/      return true;
/*   0*/    }
/*   0*/  },
/*  37*/  BeforeHtml {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*  39*/      if (t.isDoctype()) {
/*  40*/        tb.error(this);
/*  41*/        return false;
/*   0*/      } 
/*  42*/      if (t.isComment()) {
/*  43*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  44*/        if (isWhitespace(t)) {
/*  45*/            return true; 
/*   0*/           }
/*  46*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*  47*/          tb.insert(t.asStartTag());
/*  48*/          tb.transition(BeforeHead);
/*   0*/        } else {
/*  49*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "head", "body", "html", "br" })) {
/*  50*/              return anythingElse(t, tb); 
/*   0*/             }
/*  51*/          if (t.isEndTag()) {
/*  52*/            tb.error(this);
/*  53*/            return false;
/*   0*/          } 
/*  55*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/*  57*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*  61*/      tb.insert("html");
/*  62*/      tb.transition(BeforeHead);
/*  63*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/*  66*/  BeforeHead {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*  68*/      if (isWhitespace(t)) {
/*  69*/          return true; 
/*   0*/         }
/*  70*/      if (t.isComment()) {
/*  71*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  72*/        if (t.isDoctype()) {
/*  73*/          tb.error(this);
/*  74*/          return false;
/*   0*/        } 
/*  75*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*  76*/            return InBody.process(t, tb); 
/*   0*/           }
/*  77*/        if (t.isStartTag() && t.asStartTag().name().equals("head")) {
/*  78*/          Element head = tb.insert(t.asStartTag());
/*  79*/          tb.setHeadElement(head);
/*  80*/          tb.transition(InHead);
/*   0*/        } else {
/*  81*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "head", "body", "html", "br" })) {
/*  82*/            tb.process(new Token.StartTag("head"));
/*  83*/            return tb.process(t);
/*   0*/          } 
/*  84*/          if (t.isEndTag()) {
/*  85*/            tb.error(this);
/*  86*/            return false;
/*   0*/          } 
/*  88*/          tb.process(new Token.StartTag("head"));
/*  89*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*  91*/      return true;
/*   0*/    }
/*   0*/  },
/*  94*/  InHead {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*  96*/      if (isWhitespace(t)) {
/*  97*/        tb.insert(t.asCharacter());
/*  98*/        return true;
/*   0*/      } 
/* 100*/      switch (t.type) {
/*   0*/        case Comment:
/* 102*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 105*/          tb.error(this);
/* 106*/          return false;
/*   0*/        case StartTag:
/* 108*/          start = t.asStartTag();
/* 109*/          name = start.name();
/* 110*/          if (name.equals("html")) {
/* 111*/              return InBody.process(t, tb); 
/*   0*/             }
/* 112*/          if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "command", "link" })) {
/* 113*/            Element el = tb.insertEmpty(start);
/* 115*/            if (name.equals("base") && el.hasAttr("href")) {
/* 116*/                tb.setBaseUri(el); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 117*/          if (name.equals("meta")) {
/* 118*/            Element element = tb.insertEmpty(start);
/*   0*/            break;
/*   0*/          } 
/* 120*/          if (name.equals("title")) {
/* 121*/            handleRcData(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 122*/          if (StringUtil.in(name, new String[] { "noframes", "style" })) {
/* 123*/            handleRawtext(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 124*/          if (name.equals("noscript")) {
/* 126*/            tb.insert(start);
/* 127*/            tb.transition(InHeadNoscript);
/*   0*/            break;
/*   0*/          } 
/* 128*/          if (name.equals("script")) {
/* 130*/            tb.insert(start);
/* 131*/            tb.tokeniser.transition(TokeniserState.ScriptData);
/* 132*/            tb.markInsertionMode();
/* 133*/            tb.transition(Text);
/*   0*/            break;
/*   0*/          } 
/* 134*/          if (name.equals("head")) {
/* 135*/            tb.error(this);
/* 136*/            return false;
/*   0*/          } 
/* 138*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 142*/          end = t.asEndTag();
/* 143*/          name = end.name();
/* 144*/          if (name.equals("head")) {
/* 145*/            tb.pop();
/* 146*/            tb.transition(AfterHead);
/*   0*/            break;
/*   0*/          } 
/* 147*/          if (StringUtil.in(name, new String[] { "body", "html", "br" })) {
/* 148*/              return anythingElse(t, tb); 
/*   0*/             }
/* 150*/          tb.error(this);
/* 151*/          return false;
/*   0*/        default:
/* 155*/          return anythingElse(t, tb);
/*   0*/      } 
/* 157*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/* 161*/      tb.process(new Token.EndTag("head"));
/* 162*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 165*/  InHeadNoscript {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/* 167*/      if (t.isDoctype()) {
/* 168*/        tb.error(this);
/*   0*/      } else {
/* 169*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/* 170*/            return tb.process(t, InBody); 
/*   0*/           }
/* 171*/        if (t.isEndTag() && t.asEndTag().name().equals("noscript")) {
/* 172*/          tb.pop();
/* 173*/          tb.transition(InHead);
/*   0*/        } else {
/* 174*/          if (isWhitespace(t) || t.isComment() || (t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "basefont", "bgsound", "link", "meta", "noframes", "style" }))) {
/* 176*/              return tb.process(t, InHead); 
/*   0*/             }
/* 177*/          if (t.isEndTag() && t.asEndTag().name().equals("br")) {
/* 178*/              return anythingElse(t, tb); 
/*   0*/             }
/* 179*/          if ((t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "head", "noscript" })) || t.isEndTag()) {
/* 180*/            tb.error(this);
/* 181*/            return false;
/*   0*/          } 
/* 183*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/* 185*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/* 189*/      tb.error(this);
/* 190*/      tb.process(new Token.EndTag("noscript"));
/* 191*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 194*/  AfterHead {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/* 196*/      if (isWhitespace(t)) {
/* 197*/        tb.insert(t.asCharacter());
/* 198*/      } else if (t.isComment()) {
/* 199*/        tb.insert(t.asComment());
/* 200*/      } else if (t.isDoctype()) {
/* 201*/        tb.error(this);
/* 202*/      } else if (t.isStartTag()) {
/* 203*/        Token.StartTag startTag = t.asStartTag();
/* 204*/        String name = startTag.name();
/* 205*/        if (name.equals("html")) {
/* 206*/            return tb.process(t, InBody); 
/*   0*/           }
/* 207*/        if (name.equals("body")) {
/* 208*/          tb.insert(startTag);
/* 209*/          tb.framesetOk(false);
/* 210*/          tb.transition(InBody);
/* 211*/        } else if (name.equals("frameset")) {
/* 212*/          tb.insert(startTag);
/* 213*/          tb.transition(InFrameset);
/* 214*/        } else if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "title" })) {
/* 215*/          tb.error(this);
/* 216*/          Element head = tb.getHeadElement();
/* 217*/          tb.push(head);
/* 218*/          tb.process(t, InHead);
/* 219*/          tb.removeFromStack(head);
/*   0*/        } else {
/* 220*/          if (name.equals("head")) {
/* 221*/            tb.error(this);
/* 222*/            return false;
/*   0*/          } 
/* 224*/          anythingElse(t, tb);
/*   0*/        } 
/* 226*/      } else if (t.isEndTag()) {
/* 227*/        if (StringUtil.in(t.asEndTag().name(), new String[] { "body", "html" })) {
/* 228*/          anythingElse(t, tb);
/*   0*/        } else {
/* 230*/          tb.error(this);
/* 231*/          return false;
/*   0*/        } 
/*   0*/      } else {
/* 234*/        anythingElse(t, tb);
/*   0*/      } 
/* 236*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/* 240*/      tb.process(new Token.StartTag("body"));
/* 241*/      tb.framesetOk(true);
/* 242*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 245*/  InBody {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/* 247*/      switch (t.type) {
/*   0*/        case Character:
/* 249*/          c = t.asCharacter();
/* 250*/          if (c.getData().equals(TreeBuilderState.nullString)) {
/* 252*/            tb.error(this);
/* 253*/            return false;
/*   0*/          } 
/* 254*/          if (isWhitespace(c)) {
/* 255*/            tb.reconstructFormattingElements();
/* 256*/            tb.insert(c);
/*   0*/            break;
/*   0*/          } 
/* 258*/          tb.reconstructFormattingElements();
/* 259*/          tb.insert(c);
/* 260*/          tb.framesetOk(false);
/*   0*/          break;
/*   0*/        case Comment:
/* 265*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 269*/          tb.error(this);
/* 270*/          return false;
/*   0*/        case StartTag:
/* 273*/          startTag = t.asStartTag();
/* 274*/          name = startTag.name();
/* 275*/          if (name.equals("html")) {
/* 276*/            tb.error(this);
/* 278*/            Element html = tb.getStack().getFirst();
/* 279*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 280*/              if (!html.hasAttr(attribute.getKey())) {
/* 281*/                  html.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 283*/          if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title" })) {
/* 284*/              return tb.process(t, InHead); 
/*   0*/             }
/* 285*/          if (name.equals("body")) {
/* 286*/            tb.error(this);
/* 287*/            LinkedList<Element> stack = tb.getStack();
/* 288*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 290*/                return false; 
/*   0*/               }
/* 292*/            tb.framesetOk(false);
/* 293*/            Element body = stack.get(1);
/* 294*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 295*/              if (!body.hasAttr(attribute.getKey())) {
/* 296*/                  body.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 299*/          if (name.equals("frameset")) {
/* 300*/            tb.error(this);
/* 301*/            LinkedList<Element> stack = tb.getStack();
/* 302*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 304*/                return false; 
/*   0*/               }
/* 305*/            if (!tb.framesetOk()) {
/* 306*/                return false; 
/*   0*/               }
/* 308*/            Element second = stack.get(1);
/* 309*/            if (second.parent() != null) {
/* 310*/                second.remove(); 
/*   0*/               }
/* 312*/            while (stack.size() > 1) {
/* 313*/                stack.removeLast(); 
/*   0*/               }
/* 314*/            tb.insert(startTag);
/* 315*/            tb.transition(InFrameset);
/*   0*/            break;
/*   0*/          } 
/* 317*/          if (StringUtil.in(name, new String[] { 
/* 317*/                "address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", 
/* 317*/                "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", 
/* 317*/                "summary", "ul" })) {
/* 321*/            if (tb.inButtonScope("p")) {
/* 322*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 324*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 325*/          if (StringUtil.in(name, new String[] { "h1", "h2", "h3", "h4", "h5", "h6" })) {
/* 326*/            if (tb.inButtonScope("p")) {
/* 327*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 329*/            if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "h1", "h2", "h3", "h4", "h5", "h6" })) {
/* 330*/              tb.error(this);
/* 331*/              tb.pop();
/*   0*/            } 
/* 333*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 334*/          if (StringUtil.in(name, new String[] { "pre", "listing" })) {
/* 335*/            if (tb.inButtonScope("p")) {
/* 336*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 338*/            tb.insert(startTag);
/* 340*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 341*/          if (name.equals("form")) {
/* 342*/            if (tb.getFormElement() != null) {
/* 343*/              tb.error(this);
/* 344*/              return false;
/*   0*/            } 
/* 346*/            if (tb.inButtonScope("p")) {
/* 347*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 349*/            Element form = tb.insert(startTag);
/* 350*/            tb.setFormElement(form);
/*   0*/            break;
/*   0*/          } 
/* 351*/          if (name.equals("li")) {
/* 352*/            tb.framesetOk(false);
/* 353*/            LinkedList<Element> stack = tb.getStack();
/* 354*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 355*/              Element el = stack.get(i);
/* 356*/              if (el.nodeName().equals("li")) {
/* 357*/                tb.process(new Token.EndTag("li"));
/*   0*/                break;
/*   0*/              } 
/* 360*/              if (tb.isSpecial(el) && !StringUtil.in(el.nodeName(), new String[] { "address", "div", "p" })) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 363*/            if (tb.inButtonScope("p")) {
/* 364*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 366*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 367*/          if (StringUtil.in(name, new String[] { "dd", "dt" })) {
/* 368*/            tb.framesetOk(false);
/* 369*/            LinkedList<Element> stack = tb.getStack();
/* 370*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 371*/              Element el = stack.get(i);
/* 372*/              if (StringUtil.in(el.nodeName(), new String[] { "dd", "dt" })) {
/* 373*/                tb.process(new Token.EndTag(el.nodeName()));
/*   0*/                break;
/*   0*/              } 
/* 376*/              if (tb.isSpecial(el) && !StringUtil.in(el.nodeName(), new String[] { "address", "div", "p" })) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 379*/            if (tb.inButtonScope("p")) {
/* 380*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 382*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 383*/          if (name.equals("plaintext")) {
/* 384*/            if (tb.inButtonScope("p")) {
/* 385*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 387*/            tb.insert(startTag);
/* 388*/            tb.tokeniser.transition(TokeniserState.PLAINTEXT);
/*   0*/            break;
/*   0*/          } 
/* 389*/          if (name.equals("button")) {
/* 390*/            if (tb.inButtonScope("button")) {
/* 392*/              tb.error(this);
/* 393*/              tb.process(new Token.EndTag("button"));
/* 394*/              tb.process(startTag);
/*   0*/              break;
/*   0*/            } 
/* 396*/            tb.reconstructFormattingElements();
/* 397*/            tb.insert(startTag);
/* 398*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 400*/          if (name.equals("a")) {
/* 401*/            if (tb.getActiveFormattingElement("a") != null) {
/* 402*/              tb.error(this);
/* 403*/              tb.process(new Token.EndTag("a"));
/* 406*/              Element remainingA = tb.getFromStack("a");
/* 407*/              if (remainingA != null) {
/* 408*/                tb.removeFromActiveFormattingElements(remainingA);
/* 409*/                tb.removeFromStack(remainingA);
/*   0*/              } 
/*   0*/            } 
/* 412*/            tb.reconstructFormattingElements();
/* 413*/            Element a = tb.insert(startTag);
/* 414*/            tb.pushActiveFormattingElements(a);
/*   0*/            break;
/*   0*/          } 
/* 415*/          if (StringUtil.in(name, new String[] { 
/* 415*/                "b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", 
/* 415*/                "tt", "u" })) {
/* 417*/            tb.reconstructFormattingElements();
/* 418*/            Element el = tb.insert(startTag);
/* 419*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 420*/          if (name.equals("nobr")) {
/* 421*/            tb.reconstructFormattingElements();
/* 422*/            if (tb.inScope("nobr")) {
/* 423*/              tb.error(this);
/* 424*/              tb.process(new Token.EndTag("nobr"));
/* 425*/              tb.reconstructFormattingElements();
/*   0*/            } 
/* 427*/            Element el = tb.insert(startTag);
/* 428*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 429*/          if (StringUtil.in(name, new String[] { "applet", "marquee", "object" })) {
/* 430*/            tb.reconstructFormattingElements();
/* 431*/            tb.insert(startTag);
/* 432*/            tb.insertMarkerToFormattingElements();
/* 433*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 434*/          if (name.equals("table")) {
/* 435*/            if (tb.getDocument().quirksMode() != Document.QuirksMode.quirks && tb.inButtonScope("p")) {
/* 436*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 438*/            tb.insert(startTag);
/* 439*/            tb.framesetOk(false);
/* 440*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/* 441*/          if (StringUtil.in(name, new String[] { "area", "br", "embed", "img", "keygen", "wbr" })) {
/* 442*/            tb.reconstructFormattingElements();
/* 443*/            tb.insertEmpty(startTag);
/* 444*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 445*/          if (name.equals("input")) {
/* 446*/            tb.reconstructFormattingElements();
/* 447*/            Element el = tb.insertEmpty(startTag);
/* 448*/            if (!el.attr("type").equalsIgnoreCase("hidden")) {
/* 449*/                tb.framesetOk(false); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 450*/          if (StringUtil.in(name, new String[] { "param", "source", "track" })) {
/* 451*/            tb.insertEmpty(startTag);
/*   0*/            break;
/*   0*/          } 
/* 452*/          if (name.equals("hr")) {
/* 453*/            if (tb.inButtonScope("p")) {
/* 454*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 456*/            tb.insertEmpty(startTag);
/* 457*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 458*/          if (name.equals("image")) {
/* 460*/            startTag.name("img");
/* 461*/            return tb.process(startTag);
/*   0*/          } 
/* 462*/          if (name.equals("isindex")) {
/* 464*/            tb.error(this);
/* 465*/            if (tb.getFormElement() != null) {
/* 466*/                return false; 
/*   0*/               }
/* 468*/            tb.tokeniser.acknowledgeSelfClosingFlag();
/* 469*/            tb.process(new Token.StartTag("form"));
/* 470*/            if (startTag.attributes.hasKey("action")) {
/* 471*/              Element form = tb.getFormElement();
/* 472*/              form.attr("action", startTag.attributes.get("action"));
/*   0*/            } 
/* 474*/            tb.process(new Token.StartTag("hr"));
/* 475*/            tb.process(new Token.StartTag("label"));
/* 477*/            String prompt = startTag.attributes.hasKey("prompt") ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: ";
/* 481*/            tb.process(new Token.Character(prompt));
/* 484*/            Attributes inputAttribs = new Attributes();
/* 485*/            for (Attribute attr : (Iterable<Attribute>)startTag.attributes) {
/* 486*/              if (!StringUtil.in(attr.getKey(), new String[] { "name", "action", "prompt" })) {
/* 487*/                  inputAttribs.put(attr); 
/*   0*/                 }
/*   0*/            } 
/* 489*/            inputAttribs.put("name", "isindex");
/* 490*/            tb.process(new Token.StartTag("input", inputAttribs));
/* 491*/            tb.process(new Token.EndTag("label"));
/* 492*/            tb.process(new Token.StartTag("hr"));
/* 493*/            tb.process(new Token.EndTag("form"));
/*   0*/            break;
/*   0*/          } 
/* 494*/          if (name.equals("textarea")) {
/* 495*/            tb.insert(startTag);
/* 497*/            tb.tokeniser.transition(TokeniserState.Rcdata);
/* 498*/            tb.markInsertionMode();
/* 499*/            tb.framesetOk(false);
/* 500*/            tb.transition(Text);
/*   0*/            break;
/*   0*/          } 
/* 501*/          if (name.equals("xmp")) {
/* 502*/            if (tb.inButtonScope("p")) {
/* 503*/                tb.process(new Token.EndTag("p")); 
/*   0*/               }
/* 505*/            tb.reconstructFormattingElements();
/* 506*/            tb.framesetOk(false);
/* 507*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 508*/          if (name.equals("iframe")) {
/* 509*/            tb.framesetOk(false);
/* 510*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 511*/          if (name.equals("noembed")) {
/* 513*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 514*/          if (name.equals("select")) {
/* 515*/            tb.reconstructFormattingElements();
/* 516*/            tb.insert(startTag);
/* 517*/            tb.framesetOk(false);
/* 519*/            TreeBuilderState state = tb.state();
/* 520*/            if (state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow) || state.equals(InCell)) {
/* 521*/              tb.transition(InSelectInTable);
/*   0*/              break;
/*   0*/            } 
/* 523*/            tb.transition(InSelect);
/*   0*/            break;
/*   0*/          } 
/* 524*/          if (StringUtil.in("optgroup", new String[] { "option" })) {
/* 525*/            if (tb.currentElement().nodeName().equals("option")) {
/* 526*/                tb.process(new Token.EndTag("option")); 
/*   0*/               }
/* 527*/            tb.reconstructFormattingElements();
/* 528*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 529*/          if (StringUtil.in("rp", new String[] { "rt" })) {
/* 530*/            if (tb.inScope("ruby")) {
/* 531*/              tb.generateImpliedEndTags();
/* 532*/              if (!tb.currentElement().nodeName().equals("ruby")) {
/* 533*/                tb.error(this);
/* 534*/                tb.popStackToBefore("ruby");
/*   0*/              } 
/* 536*/              tb.insert(startTag);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 538*/          if (name.equals("math")) {
/* 539*/            tb.reconstructFormattingElements();
/* 541*/            tb.insert(startTag);
/* 542*/            tb.tokeniser.acknowledgeSelfClosingFlag();
/*   0*/            break;
/*   0*/          } 
/* 543*/          if (name.equals("svg")) {
/* 544*/            tb.reconstructFormattingElements();
/* 546*/            tb.insert(startTag);
/* 547*/            tb.tokeniser.acknowledgeSelfClosingFlag();
/*   0*/            break;
/*   0*/          } 
/* 548*/          if (StringUtil.in(name, new String[] { 
/* 548*/                "caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", 
/* 548*/                "tr" })) {
/* 550*/            tb.error(this);
/* 551*/            return false;
/*   0*/          } 
/* 553*/          tb.reconstructFormattingElements();
/* 554*/          tb.insert(startTag);
/*   0*/          break;
/*   0*/        case EndTag:
/* 559*/          endTag = t.asEndTag();
/* 560*/          name = endTag.name();
/* 561*/          if (name.equals("body")) {
/* 562*/            if (!tb.inScope("body")) {
/* 563*/              tb.error(this);
/* 564*/              return false;
/*   0*/            } 
/* 567*/            tb.transition(AfterBody);
/*   0*/            break;
/*   0*/          } 
/* 569*/          if (name.equals("html")) {
/* 570*/            boolean notIgnored = tb.process(new Token.EndTag("body"));
/* 571*/            if (notIgnored) {
/* 572*/                return tb.process(endTag); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 573*/          if (StringUtil.in(name, new String[] { 
/* 573*/                "address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", 
/* 573*/                "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", 
/* 573*/                "pre", "section", "summary", "ul" })) {
/* 578*/            if (!tb.inScope(name)) {
/* 580*/              tb.error(this);
/* 581*/              return false;
/*   0*/            } 
/* 583*/            tb.generateImpliedEndTags();
/* 584*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 585*/                tb.error(this); 
/*   0*/               }
/* 586*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 588*/          if (name.equals("form")) {
/* 589*/            Element currentForm = tb.getFormElement();
/* 590*/            tb.setFormElement(null);
/* 591*/            if (currentForm == null || !tb.inScope(name)) {
/* 592*/              tb.error(this);
/* 593*/              return false;
/*   0*/            } 
/* 595*/            tb.generateImpliedEndTags();
/* 596*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 597*/                tb.error(this); 
/*   0*/               }
/* 599*/            tb.removeFromStack(currentForm);
/*   0*/            break;
/*   0*/          } 
/* 601*/          if (name.equals("p")) {
/* 602*/            if (!tb.inButtonScope(name)) {
/* 603*/              tb.error(this);
/* 604*/              tb.process(new Token.StartTag(name));
/* 605*/              return tb.process(endTag);
/*   0*/            } 
/* 607*/            tb.generateImpliedEndTags(name);
/* 608*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 609*/                tb.error(this); 
/*   0*/               }
/* 610*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 612*/          if (name.equals("li")) {
/* 613*/            if (!tb.inListItemScope(name)) {
/* 614*/              tb.error(this);
/* 615*/              return false;
/*   0*/            } 
/* 617*/            tb.generateImpliedEndTags(name);
/* 618*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 619*/                tb.error(this); 
/*   0*/               }
/* 620*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 622*/          if (StringUtil.in(name, new String[] { "dd", "dt" })) {
/* 623*/            if (!tb.inScope(name)) {
/* 624*/              tb.error(this);
/* 625*/              return false;
/*   0*/            } 
/* 627*/            tb.generateImpliedEndTags(name);
/* 628*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 629*/                tb.error(this); 
/*   0*/               }
/* 630*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 632*/          if (StringUtil.in(name, new String[] { "h1", "h2", "h3", "h4", "h5", "h6" })) {
/* 633*/            if (!tb.inScope(new String[] { "h1", "h2", "h3", "h4", "h5", "h6" })) {
/* 634*/              tb.error(this);
/* 635*/              return false;
/*   0*/            } 
/* 637*/            tb.generateImpliedEndTags(name);
/* 638*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 639*/                tb.error(this); 
/*   0*/               }
/* 640*/            tb.popStackToClose(new String[] { "h1", "h2", "h3", "h4", "h5", "h6" });
/*   0*/            break;
/*   0*/          } 
/* 642*/          if (name.equals("sarcasm")) {
/* 644*/              return anyOtherEndTag(t, tb); 
/*   0*/             }
/* 645*/          if (StringUtil.in(name, new String[] { 
/* 645*/                "a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", 
/* 645*/                "strike", "strong", "tt", "u" })) {
/* 649*/            for (int i = 0; i < 8; i++) {
/* 650*/              Element formatEl = tb.getActiveFormattingElement(name);
/* 651*/              if (formatEl == null) {
/* 652*/                  return anyOtherEndTag(t, tb); 
/*   0*/                 }
/* 653*/              if (!tb.onStack(formatEl)) {
/* 654*/                tb.error(this);
/* 655*/                tb.removeFromActiveFormattingElements(formatEl);
/* 656*/                return true;
/*   0*/              } 
/* 657*/              if (!tb.inScope(formatEl.nodeName())) {
/* 658*/                tb.error(this);
/* 659*/                return false;
/*   0*/              } 
/* 660*/              if (tb.currentElement() != formatEl) {
/* 661*/                  tb.error(this); 
/*   0*/                 }
/* 663*/              Element furthestBlock = null;
/* 664*/              Element commonAncestor = null;
/*   0*/              boolean seenFormattingElement = false;
/* 666*/              LinkedList<Element> stack = tb.getStack();
/* 667*/              for (int si = 0; si < stack.size(); si++) {
/* 668*/                Element el = stack.get(si);
/* 669*/                if (el == formatEl) {
/* 670*/                  commonAncestor = stack.get(si - 1);
/* 671*/                  seenFormattingElement = true;
/* 672*/                } else if (seenFormattingElement && tb.isSpecial(el)) {
/* 673*/                  furthestBlock = el;
/*   0*/                  break;
/*   0*/                } 
/*   0*/              } 
/* 677*/              if (furthestBlock == null) {
/* 678*/                tb.popStackToClose(formatEl.nodeName());
/* 679*/                tb.removeFromActiveFormattingElements(formatEl);
/* 680*/                return true;
/*   0*/              } 
/* 685*/              Element node = furthestBlock;
/* 686*/              Element lastNode = furthestBlock;
/* 688*/              for (int j = 0; j < 3; j++) {
/* 689*/                if (tb.onStack(node)) {
/* 690*/                    node = tb.aboveOnStack(node); 
/*   0*/                   }
/* 691*/                if (!tb.isInActiveFormattingElements(node)) {
/* 692*/                  tb.removeFromStack(node);
/*   0*/                } else {
/* 694*/                  if (node == formatEl) {
/*   0*/                      break; 
/*   0*/                     }
/* 697*/                  Element replacement = new Element(Tag.valueOf(node.nodeName()), tb.getBaseUri());
/* 698*/                  tb.replaceActiveFormattingElement(node, replacement);
/* 699*/                  tb.replaceOnStack(node, replacement);
/* 700*/                  node = replacement;
/* 702*/                  if (lastNode == furthestBlock);
/* 706*/                  if (lastNode.parent() != null) {
/* 707*/                      lastNode.remove(); 
/*   0*/                     }
/* 708*/                  node.appendChild(lastNode);
/* 710*/                  lastNode = node;
/*   0*/                } 
/*   0*/              } 
/* 713*/              if (StringUtil.in(commonAncestor.nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 714*/                if (lastNode.parent() != null) {
/* 715*/                    lastNode.remove(); 
/*   0*/                   }
/* 716*/                tb.insertInFosterParent(lastNode);
/*   0*/              } else {
/* 718*/                if (lastNode.parent() != null) {
/* 719*/                    lastNode.remove(); 
/*   0*/                   }
/* 720*/                commonAncestor.appendChild(lastNode);
/*   0*/              } 
/* 723*/              Element adopter = new Element(Tag.valueOf(name), tb.getBaseUri());
/* 724*/              Node[] childNodes = furthestBlock.childNodes().<Node>toArray(new Node[furthestBlock.childNodes().size()]);
/* 725*/              for (Node childNode : childNodes) {
/* 726*/                  adopter.appendChild(childNode); 
/*   0*/                 }
/* 728*/              furthestBlock.appendChild(adopter);
/* 729*/              tb.removeFromActiveFormattingElements(formatEl);
/* 731*/              tb.removeFromStack(formatEl);
/* 732*/              tb.insertOnStackAfter(furthestBlock, adopter);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 734*/          if (StringUtil.in(name, new String[] { "applet", "marquee", "object" })) {
/* 735*/            if (!tb.inScope("name")) {
/* 736*/              if (!tb.inScope(name)) {
/* 737*/                tb.error(this);
/* 738*/                return false;
/*   0*/              } 
/* 740*/              tb.generateImpliedEndTags();
/* 741*/              if (!tb.currentElement().nodeName().equals(name)) {
/* 742*/                  tb.error(this); 
/*   0*/                 }
/* 743*/              tb.popStackToClose(name);
/* 744*/              tb.clearFormattingElementsToLastMarker();
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 746*/          if (name.equals("br")) {
/* 747*/            tb.error(this);
/* 748*/            tb.process(new Token.StartTag("br"));
/* 749*/            return false;
/*   0*/          } 
/* 751*/          return anyOtherEndTag(t, tb);
/*   0*/      } 
/* 760*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean anyOtherEndTag(Token t, TreeBuilder tb) {
/* 764*/      String name = t.asEndTag().name();
/* 765*/      DescendableLinkedList<Element> stack = tb.getStack();
/* 766*/      Iterator<Element> it = stack.descendingIterator();
/* 767*/      while (it.hasNext()) {
/* 768*/        Element node = it.next();
/* 769*/        if (node.nodeName().equals(name)) {
/* 770*/          tb.generateImpliedEndTags(name);
/* 771*/          if (!name.equals(tb.currentElement().nodeName())) {
/* 772*/              tb.error(this); 
/*   0*/             }
/* 773*/          tb.popStackToClose(name);
/*   0*/          break;
/*   0*/        } 
/* 776*/        if (tb.isSpecial(node)) {
/* 777*/          tb.error(this);
/* 778*/          return false;
/*   0*/        } 
/*   0*/      } 
/* 782*/      return true;
/*   0*/    }
/*   0*/  },
/* 785*/  Text {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/* 788*/      if (t.isCharacter()) {
/* 789*/        tb.insert(t.asCharacter());
/*   0*/      } else {
/* 790*/        if (t.isEOF()) {
/* 791*/          tb.error(this);
/* 793*/          tb.pop();
/* 794*/          tb.transition(tb.originalState());
/* 795*/          return tb.process(t);
/*   0*/        } 
/* 796*/        if (t.isEndTag()) {
/* 798*/          tb.pop();
/* 799*/          tb.transition(tb.originalState());
/*   0*/        } 
/*   0*/      } 
/* 801*/      return true;
/*   0*/    }
/*   0*/  },
/* 804*/  InTable {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/* 806*/      if (t.isCharacter()) {
/* 807*/        tb.newPendingTableCharacters();
/* 808*/        tb.markInsertionMode();
/* 809*/        tb.transition(InTableText);
/* 810*/        return tb.process(t);
/*   0*/      } 
/* 811*/      if (t.isComment()) {
/* 812*/        tb.insert(t.asComment());
/*   0*/      } else {
/* 813*/        if (t.isDoctype()) {
/* 814*/          tb.error(this);
/* 815*/          return false;
/*   0*/        } 
/* 816*/        if (t.isStartTag()) {
/* 817*/          Token.StartTag startTag = t.asStartTag();
/* 818*/          String name = startTag.name();
/* 819*/          if (name.equals("caption")) {
/* 820*/            tb.clearStackToTableContext();
/* 821*/            tb.insertMarkerToFormattingElements();
/* 822*/            tb.insert(startTag);
/* 823*/            tb.transition(InCaption);
/* 824*/          } else if (name.equals("colgroup")) {
/* 825*/            tb.clearStackToTableContext();
/* 826*/            tb.insert(startTag);
/* 827*/            tb.transition(InColumnGroup);
/*   0*/          } else {
/* 828*/            if (name.equals("col")) {
/* 829*/              tb.process(new Token.StartTag("colgroup"));
/* 830*/              return tb.process(t);
/*   0*/            } 
/* 831*/            if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/* 832*/              tb.clearStackToTableContext();
/* 833*/              tb.insert(startTag);
/* 834*/              tb.transition(InTableBody);
/*   0*/            } else {
/* 835*/              if (StringUtil.in(name, new String[] { "td", "th", "tr" })) {
/* 836*/                tb.process(new Token.StartTag("tbody"));
/* 837*/                return tb.process(t);
/*   0*/              } 
/* 838*/              if (name.equals("table")) {
/* 839*/                tb.error(this);
/* 840*/                boolean processed = tb.process(new Token.EndTag("table"));
/* 841*/                if (processed) {
/* 842*/                    return tb.process(t); 
/*   0*/                   }
/*   0*/              } else {
/* 843*/                if (StringUtil.in(name, new String[] { "style", "script" })) {
/* 844*/                    return tb.process(t, InHead); 
/*   0*/                   }
/* 845*/                if (name.equals("input")) {
/* 846*/                  if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
/* 847*/                      return anythingElse(t, tb); 
/*   0*/                     }
/* 849*/                  tb.insertEmpty(startTag);
/* 851*/                } else if (name.equals("form")) {
/* 852*/                  tb.error(this);
/* 853*/                  if (tb.getFormElement() != null) {
/* 854*/                      return false; 
/*   0*/                     }
/* 856*/                  Element form = tb.insertEmpty(startTag);
/* 857*/                  tb.setFormElement(form);
/*   0*/                } else {
/* 860*/                  return anythingElse(t, tb);
/*   0*/                } 
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/* 862*/        } else if (t.isEndTag()) {
/* 863*/          Token.EndTag endTag = t.asEndTag();
/* 864*/          String name = endTag.name();
/* 866*/          if (name.equals("table")) {
/* 867*/            if (!tb.inTableScope(name)) {
/* 868*/              tb.error(this);
/* 869*/              return false;
/*   0*/            } 
/* 871*/            tb.popStackToClose("table");
/* 873*/            tb.resetInsertionMode();
/*   0*/          } else {
/* 874*/            if (StringUtil.in(name, new String[] { 
/* 874*/                  "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", 
/* 874*/                  "tr" })) {
/* 876*/              tb.error(this);
/* 877*/              return false;
/*   0*/            } 
/* 879*/            return anythingElse(t, tb);
/*   0*/          } 
/* 881*/        } else if (t.isEOF()) {
/* 882*/          if (tb.currentElement().nodeName().equals("html")) {
/* 883*/              tb.error(this); 
/*   0*/             }
/* 884*/          return true;
/*   0*/        } 
/*   0*/      } 
/* 886*/      return anythingElse(t, tb);
/*   0*/    }
/*   0*/    
/*   0*/    boolean anythingElse(Token t, TreeBuilder tb) {
/* 890*/      tb.error(this);
/*   0*/      boolean processed = true;
/* 892*/      if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 893*/        tb.setFosterInserts(true);
/* 894*/        processed = tb.process(t, InBody);
/* 895*/        tb.setFosterInserts(false);
/*   0*/      } else {
/* 897*/        processed = tb.process(t, InBody);
/*   0*/      } 
/* 899*/      return processed;
/*   0*/    }
/*   0*/  },
/* 902*/  InTableText {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*   0*/      Token.Character c;
/* 904*/      switch (t.type) {
/*   0*/        case Character:
/* 906*/          c = t.asCharacter();
/* 907*/          if (c.getData().equals(TreeBuilderState.nullString)) {
/* 908*/            tb.error(this);
/* 909*/            return false;
/*   0*/          } 
/* 911*/          tb.getPendingTableCharacters().add(c);
/*   0*/          break;
/*   0*/        default:
/* 915*/          if (tb.getPendingTableCharacters().size() > 0) {
/* 916*/            for (Token.Character character : tb.getPendingTableCharacters()) {
/* 917*/              if (!isWhitespace(character)) {
/* 919*/                tb.error(this);
/* 920*/                if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 921*/                  tb.setFosterInserts(true);
/* 922*/                  tb.process(character, InBody);
/* 923*/                  tb.setFosterInserts(false);
/*   0*/                  continue;
/*   0*/                } 
/* 925*/                tb.process(character, InBody);
/*   0*/                continue;
/*   0*/              } 
/* 928*/              tb.insert(character);
/*   0*/            } 
/* 930*/            tb.newPendingTableCharacters();
/*   0*/          } 
/* 932*/          tb.transition(tb.originalState());
/* 933*/          return tb.process(t);
/*   0*/      } 
/* 935*/      return true;
/*   0*/    }
/*   0*/  },
/* 938*/  InCaption {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/* 940*/      if (t.isEndTag() && t.asEndTag().name().equals("caption")) {
/* 941*/        Token.EndTag endTag = t.asEndTag();
/* 942*/        String name = endTag.name();
/* 943*/        if (!tb.inTableScope(name)) {
/* 944*/          tb.error(this);
/* 945*/          return false;
/*   0*/        } 
/* 947*/        tb.generateImpliedEndTags();
/* 948*/        if (!tb.currentElement().nodeName().equals("caption")) {
/* 949*/            tb.error(this); 
/*   0*/           }
/* 950*/        tb.popStackToClose("caption");
/* 951*/        tb.clearFormattingElementsToLastMarker();
/* 952*/        tb.transition(InTable);
/* 954*/      } else if ((t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) || (t.isEndTag() && t.asEndTag().name().equals("table"))) {
/* 959*/        tb.error(this);
/* 960*/        boolean processed = tb.process(new Token.EndTag("caption"));
/* 961*/        if (processed) {
/* 962*/            return tb.process(t); 
/*   0*/           }
/*   0*/      } else {
/* 963*/        if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/* 965*/          tb.error(this);
/* 966*/          return false;
/*   0*/        } 
/* 968*/        return tb.process(t, InBody);
/*   0*/      } 
/* 970*/      return true;
/*   0*/    }
/*   0*/  },
/* 973*/  InColumnGroup {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/* 975*/      if (isWhitespace(t)) {
/* 976*/        tb.insert(t.asCharacter());
/* 977*/        return true;
/*   0*/      } 
/* 979*/      switch (t.type) {
/*   0*/        case Comment:
/* 981*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 984*/          tb.error(this);
/*   0*/          break;
/*   0*/        case StartTag:
/* 987*/          startTag = t.asStartTag();
/* 988*/          name = startTag.name();
/* 989*/          if (name.equals("html")) {
/* 990*/              return tb.process(t, InBody); 
/*   0*/             }
/* 991*/          if (name.equals("col")) {
/* 992*/            tb.insertEmpty(startTag);
/*   0*/            break;
/*   0*/          } 
/* 994*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 997*/          endTag = t.asEndTag();
/* 998*/          name = endTag.name();
/* 999*/          if (name.equals("colgroup")) {
/*1000*/            if (tb.currentElement().nodeName().equals("html")) {
/*1001*/              tb.error(this);
/*1002*/              return false;
/*   0*/            } 
/*1004*/            tb.pop();
/*1005*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1008*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1011*/          if (tb.currentElement().nodeName().equals("html")) {
/*1012*/              return true; 
/*   0*/             }
/*1014*/          return anythingElse(t, tb);
/*   0*/        default:
/*1016*/          return anythingElse(t, tb);
/*   0*/      } 
/*1018*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1022*/      boolean processed = tb.process(new Token.EndTag("colgroup"));
/*1023*/      if (processed) {
/*1024*/          return tb.process(t); 
/*   0*/         }
/*1025*/      return true;
/*   0*/    }
/*   0*/  },
/*1028*/  InTableBody {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/*1030*/      switch (t.type) {
/*   0*/        case StartTag:
/*1032*/          startTag = t.asStartTag();
/*1033*/          name = startTag.name();
/*1034*/          if (name.equals("tr")) {
/*1035*/            tb.clearStackToTableBodyContext();
/*1036*/            tb.insert(startTag);
/*1037*/            tb.transition(InRow);
/*   0*/            break;
/*   0*/          } 
/*1038*/          if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1039*/            tb.error(this);
/*1040*/            tb.process(new Token.StartTag("tr"));
/*1041*/            return tb.process(startTag);
/*   0*/          } 
/*1042*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead" })) {
/*1043*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1045*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1048*/          endTag = t.asEndTag();
/*1049*/          name = endTag.name();
/*1050*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1051*/            if (!tb.inTableScope(name)) {
/*1052*/              tb.error(this);
/*1053*/              return false;
/*   0*/            } 
/*1055*/            tb.clearStackToTableBodyContext();
/*1056*/            tb.pop();
/*1057*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1059*/          if (name.equals("table")) {
/*1060*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1061*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th", "tr" })) {
/*1062*/            tb.error(this);
/*1063*/            return false;
/*   0*/          } 
/*1065*/          return anythingElse(t, tb);
/*   0*/        default:
/*1068*/          return anythingElse(t, tb);
/*   0*/      } 
/*1070*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean exitTableBody(Token t, TreeBuilder tb) {
/*1074*/      if (!tb.inTableScope("tbody") && !tb.inTableScope("thead") && !tb.inScope("tfoot")) {
/*1076*/        tb.error(this);
/*1077*/        return false;
/*   0*/      } 
/*1079*/      tb.clearStackToTableBodyContext();
/*1080*/      tb.process(new Token.EndTag(tb.currentElement().nodeName()));
/*1081*/      return tb.process(t);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1085*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/  },
/*1088*/  InRow {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1090*/      if (t.isStartTag()) {
/*1091*/        Token.StartTag startTag = t.asStartTag();
/*1092*/        String name = startTag.name();
/*1094*/        if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1095*/          tb.clearStackToTableRowContext();
/*1096*/          tb.insert(startTag);
/*1097*/          tb.transition(InCell);
/*1098*/          tb.insertMarkerToFormattingElements();
/*   0*/        } else {
/*1099*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr" })) {
/*1100*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1102*/          return anythingElse(t, tb);
/*   0*/        } 
/*1104*/      } else if (t.isEndTag()) {
/*1105*/        Token.EndTag endTag = t.asEndTag();
/*1106*/        String name = endTag.name();
/*1108*/        if (name.equals("tr")) {
/*1109*/          if (!tb.inTableScope(name)) {
/*1110*/            tb.error(this);
/*1111*/            return false;
/*   0*/          } 
/*1113*/          tb.clearStackToTableRowContext();
/*1114*/          tb.pop();
/*1115*/          tb.transition(InTableBody);
/*   0*/        } else {
/*1116*/          if (name.equals("table")) {
/*1117*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1118*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1119*/            if (!tb.inTableScope(name)) {
/*1120*/              tb.error(this);
/*1121*/              return false;
/*   0*/            } 
/*1123*/            tb.process(new Token.EndTag("tr"));
/*1124*/            return tb.process(t);
/*   0*/          } 
/*1125*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th" })) {
/*1126*/            tb.error(this);
/*1127*/            return false;
/*   0*/          } 
/*1129*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1132*/        return anythingElse(t, tb);
/*   0*/      } 
/*1134*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1138*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean handleMissingTr(Token t, TreeBuilder tb) {
/*1142*/      boolean processed = tb.process(new Token.EndTag("tr"));
/*1143*/      if (processed) {
/*1144*/          return tb.process(t); 
/*   0*/         }
/*1146*/      return false;
/*   0*/    }
/*   0*/  },
/*1149*/  InCell {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1151*/      if (t.isEndTag()) {
/*1152*/        Token.EndTag endTag = t.asEndTag();
/*1153*/        String name = endTag.name();
/*1155*/        if (StringUtil.in(name, new String[] { "td", "th" })) {
/*1156*/          if (!tb.inTableScope(name)) {
/*1157*/            tb.error(this);
/*1158*/            tb.transition(InRow);
/*1159*/            return false;
/*   0*/          } 
/*1161*/          tb.generateImpliedEndTags();
/*1162*/          if (!tb.currentElement().nodeName().equals(name)) {
/*1163*/              tb.error(this); 
/*   0*/             }
/*1164*/          tb.popStackToClose(name);
/*1165*/          tb.clearFormattingElementsToLastMarker();
/*1166*/          tb.transition(InRow);
/*   0*/        } else {
/*1167*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html" })) {
/*1168*/            tb.error(this);
/*1169*/            return false;
/*   0*/          } 
/*1170*/          if (StringUtil.in(name, new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/*1171*/            if (!tb.inTableScope(name)) {
/*1172*/              tb.error(this);
/*1173*/              return false;
/*   0*/            } 
/*1175*/            closeCell(tb);
/*1176*/            return tb.process(t);
/*   0*/          } 
/*1178*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1180*/        if (t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/*1183*/          if (!tb.inTableScope("td") && !tb.inTableScope("th")) {
/*1184*/            tb.error(this);
/*1185*/            return false;
/*   0*/          } 
/*1187*/          closeCell(tb);
/*1188*/          return tb.process(t);
/*   0*/        } 
/*1190*/        return anythingElse(t, tb);
/*   0*/      } 
/*1192*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1196*/      return tb.process(t, InBody);
/*   0*/    }
/*   0*/    
/*   0*/    private void closeCell(TreeBuilder tb) {
/*1200*/      if (tb.inTableScope("td")) {
/*1201*/        tb.process(new Token.EndTag("td"));
/*   0*/      } else {
/*1203*/        tb.process(new Token.EndTag("th"));
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1206*/  InSelect {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*1208*/      switch (t.type) {
/*   0*/        case Character:
/*1210*/          c = t.asCharacter();
/*1211*/          if (c.getData().equals(TreeBuilderState.nullString)) {
/*1212*/            tb.error(this);
/*1213*/            return false;
/*   0*/          } 
/*1215*/          tb.insert(c);
/*   0*/          break;
/*   0*/        case Comment:
/*1219*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/*1222*/          tb.error(this);
/*1223*/          return false;
/*   0*/        case StartTag:
/*1225*/          start = t.asStartTag();
/*1226*/          name = start.name();
/*1227*/          if (name.equals("html")) {
/*1228*/              return tb.process(start, InBody); 
/*   0*/             }
/*1229*/          if (name.equals("option")) {
/*1230*/            tb.process(new Token.EndTag("option"));
/*1231*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1232*/          if (name.equals("optgroup")) {
/*1233*/            if (tb.currentElement().nodeName().equals("option")) {
/*1234*/              tb.process(new Token.EndTag("option"));
/*1235*/            } else if (tb.currentElement().nodeName().equals("optgroup")) {
/*1236*/              tb.process(new Token.EndTag("optgroup"));
/*   0*/            } 
/*1237*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1238*/          if (name.equals("select")) {
/*1239*/            tb.error(this);
/*1240*/            return tb.process(new Token.EndTag("select"));
/*   0*/          } 
/*1241*/          if (StringUtil.in(name, new String[] { "input", "keygen", "textarea" })) {
/*1242*/            tb.error(this);
/*1243*/            if (!tb.inSelectScope("select")) {
/*1244*/                return false; 
/*   0*/               }
/*1245*/            tb.process(new Token.EndTag("select"));
/*1246*/            return tb.process(start);
/*   0*/          } 
/*1247*/          if (name.equals("script")) {
/*1248*/              return tb.process(t, InHead); 
/*   0*/             }
/*1250*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1254*/          end = t.asEndTag();
/*1255*/          name = end.name();
/*1256*/          if (name.equals("optgroup")) {
/*1257*/            if (tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
/*1258*/                tb.process(new Token.EndTag("option")); 
/*   0*/               }
/*1259*/            if (tb.currentElement().nodeName().equals("optgroup")) {
/*1260*/              tb.pop();
/*   0*/              break;
/*   0*/            } 
/*1262*/            tb.error(this);
/*   0*/            break;
/*   0*/          } 
/*1263*/          if (name.equals("option")) {
/*1264*/            if (tb.currentElement().nodeName().equals("option")) {
/*1265*/              tb.pop();
/*   0*/              break;
/*   0*/            } 
/*1267*/            tb.error(this);
/*   0*/            break;
/*   0*/          } 
/*1268*/          if (name.equals("select")) {
/*1269*/            if (!tb.inSelectScope(name)) {
/*1270*/              tb.error(this);
/*1271*/              return false;
/*   0*/            } 
/*1273*/            tb.popStackToClose(name);
/*1274*/            tb.resetInsertionMode();
/*   0*/            break;
/*   0*/          } 
/*1277*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1280*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1281*/              tb.error(this); 
/*   0*/             }
/*   0*/          break;
/*   0*/        default:
/*1284*/          return anythingElse(t, tb);
/*   0*/      } 
/*1286*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1290*/      tb.error(this);
/*1291*/      return false;
/*   0*/    }
/*   0*/  },
/*1294*/  InSelectInTable {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1296*/      if (t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1297*/        tb.error(this);
/*1298*/        tb.process(new Token.EndTag("select"));
/*1299*/        return tb.process(t);
/*   0*/      } 
/*1300*/      if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1301*/        tb.error(this);
/*1302*/        if (tb.inTableScope(t.asEndTag().name())) {
/*1303*/          tb.process(new Token.EndTag("select"));
/*1304*/          return tb.process(t);
/*   0*/        } 
/*1306*/        return false;
/*   0*/      } 
/*1308*/      return tb.process(t, InSelect);
/*   0*/    }
/*   0*/  },
/*1312*/  AfterBody {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1314*/      if (isWhitespace(t)) {
/*1315*/          return tb.process(t, InBody); 
/*   0*/         }
/*1316*/      if (t.isComment()) {
/*1317*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1318*/        if (t.isDoctype()) {
/*1319*/          tb.error(this);
/*1320*/          return false;
/*   0*/        } 
/*1321*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*1322*/            return tb.process(t, InBody); 
/*   0*/           }
/*1323*/        if (t.isEndTag() && t.asEndTag().name().equals("html")) {
/*1324*/          if (tb.isFragmentParsing()) {
/*1325*/            tb.error(this);
/*1326*/            return false;
/*   0*/          } 
/*1328*/          tb.transition(AfterAfterBody);
/*1330*/        } else if (!t.isEOF()) {
/*1333*/          tb.error(this);
/*1334*/          tb.transition(InBody);
/*1335*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1337*/      return true;
/*   0*/    }
/*   0*/  },
/*1340*/  InFrameset {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1342*/      if (isWhitespace(t)) {
/*1343*/        tb.insert(t.asCharacter());
/*1344*/      } else if (t.isComment()) {
/*1345*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1346*/        if (t.isDoctype()) {
/*1347*/          tb.error(this);
/*1348*/          return false;
/*   0*/        } 
/*1349*/        if (t.isStartTag()) {
/*1350*/          Token.StartTag start = t.asStartTag();
/*1351*/          String name = start.name();
/*1352*/          if (name.equals("html")) {
/*1353*/              return tb.process(start, InBody); 
/*   0*/             }
/*1354*/          if (name.equals("frameset")) {
/*1355*/            tb.insert(start);
/*1356*/          } else if (name.equals("frame")) {
/*1357*/            tb.insertEmpty(start);
/*   0*/          } else {
/*1358*/            if (name.equals("noframes")) {
/*1359*/                return tb.process(start, InHead); 
/*   0*/               }
/*1361*/            tb.error(this);
/*1362*/            return false;
/*   0*/          } 
/*1364*/        } else if (t.isEndTag() && t.asEndTag().name().equals("frameset")) {
/*1365*/          if (tb.currentElement().nodeName().equals("html")) {
/*1366*/            tb.error(this);
/*1367*/            return false;
/*   0*/          } 
/*1369*/          tb.pop();
/*1370*/          if (!tb.isFragmentParsing() && !tb.currentElement().nodeName().equals("frameset")) {
/*1371*/              tb.transition(AfterFrameset); 
/*   0*/             }
/*1374*/        } else if (t.isEOF()) {
/*1375*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1376*/            tb.error(this);
/*1377*/            return true;
/*   0*/          } 
/*   0*/        } else {
/*1380*/          tb.error(this);
/*1381*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1383*/      return true;
/*   0*/    }
/*   0*/  },
/*1386*/  AfterFrameset {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1388*/      if (isWhitespace(t)) {
/*1389*/        tb.insert(t.asCharacter());
/*1390*/      } else if (t.isComment()) {
/*1391*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1392*/        if (t.isDoctype()) {
/*1393*/          tb.error(this);
/*1394*/          return false;
/*   0*/        } 
/*1395*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*1396*/            return tb.process(t, InBody); 
/*   0*/           }
/*1397*/        if (t.isEndTag() && t.asEndTag().name().equals("html")) {
/*1398*/          tb.transition(AfterAfterFrameset);
/*   0*/        } else {
/*1399*/          if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
/*1400*/              return tb.process(t, InHead); 
/*   0*/             }
/*1401*/          if (!t.isEOF()) {
/*1404*/            tb.error(this);
/*1405*/            return false;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1407*/      return true;
/*   0*/    }
/*   0*/  },
/*1410*/  AfterAfterBody {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1412*/      if (t.isComment()) {
/*1413*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1414*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
/*1415*/            return tb.process(t, InBody); 
/*   0*/           }
/*1416*/        if (!t.isEOF()) {
/*1419*/          tb.error(this);
/*1420*/          tb.transition(InBody);
/*1421*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1423*/      return true;
/*   0*/    }
/*   0*/  },
/*1426*/  AfterAfterFrameset {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1428*/      if (t.isComment()) {
/*1429*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1430*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
/*1431*/            return tb.process(t, InBody); 
/*   0*/           }
/*1432*/        if (!t.isEOF()) {
/*1434*/          if (t.isStartTag() && t.asStartTag().name().equals("nofrmes")) {
/*1435*/              return tb.process(t, InHead); 
/*   0*/             }
/*1437*/          tb.error(this);
/*1438*/          tb.transition(InBody);
/*1439*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1441*/      return true;
/*   0*/    }
/*   0*/  },
/*1444*/  ForeignContent {
/*   0*/    boolean process(Token t, TreeBuilder tb) {
/*1446*/      return true;
/*   0*/    }
/*   0*/  };
/*   0*/  
/*1451*/  private static String nullString = String.valueOf(0);
/*   0*/  
/*   0*/  private static boolean isWhitespace(Token t) {
/*1456*/    if (t.isCharacter()) {
/*1457*/      String data = t.asCharacter().getData();
/*1459*/      for (int i = 0; i < data.length(); i++) {
/*1460*/        char c = data.charAt(i);
/*1461*/        if (!Character.isWhitespace(c)) {
/*1462*/            return false; 
/*   0*/           }
/*   0*/      } 
/*1464*/      return true;
/*   0*/    } 
/*1466*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRcData(Token.StartTag startTag, TreeBuilder tb) {
/*1470*/    tb.insert(startTag);
/*1471*/    tb.tokeniser.transition(TokeniserState.Rcdata);
/*1472*/    tb.markInsertionMode();
/*1473*/    tb.transition(Text);
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRawtext(Token.StartTag startTag, TreeBuilder tb) {
/*1477*/    tb.insert(startTag);
/*1478*/    tb.tokeniser.transition(TokeniserState.Rawtext);
/*1479*/    tb.markInsertionMode();
/*1480*/    tb.transition(Text);
/*   0*/  }
/*   0*/  
/*   0*/  abstract boolean process(Token paramToken, TreeBuilder paramTreeBuilder);
/*   0*/}
