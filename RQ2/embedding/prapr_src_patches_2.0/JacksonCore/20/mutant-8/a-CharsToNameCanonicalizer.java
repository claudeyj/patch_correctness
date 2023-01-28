/*   0*/package com.fasterxml.jackson.core.sym;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.util.InternCache;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.BitSet;
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
/*  93*/  static final CharsToNameCanonicalizer sBootstrapSymbolTable = new CharsToNameCanonicalizer();
/*   0*/  
/*   0*/  private CharsToNameCanonicalizer _parent;
/*   0*/  
/*   0*/  private final int _hashSeed;
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
/*   0*/  private boolean _dirty;
/*   0*/  
/*   0*/  private BitSet _overflows;
/*   0*/  
/*   0*/  public static CharsToNameCanonicalizer createRoot() {
/* 223*/    long now = System.currentTimeMillis();
/* 225*/    int seed = (int)now + (int)(now >>> 32L) | 0x1;
/* 226*/    return createRoot(seed);
/*   0*/  }
/*   0*/  
/*   0*/  protected static CharsToNameCanonicalizer createRoot(int hashSeed) {
/* 230*/    return sBootstrapSymbolTable.makeOrphan(hashSeed);
/*   0*/  }
/*   0*/  
/*   0*/  private CharsToNameCanonicalizer() {
/* 238*/    this._canonicalize = true;
/* 239*/    this._flags = -1;
/* 241*/    this._dirty = true;
/* 242*/    this._hashSeed = 0;
/* 243*/    this._longestCollisionList = 0;
/* 244*/    initTables(64);
/*   0*/  }
/*   0*/  
/*   0*/  private void initTables(int initialSize) {
/* 249*/    this._symbols = new String[initialSize];
/* 250*/    this._buckets = new Bucket[initialSize >> 1];
/* 252*/    this._indexMask = initialSize - 1;
/* 253*/    this._size = 0;
/* 254*/    this._longestCollisionList = 0;
/* 256*/    this._sizeThreshold = _thresholdSize(initialSize);
/*   0*/  }
/*   0*/  
/*   0*/  private static int _thresholdSize(int hashAreaSize) {
/* 259*/    return hashAreaSize - (hashAreaSize >> 2);
/*   0*/  }
/*   0*/  
/*   0*/  private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, String[] symbols, Bucket[] buckets, int size, int hashSeed, int longestColl) {
/* 266*/    this._parent = parent;
/* 268*/    this._flags = flags;
/* 269*/    this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
/* 271*/    this._symbols = symbols;
/* 272*/    this._buckets = buckets;
/* 273*/    this._size = size;
/* 274*/    this._hashSeed = hashSeed;
/* 276*/    int arrayLen = symbols.length;
/* 277*/    this._sizeThreshold = _thresholdSize(arrayLen);
/* 278*/    this._indexMask = arrayLen - 1;
/* 279*/    this._longestCollisionList = longestColl;
/* 282*/    this._dirty = false;
/*   0*/  }
/*   0*/  
/*   0*/  public CharsToNameCanonicalizer makeChild(int flags) {
/*   0*/    String[] symbols;
/*   0*/    Bucket[] buckets;
/*   0*/    int size, hashSeed, longestCollisionList;
/* 308*/    synchronized (this) {
/* 309*/      symbols = this._symbols;
/* 310*/      buckets = this._buckets;
/* 311*/      size = this._size;
/* 312*/      hashSeed = this._hashSeed;
/* 313*/      longestCollisionList = this._longestCollisionList;
/*   0*/    } 
/* 315*/    return new CharsToNameCanonicalizer(this, flags, symbols, buckets, size, hashSeed, longestCollisionList);
/*   0*/  }
/*   0*/  
/*   0*/  private CharsToNameCanonicalizer makeOrphan(int seed) {
/* 320*/    return new CharsToNameCanonicalizer(null, -1, this._symbols, this._buckets, this._size, seed, this._longestCollisionList);
/*   0*/  }
/*   0*/  
/*   0*/  private void mergeChild(CharsToNameCanonicalizer child) {
/* 337*/    if (child.size() > 12000) {
/* 341*/      synchronized (this) {
/* 342*/        initTables(256);
/* 345*/        this._dirty = false;
/*   0*/      } 
/*   0*/    } else {
/* 350*/      if (child.size() <= size()) {
/*   0*/          return; 
/*   0*/         }
/* 354*/      synchronized (this) {
/* 355*/        this._symbols = child._symbols;
/* 356*/        this._buckets = child._buckets;
/* 357*/        this._size = child._size;
/* 358*/        this._sizeThreshold = child._sizeThreshold;
/* 359*/        this._indexMask = child._indexMask;
/* 360*/        this._longestCollisionList = child._longestCollisionList;
/* 363*/        this._dirty = false;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void release() {
/* 370*/    if (!maybeDirty()) {
/*   0*/        return; 
/*   0*/       }
/* 371*/    if (this._parent != null && this._canonicalize) {
/* 372*/      this._parent.mergeChild(this);
/* 377*/      this._dirty = false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 387*/    return this._size;
/*   0*/  }
/*   0*/  
/*   0*/  public int bucketCount() {
/* 395*/    return this._symbols.length;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean maybeDirty() {
/* 396*/    return this._dirty;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashSeed() {
/* 397*/    return this._hashSeed;
/*   0*/  }
/*   0*/  
/*   0*/  public int collisionCount() {
/* 407*/    int count = 0;
/* 409*/    for (Bucket bucket : this._buckets) {
/* 410*/      if (bucket != null) {
/* 411*/          count += bucket.length; 
/*   0*/         }
/*   0*/    } 
/* 414*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int maxCollisionLength() {
/* 424*/    return this._longestCollisionList;
/*   0*/  }
/*   0*/  
/*   0*/  public String findSymbol(char[] buffer, int start, int len, int h) {
/* 434*/    if (len < 1) {
/* 435*/        return ""; 
/*   0*/       }
/* 437*/    if (!this._canonicalize) {
/* 438*/        return new String(buffer, start, len); 
/*   0*/       }
/* 446*/    int index = _hashToIndex(h);
/* 447*/    String sym = this._symbols[index];
/* 450*/    if (sym != null) {
/* 452*/      if (sym.length() == len) {
/* 453*/        int i = 0;
/* 454*/        while (sym.charAt(i) == buffer[start + i]) {
/* 456*/          if (++i == len) {
/* 457*/              return sym; 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/* 461*/      Bucket b = this._buckets[index >> 1];
/* 462*/      if (b != null) {
/* 463*/        sym = b.has(buffer, start, len);
/* 464*/        if (sym != null) {
/* 465*/            return sym; 
/*   0*/           }
/* 467*/        sym = _findSymbol2(buffer, start, len, b.next);
/* 468*/        if (sym != null) {
/* 469*/            return sym; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 473*/    return _addSymbol(buffer, start, len, h, index);
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
/* 477*/    while (b != null) {
/* 478*/      String sym = b.has(buffer, start, len);
/* 479*/      if (sym != null) {
/* 480*/          return sym; 
/*   0*/         }
/* 482*/      b = b.next;
/*   0*/    } 
/* 484*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _addSymbol(char[] buffer, int start, int len, int h, int index) {
/* 489*/    if (!this._dirty) {
/* 490*/      copyArrays();
/* 491*/      this._dirty = true;
/* 492*/    } else if (this._size >= this._sizeThreshold) {
/* 493*/      rehash();
/* 497*/      index = _hashToIndex(calcHash(buffer, start, len));
/*   0*/    } 
/* 500*/    String newSymbol = new String(buffer, start, len);
/* 501*/    if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
/* 502*/        newSymbol = InternCache.instance.intern(newSymbol); 
/*   0*/       }
/* 504*/    this._size++;
/* 506*/    if (this._symbols[index] == null) {
/* 507*/      this._symbols[index] = newSymbol;
/*   0*/    } else {
/* 509*/      int bix = index >> 1;
/* 510*/      Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
/* 511*/      int collLen = newB.length;
/* 512*/      if (collLen > 100) {
/* 516*/        _handleSpillOverflow(bix, newB);
/*   0*/      } else {
/* 518*/        this._buckets[bix] = newB;
/* 519*/        this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
/*   0*/      } 
/*   0*/    } 
/* 523*/    return newSymbol;
/*   0*/  }
/*   0*/  
/*   0*/  private void _handleSpillOverflow(int bindex, Bucket newBucket) {
/* 528*/    if (this._overflows == null) {
/* 529*/      this._overflows = new BitSet();
/* 530*/      this._overflows.set(bindex);
/* 532*/    } else if (this._overflows.get(bindex)) {
/* 534*/      if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
/* 535*/          reportTooManyCollisions(100); 
/*   0*/         }
/* 538*/      this._canonicalize = false;
/*   0*/    } else {
/* 540*/      this._overflows.set(bindex);
/*   0*/    } 
/* 544*/    this._symbols[bindex + bindex] = newBucket.symbol;
/* 545*/    this._buckets[bindex] = null;
/* 547*/    this._size -= newBucket.length;
/* 549*/    this._longestCollisionList = -1;
/*   0*/  }
/*   0*/  
/*   0*/  public int _hashToIndex(int rawHash) {
/* 558*/    rawHash += rawHash >>> 15;
/* 559*/    rawHash ^= rawHash << 7;
/* 560*/    rawHash += rawHash >>> 3;
/* 561*/    return rawHash & this._indexMask;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(char[] buffer, int start, int len) {
/* 574*/    int hash = this._hashSeed;
/* 575*/    for (int i = start, end = start + len; i < end; i++) {
/* 576*/        hash = hash * 33 + buffer[i]; 
/*   0*/       }
/* 579*/    return (hash == 0) ? 1 : hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(String key) {
/* 584*/    int len = key.length();
/* 586*/    int hash = this._hashSeed;
/* 587*/    for (int i = 0; i < len; i++) {
/* 588*/        hash = hash * 33 + key.charAt(i); 
/*   0*/       }
/* 591*/    return (hash == 0) ? 1 : hash;
/*   0*/  }
/*   0*/  
/*   0*/  private void copyArrays() {
/* 605*/    String[] oldSyms = this._symbols;
/* 606*/    this._symbols = Arrays.<String>copyOf(oldSyms, oldSyms.length);
/* 607*/    Bucket[] oldBuckets = this._buckets;
/* 608*/    this._buckets = Arrays.<Bucket>copyOf(oldBuckets, oldBuckets.length);
/*   0*/  }
/*   0*/  
/*   0*/  private void rehash() {
/* 619*/    int size = this._symbols.length;
/* 620*/    int newSize = size + size;
/* 626*/    if (newSize > 65536) {
/* 630*/      this._size = 0;
/* 631*/      this._canonicalize = false;
/* 633*/      this._symbols = new String[64];
/* 634*/      this._buckets = new Bucket[32];
/* 635*/      this._indexMask = 63;
/* 636*/      this._dirty = true;
/*   0*/      return;
/*   0*/    } 
/* 640*/    String[] oldSyms = this._symbols;
/* 641*/    Bucket[] oldBuckets = this._buckets;
/* 642*/    this._symbols = new String[newSize];
/* 643*/    this._buckets = new Bucket[newSize >> 1];
/* 645*/    this._indexMask = newSize - 1;
/* 646*/    this._sizeThreshold = _thresholdSize(newSize);
/* 648*/    int count = 0;
/* 653*/    int maxColl = 0;
/* 654*/    for (int i = 0; i < size; i++) {
/* 655*/      String symbol = oldSyms[i];
/* 656*/      if (symbol != null) {
/* 657*/        count++;
/* 658*/        int index = _hashToIndex(calcHash(symbol));
/* 659*/        if (this._symbols[index] == null) {
/* 660*/          this._symbols[index] = symbol;
/*   0*/        } else {
/* 662*/          int bix = index >> 1;
/* 663*/          Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 664*/          this._buckets[bix] = newB;
/* 665*/          maxColl = Math.max(maxColl, newB.length);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 670*/    size >>= 1;
/* 671*/    for (int j = 0; j < size; j++) {
/* 672*/      Bucket b = oldBuckets[j];
/* 673*/      while (b != null) {
/* 674*/        count++;
/* 675*/        String symbol = b.symbol;
/* 676*/        int index = _hashToIndex(calcHash(symbol));
/* 677*/        if (this._symbols[index] == null) {
/* 678*/          this._symbols[index] = symbol;
/*   0*/        } else {
/* 680*/          int bix = index >> 1;
/* 681*/          Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 682*/          this._buckets[bix] = newB;
/* 683*/          maxColl = Math.max(maxColl, newB.length);
/*   0*/        } 
/* 685*/        b = b.next;
/*   0*/      } 
/*   0*/    } 
/* 688*/    this._longestCollisionList = maxColl;
/* 689*/    this._overflows = null;
/* 691*/    if (count != this._size) {
/* 692*/        throw new Error("Internal error on SymbolTable.rehash(): had " + this._size + " entries; now have " + count + "."); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void reportTooManyCollisions(int maxLen) {
/* 700*/    throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
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
/* 770*/      this.symbol = s;
/* 771*/      this.next = n;
/* 772*/      this.length = (n == null) ? 1 : (n.length + 1);
/*   0*/    }
/*   0*/    
/*   0*/    public String has(char[] buf, int start, int len) {
/* 776*/      if (this.symbol.length() != len) {
/* 777*/          return null; 
/*   0*/         }
/* 779*/      int i = 0;
/*   0*/      while (true) {
/* 781*/        if (this.symbol.charAt(i) != buf[start + i]) {
/* 782*/            return null; 
/*   0*/           }
/* 784*/        if (++i >= len) {
/* 785*/            return this.symbol; 
/*   0*/           }
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/}
