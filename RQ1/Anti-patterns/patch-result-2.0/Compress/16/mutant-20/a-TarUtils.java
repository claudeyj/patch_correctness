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
/*  63*/        StringBuffer result = new StringBuffer(length);
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
/* 126*/    byte trailer = buffer[end - 1];
/* 127*/    if (trailer == 0 || trailer == 32) {
/* 128*/      end--;
/*   0*/    } else {
/* 130*/      throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, end - 1, trailer));
/*   0*/    } 
/* 134*/    trailer = buffer[end - 1];
/* 135*/    if (trailer == 0 || trailer == 32) {
/* 136*/        end--; 
/*   0*/       }
/* 139*/    for (; start < end; start++) {
/* 140*/      byte currentByte = buffer[start];
/* 142*/      if (currentByte < 48 || currentByte > 55) {
/* 143*/          throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte)); 
/*   0*/         }
/* 146*/      result = (result << 3L) + (currentByte - 48);
/*   0*/    } 
/* 150*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
/* 173*/    if ((buffer[offset] & 0x80) == 0) {
/* 174*/        return parseOctal(buffer, offset, length); 
/*   0*/       }
/* 176*/    boolean negative = (buffer[offset] == -1);
/* 177*/    if (length < 9) {
/* 178*/        return parseBinaryLong(buffer, offset, length, negative); 
/*   0*/       }
/* 180*/    return parseBinaryBigInteger(buffer, offset, length, negative);
/*   0*/  }
/*   0*/  
/*   0*/  private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
/* 186*/    if (length >= 9) {
/* 187*/        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value"); 
/*   0*/       }
/* 192*/    long val = 0L;
/* 193*/    for (int i = 1; i < length; i++) {
/* 194*/        val = (val << 8L) + (buffer[offset + i] & 0xFF); 
/*   0*/       }
/* 196*/    if (negative) {
/* 198*/      val--;
/* 199*/      val ^= (long)Math.pow(2.0D, ((length - 1) * 8)) - 1L;
/*   0*/    } 
/* 201*/    return negative ? -val : val;
/*   0*/  }
/*   0*/  
/*   0*/  private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
/* 208*/    byte[] remainder = new byte[length - 1];
/* 209*/    System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
/* 210*/    BigInteger val = new BigInteger(remainder);
/* 211*/    if (negative) {
/* 213*/        val = val.add(BigInteger.valueOf(-1L)).not(); 
/*   0*/       }
/* 215*/    if (val.bitLength() > 63) {
/* 216*/        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value"); 
/*   0*/       }
/* 221*/    return negative ? -val.longValue() : val.longValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean parseBoolean(byte[] buffer, int offset) {
/* 235*/    return (buffer[offset] == 1);
/*   0*/  }
/*   0*/  
/*   0*/  private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
/* 241*/    String string = new String(buffer, offset, length);
/* 242*/    string = string.replaceAll("\000", "{NUL}");
/* 243*/    String s = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
/* 244*/    return s;
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length) {
/*   0*/    try {
/* 259*/      return parseName(buffer, offset, length, DEFAULT_ENCODING);
/* 260*/    } catch (IOException ex) {
/*   0*/      try {
/* 262*/        return parseName(buffer, offset, length, FALLBACK_ENCODING);
/* 263*/      } catch (IOException ex2) {
/* 265*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
/* 287*/    int len = length;
/* 288*/    for (; len > 0 && 
/* 289*/      buffer[offset + len - 1] == 0; len--);
/* 293*/    if (len > 0) {
/* 294*/      byte[] b = new byte[len];
/* 295*/      System.arraycopy(buffer, offset, b, 0, len);
/* 296*/      return encoding.decode(b);
/*   0*/    } 
/* 298*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
/*   0*/    try {
/* 318*/      return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
/* 319*/    } catch (IOException ex) {
/*   0*/      try {
/* 321*/        return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
/* 323*/      } catch (IOException ex2) {
/* 325*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding) throws IOException {
/* 351*/    int len = name.length();
/* 352*/    ByteBuffer b = encoding.encode(name);
/* 353*/    while (b.limit() > length && len > 0) {
/* 354*/        b = encoding.encode(name.substring(0, --len)); 
/*   0*/       }
/* 356*/    int limit = b.limit();
/* 357*/    System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
/* 360*/    for (int i = limit; i < length; i++) {
/* 361*/        buf[offset + i] = 0; 
/*   0*/       }
/* 364*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
/* 378*/    int remaining = length;
/* 379*/    remaining--;
/* 380*/    if (value == 0L) {
/* 381*/      buffer[offset + remaining--] = 48;
/*   0*/    } else {
/* 383*/      long val = value;
/* 384*/      for (; remaining >= 0 && val != 0L; remaining--) {
/* 386*/        buffer[offset + remaining] = (byte)(48 + (byte)(int)(val & 0x7L));
/* 387*/        val >>>= 3L;
/*   0*/      } 
/* 390*/      if (val != 0L) {
/* 391*/          throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length); 
/*   0*/         }
/*   0*/    } 
/* 396*/    for (; remaining >= 0; remaining--) {
/* 397*/        buffer[offset + remaining] = 48; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
/* 417*/    int idx = length - 2;
/* 418*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 420*/    buf[offset + idx++] = 32;
/* 421*/    buf[offset + idx] = 0;
/* 423*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
/* 442*/    int idx = length - 1;
/* 444*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 445*/    buf[offset + idx] = 32;
/* 447*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
/* 471*/    long maxAsOctalChar = (length == 8) ? 2097151L : 8589934591L;
/* 473*/    boolean negative = (value < 0L);
/* 474*/    if (!negative && value <= maxAsOctalChar) {
/* 475*/        return formatLongOctalBytes(value, buf, offset, length); 
/*   0*/       }
/* 478*/    if (length < 9) {
/* 479*/        formatLongBinary(value, buf, offset, length, negative); 
/*   0*/       }
/* 481*/    formatBigIntegerBinary(value, buf, offset, length, negative);
/* 483*/    buf[offset] = (byte)(negative ? 255 : 128);
/* 484*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
/* 490*/    int bits = (length - 1) * 8;
/* 491*/    long max = 1L << bits;
/* 492*/    long val = Math.abs(value);
/* 493*/    if (val >= max) {
/* 494*/        throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field."); 
/*   0*/       }
/* 497*/    if (negative) {
/* 498*/      val ^= max - 1L;
/* 499*/      val |= (255 << bits);
/* 500*/      val++;
/*   0*/    } 
/* 502*/    for (int i = offset + length - 1; i >= offset; i--) {
/* 503*/      buf[i] = (byte)(int)val;
/* 504*/      val >>= 8L;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
/* 512*/    BigInteger val = BigInteger.valueOf(value);
/* 513*/    byte[] b = val.toByteArray();
/* 514*/    int len = b.length;
/* 515*/    int off = offset + length - len;
/* 516*/    System.arraycopy(b, 0, buf, off, len);
/* 517*/    byte fill = (byte)(negative ? 255 : 0);
/* 518*/    for (int i = offset + 1; i < off; i++) {
/* 519*/        buf[i] = fill; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
/* 539*/    int idx = length - 2;
/* 540*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 542*/    buf[offset + idx++] = 0;
/* 543*/    buf[offset + idx] = 32;
/* 545*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static long computeCheckSum(byte[] buf) {
/* 555*/    long sum = 0L;
/* 557*/    for (int i = 0; i < buf.length; i++) {
/* 558*/        sum += (0xFF & buf[i]); 
/*   0*/       }
/* 561*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean verifyCheckSum(byte[] header) {
/* 600*/    long storedSum = 0L;
/* 601*/    long unsignedSum = 0L;
/* 602*/    long signedSum = 0L;
/* 604*/    int digits = 0;
/* 605*/    for (int i = 0; i < header.length; i++) {
/* 606*/      byte b = header[i];
/* 607*/      if (148 <= i && i < 156) {
/* 608*/        if (48 <= b && b <= 55 && digits++ < 6) {
/* 609*/          storedSum = storedSum * 8L + b - 48L;
/* 610*/        } else if (digits > 0) {
/* 611*/          digits = 6;
/*   0*/        } 
/* 613*/        b = 32;
/*   0*/      } 
/* 615*/      unsignedSum += (0xFF & b);
/* 616*/      signedSum += b;
/*   0*/    } 
/* 619*/    return (storedSum == unsignedSum || storedSum == signedSum || storedSum > unsignedSum);
/*   0*/  }
/*   0*/}
