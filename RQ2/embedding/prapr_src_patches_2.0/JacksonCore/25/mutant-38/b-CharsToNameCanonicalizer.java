/*   0*/package com.fasterxml.jackson.core.sym;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.util.InternCache;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.BitSet;
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public final class CharsToNameCanonicalizer {
/*   0*/  public static final int HASH_MULT = 33;
/*   0*/  
/*   0*/  private static final int DEFAULT_T_SIZE = 64;
/*   0*/  
/*   0*/  private static final int MAX_T_SIZE = 65536;
/*   0*/  
/*   0*/  static final int MAX_ENTRIES_FOR_REUSE = 12000;
/*   0*/  
/*   0*/  static final int MAX_COLL_CHAIN_LENGTH = 100;
/*   0*/  
/*   0*/  private final CharsToNameCanonicalizer _parent;
/*   0*/  
/*   0*/  private final AtomicReference<TableInfo> _tableInfo;
/*   0*/  
/*   0*/  private final int _seed;
/*   0*/  
/*   0*/  private final int _flags;
/*   0*/  
/*   0*/  private boolean _canonicalize;
/*   0*/  
/*   0*/  private String[] _symbols;
/*   0*/  
/*   0*/  private Bucket[] _buckets;
/*   0*/  
/*   0*/  private int _size;
/*   0*/  
/*   0*/  private int _sizeThreshold;
/*   0*/  
/*   0*/  private int _indexMask;
/*   0*/  
/*   0*/  private int _longestCollisionList;
/*   0*/  
/*   0*/  private boolean _hashShared;
/*   0*/  
/*   0*/  private BitSet _overflows;
/*   0*/  
/*   0*/  private CharsToNameCanonicalizer(int seed) {
/* 233*/    this._parent = null;
/* 234*/    this._seed = seed;
/* 237*/    this._canonicalize = true;
/* 238*/    this._flags = -1;
/* 240*/    this._hashShared = false;
/* 241*/    this._longestCollisionList = 0;
/* 243*/    this._tableInfo = new AtomicReference<TableInfo>(TableInfo.createInitial(64));
/*   0*/  }
/*   0*/  
/*   0*/  private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, int seed, TableInfo parentState) {
/* 254*/    this._parent = parent;
/* 255*/    this._seed = seed;
/* 256*/    this._tableInfo = null;
/* 257*/    this._flags = flags;
/* 258*/    this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
/* 261*/    this._symbols = parentState.symbols;
/* 262*/    this._buckets = parentState.buckets;
/* 264*/    this._size = parentState.size;
/* 265*/    this._longestCollisionList = parentState.longestCollisionList;
/* 268*/    int arrayLen = this._symbols.length;
/* 269*/    this._sizeThreshold = _thresholdSize(arrayLen);
/* 270*/    this._indexMask = arrayLen - 1;
/* 273*/    this._hashShared = true;
/*   0*/  }
/*   0*/  
/*   0*/  private static int _thresholdSize(int hashAreaSize) {
/* 276*/    return hashAreaSize - (hashAreaSize >> 2);
/*   0*/  }
/*   0*/  
/*   0*/  public static CharsToNameCanonicalizer createRoot() {
/* 293*/    long now = System.currentTimeMillis();
/* 295*/    int seed = (int)now + (int)(now >>> 32L) | 0x1;
/* 296*/    int i = seed;
/* 296*/    return createRoot();
/*   0*/  }
/*   0*/  
/*   0*/  protected static CharsToNameCanonicalizer createRoot(int seed) {
/* 300*/    return new CharsToNameCanonicalizer(seed);
/*   0*/  }
/*   0*/  
/*   0*/  public CharsToNameCanonicalizer makeChild(int flags) {
/* 315*/    return new CharsToNameCanonicalizer(this, flags, this._seed, this._tableInfo.get());
/*   0*/  }
/*   0*/  
/*   0*/  public void release() {
/* 326*/    if (!maybeDirty()) {
/*   0*/        return; 
/*   0*/       }
/* 329*/    if (this._parent != null && this._canonicalize) {
/* 330*/      this._parent.mergeChild(new TableInfo(this));
/* 333*/      this._hashShared = true;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void mergeChild(TableInfo childState) {
/* 346*/    int childCount = childState.size;
/* 347*/    TableInfo currState = this._tableInfo.get();
/* 351*/    if (childCount == currState.size) {
/*   0*/        return; 
/*   0*/       }
/* 358*/    if (childCount > 12000) {
/* 360*/        childState = TableInfo.createInitial(64); 
/*   0*/       }
/* 362*/    this._tableInfo.compareAndSet(currState, childState);
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 372*/    if (this._tableInfo != null) {
/* 373*/        return ((TableInfo)this._tableInfo.get()).size; 
/*   0*/       }
/* 376*/    return this._size;
/*   0*/  }
/*   0*/  
/*   0*/  public int bucketCount() {
/* 385*/    return this._symbols.length;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean maybeDirty() {
/* 386*/    return !this._hashShared;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashSeed() {
/* 387*/    return this._seed;
/*   0*/  }
/*   0*/  
/*   0*/  public int collisionCount() {
/* 397*/    int count = 0;
/* 399*/    for (Bucket bucket : this._buckets) {
/* 400*/      if (bucket != null) {
/* 401*/          count += bucket.length; 
/*   0*/         }
/*   0*/    } 
/* 404*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int maxCollisionLength() {
/* 414*/    return this._longestCollisionList;
/*   0*/  }
/*   0*/  
/*   0*/  public String findSymbol(char[] buffer, int start, int len, int h) {
/* 424*/    if (len < 1) {
/* 425*/        return ""; 
/*   0*/       }
/* 427*/    if (!this._canonicalize) {
/* 428*/        return new String(buffer, start, len); 
/*   0*/       }
/* 436*/    int index = _hashToIndex(h);
/* 437*/    String sym = this._symbols[index];
/* 440*/    if (sym != null) {
/* 442*/      if (sym.length() == len) {
/* 443*/        int i = 0;
/* 444*/        while (sym.charAt(i) == buffer[start + i]) {
/* 446*/          if (++i == len) {
/* 447*/              return sym; 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/* 451*/      Bucket b = this._buckets[index >> 1];
/* 452*/      if (b != null) {
/* 453*/        sym = b.has(buffer, start, len);
/* 454*/        if (sym != null) {
/* 455*/            return sym; 
/*   0*/           }
/* 457*/        sym = _findSymbol2(buffer, start, len, b.next);
/* 458*/        if (sym != null) {
/* 459*/            return sym; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 463*/    return _addSymbol(buffer, start, len, h, index);
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
/* 467*/    while (b != null) {
/* 468*/      String sym = b.has(buffer, start, len);
/* 469*/      if (sym != null) {
/* 470*/          return sym; 
/*   0*/         }
/* 472*/      b = b.next;
/*   0*/    } 
/* 474*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _addSymbol(char[] buffer, int start, int len, int h, int index) {
/* 479*/    if (this._hashShared) {
/* 480*/      copyArrays();
/* 481*/      this._hashShared = false;
/* 482*/    } else if (this._size >= this._sizeThreshold) {
/* 483*/      rehash();
/* 487*/      index = _hashToIndex(calcHash(buffer, start, len));
/*   0*/    } 
/* 490*/    String newSymbol = new String(buffer, start, len);
/* 491*/    if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
/* 492*/        newSymbol = InternCache.instance.intern(newSymbol); 
/*   0*/       }
/* 494*/    this._size++;
/* 496*/    if (this._symbols[index] == null) {
/* 497*/      this._symbols[index] = newSymbol;
/*   0*/    } else {
/* 499*/      int bix = index >> 1;
/* 500*/      Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
/* 501*/      int collLen = newB.length;
/* 502*/      if (collLen > 100) {
/* 505*/        _handleSpillOverflow(bix, newB);
/*   0*/      } else {
/* 507*/        this._buckets[bix] = newB;
/* 508*/        this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
/*   0*/      } 
/*   0*/    } 
/* 511*/    return newSymbol;
/*   0*/  }
/*   0*/  
/*   0*/  private void _handleSpillOverflow(int bindex, Bucket newBucket) {
/* 516*/    if (this._overflows == null) {
/* 517*/      this._overflows = new BitSet();
/* 518*/      this._overflows.set(bindex);
/* 520*/    } else if (this._overflows.get(bindex)) {
/* 522*/      if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
/* 523*/          reportTooManyCollisions(100); 
/*   0*/         }
/* 526*/      this._canonicalize = false;
/*   0*/    } else {
/* 528*/      this._overflows.set(bindex);
/*   0*/    } 
/* 532*/    this._symbols[bindex + bindex] = newBucket.symbol;
/* 533*/    this._buckets[bindex] = null;
/* 535*/    this._size -= newBucket.length;
/* 537*/    this._longestCollisionList = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int _hashToIndex(int rawHash) {
/* 546*/    rawHash += rawHash >>> 15;
/* 547*/    rawHash ^= rawHash << 7;
/* 548*/    rawHash += rawHash >>> 3;
/* 549*/    return rawHash & this._indexMask;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(char[] buffer, int start, int len) {
/* 562*/    int hash = this._seed;
/* 563*/    for (int i = start, end = start + len; i < end; i++) {
/* 564*/        hash = hash * 33 + buffer[i]; 
/*   0*/       }
/* 567*/    return (hash == 0) ? 1 : hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(String key) {
/* 572*/    int len = key.length();
/* 574*/    int hash = this._seed;
/* 575*/    for (int i = 0; i < len; i++) {
/* 576*/        hash = hash * 33 + key.charAt(i); 
/*   0*/       }
/* 579*/    return (hash == 0) ? 1 : hash;
/*   0*/  }
/*   0*/  
/*   0*/  private void copyArrays() {
/* 593*/    String[] oldSyms = this._symbols;
/* 594*/    this._symbols = Arrays.<String>copyOf(oldSyms, oldSyms.length);
/* 595*/    Bucket[] oldBuckets = this._buckets;
/* 596*/    this._buckets = Arrays.<Bucket>copyOf(oldBuckets, oldBuckets.length);
/*   0*/  }
/*   0*/  
/*   0*/  private void rehash() {
/* 607*/    int size = this._symbols.length;
/* 608*/    int newSize = size + size;
/* 614*/    if (newSize > 65536) {
/* 617*/      this._size = 0;
/* 618*/      this._canonicalize = false;
/* 620*/      this._symbols = new String[64];
/* 621*/      this._buckets = new Bucket[32];
/* 622*/      this._indexMask = 63;
/* 623*/      this._hashShared = false;
/*   0*/      return;
/*   0*/    } 
/* 627*/    String[] oldSyms = this._symbols;
/* 628*/    Bucket[] oldBuckets = this._buckets;
/* 629*/    this._symbols = new String[newSize];
/* 630*/    this._buckets = new Bucket[newSize >> 1];
/* 632*/    this._indexMask = newSize - 1;
/* 633*/    this._sizeThreshold = _thresholdSize(newSize);
/* 635*/    int count = 0;
/* 639*/    int maxColl = 0;
/* 640*/    for (int i = 0; i < size; i++) {
/* 641*/      String symbol = oldSyms[i];
/* 642*/      if (symbol != null) {
/* 643*/        count++;
/* 644*/        int index = _hashToIndex(calcHash(symbol));
/* 645*/        if (this._symbols[index] == null) {
/* 646*/          this._symbols[index] = symbol;
/*   0*/        } else {
/* 648*/          int bix = index >> 1;
/* 649*/          Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 650*/          this._buckets[bix] = newB;
/* 651*/          maxColl = Math.max(maxColl, newB.length);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 656*/    size >>= 1;
/* 657*/    for (int j = 0; j < size; j++) {
/* 658*/      Bucket b = oldBuckets[j];
/* 659*/      while (b != null) {
/* 660*/        count++;
/* 661*/        String symbol = b.symbol;
/* 662*/        int index = _hashToIndex(calcHash(symbol));
/* 663*/        if (this._symbols[index] == null) {
/* 664*/          this._symbols[index] = symbol;
/*   0*/        } else {
/* 666*/          int bix = index >> 1;
/* 667*/          Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 668*/          this._buckets[bix] = newB;
/* 669*/          maxColl = Math.max(maxColl, newB.length);
/*   0*/        } 
/* 671*/        b = b.next;
/*   0*/      } 
/*   0*/    } 
/* 674*/    this._longestCollisionList = maxColl;
/* 675*/    this._overflows = null;
/* 677*/    if (count != this._size) {
/* 678*/        throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", new Object[] { this._size, count })); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void reportTooManyCollisions(int maxLen) {
/* 688*/    throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
/*   0*/  }
/*   0*/  
/*   0*/  static final class Bucket {
/*   0*/    public final String symbol;
/*   0*/    
/*   0*/    public final Bucket next;
/*   0*/    
/*   0*/    public final int length;
/*   0*/    
/*   0*/    public Bucket(String s, Bucket n) {
/* 758*/      this.symbol = s;
/* 759*/      this.next = n;
/* 760*/      this.length = (n == null) ? 1 : (n.length + 1);
/*   0*/    }
/*   0*/    
/*   0*/    public String has(char[] buf, int start, int len) {
/* 764*/      if (this.symbol.length() != len) {
/* 765*/          return null; 
/*   0*/         }
/* 767*/      int i = 0;
/*   0*/      while (true) {
/* 769*/        if (this.symbol.charAt(i) != buf[start + i]) {
/* 770*/            return null; 
/*   0*/           }
/* 772*/        if (++i >= len) {
/* 773*/            return this.symbol; 
/*   0*/           }
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class TableInfo {
/*   0*/    final int size;
/*   0*/    
/*   0*/    final int longestCollisionList;
/*   0*/    
/*   0*/    final String[] symbols;
/*   0*/    
/*   0*/    final CharsToNameCanonicalizer.Bucket[] buckets;
/*   0*/    
/*   0*/    public TableInfo(int size, int longestCollisionList, String[] symbols, CharsToNameCanonicalizer.Bucket[] buckets) {
/* 794*/      this.size = size;
/* 795*/      this.longestCollisionList = longestCollisionList;
/* 796*/      this.symbols = symbols;
/* 797*/      this.buckets = buckets;
/*   0*/    }
/*   0*/    
/*   0*/    public TableInfo(CharsToNameCanonicalizer src) {
/* 802*/      this.size = src._size;
/* 803*/      this.longestCollisionList = src._longestCollisionList;
/* 804*/      this.symbols = src._symbols;
/* 805*/      this.buckets = src._buckets;
/*   0*/    }
/*   0*/    
/*   0*/    public static TableInfo createInitial(int sz) {
/* 809*/      return new TableInfo(0, 0, new String[sz], new CharsToNameCanonicalizer.Bucket[sz >> 1]);
/*   0*/    }
/*   0*/  }
/*   0*/}
