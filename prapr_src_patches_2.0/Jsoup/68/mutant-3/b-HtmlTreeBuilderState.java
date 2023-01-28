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
/*  17*/  Initial {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  19*/      if (isWhitespace(t)) {
/*  20*/          return true; 
/*   0*/         }
/*  21*/      if (t.isComment()) {
/*  22*/        tb.insert(t.asComment());
/*  23*/      } else if (t.isDoctype()) {
/*  26*/        Token.Doctype d = t.asDoctype();
/*  27*/        DocumentType doctype = new DocumentType(tb.settings.normalizeTag(d.getName()), d.getPublicIdentifier(), d.getSystemIdentifier());
/*  29*/        doctype.setPubSysKey(d.getPubSysKey());
/*  30*/        tb.getDocument().appendChild(doctype);
/*  31*/        if (d.isForceQuirks()) {
/*  32*/            tb.getDocument().quirksMode(Document.QuirksMode.quirks); 
/*   0*/           }
/*  33*/        tb.transition(BeforeHtml);
/*   0*/      } else {
/*  36*/        tb.transition(BeforeHtml);
/*  37*/        return tb.process(t);
/*   0*/      } 
/*  39*/      return true;
/*   0*/    }
/*   0*/  },
/*  42*/  BeforeHtml {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  44*/      if (t.isDoctype()) {
/*  45*/        tb.error(this);
/*  46*/        return false;
/*   0*/      } 
/*  47*/      if (t.isComment()) {
/*  48*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  49*/        if (isWhitespace(t)) {
/*  50*/            return true; 
/*   0*/           }
/*  51*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*  52*/          tb.insert(t.asStartTag());
/*  53*/          tb.transition(BeforeHead);
/*   0*/        } else {
/*  54*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "head", "body", "html", "br" })) {
/*  55*/              return anythingElse(t, tb); 
/*   0*/             }
/*  56*/          if (t.isEndTag()) {
/*  57*/            tb.error(this);
/*  58*/            return false;
/*   0*/          } 
/*  60*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/*  62*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*  66*/      tb.insertStartTag("html");
/*  67*/      tb.transition(BeforeHead);
/*  68*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/*  71*/  BeforeHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  73*/      if (isWhitespace(t)) {
/*  74*/          return true; 
/*   0*/         }
/*  75*/      if (t.isComment()) {
/*  76*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  77*/        if (t.isDoctype()) {
/*  78*/          tb.error(this);
/*  79*/          return false;
/*   0*/        } 
/*  80*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*  81*/            return InBody.process(t, tb); 
/*   0*/           }
/*  82*/        if (t.isStartTag() && t.asStartTag().normalName().equals("head")) {
/*  83*/          Element head = tb.insert(t.asStartTag());
/*  84*/          tb.setHeadElement(head);
/*  85*/          tb.transition(InHead);
/*   0*/        } else {
/*  86*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "head", "body", "html", "br" })) {
/*  87*/            tb.processStartTag("head");
/*  88*/            return tb.process(t);
/*   0*/          } 
/*  89*/          if (t.isEndTag()) {
/*  90*/            tb.error(this);
/*  91*/            return false;
/*   0*/          } 
/*  93*/          tb.processStartTag("head");
/*  94*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*  96*/      return true;
/*   0*/    }
/*   0*/  },
/*  99*/  InHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/* 101*/      if (isWhitespace(t)) {
/* 102*/        tb.insert(t.asCharacter());
/* 103*/        return true;
/*   0*/      } 
/* 105*/      switch (t.type) {
/*   0*/        case Comment:
/* 107*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 110*/          tb.error(this);
/* 111*/          return false;
/*   0*/        case StartTag:
/* 113*/          start = t.asStartTag();
/* 114*/          name = start.normalName();
/* 115*/          if (name.equals("html")) {
/* 116*/              return InBody.process(t, tb); 
/*   0*/             }
/* 117*/          if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "command", "link" })) {
/* 118*/            Element el = tb.insertEmpty(start);
/* 120*/            if (name.equals("base") && el.hasAttr("href")) {
/* 121*/                tb.maybeSetBaseUri(el); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 122*/          if (name.equals("meta")) {
/* 123*/            Element element = tb.insertEmpty(start);
/*   0*/            break;
/*   0*/          } 
/* 125*/          if (name.equals("title")) {
/* 126*/            handleRcData(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 127*/          if (StringUtil.in(name, new String[] { "noframes", "style" })) {
/* 128*/            handleRawtext(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 129*/          if (name.equals("noscript")) {
/* 131*/            tb.insert(start);
/* 132*/            tb.transition(InHeadNoscript);
/*   0*/            break;
/*   0*/          } 
/* 133*/          if (name.equals("script")) {
/* 136*/            tb.tokeniser.transition(TokeniserState.ScriptData);
/* 137*/            tb.markInsertionMode();
/* 138*/            tb.transition(Text);
/* 139*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/* 140*/          if (name.equals("head")) {
/* 141*/            tb.error(this);
/* 142*/            return false;
/*   0*/          } 
/* 144*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 148*/          end = t.asEndTag();
/* 149*/          name = end.normalName();
/* 150*/          if (name.equals("head")) {
/* 151*/            tb.pop();
/* 152*/            tb.transition(AfterHead);
/*   0*/            break;
/*   0*/          } 
/* 153*/          if (StringUtil.in(name, new String[] { "body", "html", "br" })) {
/* 154*/              return anythingElse(t, tb); 
/*   0*/             }
/* 156*/          tb.error(this);
/* 157*/          return false;
/*   0*/        default:
/* 161*/          return anythingElse(t, tb);
/*   0*/      } 
/* 163*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/* 167*/      tb.processEndTag("head");
/* 168*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 171*/  InHeadNoscript {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 173*/      if (t.isDoctype()) {
/* 174*/        tb.error(this);
/*   0*/      } else {
/* 175*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/* 176*/            return tb.process(t, InBody); 
/*   0*/           }
/* 177*/        if (t.isEndTag() && t.asEndTag().normalName().equals("noscript")) {
/* 178*/          tb.pop();
/* 179*/          tb.transition(InHead);
/*   0*/        } else {
/* 180*/          if (isWhitespace(t) || t.isComment() || (t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "basefont", "bgsound", "link", "meta", "noframes", "style" }))) {
/* 182*/              return tb.process(t, InHead); 
/*   0*/             }
/* 183*/          if (t.isEndTag() && t.asEndTag().normalName().equals("br")) {
/* 184*/              return anythingElse(t, tb); 
/*   0*/             }
/* 185*/          if ((t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "head", "noscript" })) || t.isEndTag()) {
/* 186*/            tb.error(this);
/* 187*/            return false;
/*   0*/          } 
/* 189*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/* 191*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/* 195*/      tb.error(this);
/* 196*/      tb.insert(new Token.Character().data(t.toString()));
/* 197*/      return true;
/*   0*/    }
/*   0*/  },
/* 200*/  AfterHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 202*/      if (isWhitespace(t)) {
/* 203*/        tb.insert(t.asCharacter());
/* 204*/      } else if (t.isComment()) {
/* 205*/        tb.insert(t.asComment());
/* 206*/      } else if (t.isDoctype()) {
/* 207*/        tb.error(this);
/* 208*/      } else if (t.isStartTag()) {
/* 209*/        Token.StartTag startTag = t.asStartTag();
/* 210*/        String name = startTag.normalName();
/* 211*/        if (name.equals("html")) {
/* 212*/            return tb.process(t, InBody); 
/*   0*/           }
/* 213*/        if (name.equals("body")) {
/* 214*/          tb.insert(startTag);
/* 215*/          tb.framesetOk(false);
/* 216*/          tb.transition(InBody);
/* 217*/        } else if (name.equals("frameset")) {
/* 218*/          tb.insert(startTag);
/* 219*/          tb.transition(InFrameset);
/* 220*/        } else if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "title" })) {
/* 221*/          tb.error(this);
/* 222*/          Element head = tb.getHeadElement();
/* 223*/          tb.push(head);
/* 224*/          tb.process(t, InHead);
/* 225*/          tb.removeFromStack(head);
/*   0*/        } else {
/* 226*/          if (name.equals("head")) {
/* 227*/            tb.error(this);
/* 228*/            return false;
/*   0*/          } 
/* 230*/          anythingElse(t, tb);
/*   0*/        } 
/* 232*/      } else if (t.isEndTag()) {
/* 233*/        if (StringUtil.in(t.asEndTag().normalName(), new String[] { "body", "html" })) {
/* 234*/          anythingElse(t, tb);
/*   0*/        } else {
/* 236*/          tb.error(this);
/* 237*/          return false;
/*   0*/        } 
/*   0*/      } else {
/* 240*/        anythingElse(t, tb);
/*   0*/      } 
/* 242*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/* 246*/      tb.processStartTag("body");
/* 247*/      tb.framesetOk(true);
/* 248*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 251*/  InBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/* 253*/      switch (t.type) {
/*   0*/        case Character:
/* 255*/          c = t.asCharacter();
/* 256*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/* 258*/            tb.error(this);
/* 259*/            return false;
/*   0*/          } 
/* 260*/          if (tb.framesetOk() && isWhitespace(c)) {
/* 261*/            tb.reconstructFormattingElements();
/* 262*/            tb.insert(c);
/*   0*/            break;
/*   0*/          } 
/* 264*/          tb.reconstructFormattingElements();
/* 265*/          tb.insert(c);
/* 266*/          tb.framesetOk(false);
/*   0*/          break;
/*   0*/        case Comment:
/* 271*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 275*/          tb.error(this);
/* 276*/          return false;
/*   0*/        case StartTag:
/* 279*/          startTag = t.asStartTag();
/* 281*/          name = startTag.normalName();
/* 282*/          if (name.equals("a")) {
/* 283*/            if (tb.getActiveFormattingElement("a") != null) {
/* 284*/              tb.error(this);
/* 285*/              tb.processEndTag("a");
/* 288*/              Element remainingA = tb.getFromStack("a");
/* 289*/              if (remainingA != null) {
/* 290*/                tb.removeFromActiveFormattingElements(remainingA);
/* 291*/                tb.removeFromStack(remainingA);
/*   0*/              } 
/*   0*/            } 
/* 294*/            tb.reconstructFormattingElements();
/* 295*/            Element a = tb.insert(startTag);
/* 296*/            tb.pushActiveFormattingElements(a);
/*   0*/            break;
/*   0*/          } 
/* 297*/          if (StringUtil.inSorted(name, Constants.InBodyStartEmptyFormatters)) {
/* 298*/            tb.reconstructFormattingElements();
/* 299*/            tb.insertEmpty(startTag);
/* 300*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 301*/          if (StringUtil.inSorted(name, Constants.InBodyStartPClosers)) {
/* 302*/            if (tb.inButtonScope("p")) {
/* 303*/                tb.processEndTag("p"); 
/*   0*/               }
/* 305*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 306*/          if (name.equals("span")) {
/* 308*/            tb.reconstructFormattingElements();
/* 309*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 310*/          if (name.equals("li")) {
/* 311*/            tb.framesetOk(false);
/* 312*/            ArrayList<Element> stack = tb.getStack();
/* 313*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 314*/              Element el = stack.get(i);
/* 315*/              if (el.nodeName().equals("li")) {
/* 316*/                tb.processEndTag("li");
/*   0*/                break;
/*   0*/              } 
/* 319*/              if (tb.isSpecial(el) && !StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 322*/            if (tb.inButtonScope("p")) {
/* 323*/                tb.processEndTag("p"); 
/*   0*/               }
/* 325*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 326*/          if (name.equals("html")) {
/* 327*/            tb.error(this);
/* 329*/            Element html = tb.getStack().get(0);
/* 330*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 331*/              if (!html.hasAttr(attribute.getKey())) {
/* 332*/                  html.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 334*/          if (StringUtil.inSorted(name, Constants.InBodyStartToHead)) {
/* 335*/              return tb.process(t, InHead); 
/*   0*/             }
/* 336*/          if (name.equals("body")) {
/* 337*/            tb.error(this);
/* 338*/            ArrayList<Element> stack = tb.getStack();
/* 339*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 341*/                return false; 
/*   0*/               }
/* 343*/            tb.framesetOk(false);
/* 344*/            Element body = stack.get(1);
/* 345*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 346*/              if (!body.hasAttr(attribute.getKey())) {
/* 347*/                  body.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 350*/          if (name.equals("frameset")) {
/* 351*/            tb.error(this);
/* 352*/            ArrayList<Element> stack = tb.getStack();
/* 353*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 355*/                return false; 
/*   0*/               }
/* 356*/            if (!tb.framesetOk()) {
/* 357*/                return false; 
/*   0*/               }
/* 359*/            Element second = stack.get(1);
/* 360*/            if (second.parent() != null) {
/* 361*/                second.remove(); 
/*   0*/               }
/* 363*/            while (stack.size() > 1) {
/* 364*/                stack.remove(stack.size() - 1); 
/*   0*/               }
/* 365*/            tb.insert(startTag);
/* 366*/            tb.transition(InFrameset);
/*   0*/            break;
/*   0*/          } 
/* 368*/          if (StringUtil.inSorted(name, Constants.Headings)) {
/* 369*/            if (tb.inButtonScope("p")) {
/* 370*/                tb.processEndTag("p"); 
/*   0*/               }
/* 372*/            if (StringUtil.inSorted(tb.currentElement().nodeName(), Constants.Headings)) {
/* 373*/              tb.error(this);
/* 374*/              tb.pop();
/*   0*/            } 
/* 376*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 377*/          if (StringUtil.inSorted(name, Constants.InBodyStartPreListing)) {
/* 378*/            if (tb.inButtonScope("p")) {
/* 379*/                tb.processEndTag("p"); 
/*   0*/               }
/* 381*/            tb.insert(startTag);
/* 383*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 384*/          if (name.equals("form")) {
/* 385*/            if (tb.getFormElement() != null) {
/* 386*/              tb.error(this);
/* 387*/              return false;
/*   0*/            } 
/* 389*/            if (tb.inButtonScope("p")) {
/* 390*/                tb.processEndTag("p"); 
/*   0*/               }
/* 392*/            tb.insertForm(startTag, true);
/*   0*/            break;
/*   0*/          } 
/* 393*/          if (StringUtil.inSorted(name, Constants.DdDt)) {
/* 394*/            tb.framesetOk(false);
/* 395*/            ArrayList<Element> stack = tb.getStack();
/* 396*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 397*/              Element el = stack.get(i);
/* 398*/              if (StringUtil.inSorted(el.nodeName(), Constants.DdDt)) {
/* 399*/                tb.processEndTag(el.nodeName());
/*   0*/                break;
/*   0*/              } 
/* 402*/              if (tb.isSpecial(el) && !StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 405*/            if (tb.inButtonScope("p")) {
/* 406*/                tb.processEndTag("p"); 
/*   0*/               }
/* 408*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 409*/          if (name.equals("plaintext")) {
/* 410*/            if (tb.inButtonScope("p")) {
/* 411*/                tb.processEndTag("p"); 
/*   0*/               }
/* 413*/            tb.insert(startTag);
/* 414*/            tb.tokeniser.transition(TokeniserState.PLAINTEXT);
/*   0*/            break;
/*   0*/          } 
/* 415*/          if (name.equals("button")) {
/* 416*/            if (tb.inButtonScope("button")) {
/* 418*/              tb.error(this);
/* 419*/              tb.processEndTag("button");
/* 420*/              tb.process(startTag);
/*   0*/              break;
/*   0*/            } 
/* 422*/            tb.reconstructFormattingElements();
/* 423*/            tb.insert(startTag);
/* 424*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 426*/          if (StringUtil.inSorted(name, Constants.Formatters)) {
/* 427*/            tb.reconstructFormattingElements();
/* 428*/            Element el = tb.insert(startTag);
/* 429*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 430*/          if (name.equals("nobr")) {
/* 431*/            tb.reconstructFormattingElements();
/* 432*/            if (tb.inScope("nobr")) {
/* 433*/              tb.error(this);
/* 434*/              tb.processEndTag("nobr");
/* 435*/              tb.reconstructFormattingElements();
/*   0*/            } 
/* 437*/            Element el = tb.insert(startTag);
/* 438*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 439*/          if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
/* 440*/            tb.reconstructFormattingElements();
/* 441*/            tb.insert(startTag);
/* 442*/            tb.insertMarkerToFormattingElements();
/* 443*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 444*/          if (name.equals("table")) {
/* 445*/            if (tb.getDocument().quirksMode() != Document.QuirksMode.quirks && tb.inButtonScope("p")) {
/* 446*/                tb.processEndTag("p"); 
/*   0*/               }
/* 448*/            tb.insert(startTag);
/* 449*/            tb.framesetOk(false);
/* 450*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/* 451*/          if (name.equals("input")) {
/* 452*/            tb.reconstructFormattingElements();
/* 453*/            Element el = tb.insertEmpty(startTag);
/* 454*/            if (!el.attr("type").equalsIgnoreCase("hidden")) {
/* 455*/                tb.framesetOk(false); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 456*/          if (StringUtil.inSorted(name, Constants.InBodyStartMedia)) {
/* 457*/            tb.insertEmpty(startTag);
/*   0*/            break;
/*   0*/          } 
/* 458*/          if (name.equals("hr")) {
/* 459*/            if (tb.inButtonScope("p")) {
/* 460*/                tb.processEndTag("p"); 
/*   0*/               }
/* 462*/            tb.insertEmpty(startTag);
/* 463*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 464*/          if (name.equals("image")) {
/* 465*/            if (tb.getFromStack("svg") == null) {
/* 466*/                return tb.process(startTag.name("img")); 
/*   0*/               }
/* 468*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 469*/          if (name.equals("isindex")) {
/* 471*/            tb.error(this);
/* 472*/            if (tb.getFormElement() != null) {
/* 473*/                return false; 
/*   0*/               }
/* 475*/            tb.processStartTag("form");
/* 476*/            if (startTag.attributes.hasKey("action")) {
/* 477*/              Element form = tb.getFormElement();
/* 478*/              form.attr("action", startTag.attributes.get("action"));
/*   0*/            } 
/* 480*/            tb.processStartTag("hr");
/* 481*/            tb.processStartTag("label");
/* 483*/            String prompt = startTag.attributes.hasKey("prompt") ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: ";
/* 487*/            tb.process(new Token.Character().data(prompt));
/* 490*/            Attributes inputAttribs = new Attributes();
/* 491*/            for (Attribute attr : (Iterable<Attribute>)startTag.attributes) {
/* 492*/              if (!StringUtil.inSorted(attr.getKey(), Constants.InBodyStartInputAttribs)) {
/* 493*/                  inputAttribs.put(attr); 
/*   0*/                 }
/*   0*/            } 
/* 495*/            inputAttribs.put("name", "isindex");
/* 496*/            tb.processStartTag("input", inputAttribs);
/* 497*/            tb.processEndTag("label");
/* 498*/            tb.processStartTag("hr");
/* 499*/            tb.processEndTag("form");
/*   0*/            break;
/*   0*/          } 
/* 500*/          if (name.equals("textarea")) {
/* 501*/            tb.insert(startTag);
/* 503*/            tb.tokeniser.transition(TokeniserState.Rcdata);
/* 504*/            tb.markInsertionMode();
/* 505*/            tb.framesetOk(false);
/* 506*/            tb.transition(Text);
/*   0*/            break;
/*   0*/          } 
/* 507*/          if (name.equals("xmp")) {
/* 508*/            if (tb.inButtonScope("p")) {
/* 509*/                tb.processEndTag("p"); 
/*   0*/               }
/* 511*/            tb.reconstructFormattingElements();
/* 512*/            tb.framesetOk(false);
/* 513*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 514*/          if (name.equals("iframe")) {
/* 515*/            tb.framesetOk(false);
/* 516*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 517*/          if (name.equals("noembed")) {
/* 519*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 520*/          if (name.equals("select")) {
/* 521*/            tb.reconstructFormattingElements();
/* 522*/            tb.insert(startTag);
/* 523*/            tb.framesetOk(false);
/* 525*/            HtmlTreeBuilderState state = tb.state();
/* 526*/            if (state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow) || state.equals(InCell)) {
/* 527*/              tb.transition(InSelectInTable);
/*   0*/              break;
/*   0*/            } 
/* 529*/            tb.transition(InSelect);
/*   0*/            break;
/*   0*/          } 
/* 530*/          if (StringUtil.inSorted(name, Constants.InBodyStartOptions)) {
/* 531*/            if (tb.currentElement().nodeName().equals("option")) {
/* 532*/                tb.processEndTag("option"); 
/*   0*/               }
/* 533*/            tb.reconstructFormattingElements();
/* 534*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 535*/          if (StringUtil.inSorted(name, Constants.InBodyStartRuby)) {
/* 536*/            if (tb.inScope("ruby")) {
/* 537*/              tb.generateImpliedEndTags();
/* 538*/              if (!tb.currentElement().nodeName().equals("ruby")) {
/* 539*/                tb.error(this);
/* 540*/                tb.popStackToBefore("ruby");
/*   0*/              } 
/* 542*/              tb.insert(startTag);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 544*/          if (name.equals("math")) {
/* 545*/            tb.reconstructFormattingElements();
/* 547*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 548*/          if (name.equals("svg")) {
/* 549*/            tb.reconstructFormattingElements();
/* 551*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 552*/          if (StringUtil.inSorted(name, Constants.InBodyStartDrop)) {
/* 553*/            tb.error(this);
/* 554*/            return false;
/*   0*/          } 
/* 556*/          tb.reconstructFormattingElements();
/* 557*/          tb.insert(startTag);
/*   0*/          break;
/*   0*/        case EndTag:
/* 562*/          endTag = t.asEndTag();
/* 563*/          name = endTag.normalName();
/* 564*/          if (StringUtil.inSorted(name, Constants.InBodyEndAdoptionFormatters)) {
/* 566*/            for (int i = 0; i < 8; i++) {
/* 567*/              Element formatEl = tb.getActiveFormattingElement(name);
/* 568*/              if (formatEl == null) {
/* 569*/                  return anyOtherEndTag(t, tb); 
/*   0*/                 }
/* 570*/              if (!tb.onStack(formatEl)) {
/* 571*/                tb.error(this);
/* 572*/                tb.removeFromActiveFormattingElements(formatEl);
/* 573*/                return true;
/*   0*/              } 
/* 574*/              if (!tb.inScope(formatEl.nodeName())) {
/* 575*/                tb.error(this);
/* 576*/                return false;
/*   0*/              } 
/* 577*/              if (tb.currentElement() != formatEl) {
/* 578*/                  tb.error(this); 
/*   0*/                 }
/* 580*/              Element furthestBlock = null;
/* 581*/              Element commonAncestor = null;
/*   0*/              boolean seenFormattingElement = false;
/* 583*/              ArrayList<Element> stack = tb.getStack();
/* 586*/              int stackSize = stack.size();
/* 587*/              for (int si = 0; si < stackSize && si < 64; si++) {
/* 588*/                Element el = stack.get(si);
/* 589*/                if (el == formatEl) {
/* 590*/                  commonAncestor = stack.get(si - 1);
/* 591*/                  seenFormattingElement = true;
/* 592*/                } else if (seenFormattingElement && tb.isSpecial(el)) {
/* 593*/                  furthestBlock = el;
/*   0*/                  break;
/*   0*/                } 
/*   0*/              } 
/* 597*/              if (furthestBlock == null) {
/* 598*/                tb.popStackToClose(formatEl.nodeName());
/* 599*/                tb.removeFromActiveFormattingElements(formatEl);
/* 600*/                return true;
/*   0*/              } 
/* 605*/              Element node = furthestBlock;
/* 606*/              Element lastNode = furthestBlock;
/* 607*/              for (int j = 0; j < 3; j++) {
/* 608*/                if (tb.onStack(node)) {
/* 609*/                    node = tb.aboveOnStack(node); 
/*   0*/                   }
/* 610*/                if (!tb.isInActiveFormattingElements(node)) {
/* 611*/                  tb.removeFromStack(node);
/*   0*/                } else {
/* 613*/                  if (node == formatEl) {
/*   0*/                      break; 
/*   0*/                     }
/* 616*/                  Element replacement = new Element(Tag.valueOf(node.nodeName(), ParseSettings.preserveCase), tb.getBaseUri());
/* 618*/                  tb.replaceActiveFormattingElement(node, replacement);
/* 619*/                  tb.replaceOnStack(node, replacement);
/* 620*/                  node = replacement;
/* 622*/                  if (lastNode == furthestBlock);
/* 626*/                  if (lastNode.parent() != null) {
/* 627*/                      lastNode.remove(); 
/*   0*/                     }
/* 628*/                  node.appendChild(lastNode);
/* 630*/                  lastNode = node;
/*   0*/                } 
/*   0*/              } 
/* 633*/              if (StringUtil.inSorted(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
/* 634*/                if (lastNode.parent() != null) {
/* 635*/                    lastNode.remove(); 
/*   0*/                   }
/* 636*/                tb.insertInFosterParent(lastNode);
/*   0*/              } else {
/* 638*/                if (lastNode.parent() != null) {
/* 639*/                    lastNode.remove(); 
/*   0*/                   }
/* 640*/                commonAncestor.appendChild(lastNode);
/*   0*/              } 
/* 643*/              Element adopter = new Element(formatEl.tag(), tb.getBaseUri());
/* 644*/              adopter.attributes().addAll(formatEl.attributes());
/* 645*/              Node[] childNodes = furthestBlock.childNodes().<Node>toArray(new Node[furthestBlock.childNodeSize()]);
/* 646*/              for (Node childNode : childNodes) {
/* 647*/                  adopter.appendChild(childNode); 
/*   0*/                 }
/* 649*/              furthestBlock.appendChild(adopter);
/* 650*/              tb.removeFromActiveFormattingElements(formatEl);
/* 652*/              tb.removeFromStack(formatEl);
/* 653*/              tb.insertOnStackAfter(furthestBlock, adopter);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 655*/          if (StringUtil.inSorted(name, Constants.InBodyEndClosers)) {
/* 656*/            if (!tb.inScope(name)) {
/* 658*/              tb.error(this);
/* 659*/              return false;
/*   0*/            } 
/* 661*/            tb.generateImpliedEndTags();
/* 662*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 663*/                tb.error(this); 
/*   0*/               }
/* 664*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 666*/          if (name.equals("span")) {
/* 668*/              return anyOtherEndTag(t, tb); 
/*   0*/             }
/* 669*/          if (name.equals("li")) {
/* 670*/            if (!tb.inListItemScope(name)) {
/* 671*/              tb.error(this);
/* 672*/              return false;
/*   0*/            } 
/* 674*/            tb.generateImpliedEndTags(name);
/* 675*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 676*/                tb.error(this); 
/*   0*/               }
/* 677*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 679*/          if (name.equals("body")) {
/* 680*/            if (!tb.inScope("body")) {
/* 681*/              tb.error(this);
/* 682*/              return false;
/*   0*/            } 
/* 685*/            tb.transition(AfterBody);
/*   0*/            break;
/*   0*/          } 
/* 687*/          if (name.equals("html")) {
/* 688*/            boolean notIgnored = tb.processEndTag("body");
/* 689*/            if (notIgnored) {
/* 690*/                return tb.process(endTag); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 691*/          if (name.equals("form")) {
/* 692*/            Element currentForm = tb.getFormElement();
/* 693*/            tb.setFormElement(null);
/* 694*/            if (currentForm == null || !tb.inScope(name)) {
/* 695*/              tb.error(this);
/* 696*/              return false;
/*   0*/            } 
/* 698*/            tb.generateImpliedEndTags();
/* 699*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 700*/                tb.error(this); 
/*   0*/               }
/* 702*/            tb.removeFromStack(currentForm);
/*   0*/            break;
/*   0*/          } 
/* 704*/          "p";
/*   0*/          if (false) {
/* 705*/            if (!tb.inButtonScope(name)) {
/* 706*/              tb.error(this);
/* 707*/              tb.processStartTag(name);
/* 708*/              return tb.process(endTag);
/*   0*/            } 
/* 710*/            tb.generateImpliedEndTags(name);
/* 711*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 712*/                tb.error(this); 
/*   0*/               }
/* 713*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 715*/          if (StringUtil.inSorted(name, Constants.DdDt)) {
/* 716*/            if (!tb.inScope(name)) {
/* 717*/              tb.error(this);
/* 718*/              return false;
/*   0*/            } 
/* 720*/            tb.generateImpliedEndTags(name);
/* 721*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 722*/                tb.error(this); 
/*   0*/               }
/* 723*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 725*/          if (StringUtil.inSorted(name, Constants.Headings)) {
/* 726*/            if (!tb.inScope(Constants.Headings)) {
/* 727*/              tb.error(this);
/* 728*/              return false;
/*   0*/            } 
/* 730*/            tb.generateImpliedEndTags(name);
/* 731*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 732*/                tb.error(this); 
/*   0*/               }
/* 733*/            tb.popStackToClose(Constants.Headings);
/*   0*/            break;
/*   0*/          } 
/* 735*/          if (name.equals("sarcasm")) {
/* 737*/              return anyOtherEndTag(t, tb); 
/*   0*/             }
/* 738*/          if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
/* 739*/            if (!tb.inScope("name")) {
/* 740*/              if (!tb.inScope(name)) {
/* 741*/                tb.error(this);
/* 742*/                return false;
/*   0*/              } 
/* 744*/              tb.generateImpliedEndTags();
/* 745*/              if (!tb.currentElement().nodeName().equals(name)) {
/* 746*/                  tb.error(this); 
/*   0*/                 }
/* 747*/              tb.popStackToClose(name);
/* 748*/              tb.clearFormattingElementsToLastMarker();
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 750*/          if (name.equals("br")) {
/* 751*/            tb.error(this);
/* 752*/            tb.processStartTag("br");
/* 753*/            return false;
/*   0*/          } 
/* 755*/          return anyOtherEndTag(t, tb);
/*   0*/      } 
/* 764*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
/* 768*/      String name = tb.settings.normalizeTag(t.asEndTag().name());
/* 769*/      ArrayList<Element> stack = tb.getStack();
/* 770*/      for (int pos = stack.size() - 1; pos >= 0; pos--) {
/* 771*/        Element node = stack.get(pos);
/* 772*/        if (node.nodeName().equals(name)) {
/* 773*/          tb.generateImpliedEndTags(name);
/* 774*/          if (!name.equals(tb.currentElement().nodeName())) {
/* 775*/              tb.error(this); 
/*   0*/             }
/* 776*/          tb.popStackToClose(name);
/*   0*/          break;
/*   0*/        } 
/* 779*/        if (tb.isSpecial(node)) {
/* 780*/          tb.error(this);
/* 781*/          return false;
/*   0*/        } 
/*   0*/      } 
/* 785*/      return true;
/*   0*/    }
/*   0*/  },
/* 788*/  Text {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 791*/      if (t.isCharacter()) {
/* 792*/        tb.insert(t.asCharacter());
/*   0*/      } else {
/* 793*/        if (t.isEOF()) {
/* 794*/          tb.error(this);
/* 796*/          tb.pop();
/* 797*/          tb.transition(tb.originalState());
/* 798*/          return tb.process(t);
/*   0*/        } 
/* 799*/        if (t.isEndTag()) {
/* 801*/          tb.pop();
/* 802*/          tb.transition(tb.originalState());
/*   0*/        } 
/*   0*/      } 
/* 804*/      return true;
/*   0*/    }
/*   0*/  },
/* 807*/  InTable {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 809*/      if (t.isCharacter()) {
/* 810*/        tb.newPendingTableCharacters();
/* 811*/        tb.markInsertionMode();
/* 812*/        tb.transition(InTableText);
/* 813*/        return tb.process(t);
/*   0*/      } 
/* 814*/      if (t.isComment()) {
/* 815*/        tb.insert(t.asComment());
/* 816*/        return true;
/*   0*/      } 
/* 817*/      if (t.isDoctype()) {
/* 818*/        tb.error(this);
/* 819*/        return false;
/*   0*/      } 
/* 820*/      if (t.isStartTag()) {
/* 821*/        Token.StartTag startTag = t.asStartTag();
/* 822*/        String name = startTag.normalName();
/* 823*/        if (name.equals("caption")) {
/* 824*/          tb.clearStackToTableContext();
/* 825*/          tb.insertMarkerToFormattingElements();
/* 826*/          tb.insert(startTag);
/* 827*/          tb.transition(InCaption);
/* 828*/        } else if (name.equals("colgroup")) {
/* 829*/          tb.clearStackToTableContext();
/* 830*/          tb.insert(startTag);
/* 831*/          tb.transition(InColumnGroup);
/*   0*/        } else {
/* 832*/          if (name.equals("col")) {
/* 833*/            tb.processStartTag("colgroup");
/* 834*/            return tb.process(t);
/*   0*/          } 
/* 835*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/* 836*/            tb.clearStackToTableContext();
/* 837*/            tb.insert(startTag);
/* 838*/            tb.transition(InTableBody);
/*   0*/          } else {
/* 839*/            if (StringUtil.in(name, new String[] { "td", "th", "tr" })) {
/* 840*/              tb.processStartTag("tbody");
/* 841*/              return tb.process(t);
/*   0*/            } 
/* 842*/            if (name.equals("table")) {
/* 843*/              tb.error(this);
/* 844*/              boolean processed = tb.processEndTag("table");
/* 845*/              if (processed) {
/* 846*/                  return tb.process(t); 
/*   0*/                 }
/*   0*/            } else {
/* 847*/              if (StringUtil.in(name, new String[] { "style", "script" })) {
/* 848*/                  return tb.process(t, InHead); 
/*   0*/                 }
/* 849*/              if (name.equals("input")) {
/* 850*/                if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
/* 851*/                    return anythingElse(t, tb); 
/*   0*/                   }
/* 853*/                tb.insertEmpty(startTag);
/* 855*/              } else if (name.equals("form")) {
/* 856*/                tb.error(this);
/* 857*/                if (tb.getFormElement() != null) {
/* 858*/                    return false; 
/*   0*/                   }
/* 860*/                tb.insertForm(startTag, false);
/*   0*/              } else {
/* 863*/                return anythingElse(t, tb);
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/* 865*/        return true;
/*   0*/      } 
/* 866*/      if (t.isEndTag()) {
/* 867*/        Token.EndTag endTag = t.asEndTag();
/* 868*/        String name = endTag.normalName();
/* 870*/        if (name.equals("table")) {
/* 871*/          if (!tb.inTableScope(name)) {
/* 872*/            tb.error(this);
/* 873*/            return false;
/*   0*/          } 
/* 875*/          tb.popStackToClose("table");
/* 877*/          tb.resetInsertionMode();
/*   0*/        } else {
/* 878*/          if (StringUtil.in(name, new String[] { 
/* 878*/                "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", 
/* 878*/                "tr" })) {
/* 880*/            tb.error(this);
/* 881*/            return false;
/*   0*/          } 
/* 883*/          return anythingElse(t, tb);
/*   0*/        } 
/* 885*/        return true;
/*   0*/      } 
/* 886*/      if (t.isEOF()) {
/* 887*/        if (tb.currentElement().nodeName().equals("html")) {
/* 888*/            tb.error(this); 
/*   0*/           }
/* 889*/        return true;
/*   0*/      } 
/* 891*/      return anythingElse(t, tb);
/*   0*/    }
/*   0*/    
/*   0*/    boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*   0*/      boolean processed;
/* 895*/      tb.error(this);
/* 897*/      if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 898*/        tb.setFosterInserts(true);
/* 899*/        processed = tb.process(t, InBody);
/* 900*/        tb.setFosterInserts(false);
/*   0*/      } else {
/* 902*/        processed = tb.process(t, InBody);
/*   0*/      } 
/* 904*/      return processed;
/*   0*/    }
/*   0*/  },
/* 907*/  InTableText {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/* 909*/      switch (t.type) {
/*   0*/        case Character:
/* 911*/          c = t.asCharacter();
/* 912*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/* 913*/            tb.error(this);
/* 914*/            return false;
/*   0*/          } 
/* 916*/          tb.getPendingTableCharacters().add(c.getData());
/*   0*/          break;
/*   0*/        default:
/* 921*/          if (tb.getPendingTableCharacters().size() > 0) {
/* 922*/            for (String character : tb.getPendingTableCharacters()) {
/* 923*/              if (!isWhitespace(character)) {
/* 925*/                tb.error(this);
/* 926*/                if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 927*/                  tb.setFosterInserts(true);
/* 928*/                  tb.process(new Token.Character().data(character), InBody);
/* 929*/                  tb.setFosterInserts(false);
/*   0*/                  continue;
/*   0*/                } 
/* 931*/                tb.process(new Token.Character().data(character), InBody);
/*   0*/                continue;
/*   0*/              } 
/* 934*/              tb.insert(new Token.Character().data(character));
/*   0*/            } 
/* 936*/            tb.newPendingTableCharacters();
/*   0*/          } 
/* 938*/          tb.transition(tb.originalState());
/* 939*/          return tb.process(t);
/*   0*/      } 
/* 941*/      return true;
/*   0*/    }
/*   0*/  },
/* 944*/  InCaption {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 946*/      if (t.isEndTag() && t.asEndTag().normalName().equals("caption")) {
/* 947*/        Token.EndTag endTag = t.asEndTag();
/* 948*/        String name = endTag.normalName();
/* 949*/        if (!tb.inTableScope(name)) {
/* 950*/          tb.error(this);
/* 951*/          return false;
/*   0*/        } 
/* 953*/        tb.generateImpliedEndTags();
/* 954*/        if (!tb.currentElement().nodeName().equals("caption")) {
/* 955*/            tb.error(this); 
/*   0*/           }
/* 956*/        tb.popStackToClose("caption");
/* 957*/        tb.clearFormattingElementsToLastMarker();
/* 958*/        tb.transition(InTable);
/* 960*/      } else if ((t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) || (t.isEndTag() && t.asEndTag().normalName().equals("table"))) {
/* 965*/        tb.error(this);
/* 966*/        boolean processed = tb.processEndTag("caption");
/* 967*/        if (processed) {
/* 968*/            return tb.process(t); 
/*   0*/           }
/*   0*/      } else {
/* 969*/        if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/* 971*/          tb.error(this);
/* 972*/          return false;
/*   0*/        } 
/* 974*/        return tb.process(t, InBody);
/*   0*/      } 
/* 976*/      return true;
/*   0*/    }
/*   0*/  },
/* 979*/  InColumnGroup {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      Token.EndTag endTag;
/* 981*/      if (isWhitespace(t)) {
/* 982*/        tb.insert(t.asCharacter());
/* 983*/        return true;
/*   0*/      } 
/* 985*/      switch (t.type) {
/*   0*/        case Comment:
/* 987*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 990*/          tb.error(this);
/*   0*/          break;
/*   0*/        case StartTag:
/* 993*/          startTag = t.asStartTag();
/* 994*/          switch (startTag.normalName()) {
/*   0*/            case "html":
/* 996*/              return tb.process(t, InBody);
/*   0*/            case "col":
/* 998*/              tb.insertEmpty(startTag);
/*   0*/              break;
/*   0*/          } 
/*1001*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1005*/          endTag = t.asEndTag();
/*1006*/          if (endTag.normalName.equals("colgroup")) {
/*1007*/            if (tb.currentElement().nodeName().equals("html")) {
/*1008*/              tb.error(this);
/*1009*/              return false;
/*   0*/            } 
/*1011*/            tb.pop();
/*1012*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1015*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1018*/          if (tb.currentElement().nodeName().equals("html")) {
/*1019*/              return true; 
/*   0*/             }
/*1021*/          return anythingElse(t, tb);
/*   0*/        default:
/*1023*/          return anythingElse(t, tb);
/*   0*/      } 
/*1025*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1029*/      boolean processed = tb.processEndTag("colgroup");
/*1030*/      if (processed) {
/*1031*/          return tb.process(t); 
/*   0*/         }
/*1032*/      return true;
/*   0*/    }
/*   0*/  },
/*1035*/  InTableBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/*1037*/      switch (t.type) {
/*   0*/        case StartTag:
/*1039*/          startTag = t.asStartTag();
/*1040*/          name = startTag.normalName();
/*1041*/          if (name.equals("template")) {
/*1042*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/*1043*/          if (name.equals("tr")) {
/*1044*/            tb.clearStackToTableBodyContext();
/*1045*/            tb.insert(startTag);
/*1046*/            tb.transition(InRow);
/*   0*/            break;
/*   0*/          } 
/*1047*/          if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1048*/            tb.error(this);
/*1049*/            tb.processStartTag("tr");
/*1050*/            return tb.process(startTag);
/*   0*/          } 
/*1051*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead" })) {
/*1052*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1054*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1057*/          endTag = t.asEndTag();
/*1058*/          name = endTag.normalName();
/*1059*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1060*/            if (!tb.inTableScope(name)) {
/*1061*/              tb.error(this);
/*1062*/              return false;
/*   0*/            } 
/*1064*/            tb.clearStackToTableBodyContext();
/*1065*/            tb.pop();
/*1066*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1068*/          if (name.equals("table")) {
/*1069*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1070*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th", "tr" })) {
/*1071*/            tb.error(this);
/*1072*/            return false;
/*   0*/          } 
/*1074*/          return anythingElse(t, tb);
/*   0*/        default:
/*1077*/          return anythingElse(t, tb);
/*   0*/      } 
/*1079*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
/*1083*/      if (!tb.inTableScope("tbody") && !tb.inTableScope("thead") && !tb.inScope("tfoot")) {
/*1085*/        tb.error(this);
/*1086*/        return false;
/*   0*/      } 
/*1088*/      tb.clearStackToTableBodyContext();
/*1089*/      tb.processEndTag(tb.currentElement().nodeName());
/*1090*/      return tb.process(t);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1094*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/  },
/*1097*/  InRow {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1099*/      if (t.isStartTag()) {
/*1100*/        Token.StartTag startTag = t.asStartTag();
/*1101*/        String name = startTag.normalName();
/*1103*/        if (name.equals("template")) {
/*1104*/          tb.insert(startTag);
/*1105*/        } else if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1106*/          tb.clearStackToTableRowContext();
/*1107*/          tb.insert(startTag);
/*1108*/          tb.transition(InCell);
/*1109*/          tb.insertMarkerToFormattingElements();
/*   0*/        } else {
/*1110*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr" })) {
/*1111*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1113*/          return anythingElse(t, tb);
/*   0*/        } 
/*1115*/      } else if (t.isEndTag()) {
/*1116*/        Token.EndTag endTag = t.asEndTag();
/*1117*/        String name = endTag.normalName();
/*1119*/        if (name.equals("tr")) {
/*1120*/          if (!tb.inTableScope(name)) {
/*1121*/            tb.error(this);
/*1122*/            return false;
/*   0*/          } 
/*1124*/          tb.clearStackToTableRowContext();
/*1125*/          tb.pop();
/*1126*/          tb.transition(InTableBody);
/*   0*/        } else {
/*1127*/          if (name.equals("table")) {
/*1128*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1129*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1130*/            if (!tb.inTableScope(name)) {
/*1131*/              tb.error(this);
/*1132*/              return false;
/*   0*/            } 
/*1134*/            tb.processEndTag("tr");
/*1135*/            return tb.process(t);
/*   0*/          } 
/*1136*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th" })) {
/*1137*/            tb.error(this);
/*1138*/            return false;
/*   0*/          } 
/*1140*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1143*/        return anythingElse(t, tb);
/*   0*/      } 
/*1145*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1149*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean handleMissingTr(Token t, TreeBuilder tb) {
/*1153*/      boolean processed = tb.processEndTag("tr");
/*1154*/      if (processed) {
/*1155*/          return tb.process(t); 
/*   0*/         }
/*1157*/      return false;
/*   0*/    }
/*   0*/  },
/*1160*/  InCell {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1162*/      if (t.isEndTag()) {
/*1163*/        Token.EndTag endTag = t.asEndTag();
/*1164*/        String name = endTag.normalName();
/*1166*/        if (StringUtil.in(name, new String[] { "td", "th" })) {
/*1167*/          if (!tb.inTableScope(name)) {
/*1168*/            tb.error(this);
/*1169*/            tb.transition(InRow);
/*1170*/            return false;
/*   0*/          } 
/*1172*/          tb.generateImpliedEndTags();
/*1173*/          if (!tb.currentElement().nodeName().equals(name)) {
/*1174*/              tb.error(this); 
/*   0*/             }
/*1175*/          tb.popStackToClose(name);
/*1176*/          tb.clearFormattingElementsToLastMarker();
/*1177*/          tb.transition(InRow);
/*   0*/        } else {
/*1178*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html" })) {
/*1179*/            tb.error(this);
/*1180*/            return false;
/*   0*/          } 
/*1181*/          if (StringUtil.in(name, new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/*1182*/            if (!tb.inTableScope(name)) {
/*1183*/              tb.error(this);
/*1184*/              return false;
/*   0*/            } 
/*1186*/            closeCell(tb);
/*1187*/            return tb.process(t);
/*   0*/          } 
/*1189*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1191*/        if (t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/*1194*/          if (!tb.inTableScope("td") && !tb.inTableScope("th")) {
/*1195*/            tb.error(this);
/*1196*/            return false;
/*   0*/          } 
/*1198*/          closeCell(tb);
/*1199*/          return tb.process(t);
/*   0*/        } 
/*1201*/        return anythingElse(t, tb);
/*   0*/      } 
/*1203*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1207*/      return tb.process(t, InBody);
/*   0*/    }
/*   0*/    
/*   0*/    private void closeCell(HtmlTreeBuilder tb) {
/*1211*/      if (tb.inTableScope("td")) {
/*1212*/        tb.processEndTag("td");
/*   0*/      } else {
/*1214*/        tb.processEndTag("th");
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1217*/  InSelect {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*1219*/      switch (t.type) {
/*   0*/        case Character:
/*1221*/          c = t.asCharacter();
/*1222*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/*1223*/            tb.error(this);
/*1224*/            return false;
/*   0*/          } 
/*1226*/          tb.insert(c);
/*   0*/          break;
/*   0*/        case Comment:
/*1230*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/*1233*/          tb.error(this);
/*1234*/          return false;
/*   0*/        case StartTag:
/*1236*/          start = t.asStartTag();
/*1237*/          name = start.normalName();
/*1238*/          if (name.equals("html")) {
/*1239*/              return tb.process(start, InBody); 
/*   0*/             }
/*1240*/          if (name.equals("option")) {
/*1241*/            if (tb.currentElement().nodeName().equals("option")) {
/*1242*/                tb.processEndTag("option"); 
/*   0*/               }
/*1243*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1244*/          if (name.equals("optgroup")) {
/*1245*/            if (tb.currentElement().nodeName().equals("option")) {
/*1246*/              tb.processEndTag("option");
/*1247*/            } else if (tb.currentElement().nodeName().equals("optgroup")) {
/*1248*/              tb.processEndTag("optgroup");
/*   0*/            } 
/*1249*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1250*/          if (name.equals("select")) {
/*1251*/            tb.error(this);
/*1252*/            return tb.processEndTag("select");
/*   0*/          } 
/*1253*/          if (StringUtil.in(name, new String[] { "input", "keygen", "textarea" })) {
/*1254*/            tb.error(this);
/*1255*/            if (!tb.inSelectScope("select")) {
/*1256*/                return false; 
/*   0*/               }
/*1257*/            tb.processEndTag("select");
/*1258*/            return tb.process(start);
/*   0*/          } 
/*1259*/          if (name.equals("script")) {
/*1260*/              return tb.process(t, InHead); 
/*   0*/             }
/*1262*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1266*/          end = t.asEndTag();
/*1267*/          name = end.normalName();
/*1268*/          switch (name) {
/*   0*/            case "optgroup":
/*1270*/              if (tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
/*1271*/                  tb.processEndTag("option"); 
/*   0*/                 }
/*1272*/              if (tb.currentElement().nodeName().equals("optgroup")) {
/*1273*/                tb.pop();
/*   0*/                break;
/*   0*/              } 
/*1275*/              tb.error(this);
/*   0*/              break;
/*   0*/            case "option":
/*1278*/              if (tb.currentElement().nodeName().equals("option")) {
/*1279*/                tb.pop();
/*   0*/                break;
/*   0*/              } 
/*1281*/              tb.error(this);
/*   0*/              break;
/*   0*/            case "select":
/*1284*/              if (!tb.inSelectScope(name)) {
/*1285*/                tb.error(this);
/*1286*/                return false;
/*   0*/              } 
/*1288*/              tb.popStackToClose(name);
/*1289*/              tb.resetInsertionMode();
/*   0*/              break;
/*   0*/          } 
/*1293*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1297*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1298*/              tb.error(this); 
/*   0*/             }
/*   0*/          break;
/*   0*/        default:
/*1301*/          return anythingElse(t, tb);
/*   0*/      } 
/*1303*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1307*/      tb.error(this);
/*1308*/      return false;
/*   0*/    }
/*   0*/  },
/*1311*/  InSelectInTable {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1313*/      if (t.isStartTag() && StringUtil.in(t.asStartTag().normalName(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1314*/        tb.error(this);
/*1315*/        tb.processEndTag("select");
/*1316*/        return tb.process(t);
/*   0*/      } 
/*1317*/      if (t.isEndTag() && StringUtil.in(t.asEndTag().normalName(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1318*/        tb.error(this);
/*1319*/        if (tb.inTableScope(t.asEndTag().normalName())) {
/*1320*/          tb.processEndTag("select");
/*1321*/          return tb.process(t);
/*   0*/        } 
/*1323*/        return false;
/*   0*/      } 
/*1325*/      return tb.process(t, InSelect);
/*   0*/    }
/*   0*/  },
/*1329*/  AfterBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1331*/      if (isWhitespace(t)) {
/*1332*/          return tb.process(t, InBody); 
/*   0*/         }
/*1333*/      if (t.isComment()) {
/*1334*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1335*/        if (t.isDoctype()) {
/*1336*/          tb.error(this);
/*1337*/          return false;
/*   0*/        } 
/*1338*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*1339*/            return tb.process(t, InBody); 
/*   0*/           }
/*1340*/        if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
/*1341*/          if (tb.isFragmentParsing()) {
/*1342*/            tb.error(this);
/*1343*/            return false;
/*   0*/          } 
/*1345*/          tb.transition(AfterAfterBody);
/*1347*/        } else if (!t.isEOF()) {
/*1350*/          tb.error(this);
/*1351*/          tb.transition(InBody);
/*1352*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1354*/      return true;
/*   0*/    }
/*   0*/  },
/*1357*/  InFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1359*/      if (isWhitespace(t)) {
/*1360*/        tb.insert(t.asCharacter());
/*1361*/      } else if (t.isComment()) {
/*1362*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1363*/        if (t.isDoctype()) {
/*1364*/          tb.error(this);
/*1365*/          return false;
/*   0*/        } 
/*1366*/        if (t.isStartTag()) {
/*1367*/          Token.StartTag start = t.asStartTag();
/*1368*/          switch (start.normalName()) {
/*   0*/            case "html":
/*1370*/              return tb.process(start, InBody);
/*   0*/            case "frameset":
/*1372*/              tb.insert(start);
/*   0*/              break;
/*   0*/            case "frame":
/*1375*/              tb.insertEmpty(start);
/*   0*/              break;
/*   0*/            case "noframes":
/*1378*/              return tb.process(start, InHead);
/*   0*/            default:
/*1380*/              tb.error(this);
/*1381*/              return false;
/*   0*/          } 
/*1383*/        } else if (t.isEndTag() && t.asEndTag().normalName().equals("frameset")) {
/*1384*/          if (tb.currentElement().nodeName().equals("html")) {
/*1385*/            tb.error(this);
/*1386*/            return false;
/*   0*/          } 
/*1388*/          tb.pop();
/*1389*/          if (!tb.isFragmentParsing() && !tb.currentElement().nodeName().equals("frameset")) {
/*1390*/              tb.transition(AfterFrameset); 
/*   0*/             }
/*1393*/        } else if (t.isEOF()) {
/*1394*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1395*/            tb.error(this);
/*1396*/            return true;
/*   0*/          } 
/*   0*/        } else {
/*1399*/          tb.error(this);
/*1400*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1402*/      return true;
/*   0*/    }
/*   0*/  },
/*1405*/  AfterFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1407*/      if (isWhitespace(t)) {
/*1408*/        tb.insert(t.asCharacter());
/*1409*/      } else if (t.isComment()) {
/*1410*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1411*/        if (t.isDoctype()) {
/*1412*/          tb.error(this);
/*1413*/          return false;
/*   0*/        } 
/*1414*/        if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
/*1415*/            return tb.process(t, InBody); 
/*   0*/           }
/*1416*/        if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
/*1417*/          tb.transition(AfterAfterFrameset);
/*   0*/        } else {
/*1418*/          if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
/*1419*/              return tb.process(t, InHead); 
/*   0*/             }
/*1420*/          if (!t.isEOF()) {
/*1423*/            tb.error(this);
/*1424*/            return false;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1426*/      return true;
/*   0*/    }
/*   0*/  },
/*1429*/  AfterAfterBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1431*/      if (t.isComment()) {
/*1432*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1433*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
/*1434*/            return tb.process(t, InBody); 
/*   0*/           }
/*1435*/        if (!t.isEOF()) {
/*1438*/          tb.error(this);
/*1439*/          tb.transition(InBody);
/*1440*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1442*/      return true;
/*   0*/    }
/*   0*/  },
/*1445*/  AfterAfterFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1447*/      if (t.isComment()) {
/*1448*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1449*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
/*1450*/            return tb.process(t, InBody); 
/*   0*/           }
/*1451*/        if (!t.isEOF()) {
/*1453*/          if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
/*1454*/              return tb.process(t, InHead); 
/*   0*/             }
/*1456*/          tb.error(this);
/*1457*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1459*/      return true;
/*   0*/    }
/*   0*/  },
/*1462*/  ForeignContent {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1464*/      return true;
/*   0*/    }
/*   0*/  };
/*   0*/  
/*1469*/  private static String nullString = String.valueOf(Character.MIN_VALUE);
/*   0*/  
/*   0*/  private static boolean isWhitespace(Token t) {
/*1474*/    if (t.isCharacter()) {
/*1475*/      String data = t.asCharacter().getData();
/*1476*/      return isWhitespace(data);
/*   0*/    } 
/*1478*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isWhitespace(String data) {
/*1483*/    for (int i = 0; i < data.length(); i++) {
/*1484*/      char c = data.charAt(i);
/*1485*/      if (!StringUtil.isWhitespace(c)) {
/*1486*/          return false; 
/*   0*/         }
/*   0*/    } 
/*1488*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
/*1492*/    tb.tokeniser.transition(TokeniserState.Rcdata);
/*1493*/    tb.markInsertionMode();
/*1494*/    tb.transition(Text);
/*1495*/    tb.insert(startTag);
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
/*1499*/    tb.tokeniser.transition(TokeniserState.Rawtext);
/*1500*/    tb.markInsertionMode();
/*1501*/    tb.transition(Text);
/*1502*/    tb.insert(startTag);
/*   0*/  }
/*   0*/  
/*   0*/  abstract boolean process(Token paramToken, HtmlTreeBuilder paramHtmlTreeBuilder);
/*   0*/  
/*   0*/  static final class Constants {
/*1509*/    static final String[] InBodyStartToHead = new String[] { "base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title" };
/*   0*/    
/*1510*/    static final String[] InBodyStartPClosers = new String[] { 
/*1510*/        "address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", 
/*1510*/        "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", 
/*1510*/        "summary", "ul" };
/*   0*/    
/*1513*/    static final String[] Headings = new String[] { "h1", "h2", "h3", "h4", "h5", "h6" };
/*   0*/    
/*1514*/    static final String[] InBodyStartPreListing = new String[] { "listing", "pre" };
/*   0*/    
/*1515*/    static final String[] InBodyStartLiBreakers = new String[] { "address", "div", "p" };
/*   0*/    
/*1516*/    static final String[] DdDt = new String[] { "dd", "dt" };
/*   0*/    
/*1517*/    static final String[] Formatters = new String[] { 
/*1517*/        "b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", 
/*1517*/        "tt", "u" };
/*   0*/    
/*1518*/    static final String[] InBodyStartApplets = new String[] { "applet", "marquee", "object" };
/*   0*/    
/*1519*/    static final String[] InBodyStartEmptyFormatters = new String[] { "area", "br", "embed", "img", "keygen", "wbr" };
/*   0*/    
/*1520*/    static final String[] InBodyStartMedia = new String[] { "param", "source", "track" };
/*   0*/    
/*1521*/    static final String[] InBodyStartInputAttribs = new String[] { "action", "name", "prompt" };
/*   0*/    
/*1522*/    static final String[] InBodyStartOptions = new String[] { "optgroup", "option" };
/*   0*/    
/*1523*/    static final String[] InBodyStartRuby = new String[] { "rp", "rt" };
/*   0*/    
/*1524*/    static final String[] InBodyStartDrop = new String[] { 
/*1524*/        "caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", 
/*1524*/        "tr" };
/*   0*/    
/*1525*/    static final String[] InBodyEndClosers = new String[] { 
/*1525*/        "address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", 
/*1525*/        "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", 
/*1525*/        "pre", "section", "summary", "ul" };
/*   0*/    
/*1528*/    static final String[] InBodyEndAdoptionFormatters = new String[] { 
/*1528*/        "a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", 
/*1528*/        "strike", "strong", "tt", "u" };
/*   0*/    
/*1529*/    static final String[] InBodyEndTableFosters = new String[] { "table", "tbody", "tfoot", "thead", "tr" };
/*   0*/  }
/*   0*/}
