/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/import java.io.File;
/*   0*/import java.io.IOException;
/*   0*/import java.io.OutputStream;
/*   0*/import java.io.StringWriter;
/*   0*/import java.nio.ByteBuffer;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Map;
/*   0*/import org.apache.commons.compress.archivers.ArchiveEntry;
/*   0*/import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*   0*/import org.apache.commons.compress.utils.CountingOutputStream;
/*   0*/
/*   0*/public class TarArchiveOutputStream extends ArchiveOutputStream {
/*   0*/  public static final int LONGFILE_ERROR = 0;
/*   0*/  
/*   0*/  public static final int LONGFILE_TRUNCATE = 1;
/*   0*/  
/*   0*/  public static final int LONGFILE_GNU = 2;
/*   0*/  
/*   0*/  public static final int LONGFILE_POSIX = 3;
/*   0*/  
/*   0*/  public static final int BIGNUMBER_ERROR = 0;
/*   0*/  
/*   0*/  public static final int BIGNUMBER_STAR = 1;
/*   0*/  
/*   0*/  public static final int BIGNUMBER_POSIX = 2;
/*   0*/  
/*   0*/  private long currSize;
/*   0*/  
/*   0*/  private String currName;
/*   0*/  
/*   0*/  private long currBytes;
/*   0*/  
/*   0*/  private final byte[] recordBuf;
/*   0*/  
/*   0*/  private int assemLen;
/*   0*/  
/*   0*/  private final byte[] assemBuf;
/*   0*/  
/*   0*/  protected final TarBuffer buffer;
/*   0*/  
/*  70*/  private int longFileMode = 0;
/*   0*/  
/*  71*/  private int bigNumberMode = 0;
/*   0*/  
/*   0*/  private boolean closed = false;
/*   0*/  
/*   0*/  private boolean haveUnclosedEntry = false;
/*   0*/  
/*   0*/  private boolean finished = false;
/*   0*/  
/*   0*/  private final OutputStream out;
/*   0*/  
/*   0*/  private final ZipEncoding encoding;
/*   0*/  
/*   0*/  private boolean addPaxHeadersForNonAsciiNames = false;
/*   0*/  
/*  86*/  private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
/*   0*/  
/*   0*/  public TarArchiveOutputStream(OutputStream os) {
/*  94*/    this(os, 10240, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveOutputStream(OutputStream os, String encoding) {
/* 104*/    this(os, 10240, 512, encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveOutputStream(OutputStream os, int blockSize) {
/* 113*/    this(os, blockSize, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveOutputStream(OutputStream os, int blockSize, String encoding) {
/* 125*/    this(os, blockSize, 512, encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize) {
/* 135*/    this(os, blockSize, recordSize, null);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize, String encoding) {
/* 148*/    this.out = new CountingOutputStream(os);
/* 149*/    this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
/* 151*/    this.buffer = new TarBuffer(this.out, blockSize, recordSize);
/* 152*/    this.assemLen = 0;
/* 153*/    this.assemBuf = new byte[recordSize];
/* 154*/    this.recordBuf = new byte[recordSize];
/*   0*/  }
/*   0*/  
/*   0*/  public void setLongFileMode(int longFileMode) {
/* 165*/    this.longFileMode = longFileMode;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBigNumberMode(int bigNumberMode) {
/* 177*/    this.bigNumberMode = bigNumberMode;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAddPaxHeadersForNonAsciiNames(boolean b) {
/* 185*/    this.addPaxHeadersForNonAsciiNames = b;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public int getCount() {
/* 191*/    return (int)getBytesWritten();
/*   0*/  }
/*   0*/  
/*   0*/  public long getBytesWritten() {
/* 196*/    return ((CountingOutputStream)this.out).getBytesWritten();
/*   0*/  }
/*   0*/  
/*   0*/  public void finish() throws IOException {
/* 210*/    if (this.finished) {
/* 211*/        throw new IOException("This archive has already been finished"); 
/*   0*/       }
/* 214*/    if (this.haveUnclosedEntry) {
/* 215*/        throw new IOException("This archives contains unclosed entries."); 
/*   0*/       }
/* 217*/    writeEOFRecord();
/* 218*/    writeEOFRecord();
/* 219*/    this.buffer.flushBlock();
/* 220*/    this.finished = true;
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 229*/    if (!this.finished) {
/* 230*/        finish(); 
/*   0*/       }
/* 233*/    if (!this.closed) {
/* 234*/      this.buffer.close();
/* 235*/      this.out.close();
/* 236*/      this.closed = true;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int getRecordSize() {
/* 246*/    return this.buffer.getRecordSize();
/*   0*/  }
/*   0*/  
/*   0*/  public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
/* 264*/    if (this.finished) {
/* 265*/        throw new IOException("Stream has already been finished"); 
/*   0*/       }
/* 267*/    TarArchiveEntry entry = (TarArchiveEntry)archiveEntry;
/* 268*/    Map<String, String> paxHeaders = new HashMap<String, String>();
/* 269*/    String entryName = entry.getName();
/* 270*/    ByteBuffer encodedName = this.encoding.encode(entryName);
/* 271*/    int nameLen = encodedName.limit() - encodedName.position();
/*   0*/    boolean paxHeaderContainsPath = false;
/* 273*/    if (nameLen >= 100) {
/* 275*/        if (this.longFileMode == 3) {
/* 276*/          paxHeaders.put("path", entryName);
/* 277*/          paxHeaderContainsPath = true;
/* 278*/        } else if (this.longFileMode == 2) {
/* 281*/          TarArchiveEntry longLinkEntry = new TarArchiveEntry("././@LongLink", (byte)76);
/* 284*/          longLinkEntry.setSize((nameLen + 1));
/* 285*/          putArchiveEntry(longLinkEntry);
/* 286*/          write(encodedName.array(), encodedName.arrayOffset(), nameLen);
/* 287*/          write(0);
/* 288*/          closeArchiveEntry();
/* 289*/        } else if (this.longFileMode != 1) {
/* 290*/          throw new RuntimeException("file name '" + entryName + "' is too long ( > " + 'd' + " bytes)");
/*   0*/        }  
/*   0*/       }
/* 296*/    if (this.bigNumberMode == 2) {
/* 297*/      addPaxHeadersForBigNumbers(paxHeaders, entry);
/* 298*/    } else if (this.bigNumberMode != 1) {
/* 299*/      failForBigNumbers(entry);
/*   0*/    } 
/* 302*/    if (this.addPaxHeadersForNonAsciiNames && !paxHeaderContainsPath && !ASCII.canEncode(entryName)) {
/* 304*/        paxHeaders.put("path", entryName); 
/*   0*/       }
/* 307*/    if (this.addPaxHeadersForNonAsciiNames && (entry.isLink() || entry.isSymbolicLink()) && !ASCII.canEncode(entry.getLinkName())) {
/* 310*/        paxHeaders.put("linkpath", entry.getLinkName()); 
/*   0*/       }
/* 313*/    if (paxHeaders.size() > 0) {
/* 314*/        writePaxHeaders(entryName, paxHeaders); 
/*   0*/       }
/* 317*/    entry.writeEntryHeader(this.recordBuf, this.encoding, (this.bigNumberMode == 1));
/* 319*/    this.buffer.writeRecord(this.recordBuf);
/* 321*/    this.currBytes = 0L;
/* 323*/    if (entry.isExtended()) {
/* 324*/      this.currSize = 0L;
/*   0*/    } else {
/* 326*/      this.currSize = entry.getSize();
/*   0*/    } 
/* 328*/    this.currName = entryName;
/* 329*/    this.haveUnclosedEntry = true;
/*   0*/  }
/*   0*/  
/*   0*/  public void closeArchiveEntry() throws IOException {
/* 344*/    if (this.finished) {
/* 345*/        throw new IOException("Stream has already been finished"); 
/*   0*/       }
/* 347*/    if (!this.haveUnclosedEntry) {
/* 348*/        throw new IOException("No current entry to close"); 
/*   0*/       }
/* 350*/    if (this.assemLen > 0) {
/* 351*/      for (int i = this.assemLen; i < this.assemBuf.length; i++) {
/* 352*/          this.assemBuf[i] = 0; 
/*   0*/         }
/* 355*/      this.buffer.writeRecord(this.assemBuf);
/* 357*/      this.currBytes += this.assemLen;
/* 358*/      this.assemLen = 0;
/*   0*/    } 
/* 361*/    if (this.currBytes < this.currSize) {
/* 362*/        throw new IOException("entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written"); 
/*   0*/       }
/* 367*/    this.haveUnclosedEntry = false;
/*   0*/  }
/*   0*/  
/*   0*/  public void write(byte[] wBuf, int wOffset, int numToWrite) throws IOException {
/* 386*/    if (this.currBytes + numToWrite > this.currSize) {
/* 387*/        throw new IOException("request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'"); 
/*   0*/       }
/* 401*/    if (this.assemLen > 0) {
/* 402*/        if (this.assemLen + numToWrite >= this.recordBuf.length) {
/* 403*/          int aLen = this.recordBuf.length - this.assemLen;
/* 405*/          System.arraycopy(this.assemBuf, 0, this.recordBuf, 0, this.assemLen);
/* 407*/          System.arraycopy(wBuf, wOffset, this.recordBuf, this.assemLen, aLen);
/* 409*/          this.buffer.writeRecord(this.recordBuf);
/* 411*/          this.currBytes += this.recordBuf.length;
/* 412*/          wOffset += aLen;
/* 413*/          numToWrite -= aLen;
/* 414*/          this.assemLen = 0;
/*   0*/        } else {
/* 416*/          System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
/* 419*/          wOffset += numToWrite;
/* 420*/          this.assemLen += numToWrite;
/* 421*/          numToWrite = 0;
/*   0*/        }  
/*   0*/       }
/* 430*/    while (numToWrite > 0) {
/* 431*/      if (numToWrite < this.recordBuf.length) {
/* 432*/        System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
/* 435*/        this.assemLen += numToWrite;
/*   0*/        break;
/*   0*/      } 
/* 440*/      this.buffer.writeRecord(wBuf, wOffset);
/* 442*/      int num = this.recordBuf.length;
/* 444*/      this.currBytes += num;
/* 445*/      numToWrite -= num;
/* 446*/      wOffset += num;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void writePaxHeaders(String entryName, Map<String, String> headers) throws IOException {
/* 456*/    String name = "./PaxHeaders.X/" + stripTo7Bits(entryName);
/* 459*/    if (name.length() >= 100) {
/* 460*/        name = name.substring(0, 99); 
/*   0*/       }
/* 462*/    TarArchiveEntry pex = new TarArchiveEntry(name, (byte)120);
/* 465*/    StringWriter w = new StringWriter();
/* 466*/    for (Map.Entry<String, String> h : headers.entrySet()) {
/* 467*/      String key = h.getKey();
/* 468*/      String value = h.getValue();
/* 469*/      int len = key.length() + value.length() + 3 + 2;
/* 472*/      String line = len + " " + key + "=" + value + "\n";
/* 473*/      int actualLength = (line.getBytes("UTF-8")).length;
/* 474*/      while (len != actualLength) {
/* 480*/        len = actualLength;
/* 481*/        line = len + " " + key + "=" + value + "\n";
/* 482*/        actualLength = (line.getBytes("UTF-8")).length;
/*   0*/      } 
/* 484*/      w.write(line);
/*   0*/    } 
/* 486*/    byte[] data = w.toString().getBytes("UTF-8");
/* 487*/    pex.setSize(data.length);
/* 488*/    putArchiveEntry(pex);
/* 489*/    write(data);
/* 490*/    closeArchiveEntry();
/*   0*/  }
/*   0*/  
/*   0*/  private String stripTo7Bits(String name) {
/* 494*/    int length = name.length();
/* 495*/    StringBuffer result = new StringBuffer(length);
/* 496*/    for (int i = 0; i < length; i++) {
/* 497*/      char stripped = (char)(name.charAt(i) & 0x7F);
/* 498*/      if (stripped != '\000') {
/* 499*/          result.append(stripped); 
/*   0*/         }
/*   0*/    } 
/* 502*/    return result.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void writeEOFRecord() throws IOException {
/* 510*/    for (int i = 0; i < this.recordBuf.length; i++) {
/* 511*/        this.recordBuf[i] = 0; 
/*   0*/       }
/* 514*/    this.buffer.writeRecord(this.recordBuf);
/*   0*/  }
/*   0*/  
/*   0*/  public void flush() throws IOException {
/* 519*/    this.out.flush();
/*   0*/  }
/*   0*/  
/*   0*/  public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 526*/    if (this.finished) {
/* 527*/        throw new IOException("Stream has already been finished"); 
/*   0*/       }
/* 529*/    return new TarArchiveEntry(inputFile, entryName);
/*   0*/  }
/*   0*/  
/*   0*/  private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarArchiveEntry entry) {
/* 534*/    addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 8589934591L);
/* 536*/    addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getGroupId(), 2097151L);
/* 538*/    addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000L, 8589934591L);
/* 541*/    addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getUserId(), 2097151L);
/* 544*/    addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", entry.getDevMajor(), 2097151L);
/* 546*/    addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", entry.getDevMinor(), 2097151L);
/* 549*/    failForBigNumber("mode", entry.getMode(), 2097151L);
/*   0*/  }
/*   0*/  
/*   0*/  private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue) {
/* 555*/    if (value < 0L || value > maxValue) {
/* 556*/        paxHeaders.put(header, String.valueOf(value)); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void failForBigNumbers(TarArchiveEntry entry) {
/* 561*/    failForBigNumber("entry size", entry.getSize(), 8589934591L);
/* 562*/    failForBigNumber("group id", entry.getGroupId(), 2097151L);
/* 563*/    failForBigNumber("last modification time", entry.getModTime().getTime() / 1000L, 8589934591L);
/* 566*/    failForBigNumber("user id", entry.getUserId(), 2097151L);
/* 567*/    failForBigNumber("mode", entry.getMode(), 2097151L);
/* 568*/    failForBigNumber("major device number", entry.getDevMajor(), 2097151L);
/* 570*/    failForBigNumber("minor device number", entry.getDevMinor(), 2097151L);
/*   0*/  }
/*   0*/  
/*   0*/  private void failForBigNumber(String field, long value, long maxValue) {
/* 575*/    if (value < 0L || value > maxValue) {
/* 576*/        throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )"); 
/*   0*/       }
/*   0*/  }
/*   0*/}
