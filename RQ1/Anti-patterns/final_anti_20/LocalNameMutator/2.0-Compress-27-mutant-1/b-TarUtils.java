/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.math.BigInteger;
/*   0*/import java.nio.ByteBuffer;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*   0*/
/*   0*/public class TarUtils {
/*   0*/  private static final int BYTE_MASK = 255;
/*   0*/  
/*  40*/  static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding(null);
/*   0*/  
/*  47*/  static final ZipEncoding FALLBACK_ENCODING = new ZipEncoding() {
/*   0*/      public boolean canEncode(String name) {
/*  48*/        return true;
/*   0*/      }
/*   0*/      
/*   0*/      public ByteBuffer encode(String name) {
/*  51*/        int length = name.length();
/*  52*/        byte[] buf = new byte[length];
/*  55*/        for (int i = 0; i < length; i++) {
/*  56*/            buf[i] = (byte)name.charAt(i); 
/*   0*/           }
/*  58*/        return ByteBuffer.wrap(buf);
/*   0*/      }
/*   0*/      
/*   0*/      public String decode(byte[] buffer) {
/*  62*/        int length = buffer.length;
/*  63*/        StringBuilder result = new StringBuilder(length);
/*  65*/        for (int i = 0; i < length; i++) {
/*  66*/          byte b = buffer[i];
/*  67*/          if (b == 0) {
/*   0*/              break; 
/*   0*/             }
/*  70*/          result.append((char)(b & 0xFF));
/*   0*/        } 
/*  73*/        return result.toString();
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  public static long parseOctal(byte[] buffer, int offset, int length) {
/* 103*/    long result = 0L;
/* 104*/    int end = offset + length;
/* 105*/    int start = offset;
/* 107*/    if (length < 2) {
/* 108*/        throw new IllegalArgumentException("Length " + length + " must be at least 2"); 
/*   0*/       }
/* 111*/    if (buffer[start] == 0) {
/* 112*/        return 0L; 
/*   0*/       }
/* 116*/    while (start < end && 
/* 117*/      buffer[start] == 32) {
/* 118*/        start++; 
/*   0*/       }
/* 128*/    byte trailer = buffer[end - 1];
/* 129*/    while (start < end && (trailer == 0 || trailer == 32)) {
/* 130*/      end--;
/* 131*/      trailer = buffer[end - 1];
/*   0*/    } 
/* 133*/    if (offset == end) {
/* 134*/        throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, trailer)); 
/*   0*/       }
/* 138*/    for (; start < end; start++) {
/* 139*/      byte currentByte = buffer[start];
/* 141*/      if (currentByte < 48 || currentByte > 55) {
/* 142*/          throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte)); 
/*   0*/         }
/* 145*/      result = (result << 3L) + (currentByte - 48);
/*   0*/    } 
/* 149*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
/* 172*/    if ((buffer[offset] & 0x80) == 0) {
/* 173*/        return parseOctal(buffer, offset, length); 
/*   0*/       }
/* 175*/    boolean negative = (buffer[offset] == -1);
/* 176*/    if (length < 9) {
/* 177*/        return parseBinaryLong(buffer, offset, length, negative); 
/*   0*/       }
/* 179*/    return parseBinaryBigInteger(buffer, offset, length, negative);
/*   0*/  }
/*   0*/  
/*   0*/  private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
/* 185*/    if (length >= 9) {
/* 186*/        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value"); 
/*   0*/       }
/* 191*/    long val = 0L;
/* 192*/    for (int i = 1; i < length; i++) {
/* 193*/        val = (val << 8L) + (buffer[offset + i] & 0xFF); 
/*   0*/       }
/* 195*/    if (negative) {
/* 197*/      val--;
/* 198*/      val ^= (long)Math.pow(2.0D, ((length - 1) * 8)) - 1L;
/*   0*/    } 
/* 200*/    return negative ? -val : val;
/*   0*/  }
/*   0*/  
/*   0*/  private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
/* 207*/    byte[] remainder = new byte[length - 1];
/* 208*/    System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
/* 209*/    BigInteger val = new BigInteger(remainder);
/* 210*/    if (negative) {
/* 212*/        val = val.add(BigInteger.valueOf(-1L)).not(); 
/*   0*/       }
/* 214*/    if (val.bitLength() > 63) {
/* 215*/        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value"); 
/*   0*/       }
/* 220*/    return negative ? -val.longValue() : val.longValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean parseBoolean(byte[] buffer, int offset) {
/* 234*/    return (buffer[offset] == 1);
/*   0*/  }
/*   0*/  
/*   0*/  private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
/* 247*/    String string = new String(buffer, offset, length);
/* 249*/    string = string.replaceAll("\000", "{NUL}");
/* 250*/    String s = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
/* 251*/    return s;
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length) {
/*   0*/    try {
/* 266*/      return parseName(buffer, offset, length, DEFAULT_ENCODING);
/* 267*/    } catch (IOException ex) {
/*   0*/      try {
/* 269*/        return parseName(buffer, offset, length, FALLBACK_ENCODING);
/* 270*/      } catch (IOException ex2) {
/* 272*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
/* 294*/    int len = length;
/* 295*/    for (; len > 0 && 
/* 296*/      buffer[offset + len - 1] == 0; len--);
/* 300*/    if (len > 0) {
/* 301*/      byte[] b = new byte[len];
/* 302*/      System.arraycopy(buffer, offset, b, 0, len);
/* 303*/      return encoding.decode(b);
/*   0*/    } 
/* 305*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
/*   0*/    try {
/* 325*/      return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
/* 326*/    } catch (IOException ex) {
/*   0*/      try {
/* 328*/        return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
/* 330*/      } catch (IOException ex2) {
/* 332*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding) throws IOException {
/* 358*/    int len = name.length();
/* 359*/    ByteBuffer b = encoding.encode(name);
/* 360*/    while (b.limit() > length && len > 0) {
/* 361*/        b = encoding.encode(name.substring(0, --len)); 
/*   0*/       }
/* 363*/    int limit = b.limit() - b.position();
/* 364*/    System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
/* 367*/    for (int i = limit; i < length; i++) {
/* 368*/        buf[offset + i] = 0; 
/*   0*/       }
/* 371*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
/* 385*/    int remaining = length;
/* 386*/    remaining--;
/* 387*/    if (value == 0L) {
/* 388*/      buffer[offset + remaining--] = 48;
/*   0*/    } else {
/* 390*/      long val = value;
/* 391*/      for (; remaining >= 0 && val != 0L; remaining--) {
/* 393*/        buffer[offset + remaining] = (byte)(48 + (byte)(int)(val & 0x7L));
/* 394*/        val >>>= 3L;
/*   0*/      } 
/* 397*/      if (val != 0L) {
/* 398*/          throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length); 
/*   0*/         }
/*   0*/    } 
/* 403*/    for (; remaining >= 0; remaining--) {
/* 404*/        buffer[offset + remaining] = 48; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
/* 424*/    int idx = length - 2;
/* 425*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 427*/    buf[offset + idx++] = 32;
/* 428*/    buf[offset + idx] = 0;
/* 430*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
/* 449*/    int idx = length - 1;
/* 451*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 452*/    buf[offset + idx] = 32;
/* 454*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
/* 478*/    long maxAsOctalChar = (length == 8) ? 2097151L : 8589934591L;
/* 480*/    boolean negative = (value < 0L);
/* 481*/    if (!negative && value <= maxAsOctalChar) {
/* 482*/        return formatLongOctalBytes(value, buf, offset, length); 
/*   0*/       }
/* 485*/    if (length < 9) {
/* 486*/        formatLongBinary(value, buf, offset, length, negative); 
/*   0*/       }
/* 488*/    formatBigIntegerBinary(value, buf, offset, length, negative);
/* 490*/    buf[offset] = (byte)(negative ? 255 : 128);
/* 491*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
/* 497*/    int bits = (length - 1) * 8;
/* 498*/    long max = 1L << bits;
/* 499*/    long val = Math.abs(value);
/* 500*/    if (val >= max) {
/* 501*/        throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field."); 
/*   0*/       }
/* 504*/    if (negative) {
/* 505*/      val ^= max - 1L;
/* 506*/      val |= (255 << bits);
/* 507*/      val++;
/*   0*/    } 
/* 509*/    for (int i = offset + length - 1; i >= offset; i--) {
/* 510*/      buf[i] = (byte)(int)val;
/* 511*/      val >>= 8L;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
/* 519*/    BigInteger val = BigInteger.valueOf(value);
/* 520*/    byte[] b = val.toByteArray();
/* 521*/    int len = b.length;
/* 522*/    int off = offset + length - len;
/* 523*/    System.arraycopy(b, 0, buf, off, len);
/* 524*/    byte fill = (byte)(negative ? 255 : 0);
/* 525*/    for (int i = offset + 1; i < off; i++) {
/* 526*/        buf[i] = fill; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
/* 546*/    int idx = length - 2;
/* 547*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 549*/    buf[offset + idx++] = 0;
/* 550*/    buf[offset + idx] = 32;
/* 552*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static long computeCheckSum(byte[] buf) {
/* 562*/    long sum = 0L;
/* 564*/    for (byte element : buf) {
/* 565*/        sum += (0xFF & element); 
/*   0*/       }
/* 568*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean verifyCheckSum(byte[] header) {
/* 607*/    long storedSum = 0L;
/* 608*/    long unsignedSum = 0L;
/* 609*/    long signedSum = 0L;
/* 611*/    int digits = 0;
/* 612*/    for (int i = 0; i < header.length; i++) {
/* 613*/      byte b = header[i];
/* 614*/      if (148 <= i && i < 156) {
/* 615*/        if (48 <= b && b <= 55 && digits++ < 6) {
/* 616*/          storedSum = storedSum * 8L + b - 48L;
/* 617*/        } else if (digits > 0) {
/* 618*/          digits = 6;
/*   0*/        } 
/* 620*/        b = 32;
/*   0*/      } 
/* 622*/      unsignedSum += (0xFF & b);
/* 623*/      signedSum += b;
/*   0*/    } 
/* 626*/    return (storedSum == unsignedSum || storedSum == signedSum || storedSum > unsignedSum);
/*   0*/  }
/*   0*/}
