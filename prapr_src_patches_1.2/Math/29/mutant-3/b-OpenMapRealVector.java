/*   0*/package org.apache.commons.math3.linear;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Iterator;
/*   0*/import org.apache.commons.math3.exception.MathArithmeticException;
/*   0*/import org.apache.commons.math3.exception.NotPositiveException;
/*   0*/import org.apache.commons.math3.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
/*   0*/
/*   0*/public class OpenMapRealVector extends SparseRealVector implements Serializable {
/*   0*/  public static final double DEFAULT_ZERO_TOLERANCE = 1.0E-12D;
/*   0*/  
/*   0*/  private static final long serialVersionUID = 8772222695580707260L;
/*   0*/  
/*   0*/  private final OpenIntToDoubleHashMap entries;
/*   0*/  
/*   0*/  private final int virtualSize;
/*   0*/  
/*   0*/  private final double epsilon;
/*   0*/  
/*   0*/  public OpenMapRealVector() {
/*  56*/    this(0, 1.0E-12D);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(int dimension) {
/*  65*/    this(dimension, 1.0E-12D);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(int dimension, double epsilon) {
/*  75*/    this.virtualSize = dimension;
/*  76*/    this.entries = new OpenIntToDoubleHashMap(0.0D);
/*  77*/    this.epsilon = epsilon;
/*   0*/  }
/*   0*/  
/*   0*/  protected OpenMapRealVector(OpenMapRealVector v, int resize) {
/*  87*/    this.virtualSize = v.getDimension() + resize;
/*  88*/    this.entries = new OpenIntToDoubleHashMap(v.entries);
/*  89*/    this.epsilon = v.epsilon;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(int dimension, int expectedSize) {
/*  99*/    this(dimension, expectedSize, 1.0E-12D);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(int dimension, int expectedSize, double epsilon) {
/* 111*/    this.virtualSize = dimension;
/* 112*/    this.entries = new OpenIntToDoubleHashMap(expectedSize, 0.0D);
/* 113*/    this.epsilon = epsilon;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(double[] values) {
/* 123*/    this(values, 1.0E-12D);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(double[] values, double epsilon) {
/* 134*/    this.virtualSize = values.length;
/* 135*/    this.entries = new OpenIntToDoubleHashMap(0.0D);
/* 136*/    this.epsilon = epsilon;
/* 137*/    for (int key = 0; key < values.length; key++) {
/* 138*/      double value = values[key];
/* 139*/      this;
/* 139*/      if (!false)
/* 140*/        this.entries.put(key, value); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(Double[] values) {
/* 152*/    this(values, 1.0E-12D);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(Double[] values, double epsilon) {
/* 163*/    this.virtualSize = values.length;
/* 164*/    this.entries = new OpenIntToDoubleHashMap(0.0D);
/* 165*/    this.epsilon = epsilon;
/* 166*/    for (int key = 0; key < values.length; key++) {
/* 167*/      double value = values[key];
/* 168*/      if (!isDefaultValue(value))
/* 169*/        this.entries.put(key, value); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(OpenMapRealVector v) {
/* 180*/    this.virtualSize = v.getDimension();
/* 181*/    this.entries = new OpenIntToDoubleHashMap(v.getEntries());
/* 182*/    this.epsilon = v.epsilon;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector(RealVector v) {
/* 191*/    this.virtualSize = v.getDimension();
/* 192*/    this.entries = new OpenIntToDoubleHashMap(0.0D);
/* 193*/    this.epsilon = 1.0E-12D;
/* 194*/    for (int key = 0; key < this.virtualSize; key++) {
/* 195*/      double value = v.getEntry(key);
/* 196*/      if (!isDefaultValue(value))
/* 197*/        this.entries.put(key, value); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private OpenIntToDoubleHashMap getEntries() {
/* 208*/    return this.entries;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isDefaultValue(double value) {
/* 220*/    return (FastMath.abs(value) < this.epsilon);
/*   0*/  }
/*   0*/  
/*   0*/  public RealVector add(RealVector v) {
/* 226*/    checkVectorDimensions(v.getDimension());
/* 227*/    if (v instanceof OpenMapRealVector)
/* 228*/      return add((OpenMapRealVector)v); 
/* 230*/    return super.add(v);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector add(OpenMapRealVector v) {
/* 244*/    checkVectorDimensions(v.getDimension());
/* 245*/    boolean copyThis = (this.entries.size() > v.entries.size());
/* 246*/    OpenMapRealVector res = copyThis ? copy() : v.copy();
/* 247*/    OpenIntToDoubleHashMap.Iterator iter = copyThis ? v.entries.iterator() : this.entries.iterator();
/* 248*/    OpenIntToDoubleHashMap randomAccess = copyThis ? this.entries : v.entries;
/* 249*/    while (iter.hasNext()) {
/* 250*/      iter.advance();
/* 251*/      int key = iter.key();
/* 252*/      if (randomAccess.containsKey(key)) {
/* 253*/        res.setEntry(key, randomAccess.get(key) + iter.value());
/*   0*/        continue;
/*   0*/      } 
/* 255*/      res.setEntry(key, iter.value());
/*   0*/    } 
/* 258*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector append(OpenMapRealVector v) {
/* 267*/    OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
/* 268*/    OpenIntToDoubleHashMap.Iterator iter = v.entries.iterator();
/* 269*/    while (iter.hasNext()) {
/* 270*/      iter.advance();
/* 271*/      res.setEntry(iter.key() + this.virtualSize, iter.value());
/*   0*/    } 
/* 273*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector append(RealVector v) {
/* 279*/    if (v instanceof OpenMapRealVector)
/* 280*/      return append((OpenMapRealVector)v); 
/* 282*/    OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
/* 283*/    for (int i = 0; i < v.getDimension(); i++)
/* 284*/      res.setEntry(i + this.virtualSize, v.getEntry(i)); 
/* 286*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector append(double d) {
/* 293*/    OpenMapRealVector res = new OpenMapRealVector(this, 1);
/* 294*/    res.setEntry(this.virtualSize, d);
/* 295*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector copy() {
/* 304*/    return new OpenMapRealVector(this);
/*   0*/  }
/*   0*/  
/*   0*/  public double dotProduct(OpenMapRealVector v) {
/* 317*/    checkVectorDimensions(v.getDimension());
/* 318*/    boolean thisIsSmaller = (this.entries.size() < v.entries.size());
/* 319*/    OpenIntToDoubleHashMap.Iterator iter = thisIsSmaller ? this.entries.iterator() : v.entries.iterator();
/* 320*/    OpenIntToDoubleHashMap larger = thisIsSmaller ? v.entries : this.entries;
/* 321*/    double d = 0.0D;
/* 322*/    while (iter.hasNext()) {
/* 323*/      iter.advance();
/* 324*/      d += iter.value() * larger.get(iter.key());
/*   0*/    } 
/* 326*/    return d;
/*   0*/  }
/*   0*/  
/*   0*/  public double dotProduct(RealVector v) {
/* 332*/    if (v instanceof OpenMapRealVector)
/* 333*/      return dotProduct((OpenMapRealVector)v); 
/* 335*/    return super.dotProduct(v);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector ebeDivide(RealVector v) {
/* 342*/    checkVectorDimensions(v.getDimension());
/* 343*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 349*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 350*/    while (iter.hasNext()) {
/* 351*/      iter.advance();
/* 352*/      res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
/*   0*/    } 
/* 354*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector ebeMultiply(RealVector v) {
/* 360*/    checkVectorDimensions(v.getDimension());
/* 361*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 362*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 363*/    while (iter.hasNext()) {
/* 364*/      iter.advance();
/* 365*/      res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
/*   0*/    } 
/* 374*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector getSubVector(int index, int n) {
/* 380*/    checkIndex(index);
/* 381*/    if (n < 0)
/* 382*/      throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n); 
/* 384*/    checkIndex(index + n - 1);
/* 385*/    OpenMapRealVector res = new OpenMapRealVector(n);
/* 386*/    int end = index + n;
/* 387*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 388*/    while (iter.hasNext()) {
/* 389*/      iter.advance();
/* 390*/      int key = iter.key();
/* 391*/      if (key >= index && key < end)
/* 392*/        res.setEntry(key - index, iter.value()); 
/*   0*/    } 
/* 395*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDimension() {
/* 401*/    return this.virtualSize;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDistance(OpenMapRealVector v) {
/* 413*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 414*/    double res = 0.0D;
/* 415*/    while (iter.hasNext()) {
/* 416*/      iter.advance();
/* 417*/      int key = iter.key();
/* 419*/      double delta = iter.value() - v.getEntry(key);
/* 420*/      res += delta * delta;
/*   0*/    } 
/* 422*/    iter = v.getEntries().iterator();
/* 423*/    while (iter.hasNext()) {
/* 424*/      iter.advance();
/* 425*/      int key = iter.key();
/* 426*/      if (!this.entries.containsKey(key)) {
/* 427*/        double value = iter.value();
/* 428*/        res += value * value;
/*   0*/      } 
/*   0*/    } 
/* 431*/    return FastMath.sqrt(res);
/*   0*/  }
/*   0*/  
/*   0*/  public double getDistance(RealVector v) {
/* 437*/    checkVectorDimensions(v.getDimension());
/* 438*/    if (v instanceof OpenMapRealVector)
/* 439*/      return getDistance((OpenMapRealVector)v); 
/* 441*/    return super.getDistance(v);
/*   0*/  }
/*   0*/  
/*   0*/  public double getEntry(int index) {
/* 448*/    checkIndex(index);
/* 449*/    return this.entries.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public double getL1Distance(OpenMapRealVector v) {
/* 462*/    double max = 0.0D;
/* 463*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 464*/    while (iter.hasNext()) {
/* 465*/      iter.advance();
/* 466*/      double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
/* 467*/      max += delta;
/*   0*/    } 
/* 469*/    iter = v.getEntries().iterator();
/* 470*/    while (iter.hasNext()) {
/* 471*/      iter.advance();
/* 472*/      int key = iter.key();
/* 473*/      if (!this.entries.containsKey(key)) {
/* 474*/        double delta = FastMath.abs(iter.value());
/* 475*/        max += FastMath.abs(delta);
/*   0*/      } 
/*   0*/    } 
/* 478*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public double getL1Distance(RealVector v) {
/* 484*/    checkVectorDimensions(v.getDimension());
/* 485*/    if (v instanceof OpenMapRealVector)
/* 486*/      return getL1Distance((OpenMapRealVector)v); 
/* 488*/    return super.getL1Distance(v);
/*   0*/  }
/*   0*/  
/*   0*/  private double getLInfDistance(OpenMapRealVector v) {
/* 499*/    double max = 0.0D;
/* 500*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 501*/    while (iter.hasNext()) {
/* 502*/      iter.advance();
/* 503*/      double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
/* 504*/      if (delta > max)
/* 505*/        max = delta; 
/*   0*/    } 
/* 508*/    iter = v.getEntries().iterator();
/* 509*/    while (iter.hasNext()) {
/* 510*/      iter.advance();
/* 511*/      int key = iter.key();
/* 512*/      if (!this.entries.containsKey(key) && 
/* 513*/        iter.value() > max)
/* 514*/        max = iter.value(); 
/*   0*/    } 
/* 518*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public double getLInfDistance(RealVector v) {
/* 524*/    checkVectorDimensions(v.getDimension());
/* 525*/    if (v instanceof OpenMapRealVector)
/* 526*/      return getLInfDistance((OpenMapRealVector)v); 
/* 528*/    return super.getLInfDistance(v);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInfinite() {
/*   0*/    boolean infiniteFound = false;
/* 536*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 537*/    while (iter.hasNext()) {
/* 538*/      iter.advance();
/* 539*/      double value = iter.value();
/* 540*/      if (Double.isNaN(value))
/* 541*/        return false; 
/* 543*/      if (Double.isInfinite(value))
/* 544*/        infiniteFound = true; 
/*   0*/    } 
/* 547*/    return infiniteFound;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNaN() {
/* 553*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 554*/    while (iter.hasNext()) {
/* 555*/      iter.advance();
/* 556*/      if (Double.isNaN(iter.value()))
/* 557*/        return true; 
/*   0*/    } 
/* 560*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector mapAdd(double d) {
/* 566*/    return copy().mapAddToSelf(d);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector mapAddToSelf(double d) {
/* 572*/    for (int i = 0; i < this.virtualSize; i++)
/* 573*/      setEntry(i, getEntry(i) + d); 
/* 575*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public RealVector projection(RealVector v) {
/* 581*/    checkVectorDimensions(v.getDimension());
/* 582*/    return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
/*   0*/  }
/*   0*/  
/*   0*/  public void setEntry(int index, double value) {
/* 588*/    checkIndex(index);
/* 589*/    if (!isDefaultValue(value)) {
/* 590*/      this.entries.put(index, value);
/* 591*/    } else if (this.entries.containsKey(index)) {
/* 592*/      this.entries.remove(index);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setSubVector(int index, RealVector v) {
/* 599*/    checkIndex(index);
/* 600*/    checkIndex(index + v.getDimension() - 1);
/* 601*/    for (int i = 0; i < v.getDimension(); i++)
/* 602*/      setEntry(i + index, v.getEntry(i)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void set(double value) {
/* 609*/    for (int i = 0; i < this.virtualSize; i++)
/* 610*/      setEntry(i, value); 
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector subtract(OpenMapRealVector v) {
/* 623*/    checkVectorDimensions(v.getDimension());
/* 624*/    OpenMapRealVector res = copy();
/* 625*/    OpenIntToDoubleHashMap.Iterator iter = v.getEntries().iterator();
/* 626*/    while (iter.hasNext()) {
/* 627*/      iter.advance();
/* 628*/      int key = iter.key();
/* 629*/      if (this.entries.containsKey(key)) {
/* 630*/        res.setEntry(key, this.entries.get(key) - iter.value());
/*   0*/        continue;
/*   0*/      } 
/* 632*/      res.setEntry(key, -iter.value());
/*   0*/    } 
/* 635*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public RealVector subtract(RealVector v) {
/* 641*/    checkVectorDimensions(v.getDimension());
/* 642*/    if (v instanceof OpenMapRealVector)
/* 643*/      return subtract((OpenMapRealVector)v); 
/* 645*/    return super.subtract(v);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector unitVector() {
/* 652*/    OpenMapRealVector res = copy();
/* 653*/    res.unitize();
/* 654*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public void unitize() {
/* 660*/    double norm = getNorm();
/* 661*/    if (isDefaultValue(norm))
/* 662*/      throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]); 
/* 664*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 665*/    while (iter.hasNext()) {
/* 666*/      iter.advance();
/* 667*/      this.entries.put(iter.key(), iter.value() / norm);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double[] toArray() {
/* 674*/    double[] res = new double[this.virtualSize];
/* 675*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 676*/    while (iter.hasNext()) {
/* 677*/      iter.advance();
/* 678*/      res[iter.key()] = iter.value();
/*   0*/    } 
/* 680*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 691*/    int prime = 31;
/* 692*/    int result = 1;
/* 694*/    long temp = Double.doubleToLongBits(this.epsilon);
/* 695*/    result = 31 * result + (int)(temp ^ temp >>> 32L);
/* 696*/    result = 31 * result + this.virtualSize;
/* 697*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 698*/    while (iter.hasNext()) {
/* 699*/      iter.advance();
/* 700*/      temp = Double.doubleToLongBits(iter.value());
/* 701*/      result = 31 * result + (int)(temp ^ temp >> 32L);
/*   0*/    } 
/* 703*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 714*/    if (this == obj)
/* 715*/      return true; 
/* 717*/    if (!(obj instanceof OpenMapRealVector))
/* 718*/      return false; 
/* 720*/    OpenMapRealVector other = (OpenMapRealVector)obj;
/* 721*/    if (this.virtualSize != other.virtualSize)
/* 722*/      return false; 
/* 724*/    if (Double.doubleToLongBits(this.epsilon) != Double.doubleToLongBits(other.epsilon))
/* 726*/      return false; 
/* 728*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 729*/    while (iter.hasNext()) {
/* 730*/      iter.advance();
/* 731*/      double test = other.getEntry(iter.key());
/* 732*/      if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value()))
/* 733*/        return false; 
/*   0*/    } 
/* 736*/    iter = other.getEntries().iterator();
/* 737*/    while (iter.hasNext()) {
/* 738*/      iter.advance();
/* 739*/      double test = iter.value();
/* 740*/      if (Double.doubleToLongBits(test) != Double.doubleToLongBits(getEntry(iter.key())))
/* 741*/        return false; 
/*   0*/    } 
/* 744*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public double getSparsity() {
/* 753*/    return this.entries.size() / getDimension();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<RealVector.Entry> sparseIterator() {
/* 759*/    return new OpenMapSparseIterator();
/*   0*/  }
/*   0*/  
/*   0*/  protected class OpenMapEntry extends RealVector.Entry {
/*   0*/    private final OpenIntToDoubleHashMap.Iterator iter;
/*   0*/    
/*   0*/    protected OpenMapEntry(OpenIntToDoubleHashMap.Iterator iter) {
/* 777*/      this.iter = iter;
/*   0*/    }
/*   0*/    
/*   0*/    public double getValue() {
/* 783*/      return this.iter.value();
/*   0*/    }
/*   0*/    
/*   0*/    public void setValue(double value) {
/* 789*/      OpenMapRealVector.this.entries.put(this.iter.key(), value);
/*   0*/    }
/*   0*/    
/*   0*/    public int getIndex() {
/* 795*/      return this.iter.key();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  protected class OpenMapSparseIterator implements Iterator<RealVector.Entry> {
/* 813*/    private final OpenIntToDoubleHashMap.Iterator iter = OpenMapRealVector.this.entries.iterator();
/*   0*/    
/* 814*/    private final RealVector.Entry current = new OpenMapRealVector.OpenMapEntry(this.iter);
/*   0*/    
/*   0*/    public boolean hasNext() {
/* 819*/      return this.iter.hasNext();
/*   0*/    }
/*   0*/    
/*   0*/    public RealVector.Entry next() {
/* 824*/      this.iter.advance();
/* 825*/      return this.current;
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/* 830*/      throw new UnsupportedOperationException("Not supported");
/*   0*/    }
/*   0*/  }
/*   0*/}
