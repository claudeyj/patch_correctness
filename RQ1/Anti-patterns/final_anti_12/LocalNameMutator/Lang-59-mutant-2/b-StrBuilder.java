/*   0*/package org.apache.commons.lang.text;
/*   0*/
/*   0*/import java.io.Reader;
/*   0*/import java.io.Writer;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import org.apache.commons.lang.ArrayUtils;
/*   0*/import org.apache.commons.lang.SystemUtils;
/*   0*/
/*   0*/public class StrBuilder implements Cloneable {
/*   0*/  static final int CAPACITY = 32;
/*   0*/  
/*   0*/  private static final long serialVersionUID = 7628716375283629643L;
/*   0*/  
/*   0*/  protected char[] buffer;
/*   0*/  
/*   0*/  protected int size;
/*   0*/  
/*   0*/  private String newLine;
/*   0*/  
/*   0*/  private String nullText;
/*   0*/  
/*   0*/  public StrBuilder() {
/*  98*/    this(32);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder(int initialCapacity) {
/* 108*/    if (initialCapacity <= 0)
/* 109*/      initialCapacity = 32; 
/* 111*/    this.buffer = new char[initialCapacity];
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder(String str) {
/* 122*/    if (str == null) {
/* 123*/      this.buffer = new char[32];
/*   0*/    } else {
/* 125*/      this.buffer = new char[str.length() + 32];
/* 126*/      append(str);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String getNewLineText() {
/* 137*/    return this.newLine;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder setNewLineText(String newLine) {
/* 147*/    this.newLine = newLine;
/* 148*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNullText() {
/* 158*/    return this.nullText;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder setNullText(String nullText) {
/* 168*/    if (nullText != null && nullText.length() == 0)
/* 169*/      nullText = null; 
/* 171*/    this.nullText = nullText;
/* 172*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public int length() {
/* 182*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder setLength(int length) {
/* 194*/    if (length < 0)
/* 195*/      throw new StringIndexOutOfBoundsException(length); 
/* 197*/    if (length < this.size) {
/* 198*/      this.size = length;
/* 199*/    } else if (length > this.size) {
/* 200*/      ensureCapacity(length);
/* 201*/      int oldEnd = this.size;
/* 202*/      int newEnd = length;
/* 203*/      this.size = length;
/* 204*/      for (int i = oldEnd; i < newEnd; i++)
/* 205*/        this.buffer[i] = Character.MIN_VALUE; 
/*   0*/    } 
/* 208*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public int capacity() {
/* 218*/    return this.buffer.length;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder ensureCapacity(int capacity) {
/* 228*/    if (capacity > this.buffer.length) {
/* 229*/      char[] old = this.buffer;
/* 230*/      this.buffer = new char[capacity];
/* 231*/      System.arraycopy(old, 0, this.buffer, 0, this.size);
/*   0*/    } 
/* 233*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder minimizeCapacity() {
/* 242*/    if (this.buffer.length > length()) {
/* 243*/      char[] old = this.buffer;
/* 244*/      this.buffer = new char[length()];
/* 245*/      System.arraycopy(old, 0, this.buffer, 0, this.size);
/*   0*/    } 
/* 247*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 260*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/* 272*/    return (this.size == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder clear() {
/* 287*/    this.size = 0;
/* 288*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public char charAt(int index) {
/* 302*/    if (index < 0 || index >= length())
/* 303*/      throw new StringIndexOutOfBoundsException(index); 
/* 305*/    return this.buffer[index];
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder setCharAt(int index, char ch) {
/* 319*/    if (index < 0 || index >= length())
/* 320*/      throw new StringIndexOutOfBoundsException(index); 
/* 322*/    this.buffer[index] = ch;
/* 323*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteCharAt(int index) {
/* 336*/    if (index < 0 || index >= this.size)
/* 337*/      throw new StringIndexOutOfBoundsException(index); 
/* 339*/    deleteImpl(index, index + 1, 1);
/* 340*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] toCharArray() {
/* 350*/    if (this.size == 0)
/* 351*/      return ArrayUtils.EMPTY_CHAR_ARRAY; 
/* 353*/    char[] chars = new char[this.size];
/* 354*/    System.arraycopy(this.buffer, 0, chars, 0, this.size);
/* 355*/    return chars;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] toCharArray(int startIndex, int endIndex) {
/* 369*/    endIndex = validateRange(startIndex, endIndex);
/* 370*/    int len = endIndex - startIndex;
/* 371*/    if (len == 0)
/* 372*/      return ArrayUtils.EMPTY_CHAR_ARRAY; 
/* 374*/    char[] chars = new char[len];
/* 375*/    System.arraycopy(this.buffer, startIndex, chars, 0, len);
/* 376*/    return chars;
/*   0*/  }
/*   0*/  
/*   0*/  public char[] getChars(char[] destination) {
/* 386*/    int len = length();
/* 387*/    if (destination == null || destination.length < len)
/* 388*/      destination = new char[len]; 
/* 390*/    System.arraycopy(this.buffer, 0, destination, 0, len);
/* 391*/    return destination;
/*   0*/  }
/*   0*/  
/*   0*/  public void getChars(int startIndex, int endIndex, char[] destination, int destinationIndex) {
/* 405*/    if (startIndex < 0)
/* 406*/      throw new StringIndexOutOfBoundsException(startIndex); 
/* 408*/    if (endIndex < 0 || endIndex > length())
/* 409*/      throw new StringIndexOutOfBoundsException(endIndex); 
/* 411*/    if (startIndex > endIndex)
/* 412*/      throw new StringIndexOutOfBoundsException("end < start"); 
/* 414*/    System.arraycopy(this.buffer, startIndex, destination, destinationIndex, endIndex - startIndex);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendNewLine() {
/* 428*/    if (this.newLine == null) {
/* 429*/      append(SystemUtils.LINE_SEPARATOR);
/* 430*/      return this;
/*   0*/    } 
/* 432*/    return append(this.newLine);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendNull() {
/* 441*/    if (this.nullText == null)
/* 442*/      return this; 
/* 444*/    return append(this.nullText);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(Object obj) {
/* 455*/    if (obj == null)
/* 456*/      return appendNull(); 
/* 458*/    return append(obj.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(String str) {
/* 469*/    if (str == null)
/* 470*/      return appendNull(); 
/* 472*/    int strLen = str.length();
/* 473*/    if (strLen > 0) {
/* 474*/      int len = length();
/* 475*/      ensureCapacity(len + strLen);
/* 476*/      str.getChars(0, strLen, this.buffer, len);
/* 477*/      this.size += strLen;
/*   0*/    } 
/* 479*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(String str, int startIndex, int length) {
/* 492*/    if (str == null)
/* 493*/      return appendNull(); 
/* 495*/    if (startIndex < 0 || startIndex > str.length())
/* 496*/      throw new StringIndexOutOfBoundsException("startIndex must be valid"); 
/* 498*/    if (length < 0 || startIndex + length > str.length())
/* 499*/      throw new StringIndexOutOfBoundsException("length must be valid"); 
/* 501*/    if (length > 0) {
/* 502*/      int len = length();
/* 503*/      ensureCapacity(len + length);
/* 504*/      str.getChars(startIndex, startIndex + length, this.buffer, len);
/* 505*/      this.size += length;
/*   0*/    } 
/* 507*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(StringBuffer str) {
/* 518*/    if (str == null)
/* 519*/      return appendNull(); 
/* 521*/    int strLen = str.length();
/* 522*/    if (strLen > 0) {
/* 523*/      int len = length();
/* 524*/      ensureCapacity(len + strLen);
/* 525*/      str.getChars(0, strLen, this.buffer, len);
/* 526*/      this.size += strLen;
/*   0*/    } 
/* 528*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(StringBuffer str, int startIndex, int length) {
/* 541*/    if (str == null)
/* 542*/      return appendNull(); 
/* 544*/    if (startIndex < 0 || startIndex > str.length())
/* 545*/      throw new StringIndexOutOfBoundsException("startIndex must be valid"); 
/* 547*/    if (length < 0 || startIndex + length > str.length())
/* 548*/      throw new StringIndexOutOfBoundsException("length must be valid"); 
/* 550*/    if (length > 0) {
/* 551*/      int len = length();
/* 552*/      ensureCapacity(len + length);
/* 553*/      str.getChars(startIndex, startIndex + length, this.buffer, len);
/* 554*/      this.size += length;
/*   0*/    } 
/* 556*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(StrBuilder str) {
/* 567*/    if (str == null)
/* 568*/      return appendNull(); 
/* 570*/    int strLen = str.length();
/* 571*/    if (strLen > 0) {
/* 572*/      int len = length();
/* 573*/      ensureCapacity(len + strLen);
/* 574*/      System.arraycopy(str.buffer, 0, this.buffer, len, strLen);
/* 575*/      this.size += strLen;
/*   0*/    } 
/* 577*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(StrBuilder str, int startIndex, int length) {
/* 590*/    if (str == null)
/* 591*/      return appendNull(); 
/* 593*/    if (startIndex < 0 || startIndex > str.length())
/* 594*/      throw new StringIndexOutOfBoundsException("startIndex must be valid"); 
/* 596*/    if (length < 0 || startIndex + length > str.length())
/* 597*/      throw new StringIndexOutOfBoundsException("length must be valid"); 
/* 599*/    if (length > 0) {
/* 600*/      int len = length();
/* 601*/      ensureCapacity(len + length);
/* 602*/      str.getChars(startIndex, startIndex + length, this.buffer, len);
/* 603*/      this.size += length;
/*   0*/    } 
/* 605*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(char[] chars) {
/* 616*/    if (chars == null)
/* 617*/      return appendNull(); 
/* 619*/    int strLen = chars.length;
/* 620*/    if (strLen > 0) {
/* 621*/      int len = length();
/* 622*/      ensureCapacity(len + strLen);
/* 623*/      System.arraycopy(chars, 0, this.buffer, len, strLen);
/* 624*/      this.size += strLen;
/*   0*/    } 
/* 626*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(char[] chars, int startIndex, int length) {
/* 639*/    if (chars == null)
/* 640*/      return appendNull(); 
/* 642*/    if (startIndex < 0 || startIndex > chars.length)
/* 643*/      throw new StringIndexOutOfBoundsException("Invalid startIndex: " + length); 
/* 645*/    if (length < 0 || startIndex + length > chars.length)
/* 646*/      throw new StringIndexOutOfBoundsException("Invalid length: " + length); 
/* 648*/    if (length > 0) {
/* 649*/      int len = length();
/* 650*/      ensureCapacity(len + length);
/* 651*/      System.arraycopy(chars, startIndex, this.buffer, len, length);
/* 652*/      this.size += length;
/*   0*/    } 
/* 654*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(boolean value) {
/* 664*/    if (value) {
/* 665*/      ensureCapacity(this.size + 4);
/* 666*/      this.buffer[this.size++] = 't';
/* 667*/      this.buffer[this.size++] = 'r';
/* 668*/      this.buffer[this.size++] = 'u';
/* 669*/      this.buffer[this.size++] = 'e';
/*   0*/    } else {
/* 671*/      ensureCapacity(this.size + 5);
/* 672*/      this.buffer[this.size++] = 'f';
/* 673*/      this.buffer[this.size++] = 'a';
/* 674*/      this.buffer[this.size++] = 'l';
/* 675*/      this.buffer[this.size++] = 's';
/* 676*/      this.buffer[this.size++] = 'e';
/*   0*/    } 
/* 678*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(char ch) {
/* 688*/    int len = length();
/* 689*/    ensureCapacity(len + 1);
/* 690*/    this.buffer[this.size++] = ch;
/* 691*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(int value) {
/* 701*/    return append(String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(long value) {
/* 711*/    return append(String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(float value) {
/* 721*/    return append(String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder append(double value) {
/* 731*/    return append(String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendWithSeparators(Object[] array, String separator) {
/* 746*/    if (array != null && array.length > 0) {
/* 747*/      separator = (separator == null) ? "" : separator;
/* 748*/      append(array[0]);
/* 749*/      for (int i = 1; i < array.length; i++) {
/* 750*/        append(separator);
/* 751*/        append(array[i]);
/*   0*/      } 
/*   0*/    } 
/* 754*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendWithSeparators(Collection coll, String separator) {
/* 768*/    if (coll != null && coll.size() > 0) {
/* 769*/      separator = (separator == null) ? "" : separator;
/* 770*/      Iterator it = coll.iterator();
/* 771*/      while (it.hasNext()) {
/* 772*/        append(it.next());
/* 773*/        if (it.hasNext())
/* 774*/          append(separator); 
/*   0*/      } 
/*   0*/    } 
/* 778*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendWithSeparators(Iterator it, String separator) {
/* 792*/    if (it != null) {
/* 793*/      separator = (separator == null) ? "" : separator;
/* 794*/      while (it.hasNext()) {
/* 795*/        append(it.next());
/* 796*/        if (it.hasNext())
/* 797*/          append(separator); 
/*   0*/      } 
/*   0*/    } 
/* 801*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendPadding(int length, char padChar) {
/* 813*/    if (length >= 0) {
/* 814*/      ensureCapacity(this.size + length);
/* 815*/      for (int i = 0; i < length; i++)
/* 816*/        this.buffer[this.size++] = padChar; 
/*   0*/    } 
/* 819*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendFixedWidthPadLeft(Object obj, int width, char padChar) {
/* 835*/    if (width > 0) {
/* 836*/      ensureCapacity(this.size + width);
/* 837*/      String str = (obj == null) ? getNullText() : obj.toString();
/* 838*/      int strLen = str.length();
/* 839*/      if (strLen >= width) {
/* 840*/        str.getChars(strLen - width, strLen, this.buffer, this.size);
/*   0*/      } else {
/* 842*/        int padLen = width - strLen;
/* 843*/        for (int i = 0; i < padLen; i++)
/* 844*/          this.buffer[this.size + i] = padChar; 
/* 846*/        str.getChars(0, strLen, this.buffer, this.size + padLen);
/*   0*/      } 
/* 848*/      this.size += width;
/*   0*/    } 
/* 850*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendFixedWidthPadLeft(int value, int width, char padChar) {
/* 864*/    return appendFixedWidthPadLeft(String.valueOf(value), width, padChar);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendFixedWidthPadRight(Object obj, int width, char padChar) {
/* 879*/    if (width > 0) {
/* 880*/      ensureCapacity(this.size + width);
/* 881*/      String str = (obj == null) ? getNullText() : obj.toString();
/* 882*/      int strLen = str.length();
/* 883*/      if (strLen >= width) {
/* 884*/        str.getChars(0, width, this.buffer, this.size);
/*   0*/      } else {
/* 886*/        int padLen = width - strLen;
/* 887*/        str.getChars(0, strLen, this.buffer, this.size);
/* 888*/        for (int i = 0; i < padLen; i++)
/* 889*/          this.buffer[this.size + strLen + i] = padChar; 
/*   0*/      } 
/* 892*/      this.size += width;
/*   0*/    } 
/* 894*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder appendFixedWidthPadRight(int value, int width, char padChar) {
/* 908*/    return appendFixedWidthPadRight(String.valueOf(value), width, padChar);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, Object obj) {
/* 922*/    if (obj == null)
/* 923*/      return insert(index, this.nullText); 
/* 925*/    return insert(index, obj.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, String str) {
/* 938*/    validateIndex(index);
/* 939*/    if (str == null)
/* 940*/      str = this.nullText; 
/* 942*/    int strLen = (str == null) ? 0 : str.length();
/* 943*/    if (strLen > 0) {
/* 944*/      int newSize = this.size + strLen;
/* 945*/      ensureCapacity(newSize);
/* 946*/      System.arraycopy(this.buffer, index, this.buffer, index + strLen, this.size - index);
/* 947*/      this.size = newSize;
/* 948*/      str.getChars(0, strLen, this.buffer, index);
/*   0*/    } 
/* 950*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, char[] chars) {
/* 963*/    validateIndex(index);
/* 964*/    if (chars == null)
/* 965*/      return insert(index, this.nullText); 
/* 967*/    int len = chars.length;
/* 968*/    if (len > 0) {
/* 969*/      ensureCapacity(this.size + len);
/* 970*/      System.arraycopy(this.buffer, index, this.buffer, index + len, this.size - index);
/* 971*/      System.arraycopy(chars, 0, this.buffer, index, len);
/* 972*/      this.size += len;
/*   0*/    } 
/* 974*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, char[] chars, int offset, int length) {
/* 989*/    validateIndex(index);
/* 990*/    if (chars == null)
/* 991*/      return insert(index, this.nullText); 
/* 993*/    if (offset < 0 || offset > chars.length)
/* 994*/      throw new StringIndexOutOfBoundsException("Invalid offset: " + offset); 
/* 996*/    if (length < 0 || offset + length > chars.length)
/* 997*/      throw new StringIndexOutOfBoundsException("Invalid length: " + length); 
/* 999*/    if (length > 0) {
/*1000*/      ensureCapacity(this.size + length);
/*1001*/      System.arraycopy(this.buffer, index, this.buffer, index + length, this.size - index);
/*1002*/      System.arraycopy(chars, offset, this.buffer, index, length);
/*1003*/      this.size += length;
/*   0*/    } 
/*1005*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, boolean value) {
/*1017*/    validateIndex(index);
/*1018*/    if (value) {
/*1019*/      ensureCapacity(this.size + 4);
/*1020*/      System.arraycopy(this.buffer, index, this.buffer, index + 4, this.size - index);
/*1021*/      this.buffer[index++] = 't';
/*1022*/      this.buffer[index++] = 'r';
/*1023*/      this.buffer[index++] = 'u';
/*1024*/      this.buffer[index] = 'e';
/*1025*/      this.size += 4;
/*   0*/    } else {
/*1027*/      ensureCapacity(this.size + 5);
/*1028*/      System.arraycopy(this.buffer, index, this.buffer, index + 5, this.size - index);
/*1029*/      this.buffer[index++] = 'f';
/*1030*/      this.buffer[index++] = 'a';
/*1031*/      this.buffer[index++] = 'l';
/*1032*/      this.buffer[index++] = 's';
/*1033*/      this.buffer[index] = 'e';
/*1034*/      this.size += 5;
/*   0*/    } 
/*1036*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, char value) {
/*1048*/    validateIndex(index);
/*1049*/    ensureCapacity(this.size + 1);
/*1050*/    System.arraycopy(this.buffer, index, this.buffer, index + 1, this.size - index);
/*1051*/    this.buffer[index] = value;
/*1052*/    this.size++;
/*1053*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, int value) {
/*1065*/    return insert(index, String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, long value) {
/*1077*/    return insert(index, String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, float value) {
/*1089*/    return insert(index, String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder insert(int index, double value) {
/*1101*/    return insert(index, String.valueOf(value));
/*   0*/  }
/*   0*/  
/*   0*/  private void deleteImpl(int startIndex, int endIndex, int len) {
/*1114*/    System.arraycopy(this.buffer, endIndex, this.buffer, startIndex, this.size - endIndex);
/*1115*/    this.size -= len;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder delete(int startIndex, int endIndex) {
/*1128*/    endIndex = validateRange(startIndex, endIndex);
/*1129*/    int len = endIndex - startIndex;
/*1130*/    if (len > 0)
/*1131*/      deleteImpl(startIndex, endIndex, len); 
/*1133*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteAll(char ch) {
/*1144*/    for (int i = 0; i < this.size; i++) {
/*1145*/      if (this.buffer[i] == ch) {
/*1146*/        int start = i;
/*   0*/        do {
/*   0*/        
/*1147*/        } while (++i < this.size && 
/*1148*/          this.buffer[i] == ch);
/*1152*/        int len = i - start;
/*1153*/        deleteImpl(start, i, len);
/*1154*/        i -= len;
/*   0*/      } 
/*   0*/    } 
/*1157*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteFirst(char ch) {
/*1167*/    for (int i = 0; i < this.size; i++) {
/*1168*/      if (this.buffer[i] == ch) {
/*1169*/        deleteImpl(i, i + 1, 1);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*1173*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteAll(String str) {
/*1184*/    int len = (str == null) ? 0 : str.length();
/*1185*/    if (len > 0) {
/*1186*/      int index = indexOf(str, 0);
/*1187*/      while (index >= 0) {
/*1188*/        deleteImpl(index, index + len, len);
/*1189*/        index = indexOf(str, index);
/*   0*/      } 
/*   0*/    } 
/*1192*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteFirst(String str) {
/*1202*/    int len = (str == null) ? 0 : str.length();
/*1203*/    if (len > 0) {
/*1204*/      int index = indexOf(str, 0);
/*1205*/      if (index >= 0)
/*1206*/        deleteImpl(index, index + len, len); 
/*   0*/    } 
/*1209*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteAll(StrMatcher matcher) {
/*1224*/    return replace(matcher, null, 0, this.size, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder deleteFirst(StrMatcher matcher) {
/*1238*/    return replace(matcher, null, 0, this.size, 1);
/*   0*/  }
/*   0*/  
/*   0*/  private void replaceImpl(int startIndex, int endIndex, int removeLen, String insertStr, int insertLen) {
/*1253*/    int newSize = this.size - removeLen + insertLen;
/*1254*/    if (insertLen != removeLen) {
/*1255*/      ensureCapacity(newSize);
/*1256*/      System.arraycopy(this.buffer, endIndex, this.buffer, startIndex + insertLen, this.size - endIndex);
/*1257*/      this.size = newSize;
/*   0*/    } 
/*1259*/    if (insertLen > 0)
/*1260*/      insertStr.getChars(0, insertLen, this.buffer, startIndex); 
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replace(int startIndex, int endIndex, String replaceStr) {
/*1276*/    endIndex = validateRange(startIndex, endIndex);
/*1277*/    int insertLen = (replaceStr == null) ? 0 : replaceStr.length();
/*1278*/    replaceImpl(startIndex, endIndex, endIndex - startIndex, replaceStr, insertLen);
/*1279*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replaceAll(char search, char replace) {
/*1292*/    if (search != replace)
/*1293*/      for (int i = 0; i < this.size; i++) {
/*1294*/        if (this.buffer[i] == search)
/*1295*/          this.buffer[i] = replace; 
/*   0*/      }  
/*1299*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replaceFirst(char search, char replace) {
/*1311*/    if (search != replace)
/*1312*/      for (int i = 0; i < this.size; i++) {
/*1313*/        if (this.buffer[i] == search) {
/*1314*/          this.buffer[i] = replace;
/*   0*/          break;
/*   0*/        } 
/*   0*/      }  
/*1319*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replaceAll(String searchStr, String replaceStr) {
/*1331*/    int searchLen = (searchStr == null) ? 0 : searchStr.length();
/*1332*/    if (searchLen > 0) {
/*1333*/      int replaceLen = (replaceStr == null) ? 0 : replaceStr.length();
/*1334*/      int index = indexOf(searchStr, 0);
/*1335*/      while (index >= 0) {
/*1336*/        replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
/*1337*/        index = indexOf(searchStr, index + replaceLen);
/*   0*/      } 
/*   0*/    } 
/*1340*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replaceFirst(String searchStr, String replaceStr) {
/*1351*/    int searchLen = (searchStr == null) ? 0 : searchStr.length();
/*1352*/    if (searchLen > 0) {
/*1353*/      int index = indexOf(searchStr, 0);
/*1354*/      if (index >= 0) {
/*1355*/        int replaceLen = (replaceStr == null) ? 0 : replaceStr.length();
/*1356*/        replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
/*   0*/      } 
/*   0*/    } 
/*1359*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replaceAll(StrMatcher matcher, String replaceStr) {
/*1375*/    return replace(matcher, replaceStr, 0, this.size, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replaceFirst(StrMatcher matcher, String replaceStr) {
/*1390*/    return replace(matcher, replaceStr, 0, this.size, 1);
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder replace(StrMatcher matcher, String replaceStr, int startIndex, int endIndex, int replaceCount) {
/*1413*/    endIndex = validateRange(startIndex, endIndex);
/*1414*/    return replaceImpl(matcher, replaceStr, startIndex, endIndex, replaceCount);
/*   0*/  }
/*   0*/  
/*   0*/  private StrBuilder replaceImpl(StrMatcher matcher, String replaceStr, int from, int to, int replaceCount) {
/*1435*/    if (matcher == null || this.size == 0)
/*1436*/      return this; 
/*1438*/    int replaceLen = (replaceStr == null) ? 0 : replaceStr.length();
/*1439*/    char[] buf = this.buffer;
/*1440*/    for (int i = from; i < to && replaceCount != 0; i++) {
/*1441*/      int removeLen = matcher.isMatch(buf, i, from, to);
/*1442*/      if (removeLen > 0) {
/*1443*/        replaceImpl(i, i + removeLen, removeLen, replaceStr, replaceLen);
/*1444*/        to = to - removeLen + replaceLen;
/*1445*/        i = i + replaceLen - 1;
/*1446*/        if (replaceCount > 0)
/*1447*/          replaceCount--; 
/*   0*/      } 
/*   0*/    } 
/*1451*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder reverse() {
/*1461*/    if (this.size == 0)
/*1462*/      return this; 
/*1465*/    int half = this.size / 2;
/*1466*/    char[] buf = this.buffer;
/*1467*/    for (int leftIdx = 0, rightIdx = this.size - 1; leftIdx < half; leftIdx++, rightIdx--) {
/*1468*/      char swap = buf[leftIdx];
/*1469*/      buf[leftIdx] = buf[rightIdx];
/*1470*/      buf[rightIdx] = swap;
/*   0*/    } 
/*1472*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StrBuilder trim() {
/*1483*/    if (this.size == 0)
/*1484*/      return this; 
/*1486*/    int len = this.size;
/*1487*/    char[] buf = this.buffer;
/*1488*/    int pos = 0;
/*1489*/    while (pos < len && buf[pos] <= ' ')
/*1490*/      pos++; 
/*1492*/    while (pos < len && buf[len - 1] <= ' ')
/*1493*/      len--; 
/*1495*/    if (len < this.size)
/*1496*/      delete(len, this.size); 
/*1498*/    if (pos > 0)
/*1499*/      delete(0, pos); 
/*1501*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean startsWith(String str) {
/*1514*/    if (str == null)
/*1515*/      return false; 
/*1517*/    int len = str.length();
/*1518*/    if (len == 0)
/*1519*/      return true; 
/*1521*/    if (len > this.size)
/*1522*/      return false; 
/*1524*/    for (int i = 0; i < len; i++) {
/*1525*/      if (this.buffer[i] != str.charAt(i))
/*1526*/        return false; 
/*   0*/    } 
/*1529*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean endsWith(String str) {
/*1541*/    if (str == null)
/*1542*/      return false; 
/*1544*/    int len = str.length();
/*1545*/    if (len == 0)
/*1546*/      return true; 
/*1548*/    if (len > this.size)
/*1549*/      return false; 
/*1551*/    int pos = this.size - len;
/*1552*/    for (int i = 0; i < len; i++, pos++) {
/*1553*/      if (this.buffer[pos] != str.charAt(i))
/*1554*/        return false; 
/*   0*/    } 
/*1557*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String substring(int start) {
/*1569*/    return substring(start, this.size);
/*   0*/  }
/*   0*/  
/*   0*/  public String substring(int startIndex, int endIndex) {
/*1586*/    endIndex = validateRange(startIndex, endIndex);
/*1587*/    return new String(this.buffer, startIndex, endIndex - startIndex);
/*   0*/  }
/*   0*/  
/*   0*/  public String leftString(int length) {
/*1603*/    if (length <= 0)
/*1604*/      return ""; 
/*1605*/    if (length >= this.size)
/*1606*/      return new String(this.buffer, 0, this.size); 
/*1608*/    return new String(this.buffer, 0, length);
/*   0*/  }
/*   0*/  
/*   0*/  public String rightString(int length) {
/*1625*/    if (length <= 0)
/*1626*/      return ""; 
/*1627*/    if (length >= this.size)
/*1628*/      return new String(this.buffer, 0, this.size); 
/*1630*/    return new String(this.buffer, this.size - length, length);
/*   0*/  }
/*   0*/  
/*   0*/  public String midString(int index, int length) {
/*1651*/    if (index < 0)
/*1652*/      index = 0; 
/*1654*/    if (length <= 0 || index >= this.size)
/*1655*/      return ""; 
/*1657*/    if (this.size <= index + length)
/*1658*/      return new String(this.buffer, index, this.size - index); 
/*1660*/    return new String(this.buffer, index, length);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean contains(char ch) {
/*1672*/    char[] thisBuf = this.buffer;
/*1673*/    for (int i = 0; i < this.size; i++) {
/*1674*/      if (thisBuf[i] == ch)
/*1675*/        return true; 
/*   0*/    } 
/*1678*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean contains(String str) {
/*1688*/    return (indexOf(str, 0) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean contains(StrMatcher matcher) {
/*1703*/    return (indexOf(matcher, 0) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(char ch) {
/*1714*/    return indexOf(ch, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(char ch, int startIndex) {
/*1725*/    startIndex = (startIndex < 0) ? 0 : startIndex;
/*1726*/    if (startIndex >= this.size)
/*1727*/      return -1; 
/*1729*/    char[] thisBuf = this.buffer;
/*1730*/    for (int i = startIndex; i < this.size; i++) {
/*1731*/      if (thisBuf[i] == ch)
/*1732*/        return i; 
/*   0*/    } 
/*1735*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(String str) {
/*1747*/    return indexOf(str, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(String str, int startIndex) {
/*1761*/    startIndex = (startIndex < 0) ? 0 : startIndex;
/*1762*/    if (str == null || startIndex >= this.size)
/*1763*/      return -1; 
/*1765*/    int strLen = str.length();
/*1766*/    if (strLen == 1)
/*1767*/      return indexOf(str.charAt(0), startIndex); 
/*1769*/    if (strLen == 0)
/*1770*/      return startIndex; 
/*1772*/    if (strLen > this.size)
/*1773*/      return -1; 
/*1775*/    char[] thisBuf = this.buffer;
/*1776*/    int len = this.size - strLen + 1;
/*1778*/    for (int i = startIndex; i < len; i++) {
/*1779*/      int j = 0;
/*   0*/      while (true) {
/*1779*/        if (j < strLen) {
/*1780*/          if (str.charAt(j) != thisBuf[i + j])
/*   0*/            break; 
/*   0*/          j++;
/*   0*/          continue;
/*   0*/        } 
/*1784*/        return i;
/*   0*/      } 
/*   0*/    } 
/*1786*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(StrMatcher matcher) {
/*1800*/    return indexOf(matcher, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(StrMatcher matcher, int startIndex) {
/*1816*/    startIndex = (startIndex < 0) ? 0 : startIndex;
/*1817*/    if (matcher == null || startIndex >= this.size)
/*1818*/      return -1; 
/*1820*/    int len = this.size;
/*1821*/    char[] buf = this.buffer;
/*1822*/    for (int i = startIndex; i < len; i++) {
/*1823*/      if (matcher.isMatch(buf, i, startIndex, len) > 0)
/*1824*/        return i; 
/*   0*/    } 
/*1827*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int lastIndexOf(char ch) {
/*1838*/    return lastIndexOf(ch, this.size - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public int lastIndexOf(char ch, int startIndex) {
/*1849*/    startIndex = (startIndex >= this.size) ? (this.size - 1) : startIndex;
/*1850*/    if (startIndex < 0)
/*1851*/      return -1; 
/*1853*/    for (int i = startIndex; i >= 0; i--) {
/*1854*/      if (this.buffer[i] == ch)
/*1855*/        return i; 
/*   0*/    } 
/*1858*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int lastIndexOf(String str) {
/*1870*/    return lastIndexOf(str, this.size - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public int lastIndexOf(String str, int startIndex) {
/*1884*/    startIndex = (startIndex >= this.size) ? (this.size - 1) : startIndex;
/*1885*/    if (str == null || startIndex < 0)
/*1886*/      return -1; 
/*1888*/    int strLen = str.length();
/*1889*/    if (strLen > 0 && strLen <= this.size) {
/*1890*/      if (strLen == 1)
/*1891*/        return lastIndexOf(str.charAt(0), startIndex); 
/*1895*/      for (int i = startIndex - strLen + 1; i >= 0; i--) {
/*1896*/        int j = 0;
/*   0*/        while (true) {
/*1896*/          if (j < strLen) {
/*1897*/            if (str.charAt(j) != this.buffer[i + j])
/*   0*/              break; 
/*   0*/            j++;
/*   0*/            continue;
/*   0*/          } 
/*1901*/          return i;
/*   0*/        } 
/*   0*/      } 
/*1904*/    } else if (strLen == 0) {
/*1905*/      return startIndex;
/*   0*/    } 
/*1907*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int lastIndexOf(StrMatcher matcher) {
/*1921*/    return lastIndexOf(matcher, this.size);
/*   0*/  }
/*   0*/  
/*   0*/  public int lastIndexOf(StrMatcher matcher, int startIndex) {
/*1937*/    startIndex = (startIndex >= this.size) ? (this.size - 1) : startIndex;
/*1938*/    if (matcher == null || startIndex < 0)
/*1939*/      return -1; 
/*1941*/    char[] buf = this.buffer;
/*1942*/    int endIndex = startIndex + 1;
/*1943*/    for (int i = startIndex; i >= 0; i--) {
/*1944*/      if (matcher.isMatch(buf, i, 0, endIndex) > 0)
/*1945*/        return i; 
/*   0*/    } 
/*1948*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public StrTokenizer asTokenizer() {
/*1985*/    return new StrBuilderTokenizer(this);
/*   0*/  }
/*   0*/  
/*   0*/  public Reader asReader() {
/*2009*/    return new StrBuilderReader(this);
/*   0*/  }
/*   0*/  
/*   0*/  public Writer asWriter() {
/*2034*/    return new StrBuilderWriter(this);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equalsIgnoreCase(StrBuilder other) {
/*2076*/    if (this == other)
/*2077*/      return true; 
/*2079*/    if (this.size != other.size)
/*2080*/      return false; 
/*2082*/    char[] thisBuf = this.buffer;
/*2083*/    char[] otherBuf = other.buffer;
/*2084*/    for (int i = this.size - 1; i >= 0; i--) {
/*2085*/      char c1 = thisBuf[i];
/*2086*/      char c2 = otherBuf[i];
/*2087*/      if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2))
/*2088*/        return false; 
/*   0*/    } 
/*2091*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(StrBuilder other) {
/*2102*/    if (this == other)
/*2103*/      return true; 
/*2105*/    if (this.size != other.size)
/*2106*/      return false; 
/*2108*/    char[] thisBuf = this.buffer;
/*2109*/    char[] otherBuf = other.buffer;
/*2110*/    for (int i = this.size - 1; i >= 0; i--) {
/*2111*/      if (thisBuf[i] != otherBuf[i])
/*2112*/        return false; 
/*   0*/    } 
/*2115*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*2126*/    if (obj instanceof StrBuilder)
/*2127*/      return equals((StrBuilder)obj); 
/*2129*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*2138*/    char[] buf = this.buffer;
/*2139*/    int hash = 0;
/*2140*/    for (int i = this.size - 1; i >= 0; i--)
/*2141*/      hash = 31 * hash + buf[i]; 
/*2143*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*2157*/    return new String(this.buffer, 0, this.size);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer toStringBuffer() {
/*2167*/    return new StringBuffer(this.size).append(this.buffer, 0, this.size);
/*   0*/  }
/*   0*/  
/*   0*/  protected int validateRange(int startIndex, int endIndex) {
/*2181*/    if (startIndex < 0)
/*2182*/      throw new StringIndexOutOfBoundsException(startIndex); 
/*2184*/    if (endIndex > this.size)
/*2185*/      endIndex = this.size; 
/*2187*/    if (startIndex > endIndex)
/*2188*/      throw new StringIndexOutOfBoundsException("end < start"); 
/*2190*/    return endIndex;
/*   0*/  }
/*   0*/  
/*   0*/  protected void validateIndex(int index) {
/*2200*/    if (index < 0 || index > this.size)
/*2201*/      throw new StringIndexOutOfBoundsException(index); 
/*   0*/  }
/*   0*/  
/*   0*/  class StrBuilderTokenizer extends StrTokenizer {
/*   0*/    private final StrBuilder this$0;
/*   0*/    
/*   0*/    StrBuilderTokenizer(StrBuilder this$0) {
/*2212*/      this.this$0 = this$0;
/*   0*/    }
/*   0*/    
/*   0*/    protected List tokenize(char[] chars, int offset, int count) {
/*2218*/      if (chars == null)
/*2219*/        return super.tokenize(this.this$0.buffer, 0, this.this$0.size()); 
/*2221*/      return super.tokenize(chars, offset, count);
/*   0*/    }
/*   0*/    
/*   0*/    public String getContent() {
/*2227*/      String str = super.getContent();
/*2228*/      if (str == null)
/*2229*/        return this.this$0.toString(); 
/*2231*/      return str;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  class StrBuilderReader extends Reader {
/*   0*/    private int pos;
/*   0*/    
/*   0*/    private int mark;
/*   0*/    
/*   0*/    private final StrBuilder this$0;
/*   0*/    
/*   0*/    StrBuilderReader(StrBuilder this$0) {
/*2247*/      this.this$0 = this$0;
/*   0*/    }
/*   0*/    
/*   0*/    public void close() {}
/*   0*/    
/*   0*/    public int read() {
/*2258*/      if (!ready())
/*2259*/        return -1; 
/*2261*/      return this.this$0.charAt(this.pos++);
/*   0*/    }
/*   0*/    
/*   0*/    public int read(char[] b, int off, int len) {
/*2266*/      if (off < 0 || len < 0 || off > b.length || off + len > b.length || off + len < 0)
/*2268*/        throw new IndexOutOfBoundsException(); 
/*2270*/      if (len == 0)
/*2271*/        return 0; 
/*2273*/      if (this.pos >= this.this$0.size())
/*2274*/        return -1; 
/*2276*/      if (this.pos + len > this.this$0.size())
/*2277*/        len = this.this$0.size() - this.pos; 
/*2279*/      this.this$0.getChars(this.pos, this.pos + len, b, off);
/*2280*/      this.pos += len;
/*2281*/      return len;
/*   0*/    }
/*   0*/    
/*   0*/    public long skip(long n) {
/*2286*/      if (this.pos + n > this.this$0.size())
/*2287*/        n = (this.this$0.size() - this.pos); 
/*2289*/      if (n < 0L)
/*2290*/        return 0L; 
/*2292*/      this.pos = (int)(this.pos + n);
/*2293*/      return n;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean ready() {
/*2298*/      return (this.pos < this.this$0.size());
/*   0*/    }
/*   0*/    
/*   0*/    public boolean markSupported() {
/*2303*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void mark(int readAheadLimit) {
/*2308*/      this.mark = this.pos;
/*   0*/    }
/*   0*/    
/*   0*/    public void reset() {
/*2313*/      this.pos = this.mark;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  class StrBuilderWriter extends Writer {
/*   0*/    private final StrBuilder this$0;
/*   0*/    
/*   0*/    StrBuilderWriter(StrBuilder this$0) {
/*2324*/      this.this$0 = this$0;
/*   0*/    }
/*   0*/    
/*   0*/    public void close() {}
/*   0*/    
/*   0*/    public void flush() {}
/*   0*/    
/*   0*/    public void write(int c) {
/*2340*/      this.this$0.append((char)c);
/*   0*/    }
/*   0*/    
/*   0*/    public void write(char[] cbuf) {
/*2345*/      this.this$0.append(cbuf);
/*   0*/    }
/*   0*/    
/*   0*/    public void write(char[] cbuf, int off, int len) {
/*2350*/      this.this$0.append(cbuf, off, len);
/*   0*/    }
/*   0*/    
/*   0*/    public void write(String str) {
/*2355*/      this.this$0.append(str);
/*   0*/    }
/*   0*/    
/*   0*/    public void write(String str, int off, int len) {
/*2360*/      this.this$0.append(str, off, len);
/*   0*/    }
/*   0*/  }
/*   0*/}
