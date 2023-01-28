/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/enum TokeniserState {
/*   9*/  Data {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/*  12*/      switch (r.current()) {
/*   0*/        case '&':
/*  14*/          t.advanceTransition(CharacterReferenceInData);
/*   0*/          break;
/*   0*/        case '<':
/*  17*/          t.advanceTransition(TagOpen);
/*   0*/          break;
/*   0*/        case '\000':
/*  20*/          t.error(this);
/*  21*/          t.emit(r.consume());
/*   0*/          break;
/*   0*/        case '￿':
/*  24*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/*  27*/          data = r.consumeData();
/*  28*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*  33*/  CharacterReferenceInData {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*  36*/      readCharRef(t, Data);
/*   0*/    }
/*   0*/  },
/*  39*/  Rcdata {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/*  42*/      switch (r.current()) {
/*   0*/        case '&':
/*  44*/          t.advanceTransition(CharacterReferenceInRcdata);
/*   0*/          break;
/*   0*/        case '<':
/*  47*/          t.advanceTransition(RcdataLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/*  50*/          t.error(this);
/*  51*/          r.advance();
/*  52*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/*  55*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/*  58*/          data = r.consumeToAny(new char[] { '&', '<', Character.MIN_VALUE });
/*  59*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*  64*/  CharacterReferenceInRcdata {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*  66*/      readCharRef(t, Rcdata);
/*   0*/    }
/*   0*/  },
/*  69*/  Rawtext {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*  71*/      readData(t, r, this, RawtextLessthanSign);
/*   0*/    }
/*   0*/  },
/*  74*/  ScriptData {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*  76*/      readData(t, r, this, ScriptDataLessthanSign);
/*   0*/    }
/*   0*/  },
/*  79*/  PLAINTEXT {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/*  81*/      switch (r.current()) {
/*   0*/        case '\000':
/*  83*/          t.error(this);
/*  84*/          r.advance();
/*  85*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/*  88*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/*  91*/          data = r.consumeTo(Character.MIN_VALUE);
/*  92*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*  97*/  TagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 100*/      switch (r.current()) {
/*   0*/        case '!':
/* 102*/          t.advanceTransition(MarkupDeclarationOpen);
/*   0*/          break;
/*   0*/        case '/':
/* 105*/          t.advanceTransition(EndTagOpen);
/*   0*/          break;
/*   0*/        case '?':
/* 108*/          t.advanceTransition(BogusComment);
/*   0*/          break;
/*   0*/        default:
/* 111*/          if (r.matchesLetter()) {
/* 112*/            t.createTagPending(true);
/* 113*/            t.transition(TagName);
/*   0*/            break;
/*   0*/          } 
/* 115*/          t.error(this);
/* 116*/          t.emit('<');
/* 117*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 123*/  EndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 125*/      if (r.isEmpty()) {
/* 126*/        t.eofError(this);
/* 127*/        t.emit("</");
/* 128*/        t.transition(Data);
/* 129*/      } else if (r.matchesLetter()) {
/* 130*/        t.createTagPending(false);
/* 131*/        t.transition(TagName);
/* 132*/      } else if (r.matches('>')) {
/* 133*/        t.error(this);
/* 134*/        t.advanceTransition(Data);
/*   0*/      } else {
/* 136*/        t.error(this);
/* 137*/        t.advanceTransition(BogusComment);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 141*/  TagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 146*/      String tagName = r.consumeTagName();
/* 147*/      t.tagPending.appendTagName(tagName);
/* 149*/      char c = r.consume();
/* 150*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/* 156*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/* 159*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/* 162*/          t.emitTagPending();
/* 163*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 166*/          t.tagPending.appendTagName(TokeniserState.replacementStr);
/*   0*/          break;
/*   0*/        case '￿':
/* 169*/          t.eofError(this);
/* 170*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 173*/          t.tagPending.appendTagName(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 177*/  RcdataLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 180*/      if (r.matches('/')) {
/* 181*/        t.createTempBuffer();
/* 182*/        t.advanceTransition(RCDATAEndTagOpen);
/* 183*/      } else if (r.matchesLetter() && t.appropriateEndTagName() != null && !r.containsIgnoreCase("</" + t.appropriateEndTagName())) {
/* 186*/        t.tagPending = t.createTagPending(false).name(t.appropriateEndTagName());
/* 187*/        t.emitTagPending();
/* 188*/        r.unconsume();
/* 189*/        t.transition(Data);
/*   0*/      } else {
/* 191*/        t.emit("<");
/* 192*/        t.transition(Rcdata);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 196*/  RCDATAEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 198*/      if (r.matchesLetter()) {
/* 199*/        t.createTagPending(false);
/* 200*/        t.tagPending.appendTagName(r.current());
/* 201*/        t.dataBuffer.append(r.current());
/* 202*/        t.advanceTransition(RCDATAEndTagName);
/*   0*/      } else {
/* 204*/        t.emit("</");
/* 205*/        t.transition(Rcdata);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 209*/  RCDATAEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 211*/      if (r.matchesLetter()) {
/* 212*/        String name = r.consumeLetterSequence();
/* 213*/        t.tagPending.appendTagName(name);
/* 214*/        t.dataBuffer.append(name);
/*   0*/        return;
/*   0*/      } 
/* 218*/      char c = r.consume();
/* 219*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/* 225*/          if (t.isAppropriateEndTagToken()) {
/* 226*/            t.transition(BeforeAttributeName);
/*   0*/            break;
/*   0*/          } 
/* 228*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/        case '/':
/* 231*/          if (t.isAppropriateEndTagToken()) {
/* 232*/            t.transition(SelfClosingStartTag);
/*   0*/            break;
/*   0*/          } 
/* 234*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/        case '>':
/* 237*/          if (t.isAppropriateEndTagToken()) {
/* 238*/            t.emitTagPending();
/* 239*/            t.transition(Data);
/*   0*/            break;
/*   0*/          } 
/* 242*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/        default:
/* 245*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void anythingElse(Tokeniser t, CharacterReader r) {
/* 250*/      t.emit("</" + t.dataBuffer.toString());
/* 251*/      r.unconsume();
/* 252*/      t.transition(Rcdata);
/*   0*/    }
/*   0*/  },
/* 255*/  RawtextLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 257*/      if (r.matches('/')) {
/* 258*/        t.createTempBuffer();
/* 259*/        t.advanceTransition(RawtextEndTagOpen);
/*   0*/      } else {
/* 261*/        t.emit('<');
/* 262*/        t.transition(Rawtext);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 266*/  RawtextEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 268*/      readEndTag(t, r, RawtextEndTagName, Rawtext);
/*   0*/    }
/*   0*/  },
/* 271*/  RawtextEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 273*/      handleDataEndTag(t, r, Rawtext);
/*   0*/    }
/*   0*/  },
/* 276*/  ScriptDataLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 278*/      switch (r.consume()) {
/*   0*/        case '/':
/* 280*/          t.createTempBuffer();
/* 281*/          t.transition(ScriptDataEndTagOpen);
/*   0*/          break;
/*   0*/        case '!':
/* 284*/          t.emit("<!");
/* 285*/          t.transition(ScriptDataEscapeStart);
/*   0*/          break;
/*   0*/        default:
/* 288*/          t.emit("<");
/* 289*/          r.unconsume();
/* 290*/          t.transition(ScriptData);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 294*/  ScriptDataEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 296*/      readEndTag(t, r, ScriptDataEndTagName, ScriptData);
/*   0*/    }
/*   0*/  },
/* 299*/  ScriptDataEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 301*/      handleDataEndTag(t, r, ScriptData);
/*   0*/    }
/*   0*/  },
/* 304*/  ScriptDataEscapeStart {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 306*/      if (r.matches('-')) {
/* 307*/        t.emit('-');
/* 308*/        t.advanceTransition(ScriptDataEscapeStartDash);
/*   0*/      } else {
/* 310*/        t.transition(ScriptData);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 314*/  ScriptDataEscapeStartDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 316*/      if (r.matches('-')) {
/* 317*/        t.emit('-');
/* 318*/        t.advanceTransition(ScriptDataEscapedDashDash);
/*   0*/      } else {
/* 320*/        t.transition(ScriptData);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 324*/  ScriptDataEscaped {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/* 326*/      if (r.isEmpty()) {
/* 327*/        t.eofError(this);
/* 328*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/* 332*/      switch (r.current()) {
/*   0*/        case '-':
/* 334*/          t.emit('-');
/* 335*/          t.advanceTransition(ScriptDataEscapedDash);
/*   0*/          break;
/*   0*/        case '<':
/* 338*/          t.advanceTransition(ScriptDataEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 341*/          t.error(this);
/* 342*/          r.advance();
/* 343*/          t.emit('�');
/*   0*/          break;
/*   0*/        default:
/* 346*/          data = r.consumeToAny(new char[] { '-', '<', Character.MIN_VALUE });
/* 347*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 351*/  ScriptDataEscapedDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 353*/      if (r.isEmpty()) {
/* 354*/        t.eofError(this);
/* 355*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/* 359*/      char c = r.consume();
/* 360*/      switch (c) {
/*   0*/        case '-':
/* 362*/          t.emit(c);
/* 363*/          t.transition(ScriptDataEscapedDashDash);
/*   0*/          break;
/*   0*/        case '<':
/* 366*/          t.transition(ScriptDataEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 369*/          t.error(this);
/* 370*/          t.emit('�');
/* 371*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/        default:
/* 374*/          t.emit(c);
/* 375*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 379*/  ScriptDataEscapedDashDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 381*/      if (r.isEmpty()) {
/* 382*/        t.eofError(this);
/* 383*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/* 387*/      char c = r.consume();
/* 388*/      switch (c) {
/*   0*/        case '-':
/* 390*/          t.emit(c);
/*   0*/          break;
/*   0*/        case '<':
/* 393*/          t.transition(ScriptDataEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '>':
/* 396*/          t.emit(c);
/* 397*/          t.transition(ScriptData);
/*   0*/          break;
/*   0*/        case '\000':
/* 400*/          t.error(this);
/* 401*/          t.emit('�');
/* 402*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/        default:
/* 405*/          t.emit(c);
/* 406*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 410*/  ScriptDataEscapedLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 412*/      if (r.matchesLetter()) {
/* 413*/        t.createTempBuffer();
/* 414*/        t.dataBuffer.append(r.current());
/* 415*/        t.emit("<" + r.current());
/* 416*/        t.advanceTransition(ScriptDataDoubleEscapeStart);
/* 417*/      } else if (r.matches('/')) {
/* 418*/        t.createTempBuffer();
/* 419*/        t.advanceTransition(ScriptDataEscapedEndTagOpen);
/*   0*/      } else {
/* 421*/        t.emit('<');
/* 422*/        t.transition(ScriptDataEscaped);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 426*/  ScriptDataEscapedEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 428*/      if (r.matchesLetter()) {
/* 429*/        t.createTagPending(false);
/* 430*/        t.tagPending.appendTagName(r.current());
/* 431*/        t.dataBuffer.append(r.current());
/* 432*/        t.advanceTransition(ScriptDataEscapedEndTagName);
/*   0*/      } else {
/* 434*/        t.emit("</");
/* 435*/        t.transition(ScriptDataEscaped);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 439*/  ScriptDataEscapedEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 441*/      handleDataEndTag(t, r, ScriptDataEscaped);
/*   0*/    }
/*   0*/  },
/* 444*/  ScriptDataDoubleEscapeStart {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 446*/      handleDataDoubleEscapeTag(t, r, ScriptDataDoubleEscaped, ScriptDataEscaped);
/*   0*/    }
/*   0*/  },
/* 449*/  ScriptDataDoubleEscaped {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/* 451*/      char c = r.current();
/* 452*/      switch (c) {
/*   0*/        case '-':
/* 454*/          t.emit(c);
/* 455*/          t.advanceTransition(ScriptDataDoubleEscapedDash);
/*   0*/          break;
/*   0*/        case '<':
/* 458*/          t.emit(c);
/* 459*/          t.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 462*/          t.error(this);
/* 463*/          r.advance();
/* 464*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 467*/          t.eofError(this);
/* 468*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 471*/          data = r.consumeToAny(new char[] { '-', '<', Character.MIN_VALUE });
/* 472*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 476*/  ScriptDataDoubleEscapedDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 478*/      char c = r.consume();
/* 479*/      switch (c) {
/*   0*/        case '-':
/* 481*/          t.emit(c);
/* 482*/          t.transition(ScriptDataDoubleEscapedDashDash);
/*   0*/          break;
/*   0*/        case '<':
/* 485*/          t.emit(c);
/* 486*/          t.transition(ScriptDataDoubleEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 489*/          t.error(this);
/* 490*/          t.emit('�');
/* 491*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/        case '￿':
/* 494*/          t.eofError(this);
/* 495*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 498*/          t.emit(c);
/* 499*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 503*/  ScriptDataDoubleEscapedDashDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 505*/      char c = r.consume();
/* 506*/      switch (c) {
/*   0*/        case '-':
/* 508*/          t.emit(c);
/*   0*/          break;
/*   0*/        case '<':
/* 511*/          t.emit(c);
/* 512*/          t.transition(ScriptDataDoubleEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '>':
/* 515*/          t.emit(c);
/* 516*/          t.transition(ScriptData);
/*   0*/          break;
/*   0*/        case '\000':
/* 519*/          t.error(this);
/* 520*/          t.emit('�');
/* 521*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/        case '￿':
/* 524*/          t.eofError(this);
/* 525*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 528*/          t.emit(c);
/* 529*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 533*/  ScriptDataDoubleEscapedLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 535*/      if (r.matches('/')) {
/* 536*/        t.emit('/');
/* 537*/        t.createTempBuffer();
/* 538*/        t.advanceTransition(ScriptDataDoubleEscapeEnd);
/*   0*/      } else {
/* 540*/        t.transition(ScriptDataDoubleEscaped);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 544*/  ScriptDataDoubleEscapeEnd {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 546*/      handleDataDoubleEscapeTag(t, r, ScriptDataEscaped, ScriptDataDoubleEscaped);
/*   0*/    }
/*   0*/  },
/* 549*/  BeforeAttributeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 552*/      char c = r.consume();
/* 553*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '/':
/* 561*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/* 564*/          t.emitTagPending();
/* 565*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 568*/          t.error(this);
/* 569*/          t.tagPending.newAttribute();
/* 570*/          r.unconsume();
/* 571*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        case '￿':
/* 574*/          t.eofError(this);
/* 575*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/*   0*/        case '=':
/* 581*/          t.error(this);
/* 582*/          t.tagPending.newAttribute();
/* 583*/          t.tagPending.appendAttributeName(c);
/* 584*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        default:
/* 587*/          t.tagPending.newAttribute();
/* 588*/          r.unconsume();
/* 589*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 593*/  AttributeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 596*/      String name = r.consumeToAnySorted(attributeNameCharsSorted);
/* 597*/      t.tagPending.appendAttributeName(name);
/* 599*/      char c = r.consume();
/* 600*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/* 606*/          t.transition(AfterAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/* 609*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '=':
/* 612*/          t.transition(BeforeAttributeValue);
/*   0*/          break;
/*   0*/        case '>':
/* 615*/          t.emitTagPending();
/* 616*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 619*/          t.error(this);
/* 620*/          t.tagPending.appendAttributeName('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 623*/          t.eofError(this);
/* 624*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/* 629*/          t.error(this);
/* 630*/          t.tagPending.appendAttributeName(c);
/*   0*/          break;
/*   0*/        default:
/* 633*/          t.tagPending.appendAttributeName(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 637*/  AfterAttributeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 639*/      char c = r.consume();
/* 640*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '/':
/* 649*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '=':
/* 652*/          t.transition(BeforeAttributeValue);
/*   0*/          break;
/*   0*/        case '>':
/* 655*/          t.emitTagPending();
/* 656*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 659*/          t.error(this);
/* 660*/          t.tagPending.appendAttributeName('�');
/* 661*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        case '￿':
/* 664*/          t.eofError(this);
/* 665*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/* 670*/          t.error(this);
/* 671*/          t.tagPending.newAttribute();
/* 672*/          t.tagPending.appendAttributeName(c);
/* 673*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        default:
/* 676*/          t.tagPending.newAttribute();
/* 677*/          r.unconsume();
/* 678*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 682*/  BeforeAttributeValue {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 684*/      char c = r.consume();
/* 685*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '"':
/* 694*/          t.transition(AttributeValue_doubleQuoted);
/*   0*/          break;
/*   0*/        case '&':
/* 697*/          r.unconsume();
/* 698*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/        case '\'':
/* 701*/          t.transition(AttributeValue_singleQuoted);
/*   0*/          break;
/*   0*/        case '\000':
/* 704*/          t.error(this);
/* 705*/          t.tagPending.appendAttributeValue('�');
/* 706*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/        case '￿':
/* 709*/          t.eofError(this);
/* 710*/          t.emitTagPending();
/* 711*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '>':
/* 714*/          t.error(this);
/* 715*/          t.emitTagPending();
/* 716*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '<':
/*   0*/        case '=':
/*   0*/        case '`':
/* 721*/          t.error(this);
/* 722*/          t.tagPending.appendAttributeValue(c);
/* 723*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/        default:
/* 726*/          r.unconsume();
/* 727*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 731*/  AttributeValue_doubleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      int[] ref;
/* 733*/      String value = r.consumeToAny(attributeDoubleValueCharsSorted);
/* 734*/      if (value.length() > 0) {
/* 735*/        t.tagPending.appendAttributeValue(value);
/*   0*/      } else {
/* 737*/        t.tagPending.setEmptyAttributeValue();
/*   0*/      } 
/* 739*/      char c = r.consume();
/* 740*/      switch (c) {
/*   0*/        case '"':
/* 742*/          t.transition(AfterAttributeValue_quoted);
/*   0*/          break;
/*   0*/        case '&':
/* 745*/          ref = t.consumeCharacterReference('"', true);
/* 746*/          if (ref != null) {
/* 747*/            t.tagPending.appendAttributeValue(ref);
/*   0*/            break;
/*   0*/          } 
/* 749*/          t.tagPending.appendAttributeValue('&');
/*   0*/          break;
/*   0*/        case '\000':
/* 752*/          t.error(this);
/* 753*/          t.tagPending.appendAttributeValue('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 756*/          t.eofError(this);
/* 757*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 760*/          t.tagPending.appendAttributeValue(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 764*/  AttributeValue_singleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      int[] ref;
/* 766*/      String value = r.consumeToAny(attributeSingleValueCharsSorted);
/* 767*/      if (value.length() > 0) {
/* 768*/        t.tagPending.appendAttributeValue(value);
/*   0*/      } else {
/*   0*/      
/*   0*/      } 
/* 772*/      char c = r.consume();
/* 773*/      switch (c) {
/*   0*/        case '\'':
/* 775*/          t.transition(AfterAttributeValue_quoted);
/*   0*/          break;
/*   0*/        case '&':
/* 778*/          ref = t.consumeCharacterReference('\'', true);
/* 779*/          if (ref != null) {
/* 780*/            t.tagPending.appendAttributeValue(ref);
/*   0*/            break;
/*   0*/          } 
/* 782*/          t.tagPending.appendAttributeValue('&');
/*   0*/          break;
/*   0*/        case '\000':
/* 785*/          t.error(this);
/* 786*/          t.tagPending.appendAttributeValue('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 789*/          t.eofError(this);
/* 790*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 793*/          t.tagPending.appendAttributeValue(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 797*/  AttributeValue_unquoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      int[] ref;
/* 799*/      String value = r.consumeToAnySorted(attributeValueUnquoted);
/* 800*/      if (value.length() > 0) {
/* 801*/          t.tagPending.appendAttributeValue(value); 
/*   0*/         }
/* 803*/      char c = r.consume();
/* 804*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/* 810*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '&':
/* 813*/          ref = t.consumeCharacterReference('>', true);
/* 814*/          if (ref != null) {
/* 815*/            t.tagPending.appendAttributeValue(ref);
/*   0*/            break;
/*   0*/          } 
/* 817*/          t.tagPending.appendAttributeValue('&');
/*   0*/          break;
/*   0*/        case '>':
/* 820*/          t.emitTagPending();
/* 821*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 824*/          t.error(this);
/* 825*/          t.tagPending.appendAttributeValue('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 828*/          t.eofError(this);
/* 829*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/*   0*/        case '=':
/*   0*/        case '`':
/* 836*/          t.error(this);
/* 837*/          t.tagPending.appendAttributeValue(c);
/*   0*/          break;
/*   0*/        default:
/* 840*/          t.tagPending.appendAttributeValue(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 846*/  AfterAttributeValue_quoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 848*/      char c = r.consume();
/* 849*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/* 855*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/* 858*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/* 861*/          t.emitTagPending();
/* 862*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/* 865*/          t.eofError(this);
/* 866*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 869*/          t.error(this);
/* 870*/          r.unconsume();
/* 871*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 876*/  SelfClosingStartTag {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 878*/      char c = r.consume();
/* 879*/      switch (c) {
/*   0*/        case '>':
/* 881*/          t.tagPending.selfClosing = true;
/* 882*/          t.emitTagPending();
/* 883*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/* 886*/          t.eofError(this);
/* 887*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 890*/          t.error(this);
/* 891*/          r.unconsume();
/* 892*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 896*/  BogusComment {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 900*/      r.unconsume();
/* 901*/      Token.Comment comment = new Token.Comment();
/* 902*/      comment.bogus = true;
/* 903*/      comment.data.append(r.consumeTo('>'));
/* 905*/      t.emit(comment);
/* 906*/      t.advanceTransition(Data);
/*   0*/    }
/*   0*/  },
/* 909*/  MarkupDeclarationOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 911*/      if (r.matchConsume("--")) {
/* 912*/        t.createCommentPending();
/* 913*/        t.transition(CommentStart);
/* 914*/      } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
/* 915*/        t.transition(Doctype);
/* 916*/      } else if (r.matchConsume("[CDATA[")) {
/* 920*/        t.createTempBuffer();
/* 921*/        t.transition(CdataSection);
/*   0*/      } else {
/* 923*/        t.error(this);
/* 924*/        t.advanceTransition(BogusComment);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 928*/  CommentStart {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 930*/      char c = r.consume();
/* 931*/      switch (c) {
/*   0*/        case '-':
/* 933*/          t.transition(CommentStartDash);
/*   0*/          break;
/*   0*/        case '\000':
/* 936*/          t.error(this);
/* 937*/          t.commentPending.data.append('�');
/* 938*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '>':
/* 941*/          t.error(this);
/* 942*/          t.emitCommentPending();
/* 943*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/* 946*/          t.eofError(this);
/* 947*/          t.emitCommentPending();
/* 948*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 951*/          t.commentPending.data.append(c);
/* 952*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 956*/  CommentStartDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 958*/      char c = r.consume();
/* 959*/      switch (c) {
/*   0*/        case '-':
/* 961*/          t.transition(CommentStartDash);
/*   0*/          break;
/*   0*/        case '\000':
/* 964*/          t.error(this);
/* 965*/          t.commentPending.data.append('�');
/* 966*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '>':
/* 969*/          t.error(this);
/* 970*/          t.emitCommentPending();
/* 971*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/* 974*/          t.eofError(this);
/* 975*/          t.emitCommentPending();
/* 976*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 979*/          t.commentPending.data.append(c);
/* 980*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 984*/  Comment {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 986*/      char c = r.current();
/* 987*/      switch (c) {
/*   0*/        case '-':
/* 989*/          t.advanceTransition(CommentEndDash);
/*   0*/          break;
/*   0*/        case '\000':
/* 992*/          t.error(this);
/* 993*/          r.advance();
/* 994*/          t.commentPending.data.append('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 997*/          t.eofError(this);
/* 998*/          t.emitCommentPending();
/* 999*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1002*/          t.commentPending.data.append(r.consumeToAny(new char[] { '-', Character.MIN_VALUE }));
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1006*/  CommentEndDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1008*/      char c = r.consume();
/*1009*/      switch (c) {
/*   0*/        case '-':
/*1011*/          t.transition(CommentEnd);
/*   0*/          break;
/*   0*/        case '\000':
/*1014*/          t.error(this);
/*1015*/          t.commentPending.data.append('-').append('�');
/*1016*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '￿':
/*1019*/          t.eofError(this);
/*1020*/          t.emitCommentPending();
/*1021*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1024*/          t.commentPending.data.append('-').append(c);
/*1025*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1029*/  CommentEnd {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1031*/      char c = r.consume();
/*1032*/      switch (c) {
/*   0*/        case '>':
/*1034*/          t.emitCommentPending();
/*1035*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/*1038*/          t.error(this);
/*1039*/          t.commentPending.data.append("--").append('�');
/*1040*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '!':
/*1043*/          t.error(this);
/*1044*/          t.transition(CommentEndBang);
/*   0*/          break;
/*   0*/        case '-':
/*1047*/          t.error(this);
/*1048*/          t.commentPending.data.append('-');
/*   0*/          break;
/*   0*/        case '￿':
/*1051*/          t.eofError(this);
/*1052*/          t.emitCommentPending();
/*1053*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1056*/          t.error(this);
/*1057*/          t.commentPending.data.append("--").append(c);
/*1058*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1062*/  CommentEndBang {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1064*/      char c = r.consume();
/*1065*/      switch (c) {
/*   0*/        case '-':
/*1067*/          t.commentPending.data.append("--!");
/*1068*/          t.transition(CommentEndDash);
/*   0*/          break;
/*   0*/        case '>':
/*1071*/          t.emitCommentPending();
/*1072*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/*1075*/          t.error(this);
/*1076*/          t.commentPending.data.append("--!").append('�');
/*1077*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '￿':
/*1080*/          t.eofError(this);
/*1081*/          t.emitCommentPending();
/*1082*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1085*/          t.commentPending.data.append("--!").append(c);
/*1086*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1090*/  Doctype {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1092*/      char c = r.consume();
/*1093*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*1099*/          t.transition(BeforeDoctypeName);
/*   0*/          break;
/*   0*/        case '￿':
/*1102*/          t.eofError(this);
/*   0*/        case '>':
/*1105*/          t.error(this);
/*1106*/          t.createDoctypePending();
/*1107*/          t.doctypePending.forceQuirks = true;
/*1108*/          t.emitDoctypePending();
/*1109*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1112*/          t.error(this);
/*1113*/          t.transition(BeforeDoctypeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1117*/  BeforeDoctypeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1119*/      if (r.matchesLetter()) {
/*1120*/        t.createDoctypePending();
/*1121*/        t.transition(DoctypeName);
/*   0*/        return;
/*   0*/      } 
/*1124*/      char c = r.consume();
/*1125*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '\000':
/*1133*/          t.error(this);
/*1134*/          t.createDoctypePending();
/*1135*/          t.doctypePending.name.append('�');
/*1136*/          t.transition(DoctypeName);
/*   0*/          break;
/*   0*/        case '￿':
/*1139*/          t.eofError(this);
/*1140*/          t.createDoctypePending();
/*1141*/          t.doctypePending.forceQuirks = true;
/*1142*/          t.emitDoctypePending();
/*1143*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1146*/          t.createDoctypePending();
/*1147*/          t.doctypePending.name.append(c);
/*1148*/          t.transition(DoctypeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1152*/  DoctypeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1154*/      if (r.matchesLetter()) {
/*1155*/        String name = r.consumeLetterSequence();
/*1156*/        t.doctypePending.name.append(name);
/*   0*/        return;
/*   0*/      } 
/*1159*/      char c = r.consume();
/*1160*/      switch (c) {
/*   0*/        case '>':
/*1162*/          t.emitDoctypePending();
/*1163*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*1170*/          t.transition(AfterDoctypeName);
/*   0*/          break;
/*   0*/        case '\000':
/*1173*/          t.error(this);
/*1174*/          t.doctypePending.name.append('�');
/*   0*/          break;
/*   0*/        case '￿':
/*1177*/          t.eofError(this);
/*1178*/          t.doctypePending.forceQuirks = true;
/*1179*/          t.emitDoctypePending();
/*1180*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1183*/          t.doctypePending.name.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1187*/  AfterDoctypeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1189*/      if (r.isEmpty()) {
/*1190*/        t.eofError(this);
/*1191*/        t.doctypePending.forceQuirks = true;
/*1192*/        t.emitDoctypePending();
/*1193*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/*1196*/      if (r.matchesAny(new char[] { '\t', '\n', '\r', '\f', ' ' })) {
/*1197*/        r.advance();
/*1198*/      } else if (r.matches('>')) {
/*1199*/        t.emitDoctypePending();
/*1200*/        t.advanceTransition(Data);
/*1201*/      } else if (r.matchConsumeIgnoreCase("PUBLIC")) {
/*1202*/        t.doctypePending.pubSysKey = "PUBLIC";
/*1203*/        t.transition(AfterDoctypePublicKeyword);
/*1204*/      } else if (r.matchConsumeIgnoreCase("SYSTEM")) {
/*1205*/        t.doctypePending.pubSysKey = "SYSTEM";
/*1206*/        t.transition(AfterDoctypeSystemKeyword);
/*   0*/      } else {
/*1208*/        t.error(this);
/*1209*/        t.doctypePending.forceQuirks = true;
/*1210*/        t.advanceTransition(BogusDoctype);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1215*/  AfterDoctypePublicKeyword {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1217*/      char c = r.consume();
/*1218*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*1224*/          t.transition(BeforeDoctypePublicIdentifier);
/*   0*/          break;
/*   0*/        case '"':
/*1227*/          t.error(this);
/*1229*/          t.transition(DoctypePublicIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1232*/          t.error(this);
/*1234*/          t.transition(DoctypePublicIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '>':
/*1237*/          t.error(this);
/*1238*/          t.doctypePending.forceQuirks = true;
/*1239*/          t.emitDoctypePending();
/*1240*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1243*/          t.eofError(this);
/*1244*/          t.doctypePending.forceQuirks = true;
/*1245*/          t.emitDoctypePending();
/*1246*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1249*/          t.error(this);
/*1250*/          t.doctypePending.forceQuirks = true;
/*1251*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1255*/  BeforeDoctypePublicIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1257*/      char c = r.consume();
/*1258*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '"':
/*1267*/          t.transition(DoctypePublicIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1271*/          t.transition(DoctypePublicIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '>':
/*1274*/          t.error(this);
/*1275*/          t.doctypePending.forceQuirks = true;
/*1276*/          t.emitDoctypePending();
/*1277*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1280*/          t.eofError(this);
/*1281*/          t.doctypePending.forceQuirks = true;
/*1282*/          t.emitDoctypePending();
/*1283*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1286*/          t.error(this);
/*1287*/          t.doctypePending.forceQuirks = true;
/*1288*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1292*/  DoctypePublicIdentifier_doubleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1294*/      char c = r.consume();
/*1295*/      switch (c) {
/*   0*/        case '"':
/*1297*/          t.transition(AfterDoctypePublicIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1300*/          t.error(this);
/*1301*/          t.doctypePending.publicIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1304*/          t.error(this);
/*1305*/          t.doctypePending.forceQuirks = true;
/*1306*/          t.emitDoctypePending();
/*1307*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1310*/          t.eofError(this);
/*1311*/          t.doctypePending.forceQuirks = true;
/*1312*/          t.emitDoctypePending();
/*1313*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1316*/          t.doctypePending.publicIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1320*/  DoctypePublicIdentifier_singleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1322*/      char c = r.consume();
/*1323*/      switch (c) {
/*   0*/        case '\'':
/*1325*/          t.transition(AfterDoctypePublicIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1328*/          t.error(this);
/*1329*/          t.doctypePending.publicIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1332*/          t.error(this);
/*1333*/          t.doctypePending.forceQuirks = true;
/*1334*/          t.emitDoctypePending();
/*1335*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1338*/          t.eofError(this);
/*1339*/          t.doctypePending.forceQuirks = true;
/*1340*/          t.emitDoctypePending();
/*1341*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1344*/          t.doctypePending.publicIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1348*/  AfterDoctypePublicIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1350*/      char c = r.consume();
/*1351*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*1357*/          t.transition(BetweenDoctypePublicAndSystemIdentifiers);
/*   0*/          break;
/*   0*/        case '>':
/*1360*/          t.emitDoctypePending();
/*1361*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*1364*/          t.error(this);
/*1366*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1369*/          t.error(this);
/*1371*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '￿':
/*1374*/          t.eofError(this);
/*1375*/          t.doctypePending.forceQuirks = true;
/*1376*/          t.emitDoctypePending();
/*1377*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1380*/          t.error(this);
/*1381*/          t.doctypePending.forceQuirks = true;
/*1382*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1386*/  BetweenDoctypePublicAndSystemIdentifiers {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1388*/      char c = r.consume();
/*1389*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '>':
/*1397*/          t.emitDoctypePending();
/*1398*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*1401*/          t.error(this);
/*1403*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1406*/          t.error(this);
/*1408*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '￿':
/*1411*/          t.eofError(this);
/*1412*/          t.doctypePending.forceQuirks = true;
/*1413*/          t.emitDoctypePending();
/*1414*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1417*/          t.error(this);
/*1418*/          t.doctypePending.forceQuirks = true;
/*1419*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1423*/  AfterDoctypeSystemKeyword {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1425*/      char c = r.consume();
/*1426*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*1432*/          t.transition(BeforeDoctypeSystemIdentifier);
/*   0*/          break;
/*   0*/        case '>':
/*1435*/          t.error(this);
/*1436*/          t.doctypePending.forceQuirks = true;
/*1437*/          t.emitDoctypePending();
/*1438*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*1441*/          t.error(this);
/*1443*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1446*/          t.error(this);
/*1448*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '￿':
/*1451*/          t.eofError(this);
/*1452*/          t.doctypePending.forceQuirks = true;
/*1453*/          t.emitDoctypePending();
/*1454*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1457*/          t.error(this);
/*1458*/          t.doctypePending.forceQuirks = true;
/*1459*/          t.emitDoctypePending();
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1463*/  BeforeDoctypeSystemIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1465*/      char c = r.consume();
/*1466*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '"':
/*1475*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1479*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '>':
/*1482*/          t.error(this);
/*1483*/          t.doctypePending.forceQuirks = true;
/*1484*/          t.emitDoctypePending();
/*1485*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1488*/          t.eofError(this);
/*1489*/          t.doctypePending.forceQuirks = true;
/*1490*/          t.emitDoctypePending();
/*1491*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1494*/          t.error(this);
/*1495*/          t.doctypePending.forceQuirks = true;
/*1496*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1500*/  DoctypeSystemIdentifier_doubleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1502*/      char c = r.consume();
/*1503*/      switch (c) {
/*   0*/        case '"':
/*1505*/          t.transition(AfterDoctypeSystemIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1508*/          t.error(this);
/*1509*/          t.doctypePending.systemIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1512*/          t.error(this);
/*1513*/          t.doctypePending.forceQuirks = true;
/*1514*/          t.emitDoctypePending();
/*1515*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1518*/          t.eofError(this);
/*1519*/          t.doctypePending.forceQuirks = true;
/*1520*/          t.emitDoctypePending();
/*1521*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1524*/          t.doctypePending.systemIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1528*/  DoctypeSystemIdentifier_singleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1530*/      char c = r.consume();
/*1531*/      switch (c) {
/*   0*/        case '\'':
/*1533*/          t.transition(AfterDoctypeSystemIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1536*/          t.error(this);
/*1537*/          t.doctypePending.systemIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1540*/          t.error(this);
/*1541*/          t.doctypePending.forceQuirks = true;
/*1542*/          t.emitDoctypePending();
/*1543*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1546*/          t.eofError(this);
/*1547*/          t.doctypePending.forceQuirks = true;
/*1548*/          t.emitDoctypePending();
/*1549*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1552*/          t.doctypePending.systemIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1556*/  AfterDoctypeSystemIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1558*/      char c = r.consume();
/*1559*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '>':
/*1567*/          t.emitDoctypePending();
/*1568*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1571*/          t.eofError(this);
/*1572*/          t.doctypePending.forceQuirks = true;
/*1573*/          t.emitDoctypePending();
/*1574*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1577*/          t.error(this);
/*1578*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1583*/  BogusDoctype {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1585*/      char c = r.consume();
/*1586*/      switch (c) {
/*   0*/        case '>':
/*1588*/          t.emitDoctypePending();
/*1589*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1592*/          t.emitDoctypePending();
/*1593*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1601*/  CdataSection {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1603*/      String data = r.consumeTo("]]>");
/*1604*/      t.dataBuffer.append(data);
/*1605*/      if (r.matchConsume("]]>") || r.isEmpty()) {
/*1606*/        t.emit(new Token.CData(t.dataBuffer.toString()));
/*1607*/        t.transition(Data);
/*   0*/      } 
/*   0*/    }
/*   0*/  };
/*   0*/  
/*1617*/  static final char[] attributeSingleValueCharsSorted = new char[] { Character.MIN_VALUE, '&', '\'' };
/*   0*/  
/*1618*/  static final char[] attributeDoubleValueCharsSorted = new char[] { Character.MIN_VALUE, '"', '&' };
/*   0*/  
/*1619*/  static final char[] attributeNameCharsSorted = new char[] { 
/*1619*/      Character.MIN_VALUE, '\t', '\n', '\f', '\r', ' ', '"', '\'', '/', '<', 
/*1619*/      '=', '>' };
/*   0*/  
/*1620*/  static final char[] attributeValueUnquoted = new char[] { 
/*1620*/      Character.MIN_VALUE, '\t', '\n', '\f', '\r', ' ', '"', '&', '\'', '<', 
/*1620*/      '=', '>', '`' };
/*   0*/  
/*1623*/  private static final String replacementStr = String.valueOf('�');
/*   0*/  
/*   0*/  static final char nullChar = '\000';
/*   0*/  
/*   0*/  private static final char replacementChar = '�';
/*   0*/  
/*   0*/  private static final char eof = '￿';
/*   0*/  
/*   0*/  private static void handleDataEndTag(Tokeniser t, CharacterReader r, TokeniserState elseTransition) {
/*1631*/    if (r.matchesLetter()) {
/*1632*/      String name = r.consumeLetterSequence();
/*1633*/      t.tagPending.appendTagName(name);
/*1634*/      t.dataBuffer.append(name);
/*   0*/      return;
/*   0*/    } 
/*   0*/    boolean needsExitTransition = false;
/*1639*/    if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
/*1640*/      char c = r.consume();
/*1641*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case '\r':
/*   0*/        case ' ':
/*1647*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/*1650*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/*1653*/          t.emitTagPending();
/*1654*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1657*/          t.dataBuffer.append(c);
/*1658*/          needsExitTransition = true;
/*   0*/          break;
/*   0*/      } 
/*   0*/    } else {
/*1661*/      needsExitTransition = true;
/*   0*/    } 
/*1664*/    if (needsExitTransition) {
/*1665*/      t.emit("</" + t.dataBuffer.toString());
/*1666*/      t.transition(elseTransition);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void readData(Tokeniser t, CharacterReader r, TokeniserState current, TokeniserState advance) {
/*   0*/    String data;
/*1671*/    switch (r.current()) {
/*   0*/      case '<':
/*1673*/        t.advanceTransition(advance);
/*   0*/        break;
/*   0*/      case '\000':
/*1676*/        t.error(current);
/*1677*/        r.advance();
/*1678*/        t.emit('�');
/*   0*/        break;
/*   0*/      case '￿':
/*1681*/        t.emit(new Token.EOF());
/*   0*/        break;
/*   0*/      default:
/*1684*/        data = r.consumeToAny(new char[] { '<', Character.MIN_VALUE });
/*1685*/        t.emit(data);
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void readCharRef(Tokeniser t, TokeniserState advance) {
/*1691*/    int[] c = t.consumeCharacterReference(null, false);
/*1692*/    if (c == null) {
/*1693*/      t.emit('&');
/*   0*/    } else {
/*1695*/      t.emit(c);
/*   0*/    } 
/*1696*/    t.transition(advance);
/*   0*/  }
/*   0*/  
/*   0*/  private static void readEndTag(Tokeniser t, CharacterReader r, TokeniserState a, TokeniserState b) {
/*1700*/    if (r.matchesLetter()) {
/*1701*/      t.createTagPending(false);
/*1702*/      t.transition(a);
/*   0*/    } else {
/*1704*/      t.emit("</");
/*1705*/      t.transition(b);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void handleDataDoubleEscapeTag(Tokeniser t, CharacterReader r, TokeniserState primary, TokeniserState fallback) {
/*1710*/    if (r.matchesLetter()) {
/*1711*/      String name = r.consumeLetterSequence();
/*1712*/      t.dataBuffer.append(name);
/*1713*/      t.emit(name);
/*   0*/      return;
/*   0*/    } 
/*1717*/    char c = r.consume();
/*1718*/    switch (c) {
/*   0*/      case '\t':
/*   0*/      case '\n':
/*   0*/      case '\f':
/*   0*/      case '\r':
/*   0*/      case ' ':
/*   0*/      case '/':
/*   0*/      case '>':
/*1726*/        if (t.dataBuffer.toString().equals("script")) {
/*1727*/          t.transition(primary);
/*   0*/        } else {
/*1729*/          t.transition(fallback);
/*   0*/        } 
/*1730*/        t.emit(c);
/*   0*/        break;
/*   0*/      default:
/*1733*/        r.unconsume();
/*1734*/        t.transition(fallback);
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  abstract void read(Tokeniser paramTokeniser, CharacterReader paramCharacterReader);
/*   0*/}
