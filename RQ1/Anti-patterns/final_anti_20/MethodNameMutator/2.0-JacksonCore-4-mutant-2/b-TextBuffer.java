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
/*   0*/  private boolean _hasSegments = false;
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
/* 234*/    if (this._allocator != null) {
/* 235*/        return this._allocator.allocCharBuffer(2, needed); 
/*   0*/       }
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
/* 301*/    if (this._inputStart >= 0) {
/* 301*/        return this._inputBuffer; 
/*   0*/       }
/* 302*/    if (this._resultArray != null) {
/* 302*/        return this._resultArray; 
/*   0*/       }
/* 303*/    if (this._resultString != null) {
/* 304*/        return this._resultArray = this._resultString.toCharArray(); 
/*   0*/       }
/* 307*/    if (!this._hasSegments) {
/* 307*/        return this._currentSegment; 
/*   0*/       }
/* 309*/    return contentsAsArray();
/*   0*/  }
/*   0*/  
/*   0*/  public String contentsAsString() {
/* 320*/    if (this._resultString == null) {
/* 322*/        if (this._resultArray != null) {
/* 323*/          this._resultString = new String(this._resultArray);
/* 326*/        } else if (this._inputStart >= 0) {
/* 327*/          if (this._inputLen < 1) {
/* 328*/              return this._resultString = ""; 
/*   0*/             }
/* 330*/          this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
/*   0*/        } else {
/* 333*/          int segLen = this._segmentSize;
/* 334*/          int currLen = this._currentSize;
/* 336*/          if (segLen == 0) {
/* 337*/            this._resultString = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/*   0*/          } else {
/* 339*/            StringBuilder sb = new StringBuilder(segLen + currLen);
/* 341*/            if (this._segments != null) {
/* 342*/                for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 343*/                  char[] curr = this._segments.get(i);
/* 344*/                  sb.append(curr, 0, curr.length);
/*   0*/                }  
/*   0*/               }
/* 348*/            sb.append(this._currentSegment, 0, this._currentSize);
/* 349*/            this._resultString = sb.toString();
/*   0*/          } 
/*   0*/        }  
/*   0*/       }
/* 354*/    return this._resultString;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] contentsAsArray() {
/* 358*/    char[] result = this._resultArray;
/* 359*/    if (result == null) {
/* 360*/        this._resultArray = result = resultArray(); 
/*   0*/       }
/* 362*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public BigDecimal contentsAsDecimal() throws NumberFormatException {
/* 372*/    if (this._resultArray != null) {
/* 373*/        return NumberInput.parseBigDecimal(this._resultArray); 
/*   0*/       }
/* 376*/    if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 377*/        return NumberInput.parseBigDecimal(this._inputBuffer, this._inputStart, this._inputLen); 
/*   0*/       }
/* 380*/    if (this._segmentSize == 0 && this._currentSegment != null) {
/* 381*/        return NumberInput.parseBigDecimal(this._currentSegment, 0, this._currentSize); 
/*   0*/       }
/* 384*/    return NumberInput.parseBigDecimal(contentsAsArray());
/*   0*/  }
/*   0*/  
/*   0*/  public double contentsAsDouble() throws NumberFormatException {
/* 392*/    return NumberInput.parseDouble(contentsAsString());
/*   0*/  }
/*   0*/  
/*   0*/  public void ensureNotShared() {
/* 406*/    if (this._inputStart >= 0) {
/* 407*/        unshare(16); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void append(char c) {
/* 413*/    if (this._inputStart >= 0) {
/* 414*/        unshare(16); 
/*   0*/       }
/* 416*/    this._resultString = null;
/* 417*/    this._resultArray = null;
/* 419*/    char[] curr = this._currentSegment;
/* 420*/    if (this._currentSize >= curr.length) {
/* 421*/      expand(1);
/* 422*/      curr = this._currentSegment;
/*   0*/    } 
/* 424*/    curr[this._currentSize++] = c;
/*   0*/  }
/*   0*/  
/*   0*/  public void append(char[] c, int start, int len) {
/* 430*/    if (this._inputStart >= 0) {
/* 431*/        unshare(len); 
/*   0*/       }
/* 433*/    this._resultString = null;
/* 434*/    this._resultArray = null;
/* 437*/    char[] curr = this._currentSegment;
/* 438*/    int max = curr.length - this._currentSize;
/* 440*/    if (max >= len) {
/* 441*/      System.arraycopy(c, start, curr, this._currentSize, len);
/* 442*/      this._currentSize += len;
/*   0*/      return;
/*   0*/    } 
/* 446*/    if (max > 0) {
/* 447*/      System.arraycopy(c, start, curr, this._currentSize, max);
/* 448*/      start += max;
/* 449*/      len -= max;
/*   0*/    } 
/*   0*/    do {
/* 456*/      expand(len);
/* 457*/      int amount = Math.min(this._currentSegment.length, len);
/* 458*/      System.arraycopy(c, start, this._currentSegment, 0, amount);
/* 459*/      this._currentSize += amount;
/* 460*/      start += amount;
/* 461*/      len -= amount;
/* 462*/    } while (len > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void append(String str, int offset, int len) {
/* 468*/    if (this._inputStart >= 0) {
/* 469*/        unshare(len); 
/*   0*/       }
/* 471*/    this._resultString = null;
/* 472*/    this._resultArray = null;
/* 475*/    char[] curr = this._currentSegment;
/* 476*/    int max = curr.length - this._currentSize;
/* 477*/    if (max >= len) {
/* 478*/      str.getChars(offset, offset + len, curr, this._currentSize);
/* 479*/      this._currentSize += len;
/*   0*/      return;
/*   0*/    } 
/* 483*/    if (max > 0) {
/* 484*/      str.getChars(offset, offset + max, curr, this._currentSize);
/* 485*/      len -= max;
/* 486*/      offset += max;
/*   0*/    } 
/*   0*/    do {
/* 493*/      expand(len);
/* 494*/      int amount = Math.min(this._currentSegment.length, len);
/* 495*/      str.getChars(offset, offset + amount, this._currentSegment, 0);
/* 496*/      this._currentSize += amount;
/* 497*/      offset += amount;
/* 498*/      len -= amount;
/* 499*/    } while (len > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public char[] getCurrentSegment() {
/* 514*/    if (this._inputStart >= 0) {
/* 515*/      unshare(1);
/*   0*/    } else {
/* 517*/      char[] curr = this._currentSegment;
/* 518*/      if (curr == null) {
/* 519*/        this._currentSegment = buf(0);
/* 520*/      } else if (this._currentSize >= curr.length) {
/* 522*/        expand(1);
/*   0*/      } 
/*   0*/    } 
/* 525*/    return this._currentSegment;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] emptyAndGetCurrentSegment() {
/* 531*/    this._inputStart = -1;
/* 532*/    this._currentSize = 0;
/* 533*/    this._inputLen = 0;
/* 535*/    this._inputBuffer = null;
/* 536*/    this._resultString = null;
/* 537*/    this._resultArray = null;
/* 540*/    if (this._hasSegments) {
/* 541*/        clearSegments(); 
/*   0*/       }
/* 543*/    char[] curr = this._currentSegment;
/* 544*/    if (curr == null) {
/* 545*/        this._currentSegment = curr = buf(0); 
/*   0*/       }
/* 547*/    return curr;
/*   0*/  }
/*   0*/  
/*   0*/  public int getCurrentSegmentSize() {
/* 550*/    return this._currentSize;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCurrentLength(int len) {
/* 551*/    this._currentSize = len;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] finishCurrentSegment() {
/* 554*/    if (this._segments == null) {
/* 555*/        this._segments = (ArrayList)new ArrayList<char>(); 
/*   0*/       }
/* 557*/    this._hasSegments = true;
/* 558*/    this._segments.add(this._currentSegment);
/* 559*/    int oldLen = this._currentSegment.length;
/* 560*/    this._segmentSize += oldLen;
/* 561*/    this._currentSize = 0;
/* 564*/    int newLen = oldLen + (oldLen >> 1);
/* 565*/    if (newLen < 1000) {
/* 566*/      newLen = 1000;
/* 567*/    } else if (newLen > 262144) {
/* 568*/      newLen = 262144;
/*   0*/    } 
/* 570*/    char[] curr = carr(newLen);
/* 571*/    this._currentSegment = curr;
/* 572*/    return curr;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] expandCurrentSegment() {
/* 582*/    char[] curr = this._currentSegment;
/* 584*/    int len = curr.length;
/* 586*/    int newLen = (len == 262144) ? 262145 : Math.max(262144, len + (len >> 1));
/* 587*/    return this._currentSegment = Arrays.copyOf(curr, newLen);
/*   0*/  }
/*   0*/  
/*   0*/  public char[] expandCurrentSegment(int minSize) {
/* 600*/    char[] curr = this._currentSegment;
/* 601*/    if (curr.length >= minSize) {
/* 601*/        return curr; 
/*   0*/       }
/* 602*/    this._currentSegment = curr = Arrays.copyOf(curr, minSize);
/* 603*/    return curr;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 617*/    return contentsAsString();
/*   0*/  }
/*   0*/  
/*   0*/  private void unshare(int needExtra) {
/* 631*/    int sharedLen = this._inputLen;
/* 632*/    this._inputLen = 0;
/* 633*/    char[] inputBuf = this._inputBuffer;
/* 634*/    this._inputBuffer = null;
/* 635*/    int start = this._inputStart;
/* 636*/    this._inputStart = -1;
/* 639*/    int needed = sharedLen + needExtra;
/* 640*/    if (this._currentSegment == null || needed > this._currentSegment.length) {
/* 641*/        this._currentSegment = buf(needed); 
/*   0*/       }
/* 643*/    if (sharedLen > 0) {
/* 644*/        System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen); 
/*   0*/       }
/* 646*/    this._segmentSize = 0;
/* 647*/    this._currentSize = sharedLen;
/*   0*/  }
/*   0*/  
/*   0*/  private void expand(int minNewSegmentSize) {
/* 657*/    if (this._segments == null) {
/* 658*/        this._segments = (ArrayList)new ArrayList<char>(); 
/*   0*/       }
/* 660*/    char[] curr = this._currentSegment;
/* 661*/    this._hasSegments = true;
/* 662*/    this._segments.add(curr);
/* 663*/    this._segmentSize += curr.length;
/* 664*/    this._currentSize = 0;
/* 665*/    int oldLen = curr.length;
/* 668*/    int newLen = oldLen + (oldLen >> 1);
/* 669*/    if (newLen < 1000) {
/* 670*/      newLen = 1000;
/* 671*/    } else if (newLen > 262144) {
/* 672*/      newLen = 262144;
/*   0*/    } 
/* 674*/    this._currentSegment = carr(newLen);
/*   0*/  }
/*   0*/  
/*   0*/  private char[] resultArray() {
/* 679*/    if (this._resultString != null) {
/* 680*/        return this._resultString.toCharArray(); 
/*   0*/       }
/* 683*/    if (this._inputStart >= 0) {
/* 684*/      int len = this._inputLen;
/* 685*/      if (len < 1) {
/* 686*/          return NO_CHARS; 
/*   0*/         }
/* 688*/      int start = this._inputStart;
/* 689*/      if (start == 0) {
/* 690*/          return Arrays.copyOf(this._inputBuffer, len); 
/*   0*/         }
/* 692*/      return Arrays.copyOfRange(this._inputBuffer, start, start + len);
/*   0*/    } 
/* 695*/    int size = size();
/* 696*/    if (size < 1) {
/* 697*/        return NO_CHARS; 
/*   0*/       }
/* 699*/    int offset = 0;
/* 700*/    char[] result = carr(size);
/* 701*/    if (this._segments != null) {
/* 702*/        for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 703*/          char[] curr = this._segments.get(i);
/* 704*/          int currLen = curr.length;
/* 705*/          System.arraycopy(curr, 0, result, offset, currLen);
/* 706*/          offset += currLen;
/*   0*/        }  
/*   0*/       }
/* 709*/    System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
/* 710*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private char[] carr(int len) {
/* 713*/    return new char[len];
/*   0*/  }
/*   0*/}
