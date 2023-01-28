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
/*   0*/  private final boolean ignoreEmptyHeaders;
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
/* 175*/  public static final CSVFormat DEFAULT = new CSVFormat(',', Constants.DOUBLE_QUOTE_CHAR, null, null, null, false, true, "\r\n", null, null, false, false);
/*   0*/  
/* 187*/  public static final CSVFormat RFC4180 = DEFAULT.withIgnoreEmptyLines(false);
/*   0*/  
/* 211*/  public static final CSVFormat EXCEL = DEFAULT.withIgnoreEmptyLines(false);
/*   0*/  
/* 214*/  public static final CSVFormat TDF = DEFAULT.withDelimiter('\t').withIgnoreSurroundingSpaces(true);
/*   0*/  
/* 227*/  public static final CSVFormat MYSQL = DEFAULT.withDelimiter('\t').withEscape('\\').withIgnoreEmptyLines(false).withQuoteChar(null).withRecordSeparator('\n');
/*   0*/  
/*   0*/  private static boolean isLineBreak(char c) {
/* 244*/    return (c == '\n' || c == '\r');
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isLineBreak(Character c) {
/* 256*/    return (c != null && isLineBreak(c));
/*   0*/  }
/*   0*/  
/*   0*/  public static CSVFormat newFormat(char delimiter) {
/* 268*/    return new CSVFormat(delimiter, null, null, null, null, false, false, null, null, null, false, false);
/*   0*/  }
/*   0*/  
/*   0*/  private CSVFormat(char delimiter, Character quoteChar, Quote quotePolicy, Character commentStart, Character escape, boolean ignoreSurroundingSpaces, boolean ignoreEmptyLines, String recordSeparator, String nullString, String[] header, boolean skipHeaderRecord, boolean ignoreEmptyHeaders) {
/* 304*/    if (isLineBreak(delimiter)) {
/* 305*/        throw new IllegalArgumentException("The delimiter cannot be a line break"); 
/*   0*/       }
/* 307*/    this.delimiter = delimiter;
/* 308*/    this.quoteChar = quoteChar;
/* 309*/    this.quotePolicy = quotePolicy;
/* 310*/    this.commentStart = commentStart;
/* 311*/    this.escape = escape;
/* 312*/    this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
/* 313*/    this.ignoreEmptyHeaders = ignoreEmptyHeaders;
/* 314*/    this.ignoreEmptyLines = ignoreEmptyLines;
/* 315*/    this.recordSeparator = recordSeparator;
/* 316*/    this.nullString = nullString;
/* 317*/    if (header == null) {
/* 318*/      this.header = null;
/*   0*/    } else {
/* 320*/      Set<String> dupCheck = new HashSet<String>();
/* 321*/      for (String hdr : header) {
/* 322*/        if (!dupCheck.add(hdr)) {
/* 323*/            throw new IllegalArgumentException("The header contains a duplicate entry: '" + hdr + "' in " + Arrays.toString(header)); 
/*   0*/           }
/*   0*/      } 
/* 327*/      this.header = (String[])header.clone();
/*   0*/    } 
/* 329*/    this.skipHeaderRecord = skipHeaderRecord;
/* 330*/    validate();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 335*/    if (this == obj) {
/* 336*/        return true; 
/*   0*/       }
/* 338*/    if (obj == null) {
/* 339*/        return false; 
/*   0*/       }
/* 341*/    if (getClass() != obj.getClass()) {
/* 342*/        return false; 
/*   0*/       }
/* 345*/    CSVFormat other = (CSVFormat)obj;
/* 346*/    if (this.delimiter != other.delimiter) {
/* 347*/        return false; 
/*   0*/       }
/* 349*/    if (this.quotePolicy != other.quotePolicy) {
/* 350*/        return false; 
/*   0*/       }
/* 352*/    if (this.quoteChar == null) {
/* 353*/      if (other.quoteChar != null) {
/* 354*/          return false; 
/*   0*/         }
/* 356*/    } else if (!this.quoteChar.equals(other.quoteChar)) {
/* 357*/      return false;
/*   0*/    } 
/* 359*/    if (this.commentStart == null) {
/* 360*/      if (other.commentStart != null) {
/* 361*/          return false; 
/*   0*/         }
/* 363*/    } else if (!this.commentStart.equals(other.commentStart)) {
/* 364*/      return false;
/*   0*/    } 
/* 366*/    if (this.escape == null) {
/* 367*/      if (other.escape != null) {
/* 368*/          return false; 
/*   0*/         }
/* 370*/    } else if (!this.escape.equals(other.escape)) {
/* 371*/      return false;
/*   0*/    } 
/* 373*/    if (this.nullString == null) {
/* 374*/      if (other.nullString != null) {
/* 375*/          return false; 
/*   0*/         }
/* 377*/    } else if (!this.nullString.equals(other.nullString)) {
/* 378*/      return false;
/*   0*/    } 
/* 380*/    if (!Arrays.equals((Object[])this.header, (Object[])other.header)) {
/* 381*/        return false; 
/*   0*/       }
/* 383*/    if (this.ignoreSurroundingSpaces != other.ignoreSurroundingSpaces) {
/* 384*/        return false; 
/*   0*/       }
/* 386*/    if (this.ignoreEmptyLines != other.ignoreEmptyLines) {
/* 387*/        return false; 
/*   0*/       }
/* 389*/    if (this.skipHeaderRecord != other.skipHeaderRecord) {
/* 390*/        return false; 
/*   0*/       }
/* 392*/    if (this.recordSeparator == null) {
/* 393*/      if (other.recordSeparator != null) {
/* 394*/          return false; 
/*   0*/         }
/* 396*/    } else if (!this.recordSeparator.equals(other.recordSeparator)) {
/* 397*/      return false;
/*   0*/    } 
/* 399*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String format(Object... values) {
/* 410*/    StringWriter out = new StringWriter();
/*   0*/    try {
/* 412*/      new CSVPrinter(out, this).printRecord(values);
/* 413*/      return out.toString().trim();
/* 414*/    } catch (IOException e) {
/* 416*/      throw new IllegalStateException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Character getCommentStart() {
/* 426*/    return this.commentStart;
/*   0*/  }
/*   0*/  
/*   0*/  public char getDelimiter() {
/* 435*/    return this.delimiter;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getEscape() {
/* 444*/    return this.escape;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] getHeader() {
/* 453*/    return (this.header != null) ? (String[])this.header.clone() : null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreEmptyHeaders() {
/* 463*/    return this.ignoreEmptyHeaders;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreEmptyLines() {
/* 473*/    return this.ignoreEmptyLines;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreSurroundingSpaces() {
/* 483*/    return this.ignoreSurroundingSpaces;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNullString() {
/* 500*/    return this.nullString;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getQuoteChar() {
/* 509*/    return this.quoteChar;
/*   0*/  }
/*   0*/  
/*   0*/  public Quote getQuotePolicy() {
/* 518*/    return this.quotePolicy;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRecordSeparator() {
/* 527*/    return this.recordSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getSkipHeaderRecord() {
/* 536*/    return this.skipHeaderRecord;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 542*/    int prime = 31;
/* 543*/    int result = 1;
/* 545*/    result = 31 * result + this.delimiter;
/* 546*/    result = 31 * result + ((this.quotePolicy == null) ? 0 : this.quotePolicy.hashCode());
/* 547*/    result = 31 * result + ((this.quoteChar == null) ? 0 : this.quoteChar.hashCode());
/* 548*/    result = 31 * result + ((this.commentStart == null) ? 0 : this.commentStart.hashCode());
/* 549*/    result = 31 * result + ((this.escape == null) ? 0 : this.escape.hashCode());
/* 550*/    result = 31 * result + ((this.nullString == null) ? 0 : this.nullString.hashCode());
/* 551*/    result = 31 * result + (this.ignoreSurroundingSpaces ? 1231 : 1237);
/* 552*/    result = 31 * result + (this.ignoreEmptyLines ? 1231 : 1237);
/* 553*/    result = 31 * result + (this.skipHeaderRecord ? 1231 : 1237);
/* 554*/    result = 31 * result + ((this.recordSeparator == null) ? 0 : this.recordSeparator.hashCode());
/* 555*/    result = 31 * result + Arrays.hashCode((Object[])this.header);
/* 556*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCommentingEnabled() {
/* 567*/    return (this.commentStart != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEscaping() {
/* 576*/    return (this.escape != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNullHandling() {
/* 585*/    return (this.nullString != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuoting() {
/* 594*/    return (this.quoteChar != null);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVParser parse(Reader in) throws IOException {
/* 611*/    return new CSVParser(in, this);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVPrinter print(Appendable out) throws IOException {
/* 628*/    return new CSVPrinter(out, this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 633*/    StringBuilder sb = new StringBuilder();
/* 634*/    sb.append("Delimiter=<").append(this.delimiter).append('>');
/* 635*/    if (isEscaping()) {
/* 636*/      sb.append(' ');
/* 637*/      sb.append("Escape=<").append(this.escape).append('>');
/*   0*/    } 
/* 639*/    if (isQuoting()) {
/* 640*/      sb.append(' ');
/* 641*/      sb.append("QuoteChar=<").append(this.quoteChar).append('>');
/*   0*/    } 
/* 643*/    if (isCommentingEnabled()) {
/* 644*/      sb.append(' ');
/* 645*/      sb.append("CommentStart=<").append(this.commentStart).append('>');
/*   0*/    } 
/* 647*/    if (isNullHandling()) {
/* 648*/      sb.append(' ');
/* 649*/      sb.append("NullString=<").append(this.nullString).append('>');
/*   0*/    } 
/* 651*/    if (this.recordSeparator != null) {
/* 652*/      sb.append(' ');
/* 653*/      sb.append("RecordSeparator=<").append(this.recordSeparator).append('>');
/*   0*/    } 
/* 655*/    if (getIgnoreEmptyLines()) {
/* 656*/        sb.append(" EmptyLines:ignored"); 
/*   0*/       }
/* 658*/    if (getIgnoreSurroundingSpaces()) {
/* 659*/        sb.append(" SurroundingSpaces:ignored"); 
/*   0*/       }
/* 661*/    sb.append(" SkipHeaderRecord:").append(this.skipHeaderRecord);
/* 662*/    if (this.header != null) {
/* 663*/      sb.append(' ');
/* 664*/      sb.append("Header:").append(Arrays.toString((Object[])this.header));
/*   0*/    } 
/* 666*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void validate() throws IllegalArgumentException {
/* 675*/    if (this.quoteChar != null && this.delimiter == this.quoteChar) {
/* 676*/        throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + this.quoteChar + "')"); 
/*   0*/       }
/* 680*/    if (this.escape != null && this.delimiter == this.escape) {
/* 681*/        throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + this.escape + "')"); 
/*   0*/       }
/* 685*/    if (this.commentStart != null && this.delimiter == this.commentStart) {
/* 686*/        throw new IllegalArgumentException("The comment start character and the delimiter cannot be the same ('" + this.commentStart + "')"); 
/*   0*/       }
/* 690*/    if (this.quoteChar != null && this.quoteChar.equals(this.commentStart)) {
/* 691*/        throw new IllegalArgumentException("The comment start character and the quoteChar cannot be the same ('" + this.commentStart + "')"); 
/*   0*/       }
/* 695*/    if (this.escape != null && this.escape.equals(this.commentStart)) {
/* 696*/        throw new IllegalArgumentException("The comment start and the escape character cannot be the same ('" + this.commentStart + "')"); 
/*   0*/       }
/* 700*/    if (this.escape == null && this.quotePolicy == Quote.NONE) {
/* 701*/        throw new IllegalArgumentException("No quotes mode set but no escape character is set"); 
/*   0*/       }
/* 704*/    if (this.recordSeparator != null && !"\r\n".equals(this.recordSeparator) && !String.valueOf('\r').equals(this.recordSeparator) && !String.valueOf('\n').equals(this.recordSeparator)) {
/* 708*/        throw new IllegalArgumentException("Record separator can only by CR, LF or CRLF"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withCommentStart(char commentStart) {
/* 724*/    return withCommentStart(commentStart);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withCommentStart(Character commentStart) {
/* 739*/    if (isLineBreak(commentStart)) {
/* 740*/        throw new IllegalArgumentException("The comment start character cannot be a line break"); 
/*   0*/       }
/* 742*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withDelimiter(char delimiter) {
/* 757*/    if (isLineBreak(delimiter)) {
/* 758*/        throw new IllegalArgumentException("The delimiter cannot be a line break"); 
/*   0*/       }
/* 760*/    return new CSVFormat(delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withEscape(char escape) {
/* 775*/    return withEscape(escape);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withEscape(Character escape) {
/* 788*/    if (isLineBreak(escape)) {
/* 789*/        throw new IllegalArgumentException("The escape character cannot be a line break"); 
/*   0*/       }
/* 791*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeader(String... header) {
/* 814*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreEmptyHeaders(boolean ignoreEmptyHeaders) {
/* 828*/    return new CSVFormat(TDF.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, ignoreEmptyHeaders, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreEmptyLines(boolean ignoreEmptyLines) {
/* 842*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
/* 856*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withNullString(String nullString) {
/* 877*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuoteChar(char quoteChar) {
/* 892*/    return withQuoteChar(quoteChar);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuoteChar(Character quoteChar) {
/* 905*/    if (isLineBreak(quoteChar)) {
/* 906*/        throw new IllegalArgumentException("The quoteChar cannot be a line break"); 
/*   0*/       }
/* 908*/    return new CSVFormat(this.delimiter, quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuotePolicy(Quote quotePolicy) {
/* 922*/    return new CSVFormat(this.delimiter, this.quoteChar, quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withRecordSeparator(char recordSeparator) {
/* 940*/    return withRecordSeparator(String.valueOf(recordSeparator));
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withRecordSeparator(String recordSeparator) {
/* 956*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, recordSeparator, this.nullString, this.header, this.skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withSkipHeaderRecord(boolean skipHeaderRecord) {
/* 971*/    return new CSVFormat(this.delimiter, this.quoteChar, this.quotePolicy, this.commentStart, this.escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.header, skipHeaderRecord, this.ignoreEmptyHeaders);
/*   0*/  }
/*   0*/}
