/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.nio.charset.CharsetEncoder;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Map;
/*   0*/import java.util.MissingResourceException;
/*   0*/import java.util.Properties;
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/import org.jsoup.parser.Parser;
/*   0*/
/*   0*/public class Entities {
/*   0*/  public enum EscapeMode {
/*  19*/    xhtml(Entities.xhtmlByVal),
/*  21*/    base(Entities.baseByVal),
/*  23*/    extended(Entities.fullByVal);
/*   0*/    
/*   0*/    private Map<Character, String> map;
/*   0*/    
/*   0*/    EscapeMode(Map<Character, String> map) {
/*  28*/      this.map = map;
/*   0*/    }
/*   0*/    
/*   0*/    public Map<Character, String> getMap() {
/*  32*/      return this.map;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isNamedEntity(String name) {
/*  50*/    return full.containsKey(name);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isBaseNamedEntity(String name) {
/*  60*/    return base.containsKey(name);
/*   0*/  }
/*   0*/  
/*   0*/  public static Character getCharacterByName(String name) {
/*  69*/    return full.get(name);
/*   0*/  }
/*   0*/  
/*   0*/  static String escape(String string, Document.OutputSettings out) {
/*  73*/    StringBuilder accum = new StringBuilder(string.length() * 2);
/*  74*/    escape(accum, string, out, false, false, false);
/*  75*/    return accum.toString();
/*   0*/  }
/*   0*/  
/*   0*/  static void escape(StringBuilder accum, String string, Document.OutputSettings out, boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite) {
/*   0*/    int codePoint;
/*   0*/    boolean lastWasWhite = false;
/*   0*/    boolean reachedNonWhite = false;
/*  84*/    EscapeMode escapeMode = out.escapeMode();
/*  85*/    CharsetEncoder encoder = out.encoder();
/*  86*/    CoreCharset coreCharset = CoreCharset.byName(encoder.charset().name());
/*  87*/    Map<Character, String> map = escapeMode.getMap();
/*  88*/    int length = string.length();
/*  91*/    for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
/*  92*/      codePoint = string.codePointAt(offset);
/*  94*/      if (normaliseWhite) {
/*  95*/        if (StringUtil.isWhitespace(codePoint)) {
/*  96*/          if ((!stripLeadingWhite || reachedNonWhite) && !lastWasWhite) {
/*  98*/            accum.append(' ');
/*  99*/            lastWasWhite = true;
/*   0*/          } 
/*   0*/          continue;
/*   0*/        } 
/* 102*/        lastWasWhite = false;
/* 103*/        reachedNonWhite = true;
/*   0*/      } 
/* 107*/      if (codePoint < 65536) {
/* 108*/        char c = (char)codePoint;
/* 110*/        switch (c) {
/*   0*/          case '&':
/* 112*/            accum.append("&amp;");
/*   0*/            break;
/*   0*/          case '\u00A0':
/* 115*/            if (escapeMode == EscapeMode.xhtml) {
/* 116*/              accum.append("&nbsp;");
/*   0*/              break;
/*   0*/            } 
/* 118*/            accum.append(c);
/*   0*/            break;
/*   0*/          case '<':
/* 121*/            if (!inAttribute) {
/* 122*/              accum.append("&lt;");
/*   0*/              break;
/*   0*/            } 
/* 124*/            accum.append(c);
/*   0*/            break;
/*   0*/          case '>':
/* 127*/            if (!inAttribute) {
/* 128*/              accum.append("&gt;");
/*   0*/              break;
/*   0*/            } 
/* 130*/            accum.append(c);
/*   0*/            break;
/*   0*/          case '"':
/* 133*/            if (inAttribute) {
/* 134*/              accum.append("&quot;");
/*   0*/              break;
/*   0*/            } 
/* 136*/            accum.append(c);
/*   0*/            break;
/*   0*/          default:
/* 139*/            if (canEncode(coreCharset, c, encoder)) {
/* 140*/              accum.append(c);
/*   0*/              break;
/*   0*/            } 
/* 141*/            if (map.containsKey(c)) {
/* 142*/              accum.append('&').append(map.get(c)).append(';');
/*   0*/              break;
/*   0*/            } 
/* 144*/            accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
/*   0*/            break;
/*   0*/        } 
/*   0*/      } else {
/* 147*/        String c = new String(Character.toChars(codePoint));
/* 148*/        if (encoder.canEncode(c)) {
/* 149*/          accum.append(c);
/*   0*/        } else {
/* 151*/          accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static String unescape(String string) {
/* 157*/    return unescape(string, false);
/*   0*/  }
/*   0*/  
/*   0*/  static String unescape(String string, boolean strict) {
/* 167*/    return Parser.unescapeEntities(string, strict);
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean canEncode(CoreCharset charset, char c, CharsetEncoder fallback) {
/* 186*/    switch (charset) {
/*   0*/      case ascii:
/* 188*/        return (c < '\u0080');
/*   0*/      case utf:
/* 190*/        return true;
/*   0*/    } 
/* 192*/    return fallback.canEncode(c);
/*   0*/  }
/*   0*/  
/*   0*/  private enum CoreCharset {
/* 197*/    ascii, utf, fallback;
/*   0*/    
/*   0*/    private static CoreCharset byName(String name) {
/* 200*/      if (name.equals("US-ASCII")) {
/* 201*/          return ascii; 
/*   0*/         }
/* 202*/      if (name.startsWith("UTF-")) {
/* 203*/          return utf; 
/*   0*/         }
/* 204*/      return fallback;
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 210*/  private static final Object[][] xhtmlArray = new Object[][] { { "quot", 34 }, { "amp", 38 }, { "lt", 60 }, { "gt", 62 } };
/*   0*/  
/* 218*/  private static final Map<Character, String> xhtmlByVal = new HashMap<Character, String>();
/*   0*/  
/* 219*/  private static final Map<String, Character> base = loadEntities("entities-base.properties");
/*   0*/  
/* 220*/  private static final Map<Character, String> baseByVal = toCharacterKey(base);
/*   0*/  
/* 221*/  private static final Map<String, Character> full = loadEntities("entities-full.properties");
/*   0*/  
/* 222*/  private static final Map<Character, String> fullByVal = toCharacterKey(full);
/*   0*/  
/*   0*/  static {
/* 224*/    for (Object[] entity : xhtmlArray) {
/* 225*/      Character c = (char)(Integer)entity[1];
/* 226*/      xhtmlByVal.put(c, (String)entity[0]);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static Map<String, Character> loadEntities(String filename) {
/* 231*/    Properties properties = new Properties();
/* 232*/    Map<String, Character> entities = new HashMap<String, Character>();
/*   0*/    try {
/* 234*/      InputStream in = Entities.class.getResourceAsStream(filename);
/* 235*/      properties.load(in);
/* 236*/      in.close();
/* 237*/    } catch (IOException e) {
/* 238*/      throw new MissingResourceException("Error loading entities resource: " + e.getMessage(), "Entities", filename);
/*   0*/    } 
/* 241*/    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
/* 242*/      Character val = (char)Integer.parseInt((String)entry.getValue(), 16);
/* 243*/      String name = (String)entry.getKey();
/* 244*/      entities.put(name, val);
/*   0*/    } 
/* 246*/    return entities;
/*   0*/  }
/*   0*/  
/*   0*/  private static Map<Character, String> toCharacterKey(Map<String, Character> inMap) {
/* 250*/    Map<Character, String> outMap = new HashMap<Character, String>();
/* 251*/    for (Map.Entry<String, Character> entry : inMap.entrySet()) {
/* 252*/      Character character = entry.getValue();
/* 253*/      String name = entry.getKey();
/* 255*/      if (outMap.containsKey(character)) {
/* 257*/        if (name.toLowerCase().equals(name)) {
/* 258*/            outMap.put(character, name); 
/*   0*/           }
/*   0*/        continue;
/*   0*/      } 
/* 260*/      outMap.put(character, name);
/*   0*/    } 
/* 263*/    return outMap;
/*   0*/  }
/*   0*/}
