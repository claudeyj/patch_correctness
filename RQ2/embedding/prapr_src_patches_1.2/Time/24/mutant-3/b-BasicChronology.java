/*   0*/package org.joda.time.chrono;
/*   0*/
/*   0*/import java.util.Locale;
/*   0*/import org.joda.time.Chronology;
/*   0*/import org.joda.time.DateTimeField;
/*   0*/import org.joda.time.DateTimeFieldType;
/*   0*/import org.joda.time.DateTimeZone;
/*   0*/import org.joda.time.DurationField;
/*   0*/import org.joda.time.DurationFieldType;
/*   0*/import org.joda.time.field.DividedDateTimeField;
/*   0*/import org.joda.time.field.FieldUtils;
/*   0*/import org.joda.time.field.MillisDurationField;
/*   0*/import org.joda.time.field.OffsetDateTimeField;
/*   0*/import org.joda.time.field.PreciseDateTimeField;
/*   0*/import org.joda.time.field.PreciseDurationField;
/*   0*/import org.joda.time.field.RemainderDateTimeField;
/*   0*/import org.joda.time.field.ZeroIsMaxDateTimeField;
/*   0*/
/*   0*/abstract class BasicChronology extends AssembledChronology {
/*   0*/  private static final long serialVersionUID = 8283225332206808863L;
/*   0*/  
/*  76*/  private static final DurationField cMillisField = MillisDurationField.INSTANCE;
/*   0*/  
/*  77*/  private static final DurationField cSecondsField = new PreciseDurationField(DurationFieldType.seconds(), 1000L);
/*   0*/  
/*  79*/  private static final DurationField cMinutesField = new PreciseDurationField(DurationFieldType.minutes(), 60000L);
/*   0*/  
/*  81*/  private static final DurationField cHoursField = new PreciseDurationField(DurationFieldType.hours(), 3600000L);
/*   0*/  
/*  83*/  private static final DurationField cHalfdaysField = new PreciseDurationField(DurationFieldType.halfdays(), 43200000L);
/*   0*/  
/*  85*/  private static final DurationField cDaysField = new PreciseDurationField(DurationFieldType.days(), 86400000L);
/*   0*/  
/*  87*/  private static final DurationField cWeeksField = new PreciseDurationField(DurationFieldType.weeks(), 604800000L);
/*   0*/  
/*  90*/  private static final DateTimeField cMillisOfSecondField = new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), cMillisField, cSecondsField);
/*   0*/  
/*  93*/  private static final DateTimeField cMillisOfDayField = new PreciseDateTimeField(DateTimeFieldType.millisOfDay(), cMillisField, cDaysField);
/*   0*/  
/*  96*/  private static final DateTimeField cSecondOfMinuteField = new PreciseDateTimeField(DateTimeFieldType.secondOfMinute(), cSecondsField, cMinutesField);
/*   0*/  
/*  99*/  private static final DateTimeField cSecondOfDayField = new PreciseDateTimeField(DateTimeFieldType.secondOfDay(), cSecondsField, cDaysField);
/*   0*/  
/* 102*/  private static final DateTimeField cMinuteOfHourField = new PreciseDateTimeField(DateTimeFieldType.minuteOfHour(), cMinutesField, cHoursField);
/*   0*/  
/* 105*/  private static final DateTimeField cMinuteOfDayField = new PreciseDateTimeField(DateTimeFieldType.minuteOfDay(), cMinutesField, cDaysField);
/*   0*/  
/* 108*/  private static final DateTimeField cHourOfDayField = new PreciseDateTimeField(DateTimeFieldType.hourOfDay(), cHoursField, cDaysField);
/*   0*/  
/* 111*/  private static final DateTimeField cHourOfHalfdayField = new PreciseDateTimeField(DateTimeFieldType.hourOfHalfday(), cHoursField, cHalfdaysField);
/*   0*/  
/* 114*/  private static final DateTimeField cClockhourOfDayField = new ZeroIsMaxDateTimeField(cHourOfDayField, DateTimeFieldType.clockhourOfDay());
/*   0*/  
/* 117*/  private static final DateTimeField cClockhourOfHalfdayField = new ZeroIsMaxDateTimeField(cHourOfHalfdayField, DateTimeFieldType.clockhourOfHalfday());
/*   0*/  
/* 120*/  private static final DateTimeField cHalfdayOfDayField = new HalfdayField();
/*   0*/  
/*   0*/  private static final int CACHE_SIZE = 1024;
/*   0*/  
/*   0*/  private static final int CACHE_MASK = 1023;
/*   0*/  
/* 126*/  private final transient YearInfo[] iYearInfoCache = new YearInfo[1024];
/*   0*/  
/*   0*/  private final int iMinDaysInFirstWeek;
/*   0*/  
/*   0*/  BasicChronology(Chronology paramChronology, Object paramObject, int paramInt) {
/* 131*/    super(paramChronology, paramObject);
/* 133*/    if (paramInt < 1 || paramInt > 7)
/* 134*/      throw new IllegalArgumentException("Invalid min days in first week: " + paramInt); 
/* 138*/    this.iMinDaysInFirstWeek = paramInt;
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZone getZone() {
/*   0*/    Chronology chronology;
/* 143*/    if ((chronology = getBase()) != null)
/* 144*/      return chronology.getZone(); 
/* 146*/    return DateTimeZone.UTC;
/*   0*/  }
/*   0*/  
/*   0*/  public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IllegalArgumentException {
/*   0*/    Chronology chronology;
/* 153*/    if ((chronology = getBase()) != null)
/* 154*/      return chronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4); 
/* 157*/    FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfDay(), paramInt4, 0, 86400000);
/* 159*/    return getDateMidnightMillis(paramInt1, paramInt2, paramInt3) + paramInt4;
/*   0*/  }
/*   0*/  
/*   0*/  public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) throws IllegalArgumentException {
/*   0*/    Chronology chronology;
/* 167*/    if ((chronology = getBase()) != null)
/* 168*/      return chronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7); 
/* 172*/    FieldUtils.verifyValueBounds(DateTimeFieldType.hourOfDay(), paramInt4, 0, 23);
/* 173*/    FieldUtils.verifyValueBounds(DateTimeFieldType.minuteOfHour(), paramInt5, 0, 59);
/* 174*/    FieldUtils.verifyValueBounds(DateTimeFieldType.secondOfMinute(), paramInt6, 0, 59);
/* 175*/    FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfSecond(), paramInt7, 0, 999);
/* 177*/    return getDateMidnightMillis(paramInt1, paramInt2, paramInt3) + (paramInt4 * 3600000) + (paramInt5 * 60000) + (paramInt6 * 1000) + paramInt7;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinimumDaysInFirstWeek() {
/* 185*/    return this.iMinDaysInFirstWeek;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object paramObject) {
/* 196*/    return super.equals(paramObject);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 206*/    return getClass().getName().hashCode() * 11 + getZone().hashCode() + getMinimumDaysInFirstWeek();
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 217*/    StringBuffer stringBuffer = new StringBuffer(60);
/* 218*/    String str = getClass().getName();
/* 219*/    int i = str.lastIndexOf('.');
/* 220*/    if (i >= 0)
/* 221*/      str = str.substring(i + 1); 
/* 223*/    stringBuffer.append(str);
/* 224*/    stringBuffer.append('[');
/* 225*/    DateTimeZone dateTimeZone = getZone();
/* 226*/    if (dateTimeZone != null)
/* 227*/      stringBuffer.append(dateTimeZone.getID()); 
/* 229*/    if (getMinimumDaysInFirstWeek() != 4) {
/* 230*/      stringBuffer.append(",mdfw=");
/* 231*/      stringBuffer.append(getMinimumDaysInFirstWeek());
/*   0*/    } 
/* 233*/    stringBuffer.append(']');
/* 234*/    return stringBuffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected void assemble(AssembledChronology.Fields paramFields) {
/* 241*/    paramFields.millis = cMillisField;
/* 242*/    paramFields.seconds = cSecondsField;
/* 243*/    paramFields.minutes = cMinutesField;
/* 244*/    paramFields.hours = cHoursField;
/* 245*/    paramFields.halfdays = cHalfdaysField;
/* 246*/    paramFields.days = cDaysField;
/* 247*/    paramFields.weeks = cWeeksField;
/* 249*/    paramFields.millisOfSecond = cMillisOfSecondField;
/* 250*/    paramFields.millisOfDay = cMillisOfDayField;
/* 251*/    paramFields.secondOfMinute = cSecondOfMinuteField;
/* 252*/    paramFields.secondOfDay = cSecondOfDayField;
/* 253*/    paramFields.minuteOfHour = cMinuteOfHourField;
/* 254*/    paramFields.minuteOfDay = cMinuteOfDayField;
/* 255*/    paramFields.hourOfDay = cHourOfDayField;
/* 256*/    paramFields.hourOfHalfday = cHourOfHalfdayField;
/* 257*/    paramFields.clockhourOfDay = cClockhourOfDayField;
/* 258*/    paramFields.clockhourOfHalfday = cClockhourOfHalfdayField;
/* 259*/    paramFields.halfdayOfDay = cHalfdayOfDayField;
/* 264*/    paramFields.year = new BasicYearDateTimeField(this);
/* 265*/    paramFields.yearOfEra = new GJYearOfEraDateTimeField(paramFields.year, this);
/* 268*/    OffsetDateTimeField offsetDateTimeField = new OffsetDateTimeField(paramFields.yearOfEra, 99);
/* 270*/    paramFields.centuryOfEra = new DividedDateTimeField(offsetDateTimeField, DateTimeFieldType.centuryOfEra(), 100);
/* 273*/    RemainderDateTimeField remainderDateTimeField = new RemainderDateTimeField((DividedDateTimeField)paramFields.centuryOfEra);
/* 275*/    paramFields.yearOfCentury = new OffsetDateTimeField(remainderDateTimeField, DateTimeFieldType.yearOfCentury(), 1);
/* 278*/    paramFields.era = new GJEraDateTimeField(this);
/* 279*/    paramFields.dayOfWeek = new GJDayOfWeekDateTimeField(this, paramFields.days);
/* 280*/    paramFields.dayOfMonth = new BasicDayOfMonthDateTimeField(this, paramFields.days);
/* 281*/    paramFields.dayOfYear = new BasicDayOfYearDateTimeField(this, paramFields.days);
/* 282*/    paramFields.monthOfYear = new GJMonthOfYearDateTimeField(this);
/* 283*/    paramFields.weekyear = new BasicWeekyearDateTimeField(this);
/* 284*/    paramFields.weekOfWeekyear = new BasicWeekOfWeekyearDateTimeField(this, paramFields.weeks);
/* 286*/    remainderDateTimeField = new RemainderDateTimeField(paramFields.weekyear, DateTimeFieldType.weekyearOfCentury(), 100);
/* 288*/    paramFields.weekyearOfCentury = new OffsetDateTimeField(remainderDateTimeField, DateTimeFieldType.weekyearOfCentury(), 1);
/* 294*/    paramFields.years = paramFields.year.getDurationField();
/* 295*/    paramFields.centuries = paramFields.centuryOfEra.getDurationField();
/* 296*/    paramFields.months = paramFields.monthOfYear.getDurationField();
/* 297*/    paramFields.weekyears = paramFields.weekyear.getDurationField();
/*   0*/  }
/*   0*/  
/*   0*/  int getDaysInYearMax() {
/* 307*/    return 366;
/*   0*/  }
/*   0*/  
/*   0*/  int getDaysInYear(int paramInt) {
/* 317*/    return isLeapYear(paramInt) ? 366 : 365;
/*   0*/  }
/*   0*/  
/*   0*/  int getWeeksInYear(int paramInt) {
/* 327*/    long l1 = getFirstWeekOfYearMillis(paramInt);
/* 328*/    long l2 = getFirstWeekOfYearMillis(paramInt + 1);
/* 329*/    return (int)((l2 - l1) / 604800000L);
/*   0*/  }
/*   0*/  
/*   0*/  long getFirstWeekOfYearMillis(int paramInt) {
/* 339*/    long l = getYearMillis(paramInt);
/* 340*/    int i = getDayOfWeek(l);
/* 342*/    if (i > 8 - this.iMinDaysInFirstWeek)
/* 344*/      return l + (8 - i) * 86400000L; 
/* 348*/    return l - (i - 1) * 86400000L;
/*   0*/  }
/*   0*/  
/*   0*/  long getYearMillis(int paramInt) {
/* 360*/    return (getYearInfo(paramInt)).iFirstDayMillis;
/*   0*/  }
/*   0*/  
/*   0*/  long getYearMonthMillis(int paramInt1, int paramInt2) {
/* 371*/    long l = getYearMillis(paramInt1);
/* 372*/    l += getTotalMillisByYearMonth(paramInt1, paramInt2);
/* 373*/    return l;
/*   0*/  }
/*   0*/  
/*   0*/  long getYearMonthDayMillis(int paramInt1, int paramInt2, int paramInt3) {
/* 385*/    long l = getYearMillis(paramInt1);
/* 386*/    l += getTotalMillisByYearMonth(paramInt1, paramInt2);
/* 387*/    return l + (paramInt3 - 1) * 86400000L;
/*   0*/  }
/*   0*/  
/*   0*/  int getYear(long paramLong) {
/* 399*/    long l1 = getAverageMillisPerYearDividedByTwo();
/* 400*/    long l2 = (paramLong >> 1L) + getApproxMillisAtEpochDividedByTwo();
/* 401*/    if (l2 < 0L)
/* 402*/      l2 = l2 - l1 + 1L; 
/* 404*/    int i = (int)(l2 / l1);
/* 406*/    long l3 = getYearMillis(i);
/* 407*/    long l4 = paramLong - l3;
/* 409*/    if (l4 < 0L) {
/* 410*/      i--;
/* 411*/    } else if (l4 >= 31536000000L) {
/*   0*/      long l;
/* 414*/      if (isLeapYear(i)) {
/* 415*/        l = 31622400000L;
/*   0*/      } else {
/* 417*/        l = 31536000000L;
/*   0*/      } 
/* 420*/      l3 += l;
/* 422*/      if (l3 <= paramLong)
/* 424*/        i++; 
/*   0*/    } 
/* 428*/    return i;
/*   0*/  }
/*   0*/  
/*   0*/  int getMonthOfYear(long paramLong) {
/* 435*/    return getMonthOfYear(paramLong, getYear(paramLong));
/*   0*/  }
/*   0*/  
/*   0*/  int getDayOfMonth(long paramLong) {
/* 448*/    int i = getYear(paramLong);
/* 449*/    int j = getMonthOfYear(paramLong, i);
/* 450*/    return getDayOfMonth(paramLong, i, j);
/*   0*/  }
/*   0*/  
/*   0*/  int getDayOfMonth(long paramLong, int paramInt) {
/* 458*/    int i = getMonthOfYear(paramLong, paramInt);
/* 459*/    return getDayOfMonth(paramLong, paramInt, i);
/*   0*/  }
/*   0*/  
/*   0*/  int getDayOfMonth(long paramLong, int paramInt1, int paramInt2) {
/* 468*/    long l = getYearMillis(paramInt1);
/* 469*/    l += getTotalMillisByYearMonth(paramInt1, paramInt2);
/* 470*/    return (int)((paramLong - l) / 86400000L) + 1;
/*   0*/  }
/*   0*/  
/*   0*/  int getDayOfYear(long paramLong) {
/* 477*/    return getDayOfYear(paramLong, getYear(paramLong));
/*   0*/  }
/*   0*/  
/*   0*/  int getDayOfYear(long paramLong, int paramInt) {
/* 485*/    long l = getYearMillis(paramInt);
/* 486*/    return (int)((paramLong - l) / 86400000L) + 1;
/*   0*/  }
/*   0*/  
/*   0*/  int getWeekyear(long paramLong) {
/* 493*/    int i = getYear(paramLong);
/* 494*/    int j = getWeekOfWeekyear(paramLong, i);
/* 495*/    if (j == 1)
/* 496*/      return getYear(paramLong + 604800000L); 
/* 497*/    if (j > 51)
/* 498*/      return getYear(paramLong - 1209600000L); 
/* 500*/    return i;
/*   0*/  }
/*   0*/  
/*   0*/  int getWeekOfWeekyear(long paramLong) {
/* 508*/    return getWeekOfWeekyear(paramLong, getYear(paramLong));
/*   0*/  }
/*   0*/  
/*   0*/  int getWeekOfWeekyear(long paramLong, int paramInt) {
/* 516*/    long l1 = getFirstWeekOfYearMillis(paramInt);
/* 517*/    if (paramLong < l1) {
/* 518*/      paramInt - 1;
/* 518*/      this;
/* 518*/      return 0;
/*   0*/    } 
/* 520*/    long l2 = getFirstWeekOfYearMillis(paramInt + 1);
/* 521*/    if (paramLong >= l2)
/* 522*/      return 1; 
/* 524*/    return (int)((paramLong - l1) / 604800000L) + 1;
/*   0*/  }
/*   0*/  
/*   0*/  int getDayOfWeek(long paramLong) {
/*   0*/    long l;
/* 534*/    if (paramLong >= 0L) {
/* 535*/      l = paramLong / 86400000L;
/*   0*/    } else {
/* 537*/      l = (paramLong - 86399999L) / 86400000L;
/* 539*/      if (l < -3L)
/* 540*/        return 7 + (int)((l + 4L) % 7L); 
/*   0*/    } 
/* 544*/    return 1 + (int)((l + 3L) % 7L);
/*   0*/  }
/*   0*/  
/*   0*/  int getMillisOfDay(long paramLong) {
/* 551*/    if (paramLong >= 0L)
/* 552*/      return (int)(paramLong % 86400000L); 
/* 554*/    return 86399999 + (int)((paramLong + 1L) % 86400000L);
/*   0*/  }
/*   0*/  
/*   0*/  int getDaysInMonthMax() {
/* 565*/    return 31;
/*   0*/  }
/*   0*/  
/*   0*/  int getDaysInMonthMax(long paramLong) {
/* 575*/    int i = getYear(paramLong);
/* 576*/    int j = getMonthOfYear(paramLong, i);
/* 577*/    return getDaysInYearMonth(i, j);
/*   0*/  }
/*   0*/  
/*   0*/  int getDaysInMonthMaxForSet(long paramLong, int paramInt) {
/* 590*/    return getDaysInMonthMax(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  long getDateMidnightMillis(int paramInt1, int paramInt2, int paramInt3) {
/* 603*/    FieldUtils.verifyValueBounds(DateTimeFieldType.year(), paramInt1, getMinYear(), getMaxYear());
/* 604*/    FieldUtils.verifyValueBounds(DateTimeFieldType.monthOfYear(), paramInt2, 1, getMaxMonth(paramInt1));
/* 605*/    FieldUtils.verifyValueBounds(DateTimeFieldType.dayOfMonth(), paramInt3, 1, getDaysInYearMonth(paramInt1, paramInt2));
/* 606*/    return getYearMonthDayMillis(paramInt1, paramInt2, paramInt3);
/*   0*/  }
/*   0*/  
/*   0*/  int getMaxMonth(int paramInt) {
/* 682*/    return getMaxMonth();
/*   0*/  }
/*   0*/  
/*   0*/  int getMaxMonth() {
/* 691*/    return 12;
/*   0*/  }
/*   0*/  
/*   0*/  private YearInfo getYearInfo(int paramInt) {
/* 738*/    YearInfo yearInfo = this.iYearInfoCache[paramInt & 0x3FF];
/* 739*/    if (yearInfo == null || yearInfo.iYear != paramInt) {
/* 740*/      yearInfo = new YearInfo(paramInt, calculateFirstDayOfYearMillis(paramInt));
/* 741*/      this.iYearInfoCache[paramInt & 0x3FF] = yearInfo;
/*   0*/    } 
/* 743*/    return yearInfo;
/*   0*/  }
/*   0*/  
/*   0*/  abstract int getMonthOfYear(long paramLong, int paramInt);
/*   0*/  
/*   0*/  abstract long getYearDifference(long paramLong1, long paramLong2);
/*   0*/  
/*   0*/  abstract boolean isLeapYear(int paramInt);
/*   0*/  
/*   0*/  abstract int getDaysInYearMonth(int paramInt1, int paramInt2);
/*   0*/  
/*   0*/  abstract int getDaysInMonthMax(int paramInt);
/*   0*/  
/*   0*/  abstract long getTotalMillisByYearMonth(int paramInt1, int paramInt2);
/*   0*/  
/*   0*/  abstract long calculateFirstDayOfYearMillis(int paramInt);
/*   0*/  
/*   0*/  abstract int getMinYear();
/*   0*/  
/*   0*/  abstract int getMaxYear();
/*   0*/  
/*   0*/  abstract long getAverageMillisPerYear();
/*   0*/  
/*   0*/  abstract long getAverageMillisPerYearDividedByTwo();
/*   0*/  
/*   0*/  abstract long getAverageMillisPerMonth();
/*   0*/  
/*   0*/  abstract long getApproxMillisAtEpochDividedByTwo();
/*   0*/  
/*   0*/  abstract long setYear(long paramLong, int paramInt);
/*   0*/  
/*   0*/  private static class HalfdayField extends PreciseDateTimeField {
/*   0*/    private static final long serialVersionUID = 581601443656929254L;
/*   0*/    
/*   0*/    HalfdayField() {
/* 750*/      super(DateTimeFieldType.halfdayOfDay(), BasicChronology.cHalfdaysField, BasicChronology.cDaysField);
/*   0*/    }
/*   0*/    
/*   0*/    public String getAsText(int param1Int, Locale param1Locale) {
/* 754*/      return GJLocaleSymbols.forLocale(param1Locale).halfdayValueToText(param1Int);
/*   0*/    }
/*   0*/    
/*   0*/    public long set(long param1Long, String param1String, Locale param1Locale) {
/* 758*/      return set(param1Long, GJLocaleSymbols.forLocale(param1Locale).halfdayTextToValue(param1String));
/*   0*/    }
/*   0*/    
/*   0*/    public int getMaximumTextLength(Locale param1Locale) {
/* 762*/      return GJLocaleSymbols.forLocale(param1Locale).getHalfdayMaxTextLength();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class YearInfo {
/*   0*/    public final int iYear;
/*   0*/    
/*   0*/    public final long iFirstDayMillis;
/*   0*/    
/*   0*/    YearInfo(int param1Int, long param1Long) {
/* 771*/      this.iYear = param1Int;
/* 772*/      this.iFirstDayMillis = param1Long;
/*   0*/    }
/*   0*/  }
/*   0*/}
