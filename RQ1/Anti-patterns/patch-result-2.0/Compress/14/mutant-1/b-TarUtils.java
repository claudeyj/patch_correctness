/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/public class TarUtils {
/*   0*/  private static final int BYTE_MASK = 255;
/*   0*/  
/*   0*/  public static long parseOctal(byte[] buffer, int offset, int length) {
/*  57*/    long result = 0L;
/*  58*/    int end = offset + length;
/*  59*/    int start = offset;
/*  61*/    if (length < 2) {
/*  62*/        throw new IllegalArgumentException("Length " + length + " must be at least 2"); 
/*   0*/       }
/*   0*/    boolean allNUL = true;
/*  66*/    for (int i = start; i < end; i++) {
/*  67*/      if (buffer[offset] != 0) {
/*  68*/        allNUL = false;
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*  72*/    if (allNUL) {
/*  73*/        return 0L; 
/*   0*/       }
/*  77*/    while (start < end && 
/*  78*/      buffer[start] == 32) {
/*  79*/        start++; 
/*   0*/       }
/*  87*/    byte trailer = buffer[end - 1];
/*  88*/    if (trailer == 0 || trailer == 32) {
/*  89*/      end--;
/*   0*/    } else {
/*  91*/      throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, end - 1, trailer));
/*   0*/    } 
/*  95*/    trailer = buffer[end - 1];
/*  96*/    if (trailer == 0 || trailer == 32) {
/*  97*/        end--; 
/*   0*/       }
/* 100*/    for (; start < end; start++) {
/* 101*/      byte currentByte = buffer[start];
/* 103*/      if (currentByte < 48 || currentByte > 55) {
/* 104*/          throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte)); 
/*   0*/         }
/* 107*/      result = (result << 3L) + (currentByte - 48);
/*   0*/    } 
/* 111*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
/* 134*/    if ((buffer[offset] & 0x80) == 0) {
/* 135*/        return parseOctal(buffer, offset, length); 
/*   0*/       }
/* 138*/    long val = (buffer[offset] & Byte.MAX_VALUE);
/* 139*/    for (int i = 1; i < length; i++) {
/* 140*/      if (val >= 36028797018963968L) {
/* 141*/          throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte " + "binary number exceeds maximum signed long value"); 
/*   0*/         }
/* 145*/      val = (val << 8L) + (buffer[offset + i] & 0xFF);
/*   0*/    } 
/* 147*/    return val;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean parseBoolean(byte[] buffer, int offset) {
/* 161*/    return (buffer[offset] == 1);
/*   0*/  }
/*   0*/  
/*   0*/  private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
/* 167*/    String string = new String(buffer, offset, length);
/* 168*/    string = string.replaceAll("\000", "{NUL}");
/* 169*/    String s = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
/* 170*/    return s;
/*   0*/  }
/*   0*/  
/*   0*/  public static String parseName(byte[] buffer, int offset, int length) {
/* 184*/    StringBuffer result = new StringBuffer(length);
/* 185*/    int end = offset + length;
/* 187*/    for (int i = offset; i < end; i++) {
/* 188*/      byte b = buffer[i];
/* 189*/      if (b == 0) {
/*   0*/          break; 
/*   0*/         }
/* 192*/      result.append((char)(b & 0xFF));
/*   0*/    } 
/* 195*/    return result.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
/*   0*/    int i;
/* 217*/    for (i = 0; i < length && i < name.length(); i++) {
/* 218*/        buf[offset + i] = (byte)name.charAt(i); 
/*   0*/       }
/* 222*/    for (; i < length; i++) {
/* 223*/        buf[offset + i] = 0; 
/*   0*/       }
/* 226*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
/* 240*/    int remaining = length;
/* 241*/    remaining--;
/* 242*/    if (value == 0L) {
/* 243*/      buffer[offset + remaining--] = 48;
/*   0*/    } else {
/* 245*/      long val = value;
/* 246*/      for (; remaining >= 0 && val != 0L; remaining--) {
/* 248*/        buffer[offset + remaining] = (byte)(48 + (byte)(int)(val & 0x7L));
/* 249*/        val >>>= 3L;
/*   0*/      } 
/* 252*/      if (val != 0L) {
/* 253*/          throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length); 
/*   0*/         }
/*   0*/    } 
/* 258*/    for (; remaining >= 0; remaining--) {
/* 259*/        buffer[offset + remaining] = 48; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
/* 279*/    int idx = length - 2;
/* 280*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 282*/    buf[offset + idx++] = 32;
/* 283*/    buf[offset + idx] = 0;
/* 285*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
/* 304*/    int idx = length - 1;
/* 306*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 307*/    buf[offset + idx] = 32;
/* 309*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
/* 333*/    long maxAsOctalChar = (length == 8) ? 2097151L : 8589934591L;
/* 335*/    if (value <= maxAsOctalChar) {
/* 336*/        return formatLongOctalBytes(value, buf, offset, length); 
/*   0*/       }
/* 339*/    long val = value;
/* 340*/    for (int i = offset + length - 1; i >= offset; i--) {
/* 341*/      buf[i] = (byte)(int)val;
/* 342*/      val >>= 8L;
/*   0*/    } 
/* 345*/    if (val != 0L || (buf[offset] & 0x80) != 0) {
/* 346*/        throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field."); 
/*   0*/       }
/* 350*/    buf[offset] = (byte)(buf[offset] | 0x80);
/* 351*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
/* 370*/    int idx = length - 2;
/* 371*/    formatUnsignedOctalString(value, buf, offset, idx);
/* 373*/    buf[offset + idx++] = 0;
/* 374*/    buf[offset + idx] = 32;
/* 376*/    return offset + length;
/*   0*/  }
/*   0*/  
/*   0*/  public static long computeCheckSum(byte[] buf) {
/* 386*/    long sum = 0L;
/* 388*/    for (int i = 0; i < buf.length; i++) {
/* 389*/        sum += (0xFF & buf[i]); 
/*   0*/       }
/* 392*/    return sum;
/*   0*/  }
/*   0*/}
