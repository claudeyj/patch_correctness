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
/*  49*/        return true;
/*   0*/      }
/*   0*/      
/*   0*/      public ByteBuffer encode(String name) {
/*  53*/        int length = name.length();
/*  54*/        byte[] buf = new byte[length];
/*  57*/        for (int i = 0; i < length; i++) {
/*  58*/            buf[i] = (byte)name.charAt(i); 
/*   0*/           }
/*  60*/        return ByteBuffer.wrap(buf);
/*   0*/      }
/*   0*/      
/*   0*/      public String decode(byte[] buffer) {
/*  65*/        int length = buffer.length;
/*  66*/        StringBuilder result = new StringBuilder(length);
/*  68*/        for (byte b : buffer) {
/*  69*/          if (b == 0) {
/*   0*/              break; 
/*   0*/             }
/*  72*/          result.append((char)(b & 0xFF));
/*   0*/        } 
/*  75*/        return result.toString();
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  public static long parseOctal(byte[] buffer, int offset, int length) {
/* 105*/    long result = 0L;
/* 106*/    int end = offset + length;
/* 107*/    int start = offset;
/* 109*/    if (length < 2) {
/* 110*/        throw new IllegalArgumentException("Length " + length + " must be at least 2"); 
/*   0*/       }
/* 113*/    if (buffer[start] == 0) {
/* 114*/        return 0L; 
/*   0*/       }
/* 118*/    while (start < end && 
/* 119*/      buffer[start] == 32) {
/* 120*/        start++; 
/*   0*/       }
/* 130*/    byte trailer = buffer[end - 1];
/* 131*/    while (start < end && (trailer == 0 || trailer == 32)) {
/* 132*/      end--;
/* 133*/      trailer = buffer[end - 1];
/*   0*/    } 
/* 136*/    for (; start < end; start++) {
/* 137*/      byte currentByte = buffer[start];
/* 139*/      if (currentByte < 48 || currentByte > 55) {
/* 140*/          throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte)); 
/*   0*/         }
/* 143*/      result = (result << 3L) + (currentByte - 48);
/*   0*/    } 
/* 147*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
/* 170*/    if ((buffer[offset] & 0x80) == 0) {
/* 171*/        return parseOctal(buffer, offset, length); 
/*   0*/       }
/* 173*/    boolean negative = (buffer[offset] == -1);
/* 174*/    if (length < 9) {
/* 175*/        return parseBinaryLong(buffer, offset, length, negative); 
/*   0*/       }
/* 177*/    return parseBinaryBigInteger(buffer, offset, length, negative);
/*   0*/  }
/*   0*/  
/*   0*/  private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
/* 183*/    if (length >= 9) {
/* 184*/        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value"); 
/*   0*/       }
/* 189*/    long val = 0L;
/* 190*/    for (int i = 1; i < length; i++) {
/* 191*/        val = (val << 8L) + (buffer[offset + i] & 0xFF); 
/*   0*/       }
/* 193*/    if (negative) {
/* 195*/      val--;
/* 196*/      val ^= (long)Math.pow(2.0D, (length - 1) * 8.0D) - 1L;
/*   0*/    } 
/* 198*/    return negative ? -val : val;
/*   0*/  }
/*   0*/  
/*   0*/  private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
/* 205*/    byte[] remainder = new byte[length - 1];
/* 206*/    System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
/* 207*/    BigInteger val = new BigInteger(remainder);
/* 208*/    if (negative) {
/* 210*/        val = val.add(BigInteger.valueOf(-1L)).not(); 
/*   0*/       }
/* 212*/    if (val.bitLength() > 63) {
/* 213*/        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value"); 
/*   0*/       }
/* 218*/    return negative ? -val.longValue() : val.longValue();
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean parseBoolean(byte[] buffer, int offset) {
/* 232*/    return (buffer[offset] == 1);
/*   0*/  }
/*   0*/  
/*   0*/  private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
/* 245*/    String string = new String(buffer, offset, length);
/* 247*/    string = string.replaceAll("\000", "{NUL}");
/* 248*/    String s = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
/* 249*/    return s;
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length) {
/*   0*/    try {
/* 264*/      return parseName(buffer, offset, length, DEFAULT_ENCODING);
/* 265*/    } catch (IOException ex) {
/*   0*/      try {
/* 267*/        return parseName(buffer, offset, length, FALLBACK_ENCODING);
/* 268*/      } catch (IOException ex2) {
/* 270*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
/* 293*/    int len = length;
/* 294*/    for (; len > 0 && 
/* 295*/      buffer[offset + len - 1] == 0; len--);
/* 299*/    if (len > 0) {
/* 300*/      byte[] b = new byte[len];
/* 301*/      System.arraycopy(buffer, offset, b, 0, len);
/* 302*/      return encoding.decode(b);
/*   0*/    } 
/* 304*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
/*   0*/    try {
/* 324*/      return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
/* 325*/    } catch (IOException ex) {
/*   0*/      try {
/* 327*/        return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
/* 329*/      } catch (IOException ex2) {
/* 331*/        throw new RuntimeException(ex2);
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
/* 500*/    if (val < 0L || val >= max) {
/* 501*/        throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field."); 
/*   0*/       }
/* 504*/    if (negative) {
/* 505*/      val ^= max - 1L;
/* 506*/      val++;
/* 507*/      val |= 255L << bits;
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
/* 522*/    length - 1;
/* 526*/    int off = offset + length - len;
/* 527*/    System.arraycopy(b, 0, buf, off, len);
/* 528*/    byte fill = (byte)(negative ? 255 : 0);
/* 529*/    for (int i = offset + 1; i < off; i++) {
/* 530*/        buf[i] = fill; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
/* 550*/    int idx = length - 2;
/* 551*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 553*/    buf[offset + idx++] = 0;
/* 554*/    buf[offset + idx] = 32;
/* 556*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static long computeCheckSum(byte[] buf) {
/* 566*/    long sum = 0L;
/* 568*/    for (byte element : buf) {
/* 569*/        sum += (0xFF & element); 
/*   0*/       }
/* 572*/    return sum;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean verifyCheckSum(byte[] header) {
/* 601*/    long storedSum = parseOctal(header, 148, 8);
/* 602*/    long unsignedSum = 0L;
/* 603*/    long signedSum = 0L;
/* 605*/    for (int i = 0; i < header.length; i++) {
/* 606*/      byte b = header[i];
/* 607*/      if (148 <= i && i < 156) {
/* 608*/          b = 32; 
/*   0*/         }
/* 610*/      unsignedSum += (0xFF & b);
/* 611*/      signedSum += b;
/*   0*/    } 
/* 613*/    return (storedSum == unsignedSum || storedSum == signedSum);
/*   0*/  }
/*   0*/}
