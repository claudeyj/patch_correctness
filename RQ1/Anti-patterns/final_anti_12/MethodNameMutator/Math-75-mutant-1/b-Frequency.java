/*   0*/package org.apache.commons.math.stat;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.text.NumberFormat;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.TreeMap;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/
/*   0*/public class Frequency implements Serializable {
/*   0*/  private static final long serialVersionUID = -3845586908418844111L;
/*   0*/  
/*   0*/  private final TreeMap<Comparable<?>, Long> freqTable;
/*   0*/  
/*   0*/  public Frequency() {
/*  60*/    this.freqTable = new TreeMap<Comparable<?>, Long>();
/*   0*/  }
/*   0*/  
/*   0*/  public Frequency(Comparator<?> comparator) {
/*  70*/    this.freqTable = (TreeMap)new TreeMap<Object, Long>(comparator);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*  81*/    NumberFormat nf = NumberFormat.getPercentInstance();
/*  82*/    StringBuffer outBuffer = new StringBuffer();
/*  83*/    outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
/*  84*/    Iterator<Comparable<?>> iter = this.freqTable.keySet().iterator();
/*  85*/    while (iter.hasNext()) {
/*  86*/      Comparable<?> value = iter.next();
/*  87*/      outBuffer.append(value);
/*  88*/      outBuffer.append('\t');
/*  89*/      outBuffer.append(getCount(value));
/*  90*/      outBuffer.append('\t');
/*  91*/      outBuffer.append(nf.format(getPct(value)));
/*  92*/      outBuffer.append('\t');
/*  93*/      outBuffer.append(nf.format(getCumPct(value)));
/*  94*/      outBuffer.append('\n');
/*   0*/    } 
/*  96*/    return outBuffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void addValue(Object v) {
/* 113*/    if (v instanceof Comparable) {
/* 114*/      addValue((Comparable)v);
/*   0*/    } else {
/* 116*/      throw MathRuntimeException.createIllegalArgumentException("class ({0}) does not implement Comparable", new Object[] { v.getClass().getName() });
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addValue(Comparable<?> v) {
/* 133*/    Comparable<?> obj = v;
/* 134*/    if (v instanceof Integer)
/* 135*/      obj = ((Integer)v).longValue(); 
/*   0*/    try {
/* 138*/      Long count = this.freqTable.get(obj);
/* 139*/      if (count == null) {
/* 140*/        this.freqTable.put(obj, 1L);
/*   0*/      } else {
/* 142*/        this.freqTable.put(obj, count + 1L);
/*   0*/      } 
/* 144*/    } catch (ClassCastException ex) {
/* 146*/      throw MathRuntimeException.createIllegalArgumentException("instance of class {0} not comparable to existing values", new Object[] { v.getClass().getName() });
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addValue(int v) {
/* 158*/    addValue((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public void addValue(Integer v) {
/* 168*/    addValue((Comparable<?>)v.longValue());
/*   0*/  }
/*   0*/  
/*   0*/  public void addValue(long v) {
/* 177*/    addValue((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public void addValue(char v) {
/* 186*/    addValue((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public void clear() {
/* 191*/    this.freqTable.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<Comparable<?>> valuesIterator() {
/* 204*/    return this.freqTable.keySet().iterator();
/*   0*/  }
/*   0*/  
/*   0*/  public long getSumFreq() {
/* 215*/    long result = 0L;
/* 216*/    Iterator<Long> iterator = this.freqTable.values().iterator();
/* 217*/    while (iterator.hasNext())
/* 218*/      result += (Long)iterator.next(); 
/* 220*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public long getCount(Object v) {
/* 233*/    return getCount((Comparable)v);
/*   0*/  }
/*   0*/  
/*   0*/  public long getCount(Comparable<?> v) {
/* 244*/    if (v instanceof Integer)
/* 245*/      return getCount(((Integer)v).longValue()); 
/* 247*/    long result = 0L;
/*   0*/    try {
/* 249*/      Long count = this.freqTable.get(v);
/* 250*/      if (count != null)
/* 251*/        result = count; 
/* 253*/    } catch (ClassCastException classCastException) {}
/* 256*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public long getCount(int v) {
/* 266*/    return getCount((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public long getCount(long v) {
/* 276*/    return getCount((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public long getCount(char v) {
/* 286*/    return getCount((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double getPct(Object v) {
/* 303*/    return getPct((Comparable)v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getPct(Comparable<?> v) {
/* 316*/    long sumFreq = getSumFreq();
/* 317*/    if (sumFreq == 0L)
/* 318*/      return Double.NaN; 
/* 320*/    return getCount(v) / sumFreq;
/*   0*/  }
/*   0*/  
/*   0*/  public double getPct(int v) {
/* 331*/    return getPct((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getPct(long v) {
/* 342*/    return getPct((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getPct(char v) {
/* 353*/    return getPct((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public long getCumFreq(Object v) {
/* 369*/    return getCumFreq((Comparable)v);
/*   0*/  }
/*   0*/  
/*   0*/  public long getCumFreq(Comparable<?> v) {
/* 382*/    if (getSumFreq() == 0L)
/* 383*/      return 0L; 
/* 385*/    if (v instanceof Integer)
/* 386*/      return getCumFreq(((Integer)v).longValue()); 
/* 388*/    Comparator<Comparable<?>> c = (Comparator)this.freqTable.comparator();
/* 389*/    if (c == null)
/* 390*/      c = new NaturalComparator(); 
/* 392*/    long result = 0L;
/*   0*/    try {
/* 395*/      Long value = this.freqTable.get(v);
/* 396*/      if (value != null)
/* 397*/        result = value; 
/* 399*/    } catch (ClassCastException ex) {
/* 400*/      return result;
/*   0*/    } 
/* 403*/    if (c.compare(v, this.freqTable.firstKey()) < 0)
/* 404*/      return 0L; 
/* 407*/    if (c.compare(v, this.freqTable.lastKey()) >= 0)
/* 408*/      return getSumFreq(); 
/* 411*/    Iterator<Comparable<?>> values = valuesIterator();
/* 412*/    while (values.hasNext()) {
/* 413*/      Comparable<?> nextValue = values.next();
/* 414*/      if (c.compare(v, nextValue) > 0) {
/* 415*/        result += getCount(nextValue);
/*   0*/        continue;
/*   0*/      } 
/* 417*/      return result;
/*   0*/    } 
/* 420*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public long getCumFreq(int v) {
/* 432*/    return getCumFreq((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public long getCumFreq(long v) {
/* 444*/    return getCumFreq((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public long getCumFreq(char v) {
/* 456*/    return getCumFreq((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public double getCumPct(Object v) {
/* 475*/    return getCumPct((Comparable)v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getCumPct(Comparable<?> v) {
/* 491*/    long sumFreq = getSumFreq();
/* 492*/    if (sumFreq == 0L)
/* 493*/      return Double.NaN; 
/* 495*/    return getCumFreq(v) / sumFreq;
/*   0*/  }
/*   0*/  
/*   0*/  public double getCumPct(int v) {
/* 508*/    return getCumPct((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getCumPct(long v) {
/* 521*/    return getCumPct((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getCumPct(char v) {
/* 534*/    return getCumPct((Comparable<?>)v);
/*   0*/  }
/*   0*/  
/*   0*/  private static class NaturalComparator<T extends Comparable<T>> implements Comparator<Comparable<T>>, Serializable {
/*   0*/    private static final long serialVersionUID = -3852193713161395148L;
/*   0*/    
/*   0*/    private NaturalComparator() {}
/*   0*/    
/*   0*/    public int compare(Comparable<T> o1, Comparable<T> o2) {
/* 561*/      return o1.compareTo((T)o2);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 568*/    int prime = 31;
/* 569*/    int result = 1;
/* 570*/    result = 31 * result + ((this.freqTable == null) ? 0 : this.freqTable.hashCode());
/* 572*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 578*/    if (this == obj)
/* 579*/      return true; 
/* 580*/    if (obj == null)
/* 581*/      return false; 
/* 582*/    if (!(obj instanceof Frequency))
/* 583*/      return false; 
/* 584*/    Frequency other = (Frequency)obj;
/* 585*/    if (this.freqTable == null) {
/* 586*/      if (other.freqTable != null)
/* 587*/        return false; 
/* 588*/    } else if (!this.freqTable.equals(other.freqTable)) {
/* 589*/      return false;
/*   0*/    } 
/* 590*/    return true;
/*   0*/  }
/*   0*/}
