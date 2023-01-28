/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/import java.io.File;
/*   0*/import java.io.IOException;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import org.apache.commons.compress.archivers.ArchiveEntry;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   0*/import org.apache.commons.compress.utils.ArchiveUtils;
/*   0*/
/*   0*/public class TarArchiveEntry implements TarConstants, ArchiveEntry {
/* 186*/  private String magic = "ustar\000";
/*   0*/  
/* 187*/  private String version = "00";
/*   0*/  
/* 188*/  private String name = "";
/*   0*/  
/* 189*/  private String linkName = "";
/*   0*/  
/*   0*/  private int mode;
/*   0*/  
/*   0*/  private int userId;
/*   0*/  
/*   0*/  private int groupId;
/*   0*/  
/*   0*/  private long size;
/*   0*/  
/*   0*/  private long modTime;
/*   0*/  
/*   0*/  private boolean checkSumOK;
/*   0*/  
/*   0*/  private byte linkFlag;
/*   0*/  
/*   0*/  private String userName;
/*   0*/  
/*   0*/  private String groupName;
/*   0*/  
/*   0*/  private int devMajor;
/*   0*/  
/*   0*/  private int devMinor;
/*   0*/  
/*   0*/  private boolean isExtended;
/*   0*/  
/*   0*/  private long realSize;
/*   0*/  
/*   0*/  private File file;
/*   0*/  
/*   0*/  public static final int MAX_NAMELEN = 31;
/*   0*/  
/*   0*/  public static final int DEFAULT_DIR_MODE = 16877;
/*   0*/  
/*   0*/  public static final int DEFAULT_FILE_MODE = 33188;
/*   0*/  
/*   0*/  public static final int MILLIS_PER_SECOND = 1000;
/*   0*/  
/*   0*/  private TarArchiveEntry() {
/* 191*/    String user = System.getProperty("user.name", "");
/* 193*/    if (user.length() > 31) {
/* 194*/        user = user.substring(0, 31); 
/*   0*/       }
/* 197*/    this.userId = 0;
/* 198*/    this.groupId = 0;
/* 199*/    this.userName = user;
/* 200*/    this.groupName = "";
/* 201*/    this.file = null;
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name) {
/* 211*/    this(name, false);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name, boolean preserveLeadingSlashes) {
/* 225*/    this();
/* 227*/    name = normalizeFileName(name, preserveLeadingSlashes);
/* 228*/    boolean isDir = name.endsWith("/");
/* 230*/    this.devMajor = 0;
/* 231*/    this.devMinor = 0;
/* 232*/    this.name = name;
/* 233*/    this.mode = isDir ? 16877 : 33188;
/* 234*/    this.linkFlag = isDir ? 53 : 48;
/* 235*/    this.userId = 0;
/* 236*/    this.groupId = 0;
/* 237*/    this.size = 0L;
/* 238*/    this.modTime = new Date().getTime() / 1000L;
/* 239*/    this.linkName = "";
/* 240*/    this.userName = "";
/* 241*/    this.groupName = "";
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name, byte linkFlag) {
/* 251*/    this(name);
/* 252*/    this.linkFlag = linkFlag;
/* 253*/    if (linkFlag == 76) {
/* 254*/      this.magic = "ustar ";
/* 255*/      this.version = " \000";
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(File file) {
/* 267*/    this(file, normalizeFileName(file.getPath(), false));
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(File file, String fileName) {
/* 278*/    this();
/* 280*/    this.file = file;
/* 282*/    this.linkName = "";
/* 284*/    if (file.isDirectory()) {
/* 285*/      this.mode = 16877;
/* 286*/      this.linkFlag = 53;
/* 288*/      int nameLength = fileName.length();
/* 289*/      if (nameLength == 0 || fileName.charAt(nameLength - 1) != '/') {
/* 290*/        this.name = fileName + "/";
/*   0*/      } else {
/* 292*/        this.name = fileName;
/*   0*/      } 
/* 294*/      this.size = 0L;
/*   0*/    } else {
/* 296*/      this.mode = 33188;
/* 297*/      this.linkFlag = 48;
/* 298*/      this.size = file.length();
/* 299*/      this.name = fileName;
/*   0*/    } 
/* 302*/    this.modTime = file.lastModified() / 1000L;
/* 303*/    this.devMajor = 0;
/* 304*/    this.devMinor = 0;
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(byte[] headerBuf) {
/* 315*/    this();
/* 316*/    parseTarHeader(headerBuf);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding) throws IOException {
/* 330*/    this();
/* 331*/    parseTarHeader(headerBuf, encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(TarArchiveEntry it) {
/* 342*/    return getName().equals(it.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object it) {
/* 354*/    if (it == null || getClass() != it.getClass()) {
/* 355*/        return false; 
/*   0*/       }
/* 357*/    return equals((TarArchiveEntry)it);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 367*/    return getName().hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDescendent(TarArchiveEntry desc) {
/* 379*/    return desc.getName().startsWith(getName());
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/* 388*/    return this.name.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public void setName(String name) {
/* 397*/    this.name = normalizeFileName(name, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void setMode(int mode) {
/* 406*/    this.mode = mode;
/*   0*/  }
/*   0*/  
/*   0*/  public String getLinkName() {
/* 415*/    return this.linkName.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public void setLinkName(String link) {
/* 426*/    this.linkName = link;
/*   0*/  }
/*   0*/  
/*   0*/  public int getUserId() {
/* 435*/    return this.userId;
/*   0*/  }
/*   0*/  
/*   0*/  public void setUserId(int userId) {
/* 444*/    this.userId = userId;
/*   0*/  }
/*   0*/  
/*   0*/  public int getGroupId() {
/* 453*/    return this.groupId;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroupId(int groupId) {
/* 462*/    this.groupId = groupId;
/*   0*/  }
/*   0*/  
/*   0*/  public String getUserName() {
/* 471*/    return this.userName.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public void setUserName(String userName) {
/* 480*/    this.userName = userName;
/*   0*/  }
/*   0*/  
/*   0*/  public String getGroupName() {
/* 489*/    return this.groupName.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroupName(String groupName) {
/* 498*/    this.groupName = groupName;
/*   0*/  }
/*   0*/  
/*   0*/  public void setIds(int userId, int groupId) {
/* 508*/    setUserId(userId);
/* 509*/    setGroupId(groupId);
/*   0*/  }
/*   0*/  
/*   0*/  public void setNames(String userName, String groupName) {
/* 519*/    setUserName(userName);
/* 520*/    setGroupName(groupName);
/*   0*/  }
/*   0*/  
/*   0*/  public void setModTime(long time) {
/* 530*/    this.modTime = time / 1000L;
/*   0*/  }
/*   0*/  
/*   0*/  public void setModTime(Date time) {
/* 539*/    this.modTime = time.getTime() / 1000L;
/*   0*/  }
/*   0*/  
/*   0*/  public Date getModTime() {
/* 548*/    return new Date(this.modTime * 1000L);
/*   0*/  }
/*   0*/  
/*   0*/  public Date getLastModifiedDate() {
/* 553*/    return getModTime();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCheckSumOK() {
/* 564*/    return this.checkSumOK;
/*   0*/  }
/*   0*/  
/*   0*/  public File getFile() {
/* 573*/    return this.file;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMode() {
/* 582*/    return this.mode;
/*   0*/  }
/*   0*/  
/*   0*/  public long getSize() {
/* 591*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSize(long size) {
/* 601*/    if (size < 0L) {
/* 602*/        throw new IllegalArgumentException("Size is out of range: " + size); 
/*   0*/       }
/* 604*/    this.size = size;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDevMajor() {
/* 614*/    return this.devMajor;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDevMajor(int devNo) {
/* 625*/    if (devNo < 0) {
/* 626*/        throw new IllegalArgumentException("Major device number is out of range: " + devNo); 
/*   0*/       }
/* 629*/    this.devMajor = devNo;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDevMinor() {
/* 639*/    return this.devMinor;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDevMinor(int devNo) {
/* 650*/    if (devNo < 0) {
/* 651*/        throw new IllegalArgumentException("Minor device number is out of range: " + devNo); 
/*   0*/       }
/* 654*/    this.devMinor = devNo;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExtended() {
/* 664*/    return this.isExtended;
/*   0*/  }
/*   0*/  
/*   0*/  public long getRealSize() {
/* 673*/    return this.realSize;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGNUSparse() {
/* 682*/    return (this.linkFlag == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGNULongNameEntry() {
/* 691*/    return (this.linkFlag == 76 && this.name.equals("././@LongLink"));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isPaxHeader() {
/* 704*/    return (this.linkFlag == 120 || this.linkFlag == 88);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGlobalPaxHeader() {
/* 716*/    return (this.linkFlag == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDirectory() {
/* 725*/    if (this.file != null) {
/* 726*/        return this.file.isDirectory(); 
/*   0*/       }
/* 729*/    if (this.linkFlag == 53) {
/* 730*/        return true; 
/*   0*/       }
/* 733*/    if (getName().endsWith("/")) {
/* 734*/        return true; 
/*   0*/       }
/* 737*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFile() {
/* 746*/    if (this.file != null) {
/* 747*/        return this.file.isFile(); 
/*   0*/       }
/* 749*/    if (this.linkFlag == 0 || this.linkFlag == 48) {
/* 750*/        return true; 
/*   0*/       }
/* 752*/    return !getName().endsWith("/");
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSymbolicLink() {
/* 761*/    return (this.linkFlag == 50);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLink() {
/* 770*/    return (this.linkFlag == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCharacterDevice() {
/* 779*/    return (this.linkFlag == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlockDevice() {
/* 788*/    return (this.linkFlag == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFIFO() {
/* 797*/    return (this.linkFlag == 54);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry[] getDirectoryEntries() {
/* 807*/    if (this.file == null || !this.file.isDirectory()) {
/* 808*/        return new TarArchiveEntry[0]; 
/*   0*/       }
/* 811*/    String[] list = this.file.list();
/* 812*/    TarArchiveEntry[] result = new TarArchiveEntry[list.length];
/* 814*/    for (int i = 0; i < list.length; i++) {
/* 815*/        result[i] = new TarArchiveEntry(new File(this.file, list[i])); 
/*   0*/       }
/* 818*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeEntryHeader(byte[] outbuf) {
/*   0*/    try {
/* 830*/      writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
/* 831*/    } catch (IOException ex) {
/*   0*/      try {
/* 833*/        writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
/* 834*/      } catch (IOException ex2) {
/* 836*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode) throws IOException {
/* 853*/    int offset = 0;
/* 855*/    offset = TarUtils.formatNameBytes(this.name, outbuf, offset, 100, encoding);
/* 857*/    offset = writeEntryHeaderField(this.mode, outbuf, offset, 8, starMode);
/* 858*/    offset = writeEntryHeaderField(this.userId, outbuf, offset, 8, starMode);
/* 860*/    offset = writeEntryHeaderField(this.groupId, outbuf, offset, 8, starMode);
/* 862*/    offset = writeEntryHeaderField(this.size, outbuf, offset, 12, starMode);
/* 863*/    offset = writeEntryHeaderField(this.modTime, outbuf, offset, 12, starMode);
/* 866*/    int csOffset = offset;
/* 868*/    for (int c = 0; c < 8; c++) {
/* 869*/        outbuf[offset++] = 32; 
/*   0*/       }
/* 872*/    outbuf[offset++] = this.linkFlag;
/* 873*/    offset = TarUtils.formatNameBytes(this.linkName, outbuf, offset, 100, encoding);
/* 875*/    offset = TarUtils.formatNameBytes(this.magic, outbuf, offset, 6);
/* 876*/    offset = TarUtils.formatNameBytes(this.version, outbuf, offset, 2);
/* 877*/    offset = TarUtils.formatNameBytes(this.userName, outbuf, offset, 32, encoding);
/* 879*/    offset = TarUtils.formatNameBytes(this.groupName, outbuf, offset, 32, encoding);
/* 881*/    offset = writeEntryHeaderField(this.devMajor, outbuf, offset, 8, starMode);
/* 883*/    offset = writeEntryHeaderField(this.devMinor, outbuf, offset, 8, starMode);
/* 886*/    while (offset < outbuf.length) {
/* 887*/        outbuf[offset++] = 0; 
/*   0*/       }
/* 890*/    long chk = TarUtils.computeCheckSum(outbuf);
/* 892*/    TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, 8);
/*   0*/  }
/*   0*/  
/*   0*/  private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode) {
/* 897*/    if (!starMode && (value < 0L || value >= 1L << 3 * (length - 1))) {
/* 902*/        return TarUtils.formatLongOctalBytes(0L, outbuf, offset, length); 
/*   0*/       }
/* 904*/    return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
/*   0*/  }
/*   0*/  
/*   0*/  public void parseTarHeader(byte[] header) {
/*   0*/    try {
/* 916*/      parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
/* 917*/    } catch (IOException ex) {
/*   0*/      try {
/* 919*/        parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true);
/* 920*/      } catch (IOException ex2) {
/* 922*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void parseTarHeader(byte[] header, ZipEncoding encoding) throws IOException {
/* 938*/    parseTarHeader(header, encoding, false);
/*   0*/  }
/*   0*/  
/*   0*/  private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle) throws IOException {
/*   0*/    String prefix;
/* 944*/    int offset = 0;
/* 946*/    this.name = oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding);
/* 948*/    offset += 100;
/* 949*/    this.mode = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/* 950*/    offset += 8;
/* 951*/    this.userId = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/* 952*/    offset += 8;
/* 953*/    this.groupId = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/* 954*/    offset += 8;
/* 955*/    this.size = TarUtils.parseOctalOrBinary(header, offset, 12);
/* 956*/    offset += 12;
/* 957*/    this.modTime = TarUtils.parseOctalOrBinary(header, offset, 12);
/* 958*/    offset += 12;
/* 959*/    this.checkSumOK = TarUtils.verifyCheckSum(header);
/* 960*/    offset += 8;
/* 961*/    this.linkFlag = header[offset++];
/* 962*/    this.linkName = oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding);
/* 964*/    offset += 100;
/* 965*/    this.magic = TarUtils.parseName(header, offset, 6);
/* 966*/    offset += 6;
/* 967*/    this.version = TarUtils.parseName(header, offset, 2);
/* 968*/    offset += 2;
/* 969*/    this.userName = oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding);
/* 971*/    offset += 32;
/* 972*/    this.groupName = oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding);
/* 974*/    offset += 32;
/* 975*/    this.devMajor = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/* 976*/    offset += 8;
/* 977*/    this.devMinor = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/* 978*/    offset += 8;
/* 980*/    int type = evaluateType(header);
/* 981*/    switch (type) {
/*   0*/      case 2:
/* 983*/        offset += 12;
/* 984*/        offset += 12;
/* 985*/        offset += 12;
/* 986*/        offset += 4;
/* 987*/        offset++;
/* 988*/        offset += 96;
/* 989*/        this.isExtended = TarUtils.parseBoolean(header, offset);
/* 990*/        offset++;
/* 991*/        this.realSize = TarUtils.parseOctal(header, offset, 12);
/* 992*/        offset += 12;
/*   0*/        break;
/*   0*/      default:
/* 997*/        prefix = oldStyle ? TarUtils.parseName(header, offset, 155) : TarUtils.parseName(header, offset, 155, encoding);
/*1002*/        if (isDirectory() && !this.name.endsWith("/")) {
/*1003*/            this.name += "/"; 
/*   0*/           }
/*1005*/        if (prefix.length() > 0) {
/*1006*/          String str = prefix;
/*1006*/          this.name = new StringBuilder().append((CharSequence)null, offset, offset).append("/").append(this.name).toString();
/*   0*/        } 
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static String normalizeFileName(String fileName, boolean preserveLeadingSlashes) {
/*1018*/    String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
/*1020*/    if (osname != null) {
/*1025*/        if (osname.startsWith("windows")) {
/*1026*/          if (fileName.length() > 2) {
/*1027*/            char ch1 = fileName.charAt(0);
/*1028*/            char ch2 = fileName.charAt(1);
/*1030*/            if (ch2 == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z'))) {
/*1033*/                fileName = fileName.substring(2); 
/*   0*/               }
/*   0*/          } 
/*1036*/        } else if (osname.indexOf("netware") > -1) {
/*1037*/          int colon = fileName.indexOf(':');
/*1038*/          if (colon != -1) {
/*1039*/              fileName = fileName.substring(colon + 1); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/*1044*/    fileName = fileName.replace(File.separatorChar, '/');
/*1049*/    while (!preserveLeadingSlashes && fileName.startsWith("/")) {
/*1050*/        fileName = fileName.substring(1); 
/*   0*/       }
/*1052*/    return fileName;
/*   0*/  }
/*   0*/  
/*   0*/  private int evaluateType(byte[] header) {
/*1062*/    if (ArchiveUtils.matchAsciiBuffer("ustar ", header, 257, 6)) {
/*1063*/        return 2; 
/*   0*/       }
/*1065*/    if (ArchiveUtils.matchAsciiBuffer("ustar\000", header, 257, 6)) {
/*1066*/        return 3; 
/*   0*/       }
/*1068*/    return 0;
/*   0*/  }
/*   0*/}
