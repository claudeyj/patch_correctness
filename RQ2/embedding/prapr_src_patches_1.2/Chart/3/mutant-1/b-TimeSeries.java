/*   0*/package org.jfree.data.time;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.InvocationTargetException;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Date;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.TimeZone;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.data.general.Series;
/*   0*/import org.jfree.data.general.SeriesException;
/*   0*/
/*   0*/public class TimeSeries extends Series implements Cloneable, Serializable {
/*   0*/  private static final long serialVersionUID = -5032960206869675528L;
/*   0*/  
/*   0*/  protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";
/*   0*/  
/*   0*/  protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";
/*   0*/  
/*   0*/  private String domain;
/*   0*/  
/*   0*/  private String range;
/*   0*/  
/*   0*/  protected Class timePeriodClass;
/*   0*/  
/*   0*/  protected List data;
/*   0*/  
/*   0*/  private int maximumItemCount;
/*   0*/  
/*   0*/  private long maximumItemAge;
/*   0*/  
/*   0*/  private double minY;
/*   0*/  
/*   0*/  private double maxY;
/*   0*/  
/*   0*/  public TimeSeries(Comparable name) {
/* 169*/    this(name, "Time", "Value");
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeries(Comparable name, String domain, String range) {
/* 186*/    super(name);
/* 187*/    this.domain = domain;
/* 188*/    this.range = range;
/* 189*/    this.timePeriodClass = null;
/* 190*/    this.data = new ArrayList();
/* 191*/    this.maximumItemCount = Integer.MAX_VALUE;
/* 192*/    this.maximumItemAge = Long.MAX_VALUE;
/* 193*/    this.minY = Double.NaN;
/* 194*/    this.maxY = Double.NaN;
/*   0*/  }
/*   0*/  
/*   0*/  public String getDomainDescription() {
/* 205*/    return this.domain;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainDescription(String description) {
/* 218*/    String old = this.domain;
/* 219*/    this.domain = description;
/* 220*/    firePropertyChange("Domain", old, description);
/*   0*/  }
/*   0*/  
/*   0*/  public String getRangeDescription() {
/* 231*/    return this.range;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeDescription(String description) {
/* 243*/    String old = this.range;
/* 244*/    this.range = description;
/* 245*/    firePropertyChange("Range", old, description);
/*   0*/  }
/*   0*/  
/*   0*/  public int getItemCount() {
/* 254*/    return this.data.size();
/*   0*/  }
/*   0*/  
/*   0*/  public List getItems() {
/* 265*/    return Collections.unmodifiableList(this.data);
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximumItemCount() {
/* 277*/    return this.maximumItemCount;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaximumItemCount(int maximum) {
/* 292*/    if (maximum < 0)
/* 293*/      throw new IllegalArgumentException("Negative 'maximum' argument."); 
/* 295*/    this.maximumItemCount = maximum;
/* 296*/    int count = this.data.size();
/* 297*/    if (count > maximum)
/* 298*/      delete(0, count - maximum - 1); 
/*   0*/  }
/*   0*/  
/*   0*/  public long getMaximumItemAge() {
/* 310*/    return this.maximumItemAge;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaximumItemAge(long periods) {
/* 326*/    if (periods < 0L)
/* 327*/      throw new IllegalArgumentException("Negative 'periods' argument."); 
/* 329*/    this.maximumItemAge = periods;
/* 330*/    removeAgedItems(true);
/*   0*/  }
/*   0*/  
/*   0*/  public double getMinY() {
/* 345*/    return this.minY;
/*   0*/  }
/*   0*/  
/*   0*/  public double getMaxY() {
/* 360*/    return this.maxY;
/*   0*/  }
/*   0*/  
/*   0*/  public Class getTimePeriodClass() {
/* 374*/    return this.timePeriodClass;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeriesDataItem getDataItem(int index) {
/* 389*/    TimeSeriesDataItem item = this.data.get(index);
/* 390*/    return (TimeSeriesDataItem)item.clone();
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeriesDataItem getDataItem(RegularTimePeriod period) {
/* 406*/    int index = getIndex(period);
/* 407*/    if (index >= 0)
/* 408*/      return getDataItem(index); 
/* 411*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  TimeSeriesDataItem getRawDataItem(int index) {
/* 429*/    return this.data.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  TimeSeriesDataItem getRawDataItem(RegularTimePeriod period) {
/* 446*/    int index = getIndex(period);
/* 447*/    if (index >= 0)
/* 448*/      return this.data.get(index); 
/* 451*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public RegularTimePeriod getTimePeriod(int index) {
/* 463*/    return getRawDataItem(index).getPeriod();
/*   0*/  }
/*   0*/  
/*   0*/  public RegularTimePeriod getNextTimePeriod() {
/* 473*/    RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
/* 474*/    return last.next();
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getTimePeriods() {
/* 483*/    Collection<RegularTimePeriod> result = new ArrayList();
/* 484*/    for (int i = 0; i < getItemCount(); i++)
/* 485*/      result.add(getTimePeriod(i)); 
/* 487*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getTimePeriodsUniqueToOtherSeries(TimeSeries series) {
/* 499*/    Collection<RegularTimePeriod> result = new ArrayList();
/* 500*/    for (int i = 0; i < series.getItemCount(); i++) {
/* 501*/      RegularTimePeriod period = series.getTimePeriod(i);
/* 502*/      int index = getIndex(period);
/* 503*/      if (index < 0)
/* 504*/        result.add(period); 
/*   0*/    } 
/* 507*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndex(RegularTimePeriod period) {
/* 519*/    if (period == null)
/* 520*/      throw new IllegalArgumentException("Null 'period' argument."); 
/* 522*/    TimeSeriesDataItem dummy = new TimeSeriesDataItem(period, -2.147483648E9D);
/* 524*/    return Collections.binarySearch(this.data, dummy);
/*   0*/  }
/*   0*/  
/*   0*/  public Number getValue(int index) {
/* 535*/    return getRawDataItem(index).getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public Number getValue(RegularTimePeriod period) {
/* 547*/    int index = getIndex(period);
/* 548*/    if (index >= 0)
/* 549*/      return getValue(index); 
/* 552*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void add(TimeSeriesDataItem item) {
/* 564*/    add(item, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(TimeSeriesDataItem item, boolean notify) {
/* 576*/    if (item == null)
/* 577*/      throw new IllegalArgumentException("Null 'item' argument."); 
/* 579*/    item = (TimeSeriesDataItem)item.clone();
/* 580*/    Class<?> c = item.getPeriod().getClass();
/* 581*/    if (this.timePeriodClass == null) {
/* 582*/      this.timePeriodClass = c;
/* 584*/    } else if (!this.timePeriodClass.equals(c)) {
/* 585*/      StringBuffer b = new StringBuffer();
/* 586*/      b.append("You are trying to add data where the time period class ");
/* 587*/      b.append("is ");
/* 588*/      b.append(item.getPeriod().getClass().getName());
/* 589*/      b.append(", but the TimeSeries is expecting an instance of ");
/* 590*/      b.append(this.timePeriodClass.getName());
/* 591*/      b.append(".");
/* 592*/      throw new SeriesException(b.toString());
/*   0*/    } 
/*   0*/    boolean added = false;
/* 597*/    int count = getItemCount();
/* 598*/    if (count == 0) {
/* 599*/      this.data.add(item);
/* 600*/      added = true;
/*   0*/    } else {
/* 603*/      RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
/* 604*/      if (item.getPeriod().compareTo((T)last) > 0) {
/* 605*/        this.data.add(item);
/* 606*/        added = true;
/*   0*/      } else {
/* 609*/        int index = Collections.binarySearch(this.data, item);
/* 610*/        if (index < 0) {
/* 611*/          this.data.add(-index - 1, item);
/* 612*/          added = true;
/*   0*/        } else {
/* 615*/          StringBuffer b = new StringBuffer();
/* 616*/          b.append("You are attempting to add an observation for ");
/* 617*/          b.append("the time period ");
/* 618*/          b.append(item.getPeriod().toString());
/* 619*/          b.append(" but the series already contains an observation");
/* 620*/          b.append(" for that time period. Duplicates are not ");
/* 621*/          b.append("permitted.  Try using the addOrUpdate() method.");
/* 622*/          throw new SeriesException(b.toString());
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 626*/    if (added) {
/* 627*/      updateBoundsForAddedItem(item);
/* 629*/      if (getItemCount() > this.maximumItemCount) {
/* 630*/        TimeSeriesDataItem d = this.data.remove(0);
/* 631*/        updateBoundsForRemovedItem(d);
/*   0*/      } 
/* 634*/      removeAgedItems(false);
/* 637*/      if (notify)
/* 638*/        findBoundsByIteration(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void add(RegularTimePeriod period, double value) {
/* 653*/    add(period, value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(RegularTimePeriod period, double value, boolean notify) {
/* 666*/    TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
/* 667*/    add(item, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(RegularTimePeriod period, Number value) {
/* 680*/    add(period, value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(RegularTimePeriod period, Number value, boolean notify) {
/* 693*/    TimeSeriesDataItem item = new TimeSeriesDataItem(period, value);
/* 694*/    add(item, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void update(RegularTimePeriod period, Number value) {
/* 705*/    TimeSeriesDataItem temp = new TimeSeriesDataItem(period, value);
/* 706*/    int index = Collections.binarySearch(this.data, temp);
/* 707*/    if (index < 0)
/* 708*/      throw new SeriesException("There is no existing value for the specified 'period'."); 
/* 711*/    update(index, value);
/*   0*/  }
/*   0*/  
/*   0*/  public void update(int index, Number value) {
/* 721*/    TimeSeriesDataItem item = this.data.get(index);
/*   0*/    boolean iterate = false;
/* 723*/    Number oldYN = item.getValue();
/* 724*/    if (oldYN != null) {
/* 725*/      double oldY = oldYN.doubleValue();
/* 726*/      if (!Double.isNaN(oldY))
/* 727*/        iterate = (oldY <= this.minY || oldY >= this.maxY); 
/*   0*/    } 
/* 730*/    item.setValue(value);
/* 731*/    if (iterate) {
/* 732*/      findBoundsByIteration();
/* 734*/    } else if (value != null) {
/* 735*/      double yy = value.doubleValue();
/* 736*/      this.minY = minIgnoreNaN(this.minY, yy);
/* 737*/      this.maxY = maxIgnoreNaN(this.maxY, yy);
/*   0*/    } 
/* 739*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeries addAndOrUpdate(TimeSeries series) {
/* 751*/    TimeSeries overwritten = new TimeSeries("Overwritten values from: " + getKey());
/* 753*/    for (int i = 0; i < series.getItemCount(); i++) {
/* 754*/      TimeSeriesDataItem item = series.getRawDataItem(i);
/* 755*/      TimeSeriesDataItem oldItem = addOrUpdate(item.getPeriod(), item.getValue());
/* 757*/      if (oldItem != null)
/* 758*/        overwritten.add(oldItem); 
/*   0*/    } 
/* 761*/    return overwritten;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, double value) {
/* 777*/    return addOrUpdate(period, new Double(value));
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeriesDataItem addOrUpdate(RegularTimePeriod period, Number value) {
/* 793*/    return addOrUpdate(new TimeSeriesDataItem(period, value));
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeriesDataItem addOrUpdate(TimeSeriesDataItem item) {
/* 809*/    if (item == null)
/* 810*/      throw new IllegalArgumentException("Null 'period' argument."); 
/* 812*/    Class<?> periodClass = item.getPeriod().getClass();
/* 813*/    if (this.timePeriodClass == null) {
/* 814*/      this.timePeriodClass = periodClass;
/* 816*/    } else if (!this.timePeriodClass.equals(periodClass)) {
/* 817*/      String msg = "You are trying to add data where the time period class is " + periodClass.getName() + ", but the TimeSeries is expecting an instance of " + this.timePeriodClass.getName() + ".";
/* 821*/      throw new SeriesException(msg);
/*   0*/    } 
/* 823*/    TimeSeriesDataItem overwritten = null;
/* 824*/    int index = Collections.binarySearch(this.data, item);
/* 825*/    if (index >= 0) {
/* 826*/      TimeSeriesDataItem existing = this.data.get(index);
/* 828*/      overwritten = (TimeSeriesDataItem)existing.clone();
/*   0*/      boolean iterate = false;
/* 832*/      Number oldYN = existing.getValue();
/* 833*/      double oldY = (oldYN != null) ? oldYN.doubleValue() : Double.NaN;
/* 834*/      if (!Double.isNaN(oldY))
/* 835*/        iterate = (oldY <= this.minY || oldY >= this.maxY); 
/* 837*/      existing.setValue(item.getValue());
/* 838*/      if (iterate) {
/* 839*/        findBoundsByIteration();
/* 841*/      } else if (item.getValue() != null) {
/* 842*/        double yy = item.getValue().doubleValue();
/* 843*/        this.minY = minIgnoreNaN(this.minY, yy);
/* 844*/        this.maxY = minIgnoreNaN(this.maxY, yy);
/*   0*/      } 
/*   0*/    } else {
/* 848*/      item = (TimeSeriesDataItem)item.clone();
/* 849*/      this.data.add(-index - 1, item);
/* 850*/      updateBoundsForAddedItem(item);
/* 853*/      if (getItemCount() > this.maximumItemCount) {
/* 854*/        TimeSeriesDataItem d = this.data.remove(0);
/* 855*/        updateBoundsForRemovedItem(d);
/*   0*/      } 
/*   0*/    } 
/* 858*/    removeAgedItems(false);
/* 861*/    fireSeriesChanged();
/* 862*/    return overwritten;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeAgedItems(boolean notify) {
/* 877*/    if (getItemCount() > 1) {
/* 878*/      long latest = getTimePeriod(getItemCount() - 1).getSerialIndex();
/*   0*/      boolean removed = false;
/* 880*/      while (latest - getTimePeriod(0).getSerialIndex() > this.maximumItemAge) {
/* 882*/        this.data.remove(0);
/* 883*/        removed = true;
/*   0*/      } 
/* 885*/      if (removed) {
/* 886*/        findBoundsByIteration();
/* 887*/        if (notify)
/* 888*/          fireSeriesChanged(); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeAgedItems(long latest, boolean notify) {
/* 905*/    if (this.data.isEmpty())
/*   0*/      return; 
/* 909*/    long index = Long.MAX_VALUE;
/*   0*/    try {
/* 911*/      Method m = RegularTimePeriod.class.getDeclaredMethod("createInstance", new Class<?>[] { Class.class, Date.class, TimeZone.class });
/* 914*/      RegularTimePeriod newest = (RegularTimePeriod)m.invoke(this.timePeriodClass, new Object[] { this.timePeriodClass, new Date(latest), TimeZone.getDefault() });
/* 917*/      index = newest.getSerialIndex();
/* 919*/    } catch (NoSuchMethodException e) {
/* 920*/      e.printStackTrace();
/* 922*/    } catch (IllegalAccessException e) {
/* 923*/      e.printStackTrace();
/* 925*/    } catch (InvocationTargetException e) {
/* 926*/      e.printStackTrace();
/*   0*/    } 
/*   0*/    boolean removed = false;
/* 932*/    while (getItemCount() > 0 && index - getTimePeriod(0).getSerialIndex() > this.maximumItemAge) {
/* 934*/      this.data.remove(0);
/* 935*/      removed = true;
/*   0*/    } 
/* 937*/    if (removed) {
/* 938*/      findBoundsByIteration();
/* 939*/      if (notify)
/* 940*/        fireSeriesChanged(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void clear() {
/* 950*/    if (this.data.size() > 0) {
/* 951*/      this.data.clear();
/* 952*/      this.timePeriodClass = null;
/* 953*/      this.minY = Double.NaN;
/* 954*/      this.maxY = Double.NaN;
/* 955*/      fireSeriesChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void delete(RegularTimePeriod period) {
/* 968*/    int index = getIndex(period);
/* 969*/    if (index >= 0) {
/* 970*/      TimeSeriesDataItem item = this.data.remove(index);
/* 972*/      updateBoundsForRemovedItem(item);
/* 973*/      if (this.data.isEmpty())
/* 974*/        this.timePeriodClass = null; 
/* 976*/      fireSeriesChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void delete(int start, int end) {
/* 987*/    delete(start, end, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void delete(int start, int end, boolean notify) {
/*1000*/    if (end < start)
/*1001*/      throw new IllegalArgumentException("Requires start <= end."); 
/*1003*/    for (int i = 0; i <= end - start; i++)
/*1004*/      this.data.remove(start); 
/*1006*/    findBoundsByIteration();
/*1007*/    if (this.data.isEmpty())
/*1008*/      this.timePeriodClass = null; 
/*1010*/    if (notify)
/*1011*/      fireSeriesChanged(); 
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1031*/    TimeSeries clone = (TimeSeries)super.clone();
/*1032*/    clone.data = (List)ObjectUtilities.deepClone(this.data);
/*1033*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeries createCopy(int start, int end) throws CloneNotSupportedException {
/*1050*/    if (start < 0)
/*1051*/      throw new IllegalArgumentException("Requires start >= 0."); 
/*1053*/    if (end < start)
/*1054*/      throw new IllegalArgumentException("Requires start <= end."); 
/*1056*/    TimeSeries copy = (TimeSeries)super.clone();
/*1057*/    copy.data = new ArrayList();
/*1058*/    if (this.data.size() > 0)
/*1059*/      for (int index = start; index <= end; index++) {
/*1060*/        TimeSeriesDataItem item = this.data.get(index);
/*1062*/        TimeSeriesDataItem clone = (TimeSeriesDataItem)item.clone();
/*   0*/        try {
/*1064*/          copy.add(clone);
/*1066*/        } catch (SeriesException e) {
/*1067*/          e.printStackTrace();
/*   0*/        } 
/*   0*/      }  
/*1071*/    return copy;
/*   0*/  }
/*   0*/  
/*   0*/  public TimeSeries createCopy(RegularTimePeriod start, RegularTimePeriod end) throws CloneNotSupportedException {
/*1091*/    if (start == null)
/*1092*/      throw new IllegalArgumentException("Null 'start' argument."); 
/*1094*/    if (end == null)
/*1095*/      throw new IllegalArgumentException("Null 'end' argument."); 
/*1097*/    if (start.compareTo((T)end) > 0)
/*1098*/      throw new IllegalArgumentException("Requires start on or before end."); 
/*   0*/    boolean emptyRange = false;
/*1102*/    int startIndex = getIndex(start);
/*1103*/    if (startIndex < 0) {
/*1104*/      startIndex = -(startIndex + 1);
/*1105*/      if (startIndex == this.data.size())
/*1106*/        emptyRange = true; 
/*   0*/    } 
/*1109*/    int endIndex = getIndex(end);
/*1110*/    if (endIndex < 0) {
/*1111*/      endIndex = -(endIndex + 1);
/*1112*/      endIndex--;
/*   0*/    } 
/*1114*/    if (endIndex < 0 || endIndex < startIndex)
/*1115*/      emptyRange = true; 
/*1117*/    if (emptyRange) {
/*1118*/      TimeSeries copy = (TimeSeries)super.clone();
/*1119*/      copy.data = new ArrayList();
/*1120*/      return copy;
/*   0*/    } 
/*1123*/    return createCopy(startIndex, endIndex);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*1136*/    if (obj == this)
/*1137*/      return true; 
/*1139*/    if (!(obj instanceof TimeSeries))
/*1140*/      return false; 
/*1142*/    TimeSeries that = (TimeSeries)obj;
/*1143*/    if (!ObjectUtilities.equal(getDomainDescription(), that.getDomainDescription()))
/*1145*/      return false; 
/*1147*/    if (!ObjectUtilities.equal(getRangeDescription(), that.getRangeDescription()))
/*1149*/      return false; 
/*1151*/    if (!ObjectUtilities.equal(this.timePeriodClass, that.timePeriodClass))
/*1153*/      return false; 
/*1155*/    if (getMaximumItemAge() != that.getMaximumItemAge())
/*1156*/      return false; 
/*1158*/    if (getMaximumItemCount() != that.getMaximumItemCount())
/*1159*/      return false; 
/*1161*/    int count = getItemCount();
/*1162*/    if (count != that.getItemCount())
/*1163*/      return false; 
/*1165*/    if (!ObjectUtilities.equal(this.data, that.data))
/*1166*/      return false; 
/*1168*/    return super.equals(obj);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1177*/    int result = super.hashCode();
/*1178*/    result = 29 * result + ((this.domain != null) ? this.domain.hashCode() : 0);
/*1180*/    result = 29 * result + ((this.range != null) ? this.range.hashCode() : 0);
/*1181*/    result = 29 * result + ((this.timePeriodClass != null) ? this.timePeriodClass.hashCode() : 0);
/*1185*/    int count = getItemCount();
/*1186*/    if (count > 0) {
/*1187*/      TimeSeriesDataItem item = getRawDataItem(0);
/*1188*/      result = 29 * result + item.hashCode();
/*   0*/    } 
/*1190*/    if (count > 1) {
/*1191*/      TimeSeriesDataItem item = getRawDataItem(count - 1);
/*1192*/      result = 29 * result + item.hashCode();
/*   0*/    } 
/*1194*/    if (count > 2) {
/*1195*/      TimeSeriesDataItem item = getRawDataItem(count / 2);
/*1196*/      result = 29 * result + item.hashCode();
/*   0*/    } 
/*1198*/    result = 29 * result + this.maximumItemCount;
/*1199*/    result = 29 * result + (int)this.maximumItemAge;
/*1200*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private void updateBoundsForAddedItem(TimeSeriesDataItem item) {
/*1211*/    Number yN = item.getValue();
/*1212*/    if (item.getValue() != null) {
/*1213*/      double y = yN.doubleValue();
/*1214*/      this.minY = minIgnoreNaN(this.minY, y);
/*1215*/      this.maxY = maxIgnoreNaN(this.maxY, y);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void updateBoundsForRemovedItem(TimeSeriesDataItem item) {
/*1228*/    Number yN = item.getValue();
/*1229*/    if (yN != null) {
/*1230*/      double y = yN.doubleValue();
/*1231*/      if (!Double.isNaN(y) && (
/*1232*/        y <= this.minY || y >= this.maxY))
/*1233*/        findBoundsByIteration(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void findBoundsByIteration() {
/*1246*/    this.minY = Double.NaN;
/*1247*/    this.maxY = Double.NaN;
/*1248*/    Iterator<TimeSeriesDataItem> iterator = this.data.iterator();
/*1249*/    while (iterator.hasNext()) {
/*1250*/      TimeSeriesDataItem item = iterator.next();
/*1251*/      updateBoundsForAddedItem(item);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private double minIgnoreNaN(double a, double b) {
/*1265*/    if (Double.isNaN(a))
/*1266*/      return b; 
/*1269*/    if (Double.isNaN(b))
/*1270*/      return a; 
/*1273*/    return Math.min(a, b);
/*   0*/  }
/*   0*/  
/*   0*/  private double maxIgnoreNaN(double a, double b) {
/*1288*/    if (Double.isNaN(a))
/*1289*/      return b; 
/*1292*/    if (Double.isNaN(b))
/*1293*/      return a; 
/*1296*/    return Math.max(a, b);
/*   0*/  }
/*   0*/}
