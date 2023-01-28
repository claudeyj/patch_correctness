/*   0*/package org.joda.time.tz;
/*   0*/
/*   0*/import java.io.DataInput;
/*   0*/import java.io.DataInputStream;
/*   0*/import java.io.DataOutput;
/*   0*/import java.io.DataOutputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.text.DateFormatSymbols;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.Locale;
/*   0*/import org.joda.time.Chronology;
/*   0*/import org.joda.time.DateTime;
/*   0*/import org.joda.time.DateTimeUtils;
/*   0*/import org.joda.time.DateTimeZone;
/*   0*/import org.joda.time.Period;
/*   0*/import org.joda.time.PeriodType;
/*   0*/import org.joda.time.chrono.ISOChronology;
/*   0*/
/*   0*/public class DateTimeZoneBuilder {
/*   0*/  private final ArrayList<RuleSet> iRuleSets;
/*   0*/  
/*   0*/  public static DateTimeZone readFrom(InputStream paramInputStream, String paramString) throws IOException {
/*  95*/    if (paramInputStream instanceof DataInput)
/*  96*/      return readFrom((DataInput)paramInputStream, paramString); 
/*  98*/    return readFrom((DataInput)new DataInputStream(paramInputStream), paramString);
/*   0*/  }
/*   0*/  
/*   0*/  public static DateTimeZone readFrom(DataInput paramDataInput, String paramString) throws IOException {
/*   0*/    DateTimeZone dateTimeZone;
/* 110*/    switch (paramDataInput.readUnsignedByte()) {
/*   0*/      case 70:
/* 112*/        dateTimeZone = new FixedDateTimeZone(paramString, paramDataInput.readUTF(), (int)readMillis(paramDataInput), (int)readMillis(paramDataInput));
/* 114*/        if (dateTimeZone.equals(DateTimeZone.UTC))
/* 115*/          dateTimeZone = DateTimeZone.UTC; 
/* 117*/        return dateTimeZone;
/*   0*/      case 67:
/* 119*/        return CachedDateTimeZone.forZone(PrecalculatedZone.readFrom(paramDataInput, paramString));
/*   0*/      case 80:
/* 121*/        return PrecalculatedZone.readFrom(paramDataInput, paramString);
/*   0*/    } 
/* 123*/    throw new IOException("Invalid encoding");
/*   0*/  }
/*   0*/  
/*   0*/  static void writeMillis(DataOutput paramDataOutput, long paramLong) throws IOException {
/* 140*/    if (paramLong % 1800000L == 0L) {
/* 142*/      long l = paramLong / 1800000L;
/* 143*/      if (l << 58L >> 58L == l) {
/* 145*/        paramDataOutput.writeByte((int)(l & 0x3FL));
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/* 150*/    if (paramLong % 60000L == 0L) {
/* 152*/      long l = paramLong / 60000L;
/* 153*/      if (l << 34L >> 34L == l) {
/* 155*/        paramDataOutput.writeInt(0x40000000 | (int)(l & 0x3FFFFFFFL));
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/* 160*/    if (paramLong % 1000L == 0L) {
/* 162*/      long l = paramLong / 1000L;
/* 163*/      if (l << 26L >> 26L == l) {
/* 165*/        paramDataOutput.writeByte(0x80 | (int)(l >> 32L & 0x3FL));
/* 166*/        paramDataOutput.writeInt((int)(l & 0xFFFFFFFFFFFFFFFFL));
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/* 175*/    paramDataOutput.writeByte((paramLong < 0L) ? 255 : 192);
/* 176*/    paramDataOutput.writeLong(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  static long readMillis(DataInput paramDataInput) throws IOException {
/*   0*/    long l;
/* 183*/    int i = paramDataInput.readUnsignedByte();
/* 184*/    switch (i >> 6) {
/*   0*/      default:
/* 187*/        i = i << 26 >> 26;
/* 188*/        return i * 1800000L;
/*   0*/      case 1:
/* 192*/        i = i << 26 >> 2;
/* 193*/        i |= paramDataInput.readUnsignedByte() << 16;
/* 194*/        i |= paramDataInput.readUnsignedByte() << 8;
/* 195*/        i |= paramDataInput.readUnsignedByte();
/* 196*/        return i * 60000L;
/*   0*/      case 2:
/* 200*/        l = i << 58L >> 26L;
/* 201*/        l |= (paramDataInput.readUnsignedByte() << 24);
/* 202*/        l |= (paramDataInput.readUnsignedByte() << 16);
/* 203*/        l |= (paramDataInput.readUnsignedByte() << 8);
/* 204*/        l |= paramDataInput.readUnsignedByte();
/* 205*/        return l * 1000L;
/*   0*/      case 3:
/*   0*/        break;
/*   0*/    } 
/* 209*/    return paramDataInput.readLong();
/*   0*/  }
/*   0*/  
/*   0*/  private static DateTimeZone buildFixedZone(String paramString1, String paramString2, int paramInt1, int paramInt2) {
/* 215*/    if ("UTC".equals(paramString1) && paramString1.equals(paramString2) && paramInt1 == 0 && paramInt2 == 0)
/* 217*/      return DateTimeZone.UTC; 
/* 219*/    return new FixedDateTimeZone(paramString1, paramString2, paramInt1, paramInt2);
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZoneBuilder() {
/* 226*/    this.iRuleSets = new ArrayList<RuleSet>(10);
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZoneBuilder addCutover(int paramInt1, char paramChar, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5) {
/* 252*/    if (this.iRuleSets.size() > 0) {
/* 253*/      OfYear ofYear = new OfYear(paramChar, paramInt2, paramInt3, paramInt4, paramBoolean, paramInt5);
/* 255*/      RuleSet ruleSet = this.iRuleSets.get(this.iRuleSets.size() - 1);
/* 256*/      ruleSet.setUpperLimit(paramInt1, ofYear);
/*   0*/    } 
/* 258*/    this.iRuleSets.add(new RuleSet());
/* 259*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZoneBuilder setStandardOffset(int paramInt) {
/* 268*/    getLastRuleSet().setStandardOffset(paramInt);
/* 269*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZoneBuilder setFixedSavings(String paramString, int paramInt) {
/* 276*/    getLastRuleSet().setFixedSavings(paramString, paramInt);
/* 277*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZoneBuilder addRecurringSavings(String paramString, int paramInt1, int paramInt2, int paramInt3, char paramChar, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, int paramInt7) {
/* 309*/    if (paramInt2 <= paramInt3) {
/* 310*/      OfYear ofYear = new OfYear(paramChar, paramInt4, paramInt5, paramInt6, paramBoolean, paramInt7);
/* 312*/      Recurrence recurrence = new Recurrence(ofYear, paramString, paramInt1);
/* 313*/      Rule rule = new Rule(recurrence, paramInt2, paramInt3);
/* 314*/      getLastRuleSet().addRule(rule);
/*   0*/    } 
/* 316*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  private RuleSet getLastRuleSet() {
/* 320*/    if (this.iRuleSets.size() == 0)
/* 321*/      addCutover(Integer.MIN_VALUE, 'w', 1, 1, 0, false, 0); 
/* 323*/    return this.iRuleSets.get(this.iRuleSets.size() - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public DateTimeZone toDateTimeZone(String paramString, boolean paramBoolean) {
/* 333*/    if (paramString == null)
/* 334*/      throw new IllegalArgumentException(); 
/* 339*/    ArrayList<Transition> arrayList = new ArrayList();
/* 343*/    DSTZone dSTZone = null;
/* 345*/    long l = Long.MIN_VALUE;
/* 346*/    int i = 0;
/* 348*/    int j = this.iRuleSets.size();
/* 349*/    for (int k = 0; k < j; k++) {
/* 350*/      RuleSet ruleSet = this.iRuleSets.get(k);
/* 351*/      Transition transition = ruleSet.firstTransition(l);
/* 352*/      if (transition != null) {
/* 355*/        addTransition(arrayList, transition);
/* 356*/        l = transition.getMillis();
/* 357*/        i = transition.getSaveMillis();
/* 360*/        ruleSet = new RuleSet(ruleSet);
/* 362*/        while ((transition = ruleSet.nextTransition(l, i)) != null && (
/* 363*/          !addTransition(arrayList, transition) || 
/* 364*/          dSTZone == null)) {
/* 369*/          l = transition.getMillis();
/* 370*/          i = transition.getSaveMillis();
/* 371*/          if (dSTZone == null && k == j - 1)
/* 372*/            dSTZone = ruleSet.buildTailZone(paramString); 
/*   0*/        } 
/* 379*/        l = ruleSet.getUpperLimit(i);
/*   0*/      } 
/*   0*/    } 
/* 383*/    if (arrayList.size() == 0) {
/* 384*/      if (dSTZone != null)
/* 386*/        return dSTZone; 
/* 388*/      return buildFixedZone(paramString, "UTC", 0, 0);
/*   0*/    } 
/* 390*/    if (arrayList.size() == 1 && dSTZone == null) {
/* 391*/      Transition transition = arrayList.get(0);
/* 392*/      return buildFixedZone(paramString, transition.getNameKey(), transition.getWallOffset(), transition.getStandardOffset());
/*   0*/    } 
/* 396*/    PrecalculatedZone precalculatedZone = PrecalculatedZone.create(paramString, paramBoolean, arrayList, dSTZone);
/* 397*/    if (precalculatedZone.isCachable())
/* 398*/      return CachedDateTimeZone.forZone(precalculatedZone); 
/* 400*/    return precalculatedZone;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean addTransition(ArrayList<Transition> paramArrayList, Transition paramTransition) {
/* 404*/    int i = paramArrayList.size();
/* 405*/    if (i == 0) {
/* 406*/      paramArrayList.add(paramTransition);
/* 407*/      return true;
/*   0*/    } 
/* 410*/    Transition transition = paramArrayList.get(i - 1);
/* 411*/    if (!paramTransition.isTransitionFrom(transition))
/* 412*/      return false; 
/* 417*/    int j = 0;
/* 418*/    if (i >= 2)
/* 419*/      j = ((Transition)paramArrayList.get(i - 2)).getWallOffset(); 
/* 421*/    int k = transition.getWallOffset();
/* 423*/    long l1 = transition.getMillis() + j;
/* 424*/    long l2 = paramTransition.getMillis() + k;
/* 426*/    if (l2 != l1) {
/* 427*/      paramArrayList.add(paramTransition);
/* 428*/      return true;
/*   0*/    } 
/* 431*/    paramArrayList.remove(i - 1);
/* 432*/    return addTransition(paramArrayList, paramTransition);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTo(String paramString, OutputStream paramOutputStream) throws IOException {
/* 443*/    if (paramOutputStream instanceof DataOutput) {
/* 444*/      writeTo(paramString, (DataOutput)paramOutputStream);
/*   0*/    } else {
/* 446*/      writeTo(paramString, (DataOutput)new DataOutputStream(paramOutputStream));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTo(String paramString, DataOutput paramDataOutput) throws IOException {
/* 459*/    DateTimeZone dateTimeZone = toDateTimeZone(paramString, false);
/* 461*/    if (dateTimeZone instanceof FixedDateTimeZone) {
/* 462*/      paramDataOutput.writeByte(70);
/* 463*/      paramDataOutput.writeUTF(dateTimeZone.getNameKey(0L));
/* 464*/      writeMillis(paramDataOutput, dateTimeZone.getOffset(0L));
/* 465*/      writeMillis(paramDataOutput, dateTimeZone.getStandardOffset(0L));
/*   0*/    } else {
/* 467*/      if (dateTimeZone instanceof CachedDateTimeZone) {
/* 468*/        paramDataOutput.writeByte(67);
/* 469*/        dateTimeZone = ((CachedDateTimeZone)dateTimeZone).getUncachedZone();
/*   0*/      } else {
/* 471*/        paramDataOutput.writeByte(80);
/*   0*/      } 
/* 473*/      ((PrecalculatedZone)dateTimeZone).writeTo(paramDataOutput);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static final class OfYear {
/*   0*/    final char iMode;
/*   0*/    
/*   0*/    final int iMonthOfYear;
/*   0*/    
/*   0*/    final int iDayOfMonth;
/*   0*/    
/*   0*/    final int iDayOfWeek;
/*   0*/    
/*   0*/    final boolean iAdvance;
/*   0*/    
/*   0*/    final int iMillisOfDay;
/*   0*/    
/*   0*/    static OfYear readFrom(DataInput param1DataInput) throws IOException {
/* 482*/      return new OfYear((char)param1DataInput.readUnsignedByte(), param1DataInput.readUnsignedByte(), param1DataInput.readByte(), param1DataInput.readUnsignedByte(), param1DataInput.readBoolean(), (int)DateTimeZoneBuilder.readMillis(param1DataInput));
/*   0*/    }
/*   0*/    
/*   0*/    OfYear(char param1Char, int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean, int param1Int4) {
/* 505*/      if (param1Char != 'u' && param1Char != 'w' && param1Char != 's')
/* 506*/        throw new IllegalArgumentException("Unknown mode: " + param1Char); 
/* 509*/      this.iMode = param1Char;
/* 510*/      this.iMonthOfYear = param1Int1;
/* 511*/      this.iDayOfMonth = param1Int2;
/* 512*/      this.iDayOfWeek = param1Int3;
/* 513*/      this.iAdvance = param1Boolean;
/* 514*/      this.iMillisOfDay = param1Int4;
/*   0*/    }
/*   0*/    
/*   0*/    public long setInstant(int param1Int1, int param1Int2, int param1Int3) {
/*   0*/      int i;
/* 522*/      if (this.iMode == 'w') {
/* 523*/        i = param1Int2 + param1Int3;
/* 524*/      } else if (this.iMode == 's') {
/* 525*/        i = param1Int2;
/*   0*/      } else {
/* 527*/        i = 0;
/*   0*/      } 
/* 530*/      ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
/* 531*/      long l = iSOChronology.year().set(0L, param1Int1);
/* 532*/      l = iSOChronology.monthOfYear().set(l, this.iMonthOfYear);
/* 533*/      l = iSOChronology.millisOfDay().set(l, this.iMillisOfDay);
/* 534*/      l = setDayOfMonth(iSOChronology, l);
/* 536*/      if (this.iDayOfWeek != 0)
/* 537*/        l = setDayOfWeek(iSOChronology, l); 
/* 541*/      return l - i;
/*   0*/    }
/*   0*/    
/*   0*/    public long next(long param1Long, int param1Int1, int param1Int2) {
/*   0*/      int i;
/* 549*/      if (this.iMode == 'w') {
/* 550*/        i = param1Int1 + param1Int2;
/* 551*/      } else if (this.iMode == 's') {
/* 552*/        i = param1Int1;
/*   0*/      } else {
/* 554*/        i = 0;
/*   0*/      } 
/* 558*/      param1Long += i;
/* 560*/      ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
/* 561*/      long l = iSOChronology.monthOfYear().set(param1Long, this.iMonthOfYear);
/* 563*/      l = iSOChronology.millisOfDay().set(l, 0);
/* 564*/      l = iSOChronology.millisOfDay().add(l, this.iMillisOfDay);
/* 565*/      l = setDayOfMonthNext(iSOChronology, l);
/* 567*/      if (this.iDayOfWeek == 0) {
/* 568*/        if (l <= param1Long) {
/* 569*/          l = iSOChronology.year().add(l, 1);
/* 570*/          l = setDayOfMonthNext(iSOChronology, l);
/*   0*/        } 
/*   0*/      } else {
/* 573*/        l = setDayOfWeek(iSOChronology, l);
/* 574*/        if (l <= param1Long) {
/* 575*/          l = iSOChronology.year().add(l, 1);
/* 576*/          l = iSOChronology.monthOfYear().set(l, this.iMonthOfYear);
/* 577*/          l = setDayOfMonthNext(iSOChronology, l);
/* 578*/          l = setDayOfWeek(iSOChronology, l);
/*   0*/        } 
/*   0*/      } 
/* 583*/      return l - i;
/*   0*/    }
/*   0*/    
/*   0*/    public long previous(long param1Long, int param1Int1, int param1Int2) {
/*   0*/      int i;
/* 591*/      if (this.iMode == 'w') {
/* 592*/        i = param1Int1 + param1Int2;
/* 593*/      } else if (this.iMode == 's') {
/* 594*/        i = param1Int1;
/*   0*/      } else {
/* 596*/        i = 0;
/*   0*/      } 
/* 600*/      param1Long += i;
/* 602*/      ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
/* 603*/      long l = iSOChronology.monthOfYear().set(param1Long, this.iMonthOfYear);
/* 605*/      l = iSOChronology.millisOfDay().set(l, 0);
/* 606*/      l = iSOChronology.millisOfDay().add(l, this.iMillisOfDay);
/* 607*/      l = setDayOfMonthPrevious(iSOChronology, l);
/* 609*/      if (this.iDayOfWeek == 0) {
/* 610*/        if (l >= param1Long) {
/* 611*/          l = iSOChronology.year().add(l, -1);
/* 612*/          l = setDayOfMonthPrevious(iSOChronology, l);
/*   0*/        } 
/*   0*/      } else {
/* 615*/        l = setDayOfWeek(iSOChronology, l);
/* 616*/        if (l >= param1Long) {
/* 617*/          l = iSOChronology.year().add(l, -1);
/* 618*/          l = iSOChronology.monthOfYear().set(l, this.iMonthOfYear);
/* 619*/          l = setDayOfMonthPrevious(iSOChronology, l);
/* 620*/          l = setDayOfWeek(iSOChronology, l);
/*   0*/        } 
/*   0*/      } 
/* 625*/      return l - i;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object param1Object) {
/* 629*/      if (this == param1Object)
/* 630*/        return true; 
/* 632*/      if (param1Object instanceof OfYear) {
/* 633*/        OfYear ofYear = (OfYear)param1Object;
/* 634*/        return (this.iMode == ofYear.iMode && this.iMonthOfYear == ofYear.iMonthOfYear && this.iDayOfMonth == ofYear.iDayOfMonth && this.iDayOfWeek == ofYear.iDayOfWeek && this.iAdvance == ofYear.iAdvance && this.iMillisOfDay == ofYear.iMillisOfDay);
/*   0*/      } 
/* 642*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public void writeTo(DataOutput param1DataOutput) throws IOException {
/* 659*/      param1DataOutput.writeByte(this.iMode);
/* 660*/      param1DataOutput.writeByte(this.iMonthOfYear);
/* 661*/      param1DataOutput.writeByte(this.iDayOfMonth);
/* 662*/      param1DataOutput.writeByte(this.iDayOfWeek);
/* 663*/      param1DataOutput.writeBoolean(this.iAdvance);
/* 664*/      DateTimeZoneBuilder.writeMillis(param1DataOutput, this.iMillisOfDay);
/*   0*/    }
/*   0*/    
/*   0*/    private long setDayOfMonthNext(Chronology param1Chronology, long param1Long) {
/*   0*/      try {
/* 672*/        param1Long = setDayOfMonth(param1Chronology, param1Long);
/* 673*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 674*/        if (this.iMonthOfYear == 2 && this.iDayOfMonth == 29) {
/* 675*/          while (!param1Chronology.year().isLeap(param1Long))
/* 676*/            param1Long = param1Chronology.year().add(param1Long, 1); 
/* 678*/          param1Long = setDayOfMonth(param1Chronology, param1Long);
/*   0*/        } else {
/* 680*/          throw illegalArgumentException;
/*   0*/        } 
/*   0*/      } 
/* 683*/      return param1Long;
/*   0*/    }
/*   0*/    
/*   0*/    private long setDayOfMonthPrevious(Chronology param1Chronology, long param1Long) {
/*   0*/      try {
/* 691*/        param1Long = setDayOfMonth(param1Chronology, param1Long);
/* 692*/      } catch (IllegalArgumentException illegalArgumentException) {
/* 693*/        if (this.iMonthOfYear == 2 && this.iDayOfMonth == 29) {
/* 694*/          while (!param1Chronology.year().isLeap(param1Long))
/* 695*/            param1Long = param1Chronology.year().add(param1Long, -1); 
/* 697*/          param1Long = setDayOfMonth(param1Chronology, param1Long);
/*   0*/        } else {
/* 699*/          throw illegalArgumentException;
/*   0*/        } 
/*   0*/      } 
/* 702*/      return param1Long;
/*   0*/    }
/*   0*/    
/*   0*/    private long setDayOfMonth(Chronology param1Chronology, long param1Long) {
/* 706*/      if (this.iDayOfMonth >= 0) {
/* 707*/        param1Long = param1Chronology.dayOfMonth().set(param1Long, this.iDayOfMonth);
/*   0*/      } else {
/* 709*/        param1Long = param1Chronology.dayOfMonth().set(param1Long, 1);
/* 710*/        param1Long = param1Chronology.monthOfYear().add(param1Long, 1);
/* 711*/        param1Long = param1Chronology.dayOfMonth().add(param1Long, this.iDayOfMonth);
/*   0*/      } 
/* 713*/      return param1Long;
/*   0*/    }
/*   0*/    
/*   0*/    private long setDayOfWeek(Chronology param1Chronology, long param1Long) {
/* 717*/      int i = param1Chronology.dayOfWeek().get(param1Long);
/* 718*/      int j = this.iDayOfWeek - i;
/* 719*/      if (j != 0) {
/* 720*/        if (this.iAdvance) {
/* 721*/          if (j < 0)
/* 722*/            j += 7; 
/* 725*/        } else if (j > 0) {
/* 726*/          j -= 7;
/*   0*/        } 
/* 729*/        param1Long = param1Chronology.dayOfWeek().add(param1Long, j);
/*   0*/      } 
/* 731*/      return param1Long;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class Recurrence {
/*   0*/    final DateTimeZoneBuilder.OfYear iOfYear;
/*   0*/    
/*   0*/    final String iNameKey;
/*   0*/    
/*   0*/    final int iSaveMillis;
/*   0*/    
/*   0*/    static Recurrence readFrom(DataInput param1DataInput) throws IOException {
/* 740*/      return new Recurrence(DateTimeZoneBuilder.OfYear.readFrom(param1DataInput), param1DataInput.readUTF(), (int)DateTimeZoneBuilder.readMillis(param1DataInput));
/*   0*/    }
/*   0*/    
/*   0*/    Recurrence(DateTimeZoneBuilder.OfYear param1OfYear, String param1String, int param1Int) {
/* 748*/      this.iOfYear = param1OfYear;
/* 749*/      this.iNameKey = param1String;
/* 750*/      this.iSaveMillis = param1Int;
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeZoneBuilder.OfYear getOfYear() {
/* 754*/      return this.iOfYear;
/*   0*/    }
/*   0*/    
/*   0*/    public long next(long param1Long, int param1Int1, int param1Int2) {
/* 761*/      return this.iOfYear.next(param1Long, param1Int1, param1Int2);
/*   0*/    }
/*   0*/    
/*   0*/    public long previous(long param1Long, int param1Int1, int param1Int2) {
/* 768*/      return this.iOfYear.previous(param1Long, param1Int1, param1Int2);
/*   0*/    }
/*   0*/    
/*   0*/    public String getNameKey() {
/* 772*/      return this.iNameKey;
/*   0*/    }
/*   0*/    
/*   0*/    public int getSaveMillis() {
/* 776*/      return this.iSaveMillis;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object param1Object) {
/* 780*/      if (this == param1Object)
/* 781*/        return true; 
/* 783*/      if (param1Object instanceof Recurrence) {
/* 784*/        Recurrence recurrence = (Recurrence)param1Object;
/* 785*/        return (this.iSaveMillis == recurrence.iSaveMillis && this.iNameKey.equals(recurrence.iNameKey) && this.iOfYear.equals(recurrence.iOfYear));
/*   0*/      } 
/* 790*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public void writeTo(DataOutput param1DataOutput) throws IOException {
/* 794*/      this.iOfYear.writeTo(param1DataOutput);
/* 795*/      param1DataOutput.writeUTF(this.iNameKey);
/* 796*/      DateTimeZoneBuilder.writeMillis(param1DataOutput, this.iSaveMillis);
/*   0*/    }
/*   0*/    
/*   0*/    Recurrence rename(String param1String) {
/* 800*/      return new Recurrence(this.iOfYear, param1String, this.iSaveMillis);
/*   0*/    }
/*   0*/    
/*   0*/    Recurrence renameAppend(String param1String) {
/* 804*/      return rename((this.iNameKey + param1String).intern());
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class Rule {
/*   0*/    final DateTimeZoneBuilder.Recurrence iRecurrence;
/*   0*/    
/*   0*/    final int iFromYear;
/*   0*/    
/*   0*/    final int iToYear;
/*   0*/    
/*   0*/    Rule(DateTimeZoneBuilder.Recurrence param1Recurrence, int param1Int1, int param1Int2) {
/* 817*/      this.iRecurrence = param1Recurrence;
/* 818*/      this.iFromYear = param1Int1;
/* 819*/      this.iToYear = param1Int2;
/*   0*/    }
/*   0*/    
/*   0*/    public int getFromYear() {
/* 823*/      return this.iFromYear;
/*   0*/    }
/*   0*/    
/*   0*/    public int getToYear() {
/* 827*/      return getSaveMillis();
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeZoneBuilder.OfYear getOfYear() {
/* 831*/      return this.iRecurrence.getOfYear();
/*   0*/    }
/*   0*/    
/*   0*/    public String getNameKey() {
/* 835*/      return this.iRecurrence.getNameKey();
/*   0*/    }
/*   0*/    
/*   0*/    public int getSaveMillis() {
/* 839*/      return this.iRecurrence.getSaveMillis();
/*   0*/    }
/*   0*/    
/*   0*/    public long next(long param1Long, int param1Int1, int param1Int2) {
/*   0*/      int j;
/* 843*/      ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
/* 845*/      int i = param1Int1 + param1Int2;
/* 846*/      long l1 = param1Long;
/* 849*/      if (param1Long == Long.MIN_VALUE) {
/* 850*/        j = Integer.MIN_VALUE;
/*   0*/      } else {
/* 852*/        j = iSOChronology.year().get(param1Long + i);
/*   0*/      } 
/* 855*/      if (j < this.iFromYear) {
/* 857*/        l1 = iSOChronology.year().set(0L, this.iFromYear) - i;
/* 860*/        l1--;
/*   0*/      } 
/* 863*/      long l2 = this.iRecurrence.next(l1, param1Int1, param1Int2);
/* 865*/      if (l2 > param1Long) {
/* 866*/        j = iSOChronology.year().get(l2 + i);
/* 867*/        if (j > this.iToYear)
/* 869*/          l2 = param1Long; 
/*   0*/      } 
/* 873*/      return l2;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class Transition {
/*   0*/    private final long iMillis;
/*   0*/    
/*   0*/    private final String iNameKey;
/*   0*/    
/*   0*/    private final int iWallOffset;
/*   0*/    
/*   0*/    private final int iStandardOffset;
/*   0*/    
/*   0*/    Transition(long param1Long, Transition param1Transition) {
/* 884*/      this.iMillis = param1Long;
/* 885*/      this.iNameKey = param1Transition.iNameKey;
/* 886*/      this.iWallOffset = param1Transition.iWallOffset;
/* 887*/      this.iStandardOffset = param1Transition.iStandardOffset;
/*   0*/    }
/*   0*/    
/*   0*/    Transition(long param1Long, DateTimeZoneBuilder.Rule param1Rule, int param1Int) {
/* 891*/      this.iMillis = param1Long;
/* 892*/      this.iNameKey = param1Rule.getNameKey();
/* 893*/      this.iWallOffset = param1Int + param1Rule.getSaveMillis();
/* 894*/      this.iStandardOffset = param1Int;
/*   0*/    }
/*   0*/    
/*   0*/    Transition(long param1Long, String param1String, int param1Int1, int param1Int2) {
/* 899*/      this.iMillis = param1Long;
/* 900*/      this.iNameKey = param1String;
/* 901*/      this.iWallOffset = param1Int1;
/* 902*/      this.iStandardOffset = param1Int2;
/*   0*/    }
/*   0*/    
/*   0*/    public long getMillis() {
/* 906*/      return this.iMillis;
/*   0*/    }
/*   0*/    
/*   0*/    public String getNameKey() {
/* 910*/      return this.iNameKey;
/*   0*/    }
/*   0*/    
/*   0*/    public int getWallOffset() {
/* 914*/      return this.iWallOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public int getStandardOffset() {
/* 918*/      return this.iStandardOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public int getSaveMillis() {
/* 922*/      return this.iWallOffset - this.iStandardOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isTransitionFrom(Transition param1Transition) {
/* 929*/      if (param1Transition == null)
/* 930*/        return true; 
/* 932*/      return (this.iMillis > param1Transition.iMillis && (this.iWallOffset != param1Transition.iWallOffset || !this.iNameKey.equals(param1Transition.iNameKey)));
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class RuleSet {
/*   0*/    private static final int YEAR_LIMIT;
/*   0*/    
/*   0*/    private int iStandardOffset;
/*   0*/    
/*   0*/    private ArrayList<DateTimeZoneBuilder.Rule> iRules;
/*   0*/    
/*   0*/    private String iInitialNameKey;
/*   0*/    
/*   0*/    private int iInitialSaveMillis;
/*   0*/    
/*   0*/    private int iUpperYear;
/*   0*/    
/*   0*/    private DateTimeZoneBuilder.OfYear iUpperOfYear;
/*   0*/    
/*   0*/    static {
/* 948*/      long l = DateTimeUtils.currentTimeMillis();
/* 949*/      YEAR_LIMIT = ISOChronology.getInstanceUTC().year().get(l) + 100;
/*   0*/    }
/*   0*/    
/*   0*/    RuleSet() {
/* 964*/      this.iRules = new ArrayList<DateTimeZoneBuilder.Rule>(10);
/* 965*/      this.iUpperYear = Integer.MAX_VALUE;
/*   0*/    }
/*   0*/    
/*   0*/    RuleSet(RuleSet param1RuleSet) {
/* 972*/      this.iStandardOffset = param1RuleSet.iStandardOffset;
/* 973*/      this.iRules = new ArrayList<DateTimeZoneBuilder.Rule>(param1RuleSet.iRules);
/* 974*/      this.iInitialNameKey = param1RuleSet.iInitialNameKey;
/* 975*/      this.iInitialSaveMillis = param1RuleSet.iInitialSaveMillis;
/* 976*/      this.iUpperYear = param1RuleSet.iUpperYear;
/* 977*/      this.iUpperOfYear = param1RuleSet.iUpperOfYear;
/*   0*/    }
/*   0*/    
/*   0*/    public int getStandardOffset() {
/* 981*/      return this.iStandardOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public void setStandardOffset(int param1Int) {
/* 985*/      this.iStandardOffset = param1Int;
/*   0*/    }
/*   0*/    
/*   0*/    public void setFixedSavings(String param1String, int param1Int) {
/* 989*/      this.iInitialNameKey = param1String;
/* 990*/      this.iInitialSaveMillis = param1Int;
/*   0*/    }
/*   0*/    
/*   0*/    public void addRule(DateTimeZoneBuilder.Rule param1Rule) {
/* 994*/      if (!this.iRules.contains(param1Rule))
/* 995*/        this.iRules.add(param1Rule); 
/*   0*/    }
/*   0*/    
/*   0*/    public void setUpperLimit(int param1Int, DateTimeZoneBuilder.OfYear param1OfYear) {
/*1000*/      this.iUpperYear = param1Int;
/*1001*/      this.iUpperOfYear = param1OfYear;
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeZoneBuilder.Transition firstTransition(long param1Long) {
/*1011*/      if (this.iInitialNameKey != null)
/*1013*/        return new DateTimeZoneBuilder.Transition(param1Long, this.iInitialNameKey, this.iStandardOffset + this.iInitialSaveMillis, this.iStandardOffset); 
/*1018*/      ArrayList<DateTimeZoneBuilder.Rule> arrayList = new ArrayList<DateTimeZoneBuilder.Rule>(this.iRules);
/*1024*/      long l = Long.MIN_VALUE;
/*1025*/      int i = 0;
/*1026*/      DateTimeZoneBuilder.Transition transition1 = null;
/*   0*/      DateTimeZoneBuilder.Transition transition2;
/*1029*/      while ((transition2 = nextTransition(l, i)) != null) {
/*1030*/        l = transition2.getMillis();
/*1032*/        if (l == param1Long) {
/*1033*/          transition1 = new DateTimeZoneBuilder.Transition(param1Long, transition2);
/*   0*/          break;
/*   0*/        } 
/*1037*/        if (l > param1Long) {
/*1038*/          if (transition1 == null)
/*1042*/            for (DateTimeZoneBuilder.Rule rule : arrayList) {
/*1043*/              if (rule.getSaveMillis() == 0) {
/*1044*/                transition1 = new DateTimeZoneBuilder.Transition(param1Long, rule, this.iStandardOffset);
/*   0*/                break;
/*   0*/              } 
/*   0*/            }  
/*1049*/          if (transition1 == null)
/*1053*/            transition1 = new DateTimeZoneBuilder.Transition(param1Long, transition2.getNameKey(), this.iStandardOffset, this.iStandardOffset); 
/*   0*/          break;
/*   0*/        } 
/*1061*/        transition1 = new DateTimeZoneBuilder.Transition(param1Long, transition2);
/*1063*/        i = transition2.getSaveMillis();
/*   0*/      } 
/*1066*/      this.iRules = arrayList;
/*1067*/      return transition1;
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeZoneBuilder.Transition nextTransition(long param1Long, int param1Int) {
/*1082*/      ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
/*1085*/      DateTimeZoneBuilder.Rule rule = null;
/*1086*/      long l = Long.MAX_VALUE;
/*1088*/      Iterator<DateTimeZoneBuilder.Rule> iterator = this.iRules.iterator();
/*1089*/      while (iterator.hasNext()) {
/*1090*/        DateTimeZoneBuilder.Rule rule1 = iterator.next();
/*1091*/        long l1 = rule1.next(param1Long, this.iStandardOffset, param1Int);
/*1092*/        if (l1 <= param1Long) {
/*1093*/          iterator.remove();
/*   0*/          continue;
/*   0*/        } 
/*1098*/        if (l1 <= l) {
/*1100*/          rule = rule1;
/*1101*/          l = l1;
/*   0*/        } 
/*   0*/      } 
/*1105*/      if (rule == null)
/*1106*/        return null; 
/*1110*/      if (iSOChronology.year().get(l) >= YEAR_LIMIT)
/*1111*/        return null; 
/*1115*/      if (this.iUpperYear < Integer.MAX_VALUE) {
/*1116*/        long l1 = this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, param1Int);
/*1118*/        if (l >= l1)
/*1120*/          return null; 
/*   0*/      } 
/*1124*/      return new DateTimeZoneBuilder.Transition(l, rule, this.iStandardOffset);
/*   0*/    }
/*   0*/    
/*   0*/    public long getUpperLimit(int param1Int) {
/*1131*/      if (this.iUpperYear == Integer.MAX_VALUE)
/*1132*/        return Long.MAX_VALUE; 
/*1134*/      return this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, param1Int);
/*   0*/    }
/*   0*/    
/*   0*/    public DateTimeZoneBuilder.DSTZone buildTailZone(String param1String) {
/*1141*/      if (this.iRules.size() == 2) {
/*1142*/        DateTimeZoneBuilder.Rule rule1 = this.iRules.get(0);
/*1143*/        DateTimeZoneBuilder.Rule rule2 = this.iRules.get(1);
/*1144*/        if (rule1.getToYear() == Integer.MAX_VALUE && rule2.getToYear() == Integer.MAX_VALUE)
/*1154*/          return new DateTimeZoneBuilder.DSTZone(param1String, this.iStandardOffset, rule1.iRecurrence, rule2.iRecurrence); 
/*   0*/      } 
/*1158*/      return null;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class DSTZone extends DateTimeZone {
/*   0*/    private static final long serialVersionUID = 6941492635554961361L;
/*   0*/    
/*   0*/    final int iStandardOffset;
/*   0*/    
/*   0*/    final DateTimeZoneBuilder.Recurrence iStartRecurrence;
/*   0*/    
/*   0*/    final DateTimeZoneBuilder.Recurrence iEndRecurrence;
/*   0*/    
/*   0*/    static DSTZone readFrom(DataInput param1DataInput, String param1String) throws IOException {
/*1166*/      return new DSTZone(param1String, (int)DateTimeZoneBuilder.readMillis(param1DataInput), DateTimeZoneBuilder.Recurrence.readFrom(param1DataInput), DateTimeZoneBuilder.Recurrence.readFrom(param1DataInput));
/*   0*/    }
/*   0*/    
/*   0*/    DSTZone(String param1String, int param1Int, DateTimeZoneBuilder.Recurrence param1Recurrence1, DateTimeZoneBuilder.Recurrence param1Recurrence2) {
/*1176*/      super(param1String);
/*1177*/      this.iStandardOffset = param1Int;
/*1178*/      this.iStartRecurrence = param1Recurrence1;
/*1179*/      this.iEndRecurrence = param1Recurrence2;
/*   0*/    }
/*   0*/    
/*   0*/    public String getNameKey(long param1Long) {
/*1183*/      return findMatchingRecurrence(param1Long).getNameKey();
/*   0*/    }
/*   0*/    
/*   0*/    public int getOffset(long param1Long) {
/*1187*/      return this.iStandardOffset + findMatchingRecurrence(param1Long).getSaveMillis();
/*   0*/    }
/*   0*/    
/*   0*/    public int getStandardOffset(long param1Long) {
/*1191*/      return this.iStandardOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isFixed() {
/*1195*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public long nextTransition(long param1Long) {
/*   0*/      long l1, l2;
/*1199*/      int i = this.iStandardOffset;
/*1200*/      DateTimeZoneBuilder.Recurrence recurrence1 = this.iStartRecurrence;
/*1201*/      DateTimeZoneBuilder.Recurrence recurrence2 = this.iEndRecurrence;
/*   0*/      try {
/*1206*/        l1 = recurrence1.next(param1Long, i, recurrence2.getSaveMillis());
/*1208*/        if (param1Long > 0L && l1 < 0L)
/*1210*/          l1 = param1Long; 
/*1212*/      } catch (IllegalArgumentException illegalArgumentException) {
/*1214*/        l1 = param1Long;
/*1215*/      } catch (ArithmeticException arithmeticException) {
/*1217*/        l1 = param1Long;
/*   0*/      } 
/*   0*/      try {
/*1221*/        l2 = recurrence2.next(param1Long, i, recurrence1.getSaveMillis());
/*1223*/        if (param1Long > 0L && l2 < 0L)
/*1225*/          l2 = param1Long; 
/*1227*/      } catch (IllegalArgumentException illegalArgumentException) {
/*1229*/        l2 = param1Long;
/*1230*/      } catch (ArithmeticException arithmeticException) {
/*1232*/        l2 = param1Long;
/*   0*/      } 
/*1235*/      return (l1 > l2) ? l2 : l1;
/*   0*/    }
/*   0*/    
/*   0*/    public long previousTransition(long param1Long) {
/*   0*/      long l1, l2;
/*1241*/      param1Long++;
/*1243*/      int i = this.iStandardOffset;
/*1244*/      DateTimeZoneBuilder.Recurrence recurrence1 = this.iStartRecurrence;
/*1245*/      DateTimeZoneBuilder.Recurrence recurrence2 = this.iEndRecurrence;
/*   0*/      try {
/*1250*/        l1 = recurrence1.previous(param1Long, i, recurrence2.getSaveMillis());
/*1252*/        if (param1Long < 0L && l1 > 0L)
/*1254*/          l1 = param1Long; 
/*1256*/      } catch (IllegalArgumentException illegalArgumentException) {
/*1258*/        l1 = param1Long;
/*1259*/      } catch (ArithmeticException arithmeticException) {
/*1261*/        l1 = param1Long;
/*   0*/      } 
/*   0*/      try {
/*1265*/        l2 = recurrence2.previous(param1Long, i, recurrence1.getSaveMillis());
/*1267*/        if (param1Long < 0L && l2 > 0L)
/*1269*/          l2 = param1Long; 
/*1271*/      } catch (IllegalArgumentException illegalArgumentException) {
/*1273*/        l2 = param1Long;
/*1274*/      } catch (ArithmeticException arithmeticException) {
/*1276*/        l2 = param1Long;
/*   0*/      } 
/*1279*/      return ((l1 > l2) ? l1 : l2) - 1L;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object param1Object) {
/*1283*/      if (this == param1Object)
/*1284*/        return true; 
/*1286*/      if (param1Object instanceof DSTZone) {
/*1287*/        DSTZone dSTZone = (DSTZone)param1Object;
/*1288*/        return (getID().equals(dSTZone.getID()) && this.iStandardOffset == dSTZone.iStandardOffset && this.iStartRecurrence.equals(dSTZone.iStartRecurrence) && this.iEndRecurrence.equals(dSTZone.iEndRecurrence));
/*   0*/      } 
/*1294*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public void writeTo(DataOutput param1DataOutput) throws IOException {
/*1298*/      DateTimeZoneBuilder.writeMillis(param1DataOutput, this.iStandardOffset);
/*1299*/      this.iStartRecurrence.writeTo(param1DataOutput);
/*1300*/      this.iEndRecurrence.writeTo(param1DataOutput);
/*   0*/    }
/*   0*/    
/*   0*/    private DateTimeZoneBuilder.Recurrence findMatchingRecurrence(long param1Long) {
/*   0*/      long l1, l2;
/*1304*/      int i = this.iStandardOffset;
/*1305*/      DateTimeZoneBuilder.Recurrence recurrence1 = this.iStartRecurrence;
/*1306*/      DateTimeZoneBuilder.Recurrence recurrence2 = this.iEndRecurrence;
/*   0*/      try {
/*1311*/        l1 = recurrence1.next(param1Long, i, recurrence2.getSaveMillis());
/*1313*/      } catch (IllegalArgumentException illegalArgumentException) {
/*1315*/        l1 = param1Long;
/*1316*/      } catch (ArithmeticException arithmeticException) {
/*1318*/        l1 = param1Long;
/*   0*/      } 
/*   0*/      try {
/*1322*/        l2 = recurrence2.next(param1Long, i, recurrence1.getSaveMillis());
/*1324*/      } catch (IllegalArgumentException illegalArgumentException) {
/*1326*/        l2 = param1Long;
/*1327*/      } catch (ArithmeticException arithmeticException) {
/*1329*/        l2 = param1Long;
/*   0*/      } 
/*1332*/      return (l1 > l2) ? recurrence1 : recurrence2;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class PrecalculatedZone extends DateTimeZone {
/*   0*/    private static final long serialVersionUID = 7811976468055766265L;
/*   0*/    
/*   0*/    private final long[] iTransitions;
/*   0*/    
/*   0*/    private final int[] iWallOffsets;
/*   0*/    
/*   0*/    private final int[] iStandardOffsets;
/*   0*/    
/*   0*/    private final String[] iNameKeys;
/*   0*/    
/*   0*/    private final DateTimeZoneBuilder.DSTZone iTailZone;
/*   0*/    
/*   0*/    static PrecalculatedZone readFrom(DataInput param1DataInput, String param1String) throws IOException {
/*1341*/      int i = param1DataInput.readUnsignedShort();
/*1342*/      String[] arrayOfString1 = new String[i];
/*   0*/      int j;
/*1343*/      for (j = 0; j < i; j++)
/*1344*/        arrayOfString1[j] = param1DataInput.readUTF(); 
/*1347*/      j = param1DataInput.readInt();
/*1348*/      long[] arrayOfLong = new long[j];
/*1349*/      int[] arrayOfInt1 = new int[j];
/*1350*/      int[] arrayOfInt2 = new int[j];
/*1351*/      String[] arrayOfString2 = new String[j];
/*1353*/      for (int k = 0; k < j; k++) {
/*1354*/        arrayOfLong[k] = DateTimeZoneBuilder.readMillis(param1DataInput);
/*1355*/        arrayOfInt1[k] = (int)DateTimeZoneBuilder.readMillis(param1DataInput);
/*1356*/        arrayOfInt2[k] = (int)DateTimeZoneBuilder.readMillis(param1DataInput);
/*   0*/        try {
/*   0*/          int m;
/*1359*/          if (i < 256) {
/*1360*/            m = param1DataInput.readUnsignedByte();
/*   0*/          } else {
/*1362*/            m = param1DataInput.readUnsignedShort();
/*   0*/          } 
/*1364*/          arrayOfString2[k] = arrayOfString1[m];
/*1365*/        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
/*1366*/          throw new IOException("Invalid encoding");
/*   0*/        } 
/*   0*/      } 
/*1370*/      DateTimeZoneBuilder.DSTZone dSTZone = null;
/*1371*/      if (param1DataInput.readBoolean())
/*1372*/        dSTZone = DateTimeZoneBuilder.DSTZone.readFrom(param1DataInput, param1String); 
/*1375*/      return new PrecalculatedZone(param1String, arrayOfLong, arrayOfInt1, arrayOfInt2, arrayOfString2, dSTZone);
/*   0*/    }
/*   0*/    
/*   0*/    static PrecalculatedZone create(String param1String, boolean param1Boolean, ArrayList<DateTimeZoneBuilder.Transition> param1ArrayList, DateTimeZoneBuilder.DSTZone param1DSTZone) {
/*1389*/      int i = param1ArrayList.size();
/*1390*/      if (i == 0)
/*1391*/        throw new IllegalArgumentException(); 
/*1394*/      long[] arrayOfLong = new long[i];
/*1395*/      int[] arrayOfInt1 = new int[i];
/*1396*/      int[] arrayOfInt2 = new int[i];
/*1397*/      String[] arrayOfString1 = new String[i];
/*1399*/      DateTimeZoneBuilder.Transition transition = null;
/*1400*/      for (int j = 0; j < i; j++) {
/*1401*/        DateTimeZoneBuilder.Transition transition1 = param1ArrayList.get(j);
/*1403*/        if (!transition1.isTransitionFrom(transition))
/*1404*/          throw new IllegalArgumentException(param1String); 
/*1407*/        arrayOfLong[j] = transition1.getMillis();
/*1408*/        arrayOfInt1[j] = transition1.getWallOffset();
/*1409*/        arrayOfInt2[j] = transition1.getStandardOffset();
/*1410*/        arrayOfString1[j] = transition1.getNameKey();
/*1412*/        transition = transition1;
/*   0*/      } 
/*1417*/      String[] arrayOfString2 = new String[5];
/*1418*/      String[][] arrayOfString = new DateFormatSymbols(Locale.ENGLISH).getZoneStrings();
/*1419*/      for (int k = 0; k < arrayOfString.length; k++) {
/*1420*/        String[] arrayOfString3 = arrayOfString[k];
/*1421*/        if (arrayOfString3 != null && arrayOfString3.length == 5 && param1String.equals(arrayOfString3[0]))
/*1422*/          arrayOfString2 = arrayOfString3; 
/*   0*/      } 
/*1426*/      ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
/*1428*/      for (int m = 0; m < arrayOfString1.length - 1; m++) {
/*1429*/        String str1 = arrayOfString1[m];
/*1430*/        String str2 = arrayOfString1[m + 1];
/*1431*/        long l1 = arrayOfInt1[m];
/*1432*/        long l2 = arrayOfInt1[m + 1];
/*1433*/        long l3 = arrayOfInt2[m];
/*1434*/        long l4 = arrayOfInt2[m + 1];
/*1435*/        Period period = new Period(arrayOfLong[m], arrayOfLong[m + 1], PeriodType.yearMonthDay(), iSOChronology);
/*1436*/        if (l1 != l2 && l3 == l4 && str1.equals(str2) && period.getYears() == 0 && period.getMonths() > 4 && period.getMonths() < 8 && str1.equals(arrayOfString2[2]) && str1.equals(arrayOfString2[4])) {
/*1443*/          if (ZoneInfoCompiler.verbose()) {
/*1444*/            System.out.println("Fixing duplicate name key - " + str2);
/*1445*/            System.out.println("     - " + new DateTime(arrayOfLong[m], iSOChronology) + " - " + new DateTime(arrayOfLong[m + 1], iSOChronology));
/*   0*/          } 
/*1448*/          if (l1 > l2) {
/*1449*/            arrayOfString1[m] = (str1 + "-Summer").intern();
/*1450*/          } else if (l1 < l2) {
/*1451*/            arrayOfString1[m + 1] = (str2 + "-Summer").intern();
/*1452*/            m++;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1457*/      if (param1DSTZone != null && 
/*1458*/        param1DSTZone.iStartRecurrence.getNameKey().equals(param1DSTZone.iEndRecurrence.getNameKey())) {
/*1460*/        if (ZoneInfoCompiler.verbose())
/*1461*/          System.out.println("Fixing duplicate recurrent name key - " + param1DSTZone.iStartRecurrence.getNameKey()); 
/*1464*/        if (param1DSTZone.iStartRecurrence.getSaveMillis() > 0) {
/*1465*/          param1DSTZone = new DateTimeZoneBuilder.DSTZone(param1DSTZone.getID(), param1DSTZone.iStandardOffset, param1DSTZone.iStartRecurrence.renameAppend("-Summer"), param1DSTZone.iEndRecurrence);
/*   0*/        } else {
/*1471*/          param1DSTZone = new DateTimeZoneBuilder.DSTZone(param1DSTZone.getID(), param1DSTZone.iStandardOffset, param1DSTZone.iStartRecurrence, param1DSTZone.iEndRecurrence.renameAppend("-Summer"));
/*   0*/        } 
/*   0*/      } 
/*1480*/      return new PrecalculatedZone(param1Boolean ? param1String : "", arrayOfLong, arrayOfInt1, arrayOfInt2, arrayOfString1, param1DSTZone);
/*   0*/    }
/*   0*/    
/*   0*/    private PrecalculatedZone(String param1String, long[] param1ArrayOflong, int[] param1ArrayOfint1, int[] param1ArrayOfint2, String[] param1ArrayOfString, DateTimeZoneBuilder.DSTZone param1DSTZone) {
/*1500*/      super(param1String);
/*1501*/      this.iTransitions = param1ArrayOflong;
/*1502*/      this.iWallOffsets = param1ArrayOfint1;
/*1503*/      this.iStandardOffsets = param1ArrayOfint2;
/*1504*/      this.iNameKeys = param1ArrayOfString;
/*1505*/      this.iTailZone = param1DSTZone;
/*   0*/    }
/*   0*/    
/*   0*/    public String getNameKey(long param1Long) {
/*1509*/      long[] arrayOfLong = this.iTransitions;
/*1510*/      int i = Arrays.binarySearch(arrayOfLong, param1Long);
/*1511*/      if (i >= 0)
/*1512*/        return this.iNameKeys[i]; 
/*1514*/      i ^= 0xFFFFFFFF;
/*1515*/      if (i < arrayOfLong.length) {
/*1516*/        if (i > 0)
/*1517*/          return this.iNameKeys[i - 1]; 
/*1519*/        return "UTC";
/*   0*/      } 
/*1521*/      if (this.iTailZone == null)
/*1522*/        return this.iNameKeys[i - 1]; 
/*1524*/      return this.iTailZone.getNameKey(param1Long);
/*   0*/    }
/*   0*/    
/*   0*/    public int getOffset(long param1Long) {
/*1528*/      long[] arrayOfLong = this.iTransitions;
/*1529*/      int i = Arrays.binarySearch(arrayOfLong, param1Long);
/*1530*/      if (i >= 0)
/*1531*/        return this.iWallOffsets[i]; 
/*1533*/      i ^= 0xFFFFFFFF;
/*1534*/      if (i < arrayOfLong.length) {
/*1535*/        if (i > 0)
/*1536*/          return this.iWallOffsets[i - 1]; 
/*1538*/        return 0;
/*   0*/      } 
/*1540*/      if (this.iTailZone == null)
/*1541*/        return this.iWallOffsets[i - 1]; 
/*1543*/      return this.iTailZone.getOffset(param1Long);
/*   0*/    }
/*   0*/    
/*   0*/    public int getStandardOffset(long param1Long) {
/*1547*/      long[] arrayOfLong = this.iTransitions;
/*1548*/      int i = Arrays.binarySearch(arrayOfLong, param1Long);
/*1549*/      if (i >= 0)
/*1550*/        return this.iStandardOffsets[i]; 
/*1552*/      i ^= 0xFFFFFFFF;
/*1553*/      if (i < arrayOfLong.length) {
/*1554*/        if (i > 0)
/*1555*/          return this.iStandardOffsets[i - 1]; 
/*1557*/        return 0;
/*   0*/      } 
/*1559*/      if (this.iTailZone == null)
/*1560*/        return this.iStandardOffsets[i - 1]; 
/*1562*/      return this.iTailZone.getStandardOffset(param1Long);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isFixed() {
/*1566*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public long nextTransition(long param1Long) {
/*1570*/      long[] arrayOfLong = this.iTransitions;
/*1571*/      int i = Arrays.binarySearch(arrayOfLong, param1Long);
/*1572*/      i = (i >= 0) ? (i + 1) : (i ^ 0xFFFFFFFF);
/*1573*/      if (i < arrayOfLong.length)
/*1574*/        return arrayOfLong[i]; 
/*1576*/      if (this.iTailZone == null)
/*1577*/        return param1Long; 
/*1579*/      long l = arrayOfLong[arrayOfLong.length - 1];
/*1580*/      if (param1Long < l)
/*1581*/        param1Long = l; 
/*1583*/      return this.iTailZone.nextTransition(param1Long);
/*   0*/    }
/*   0*/    
/*   0*/    public long previousTransition(long param1Long) {
/*1587*/      long[] arrayOfLong = this.iTransitions;
/*1588*/      int i = Arrays.binarySearch(arrayOfLong, param1Long);
/*1589*/      if (i >= 0) {
/*1590*/        if (param1Long > Long.MIN_VALUE)
/*1591*/          return param1Long - 1L; 
/*1593*/        return param1Long;
/*   0*/      } 
/*1595*/      i ^= 0xFFFFFFFF;
/*1596*/      if (i < arrayOfLong.length) {
/*1597*/        if (i > 0) {
/*1598*/          long l1 = arrayOfLong[i - 1];
/*1599*/          if (l1 > Long.MIN_VALUE)
/*1600*/            return l1 - 1L; 
/*   0*/        } 
/*1603*/        return param1Long;
/*   0*/      } 
/*1605*/      if (this.iTailZone != null) {
/*1606*/        long l1 = this.iTailZone.previousTransition(param1Long);
/*1607*/        if (l1 < param1Long)
/*1608*/          return l1; 
/*   0*/      } 
/*1611*/      long l = arrayOfLong[i - 1];
/*1612*/      if (l > Long.MIN_VALUE)
/*1613*/        return l - 1L; 
/*1615*/      return param1Long;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object param1Object) {
/*1619*/      if (this == param1Object)
/*1620*/        return true; 
/*1622*/      if (param1Object instanceof PrecalculatedZone) {
/*1623*/        PrecalculatedZone precalculatedZone = (PrecalculatedZone)param1Object;
/*1624*/        return (getID().equals(precalculatedZone.getID()) && Arrays.equals(this.iTransitions, precalculatedZone.iTransitions) && Arrays.equals((Object[])this.iNameKeys, (Object[])precalculatedZone.iNameKeys) && Arrays.equals(this.iWallOffsets, precalculatedZone.iWallOffsets) && Arrays.equals(this.iStandardOffsets, precalculatedZone.iStandardOffsets) && ((this.iTailZone == null) ? (null == precalculatedZone.iTailZone) : this.iTailZone.equals(precalculatedZone.iTailZone)));
/*   0*/      } 
/*1634*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public void writeTo(DataOutput param1DataOutput) throws IOException {
/*1638*/      int i = this.iTransitions.length;
/*1641*/      HashSet<String> hashSet = new HashSet();
/*   0*/      int j;
/*1642*/      for (j = 0; j < i; j++)
/*1643*/        hashSet.add(this.iNameKeys[j]); 
/*1646*/      j = hashSet.size();
/*1647*/      if (j > 65535)
/*1648*/        throw new UnsupportedOperationException("String pool is too large"); 
/*1650*/      String[] arrayOfString = new String[j];
/*1651*/      Iterator<String> iterator = hashSet.iterator();
/*1652*/      for (int n = 0; iterator.hasNext(); n++)
/*1653*/        arrayOfString[n] = iterator.next(); 
/*1657*/      param1DataOutput.writeShort(j);
/*1658*/      for (int m = 0; m < j; m++)
/*1659*/        param1DataOutput.writeUTF(arrayOfString[m]); 
/*1662*/      param1DataOutput.writeInt(i);
/*1664*/      for (int k = 0; k < i; k++) {
/*1665*/        DateTimeZoneBuilder.writeMillis(param1DataOutput, this.iTransitions[k]);
/*1666*/        DateTimeZoneBuilder.writeMillis(param1DataOutput, this.iWallOffsets[k]);
/*1667*/        DateTimeZoneBuilder.writeMillis(param1DataOutput, this.iStandardOffsets[k]);
/*1670*/        String str = this.iNameKeys[k];
/*1671*/        for (int i1 = 0; i1 < j; i1++) {
/*1672*/          if (arrayOfString[i1].equals(str)) {
/*1673*/            if (j < 256) {
/*1674*/              param1DataOutput.writeByte(i1);
/*   0*/              break;
/*   0*/            } 
/*1676*/            param1DataOutput.writeShort(i1);
/*   0*/            break;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*1683*/      param1DataOutput.writeBoolean((this.iTailZone != null));
/*1684*/      if (this.iTailZone != null)
/*1685*/        this.iTailZone.writeTo(param1DataOutput); 
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isCachable() {
/*1690*/      if (this.iTailZone != null)
/*1691*/        return true; 
/*1693*/      long[] arrayOfLong = this.iTransitions;
/*1694*/      if (arrayOfLong.length <= 1)
/*1695*/        return false; 
/*1700*/      double d = 0.0D;
/*1701*/      int i = 0;
/*1703*/      for (int j = 1; j < arrayOfLong.length; j++) {
/*1704*/        long l = arrayOfLong[j] - arrayOfLong[j - 1];
/*1705*/        if (l < 63158400000L) {
/*1706*/          d += l;
/*1707*/          i++;
/*   0*/        } 
/*   0*/      } 
/*1711*/      if (i > 0) {
/*1712*/        double d1 = d / i;
/*1713*/        d1 /= 8.64E7D;
/*1714*/        if (d1 >= 25.0D)
/*1721*/          return true; 
/*   0*/      } 
/*1725*/      return false;
/*   0*/    }
/*   0*/  }
/*   0*/}
