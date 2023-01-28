/*   0*/package com.fasterxml.jackson.core.util;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.io.NumberInput;
/*   0*/import java.math.BigDecimal;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/
/*   0*/public final class TextBuffer {
/*  29*/  static final char[] NO_CHARS = new char[0];
/*   0*/  
/*   0*/  static final int MIN_SEGMENT_LEN = 1000;
/*   0*/  
/*   0*/  static final int MAX_SEGMENT_LEN = 262144;
/*   0*/  
/*   0*/  private final BufferRecycler _allocator;
/*   0*/  
/*   0*/  private char[] _inputBuffer;
/*   0*/  
/*   0*/  private int _inputStart;
/*   0*/  
/*   0*/  private int _inputLen;
/*   0*/  
/*   0*/  private ArrayList<char[]> _segments;
/*   0*/  
/*   0*/  private boolean _hasSegments;
/*   0*/  
/*   0*/  private int _segmentSize;
/*   0*/  
/*   0*/  private char[] _currentSegment;
/*   0*/  
/*   0*/  private int _currentSize;
/*   0*/  
/*   0*/  private String _resultString;
/*   0*/  
/*   0*/  private char[] _resultArray;
/*   0*/  
/*   0*/  public TextBuffer(BufferRecycler allocator) {
/* 122*/    this._allocator = allocator;
/*   0*/  }
/*   0*/  
/*   0*/  public void releaseBuffers() {
/* 136*/    if (this._allocator == null) {
/* 137*/      resetWithEmpty();
/* 139*/    } else if (this._currentSegment != null) {
/* 141*/      resetWithEmpty();
/* 143*/      char[] buf = this._currentSegment;
/* 144*/      this._currentSegment = null;
/* 145*/      this._allocator.releaseCharBuffer(2, buf);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void resetWithEmpty() {
/* 156*/    this._inputStart = -1;
/* 157*/    this._currentSize = 0;
/* 158*/    this._inputLen = 0;
/* 160*/    this._inputBuffer = null;
/* 161*/    this._resultString = null;
/* 162*/    this._resultArray = null;
/* 165*/    if (this._hasSegments) {
/* 166*/        clearSegments(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void resetWithShared(char[] buf, int start, int len) {
/* 179*/    this._resultString = null;
/* 180*/    this._resultArray = null;
/* 183*/    this._inputBuffer = buf;
/* 184*/    this._inputStart = start;
/* 185*/    this._inputLen = len;
/* 188*/    if (this._hasSegments) {
/* 189*/        clearSegments(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void resetWithCopy(char[] buf, int start, int len) {
/* 195*/    this._inputBuffer = null;
/* 196*/    this._inputStart = -1;
/* 197*/    this._inputLen = 0;
/* 199*/    this._resultString = null;
/* 200*/    this._resultArray = null;
/* 203*/    if (this._hasSegments) {
/* 204*/      clearSegments();
/* 205*/    } else if (this._currentSegment == null) {
/* 206*/      this._currentSegment = buf(len);
/*   0*/    } 
/* 208*/    this._currentSize = this._segmentSize = 0;
/* 209*/    append(buf, start, len);
/*   0*/  }
/*   0*/  
/*   0*/  public void resetWithString(String value) {
/* 214*/    this._inputBuffer = null;
/* 215*/    this._inputStart = -1;
/* 216*/    this._inputLen = 0;
/* 218*/    this._resultString = value;
/* 219*/    this._resultArray = null;
/* 221*/    if (this._hasSegments) {
/* 222*/        clearSegments(); 
/*   0*/       }
/* 224*/    this._currentSize = 0;
/*   0*/  }
/*   0*/  
/*   0*/  private char[] buf(int needed) {
/* 237*/    return new char[Math.max(needed, 1000)];
/*   0*/  }
/*   0*/  
/*   0*/  private void clearSegments() {
/* 242*/    this._hasSegments = false;
/* 250*/    this._segments.clear();
/* 251*/    this._currentSize = this._segmentSize = 0;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 264*/    if (this._inputStart >= 0) {
/* 265*/        return this._inputLen; 
/*   0*/       }
/* 267*/    if (this._resultArray != null) {
/* 268*/        return this._resultArray.length; 
/*   0*/       }
/* 270*/    if (this._resultString != null) {
/* 271*/        return this._resultString.length(); 
/*   0*/       }
/* 274*/    return this._segmentSize + this._currentSize;
/*   0*/  }
/*   0*/  
/*   0*/  public int getTextOffset() {
/* 282*/    return (this._inputStart >= 0) ? this._inputStart : 0;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasTextAsCharacters() {
/* 292*/    if (this._inputStart >= 0 || this._resultArray != null) {
/* 292*/        return true; 
/*   0*/       }
/* 294*/    if (this._resultString != null) {
/* 294*/        return false; 
/*   0*/       }
/* 295*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] getTextBuffer() {
/* 306*/    if (this._inputStart >= 0) {
/* 306*/        return this._inputBuffer; 
/*   0*/       }
/* 307*/    if (this._resultArray != null) {
/* 307*/        return this._resultArray; 
/*   0*/       }
/* 308*/    if (this._resultString != null) {
/* 309*/        return this._resultArray = this._resultString.toCharArray(); 
/*   0*/       }
/* 312*/    if (!this._hasSegments) {
/* 313*/        return (this._currentSegment == null) ? NO_CHARS : this._currentSegment; 
/*   0*/       }
/* 316*/    return contentsAsArray();
/*   0*/  }
/*   0*/  
/*   0*/  public String contentsAsString() {
/* 327*/    if (this._resultString == null) {
/* 329*/        if (this._resultArray != null) {
/* 330*/          this._resultString = new String(this._resultArray);
/* 333*/        } else if (this._inputStart >= 0) {
/* 334*/          if (this._inputLen < 1) {
/* 335*/              return this._resultString = ""; 
/*   0*/             }
/* 337*/          this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
/*   0*/        } else {
/* 340*/          int segLen = this._segmentSize;
/* 341*/          int currLen = this._currentSize;
/* 343*/          if (segLen == 0) {
/* 344*/            this._resultString = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/*   0*/          } else {
/* 346*/            StringBuilder sb = new StringBuilder(segLen + currLen);
/* 348*/            if (this._segments != null) {
/* 349*/                for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 350*/                  char[] curr = this._segments.get(i);
/* 351*/                  sb.append(curr, 0, curr.length);
/*   0*/                }  
/*   0*/               }
/* 355*/            sb.append(this._currentSegment, 0, this._currentSize);
/* 356*/            this._resultString = sb.toString();
/*   0*/          } 
/*   0*/        }  
/*   0*/       }
/* 361*/    return this._resultString;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] contentsAsArray() {
/* 365*/    char[] result = this._resultArray;
/* 366*/    if (result == null) {
/* 367*/        this._resultArray = result = resultArray(); 
/*   0*/       }
/* 369*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public BigDecimal contentsAsDecimal() throws NumberFormatException {
/* 379*/    if (this._resultArray != null) {
/* 380*/        return NumberInput.parseBigDecimal(this._resultArray); 
/*   0*/       }
/* 383*/    if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 384*/        return NumberInput.parseBigDecimal(this._inputBuffer, this._inputStart, this._inputLen); 
/*   0*/       }
/* 387*/    if (this._segmentSize == 0 && this._currentSegment != null) {
/* 388*/        return NumberInput.parseBigDecimal(this._currentSegment, 0, this._currentSize); 
/*   0*/       }
/* 391*/    return NumberInput.parseBigDecimal(contentsAsArray());
/*   0*/  }
/*   0*/  
/*   0*/  public double contentsAsDouble() throws NumberFormatException {
/* 399*/    return NumberInput.parseDouble(contentsAsString());
/*   0*/  }
/*   0*/  
/*   0*/  public void ensureNotShared() {
/* 413*/    if (this._inputStart >= 0) {
/* 414*/        unshare(16); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void append(char c) {
/* 420*/    if (this._inputStart >= 0) {
/* 421*/        unshare(16); 
/*   0*/       }
/* 423*/    this._resultString = null;
/* 424*/    this._resultArray = null;
/* 426*/    char[] curr = this._currentSegment;
/* 427*/    if (this._currentSize >= curr.length) {
/* 428*/      expand(1);
/* 429*/      curr = this._currentSegment;
/*   0*/    } 
/* 431*/    curr[this._currentSize++] = c;
/*   0*/  }
/*   0*/  
/*   0*/  public void append(char[] c, int start, int len) {
/* 437*/    if (this._inputStart >= 0) {
/* 438*/        unshare(len); 
/*   0*/       }
/* 440*/    this._resultString = null;
/* 441*/    this._resultArray = null;
/* 444*/    char[] curr = this._currentSegment;
/* 445*/    int max = curr.length - this._currentSize;
/* 447*/    if (max >= len) {
/* 448*/      System.arraycopy(c, start, curr, this._currentSize, len);
/* 449*/      this._currentSize += len;
/*   0*/      return;
/*   0*/    } 
/* 453*/    if (max > 0) {
/* 454*/      System.arraycopy(c, start, curr, this._currentSize, max);
/* 455*/      start += max;
/* 456*/      len -= max;
/*   0*/    } 
/*   0*/    do {
/* 463*/      expand(len);
/* 464*/      int amount = Math.min(this._currentSegment.length, len);
/* 465*/      System.arraycopy(c, start, this._currentSegment, 0, amount);
/* 466*/      this._currentSize += amount;
/* 467*/      start += amount;
/* 468*/      len -= amount;
/* 469*/    } while (len > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void append(String str, int offset, int len) {
/* 475*/    if (this._inputStart >= 0) {
/* 476*/        unshare(len); 
/*   0*/       }
/* 478*/    this._resultString = null;
/* 479*/    this._resultArray = null;
/* 482*/    char[] curr = this._currentSegment;
/* 483*/    int max = curr.length - this._currentSize;
/* 484*/    if (max >= len) {
/* 485*/      str.getChars(offset, offset + len, curr, this._currentSize);
/* 486*/      this._currentSize += len;
/*   0*/      return;
/*   0*/    } 
/* 490*/    if (max > 0) {
/* 491*/      str.getChars(offset, offset + max, curr, this._currentSize);
/* 492*/      len -= max;
/* 493*/      offset += max;
/*   0*/    } 
/*   0*/    do {
/* 500*/      expand(len);
/* 501*/      int amount = Math.min(this._currentSegment.length, len);
/* 502*/      str.getChars(offset, offset + amount, this._currentSegment, 0);
/* 503*/      this._currentSize += amount;
/* 504*/      offset += amount;
/* 505*/      len -= amount;
/* 506*/    } while (len > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public char[] getCurrentSegment() {
/* 521*/    if (this._inputStart >= 0) {
/* 522*/      unshare(1);
/*   0*/    } else {
/* 524*/      char[] curr = this._currentSegment;
/* 525*/      if (curr == null) {
/* 526*/        this._currentSegment = buf(0);
/* 527*/      } else if (this._currentSize >= curr.length) {
/* 529*/        expand(1);
/*   0*/      } 
/*   0*/    } 
/* 532*/    return this._currentSegment;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] emptyAndGetCurrentSegment() {
/* 538*/    this._inputStart = -1;
/* 539*/    this._currentSize = 0;
/* 540*/    this._inputLen = 0;
/* 542*/    this._inputBuffer = null;
/* 543*/    this._resultString = null;
/* 544*/    this._resultArray = null;
/* 547*/    if (this._hasSegments) {
/* 548*/        clearSegments(); 
/*   0*/       }
/* 550*/    char[] curr = this._currentSegment;
/* 551*/    if (curr == null) {
/* 552*/        this._currentSegment = curr = buf(0); 
/*   0*/       }
/* 554*/    return curr;
/*   0*/  }
/*   0*/  
/*   0*/  public int getCurrentSegmentSize() {
/* 557*/    return this._currentSize;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCurrentLength(int len) {
/* 558*/    this._currentSize = len;
/*   0*/  }
/*   0*/  
/*   0*/  public String setCurrentAndReturn(int len) {
/* 564*/    this._currentSize = len;
/* 566*/    if (this._segmentSize > 0) {
/* 567*/        return contentsAsString(); 
/*   0*/       }
/* 570*/    int currLen = this._currentSize;
/* 571*/    String str = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/* 572*/    this._resultString = str;
/* 573*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] finishCurrentSegment() {
/* 577*/    if (this._segments == null) {
/* 578*/        this._segments = (ArrayList)new ArrayList<char>(); 
/*   0*/       }
/* 580*/    this._hasSegments = true;
/* 581*/    this._segments.add(this._currentSegment);
/* 582*/    int oldLen = this._currentSegment.length;
/* 583*/    this._segmentSize += oldLen;
/* 584*/    this._currentSize = 0;
/* 587*/    int newLen = oldLen + (oldLen >> 1);
/* 588*/    if (newLen < 1000) {
/* 589*/      newLen = 1000;
/* 590*/    } else if (newLen > 262144) {
/* 591*/      newLen = 262144;
/*   0*/    } 
/* 593*/    char[] curr = carr(newLen);
/* 594*/    this._currentSegment = curr;
/* 595*/    return curr;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] expandCurrentSegment() {
/* 605*/    char[] curr = this._currentSegment;
/* 607*/    int len = curr.length;
/* 608*/    int newLen = len + (len >> 1);
/* 610*/    if (newLen > 262144) {
/* 611*/        newLen = len + (len >> 2); 
/*   0*/       }
/* 613*/    return this._currentSegment = Arrays.copyOf(curr, newLen);
/*   0*/  }
/*   0*/  
/*   0*/  public char[] expandCurrentSegment(int minSize) {
/* 626*/    char[] curr = this._currentSegment;
/* 627*/    if (curr.length >= minSize) {
/* 627*/        return curr; 
/*   0*/       }
/* 628*/    this._currentSegment = curr = Arrays.copyOf(curr, minSize);
/* 629*/    return curr;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 643*/    return contentsAsString();
/*   0*/  }
/*   0*/  
/*   0*/  private void unshare(int needExtra) {
/* 657*/    int sharedLen = this._inputLen;
/* 658*/    this._inputLen = 0;
/* 659*/    char[] inputBuf = this._inputBuffer;
/* 660*/    this._inputBuffer = null;
/* 661*/    int start = this._inputStart;
/* 662*/    this._inputStart = -1;
/* 665*/    int needed = sharedLen + needExtra;
/* 666*/    if (this._currentSegment == null || needed > this._currentSegment.length) {
/* 667*/        this._currentSegment = buf(needed); 
/*   0*/       }
/* 669*/    if (sharedLen > 0) {
/* 670*/        System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen); 
/*   0*/       }
/* 672*/    this._segmentSize = 0;
/* 673*/    this._currentSize = sharedLen;
/*   0*/  }
/*   0*/  
/*   0*/  private void expand(int minNewSegmentSize) {
/* 683*/    if (this._segments == null) {
/* 684*/        this._segments = (ArrayList)new ArrayList<char>(); 
/*   0*/       }
/* 686*/    char[] curr = this._currentSegment;
/* 687*/    this._hasSegments = true;
/* 688*/    this._segments.add(curr);
/* 689*/    this._segmentSize += curr.length;
/* 690*/    this._currentSize = 0;
/* 691*/    int oldLen = curr.length;
/* 694*/    int newLen = oldLen + (oldLen >> 1);
/* 695*/    if (newLen < 1000) {
/* 696*/      newLen = 1000;
/* 697*/    } else if (newLen > 262144) {
/* 698*/      newLen = 262144;
/*   0*/    } 
/* 700*/    this._currentSegment = carr(newLen);
/*   0*/  }
/*   0*/  
/*   0*/  private char[] resultArray() {
/* 705*/    if (this._resultString != null) {
/* 706*/        return this._resultString.toCharArray(); 
/*   0*/       }
/* 709*/    if (this._inputStart >= 0) {
/* 710*/      int len = this._inputLen;
/* 711*/      if (len < 1) {
/* 712*/          return NO_CHARS; 
/*   0*/         }
/* 714*/      int start = this._inputStart;
/* 715*/      if (start == 0) {
/* 716*/          return Arrays.copyOf(this._inputBuffer, len); 
/*   0*/         }
/* 718*/      return Arrays.copyOfRange(this._inputBuffer, start, start + len);
/*   0*/    } 
/* 721*/    int size = size();
/* 722*/    if (size < 1) {
/* 723*/        return NO_CHARS; 
/*   0*/       }
/* 725*/    int offset = 0;
/* 726*/    char[] result = carr(size);
/* 727*/    if (this._segments != null) {
/* 728*/        for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 729*/          char[] curr = this._segments.get(i);
/* 730*/          int currLen = curr.length;
/* 731*/          System.arraycopy(curr, 0, result, offset, currLen);
/* 732*/          offset += currLen;
/*   0*/        }  
/*   0*/       }
/* 735*/    System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
/* 736*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private char[] carr(int len) {
/* 739*/    return new char[len];
/*   0*/  }
/*   0*/}
