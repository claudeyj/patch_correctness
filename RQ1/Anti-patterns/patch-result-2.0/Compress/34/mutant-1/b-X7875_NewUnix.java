/*   0*/package org.apache.commons.compress.archivers.zip;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.math.BigInteger;
/*   0*/import java.util.zip.ZipException;
/*   0*/
/*   0*/public class X7875_NewUnix implements ZipExtraField, Cloneable, Serializable {
/*  57*/  private static final ZipShort HEADER_ID = new ZipShort(30837);
/*   0*/  
/*  58*/  private static final BigInteger ONE_THOUSAND = BigInteger.valueOf(1000L);
/*   0*/  
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  61*/  private int version = 1;
/*   0*/  
/*   0*/  private BigInteger uid;
/*   0*/  
/*   0*/  private BigInteger gid;
/*   0*/  
/*   0*/  public X7875_NewUnix() {
/*  75*/    reset();
/*   0*/  }
/*   0*/  
/*   0*/  public ZipShort getHeaderId() {
/*  84*/    return HEADER_ID;
/*   0*/  }
/*   0*/  
/*   0*/  public long getUID() {
/*  95*/    return ZipUtil.bigToLong(this.uid);
/*   0*/  }
/*   0*/  
/*   0*/  public long getGID() {
/* 105*/    return ZipUtil.bigToLong(this.gid);
/*   0*/  }
/*   0*/  
/*   0*/  public void setUID(long l) {
/* 113*/    this.uid = ZipUtil.longToBig(l);
/*   0*/  }
/*   0*/  
/*   0*/  public void setGID(long l) {
/* 122*/    this.gid = ZipUtil.longToBig(l);
/*   0*/  }
/*   0*/  
/*   0*/  public ZipShort getLocalFileDataLength() {
/* 132*/    int uidSize = (trimLeadingZeroesForceMinLength(this.uid.toByteArray())).length;
/* 133*/    int gidSize = (trimLeadingZeroesForceMinLength(this.gid.toByteArray())).length;
/* 136*/    return new ZipShort(3 + uidSize + gidSize);
/*   0*/  }
/*   0*/  
/*   0*/  public ZipShort getCentralDirectoryLength() {
/* 146*/    this;
/* 146*/    return new ZipShort(0);
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] getLocalFileDataData() {
/* 156*/    byte[] uidBytes = this.uid.toByteArray();
/* 157*/    byte[] gidBytes = this.gid.toByteArray();
/* 162*/    uidBytes = trimLeadingZeroesForceMinLength(uidBytes);
/* 163*/    gidBytes = trimLeadingZeroesForceMinLength(gidBytes);
/* 170*/    byte[] data = new byte[3 + uidBytes.length + gidBytes.length];
/* 173*/    ZipUtil.reverse(uidBytes);
/* 174*/    ZipUtil.reverse(gidBytes);
/* 176*/    int pos = 0;
/* 177*/    data[pos++] = ZipUtil.unsignedIntToSignedByte(this.version);
/* 178*/    data[pos++] = ZipUtil.unsignedIntToSignedByte(uidBytes.length);
/* 179*/    System.arraycopy(uidBytes, 0, data, pos, uidBytes.length);
/* 180*/    pos += uidBytes.length;
/* 181*/    data[pos++] = ZipUtil.unsignedIntToSignedByte(gidBytes.length);
/* 182*/    System.arraycopy(gidBytes, 0, data, pos, gidBytes.length);
/* 183*/    return data;
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] getCentralDirectoryData() {
/* 193*/    return new byte[0];
/*   0*/  }
/*   0*/  
/*   0*/  public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
/* 207*/    reset();
/* 208*/    this.version = ZipUtil.signedByteToUnsignedInt(data[offset++]);
/* 209*/    int uidSize = ZipUtil.signedByteToUnsignedInt(data[offset++]);
/* 210*/    byte[] uidBytes = new byte[uidSize];
/* 211*/    System.arraycopy(data, offset, uidBytes, 0, uidSize);
/* 212*/    offset += uidSize;
/* 213*/    this.uid = new BigInteger(1, ZipUtil.reverse(uidBytes));
/* 215*/    int gidSize = ZipUtil.signedByteToUnsignedInt(data[offset++]);
/* 216*/    byte[] gidBytes = new byte[gidSize];
/* 217*/    System.arraycopy(data, offset, gidBytes, 0, gidSize);
/* 218*/    this.gid = new BigInteger(1, ZipUtil.reverse(gidBytes));
/*   0*/  }
/*   0*/  
/*   0*/  public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {}
/*   0*/  
/*   0*/  private void reset() {
/* 236*/    this.uid = ONE_THOUSAND;
/* 237*/    this.gid = ONE_THOUSAND;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 249*/    return "0x7875 Zip Extra Field: UID=" + this.uid + " GID=" + this.gid;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/* 254*/    return super.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 259*/    if (o instanceof X7875_NewUnix) {
/* 260*/      X7875_NewUnix xf = (X7875_NewUnix)o;
/* 262*/      return (this.version == xf.version && this.uid.equals(xf.uid) && this.gid.equals(xf.gid));
/*   0*/    } 
/* 264*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 269*/    int hc = -1234567 * this.version;
/* 273*/    hc ^= Integer.rotateLeft(this.uid.hashCode(), 16);
/* 274*/    hc ^= this.gid.hashCode();
/* 275*/    return hc;
/*   0*/  }
/*   0*/  
/*   0*/  static byte[] trimLeadingZeroesForceMinLength(byte[] array) {
/* 288*/    if (array == null) {
/* 289*/        return array; 
/*   0*/       }
/* 292*/    int pos = 0;
/*   0*/    byte[] arr$;
/*   0*/    int len$, i$;
/* 293*/    for (arr$ = array, len$ = arr$.length, i$ = 0; i$ < len$; ) {
/* 293*/      byte b = arr$[i$];
/* 294*/      if (b == 0) {
/* 295*/        pos++;
/*   0*/        i$++;
/*   0*/      } 
/*   0*/    } 
/* 338*/    int MIN_LENGTH = 1;
/* 340*/    byte[] trimmedArray = new byte[Math.max(1, array.length - pos)];
/* 341*/    int startPos = trimmedArray.length - array.length - pos;
/* 342*/    System.arraycopy(array, pos, trimmedArray, startPos, trimmedArray.length - startPos);
/* 343*/    return trimmedArray;
/*   0*/  }
/*   0*/}
