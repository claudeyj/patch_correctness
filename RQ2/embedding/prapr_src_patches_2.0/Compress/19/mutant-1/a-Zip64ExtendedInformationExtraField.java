/*   0*/package org.apache.commons.compress.archivers.zip;
/*   0*/
/*   0*/import java.util.zip.ZipException;
/*   0*/
/*   0*/public class Zip64ExtendedInformationExtraField implements ZipExtraField {
/*  78*/  static final ZipShort HEADER_ID = new ZipShort(1);
/*   0*/  
/*   0*/  private static final String LFH_MUST_HAVE_BOTH_SIZES_MSG = "Zip64 extended information must contain both size values in the local file header.";
/*   0*/  
/*  83*/  private static final byte[] EMPTY = new byte[0];
/*   0*/  
/*   0*/  private ZipEightByteInteger size;
/*   0*/  
/*   0*/  private ZipEightByteInteger compressedSize;
/*   0*/  
/*   0*/  private ZipEightByteInteger relativeHeaderOffset;
/*   0*/  
/*   0*/  private ZipLong diskStart;
/*   0*/  
/*   0*/  private byte[] rawCentralDirectoryData;
/*   0*/  
/*   0*/  public Zip64ExtendedInformationExtraField() {}
/*   0*/  
/*   0*/  public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize) {
/* 116*/    this(size, compressedSize, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize, ZipEightByteInteger relativeHeaderOffset, ZipLong diskStart) {
/* 131*/    this.size = size;
/* 132*/    this.compressedSize = compressedSize;
/* 133*/    this.relativeHeaderOffset = relativeHeaderOffset;
/* 134*/    this.diskStart = diskStart;
/*   0*/  }
/*   0*/  
/*   0*/  public ZipShort getHeaderId() {
/* 139*/    return HEADER_ID;
/*   0*/  }
/*   0*/  
/*   0*/  public ZipShort getLocalFileDataLength() {
/* 144*/    return new ZipShort((this.size != null) ? 16 : 0);
/*   0*/  }
/*   0*/  
/*   0*/  public ZipShort getCentralDirectoryLength() {
/* 149*/    return new ZipShort(((this.size != null) ? 8 : 0) + ((this.compressedSize != null) ? 8 : 0) + ((this.relativeHeaderOffset != null) ? 8 : 0) + ((this.diskStart != null) ? 4 : 0));
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] getLocalFileDataData() {
/* 157*/    if (this.size != null || this.compressedSize != null) {
/* 158*/      if (this.size == null || this.compressedSize == null) {
/* 159*/          throw new IllegalArgumentException("Zip64 extended information must contain both size values in the local file header."); 
/*   0*/         }
/* 161*/      byte[] data = new byte[16];
/* 162*/      addSizes(data);
/* 163*/      return data;
/*   0*/    } 
/* 165*/    return EMPTY;
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] getCentralDirectoryData() {
/* 170*/    byte[] data = new byte[getCentralDirectoryLength().getValue()];
/* 171*/    int off = addSizes(data);
/* 172*/    if (this.relativeHeaderOffset != null) {
/* 173*/      System.arraycopy(this.relativeHeaderOffset.getBytes(), 0, data, off, 8);
/* 174*/      off += 8;
/*   0*/    } 
/* 176*/    if (this.diskStart != null) {
/* 177*/      System.arraycopy(this.diskStart.getBytes(), 0, data, off, 4);
/* 178*/      off += 4;
/*   0*/    } 
/* 180*/    return data;
/*   0*/  }
/*   0*/  
/*   0*/  public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
/* 186*/    if (length == 0) {
/*   0*/        return; 
/*   0*/       }
/* 193*/    if (length < 16) {
/* 194*/        throw new ZipException("Zip64 extended information must contain both size values in the local file header."); 
/*   0*/       }
/* 196*/    this.size = new ZipEightByteInteger(buffer, offset);
/* 197*/    offset += 8;
/* 198*/    this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 199*/    offset += 8;
/* 200*/    int remaining = length - 16;
/* 201*/    if (remaining >= 8) {
/* 202*/      this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 203*/      offset += 8;
/* 204*/      remaining -= 8;
/*   0*/    } 
/* 206*/    if (remaining >= 4) {
/* 207*/      this.diskStart = new ZipLong(buffer, offset);
/* 208*/      offset += 4;
/* 209*/      remaining -= 4;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
/* 218*/    this.rawCentralDirectoryData = new byte[length];
/* 219*/    System.arraycopy(buffer, offset, this.rawCentralDirectoryData, 0, length);
/* 227*/    if (length >= 28) {
/* 228*/      parseFromLocalFileData(buffer, offset, length);
/* 229*/    } else if (length == 24) {
/* 230*/      this.size = new ZipEightByteInteger(buffer, offset);
/* 231*/      offset += 8;
/* 232*/      this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 233*/      offset += 8;
/* 234*/      this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 235*/    } else if (length % 8 == 4) {
/* 236*/      this.diskStart = new ZipLong(buffer, offset + length - 4);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void reparseCentralDirectoryData(boolean hasUncompressedSize, boolean hasCompressedSize, boolean hasRelativeHeaderOffset, boolean hasDiskStart) throws ZipException {
/* 254*/    if (this.rawCentralDirectoryData != null) {
/* 255*/      int expectedLength = (hasUncompressedSize ? 8 : 0) + (hasCompressedSize ? 8 : 0) + (hasRelativeHeaderOffset ? 8 : 0) + (hasDiskStart ? 4 : 0);
/* 259*/      if (this.rawCentralDirectoryData.length != expectedLength) {
/* 260*/          throw new ZipException("central directory zip64 extended information extra field's length doesn't match central directory data.  Expected length " + expectedLength + " but is " + this.rawCentralDirectoryData.length); 
/*   0*/         }
/* 267*/      int offset = 0;
/* 268*/      if (hasUncompressedSize) {
/* 269*/        this.size = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 270*/        offset += 8;
/*   0*/      } 
/* 272*/      if (hasCompressedSize) {
/* 273*/        this.compressedSize = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 275*/        offset += 8;
/*   0*/      } 
/* 277*/      if (hasRelativeHeaderOffset) {
/* 278*/        this.relativeHeaderOffset = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 280*/        offset += 8;
/*   0*/      } 
/* 282*/      if (hasDiskStart) {
/* 283*/        this.diskStart = new ZipLong(this.rawCentralDirectoryData, offset);
/* 284*/        offset += 4;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public ZipEightByteInteger getSize() {
/* 293*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSize(ZipEightByteInteger size) {
/* 300*/    this.size = size;
/*   0*/  }
/*   0*/  
/*   0*/  public ZipEightByteInteger getCompressedSize() {
/* 307*/    return this.compressedSize;
/*   0*/  }
/*   0*/  
/*   0*/  public void setCompressedSize(ZipEightByteInteger compressedSize) {
/* 314*/    this.compressedSize = compressedSize;
/*   0*/  }
/*   0*/  
/*   0*/  public ZipEightByteInteger getRelativeHeaderOffset() {
/* 321*/    return this.relativeHeaderOffset;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRelativeHeaderOffset(ZipEightByteInteger rho) {
/* 328*/    this.relativeHeaderOffset = rho;
/*   0*/  }
/*   0*/  
/*   0*/  public ZipLong getDiskStartNumber() {
/* 335*/    return this.diskStart;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDiskStartNumber(ZipLong ds) {
/* 342*/    this.diskStart = ds;
/*   0*/  }
/*   0*/  
/*   0*/  private int addSizes(byte[] data) {
/* 346*/    int off = 0;
/* 347*/    if (this.size != null) {
/* 348*/      System.arraycopy(this.size.getBytes(), 0, data, 0, 8);
/* 349*/      off += 8;
/*   0*/    } 
/* 351*/    if (this.compressedSize != null) {
/* 352*/      System.arraycopy(this.compressedSize.getBytes(), 0, data, off, 8);
/* 353*/      off += 8;
/*   0*/    } 
/* 355*/    return off;
/*   0*/  }
/*   0*/}
