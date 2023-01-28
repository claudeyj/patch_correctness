/*   0*/package com.google.gson.internal.bind.util;
/*   0*/
/*   0*/import java.text.ParseException;
/*   0*/import java.text.ParsePosition;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Date;
/*   0*/import java.util.GregorianCalendar;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/
/*   0*/public class ISO8601Utils {
/*   0*/  private static final String UTC_ID = "UTC";
/*   0*/  
/*  30*/  private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
/*   0*/  
/*   0*/  public static String format(Date date) {
/*  45*/    return format(date, false, TIMEZONE_UTC);
/*   0*/  }
/*   0*/  
/*   0*/  public static String format(Date date, boolean millis) {
/*  56*/    return format(date, millis, TIMEZONE_UTC);
/*   0*/  }
/*   0*/  
/*   0*/  public static String format(Date date, boolean millis, TimeZone tz) {
/*  68*/    Calendar calendar = new GregorianCalendar(tz, Locale.US);
/*  69*/    calendar.setTime(date);
/*  72*/    int capacity = "yyyy-MM-ddThh:mm:ss".length();
/*  73*/    capacity += millis ? ".sss".length() : 0;
/*  74*/    capacity += (tz.getRawOffset() == 0) ? "Z".length() : "+hh:mm".length();
/*  75*/    StringBuilder formatted = new StringBuilder(capacity);
/*  77*/    padInt(formatted, calendar.get(1), "yyyy".length());
/*  78*/    formatted.append('-');
/*  79*/    padInt(formatted, calendar.get(2) + 1, "MM".length());
/*  80*/    formatted.append('-');
/*  81*/    padInt(formatted, calendar.get(5), "dd".length());
/*  82*/    formatted.append('T');
/*  83*/    padInt(formatted, calendar.get(11), "hh".length());
/*  84*/    formatted.append(':');
/*  85*/    padInt(formatted, calendar.get(12), "mm".length());
/*  86*/    formatted.append(':');
/*  87*/    padInt(formatted, calendar.get(13), "ss".length());
/*  88*/    if (millis) {
/*  89*/      formatted.append('.');
/*  90*/      padInt(formatted, calendar.get(14), "sss".length());
/*   0*/    } 
/*  93*/    int offset = tz.getOffset(calendar.getTimeInMillis());
/*  94*/    if (offset != 0) {
/*  95*/      int hours = Math.abs(offset / 60000 / 60);
/*  96*/      int minutes = Math.abs(offset / 60000 % 60);
/*  97*/      formatted.append((offset < 0) ? 45 : 43);
/*  98*/      padInt(formatted, hours, "hh".length());
/*  99*/      formatted.append(':');
/* 100*/      padInt(formatted, minutes, "mm".length());
/*   0*/    } else {
/* 102*/      formatted.append('Z');
/*   0*/    } 
/* 105*/    return formatted.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static Date parse(String date, ParsePosition pos) throws ParseException {
/*   0*/    // Byte code:
/*   0*/    //   0: aconst_null
/*   0*/    //   1: astore_2
/*   0*/    //   2: aload_1
/*   0*/    //   3: invokevirtual getIndex : ()I
/*   0*/    //   6: istore_3
/*   0*/    //   7: aload_0
/*   0*/    //   8: iload_3
/*   0*/    //   9: iinc #3, 4
/*   0*/    //   12: iload_3
/*   0*/    //   13: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   16: istore #4
/*   0*/    //   18: aload_0
/*   0*/    //   19: iload_3
/*   0*/    //   20: bipush #45
/*   0*/    //   22: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*   0*/    //   25: ifeq -> 31
/*   0*/    //   28: iinc #3, 1
/*   0*/    //   31: aload_0
/*   0*/    //   32: iload_3
/*   0*/    //   33: iinc #3, 2
/*   0*/    //   36: iload_3
/*   0*/    //   37: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   40: istore #5
/*   0*/    //   42: aload_0
/*   0*/    //   43: iload_3
/*   0*/    //   44: bipush #45
/*   0*/    //   46: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*   0*/    //   49: ifeq -> 55
/*   0*/    //   52: iinc #3, 1
/*   0*/    //   55: aload_0
/*   0*/    //   56: iload_3
/*   0*/    //   57: iinc #3, 2
/*   0*/    //   60: iload_3
/*   0*/    //   61: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   64: istore #6
/*   0*/    //   66: iconst_0
/*   0*/    //   67: istore #7
/*   0*/    //   69: iconst_0
/*   0*/    //   70: istore #8
/*   0*/    //   72: iconst_0
/*   0*/    //   73: istore #9
/*   0*/    //   75: iconst_0
/*   0*/    //   76: istore #10
/*   0*/    //   78: aload_0
/*   0*/    //   79: iload_3
/*   0*/    //   80: bipush #84
/*   0*/    //   82: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*   0*/    //   85: istore #11
/*   0*/    //   87: iload #11
/*   0*/    //   89: ifne -> 128
/*   0*/    //   92: aload_0
/*   0*/    //   93: invokevirtual length : ()I
/*   0*/    //   96: iload_3
/*   0*/    //   97: if_icmpgt -> 128
/*   0*/    //   100: new java/util/GregorianCalendar
/*   0*/    //   103: dup
/*   0*/    //   104: iload #4
/*   0*/    //   106: iload #5
/*   0*/    //   108: iconst_1
/*   0*/    //   109: isub
/*   0*/    //   110: iload #6
/*   0*/    //   112: invokespecial <init> : (III)V
/*   0*/    //   115: astore #12
/*   0*/    //   117: aload_1
/*   0*/    //   118: iload_3
/*   0*/    //   119: invokevirtual setIndex : (I)V
/*   0*/    //   122: aload #12
/*   0*/    //   124: invokevirtual getTime : ()Ljava/util/Date;
/*   0*/    //   127: areturn
/*   0*/    //   128: iload #11
/*   0*/    //   130: ifeq -> 347
/*   0*/    //   133: aload_0
/*   0*/    //   134: iinc #3, 1
/*   0*/    //   137: iload_3
/*   0*/    //   138: iinc #3, 2
/*   0*/    //   141: iload_3
/*   0*/    //   142: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   145: istore #7
/*   0*/    //   147: aload_0
/*   0*/    //   148: iload_3
/*   0*/    //   149: bipush #58
/*   0*/    //   151: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*   0*/    //   154: ifeq -> 160
/*   0*/    //   157: iinc #3, 1
/*   0*/    //   160: aload_0
/*   0*/    //   161: iload_3
/*   0*/    //   162: iinc #3, 2
/*   0*/    //   165: iload_3
/*   0*/    //   166: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   169: istore #8
/*   0*/    //   171: aload_0
/*   0*/    //   172: iload_3
/*   0*/    //   173: bipush #58
/*   0*/    //   175: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*   0*/    //   178: ifeq -> 184
/*   0*/    //   181: iinc #3, 1
/*   0*/    //   184: aload_0
/*   0*/    //   185: invokevirtual length : ()I
/*   0*/    //   188: iload_3
/*   0*/    //   189: if_icmple -> 347
/*   0*/    //   192: aload_0
/*   0*/    //   193: iload_3
/*   0*/    //   194: invokevirtual charAt : (I)C
/*   0*/    //   197: istore #12
/*   0*/    //   199: iload #12
/*   0*/    //   201: bipush #90
/*   0*/    //   203: if_icmpeq -> 347
/*   0*/    //   206: iload #12
/*   0*/    //   208: bipush #43
/*   0*/    //   210: if_icmpeq -> 347
/*   0*/    //   213: iload #12
/*   0*/    //   215: bipush #45
/*   0*/    //   217: if_icmpeq -> 347
/*   0*/    //   220: aload_0
/*   0*/    //   221: iload_3
/*   0*/    //   222: iinc #3, 2
/*   0*/    //   225: iload_3
/*   0*/    //   226: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   229: istore #9
/*   0*/    //   231: iload #9
/*   0*/    //   233: bipush #59
/*   0*/    //   235: if_icmple -> 249
/*   0*/    //   238: iload #9
/*   0*/    //   240: bipush #63
/*   0*/    //   242: if_icmpge -> 249
/*   0*/    //   245: bipush #59
/*   0*/    //   247: istore #9
/*   0*/    //   249: aload_0
/*   0*/    //   250: iload_3
/*   0*/    //   251: bipush #46
/*   0*/    //   253: invokestatic checkOffset : (Ljava/lang/String;IC)Z
/*   0*/    //   256: ifeq -> 347
/*   0*/    //   259: iinc #3, 1
/*   0*/    //   262: aload_0
/*   0*/    //   263: iload_3
/*   0*/    //   264: iconst_1
/*   0*/    //   265: iadd
/*   0*/    //   266: invokestatic indexOfNonDigit : (Ljava/lang/String;I)I
/*   0*/    //   269: istore #13
/*   0*/    //   271: iload #13
/*   0*/    //   273: iload_3
/*   0*/    //   274: iconst_3
/*   0*/    //   275: iadd
/*   0*/    //   276: invokestatic min : (II)I
/*   0*/    //   279: istore #14
/*   0*/    //   281: aload_0
/*   0*/    //   282: iload_3
/*   0*/    //   283: iload #14
/*   0*/    //   285: invokestatic parseInt : (Ljava/lang/String;II)I
/*   0*/    //   288: istore #15
/*   0*/    //   290: iload #14
/*   0*/    //   292: iload_3
/*   0*/    //   293: isub
/*   0*/    //   294: lookupswitch default -> 340, 1 -> 330, 2 -> 320
/*   0*/    //   320: iload #15
/*   0*/    //   322: bipush #10
/*   0*/    //   324: imul
/*   0*/    //   325: istore #10
/*   0*/    //   327: goto -> 344
/*   0*/    //   330: iload #15
/*   0*/    //   332: bipush #100
/*   0*/    //   334: imul
/*   0*/    //   335: istore #10
/*   0*/    //   337: goto -> 344
/*   0*/    //   340: iload #15
/*   0*/    //   342: istore #10
/*   0*/    //   344: iload #13
/*   0*/    //   346: istore_3
/*   0*/    //   347: aload_0
/*   0*/    //   348: invokevirtual length : ()I
/*   0*/    //   351: iload_3
/*   0*/    //   352: if_icmpgt -> 365
/*   0*/    //   355: new java/lang/IllegalArgumentException
/*   0*/    //   358: dup
/*   0*/    //   359: ldc 'No time zone indicator'
/*   0*/    //   361: invokespecial <init> : (Ljava/lang/String;)V
/*   0*/    //   364: athrow
/*   0*/    //   365: aconst_null
/*   0*/    //   366: astore #12
/*   0*/    //   368: aload_0
/*   0*/    //   369: iload_3
/*   0*/    //   370: invokevirtual charAt : (I)C
/*   0*/    //   373: istore #13
/*   0*/    //   375: iload #13
/*   0*/    //   377: bipush #90
/*   0*/    //   379: if_icmpne -> 393
/*   0*/    //   382: getstatic com/google/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
/*   0*/    //   385: astore #12
/*   0*/    //   387: iinc #3, 1
/*   0*/    //   390: goto -> 594
/*   0*/    //   393: iload #13
/*   0*/    //   395: bipush #43
/*   0*/    //   397: if_icmpeq -> 407
/*   0*/    //   400: iload #13
/*   0*/    //   402: bipush #45
/*   0*/    //   404: if_icmpne -> 561
/*   0*/    //   407: aload_0
/*   0*/    //   408: iload_3
/*   0*/    //   409: invokevirtual substring : (I)Ljava/lang/String;
/*   0*/    //   412: astore #14
/*   0*/    //   414: iload_3
/*   0*/    //   415: aload #14
/*   0*/    //   417: invokevirtual length : ()I
/*   0*/    //   420: iadd
/*   0*/    //   421: istore_3
/*   0*/    //   422: ldc '+0000'
/*   0*/    //   424: aload #14
/*   0*/    //   426: invokevirtual equals : (Ljava/lang/Object;)Z
/*   0*/    //   429: ifne -> 442
/*   0*/    //   432: ldc '+00:00'
/*   0*/    //   434: aload #14
/*   0*/    //   436: invokevirtual equals : (Ljava/lang/Object;)Z
/*   0*/    //   439: ifeq -> 450
/*   0*/    //   442: getstatic com/google/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
/*   0*/    //   445: astore #12
/*   0*/    //   447: goto -> 558
/*   0*/    //   450: new java/lang/StringBuilder
/*   0*/    //   453: dup
/*   0*/    //   454: invokespecial <init> : ()V
/*   0*/    //   457: ldc 'GMT'
/*   0*/    //   459: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   462: aload #14
/*   0*/    //   464: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   467: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   470: astore #15
/*   0*/    //   472: aload #15
/*   0*/    //   474: invokestatic getTimeZone : (Ljava/lang/String;)Ljava/util/TimeZone;
/*   0*/    //   477: astore #12
/*   0*/    //   479: aload #12
/*   0*/    //   481: invokevirtual getID : ()Ljava/lang/String;
/*   0*/    //   484: astore #16
/*   0*/    //   486: aload #16
/*   0*/    //   488: aload #15
/*   0*/    //   490: invokevirtual equals : (Ljava/lang/Object;)Z
/*   0*/    //   493: ifne -> 558
/*   0*/    //   496: aload #16
/*   0*/    //   498: ldc ':'
/*   0*/    //   500: ldc ''
/*   0*/    //   502: invokevirtual replace : (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*   0*/    //   505: astore #17
/*   0*/    //   507: aload #17
/*   0*/    //   509: aload #15
/*   0*/    //   511: invokevirtual equals : (Ljava/lang/Object;)Z
/*   0*/    //   514: ifeq -> 558
/*   0*/    //   517: new java/lang/IndexOutOfBoundsException
/*   0*/    //   520: dup
/*   0*/    //   521: new java/lang/StringBuilder
/*   0*/    //   524: dup
/*   0*/    //   525: invokespecial <init> : ()V
/*   0*/    //   528: ldc 'Mismatching time zone indicator: '
/*   0*/    //   530: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   533: aload #15
/*   0*/    //   535: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   538: ldc ' given, resolves to '
/*   0*/    //   540: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   543: aload #12
/*   0*/    //   545: invokevirtual getID : ()Ljava/lang/String;
/*   0*/    //   548: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   551: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   554: invokespecial <init> : (Ljava/lang/String;)V
/*   0*/    //   557: athrow
/*   0*/    //   558: goto -> 594
/*   0*/    //   561: new java/lang/IndexOutOfBoundsException
/*   0*/    //   564: dup
/*   0*/    //   565: new java/lang/StringBuilder
/*   0*/    //   568: dup
/*   0*/    //   569: invokespecial <init> : ()V
/*   0*/    //   572: ldc 'Invalid time zone indicator ''
/*   0*/    //   574: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   577: iload #13
/*   0*/    //   579: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*   0*/    //   582: ldc '''
/*   0*/    //   584: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   587: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   590: invokespecial <init> : (Ljava/lang/String;)V
/*   0*/    //   593: athrow
/*   0*/    //   594: new java/util/GregorianCalendar
/*   0*/    //   597: dup
/*   0*/    //   598: aload #12
/*   0*/    //   600: invokespecial <init> : (Ljava/util/TimeZone;)V
/*   0*/    //   603: astore #14
/*   0*/    //   605: aload #14
/*   0*/    //   607: iconst_0
/*   0*/    //   608: invokevirtual setLenient : (Z)V
/*   0*/    //   611: aload #14
/*   0*/    //   613: iconst_1
/*   0*/    //   614: iload #4
/*   0*/    //   616: invokevirtual set : (II)V
/*   0*/    //   619: aload #14
/*   0*/    //   621: iconst_2
/*   0*/    //   622: iload #5
/*   0*/    //   624: iconst_1
/*   0*/    //   625: isub
/*   0*/    //   626: invokevirtual set : (II)V
/*   0*/    //   629: aload #14
/*   0*/    //   631: iconst_5
/*   0*/    //   632: iload #6
/*   0*/    //   634: invokevirtual set : (II)V
/*   0*/    //   637: aload #14
/*   0*/    //   639: bipush #11
/*   0*/    //   641: iload #7
/*   0*/    //   643: invokevirtual set : (II)V
/*   0*/    //   646: aload #14
/*   0*/    //   648: bipush #12
/*   0*/    //   650: iload #8
/*   0*/    //   652: invokevirtual set : (II)V
/*   0*/    //   655: aload #14
/*   0*/    //   657: bipush #13
/*   0*/    //   659: iload #9
/*   0*/    //   661: invokevirtual set : (II)V
/*   0*/    //   664: aload #14
/*   0*/    //   666: bipush #14
/*   0*/    //   668: iload #10
/*   0*/    //   670: invokevirtual set : (II)V
/*   0*/    //   673: aload_1
/*   0*/    //   674: iload_3
/*   0*/    //   675: invokevirtual setIndex : (I)V
/*   0*/    //   678: aload #14
/*   0*/    //   680: invokevirtual getTime : ()Ljava/util/Date;
/*   0*/    //   683: areturn
/*   0*/    //   684: astore_3
/*   0*/    //   685: aload_3
/*   0*/    //   686: astore_2
/*   0*/    //   687: goto -> 699
/*   0*/    //   690: astore_3
/*   0*/    //   691: aload_3
/*   0*/    //   692: astore_2
/*   0*/    //   693: goto -> 699
/*   0*/    //   696: astore_3
/*   0*/    //   697: aload_3
/*   0*/    //   698: astore_2
/*   0*/    //   699: aload_0
/*   0*/    //   700: ifnonnull -> 707
/*   0*/    //   703: aconst_null
/*   0*/    //   704: goto -> 731
/*   0*/    //   707: new java/lang/StringBuilder
/*   0*/    //   710: dup
/*   0*/    //   711: invokespecial <init> : ()V
/*   0*/    //   714: bipush #34
/*   0*/    //   716: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*   0*/    //   719: aload_0
/*   0*/    //   720: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   723: ldc '''
/*   0*/    //   725: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   728: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   731: astore_3
/*   0*/    //   732: aload_2
/*   0*/    //   733: invokevirtual getMessage : ()Ljava/lang/String;
/*   0*/    //   736: astore #4
/*   0*/    //   738: aload #4
/*   0*/    //   740: ifnull -> 751
/*   0*/    //   743: aload #4
/*   0*/    //   745: invokevirtual isEmpty : ()Z
/*   0*/    //   748: ifeq -> 783
/*   0*/    //   751: new java/lang/StringBuilder
/*   0*/    //   754: dup
/*   0*/    //   755: invokespecial <init> : ()V
/*   0*/    //   758: ldc '('
/*   0*/    //   760: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   763: aload_2
/*   0*/    //   764: invokevirtual getClass : ()Ljava/lang/Class;
/*   0*/    //   767: invokevirtual getName : ()Ljava/lang/String;
/*   0*/    //   770: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   773: ldc ')'
/*   0*/    //   775: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   778: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   781: astore #4
/*   0*/    //   783: new java/text/ParseException
/*   0*/    //   786: dup
/*   0*/    //   787: new java/lang/StringBuilder
/*   0*/    //   790: dup
/*   0*/    //   791: invokespecial <init> : ()V
/*   0*/    //   794: ldc 'Failed to parse date ['
/*   0*/    //   796: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   799: aload_3
/*   0*/    //   800: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   803: ldc ']: '
/*   0*/    //   805: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   808: aload #4
/*   0*/    //   810: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   813: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   816: aload_1
/*   0*/    //   817: invokevirtual getIndex : ()I
/*   0*/    //   820: invokespecial <init> : (Ljava/lang/String;I)V
/*   0*/    //   823: astore #5
/*   0*/    //   825: aload #5
/*   0*/    //   827: aload_2
/*   0*/    //   828: invokevirtual initCause : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*   0*/    //   831: pop
/*   0*/    //   832: aload #5
/*   0*/    //   834: athrow
/*   0*/    // Line number table:
/*   0*/    //   Java source line number -> byte code offset
/*   0*/    //   #124	-> 0
/*   0*/    //   #126	-> 2
/*   0*/    //   #129	-> 7
/*   0*/    //   #130	-> 18
/*   0*/    //   #131	-> 28
/*   0*/    //   #135	-> 31
/*   0*/    //   #136	-> 42
/*   0*/    //   #137	-> 52
/*   0*/    //   #141	-> 55
/*   0*/    //   #143	-> 66
/*   0*/    //   #144	-> 69
/*   0*/    //   #145	-> 72
/*   0*/    //   #146	-> 75
/*   0*/    //   #149	-> 78
/*   0*/    //   #151	-> 87
/*   0*/    //   #152	-> 100
/*   0*/    //   #154	-> 117
/*   0*/    //   #155	-> 122
/*   0*/    //   #158	-> 128
/*   0*/    //   #161	-> 133
/*   0*/    //   #162	-> 147
/*   0*/    //   #163	-> 157
/*   0*/    //   #166	-> 160
/*   0*/    //   #167	-> 171
/*   0*/    //   #168	-> 181
/*   0*/    //   #171	-> 184
/*   0*/    //   #172	-> 192
/*   0*/    //   #173	-> 199
/*   0*/    //   #174	-> 220
/*   0*/    //   #175	-> 231
/*   0*/    //   #177	-> 249
/*   0*/    //   #178	-> 259
/*   0*/    //   #179	-> 262
/*   0*/    //   #180	-> 271
/*   0*/    //   #181	-> 281
/*   0*/    //   #183	-> 290
/*   0*/    //   #185	-> 320
/*   0*/    //   #186	-> 327
/*   0*/    //   #188	-> 330
/*   0*/    //   #189	-> 337
/*   0*/    //   #191	-> 340
/*   0*/    //   #193	-> 344
/*   0*/    //   #200	-> 347
/*   0*/    //   #201	-> 355
/*   0*/    //   #204	-> 365
/*   0*/    //   #205	-> 368
/*   0*/    //   #207	-> 375
/*   0*/    //   #208	-> 382
/*   0*/    //   #209	-> 387
/*   0*/    //   #210	-> 393
/*   0*/    //   #211	-> 407
/*   0*/    //   #215	-> 414
/*   0*/    //   #217	-> 422
/*   0*/    //   #218	-> 442
/*   0*/    //   #224	-> 450
/*   0*/    //   #227	-> 472
/*   0*/    //   #229	-> 479
/*   0*/    //   #230	-> 486
/*   0*/    //   #236	-> 496
/*   0*/    //   #237	-> 507
/*   0*/    //   #238	-> 517
/*   0*/    //   #243	-> 558
/*   0*/    //   #244	-> 561
/*   0*/    //   #247	-> 594
/*   0*/    //   #248	-> 605
/*   0*/    //   #249	-> 611
/*   0*/    //   #250	-> 619
/*   0*/    //   #251	-> 629
/*   0*/    //   #252	-> 637
/*   0*/    //   #253	-> 646
/*   0*/    //   #254	-> 655
/*   0*/    //   #255	-> 664
/*   0*/    //   #257	-> 673
/*   0*/    //   #258	-> 678
/*   0*/    //   #261	-> 684
/*   0*/    //   #262	-> 685
/*   0*/    //   #267	-> 687
/*   0*/    //   #263	-> 690
/*   0*/    //   #264	-> 691
/*   0*/    //   #267	-> 693
/*   0*/    //   #265	-> 696
/*   0*/    //   #266	-> 697
/*   0*/    //   #268	-> 699
/*   0*/    //   #269	-> 732
/*   0*/    //   #270	-> 738
/*   0*/    //   #271	-> 751
/*   0*/    //   #273	-> 783
/*   0*/    //   #274	-> 825
/*   0*/    //   #275	-> 832
/*   0*/    // Local variable table:
/*   0*/    //   start	length	slot	name	descriptor
/*   0*/    //   117	11	12	calendar	Ljava/util/Calendar;
/*   0*/    //   271	76	13	endOffset	I
/*   0*/    //   281	66	14	parseEndOffset	I
/*   0*/    //   290	57	15	fraction	I
/*   0*/    //   199	148	12	c	C
/*   0*/    //   507	51	17	cleaned	Ljava/lang/String;
/*   0*/    //   472	86	15	timezoneId	Ljava/lang/String;
/*   0*/    //   486	72	16	act	Ljava/lang/String;
/*   0*/    //   414	144	14	timezoneOffset	Ljava/lang/String;
/*   0*/    //   7	677	3	offset	I
/*   0*/    //   18	666	4	year	I
/*   0*/    //   42	642	5	month	I
/*   0*/    //   66	618	6	day	I
/*   0*/    //   69	615	7	hour	I
/*   0*/    //   72	612	8	minutes	I
/*   0*/    //   75	609	9	seconds	I
/*   0*/    //   78	606	10	milliseconds	I
/*   0*/    //   87	597	11	hasT	Z
/*   0*/    //   368	316	12	timezone	Ljava/util/TimeZone;
/*   0*/    //   375	309	13	timezoneIndicator	C
/*   0*/    //   605	79	14	calendar	Ljava/util/Calendar;
/*   0*/    //   685	2	3	e	Ljava/lang/IndexOutOfBoundsException;
/*   0*/    //   691	2	3	e	Ljava/lang/NumberFormatException;
/*   0*/    //   697	2	3	e	Ljava/lang/IllegalArgumentException;
/*   0*/    //   0	835	0	date	Ljava/lang/String;
/*   0*/    //   0	835	1	pos	Ljava/text/ParsePosition;
/*   0*/    //   2	833	2	fail	Ljava/lang/Exception;
/*   0*/    //   732	103	3	input	Ljava/lang/String;
/*   0*/    //   738	97	4	msg	Ljava/lang/String;
/*   0*/    //   825	10	5	ex	Ljava/text/ParseException;
/*   0*/    // Exception table:
/*   0*/    //   from	to	target	type
/*   0*/    //   2	127	684	java/lang/IndexOutOfBoundsException
/*   0*/    //   2	127	690	java/lang/NumberFormatException
/*   0*/    //   2	127	696	java/lang/IllegalArgumentException
/*   0*/    //   128	683	684	java/lang/IndexOutOfBoundsException
/*   0*/    //   128	683	690	java/lang/NumberFormatException
/*   0*/    //   128	683	696	java/lang/IllegalArgumentException
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean checkOffset(String value, int offset, char expected) {
/* 287*/    return (offset < value.length() && value.charAt(offset) == expected);
/*   0*/  }
/*   0*/  
/*   0*/  private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
/* 300*/    if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
/* 301*/        throw new NumberFormatException(value); 
/*   0*/       }
/* 304*/    int i = beginIndex;
/* 305*/    int result = 0;
/* 307*/    if (i < endIndex) {
/* 308*/      int digit = Character.digit(value.charAt(i++), 10);
/* 309*/      if (digit < 0) {
/* 310*/          throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex)); 
/*   0*/         }
/* 312*/      result = -digit;
/*   0*/    } 
/* 314*/    while (i < endIndex) {
/* 315*/      int digit = Character.digit(value.charAt(i++), 10);
/* 316*/      if (digit < 0) {
/* 317*/          throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex)); 
/*   0*/         }
/* 319*/      result *= 10;
/* 320*/      result -= digit;
/*   0*/    } 
/* 322*/    return -result;
/*   0*/  }
/*   0*/  
/*   0*/  private static void padInt(StringBuilder buffer, int value, int length) {
/* 333*/    String strValue = Integer.toString(value);
/* 334*/    for (int i = length - strValue.length(); i > 0; i--) {
/* 335*/        buffer.append('0'); 
/*   0*/       }
/* 337*/    buffer.append(strValue);
/*   0*/  }
/*   0*/  
/*   0*/  private static int indexOfNonDigit(String string, int offset) {
/* 344*/    for (int i = offset; i < string.length(); i++) {
/* 345*/      char c = string.charAt(i);
/* 346*/      if (c < '0' || c > '9') {
/* 346*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 348*/    return string.length();
/*   0*/  }
/*   0*/}
