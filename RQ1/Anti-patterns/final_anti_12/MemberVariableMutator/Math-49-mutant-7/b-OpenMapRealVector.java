/*   0*/package org.apache.commons.math.linear;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Iterator;
/*   0*/import org.apache.commons.math.exception.MathArithmeticException;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/import org.apache.commons.math.util.FastMath;
/*   0*/import org.apache.commons.math.util.OpenIntToDoubleHashMap;
/*   0*/
/*   0*/public class OpenMapRealVector extends AbstractRealVector implements SparseRealVector, Serializable {
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
/*  77*/    this;
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
/* 139*/      if (!isDefaultValue(value))
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
/* 278*/    if (v instanceof OpenMapRealVector)
/* 279*/      return append((OpenMapRealVector)v); 
/* 281*/    return append(v.getData());
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector append(double d) {
/* 286*/    OpenMapRealVector res = new OpenMapRealVector(this, 1);
/* 287*/    res.setEntry(this.virtualSize, d);
/* 288*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector append(double[] a) {
/* 293*/    OpenMapRealVector res = new OpenMapRealVector(this, a.length);
/* 294*/    for (int i = 0; i < a.length; i++)
/* 295*/      res.setEntry(i + this.virtualSize, a[i]); 
/* 297*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector copy() {
/* 306*/    return new OpenMapRealVector(this);
/*   0*/  }
/*   0*/  
/*   0*/  public double dotProduct(OpenMapRealVector v) {
/* 319*/    checkVectorDimensions(v.getDimension());
/* 320*/    boolean thisIsSmaller = (this.entries.size() < v.entries.size());
/* 321*/    OpenIntToDoubleHashMap.Iterator iter = thisIsSmaller ? this.entries.iterator() : v.entries.iterator();
/* 322*/    OpenIntToDoubleHashMap larger = thisIsSmaller ? v.entries : this.entries;
/* 323*/    double d = 0.0D;
/* 324*/    while (iter.hasNext()) {
/* 325*/      iter.advance();
/* 326*/      d += iter.value() * larger.get(iter.key());
/*   0*/    } 
/* 328*/    return d;
/*   0*/  }
/*   0*/  
/*   0*/  public double dotProduct(RealVector v) {
/* 334*/    if (v instanceof OpenMapRealVector)
/* 335*/      return dotProduct((OpenMapRealVector)v); 
/* 337*/    return super.dotProduct(v);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector ebeDivide(RealVector v) {
/* 343*/    checkVectorDimensions(v.getDimension());
/* 344*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 345*/    OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
/* 346*/    while (iter.hasNext()) {
/* 347*/      iter.advance();
/* 348*/      res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
/*   0*/    } 
/* 350*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector ebeDivide(double[] v) {
/* 356*/    checkVectorDimensions(v.length);
/* 357*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 358*/    OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
/* 359*/    while (iter.hasNext()) {
/* 360*/      iter.advance();
/* 361*/      res.setEntry(iter.key(), iter.value() / v[iter.key()]);
/*   0*/    } 
/* 363*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector ebeMultiply(RealVector v) {
/* 368*/    checkVectorDimensions(v.getDimension());
/* 369*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 370*/    OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
/* 371*/    while (iter.hasNext()) {
/* 372*/      iter.advance();
/* 373*/      res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
/*   0*/    } 
/* 375*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector ebeMultiply(double[] v) {
/* 381*/    checkVectorDimensions(v.length);
/* 382*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 383*/    OpenIntToDoubleHashMap.Iterator iter = res.entries.iterator();
/* 384*/    while (iter.hasNext()) {
/* 385*/      iter.advance();
/* 386*/      res.setEntry(iter.key(), iter.value() * v[iter.key()]);
/*   0*/    } 
/* 388*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector getSubVector(int index, int n) {
/* 393*/    checkIndex(index);
/* 394*/    checkIndex(index + n - 1);
/* 395*/    OpenMapRealVector res = new OpenMapRealVector(n);
/* 396*/    int end = index + n;
/* 397*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 398*/    while (iter.hasNext()) {
/* 399*/      iter.advance();
/* 400*/      int key = iter.key();
/* 401*/      if (key >= index && key < end)
/* 402*/        res.setEntry(key - index, iter.value()); 
/*   0*/    } 
/* 405*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public double[] getData() {
/* 411*/    double[] res = new double[this.virtualSize];
/* 412*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 413*/    while (iter.hasNext()) {
/* 414*/      iter.advance();
/* 415*/      res[iter.key()] = iter.value();
/*   0*/    } 
/* 417*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDimension() {
/* 422*/    return this.virtualSize;
/*   0*/  }
/*   0*/  
/*   0*/  public double getDistance(OpenMapRealVector v) {
/* 434*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 435*/    double res = 0.0D;
/* 436*/    while (iter.hasNext()) {
/* 437*/      iter.advance();
/* 438*/      int key = iter.key();
/* 440*/      double delta = iter.value() - v.getEntry(key);
/* 441*/      res += delta * delta;
/*   0*/    } 
/* 443*/    iter = v.getEntries().iterator();
/* 444*/    while (iter.hasNext()) {
/* 445*/      iter.advance();
/* 446*/      int key = iter.key();
/* 447*/      if (!this.entries.containsKey(key)) {
/* 448*/        double value = iter.value();
/* 449*/        res += value * value;
/*   0*/      } 
/*   0*/    } 
/* 452*/    return FastMath.sqrt(res);
/*   0*/  }
/*   0*/  
/*   0*/  public double getDistance(RealVector v) {
/* 458*/    checkVectorDimensions(v.getDimension());
/* 459*/    if (v instanceof OpenMapRealVector)
/* 460*/      return getDistance((OpenMapRealVector)v); 
/* 462*/    return getDistance(v.getData());
/*   0*/  }
/*   0*/  
/*   0*/  public double getDistance(double[] v) {
/* 468*/    checkVectorDimensions(v.length);
/* 469*/    double res = 0.0D;
/* 470*/    for (int i = 0; i < v.length; i++) {
/* 471*/      double delta = this.entries.get(i) - v[i];
/* 472*/      res += delta * delta;
/*   0*/    } 
/* 474*/    return FastMath.sqrt(res);
/*   0*/  }
/*   0*/  
/*   0*/  public double getEntry(int index) {
/* 479*/    checkIndex(index);
/* 480*/    return this.entries.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public double getL1Distance(OpenMapRealVector v) {
/* 493*/    double max = 0.0D;
/* 494*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 495*/    while (iter.hasNext()) {
/* 496*/      iter.advance();
/* 497*/      double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
/* 498*/      max += delta;
/*   0*/    } 
/* 500*/    iter = v.getEntries().iterator();
/* 501*/    while (iter.hasNext()) {
/* 502*/      iter.advance();
/* 503*/      int key = iter.key();
/* 504*/      if (!this.entries.containsKey(key)) {
/* 505*/        double delta = FastMath.abs(iter.value());
/* 506*/        max += FastMath.abs(delta);
/*   0*/      } 
/*   0*/    } 
/* 509*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public double getL1Distance(RealVector v) {
/* 515*/    checkVectorDimensions(v.getDimension());
/* 516*/    if (v instanceof OpenMapRealVector)
/* 517*/      return getL1Distance((OpenMapRealVector)v); 
/* 519*/    return getL1Distance(v.getData());
/*   0*/  }
/*   0*/  
/*   0*/  public double getL1Distance(double[] v) {
/* 525*/    checkVectorDimensions(v.length);
/* 526*/    double max = 0.0D;
/* 527*/    for (int i = 0; i < v.length; i++) {
/* 528*/      double delta = FastMath.abs(getEntry(i) - v[i]);
/* 529*/      max += delta;
/*   0*/    } 
/* 531*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  private double getLInfDistance(OpenMapRealVector v) {
/* 541*/    double max = 0.0D;
/* 542*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 543*/    while (iter.hasNext()) {
/* 544*/      iter.advance();
/* 545*/      double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
/* 546*/      if (delta > max)
/* 547*/        max = delta; 
/*   0*/    } 
/* 550*/    iter = v.getEntries().iterator();
/* 551*/    while (iter.hasNext()) {
/* 552*/      iter.advance();
/* 553*/      int key = iter.key();
/* 554*/      if (!this.entries.containsKey(key) && 
/* 555*/        iter.value() > max)
/* 556*/        max = iter.value(); 
/*   0*/    } 
/* 560*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public double getLInfDistance(RealVector v) {
/* 566*/    checkVectorDimensions(v.getDimension());
/* 567*/    if (v instanceof OpenMapRealVector)
/* 568*/      return getLInfDistance((OpenMapRealVector)v); 
/* 570*/    return getLInfDistance(v.getData());
/*   0*/  }
/*   0*/  
/*   0*/  public double getLInfDistance(double[] v) {
/* 576*/    checkVectorDimensions(v.length);
/* 577*/    double max = 0.0D;
/* 578*/    for (int i = 0; i < v.length; i++) {
/* 579*/      double delta = FastMath.abs(getEntry(i) - v[i]);
/* 580*/      if (delta > max)
/* 581*/        max = delta; 
/*   0*/    } 
/* 584*/    return max;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isInfinite() {
/*   0*/    boolean infiniteFound = false;
/* 590*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 591*/    while (iter.hasNext()) {
/* 592*/      iter.advance();
/* 593*/      double value = iter.value();
/* 594*/      if (Double.isNaN(value))
/* 595*/        return false; 
/* 597*/      if (Double.isInfinite(value))
/* 598*/        infiniteFound = true; 
/*   0*/    } 
/* 601*/    return infiniteFound;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNaN() {
/* 606*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 607*/    while (iter.hasNext()) {
/* 608*/      iter.advance();
/* 609*/      if (Double.isNaN(iter.value()))
/* 610*/        return true; 
/*   0*/    } 
/* 613*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector mapAdd(double d) {
/* 619*/    return copy().mapAddToSelf(d);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector mapAddToSelf(double d) {
/* 625*/    for (int i = 0; i < this.virtualSize; i++)
/* 626*/      setEntry(i, getEntry(i) + d); 
/* 628*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public RealMatrix outerProduct(double[] v) {
/* 634*/    int n = v.length;
/* 635*/    RealMatrix res = new OpenMapRealMatrix(this.virtualSize, n);
/* 636*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 637*/    while (iter.hasNext()) {
/* 638*/      iter.advance();
/* 639*/      int row = iter.key();
/* 640*/      double value = iter.value();
/* 641*/      for (int col = 0; col < n; col++)
/* 642*/        res.setEntry(row, col, value * v[col]); 
/*   0*/    } 
/* 645*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public RealVector projection(RealVector v) {
/* 650*/    checkVectorDimensions(v.getDimension());
/* 651*/    return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector projection(double[] v) {
/* 657*/    checkVectorDimensions(v.length);
/* 658*/    return (OpenMapRealVector)projection(new OpenMapRealVector(v));
/*   0*/  }
/*   0*/  
/*   0*/  public void setEntry(int index, double value) {
/* 663*/    checkIndex(index);
/* 664*/    if (!isDefaultValue(value)) {
/* 665*/      this.entries.put(index, value);
/* 666*/    } else if (this.entries.containsKey(index)) {
/* 667*/      this.entries.remove(index);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setSubVector(int index, RealVector v) {
/* 674*/    checkIndex(index);
/* 675*/    checkIndex(index + v.getDimension() - 1);
/* 676*/    setSubVector(index, v.getData());
/*   0*/  }
/*   0*/  
/*   0*/  public void setSubVector(int index, double[] v) {
/* 682*/    checkIndex(index);
/* 683*/    checkIndex(index + v.length - 1);
/* 684*/    for (int i = 0; i < v.length; i++)
/* 685*/      setEntry(i + index, v[i]); 
/*   0*/  }
/*   0*/  
/*   0*/  public void set(double value) {
/* 692*/    for (int i = 0; i < this.virtualSize; i++)
/* 693*/      setEntry(i, value); 
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector subtract(OpenMapRealVector v) {
/* 706*/    checkVectorDimensions(v.getDimension());
/* 707*/    OpenMapRealVector res = copy();
/* 708*/    OpenIntToDoubleHashMap.Iterator iter = v.getEntries().iterator();
/* 709*/    while (iter.hasNext()) {
/* 710*/      iter.advance();
/* 711*/      int key = iter.key();
/* 712*/      if (this.entries.containsKey(key)) {
/* 713*/        res.setEntry(key, this.entries.get(key) - iter.value());
/*   0*/        continue;
/*   0*/      } 
/* 715*/      res.setEntry(key, -iter.value());
/*   0*/    } 
/* 718*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector subtract(RealVector v) {
/* 724*/    checkVectorDimensions(v.getDimension());
/* 725*/    if (v instanceof OpenMapRealVector)
/* 726*/      return subtract((OpenMapRealVector)v); 
/* 728*/    return subtract(v.getData());
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector subtract(double[] v) {
/* 734*/    checkVectorDimensions(v.length);
/* 735*/    OpenMapRealVector res = new OpenMapRealVector(this);
/* 736*/    for (int i = 0; i < v.length; i++) {
/* 737*/      if (this.entries.containsKey(i)) {
/* 738*/        res.setEntry(i, this.entries.get(i) - v[i]);
/*   0*/      } else {
/* 740*/        res.setEntry(i, -v[i]);
/*   0*/      } 
/*   0*/    } 
/* 743*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenMapRealVector unitVector() {
/* 750*/    OpenMapRealVector res = copy();
/* 751*/    res.unitize();
/* 752*/    return res;
/*   0*/  }
/*   0*/  
/*   0*/  public void unitize() {
/* 758*/    double norm = getNorm();
/* 759*/    if (isDefaultValue(norm))
/* 760*/      throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]); 
/* 762*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 763*/    while (iter.hasNext()) {
/* 764*/      iter.advance();
/* 765*/      this.entries.put(iter.key(), iter.value() / norm);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double[] toArray() {
/* 772*/    return getData();
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 783*/    int prime = 31;
/* 784*/    int result = 1;
/* 786*/    long temp = Double.doubleToLongBits(this.epsilon);
/* 787*/    result = 31 * result + (int)(temp ^ temp >>> 32L);
/* 788*/    result = 31 * result + this.virtualSize;
/* 789*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 790*/    while (iter.hasNext()) {
/* 791*/      iter.advance();
/* 792*/      temp = Double.doubleToLongBits(iter.value());
/* 793*/      result = 31 * result + (int)(temp ^ temp >> 32L);
/*   0*/    } 
/* 795*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 806*/    if (this == obj)
/* 807*/      return true; 
/* 809*/    if (!(obj instanceof OpenMapRealVector))
/* 810*/      return false; 
/* 812*/    OpenMapRealVector other = (OpenMapRealVector)obj;
/* 813*/    if (this.virtualSize != other.virtualSize)
/* 814*/      return false; 
/* 816*/    if (Double.doubleToLongBits(this.epsilon) != Double.doubleToLongBits(other.epsilon))
/* 818*/      return false; 
/* 820*/    OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
/* 821*/    while (iter.hasNext()) {
/* 822*/      iter.advance();
/* 823*/      double test = other.getEntry(iter.key());
/* 824*/      if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value()))
/* 825*/        return false; 
/*   0*/    } 
/* 828*/    iter = other.getEntries().iterator();
/* 829*/    while (iter.hasNext()) {
/* 830*/      iter.advance();
/* 831*/      double test = iter.value();
/* 832*/      if (Double.doubleToLongBits(test) != Double.doubleToLongBits(getEntry(iter.key())))
/* 833*/        return false; 
/*   0*/    } 
/* 836*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public double getSparsity() {
/* 845*/    return this.entries.size() / getDimension();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<RealVector.Entry> sparseIterator() {
/* 851*/    return new OpenMapSparseIterator();
/*   0*/  }
/*   0*/  
/*   0*/  protected class OpenMapEntry extends RealVector.Entry {
/*   0*/    private final OpenIntToDoubleHashMap.Iterator iter;
/*   0*/    
/*   0*/    protected OpenMapEntry(OpenIntToDoubleHashMap.Iterator iter) {
/* 869*/      this.iter = iter;
/*   0*/    }
/*   0*/    
/*   0*/    public double getValue() {
/* 875*/      return this.iter.value();
/*   0*/    }
/*   0*/    
/*   0*/    public void setValue(double value) {
/* 881*/      OpenMapRealVector.this.entries.put(this.iter.key(), value);
/*   0*/    }
/*   0*/    
/*   0*/    public int getIndex() {
/* 887*/      return this.iter.key();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  protected class OpenMapSparseIterator implements Iterator<RealVector.Entry> {
/* 905*/    private final OpenIntToDoubleHashMap.Iterator iter = OpenMapRealVector.this.entries.iterator();
/*   0*/    
/* 906*/    private final RealVector.Entry current = new OpenMapRealVector.OpenMapEntry(this.iter);
/*   0*/    
/*   0*/    public boolean hasNext() {
/* 911*/      return this.iter.hasNext();
/*   0*/    }
/*   0*/    
/*   0*/    public RealVector.Entry next() {
/* 916*/      this.iter.advance();
/* 917*/      return this.current;
/*   0*/    }
/*   0*/    
/*   0*/    public void remove() {
/* 922*/      throw new UnsupportedOperationException("Not supported");
/*   0*/    }
/*   0*/  }
/*   0*/}
