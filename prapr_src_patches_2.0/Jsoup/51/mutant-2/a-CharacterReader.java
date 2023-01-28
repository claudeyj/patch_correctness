/*   0*/package org.jsoup.parser;
/*   0*/
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Locale;
/*   0*/import org.jsoup.helper.Validate;
/*   0*/
/*   0*/final class CharacterReader {
/*   0*/  static final char EOF = '￿';
/*   0*/  
/*   0*/  private static final int maxCacheLen = 12;
/*   0*/  
/*   0*/  private final char[] input;
/*   0*/  
/*   0*/  private final int length;
/*   0*/  
/*  17*/  private int pos = 0;
/*   0*/  
/*  18*/  private int mark = 0;
/*   0*/  
/*  19*/  private final String[] stringCache = new String[512];
/*   0*/  
/*   0*/  CharacterReader(String input) {
/*  22*/    Validate.notNull(input);
/*  23*/    this.input = input.toCharArray();
/*  24*/    this.length = this.input.length;
/*   0*/  }
/*   0*/  
/*   0*/  int pos() {
/*  28*/    return this.pos;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isEmpty() {
/*  32*/    return (this.pos >= this.length);
/*   0*/  }
/*   0*/  
/*   0*/  char current() {
/*  36*/    return (this.pos >= this.length) ? Character.MAX_VALUE : this.input[this.pos];
/*   0*/  }
/*   0*/  
/*   0*/  char consume() {
/*  40*/    char val = (this.pos >= this.length) ? Character.MAX_VALUE : this.input[this.pos];
/*  41*/    this.pos++;
/*  42*/    return val;
/*   0*/  }
/*   0*/  
/*   0*/  void unconsume() {
/*  46*/    this.pos--;
/*   0*/  }
/*   0*/  
/*   0*/  void advance() {
/*  50*/    this.pos++;
/*   0*/  }
/*   0*/  
/*   0*/  void mark() {
/*  54*/    this.mark = this.pos;
/*   0*/  }
/*   0*/  
/*   0*/  void rewindToMark() {
/*  58*/    this.pos = this.mark;
/*   0*/  }
/*   0*/  
/*   0*/  String consumeAsString() {
/*  62*/    return new String(this.input, this.pos++, 1);
/*   0*/  }
/*   0*/  
/*   0*/  int nextIndexOf(char c) {
/*  72*/    for (int i = this.pos; i < this.length; i++) {
/*  73*/      if (c == this.input[i]) {
/*  74*/          return i - this.pos; 
/*   0*/         }
/*   0*/    } 
/*  76*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  int nextIndexOf(CharSequence seq) {
/*  87*/    char startChar = seq.charAt(0);
/*  88*/    for (int offset = this.pos; offset < this.length; offset++) {
/*  90*/      if (startChar != this.input[offset]) {
/*  91*/          while (++offset < this.length && startChar != this.input[offset]); 
/*   0*/         }
/*  92*/      int i = offset + 1;
/*  93*/      int last = i + seq.length() - 1;
/*  94*/      if (offset < this.length && last <= this.length) {
/*  95*/        for (int j = 1; i < last && seq.charAt(j) == this.input[i]; ) {
/*  95*/          i++;
/*  95*/          j++;
/*   0*/        } 
/*  96*/        if (i == last) {
/*  97*/            return offset - this.pos; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 100*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  String consumeTo(char c) {
/* 104*/    int offset = nextIndexOf(c);
/* 105*/    if (offset != -1) {
/* 106*/      String consumed = cacheString(this.pos, offset);
/* 107*/      this.pos += offset;
/* 108*/      return consumed;
/*   0*/    } 
/* 110*/    return consumeToEnd();
/*   0*/  }
/*   0*/  
/*   0*/  String consumeTo(String seq) {
/* 115*/    int offset = nextIndexOf(seq);
/* 116*/    if (offset != -1) {
/* 117*/      String consumed = cacheString(this.pos, offset);
/* 118*/      this.pos += offset;
/* 119*/      return consumed;
/*   0*/    } 
/* 121*/    return consumeToEnd();
/*   0*/  }
/*   0*/  
/*   0*/  String consumeToAny(char... chars) {
/* 126*/    int start = this.pos;
/* 127*/    int remaining = this.length;
/* 128*/    char[] val = this.input;
/* 130*/    label18: while (this.pos < remaining) {
/* 131*/      for (char c : chars) {
/* 132*/        if (val[this.pos] == c) {
/*   0*/            break label18; 
/*   0*/           }
/*   0*/      } 
/* 135*/      this.pos++;
/*   0*/    } 
/* 138*/    return (this.pos > start) ? cacheString(start, this.pos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeToAnySorted(char... chars) {
/* 142*/    int start = this.pos;
/* 143*/    int remaining = this.length;
/* 144*/    char[] val = this.input;
/* 146*/    while (this.pos < remaining && 
/* 147*/      Arrays.binarySearch(chars, val[this.pos]) < 0) {
/* 149*/        this.pos++; 
/*   0*/       }
/* 152*/    return (this.pos > start) ? cacheString(start, this.pos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeData() {
/* 157*/    int start = this.pos;
/* 158*/    int remaining = this.length;
/* 159*/    char[] val = this.input;
/* 161*/    while (this.pos < remaining) {
/* 162*/      char c = val[this.pos];
/* 163*/      if (c == '&' || c == '<' || c == '\000') {
/*   0*/          break; 
/*   0*/         }
/* 165*/      this.pos++;
/*   0*/    } 
/* 168*/    return (this.pos > start) ? cacheString(start, this.pos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeTagName() {
/* 173*/    int start = this.pos;
/* 174*/    int remaining = this.length;
/* 175*/    char[] val = this.input;
/* 177*/    while (this.pos < remaining) {
/* 178*/      char c = val[this.pos];
/* 179*/      if (c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == ' ' || c == '/' || c == '>' || c == '\000') {
/*   0*/          break; 
/*   0*/         }
/* 181*/      this.pos++;
/*   0*/    } 
/* 184*/    return (this.pos > start) ? cacheString(start, this.pos - start) : "";
/*   0*/  }
/*   0*/  
/*   0*/  String consumeToEnd() {
/* 188*/    String data = cacheString(this.pos, this.length - this.pos);
/* 189*/    this.pos = this.length;
/* 190*/    return data;
/*   0*/  }
/*   0*/  
/*   0*/  String consumeLetterSequence() {
/* 194*/    int start = this.pos;
/* 195*/    while (this.pos < this.length) {
/* 196*/      char c = this.input[this.pos];
/* 197*/      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
/* 198*/          this.pos++; 
/*   0*/         }
/*   0*/    } 
/* 203*/    return cacheString(start, this.pos - start);
/*   0*/  }
/*   0*/  
/*   0*/  String consumeLetterThenDigitSequence() {
/* 207*/    int start = this.pos;
/* 208*/    while (this.pos < this.length) {
/* 209*/      char c = this.input[this.pos];
/* 210*/      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
/* 211*/          this.pos++; 
/*   0*/         }
/*   0*/    } 
/* 215*/    while (!isEmpty()) {
/* 216*/      char c = this.input[this.pos];
/* 217*/      if (c >= '0' && c <= '9') {
/* 218*/          this.pos++; 
/*   0*/         }
/*   0*/    } 
/* 223*/    return cacheString(start, this.pos - start);
/*   0*/  }
/*   0*/  
/*   0*/  String consumeHexSequence() {
/* 227*/    int start = this.pos;
/* 228*/    while (this.pos < this.length) {
/* 229*/      char c = this.input[this.pos];
/* 230*/      if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
/* 231*/          this.pos++; 
/*   0*/         }
/*   0*/    } 
/* 235*/    return cacheString(start, this.pos - start);
/*   0*/  }
/*   0*/  
/*   0*/  String consumeDigitSequence() {
/* 239*/    int start = this.pos;
/* 240*/    while (this.pos < this.length) {
/* 241*/      char c = this.input[this.pos];
/* 242*/      if (c >= '0' && c <= '9') {
/* 243*/          this.pos++; 
/*   0*/         }
/*   0*/    } 
/* 247*/    return cacheString(start, this.pos - start);
/*   0*/  }
/*   0*/  
/*   0*/  boolean matches(char c) {
/* 251*/    return (!isEmpty() && this.input[this.pos] == c);
/*   0*/  }
/*   0*/  
/*   0*/  boolean matches(String seq) {
/* 256*/    int scanLength = seq.length();
/* 257*/    if (scanLength > this.length - this.pos) {
/* 258*/        return false; 
/*   0*/       }
/* 260*/    for (int offset = 0; offset < scanLength; offset++) {
/* 261*/      if (seq.charAt(offset) != this.input[this.pos + offset]) {
/* 262*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 263*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesIgnoreCase(String seq) {
/* 267*/    int scanLength = seq.length();
/* 268*/    if (scanLength > this.length - this.pos) {
/* 269*/        return false; 
/*   0*/       }
/* 271*/    for (int offset = 0; offset < scanLength; offset++) {
/* 272*/      char upScan = Character.toUpperCase(seq.charAt(offset));
/* 273*/      char upTarget = Character.toUpperCase(this.input[this.pos + offset]);
/* 274*/      if (upScan != upTarget) {
/* 275*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 277*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesAny(char... seq) {
/* 281*/    if (isEmpty()) {
/* 282*/        return false; 
/*   0*/       }
/* 284*/    char c = this.input[this.pos];
/* 285*/    for (char seek : seq) {
/* 286*/      if (seek == c) {
/* 287*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 289*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesAnySorted(char[] seq) {
/* 293*/    return (!isEmpty() && Arrays.binarySearch(seq, this.input[this.pos]) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesLetter() {
/* 297*/    if (isEmpty()) {
/* 298*/        return false; 
/*   0*/       }
/* 299*/    char c = this.input[this.pos];
/* 300*/    return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchesDigit() {
/* 304*/    if (isEmpty()) {
/* 305*/        return false; 
/*   0*/       }
/* 306*/    char c = this.input[this.pos];
/* 307*/    return (c >= '0' && c <= '9');
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchConsume(String seq) {
/* 311*/    if (matches(seq)) {
/* 312*/      this.pos += seq.length();
/* 313*/      return true;
/*   0*/    } 
/* 315*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean matchConsumeIgnoreCase(String seq) {
/* 320*/    if (matchesIgnoreCase(seq)) {
/* 321*/      this.pos += seq.length();
/* 322*/      return true;
/*   0*/    } 
/* 324*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean containsIgnoreCase(String seq) {
/* 330*/    String loScan = seq.toLowerCase(Locale.ENGLISH);
/* 331*/    String hiScan = seq.toUpperCase(Locale.ENGLISH);
/* 332*/    return (nextIndexOf(loScan) > -1 || nextIndexOf(hiScan) > -1);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 337*/    return new String(this.input, this.pos, this.length - this.pos);
/*   0*/  }
/*   0*/  
/*   0*/  private String cacheString(int start, int count) {
/* 348*/    char[] val = this.input;
/* 349*/    String[] cache = this.stringCache;
/* 352*/    if (count > 12) {
/* 353*/        return new String(val, start, count); 
/*   0*/       }
/* 356*/    int hash = 0;
/* 357*/    int offset = start;
/* 358*/    for (int i = 0; i < count; i++) {
/* 359*/        hash = 31 * hash + val[offset++]; 
/*   0*/       }
/* 363*/    int index = hash & cache.length - 1;
/* 364*/    String cached = cache[index];
/* 366*/    if (cached == null) {
/* 367*/      cached = new String(val, start, count);
/* 368*/      cache[index] = cached;
/*   0*/    } else {
/* 370*/      if (rangeEquals(start, count, cached)) {
/* 371*/          return cached; 
/*   0*/         }
/* 373*/      cached = new String(val, start, count);
/* 374*/      cache[index] = cached;
/*   0*/    } 
/* 377*/    return cached;
/*   0*/  }
/*   0*/  
/*   0*/  boolean rangeEquals(int start, int count, String cached) {
/* 384*/    if (count == cached.length()) {
/* 385*/      char[] one = this.input;
/* 386*/      int i = start;
/* 387*/      int j = 0;
/* 388*/      while (count-- != 0) {
/* 389*/        if (one[i++] != cached.charAt(j++)) {
/* 390*/            return false; 
/*   0*/           }
/*   0*/      } 
/* 392*/      return true;
/*   0*/    } 
/* 394*/    return false;
/*   0*/  }
/*   0*/}
