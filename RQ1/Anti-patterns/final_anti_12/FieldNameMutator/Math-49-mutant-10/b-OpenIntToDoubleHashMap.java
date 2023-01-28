/*   0*/package org.apache.commons.math.util;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ConcurrentModificationException;
/*   0*/import java.util.NoSuchElementException;
/*   0*/import org.apache.commons.math.MathRuntimeException;
/*   0*/import org.apache.commons.math.exception.util.LocalizedFormats;
/*   0*/
/*   0*/public class OpenIntToDoubleHashMap implements Serializable {
/*   0*/  protected static final byte FREE = 0;
/*   0*/  
/*   0*/  protected static final byte FULL = 1;
/*   0*/  
/*   0*/  protected static final byte REMOVED = 2;
/*   0*/  
/*   0*/  private static final long serialVersionUID = -3646337053166149105L;
/*   0*/  
/*   0*/  private static final float LOAD_FACTOR = 0.5F;
/*   0*/  
/*   0*/  private static final int DEFAULT_EXPECTED_SIZE = 16;
/*   0*/  
/*   0*/  private static final int RESIZE_MULTIPLIER = 2;
/*   0*/  
/*   0*/  private static final int PERTURB_SHIFT = 5;
/*   0*/  
/*   0*/  private int[] keys;
/*   0*/  
/*   0*/  private double[] values;
/*   0*/  
/*   0*/  private byte[] states;
/*   0*/  
/*   0*/  private final double missingEntries;
/*   0*/  
/*   0*/  private int size;
/*   0*/  
/*   0*/  private int mask;
/*   0*/  
/*   0*/  private transient int count;
/*   0*/  
/*   0*/  public OpenIntToDoubleHashMap() {
/*  95*/    this(16, Double.NaN);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenIntToDoubleHashMap(double missingEntries) {
/* 103*/    this(16, missingEntries);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenIntToDoubleHashMap(int expectedSize) {
/* 111*/    this(expectedSize, Double.NaN);
/*   0*/  }
/*   0*/  
/*   0*/  public OpenIntToDoubleHashMap(int expectedSize, double missingEntries) {
/* 121*/    int capacity = computeCapacity(expectedSize);
/* 122*/    this.keys = new int[capacity];
/* 123*/    this.values = new double[capacity];
/* 124*/    this.states = new byte[capacity];
/* 125*/    this.missingEntries = missingEntries;
/* 126*/    this.mask = capacity - 1;
/*   0*/  }
/*   0*/  
/*   0*/  public OpenIntToDoubleHashMap(OpenIntToDoubleHashMap source) {
/* 134*/    int length = source.keys.length;
/* 135*/    this.keys = new int[length];
/* 136*/    System.arraycopy(source.keys, 0, this.keys, 0, length);
/* 137*/    this.values = new double[length];
/* 138*/    System.arraycopy(source.values, 0, this.values, 0, length);
/* 139*/    this.states = new byte[length];
/* 140*/    System.arraycopy(source.states, 0, this.states, 0, length);
/* 141*/    this.missingEntries = source.missingEntries;
/* 142*/    this.size = source.size;
/* 143*/    this.mask = source.mask;
/* 144*/    this.count = source.count;
/*   0*/  }
/*   0*/  
/*   0*/  private static int computeCapacity(int expectedSize) {
/* 153*/    if (expectedSize == 0)
/* 154*/      return 1; 
/* 156*/    int capacity = (int)FastMath.ceil((expectedSize / 0.5F));
/* 157*/    int powerOfTwo = Integer.highestOneBit(capacity);
/* 158*/    if (powerOfTwo == capacity)
/* 159*/      return capacity; 
/* 161*/    return nextPowerOfTwo(capacity);
/*   0*/  }
/*   0*/  
/*   0*/  private static int nextPowerOfTwo(int i) {
/* 170*/    return Integer.highestOneBit(i) << 1;
/*   0*/  }
/*   0*/  
/*   0*/  public double get(int key) {
/* 180*/    int hash = hashOf(key);
/* 181*/    int index = hash & this.mask;
/* 182*/    if (containsKey(key, index))
/* 183*/      return this.values[index]; 
/* 186*/    if (this.states[index] == 0)
/* 187*/      return this.missingEntries; 
/* 190*/    int j = index;
/* 191*/    for (int perturb = perturb(hash); this.states[index] != 0; perturb >>= 5) {
/* 192*/      j = probe(perturb, j);
/* 193*/      index = j & this.mask;
/* 194*/      if (containsKey(key, index))
/* 195*/        return this.values[index]; 
/*   0*/    } 
/* 199*/    return this.missingEntries;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean containsKey(int key) {
/* 210*/    int hash = hashOf(key);
/* 211*/    int index = hash & this.mask;
/* 212*/    if (containsKey(key, index))
/* 213*/      return true; 
/* 216*/    if (this.states[index] == 0)
/* 217*/      return false; 
/* 220*/    int j = index;
/* 221*/    for (int perturb = perturb(hash); this.states[index] != 0; perturb >>= 5) {
/* 222*/      j = probe(perturb, j);
/* 223*/      index = j & this.mask;
/* 224*/      if (containsKey(key, index))
/* 225*/        return true; 
/*   0*/    } 
/* 229*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator iterator() {
/* 241*/    return new Iterator();
/*   0*/  }
/*   0*/  
/*   0*/  private static int perturb(int hash) {
/* 250*/    return hash & Integer.MAX_VALUE;
/*   0*/  }
/*   0*/  
/*   0*/  private int findInsertionIndex(int key) {
/* 259*/    return findInsertionIndex(this.keys, this.states, key, this.mask);
/*   0*/  }
/*   0*/  
/*   0*/  private static int findInsertionIndex(int[] keys, byte[] states, int key, int mask) {
/* 272*/    int hash = hashOf(key);
/* 273*/    int index = hash & mask;
/* 274*/    if (states[index] == 0)
/* 275*/      return index; 
/* 276*/    if (states[index] == 1 && keys[index] == key)
/* 277*/      return changeIndexSign(index); 
/* 280*/    int perturb = perturb(hash);
/* 281*/    int j = index;
/* 282*/    if (states[index] == 1)
/*   0*/      do {
/* 284*/        j = probe(perturb, j);
/* 285*/        index = j & mask;
/* 286*/        perturb >>= 5;
/* 288*/      } while (states[index] == 1 && keys[index] != key); 
/* 294*/    if (states[index] == 0)
/* 295*/      return index; 
/* 296*/    if (states[index] == 1)
/* 299*/      return changeIndexSign(index); 
/* 302*/    int firstRemoved = index;
/*   0*/    while (true) {
/* 304*/      j = probe(perturb, j);
/* 305*/      index = j & mask;
/* 307*/      if (states[index] == 0)
/* 308*/        return firstRemoved; 
/* 309*/      if (states[index] == 1 && keys[index] == key)
/* 310*/        return changeIndexSign(index); 
/* 313*/      perturb >>= 5;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static int probe(int perturb, int j) {
/* 326*/    return (j << 2) + j + perturb + 1;
/*   0*/  }
/*   0*/  
/*   0*/  private static int changeIndexSign(int index) {
/* 335*/    return -index - 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 343*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  public double remove(int key) {
/* 354*/    int hash = hashOf(key);
/* 355*/    int index = hash & this.mask;
/* 356*/    if (containsKey(key, index))
/* 357*/      return doRemove(index); 
/* 360*/    if (this.states[index] == 0)
/* 361*/      return this.missingEntries; 
/* 364*/    int j = index;
/* 365*/    for (int perturb = perturb(hash); this.states[index] != 0; perturb >>= 5) {
/* 366*/      j = probe(perturb, j);
/* 367*/      index = j & this.mask;
/* 368*/      if (containsKey(key, index))
/* 369*/        return doRemove(index); 
/*   0*/    } 
/* 373*/    return this.missingEntries;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean containsKey(int key, int index) {
/* 385*/    return ((key != 0 || this.states[index] == 1) && this.keys[index] == key);
/*   0*/  }
/*   0*/  
/*   0*/  private double doRemove(int index) {
/* 394*/    this.keys[index] = 0;
/* 395*/    this.states[index] = 2;
/* 396*/    double previous = this.values[index];
/* 397*/    this.values[index] = this.missingEntries;
/* 398*/    this.size--;
/* 399*/    this.count = this.size + 1;
/* 400*/    return previous;
/*   0*/  }
/*   0*/  
/*   0*/  public double put(int key, double value) {
/* 410*/    int index = findInsertionIndex(key);
/* 411*/    double previous = this.missingEntries;
/*   0*/    boolean newMapping = true;
/* 413*/    if (index < 0) {
/* 414*/      index = changeIndexSign(index);
/* 415*/      previous = this.values[index];
/* 416*/      newMapping = false;
/*   0*/    } 
/* 418*/    this.keys[index] = key;
/* 419*/    this.states[index] = 1;
/* 420*/    this.values[index] = value;
/* 421*/    if (newMapping) {
/* 422*/      this.size++;
/* 423*/      if (shouldGrowTable())
/* 424*/        growTable(); 
/* 426*/      this.count++;
/*   0*/    } 
/* 428*/    return previous;
/*   0*/  }
/*   0*/  
/*   0*/  private void growTable() {
/* 437*/    int oldLength = this.states.length;
/* 438*/    int[] oldKeys = this.keys;
/* 439*/    double[] oldValues = this.values;
/* 440*/    byte[] oldStates = this.states;
/* 442*/    int newLength = 2 * oldLength;
/* 443*/    int[] newKeys = new int[newLength];
/* 444*/    double[] newValues = new double[newLength];
/* 445*/    byte[] newStates = new byte[newLength];
/* 446*/    int newMask = newLength - 1;
/* 447*/    for (int i = 0; i < oldLength; i++) {
/* 448*/      if (oldStates[i] == 1) {
/* 449*/        int key = oldKeys[i];
/* 450*/        int index = findInsertionIndex(newKeys, newStates, key, newMask);
/* 451*/        newKeys[index] = key;
/* 452*/        newValues[index] = oldValues[i];
/* 453*/        newStates[index] = 1;
/*   0*/      } 
/*   0*/    } 
/* 457*/    this.mask = newMask;
/* 458*/    this.keys = newKeys;
/* 459*/    this.values = newValues;
/* 460*/    this.states = newStates;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean shouldGrowTable() {
/* 469*/    return (this.size > (this.mask + 1) * 0.5F);
/*   0*/  }
/*   0*/  
/*   0*/  private static int hashOf(int key) {
/* 478*/    int h = key ^ key >>> 20 ^ key >>> 12;
/* 479*/    return h ^ h >>> 7 ^ h >>> 4;
/*   0*/  }
/*   0*/  
/*   0*/  public class Iterator {
/* 501*/    private final int referenceCount = OpenIntToDoubleHashMap.this.count;
/*   0*/    
/*   0*/    private int current;
/*   0*/    
/* 504*/    private int next = -1;
/*   0*/    
/*   0*/    private Iterator() {
/*   0*/      try {
/* 506*/        advance();
/* 507*/      } catch (NoSuchElementException noSuchElementException) {}
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasNext() {
/* 518*/      return (this.next >= 0);
/*   0*/    }
/*   0*/    
/*   0*/    public int key() throws ConcurrentModificationException, NoSuchElementException {
/* 529*/      if (this.referenceCount != OpenIntToDoubleHashMap.this.count)
/* 530*/        throw MathRuntimeException.createConcurrentModificationException(LocalizedFormats.MAP_MODIFIED_WHILE_ITERATING, new Object[0]); 
/* 532*/      if (this.current < 0)
/* 533*/        throw MathRuntimeException.createNoSuchElementException(LocalizedFormats.ITERATOR_EXHAUSTED, new Object[0]); 
/* 535*/      return OpenIntToDoubleHashMap.this.keys[this.current];
/*   0*/    }
/*   0*/    
/*   0*/    public double value() throws ConcurrentModificationException, NoSuchElementException {
/* 546*/      if (this.referenceCount != OpenIntToDoubleHashMap.this.count)
/* 547*/        throw MathRuntimeException.createConcurrentModificationException(LocalizedFormats.MAP_MODIFIED_WHILE_ITERATING, new Object[0]); 
/* 549*/      if (this.current < 0)
/* 550*/        throw MathRuntimeException.createNoSuchElementException(LocalizedFormats.ITERATOR_EXHAUSTED, new Object[0]); 
/* 552*/      return OpenIntToDoubleHashMap.this.values[this.current];
/*   0*/    }
/*   0*/    
/*   0*/    public void advance() throws ConcurrentModificationException, NoSuchElementException {
/* 563*/      if (this.referenceCount != OpenIntToDoubleHashMap.this.count)
/* 564*/        throw MathRuntimeException.createConcurrentModificationException(LocalizedFormats.MAP_MODIFIED_WHILE_ITERATING, new Object[0]); 
/* 568*/      this.current = this.next;
/*   0*/      while (true) {
/*   0*/        try {
/* 572*/          if (OpenIntToDoubleHashMap.this.states[++this.next] != 1)
/*   0*/            continue; 
/*   0*/          break;
/* 575*/        } catch (ArrayIndexOutOfBoundsException e) {
/* 576*/          this.next = -2;
/* 577*/          if (this.current < 0)
/* 578*/            throw MathRuntimeException.createNoSuchElementException(LocalizedFormats.ITERATOR_EXHAUSTED, new Object[0]); 
/*   0*/        } 
/*   0*/        return;
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 595*/    stream.defaultReadObject();
/* 596*/    this.count = 0;
/*   0*/  }
/*   0*/}
