/*   0*/package org.apache.commons.lang3;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.InvocationTargetException;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Locale;
/*   0*/import java.util.regex.Pattern;
/*   0*/
/*   0*/public class StringUtils {
/*   0*/  public static final String EMPTY = "";
/*   0*/  
/*   0*/  public static final int INDEX_NOT_FOUND = -1;
/*   0*/  
/*   0*/  private static final int PAD_LIMIT = 8192;
/*   0*/  
/*   0*/  public static boolean isEmpty(CharSequence cs) {
/* 194*/    return (cs == null || cs.length() == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNotEmpty(CharSequence cs) {
/* 212*/    return !isEmpty(cs);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBlank(CharSequence cs) {
/*   0*/    int strLen;
/* 232*/    if (cs == null || (strLen = cs.length()) == 0)
/* 233*/      return true; 
/* 235*/    for (int i = 0; i < strLen; i++) {
/* 236*/      if (!Character.isWhitespace(cs.charAt(i)))
/* 237*/        return false; 
/*   0*/    } 
/* 240*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNotBlank(CharSequence cs) {
/* 260*/    return !isBlank(cs);
/*   0*/  }
/*   0*/  
/*   0*/  public static String trim(String str) {
/* 289*/    return (str == null) ? null : str.trim();
/*   0*/  }
/*   0*/  
/*   0*/  public static String trimToNull(String str) {
/* 315*/    String ts = trim(str);
/* 316*/    return isEmpty(ts) ? null : ts;
/*   0*/  }
/*   0*/  
/*   0*/  public static String trimToEmpty(String str) {
/* 341*/    return (str == null) ? "" : str.trim();
/*   0*/  }
/*   0*/  
/*   0*/  public static String strip(String str) {
/* 369*/    return strip(str, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripToNull(String str) {
/* 396*/    if (str == null)
/* 397*/      return null; 
/* 399*/    str = strip(str, null);
/* 400*/    return (str.length() == 0) ? null : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripToEmpty(String str) {
/* 426*/    return (str == null) ? "" : strip(str, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String strip(String str, String stripChars) {
/* 456*/    if (isEmpty(str))
/* 457*/      return str; 
/* 459*/    str = stripStart(str, stripChars);
/* 460*/    return stripEnd(str, stripChars);
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripStart(String str, String stripChars) {
/*   0*/    int strLen;
/* 489*/    if (str == null || (strLen = str.length()) == 0)
/* 490*/      return str; 
/* 492*/    int start = 0;
/* 493*/    if (stripChars == null) {
/* 494*/      while (start != strLen && Character.isWhitespace(str.charAt(start)))
/* 495*/        start++; 
/*   0*/    } else {
/* 497*/      if (stripChars.length() == 0)
/* 498*/        return str; 
/* 500*/      while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1)
/* 501*/        start++; 
/*   0*/    } 
/* 504*/    return str.substring(start);
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripEnd(String str, String stripChars) {
/*   0*/    int end;
/* 533*/    if (str == null || (end = str.length()) == 0)
/* 534*/      return str; 
/* 537*/    if (stripChars == null) {
/* 538*/      while (end != 0 && Character.isWhitespace(str.charAt(end - 1)))
/* 539*/        end--; 
/*   0*/    } else {
/* 541*/      if (stripChars.length() == 0)
/* 542*/        return str; 
/* 544*/      while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1)
/* 545*/        end--; 
/*   0*/    } 
/* 548*/    return str.substring(0, end);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] stripAll(String[] strs) {
/* 573*/    return stripAll(strs, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] stripAll(String[] strs, String stripChars) {
/*   0*/    int strsLen;
/* 603*/    if (strs == null || (strsLen = strs.length) == 0)
/* 604*/      return strs; 
/* 606*/    String[] newArr = new String[strsLen];
/* 607*/    for (int i = 0; i < strsLen; i++)
/* 608*/      newArr[i] = strip(strs[i], stripChars); 
/* 610*/    return newArr;
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripAccents(String input) {
/* 630*/    if (input == null)
/* 631*/      return null; 
/* 633*/    if (SystemUtils.isJavaVersionAtLeast(1.6F))
/*   0*/      try {
/* 640*/        Class<?> normalizerFormClass = ClassUtils.getClass("java.text.Normalizer$Form", false);
/* 643*/        Class<?> normalizerClass = ClassUtils.getClass("java.text.Normalizer", false);
/* 646*/        Method method = normalizerClass.getMethod("normalize", new Class<?>[] { CharSequence.class, normalizerFormClass });
/* 649*/        Field nfd = normalizerFormClass.getField("NFD");
/* 652*/        String decomposed = (String)method.invoke(null, new Object[] { input, nfd.get(null) });
/* 655*/        Pattern accentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
/* 656*/        return accentPattern.matcher(decomposed).replaceAll("");
/* 657*/      } catch (ClassNotFoundException cnfe) {
/* 658*/        throw new RuntimeException("ClassNotFoundException occurred during 1.6 backcompat code", cnfe);
/* 659*/      } catch (NoSuchMethodException nsme) {
/* 660*/        throw new RuntimeException("NoSuchMethodException occurred during 1.6 backcompat code", nsme);
/* 661*/      } catch (NoSuchFieldException nsfe) {
/* 662*/        throw new RuntimeException("NoSuchFieldException occurred during 1.6 backcompat code", nsfe);
/* 663*/      } catch (IllegalAccessException iae) {
/* 664*/        throw new RuntimeException("IllegalAccessException occurred during 1.6 backcompat code", iae);
/* 665*/      } catch (IllegalArgumentException iae) {
/* 666*/        throw new RuntimeException("IllegalArgumentException occurred during 1.6 backcompat code", iae);
/* 667*/      } catch (InvocationTargetException ite) {
/* 668*/        throw new RuntimeException("InvocationTargetException occurred during 1.6 backcompat code", ite);
/* 669*/      } catch (SecurityException se) {
/* 670*/        throw new RuntimeException("SecurityException occurred during 1.6 backcompat code", se);
/*   0*/      }  
/* 673*/    throw new UnsupportedOperationException("The stripAccents(String) method is not supported until Java 1.6");
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(CharSequence cs1, CharSequence cs2) {
/* 700*/    return (cs1 == null) ? ((cs2 == null)) : cs1.equals(cs2);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equalsIgnoreCase(String str1, String str2) {
/* 725*/    return (str1 == null) ? ((str2 == null)) : str1.equalsIgnoreCase(str2);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, char searchChar) {
/* 750*/    if (isEmpty(str))
/* 751*/      return -1; 
/* 753*/    return str.indexOf(searchChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, char searchChar, int startPos) {
/* 782*/    if (isEmpty(str))
/* 783*/      return -1; 
/* 785*/    return str.indexOf(searchChar, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, String searchStr) {
/* 812*/    if (str == null || searchStr == null)
/* 813*/      return -1; 
/* 815*/    return str.indexOf(searchStr);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, String searchStr, int startPos) {
/* 851*/    if (str == null || searchStr == null)
/* 852*/      return -1; 
/* 854*/    return str.indexOf(searchStr, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
/* 891*/    return ordinalIndexOf(str, searchStr, ordinal, false);
/*   0*/  }
/*   0*/  
/*   0*/  private static int ordinalIndexOf(String str, String searchStr, int ordinal, boolean lastIndex) {
/* 909*/    if (str == null || searchStr == null || ordinal <= 0)
/* 910*/      return -1; 
/* 912*/    if (searchStr.length() == 0)
/* 913*/      return lastIndex ? str.length() : 0; 
/* 915*/    int found = 0;
/* 916*/    int index = lastIndex ? str.length() : -1;
/*   0*/    while (true) {
/* 918*/      if (lastIndex) {
/* 919*/        index = str.lastIndexOf(searchStr, index - 1);
/*   0*/      } else {
/* 921*/        index = str.indexOf(searchStr, index + 1);
/*   0*/      } 
/* 923*/      if (index < 0)
/* 924*/        return index; 
/* 926*/      found++;
/* 927*/      if (found >= ordinal)
/* 928*/        return index; 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfIgnoreCase(String str, String searchStr) {
/* 956*/    return indexOfIgnoreCase(str, searchStr, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfIgnoreCase(String str, String searchStr, int startPos) {
/* 991*/    if (str == null || searchStr == null)
/* 992*/      return -1; 
/* 994*/    if (startPos < 0)
/* 995*/      startPos = 0; 
/* 997*/    int endLimit = str.length() - searchStr.length() + 1;
/* 998*/    if (startPos > endLimit)
/* 999*/      return -1; 
/*1001*/    if (searchStr.length() == 0)
/*1002*/      return startPos; 
/*1004*/    for (int i = startPos; i < endLimit; i++) {
/*1005*/      if (str.regionMatches(true, i, searchStr, 0, searchStr.length()))
/*1006*/        return i; 
/*   0*/    } 
/*1009*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, char searchChar) {
/*1034*/    if (isEmpty(str))
/*1035*/      return -1; 
/*1037*/    return str.lastIndexOf(searchChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, char searchChar, int startPos) {
/*1068*/    if (isEmpty(str))
/*1069*/      return -1; 
/*1071*/    return str.lastIndexOf(searchChar, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, String searchStr) {
/*1097*/    if (str == null || searchStr == null)
/*1098*/      return -1; 
/*1100*/    return str.lastIndexOf(searchStr);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastOrdinalIndexOf(String str, String searchStr, int ordinal) {
/*1137*/    return ordinalIndexOf(str, searchStr, ordinal, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, String searchStr, int startPos) {
/*1169*/    if (str == null || searchStr == null)
/*1170*/      return -1; 
/*1172*/    return str.lastIndexOf(searchStr, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOfIgnoreCase(String str, String searchStr) {
/*1198*/    if (str == null || searchStr == null)
/*1199*/      return -1; 
/*1201*/    return lastIndexOfIgnoreCase(str, searchStr, str.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOfIgnoreCase(String str, String searchStr, int startPos) {
/*1233*/    if (str == null || searchStr == null)
/*1234*/      return -1; 
/*1236*/    if (startPos > str.length() - searchStr.length())
/*1237*/      startPos = str.length() - searchStr.length(); 
/*1239*/    if (startPos < 0)
/*1240*/      return -1; 
/*1242*/    if (searchStr.length() == 0)
/*1243*/      return startPos; 
/*1246*/    for (int i = startPos; i >= 0; i--) {
/*1247*/      if (str.regionMatches(true, i, searchStr, 0, searchStr.length()))
/*1248*/        return i; 
/*   0*/    } 
/*1251*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean contains(String str, char searchChar) {
/*1276*/    if (isEmpty(str))
/*1277*/      return false; 
/*1279*/    return (str.indexOf(searchChar) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean contains(String str, String searchStr) {
/*1304*/    if (str == null || searchStr == null)
/*1305*/      return false; 
/*1307*/    return (str.indexOf(searchStr) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsIgnoreCase(String str, String searchStr) {
/*1334*/    if (str == null || searchStr == null)
/*1335*/      return false; 
/*1337*/    int len = searchStr.length();
/*1338*/    int max = str.length() - len;
/*1339*/    for (int i = 0; i <= max; i++) {
/*1340*/      if (str.regionMatches(true, i, searchStr, 0, len))
/*1341*/        return true; 
/*   0*/    } 
/*1344*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAny(CharSequence cs, char[] searchChars) {
/*1372*/    if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars))
/*1373*/      return -1; 
/*1375*/    for (int i = 0; i < cs.length(); i++) {
/*1376*/      char ch = cs.charAt(i);
/*1377*/      for (int j = 0; j < searchChars.length; j++) {
/*1378*/        if (searchChars[j] == ch)
/*1379*/          return i; 
/*   0*/      } 
/*   0*/    } 
/*1383*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAny(CharSequence cs, String searchChars) {
/*1409*/    if (isEmpty(cs) || isEmpty(searchChars))
/*1410*/      return -1; 
/*1412*/    return indexOfAny(cs, searchChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsAny(CharSequence cs, char[] searchChars) {
/*1441*/    if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars))
/*1442*/      return false; 
/*1444*/    int csLength = cs.length();
/*1445*/    int searchLength = searchChars.length;
/*1446*/    for (int i = 1; i < csLength; i++) {
/*1447*/      char ch = cs.charAt(i);
/*1448*/      for (int j = 0; j < searchLength; j++) {
/*1449*/        if (searchChars[j] == ch)
/*1452*/          return true; 
/*   0*/      } 
/*   0*/    } 
/*1456*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsAny(CharSequence cs, String searchChars) {
/*1487*/    if (searchChars == null)
/*1488*/      return false; 
/*1490*/    return containsAny(cs, searchChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAnyBut(CharSequence cs, char[] searchChars) {
/*1518*/    if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars))
/*1519*/      return -1; 
/*1521*/    for (int i = 0; i < cs.length(); i++) {
/*1522*/      char ch = cs.charAt(i);
/*1523*/      int j = 0;
/*   0*/      while (true) {
/*1523*/        if (j < searchChars.length) {
/*1524*/          if (searchChars[j] == ch)
/*   0*/            break; 
/*   0*/          j++;
/*   0*/          continue;
/*   0*/        } 
/*1528*/        return i;
/*   0*/      } 
/*   0*/    } 
/*1530*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAnyBut(String str, String searchChars) {
/*1556*/    if (isEmpty(str) || isEmpty(searchChars))
/*1557*/      return -1; 
/*1559*/    for (int i = 0; i < str.length(); i++) {
/*1560*/      if (searchChars.indexOf(str.charAt(i)) < 0)
/*1561*/        return i; 
/*   0*/    } 
/*1564*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsOnly(CharSequence cs, char[] valid) {
/*1592*/    if (valid == null || cs == null)
/*1593*/      return false; 
/*1595*/    if (cs.length() == 0)
/*1596*/      return true; 
/*1598*/    if (valid.length == 0)
/*1599*/      return false; 
/*1601*/    return (indexOfAnyBut(cs, valid) == -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsOnly(CharSequence cs, String validChars) {
/*1627*/    if (cs == null || validChars == null)
/*1628*/      return false; 
/*1630*/    return containsOnly(cs, validChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsNone(CharSequence cs, char[] invalidChars) {
/*1658*/    if (cs == null || invalidChars == null)
/*1659*/      return true; 
/*1661*/    int strSize = cs.length();
/*1662*/    int validSize = invalidChars.length;
/*1663*/    for (int i = 0; i < strSize; i++) {
/*1664*/      char ch = cs.charAt(i);
/*1665*/      for (int j = 0; j < validSize; j++) {
/*1666*/        if (invalidChars[j] == ch)
/*1667*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1671*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsNone(CharSequence cs, String invalidChars) {
/*1697*/    if (cs == null || invalidChars == null)
/*1698*/      return true; 
/*1700*/    return containsNone(cs, invalidChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAny(String str, String[] searchStrs) {
/*1732*/    if (str == null || searchStrs == null)
/*1733*/      return -1; 
/*1735*/    int sz = searchStrs.length;
/*1738*/    int ret = Integer.MAX_VALUE;
/*1740*/    int tmp = 0;
/*1741*/    for (int i = 0; i < sz; i++) {
/*1742*/      String search = searchStrs[i];
/*1743*/      if (search != null) {
/*1746*/        tmp = str.indexOf(search);
/*1747*/        if (tmp != -1)
/*1751*/          if (tmp < ret)
/*1752*/            ret = tmp;  
/*   0*/      } 
/*   0*/    } 
/*1756*/    return (ret == Integer.MAX_VALUE) ? -1 : ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOfAny(String str, String[] searchStrs) {
/*1785*/    if (str == null || searchStrs == null)
/*1786*/      return -1; 
/*1788*/    int sz = searchStrs.length;
/*1789*/    int ret = -1;
/*1790*/    int tmp = 0;
/*1791*/    for (int i = 0; i < sz; i++) {
/*1792*/      String search = searchStrs[i];
/*1793*/      if (search != null) {
/*1796*/        tmp = str.lastIndexOf(search);
/*1797*/        if (tmp > ret)
/*1798*/          ret = tmp; 
/*   0*/      } 
/*   0*/    } 
/*1801*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static String substring(String str, int start) {
/*1831*/    if (str == null)
/*1832*/      return null; 
/*1836*/    if (start < 0)
/*1837*/      start = str.length() + start; 
/*1840*/    if (start < 0)
/*1841*/      start = 0; 
/*1843*/    if (start > str.length())
/*1844*/      return ""; 
/*1847*/    return str.substring(start);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substring(String str, int start, int end) {
/*1886*/    if (str == null)
/*1887*/      return null; 
/*1891*/    if (end < 0)
/*1892*/      end = str.length() + end; 
/*1894*/    if (start < 0)
/*1895*/      start = str.length() + start; 
/*1899*/    if (end > str.length())
/*1900*/      end = str.length(); 
/*1904*/    if (start > end)
/*1905*/      return ""; 
/*1908*/    if (start < 0)
/*1909*/      start = 0; 
/*1911*/    if (end < 0)
/*1912*/      end = 0; 
/*1915*/    return str.substring(start, end);
/*   0*/  }
/*   0*/  
/*   0*/  public static String left(String str, int len) {
/*1941*/    if (str == null)
/*1942*/      return null; 
/*1944*/    if (len < 0)
/*1945*/      return ""; 
/*1947*/    if (str.length() <= len)
/*1948*/      return str; 
/*1950*/    return str.substring(0, len);
/*   0*/  }
/*   0*/  
/*   0*/  public static String right(String str, int len) {
/*1974*/    if (str == null)
/*1975*/      return null; 
/*1977*/    if (len < 0)
/*1978*/      return ""; 
/*1980*/    if (str.length() <= len)
/*1981*/      return str; 
/*1983*/    return str.substring(str.length() - len);
/*   0*/  }
/*   0*/  
/*   0*/  public static String mid(String str, int pos, int len) {
/*2011*/    if (str == null)
/*2012*/      return null; 
/*2014*/    if (len < 0 || pos > str.length())
/*2015*/      return ""; 
/*2017*/    if (pos < 0)
/*2018*/      pos = 0; 
/*2020*/    if (str.length() <= pos + len)
/*2021*/      return str.substring(pos); 
/*2023*/    return str.substring(pos, pos + len);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBefore(String str, String separator) {
/*2056*/    if (isEmpty(str) || separator == null)
/*2057*/      return str; 
/*2059*/    if (separator.length() == 0)
/*2060*/      return ""; 
/*2062*/    int pos = str.indexOf(separator);
/*2063*/    if (pos == -1)
/*2064*/      return str; 
/*2066*/    return str.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringAfter(String str, String separator) {
/*2098*/    if (isEmpty(str))
/*2099*/      return str; 
/*2101*/    if (separator == null)
/*2102*/      return ""; 
/*2104*/    int pos = str.indexOf(separator);
/*2105*/    if (pos == -1)
/*2106*/      return ""; 
/*2108*/    return str.substring(pos + separator.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBeforeLast(String str, String separator) {
/*2139*/    if (isEmpty(str) || isEmpty(separator))
/*2140*/      return str; 
/*2142*/    int pos = str.lastIndexOf(separator);
/*2143*/    if (pos == -1)
/*2144*/      return str; 
/*2146*/    return str.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringAfterLast(String str, String separator) {
/*2179*/    if (isEmpty(str))
/*2180*/      return str; 
/*2182*/    if (isEmpty(separator))
/*2183*/      return ""; 
/*2185*/    int pos = str.lastIndexOf(separator);
/*2186*/    if (pos == -1 || pos == str.length() - separator.length())
/*2187*/      return ""; 
/*2189*/    return str.substring(pos + separator.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBetween(String str, String tag) {
/*2216*/    return substringBetween(str, tag, tag);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBetween(String str, String open, String close) {
/*2247*/    if (str == null || open == null || close == null)
/*2248*/      return null; 
/*2250*/    int start = str.indexOf(open);
/*2251*/    if (start != -1) {
/*2252*/      int end = str.indexOf(close, start + open.length());
/*2253*/      if (end != -1)
/*2254*/        return str.substring(start + open.length(), end); 
/*   0*/    } 
/*2257*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] substringsBetween(String str, String open, String close) {
/*2283*/    if (str == null || isEmpty(open) || isEmpty(close))
/*2284*/      return null; 
/*2286*/    int strLen = str.length();
/*2287*/    if (strLen == 0)
/*2288*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2290*/    int closeLen = close.length();
/*2291*/    int openLen = open.length();
/*2292*/    List<String> list = new ArrayList<String>();
/*2293*/    int pos = 0;
/*2294*/    while (pos < strLen - closeLen) {
/*2295*/      int start = str.indexOf(open, pos);
/*2296*/      if (start < 0)
/*   0*/        break; 
/*2299*/      start += openLen;
/*2300*/      int end = str.indexOf(close, start);
/*2301*/      if (end < 0)
/*   0*/        break; 
/*2304*/      list.add(str.substring(start, end));
/*2305*/      pos = end + closeLen;
/*   0*/    } 
/*2307*/    if (list.isEmpty())
/*2308*/      return null; 
/*2310*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str) {
/*2341*/    return split(str, null, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str, char separatorChar) {
/*2369*/    return splitWorker(str, separatorChar, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str, String separatorChars) {
/*2398*/    return splitWorker(str, separatorChars, -1, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str, String separatorChars, int max) {
/*2432*/    return splitWorker(str, separatorChars, max, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparator(String str, String separator) {
/*2459*/    return splitByWholeSeparatorWorker(str, separator, -1, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparator(String str, String separator, int max) {
/*2490*/    return splitByWholeSeparatorWorker(str, separator, max, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
/*2519*/    return splitByWholeSeparatorWorker(str, separator, -1, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
/*2552*/    return splitByWholeSeparatorWorker(str, separator, max, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
/*2572*/    if (str == null)
/*2573*/      return null; 
/*2576*/    int len = str.length();
/*2578*/    if (len == 0)
/*2579*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2582*/    if (separator == null || "".equals(separator))
/*2584*/      return splitWorker(str, null, max, preserveAllTokens); 
/*2587*/    int separatorLength = separator.length();
/*2589*/    ArrayList<String> substrings = new ArrayList<String>();
/*2590*/    int numberOfSubstrings = 0;
/*2591*/    int beg = 0;
/*2592*/    int end = 0;
/*2593*/    while (end < len) {
/*2594*/      end = str.indexOf(separator, beg);
/*2596*/      if (end > -1) {
/*2597*/        if (end > beg) {
/*2598*/          numberOfSubstrings++;
/*2600*/          if (numberOfSubstrings == max) {
/*2601*/            end = len;
/*2602*/            substrings.add(str.substring(beg));
/*   0*/            continue;
/*   0*/          } 
/*2606*/          substrings.add(str.substring(beg, end));
/*2611*/          beg = end + separatorLength;
/*   0*/          continue;
/*   0*/        } 
/*2615*/        if (preserveAllTokens) {
/*2616*/          numberOfSubstrings++;
/*2617*/          if (numberOfSubstrings == max) {
/*2618*/            end = len;
/*2619*/            substrings.add(str.substring(beg));
/*   0*/          } else {
/*2621*/            substrings.add("");
/*   0*/          } 
/*   0*/        } 
/*2624*/        beg = end + separatorLength;
/*   0*/        continue;
/*   0*/      } 
/*2628*/      substrings.add(str.substring(beg));
/*2629*/      end = len;
/*   0*/    } 
/*2633*/    return substrings.<String>toArray(new String[substrings.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str) {
/*2662*/    return splitWorker(str, null, -1, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str, char separatorChar) {
/*2698*/    return splitWorker(str, separatorChar, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
/*2716*/    if (str == null)
/*2717*/      return null; 
/*2719*/    int len = str.length();
/*2720*/    if (len == 0)
/*2721*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2723*/    List<String> list = new ArrayList<String>();
/*2724*/    int i = 0, start = 0;
/*   0*/    boolean match = false;
/*   0*/    boolean lastMatch = false;
/*2727*/    while (i < len) {
/*2728*/      if (str.charAt(i) == separatorChar) {
/*2729*/        if (match || preserveAllTokens) {
/*2730*/          list.add(str.substring(start, i));
/*2731*/          match = false;
/*2732*/          lastMatch = true;
/*   0*/        } 
/*2734*/        start = ++i;
/*   0*/        continue;
/*   0*/      } 
/*2737*/      lastMatch = false;
/*2738*/      match = true;
/*2739*/      i++;
/*   0*/    } 
/*2741*/    if (match || (preserveAllTokens && lastMatch))
/*2742*/      list.add(str.substring(start, i)); 
/*2744*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str, String separatorChars) {
/*2781*/    return splitWorker(str, separatorChars, -1, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
/*2821*/    return splitWorker(str, separatorChars, max, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
/*2843*/    if (str == null)
/*2844*/      return null; 
/*2846*/    int len = str.length();
/*2847*/    if (len == 0)
/*2848*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2850*/    List<String> list = new ArrayList<String>();
/*2851*/    int sizePlus1 = 1;
/*2852*/    int i = 0, start = 0;
/*   0*/    boolean match = false;
/*   0*/    boolean lastMatch = false;
/*2855*/    if (separatorChars == null) {
/*2857*/      while (i < len) {
/*2858*/        if (Character.isWhitespace(str.charAt(i))) {
/*2859*/          if (match || preserveAllTokens) {
/*2860*/            lastMatch = true;
/*2861*/            if (sizePlus1++ == max) {
/*2862*/              i = len;
/*2863*/              lastMatch = false;
/*   0*/            } 
/*2865*/            list.add(str.substring(start, i));
/*2866*/            match = false;
/*   0*/          } 
/*2868*/          start = ++i;
/*   0*/          continue;
/*   0*/        } 
/*2871*/        lastMatch = false;
/*2872*/        match = true;
/*2873*/        i++;
/*   0*/      } 
/*2875*/    } else if (separatorChars.length() == 1) {
/*2877*/      char sep = separatorChars.charAt(0);
/*2878*/      while (i < len) {
/*2879*/        if (str.charAt(i) == sep) {
/*2880*/          if (match || preserveAllTokens) {
/*2881*/            lastMatch = true;
/*2882*/            if (sizePlus1++ == max) {
/*2883*/              i = len;
/*2884*/              lastMatch = false;
/*   0*/            } 
/*2886*/            list.add(str.substring(start, i));
/*2887*/            match = false;
/*   0*/          } 
/*2889*/          start = ++i;
/*   0*/          continue;
/*   0*/        } 
/*2892*/        lastMatch = false;
/*2893*/        match = true;
/*2894*/        i++;
/*   0*/      } 
/*   0*/    } else {
/*2898*/      while (i < len) {
/*2899*/        if (separatorChars.indexOf(str.charAt(i)) >= 0) {
/*2900*/          if (match || preserveAllTokens) {
/*2901*/            lastMatch = true;
/*2902*/            if (sizePlus1++ == max) {
/*2903*/              i = len;
/*2904*/              lastMatch = false;
/*   0*/            } 
/*2906*/            list.add(str.substring(start, i));
/*2907*/            match = false;
/*   0*/          } 
/*2909*/          start = ++i;
/*   0*/          continue;
/*   0*/        } 
/*2912*/        lastMatch = false;
/*2913*/        match = true;
/*2914*/        i++;
/*   0*/      } 
/*   0*/    } 
/*2917*/    if (match || (preserveAllTokens && lastMatch))
/*2918*/      list.add(str.substring(start, i)); 
/*2920*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByCharacterType(String str) {
/*2943*/    return splitByCharacterType(str, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByCharacterTypeCamelCase(String str) {
/*2971*/    return splitByCharacterType(str, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitByCharacterType(String str, boolean camelCase) {
/*2989*/    if (str == null)
/*2990*/      return null; 
/*2992*/    if (str.length() == 0)
/*2993*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2995*/    char[] c = str.toCharArray();
/*2996*/    List<String> list = new ArrayList<String>();
/*2997*/    int tokenStart = 0;
/*2998*/    int currentType = Character.getType(c[tokenStart]);
/*2999*/    for (int pos = tokenStart + 1; pos < c.length; pos++) {
/*3000*/      int type = Character.getType(c[pos]);
/*3001*/      if (type != currentType) {
/*3004*/        if (camelCase && type == 2 && currentType == 1) {
/*3005*/          int newTokenStart = pos - 1;
/*3006*/          if (newTokenStart != tokenStart) {
/*3007*/            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
/*3008*/            tokenStart = newTokenStart;
/*   0*/          } 
/*   0*/        } else {
/*3011*/          list.add(new String(c, tokenStart, pos - tokenStart));
/*3012*/          tokenStart = pos;
/*   0*/        } 
/*3014*/        currentType = type;
/*   0*/      } 
/*   0*/    } 
/*3016*/    list.add(new String(c, tokenStart, c.length - tokenStart));
/*3017*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array) {
/*3043*/    return join(array, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, char separator) {
/*3069*/    if (array == null)
/*3070*/      return null; 
/*3073*/    return join(array, separator, 0, array.length);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, char separator, int startIndex, int endIndex) {
/*3103*/    if (array == null)
/*3104*/      return null; 
/*3106*/    int bufSize = endIndex - startIndex;
/*3107*/    if (bufSize <= 0)
/*3108*/      return ""; 
/*3111*/    bufSize *= ((array[startIndex] == null) ? 16 : array[startIndex].toString().length()) + 1;
/*3112*/    StringBuilder buf = new StringBuilder(bufSize);
/*3114*/    for (int i = startIndex; i < endIndex; i++) {
/*3115*/      if (i > startIndex)
/*3116*/        buf.append(separator); 
/*3118*/      if (array[i] != null)
/*3119*/        buf.append(array[i]); 
/*   0*/    } 
/*3122*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, String separator) {
/*3150*/    if (array == null)
/*3151*/      return null; 
/*3153*/    return join(array, separator, 0, array.length);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, String separator, int startIndex, int endIndex) {
/*3184*/    if (array == null)
/*3185*/      return null; 
/*3187*/    if (separator == null)
/*3188*/      separator = ""; 
/*3193*/    int bufSize = endIndex - startIndex;
/*3194*/    if (bufSize <= 0)
/*3195*/      return ""; 
/*3198*/    bufSize *= ((array[startIndex] == null) ? 16 : array[startIndex].toString().length()) + separator.length();
/*3201*/    StringBuilder buf = new StringBuilder(bufSize);
/*3203*/    for (int i = startIndex; i < endIndex; i++) {
/*3204*/      if (i > startIndex)
/*3205*/        buf.append(separator); 
/*3207*/      if (array[i] != null)
/*3208*/        buf.append(array[i]); 
/*   0*/    } 
/*3211*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterator<?> iterator, char separator) {
/*3231*/    if (iterator == null)
/*3232*/      return null; 
/*3234*/    if (!iterator.hasNext())
/*3235*/      return ""; 
/*3237*/    Object first = iterator.next();
/*3238*/    if (!iterator.hasNext())
/*3239*/      return ObjectUtils.toString(first); 
/*3243*/    StringBuilder buf = new StringBuilder(256);
/*3244*/    if (first != null)
/*3245*/      buf.append(first); 
/*3248*/    while (iterator.hasNext()) {
/*3249*/      buf.append(separator);
/*3250*/      Object obj = iterator.next();
/*3251*/      if (obj != null)
/*3252*/        buf.append(obj); 
/*   0*/    } 
/*3256*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterator<?> iterator, String separator) {
/*3275*/    if (iterator == null)
/*3276*/      return null; 
/*3278*/    if (!iterator.hasNext())
/*3279*/      return ""; 
/*3281*/    Object first = iterator.next();
/*3282*/    if (!iterator.hasNext())
/*3283*/      return ObjectUtils.toString(first); 
/*3287*/    StringBuilder buf = new StringBuilder(256);
/*3288*/    if (first != null)
/*3289*/      buf.append(first); 
/*3292*/    while (iterator.hasNext()) {
/*3293*/      if (separator != null)
/*3294*/        buf.append(separator); 
/*3296*/      Object obj = iterator.next();
/*3297*/      if (obj != null)
/*3298*/        buf.append(obj); 
/*   0*/    } 
/*3301*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterable<?> iterable, char separator) {
/*3319*/    if (iterable == null)
/*3320*/      return null; 
/*3322*/    return join(iterable.iterator(), separator);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterable<?> iterable, String separator) {
/*3340*/    if (iterable == null)
/*3341*/      return null; 
/*3343*/    return join(iterable.iterator(), separator);
/*   0*/  }
/*   0*/  
/*   0*/  public static String deleteWhitespace(String str) {
/*3363*/    if (isEmpty(str))
/*3364*/      return str; 
/*3366*/    int sz = str.length();
/*3367*/    char[] chs = new char[sz];
/*3368*/    int count = 0;
/*3369*/    for (int i = 0; i < sz; i++) {
/*3370*/      if (!Character.isWhitespace(str.charAt(i)))
/*3371*/        chs[count++] = str.charAt(i); 
/*   0*/    } 
/*3374*/    if (count == sz)
/*3375*/      return str; 
/*3377*/    return new String(chs, 0, count);
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeStart(String str, String remove) {
/*3407*/    if (isEmpty(str) || isEmpty(remove))
/*3408*/      return str; 
/*3410*/    if (str.startsWith(remove))
/*3411*/      return str.substring(remove.length()); 
/*3413*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeStartIgnoreCase(String str, String remove) {
/*3442*/    if (isEmpty(str) || isEmpty(remove))
/*3443*/      return str; 
/*3445*/    if (startsWithIgnoreCase(str, remove))
/*3446*/      return str.substring(remove.length()); 
/*3448*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeEnd(String str, String remove) {
/*3476*/    if (isEmpty(str) || isEmpty(remove))
/*3477*/      return str; 
/*3479*/    if (str.endsWith(remove))
/*3480*/      return str.substring(0, str.length() - remove.length()); 
/*3482*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeEndIgnoreCase(String str, String remove) {
/*3512*/    if (isEmpty(str) || isEmpty(remove))
/*3513*/      return str; 
/*3515*/    if (endsWithIgnoreCase(str, remove))
/*3516*/      return str.substring(0, str.length() - remove.length()); 
/*3518*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String remove(String str, String remove) {
/*3545*/    if (isEmpty(str) || isEmpty(remove))
/*3546*/      return str; 
/*3548*/    return replace(str, remove, "", -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String remove(String str, char remove) {
/*3571*/    if (isEmpty(str) || str.indexOf(remove) == -1)
/*3572*/      return str; 
/*3574*/    char[] chars = str.toCharArray();
/*3575*/    int pos = 0;
/*3576*/    for (int i = 0; i < chars.length; i++) {
/*3577*/      if (chars[i] != remove)
/*3578*/        chars[pos++] = chars[i]; 
/*   0*/    } 
/*3581*/    return new String(chars, 0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceOnce(String text, String searchString, String replacement) {
/*3610*/    return replace(text, searchString, replacement, 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replace(String text, String searchString, String replacement) {
/*3637*/    return replace(text, searchString, replacement, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replace(String text, String searchString, String replacement, int max) {
/*3669*/    if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0)
/*3670*/      return text; 
/*3672*/    int start = 0;
/*3673*/    int end = text.indexOf(searchString, start);
/*3674*/    if (end == -1)
/*3675*/      return text; 
/*3677*/    int replLength = searchString.length();
/*3678*/    int increase = replacement.length() - replLength;
/*3679*/    increase = (increase < 0) ? 0 : increase;
/*3680*/    increase *= (max < 0) ? 16 : ((max > 64) ? 64 : max);
/*3681*/    StringBuilder buf = new StringBuilder(text.length() + increase);
/*3682*/    while (end != -1) {
/*3683*/      buf.append(text.substring(start, end)).append(replacement);
/*3684*/      start = end + replLength;
/*3685*/      if (--max == 0)
/*   0*/        break; 
/*3688*/      end = text.indexOf(searchString, start);
/*   0*/    } 
/*3690*/    buf.append(text.substring(start));
/*3691*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceEach(String text, String[] searchList, String[] replacementList) {
/*3734*/    return replaceEach(text, searchList, replacementList, false, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
/*3785*/    int timeToLive = (searchList == null) ? 0 : searchList.length;
/*3786*/    return replaceEach(text, searchList, replacementList, true, timeToLive);
/*   0*/  }
/*   0*/  
/*   0*/  private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
/*3844*/    if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0)
/*3847*/      return text; 
/*3851*/    if (timeToLive < 0)
/*3852*/      throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text); 
/*3855*/    int searchLength = searchList.length;
/*3856*/    int replacementLength = replacementList.length;
/*3859*/    if (searchLength != replacementLength)
/*3860*/      throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength); 
/*3867*/    boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
/*3870*/    int textIndex = -1;
/*3871*/    int replaceIndex = -1;
/*3872*/    int tempIndex = -1;
/*3876*/    for (int i = 0; i < searchLength; i++) {
/*3877*/      if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && searchList[i].length() != 0 && replacementList[i] != null) {
/*3882*/        tempIndex = text.indexOf(searchList[i]);
/*3885*/        if (tempIndex == -1) {
/*3886*/          noMoreMatchesForReplIndex[i] = true;
/*3888*/        } else if (textIndex == -1 || tempIndex < textIndex) {
/*3889*/          textIndex = tempIndex;
/*3890*/          replaceIndex = i;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3897*/    if (textIndex == -1)
/*3898*/      return text; 
/*3901*/    int start = 0;
/*3904*/    int increase = 0;
/*3907*/    for (int j = 0; j < searchList.length; j++) {
/*3908*/      if (searchList[j] != null && replacementList[j] != null) {
/*3911*/        int greater = replacementList[j].length() - searchList[j].length();
/*3912*/        if (greater > 0)
/*3913*/          increase += 3 * greater; 
/*   0*/      } 
/*   0*/    } 
/*3917*/    increase = Math.min(increase, text.length() / 5);
/*3919*/    StringBuilder buf = new StringBuilder(text.length() + increase);
/*3921*/    while (textIndex != -1) {
/*3923*/      for (int n = start; n < textIndex; n++)
/*3924*/        buf.append(text.charAt(n)); 
/*3926*/      buf.append(replacementList[replaceIndex]);
/*3928*/      start = textIndex + searchList[replaceIndex].length();
/*3930*/      textIndex = -1;
/*3931*/      replaceIndex = -1;
/*3932*/      tempIndex = -1;
/*3935*/      for (int m = 0; m < searchLength; m++) {
/*3936*/        if (!noMoreMatchesForReplIndex[m] && searchList[m] != null && searchList[m].length() != 0 && replacementList[m] != null) {
/*3941*/          tempIndex = text.indexOf(searchList[m], start);
/*3944*/          if (tempIndex == -1) {
/*3945*/            noMoreMatchesForReplIndex[m] = true;
/*3947*/          } else if (textIndex == -1 || tempIndex < textIndex) {
/*3948*/            textIndex = tempIndex;
/*3949*/            replaceIndex = m;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3956*/    int textLength = text.length();
/*3957*/    for (int k = start; k < textLength; k++)
/*3958*/      buf.append(text.charAt(k)); 
/*3960*/    String result = buf.toString();
/*3961*/    if (!repeat)
/*3962*/      return result; 
/*3965*/    return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceChars(String str, char searchChar, char replaceChar) {
/*3991*/    if (str == null)
/*3992*/      return null; 
/*3994*/    return str.replace(searchChar, replaceChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceChars(String str, String searchChars, String replaceChars) {
/*4034*/    if (isEmpty(str) || isEmpty(searchChars))
/*4035*/      return str; 
/*4037*/    if (replaceChars == null)
/*4038*/      replaceChars = ""; 
/*   0*/    boolean modified = false;
/*4041*/    int replaceCharsLength = replaceChars.length();
/*4042*/    int strLength = str.length();
/*4043*/    StringBuilder buf = new StringBuilder(strLength);
/*4044*/    for (int i = 0; i < strLength; i++) {
/*4045*/      char ch = str.charAt(i);
/*4046*/      int index = searchChars.indexOf(ch);
/*4047*/      if (index >= 0) {
/*4048*/        modified = true;
/*4049*/        if (index < replaceCharsLength)
/*4050*/          buf.append(replaceChars.charAt(index)); 
/*   0*/      } else {
/*4053*/        buf.append(ch);
/*   0*/      } 
/*   0*/    } 
/*4056*/    if (modified)
/*4057*/      return buf.toString(); 
/*4059*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String overlay(String str, String overlay, int start, int end) {
/*4094*/    if (str == null)
/*4095*/      return null; 
/*4097*/    if (overlay == null)
/*4098*/      overlay = ""; 
/*4100*/    int len = str.length();
/*4101*/    if (start < 0)
/*4102*/      start = 0; 
/*4104*/    if (start > len)
/*4105*/      start = len; 
/*4107*/    if (end < 0)
/*4108*/      end = 0; 
/*4110*/    if (end > len)
/*4111*/      end = len; 
/*4113*/    if (start > end) {
/*4114*/      int temp = start;
/*4115*/      start = end;
/*4116*/      end = temp;
/*   0*/    } 
/*4118*/    return new StringBuilder(len + start - end + overlay.length() + 1).append(str.substring(0, start)).append(overlay).append(str.substring(end)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String chomp(String str) {
/*4153*/    if (isEmpty(str))
/*4154*/      return str; 
/*4157*/    if (str.length() == 1) {
/*4158*/      char ch = str.charAt(0);
/*4159*/      if (ch == '\r' || ch == '\n')
/*4160*/        return ""; 
/*4162*/      return str;
/*   0*/    } 
/*4165*/    int lastIdx = str.length() - 1;
/*4166*/    char last = str.charAt(lastIdx);
/*4168*/    if (last == '\n') {
/*4169*/      if (str.charAt(lastIdx - 1) == '\r')
/*4170*/        lastIdx--; 
/*4172*/    } else if (last != '\r') {
/*4173*/      lastIdx++;
/*   0*/    } 
/*4175*/    return str.substring(0, lastIdx);
/*   0*/  }
/*   0*/  
/*   0*/  public static String chomp(String str, String separator) {
/*4205*/    if (isEmpty(str) || separator == null)
/*4206*/      return str; 
/*4208*/    if (str.endsWith(separator))
/*4209*/      return str.substring(0, str.length() - separator.length()); 
/*4211*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String chop(String str) {
/*4240*/    if (str == null)
/*4241*/      return null; 
/*4243*/    int strLen = str.length();
/*4244*/    if (strLen < 2)
/*4245*/      return ""; 
/*4247*/    int lastIdx = strLen - 1;
/*4248*/    String ret = str.substring(0, lastIdx);
/*4249*/    char last = str.charAt(lastIdx);
/*4250*/    if (last == '\n' && 
/*4251*/      ret.charAt(lastIdx - 1) == '\r')
/*4252*/      return ret.substring(0, lastIdx - 1); 
/*4255*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static String repeat(String str, int repeat) {
/*   0*/    char ch, output1[];
/*   0*/    int i;
/*   0*/    char ch0, ch1, output2[];
/*   0*/    int j;
/*4285*/    if (str == null)
/*4286*/      return null; 
/*4288*/    if (repeat <= 0)
/*4289*/      return ""; 
/*4291*/    int inputLength = str.length();
/*4292*/    if (repeat == 1 || inputLength == 0)
/*4293*/      return str; 
/*4295*/    if (inputLength == 1 && repeat <= 8192)
/*4296*/      return padding(repeat, str.charAt(0)); 
/*4299*/    int outputLength = inputLength * repeat;
/*4300*/    switch (inputLength) {
/*   0*/      case 1:
/*4302*/        ch = str.charAt(0);
/*4303*/        output1 = new char[outputLength];
/*4304*/        for (i = repeat - 1; i >= 0; i--)
/*4305*/          output1[i] = ch; 
/*4307*/        return new String(output1);
/*   0*/      case 2:
/*4309*/        ch0 = str.charAt(0);
/*4310*/        ch1 = str.charAt(1);
/*4311*/        output2 = new char[outputLength];
/*4312*/        for (j = repeat * 2 - 2; j >= 0; j--, j--) {
/*4313*/          output2[j] = ch0;
/*4314*/          output2[j + 1] = ch1;
/*   0*/        } 
/*4316*/        return new String(output2);
/*   0*/    } 
/*4318*/    StringBuilder buf = new StringBuilder(outputLength);
/*4319*/    for (int k = 0; k < repeat; k++)
/*4320*/      buf.append(str); 
/*4322*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String repeat(String str, String separator, int repeat) {
/*4346*/    if (str == null || separator == null)
/*4347*/      return repeat(str, repeat); 
/*4350*/    String result = repeat(str + separator, repeat);
/*4351*/    return removeEnd(result, separator);
/*   0*/  }
/*   0*/  
/*   0*/  private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
/*4379*/    if (repeat < 0)
/*4380*/      throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat); 
/*4382*/    char[] buf = new char[repeat];
/*4383*/    for (int i = 0; i < buf.length; i++)
/*4384*/      buf[i] = padChar; 
/*4386*/    return new String(buf);
/*   0*/  }
/*   0*/  
/*   0*/  public static String rightPad(String str, int size) {
/*4409*/    return rightPad(str, size, ' ');
/*   0*/  }
/*   0*/  
/*   0*/  public static String rightPad(String str, int size, char padChar) {
/*4434*/    if (str == null)
/*4435*/      return null; 
/*4437*/    int pads = size - str.length();
/*4438*/    if (pads <= 0)
/*4439*/      return str; 
/*4441*/    if (pads > 8192)
/*4442*/      return rightPad(str, size, String.valueOf(padChar)); 
/*4444*/    return str.concat(padding(pads, padChar));
/*   0*/  }
/*   0*/  
/*   0*/  public static String rightPad(String str, int size, String padStr) {
/*4471*/    if (str == null)
/*4472*/      return null; 
/*4474*/    if (isEmpty(padStr))
/*4475*/      padStr = " "; 
/*4477*/    int padLen = padStr.length();
/*4478*/    int strLen = str.length();
/*4479*/    int pads = size - strLen;
/*4480*/    if (pads <= 0)
/*4481*/      return str; 
/*4483*/    if (padLen == 1 && pads <= 8192)
/*4484*/      return rightPad(str, size, padStr.charAt(0)); 
/*4487*/    if (pads == padLen)
/*4488*/      return str.concat(padStr); 
/*4489*/    if (pads < padLen)
/*4490*/      return str.concat(padStr.substring(0, pads)); 
/*4492*/    char[] padding = new char[pads];
/*4493*/    char[] padChars = padStr.toCharArray();
/*4494*/    for (int i = 0; i < pads; i++)
/*4495*/      padding[i] = padChars[i % padLen]; 
/*4497*/    return str.concat(new String(padding));
/*   0*/  }
/*   0*/  
/*   0*/  public static String leftPad(String str, int size) {
/*4521*/    return leftPad(str, size, ' ');
/*   0*/  }
/*   0*/  
/*   0*/  public static String leftPad(String str, int size, char padChar) {
/*4546*/    if (str == null)
/*4547*/      return null; 
/*4549*/    int pads = size - str.length();
/*4550*/    if (pads <= 0)
/*4551*/      return str; 
/*4553*/    if (pads > 8192)
/*4554*/      return leftPad(str, size, String.valueOf(padChar)); 
/*4556*/    return padding(pads, padChar).concat(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static String leftPad(String str, int size, String padStr) {
/*4583*/    if (str == null)
/*4584*/      return null; 
/*4586*/    if (isEmpty(padStr))
/*4587*/      padStr = " "; 
/*4589*/    int padLen = padStr.length();
/*4590*/    int strLen = str.length();
/*4591*/    int pads = size - strLen;
/*4592*/    if (pads <= 0)
/*4593*/      return str; 
/*4595*/    if (padLen == 1 && pads <= 8192)
/*4596*/      return leftPad(str, size, padStr.charAt(0)); 
/*4599*/    if (pads == padLen)
/*4600*/      return padStr.concat(str); 
/*4601*/    if (pads < padLen)
/*4602*/      return padStr.substring(0, pads).concat(str); 
/*4604*/    char[] padding = new char[pads];
/*4605*/    char[] padChars = padStr.toCharArray();
/*4606*/    for (int i = 0; i < pads; i++)
/*4607*/      padding[i] = padChars[i % padLen]; 
/*4609*/    return new String(padding).concat(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static int length(CharSequence cs) {
/*4625*/    return CharSequenceUtils.length(cs);
/*   0*/  }
/*   0*/  
/*   0*/  public static String center(String str, int size) {
/*4654*/    return center(str, size, ' ');
/*   0*/  }
/*   0*/  
/*   0*/  public static String center(String str, int size, char padChar) {
/*4682*/    if (str == null || size <= 0)
/*4683*/      return str; 
/*4685*/    int strLen = str.length();
/*4686*/    int pads = size - strLen;
/*4687*/    if (pads <= 0)
/*4688*/      return str; 
/*4690*/    str = leftPad(str, strLen + pads / 2, padChar);
/*4691*/    str = rightPad(str, size, padChar);
/*4692*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String center(String str, int size, String padStr) {
/*4722*/    if (str == null || size <= 0)
/*4723*/      return str; 
/*4725*/    if (isEmpty(padStr))
/*4726*/      padStr = " "; 
/*4728*/    int strLen = str.length();
/*4729*/    int pads = size - strLen;
/*4730*/    if (pads <= 0)
/*4731*/      return str; 
/*4733*/    str = leftPad(str, strLen + pads / 2, padStr);
/*4734*/    str = rightPad(str, size, padStr);
/*4735*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String upperCase(String str) {
/*4760*/    if (str == null)
/*4761*/      return null; 
/*4763*/    return str.toUpperCase();
/*   0*/  }
/*   0*/  
/*   0*/  public static String upperCase(String str, Locale locale) {
/*4783*/    if (str == null)
/*4784*/      return null; 
/*4786*/    return str.toUpperCase(locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static String lowerCase(String str) {
/*4809*/    if (str == null)
/*4810*/      return null; 
/*4812*/    return str.toLowerCase();
/*   0*/  }
/*   0*/  
/*   0*/  public static String lowerCase(String str, Locale locale) {
/*4832*/    if (str == null)
/*4833*/      return null; 
/*4835*/    return str.toLowerCase(locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static String capitalize(CharSequence cs) {
/*4859*/    if (cs == null)
/*4860*/      return null; 
/*   0*/    int strLen;
/*4863*/    if ((strLen = cs.length()) == 0)
/*4864*/      return cs.toString(); 
/*4866*/    return new StringBuilder(strLen).append(Character.toTitleCase(cs.charAt(0))).append(CharSequenceUtils.subSequence(cs, 1)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String uncapitalize(CharSequence cs) {
/*4893*/    if (cs == null)
/*4894*/      return null; 
/*   0*/    int strLen;
/*4897*/    if ((strLen = cs.length()) == 0)
/*4898*/      return cs.toString(); 
/*4900*/    return new StringBuilder(strLen).append(Character.toLowerCase(cs.charAt(0))).append(CharSequenceUtils.subSequence(cs, 1)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String swapCase(String str) {
/*   0*/    int strLen;
/*4935*/    if (str == null || (strLen = str.length()) == 0)
/*4936*/      return str; 
/*4938*/    StringBuilder buffer = new StringBuilder(strLen);
/*4940*/    char ch = Character.MIN_VALUE;
/*4941*/    for (int i = 0; i < strLen; i++) {
/*4942*/      ch = str.charAt(i);
/*4943*/      if (Character.isUpperCase(ch)) {
/*4944*/        ch = Character.toLowerCase(ch);
/*4945*/      } else if (Character.isTitleCase(ch)) {
/*4946*/        ch = Character.toLowerCase(ch);
/*4947*/      } else if (Character.isLowerCase(ch)) {
/*4948*/        ch = Character.toUpperCase(ch);
/*   0*/      } 
/*4950*/      buffer.append(ch);
/*   0*/    } 
/*4952*/    return buffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static int countMatches(String str, String sub) {
/*4977*/    if (isEmpty(str) || isEmpty(sub))
/*4978*/      return 0; 
/*4980*/    int count = 0;
/*4981*/    int idx = 0;
/*4982*/    while ((idx = str.indexOf(sub, idx)) != -1) {
/*4983*/      count++;
/*4984*/      idx += sub.length();
/*   0*/    } 
/*4986*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlpha(CharSequence cs) {
/*5010*/    if (cs == null)
/*5011*/      return false; 
/*5013*/    int sz = cs.length();
/*5014*/    for (int i = 0; i < sz; i++) {
/*5015*/      if (!Character.isLetter(cs.charAt(i)))
/*5016*/        return false; 
/*   0*/    } 
/*5019*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlphaSpace(CharSequence cs) {
/*5044*/    if (cs == null)
/*5045*/      return false; 
/*5047*/    int sz = cs.length();
/*5048*/    for (int i = 0; i < sz; i++) {
/*5049*/      if (!Character.isLetter(cs.charAt(i)) && cs.charAt(i) != ' ')
/*5050*/        return false; 
/*   0*/    } 
/*5053*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlphanumeric(CharSequence cs) {
/*5077*/    if (cs == null)
/*5078*/      return false; 
/*5080*/    int sz = cs.length();
/*5081*/    for (int i = 0; i < sz; i++) {
/*5082*/      if (!Character.isLetterOrDigit(cs.charAt(i)))
/*5083*/        return false; 
/*   0*/    } 
/*5086*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlphanumericSpace(CharSequence cs) {
/*5111*/    if (cs == null)
/*5112*/      return false; 
/*5114*/    int sz = cs.length();
/*5115*/    for (int i = 0; i < sz; i++) {
/*5116*/      if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ')
/*5117*/        return false; 
/*   0*/    } 
/*5120*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAsciiPrintable(CharSequence cs) {
/*5149*/    if (cs == null)
/*5150*/      return false; 
/*5152*/    int sz = cs.length();
/*5153*/    for (int i = 0; i < sz; i++) {
/*5154*/      if (!CharUtils.isAsciiPrintable(cs.charAt(i)))
/*5155*/        return false; 
/*   0*/    } 
/*5158*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumeric(CharSequence cs) {
/*5183*/    if (cs == null)
/*5184*/      return false; 
/*5186*/    int sz = cs.length();
/*5187*/    for (int i = 0; i < sz; i++) {
/*5188*/      if (!Character.isDigit(cs.charAt(i)))
/*5189*/        return false; 
/*   0*/    } 
/*5192*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumericSpace(CharSequence cs) {
/*5219*/    if (cs == null)
/*5220*/      return false; 
/*5222*/    int sz = cs.length();
/*5223*/    for (int i = 0; i < sz; i++) {
/*5224*/      if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ')
/*5225*/        return false; 
/*   0*/    } 
/*5228*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isWhitespace(CharSequence cs) {
/*5251*/    if (cs == null)
/*5252*/      return false; 
/*5254*/    int sz = cs.length();
/*5255*/    for (int i = 0; i < sz; i++) {
/*5256*/      if (!Character.isWhitespace(cs.charAt(i)))
/*5257*/        return false; 
/*   0*/    } 
/*5260*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAllLowerCase(CharSequence cs) {
/*5282*/    if (cs == null || isEmpty(cs))
/*5283*/      return false; 
/*5285*/    int sz = cs.length();
/*5286*/    for (int i = 0; i < sz; i++) {
/*5287*/      if (!Character.isLowerCase(cs.charAt(i)))
/*5288*/        return false; 
/*   0*/    } 
/*5291*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAllUpperCase(CharSequence cs) {
/*5313*/    if (cs == null || isEmpty(cs))
/*5314*/      return false; 
/*5316*/    int sz = cs.length();
/*5317*/    for (int i = 0; i < sz; i++) {
/*5318*/      if (!Character.isUpperCase(cs.charAt(i)))
/*5319*/        return false; 
/*   0*/    } 
/*5322*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static String defaultString(String str) {
/*5344*/    return (str == null) ? "" : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String defaultString(String str, String defaultStr) {
/*5365*/    return (str == null) ? defaultStr : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static CharSequence defaultIfEmpty(CharSequence str, CharSequence defaultStr) {
/*5386*/    return isEmpty(str) ? defaultStr : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String reverse(String str) {
/*5406*/    if (str == null)
/*5407*/      return null; 
/*5409*/    return CharSequenceUtils.reverse(str).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String reverseDelimited(String str, char separatorChar) {
/*5432*/    if (str == null)
/*5433*/      return null; 
/*5437*/    String[] strs = split(str, separatorChar);
/*5438*/    ArrayUtils.reverse((Object[])strs);
/*5439*/    return join((Object[])strs, separatorChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static String abbreviate(String str, int maxWidth) {
/*5477*/    return abbreviate(str, 0, maxWidth);
/*   0*/  }
/*   0*/  
/*   0*/  public static String abbreviate(String str, int offset, int maxWidth) {
/*5516*/    if (str == null)
/*5517*/      return null; 
/*5519*/    if (maxWidth < 4)
/*5520*/      throw new IllegalArgumentException("Minimum abbreviation width is 4"); 
/*5522*/    if (str.length() <= maxWidth)
/*5523*/      return str; 
/*5525*/    if (offset > str.length())
/*5526*/      offset = str.length(); 
/*5528*/    if (str.length() - offset < maxWidth - 3)
/*5529*/      offset = str.length() - maxWidth - 3; 
/*5531*/    String abrevMarker = "...";
/*5532*/    if (offset <= 4)
/*5533*/      return str.substring(0, maxWidth - 3) + "..."; 
/*5535*/    if (maxWidth < 7)
/*5536*/      throw new IllegalArgumentException("Minimum abbreviation width with offset is 7"); 
/*5538*/    if (offset + maxWidth - 3 < str.length())
/*5539*/      return "..." + abbreviate(str.substring(offset), maxWidth - 3); 
/*5541*/    return "..." + str.substring(str.length() - maxWidth - 3);
/*   0*/  }
/*   0*/  
/*   0*/  public static String abbreviateMiddle(String str, String middle, int length) {
/*5574*/    if (isEmpty(str) || isEmpty(middle))
/*5575*/      return str; 
/*5578*/    if (length >= str.length() || length < middle.length() + 2)
/*5579*/      return str; 
/*5582*/    int targetSting = length - middle.length();
/*5583*/    int startOffset = targetSting / 2 + targetSting % 2;
/*5584*/    int endOffset = str.length() - targetSting / 2;
/*5586*/    StringBuilder builder = new StringBuilder(length);
/*5587*/    builder.append(str.substring(0, startOffset));
/*5588*/    builder.append(middle);
/*5589*/    builder.append(str.substring(endOffset));
/*5591*/    return builder.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String difference(String str1, String str2) {
/*5622*/    if (str1 == null)
/*5623*/      return str2; 
/*5625*/    if (str2 == null)
/*5626*/      return str1; 
/*5628*/    int at = indexOfDifference(str1, str2);
/*5629*/    if (at == -1)
/*5630*/      return ""; 
/*5632*/    return str2.substring(at);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
/*5659*/    if (cs1 == cs2)
/*5660*/      return -1; 
/*5662*/    if (cs1 == null || cs2 == null)
/*5663*/      return 0; 
/*   0*/    int i;
/*5666*/    for (i = 0; i < cs1.length() && i < cs2.length() && 
/*5667*/      cs1.charAt(i) == cs2.charAt(i); i++);
/*5671*/    if (i < cs2.length() || i < cs1.length())
/*5672*/      return i; 
/*5674*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfDifference(CharSequence[] css) {
/*5709*/    if (css == null || css.length <= 1)
/*5710*/      return -1; 
/*   0*/    boolean anyStringNull = false;
/*   0*/    boolean allStringsNull = true;
/*5714*/    int arrayLen = css.length;
/*5715*/    int shortestStrLen = Integer.MAX_VALUE;
/*5716*/    int longestStrLen = 0;
/*5721*/    for (int i = 0; i < arrayLen; i++) {
/*5722*/      if (css[i] == null) {
/*5723*/        anyStringNull = true;
/*5724*/        shortestStrLen = 0;
/*   0*/      } else {
/*5726*/        allStringsNull = false;
/*5727*/        shortestStrLen = Math.min(css[i].length(), shortestStrLen);
/*5728*/        longestStrLen = Math.max(css[i].length(), longestStrLen);
/*   0*/      } 
/*   0*/    } 
/*5733*/    if (allStringsNull || (longestStrLen == 0 && !anyStringNull))
/*5734*/      return -1; 
/*5738*/    if (shortestStrLen == 0)
/*5739*/      return 0; 
/*5743*/    int firstDiff = -1;
/*5744*/    for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
/*5745*/      char comparisonChar = css[0].charAt(stringPos);
/*5746*/      for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
/*5747*/        if (css[arrayPos].charAt(stringPos) != comparisonChar) {
/*5748*/          firstDiff = stringPos;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/*5752*/      if (firstDiff != -1)
/*   0*/        break; 
/*   0*/    } 
/*5757*/    if (firstDiff == -1 && shortestStrLen != longestStrLen)
/*5761*/      return shortestStrLen; 
/*5763*/    return firstDiff;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getCommonPrefix(String[] strs) {
/*5800*/    if (strs == null || strs.length == 0)
/*5801*/      return ""; 
/*5803*/    int smallestIndexOfDiff = indexOfDifference((CharSequence[])strs);
/*5804*/    if (smallestIndexOfDiff == -1) {
/*5806*/      if (strs[0] == null)
/*5807*/        return ""; 
/*5809*/      return strs[0];
/*   0*/    } 
/*5810*/    if (smallestIndexOfDiff == 0)
/*5812*/      return ""; 
/*5815*/    return strs[0].substring(0, smallestIndexOfDiff);
/*   0*/  }
/*   0*/  
/*   0*/  public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
/*5856*/    if (s == null || t == null)
/*5857*/      throw new IllegalArgumentException("Strings must not be null"); 
/*5877*/    int n = s.length();
/*5878*/    int m = t.length();
/*5880*/    if (n == 0)
/*5881*/      return m; 
/*5882*/    if (m == 0)
/*5883*/      return n; 
/*5886*/    if (n > m) {
/*5888*/      CharSequence tmp = s;
/*5889*/      s = t;
/*5890*/      t = tmp;
/*5891*/      n = m;
/*5892*/      m = t.length();
/*   0*/    } 
/*5895*/    int[] p = new int[n + 1];
/*5896*/    int[] d = new int[n + 1];
/*5907*/    for (int i = 0; i <= n; i++)
/*5908*/      p[i] = i; 
/*5911*/    for (int j = 1; j <= m; j++) {
/*5912*/      char t_j = t.charAt(j - 1);
/*5913*/      d[0] = j;
/*5915*/      for (int k = 1; k <= n; k++) {
/*5916*/        int cost = (s.charAt(k - 1) == t_j) ? 0 : 1;
/*5918*/        d[k] = Math.min(Math.min(d[k - 1] + 1, p[k] + 1), p[k - 1] + cost);
/*   0*/      } 
/*5922*/      int[] _d = p;
/*5923*/      p = d;
/*5924*/      d = _d;
/*   0*/    } 
/*5929*/    return p[n];
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean startsWith(String str, String prefix) {
/*5978*/    return startsWith(str, prefix, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean startsWithIgnoreCase(String str, String prefix) {
/*6003*/    return startsWith(str, prefix, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
/*6018*/    if (str == null || prefix == null)
/*6019*/      return (str == null && prefix == null); 
/*6021*/    if (prefix.length() > str.length())
/*6022*/      return false; 
/*6024*/    return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean startsWithAny(String string, String[] searchStrings) {
/*6046*/    if (isEmpty(string) || ArrayUtils.isEmpty(searchStrings))
/*6047*/      return false; 
/*6049*/    for (int i = 0; i < searchStrings.length; i++) {
/*6050*/      String searchString = searchStrings[i];
/*6051*/      if (startsWith(string, searchString))
/*6052*/        return true; 
/*   0*/    } 
/*6055*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean endsWith(String str, String suffix) {
/*6084*/    return endsWith(str, suffix, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean endsWithIgnoreCase(String str, String suffix) {
/*6110*/    return endsWith(str, suffix, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
/*6125*/    if (str == null || suffix == null)
/*6126*/      return (str == null && suffix == null); 
/*6128*/    if (suffix.length() > str.length())
/*6129*/      return false; 
/*6131*/    int strOffset = str.length() - suffix.length();
/*6132*/    return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
/*   0*/  }
/*   0*/}
