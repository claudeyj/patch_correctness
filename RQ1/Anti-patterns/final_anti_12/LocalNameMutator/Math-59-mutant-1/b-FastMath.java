/*   0*/package org.apache.commons.math.util;
/*   0*/
/*   0*/public class FastMath {
/*   0*/  public static final double PI = 3.141592653589793D;
/*   0*/  
/*   0*/  public static final double E = 2.718281828459045D;
/*   0*/  
/*  35*/  private static final double[] EXP_INT_TABLE_A = new double[1500];
/*   0*/  
/*  40*/  private static final double[] EXP_INT_TABLE_B = new double[1500];
/*   0*/  
/*  45*/  private static final double[] EXP_FRAC_TABLE_A = new double[1025];
/*   0*/  
/*  50*/  private static final double[] EXP_FRAC_TABLE_B = new double[1025];
/*   0*/  
/*  53*/  private static final double[] FACT = new double[20];
/*   0*/  
/*  56*/  private static final double[][] LN_MANT = new double[1024][];
/*   0*/  
/*   0*/  private static final double LN_2_A = 0.6931470632553101D;
/*   0*/  
/*   0*/  private static final double LN_2_B = 1.1730463525082348E-7D;
/*   0*/  
/*  65*/  private static final double[][] LN_SPLIT_COEF = new double[][] { 
/*  65*/      { 2.0D, 0.0D }, { 0.6666666269302368D, 3.9736429850260626E-8D }, { 0.3999999761581421D, 2.3841857910019882E-8D }, { 0.2857142686843872D, 1.7029898543501842E-8D }, { 0.2222222089767456D, 1.3245471311735498E-8D }, { 0.1818181574344635D, 2.4384203044354907E-8D }, { 0.1538461446762085D, 9.140260083262505E-9D }, { 0.13333332538604736D, 9.220590270857665E-9D }, { 0.11764700710773468D, 1.2393345855018391E-8D }, { 0.10526403784751892D, 8.251545029714408E-9D }, 
/*  65*/      { 0.0952233225107193D, 1.2675934823758863E-8D }, { 0.08713622391223907D, 1.1430250008909141E-8D }, { 0.07842259109020233D, 2.404307984052299E-9D }, { 0.08371849358081818D, 1.176342548272881E-8D }, { 0.030589580535888672D, 1.2958646899018938E-9D }, { 0.14982303977012634D, 1.225743062930824E-8D } };
/*   0*/  
/*  85*/  private static final double[][] LN_QUICK_COEF = new double[][] { { 1.0D, 5.669184079525E-24D }, { -0.25D, -0.25D }, { 0.3333333134651184D, 1.986821492305628E-8D }, { -0.25D, -6.663542893624021E-14D }, { 0.19999998807907104D, 1.1921056801463227E-8D }, { -0.1666666567325592D, -7.800414592973399E-9D }, { 0.1428571343421936D, 5.650007086920087E-9D }, { -0.12502530217170715D, -7.44321345601866E-11D }, { 0.11113807559013367D, 9.219544613762692E-9D } };
/*   0*/  
/*  98*/  private static final double[][] LN_HI_PREC_COEF = new double[][] { { 1.0D, -6.032174644509064E-23D }, { -0.25D, -0.25D }, { 0.3333333134651184D, 1.9868161777724352E-8D }, { -0.2499999701976776D, -2.957007209750105E-8D }, { 0.19999954104423523D, 1.5830993332061267E-10D }, { -0.16624879837036133D, -2.6033824355191673E-8D } };
/*   0*/  
/* 108*/  private static final double[] SINE_TABLE_A = new double[14];
/*   0*/  
/* 111*/  private static final double[] SINE_TABLE_B = new double[14];
/*   0*/  
/* 114*/  private static final double[] COSINE_TABLE_A = new double[14];
/*   0*/  
/* 117*/  private static final double[] COSINE_TABLE_B = new double[14];
/*   0*/  
/* 120*/  private static final double[] TANGENT_TABLE_A = new double[14];
/*   0*/  
/* 123*/  private static final double[] TANGENT_TABLE_B = new double[14];
/*   0*/  
/* 126*/  private static long[] RECIP_2PI = new long[] { 
/* 126*/      2935890503282001226L, 9154082963658192752L, 3952090531849364496L, 9193070505571053912L, 7910884519577875640L, 113236205062349959L, 4577762542105553359L, -5034868814120038111L, 4208363204685324176L, 5648769086999809661L, 
/* 126*/      2819561105158720014L, -4035746434778044925L, -302932621132653753L, -2644281811660520851L, -3183605296591799669L, 6722166367014452318L, -3512299194304650054L, -7278142539171889152L };
/*   0*/  
/* 147*/  private static long[] PI_O_4_BITS = new long[] { -3958705157555305932L, -4267615245585081135L };
/*   0*/  
/* 155*/  private static final double[] EIGHTHES = new double[] { 
/* 155*/      0.0D, 0.125D, 0.25D, 0.375D, 0.5D, 0.625D, 0.75D, 0.875D, 1.0D, 1.125D, 
/* 155*/      1.25D, 1.375D, 1.5D, 1.625D };
/*   0*/  
/* 158*/  private static final double[] CBRTTWO = new double[] { 0.6299605249474366D, 0.7937005259840998D, 1.0D, 1.2599210498948732D, 1.5874010519681994D };
/*   0*/  
/*   0*/  static {
/* 169*/    FACT[0] = 1.0D;
/* 170*/    for (int i = 1; i < 20; i++)
/* 171*/      FACT[i] = FACT[i - 1] * i; 
/* 174*/    double[] tmp = new double[2];
/* 175*/    double[] recip = new double[2];
/* 178*/    for (int m = 0; m < 750; m++) {
/* 179*/      expint(m, tmp);
/* 180*/      EXP_INT_TABLE_A[m + 750] = tmp[0];
/* 181*/      EXP_INT_TABLE_B[m + 750] = tmp[1];
/* 183*/      if (m != 0) {
/* 185*/        splitReciprocal(tmp, recip);
/* 186*/        EXP_INT_TABLE_A[750 - m] = recip[0];
/* 187*/        EXP_INT_TABLE_B[750 - m] = recip[1];
/*   0*/      } 
/*   0*/    } 
/* 192*/    for (int k = 0; k < 1025; k++) {
/* 193*/      slowexp(k / 1024.0D, tmp);
/* 194*/      EXP_FRAC_TABLE_A[k] = tmp[0];
/* 195*/      EXP_FRAC_TABLE_B[k] = tmp[1];
/*   0*/    } 
/* 199*/    for (int j = 0; j < 1024; j++) {
/* 200*/      double d = Double.longBitsToDouble(j << 42L | 0x3FF0000000000000L);
/* 201*/      LN_MANT[j] = slowLog(d);
/*   0*/    } 
/* 205*/    buildSinCosTables();
/*   0*/  }
/*   0*/  
/*   0*/  public static double sqrt(double a) {
/* 219*/    return Math.sqrt(a);
/*   0*/  }
/*   0*/  
/*   0*/  public static double cosh(double x) {
/* 227*/    if (x != x)
/* 228*/      return x; 
/* 231*/    if (x > 20.0D)
/* 232*/      return exp(x) / 2.0D; 
/* 235*/    if (x < -20.0D)
/* 236*/      return exp(-x) / 2.0D; 
/* 239*/    double[] hiPrec = new double[2];
/* 240*/    if (x < 0.0D)
/* 241*/      x = -x; 
/* 243*/    exp(x, 0.0D, hiPrec);
/* 245*/    double ya = hiPrec[0] + hiPrec[1];
/* 246*/    double yb = -(ya - hiPrec[0] - hiPrec[1]);
/* 248*/    double temp = ya * 1.073741824E9D;
/* 249*/    double yaa = ya + temp - temp;
/* 250*/    double yab = ya - yaa;
/* 253*/    double recip = 1.0D / ya;
/* 254*/    temp = recip * 1.073741824E9D;
/* 255*/    double recipa = recip + temp - temp;
/* 256*/    double recipb = recip - recipa;
/* 259*/    recipb += (1.0D - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip;
/* 261*/    recipb += -yb * recip * recip;
/* 264*/    temp = ya + recipa;
/* 265*/    yb += -(temp - ya - recipa);
/* 266*/    ya = temp;
/* 267*/    temp = ya + recipb;
/* 268*/    yb += -(temp - ya - recipb);
/* 269*/    ya = temp;
/* 271*/    double result = ya + yb;
/* 272*/    result *= 0.5D;
/* 273*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double sinh(double x) {
/*   0*/    double result;
/*   0*/    boolean negate = false;
/* 282*/    if (x != x)
/* 283*/      return x; 
/* 286*/    if (x > 20.0D)
/* 287*/      return exp(x) / 2.0D; 
/* 290*/    if (x < -20.0D)
/* 291*/      return -exp(-x) / 2.0D; 
/* 294*/    if (x == 0.0D)
/* 295*/      return x; 
/* 298*/    if (x < 0.0D) {
/* 299*/      x = -x;
/* 300*/      negate = true;
/*   0*/    } 
/* 305*/    if (x > 0.25D) {
/* 306*/      double[] hiPrec = new double[2];
/* 307*/      exp(x, 0.0D, hiPrec);
/* 309*/      double ya = hiPrec[0] + hiPrec[1];
/* 310*/      double yb = -(ya - hiPrec[0] - hiPrec[1]);
/* 312*/      double temp = ya * 1.073741824E9D;
/* 313*/      double yaa = ya + temp - temp;
/* 314*/      double yab = ya - yaa;
/* 317*/      double recip = 1.0D / ya;
/* 318*/      temp = recip * 1.073741824E9D;
/* 319*/      double recipa = recip + temp - temp;
/* 320*/      double recipb = recip - recipa;
/* 323*/      recipb += (1.0D - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip;
/* 325*/      recipb += -yb * recip * recip;
/* 327*/      recipa = -recipa;
/* 328*/      recipb = -recipb;
/* 331*/      temp = ya + recipa;
/* 332*/      yb += -(temp - ya - recipa);
/* 333*/      ya = temp;
/* 334*/      temp = ya + recipb;
/* 335*/      yb += -(temp - ya - recipb);
/* 336*/      ya = temp;
/* 338*/      result = ya + yb;
/* 339*/      result *= 0.5D;
/*   0*/    } else {
/* 342*/      double[] hiPrec = new double[2];
/* 343*/      expm1(x, hiPrec);
/* 345*/      double ya = hiPrec[0] + hiPrec[1];
/* 346*/      double yb = -(ya - hiPrec[0] - hiPrec[1]);
/* 349*/      double denom = 1.0D + ya;
/* 350*/      double denomr = 1.0D / denom;
/* 351*/      double denomb = -(denom - 1.0D - ya) + yb;
/* 352*/      double ratio = ya * denomr;
/* 353*/      double temp = ratio * 1.073741824E9D;
/* 354*/      double ra = ratio + temp - temp;
/* 355*/      double rb = ratio - ra;
/* 357*/      temp = denom * 1.073741824E9D;
/* 358*/      double za = denom + temp - temp;
/* 359*/      double zb = denom - za;
/* 361*/      rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;
/* 364*/      rb += yb * denomr;
/* 365*/      rb += -ya * denomb * denomr * denomr;
/* 368*/      temp = ya + ra;
/* 369*/      yb += -(temp - ya - ra);
/* 370*/      ya = temp;
/* 371*/      temp = ya + rb;
/* 372*/      yb += -(temp - ya - rb);
/* 373*/      ya = temp;
/* 375*/      result = ya + yb;
/* 376*/      result *= 0.5D;
/*   0*/    } 
/* 379*/    if (negate)
/* 380*/      result = -result; 
/* 383*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double tanh(double x) {
/*   0*/    double result;
/*   0*/    boolean negate = false;
/* 393*/    if (x != x)
/* 394*/      return x; 
/* 397*/    if (x > 20.0D)
/* 398*/      return 1.0D; 
/* 401*/    if (x < -20.0D)
/* 402*/      return -1.0D; 
/* 405*/    if (x == 0.0D)
/* 406*/      return x; 
/* 409*/    if (x < 0.0D) {
/* 410*/      x = -x;
/* 411*/      negate = true;
/*   0*/    } 
/* 415*/    if (x >= 0.5D) {
/* 416*/      double[] hiPrec = new double[2];
/* 418*/      exp(x * 2.0D, 0.0D, hiPrec);
/* 420*/      double ya = hiPrec[0] + hiPrec[1];
/* 421*/      double yb = -(ya - hiPrec[0] - hiPrec[1]);
/* 424*/      double na = -1.0D + ya;
/* 425*/      double nb = -(na + 1.0D - ya);
/* 426*/      double temp = na + yb;
/* 427*/      nb += -(temp - na - yb);
/* 428*/      na = temp;
/* 431*/      double da = 1.0D + ya;
/* 432*/      double db = -(da - 1.0D - ya);
/* 433*/      temp = da + yb;
/* 434*/      db += -(temp - da - yb);
/* 435*/      da = temp;
/* 437*/      temp = da * 1.073741824E9D;
/* 438*/      double daa = da + temp - temp;
/* 439*/      double dab = da - daa;
/* 442*/      double ratio = na / da;
/* 443*/      temp = ratio * 1.073741824E9D;
/* 444*/      double ratioa = ratio + temp - temp;
/* 445*/      double ratiob = ratio - ratioa;
/* 448*/      ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da;
/* 451*/      ratiob += nb / da;
/* 453*/      ratiob += -db * na / da / da;
/* 455*/      result = ratioa + ratiob;
/*   0*/    } else {
/* 458*/      double[] hiPrec = new double[2];
/* 460*/      expm1(x * 2.0D, hiPrec);
/* 462*/      double ya = hiPrec[0] + hiPrec[1];
/* 463*/      double yb = -(ya - hiPrec[0] - hiPrec[1]);
/* 466*/      double na = ya;
/* 467*/      double nb = yb;
/* 470*/      double da = 2.0D + ya;
/* 471*/      double db = -(da - 2.0D - ya);
/* 472*/      double temp = da + yb;
/* 473*/      db += -(temp - da - yb);
/* 474*/      da = temp;
/* 476*/      temp = da * 1.073741824E9D;
/* 477*/      double daa = da + temp - temp;
/* 478*/      double dab = da - daa;
/* 481*/      double ratio = na / da;
/* 482*/      temp = ratio * 1.073741824E9D;
/* 483*/      double ratioa = ratio + temp - temp;
/* 484*/      double ratiob = ratio - ratioa;
/* 487*/      ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da;
/* 490*/      ratiob += nb / da;
/* 492*/      ratiob += -db * na / da / da;
/* 494*/      result = ratioa + ratiob;
/*   0*/    } 
/* 497*/    if (negate)
/* 498*/      result = -result; 
/* 501*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double acosh(double a) {
/* 509*/    return log(a + sqrt(a * a - 1.0D));
/*   0*/  }
/*   0*/  
/*   0*/  public static double asinh(double a) {
/*   0*/    double absAsinh;
/*   0*/    boolean negative = false;
/* 519*/    if (a < 0.0D) {
/* 520*/      negative = true;
/* 521*/      a = -a;
/*   0*/    } 
/* 525*/    if (a > 0.167D) {
/* 526*/      absAsinh = log(sqrt(a * a + 1.0D) + a);
/*   0*/    } else {
/* 528*/      double a2 = a * a;
/* 529*/      if (a > 0.097D) {
/* 530*/        absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * (0.2D - a2 * (0.14285714285714285D - a2 * (0.1111111111111111D - a2 * (0.09090909090909091D - a2 * (0.07692307692307693D - a2 * (0.06666666666666667D - a2 * 0.058823529411764705D * 15.0D / 16.0D) * 13.0D / 14.0D) * 11.0D / 12.0D) * 9.0D / 10.0D) * 7.0D / 8.0D) * 5.0D / 6.0D) * 3.0D / 4.0D) / 2.0D);
/* 531*/      } else if (a > 0.036D) {
/* 532*/        absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * (0.2D - a2 * (0.14285714285714285D - a2 * (0.1111111111111111D - a2 * (0.09090909090909091D - a2 * 0.07692307692307693D * 11.0D / 12.0D) * 9.0D / 10.0D) * 7.0D / 8.0D) * 5.0D / 6.0D) * 3.0D / 4.0D) / 2.0D);
/* 533*/      } else if (a > 0.0036D) {
/* 534*/        absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * (0.2D - a2 * (0.14285714285714285D - a2 * 0.1111111111111111D * 7.0D / 8.0D) * 5.0D / 6.0D) * 3.0D / 4.0D) / 2.0D);
/*   0*/      } else {
/* 536*/        absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * 0.2D * 3.0D / 4.0D) / 2.0D);
/*   0*/      } 
/*   0*/    } 
/* 540*/    return negative ? -absAsinh : absAsinh;
/*   0*/  }
/*   0*/  
/*   0*/  public static double atanh(double a) {
/*   0*/    double absAtanh;
/*   0*/    boolean negative = false;
/* 551*/    if (a < 0.0D) {
/* 552*/      negative = true;
/* 553*/      a = -a;
/*   0*/    } 
/* 557*/    if (a > 0.15D) {
/* 558*/      absAtanh = 0.5D * log((1.0D + a) / (1.0D - a));
/*   0*/    } else {
/* 560*/      double a2 = a * a;
/* 561*/      if (a > 0.087D) {
/* 562*/        absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * (0.2D + a2 * (0.14285714285714285D + a2 * (0.1111111111111111D + a2 * (0.09090909090909091D + a2 * (0.07692307692307693D + a2 * (0.06666666666666667D + a2 * 0.058823529411764705D))))))));
/* 563*/      } else if (a > 0.031D) {
/* 564*/        absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * (0.2D + a2 * (0.14285714285714285D + a2 * (0.1111111111111111D + a2 * (0.09090909090909091D + a2 * 0.07692307692307693D))))));
/* 565*/      } else if (a > 0.003D) {
/* 566*/        absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * (0.2D + a2 * (0.14285714285714285D + a2 * 0.1111111111111111D))));
/*   0*/      } else {
/* 568*/        absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * 0.2D));
/*   0*/      } 
/*   0*/    } 
/* 572*/    return negative ? -absAtanh : absAtanh;
/*   0*/  }
/*   0*/  
/*   0*/  public static double signum(double a) {
/* 582*/    return (a < 0.0D) ? -1.0D : ((a > 0.0D) ? 1.0D : (Double.isNaN(a) ? Double.NaN : 0.0D));
/*   0*/  }
/*   0*/  
/*   0*/  public static double nextUp(double a) {
/* 590*/    return nextAfter(a, Double.POSITIVE_INFINITY);
/*   0*/  }
/*   0*/  
/*   0*/  public static double random() {
/* 597*/    return Math.random();
/*   0*/  }
/*   0*/  
/*   0*/  public static double exp(double x) {
/* 621*/    return exp(x, 0.0D, null);
/*   0*/  }
/*   0*/  
/*   0*/  private static double exp(double x, double extra, double[] hiPrec) {
/*   0*/    double intPartA, intPartB;
/*   0*/    int intVal;
/*   0*/    double result;
/* 640*/    if (x < 0.0D) {
/* 641*/      intVal = (int)-x;
/* 643*/      if (intVal > 746) {
/* 644*/        if (hiPrec != null) {
/* 645*/          hiPrec[0] = 0.0D;
/* 646*/          hiPrec[1] = 0.0D;
/*   0*/        } 
/* 648*/        return 0.0D;
/*   0*/      } 
/* 651*/      if (intVal > 709) {
/* 653*/        double d = exp(x + 40.19140625D, extra, hiPrec) / 2.85040095144011776E17D;
/* 654*/        if (hiPrec != null) {
/* 655*/          hiPrec[0] = hiPrec[0] / 2.85040095144011776E17D;
/* 656*/          hiPrec[1] = hiPrec[1] / 2.85040095144011776E17D;
/*   0*/        } 
/* 658*/        return d;
/*   0*/      } 
/* 661*/      if (intVal == 709) {
/* 663*/        double d = exp(x + 1.494140625D, extra, hiPrec) / 4.455505956692757D;
/* 664*/        if (hiPrec != null) {
/* 665*/          hiPrec[0] = hiPrec[0] / 4.455505956692757D;
/* 666*/          hiPrec[1] = hiPrec[1] / 4.455505956692757D;
/*   0*/        } 
/* 668*/        return d;
/*   0*/      } 
/* 671*/      intVal++;
/* 673*/      intPartA = EXP_INT_TABLE_A[750 - intVal];
/* 674*/      intPartB = EXP_INT_TABLE_B[750 - intVal];
/* 676*/      intVal = -intVal;
/*   0*/    } else {
/* 678*/      intVal = (int)x;
/* 680*/      if (intVal > 709) {
/* 681*/        if (hiPrec != null) {
/* 682*/          hiPrec[0] = Double.POSITIVE_INFINITY;
/* 683*/          hiPrec[1] = 0.0D;
/*   0*/        } 
/* 685*/        return Double.POSITIVE_INFINITY;
/*   0*/      } 
/* 688*/      intPartA = EXP_INT_TABLE_A[750 + intVal];
/* 689*/      intPartB = EXP_INT_TABLE_B[750 + intVal];
/*   0*/    } 
/* 696*/    int intFrac = (int)((x - intVal) * 1024.0D);
/* 697*/    double fracPartA = EXP_FRAC_TABLE_A[intFrac];
/* 698*/    double fracPartB = EXP_FRAC_TABLE_B[intFrac];
/* 704*/    double epsilon = x - intVal + intFrac / 1024.0D;
/* 713*/    double z = 0.04168701738764507D;
/* 714*/    z = z * epsilon + 0.1666666505023083D;
/* 715*/    z = z * epsilon + 0.5000000000042687D;
/* 716*/    z = z * epsilon + 1.0D;
/* 717*/    z = z * epsilon + -3.940510424527919E-20D;
/* 724*/    double tempA = intPartA * fracPartA;
/* 725*/    double tempB = intPartA * fracPartB + intPartB * fracPartA + intPartB * fracPartB;
/* 731*/    double tempC = tempB + tempA;
/* 733*/    if (extra != 0.0D) {
/* 734*/      result = tempC * extra * z + tempC * extra + tempC * z + tempB + tempA;
/*   0*/    } else {
/* 736*/      result = tempC * z + tempB + tempA;
/*   0*/    } 
/* 739*/    if (hiPrec != null) {
/* 741*/      hiPrec[0] = tempA;
/* 742*/      hiPrec[1] = tempC * extra * z + tempC * extra + tempC * z + tempB;
/*   0*/    } 
/* 745*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double expm1(double x) {
/* 753*/    return expm1(x, null);
/*   0*/  }
/*   0*/  
/*   0*/  private static double expm1(double x, double[] hiPrecOut) {
/* 762*/    if (x != x || x == 0.0D)
/* 763*/      return x; 
/* 766*/    if (x <= -1.0D || x >= 1.0D) {
/* 769*/      double[] hiPrec = new double[2];
/* 770*/      exp(x, 0.0D, hiPrec);
/* 771*/      if (x > 0.0D)
/* 772*/        return -1.0D + hiPrec[0] + hiPrec[1]; 
/* 774*/      double ra = -1.0D + hiPrec[0];
/* 775*/      double rb = -(ra + 1.0D - hiPrec[0]);
/* 776*/      rb += hiPrec[1];
/* 777*/      return ra + rb;
/*   0*/    } 
/*   0*/    boolean negative = false;
/* 786*/    if (x < 0.0D) {
/* 787*/      x = -x;
/* 788*/      negative = true;
/*   0*/    } 
/* 792*/    int intFrac = (int)(x * 1024.0D);
/* 793*/    double tempA = EXP_FRAC_TABLE_A[intFrac] - 1.0D;
/* 794*/    double tempB = EXP_FRAC_TABLE_B[intFrac];
/* 796*/    double d1 = tempA + tempB;
/* 797*/    tempB = -(d1 - tempA - tempB);
/* 798*/    tempA = d1;
/* 800*/    d1 = tempA * 1.073741824E9D;
/* 801*/    double baseA = tempA + d1 - d1;
/* 802*/    double baseB = tempB + tempA - baseA;
/* 804*/    double epsilon = x - intFrac / 1024.0D;
/* 809*/    double zb = 0.008336750013465571D;
/* 810*/    zb = zb * epsilon + 0.041666663879186654D;
/* 811*/    zb = zb * epsilon + 0.16666666666745392D;
/* 812*/    zb = zb * epsilon + 0.49999999999999994D;
/* 813*/    zb *= epsilon;
/* 814*/    zb *= epsilon;
/* 816*/    double za = epsilon;
/* 817*/    double temp = za + zb;
/* 818*/    zb = -(temp - za - zb);
/* 819*/    za = temp;
/* 821*/    temp = za * 1.073741824E9D;
/* 822*/    temp = za + temp - temp;
/* 823*/    zb += za - temp;
/* 824*/    za = temp;
/* 827*/    double ya = za * baseA;
/* 829*/    temp = ya + za * baseB;
/* 830*/    double yb = -(temp - ya - za * baseB);
/* 831*/    ya = temp;
/* 833*/    temp = ya + zb * baseA;
/* 834*/    yb += -(temp - ya - zb * baseA);
/* 835*/    ya = temp;
/* 837*/    temp = ya + zb * baseB;
/* 838*/    yb += -(temp - ya - zb * baseB);
/* 839*/    ya = temp;
/* 843*/    temp = ya + baseA;
/* 844*/    yb += -(temp - baseA - ya);
/* 845*/    ya = temp;
/* 847*/    temp = ya + za;
/* 849*/    yb += -(temp - ya - za);
/* 850*/    ya = temp;
/* 852*/    temp = ya + baseB;
/* 854*/    yb += -(temp - ya - baseB);
/* 855*/    ya = temp;
/* 857*/    temp = ya + zb;
/* 859*/    yb += -(temp - ya - zb);
/* 860*/    ya = temp;
/* 862*/    if (negative) {
/* 864*/      double denom = 1.0D + ya;
/* 865*/      double denomr = 1.0D / denom;
/* 866*/      double denomb = -(denom - 1.0D - ya) + yb;
/* 867*/      double ratio = ya * denomr;
/* 868*/      temp = ratio * 1.073741824E9D;
/* 869*/      double ra = ratio + temp - temp;
/* 870*/      double rb = ratio - ra;
/* 872*/      temp = denom * 1.073741824E9D;
/* 873*/      za = denom + temp - temp;
/* 874*/      zb = denom - za;
/* 876*/      rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;
/* 887*/      rb += yb * denomr;
/* 888*/      rb += -ya * denomb * denomr * denomr;
/* 891*/      ya = -ra;
/* 892*/      yb = -rb;
/*   0*/    } 
/* 895*/    if (hiPrecOut != null) {
/* 896*/      hiPrecOut[0] = ya;
/* 897*/      hiPrecOut[1] = yb;
/*   0*/    } 
/* 900*/    return ya + yb;
/*   0*/  }
/*   0*/  
/*   0*/  private static double slowexp(double x, double[] result) {
/* 911*/    double[] xs = new double[2];
/* 912*/    double[] ys = new double[2];
/* 913*/    double[] facts = new double[2];
/* 914*/    double[] as = new double[2];
/* 915*/    split(x, xs);
/* 916*/    ys[1] = 0.0D;
/* 916*/    ys[0] = 0.0D;
/* 918*/    for (int i = 19; i >= 0; i--) {
/* 919*/      splitMult(xs, ys, as);
/* 920*/      ys[0] = as[0];
/* 921*/      ys[1] = as[1];
/* 923*/      split(FACT[i], as);
/* 924*/      splitReciprocal(as, facts);
/* 926*/      splitAdd(ys, facts, as);
/* 927*/      ys[0] = as[0];
/* 928*/      ys[1] = as[1];
/*   0*/    } 
/* 931*/    if (result != null) {
/* 932*/      result[0] = ys[0];
/* 933*/      result[1] = ys[1];
/*   0*/    } 
/* 936*/    return ys[0] + ys[1];
/*   0*/  }
/*   0*/  
/*   0*/  private static void split(double d, double[] split) {
/* 945*/    if (d < 8.0E298D && d > -8.0E298D) {
/* 946*/      double a = d * 1.073741824E9D;
/* 947*/      split[0] = d + a - a;
/* 948*/      split[1] = d - split[0];
/*   0*/    } else {
/* 950*/      double a = d * 9.313225746154785E-10D;
/* 951*/      split[0] = (d + a - d) * 1.073741824E9D;
/* 952*/      split[1] = d - split[0];
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void resplit(double[] a) {
/* 961*/    double c = a[0] + a[1];
/* 962*/    double d = -(c - a[0] - a[1]);
/* 964*/    if (c < 8.0E298D && c > -8.0E298D) {
/* 965*/      double z = c * 1.073741824E9D;
/* 966*/      a[0] = c + z - z;
/* 967*/      a[1] = c - a[0] + d;
/*   0*/    } else {
/* 969*/      double z = c * 9.313225746154785E-10D;
/* 970*/      a[0] = (c + z - c) * 1.073741824E9D;
/* 971*/      a[1] = c - a[0] + d;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void splitMult(double[] a, double[] b, double[] ans) {
/* 981*/    ans[0] = a[0] * b[0];
/* 982*/    ans[1] = a[0] * b[1] + a[1] * b[0] + a[1] * b[1];
/* 985*/    resplit(ans);
/*   0*/  }
/*   0*/  
/*   0*/  private static void splitAdd(double[] a, double[] b, double[] ans) {
/* 994*/    ans[0] = a[0] + b[0];
/* 995*/    ans[1] = a[1] + b[1];
/* 997*/    resplit(ans);
/*   0*/  }
/*   0*/  
/*   0*/  private static void splitReciprocal(double[] in, double[] result) {
/*1019*/    double b = 2.384185791015625E-7D;
/*1020*/    double a = 0.9999997615814209D;
/*1022*/    if (in[0] == 0.0D) {
/*1023*/      in[0] = in[1];
/*1024*/      in[1] = 0.0D;
/*   0*/    } 
/*1027*/    result[0] = 0.9999997615814209D / in[0];
/*1028*/    result[1] = (2.384185791015625E-7D * in[0] - 0.9999997615814209D * in[1]) / (in[0] * in[0] + in[0] * in[1]);
/*1030*/    if (result[1] != result[1])
/*1031*/      result[1] = 0.0D; 
/*1035*/    resplit(result);
/*1037*/    for (int i = 0; i < 2; i++) {
/*1039*/      double err = 1.0D - result[0] * in[0] - result[0] * in[1] - result[1] * in[0] - result[1] * in[1];
/*1042*/      err *= result[0] + result[1];
/*1044*/      result[1] = result[1] + err;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static void quadMult(double[] a, double[] b, double[] result) {
/*1054*/    double[] xs = new double[2];
/*1055*/    double[] ys = new double[2];
/*1056*/    double[] zs = new double[2];
/*1059*/    split(a[0], xs);
/*1060*/    split(b[0], ys);
/*1061*/    splitMult(xs, ys, zs);
/*1063*/    result[0] = zs[0];
/*1064*/    result[1] = zs[1];
/*1067*/    split(b[1], ys);
/*1068*/    splitMult(xs, ys, zs);
/*1070*/    double tmp = result[0] + zs[0];
/*1071*/    result[1] = result[1] - tmp - result[0] - zs[0];
/*1072*/    result[0] = tmp;
/*1073*/    tmp = result[0] + zs[1];
/*1074*/    result[1] = result[1] - tmp - result[0] - zs[1];
/*1075*/    result[0] = tmp;
/*1078*/    split(a[1], xs);
/*1079*/    split(b[0], ys);
/*1080*/    splitMult(xs, ys, zs);
/*1082*/    tmp = result[0] + zs[0];
/*1083*/    result[1] = result[1] - tmp - result[0] - zs[0];
/*1084*/    result[0] = tmp;
/*1085*/    tmp = result[0] + zs[1];
/*1086*/    result[1] = result[1] - tmp - result[0] - zs[1];
/*1087*/    result[0] = tmp;
/*1090*/    split(a[1], xs);
/*1091*/    split(b[1], ys);
/*1092*/    splitMult(xs, ys, zs);
/*1094*/    tmp = result[0] + zs[0];
/*1095*/    result[1] = result[1] - tmp - result[0] - zs[0];
/*1096*/    result[0] = tmp;
/*1097*/    tmp = result[0] + zs[1];
/*1098*/    result[1] = result[1] - tmp - result[0] - zs[1];
/*1099*/    result[0] = tmp;
/*   0*/  }
/*   0*/  
/*   0*/  private static double expint(int p, double[] result) {
/*1109*/    double[] xs = new double[2];
/*1110*/    double[] as = new double[2];
/*1111*/    double[] ys = new double[2];
/*1120*/    xs[0] = Math.E;
/*1121*/    xs[1] = 1.4456468917292502E-16D;
/*1123*/    split(1.0D, ys);
/*1125*/    while (p > 0) {
/*1126*/      if ((p & 0x1) != 0) {
/*1127*/        quadMult(ys, xs, as);
/*1128*/        ys[0] = as[0];
/*1128*/        ys[1] = as[1];
/*   0*/      } 
/*1131*/      quadMult(xs, xs, as);
/*1132*/      xs[0] = as[0];
/*1132*/      xs[1] = as[1];
/*1134*/      p >>= 1;
/*   0*/    } 
/*1137*/    if (result != null) {
/*1138*/      result[0] = ys[0];
/*1139*/      result[1] = ys[1];
/*1141*/      resplit(result);
/*   0*/    } 
/*1144*/    return ys[0] + ys[1];
/*   0*/  }
/*   0*/  
/*   0*/  public static double log(double x) {
/*1155*/    return log(x, null);
/*   0*/  }
/*   0*/  
/*   0*/  private static double log(double x, double[] hiPrec) {
/*1165*/    long bits = Double.doubleToLongBits(x);
/*1168*/    if (((bits & Long.MIN_VALUE) != 0L || x != x) && 
/*1169*/      x != 0.0D) {
/*1170*/      if (hiPrec != null)
/*1171*/        hiPrec[0] = Double.NaN; 
/*1174*/      return Double.NaN;
/*   0*/    } 
/*1179*/    if (x == Double.POSITIVE_INFINITY) {
/*1180*/      if (hiPrec != null)
/*1181*/        hiPrec[0] = Double.POSITIVE_INFINITY; 
/*1184*/      return Double.POSITIVE_INFINITY;
/*   0*/    } 
/*1188*/    int exp = (int)(bits >> 52L) - 1023;
/*1190*/    if ((bits & 0x7FF0000000000000L) == 0L) {
/*1192*/      if (x == 0.0D) {
/*1194*/        if (hiPrec != null)
/*1195*/          hiPrec[0] = Double.NEGATIVE_INFINITY; 
/*1198*/        return Double.NEGATIVE_INFINITY;
/*   0*/      } 
/*1202*/      bits <<= 1L;
/*1203*/      while ((bits & 0x10000000000000L) == 0L) {
/*1204*/        exp--;
/*1205*/        bits <<= 1L;
/*   0*/      } 
/*   0*/    } 
/*1210*/    if ((exp == -1 || exp == 0) && 
/*1211*/      x < 1.01D && x > 0.99D && hiPrec == null) {
/*1216*/      double xa = x - 1.0D;
/*1217*/      double xb = xa - x + 1.0D;
/*1218*/      double tmp = xa * 1.073741824E9D;
/*1219*/      double aa = xa + tmp - tmp;
/*1220*/      double ab = xa - aa;
/*1221*/      xa = aa;
/*1222*/      xb = ab;
/*1224*/      double ya = LN_QUICK_COEF[LN_QUICK_COEF.length - 1][0];
/*1225*/      double yb = LN_QUICK_COEF[LN_QUICK_COEF.length - 1][1];
/*1227*/      for (int i = LN_QUICK_COEF.length - 2; i >= 0; i--) {
/*1229*/        aa = ya * xa;
/*1230*/        ab = ya * xb + yb * xa + yb * xb;
/*1232*/        tmp = aa * 1.073741824E9D;
/*1233*/        ya = aa + tmp - tmp;
/*1234*/        yb = aa - ya + ab;
/*1237*/        aa = ya + LN_QUICK_COEF[i][0];
/*1238*/        ab = yb + LN_QUICK_COEF[i][1];
/*1240*/        tmp = aa * 1.073741824E9D;
/*1241*/        ya = aa + tmp - tmp;
/*1242*/        yb = aa - ya + ab;
/*   0*/      } 
/*1246*/      aa = ya * xa;
/*1247*/      ab = ya * xb + yb * xa + yb * xb;
/*1249*/      tmp = aa * 1.073741824E9D;
/*1250*/      ya = aa + tmp - tmp;
/*1251*/      yb = aa - ya + ab;
/*1253*/      return ya + yb;
/*   0*/    } 
/*1258*/    double[] lnm = LN_MANT[(int)((bits & 0xFFC0000000000L) >> 42L)];
/*1269*/    double epsilon = (bits & 0x3FFFFFFFFFFL) / (4.503599627370496E15D + (bits & 0xFFC0000000000L));
/*1271*/    double lnza = 0.0D;
/*1272*/    double lnzb = 0.0D;
/*1274*/    if (hiPrec != null) {
/*1276*/      double tmp = epsilon * 1.073741824E9D;
/*1277*/      double aa = epsilon + tmp - tmp;
/*1278*/      double ab = epsilon - aa;
/*1279*/      double xa = aa;
/*1280*/      double xb = ab;
/*1283*/      double numer = (bits & 0x3FFFFFFFFFFL);
/*1284*/      double denom = 4.503599627370496E15D + (bits & 0xFFC0000000000L);
/*1285*/      aa = numer - xa * denom - xb * denom;
/*1286*/      xb += aa / denom;
/*1289*/      double ya = LN_HI_PREC_COEF[LN_HI_PREC_COEF.length - 1][0];
/*1290*/      double yb = LN_HI_PREC_COEF[LN_HI_PREC_COEF.length - 1][1];
/*1292*/      for (int i = LN_HI_PREC_COEF.length - 2; i >= 0; i--) {
/*1294*/        aa = ya * xa;
/*1295*/        ab = ya * xb + yb * xa + yb * xb;
/*1297*/        tmp = aa * 1.073741824E9D;
/*1298*/        ya = aa + tmp - tmp;
/*1299*/        yb = aa - ya + ab;
/*1302*/        aa = ya + LN_HI_PREC_COEF[i][0];
/*1303*/        ab = yb + LN_HI_PREC_COEF[i][1];
/*1305*/        tmp = aa * 1.073741824E9D;
/*1306*/        ya = aa + tmp - tmp;
/*1307*/        yb = aa - ya + ab;
/*   0*/      } 
/*1311*/      aa = ya * xa;
/*1312*/      ab = ya * xb + yb * xa + yb * xb;
/*1320*/      lnza = aa + ab;
/*1321*/      lnzb = -(lnza - aa - ab);
/*   0*/    } else {
/*1325*/      lnza = -0.16624882440418567D;
/*1326*/      lnza = lnza * epsilon + 0.19999954120254515D;
/*1327*/      lnza = lnza * epsilon + -0.2499999997677497D;
/*1328*/      lnza = lnza * epsilon + 0.3333333333332802D;
/*1329*/      lnza = lnza * epsilon + -0.5D;
/*1330*/      lnza = lnza * epsilon + 1.0D;
/*1331*/      lnza *= epsilon;
/*   0*/    } 
/*1348*/    double a = 0.6931470632553101D * exp;
/*1349*/    double b = 0.0D;
/*1350*/    double c = a + lnm[0];
/*1351*/    double d = -(c - a - lnm[0]);
/*1352*/    a = c;
/*1353*/    b += d;
/*1355*/    c = a + lnza;
/*1356*/    d = -(c - a - lnza);
/*1357*/    a = c;
/*1358*/    b += d;
/*1360*/    c = a + 1.1730463525082348E-7D * exp;
/*1361*/    d = -(c - a - 1.1730463525082348E-7D * exp);
/*1362*/    a = c;
/*1363*/    b += d;
/*1365*/    c = a + lnm[1];
/*1366*/    d = -(c - a - lnm[1]);
/*1367*/    a = c;
/*1368*/    b += d;
/*1370*/    c = a + lnzb;
/*1371*/    d = -(c - a - lnzb);
/*1372*/    a = c;
/*1373*/    b += d;
/*1375*/    if (hiPrec != null) {
/*1376*/      hiPrec[0] = a;
/*1377*/      hiPrec[1] = b;
/*   0*/    } 
/*1380*/    return a + b;
/*   0*/  }
/*   0*/  
/*   0*/  public static double log1p(double x) {
/*1388*/    double xpa = 1.0D + x;
/*1389*/    double xpb = -(xpa - 1.0D - x);
/*1391*/    if (x == -1.0D)
/*1392*/      return x / 0.0D; 
/*1395*/    if (x > 0.0D && 1.0D / x == 0.0D)
/*1396*/      return x; 
/*1399*/    if (x > 1.0E-6D || x < -1.0E-6D) {
/*1400*/      double[] hiPrec = new double[2];
/*1402*/      log(xpa, hiPrec);
/*1406*/      double fx1 = xpb / xpa;
/*1408*/      double epsilon = 0.5D * fx1 + 1.0D;
/*1409*/      epsilon *= fx1;
/*1411*/      return epsilon + hiPrec[1] + hiPrec[0];
/*   0*/    } 
/*1415*/    double y = x * 0.333333333333333D - 0.5D;
/*1416*/    y = y * x + 1.0D;
/*1417*/    y *= x;
/*1419*/    return y;
/*   0*/  }
/*   0*/  
/*   0*/  public static double log10(double x) {
/*1427*/    double[] hiPrec = new double[2];
/*1429*/    log(x, hiPrec);
/*1431*/    double tmp = hiPrec[0] * 1.073741824E9D;
/*1432*/    double lna = hiPrec[0] + tmp - tmp;
/*1433*/    double lnb = hiPrec[0] - lna + hiPrec[1];
/*1435*/    double rln10a = 0.4342944622039795D;
/*1436*/    double rln10b = 1.9699272335463627E-8D;
/*1438*/    return 1.9699272335463627E-8D * lnb + 1.9699272335463627E-8D * lna + 0.4342944622039795D * lnb + 0.4342944622039795D * lna;
/*   0*/  }
/*   0*/  
/*   0*/  public static double pow(double x, double y) {
/*   0*/    double ya, yb;
/*1449*/    double[] lns = new double[2];
/*1451*/    if (y == 0.0D)
/*1452*/      return 1.0D; 
/*1455*/    if (x != x)
/*1456*/      return x; 
/*1460*/    if (x == 0.0D) {
/*1461*/      long bits = Double.doubleToLongBits(x);
/*1462*/      if ((bits & Long.MIN_VALUE) != 0L) {
/*1464*/        long yi = (long)y;
/*1466*/        if (y < 0.0D && y == yi && (yi & 0x1L) == 1L)
/*1467*/          return Double.NEGATIVE_INFINITY; 
/*1470*/        if (y < 0.0D && y == yi && (yi & 0x1L) == 1L)
/*1471*/          return -0.0D; 
/*1474*/        if (y > 0.0D && y == yi && (yi & 0x1L) == 1L)
/*1475*/          return -0.0D; 
/*   0*/      } 
/*1479*/      if (y < 0.0D)
/*1480*/        return Double.POSITIVE_INFINITY; 
/*1482*/      if (y > 0.0D)
/*1483*/        return 0.0D; 
/*1486*/      return Double.NaN;
/*   0*/    } 
/*1489*/    if (x == Double.POSITIVE_INFINITY) {
/*1490*/      if (y != y)
/*1491*/        return y; 
/*1493*/      if (y < 0.0D)
/*1494*/        return 0.0D; 
/*1496*/      return Double.POSITIVE_INFINITY;
/*   0*/    } 
/*1500*/    if (y == Double.POSITIVE_INFINITY) {
/*1501*/      if (x * x == 1.0D)
/*1502*/        return Double.NaN; 
/*1504*/      if (x * x > 1.0D)
/*1505*/        return Double.POSITIVE_INFINITY; 
/*1507*/      return 0.0D;
/*   0*/    } 
/*1511*/    if (x == Double.NEGATIVE_INFINITY) {
/*1512*/      if (y != y)
/*1513*/        return y; 
/*1516*/      if (y < 0.0D) {
/*1517*/        long yi = (long)y;
/*1518*/        if (y == yi && (yi & 0x1L) == 1L)
/*1519*/          return -0.0D; 
/*1522*/        return 0.0D;
/*   0*/      } 
/*1525*/      if (y > 0.0D) {
/*1526*/        long yi = (long)y;
/*1527*/        if (y == yi && (yi & 0x1L) == 1L)
/*1528*/          return Double.NEGATIVE_INFINITY; 
/*1531*/        return Double.POSITIVE_INFINITY;
/*   0*/      } 
/*   0*/    } 
/*1535*/    if (y == Double.NEGATIVE_INFINITY) {
/*1537*/      if (x * x == 1.0D)
/*1538*/        return Double.NaN; 
/*1541*/      if (x * x < 1.0D)
/*1542*/        return Double.POSITIVE_INFINITY; 
/*1544*/      return 0.0D;
/*   0*/    } 
/*1549*/    if (x < 0.0D) {
/*1551*/      if (y >= 4.503599627370496E15D || y <= -4.503599627370496E15D)
/*1552*/        return pow(-x, y); 
/*1555*/      if (y == (long)y)
/*1557*/        return (((long)y & 0x1L) == 0L) ? pow(-x, y) : -pow(-x, y); 
/*1559*/      return Double.NaN;
/*   0*/    } 
/*1566*/    if (y < 8.0E298D && y > -8.0E298D) {
/*1567*/      double d = y * 1.073741824E9D;
/*1568*/      ya = y + d - d;
/*1569*/      yb = y - ya;
/*   0*/    } else {
/*1571*/      double d1 = y * 9.313225746154785E-10D;
/*1572*/      double d2 = d1 * 9.313225746154785E-10D;
/*1573*/      ya = (d1 + d2 - d1) * 1.073741824E9D * 1.073741824E9D;
/*1574*/      yb = y - ya;
/*   0*/    } 
/*1578*/    log(x, lns);
/*1579*/    double lna = lns[0];
/*1580*/    double lnb = lns[1];
/*1583*/    double tmp1 = lna * 1.073741824E9D;
/*1584*/    double tmp2 = lna + tmp1 - tmp1;
/*1585*/    lnb += lna - tmp2;
/*1586*/    lna = tmp2;
/*1589*/    double aa = lna * ya;
/*1590*/    double ab = lna * yb + lnb * ya + lnb * yb;
/*1592*/    lna = aa + ab;
/*1593*/    lnb = -(lna - aa - ab);
/*1595*/    double z = 0.008333333333333333D;
/*1596*/    z = z * lnb + 0.041666666666666664D;
/*1597*/    z = z * lnb + 0.16666666666666666D;
/*1598*/    z = z * lnb + 0.5D;
/*1599*/    z = z * lnb + 1.0D;
/*1600*/    z *= lnb;
/*1602*/    double result = exp(lna, z, null);
/*1604*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private static double[] slowLog(double xi) {
/*1627*/    double[] x = new double[2];
/*1628*/    double[] x2 = new double[2];
/*1629*/    double[] y = new double[2];
/*1630*/    double[] a = new double[2];
/*1632*/    split(xi, x);
/*1635*/    x[0] = x[0] + 1.0D;
/*1636*/    resplit(x);
/*1637*/    splitReciprocal(x, a);
/*1638*/    x[0] = x[0] - 2.0D;
/*1639*/    resplit(x);
/*1640*/    splitMult(x, a, y);
/*1641*/    x[0] = y[0];
/*1642*/    x[1] = y[1];
/*1645*/    splitMult(x, x, x2);
/*1651*/    y[0] = LN_SPLIT_COEF[LN_SPLIT_COEF.length - 1][0];
/*1652*/    y[1] = LN_SPLIT_COEF[LN_SPLIT_COEF.length - 1][1];
/*1654*/    for (int i = LN_SPLIT_COEF.length - 2; i >= 0; i--) {
/*1655*/      splitMult(y, x2, a);
/*1656*/      y[0] = a[0];
/*1657*/      y[1] = a[1];
/*1658*/      splitAdd(y, LN_SPLIT_COEF[i], a);
/*1659*/      y[0] = a[0];
/*1660*/      y[1] = a[1];
/*   0*/    } 
/*1663*/    splitMult(y, x, a);
/*1664*/    y[0] = a[0];
/*1665*/    y[1] = a[1];
/*1667*/    return y;
/*   0*/  }
/*   0*/  
/*   0*/  private static double slowSin(double x, double[] result) {
/*1677*/    double[] xs = new double[2];
/*1678*/    double[] ys = new double[2];
/*1679*/    double[] facts = new double[2];
/*1680*/    double[] as = new double[2];
/*1681*/    split(x, xs);
/*1682*/    ys[1] = 0.0D;
/*1682*/    ys[0] = 0.0D;
/*1684*/    for (int i = 19; i >= 0; i--) {
/*1685*/      splitMult(xs, ys, as);
/*1686*/      ys[0] = as[0];
/*1686*/      ys[1] = as[1];
/*1688*/      if ((i & 0x1) != 0) {
/*1692*/        split(FACT[i], as);
/*1693*/        splitReciprocal(as, facts);
/*1695*/        if ((i & 0x2) != 0) {
/*1696*/          facts[0] = -facts[0];
/*1697*/          facts[1] = -facts[1];
/*   0*/        } 
/*1700*/        splitAdd(ys, facts, as);
/*1701*/        ys[0] = as[0];
/*1701*/        ys[1] = as[1];
/*   0*/      } 
/*   0*/    } 
/*1704*/    if (result != null) {
/*1705*/      result[0] = ys[0];
/*1706*/      result[1] = ys[1];
/*   0*/    } 
/*1709*/    return ys[0] + ys[1];
/*   0*/  }
/*   0*/  
/*   0*/  private static double slowCos(double x, double[] result) {
/*1720*/    double[] xs = new double[2];
/*1721*/    double[] ys = new double[2];
/*1722*/    double[] facts = new double[2];
/*1723*/    double[] as = new double[2];
/*1724*/    split(x, xs);
/*1725*/    ys[1] = 0.0D;
/*1725*/    ys[0] = 0.0D;
/*1727*/    for (int i = 19; i >= 0; i--) {
/*1728*/      splitMult(xs, ys, as);
/*1729*/      ys[0] = as[0];
/*1729*/      ys[1] = as[1];
/*1731*/      if ((i & 0x1) == 0) {
/*1735*/        split(FACT[i], as);
/*1736*/        splitReciprocal(as, facts);
/*1738*/        if ((i & 0x2) != 0) {
/*1739*/          facts[0] = -facts[0];
/*1740*/          facts[1] = -facts[1];
/*   0*/        } 
/*1743*/        splitAdd(ys, facts, as);
/*1744*/        ys[0] = as[0];
/*1744*/        ys[1] = as[1];
/*   0*/      } 
/*   0*/    } 
/*1747*/    if (result != null) {
/*1748*/      result[0] = ys[0];
/*1749*/      result[1] = ys[1];
/*   0*/    } 
/*1752*/    return ys[0] + ys[1];
/*   0*/  }
/*   0*/  
/*   0*/  private static void buildSinCosTables() {
/*1758*/    double[] result = new double[2];
/*1761*/    for (int i = 0; i < 7; i++) {
/*1762*/      double x = i / 8.0D;
/*1764*/      slowSin(x, result);
/*1765*/      SINE_TABLE_A[i] = result[0];
/*1766*/      SINE_TABLE_B[i] = result[1];
/*1768*/      slowCos(x, result);
/*1769*/      COSINE_TABLE_A[i] = result[0];
/*1770*/      COSINE_TABLE_B[i] = result[1];
/*   0*/    } 
/*1774*/    for (int k = 7; k < 14; k++) {
/*1775*/      double[] xs = new double[2];
/*1776*/      double[] ys = new double[2];
/*1777*/      double[] as = new double[2];
/*1778*/      double[] bs = new double[2];
/*1779*/      double[] temps = new double[2];
/*1781*/      if ((k & 0x1) == 0) {
/*1783*/        xs[0] = SINE_TABLE_A[k / 2];
/*1784*/        xs[1] = SINE_TABLE_B[k / 2];
/*1785*/        ys[0] = COSINE_TABLE_A[k / 2];
/*1786*/        ys[1] = COSINE_TABLE_B[k / 2];
/*1789*/        splitMult(xs, ys, result);
/*1790*/        SINE_TABLE_A[k] = result[0] * 2.0D;
/*1791*/        SINE_TABLE_B[k] = result[1] * 2.0D;
/*1794*/        splitMult(ys, ys, as);
/*1795*/        splitMult(xs, xs, temps);
/*1796*/        temps[0] = -temps[0];
/*1797*/        temps[1] = -temps[1];
/*1798*/        splitAdd(as, temps, result);
/*1799*/        COSINE_TABLE_A[k] = result[0];
/*1800*/        COSINE_TABLE_B[k] = result[1];
/*   0*/      } else {
/*1802*/        xs[0] = SINE_TABLE_A[k / 2];
/*1803*/        xs[1] = SINE_TABLE_B[k / 2];
/*1804*/        ys[0] = COSINE_TABLE_A[k / 2];
/*1805*/        ys[1] = COSINE_TABLE_B[k / 2];
/*1806*/        as[0] = SINE_TABLE_A[k / 2 + 1];
/*1807*/        as[1] = SINE_TABLE_B[k / 2 + 1];
/*1808*/        bs[0] = COSINE_TABLE_A[k / 2 + 1];
/*1809*/        bs[1] = COSINE_TABLE_B[k / 2 + 1];
/*1812*/        splitMult(xs, bs, temps);
/*1813*/        splitMult(ys, as, result);
/*1814*/        splitAdd(result, temps, result);
/*1815*/        SINE_TABLE_A[k] = result[0];
/*1816*/        SINE_TABLE_B[k] = result[1];
/*1819*/        splitMult(ys, bs, result);
/*1820*/        splitMult(xs, as, temps);
/*1821*/        temps[0] = -temps[0];
/*1822*/        temps[1] = -temps[1];
/*1823*/        splitAdd(result, temps, result);
/*1824*/        COSINE_TABLE_A[k] = result[0];
/*1825*/        COSINE_TABLE_B[k] = result[1];
/*   0*/      } 
/*   0*/    } 
/*1830*/    for (int j = 0; j < 14; j++) {
/*1831*/      double[] xs = new double[2];
/*1832*/      double[] ys = new double[2];
/*1833*/      double[] as = new double[2];
/*1835*/      as[0] = COSINE_TABLE_A[j];
/*1836*/      as[1] = COSINE_TABLE_B[j];
/*1838*/      splitReciprocal(as, ys);
/*1840*/      xs[0] = SINE_TABLE_A[j];
/*1841*/      xs[1] = SINE_TABLE_B[j];
/*1843*/      splitMult(xs, ys, as);
/*1845*/      TANGENT_TABLE_A[j] = as[0];
/*1846*/      TANGENT_TABLE_B[j] = as[1];
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static double polySine(double x) {
/*1859*/    double x2 = x * x;
/*1861*/    double p = 2.7553817452272217E-6D;
/*1862*/    p = p * x2 + -1.9841269659586505E-4D;
/*1863*/    p = p * x2 + 0.008333333333329196D;
/*1864*/    p = p * x2 + -0.16666666666666666D;
/*1867*/    p = p * x2 * x;
/*1869*/    return p;
/*   0*/  }
/*   0*/  
/*   0*/  private static double polyCosine(double x) {
/*1879*/    double x2 = x * x;
/*1881*/    double p = 2.479773539153719E-5D;
/*1882*/    p = p * x2 + -0.0013888888689039883D;
/*1883*/    p = p * x2 + 0.041666666666621166D;
/*1884*/    p = p * x2 + -0.49999999999999994D;
/*1885*/    p *= x2;
/*1887*/    return p;
/*   0*/  }
/*   0*/  
/*   0*/  private static double sinQ(double xa, double xb) {
/*1898*/    int idx = (int)(xa * 8.0D + 0.5D);
/*1899*/    double epsilon = xa - EIGHTHES[idx];
/*1902*/    double sintA = SINE_TABLE_A[idx];
/*1903*/    double sintB = SINE_TABLE_B[idx];
/*1904*/    double costA = COSINE_TABLE_A[idx];
/*1905*/    double costB = COSINE_TABLE_B[idx];
/*1908*/    double sinEpsA = epsilon;
/*1909*/    double sinEpsB = polySine(epsilon);
/*1910*/    double cosEpsA = 1.0D;
/*1911*/    double cosEpsB = polyCosine(epsilon);
/*1914*/    double temp = sinEpsA * 1.073741824E9D;
/*1915*/    double temp2 = sinEpsA + temp - temp;
/*1916*/    sinEpsB += sinEpsA - temp2;
/*1917*/    sinEpsA = temp2;
/*1943*/    double a = 0.0D;
/*1944*/    double b = 0.0D;
/*1946*/    double t = sintA;
/*1947*/    double c = a + t;
/*1948*/    double d = -(c - a - t);
/*1949*/    a = c;
/*1950*/    b += d;
/*1952*/    t = costA * sinEpsA;
/*1953*/    c = a + t;
/*1954*/    d = -(c - a - t);
/*1955*/    a = c;
/*1956*/    b += d;
/*1958*/    b = b + sintA * cosEpsB + costA * sinEpsB;
/*1973*/    b = b + sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB;
/*2000*/    if (xb != 0.0D) {
/*2001*/      t = ((costA + costB) * (1.0D + cosEpsB) - (sintA + sintB) * (sinEpsA + sinEpsB)) * xb;
/*2003*/      c = a + t;
/*2004*/      d = -(c - a - t);
/*2005*/      a = c;
/*2006*/      b += d;
/*   0*/    } 
/*2009*/    double result = a + b;
/*2011*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private static double cosQ(double xa, double xb) {
/*2022*/    double pi2a = 1.5707963267948966D;
/*2023*/    double pi2b = 6.123233995736766E-17D;
/*2025*/    double a = 1.5707963267948966D - xa;
/*2026*/    double b = -(a - 1.5707963267948966D + xa);
/*2027*/    b += 6.123233995736766E-17D - xb;
/*2029*/    return sinQ(a, b);
/*   0*/  }
/*   0*/  
/*   0*/  private static double tanQ(double xa, double xb, boolean cotanFlag) {
/*2042*/    int idx = (int)(xa * 8.0D + 0.5D);
/*2043*/    double epsilon = xa - EIGHTHES[idx];
/*2046*/    double sintA = SINE_TABLE_A[idx];
/*2047*/    double sintB = SINE_TABLE_B[idx];
/*2048*/    double costA = COSINE_TABLE_A[idx];
/*2049*/    double costB = COSINE_TABLE_B[idx];
/*2052*/    double sinEpsA = epsilon;
/*2053*/    double sinEpsB = polySine(epsilon);
/*2054*/    double cosEpsA = 1.0D;
/*2055*/    double cosEpsB = polyCosine(epsilon);
/*2058*/    double temp = sinEpsA * 1.073741824E9D;
/*2059*/    double temp2 = sinEpsA + temp - temp;
/*2060*/    sinEpsB += sinEpsA - temp2;
/*2061*/    sinEpsA = temp2;
/*2086*/    double a = 0.0D;
/*2087*/    double b = 0.0D;
/*2090*/    double t = sintA;
/*2091*/    double c = a + t;
/*2092*/    double d = -(c - a - t);
/*2093*/    a = c;
/*2094*/    b += d;
/*2096*/    t = costA * sinEpsA;
/*2097*/    c = a + t;
/*2098*/    d = -(c - a - t);
/*2099*/    a = c;
/*2100*/    b += d;
/*2102*/    b = b + sintA * cosEpsB + costA * sinEpsB;
/*2103*/    b = b + sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB;
/*2105*/    double sina = a + b;
/*2106*/    double sinb = -(sina - a - b);
/*2110*/    a = b = c = d = 0.0D;
/*2112*/    t = costA * 1.0D;
/*2113*/    c = a + t;
/*2114*/    d = -(c - a - t);
/*2115*/    a = c;
/*2116*/    b += d;
/*2118*/    t = -sintA * sinEpsA;
/*2119*/    c = a + t;
/*2120*/    d = -(c - a - t);
/*2121*/    a = c;
/*2122*/    b += d;
/*2124*/    b = b + costB * 1.0D + costA * cosEpsB + costB * cosEpsB;
/*2125*/    b -= sintB * sinEpsA + sintA * sinEpsB + sintB * sinEpsB;
/*2127*/    double cosa = a + b;
/*2128*/    double cosb = -(cosa - a - b);
/*2130*/    if (cotanFlag) {
/*2132*/      double tmp = cosa;
/*2132*/      cosa = sina;
/*2132*/      sina = tmp;
/*2133*/      tmp = cosb;
/*2133*/      cosb = sinb;
/*2133*/      sinb = tmp;
/*   0*/    } 
/*2147*/    double est = sina / cosa;
/*2150*/    temp = est * 1.073741824E9D;
/*2151*/    double esta = est + temp - temp;
/*2152*/    double estb = est - esta;
/*2154*/    temp = cosa * 1.073741824E9D;
/*2155*/    double cosaa = cosa + temp - temp;
/*2156*/    double cosab = cosa - cosaa;
/*2159*/    double err = (sina - esta * cosaa - esta * cosab - estb * cosaa - estb * cosab) / cosa;
/*2160*/    err += sinb / cosa;
/*2161*/    err += -sina * cosb / cosa / cosa;
/*2163*/    if (xb != 0.0D) {
/*2166*/      double xbadj = xb + est * est * xb;
/*2167*/      if (cotanFlag)
/*2168*/        xbadj = -xbadj; 
/*2171*/      err += xbadj;
/*   0*/    } 
/*2174*/    return est + err;
/*   0*/  }
/*   0*/  
/*   0*/  private static void reducePayneHanek(double x, double[] result) {
/*   0*/    long shpi0, shpiA, shpiB;
/*2191*/    long inbits = Double.doubleToLongBits(x);
/*2192*/    int exponent = (int)(inbits >> 52L & 0x7FFL) - 1023;
/*2195*/    inbits &= 0xFFFFFFFFFFFFFL;
/*2196*/    inbits |= 0x10000000000000L;
/*2199*/    exponent++;
/*2200*/    inbits <<= 11L;
/*2206*/    int idx = exponent >> 6;
/*2207*/    int shift = exponent - (idx << 6);
/*2209*/    if (shift != 0) {
/*2210*/      shpi0 = (idx == 0) ? 0L : (RECIP_2PI[idx - 1] << shift);
/*2211*/      shpi0 |= RECIP_2PI[idx] >>> 64 - shift;
/*2212*/      shpiA = RECIP_2PI[idx] << shift | RECIP_2PI[idx + 1] >>> 64 - shift;
/*2213*/      shpiB = RECIP_2PI[idx + 1] << shift | RECIP_2PI[idx + 2] >>> 64 - shift;
/*   0*/    } else {
/*2215*/      shpi0 = (idx == 0) ? 0L : RECIP_2PI[idx - 1];
/*2216*/      shpiA = RECIP_2PI[idx];
/*2217*/      shpiB = RECIP_2PI[idx + 1];
/*   0*/    } 
/*2221*/    long a = inbits >>> 32L;
/*2222*/    long b = inbits & 0xFFFFFFFFL;
/*2224*/    long c = shpiA >>> 32L;
/*2225*/    long d = shpiA & 0xFFFFFFFFL;
/*2227*/    long ac = a * c;
/*2228*/    long bd = b * d;
/*2229*/    long bc = b * c;
/*2230*/    long ad = a * d;
/*2232*/    long prodB = bd + (ad << 32L);
/*2233*/    long prodA = ac + (ad >>> 32L);
/*2235*/    boolean bita = ((bd & Long.MIN_VALUE) != 0L);
/*2236*/    boolean bitb = ((ad & 0x80000000L) != 0L);
/*2237*/    boolean bitsum = ((prodB & Long.MIN_VALUE) != 0L);
/*2240*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2242*/      prodA++; 
/*2245*/    bita = ((prodB & Long.MIN_VALUE) != 0L);
/*2246*/    bitb = ((bc & 0x80000000L) != 0L);
/*2248*/    prodB += bc << 32L;
/*2249*/    prodA += bc >>> 32L;
/*2251*/    bitsum = ((prodB & Long.MIN_VALUE) != 0L);
/*2254*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2256*/      prodA++; 
/*2260*/    c = shpiB >>> 32L;
/*2261*/    d = shpiB & 0xFFFFFFFFL;
/*2262*/    ac = a * c;
/*2263*/    bc = b * c;
/*2264*/    ad = a * d;
/*2267*/    ac += bc + ad >>> 32L;
/*2269*/    bita = ((prodB & Long.MIN_VALUE) != 0L);
/*2270*/    bitb = ((ac & Long.MIN_VALUE) != 0L);
/*2271*/    prodB += ac;
/*2272*/    bitsum = ((prodB & Long.MIN_VALUE) != 0L);
/*2274*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2276*/      prodA++; 
/*2280*/    c = shpi0 >>> 32L;
/*2281*/    d = shpi0 & 0xFFFFFFFFL;
/*2283*/    bd = b * d;
/*2284*/    bc = b * c;
/*2285*/    ad = a * d;
/*2287*/    prodA += bd + (bc + ad << 32L);
/*2299*/    int intPart = (int)(prodA >>> 62L);
/*2302*/    prodA <<= 2L;
/*2303*/    prodA |= prodB >>> 62L;
/*2304*/    prodB <<= 2L;
/*2307*/    a = prodA >>> 32L;
/*2308*/    b = prodA & 0xFFFFFFFFL;
/*2310*/    c = PI_O_4_BITS[0] >>> 32L;
/*2311*/    d = PI_O_4_BITS[0] & 0xFFFFFFFFL;
/*2313*/    ac = a * c;
/*2314*/    bd = b * d;
/*2315*/    bc = b * c;
/*2316*/    ad = a * d;
/*2318*/    long prod2B = bd + (ad << 32L);
/*2319*/    long prod2A = ac + (ad >>> 32L);
/*2321*/    bita = ((bd & Long.MIN_VALUE) != 0L);
/*2322*/    bitb = ((ad & 0x80000000L) != 0L);
/*2323*/    bitsum = ((prod2B & Long.MIN_VALUE) != 0L);
/*2326*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2328*/      prod2A++; 
/*2331*/    bita = ((prod2B & Long.MIN_VALUE) != 0L);
/*2332*/    bitb = ((bc & 0x80000000L) != 0L);
/*2334*/    prod2B += bc << 32L;
/*2335*/    prod2A += bc >>> 32L;
/*2337*/    bitsum = ((prod2B & Long.MIN_VALUE) != 0L);
/*2340*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2342*/      prod2A++; 
/*2346*/    c = PI_O_4_BITS[1] >>> 32L;
/*2347*/    d = PI_O_4_BITS[1] & 0xFFFFFFFFL;
/*2348*/    ac = a * c;
/*2349*/    bc = b * c;
/*2350*/    ad = a * d;
/*2353*/    ac += bc + ad >>> 32L;
/*2355*/    bita = ((prod2B & Long.MIN_VALUE) != 0L);
/*2356*/    bitb = ((ac & Long.MIN_VALUE) != 0L);
/*2357*/    prod2B += ac;
/*2358*/    bitsum = ((prod2B & Long.MIN_VALUE) != 0L);
/*2360*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2362*/      prod2A++; 
/*2366*/    a = prodB >>> 32L;
/*2367*/    b = prodB & 0xFFFFFFFFL;
/*2368*/    c = PI_O_4_BITS[0] >>> 32L;
/*2369*/    d = PI_O_4_BITS[0] & 0xFFFFFFFFL;
/*2370*/    ac = a * c;
/*2371*/    bc = b * c;
/*2372*/    ad = a * d;
/*2375*/    ac += bc + ad >>> 32L;
/*2377*/    bita = ((prod2B & Long.MIN_VALUE) != 0L);
/*2378*/    bitb = ((ac & Long.MIN_VALUE) != 0L);
/*2379*/    prod2B += ac;
/*2380*/    bitsum = ((prod2B & Long.MIN_VALUE) != 0L);
/*2382*/    if ((bita && bitb) || ((bita || bitb) && !bitsum))
/*2384*/      prod2A++; 
/*2388*/    double tmpA = (prod2A >>> 12L) / 4.503599627370496E15D;
/*2389*/    double tmpB = (((prod2A & 0xFFFL) << 40L) + (prod2B >>> 24L)) / 4.503599627370496E15D / 4.503599627370496E15D;
/*2391*/    double sumA = tmpA + tmpB;
/*2392*/    double sumB = -(sumA - tmpA - tmpB);
/*2395*/    result[0] = intPart;
/*2396*/    result[1] = sumA * 2.0D;
/*2397*/    result[2] = sumB * 2.0D;
/*   0*/  }
/*   0*/  
/*   0*/  public static double sin(double x) {
/*   0*/    boolean negative = false;
/*2407*/    int quadrant = 0;
/*2409*/    double xb = 0.0D;
/*2412*/    double xa = x;
/*2413*/    if (x < 0.0D) {
/*2414*/      negative = true;
/*2415*/      xa = -xa;
/*   0*/    } 
/*2419*/    if (xa == 0.0D) {
/*2420*/      long bits = Double.doubleToLongBits(x);
/*2421*/      if (bits < 0L)
/*2422*/        return -0.0D; 
/*2424*/      return 0.0D;
/*   0*/    } 
/*2427*/    if (xa != xa || xa == Double.POSITIVE_INFINITY)
/*2428*/      return Double.NaN; 
/*2432*/    if (xa > 3294198.0D) {
/*2436*/      double[] reduceResults = new double[3];
/*2437*/      reducePayneHanek(xa, reduceResults);
/*2438*/      quadrant = (int)reduceResults[0] & 0x3;
/*2439*/      xa = reduceResults[1];
/*2440*/      xb = reduceResults[2];
/*2441*/    } else if (xa > 1.5707963267948966D) {
/*   0*/      double remA, remB;
/*2446*/      int k = (int)(xa * 0.6366197723675814D);
/*   0*/      while (true) {
/*2452*/        double a = -k * 1.570796251296997D;
/*2453*/        remA = xa + a;
/*2454*/        remB = -(remA - xa - a);
/*2456*/        a = -k * 7.549789948768648E-8D;
/*2457*/        double b = remA;
/*2458*/        remA = a + b;
/*2459*/        remB += -(remA - b - a);
/*2461*/        a = -k * 6.123233995736766E-17D;
/*2462*/        b = remA;
/*2463*/        remA = a + b;
/*2464*/        remB += -(remA - b - a);
/*2466*/        if (remA > 0.0D)
/*   0*/          break; 
/*2472*/        k--;
/*   0*/      } 
/*2474*/      quadrant = k & 0x3;
/*2475*/      xa = remA;
/*2476*/      xb = remB;
/*   0*/    } 
/*2479*/    if (negative)
/*2480*/      quadrant ^= 0x2; 
/*2483*/    switch (quadrant) {
/*   0*/      case 0:
/*2485*/        return sinQ(xa, xb);
/*   0*/      case 1:
/*2487*/        return cosQ(xa, xb);
/*   0*/      case 2:
/*2489*/        return -sinQ(xa, xb);
/*   0*/      case 3:
/*2491*/        return -cosQ(xa, xb);
/*   0*/    } 
/*2493*/    return Double.NaN;
/*   0*/  }
/*   0*/  
/*   0*/  public static double cos(double x) {
/*2503*/    int quadrant = 0;
/*2506*/    double xa = x;
/*2507*/    if (x < 0.0D)
/*2508*/      xa = -xa; 
/*2511*/    if (xa != xa || xa == Double.POSITIVE_INFINITY)
/*2512*/      return Double.NaN; 
/*2516*/    double xb = 0.0D;
/*2517*/    if (xa > 3294198.0D) {
/*2521*/      double[] reduceResults = new double[3];
/*2522*/      reducePayneHanek(xa, reduceResults);
/*2523*/      quadrant = (int)reduceResults[0] & 0x3;
/*2524*/      xa = reduceResults[1];
/*2525*/      xb = reduceResults[2];
/*2526*/    } else if (xa > 1.5707963267948966D) {
/*   0*/      double remA, remB;
/*2531*/      int k = (int)(xa * 0.6366197723675814D);
/*   0*/      while (true) {
/*2537*/        double a = -k * 1.570796251296997D;
/*2538*/        remA = xa + a;
/*2539*/        remB = -(remA - xa - a);
/*2541*/        a = -k * 7.549789948768648E-8D;
/*2542*/        double b = remA;
/*2543*/        remA = a + b;
/*2544*/        remB += -(remA - b - a);
/*2546*/        a = -k * 6.123233995736766E-17D;
/*2547*/        b = remA;
/*2548*/        remA = a + b;
/*2549*/        remB += -(remA - b - a);
/*2551*/        if (remA > 0.0D)
/*   0*/          break; 
/*2557*/        k--;
/*   0*/      } 
/*2559*/      quadrant = k & 0x3;
/*2560*/      xa = remA;
/*2561*/      xb = remB;
/*   0*/    } 
/*2567*/    switch (quadrant) {
/*   0*/      case 0:
/*2569*/        return cosQ(xa, xb);
/*   0*/      case 1:
/*2571*/        return -sinQ(xa, xb);
/*   0*/      case 2:
/*2573*/        return -cosQ(xa, xb);
/*   0*/      case 3:
/*2575*/        return sinQ(xa, xb);
/*   0*/    } 
/*2577*/    return Double.NaN;
/*   0*/  }
/*   0*/  
/*   0*/  public static double tan(double x) {
/*   0*/    double result;
/*   0*/    boolean negative = false;
/*2588*/    int quadrant = 0;
/*2591*/    double xa = x;
/*2592*/    if (x < 0.0D) {
/*2593*/      negative = true;
/*2594*/      xa = -xa;
/*   0*/    } 
/*2598*/    if (xa == 0.0D) {
/*2599*/      long bits = Double.doubleToLongBits(x);
/*2600*/      if (bits < 0L)
/*2601*/        return -0.0D; 
/*2603*/      return 0.0D;
/*   0*/    } 
/*2606*/    if (xa != xa || xa == Double.POSITIVE_INFINITY)
/*2607*/      return Double.NaN; 
/*2611*/    double xb = 0.0D;
/*2612*/    if (xa > 3294198.0D) {
/*2616*/      double[] reduceResults = new double[3];
/*2617*/      reducePayneHanek(xa, reduceResults);
/*2618*/      quadrant = (int)reduceResults[0] & 0x3;
/*2619*/      xa = reduceResults[1];
/*2620*/      xb = reduceResults[2];
/*2621*/    } else if (xa > 1.5707963267948966D) {
/*   0*/      double remA, remB;
/*2626*/      int k = (int)(xa * 0.6366197723675814D);
/*   0*/      while (true) {
/*2632*/        double a = -k * 1.570796251296997D;
/*2633*/        remA = xa + a;
/*2634*/        remB = -(remA - xa - a);
/*2636*/        a = -k * 7.549789948768648E-8D;
/*2637*/        double b = remA;
/*2638*/        remA = a + b;
/*2639*/        remB += -(remA - b - a);
/*2641*/        a = -k * 6.123233995736766E-17D;
/*2642*/        b = remA;
/*2643*/        remA = a + b;
/*2644*/        remB += -(remA - b - a);
/*2646*/        if (remA > 0.0D)
/*   0*/          break; 
/*2652*/        k--;
/*   0*/      } 
/*2654*/      quadrant = k & 0x3;
/*2655*/      xa = remA;
/*2656*/      xb = remB;
/*   0*/    } 
/*2659*/    if (xa > 1.5D) {
/*2661*/      double pi2a = 1.5707963267948966D;
/*2662*/      double pi2b = 6.123233995736766E-17D;
/*2664*/      double a = 1.5707963267948966D - xa;
/*2665*/      double b = -(a - 1.5707963267948966D + xa);
/*2666*/      b += 6.123233995736766E-17D - xb;
/*2668*/      xa = a + b;
/*2669*/      xb = -(xa - a - b);
/*2670*/      quadrant ^= 0x1;
/*2671*/      negative ^= true;
/*   0*/    } 
/*2675*/    if ((quadrant & 0x1) == 0) {
/*2676*/      result = tanQ(xa, xb, false);
/*   0*/    } else {
/*2678*/      result = -tanQ(xa, xb, true);
/*   0*/    } 
/*2681*/    if (negative)
/*2682*/      result = -result; 
/*2685*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double atan(double x) {
/*2694*/    return atan(x, 0.0D, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static double atan(double xa, double xb, boolean leftPlane) {
/*   0*/    int idx;
/*   0*/    boolean negate = false;
/*2707*/    if (xa < 0.0D) {
/*2709*/      xa = -xa;
/*2710*/      xb = -xb;
/*2711*/      negate = true;
/*   0*/    } 
/*2714*/    if (xa > 1.633123935319537E16D)
/*2715*/      return (negate ^ leftPlane) ? -1.5707963267948966D : 1.5707963267948966D; 
/*2719*/    if (xa < 1.0D) {
/*2720*/      idx = (int)((-1.7168146928204135D * xa * xa + 8.0D) * xa + 0.5D);
/*   0*/    } else {
/*2722*/      double d = 1.0D / xa;
/*2723*/      idx = (int)(-((-1.7168146928204135D * d * d + 8.0D) * d) + 13.07D);
/*   0*/    } 
/*2725*/    double epsA = xa - TANGENT_TABLE_A[idx];
/*2726*/    double epsB = -(epsA - xa + TANGENT_TABLE_A[idx]);
/*2727*/    epsB += xb - TANGENT_TABLE_B[idx];
/*2729*/    double temp = epsA + epsB;
/*2730*/    epsB = -(temp - epsA - epsB);
/*2731*/    epsA = temp;
/*2734*/    temp = xa * 1.073741824E9D;
/*2735*/    double ya = xa + temp - temp;
/*2736*/    double yb = xb + xa - ya;
/*2737*/    xa = ya;
/*2738*/    xb += yb;
/*2741*/    if (idx == 0) {
/*2744*/      double denom = 1.0D / (1.0D + (xa + xb) * (TANGENT_TABLE_A[idx] + TANGENT_TABLE_B[idx]));
/*2746*/      ya = epsA * denom;
/*2747*/      yb = epsB * denom;
/*   0*/    } else {
/*2749*/      double temp2 = xa * TANGENT_TABLE_A[idx];
/*2750*/      double d1 = 1.0D + temp2;
/*2751*/      double d2 = -(d1 - 1.0D - temp2);
/*2752*/      temp2 = xb * TANGENT_TABLE_A[idx] + xa * TANGENT_TABLE_B[idx];
/*2753*/      temp = d1 + temp2;
/*2754*/      d2 += -(temp - d1 - temp2);
/*2755*/      d1 = temp;
/*2757*/      d2 += xb * TANGENT_TABLE_B[idx];
/*2758*/      ya = epsA / d1;
/*2760*/      temp = ya * 1.073741824E9D;
/*2761*/      double yaa = ya + temp - temp;
/*2762*/      double yab = ya - yaa;
/*2764*/      temp = d1 * 1.073741824E9D;
/*2765*/      double zaa = d1 + temp - temp;
/*2766*/      double zab = d1 - zaa;
/*2769*/      yb = (epsA - yaa * zaa - yaa * zab - yab * zaa - yab * zab) / d1;
/*2771*/      yb += -epsA * d2 / d1 / d1;
/*2772*/      yb += epsB / d1;
/*   0*/    } 
/*2776*/    epsA = ya;
/*2777*/    epsB = yb;
/*2780*/    double epsA2 = epsA * epsA;
/*2791*/    yb = 0.07490822288864472D;
/*2792*/    yb = yb * epsA2 + -0.09088450866185192D;
/*2793*/    yb = yb * epsA2 + 0.11111095942313305D;
/*2794*/    yb = yb * epsA2 + -0.1428571423679182D;
/*2795*/    yb = yb * epsA2 + 0.19999999999923582D;
/*2796*/    yb = yb * epsA2 + -0.33333333333333287D;
/*2797*/    yb = yb * epsA2 * epsA;
/*2800*/    ya = epsA;
/*2802*/    temp = ya + yb;
/*2803*/    yb = -(temp - ya - yb);
/*2804*/    ya = temp;
/*2807*/    yb += epsB / (1.0D + epsA * epsA);
/*2813*/    double za = EIGHTHES[idx] + ya;
/*2814*/    double zb = -(za - EIGHTHES[idx] - ya);
/*2815*/    temp = za + yb;
/*2816*/    zb += -(temp - za - yb);
/*2817*/    za = temp;
/*2819*/    double result = za + zb;
/*2820*/    double resultb = -(result - za - zb);
/*2822*/    if (leftPlane) {
/*2824*/      double pia = Math.PI;
/*2825*/      double pib = 1.2246467991473532E-16D;
/*2827*/      za = Math.PI - result;
/*2828*/      zb = -(za - Math.PI + result);
/*2829*/      zb += 1.2246467991473532E-16D - resultb;
/*2831*/      result = za + zb;
/*2832*/      resultb = -(result - za - zb);
/*   0*/    } 
/*2836*/    if (negate ^ leftPlane)
/*2837*/      result = -result; 
/*2840*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double atan2(double y, double x) {
/*2850*/    if (x != x || y != y)
/*2851*/      return Double.NaN; 
/*2854*/    if (y == 0.0D) {
/*2855*/      double d1 = x * y;
/*2856*/      double invx = 1.0D / x;
/*2857*/      double invy = 1.0D / y;
/*2859*/      if (invx == 0.0D) {
/*2860*/        if (x > 0.0D)
/*2861*/          return 0.0D; 
/*2863*/        return Math.PI;
/*   0*/      } 
/*2867*/      if (d1 != d1)
/*2868*/        return x / y; 
/*2871*/      if (x < 0.0D || invx < 0.0D) {
/*2872*/        if (y < 0.0D || invy < 0.0D)
/*2873*/          return -3.141592653589793D; 
/*2875*/        return Math.PI;
/*   0*/      } 
/*2878*/      return d1;
/*   0*/    } 
/*2882*/    if (y == Double.POSITIVE_INFINITY) {
/*2883*/      if (x == Double.POSITIVE_INFINITY)
/*2884*/        return 0.7853981633974483D; 
/*2887*/      if (x == Double.NEGATIVE_INFINITY)
/*2888*/        return 2.356194490192345D; 
/*2891*/      return 1.5707963267948966D;
/*   0*/    } 
/*2894*/    if (y == Double.NEGATIVE_INFINITY) {
/*2895*/      if (x == Double.POSITIVE_INFINITY)
/*2896*/        return -0.7853981633974483D; 
/*2899*/      if (x == Double.NEGATIVE_INFINITY)
/*2900*/        return -2.356194490192345D; 
/*2903*/      return -1.5707963267948966D;
/*   0*/    } 
/*2906*/    if (x == Double.POSITIVE_INFINITY) {
/*2907*/      if (y > 0.0D || 1.0D / y > 0.0D)
/*2908*/        return 0.0D; 
/*2911*/      if (y < 0.0D || 1.0D / y < 0.0D)
/*2912*/        return -0.0D; 
/*   0*/    } 
/*2916*/    if (x == Double.NEGATIVE_INFINITY) {
/*2918*/      if (y > 0.0D || 1.0D / y > 0.0D)
/*2919*/        return Math.PI; 
/*2922*/      if (y < 0.0D || 1.0D / y < 0.0D)
/*2923*/        return -3.141592653589793D; 
/*   0*/    } 
/*2927*/    if (x == 0.0D) {
/*2928*/      if (y > 0.0D || 1.0D / y > 0.0D)
/*2929*/        return 1.5707963267948966D; 
/*2932*/      if (y < 0.0D || 1.0D / y < 0.0D)
/*2933*/        return -1.5707963267948966D; 
/*   0*/    } 
/*2937*/    if (x > 8.0E298D || x < -8.0E298D) {
/*2938*/      x *= 9.313225746154785E-10D;
/*2939*/      y *= 9.313225746154785E-10D;
/*   0*/    } 
/*2943*/    double temp = x * 1.073741824E9D;
/*2944*/    double xa = x + temp - temp;
/*2945*/    double xb = x - xa;
/*2948*/    double r = y / x;
/*2949*/    temp = r * 1.073741824E9D;
/*2950*/    double ra = r + temp - temp;
/*2951*/    double rb = r - ra;
/*2953*/    rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x;
/*2955*/    temp = ra + rb;
/*2956*/    rb = -(temp - ra - rb);
/*2957*/    ra = temp;
/*2960*/    double result = atan(ra, rb, (x < 0.0D));
/*2962*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static double asin(double x) {
/*2970*/    if (x != x)
/*2971*/      return Double.NaN; 
/*2974*/    if (x > 1.0D || x < -1.0D)
/*2975*/      return Double.NaN; 
/*2978*/    if (x == 1.0D)
/*2979*/      return 1.5707963267948966D; 
/*2982*/    if (x == -1.0D)
/*2983*/      return -1.5707963267948966D; 
/*2989*/    double temp = x * 1.073741824E9D;
/*2990*/    double xa = x + temp - temp;
/*2991*/    double xb = x - xa;
/*2994*/    double ya = xa * xa;
/*2995*/    double yb = xa * xb * 2.0D + xb * xb;
/*2998*/    ya = -ya;
/*2999*/    yb = -yb;
/*3001*/    double za = 1.0D + ya;
/*3002*/    double zb = -(za - 1.0D - ya);
/*3004*/    temp = za + yb;
/*3005*/    zb += -(temp - za - yb);
/*3006*/    za = temp;
/*3010*/    double y = sqrt(za);
/*3011*/    temp = y * 1.073741824E9D;
/*3012*/    ya = y + temp - temp;
/*3013*/    yb = y - ya;
/*3016*/    yb += (za - ya * ya - 2.0D * ya * yb - yb * yb) / 2.0D * y;
/*3019*/    double dx = zb / 2.0D * y;
/*3022*/    double r = x / y;
/*3023*/    temp = r * 1.073741824E9D;
/*3024*/    double ra = r + temp - temp;
/*3025*/    double rb = r - ra;
/*3027*/    rb += (x - ra * ya - ra * yb - rb * ya - rb * yb) / y;
/*3028*/    rb += -x * dx / y / y;
/*3030*/    temp = ra + rb;
/*3031*/    rb = -(temp - ra - rb);
/*3032*/    ra = temp;
/*3034*/    return atan(ra, rb, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static double acos(double x) {
/*3042*/    if (x != x)
/*3043*/      return Double.NaN; 
/*3046*/    if (x > 1.0D || x < -1.0D)
/*3047*/      return Double.NaN; 
/*3050*/    if (x == -1.0D)
/*3051*/      return Math.PI; 
/*3054*/    if (x == 1.0D)
/*3055*/      return 0.0D; 
/*3058*/    if (x == 0.0D)
/*3059*/      return 1.5707963267948966D; 
/*3065*/    double temp = x * 1.073741824E9D;
/*3066*/    double xa = x + temp - temp;
/*3067*/    double xb = x - xa;
/*3070*/    double ya = xa * xa;
/*3071*/    double yb = xa * xb * 2.0D + xb * xb;
/*3074*/    ya = -ya;
/*3075*/    yb = -yb;
/*3077*/    double za = 1.0D + ya;
/*3078*/    double zb = -(za - 1.0D - ya);
/*3080*/    temp = za + yb;
/*3081*/    zb += -(temp - za - yb);
/*3082*/    za = temp;
/*3085*/    double y = sqrt(za);
/*3086*/    temp = y * 1.073741824E9D;
/*3087*/    ya = y + temp - temp;
/*3088*/    yb = y - ya;
/*3091*/    yb += (za - ya * ya - 2.0D * ya * yb - yb * yb) / 2.0D * y;
/*3094*/    yb += zb / 2.0D * y;
/*3095*/    y = ya + yb;
/*3096*/    yb = -(y - ya - yb);
/*3099*/    double r = y / x;
/*3100*/    temp = r * 1.073741824E9D;
/*3101*/    double ra = r + temp - temp;
/*3102*/    double rb = r - ra;
/*3104*/    rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x;
/*3105*/    rb += yb / x;
/*3107*/    temp = ra + rb;
/*3108*/    rb = -(temp - ra - rb);
/*3109*/    ra = temp;
/*3111*/    return atan(ra, rb, (x < 0.0D));
/*   0*/  }
/*   0*/  
/*   0*/  public static double cbrt(double x) {
/*3120*/    long inbits = Double.doubleToLongBits(x);
/*3121*/    int exponent = (int)(inbits >> 52L & 0x7FFL) - 1023;
/*   0*/    boolean subnormal = false;
/*3124*/    if (exponent == -1023) {
/*3125*/      if (x == 0.0D)
/*3126*/        return x; 
/*3130*/      subnormal = true;
/*3131*/      x *= 1.8014398509481984E16D;
/*3132*/      inbits = Double.doubleToLongBits(x);
/*3133*/      exponent = (int)(inbits >> 52L & 0x7FFL) - 1023;
/*   0*/    } 
/*3136*/    if (exponent == 1024)
/*3138*/      return x; 
/*3142*/    int exp3 = exponent / 3;
/*3145*/    double p2 = Double.longBitsToDouble(inbits & Long.MIN_VALUE | (exp3 + 1023 & 0x7FF) << 52L);
/*3149*/    double mant = Double.longBitsToDouble(inbits & 0xFFFFFFFFFFFFFL | 0x3FF0000000000000L);
/*3152*/    double est = -0.010714690733195933D;
/*3153*/    est = est * mant + 0.0875862700108075D;
/*3154*/    est = est * mant + -0.3058015757857271D;
/*3155*/    est = est * mant + 0.7249995199969751D;
/*3156*/    est = est * mant + 0.5039018405998233D;
/*3158*/    est *= CBRTTWO[exponent % 3 + 2];
/*3163*/    double xs = x / p2 * p2 * p2;
/*3164*/    est += (xs - est * est * est) / 3.0D * est * est;
/*3165*/    est += (xs - est * est * est) / 3.0D * est * est;
/*3168*/    double temp = est * 1.073741824E9D;
/*3169*/    double ya = est + temp - temp;
/*3170*/    double yb = est - ya;
/*3172*/    double za = ya * ya;
/*3173*/    double zb = ya * yb * 2.0D + yb * yb;
/*3174*/    temp = za * 1.073741824E9D;
/*3175*/    double temp2 = za + temp - temp;
/*3176*/    zb += za - temp2;
/*3177*/    za = temp2;
/*3179*/    zb = za * yb + ya * zb + zb * yb;
/*3180*/    za *= ya;
/*3182*/    double na = xs - za;
/*3183*/    double nb = -(na - xs + za);
/*3184*/    nb -= zb;
/*3186*/    est += (na + nb) / 3.0D * est * est;
/*3189*/    est *= p2;
/*3191*/    if (subnormal)
/*3192*/      est *= 3.814697265625E-6D; 
/*3195*/    return est;
/*   0*/  }
/*   0*/  
/*   0*/  public static double toRadians(double x) {
/*3205*/    double facta = 0.01745329052209854D;
/*3206*/    double factb = 1.997844754509471E-9D;
/*3208*/    double temp = x * 1.073741824E9D;
/*3209*/    double xa = x + temp - temp;
/*3210*/    double xb = x - xa;
/*3212*/    return xb * 1.997844754509471E-9D + xb * 0.01745329052209854D + xa * 1.997844754509471E-9D + xa * 0.01745329052209854D;
/*   0*/  }
/*   0*/  
/*   0*/  public static double toDegrees(double x) {
/*3222*/    double facta = 57.2957763671875D;
/*3223*/    double factb = 3.145894820876798E-6D;
/*3225*/    double temp = x * 1.073741824E9D;
/*3226*/    double xa = x + temp - temp;
/*3227*/    double xb = x - xa;
/*3229*/    return xb * 3.145894820876798E-6D + xb * 57.2957763671875D + xa * 3.145894820876798E-6D + xa * 57.2957763671875D;
/*   0*/  }
/*   0*/  
/*   0*/  public static int abs(int x) {
/*3238*/    return (x < 0) ? -x : x;
/*   0*/  }
/*   0*/  
/*   0*/  public static long abs(long x) {
/*3247*/    return (x < 0L) ? -x : x;
/*   0*/  }
/*   0*/  
/*   0*/  public static float abs(float x) {
/*3256*/    return (x < 0.0F) ? -x : x;
/*   0*/  }
/*   0*/  
/*   0*/  public static double abs(double x) {
/*3265*/    return (x < 0.0D) ? -x : x;
/*   0*/  }
/*   0*/  
/*   0*/  public static double ulp(double x) {
/*3275*/    return abs(x - Double.longBitsToDouble(Double.doubleToLongBits(x) ^ 0x1L));
/*   0*/  }
/*   0*/  
/*   0*/  public static double nextAfter(double d, double direction) {
/*3297*/    if (Double.isNaN(d) || Double.isInfinite(d))
/*3298*/      return d; 
/*3299*/    if (d == 0.0D)
/*3300*/      return (direction < 0.0D) ? -4.9E-324D : Double.MIN_VALUE; 
/*3306*/    long bits = Double.doubleToLongBits(d);
/*3307*/    long sign = bits & Long.MIN_VALUE;
/*3308*/    long exponent = bits & 0x7FF0000000000000L;
/*3309*/    long mantissa = bits & 0xFFFFFFFFFFFFFL;
/*3311*/    if (d * (direction - d) >= 0.0D) {
/*3313*/      if (mantissa == 4503599627370495L)
/*3314*/        return Double.longBitsToDouble(sign | exponent + 4503599627370496L); 
/*3317*/      return Double.longBitsToDouble(sign | exponent | mantissa + 1L);
/*   0*/    } 
/*3322*/    if (mantissa == 0L)
/*3323*/      return Double.longBitsToDouble(sign | exponent - 4503599627370496L | 0xFFFFFFFFFFFFFL); 
/*3327*/    return Double.longBitsToDouble(sign | exponent | mantissa - 1L);
/*   0*/  }
/*   0*/  
/*   0*/  public static double floor(double x) {
/*3341*/    if (x != x)
/*3342*/      return x; 
/*3345*/    if (x >= 4.503599627370496E15D || x <= -4.503599627370496E15D)
/*3346*/      return x; 
/*3349*/    long y = (long)x;
/*3350*/    if (x < 0.0D && y != x)
/*3351*/      y--; 
/*3354*/    if (y == 0L)
/*3355*/      return x * y; 
/*3358*/    return y;
/*   0*/  }
/*   0*/  
/*   0*/  public static double ceil(double x) {
/*3368*/    if (x != x)
/*3369*/      return x; 
/*3372*/    double y = floor(x);
/*3373*/    if (y == x)
/*3374*/      return y; 
/*3377*/    y++;
/*3379*/    if (y == 0.0D)
/*3380*/      return x * y; 
/*3383*/    return y;
/*   0*/  }
/*   0*/  
/*   0*/  public static double rint(double x) {
/*3391*/    double y = floor(x);
/*3392*/    double d = x - y;
/*3394*/    if (d > 0.5D)
/*3395*/      return y + 1.0D; 
/*3397*/    if (d < 0.5D)
/*3398*/      return y; 
/*3402*/    long z = (long)y;
/*3403*/    return ((z & 0x1L) == 0L) ? y : (y + 1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static long round(double x) {
/*3411*/    return (long)floor(x + 0.5D);
/*   0*/  }
/*   0*/  
/*   0*/  public static int round(float x) {
/*3419*/    return Math.round(x);
/*   0*/  }
/*   0*/  
/*   0*/  public static int min(int a, int b) {
/*3428*/    return (a <= b) ? a : b;
/*   0*/  }
/*   0*/  
/*   0*/  public static long min(long a, long b) {
/*3437*/    return (a <= b) ? a : b;
/*   0*/  }
/*   0*/  
/*   0*/  public static float min(float a, float b) {
/*3446*/    return (a <= b) ? a : (Float.isNaN(a + b) ? Float.NaN : b);
/*   0*/  }
/*   0*/  
/*   0*/  public static double min(double a, double b) {
/*3455*/    return (a <= b) ? a : (Double.isNaN(a + b) ? Double.NaN : b);
/*   0*/  }
/*   0*/  
/*   0*/  public static int max(int a, int b) {
/*3464*/    return (a <= b) ? b : a;
/*   0*/  }
/*   0*/  
/*   0*/  public static long max(long a, long b) {
/*3473*/    return (a <= b) ? b : a;
/*   0*/  }
/*   0*/  
/*   0*/  public static float max(float a, float b) {
/*3482*/    return (a <= b) ? b : (Float.isNaN(a + b) ? Float.NaN : a);
/*   0*/  }
/*   0*/  
/*   0*/  public static double max(double a, double b) {
/*3491*/    return (a <= b) ? b : (Double.isNaN(a + b) ? Double.NaN : a);
/*   0*/  }
/*   0*/}
