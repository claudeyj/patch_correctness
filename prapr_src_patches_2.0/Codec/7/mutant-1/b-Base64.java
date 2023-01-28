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
/* 409*/      if (this.buffer != b) {
/* 410*/        System.arraycopy(this.buffer, this.readPos, b, bPos, len);
/* 411*/        this.readPos += len;
/* 412*/        if (this.readPos >= this.pos) {
/* 413*/            this.buffer = null; 
/*   0*/           }
/*   0*/      } else {
/* 418*/        this.buffer = null;
/*   0*/      } 
/* 420*/      return len;
/*   0*/    } 
/* 422*/    return this.eof ? -1 : 0;
/*   0*/  }
/*   0*/  
/*   0*/  void setInitialBuffer(byte[] out, int outPos, int outAvail) {
/* 439*/    if (out != null && out.length == outAvail) {
/* 440*/      this.buffer = out;
/* 441*/      this.pos = outPos;
/* 442*/      this.readPos = outPos;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void encode(byte[] in, int inPos, int inAvail) {
/* 465*/    if (this.eof) {
/*   0*/        return; 
/*   0*/       }
/* 470*/    if (inAvail < 0) {
/* 471*/      this.eof = true;
/* 472*/      if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
/* 473*/          resizeBuffer(); 
/*   0*/         }
/* 475*/      switch (this.modulus) {
/*   0*/        case 1:
/* 477*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 2 & 0x3F];
/* 478*/          this.buffer[this.pos++] = this.encodeTable[this.x << 4 & 0x3F];
/* 480*/          if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 481*/            this.buffer[this.pos++] = 61;
/* 482*/            this.buffer[this.pos++] = 61;
/*   0*/          } 
/*   0*/          break;
/*   0*/        case 2:
/* 487*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 10 & 0x3F];
/* 488*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 4 & 0x3F];
/* 489*/          this.buffer[this.pos++] = this.encodeTable[this.x << 2 & 0x3F];
/* 491*/          if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 492*/              this.buffer[this.pos++] = 61; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/* 496*/      if (this.lineLength > 0 && this.pos > 0) {
/* 497*/        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 498*/        this.pos += this.lineSeparator.length;
/*   0*/      } 
/*   0*/    } else {
/* 501*/      for (int i = 0; i < inAvail; i++) {
/* 502*/        if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
/* 503*/            resizeBuffer(); 
/*   0*/           }
/* 505*/        this.modulus = ++this.modulus % 3;
/* 506*/        int b = in[inPos++];
/* 507*/        if (b < 0) {
/* 508*/            b += 256; 
/*   0*/           }
/* 510*/        this.x = (this.x << 8) + b;
/* 511*/        if (0 == this.modulus) {
/* 512*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 18 & 0x3F];
/* 513*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 12 & 0x3F];
/* 514*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 6 & 0x3F];
/* 515*/          this.buffer[this.pos++] = this.encodeTable[this.x & 0x3F];
/* 516*/          this.currentLinePos += 4;
/* 517*/          if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
/* 518*/            System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 519*/            this.pos += this.lineSeparator.length;
/* 520*/            this.currentLinePos = 0;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void decode(byte[] in, int inPos, int inAvail) {
/* 551*/    if (this.eof) {
/*   0*/        return; 
/*   0*/       }
/* 554*/    if (inAvail < 0) {
/* 555*/        this.eof = true; 
/*   0*/       }
/* 557*/    for (int i = 0; i < inAvail; i++) {
/* 558*/      if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
/* 559*/          resizeBuffer(); 
/*   0*/         }
/* 561*/      byte b = in[inPos++];
/* 562*/      if (b == 61) {
/* 564*/        this.eof = true;
/*   0*/        break;
/*   0*/      } 
/* 567*/      if (b >= 0 && b < DECODE_TABLE.length) {
/* 568*/        int result = DECODE_TABLE[b];
/* 569*/        if (result >= 0) {
/* 570*/          this.modulus = ++this.modulus % 4;
/* 571*/          this.x = (this.x << 6) + result;
/* 572*/          if (this.modulus == 0) {
/* 573*/            this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/* 574*/            this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
/* 575*/            this.buffer[this.pos++] = (byte)(this.x & 0xFF);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 585*/    if (this.eof && this.modulus != 0) {
/* 586*/      if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
/* 587*/          resizeBuffer(); 
/*   0*/         }
/* 590*/      this.x <<= 6;
/* 591*/      switch (this.modulus) {
/*   0*/        case 2:
/* 593*/          this.x <<= 6;
/* 594*/          this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/*   0*/          break;
/*   0*/        case 3:
/* 597*/          this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/* 598*/          this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBase64(byte octet) {
/* 613*/    return (octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isArrayByteBase64(byte[] arrayOctet) {
/* 626*/    for (int i = 0; i < arrayOctet.length; i++) {
/* 627*/      if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
/* 628*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 631*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean containsBase64Byte(byte[] arrayOctet) {
/* 642*/    for (int i = 0; i < arrayOctet.length; i++) {
/* 643*/      if (isBase64(arrayOctet[i])) {
/* 644*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 647*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData) {
/* 658*/    return encodeBase64(binaryData, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String encodeBase64String(byte[] binaryData) {
/* 670*/    boolean bool = true;
/* 670*/    byte[] arrayOfByte = binaryData;
/* 670*/    return StringUtils.newStringUtf8(encodeBase64(arrayOfByte));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64URLSafe(byte[] binaryData) {
/* 683*/    return encodeBase64(binaryData, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String encodeBase64URLSafeString(byte[] binaryData) {
/* 696*/    return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64Chunked(byte[] binaryData) {
/* 707*/    return encodeBase64(binaryData, true);
/*   0*/  }
/*   0*/  
/*   0*/  public Object decode(Object pObject) throws DecoderException {
/* 721*/    if (pObject instanceof byte[]) {
/* 722*/        return decode((byte[])pObject); 
/*   0*/       }
/* 723*/    if (pObject instanceof String) {
/* 724*/        return decode((String)pObject); 
/*   0*/       }
/* 726*/    throw new DecoderException("Parameter supplied to Base64 decode is not a byte[] or a String");
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] decode(String pArray) {
/* 739*/    return decode(StringUtils.getBytesUtf8(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] decode(byte[] pArray) {
/* 750*/    reset();
/* 751*/    if (pArray == null || pArray.length == 0) {
/* 752*/        return pArray; 
/*   0*/       }
/* 754*/    long len = (pArray.length * 3 / 4);
/* 755*/    byte[] buf = new byte[(int)len];
/* 756*/    setInitialBuffer(buf, 0, buf.length);
/* 757*/    decode(pArray, 0, pArray.length);
/* 758*/    decode(pArray, 0, -1);
/* 766*/    byte[] result = new byte[this.pos];
/* 767*/    readResults(result, 0, result.length);
/* 768*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
/* 783*/    return encodeBase64(binaryData, isChunked, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
/* 801*/    return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
/* 821*/    if (binaryData == null || binaryData.length == 0) {
/* 822*/        return binaryData; 
/*   0*/       }
/* 825*/    long len = getEncodeLength(binaryData, 76, CHUNK_SEPARATOR);
/* 826*/    if (len > maxResultSize) {
/* 827*/        throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maxium size of " + maxResultSize); 
/*   0*/       }
/* 833*/    Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
/* 834*/    return b64.encode(binaryData);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] decodeBase64(String base64String) {
/* 846*/    return new Base64().decode(base64String);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] decodeBase64(byte[] base64Data) {
/* 857*/    return new Base64().decode(base64Data);
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] discardWhitespace(byte[] data) {
/* 869*/    byte[] groomedData = new byte[data.length];
/* 870*/    int bytesCopied = 0;
/* 871*/    for (int i = 0; i < data.length; i++) {
/* 872*/      switch (data[i]) {
/*   0*/        case 9:
/*   0*/        case 10:
/*   0*/        case 13:
/*   0*/        case 32:
/*   0*/          break;
/*   0*/        default:
/* 879*/          groomedData[bytesCopied++] = data[i];
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 882*/    byte[] packedData = new byte[bytesCopied];
/* 883*/    System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/* 884*/    return packedData;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isWhiteSpace(byte byteToCheck) {
/* 895*/    switch (byteToCheck) {
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 13:
/*   0*/      case 32:
/* 900*/        return true;
/*   0*/    } 
/* 902*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Object encode(Object pObject) throws EncoderException {
/* 919*/    if (!(pObject instanceof byte[])) {
/* 920*/        throw new EncoderException("Parameter supplied to Base64 encode is not a byte[]"); 
/*   0*/       }
/* 922*/    return encode((byte[])pObject);
/*   0*/  }
/*   0*/  
/*   0*/  public String encodeToString(byte[] pArray) {
/* 934*/    return StringUtils.newStringUtf8(encode(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] encode(byte[] pArray) {
/* 945*/    reset();
/* 946*/    if (pArray == null || pArray.length == 0) {
/* 947*/        return pArray; 
/*   0*/       }
/* 949*/    long len = getEncodeLength(pArray, this.lineLength, this.lineSeparator);
/* 950*/    byte[] buf = new byte[(int)len];
/* 951*/    setInitialBuffer(buf, 0, buf.length);
/* 952*/    encode(pArray, 0, pArray.length);
/* 953*/    encode(pArray, 0, -1);
/* 955*/    if (this.buffer != buf) {
/* 956*/        readResults(buf, 0, buf.length); 
/*   0*/       }
/* 960*/    if (isUrlSafe() && this.pos < buf.length) {
/* 961*/      byte[] smallerBuf = new byte[this.pos];
/* 962*/      System.arraycopy(buf, 0, smallerBuf, 0, this.pos);
/* 963*/      buf = smallerBuf;
/*   0*/    } 
/* 965*/    return buf;
/*   0*/  }
/*   0*/  
/*   0*/  private static long getEncodeLength(byte[] pArray, int chunkSize, byte[] chunkSeparator) {
/* 981*/    chunkSize = chunkSize / 4 * 4;
/* 983*/    long len = (pArray.length * 4 / 3);
/* 984*/    long mod = len % 4L;
/* 985*/    if (mod != 0L) {
/* 986*/        len += 4L - mod; 
/*   0*/       }
/* 988*/    if (chunkSize > 0) {
/* 989*/      boolean lenChunksPerfectly = (len % chunkSize == 0L);
/* 990*/      len += len / chunkSize * chunkSeparator.length;
/* 991*/      if (!lenChunksPerfectly) {
/* 992*/          len += chunkSeparator.length; 
/*   0*/         }
/*   0*/    } 
/* 995*/    return len;
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger decodeInteger(byte[] pArray) {
/*1008*/    return new BigInteger(1, decodeBase64(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeInteger(BigInteger bigInt) {
/*1022*/    if (bigInt == null) {
/*1023*/        throw new NullPointerException("encodeInteger called with null parameter"); 
/*   0*/       }
/*1025*/    return encodeBase64(toIntegerBytes(bigInt), false);
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] toIntegerBytes(BigInteger bigInt) {
/*1036*/    int bitlen = bigInt.bitLength();
/*1038*/    bitlen = bitlen + 7 >> 3 << 3;
/*1039*/    byte[] bigBytes = bigInt.toByteArray();
/*1041*/    if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
/*1042*/        return bigBytes; 
/*   0*/       }
/*1045*/    int startSrc = 0;
/*1046*/    int len = bigBytes.length;
/*1049*/    if (bigInt.bitLength() % 8 == 0) {
/*1050*/      startSrc = 1;
/*1051*/      len--;
/*   0*/    } 
/*1053*/    int startDst = bitlen / 8 - len;
/*1054*/    byte[] resizedBytes = new byte[bitlen / 8];
/*1055*/    System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
/*1056*/    return resizedBytes;
/*   0*/  }
/*   0*/  
/*   0*/  private void reset() {
/*1063*/    this.buffer = null;
/*1064*/    this.pos = 0;
/*1065*/    this.readPos = 0;
/*1066*/    this.currentLinePos = 0;
/*1067*/    this.modulus = 0;
/*1068*/    this.eof = false;
/*   0*/  }
/*   0*/}
