/*   0*/package com.fasterxml.jackson.core.json;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.JsonGenerationException;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonStreamContext;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.SerializableString;
/*   0*/import com.fasterxml.jackson.core.io.CharTypes;
/*   0*/import com.fasterxml.jackson.core.io.CharacterEscapes;
/*   0*/import com.fasterxml.jackson.core.io.IOContext;
/*   0*/import com.fasterxml.jackson.core.io.NumberOutput;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.math.BigDecimal;
/*   0*/import java.math.BigInteger;
/*   0*/
/*   0*/public class UTF8JsonGenerator extends JsonGeneratorImpl {
/*   0*/  private static final byte BYTE_u = 117;
/*   0*/  
/*   0*/  private static final byte BYTE_0 = 48;
/*   0*/  
/*   0*/  private static final byte BYTE_LBRACKET = 91;
/*   0*/  
/*   0*/  private static final byte BYTE_RBRACKET = 93;
/*   0*/  
/*   0*/  private static final byte BYTE_LCURLY = 123;
/*   0*/  
/*   0*/  private static final byte BYTE_RCURLY = 125;
/*   0*/  
/*   0*/  private static final byte BYTE_BACKSLASH = 92;
/*   0*/  
/*   0*/  private static final byte BYTE_COMMA = 44;
/*   0*/  
/*   0*/  private static final byte BYTE_COLON = 58;
/*   0*/  
/*   0*/  private static final byte BYTE_QUOTE = 34;
/*   0*/  
/*   0*/  private static final int MAX_BYTES_TO_BUFFER = 512;
/*   0*/  
/*  30*/  private static final byte[] HEX_CHARS = CharTypes.copyHexBytes();
/*   0*/  
/*  32*/  private static final byte[] NULL_BYTES = new byte[] { 110, 117, 108, 108 };
/*   0*/  
/*  33*/  private static final byte[] TRUE_BYTES = new byte[] { 116, 114, 117, 101 };
/*   0*/  
/*  34*/  private static final byte[] FALSE_BYTES = new byte[] { 102, 97, 108, 115, 101 };
/*   0*/  
/*   0*/  protected final OutputStream _outputStream;
/*   0*/  
/*   0*/  protected byte[] _outputBuffer;
/*   0*/  
/*   0*/  protected int _outputTail;
/*   0*/  
/*   0*/  protected final int _outputEnd;
/*   0*/  
/*   0*/  protected final int _outputMaxContiguous;
/*   0*/  
/*   0*/  protected char[] _charBuffer;
/*   0*/  
/*   0*/  protected final int _charBufferLength;
/*   0*/  
/*   0*/  protected byte[] _entityBuffer;
/*   0*/  
/*   0*/  protected boolean _bufferRecyclable;
/*   0*/  
/*   0*/  public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
/* 103*/    super(ctxt, features, codec);
/* 104*/    this._outputStream = out;
/* 105*/    this._bufferRecyclable = true;
/* 106*/    this._outputBuffer = ctxt.allocWriteEncodingBuffer();
/* 107*/    this._outputEnd = this._outputBuffer.length;
/* 113*/    this._outputMaxContiguous = this._outputEnd >> 3;
/* 114*/    this._charBuffer = ctxt.allocConcatBuffer();
/* 115*/    this._charBufferLength = this._charBuffer.length;
/* 118*/    if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/* 119*/        setHighestNonEscapedChar(127); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
/* 128*/    super(ctxt, features, codec);
/* 129*/    this._outputStream = out;
/* 130*/    this._bufferRecyclable = bufferRecyclable;
/* 131*/    this._outputTail = outputOffset;
/* 132*/    this._outputBuffer = outputBuffer;
/* 133*/    this._outputEnd = this._outputBuffer.length;
/* 135*/    this._outputMaxContiguous = this._outputEnd >> 3;
/* 136*/    this._charBuffer = ctxt.allocConcatBuffer();
/* 137*/    this._charBufferLength = this._charBuffer.length;
/*   0*/  }
/*   0*/  
/*   0*/  public Object getOutputTarget() {
/* 148*/    return this._outputStream;
/*   0*/  }
/*   0*/  
/*   0*/  public int getOutputBuffered() {
/* 154*/    return this._outputTail;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeFieldName(String name) throws IOException {
/* 166*/    if (this._cfgPrettyPrinter != null) {
/* 167*/      _writePPFieldName(name);
/*   0*/      return;
/*   0*/    } 
/* 170*/    int status = this._writeContext.writeFieldName(name);
/* 171*/    if (status == 4) {
/* 172*/        _reportError("Can not write a field name, expecting a value"); 
/*   0*/       }
/* 174*/    if (status == 1) {
/* 175*/      if (this._outputTail >= this._outputEnd) {
/* 176*/          _flushBuffer(); 
/*   0*/         }
/* 178*/      this._outputBuffer[this._outputTail++] = 44;
/*   0*/    } 
/* 183*/    if (this._cfgUnqNames) {
/* 184*/      _writeStringSegments(name, false);
/*   0*/      return;
/*   0*/    } 
/* 187*/    int len = name.length();
/* 189*/    if (len > this._charBufferLength) {
/* 190*/      _writeStringSegments(name, true);
/*   0*/      return;
/*   0*/    } 
/* 193*/    if (this._outputTail >= this._outputEnd) {
/* 194*/        _flushBuffer(); 
/*   0*/       }
/* 196*/    this._outputBuffer[this._outputTail++] = 34;
/* 198*/    if (len <= this._outputMaxContiguous) {
/* 199*/      if (this._outputTail + len > this._outputEnd) {
/* 200*/          _flushBuffer(); 
/*   0*/         }
/* 202*/      _writeStringSegment(name, 0, len);
/*   0*/    } else {
/* 204*/      _writeStringSegments(name, 0, len);
/*   0*/    } 
/* 207*/    if (this._outputTail >= this._outputEnd) {
/* 208*/        _flushBuffer(); 
/*   0*/       }
/* 210*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeFieldName(SerializableString name) throws IOException {
/* 216*/    if (this._cfgPrettyPrinter != null) {
/* 217*/      _writePPFieldName(name);
/*   0*/      return;
/*   0*/    } 
/* 220*/    int status = this._writeContext.writeFieldName(name.getValue());
/* 221*/    if (status == 4) {
/* 222*/        _reportError("Can not write a field name, expecting a value"); 
/*   0*/       }
/* 224*/    if (status == 1) {
/* 225*/      if (this._outputTail >= this._outputEnd) {
/* 226*/          _flushBuffer(); 
/*   0*/         }
/* 228*/      this._outputBuffer[this._outputTail++] = 44;
/*   0*/    } 
/* 230*/    if (this._cfgUnqNames) {
/* 231*/      _writeUnq(name);
/*   0*/      return;
/*   0*/    } 
/* 234*/    if (this._outputTail >= this._outputEnd) {
/* 235*/        _flushBuffer(); 
/*   0*/       }
/* 237*/    this._outputBuffer[this._outputTail++] = 34;
/* 238*/    int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/* 239*/    if (len < 0) {
/* 240*/      _writeBytes(name.asQuotedUTF8());
/*   0*/    } else {
/* 242*/      this._outputTail += len;
/*   0*/    } 
/* 244*/    if (this._outputTail >= this._outputEnd) {
/* 245*/        _flushBuffer(); 
/*   0*/       }
/* 247*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeUnq(SerializableString name) throws IOException {
/* 251*/    int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/* 252*/    if (len < 0) {
/* 253*/      _writeBytes(name.asQuotedUTF8());
/*   0*/    } else {
/* 255*/      this._outputTail += len;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeStartArray() throws IOException {
/* 268*/    _verifyValueWrite("start an array");
/* 269*/    this._writeContext = this._writeContext.createChildArrayContext();
/* 270*/    if (this._cfgPrettyPrinter != null) {
/* 271*/      this._cfgPrettyPrinter.writeStartArray(this);
/*   0*/    } else {
/* 273*/      if (this._outputTail >= this._outputEnd) {
/* 274*/          _flushBuffer(); 
/*   0*/         }
/* 276*/      this._outputBuffer[this._outputTail++] = 91;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeEndArray() throws IOException {
/* 283*/    if (!this._writeContext.inArray()) {
/* 284*/        _reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc()); 
/*   0*/       }
/* 286*/    if (this._cfgPrettyPrinter != null) {
/* 287*/      this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
/*   0*/    } else {
/* 289*/      if (this._outputTail >= this._outputEnd) {
/* 290*/          _flushBuffer(); 
/*   0*/         }
/* 292*/      this._outputBuffer[this._outputTail++] = 93;
/*   0*/    } 
/* 294*/    this._writeContext = this._writeContext.clearAndGetParent();
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeStartObject() throws IOException {
/* 300*/    _verifyValueWrite("start an object");
/* 301*/    this._writeContext = this._writeContext.createChildObjectContext();
/* 302*/    if (this._cfgPrettyPrinter != null) {
/* 303*/      this._cfgPrettyPrinter.writeStartObject(this);
/*   0*/    } else {
/* 305*/      if (this._outputTail >= this._outputEnd) {
/* 306*/          _flushBuffer(); 
/*   0*/         }
/* 308*/      this._outputBuffer[this._outputTail++] = 123;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeEndObject() throws IOException {
/* 315*/    if (!this._writeContext.inObject()) {
/* 316*/        _reportError("Current context not an object but " + this._writeContext.getTypeDesc()); 
/*   0*/       }
/* 318*/    if (this._cfgPrettyPrinter != null) {
/* 319*/      this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
/*   0*/    } else {
/* 321*/      if (this._outputTail >= this._outputEnd) {
/* 322*/          _flushBuffer(); 
/*   0*/         }
/* 324*/      this._outputBuffer[this._outputTail++] = 125;
/*   0*/    } 
/* 326*/    this._writeContext = this._writeContext.clearAndGetParent();
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _writePPFieldName(String name) throws IOException {
/* 335*/    int status = this._writeContext.writeFieldName(name);
/* 336*/    if (status == 4) {
/* 337*/        _reportError("Can not write a field name, expecting a value"); 
/*   0*/       }
/* 339*/    if (status == 1) {
/* 340*/      this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*   0*/    } else {
/* 342*/      this._cfgPrettyPrinter.beforeObjectEntries(this);
/*   0*/    } 
/* 344*/    if (this._cfgUnqNames) {
/* 345*/      _writeStringSegments(name, false);
/*   0*/      return;
/*   0*/    } 
/* 348*/    int len = name.length();
/* 349*/    if (len > this._charBufferLength) {
/* 350*/      _writeStringSegments(name, true);
/*   0*/      return;
/*   0*/    } 
/* 353*/    if (this._outputTail >= this._outputEnd) {
/* 354*/        _flushBuffer(); 
/*   0*/       }
/* 356*/    this._outputBuffer[this._outputTail++] = 34;
/* 357*/    name.getChars(0, len, this._charBuffer, 0);
/* 359*/    if (len <= this._outputMaxContiguous) {
/* 360*/      if (this._outputTail + len > this._outputEnd) {
/* 361*/          _flushBuffer(); 
/*   0*/         }
/* 363*/      _writeStringSegment(this._charBuffer, 0, len);
/*   0*/    } else {
/* 365*/      _writeStringSegments(this._charBuffer, 0, len);
/*   0*/    } 
/* 367*/    if (this._outputTail >= this._outputEnd) {
/* 368*/        _flushBuffer(); 
/*   0*/       }
/* 370*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _writePPFieldName(SerializableString name) throws IOException {
/* 375*/    int status = this._writeContext.writeFieldName(name.getValue());
/* 376*/    if (status == 4) {
/* 377*/        _reportError("Can not write a field name, expecting a value"); 
/*   0*/       }
/* 379*/    if (status == 1) {
/* 380*/      this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*   0*/    } else {
/* 382*/      this._cfgPrettyPrinter.beforeObjectEntries(this);
/*   0*/    } 
/* 385*/    boolean addQuotes = !this._cfgUnqNames;
/* 386*/    if (addQuotes) {
/* 387*/      if (this._outputTail >= this._outputEnd) {
/* 388*/          _flushBuffer(); 
/*   0*/         }
/* 390*/      this._outputBuffer[this._outputTail++] = 34;
/*   0*/    } 
/* 392*/    _writeBytes(name.asQuotedUTF8());
/* 393*/    if (addQuotes) {
/* 394*/      if (this._outputTail >= this._outputEnd) {
/* 395*/          _flushBuffer(); 
/*   0*/         }
/* 397*/      this._outputBuffer[this._outputTail++] = 34;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeString(String text) throws IOException {
/* 410*/    _verifyValueWrite("write a string");
/* 411*/    if (text == null) {
/* 412*/      _writeNull();
/*   0*/      return;
/*   0*/    } 
/* 416*/    int len = text.length();
/* 417*/    if (len > this._outputMaxContiguous) {
/* 418*/      _writeStringSegments(text, true);
/*   0*/      return;
/*   0*/    } 
/* 421*/    if (this._outputTail + len >= this._outputEnd) {
/* 422*/        _flushBuffer(); 
/*   0*/       }
/* 424*/    this._outputBuffer[this._outputTail++] = 34;
/* 425*/    _writeStringSegment(text, 0, len);
/* 426*/    if (this._outputTail >= this._outputEnd) {
/* 427*/        _flushBuffer(); 
/*   0*/       }
/* 429*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeString(char[] text, int offset, int len) throws IOException {
/* 435*/    _verifyValueWrite("write a string");
/* 436*/    if (this._outputTail >= this._outputEnd) {
/* 437*/        _flushBuffer(); 
/*   0*/       }
/* 439*/    this._outputBuffer[this._outputTail++] = 34;
/* 441*/    if (len <= this._outputMaxContiguous) {
/* 442*/      if (this._outputTail + len > this._outputEnd) {
/* 443*/          _flushBuffer(); 
/*   0*/         }
/* 445*/      _writeStringSegment(text, offset, len);
/*   0*/    } else {
/* 447*/      _writeStringSegments(text, offset, len);
/*   0*/    } 
/* 450*/    if (this._outputTail >= this._outputEnd) {
/* 451*/        _flushBuffer(); 
/*   0*/       }
/* 453*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeString(SerializableString text) throws IOException {
/* 459*/    _verifyValueWrite("write a string");
/* 460*/    if (this._outputTail >= this._outputEnd) {
/* 461*/        _flushBuffer(); 
/*   0*/       }
/* 463*/    this._outputBuffer[this._outputTail++] = 34;
/* 464*/    int len = text.appendQuotedUTF8(this._outputBuffer, this._outputTail);
/* 465*/    if (len < 0) {
/* 466*/      _writeBytes(text.asQuotedUTF8());
/*   0*/    } else {
/* 468*/      this._outputTail += len;
/*   0*/    } 
/* 470*/    if (this._outputTail >= this._outputEnd) {
/* 471*/        _flushBuffer(); 
/*   0*/       }
/* 473*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
/* 479*/    _verifyValueWrite("write a string");
/* 480*/    if (this._outputTail >= this._outputEnd) {
/* 481*/        _flushBuffer(); 
/*   0*/       }
/* 483*/    this._outputBuffer[this._outputTail++] = 34;
/* 484*/    _writeBytes(text, offset, length);
/* 485*/    if (this._outputTail >= this._outputEnd) {
/* 486*/        _flushBuffer(); 
/*   0*/       }
/* 488*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeUTF8String(byte[] text, int offset, int len) throws IOException {
/* 494*/    _verifyValueWrite("write a string");
/* 495*/    if (this._outputTail >= this._outputEnd) {
/* 496*/        _flushBuffer(); 
/*   0*/       }
/* 498*/    this._outputBuffer[this._outputTail++] = 34;
/* 500*/    if (len <= this._outputMaxContiguous) {
/* 501*/      _writeUTF8Segment(text, offset, len);
/*   0*/    } else {
/* 503*/      _writeUTF8Segments(text, offset, len);
/*   0*/    } 
/* 505*/    if (this._outputTail >= this._outputEnd) {
/* 506*/        _flushBuffer(); 
/*   0*/       }
/* 508*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(String text) throws IOException {
/* 519*/    writeRaw(text, 0, text.length());
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(String text, int offset, int len) throws IOException {
/* 525*/    char[] buf = this._charBuffer;
/* 533*/    while (len > 0) {
/* 534*/      int len2 = Math.min(buf.length, len);
/* 535*/      text.getChars(offset, offset + len2, buf, 0);
/* 536*/      writeRaw(buf, 0, len2);
/* 539*/      offset += len2;
/* 540*/      len -= len2;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(SerializableString text) throws IOException {
/* 547*/    byte[] raw = text.asUnquotedUTF8();
/* 548*/    if (raw.length > 0) {
/* 549*/        _writeBytes(raw); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRawValue(SerializableString text) throws IOException {
/* 556*/    _verifyValueWrite("write a raw (unencoded) value");
/* 557*/    byte[] raw = text.asUnquotedUTF8();
/* 558*/    if (raw.length > 0) {
/* 559*/        _writeBytes(raw); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public final void writeRaw(char[] cbuf, int offset, int len) throws IOException {
/* 569*/    int len3 = len + len + len;
/* 570*/    if (this._outputTail + len3 > this._outputEnd) {
/* 572*/      if (this._outputEnd < len3) {
/* 573*/        _writeSegmentedRaw(cbuf, offset, len);
/*   0*/        return;
/*   0*/      } 
/* 577*/      _flushBuffer();
/*   0*/    } 
/* 580*/    len += offset;
/* 584*/    while (offset < len) {
/*   0*/      while (true) {
/* 587*/        int ch = cbuf[offset];
/* 588*/        if (ch > 127) {
/* 596*/          ch = cbuf[offset++];
/* 597*/          if (ch < 2048) {
/* 598*/            this._outputBuffer[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/* 599*/            this._outputBuffer[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/*   0*/            continue;
/*   0*/          } 
/* 601*/          offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
/*   0*/          continue;
/*   0*/        } 
/*   0*/        this._outputBuffer[this._outputTail++] = (byte)ch;
/*   0*/        if (++offset >= len) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRaw(char ch) throws IOException {
/* 609*/    if (this._outputTail + 3 >= this._outputEnd) {
/* 610*/        _flushBuffer(); 
/*   0*/       }
/* 612*/    byte[] bbuf = this._outputBuffer;
/* 613*/    if (ch <= '\u007F') {
/* 614*/      bbuf[this._outputTail++] = (byte)ch;
/* 615*/    } else if (ch < 'ࠀ') {
/* 616*/      bbuf[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/* 617*/      bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/*   0*/    } else {
/* 619*/      _outputRawMultiByteChar(ch, null, 0, 0);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeSegmentedRaw(char[] cbuf, int offset, int len) throws IOException {
/* 629*/    int end = this._outputEnd;
/* 630*/    byte[] bbuf = this._outputBuffer;
/* 631*/    int inputEnd = offset + len;
/* 634*/    while (offset < inputEnd) {
/*   0*/      while (true) {
/* 637*/        int ch = cbuf[offset];
/* 638*/        if (ch >= 128) {
/* 650*/          if (this._outputTail + 3 >= this._outputEnd) {
/* 651*/              _flushBuffer(); 
/*   0*/             }
/* 653*/          ch = cbuf[offset++];
/* 654*/          if (ch < 2048) {
/* 655*/            bbuf[this._outputTail++] = (byte)(0xC0 | ch >> 6);
/* 656*/            bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/*   0*/            continue;
/*   0*/          } 
/* 658*/          offset = _outputRawMultiByteChar(ch, cbuf, offset, inputEnd);
/*   0*/          continue;
/*   0*/        } 
/*   0*/        if (this._outputTail >= end) {
/*   0*/            _flushBuffer(); 
/*   0*/           }
/*   0*/        bbuf[this._outputTail++] = (byte)ch;
/*   0*/        if (++offset >= inputEnd) {
/*   0*/            break; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
/* 684*/    _verifyValueWrite("write a binary value");
/* 686*/    if (this._outputTail >= this._outputEnd) {
/* 687*/        _flushBuffer(); 
/*   0*/       }
/* 689*/    this._outputBuffer[this._outputTail++] = 34;
/* 690*/    _writeBinary(b64variant, data, offset, offset + len);
/* 692*/    if (this._outputTail >= this._outputEnd) {
/* 693*/        _flushBuffer(); 
/*   0*/       }
/* 695*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
/*   0*/    int bytes;
/* 703*/    _verifyValueWrite("write a binary value");
/* 705*/    if (this._outputTail >= this._outputEnd) {
/* 706*/        _flushBuffer(); 
/*   0*/       }
/* 708*/    this._outputBuffer[this._outputTail++] = 34;
/* 709*/    byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
/*   0*/    try {
/* 712*/      if (dataLength < 0) {
/* 713*/        bytes = _writeBinary(b64variant, data, encodingBuffer);
/*   0*/      } else {
/* 715*/        int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
/* 716*/        if (missing > 0) {
/* 717*/            _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")"); 
/*   0*/           }
/* 719*/        bytes = dataLength;
/*   0*/      } 
/*   0*/    } finally {
/* 722*/      this._ioContext.releaseBase64Buffer(encodingBuffer);
/*   0*/    } 
/* 725*/    if (this._outputTail >= this._outputEnd) {
/* 726*/        _flushBuffer(); 
/*   0*/       }
/* 728*/    this._outputBuffer[this._outputTail++] = 34;
/* 729*/    return bytes;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(short s) throws IOException {
/* 741*/    _verifyValueWrite("write a number");
/* 743*/    if (this._outputTail + 6 >= this._outputEnd) {
/* 744*/        _flushBuffer(); 
/*   0*/       }
/* 746*/    if (this._cfgNumbersAsStrings) {
/* 747*/      _writeQuotedShort(s);
/*   0*/      return;
/*   0*/    } 
/* 750*/    this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeQuotedShort(short s) throws IOException {
/* 754*/    if (this._outputTail + 8 >= this._outputEnd) {
/* 755*/        _flushBuffer(); 
/*   0*/       }
/* 757*/    this._outputBuffer[this._outputTail++] = 34;
/* 758*/    this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
/* 759*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(int i) throws IOException {
/* 765*/    _verifyValueWrite("write a number");
/* 767*/    if (this._outputTail + 11 >= this._outputEnd) {
/* 768*/        _flushBuffer(); 
/*   0*/       }
/* 770*/    if (this._cfgNumbersAsStrings) {
/* 771*/      _writeQuotedInt(i);
/*   0*/      return;
/*   0*/    } 
/* 774*/    this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeQuotedInt(int i) throws IOException {
/* 779*/    if (this._outputTail + 13 >= this._outputEnd) {
/* 780*/        _flushBuffer(); 
/*   0*/       }
/* 782*/    this._outputBuffer[this._outputTail++] = 34;
/* 783*/    this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/* 784*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(long l) throws IOException {
/* 790*/    _verifyValueWrite("write a number");
/* 791*/    if (this._cfgNumbersAsStrings) {
/* 792*/      _writeQuotedLong(l);
/*   0*/      return;
/*   0*/    } 
/* 795*/    if (this._outputTail + 21 >= this._outputEnd) {
/* 797*/        _flushBuffer(); 
/*   0*/       }
/* 799*/    this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeQuotedLong(long l) throws IOException {
/* 804*/    if (this._outputTail + 23 >= this._outputEnd) {
/* 805*/        _flushBuffer(); 
/*   0*/       }
/* 807*/    this._outputBuffer[this._outputTail++] = 34;
/* 808*/    this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/* 809*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(BigInteger value) throws IOException {
/* 815*/    _verifyValueWrite("write a number");
/* 816*/    if (value == null) {
/* 817*/      _writeNull();
/* 818*/    } else if (this._cfgNumbersAsStrings) {
/* 819*/      _writeQuotedRaw(value.toString());
/*   0*/    } else {
/* 821*/      writeRaw(value.toString());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(double d) throws IOException {
/* 829*/    if (this._cfgNumbersAsStrings || ((Double.isNaN(d) || Double.isInfinite(d)) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS.enabledIn(this._features))) {
/* 832*/      writeString(String.valueOf(d));
/*   0*/      return;
/*   0*/    } 
/* 836*/    _verifyValueWrite("write a number");
/* 837*/    writeRaw(String.valueOf(d));
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(float f) throws IOException {
/* 843*/    if (this._cfgNumbersAsStrings || ((Float.isNaN(f) || Float.isInfinite(f)) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS.enabledIn(this._features))) {
/* 847*/      writeString(String.valueOf(f));
/*   0*/      return;
/*   0*/    } 
/* 851*/    _verifyValueWrite("write a number");
/* 852*/    writeRaw(String.valueOf(f));
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(BigDecimal value) throws IOException {
/* 859*/    _verifyValueWrite("write a number");
/* 860*/    if (value == null) {
/* 861*/      _writeNull();
/* 862*/    } else if (this._cfgNumbersAsStrings) {
/* 863*/      String raw = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features) ? value.toPlainString() : value.toString();
/* 865*/      _writeQuotedRaw(raw);
/* 866*/    } else if (JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features)) {
/* 867*/      writeRaw(value.toPlainString());
/*   0*/    } else {
/* 869*/      writeRaw(value.toString());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNumber(String encodedValue) throws IOException {
/* 876*/    _verifyValueWrite("write a number");
/* 877*/    if (this._cfgNumbersAsStrings) {
/* 878*/      _writeQuotedRaw(encodedValue);
/*   0*/    } else {
/* 880*/      writeRaw(encodedValue);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeQuotedRaw(String value) throws IOException {
/* 886*/    if (this._outputTail >= this._outputEnd) {
/* 887*/        _flushBuffer(); 
/*   0*/       }
/* 889*/    this._outputBuffer[this._outputTail++] = 34;
/* 890*/    writeRaw(value);
/* 891*/    if (this._outputTail >= this._outputEnd) {
/* 892*/        _flushBuffer(); 
/*   0*/       }
/* 894*/    this._outputBuffer[this._outputTail++] = 34;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeBoolean(boolean state) throws IOException {
/* 900*/    _verifyValueWrite("write a boolean value");
/* 901*/    if (this._outputTail + 5 >= this._outputEnd) {
/* 902*/        _flushBuffer(); 
/*   0*/       }
/* 904*/    byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
/* 905*/    int len = keyword.length;
/* 906*/    System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
/* 907*/    this._outputTail += len;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeNull() throws IOException {
/* 913*/    _verifyValueWrite("write a null");
/* 914*/    _writeNull();
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _verifyValueWrite(String typeMsg) throws IOException {
/* 926*/    int status = this._writeContext.writeValue();
/* 927*/    if (status == 5) {
/* 928*/        _reportError("Can not " + typeMsg + ", expecting field name"); 
/*   0*/       }
/* 930*/    if (this._cfgPrettyPrinter == null) {
/*   0*/      byte b;
/* 932*/      switch (status) {
/*   0*/        case 1:
/* 934*/          b = 44;
/*   0*/          break;
/*   0*/        case 2:
/* 937*/          b = 58;
/*   0*/          break;
/*   0*/        case 3:
/* 940*/          if (this._rootValueSeparator != null) {
/* 941*/            byte[] raw = this._rootValueSeparator.asUnquotedUTF8();
/* 942*/            if (raw.length > 0) {
/* 943*/                _writeBytes(raw); 
/*   0*/               }
/*   0*/          } 
/*   0*/          return;
/*   0*/        default:
/*   0*/          return;
/*   0*/      } 
/* 951*/      if (this._outputTail >= this._outputEnd) {
/* 952*/          _flushBuffer(); 
/*   0*/         }
/* 954*/      this._outputBuffer[this._outputTail] = b;
/* 955*/      this._outputTail++;
/*   0*/      return;
/*   0*/    } 
/* 959*/    _verifyPrettyValueWrite(typeMsg, status);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
/* 965*/    switch (status) {
/*   0*/      case 1:
/* 967*/        this._cfgPrettyPrinter.writeArrayValueSeparator(this);
/*   0*/        break;
/*   0*/      case 2:
/* 970*/        this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
/*   0*/        break;
/*   0*/      case 3:
/* 973*/        this._cfgPrettyPrinter.writeRootValueSeparator(this);
/*   0*/        break;
/*   0*/      case 0:
/* 977*/        if (this._writeContext.inArray()) {
/* 978*/          this._cfgPrettyPrinter.beforeArrayValues(this);
/*   0*/          break;
/*   0*/        } 
/* 979*/        if (this._writeContext.inObject()) {
/* 980*/            this._cfgPrettyPrinter.beforeObjectEntries(this); 
/*   0*/           }
/*   0*/        break;
/*   0*/      default:
/* 984*/        _throwInternal();
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void flush() throws IOException {
/* 998*/    _flushBuffer();
/* 999*/    if (this._outputStream != null && 
/*1000*/      isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*1001*/        this._outputStream.flush(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/*1009*/    super.close();
/*1015*/    if (this._outputBuffer != null && isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
/*   0*/        while (true) {
/*1018*/          JsonStreamContext ctxt = getOutputContext();
/*1019*/          if (ctxt.inArray()) {
/*1020*/            writeEndArray();
/*   0*/            continue;
/*   0*/          } 
/*1021*/          if (ctxt.inObject()) {
/*1022*/            writeEndObject();
/*   0*/            continue;
/*   0*/          } 
/*   0*/          break;
/*   0*/        }  
/*   0*/       }
/*1028*/    _flushBuffer();
/*1029*/    this._outputTail = 0;
/*1037*/    if (this._outputStream != null) {
/*1038*/        if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
/*1039*/          this._outputStream.close();
/*1040*/        } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
/*1042*/          this._outputStream.flush();
/*   0*/        }  
/*   0*/       }
/*1046*/    _releaseBuffers();
/*   0*/  }
/*   0*/  
/*   0*/  protected void _releaseBuffers() {
/*1052*/    byte[] buf = this._outputBuffer;
/*1053*/    if (buf != null && this._bufferRecyclable) {
/*1054*/      this._outputBuffer = null;
/*1055*/      this._ioContext.releaseWriteEncodingBuffer(buf);
/*   0*/    } 
/*1057*/    char[] cbuf = this._charBuffer;
/*1058*/    if (cbuf != null) {
/*1059*/      this._charBuffer = null;
/*1060*/      this._ioContext.releaseConcatBuffer(cbuf);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeBytes(byte[] bytes) throws IOException {
/*1072*/    int len = bytes.length;
/*1073*/    if (this._outputTail + len > this._outputEnd) {
/*1074*/      _flushBuffer();
/*1076*/      if (len > 512) {
/*1077*/        this._outputStream.write(bytes, 0, len);
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/*1081*/    System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
/*1082*/    this._outputTail += len;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException {
/*1087*/    if (this._outputTail + len > this._outputEnd) {
/*1088*/      _flushBuffer();
/*1090*/      if (len > 512) {
/*1091*/        this._outputStream.write(bytes, offset, len);
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/*1095*/    System.arraycopy(bytes, offset, this._outputBuffer, this._outputTail, len);
/*1096*/    this._outputTail += len;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegments(String text, boolean addQuotes) throws IOException {
/*1114*/    if (addQuotes) {
/*1115*/      if (this._outputTail >= this._outputEnd) {
/*1116*/          _flushBuffer(); 
/*   0*/         }
/*1118*/      this._outputBuffer[this._outputTail++] = 34;
/*   0*/    } 
/*1121*/    int left = text.length();
/*1122*/    int offset = 0;
/*1124*/    while (left > 0) {
/*1125*/      int len = Math.min(this._outputMaxContiguous, left);
/*1126*/      if (this._outputTail + len > this._outputEnd) {
/*1127*/          _flushBuffer(); 
/*   0*/         }
/*1129*/      _writeStringSegment(text, offset, len);
/*1130*/      offset += len;
/*1131*/      left -= len;
/*   0*/    } 
/*1134*/    if (addQuotes) {
/*1135*/      if (this._outputTail >= this._outputEnd) {
/*1136*/          _flushBuffer(); 
/*   0*/         }
/*1138*/      this._outputBuffer[this._outputTail++] = 34;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegments(char[] cbuf, int offset, int totalLen) throws IOException {
/*   0*/    do {
/*1151*/      int len = Math.min(this._outputMaxContiguous, totalLen);
/*1152*/      if (this._outputTail + len > this._outputEnd) {
/*1153*/          _flushBuffer(); 
/*   0*/         }
/*1155*/      _writeStringSegment(cbuf, offset, len);
/*1156*/      offset += len;
/*1157*/      totalLen -= len;
/*1158*/    } while (totalLen > 0);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegments(String text, int offset, int totalLen) throws IOException {
/*   0*/    do {
/*1164*/      int len = Math.min(this._outputMaxContiguous, totalLen);
/*1165*/      if (this._outputTail + len > this._outputEnd) {
/*1166*/          _flushBuffer(); 
/*   0*/         }
/*1168*/      _writeStringSegment(text, offset, len);
/*1169*/      offset += len;
/*1170*/      totalLen -= len;
/*1171*/    } while (totalLen > 0);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegment(char[] cbuf, int offset, int len) throws IOException {
/*1194*/    len += offset;
/*1196*/    int outputPtr = this._outputTail;
/*1197*/    byte[] outputBuffer = this._outputBuffer;
/*1198*/    int[] escCodes = this._outputEscapes;
/*1200*/    while (offset < len) {
/*1201*/      int ch = cbuf[offset];
/*1203*/      if (ch > 127 || escCodes[ch] != 0) {
/*   0*/          break; 
/*   0*/         }
/*1206*/      outputBuffer[outputPtr++] = (byte)ch;
/*1207*/      offset++;
/*   0*/    } 
/*1209*/    this._outputTail = outputPtr;
/*1210*/    if (offset < len) {
/*1212*/        if (this._characterEscapes != null) {
/*1213*/          _writeCustomStringSegment2(cbuf, offset, len);
/*1215*/        } else if (this._maximumNonEscapedChar == 0) {
/*1216*/          _writeStringSegment2(cbuf, offset, len);
/*   0*/        } else {
/*1218*/          _writeStringSegmentASCII2(cbuf, offset, len);
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegment(String text, int offset, int len) throws IOException {
/*1228*/    len += offset;
/*1230*/    int outputPtr = this._outputTail;
/*1231*/    byte[] outputBuffer = this._outputBuffer;
/*1232*/    int[] escCodes = this._outputEscapes;
/*1234*/    while (offset < len) {
/*1235*/      int ch = text.charAt(offset);
/*1237*/      if (ch > 127 || escCodes[ch] != 0) {
/*   0*/          break; 
/*   0*/         }
/*1240*/      outputBuffer[outputPtr++] = (byte)ch;
/*1241*/      offset++;
/*   0*/    } 
/*1243*/    this._outputTail = outputPtr;
/*1244*/    if (offset < len) {
/*1245*/        if (this._characterEscapes != null) {
/*1246*/          _writeCustomStringSegment2(text, offset, len);
/*1247*/        } else if (this._maximumNonEscapedChar == 0) {
/*1248*/          _writeStringSegment2(text, offset, len);
/*   0*/        } else {
/*1250*/          _writeStringSegmentASCII2(text, offset, len);
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegment2(char[] cbuf, int offset, int end) throws IOException {
/*1262*/    if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/*1263*/        _flushBuffer(); 
/*   0*/       }
/*1266*/    int outputPtr = this._outputTail;
/*1268*/    byte[] outputBuffer = this._outputBuffer;
/*1269*/    int[] escCodes = this._outputEscapes;
/*1271*/    while (offset < end) {
/*1272*/      int ch = cbuf[offset++];
/*1273*/      if (ch <= 127) {
/*1274*/        if (escCodes[ch] == 0) {
/*1275*/          outputBuffer[outputPtr++] = (byte)ch;
/*   0*/          continue;
/*   0*/        } 
/*1278*/        int escape = escCodes[ch];
/*1279*/        if (escape > 0) {
/*1280*/          outputBuffer[outputPtr++] = 92;
/*1281*/          outputBuffer[outputPtr++] = (byte)escape;
/*   0*/          continue;
/*   0*/        } 
/*1284*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1288*/      if (ch <= 2047) {
/*1289*/        outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/*1290*/        outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/        continue;
/*   0*/      } 
/*1292*/      outputPtr = _outputMultiByteChar(ch, outputPtr);
/*   0*/    } 
/*1295*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegment2(String text, int offset, int end) throws IOException {
/*1300*/    if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/*1301*/        _flushBuffer(); 
/*   0*/       }
/*1304*/    int outputPtr = this._outputTail;
/*1306*/    byte[] outputBuffer = this._outputBuffer;
/*1307*/    int[] escCodes = this._outputEscapes;
/*1309*/    while (offset < end) {
/*1310*/      int ch = text.charAt(offset++);
/*1311*/      if (ch <= 127) {
/*1312*/        if (escCodes[ch] == 0) {
/*1313*/          outputBuffer[outputPtr++] = (byte)ch;
/*   0*/          continue;
/*   0*/        } 
/*1316*/        int escape = escCodes[ch];
/*1317*/        if (escape > 0) {
/*1318*/          outputBuffer[outputPtr++] = 92;
/*1319*/          outputBuffer[outputPtr++] = (byte)escape;
/*   0*/          continue;
/*   0*/        } 
/*1322*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1326*/      if (ch <= 2047) {
/*1327*/        outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/*1328*/        outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/        continue;
/*   0*/      } 
/*1330*/      outputPtr = _outputMultiByteChar(ch, outputPtr);
/*   0*/    } 
/*1333*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end) throws IOException {
/*1350*/    if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/*1351*/        _flushBuffer(); 
/*   0*/       }
/*1354*/    int outputPtr = this._outputTail;
/*1356*/    byte[] outputBuffer = this._outputBuffer;
/*1357*/    int[] escCodes = this._outputEscapes;
/*1358*/    int maxUnescaped = this._maximumNonEscapedChar;
/*1360*/    while (offset < end) {
/*1361*/      int ch = cbuf[offset++];
/*1362*/      if (ch <= 127) {
/*1363*/        if (escCodes[ch] == 0) {
/*1364*/          outputBuffer[outputPtr++] = (byte)ch;
/*   0*/          continue;
/*   0*/        } 
/*1367*/        int escape = escCodes[ch];
/*1368*/        if (escape > 0) {
/*1369*/          outputBuffer[outputPtr++] = 92;
/*1370*/          outputBuffer[outputPtr++] = (byte)escape;
/*   0*/          continue;
/*   0*/        } 
/*1373*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1377*/      if (ch > maxUnescaped) {
/*1378*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1381*/      if (ch <= 2047) {
/*1382*/        outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/*1383*/        outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/        continue;
/*   0*/      } 
/*1385*/      outputPtr = _outputMultiByteChar(ch, outputPtr);
/*   0*/    } 
/*1388*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeStringSegmentASCII2(String text, int offset, int end) throws IOException {
/*1394*/    if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/*1395*/        _flushBuffer(); 
/*   0*/       }
/*1398*/    int outputPtr = this._outputTail;
/*1400*/    byte[] outputBuffer = this._outputBuffer;
/*1401*/    int[] escCodes = this._outputEscapes;
/*1402*/    int maxUnescaped = this._maximumNonEscapedChar;
/*1404*/    while (offset < end) {
/*1405*/      int ch = text.charAt(offset++);
/*1406*/      if (ch <= 127) {
/*1407*/        if (escCodes[ch] == 0) {
/*1408*/          outputBuffer[outputPtr++] = (byte)ch;
/*   0*/          continue;
/*   0*/        } 
/*1411*/        int escape = escCodes[ch];
/*1412*/        if (escape > 0) {
/*1413*/          outputBuffer[outputPtr++] = 92;
/*1414*/          outputBuffer[outputPtr++] = (byte)escape;
/*   0*/          continue;
/*   0*/        } 
/*1417*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1421*/      if (ch > maxUnescaped) {
/*1422*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1425*/      if (ch <= 2047) {
/*1426*/        outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/*1427*/        outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/        continue;
/*   0*/      } 
/*1429*/      outputPtr = _outputMultiByteChar(ch, outputPtr);
/*   0*/    } 
/*1432*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeCustomStringSegment2(char[] cbuf, int offset, int end) throws IOException {
/*1449*/    if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/*1450*/        _flushBuffer(); 
/*   0*/       }
/*1452*/    int outputPtr = this._outputTail;
/*1454*/    byte[] outputBuffer = this._outputBuffer;
/*1455*/    int[] escCodes = this._outputEscapes;
/*1457*/    int maxUnescaped = (this._maximumNonEscapedChar <= 0) ? 65535 : this._maximumNonEscapedChar;
/*1458*/    CharacterEscapes customEscapes = this._characterEscapes;
/*1460*/    while (offset < end) {
/*1461*/      int ch = cbuf[offset++];
/*1462*/      if (ch <= 127) {
/*1463*/        if (escCodes[ch] == 0) {
/*1464*/          outputBuffer[outputPtr++] = (byte)ch;
/*   0*/          continue;
/*   0*/        } 
/*1467*/        int escape = escCodes[ch];
/*1468*/        if (escape > 0) {
/*1469*/          outputBuffer[outputPtr++] = 92;
/*1470*/          outputBuffer[outputPtr++] = (byte)escape;
/*   0*/          continue;
/*   0*/        } 
/*1471*/        if (escape == -2) {
/*1472*/          SerializableString serializableString = customEscapes.getEscapeSequence(ch);
/*1473*/          if (serializableString == null) {
/*1474*/              _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch) + ", although was supposed to have one"); 
/*   0*/             }
/*1477*/          outputPtr = _writeCustomEscape(outputBuffer, outputPtr, serializableString, end - offset);
/*   0*/          continue;
/*   0*/        } 
/*1480*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1484*/      if (ch > maxUnescaped) {
/*1485*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1488*/      SerializableString esc = customEscapes.getEscapeSequence(ch);
/*1489*/      if (esc != null) {
/*1490*/        outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*   0*/        continue;
/*   0*/      } 
/*1493*/      if (ch <= 2047) {
/*1494*/        outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/*1495*/        outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/        continue;
/*   0*/      } 
/*1497*/      outputPtr = _outputMultiByteChar(ch, outputPtr);
/*   0*/    } 
/*1500*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeCustomStringSegment2(String text, int offset, int end) throws IOException {
/*1506*/    if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/*1507*/        _flushBuffer(); 
/*   0*/       }
/*1509*/    int outputPtr = this._outputTail;
/*1511*/    byte[] outputBuffer = this._outputBuffer;
/*1512*/    int[] escCodes = this._outputEscapes;
/*1514*/    int maxUnescaped = (this._maximumNonEscapedChar <= 0) ? 65535 : this._maximumNonEscapedChar;
/*1515*/    CharacterEscapes customEscapes = this._characterEscapes;
/*1517*/    while (offset < end) {
/*1518*/      int ch = text.charAt(offset++);
/*1519*/      if (ch <= 127) {
/*1520*/        if (escCodes[ch] == 0) {
/*1521*/          outputBuffer[outputPtr++] = (byte)ch;
/*   0*/          continue;
/*   0*/        } 
/*1524*/        int escape = escCodes[ch];
/*1525*/        if (escape > 0) {
/*1526*/          outputBuffer[outputPtr++] = 92;
/*1527*/          outputBuffer[outputPtr++] = (byte)escape;
/*   0*/          continue;
/*   0*/        } 
/*1528*/        if (escape == -2) {
/*1529*/          SerializableString serializableString = customEscapes.getEscapeSequence(ch);
/*1530*/          if (serializableString == null) {
/*1531*/              _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch) + ", although was supposed to have one"); 
/*   0*/             }
/*1534*/          outputPtr = _writeCustomEscape(outputBuffer, outputPtr, serializableString, end - offset);
/*   0*/          continue;
/*   0*/        } 
/*1537*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1541*/      if (ch > maxUnescaped) {
/*1542*/        outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/        continue;
/*   0*/      } 
/*1545*/      SerializableString esc = customEscapes.getEscapeSequence(ch);
/*1546*/      if (esc != null) {
/*1547*/        outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*   0*/        continue;
/*   0*/      } 
/*1550*/      if (ch <= 2047) {
/*1551*/        outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
/*1552*/        outputBuffer[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/        continue;
/*   0*/      } 
/*1554*/      outputPtr = _outputMultiByteChar(ch, outputPtr);
/*   0*/    } 
/*1557*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars) throws IOException, JsonGenerationException {
/*1563*/    byte[] raw = esc.asUnquotedUTF8();
/*1564*/    int len = raw.length;
/*1565*/    if (len > 6) {
/*1566*/        return _handleLongCustomEscape(outputBuffer, outputPtr, this._outputEnd, raw, remainingChars); 
/*   0*/       }
/*1569*/    System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/*1570*/    return outputPtr + len;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars) throws IOException, JsonGenerationException {
/*1577*/    int len = raw.length;
/*1578*/    if (outputPtr + len > outputEnd) {
/*1579*/      this._outputTail = outputPtr;
/*1580*/      _flushBuffer();
/*1581*/      outputPtr = this._outputTail;
/*1582*/      if (len > outputBuffer.length) {
/*1583*/        this._outputStream.write(raw, 0, len);
/*1584*/        return outputPtr;
/*   0*/      } 
/*1586*/      System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/*1587*/      outputPtr += len;
/*   0*/    } 
/*1590*/    if (outputPtr + 6 * remainingChars > outputEnd) {
/*1591*/      _flushBuffer();
/*1592*/      return this._outputTail;
/*   0*/    } 
/*1594*/    return outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen) throws IOException, JsonGenerationException {
/*   0*/    do {
/*1612*/      int len = Math.min(this._outputMaxContiguous, totalLen);
/*1613*/      _writeUTF8Segment(utf8, offset, len);
/*1614*/      offset += len;
/*1615*/      totalLen -= len;
/*1616*/    } while (totalLen > 0);
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeUTF8Segment(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
/*1623*/    int[] escCodes = this._outputEscapes;
/*1625*/    for (int ptr = offset, end = offset + len; ptr < end; ) {
/*1627*/      int ch = utf8[ptr++];
/*1628*/      if (ch >= 0 && escCodes[ch] != 0) {
/*1629*/        _writeUTF8Segment2(utf8, offset, len);
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/*1635*/    if (this._outputTail + len > this._outputEnd) {
/*1636*/        _flushBuffer(); 
/*   0*/       }
/*1638*/    System.arraycopy(utf8, offset, this._outputBuffer, this._outputTail, len);
/*1639*/    this._outputTail += len;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeUTF8Segment2(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
/*1645*/    int outputPtr = this._outputTail;
/*1648*/    if (outputPtr + len * 6 > this._outputEnd) {
/*1649*/      _flushBuffer();
/*1650*/      outputPtr = this._outputTail;
/*   0*/    } 
/*1653*/    byte[] outputBuffer = this._outputBuffer;
/*1654*/    int[] escCodes = this._outputEscapes;
/*1655*/    len += offset;
/*1657*/    while (offset < len) {
/*1658*/      byte b = utf8[offset++];
/*1659*/      int ch = b;
/*1660*/      if (ch < 0 || escCodes[ch] == 0) {
/*1661*/        outputBuffer[outputPtr++] = b;
/*   0*/        continue;
/*   0*/      } 
/*1664*/      int escape = escCodes[ch];
/*1665*/      if (escape > 0) {
/*1666*/        outputBuffer[outputPtr++] = 92;
/*1667*/        outputBuffer[outputPtr++] = (byte)escape;
/*   0*/        continue;
/*   0*/      } 
/*1670*/      outputPtr = _writeGenericEscape(ch, outputPtr);
/*   0*/    } 
/*1673*/    this._outputTail = outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
/*1687*/    int safeInputEnd = inputEnd - 3;
/*1689*/    int safeOutputEnd = this._outputEnd - 6;
/*1690*/    int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*1693*/    while (inputPtr <= safeInputEnd) {
/*1694*/      if (this._outputTail > safeOutputEnd) {
/*1695*/          _flushBuffer(); 
/*   0*/         }
/*1698*/      int b24 = input[inputPtr++] << 8;
/*1699*/      b24 |= input[inputPtr++] & 0xFF;
/*1700*/      b24 = b24 << 8 | input[inputPtr++] & 0xFF;
/*1701*/      this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/*1702*/      if (--chunksBeforeLF <= 0) {
/*1704*/        this._outputBuffer[this._outputTail++] = 92;
/*1705*/        this._outputBuffer[this._outputTail++] = 110;
/*1706*/        chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*   0*/      } 
/*   0*/    } 
/*1711*/    int inputLeft = inputEnd - inputPtr;
/*1712*/    if (inputLeft > 0) {
/*1713*/      if (this._outputTail > safeOutputEnd) {
/*1714*/          _flushBuffer(); 
/*   0*/         }
/*1716*/      int b24 = input[inputPtr++] << 16;
/*1717*/      if (inputLeft == 2) {
/*1718*/          b24 |= (input[inputPtr++] & 0xFF) << 8; 
/*   0*/         }
/*1720*/      this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
/*1729*/    int inputPtr = 0;
/*1730*/    int inputEnd = 0;
/*1731*/    int lastFullOffset = -3;
/*1734*/    int safeOutputEnd = this._outputEnd - 6;
/*1735*/    int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*1737*/    while (bytesLeft > 2) {
/*1738*/      if (inputPtr > lastFullOffset) {
/*1739*/        inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/*1740*/        inputPtr = 0;
/*1741*/        if (inputEnd < 3) {
/*   0*/            break; 
/*   0*/           }
/*1744*/        lastFullOffset = inputEnd - 3;
/*   0*/      } 
/*1746*/      if (this._outputTail > safeOutputEnd) {
/*1747*/          _flushBuffer(); 
/*   0*/         }
/*1749*/      int b24 = readBuffer[inputPtr++] << 8;
/*1750*/      b24 |= readBuffer[inputPtr++] & 0xFF;
/*1751*/      b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/*1752*/      bytesLeft -= 3;
/*1753*/      this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/*1754*/      if (--chunksBeforeLF <= 0) {
/*1755*/        this._outputBuffer[this._outputTail++] = 92;
/*1756*/        this._outputBuffer[this._outputTail++] = 110;
/*1757*/        chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*   0*/      } 
/*   0*/    } 
/*1762*/    if (bytesLeft > 0) {
/*1763*/      inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
/*1764*/      inputPtr = 0;
/*1765*/      if (inputEnd > 0) {
/*   0*/        int amount;
/*1766*/        if (this._outputTail > safeOutputEnd) {
/*1767*/            _flushBuffer(); 
/*   0*/           }
/*1769*/        int b24 = readBuffer[inputPtr++] << 16;
/*1771*/        if (inputPtr < inputEnd) {
/*1772*/          b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/*1773*/          amount = 2;
/*   0*/        } else {
/*1775*/          amount = 1;
/*   0*/        } 
/*1777*/        this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*1778*/        bytesLeft -= amount;
/*   0*/      } 
/*   0*/    } 
/*1781*/    return bytesLeft;
/*   0*/  }
/*   0*/  
/*   0*/  protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
/*1789*/    int inputPtr = 0;
/*1790*/    int inputEnd = 0;
/*1791*/    int lastFullOffset = -3;
/*1792*/    int bytesDone = 0;
/*1795*/    int safeOutputEnd = this._outputEnd - 6;
/*1796*/    int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*   0*/    while (true) {
/*1800*/      if (inputPtr > lastFullOffset) {
/*1801*/        inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
/*1802*/        inputPtr = 0;
/*1803*/        if (inputEnd < 3) {
/*   0*/            break; 
/*   0*/           }
/*1806*/        lastFullOffset = inputEnd - 3;
/*   0*/      } 
/*1808*/      if (this._outputTail > safeOutputEnd) {
/*1809*/          _flushBuffer(); 
/*   0*/         }
/*1812*/      int b24 = readBuffer[inputPtr++] << 8;
/*1813*/      b24 |= readBuffer[inputPtr++] & 0xFF;
/*1814*/      b24 = b24 << 8 | readBuffer[inputPtr++] & 0xFF;
/*1815*/      bytesDone += 3;
/*1816*/      this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/*1817*/      if (--chunksBeforeLF <= 0) {
/*1818*/        this._outputBuffer[this._outputTail++] = 92;
/*1819*/        this._outputBuffer[this._outputTail++] = 110;
/*1820*/        chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*   0*/      } 
/*   0*/    } 
/*1825*/    if (inputPtr < inputEnd) {
/*1826*/      if (this._outputTail > safeOutputEnd) {
/*1827*/          _flushBuffer(); 
/*   0*/         }
/*1829*/      int b24 = readBuffer[inputPtr++] << 16;
/*1830*/      int amount = 1;
/*1831*/      if (inputPtr < inputEnd) {
/*1832*/        b24 |= (readBuffer[inputPtr] & 0xFF) << 8;
/*1833*/        amount = 2;
/*   0*/      } 
/*1835*/      bytesDone += amount;
/*1836*/      this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
/*   0*/    } 
/*1838*/    return bytesDone;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
/*1846*/    int i = 0;
/*1847*/    while (inputPtr < inputEnd) {
/*1848*/        readBuffer[i++] = readBuffer[inputPtr++]; 
/*   0*/       }
/*1850*/    inputPtr = 0;
/*1851*/    inputEnd = i;
/*1852*/    maxRead = Math.min(maxRead, readBuffer.length);
/*   0*/    do {
/*1855*/      int length = maxRead - inputEnd;
/*1856*/      if (length == 0) {
/*   0*/          break; 
/*   0*/         }
/*1859*/      int count = in.read(readBuffer, inputEnd, length);
/*1860*/      if (count < 0) {
/*1861*/          return inputEnd; 
/*   0*/         }
/*1863*/      inputEnd += count;
/*1864*/    } while (inputEnd < 3);
/*1865*/    return inputEnd;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputEnd) throws IOException {
/*1883*/    if (this._outputTail >= 55296 && 
/*1884*/      ch <= 57343) {
/*1886*/      if (inputOffset >= inputEnd || cbuf == null) {
/*1887*/          _reportError("Split surrogate on writeRaw() input (last character)"); 
/*   0*/         }
/*1889*/      _outputSurrogates(ch, cbuf[inputOffset]);
/*1890*/      return inputOffset + 1;
/*   0*/    } 
/*1893*/    byte[] bbuf = this._outputBuffer;
/*1894*/    bbuf[this._outputTail++] = (byte)(0xE0 | ch >> 12);
/*1895*/    bbuf[this._outputTail++] = (byte)(0x80 | ch >> 6 & 0x3F);
/*1896*/    bbuf[this._outputTail++] = (byte)(0x80 | ch & 0x3F);
/*1897*/    return inputOffset;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _outputSurrogates(int surr1, int surr2) throws IOException {
/*1902*/    int c = _decodeSurrogate(surr1, surr2);
/*1903*/    if (this._outputTail + 4 > this._outputEnd) {
/*1904*/        _flushBuffer(); 
/*   0*/       }
/*1906*/    byte[] bbuf = this._outputBuffer;
/*1907*/    bbuf[this._outputTail++] = (byte)(0xF0 | c >> 18);
/*1908*/    bbuf[this._outputTail++] = (byte)(0x80 | c >> 12 & 0x3F);
/*1909*/    bbuf[this._outputTail++] = (byte)(0x80 | c >> 6 & 0x3F);
/*1910*/    bbuf[this._outputTail++] = (byte)(0x80 | c & 0x3F);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _outputMultiByteChar(int ch, int outputPtr) throws IOException {
/*1924*/    byte[] bbuf = this._outputBuffer;
/*1925*/    if (ch >= 55296 && ch <= 57343) {
/*1930*/      bbuf[outputPtr++] = 92;
/*1931*/      bbuf[outputPtr++] = 117;
/*1933*/      bbuf[outputPtr++] = HEX_CHARS[ch >> 12 & 0xF];
/*1934*/      bbuf[outputPtr++] = HEX_CHARS[ch >> 8 & 0xF];
/*1935*/      bbuf[outputPtr++] = HEX_CHARS[ch >> 4 & 0xF];
/*1936*/      bbuf[outputPtr++] = HEX_CHARS[ch & 0xF];
/*   0*/    } else {
/*1939*/      bbuf[outputPtr++] = (byte)(0xE0 | ch >> 12);
/*1940*/      bbuf[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
/*1941*/      bbuf[outputPtr++] = (byte)(0x80 | ch & 0x3F);
/*   0*/    } 
/*1943*/    return outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeNull() throws IOException {
/*1948*/    if (this._outputTail + 4 >= this._outputEnd) {
/*1949*/        _flushBuffer(); 
/*   0*/       }
/*1951*/    System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
/*1952*/    this._outputTail += 4;
/*   0*/  }
/*   0*/  
/*   0*/  private int _writeGenericEscape(int charToEscape, int outputPtr) throws IOException {
/*1962*/    byte[] bbuf = this._outputBuffer;
/*1963*/    bbuf[outputPtr++] = 92;
/*1964*/    bbuf[outputPtr++] = 117;
/*1965*/    if (charToEscape > 255) {
/*1966*/      int hi = charToEscape >> 8 & 0xFF;
/*1967*/      bbuf[outputPtr++] = HEX_CHARS[hi >> 4];
/*1968*/      bbuf[outputPtr++] = HEX_CHARS[hi & 0xF];
/*1969*/      charToEscape &= 0xFF;
/*   0*/    } else {
/*1971*/      bbuf[outputPtr++] = 48;
/*1972*/      bbuf[outputPtr++] = 48;
/*   0*/    } 
/*1975*/    bbuf[outputPtr++] = HEX_CHARS[charToEscape >> 4];
/*1976*/    bbuf[outputPtr++] = HEX_CHARS[charToEscape & 0xF];
/*1977*/    return outputPtr;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _flushBuffer() throws IOException {
/*1982*/    int len = this._outputTail;
/*1983*/    if (len > 0) {
/*1984*/      this._outputTail = 0;
/*1985*/      this._outputStream.write(this._outputBuffer, 0, len);
/*   0*/    } 
/*   0*/  }
/*   0*/}
