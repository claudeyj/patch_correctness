/*   0*/package org.apache.commons.lang;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class LocaleUtils {
/*   0*/  private static final List cAvailableLocaleList;
/*   0*/  
/*   0*/  private static Set cAvailableLocaleSet;
/*   0*/  
/*  47*/  private static final Map cLanguagesByCountry = Collections.synchronizedMap(new HashMap());
/*   0*/  
/*  49*/  private static final Map cCountriesByLanguage = Collections.synchronizedMap(new HashMap());
/*   0*/  
/*   0*/  static {
/*  51*/    List list = Arrays.asList(Locale.getAvailableLocales());
/*  52*/    cAvailableLocaleList = Collections.unmodifiableList(list);
/*   0*/  }
/*   0*/  
/*   0*/  public static Locale toLocale(String str) {
/*  95*/    if (str == null)
/*  96*/      return null; 
/*  98*/    int len = str.length();
/*  99*/    if (len != 2 && len != 5 && len < 7)
/* 100*/      throw new IllegalArgumentException("Invalid locale format: " + str); 
/* 102*/    char ch0 = str.charAt(0);
/* 103*/    char ch1 = str.charAt(1);
/* 104*/    if (ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z')
/* 105*/      throw new IllegalArgumentException("Invalid locale format: " + str); 
/* 107*/    if (len == 2)
/* 108*/      return new Locale(str, ""); 
/* 110*/    if (str.charAt(2) != '_')
/* 111*/      throw new IllegalArgumentException("Invalid locale format: " + str); 
/* 113*/    char ch3 = str.charAt(3);
/* 114*/    char ch4 = str.charAt(4);
/* 115*/    if (ch3 < 'A' || ch3 > 'Z' || ch4 < 'A' || ch4 > 'Z')
/* 116*/      throw new IllegalArgumentException("Invalid locale format: " + str); 
/* 118*/    if (len == 5)
/* 119*/      return new Locale(str.substring(0, 2), str.substring(3, 5)); 
/* 121*/    if (str.charAt(5) != '_')
/* 122*/      throw new IllegalArgumentException("Invalid locale format: " + str); 
/* 124*/    return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
/*   0*/  }
/*   0*/  
/*   0*/  public static List localeLookupList(Locale locale) {
/* 143*/    return localeLookupList(locale, locale);
/*   0*/  }
/*   0*/  
/*   0*/  public static List localeLookupList(Locale locale, Locale defaultLocale) {
/* 165*/    List list = new ArrayList(4);
/* 166*/    if (locale != null) {
/* 167*/      list.add(locale);
/* 168*/      if (locale.getVariant().length() > 0)
/* 169*/        list.add(new Locale(locale.getLanguage(), locale.getCountry())); 
/* 171*/      if (locale.getCountry().length() > 0)
/* 172*/        list.add(new Locale(locale.getLanguage(), "")); 
/* 174*/      if (!list.contains(defaultLocale))
/* 175*/        list.add(defaultLocale); 
/*   0*/    } 
/* 178*/    return Collections.unmodifiableList(list);
/*   0*/  }
/*   0*/  
/*   0*/  public static List availableLocaleList() {
/* 192*/    return cAvailableLocaleList;
/*   0*/  }
/*   0*/  
/*   0*/  public static Set availableLocaleSet() {
/* 206*/    Set set = cAvailableLocaleSet;
/* 207*/    if (set == null) {
/* 208*/      set = new HashSet(availableLocaleList());
/* 209*/      set = Collections.unmodifiableSet(set);
/* 210*/      cAvailableLocaleSet = set;
/*   0*/    } 
/* 212*/    return set;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isAvailableLocale(Locale locale) {
/* 223*/    Locale locale1 = locale;
/* 223*/    return (cAvailableLocaleSet == null) ? false : cAvailableLocaleSet.contains(locale1);
/*   0*/  }
/*   0*/  
/*   0*/  public static List languagesByCountry(String countryCode) {
/* 237*/    List langs = (List)cLanguagesByCountry.get(countryCode);
/* 238*/    if (langs == null) {
/* 239*/      if (countryCode != null) {
/* 240*/        langs = new ArrayList();
/* 241*/        List locales = availableLocaleList();
/* 242*/        for (int i = 0; i < locales.size(); i++) {
/* 243*/          Locale locale = (Locale)locales.get(i);
/* 244*/          if (countryCode.equals(locale.getCountry()) && locale.getVariant().length() == 0)
/* 246*/            langs.add(locale); 
/*   0*/        } 
/* 249*/        langs = Collections.unmodifiableList(langs);
/*   0*/      } else {
/* 251*/        langs = Collections.EMPTY_LIST;
/*   0*/      } 
/* 253*/      cLanguagesByCountry.put(countryCode, langs);
/*   0*/    } 
/* 255*/    return langs;
/*   0*/  }
/*   0*/  
/*   0*/  public static List countriesByLanguage(String languageCode) {
/* 269*/    List countries = (List)cCountriesByLanguage.get(languageCode);
/* 270*/    if (countries == null) {
/* 271*/      if (languageCode != null) {
/* 272*/        countries = new ArrayList();
/* 273*/        List locales = availableLocaleList();
/* 274*/        for (int i = 0; i < locales.size(); i++) {
/* 275*/          Locale locale = (Locale)locales.get(i);
/* 276*/          if (languageCode.equals(locale.getLanguage()) && locale.getCountry().length() != 0 && locale.getVariant().length() == 0)
/* 279*/            countries.add(locale); 
/*   0*/        } 
/* 282*/        countries = Collections.unmodifiableList(countries);
/*   0*/      } else {
/* 284*/        countries = Collections.EMPTY_LIST;
/*   0*/      } 
/* 286*/      cCountriesByLanguage.put(languageCode, countries);
/*   0*/    } 
/* 288*/    return countries;
/*   0*/  }
/*   0*/}
