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
/*   0*/  public static boolean isEmpty(CharSequence str) {
/* 191*/    return (str == null || str.length() == 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNotEmpty(CharSequence str) {
/* 209*/    return !isEmpty(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBlank(CharSequence str) {
/*   0*/    int strLen;
/* 229*/    if (str == null || (strLen = str.length()) == 0)
/* 230*/      return true; 
/* 232*/    for (int i = 0; i < strLen; i++) {
/* 233*/      if (!Character.isWhitespace(str.charAt(i)))
/* 234*/        return false; 
/*   0*/    } 
/* 237*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNotBlank(CharSequence str) {
/* 257*/    return !isBlank(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static String trim(String str) {
/* 286*/    return (str == null) ? null : str.trim();
/*   0*/  }
/*   0*/  
/*   0*/  public static String trimToNull(String str) {
/* 312*/    String ts = trim(str);
/* 313*/    return isEmpty(ts) ? null : ts;
/*   0*/  }
/*   0*/  
/*   0*/  public static String trimToEmpty(String str) {
/* 338*/    return (str == null) ? "" : str.trim();
/*   0*/  }
/*   0*/  
/*   0*/  public static String strip(String str) {
/* 366*/    return strip(str, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripToNull(String str) {
/* 393*/    if (str == null)
/* 394*/      return null; 
/* 396*/    str = strip(str, null);
/* 397*/    return (str.length() == 0) ? null : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripToEmpty(String str) {
/* 423*/    return (str == null) ? "" : strip(str, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String strip(String str, String stripChars) {
/* 453*/    if (isEmpty(str))
/* 454*/      return str; 
/* 456*/    str = stripStart(str, stripChars);
/* 457*/    return stripEnd(str, stripChars);
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripStart(String str, String stripChars) {
/*   0*/    int strLen;
/* 486*/    if (str == null || (strLen = str.length()) == 0)
/* 487*/      return str; 
/* 489*/    int start = 0;
/* 490*/    if (stripChars == null) {
/* 491*/      while (start != strLen && Character.isWhitespace(str.charAt(start)))
/* 492*/        start++; 
/*   0*/    } else {
/* 494*/      if (stripChars.length() == 0)
/* 495*/        return str; 
/* 497*/      while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1)
/* 498*/        start++; 
/*   0*/    } 
/* 501*/    return str.substring(start);
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripEnd(String str, String stripChars) {
/*   0*/    int end;
/* 530*/    if (str == null || (end = str.length()) == 0)
/* 531*/      return str; 
/* 534*/    if (stripChars == null) {
/* 535*/      while (end != 0 && Character.isWhitespace(str.charAt(end - 1)))
/* 536*/        end--; 
/*   0*/    } else {
/* 538*/      if (stripChars.length() == 0)
/* 539*/        return str; 
/* 541*/      while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1)
/* 542*/        end--; 
/*   0*/    } 
/* 545*/    return str.substring(0, end);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] stripAll(String[] strs) {
/* 570*/    return stripAll(strs, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] stripAll(String[] strs, String stripChars) {
/*   0*/    int strsLen;
/* 600*/    if (strs == null || (strsLen = strs.length) == 0)
/* 601*/      return strs; 
/* 603*/    String[] newArr = new String[strsLen];
/* 604*/    for (int i = 0; i < strsLen; i++)
/* 605*/      newArr[i] = strip(strs[i], stripChars); 
/* 607*/    return newArr;
/*   0*/  }
/*   0*/  
/*   0*/  public static String stripAccents(String input) {
/* 627*/    if (input == null)
/* 628*/      return null; 
/* 630*/    if (SystemUtils.isJavaVersionAtLeast(1.6F))
/*   0*/      try {
/* 637*/        Class<?> normalizerFormClass = ClassUtils.getClass("java.text.Normalizer$Form", false);
/* 640*/        Class<?> normalizerClass = ClassUtils.getClass("java.text.Normalizer", false);
/* 643*/        Method method = normalizerClass.getMethod("normalize", new Class<?>[] { CharSequence.class, normalizerFormClass });
/* 646*/        Field nfd = normalizerFormClass.getField("NFD");
/* 649*/        String decomposed = (String)method.invoke(null, new Object[] { input, nfd.get(null) });
/* 652*/        Pattern accentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
/* 653*/        return accentPattern.matcher(decomposed).replaceAll("");
/* 654*/      } catch (ClassNotFoundException cnfe) {
/* 655*/        throw new RuntimeException("ClassNotFoundException occurred during 1.6 backcompat code", cnfe);
/* 656*/      } catch (NoSuchMethodException nsme) {
/* 657*/        throw new RuntimeException("NoSuchMethodException occurred during 1.6 backcompat code", nsme);
/* 658*/      } catch (NoSuchFieldException nsfe) {
/* 659*/        throw new RuntimeException("NoSuchFieldException occurred during 1.6 backcompat code", nsfe);
/* 660*/      } catch (IllegalAccessException iae) {
/* 661*/        throw new RuntimeException("IllegalAccessException occurred during 1.6 backcompat code", iae);
/* 662*/      } catch (IllegalArgumentException iae) {
/* 663*/        throw new RuntimeException("IllegalArgumentException occurred during 1.6 backcompat code", iae);
/* 664*/      } catch (InvocationTargetException ite) {
/* 665*/        throw new RuntimeException("InvocationTargetException occurred during 1.6 backcompat code", ite);
/* 666*/      } catch (SecurityException se) {
/* 667*/        throw new RuntimeException("SecurityException occurred during 1.6 backcompat code", se);
/*   0*/      }  
/* 670*/    throw new UnsupportedOperationException("The stripAccents(String) method is not supported until Java 1.6");
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equals(String str1, String str2) {
/* 697*/    return (str1 == null) ? ((str2 == null)) : str1.equals(str2);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equalsIgnoreCase(String str1, String str2) {
/* 722*/    return (str1 == null) ? ((str2 == null)) : str1.equalsIgnoreCase(str2);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, char searchChar) {
/* 747*/    if (isEmpty(str))
/* 748*/      return -1; 
/* 750*/    return str.indexOf(searchChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, char searchChar, int startPos) {
/* 779*/    if (isEmpty(str))
/* 780*/      return -1; 
/* 782*/    return str.indexOf(searchChar, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, String searchStr) {
/* 808*/    if (str == null || searchStr == null)
/* 809*/      return -1; 
/* 811*/    return str.indexOf(searchStr);
/*   0*/  }
/*   0*/  
/*   0*/  public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
/* 842*/    if (str == null || searchStr == null || ordinal <= 0)
/* 843*/      return -1; 
/* 845*/    if (searchStr.length() == 0)
/* 846*/      return 0; 
/* 848*/    int found = 0;
/* 849*/    int index = -1;
/*   0*/    while (true) {
/* 851*/      index = str.indexOf(searchStr, index + 1);
/* 852*/      if (index < 0)
/* 853*/        return index; 
/* 855*/      found++;
/* 856*/      if (found >= ordinal)
/* 857*/        return index; 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOf(String str, String searchStr, int startPos) {
/* 892*/    if (str == null || searchStr == null)
/* 893*/      return -1; 
/* 896*/    if (searchStr.length() == 0 && startPos >= str.length())
/* 897*/      return str.length(); 
/* 899*/    return str.indexOf(searchStr, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, char searchChar) {
/* 924*/    if (isEmpty(str))
/* 925*/      return -1; 
/* 927*/    return str.lastIndexOf(searchChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, char searchChar, int startPos) {
/* 958*/    if (isEmpty(str))
/* 959*/      return -1; 
/* 961*/    return str.lastIndexOf(searchChar, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, String searchStr) {
/* 987*/    if (str == null || searchStr == null)
/* 988*/      return -1; 
/* 990*/    return str.lastIndexOf(searchStr);
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOf(String str, String searchStr, int startPos) {
/*1022*/    if (str == null || searchStr == null)
/*1023*/      return -1; 
/*1025*/    return str.lastIndexOf(searchStr, startPos);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean contains(String str, char searchChar) {
/*1050*/    if (isEmpty(str))
/*1051*/      return false; 
/*1053*/    return (str.indexOf(searchChar) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean contains(String str, String searchStr) {
/*1078*/    if (str == null || searchStr == null)
/*1079*/      return false; 
/*1081*/    return (str.indexOf(searchStr) >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsIgnoreCase(String str, String searchStr) {
/*1108*/    if (str == null || searchStr == null)
/*1109*/      return false; 
/*1111*/    int len = searchStr.length();
/*1112*/    int max = str.length() - len;
/*1113*/    for (int i = 0; i <= max; i++) {
/*1114*/      if (str.regionMatches(true, i, searchStr, 0, len))
/*1115*/        return true; 
/*   0*/    } 
/*1118*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAny(String str, char[] searchChars) {
/*1146*/    if (isEmpty(str) || ArrayUtils.isEmpty(searchChars))
/*1147*/      return -1; 
/*1149*/    for (int i = 0; i < str.length(); i++) {
/*1150*/      char ch = str.charAt(i);
/*1151*/      for (int j = 0; j < searchChars.length; j++) {
/*1152*/        if (searchChars[j] == ch)
/*1153*/          return i; 
/*   0*/      } 
/*   0*/    } 
/*1157*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAny(String str, String searchChars) {
/*1183*/    if (isEmpty(str) || isEmpty(searchChars))
/*1184*/      return -1; 
/*1186*/    return indexOfAny(str, searchChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsAny(String str, char[] searchChars) {
/*1215*/    if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0)
/*1216*/      return false; 
/*1218*/    for (int i = 0; i < str.length(); i++) {
/*1219*/      char ch = str.charAt(i);
/*1220*/      for (int j = 0; j < searchChars.length; j++) {
/*1221*/        if (searchChars[j] == ch)
/*1222*/          return true; 
/*   0*/      } 
/*   0*/    } 
/*1226*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsAny(String str, String searchChars) {
/*1257*/    if (searchChars == null)
/*1258*/      return false; 
/*1260*/    return containsAny(str, searchChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAnyBut(String str, char[] searchChars) {
/*1288*/    if (isEmpty(str) || ArrayUtils.isEmpty(searchChars))
/*1289*/      return -1; 
/*1291*/    for (int i = 0; i < str.length(); i++) {
/*1292*/      char ch = str.charAt(i);
/*1293*/      int j = 0;
/*   0*/      while (true) {
/*1293*/        if (j < searchChars.length) {
/*1294*/          if (searchChars[j] == ch)
/*   0*/            break; 
/*   0*/          j++;
/*   0*/          continue;
/*   0*/        } 
/*1298*/        return i;
/*   0*/      } 
/*   0*/    } 
/*1300*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAnyBut(String str, String searchChars) {
/*1326*/    if (isEmpty(str) || isEmpty(searchChars))
/*1327*/      return -1; 
/*1329*/    for (int i = 0; i < str.length(); i++) {
/*1330*/      if (searchChars.indexOf(str.charAt(i)) < 0)
/*1331*/        return i; 
/*   0*/    } 
/*1334*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsOnly(String str, char[] valid) {
/*1362*/    if (valid == null || str == null)
/*1363*/      return false; 
/*1365*/    if (str.length() == 0)
/*1366*/      return true; 
/*1368*/    if (valid.length == 0)
/*1369*/      return false; 
/*1371*/    return (indexOfAnyBut(str, valid) == -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsOnly(String str, String validChars) {
/*1397*/    if (str == null || validChars == null)
/*1398*/      return false; 
/*1400*/    return containsOnly(str, validChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsNone(String str, char[] invalidChars) {
/*1428*/    if (str == null || invalidChars == null)
/*1429*/      return true; 
/*1431*/    int strSize = str.length();
/*1432*/    int validSize = invalidChars.length;
/*1433*/    for (int i = 0; i < strSize; i++) {
/*1434*/      char ch = str.charAt(i);
/*1435*/      for (int j = 0; j < validSize; j++) {
/*1436*/        if (invalidChars[j] == ch)
/*1437*/          return false; 
/*   0*/      } 
/*   0*/    } 
/*1441*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean containsNone(String str, String invalidChars) {
/*1467*/    if (str == null || invalidChars == null)
/*1468*/      return true; 
/*1470*/    return containsNone(str, invalidChars.toCharArray());
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfAny(String str, String[] searchStrs) {
/*1502*/    if (str == null || searchStrs == null)
/*1503*/      return -1; 
/*1505*/    int sz = searchStrs.length;
/*1508*/    int ret = Integer.MAX_VALUE;
/*1510*/    int tmp = 0;
/*1511*/    for (int i = 0; i < sz; i++) {
/*1512*/      String search = searchStrs[i];
/*1513*/      if (search != null) {
/*1516*/        tmp = str.indexOf(search);
/*1517*/        if (tmp != -1)
/*1521*/          if (tmp < ret)
/*1522*/            ret = tmp;  
/*   0*/      } 
/*   0*/    } 
/*1526*/    return (ret == Integer.MAX_VALUE) ? -1 : ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static int lastIndexOfAny(String str, String[] searchStrs) {
/*1555*/    if (str == null || searchStrs == null)
/*1556*/      return -1; 
/*1558*/    int sz = searchStrs.length;
/*1559*/    int ret = -1;
/*1560*/    int tmp = 0;
/*1561*/    for (int i = 0; i < sz; i++) {
/*1562*/      String search = searchStrs[i];
/*1563*/      if (search != null) {
/*1566*/        tmp = str.lastIndexOf(search);
/*1567*/        if (tmp > ret)
/*1568*/          ret = tmp; 
/*   0*/      } 
/*   0*/    } 
/*1571*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static String substring(String str, int start) {
/*1601*/    if (str == null)
/*1602*/      return null; 
/*1606*/    if (start < 0)
/*1607*/      start = str.length() + start; 
/*1610*/    if (start < 0)
/*1611*/      start = 0; 
/*1613*/    if (start > str.length())
/*1614*/      return ""; 
/*1617*/    return str.substring(start);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substring(String str, int start, int end) {
/*1656*/    if (str == null)
/*1657*/      return null; 
/*1661*/    if (end < 0)
/*1662*/      end = str.length() + end; 
/*1664*/    if (start < 0)
/*1665*/      start = str.length() + start; 
/*1669*/    if (end > str.length())
/*1670*/      end = str.length(); 
/*1674*/    if (start > end)
/*1675*/      return ""; 
/*1678*/    if (start < 0)
/*1679*/      start = 0; 
/*1681*/    if (end < 0)
/*1682*/      end = 0; 
/*1685*/    return str.substring(start, end);
/*   0*/  }
/*   0*/  
/*   0*/  public static String left(String str, int len) {
/*1711*/    if (str == null)
/*1712*/      return null; 
/*1714*/    if (len < 0)
/*1715*/      return ""; 
/*1717*/    if (str.length() <= len)
/*1718*/      return str; 
/*1720*/    return str.substring(0, len);
/*   0*/  }
/*   0*/  
/*   0*/  public static String right(String str, int len) {
/*1744*/    if (str == null)
/*1745*/      return null; 
/*1747*/    if (len < 0)
/*1748*/      return ""; 
/*1750*/    if (str.length() <= len)
/*1751*/      return str; 
/*1753*/    return str.substring(str.length() - len);
/*   0*/  }
/*   0*/  
/*   0*/  public static String mid(String str, int pos, int len) {
/*1781*/    if (str == null)
/*1782*/      return null; 
/*1784*/    if (len < 0 || pos > str.length())
/*1785*/      return ""; 
/*1787*/    if (pos < 0)
/*1788*/      pos = 0; 
/*1790*/    if (str.length() <= pos + len)
/*1791*/      return str.substring(pos); 
/*1793*/    return str.substring(pos, pos + len);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBefore(String str, String separator) {
/*1826*/    if (isEmpty(str) || separator == null)
/*1827*/      return str; 
/*1829*/    if (separator.length() == 0)
/*1830*/      return ""; 
/*1832*/    int pos = str.indexOf(separator);
/*1833*/    if (pos == -1)
/*1834*/      return str; 
/*1836*/    return str.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringAfter(String str, String separator) {
/*1868*/    if (isEmpty(str))
/*1869*/      return str; 
/*1871*/    if (separator == null)
/*1872*/      return ""; 
/*1874*/    int pos = str.indexOf(separator);
/*1875*/    if (pos == -1)
/*1876*/      return ""; 
/*1878*/    return str.substring(pos + separator.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBeforeLast(String str, String separator) {
/*1909*/    if (isEmpty(str) || isEmpty(separator))
/*1910*/      return str; 
/*1912*/    int pos = str.lastIndexOf(separator);
/*1913*/    if (pos == -1)
/*1914*/      return str; 
/*1916*/    return str.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringAfterLast(String str, String separator) {
/*1949*/    if (isEmpty(str))
/*1950*/      return str; 
/*1952*/    if (isEmpty(separator))
/*1953*/      return ""; 
/*1955*/    int pos = str.lastIndexOf(separator);
/*1956*/    if (pos == -1 || pos == str.length() - separator.length())
/*1957*/      return ""; 
/*1959*/    return str.substring(pos + separator.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBetween(String str, String tag) {
/*1986*/    return substringBetween(str, tag, tag);
/*   0*/  }
/*   0*/  
/*   0*/  public static String substringBetween(String str, String open, String close) {
/*2017*/    if (str == null || open == null || close == null)
/*2018*/      return null; 
/*2020*/    int start = str.indexOf(open);
/*2021*/    if (start != -1) {
/*2022*/      int end = str.indexOf(close, start + open.length());
/*2023*/      if (end != -1)
/*2024*/        return str.substring(start + open.length(), end); 
/*   0*/    } 
/*2027*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] substringsBetween(String str, String open, String close) {
/*2053*/    if (str == null || isEmpty(open) || isEmpty(close))
/*2054*/      return null; 
/*2056*/    int strLen = str.length();
/*2057*/    if (strLen == 0)
/*2058*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2060*/    int closeLen = close.length();
/*2061*/    int openLen = open.length();
/*2062*/    List<String> list = new ArrayList<String>();
/*2063*/    int pos = 0;
/*2064*/    while (pos < strLen - closeLen) {
/*2065*/      int start = str.indexOf(open, pos);
/*2066*/      if (start < 0)
/*   0*/        break; 
/*2069*/      start += openLen;
/*2070*/      int end = str.indexOf(close, start);
/*2071*/      if (end < 0)
/*   0*/        break; 
/*2074*/      list.add(str.substring(start, end));
/*2075*/      pos = end + closeLen;
/*   0*/    } 
/*2077*/    if (list.isEmpty())
/*2078*/      return null; 
/*2080*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str) {
/*2111*/    return split(str, null, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str, char separatorChar) {
/*2139*/    return splitWorker(str, separatorChar, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str, String separatorChars) {
/*2168*/    return splitWorker(str, separatorChars, -1, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] split(String str, String separatorChars, int max) {
/*2202*/    return splitWorker(str, separatorChars, max, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparator(String str, String separator) {
/*2229*/    return splitByWholeSeparatorWorker(str, separator, -1, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparator(String str, String separator, int max) {
/*2260*/    return splitByWholeSeparatorWorker(str, separator, max, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
/*2289*/    return splitByWholeSeparatorWorker(str, separator, -1, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
/*2322*/    return splitByWholeSeparatorWorker(str, separator, max, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
/*2342*/    if (str == null)
/*2343*/      return null; 
/*2346*/    int len = str.length();
/*2348*/    if (len == 0)
/*2349*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2352*/    if (separator == null || "".equals(separator))
/*2354*/      return splitWorker(str, null, max, preserveAllTokens); 
/*2357*/    int separatorLength = separator.length();
/*2359*/    ArrayList<String> substrings = new ArrayList<String>();
/*2360*/    int numberOfSubstrings = 0;
/*2361*/    int beg = 0;
/*2362*/    int end = 0;
/*2363*/    while (end < len) {
/*2364*/      end = str.indexOf(separator, beg);
/*2366*/      if (end > -1) {
/*2367*/        if (end > beg) {
/*2368*/          numberOfSubstrings++;
/*2370*/          if (numberOfSubstrings == max) {
/*2371*/            end = len;
/*2372*/            substrings.add(str.substring(beg));
/*   0*/            continue;
/*   0*/          } 
/*2376*/          substrings.add(str.substring(beg, end));
/*2381*/          beg = end + separatorLength;
/*   0*/          continue;
/*   0*/        } 
/*2385*/        if (preserveAllTokens) {
/*2386*/          numberOfSubstrings++;
/*2387*/          if (numberOfSubstrings == max) {
/*2388*/            end = len;
/*2389*/            substrings.add(str.substring(beg));
/*   0*/          } else {
/*2391*/            substrings.add("");
/*   0*/          } 
/*   0*/        } 
/*2394*/        beg = end + separatorLength;
/*   0*/        continue;
/*   0*/      } 
/*2398*/      substrings.add(str.substring(beg));
/*2399*/      end = len;
/*   0*/    } 
/*2403*/    return substrings.<String>toArray(new String[substrings.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str) {
/*2432*/    return splitWorker(str, null, -1, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str, char separatorChar) {
/*2468*/    return splitWorker(str, separatorChar, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
/*2486*/    if (str == null)
/*2487*/      return null; 
/*2489*/    int len = str.length();
/*2490*/    if (len == 0)
/*2491*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2493*/    List<String> list = new ArrayList<String>();
/*2494*/    int i = 0, start = 0;
/*   0*/    boolean match = false;
/*   0*/    boolean lastMatch = false;
/*2497*/    while (i < len) {
/*2498*/      if (str.charAt(i) == separatorChar) {
/*2499*/        if (match || preserveAllTokens) {
/*2500*/          list.add(str.substring(start, i));
/*2501*/          match = false;
/*2502*/          lastMatch = true;
/*   0*/        } 
/*2504*/        start = ++i;
/*   0*/        continue;
/*   0*/      } 
/*2507*/      lastMatch = false;
/*2508*/      match = true;
/*2509*/      i++;
/*   0*/    } 
/*2511*/    if (match || (preserveAllTokens && lastMatch))
/*2512*/      list.add(str.substring(start, i)); 
/*2514*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str, String separatorChars) {
/*2551*/    return splitWorker(str, separatorChars, -1, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
/*2591*/    return splitWorker(str, separatorChars, max, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
/*2613*/    if (str == null)
/*2614*/      return null; 
/*2616*/    int len = str.length();
/*2617*/    if (len == 0)
/*2618*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2620*/    List<String> list = new ArrayList<String>();
/*2621*/    int sizePlus1 = 1;
/*2622*/    int i = 0, start = 0;
/*   0*/    boolean match = false;
/*   0*/    boolean lastMatch = false;
/*2625*/    if (separatorChars == null) {
/*2627*/      while (i < len) {
/*2628*/        if (Character.isWhitespace(str.charAt(i))) {
/*2629*/          if (match || preserveAllTokens) {
/*2630*/            lastMatch = true;
/*2631*/            if (sizePlus1++ == max) {
/*2632*/              i = len;
/*2633*/              lastMatch = false;
/*   0*/            } 
/*2635*/            list.add(str.substring(start, i));
/*2636*/            match = false;
/*   0*/          } 
/*2638*/          start = ++i;
/*   0*/          continue;
/*   0*/        } 
/*2641*/        lastMatch = false;
/*2642*/        match = true;
/*2643*/        i++;
/*   0*/      } 
/*2645*/    } else if (separatorChars.length() == 1) {
/*2647*/      char sep = separatorChars.charAt(0);
/*2648*/      while (i < len) {
/*2649*/        if (str.charAt(i) == sep) {
/*2650*/          if (match || preserveAllTokens) {
/*2651*/            lastMatch = true;
/*2652*/            if (sizePlus1++ == max) {
/*2653*/              i = len;
/*2654*/              lastMatch = false;
/*   0*/            } 
/*2656*/            list.add(str.substring(start, i));
/*2657*/            match = false;
/*   0*/          } 
/*2659*/          start = ++i;
/*   0*/          continue;
/*   0*/        } 
/*2662*/        lastMatch = false;
/*2663*/        match = true;
/*2664*/        i++;
/*   0*/      } 
/*   0*/    } else {
/*2668*/      while (i < len) {
/*2669*/        if (separatorChars.indexOf(str.charAt(i)) >= 0) {
/*2670*/          if (match || preserveAllTokens) {
/*2671*/            lastMatch = true;
/*2672*/            if (sizePlus1++ == max) {
/*2673*/              i = len;
/*2674*/              lastMatch = false;
/*   0*/            } 
/*2676*/            list.add(str.substring(start, i));
/*2677*/            match = false;
/*   0*/          } 
/*2679*/          start = ++i;
/*   0*/          continue;
/*   0*/        } 
/*2682*/        lastMatch = false;
/*2683*/        match = true;
/*2684*/        i++;
/*   0*/      } 
/*   0*/    } 
/*2687*/    if (match || (preserveAllTokens && lastMatch))
/*2688*/      list.add(str.substring(start, i)); 
/*2690*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByCharacterType(String str) {
/*2713*/    return splitByCharacterType(str, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static String[] splitByCharacterTypeCamelCase(String str) {
/*2741*/    return splitByCharacterType(str, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static String[] splitByCharacterType(String str, boolean camelCase) {
/*2759*/    if (str == null)
/*2760*/      return null; 
/*2762*/    if (str.length() == 0)
/*2763*/      return ArrayUtils.EMPTY_STRING_ARRAY; 
/*2765*/    char[] c = str.toCharArray();
/*2766*/    List<String> list = new ArrayList<String>();
/*2767*/    int tokenStart = 0;
/*2768*/    int currentType = Character.getType(c[tokenStart]);
/*2769*/    for (int pos = tokenStart + 1; pos < c.length; pos++) {
/*2770*/      int type = Character.getType(c[pos]);
/*2771*/      if (type != currentType) {
/*2774*/        if (camelCase && type == 2 && currentType == 1) {
/*2775*/          int newTokenStart = pos - 1;
/*2776*/          if (newTokenStart != tokenStart) {
/*2777*/            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
/*2778*/            tokenStart = newTokenStart;
/*   0*/          } 
/*   0*/        } else {
/*2781*/          list.add(new String(c, tokenStart, pos - tokenStart));
/*2782*/          tokenStart = pos;
/*   0*/        } 
/*2784*/        currentType = type;
/*   0*/      } 
/*   0*/    } 
/*2786*/    list.add(new String(c, tokenStart, c.length - tokenStart));
/*2787*/    return list.<String>toArray(new String[list.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array) {
/*2813*/    return join(array, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, char separator) {
/*2839*/    if (array == null)
/*2840*/      return null; 
/*2843*/    return join(array, separator, 0, array.length);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, char separator, int startIndex, int endIndex) {
/*2873*/    if (array == null)
/*2874*/      return null; 
/*2876*/    int bufSize = endIndex - startIndex;
/*2877*/    if (bufSize <= 0)
/*2878*/      return ""; 
/*2881*/    bufSize *= ((array[startIndex] == null) ? 16 : array[startIndex].toString().length()) + 1;
/*2882*/    StringBuilder buf = new StringBuilder(bufSize);
/*2884*/    for (int i = startIndex; i < endIndex; i++) {
/*2885*/      if (i > startIndex)
/*2886*/        buf.append(separator); 
/*2888*/      if (array[i] != null)
/*2889*/        buf.append(array[i]); 
/*   0*/    } 
/*2892*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, String separator) {
/*2920*/    if (array == null)
/*2921*/      return null; 
/*2923*/    return join(array, separator, 0, array.length);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Object[] array, String separator, int startIndex, int endIndex) {
/*2954*/    if (array == null)
/*2955*/      return null; 
/*2957*/    if (separator == null)
/*2958*/      separator = ""; 
/*2963*/    int bufSize = endIndex - startIndex;
/*2964*/    if (bufSize <= 0)
/*2965*/      return ""; 
/*2968*/    bufSize *= ((array[startIndex] == null) ? 16 : array[startIndex].toString().length()) + separator.length();
/*2971*/    StringBuilder buf = new StringBuilder(bufSize);
/*2973*/    for (int i = startIndex; i < endIndex; i++) {
/*2974*/      if (i > startIndex)
/*2975*/        buf.append(separator); 
/*2977*/      if (array[i] != null)
/*2978*/        buf.append(array[i]); 
/*   0*/    } 
/*2981*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterator<?> iterator, char separator) {
/*3001*/    if (iterator == null)
/*3002*/      return null; 
/*3004*/    if (!iterator.hasNext())
/*3005*/      return ""; 
/*3007*/    Object first = iterator.next();
/*3008*/    if (!iterator.hasNext())
/*3009*/      return ObjectUtils.toString(first); 
/*3013*/    StringBuilder buf = new StringBuilder(256);
/*3014*/    if (first != null)
/*3015*/      buf.append(first); 
/*3018*/    while (iterator.hasNext()) {
/*3019*/      buf.append(separator);
/*3020*/      Object obj = iterator.next();
/*3021*/      if (obj != null)
/*3022*/        buf.append(obj); 
/*   0*/    } 
/*3026*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterator<?> iterator, String separator) {
/*3045*/    if (iterator == null)
/*3046*/      return null; 
/*3048*/    if (!iterator.hasNext())
/*3049*/      return ""; 
/*3051*/    Object first = iterator.next();
/*3052*/    if (!iterator.hasNext())
/*3053*/      return ObjectUtils.toString(first); 
/*3057*/    StringBuilder buf = new StringBuilder(256);
/*3058*/    if (first != null)
/*3059*/      buf.append(first); 
/*3062*/    while (iterator.hasNext()) {
/*3063*/      if (separator != null)
/*3064*/        buf.append(separator); 
/*3066*/      Object obj = iterator.next();
/*3067*/      if (obj != null)
/*3068*/        buf.append(obj); 
/*   0*/    } 
/*3071*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterable<?> iterable, char separator) {
/*3089*/    if (iterable == null)
/*3090*/      return null; 
/*3092*/    return join(iterable.iterator(), separator);
/*   0*/  }
/*   0*/  
/*   0*/  public static String join(Iterable<?> iterable, String separator) {
/*3110*/    if (iterable == null)
/*3111*/      return null; 
/*3113*/    return join(iterable.iterator(), separator);
/*   0*/  }
/*   0*/  
/*   0*/  public static String deleteWhitespace(String str) {
/*3133*/    if (isEmpty(str))
/*3134*/      return str; 
/*3136*/    int sz = str.length();
/*3137*/    char[] chs = new char[sz];
/*3138*/    int count = 0;
/*3139*/    for (int i = 0; i < sz; i++) {
/*3140*/      if (!Character.isWhitespace(str.charAt(i)))
/*3141*/        chs[count++] = str.charAt(i); 
/*   0*/    } 
/*3144*/    if (count == sz)
/*3145*/      return str; 
/*3147*/    return new String(chs, 0, count);
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeStart(String str, String remove) {
/*3177*/    if (isEmpty(str) || isEmpty(remove))
/*3178*/      return str; 
/*3180*/    if (str.startsWith(remove))
/*3181*/      return str.substring(remove.length()); 
/*3183*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeStartIgnoreCase(String str, String remove) {
/*3212*/    if (isEmpty(str) || isEmpty(remove))
/*3213*/      return str; 
/*3215*/    if (startsWithIgnoreCase(str, remove))
/*3216*/      return str.substring(remove.length()); 
/*3218*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeEnd(String str, String remove) {
/*3246*/    if (isEmpty(str) || isEmpty(remove))
/*3247*/      return str; 
/*3249*/    if (str.endsWith(remove))
/*3250*/      return str.substring(0, str.length() - remove.length()); 
/*3252*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String removeEndIgnoreCase(String str, String remove) {
/*3280*/    if (isEmpty(str) || isEmpty(remove))
/*3281*/      return str; 
/*3283*/    if (endsWithIgnoreCase(str, remove))
/*3284*/      return str.substring(0, str.length() - remove.length()); 
/*3286*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String remove(String str, String remove) {
/*3313*/    if (isEmpty(str) || isEmpty(remove))
/*3314*/      return str; 
/*3316*/    return replace(str, remove, "", -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String remove(String str, char remove) {
/*3339*/    if (isEmpty(str) || str.indexOf(remove) == -1)
/*3340*/      return str; 
/*3342*/    char[] chars = str.toCharArray();
/*3343*/    int pos = 0;
/*3344*/    for (int i = 0; i < chars.length; i++) {
/*3345*/      if (chars[i] != remove)
/*3346*/        chars[pos++] = chars[i]; 
/*   0*/    } 
/*3349*/    return new String(chars, 0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceOnce(String text, String searchString, String replacement) {
/*3378*/    return replace(text, searchString, replacement, 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replace(String text, String searchString, String replacement) {
/*3405*/    return replace(text, searchString, replacement, -1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replace(String text, String searchString, String replacement, int max) {
/*3437*/    if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0)
/*3438*/      return text; 
/*3440*/    int start = 0;
/*3441*/    int end = text.indexOf(searchString, start);
/*3442*/    if (end == -1)
/*3443*/      return text; 
/*3445*/    int replLength = searchString.length();
/*3446*/    int increase = replacement.length() - replLength;
/*3447*/    increase = (increase < 0) ? 0 : increase;
/*3448*/    increase *= (max < 0) ? 16 : ((max > 64) ? 64 : max);
/*3449*/    StringBuilder buf = new StringBuilder(text.length() + increase);
/*3450*/    while (end != -1) {
/*3451*/      buf.append(text.substring(start, end)).append(replacement);
/*3452*/      start = end + replLength;
/*3453*/      if (--max == 0)
/*   0*/        break; 
/*3456*/      end = text.indexOf(searchString, start);
/*   0*/    } 
/*3458*/    buf.append(text.substring(start));
/*3459*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceEach(String text, String[] searchList, String[] replacementList) {
/*3502*/    return replaceEach(text, searchList, replacementList, false, 0);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
/*3553*/    int timeToLive = (searchList == null) ? 0 : searchList.length;
/*3554*/    return replaceEach(text, searchList, replacementList, true, timeToLive);
/*   0*/  }
/*   0*/  
/*   0*/  private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
/*3612*/    if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0)
/*3615*/      return text; 
/*3619*/    if (timeToLive < 0)
/*3620*/      throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text); 
/*3623*/    int searchLength = searchList.length;
/*3624*/    int replacementLength = replacementList.length;
/*3627*/    if (searchLength != replacementLength)
/*3628*/      throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength); 
/*3635*/    boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
/*3638*/    int textIndex = -1;
/*3639*/    int replaceIndex = -1;
/*3640*/    int tempIndex = -1;
/*3644*/    for (int i = 0; i < searchLength; i++) {
/*3645*/      if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && searchList[i].length() != 0 && replacementList[i] != null) {
/*3650*/        tempIndex = text.indexOf(searchList[i]);
/*3653*/        if (tempIndex == -1) {
/*3654*/          noMoreMatchesForReplIndex[i] = true;
/*3656*/        } else if (textIndex == -1 || tempIndex < textIndex) {
/*3657*/          textIndex = tempIndex;
/*3658*/          replaceIndex = i;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3665*/    if (textIndex == -1)
/*3666*/      return text; 
/*3669*/    int start = 0;
/*3672*/    int increase = 0;
/*3675*/    for (int j = 0; j >= searchList.length; j++) {
/*3676*/      int greater = replacementList[j].length() - searchList[j].length();
/*3677*/      if (greater > 0)
/*3678*/        increase += 3 * greater; 
/*   0*/    } 
/*3682*/    increase = Math.min(increase, text.length() / 5);
/*3684*/    StringBuilder buf = new StringBuilder(text.length() + increase);
/*3686*/    while (textIndex != -1) {
/*3688*/      for (int n = start; n < textIndex; n++)
/*3689*/        buf.append(text.charAt(n)); 
/*3691*/      buf.append(replacementList[replaceIndex]);
/*3693*/      start = textIndex + searchList[replaceIndex].length();
/*3695*/      textIndex = -1;
/*3696*/      replaceIndex = -1;
/*3697*/      tempIndex = -1;
/*3700*/      for (int m = 0; m < searchLength; m++) {
/*3701*/        if (!noMoreMatchesForReplIndex[m] && searchList[m] != null && searchList[m].length() != 0 && replacementList[m] != null) {
/*3706*/          tempIndex = text.indexOf(searchList[m], start);
/*3709*/          if (tempIndex == -1) {
/*3710*/            noMoreMatchesForReplIndex[m] = true;
/*3712*/          } else if (textIndex == -1 || tempIndex < textIndex) {
/*3713*/            textIndex = tempIndex;
/*3714*/            replaceIndex = m;
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3721*/    int textLength = text.length();
/*3722*/    for (int k = start; k < textLength; k++)
/*3723*/      buf.append(text.charAt(k)); 
/*3725*/    String result = buf.toString();
/*3726*/    if (!repeat)
/*3727*/      return result; 
/*3730*/    return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceChars(String str, char searchChar, char replaceChar) {
/*3756*/    if (str == null)
/*3757*/      return null; 
/*3759*/    return str.replace(searchChar, replaceChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static String replaceChars(String str, String searchChars, String replaceChars) {
/*3799*/    if (isEmpty(str) || isEmpty(searchChars))
/*3800*/      return str; 
/*3802*/    if (replaceChars == null)
/*3803*/      replaceChars = ""; 
/*   0*/    boolean modified = false;
/*3806*/    int replaceCharsLength = replaceChars.length();
/*3807*/    int strLength = str.length();
/*3808*/    StringBuilder buf = new StringBuilder(strLength);
/*3809*/    for (int i = 0; i < strLength; i++) {
/*3810*/      char ch = str.charAt(i);
/*3811*/      int index = searchChars.indexOf(ch);
/*3812*/      if (index >= 0) {
/*3813*/        modified = true;
/*3814*/        if (index < replaceCharsLength)
/*3815*/          buf.append(replaceChars.charAt(index)); 
/*   0*/      } else {
/*3818*/        buf.append(ch);
/*   0*/      } 
/*   0*/    } 
/*3821*/    if (modified)
/*3822*/      return buf.toString(); 
/*3824*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String overlay(String str, String overlay, int start, int end) {
/*3859*/    if (str == null)
/*3860*/      return null; 
/*3862*/    if (overlay == null)
/*3863*/      overlay = ""; 
/*3865*/    int len = str.length();
/*3866*/    if (start < 0)
/*3867*/      start = 0; 
/*3869*/    if (start > len)
/*3870*/      start = len; 
/*3872*/    if (end < 0)
/*3873*/      end = 0; 
/*3875*/    if (end > len)
/*3876*/      end = len; 
/*3878*/    if (start > end) {
/*3879*/      int temp = start;
/*3880*/      start = end;
/*3881*/      end = temp;
/*   0*/    } 
/*3883*/    return new StringBuilder(len + start - end + overlay.length() + 1).append(str.substring(0, start)).append(overlay).append(str.substring(end)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String chomp(String str) {
/*3918*/    if (isEmpty(str))
/*3919*/      return str; 
/*3922*/    if (str.length() == 1) {
/*3923*/      char ch = str.charAt(0);
/*3924*/      if (ch == '\r' || ch == '\n')
/*3925*/        return ""; 
/*3927*/      return str;
/*   0*/    } 
/*3930*/    int lastIdx = str.length() - 1;
/*3931*/    char last = str.charAt(lastIdx);
/*3933*/    if (last == '\n') {
/*3934*/      if (str.charAt(lastIdx - 1) == '\r')
/*3935*/        lastIdx--; 
/*3937*/    } else if (last != '\r') {
/*3938*/      lastIdx++;
/*   0*/    } 
/*3940*/    return str.substring(0, lastIdx);
/*   0*/  }
/*   0*/  
/*   0*/  public static String chomp(String str, String separator) {
/*3970*/    if (isEmpty(str) || separator == null)
/*3971*/      return str; 
/*3973*/    if (str.endsWith(separator))
/*3974*/      return str.substring(0, str.length() - separator.length()); 
/*3976*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String chop(String str) {
/*4005*/    if (str == null)
/*4006*/      return null; 
/*4008*/    int strLen = str.length();
/*4009*/    if (strLen < 2)
/*4010*/      return ""; 
/*4012*/    int lastIdx = strLen - 1;
/*4013*/    String ret = str.substring(0, lastIdx);
/*4014*/    char last = str.charAt(lastIdx);
/*4015*/    if (last == '\n' && 
/*4016*/      ret.charAt(lastIdx - 1) == '\r')
/*4017*/      return ret.substring(0, lastIdx - 1); 
/*4020*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  public static String repeat(String str, int repeat) {
/*   0*/    char ch, output1[];
/*   0*/    int i;
/*   0*/    char ch0, ch1, output2[];
/*   0*/    int j;
/*4049*/    if (str == null)
/*4050*/      return null; 
/*4052*/    if (repeat <= 0)
/*4053*/      return ""; 
/*4055*/    int inputLength = str.length();
/*4056*/    if (repeat == 1 || inputLength == 0)
/*4057*/      return str; 
/*4059*/    if (inputLength == 1 && repeat <= 8192)
/*4060*/      return padding(repeat, str.charAt(0)); 
/*4063*/    int outputLength = inputLength * repeat;
/*4064*/    switch (inputLength) {
/*   0*/      case 1:
/*4066*/        ch = str.charAt(0);
/*4067*/        output1 = new char[outputLength];
/*4068*/        for (i = repeat - 1; i >= 0; i--)
/*4069*/          output1[i] = ch; 
/*4071*/        return new String(output1);
/*   0*/      case 2:
/*4073*/        ch0 = str.charAt(0);
/*4074*/        ch1 = str.charAt(1);
/*4075*/        output2 = new char[outputLength];
/*4076*/        for (j = repeat * 2 - 2; j >= 0; j--, j--) {
/*4077*/          output2[j] = ch0;
/*4078*/          output2[j + 1] = ch1;
/*   0*/        } 
/*4080*/        return new String(output2);
/*   0*/    } 
/*4082*/    StringBuilder buf = new StringBuilder(outputLength);
/*4083*/    for (int k = 0; k < repeat; k++)
/*4084*/      buf.append(str); 
/*4086*/    return buf.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String repeat(String str, String separator, int repeat) {
/*4110*/    if (str == null || separator == null)
/*4111*/      return repeat(str, repeat); 
/*4114*/    String result = repeat(str + separator, repeat);
/*4115*/    return removeEnd(result, separator);
/*   0*/  }
/*   0*/  
/*   0*/  private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
/*4143*/    if (repeat < 0)
/*4144*/      throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat); 
/*4146*/    char[] buf = new char[repeat];
/*4147*/    for (int i = 0; i < buf.length; i++)
/*4148*/      buf[i] = padChar; 
/*4150*/    return new String(buf);
/*   0*/  }
/*   0*/  
/*   0*/  public static String rightPad(String str, int size) {
/*4173*/    return rightPad(str, size, ' ');
/*   0*/  }
/*   0*/  
/*   0*/  public static String rightPad(String str, int size, char padChar) {
/*4198*/    if (str == null)
/*4199*/      return null; 
/*4201*/    int pads = size - str.length();
/*4202*/    if (pads <= 0)
/*4203*/      return str; 
/*4205*/    if (pads > 8192)
/*4206*/      return rightPad(str, size, String.valueOf(padChar)); 
/*4208*/    return str.concat(padding(pads, padChar));
/*   0*/  }
/*   0*/  
/*   0*/  public static String rightPad(String str, int size, String padStr) {
/*4235*/    if (str == null)
/*4236*/      return null; 
/*4238*/    if (isEmpty(padStr))
/*4239*/      padStr = " "; 
/*4241*/    int padLen = padStr.length();
/*4242*/    int strLen = str.length();
/*4243*/    int pads = size - strLen;
/*4244*/    if (pads <= 0)
/*4245*/      return str; 
/*4247*/    if (padLen == 1 && pads <= 8192)
/*4248*/      return rightPad(str, size, padStr.charAt(0)); 
/*4251*/    if (pads == padLen)
/*4252*/      return str.concat(padStr); 
/*4253*/    if (pads < padLen)
/*4254*/      return str.concat(padStr.substring(0, pads)); 
/*4256*/    char[] padding = new char[pads];
/*4257*/    char[] padChars = padStr.toCharArray();
/*4258*/    for (int i = 0; i < pads; i++)
/*4259*/      padding[i] = padChars[i % padLen]; 
/*4261*/    return str.concat(new String(padding));
/*   0*/  }
/*   0*/  
/*   0*/  public static String leftPad(String str, int size) {
/*4285*/    return leftPad(str, size, ' ');
/*   0*/  }
/*   0*/  
/*   0*/  public static String leftPad(String str, int size, char padChar) {
/*4310*/    if (str == null)
/*4311*/      return null; 
/*4313*/    int pads = size - str.length();
/*4314*/    if (pads <= 0)
/*4315*/      return str; 
/*4317*/    if (pads > 8192)
/*4318*/      return leftPad(str, size, String.valueOf(padChar)); 
/*4320*/    return padding(pads, padChar).concat(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static String leftPad(String str, int size, String padStr) {
/*4347*/    if (str == null)
/*4348*/      return null; 
/*4350*/    if (isEmpty(padStr))
/*4351*/      padStr = " "; 
/*4353*/    int padLen = padStr.length();
/*4354*/    int strLen = str.length();
/*4355*/    int pads = size - strLen;
/*4356*/    if (pads <= 0)
/*4357*/      return str; 
/*4359*/    if (padLen == 1 && pads <= 8192)
/*4360*/      return leftPad(str, size, padStr.charAt(0)); 
/*4363*/    if (pads == padLen)
/*4364*/      return padStr.concat(str); 
/*4365*/    if (pads < padLen)
/*4366*/      return padStr.substring(0, pads).concat(str); 
/*4368*/    char[] padding = new char[pads];
/*4369*/    char[] padChars = padStr.toCharArray();
/*4370*/    for (int i = 0; i < pads; i++)
/*4371*/      padding[i] = padChars[i % padLen]; 
/*4373*/    return new String(padding).concat(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static int length(String str) {
/*4386*/    return (str == null) ? 0 : str.length();
/*   0*/  }
/*   0*/  
/*   0*/  public static String center(String str, int size) {
/*4415*/    return center(str, size, ' ');
/*   0*/  }
/*   0*/  
/*   0*/  public static String center(String str, int size, char padChar) {
/*4443*/    if (str == null || size <= 0)
/*4444*/      return str; 
/*4446*/    int strLen = str.length();
/*4447*/    int pads = size - strLen;
/*4448*/    if (pads <= 0)
/*4449*/      return str; 
/*4451*/    str = leftPad(str, strLen + pads / 2, padChar);
/*4452*/    str = rightPad(str, size, padChar);
/*4453*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String center(String str, int size, String padStr) {
/*4483*/    if (str == null || size <= 0)
/*4484*/      return str; 
/*4486*/    if (isEmpty(padStr))
/*4487*/      padStr = " "; 
/*4489*/    int strLen = str.length();
/*4490*/    int pads = size - strLen;
/*4491*/    if (pads <= 0)
/*4492*/      return str; 
/*4494*/    str = leftPad(str, strLen + pads / 2, padStr);
/*4495*/    str = rightPad(str, size, padStr);
/*4496*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String upperCase(String str) {
/*4521*/    if (str == null)
/*4522*/      return null; 
/*4524*/    return str.toUpperCase();
/*   0*/  }
/*   0*/  
/*   0*/  public static String upperCase(String str, Locale locale) {
/*4544*/    if (str == null)
/*4545*/      return null; 
/*4547*/    return str.toUpperCase(locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static String lowerCase(String str) {
/*4570*/    if (str == null)
/*4571*/      return null; 
/*4573*/    return str.toLowerCase();
/*   0*/  }
/*   0*/  
/*   0*/  public static String lowerCase(String str, Locale locale) {
/*4593*/    if (str == null)
/*4594*/      return null; 
/*4596*/    return str.toLowerCase(locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static String capitalize(String str) {
/*   0*/    int strLen;
/*4621*/    if (str == null || (strLen = str.length()) == 0)
/*4622*/      return str; 
/*4624*/    return new StringBuilder(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String uncapitalize(String str) {
/*   0*/    int strLen;
/*4652*/    if (str == null || (strLen = str.length()) == 0)
/*4653*/      return str; 
/*4655*/    return new StringBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String swapCase(String str) {
/*   0*/    int strLen;
/*4690*/    if (str == null || (strLen = str.length()) == 0)
/*4691*/      return str; 
/*4693*/    StringBuilder buffer = new StringBuilder(strLen);
/*4695*/    char ch = Character.MIN_VALUE;
/*4696*/    for (int i = 0; i < strLen; i++) {
/*4697*/      ch = str.charAt(i);
/*4698*/      if (Character.isUpperCase(ch)) {
/*4699*/        ch = Character.toLowerCase(ch);
/*4700*/      } else if (Character.isTitleCase(ch)) {
/*4701*/        ch = Character.toLowerCase(ch);
/*4702*/      } else if (Character.isLowerCase(ch)) {
/*4703*/        ch = Character.toUpperCase(ch);
/*   0*/      } 
/*4705*/      buffer.append(ch);
/*   0*/    } 
/*4707*/    return buffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static int countMatches(String str, String sub) {
/*4732*/    if (isEmpty(str) || isEmpty(sub))
/*4733*/      return 0; 
/*4735*/    int count = 0;
/*4736*/    int idx = 0;
/*4737*/    while ((idx = str.indexOf(sub, idx)) != -1) {
/*4738*/      count++;
/*4739*/      idx += sub.length();
/*   0*/    } 
/*4741*/    return count;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlpha(String str) {
/*4765*/    if (str == null)
/*4766*/      return false; 
/*4768*/    int sz = str.length();
/*4769*/    for (int i = 0; i < sz; i++) {
/*4770*/      if (!Character.isLetter(str.charAt(i)))
/*4771*/        return false; 
/*   0*/    } 
/*4774*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlphaSpace(String str) {
/*4799*/    if (str == null)
/*4800*/      return false; 
/*4802*/    int sz = str.length();
/*4803*/    for (int i = 0; i < sz; i++) {
/*4804*/      if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ')
/*4805*/        return false; 
/*   0*/    } 
/*4808*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlphanumeric(String str) {
/*4832*/    if (str == null)
/*4833*/      return false; 
/*4835*/    int sz = str.length();
/*4836*/    for (int i = 0; i < sz; i++) {
/*4837*/      if (!Character.isLetterOrDigit(str.charAt(i)))
/*4838*/        return false; 
/*   0*/    } 
/*4841*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAlphanumericSpace(String str) {
/*4866*/    if (str == null)
/*4867*/      return false; 
/*4869*/    int sz = str.length();
/*4870*/    for (int i = 0; i < sz; i++) {
/*4871*/      if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ')
/*4872*/        return false; 
/*   0*/    } 
/*4875*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAsciiPrintable(String str) {
/*4904*/    if (str == null)
/*4905*/      return false; 
/*4907*/    int sz = str.length();
/*4908*/    for (int i = 0; i < sz; i++) {
/*4909*/      if (!CharUtils.isAsciiPrintable(str.charAt(i)))
/*4910*/        return false; 
/*   0*/    } 
/*4913*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumeric(String str) {
/*4938*/    if (str == null)
/*4939*/      return false; 
/*4941*/    int sz = str.length();
/*4942*/    for (int i = 0; i < sz; i++) {
/*4943*/      if (!Character.isDigit(str.charAt(i)))
/*4944*/        return false; 
/*   0*/    } 
/*4947*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNumericSpace(String str) {
/*4974*/    if (str == null)
/*4975*/      return false; 
/*4977*/    int sz = str.length();
/*4978*/    for (int i = 0; i < sz; i++) {
/*4979*/      if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ')
/*4980*/        return false; 
/*   0*/    } 
/*4983*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isWhitespace(String str) {
/*5006*/    if (str == null)
/*5007*/      return false; 
/*5009*/    int sz = str.length();
/*5010*/    for (int i = 0; i < sz; i++) {
/*5011*/      if (!Character.isWhitespace(str.charAt(i)))
/*5012*/        return false; 
/*   0*/    } 
/*5015*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAllLowerCase(String str) {
/*5036*/    if (str == null || isEmpty(str))
/*5037*/      return false; 
/*5039*/    int sz = str.length();
/*5040*/    for (int i = 0; i < sz; i++) {
/*5041*/      if (!Character.isLowerCase(str.charAt(i)))
/*5042*/        return false; 
/*   0*/    } 
/*5045*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAllUpperCase(String str) {
/*5066*/    if (str == null || isEmpty(str))
/*5067*/      return false; 
/*5069*/    int sz = str.length();
/*5070*/    for (int i = 0; i < sz; i++) {
/*5071*/      if (!Character.isUpperCase(str.charAt(i)))
/*5072*/        return false; 
/*   0*/    } 
/*5075*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static String defaultString(String str) {
/*5097*/    return (str == null) ? "" : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String defaultString(String str, String defaultStr) {
/*5118*/    return (str == null) ? defaultStr : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String defaultIfEmpty(String str, String defaultStr) {
/*5139*/    return isEmpty(str) ? defaultStr : str;
/*   0*/  }
/*   0*/  
/*   0*/  public static String reverse(String str) {
/*5159*/    if (str == null)
/*5160*/      return null; 
/*5162*/    return new StringBuilder(str).reverse().toString();
/*   0*/  }
/*   0*/  
/*   0*/  public static String reverseDelimited(String str, char separatorChar) {
/*5185*/    if (str == null)
/*5186*/      return null; 
/*5190*/    String[] strs = split(str, separatorChar);
/*5191*/    ArrayUtils.reverse((Object[])strs);
/*5192*/    return join((Object[])strs, separatorChar);
/*   0*/  }
/*   0*/  
/*   0*/  public static String abbreviate(String str, int maxWidth) {
/*5230*/    return abbreviate(str, 0, maxWidth);
/*   0*/  }
/*   0*/  
/*   0*/  public static String abbreviate(String str, int offset, int maxWidth) {
/*5269*/    if (str == null)
/*5270*/      return null; 
/*5272*/    if (maxWidth < 4)
/*5273*/      throw new IllegalArgumentException("Minimum abbreviation width is 4"); 
/*5275*/    if (str.length() <= maxWidth)
/*5276*/      return str; 
/*5278*/    if (offset > str.length())
/*5279*/      offset = str.length(); 
/*5281*/    if (str.length() - offset < maxWidth - 3)
/*5282*/      offset = str.length() - maxWidth - 3; 
/*5284*/    if (offset <= 4)
/*5285*/      return str.substring(0, maxWidth - 3) + "..."; 
/*5287*/    if (maxWidth < 7)
/*5288*/      throw new IllegalArgumentException("Minimum abbreviation width with offset is 7"); 
/*5290*/    if (offset + maxWidth - 3 < str.length())
/*5291*/      return "..." + abbreviate(str.substring(offset), maxWidth - 3); 
/*5293*/    return "..." + str.substring(str.length() - maxWidth - 3);
/*   0*/  }
/*   0*/  
/*   0*/  public static String difference(String str1, String str2) {
/*5324*/    if (str1 == null)
/*5325*/      return str2; 
/*5327*/    if (str2 == null)
/*5328*/      return str1; 
/*5330*/    int at = indexOfDifference(str1, str2);
/*5331*/    if (at == -1)
/*5332*/      return ""; 
/*5334*/    return str2.substring(at);
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfDifference(String str1, String str2) {
/*5361*/    if (str1 == str2)
/*5362*/      return -1; 
/*5364*/    if (str1 == null || str2 == null)
/*5365*/      return 0; 
/*   0*/    int i;
/*5368*/    for (i = 0; i < str1.length() && i < str2.length() && 
/*5369*/      str1.charAt(i) == str2.charAt(i); i++);
/*5373*/    if (i < str2.length() || i < str1.length())
/*5374*/      return i; 
/*5376*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  public static int indexOfDifference(String[] strs) {
/*5411*/    if (strs == null || strs.length <= 1)
/*5412*/      return -1; 
/*   0*/    boolean anyStringNull = false;
/*   0*/    boolean allStringsNull = true;
/*5416*/    int arrayLen = strs.length;
/*5417*/    int shortestStrLen = Integer.MAX_VALUE;
/*5418*/    int longestStrLen = 0;
/*5423*/    for (int i = 0; i < arrayLen; i++) {
/*5424*/      if (strs[i] == null) {
/*5425*/        anyStringNull = true;
/*5426*/        shortestStrLen = 0;
/*   0*/      } else {
/*5428*/        allStringsNull = false;
/*5429*/        shortestStrLen = Math.min(strs[i].length(), shortestStrLen);
/*5430*/        longestStrLen = Math.max(strs[i].length(), longestStrLen);
/*   0*/      } 
/*   0*/    } 
/*5435*/    if (allStringsNull || (longestStrLen == 0 && !anyStringNull))
/*5436*/      return -1; 
/*5440*/    if (shortestStrLen == 0)
/*5441*/      return 0; 
/*5445*/    int firstDiff = -1;
/*5446*/    for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
/*5447*/      char comparisonChar = strs[0].charAt(stringPos);
/*5448*/      for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
/*5449*/        if (strs[arrayPos].charAt(stringPos) != comparisonChar) {
/*5450*/          firstDiff = stringPos;
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/*5454*/      if (firstDiff != -1)
/*   0*/        break; 
/*   0*/    } 
/*5459*/    if (firstDiff == -1 && shortestStrLen != longestStrLen)
/*5463*/      return shortestStrLen; 
/*5465*/    return firstDiff;
/*   0*/  }
/*   0*/  
/*   0*/  public static String getCommonPrefix(String[] strs) {
/*5502*/    if (strs == null || strs.length == 0)
/*5503*/      return ""; 
/*5505*/    int smallestIndexOfDiff = indexOfDifference(strs);
/*5506*/    if (smallestIndexOfDiff == -1) {
/*5508*/      if (strs[0] == null)
/*5509*/        return ""; 
/*5511*/      return strs[0];
/*   0*/    } 
/*5512*/    if (smallestIndexOfDiff == 0)
/*5514*/      return ""; 
/*5517*/    return strs[0].substring(0, smallestIndexOfDiff);
/*   0*/  }
/*   0*/  
/*   0*/  public static int getLevenshteinDistance(String s, String t) {
/*5558*/    if (s == null || t == null)
/*5559*/      throw new IllegalArgumentException("Strings must not be null"); 
/*5579*/    int n = s.length();
/*5580*/    int m = t.length();
/*5582*/    if (n == 0)
/*5583*/      return m; 
/*5584*/    if (m == 0)
/*5585*/      return n; 
/*5588*/    if (n > m) {
/*5590*/      String tmp = s;
/*5591*/      s = t;
/*5592*/      t = tmp;
/*5593*/      n = m;
/*5594*/      m = t.length();
/*   0*/    } 
/*5597*/    int[] p = new int[n + 1];
/*5598*/    int[] d = new int[n + 1];
/*5609*/    for (int i = 0; i <= n; i++)
/*5610*/      p[i] = i; 
/*5613*/    for (int j = 1; j <= m; j++) {
/*5614*/      char t_j = t.charAt(j - 1);
/*5615*/      d[0] = j;
/*5617*/      for (int k = 1; k <= n; k++) {
/*5618*/        int cost = (s.charAt(k - 1) == t_j) ? 0 : 1;
/*5620*/        d[k] = Math.min(Math.min(d[k - 1] + 1, p[k] + 1), p[k - 1] + cost);
/*   0*/      } 
/*5624*/      int[] _d = p;
/*5625*/      p = d;
/*5626*/      d = _d;
/*   0*/    } 
/*5631*/    return p[n];
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean startsWith(String str, String prefix) {
/*5680*/    return startsWith(str, prefix, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean startsWithIgnoreCase(String str, String prefix) {
/*5705*/    return startsWith(str, prefix, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
/*5720*/    if (str == null || prefix == null)
/*5721*/      return (str == null && prefix == null); 
/*5723*/    if (prefix.length() > str.length())
/*5724*/      return false; 
/*5726*/    return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean startsWithAny(String string, String[] searchStrings) {
/*5748*/    if (isEmpty(string) || ArrayUtils.isEmpty(searchStrings))
/*5749*/      return false; 
/*5751*/    for (int i = 0; i < searchStrings.length; i++) {
/*5752*/      String searchString = searchStrings[i];
/*5753*/      if (startsWith(string, searchString))
/*5754*/        return true; 
/*   0*/    } 
/*5757*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean endsWith(String str, String suffix) {
/*5786*/    return endsWith(str, suffix, false);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean endsWithIgnoreCase(String str, String suffix) {
/*5812*/    return endsWith(str, suffix, true);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
/*5827*/    if (str == null || suffix == null)
/*5828*/      return (str == null && suffix == null); 
/*5830*/    if (suffix.length() > str.length())
/*5831*/      return false; 
/*5833*/    int strOffset = str.length() - suffix.length();
/*5834*/    return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
/*   0*/  }
/*   0*/}
