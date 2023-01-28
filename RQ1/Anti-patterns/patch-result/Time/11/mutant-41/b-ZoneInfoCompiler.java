/*   0*/package org.joda.time.tz;
/*   0*/
/*   0*/import java.io.BufferedReader;
/*   0*/import java.io.DataOutputStream;
/*   0*/import java.io.File;
/*   0*/import java.io.FileInputStream;
/*   0*/import java.io.FileOutputStream;
/*   0*/import java.io.FileReader;
/*   0*/import java.io.IOException;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.StringTokenizer;
/*   0*/import java.util.TreeMap;
/*   0*/import org.joda.time.Chronology;
/*   0*/import org.joda.time.DateTime;
/*   0*/import org.joda.time.DateTimeField;
/*   0*/import org.joda.time.DateTimeZone;
/*   0*/import org.joda.time.LocalDate;
/*   0*/import org.joda.time.MutableDateTime;
/*   0*/import org.joda.time.chrono.ISOChronology;
/*   0*/import org.joda.time.chrono.LenientChronology;
/*   0*/import org.joda.time.format.DateTimeFormatter;
/*   0*/import org.joda.time.format.ISODateTimeFormat;
/*   0*/
/*   0*/public class ZoneInfoCompiler {
/*   0*/  static DateTimeOfYear cStartOfYear;
/*   0*/  
/*   0*/  static Chronology cLenientISO;
/*   0*/  
/*  68*/  static ThreadLocal<Boolean> cVerbose = new ThreadLocal<Boolean>();
/*   0*/  
/*   0*/  private Map<String, RuleSet> iRuleSets;
/*   0*/  
/*   0*/  private List<Zone> iZones;
/*   0*/  
/*   0*/  private List<String> iLinks;
/*   0*/  
/*   0*/  static {
/*  70*/    cVerbose.set(Boolean.FALSE);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean verbose() {
/*  78*/    (Boolean)cVerbose.get();
/*  78*/    return ((Boolean)cVerbose.get() == null) ? false : (Boolean)cVerbose.get();
/*   0*/  }
/*   0*/  
/*   0*/  public static void main(String[] paramArrayOfString) throws Exception {
/*  94*/    if (paramArrayOfString.length == 0) {
/*  95*/      printUsage();
/*   0*/      return;
/*   0*/    } 
/*  99*/    File file1 = null;
/* 100*/    File file2 = null;
/*   0*/    boolean bool = false;
/*   0*/    int i;
/* 104*/    for (i = 0; i < paramArrayOfString.length; i++) {
/*   0*/      try {
/* 106*/        if ("-src".equals(paramArrayOfString[i])) {
/* 107*/          file1 = new File(paramArrayOfString[++i]);
/* 108*/        } else if ("-dst".equals(paramArrayOfString[i])) {
/* 109*/          file2 = new File(paramArrayOfString[++i]);
/* 110*/        } else if ("-verbose".equals(paramArrayOfString[i])) {
/* 111*/          bool = true;
/*   0*/        } else {
/* 112*/          if ("-?".equals(paramArrayOfString[i])) {
/* 113*/            printUsage();
/*   0*/            return;
/*   0*/          } 
/*   0*/          break;
/*   0*/        } 
/* 118*/      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
/* 119*/        printUsage();
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/* 124*/    if (i >= paramArrayOfString.length) {
/* 125*/      printUsage();
/*   0*/      return;
/*   0*/    } 
/* 129*/    File[] arrayOfFile = new File[paramArrayOfString.length - i];
/* 130*/    for (int j = 0; i < paramArrayOfString.length; i++, j++)
/* 131*/      arrayOfFile[j] = (file1 == null) ? new File(paramArrayOfString[i]) : new File(file1, paramArrayOfString[i]); 
/* 134*/    cVerbose.set(bool);
/* 135*/    ZoneInfoCompiler zoneInfoCompiler = new ZoneInfoCompiler();
/* 136*/    zoneInfoCompiler.compile(file2, arrayOfFile);
/*   0*/  }
/*   0*/  
/*   0*/  private static void printUsage() {
/* 140*/    System.out.println("Usage: java org.joda.time.tz.ZoneInfoCompiler <options> <source files>");
/* 141*/    System.out.println("where possible options include:");
/* 142*/    System.out.println("  -src <directory>    Specify where to read source files");
/* 143*/    System.out.println("  -dst <directory>    Specify where to write generated files");
/* 144*/    System.out.println("  -verbose            Output verbosely (default false)");
/*   0*/  }
/*   0*/  
/*   0*/  static DateTimeOfYear getStartOfYear() {
/* 148*/    if (cStartOfYear == null)
/* 149*/      cStartOfYear = new DateTimeOfYear(); 
/* 151*/    return cStartOfYear;
/*   0*/  }
/*   0*/  
/*   0*/  static Chronology getLenientISOChronology() {
/* 155*/    if (cLenientISO == null)
/* 156*/      cLenientISO = LenientChronology.getInstance(ISOChronology.getInstanceUTC()); 
/* 158*/    return cLenientISO;
/*   0*/  }
/*   0*/  
/*   0*/  static void writeZoneInfoMap(DataOutputStream paramDataOutputStream, Map<String, DateTimeZone> paramMap) throws IOException {
/* 166*/    HashMap<String, Short> hashMap = new HashMap(paramMap.size());
/* 167*/    TreeMap<Short, String> treeMap = new TreeMap();
/* 169*/    short s = 0;
/* 170*/    for (Map.Entry<String, DateTimeZone> entry : paramMap.entrySet()) {
/* 171*/      String str = (String)entry.getKey();
/* 172*/      if (!hashMap.containsKey(str)) {
/* 173*/        Short short_ = s;
/* 174*/        hashMap.put(str, short_);
/* 175*/        treeMap.put(short_, str);
/* 176*/        s = (short)(s + 1);
/* 176*/        if (s == 0)
/* 177*/          throw new InternalError("Too many time zone ids"); 
/*   0*/      } 
/* 180*/      str = ((DateTimeZone)entry.getValue()).getID();
/* 181*/      if (!hashMap.containsKey(str)) {
/* 182*/        Short short_ = s;
/* 183*/        hashMap.put(str, short_);
/* 184*/        treeMap.put(short_, str);
/* 185*/        s = (short)(s + 1);
/* 185*/        if (s == 0)
/* 186*/          throw new InternalError("Too many time zone ids"); 
/*   0*/      } 
/*   0*/    } 
/* 192*/    paramDataOutputStream.writeShort(treeMap.size());
/* 193*/    for (String str : treeMap.values())
/* 194*/      paramDataOutputStream.writeUTF(str); 
/* 198*/    paramDataOutputStream.writeShort(paramMap.size());
/* 199*/    for (Map.Entry<String, DateTimeZone> entry : paramMap.entrySet()) {
/* 200*/      String str = (String)entry.getKey();
/* 201*/      paramDataOutputStream.writeShort((Short)hashMap.get(str));
/* 202*/      str = ((DateTimeZone)entry.getValue()).getID();
/* 203*/      paramDataOutputStream.writeShort((Short)hashMap.get(str));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static int parseYear(String paramString, int paramInt) {
/* 208*/    paramString = paramString.toLowerCase();
/* 209*/    if (paramString.equals("minimum") || paramString.equals("min"))
/* 210*/      return Integer.MIN_VALUE; 
/* 211*/    if (paramString.equals("maximum") || paramString.equals("max"))
/* 212*/      return Integer.MAX_VALUE; 
/* 213*/    if (paramString.equals("only"))
/* 214*/      return paramInt; 
/* 216*/    return Integer.parseInt(paramString);
/*   0*/  }
/*   0*/  
/*   0*/  static int parseMonth(String paramString) {
/* 220*/    DateTimeField dateTimeField = ISOChronology.getInstanceUTC().monthOfYear();
/* 221*/    return dateTimeField.get(dateTimeField.set(0L, paramString, Locale.ENGLISH));
/*   0*/  }
/*   0*/  
/*   0*/  static int parseDayOfWeek(String paramString) {
/* 225*/    DateTimeField dateTimeField = ISOChronology.getInstanceUTC().dayOfWeek();
/* 226*/    return dateTimeField.get(dateTimeField.set(0L, paramString, Locale.ENGLISH));
/*   0*/  }
/*   0*/  
/*   0*/  static String parseOptional(String paramString) {
/* 230*/    return paramString.equals("-") ? null : paramString;
/*   0*/  }
/*   0*/  
/*   0*/  static int parseTime(String paramString) {
/* 234*/    DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.hourMinuteSecondFraction();
/* 235*/    MutableDateTime mutableDateTime = new MutableDateTime(0L, getLenientISOChronology());
/* 236*/    int i = 0;
/* 237*/    if (paramString.startsWith("-"))
/* 238*/      i = 1; 
/* 240*/    int j = dateTimeFormatter.parseInto(mutableDateTime, paramString, i);
/* 241*/    if (j == (i ^ 0xFFFFFFFF))
/* 242*/      throw new IllegalArgumentException(paramString); 
/* 244*/    int k = (int)mutableDateTime.getMillis();
/* 245*/    if (i == 1)
/* 246*/      k = -k; 
/* 248*/    return k;
/*   0*/  }
/*   0*/  
/*   0*/  static char parseZoneChar(char paramChar) {
/* 252*/    switch (paramChar) {
/*   0*/      case 'S':
/*   0*/      case 's':
/* 255*/        return 's';
/*   0*/      case 'G':
/*   0*/      case 'U':
/*   0*/      case 'Z':
/*   0*/      case 'g':
/*   0*/      case 'u':
/*   0*/      case 'z':
/* 258*/        return 'u';
/*   0*/    } 
/* 261*/    return 'w';
/*   0*/  }
/*   0*/  
/*   0*/  static boolean test(String paramString, DateTimeZone paramDateTimeZone) {
/* 269*/    if (!paramString.equals(paramDateTimeZone.getID()))
/* 270*/      return true; 
/* 275*/    long l1 = ISOChronology.getInstanceUTC().year().set(0L, 1850);
/* 276*/    long l2 = ISOChronology.getInstanceUTC().year().set(0L, 2050);
/* 278*/    int i = paramDateTimeZone.getOffset(l1);
/* 279*/    String str = paramDateTimeZone.getNameKey(l1);
/* 281*/    ArrayList<Long> arrayList = new ArrayList();
/*   0*/    while (true) {
/* 284*/      long l = paramDateTimeZone.nextTransition(l1);
/* 285*/      if (l == l1 || l > l2)
/*   0*/        break; 
/* 289*/      l1 = l;
/* 291*/      int k = paramDateTimeZone.getOffset(l1);
/* 292*/      String str1 = paramDateTimeZone.getNameKey(l1);
/* 294*/      if (i == k && str.equals(str1)) {
/* 296*/        System.out.println("*d* Error in " + paramDateTimeZone.getID() + " " + new DateTime(l1, ISOChronology.getInstanceUTC()));
/* 299*/        return false;
/*   0*/      } 
/* 302*/      if (str1 == null || (str1.length() < 3 && !"??".equals(str1))) {
/* 303*/        System.out.println("*s* Error in " + paramDateTimeZone.getID() + " " + new DateTime(l1, ISOChronology.getInstanceUTC()) + ", nameKey=" + str1);
/* 307*/        return false;
/*   0*/      } 
/* 310*/      arrayList.add(l1);
/* 312*/      i = k;
/* 313*/      str = str1;
/*   0*/    } 
/* 318*/    l1 = ISOChronology.getInstanceUTC().year().set(0L, 2050);
/* 319*/    l2 = ISOChronology.getInstanceUTC().year().set(0L, 1850);
/* 321*/    for (int j = arrayList.size(); --j >= 0; ) {
/* 322*/      long l3 = paramDateTimeZone.previousTransition(l1);
/* 323*/      if (l3 == l1 || l3 < l2)
/*   0*/        break; 
/* 327*/      l1 = l3;
/* 329*/      long l4 = (Long)arrayList.get(j);
/* 331*/      if (l4 - 1L != l1) {
/* 332*/        System.out.println("*r* Error in " + paramDateTimeZone.getID() + " " + new DateTime(l1, ISOChronology.getInstanceUTC()) + " != " + new DateTime(l4 - 1L, ISOChronology.getInstanceUTC()));
/* 338*/        return false;
/*   0*/      } 
/*   0*/    } 
/* 342*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public ZoneInfoCompiler() {
/* 355*/    this.iRuleSets = new HashMap<String, RuleSet>();
/* 356*/    this.iZones = new ArrayList<Zone>();
/* 357*/    this.iLinks = new ArrayList<String>();
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, DateTimeZone> compile(File paramFile, File[] paramArrayOfFile) throws IOException {
/* 367*/    if (paramArrayOfFile != null)
/* 368*/      for (int k = 0; k < paramArrayOfFile.length; k++) {
/* 369*/        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfFile[k]));
/* 370*/        parseDataFile(bufferedReader);
/* 371*/        bufferedReader.close();
/*   0*/      }  
/* 375*/    if (paramFile != null) {
/* 376*/      if (!paramFile.exists() && 
/* 377*/        !paramFile.mkdirs())
/* 378*/        throw new IOException("Destination directory doesn't exist and cannot be created: " + paramFile); 
/* 381*/      if (!paramFile.isDirectory())
/* 382*/        throw new IOException("Destination is not a directory: " + paramFile); 
/*   0*/    } 
/* 386*/    TreeMap<String, DateTimeZone> treeMap = new TreeMap();
/* 388*/    System.out.println("Writing zoneinfo files");
/* 389*/    for (int j = 0; j < this.iZones.size(); j++) {
/* 390*/      Zone zone = this.iZones.get(j);
/* 391*/      DateTimeZoneBuilder dateTimeZoneBuilder = new DateTimeZoneBuilder();
/* 392*/      zone.addToBuilder(dateTimeZoneBuilder, this.iRuleSets);
/* 393*/      DateTimeZone dateTimeZone1 = dateTimeZoneBuilder.toDateTimeZone(zone.iName, true);
/* 394*/      DateTimeZone dateTimeZone2 = dateTimeZone1;
/* 395*/      if (test(dateTimeZone2.getID(), dateTimeZone2)) {
/* 396*/        treeMap.put(dateTimeZone2.getID(), dateTimeZone2);
/* 397*/        if (paramFile != null) {
/* 398*/          if (verbose())
/* 399*/            System.out.println("Writing " + dateTimeZone2.getID()); 
/* 401*/          File file = new File(paramFile, dateTimeZone2.getID());
/* 402*/          if (!file.getParentFile().exists())
/* 403*/            file.getParentFile().mkdirs(); 
/* 405*/          FileOutputStream fileOutputStream = new FileOutputStream(file);
/*   0*/          try {
/* 407*/            dateTimeZoneBuilder.writeTo(zone.iName, fileOutputStream);
/*   0*/          } finally {
/* 409*/            fileOutputStream.close();
/*   0*/          } 
/* 413*/          FileInputStream fileInputStream = new FileInputStream(file);
/* 414*/          DateTimeZone dateTimeZone = DateTimeZoneBuilder.readFrom(fileInputStream, dateTimeZone2.getID());
/* 415*/          fileInputStream.close();
/* 417*/          if (!dateTimeZone1.equals(dateTimeZone))
/* 418*/            System.out.println("*e* Error in " + dateTimeZone2.getID() + ": Didn't read properly from file"); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 425*/    for (int i = 0; i < 2; i++) {
/* 426*/      for (int k = 0; k < this.iLinks.size(); k += 2) {
/* 427*/        String str1 = this.iLinks.get(k);
/* 428*/        String str2 = this.iLinks.get(k + 1);
/* 429*/        DateTimeZone dateTimeZone = treeMap.get(str1);
/* 430*/        if (dateTimeZone == null) {
/* 431*/          if (i > 0)
/* 432*/            System.out.println("Cannot find time zone '" + str1 + "' to link alias '" + str2 + "' to"); 
/*   0*/        } else {
/* 436*/          treeMap.put(str2, dateTimeZone);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 441*/    if (paramFile != null) {
/* 442*/      System.out.println("Writing ZoneInfoMap");
/* 443*/      File file = new File(paramFile, "ZoneInfoMap");
/* 444*/      if (!file.getParentFile().exists())
/* 445*/        file.getParentFile().mkdirs(); 
/* 448*/      FileOutputStream fileOutputStream = new FileOutputStream(file);
/* 449*/      DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
/*   0*/      try {
/* 452*/        TreeMap<String, DateTimeZone> treeMap1 = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 453*/        treeMap1.putAll(treeMap);
/* 454*/        writeZoneInfoMap(dataOutputStream, treeMap1);
/*   0*/      } finally {
/* 456*/        dataOutputStream.close();
/*   0*/      } 
/*   0*/    } 
/* 460*/    return treeMap;
/*   0*/  }
/*   0*/  
/*   0*/  public void parseDataFile(BufferedReader paramBufferedReader) throws IOException {
/* 464*/    Zone zone = null;
/*   0*/    String str;
/* 466*/    while ((str = paramBufferedReader.readLine()) != null) {
/* 467*/      String str1 = str.trim();
/* 468*/      if (str1.length() == 0 || str1.charAt(0) == '#')
/*   0*/        continue; 
/* 472*/      int i = str.indexOf('#');
/* 473*/      if (i >= 0)
/* 474*/        str = str.substring(0, i); 
/* 479*/      StringTokenizer stringTokenizer = new StringTokenizer(str, " \t");
/* 481*/      if (Character.isWhitespace(str.charAt(0)) && stringTokenizer.hasMoreTokens()) {
/* 482*/        if (zone != null)
/* 484*/          zone.chain(stringTokenizer); 
/*   0*/        continue;
/*   0*/      } 
/* 488*/      if (zone != null)
/* 489*/        this.iZones.add(zone); 
/* 491*/      zone = null;
/* 494*/      if (stringTokenizer.hasMoreTokens()) {
/* 495*/        String str2 = stringTokenizer.nextToken();
/* 496*/        if (str2.equalsIgnoreCase("Rule")) {
/* 497*/          Rule rule = new Rule(stringTokenizer);
/* 498*/          RuleSet ruleSet = this.iRuleSets.get(rule.iName);
/* 499*/          if (ruleSet == null) {
/* 500*/            ruleSet = new RuleSet(rule);
/* 501*/            this.iRuleSets.put(rule.iName, ruleSet);
/*   0*/            continue;
/*   0*/          } 
/* 503*/          ruleSet.addRule(rule);
/*   0*/          continue;
/*   0*/        } 
/* 505*/        if (str2.equalsIgnoreCase("Zone")) {
/* 506*/          zone = new Zone(stringTokenizer);
/*   0*/          continue;
/*   0*/        } 
/* 507*/        if (str2.equalsIgnoreCase("Link")) {
/* 508*/          this.iLinks.add(stringTokenizer.nextToken());
/* 509*/          this.iLinks.add(stringTokenizer.nextToken());
/*   0*/          continue;
/*   0*/        } 
/* 511*/        System.out.println("Unknown line: " + str);
/*   0*/      } 
/*   0*/    } 
/* 516*/    if (zone != null)
/* 517*/      this.iZones.add(zone); 
/*   0*/  }
/*   0*/  
/*   0*/  static class DateTimeOfYear {
/*   0*/    public final int iMonthOfYear;
/*   0*/    
/*   0*/    public final int iDayOfMonth;
/*   0*/    
/*   0*/    public final int iDayOfWeek;
/*   0*/    
/*   0*/    public final boolean iAdvanceDayOfWeek;
/*   0*/    
/*   0*/    public final int iMillisOfDay;
/*   0*/    
/*   0*/    public final char iZoneChar;
/*   0*/    
/*   0*/    DateTimeOfYear() {
/* 530*/      this.iMonthOfYear = 1;
/* 531*/      this.iDayOfMonth = 1;
/* 532*/      this.iDayOfWeek = 0;
/* 533*/      this.iAdvanceDayOfWeek = false;
/* 534*/      this.iMillisOfDay = 0;
/* 535*/      this.iZoneChar = 'w';
/*   0*/    }
/*   0*/    
/*   0*/    DateTimeOfYear(StringTokenizer param1StringTokenizer) {
/* 539*/      int i = 1;
/* 540*/      int j = 1;
/* 541*/      int k = 0;
/* 542*/      int m = 0;
/*   0*/      boolean bool = false;
/* 544*/      char c = 'w';
/* 546*/      if (param1StringTokenizer.hasMoreTokens()) {
/* 547*/        i = ZoneInfoCompiler.parseMonth(param1StringTokenizer.nextToken());
/* 549*/        if (param1StringTokenizer.hasMoreTokens()) {
/* 550*/          String str = param1StringTokenizer.nextToken();
/* 551*/          if (str.startsWith("last")) {
/* 552*/            j = -1;
/* 553*/            k = ZoneInfoCompiler.parseDayOfWeek(str.substring(4));
/* 554*/            bool = false;
/*   0*/          } else {
/*   0*/            try {
/* 557*/              j = Integer.parseInt(str);
/* 558*/              k = 0;
/* 559*/              bool = false;
/* 560*/            } catch (NumberFormatException numberFormatException) {
/* 561*/              int n = str.indexOf(">=");
/* 562*/              if (n > 0) {
/* 563*/                j = Integer.parseInt(str.substring(n + 2));
/* 564*/                k = ZoneInfoCompiler.parseDayOfWeek(str.substring(0, n));
/* 565*/                bool = true;
/*   0*/              } else {
/* 567*/                n = str.indexOf("<=");
/* 568*/                if (n > 0) {
/* 569*/                  j = Integer.parseInt(str.substring(n + 2));
/* 570*/                  k = ZoneInfoCompiler.parseDayOfWeek(str.substring(0, n));
/* 571*/                  bool = false;
/*   0*/                } else {
/* 573*/                  throw new IllegalArgumentException(str);
/*   0*/                } 
/*   0*/              } 
/*   0*/            } 
/*   0*/          } 
/* 579*/          if (param1StringTokenizer.hasMoreTokens()) {
/* 580*/            str = param1StringTokenizer.nextToken();
/* 581*/            c = ZoneInfoCompiler.parseZoneChar(str.charAt(str.length() - 1));
/* 582*/            if (str.equals("24:00")) {
/* 583*/              LocalDate localDate = (j == -1) ? new LocalDate(2001, i, 1).plusMonths(1) : new LocalDate(2001, i, j).plusDays(1);
/* 586*/              bool = (j != -1);
/* 587*/              i = localDate.getMonthOfYear();
/* 588*/              j = localDate.getDayOfMonth();
/* 589*/              k = (k - 1 + 1) % 7 + 1;
/*   0*/            } else {
/* 591*/              m = ZoneInfoCompiler.parseTime(str);
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 597*/      this.iMonthOfYear = i;
/* 598*/      this.iDayOfMonth = j;
/* 599*/      this.iDayOfWeek = k;
/* 600*/      this.iAdvanceDayOfWeek = bool;
/* 601*/      this.iMillisOfDay = m;
/* 602*/      this.iZoneChar = c;
/*   0*/    }
/*   0*/    
/*   0*/    public void addRecurring(DateTimeZoneBuilder param1DateTimeZoneBuilder, String param1String, int param1Int1, int param1Int2, int param1Int3) {
/* 611*/      param1DateTimeZoneBuilder.addRecurringSavings(param1String, param1Int1, param1Int2, param1Int3, this.iZoneChar, this.iMonthOfYear, this.iDayOfMonth, this.iDayOfWeek, this.iAdvanceDayOfWeek, this.iMillisOfDay);
/*   0*/    }
/*   0*/    
/*   0*/    public void addCutover(DateTimeZoneBuilder param1DateTimeZoneBuilder, int param1Int) {
/* 625*/      param1DateTimeZoneBuilder.addCutover(param1Int, this.iZoneChar, this.iMonthOfYear, this.iDayOfMonth, this.iDayOfWeek, this.iAdvanceDayOfWeek, this.iMillisOfDay);
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 635*/      return "MonthOfYear: " + this.iMonthOfYear + "\n" + "DayOfMonth: " + this.iDayOfMonth + "\n" + "DayOfWeek: " + this.iDayOfWeek + "\n" + "AdvanceDayOfWeek: " + this.iAdvanceDayOfWeek + "\n" + "MillisOfDay: " + this.iMillisOfDay + "\n" + "ZoneChar: " + this.iZoneChar + "\n";
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class Rule {
/*   0*/    public final String iName;
/*   0*/    
/*   0*/    public final int iFromYear;
/*   0*/    
/*   0*/    public final int iToYear;
/*   0*/    
/*   0*/    public final String iType;
/*   0*/    
/*   0*/    public final ZoneInfoCompiler.DateTimeOfYear iDateTimeOfYear;
/*   0*/    
/*   0*/    public final int iSaveMillis;
/*   0*/    
/*   0*/    public final String iLetterS;
/*   0*/    
/*   0*/    Rule(StringTokenizer param1StringTokenizer) {
/* 655*/      this.iName = param1StringTokenizer.nextToken().intern();
/* 656*/      this.iFromYear = ZoneInfoCompiler.parseYear(param1StringTokenizer.nextToken(), 0);
/* 657*/      this.iToYear = ZoneInfoCompiler.parseYear(param1StringTokenizer.nextToken(), this.iFromYear);
/* 658*/      if (this.iToYear < this.iFromYear)
/* 659*/        throw new IllegalArgumentException(); 
/* 661*/      this.iType = ZoneInfoCompiler.parseOptional(param1StringTokenizer.nextToken());
/* 662*/      this.iDateTimeOfYear = new ZoneInfoCompiler.DateTimeOfYear(param1StringTokenizer);
/* 663*/      this.iSaveMillis = ZoneInfoCompiler.parseTime(param1StringTokenizer.nextToken());
/* 664*/      this.iLetterS = ZoneInfoCompiler.parseOptional(param1StringTokenizer.nextToken());
/*   0*/    }
/*   0*/    
/*   0*/    public void addRecurring(DateTimeZoneBuilder param1DateTimeZoneBuilder, String param1String) {
/* 671*/      String str = formatName(param1String);
/* 672*/      this.iDateTimeOfYear.addRecurring(param1DateTimeZoneBuilder, str, this.iSaveMillis, this.iFromYear, this.iToYear);
/*   0*/    }
/*   0*/    
/*   0*/    private String formatName(String param1String) {
/*   0*/      String str3;
/* 677*/      int i = param1String.indexOf('/');
/* 678*/      if (i > 0) {
/* 679*/        if (this.iSaveMillis == 0)
/* 681*/          return param1String.substring(0, i).intern(); 
/* 683*/        return param1String.substring(i + 1).intern();
/*   0*/      } 
/* 686*/      i = param1String.indexOf("%s");
/* 687*/      if (i < 0)
/* 688*/        return param1String; 
/* 690*/      String str1 = param1String.substring(0, i);
/* 691*/      String str2 = param1String.substring(i + 2);
/* 693*/      if (this.iLetterS == null) {
/* 694*/        str3 = str1.concat(str2);
/*   0*/      } else {
/* 696*/        str3 = str1 + this.iLetterS + str2;
/*   0*/      } 
/* 698*/      return str3.intern();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 702*/      return "[Rule]\nName: " + this.iName + "\n" + "FromYear: " + this.iFromYear + "\n" + "ToYear: " + this.iToYear + "\n" + "Type: " + this.iType + "\n" + this.iDateTimeOfYear + "SaveMillis: " + this.iSaveMillis + "\n" + "LetterS: " + this.iLetterS + "\n";
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class RuleSet {
/*   0*/    private List<ZoneInfoCompiler.Rule> iRules;
/*   0*/    
/*   0*/    RuleSet(ZoneInfoCompiler.Rule param1Rule) {
/* 718*/      this.iRules = new ArrayList<ZoneInfoCompiler.Rule>();
/* 719*/      this.iRules.add(param1Rule);
/*   0*/    }
/*   0*/    
/*   0*/    void addRule(ZoneInfoCompiler.Rule param1Rule) {
/* 723*/      if (!param1Rule.iName.equals(((ZoneInfoCompiler.Rule)this.iRules.get(0)).iName))
/* 724*/        throw new IllegalArgumentException("Rule name mismatch"); 
/* 726*/      this.iRules.add(param1Rule);
/*   0*/    }
/*   0*/    
/*   0*/    public void addRecurring(DateTimeZoneBuilder param1DateTimeZoneBuilder, String param1String) {
/* 733*/      for (int i = 0; i < this.iRules.size(); i++) {
/* 734*/        ZoneInfoCompiler.Rule rule = this.iRules.get(i);
/* 735*/        rule.addRecurring(param1DateTimeZoneBuilder, param1String);
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class Zone {
/*   0*/    public final String iName;
/*   0*/    
/*   0*/    public final int iOffsetMillis;
/*   0*/    
/*   0*/    public final String iRules;
/*   0*/    
/*   0*/    public final String iFormat;
/*   0*/    
/*   0*/    public final int iUntilYear;
/*   0*/    
/*   0*/    public final ZoneInfoCompiler.DateTimeOfYear iUntilDateTimeOfYear;
/*   0*/    
/*   0*/    private Zone iNext;
/*   0*/    
/*   0*/    Zone(StringTokenizer param1StringTokenizer) {
/* 751*/      this(param1StringTokenizer.nextToken(), param1StringTokenizer);
/*   0*/    }
/*   0*/    
/*   0*/    private Zone(String param1String, StringTokenizer param1StringTokenizer) {
/* 755*/      this.iName = param1String.intern();
/* 756*/      this.iOffsetMillis = ZoneInfoCompiler.parseTime(param1StringTokenizer.nextToken());
/* 757*/      this.iRules = ZoneInfoCompiler.parseOptional(param1StringTokenizer.nextToken());
/* 758*/      this.iFormat = param1StringTokenizer.nextToken().intern();
/* 760*/      int i = Integer.MAX_VALUE;
/* 761*/      ZoneInfoCompiler.DateTimeOfYear dateTimeOfYear = ZoneInfoCompiler.getStartOfYear();
/* 763*/      if (param1StringTokenizer.hasMoreTokens()) {
/* 764*/        i = Integer.parseInt(param1StringTokenizer.nextToken());
/* 765*/        if (param1StringTokenizer.hasMoreTokens())
/* 766*/          dateTimeOfYear = new ZoneInfoCompiler.DateTimeOfYear(param1StringTokenizer); 
/*   0*/      } 
/* 770*/      this.iUntilYear = i;
/* 771*/      this.iUntilDateTimeOfYear = dateTimeOfYear;
/*   0*/    }
/*   0*/    
/*   0*/    void chain(StringTokenizer param1StringTokenizer) {
/* 775*/      if (this.iNext != null) {
/* 776*/        this.iNext.chain(param1StringTokenizer);
/*   0*/      } else {
/* 778*/        this.iNext = new Zone(this.iName, param1StringTokenizer);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public void addToBuilder(DateTimeZoneBuilder param1DateTimeZoneBuilder, Map<String, ZoneInfoCompiler.RuleSet> param1Map) {
/* 794*/      addToBuilder(this, param1DateTimeZoneBuilder, param1Map);
/*   0*/    }
/*   0*/    
/*   0*/    private static void addToBuilder(Zone param1Zone, DateTimeZoneBuilder param1DateTimeZoneBuilder, Map<String, ZoneInfoCompiler.RuleSet> param1Map) {
/* 801*/      for (; param1Zone != null; param1Zone = param1Zone.iNext) {
/* 802*/        param1DateTimeZoneBuilder.setStandardOffset(param1Zone.iOffsetMillis);
/* 804*/        if (param1Zone.iRules == null) {
/* 805*/          param1DateTimeZoneBuilder.setFixedSavings(param1Zone.iFormat, 0);
/*   0*/        } else {
/*   0*/          try {
/* 809*/            int i = ZoneInfoCompiler.parseTime(param1Zone.iRules);
/* 810*/            param1DateTimeZoneBuilder.setFixedSavings(param1Zone.iFormat, i);
/* 812*/          } catch (Exception exception) {
/* 813*/            ZoneInfoCompiler.RuleSet ruleSet = param1Map.get(param1Zone.iRules);
/* 814*/            if (ruleSet == null)
/* 815*/              throw new IllegalArgumentException("Rules not found: " + param1Zone.iRules); 
/* 818*/            ruleSet.addRecurring(param1DateTimeZoneBuilder, param1Zone.iFormat);
/*   0*/          } 
/*   0*/        } 
/* 822*/        if (param1Zone.iUntilYear == Integer.MAX_VALUE)
/*   0*/          break; 
/* 826*/        param1Zone.iUntilDateTimeOfYear.addCutover(param1DateTimeZoneBuilder, param1Zone.iUntilYear);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 831*/      String str = "[Zone]\nName: " + this.iName + "\n" + "OffsetMillis: " + this.iOffsetMillis + "\n" + "Rules: " + this.iRules + "\n" + "Format: " + this.iFormat + "\n" + "UntilYear: " + this.iUntilYear + "\n" + this.iUntilDateTimeOfYear;
/* 840*/      if (this.iNext == null)
/* 841*/        return str; 
/* 844*/      return str + "...\n" + this.iNext.toString();
/*   0*/    }
/*   0*/  }
/*   0*/}
