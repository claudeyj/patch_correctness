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
/*   0*/    //   390: goto -> 595
/*   0*/    //   393: iload #13
/*   0*/    //   395: bipush #43
/*   0*/    //   397: if_icmpeq -> 407
/*   0*/    //   400: iload #13
/*   0*/    //   402: bipush #45
/*   0*/    //   404: if_icmpne -> 562
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
/*   0*/    //   447: goto -> 559
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
/*   0*/    //   493: pop
/*   0*/    //   494: goto -> 559
/*   0*/    //   497: aload #16
/*   0*/    //   499: ldc ':'
/*   0*/    //   501: ldc ''
/*   0*/    //   503: invokevirtual replace : (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
/*   0*/    //   506: astore #17
/*   0*/    //   508: aload #17
/*   0*/    //   510: aload #15
/*   0*/    //   512: invokevirtual equals : (Ljava/lang/Object;)Z
/*   0*/    //   515: ifne -> 559
/*   0*/    //   518: new java/lang/IndexOutOfBoundsException
/*   0*/    //   521: dup
/*   0*/    //   522: new java/lang/StringBuilder
/*   0*/    //   525: dup
/*   0*/    //   526: invokespecial <init> : ()V
/*   0*/    //   529: ldc 'Mismatching time zone indicator: '
/*   0*/    //   531: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   534: aload #15
/*   0*/    //   536: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   539: ldc ' given, resolves to '
/*   0*/    //   541: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   544: aload #12
/*   0*/    //   546: invokevirtual getID : ()Ljava/lang/String;
/*   0*/    //   549: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   552: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   555: invokespecial <init> : (Ljava/lang/String;)V
/*   0*/    //   558: athrow
/*   0*/    //   559: goto -> 595
/*   0*/    //   562: new java/lang/IndexOutOfBoundsException
/*   0*/    //   565: dup
/*   0*/    //   566: new java/lang/StringBuilder
/*   0*/    //   569: dup
/*   0*/    //   570: invokespecial <init> : ()V
/*   0*/    //   573: ldc 'Invalid time zone indicator ''
/*   0*/    //   575: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   578: iload #13
/*   0*/    //   580: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*   0*/    //   583: ldc '''
/*   0*/    //   585: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   588: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   591: invokespecial <init> : (Ljava/lang/String;)V
/*   0*/    //   594: athrow
/*   0*/    //   595: new java/util/GregorianCalendar
/*   0*/    //   598: dup
/*   0*/    //   599: aload #12
/*   0*/    //   601: invokespecial <init> : (Ljava/util/TimeZone;)V
/*   0*/    //   604: astore #14
/*   0*/    //   606: aload #14
/*   0*/    //   608: iconst_0
/*   0*/    //   609: invokevirtual setLenient : (Z)V
/*   0*/    //   612: aload #14
/*   0*/    //   614: iconst_1
/*   0*/    //   615: iload #4
/*   0*/    //   617: invokevirtual set : (II)V
/*   0*/    //   620: aload #14
/*   0*/    //   622: iconst_2
/*   0*/    //   623: iload #5
/*   0*/    //   625: iconst_1
/*   0*/    //   626: isub
/*   0*/    //   627: invokevirtual set : (II)V
/*   0*/    //   630: aload #14
/*   0*/    //   632: iconst_5
/*   0*/    //   633: iload #6
/*   0*/    //   635: invokevirtual set : (II)V
/*   0*/    //   638: aload #14
/*   0*/    //   640: bipush #11
/*   0*/    //   642: iload #7
/*   0*/    //   644: invokevirtual set : (II)V
/*   0*/    //   647: aload #14
/*   0*/    //   649: bipush #12
/*   0*/    //   651: iload #8
/*   0*/    //   653: invokevirtual set : (II)V
/*   0*/    //   656: aload #14
/*   0*/    //   658: bipush #13
/*   0*/    //   660: iload #9
/*   0*/    //   662: invokevirtual set : (II)V
/*   0*/    //   665: aload #14
/*   0*/    //   667: bipush #14
/*   0*/    //   669: iload #10
/*   0*/    //   671: invokevirtual set : (II)V
/*   0*/    //   674: aload_1
/*   0*/    //   675: iload_3
/*   0*/    //   676: invokevirtual setIndex : (I)V
/*   0*/    //   679: aload #14
/*   0*/    //   681: invokevirtual getTime : ()Ljava/util/Date;
/*   0*/    //   684: areturn
/*   0*/    //   685: astore_3
/*   0*/    //   686: aload_3
/*   0*/    //   687: astore_2
/*   0*/    //   688: goto -> 700
/*   0*/    //   691: astore_3
/*   0*/    //   692: aload_3
/*   0*/    //   693: astore_2
/*   0*/    //   694: goto -> 700
/*   0*/    //   697: astore_3
/*   0*/    //   698: aload_3
/*   0*/    //   699: astore_2
/*   0*/    //   700: aload_0
/*   0*/    //   701: ifnonnull -> 708
/*   0*/    //   704: aconst_null
/*   0*/    //   705: goto -> 732
/*   0*/    //   708: new java/lang/StringBuilder
/*   0*/    //   711: dup
/*   0*/    //   712: invokespecial <init> : ()V
/*   0*/    //   715: bipush #34
/*   0*/    //   717: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*   0*/    //   720: aload_0
/*   0*/    //   721: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   724: ldc '''
/*   0*/    //   726: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   729: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   732: astore_3
/*   0*/    //   733: aload_2
/*   0*/    //   734: invokevirtual getMessage : ()Ljava/lang/String;
/*   0*/    //   737: astore #4
/*   0*/    //   739: aload #4
/*   0*/    //   741: ifnull -> 752
/*   0*/    //   744: aload #4
/*   0*/    //   746: invokevirtual isEmpty : ()Z
/*   0*/    //   749: ifeq -> 784
/*   0*/    //   752: new java/lang/StringBuilder
/*   0*/    //   755: dup
/*   0*/    //   756: invokespecial <init> : ()V
/*   0*/    //   759: ldc '('
/*   0*/    //   761: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   764: aload_2
/*   0*/    //   765: invokevirtual getClass : ()Ljava/lang/Class;
/*   0*/    //   768: invokevirtual getName : ()Ljava/lang/String;
/*   0*/    //   771: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   774: ldc ')'
/*   0*/    //   776: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   779: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   782: astore #4
/*   0*/    //   784: new java/text/ParseException
/*   0*/    //   787: dup
/*   0*/    //   788: new java/lang/StringBuilder
/*   0*/    //   791: dup
/*   0*/    //   792: invokespecial <init> : ()V
/*   0*/    //   795: ldc 'Failed to parse date ['
/*   0*/    //   797: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   800: aload_3
/*   0*/    //   801: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   804: ldc ']: '
/*   0*/    //   806: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   809: aload #4
/*   0*/    //   811: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*   0*/    //   814: invokevirtual toString : ()Ljava/lang/String;
/*   0*/    //   817: aload_1
/*   0*/    //   818: invokevirtual getIndex : ()I
/*   0*/    //   821: invokespecial <init> : (Ljava/lang/String;I)V
/*   0*/    //   824: astore #5
/*   0*/    //   826: aload #5
/*   0*/    //   828: aload_2
/*   0*/    //   829: invokevirtual initCause : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*   0*/    //   832: pop
/*   0*/    //   833: aload #5
/*   0*/    //   835: athrow
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
/*   0*/    //   #236	-> 497
/*   0*/    //   #237	-> 508
/*   0*/    //   #238	-> 518
/*   0*/    //   #243	-> 559
/*   0*/    //   #244	-> 562
/*   0*/    //   #247	-> 595
/*   0*/    //   #248	-> 606
/*   0*/    //   #249	-> 612
/*   0*/    //   #250	-> 620
/*   0*/    //   #251	-> 630
/*   0*/    //   #252	-> 638
/*   0*/    //   #253	-> 647
/*   0*/    //   #254	-> 656
/*   0*/    //   #255	-> 665
/*   0*/    //   #257	-> 674
/*   0*/    //   #258	-> 679
/*   0*/    //   #261	-> 685
/*   0*/    //   #262	-> 686
/*   0*/    //   #267	-> 688
/*   0*/    //   #263	-> 691
/*   0*/    //   #264	-> 692
/*   0*/    //   #267	-> 694
/*   0*/    //   #265	-> 697
/*   0*/    //   #266	-> 698
/*   0*/    //   #268	-> 700
/*   0*/    //   #269	-> 733
/*   0*/    //   #270	-> 739
/*   0*/    //   #271	-> 752
/*   0*/    //   #273	-> 784
/*   0*/    //   #274	-> 826
/*   0*/    //   #275	-> 833
/*   0*/    // Local variable table:
/*   0*/    //   start	length	slot	name	descriptor
/*   0*/    //   117	11	12	calendar	Ljava/util/Calendar;
/*   0*/    //   271	76	13	endOffset	I
/*   0*/    //   281	66	14	parseEndOffset	I
/*   0*/    //   290	57	15	fraction	I
/*   0*/    //   199	148	12	c	C
/*   0*/    //   508	51	17	cleaned	Ljava/lang/String;
/*   0*/    //   472	87	15	timezoneId	Ljava/lang/String;
/*   0*/    //   486	73	16	act	Ljava/lang/String;
/*   0*/    //   414	145	14	timezoneOffset	Ljava/lang/String;
/*   0*/    //   7	678	3	offset	I
/*   0*/    //   18	667	4	year	I
/*   0*/    //   42	643	5	month	I
/*   0*/    //   66	619	6	day	I
/*   0*/    //   69	616	7	hour	I
/*   0*/    //   72	613	8	minutes	I
/*   0*/    //   75	610	9	seconds	I
/*   0*/    //   78	607	10	milliseconds	I
/*   0*/    //   87	598	11	hasT	Z
/*   0*/    //   368	317	12	timezone	Ljava/util/TimeZone;
/*   0*/    //   375	310	13	timezoneIndicator	C
/*   0*/    //   606	79	14	calendar	Ljava/util/Calendar;
/*   0*/    //   686	2	3	e	Ljava/lang/IndexOutOfBoundsException;
/*   0*/    //   692	2	3	e	Ljava/lang/NumberFormatException;
/*   0*/    //   698	2	3	e	Ljava/lang/IllegalArgumentException;
/*   0*/    //   0	836	0	date	Ljava/lang/String;
/*   0*/    //   0	836	1	pos	Ljava/text/ParsePosition;
/*   0*/    //   2	834	2	fail	Ljava/lang/Exception;
/*   0*/    //   733	103	3	input	Ljava/lang/String;
/*   0*/    //   739	97	4	msg	Ljava/lang/String;
/*   0*/    //   826	10	5	ex	Ljava/text/ParseException;
/*   0*/    // Exception table:
/*   0*/    //   from	to	target	type
/*   0*/    //   2	127	685	java/lang/IndexOutOfBoundsException
/*   0*/    //   2	127	691	java/lang/NumberFormatException
/*   0*/    //   2	127	697	java/lang/IllegalArgumentException
/*   0*/    //   128	684	685	java/lang/IndexOutOfBoundsException
/*   0*/    //   128	684	691	java/lang/NumberFormatException
/*   0*/    //   128	684	697	java/lang/IllegalArgumentException
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
