/*   0*/package org.apache.commons.lang3.time;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.text.DateFormatSymbols;
/*   0*/import java.text.ParseException;
/*   0*/import java.text.ParsePosition;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.Date;
/*   0*/import java.util.List;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.SortedMap;
/*   0*/import java.util.TimeZone;
/*   0*/import java.util.TreeMap;
/*   0*/import java.util.concurrent.ConcurrentHashMap;
/*   0*/import java.util.concurrent.ConcurrentMap;
/*   0*/import java.util.regex.Matcher;
/*   0*/import java.util.regex.Pattern;
/*   0*/
/*   0*/public class FastDateParser implements DateParser, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  76*/  private static final ConcurrentMap<Locale, TimeZoneStrategy> tzsCache = new ConcurrentHashMap<Locale, TimeZoneStrategy>(3);
/*   0*/  
/*  79*/  static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
/*   0*/  
/*   0*/  private final String pattern;
/*   0*/  
/*   0*/  private final TimeZone timeZone;
/*   0*/  
/*   0*/  private final Locale locale;
/*   0*/  
/*   0*/  private transient Pattern parsePattern;
/*   0*/  
/*   0*/  private transient Strategy[] strategies;
/*   0*/  
/*   0*/  private transient int thisYear;
/*   0*/  
/*   0*/  private transient ConcurrentMap<Integer, KeyValue[]> nameValues;
/*   0*/  
/*   0*/  private transient String currentFormatField;
/*   0*/  
/*   0*/  private transient Strategy nextStrategy;
/*   0*/  
/*   0*/  protected FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
/* 105*/    this.pattern = pattern;
/* 106*/    this.timeZone = timeZone;
/* 107*/    this.locale = locale;
/* 108*/    init();
/*   0*/  }
/*   0*/  
/*   0*/  private void init() {
/* 116*/    this.thisYear = Calendar.getInstance(this.timeZone, this.locale).get(1);
/* 118*/    this.nameValues = (ConcurrentMap)new ConcurrentHashMap<Integer, KeyValue>();
/* 120*/    StringBuilder regex = new StringBuilder();
/* 121*/    List<Strategy> collector = new ArrayList<Strategy>();
/* 123*/    Matcher patternMatcher = formatPattern.matcher(this.pattern);
/* 124*/    if (!patternMatcher.lookingAt())
/* 125*/      throw new IllegalArgumentException("Invalid pattern"); 
/* 128*/    this.currentFormatField = patternMatcher.group();
/* 129*/    Strategy currentStrategy = getStrategy(this.currentFormatField);
/*   0*/    while (true) {
/* 131*/      patternMatcher.region(patternMatcher.end(), patternMatcher.regionEnd());
/* 132*/      if (!patternMatcher.lookingAt()) {
/* 133*/        this.nextStrategy = null;
/*   0*/        break;
/*   0*/      } 
/* 136*/      String nextFormatField = patternMatcher.group();
/* 137*/      this.nextStrategy = getStrategy(nextFormatField);
/* 138*/      if (currentStrategy.addRegex(this, regex))
/* 139*/        collector.add(currentStrategy); 
/* 141*/      this.currentFormatField = nextFormatField;
/* 142*/      currentStrategy = this.nextStrategy;
/*   0*/    } 
/* 144*/    if (currentStrategy.addRegex(this, regex))
/* 145*/      collector.add(currentStrategy); 
/* 147*/    this.currentFormatField = null;
/* 148*/    this.strategies = collector.<Strategy>toArray(new Strategy[collector.size()]);
/* 149*/    this.parsePattern = Pattern.compile(regex.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public String getPattern() {
/* 159*/    return this.pattern;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeZone getTimeZone() {
/* 167*/    return this.timeZone;
/*   0*/  }
/*   0*/  
/*   0*/  public Locale getLocale() {
/* 175*/    return this.locale;
/*   0*/  }
/*   0*/  
/*   0*/  Pattern getParsePattern() {
/* 180*/    return this.parsePattern;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 193*/    if (!(obj instanceof FastDateParser))
/* 194*/      return false; 
/* 196*/    FastDateParser other = (FastDateParser)obj;
/* 197*/    return (this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale));
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 209*/    return this.pattern.hashCode() + 13 * (this.timeZone.hashCode() + 13 * this.locale.hashCode());
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 219*/    return "FastDateParser[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 233*/    in.defaultReadObject();
/* 234*/    init();
/*   0*/  }
/*   0*/  
/*   0*/  public Object parseObject(String source) throws ParseException {
/* 242*/    return parse(source);
/*   0*/  }
/*   0*/  
/*   0*/  public Date parse(String source) throws ParseException {
/* 250*/    Date date = parse(source, new ParsePosition(0));
/* 251*/    if (date == null) {
/* 253*/      if (this.locale.equals(JAPANESE_IMPERIAL))
/* 254*/        throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\n" + "Unparseable date: \"" + source + "\" does not match " + this.parsePattern.pattern(), 0); 
/* 258*/      throw new ParseException("Unparseable date: \"" + source + "\" does not match " + this.parsePattern.pattern(), 0);
/*   0*/    } 
/* 260*/    return date;
/*   0*/  }
/*   0*/  
/*   0*/  public Object parseObject(String source, ParsePosition pos) {
/* 268*/    return parse(source, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public Date parse(String source, ParsePosition pos) {
/* 276*/    int offset = pos.getIndex();
/* 277*/    Matcher matcher = this.parsePattern.matcher(source.substring(offset));
/* 278*/    if (!matcher.lookingAt())
/* 279*/      return null; 
/* 282*/    Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
/* 283*/    cal.clear();
/* 285*/    for (int i = 0; i < this.strategies.length; ) {
/* 286*/      Strategy strategy = this.strategies[i++];
/* 287*/      strategy.setCalendar(this, cal, matcher.group(i));
/*   0*/    } 
/* 289*/    pos.setIndex(offset + matcher.end());
/* 290*/    return cal.getTime();
/*   0*/  }
/*   0*/  
/*   0*/  private static StringBuilder escapeRegex(StringBuilder regex, String value, boolean unquote) {
/*   0*/    boolean wasWhite = false;
/* 305*/    for (int i = 0; i < value.length(); i++) {
/* 306*/      char c = value.charAt(i);
/* 307*/      if (Character.isWhitespace(c)) {
/* 308*/        if (!wasWhite) {
/* 309*/          wasWhite = true;
/* 310*/          String str = "\\s*+";
/* 310*/          regex.append(c);
/*   0*/        } 
/*   0*/      } else {
/* 314*/        wasWhite = false;
/* 315*/        switch (c) {
/*   0*/          case '\'':
/* 317*/            if (unquote) {
/* 318*/              if (++i == value.length())
/* 319*/                return regex; 
/* 321*/              c = value.charAt(i);
/*   0*/            } 
/*   0*/            break;
/*   0*/          case '$':
/*   0*/          case '(':
/*   0*/          case ')':
/*   0*/          case '*':
/*   0*/          case '+':
/*   0*/          case '.':
/*   0*/          case '?':
/*   0*/          case '[':
/*   0*/          case '\\':
/*   0*/          case ']':
/*   0*/          case '^':
/*   0*/          case '{':
/*   0*/          case '|':
/*   0*/          case '}':
/* 338*/            regex.append('\\');
/*   0*/            break;
/*   0*/        } 
/* 340*/        regex.append(c);
/*   0*/      } 
/*   0*/    } 
/* 342*/    return regex;
/*   0*/  }
/*   0*/  
/*   0*/  private static class KeyValue {
/*   0*/    public String key;
/*   0*/    
/*   0*/    public int value;
/*   0*/    
/*   0*/    public KeyValue(String key, int value) {
/* 358*/      this.key = key;
/* 359*/      this.value = value;
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 366*/  private static final Comparator<KeyValue> IGNORE_CASE_COMPARATOR = new Comparator<KeyValue>() {
/*   0*/      public int compare(FastDateParser.KeyValue left, FastDateParser.KeyValue right) {
/* 369*/        return left.key.compareToIgnoreCase(right.key);
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  KeyValue[] getDisplayNames(int field) {
/* 379*/    Integer fieldInt = field;
/* 380*/    KeyValue[] fieldKeyValues = this.nameValues.get(fieldInt);
/* 381*/    if (fieldKeyValues == null) {
/*   0*/      Calendar c;
/*   0*/      String[] shortEras, longEras;
/* 382*/      DateFormatSymbols symbols = DateFormatSymbols.getInstance(this.locale);
/* 383*/      switch (field) {
/*   0*/        case 0:
/* 388*/          c = Calendar.getInstance(this.locale);
/* 390*/          shortEras = toArray(c.getDisplayNames(0, 1, this.locale));
/* 391*/          longEras = toArray(c.getDisplayNames(0, 2, this.locale));
/* 392*/          fieldKeyValues = createKeyValues(longEras, shortEras);
/*   0*/          break;
/*   0*/        case 7:
/* 395*/          fieldKeyValues = createKeyValues(symbols.getWeekdays(), symbols.getShortWeekdays());
/*   0*/          break;
/*   0*/        case 9:
/* 398*/          fieldKeyValues = createKeyValues(symbols.getAmPmStrings(), null);
/*   0*/          break;
/*   0*/        case 2:
/* 401*/          fieldKeyValues = createKeyValues(symbols.getMonths(), symbols.getShortMonths());
/*   0*/          break;
/*   0*/        default:
/* 404*/          throw new IllegalArgumentException("Invalid field value " + field);
/*   0*/      } 
/* 406*/      KeyValue[] prior = this.nameValues.putIfAbsent(fieldInt, fieldKeyValues);
/* 407*/      if (prior != null)
/* 408*/        fieldKeyValues = prior; 
/*   0*/    } 
/* 411*/    return fieldKeyValues;
/*   0*/  }
/*   0*/  
/*   0*/  private String[] toArray(Map<String, Integer> era) {
/* 415*/    String[] eras = new String[era.size()];
/* 416*/    for (Map.Entry<String, Integer> me : era.entrySet()) {
/* 417*/      int idx = (Integer)me.getValue();
/* 418*/      String key = me.getKey();
/* 419*/      if (key == null)
/* 420*/        throw new IllegalArgumentException(); 
/* 422*/      eras[idx] = key;
/*   0*/    } 
/* 424*/    return eras;
/*   0*/  }
/*   0*/  
/*   0*/  private static KeyValue[] createKeyValues(String[] longValues, String[] shortValues) {
/* 434*/    KeyValue[] fieldKeyValues = new KeyValue[count(longValues) + count(shortValues)];
/* 435*/    copy(fieldKeyValues, copy(fieldKeyValues, 0, longValues), shortValues);
/* 436*/    Arrays.sort(fieldKeyValues, IGNORE_CASE_COMPARATOR);
/* 437*/    return fieldKeyValues;
/*   0*/  }
/*   0*/  
/*   0*/  private static int count(String[] values) {
/* 446*/    int count = 0;
/* 447*/    if (values != null)
/* 448*/      for (String value : values) {
/* 449*/        if (value.length() > 0)
/* 450*/          count++; 
/*   0*/      }  
/* 454*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  private static int copy(KeyValue[] fieldKeyValues, int offset, String[] values) {
/* 465*/    if (values != null)
/* 466*/      for (int i = 0; i < values.length; i++) {
/* 467*/        String value = values[i];
/* 468*/        if (value.length() > 0)
/* 469*/          fieldKeyValues[offset++] = new KeyValue(value, i); 
/*   0*/      }  
/* 473*/    return offset;
/*   0*/  }
/*   0*/  
/*   0*/  int adjustYear(int twoDigitYear) {
/* 482*/    int trial = twoDigitYear + this.thisYear - this.thisYear % 100;
/* 483*/    if (trial < this.thisYear + 20)
/* 484*/      return trial; 
/* 486*/    return trial - 100;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isNextNumber() {
/* 494*/    return (this.nextStrategy != null && this.nextStrategy.isNumber());
/*   0*/  }
/*   0*/  
/*   0*/  int getFieldWidth() {
/* 502*/    return this.currentFormatField.length();
/*   0*/  }
/*   0*/  
/* 535*/  private static final Pattern formatPattern = Pattern.compile("D+|E+|F+|G+|H+|K+|M+|S+|W+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|''|'[^']++(''[^']*+)*+'|[^'A-Za-z]++");
/*   0*/  
/*   0*/  private Strategy getStrategy(String formatField) {
/* 544*/    switch (formatField.charAt(0)) {
/*   0*/      case '\'':
/* 546*/        if (formatField.length() > 2)
/* 547*/          formatField = formatField.substring(1, formatField.length() - 1); 
/*   0*/      default:
/* 551*/        return new CopyQuotedStrategy(formatField);
/*   0*/      case 'D':
/* 553*/        return DAY_OF_YEAR_STRATEGY;
/*   0*/      case 'E':
/* 555*/        return DAY_OF_WEEK_STRATEGY;
/*   0*/      case 'F':
/* 557*/        return DAY_OF_WEEK_IN_MONTH_STRATEGY;
/*   0*/      case 'G':
/* 559*/        return ERA_STRATEGY;
/*   0*/      case 'H':
/* 561*/        return MODULO_HOUR_OF_DAY_STRATEGY;
/*   0*/      case 'K':
/* 563*/        return HOUR_STRATEGY;
/*   0*/      case 'M':
/* 565*/        return (formatField.length() >= 3) ? TEXT_MONTH_STRATEGY : NUMBER_MONTH_STRATEGY;
/*   0*/      case 'S':
/* 567*/        return MILLISECOND_STRATEGY;
/*   0*/      case 'W':
/* 569*/        return WEEK_OF_MONTH_STRATEGY;
/*   0*/      case 'Z':
/*   0*/        break;
/*   0*/      case 'a':
/* 573*/        return AM_PM_STRATEGY;
/*   0*/      case 'd':
/* 575*/        return DAY_OF_MONTH_STRATEGY;
/*   0*/      case 'h':
/* 577*/        return MODULO_HOUR_STRATEGY;
/*   0*/      case 'k':
/* 579*/        return HOUR_OF_DAY_STRATEGY;
/*   0*/      case 'm':
/* 581*/        return MINUTE_STRATEGY;
/*   0*/      case 's':
/* 583*/        return SECOND_STRATEGY;
/*   0*/      case 'w':
/* 585*/        return WEEK_OF_YEAR_STRATEGY;
/*   0*/      case 'y':
/* 587*/        return (formatField.length() > 2) ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
/*   0*/      case 'z':
/*   0*/        break;
/*   0*/    } 
/* 591*/    TimeZoneStrategy tzs = tzsCache.get(this.locale);
/* 592*/    if (tzs == null) {
/* 593*/      tzs = new TimeZoneStrategy(this.locale);
/* 594*/      TimeZoneStrategy inCache = tzsCache.putIfAbsent(this.locale, tzs);
/* 595*/      if (inCache != null)
/* 596*/        return inCache; 
/*   0*/    } 
/* 599*/    return tzs;
/*   0*/  }
/*   0*/  
/*   0*/  private static class CopyQuotedStrategy implements Strategy {
/*   0*/    private final String formatField;
/*   0*/    
/*   0*/    CopyQuotedStrategy(String formatField) {
/* 613*/      this.formatField = formatField;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isNumber() {
/* 621*/      char c = this.formatField.charAt(0);
/* 622*/      if (c == '\'')
/* 623*/        c = this.formatField.charAt(1); 
/* 625*/      return Character.isDigit(c);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean addRegex(FastDateParser parser, StringBuilder regex) {
/* 633*/      FastDateParser.escapeRegex(regex, this.formatField, true);
/* 634*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public void setCalendar(FastDateParser parser, Calendar cal, String value) {}
/*   0*/  }
/*   0*/  
/*   0*/  private static class TextStrategy implements Strategy {
/*   0*/    private final int field;
/*   0*/    
/*   0*/    TextStrategy(int field) {
/* 656*/      this.field = field;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isNumber() {
/* 664*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean addRegex(FastDateParser parser, StringBuilder regex) {
/* 672*/      regex.append('(');
/* 673*/      for (FastDateParser.KeyValue textKeyValue : parser.getDisplayNames(this.field))
/* 674*/        FastDateParser.escapeRegex(regex, textKeyValue.key, false).append('|'); 
/* 676*/      regex.setCharAt(regex.length() - 1, ')');
/* 677*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void setCalendar(FastDateParser parser, Calendar cal, String value) {
/* 685*/      FastDateParser.KeyValue[] textKeyValues = parser.getDisplayNames(this.field);
/* 686*/      int idx = Arrays.binarySearch(textKeyValues, new FastDateParser.KeyValue(value, -1), FastDateParser.IGNORE_CASE_COMPARATOR);
/* 687*/      if (idx < 0) {
/* 688*/        StringBuilder sb = new StringBuilder(value);
/* 689*/        sb.append(" not in (");
/* 690*/        for (FastDateParser.KeyValue textKeyValue : textKeyValues)
/* 691*/          sb.append(textKeyValue.key).append(' '); 
/* 693*/        sb.setCharAt(sb.length() - 1, ')');
/* 694*/        throw new IllegalArgumentException(sb.toString());
/*   0*/      } 
/* 696*/      cal.set(this.field, (textKeyValues[idx]).value);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class NumberStrategy implements Strategy {
/*   0*/    protected final int field;
/*   0*/    
/*   0*/    NumberStrategy(int field) {
/* 711*/      this.field = field;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isNumber() {
/* 719*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean addRegex(FastDateParser parser, StringBuilder regex) {
/* 727*/      if (parser.isNextNumber()) {
/* 728*/        regex.append("(\\p{IsNd}{").append(parser.getFieldWidth()).append("}+)");
/*   0*/      } else {
/* 731*/        regex.append("(\\p{IsNd}++)");
/*   0*/      } 
/* 733*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void setCalendar(FastDateParser parser, Calendar cal, String value) {
/* 741*/      cal.set(this.field, modify(Integer.parseInt(value)));
/*   0*/    }
/*   0*/    
/*   0*/    public int modify(int iValue) {
/* 750*/      return iValue;
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 754*/  private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) {
/*   0*/      public void setCalendar(FastDateParser parser, Calendar cal, String value) {
/* 760*/        int iValue = Integer.parseInt(value);
/* 761*/        if (iValue < 100)
/* 762*/          iValue = parser.adjustYear(iValue); 
/* 764*/        cal.set(1, iValue);
/*   0*/      }
/*   0*/    };
/*   0*/  
/*   0*/  private static class TimeZoneStrategy implements Strategy {
/*   0*/    final String validTimeZoneChars;
/*   0*/    
/* 774*/    final SortedMap<String, TimeZone> tzNames = new TreeMap<String, TimeZone>(String.CASE_INSENSITIVE_ORDER);
/*   0*/    
/*   0*/    TimeZoneStrategy(Locale locale) {
/* 781*/      for (String id : TimeZone.getAvailableIDs()) {
/* 782*/        if (!id.startsWith("GMT")) {
/* 785*/          TimeZone tz = TimeZone.getTimeZone(id);
/* 786*/          this.tzNames.put(tz.getDisplayName(false, 0, locale), tz);
/* 787*/          this.tzNames.put(tz.getDisplayName(false, 1, locale), tz);
/* 788*/          if (tz.useDaylightTime()) {
/* 789*/            this.tzNames.put(tz.getDisplayName(true, 0, locale), tz);
/* 790*/            this.tzNames.put(tz.getDisplayName(true, 1, locale), tz);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 793*/      StringBuilder sb = new StringBuilder();
/* 794*/      sb.append("(GMT[+\\-]\\d{0,1}\\d{2}|[+\\-]\\d{2}:?\\d{2}|");
/* 795*/      for (String id : this.tzNames.keySet())
/* 796*/        FastDateParser.escapeRegex(sb, id, false).append('|'); 
/* 798*/      sb.setCharAt(sb.length() - 1, ')');
/* 799*/      this.validTimeZoneChars = sb.toString();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isNumber() {
/* 807*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean addRegex(FastDateParser parser, StringBuilder regex) {
/* 815*/      regex.append(this.validTimeZoneChars);
/* 816*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public void setCalendar(FastDateParser parser, Calendar cal, String value) {
/*   0*/      TimeZone tz;
/* 825*/      if (value.charAt(0) == '+' || value.charAt(0) == '-') {
/* 826*/        tz = TimeZone.getTimeZone("GMT" + value);
/* 828*/      } else if (value.startsWith("GMT")) {
/* 829*/        tz = TimeZone.getTimeZone(value);
/*   0*/      } else {
/* 832*/        tz = this.tzNames.get(value);
/* 833*/        if (tz == null)
/* 834*/          throw new IllegalArgumentException(value + " is not a supported timezone name"); 
/*   0*/      } 
/* 837*/      cal.setTimeZone(tz);
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 842*/  private static final Strategy ERA_STRATEGY = new TextStrategy(0);
/*   0*/  
/* 843*/  private static final Strategy DAY_OF_WEEK_STRATEGY = new TextStrategy(7);
/*   0*/  
/* 844*/  private static final Strategy AM_PM_STRATEGY = new TextStrategy(9);
/*   0*/  
/* 845*/  private static final Strategy TEXT_MONTH_STRATEGY = new TextStrategy(2);
/*   0*/  
/* 847*/  private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) {
/*   0*/      public int modify(int iValue) {
/* 850*/        return iValue - 1;
/*   0*/      }
/*   0*/    };
/*   0*/  
/* 853*/  private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
/*   0*/  
/* 854*/  private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
/*   0*/  
/* 855*/  private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
/*   0*/  
/* 856*/  private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
/*   0*/  
/* 857*/  private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
/*   0*/  
/* 858*/  private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
/*   0*/  
/* 859*/  private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
/*   0*/  
/* 860*/  private static final Strategy MODULO_HOUR_OF_DAY_STRATEGY = new NumberStrategy(11) {
/*   0*/      public int modify(int iValue) {
/* 863*/        return iValue % 24;
/*   0*/      }
/*   0*/    };
/*   0*/  
/* 866*/  private static final Strategy MODULO_HOUR_STRATEGY = new NumberStrategy(10) {
/*   0*/      public int modify(int iValue) {
/* 869*/        return iValue % 12;
/*   0*/      }
/*   0*/    };
/*   0*/  
/* 872*/  private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
/*   0*/  
/* 873*/  private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
/*   0*/  
/* 874*/  private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
/*   0*/  
/* 875*/  private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);
/*   0*/  
/*   0*/  private static interface Strategy {
/*   0*/    boolean isNumber();
/*   0*/    
/*   0*/    void setCalendar(FastDateParser param1FastDateParser, Calendar param1Calendar, String param1String);
/*   0*/    
/*   0*/    boolean addRegex(FastDateParser param1FastDateParser, StringBuilder param1StringBuilder);
/*   0*/  }
/*   0*/}
