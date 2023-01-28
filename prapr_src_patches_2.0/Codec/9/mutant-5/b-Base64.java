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
/*   0*/  void encode(byte[] in, int inPos, int inAvail) {
/* 438*/    if (this.eof) {
/*   0*/        return; 
/*   0*/       }
/* 443*/    if (inAvail < 0) {
/* 444*/      this.eof = true;
/* 445*/      if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
/* 446*/          resizeBuffer(); 
/*   0*/         }
/* 448*/      switch (this.modulus) {
/*   0*/        case 1:
/* 450*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 2 & 0x3F];
/* 451*/          this.buffer[this.pos++] = this.encodeTable[this.x << 4 & 0x3F];
/* 453*/          if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 454*/            this.buffer[this.pos++] = 61;
/* 455*/            this.buffer[this.pos++] = 61;
/*   0*/          } 
/*   0*/          break;
/*   0*/        case 2:
/* 460*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 10 & 0x3F];
/* 461*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 4 & 0x3F];
/* 462*/          this.buffer[this.pos++] = this.encodeTable[this.x << 2 & 0x3F];
/* 464*/          if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 465*/              this.buffer[this.pos++] = 61; 
/*   0*/             }
/*   0*/          break;
/*   0*/      } 
/* 471*/      byte b = this.lineSeparator[this.lineSeparator.length - 1];
/* 472*/      if (this.lineLength > 0 && this.pos > 0 && this.buffer[this.pos - 1] != b) {
/* 473*/        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 474*/        this.pos += this.lineSeparator.length;
/*   0*/      } 
/*   0*/    } else {
/* 477*/      for (int i = 0; i < inAvail; i++) {
/* 478*/        if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
/* 479*/            resizeBuffer(); 
/*   0*/           }
/* 481*/        this.modulus = ++this.modulus % 3;
/* 482*/        int b = in[inPos++];
/* 483*/        if (b < 0) {
/* 484*/            b += 256; 
/*   0*/           }
/* 486*/        this.x = (this.x << 8) + b;
/* 487*/        if (0 == this.modulus) {
/* 488*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 18 & 0x3F];
/* 489*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 12 & 0x3F];
/* 490*/          this.buffer[this.pos++] = this.encodeTable[this.x >> 6 & 0x3F];
/* 491*/          this.buffer[this.pos++] = this.encodeTable[this.x & 0x3F];
/* 492*/          this.currentLinePos += 4;
/* 493*/          if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
/* 494*/            System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
/* 495*/            this.pos += this.lineSeparator.length;
/* 496*/            this.currentLinePos = 0;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  void decode(byte[] in, int inPos, int inAvail) {
/* 527*/    if (this.eof) {
/*   0*/        return; 
/*   0*/       }
/* 530*/    if (inAvail < 0) {
/* 531*/        this.eof = true; 
/*   0*/       }
/* 533*/    for (int i = 0; i < inAvail; i++) {
/* 534*/      if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
/* 535*/          resizeBuffer(); 
/*   0*/         }
/* 537*/      byte b = in[inPos++];
/* 538*/      if (b == 61) {
/* 540*/        this.eof = true;
/*   0*/        break;
/*   0*/      } 
/* 543*/      if (b >= 0 && b < DECODE_TABLE.length) {
/* 544*/        int result = DECODE_TABLE[b];
/* 545*/        if (result >= 0) {
/* 546*/          this.modulus = ++this.modulus % 4;
/* 547*/          this.x = (this.x << 6) + result;
/* 548*/          if (this.modulus == 0) {
/* 549*/            this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
/* 550*/            this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
/* 551*/            this.buffer[this.pos++] = (byte)(this.x & 0xFF);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 561*/    if (this.eof && this.modulus != 0) {
/* 562*/      if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
/* 563*/          resizeBuffer(); 
/*   0*/         }
/* 568*/      switch (this.modulus) {
/*   0*/        case 2:
/* 572*/          this.x >>= 4;
/* 573*/          this.buffer[this.pos++] = (byte)(this.x & 0xFF);
/*   0*/          break;
/*   0*/        case 3:
/* 576*/          this.x >>= 2;
/* 577*/          this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
/* 578*/          this.buffer[this.pos++] = (byte)(this.x & 0xFF);
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBase64(byte octet) {
/* 593*/    return (octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBase64(String base64) {
/* 607*/    return isBase64(StringUtils.getBytesUtf8(base64));
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isArrayByteBase64(byte[] arrayOctet) {
/* 621*/    return isBase64(arrayOctet);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBase64(byte[] arrayOctet) {
/* 635*/    for (int i = 0; i < arrayOctet.length; i++) {
/* 636*/      if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
/* 637*/          return false; 
/*   0*/         }
/*   0*/    } 
/* 640*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean containsBase64Byte(byte[] arrayOctet) {
/* 651*/    for (int i = 0; i < arrayOctet.length; i++) {
/* 652*/      if (isBase64(arrayOctet[i])) {
/* 653*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 656*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData) {
/* 667*/    return encodeBase64(binaryData, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String encodeBase64String(byte[] binaryData) {
/* 682*/    return StringUtils.newStringUtf8(encodeBase64(binaryData, false));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64URLSafe(byte[] binaryData) {
/* 695*/    return encodeBase64(binaryData, false, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String encodeBase64URLSafeString(byte[] binaryData) {
/* 708*/    return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64Chunked(byte[] binaryData) {
/* 719*/    return encodeBase64(binaryData, true);
/*   0*/  }
/*   0*/  
/*   0*/  public Object decode(Object pObject) throws DecoderException {
/* 733*/    if (pObject instanceof byte[]) {
/* 734*/        return decode((byte[])pObject); 
/*   0*/       }
/* 735*/    if (pObject instanceof String) {
/* 736*/        return decode((String)pObject); 
/*   0*/       }
/* 738*/    throw new DecoderException("Parameter supplied to Base64 decode is not a byte[] or a String");
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] decode(String pArray) {
/* 751*/    return decode(StringUtils.getBytesUtf8(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] decode(byte[] pArray) {
/* 762*/    reset();
/* 763*/    if (pArray == null || pArray.length == 0) {
/* 764*/        return pArray; 
/*   0*/       }
/* 766*/    decode(pArray, 0, pArray.length);
/* 767*/    decode(pArray, 0, -1);
/* 768*/    byte[] result = new byte[this.pos];
/* 769*/    readResults(result, 0, result.length);
/* 770*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
/* 785*/    return encodeBase64(binaryData, isChunked, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
/* 803*/    return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
/* 823*/    if (binaryData == null || binaryData.length == 0) {
/* 824*/        return binaryData; 
/*   0*/       }
/* 827*/    long len = getEncodeLength(binaryData, 76, CHUNK_SEPARATOR);
/* 828*/    if (len > maxResultSize) {
/* 829*/        throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maxium size of " + maxResultSize); 
/*   0*/       }
/* 835*/    Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
/* 836*/    return b64.encode(binaryData);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] decodeBase64(String base64String) {
/* 848*/    return new Base64().decode(base64String);
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] decodeBase64(byte[] base64Data) {
/* 859*/    return new Base64().decode(base64Data);
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] discardWhitespace(byte[] data) {
/* 871*/    byte[] groomedData = new byte[data.length];
/* 872*/    int bytesCopied = 0;
/* 873*/    for (int i = 0; i < data.length; i++) {
/* 874*/      switch (data[i]) {
/*   0*/        case 9:
/*   0*/        case 10:
/*   0*/        case 13:
/*   0*/        case 32:
/*   0*/          break;
/*   0*/        default:
/* 881*/          groomedData[bytesCopied++] = data[i];
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 884*/    byte[] packedData = new byte[bytesCopied];
/* 885*/    System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/* 886*/    return packedData;
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean isWhiteSpace(byte byteToCheck) {
/* 897*/    switch (byteToCheck) {
/*   0*/      case 9:
/*   0*/      case 10:
/*   0*/      case 13:
/*   0*/      case 32:
/* 902*/        return true;
/*   0*/    } 
/* 904*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Object encode(Object pObject) throws EncoderException {
/* 921*/    if (!(pObject instanceof byte[])) {
/* 922*/        throw new EncoderException("Parameter supplied to Base64 encode is not a byte[]"); 
/*   0*/       }
/* 924*/    return encode((byte[])pObject);
/*   0*/  }
/*   0*/  
/*   0*/  public String encodeToString(byte[] pArray) {
/* 936*/    return StringUtils.newStringUtf8(encode(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] encode(byte[] pArray) {
/* 947*/    reset();
/* 948*/    if (pArray == null || pArray.length == 0) {
/* 949*/        return pArray; 
/*   0*/       }
/* 951*/    encode(pArray, 0, pArray.length);
/* 952*/    encode(pArray, 0, -1);
/* 953*/    byte[] buf = new byte[this.pos - this.readPos];
/* 954*/    readResults(buf, 0, buf.length);
/* 955*/    return buf;
/*   0*/  }
/*   0*/  
/*   0*/  private static long getEncodeLength(byte[] pArray, int chunkSize, byte[] chunkSeparator) {
/* 971*/    chunkSize = chunkSize / 4 * 4;
/* 973*/    long len = (pArray.length * 4 / 3);
/* 974*/    long mod = len % 4L;
/* 975*/    if (mod != 0L) {
/* 976*/        len += 4L - mod; 
/*   0*/       }
/* 978*/    if (chunkSize > 0) {
/* 979*/      boolean lenChunksPerfectly = (len % chunkSize == 0L);
/* 980*/      len += len / chunkSize * chunkSeparator.length;
/* 981*/      if (lenChunksPerfectly) {
/* 982*/          len += chunkSeparator.length; 
/*   0*/         }
/*   0*/    } 
/* 985*/    return len;
/*   0*/  }
/*   0*/  
/*   0*/  public static BigInteger decodeInteger(byte[] pArray) {
/* 998*/    return new BigInteger(1, decodeBase64(pArray));
/*   0*/  }
/*   0*/  
/*   0*/  public static byte[] encodeInteger(BigInteger bigInt) {
/*1012*/    if (bigInt == null) {
/*1013*/        throw new NullPointerException("encodeInteger called with null parameter"); 
/*   0*/       }
/*1015*/    return encodeBase64(toIntegerBytes(bigInt), false);
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] toIntegerBytes(BigInteger bigInt) {
/*1026*/    int bitlen = bigInt.bitLength();
/*1028*/    bitlen = bitlen + 7 >> 3 << 3;
/*1029*/    byte[] bigBytes = bigInt.toByteArray();
/*1031*/    if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
/*1032*/        return bigBytes; 
/*   0*/       }
/*1035*/    int startSrc = 0;
/*1036*/    int len = bigBytes.length;
/*1039*/    if (bigInt.bitLength() % 8 == 0) {
/*1040*/      startSrc = 1;
/*1041*/      len--;
/*   0*/    } 
/*1043*/    int startDst = bitlen / 8 - len;
/*1044*/    byte[] resizedBytes = new byte[bitlen / 8];
/*1045*/    System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
/*1046*/    return resizedBytes;
/*   0*/  }
/*   0*/  
/*   0*/  private void reset() {
/*1053*/    this.buffer = null;
/*1054*/    this.pos = 0;
/*1055*/    this.readPos = 0;
/*1056*/    this.currentLinePos = 0;
/*1057*/    this.modulus = 0;
/*1058*/    this.eof = false;
/*   0*/  }
/*   0*/}
