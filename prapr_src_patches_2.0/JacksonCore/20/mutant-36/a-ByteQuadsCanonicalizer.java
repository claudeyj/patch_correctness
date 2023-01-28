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
/* 288*/    long now = System.currentTimeMillis();
/* 290*/    int seed = (int)now + (int)(now >>> 32L) | 0x1;
/* 291*/    return createRoot(seed);
/*   0*/  }
/*   0*/  
/*   0*/  protected static ByteQuadsCanonicalizer createRoot(int seed) {
/* 299*/    return new ByteQuadsCanonicalizer(64, true, seed, true);
/*   0*/  }
/*   0*/  
/*   0*/  public ByteQuadsCanonicalizer makeChild(int flags) {
/* 307*/    return new ByteQuadsCanonicalizer(this, JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(flags), this._seed, JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags), this._tableInfo.get());
/*   0*/  }
/*   0*/  
/*   0*/  public void release() {
/* 324*/    if (this._parent != null && maybeDirty()) {
/* 325*/      this._parent.mergeChild(new TableInfo(this));
/* 329*/      this._hashShared = true;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void mergeChild(TableInfo childState) {
/* 335*/    int childCount = childState.count;
/* 336*/    TableInfo currState = this._tableInfo.get();
/* 340*/    if (childCount == currState.count) {
/*   0*/        return; 
/*   0*/       }
/* 348*/    if (childCount > 6000) {
/* 350*/        childState = TableInfo.createInitial(64); 
/*   0*/       }
/* 352*/    this._tableInfo.compareAndSet(currState, childState);
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 363*/    if (this._tableInfo != null) {
/* 364*/        return ((TableInfo)this._tableInfo.get()).count; 
/*   0*/       }
/* 367*/    return this._count;
/*   0*/  }
/*   0*/  
/*   0*/  public int bucketCount() {
/* 373*/    return this._hashSize;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean maybeDirty() {
/* 380*/    return !this._hashShared;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashSeed() {
/* 382*/    return this._seed;
/*   0*/  }
/*   0*/  
/*   0*/  public int primaryCount() {
/* 391*/    int count = 0;
/* 392*/    for (int offset = 3, end = this._secondaryStart; offset < end; offset += 4) {
/* 393*/      if (this._hashArea[offset] != 0) {
/* 394*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 397*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int secondaryCount() {
/* 405*/    int count = 0;
/* 406*/    int offset = this._secondaryStart + 3;
/* 407*/    for (int end = this._tertiaryStart; offset < end; offset += 4) {
/* 408*/      if (this._hashArea[offset] != 0) {
/* 409*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 412*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int tertiaryCount() {
/* 420*/    int count = 0;
/* 421*/    int offset = this._tertiaryStart + 3;
/* 422*/    for (int end = offset + this._hashSize; offset < end; offset += 4) {
/* 423*/      if (this._hashArea[offset] != 0) {
/* 424*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 427*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public int spilloverCount() {
/* 436*/    return this._spilloverEnd - _spilloverStart() >> 2;
/*   0*/  }
/*   0*/  
/*   0*/  public int totalCount() {
/* 441*/    int count = 0;
/* 442*/    for (int offset = 3, end = this._hashSize << 3; offset < end; offset += 4) {
/* 443*/      if (this._hashArea[offset] != 0) {
/* 444*/          count++; 
/*   0*/         }
/*   0*/    } 
/* 447*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 452*/    int pri = primaryCount();
/* 453*/    int sec = secondaryCount();
/* 454*/    int tert = tertiaryCount();
/* 455*/    int spill = spilloverCount();
/* 456*/    int total = totalCount();
/* 457*/    return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", new Object[] { getClass().getName(), this._count, this._hashSize, pri, sec, tert, spill, pri + sec + tert + spill, total });
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int q1) {
/* 470*/    int offset = _calcOffset(calcHash(q1));
/* 472*/    int[] hashArea = this._hashArea;
/* 474*/    int len = hashArea[offset + 3];
/* 476*/    if (len == 1) {
/* 477*/      if (hashArea[offset] == q1) {
/* 478*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 480*/    } else if (len == 0) {
/* 481*/      return null;
/*   0*/    } 
/* 484*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 486*/    len = hashArea[offset2 + 3];
/* 488*/    if (len == 1) {
/* 489*/      if (hashArea[offset2] == q1) {
/* 490*/          return this._names[offset2 >> 2]; 
/*   0*/         }
/* 492*/    } else if (len == 0) {
/* 493*/      return null;
/*   0*/    } 
/* 497*/    return _findSecondary(offset, q1);
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int q1, int q2) {
/* 502*/    int offset = _calcOffset(calcHash(q1, q2));
/* 504*/    int[] hashArea = this._hashArea;
/* 506*/    int len = hashArea[offset + 3];
/* 508*/    if (len == 2) {
/* 509*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1]) {
/* 510*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 512*/    } else if (len == 0) {
/* 513*/      return null;
/*   0*/    } 
/* 516*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 518*/    len = hashArea[offset2 + 3];
/* 520*/    if (len == 2) {
/* 521*/      if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1]) {
/* 522*/          return this._names[offset2 >> 2]; 
/*   0*/         }
/* 524*/    } else if (len == 0) {
/* 525*/      return null;
/*   0*/    } 
/* 527*/    return _findSecondary(offset, q1, q2);
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int q1, int q2, int q3) {
/* 532*/    int offset = _calcOffset(calcHash(q1, q2, q3));
/* 533*/    int[] hashArea = this._hashArea;
/* 534*/    int len = hashArea[offset + 3];
/* 536*/    if (len == 3) {
/* 537*/      if (q1 == hashArea[offset] && hashArea[offset + 1] == q2 && hashArea[offset + 2] == q3) {
/* 538*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 540*/    } else if (len == 0) {
/* 541*/      return null;
/*   0*/    } 
/* 544*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 546*/    len = hashArea[offset2 + 3];
/* 548*/    if (len == 3) {
/* 549*/      if (q1 == hashArea[offset2] && hashArea[offset2 + 1] == q2 && hashArea[offset2 + 2] == q3) {
/* 550*/          return this._names[offset2 >> 2]; 
/*   0*/         }
/* 552*/    } else if (len == 0) {
/* 553*/      return null;
/*   0*/    } 
/* 555*/    return _findSecondary(offset, q1, q2, q3);
/*   0*/  }
/*   0*/  
/*   0*/  public String findName(int[] q, int qlen) {
/* 564*/    if (qlen < 4) {
/* 565*/      if (qlen == 3) {
/* 566*/          return findName(q[0], q[1], q[2]); 
/*   0*/         }
/* 568*/      if (qlen == 2) {
/* 569*/          return findName(q[0], q[1]); 
/*   0*/         }
/* 571*/      return findName(q[0]);
/*   0*/    } 
/* 573*/    int hash = calcHash(q, qlen);
/* 574*/    int offset = _calcOffset(hash);
/* 576*/    int[] hashArea = this._hashArea;
/* 578*/    int len = hashArea[offset + 3];
/* 580*/    if (hash == hashArea[offset] && len == qlen) {
/* 582*/        if (_verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 583*/            return this._names[offset >> 2]; 
/*   0*/           } 
/*   0*/       }
/* 586*/    if (len == 0) {
/* 587*/        return null; 
/*   0*/       }
/* 590*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 592*/    int len2 = hashArea[offset2 + 3];
/* 593*/    if (hash == hashArea[offset2] && len2 == qlen && 
/* 594*/      _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
/* 595*/        return this._names[offset2 >> 2]; 
/*   0*/       }
/* 598*/    return _findSecondary(offset, hash, q, qlen);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _calcOffset(int hash) {
/* 606*/    int ix = hash & this._hashSize - 1;
/* 608*/    return ix << 2;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1) {
/* 623*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 624*/    int[] hashArea = this._hashArea;
/* 625*/    int bucketSize = 1 << this._tertiaryShift;
/* 626*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 627*/      int len = hashArea[offset + 3];
/* 628*/      if (q1 == hashArea[offset] && 1 == len) {
/* 629*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 631*/      if (len == 0) {
/* 632*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 638*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 639*/      if (q1 == hashArea[offset] && 1 == hashArea[offset + 3]) {
/* 640*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 643*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1, int q2) {
/* 648*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 649*/    int[] hashArea = this._hashArea;
/* 651*/    int bucketSize = 1 << this._tertiaryShift;
/* 652*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 653*/      int len = hashArea[offset + 3];
/* 654*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
/* 655*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 657*/      if (len == 0) {
/* 658*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 661*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 662*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == hashArea[offset + 3]) {
/* 663*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 666*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int q1, int q2, int q3) {
/* 671*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 672*/    int[] hashArea = this._hashArea;
/* 674*/    int bucketSize = 1 << this._tertiaryShift;
/* 675*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 676*/      int len = hashArea[offset + 3];
/* 677*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
/* 678*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 680*/      if (len == 0) {
/* 681*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 684*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 685*/      if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == hashArea[offset + 3]) {
/* 687*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 690*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
/* 695*/    int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 696*/    int[] hashArea = this._hashArea;
/* 698*/    int bucketSize = 1 << this._tertiaryShift;
/* 699*/    for (int end = offset + bucketSize; offset < end; offset += 4) {
/* 700*/      int len = hashArea[offset + 3];
/* 701*/      if (hash == hashArea[offset] && qlen == len && 
/* 702*/        _verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 703*/          return this._names[offset >> 2]; 
/*   0*/         }
/* 706*/      if (len == 0) {
/* 707*/          return null; 
/*   0*/         }
/*   0*/    } 
/* 710*/    for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/* 711*/      if (hash == hashArea[offset] && qlen == hashArea[offset + 3] && 
/* 712*/        _verifyLongName(q, qlen, hashArea[offset + 1])) {
/* 713*/          return this._names[offset >> 2]; 
/*   0*/         }
/*   0*/    } 
/* 717*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyLongName(int[] q, int qlen, int spillOffset) {
/* 722*/    int[] hashArea = this._hashArea;
/* 724*/    int ix = 0;
/* 726*/    switch (qlen) {
/*   0*/      default:
/* 728*/        return _verifyLongName2(q, qlen, spillOffset);
/*   0*/      case 8:
/* 730*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 730*/            return false; 
/*   0*/           }
/*   0*/      case 7:
/* 732*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 732*/            return false; 
/*   0*/           }
/*   0*/      case 6:
/* 734*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 734*/            return false; 
/*   0*/           }
/*   0*/      case 5:
/* 736*/        if (q[ix++] != hashArea[spillOffset++]) {
/* 736*/            return false; 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 4:
/*   0*/        break;
/*   0*/    } 
/* 738*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 738*/        return false; 
/*   0*/       }
/* 739*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 739*/        return false; 
/*   0*/       }
/* 740*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 740*/        return false; 
/*   0*/       }
/* 741*/    if (q[ix++] != hashArea[spillOffset++]) {
/* 741*/        return false; 
/*   0*/       }
/* 743*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
/* 748*/    int ix = 0;
/*   0*/    while (true) {
/* 750*/      if (q[ix++] != this._hashArea[spillOffset++]) {
/* 751*/          return false; 
/*   0*/         }
/* 753*/      if (ix >= qlen) {
/* 754*/          return true; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1) {
/* 764*/    _verifySharing();
/* 765*/    if (this._intern) {
/* 766*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 768*/    int offset = _findOffsetForAdd(calcHash(q1));
/* 769*/    this._hashArea[offset] = q1;
/* 770*/    this._hashArea[offset + 3] = 1;
/* 771*/    this._names[offset >> 2] = name;
/* 772*/    this._count++;
/* 773*/    _verifyNeedForRehash();
/* 774*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1, int q2) {
/* 778*/    _verifySharing();
/* 779*/    if (this._intern) {
/* 780*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 782*/    int hash = (q2 == 0) ? calcHash(q1) : calcHash(q1, q2);
/* 783*/    int offset = _findOffsetForAdd(hash);
/* 784*/    this._hashArea[offset] = q1;
/* 785*/    this._hashArea[offset + 1] = q2;
/* 786*/    this._hashArea[offset + 3] = 2;
/* 787*/    this._names[offset >> 2] = name;
/* 788*/    this._count++;
/* 789*/    _verifyNeedForRehash();
/* 790*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int q1, int q2, int q3) {
/* 794*/    _verifySharing();
/* 795*/    if (this._intern) {
/* 796*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 798*/    int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
/* 799*/    this._hashArea[offset] = q1;
/* 800*/    this._hashArea[offset + 1] = q2;
/* 801*/    this._hashArea[offset + 2] = q3;
/* 802*/    this._hashArea[offset + 3] = 3;
/* 803*/    this._names[offset >> 2] = name;
/* 804*/    this._count++;
/* 805*/    _verifyNeedForRehash();
/* 806*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  public String addName(String name, int[] q, int qlen) {
/*   0*/    int offset, hash, longStart;
/* 811*/    _verifySharing();
/* 812*/    if (this._intern) {
/* 813*/        name = InternCache.instance.intern(name); 
/*   0*/       }
/* 817*/    switch (qlen) {
/*   0*/      case 1:
/* 820*/        offset = _findOffsetForAdd(calcHash(q[0]));
/* 821*/        this._hashArea[offset] = q[0];
/* 822*/        this._hashArea[offset + 3] = 1;
/*   0*/        break;
/*   0*/      case 2:
/* 827*/        offset = _findOffsetForAdd(calcHash(q[0], q[1]));
/* 828*/        this._hashArea[offset] = q[0];
/* 829*/        this._hashArea[offset + 1] = q[1];
/* 830*/        this._hashArea[offset + 3] = 2;
/*   0*/        break;
/*   0*/      case 3:
/* 835*/        offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2]));
/* 836*/        this._hashArea[offset] = q[0];
/* 837*/        this._hashArea[offset + 1] = q[1];
/* 838*/        this._hashArea[offset + 2] = q[2];
/* 839*/        this._hashArea[offset + 3] = 3;
/*   0*/        break;
/*   0*/      default:
/* 843*/        hash = calcHash(q, qlen);
/* 844*/        offset = _findOffsetForAdd(hash);
/* 846*/        this._hashArea[offset] = hash;
/* 847*/        longStart = _appendLongName(q, qlen);
/* 848*/        this._hashArea[offset + 1] = longStart;
/* 849*/        this._hashArea[offset + 3] = qlen;
/*   0*/        break;
/*   0*/    } 
/* 852*/    this._names[offset >> 2] = name;
/* 855*/    this._count++;
/* 856*/    _verifyNeedForRehash();
/* 857*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  private void _verifyNeedForRehash() {
/* 862*/    if (this._count > this._hashSize >> 1) {
/* 863*/      int spillCount = this._spilloverEnd - _spilloverStart() >> 2;
/* 864*/      if (spillCount > 1 + this._count >> 7 || this._count > this._hashSize * 0.8D) {
/* 866*/          this._needRehash = true; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _verifySharing() {
/* 873*/    if (this._hashShared) {
/* 874*/      this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
/* 875*/      this._names = Arrays.<String>copyOf(this._names, this._names.length);
/* 876*/      this._hashShared = false;
/* 879*/      _verifyNeedForRehash();
/*   0*/    } 
/* 881*/    if (this._needRehash) {
/* 882*/        rehash(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private int _findOffsetForAdd(int hash) {
/* 892*/    int offset = _calcOffset(hash);
/* 893*/    int[] hashArea = this._hashArea;
/* 894*/    if (hashArea[offset + 3] == 0) {
/* 896*/        return offset; 
/*   0*/       }
/* 899*/    int offset2 = this._secondaryStart + (offset >> 3 << 2);
/* 900*/    if (hashArea[offset2 + 3] == 0) {
/* 902*/        return offset2; 
/*   0*/       }
/* 906*/    offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/* 907*/    int bucketSize = 1 << this._tertiaryShift;
/*   0*/    int end;
/* 908*/    for (end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/* 909*/      if (hashArea[offset2 + 3] == 0) {
/* 911*/          return offset2; 
/*   0*/         }
/*   0*/    } 
/* 916*/    offset = this._spilloverEnd;
/* 917*/    this._spilloverEnd += 4;
/* 927*/    end = this._hashSize << 3;
/* 928*/    if (this._spilloverEnd >= end) {
/* 929*/      if (this._failOnDoS) {
/* 930*/          _reportTooManyCollisions(); 
/*   0*/         }
/* 934*/      this._needRehash = true;
/*   0*/    } 
/* 936*/    return offset;
/*   0*/  }
/*   0*/  
/*   0*/  private int _appendLongName(int[] quads, int qlen) {
/* 941*/    int start = this._longNameOffset;
/* 944*/    if (start + qlen > this._hashArea.length) {
/* 946*/      int toAdd = start + qlen - this._hashArea.length;
/* 948*/      int minAdd = Math.min(4096, this._hashSize);
/* 950*/      int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
/* 951*/      this._hashArea = Arrays.copyOf(this._hashArea, newSize);
/*   0*/    } 
/* 953*/    System.arraycopy(quads, 0, this._hashArea, start, qlen);
/* 954*/    this._longNameOffset += qlen;
/* 955*/    return start;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1) {
/* 980*/    int hash = q1 ^ this._seed;
/* 986*/    hash += hash >>> 16;
/* 987*/    hash ^= hash << 3;
/* 988*/    hash += hash >>> 12;
/* 989*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1, int q2) {
/* 996*/    int hash = q1;
/* 998*/    hash += hash >>> 15;
/* 999*/    hash ^= hash >>> 9;
/*1000*/    hash += q2 * 33;
/*1001*/    hash ^= this._seed;
/*1002*/    hash += hash >>> 16;
/*1003*/    hash ^= hash >>> 4;
/*1004*/    hash += hash << 3;
/*1006*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int q1, int q2, int q3) {
/*1011*/    int hash = q1 ^ this._seed;
/*1012*/    hash += hash >>> 9;
/*1013*/    hash *= 31;
/*1014*/    hash += q2;
/*1015*/    hash *= 33;
/*1016*/    hash += hash >>> 15;
/*1017*/    hash ^= q3;
/*1019*/    hash += hash >>> 4;
/*1021*/    hash += hash >>> 15;
/*1022*/    hash ^= hash << 9;
/*1024*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  public int calcHash(int[] q, int qlen) {
/*1029*/    if (qlen < 4) {
/*1030*/        throw new IllegalArgumentException(); 
/*   0*/       }
/*1037*/    int hash = q[0] ^ this._seed;
/*1038*/    hash += hash >>> 9;
/*1039*/    hash += q[1];
/*1040*/    hash += hash >>> 15;
/*1041*/    hash *= 33;
/*1042*/    hash ^= q[2];
/*1043*/    hash += hash >>> 4;
/*1045*/    for (int i = 3; i < qlen; i++) {
/*1046*/      int next = q[i];
/*1047*/      next ^= next >> 21;
/*1048*/      hash += next;
/*   0*/    } 
/*1050*/    hash *= 65599;
/*1053*/    hash += hash >>> 19;
/*1054*/    hash ^= hash << 5;
/*1055*/    return hash;
/*   0*/  }
/*   0*/  
/*   0*/  private void rehash() {
/*1066*/    this._needRehash = false;
/*1068*/    this._hashShared = false;
/*1072*/    int[] oldHashArea = this._hashArea;
/*1073*/    String[] oldNames = this._names;
/*1074*/    int oldSize = this._hashSize;
/*1075*/    int oldCount = this._count;
/*1076*/    int newSize = oldSize + oldSize;
/*1077*/    int oldEnd = this._spilloverEnd;
/*1082*/    if (newSize > 65536) {
/*1083*/      nukeSymbols(true);
/*   0*/      return;
/*   0*/    } 
/*1087*/    this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
/*1088*/    this._hashSize = newSize;
/*1089*/    this._secondaryStart = newSize << 2;
/*1090*/    this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/*1091*/    this._tertiaryShift = _calcTertiaryShift(newSize);
/*1094*/    this._names = new String[oldNames.length << 1];
/*1095*/    nukeSymbols(false);
/*1102*/    int copyCount = 0;
/*1103*/    int[] q = new int[16];
/*1104*/    for (int offset = 0, end = oldEnd; offset < end; offset += 4) {
/*1105*/      int len = oldHashArea[offset + 3];
/*1106*/      if (len != 0) {
/*   0*/        int qoff;
/*1109*/        copyCount++;
/*1110*/        String name = oldNames[offset >> 2];
/*1111*/        switch (len) {
/*   0*/          case 1:
/*1113*/            q[0] = oldHashArea[offset];
/*1114*/            addName(name, q, 1);
/*   0*/            break;
/*   0*/          case 2:
/*1117*/            q[0] = oldHashArea[offset];
/*1118*/            q[1] = oldHashArea[offset + 1];
/*1119*/            addName(name, q, 2);
/*   0*/            break;
/*   0*/          case 3:
/*1122*/            q[0] = oldHashArea[offset];
/*1123*/            q[1] = oldHashArea[offset + 1];
/*1124*/            q[2] = oldHashArea[offset + 2];
/*1125*/            addName(name, q, 3);
/*   0*/            break;
/*   0*/          default:
/*1128*/            if (len > q.length) {
/*1129*/                q = new int[len]; 
/*   0*/               }
/*1132*/            qoff = oldHashArea[offset + 1];
/*1133*/            System.arraycopy(oldHashArea, qoff, q, 0, len);
/*1134*/            addName(name, q, len);
/*   0*/            break;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1141*/    if (copyCount != oldCount) {
/*1142*/        throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void nukeSymbols(boolean fill) {
/*1151*/    this._count = 0;
/*1153*/    this._spilloverEnd = _spilloverStart();
/*1155*/    this._longNameOffset = this._hashSize << 3;
/*1156*/    if (fill) {
/*1157*/      Arrays.fill(this._hashArea, 0);
/*1158*/      Arrays.fill((Object[])this._names, null);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final int _spilloverStart() {
/*1174*/    int offset = this._hashSize;
/*1175*/    return (offset << 3) - offset;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _reportTooManyCollisions() {
/*1181*/    if (this._hashSize <= 1024) {
/*   0*/        return; 
/*   0*/       }
/*1184*/    throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions." + " You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
/*   0*/  }
/*   0*/  
/*   0*/  static int _calcTertiaryShift(int primarySlots) {
/*1193*/    int tertSlots = primarySlots >> 2;
/*1196*/    if (tertSlots < 64) {
/*1197*/        return 4; 
/*   0*/       }
/*1199*/    if (tertSlots <= 256) {
/*1200*/        return 5; 
/*   0*/       }
/*1202*/    if (tertSlots <= 1024) {
/*1203*/        return 6; 
/*   0*/       }
/*1206*/    return 7;
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
/*1235*/      this.size = size;
/*1236*/      this.count = count;
/*1237*/      this.tertiaryShift = tertiaryShift;
/*1238*/      this.mainHash = mainHash;
/*1239*/      this.names = names;
/*1240*/      this.spilloverEnd = spilloverEnd;
/*1241*/      this.longNameOffset = longNameOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public TableInfo(ByteQuadsCanonicalizer src) {
/*1246*/      this.size = src._hashSize;
/*1247*/      this.count = src._count;
/*1248*/      this.tertiaryShift = src._tertiaryShift;
/*1249*/      this.mainHash = src._hashArea;
/*1250*/      this.names = src._names;
/*1251*/      this.spilloverEnd = src._spilloverEnd;
/*1252*/      this.longNameOffset = src._longNameOffset;
/*   0*/    }
/*   0*/    
/*   0*/    public static TableInfo createInitial(int sz) {
/*1256*/      int hashAreaSize = sz << 3;
/*1257*/      int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
/*1259*/      return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
/*   0*/    }
/*   0*/  }
/*   0*/}
