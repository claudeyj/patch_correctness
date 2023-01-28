/*   0*/package org.joda.time;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Locale;
/*   0*/import org.joda.time.base.AbstractPartial;
/*   0*/import org.joda.time.field.AbstractPartialFieldProperty;
/*   0*/import org.joda.time.field.FieldUtils;
/*   0*/import org.joda.time.format.DateTimeFormat;
/*   0*/import org.joda.time.format.DateTimeFormatter;
/*   0*/import org.joda.time.format.ISODateTimeFormat;
/*   0*/
/*   0*/public final class Partial extends AbstractPartial implements ReadablePartial, Serializable {
/*   0*/  private static final long serialVersionUID = 12324121189002L;
/*   0*/  
/*   0*/  private final Chronology iChronology;
/*   0*/  
/*   0*/  private final DateTimeFieldType[] iTypes;
/*   0*/  
/*   0*/  private final int[] iValues;
/*   0*/  
/*   0*/  private transient DateTimeFormatter[] iFormatter;
/*   0*/  
/*   0*/  public Partial() {
/* 103*/    this((Chronology)null);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial(Chronology paramChronology) {
/* 124*/    this.iChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/* 125*/    this.iTypes = new DateTimeFieldType[0];
/* 126*/    this.iValues = new int[0];
/*   0*/  }
/*   0*/  
/*   0*/  public Partial(DateTimeFieldType paramDateTimeFieldType, int paramInt) {
/* 139*/    this(paramDateTimeFieldType, paramInt, null);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial(DateTimeFieldType paramDateTimeFieldType, int paramInt, Chronology paramChronology) {
/* 154*/    paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/* 155*/    this.iChronology = paramChronology;
/* 156*/    if (paramDateTimeFieldType == null)
/* 157*/      throw new IllegalArgumentException("The field type must not be null"); 
/* 159*/    this.iTypes = new DateTimeFieldType[] { paramDateTimeFieldType };
/* 160*/    this.iValues = new int[] { paramInt };
/* 161*/    paramChronology.validate(this, this.iValues);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial(DateTimeFieldType[] paramArrayOfDateTimeFieldType, int[] paramArrayOfint) {
/* 175*/    this(paramArrayOfDateTimeFieldType, paramArrayOfint, null);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial(DateTimeFieldType[] paramArrayOfDateTimeFieldType, int[] paramArrayOfint, Chronology paramChronology) {
/* 191*/    paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/* 192*/    this.iChronology = paramChronology;
/* 193*/    if (paramArrayOfDateTimeFieldType == null)
/* 194*/      throw new IllegalArgumentException("Types array must not be null"); 
/* 196*/    if (paramArrayOfint == null)
/* 197*/      throw new IllegalArgumentException("Values array must not be null"); 
/* 199*/    if (paramArrayOfint.length != paramArrayOfDateTimeFieldType.length)
/* 200*/      throw new IllegalArgumentException("Values array must be the same length as the types array"); 
/* 202*/    if (paramArrayOfDateTimeFieldType.length == 0) {
/* 203*/      this.iTypes = paramArrayOfDateTimeFieldType;
/* 204*/      this.iValues = paramArrayOfint;
/*   0*/      return;
/*   0*/    } 
/* 207*/    for (int i = 0; i < paramArrayOfDateTimeFieldType.length; i++) {
/* 208*/      if (paramArrayOfDateTimeFieldType[i] == null)
/* 209*/        throw new IllegalArgumentException("Types array must not contain null: index " + i); 
/*   0*/    } 
/* 212*/    DurationField durationField = null;
/* 213*/    for (int j = 0; j < paramArrayOfDateTimeFieldType.length; j++) {
/* 214*/      DateTimeFieldType dateTimeFieldType = paramArrayOfDateTimeFieldType[j];
/* 215*/      DurationField durationField1 = dateTimeFieldType.getDurationType().getField(this.iChronology);
/* 216*/      if (j > 0) {
/* 217*/        int k = durationField.compareTo(durationField1);
/* 218*/        if (k < 0 || (k != 0 && !durationField1.isSupported()))
/* 219*/          throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[j - 1].getName() + " < " + dateTimeFieldType.getName()); 
/* 221*/        if (k == 0)
/* 222*/          if (paramArrayOfDateTimeFieldType[j - 1].getRangeDurationType() == null) {
/* 223*/            if (dateTimeFieldType.getRangeDurationType() == null)
/* 224*/              throw new IllegalArgumentException("Types array must not contain duplicate: " + dateTimeFieldType.getName()); 
/*   0*/          } else {
/* 227*/            if (dateTimeFieldType.getRangeDurationType() == null)
/* 228*/              throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[j - 1].getName() + " < " + dateTimeFieldType.getName()); 
/* 231*/            DurationField durationField2 = paramArrayOfDateTimeFieldType[j - 1].getRangeDurationType().getField(this.iChronology);
/* 232*/            DurationField durationField3 = dateTimeFieldType.getRangeDurationType().getField(this.iChronology);
/* 233*/            if (durationField2.compareTo(durationField3) < 0)
/* 234*/              throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[j - 1].getName() + " < " + dateTimeFieldType.getName()); 
/* 237*/            if (durationField2.compareTo(durationField3) == 0)
/* 238*/              throw new IllegalArgumentException("Types array must not contain duplicate: " + dateTimeFieldType.getName()); 
/*   0*/          }  
/*   0*/      } 
/* 243*/      durationField = durationField1;
/*   0*/    } 
/* 246*/    this.iTypes = (DateTimeFieldType[])paramArrayOfDateTimeFieldType.clone();
/* 247*/    paramChronology.validate(this, paramArrayOfint);
/* 248*/    this.iValues = (int[])paramArrayOfint.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public Partial(ReadablePartial paramReadablePartial) {
/* 259*/    if (paramReadablePartial == null)
/* 260*/      throw new IllegalArgumentException("The partial must not be null"); 
/* 262*/    this.iChronology = DateTimeUtils.getChronology(paramReadablePartial.getChronology()).withUTC();
/* 263*/    this.iTypes = new DateTimeFieldType[paramReadablePartial.size()];
/* 264*/    this.iValues = new int[paramReadablePartial.size()];
/* 265*/    for (int i = 0; i < paramReadablePartial.size(); i++) {
/* 266*/      this.iTypes[i] = paramReadablePartial.getFieldType(i);
/* 267*/      this.iValues[i] = paramReadablePartial.getValue(i);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  Partial(Partial paramPartial, int[] paramArrayOfint) {
/* 281*/    this.iChronology = paramPartial.iChronology;
/* 282*/    this.iTypes = paramPartial.iTypes;
/* 283*/    this.iValues = paramArrayOfint;
/*   0*/  }
/*   0*/  
/*   0*/  Partial(Chronology paramChronology, DateTimeFieldType[] paramArrayOfDateTimeFieldType, int[] paramArrayOfint) {
/* 297*/    this.iChronology = paramChronology;
/* 298*/    this.iTypes = paramArrayOfDateTimeFieldType;
/* 299*/    this.iValues = paramArrayOfint;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 309*/    return this.iTypes.length;
/*   0*/  }
/*   0*/  
/*   0*/  public Chronology getChronology() {
/* 321*/    return this.iChronology;
/*   0*/  }
/*   0*/  
/*   0*/  protected DateTimeField getField(int paramInt, Chronology paramChronology) {
/* 333*/    return this.iTypes[paramInt].getField(paramChronology);
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeFieldType getFieldType(int paramInt) {
/* 344*/    return this.iTypes[paramInt];
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeFieldType[] getFieldTypes() {
/* 356*/    return (DateTimeFieldType[])this.iTypes.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public int getValue(int paramInt) {
/* 368*/    return this.iValues[paramInt];
/*   0*/  }
/*   0*/  
/*   0*/  public int[] getValues() {
/* 381*/    return (int[])this.iValues.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public Partial withChronologyRetainFields(Chronology paramChronology) {
/* 400*/    paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 401*/    paramChronology = paramChronology.withUTC();
/* 402*/    if (paramChronology == getChronology())
/* 403*/      return this; 
/* 405*/    Partial partial = new Partial(paramChronology, this.iTypes, this.iValues);
/* 406*/    paramChronology.validate(partial, this.iValues);
/* 407*/    return partial;
/*   0*/  }
/*   0*/  
/*   0*/  public Partial with(DateTimeFieldType paramDateTimeFieldType, int paramInt) {
/* 427*/    if (paramDateTimeFieldType == null)
/* 428*/      throw new IllegalArgumentException("The field type must not be null"); 
/* 430*/    int i = indexOf(paramDateTimeFieldType);
/* 431*/    if (i == -1) {
/* 432*/      DateTimeFieldType[] arrayOfDateTimeFieldType = new DateTimeFieldType[this.iTypes.length + 1];
/* 433*/      int[] arrayOfInt1 = new int[arrayOfDateTimeFieldType.length];
/* 436*/      int j = 0;
/* 437*/      DurationField durationField = paramDateTimeFieldType.getDurationType().getField(this.iChronology);
/* 438*/      if (durationField.isSupported())
/* 439*/        for (; j < this.iTypes.length; j++) {
/* 440*/          DateTimeFieldType dateTimeFieldType = this.iTypes[j];
/* 441*/          DurationField durationField1 = dateTimeFieldType.getDurationType().getField(this.iChronology);
/* 442*/          if (durationField1.isSupported()) {
/* 443*/            int k = durationField.compareTo(durationField1);
/* 444*/            if (k > 0)
/*   0*/              break; 
/* 446*/            if (k == 0) {
/* 447*/              DurationField durationField2 = paramDateTimeFieldType.getRangeDurationType().getField(this.iChronology);
/* 448*/              DurationField durationField3 = dateTimeFieldType.getRangeDurationType().getField(this.iChronology);
/* 449*/              if (durationField2.compareTo(durationField3) > 0)
/*   0*/                break; 
/*   0*/            } 
/*   0*/          } 
/*   0*/        }  
/* 456*/      System.arraycopy(this.iTypes, 0, arrayOfDateTimeFieldType, 0, j);
/* 457*/      System.arraycopy(this.iValues, 0, arrayOfInt1, 0, j);
/* 458*/      arrayOfDateTimeFieldType[j] = paramDateTimeFieldType;
/* 459*/      arrayOfInt1[j] = paramInt;
/* 460*/      System.arraycopy(this.iTypes, j, arrayOfDateTimeFieldType, j + 1, arrayOfDateTimeFieldType.length - j - 1);
/* 461*/      System.arraycopy(this.iValues, j, arrayOfInt1, j + 1, arrayOfInt1.length - j - 1);
/* 464*/      Partial partial = new Partial(this.iChronology, arrayOfDateTimeFieldType, arrayOfInt1);
/* 465*/      this.iChronology.validate(partial, arrayOfInt1);
/* 466*/      return partial;
/*   0*/    } 
/* 468*/    if (paramInt == getValue(i))
/* 469*/      return this; 
/* 471*/    int[] arrayOfInt = getValues();
/* 472*/    arrayOfInt = getField(i).set(this, i, arrayOfInt, paramInt);
/* 473*/    return new Partial(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial without(DateTimeFieldType paramDateTimeFieldType) {
/* 485*/    int i = indexOf(paramDateTimeFieldType);
/* 486*/    if (i != -1) {
/* 487*/      DateTimeFieldType[] arrayOfDateTimeFieldType = new DateTimeFieldType[size() - 1];
/* 488*/      int[] arrayOfInt = new int[size() - 1];
/* 489*/      System.arraycopy(this.iTypes, 0, arrayOfDateTimeFieldType, 0, i);
/* 490*/      System.arraycopy(this.iTypes, i + 1, arrayOfDateTimeFieldType, i, arrayOfDateTimeFieldType.length - i);
/* 491*/      System.arraycopy(this.iValues, 0, arrayOfInt, 0, i);
/* 492*/      System.arraycopy(this.iValues, i + 1, arrayOfInt, i, arrayOfInt.length - i);
/* 493*/      Partial partial = new Partial(this.iChronology, arrayOfDateTimeFieldType, arrayOfInt);
/* 494*/      this.iChronology.validate(partial, arrayOfInt);
/* 495*/      return partial;
/*   0*/    } 
/* 497*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Partial withField(DateTimeFieldType paramDateTimeFieldType, int paramInt) {
/* 516*/    int i = indexOfSupported(paramDateTimeFieldType);
/* 517*/    if (paramInt == getValue(i))
/* 518*/      return this; 
/* 520*/    int[] arrayOfInt = getValues();
/* 521*/    arrayOfInt = getField(i).set(this, i, arrayOfInt, paramInt);
/* 522*/    return new Partial(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt) {
/* 540*/    int i = indexOfSupported(paramDurationFieldType);
/* 541*/    if (paramInt == 0)
/* 542*/      return this; 
/* 544*/    int[] arrayOfInt = getValues();
/* 545*/    arrayOfInt = getField(i).add(this, i, arrayOfInt, paramInt);
/* 546*/    return new Partial(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial withFieldAddWrapped(DurationFieldType paramDurationFieldType, int paramInt) {
/* 564*/    int i = indexOfSupported(paramDurationFieldType);
/* 565*/    if (paramInt == 0)
/* 566*/      return this; 
/* 568*/    int[] arrayOfInt = getValues();
/* 569*/    arrayOfInt = getField(i).addWrapPartial(this, i, arrayOfInt, paramInt);
/* 570*/    return new Partial(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt) {
/* 589*/    if (paramReadablePeriod == null || paramInt == 0)
/* 590*/      return this; 
/* 592*/    int[] arrayOfInt = getValues();
/* 593*/    for (int i = 0; i < paramReadablePeriod.size(); i++) {
/* 594*/      DurationFieldType durationFieldType = paramReadablePeriod.getFieldType(i);
/* 595*/      int j = indexOf(durationFieldType);
/* 596*/      if (j >= 0)
/* 597*/        arrayOfInt = getField(j).add(this, j, arrayOfInt, FieldUtils.safeMultiply(paramReadablePeriod.getValue(i), paramInt)); 
/*   0*/    } 
/* 601*/    return new Partial(this, arrayOfInt);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial plus(ReadablePeriod paramReadablePeriod) {
/* 614*/    return withPeriodAdded(paramReadablePeriod, 1);
/*   0*/  }
/*   0*/  
/*   0*/  public Partial minus(ReadablePeriod paramReadablePeriod) {
/* 627*/    return withPeriodAdded(paramReadablePeriod, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public Property property(DateTimeFieldType paramDateTimeFieldType) {
/* 642*/    return new Property(this, indexOfSupported(paramDateTimeFieldType));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isMatch(ReadableInstant paramReadableInstant) {
/* 656*/    long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 657*/    Chronology chronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 658*/    for (int i = 0; i < this.iTypes.length; i++) {
/* 659*/      int j = this.iTypes[i].getField(chronology).get(l);
/* 660*/      if (j != this.iValues[i])
/* 661*/        return false; 
/*   0*/    } 
/* 664*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isMatch(ReadablePartial paramReadablePartial) {
/* 680*/    if (paramReadablePartial == null)
/* 681*/      throw new IllegalArgumentException("The partial must not be null"); 
/* 683*/    for (int i = 0; i < this.iTypes.length; i++) {
/* 684*/      int j = paramReadablePartial.get(this.iTypes[i]);
/* 685*/      if (j != this.iValues[i])
/* 686*/        return false; 
/*   0*/    } 
/* 689*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeFormatter getFormatter() {
/* 705*/    DateTimeFormatter[] arrayOfDateTimeFormatter = this.iFormatter;
/* 706*/    if (arrayOfDateTimeFormatter == null) {
/* 707*/      if (size() == 0)
/* 708*/        return null; 
/* 710*/      arrayOfDateTimeFormatter = new DateTimeFormatter[2];
/*   0*/      try {
/* 712*/        ArrayList<DateTimeFieldType> arrayList = new ArrayList(Arrays.asList((Object[])this.iTypes));
/* 713*/        arrayOfDateTimeFormatter[0] = ISODateTimeFormat.forFields(arrayList, true, false);
/* 714*/        if (arrayList.size() == 0)
/* 715*/          arrayOfDateTimeFormatter[1] = arrayOfDateTimeFormatter[0]; 
/* 717*/      } catch (IllegalArgumentException illegalArgumentException) {}
/* 720*/      this.iFormatter = arrayOfDateTimeFormatter;
/*   0*/    } 
/* 722*/    return arrayOfDateTimeFormatter[0];
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 738*/    DateTimeFormatter[] arrayOfDateTimeFormatter = this.iFormatter;
/* 739*/    if (arrayOfDateTimeFormatter == null) {
/* 740*/      getFormatter();
/* 741*/      arrayOfDateTimeFormatter = this.iFormatter;
/* 742*/      if (arrayOfDateTimeFormatter == null)
/* 743*/        return toStringList(); 
/*   0*/    } 
/* 746*/    DateTimeFormatter dateTimeFormatter = arrayOfDateTimeFormatter[1];
/* 747*/    if (dateTimeFormatter == null)
/* 748*/      return toStringList(); 
/* 750*/    return dateTimeFormatter.print(this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toStringList() {
/* 763*/    int i = size();
/* 764*/    StringBuilder stringBuilder = new StringBuilder(20 * i);
/* 765*/    stringBuilder.append('[');
/* 766*/    for (int j = 0; j < i; j++) {
/* 767*/      if (j > 0)
/* 768*/        stringBuilder.append(',').append(' '); 
/* 770*/      stringBuilder.append(this.iTypes[j].getName());
/* 771*/      stringBuilder.append('=');
/* 772*/      stringBuilder.append(this.iValues[j]);
/*   0*/    } 
/* 774*/    stringBuilder.append(']');
/* 775*/    return stringBuilder.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(String paramString) {
/* 786*/    if (paramString == null)
/* 787*/      return toString(); 
/* 789*/    return DateTimeFormat.forPattern(paramString).print(this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString(String paramString, Locale paramLocale) {
/* 801*/    if (paramString == null)
/* 802*/      return toString(); 
/* 804*/    return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
/*   0*/  }
/*   0*/  
/*   0*/  public static class Property extends AbstractPartialFieldProperty implements Serializable {
/*   0*/    private static final long serialVersionUID = 53278362873888L;
/*   0*/    
/*   0*/    private final Partial iPartial;
/*   0*/    
/*   0*/    private final int iFieldIndex;
/*   0*/    
/*   0*/    Property(Partial param1Partial, int param1Int) {
/* 834*/      this.iPartial = param1Partial;
/* 835*/      this.iFieldIndex = param1Int;
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeField getField() {
/* 844*/      return this.iPartial.getField(this.iFieldIndex);
/*   0*/    }
/*   0*/    
/*   0*/    protected ReadablePartial getReadablePartial() {
/* 853*/      return this.iPartial;
/*   0*/    }
/*   0*/    
/*   0*/    public Partial getPartial() {
/* 862*/      return this.iPartial;
/*   0*/    }
/*   0*/    
/*   0*/    public int get() {
/* 871*/      return this.iPartial.getValue(this.iFieldIndex);
/*   0*/    }
/*   0*/    
/*   0*/    public Partial addToCopy(int param1Int) {
/* 893*/      int[] arrayOfInt = this.iPartial.getValues();
/* 894*/      arrayOfInt = getField().add(this.iPartial, this.iFieldIndex, arrayOfInt, param1Int);
/* 895*/      return new Partial(this.iPartial, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public Partial addWrapFieldToCopy(int param1Int) {
/* 917*/      int[] arrayOfInt = this.iPartial.getValues();
/* 918*/      arrayOfInt = getField().addWrapField(this.iPartial, this.iFieldIndex, arrayOfInt, param1Int);
/* 919*/      return new Partial(this.iPartial, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public Partial setCopy(int param1Int) {
/* 934*/      int[] arrayOfInt = this.iPartial.getValues();
/* 935*/      arrayOfInt = getField().set(this.iPartial, this.iFieldIndex, arrayOfInt, param1Int);
/* 936*/      return new Partial(this.iPartial, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public Partial setCopy(String param1String, Locale param1Locale) {
/* 951*/      int[] arrayOfInt = this.iPartial.getValues();
/* 952*/      arrayOfInt = getField().set(this.iPartial, this.iFieldIndex, arrayOfInt, param1String, param1Locale);
/* 953*/      return new Partial(this.iPartial, arrayOfInt);
/*   0*/    }
/*   0*/    
/*   0*/    public Partial setCopy(String param1String) {
/* 967*/      return setCopy(param1String, null);
/*   0*/    }
/*   0*/    
/*   0*/    public Partial withMaximumValue() {
/* 981*/      return setCopy(getMaximumValue());
/*   0*/    }
/*   0*/    
/*   0*/    public Partial withMinimumValue() {
/* 994*/      return setCopy(getMinimumValue());
/*   0*/    }
/*   0*/  }
/*   0*/}
