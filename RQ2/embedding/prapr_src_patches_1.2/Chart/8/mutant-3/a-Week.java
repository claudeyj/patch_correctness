/*   0*/package org.jfree.data.time;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Date;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/
/*   0*/public class Week extends RegularTimePeriod implements Serializable {
/*   0*/  private static final long serialVersionUID = 1856387786939865061L;
/*   0*/  
/*   0*/  public static final int FIRST_WEEK_IN_YEAR = 1;
/*   0*/  
/*   0*/  public static final int LAST_WEEK_IN_YEAR = 53;
/*   0*/  
/*   0*/  private short year;
/*   0*/  
/*   0*/  private byte week;
/*   0*/  
/*   0*/  private long firstMillisecond;
/*   0*/  
/*   0*/  private long lastMillisecond;
/*   0*/  
/*   0*/  public Week() {
/* 118*/    this(new Date());
/*   0*/  }
/*   0*/  
/*   0*/  public Week(int week, int year) {
/* 128*/    if (week < 1 && week > 53)
/* 129*/      throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53."); 
/* 132*/    this.week = (byte)week;
/* 133*/    this.year = (short)year;
/* 134*/    peg(Calendar.getInstance());
/*   0*/  }
/*   0*/  
/*   0*/  public Week(int week, Year year) {
/* 144*/    if (week < 1 && week > 53)
/* 145*/      throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53."); 
/* 148*/    this.week = (byte)week;
/* 149*/    this.year = (short)year.getYear();
/* 150*/    peg(Calendar.getInstance());
/*   0*/  }
/*   0*/  
/*   0*/  public Week(Date time) {
/* 161*/    this(time, RegularTimePeriod.DEFAULT_TIME_ZONE, Locale.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  public Week(Date time, TimeZone zone) {
/* 175*/    this(time, RegularTimePeriod.DEFAULT_TIME_ZONE, Locale.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  public Week(Date time, TimeZone zone, Locale locale) {
/* 189*/    if (time == null)
/* 190*/      throw new IllegalArgumentException("Null 'time' argument."); 
/* 192*/    if (zone == null)
/* 193*/      throw new IllegalArgumentException("Null 'zone' argument."); 
/* 195*/    if (locale == null)
/* 196*/      throw new IllegalArgumentException("Null 'locale' argument."); 
/* 198*/    Calendar calendar = Calendar.getInstance(zone, locale);
/* 199*/    calendar.setTime(time);
/* 204*/    int tempWeek = calendar.get(3);
/* 205*/    if (tempWeek == 1 && calendar.get(2) == 11) {
/* 207*/      this.week = 1;
/* 208*/      this.year = (short)(calendar.get(1) + 1);
/*   0*/    } else {
/* 211*/      this.week = (byte)Math.min(tempWeek, 53);
/* 212*/      int yyyy = calendar.get(1);
/* 215*/      if (calendar.get(2) == 0 && this.week >= 52)
/* 217*/        yyyy--; 
/* 219*/      this.year = (short)yyyy;
/*   0*/    } 
/* 221*/    peg(calendar);
/*   0*/  }
/*   0*/  
/*   0*/  public Year getYear() {
/* 230*/    return new Year(this.year);
/*   0*/  }
/*   0*/  
/*   0*/  public int getYearValue() {
/* 239*/    return this.year;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWeek() {
/* 248*/    return this.week;
/*   0*/  }
/*   0*/  
/*   0*/  public long getFirstMillisecond() {
/* 262*/    return this.firstMillisecond;
/*   0*/  }
/*   0*/  
/*   0*/  public long getLastMillisecond() {
/* 276*/    return this.lastMillisecond;
/*   0*/  }
/*   0*/  
/*   0*/  public void peg(Calendar calendar) {
/* 288*/    this.firstMillisecond = getFirstMillisecond(calendar);
/* 289*/    this.lastMillisecond = getLastMillisecond(calendar);
/*   0*/  }
/*   0*/  
/*   0*/  public RegularTimePeriod previous() {
/*   0*/    Week result;
/* 303*/    if (this.week != 1) {
/* 304*/      result = new Week(this.week - 1, this.year);
/* 308*/    } else if (this.year > 1900) {
/* 309*/      int yy = this.year - 1;
/* 310*/      Calendar prevYearCalendar = Calendar.getInstance();
/* 311*/      prevYearCalendar.set(yy, 11, 31);
/* 312*/      result = new Week(prevYearCalendar.getActualMaximum(3), yy);
/*   0*/    } else {
/* 316*/      result = null;
/*   0*/    } 
/* 319*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public RegularTimePeriod next() {
/*   0*/    Week result;
/* 335*/    if (this.week < 52) {
/* 336*/      result = new Week(this.week + 1, this.year);
/*   0*/    } else {
/* 339*/      Calendar calendar = Calendar.getInstance();
/* 340*/      calendar.set(this.year, 11, 31);
/* 341*/      int actualMaxWeek = calendar.getActualMaximum(3);
/* 343*/      if (this.week < actualMaxWeek) {
/* 344*/        result = new Week(this.week + 1, this.year);
/* 347*/      } else if (this.year < 9999) {
/* 348*/        result = new Week(1, this.year + 1);
/*   0*/      } else {
/* 351*/        result = null;
/*   0*/      } 
/*   0*/    } 
/* 355*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public long getSerialIndex() {
/* 365*/    return this.year * 53L + this.week;
/*   0*/  }
/*   0*/  
/*   0*/  public long getFirstMillisecond(Calendar calendar) {
/* 380*/    Calendar c = (Calendar)calendar.clone();
/* 381*/    c.clear();
/* 382*/    c.set(1, this.year);
/* 383*/    c.set(3, this.week);
/* 384*/    c.set(7, c.getFirstDayOfWeek());
/* 385*/    c.set(10, 0);
/* 386*/    c.set(12, 0);
/* 387*/    c.set(13, 0);
/* 388*/    c.set(14, 0);
/* 390*/    return c.getTime().getTime();
/*   0*/  }
/*   0*/  
/*   0*/  public long getLastMillisecond(Calendar calendar) {
/* 405*/    Calendar c = (Calendar)calendar.clone();
/* 406*/    c.clear();
/* 407*/    c.set(1, this.year);
/* 408*/    c.set(3, this.week + 1);
/* 409*/    c.set(7, c.getFirstDayOfWeek());
/* 410*/    c.set(10, 0);
/* 411*/    c.set(12, 0);
/* 412*/    c.set(13, 0);
/* 413*/    c.set(14, 0);
/* 415*/    return c.getTime().getTime() - 1L;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 426*/    return "Week " + this.week + ", " + this.year;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 441*/    if (obj == this)
/* 442*/      return true; 
/* 444*/    if (!(obj instanceof Week))
/* 445*/      return false; 
/* 447*/    Week that = (Week)obj;
/* 448*/    if (this.week != that.week)
/* 449*/      return false; 
/* 451*/    if (this.year != that.year)
/* 452*/      return false; 
/* 454*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 468*/    int result = 17;
/* 469*/    result = 37 * result + this.week;
/* 470*/    result = 37 * result + this.year;
/* 471*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int compareTo(Object o1) {
/*   0*/    int result;
/* 490*/    if (o1 instanceof Week) {
/* 491*/      Week w = (Week)o1;
/* 492*/      result = this.year - w.getYear().getYear();
/* 493*/      if (result == 0)
/* 494*/        result = this.week - w.getWeek(); 
/* 500*/    } else if (o1 instanceof RegularTimePeriod) {
/* 502*/      result = 0;
/*   0*/    } else {
/* 509*/      result = 1;
/*   0*/    } 
/* 512*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Week parseWeek(String s) {
/* 529*/    Week result = null;
/* 530*/    if (s != null) {
/* 533*/      s = s.trim();
/* 535*/      int i = findSeparator(s);
/* 536*/      if (i != -1) {
/* 537*/        String s1 = s.substring(0, i).trim();
/* 538*/        String s2 = s.substring(i + 1, s.length()).trim();
/* 540*/        Year y = evaluateAsYear(s1);
/* 542*/        if (y != null) {
/* 543*/          int w = stringToWeek(s2);
/* 544*/          if (w == -1)
/* 545*/            throw new TimePeriodFormatException("Can't evaluate the week."); 
/* 548*/          result = new Week(w, y);
/*   0*/        } else {
/* 551*/          y = evaluateAsYear(s2);
/* 552*/          if (y != null) {
/* 553*/            int w = stringToWeek(s1);
/* 554*/            if (w == -1)
/* 555*/              throw new TimePeriodFormatException("Can't evaluate the week."); 
/* 558*/            result = new Week(w, y);
/*   0*/          } else {
/* 561*/            throw new TimePeriodFormatException("Can't evaluate the year.");
/*   0*/          } 
/*   0*/        } 
/*   0*/      } else {
/* 568*/        throw new TimePeriodFormatException("Could not find separator.");
/*   0*/      } 
/*   0*/    } 
/* 573*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private static int findSeparator(String s) {
/* 587*/    int result = s.indexOf('-');
/* 588*/    if (result == -1)
/* 589*/      result = s.indexOf(','); 
/* 591*/    if (result == -1)
/* 592*/      result = s.indexOf(' '); 
/* 594*/    if (result == -1)
/* 595*/      result = s.indexOf('.'); 
/* 597*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private static Year evaluateAsYear(String s) {
/* 611*/    Year result = null;
/*   0*/    try {
/* 613*/      result = Year.parseYear(s);
/* 615*/    } catch (TimePeriodFormatException timePeriodFormatException) {}
/* 618*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private static int stringToWeek(String s) {
/* 631*/    int result = -1;
/* 632*/    s = s.replace('W', ' ');
/* 633*/    s = s.trim();
/*   0*/    try {
/* 635*/      result = Integer.parseInt(s);
/* 636*/      if (result < 1 || result > 53)
/* 637*/        result = -1; 
/* 640*/    } catch (NumberFormatException numberFormatException) {}
/* 643*/    return result;
/*   0*/  }
/*   0*/}
