/*   0*/package com.fasterxml.jackson.core.filter;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonStreamContext;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.util.JsonParserDelegate;
/*   0*/import java.io.IOException;
/*   0*/import java.io.OutputStream;
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/
/*   0*/public class FilteringParserDelegate extends JsonParserDelegate {
/*   0*/  protected TokenFilter rootFilter;
/*   0*/  
/*   0*/  protected boolean _allowMultipleMatches;
/*   0*/  
/*   0*/  protected boolean _includePath;
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected boolean _includeImmediateParent;
/*   0*/  
/*   0*/  protected JsonToken _currToken;
/*   0*/  
/*   0*/  protected JsonToken _lastClearedToken;
/*   0*/  
/*   0*/  protected TokenFilterContext _headContext;
/*   0*/  
/*   0*/  protected TokenFilterContext _exposedContext;
/*   0*/  
/*   0*/  protected TokenFilter _itemFilter;
/*   0*/  
/*   0*/  protected int _matchCount;
/*   0*/  
/*   0*/  public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
/* 117*/    super(p);
/* 118*/    this.rootFilter = f;
/* 120*/    this._itemFilter = f;
/* 121*/    this._headContext = TokenFilterContext.createRootContext(f);
/* 122*/    this._includePath = includePath;
/* 123*/    this._allowMultipleMatches = allowMultipleMatches;
/*   0*/  }
/*   0*/  
/*   0*/  public TokenFilter getFilter() {
/* 132*/    return this.rootFilter;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMatchCount() {
/* 139*/    return this._matchCount;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken getCurrentToken() {
/* 148*/    return this._currToken;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken currentToken() {
/* 149*/    return this._currToken;
/*   0*/  }
/*   0*/  
/*   0*/  public final int getCurrentTokenId() {
/* 152*/    JsonToken t = this._currToken;
/* 153*/    return (t == null) ? 0 : t.id();
/*   0*/  }
/*   0*/  
/*   0*/  public final int currentTokenId() {
/* 156*/    JsonToken t = this._currToken;
/* 157*/    return (t == null) ? 0 : t.id();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasCurrentToken() {
/* 160*/    return (this._currToken != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasTokenId(int id) {
/* 162*/    JsonToken t = this._currToken;
/* 163*/    if (t == null) {
/* 164*/        return (0 == id); 
/*   0*/       }
/* 166*/    return (t.id() == id);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasToken(JsonToken t) {
/* 170*/    return (this._currToken == t);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExpectedStartArrayToken() {
/* 173*/    return (this._currToken == JsonToken.START_ARRAY);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExpectedStartObjectToken() {
/* 174*/    return (this._currToken == JsonToken.START_OBJECT);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonLocation getCurrentLocation() {
/* 176*/    return this.delegate.getCurrentLocation();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonStreamContext getParsingContext() {
/* 180*/    return _filterContext();
/*   0*/  }
/*   0*/  
/*   0*/  public String getCurrentName() throws IOException {
/* 186*/    JsonStreamContext ctxt = _filterContext();
/* 187*/    if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
/* 188*/      JsonStreamContext parent = ctxt.getParent();
/* 189*/      return (parent == null) ? null : parent.getCurrentName();
/*   0*/    } 
/* 191*/    return ctxt.getCurrentName();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearCurrentToken() {
/* 202*/    if (this._currToken != null) {
/* 203*/      this._lastClearedToken = this._currToken;
/* 204*/      this._currToken = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken getLastClearedToken() {
/* 209*/    return this._lastClearedToken;
/*   0*/  }
/*   0*/  
/*   0*/  public void overrideCurrentName(String name) {
/* 217*/    throw new UnsupportedOperationException("Can not currently override name during filtering read");
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken nextToken() throws IOException {
/*   0*/    TokenFilter f;
/*   0*/    boolean returnEnd;
/*   0*/    String name;
/* 236*/    if (!this._allowMultipleMatches && this._currToken != null && this._exposedContext == null) {
/* 238*/        if (this._currToken.isStructEnd()) {
/* 239*/          if (!this._headContext.isStartHandled()) {
/* 240*/              return this._currToken = null; 
/*   0*/             }
/* 242*/        } else if (this._currToken.isScalarValue()) {
/* 245*/          if (!this._headContext.isStartHandled() && this._itemFilter == TokenFilter.INCLUDE_ALL) {
/* 246*/              return this._currToken = null; 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 251*/    TokenFilterContext ctxt = this._exposedContext;
/* 253*/    if (ctxt != null) {
/*   0*/        while (true) {
/* 255*/          JsonToken jsonToken = ctxt.nextTokenToRead();
/* 256*/          if (jsonToken != null) {
/* 257*/            this._currToken = jsonToken;
/* 258*/            return jsonToken;
/*   0*/          } 
/* 261*/          if (ctxt == this._headContext) {
/* 262*/            this._exposedContext = null;
/* 263*/            if (ctxt.inArray()) {
/* 264*/              jsonToken = this.delegate.getCurrentToken();
/* 267*/              this._currToken = jsonToken;
/* 268*/              return jsonToken;
/*   0*/            } 
/*   0*/            break;
/*   0*/          } 
/* 283*/          ctxt = this._headContext.findChildOf(ctxt);
/* 284*/          this._exposedContext = ctxt;
/* 285*/          if (ctxt == null) {
/* 286*/              throw _constructError("Unexpected problem: chain of filtered context broken"); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 292*/    JsonToken t = this.delegate.nextToken();
/* 293*/    if (t == null) {
/* 295*/      this._currToken = t;
/* 296*/      return t;
/*   0*/    } 
/* 302*/    switch (t.id()) {
/*   0*/      case 3:
/* 304*/        f = this._itemFilter;
/* 305*/        if (f == TokenFilter.INCLUDE_ALL) {
/* 306*/          this._headContext = this._headContext.createChildArrayContext(f, true);
/* 307*/          return this._currToken = t;
/*   0*/        } 
/* 309*/        if (f == null) {
/* 310*/          this.delegate.skipChildren();
/*   0*/          break;
/*   0*/        } 
/* 314*/        f = this._headContext.checkValue(f);
/* 315*/        if (f == null) {
/* 316*/          this.delegate.skipChildren();
/*   0*/          break;
/*   0*/        } 
/* 319*/        if (f != TokenFilter.INCLUDE_ALL) {
/* 320*/            f = f.filterStartArray(); 
/*   0*/           }
/* 322*/        this._itemFilter = f;
/* 323*/        if (f == TokenFilter.INCLUDE_ALL) {
/* 324*/          this._headContext = this._headContext.createChildArrayContext(f, true);
/* 325*/          return this._currToken = t;
/*   0*/        } 
/* 327*/        this._headContext = this._headContext.createChildArrayContext(f, false);
/* 330*/        if (this._includePath) {
/* 331*/          t = _nextTokenWithBuffering(this._headContext);
/* 332*/          if (t != null) {
/* 333*/            this._currToken = t;
/* 334*/            return t;
/*   0*/          } 
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 1:
/* 340*/        f = this._itemFilter;
/* 341*/        if (f == TokenFilter.INCLUDE_ALL) {
/* 342*/          this._headContext = this._headContext.createChildObjectContext(f, true);
/* 343*/          return this._currToken = t;
/*   0*/        } 
/* 345*/        if (f == null) {
/* 346*/          this.delegate.skipChildren();
/*   0*/          break;
/*   0*/        } 
/* 350*/        f = this._headContext.checkValue(f);
/* 351*/        if (f == null) {
/* 352*/          this.delegate.skipChildren();
/*   0*/          break;
/*   0*/        } 
/* 355*/        if (f != TokenFilter.INCLUDE_ALL) {
/* 356*/            f = f.filterStartObject(); 
/*   0*/           }
/* 358*/        this._itemFilter = f;
/* 359*/        if (f == TokenFilter.INCLUDE_ALL) {
/* 360*/          this._headContext = this._headContext.createChildObjectContext(f, true);
/* 361*/          return this._currToken = t;
/*   0*/        } 
/* 363*/        this._headContext = this._headContext.createChildObjectContext(f, false);
/* 365*/        if (this._includePath) {
/* 366*/          t = _nextTokenWithBuffering(this._headContext);
/* 367*/          if (t != null) {
/* 368*/            this._currToken = t;
/* 369*/            return t;
/*   0*/          } 
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 2:
/*   0*/      case 4:
/* 379*/        returnEnd = this._headContext.isStartHandled();
/* 380*/        f = this._headContext.getFilter();
/* 381*/        if (f != null && f != TokenFilter.INCLUDE_ALL) {
/* 382*/            f.filterFinishArray(); 
/*   0*/           }
/* 384*/        this._headContext = this._headContext.getParent();
/* 385*/        this._itemFilter = this._headContext.getFilter();
/* 386*/        if (returnEnd) {
/* 387*/            return this._currToken = t; 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 5:
/* 394*/        name = this.delegate.getCurrentName();
/* 396*/        f = this._headContext.setFieldName(name);
/* 397*/        if (f == TokenFilter.INCLUDE_ALL) {
/* 398*/          this._itemFilter = f;
/* 399*/          if (!this._includePath) {
/* 402*/              if (this._includeImmediateParent && !this._headContext.isStartHandled()) {
/* 403*/                t = this._headContext.nextTokenToRead();
/* 404*/                this._exposedContext = this._headContext;
/*   0*/              }  
/*   0*/             }
/* 407*/          return this._currToken = t;
/*   0*/        } 
/* 409*/        if (f == null) {
/* 410*/          this.delegate.nextToken();
/* 411*/          this.delegate.skipChildren();
/*   0*/          break;
/*   0*/        } 
/* 414*/        f = f.includeProperty(name);
/* 415*/        if (f == null) {
/* 416*/          this.delegate.nextToken();
/* 417*/          this.delegate.skipChildren();
/*   0*/          break;
/*   0*/        } 
/* 420*/        this._itemFilter = f;
/* 421*/        if (f == TokenFilter.INCLUDE_ALL && 
/* 422*/          this._includePath) {
/* 423*/            return this._currToken = t; 
/*   0*/           }
/* 426*/        if (this._includePath) {
/* 427*/          t = _nextTokenWithBuffering(this._headContext);
/* 428*/          if (t != null) {
/* 429*/            this._currToken = t;
/* 430*/            return t;
/*   0*/          } 
/*   0*/        } 
/*   0*/        break;
/*   0*/      default:
/* 437*/        f = this._itemFilter;
/* 438*/        if (f == TokenFilter.INCLUDE_ALL) {
/* 439*/            return this._currToken = t; 
/*   0*/           }
/* 441*/        if (f != null) {
/* 442*/          f = this._headContext.checkValue(f);
/* 443*/          if (f == TokenFilter.INCLUDE_ALL || (f != null && f.includeValue(this.delegate))) {
/* 445*/              return this._currToken = t; 
/*   0*/             }
/*   0*/        } 
/*   0*/        break;
/*   0*/    } 
/* 453*/    return _nextToken2();
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonToken _nextToken2() throws IOException {
/*   0*/    JsonToken t;
/*   0*/    while (true) {
/*   0*/      boolean returnEnd;
/*   0*/      String name;
/* 466*/      t = this.delegate.nextToken();
/* 467*/      if (t == null) {
/* 468*/        this._currToken = t;
/* 469*/        return t;
/*   0*/      } 
/* 473*/      switch (t.id()) {
/*   0*/        case 3:
/* 475*/          f = this._itemFilter;
/* 476*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 477*/            this._headContext = this._headContext.createChildArrayContext(f, true);
/* 478*/            return this._currToken = t;
/*   0*/          } 
/* 480*/          if (f == null) {
/* 481*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 485*/          f = this._headContext.checkValue(f);
/* 486*/          if (f == null) {
/* 487*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 490*/          if (f != TokenFilter.INCLUDE_ALL) {
/* 491*/              f = f.filterStartArray(); 
/*   0*/             }
/* 493*/          this._itemFilter = f;
/* 494*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 495*/            this._headContext = this._headContext.createChildArrayContext(f, true);
/* 496*/            return this._currToken = t;
/*   0*/          } 
/* 498*/          this._headContext = this._headContext.createChildArrayContext(f, false);
/* 500*/          if (this._includePath) {
/* 501*/            t = _nextTokenWithBuffering(this._headContext);
/* 502*/            if (t != null) {
/* 503*/              this._currToken = t;
/* 504*/              return t;
/*   0*/            } 
/*   0*/          } 
/*   0*/          continue;
/*   0*/        case 1:
/* 510*/          f = this._itemFilter;
/* 511*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 512*/            this._headContext = this._headContext.createChildObjectContext(f, true);
/* 513*/            return this._currToken = t;
/*   0*/          } 
/* 515*/          if (f == null) {
/* 516*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 520*/          f = this._headContext.checkValue(f);
/* 521*/          if (f == null) {
/* 522*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 525*/          if (f != TokenFilter.INCLUDE_ALL) {
/* 526*/              f = f.filterStartObject(); 
/*   0*/             }
/* 528*/          this._itemFilter = f;
/* 529*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 530*/            this._headContext = this._headContext.createChildObjectContext(f, true);
/* 531*/            return this._currToken = t;
/*   0*/          } 
/* 533*/          this._headContext = this._headContext.createChildObjectContext(f, false);
/* 534*/          if (this._includePath) {
/* 535*/            t = _nextTokenWithBuffering(this._headContext);
/* 536*/            if (t != null) {
/* 537*/              this._currToken = t;
/* 538*/              return t;
/*   0*/            } 
/*   0*/          } 
/*   0*/          continue;
/*   0*/        case 2:
/*   0*/        case 4:
/* 546*/          returnEnd = this._headContext.isStartHandled();
/* 547*/          f = this._headContext.getFilter();
/* 548*/          if (f != null && f != TokenFilter.INCLUDE_ALL) {
/* 549*/              f.filterFinishArray(); 
/*   0*/             }
/* 551*/          this._headContext = this._headContext.getParent();
/* 552*/          this._itemFilter = this._headContext.getFilter();
/* 553*/          if (returnEnd) {
/* 554*/              return this._currToken = t; 
/*   0*/             }
/*   0*/          continue;
/*   0*/        case 5:
/* 561*/          name = this.delegate.getCurrentName();
/* 562*/          f = this._headContext.setFieldName(name);
/* 563*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 564*/            this._itemFilter = f;
/* 565*/            return this._currToken = t;
/*   0*/          } 
/* 567*/          if (f == null) {
/* 568*/            this.delegate.nextToken();
/* 569*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 572*/          f = f.includeProperty(name);
/* 573*/          if (f == null) {
/* 574*/            this.delegate.nextToken();
/* 575*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 578*/          this._itemFilter = f;
/* 579*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 580*/            if (this._includePath) {
/* 581*/                return this._currToken = t; 
/*   0*/               }
/*   0*/            continue;
/*   0*/          } 
/* 586*/          if (this._includePath) {
/* 587*/            t = _nextTokenWithBuffering(this._headContext);
/* 588*/            if (t != null) {
/* 589*/              this._currToken = t;
/* 590*/              return t;
/*   0*/            } 
/*   0*/          } 
/*   0*/          continue;
/*   0*/      } 
/* 597*/      TokenFilter f = this._itemFilter;
/* 598*/      if (f == TokenFilter.INCLUDE_ALL) {
/* 599*/          return this._currToken = t; 
/*   0*/         }
/* 601*/      if (f != null) {
/* 602*/        f = this._headContext.checkValue(f);
/* 603*/        if (f == TokenFilter.INCLUDE_ALL || (f != null && f.includeValue(this.delegate))) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 605*/    return this._currToken = t;
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonToken _nextTokenWithBuffering(TokenFilterContext buffRoot) throws IOException {
/*   0*/    while (true) {
/*   0*/      boolean gotEnd;
/*   0*/      String name;
/*   0*/      boolean returnEnd;
/* 622*/      JsonToken t = this.delegate.nextToken();
/* 623*/      if (t == null) {
/* 624*/          return t; 
/*   0*/         }
/* 632*/      switch (t.id()) {
/*   0*/        case 3:
/* 634*/          f = this._headContext.checkValue(this._itemFilter);
/* 635*/          if (f == null) {
/* 636*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 639*/          if (f != TokenFilter.INCLUDE_ALL) {
/* 640*/              f = f.filterStartArray(); 
/*   0*/             }
/* 642*/          this._itemFilter = f;
/* 643*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 644*/            this._headContext = this._headContext.createChildArrayContext(f, true);
/* 645*/            return _nextBuffered(buffRoot);
/*   0*/          } 
/* 647*/          this._headContext = this._headContext.createChildArrayContext(f, false);
/*   0*/          continue;
/*   0*/        case 1:
/* 651*/          f = this._itemFilter;
/* 652*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 653*/            this._headContext = this._headContext.createChildObjectContext(f, true);
/* 654*/            return t;
/*   0*/          } 
/* 656*/          if (f == null) {
/* 657*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 661*/          f = this._headContext.checkValue(f);
/* 662*/          if (f == null) {
/* 663*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 666*/          if (f != TokenFilter.INCLUDE_ALL) {
/* 667*/              f = f.filterStartObject(); 
/*   0*/             }
/* 669*/          this._itemFilter = f;
/* 670*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 671*/            this._headContext = this._headContext.createChildObjectContext(f, true);
/* 672*/            return _nextBuffered(buffRoot);
/*   0*/          } 
/* 674*/          this._headContext = this._headContext.createChildObjectContext(f, false);
/*   0*/          continue;
/*   0*/        case 2:
/*   0*/        case 4:
/* 682*/          f = this._headContext.getFilter();
/* 683*/          if (f != null && f != TokenFilter.INCLUDE_ALL) {
/* 684*/              f.filterFinishArray(); 
/*   0*/             }
/* 686*/          gotEnd = (this._headContext == buffRoot);
/* 687*/          returnEnd = (gotEnd && this._headContext.isStartHandled());
/* 689*/          this._headContext = this._headContext.getParent();
/* 690*/          this._itemFilter = this._headContext.getFilter();
/* 692*/          if (returnEnd) {
/* 693*/              return t; 
/*   0*/             }
/* 696*/          if (gotEnd || this._headContext == buffRoot) {
/* 697*/              return null; 
/*   0*/             }
/*   0*/          continue;
/*   0*/        case 5:
/* 704*/          name = this.delegate.getCurrentName();
/* 705*/          f = this._headContext.setFieldName(name);
/* 706*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 707*/            this._itemFilter = f;
/* 708*/            return _nextBuffered(buffRoot);
/*   0*/          } 
/* 710*/          if (f == null) {
/* 711*/            this.delegate.nextToken();
/* 712*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 715*/          f = f.includeProperty(name);
/* 716*/          if (f == null) {
/* 717*/            this.delegate.nextToken();
/* 718*/            this.delegate.skipChildren();
/*   0*/            continue;
/*   0*/          } 
/* 721*/          this._itemFilter = f;
/* 722*/          if (f == TokenFilter.INCLUDE_ALL) {
/* 723*/              return _nextBuffered(buffRoot); 
/*   0*/             }
/*   0*/          continue;
/*   0*/      } 
/* 729*/      TokenFilter f = this._itemFilter;
/* 730*/      if (f == TokenFilter.INCLUDE_ALL) {
/* 731*/          return _nextBuffered(buffRoot); 
/*   0*/         }
/* 733*/      if (f != null) {
/* 734*/        f = this._headContext.checkValue(f);
/* 735*/        if (f == TokenFilter.INCLUDE_ALL || (f != null && f.includeValue(this.delegate))) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 737*/    return _nextBuffered(buffRoot);
/*   0*/  }
/*   0*/  
/*   0*/  private JsonToken _nextBuffered(TokenFilterContext buffRoot) throws IOException {
/* 748*/    this._exposedContext = buffRoot;
/* 749*/    TokenFilterContext ctxt = buffRoot;
/* 750*/    JsonToken t = ctxt.nextTokenToRead();
/* 751*/    if (t != null) {
/* 752*/        return t; 
/*   0*/       }
/*   0*/    while (true) {
/* 756*/      if (ctxt == this._headContext) {
/* 757*/          throw _constructError("Internal error: failed to locate expected buffered tokens"); 
/*   0*/         }
/* 764*/      ctxt = this._exposedContext.findChildOf(ctxt);
/* 765*/      this._exposedContext = ctxt;
/* 766*/      if (ctxt == null) {
/* 767*/          throw _constructError("Unexpected problem: chain of filtered context broken"); 
/*   0*/         }
/* 769*/      t = this._exposedContext.nextTokenToRead();
/* 770*/      if (t != null) {
/* 771*/          return t; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JsonToken nextValue() throws IOException {
/* 779*/    JsonToken t = nextToken();
/* 780*/    if (t == JsonToken.FIELD_NAME) {
/* 781*/        t = nextToken(); 
/*   0*/       }
/* 783*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser skipChildren() throws IOException {
/* 794*/    if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
/* 796*/        return this; 
/*   0*/       }
/* 798*/    int open = 1;
/*   0*/    while (true) {
/* 803*/      JsonToken t = nextToken();
/* 804*/      if (t == null) {
/* 805*/          return this; 
/*   0*/         }
/* 807*/      if (t.isStructStart()) {
/* 808*/        open++;
/*   0*/        continue;
/*   0*/      } 
/* 809*/      if (t.isStructEnd() && 
/* 810*/        --open == 0) {
/* 811*/          return this; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String getText() throws IOException {
/* 823*/    return this.delegate.getText();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasTextCharacters() {
/* 824*/    return this.delegate.hasTextCharacters();
/*   0*/  }
/*   0*/  
/*   0*/  public char[] getTextCharacters() throws IOException {
/* 825*/    return this.delegate.getTextCharacters();
/*   0*/  }
/*   0*/  
/*   0*/  public int getTextLength() throws IOException {
/* 826*/    return this.delegate.getTextLength();
/*   0*/  }
/*   0*/  
/*   0*/  public int getTextOffset() throws IOException {
/* 827*/    return this.delegate.getTextOffset();
/*   0*/  }
/*   0*/  
/*   0*/  public BigInteger getBigIntegerValue() throws IOException {
/* 836*/    return this.delegate.getBigIntegerValue();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getBooleanValue() throws IOException {
/* 839*/    return this.delegate.getBooleanValue();
/*   0*/  }
/*   0*/  
/*   0*/  public byte getByteValue() throws IOException {
/* 842*/    return this.delegate.getByteValue();
/*   0*/  }
/*   0*/  
/*   0*/  public short getShortValue() throws IOException {
/* 845*/    return this.delegate.getShortValue();
/*   0*/  }
/*   0*/  
/*   0*/  public BigDecimal getDecimalValue() throws IOException {
/* 848*/    return this.delegate.getDecimalValue();
/*   0*/  }
/*   0*/  
/*   0*/  public double getDoubleValue() throws IOException {
/* 851*/    return this.delegate.getDoubleValue();
/*   0*/  }
/*   0*/  
/*   0*/  public float getFloatValue() throws IOException {
/* 854*/    return this.delegate.getFloatValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getIntValue() throws IOException {
/* 857*/    return this.delegate.getIntValue();
/*   0*/  }
/*   0*/  
/*   0*/  public long getLongValue() throws IOException {
/* 860*/    return this.delegate.getLongValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser.NumberType getNumberType() throws IOException {
/* 863*/    return this.delegate.getNumberType();
/*   0*/  }
/*   0*/  
/*   0*/  public Number getNumberValue() throws IOException {
/* 866*/    return this.delegate.getNumberValue();
/*   0*/  }
/*   0*/  
/*   0*/  public int getValueAsInt() throws IOException {
/* 874*/    return this.delegate.getValueAsInt();
/*   0*/  }
/*   0*/  
/*   0*/  public int getValueAsInt(int defaultValue) throws IOException {
/* 875*/    return this.delegate.getValueAsInt(defaultValue);
/*   0*/  }
/*   0*/  
/*   0*/  public long getValueAsLong() throws IOException {
/* 876*/    return this.delegate.getValueAsLong();
/*   0*/  }
/*   0*/  
/*   0*/  public long getValueAsLong(long defaultValue) throws IOException {
/* 877*/    return this.delegate.getValueAsLong(defaultValue);
/*   0*/  }
/*   0*/  
/*   0*/  public double getValueAsDouble() throws IOException {
/* 878*/    return this.delegate.getValueAsDouble();
/*   0*/  }
/*   0*/  
/*   0*/  public double getValueAsDouble(double defaultValue) throws IOException {
/* 879*/    return this.delegate.getValueAsDouble(defaultValue);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getValueAsBoolean() throws IOException {
/* 880*/    return this.delegate.getValueAsBoolean();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
/* 881*/    return this.delegate.getValueAsBoolean(defaultValue);
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueAsString() throws IOException {
/* 882*/    return this.delegate.getValueAsString();
/*   0*/  }
/*   0*/  
/*   0*/  public String getValueAsString(String defaultValue) throws IOException {
/* 883*/    return this.delegate.getValueAsString(defaultValue);
/*   0*/  }
/*   0*/  
/*   0*/  public Object getEmbeddedObject() throws IOException {
/* 891*/    return this.delegate.getEmbeddedObject();
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/* 892*/    return this.delegate.getBinaryValue(b64variant);
/*   0*/  }
/*   0*/  
/*   0*/  public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/* 893*/    return this.delegate.readBinaryValue(b64variant, out);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonLocation getTokenLocation() {
/* 894*/    return this.delegate.getTokenLocation();
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonStreamContext _filterContext() {
/* 903*/    if (this._exposedContext != null) {
/* 904*/        return this._exposedContext; 
/*   0*/       }
/* 906*/    return this._headContext;
/*   0*/  }
/*   0*/}
