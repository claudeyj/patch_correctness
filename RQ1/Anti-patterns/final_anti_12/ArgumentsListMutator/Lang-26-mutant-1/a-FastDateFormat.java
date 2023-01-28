/*   0*/package org.apache.commons.lang3.time;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.text.DateFormat;
/*   0*/import java.text.DateFormatSymbols;
/*   0*/import java.text.FieldPosition;
/*   0*/import java.text.Format;
/*   0*/import java.text.ParsePosition;
/*   0*/import java.text.SimpleDateFormat;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Date;
/*   0*/import java.util.GregorianCalendar;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.TimeZone;
/*   0*/import org.apache.commons.lang3.Validate;
/*   0*/
/*   0*/public class FastDateFormat extends Format {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public static final int FULL = 0;
/*   0*/  
/*   0*/  public static final int LONG = 1;
/*   0*/  
/*   0*/  public static final int MEDIUM = 2;
/*   0*/  
/*   0*/  public static final int SHORT = 3;
/*   0*/  
/*   0*/  private static String cDefaultPattern;
/*   0*/  
/* 111*/  private static final Map<FastDateFormat, FastDateFormat> cInstanceCache = new HashMap<FastDateFormat, FastDateFormat>(7);
/*   0*/  
/* 112*/  private static final Map<Object, FastDateFormat> cDateInstanceCache = new HashMap<Object, FastDateFormat>(7);
/*   0*/  
/* 113*/  private static final Map<Object, FastDateFormat> cTimeInstanceCache = new HashMap<Object, FastDateFormat>(7);
/*   0*/  
/* 114*/  private static final Map<Object, FastDateFormat> cDateTimeInstanceCache = new HashMap<Object, FastDateFormat>(7);
/*   0*/  
/* 115*/  private static final Map<Object, String> cTimeZoneDisplayCache = new HashMap<Object, String>(7);
/*   0*/  
/*   0*/  private final String mPattern;
/*   0*/  
/*   0*/  private final TimeZone mTimeZone;
/*   0*/  
/*   0*/  private final boolean mTimeZoneForced;
/*   0*/  
/*   0*/  private final Locale mLocale;
/*   0*/  
/*   0*/  private final boolean mLocaleForced;
/*   0*/  
/*   0*/  private transient Rule[] mRules;
/*   0*/  
/*   0*/  private transient int mMaxLengthEstimate;
/*   0*/  
/*   0*/  public static FastDateFormat getInstance() {
/* 154*/    return getInstance(getDefaultPattern(), null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getInstance(String pattern) {
/* 167*/    return getInstance(pattern, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
/* 182*/    return getInstance(pattern, timeZone, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getInstance(String pattern, Locale locale) {
/* 196*/    return getInstance(pattern, null, locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static synchronized FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
/* 213*/    FastDateFormat emptyFormat = new FastDateFormat(pattern, timeZone, locale);
/* 214*/    FastDateFormat format = cInstanceCache.get(emptyFormat);
/* 215*/    if (format == null) {
/* 216*/      format = emptyFormat;
/* 217*/      format.init();
/* 218*/      cInstanceCache.put(format, format);
/*   0*/    } 
/* 220*/    return format;
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getDateInstance(int style) {
/* 235*/    return getDateInstance(style, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getDateInstance(int style, Locale locale) {
/* 250*/    return getDateInstance(style, null, locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
/* 266*/    return getDateInstance(style, timeZone, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static synchronized FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
/* 281*/    Object key = style;
/* 282*/    if (timeZone != null)
/* 283*/      key = new Pair(key, timeZone); 
/* 286*/    if (locale == null)
/* 287*/      locale = Locale.getDefault(); 
/* 290*/    key = new Pair(key, locale);
/* 292*/    FastDateFormat format = cDateInstanceCache.get(key);
/* 293*/    if (format == null)
/*   0*/      try {
/* 295*/        SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateInstance(style, locale);
/* 296*/        String pattern = formatter.toPattern();
/* 297*/        format = getInstance(pattern, timeZone, locale);
/* 298*/        cDateInstanceCache.put(key, format);
/* 300*/      } catch (ClassCastException ex) {
/* 301*/        throw new IllegalArgumentException("No date pattern for locale: " + locale);
/*   0*/      }  
/* 304*/    return format;
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getTimeInstance(int style) {
/* 319*/    return getTimeInstance(style, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getTimeInstance(int style, Locale locale) {
/* 334*/    return getTimeInstance(style, null, locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
/* 350*/    return getTimeInstance(style, timeZone, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static synchronized FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
/* 366*/    Object key = style;
/* 367*/    if (timeZone != null)
/* 368*/      key = new Pair(key, timeZone); 
/* 370*/    if (locale != null)
/* 371*/      key = new Pair(key, locale); 
/* 374*/    FastDateFormat format = cTimeInstanceCache.get(key);
/* 375*/    if (format == null) {
/* 376*/      if (locale == null)
/* 377*/        locale = Locale.getDefault(); 
/*   0*/      try {
/* 381*/        SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getTimeInstance(style, locale);
/* 382*/        String pattern = formatter.toPattern();
/* 383*/        format = getInstance(pattern, timeZone, locale);
/* 384*/        cTimeInstanceCache.put(key, format);
/* 386*/      } catch (ClassCastException ex) {
/* 387*/        throw new IllegalArgumentException("No date pattern for locale: " + locale);
/*   0*/      } 
/*   0*/    } 
/* 390*/    return format;
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
/* 407*/    return getDateTimeInstance(dateStyle, timeStyle, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
/* 424*/    return getDateTimeInstance(dateStyle, timeStyle, null, locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
/* 442*/    return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static synchronized FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
/* 460*/    Object key = new Pair(dateStyle, timeStyle);
/* 461*/    if (timeZone != null)
/* 462*/      key = new Pair(key, timeZone); 
/* 464*/    if (locale == null)
/* 465*/      locale = Locale.getDefault(); 
/* 467*/    key = new Pair(key, locale);
/* 469*/    FastDateFormat format = cDateTimeInstanceCache.get(key);
/* 470*/    if (format == null)
/*   0*/      try {
/* 472*/        SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
/* 474*/        String pattern = formatter.toPattern();
/* 475*/        format = getInstance(pattern, timeZone, locale);
/* 476*/        cDateTimeInstanceCache.put(key, format);
/* 478*/      } catch (ClassCastException ex) {
/* 479*/        throw new IllegalArgumentException("No date time pattern for locale: " + locale);
/*   0*/      }  
/* 482*/    return format;
/*   0*/  }
/*   0*/  
/*   0*/  static synchronized String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
/* 497*/    Object key = new TimeZoneDisplayKey(tz, daylight, style, locale);
/* 498*/    String value = cTimeZoneDisplayCache.get(key);
/* 499*/    if (value == null) {
/* 501*/      value = tz.getDisplayName(daylight, style, locale);
/* 502*/      cTimeZoneDisplayCache.put(key, value);
/*   0*/    } 
/* 504*/    return value;
/*   0*/  }
/*   0*/  
/*   0*/  private static synchronized String getDefaultPattern() {
/* 513*/    if (cDefaultPattern == null)
/* 514*/      cDefaultPattern = new SimpleDateFormat().toPattern(); 
/* 516*/    return cDefaultPattern;
/*   0*/  }
/*   0*/  
/*   0*/  protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
/* 536*/    if (pattern == null)
/* 537*/      throw new IllegalArgumentException("The pattern must not be null"); 
/* 539*/    this.mPattern = pattern;
/* 541*/    this.mTimeZoneForced = (timeZone != null);
/* 542*/    if (timeZone == null)
/* 543*/      timeZone = TimeZone.getDefault(); 
/* 545*/    this.mTimeZone = timeZone;
/* 547*/    this.mLocaleForced = (locale != null);
/* 548*/    if (locale == null)
/* 549*/      locale = Locale.getDefault(); 
/* 551*/    this.mLocale = locale;
/*   0*/  }
/*   0*/  
/*   0*/  protected void init() {
/* 558*/    List<Rule> rulesList = parsePattern();
/* 559*/    this.mRules = rulesList.<Rule>toArray(new Rule[rulesList.size()]);
/* 561*/    int len = 0;
/* 562*/    for (int i = this.mRules.length; --i >= 0;)
/* 563*/      len += this.mRules[i].estimateLength(); 
/* 566*/    this.mMaxLengthEstimate = len;
/*   0*/  }
/*   0*/  
/*   0*/  protected List<Rule> parsePattern() {
/* 578*/    DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
/* 579*/    List<Rule> rules = new ArrayList<Rule>();
/* 581*/    String[] ERAs = symbols.getEras();
/* 582*/    String[] months = symbols.getMonths();
/* 583*/    String[] shortMonths = symbols.getShortMonths();
/* 584*/    String[] weekdays = symbols.getWeekdays();
/* 585*/    String[] shortWeekdays = symbols.getShortWeekdays();
/* 586*/    String[] AmPmStrings = symbols.getAmPmStrings();
/* 588*/    int length = this.mPattern.length();
/* 589*/    int[] indexRef = new int[1];
/* 591*/    for (int i = 0; i < length; i++) {
/*   0*/      Rule rule;
/*   0*/      String sub;
/* 592*/      indexRef[0] = i;
/* 593*/      String token = parseToken(this.mPattern, indexRef);
/* 594*/      i = indexRef[0];
/* 596*/      int tokenLen = token.length();
/* 597*/      if (tokenLen == 0)
/*   0*/        break; 
/* 602*/      char c = token.charAt(0);
/* 604*/      switch (c) {
/*   0*/        case 'G':
/* 606*/          rule = new TextField(0, ERAs);
/*   0*/          break;
/*   0*/        case 'y':
/* 609*/          if (tokenLen >= 4) {
/* 610*/            rule = selectNumberRule(1, tokenLen);
/*   0*/            break;
/*   0*/          } 
/* 612*/          rule = TwoDigitYearField.INSTANCE;
/*   0*/          break;
/*   0*/        case 'M':
/* 616*/          if (tokenLen >= 4) {
/* 617*/            rule = new TextField(2, months);
/*   0*/            break;
/*   0*/          } 
/* 618*/          if (tokenLen == 3) {
/* 619*/            rule = new TextField(2, shortMonths);
/*   0*/            break;
/*   0*/          } 
/* 620*/          if (tokenLen == 2) {
/* 621*/            rule = TwoDigitMonthField.INSTANCE;
/*   0*/            break;
/*   0*/          } 
/* 623*/          rule = UnpaddedMonthField.INSTANCE;
/*   0*/          break;
/*   0*/        case 'd':
/* 627*/          rule = selectNumberRule(5, tokenLen);
/*   0*/          break;
/*   0*/        case 'h':
/* 630*/          rule = new TwelveHourField(selectNumberRule(10, tokenLen));
/*   0*/          break;
/*   0*/        case 'H':
/* 633*/          rule = selectNumberRule(11, tokenLen);
/*   0*/          break;
/*   0*/        case 'm':
/* 636*/          rule = selectNumberRule(12, tokenLen);
/*   0*/          break;
/*   0*/        case 's':
/* 639*/          rule = selectNumberRule(13, tokenLen);
/*   0*/          break;
/*   0*/        case 'S':
/* 642*/          rule = selectNumberRule(14, tokenLen);
/*   0*/          break;
/*   0*/        case 'E':
/* 645*/          rule = new TextField(7, (tokenLen < 4) ? shortWeekdays : weekdays);
/*   0*/          break;
/*   0*/        case 'D':
/* 648*/          rule = selectNumberRule(6, tokenLen);
/*   0*/          break;
/*   0*/        case 'F':
/* 651*/          rule = selectNumberRule(8, tokenLen);
/*   0*/          break;
/*   0*/        case 'w':
/* 654*/          rule = selectNumberRule(3, tokenLen);
/*   0*/          break;
/*   0*/        case 'W':
/* 657*/          rule = selectNumberRule(4, tokenLen);
/*   0*/          break;
/*   0*/        case 'a':
/* 660*/          rule = new TextField(9, AmPmStrings);
/*   0*/          break;
/*   0*/        case 'k':
/* 663*/          rule = new TwentyFourHourField(selectNumberRule(11, tokenLen));
/*   0*/          break;
/*   0*/        case 'K':
/* 666*/          rule = selectNumberRule(10, tokenLen);
/*   0*/          break;
/*   0*/        case 'z':
/* 669*/          if (tokenLen >= 4) {
/* 670*/            rule = new TimeZoneNameRule(this.mTimeZone, this.mTimeZoneForced, this.mLocale, 1);
/*   0*/            break;
/*   0*/          } 
/* 672*/          rule = new TimeZoneNameRule(this.mTimeZone, this.mTimeZoneForced, this.mLocale, 0);
/*   0*/          break;
/*   0*/        case 'Z':
/* 676*/          if (tokenLen == 1) {
/* 677*/            rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
/*   0*/            break;
/*   0*/          } 
/* 679*/          rule = TimeZoneNumberRule.INSTANCE_COLON;
/*   0*/          break;
/*   0*/        case '\'':
/* 683*/          sub = token.substring(1);
/* 684*/          if (sub.length() == 1) {
/* 685*/            rule = new CharacterLiteral(sub.charAt(0));
/*   0*/            break;
/*   0*/          } 
/* 687*/          rule = new StringLiteral(sub);
/*   0*/          break;
/*   0*/        default:
/* 691*/          throw new IllegalArgumentException("Illegal pattern component: " + token);
/*   0*/      } 
/* 694*/      rules.add(rule);
/*   0*/    } 
/* 697*/    return rules;
/*   0*/  }
/*   0*/  
/*   0*/  protected String parseToken(String pattern, int[] indexRef) {
/* 708*/    StringBuilder buf = new StringBuilder();
/* 710*/    int i = indexRef[0];
/* 711*/    int length = pattern.length();
/* 713*/    char c = pattern.charAt(i);
/* 714*/    if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
/* 717*/      buf.append(c);
/* 719*/      while (i + 1 < length) {
/* 720*/        char peek = pattern.charAt(i + 1);
/* 721*/        if (peek == c) {
/* 722*/          buf.append(c);
/* 723*/          i++;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/* 730*/      buf.append('\'');
/*   0*/      boolean inLiteral = false;
/* 734*/      for (; i < length; i++) {
/* 735*/        c = pattern.charAt(i);
/* 737*/        if (c == '\'') {
/* 738*/          if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
/* 740*/            i++;
/* 741*/            buf.append(c);
/*   0*/          } else {
/* 743*/            inLiteral = !inLiteral;
/*   0*/          } 
/*   0*/        } else {
/* 745*/          if (!inLiteral && ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
/* 747*/            i--;
/*   0*/            break;
/*   0*/          } 
/* 750*/          buf.append(c);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 755*/    indexRef[0] = i;
/* 756*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected NumberRule selectNumberRule(int field, int padding) {
/* 767*/    switch (padding) {
/*   0*/      case 1:
/* 769*/        return new UnpaddedNumberField(field);
/*   0*/      case 2:
/* 771*/        return new TwoDigitNumberField(field);
/*   0*/    } 
/* 773*/    return new PaddedNumberField(field, padding);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
/* 790*/    if (obj instanceof Date)
/* 791*/      return format((Date)obj, toAppendTo); 
/* 792*/    if (obj instanceof Calendar)
/* 793*/      return format((Calendar)obj, toAppendTo); 
/* 794*/    if (obj instanceof Long)
/* 795*/      return format((Long)obj, toAppendTo); 
/* 797*/    throw new IllegalArgumentException("Unknown class: " + ((obj == null) ? "<null>" : obj.getClass().getName()));
/*   0*/  }
/*   0*/  
/*   0*/  public String format(long millis) {
/* 810*/    return format(new Date(millis));
/*   0*/  }
/*   0*/  
/*   0*/  public String format(Date date) {
/* 820*/    Calendar c = new GregorianCalendar(this.mTimeZone);
/* 821*/    c.setTime(date);
/* 822*/    return applyRules(c, new StringBuffer(this.mMaxLengthEstimate)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String format(Calendar calendar) {
/* 832*/    return format(calendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer format(long millis, StringBuffer buf) {
/* 845*/    return format(new Date(millis), buf);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer format(Date date, StringBuffer buf) {
/* 857*/    Calendar c = new GregorianCalendar(this.mTimeZone);
/* 858*/    c.setTime(date);
/* 859*/    return applyRules(c, buf);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer format(Calendar calendar, StringBuffer buf) {
/* 871*/    if (this.mTimeZoneForced) {
/* 872*/      calendar.getTimeInMillis();
/* 873*/      calendar = (Calendar)calendar.clone();
/* 874*/      calendar.setTimeZone(this.mTimeZone);
/*   0*/    } 
/* 876*/    return applyRules(calendar, buf);
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
/* 888*/    Rule[] rules = this.mRules;
/* 889*/    int len = this.mRules.length;
/* 890*/    for (int i = 0; i < len; i++)
/* 891*/      rules[i].appendTo(buf, calendar); 
/* 893*/    return buf;
/*   0*/  }
/*   0*/  
/*   0*/  public Object parseObject(String source, ParsePosition pos) {
/* 907*/    pos.setIndex(0);
/* 908*/    pos.setErrorIndex(0);
/* 909*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String getPattern() {
/* 920*/    return this.mPattern;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeZone getTimeZone() {
/* 934*/    return this.mTimeZone;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getTimeZoneOverridesCalendar() {
/* 945*/    return this.mTimeZoneForced;
/*   0*/  }
/*   0*/  
/*   0*/  public Locale getLocale() {
/* 954*/    return this.mLocale;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxLengthEstimate() {
/* 967*/    return this.mMaxLengthEstimate;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 980*/    if (!(obj instanceof FastDateFormat))
/* 981*/      return false; 
/* 983*/    FastDateFormat other = (FastDateFormat)obj;
/* 984*/    if ((this.mPattern == other.mPattern || this.mPattern.equals(other.mPattern)) && (this.mTimeZone == other.mTimeZone || this.mTimeZone.equals(other.mTimeZone)) && (this.mLocale == other.mLocale || this.mLocale.equals(other.mLocale)) && this.mTimeZoneForced == other.mTimeZoneForced && this.mLocaleForced == other.mLocaleForced)
/* 991*/      return true; 
/* 993*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1003*/    int total = 0;
/*1004*/    total += this.mPattern.hashCode();
/*1005*/    total += this.mTimeZone.hashCode();
/*1006*/    total += this.mTimeZoneForced ? 1 : 0;
/*1007*/    total += this.mLocale.hashCode();
/*1008*/    total += this.mLocaleForced ? 1 : 0;
/*1009*/    return total;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1019*/    return "FastDateFormat[" + this.mPattern + "]";
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/*1033*/    in.defaultReadObject();
/*1034*/    init();
/*   0*/  }
/*   0*/  
/*   0*/  private static interface Rule {
/*   0*/    int estimateLength();
/*   0*/    
/*   0*/    void appendTo(StringBuffer param1StringBuffer, Calendar param1Calendar);
/*   0*/  }
/*   0*/  
/*   0*/  private static interface NumberRule extends Rule {
/*   0*/    void appendTo(StringBuffer param1StringBuffer, int param1Int);
/*   0*/  }
/*   0*/  
/*   0*/  private static class CharacterLiteral implements Rule {
/*   0*/    private final char mValue;
/*   0*/    
/*   0*/    CharacterLiteral(char value) {
/*1085*/      this.mValue = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1092*/      return 1;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1099*/      buffer.append(this.mValue);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class StringLiteral implements Rule {
/*   0*/    private final String mValue;
/*   0*/    
/*   0*/    StringLiteral(String value) {
/*1116*/      this.mValue = value;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1123*/      return this.mValue.length();
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1130*/      buffer.append(this.mValue);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TextField implements Rule {
/*   0*/    private final int mField;
/*   0*/    
/*   0*/    private final String[] mValues;
/*   0*/    
/*   0*/    TextField(int field, String[] values) {
/*1149*/      this.mField = field;
/*1150*/      this.mValues = values;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1157*/      int max = 0;
/*1158*/      for (int i = this.mValues.length; --i >= 0; ) {
/*1159*/        int len = this.mValues[i].length();
/*1160*/        if (len > max)
/*1161*/          max = len; 
/*   0*/      } 
/*1164*/      return max;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1171*/      buffer.append(this.mValues[calendar.get(this.mField)]);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class UnpaddedNumberField implements NumberRule {
/*   0*/    private final int mField;
/*   0*/    
/*   0*/    UnpaddedNumberField(int field) {
/*1187*/      this.mField = field;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1194*/      return 4;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1201*/      appendTo(buffer, calendar.get(this.mField));
/*   0*/    }
/*   0*/    
/*   0*/    public final void appendTo(StringBuffer buffer, int value) {
/*1208*/      if (value < 10) {
/*1209*/        buffer.append((char)(value + 48));
/*1210*/      } else if (value < 100) {
/*1211*/        buffer.append((char)(value / 10 + 48));
/*1212*/        buffer.append((char)(value % 10 + 48));
/*   0*/      } else {
/*1214*/        buffer.append(Integer.toString(value));
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class UnpaddedMonthField implements NumberRule {
/*1223*/    static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();
/*   0*/    
/*   0*/    public int estimateLength() {
/*1237*/      return 2;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1244*/      appendTo(buffer, calendar.get(2) + 1);
/*   0*/    }
/*   0*/    
/*   0*/    public final void appendTo(StringBuffer buffer, int value) {
/*1251*/      if (value < 10) {
/*1252*/        buffer.append((char)(value + 48));
/*   0*/      } else {
/*1254*/        buffer.append((char)(value / 10 + 48));
/*1255*/        buffer.append((char)(value % 10 + 48));
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class PaddedNumberField implements NumberRule {
/*   0*/    private final int mField;
/*   0*/    
/*   0*/    private final int mSize;
/*   0*/    
/*   0*/    PaddedNumberField(int field, int size) {
/*1274*/      if (size < 3)
/*1276*/        throw new IllegalArgumentException(); 
/*1278*/      this.mField = field;
/*1279*/      this.mSize = size;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1286*/      return 4;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1293*/      appendTo(buffer, calendar.get(this.mField));
/*   0*/    }
/*   0*/    
/*   0*/    public final void appendTo(StringBuffer buffer, int value) {
/*1300*/      if (value < 100) {
/*1301*/        for (int i = this.mSize; --i >= 2;)
/*1302*/          buffer.append('0'); 
/*1304*/        buffer.append((char)(value / 10 + 48));
/*1305*/        buffer.append((char)(value % 10 + 48));
/*   0*/      } else {
/*   0*/        int digits;
/*1308*/        if (value < 1000) {
/*1309*/          digits = 3;
/*   0*/        } else {
/*1311*/          Validate.isTrue((value > -1), "Negative values should not be possible", value);
/*1312*/          digits = Integer.toString(value).length();
/*   0*/        } 
/*1314*/        for (int i = this.mSize; --i >= digits;)
/*1315*/          buffer.append('0'); 
/*1317*/        buffer.append(Integer.toString(value));
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TwoDigitNumberField implements NumberRule {
/*   0*/    private final int mField;
/*   0*/    
/*   0*/    TwoDigitNumberField(int field) {
/*1334*/      this.mField = field;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1341*/      return 2;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1348*/      appendTo(buffer, calendar.get(this.mField));
/*   0*/    }
/*   0*/    
/*   0*/    public final void appendTo(StringBuffer buffer, int value) {
/*1355*/      if (value < 100) {
/*1356*/        buffer.append((char)(value / 10 + 48));
/*1357*/        buffer.append((char)(value % 10 + 48));
/*   0*/      } else {
/*1359*/        buffer.append(Integer.toString(value));
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TwoDigitYearField implements NumberRule {
/*1368*/    static final TwoDigitYearField INSTANCE = new TwoDigitYearField();
/*   0*/    
/*   0*/    public int estimateLength() {
/*1381*/      return 2;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1388*/      appendTo(buffer, calendar.get(1) % 100);
/*   0*/    }
/*   0*/    
/*   0*/    public final void appendTo(StringBuffer buffer, int value) {
/*1395*/      buffer.append((char)(value / 10 + 48));
/*1396*/      buffer.append((char)(value % 10 + 48));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TwoDigitMonthField implements NumberRule {
/*1404*/    static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();
/*   0*/    
/*   0*/    public int estimateLength() {
/*1417*/      return 2;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1424*/      appendTo(buffer, calendar.get(2) + 1);
/*   0*/    }
/*   0*/    
/*   0*/    public final void appendTo(StringBuffer buffer, int value) {
/*1431*/      buffer.append((char)(value / 10 + 48));
/*1432*/      buffer.append((char)(value % 10 + 48));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TwelveHourField implements NumberRule {
/*   0*/    private final FastDateFormat.NumberRule mRule;
/*   0*/    
/*   0*/    TwelveHourField(FastDateFormat.NumberRule rule) {
/*1449*/      this.mRule = rule;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1456*/      return this.mRule.estimateLength();
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1463*/      int value = calendar.get(10);
/*1464*/      if (value == 0)
/*1465*/        value = calendar.getLeastMaximum(10) + 1; 
/*1467*/      this.mRule.appendTo(buffer, value);
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, int value) {
/*1474*/      this.mRule.appendTo(buffer, value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TwentyFourHourField implements NumberRule {
/*   0*/    private final FastDateFormat.NumberRule mRule;
/*   0*/    
/*   0*/    TwentyFourHourField(FastDateFormat.NumberRule rule) {
/*1491*/      this.mRule = rule;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1498*/      return this.mRule.estimateLength();
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1505*/      int value = calendar.get(11);
/*1506*/      if (value == 0)
/*1507*/        value = calendar.getMaximum(11) + 1; 
/*1509*/      this.mRule.appendTo(buffer, value);
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, int value) {
/*1516*/      this.mRule.appendTo(buffer, value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TimeZoneNameRule implements Rule {
/*   0*/    private final TimeZone mTimeZone;
/*   0*/    
/*   0*/    private final boolean mTimeZoneForced;
/*   0*/    
/*   0*/    private final Locale mLocale;
/*   0*/    
/*   0*/    private final int mStyle;
/*   0*/    
/*   0*/    private final String mStandard;
/*   0*/    
/*   0*/    private final String mDaylight;
/*   0*/    
/*   0*/    TimeZoneNameRule(TimeZone timeZone, boolean timeZoneForced, Locale locale, int style) {
/*1540*/      this.mTimeZone = timeZone;
/*1541*/      this.mTimeZoneForced = timeZoneForced;
/*1542*/      this.mLocale = locale;
/*1543*/      this.mStyle = style;
/*1545*/      if (timeZoneForced) {
/*1546*/        this.mStandard = FastDateFormat.getTimeZoneDisplay(timeZone, false, style, locale);
/*1547*/        this.mDaylight = FastDateFormat.getTimeZoneDisplay(timeZone, true, style, locale);
/*   0*/      } else {
/*1549*/        this.mStandard = null;
/*1550*/        this.mDaylight = null;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1558*/      if (this.mTimeZoneForced)
/*1559*/        return Math.max(this.mStandard.length(), this.mDaylight.length()); 
/*1560*/      if (this.mStyle == 0)
/*1561*/        return 4; 
/*1563*/      return 40;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1571*/      if (this.mTimeZoneForced) {
/*1572*/        if (this.mTimeZone.useDaylightTime() && calendar.get(16) != 0) {
/*1573*/          buffer.append(this.mDaylight);
/*   0*/        } else {
/*1575*/          buffer.append(this.mStandard);
/*   0*/        } 
/*   0*/      } else {
/*1578*/        TimeZone timeZone = calendar.getTimeZone();
/*1579*/        if (timeZone.useDaylightTime() && calendar.get(16) != 0) {
/*1580*/          buffer.append(FastDateFormat.getTimeZoneDisplay(timeZone, true, this.mStyle, this.mLocale));
/*   0*/        } else {
/*1582*/          buffer.append(FastDateFormat.getTimeZoneDisplay(timeZone, false, this.mStyle, this.mLocale));
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TimeZoneNumberRule implements Rule {
/*1593*/    static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
/*   0*/    
/*1594*/    static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
/*   0*/    
/*   0*/    final boolean mColon;
/*   0*/    
/*   0*/    TimeZoneNumberRule(boolean colon) {
/*1604*/      this.mColon = colon;
/*   0*/    }
/*   0*/    
/*   0*/    public int estimateLength() {
/*1611*/      return 5;
/*   0*/    }
/*   0*/    
/*   0*/    public void appendTo(StringBuffer buffer, Calendar calendar) {
/*1618*/      int offset = calendar.get(15) + calendar.get(16);
/*1620*/      if (offset < 0) {
/*1621*/        buffer.append('-');
/*1622*/        offset = -offset;
/*   0*/      } else {
/*1624*/        buffer.append('+');
/*   0*/      } 
/*1627*/      int hours = offset / 3600000;
/*1628*/      buffer.append((char)(hours / 10 + 48));
/*1629*/      buffer.append((char)(hours % 10 + 48));
/*1631*/      if (this.mColon)
/*1632*/        buffer.append(':'); 
/*1635*/      int minutes = offset / 60000 - 60 * hours;
/*1636*/      buffer.append((char)(minutes / 10 + 48));
/*1637*/      buffer.append((char)(minutes % 10 + 48));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TimeZoneDisplayKey {
/*   0*/    private final TimeZone mTimeZone;
/*   0*/    
/*   0*/    private final int mStyle;
/*   0*/    
/*   0*/    private final Locale mLocale;
/*   0*/    
/*   0*/    TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
/*1660*/      this.mTimeZone = timeZone;
/*1661*/      if (daylight)
/*1662*/        style |= Integer.MIN_VALUE; 
/*1664*/      this.mStyle = style;
/*1665*/      this.mLocale = locale;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*1673*/      return this.mStyle * 31 + this.mLocale.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object obj) {
/*1681*/      if (this == obj)
/*1682*/        return true; 
/*1684*/      if (obj instanceof TimeZoneDisplayKey) {
/*1685*/        TimeZoneDisplayKey other = (TimeZoneDisplayKey)obj;
/*1686*/        return (this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale));
/*   0*/      } 
/*1691*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class Pair {
/*   0*/    private final Object mObj1;
/*   0*/    
/*   0*/    private final Object mObj2;
/*   0*/    
/*   0*/    public Pair(Object obj1, Object obj2) {
/*1712*/      this.mObj1 = obj1;
/*1713*/      this.mObj2 = obj2;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object obj) {
/*1721*/      if (this == obj)
/*1722*/        return true; 
/*1725*/      if (!(obj instanceof Pair))
/*1726*/        return false; 
/*1729*/      Pair key = (Pair)obj;
/*1731*/      if ((this.mObj1 == null) ? (key.mObj1 == null) : this.mObj1.equals(key.mObj1))
/*1731*/        if ((this.mObj2 == null) ? (key.mObj2 == null) : this.mObj2.equals(key.mObj2)); 
/*1731*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/*1743*/      return ((this.mObj1 == null) ? 0 : this.mObj1.hashCode()) + ((this.mObj2 == null) ? 0 : this.mObj2.hashCode());
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/*1753*/      return "[" + this.mObj1 + ':' + this.mObj2 + ']';
/*   0*/    }
/*   0*/  }
/*   0*/}
