/*   0*/package org.apache.commons.math3.random;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.apache.commons.math3.util.FastMath;
/*   0*/
/*   0*/public class MersenneTwister extends BitsStreamGenerator implements Serializable {
/*   0*/  private static final long serialVersionUID = 8661194735290153518L;
/*   0*/  
/*   0*/  private static final int N = 624;
/*   0*/  
/*   0*/  private static final int M = 397;
/*   0*/  
/*  94*/  private static final int[] MAG01 = new int[] { 0, -1727483681 };
/*   0*/  
/*   0*/  private int[] mt;
/*   0*/  
/*   0*/  private int mti;
/*   0*/  
/*   0*/  public MersenneTwister() {
/* 107*/    this.mt = new int[624];
/* 108*/    setSeed(System.currentTimeMillis() + System.identityHashCode(this));
/*   0*/  }
/*   0*/  
/*   0*/  public MersenneTwister(int seed) {
/* 115*/    this.mt = new int[624];
/* 116*/    setSeed(seed);
/*   0*/  }
/*   0*/  
/*   0*/  public MersenneTwister(int[] seed) {
/* 124*/    this.mt = new int[624];
/* 125*/    setSeed(seed);
/*   0*/  }
/*   0*/  
/*   0*/  public MersenneTwister(long seed) {
/* 132*/    this.mt = new int[624];
/* 133*/    setSeed(seed);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeed(int seed) {
/* 144*/    long longMT = seed;
/* 146*/    this.mt[0] = (int)longMT;
/* 147*/    for (this.mti = 1; this.mti < 624; this.mti++) {
/* 150*/      longMT = 1812433253L * (longMT ^ longMT >> 30L) + this.mti & 0xFFFFFFFFL;
/* 151*/      this.mt[this.mti] = (int)longMT;
/*   0*/    } 
/* 154*/    clear();
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeed(int[] seed) {
/* 167*/    if (seed == null) {
/* 168*/      setSeed(System.currentTimeMillis() + System.identityHashCode(this));
/*   0*/      return;
/*   0*/    } 
/* 172*/    setSeed(19650218);
/* 173*/    int i = 1;
/* 174*/    int j = 0;
/* 176*/    for (int k = FastMath.max(624, seed.length); k != 0; k--) {
/* 177*/      long l0 = this.mt[i] & 0x7FFFFFFFL | ((this.mt[i] < 0) ? 2147483648L : 0L);
/* 178*/      long l1 = this.mt[i - 1] & 0x7FFFFFFFL | ((this.mt[i - 1] < 0) ? 2147483648L : 0L);
/* 179*/      long l = (l0 ^ (l1 ^ l1 >> 30L) * 1664525L) + seed[j] + j;
/* 180*/      this.mt[i] = (int)(l & 0xFFFFFFFFL);
/* 181*/      i++;
/* 181*/      j++;
/* 182*/      if (i >= 624) {
/* 183*/        this.mt[0] = this.mt[623];
/* 184*/        i = 1;
/*   0*/      } 
/* 186*/      if (j >= seed.length)
/* 187*/        j = 0; 
/*   0*/    } 
/* 191*/    for (int m = 623; m != 0; m--) {
/* 192*/      long l0 = this.mt[i] & 0x7FFFFFFFL | ((this.mt[i] < 0) ? 2147483648L : 0L);
/* 193*/      long l1 = this.mt[i - 1] & 0x7FFFFFFFL | ((this.mt[i - 1] < 0) ? 2147483648L : 0L);
/* 194*/      long l = (l0 ^ (l1 ^ l1 >> 30L) * 1566083941L) - i;
/* 195*/      this.mt[i] = (int)(l & 0xFFFFFFFFL);
/* 196*/      i++;
/* 197*/      if (i >= 624) {
/* 198*/        this.mt[0] = this.mt[623];
/* 199*/        i = 1;
/*   0*/      } 
/*   0*/    } 
/* 203*/    this.mt[0] = Integer.MIN_VALUE;
/* 205*/    clear();
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeed(long seed) {
/* 216*/    setSeed(new int[] { (int)(seed >>> 32L), (int)(seed & 0xFFFFFFFFL) });
/*   0*/  }
/*   0*/  
/*   0*/  protected int next(int bits) {
/* 233*/    this;
/* 233*/    if (((this == null) ? 0 : this.mti) >= 624) {
/* 234*/      int mtNext = this.mt[0];
/* 235*/      for (int k = 0; k < 227; k++) {
/* 236*/        int mtCurr = mtNext;
/* 237*/        mtNext = this.mt[k + 1];
/* 238*/        int m = mtCurr & Integer.MIN_VALUE | mtNext & Integer.MAX_VALUE;
/* 239*/        this.mt[k] = this.mt[k + 397] ^ m >>> 1 ^ MAG01[m & 0x1];
/*   0*/      } 
/* 241*/      for (int i = 227; i < 623; i++) {
/* 242*/        int mtCurr = mtNext;
/* 243*/        mtNext = this.mt[i + 1];
/* 244*/        int m = mtCurr & Integer.MIN_VALUE | mtNext & Integer.MAX_VALUE;
/* 245*/        this.mt[i] = this.mt[i + -227] ^ m >>> 1 ^ MAG01[m & 0x1];
/*   0*/      } 
/* 247*/      int j = mtNext & Integer.MIN_VALUE | this.mt[0] & Integer.MAX_VALUE;
/* 248*/      this.mt[623] = this.mt[396] ^ j >>> 1 ^ MAG01[j & 0x1];
/* 250*/      this.mti = 0;
/*   0*/    } 
/* 253*/    int y = this.mt[this.mti++];
/* 256*/    y ^= y >>> 11;
/* 257*/    y ^= y << 7 & 0x9D2C5680;
/* 258*/    y ^= y << 15 & 0xEFC60000;
/* 259*/    y ^= y >>> 18;
/* 261*/    return y >>> 32 - bits;
/*   0*/  }
/*   0*/}
