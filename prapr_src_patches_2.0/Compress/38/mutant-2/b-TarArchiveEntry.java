/*   0*/package org.apache.commons.compress.archivers.tar;
/*   0*/
/*   0*/import java.io.File;
/*   0*/import java.io.IOException;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import org.apache.commons.compress.archivers.ArchiveEntry;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   0*/import org.apache.commons.compress.utils.ArchiveUtils;
/*   0*/
/*   0*/public class TarArchiveEntry implements TarConstants, ArchiveEntry {
/* 146*/  private static final TarArchiveEntry[] EMPTY_TAR_ARCHIVE_ENTRIES = new TarArchiveEntry[0];
/*   0*/  
/* 149*/  private String name = "";
/*   0*/  
/*   0*/  private boolean preserveLeadingSlashes;
/*   0*/  
/*   0*/  private int mode;
/*   0*/  
/* 158*/  private long userId = 0L;
/*   0*/  
/* 161*/  private long groupId = 0L;
/*   0*/  
/* 164*/  private long size = 0L;
/*   0*/  
/*   0*/  private long modTime;
/*   0*/  
/*   0*/  private boolean checkSumOK;
/*   0*/  
/*   0*/  private byte linkFlag;
/*   0*/  
/* 176*/  private String linkName = "";
/*   0*/  
/* 179*/  private String magic = "ustar\000";
/*   0*/  
/* 181*/  private String version = "00";
/*   0*/  
/*   0*/  private String userName;
/*   0*/  
/* 187*/  private String groupName = "";
/*   0*/  
/* 190*/  private int devMajor = 0;
/*   0*/  
/* 193*/  private int devMinor = 0;
/*   0*/  
/*   0*/  private boolean isExtended;
/*   0*/  
/*   0*/  private long realSize;
/*   0*/  
/*   0*/  private boolean paxGNUSparse;
/*   0*/  
/*   0*/  private boolean starSparse;
/*   0*/  
/*   0*/  private final File file;
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
/* 226*/    String user = System.getProperty("user.name", "");
/* 228*/    if (user.length() > 31) {
/* 229*/        user = user.substring(0, 31); 
/*   0*/       }
/* 232*/    this.userName = user;
/* 233*/    this.file = null;
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name) {
/* 243*/    this(name, false);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name, boolean preserveLeadingSlashes) {
/* 257*/    this();
/* 259*/    this.preserveLeadingSlashes = preserveLeadingSlashes;
/* 261*/    name = normalizeFileName(name, preserveLeadingSlashes);
/* 262*/    boolean isDir = name.endsWith("/");
/* 264*/    this.name = name;
/* 265*/    this.mode = isDir ? 16877 : 33188;
/* 266*/    this.linkFlag = isDir ? 53 : 48;
/* 267*/    this.modTime = new Date().getTime() / 1000L;
/* 268*/    this.userName = "";
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name, byte linkFlag) {
/* 278*/    this(name, linkFlag, false);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(String name, byte linkFlag, boolean preserveLeadingSlashes) {
/* 292*/    this(name, preserveLeadingSlashes);
/* 293*/    this.linkFlag = linkFlag;
/* 294*/    if (linkFlag == 76) {
/* 295*/      this.magic = "ustar ";
/* 296*/      this.version = " \000";
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(File file) {
/* 308*/    this(file, file.getPath());
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(File file, String fileName) {
/* 319*/    String normalizedName = normalizeFileName(fileName, false);
/* 320*/    this.file = file;
/* 322*/    if (file.isDirectory()) {
/* 323*/      this.mode = 16877;
/* 324*/      this.linkFlag = 53;
/* 326*/      int nameLength = normalizedName.length();
/* 327*/      if (nameLength == 0 || normalizedName.charAt(nameLength - 1) != '/') {
/* 328*/        this.name = normalizedName + "/";
/*   0*/      } else {
/* 330*/        this.name = normalizedName;
/*   0*/      } 
/*   0*/    } else {
/* 333*/      this.mode = 33188;
/* 334*/      this.linkFlag = 48;
/* 335*/      this.size = file.length();
/* 336*/      this.name = normalizedName;
/*   0*/    } 
/* 339*/    this.modTime = file.lastModified() / 1000L;
/* 340*/    this.userName = "";
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(byte[] headerBuf) {
/* 351*/    this();
/* 352*/    parseTarHeader(headerBuf);
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding) throws IOException {
/* 367*/    this();
/* 368*/    parseTarHeader(headerBuf, encoding);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(TarArchiveEntry it) {
/* 379*/    return getName().equals(it.getName());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object it) {
/* 391*/    if (it == null || getClass() != it.getClass()) {
/* 392*/        return false; 
/*   0*/       }
/* 394*/    return equals((TarArchiveEntry)it);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 404*/    return getName().hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDescendent(TarArchiveEntry desc) {
/* 416*/    return desc.getName().startsWith(getName());
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/* 426*/    return this.name;
/*   0*/  }
/*   0*/  
/*   0*/  public void setName(String name) {
/* 435*/    this.name = normalizeFileName(name, this.preserveLeadingSlashes);
/*   0*/  }
/*   0*/  
/*   0*/  public void setMode(int mode) {
/* 444*/    this.mode = mode;
/*   0*/  }
/*   0*/  
/*   0*/  public String getLinkName() {
/* 453*/    return this.linkName;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLinkName(String link) {
/* 464*/    this.linkName = link;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public int getUserId() {
/* 476*/    return (int)(this.userId & 0xFFFFFFFFFFFFFFFFL);
/*   0*/  }
/*   0*/  
/*   0*/  public void setUserId(int userId) {
/* 485*/    setUserId(userId);
/*   0*/  }
/*   0*/  
/*   0*/  public long getLongUserId() {
/* 495*/    return this.userId;
/*   0*/  }
/*   0*/  
/*   0*/  public void setUserId(long userId) {
/* 505*/    this.userId = userId;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public int getGroupId() {
/* 517*/    return (int)(this.groupId & 0xFFFFFFFFFFFFFFFFL);
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroupId(int groupId) {
/* 526*/    setGroupId(groupId);
/*   0*/  }
/*   0*/  
/*   0*/  public long getLongGroupId() {
/* 536*/    return this.groupId;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroupId(long groupId) {
/* 546*/    this.groupId = groupId;
/*   0*/  }
/*   0*/  
/*   0*/  public String getUserName() {
/* 555*/    return this.userName;
/*   0*/  }
/*   0*/  
/*   0*/  public void setUserName(String userName) {
/* 564*/    this.userName = userName;
/*   0*/  }
/*   0*/  
/*   0*/  public String getGroupName() {
/* 573*/    return this.groupName;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroupName(String groupName) {
/* 582*/    this.groupName = groupName;
/*   0*/  }
/*   0*/  
/*   0*/  public void setIds(int userId, int groupId) {
/* 592*/    setUserId(userId);
/* 593*/    setGroupId(groupId);
/*   0*/  }
/*   0*/  
/*   0*/  public void setNames(String userName, String groupName) {
/* 603*/    setUserName(userName);
/* 604*/    setGroupName(groupName);
/*   0*/  }
/*   0*/  
/*   0*/  public void setModTime(long time) {
/* 614*/    this.modTime = time / 1000L;
/*   0*/  }
/*   0*/  
/*   0*/  public void setModTime(Date time) {
/* 623*/    this.modTime = time.getTime() / 1000L;
/*   0*/  }
/*   0*/  
/*   0*/  public Date getModTime() {
/* 632*/    return new Date(this.modTime * 1000L);
/*   0*/  }
/*   0*/  
/*   0*/  public Date getLastModifiedDate() {
/* 637*/    return getModTime();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCheckSumOK() {
/* 648*/    return this.checkSumOK;
/*   0*/  }
/*   0*/  
/*   0*/  public File getFile() {
/* 657*/    return this.file;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMode() {
/* 666*/    return this.mode;
/*   0*/  }
/*   0*/  
/*   0*/  public long getSize() {
/* 676*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSize(long size) {
/* 686*/    if (size < 0L) {
/* 687*/        throw new IllegalArgumentException("Size is out of range: " + size); 
/*   0*/       }
/* 689*/    this.size = size;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDevMajor() {
/* 699*/    return this.devMajor;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDevMajor(int devNo) {
/* 710*/    if (devNo < 0) {
/* 711*/        throw new IllegalArgumentException("Major device number is out of range: " + devNo); 
/*   0*/       }
/* 714*/    this.devMajor = devNo;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDevMinor() {
/* 724*/    return this.devMinor;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDevMinor(int devNo) {
/* 735*/    if (devNo < 0) {
/* 736*/        throw new IllegalArgumentException("Minor device number is out of range: " + devNo); 
/*   0*/       }
/* 739*/    this.devMinor = devNo;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isExtended() {
/* 749*/    return this.isExtended;
/*   0*/  }
/*   0*/  
/*   0*/  public long getRealSize() {
/* 758*/    return this.realSize;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGNUSparse() {
/* 767*/    return (isOldGNUSparse() || isPaxGNUSparse());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isOldGNUSparse() {
/* 778*/    return (this.linkFlag == 83);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isPaxGNUSparse() {
/* 789*/    return this.paxGNUSparse;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isStarSparse() {
/* 799*/    return this.starSparse;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGNULongLinkEntry() {
/* 808*/    return (this.linkFlag == 75);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGNULongNameEntry() {
/* 817*/    return (this.linkFlag == 76);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isPaxHeader() {
/* 829*/    return (this.linkFlag == 120 || this.linkFlag == 88);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isGlobalPaxHeader() {
/* 841*/    return (this.linkFlag == 103);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDirectory() {
/* 851*/    if (this.file != null) {
/* 852*/        return this.file.isDirectory(); 
/*   0*/       }
/* 855*/    if (this.linkFlag == 53) {
/* 856*/        return true; 
/*   0*/       }
/* 859*/    if (getName().endsWith("/")) {
/* 860*/      true;
/* 860*/      return false;
/*   0*/    } 
/* 863*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFile() {
/* 873*/    if (this.file != null) {
/* 874*/        return this.file.isFile(); 
/*   0*/       }
/* 876*/    if (this.linkFlag == 0 || this.linkFlag == 48) {
/* 877*/        return true; 
/*   0*/       }
/* 879*/    return !getName().endsWith("/");
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSymbolicLink() {
/* 889*/    return (this.linkFlag == 50);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLink() {
/* 899*/    return (this.linkFlag == 49);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCharacterDevice() {
/* 909*/    return (this.linkFlag == 51);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBlockDevice() {
/* 919*/    return (this.linkFlag == 52);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isFIFO() {
/* 929*/    return (this.linkFlag == 54);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSparse() {
/* 939*/    return (isGNUSparse() || isStarSparse());
/*   0*/  }
/*   0*/  
/*   0*/  public TarArchiveEntry[] getDirectoryEntries() {
/* 949*/    if (this.file == null || !this.file.isDirectory()) {
/* 950*/        return EMPTY_TAR_ARCHIVE_ENTRIES; 
/*   0*/       }
/* 953*/    String[] list = this.file.list();
/* 954*/    if (list == null) {
/* 955*/        return EMPTY_TAR_ARCHIVE_ENTRIES; 
/*   0*/       }
/* 957*/    TarArchiveEntry[] result = new TarArchiveEntry[list.length];
/* 959*/    for (int i = 0; i < result.length; i++) {
/* 960*/        result[i] = new TarArchiveEntry(new File(this.file, list[i])); 
/*   0*/       }
/* 963*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeEntryHeader(byte[] outbuf) {
/*   0*/    try {
/* 975*/      writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
/* 976*/    } catch (IOException ex) {
/*   0*/      try {
/* 978*/        writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
/* 979*/      } catch (IOException ex2) {
/* 981*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode) throws IOException {
/* 999*/    int offset = 0;
/*1001*/    offset = TarUtils.formatNameBytes(this.name, outbuf, offset, 100, encoding);
/*1003*/    offset = writeEntryHeaderField(this.mode, outbuf, offset, 8, starMode);
/*1004*/    offset = writeEntryHeaderField(this.userId, outbuf, offset, 8, starMode);
/*1006*/    offset = writeEntryHeaderField(this.groupId, outbuf, offset, 8, starMode);
/*1008*/    offset = writeEntryHeaderField(this.size, outbuf, offset, 12, starMode);
/*1009*/    offset = writeEntryHeaderField(this.modTime, outbuf, offset, 12, starMode);
/*1012*/    int csOffset = offset;
/*1014*/    for (int c = 0; c < 8; c++) {
/*1015*/        outbuf[offset++] = 32; 
/*   0*/       }
/*1018*/    outbuf[offset++] = this.linkFlag;
/*1019*/    offset = TarUtils.formatNameBytes(this.linkName, outbuf, offset, 100, encoding);
/*1021*/    offset = TarUtils.formatNameBytes(this.magic, outbuf, offset, 6);
/*1022*/    offset = TarUtils.formatNameBytes(this.version, outbuf, offset, 2);
/*1023*/    offset = TarUtils.formatNameBytes(this.userName, outbuf, offset, 32, encoding);
/*1025*/    offset = TarUtils.formatNameBytes(this.groupName, outbuf, offset, 32, encoding);
/*1027*/    offset = writeEntryHeaderField(this.devMajor, outbuf, offset, 8, starMode);
/*1029*/    offset = writeEntryHeaderField(this.devMinor, outbuf, offset, 8, starMode);
/*1032*/    while (offset < outbuf.length) {
/*1033*/        outbuf[offset++] = 0; 
/*   0*/       }
/*1036*/    long chk = TarUtils.computeCheckSum(outbuf);
/*1038*/    TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, 8);
/*   0*/  }
/*   0*/  
/*   0*/  private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode) {
/*1043*/    if (!starMode && (value < 0L || value >= 1L << 3 * (length - 1))) {
/*1048*/        return TarUtils.formatLongOctalBytes(0L, outbuf, offset, length); 
/*   0*/       }
/*1050*/    return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
/*   0*/  }
/*   0*/  
/*   0*/  public void parseTarHeader(byte[] header) {
/*   0*/    try {
/*1062*/      parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
/*1063*/    } catch (IOException ex) {
/*   0*/      try {
/*1065*/        parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true);
/*1066*/      } catch (IOException ex2) {
/*1068*/        throw new RuntimeException(ex2);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void parseTarHeader(byte[] header, ZipEncoding encoding) throws IOException {
/*1085*/    parseTarHeader(header, encoding, false);
/*   0*/  }
/*   0*/  
/*   0*/  private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle) throws IOException {
/*   0*/    String xstarPrefix, prefix;
/*1091*/    int offset = 0;
/*1093*/    this.name = oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding);
/*1095*/    offset += 100;
/*1096*/    this.mode = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/*1097*/    offset += 8;
/*1098*/    this.userId = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/*1099*/    offset += 8;
/*1100*/    this.groupId = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/*1101*/    offset += 8;
/*1102*/    this.size = TarUtils.parseOctalOrBinary(header, offset, 12);
/*1103*/    offset += 12;
/*1104*/    this.modTime = TarUtils.parseOctalOrBinary(header, offset, 12);
/*1105*/    offset += 12;
/*1106*/    this.checkSumOK = TarUtils.verifyCheckSum(header);
/*1107*/    offset += 8;
/*1108*/    this.linkFlag = header[offset++];
/*1109*/    this.linkName = oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding);
/*1111*/    offset += 100;
/*1112*/    this.magic = TarUtils.parseName(header, offset, 6);
/*1113*/    offset += 6;
/*1114*/    this.version = TarUtils.parseName(header, offset, 2);
/*1115*/    offset += 2;
/*1116*/    this.userName = oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding);
/*1118*/    offset += 32;
/*1119*/    this.groupName = oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding);
/*1121*/    offset += 32;
/*1122*/    this.devMajor = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/*1123*/    offset += 8;
/*1124*/    this.devMinor = (int)TarUtils.parseOctalOrBinary(header, offset, 8);
/*1125*/    offset += 8;
/*1127*/    int type = evaluateType(header);
/*1128*/    switch (type) {
/*   0*/      case 2:
/*1130*/        offset += 12;
/*1131*/        offset += 12;
/*1132*/        offset += 12;
/*1133*/        offset += 4;
/*1134*/        offset++;
/*1135*/        offset += 96;
/*1136*/        this.isExtended = TarUtils.parseBoolean(header, offset);
/*1137*/        offset++;
/*1138*/        this.realSize = TarUtils.parseOctal(header, offset, 12);
/*1139*/        offset += 12;
/*   0*/        break;
/*   0*/      case 4:
/*1143*/        xstarPrefix = oldStyle ? TarUtils.parseName(header, offset, 131) : TarUtils.parseName(header, offset, 131, encoding);
/*1146*/        if (xstarPrefix.length() > 0) {
/*1147*/            this.name = xstarPrefix + "/" + this.name; 
/*   0*/           }
/*   0*/        break;
/*   0*/      default:
/*1153*/        prefix = oldStyle ? TarUtils.parseName(header, offset, 155) : TarUtils.parseName(header, offset, 155, encoding);
/*1158*/        if (isDirectory() && !this.name.endsWith("/")) {
/*1159*/            this.name += "/"; 
/*   0*/           }
/*1161*/        if (prefix.length() > 0) {
/*1162*/            this.name = prefix + "/" + this.name; 
/*   0*/           }
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static String normalizeFileName(String fileName, boolean preserveLeadingSlashes) {
/*1174*/    String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
/*1176*/    if (osname != null) {
/*1181*/        if (osname.startsWith("windows")) {
/*1182*/          if (fileName.length() > 2) {
/*1183*/            char ch1 = fileName.charAt(0);
/*1184*/            char ch2 = fileName.charAt(1);
/*1186*/            if (ch2 == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z'))) {
/*1189*/                fileName = fileName.substring(2); 
/*   0*/               }
/*   0*/          } 
/*1192*/        } else if (osname.contains("netware")) {
/*1193*/          int colon = fileName.indexOf(':');
/*1194*/          if (colon != -1) {
/*1195*/              fileName = fileName.substring(colon + 1); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/*1200*/    fileName = fileName.replace(File.separatorChar, '/');
/*1205*/    while (!preserveLeadingSlashes && fileName.startsWith("/")) {
/*1206*/        fileName = fileName.substring(1); 
/*   0*/       }
/*1208*/    return fileName;
/*   0*/  }
/*   0*/  
/*   0*/  private int evaluateType(byte[] header) {
/*1218*/    if (ArchiveUtils.matchAsciiBuffer("ustar ", header, 257, 6)) {
/*1219*/        return 2; 
/*   0*/       }
/*1221*/    if (ArchiveUtils.matchAsciiBuffer("ustar\000", header, 257, 6)) {
/*1222*/      if (ArchiveUtils.matchAsciiBuffer("tar\000", header, 508, 4)) {
/*1224*/          return 4; 
/*   0*/         }
/*1226*/      return 3;
/*   0*/    } 
/*1228*/    return 0;
/*   0*/  }
/*   0*/  
/*   0*/  void fillGNUSparse0xData(Map<String, String> headers) {
/*1232*/    this.paxGNUSparse = true;
/*1233*/    this.realSize = Integer.parseInt(headers.get("GNU.sparse.size"));
/*1234*/    if (headers.containsKey("GNU.sparse.name")) {
/*1236*/        this.name = headers.get("GNU.sparse.name"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  void fillGNUSparse1xData(Map<String, String> headers) {
/*1241*/    this.paxGNUSparse = true;
/*1242*/    this.realSize = Integer.parseInt(headers.get("GNU.sparse.realsize"));
/*1243*/    this.name = headers.get("GNU.sparse.name");
/*   0*/  }
/*   0*/  
/*   0*/  void fillStarSparseData(Map<String, String> headers) {
/*1247*/    this.starSparse = true;
/*1248*/    if (headers.containsKey("SCHILY.realsize")) {
/*1249*/        this.realSize = Long.parseLong(headers.get("SCHILY.realsize")); 
/*   0*/       }
/*   0*/  }
/*   0*/}
