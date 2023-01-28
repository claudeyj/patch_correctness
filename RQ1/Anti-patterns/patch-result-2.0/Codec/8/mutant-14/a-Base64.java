/*   0*/package org.apache.commons.codec.binary;
/*   0*/
/*   0*/import java.math.BigInteger;
/*   0*/import org.apache.commons.codec.BinaryDecoder;
/*   0*/import org.apache.commons.codec.BinaryEncoder;
/*   0*/import org.apache.commons.codec.DecoderException;
/*   0*/import org.apache.commons.codec.EncoderException;
/*   0*/
/*   0*/public class Base64 implements BinaryEncoder, BinaryDecoder {
/*   0*/  private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
/*   0*/  
/*   0*/  private static final int DEFAULT_BUFFER_SIZE = 8192;
/*   0*/  
/*   0*/  public static final int MIME_CHUNK_SIZE = 76;
/*   0*/  
/*   0*/  public static final int PEM_CHUNK_SIZE = 64;
/*   0*/  
/*  91*/  static final byte[] CHUNK_SEPARATOR = new byte[] { 13, 10 };
/*   0*/  
/* 100*/  private static final byte[] STANDARD_ENCODE_TABLE = new byte[] { 
/* 100*/      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
/* 100*/      75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
/* 100*/      85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
/* 100*/      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
/* 100*/      111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
/* 100*/      121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
/* 100*/      56, 57, 43, 47 };
/*   0*/  
/* 113*/  private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[] { 
/* 113*/      65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
/* 113*/      75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
/* 113*/      85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
/* 113*/      101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
/* 113*/      111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
/* 113*/      121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
/* 113*/      56, 57, 45, 95 };
/*   0*/  
/*   0*/  private static final byte PAD = 61;
/*   0*/  
/* 137*/  private static final byte[] DECODE_TABLE = new byte[] { 
/* 137*/      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
/* 137*/      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
/* 137*/      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
/* 137*/      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
/* 137*/      -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 
/* 137*/      54, 55, 56, 57, 58, 59, 60, 61, -1, -1, 
/* 137*/      -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 
/* 137*/      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
/* 137*/      15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
/* 137*/      25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 
/* 137*/      29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
/* 137*/      39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
/* 137*/      49, 50, 51 };
/*   0*/  
/*   0*/  private static final int MASK_6BITS = 63;
/*   0*/  
/*   0*/  private static final int MASK_8BITS = 255;
/*   0*/  
/*   0*/  private final byte[] encodeTable;
/*   0*/  
/*   0*/  private final int lineLength;
/*   0*/  
/*   0*/  private final byte[] lineSeparator;
/*   0*/  
/*   0*/  private final int decodeSize;
/*   0*/  
/*   0*/  private final int encodeSize;
/*   0*/  
/*   0*/  private byte[] buffer;
/*   0*/  
/*   0*/  private int pos;
/*   0*/  
/*   0*/  private int readPos;
/*   0*/  
/*   0*/  private int currentLinePos;
/*   0*/  
/*   0*/  private int modulus;
/*   0*/  
/*   0*/  private boolean eof;
/*   0*/  
/*   0*/  private int x;
/*   0*/  
/*   0*/  public Base64() {
/* 237*/    this(0);
/*   0*/  }
/*   0*/  
/*   0*/  public Base64(boolean urlSafe) {
/* 256*/    this(76, CHUNK_SEPARATOR, urlSafe);
/*   0*/  }
/*   0*/  
/*   0*/  public Base64(int lineLength) {
/* 278*/    this(lineLength, CHUNK_SEPARATOR);
/*   0*/  }
/*   0*/  
/*   0*/  public Base64(int lineLength, byte[] lineSeparator) {
/* 304*/    this(lineLength, lineSeparator, false);
/*   0*/  }
/*   0*/  
/*   0*/  public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
/* 333*/    if (lineSeparator == null) {
/* 334*/      lineLength = 0;
/* 335*/      lineSeparator = CHUNK_SEPARATOR;
/*   0*/    } 
/* 337*/    this.lineLength = (lineLength > 0) ? (lineLength / 4 * 4) : 0;
/* 338*/    this.lineSeparator = new byte[lineSeparator.length];
/* 339*/    System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
/* 340*/    if (lineLength > 0) {
/* 341*/      this.encodeSize = 4 + lineSeparator.length;
/*   0*/    } else {
/* 343*/      this.encodeSize = 4;
/*   0*/    } 
/* 345*/    this.decodeSize = this.encodeSize - 1;
/* 346*/    if (containsBase64Byte(lineSeparator)) {
/* 347*/      String sep = StringUtils.newStringUtf8(lineSeparator);
/* 348*/      throw new IllegalArgumentException("lineSeperator must not contain base64 characters: [" + sep + "]");
/*   0*/    } 
/* 350*/    this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isUrlSafe() {
/* 360*/    return (this.encodeTable == URL_SAFE_ENCODE_TABLE);
/*   0*/  }
/*   0*/  
/*   0*/  boolean hasData() {
/* 369*/    return (this.buffer != null);
/*   0*/  }
/*   0*/  
/*   0*/  int avail() {
/* 378*/    return (this.buffer != null) ? (this.pos - this.readPos) : 0;
/*   0*/  }
/*   0*/  
/*   0*/  private void resizeBuffer() {
/* 383*/    if (this.buffer == null) {
/* 384*/      this.buffer = new byte[8192];
/* 385*/      this.pos = 0;
/* 386*/      this.readPos = 0;
/*   0*/    } else {
/* 388*/      byte[] b = new byte[this.buffer.length * 2];
/* 389*/      System.arraycopy(this.buffer, 0, b, 0, this.buffer.length);
/* 390*/      this.buffer = b;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  int readResults(byte[] b, int bPos, int bAvail) {
/* 407*/    if (this.buffer != null) {
/* 408*/      int len = Math.min(avail(), bAvail);
/* 409*/      System.arraycopy(this.buffer, this.readPos, b, bPos, len);
/* 410*/      this.readPos += len;
/* 411*/      if (this.readPos >= this.pos) {
/* 412*/          this.buffer = null; 
/*   0*/         }
/* 414*/      return len;
/*   0*/    } 
/* 416*/    return this.eof ? -1 : 0;
/*   0*/  }
/*   0*/  
/*   0*/  void setInitialBuffer(byte[] out, int outPos, int outAvail) {
/* 420*/    if (out != null && out.length == outAvail) {
/* 421*/      this.buffer = out;
/* 422*/      this.pos = outPos;
/* 423*/      this.readPos = outPos;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void encode(byte[] in, int inPos, int inAvail) {
/* 445*/    if (this.eof) {
/*   0*/        return; 
/*   0*/       }
/* 450*/    if (inAvail < 0) {
/* 451*/      this.eof = true;
/* 452*/      if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
/* 453*/          resizeBuffer(); 
/*   0*/         }
/* 455*/      switch (this.modulus) {
/*   0*/        case 1:
/* 457*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 2 & 0x3F];
/* 458*/          this.buffer[this.pos++] = this.encodeTable[this.x << 4 & 0x3F];
/* 460*/          if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 461*/            this.buffer[this.pos++] = 61;
/* 462*/            this.buffer[this.pos++] = 61;
/*   0*/          } 
/*   0*/          break;
/*   0*/        case 2:
/* 467*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 10 & 0x3F];
/* 468*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 4 & 0x3F];
/* 469*/          this.buffer[this.pos++] = this.encodeTable[this.x << 2 & 0x3F];
/* 471*/          if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 472*/              this.buffer[this.pos++] = 61; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/* 478*/      byte b = this.lineSeparator[this.lineSeparator.length - 1];
/* 479*/      if (this.lineLength > 0 && this.pos > 0 && this.buffer[this.pos - 1] != b) {
/* 480*/        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 481*/        this.pos += this.lineSeparator.length;
/*   0*/      } 
/*   0*/    } else {
/* 484*/      for (int i = 0; i < inAvail; i++) {
/* 485*/        if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
/* 486*/            resizeBuffer(); 
/*   0*/           }
/* 488*/        this.modulus = ++this.modulus % 3;
/* 489*/        int b = in[inPos++];
/* 490*/        if (b < 0) {
/* 491*/            b += 256; 
/*   0*/           }
/* 493*/        this.x = (this.x << 8) + b;
/* 494*/        if (0 == this.modulus) {
/* 495*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 18 & 0x3F];
/* 496*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 12 & 0x3F];
/* 497*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 6 & 0x3F];
/* 498*/          this.buffer[this.pos++] = this.encodeTable[this.x & 0x3F];
/* 499*/          this.currentLinePos += 4;
/* 500*/          if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
/* 501*/            System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 502*/            this.pos += this.lineSeparator.length;
/* 503*/            this.currentLinePos = 0;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void decode(byte[] in, int inPos, int inAvail) {
/* 534*/    if (this.eof) {
/*   0*/        return; 
/*   0*/       }
/* 537*/    if (inAvail < 0) {
/* 538*/        this.eof = true; 
/*   0*/       }
/* 540*/    for (int i = 0; i < inAvail; i++) {
/* 541*/      if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
/* 542*/          resizeBuffer(); 
/*   0*/         }
/* 544*/      byte b = in[inPos++];
/* 545*/      if (b == 61) {
/* 547*/        this.eof = true;
/*   0*/        break;
/*   0*/      } 
/* 550*/      if (b >= 0 && b < DECODE_TABLE.length) {
/* 551*/        int result = DECODE_TABLE[b];
/* 552*/        if (result >= 0) {
/* 553*/          this.modulus = ++this.modulus % 4;
/* 554*/          this.x = (this.x << 6) + result;
/* 555*/          if (this.modulus == 0) {
/* 556*/            this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/* 557*/            this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
/* 558*/            this.buffer[this.pos++] = (byte)(this.x & 0xFF);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 568*/    if (this.eof && this.modulus != 0) {
/* 569*/      if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
/* 570*/          resizeBuffer(); 
/*   0*/         }
/* 573*/      this.x <<= 6;
/* 574*/      switch (this.modulus) {
/*   0*/        case 2:
/* 576*/          this.x <<= 6;
/* 577*/          this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/*   0*/          break;
/*   0*/        case 3:
/* 580*/          this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/* 581*/          this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBase64(byte octet) {
/* 596*/    return (octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isArrayByteBase64(byte[] arrayOctet) {
/* 609*/    for (int i = 0; i < arrayOctet.length; i++) {
/* 610*/      if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
/* 611*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 614*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean containsBase64Byte(byte[] arrayOctet) {
/* 625*/    for (int i = 0; i < arrayOctet.length; i++) {
/* 626*/      if (isBase64(arrayOctet[i])) {
/* 627*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 630*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData) {
/* 641*/    return encodeBase64(binaryData, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String encodeBase64String(byte[] binaryData) {
/* 653*/    return StringUtils.newStringUtf8(encodeBase64(binaryData, false));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64URLSafe(byte[] binaryData) {
/* 666*/    return encodeBase64(binaryData, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String encodeBase64URLSafeString(byte[] binaryData) {
/* 679*/    return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64Chunked(byte[] binaryData) {
/* 690*/    return encodeBase64(binaryData, true);
/*   0*/  }
/*   0*/  
/*   0*/  public Object decode(Object pObject) throws DecoderException {
/* 704*/    if (pObject instanceof byte[]) {
/* 705*/        return decode((byte[])pObject); 
/*   0*/       }
/* 706*/    if (pObject instanceof String) {
/* 707*/        return decode((String)pObject); 
/*   0*/       }
/* 709*/    throw new DecoderException("Parameter supplied to Base64 decode is not a byte[] or a String");
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] decode(String pArray) {
/* 722*/    return decode(StringUtils.getBytesUtf8(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] decode(byte[] pArray) {
/* 733*/    reset();
/* 734*/    if (pArray == null || pArray.length == 0) {
/* 735*/        return pArray; 
/*   0*/       }
/* 737*/    decode(pArray, 0, pArray.length);
/* 738*/    decode(pArray, 0, -1);
/* 739*/    byte[] result = new byte[this.pos];
/* 740*/    readResults(result, 0, result.length);
/* 741*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
/* 756*/    return encodeBase64(binaryData, isChunked, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
/* 774*/    return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
/* 794*/    if (binaryData == null || binaryData.length == 0) {
/* 795*/        return binaryData; 
/*   0*/       }
/* 798*/    long len = getEncodeLength(binaryData, 76, CHUNK_SEPARATOR);
/* 799*/    if (len > maxResultSize) {
/* 800*/        throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maxium size of " + maxResultSize); 
/*   0*/       }
/* 806*/    Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
/* 807*/    return b64.encode(binaryData);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] decodeBase64(String base64String) {
/* 819*/    return new Base64().decode(base64String);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] decodeBase64(byte[] base64Data) {
/* 830*/    return new Base64().decode(base64Data);
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] discardWhitespace(byte[] data) {
/* 842*/    byte[] groomedData = new byte[data.length];
/* 843*/    int bytesCopied = 0;
/* 844*/    for (int i = 0; i < data.length; i++) {
/* 845*/      switch (data[i]) {
/*   0*/        case 9:
/*   0*/        case 10:
/*   0*/        case 13:
/*   0*/        case 32:
/*   0*/          break;
/*   0*/        default:
/* 852*/          groomedData[bytesCopied++] = data[i];
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 855*/    byte[] packedData = new byte[bytesCopied];
/* 856*/    System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/* 857*/    return packedData;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isWhiteSpace(byte byteToCheck) {
/* 868*/    switch (byteToCheck) {
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 13:
/*   0*/      case 32:
/* 873*/        return true;
/*   0*/    } 
/* 875*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Object encode(Object pObject) throws EncoderException {
/* 892*/    if (!(pObject instanceof byte[])) {
/* 893*/        throw new EncoderException("Parameter supplied to Base64 encode is not a byte[]"); 
/*   0*/       }
/* 895*/    return encode((byte[])pObject);
/*   0*/  }
/*   0*/  
/*   0*/  public String encodeToString(byte[] pArray) {
/* 907*/    return StringUtils.newStringUtf8(encode(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] encode(byte[] pArray) {
/* 918*/    reset();
/* 919*/    if (pArray == null || pArray.length == 0) {
/* 920*/        return pArray; 
/*   0*/       }
/* 922*/    encode(pArray, 0, pArray.length);
/* 923*/    encode(pArray, 0, -1);
/* 924*/    byte[] buf = new byte[this.pos - this.readPos];
/* 925*/    readResults(buf, 0, buf.length);
/* 926*/    return buf;
/*   0*/  }
/*   0*/  
/*   0*/  private static long getEncodeLength(byte[] pArray, int chunkSize, byte[] chunkSeparator) {
/* 942*/    chunkSize = chunkSize / 4 * 4;
/* 944*/    long len = (pArray.length * 4 / 3);
/* 945*/    long mod = len % 4L;
/* 946*/    if (mod != 0L) {
/* 947*/        len += 4L - mod; 
/*   0*/       }
/* 949*/    if (chunkSize > 0) {
/* 950*/      boolean lenChunksPerfectly = (len % chunkSize == 0L);
/* 951*/      len += len / chunkSize * chunkSeparator.length;
/* 952*/      if (!lenChunksPerfectly) {
/* 953*/          len += chunkSeparator.length; 
/*   0*/         }
/*   0*/    } 
/* 956*/    return len;
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger decodeInteger(byte[] pArray) {
/* 969*/    return new BigInteger(1, decodeBase64(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeInteger(BigInteger bigInt) {
/* 983*/    if (bigInt == null) {
/* 984*/        throw new NullPointerException("encodeInteger called with null parameter"); 
/*   0*/       }
/* 986*/    return encodeBase64(toIntegerBytes(bigInt), false);
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] toIntegerBytes(BigInteger bigInt) {
/* 997*/    int bitlen = bigInt.bitLength();
/* 999*/    bitlen = bitlen + 7 >> 3 << 3;
/*1000*/    byte[] bigBytes = bigInt.toByteArray();
/*1002*/    if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
/*1003*/        return bigBytes; 
/*   0*/       }
/*1006*/    int startSrc = 0;
/*1007*/    int len = bigBytes.length;
/*1010*/    if (bigInt.bitLength() % 8 == 0) {
/*1011*/      startSrc = 1;
/*1012*/      len--;
/*   0*/    } 
/*1014*/    int startDst = bitlen / 8 - len;
/*1015*/    byte[] resizedBytes = new byte[bitlen / 8];
/*1016*/    System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
/*1017*/    return resizedBytes;
/*   0*/  }
/*   0*/  
/*   0*/  private void reset() {
/*1024*/    this.buffer = null;
/*1025*/    this.pos = 0;
/*1026*/    this.readPos = 0;
/*1027*/    this.currentLinePos = 0;
/*1028*/    this.modulus = 0;
/*1029*/    this.eof = false;
/*   0*/  }
/*   0*/}
