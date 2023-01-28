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
/*  22*/        DocumentType doctype = new DocumentType(d.getName(), d.getPublicIdentifier(), d.getSystemIdentifier(), tb.getBaseUri());
/*  23*/        tb.getDocument().appendChild(doctype);
/*  24*/        if (d.isForceQuirks()) {
/*  25*/            tb.getDocument().quirksMode(Document.QuirksMode.quirks); 
/*   0*/           }
/*  26*/        tb.transition(BeforeHtml);
/*   0*/      } else {
/*  29*/        tb.transition(BeforeHtml);
/*  30*/        return tb.process(t);
/*   0*/      } 
/*  32*/      return true;
/*   0*/    }
/*   0*/  },
/*  35*/  BeforeHtml {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  37*/      if (t.isDoctype()) {
/*  38*/        tb.error(this);
/*  39*/        return false;
/*   0*/      } 
/*  40*/      if (t.isComment()) {
/*  41*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  42*/        if (isWhitespace(t)) {
/*  43*/            return true; 
/*   0*/           }
/*  44*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*  45*/          tb.insert(t.asStartTag());
/*  46*/          tb.transition(BeforeHead);
/*   0*/        } else {
/*  47*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "head", "body", "html", "br" })) {
/*  48*/              return anythingElse(t, tb); 
/*   0*/             }
/*  49*/          if (t.isEndTag()) {
/*  50*/            tb.error(this);
/*  51*/            return false;
/*   0*/          } 
/*  53*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/*  55*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*  59*/      tb.insertStartTag("html");
/*  60*/      tb.transition(BeforeHead);
/*  61*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/*  64*/  BeforeHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*  66*/      if (isWhitespace(t)) {
/*  67*/          return true; 
/*   0*/         }
/*  68*/      if (t.isComment()) {
/*  69*/        tb.insert(t.asComment());
/*   0*/      } else {
/*  70*/        if (t.isDoctype()) {
/*  71*/          tb.error(this);
/*  72*/          return false;
/*   0*/        } 
/*  73*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*  74*/            return InBody.process(t, tb); 
/*   0*/           }
/*  75*/        if (t.isStartTag() && t.asStartTag().name().equals("head")) {
/*  76*/          Element head = tb.insert(t.asStartTag());
/*  77*/          tb.setHeadElement(head);
/*  78*/          tb.transition(InHead);
/*   0*/        } else {
/*  79*/          if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "head", "body", "html", "br" })) {
/*  80*/            tb.processStartTag("head");
/*  81*/            return tb.process(t);
/*   0*/          } 
/*  82*/          if (t.isEndTag()) {
/*  83*/            tb.error(this);
/*  84*/            return false;
/*   0*/          } 
/*  86*/          tb.processStartTag("head");
/*  87*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*  89*/      return true;
/*   0*/    }
/*   0*/  },
/*  92*/  InHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*  94*/      if (isWhitespace(t)) {
/*  95*/        tb.insert(t.asCharacter());
/*  96*/        return true;
/*   0*/      } 
/*  98*/      switch (t.type) {
/*   0*/        case Comment:
/* 100*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 103*/          tb.error(this);
/* 104*/          return false;
/*   0*/        case StartTag:
/* 106*/          start = t.asStartTag();
/* 107*/          name = start.name();
/* 108*/          if (name.equals("html")) {
/* 109*/              return InBody.process(t, tb); 
/*   0*/             }
/* 110*/          if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "command", "link" })) {
/* 111*/            Element el = tb.insertEmpty(start);
/* 113*/            if (name.equals("base") && el.hasAttr("href")) {
/* 114*/                tb.maybeSetBaseUri(el); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 115*/          if (name.equals("meta")) {
/* 116*/            Element element = tb.insertEmpty(start);
/*   0*/            break;
/*   0*/          } 
/* 118*/          if (name.equals("title")) {
/* 119*/            handleRcData(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 120*/          if (StringUtil.in(name, new String[] { "noframes", "style" })) {
/* 121*/            handleRawtext(start, tb);
/*   0*/            break;
/*   0*/          } 
/* 122*/          if (name.equals("noscript")) {
/* 124*/            tb.insert(start);
/* 125*/            tb.transition(InHeadNoscript);
/*   0*/            break;
/*   0*/          } 
/* 126*/          if (name.equals("script")) {
/* 129*/            tb.tokeniser.transition(TokeniserState.ScriptData);
/* 130*/            tb.markInsertionMode();
/* 131*/            tb.transition(Text);
/* 132*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/* 133*/          if (name.equals("head")) {
/* 134*/            tb.error(this);
/* 135*/            return false;
/*   0*/          } 
/* 137*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 141*/          end = t.asEndTag();
/* 142*/          name = end.name();
/* 143*/          if (name.equals("head")) {
/* 144*/            tb.pop();
/* 145*/            tb.transition(AfterHead);
/*   0*/            break;
/*   0*/          } 
/* 146*/          if (StringUtil.in(name, new String[] { "body", "html", "br" })) {
/* 147*/              return anythingElse(t, tb); 
/*   0*/             }
/* 149*/          tb.error(this);
/* 150*/          return false;
/*   0*/        default:
/* 154*/          return anythingElse(t, tb);
/*   0*/      } 
/* 156*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/* 160*/      tb.processEndTag("head");
/* 161*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 164*/  InHeadNoscript {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 166*/      if (t.isDoctype()) {
/* 167*/        tb.error(this);
/*   0*/      } else {
/* 168*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/* 169*/            return tb.process(t, InBody); 
/*   0*/           }
/* 170*/        if (t.isEndTag() && t.asEndTag().name().equals("noscript")) {
/* 171*/          tb.pop();
/* 172*/          tb.transition(InHead);
/*   0*/        } else {
/* 173*/          if (isWhitespace(t) || t.isComment() || (t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "basefont", "bgsound", "link", "meta", "noframes", "style" }))) {
/* 175*/              return tb.process(t, InHead); 
/*   0*/             }
/* 176*/          if (t.isEndTag() && t.asEndTag().name().equals("br")) {
/* 177*/              return anythingElse(t, tb); 
/*   0*/             }
/* 178*/          if ((t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "head", "noscript" })) || t.isEndTag()) {
/* 179*/            tb.error(this);
/* 180*/            return false;
/*   0*/          } 
/* 182*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } 
/* 184*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/* 188*/      tb.error(this);
/* 189*/      tb.insert(new Token.Character().data(t.toString()));
/* 190*/      return true;
/*   0*/    }
/*   0*/  },
/* 193*/  AfterHead {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 195*/      if (isWhitespace(t)) {
/* 196*/        tb.insert(t.asCharacter());
/* 197*/      } else if (t.isComment()) {
/* 198*/        tb.insert(t.asComment());
/* 199*/      } else if (t.isDoctype()) {
/* 200*/        tb.error(this);
/* 201*/      } else if (t.isStartTag()) {
/* 202*/        Token.StartTag startTag = t.asStartTag();
/* 203*/        String name = startTag.name();
/* 204*/        if (name.equals("html")) {
/* 205*/            return tb.process(t, InBody); 
/*   0*/           }
/* 206*/        if (name.equals("body")) {
/* 207*/          tb.insert(startTag);
/* 208*/          tb.framesetOk(false);
/* 209*/          tb.transition(InBody);
/* 210*/        } else if (name.equals("frameset")) {
/* 211*/          tb.insert(startTag);
/* 212*/          tb.transition(InFrameset);
/* 213*/        } else if (StringUtil.in(name, new String[] { "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "title" })) {
/* 214*/          tb.error(this);
/* 215*/          Element head = tb.getHeadElement();
/* 216*/          tb.push(head);
/* 217*/          tb.process(t, InHead);
/* 218*/          tb.removeFromStack(head);
/*   0*/        } else {
/* 219*/          if (name.equals("head")) {
/* 220*/            tb.error(this);
/* 221*/            return false;
/*   0*/          } 
/* 223*/          anythingElse(t, tb);
/*   0*/        } 
/* 225*/      } else if (t.isEndTag()) {
/* 226*/        if (StringUtil.in(t.asEndTag().name(), new String[] { "body", "html" })) {
/* 227*/          anythingElse(t, tb);
/*   0*/        } else {
/* 229*/          tb.error(this);
/* 230*/          return false;
/*   0*/        } 
/*   0*/      } else {
/* 233*/        anythingElse(t, tb);
/*   0*/      } 
/* 235*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/* 239*/      tb.processStartTag("body");
/* 240*/      tb.framesetOk(true);
/* 241*/      return tb.process(t);
/*   0*/    }
/*   0*/  },
/* 244*/  InBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/* 246*/      switch (t.type) {
/*   0*/        case Character:
/* 248*/          c = t.asCharacter();
/* 249*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/* 251*/            tb.error(this);
/* 252*/            return false;
/*   0*/          } 
/* 253*/          if (tb.framesetOk() && isWhitespace(c)) {
/* 254*/            tb.reconstructFormattingElements();
/* 255*/            tb.insert(c);
/*   0*/            break;
/*   0*/          } 
/* 257*/          tb.reconstructFormattingElements();
/* 258*/          tb.insert(c);
/* 259*/          tb.framesetOk(false);
/*   0*/          break;
/*   0*/        case Comment:
/* 264*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 268*/          tb.error(this);
/* 269*/          return false;
/*   0*/        case StartTag:
/* 272*/          startTag = t.asStartTag();
/* 273*/          name = startTag.name();
/* 274*/          if (name.equals("html")) {
/* 275*/            tb.error(this);
/* 277*/            Element html = tb.getStack().get(0);
/* 278*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 279*/              if (!html.hasAttr(attribute.getKey())) {
/* 280*/                  html.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 282*/          if (StringUtil.in(name, Constants.InBodyStartToHead)) {
/* 283*/              return tb.process(t, InHead); 
/*   0*/             }
/* 284*/          if (name.equals("body")) {
/* 285*/            tb.error(this);
/* 286*/            ArrayList<Element> stack = tb.getStack();
/* 287*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 289*/                return false; 
/*   0*/               }
/* 291*/            tb.framesetOk(false);
/* 292*/            Element body = stack.get(1);
/* 293*/            for (Attribute attribute : (Iterable<Attribute>)startTag.getAttributes()) {
/* 294*/              if (!body.hasAttr(attribute.getKey())) {
/* 295*/                  body.attributes().put(attribute); 
/*   0*/                 }
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 298*/          if (name.equals("frameset")) {
/* 299*/            tb.error(this);
/* 300*/            ArrayList<Element> stack = tb.getStack();
/* 301*/            if (stack.size() == 1 || (stack.size() > 2 && !((Element)stack.get(1)).nodeName().equals("body"))) {
/* 303*/                return false; 
/*   0*/               }
/* 304*/            if (!tb.framesetOk()) {
/* 305*/                return false; 
/*   0*/               }
/* 307*/            Element second = stack.get(1);
/* 308*/            if (second.parent() != null) {
/* 309*/                second.remove(); 
/*   0*/               }
/* 311*/            while (stack.size() > 1) {
/* 312*/                stack.remove(stack.size() - 1); 
/*   0*/               }
/* 313*/            tb.insert(startTag);
/* 314*/            tb.transition(InFrameset);
/*   0*/            break;
/*   0*/          } 
/* 316*/          if (StringUtil.in(name, Constants.InBodyStartPClosers)) {
/* 317*/            if (tb.inButtonScope("p")) {
/* 318*/                tb.processEndTag("p"); 
/*   0*/               }
/* 320*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 321*/          if (StringUtil.in(name, Constants.Headings)) {
/* 322*/            if (tb.inButtonScope("p")) {
/* 323*/                tb.processEndTag("p"); 
/*   0*/               }
/* 325*/            if (StringUtil.in(tb.currentElement().nodeName(), Constants.Headings)) {
/* 326*/              tb.error(this);
/* 327*/              tb.pop();
/*   0*/            } 
/* 329*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 330*/          if (StringUtil.in(name, Constants.InBodyStartPreListing)) {
/* 331*/            if (tb.inButtonScope("p")) {
/* 332*/                tb.processEndTag("p"); 
/*   0*/               }
/* 334*/            tb.insert(startTag);
/* 336*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 337*/          if (name.equals("form")) {
/* 338*/            if (tb.getFormElement() != null) {
/* 339*/              tb.error(this);
/* 340*/              return false;
/*   0*/            } 
/* 342*/            if (tb.inButtonScope("p")) {
/* 343*/                tb.processEndTag("p"); 
/*   0*/               }
/* 345*/            tb.insertForm(startTag, true);
/*   0*/            break;
/*   0*/          } 
/* 346*/          if (name.equals("li")) {
/* 347*/            tb.framesetOk(false);
/* 348*/            ArrayList<Element> stack = tb.getStack();
/* 349*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 350*/              Element el = stack.get(i);
/* 351*/              if (el.nodeName().equals("li")) {
/* 352*/                tb.processEndTag("li");
/*   0*/                break;
/*   0*/              } 
/* 355*/              if (tb.isSpecial(el) && !StringUtil.in(el.nodeName(), Constants.InBodyStartLiBreakers)) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 358*/            if (tb.inButtonScope("p")) {
/* 359*/                tb.processEndTag("p"); 
/*   0*/               }
/* 361*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 362*/          if (StringUtil.in(name, Constants.DdDt)) {
/* 363*/            tb.framesetOk(false);
/* 364*/            ArrayList<Element> stack = tb.getStack();
/* 365*/            for (int i = stack.size() - 1; i > 0; i--) {
/* 366*/              Element el = stack.get(i);
/* 367*/              if (StringUtil.in(el.nodeName(), Constants.DdDt)) {
/* 368*/                tb.processEndTag(el.nodeName());
/*   0*/                break;
/*   0*/              } 
/* 371*/              if (tb.isSpecial(el) && !StringUtil.in(el.nodeName(), Constants.InBodyStartLiBreakers)) {
/*   0*/                  break; 
/*   0*/                 }
/*   0*/            } 
/* 374*/            if (tb.inButtonScope("p")) {
/* 375*/                tb.processEndTag("p"); 
/*   0*/               }
/* 377*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 378*/          if (name.equals("plaintext")) {
/* 379*/            if (tb.inButtonScope("p")) {
/* 380*/                tb.processEndTag("p"); 
/*   0*/               }
/* 382*/            tb.insert(startTag);
/* 383*/            tb.tokeniser.transition(TokeniserState.PLAINTEXT);
/*   0*/            break;
/*   0*/          } 
/* 384*/          if (name.equals("button")) {
/* 385*/            if (tb.inButtonScope("button")) {
/* 387*/              tb.error(this);
/* 388*/              tb.processEndTag("button");
/* 389*/              tb.process(startTag);
/*   0*/              break;
/*   0*/            } 
/* 391*/            tb.reconstructFormattingElements();
/* 392*/            tb.insert(startTag);
/* 393*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 395*/          if (name.equals("a")) {
/* 396*/            if (tb.getActiveFormattingElement("a") != null) {
/* 397*/              tb.error(this);
/* 398*/              tb.processEndTag("a");
/* 401*/              Element remainingA = tb.getFromStack("a");
/* 402*/              if (remainingA != null) {
/* 403*/                tb.removeFromActiveFormattingElements(remainingA);
/* 404*/                tb.removeFromStack(remainingA);
/*   0*/              } 
/*   0*/            } 
/* 407*/            tb.reconstructFormattingElements();
/* 408*/            Element a = tb.insert(startTag);
/* 409*/            tb.pushActiveFormattingElements(a);
/*   0*/            break;
/*   0*/          } 
/* 410*/          if (StringUtil.in(name, Constants.Formatters)) {
/* 411*/            tb.reconstructFormattingElements();
/* 412*/            Element el = tb.insert(startTag);
/* 413*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 414*/          if (name.equals("nobr")) {
/* 415*/            tb.reconstructFormattingElements();
/* 416*/            if (tb.inScope("nobr")) {
/* 417*/              tb.error(this);
/* 418*/              tb.processEndTag("nobr");
/* 419*/              tb.reconstructFormattingElements();
/*   0*/            } 
/* 421*/            Element el = tb.insert(startTag);
/* 422*/            tb.pushActiveFormattingElements(el);
/*   0*/            break;
/*   0*/          } 
/* 423*/          if (StringUtil.in(name, Constants.InBodyStartApplets)) {
/* 424*/            tb.reconstructFormattingElements();
/* 425*/            tb.insert(startTag);
/* 426*/            tb.insertMarkerToFormattingElements();
/* 427*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 428*/          if (name.equals("table")) {
/* 429*/            if (tb.getDocument().quirksMode() != Document.QuirksMode.quirks && tb.inButtonScope("p")) {
/* 430*/                tb.processEndTag("p"); 
/*   0*/               }
/* 432*/            tb.insert(startTag);
/* 433*/            tb.framesetOk(false);
/* 434*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/* 435*/          if (StringUtil.in(name, Constants.InBodyStartEmptyFormatters)) {
/* 436*/            tb.reconstructFormattingElements();
/* 437*/            tb.insertEmpty(startTag);
/* 438*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 439*/          if (name.equals("input")) {
/* 440*/            tb.reconstructFormattingElements();
/* 441*/            Element el = tb.insertEmpty(startTag);
/* 442*/            if (!el.attr("type").equalsIgnoreCase("hidden")) {
/* 443*/                tb.framesetOk(false); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 444*/          if (StringUtil.in(name, Constants.InBodyStartMedia)) {
/* 445*/            tb.insertEmpty(startTag);
/*   0*/            break;
/*   0*/          } 
/* 446*/          if (name.equals("hr")) {
/* 447*/            if (tb.inButtonScope("p")) {
/* 448*/                tb.processEndTag("p"); 
/*   0*/               }
/* 450*/            tb.insertEmpty(startTag);
/* 451*/            tb.framesetOk(false);
/*   0*/            break;
/*   0*/          } 
/* 452*/          if (name.equals("image")) {
/* 453*/            if (tb.getFromStack("svg") == null) {
/* 454*/                return tb.process(startTag.name("img")); 
/*   0*/               }
/* 456*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 457*/          if (name.equals("isindex")) {
/* 459*/            tb.error(this);
/* 460*/            if (tb.getFormElement() != null) {
/* 461*/                return false; 
/*   0*/               }
/* 463*/            tb.tokeniser.acknowledgeSelfClosingFlag();
/* 464*/            tb.processStartTag("form");
/* 465*/            if (startTag.attributes.hasKey("action")) {
/* 466*/              Element form = tb.getFormElement();
/* 467*/              form.attr("action", startTag.attributes.get("action"));
/*   0*/            } 
/* 469*/            tb.processStartTag("hr");
/* 470*/            tb.processStartTag("label");
/* 472*/            String prompt = startTag.attributes.hasKey("prompt") ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: ";
/* 476*/            tb.process(new Token.Character().data(prompt));
/* 479*/            Attributes inputAttribs = new Attributes();
/* 480*/            for (Attribute attr : (Iterable<Attribute>)startTag.attributes) {
/* 481*/              if (!StringUtil.in(attr.getKey(), Constants.InBodyStartInputAttribs)) {
/* 482*/                  inputAttribs.put(attr); 
/*   0*/                 }
/*   0*/            } 
/* 484*/            inputAttribs.put("name", "isindex");
/* 485*/            tb.processStartTag("input", inputAttribs);
/* 486*/            tb.processEndTag("label");
/* 487*/            tb.processStartTag("hr");
/* 488*/            tb.processEndTag("form");
/*   0*/            break;
/*   0*/          } 
/* 489*/          if (name.equals("textarea")) {
/* 490*/            tb.insert(startTag);
/* 492*/            tb.tokeniser.transition(TokeniserState.Rcdata);
/* 493*/            tb.markInsertionMode();
/* 494*/            tb.framesetOk(false);
/* 495*/            tb.transition(Text);
/*   0*/            break;
/*   0*/          } 
/* 496*/          if (name.equals("xmp")) {
/* 497*/            if (tb.inButtonScope("p")) {
/* 498*/                tb.processEndTag("p"); 
/*   0*/               }
/* 500*/            tb.reconstructFormattingElements();
/* 501*/            tb.framesetOk(false);
/* 502*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 503*/          if (name.equals("iframe")) {
/* 504*/            tb.framesetOk(false);
/* 505*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 506*/          if (name.equals("noembed")) {
/* 508*/            handleRawtext(startTag, tb);
/*   0*/            break;
/*   0*/          } 
/* 509*/          if (name.equals("select")) {
/* 510*/            tb.reconstructFormattingElements();
/* 511*/            tb.insert(startTag);
/* 512*/            tb.framesetOk(false);
/* 514*/            HtmlTreeBuilderState state = tb.state();
/* 515*/            if (state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow) || state.equals(InCell)) {
/* 516*/              tb.transition(InSelectInTable);
/*   0*/              break;
/*   0*/            } 
/* 518*/            tb.transition(InSelect);
/*   0*/            break;
/*   0*/          } 
/* 519*/          if (StringUtil.in(name, Constants.InBodyStartOptions)) {
/* 520*/            if (tb.currentElement().nodeName().equals("option")) {
/* 521*/                tb.processEndTag("option"); 
/*   0*/               }
/* 522*/            tb.reconstructFormattingElements();
/* 523*/            tb.insert(startTag);
/*   0*/            break;
/*   0*/          } 
/* 524*/          if (StringUtil.in(name, Constants.InBodyStartRuby)) {
/* 525*/            if (tb.inScope("ruby")) {
/* 526*/              tb.generateImpliedEndTags();
/* 527*/              if (!tb.currentElement().nodeName().equals("ruby")) {
/* 528*/                tb.error(this);
/* 529*/                tb.popStackToBefore("ruby");
/*   0*/              } 
/* 531*/              tb.insert(startTag);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 533*/          if (name.equals("math")) {
/* 534*/            tb.reconstructFormattingElements();
/* 536*/            tb.insert(startTag);
/* 537*/            tb.tokeniser.acknowledgeSelfClosingFlag();
/*   0*/            break;
/*   0*/          } 
/* 538*/          if (name.equals("svg")) {
/* 539*/            tb.reconstructFormattingElements();
/* 541*/            tb.insert(startTag);
/* 542*/            tb.tokeniser.acknowledgeSelfClosingFlag();
/*   0*/            break;
/*   0*/          } 
/* 543*/          if (StringUtil.in(name, Constants.InBodyStartDrop)) {
/* 544*/            tb.error(this);
/* 545*/            return false;
/*   0*/          } 
/* 547*/          tb.reconstructFormattingElements();
/* 548*/          tb.insert(startTag);
/*   0*/          break;
/*   0*/        case EndTag:
/* 553*/          endTag = t.asEndTag();
/* 554*/          name = endTag.name();
/* 555*/          if (name.equals("body")) {
/* 556*/            if (!tb.inScope("body")) {
/* 557*/              tb.error(this);
/* 558*/              return false;
/*   0*/            } 
/* 561*/            tb.transition(AfterBody);
/*   0*/            break;
/*   0*/          } 
/* 563*/          if (name.equals("html")) {
/* 564*/            boolean notIgnored = tb.processEndTag("body");
/* 565*/            if (notIgnored) {
/* 566*/                return tb.process(endTag); 
/*   0*/               }
/*   0*/            break;
/*   0*/          } 
/* 567*/          if (StringUtil.in(name, Constants.InBodyEndClosers)) {
/* 568*/            if (!tb.inScope(name)) {
/* 570*/              tb.error(this);
/* 571*/              return false;
/*   0*/            } 
/* 573*/            tb.generateImpliedEndTags();
/* 574*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 575*/                tb.error(this); 
/*   0*/               }
/* 576*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 578*/          if (name.equals("form")) {
/* 579*/            Element currentForm = tb.getFormElement();
/* 580*/            tb.setFormElement(null);
/* 581*/            if (currentForm == null || !tb.inScope(name)) {
/* 582*/              tb.error(this);
/* 583*/              return false;
/*   0*/            } 
/* 585*/            tb.generateImpliedEndTags();
/* 586*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 587*/                tb.error(this); 
/*   0*/               }
/* 589*/            tb.removeFromStack(currentForm);
/*   0*/            break;
/*   0*/          } 
/* 591*/          if (name.equals("p")) {
/* 592*/            if (!tb.inButtonScope(name)) {
/* 593*/              tb.error(this);
/* 594*/              tb.processStartTag(name);
/* 595*/              return tb.process(endTag);
/*   0*/            } 
/* 597*/            tb.generateImpliedEndTags(name);
/* 598*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 599*/                tb.error(this); 
/*   0*/               }
/* 600*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 602*/          if (name.equals("li")) {
/* 603*/            if (!tb.inListItemScope(name)) {
/* 604*/              tb.error(this);
/* 605*/              return false;
/*   0*/            } 
/* 607*/            tb.generateImpliedEndTags(name);
/* 608*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 609*/                tb.error(this); 
/*   0*/               }
/* 610*/            tb.popStackToClose(name);
/*   0*/            break;
/*   0*/          } 
/* 612*/          if (StringUtil.in(name, Constants.DdDt)) {
/* 613*/            if (!tb.inScope(name)) {
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
/* 622*/          if (StringUtil.in(name, Constants.Headings)) {
/* 623*/            if (!tb.inScope(Constants.Headings)) {
/* 624*/              tb.error(this);
/* 625*/              return false;
/*   0*/            } 
/* 627*/            tb.generateImpliedEndTags(name);
/* 628*/            if (!tb.currentElement().nodeName().equals(name)) {
/* 629*/                tb.error(this); 
/*   0*/               }
/* 630*/            tb.popStackToClose(Constants.Headings);
/*   0*/            break;
/*   0*/          } 
/* 632*/          if (name.equals("sarcasm")) {
/* 634*/              return anyOtherEndTag(t, tb); 
/*   0*/             }
/* 635*/          if (StringUtil.in(name, Constants.InBodyEndAdoptionFormatters)) {
/* 637*/            for (int i = 0; i < 8; i++) {
/* 638*/              Element formatEl = tb.getActiveFormattingElement(name);
/* 639*/              if (formatEl == null) {
/* 640*/                  return anyOtherEndTag(t, tb); 
/*   0*/                 }
/* 641*/              if (!tb.onStack(formatEl)) {
/* 642*/                tb.error(this);
/* 643*/                tb.removeFromActiveFormattingElements(formatEl);
/* 644*/                return true;
/*   0*/              } 
/* 645*/              if (!tb.inScope(formatEl.nodeName())) {
/* 646*/                tb.error(this);
/* 647*/                return false;
/*   0*/              } 
/* 648*/              if (tb.currentElement() != formatEl) {
/* 649*/                  tb.error(this); 
/*   0*/                 }
/* 651*/              Element furthestBlock = null;
/* 652*/              Element commonAncestor = null;
/*   0*/              boolean seenFormattingElement = false;
/* 654*/              ArrayList<Element> stack = tb.getStack();
/* 657*/              int stackSize = stack.size();
/* 658*/              for (int si = 0; si < stackSize && si < 64; si++) {
/* 659*/                Element el = stack.get(si);
/* 660*/                if (el == formatEl) {
/* 661*/                  commonAncestor = stack.get(si - 1);
/* 662*/                  seenFormattingElement = true;
/* 663*/                } else if (seenFormattingElement && tb.isSpecial(el)) {
/* 664*/                  furthestBlock = el;
/*   0*/                  break;
/*   0*/                } 
/*   0*/              } 
/* 668*/              if (furthestBlock == null) {
/* 669*/                tb.popStackToClose(formatEl.nodeName());
/* 670*/                tb.removeFromActiveFormattingElements(formatEl);
/* 671*/                return true;
/*   0*/              } 
/* 676*/              Element node = furthestBlock;
/* 677*/              Element lastNode = furthestBlock;
/* 678*/              for (int j = 0; j < 3; j++) {
/* 679*/                if (tb.onStack(node)) {
/* 680*/                    node = tb.aboveOnStack(node); 
/*   0*/                   }
/* 681*/                if (!tb.isInActiveFormattingElements(node)) {
/* 682*/                  tb.removeFromStack(node);
/*   0*/                } else {
/* 684*/                  if (node == formatEl) {
/*   0*/                      break; 
/*   0*/                     }
/* 687*/                  Element replacement = new Element(Tag.valueOf(node.nodeName()), tb.getBaseUri());
/* 688*/                  tb.replaceActiveFormattingElement(node, replacement);
/* 689*/                  tb.replaceOnStack(node, replacement);
/* 690*/                  node = replacement;
/* 692*/                  if (lastNode == furthestBlock);
/* 696*/                  if (lastNode.parent() != null) {
/* 697*/                      lastNode.remove(); 
/*   0*/                     }
/* 698*/                  node.appendChild(lastNode);
/* 700*/                  lastNode = node;
/*   0*/                } 
/*   0*/              } 
/* 703*/              if (StringUtil.in(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
/* 704*/                if (lastNode.parent() != null) {
/* 705*/                    lastNode.remove(); 
/*   0*/                   }
/* 706*/                tb.insertInFosterParent(lastNode);
/*   0*/              } else {
/* 708*/                if (lastNode.parent() != null) {
/* 709*/                    lastNode.remove(); 
/*   0*/                   }
/* 710*/                commonAncestor.appendChild(lastNode);
/*   0*/              } 
/* 713*/              Element adopter = new Element(formatEl.tag(), tb.getBaseUri());
/* 714*/              adopter.attributes().addAll(formatEl.attributes());
/* 715*/              Node[] childNodes = furthestBlock.childNodes().<Node>toArray(new Node[furthestBlock.childNodeSize()]);
/* 716*/              for (Node childNode : childNodes) {
/* 717*/                  adopter.appendChild(childNode); 
/*   0*/                 }
/* 719*/              furthestBlock.appendChild(adopter);
/* 720*/              tb.removeFromActiveFormattingElements(formatEl);
/* 722*/              tb.removeFromStack(formatEl);
/* 723*/              tb.insertOnStackAfter(furthestBlock, adopter);
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 725*/          if (StringUtil.in(name, Constants.InBodyStartApplets)) {
/* 726*/            if (!tb.inScope("name")) {
/* 727*/              if (!tb.inScope(name)) {
/* 728*/                tb.error(this);
/* 729*/                return false;
/*   0*/              } 
/* 731*/              tb.generateImpliedEndTags();
/* 732*/              if (!tb.currentElement().nodeName().equals(name)) {
/* 733*/                  tb.error(this); 
/*   0*/                 }
/* 734*/              tb.popStackToClose(name);
/* 735*/              tb.clearFormattingElementsToLastMarker();
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 737*/          if (name.equals("br")) {
/* 738*/            tb.error(this);
/* 739*/            tb.processStartTag("br");
/* 740*/            return false;
/*   0*/          } 
/* 742*/          return anyOtherEndTag(t, tb);
/*   0*/      } 
/* 751*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
/* 755*/      String name = t.asEndTag().name();
/* 756*/      ArrayList<Element> stack = tb.getStack();
/* 757*/      for (int pos = stack.size() - 1; pos >= 0; pos--) {
/* 758*/        Element node = stack.get(pos);
/* 759*/        if (node.nodeName().equals(name)) {
/* 760*/          tb.generateImpliedEndTags(name);
/* 761*/          if (!name.equals(tb.currentElement().nodeName())) {
/* 762*/              tb.error(this); 
/*   0*/             }
/* 763*/          tb.popStackToClose(name);
/*   0*/          break;
/*   0*/        } 
/* 766*/        if (tb.isSpecial(node)) {
/* 767*/          tb.error(this);
/* 768*/          return false;
/*   0*/        } 
/*   0*/      } 
/* 772*/      return true;
/*   0*/    }
/*   0*/  },
/* 775*/  Text {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 778*/      if (t.isCharacter()) {
/* 779*/        tb.insert(t.asCharacter());
/*   0*/      } else {
/* 780*/        if (t.isEOF()) {
/* 781*/          tb.error(this);
/* 783*/          tb.pop();
/* 784*/          tb.transition(tb.originalState());
/* 785*/          return tb.process(t);
/*   0*/        } 
/* 786*/        if (t.isEndTag()) {
/* 788*/          tb.pop();
/* 789*/          tb.transition(tb.originalState());
/*   0*/        } 
/*   0*/      } 
/* 791*/      return true;
/*   0*/    }
/*   0*/  },
/* 794*/  InTable {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 796*/      if (t.isCharacter()) {
/* 797*/        tb.newPendingTableCharacters();
/* 798*/        tb.markInsertionMode();
/* 799*/        tb.transition(InTableText);
/* 800*/        return tb.process(t);
/*   0*/      } 
/* 801*/      if (t.isComment()) {
/* 802*/        tb.insert(t.asComment());
/* 803*/        return true;
/*   0*/      } 
/* 804*/      if (t.isDoctype()) {
/* 805*/        tb.error(this);
/* 806*/        return false;
/*   0*/      } 
/* 807*/      if (t.isStartTag()) {
/* 808*/        Token.StartTag startTag = t.asStartTag();
/* 809*/        String name = startTag.name();
/* 810*/        if (name.equals("caption")) {
/* 811*/          tb.clearStackToTableContext();
/* 812*/          tb.insertMarkerToFormattingElements();
/* 813*/          tb.insert(startTag);
/* 814*/          tb.transition(InCaption);
/* 815*/        } else if (name.equals("colgroup")) {
/* 816*/          tb.clearStackToTableContext();
/* 817*/          tb.insert(startTag);
/* 818*/          tb.transition(InColumnGroup);
/*   0*/        } else {
/* 819*/          if (name.equals("col")) {
/* 820*/            tb.processStartTag("colgroup");
/* 821*/            return tb.process(t);
/*   0*/          } 
/* 822*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/* 823*/            tb.clearStackToTableContext();
/* 824*/            tb.insert(startTag);
/* 825*/            tb.transition(InTableBody);
/*   0*/          } else {
/* 826*/            if (StringUtil.in(name, new String[] { "td", "th", "tr" })) {
/* 827*/              tb.processStartTag("tbody");
/* 828*/              return tb.process(t);
/*   0*/            } 
/* 829*/            if (name.equals("table")) {
/* 830*/              tb.error(this);
/* 831*/              boolean processed = tb.processEndTag("table");
/* 832*/              if (processed) {
/* 833*/                  return tb.process(t); 
/*   0*/                 }
/*   0*/            } else {
/* 834*/              if (StringUtil.in(name, new String[] { "style", "script" })) {
/* 835*/                  return tb.process(t, InHead); 
/*   0*/                 }
/* 836*/              if (name.equals("input")) {
/* 837*/                if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
/* 838*/                    return anythingElse(t, tb); 
/*   0*/                   }
/* 840*/                tb.insertEmpty(startTag);
/* 842*/              } else if (name.equals("form")) {
/* 843*/                tb.error(this);
/* 844*/                if (tb.getFormElement() != null) {
/* 845*/                    return false; 
/*   0*/                   }
/* 847*/                tb.insertForm(startTag, false);
/*   0*/              } else {
/* 850*/                return anythingElse(t, tb);
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/* 852*/        return true;
/*   0*/      } 
/* 853*/      if (t.isEndTag()) {
/* 854*/        Token.EndTag endTag = t.asEndTag();
/* 855*/        String name = endTag.name();
/* 857*/        if (name.equals("table")) {
/* 858*/          if (!tb.inTableScope(name)) {
/* 859*/            tb.error(this);
/* 860*/            return false;
/*   0*/          } 
/* 862*/          tb.popStackToClose("table");
/* 864*/          tb.resetInsertionMode();
/*   0*/        } else {
/* 865*/          if (StringUtil.in(name, new String[] { 
/* 865*/                "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", 
/* 865*/                "tr" })) {
/* 867*/            tb.error(this);
/* 868*/            return false;
/*   0*/          } 
/* 870*/          return anythingElse(t, tb);
/*   0*/        } 
/* 872*/        return true;
/*   0*/      } 
/* 873*/      if (t.isEOF()) {
/* 874*/        if (tb.currentElement().nodeName().equals("html")) {
/* 875*/            tb.error(this); 
/*   0*/           }
/* 876*/        return true;
/*   0*/      } 
/* 878*/      return anythingElse(t, tb);
/*   0*/    }
/*   0*/    
/*   0*/    boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*   0*/      boolean processed;
/* 882*/      tb.error(this);
/* 884*/      if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 885*/        tb.setFosterInserts(true);
/* 886*/        processed = tb.process(t, InBody);
/* 887*/        tb.setFosterInserts(false);
/*   0*/      } else {
/* 889*/        processed = tb.process(t, InBody);
/*   0*/      } 
/* 891*/      return processed;
/*   0*/    }
/*   0*/  },
/* 894*/  InTableText {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/* 896*/      switch (t.type) {
/*   0*/        case Character:
/* 898*/          c = t.asCharacter();
/* 899*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/* 900*/            tb.error(this);
/* 901*/            return false;
/*   0*/          } 
/* 903*/          tb.getPendingTableCharacters().add(c.getData());
/*   0*/          break;
/*   0*/        default:
/* 908*/          if (tb.getPendingTableCharacters().size() > 0) {
/* 909*/            for (String character : tb.getPendingTableCharacters()) {
/* 910*/              if (!isWhitespace(character)) {
/* 912*/                tb.error(this);
/* 913*/                if (StringUtil.in(tb.currentElement().nodeName(), new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/* 914*/                  tb.setFosterInserts(true);
/* 915*/                  tb.process(new Token.Character().data(character), InBody);
/* 916*/                  tb.setFosterInserts(false);
/*   0*/                  continue;
/*   0*/                } 
/* 918*/                tb.process(new Token.Character().data(character), InBody);
/*   0*/                continue;
/*   0*/              } 
/* 921*/              tb.insert(new Token.Character().data(character));
/*   0*/            } 
/* 923*/            tb.newPendingTableCharacters();
/*   0*/          } 
/* 925*/          tb.transition(tb.originalState());
/* 926*/          return tb.process(t);
/*   0*/      } 
/* 928*/      return true;
/*   0*/    }
/*   0*/  },
/* 931*/  InCaption {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/* 933*/      if (t.isEndTag() && t.asEndTag().name().equals("caption")) {
/* 934*/        Token.EndTag endTag = t.asEndTag();
/* 935*/        String name = endTag.name();
/* 936*/        if (!tb.inTableScope(name)) {
/* 937*/          tb.error(this);
/* 938*/          return false;
/*   0*/        } 
/* 940*/        tb.generateImpliedEndTags();
/* 941*/        if (!tb.currentElement().nodeName().equals("caption")) {
/* 942*/            tb.error(this); 
/*   0*/           }
/* 943*/        tb.popStackToClose("caption");
/* 944*/        tb.clearFormattingElementsToLastMarker();
/* 945*/        tb.transition(InTable);
/* 947*/      } else if ((t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) || (t.isEndTag() && t.asEndTag().name().equals("table"))) {
/* 952*/        tb.error(this);
/* 953*/        boolean processed = tb.processEndTag("caption");
/* 954*/        if (processed) {
/* 955*/            return tb.process(t); 
/*   0*/           }
/*   0*/      } else {
/* 956*/        if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/* 958*/          tb.error(this);
/* 959*/          return false;
/*   0*/        } 
/* 961*/        return tb.process(t, InBody);
/*   0*/      } 
/* 963*/      return true;
/*   0*/    }
/*   0*/  },
/* 966*/  InColumnGroup {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/* 968*/      if (isWhitespace(t)) {
/* 969*/        tb.insert(t.asCharacter());
/* 970*/        return true;
/*   0*/      } 
/* 972*/      switch (t.type) {
/*   0*/        case Comment:
/* 974*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/* 977*/          tb.error(this);
/*   0*/          break;
/*   0*/        case StartTag:
/* 980*/          startTag = t.asStartTag();
/* 981*/          name = startTag.name();
/* 982*/          if (name.equals("html")) {
/* 983*/              return tb.process(t, InBody); 
/*   0*/             }
/* 984*/          if (name.equals("col")) {
/* 985*/            tb.insertEmpty(startTag);
/*   0*/            break;
/*   0*/          } 
/* 987*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/* 990*/          endTag = t.asEndTag();
/* 991*/          name = endTag.name();
/* 992*/          if (name.equals("colgroup")) {
/* 993*/            if (tb.currentElement().nodeName().equals("html")) {
/* 994*/              tb.error(this);
/* 995*/              return false;
/*   0*/            } 
/* 997*/            tb.pop();
/* 998*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1001*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1004*/          if (tb.currentElement().nodeName().equals("html")) {
/*1005*/              return true; 
/*   0*/             }
/*1007*/          return anythingElse(t, tb);
/*   0*/        default:
/*1009*/          return anythingElse(t, tb);
/*   0*/      } 
/*1011*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, TreeBuilder tb) {
/*1015*/      boolean processed = tb.processEndTag("colgroup");
/*1016*/      if (processed) {
/*1017*/          return tb.process(t); 
/*   0*/         }
/*1018*/      return true;
/*   0*/    }
/*   0*/  },
/*1021*/  InTableBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.StartTag startTag;
/*   0*/      String name;
/*   0*/      Token.EndTag endTag;
/*1023*/      switch (t.type) {
/*   0*/        case StartTag:
/*1025*/          startTag = t.asStartTag();
/*1026*/          name = startTag.name();
/*1027*/          if (name.equals("tr")) {
/*1028*/            tb.clearStackToTableBodyContext();
/*1029*/            tb.insert(startTag);
/*1030*/            tb.transition(InRow);
/*   0*/            break;
/*   0*/          } 
/*1031*/          if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1032*/            tb.error(this);
/*1033*/            tb.processStartTag("tr");
/*1034*/            return tb.process(startTag);
/*   0*/          } 
/*1035*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead" })) {
/*1036*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1038*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1041*/          endTag = t.asEndTag();
/*1042*/          name = endTag.name();
/*1043*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1044*/            if (!tb.inTableScope(name)) {
/*1045*/              tb.error(this);
/*1046*/              return false;
/*   0*/            } 
/*1048*/            tb.clearStackToTableBodyContext();
/*1049*/            tb.pop();
/*1050*/            tb.transition(InTable);
/*   0*/            break;
/*   0*/          } 
/*1052*/          if (name.equals("table")) {
/*1053*/              return exitTableBody(t, tb); 
/*   0*/             }
/*1054*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th", "tr" })) {
/*1055*/            tb.error(this);
/*1056*/            return false;
/*   0*/          } 
/*1058*/          return anythingElse(t, tb);
/*   0*/        default:
/*1061*/          return anythingElse(t, tb);
/*   0*/      } 
/*1063*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
/*1067*/      if (!tb.inTableScope("tbody") && !tb.inTableScope("thead") && !tb.inScope("tfoot")) {
/*1069*/        tb.error(this);
/*1070*/        return false;
/*   0*/      } 
/*1072*/      tb.clearStackToTableBodyContext();
/*1073*/      tb.processEndTag(tb.currentElement().nodeName());
/*1074*/      return tb.process(t);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1078*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/  },
/*1081*/  InRow {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1083*/      if (t.isStartTag()) {
/*1084*/        Token.StartTag startTag = t.asStartTag();
/*1085*/        String name = startTag.name();
/*1087*/        if (StringUtil.in(name, new String[] { "th", "td" })) {
/*1088*/          tb.clearStackToTableRowContext();
/*1089*/          tb.insert(startTag);
/*1090*/          tb.transition(InCell);
/*1091*/          tb.insertMarkerToFormattingElements();
/*   0*/        } else {
/*1092*/          if (StringUtil.in(name, new String[] { "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr" })) {
/*1093*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1095*/          return anythingElse(t, tb);
/*   0*/        } 
/*1097*/      } else if (t.isEndTag()) {
/*1098*/        Token.EndTag endTag = t.asEndTag();
/*1099*/        String name = endTag.name();
/*1101*/        if (name.equals("tr")) {
/*1102*/          if (!tb.inTableScope(name)) {
/*1103*/            tb.error(this);
/*1104*/            return false;
/*   0*/          } 
/*1106*/          tb.clearStackToTableRowContext();
/*1107*/          tb.pop();
/*1108*/          tb.transition(InTableBody);
/*   0*/        } else {
/*1109*/          "table";
/*   0*/          if (false) {
/*1110*/              return handleMissingTr(t, tb); 
/*   0*/             }
/*1111*/          if (StringUtil.in(name, new String[] { "tbody", "tfoot", "thead" })) {
/*1112*/            if (!tb.inTableScope(name)) {
/*1113*/              tb.error(this);
/*1114*/              return false;
/*   0*/            } 
/*1116*/            tb.processEndTag("tr");
/*1117*/            return tb.process(t);
/*   0*/          } 
/*1118*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html", "td", "th" })) {
/*1119*/            tb.error(this);
/*1120*/            return false;
/*   0*/          } 
/*1122*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1125*/        return anythingElse(t, tb);
/*   0*/      } 
/*1127*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1131*/      return tb.process(t, InTable);
/*   0*/    }
/*   0*/    
/*   0*/    private boolean handleMissingTr(Token t, TreeBuilder tb) {
/*1135*/      boolean processed = tb.processEndTag("tr");
/*1136*/      if (processed) {
/*1137*/          return tb.process(t); 
/*   0*/         }
/*1139*/      return false;
/*   0*/    }
/*   0*/  },
/*1142*/  InCell {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1144*/      if (t.isEndTag()) {
/*1145*/        Token.EndTag endTag = t.asEndTag();
/*1146*/        String name = endTag.name();
/*1148*/        if (StringUtil.in(name, new String[] { "td", "th" })) {
/*1149*/          if (!tb.inTableScope(name)) {
/*1150*/            tb.error(this);
/*1151*/            tb.transition(InRow);
/*1152*/            return false;
/*   0*/          } 
/*1154*/          tb.generateImpliedEndTags();
/*1155*/          if (!tb.currentElement().nodeName().equals(name)) {
/*1156*/              tb.error(this); 
/*   0*/             }
/*1157*/          tb.popStackToClose(name);
/*1158*/          tb.clearFormattingElementsToLastMarker();
/*1159*/          tb.transition(InRow);
/*   0*/        } else {
/*1160*/          if (StringUtil.in(name, new String[] { "body", "caption", "col", "colgroup", "html" })) {
/*1161*/            tb.error(this);
/*1162*/            return false;
/*   0*/          } 
/*1163*/          if (StringUtil.in(name, new String[] { "table", "tbody", "tfoot", "thead", "tr" })) {
/*1164*/            if (!tb.inTableScope(name)) {
/*1165*/              tb.error(this);
/*1166*/              return false;
/*   0*/            } 
/*1168*/            closeCell(tb);
/*1169*/            return tb.process(t);
/*   0*/          } 
/*1171*/          return anythingElse(t, tb);
/*   0*/        } 
/*   0*/      } else {
/*1173*/        if (t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr" })) {
/*1176*/          if (!tb.inTableScope("td") && !tb.inTableScope("th")) {
/*1177*/            tb.error(this);
/*1178*/            return false;
/*   0*/          } 
/*1180*/          closeCell(tb);
/*1181*/          return tb.process(t);
/*   0*/        } 
/*1183*/        return anythingElse(t, tb);
/*   0*/      } 
/*1185*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1189*/      return tb.process(t, InBody);
/*   0*/    }
/*   0*/    
/*   0*/    private void closeCell(HtmlTreeBuilder tb) {
/*1193*/      if (tb.inTableScope("td")) {
/*1194*/        tb.processEndTag("td");
/*   0*/      } else {
/*1196*/        tb.processEndTag("th");
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1199*/  InSelect {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*   0*/      Token.Character c;
/*   0*/      Token.StartTag start;
/*   0*/      String name;
/*   0*/      Token.EndTag end;
/*1201*/      switch (t.type) {
/*   0*/        case Character:
/*1203*/          c = t.asCharacter();
/*1204*/          if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
/*1205*/            tb.error(this);
/*1206*/            return false;
/*   0*/          } 
/*1208*/          tb.insert(c);
/*   0*/          break;
/*   0*/        case Comment:
/*1212*/          tb.insert(t.asComment());
/*   0*/          break;
/*   0*/        case Doctype:
/*1215*/          tb.error(this);
/*1216*/          return false;
/*   0*/        case StartTag:
/*1218*/          start = t.asStartTag();
/*1219*/          name = start.name();
/*1220*/          if (name.equals("html")) {
/*1221*/              return tb.process(start, InBody); 
/*   0*/             }
/*1222*/          if (name.equals("option")) {
/*1223*/            tb.processEndTag("option");
/*1224*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1225*/          if (name.equals("optgroup")) {
/*1226*/            if (tb.currentElement().nodeName().equals("option")) {
/*1227*/              tb.processEndTag("option");
/*1228*/            } else if (tb.currentElement().nodeName().equals("optgroup")) {
/*1229*/              tb.processEndTag("optgroup");
/*   0*/            } 
/*1230*/            tb.insert(start);
/*   0*/            break;
/*   0*/          } 
/*1231*/          if (name.equals("select")) {
/*1232*/            tb.error(this);
/*1233*/            return tb.processEndTag("select");
/*   0*/          } 
/*1234*/          if (StringUtil.in(name, new String[] { "input", "keygen", "textarea" })) {
/*1235*/            tb.error(this);
/*1236*/            if (!tb.inSelectScope("select")) {
/*1237*/                return false; 
/*   0*/               }
/*1238*/            tb.processEndTag("select");
/*1239*/            return tb.process(start);
/*   0*/          } 
/*1240*/          if (name.equals("script")) {
/*1241*/              return tb.process(t, InHead); 
/*   0*/             }
/*1243*/          return anythingElse(t, tb);
/*   0*/        case EndTag:
/*1247*/          end = t.asEndTag();
/*1248*/          name = end.name();
/*1249*/          if (name.equals("optgroup")) {
/*1250*/            if (tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
/*1251*/                tb.processEndTag("option"); 
/*   0*/               }
/*1252*/            if (tb.currentElement().nodeName().equals("optgroup")) {
/*1253*/              tb.pop();
/*   0*/              break;
/*   0*/            } 
/*1255*/            tb.error(this);
/*   0*/            break;
/*   0*/          } 
/*1256*/          if (name.equals("option")) {
/*1257*/            if (tb.currentElement().nodeName().equals("option")) {
/*1258*/              tb.pop();
/*   0*/              break;
/*   0*/            } 
/*1260*/            tb.error(this);
/*   0*/            break;
/*   0*/          } 
/*1261*/          if (name.equals("select")) {
/*1262*/            if (!tb.inSelectScope(name)) {
/*1263*/              tb.error(this);
/*1264*/              return false;
/*   0*/            } 
/*1266*/            tb.popStackToClose(name);
/*1267*/            tb.resetInsertionMode();
/*   0*/            break;
/*   0*/          } 
/*1270*/          return anythingElse(t, tb);
/*   0*/        case EOF:
/*1273*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1274*/              tb.error(this); 
/*   0*/             }
/*   0*/          break;
/*   0*/        default:
/*1277*/          return anythingElse(t, tb);
/*   0*/      } 
/*1279*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
/*1283*/      tb.error(this);
/*1284*/      return false;
/*   0*/    }
/*   0*/  },
/*1287*/  InSelectInTable {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1289*/      if (t.isStartTag() && StringUtil.in(t.asStartTag().name(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1290*/        tb.error(this);
/*1291*/        tb.processEndTag("select");
/*1292*/        return tb.process(t);
/*   0*/      } 
/*1293*/      if (t.isEndTag() && StringUtil.in(t.asEndTag().name(), new String[] { "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th" })) {
/*1294*/        tb.error(this);
/*1295*/        if (tb.inTableScope(t.asEndTag().name())) {
/*1296*/          tb.processEndTag("select");
/*1297*/          return tb.process(t);
/*   0*/        } 
/*1299*/        return false;
/*   0*/      } 
/*1301*/      return tb.process(t, InSelect);
/*   0*/    }
/*   0*/  },
/*1305*/  AfterBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1307*/      if (isWhitespace(t)) {
/*1308*/          return tb.process(t, InBody); 
/*   0*/         }
/*1309*/      if (t.isComment()) {
/*1310*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1311*/        if (t.isDoctype()) {
/*1312*/          tb.error(this);
/*1313*/          return false;
/*   0*/        } 
/*1314*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*1315*/            return tb.process(t, InBody); 
/*   0*/           }
/*1316*/        if (t.isEndTag() && t.asEndTag().name().equals("html")) {
/*1317*/          if (tb.isFragmentParsing()) {
/*1318*/            tb.error(this);
/*1319*/            return false;
/*   0*/          } 
/*1321*/          tb.transition(AfterAfterBody);
/*1323*/        } else if (!t.isEOF()) {
/*1326*/          tb.error(this);
/*1327*/          tb.transition(InBody);
/*1328*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1330*/      return true;
/*   0*/    }
/*   0*/  },
/*1333*/  InFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1335*/      if (isWhitespace(t)) {
/*1336*/        tb.insert(t.asCharacter());
/*1337*/      } else if (t.isComment()) {
/*1338*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1339*/        if (t.isDoctype()) {
/*1340*/          tb.error(this);
/*1341*/          return false;
/*   0*/        } 
/*1342*/        if (t.isStartTag()) {
/*1343*/          Token.StartTag start = t.asStartTag();
/*1344*/          String name = start.name();
/*1345*/          if (name.equals("html")) {
/*1346*/              return tb.process(start, InBody); 
/*   0*/             }
/*1347*/          if (name.equals("frameset")) {
/*1348*/            tb.insert(start);
/*1349*/          } else if (name.equals("frame")) {
/*1350*/            tb.insertEmpty(start);
/*   0*/          } else {
/*1351*/            if (name.equals("noframes")) {
/*1352*/                return tb.process(start, InHead); 
/*   0*/               }
/*1354*/            tb.error(this);
/*1355*/            return false;
/*   0*/          } 
/*1357*/        } else if (t.isEndTag() && t.asEndTag().name().equals("frameset")) {
/*1358*/          if (tb.currentElement().nodeName().equals("html")) {
/*1359*/            tb.error(this);
/*1360*/            return false;
/*   0*/          } 
/*1362*/          tb.pop();
/*1363*/          if (!tb.isFragmentParsing() && !tb.currentElement().nodeName().equals("frameset")) {
/*1364*/              tb.transition(AfterFrameset); 
/*   0*/             }
/*1367*/        } else if (t.isEOF()) {
/*1368*/          if (!tb.currentElement().nodeName().equals("html")) {
/*1369*/            tb.error(this);
/*1370*/            return true;
/*   0*/          } 
/*   0*/        } else {
/*1373*/          tb.error(this);
/*1374*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1376*/      return true;
/*   0*/    }
/*   0*/  },
/*1379*/  AfterFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1381*/      if (isWhitespace(t)) {
/*1382*/        tb.insert(t.asCharacter());
/*1383*/      } else if (t.isComment()) {
/*1384*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1385*/        if (t.isDoctype()) {
/*1386*/          tb.error(this);
/*1387*/          return false;
/*   0*/        } 
/*1388*/        if (t.isStartTag() && t.asStartTag().name().equals("html")) {
/*1389*/            return tb.process(t, InBody); 
/*   0*/           }
/*1390*/        if (t.isEndTag() && t.asEndTag().name().equals("html")) {
/*1391*/          tb.transition(AfterAfterFrameset);
/*   0*/        } else {
/*1392*/          if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
/*1393*/              return tb.process(t, InHead); 
/*   0*/             }
/*1394*/          if (!t.isEOF()) {
/*1397*/            tb.error(this);
/*1398*/            return false;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1400*/      return true;
/*   0*/    }
/*   0*/  },
/*1403*/  AfterAfterBody {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1405*/      if (t.isComment()) {
/*1406*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1407*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
/*1408*/            return tb.process(t, InBody); 
/*   0*/           }
/*1409*/        if (!t.isEOF()) {
/*1412*/          tb.error(this);
/*1413*/          tb.transition(InBody);
/*1414*/          return tb.process(t);
/*   0*/        } 
/*   0*/      } 
/*1416*/      return true;
/*   0*/    }
/*   0*/  },
/*1419*/  AfterAfterFrameset {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1421*/      if (t.isComment()) {
/*1422*/        tb.insert(t.asComment());
/*   0*/      } else {
/*1423*/        if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().name().equals("html"))) {
/*1424*/            return tb.process(t, InBody); 
/*   0*/           }
/*1425*/        if (!t.isEOF()) {
/*1427*/          if (t.isStartTag() && t.asStartTag().name().equals("noframes")) {
/*1428*/              return tb.process(t, InHead); 
/*   0*/             }
/*1430*/          tb.error(this);
/*1431*/          return false;
/*   0*/        } 
/*   0*/      } 
/*1433*/      return true;
/*   0*/    }
/*   0*/  },
/*1436*/  ForeignContent {
/*   0*/    boolean process(Token t, HtmlTreeBuilder tb) {
/*1438*/      return true;
/*   0*/    }
/*   0*/  };
/*   0*/  
/*1443*/  private static String nullString = String.valueOf(Character.MIN_VALUE);
/*   0*/  
/*   0*/  private static boolean isWhitespace(Token t) {
/*1448*/    if (t.isCharacter()) {
/*1449*/      String data = t.asCharacter().getData();
/*1450*/      return isWhitespace(data);
/*   0*/    } 
/*1452*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isWhitespace(String data) {
/*1457*/    for (int i = 0; i < data.length(); i++) {
/*1458*/      char c = data.charAt(i);
/*1459*/      if (!StringUtil.isWhitespace(c)) {
/*1460*/          return false; 
/*   0*/         }
/*   0*/    } 
/*1462*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
/*1466*/    tb.insert(startTag);
/*1467*/    tb.tokeniser.transition(TokeniserState.Rcdata);
/*1468*/    tb.markInsertionMode();
/*1469*/    tb.transition(Text);
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
/*1473*/    tb.insert(startTag);
/*1474*/    tb.tokeniser.transition(TokeniserState.Rawtext);
/*1475*/    tb.markInsertionMode();
/*1476*/    tb.transition(Text);
/*   0*/  }
/*   0*/  
/*   0*/  abstract boolean process(Token paramToken, HtmlTreeBuilder paramHtmlTreeBuilder);
/*   0*/  
/*   0*/  private static final class Constants {
/*1482*/    private static final String[] InBodyStartToHead = new String[] { "base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title" };
/*   0*/    
/*1483*/    private static final String[] InBodyStartPClosers = new String[] { 
/*1483*/        "address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", 
/*1483*/        "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", 
/*1483*/        "summary", "ul" };
/*   0*/    
/*1486*/    private static final String[] Headings = new String[] { "h1", "h2", "h3", "h4", "h5", "h6" };
/*   0*/    
/*1487*/    private static final String[] InBodyStartPreListing = new String[] { "pre", "listing" };
/*   0*/    
/*1488*/    private static final String[] InBodyStartLiBreakers = new String[] { "address", "div", "p" };
/*   0*/    
/*1489*/    private static final String[] DdDt = new String[] { "dd", "dt" };
/*   0*/    
/*1490*/    private static final String[] Formatters = new String[] { 
/*1490*/        "b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", 
/*1490*/        "tt", "u" };
/*   0*/    
/*1491*/    private static final String[] InBodyStartApplets = new String[] { "applet", "marquee", "object" };
/*   0*/    
/*1492*/    private static final String[] InBodyStartEmptyFormatters = new String[] { "area", "br", "embed", "img", "keygen", "wbr" };
/*   0*/    
/*1493*/    private static final String[] InBodyStartMedia = new String[] { "param", "source", "track" };
/*   0*/    
/*1494*/    private static final String[] InBodyStartInputAttribs = new String[] { "name", "action", "prompt" };
/*   0*/    
/*1495*/    private static final String[] InBodyStartOptions = new String[] { "optgroup", "option" };
/*   0*/    
/*1496*/    private static final String[] InBodyStartRuby = new String[] { "rp", "rt" };
/*   0*/    
/*1497*/    private static final String[] InBodyStartDrop = new String[] { 
/*1497*/        "caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", 
/*1497*/        "tr" };
/*   0*/    
/*1498*/    private static final String[] InBodyEndClosers = new String[] { 
/*1498*/        "address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", 
/*1498*/        "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", 
/*1498*/        "pre", "section", "summary", "ul" };
/*   0*/    
/*1501*/    private static final String[] InBodyEndAdoptionFormatters = new String[] { 
/*1501*/        "a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", 
/*1501*/        "strike", "strong", "tt", "u" };
/*   0*/    
/*1502*/    private static final String[] InBodyEndTableFosters = new String[] { "table", "tbody", "tfoot", "thead", "tr" };
/*   0*/  }
/*   0*/}
