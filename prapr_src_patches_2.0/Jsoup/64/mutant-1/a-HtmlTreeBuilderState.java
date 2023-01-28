/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.nodes.Attribute;
/*   0*/import org.jsoup.nodes.Attributes;
/*   0*/import org.jsoup.nodes.Document;
/*   0*/import org.jsoup.nodes.DocumentType;
/*   0*/import org.jsoup.nodes.Element;
/*   0*/import org.jsoup.nodes.Node;
/*   0*/
/*   0*/enum HtmlTreeBuilderState {
/*  12*/  Initial {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  14*/      if (isWhitespace(t)) {
/*  15*/          return true; 
/*   0*/         }
/*  16*/      if (t.isComment()) {
/*  17*/        tb.insert(t.asComment());
/*  18*/      } else if (t.isDoctype()) {
/*  21*/        Token.Doctype d = t.asDoctype();
/*  22*/        DocumentType doctype = new DocumentType(tb.settings.normalizeTag(d.getName()), d.getPubSysKey(), d.getPublicIdentifier(), d.getSystemIdentifier(), tb.getBaseUri());
/*  24*/        tb.getDocument().appendChild(doctype);
/*  25*/        if (d.isForceQuirks()) {
/*  26*/            tb.getDocument().quirksMode(Document.QuirksMode.quirks); 
/*   0*/           }
/*  27*/        tb.transition(BeforeHtml);
/*   0*/      } else {
/*  30*/        tb.transition(BeforeHtml);
/*  31*/        return tb.process(t);
/*   0*/      } 
/*  33*/      return true;
/*   0*/    }
/*   0*/  },
/*  36*/  BeforeHtml {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  38*/      if (t.isDoctype()) {
/*  39*/        tb.error(this);
/*  40*/        return false;
/*   0*/      } 
/*  41*/      if (t.isComment()) {
/*  42*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  43*/        if (isWhitespace(t)) {
/*  44*/            return true; 
/*   0*/           }
/*  45*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*  46*/          tb.insert(t.asStartTag());
/*  47*/          tb.transition(BeforeHead);
/*   0*/        } else {
/*  48*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "head", "body", "html", "br" })) {
/*  49*/              return anythingElse(t, tb); 
/*   0*/             }
/*  50*/          if (t.isEndTag()) {
/*  51*/            tb.error(this);
/*  52*/            return false;
/*   0*/          } 
/*  54*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/*  56*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*  60*/      tb.insertStartTag("html");
/*  61*/      tb.transition(BeforeHead);
/*  62*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/*  65*/  BeforeHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  67*/      if (isWhitespace(t)) {
/*  68*/          return true; 
/*   0*/         }
/*  69*/      if (t.isComment()) {
/*  70*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  71*/        if (t.isDoctype()) {
/*  72*/          tb.error(this);
/*  73*/          return false;
/*   0*/        } 
/*  74*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*  75*/            return InBody.process(t, tb); 
/*   0*/           }
/*  76*/        if (t.isStartTag() && t.asStartTag().normalName().equals("head")) {
/*  77*/          Element head = tb.insert(t.asStartTag());
/*  78*/          tb.setHeadElement(head);
/*  79*/          tb.transition(InHead);
/*   0*/        } else {
/*  80*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "head", "body", "html", "br" })) {
/*  81*/            tb.processStartTag("head");
/*  82*/            return tb.process(t);
/*   0*/          } 
/*  83*/          if (t.isEndTag()) {
/*  84*/            tb.error(this);
/*  85*/            return false;
/*   0*/          } 
/*  87*/          tb.processStartTag("head");
/*  88*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*  90*/      return true;
/*   0*/    }
/*   0*/  },
/*  93*/  InHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*  95*/      if (isWhitespace(t)) {
/*  96*/        tb.insert(t.asCharacter());
/*  97*/        return true;
/*   0*/      } 
/*  99*/      switch (t.type) {
/*   0*/        case Comment:
/* 101*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 104*/          tb.error(this);
/* 105*/          return false;
/*   0*/        case StartTag:
/* 107*/          start = t.asStartTag();
/* 108*/          name = start.normalName();
/* 109*/          if (name.equals("html")) {
/* 110*/              return InBody.process(t, tb); 
/*   0*/             }
/* 111*/          if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "command", "link" })) {
/* 112*/            Element el = tb.insertEmpty(start);
/* 114*/            if (name.equals("base") && el.hasAttr("href")) {
/* 115*/                tb.maybeSetBaseUri(el); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 116*/          if (name.equals("meta")) {
/* 117*/            Element element = tb.insertEmpty(start);
/*   0*/            break;
/*   0*/          } 
/* 119*/          if (name.equals("title")) {
/* 120*/            handleRcData(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 121*/          if (StringUtil.in(name, new String[] { "noframes", "style" })) {
/* 122*/            handleRawtext(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 123*/          if (name.equals("noscript")) {
/* 125*/            tb.insert(start);
/* 126*/            tb.transition(InHeadNoscript);
/*   0*/            break;
/*   0*/          } 
/* 127*/          if (name.equals("script")) {
/* 130*/            tb.tokeniser.transition(TokeniserState.ScriptData);
/* 131*/            tb.markInsertionMode();
/* 132*/            tb.transition(Text);
/* 133*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/* 134*/          if (name.equals("head")) {
/* 135*/            tb.error(this);
/* 136*/            return false;
/*   0*/          } 
/* 138*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 142*/          end = t.asEndTag();
/* 143*/          name = end.normalName();
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
/* 161*/      tb.processEndTag("head");
/* 162*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 165*/  InHeadNoscript {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 167*/      if (t.isDoctype()) {
/* 168*/        tb.error(this);
/*   0*/      } else {
/* 169*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/* 170*/            return tb.process(t, InBody); 
/*   0*/           }
/* 171*/        if (t.isEndTag() && t.asEndTag().normalName().equals("noscript")) {
/* 172*/          tb.pop();
/* 173*/          tb.transition(InHead);
/*   0*/        } else {
/* 174*/          if (isWhitespace(t) || t.isComment() || (t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "basefont", "bgsound", "link", "meta", "noframes", "style" }))) {
/* 176*/              return tb.process(t, InHead); 
/*   0*/             }
/* 177*/          if (t.isEndTag() && t.asEndTag().normalName().equals("br")) {
/* 178*/              return anythingElse(t, tb); 
/*   0*/             }
/* 179*/          if ((t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "head", "noscript" })) || t.isEndTag()) {
/* 180*/            tb.error(this);
/* 181*/            return false;
/*   0*/          } 
/* 183*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/* 185*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/* 189*/      tb.error(this);
/* 190*/      tb.insert(new Token.Character().data(t.toString()));
/* 191*/      return true;
/*   0*/    }
/*   0*/  },
/* 194*/  AfterHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 196*/      if (isWhitespace(t)) {
/* 197*/        tb.insert(t.asCharacter());
/* 198*/      } else if (t.isComment()) {
/* 199*/        tb.insert(t.asComment());
/* 200*/      } else if (t.isDoctype()) {
/* 201*/        tb.error(this);
/* 202*/      } else if (t.isStartTag()) {
/* 203*/        Token.StartTag startTag = t.asStartTag();
/* 204*/        String name = startTag.normalName();
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
/* 227*/        if (StringUtil.in(t.asEndTag().normalName(), new String[] { "body", "html" })) {
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
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/* 240*/      tb.processStartTag("body");
/* 241*/      tb.framesetOk(true);
/* 242*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 245*/  InBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/* 247*/      switch (t.type) {
/*   0*/        case Character:
/* 249*/          c = t.asCharacter();
/* 250*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/* 252*/            tb.error(this);
/* 253*/            return false;
/*   0*/          } 
/* 254*/          if (tb.framesetOk() && isWhitespace(c)) {
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
/* 275*/          name = startTag.normalName();
/* 276*/          if (name.equals("a")) {
/* 277*/            if (tb.getActiveFormattingElement("a") != null) {
/* 278*/              tb.error(this);
/* 279*/              tb.processEndTag("a");
/* 282*/              Element remainingA = tb.getFromStack("a");
/* 283*/              if (remainingA != null) {
/* 284*/                tb.removeFromActiveFormattingElements(remainingA);
/* 285*/                tb.removeFromStack(remainingA);
/*   0*/              } 
/*   0*/            } 
/* 288*/            tb.reconstructFormattingElements();
/* 289*/            Element a = tb.insert(startTag);
/* 290*/            tb.pushActiveFormattingElements(a);
/*   0*/            break;
/*   0*/          } 
/* 291*/          if (StringUtil.inSorted(name, Constants.InBodyStartEmptyFormatters)) {
/* 292*/            tb.reconstructFormattingElements();
/* 293*/            tb.insertEmpty(startTag);
/* 294*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 295*/          if (StringUtil.inSorted(name, Constants.InBodyStartPClosers)) {
/* 296*/            if (tb.inButtonScope("p")) {
/* 297*/                tb.processEndTag("p"); 
/*   0*/               }
/* 299*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 300*/          if (name.equals("span")) {
/* 302*/            tb.reconstructFormattingElements();
/* 303*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 304*/          if (name.equals("li")) {
/* 305*/            tb.framesetOk(false);
/* 306*/            ArrayList<Element> stack = tb.getStack();
/* 307*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 308*/              Element el = stack.get(i);
/* 309*/              if (el.nodeName().equals("li")) {
/* 310*/                tb.processEndTag("li");
/*   0*/                break;
/*   0*/              } 
/* 313*/              if (tb.isSpecial(el) && !StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 316*/            if (tb.inButtonScope("p")) {
/* 317*/                tb.processEndTag("p"); 
/*   0*/               }
/* 319*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 320*/          if (name.equals("html")) {
/* 321*/            tb.error(this);
/* 323*/            Element html = tb.getStack().get(0);
/* 324*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 325*/              if (!html.hasAttr(attribute.getKey())) {
/* 326*/                  html.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 328*/          if (StringUtil.inSorted(name, Constants.InBodyStartToHead)) {
/* 329*/              return tb.process(t, InHead); 
/*   0*/             }
/* 330*/          if (name.equals("body")) {
/* 331*/            tb.error(this);
/* 332*/            ArrayList<Element> stack = tb.getStack();
/* 333*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 335*/                return false; 
/*   0*/               }
/* 337*/            tb.framesetOk(false);
/* 338*/            Element body = stack.get(1);
/* 339*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 340*/              if (!body.hasAttr(attribute.getKey())) {
/* 341*/                  body.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 344*/          if (name.equals("frameset")) {
/* 345*/            tb.error(this);
/* 346*/            ArrayList<Element> stack = tb.getStack();
/* 347*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 349*/                return false; 
/*   0*/               }
/* 350*/            if (!tb.framesetOk()) {
/* 351*/                return false; 
/*   0*/               }
/* 353*/            Element second = stack.get(1);
/* 354*/            if (second.parent() != null) {
/* 355*/                second.remove(); 
/*   0*/               }
/* 357*/            while (stack.size() > 1) {
/* 358*/                stack.remove(stack.size() - 1); 
/*   0*/               }
/* 359*/            tb.insert(startTag);
/* 360*/            tb.transition(InFrameset);
/*   0*/            break;
/*   0*/          } 
/* 362*/          if (StringUtil.inSorted(name, Constants.Headings)) {
/* 363*/            if (tb.inButtonScope("p")) {
/* 364*/                tb.processEndTag("p"); 
/*   0*/               }
/* 366*/            if (StringUtil.inSorted(tb.currentElement().nodeName(), Constants.Headings)) {
/* 367*/              tb.error(this);
/* 368*/              tb.pop();
/*   0*/            } 
/* 370*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 371*/          if (StringUtil.inSorted(name, Constants.InBodyStartPreListing)) {
/* 372*/            if (tb.inButtonScope("p")) {
/* 373*/                tb.processEndTag("p"); 
/*   0*/               }
/* 375*/            tb.insert(startTag);
/* 377*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 378*/          if (name.equals("form")) {
/* 379*/            if (tb.getFormElement() != null) {
/* 380*/              tb.error(this);
/* 381*/              return false;
/*   0*/            } 
/* 383*/            if (tb.inButtonScope("p")) {
/* 384*/                tb.processEndTag("p"); 
/*   0*/               }
/* 386*/            tb.insertForm(startTag, true);
/*   0*/            break;
/*   0*/          } 
/* 387*/          if (StringUtil.inSorted(name, Constants.DdDt)) {
/* 388*/            tb.framesetOk(false);
/* 389*/            ArrayList<Element> stack = tb.getStack();
/* 390*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 391*/              Element el = stack.get(i);
/* 392*/              if (StringUtil.inSorted(el.nodeName(), Constants.DdDt)) {
/* 393*/                tb.processEndTag(el.nodeName());
/*   0*/                break;
/*   0*/              } 
/* 396*/              if (tb.isSpecial(el) && !StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 399*/            if (tb.inButtonScope("p")) {
/* 400*/                tb.processEndTag("p"); 
/*   0*/               }
/* 402*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 403*/          if (name.equals("plaintext")) {
/* 404*/            if (tb.inButtonScope("p")) {
/* 405*/                tb.processEndTag("p"); 
/*   0*/               }
/* 407*/            tb.insert(startTag);
/* 408*/            tb.tokeniser.transition(TokeniserState.PLAINTEXT);
/*   0*/            break;
/*   0*/          } 
/* 409*/          if (name.equals("button")) {
/* 410*/            if (tb.inButtonScope("button")) {
/* 412*/              tb.error(this);
/* 413*/              tb.processEndTag("button");
/* 414*/              tb.process(startTag);
/*   0*/              break;
/*   0*/            } 
/* 416*/            tb.reconstructFormattingElements();
/* 417*/            tb.insert(startTag);
/* 418*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 420*/          if (StringUtil.inSorted(name, Constants.Formatters)) {
/* 421*/            tb.reconstructFormattingElements();
/* 422*/            Element el = tb.insert(startTag);
/* 423*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 424*/          if (name.equals("nobr")) {
/* 425*/            tb.reconstructFormattingElements();
/* 426*/            if (tb.inScope("nobr")) {
/* 427*/              tb.error(this);
/* 428*/              tb.processEndTag("nobr");
/* 429*/              tb.reconstructFormattingElements();
/*   0*/            } 
/* 431*/            Element el = tb.insert(startTag);
/* 432*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 433*/          if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
/* 434*/            tb.reconstructFormattingElements();
/* 435*/            tb.insert(startTag);
/* 436*/            tb.insertMarkerToFormattingElements();
/* 437*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 438*/          if (name.equals("table")) {
/* 439*/            if (tb.getDocument().quirksMode() != Document.QuirksMode.quirks && tb.inButtonScope("p")) {
/* 440*/                tb.processEndTag("p"); 
/*   0*/               }
/* 442*/            tb.insert(startTag);
/* 443*/            tb.framesetOk(false);
/* 444*/            tb.transition(InTable);
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
/* 450*/          if (StringUtil.inSorted(name, Constants.InBodyStartMedia)) {
/* 451*/            tb.insertEmpty(startTag);
/*   0*/            break;
/*   0*/          } 
/* 452*/          if (name.equals("hr")) {
/* 453*/            if (tb.inButtonScope("p")) {
/* 454*/                tb.processEndTag("p"); 
/*   0*/               }
/* 456*/            tb.insertEmpty(startTag);
/* 457*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 458*/          if (name.equals("image")) {
/* 459*/            if (tb.getFromStack("svg") == null) {
/* 460*/                return tb.process(startTag.name("img")); 
/*   0*/               }
/* 462*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 463*/          if (name.equals("isindex")) {
/* 465*/            tb.error(this);
/* 466*/            if (tb.getFormElement() != null) {
/* 467*/                return false; 
/*   0*/               }
/* 469*/            tb.processStartTag("form");
/* 470*/            if (startTag.attributes.hasKey("action")) {
/* 471*/              Element form = tb.getFormElement();
/* 472*/              form.attr("action", startTag.attributes.get("action"));
/*   0*/            } 
/* 474*/            tb.processStartTag("hr");
/* 475*/            tb.processStartTag("label");
/* 477*/            String prompt = startTag.attributes.hasKey("prompt") ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: ";
/* 481*/            tb.process(new Token.Character().data(prompt));
/* 484*/            Attributes inputAttribs = new Attributes();
/* 485*/            for (Attribute attr : (Iterable<Attribute>)startTag.attributes) {
/* 486*/              if (!StringUtil.inSorted(attr.getKey(), Constants.InBodyStartInputAttribs)) {
/* 487*/                  inputAttribs.put(attr); 
/*   0*/                 }
/*   0*/            } 
/* 489*/            inputAttribs.put("name", "isindex");
/* 490*/            tb.processStartTag("input", inputAttribs);
/* 491*/            tb.processEndTag("label");
/* 492*/            tb.processStartTag("hr");
/* 493*/            tb.processEndTag("form");
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
/* 503*/                tb.processEndTag("p"); 
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
/* 519*/            HtmlTreeBuilderState state = tb.state();
/* 520*/            if (state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow) || state.equals(InCell)) {
/* 521*/              tb.transition(InSelectInTable);
/*   0*/              break;
/*   0*/            } 
/* 523*/            tb.transition(InSelect);
/*   0*/            break;
/*   0*/          } 
/* 524*/          if (StringUtil.inSorted(name, Constants.InBodyStartOptions)) {
/* 525*/            if (tb.currentElement().nodeName().equals("option")) {
/* 526*/                tb.processEndTag("option"); 
/*   0*/               }
/* 527*/            tb.reconstructFormattingElements();
/* 528*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 529*/          if (StringUtil.inSorted(name, Constants.InBodyStartRuby)) {
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
/*   0*/            break;
/*   0*/          } 
/* 542*/          if (name.equals("svg")) {
/* 543*/            tb.reconstructFormattingElements();
/* 545*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 546*/          if (StringUtil.inSorted(name, Constants.InBodyStartDrop)) {
/* 547*/            tb.error(this);
/* 548*/            return false;
/*   0*/          } 
/* 550*/          tb.reconstructFormattingElements();
/* 551*/          tb.insert(startTag);
/*   0*/          break;
/*   0*/        case EndTag:
/* 556*/          endTag = t.asEndTag();
/* 557*/          name = endTag.normalName();
/* 558*/          if (StringUtil.inSorted(name, Constants.InBodyEndAdoptionFormatters)) {
/* 560*/            for (int i = 0; i < 8; i++) {
/* 561*/              Element formatEl = tb.getActiveFormattingElement(name);
/* 562*/              if (formatEl == null) {
/* 563*/                  return anyOtherEndTag(t, tb); 
/*   0*/                 }
/* 564*/              if (!tb.onStack(formatEl)) {
/* 565*/                tb.error(this);
/* 566*/                tb.removeFromActiveFormattingElements(formatEl);
/* 567*/                return true;
/*   0*/              } 
/* 568*/              if (!tb.inScope(formatEl.nodeName())) {
/* 569*/                tb.error(this);
/* 570*/                return false;
/*   0*/              } 
/* 571*/              if (tb.currentElement() != formatEl) {
/* 572*/                  tb.error(this); 
/*   0*/                 }
/* 574*/              Element furthestBlock = null;
/* 575*/              Element commonAncestor = null;
/*   0*/              boolean seenFormattingElement = false;
/* 577*/              ArrayList<Element> stack = tb.getStack();
/* 580*/              int stackSize = stack.size();
/* 581*/              for (int si = 0; si < stackSize && si < 64; si++) {
/* 582*/                Element el = stack.get(si);
/* 583*/                if (el == formatEl) {
/* 584*/                  commonAncestor = stack.get(si - 1);
/* 585*/                  seenFormattingElement = true;
/* 586*/                } else if (seenFormattingElement && tb.isSpecial(el)) {
/* 587*/                  furthestBlock = el;
/*   0*/                  break;
/*   0*/                } 
/*   0*/              } 
/* 591*/              if (furthestBlock == null) {
/* 592*/                tb.popStackToClose(formatEl.nodeName());
/* 593*/                tb.removeFromActiveFormattingElements(formatEl);
/* 594*/                return true;
/*   0*/              } 
/* 599*/              Element node = furthestBlock;
/* 600*/              Element lastNode = furthestBlock;
/* 601*/              for (int j = 0; j < 3; j++) {
/* 602*/                if (tb.onStack(node)) {
/* 603*/                    node = tb.aboveOnStack(node); 
/*   0*/                   }
/* 604*/                if (!tb.isInActiveFormattingElements(node)) {
/* 605*/                  tb.removeFromStack(node);
/*   0*/                } else {
/* 607*/                  if (node == formatEl) {
/*   0*/                      break; 
/*   0*/                     }
/* 610*/                  Element replacement = new Element(Tag.valueOf(node.nodeName(), ParseSettings.preserveCase), tb.getBaseUri());
/* 612*/                  tb.replaceActiveFormattingElement(node, replacement);
/* 613*/                  tb.replaceOnStack(node, replacement);
/* 614*/                  node = replacement;
/* 616*/                  if (lastNode == furthestBlock);
/* 620*/                  if (lastNode.parent() != null) {
/* 621*/                      lastNode.remove(); 
/*   0*/                     }
/* 622*/                  node.appendChild(lastNode);
/* 624*/                  lastNode = node;
/*   0*/                } 
/*   0*/              } 
/* 627*/              if (StringUtil.inSorted(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
/* 628*/                if (lastNode.parent() != null) {
/* 629*/                    lastNode.remove(); 
/*   0*/                   }
/* 630*/                tb.insertInFosterParent(lastNode);
/*   0*/              } else {
/* 632*/                if (lastNode.parent() != null) {
/* 633*/                    lastNode.remove(); 
/*   0*/                   }
/* 634*/                commonAncestor.appendChild(lastNode);
/*   0*/              } 
/* 637*/              Element adopter = new Element(formatEl.tag(), tb.getBaseUri());
/* 638*/              adopter.attributes().addAll(formatEl.attributes());
/* 639*/              Node[] childNodes = furthestBlock.childNodes().<Node>toArray(new Node[furthestBlock.childNodeSize()]);
/* 640*/              for (Node childNode : childNodes) {
/* 641*/                  adopter.appendChild(childNode); 
/*   0*/                 }
/* 643*/              furthestBlock.appendChild(adopter);
/* 644*/              tb.removeFromActiveFormattingElements(formatEl);
/* 646*/              tb.removeFromStack(formatEl);
/* 647*/              tb.insertOnStackAfter(furthestBlock, adopter);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 649*/          if (StringUtil.inSorted(name, Constants.InBodyEndClosers)) {
/* 650*/            if (!tb.inScope(name)) {
/* 652*/              tb.error(this);
/* 653*/              return false;
/*   0*/            } 
/* 655*/            tb.generateImpliedEndTags();
/* 656*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 657*/                tb.error(this); 
/*   0*/               }
/* 658*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 660*/          if (name.equals("span")) {
/* 662*/              return anyOtherEndTag(t, tb); 
/*   0*/             }
/* 663*/          if (name.equals("li")) {
/* 664*/            if (!tb.inListItemScope(name)) {
/* 665*/              tb.error(this);
/* 666*/              return false;
/*   0*/            } 
/* 668*/            tb.generateImpliedEndTags(name);
/* 669*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 670*/                tb.error(this); 
/*   0*/               }
/* 671*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 673*/          if (name.equals("body")) {
/* 674*/            if (!tb.inScope("body")) {
/* 675*/              tb.error(this);
/* 676*/              return false;
/*   0*/            } 
/* 679*/            tb.transition(AfterBody);
/*   0*/            break;
/*   0*/          } 
/* 681*/          if (name.equals("html")) {
/* 682*/            boolean notIgnored = tb.processEndTag("body");
/* 683*/            if (notIgnored) {
/* 684*/                return tb.process(endTag); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 685*/          if (name.equals("form")) {
/* 686*/            Element currentForm = tb.getFormElement();
/* 687*/            tb.setFormElement(null);
/* 688*/            if (currentForm == null || !tb.inScope(name)) {
/* 689*/              tb.error(this);
/* 690*/              return false;
/*   0*/            } 
/* 692*/            tb.generateImpliedEndTags();
/* 693*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 694*/                tb.error(this); 
/*   0*/               }
/* 696*/            tb.removeFromStack(currentForm);
/*   0*/            break;
/*   0*/          } 
/* 698*/          if (name.equals("p")) {
/* 699*/            if (!tb.inButtonScope(name)) {
/* 700*/              tb.error(this);
/* 701*/              tb.processStartTag(name);
/* 702*/              return tb.process(endTag);
/*   0*/            } 
/* 704*/            tb.generateImpliedEndTags(name);
/* 705*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 706*/                tb.error(this); 
/*   0*/               }
/* 707*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 709*/          if (StringUtil.inSorted(name, Constants.DdDt)) {
/* 710*/            if (!tb.inScope(name)) {
/* 711*/              tb.error(this);
/* 712*/              return false;
/*   0*/            } 
/* 714*/            tb.generateImpliedEndTags(name);
/* 715*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 716*/                tb.error(this); 
/*   0*/               }
/* 717*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 719*/          if (StringUtil.inSorted(name, Constants.Headings)) {
/* 720*/            if (!tb.inScope(Constants.Headings)) {
/* 721*/              tb.error(this);
/* 722*/              return false;
/*   0*/            } 
/* 724*/            tb.generateImpliedEndTags(name);
/* 725*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 726*/                tb.error(this); 
/*   0*/               }
/* 727*/            tb.popStackToClose(Constants.Headings);
/*   0*/            break;
/*   0*/          } 
/* 729*/          if (name.equals("sarcasm")) {
/* 731*/              return anyOtherEndTag(t, tb); 
/*   0*/             }
/* 732*/          if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
/* 733*/            if (!tb.inScope("name")) {
/* 734*/              if (!tb.inScope(name)) {
/* 735*/                tb.error(this);
/* 736*/                return false;
/*   0*/              } 
/* 738*/              tb.generateImpliedEndTags();
/* 739*/              if (!tb.currentElement().nodeName().equals(name)) {
/* 740*/                  tb.error(this); 
/*   0*/                 }
/* 741*/              tb.popStackToClose(name);
/* 742*/              tb.clearFormattingElementsToLastMarker();
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 744*/          if (name.equals("br")) {
/* 745*/            tb.error(this);
/* 746*/            tb.processStartTag("br");
/* 747*/            return false;
/*   0*/          } 
/* 749*/          return anyOtherEndTag(t, tb);
/*   0*/      } 
/* 758*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
/* 762*/      String name = t.asEndTag().name();
/* 763*/      ArrayList<Element> stack = tb.getStack();
/* 764*/      for (int pos = stack.size() - 1; pos >= 0; pos--) {
/* 765*/        Element node = stack.get(pos);
/* 766*/        if (node.nodeName().equals(name)) {
/* 767*/          tb.generateImpliedEndTags(name);
/* 768*/          if (!name.equals(tb.currentElement().nodeName())) {
/* 769*/              tb.error(this); 
/*   0*/             }
/* 770*/          tb.popStackToClose(name);
/*   0*/          break;
/*   0*/        } 
/* 773*/        if (tb.isSpecial(node)) {
/* 774*/          tb.error(this);
/* 775*/          return false;
/*   0*/        } 
/*   0*/      } 
/* 779*/      return true;
/*   0*/    }
/*   0*/  },
/* 782*/  Text {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 785*/      if (t.isCharacter()) {
/* 786*/        tb.insert(t.asCharacter());
/*   0*/      } else {
/* 787*/        if (t.isEOF()) {
/* 788*/          tb.error(this);
/* 790*/          tb.pop();
/* 791*/          tb.transition(tb.originalState());
/* 792*/          return tb.process(t);
/*   0*/        } 
/* 793*/        if (t.isEndTag()) {
/* 795*/          tb.pop();
/* 796*/          tb.transition(tb.originalState());
/*   0*/        } 
/*   0*/      } 
/* 798*/      return true;
/*   0*/    }
/*   0*/  },
/* 801*/  InTable {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 803*/      if (t.isCharacter()) {
/* 804*/        tb.newPendingTableCharacters();
/* 805*/        tb.markInsertionMode();
/* 806*/        tb.transition(InTableText);
/* 807*/        return tb.process(t);
/*   0*/      } 
/* 808*/      if (t.isComment()) {
/* 809*/        tb.insert(t.asComment());
/* 810*/        return true;
/*   0*/      } 
/* 811*/      if (t.isDoctype()) {
/* 812*/        tb.error(this);
/* 813*/        return false;
/*   0*/      } 
/* 814*/      if (t.isStartTag()) {
/* 815*/        Token.StartTag startTag = t.asStartTag();
/* 816*/        String name = startTag.normalName();
/* 817*/        if (name.equals("caption")) {
/* 818*/          tb.clearStackToTableContext();
/* 819*/          tb.insertMarkerToFormattingElements();
/* 820*/          tb.insert(startTag);
/* 821*/          tb.transition(InCaption);
/* 822*/        } else if (name.equals("colgroup")) {
/* 823*/          tb.clearStackToTableContext();
/* 824*/          tb.insert(startTag);
/* 825*/          tb.transition(InColumnGroup);
/*   0*/        } else {
/* 826*/          if (name.equals("col")) {
/* 827*/            tb.processStartTag("colgroup");
/* 828*/            return tb.process(t);
/*   0*/          } 
/* 829*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/* 830*/            tb.clearStackToTableContext();
/* 831*/            tb.insert(startTag);
/* 832*/            tb.transition(InTableBody);
/*   0*/          } else {
/* 833*/            if (StringUtil.in(name, new String[] { "td", "th", "tr" })) {
/* 834*/              tb.processStartTag("tbody");
/* 835*/              return tb.process(t);
/*   0*/            } 
/* 836*/            if (name.equals("table")) {
/* 837*/              tb.error(this);
/* 838*/              boolean processed = tb.processEndTag("table");
/* 839*/              if (processed) {
/* 840*/                  return tb.process(t); 
/*   0*/                 }
/*   0*/            } else {
/* 841*/              if (StringUtil.in(name, new String[] { "style", "script" })) {
/* 842*/                  return tb.process(t, InHead); 
/*   0*/                 }
/* 843*/              if (name.equals("input")) {
/* 844*/                if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
/* 845*/                    return anythingElse(t, tb); 
/*   0*/                   }
/* 847*/                tb.insertEmpty(startTag);
/* 849*/              } else if (name.equals("form")) {
/* 850*/                tb.error(this);
/* 851*/                if (tb.getFormElement() != null) {
/* 852*/                    return false; 
/*   0*/                   }
/* 854*/                tb.insertForm(startTag, false);
/*   0*/              } else {
/* 857*/                return anythingElse(t, tb);
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/* 859*/        return true;
/*   0*/      } 
/* 860*/      if (t.isEndTag()) {
/* 861*/        Token.EndTag endTag = t.asEndTag();
/* 862*/        String name = endTag.normalName();
/* 864*/        if (name.equals("table")) {
/* 865*/          if (!tb.inTableScope(name)) {
/* 866*/            tb.error(this);
/* 867*/            return false;
/*   0*/          } 
/* 869*/          tb.popStackToClose("table");
/* 871*/          tb.resetInsertionMode();
/*   0*/        } else {
/* 872*/          if (StringUtil.in(name, new String[] { 
/* 872*/                "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", 
/* 872*/                "tr" })) {
/* 874*/            tb.error(this);
/* 875*/            return false;
/*   0*/          } 
/* 877*/          return anythingElse(t, tb);
/*   0*/        } 
/* 879*/        return true;
/*   0*/      } 
/* 880*/      if (t.isEOF()) {
/* 881*/        if (tb.currentElement().nodeName().equals("html")) {
/* 882*/            tb.error(this); 
/*   0*/           }
/* 883*/        return true;
/*   0*/      } 
/* 885*/      return anythingElse(t, tb);
/*   0*/    }
/*   0*/    
/*   0*/    boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*   0*/      boolean processed;
/* 889*/      tb.error(this);
/* 891*/      if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 892*/        tb.setFosterInserts(true);
/* 893*/        processed = tb.process(t, InBody);
/* 894*/        tb.setFosterInserts(false);
/*   0*/      } else {
/* 896*/        processed = tb.process(t, InBody);
/*   0*/      } 
/* 898*/      return processed;
/*   0*/    }
/*   0*/  },
/* 901*/  InTableText {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/* 903*/      switch (t.type) {
/*   0*/        case Character:
/* 905*/          c = t.asCharacter();
/* 906*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/* 907*/            tb.error(this);
/* 908*/            return false;
/*   0*/          } 
/* 910*/          tb.getPendingTableCharacters().add(c.getData());
/*   0*/          break;
/*   0*/        default:
/* 915*/          if (tb.getPendingTableCharacters().size() > 0) {
/* 916*/            for (String character : tb.getPendingTableCharacters()) {
/* 917*/              if (!isWhitespace(character)) {
/* 919*/                tb.error(this);
/* 920*/                if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 921*/                  tb.setFosterInserts(true);
/* 922*/                  tb.process(new Token.Character().data(character), InBody);
/* 923*/                  tb.setFosterInserts(false);
/*   0*/                  continue;
/*   0*/                } 
/* 925*/                tb.process(new Token.Character().data(character), InBody);
/*   0*/                continue;
/*   0*/              } 
/* 928*/              tb.insert(new Token.Character().data(character));
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
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 940*/      if (t.isEndTag() && t.asEndTag().normalName().equals("caption")) {
/* 941*/        Token.EndTag endTag = t.asEndTag();
/* 942*/        String name = endTag.normalName();
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
/* 954*/      } else if ((t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) || (t.isEndTag() && t.asEndTag().normalName().equals("table"))) {
/* 959*/        tb.error(this);
/* 960*/        boolean processed = tb.processEndTag("caption");
/* 961*/        if (processed) {
/* 962*/            return tb.process(t); 
/*   0*/           }
/*   0*/      } else {
/* 963*/        if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/* 965*/          tb.error(this);
/* 966*/          return false;
/*   0*/        } 
/* 968*/        return tb.process(t, InBody);
/*   0*/      } 
/* 970*/      return true;
/*   0*/    }
/*   0*/  },
/* 973*/  InColumnGroup {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
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
/* 988*/          switch (startTag.normalName()) {
/*   0*/            case "html":
/* 990*/              return tb.process(t, InBody);
/*   0*/            case "col":
/* 992*/              tb.insertEmpty(startTag);
/*   0*/              break;
/*   0*/          } 
/* 995*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 999*/          endTag = t.asEndTag();
/*1000*/          if (endTag.normalName.equals("colgroup")) {
/*1001*/            if (tb.currentElement().nodeName().equals("html")) {
/*1002*/              tb.error(this);
/*1003*/              return false;
/*   0*/            } 
/*1005*/            tb.pop();
/*1006*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1009*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1012*/          if (tb.currentElement().nodeName().equals("html")) {
/*1013*/              return true; 
/*   0*/             }
/*1015*/          return anythingElse(t, tb);
/*   0*/        default:
/*1017*/          return anythingElse(t, tb);
/*   0*/      } 
/*1019*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1023*/      boolean processed = tb.processEndTag("colgroup");
/*1024*/      if (processed) {
/*1025*/          return tb.process(t); 
/*   0*/         }
/*1026*/      return true;
/*   0*/    }
/*   0*/  },
/*1029*/  InTableBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/*1031*/      switch (t.type) {
/*   0*/        case StartTag:
/*1033*/          startTag = t.asStartTag();
/*1034*/          name = startTag.normalName();
/*1035*/          if (name.equals("tr")) {
/*1036*/            tb.clearStackToTableBodyContext();
/*1037*/            tb.insert(startTag);
/*1038*/            tb.transition(InRow);
/*   0*/            break;
/*   0*/          } 
/*1039*/          if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1040*/            tb.error(this);
/*1041*/            tb.processStartTag("tr");
/*1042*/            return tb.process(startTag);
/*   0*/          } 
/*1043*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead" })) {
/*1044*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1046*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1049*/          endTag = t.asEndTag();
/*1050*/          name = endTag.normalName();
/*1051*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1052*/            if (!tb.inTableScope(name)) {
/*1053*/              tb.error(this);
/*1054*/              return false;
/*   0*/            } 
/*1056*/            tb.clearStackToTableBodyContext();
/*1057*/            tb.pop();
/*1058*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1060*/          if (name.equals("table")) {
/*1061*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1062*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th", "tr" })) {
/*1063*/            tb.error(this);
/*1064*/            return false;
/*   0*/          } 
/*1066*/          return anythingElse(t, tb);
/*   0*/        default:
/*1069*/          return anythingElse(t, tb);
/*   0*/      } 
/*1071*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
/*1075*/      if (!tb.inTableScope("tbody") && !tb.inTableScope("thead") && !tb.inScope("tfoot")) {
/*1077*/        tb.error(this);
/*1078*/        return false;
/*   0*/      } 
/*1080*/      tb.clearStackToTableBodyContext();
/*1081*/      tb.processEndTag(tb.currentElement().nodeName());
/*1082*/      return tb.process(t);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1086*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/  },
/*1089*/  InRow {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1091*/      if (t.isStartTag()) {
/*1092*/        Token.StartTag startTag = t.asStartTag();
/*1093*/        String name = startTag.normalName();
/*1095*/        if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1096*/          tb.clearStackToTableRowContext();
/*1097*/          tb.insert(startTag);
/*1098*/          tb.transition(InCell);
/*1099*/          tb.insertMarkerToFormattingElements();
/*   0*/        } else {
/*1100*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr" })) {
/*1101*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1103*/          return anythingElse(t, tb);
/*   0*/        } 
/*1105*/      } else if (t.isEndTag()) {
/*1106*/        Token.EndTag endTag = t.asEndTag();
/*1107*/        String name = endTag.normalName();
/*1109*/        if (name.equals("tr")) {
/*1110*/          if (!tb.inTableScope(name)) {
/*1111*/            tb.error(this);
/*1112*/            return false;
/*   0*/          } 
/*1114*/          tb.clearStackToTableRowContext();
/*1115*/          tb.pop();
/*1116*/          tb.transition(InTableBody);
/*   0*/        } else {
/*1117*/          if (name.equals("table")) {
/*1118*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1119*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1120*/            if (!tb.inTableScope(name)) {
/*1121*/              tb.error(this);
/*1122*/              return false;
/*   0*/            } 
/*1124*/            tb.processEndTag("tr");
/*1125*/            return tb.process(t);
/*   0*/          } 
/*1126*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th" })) {
/*1127*/            tb.error(this);
/*1128*/            return false;
/*   0*/          } 
/*1130*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1133*/        return anythingElse(t, tb);
/*   0*/      } 
/*1135*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1139*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean handleMissingTr(Token t, TreeBuilder tb) {
/*1143*/      boolean processed = tb.processEndTag("tr");
/*1144*/      if (processed) {
/*1145*/          return tb.process(t); 
/*   0*/         }
/*1147*/      return false;
/*   0*/    }
/*   0*/  },
/*1150*/  InCell {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1152*/      if (t.isEndTag()) {
/*1153*/        Token.EndTag endTag = t.asEndTag();
/*1154*/        String name = endTag.normalName();
/*1156*/        if (StringUtil.in(name, new String[] { "td", "th" })) {
/*1157*/          if (!tb.inTableScope(name)) {
/*1158*/            tb.error(this);
/*1159*/            tb.transition(InRow);
/*1160*/            return false;
/*   0*/          } 
/*1162*/          tb.generateImpliedEndTags();
/*1163*/          if (!tb.currentElement().nodeName().equals(name)) {
/*1164*/              tb.error(this); 
/*   0*/             }
/*1165*/          tb.popStackToClose(name);
/*1166*/          tb.clearFormattingElementsToLastMarker();
/*1167*/          tb.transition(InRow);
/*   0*/        } else {
/*1168*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html" })) {
/*1169*/            tb.error(this);
/*1170*/            return false;
/*   0*/          } 
/*1171*/          if (StringUtil.in(name, new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/*1172*/            if (!tb.inTableScope(name)) {
/*1173*/              tb.error(this);
/*1174*/              return false;
/*   0*/            } 
/*1176*/            closeCell(tb);
/*1177*/            return tb.process(t);
/*   0*/          } 
/*1179*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1181*/        if (t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/*1184*/          if (!tb.inTableScope("td") && !tb.inTableScope("th")) {
/*1185*/            tb.error(this);
/*1186*/            return false;
/*   0*/          } 
/*1188*/          closeCell(tb);
/*1189*/          return tb.process(t);
/*   0*/        } 
/*1191*/        return anythingElse(t, tb);
/*   0*/      } 
/*1193*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1197*/      return tb.process(t, InBody);
/*   0*/    }
/*   0*/    
/*   0*/    private void closeCell(HtmlTreeBuilder tb) {
/*1201*/      if (tb.inTableScope("td")) {
/*1202*/        tb.processEndTag("td");
/*   0*/      } else {
/*1204*/        tb.processEndTag("th");
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1207*/  InSelect {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*1209*/      switch (t.type) {
/*   0*/        case Character:
/*1211*/          c = t.asCharacter();
/*1212*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/*1213*/            tb.error(this);
/*1214*/            return false;
/*   0*/          } 
/*1216*/          tb.insert(c);
/*   0*/          break;
/*   0*/        case Comment:
/*1220*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/*1223*/          tb.error(this);
/*1224*/          return false;
/*   0*/        case StartTag:
/*1226*/          start = t.asStartTag();
/*1227*/          name = start.normalName();
/*1228*/          if (name.equals("html")) {
/*1229*/              return tb.process(start, InBody); 
/*   0*/             }
/*1230*/          if (name.equals("option")) {
/*1231*/            if (tb.currentElement().nodeName().equals("option")) {
/*1232*/                tb.processEndTag("option"); 
/*   0*/               }
/*1233*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1234*/          if (name.equals("optgroup")) {
/*1235*/            if (tb.currentElement().nodeName().equals("option")) {
/*1236*/              tb.processEndTag("option");
/*1237*/            } else if (tb.currentElement().nodeName().equals("optgroup")) {
/*1238*/              tb.processEndTag("optgroup");
/*   0*/            } 
/*1239*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1240*/          if (name.equals("select")) {
/*1241*/            tb.error(this);
/*1242*/            return tb.processEndTag("select");
/*   0*/          } 
/*1243*/          if (StringUtil.in(name, new String[] { "input", "keygen", "textarea" })) {
/*1244*/            tb.error(this);
/*1245*/            if (!tb.inSelectScope("select")) {
/*1246*/                return false; 
/*   0*/               }
/*1247*/            tb.processEndTag("select");
/*1248*/            return tb.process(start);
/*   0*/          } 
/*1249*/          if (name.equals("script")) {
/*1250*/              return tb.process(t, InHead); 
/*   0*/             }
/*1252*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1256*/          end = t.asEndTag();
/*1257*/          name = end.normalName();
/*1258*/          switch (name) {
/*   0*/            case "optgroup":
/*1260*/              if (tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
/*1261*/                  tb.processEndTag("option"); 
/*   0*/                 }
/*1262*/              if (tb.currentElement().nodeName().equals("optgroup")) {
/*1263*/                tb.pop();
/*   0*/                break;
/*   0*/              } 
/*1265*/              tb.error(this);
/*   0*/              break;
/*   0*/            case "option":
/*1268*/              if (tb.currentElement().nodeName().equals("option")) {
/*1269*/                tb.pop();
/*   0*/                break;
/*   0*/              } 
/*1271*/              tb.error(this);
/*   0*/              break;
/*   0*/            case "select":
/*1274*/              if (!tb.inSelectScope(name)) {
/*1275*/                tb.error(this);
/*1276*/                return false;
/*   0*/              } 
/*1278*/              tb.popStackToClose(name);
/*1279*/              tb.resetInsertionMode();
/*   0*/              break;
/*   0*/          } 
/*1283*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1287*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1288*/              tb.error(this); 
/*   0*/             }
/*   0*/          break;
/*   0*/        default:
/*1291*/          return anythingElse(t, tb);
/*   0*/      } 
/*1293*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1297*/      tb.error(this);
/*1298*/      return false;
/*   0*/    }
/*   0*/  },
/*1301*/  InSelectInTable {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1303*/      if (t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1304*/        tb.error(this);
/*1305*/        tb.processEndTag("select");
/*1306*/        return tb.process(t);
/*   0*/      } 
/*1307*/      if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1308*/        tb.error(this);
/*1309*/        if (tb.inTableScope(t.asEndTag().normalName())) {
/*1310*/          tb.processEndTag("select");
/*1311*/          return tb.process(t);
/*   0*/        } 
/*1313*/        return false;
/*   0*/      } 
/*1315*/      return tb.process(t, InSelect);
/*   0*/    }
/*   0*/  },
/*1319*/  AfterBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1321*/      if (isWhitespace(t)) {
/*1322*/          return tb.process(t, InBody); 
/*   0*/         }
/*1323*/      if (t.isComment()) {
/*1324*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1325*/        if (t.isDoctype()) {
/*1326*/          tb.error(this);
/*1327*/          return false;
/*   0*/        } 
/*1328*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*1329*/            return tb.process(t, InBody); 
/*   0*/           }
/*1330*/        if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
/*1331*/          if (tb.isFragmentParsing()) {
/*1332*/            tb.error(this);
/*1333*/            return false;
/*   0*/          } 
/*1335*/          tb.transition(AfterAfterBody);
/*1337*/        } else if (!t.isEOF()) {
/*1340*/          tb.error(this);
/*1341*/          tb.transition(InBody);
/*1342*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1344*/      return true;
/*   0*/    }
/*   0*/  },
/*1347*/  InFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1349*/      if (isWhitespace(t)) {
/*1350*/        tb.insert(t.asCharacter());
/*1351*/      } else if (t.isComment()) {
/*1352*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1353*/        if (t.isDoctype()) {
/*1354*/          tb.error(this);
/*1355*/          return false;
/*   0*/        } 
/*1356*/        if (t.isStartTag()) {
/*1357*/          Token.StartTag start = t.asStartTag();
/*1358*/          switch (start.normalName()) {
/*   0*/            case "html":
/*1360*/              return tb.process(start, InBody);
/*   0*/            case "frameset":
/*1362*/              tb.insert(start);
/*   0*/              break;
/*   0*/            case "frame":
/*1365*/              tb.insertEmpty(start);
/*   0*/              break;
/*   0*/            case "noframes":
/*1368*/              return tb.process(start, InHead);
/*   0*/            default:
/*1370*/              tb.error(this);
/*1371*/              return false;
/*   0*/          } 
/*1373*/        } else if (t.isEndTag() && t.asEndTag().normalName().equals("frameset")) {
/*1374*/          if (tb.currentElement().nodeName().equals("html")) {
/*1375*/            tb.error(this);
/*1376*/            return false;
/*   0*/          } 
/*1378*/          tb.pop();
/*1379*/          if (!tb.isFragmentParsing() && !tb.currentElement().nodeName().equals("frameset")) {
/*1380*/              tb.transition(AfterFrameset); 
/*   0*/             }
/*1383*/        } else if (t.isEOF()) {
/*1384*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1385*/            tb.error(this);
/*1386*/            return true;
/*   0*/          } 
/*   0*/        } else {
/*1389*/          tb.error(this);
/*1390*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1392*/      return true;
/*   0*/    }
/*   0*/  },
/*1395*/  AfterFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1397*/      if (isWhitespace(t)) {
/*1398*/        tb.insert(t.asCharacter());
/*1399*/      } else if (t.isComment()) {
/*1400*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1401*/        if (t.isDoctype()) {
/*1402*/          tb.error(this);
/*1403*/          return false;
/*   0*/        } 
/*1404*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*1405*/            return tb.process(t, InBody); 
/*   0*/           }
/*1406*/        if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
/*1407*/          tb.transition(AfterAfterFrameset);
/*   0*/        } else {
/*1408*/          if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
/*1409*/              return tb.process(t, InHead); 
/*   0*/             }
/*1410*/          if (!t.isEOF()) {
/*1413*/            tb.error(this);
/*1414*/            return false;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1416*/      return true;
/*   0*/    }
/*   0*/  },
/*1419*/  AfterAfterBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1421*/      if (t.isComment()) {
/*1422*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1423*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
/*1424*/            return tb.process(t, InBody); 
/*   0*/           }
/*1425*/        if (!t.isEOF()) {
/*1428*/          tb.error(this);
/*1429*/          tb.transition(InBody);
/*1430*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1432*/      return true;
/*   0*/    }
/*   0*/  },
/*1435*/  AfterAfterFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1437*/      if (t.isComment()) {
/*1438*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1439*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
/*1440*/            return tb.process(t, InBody); 
/*   0*/           }
/*1441*/        if (!t.isEOF()) {
/*1443*/          if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
/*1444*/              return tb.process(t, InHead); 
/*   0*/             }
/*1446*/          tb.error(this);
/*1447*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1449*/      return true;
/*   0*/    }
/*   0*/  },
/*1452*/  ForeignContent {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1454*/      return true;
/*   0*/    }
/*   0*/  };
/*   0*/  
/*1459*/  private static String nullString = String.valueOf(Character.MIN_VALUE);
/*   0*/  
/*   0*/  private static boolean isWhitespace(Token t) {
/*1464*/    if (t.isCharacter()) {
/*1465*/      String data = t.asCharacter().getData();
/*1466*/      return isWhitespace(data);
/*   0*/    } 
/*1468*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isWhitespace(String data) {
/*1473*/    for (int i = 0; i < data.length(); i++) {
/*1474*/      char c = data.charAt(i);
/*1475*/      if (!StringUtil.isWhitespace(c)) {
/*1476*/          return false; 
/*   0*/         }
/*   0*/    } 
/*1478*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
/*1482*/    tb.tokeniser.transition(TokeniserState.Rcdata);
/*1483*/    tb.markInsertionMode();
/*1484*/    tb.transition(Text);
/*1485*/    tb.insert(startTag);
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
/*1489*/    tb.insert(startTag);
/*1490*/    tb.tokeniser.transition(TokeniserState.Rawtext);
/*1491*/    tb.markInsertionMode();
/*1492*/    tb.transition(Text);
/*   0*/  }
/*   0*/  
/*   0*/  abstract boolean process(Token paramToken, HtmlTreeBuilder paramHtmlTreeBuilder);
/*   0*/  
/*   0*/  private static final class Constants {
/*1498*/    private static final String[] InBodyStartToHead = new String[] { "base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title" };
/*   0*/    
/*1499*/    private static final String[] InBodyStartPClosers = new String[] { 
/*1499*/        "address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", 
/*1499*/        "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", 
/*1499*/        "summary", "ul" };
/*   0*/    
/*1502*/    private static final String[] Headings = new String[] { "h1", "h2", "h3", "h4", "h5", "h6" };
/*   0*/    
/*1503*/    private static final String[] InBodyStartPreListing = new String[] { "pre", "listing" };
/*   0*/    
/*1504*/    private static final String[] InBodyStartLiBreakers = new String[] { "address", "div", "p" };
/*   0*/    
/*1505*/    private static final String[] DdDt = new String[] { "dd", "dt" };
/*   0*/    
/*1506*/    private static final String[] Formatters = new String[] { 
/*1506*/        "b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", 
/*1506*/        "tt", "u" };
/*   0*/    
/*1507*/    private static final String[] InBodyStartApplets = new String[] { "applet", "marquee", "object" };
/*   0*/    
/*1508*/    private static final String[] InBodyStartEmptyFormatters = new String[] { "area", "br", "embed", "img", "keygen", "wbr" };
/*   0*/    
/*1509*/    private static final String[] InBodyStartMedia = new String[] { "param", "source", "track" };
/*   0*/    
/*1510*/    private static final String[] InBodyStartInputAttribs = new String[] { "name", "action", "prompt" };
/*   0*/    
/*1511*/    private static final String[] InBodyStartOptions = new String[] { "optgroup", "option" };
/*   0*/    
/*1512*/    private static final String[] InBodyStartRuby = new String[] { "rp", "rt" };
/*   0*/    
/*1513*/    private static final String[] InBodyStartDrop = new String[] { 
/*1513*/        "caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", 
/*1513*/        "tr" };
/*   0*/    
/*1514*/    private static final String[] InBodyEndClosers = new String[] { 
/*1514*/        "address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", 
/*1514*/        "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", 
/*1514*/        "pre", "section", "summary", "ul" };
/*   0*/    
/*1517*/    private static final String[] InBodyEndAdoptionFormatters = new String[] { 
/*1517*/        "a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", 
/*1517*/        "strike", "strong", "tt", "u" };
/*   0*/    
/*1518*/    private static final String[] InBodyEndTableFosters = new String[] { "table", "tbody", "tfoot", "thead", "tr" };
/*   0*/  }
/*   0*/}
