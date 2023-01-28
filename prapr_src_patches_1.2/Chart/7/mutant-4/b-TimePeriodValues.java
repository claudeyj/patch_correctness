/*   0*/package org.jfree.data.time;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.List;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.data.general.Series;
/*   0*/import org.jfree.data.general.SeriesException;
/*   0*/
/*   0*/public class TimePeriodValues extends Series implements Serializable {
/*   0*/  static final long serialVersionUID = -2210593619794989709L;
/*   0*/  
/*   0*/  protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";
/*   0*/  
/*   0*/  protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";
/*   0*/  
/*   0*/  private String domain;
/*   0*/  
/*   0*/  private String range;
/*   0*/  
/*   0*/  private List data;
/*   0*/  
/*  91*/  private int minStartIndex = -1;
/*   0*/  
/*  94*/  private int maxStartIndex = -1;
/*   0*/  
/*  97*/  private int minMiddleIndex = -1;
/*   0*/  
/* 100*/  private int maxMiddleIndex = -1;
/*   0*/  
/* 103*/  private int minEndIndex = -1;
/*   0*/  
/* 106*/  private int maxEndIndex = -1;
/*   0*/  
/*   0*/  public TimePeriodValues(Comparable name) {
/* 114*/    this(name, "Time", "Value");
/*   0*/  }
/*   0*/  
/*   0*/  public TimePeriodValues(Comparable name, String domain, String range) {
/* 129*/    super(name);
/* 130*/    this.domain = domain;
/* 131*/    this.range = range;
/* 132*/    this.data = new ArrayList();
/*   0*/  }
/*   0*/  
/*   0*/  public String getDomainDescription() {
/* 144*/    return this.domain;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainDescription(String description) {
/* 156*/    String old = this.domain;
/* 157*/    this.domain = description;
/* 158*/    firePropertyChange("Domain", old, description);
/*   0*/  }
/*   0*/  
/*   0*/  public String getRangeDescription() {
/* 170*/    return this.range;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeDescription(String description) {
/* 182*/    String old = this.range;
/* 183*/    this.range = description;
/* 184*/    firePropertyChange("Range", old, description);
/*   0*/  }
/*   0*/  
/*   0*/  public int getItemCount() {
/* 193*/    return this.data.size();
/*   0*/  }
/*   0*/  
/*   0*/  public TimePeriodValue getDataItem(int index) {
/* 205*/    return this.data.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public TimePeriod getTimePeriod(int index) {
/* 219*/    return getDataItem(index).getPeriod();
/*   0*/  }
/*   0*/  
/*   0*/  public Number getValue(int index) {
/* 233*/    return getDataItem(index).getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public void add(TimePeriodValue item) {
/* 243*/    if (item == null)
/* 244*/      throw new IllegalArgumentException("Null item not allowed."); 
/* 246*/    this.data.add(item);
/* 247*/    updateBounds(item.getPeriod(), this.data.size() - 1);
/* 248*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  private void updateBounds(TimePeriod period, int index) {
/* 259*/    long start = period.getStart().getTime();
/* 260*/    long end = period.getEnd().getTime();
/* 261*/    long middle = start + (end - start) / 2L;
/* 263*/    if (this.minStartIndex >= 0) {
/* 264*/      long minStart = getDataItem(this.minStartIndex).getPeriod().getStart().getTime();
/* 266*/      if (start < minStart)
/* 267*/        this.minStartIndex = index; 
/*   0*/    } else {
/* 271*/      this.minStartIndex = index;
/*   0*/    } 
/* 274*/    if (this.maxStartIndex >= 0) {
/* 275*/      long maxStart = getDataItem(this.maxStartIndex).getPeriod().getStart().getTime();
/* 277*/      if (start > maxStart)
/* 278*/        this.maxStartIndex = index; 
/*   0*/    } else {
/* 282*/      this.maxStartIndex = index;
/*   0*/    } 
/* 285*/    if (this.minMiddleIndex >= 0) {
/* 286*/      long s = getDataItem(this.minMiddleIndex).getPeriod().getStart().getTime();
/* 288*/      long e = getDataItem(this.minMiddleIndex).getPeriod().getEnd().getTime();
/* 290*/      long minMiddle = s + (e - s) / 2L;
/* 291*/      if (middle < minMiddle)
/* 292*/        this.minMiddleIndex = index; 
/*   0*/    } else {
/* 296*/      this.minMiddleIndex = index;
/*   0*/    } 
/* 299*/    if (this.maxMiddleIndex >= 0) {
/* 300*/      long s = getDataItem(this.minMiddleIndex).getPeriod().getStart().getTime();
/* 302*/      long e = getDataItem(this.minMiddleIndex).getPeriod().getEnd().getTime();
/* 304*/      long maxMiddle = s + (e - s) / 2L;
/* 305*/      if (middle > maxMiddle)
/* 306*/        this.maxMiddleIndex = index; 
/*   0*/    } else {
/* 310*/      this.maxMiddleIndex = index;
/*   0*/    } 
/* 313*/    if (this.minEndIndex >= 0) {
/* 314*/      long minEnd = getDataItem(this.minEndIndex).getPeriod().getEnd().getTime();
/* 316*/      if (end < minEnd)
/* 317*/        this.minEndIndex = index; 
/*   0*/    } else {
/* 321*/      this.minEndIndex = index;
/*   0*/    } 
/* 324*/    if (this.maxEndIndex >= 0) {
/* 325*/      long maxEnd = getDataItem(this.maxEndIndex).getPeriod().getEnd().getTime();
/* 327*/      if (end > maxEnd)
/* 328*/        this.maxEndIndex = index; 
/*   0*/    } else {
/* 332*/      this.maxEndIndex = index;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void recalculateBounds() {
/* 341*/    this.minStartIndex = -1;
/* 342*/    this.minMiddleIndex = -1;
/* 343*/    this.minEndIndex = -1;
/* 344*/    this.maxStartIndex = -1;
/* 345*/    this.maxMiddleIndex = -1;
/* 346*/    this.maxEndIndex = -1;
/* 347*/    for (int i = 0; i < this.data.size(); i++) {
/* 348*/      TimePeriodValue tpv = this.data.get(i);
/* 349*/      updateBounds(tpv.getPeriod(), i);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void add(TimePeriod period, double value) {
/* 363*/    TimePeriodValue item = new TimePeriodValue(period, value);
/* 364*/    add(item);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(TimePeriod period, Number value) {
/* 375*/    TimePeriodValue item = new TimePeriodValue(period, value);
/* 376*/    add(item);
/*   0*/  }
/*   0*/  
/*   0*/  public void update(int index, Number value) {
/* 387*/    TimePeriodValue item = getDataItem(index);
/* 388*/    item.setValue(value);
/* 389*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void delete(int start, int end) {
/* 400*/    for (int i = 0; i <= end - start; i++)
/* 401*/      this.data.remove(start); 
/* 403*/    recalculateBounds();
/* 404*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 415*/    if (obj == this)
/* 416*/      return true; 
/* 418*/    if (!(obj instanceof TimePeriodValues))
/* 419*/      return false; 
/* 421*/    if (!super.equals(obj))
/* 422*/      return false; 
/* 424*/    TimePeriodValues that = (TimePeriodValues)obj;
/* 425*/    if (!ObjectUtilities.equal(getDomainDescription(), that.getDomainDescription()))
/* 427*/      return false; 
/* 429*/    if (!ObjectUtilities.equal(getRangeDescription(), that.getRangeDescription()))
/* 431*/      return false; 
/* 433*/    int count = getItemCount();
/* 434*/    if (count != that.getItemCount())
/* 435*/      return false; 
/* 437*/    for (int i = 0; i < count; i++) {
/* 438*/      if (!getDataItem(i).equals(that.getDataItem(i)))
/* 439*/        return false; 
/*   0*/    } 
/* 442*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 452*/    int result = (this.domain != null) ? this.domain.hashCode() : 0;
/* 453*/    result = 29 * result + ((this.range != null) ? this.range.hashCode() : 0);
/* 454*/    result = 29 * result + this.data.hashCode();
/* 455*/    result = 29 * result + this.minStartIndex;
/* 456*/    result = 29 * result + this.maxStartIndex;
/* 457*/    result = 29 * result + this.minMiddleIndex;
/* 458*/    result = 29 * result + this.maxMiddleIndex;
/* 459*/    result = 29 * result + this.minEndIndex;
/* 460*/    result = 29 * result + this.maxEndIndex;
/* 461*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/* 480*/    Object clone = createCopy(0, getItemCount() - 1);
/* 481*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public TimePeriodValues createCopy(int start, int end) throws CloneNotSupportedException {
/* 498*/    TimePeriodValues copy = (TimePeriodValues)super.clone();
/* 500*/    copy.data = new ArrayList();
/* 501*/    if (this.data.size() > 0)
/* 502*/      for (int index = start; index <= end; index++) {
/* 503*/        TimePeriodValue item = this.data.get(index);
/* 504*/        TimePeriodValue clone = (TimePeriodValue)item.clone();
/*   0*/        try {
/* 506*/          copy.add(clone);
/* 508*/        } catch (SeriesException e) {
/* 509*/          System.err.println("Failed to add cloned item.");
/*   0*/        } 
/*   0*/      }  
/* 513*/    return copy;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinStartIndex() {
/* 523*/    return this.minStartIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxStartIndex() {
/* 532*/    return this.maxStartIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinMiddleIndex() {
/* 542*/    return this.minMiddleIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxMiddleIndex() {
/* 552*/    return this.maxEndIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinEndIndex() {
/* 561*/    return this.minEndIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaxEndIndex() {
/* 570*/    return this.maxEndIndex;
/*   0*/  }
/*   0*/}
