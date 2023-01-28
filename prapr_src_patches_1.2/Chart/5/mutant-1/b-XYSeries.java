/*   0*/package org.jfree.data.xy;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collections;
/*   0*/import java.util.List;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.data.general.Series;
/*   0*/import org.jfree.data.general.SeriesException;
/*   0*/
/*   0*/public class XYSeries extends Series implements Cloneable, Serializable {
/*   0*/  static final long serialVersionUID = -5908509288197150436L;
/*   0*/  
/*   0*/  protected List data;
/*   0*/  
/* 109*/  private int maximumItemCount = Integer.MAX_VALUE;
/*   0*/  
/*   0*/  private boolean autoSort;
/*   0*/  
/*   0*/  private boolean allowDuplicateXValues;
/*   0*/  
/*   0*/  public XYSeries(Comparable key) {
/* 125*/    this(key, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public XYSeries(Comparable key, boolean autoSort) {
/* 137*/    this(key, autoSort, true);
/*   0*/  }
/*   0*/  
/*   0*/  public XYSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
/* 153*/    super(key);
/* 154*/    this.data = new ArrayList();
/* 155*/    this.autoSort = autoSort;
/* 156*/    this.allowDuplicateXValues = allowDuplicateXValues;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getAutoSort() {
/* 167*/    return this.autoSort;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getAllowDuplicateXValues() {
/* 177*/    return this.allowDuplicateXValues;
/*   0*/  }
/*   0*/  
/*   0*/  public int getItemCount() {
/* 186*/    return this.data.size();
/*   0*/  }
/*   0*/  
/*   0*/  public List getItems() {
/* 196*/    return Collections.unmodifiableList(this.data);
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximumItemCount() {
/* 207*/    return this.maximumItemCount;
/*   0*/  }
/*   0*/  
/*   0*/  public void setMaximumItemCount(int maximum) {
/* 225*/    this.maximumItemCount = maximum;
/*   0*/    boolean dataRemoved = false;
/* 227*/    while (this.data.size() > maximum) {
/* 228*/      this.data.remove(0);
/* 229*/      dataRemoved = true;
/*   0*/    } 
/* 231*/    if (dataRemoved)
/* 232*/      fireSeriesChanged(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void add(XYDataItem item) {
/* 244*/    add(item, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(double x, double y) {
/* 255*/    add(new Double(x), new Double(y), true);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(double x, double y, boolean notify) {
/* 269*/    add(new Double(x), new Double(y), notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(double x, Number y) {
/* 281*/    add(new Double(x), y);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(double x, Number y, boolean notify) {
/* 296*/    add(new Double(x), y, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(Number x, Number y) {
/* 315*/    add(x, y, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(Number x, Number y, boolean notify) {
/* 333*/    XYDataItem item = new XYDataItem(x, y);
/* 334*/    add(item, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(XYDataItem item, boolean notify) {
/* 348*/    if (item == null)
/* 349*/      throw new IllegalArgumentException("Null 'item' argument."); 
/* 352*/    if (this.autoSort) {
/* 353*/      int index = Collections.binarySearch(this.data, item);
/* 354*/      if (index < 0) {
/* 355*/        this.data.add(-index - 1, item);
/* 358*/      } else if (this.allowDuplicateXValues) {
/* 360*/        int size = this.data.size();
/* 361*/        while (index < size && item.compareTo(this.data.get(index)) == 0)
/* 363*/          index++; 
/* 365*/        if (index < this.data.size()) {
/* 366*/          this.data.add(index, item);
/*   0*/        } else {
/* 369*/          this.data.add(item);
/*   0*/        } 
/*   0*/      } else {
/* 373*/        throw new SeriesException("X-value already exists.");
/*   0*/      } 
/*   0*/    } else {
/* 378*/      if (!this.allowDuplicateXValues) {
/* 381*/        int index = indexOf(item.getX());
/* 382*/        if (index >= 0)
/* 383*/          throw new SeriesException("X-value already exists."); 
/*   0*/      } 
/* 386*/      this.data.add(item);
/*   0*/    } 
/* 388*/    if (getItemCount() > this.maximumItemCount)
/* 389*/      this.data.remove(0); 
/* 391*/    if (notify)
/* 392*/      fireSeriesChanged(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void delete(int start, int end) {
/* 404*/    for (int i = start; i <= end; i++)
/* 405*/      this.data.remove(start); 
/* 407*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataItem remove(int index) {
/* 419*/    XYDataItem result = this.data.remove(index);
/* 420*/    fireSeriesChanged();
/* 421*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataItem remove(Number x) {
/* 433*/    return remove(indexOf(x));
/*   0*/  }
/*   0*/  
/*   0*/  public void clear() {
/* 440*/    if (this.data.size() > 0) {
/* 441*/      this.data.clear();
/* 442*/      fireSeriesChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataItem getDataItem(int index) {
/* 454*/    return this.data.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public Number getX(int index) {
/* 465*/    return getDataItem(index).getX();
/*   0*/  }
/*   0*/  
/*   0*/  public Number getY(int index) {
/* 476*/    return getDataItem(index).getY();
/*   0*/  }
/*   0*/  
/*   0*/  public void updateByIndex(int index, Number y) {
/* 489*/    XYDataItem item = getDataItem(index);
/* 490*/    item.setY(y);
/* 491*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void update(Number x, Number y) {
/* 504*/    int index = indexOf(x);
/* 505*/    if (index < 0)
/* 506*/      throw new SeriesException("No observation for x = " + x); 
/* 509*/    XYDataItem item = getDataItem(index);
/* 510*/    item.setY(y);
/* 511*/    fireSeriesChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataItem addOrUpdate(double x, double y) {
/* 527*/    return addOrUpdate(new Double(x), new Double(y));
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataItem addOrUpdate(Number x, Number y) {
/* 541*/    if (x == null)
/* 542*/      throw new IllegalArgumentException("Null 'x' argument."); 
/* 546*/    XYDataItem overwritten = null;
/* 547*/    int index = indexOf(x);
/* 548*/    if (index >= 0 && !this.allowDuplicateXValues) {
/* 549*/      XYDataItem existing = this.data.get(index);
/*   0*/      try {
/* 551*/        overwritten = (XYDataItem)existing.clone();
/* 553*/      } catch (CloneNotSupportedException e) {
/* 554*/        throw new SeriesException("Couldn't clone XYDataItem!");
/*   0*/      } 
/* 556*/      existing.setY(y);
/*   0*/    } else {
/* 567*/      this.data.add(new XYDataItem(x, y));
/* 570*/      if (getItemCount() > this.maximumItemCount)
/* 571*/        this.data.remove(0); 
/*   0*/    } 
/* 574*/    fireSeriesChanged();
/* 575*/    return overwritten;
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(Number x) {
/* 589*/    if (this.autoSort)
/* 590*/      return Collections.binarySearch(this.data, new XYDataItem(x, null)); 
/* 593*/    for (int i = 0; i < this.data.size(); i++) {
/* 594*/      XYDataItem item = this.data.get(i);
/* 595*/      if (item.getX().equals(x))
/* 596*/        return i; 
/*   0*/    } 
/* 599*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public double[][] toArray() {
/* 611*/    int itemCount = getItemCount();
/* 612*/    double[][] result = new double[2][itemCount];
/* 613*/    for (int i = 0; i < itemCount; i++) {
/* 614*/      result[0][i] = getX(i).doubleValue();
/* 615*/      Number y = getY(i);
/* 616*/      if (y != null) {
/* 617*/        result[1][i] = y.doubleValue();
/*   0*/      } else {
/* 620*/        result[1][i] = Double.NaN;
/*   0*/      } 
/*   0*/    } 
/* 623*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/* 634*/    XYSeries clone = (XYSeries)super.clone();
/* 635*/    clone.data = (List)ObjectUtilities.deepClone(this.data);
/* 636*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public XYSeries createCopy(int start, int end) throws CloneNotSupportedException {
/* 652*/    XYSeries copy = (XYSeries)super.clone();
/* 653*/    copy.data = new ArrayList();
/* 654*/    if (this.data.size() > 0)
/* 655*/      for (int index = start; index <= end; index++) {
/* 656*/        XYDataItem item = this.data.get(index);
/* 657*/        XYDataItem clone = (XYDataItem)item.clone();
/*   0*/        try {
/* 659*/          copy.add(clone);
/* 661*/        } catch (SeriesException e) {
/* 662*/          System.err.println("Unable to add cloned data item.");
/*   0*/        } 
/*   0*/      }  
/* 666*/    return copy;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 679*/    if (obj == this)
/* 680*/      return true; 
/* 682*/    if (!(obj instanceof XYSeries))
/* 683*/      return false; 
/* 685*/    if (!super.equals(obj))
/* 686*/      return false; 
/* 688*/    XYSeries that = (XYSeries)obj;
/* 689*/    if (this.maximumItemCount != that.maximumItemCount)
/* 690*/      return false; 
/* 692*/    if (this.autoSort != that.autoSort)
/* 693*/      return false; 
/* 695*/    if (this.allowDuplicateXValues != that.allowDuplicateXValues)
/* 696*/      return false; 
/* 698*/    if (!ObjectUtilities.equal(this.data, that.data))
/* 699*/      return false; 
/* 701*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 710*/    int result = super.hashCode();
/* 713*/    int count = getItemCount();
/* 714*/    if (count > 0) {
/* 715*/      XYDataItem item = getDataItem(0);
/* 716*/      result = 29 * result + item.hashCode();
/*   0*/    } 
/* 718*/    if (count > 1) {
/* 719*/      XYDataItem item = getDataItem(count - 1);
/* 720*/      result = 29 * result + item.hashCode();
/*   0*/    } 
/* 722*/    if (count > 2) {
/* 723*/      XYDataItem item = getDataItem(count / 2);
/* 724*/      result = 29 * result + item.hashCode();
/*   0*/    } 
/* 726*/    result = 29 * result + this.maximumItemCount;
/* 727*/    result = 29 * result + (this.autoSort ? 1 : 0);
/* 728*/    result = 29 * result + (this.allowDuplicateXValues ? 1 : 0);
/* 729*/    return result;
/*   0*/  }
/*   0*/}
