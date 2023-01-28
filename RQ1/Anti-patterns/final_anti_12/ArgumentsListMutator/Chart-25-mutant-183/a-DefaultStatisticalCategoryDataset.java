/*   0*/package org.jfree.data.statistics;
/*   0*/
/*   0*/import java.util.List;
/*   0*/import org.jfree.data.KeyedObjects2D;
/*   0*/import org.jfree.data.Range;
/*   0*/import org.jfree.data.RangeInfo;
/*   0*/import org.jfree.data.general.AbstractDataset;
/*   0*/
/*   0*/public class DefaultStatisticalCategoryDataset extends AbstractDataset implements StatisticalCategoryDataset, RangeInfo {
/*  91*/  private KeyedObjects2D data = new KeyedObjects2D();
/*   0*/  
/*  92*/  private double minimumRangeValue = Double.NaN;
/*   0*/  
/*  93*/  private double maximumRangeValue = Double.NaN;
/*   0*/  
/*  94*/  private double minimumRangeValueIncStdDev = Double.NaN;
/*   0*/  
/*  95*/  private double maximumRangeValueIncStdDev = Double.NaN;
/*   0*/  
/*   0*/  public Number getMeanValue(int row, int column) {
/* 107*/    Number result = null;
/* 108*/    MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(row, column);
/* 110*/    if (masd != null)
/* 111*/      result = masd.getMean(); 
/* 113*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Number getValue(int row, int column) {
/* 126*/    return getMeanValue(row, column);
/*   0*/  }
/*   0*/  
/*   0*/  public Number getValue(Comparable rowKey, Comparable columnKey) {
/* 139*/    return getMeanValue(rowKey, columnKey);
/*   0*/  }
/*   0*/  
/*   0*/  public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
/* 151*/    Number result = null;
/* 152*/    MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(rowKey, columnKey);
/* 154*/    if (masd != null)
/* 155*/      result = masd.getMean(); 
/* 157*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Number getStdDevValue(int row, int column) {
/* 169*/    Number result = null;
/* 170*/    MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(row, column);
/* 172*/    if (masd != null)
/* 173*/      result = masd.getStandardDeviation(); 
/* 175*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Number getStdDevValue(Comparable rowKey, Comparable columnKey) {
/* 187*/    Number result = null;
/* 188*/    MeanAndStandardDeviation masd = (MeanAndStandardDeviation)this.data.getObject(rowKey, columnKey);
/* 190*/    if (masd != null)
/* 191*/      result = masd.getStandardDeviation(); 
/* 193*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getColumnIndex(Comparable key) {
/* 204*/    return this.data.getColumnIndex(key);
/*   0*/  }
/*   0*/  
/*   0*/  public Comparable getColumnKey(int column) {
/* 215*/    return this.data.getColumnKey(column);
/*   0*/  }
/*   0*/  
/*   0*/  public List getColumnKeys() {
/* 224*/    return this.data.getColumnKeys();
/*   0*/  }
/*   0*/  
/*   0*/  public int getRowIndex(Comparable key) {
/* 235*/    return this.data.getRowIndex(key);
/*   0*/  }
/*   0*/  
/*   0*/  public Comparable getRowKey(int row) {
/* 246*/    return this.data.getRowKey(row);
/*   0*/  }
/*   0*/  
/*   0*/  public List getRowKeys() {
/* 255*/    return this.data.getRowKeys();
/*   0*/  }
/*   0*/  
/*   0*/  public int getRowCount() {
/* 264*/    return this.data.getRowCount();
/*   0*/  }
/*   0*/  
/*   0*/  public int getColumnCount() {
/* 273*/    return this.data.getColumnCount();
/*   0*/  }
/*   0*/  
/*   0*/  public void add(double mean, double standardDeviation, Comparable rowKey, Comparable columnKey) {
/* 286*/    add(new Double(mean), new Double(standardDeviation), rowKey, columnKey);
/*   0*/  }
/*   0*/  
/*   0*/  public void add(Number mean, Number standardDeviation, Comparable rowKey, Comparable columnKey) {
/* 299*/    MeanAndStandardDeviation item = new MeanAndStandardDeviation(mean, standardDeviation);
/* 301*/    this.data.addObject(item, rowKey, columnKey);
/* 302*/    double m = 0.0D;
/* 303*/    double sd = 0.0D;
/* 304*/    if (mean != null)
/* 305*/      m = mean.doubleValue(); 
/* 307*/    if (standardDeviation != null)
/* 308*/      sd = standardDeviation.doubleValue(); 
/* 311*/    if (!Double.isNaN(m) && (
/* 312*/      Double.isNaN(this.maximumRangeValue) || m > this.maximumRangeValue))
/* 314*/      this.maximumRangeValue = m; 
/* 318*/    if (!Double.isNaN(m + sd) && (
/* 319*/      Double.isNaN(this.maximumRangeValueIncStdDev) || m + sd > this.maximumRangeValueIncStdDev))
/* 321*/      this.maximumRangeValueIncStdDev = m + sd; 
/* 325*/    if (!Double.isNaN(m) && (
/* 326*/      Double.isNaN(this.minimumRangeValue) || m < this.minimumRangeValue))
/* 328*/      this.minimumRangeValue = m; 
/* 332*/    if (!Double.isNaN(m - sd) && (
/* 333*/      Double.isNaN(this.minimumRangeValueIncStdDev) || m - sd < this.minimumRangeValueIncStdDev))
/* 335*/      this.minimumRangeValueIncStdDev = m - sd; 
/* 339*/    fireDatasetChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public double getRangeLowerBound(boolean includeInterval) {
/* 352*/    return this.minimumRangeValue;
/*   0*/  }
/*   0*/  
/*   0*/  public double getRangeUpperBound(boolean includeInterval) {
/* 365*/    return this.maximumRangeValue;
/*   0*/  }
/*   0*/  
/*   0*/  public Range getRangeBounds(boolean includeInterval) {
/* 377*/    Range result = null;
/* 378*/    if (includeInterval) {
/* 379*/      if (!Double.isNaN(this.minimumRangeValueIncStdDev) && !Double.isNaN(this.maximumRangeValueIncStdDev))
/* 381*/        result = new Range(this.minimumRangeValueIncStdDev, this.maximumRangeValueIncStdDev); 
/* 385*/    } else if (!Double.isNaN(this.minimumRangeValue) && !Double.isNaN(this.maximumRangeValue)) {
/* 387*/      result = new Range(this.minimumRangeValue, this.maximumRangeValue);
/*   0*/    } 
/* 389*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 400*/    if (obj == this)
/* 401*/      return true; 
/* 403*/    if (!(obj instanceof DefaultStatisticalCategoryDataset))
/* 404*/      return false; 
/* 406*/    DefaultStatisticalCategoryDataset that = (DefaultStatisticalCategoryDataset)obj;
/* 408*/    if (!this.data.equals(that.data))
/* 409*/      return false; 
/* 411*/    return true;
/*   0*/  }
/*   0*/}
