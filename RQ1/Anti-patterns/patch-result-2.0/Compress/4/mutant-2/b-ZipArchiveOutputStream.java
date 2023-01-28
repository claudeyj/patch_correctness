/*   0*/package org.apache.commons.compress.archivers.zip;
/*   0*/
/*   0*/import java.io.File;
/*   0*/import java.io.FileOutputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.OutputStream;
/*   0*/import java.io.RandomAccessFile;
/*   0*/import java.nio.ByteBuffer;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.zip.CRC32;
/*   0*/import java.util.zip.Deflater;
/*   0*/import java.util.zip.ZipException;
/*   0*/import org.apache.commons.compress.archivers.ArchiveEntry;
/*   0*/import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*   0*/
/*   0*/public class ZipArchiveOutputStream extends ArchiveOutputStream {
/*   0*/  static final int BYTE_MASK = 255;
/*   0*/  
/*   0*/  private static final int SHORT = 2;
/*   0*/  
/*   0*/  private static final int WORD = 4;
/*   0*/  
/*   0*/  static final int BUFFER_SIZE = 512;
/*   0*/  
/*   0*/  private static final int DEFLATER_BLOCK_SIZE = 8192;
/*   0*/  
/*   0*/  public static final int DEFLATED = 8;
/*   0*/  
/*   0*/  public static final int DEFAULT_COMPRESSION = -1;
/*   0*/  
/*   0*/  public static final int STORED = 0;
/*   0*/  
/*   0*/  static final String DEFAULT_ENCODING = "UTF8";
/*   0*/  
/*   0*/  public static final int EFS_FLAG = 2048;
/*   0*/  
/*   0*/  private ZipArchiveEntry entry;
/*   0*/  
/* 107*/  private String comment = "";
/*   0*/  
/* 112*/  private int level = -1;
/*   0*/  
/*   0*/  private boolean hasCompressionLevelChanged = false;
/*   0*/  
/* 123*/  private int method = 8;
/*   0*/  
/* 128*/  private final List entries = new LinkedList();
/*   0*/  
/* 133*/  private final CRC32 crc = new CRC32();
/*   0*/  
/* 138*/  private long written = 0L;
/*   0*/  
/* 143*/  private long dataStart = 0L;
/*   0*/  
/* 149*/  private long localDataStart = 0L;
/*   0*/  
/* 154*/  private long cdOffset = 0L;
/*   0*/  
/* 159*/  private long cdLength = 0L;
/*   0*/  
/* 164*/  private static final byte[] ZERO = new byte[] { 0, 0 };
/*   0*/  
/* 169*/  private static final byte[] LZERO = new byte[] { 0, 0, 0, 0 };
/*   0*/  
/* 174*/  private final Map offsets = new HashMap();
/*   0*/  
/* 183*/  private String encoding = "UTF8";
/*   0*/  
/* 191*/  private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
/*   0*/  
/* 198*/  protected final Deflater def = new Deflater(this.level, true);
/*   0*/  
/* 204*/  private final byte[] buf = new byte[512];
/*   0*/  
/*   0*/  private final RandomAccessFile raf;
/*   0*/  
/*   0*/  private final OutputStream out;
/*   0*/  
/*   0*/  private boolean useEFS = true;
/*   0*/  
/*   0*/  private boolean fallbackToUTF8 = false;
/*   0*/  
/* 226*/  private UnicodeExtraFieldPolicy createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
/*   0*/  
/*   0*/  public ZipArchiveOutputStream(OutputStream out) {
/* 234*/    this.out = out;
/* 235*/    this.raf = null;
/*   0*/  }
/*   0*/  
/*   0*/  public ZipArchiveOutputStream(File file) throws IOException {
/* 245*/    OutputStream o = null;
/* 246*/    RandomAccessFile _raf = null;
/*   0*/    try {
/* 248*/      _raf = new RandomAccessFile(file, "rw");
/* 249*/      _raf.setLength(0L);
/* 250*/    } catch (IOException e) {
/* 251*/      if (_raf != null) {
/*   0*/        try {
/* 253*/          _raf.close();
/* 254*/        } catch (IOException iOException) {}
/* 257*/        _raf = null;
/*   0*/      } 
/* 259*/      o = new FileOutputStream(file);
/*   0*/    } 
/* 261*/    this.out = o;
/* 262*/    this.raf = _raf;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSeekable() {
/* 275*/    return (this.raf != null);
/*   0*/  }
/*   0*/  
/*   0*/  public void setEncoding(String encoding) {
/* 288*/    this.encoding = encoding;
/* 289*/    this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/* 290*/    this.useEFS &= ZipEncodingHelper.isUTF8(encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public String getEncoding() {
/* 299*/    return this.encoding;
/*   0*/  }
/*   0*/  
/*   0*/  public void setUseLanguageEncodingFlag(boolean b) {
/* 309*/    this.useEFS = (b && ZipEncodingHelper.isUTF8(this.encoding));
/*   0*/  }
/*   0*/  
/*   0*/  public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b) {
/* 318*/    this.createUnicodeExtraFields = b;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFallbackToUTF8(boolean b) {
/* 328*/    this.fallbackToUTF8 = b;
/*   0*/  }
/*   0*/  
/*   0*/  public void finish() throws IOException {
/* 335*/    if (this.entry != null) {
/* 336*/        throw new IOException("This archives contains unclosed entries."); 
/*   0*/       }
/* 339*/    this.cdOffset = this.written;
/* 340*/    for (Iterator i = this.entries.iterator(); i.hasNext();) {
/* 341*/        writeCentralFileHeader((ZipArchiveEntry)i.next()); 
/*   0*/       }
/* 343*/    this.cdLength = this.written - this.cdOffset;
/* 344*/    writeCentralDirectoryEnd();
/* 345*/    this.offsets.clear();
/* 346*/    this.entries.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public void closeArchiveEntry() throws IOException {
/* 354*/    if (this.entry == null) {
/*   0*/        return; 
/*   0*/       }
/* 358*/    long realCrc = this.crc.getValue();
/* 359*/    this.crc.reset();
/* 361*/    if (this.entry.getMethod() == 8) {
/* 362*/      this.def.finish();
/* 363*/      while (!this.def.finished()) {
/* 364*/          deflate(); 
/*   0*/         }
/* 367*/      this.entry.setSize(ZipUtil.adjustToLong(this.def.getTotalIn()));
/* 368*/      this.entry.setCompressedSize(ZipUtil.adjustToLong(this.def.getTotalOut()));
/* 369*/      this.entry.setCrc(realCrc);
/* 371*/      this.def.reset();
/* 373*/      this.written += this.entry.getCompressedSize();
/* 374*/    } else if (this.raf == null) {
/* 375*/      if (this.entry.getCrc() != realCrc) {
/* 376*/          throw new ZipException("bad CRC checksum for entry " + this.entry.getName() + ": " + Long.toHexString(this.entry.getCrc()) + " instead of " + Long.toHexString(realCrc)); 
/*   0*/         }
/* 383*/      if (this.entry.getSize() != this.written - this.dataStart) {
/* 384*/          throw new ZipException("bad size for entry " + this.entry.getName() + ": " + this.entry.getSize() + " instead of " + (this.written - this.dataStart)); 
/*   0*/         }
/*   0*/    } else {
/* 391*/      long size = this.written - this.dataStart;
/* 393*/      this.entry.setSize(size);
/* 394*/      this.entry.setCompressedSize(size);
/* 395*/      this.entry.setCrc(realCrc);
/*   0*/    } 
/* 400*/    if (this.raf != null) {
/* 401*/      long save = this.raf.getFilePointer();
/* 403*/      this.raf.seek(this.localDataStart);
/* 404*/      writeOut(ZipLong.getBytes(this.entry.getCrc()));
/* 405*/      writeOut(ZipLong.getBytes(this.entry.getCompressedSize()));
/* 406*/      writeOut(ZipLong.getBytes(this.entry.getSize()));
/* 407*/      this.raf.seek(save);
/*   0*/    } 
/* 410*/    writeDataDescriptor(this.entry);
/* 411*/    this.entry = null;
/*   0*/  }
/*   0*/  
/*   0*/  public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
/* 417*/    closeArchiveEntry();
/* 419*/    this.entry = (ZipArchiveEntry)archiveEntry;
/* 420*/    this.entries.add(this.entry);
/* 422*/    if (this.entry.getMethod() == -1) {
/* 423*/        this.entry.setMethod(this.method); 
/*   0*/       }
/* 426*/    if (this.entry.getTime() == -1L) {
/* 427*/        this.entry.setTime(System.currentTimeMillis()); 
/*   0*/       }
/* 431*/    if (this.entry.getMethod() == 0 && this.raf == null) {
/* 432*/      if (this.entry.getSize() == -1L) {
/* 433*/          throw new ZipException("uncompressed size is required for STORED method when not writing to a file"); 
/*   0*/         }
/* 437*/      if (this.entry.getCrc() == -1L) {
/* 438*/          throw new ZipException("crc checksum is required for STORED method when not writing to a file"); 
/*   0*/         }
/* 441*/      this.entry.setCompressedSize(this.entry.getSize());
/*   0*/    } 
/* 444*/    if (this.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
/* 445*/      this.def.setLevel(this.level);
/* 446*/      this.hasCompressionLevelChanged = false;
/*   0*/    } 
/* 448*/    writeLocalFileHeader(this.entry);
/*   0*/  }
/*   0*/  
/*   0*/  public void setComment(String comment) {
/* 456*/    this.comment = comment;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLevel(int level) {
/* 468*/    if (level < -1 || level > 9) {
/* 470*/        throw new IllegalArgumentException("Invalid compression level: " + level); 
/*   0*/       }
/* 473*/    this.hasCompressionLevelChanged = (this.level != level);
/* 474*/    this.level = level;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMethod(int method) {
/* 484*/    this.method = method;
/*   0*/  }
/*   0*/  
/*   0*/  public void write(byte[] b, int offset, int length) throws IOException {
/* 495*/    if (this.entry.getMethod() == 8) {
/* 496*/      if (length > 0 && 
/* 497*/        !this.def.finished()) {
/* 498*/          if (length <= 8192) {
/* 499*/            this.def.setInput(b, offset, length);
/* 500*/            deflateUntilInputIsNeeded();
/*   0*/          } else {
/* 502*/            int fullblocks = length / 8192;
/* 503*/            for (int i = 0; i < fullblocks; i++) {
/* 504*/              this.def.setInput(b, offset + i * 8192, 8192);
/* 506*/              deflateUntilInputIsNeeded();
/*   0*/            } 
/* 508*/            int done = fullblocks * 8192;
/* 509*/            if (done < length) {
/* 510*/              this.def.setInput(b, offset + done, length - done);
/* 511*/              deflateUntilInputIsNeeded();
/*   0*/            } 
/*   0*/          }  
/*   0*/         }
/*   0*/    } else {
/* 517*/      writeOut(b, offset, length);
/* 518*/      this.written += length;
/*   0*/    } 
/* 520*/    this.crc.update(b, offset, length);
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 530*/    closeArchiveEntry();
/* 531*/    if (this.raf != null) {
/* 532*/        this.raf.close(); 
/*   0*/       }
/* 534*/    if (this.out != null) {
/* 535*/        this.out.close(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void flush() throws IOException {
/* 546*/    if (this.out != null) {
/* 547*/        this.out.flush(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/* 557*/  static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
/*   0*/  
/* 561*/  static final byte[] DD_SIG = ZipLong.getBytes(134695760L);
/*   0*/  
/* 565*/  static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
/*   0*/  
/* 569*/  static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
/*   0*/  
/*   0*/  protected final void deflate() throws IOException {
/* 576*/    int len = this.def.deflate(this.buf, 0, this.buf.length);
/* 577*/    if (len > 0) {
/* 578*/        writeOut(this.buf, 0, len); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void writeLocalFileHeader(ZipArchiveEntry ze) throws IOException {
/*   0*/    ZipEncoding entryEncoding;
/* 589*/    boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 593*/    if (!encodable && this.fallbackToUTF8) {
/* 594*/      entryEncoding = ZipEncodingHelper.UTF8_ZIP_ENCODING;
/*   0*/    } else {
/* 596*/      entryEncoding = this.zipEncoding;
/*   0*/    } 
/* 599*/    ByteBuffer name = entryEncoding.encode(ze.getName());
/* 601*/    if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
/* 603*/      if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !encodable) {
/* 605*/          ze.addExtraField(new UnicodePathExtraField(ze.getName(), name.array(), name.arrayOffset(), name.limit())); 
/*   0*/         }
/* 611*/      String comm = ze.getComment();
/* 612*/      if (comm != null && !"".equals(comm)) {
/* 614*/        boolean commentEncodable = this.zipEncoding.canEncode(comm);
/* 616*/        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !commentEncodable) {
/* 618*/          ByteBuffer commentB = entryEncoding.encode(comm);
/* 619*/          ze.addExtraField(new UnicodeCommentExtraField(comm, commentB.array(), commentB.arrayOffset(), commentB.limit()));
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 628*/    this.offsets.put(ze, ZipLong.getBytes(this.written));
/* 630*/    writeOut(LFH_SIG);
/* 631*/    this.written += 4L;
/* 634*/    int zipMethod = ze.getMethod();
/* 636*/    writeVersionNeededToExtractAndGeneralPurposeBits(zipMethod, (!encodable && this.fallbackToUTF8));
/* 639*/    this.written += 4L;
/* 642*/    writeOut(ZipShort.getBytes(zipMethod));
/* 643*/    this.written += 2L;
/* 646*/    writeOut(ZipUtil.toDosTime(ze.getTime()));
/* 647*/    this.written += 4L;
/* 652*/    this.localDataStart = this.written;
/* 653*/    if (zipMethod == 8 || this.raf != null) {
/* 654*/      writeOut(LZERO);
/* 655*/      writeOut(LZERO);
/* 656*/      writeOut(LZERO);
/*   0*/    } else {
/* 658*/      writeOut(ZipLong.getBytes(ze.getCrc()));
/* 659*/      writeOut(ZipLong.getBytes(ze.getSize()));
/* 660*/      writeOut(ZipLong.getBytes(ze.getSize()));
/*   0*/    } 
/* 663*/    this.written += 12L;
/* 667*/    writeOut(ZipShort.getBytes(name.limit()));
/* 668*/    this.written += 2L;
/* 671*/    byte[] extra = ze.getLocalFileDataExtra();
/* 672*/    writeOut(ZipShort.getBytes(extra.length));
/* 673*/    this.written += 2L;
/* 676*/    writeOut(name.array(), name.arrayOffset(), name.limit());
/* 677*/    this.written += name.limit();
/* 680*/    writeOut(extra);
/* 681*/    this.written += extra.length;
/* 683*/    this.dataStart = this.written;
/*   0*/  }
/*   0*/  
/*   0*/  protected void writeDataDescriptor(ZipArchiveEntry ze) throws IOException {
/* 692*/    if (ze.getMethod() != 8 || this.raf != null) {
/*   0*/        return; 
/*   0*/       }
/* 695*/    writeOut(DD_SIG);
/* 696*/    writeOut(ZipLong.getBytes(this.entry.getCrc()));
/* 697*/    writeOut(ZipLong.getBytes(this.entry.getCompressedSize()));
/* 698*/    writeOut(ZipLong.getBytes(this.entry.getSize()));
/* 700*/    this.written += 16L;
/*   0*/  }
/*   0*/  
/*   0*/  protected void writeCentralFileHeader(ZipArchiveEntry ze) throws IOException {
/*   0*/    ZipEncoding entryEncoding;
/* 710*/    writeOut(CFH_SIG);
/* 711*/    this.written += 4L;
/* 715*/    writeOut(ZipShort.getBytes(ze.getPlatform() << 8 | 0x14));
/* 716*/    this.written += 2L;
/* 718*/    int zipMethod = ze.getMethod();
/* 719*/    boolean encodable = this.zipEncoding.canEncode(ze.getName());
/* 720*/    writeVersionNeededToExtractAndGeneralPurposeBits(zipMethod, (!encodable && this.fallbackToUTF8));
/* 723*/    this.written += 4L;
/* 726*/    writeOut(ZipShort.getBytes(zipMethod));
/* 727*/    this.written += 2L;
/* 730*/    writeOut(ZipUtil.toDosTime(ze.getTime()));
/* 731*/    this.written += 4L;
/* 736*/    writeOut(ZipLong.getBytes(ze.getCrc()));
/* 737*/    writeOut(ZipLong.getBytes(ze.getCompressedSize()));
/* 738*/    writeOut(ZipLong.getBytes(ze.getSize()));
/* 740*/    this.written += 12L;
/* 746*/    if (!encodable && this.fallbackToUTF8) {
/* 747*/      entryEncoding = ZipEncodingHelper.UTF8_ZIP_ENCODING;
/*   0*/    } else {
/* 749*/      entryEncoding = this.zipEncoding;
/*   0*/    } 
/* 752*/    ByteBuffer name = entryEncoding.encode(ze.getName());
/* 754*/    writeOut(ZipShort.getBytes(name.limit()));
/* 755*/    this.written += 2L;
/* 758*/    byte[] extra = ze.getCentralDirectoryExtra();
/* 759*/    writeOut(ZipShort.getBytes(extra.length));
/* 760*/    this.written += 2L;
/* 763*/    String comm = ze.getComment();
/* 764*/    if (comm == null) {
/* 765*/        comm = ""; 
/*   0*/       }
/* 768*/    ByteBuffer commentB = entryEncoding.encode(comm);
/* 770*/    writeOut(ZipShort.getBytes(commentB.limit()));
/* 771*/    this.written += 2L;
/* 774*/    writeOut(ZERO);
/* 775*/    this.written += 2L;
/* 778*/    writeOut(ZipShort.getBytes(ze.getInternalAttributes()));
/* 779*/    this.written += 2L;
/* 782*/    writeOut(ZipLong.getBytes(ze.getExternalAttributes()));
/* 783*/    this.written += 4L;
/* 786*/    writeOut((byte[])this.offsets.get(ze));
/* 787*/    this.written += 4L;
/* 790*/    writeOut(name.array(), name.arrayOffset(), name.limit());
/* 791*/    this.written += name.limit();
/* 794*/    writeOut(extra);
/* 795*/    this.written += extra.length;
/* 798*/    writeOut(commentB.array(), commentB.arrayOffset(), commentB.limit());
/* 799*/    this.written += commentB.limit();
/*   0*/  }
/*   0*/  
/*   0*/  protected void writeCentralDirectoryEnd() throws IOException {
/* 807*/    writeOut(EOCD_SIG);
/* 810*/    writeOut(ZERO);
/* 811*/    writeOut(ZERO);
/* 814*/    byte[] num = ZipShort.getBytes(this.entries.size());
/* 815*/    writeOut(num);
/* 816*/    writeOut(num);
/* 819*/    writeOut(ZipLong.getBytes(this.cdLength));
/* 820*/    writeOut(ZipLong.getBytes(this.cdOffset));
/* 823*/    ByteBuffer data = this.zipEncoding.encode(this.comment);
/* 824*/    writeOut(ZipShort.getBytes(data.limit()));
/* 825*/    writeOut(data.array(), data.arrayOffset(), data.limit());
/*   0*/  }
/*   0*/  
/*   0*/  protected final void writeOut(byte[] data) throws IOException {
/* 834*/    writeOut(data, 0, data.length);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void writeOut(byte[] data, int offset, int length) throws IOException {
/* 846*/    if (this.raf != null) {
/* 847*/      this.raf.write(data, offset, length);
/*   0*/    } else {
/* 849*/      this.out.write(data, offset, length);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void deflateUntilInputIsNeeded() throws IOException {
/* 854*/    while (!this.def.needsInput()) {
/* 855*/        deflate(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void writeVersionNeededToExtractAndGeneralPurposeBits(int zipMethod, boolean utfFallback) throws IOException {
/* 866*/    int versionNeededToExtract = 10;
/* 867*/    int generalPurposeFlag = (this.useEFS || utfFallback) ? 2048 : 0;
/* 868*/    if (zipMethod == 8 && this.raf == null) {
/* 871*/      versionNeededToExtract = 20;
/* 873*/      generalPurposeFlag |= 0x8;
/*   0*/    } 
/* 878*/    writeOut(ZipShort.getBytes(versionNeededToExtract));
/* 880*/    writeOut(ZipShort.getBytes(generalPurposeFlag));
/*   0*/  }
/*   0*/  
/*   0*/  public static final class UnicodeExtraFieldPolicy {
/* 891*/    public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
/*   0*/    
/* 896*/    public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
/*   0*/    
/* 902*/    public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
/*   0*/    
/*   0*/    private final String name;
/*   0*/    
/*   0*/    private UnicodeExtraFieldPolicy(String n) {
/* 907*/      this.name = n;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 910*/      return this.name;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
/* 916*/    return new ZipArchiveEntry(inputFile, entryName);
/*   0*/  }
/*   0*/}
