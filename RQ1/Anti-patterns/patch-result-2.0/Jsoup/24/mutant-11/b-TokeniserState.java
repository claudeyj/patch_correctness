/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/enum TokeniserState {
/*   7*/  Data {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/*  10*/      switch (r.current()) {
/*   0*/        case '&':
/*  12*/          t.advanceTransition(CharacterReferenceInData);
/*   0*/          break;
/*   0*/        case '<':
/*  15*/          t.advanceTransition(TagOpen);
/*   0*/          break;
/*   0*/        case '\000':
/*  18*/          t.error(this);
/*  19*/          t.emit(r.consume());
/*   0*/          break;
/*   0*/        case '￿':
/*  22*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/*  25*/          data = r.consumeToAny(new char[] { '&', '<', Character.MIN_VALUE });
/*  26*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*  31*/  CharacterReferenceInData {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*  34*/      Character c = t.consumeCharacterReference(null, false);
/*  35*/      if (c == null) {
/*  36*/        t.emit('&');
/*   0*/      } else {
/*  38*/        t.emit(c);
/*   0*/      } 
/*  39*/      t.transition(Data);
/*   0*/    }
/*   0*/  },
/*  42*/  Rcdata {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/*  45*/      switch (r.current()) {
/*   0*/        case '&':
/*  47*/          t.advanceTransition(CharacterReferenceInRcdata);
/*   0*/          break;
/*   0*/        case '<':
/*  50*/          t.advanceTransition(RcdataLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/*  53*/          t.error(this);
/*  54*/          r.advance();
/*  55*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/*  58*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/*  61*/          data = r.consumeToAny(new char[] { '&', '<', Character.MIN_VALUE });
/*  62*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*  67*/  CharacterReferenceInRcdata {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*  69*/      Character c = t.consumeCharacterReference(null, false);
/*  70*/      if (c == null) {
/*  71*/        t.emit('&');
/*   0*/      } else {
/*  73*/        t.emit(c);
/*   0*/      } 
/*  74*/      t.transition(Rcdata);
/*   0*/    }
/*   0*/  },
/*  77*/  Rawtext {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/*  79*/      switch (r.current()) {
/*   0*/        case '<':
/*  81*/          t.advanceTransition(RawtextLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/*  84*/          t.error(this);
/*  85*/          r.advance();
/*  86*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/*  89*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/*  92*/          data = r.consumeToAny(new char[] { '<', Character.MIN_VALUE });
/*  93*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*  98*/  ScriptData {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/* 100*/      switch (r.current()) {
/*   0*/        case '<':
/* 102*/          t.advanceTransition(ScriptDataLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 105*/          t.error(this);
/* 106*/          r.advance();
/* 107*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 110*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/* 113*/          data = r.consumeToAny(new char[] { '<', Character.MIN_VALUE });
/* 114*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 119*/  PLAINTEXT {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/* 121*/      switch (r.current()) {
/*   0*/        case '\000':
/* 123*/          t.error(this);
/* 124*/          r.advance();
/* 125*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 128*/          t.emit(new Token.EOF());
/*   0*/          break;
/*   0*/        default:
/* 131*/          data = r.consumeTo(Character.MIN_VALUE);
/* 132*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 137*/  TagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 140*/      switch (r.current()) {
/*   0*/        case '!':
/* 142*/          t.advanceTransition(MarkupDeclarationOpen);
/*   0*/          break;
/*   0*/        case '/':
/* 145*/          t.advanceTransition(EndTagOpen);
/*   0*/          break;
/*   0*/        case '?':
/* 148*/          t.advanceTransition(BogusComment);
/*   0*/          break;
/*   0*/        default:
/* 151*/          if (r.matchesLetter()) {
/* 152*/            t.createTagPending(true);
/* 153*/            t.transition(TagName);
/*   0*/            break;
/*   0*/          } 
/* 155*/          t.error(this);
/* 156*/          t.emit('<');
/* 157*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 163*/  EndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 165*/      if (r.isEmpty()) {
/* 166*/        t.eofError(this);
/* 167*/        t.emit("</");
/* 168*/        t.transition(Data);
/* 169*/      } else if (r.matchesLetter()) {
/* 170*/        t.createTagPending(false);
/* 171*/        t.transition(TagName);
/* 172*/      } else if (r.matches('>')) {
/* 173*/        t.error(this);
/* 174*/        t.advanceTransition(Data);
/*   0*/      } else {
/* 176*/        t.error(this);
/* 177*/        t.advanceTransition(BogusComment);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 181*/  TagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 185*/      String tagName = r.consumeToAny(new char[] { '\t', '\n', '\f', ' ', '/', '>', Character.MIN_VALUE }).toLowerCase();
/* 186*/      t.tagPending.appendTagName(tagName);
/* 188*/      switch (r.consume()) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/* 193*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/* 196*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/* 199*/          t.emitTagPending();
/* 200*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 203*/          t.tagPending.appendTagName(TokeniserState.replacementStr);
/*   0*/          break;
/*   0*/        case '￿':
/* 206*/          t.eofError(this);
/* 207*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 212*/  RcdataLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 215*/      if (r.matches('/')) {
/* 216*/        t.createTempBuffer();
/* 217*/        t.advanceTransition(RCDATAEndTagOpen);
/* 218*/      } else if (r.matchesLetter() && !r.containsIgnoreCase("</" + t.appropriateEndTagName())) {
/* 221*/        t.tagPending = new Token.EndTag(t.appropriateEndTagName());
/* 222*/        t.emitTagPending();
/* 223*/        r.unconsume();
/* 224*/        t.transition(Data);
/*   0*/      } else {
/* 226*/        t.emit("<");
/* 227*/        t.transition(Rcdata);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 231*/  RCDATAEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 233*/      if (r.matchesLetter()) {
/* 234*/        t.createTagPending(false);
/* 235*/        t.tagPending.appendTagName(Character.toLowerCase(r.current()));
/* 236*/        t.dataBuffer.append(Character.toLowerCase(r.current()));
/* 237*/        t.advanceTransition(RCDATAEndTagName);
/*   0*/      } else {
/* 239*/        t.emit("</");
/* 240*/        t.transition(Rcdata);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 244*/  RCDATAEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 246*/      if (r.matchesLetter()) {
/* 247*/        String name = r.consumeLetterSequence();
/* 248*/        t.tagPending.appendTagName(name.toLowerCase());
/* 249*/        t.dataBuffer.append(name);
/*   0*/        return;
/*   0*/      } 
/* 253*/      char c = r.consume();
/* 254*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/* 259*/          if (t.isAppropriateEndTagToken()) {
/* 260*/            t.transition(BeforeAttributeName);
/*   0*/            break;
/*   0*/          } 
/* 262*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/        case '/':
/* 265*/          if (t.isAppropriateEndTagToken()) {
/* 266*/            t.transition(SelfClosingStartTag);
/*   0*/            break;
/*   0*/          } 
/* 268*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/        case '>':
/* 271*/          if (t.isAppropriateEndTagToken()) {
/* 272*/            t.emitTagPending();
/* 273*/            t.transition(Data);
/*   0*/            break;
/*   0*/          } 
/* 276*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/        default:
/* 279*/          anythingElse(t, r);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void anythingElse(Tokeniser t, CharacterReader r) {
/* 284*/      t.emit("</" + t.dataBuffer.toString());
/* 285*/      t.transition(Rcdata);
/*   0*/    }
/*   0*/  },
/* 288*/  RawtextLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 290*/      if (r.matches('/')) {
/* 291*/        t.createTempBuffer();
/* 292*/        t.advanceTransition(RawtextEndTagOpen);
/*   0*/      } else {
/* 294*/        t.emit('<');
/* 295*/        t.transition(Rawtext);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 299*/  RawtextEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 301*/      if (r.matchesLetter()) {
/* 302*/        t.createTagPending(false);
/* 303*/        t.transition(RawtextEndTagName);
/*   0*/      } else {
/* 305*/        t.emit("</");
/* 306*/        t.transition(Rawtext);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 310*/  RawtextEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 312*/      if (r.matchesLetter()) {
/* 313*/        String name = r.consumeLetterSequence();
/* 314*/        t.tagPending.appendTagName(name.toLowerCase());
/* 315*/        t.dataBuffer.append(name);
/*   0*/        return;
/*   0*/      } 
/* 319*/      if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
/* 320*/        char c = r.consume();
/* 321*/        switch (c) {
/*   0*/          case '\t':
/*   0*/          case '\n':
/*   0*/          case '\f':
/*   0*/          case ' ':
/* 326*/            t.transition(BeforeAttributeName);
/*   0*/            break;
/*   0*/          case '/':
/* 329*/            t.transition(SelfClosingStartTag);
/*   0*/            break;
/*   0*/          case '>':
/* 332*/            t.emitTagPending();
/* 333*/            t.transition(Data);
/*   0*/            break;
/*   0*/          default:
/* 336*/            t.dataBuffer.append(c);
/* 337*/            anythingElse(t, r);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } else {
/* 340*/        anythingElse(t, r);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void anythingElse(Tokeniser t, CharacterReader r) {
/* 344*/      t.emit("</" + t.dataBuffer.toString());
/* 345*/      t.transition(Rawtext);
/*   0*/    }
/*   0*/  },
/* 348*/  ScriptDataLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 350*/      switch (r.consume()) {
/*   0*/        case '/':
/* 352*/          t.createTempBuffer();
/* 353*/          t.transition(ScriptDataEndTagOpen);
/*   0*/          break;
/*   0*/        case '!':
/* 356*/          t.emit("<!");
/* 357*/          t.transition(ScriptDataEscapeStart);
/*   0*/          break;
/*   0*/        default:
/* 360*/          t.emit("<");
/* 361*/          r.unconsume();
/* 362*/          t.transition(ScriptData);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 366*/  ScriptDataEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 368*/      if (r.matchesLetter()) {
/* 369*/        t.createTagPending(false);
/* 370*/        t.transition(ScriptDataEndTagName);
/*   0*/      } else {
/* 372*/        t.emit("</");
/* 373*/        t.transition(ScriptData);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 378*/  ScriptDataEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 380*/      if (r.matchesLetter()) {
/* 381*/        String name = r.consumeLetterSequence();
/* 382*/        t.tagPending.appendTagName(name.toLowerCase());
/* 383*/        t.dataBuffer.append(name);
/*   0*/        return;
/*   0*/      } 
/* 387*/      if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
/* 388*/        char c = r.consume();
/* 389*/        switch (c) {
/*   0*/          case '\t':
/*   0*/          case '\n':
/*   0*/          case '\f':
/*   0*/          case ' ':
/* 394*/            t.transition(BeforeAttributeName);
/*   0*/            break;
/*   0*/          case '/':
/* 397*/            t.transition(SelfClosingStartTag);
/*   0*/            break;
/*   0*/          case '>':
/* 400*/            t.emitTagPending();
/* 401*/            t.transition(Data);
/*   0*/            break;
/*   0*/          default:
/* 404*/            t.dataBuffer.append(c);
/* 405*/            anythingElse(t, r);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } else {
/* 408*/        anythingElse(t, r);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void anythingElse(Tokeniser t, CharacterReader r) {
/* 413*/      t.emit("</" + t.dataBuffer.toString());
/* 414*/      t.transition(ScriptData);
/*   0*/    }
/*   0*/  },
/* 417*/  ScriptDataEscapeStart {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 419*/      if (r.matches('-')) {
/* 420*/        t.emit('-');
/* 421*/        t.advanceTransition(ScriptDataEscapeStartDash);
/*   0*/      } else {
/* 423*/        t.transition(ScriptData);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 427*/  ScriptDataEscapeStartDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 429*/      if (r.matches('-')) {
/* 430*/        t.emit('-');
/* 431*/        t.advanceTransition(ScriptDataEscapedDashDash);
/*   0*/      } else {
/* 433*/        t.transition(ScriptData);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 437*/  ScriptDataEscaped {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/* 439*/      if (r.isEmpty()) {
/* 440*/        t.eofError(this);
/* 441*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/* 445*/      switch (r.current()) {
/*   0*/        case '-':
/* 447*/          t.emit('-');
/* 448*/          t.advanceTransition(ScriptDataEscapedDash);
/*   0*/          break;
/*   0*/        case '<':
/* 451*/          t.advanceTransition(ScriptDataEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 454*/          t.error(this);
/* 455*/          r.advance();
/* 456*/          t.emit('�');
/*   0*/          break;
/*   0*/        default:
/* 459*/          data = r.consumeToAny(new char[] { '-', '<', Character.MIN_VALUE });
/* 460*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 464*/  ScriptDataEscapedDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 466*/      if (r.isEmpty()) {
/* 467*/        t.eofError(this);
/* 468*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/* 472*/      char c = r.consume();
/* 473*/      switch (c) {
/*   0*/        case '-':
/* 475*/          t.emit(c);
/* 476*/          t.transition(ScriptDataEscapedDashDash);
/*   0*/          break;
/*   0*/        case '<':
/* 479*/          t.transition(ScriptDataEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 482*/          t.error(this);
/* 483*/          t.emit('�');
/* 484*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/        default:
/* 487*/          t.emit(c);
/* 488*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 492*/  ScriptDataEscapedDashDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 494*/      if (r.isEmpty()) {
/* 495*/        t.eofError(this);
/* 496*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/* 500*/      char c = r.consume();
/* 501*/      switch (c) {
/*   0*/        case '-':
/* 503*/          t.emit(c);
/*   0*/          break;
/*   0*/        case '<':
/* 506*/          t.transition(ScriptDataEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '>':
/* 509*/          t.emit(c);
/* 510*/          t.transition(ScriptData);
/*   0*/          break;
/*   0*/        case '\000':
/* 513*/          t.error(this);
/* 514*/          t.emit('�');
/* 515*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/        default:
/* 518*/          t.emit(c);
/* 519*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 523*/  ScriptDataEscapedLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 525*/      if (r.matchesLetter()) {
/* 526*/        t.createTempBuffer();
/* 527*/        t.dataBuffer.append(Character.toLowerCase(r.current()));
/* 528*/        t.emit("<" + r.current());
/* 529*/        t.advanceTransition(ScriptDataDoubleEscapeStart);
/* 530*/      } else if (r.matches('/')) {
/* 531*/        t.createTempBuffer();
/* 532*/        t.advanceTransition(ScriptDataEscapedEndTagOpen);
/*   0*/      } else {
/* 534*/        t.emit('<');
/* 535*/        t.transition(ScriptDataEscaped);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 539*/  ScriptDataEscapedEndTagOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 541*/      r.matchesLetter();
/* 547*/      t.emit("</");
/* 548*/      t.transition(ScriptDataEscaped);
/*   0*/      while (true) {
/*   0*/          return; 
/*   0*/         }
/*   0*/    }
/*   0*/  },
/* 552*/  ScriptDataEscapedEndTagName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 554*/      if (r.matchesLetter()) {
/* 555*/        String name = r.consumeLetterSequence();
/* 556*/        t.tagPending.appendTagName(name.toLowerCase());
/* 557*/        t.dataBuffer.append(name);
/* 558*/        r.advance();
/*   0*/        return;
/*   0*/      } 
/* 562*/      if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
/* 563*/        char c = r.consume();
/* 564*/        switch (c) {
/*   0*/          case '\t':
/*   0*/          case '\n':
/*   0*/          case '\f':
/*   0*/          case ' ':
/* 569*/            t.transition(BeforeAttributeName);
/*   0*/            break;
/*   0*/          case '/':
/* 572*/            t.transition(SelfClosingStartTag);
/*   0*/            break;
/*   0*/          case '>':
/* 575*/            t.emitTagPending();
/* 576*/            t.transition(Data);
/*   0*/            break;
/*   0*/          default:
/* 579*/            t.dataBuffer.append(c);
/* 580*/            anythingElse(t, r);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } else {
/* 584*/        anythingElse(t, r);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void anythingElse(Tokeniser t, CharacterReader r) {
/* 589*/      t.emit("</" + t.dataBuffer.toString());
/* 590*/      t.transition(ScriptDataEscaped);
/*   0*/    }
/*   0*/  },
/* 593*/  ScriptDataDoubleEscapeStart {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 595*/      if (r.matchesLetter()) {
/* 596*/        String name = r.consumeLetterSequence();
/* 597*/        t.dataBuffer.append(name.toLowerCase());
/* 598*/        t.emit(name);
/*   0*/        return;
/*   0*/      } 
/* 602*/      char c = r.consume();
/* 603*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/        case '/':
/*   0*/        case '>':
/* 610*/          if (t.dataBuffer.toString().equals("script")) {
/* 611*/            t.transition(ScriptDataDoubleEscaped);
/*   0*/          } else {
/* 613*/            t.transition(ScriptDataEscaped);
/*   0*/          } 
/* 614*/          t.emit(c);
/*   0*/          break;
/*   0*/        default:
/* 617*/          r.unconsume();
/* 618*/          t.transition(ScriptDataEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 622*/  ScriptDataDoubleEscaped {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      String data;
/* 624*/      char c = r.current();
/* 625*/      switch (c) {
/*   0*/        case '-':
/* 627*/          t.emit(c);
/* 628*/          t.advanceTransition(ScriptDataDoubleEscapedDash);
/*   0*/          break;
/*   0*/        case '<':
/* 631*/          t.emit(c);
/* 632*/          t.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 635*/          t.error(this);
/* 636*/          r.advance();
/* 637*/          t.emit('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 640*/          t.eofError(this);
/* 641*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 644*/          data = r.consumeToAny(new char[] { '-', '<', Character.MIN_VALUE });
/* 645*/          t.emit(data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 649*/  ScriptDataDoubleEscapedDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 651*/      char c = r.consume();
/* 652*/      switch (c) {
/*   0*/        case '-':
/* 654*/          t.emit(c);
/* 655*/          t.transition(ScriptDataDoubleEscapedDashDash);
/*   0*/          break;
/*   0*/        case '<':
/* 658*/          t.emit(c);
/* 659*/          t.transition(ScriptDataDoubleEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '\000':
/* 662*/          t.error(this);
/* 663*/          t.emit('�');
/* 664*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/        case '￿':
/* 667*/          t.eofError(this);
/* 668*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 671*/          t.emit(c);
/* 672*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 676*/  ScriptDataDoubleEscapedDashDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 678*/      char c = r.consume();
/* 679*/      switch (c) {
/*   0*/        case '-':
/* 681*/          t.emit(c);
/*   0*/          break;
/*   0*/        case '<':
/* 684*/          t.emit(c);
/* 685*/          t.transition(ScriptDataDoubleEscapedLessthanSign);
/*   0*/          break;
/*   0*/        case '>':
/* 688*/          t.emit(c);
/* 689*/          t.transition(ScriptData);
/*   0*/          break;
/*   0*/        case '\000':
/* 692*/          t.error(this);
/* 693*/          t.emit('�');
/* 694*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/        case '￿':
/* 697*/          t.eofError(this);
/* 698*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/* 701*/          t.emit(c);
/* 702*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 706*/  ScriptDataDoubleEscapedLessthanSign {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 708*/      if (r.matches('/')) {
/* 709*/        t.emit('/');
/* 710*/        t.createTempBuffer();
/* 711*/        t.advanceTransition(ScriptDataDoubleEscapeEnd);
/*   0*/      } else {
/* 713*/        t.transition(ScriptDataDoubleEscaped);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 717*/  ScriptDataDoubleEscapeEnd {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 719*/      if (r.matchesLetter()) {
/* 720*/        String name = r.consumeLetterSequence();
/* 721*/        t.dataBuffer.append(name.toLowerCase());
/* 722*/        t.emit(name);
/*   0*/        return;
/*   0*/      } 
/* 726*/      char c = r.consume();
/* 727*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/        case '/':
/*   0*/        case '>':
/* 734*/          if (t.dataBuffer.toString().equals("script")) {
/* 735*/            t.transition(ScriptDataEscaped);
/*   0*/          } else {
/* 737*/            t.transition(ScriptDataDoubleEscaped);
/*   0*/          } 
/* 738*/          t.emit(c);
/*   0*/          break;
/*   0*/        default:
/* 741*/          r.unconsume();
/* 742*/          t.transition(ScriptDataDoubleEscaped);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 746*/  BeforeAttributeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 749*/      char c = r.consume();
/* 750*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '/':
/* 757*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/* 760*/          t.emitTagPending();
/* 761*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 764*/          t.error(this);
/* 765*/          t.tagPending.newAttribute();
/* 766*/          r.unconsume();
/* 767*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        case '￿':
/* 770*/          t.eofError(this);
/* 771*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/*   0*/        case '=':
/* 777*/          t.error(this);
/* 778*/          t.tagPending.newAttribute();
/* 779*/          t.tagPending.appendAttributeName(c);
/* 780*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        default:
/* 783*/          t.tagPending.newAttribute();
/* 784*/          r.unconsume();
/* 785*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 789*/  AttributeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 792*/      String name = r.consumeToAny(new char[] { 
/* 792*/            '\t', '\n', '\f', ' ', '/', '=', '>', Character.MIN_VALUE, '"', '\'', 
/* 792*/            '<' });
/* 793*/      t.tagPending.appendAttributeName(name.toLowerCase());
/* 795*/      char c = r.consume();
/* 796*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/* 801*/          t.transition(AfterAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/* 804*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '=':
/* 807*/          t.transition(BeforeAttributeValue);
/*   0*/          break;
/*   0*/        case '>':
/* 810*/          t.emitTagPending();
/* 811*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 814*/          t.error(this);
/* 815*/          t.tagPending.appendAttributeName('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 818*/          t.eofError(this);
/* 819*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/* 824*/          t.error(this);
/* 825*/          t.tagPending.appendAttributeName(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 830*/  AfterAttributeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 832*/      char c = r.consume();
/* 833*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '/':
/* 841*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '=':
/* 844*/          t.transition(BeforeAttributeValue);
/*   0*/          break;
/*   0*/        case '>':
/* 847*/          t.emitTagPending();
/* 848*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/* 851*/          t.error(this);
/* 852*/          t.tagPending.appendAttributeName('�');
/* 853*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        case '￿':
/* 856*/          t.eofError(this);
/* 857*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/* 862*/          t.error(this);
/* 863*/          t.tagPending.newAttribute();
/* 864*/          t.tagPending.appendAttributeName(c);
/* 865*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/        default:
/* 868*/          t.tagPending.newAttribute();
/* 869*/          r.unconsume();
/* 870*/          t.transition(AttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 874*/  BeforeAttributeValue {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/* 876*/      char c = r.consume();
/* 877*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '"':
/* 885*/          t.transition(AttributeValue_doubleQuoted);
/*   0*/          break;
/*   0*/        case '&':
/* 888*/          r.unconsume();
/* 889*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/        case '\'':
/* 892*/          t.transition(AttributeValue_singleQuoted);
/*   0*/          break;
/*   0*/        case '\000':
/* 895*/          t.error(this);
/* 896*/          t.tagPending.appendAttributeValue('�');
/* 897*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/        case '￿':
/* 900*/          t.eofError(this);
/* 901*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '>':
/* 904*/          t.error(this);
/* 905*/          t.emitTagPending();
/* 906*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '<':
/*   0*/        case '=':
/*   0*/        case '`':
/* 911*/          t.error(this);
/* 912*/          t.tagPending.appendAttributeValue(c);
/* 913*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/        default:
/* 916*/          r.unconsume();
/* 917*/          t.transition(AttributeValue_unquoted);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 921*/  AttributeValue_doubleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      Character ref;
/* 923*/      String value = r.consumeToAny(new char[] { '"', '&', Character.MIN_VALUE });
/* 924*/      if (value.length() > 0) {
/* 925*/          t.tagPending.appendAttributeValue(value); 
/*   0*/         }
/* 927*/      char c = r.consume();
/* 928*/      switch (c) {
/*   0*/        case '"':
/* 930*/          t.transition(AfterAttributeValue_quoted);
/*   0*/          break;
/*   0*/        case '&':
/* 933*/          ref = t.consumeCharacterReference('"', true);
/* 934*/          if (ref != null) {
/* 935*/            t.tagPending.appendAttributeValue(ref);
/*   0*/            break;
/*   0*/          } 
/* 937*/          t.tagPending.appendAttributeValue('&');
/*   0*/          break;
/*   0*/        case '\000':
/* 940*/          t.error(this);
/* 941*/          t.tagPending.appendAttributeValue('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 944*/          t.eofError(this);
/* 945*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 951*/  AttributeValue_singleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      Character ref;
/* 953*/      String value = r.consumeToAny(new char[] { '\'', '&', Character.MIN_VALUE });
/* 954*/      if (value.length() > 0) {
/* 955*/          t.tagPending.appendAttributeValue(value); 
/*   0*/         }
/* 957*/      char c = r.consume();
/* 958*/      switch (c) {
/*   0*/        case '\'':
/* 960*/          t.transition(AfterAttributeValue_quoted);
/*   0*/          break;
/*   0*/        case '&':
/* 963*/          ref = t.consumeCharacterReference('\'', true);
/* 964*/          if (ref != null) {
/* 965*/            t.tagPending.appendAttributeValue(ref);
/*   0*/            break;
/*   0*/          } 
/* 967*/          t.tagPending.appendAttributeValue('&');
/*   0*/          break;
/*   0*/        case '\000':
/* 970*/          t.error(this);
/* 971*/          t.tagPending.appendAttributeValue('�');
/*   0*/          break;
/*   0*/        case '￿':
/* 974*/          t.eofError(this);
/* 975*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/* 981*/  AttributeValue_unquoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*   0*/      Character ref;
/* 983*/      String value = r.consumeToAny(new char[] { 
/* 983*/            '\t', '\n', '\f', ' ', '&', '>', Character.MIN_VALUE, '"', '\'', '<', 
/* 983*/            '=', '`' });
/* 984*/      if (value.length() > 0) {
/* 985*/          t.tagPending.appendAttributeValue(value); 
/*   0*/         }
/* 987*/      char c = r.consume();
/* 988*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/* 993*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '&':
/* 996*/          ref = t.consumeCharacterReference('>', true);
/* 997*/          if (ref != null) {
/* 998*/            t.tagPending.appendAttributeValue(ref);
/*   0*/            break;
/*   0*/          } 
/*1000*/          t.tagPending.appendAttributeValue('&');
/*   0*/          break;
/*   0*/        case '>':
/*1003*/          t.emitTagPending();
/*1004*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/*1007*/          t.error(this);
/*1008*/          t.tagPending.appendAttributeValue('�');
/*   0*/          break;
/*   0*/        case '￿':
/*1011*/          t.eofError(this);
/*1012*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*   0*/        case '\'':
/*   0*/        case '<':
/*   0*/        case '=':
/*   0*/        case '`':
/*1019*/          t.error(this);
/*1020*/          t.tagPending.appendAttributeValue(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1028*/  AfterAttributeValue_quoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1030*/      char c = r.consume();
/*1031*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*1036*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/        case '/':
/*1039*/          t.transition(SelfClosingStartTag);
/*   0*/          break;
/*   0*/        case '>':
/*1042*/          t.emitTagPending();
/*1043*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1046*/          t.eofError(this);
/*1047*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1050*/          t.error(this);
/*1051*/          r.unconsume();
/*1052*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1057*/  SelfClosingStartTag {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1059*/      char c = r.consume();
/*1060*/      switch (c) {
/*   0*/        case '>':
/*1062*/          t.tagPending.selfClosing = true;
/*1063*/          t.emitTagPending();
/*1064*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1067*/          t.eofError(this);
/*1068*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1071*/          t.error(this);
/*1072*/          t.transition(BeforeAttributeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1076*/  BogusComment {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1080*/      r.unconsume();
/*1081*/      Token.Comment comment = new Token.Comment();
/*1082*/      comment.data.append(r.consumeTo('>'));
/*1084*/      t.emit(comment);
/*1085*/      t.advanceTransition(Data);
/*   0*/    }
/*   0*/  },
/*1088*/  MarkupDeclarationOpen {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1090*/      if (r.matchConsume("--")) {
/*1091*/        t.createCommentPending();
/*1092*/        t.transition(CommentStart);
/*1093*/      } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
/*1094*/        t.transition(Doctype);
/*1095*/      } else if (r.matchConsume("[CDATA[")) {
/*1099*/        t.transition(CdataSection);
/*   0*/      } else {
/*1101*/        t.error(this);
/*1102*/        t.advanceTransition(BogusComment);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1106*/  CommentStart {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1108*/      char c = r.consume();
/*1109*/      switch (c) {
/*   0*/        case '-':
/*1111*/          t.transition(CommentStartDash);
/*   0*/          break;
/*   0*/        case '\000':
/*1114*/          t.error(this);
/*1115*/          t.commentPending.data.append('�');
/*1116*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '>':
/*1119*/          t.error(this);
/*1120*/          t.emitCommentPending();
/*1121*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1124*/          t.eofError(this);
/*1125*/          t.emitCommentPending();
/*1126*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1129*/          t.commentPending.data.append(c);
/*1130*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1134*/  CommentStartDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1136*/      char c = r.consume();
/*1137*/      switch (c) {
/*   0*/        case '-':
/*1139*/          t.transition(CommentStartDash);
/*   0*/          break;
/*   0*/        case '\000':
/*1142*/          t.error(this);
/*1143*/          t.commentPending.data.append('�');
/*1144*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '>':
/*1147*/          t.error(this);
/*1148*/          t.emitCommentPending();
/*1149*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1152*/          t.eofError(this);
/*1153*/          t.emitCommentPending();
/*1154*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1157*/          t.commentPending.data.append(c);
/*1158*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1162*/  Comment {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1164*/      char c = r.current();
/*1165*/      switch (c) {
/*   0*/        case '-':
/*1167*/          t.advanceTransition(CommentEndDash);
/*   0*/          break;
/*   0*/        case '\000':
/*1170*/          t.error(this);
/*1171*/          r.advance();
/*1172*/          t.commentPending.data.append('�');
/*   0*/          break;
/*   0*/        case '￿':
/*1175*/          t.eofError(this);
/*1176*/          t.emitCommentPending();
/*1177*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1180*/          t.commentPending.data.append(r.consumeToAny(new char[] { '-', Character.MIN_VALUE }));
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1184*/  CommentEndDash {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1186*/      char c = r.consume();
/*1187*/      switch (c) {
/*   0*/        case '-':
/*1189*/          t.transition(CommentEnd);
/*   0*/          break;
/*   0*/        case '\000':
/*1192*/          t.error(this);
/*1193*/          t.commentPending.data.append('-').append('�');
/*1194*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '￿':
/*1197*/          t.eofError(this);
/*1198*/          t.emitCommentPending();
/*1199*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1202*/          t.commentPending.data.append('-').append(c);
/*1203*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1207*/  CommentEnd {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1209*/      char c = r.consume();
/*1210*/      switch (c) {
/*   0*/        case '>':
/*1212*/          t.emitCommentPending();
/*1213*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/*1216*/          t.error(this);
/*1217*/          t.commentPending.data.append("--").append('�');
/*1218*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '!':
/*1221*/          t.error(this);
/*1222*/          t.transition(CommentEndBang);
/*   0*/          break;
/*   0*/        case '-':
/*1225*/          t.error(this);
/*1226*/          t.commentPending.data.append('-');
/*   0*/          break;
/*   0*/        case '￿':
/*1229*/          t.eofError(this);
/*1230*/          t.emitCommentPending();
/*1231*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1234*/          t.error(this);
/*1235*/          t.commentPending.data.append("--").append(c);
/*1236*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1240*/  CommentEndBang {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1242*/      char c = r.consume();
/*1243*/      switch (c) {
/*   0*/        case '-':
/*1245*/          t.commentPending.data.append("--!");
/*1246*/          t.transition(CommentEndDash);
/*   0*/          break;
/*   0*/        case '>':
/*1249*/          t.emitCommentPending();
/*1250*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\000':
/*1253*/          t.error(this);
/*1254*/          t.commentPending.data.append("--!").append('�');
/*1255*/          t.transition(Comment);
/*   0*/          break;
/*   0*/        case '￿':
/*1258*/          t.eofError(this);
/*1259*/          t.emitCommentPending();
/*1260*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1263*/          t.commentPending.data.append("--!").append(c);
/*1264*/          t.transition(Comment);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1268*/  Doctype {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1270*/      char c = r.consume();
/*1271*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*1276*/          t.transition(BeforeDoctypeName);
/*   0*/          break;
/*   0*/        case '￿':
/*1279*/          t.eofError(this);
/*1280*/          t.createDoctypePending();
/*1281*/          t.doctypePending.forceQuirks = true;
/*1282*/          t.emitDoctypePending();
/*1283*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1286*/          t.error(this);
/*1287*/          t.transition(BeforeDoctypeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1291*/  BeforeDoctypeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1293*/      if (r.matchesLetter()) {
/*1294*/        t.createDoctypePending();
/*1295*/        t.transition(DoctypeName);
/*   0*/        return;
/*   0*/      } 
/*1298*/      char c = r.consume();
/*1299*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '\000':
/*1306*/          t.error(this);
/*1307*/          t.doctypePending.name.append('�');
/*1308*/          t.transition(DoctypeName);
/*   0*/          break;
/*   0*/        case '￿':
/*1311*/          t.eofError(this);
/*1312*/          t.createDoctypePending();
/*1313*/          t.doctypePending.forceQuirks = true;
/*1314*/          t.emitDoctypePending();
/*1315*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1318*/          t.createDoctypePending();
/*1319*/          t.doctypePending.name.append(c);
/*1320*/          t.transition(DoctypeName);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1324*/  DoctypeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1326*/      if (r.matchesLetter()) {
/*1327*/        String name = r.consumeLetterSequence();
/*1328*/        t.doctypePending.name.append(name.toLowerCase());
/*   0*/        return;
/*   0*/      } 
/*1331*/      char c = r.consume();
/*1332*/      switch (c) {
/*   0*/        case '>':
/*1334*/          t.emitDoctypePending();
/*1335*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*1341*/          t.transition(AfterDoctypeName);
/*   0*/          break;
/*   0*/        case '\000':
/*1344*/          t.error(this);
/*1345*/          t.doctypePending.name.append('�');
/*   0*/          break;
/*   0*/        case '￿':
/*1348*/          t.eofError(this);
/*1349*/          t.doctypePending.forceQuirks = true;
/*1350*/          t.emitDoctypePending();
/*1351*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1354*/          t.doctypePending.name.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1358*/  AfterDoctypeName {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1360*/      if (r.isEmpty()) {
/*1361*/        t.eofError(this);
/*1362*/        t.doctypePending.forceQuirks = true;
/*1363*/        t.emitDoctypePending();
/*1364*/        t.transition(Data);
/*   0*/        return;
/*   0*/      } 
/*1367*/      if (r.matchesAny(new char[] { '\t', '\n', '\f', ' ' })) {
/*1368*/        r.advance();
/*1369*/      } else if (r.matches('>')) {
/*1370*/        t.emitDoctypePending();
/*1371*/        t.advanceTransition(Data);
/*1372*/      } else if (r.matchConsumeIgnoreCase("PUBLIC")) {
/*1373*/        t.transition(AfterDoctypePublicKeyword);
/*1374*/      } else if (r.matchConsumeIgnoreCase("SYSTEM")) {
/*1375*/        t.transition(AfterDoctypeSystemKeyword);
/*   0*/      } else {
/*1377*/        t.error(this);
/*1378*/        t.doctypePending.forceQuirks = true;
/*1379*/        t.advanceTransition(BogusDoctype);
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1384*/  AfterDoctypePublicKeyword {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1386*/      char c = r.consume();
/*1387*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*1392*/          t.transition(BeforeDoctypePublicIdentifier);
/*   0*/          break;
/*   0*/        case '"':
/*1395*/          t.error(this);
/*1397*/          t.transition(DoctypePublicIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1400*/          t.error(this);
/*1402*/          t.transition(DoctypePublicIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '>':
/*1405*/          t.error(this);
/*1406*/          t.doctypePending.forceQuirks = true;
/*1407*/          t.emitDoctypePending();
/*1408*/          t.transition(Data);
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
/*1423*/  BeforeDoctypePublicIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1425*/      char c = r.consume();
/*1426*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '"':
/*1434*/          t.transition(DoctypePublicIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1438*/          t.transition(DoctypePublicIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '>':
/*1441*/          t.error(this);
/*1442*/          t.doctypePending.forceQuirks = true;
/*1443*/          t.emitDoctypePending();
/*1444*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1447*/          t.eofError(this);
/*1448*/          t.doctypePending.forceQuirks = true;
/*1449*/          t.emitDoctypePending();
/*1450*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1453*/          t.error(this);
/*1454*/          t.doctypePending.forceQuirks = true;
/*1455*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1459*/  DoctypePublicIdentifier_doubleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1461*/      char c = r.consume();
/*1462*/      switch (c) {
/*   0*/        case '"':
/*1464*/          t.transition(AfterDoctypePublicIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1467*/          t.error(this);
/*1468*/          t.doctypePending.publicIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1471*/          t.error(this);
/*1472*/          t.doctypePending.forceQuirks = true;
/*1473*/          t.emitDoctypePending();
/*1474*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1477*/          t.eofError(this);
/*1478*/          t.doctypePending.forceQuirks = true;
/*1479*/          t.emitDoctypePending();
/*1480*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1483*/          t.doctypePending.publicIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1487*/  DoctypePublicIdentifier_singleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1489*/      char c = r.consume();
/*1490*/      switch (c) {
/*   0*/        case '\'':
/*1492*/          t.transition(AfterDoctypePublicIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1495*/          t.error(this);
/*1496*/          t.doctypePending.publicIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1499*/          t.error(this);
/*1500*/          t.doctypePending.forceQuirks = true;
/*1501*/          t.emitDoctypePending();
/*1502*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1505*/          t.eofError(this);
/*1506*/          t.doctypePending.forceQuirks = true;
/*1507*/          t.emitDoctypePending();
/*1508*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1511*/          t.doctypePending.publicIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1515*/  AfterDoctypePublicIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1517*/      char c = r.consume();
/*1518*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*1523*/          t.transition(BetweenDoctypePublicAndSystemIdentifiers);
/*   0*/          break;
/*   0*/        case '>':
/*1526*/          t.emitDoctypePending();
/*1527*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*1530*/          t.error(this);
/*1532*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1535*/          t.error(this);
/*1537*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '￿':
/*1540*/          t.eofError(this);
/*1541*/          t.doctypePending.forceQuirks = true;
/*1542*/          t.emitDoctypePending();
/*1543*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1546*/          t.error(this);
/*1547*/          t.doctypePending.forceQuirks = true;
/*1548*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1552*/  BetweenDoctypePublicAndSystemIdentifiers {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1554*/      char c = r.consume();
/*1555*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '>':
/*1562*/          t.emitDoctypePending();
/*1563*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*1566*/          t.error(this);
/*1568*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1571*/          t.error(this);
/*1573*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '￿':
/*1576*/          t.eofError(this);
/*1577*/          t.doctypePending.forceQuirks = true;
/*1578*/          t.emitDoctypePending();
/*1579*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1582*/          t.error(this);
/*1583*/          t.doctypePending.forceQuirks = true;
/*1584*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1588*/  AfterDoctypeSystemKeyword {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1590*/      char c = r.consume();
/*1591*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*1596*/          t.transition(BeforeDoctypeSystemIdentifier);
/*   0*/          break;
/*   0*/        case '>':
/*1599*/          t.error(this);
/*1600*/          t.doctypePending.forceQuirks = true;
/*1601*/          t.emitDoctypePending();
/*1602*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '"':
/*1605*/          t.error(this);
/*1607*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1610*/          t.error(this);
/*1612*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '￿':
/*1615*/          t.eofError(this);
/*1616*/          t.doctypePending.forceQuirks = true;
/*1617*/          t.emitDoctypePending();
/*1618*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1621*/          t.error(this);
/*1622*/          t.doctypePending.forceQuirks = true;
/*1623*/          t.emitDoctypePending();
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1627*/  BeforeDoctypeSystemIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1629*/      char c = r.consume();
/*1630*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '"':
/*1638*/          t.transition(DoctypeSystemIdentifier_doubleQuoted);
/*   0*/          break;
/*   0*/        case '\'':
/*1642*/          t.transition(DoctypeSystemIdentifier_singleQuoted);
/*   0*/          break;
/*   0*/        case '>':
/*1645*/          t.error(this);
/*1646*/          t.doctypePending.forceQuirks = true;
/*1647*/          t.emitDoctypePending();
/*1648*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1651*/          t.eofError(this);
/*1652*/          t.doctypePending.forceQuirks = true;
/*1653*/          t.emitDoctypePending();
/*1654*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1657*/          t.error(this);
/*1658*/          t.doctypePending.forceQuirks = true;
/*1659*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1663*/  DoctypeSystemIdentifier_doubleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1665*/      char c = r.consume();
/*1666*/      switch (c) {
/*   0*/        case '"':
/*1668*/          t.transition(AfterDoctypeSystemIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1671*/          t.error(this);
/*1672*/          t.doctypePending.systemIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1675*/          t.error(this);
/*1676*/          t.doctypePending.forceQuirks = true;
/*1677*/          t.emitDoctypePending();
/*1678*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1681*/          t.eofError(this);
/*1682*/          t.doctypePending.forceQuirks = true;
/*1683*/          t.emitDoctypePending();
/*1684*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1687*/          t.doctypePending.systemIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1691*/  DoctypeSystemIdentifier_singleQuoted {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1693*/      char c = r.consume();
/*1694*/      switch (c) {
/*   0*/        case '\'':
/*1696*/          t.transition(AfterDoctypeSystemIdentifier);
/*   0*/          break;
/*   0*/        case '\000':
/*1699*/          t.error(this);
/*1700*/          t.doctypePending.systemIdentifier.append('�');
/*   0*/          break;
/*   0*/        case '>':
/*1703*/          t.error(this);
/*1704*/          t.doctypePending.forceQuirks = true;
/*1705*/          t.emitDoctypePending();
/*1706*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1709*/          t.eofError(this);
/*1710*/          t.doctypePending.forceQuirks = true;
/*1711*/          t.emitDoctypePending();
/*1712*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1715*/          t.doctypePending.systemIdentifier.append(c);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1719*/  AfterDoctypeSystemIdentifier {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1721*/      char c = r.consume();
/*1722*/      switch (c) {
/*   0*/        case '\t':
/*   0*/        case '\n':
/*   0*/        case '\f':
/*   0*/        case ' ':
/*   0*/          break;
/*   0*/        case '>':
/*1729*/          t.emitDoctypePending();
/*1730*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1733*/          t.eofError(this);
/*1734*/          t.doctypePending.forceQuirks = true;
/*1735*/          t.emitDoctypePending();
/*1736*/          t.transition(Data);
/*   0*/          break;
/*   0*/        default:
/*1739*/          t.error(this);
/*1740*/          t.transition(BogusDoctype);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1745*/  BogusDoctype {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1747*/      char c = r.consume();
/*1748*/      switch (c) {
/*   0*/        case '>':
/*1750*/          t.emitDoctypePending();
/*1751*/          t.transition(Data);
/*   0*/          break;
/*   0*/        case '￿':
/*1754*/          t.emitDoctypePending();
/*1755*/          t.transition(Data);
/*   0*/          break;
/*   0*/      } 
/*   0*/    }
/*   0*/  },
/*1763*/  CdataSection {
/*   0*/    void read(Tokeniser t, CharacterReader r) {
/*1765*/      String data = r.consumeTo("]]>");
/*1766*/      t.emit(data);
/*1767*/      r.matchConsume("]]>");
/*1768*/      t.transition(Data);
/*   0*/    }
/*   0*/  };
/*   0*/  
/*   0*/  abstract void read(Tokeniser paramTokeniser, CharacterReader paramCharacterReader);
/*   0*/  
/*1777*/  private static final String replacementStr = String.valueOf('�');
/*   0*/  
/*   0*/  private static final char nullChar = '\000';
/*   0*/  
/*   0*/  private static final char replacementChar = '�';
/*   0*/  
/*   0*/  private static final char eof = '￿';
/*   0*/}
