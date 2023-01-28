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
/*   0*/    // Byte code:
/*   0*/    //   0: aload_1
/*   0*/    //   1: ifnonnull -> 12
/*   0*/    //   4: new java/lang/NullPointerException
/*   0*/    //   7: dup
/*   0*/    //   8: invokespecial <init> : ()V
/*   0*/    //   11: athrow
/*   0*/    //   12: iload_2
/*   0*/    //   13: iflt -> 20
/*   0*/    //   16: iload_3
/*   0*/    //   17: ifge -> 28
/*   0*/    //   20: new java/lang/IndexOutOfBoundsException
/*   0*/    //   23: dup
/*   0*/    //   24: invokespecial <init> : ()V
/*   0*/    //   27: athrow
/*   0*/    //   28: iload_2
/*   0*/    //   29: aload_1
/*   0*/    //   30: arraylength
/*   0*/    //   31: if_icmpgt -> 42
/*   0*/    //   34: iload_2
/*   0*/    //   35: iload_3
/*   0*/    //   36: iadd
/*   0*/    //   37: aload_1
/*   0*/    //   38: arraylength
/*   0*/    //   39: if_icmple -> 50
/*   0*/    //   42: new java/lang/IndexOutOfBoundsException
/*   0*/    //   45: dup
/*   0*/    //   46: invokespecial <init> : ()V
/*   0*/    //   49: athrow
/*   0*/    //   50: iload_3
/*   0*/    //   51: ifne -> 56
/*   0*/    //   54: iconst_0
/*   0*/    //   55: ireturn
/*   0*/    //   56: iconst_0
/*   0*/    //   57: istore #4
/*   0*/    //   59: iload #4
/*   0*/    //   61: ifne -> 176
/*   0*/    //   64: aload_0
/*   0*/    //   65: getfield base64 : Lorg/apache/commons/codec/binary/Base64;
/*   0*/    //   68: invokevirtual hasData : ()Z
/*   0*/    //   71: ifne -> 161
/*   0*/    //   74: aload_0
/*   0*/    //   75: getfield doEncode : Z
/*   0*/    //   78: ifeq -> 87
/*   0*/    //   81: sipush #4096
/*   0*/    //   84: goto -> 90
/*   0*/    //   87: sipush #8192
/*   0*/    //   90: newarray byte
/*   0*/    //   92: astore #5
/*   0*/    //   94: aload_0
/*   0*/    //   95: getfield in : Ljava/io/InputStream;
/*   0*/    //   98: aload #5
/*   0*/    //   100: invokevirtual read : ([B)I
/*   0*/    //   103: istore #6
/*   0*/    //   105: iload #6
/*   0*/    //   107: pop
/*   0*/    //   108: goto -> 127
/*   0*/    //   111: aload_1
/*   0*/    //   112: arraylength
/*   0*/    //   113: iload_3
/*   0*/    //   114: if_icmpne -> 127
/*   0*/    //   117: aload_0
/*   0*/    //   118: getfield base64 : Lorg/apache/commons/codec/binary/Base64;
/*   0*/    //   121: aload_1
/*   0*/    //   122: iload_2
/*   0*/    //   123: iload_3
/*   0*/    //   124: invokevirtual setInitialBuffer : ([BII)V
/*   0*/    //   127: aload_0
/*   0*/    //   128: getfield doEncode : Z
/*   0*/    //   131: ifeq -> 149
/*   0*/    //   134: aload_0
/*   0*/    //   135: getfield base64 : Lorg/apache/commons/codec/binary/Base64;
/*   0*/    //   138: aload #5
/*   0*/    //   140: iconst_0
/*   0*/    //   141: iload #6
/*   0*/    //   143: invokevirtual encode : ([BII)V
/*   0*/    //   146: goto -> 161
/*   0*/    //   149: aload_0
/*   0*/    //   150: getfield base64 : Lorg/apache/commons/codec/binary/Base64;
/*   0*/    //   153: aload #5
/*   0*/    //   155: iconst_0
/*   0*/    //   156: iload #6
/*   0*/    //   158: invokevirtual decode : ([BII)V
/*   0*/    //   161: aload_0
/*   0*/    //   162: getfield base64 : Lorg/apache/commons/codec/binary/Base64;
/*   0*/    //   165: aload_1
/*   0*/    //   166: iload_2
/*   0*/    //   167: iload_3
/*   0*/    //   168: invokevirtual readResults : ([BII)I
/*   0*/    //   171: istore #4
/*   0*/    //   173: goto -> 59
/*   0*/    //   176: iload #4
/*   0*/    //   178: ireturn
/*   0*/    // Line number table:
/*   0*/    //   Java source line number -> byte code offset
/*   0*/    //   #139	-> 0
/*   0*/    //   #140	-> 4
/*   0*/    //   #141	-> 12
/*   0*/    //   #142	-> 20
/*   0*/    //   #143	-> 28
/*   0*/    //   #144	-> 42
/*   0*/    //   #145	-> 50
/*   0*/    //   #146	-> 54
/*   0*/    //   #148	-> 56
/*   0*/    //   #165	-> 59
/*   0*/    //   #166	-> 64
/*   0*/    //   #167	-> 74
/*   0*/    //   #168	-> 94
/*   0*/    //   #169	-> 105
/*   0*/    //   #170	-> 117
/*   0*/    //   #172	-> 127
/*   0*/    //   #173	-> 134
/*   0*/    //   #175	-> 149
/*   0*/    //   #178	-> 161
/*   0*/    //   #180	-> 176
/*   0*/    // Local variable table:
/*   0*/    //   start	length	slot	name	descriptor
/*   0*/    //   94	67	5	buf	[B
/*   0*/    //   105	56	6	c	I
/*   0*/    //   59	120	4	readLen	I
/*   0*/    //   0	179	0	this	Lorg/apache/commons/codec/binary/Base64InputStream;
/*   0*/    //   0	179	1	b	[B
/*   0*/    //   0	179	2	offset	I
/*   0*/    //   0	179	3	len	I
/*   0*/  }
/*   0*/  
/*   0*/  public boolean markSupported() {
/* 190*/    return false;
/*   0*/  }
/*   0*/}
