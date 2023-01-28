/*   0*/package org.apache.commons.codec.binary;
/*   0*/
/*   0*/import java.io.FilterInputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/
/*   0*/public class Base64InputStream extends FilterInputStream {
/*   0*/  private final boolean doEncode;
/*   0*/  
/*   0*/  private final Base64 base64;
/*   0*/  
/*  52*/  private final byte[] singleByte = new byte[1];
/*   0*/  
/*   0*/  public Base64InputStream(InputStream in) {
/*  61*/    this(in, false);
/*   0*/  }
/*   0*/  
/*   0*/  public Base64InputStream(InputStream in, boolean doEncode) {
/*  74*/    super(in);
/*  75*/    this.doEncode = doEncode;
/*  76*/    this.base64 = new Base64(false);
/*   0*/  }
/*   0*/  
/*   0*/  public Base64InputStream(InputStream in, boolean doEncode, int lineLength, byte[] lineSeparator) {
/*  96*/    super(in);
/*  97*/    this.doEncode = doEncode;
/*  98*/    this.base64 = new Base64(lineLength, lineSeparator);
/*   0*/  }
/*   0*/  
/*   0*/  public int read() throws IOException {
/* 109*/    int r = read(this.singleByte, 0, 1);
/* 110*/    while (r == 0) {
/* 111*/        r = read(this.singleByte, 0, 1); 
/*   0*/       }
/* 113*/    if (r > 0) {
/* 114*/        return (this.singleByte[0] < 0) ? (256 + this.singleByte[0]) : this.singleByte[0]; 
/*   0*/       }
/* 116*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int read(byte[] b, int offset, int len) throws IOException {
/* 139*/    if (b == null) {
/* 140*/        throw new NullPointerException(); 
/*   0*/       }
/* 141*/    if (offset < 0 || len < 0) {
/* 142*/        throw new IndexOutOfBoundsException(); 
/*   0*/       }
/* 143*/    if (offset > b.length || offset + len > b.length) {
/* 144*/        throw new IndexOutOfBoundsException(); 
/*   0*/       }
/* 145*/    if (len == 0) {
/* 146*/        return 0; 
/*   0*/       }
/* 148*/    int readLen = 0;
/* 165*/    while (readLen == 0) {
/* 166*/      if (!this.base64.hasData()) {
/* 167*/        byte[] buf = new byte[this.doEncode ? 4096 : 8192];
/* 168*/        int c = this.in.read(buf);
/* 169*/        if (c > 0 && b.length == len) {
/* 170*/            this.base64.setInitialBuffer(b, offset, offset); 
/*   0*/           }
/* 172*/        if (this.doEncode) {
/* 173*/          this.base64.encode(buf, 0, c);
/*   0*/        } else {
/* 175*/          this.base64.decode(buf, 0, c);
/*   0*/        } 
/*   0*/      } 
/* 178*/      readLen = this.base64.readResults(b, offset, len);
/*   0*/    } 
/* 180*/    return readLen;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean markSupported() {
/* 190*/    return false;
/*   0*/  }
/*   0*/}
