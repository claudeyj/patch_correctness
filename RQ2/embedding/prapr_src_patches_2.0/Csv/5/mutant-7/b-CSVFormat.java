/*   0*/package org.apache.commons.csv;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.Reader;
/*   0*/import java.io.Serializable;
/*   0*/import java.io.StringWriter;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public final class CSVFormat implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  private final char delimiter;
/*   0*/  
/*   0*/  private final Character quoteChar;
/*   0*/  
/*   0*/  private final Quote quotePolicy;
/*   0*/  
/*   0*/  private final Character commentStart;
/*   0*/  
/*   0*/  private final Character escape;
/*   0*/  
/*   0*/  private final boolean ignoreSurroundingSpaces;
/*   0*/  
/*   0*/  private final boolean ignoreEmptyLines;
/*   0*/  
/*   0*/  private final String recordSeparator;
/*   0*/  
/*   0*/  private final String nullString;
/*   0*/  
/*   0*/  private final String[] header;
/*   0*/  
/*   0*/  private final boolean skipHeaderRecord;
/*   0*/  
/* 174*/  public static final CSVFormat DEFAULT = new CSVFormat(',', Constants.DOUBLE_QUOTE_CHAR, null, null, null, false, true, "\r\n", null, null, false);
/*   0*/  
/* 186*/  public static final CSVFormat RFC4180 = DEFAULT.withIgnoreEmptyLines(false);
/*   0*/  
/* 205*/  public static final CSVFormat EXCEL = DEFAULT.withIgnoreEmptyLines(false);
/*   0*/  
/* 208*/  public static final CSVFormat TDF = DEFAULT.withDelimiter('\t').withIgnoreSurroundingSpaces(true);
/*   0*/  
/* 221*/  public static final CSVFormat MYSQL = DEFAULT.withDelimiter('\t').withEscape('\\').withIgnoreEmptyLines(false).withQuoteChar(null).withRecordSeparator('\n');
/*   0*/  
/*   0*/  private static boolean isLineBreak(char c) {
/* 238*/    return (c == '\n' || c == '\r');
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isLineBreak(Character c) {
/* 250*/    return (c != null && isLineBreak(c));
/*   0*/  }
/*   0*/  
/*   0*/  public static CSVFormat newFormat(char delimiter) {
/* 262*/    return new CSVFormat(delimiter, null, null, null, null, false, false, null, null, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  CSVFormat(char delimiter, Character quoteChar, Quote quotePolicy, Character commentStart, Character escape, boolean ignoreSurroundingSpaces, boolean ignoreEmptyLines, String recordSeparator, String nullString, String[] header, boolean skipHeaderRecord) {
/* 297*/    if (isLineBreak(delimiter)) {
/* 298*/        throw new IllegalArgumentException("The delimiter cannot be a line break"); 
/*   0*/       }
/* 300*/    this.delimiter = delimiter;
/* 301*/    this.quoteChar = quoteChar;
/* 302*/    this.quotePolicy = quotePolicy;
/* 303*/    this.commentStart = commentStart;
/* 304*/    this.escape = escape;
/* 305*/    this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
/* 306*/    this.ignoreEmptyLines = ignoreEmptyLines;
/* 307*/    this.recordSeparator = recordSeparator;
/* 308*/    this.nullString = nullString;
/* 309*/    this.header = (header == null) ? null : (String[])header.clone();
/* 310*/    this.skipHeaderRecord = skipHeaderRecord;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 315*/    if (this == obj) {
/* 316*/        return true; 
/*   0*/       }
/* 318*/    if (obj == null) {
/* 319*/        return false; 
/*   0*/       }
/* 321*/    if (getClass() != obj.getClass()) {
/* 322*/        return false; 
/*   0*/       }
/* 325*/    CSVFormat other = (CSVFormat)obj;
/* 326*/    if (this.delimiter != other.delimiter) {
/* 327*/        return false; 
/*   0*/       }
/* 329*/    if (this.quotePolicy != other.quotePolicy) {
/* 330*/        return false; 
/*   0*/       }
/* 332*/    if (this.quoteChar == null) {
/* 333*/      if (other.quoteChar != null) {
/* 334*/          return false; 
/*   0*/         }
/* 336*/    } else if (!this.quoteChar.equals(other.quoteChar)) {
/* 337*/      return false;
/*   0*/    } 
/* 339*/    if (this.commentStart == null) {
/* 340*/      if (other.commentStart != null) {
/* 341*/          return false; 
/*   0*/         }
/* 343*/    } else if (!this.commentStart.equals(other.commentStart)) {
/* 344*/      return false;
/*   0*/    } 
/* 346*/    if (this.escape == null) {
/* 347*/      if (other.escape != null) {
/* 348*/          return false; 
/*   0*/         }
/* 350*/    } else if (!this.escape.equals(other.escape)) {
/* 351*/      return false;
/*   0*/    } 
/* 353*/    if (!Arrays.equals((Object[])this.header, (Object[])other.header)) {
/* 354*/        return false; 
/*   0*/       }
/* 356*/    if (this.ignoreSurroundingSpaces != other.ignoreSurroundingSpaces) {
/* 357*/        return false; 
/*   0*/       }
/* 359*/    if (this.ignoreEmptyLines != other.ignoreEmptyLines) {
/* 360*/        return false; 
/*   0*/       }
/* 362*/    if (this.recordSeparator == null) {
/* 363*/      if (other.recordSeparator != null) {
/* 364*/          return false; 
/*   0*/         }
/* 366*/    } else if (!this.recordSeparator.equals(other.recordSeparator)) {
/* 367*/      return false;
/*   0*/    } 
/* 369*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String format(Object... values) {
/* 380*/    StringWriter out = new StringWriter();
/*   0*/    try {
/* 382*/      new CSVPrinter(out, this).printRecord(values);
/* 383*/      return out.toString().trim();
/* 384*/    } catch (IOException e) {
/* 386*/      throw new IllegalStateException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Character getCommentStart() {
/* 396*/    return this.commentStart;
/*   0*/  }
/*   0*/  
/*   0*/  public char getDelimiter() {
/* 405*/    return this.delimiter;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getEscape() {
/* 414*/    return this.escape;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] getHeader() {
/* 423*/    return (this.header != null) ? (String[])this.header.clone() : null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreEmptyLines() {
/* 433*/    return this.ignoreEmptyLines;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreSurroundingSpaces() {
/* 443*/    return this.ignoreSurroundingSpaces;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNullString() {
/* 460*/    return this.nullString;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getQuoteChar() {
/* 469*/    return this.quoteChar;
/*   0*/  }
/*   0*/  
/*   0*/  public Quote getQuotePolicy() {
/* 478*/    return this.quotePolicy;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRecordSeparator() {
/* 487*/    return this.recordSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getSkipHeaderRecord() {
/* 496*/    return this.skipHeaderRecord;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 502*/    int prime = 31;
/* 503*/    int result = 1;
/* 505*/    result = 31 * result + this.delimiter;
/* 506*/    result = 31 * result + ((this.quotePolicy == null) ? 0 : this.quotePolicy.hashCode());
/* 507*/    result = 31 * result + ((this.quoteChar == null) ? 0 : this.quoteChar.hashCode());
/* 508*/    result = 31 * result + ((this.commentStart == null) ? 0 : this.commentStart.hashCode());
/* 509*/    result = 31 * result + ((this.escape == null) ? 0 : this.escape.hashCode());
/* 510*/    result = 31 * result + (this.ignoreSurroundingSpaces ? 1231 : 1237);
/* 511*/    result = 31 * result + (this.ignoreEmptyLines ? 1231 : 1237);
/* 512*/    result = 31 * result + ((this.recordSeparator == null) ? 0 : this.recordSeparator.hashCode());
/* 513*/    result = 31 * result + Arrays.hashCode((Object[])this.header);
/* 514*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCommentingEnabled() {
/* 525*/    return (this.commentStart != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEscaping() {
/* 534*/    return (this.escape != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNullHandling() {
/* 543*/    return (this.nullString != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuoting() {
/* 552*/    return (this.quoteChar != null);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVParser parse(Reader in) throws IOException {
/* 569*/    return new CSVParser(in, this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 574*/    StringBuilder sb = new StringBuilder();
/* 575*/    sb.append("Delimiter=<").append(this.delimiter).append('>');
/* 576*/    if (isEscaping()) {
/* 577*/      sb.append(' ');
/* 578*/      sb.append("Escape=<").append(this.escape).append('>');
/*   0*/    } 
/* 580*/    if (isQuoting()) {
/* 581*/      sb.append(' ');
/* 582*/      sb.append("QuoteChar=<").append(this.quoteChar).append('>');
/*   0*/    } 
/* 584*/    if (isCommentingEnabled()) {
/* 585*/      sb.append(' ');
/* 586*/      sb.append("CommentStart=<").append(this.commentStart).append('>');
/*   0*/    } 
/* 588*/    if (isNullHandling()) {
/* 589*/      sb.append(' ');
/* 590*/      sb.append("NullString=<").append(this.nullString).append('>');
/*   0*/    } 
/* 592*/    if (this.recordSeparator != null) {
/* 593*/      sb.append(' ');
/* 594*/      sb.append("RecordSeparator=<").append(this.recordSeparator).append('>');
/*   0*/    } 
/* 596*/    if (getIgnoreEmptyLines()) {
/* 597*/        sb.append(" EmptyLines:ignored"); 
/*   0*/       }
/* 599*/    if (getIgnoreSurroundingSpaces()) {
/* 600*/        sb.append(" SurroundingSpaces:ignored"); 
/*   0*/       }
/* 602*/    sb.append(" SkipHeaderRecord:").append(this.skipHeaderRecord);
/* 603*/    if (this.header != null) {
/* 604*/      sb.append(' ');
/* 605*/      sb.append("Header:").append(Arrays.toString((Object[])this.header));
/*   0*/    } 
/* 607*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  void validate() throws IllegalStateException {
/* 616*/    if (this.quoteChar != null && this.delimiter == this.quoteChar) {
/* 617*/        throw new IllegalStateException("The quoteChar character and the delimiter cannot be the same ('" + this.quoteChar + "')"); 
/*   0*/       }
/* 621*/    if (this.escape != null && this.delimiter == this.escape) {
/* 622*/        throw new IllegalStateException("The escape character and the delimiter cannot be the same ('" + this.escape + "')"); 
/*   0*/       }
/* 626*/    if (this.commentStart != null && this.delimiter == this.commentStart) {
/* 627*/        throw new IllegalStateException("The comment start character and the delimiter cannot be the same ('" + this.commentStart + "')"); 
/*   0*/       }
/* 631*/    if (this.quoteChar != null && this.quoteChar.equals(this.commentStart)) {
/* 632*/        throw new IllegalStateException("The comment start character and the quoteChar cannot be the same ('" + this.commentStart + "')"); 
/*   0*/       }
/* 636*/    if (this.escape != null && this.escape.equals(this.commentStart)) {
/* 637*/        throw new IllegalStateException("The comment start and the escape character cannot be the same ('" + this.commentStart + "')"); 
/*   0*/       }
/* 641*/    if (this.escape == null && this.quotePolicy == Quote.NONE) {
/* 642*/        throw new IllegalStateException("No quotes mode set but no escape character is set"); 
/*   0*/       }
/* 645*/    if (this.header != null) {
/* 646*/      Set<String> set = new HashSet<String>(this.header.length);
/* 647*/      set.addAll(Arrays.asList(this.header));
/* 648*/      if (set.size() != this.header.length) {
/* 649*/          throw new IllegalStateException("The header contains duplicate names: " + Arrays.toString(this.header)); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withCommentStart(char commentStart) {
/* 666*/    return withCommentStart(commentStart);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withCommentStart(Character commentStart) {
/* 681*/    if (isLineBreak(commentStart)) {
/* 682*/        throw new IllegalArgumentException("The comment start character cannot be a line break"); 
/*   0*/       }
/* 684*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withDelimiter(char delimiter) {
/* 698*/    if (isLineBreak(delimiter)) {
/* 699*/        throw new IllegalArgumentException("The delimiter cannot be a line break"); 
/*   0*/       }
/* 701*/    return new CSVFormat(delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withEscape(char escape) {
/* 715*/    return withEscape(escape);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withEscape(Character escape) {
/* 728*/    if (isLineBreak(escape)) {
/* 729*/        throw new IllegalArgumentException("The escape character cannot be a line break"); 
/*   0*/       }
/* 731*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeader(String... header) {
/* 753*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, TDF.recordSeparator, this.nullString, header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreEmptyLines(boolean ignoreEmptyLines) {
/* 766*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
/* 779*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withNullString(String nullString) {
/* 799*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuoteChar(char quoteChar) {
/* 813*/    return withQuoteChar(quoteChar);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuoteChar(Character quoteChar) {
/* 826*/    if (isLineBreak(quoteChar)) {
/* 827*/        throw new IllegalArgumentException("The quoteChar cannot be a line break"); 
/*   0*/       }
/* 829*/    return new CSVFormat(this.delimiter, quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuotePolicy(Quote quotePolicy) {
/* 842*/    return new CSVFormat(this.delimiter, this.quoteChar, quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withRecordSeparator(char recordSeparator) {
/* 855*/    return withRecordSeparator(String.valueOf(recordSeparator));
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withRecordSeparator(String recordSeparator) {
/* 867*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, recordSeparator, this.nullString, this.header, this.skipHeaderRecord);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withSkipHeaderRecord(boolean skipHeaderRecord) {
/* 881*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, skipHeaderRecord);
/*   0*/  }
/*   0*/}
