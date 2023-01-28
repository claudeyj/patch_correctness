/*   0*/package com.fasterxml.jackson.core.util;
/*   0*/
/*   0*/public class BufferRecycler {
/*   0*/  public static final int BYTE_READ_IO_BUFFER = 0;
/*   0*/  
/*   0*/  public static final int BYTE_WRITE_ENCODING_BUFFER = 1;
/*   0*/  
/*   0*/  public static final int BYTE_WRITE_CONCAT_BUFFER = 2;
/*   0*/  
/*   0*/  public static final int BYTE_BASE64_CODEC_BUFFER = 3;
/*   0*/  
/*   0*/  public static final int CHAR_TOKEN_BUFFER = 0;
/*   0*/  
/*   0*/  public static final int CHAR_CONCAT_BUFFER = 1;
/*   0*/  
/*   0*/  public static final int CHAR_TEXT_BUFFER = 2;
/*   0*/  
/*   0*/  public static final int CHAR_NAME_COPY_BUFFER = 3;
/*   0*/  
/*  45*/  private static final int[] BYTE_BUFFER_LENGTHS = new int[] { 8000, 8000, 2000, 2000 };
/*   0*/  
/*  46*/  private static final int[] CHAR_BUFFER_LENGTHS = new int[] { 4000, 4000, 200, 200 };
/*   0*/  
/*   0*/  protected final byte[][] _byteBuffers;
/*   0*/  
/*   0*/  protected final char[][] _charBuffers;
/*   0*/  
/*   0*/  public BufferRecycler() {
/*  62*/    this(4, 4);
/*   0*/  }
/*   0*/  
/*   0*/  protected BufferRecycler(int bbCount, int cbCount) {
/*  72*/    this._byteBuffers = new byte[bbCount][];
/*  73*/    this._charBuffers = new char[cbCount][];
/*   0*/  }
/*   0*/  
/*   0*/  public final byte[] allocByteBuffer(int ix) {
/*  86*/    return allocByteBuffer(ix, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] allocByteBuffer(int ix, int minSize) {
/*  90*/    int DEF_SIZE = byteBufferLength(ix);
/*  91*/    if (minSize < DEF_SIZE) {
/*  92*/        minSize = DEF_SIZE; 
/*   0*/       }
/*  94*/    byte[] buffer = this._byteBuffers[ix];
/*  95*/    if (buffer == null || buffer.length < minSize) {
/*  96*/      buffer = balloc(minSize);
/*   0*/    } else {
/*  98*/      this._byteBuffers[ix] = null;
/*   0*/    } 
/* 100*/    return buffer;
/*   0*/  }
/*   0*/  
/*   0*/  public final void releaseByteBuffer(int ix, byte[] buffer) {
/* 104*/    this._byteBuffers[ix] = buffer;
/*   0*/  }
/*   0*/  
/*   0*/  public final char[] allocCharBuffer(int ix) {
/* 114*/    return allocCharBuffer(ix, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public char[] allocCharBuffer(int ix, int minSize) {
/* 118*/    int DEF_SIZE = charBufferLength(ix);
/* 119*/    if (minSize < DEF_SIZE) {
/* 120*/        minSize = DEF_SIZE; 
/*   0*/       }
/* 122*/    char[] buffer = this._charBuffers[ix];
/* 123*/    if (buffer == null || buffer.length < minSize) {
/* 124*/      buffer = calloc(minSize);
/*   0*/    } else {
/* 126*/      this._charBuffers[ix] = null;
/*   0*/    } 
/* 128*/    return buffer;
/*   0*/  }
/*   0*/  
/*   0*/  public void releaseCharBuffer(int ix, char[] buffer) {
/* 132*/    this._charBuffers[ix] = buffer;
/*   0*/  }
/*   0*/  
/*   0*/  protected int byteBufferLength(int ix) {
/* 142*/    return BYTE_BUFFER_LENGTHS[ix];
/*   0*/  }
/*   0*/  
/*   0*/  protected int charBufferLength(int ix) {
/* 146*/    return CHAR_BUFFER_LENGTHS[ix];
/*   0*/  }
/*   0*/  
/*   0*/  protected byte[] balloc(int size) {
/* 155*/    return new byte[size];
/*   0*/  }
/*   0*/  
/*   0*/  protected char[] calloc(int size) {
/* 156*/    return new char[size];
/*   0*/  }
/*   0*/}
