/*   0*/package org.apache.commons.csv;
/*   0*/
/*   0*/import java.io.File;
/*   0*/import java.io.FileOutputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.OutputStreamWriter;
/*   0*/import java.io.Reader;
/*   0*/import java.io.Serializable;
/*   0*/import java.io.StringWriter;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.nio.file.Files;
/*   0*/import java.nio.file.Path;
/*   0*/import java.sql.ResultSet;
/*   0*/import java.sql.ResultSetMetaData;
/*   0*/import java.sql.SQLException;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public final class CSVFormat implements Serializable {
/*   0*/  public enum Predefined {
/* 168*/    Default(CSVFormat.DEFAULT),
/* 173*/    Excel(CSVFormat.EXCEL),
/* 179*/    InformixUnload(CSVFormat.INFORMIX_UNLOAD),
/* 185*/    InformixUnloadCsv(CSVFormat.INFORMIX_UNLOAD_CSV),
/* 190*/    MySQL(CSVFormat.MYSQL),
/* 196*/    PostgreSQLCsv(CSVFormat.POSTGRESQL_CSV),
/* 201*/    PostgreSQLText(CSVFormat.POSTGRESQL_TEXT),
/* 206*/    RFC4180(CSVFormat.RFC4180),
/* 211*/    TDF(CSVFormat.TDF);
/*   0*/    
/*   0*/    private final CSVFormat format;
/*   0*/    
/*   0*/    Predefined(CSVFormat format) {
/* 216*/      this.format = format;
/*   0*/    }
/*   0*/    
/*   0*/    public CSVFormat getFormat() {
/* 225*/      return this.format;
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 244*/  public static final CSVFormat DEFAULT = new CSVFormat(',', Constants.DOUBLE_QUOTE_CHAR, null, null, null, false, true, "\r\n", null, null, null, false, false, false, false, false, false);
/*   0*/  
/* 277*/  public static final CSVFormat EXCEL = DEFAULT.withIgnoreEmptyLines(false).withAllowMissingColumnNames();
/*   0*/  
/* 307*/  public static final CSVFormat INFORMIX_UNLOAD = DEFAULT.withDelimiter('|').withEscape('\\').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
/*   0*/  
/* 338*/  public static final CSVFormat INFORMIX_UNLOAD_CSV = DEFAULT.withDelimiter(',').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
/*   0*/  
/* 370*/  public static final CSVFormat MYSQL = DEFAULT.withDelimiter('\t').withEscape('\\').withIgnoreEmptyLines(false).withQuote(null).withRecordSeparator('\n').withNullString("\\N").withQuoteMode(QuoteMode.ALL_NON_NULL);
/*   0*/  
/* 407*/  public static final CSVFormat POSTGRESQL_CSV = DEFAULT.withDelimiter(',').withEscape(Constants.DOUBLE_QUOTE_CHAR).withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n').withNullString("").withQuoteMode(QuoteMode.ALL_NON_NULL);
/*   0*/  
/* 444*/  public static final CSVFormat POSTGRESQL_TEXT = DEFAULT.withDelimiter('\t').withEscape(Constants.DOUBLE_QUOTE_CHAR).withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n').withNullString("\\N").withQuoteMode(QuoteMode.ALL_NON_NULL);
/*   0*/  
/* 469*/  public static final CSVFormat RFC4180 = DEFAULT.withIgnoreEmptyLines(false);
/*   0*/  
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/* 489*/  public static final CSVFormat TDF = DEFAULT.withDelimiter('\t').withIgnoreSurroundingSpaces();
/*   0*/  
/*   0*/  private final boolean allowMissingColumnNames;
/*   0*/  
/*   0*/  private final Character commentMarker;
/*   0*/  
/*   0*/  private final char delimiter;
/*   0*/  
/*   0*/  private final Character escapeCharacter;
/*   0*/  
/*   0*/  private final String[] header;
/*   0*/  
/*   0*/  private final String[] headerComments;
/*   0*/  
/*   0*/  private final boolean ignoreEmptyLines;
/*   0*/  
/*   0*/  private final boolean ignoreHeaderCase;
/*   0*/  
/*   0*/  private final boolean ignoreSurroundingSpaces;
/*   0*/  
/*   0*/  private final String nullString;
/*   0*/  
/*   0*/  private final Character quoteCharacter;
/*   0*/  
/*   0*/  private final QuoteMode quoteMode;
/*   0*/  
/*   0*/  private final String recordSeparator;
/*   0*/  
/*   0*/  private final boolean skipHeaderRecord;
/*   0*/  
/*   0*/  private final boolean trailingDelimiter;
/*   0*/  
/*   0*/  private final boolean trim;
/*   0*/  
/*   0*/  private final boolean autoFlush;
/*   0*/  
/*   0*/  private static boolean isLineBreak(char c) {
/* 503*/    return (c == '\n' || c == '\r');
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isLineBreak(Character c) {
/* 515*/    return (c != null && isLineBreak(c));
/*   0*/  }
/*   0*/  
/*   0*/  public static CSVFormat newFormat(char delimiter) {
/* 539*/    return new CSVFormat(delimiter, null, null, null, null, false, false, null, null, null, null, false, false, false, false, false, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static CSVFormat valueOf(String format) {
/* 552*/    return Predefined.valueOf(format).getFormat();
/*   0*/  }
/*   0*/  
/*   0*/  private CSVFormat(char delimiter, Character quoteChar, QuoteMode quoteMode, Character commentStart, Character escape, boolean ignoreSurroundingSpaces, boolean ignoreEmptyLines, String recordSeparator, String nullString, Object[] headerComments, String[] header, boolean skipHeaderRecord, boolean allowMissingColumnNames, boolean ignoreHeaderCase, boolean trim, boolean trailingDelimiter, boolean autoFlush) {
/* 634*/    this.delimiter = delimiter;
/* 635*/    this.quoteCharacter = quoteChar;
/* 636*/    this.quoteMode = quoteMode;
/* 637*/    this.commentMarker = commentStart;
/* 638*/    this.escapeCharacter = escape;
/* 639*/    this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
/* 640*/    this.allowMissingColumnNames = allowMissingColumnNames;
/* 641*/    this.ignoreEmptyLines = ignoreEmptyLines;
/* 642*/    this.recordSeparator = recordSeparator;
/* 643*/    this.nullString = nullString;
/* 644*/    this.headerComments = toStringArray(headerComments);
/* 645*/    this.header = (header == null) ? null : (String[])header.clone();
/* 646*/    this.skipHeaderRecord = skipHeaderRecord;
/* 647*/    this.ignoreHeaderCase = ignoreHeaderCase;
/* 648*/    this.trailingDelimiter = trailingDelimiter;
/* 649*/    this.trim = trim;
/* 650*/    this.autoFlush = autoFlush;
/* 651*/    validate();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 656*/    if (this == obj) {
/* 657*/        return true; 
/*   0*/       }
/* 659*/    if (obj == null) {
/* 660*/        return false; 
/*   0*/       }
/* 662*/    if (getClass() != obj.getClass()) {
/* 663*/        return false; 
/*   0*/       }
/* 666*/    CSVFormat other = (CSVFormat)obj;
/* 667*/    if (this.delimiter != other.delimiter) {
/* 668*/        return false; 
/*   0*/       }
/* 670*/    if (this.quoteMode != other.quoteMode) {
/* 671*/        return false; 
/*   0*/       }
/* 673*/    if (this.quoteCharacter == null) {
/* 674*/      if (other.quoteCharacter != null) {
/* 675*/          return false; 
/*   0*/         }
/* 677*/    } else if (!this.quoteCharacter.equals(other.quoteCharacter)) {
/* 678*/      return false;
/*   0*/    } 
/* 680*/    if (this.commentMarker == null) {
/* 681*/      if (other.commentMarker != null) {
/* 682*/          return false; 
/*   0*/         }
/* 684*/    } else if (!this.commentMarker.equals(other.commentMarker)) {
/* 685*/      return false;
/*   0*/    } 
/* 687*/    if (this.escapeCharacter == null) {
/* 688*/      if (other.escapeCharacter != null) {
/* 689*/          return false; 
/*   0*/         }
/* 691*/    } else if (!this.escapeCharacter.equals(other.escapeCharacter)) {
/* 692*/      return false;
/*   0*/    } 
/* 694*/    if (this.nullString == null) {
/* 695*/      if (other.nullString != null) {
/* 696*/          return false; 
/*   0*/         }
/* 698*/    } else if (!this.nullString.equals(other.nullString)) {
/* 699*/      return false;
/*   0*/    } 
/* 701*/    if (!Arrays.equals((Object[])this.header, (Object[])other.header)) {
/* 702*/        return false; 
/*   0*/       }
/* 704*/    if (this.ignoreSurroundingSpaces != other.ignoreSurroundingSpaces) {
/* 705*/        return false; 
/*   0*/       }
/* 707*/    if (this.ignoreEmptyLines != other.ignoreEmptyLines) {
/* 708*/        return false; 
/*   0*/       }
/* 710*/    if (this.skipHeaderRecord != other.skipHeaderRecord) {
/* 711*/        return false; 
/*   0*/       }
/* 713*/    if (this.recordSeparator == null) {
/* 714*/      if (other.recordSeparator != null) {
/* 715*/          return false; 
/*   0*/         }
/* 717*/    } else if (!this.recordSeparator.equals(other.recordSeparator)) {
/* 718*/      return false;
/*   0*/    } 
/* 720*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String format(Object... values) {
/* 731*/    StringWriter out = new StringWriter();
/* 732*/    try (CSVPrinter csvPrinter = new CSVPrinter(out, this)) {
/* 733*/      csvPrinter.printRecord(values);
/* 734*/      return out.toString().trim();
/* 735*/    } catch (IOException e) {
/* 737*/      throw new IllegalStateException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getAllowMissingColumnNames() {
/* 748*/    return this.allowMissingColumnNames;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getCommentMarker() {
/* 757*/    return this.commentMarker;
/*   0*/  }
/*   0*/  
/*   0*/  public char getDelimiter() {
/* 766*/    return this.delimiter;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getEscapeCharacter() {
/* 775*/    return this.escapeCharacter;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] getHeader() {
/* 784*/    return (this.header != null) ? (String[])this.header.clone() : null;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] getHeaderComments() {
/* 793*/    return (this.headerComments != null) ? (String[])this.headerComments.clone() : null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreEmptyLines() {
/* 803*/    return this.ignoreEmptyLines;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreHeaderCase() {
/* 813*/    return this.ignoreHeaderCase;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getIgnoreSurroundingSpaces() {
/* 822*/    return this.ignoreSurroundingSpaces;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNullString() {
/* 836*/    return this.nullString;
/*   0*/  }
/*   0*/  
/*   0*/  public Character getQuoteCharacter() {
/* 845*/    return this.quoteCharacter;
/*   0*/  }
/*   0*/  
/*   0*/  public QuoteMode getQuoteMode() {
/* 854*/    return this.quoteMode;
/*   0*/  }
/*   0*/  
/*   0*/  public String getRecordSeparator() {
/* 863*/    return this.recordSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getSkipHeaderRecord() {
/* 872*/    return this.skipHeaderRecord;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getTrailingDelimiter() {
/* 882*/    return this.trailingDelimiter;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getTrim() {
/* 891*/    return this.trim;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getAutoFlush() {
/* 901*/    return this.autoFlush;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 906*/    int prime = 31;
/* 907*/    int result = 1;
/* 909*/    result = 31 * result + this.delimiter;
/* 910*/    result = 31 * result + ((this.quoteMode == null) ? 0 : this.quoteMode.hashCode());
/* 911*/    result = 31 * result + ((this.quoteCharacter == null) ? 0 : this.quoteCharacter.hashCode());
/* 912*/    result = 31 * result + ((this.commentMarker == null) ? 0 : this.commentMarker.hashCode());
/* 913*/    result = 31 * result + ((this.escapeCharacter == null) ? 0 : this.escapeCharacter.hashCode());
/* 914*/    result = 31 * result + ((this.nullString == null) ? 0 : this.nullString.hashCode());
/* 915*/    result = 31 * result + (this.ignoreSurroundingSpaces ? 1231 : 1237);
/* 916*/    result = 31 * result + (this.ignoreHeaderCase ? 1231 : 1237);
/* 917*/    result = 31 * result + (this.ignoreEmptyLines ? 1231 : 1237);
/* 918*/    result = 31 * result + (this.skipHeaderRecord ? 1231 : 1237);
/* 919*/    result = 31 * result + ((this.recordSeparator == null) ? 0 : this.recordSeparator.hashCode());
/* 920*/    result = 31 * result + Arrays.hashCode((Object[])this.header);
/* 921*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCommentMarkerSet() {
/* 932*/    return (this.commentMarker != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEscapeCharacterSet() {
/* 941*/    return (this.escapeCharacter != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNullStringSet() {
/* 950*/    return (this.nullString != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isQuoteCharacterSet() {
/* 959*/    return (this.quoteCharacter != null);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVParser parse(Reader in) throws IOException {
/* 976*/    return new CSVParser(in, this);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVPrinter print(Appendable out) throws IOException {
/* 993*/    return new CSVPrinter(out, this);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVPrinter printer() throws IOException {
/*1009*/    return new CSVPrinter(System.out, this);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVPrinter print(File out, Charset charset) throws IOException {
/*1031*/    return new CSVPrinter(new OutputStreamWriter(new FileOutputStream(out), charset), this);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVPrinter print(Path out, Charset charset) throws IOException {
/*1051*/    return print(Files.newBufferedWriter(out, charset, new java.nio.file.OpenOption[0]));
/*   0*/  }
/*   0*/  
/*   0*/  public void print(Object value, Appendable out, boolean newRecord) throws IOException {
/*1072*/    if (value == null) {
/*1074*/      if (null == this.nullString) {
/*1075*/        charSequence = "";
/*1077*/      } else if (QuoteMode.ALL == this.quoteMode) {
/*1078*/        charSequence = this.quoteCharacter + this.nullString + this.quoteCharacter;
/*   0*/      } else {
/*1080*/        charSequence = this.nullString;
/*   0*/      } 
/*   0*/    } else {
/*1084*/      charSequence = (value instanceof CharSequence) ? (CharSequence)value : value.toString();
/*   0*/    } 
/*1086*/    CharSequence charSequence = getTrim() ? trim(charSequence) : charSequence;
/*1087*/    print(value, charSequence, 0, charSequence.length(), out, newRecord);
/*   0*/  }
/*   0*/  
/*   0*/  private void print(Object object, CharSequence value, int offset, int len, Appendable out, boolean newRecord) throws IOException {
/*1092*/    if (!newRecord) {
/*1093*/        out.append(getDelimiter()); 
/*   0*/       }
/*1095*/    if (object == null) {
/*1096*/      out.append(value);
/*1097*/    } else if (isQuoteCharacterSet()) {
/*1099*/      printAndQuote(object, value, offset, len, out, newRecord);
/*1100*/    } else if (isEscapeCharacterSet()) {
/*1101*/      printAndEscape(value, offset, len, out);
/*   0*/    } else {
/*1103*/      out.append(value, offset, offset + len);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void printAndEscape(CharSequence value, int offset, int len, Appendable out) throws IOException {
/*1112*/    int start = offset;
/*1113*/    int pos = offset;
/*1114*/    int end = offset + len;
/*1116*/    char delim = getDelimiter();
/*1117*/    char escape = getEscapeCharacter();
/*1119*/    while (pos < end) {
/*1120*/      char c = value.charAt(pos);
/*1121*/      if (c == '\r' || c == '\n' || c == delim || c == escape) {
/*1123*/        if (pos > start) {
/*1124*/            out.append(value, start, pos); 
/*   0*/           }
/*1126*/        if (c == '\n') {
/*1127*/          c = 'n';
/*1128*/        } else if (c == '\r') {
/*1129*/          c = 'r';
/*   0*/        } 
/*1132*/        out.append(escape);
/*1133*/        out.append(c);
/*1135*/        start = pos + 1;
/*   0*/      } 
/*1138*/      pos++;
/*   0*/    } 
/*1142*/    if (pos > start) {
/*1143*/        out.append(value, start, pos); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void printAndQuote(Object object, CharSequence value, int offset, int len, Appendable out, boolean newRecord) throws IOException {
/*   0*/    boolean quote = false;
/*1154*/    int start = offset;
/*1155*/    int pos = offset;
/*1156*/    int end = offset + len;
/*1158*/    char delimChar = getDelimiter();
/*1159*/    char quoteChar = getQuoteCharacter();
/*1161*/    QuoteMode quoteModePolicy = getQuoteMode();
/*1162*/    if (quoteModePolicy == null) {
/*1163*/        quoteModePolicy = QuoteMode.MINIMAL; 
/*   0*/       }
/*1165*/    switch (quoteModePolicy) {
/*   0*/      case ALL:
/*   0*/      case ALL_NON_NULL:
/*1168*/        quote = true;
/*   0*/        break;
/*   0*/      case NON_NUMERIC:
/*1171*/        quote = !(object instanceof Number);
/*   0*/        break;
/*   0*/      case NONE:
/*1175*/        printAndEscape(value, offset, len, out);
/*   0*/        return;
/*   0*/      case MINIMAL:
/*1178*/        if (len <= 0) {
/*1183*/          if (newRecord) {
/*1184*/              quote = true; 
/*   0*/             }
/*   0*/        } else {
/*1187*/          char c = value.charAt(pos);
/*1189*/          if (this.skipHeaderRecord && (c < ' ' || (c > '!' && c < '#') || (c > '+' && c < '-') || c > '~')) {
/*1190*/            quote = true;
/*1191*/          } else if (c <= '#') {
/*1195*/            quote = true;
/*   0*/          } else {
/*1197*/            while (pos < end) {
/*1198*/              c = value.charAt(pos);
/*1199*/              if (c == '\n' || c == '\r' || c == quoteChar || c == delimChar) {
/*1200*/                quote = true;
/*   0*/                break;
/*   0*/              } 
/*1203*/              pos++;
/*   0*/            } 
/*1206*/            if (!quote) {
/*1207*/              pos = end - 1;
/*1208*/              c = value.charAt(pos);
/*1211*/              if (c <= ' ') {
/*1212*/                  quote = true; 
/*   0*/                 }
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*1218*/        if (!quote) {
/*1220*/          out.append(value, start, end);
/*   0*/          return;
/*   0*/        } 
/*   0*/        break;
/*   0*/      default:
/*1225*/        throw new IllegalStateException("Unexpected Quote value: " + quoteModePolicy);
/*   0*/    } 
/*1228*/    if (!quote) {
/*1230*/      out.append(value, start, end);
/*   0*/      return;
/*   0*/    } 
/*1235*/    out.append(quoteChar);
/*1239*/    while (pos < end) {
/*1240*/      char c = value.charAt(pos);
/*1241*/      if (c == quoteChar) {
/*1245*/        out.append(value, start, pos + 1);
/*1248*/        start = pos;
/*   0*/      } 
/*1250*/      pos++;
/*   0*/    } 
/*1254*/    out.append(value, start, pos);
/*1255*/    out.append(quoteChar);
/*   0*/  }
/*   0*/  
/*   0*/  public void println(Appendable out) throws IOException {
/*1268*/    if (getTrailingDelimiter()) {
/*1269*/        out.append(getDelimiter()); 
/*   0*/       }
/*1271*/    if (this.recordSeparator != null) {
/*1272*/        out.append(this.recordSeparator); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printRecord(Appendable out, Object... values) throws IOException {
/*1294*/    for (int i = 0; i < values.length; i++) {
/*1295*/        print(values[i], out, (i == 0)); 
/*   0*/       }
/*1297*/    println(out);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1302*/    StringBuilder sb = new StringBuilder();
/*1303*/    sb.append("Delimiter=<").append(this.delimiter).append('>');
/*1304*/    if (isEscapeCharacterSet()) {
/*1305*/      sb.append(' ');
/*1306*/      sb.append("Escape=<").append(this.escapeCharacter).append('>');
/*   0*/    } 
/*1308*/    if (isQuoteCharacterSet()) {
/*1309*/      sb.append(' ');
/*1310*/      sb.append("QuoteChar=<").append(this.quoteCharacter).append('>');
/*   0*/    } 
/*1312*/    if (isCommentMarkerSet()) {
/*1313*/      sb.append(' ');
/*1314*/      sb.append("CommentStart=<").append(this.commentMarker).append('>');
/*   0*/    } 
/*1316*/    if (isNullStringSet()) {
/*1317*/      sb.append(' ');
/*1318*/      sb.append("NullString=<").append(this.nullString).append('>');
/*   0*/    } 
/*1320*/    if (this.recordSeparator != null) {
/*1321*/      sb.append(' ');
/*1322*/      sb.append("RecordSeparator=<").append(this.recordSeparator).append('>');
/*   0*/    } 
/*1324*/    if (getIgnoreEmptyLines()) {
/*1325*/        sb.append(" EmptyLines:ignored"); 
/*   0*/       }
/*1327*/    if (getIgnoreSurroundingSpaces()) {
/*1328*/        sb.append(" SurroundingSpaces:ignored"); 
/*   0*/       }
/*1330*/    if (getIgnoreHeaderCase()) {
/*1331*/        sb.append(" IgnoreHeaderCase:ignored"); 
/*   0*/       }
/*1333*/    sb.append(" SkipHeaderRecord:").append(this.skipHeaderRecord);
/*1334*/    if (this.headerComments != null) {
/*1335*/      sb.append(' ');
/*1336*/      sb.append("HeaderComments:").append(Arrays.toString((Object[])this.headerComments));
/*   0*/    } 
/*1338*/    if (this.header != null) {
/*1339*/      sb.append(' ');
/*1340*/      sb.append("Header:").append(Arrays.toString((Object[])this.header));
/*   0*/    } 
/*1342*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private String[] toStringArray(Object[] values) {
/*1346*/    if (values == null) {
/*1347*/        return null; 
/*   0*/       }
/*1349*/    String[] strings = new String[values.length];
/*1350*/    for (int i = 0; i < values.length; i++) {
/*1351*/      Object value = values[i];
/*1352*/      strings[i] = (value == null) ? null : value.toString();
/*   0*/    } 
/*1354*/    return strings;
/*   0*/  }
/*   0*/  
/*   0*/  private CharSequence trim(CharSequence charSequence) {
/*1358*/    if (charSequence instanceof String) {
/*1359*/        return ((String)charSequence).trim(); 
/*   0*/       }
/*1361*/    int count = charSequence.length();
/*1362*/    int len = count;
/*1363*/    int pos = 0;
/*1365*/    while (pos < len && charSequence.charAt(pos) <= ' ') {
/*1366*/        pos++; 
/*   0*/       }
/*1368*/    while (pos < len && charSequence.charAt(len - 1) <= ' ') {
/*1369*/        len--; 
/*   0*/       }
/*1371*/    return (pos > 0 || len < count) ? charSequence.subSequence(pos, len) : charSequence;
/*   0*/  }
/*   0*/  
/*   0*/  private void validate() throws IllegalArgumentException {
/*1380*/    if (isLineBreak(this.delimiter)) {
/*1381*/        throw new IllegalArgumentException("The delimiter cannot be a line break"); 
/*   0*/       }
/*1384*/    if (this.quoteCharacter != null && this.delimiter == this.quoteCharacter) {
/*1385*/        throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + this.quoteCharacter + "')"); 
/*   0*/       }
/*1389*/    if (this.escapeCharacter != null && this.delimiter == this.escapeCharacter) {
/*1390*/        throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + this.escapeCharacter + "')"); 
/*   0*/       }
/*1394*/    if (this.commentMarker != null && this.delimiter == this.commentMarker) {
/*1395*/        throw new IllegalArgumentException("The comment start character and the delimiter cannot be the same ('" + this.commentMarker + "')"); 
/*   0*/       }
/*1399*/    if (this.quoteCharacter != null && this.quoteCharacter.equals(this.commentMarker)) {
/*1400*/        throw new IllegalArgumentException("The comment start character and the quoteChar cannot be the same ('" + this.commentMarker + "')"); 
/*   0*/       }
/*1404*/    if (this.escapeCharacter != null && this.escapeCharacter.equals(this.commentMarker)) {
/*1405*/        throw new IllegalArgumentException("The comment start and the escape character cannot be the same ('" + this.commentMarker + "')"); 
/*   0*/       }
/*1409*/    if (this.escapeCharacter == null && this.quoteMode == QuoteMode.NONE) {
/*1410*/        throw new IllegalArgumentException("No quotes mode set but no escape character is set"); 
/*   0*/       }
/*1414*/    if (this.header != null) {
/*1415*/      Set<String> dupCheck = new HashSet<>();
/*1416*/      for (String hdr : this.header) {
/*1417*/        if (!dupCheck.add(hdr)) {
/*1418*/            throw new IllegalArgumentException("The header contains a duplicate entry: '" + hdr + "' in " + Arrays.toString(this.header)); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withAllowMissingColumnNames() {
/*1433*/    return withAllowMissingColumnNames(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withAllowMissingColumnNames(boolean allowMissingColumnNames) {
/*1445*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withCommentMarker(char commentMarker) {
/*1462*/    return withCommentMarker(commentMarker);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withCommentMarker(Character commentMarker) {
/*1477*/    if (isLineBreak(commentMarker)) {
/*1478*/        throw new IllegalArgumentException("The comment start marker character cannot be a line break"); 
/*   0*/       }
/*1480*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withDelimiter(char delimiter) {
/*1495*/    if (isLineBreak(delimiter)) {
/*1496*/        throw new IllegalArgumentException("The delimiter cannot be a line break"); 
/*   0*/       }
/*1498*/    return new CSVFormat(delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withEscape(char escape) {
/*1513*/    return withEscape(escape);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withEscape(Character escape) {
/*1526*/    if (isLineBreak(escape)) {
/*1527*/        throw new IllegalArgumentException("The escape character cannot be a line break"); 
/*   0*/       }
/*1529*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withFirstRecordAsHeader() {
/*1551*/    return withHeader(new String[0]).withSkipHeaderRecord();
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeader(Class<? extends Enum<?>> headerEnum) {
/*1581*/    String[] header = null;
/*1582*/    if (headerEnum != null) {
/*1583*/      Enum[] arrayOfEnum = (Enum[])headerEnum.getEnumConstants();
/*1584*/      header = new String[arrayOfEnum.length];
/*1585*/      for (int i = 0; i < arrayOfEnum.length; i++) {
/*1586*/          header[i] = arrayOfEnum[i].name(); 
/*   0*/         }
/*   0*/    } 
/*1589*/    return withHeader(header);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeader(ResultSet resultSet) throws SQLException {
/*1619*/    return withHeader((resultSet != null) ? resultSet.getMetaData() : null);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeader(ResultSetMetaData metaData) throws SQLException {
/*1649*/    String[] labels = null;
/*1650*/    if (metaData != null) {
/*1651*/      int columnCount = metaData.getColumnCount();
/*1652*/      labels = new String[columnCount];
/*1653*/      for (int i = 0; i < columnCount; i++) {
/*1654*/          labels[i] = metaData.getColumnLabel(i + 1); 
/*   0*/         }
/*   0*/    } 
/*1657*/    return withHeader(labels);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeader(String... header) {
/*1684*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withHeaderComments(Object... headerComments) {
/*1705*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreEmptyLines() {
/*1718*/    return withIgnoreEmptyLines(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreEmptyLines(boolean ignoreEmptyLines) {
/*1730*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreHeaderCase() {
/*1743*/    return withIgnoreHeaderCase(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreHeaderCase(boolean ignoreHeaderCase) {
/*1756*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreSurroundingSpaces() {
/*1769*/    return withIgnoreSurroundingSpaces(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
/*1781*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withNullString(String nullString) {
/*1800*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuote(char quoteChar) {
/*1815*/    return withQuote(quoteChar);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuote(Character quoteChar) {
/*1828*/    if (isLineBreak(quoteChar)) {
/*1829*/        throw new IllegalArgumentException("The quoteChar cannot be a line break"); 
/*   0*/       }
/*1831*/    return new CSVFormat(this.delimiter, quoteChar, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withQuoteMode(QuoteMode quoteModePolicy) {
/*1845*/    return new CSVFormat(this.delimiter, this.quoteCharacter, quoteModePolicy, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withRecordSeparator(char recordSeparator) {
/*1864*/    return withRecordSeparator(String.valueOf(recordSeparator));
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withRecordSeparator(String recordSeparator) {
/*1883*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withSkipHeaderRecord() {
/*1897*/    return withSkipHeaderRecord(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withSkipHeaderRecord(boolean skipHeaderRecord) {
/*1910*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withTrailingDelimiter() {
/*1922*/    return withTrailingDelimiter(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withTrailingDelimiter(boolean trailingDelimiter) {
/*1935*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withTrim() {
/*1947*/    return withTrim(true);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withTrim(boolean trim) {
/*1960*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, trim, this.trailingDelimiter, this.autoFlush);
/*   0*/  }
/*   0*/  
/*   0*/  public CSVFormat withAutoFlush(boolean autoFlush) {
/*1975*/    return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, (Object[])this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter, autoFlush);
/*   0*/  }
/*   0*/}
