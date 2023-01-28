/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.util.Arrays;
/*   0*/
/*   0*/class TarBuffer {
/*   0*/  public static final int DEFAULT_RCDSIZE = 512;
/*   0*/  
/*   0*/  public static final int DEFAULT_BLKSIZE = 10240;
/*   0*/  
/*   0*/  private InputStream inStream;
/*   0*/  
/*   0*/  private OutputStream outStream;
/*   0*/  
/*   0*/  private byte[] blockBuffer;
/*   0*/  
/*   0*/  private int currBlkIdx;
/*   0*/  
/*   0*/  private int currRecIdx;
/*   0*/  
/*   0*/  private int blockSize;
/*   0*/  
/*   0*/  private int recordSize;
/*   0*/  
/*   0*/  private int recsPerBlock;
/*   0*/  
/*   0*/  public TarBuffer(InputStream inStream) {
/*  61*/    this(inStream, 10240);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(InputStream inStream, int blockSize) {
/*  70*/    this(inStream, blockSize, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(InputStream inStream, int blockSize, int recordSize) {
/*  80*/    this.inStream = inStream;
/*  81*/    this.outStream = null;
/*  83*/    initialize(blockSize, recordSize);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(OutputStream outStream) {
/*  91*/    this(outStream, 10240);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(OutputStream outStream, int blockSize) {
/* 100*/    this(outStream, blockSize, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(OutputStream outStream, int blockSize, int recordSize) {
/* 110*/    this.inStream = null;
/* 111*/    this.outStream = outStream;
/* 113*/    initialize(blockSize, recordSize);
/*   0*/  }
/*   0*/  
/*   0*/  private void initialize(int blockSize, int recordSize) {
/* 120*/    this.blockSize = blockSize;
/* 121*/    this.recordSize = recordSize;
/* 122*/    this.recsPerBlock = this.blockSize / this.recordSize;
/* 123*/    this.blockBuffer = new byte[this.blockSize];
/* 125*/    if (this.inStream != null) {
/* 126*/      this.currBlkIdx = -1;
/* 127*/      this.currRecIdx = this.recsPerBlock;
/*   0*/    } else {
/* 129*/      this.currBlkIdx = 0;
/* 130*/      this.currRecIdx = 0;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int getBlockSize() {
/* 139*/    return this.blockSize;
/*   0*/  }
/*   0*/  
/*   0*/  public int getRecordSize() {
/* 147*/    return this.recordSize;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEOFRecord(byte[] record) {
/* 158*/    for (int i = 0, sz = getRecordSize(); i < sz; i++) {
/* 159*/      if (record[i] != 0) {
/* 160*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 164*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void skipRecord() throws IOException {
/* 172*/    if (this.inStream == null) {
/* 173*/        throw new IOException("reading (via skip) from an output buffer"); 
/*   0*/       }
/* 176*/    if (this.currRecIdx >= this.recsPerBlock && !readBlock()) {
/*   0*/        return; 
/*   0*/       }
/* 180*/    this.currRecIdx++;
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] readRecord() throws IOException {
/* 190*/    if (this.inStream == null) {
/* 191*/      if (this.outStream == null) {
/* 192*/          throw new IOException("input buffer is closed"); 
/*   0*/         }
/* 194*/      throw new IOException("reading from an output buffer");
/*   0*/    } 
/* 197*/    if (this.currRecIdx >= this.recsPerBlock && !readBlock()) {
/* 198*/        return null; 
/*   0*/       }
/* 201*/    byte[] result = new byte[this.recordSize];
/* 203*/    System.arraycopy(this.blockBuffer, this.currRecIdx * this.recordSize, result, 0, this.recordSize);
/* 207*/    this.currRecIdx++;
/* 209*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean readBlock() throws IOException {
/* 216*/    if (this.inStream == null) {
/* 217*/        throw new IOException("reading from an output buffer"); 
/*   0*/       }
/* 220*/    this.currRecIdx = 0;
/* 222*/    int offset = 0;
/* 223*/    int bytesNeeded = this.blockSize;
/* 225*/    while (bytesNeeded > 0) {
/* 226*/      long numBytes = this.inStream.read(this.blockBuffer, offset, this.blockSize);
/* 242*/      if (numBytes == -1L) {
/* 243*/        if (offset == 0) {
/* 247*/            return false; 
/*   0*/           }
/* 255*/        Arrays.fill(this.blockBuffer, offset, offset + bytesNeeded, (byte)0);
/*   0*/        break;
/*   0*/      } 
/* 260*/      offset = (int)(offset + numBytes);
/* 261*/      bytesNeeded = (int)(bytesNeeded - numBytes);
/* 263*/      if (numBytes != this.blockSize);
/*   0*/    } 
/* 268*/    this.currBlkIdx++;
/* 270*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int getCurrentBlockNum() {
/* 279*/    return this.currBlkIdx;
/*   0*/  }
/*   0*/  
/*   0*/  public int getCurrentRecordNum() {
/* 289*/    return this.currRecIdx - 1;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRecord(byte[] record) throws IOException {
/* 299*/    if (this.outStream == null) {
/* 300*/      if (this.inStream == null) {
/* 301*/          throw new IOException("Output buffer is closed"); 
/*   0*/         }
/* 303*/      throw new IOException("writing to an input buffer");
/*   0*/    } 
/* 306*/    if (record.length != this.recordSize) {
/* 307*/        throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + this.recordSize + "'"); 
/*   0*/       }
/* 313*/    if (this.currRecIdx >= this.recsPerBlock) {
/* 314*/        writeBlock(); 
/*   0*/       }
/* 317*/    System.arraycopy(record, 0, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
/* 321*/    this.currRecIdx++;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRecord(byte[] buf, int offset) throws IOException {
/* 334*/    if (this.outStream == null) {
/* 335*/      if (this.inStream == null) {
/* 336*/          throw new IOException("Output buffer is closed"); 
/*   0*/         }
/* 338*/      throw new IOException("writing to an input buffer");
/*   0*/    } 
/* 341*/    if (offset + this.recordSize > buf.length) {
/* 342*/        throw new IOException("record has length '" + buf.length + "' with offset '" + offset + "' which is less than the record size of '" + this.recordSize + "'"); 
/*   0*/       }
/* 348*/    if (this.currRecIdx >= this.recsPerBlock) {
/* 349*/        writeBlock(); 
/*   0*/       }
/* 352*/    System.arraycopy(buf, offset, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
/* 356*/    this.currRecIdx++;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeBlock() throws IOException {
/* 363*/    if (this.outStream == null) {
/* 364*/        throw new IOException("writing to an input buffer"); 
/*   0*/       }
/* 367*/    this.outStream.write(this.blockBuffer, 0, this.blockSize);
/* 368*/    this.outStream.flush();
/* 370*/    this.currRecIdx = 0;
/* 371*/    this.currBlkIdx++;
/* 372*/    Arrays.fill(this.blockBuffer, (byte)0);
/*   0*/  }
/*   0*/  
/*   0*/  void flushBlock() throws IOException {
/* 379*/    if (this.outStream == null) {
/* 380*/        throw new IOException("writing to an input buffer"); 
/*   0*/       }
/* 383*/    if (this.currRecIdx > 0) {
/* 384*/        writeBlock(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 394*/    if (this.outStream != null) {
/* 395*/      flushBlock();
/* 397*/      if (this.outStream != System.out && this.outStream != System.err) {
/* 399*/        this.outStream.close();
/* 401*/        this.outStream = null;
/*   0*/      } 
/* 403*/    } else if (this.inStream != null) {
/* 404*/      if (this.inStream != System.in) {
/* 405*/          this.inStream.close(); 
/*   0*/         }
/* 407*/      this.inStream = null;
/*   0*/    } 
/*   0*/  }
/*   0*/}
