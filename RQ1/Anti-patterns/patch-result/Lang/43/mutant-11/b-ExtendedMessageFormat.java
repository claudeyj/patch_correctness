/*   0*/package org.apache.commons.lang.text;
/*   0*/
/*   0*/import java.text.Format;
/*   0*/import java.text.MessageFormat;
/*   0*/import java.text.ParsePosition;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class ExtendedMessageFormat extends MessageFormat {
/*   0*/  private static final long serialVersionUID = -2362048321261811743L;
/*   0*/  
/*   0*/  private static final String DUMMY_PATTERN = "";
/*   0*/  
/*   0*/  private static final String ESCAPED_QUOTE = "''";
/*   0*/  
/*   0*/  private static final char START_FMT = ',';
/*   0*/  
/*   0*/  private static final char END_FE = '}';
/*   0*/  
/*   0*/  private static final char START_FE = '{';
/*   0*/  
/*   0*/  private static final char QUOTE = '\'';
/*   0*/  
/*   0*/  private String toPattern;
/*   0*/  
/*   0*/  private Map registry;
/*   0*/  
/*   0*/  public ExtendedMessageFormat(String pattern) {
/*  90*/    this(pattern, Locale.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  public ExtendedMessageFormat(String pattern, Locale locale) {
/* 101*/    this(pattern, locale, null);
/*   0*/  }
/*   0*/  
/*   0*/  public ExtendedMessageFormat(String pattern, Map registry) {
/* 112*/    this(pattern, Locale.getDefault(), registry);
/*   0*/  }
/*   0*/  
/*   0*/  public ExtendedMessageFormat(String pattern, Locale locale, Map registry) {
/* 124*/    super("");
/* 125*/    setLocale(locale);
/* 126*/    this.registry = registry;
/* 127*/    applyPattern(pattern);
/*   0*/  }
/*   0*/  
/*   0*/  public String toPattern() {
/* 134*/    return this.toPattern;
/*   0*/  }
/*   0*/  
/*   0*/  public final void applyPattern(String pattern) {
/* 143*/    if (this.registry == null) {
/* 144*/      super.applyPattern(pattern);
/* 145*/      this.toPattern = super.toPattern();
/*   0*/      return;
/*   0*/    } 
/* 148*/    ArrayList foundFormats = new ArrayList();
/* 149*/    ArrayList foundDescriptions = new ArrayList();
/* 150*/    StringBuffer stripCustom = new StringBuffer(pattern.length());
/* 152*/    ParsePosition pos = new ParsePosition(0);
/* 153*/    char[] c = pattern.toCharArray();
/* 154*/    int fmtCount = 0;
/* 155*/    while (pos.getIndex() < pattern.length()) {
/* 156*/      switch (c[pos.getIndex()]) {
/*   0*/      
/*   0*/      } 
/* 158*/      appendQuotedString(pattern, pos, stripCustom, true);
/*   0*/    } 
/* 191*/    super.applyPattern(stripCustom.toString());
/* 192*/    this.toPattern = insertFormats(super.toPattern(), foundDescriptions);
/* 193*/    if (containsElements(foundFormats)) {
/* 194*/      Format[] origFormats = getFormats();
/* 197*/      int i = 0;
/* 198*/      for (Iterator it = foundFormats.iterator(); it.hasNext(); i++) {
/* 199*/        Format f = (Format)it.next();
/* 200*/        if (f != null)
/* 201*/          origFormats[i] = f; 
/*   0*/      } 
/* 204*/      super.setFormats(origFormats);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setFormat(int formatElementIndex, Format newFormat) {
/* 213*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
/* 221*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public void setFormats(Format[] newFormats) {
/* 229*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  public void setFormatsByArgumentIndex(Format[] newFormats) {
/* 237*/    throw new UnsupportedOperationException();
/*   0*/  }
/*   0*/  
/*   0*/  private Format getFormat(String desc) {
/* 247*/    if (this.registry != null) {
/* 248*/      String name = desc;
/* 249*/      String args = null;
/* 250*/      int i = desc.indexOf(',');
/* 251*/      if (i > 0) {
/* 252*/        name = desc.substring(0, i).trim();
/* 253*/        args = desc.substring(i + 1).trim();
/*   0*/      } 
/* 255*/      FormatFactory factory = (FormatFactory)this.registry.get(name);
/* 256*/      if (factory != null)
/* 257*/        return factory.getFormat(name, args, getLocale()); 
/*   0*/    } 
/* 260*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  private int readArgumentIndex(String pattern, ParsePosition pos) {
/* 271*/    int start = pos.getIndex();
/* 272*/    seekNonWs(pattern, pos);
/* 273*/    StringBuffer result = new StringBuffer();
/*   0*/    boolean error = false;
/* 275*/    for (; !error && pos.getIndex() < pattern.length(); next(pos)) {
/* 276*/      char c = pattern.charAt(pos.getIndex());
/* 277*/      if (Character.isWhitespace(c)) {
/* 278*/        seekNonWs(pattern, pos);
/* 279*/        c = pattern.charAt(pos.getIndex());
/* 280*/        if (c != ',' && c != '}') {
/* 281*/          error = true;
/*   0*/          continue;
/*   0*/        } 
/*   0*/      } 
/* 285*/      if ((c == ',' || c == '}') && result.length() > 0)
/*   0*/        try {
/* 287*/          return Integer.parseInt(result.toString());
/* 288*/        } catch (NumberFormatException numberFormatException) {} 
/* 293*/      error = !Character.isDigit(c);
/* 294*/      result.append(c);
/*   0*/    } 
/* 296*/    if (error)
/* 297*/      throw new IllegalArgumentException("Invalid format argument index at position " + start + ": " + pattern.substring(start, pos.getIndex())); 
/* 301*/    throw new IllegalArgumentException("Unterminated format element at position " + start);
/*   0*/  }
/*   0*/  
/*   0*/  private String parseFormatDescription(String pattern, ParsePosition pos) {
/* 313*/    int start = pos.getIndex();
/* 314*/    seekNonWs(pattern, pos);
/* 315*/    int text = pos.getIndex();
/* 316*/    int depth = 1;
/* 317*/    for (; pos.getIndex() < pattern.length(); next(pos)) {
/* 318*/      switch (pattern.charAt(pos.getIndex())) {
/*   0*/        case '{':
/* 320*/          depth++;
/*   0*/          break;
/*   0*/        case '}':
/* 323*/          depth--;
/* 324*/          if (depth == 0)
/* 325*/            return pattern.substring(text, pos.getIndex()); 
/*   0*/          break;
/*   0*/        case '\'':
/* 329*/          getQuotedString(pattern, pos, false);
/*   0*/          break;
/*   0*/      } 
/*   0*/    } 
/* 333*/    throw new IllegalArgumentException("Unterminated format element at position " + start);
/*   0*/  }
/*   0*/  
/*   0*/  private String insertFormats(String pattern, ArrayList customPatterns) {
/* 345*/    if (!containsElements(customPatterns))
/* 346*/      return pattern; 
/* 348*/    StringBuffer sb = new StringBuffer(pattern.length() * 2);
/* 349*/    ParsePosition pos = new ParsePosition(0);
/* 350*/    int fe = -1;
/* 351*/    int depth = 0;
/* 352*/    while (pos.getIndex() < pattern.length()) {
/* 353*/      char c = pattern.charAt(pos.getIndex());
/* 354*/      switch (c) {
/*   0*/        case '\'':
/* 356*/          appendQuotedString(pattern, pos, sb, false);
/*   0*/          continue;
/*   0*/        case '{':
/* 359*/          depth++;
/* 360*/          if (depth == 1) {
/* 361*/            fe++;
/* 362*/            sb.append('{').append(readArgumentIndex(pattern, next(pos)));
/* 364*/            String customPattern = (String)customPatterns.get(fe);
/* 365*/            if (customPattern != null)
/* 366*/              sb.append(',').append(customPattern); 
/*   0*/          } 
/*   0*/          continue;
/*   0*/        case '}':
/* 371*/          depth--;
/*   0*/          break;
/*   0*/      } 
/* 374*/      sb.append(c);
/* 375*/      next(pos);
/*   0*/    } 
/* 378*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  private void seekNonWs(String pattern, ParsePosition pos) {
/* 388*/    int len = 0;
/* 389*/    char[] buffer = pattern.toCharArray();
/*   0*/    do {
/* 391*/      len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
/* 392*/      pos.setIndex(pos.getIndex() + len);
/* 393*/    } while (len > 0 && pos.getIndex() < pattern.length());
/*   0*/  }
/*   0*/  
/*   0*/  private ParsePosition next(ParsePosition pos) {
/* 403*/    pos.setIndex(pos.getIndex() + 1);
/* 404*/    return pos;
/*   0*/  }
/*   0*/  
/*   0*/  private StringBuffer appendQuotedString(String pattern, ParsePosition pos, StringBuffer appendTo, boolean escapingOn) {
/* 419*/    int start = pos.getIndex();
/* 420*/    char[] c = pattern.toCharArray();
/* 421*/    if (escapingOn && c[start] == '\'')
/* 422*/      return (appendTo == null) ? null : appendTo.append('\''); 
/* 424*/    int lastHold = start;
/* 425*/    for (int i = pos.getIndex(); i < pattern.length(); i++) {
/* 426*/      if (escapingOn && pattern.substring(i).startsWith("''")) {
/* 427*/        appendTo.append(c, lastHold, pos.getIndex() - lastHold).append('\'');
/* 429*/        pos.setIndex(i + "''".length());
/* 430*/        lastHold = pos.getIndex();
/*   0*/      } else {
/* 433*/        switch (c[pos.getIndex()]) {
/*   0*/          case '\'':
/* 435*/            next(pos);
/* 436*/            return (appendTo == null) ? null : appendTo.append(c, lastHold, pos.getIndex() - lastHold);
/*   0*/        } 
/* 439*/        next(pos);
/*   0*/      } 
/*   0*/    } 
/* 442*/    throw new IllegalArgumentException("Unterminated quoted string at position " + start);
/*   0*/  }
/*   0*/  
/*   0*/  private void getQuotedString(String pattern, ParsePosition pos, boolean escapingOn) {
/* 455*/    appendQuotedString(pattern, pos, null, escapingOn);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean containsElements(Collection coll) {
/* 464*/    if (coll == null || coll.size() == 0)
/* 465*/      return false; 
/* 467*/    for (Iterator iter = coll.iterator(); iter.hasNext();) {
/* 468*/      if (iter.next() != null)
/* 469*/        return true; 
/*   0*/    } 
/* 472*/    return false;
/*   0*/  }
/*   0*/}
