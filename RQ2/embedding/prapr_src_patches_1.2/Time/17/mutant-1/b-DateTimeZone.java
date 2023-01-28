/*   0*/package org.joda.time;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.ObjectStreamException;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.ref.Reference;
/*   0*/import java.lang.ref.SoftReference;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.TimeZone;
/*   0*/import org.joda.convert.FromString;
/*   0*/import org.joda.convert.ToString;
/*   0*/import org.joda.time.chrono.BaseChronology;
/*   0*/import org.joda.time.field.FieldUtils;
/*   0*/import org.joda.time.format.DateTimeFormat;
/*   0*/import org.joda.time.format.DateTimeFormatter;
/*   0*/import org.joda.time.format.DateTimeFormatterBuilder;
/*   0*/import org.joda.time.format.FormatUtils;
/*   0*/import org.joda.time.tz.DefaultNameProvider;
/*   0*/import org.joda.time.tz.FixedDateTimeZone;
/*   0*/import org.joda.time.tz.NameProvider;
/*   0*/import org.joda.time.tz.Provider;
/*   0*/import org.joda.time.tz.UTCProvider;
/*   0*/import org.joda.time.tz.ZoneInfoProvider;
/*   0*/
/*   0*/public abstract class DateTimeZone implements Serializable {
/*   0*/  private static final long serialVersionUID = 5546345482340108586L;
/*   0*/  
/*  95*/  public static final DateTimeZone UTC = new FixedDateTimeZone("UTC", "UTC", 0, 0);
/*   0*/  
/*   0*/  private static Provider cProvider;
/*   0*/  
/*   0*/  private static NameProvider cNameProvider;
/*   0*/  
/*   0*/  private static Set<String> cAvailableIDs;
/*   0*/  
/*   0*/  private static volatile DateTimeZone cDefault;
/*   0*/  
/*   0*/  private static DateTimeFormatter cOffsetFormatter;
/*   0*/  
/*   0*/  private static Map<String, SoftReference<DateTimeZone>> iFixedOffsetCache;
/*   0*/  
/*   0*/  private static Map<String, String> cZoneIdConversion;
/*   0*/  
/*   0*/  private final String iID;
/*   0*/  
/*   0*/  static {
/* 115*/    setProvider0(null);
/* 116*/    setNameProvider0(null);
/*   0*/  }
/*   0*/  
/*   0*/  public static DateTimeZone getDefault() {
/* 133*/    DateTimeZone dateTimeZone = cDefault;
/* 134*/    if (dateTimeZone == null)
/* 135*/      synchronized (DateTimeZone.class) {
/* 136*/        dateTimeZone = cDefault;
/* 137*/        if (dateTimeZone == null) {
/* 138*/          DateTimeZone dateTimeZone1 = null;
/*   0*/          try {
/*   0*/            try {
/* 141*/              String str = System.getProperty("user.timezone");
/* 142*/              if (str != null)
/* 143*/                dateTimeZone1 = forID(str); 
/* 145*/            } catch (RuntimeException runtimeException) {}
/* 148*/            if (dateTimeZone1 == null)
/* 149*/              dateTimeZone1 = forTimeZone(TimeZone.getDefault()); 
/* 151*/          } catch (IllegalArgumentException illegalArgumentException) {}
/* 154*/          if (dateTimeZone1 == null)
/* 155*/            dateTimeZone1 = UTC; 
/* 157*/          cDefault = dateTimeZone = dateTimeZone1;
/*   0*/        } 
/*   0*/      }  
/* 161*/    return dateTimeZone;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setDefault(DateTimeZone paramDateTimeZone) throws SecurityException {
/* 174*/    SecurityManager securityManager = System.getSecurityManager();
/* 175*/    if (securityManager != null)
/* 176*/      securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setDefault")); 
/* 178*/    if (paramDateTimeZone == null)
/* 179*/      throw new IllegalArgumentException("The datetime zone must not be null"); 
/* 181*/    synchronized (DateTimeZone.class) {
/* 182*/      cDefault = paramDateTimeZone;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  @FromString
/*   0*/  public static DateTimeZone forID(String paramString) {
/* 204*/    if (paramString == null)
/* 205*/      return getDefault(); 
/* 207*/    if (paramString.equals("UTC"))
/* 208*/      return UTC; 
/* 210*/    DateTimeZone dateTimeZone = cProvider.getZone(paramString);
/* 211*/    if (dateTimeZone != null)
/* 212*/      return dateTimeZone; 
/* 214*/    if (paramString.startsWith("+") || paramString.startsWith("-")) {
/* 215*/      int i = parseOffset(paramString);
/* 216*/      if (i == 0L)
/* 217*/        return UTC; 
/* 219*/      paramString = printOffset(i);
/* 220*/      return fixedOffsetZone(paramString, i);
/*   0*/    } 
/* 223*/    throw new IllegalArgumentException("The datetime zone id '" + paramString + "' is not recognised");
/*   0*/  }
/*   0*/  
/*   0*/  public static DateTimeZone forOffsetHours(int paramInt) throws IllegalArgumentException {
/* 237*/    return forOffsetHoursMinutes(paramInt, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static DateTimeZone forOffsetHoursMinutes(int paramInt1, int paramInt2) throws IllegalArgumentException {
/* 254*/    if (paramInt1 == 0 && paramInt2 == 0)
/* 255*/      return UTC; 
/* 257*/    if (paramInt2 < 0 || paramInt2 > 59)
/* 258*/      throw new IllegalArgumentException("Minutes out of range: " + paramInt2); 
/* 260*/    int i = 0;
/*   0*/    try {
/* 262*/      int j = FieldUtils.safeMultiply(paramInt1, 60);
/* 263*/      if (j < 0) {
/* 264*/        paramInt2 = FieldUtils.safeAdd(j, -paramInt2);
/*   0*/      } else {
/* 266*/        paramInt2 = FieldUtils.safeAdd(j, paramInt2);
/*   0*/      } 
/* 268*/      i = FieldUtils.safeMultiply(paramInt2, 60000);
/* 269*/    } catch (ArithmeticException arithmeticException) {
/* 270*/      throw new IllegalArgumentException("Offset is too large");
/*   0*/    } 
/* 272*/    return forOffsetMillis(i);
/*   0*/  }
/*   0*/  
/*   0*/  public static DateTimeZone forOffsetMillis(int paramInt) {
/* 282*/    String str = printOffset(paramInt);
/* 283*/    return fixedOffsetZone(str, paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public static DateTimeZone forTimeZone(TimeZone paramTimeZone) {
/* 302*/    if (paramTimeZone == null)
/* 303*/      return getDefault(); 
/* 305*/    String str1 = paramTimeZone.getID();
/* 306*/    if (str1.equals("UTC"))
/* 307*/      return UTC; 
/* 311*/    DateTimeZone dateTimeZone = null;
/* 312*/    String str2 = getConvertedId(str1);
/* 313*/    if (str2 != null)
/* 314*/      dateTimeZone = cProvider.getZone(str2); 
/* 316*/    if (dateTimeZone == null)
/* 317*/      dateTimeZone = cProvider.getZone(str1); 
/* 319*/    if (dateTimeZone != null)
/* 320*/      return dateTimeZone; 
/* 324*/    if (str2 == null) {
/* 325*/      str2 = paramTimeZone.getDisplayName();
/* 326*/      if (str2.startsWith("GMT+") || str2.startsWith("GMT-")) {
/* 327*/        str2 = str2.substring(3);
/* 328*/        int i = parseOffset(str2);
/* 329*/        if (i == 0L)
/* 330*/          return UTC; 
/* 332*/        str2 = printOffset(i);
/* 333*/        return fixedOffsetZone(str2, i);
/*   0*/      } 
/*   0*/    } 
/* 337*/    throw new IllegalArgumentException("The datetime zone id '" + str1 + "' is not recognised");
/*   0*/  }
/*   0*/  
/*   0*/  private static synchronized DateTimeZone fixedOffsetZone(String paramString, int paramInt) {
/* 349*/    if (paramInt == 0)
/* 350*/      return UTC; 
/* 352*/    if (iFixedOffsetCache == null)
/* 353*/      iFixedOffsetCache = new HashMap<String, SoftReference<DateTimeZone>>(); 
/* 356*/    Reference<DateTimeZone> reference = iFixedOffsetCache.get(paramString);
/* 357*/    if (reference != null) {
/* 358*/      DateTimeZone dateTimeZone = reference.get();
/* 359*/      if (dateTimeZone != null)
/* 360*/        return dateTimeZone; 
/*   0*/    } 
/* 363*/    FixedDateTimeZone fixedDateTimeZone = new FixedDateTimeZone(paramString, null, paramInt, paramInt);
/* 364*/    iFixedOffsetCache.put(paramString, new SoftReference<DateTimeZone>(fixedDateTimeZone));
/* 365*/    return fixedDateTimeZone;
/*   0*/  }
/*   0*/  
/*   0*/  public static Set<String> getAvailableIDs() {
/* 374*/    return cAvailableIDs;
/*   0*/  }
/*   0*/  
/*   0*/  public static Provider getProvider() {
/* 387*/    return cProvider;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setProvider(Provider paramProvider) throws SecurityException {
/* 401*/    SecurityManager securityManager = System.getSecurityManager();
/* 402*/    if (securityManager != null)
/* 403*/      securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setProvider")); 
/* 405*/    setProvider0(paramProvider);
/*   0*/  }
/*   0*/  
/*   0*/  private static void setProvider0(Provider paramProvider) {
/* 415*/    if (paramProvider == null)
/* 416*/      paramProvider = getDefaultProvider(); 
/* 418*/    Set<String> set = paramProvider.getAvailableIDs();
/* 419*/    if (set == null || set.size() == 0)
/* 420*/      throw new IllegalArgumentException("The provider doesn't have any available ids"); 
/* 423*/    if (!set.contains("UTC"))
/* 424*/      throw new IllegalArgumentException("The provider doesn't support UTC"); 
/* 426*/    if (!UTC.equals(paramProvider.getZone("UTC")))
/* 427*/      throw new IllegalArgumentException("Invalid UTC zone provided"); 
/* 429*/    cProvider = paramProvider;
/* 430*/    cAvailableIDs = set;
/*   0*/  }
/*   0*/  
/*   0*/  private static Provider getDefaultProvider() {
/* 443*/    Provider provider = null;
/*   0*/    try {
/* 446*/      String str = System.getProperty("org.joda.time.DateTimeZone.Provider");
/* 448*/      if (str != null)
/*   0*/        try {
/* 450*/          provider = (Provider)Class.forName(str).newInstance();
/* 451*/        } catch (Exception exception) {
/* 452*/          Thread thread = Thread.currentThread();
/* 453*/          thread.getThreadGroup().uncaughtException(thread, exception);
/*   0*/        }  
/* 456*/    } catch (SecurityException securityException) {}
/* 460*/    if (provider == null)
/*   0*/      try {
/* 462*/        provider = new ZoneInfoProvider("org/joda/time/tz/data");
/* 463*/      } catch (Exception exception) {
/* 464*/        Thread thread = Thread.currentThread();
/* 465*/        thread.getThreadGroup().uncaughtException(thread, exception);
/*   0*/      }  
/* 469*/    if (provider == null)
/* 470*/      provider = new UTCProvider(); 
/* 473*/    return provider;
/*   0*/  }
/*   0*/  
/*   0*/  public static NameProvider getNameProvider() {
/* 486*/    return cNameProvider;
/*   0*/  }
/*   0*/  
/*   0*/  public static void setNameProvider(NameProvider paramNameProvider) throws SecurityException {
/* 500*/    SecurityManager securityManager = System.getSecurityManager();
/* 501*/    if (securityManager != null)
/* 502*/      securityManager.checkPermission(new JodaTimePermission("DateTimeZone.setNameProvider")); 
/* 504*/    setNameProvider0(paramNameProvider);
/*   0*/  }
/*   0*/  
/*   0*/  private static void setNameProvider0(NameProvider paramNameProvider) {
/* 514*/    if (paramNameProvider == null)
/* 515*/      paramNameProvider = getDefaultNameProvider(); 
/* 517*/    cNameProvider = paramNameProvider;
/*   0*/  }
/*   0*/  
/*   0*/  private static NameProvider getDefaultNameProvider() {
/* 529*/    NameProvider nameProvider = null;
/*   0*/    try {
/* 531*/      String str = System.getProperty("org.joda.time.DateTimeZone.NameProvider");
/* 532*/      if (str != null)
/*   0*/        try {
/* 534*/          nameProvider = (NameProvider)Class.forName(str).newInstance();
/* 535*/        } catch (Exception exception) {
/* 536*/          Thread thread = Thread.currentThread();
/* 537*/          thread.getThreadGroup().uncaughtException(thread, exception);
/*   0*/        }  
/* 540*/    } catch (SecurityException securityException) {}
/* 544*/    if (nameProvider == null)
/* 545*/      nameProvider = new DefaultNameProvider(); 
/* 548*/    return nameProvider;
/*   0*/  }
/*   0*/  
/*   0*/  private static synchronized String getConvertedId(String paramString) {
/* 559*/    Map<String, String> map = cZoneIdConversion;
/* 560*/    if (map == null) {
/* 562*/      map = new HashMap<String, String>();
/* 563*/      map.put("GMT", "UTC");
/* 564*/      map.put("WET", "WET");
/* 565*/      map.put("CET", "CET");
/* 566*/      map.put("MET", "CET");
/* 567*/      map.put("ECT", "CET");
/* 568*/      map.put("EET", "EET");
/* 569*/      map.put("MIT", "Pacific/Apia");
/* 570*/      map.put("HST", "Pacific/Honolulu");
/* 571*/      map.put("AST", "America/Anchorage");
/* 572*/      map.put("PST", "America/Los_Angeles");
/* 573*/      map.put("MST", "America/Denver");
/* 574*/      map.put("PNT", "America/Phoenix");
/* 575*/      map.put("CST", "America/Chicago");
/* 576*/      map.put("EST", "America/New_York");
/* 577*/      map.put("IET", "America/Indiana/Indianapolis");
/* 578*/      map.put("PRT", "America/Puerto_Rico");
/* 579*/      map.put("CNT", "America/St_Johns");
/* 580*/      map.put("AGT", "America/Argentina/Buenos_Aires");
/* 581*/      map.put("BET", "America/Sao_Paulo");
/* 582*/      map.put("ART", "Africa/Cairo");
/* 583*/      map.put("CAT", "Africa/Harare");
/* 584*/      map.put("EAT", "Africa/Addis_Ababa");
/* 585*/      map.put("NET", "Asia/Yerevan");
/* 586*/      map.put("PLT", "Asia/Karachi");
/* 587*/      map.put("IST", "Asia/Kolkata");
/* 588*/      map.put("BST", "Asia/Dhaka");
/* 589*/      map.put("VST", "Asia/Ho_Chi_Minh");
/* 590*/      map.put("CTT", "Asia/Shanghai");
/* 591*/      map.put("JST", "Asia/Tokyo");
/* 592*/      map.put("ACT", "Australia/Darwin");
/* 593*/      map.put("AET", "Australia/Sydney");
/* 594*/      map.put("SST", "Pacific/Guadalcanal");
/* 595*/      map.put("NST", "Pacific/Auckland");
/* 596*/      cZoneIdConversion = map;
/*   0*/    } 
/* 598*/    return map.get(paramString);
/*   0*/  }
/*   0*/  
/*   0*/  private static int parseOffset(String paramString) {
/* 604*/    BaseChronology baseChronology = new BaseChronology() {
/*   0*/        public DateTimeZone getZone() {
/* 606*/          return null;
/*   0*/        }
/*   0*/        
/*   0*/        public Chronology withUTC() {
/* 609*/          return this;
/*   0*/        }
/*   0*/        
/*   0*/        public Chronology withZone(DateTimeZone param1DateTimeZone) {
/* 612*/          return this;
/*   0*/        }
/*   0*/        
/*   0*/        public String toString() {
/* 615*/          return getClass().getName();
/*   0*/        }
/*   0*/      };
/* 618*/    return -((int)offsetFormatter().withChronology(baseChronology).parseMillis(paramString));
/*   0*/  }
/*   0*/  
/*   0*/  private static String printOffset(int paramInt) {
/* 631*/    StringBuffer stringBuffer = new StringBuffer();
/* 632*/    if (paramInt >= 0) {
/* 633*/      stringBuffer.append('+');
/*   0*/    } else {
/* 635*/      stringBuffer.append('-');
/* 636*/      paramInt = -paramInt;
/*   0*/    } 
/* 639*/    int i = paramInt / 3600000;
/* 640*/    FormatUtils.appendPaddedInteger(stringBuffer, i, 2);
/* 641*/    paramInt -= i * 3600000;
/* 643*/    int j = paramInt / 60000;
/* 644*/    stringBuffer.append(':');
/* 645*/    FormatUtils.appendPaddedInteger(stringBuffer, j, 2);
/* 646*/    paramInt -= j * 60000;
/* 647*/    if (paramInt == 0)
/* 648*/      return stringBuffer.toString(); 
/* 651*/    int k = paramInt / 1000;
/* 652*/    stringBuffer.append(':');
/* 653*/    FormatUtils.appendPaddedInteger(stringBuffer, k, 2);
/* 654*/    paramInt -= k * 1000;
/* 655*/    if (paramInt == 0)
/* 656*/      return stringBuffer.toString(); 
/* 659*/    stringBuffer.append('.');
/* 660*/    FormatUtils.appendPaddedInteger(stringBuffer, paramInt, 3);
/* 661*/    return stringBuffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private static synchronized DateTimeFormatter offsetFormatter() {
/* 670*/    if (cOffsetFormatter == null)
/* 671*/      cOffsetFormatter = new DateTimeFormatterBuilder().appendTimeZoneOffset(null, true, 2, 4).toFormatter(); 
/* 675*/    return cOffsetFormatter;
/*   0*/  }
/*   0*/  
/*   0*/  protected DateTimeZone(String paramString) {
/* 690*/    if (paramString == null)
/* 691*/      throw new IllegalArgumentException("Id must not be null"); 
/* 693*/    this.iID = paramString;
/*   0*/  }
/*   0*/  
/*   0*/  @ToString
/*   0*/  public final String getID() {
/* 706*/    return this.iID;
/*   0*/  }
/*   0*/  
/*   0*/  public final String getShortName(long paramLong) {
/* 729*/    return getShortName(paramLong, null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getShortName(long paramLong, Locale paramLocale) {
/* 744*/    if (paramLocale == null)
/* 745*/      paramLocale = Locale.getDefault(); 
/* 747*/    String str1 = getNameKey(paramLong);
/* 748*/    if (str1 == null)
/* 749*/      return this.iID; 
/* 751*/    String str2 = cNameProvider.getShortName(paramLocale, this.iID, str1);
/* 752*/    if (str2 != null)
/* 753*/      return str2; 
/* 755*/    return printOffset(getOffset(paramLong));
/*   0*/  }
/*   0*/  
/*   0*/  public final String getName(long paramLong) {
/* 769*/    return getName(paramLong, null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getName(long paramLong, Locale paramLocale) {
/* 784*/    if (paramLocale == null)
/* 785*/      paramLocale = Locale.getDefault(); 
/* 787*/    String str1 = getNameKey(paramLong);
/* 788*/    if (str1 == null)
/* 789*/      return this.iID; 
/* 791*/    String str2 = cNameProvider.getName(paramLocale, this.iID, str1);
/* 792*/    if (str2 != null)
/* 793*/      return str2; 
/* 795*/    return printOffset(getOffset(paramLong));
/*   0*/  }
/*   0*/  
/*   0*/  public final int getOffset(ReadableInstant paramReadableInstant) {
/* 813*/    if (paramReadableInstant == null)
/* 814*/      return getOffset(DateTimeUtils.currentTimeMillis()); 
/* 816*/    return getOffset(paramReadableInstant.getMillis());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isStandardOffset(long paramLong) {
/* 844*/    return (getOffset(paramLong) == getStandardOffset(paramLong));
/*   0*/  }
/*   0*/  
/*   0*/  public int getOffsetFromLocal(long paramLong) {
/* 882*/    int i = getOffset(paramLong);
/* 884*/    long l = paramLong - i;
/* 885*/    int j = getOffset(l);
/* 887*/    if (i != j) {
/* 890*/      if (i - j < 0) {
/* 894*/        long l1 = nextTransition(l);
/* 895*/        long l2 = nextTransition(paramLong - j);
/* 896*/        if (l1 != l2)
/* 897*/          return i; 
/*   0*/      } 
/* 900*/    } else if (i >= 0) {
/* 901*/      long l1 = previousTransition(l);
/* 902*/      if (l1 < l) {
/* 903*/        int k = getOffset(l1);
/* 904*/        int m = k - i;
/* 905*/        if (l - l1 <= m)
/* 906*/          return k; 
/*   0*/      } 
/*   0*/    } 
/* 910*/    return j;
/*   0*/  }
/*   0*/  
/*   0*/  public long convertUTCToLocal(long paramLong) {
/* 924*/    int i = getOffset(paramLong);
/* 925*/    long l = paramLong + i;
/* 927*/    if ((paramLong ^ l) < 0L && (paramLong ^ i) >= 0L)
/* 928*/      throw new ArithmeticException("Adding time zone offset caused overflow"); 
/* 930*/    return l;
/*   0*/  }
/*   0*/  
/*   0*/  public long convertLocalToUTC(long paramLong1, boolean paramBoolean, long paramLong2) {
/* 951*/    int i = getOffset(paramLong2);
/* 952*/    long l = paramLong1 - i;
/* 953*/    int j = getOffset(l);
/* 954*/    if (j == i)
/* 955*/      return l; 
/* 957*/    return convertLocalToUTC(paramLong1, paramBoolean);
/*   0*/  }
/*   0*/  
/*   0*/  public long convertLocalToUTC(long paramLong, boolean paramBoolean) {
/* 974*/    int i = getOffset(paramLong);
/* 976*/    int j = getOffset(paramLong - i);
/* 978*/    if (i != j)
/* 982*/      if (paramBoolean || i < 0) {
/* 984*/        long l1 = nextTransition(paramLong - i);
/* 985*/        if (l1 == paramLong - i)
/* 986*/          l1 = Long.MAX_VALUE; 
/* 988*/        long l2 = nextTransition(paramLong - j);
/* 989*/        if (l2 == paramLong - j)
/* 990*/          l2 = Long.MAX_VALUE; 
/* 992*/        if (l1 != l2) {
/* 994*/          if (paramBoolean)
/* 996*/            throw new IllegalArgumentException("Illegal instant due to time zone offset transition: " + DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").print(new Instant(paramLong)) + " (" + getID() + ")"); 
/*1003*/          j = i;
/*   0*/        } 
/*   0*/      }  
/*1009*/    long l = paramLong - j;
/*1011*/    if ((paramLong ^ l) < 0L && (paramLong ^ j) < 0L)
/*1012*/      throw new ArithmeticException("Subtracting time zone offset caused overflow"); 
/*1014*/    return l;
/*   0*/  }
/*   0*/  
/*   0*/  public long getMillisKeepLocal(DateTimeZone paramDateTimeZone, long paramLong) {
/*1028*/    if (paramDateTimeZone == null)
/*1029*/      paramDateTimeZone = getDefault(); 
/*1031*/    if (paramDateTimeZone == this)
/*1032*/      return paramLong; 
/*1034*/    long l = convertUTCToLocal(paramLong);
/*1035*/    return paramDateTimeZone.convertLocalToUTC(l, false, paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLocalDateTimeGap(LocalDateTime paramLocalDateTime) {
/*1145*/    if (isFixed())
/*1146*/      return false; 
/*   0*/    try {
/*1149*/      paramLocalDateTime.toDateTime(this);
/*1150*/      return false;
/*1151*/    } catch (IllegalArgumentException illegalArgumentException) {
/*1152*/      return true;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public long adjustOffset(long paramLong, boolean paramBoolean) {
/*1167*/    long l1 = convertUTCToLocal(paramLong - 10800000L);
/*1168*/    paramLong + 10800000L;
/*1168*/    this;
/*1168*/    long l2 = 0L;
/*1169*/    if (l1 == l2)
/*1170*/      return paramLong; 
/*1174*/    long l3 = convertUTCToLocal(paramLong);
/*1175*/    return convertLocalToUTC(l3, false, paramBoolean ? l2 : l1);
/*   0*/  }
/*   0*/  
/*   0*/  public TimeZone toTimeZone() {
/*1220*/    return TimeZone.getTimeZone(this.iID);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1237*/    return 57 + getID().hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1245*/    return getID();
/*   0*/  }
/*   0*/  
/*   0*/  protected Object writeReplace() throws ObjectStreamException {
/*1255*/    return new Stub(this.iID);
/*   0*/  }
/*   0*/  
/*   0*/  public abstract String getNameKey(long paramLong);
/*   0*/  
/*   0*/  public abstract int getOffset(long paramLong);
/*   0*/  
/*   0*/  public abstract int getStandardOffset(long paramLong);
/*   0*/  
/*   0*/  public abstract boolean isFixed();
/*   0*/  
/*   0*/  public abstract long nextTransition(long paramLong);
/*   0*/  
/*   0*/  public abstract long previousTransition(long paramLong);
/*   0*/  
/*   0*/  public abstract boolean equals(Object paramObject);
/*   0*/  
/*   0*/  private static final class Stub implements Serializable {
/*   0*/    private static final long serialVersionUID = -6471952376487863581L;
/*   0*/    
/*   0*/    private transient String iID;
/*   0*/    
/*   0*/    Stub(String param1String) {
/*1272*/      this.iID = param1String;
/*   0*/    }
/*   0*/    
/*   0*/    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
/*1276*/      param1ObjectOutputStream.writeUTF(this.iID);
/*   0*/    }
/*   0*/    
/*   0*/    private void readObject(ObjectInputStream param1ObjectInputStream) throws IOException {
/*1280*/      this.iID = param1ObjectInputStream.readUTF();
/*   0*/    }
/*   0*/    
/*   0*/    private Object readResolve() throws ObjectStreamException {
/*1284*/      return DateTimeZone.forID(this.iID);
/*   0*/    }
/*   0*/  }
/*   0*/}
