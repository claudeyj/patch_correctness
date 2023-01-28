/*   0*/package org.apache.commons.codec.language;
/*   0*/
/*   0*/import org.apache.commons.codec.EncoderException;
/*   0*/import org.apache.commons.codec.StringEncoder;
/*   0*/
/*   0*/public class Soundex implements StringEncoder {
/*   0*/  public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
/*   0*/  
/*  52*/  private static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
/*   0*/  
/*  59*/  public static final Soundex US_ENGLISH = new Soundex();
/*   0*/  
/*   0*/  @Deprecated
/*  66*/  private int maxLength = 4;
/*   0*/  
/*   0*/  private final char[] soundexMapping;
/*   0*/  
/*   0*/  public Soundex() {
/*  82*/    this.soundexMapping = US_ENGLISH_MAPPING;
/*   0*/  }
/*   0*/  
/*   0*/  public Soundex(char[] mapping) {
/*  96*/    this.soundexMapping = new char[mapping.length];
/*  97*/    System.arraycopy(mapping, 0, this.soundexMapping, 0, mapping.length);
/*   0*/  }
/*   0*/  
/*   0*/  public Soundex(String mapping) {
/* 109*/    this.soundexMapping = mapping.toCharArray();
/*   0*/  }
/*   0*/  
/*   0*/  public int difference(String s1, String s2) throws EncoderException {
/* 132*/    return SoundexUtils.difference(this, s1, s2);
/*   0*/  }
/*   0*/  
/*   0*/  public Object encode(Object obj) throws EncoderException {
/* 150*/    if (!(obj instanceof String)) {
/* 151*/        throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String"); 
/*   0*/       }
/* 153*/    return soundex((String)obj);
/*   0*/  }
/*   0*/  
/*   0*/  public String encode(String str) {
/* 167*/    return soundex(str);
/*   0*/  }
/*   0*/  
/*   0*/  private char getMappingCode(String str, int index) {
/* 185*/    char mappedChar = map(str.charAt(index));
/* 187*/    if (index > 1 && mappedChar != '0') {
/* 188*/      char hwChar = str.charAt(index - 1);
/* 189*/      if ('H' == hwChar || 'W' == hwChar) {
/* 190*/        char preHWChar = str.charAt(index - 2);
/* 191*/        char firstCode = map(preHWChar);
/* 192*/        if (firstCode == mappedChar || 'H' == preHWChar || 'W' == preHWChar) {
/* 193*/            return Character.MIN_VALUE; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 197*/    return mappedChar;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public int getMaxLength() {
/* 208*/    return this.maxLength;
/*   0*/  }
/*   0*/  
/*   0*/  private char[] getSoundexMapping() {
/* 217*/    return this.soundexMapping;
/*   0*/  }
/*   0*/  
/*   0*/  private char map(char ch) {
/* 230*/    int index = ch - 65;
/* 231*/    if (index < 0 || index >= (getSoundexMapping()).length) {
/* 232*/        throw new IllegalArgumentException("The character is not mapped: " + ch); 
/*   0*/       }
/* 234*/    return getSoundexMapping()[index];
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void setMaxLength(int maxLength) {
/* 246*/    this.maxLength = maxLength;
/*   0*/  }
/*   0*/  
/*   0*/  public String soundex(String str) {
/* 259*/    if (str == null) {
/* 260*/        return null; 
/*   0*/       }
/* 262*/    str = SoundexUtils.clean(str);
/* 263*/    if (str.length() == 0) {
/* 264*/        return str; 
/*   0*/       }
/* 266*/    char[] out = { '0', '0', '0', '0' };
/* 268*/    int incount = 1, count = 1;
/* 269*/    out[0] = str.charAt(0);
/* 271*/    char last = getMappingCode(str, 0);
/* 272*/    while (incount < str.length() && count < out.length) {
/* 273*/      char mapped = getMappingCode(str, incount++);
/* 274*/      if (mapped != '\000') {
/* 275*/        if (mapped != '0' && mapped != last) {
/* 276*/            out[count++] = mapped; 
/*   0*/           }
/* 278*/        last = mapped;
/*   0*/      } 
/*   0*/    } 
/* 281*/    return new String(out);
/*   0*/  }
/*   0*/}
