/*   0*/package com.fasterxml.jackson.core.json;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonLocation;
/*   0*/import com.fasterxml.jackson.core.JsonParseException;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.SerializableString;
/*   0*/import com.fasterxml.jackson.core.base.ParserBase;
/*   0*/import com.fasterxml.jackson.core.io.CharTypes;
/*   0*/import com.fasterxml.jackson.core.io.IOContext;
/*   0*/import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
/*   0*/import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*   0*/import com.fasterxml.jackson.core.util.TextBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.OutputStream;
/*   0*/import java.io.Reader;
/*   0*/import java.io.Writer;
/*   0*/
/*   0*/public class ReaderBasedJsonParser extends ParserBase {
/*  22*/  protected static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
/*   0*/  
/*  26*/  protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
/*   0*/  
/*   0*/  protected Reader _reader;
/*   0*/  
/*   0*/  protected char[] _inputBuffer;
/*   0*/  
/*   0*/  protected boolean _bufferRecyclable;
/*   0*/  
/*   0*/  protected ObjectCodec _objectCodec;
/*   0*/  
/*   0*/  protected final CharsToNameCanonicalizer _symbols;
/*   0*/  
/*   0*/  protected final int _hashSeed;
/*   0*/  
/*   0*/  protected boolean _tokenIncomplete;
/*   0*/  
/*   0*/  protected long _nameStartOffset;
/*   0*/  
/*   0*/  protected int _nameStartRow;
/*   0*/  
/*   0*/  protected int _nameStartCol;
/*   0*/  
/*   0*/  public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable) {
/* 118*/    super(ctxt, features);
/* 119*/    this._reader = r;
/* 120*/    this._inputBuffer = inputBuffer;
/* 121*/    this._inputPtr = start;
/* 122*/    this._inputEnd = end;
/* 123*/    this._objectCodec = codec;
/* 124*/    this._symbols = st;
/* 125*/    this._hashSeed = st.hashSeed();
/* 126*/    this._bufferRecyclable = bufferRecyclable;
/*   0*/  }
/*   0*/  
/*   0*/  public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
/* 136*/    super(ctxt, features);
/* 137*/    this._reader = r;
/* 138*/    this._inputBuffer = ctxt.allocTokenBuffer();
/* 139*/    this._inputPtr = 0;
/* 140*/    this._inputEnd = 0;
/* 141*/    this._objectCodec = codec;
/* 142*/    this._symbols = st;
/* 143*/    this._hashSeed = st.hashSeed();
/* 144*/    this._bufferRecyclable = true;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectCodec getCodec() {
/* 153*/    return this._objectCodec;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCodec(ObjectCodec c) {
/* 154*/    this._objectCodec = c;
/*   0*/  }
/*   0*/  
/*   0*/  public int releaseBuffered(Writer w) throws IOException {
/* 158*/    int count = this._inputEnd - this._inputPtr;
/* 159*/    if (count < 1) {
/* 159*/        return 0; 
/*   0*/       }
/* 161*/    int origPtr = this._inputPtr;
/* 162*/    w.write(this._inputBuffer, origPtr, count);
/* 163*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getInputSource() {
/* 166*/    return this._reader;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected char getNextChar(String eofMsg) throws IOException {
/* 170*/    return getNextChar(eofMsg, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
/* 174*/    if (this._inputPtr >= this._inputEnd && 
/* 175*/      !_loadMore()) {
/* 176*/        _reportInvalidEOF(eofMsg, forToken); 
/*   0*/       }
/* 179*/    return this._inputBuffer[this._inputPtr++];
/*   0*/  }
/*   0*/  
/*   0*/  protected void _closeInput() throws IOException {
/* 191*/    if (this._reader != null) {
/* 192*/      if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
/* 193*/          this._reader.close(); 
/*   0*/         }
/* 195*/      this._reader = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _releaseBuffers() throws IOException {
/* 207*/    super._releaseBuffers();
/* 209*/    this._symbols.release();
/* 211*/    if (this._bufferRecyclable) {
/* 212*/      char[] buf = this._inputBuffer;
/* 213*/      if (buf != null) {
/* 214*/        this._inputBuffer = null;
/* 215*/        this._ioContext.releaseTokenBuffer(buf);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _loadMoreGuaranteed() throws IOException {
/* 227*/    if (!_loadMore()) {
/* 227*/        _reportInvalidEOF(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _loadMore() throws IOException {
/* 232*/    int bufSize = this._inputEnd;
/* 234*/    this._currInputProcessed += bufSize;
/* 235*/    this._currInputRowStart -= bufSize;
/* 240*/    this._nameStartOffset -= bufSize;
/* 242*/    if (this._reader != null) {
/* 243*/      int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
/* 244*/      if (count > 0) {
/* 245*/        this._inputPtr = 0;
/* 246*/        this._inputEnd = count;
/* 247*/        return true;
/*   0*/      } 
/* 250*/      _closeInput();
/* 252*/      if (count == 0) {
/* 253*/          throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd); 
/*   0*/         }
/*   0*/    } 
/* 256*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public final String getText() throws IOException {
/* 274*/    JsonToken t = this._currToken;
/* 275*/    if (t == JsonToken.VALUE_STRING) {
/* 276*/      if (this._tokenIncomplete) {
/* 277*/        this._tokenIncomplete = false;
/* 278*/        _finishString();
/*   0*/      } 
/* 280*/      return this._textBuffer.contentsAsString();
/*   0*/    } 
/* 282*/    return _getText2(t);
/*   0*/  }
/*   0*/  
/*   0*/  public int getText(Writer writer) throws IOException {
/* 288*/    JsonToken t = this._currToken;
/* 289*/    if (t == JsonToken.VALUE_STRING) {
/* 290*/      if (this._tokenIncomplete) {
/* 291*/        this._tokenIncomplete = false;
/* 292*/        _finishString();
/*   0*/      } 
/* 294*/      return this._textBuffer.contentsToWriter(writer);
/*   0*/    } 
/* 296*/    if (t == JsonToken.FIELD_NAME) {
/* 297*/      String n = this._parsingContext.getCurrentName();
/* 298*/      writer.write(n);
/* 299*/      return n.length();
/*   0*/    } 
/* 301*/    if (t != null) {
/* 302*/      if (t.isNumeric()) {
/* 303*/          return this._textBuffer.contentsToWriter(writer); 
/*   0*/         }
/* 305*/      char[] ch = t.asCharArray();
/* 306*/      writer.write(ch);
/* 307*/      return ch.length;
/*   0*/    } 
/* 309*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public final String getValueAsString() throws IOException {
/* 318*/    if (this._currToken == JsonToken.VALUE_STRING) {
/* 319*/      if (this._tokenIncomplete) {
/* 320*/        this._tokenIncomplete = false;
/* 321*/        _finishString();
/*   0*/      } 
/* 323*/      return this._textBuffer.contentsAsString();
/*   0*/    } 
/* 325*/    if (this._currToken == JsonToken.FIELD_NAME) {
/* 326*/        return getCurrentName(); 
/*   0*/       }
/* 328*/    return super.getValueAsString(null);
/*   0*/  }
/*   0*/  
/*   0*/  public final String getValueAsString(String defValue) throws IOException {
/* 334*/    if (this._currToken == JsonToken.VALUE_STRING) {
/* 335*/      if (this._tokenIncomplete) {
/* 336*/        this._tokenIncomplete = false;
/* 337*/        _finishString();
/*   0*/      } 
/* 339*/      return this._textBuffer.contentsAsString();
/*   0*/    } 
/* 341*/    if (this._currToken == JsonToken.FIELD_NAME) {
/* 342*/        return getCurrentName(); 
/*   0*/       }
/* 344*/    return super.getValueAsString(defValue);
/*   0*/  }
/*   0*/  
/*   0*/  protected final String _getText2(JsonToken t) {
/* 348*/    if (t == null) {
/* 349*/        return null; 
/*   0*/       }
/* 351*/    switch (t.id()) {
/*   0*/      case 5:
/* 353*/        return this._parsingContext.getCurrentName();
/*   0*/      case 6:
/*   0*/      case 7:
/*   0*/      case 8:
/* 359*/        return this._textBuffer.contentsAsString();
/*   0*/    } 
/* 361*/    return t.asString();
/*   0*/  }
/*   0*/  
/*   0*/  public final char[] getTextCharacters() throws IOException {
/* 368*/    if (this._currToken != null) {
/* 369*/      switch (this._currToken.id()) {
/*   0*/        case 5:
/* 371*/          if (!this._nameCopied) {
/* 372*/            String name = this._parsingContext.getCurrentName();
/* 373*/            int nameLen = name.length();
/* 374*/            if (this._nameCopyBuffer == null) {
/* 375*/              this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/* 376*/            } else if (this._nameCopyBuffer.length < nameLen) {
/* 377*/              this._nameCopyBuffer = new char[nameLen];
/*   0*/            } 
/* 379*/            name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/* 380*/            this._nameCopied = true;
/*   0*/          } 
/* 382*/          return this._nameCopyBuffer;
/*   0*/        case 6:
/* 384*/          if (this._tokenIncomplete) {
/* 385*/            this._tokenIncomplete = false;
/* 386*/            _finishString();
/*   0*/          } 
/*   0*/        case 7:
/*   0*/        case 8:
/* 391*/          return this._textBuffer.getTextBuffer();
/*   0*/      } 
/* 393*/      return this._currToken.asCharArray();
/*   0*/    } 
/* 396*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public final int getTextLength() throws IOException {
/* 402*/    if (this._currToken != null) {
/* 403*/      switch (this._currToken.id()) {
/*   0*/        case 5:
/* 405*/          return this._parsingContext.getCurrentName().length();
/*   0*/        case 6:
/* 407*/          if (this._tokenIncomplete) {
/* 408*/            this._tokenIncomplete = false;
/* 409*/            _finishString();
/*   0*/          } 
/*   0*/        case 7:
/*   0*/        case 8:
/* 414*/          return this._textBuffer.size();
/*   0*/      } 
/* 416*/      return (this._currToken.asCharArray()).length;
/*   0*/    } 
/* 419*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public final int getTextOffset() throws IOException {
/* 426*/    if (this._currToken != null) {
/* 427*/        switch (this._currToken.id()) {
/*   0*/          case 5:
/* 429*/            return 0;
/*   0*/          case 6:
/* 431*/            if (this._tokenIncomplete) {
/* 432*/              this._tokenIncomplete = false;
/* 433*/              _finishString();
/*   0*/            } 
/*   0*/          case 7:
/*   0*/          case 8:
/* 438*/            return this._textBuffer.getTextOffset();
/*   0*/        }  
/*   0*/       }
/* 442*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
/* 448*/    if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT && this._binaryValue != null) {
/* 449*/        return this._binaryValue; 
/*   0*/       }
/* 451*/    if (this._currToken != JsonToken.VALUE_STRING) {
/* 452*/        _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary"); 
/*   0*/       }
/* 455*/    if (this._tokenIncomplete) {
/*   0*/      try {
/* 457*/        this._binaryValue = _decodeBase64(b64variant);
/* 458*/      } catch (IllegalArgumentException iae) {
/* 459*/        throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*   0*/      } 
/* 464*/      this._tokenIncomplete = false;
/* 466*/    } else if (this._binaryValue == null) {
/* 468*/      ByteArrayBuilder builder = _getByteArrayBuilder();
/* 469*/      _decodeBase64(getText(), builder, b64variant);
/* 470*/      this._binaryValue = builder.toByteArray();
/*   0*/    } 
/* 473*/    return this._binaryValue;
/*   0*/  }
/*   0*/  
/*   0*/  public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
/* 480*/    if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
/* 481*/      byte[] b = getBinaryValue(b64variant);
/* 482*/      out.write(b);
/* 483*/      return b.length;
/*   0*/    } 
/* 486*/    byte[] buf = this._ioContext.allocBase64Buffer();
/*   0*/    try {
/* 488*/      return _readBinary(b64variant, out, buf);
/*   0*/    } finally {
/* 490*/      this._ioContext.releaseBase64Buffer(buf);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
/* 496*/    int outputPtr = 0;
/* 497*/    int outputEnd = buffer.length - 3;
/* 498*/    int outputCount = 0;
/*   0*/    while (true) {
/* 504*/      if (this._inputPtr >= this._inputEnd) {
/* 505*/          _loadMoreGuaranteed(); 
/*   0*/         }
/* 507*/      char ch = this._inputBuffer[this._inputPtr++];
/* 508*/      if (ch > ' ') {
/* 509*/        int bits = b64variant.decodeBase64Char(ch);
/* 510*/        if (bits < 0) {
/* 511*/          if (ch == '"') {
/*   0*/              break; 
/*   0*/             }
/* 514*/          bits = _decodeBase64Escape(b64variant, ch, 0);
/* 515*/          if (bits < 0) {
/*   0*/              continue; 
/*   0*/             }
/*   0*/        } 
/* 521*/        if (outputPtr > outputEnd) {
/* 522*/          outputCount += outputPtr;
/* 523*/          out.write(buffer, 0, outputPtr);
/* 524*/          outputPtr = 0;
/*   0*/        } 
/* 527*/        int decodedData = bits;
/* 531*/        if (this._inputPtr >= this._inputEnd) {
/* 532*/            _loadMoreGuaranteed(); 
/*   0*/           }
/* 534*/        ch = this._inputBuffer[this._inputPtr++];
/* 535*/        bits = b64variant.decodeBase64Char(ch);
/* 536*/        if (bits < 0) {
/* 537*/            bits = _decodeBase64Escape(b64variant, ch, 1); 
/*   0*/           }
/* 539*/        decodedData = decodedData << 6 | bits;
/* 542*/        if (this._inputPtr >= this._inputEnd) {
/* 543*/            _loadMoreGuaranteed(); 
/*   0*/           }
/* 545*/        ch = this._inputBuffer[this._inputPtr++];
/* 546*/        bits = b64variant.decodeBase64Char(ch);
/* 549*/        if (bits < 0) {
/* 550*/          if (bits != -2) {
/* 552*/            if (ch == '"') {
/* 553*/              decodedData >>= 4;
/* 554*/              buffer[outputPtr++] = (byte)decodedData;
/* 555*/              if (b64variant.usesPadding()) {
/* 556*/                this._inputPtr--;
/* 557*/                _handleBase64MissingPadding(b64variant);
/*   0*/              } 
/*   0*/              break;
/*   0*/            } 
/* 561*/            bits = _decodeBase64Escape(b64variant, ch, 2);
/*   0*/          } 
/* 563*/          if (bits == -2) {
/* 565*/            if (this._inputPtr >= this._inputEnd) {
/* 566*/                _loadMoreGuaranteed(); 
/*   0*/               }
/* 568*/            ch = this._inputBuffer[this._inputPtr++];
/* 569*/            if (!b64variant.usesPaddingChar(ch) && 
/* 570*/              _decodeBase64Escape(b64variant, ch, 3) != -2) {
/* 571*/                throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'"); 
/*   0*/               }
/* 575*/            decodedData >>= 4;
/* 576*/            buffer[outputPtr++] = (byte)decodedData;
/*   0*/            continue;
/*   0*/          } 
/*   0*/        } 
/* 581*/        decodedData = decodedData << 6 | bits;
/* 583*/        if (this._inputPtr >= this._inputEnd) {
/* 584*/            _loadMoreGuaranteed(); 
/*   0*/           }
/* 586*/        ch = this._inputBuffer[this._inputPtr++];
/* 587*/        bits = b64variant.decodeBase64Char(ch);
/* 588*/        if (bits < 0) {
/* 589*/          if (bits != -2) {
/* 591*/            if (ch == '"') {
/* 592*/              decodedData >>= 2;
/* 593*/              buffer[outputPtr++] = (byte)(decodedData >> 8);
/* 594*/              buffer[outputPtr++] = (byte)decodedData;
/* 595*/              if (b64variant.usesPadding()) {
/* 596*/                this._inputPtr--;
/* 597*/                _handleBase64MissingPadding(b64variant);
/*   0*/              } 
/*   0*/              break;
/*   0*/            } 
/* 601*/            bits = _decodeBase64Escape(b64variant, ch, 3);
/*   0*/          } 
/* 603*/          if (bits == -2) {
/* 610*/            decodedData >>= 2;
/* 611*/            buffer[outputPtr++] = (byte)(decodedData >> 8);
/* 612*/            buffer[outputPtr++] = (byte)decodedData;
/*   0*/            continue;
/*   0*/          } 
/*   0*/        } 
/* 617*/        decodedData = decodedData << 6 | bits;
/* 618*/        buffer[outputPtr++] = (byte)(decodedData >> 16);
/* 619*/        buffer[outputPtr++] = (byte)(decodedData >> 8);
/* 620*/        buffer[outputPtr++] = (byte)decodedData;
/*   0*/      } 
/*   0*/    } 
/* 622*/    this._tokenIncomplete = false;
/* 623*/    if (outputPtr > 0) {
/* 624*/      outputCount += outputPtr;
/* 625*/      out.write(buffer, 0, outputPtr);
/*   0*/    } 
/* 627*/    return outputCount;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonToken nextToken() throws IOException {
/*   0*/    JsonToken t;
/* 647*/    if (this._currToken == JsonToken.FIELD_NAME) {
/* 648*/        return _nextAfterName(); 
/*   0*/       }
/* 652*/    this._numTypesValid = 0;
/* 653*/    if (this._tokenIncomplete) {
/* 654*/        _skipString(); 
/*   0*/       }
/* 656*/    int i = _skipWSOrEnd();
/* 657*/    if (i < 0) {
/* 660*/      close();
/* 661*/      return this._currToken = null;
/*   0*/    } 
/* 664*/    this._binaryValue = null;
/* 667*/    if (i == 93 || i == 125) {
/* 668*/      _closeScope(i);
/* 669*/      return this._currToken;
/*   0*/    } 
/* 673*/    if (this._parsingContext.expectComma()) {
/* 674*/      i = _skipComma(i);
/* 677*/      if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/* 678*/        i == 93 || i == 125)) {
/* 679*/        _closeScope(i);
/* 680*/        return this._currToken;
/*   0*/      } 
/*   0*/    } 
/* 688*/    boolean inObject = this._parsingContext.inObject();
/* 689*/    if (inObject) {
/* 691*/      _updateNameLocation();
/* 692*/      String name = (i == 34) ? _parseName() : _handleOddName(i);
/* 693*/      this._parsingContext.setCurrentName(name);
/* 694*/      this._currToken = JsonToken.FIELD_NAME;
/* 695*/      i = _skipColon();
/*   0*/    } 
/* 697*/    _updateLocation();
/* 703*/    switch (i) {
/*   0*/      case 34:
/* 705*/        this._tokenIncomplete = true;
/* 706*/        t = JsonToken.VALUE_STRING;
/*   0*/        break;
/*   0*/      case 91:
/* 709*/        if (!inObject) {
/* 710*/            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol); 
/*   0*/           }
/* 712*/        t = JsonToken.START_ARRAY;
/*   0*/        break;
/*   0*/      case 123:
/* 715*/        if (!inObject) {
/* 716*/            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol); 
/*   0*/           }
/* 718*/        t = JsonToken.START_OBJECT;
/*   0*/        break;
/*   0*/      case 125:
/* 723*/        _reportUnexpectedChar(i, "expected a value");
/*   0*/      case 116:
/* 725*/        _matchTrue();
/* 726*/        t = JsonToken.VALUE_TRUE;
/*   0*/        break;
/*   0*/      case 102:
/* 729*/        _matchFalse();
/* 730*/        t = JsonToken.VALUE_FALSE;
/*   0*/        break;
/*   0*/      case 110:
/* 733*/        _matchNull();
/* 734*/        t = JsonToken.VALUE_NULL;
/*   0*/        break;
/*   0*/      case 45:
/* 742*/        t = _parseNegNumber();
/*   0*/        break;
/*   0*/      case 48:
/*   0*/      case 49:
/*   0*/      case 50:
/*   0*/      case 51:
/*   0*/      case 52:
/*   0*/      case 53:
/*   0*/      case 54:
/*   0*/      case 55:
/*   0*/      case 56:
/*   0*/      case 57:
/* 754*/        t = _parsePosNumber(i);
/*   0*/        break;
/*   0*/      default:
/* 757*/        t = _handleOddValue(i);
/*   0*/        break;
/*   0*/    } 
/* 761*/    if (inObject) {
/* 762*/      this._nextToken = t;
/* 763*/      return this._currToken;
/*   0*/    } 
/* 765*/    this._currToken = t;
/* 766*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  private final JsonToken _nextAfterName() {
/* 771*/    this._nameCopied = false;
/* 772*/    JsonToken t = this._nextToken;
/* 773*/    this._nextToken = null;
/* 778*/    if (t == JsonToken.START_ARRAY) {
/* 779*/      this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/* 780*/    } else if (t == JsonToken.START_OBJECT) {
/* 781*/      this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*   0*/    } 
/* 783*/    return this._currToken = t;
/*   0*/  }
/*   0*/  
/*   0*/  public void finishToken() throws IOException {
/* 788*/    if (this._tokenIncomplete) {
/* 789*/      this._tokenIncomplete = false;
/* 790*/      _finishString();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean nextFieldName(SerializableString sstr) throws IOException {
/* 806*/    this._numTypesValid = 0;
/* 807*/    if (this._currToken == JsonToken.FIELD_NAME) {
/* 808*/      _nextAfterName();
/* 809*/      return false;
/*   0*/    } 
/* 811*/    if (this._tokenIncomplete) {
/* 812*/        _skipString(); 
/*   0*/       }
/* 814*/    int i = _skipWSOrEnd();
/* 815*/    if (i < 0) {
/* 816*/      close();
/* 817*/      this._currToken = null;
/* 818*/      return false;
/*   0*/    } 
/* 820*/    this._binaryValue = null;
/* 823*/    if (i == 93 || i == 125) {
/* 824*/      _closeScope(i);
/* 825*/      return false;
/*   0*/    } 
/* 828*/    if (this._parsingContext.expectComma()) {
/* 829*/      i = _skipComma(i);
/* 832*/      if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/* 833*/        i == 93 || i == 125)) {
/* 834*/        _closeScope(i);
/* 835*/        return false;
/*   0*/      } 
/*   0*/    } 
/* 840*/    if (!this._parsingContext.inObject()) {
/* 841*/      _updateLocation();
/* 842*/      _nextTokenNotInObject(i);
/* 843*/      return false;
/*   0*/    } 
/* 846*/    _updateNameLocation();
/* 847*/    if (i == 34) {
/* 849*/      char[] nameChars = sstr.asQuotedChars();
/* 850*/      int len = nameChars.length;
/* 853*/      if (this._inputPtr + len + 4 < this._inputEnd) {
/* 855*/        int end = this._inputPtr + len;
/* 856*/        if (this._inputBuffer[end] == '"') {
/* 857*/          int offset = 0;
/* 858*/          int ptr = this._inputPtr;
/*   0*/          while (true) {
/* 860*/            if (ptr == end) {
/* 861*/              this._parsingContext.setCurrentName(sstr.getValue());
/* 862*/              _isNextTokenNameYes(_skipColonFast(ptr + 1));
/* 863*/              return true;
/*   0*/            } 
/* 865*/            if (nameChars[offset] != this._inputBuffer[ptr]) {
/*   0*/                break; 
/*   0*/               }
/* 868*/            offset++;
/* 869*/            ptr++;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 874*/    return _isNextTokenNameMaybe(i, sstr.getValue());
/*   0*/  }
/*   0*/  
/*   0*/  public String nextFieldName() throws IOException {
/*   0*/    JsonToken t;
/* 882*/    this._numTypesValid = 0;
/* 883*/    if (this._currToken == JsonToken.FIELD_NAME) {
/* 884*/      _nextAfterName();
/* 885*/      return null;
/*   0*/    } 
/* 887*/    if (this._tokenIncomplete) {
/* 888*/        _skipString(); 
/*   0*/       }
/* 890*/    int i = _skipWSOrEnd();
/* 891*/    if (i < 0) {
/* 892*/      close();
/* 893*/      this._currToken = null;
/* 894*/      return null;
/*   0*/    } 
/* 896*/    this._binaryValue = null;
/* 897*/    if (i == 93 || i == 125) {
/* 898*/      _closeScope(i);
/* 899*/      return null;
/*   0*/    } 
/* 901*/    if (this._parsingContext.expectComma()) {
/* 902*/      i = _skipComma(i);
/* 903*/      if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (
/* 904*/        i == 93 || i == 125)) {
/* 905*/        _closeScope(i);
/* 906*/        return null;
/*   0*/      } 
/*   0*/    } 
/* 910*/    if (!this._parsingContext.inObject()) {
/* 911*/      _updateLocation();
/* 912*/      _nextTokenNotInObject(i);
/* 913*/      return null;
/*   0*/    } 
/* 916*/    _updateNameLocation();
/* 917*/    String name = (i == 34) ? _parseName() : _handleOddName(i);
/* 918*/    this._parsingContext.setCurrentName(name);
/* 919*/    this._currToken = JsonToken.FIELD_NAME;
/* 920*/    i = _skipColon();
/* 922*/    _updateLocation();
/* 923*/    if (i == 34) {
/* 924*/      this._tokenIncomplete = true;
/* 925*/      this._nextToken = JsonToken.VALUE_STRING;
/* 926*/      return name;
/*   0*/    } 
/* 933*/    switch (i) {
/*   0*/      case 45:
/* 935*/        t = _parseNegNumber();
/*   0*/        break;
/*   0*/      case 48:
/*   0*/      case 49:
/*   0*/      case 50:
/*   0*/      case 51:
/*   0*/      case 52:
/*   0*/      case 53:
/*   0*/      case 54:
/*   0*/      case 55:
/*   0*/      case 56:
/*   0*/      case 57:
/* 947*/        t = _parsePosNumber(i);
/*   0*/        break;
/*   0*/      case 102:
/* 950*/        _matchFalse();
/* 951*/        t = JsonToken.VALUE_FALSE;
/*   0*/        break;
/*   0*/      case 110:
/* 954*/        _matchNull();
/* 955*/        t = JsonToken.VALUE_NULL;
/*   0*/        break;
/*   0*/      case 116:
/* 958*/        _matchTrue();
/* 959*/        t = JsonToken.VALUE_TRUE;
/*   0*/        break;
/*   0*/      case 91:
/* 962*/        t = JsonToken.START_ARRAY;
/*   0*/        break;
/*   0*/      case 123:
/* 965*/        t = JsonToken.START_OBJECT;
/*   0*/        break;
/*   0*/      default:
/* 968*/        t = _handleOddValue(i);
/*   0*/        break;
/*   0*/    } 
/* 971*/    this._nextToken = t;
/* 972*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _isNextTokenNameYes(int i) throws IOException {
/* 977*/    this._currToken = JsonToken.FIELD_NAME;
/* 978*/    _updateLocation();
/* 980*/    switch (i) {
/*   0*/      case 34:
/* 982*/        this._tokenIncomplete = true;
/* 983*/        this._nextToken = JsonToken.VALUE_STRING;
/*   0*/        return;
/*   0*/      case 91:
/* 986*/        this._nextToken = JsonToken.START_ARRAY;
/*   0*/        return;
/*   0*/      case 123:
/* 989*/        this._nextToken = JsonToken.START_OBJECT;
/*   0*/        return;
/*   0*/      case 116:
/* 992*/        _matchToken("true", 1);
/* 993*/        this._nextToken = JsonToken.VALUE_TRUE;
/*   0*/        return;
/*   0*/      case 102:
/* 996*/        _matchToken("false", 1);
/* 997*/        this._nextToken = JsonToken.VALUE_FALSE;
/*   0*/        return;
/*   0*/      case 110:
/*1000*/        _matchToken("null", 1);
/*1001*/        this._nextToken = JsonToken.VALUE_NULL;
/*   0*/        return;
/*   0*/      case 45:
/*1004*/        this._nextToken = _parseNegNumber();
/*   0*/        return;
/*   0*/      case 48:
/*   0*/      case 49:
/*   0*/      case 50:
/*   0*/      case 51:
/*   0*/      case 52:
/*   0*/      case 53:
/*   0*/      case 54:
/*   0*/      case 55:
/*   0*/      case 56:
/*   0*/      case 57:
/*1016*/        this._nextToken = _parsePosNumber(i);
/*   0*/        return;
/*   0*/    } 
/*1019*/    this._nextToken = _handleOddValue(i);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _isNextTokenNameMaybe(int i, String nameToMatch) throws IOException {
/*   0*/    JsonToken t;
/*1025*/    String name = (i == 34) ? _parseName() : _handleOddName(i);
/*1026*/    this._parsingContext.setCurrentName(name);
/*1027*/    this._currToken = JsonToken.FIELD_NAME;
/*1028*/    i = _skipColon();
/*1029*/    _updateLocation();
/*1030*/    if (i == 34) {
/*1031*/      this._tokenIncomplete = true;
/*1032*/      this._nextToken = JsonToken.VALUE_STRING;
/*1033*/      return nameToMatch.equals(name);
/*   0*/    } 
/*1037*/    switch (i) {
/*   0*/      case 45:
/*1039*/        t = _parseNegNumber();
/*   0*/        break;
/*   0*/      case 48:
/*   0*/      case 49:
/*   0*/      case 50:
/*   0*/      case 51:
/*   0*/      case 52:
/*   0*/      case 53:
/*   0*/      case 54:
/*   0*/      case 55:
/*   0*/      case 56:
/*   0*/      case 57:
/*1051*/        t = _parsePosNumber(i);
/*   0*/        break;
/*   0*/      case 102:
/*1054*/        _matchFalse();
/*1055*/        t = JsonToken.VALUE_FALSE;
/*   0*/        break;
/*   0*/      case 110:
/*1058*/        _matchNull();
/*1059*/        t = JsonToken.VALUE_NULL;
/*   0*/        break;
/*   0*/      case 116:
/*1062*/        _matchTrue();
/*1063*/        t = JsonToken.VALUE_TRUE;
/*   0*/        break;
/*   0*/      case 91:
/*1066*/        t = JsonToken.START_ARRAY;
/*   0*/        break;
/*   0*/      case 123:
/*1069*/        t = JsonToken.START_OBJECT;
/*   0*/        break;
/*   0*/      default:
/*1072*/        t = _handleOddValue(i);
/*   0*/        break;
/*   0*/    } 
/*1075*/    this._nextToken = t;
/*1076*/    return nameToMatch.equals(name);
/*   0*/  }
/*   0*/  
/*   0*/  private final JsonToken _nextTokenNotInObject(int i) throws IOException {
/*1081*/    if (i == 34) {
/*1082*/      this._tokenIncomplete = true;
/*1083*/      return this._currToken = JsonToken.VALUE_STRING;
/*   0*/    } 
/*1085*/    switch (i) {
/*   0*/      case 91:
/*1087*/        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*1088*/        return this._currToken = JsonToken.START_ARRAY;
/*   0*/      case 123:
/*1090*/        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*1091*/        return this._currToken = JsonToken.START_OBJECT;
/*   0*/      case 116:
/*1093*/        _matchToken("true", 1);
/*1094*/        return this._currToken = JsonToken.VALUE_TRUE;
/*   0*/      case 102:
/*1096*/        _matchToken("false", 1);
/*1097*/        return this._currToken = JsonToken.VALUE_FALSE;
/*   0*/      case 110:
/*1099*/        _matchToken("null", 1);
/*1100*/        return this._currToken = JsonToken.VALUE_NULL;
/*   0*/      case 45:
/*1102*/        return this._currToken = _parseNegNumber();
/*   0*/      case 48:
/*   0*/      case 49:
/*   0*/      case 50:
/*   0*/      case 51:
/*   0*/      case 52:
/*   0*/      case 53:
/*   0*/      case 54:
/*   0*/      case 55:
/*   0*/      case 56:
/*   0*/      case 57:
/*1117*/        return this._currToken = _parsePosNumber(i);
/*   0*/      case 44:
/*   0*/      case 93:
/*1128*/        if (isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
/*1129*/          this._inputPtr--;
/*1130*/          return this._currToken = JsonToken.VALUE_NULL;
/*   0*/        } 
/*   0*/        break;
/*   0*/    } 
/*1133*/    return this._currToken = _handleOddValue(i);
/*   0*/  }
/*   0*/  
/*   0*/  public final String nextTextValue() throws IOException {
/*1140*/    if (this._currToken == JsonToken.FIELD_NAME) {
/*1141*/      this._nameCopied = false;
/*1142*/      JsonToken t = this._nextToken;
/*1143*/      this._nextToken = null;
/*1144*/      this._currToken = t;
/*1145*/      if (t == JsonToken.VALUE_STRING) {
/*1146*/        if (this._tokenIncomplete) {
/*1147*/          this._tokenIncomplete = false;
/*1148*/          _finishString();
/*   0*/        } 
/*1150*/        return this._textBuffer.contentsAsString();
/*   0*/      } 
/*1152*/      if (t == JsonToken.START_ARRAY) {
/*1153*/        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*1154*/      } else if (t == JsonToken.START_OBJECT) {
/*1155*/        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*   0*/      } 
/*1157*/      return null;
/*   0*/    } 
/*1160*/    return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
/*   0*/  }
/*   0*/  
/*   0*/  public final int nextIntValue(int defaultValue) throws IOException {
/*1167*/    if (this._currToken == JsonToken.FIELD_NAME) {
/*1168*/      this._nameCopied = false;
/*1169*/      JsonToken t = this._nextToken;
/*1170*/      this._nextToken = null;
/*1171*/      this._currToken = t;
/*1172*/      if (t == JsonToken.VALUE_NUMBER_INT) {
/*1173*/          return getIntValue(); 
/*   0*/         }
/*1175*/      if (t == JsonToken.START_ARRAY) {
/*1176*/        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*1177*/      } else if (t == JsonToken.START_OBJECT) {
/*1178*/        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*   0*/      } 
/*1180*/      return defaultValue;
/*   0*/    } 
/*1183*/    return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
/*   0*/  }
/*   0*/  
/*   0*/  public final long nextLongValue(long defaultValue) throws IOException {
/*1190*/    if (this._currToken == JsonToken.FIELD_NAME) {
/*1191*/      this._nameCopied = false;
/*1192*/      JsonToken t = this._nextToken;
/*1193*/      this._nextToken = null;
/*1194*/      this._currToken = t;
/*1195*/      if (t == JsonToken.VALUE_NUMBER_INT) {
/*1196*/          return getLongValue(); 
/*   0*/         }
/*1198*/      if (t == JsonToken.START_ARRAY) {
/*1199*/        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*1200*/      } else if (t == JsonToken.START_OBJECT) {
/*1201*/        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*   0*/      } 
/*1203*/      return defaultValue;
/*   0*/    } 
/*1206*/    return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
/*   0*/  }
/*   0*/  
/*   0*/  public final Boolean nextBooleanValue() throws IOException {
/*1213*/    if (this._currToken == JsonToken.FIELD_NAME) {
/*1214*/      this._nameCopied = false;
/*1215*/      JsonToken jsonToken = this._nextToken;
/*1216*/      this._nextToken = null;
/*1217*/      this._currToken = jsonToken;
/*1218*/      if (jsonToken == JsonToken.VALUE_TRUE) {
/*1219*/          return Boolean.TRUE; 
/*   0*/         }
/*1221*/      if (jsonToken == JsonToken.VALUE_FALSE) {
/*1222*/          return Boolean.FALSE; 
/*   0*/         }
/*1224*/      if (jsonToken == JsonToken.START_ARRAY) {
/*1225*/        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*1226*/      } else if (jsonToken == JsonToken.START_OBJECT) {
/*1227*/        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*   0*/      } 
/*1229*/      return null;
/*   0*/    } 
/*1231*/    JsonToken t = nextToken();
/*1232*/    if (t != null) {
/*1233*/      int id = t.id();
/*1234*/      if (id == 9) {
/*1234*/          return Boolean.TRUE; 
/*   0*/         }
/*1235*/      if (id == 10) {
/*1235*/          return Boolean.FALSE; 
/*   0*/         }
/*   0*/    } 
/*1237*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonToken _parsePosNumber(int ch) throws IOException {
/*1268*/    int ptr = this._inputPtr;
/*1269*/    int startPtr = ptr - 1;
/*1270*/    int inputLen = this._inputEnd;
/*1273*/    if (ch == 48) {
/*1274*/        return _parseNumber2(false, startPtr); 
/*   0*/       }
/*1283*/    int intLen = 1;
/*   0*/    while (true) {
/*1288*/      if (ptr >= inputLen) {
/*1289*/        this._inputPtr = startPtr;
/*1290*/        return _parseNumber2(false, startPtr);
/*   0*/      } 
/*1292*/      ch = this._inputBuffer[ptr++];
/*1293*/      if (ch < 48 || ch > 57) {
/*   0*/          break; 
/*   0*/         }
/*1296*/      intLen++;
/*   0*/    } 
/*1298*/    if (ch == 46 || ch == 101 || ch == 69) {
/*1299*/      this._inputPtr = ptr;
/*1300*/      return _parseFloat(ch, startPtr, ptr, false, intLen);
/*   0*/    } 
/*1304*/    this._inputPtr = --ptr;
/*1306*/    if (this._parsingContext.inRoot()) {
/*1307*/        _verifyRootSpace(ch); 
/*   0*/       }
/*1309*/    int len = ptr - startPtr;
/*1310*/    this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*1311*/    return resetInt(false, intLen);
/*   0*/  }
/*   0*/  
/*   0*/  private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen) throws IOException {
/*1317*/    int inputLen = this._inputEnd;
/*1318*/    int fractLen = 0;
/*1321*/    if (ch == 46) {
/*   0*/      while (true) {
/*1324*/        if (ptr >= inputLen) {
/*1325*/            return _parseNumber2(neg, startPtr); 
/*   0*/           }
/*1327*/        ch = this._inputBuffer[ptr++];
/*1328*/        if (ch < 48 || ch > 57) {
/*   0*/            break; 
/*   0*/           }
/*1331*/        fractLen++;
/*   0*/      } 
/*1334*/      if (fractLen == 0) {
/*1335*/          reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit"); 
/*   0*/         }
/*   0*/    } 
/*1338*/    int expLen = 0;
/*1339*/    if (ch == 101 || ch == 69) {
/*1340*/      if (ptr >= inputLen) {
/*1341*/        this._inputPtr = startPtr;
/*1342*/        return _parseNumber2(neg, startPtr);
/*   0*/      } 
/*1345*/      ch = this._inputBuffer[ptr++];
/*1346*/      if (ch == 45 || ch == 43) {
/*1347*/        if (ptr >= inputLen) {
/*1348*/          this._inputPtr = startPtr;
/*1349*/          return _parseNumber2(neg, startPtr);
/*   0*/        } 
/*1351*/        ch = this._inputBuffer[ptr++];
/*   0*/      } 
/*1353*/      while (ch <= 57 && ch >= 48) {
/*1354*/        expLen++;
/*1355*/        if (ptr >= inputLen) {
/*1356*/          this._inputPtr = startPtr;
/*1357*/          return _parseNumber2(neg, startPtr);
/*   0*/        } 
/*1359*/        ch = this._inputBuffer[ptr++];
/*   0*/      } 
/*1362*/      if (expLen == 0) {
/*1363*/          reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit"); 
/*   0*/         }
/*   0*/    } 
/*1367*/    this._inputPtr = --ptr;
/*1369*/    if (this._parsingContext.inRoot()) {
/*1370*/        _verifyRootSpace(ch); 
/*   0*/       }
/*1372*/    int len = ptr - startPtr;
/*1373*/    this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*1375*/    return resetFloat(neg, intLen, fractLen, expLen);
/*   0*/  }
/*   0*/  
/*   0*/  protected final JsonToken _parseNegNumber() throws IOException {
/*1380*/    int ptr = this._inputPtr;
/*1381*/    int startPtr = ptr - 1;
/*1382*/    int inputLen = this._inputEnd;
/*1384*/    if (ptr >= inputLen) {
/*1385*/        return _parseNumber2(true, startPtr); 
/*   0*/       }
/*1387*/    int ch = this._inputBuffer[ptr++];
/*1389*/    if (ch > 57 || ch < 48) {
/*1390*/      this._inputPtr = ptr;
/*1391*/      return _handleInvalidNumberStart(ch, true);
/*   0*/    } 
/*1394*/    if (ch == 48) {
/*1395*/        return _parseNumber2(true, startPtr); 
/*   0*/       }
/*1397*/    int intLen = 1;
/*   0*/    while (true) {
/*1402*/      if (ptr >= inputLen) {
/*1403*/          return _parseNumber2(true, startPtr); 
/*   0*/         }
/*1405*/      ch = this._inputBuffer[ptr++];
/*1406*/      if (ch < 48 || ch > 57) {
/*   0*/          break; 
/*   0*/         }
/*1409*/      intLen++;
/*   0*/    } 
/*1412*/    if (ch == 46 || ch == 101 || ch == 69) {
/*1413*/      this._inputPtr = ptr;
/*1414*/      return _parseFloat(ch, startPtr, ptr, true, intLen);
/*   0*/    } 
/*1417*/    this._inputPtr = --ptr;
/*1418*/    if (this._parsingContext.inRoot()) {
/*1419*/        _verifyRootSpace(ch); 
/*   0*/       }
/*1421*/    int len = ptr - startPtr;
/*1422*/    this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*1423*/    return resetInt(true, intLen);
/*   0*/  }
/*   0*/  
/*   0*/  private final JsonToken _parseNumber2(boolean neg, int startPtr) throws IOException {
/*1435*/    this._inputPtr = neg ? (startPtr + 1) : startPtr;
/*1436*/    char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*1437*/    int outPtr = 0;
/*1440*/    if (neg) {
/*1441*/        outBuf[outPtr++] = '-'; 
/*   0*/       }
/*1445*/    int intLen = 0;
/*1446*/    char c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("No digit following minus sign", JsonToken.VALUE_NUMBER_INT);
/*1448*/    if (c == '0') {
/*1449*/        c = _verifyNoLeadingZeroes(); 
/*   0*/       }
/*   0*/    boolean eof = false;
/*1455*/    while (c >= '0' && c <= '9') {
/*1456*/      intLen++;
/*1457*/      if (outPtr >= outBuf.length) {
/*1458*/        outBuf = this._textBuffer.finishCurrentSegment();
/*1459*/        outPtr = 0;
/*   0*/      } 
/*1461*/      outBuf[outPtr++] = c;
/*1462*/      if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*1464*/        c = Character.MIN_VALUE;
/*1465*/        eof = true;
/*   0*/        break;
/*   0*/      } 
/*1468*/      c = this._inputBuffer[this._inputPtr++];
/*   0*/    } 
/*1471*/    if (intLen == 0) {
/*1472*/        return _handleInvalidNumberStart(c, neg); 
/*   0*/       }
/*1475*/    int fractLen = 0;
/*1477*/    if (c == '.') {
/*1478*/      if (outPtr >= outBuf.length) {
/*1479*/        outBuf = this._textBuffer.finishCurrentSegment();
/*1480*/        outPtr = 0;
/*   0*/      } 
/*1482*/      outBuf[outPtr++] = c;
/*   0*/      while (true) {
/*1486*/        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*1487*/          eof = true;
/*   0*/          break;
/*   0*/        } 
/*1490*/        c = this._inputBuffer[this._inputPtr++];
/*1491*/        if (c < '0' || c > '9') {
/*   0*/            break; 
/*   0*/           }
/*1494*/        fractLen++;
/*1495*/        if (outPtr >= outBuf.length) {
/*1496*/          outBuf = this._textBuffer.finishCurrentSegment();
/*1497*/          outPtr = 0;
/*   0*/        } 
/*1499*/        outBuf[outPtr++] = c;
/*   0*/      } 
/*1502*/      if (fractLen == 0) {
/*1503*/          reportUnexpectedNumberChar(c, "Decimal point not followed by a digit"); 
/*   0*/         }
/*   0*/    } 
/*1507*/    int expLen = 0;
/*1508*/    if (c == 'e' || c == 'E') {
/*1509*/      if (outPtr >= outBuf.length) {
/*1510*/        outBuf = this._textBuffer.finishCurrentSegment();
/*1511*/        outPtr = 0;
/*   0*/      } 
/*1513*/      outBuf[outPtr++] = c;
/*1515*/      c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("expected a digit for number exponent");
/*1518*/      if (c == '-' || c == '+') {
/*1519*/        if (outPtr >= outBuf.length) {
/*1520*/          outBuf = this._textBuffer.finishCurrentSegment();
/*1521*/          outPtr = 0;
/*   0*/        } 
/*1523*/        outBuf[outPtr++] = c;
/*1525*/        c = (this._inputPtr < this._inputEnd) ? this._inputBuffer[this._inputPtr++] : getNextChar("expected a digit for number exponent");
/*   0*/      } 
/*1530*/      while (c <= '9' && c >= '0') {
/*1531*/        expLen++;
/*1532*/        if (outPtr >= outBuf.length) {
/*1533*/          outBuf = this._textBuffer.finishCurrentSegment();
/*1534*/          outPtr = 0;
/*   0*/        } 
/*1536*/        outBuf[outPtr++] = c;
/*1537*/        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*1538*/          eof = true;
/*   0*/          break;
/*   0*/        } 
/*1541*/        c = this._inputBuffer[this._inputPtr++];
/*   0*/      } 
/*1544*/      if (expLen == 0) {
/*1545*/          reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit"); 
/*   0*/         }
/*   0*/    } 
/*1550*/    if (!eof) {
/*1551*/      this._inputPtr--;
/*1552*/      if (this._parsingContext.inRoot()) {
/*1553*/          _verifyRootSpace(c); 
/*   0*/         }
/*   0*/    } 
/*1556*/    this._textBuffer.setCurrentLength(outPtr);
/*1558*/    return reset(neg, intLen, fractLen, expLen);
/*   0*/  }
/*   0*/  
/*   0*/  private final char _verifyNoLeadingZeroes() throws IOException {
/*1568*/    if (this._inputPtr < this._inputEnd) {
/*1569*/      char ch = this._inputBuffer[this._inputPtr];
/*1571*/      if (ch < '0' || ch > '9') {
/*1572*/          return '0'; 
/*   0*/         }
/*   0*/    } 
/*1576*/    return _verifyNLZ2();
/*   0*/  }
/*   0*/  
/*   0*/  private char _verifyNLZ2() throws IOException {
/*1581*/    if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*1582*/        return '0'; 
/*   0*/       }
/*1584*/    char ch = this._inputBuffer[this._inputPtr];
/*1585*/    if (ch < '0' || ch > '9') {
/*1586*/        return '0'; 
/*   0*/       }
/*1588*/    if (!isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
/*1589*/        reportInvalidNumber("Leading zeroes not allowed"); 
/*   0*/       }
/*1592*/    this._inputPtr++;
/*1593*/    if (ch == '0') {
/*1594*/        while (this._inputPtr < this._inputEnd || _loadMore()) {
/*1595*/          ch = this._inputBuffer[this._inputPtr];
/*1596*/          if (ch < '0' || ch > '9') {
/*1597*/              return '0'; 
/*   0*/             }
/*1599*/          this._inputPtr++;
/*1600*/          if (ch != '0') {
/*   0*/              break; 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/*1605*/    return ch;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonToken _handleInvalidNumberStart(int ch, boolean negative) throws IOException {
/*1614*/    if (ch == 73) {
/*1615*/      if (this._inputPtr >= this._inputEnd && 
/*1616*/        !_loadMore()) {
/*1617*/          _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT); 
/*   0*/         }
/*1620*/      ch = this._inputBuffer[this._inputPtr++];
/*1621*/      if (ch == 78) {
/*1622*/        String match = negative ? "-INF" : "+INF";
/*1623*/        _matchToken(match, 3);
/*1624*/        if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/*1625*/            return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY); 
/*   0*/           }
/*1627*/        _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*1628*/      } else if (ch == 110) {
/*1629*/        String match = negative ? "-Infinity" : "+Infinity";
/*1630*/        _matchToken(match, 3);
/*1631*/        if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/*1632*/            return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY); 
/*   0*/           }
/*1634*/        _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*   0*/      } 
/*   0*/    } 
/*1637*/    reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/*1638*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _verifyRootSpace(int ch) throws IOException {
/*1651*/    this._inputPtr++;
/*1652*/    switch (ch) {
/*   0*/      case 9:
/*   0*/      case 32:
/*   0*/        return;
/*   0*/      case 13:
/*1657*/        _skipCR();
/*   0*/        return;
/*   0*/      case 10:
/*1660*/        this._currInputRow++;
/*1661*/        this._currInputRowStart = this._inputPtr;
/*   0*/        return;
/*   0*/    } 
/*1664*/    _reportMissingRootWS(ch);
/*   0*/  }
/*   0*/  
/*   0*/  protected final String _parseName() throws IOException {
/*1677*/    int ptr = this._inputPtr;
/*1678*/    int hash = this._hashSeed;
/*1679*/    int[] codes = _icLatin1;
/*1681*/    while (ptr < this._inputEnd) {
/*1682*/      int ch = this._inputBuffer[ptr];
/*1683*/      if (ch < codes.length && codes[ch] != 0) {
/*1684*/        if (ch == 34) {
/*1685*/          int i = this._inputPtr;
/*1686*/          this._inputPtr = ptr + 1;
/*1687*/          return this._symbols.findSymbol(this._inputBuffer, i, ptr - i, hash);
/*   0*/        } 
/*   0*/        break;
/*   0*/      } 
/*1691*/      hash = hash * 33 + ch;
/*1692*/      ptr++;
/*   0*/    } 
/*1694*/    int start = this._inputPtr;
/*1695*/    this._inputPtr = ptr;
/*1696*/    return _parseName2(start, hash, 34);
/*   0*/  }
/*   0*/  
/*   0*/  private String _parseName2(int startPtr, int hash, int endChar) throws IOException {
/*1701*/    this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/*1706*/    char[] outBuf = this._textBuffer.getCurrentSegment();
/*1707*/    int outPtr = this._textBuffer.getCurrentSegmentSize();
/*   0*/    while (true) {
/*1710*/      if (this._inputPtr >= this._inputEnd && 
/*1711*/        !_loadMore()) {
/*1712*/          _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME); 
/*   0*/         }
/*1715*/      char c = this._inputBuffer[this._inputPtr++];
/*1716*/      int i = c;
/*1717*/      if (i <= 92) {
/*1718*/          if (i == 92) {
/*1723*/            c = _decodeEscaped();
/*1724*/          } else if (i <= endChar) {
/*1725*/            if (i == endChar) {
/*   0*/                break; 
/*   0*/               }
/*1728*/            if (i < 32) {
/*1729*/                _throwUnquotedSpace(i, "name"); 
/*   0*/               }
/*   0*/          }  
/*   0*/         }
/*1733*/      hash = hash * 33 + c;
/*1735*/      outBuf[outPtr++] = c;
/*1738*/      if (outPtr >= outBuf.length) {
/*1739*/        outBuf = this._textBuffer.finishCurrentSegment();
/*1740*/        outPtr = 0;
/*   0*/      } 
/*   0*/    } 
/*1743*/    this._textBuffer.setCurrentLength(outPtr);
/*1745*/    TextBuffer tb = this._textBuffer;
/*1746*/    char[] buf = tb.getTextBuffer();
/*1747*/    int start = tb.getTextOffset();
/*1748*/    int len = tb.size();
/*1749*/    return this._symbols.findSymbol(buf, start, len, hash);
/*   0*/  }
/*   0*/  
/*   0*/  protected String _handleOddName(int i) throws IOException {
/*   0*/    boolean firstOk;
/*1762*/    if (i == 39 && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/*1763*/        return _parseAposName(); 
/*   0*/       }
/*1766*/    if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
/*1767*/        _reportUnexpectedChar(i, "was expecting double-quote to start field name"); 
/*   0*/       }
/*1769*/    int[] codes = CharTypes.getInputCodeLatin1JsNames();
/*1770*/    int maxCode = codes.length;
/*1775*/    if (i < maxCode) {
/*1776*/      firstOk = (codes[i] == 0);
/*   0*/    } else {
/*1778*/      firstOk = Character.isJavaIdentifierPart((char)i);
/*   0*/    } 
/*1780*/    if (!firstOk) {
/*1781*/        _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name"); 
/*   0*/       }
/*1783*/    int ptr = this._inputPtr;
/*1784*/    int hash = this._hashSeed;
/*1785*/    int inputLen = this._inputEnd;
/*1787*/    if (ptr < inputLen) {
/*   0*/        do {
/*1789*/          int ch = this._inputBuffer[ptr];
/*1790*/          if (ch < maxCode) {
/*1791*/            if (codes[ch] != 0) {
/*1792*/              int j = this._inputPtr - 1;
/*1793*/              this._inputPtr = ptr;
/*1794*/              return this._symbols.findSymbol(this._inputBuffer, j, ptr - j, hash);
/*   0*/            } 
/*1796*/          } else if (!Character.isJavaIdentifierPart((char)ch)) {
/*1797*/            int j = this._inputPtr - 1;
/*1798*/            this._inputPtr = ptr;
/*1799*/            return this._symbols.findSymbol(this._inputBuffer, j, ptr - j, hash);
/*   0*/          } 
/*1801*/          hash = hash * 33 + ch;
/*1802*/          ++ptr;
/*1803*/        } while (ptr < inputLen); 
/*   0*/       }
/*1805*/    int start = this._inputPtr - 1;
/*1806*/    this._inputPtr = ptr;
/*1807*/    return _handleOddName2(start, hash, codes);
/*   0*/  }
/*   0*/  
/*   0*/  protected String _parseAposName() throws IOException {
/*1813*/    int ptr = this._inputPtr;
/*1814*/    int hash = this._hashSeed;
/*1815*/    int inputLen = this._inputEnd;
/*1817*/    if (ptr < inputLen) {
/*1818*/      int[] codes = _icLatin1;
/*1819*/      int maxCode = codes.length;
/*   0*/      do {
/*1822*/        int ch = this._inputBuffer[ptr];
/*1823*/        if (ch == 39) {
/*1824*/          int i = this._inputPtr;
/*1825*/          this._inputPtr = ptr + 1;
/*1826*/          return this._symbols.findSymbol(this._inputBuffer, i, ptr - i, hash);
/*   0*/        } 
/*1828*/        if (ch < maxCode && codes[ch] != 0) {
/*   0*/            break; 
/*   0*/           }
/*1831*/        hash = hash * 33 + ch;
/*1832*/        ++ptr;
/*1833*/      } while (ptr < inputLen);
/*   0*/    } 
/*1836*/    int start = this._inputPtr;
/*1837*/    this._inputPtr = ptr;
/*1839*/    return _parseName2(start, hash, 39);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonToken _handleOddValue(int i) throws IOException {
/*1849*/    switch (i) {
/*   0*/      case 39:
/*1856*/        if (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/*1857*/            return _handleApos(); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 93:
/*1865*/        if (!this._parsingContext.inArray()) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      case 44:
/*1870*/        if (isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
/*1871*/          this._inputPtr--;
/*1872*/          return JsonToken.VALUE_NULL;
/*   0*/        } 
/*   0*/        break;
/*   0*/      case 78:
/*1876*/        _matchToken("NaN", 1);
/*1877*/        if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/*1878*/            return resetAsNaN("NaN", Double.NaN); 
/*   0*/           }
/*1880*/        _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*   0*/        break;
/*   0*/      case 73:
/*1883*/        _matchToken("Infinity", 1);
/*1884*/        if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/*1885*/            return resetAsNaN("Infinity", Double.POSITIVE_INFINITY); 
/*   0*/           }
/*1887*/        _reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*   0*/        break;
/*   0*/      case 43:
/*1890*/        if (this._inputPtr >= this._inputEnd && 
/*1891*/          !_loadMore()) {
/*1892*/            _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT); 
/*   0*/           }
/*1895*/        return _handleInvalidNumberStart(this._inputBuffer[this._inputPtr++], false);
/*   0*/    } 
/*1898*/    if (Character.isJavaIdentifierStart(i)) {
/*1899*/        _reportInvalidToken("" + (char)i, "('true', 'false' or 'null')"); 
/*   0*/       }
/*1902*/    _reportUnexpectedChar(i, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
/*1903*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonToken _handleApos() throws IOException {
/*1908*/    char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*1909*/    int outPtr = this._textBuffer.getCurrentSegmentSize();
/*   0*/    while (true) {
/*1912*/      if (this._inputPtr >= this._inputEnd && 
/*1913*/        !_loadMore()) {
/*1914*/          _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING); 
/*   0*/         }
/*1918*/      char c = this._inputBuffer[this._inputPtr++];
/*1919*/      int i = c;
/*1920*/      if (i <= 92) {
/*1921*/          if (i == 92) {
/*1926*/            c = _decodeEscaped();
/*1927*/          } else if (i <= 39) {
/*1928*/            if (i == 39) {
/*   0*/                break; 
/*   0*/               }
/*1931*/            if (i < 32) {
/*1932*/                _throwUnquotedSpace(i, "string value"); 
/*   0*/               }
/*   0*/          }  
/*   0*/         }
/*1937*/      if (outPtr >= outBuf.length) {
/*1938*/        outBuf = this._textBuffer.finishCurrentSegment();
/*1939*/        outPtr = 0;
/*   0*/      } 
/*1942*/      outBuf[outPtr++] = c;
/*   0*/    } 
/*1944*/    this._textBuffer.setCurrentLength(outPtr);
/*1945*/    return JsonToken.VALUE_STRING;
/*   0*/  }
/*   0*/  
/*   0*/  private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException {
/*1950*/    this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/*1951*/    char[] outBuf = this._textBuffer.getCurrentSegment();
/*1952*/    int outPtr = this._textBuffer.getCurrentSegmentSize();
/*1953*/    int maxCode = codes.length;
/*1956*/    while (this._inputPtr < _skipColon() || 
/*1957*/      _loadMore()) {
/*1961*/      char c = this._inputBuffer[this._inputPtr];
/*1962*/      int i = c;
/*1963*/      if ((i <= maxCode) ? (
/*1964*/        codes[i] != 0) : 

/*   0*/        
/*1967*/        !Character.isJavaIdentifierPart(c)) {
/*   0*/          break; 
/*   0*/         }
/*1970*/      this._inputPtr++;
/*1971*/      hash = hash * 33 + i;
/*1973*/      outBuf[outPtr++] = c;
/*1976*/      if (outPtr >= outBuf.length) {
/*1977*/        outBuf = this._textBuffer.finishCurrentSegment();
/*1978*/        outPtr = 0;
/*   0*/      } 
/*   0*/    } 
/*1981*/    this._textBuffer.setCurrentLength(outPtr);
/*1983*/    TextBuffer tb = this._textBuffer;
/*1984*/    char[] buf = tb.getTextBuffer();
/*1985*/    int start = tb.getTextOffset();
/*1986*/    int len = tb.size();
/*1988*/    return this._symbols.findSymbol(buf, start, len, hash);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _finishString() throws IOException {
/*1999*/    int ptr = this._inputPtr;
/*2000*/    int inputLen = this._inputEnd;
/*2002*/    if (ptr < inputLen) {
/*2003*/      int[] codes = _icLatin1;
/*2004*/      int maxCode = codes.length;
/*   0*/      do {
/*2007*/        int ch = this._inputBuffer[ptr];
/*2008*/        if (ch < maxCode && codes[ch] != 0) {
/*2009*/          if (ch == 34) {
/*2010*/            this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/*2011*/            this._inputPtr = ptr + 1;
/*   0*/            return;
/*   0*/          } 
/*   0*/          break;
/*   0*/        } 
/*2017*/        ++ptr;
/*2018*/      } while (ptr < inputLen);
/*   0*/    } 
/*2022*/    this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/*2023*/    this._inputPtr = ptr;
/*2024*/    _finishString2();
/*   0*/  }
/*   0*/  
/*   0*/  protected void _finishString2() throws IOException {
/*2029*/    char[] outBuf = this._textBuffer.getCurrentSegment();
/*2030*/    int outPtr = this._textBuffer.getCurrentSegmentSize();
/*2031*/    int[] codes = _icLatin1;
/*2032*/    int maxCode = codes.length;
/*   0*/    while (true) {
/*2035*/      if (this._inputPtr >= this._inputEnd && 
/*2036*/        !_loadMore()) {
/*2037*/          _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING); 
/*   0*/         }
/*2041*/      char c = this._inputBuffer[this._inputPtr++];
/*2042*/      int i = c;
/*2043*/      if (i < maxCode && codes[i] != 0) {
/*2044*/        if (i == 34) {
/*   0*/            break; 
/*   0*/           }
/*2046*/        if (i == 92) {
/*2051*/          c = _decodeEscaped();
/*2052*/        } else if (i < 32) {
/*2053*/          _throwUnquotedSpace(i, "string value");
/*   0*/        } 
/*   0*/      } 
/*2057*/      if (outPtr >= outBuf.length) {
/*2058*/        outBuf = this._textBuffer.finishCurrentSegment();
/*2059*/        outPtr = 0;
/*   0*/      } 
/*2062*/      outBuf[outPtr++] = c;
/*   0*/    } 
/*2064*/    this._textBuffer.setCurrentLength(outPtr);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _skipString() throws IOException {
/*2074*/    this._tokenIncomplete = false;
/*2076*/    int inPtr = this._inputPtr;
/*2077*/    int inLen = this._inputEnd;
/*2078*/    char[] inBuf = this._inputBuffer;
/*   0*/    while (true) {
/*2081*/      if (inPtr >= inLen) {
/*2082*/        this._inputPtr = inPtr;
/*2083*/        if (!_loadMore()) {
/*2084*/            _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING); 
/*   0*/           }
/*2087*/        inPtr = this._inputPtr;
/*2088*/        inLen = this._inputEnd;
/*   0*/      } 
/*2090*/      char c = inBuf[inPtr++];
/*2091*/      int i = c;
/*2092*/      if (i <= 92) {
/*2093*/        if (i == 92) {
/*2096*/          this._inputPtr = inPtr;
/*2097*/          _decodeEscaped();
/*2098*/          inPtr = this._inputPtr;
/*2099*/          inLen = this._inputEnd;
/*   0*/          continue;
/*   0*/        } 
/*2100*/        if (i <= 34) {
/*2101*/          if (i == 34) {
/*2102*/            this._inputPtr = inPtr;
/*   0*/            break;
/*   0*/          } 
/*2105*/          if (i < 32) {
/*2106*/            this._inputPtr = inPtr;
/*2107*/            _throwUnquotedSpace(i, "string value");
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _skipCR() throws IOException {
/*2125*/    if ((this._inputPtr < this._inputEnd || _loadMore()) && 
/*2126*/      this._inputBuffer[this._inputPtr] == '\n') {
/*2127*/        this._inputPtr++; 
/*   0*/       }
/*2130*/    this._currInputRow++;
/*2131*/    this._currInputRowStart = this._inputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _skipColon() throws IOException {
/*2136*/    if (this._inputPtr + 4 >= this._inputEnd) {
/*2137*/        return _skipColon2(false); 
/*   0*/       }
/*2139*/    char c = this._inputBuffer[this._inputPtr];
/*2140*/    if (c == ':') {
/*2141*/      int i = this._inputBuffer[++this._inputPtr];
/*2142*/      if (i > 32) {
/*2143*/        if (i == 47 || i == 35) {
/*2144*/            return _skipColon2(true); 
/*   0*/           }
/*2146*/        this._inputPtr++;
/*2147*/        return i;
/*   0*/      } 
/*2149*/      if (i == 32 || i == 9) {
/*2150*/        i = this._inputBuffer[++this._inputPtr];
/*2151*/        if (i > 32) {
/*2152*/          if (i == 47 || i == 35) {
/*2153*/              return _skipColon2(true); 
/*   0*/             }
/*2155*/          this._inputPtr++;
/*2156*/          return i;
/*   0*/        } 
/*   0*/      } 
/*2159*/      return _skipColon2(true);
/*   0*/    } 
/*2161*/    if (c == ' ' || c == '\t') {
/*2162*/        c = this._inputBuffer[++this._inputPtr]; 
/*   0*/       }
/*2164*/    if (c == ':') {
/*2165*/      int i = this._inputBuffer[++this._inputPtr];
/*2166*/      if (i > 32) {
/*2167*/        if (i == 47 || i == 35) {
/*2168*/            return _skipColon2(true); 
/*   0*/           }
/*2170*/        this._inputPtr++;
/*2171*/        return i;
/*   0*/      } 
/*2173*/      if (i == 32 || i == 9) {
/*2174*/        i = this._inputBuffer[++this._inputPtr];
/*2175*/        if (i > 32) {
/*2176*/          if (i == 47 || i == 35) {
/*2177*/              return _skipColon2(true); 
/*   0*/             }
/*2179*/          this._inputPtr++;
/*2180*/          return i;
/*   0*/        } 
/*   0*/      } 
/*2183*/      return _skipColon2(true);
/*   0*/    } 
/*2185*/    return _skipColon2(false);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _skipColon2(boolean gotColon) throws IOException {
/*2190*/    while (this._inputPtr < this._inputEnd || _loadMore()) {
/*2191*/      int i = this._inputBuffer[this._inputPtr++];
/*2192*/      if (i > 32) {
/*2193*/        if (i == 47) {
/*2194*/          _skipComment();
/*   0*/          continue;
/*   0*/        } 
/*2197*/        if (i == 35 && 
/*2198*/          _skipYAMLComment()) {
/*   0*/            continue; 
/*   0*/           }
/*2202*/        if (gotColon) {
/*2203*/            return i; 
/*   0*/           }
/*2205*/        if (i != 58) {
/*2206*/            _reportUnexpectedChar(i, "was expecting a colon to separate field name and value"); 
/*   0*/           }
/*2208*/        gotColon = true;
/*   0*/        continue;
/*   0*/      } 
/*2211*/      if (i < 32) {
/*2212*/        if (i == 10) {
/*2213*/          this._currInputRow++;
/*2214*/          this._currInputRowStart = this._inputPtr;
/*   0*/          continue;
/*   0*/        } 
/*2215*/        if (i == 13) {
/*2216*/          _skipCR();
/*   0*/          continue;
/*   0*/        } 
/*2217*/        if (i != 9) {
/*2218*/            _throwInvalidSpace(i); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*2222*/    _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
/*2224*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _skipColonFast(int ptr) throws IOException {
/*2230*/    int i = this._inputBuffer[ptr++];
/*2231*/    if (i == 58) {
/*2232*/      i = this._inputBuffer[ptr++];
/*2233*/      if (i > 32) {
/*2234*/        if (i != 47 && i != 35) {
/*2235*/          this._inputPtr = ptr;
/*2236*/          return i;
/*   0*/        } 
/*2238*/      } else if (i == 32 || i == 9) {
/*2239*/        i = this._inputBuffer[ptr++];
/*2240*/        if (i > 32 && 
/*2241*/          i != 47 && i != 35) {
/*2242*/          this._inputPtr = ptr;
/*2243*/          return i;
/*   0*/        } 
/*   0*/      } 
/*2247*/      this._inputPtr = ptr - 1;
/*2248*/      return _skipColon2(true);
/*   0*/    } 
/*2250*/    if (i == 32 || i == 9) {
/*2251*/        i = this._inputBuffer[ptr++]; 
/*   0*/       }
/*2253*/    boolean gotColon = (i == 58);
/*2254*/    if (gotColon) {
/*2255*/      i = this._inputBuffer[ptr++];
/*2256*/      if (i > 32) {
/*2257*/        if (i != 47 && i != 35) {
/*2258*/          this._inputPtr = ptr;
/*2259*/          return i;
/*   0*/        } 
/*2261*/      } else if (i == 32 || i == 9) {
/*2262*/        i = this._inputBuffer[ptr++];
/*2263*/        if (i > 32 && 
/*2264*/          i != 47 && i != 35) {
/*2265*/          this._inputPtr = ptr;
/*2266*/          return i;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2271*/    this._inputPtr = ptr - 1;
/*2272*/    return _skipColon2(gotColon);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _skipComma(int i) throws IOException {
/*2278*/    if (i != 44) {
/*2279*/        _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries"); 
/*   0*/       }
/*2281*/    while (this._inputPtr < this._inputEnd) {
/*2282*/      i = this._inputBuffer[this._inputPtr++];
/*2283*/      if (i > 32) {
/*2284*/        if (i == 47 || i == 35) {
/*2285*/          this._inputPtr--;
/*2286*/          return _skipAfterComma2();
/*   0*/        } 
/*2288*/        return i;
/*   0*/      } 
/*2290*/      if (i < 32) {
/*2291*/        if (i == 10) {
/*2292*/          this._currInputRow++;
/*2293*/          this._currInputRowStart = this._inputPtr;
/*   0*/          continue;
/*   0*/        } 
/*2294*/        if (i == 13) {
/*2295*/          _skipCR();
/*   0*/          continue;
/*   0*/        } 
/*2296*/        if (i != 9) {
/*2297*/            _throwInvalidSpace(i); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*2301*/    return _skipAfterComma2();
/*   0*/  }
/*   0*/  
/*   0*/  private final int _skipAfterComma2() throws IOException {
/*2306*/    while (this._inputPtr < this._inputEnd || _loadMore()) {
/*2307*/      int i = this._inputBuffer[this._inputPtr++];
/*2308*/      if (i > 32) {
/*2309*/        if (i == 47) {
/*2310*/          _skipComment();
/*   0*/          continue;
/*   0*/        } 
/*2313*/        if (i == 35 && 
/*2314*/          _skipYAMLComment()) {
/*   0*/            continue; 
/*   0*/           }
/*2318*/        return i;
/*   0*/      } 
/*2320*/      if (i < 32) {
/*2321*/        if (i == 10) {
/*2322*/          this._currInputRow++;
/*2323*/          this._currInputRowStart = this._inputPtr;
/*   0*/          continue;
/*   0*/        } 
/*2324*/        if (i == 13) {
/*2325*/          _skipCR();
/*   0*/          continue;
/*   0*/        } 
/*2326*/        if (i != 9) {
/*2327*/            _throwInvalidSpace(i); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*2331*/    throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
/*   0*/  }
/*   0*/  
/*   0*/  private final int _skipWSOrEnd() throws IOException {
/*2338*/    if (this._inputPtr >= this._inputEnd && 
/*2339*/      !_loadMore()) {
/*2340*/        return _eofAsNextChar(); 
/*   0*/       }
/*2343*/    int i = this._inputBuffer[this._inputPtr++];
/*2344*/    if (i > 32) {
/*2345*/      if (i == 47 || i == 35) {
/*2346*/        this._inputPtr--;
/*2347*/        return _skipWSOrEnd2();
/*   0*/      } 
/*2349*/      return i;
/*   0*/    } 
/*2351*/    if (i != 32) {
/*2352*/        if (i == 10) {
/*2353*/          this._currInputRow++;
/*2354*/          this._currInputRowStart = this._inputPtr;
/*2355*/        } else if (i == 13) {
/*2356*/          _skipCR();
/*2357*/        } else if (i != 9) {
/*2358*/          _throwInvalidSpace(i);
/*   0*/        }  
/*   0*/       }
/*2362*/    while (this._inputPtr < this._inputEnd) {
/*2363*/      i = this._inputBuffer[this._inputPtr++];
/*2364*/      if (i > 32) {
/*2365*/        if (i == 47 || i == 35) {
/*2366*/          this._inputPtr--;
/*2367*/          return _skipWSOrEnd2();
/*   0*/        } 
/*2369*/        return i;
/*   0*/      } 
/*2371*/      if (i != 32) {
/*2372*/        if (i == 10) {
/*2373*/          this._currInputRow++;
/*2374*/          this._currInputRowStart = this._inputPtr;
/*   0*/          continue;
/*   0*/        } 
/*2375*/        if (i == 13) {
/*2376*/          _skipCR();
/*   0*/          continue;
/*   0*/        } 
/*2377*/        if (i != 9) {
/*2378*/            _throwInvalidSpace(i); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*2382*/    return _skipWSOrEnd2();
/*   0*/  }
/*   0*/  
/*   0*/  private int _skipWSOrEnd2() throws IOException {
/*   0*/    while (true) {
/*2388*/      if (this._inputPtr >= this._inputEnd && 
/*2389*/        !_loadMore()) {
/*2390*/          return _eofAsNextChar(); 
/*   0*/         }
/*2393*/      int i = this._inputBuffer[this._inputPtr++];
/*2394*/      if (i > 32) {
/*2395*/        if (i == 47) {
/*2396*/          _skipComment();
/*   0*/          continue;
/*   0*/        } 
/*2399*/        if (i == 35 && 
/*2400*/          _skipYAMLComment()) {
/*   0*/            continue; 
/*   0*/           }
/*2404*/        return i;
/*   0*/      } 
/*2405*/      if (i != 32) {
/*2406*/        if (i == 10) {
/*2407*/          this._currInputRow++;
/*2408*/          this._currInputRowStart = this._inputPtr;
/*   0*/          continue;
/*   0*/        } 
/*2409*/        if (i == 13) {
/*2410*/          _skipCR();
/*   0*/          continue;
/*   0*/        } 
/*2411*/        if (i != 9) {
/*2412*/            _throwInvalidSpace(i); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _skipComment() throws IOException {
/*2420*/    if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
/*2421*/        _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)"); 
/*   0*/       }
/*2424*/    if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*2425*/        _reportInvalidEOF(" in a comment", null); 
/*   0*/       }
/*2427*/    char c = this._inputBuffer[this._inputPtr++];
/*2428*/    if (c == '/') {
/*2429*/      _skipLine();
/*2430*/    } else if (c == '*') {
/*2431*/      _skipCComment();
/*   0*/    } else {
/*2433*/      _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _skipCComment() throws IOException {
/*2440*/    while (this._inputPtr < this._inputEnd || _loadMore()) {
/*2441*/      int i = this._inputBuffer[this._inputPtr++];
/*2442*/      if (i <= 42) {
/*2443*/        if (i == 42) {
/*2444*/          if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*   0*/              break; 
/*   0*/             }
/*2447*/          if (this._inputBuffer[this._inputPtr] == '/') {
/*2448*/            this._inputPtr++;
/*   0*/            return;
/*   0*/          } 
/*   0*/          continue;
/*   0*/        } 
/*2453*/        if (i < 32) {
/*2454*/          if (i == 10) {
/*2455*/            this._currInputRow++;
/*2456*/            this._currInputRowStart = this._inputPtr;
/*   0*/            continue;
/*   0*/          } 
/*2457*/          if (i == 13) {
/*2458*/            _skipCR();
/*   0*/            continue;
/*   0*/          } 
/*2459*/          if (i != 9) {
/*2460*/              _throwInvalidSpace(i); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2465*/    _reportInvalidEOF(" in a comment", null);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _skipYAMLComment() throws IOException {
/*2470*/    if (!isEnabled(JsonParser.Feature.ALLOW_YAML_COMMENTS)) {
/*2471*/        return false; 
/*   0*/       }
/*2473*/    _skipLine();
/*2474*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void _skipLine() throws IOException {
/*2480*/    while (this._inputPtr < this._inputEnd || _loadMore()) {
/*2481*/      int i = this._inputBuffer[this._inputPtr++];
/*2482*/      if (i < 32) {
/*2483*/        if (i == 10) {
/*2484*/          this._currInputRow++;
/*2485*/          this._currInputRowStart = this._inputPtr;
/*   0*/          break;
/*   0*/        } 
/*2487*/        if (i == 13) {
/*2488*/          _skipCR();
/*   0*/          break;
/*   0*/        } 
/*2490*/        if (i != 9) {
/*2491*/            _throwInvalidSpace(i); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected char _decodeEscaped() throws IOException {
/*2500*/    if (this._inputPtr >= this._inputEnd && 
/*2501*/      !_loadMore()) {
/*2502*/        _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING); 
/*   0*/       }
/*2505*/    char c = this._inputBuffer[this._inputPtr++];
/*2507*/    switch (c) {
/*   0*/      case 'b':
/*2510*/        return '\b';
/*   0*/      case 't':
/*2512*/        return '\t';
/*   0*/      case 'n':
/*2514*/        return '\n';
/*   0*/      case 'f':
/*2516*/        return '\f';
/*   0*/      case 'r':
/*2518*/        return '\r';
/*   0*/      case '"':
/*   0*/      case '/':
/*   0*/      case '\\':
/*2524*/        return c;
/*   0*/      case 'u':
/*   0*/        break;
/*   0*/      default:
/*2530*/        return _handleUnrecognizedCharacterEscape(c);
/*   0*/    } 
/*2534*/    int value = 0;
/*2535*/    for (int i = 0; i < 4; i++) {
/*2536*/      if (this._inputPtr >= this._inputEnd && 
/*2537*/        !_loadMore()) {
/*2538*/          _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING); 
/*   0*/         }
/*2541*/      int ch = this._inputBuffer[this._inputPtr++];
/*2542*/      int digit = CharTypes.charToHex(ch);
/*2543*/      if (digit < 0) {
/*2544*/          _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence"); 
/*   0*/         }
/*2546*/      value = value << 4 | digit;
/*   0*/    } 
/*2548*/    return (char)value;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _matchTrue() throws IOException {
/*2552*/    int ptr = this._inputPtr;
/*2553*/    if (ptr + 3 < this._inputEnd) {
/*2554*/      char[] b = this._inputBuffer;
/*2555*/      if (b[ptr] == 'r' && b[++ptr] == 'u' && b[++ptr] == 'e') {
/*2556*/        char c = b[++ptr];
/*2557*/        if (c < '0' || c == ']' || c == '}') {
/*2558*/          this._inputPtr = ptr;
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2564*/    _matchToken("true", 1);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _matchFalse() throws IOException {
/*2568*/    int ptr = this._inputPtr;
/*2569*/    if (ptr + 4 < this._inputEnd) {
/*2570*/      char[] b = this._inputBuffer;
/*2571*/      if (b[ptr] == 'a' && b[++ptr] == 'l' && b[++ptr] == 's' && b[++ptr] == 'e') {
/*2572*/        char c = b[++ptr];
/*2573*/        if (c < '0' || c == ']' || c == '}') {
/*2574*/          this._inputPtr = ptr;
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2580*/    _matchToken("false", 1);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _matchNull() throws IOException {
/*2584*/    int ptr = this._inputPtr;
/*2585*/    if (ptr + 3 < this._inputEnd) {
/*2586*/      char[] b = this._inputBuffer;
/*2587*/      if (b[ptr] == 'u' && b[++ptr] == 'l' && b[++ptr] == 'l') {
/*2588*/        char c = b[++ptr];
/*2589*/        if (c < '0' || c == ']' || c == '}') {
/*2590*/          this._inputPtr = ptr;
/*   0*/          return;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2596*/    _matchToken("null", 1);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _matchToken(String matchStr, int i) throws IOException {
/*2604*/    int len = matchStr.length();
/*2605*/    if (this._inputPtr + len >= this._inputEnd) {
/*2606*/      _matchToken2(matchStr, i);
/*   0*/      return;
/*   0*/    } 
/*   0*/    while (true) {
/*2611*/      if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/*2612*/          _reportInvalidToken(matchStr.substring(0, i)); 
/*   0*/         }
/*2614*/      this._inputPtr++;
/*2615*/      if (++i >= len) {
/*2616*/        int ch = this._inputBuffer[this._inputPtr];
/*2617*/        if (ch >= 48 && ch != 93 && ch != 125) {
/*2618*/            _checkMatchEnd(matchStr, i, ch); 
/*   0*/           }
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _matchToken2(String matchStr, int i) throws IOException {
/*2624*/    int len = matchStr.length();
/*   0*/    do {
/*2626*/      if ((this._inputPtr >= this._inputEnd && !_loadMore()) || this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/*2628*/          _reportInvalidToken(matchStr.substring(0, i)); 
/*   0*/         }
/*2630*/      this._inputPtr++;
/*2631*/    } while (++i < len);
/*2634*/    if (this._inputPtr >= this._inputEnd && !_loadMore()) {
/*   0*/        return; 
/*   0*/       }
/*2637*/    int ch = this._inputBuffer[this._inputPtr];
/*2638*/    if (ch >= 48 && ch != 93 && ch != 125) {
/*2639*/        _checkMatchEnd(matchStr, i, ch); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final void _checkMatchEnd(String matchStr, int i, int c) throws IOException {
/*2645*/    char ch = (char)c;
/*2646*/    if (Character.isJavaIdentifierPart(ch)) {
/*2647*/        _reportInvalidToken(matchStr.substring(0, i)); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
/*2664*/    ByteArrayBuilder builder = _getByteArrayBuilder();
/*   0*/    while (true) {
/*2671*/      if (this._inputPtr >= this._inputEnd) {
/*2672*/          _loadMoreGuaranteed(); 
/*   0*/         }
/*2674*/      char ch = this._inputBuffer[this._inputPtr++];
/*2675*/      if (ch > ' ') {
/*2676*/        int bits = b64variant.decodeBase64Char(ch);
/*2677*/        if (bits < 0) {
/*2678*/          if (ch == '"') {
/*2679*/              return builder.toByteArray(); 
/*   0*/             }
/*2681*/          bits = _decodeBase64Escape(b64variant, ch, 0);
/*2682*/          if (bits < 0) {
/*   0*/              continue; 
/*   0*/             }
/*   0*/        } 
/*2686*/        int decodedData = bits;
/*2690*/        if (this._inputPtr >= this._inputEnd) {
/*2691*/            _loadMoreGuaranteed(); 
/*   0*/           }
/*2693*/        ch = this._inputBuffer[this._inputPtr++];
/*2694*/        bits = b64variant.decodeBase64Char(ch);
/*2695*/        if (bits < 0) {
/*2696*/            bits = _decodeBase64Escape(b64variant, ch, 1); 
/*   0*/           }
/*2698*/        decodedData = decodedData << 6 | bits;
/*2701*/        if (this._inputPtr >= this._inputEnd) {
/*2702*/            _loadMoreGuaranteed(); 
/*   0*/           }
/*2704*/        ch = this._inputBuffer[this._inputPtr++];
/*2705*/        bits = b64variant.decodeBase64Char(ch);
/*2708*/        if (bits < 0) {
/*2709*/          if (bits != -2) {
/*2711*/            if (ch == '"') {
/*2712*/              decodedData >>= 4;
/*2713*/              builder.append(decodedData);
/*2714*/              if (b64variant.usesPadding()) {
/*2715*/                this._inputPtr--;
/*2716*/                _handleBase64MissingPadding(b64variant);
/*   0*/              } 
/*2718*/              return builder.toByteArray();
/*   0*/            } 
/*2720*/            bits = _decodeBase64Escape(b64variant, ch, 2);
/*   0*/          } 
/*2722*/          if (bits == -2) {
/*2724*/            if (this._inputPtr >= this._inputEnd) {
/*2725*/                _loadMoreGuaranteed(); 
/*   0*/               }
/*2727*/            ch = this._inputBuffer[this._inputPtr++];
/*2728*/            if (!b64variant.usesPaddingChar(ch) && 
/*2729*/              _decodeBase64Escape(b64variant, ch, 3) != -2) {
/*2730*/                throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'"); 
/*   0*/               }
/*2734*/            decodedData >>= 4;
/*2735*/            builder.append(decodedData);
/*   0*/            continue;
/*   0*/          } 
/*   0*/        } 
/*2741*/        decodedData = decodedData << 6 | bits;
/*2743*/        if (this._inputPtr >= this._inputEnd) {
/*2744*/            _loadMoreGuaranteed(); 
/*   0*/           }
/*2746*/        ch = this._inputBuffer[this._inputPtr++];
/*2747*/        bits = b64variant.decodeBase64Char(ch);
/*2748*/        if (bits < 0) {
/*2749*/          if (bits != -2) {
/*2751*/            if (ch == '"') {
/*2752*/              decodedData >>= 2;
/*2753*/              builder.appendTwoBytes(decodedData);
/*2754*/              if (b64variant.usesPadding()) {
/*2755*/                this._inputPtr--;
/*2756*/                _handleBase64MissingPadding(b64variant);
/*   0*/              } 
/*2758*/              return builder.toByteArray();
/*   0*/            } 
/*2760*/            bits = _decodeBase64Escape(b64variant, ch, 3);
/*   0*/          } 
/*2762*/          if (bits == -2) {
/*2768*/            decodedData >>= 2;
/*2769*/            builder.appendTwoBytes(decodedData);
/*   0*/            continue;
/*   0*/          } 
/*   0*/        } 
/*2775*/        decodedData = decodedData << 6 | bits;
/*2776*/        builder.appendThreeBytes(decodedData);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JsonLocation getTokenLocation() {
/*2789*/    if (this._currToken == JsonToken.FIELD_NAME) {
/*2790*/      long total = this._currInputProcessed + this._nameStartOffset - 1L;
/*2791*/      return new JsonLocation(_getSourceReference(), -1L, total, this._nameStartRow, this._nameStartCol);
/*   0*/    } 
/*2794*/    return new JsonLocation(_getSourceReference(), -1L, this._tokenInputTotal - 1L, this._tokenInputRow, this._tokenInputCol);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonLocation getCurrentLocation() {
/*2800*/    int col = this._inputPtr - this._currInputRowStart + 1;
/*2801*/    return new JsonLocation(_getSourceReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _updateLocation() {
/*2809*/    int ptr = this._inputPtr;
/*2810*/    this._tokenInputTotal = this._currInputProcessed + ptr;
/*2811*/    this._tokenInputRow = this._currInputRow;
/*2812*/    this._tokenInputCol = ptr - this._currInputRowStart;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _updateNameLocation() {
/*2818*/    int ptr = this._inputPtr;
/*2819*/    this._nameStartOffset = ptr;
/*2820*/    this._nameStartRow = this._currInputRow;
/*2821*/    this._nameStartCol = ptr - this._currInputRowStart;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportInvalidToken(String matchedPart) throws IOException {
/*2831*/    _reportInvalidToken(matchedPart, "'null', 'true', 'false' or NaN");
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
/*2840*/    StringBuilder sb = new StringBuilder(matchedPart);
/*2841*/    while (this._inputPtr < this._inputEnd || _loadMore()) {
/*2842*/      char c = this._inputBuffer[this._inputPtr];
/*2843*/      if (!Character.isJavaIdentifierPart(c)) {
/*   0*/          break; 
/*   0*/         }
/*2846*/      this._inputPtr++;
/*2847*/      sb.append(c);
/*2848*/      if (sb.length() >= 256) {
/*2849*/        sb.append("...");
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*2853*/    _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
/*   0*/  }
/*   0*/  
/*   0*/  private void _closeScope(int i) throws JsonParseException {
/*2863*/    if (i == 93) {
/*2864*/      _updateLocation();
/*2865*/      if (!this._parsingContext.inArray()) {
/*2866*/          _reportMismatchedEndMarker(i, '}'); 
/*   0*/         }
/*2868*/      this._parsingContext = this._parsingContext.clearAndGetParent();
/*2869*/      this._currToken = JsonToken.END_ARRAY;
/*   0*/    } 
/*2871*/    if (i == 125) {
/*2872*/      _updateLocation();
/*2873*/      if (!this._parsingContext.inObject()) {
/*2874*/          _reportMismatchedEndMarker(i, ']'); 
/*   0*/         }
/*2876*/      this._parsingContext = this._parsingContext.clearAndGetParent();
/*2877*/      this._currToken = JsonToken.END_OBJECT;
/*   0*/    } 
/*   0*/  }
/*   0*/}
