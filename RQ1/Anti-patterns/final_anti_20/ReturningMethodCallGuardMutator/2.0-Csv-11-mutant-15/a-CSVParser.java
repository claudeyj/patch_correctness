/*   0*/package org.apache.commons.csv;
/*   0*/
/*   0*/import java.io.Closeable;
/*   0*/import java.io.File;
/*   0*/import java.io.FileInputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStreamReader;
/*   0*/import java.io.Reader;
/*   0*/import java.io.StringReader;
/*   0*/import java.net.URL;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.NoSuchElementException;
/*   0*/
/*   0*/public final class CSVParser implements Iterable<CSVRecord>, Closeable {
/*   0*/  private final CSVFormat format;
/*   0*/  
/*   0*/  private final Map<String, Integer> headerMap;
/*   0*/  
/*   0*/  private final Lexer lexer;
/*   0*/  
/*   0*/  public static CSVParser parse(File file, Charset charset, CSVFormat format) throws IOException {
/* 157*/    Assertions.notNull(file, "file");
/* 158*/    Assertions.notNull(format, "format");
/* 160*/    return new CSVParser(new InputStreamReader(new FileInputStream(file), charset), format);
/*   0*/  }
/*   0*/  
/*   0*/  public static CSVParser parse(String string, CSVFormat format) throws IOException {
/* 177*/    Assertions.notNull(string, "string");
/* 178*/    Assertions.notNull(format, "format");
/* 180*/    return new CSVParser(new StringReader(string), format);
/*   0*/  }
/*   0*/  
/*   0*/  public static CSVParser parse(URL url, Charset charset, CSVFormat format) throws IOException {
/* 204*/    Assertions.notNull(url, "url");
/* 205*/    Assertions.notNull(charset, "charset");
/* 206*/    Assertions.notNull(format, "format");
/* 208*/    return new CSVParser(new InputStreamReader(url.openStream(), charset), format);
/*   0*/  }
/*   0*/  
/* 221*/  private final List<String> record = new ArrayList<String>();
/*   0*/  
/*   0*/  private long recordNumber;
/*   0*/  
/* 225*/  private final Token reusableToken = new Token();
/*   0*/  
/*   0*/  public CSVParser(Reader reader, CSVFormat format) throws IOException {
/* 245*/    Assertions.notNull(reader, "reader");
/* 246*/    Assertions.notNull(format, "format");
/* 248*/    this.format = format;
/* 249*/    this.lexer = new Lexer(format, new ExtendedBufferedReader(reader));
/* 250*/    this.headerMap = initializeHeader();
/*   0*/  }
/*   0*/  
/*   0*/  private void addRecordValue() {
/* 254*/    String input = this.reusableToken.content.toString();
/* 255*/    String nullString = this.format.getNullString();
/* 256*/    if (nullString == null) {
/* 257*/      this.record.add(input);
/*   0*/    } else {
/* 259*/      this.record.add(input.equalsIgnoreCase(nullString) ? null : input);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 270*/    if (this.lexer != null) {
/* 271*/        this.lexer.close(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public long getCurrentLineNumber() {
/* 286*/    return this.lexer.getCurrentLineNumber();
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, Integer> getHeaderMap() {
/* 297*/    return (this.headerMap == null) ? null : new LinkedHashMap<String, Integer>(this.headerMap);
/*   0*/  }
/*   0*/  
/*   0*/  public long getRecordNumber() {
/* 311*/    return this.recordNumber;
/*   0*/  }
/*   0*/  
/*   0*/  public List<CSVRecord> getRecords() throws IOException {
/* 327*/    return getRecords(new ArrayList<CSVRecord>());
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends java.util.Collection<CSVRecord>> T getRecords(T records) throws IOException {
/*   0*/    CSVRecord rec;
/* 347*/    while ((rec = nextRecord()) != null) {
/* 348*/        records.add(rec); 
/*   0*/       }
/* 350*/    return records;
/*   0*/  }
/*   0*/  
/*   0*/  private Map<String, Integer> initializeHeader() throws IOException {
/* 360*/    Map<String, Integer> hdrMap = null;
/* 361*/    String[] formatHeader = this.format.getHeader();
/* 362*/    if (formatHeader != null) {
/* 363*/      hdrMap = new LinkedHashMap<String, Integer>();
/* 365*/      String[] headerRecord = null;
/* 366*/      if (formatHeader.length == 0) {
/* 368*/        CSVRecord nextRecord = nextRecord();
/* 369*/        if (nextRecord != null) {
/* 370*/            headerRecord = nextRecord.values(); 
/*   0*/           }
/*   0*/      } else {
/* 373*/        if (this.format.getSkipHeaderRecord()) {
/* 374*/            nextRecord(); 
/*   0*/           }
/* 376*/        headerRecord = formatHeader;
/*   0*/      } 
/* 380*/      if (headerRecord != null) {
/* 381*/          for (int i = 0; i < headerRecord.length; i++) {
/* 382*/            String header = headerRecord[i];
/* 383*/            boolean containsHeader = hdrMap.containsKey(header);
/* 384*/            boolean emptyHeader = header.trim().isEmpty();
/* 385*/            if (containsHeader && (!emptyHeader || (emptyHeader && !this.format.getIgnoreEmptyHeaders()))) {
/* 386*/                throw new IllegalArgumentException("The header contains a duplicate name: \"" + header + "\" in " + Arrays.toString(headerRecord)); 
/*   0*/               }
/* 389*/            hdrMap.put(header, i);
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 393*/    return hdrMap;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isClosed() {
/* 397*/    return this.lexer.isClosed();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<CSVRecord> iterator() {
/* 409*/    return new Iterator<CSVRecord>() {
/*   0*/        private CSVRecord current;
/*   0*/        
/*   0*/        private CSVRecord getNextRecord() {
/*   0*/          try {
/* 414*/            return CSVParser.this.nextRecord();
/* 415*/          } catch (IOException e) {
/* 417*/            throw new RuntimeException(e);
/*   0*/          } 
/*   0*/        }
/*   0*/        
/*   0*/        public boolean hasNext() {
/* 422*/          if (CSVParser.this.isClosed()) {
/* 423*/              return false; 
/*   0*/             }
/* 425*/          if (this.current == null) {
/* 426*/              this.current = getNextRecord(); 
/*   0*/             }
/* 429*/          return (this.current != null);
/*   0*/        }
/*   0*/        
/*   0*/        public CSVRecord next() {
/* 433*/          if (CSVParser.this.isClosed()) {
/* 434*/              throw new NoSuchElementException("CSVParser has been closed"); 
/*   0*/             }
/* 436*/          CSVRecord next = this.current;
/* 437*/          this.current = null;
/* 439*/          if (next == null) {
/* 441*/            next = getNextRecord();
/* 442*/            if (next == null) {
/* 443*/                throw new NoSuchElementException("No more CSV records available"); 
/*   0*/               }
/*   0*/          } 
/* 447*/          return next;
/*   0*/        }
/*   0*/        
/*   0*/        public void remove() {
/* 451*/          throw new UnsupportedOperationException();
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  CSVRecord nextRecord() throws IOException {
/* 464*/    CSVRecord result = null;
/* 465*/    this.record.clear();
/* 466*/    StringBuilder sb = null;
/*   0*/    do {
/* 468*/      this.reusableToken.reset();
/* 469*/      this.lexer.nextToken(this.reusableToken);
/* 470*/      switch (this.reusableToken.type) {
/*   0*/        case TOKEN:
/* 472*/          addRecordValue();
/*   0*/          break;
/*   0*/        case EORECORD:
/* 475*/          addRecordValue();
/*   0*/          break;
/*   0*/        case EOF:
/* 478*/          if (this.reusableToken.isReady) {
/* 479*/              addRecordValue(); 
/*   0*/             }
/*   0*/          break;
/*   0*/        case INVALID:
/* 483*/          throw new IOException("(line " + getCurrentLineNumber() + ") invalid parse sequence");
/*   0*/        case COMMENT:
/* 485*/          if (sb == null) {
/* 486*/            sb = new StringBuilder();
/*   0*/          } else {
/* 488*/            sb.append('\n');
/*   0*/          } 
/* 490*/          sb.append((CharSequence)this.reusableToken.content);
/* 491*/          this.reusableToken.type = Token.Type.TOKEN;
/*   0*/          break;
/*   0*/        default:
/* 494*/          throw new IllegalStateException("Unexpected Token type: " + this.reusableToken.type);
/*   0*/      } 
/* 496*/    } while (this.reusableToken.type == Token.Type.TOKEN);
/* 498*/    if (!this.record.isEmpty()) {
/* 499*/      this.recordNumber++;
/* 500*/      String comment = (sb == null) ? null : sb.toString();
/* 501*/      result = new CSVRecord(this.record.<String>toArray(new String[this.record.size()]), this.headerMap, comment, this.recordNumber);
/*   0*/    } 
/* 504*/    return result;
/*   0*/  }
/*   0*/}
