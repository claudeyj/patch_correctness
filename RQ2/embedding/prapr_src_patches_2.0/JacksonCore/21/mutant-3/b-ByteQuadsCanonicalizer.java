/*   0*/package com.fasterxml.jackson.core.sym;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.util.InternCache;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public final class ByteQuadsCanonicalizer {
/*   0*/  private static final int DEFAULT_T_SIZE = 64;
/*   0*/  
/*   0*/  private static final int MAX_T_SIZE = 65536;
/*   0*/  
/*   0*/  private static final int MIN_HASH_SIZE = 16;
/*   0*/  
/*   0*/  static final int MAX_ENTRIES_FOR_REUSE = 6000;
/*   0*/  
/*   0*/  private final ByteQuadsCanonicalizer _parent;
/*   0*/  
/*   0*/  private final AtomicReference<TableInfo> _tableInfo;
/*   0*/  
/*   0*/  private final int _seed;
/*   0*/  
/*   0*/  private boolean _intern;
/*   0*/  
/*   0*/  private final boolean _failOnDoS;
/*   0*/  
/*   0*/  private int[] _hashArea;
/*   0*/  
/*   0*/  private int _hashSize;
/*   0*/  
/*   0*/  private int _secondaryStart;
/*   0*/  
/*   0*/  private int _tertiaryStart;
/*   0*/  
/*   0*/  private int _tertiaryShift;
/*   0*/  
/*   0*/  private int _count;
/*   0*/  
/*   0*/  private String[] _names;
/*   0*/  
/*   0*/  private int _spilloverEnd;
/*   0*/  
/*   0*/  private int _longNameOffset;
/*   0*/  
/*   0*/  private transient boolean _needRehash;
/*   0*/  
/*   0*/  private boolean _hashShared;
/*   0*/  
/*   0*/  private static final int MULT = 33;
/*   0*/  
/*   0*/  private static final int MULT2 = 65599;
/*   0*/  
/*   0*/  private static final int MULT3 = 31;
/*   0*/  
/*   0*/  private ByteQuadsCanonicalizer(int sz, boolean intern, int seed, boolean failOnDoS) {
/* 223*/    this._parent = null;
/* 224*/    this._seed = seed;
/* 225*/    this._intern = intern;
/* 226*/    nukeSymbols(failOnDoS);
/* 228*/    if (sz < 16) {
/* 229*/      sz = 16;
/* 233*/    } else if ((sz & sz - 1) != 0) {
/* 234*/      int curr = 16;
/* 235*/      while (curr < sz) {
/* 236*/          curr += curr; 
/*   0*/         }
/* 238*/      sz = curr;
/*   0*/    } 
/* 241*/    this._tableInfo = new AtomicReference<TableInfo>(TableInfo.createInitial(sz));
/*   0*/  }
/*   0*/  
/*   0*/  private ByteQuadsCanonicalizer(ByteQuadsCanonicalizer parent, boolean intern, int seed, boolean failOnDoS, TableInfo state) {
/* 250*/    this._parent = parent;
/* 251*/    this._seed = seed;
/* 252*/    this._intern = intern;
/* 253*/    this._failOnDoS = failOnDoS;
/* 254*/    this._tableInfo = null;
/* 257*/    this._count = state.count;
/* 258*/    this._hashSize = state.size;
/* 259*/    this._secondaryStart = this._hashSize << 2;
/* 260*/    this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/* 261*/    this._tertiaryShift = state.tertiaryShift;
/* 263*/    this._hashArea = state.mainHash;
/* 264*/    this._names = state.names;
/* 266*/    this._spilloverEnd = state.spilloverEnd;
/* 267*/    this._longNameOffset = state.longNameOffset;
/* 270*/    this._needRehash = false;
/* 271*/    this._hashShared = true;
/*   0*/  }
/*   0*/  
/*   0*/  public static ByteQuadsCanonicalizer createRoot() {
/* 287*/    long now = System.currentTimeMillis();
/* 289*/    int seed = (int)now + (int)(now >>> 32L) | 0x1;
/* 290*/    return createRoot(seed);
/*   0*/  }
/*   0*/  
/*   0*/  protected static ByteQuadsCanonicalizer createRoot(int seed) {
/* 296*/    return new ByteQuadsCanonicalizer(64, true, seed, true);
/*   0*/  }
/*   0*/  
/*   0*/  public ByteQuadsCanonicalizer makeChild(int flags) {
/* 304*/    return new ByteQuadsCanonicalizer(this, JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(flags), this._seed, JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags), this._tableInfo.get());
/*   0*/  }
/*   0*/  
/*   0*/  public void release() {
/* 320*/    if (this._parent != null && maybeDirty()) {
/* 321*/      this._parent.mergeChild(new TableInfo(this));
/* 324*/      this._hashShared = true;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void mergeChild(TableInfo childState) {
/* 330*/    int childCount = childState.count;
/* 331*/    TableInfo currState = this._tableInfo.get();
/* 335*/    if (childCount == currState.count) {
/*   0*/        return; 
/*   0*/       }
/* 343*/    if (childCount > 6000) {
/* 345*/        childState = TableInfo.createInitial(64); 
/*   0*/       }
/* 347*/    this._tableInfo.compareAndSet(currState, childState);
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 358*/    if (this._tableInfo != null) {
/* 359*/        return ((TableInfo)this._tableInfo.get()).count; 
/*   0*/       }
/* 362*/    return this._count;
/*   0*/  }
/*   0*/  
/*   0*/  public int bucketCount() {
/* 368*/    return this._hashSize;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean maybeDirty() {
/* 375*/    return !this._hashShared;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashSeed() {
/* 377*/    return this._seed;
/*   0*/  }
/*   0*/  
/*   0*/  public int primaryCount() {
/* 386*/    int count = 0;
/* 387*/    for (int offset = 3, end = this._secondaryStart; offset < end; offset += 4) {
/* 388*/      if (this._hashArea[offset] != 0) {
/* 389*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 392*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int secondaryCount() {
/* 400*/    int count = 0;
/* 401*/    int offset = this._secondaryStart + 3;
/* 402*/    for (int end = this._tertiaryStart; offset < end; offset += 4) {
/* 403*/      if (this._hashArea[offset] != 0) {
/* 404*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 407*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int tertiaryCount() {
/* 415*/    int count = 0;
/* 416*/    int offset = this._tertiaryStart + 3;
/* 417*/    for (int end = offset + this._hashSize; offset < end; offset += 4) {
/* 418*/      if (this._hashArea[offset] != 0) {
/* 419*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 422*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int spilloverCount() {
/* 431*/    return this._spilloverEnd - _spilloverStart() >> 2;
/*   0*/  }
/*   0*/  
/*   0*/  public int totalCount() {
/* 436*/    int count = 0;
/* 437*/    for (int offset = 3, end = this._hashSize << 3; offset < end; offset += 4) {
/* 438*/      if (this._hashArea[offset] != 0) {
/* 439*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 442*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 447*/    int pri = primaryCount();
/* 448*/    int sec = secondaryCount();
/* 449*/    int tert = tertiaryCount();
/* 450*/    int spill = spilloverCount();
/* 451*/    int total = totalCount();
/* 452*/    return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", new Object[] { getClass().getName(), this._count, this._hashSize, pri, sec, tert, spill, pri + sec + tert + spill, total });
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int q1) {
/* 465*/    int offset = _calcOffset(calcHash(q1));
/* 467*/    int[] hashArea = this._hashArea;
/* 469*/    int len = hashArea[offset + 3];
/* 471*/    if (len == 1) {
/* 472*/      if (hashArea[offset] == q1) {
/* 473*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 475*/    } else if (len == 0) {
/* 476*/      return null;
/*   0*/    } 
/* 479*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 481*/    len = hashArea[offset2 + 3];
/* 483*/    if (len == 1) {
/* 484*/      if (hashArea[offset2] == q1) {
/* 485*/          return this._names[offset2 >> 2]; 
/*   0*/         }
/* 487*/    } else if (len == 0) {
/* 488*/      return null;
/*   0*/    } 
/* 492*/    return _findSecondary(offset, q1);
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int q1, int q2) {
/* 497*/    int offset = _calcOffset(calcHash(q1, q2));
/* 499*/    int[] hashArea = this._hashArea;
/* 501*/    int len = hashArea[offset + 3];
/* 503*/    if (len == 2) {
/* 504*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1]) {
/* 505*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 507*/    } else if (len == 0) {
/* 508*/      return null;
/*   0*/    } 
/* 511*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 513*/    len = hashArea[offset2 + 3];
/* 515*/    if (len == 2) {
/* 516*/      if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1]) {
/* 517*/          return this._names[offset2 >> 2]; 
/*   0*/         }
/* 519*/    } else if (len == 0) {
/* 520*/      return null;
/*   0*/    } 
/* 522*/    return _findSecondary(offset, q1, q2);
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int q1, int q2, int q3) {
/* 527*/    int offset = _calcOffset(calcHash(q1, q2, q3));
/* 528*/    int[] hashArea = this._hashArea;
/* 529*/    int len = hashArea[offset + 3];
/* 531*/    if (len == 3) {
/* 532*/      if (q1 == hashArea[offset] && hashArea[offset + 1] == q2 && hashArea[offset + 2] == q3) {
/* 533*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 535*/    } else if (len == 0) {
/* 536*/      return null;
/*   0*/    } 
/* 539*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 541*/    len = hashArea[offset2 + 3];
/* 543*/    if (len == 3) {
/* 544*/      if (q1 == hashArea[offset2] && hashArea[offset2 + 1] == q2 && hashArea[offset2 + 2] == q3) {
/* 545*/          return this._names[offset2 >> 2]; 
/*   0*/         }
/* 547*/    } else if (len == 0) {
/* 548*/      return null;
/*   0*/    } 
/* 550*/    return _findSecondary(offset, q1, q2, q3);
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int[] q, int qlen) {
/* 559*/    if (qlen < 4) {
/* 560*/      if (qlen == 3) {
/* 561*/          return findName(q[0], q[1], q[2]); 
/*   0*/         }
/* 563*/      if (qlen == 2) {
/* 564*/          return findName(q[0], q[1]); 
/*   0*/         }
/* 566*/      return findName(q[0]);
/*   0*/    } 
/* 568*/    int hash = calcHash(q, qlen);
/* 569*/    int offset = _calcOffset(hash);
/* 571*/    int[] hashArea = this._hashArea;
/* 573*/    int len = hashArea[offset + 3];
/* 575*/    if (hash == hashArea[offset] && len == qlen) {
/* 577*/        if (_verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 578*/            return this._names[offset >> 2]; 
/*   0*/           } 
/*   0*/       }
/* 581*/    if (len == 0) {
/* 582*/        return null; 
/*   0*/       }
/* 585*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 587*/    int len2 = hashArea[offset2 + 3];
/* 588*/    if (hash == hashArea[offset2] && len2 == qlen && 
/* 589*/      _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
/* 590*/        return this._names[offset2 >> 2]; 
/*   0*/       }
/* 593*/    return _findSecondary(offset, hash, q, qlen);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _calcOffset(int hash) {
/* 601*/    int ix = hash & this._hashSize - 1;
/* 603*/    return ix << 2;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1) {
/* 618*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 619*/    int[] hashArea = this._hashArea;
/* 620*/    int bucketSize = 1 << this._tertiaryShift;
/* 621*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 622*/      int len = hashArea[offset + 3];
/* 623*/      if (q1 == hashArea[offset] && 1 == len) {
/* 624*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 626*/      if (len == 0) {
/* 627*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 633*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 634*/      if (q1 == hashArea[offset] && 1 == hashArea[offset + 3]) {
/* 635*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 638*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1, int q2) {
/* 643*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 644*/    int[] hashArea = this._hashArea;
/* 646*/    int bucketSize = 1 << this._tertiaryShift;
/* 647*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 648*/      int len = hashArea[offset + 3];
/* 649*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
/* 650*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 652*/      if (len == 0) {
/* 653*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 656*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 657*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == hashArea[offset + 3]) {
/* 658*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 661*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1, int q2, int q3) {
/* 666*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 667*/    int[] hashArea = this._hashArea;
/* 669*/    int bucketSize = 1 << this._tertiaryShift;
/* 670*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 671*/      int len = hashArea[offset + 3];
/* 672*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
/* 673*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 675*/      if (len == 0) {
/* 676*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 679*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 680*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == hashArea[offset + 3]) {
/* 682*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 685*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
/* 690*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 691*/    int[] hashArea = this._hashArea;
/* 693*/    int bucketSize = 1 << this._tertiaryShift;
/* 694*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 695*/      int len = hashArea[offset + 3];
/* 696*/      if (hash == hashArea[offset] && qlen == len && 
/* 697*/        _verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 698*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 701*/      if (len == 0) {
/* 702*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 705*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 706*/      if (hash == hashArea[offset] && qlen == hashArea[offset + 3] && 
/* 707*/        _verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 708*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 712*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyLongName(int[] q, int qlen, int spillOffset) {
/* 717*/    int[] hashArea = this._hashArea;
/* 719*/    int ix = 0;
/* 721*/    switch (qlen) {
/*   0*/      default:
/* 723*/        return _verifyLongName2(q, qlen, spillOffset);
/*   0*/      case 8:
/* 725*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 725*/            return false; 
/*   0*/           }
/*   0*/      case 7:
/* 727*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 727*/            return false; 
/*   0*/           }
/*   0*/      case 6:
/* 729*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 729*/            return false; 
/*   0*/           }
/*   0*/      case 5:
/* 731*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 731*/            return false; 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 4:
/*   0*/        break;
/*   0*/    } 
/* 733*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 733*/        return false; 
/*   0*/       }
/* 734*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 734*/        return false; 
/*   0*/       }
/* 735*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 735*/        return false; 
/*   0*/       }
/* 736*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 736*/        return false; 
/*   0*/       }
/* 738*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
/* 743*/    int ix = 0;
/*   0*/    while (true) {
/* 745*/      if (q[ix++] != this._hashArea[spillOffset++]) {
/* 746*/          return false; 
/*   0*/         }
/* 748*/      if (ix >= qlen) {
/* 749*/          return true; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1) {
/* 759*/    _verifySharing();
/* 760*/    if (this._intern) {
/* 761*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 763*/    int offset = _findOffsetForAdd(calcHash(q1));
/* 764*/    this._hashArea[offset] = q1;
/* 765*/    this._hashArea[offset + 3] = 1;
/* 766*/    this._names[offset >> 2] = name;
/* 767*/    this._count++;
/* 768*/    _verifyNeedForRehash();
/* 769*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1, int q2) {
/* 773*/    _verifySharing();
/* 774*/    if (this._intern) {
/* 775*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 777*/    int hash = (q2 == 0) ? calcHash(q1) : calcHash(q1, q2);
/* 778*/    int offset = _findOffsetForAdd(hash);
/* 779*/    this._hashArea[offset] = q1;
/* 780*/    this._hashArea[offset + 1] = q2;
/* 781*/    this._hashArea[offset + 3] = 2;
/* 782*/    this._names[offset >> 2] = name;
/* 783*/    this._count++;
/* 784*/    _verifyNeedForRehash();
/* 785*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1, int q2, int q3) {
/* 789*/    _verifySharing();
/* 790*/    if (this._intern) {
/* 791*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 793*/    int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
/* 794*/    this._hashArea[offset] = q1;
/* 795*/    this._hashArea[offset + 1] = q2;
/* 796*/    this._hashArea[offset + 2] = q3;
/* 797*/    this._hashArea[offset + 3] = 3;
/* 798*/    this._names[offset >> 2] = name;
/* 799*/    this._count++;
/* 800*/    _verifyNeedForRehash();
/* 801*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int[] q, int qlen) {
/*   0*/    int offset, hash, longStart;
/* 806*/    _verifySharing();
/* 807*/    if (this._intern) {
/* 808*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 812*/    switch (qlen) {
/*   0*/      case 1:
/* 815*/        offset = _findOffsetForAdd(calcHash(q[0]));
/* 816*/        this._hashArea[offset] = q[0];
/* 817*/        this._hashArea[offset + 3] = 1;
/*   0*/        break;
/*   0*/      case 2:
/* 822*/        offset = _findOffsetForAdd(calcHash(q[0], q[1]));
/* 823*/        this._hashArea[offset] = q[0];
/* 824*/        this._hashArea[offset + 1] = q[1];
/* 825*/        this._hashArea[offset + 3] = 2;
/*   0*/        break;
/*   0*/      case 3:
/* 830*/        offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2]));
/* 831*/        this._hashArea[offset] = q[0];
/* 832*/        this._hashArea[offset + 1] = q[1];
/* 833*/        this._hashArea[offset + 2] = q[2];
/* 834*/        this._hashArea[offset + 3] = 3;
/*   0*/        break;
/*   0*/      default:
/* 838*/        hash = calcHash(q, qlen);
/* 839*/        offset = _findOffsetForAdd(hash);
/* 841*/        this._hashArea[offset] = hash;
/* 842*/        longStart = _appendLongName(q, qlen);
/* 843*/        this._hashArea[offset + 1] = longStart;
/* 844*/        this._hashArea[offset + 3] = qlen;
/*   0*/        break;
/*   0*/    } 
/* 847*/    this._names[offset >> 2] = name;
/* 850*/    this._count++;
/* 851*/    _verifyNeedForRehash();
/* 852*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  private void _verifyNeedForRehash() {
/* 857*/    if (this._count > this._hashSize >> 1) {
/* 858*/      int spillCount = this._spilloverEnd - _spilloverStart() >> 2;
/* 859*/      if (spillCount > 1 + this._count >> 7 || this._count > this._hashSize * 0.8D) {
/* 861*/          this._needRehash = true; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _verifySharing() {
/* 868*/    if (this._hashShared) {
/* 869*/      this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
/* 870*/      this._names = Arrays.<String>copyOf(this._names, this._names.length);
/* 871*/      this._hashShared = false;
/* 874*/      _verifyNeedForRehash();
/*   0*/    } 
/* 876*/    if (this._needRehash) {
/* 877*/        rehash(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private int _findOffsetForAdd(int hash) {
/* 887*/    int offset = _calcOffset(hash);
/* 888*/    int[] hashArea = this._hashArea;
/* 889*/    if (hashArea[offset + 3] == 0) {
/* 891*/        return offset; 
/*   0*/       }
/* 894*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 895*/    if (hashArea[offset2 + 3] == 0) {
/* 897*/        return offset2; 
/*   0*/       }
/* 901*/    offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 902*/    int bucketSize = 1 << this._tertiaryShift;
/*   0*/    int end;
/* 903*/    for (end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/* 904*/      if (hashArea[offset2 + 3] == 0) {
/* 906*/          return offset2; 
/*   0*/         }
/*   0*/    } 
/* 911*/    offset = this._spilloverEnd;
/* 912*/    this._spilloverEnd += 4;
/* 922*/    end = this._hashSize << 3;
/* 923*/    if (this._spilloverEnd >= end) {
/* 924*/      if (this._failOnDoS) {
/* 925*/          _reportTooManyCollisions(); 
/*   0*/         }
/* 929*/      this._needRehash = true;
/*   0*/    } 
/* 931*/    return offset;
/*   0*/  }
/*   0*/  
/*   0*/  private int _appendLongName(int[] quads, int qlen) {
/* 936*/    int start = this._longNameOffset;
/* 939*/    if (start + qlen > this._hashArea.length) {
/* 941*/      int toAdd = start + qlen - this._hashArea.length;
/* 943*/      int minAdd = Math.min(4096, this._hashSize);
/* 945*/      int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
/* 946*/      this._hashArea = Arrays.copyOf(this._hashArea, newSize);
/*   0*/    } 
/* 948*/    System.arraycopy(quads, 0, this._hashArea, start, qlen);
/* 949*/    this._longNameOffset += qlen;
/* 950*/    return start;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1) {
/* 975*/    int hash = q1 ^ this._seed;
/* 981*/    hash += hash >>> 16;
/* 982*/    hash ^= hash << 3;
/* 983*/    hash += hash >>> 12;
/* 984*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1, int q2) {
/* 991*/    int hash = q1;
/* 993*/    hash += hash >>> 15;
/* 994*/    hash ^= hash >>> 9;
/* 995*/    hash += q2 * 33;
/* 996*/    hash ^= this._seed;
/* 997*/    hash += hash >>> 16;
/* 998*/    hash ^= hash >>> 4;
/* 999*/    hash += hash << 3;
/*1001*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1, int q2, int q3) {
/*1006*/    int hash = q1 ^ this._seed;
/*1007*/    hash += hash >>> 9;
/*1008*/    hash *= 31;
/*1009*/    hash += q2;
/*1010*/    hash *= 33;
/*1011*/    hash += hash >>> 15;
/*1012*/    hash ^= q3;
/*1014*/    hash += hash >>> 4;
/*1016*/    hash += hash >>> 15;
/*1017*/    hash ^= hash << 9;
/*1019*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int[] q, int qlen) {
/*1024*/    if (qlen < 4) {
/*1025*/        throw new IllegalArgumentException(); 
/*   0*/       }
/*1032*/    int hash = q[0] ^ this._seed;
/*1033*/    hash += hash >>> 9;
/*1034*/    hash += q[1];
/*1035*/    hash += hash >>> 15;
/*1036*/    hash *= 33;
/*1037*/    hash ^= q[2];
/*1038*/    hash += hash >>> 4;
/*1040*/    for (int i = 3; i < qlen; i++) {
/*1041*/      int next = q[i];
/*1042*/      next ^= next >> 21;
/*1043*/      hash += next;
/*   0*/    } 
/*1045*/    hash *= 65599;
/*1048*/    hash += hash >>> 19;
/*1049*/    hash ^= hash << 5;
/*1050*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  private void rehash() {
/*1061*/    this._needRehash = false;
/*1063*/    this._hashShared = false;
/*1067*/    int[] oldHashArea = this._hashArea;
/*1068*/    String[] oldNames = this._names;
/*1069*/    int oldSize = this._hashSize;
/*1070*/    int oldCount = this._count;
/*1071*/    int newSize = oldSize + oldSize;
/*1072*/    int oldEnd = this._spilloverEnd;
/*1077*/    if (newSize > 65536) {
/*1078*/      nukeSymbols(true);
/*   0*/      return;
/*   0*/    } 
/*1082*/    this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
/*1083*/    this._hashSize = newSize;
/*1084*/    this._secondaryStart = newSize << 2;
/*1085*/    this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/*1086*/    this._tertiaryShift = _calcTertiaryShift(newSize);
/*1089*/    this._names = new String[oldNames.length << 1];
/*1090*/    nukeSymbols(false);
/*1097*/    int copyCount = 0;
/*1098*/    int[] q = new int[16];
/*1099*/    for (int offset = 0, end = oldEnd; offset < end; offset += 4) {
/*1100*/      int len = oldHashArea[offset + 3];
/*1101*/      if (len != 0) {
/*   0*/        int qoff;
/*1104*/        copyCount++;
/*1105*/        String name = oldNames[offset >> 2];
/*1106*/        switch (len) {
/*   0*/          case 1:
/*1108*/            q[0] = oldHashArea[offset];
/*1109*/            addName(name, q, 1);
/*   0*/            break;
/*   0*/          case 2:
/*1112*/            q[0] = oldHashArea[offset];
/*1113*/            q[1] = oldHashArea[offset + 1];
/*1114*/            addName(name, q, 2);
/*   0*/            break;
/*   0*/          case 3:
/*1117*/            q[0] = oldHashArea[offset];
/*1118*/            q[1] = oldHashArea[offset + 1];
/*1119*/            q[2] = oldHashArea[offset + 2];
/*1120*/            addName(name, q, 3);
/*   0*/            break;
/*   0*/          default:
/*1123*/            if (len > q.length) {
/*1124*/                q = new int[len]; 
/*   0*/               }
/*1127*/            qoff = oldHashArea[offset + 1];
/*1128*/            System.arraycopy(oldHashArea, qoff, q, 0, len);
/*1129*/            addName(name, q, len);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1136*/    if (copyCount != oldCount) {
/*1137*/        throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void nukeSymbols(boolean fill) {
/*1146*/    this._count = 0;
/*1148*/    this._spilloverEnd = _spilloverStart();
/*1150*/    this._longNameOffset = this._hashSize << 3;
/*1151*/    if (fill) {
/*1152*/      Arrays.fill(this._hashArea, 0);
/*1153*/      Arrays.fill((Object[])this._names, null);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final int _spilloverStart() {
/*1169*/    int offset = this._hashSize;
/*1170*/    return (offset << 3) - offset;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportTooManyCollisions() {
/*1176*/    if (this._hashSize <= 1024) {
/*   0*/        return; 
/*   0*/       }
/*1179*/    throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions." + " You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
/*   0*/  }
/*   0*/  
/*   0*/  static int _calcTertiaryShift(int primarySlots) {
/*1188*/    int tertSlots = primarySlots >> 2;
/*1191*/    if (tertSlots < 64) {
/*1192*/        return 4; 
/*   0*/       }
/*1194*/    if (tertSlots <= 256) {
/*1195*/        return 5; 
/*   0*/       }
/*1197*/    if (tertSlots <= 1024) {
/*1198*/        return 6; 
/*   0*/       }
/*1201*/    return 7;
/*   0*/  }
/*   0*/  
/*   0*/  private static final class TableInfo {
/*   0*/    public final int size;
/*   0*/    
/*   0*/    public final int count;
/*   0*/    
/*   0*/    public final int tertiaryShift;
/*   0*/    
/*   0*/    public final int[] mainHash;
/*   0*/    
/*   0*/    public final String[] names;
/*   0*/    
/*   0*/    public final int spilloverEnd;
/*   0*/    
/*   0*/    public final int longNameOffset;
/*   0*/    
/*   0*/    public TableInfo(int size, int count, int tertiaryShift, int[] mainHash, String[] names, int spilloverEnd, int longNameOffset) {
/*1230*/      this.size = size;
/*1231*/      this.count = count;
/*1232*/      this.tertiaryShift = tertiaryShift;
/*1233*/      this.mainHash = mainHash;
/*1234*/      this.names = names;
/*1235*/      this.spilloverEnd = spilloverEnd;
/*1236*/      this.longNameOffset = longNameOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public TableInfo(ByteQuadsCanonicalizer src) {
/*1241*/      this.size = src._hashSize;
/*1242*/      this.count = src._count;
/*1243*/      this.tertiaryShift = src._tertiaryShift;
/*1244*/      this.mainHash = src._hashArea;
/*1245*/      this.names = src._names;
/*1246*/      this.spilloverEnd = src._spilloverEnd;
/*1247*/      this.longNameOffset = src._longNameOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public static TableInfo createInitial(int sz) {
/*1251*/      int hashAreaSize = sz << 3;
/*1252*/      int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
/*1254*/      return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
/*   0*/    }
/*   0*/  }
/*   0*/}
