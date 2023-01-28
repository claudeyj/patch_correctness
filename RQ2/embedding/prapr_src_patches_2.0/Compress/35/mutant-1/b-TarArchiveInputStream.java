/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/import java.io.ByteArrayOutputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Map;
/*   0*/import org.apache.commons.compress.archivers.ArchiveEntry;
/*   0*/import org.apache.commons.compress.archivers.ArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*   0*/import org.apache.commons.compress.utils.ArchiveUtils;
/*   0*/import org.apache.commons.compress.utils.IOUtils;
/*   0*/
/*   0*/public class TarArchiveInputStream extends ArchiveInputStream {
/*   0*/  private static final int SMALL_BUFFER_SIZE = 256;
/*   0*/  
/*  52*/  private final byte[] SMALL_BUF = new byte[256];
/*   0*/  
/*   0*/  private final int recordSize;
/*   0*/  
/*   0*/  private final int blockSize;
/*   0*/  
/*   0*/  private boolean hasHitEOF;
/*   0*/  
/*   0*/  private long entrySize;
/*   0*/  
/*   0*/  private long entryOffset;
/*   0*/  
/*   0*/  private final InputStream is;
/*   0*/  
/*   0*/  private TarArchiveEntry currEntry;
/*   0*/  
/*   0*/  private final ZipEncoding zipEncoding;
/*   0*/  
/*   0*/  final String encoding;
/*   0*/  
/*   0*/  public TarArchiveInputStream(InputStream is) {
/*  86*/    this(is, 10240, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveInputStream(InputStream is, String encoding) {
/*  96*/    this(is, 10240, 512, encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveInputStream(InputStream is, int blockSize) {
/* 106*/    this(is, blockSize, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveInputStream(InputStream is, int blockSize, String encoding) {
/* 118*/    this(is, blockSize, 512, encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveInputStream(InputStream is, int blockSize, int recordSize) {
/* 128*/    this(is, blockSize, recordSize, null);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding) {
/* 141*/    this.is = is;
/* 142*/    this.hasHitEOF = false;
/* 143*/    this.encoding = encoding;
/* 144*/    this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/* 145*/    this.recordSize = recordSize;
/* 146*/    this.blockSize = blockSize;
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 155*/    this.is.close();
/*   0*/  }
/*   0*/  
/*   0*/  public int getRecordSize() {
/* 164*/    return this.recordSize;
/*   0*/  }
/*   0*/  
/*   0*/  public int available() throws IOException {
/* 181*/    if (isDirectory()) {
/* 182*/        return 0; 
/*   0*/       }
/* 184*/    if (this.entrySize - this.entryOffset > 2147483647L) {
/* 185*/        return Integer.MAX_VALUE; 
/*   0*/       }
/* 187*/    return (int)(this.entrySize - this.entryOffset);
/*   0*/  }
/*   0*/  
/*   0*/  public long skip(long n) throws IOException {
/* 209*/    if (n <= 0L || isDirectory()) {
/* 210*/        return 0L; 
/*   0*/       }
/* 213*/    long available = this.entrySize - this.entryOffset;
/* 214*/    long skipped = this.is.skip(Math.min(n, available));
/* 215*/    count(skipped);
/* 216*/    this.entryOffset += skipped;
/* 217*/    return skipped;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean markSupported() {
/* 227*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public void mark(int markLimit) {}
/*   0*/  
/*   0*/  public synchronized void reset() {}
/*   0*/  
/*   0*/  public TarArchiveEntry getNextTarEntry() throws IOException {
/* 260*/    if (this.hasHitEOF) {
/* 261*/        return null; 
/*   0*/       }
/* 264*/    if (this.currEntry != null) {
/* 266*/      IOUtils.skip(this, Long.MAX_VALUE);
/* 269*/      skipRecordPadding();
/*   0*/    } 
/* 272*/    byte[] headerBuf = getRecord();
/* 274*/    if (headerBuf == null) {
/* 276*/      this.currEntry = null;
/* 277*/      return null;
/*   0*/    } 
/*   0*/    try {
/* 281*/      this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding);
/* 282*/    } catch (IllegalArgumentException e) {
/* 283*/      IOException ioe = new IOException("Error detected parsing the header");
/* 284*/      ioe.initCause(e);
/* 285*/      throw ioe;
/*   0*/    } 
/* 288*/    this.entryOffset = 0L;
/* 289*/    this.entrySize = this.currEntry.getSize();
/* 291*/    if (this.currEntry.isGNULongLinkEntry()) {
/* 292*/      byte[] longLinkData = getLongNameData();
/* 293*/      if (longLinkData == null) {
/* 297*/          return null; 
/*   0*/         }
/* 299*/      this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
/*   0*/    } 
/* 302*/    if (this.currEntry.isGNULongNameEntry()) {
/* 303*/      byte[] longNameData = getLongNameData();
/* 304*/      if (longNameData == null) {
/* 308*/          return null; 
/*   0*/         }
/* 310*/      this.currEntry.setName(this.zipEncoding.decode(longNameData));
/*   0*/    } 
/* 313*/    if (this.currEntry.isPaxHeader()) {
/* 314*/        paxHeaders(); 
/*   0*/       }
/* 317*/    if (this.currEntry.isGNUSparse()) {
/* 318*/        readGNUSparse(); 
/*   0*/       }
/* 325*/    this.entrySize = this.currEntry.getSize();
/* 327*/    return this.currEntry;
/*   0*/  }
/*   0*/  
/*   0*/  private void skipRecordPadding() throws IOException {
/* 335*/    if (!isDirectory() && this.entrySize > 0L && this.entrySize % this.recordSize != 0L) {
/* 336*/      long numRecords = this.entrySize / this.recordSize + 1L;
/* 337*/      long padding = numRecords * this.recordSize - this.entrySize;
/* 338*/      long skipped = IOUtils.skip(this.is, padding);
/* 339*/      count(skipped);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected byte[] getLongNameData() throws IOException {
/* 351*/    ByteArrayOutputStream longName = new ByteArrayOutputStream();
/* 352*/    int length = 0;
/* 353*/    while ((length = read(this.SMALL_BUF)) >= 0) {
/* 354*/        longName.write(this.SMALL_BUF, 0, length); 
/*   0*/       }
/* 356*/    getNextEntry();
/* 357*/    if (this.currEntry == null) {
/* 360*/        return null; 
/*   0*/       }
/* 362*/    byte[] longNameData = longName.toByteArray();
/* 364*/    length = longNameData.length;
/* 365*/    while (length > 0 && longNameData[length - 1] == 0) {
/* 366*/        length--; 
/*   0*/       }
/* 368*/    if (length != longNameData.length) {
/* 369*/      byte[] l = new byte[length];
/* 370*/      System.arraycopy(longNameData, 0, l, 0, length);
/* 371*/      longNameData = l;
/*   0*/    } 
/* 373*/    return longNameData;
/*   0*/  }
/*   0*/  
/*   0*/  private byte[] getRecord() throws IOException {
/* 391*/    byte[] headerBuf = readRecord();
/* 392*/    this.hasHitEOF = isEOFRecord(headerBuf);
/* 393*/    if (this.hasHitEOF && headerBuf != null) {
/* 394*/      tryToConsumeSecondEOFRecord();
/* 395*/      consumeRemainderOfLastBlock();
/* 396*/      headerBuf = null;
/*   0*/    } 
/* 398*/    return headerBuf;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isEOFRecord(byte[] record) {
/* 409*/    return (record == null || ArchiveUtils.isArrayZero(record, this.recordSize));
/*   0*/  }
/*   0*/  
/*   0*/  protected byte[] readRecord() throws IOException {
/* 420*/    byte[] record = new byte[this.recordSize];
/* 422*/    int readNow = IOUtils.readFully(this.is, record);
/* 423*/    count(readNow);
/* 424*/    if (readNow != this.recordSize) {
/* 425*/        return null; 
/*   0*/       }
/* 428*/    return record;
/*   0*/  }
/*   0*/  
/*   0*/  private void paxHeaders() throws IOException {
/* 432*/    Map<String, String> headers = parsePaxHeaders(this);
/* 433*/    getNextEntry();
/* 434*/    applyPaxHeadersToCurrentEntry(headers);
/*   0*/  }
/*   0*/  
/*   0*/  Map<String, String> parsePaxHeaders(InputStream i) throws IOException {
/*   0*/    int ch;
/* 438*/    Map<String, String> headers = new HashMap<String, String>();
/*   0*/    do {
/* 442*/      int len = 0;
/* 443*/      int read = 0;
/* 444*/      while ((ch = i.read()) != -1) {
/* 445*/        read++;
/* 446*/        if (ch == 32) {
/* 448*/          ByteArrayOutputStream coll = new ByteArrayOutputStream();
/* 449*/          while ((ch = i.read()) != -1) {
/* 450*/            read++;
/* 451*/            if (ch == 61) {
/* 452*/              String keyword = coll.toString("UTF-8");
/* 454*/              int restLen = len - read;
/* 455*/              byte[] rest = new byte[restLen];
/* 456*/              int got = IOUtils.readFully(i, rest);
/* 457*/              if (got != restLen) {
/* 458*/                  throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got); 
/*   0*/                 }
/* 465*/              String value = new String(rest, 0, restLen - 1, "UTF-8");
/* 467*/              headers.put(keyword, value);
/*   0*/              break;
/*   0*/            } 
/* 470*/            coll.write((byte)ch);
/*   0*/          } 
/*   0*/          break;
/*   0*/        } 
/* 474*/        len *= 10;
/* 475*/        len += ch - 48;
/*   0*/      } 
/* 477*/    } while (ch != -1);
/* 481*/    return headers;
/*   0*/  }
/*   0*/  
/*   0*/  private void applyPaxHeadersToCurrentEntry(Map<String, String> headers) {
/* 496*/    for (Map.Entry<String, String> ent : headers.entrySet()) {
/* 497*/      String key = ent.getKey();
/* 498*/      String val = ent.getValue();
/* 499*/      if ("path".equals(key)) {
/* 500*/        this.currEntry.setName(val);
/*   0*/        continue;
/*   0*/      } 
/* 501*/      if ("linkpath".equals(key)) {
/* 502*/        this.currEntry.setLinkName(val);
/*   0*/        continue;
/*   0*/      } 
/* 503*/      if ("gid".equals(key)) {
/* 504*/        this.currEntry.setGroupId(Long.parseLong(val));
/*   0*/        continue;
/*   0*/      } 
/* 505*/      if ("gname".equals(key)) {
/* 506*/        this.currEntry.setGroupName(val);
/*   0*/        continue;
/*   0*/      } 
/* 507*/      if ("uid".equals(key)) {
/* 508*/        this.currEntry.setUserId(Long.parseLong(val));
/*   0*/        continue;
/*   0*/      } 
/* 509*/      if ("uname".equals(key)) {
/* 510*/        this.currEntry.setUserName(val);
/*   0*/        continue;
/*   0*/      } 
/* 511*/      if ("size".equals(key)) {
/* 512*/        this.currEntry.setSize(Long.parseLong(val));
/*   0*/        continue;
/*   0*/      } 
/* 513*/      if ("mtime".equals(key)) {
/* 514*/        this.currEntry.setModTime((long)(Double.parseDouble(val) * 1000.0D));
/*   0*/        continue;
/*   0*/      } 
/* 515*/      if ("SCHILY.devminor".equals(key)) {
/* 516*/        this.currEntry.setDevMinor(Integer.parseInt(val));
/*   0*/        continue;
/*   0*/      } 
/* 517*/      if ("SCHILY.devmajor".equals(key)) {
/* 518*/          this.currEntry.setDevMajor(Integer.parseInt(val)); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void readGNUSparse() throws IOException {
/* 536*/    if (this.currEntry.isExtended()) {
/*   0*/      TarArchiveSparseEntry entry;
/*   0*/      do {
/* 539*/        byte[] headerBuf = getRecord();
/* 540*/        if (headerBuf == null) {
/* 541*/          this.currEntry = null;
/*   0*/          break;
/*   0*/        } 
/* 544*/        entry = new TarArchiveSparseEntry(headerBuf);
/* 548*/      } while (entry.isExtended());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean isDirectory() {
/* 553*/    return (this.currEntry != null && this.currEntry.isDirectory());
/*   0*/  }
/*   0*/  
/*   0*/  public ArchiveEntry getNextEntry() throws IOException {
/* 565*/    return getNextTarEntry();
/*   0*/  }
/*   0*/  
/*   0*/  private void tryToConsumeSecondEOFRecord() throws IOException {
/*   0*/    boolean shouldReset = true;
/* 580*/    boolean marked = this.is.markSupported();
/* 581*/    if (marked) {
/* 582*/        this.is.mark(this.recordSize); 
/*   0*/       }
/*   0*/    try {
/* 585*/      shouldReset = !isEOFRecord(readRecord());
/*   0*/    } finally {
/* 587*/      if (shouldReset && marked) {
/* 588*/        pushedBackBytes(this.recordSize);
/* 589*/        this.is.reset();
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int read(byte[] buf, int offset, int numToRead) throws IOException {
/* 609*/    int totalRead = 0;
/* 611*/    if (this.hasHitEOF || isDirectory() || this.entryOffset >= this.entrySize) {
/* 612*/        return -1; 
/*   0*/       }
/* 615*/    if (this.currEntry == null) {
/* 616*/        throw new IllegalStateException("No current tar entry"); 
/*   0*/       }
/* 619*/    numToRead = Math.min(numToRead, available());
/* 621*/    totalRead = this.is.read(buf, offset, numToRead);
/* 623*/    if (totalRead == -1) {
/* 624*/      if (numToRead > 0) {
/* 625*/          throw new IOException("Truncated TAR archive"); 
/*   0*/         }
/* 627*/      this.hasHitEOF = true;
/*   0*/    } else {
/* 629*/      count(totalRead);
/* 630*/      this.entryOffset += totalRead;
/*   0*/    } 
/* 633*/    return totalRead;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canReadEntryData(ArchiveEntry ae) {
/* 643*/    if (ae instanceof TarArchiveEntry) {
/* 644*/      TarArchiveEntry te = (TarArchiveEntry)ae;
/* 645*/      return !te.isGNUSparse();
/*   0*/    } 
/* 647*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry getCurrentEntry() {
/* 656*/    return this.currEntry;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void setCurrentEntry(TarArchiveEntry e) {
/* 660*/    this.currEntry = e;
/*   0*/  }
/*   0*/  
/*   0*/  protected final boolean isAtEOF() {
/* 664*/    return this.hasHitEOF;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void setAtEOF(boolean b) {
/* 668*/    this.hasHitEOF = b;
/*   0*/  }
/*   0*/  
/*   0*/  private void consumeRemainderOfLastBlock() throws IOException {
/* 677*/    long bytesReadOfLastBlock = getBytesRead() % this.blockSize;
/* 678*/    if (bytesReadOfLastBlock > 0L) {
/* 679*/      long skipped = IOUtils.skip(this.is, this.blockSize - bytesReadOfLastBlock);
/* 680*/      count(skipped);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean matches(byte[] signature, int length) {
/* 694*/    if (length < 265) {
/* 695*/        return false; 
/*   0*/       }
/* 698*/    if (ArchiveUtils.matchAsciiBuffer("ustar\000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2)) {
/* 704*/        return true; 
/*   0*/       }
/* 706*/    if (ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(" \000", signature, 263, 2) || ArchiveUtils.matchAsciiBuffer("0\000", signature, 263, 2))) {
/* 717*/        return true; 
/*   0*/       }
/* 720*/    ArchiveUtils.matchAsciiBuffer("ustar\000", signature, 257, 6);
/* 720*/    if (ArchiveUtils.matchAsciiBuffer("\000\000", signature, 263, 2)) {
/* 726*/        return true; 
/*   0*/       }
/* 728*/    return false;
/*   0*/  }
/*   0*/}
