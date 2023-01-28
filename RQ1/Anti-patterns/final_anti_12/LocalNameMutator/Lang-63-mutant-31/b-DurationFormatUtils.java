/*   0*/package org.apache.commons.lang.time;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Calendar;
/*   0*/import java.util.Date;
/*   0*/import java.util.TimeZone;
/*   0*/import org.apache.commons.lang.StringUtils;
/*   0*/
/*   0*/public class DurationFormatUtils {
/*   0*/  public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.S'S'";
/*   0*/  
/*   0*/  public static String formatDurationHMS(long durationMillis) {
/*  80*/    return formatDuration(durationMillis, "H:mm:ss.SSS");
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatDurationISO(long durationMillis) {
/*  95*/    return formatDuration(durationMillis, "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.S'S'", false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatDuration(long durationMillis, String format) {
/* 110*/    return formatDuration(durationMillis, format, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatDuration(long durationMillis, String format, boolean padWithZeros) {
/* 128*/    Token[] tokens = lexx(format);
/* 130*/    int days = 0;
/* 131*/    int hours = 0;
/* 132*/    int minutes = 0;
/* 133*/    int seconds = 0;
/* 134*/    int milliseconds = 0;
/* 136*/    if (Token.containsTokenWithValue(tokens, d)) {
/* 137*/      days = (int)(durationMillis / 86400000L);
/* 138*/      durationMillis -= days * 86400000L;
/*   0*/    } 
/* 140*/    if (Token.containsTokenWithValue(tokens, H)) {
/* 141*/      hours = (int)(durationMillis / 3600000L);
/* 142*/      durationMillis -= hours * 3600000L;
/*   0*/    } 
/* 144*/    if (Token.containsTokenWithValue(tokens, m)) {
/* 145*/      minutes = (int)(durationMillis / 60000L);
/* 146*/      durationMillis -= minutes * 60000L;
/*   0*/    } 
/* 148*/    if (Token.containsTokenWithValue(tokens, s)) {
/* 149*/      seconds = (int)(durationMillis / 1000L);
/* 150*/      durationMillis -= seconds * 1000L;
/*   0*/    } 
/* 152*/    if (Token.containsTokenWithValue(tokens, S))
/* 153*/      milliseconds = (int)durationMillis; 
/* 156*/    return format(tokens, 0, 0, days, hours, minutes, seconds, milliseconds, padWithZeros);
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatDurationWords(long durationMillis, boolean suppressLeadingZeroElements, boolean suppressTrailingZeroElements) {
/* 178*/    String duration = formatDuration(durationMillis, "d' days 'H' hours 'm' minutes 's' seconds'");
/* 179*/    if (suppressLeadingZeroElements) {
/* 181*/      duration = " " + duration;
/* 182*/      String tmp = StringUtils.replaceOnce(duration, " 0 days", "");
/* 183*/      if (tmp.length() != duration.length()) {
/* 184*/        duration = tmp;
/* 185*/        tmp = StringUtils.replaceOnce(duration, " 0 hours", "");
/* 186*/        if (tmp.length() != duration.length()) {
/* 187*/          duration = tmp;
/* 188*/          tmp = StringUtils.replaceOnce(duration, " 0 minutes", "");
/* 189*/          duration = tmp;
/* 190*/          if (tmp.length() != duration.length())
/* 191*/            duration = StringUtils.replaceOnce(tmp, " 0 seconds", ""); 
/*   0*/        } 
/*   0*/      } 
/* 195*/      if (duration.length() != 0)
/* 197*/        duration = duration.substring(1); 
/*   0*/    } 
/* 200*/    if (suppressTrailingZeroElements) {
/* 201*/      String tmp = StringUtils.replaceOnce(duration, " 0 seconds", "");
/* 202*/      if (tmp.length() != duration.length()) {
/* 203*/        duration = tmp;
/* 204*/        tmp = StringUtils.replaceOnce(duration, " 0 minutes", "");
/* 205*/        if (tmp.length() != duration.length()) {
/* 206*/          duration = tmp;
/* 207*/          tmp = StringUtils.replaceOnce(duration, " 0 hours", "");
/* 208*/          if (tmp.length() != duration.length())
/* 209*/            duration = StringUtils.replaceOnce(tmp, " 0 days", ""); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 215*/    duration = " " + duration;
/* 216*/    duration = StringUtils.replaceOnce(duration, " 1 seconds", " 1 second");
/* 217*/    duration = StringUtils.replaceOnce(duration, " 1 minutes", " 1 minute");
/* 218*/    duration = StringUtils.replaceOnce(duration, " 1 hours", " 1 hour");
/* 219*/    duration = StringUtils.replaceOnce(duration, " 1 days", " 1 day");
/* 220*/    return duration.trim();
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatPeriodISO(long startMillis, long endMillis) {
/* 234*/    return formatPeriod(startMillis, endMillis, "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.S'S'", false, TimeZone.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatPeriod(long startMillis, long endMillis, String format) {
/* 247*/    return formatPeriod(startMillis, endMillis, format, true, TimeZone.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatPeriod(long startMillis, long endMillis, String format, boolean padWithZeros, TimeZone timezone) {
/* 265*/    long millis = endMillis - startMillis;
/* 266*/    if (millis < 2419200000L)
/* 267*/      return formatDuration(millis, format, padWithZeros); 
/* 270*/    Token[] tokens = lexx(format);
/* 274*/    Calendar start = Calendar.getInstance(timezone);
/* 275*/    start.setTime(new Date(startMillis));
/* 276*/    Calendar end = Calendar.getInstance(timezone);
/* 277*/    end.setTime(new Date(endMillis));
/* 280*/    int milliseconds = end.get(14) - start.get(14);
/* 281*/    int seconds = end.get(13) - start.get(13);
/* 282*/    int minutes = end.get(12) - start.get(12);
/* 283*/    int hours = end.get(11) - start.get(11);
/* 284*/    int days = end.get(5) - start.get(5);
/* 285*/    int months = end.get(2) - start.get(2);
/* 286*/    int years = end.get(1) - start.get(1);
/* 289*/    while (milliseconds < 0) {
/* 290*/      milliseconds += 1000;
/* 291*/      seconds--;
/*   0*/    } 
/* 293*/    while (seconds < 0) {
/* 294*/      seconds += 60;
/* 295*/      minutes--;
/*   0*/    } 
/* 297*/    while (minutes < 0) {
/* 298*/      minutes += 60;
/* 299*/      hours--;
/*   0*/    } 
/* 301*/    while (hours < 0) {
/* 302*/      hours += 24;
/* 303*/      days--;
/*   0*/    } 
/* 305*/    while (days < 0) {
/* 306*/      days += 31;
/* 312*/      months--;
/*   0*/    } 
/* 314*/    while (months < 0) {
/* 315*/      months += 12;
/* 316*/      years--;
/*   0*/    } 
/* 318*/    milliseconds -= reduceAndCorrect(start, end, 14, milliseconds);
/* 319*/    seconds -= reduceAndCorrect(start, end, 13, seconds);
/* 320*/    minutes -= reduceAndCorrect(start, end, 12, minutes);
/* 321*/    hours -= reduceAndCorrect(start, end, 11, hours);
/* 322*/    days -= reduceAndCorrect(start, end, 5, days);
/* 323*/    months -= reduceAndCorrect(start, start, 2, months);
/* 324*/    years -= reduceAndCorrect(start, end, 1, years);
/* 329*/    if (!Token.containsTokenWithValue(tokens, y))
/* 330*/      if (Token.containsTokenWithValue(tokens, M)) {
/* 331*/        months += 12 * years;
/* 332*/        years = 0;
/*   0*/      } else {
/* 335*/        days += 365 * years;
/* 336*/        years = 0;
/*   0*/      }  
/* 339*/    if (!Token.containsTokenWithValue(tokens, M)) {
/* 340*/      days += end.get(6) - start.get(6);
/* 341*/      months = 0;
/*   0*/    } 
/* 343*/    if (!Token.containsTokenWithValue(tokens, d)) {
/* 344*/      hours += 24 * days;
/* 345*/      days = 0;
/*   0*/    } 
/* 347*/    if (!Token.containsTokenWithValue(tokens, H)) {
/* 348*/      minutes += 60 * hours;
/* 349*/      hours = 0;
/*   0*/    } 
/* 351*/    if (!Token.containsTokenWithValue(tokens, m)) {
/* 352*/      seconds += 60 * minutes;
/* 353*/      minutes = 0;
/*   0*/    } 
/* 355*/    if (!Token.containsTokenWithValue(tokens, s)) {
/* 356*/      milliseconds += 1000 * seconds;
/* 357*/      seconds = 0;
/*   0*/    } 
/* 360*/    return format(tokens, years, months, days, hours, minutes, seconds, milliseconds, padWithZeros);
/*   0*/  }
/*   0*/  
/*   0*/  static String format(Token[] tokens, int years, int months, int days, int hours, int minutes, int seconds, int milliseconds, boolean padWithZeros) {
/* 380*/    StringBuffer buffer = new StringBuffer();
/*   0*/    boolean lastOutputSeconds = false;
/* 382*/    int sz = tokens.length;
/* 383*/    for (int i = 0; i < sz; i++) {
/* 384*/      Token token = tokens[i];
/* 385*/      Object value = token.getValue();
/* 386*/      int count = token.getCount();
/* 387*/      if (value instanceof StringBuffer) {
/* 388*/        buffer.append(value.toString());
/* 390*/      } else if (value == y) {
/* 391*/        buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(years), count, '0') : Integer.toString(years));
/* 393*/        lastOutputSeconds = false;
/* 394*/      } else if (value == M) {
/* 395*/        buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(months), count, '0') : Integer.toString(months));
/* 397*/        lastOutputSeconds = false;
/* 398*/      } else if (value == d) {
/* 399*/        buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(days), count, '0') : Integer.toString(days));
/* 401*/        lastOutputSeconds = false;
/* 402*/      } else if (value == H) {
/* 403*/        buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(hours), count, '0') : Integer.toString(hours));
/* 405*/        lastOutputSeconds = false;
/* 406*/      } else if (value == m) {
/* 407*/        buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(minutes), count, '0') : Integer.toString(minutes));
/* 409*/        lastOutputSeconds = false;
/* 410*/      } else if (value == s) {
/* 411*/        buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(seconds), count, '0') : Integer.toString(seconds));
/* 413*/        lastOutputSeconds = true;
/* 414*/      } else if (value == S) {
/* 415*/        if (lastOutputSeconds) {
/* 416*/          milliseconds += 1000;
/* 417*/          String str = padWithZeros ? StringUtils.leftPad(Integer.toString(milliseconds), count, '0') : Integer.toString(milliseconds);
/* 420*/          buffer.append(str.substring(1));
/*   0*/        } else {
/* 422*/          buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(milliseconds), count, '0') : Integer.toString(milliseconds));
/*   0*/        } 
/* 426*/        lastOutputSeconds = false;
/*   0*/      } 
/*   0*/    } 
/* 430*/    return buffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  static int reduceAndCorrect(Calendar start, Calendar end, int field, int difference) {
/* 433*/    end.add(field, -1 * difference);
/* 434*/    int endValue = end.get(field);
/* 435*/    int startValue = start.get(field);
/* 436*/    if (endValue < startValue) {
/* 437*/      int newdiff = startValue - endValue;
/* 438*/      end.add(field, newdiff);
/* 439*/      return newdiff;
/*   0*/    } 
/* 441*/    return 0;
/*   0*/  }
/*   0*/  
/* 445*/  static final Object y = "y";
/*   0*/  
/* 446*/  static final Object M = "M";
/*   0*/  
/* 447*/  static final Object d = "d";
/*   0*/  
/* 448*/  static final Object H = "H";
/*   0*/  
/* 449*/  static final Object m = "m";
/*   0*/  
/* 450*/  static final Object s = "s";
/*   0*/  
/* 451*/  static final Object S = "S";
/*   0*/  
/*   0*/  static Token[] lexx(String format) {
/* 460*/    char[] array = format.toCharArray();
/* 461*/    ArrayList list = new ArrayList(array.length);
/*   0*/    boolean inLiteral = false;
/* 464*/    StringBuffer buffer = null;
/* 465*/    Token previous = null;
/* 466*/    int sz = array.length;
/* 467*/    for (int i = 0; i < sz; i++) {
/* 468*/      char ch = array[i];
/* 469*/      if (inLiteral && ch != '\'') {
/* 470*/        buffer.append(ch);
/*   0*/      } else {
/* 473*/        Object value = null;
/* 474*/        switch (ch) {
/*   0*/          case '\'':
/* 477*/            if (inLiteral) {
/* 478*/              buffer = null;
/* 479*/              inLiteral = false;
/*   0*/              break;
/*   0*/            } 
/* 481*/            buffer = new StringBuffer();
/* 482*/            list.add(new Token(buffer));
/* 483*/            inLiteral = true;
/*   0*/            break;
/*   0*/          case 'y':
/* 486*/            value = y;
/*   0*/            break;
/*   0*/          case 'M':
/* 487*/            value = M;
/*   0*/            break;
/*   0*/          case 'd':
/* 488*/            value = d;
/*   0*/            break;
/*   0*/          case 'H':
/* 489*/            value = H;
/*   0*/            break;
/*   0*/          case 'm':
/* 490*/            value = m;
/*   0*/            break;
/*   0*/          case 's':
/* 491*/            value = s;
/*   0*/            break;
/*   0*/          case 'S':
/* 492*/            value = S;
/*   0*/            break;
/*   0*/          default:
/* 494*/            if (buffer == null) {
/* 495*/              buffer = new StringBuffer();
/* 496*/              list.add(new Token(buffer));
/*   0*/            } 
/* 498*/            buffer.append(ch);
/*   0*/            break;
/*   0*/        } 
/* 501*/        if (value != null) {
/* 502*/          if (previous != null && previous.getValue() == value) {
/* 503*/            previous.increment();
/*   0*/          } else {
/* 505*/            Token token = new Token(value);
/* 506*/            list.add(token);
/* 507*/            previous = token;
/*   0*/          } 
/* 509*/          buffer = null;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 512*/    return (Token[])list.toArray(new Token[0]);
/*   0*/  }
/*   0*/  
/*   0*/  static class Token {
/*   0*/    private Object value;
/*   0*/    
/*   0*/    private int count;
/*   0*/    
/*   0*/    static boolean containsTokenWithValue(Token[] tokens, Object value) {
/* 528*/      int sz = tokens.length;
/* 529*/      for (int i = 0; i < sz; i++) {
/* 530*/        if (tokens[i].getValue() == value)
/* 531*/          return true; 
/*   0*/      } 
/* 534*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    Token(Object value) {
/* 546*/      this.value = value;
/* 547*/      this.count = 1;
/*   0*/    }
/*   0*/    
/*   0*/    Token(Object value, int count) {
/* 558*/      this.value = value;
/* 559*/      this.count = count;
/*   0*/    }
/*   0*/    
/*   0*/    void increment() {
/* 566*/      this.count++;
/*   0*/    }
/*   0*/    
/*   0*/    int getCount() {
/* 575*/      return this.count;
/*   0*/    }
/*   0*/    
/*   0*/    Object getValue() {
/* 584*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object obj2) {
/* 594*/      if (obj2 instanceof Token) {
/* 595*/        Token tok2 = (Token)obj2;
/* 596*/        if (this.value.getClass() != tok2.value.getClass())
/* 597*/          return false; 
/* 599*/        if (this.count != tok2.count)
/* 600*/          return false; 
/* 602*/        if (this.value instanceof StringBuffer)
/* 603*/          return this.value.toString().equals(tok2.value.toString()); 
/* 604*/        if (this.value instanceof Number)
/* 605*/          return this.value.equals(tok2.value); 
/* 607*/        return (this.value == tok2.value);
/*   0*/      } 
/* 610*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 622*/      return this.value.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 631*/      return StringUtils.repeat(this.value.toString(), this.count);
/*   0*/    }
/*   0*/  }
/*   0*/}
