/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.Reader;
/*   0*/import java.io.StringReader;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Locale;
/*   0*/import org.jsoup.UncheckedIOException;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/
/*   0*/public final class CharacterReader {
/*   0*/  static final char EOF = '￿';
/*   0*/  
/*   0*/  private static final int maxStringCacheLen = 12;
/*   0*/  
/*   0*/  static final int maxBufferLen = 32768;
/*   0*/  
/*   0*/  private static final int readAheadLimit = 24576;
/*   0*/  
/*   0*/  private final char[] charBuf;
/*   0*/  
/*   0*/  private final Reader reader;
/*   0*/  
/*   0*/  private int bufLength;
/*   0*/  
/*   0*/  private int bufSplitPoint;
/*   0*/  
/*   0*/  private int bufPos;
/*   0*/  
/*   0*/  private int readerPos;
/*   0*/  
/*   0*/  private int bufMark;
/*   0*/  
/*  28*/  private final String[] stringCache = new String[512];
/*   0*/  
/*   0*/  public CharacterReader(Reader input, int sz) {
/*  31*/    Validate.notNull(input);
/*  32*/    Validate.isTrue(input.markSupported());
/*  33*/    this.reader = input;
/*  34*/    this.charBuf = new char[(sz > 32768) ? 32768 : sz];
/*  35*/    bufferUp();
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterReader(Reader input) {
/*  39*/    this(input, 32768);
/*   0*/  }
/*   0*/  
/*   0*/  public CharacterReader(String input) {
/*  43*/    this(new StringReader(input), input.length());
/*   0*/  }
/*   0*/  
/*   0*/  private void bufferUp() {
/*  47*/    if (this.bufPos < this.bufSplitPoint) {
/*   0*/        return; 
/*   0*/       }
/*   0*/    try {
/*  51*/      this.readerPos += this.bufPos;
/*  52*/      this.reader.skip(this.bufPos);
/*  53*/      this.reader.mark(32768);
/*  54*/      this.bufLength = this.reader.read(this.charBuf);
/*  55*/      this.reader.reset();
/*  56*/      this.bufPos = 0;
/*  57*/      this.bufMark = 0;
/*  58*/      this.bufSplitPoint = (this.bufLength > 24576) ? 24576 : this.bufLength;
/*  59*/    } catch (IOException e) {
/*  60*/      throw new UncheckedIOException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int pos() {
/*  69*/    return this.readerPos + this.bufPos;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/*  77*/    return (this.bufPos >= this.bufLength);
/*   0*/  }
/*   0*/  
/*   0*/  public char current() {
/*  85*/    bufferUp();
/*  86*/    return isEmpty() ? Character.MAX_VALUE : this.charBuf[this.bufPos];
/*   0*/  }
/*   0*/  
/*   0*/  char consume() {
/*  90*/    bufferUp();
/*  91*/    char val = isEmpty() ? Character.MAX_VALUE : this.charBuf[this.bufPos];
/*  92*/    this.bufPos++;
/*  93*/    return val;
/*   0*/  }
/*   0*/  
/*   0*/  void unconsume() {
/*  97*/    this.bufPos--;
/*   0*/  }
/*   0*/  
/*   0*/  public void advance() {
/* 104*/    this.bufPos++;
/*   0*/  }
/*   0*/  
/*   0*/  void mark() {
/* 108*/    this.bufMark = this.bufPos;
/*   0*/  }
/*   0*/  
/*   0*/  void rewindToMark() {
/* 112*/    this.bufPos = this.bufMark;
/*   0*/  }
/*   0*/  
/*   0*/  int nextIndexOf(char c) {
/* 122*/    bufferUp();
/* 123*/    for (int i = this.bufPos; i < this.bufLength; i++) {
/* 124*/      if (c == this.charBuf[i]) {
/* 125*/          return i - this.bufPos; 
/*   0*/         }
/*   0*/    } 
/* 127*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  int nextIndexOf(CharSequence seq) {
/* 137*/    bufferUp();
/* 139*/    char startChar = seq.charAt(0);
/* 140*/    for (int offset = this.bufPos; offset < this.bufLength; offset++) {
/* 142*/      if (startChar != this.charBuf[offset]) {
/* 143*/          while (++offset < this.bufLength && startChar != this.charBuf[offset]); 
/*   0*/         }
/* 144*/      int i = offset + 1;
/* 145*/      int last = i + seq.length() - 1;
/* 146*/      if (offset < this.bufLength && last <= this.bufLength) {
/* 147*/        for (int j = 1; i < last && seq.charAt(j) == this.charBuf[i]; ) {
/* 147*/          i++;
/* 147*/          j++;
/*   0*/        } 
/* 148*/        if (i == last) {
/* 149*/            return offset - this.bufPos; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 152*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public String consumeTo(char c) {
/* 161*/    int offset = nextIndexOf(c);
/* 162*/    if (offset != -1) {
/* 163*/      String consumed = cacheString(this.charBuf, this.stringCache, this.bufPos, offset);
/* 164*/      this.bufPos += offset;
/* 165*/      return consumed;
/*   0*/    } 
/* 167*/    return consumeToEnd();
/*   0*/  }
/*   0*/  
/*   0*/  String consumeTo(String seq) {
/* 172*/    int offset = nextIndexOf(seq);
/* 173*/    if (offset != -1) {
/* 174*/      String consumed = cacheString(this.charBuf, this.stringCache, this.bufPos, offset);
/* 175*/      this.bufPos += offset;
/* 176*/      return consumed;
/*   0*/    } 
/* 178*/    return consumeToEnd();
/*   0*/  }
/*   0*/  
/*   0*/  public String consumeToAny(char... chars) {
/* 188*/    bufferUp();
/* 189*/    int start = this.bufPos;
/* 190*/    int remaining = this.bufLength;
/* 191*/    char[] val = this.charBuf;
/* 193*/    label18: while (this.bufPos < remaining) {
/* 194*/      for (char c : chars) {
/* 195*/        if (val[this.bufPos] == c) {
/*   0*/            break label18; 
/*   0*/           }
/*   0*/      } 
/* 198*/      this.bufPos++;
/*   0*/    } 
/* 201*/    return (this.bufPos > start) ? cacheString(this.charBuf, this.stringCache, start, this.bufPos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeToAnySorted(char... chars) {
/* 205*/    bufferUp();
/* 206*/    int start = this.bufPos;
/* 207*/    int remaining = this.bufLength;
/* 208*/    char[] val = this.charBuf;
/* 210*/    while (this.bufPos < remaining && 
/* 211*/      Arrays.binarySearch(chars, val[this.bufPos]) < 0) {
/* 213*/        this.bufPos++; 
/*   0*/       }
/* 216*/    return (this.bufPos > start) ? cacheString(this.charBuf, this.stringCache, start, this.bufPos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeData() {
/* 221*/    bufferUp();
/* 222*/    int start = this.bufPos;
/* 223*/    int remaining = this.bufLength;
/* 224*/    char[] val = this.charBuf;
/* 226*/    while (this.bufPos < remaining) {
/* 227*/      char c = val[this.bufPos];
/* 228*/      if (c == '&' || c == '<' || c == '\000') {
/*   0*/          break; 
/*   0*/         }
/* 230*/      this.bufPos++;
/*   0*/    } 
/* 233*/    return (this.bufPos > start) ? cacheString(this.charBuf, this.stringCache, start, this.bufPos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeTagName() {
/* 238*/    bufferUp();
/* 239*/    int start = this.bufPos;
/* 240*/    int remaining = this.bufLength;
/* 241*/    char[] val = this.charBuf;
/* 243*/    while (this.bufPos < remaining) {
/* 244*/      char c = val[this.bufPos];
/* 245*/      if (c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == ' ' || c == '/' || c == '>' || c == '\000') {
/*   0*/          break; 
/*   0*/         }
/* 247*/      this.bufPos++;
/*   0*/    } 
/* 250*/    return (this.bufPos > start) ? cacheString(this.charBuf, this.stringCache, start, this.bufPos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeToEnd() {
/* 254*/    bufferUp();
/* 255*/    String data = cacheString(this.charBuf, this.stringCache, this.bufPos, this.bufLength - this.bufPos);
/* 256*/    this.bufPos = this.bufLength;
/* 257*/    return data;
/*   0*/  }
/*   0*/  
/*   0*/  String consumeLetterSequence() {
/* 261*/    bufferUp();
/* 262*/    int start = this.bufPos;
/* 263*/    while (this.bufPos < this.bufLength) {
/* 264*/      char c = this.charBuf[this.bufPos];
/* 265*/      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c)) {
/* 266*/          this.bufPos++; 
/*   0*/         }
/*   0*/    } 
/* 271*/    return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
/*   0*/  }
/*   0*/  
/*   0*/  String consumeLetterThenDigitSequence() {
/* 275*/    bufferUp();
/* 276*/    int start = this.bufPos;
/* 277*/    while (this.bufPos < this.bufLength) {
/* 278*/      char c = this.charBuf[this.bufPos];
/* 279*/      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c)) {
/* 280*/          this.bufPos++; 
/*   0*/         }
/*   0*/    } 
/* 284*/    while (!isEmpty()) {
/* 285*/      char c = this.charBuf[this.bufPos];
/* 286*/      if (c >= '0' && c <= '9') {
/* 287*/          this.bufPos++; 
/*   0*/         }
/*   0*/    } 
/* 292*/    return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
/*   0*/  }
/*   0*/  
/*   0*/  String consumeHexSequence() {
/* 296*/    bufferUp();
/* 297*/    int start = this.bufPos;
/* 298*/    while (this.bufPos < this.bufLength) {
/* 299*/      char c = this.charBuf[this.bufPos];
/* 300*/      if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
/* 301*/          this.bufPos++; 
/*   0*/         }
/*   0*/    } 
/* 305*/    return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
/*   0*/  }
/*   0*/  
/*   0*/  String consumeDigitSequence() {
/* 309*/    bufferUp();
/* 310*/    int start = this.bufPos;
/* 311*/    while (this.bufPos < this.bufLength) {
/* 312*/      char c = this.charBuf[this.bufPos];
/* 313*/      if (c >= '0' && c <= '9') {
/* 314*/          this.bufPos++; 
/*   0*/         }
/*   0*/    } 
/* 318*/    return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
/*   0*/  }
/*   0*/  
/*   0*/  boolean matches(char c) {
/* 322*/    return (!isEmpty() && this.charBuf[this.bufPos] == c);
/*   0*/  }
/*   0*/  
/*   0*/  boolean matches(String seq) {
/* 327*/    bufferUp();
/* 328*/    int scanLength = seq.length();
/* 329*/    if (scanLength > this.bufLength - this.bufPos) {
/* 330*/        return false; 
/*   0*/       }
/* 332*/    for (int offset = 0; offset < scanLength; offset++) {
/* 333*/      if (seq.charAt(offset) != this.charBuf[this.bufPos + offset]) {
/* 334*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 335*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesIgnoreCase(String seq) {
/* 339*/    bufferUp();
/* 340*/    int scanLength = seq.length();
/* 341*/    if (scanLength > this.bufLength - this.bufPos) {
/* 342*/        return false; 
/*   0*/       }
/* 344*/    for (int offset = 0; offset < scanLength; offset++) {
/* 345*/      char upScan = Character.toUpperCase(seq.charAt(offset));
/* 346*/      char upTarget = Character.toUpperCase(this.charBuf[this.bufPos + offset]);
/* 347*/      if (upScan != upTarget) {
/* 348*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 350*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesAny(char... seq) {
/* 354*/    if (isEmpty()) {
/* 355*/        return false; 
/*   0*/       }
/* 357*/    bufferUp();
/* 358*/    char c = this.charBuf[this.bufPos];
/* 359*/    for (char seek : seq) {
/* 360*/      if (seek == c) {
/* 361*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 363*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesAnySorted(char[] seq) {
/* 367*/    bufferUp();
/* 368*/    return (!isEmpty() && Arrays.binarySearch(seq, this.charBuf[this.bufPos]) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesLetter() {
/* 372*/    if (isEmpty()) {
/* 373*/        return false; 
/*   0*/       }
/* 374*/    char c = this.charBuf[this.bufPos];
/* 375*/    return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c));
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesDigit() {
/* 379*/    if (isEmpty()) {
/* 380*/        return false; 
/*   0*/       }
/* 381*/    char c = this.charBuf[this.bufPos];
/* 382*/    return (c >= '0' && c <= '9');
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchConsume(String seq) {
/* 386*/    bufferUp();
/* 387*/    if (matches(seq)) {
/* 388*/      this.bufPos += seq.length();
/* 389*/      return true;
/*   0*/    } 
/* 391*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchConsumeIgnoreCase(String seq) {
/* 396*/    if (matchesIgnoreCase(seq)) {
/* 397*/      this.bufPos += seq.length();
/* 398*/      return true;
/*   0*/    } 
/* 400*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean containsIgnoreCase(String seq) {
/* 406*/    String loScan = seq.toLowerCase(Locale.ENGLISH);
/* 407*/    String hiScan = seq.toUpperCase(Locale.ENGLISH);
/* 408*/    return (nextIndexOf(loScan) > -1 || nextIndexOf(hiScan) > -1);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 413*/    return new String(this.charBuf, this.bufPos, this.bufLength - this.bufPos);
/*   0*/  }
/*   0*/  
/*   0*/  private static String cacheString(char[] charBuf, String[] stringCache, int start, int count) {
/* 425*/    if (count > 12) {
/* 426*/        return new String(charBuf, start, count); 
/*   0*/       }
/* 429*/    int hash = 0;
/* 430*/    int offset = start;
/* 431*/    for (int i = 0; i < count; i++) {
/* 432*/        hash = 31 * hash + charBuf[offset++]; 
/*   0*/       }
/* 436*/    int index = hash & stringCache.length - 1;
/* 437*/    String cached = stringCache[index];
/* 439*/    if (cached == null) {
/* 440*/      cached = new String(charBuf, start, count);
/* 441*/      stringCache[index] = cached;
/*   0*/    } else {
/* 443*/      if (rangeEquals(charBuf, start, count, cached)) {
/* 444*/          return cached; 
/*   0*/         }
/* 446*/      cached = new String(charBuf, start, count);
/* 447*/      stringCache[index] = cached;
/*   0*/    } 
/* 450*/    return cached;
/*   0*/  }
/*   0*/  
/*   0*/  static boolean rangeEquals(char[] charBuf, int start, int count, String cached) {
/* 457*/    if (count == cached.length()) {
/* 458*/      int i = start;
/* 459*/      int j = 0;
/* 460*/      while (count-- != 0) {
/* 461*/        if (charBuf[i++] != cached.charAt(j++)) {
/* 462*/            return false; 
/*   0*/           }
/*   0*/      } 
/* 464*/      return true;
/*   0*/    } 
/* 466*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean rangeEquals(int start, int count, String cached) {
/* 471*/    return rangeEquals(this.charBuf, start, count, cached);
/*   0*/  }
/*   0*/}
