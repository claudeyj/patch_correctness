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
/* 225*/    nukeSymbols(intern);
/* 226*/    this._failOnDoS = failOnDoS;
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
/* 560*/      switch (qlen) {
/*   0*/        case 3:
/* 562*/          return findName(q[0], q[1], q[2]);
/*   0*/        case 2:
/* 564*/          return findName(q[0], q[1]);
/*   0*/        case 1:
/* 566*/          return findName(q[0]);
/*   0*/      } 
/* 568*/      return "";
/*   0*/    } 
/* 571*/    int hash = calcHash(q, qlen);
/* 572*/    int offset = _calcOffset(hash);
/* 574*/    int[] hashArea = this._hashArea;
/* 576*/    int len = hashArea[offset + 3];
/* 578*/    if (hash == hashArea[offset] && len == qlen) {
/* 580*/        if (_verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 581*/            return this._names[offset >> 2]; 
/*   0*/           } 
/*   0*/       }
/* 584*/    if (len == 0) {
/* 585*/        return null; 
/*   0*/       }
/* 588*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 590*/    int len2 = hashArea[offset2 + 3];
/* 591*/    if (hash == hashArea[offset2] && len2 == qlen && 
/* 592*/      _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
/* 593*/        return this._names[offset2 >> 2]; 
/*   0*/       }
/* 596*/    return _findSecondary(offset, hash, q, qlen);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _calcOffset(int hash) {
/* 604*/    int ix = hash & this._hashSize - 1;
/* 606*/    return ix << 2;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1) {
/* 621*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 622*/    int[] hashArea = this._hashArea;
/* 623*/    int bucketSize = 1 << this._tertiaryShift;
/* 624*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 625*/      int len = hashArea[offset + 3];
/* 626*/      if (q1 == hashArea[offset] && 1 == len) {
/* 627*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 629*/      if (len == 0) {
/* 630*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 636*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 637*/      if (q1 == hashArea[offset] && 1 == hashArea[offset + 3]) {
/* 638*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 641*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1, int q2) {
/* 646*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 647*/    int[] hashArea = this._hashArea;
/* 649*/    int bucketSize = 1 << this._tertiaryShift;
/* 650*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 651*/      int len = hashArea[offset + 3];
/* 652*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
/* 653*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 655*/      if (len == 0) {
/* 656*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 659*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 660*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == hashArea[offset + 3]) {
/* 661*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 664*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1, int q2, int q3) {
/* 669*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 670*/    int[] hashArea = this._hashArea;
/* 672*/    int bucketSize = 1 << this._tertiaryShift;
/* 673*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 674*/      int len = hashArea[offset + 3];
/* 675*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
/* 676*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 678*/      if (len == 0) {
/* 679*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 682*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 683*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == hashArea[offset + 3]) {
/* 685*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 688*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
/* 693*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 694*/    int[] hashArea = this._hashArea;
/* 696*/    int bucketSize = 1 << this._tertiaryShift;
/* 697*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 698*/      int len = hashArea[offset + 3];
/* 699*/      if (hash == hashArea[offset] && qlen == len && 
/* 700*/        _verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 701*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 704*/      if (len == 0) {
/* 705*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 708*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 709*/      if (hash == hashArea[offset] && qlen == hashArea[offset + 3] && 
/* 710*/        _verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 711*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 715*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyLongName(int[] q, int qlen, int spillOffset) {
/* 720*/    int[] hashArea = this._hashArea;
/* 722*/    int ix = 0;
/* 724*/    switch (qlen) {
/*   0*/      default:
/* 726*/        return _verifyLongName2(q, qlen, spillOffset);
/*   0*/      case 8:
/* 728*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 728*/            return false; 
/*   0*/           }
/*   0*/      case 7:
/* 730*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 730*/            return false; 
/*   0*/           }
/*   0*/      case 6:
/* 732*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 732*/            return false; 
/*   0*/           }
/*   0*/      case 5:
/* 734*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 734*/            return false; 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 4:
/*   0*/        break;
/*   0*/    } 
/* 736*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 736*/        return false; 
/*   0*/       }
/* 737*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 737*/        return false; 
/*   0*/       }
/* 738*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 738*/        return false; 
/*   0*/       }
/* 739*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 739*/        return false; 
/*   0*/       }
/* 741*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
/* 746*/    int ix = 0;
/*   0*/    while (true) {
/* 748*/      if (q[ix++] != this._hashArea[spillOffset++]) {
/* 749*/          return false; 
/*   0*/         }
/* 751*/      if (ix >= qlen) {
/* 752*/          return true; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1) {
/* 762*/    _verifySharing();
/* 763*/    if (this._intern) {
/* 764*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 766*/    int offset = _findOffsetForAdd(calcHash(q1));
/* 767*/    this._hashArea[offset] = q1;
/* 768*/    this._hashArea[offset + 3] = 1;
/* 769*/    this._names[offset >> 2] = name;
/* 770*/    this._count++;
/* 771*/    _verifyNeedForRehash();
/* 772*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1, int q2) {
/* 776*/    _verifySharing();
/* 777*/    if (this._intern) {
/* 778*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 780*/    int hash = (q2 == 0) ? calcHash(q1) : calcHash(q1, q2);
/* 781*/    int offset = _findOffsetForAdd(hash);
/* 782*/    this._hashArea[offset] = q1;
/* 783*/    this._hashArea[offset + 1] = q2;
/* 784*/    this._hashArea[offset + 3] = 2;
/* 785*/    this._names[offset >> 2] = name;
/* 786*/    this._count++;
/* 787*/    _verifyNeedForRehash();
/* 788*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1, int q2, int q3) {
/* 792*/    _verifySharing();
/* 793*/    if (this._intern) {
/* 794*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 796*/    int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
/* 797*/    this._hashArea[offset] = q1;
/* 798*/    this._hashArea[offset + 1] = q2;
/* 799*/    this._hashArea[offset + 2] = q3;
/* 800*/    this._hashArea[offset + 3] = 3;
/* 801*/    this._names[offset >> 2] = name;
/* 802*/    this._count++;
/* 803*/    _verifyNeedForRehash();
/* 804*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int[] q, int qlen) {
/*   0*/    int offset, hash, longStart;
/* 809*/    _verifySharing();
/* 810*/    if (this._intern) {
/* 811*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 815*/    switch (qlen) {
/*   0*/      case 1:
/* 818*/        offset = _findOffsetForAdd(calcHash(q[0]));
/* 819*/        this._hashArea[offset] = q[0];
/* 820*/        this._hashArea[offset + 3] = 1;
/*   0*/        break;
/*   0*/      case 2:
/* 825*/        offset = _findOffsetForAdd(calcHash(q[0], q[1]));
/* 826*/        this._hashArea[offset] = q[0];
/* 827*/        this._hashArea[offset + 1] = q[1];
/* 828*/        this._hashArea[offset + 3] = 2;
/*   0*/        break;
/*   0*/      case 3:
/* 833*/        offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2]));
/* 834*/        this._hashArea[offset] = q[0];
/* 835*/        this._hashArea[offset + 1] = q[1];
/* 836*/        this._hashArea[offset + 2] = q[2];
/* 837*/        this._hashArea[offset + 3] = 3;
/*   0*/        break;
/*   0*/      default:
/* 841*/        hash = calcHash(q, qlen);
/* 842*/        offset = _findOffsetForAdd(hash);
/* 844*/        this._hashArea[offset] = hash;
/* 845*/        longStart = _appendLongName(q, qlen);
/* 846*/        this._hashArea[offset + 1] = longStart;
/* 847*/        this._hashArea[offset + 3] = qlen;
/*   0*/        break;
/*   0*/    } 
/* 850*/    this._names[offset >> 2] = name;
/* 853*/    this._count++;
/* 854*/    _verifyNeedForRehash();
/* 855*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  private void _verifyNeedForRehash() {
/* 860*/    if (this._count > this._hashSize >> 1) {
/* 861*/      int spillCount = this._spilloverEnd - _spilloverStart() >> 2;
/* 862*/      if (spillCount > 1 + this._count >> 7 || this._count > this._hashSize * 0.8D) {
/* 864*/          this._needRehash = true; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _verifySharing() {
/* 871*/    if (this._hashShared) {
/* 872*/      this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
/* 873*/      this._names = Arrays.<String>copyOf(this._names, this._names.length);
/* 874*/      this._hashShared = false;
/* 877*/      _verifyNeedForRehash();
/*   0*/    } 
/* 879*/    if (this._needRehash) {
/* 880*/        rehash(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private int _findOffsetForAdd(int hash) {
/* 890*/    int offset = _calcOffset(hash);
/* 891*/    int[] hashArea = this._hashArea;
/* 892*/    if (hashArea[offset + 3] == 0) {
/* 894*/        return offset; 
/*   0*/       }
/* 897*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 898*/    if (hashArea[offset2 + 3] == 0) {
/* 900*/        return offset2; 
/*   0*/       }
/* 904*/    offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 905*/    int bucketSize = 1 << this._tertiaryShift;
/*   0*/    int end;
/* 906*/    for (end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/* 907*/      if (hashArea[offset2 + 3] == 0) {
/* 909*/          return offset2; 
/*   0*/         }
/*   0*/    } 
/* 914*/    offset = this._spilloverEnd;
/* 915*/    this._spilloverEnd += 4;
/* 925*/    end = this._hashSize << 3;
/* 926*/    if (this._spilloverEnd >= end) {
/* 927*/      if (this._failOnDoS) {
/* 928*/          _reportTooManyCollisions(); 
/*   0*/         }
/* 932*/      this._needRehash = true;
/*   0*/    } 
/* 934*/    return offset;
/*   0*/  }
/*   0*/  
/*   0*/  private int _appendLongName(int[] quads, int qlen) {
/* 939*/    int start = this._longNameOffset;
/* 942*/    if (start + qlen > this._hashArea.length) {
/* 944*/      int toAdd = start + qlen - this._hashArea.length;
/* 946*/      int minAdd = Math.min(4096, this._hashSize);
/* 948*/      int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
/* 949*/      this._hashArea = Arrays.copyOf(this._hashArea, newSize);
/*   0*/    } 
/* 951*/    System.arraycopy(quads, 0, this._hashArea, start, qlen);
/* 952*/    this._longNameOffset += qlen;
/* 953*/    return start;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1) {
/* 978*/    int hash = q1 ^ this._seed;
/* 984*/    hash += hash >>> 16;
/* 985*/    hash ^= hash << 3;
/* 986*/    hash += hash >>> 12;
/* 987*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1, int q2) {
/* 994*/    int hash = q1;
/* 996*/    hash += hash >>> 15;
/* 997*/    hash ^= hash >>> 9;
/* 998*/    hash += q2 * 33;
/* 999*/    hash ^= this._seed;
/*1000*/    hash += hash >>> 16;
/*1001*/    hash ^= hash >>> 4;
/*1002*/    hash += hash << 3;
/*1004*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1, int q2, int q3) {
/*1009*/    int hash = q1 ^ this._seed;
/*1010*/    hash += hash >>> 9;
/*1011*/    hash *= 31;
/*1012*/    hash += q2;
/*1013*/    hash *= 33;
/*1014*/    hash += hash >>> 15;
/*1015*/    hash ^= q3;
/*1017*/    hash += hash >>> 4;
/*1019*/    hash += hash >>> 15;
/*1020*/    hash ^= hash << 9;
/*1022*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int[] q, int qlen) {
/*1027*/    if (qlen < 4) {
/*1028*/        throw new IllegalArgumentException(); 
/*   0*/       }
/*1035*/    int hash = q[0] ^ this._seed;
/*1036*/    hash += hash >>> 9;
/*1037*/    hash += q[1];
/*1038*/    hash += hash >>> 15;
/*1039*/    hash *= 33;
/*1040*/    hash ^= q[2];
/*1041*/    hash += hash >>> 4;
/*1043*/    for (int i = 3; i < qlen; i++) {
/*1044*/      int next = q[i];
/*1045*/      next ^= next >> 21;
/*1046*/      hash += next;
/*   0*/    } 
/*1048*/    hash *= 65599;
/*1051*/    hash += hash >>> 19;
/*1052*/    hash ^= hash << 5;
/*1053*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  private void rehash() {
/*1064*/    this._needRehash = false;
/*1066*/    this._hashShared = false;
/*1070*/    int[] oldHashArea = this._hashArea;
/*1071*/    String[] oldNames = this._names;
/*1072*/    int oldSize = this._hashSize;
/*1073*/    int oldCount = this._count;
/*1074*/    int newSize = oldSize + oldSize;
/*1075*/    int oldEnd = this._spilloverEnd;
/*1080*/    if (newSize > 65536) {
/*1081*/      nukeSymbols(true);
/*   0*/      return;
/*   0*/    } 
/*1085*/    this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
/*1086*/    this._hashSize = newSize;
/*1087*/    this._secondaryStart = newSize << 2;
/*1088*/    this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/*1089*/    this._tertiaryShift = _calcTertiaryShift(newSize);
/*1092*/    this._names = new String[oldNames.length << 1];
/*1093*/    nukeSymbols(false);
/*1100*/    int copyCount = 0;
/*1101*/    int[] q = new int[16];
/*1102*/    for (int offset = 0, end = oldEnd; offset < end; offset += 4) {
/*1103*/      int len = oldHashArea[offset + 3];
/*1104*/      if (len != 0) {
/*   0*/        int qoff;
/*1107*/        copyCount++;
/*1108*/        String name = oldNames[offset >> 2];
/*1109*/        switch (len) {
/*   0*/          case 1:
/*1111*/            q[0] = oldHashArea[offset];
/*1112*/            addName(name, q, 1);
/*   0*/            break;
/*   0*/          case 2:
/*1115*/            q[0] = oldHashArea[offset];
/*1116*/            q[1] = oldHashArea[offset + 1];
/*1117*/            addName(name, q, 2);
/*   0*/            break;
/*   0*/          case 3:
/*1120*/            q[0] = oldHashArea[offset];
/*1121*/            q[1] = oldHashArea[offset + 1];
/*1122*/            q[2] = oldHashArea[offset + 2];
/*1123*/            addName(name, q, 3);
/*   0*/            break;
/*   0*/          default:
/*1126*/            if (len > q.length) {
/*1127*/                q = new int[len]; 
/*   0*/               }
/*1130*/            qoff = oldHashArea[offset + 1];
/*1131*/            System.arraycopy(oldHashArea, qoff, q, 0, len);
/*1132*/            addName(name, q, len);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1139*/    if (copyCount != oldCount) {
/*1140*/        throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void nukeSymbols(boolean fill) {
/*1149*/    this._count = 0;
/*1151*/    this._spilloverEnd = _spilloverStart();
/*1153*/    this._longNameOffset = this._hashSize << 3;
/*1154*/    if (fill) {
/*1155*/      Arrays.fill(this._hashArea, 0);
/*1156*/      Arrays.fill((Object[])this._names, null);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final int _spilloverStart() {
/*1172*/    int offset = this._hashSize;
/*1173*/    return (offset << 3) - offset;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportTooManyCollisions() {
/*1179*/    if (this._hashSize <= 1024) {
/*   0*/        return; 
/*   0*/       }
/*1182*/    throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions." + " You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
/*   0*/  }
/*   0*/  
/*   0*/  static int _calcTertiaryShift(int primarySlots) {
/*1191*/    int tertSlots = primarySlots >> 2;
/*1194*/    if (tertSlots < 64) {
/*1195*/        return 4; 
/*   0*/       }
/*1197*/    if (tertSlots <= 256) {
/*1198*/        return 5; 
/*   0*/       }
/*1200*/    if (tertSlots <= 1024) {
/*1201*/        return 6; 
/*   0*/       }
/*1204*/    return 7;
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
/*1233*/      this.size = size;
/*1234*/      this.count = count;
/*1235*/      this.tertiaryShift = tertiaryShift;
/*1236*/      this.mainHash = mainHash;
/*1237*/      this.names = names;
/*1238*/      this.spilloverEnd = spilloverEnd;
/*1239*/      this.longNameOffset = longNameOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public TableInfo(ByteQuadsCanonicalizer src) {
/*1244*/      this.size = src._hashSize;
/*1245*/      this.count = src._count;
/*1246*/      this.tertiaryShift = src._tertiaryShift;
/*1247*/      this.mainHash = src._hashArea;
/*1248*/      this.names = src._names;
/*1249*/      this.spilloverEnd = src._spilloverEnd;
/*1250*/      this.longNameOffset = src._longNameOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public static TableInfo createInitial(int sz) {
/*1254*/      int hashAreaSize = sz << 3;
/*1255*/      int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
/*1257*/      return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
/*   0*/    }
/*   0*/  }
/*   0*/}
