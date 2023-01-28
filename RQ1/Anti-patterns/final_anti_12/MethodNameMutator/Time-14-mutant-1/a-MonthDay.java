/*   0*/package org.joda.time;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import org.joda.convert.FromString;
/*   0*/import org.joda.convert.ToString;
/*   0*/import org.joda.time.base.BasePartial;
/*   0*/import org.joda.time.chrono.ISOChronology;
/*   0*/import org.joda.time.field.AbstractPartialFieldProperty;
/*   0*/import org.joda.time.field.FieldUtils;
/*   0*/import org.joda.time.format.DateTimeFormat;
/*   0*/import org.joda.time.format.DateTimeFormatter;
/*   0*/import org.joda.time.format.DateTimeFormatterBuilder;
/*   0*/import org.joda.time.format.ISODateTimeFormat;
/*   0*/
/*   0*/public final class MonthDay extends BasePartial implements ReadablePartial, Serializable {
/*   0*/  private static final long serialVersionUID = 2954560699050434609L;
/*   0*/  
/*  79*/  private static final DateTimeFieldType[] FIELD_TYPES = new DateTimeFieldType[] { DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth() };
/*   0*/  
/*  84*/  private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder().appendOptional(ISODateTimeFormat.localDateParser().getParser()).appendOptional(DateTimeFormat.forPattern("--MM-dd").getParser()).toFormatter();
/*   0*/  
/*   0*/  public static final int MONTH_OF_YEAR = 0;
/*   0*/  
/*   0*/  public static final int DAY_OF_MONTH = 1;
/*   0*/  
/*   0*/  public static MonthDay now() {
/* 103*/    return new MonthDay();
/*   0*/  }
/*   0*/  
/*   0*/  public static MonthDay now(DateTimeZone paramDateTimeZone) {
/* 116*/    if (paramDateTimeZone == null)
/* 117*/      throw new NullPointerException("Zone must not be null"); 
/* 119*/    return new MonthDay(paramDateTimeZone);
/*   0*/  }
/*   0*/  
/*   0*/  public static MonthDay now(Chronology paramChronology) {
/* 132*/    if (paramChronology == null)
/* 133*/      throw new NullPointerException("Chronology must not be null"); 
/* 135*/    return new MonthDay(paramChronology);
/*   0*/  }
/*   0*/  
/*   0*/  @FromString
/*   0*/  public static MonthDay parse(String paramString) {
/* 149*/    return parse(paramString, PARSER);
/*   0*/  }
/*   0*/  
/*   0*/  public static MonthDay parse(String paramString, DateTimeFormatter paramDateTimeFormatter) {
/* 160*/    LocalDate localDate = paramDateTimeFormatter.parseLocalDate(paramString);
/* 161*/    return new MonthDay(localDate.getMonthOfYear(), localDate.getDayOfMonth());
/*   0*/  }
/*   0*/  
/*   0*/  public static MonthDay fromCalendarFields(Calendar paramCalendar) {
/* 182*/    if (paramCalendar == null)
/* 183*/      throw new IllegalArgumentException("The calendar must not be null"); 
/* 185*/    return new MonthDay(paramCalendar.get(2) + 1, paramCalendar.get(5));
/*   0*/  }
/*   0*/  
/*   0*/  public static MonthDay fromDateFields(Date paramDate) {
/* 203*/    if (paramDate == null)
/* 204*/      throw new IllegalArgumentException("The date must not be null"); 
/* 206*/    return new MonthDay(paramDate.getMonth() + 1, paramDate.getDate());
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay() {}
/*   0*/  
/*   0*/  public MonthDay(DateTimeZone paramDateTimeZone) {
/* 236*/    super(ISOChronology.getInstance(paramDateTimeZone));
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(Chronology paramChronology) {
/* 251*/    super(paramChronology);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(long paramLong) {
/* 265*/    super(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(long paramLong, Chronology paramChronology) {
/* 280*/    super(paramLong, paramChronology);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(Object paramObject) {
/* 297*/    super(paramObject, null, ISODateTimeFormat.localDateParser());
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(Object paramObject, Chronology paramChronology) {
/* 319*/    super(paramObject, DateTimeUtils.getChronology(paramChronology), ISODateTimeFormat.localDateParser());
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(int paramInt1, int paramInt2) {
/* 334*/    this(paramInt1, paramInt2, null);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay(int paramInt1, int paramInt2, Chronology paramChronology) {
/* 352*/    super(new int[] { paramInt1, paramInt2 }, paramChronology);
/*   0*/  }
/*   0*/  
/*   0*/  MonthDay(MonthDay paramMonthDay, int[] paramArrayOfint) {
/* 362*/    super(paramMonthDay, paramArrayOfint);
/*   0*/  }
/*   0*/  
/*   0*/  MonthDay(MonthDay paramMonthDay, Chronology paramChronology) {
/* 372*/    super((BasePartial)paramMonthDay, paramChronology);
/*   0*/  }
/*   0*/  
/*   0*/  private Object readResolve() {
/* 380*/    if (!DateTimeZone.UTC.equals(getChronology().getZone()))
/* 381*/      return new MonthDay(this, getChronology().withUTC()); 
/* 383*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 395*/    return 2;
/*   0*/  }
/*   0*/  
/*   0*/  protected DateTimeField getField(int paramInt, Chronology paramChronology) {
/* 408*/    switch (paramInt) {
/*   0*/      case 0:
/* 410*/        return paramChronology.monthOfYear();
/*   0*/      case 1:
/* 412*/        return paramChronology.dayOfMonth();
/*   0*/    } 
/* 414*/    throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeFieldType getFieldType(int paramInt) {
/* 426*/    return FIELD_TYPES[paramInt];
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeFieldType[] getFieldTypes() {
/* 437*/    return (DateTimeFieldType[])FIELD_TYPES.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay withChronologyRetainFields(Chronology paramChronology) {
/* 456*/    paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 457*/    paramChronology = paramChronology.withUTC();
/* 458*/    if (paramChronology == getChronology())
/* 459*/      return this; 
/* 461*/    MonthDay monthDay = new MonthDay(this, paramChronology);
/* 462*/    paramChronology.validate(monthDay, getValues());
/* 463*/    return monthDay;
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay withField(DateTimeFieldType paramDateTimeFieldType, int paramInt) {
/* 486*/    int i = indexOfSupported(paramDateTimeFieldType);
/* 487*/    if (paramInt == getValue(i))
/* 488*/      return this; 
/* 490*/    int[] arrayOfInt = getValues();
/* 491*/    arrayOfInt = getField(i).set(this, i, arrayOfInt, paramInt);
/* 492*/    return new MonthDay(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt) {
/* 514*/    int i = indexOfSupported(paramDurationFieldType);
/* 515*/    if (paramInt == 0)
/* 516*/      return this; 
/* 518*/    int[] arrayOfInt = getValues();
/* 519*/    arrayOfInt = getField(i).add(this, i, arrayOfInt, paramInt);
/* 520*/    return new MonthDay(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt) {
/* 540*/    if (paramReadablePeriod == null || paramInt == 0)
/* 541*/      return this; 
/* 543*/    int[] arrayOfInt = getValues();
/* 544*/    for (int i = 0; i < paramReadablePeriod.size(); i++) {
/* 545*/      DurationFieldType durationFieldType = paramReadablePeriod.getFieldType(i);
/* 546*/      int j = indexOf(durationFieldType);
/* 547*/      if (j >= 0)
/* 548*/        arrayOfInt = getField(j).add(this, j, arrayOfInt, FieldUtils.safeMultiply(paramReadablePeriod.getValue(i), paramInt)); 
/*   0*/    } 
/* 552*/    return new MonthDay(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay plus(ReadablePeriod paramReadablePeriod) {
/* 570*/    return withPeriodAdded(paramReadablePeriod, 1);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay plusMonths(int paramInt) {
/* 592*/    return withFieldAdded(DurationFieldType.months(), paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay plusDays(int paramInt) {
/* 612*/    return withFieldAdded(DurationFieldType.days(), paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay minus(ReadablePeriod paramReadablePeriod) {
/* 630*/    return withPeriodAdded(paramReadablePeriod, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay minusMonths(int paramInt) {
/* 652*/    return withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(paramInt));
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay minusDays(int paramInt) {
/* 672*/    return withFieldAdded(DurationFieldType.days(), FieldUtils.safeNegate(paramInt));
/*   0*/  }
/*   0*/  
/*   0*/  public LocalDate toLocalDate(int paramInt) {
/* 683*/    return new LocalDate(paramInt, getMonthOfYear(), getDayOfMonth(), getChronology());
/*   0*/  }
/*   0*/  
/*   0*/  public int getMonthOfYear() {
/* 693*/    return getValue(0);
/*   0*/  }
/*   0*/  
/*   0*/  public int getDayOfMonth() {
/* 702*/    return getValue(1);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay withMonthOfYear(int paramInt) {
/* 718*/    int[] arrayOfInt = getValues();
/* 719*/    arrayOfInt = getChronology().monthOfYear().set(this, 0, arrayOfInt, paramInt);
/* 720*/    return new MonthDay(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public MonthDay withDayOfMonth(int paramInt) {
/* 735*/    int[] arrayOfInt = getValues();
/* 736*/    arrayOfInt = getChronology().dayOfMonth().set(this, 1, arrayOfInt, paramInt);
/* 737*/    return new MonthDay(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public Property property(DateTimeFieldType paramDateTimeFieldType) {
/* 750*/    return new Property(this, indexOfSupported(paramDateTimeFieldType));
/*   0*/  }
/*   0*/  
/*   0*/  public Property monthOfYear() {
/* 760*/    return new Property(this, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public Property dayOfMonth() {
/* 769*/    return new Property(this, 1);
/*   0*/  }
/*   0*/  
/*   0*/  @ToString
/*   0*/  public String toString() {
/* 780*/    ArrayList<DateTimeFieldType> arrayList = new ArrayList();
/* 781*/    arrayList.add(DateTimeFieldType.monthOfYear());
/* 782*/    arrayList.add(DateTimeFieldType.dayOfMonth());
/* 783*/    return ISODateTimeFormat.forFields(arrayList, true, true).print(this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(String paramString) {
/* 793*/    if (paramString == null)
/* 794*/      return toString(); 
/* 796*/    return DateTimeFormat.forPattern(paramString).print(this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(String paramString, Locale paramLocale) throws IllegalArgumentException {
/* 807*/    if (paramString == null)
/* 808*/      return toString(); 
/* 810*/    return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
/*   0*/  }
/*   0*/  
/*   0*/  public static class Property extends AbstractPartialFieldProperty implements Serializable {
/*   0*/    private static final long serialVersionUID = 5727734012190224363L;
/*   0*/    
/*   0*/    private final MonthDay iBase;
/*   0*/    
/*   0*/    private final int iFieldIndex;
/*   0*/    
/*   0*/    Property(MonthDay param1MonthDay, int param1Int) {
/* 840*/      this.iBase = param1MonthDay;
/* 841*/      this.iFieldIndex = param1Int;
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeField getField() {
/* 850*/      return this.iBase.getField(this.iFieldIndex);
/*   0*/    }
/*   0*/    
/*   0*/    protected ReadablePartial getReadablePartial() {
/* 859*/      return this.iBase;
/*   0*/    }
/*   0*/    
/*   0*/    public MonthDay getMonthDay() {
/* 868*/      return this.iBase;
/*   0*/    }
/*   0*/    
/*   0*/    public int get() {
/* 877*/      return this.iBase.getValue(this.iFieldIndex);
/*   0*/    }
/*   0*/    
/*   0*/    public MonthDay addToCopy(int param1Int) {
/* 896*/      int[] arrayOfInt = this.iBase.getValues();
/* 897*/      arrayOfInt = getField().add(this.iBase, this.iFieldIndex, arrayOfInt, param1Int);
/* 898*/      return new MonthDay(this.iBase, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public MonthDay addWrapFieldToCopy(int param1Int) {
/* 920*/      int[] arrayOfInt = this.iBase.getValues();
/* 921*/      arrayOfInt = getField().addWrapField(this.iBase, this.iFieldIndex, arrayOfInt, param1Int);
/* 922*/      return new MonthDay(this.iBase, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public MonthDay setCopy(int param1Int) {
/* 937*/      int[] arrayOfInt = this.iBase.getValues();
/* 938*/      arrayOfInt = getField().set(this.iBase, this.iFieldIndex, arrayOfInt, param1Int);
/* 939*/      return new MonthDay(this.iBase, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public MonthDay setCopy(String param1String, Locale param1Locale) {
/* 954*/      int[] arrayOfInt = this.iBase.getValues();
/* 955*/      arrayOfInt = getField().set(this.iBase, this.iFieldIndex, arrayOfInt, param1String, param1Locale);
/* 956*/      return new MonthDay(this.iBase, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public MonthDay setCopy(String param1String) {
/* 970*/      return setCopy(param1String, null);
/*   0*/    }
/*   0*/  }
/*   0*/}
