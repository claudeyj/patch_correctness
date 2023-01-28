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
/*   0*/  private final int blockSize;
/*   0*/  
/*   0*/  private final int recordSize;
/*   0*/  
/*   0*/  private final int recsPerBlock;
/*   0*/  
/*   0*/  private final byte[] blockBuffer;
/*   0*/  
/*   0*/  private int currBlkIdx;
/*   0*/  
/*   0*/  private int currRecIdx;
/*   0*/  
/*   0*/  public TarBuffer(InputStream inStream) {
/*  63*/    this(inStream, 10240);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(InputStream inStream, int blockSize) {
/*  72*/    this(inStream, blockSize, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(InputStream inStream, int blockSize, int recordSize) {
/*  82*/    this(inStream, null, blockSize, recordSize);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(OutputStream outStream) {
/*  90*/    this(outStream, 10240);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(OutputStream outStream, int blockSize) {
/*  99*/    this(outStream, blockSize, 512);
/*   0*/  }
/*   0*/  
/*   0*/  public TarBuffer(OutputStream outStream, int blockSize, int recordSize) {
/* 109*/    this(null, outStream, blockSize, recordSize);
/*   0*/  }
/*   0*/  
/*   0*/  private TarBuffer(InputStream inStream, OutputStream outStream, int blockSize, int recordSize) {
/* 116*/    this.inStream = inStream;
/* 117*/    this.outStream = outStream;
/* 118*/    this.blockSize = blockSize;
/* 119*/    this.recordSize = recordSize;
/* 120*/    this.recsPerBlock = this.blockSize / this.recordSize;
/* 121*/    this.blockBuffer = new byte[this.blockSize];
/* 123*/    if (this.inStream != null) {
/* 124*/      this.currBlkIdx = -1;
/* 125*/      this.currRecIdx = this.recsPerBlock;
/*   0*/    } else {
/* 127*/      this.currBlkIdx = 0;
/* 128*/      this.currRecIdx = 0;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int getBlockSize() {
/* 137*/    return this.blockSize;
/*   0*/  }
/*   0*/  
/*   0*/  public int getRecordSize() {
/* 145*/    return this.recordSize;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEOFRecord(byte[] record) {
/* 156*/    for (int i = 0, sz = getRecordSize(); i < sz; i++) {
/* 157*/      if (record[i] != 0) {
/* 158*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 162*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void skipRecord() throws IOException {
/* 170*/    if (this.inStream == null) {
/* 171*/        throw new IOException("reading (via skip) from an output buffer"); 
/*   0*/       }
/* 174*/    if (this.currRecIdx >= this.recsPerBlock && !readBlock()) {
/*   0*/        return; 
/*   0*/       }
/* 178*/    this.currRecIdx++;
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] readRecord() throws IOException {
/* 188*/    if (this.inStream == null) {
/* 189*/      if (this.outStream == null) {
/* 190*/          throw new IOException("input buffer is closed"); 
/*   0*/         }
/* 192*/      throw new IOException("reading from an output buffer");
/*   0*/    } 
/* 195*/    if (this.currRecIdx >= this.recsPerBlock && !readBlock()) {
/* 196*/        return null; 
/*   0*/       }
/* 199*/    byte[] result = new byte[this.recordSize];
/* 201*/    System.arraycopy(this.blockBuffer, this.currRecIdx * this.recordSize, result, 0, this.recordSize);
/* 205*/    this.currRecIdx++;
/* 207*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean readBlock() throws IOException {
/* 214*/    if (this.inStream == null) {
/* 215*/        throw new IOException("reading from an output buffer"); 
/*   0*/       }
/* 218*/    this.currRecIdx = 0;
/* 220*/    int offset = 0;
/* 221*/    int bytesNeeded = this.blockSize;
/* 223*/    while (bytesNeeded > 0) {
/* 224*/      long numBytes = this.inStream.read(this.blockBuffer, offset, bytesNeeded);
/* 240*/      if (numBytes == -1L) {
/* 241*/        if (offset == 0) {
/* 245*/            return false; 
/*   0*/           }
/* 253*/        Arrays.fill(this.blockBuffer, offset, offset + bytesNeeded, (byte)0);
/*   0*/        break;
/*   0*/      } 
/* 258*/      offset = (int)(bytesNeeded + numBytes);
/* 259*/      bytesNeeded = (int)(bytesNeeded - numBytes);
/* 261*/      if (numBytes != this.blockSize);
/*   0*/    } 
/* 266*/    this.currBlkIdx++;
/* 268*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int getCurrentBlockNum() {
/* 277*/    return this.currBlkIdx;
/*   0*/  }
/*   0*/  
/*   0*/  public int getCurrentRecordNum() {
/* 287*/    return this.currRecIdx - 1;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRecord(byte[] record) throws IOException {
/* 297*/    if (this.outStream == null) {
/* 298*/      if (this.inStream == null) {
/* 299*/          throw new IOException("Output buffer is closed"); 
/*   0*/         }
/* 301*/      throw new IOException("writing to an input buffer");
/*   0*/    } 
/* 304*/    if (record.length != this.recordSize) {
/* 305*/        throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + this.recordSize + "'"); 
/*   0*/       }
/* 311*/    if (this.currRecIdx >= this.recsPerBlock) {
/* 312*/        writeBlock(); 
/*   0*/       }
/* 315*/    System.arraycopy(record, 0, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
/* 319*/    this.currRecIdx++;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeRecord(byte[] buf, int offset) throws IOException {
/* 332*/    if (this.outStream == null) {
/* 333*/      if (this.inStream == null) {
/* 334*/          throw new IOException("Output buffer is closed"); 
/*   0*/         }
/* 336*/      throw new IOException("writing to an input buffer");
/*   0*/    } 
/* 339*/    if (offset + this.recordSize > buf.length) {
/* 340*/        throw new IOException("record has length '" + buf.length + "' with offset '" + offset + "' which is less than the record size of '" + this.recordSize + "'"); 
/*   0*/       }
/* 346*/    if (this.currRecIdx >= this.recsPerBlock) {
/* 347*/        writeBlock(); 
/*   0*/       }
/* 350*/    System.arraycopy(buf, offset, this.blockBuffer, this.currRecIdx * this.recordSize, this.recordSize);
/* 354*/    this.currRecIdx++;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeBlock() throws IOException {
/* 361*/    if (this.outStream == null) {
/* 362*/        throw new IOException("writing to an input buffer"); 
/*   0*/       }
/* 365*/    this.outStream.write(this.blockBuffer, 0, this.blockSize);
/* 366*/    this.outStream.flush();
/* 368*/    this.currRecIdx = 0;
/* 369*/    this.currBlkIdx++;
/* 370*/    Arrays.fill(this.blockBuffer, (byte)0);
/*   0*/  }
/*   0*/  
/*   0*/  void flushBlock() throws IOException {
/* 377*/    if (this.outStream == null) {
/* 378*/        throw new IOException("writing to an input buffer"); 
/*   0*/       }
/* 381*/    if (this.currRecIdx > 0) {
/* 382*/        writeBlock(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void close() throws IOException {
/* 392*/    if (this.outStream != null) {
/* 393*/      flushBlock();
/* 395*/      if (this.outStream != System.out && this.outStream != System.err) {
/* 397*/        this.outStream.close();
/* 399*/        this.outStream = null;
/*   0*/      } 
/* 401*/    } else if (this.inStream != null) {
/* 402*/      if (this.inStream != System.in) {
/* 403*/          this.inStream.close(); 
/*   0*/         }
/* 405*/      this.inStream = null;
/*   0*/    } 
/*   0*/  }
/*   0*/}
