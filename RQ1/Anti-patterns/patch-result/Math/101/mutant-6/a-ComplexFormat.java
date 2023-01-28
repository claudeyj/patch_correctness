/*   0*/package org.apache.commons.math.complex;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.text.FieldPosition;
/*   0*/import java.text.Format;
/*   0*/import java.text.NumberFormat;
/*   0*/import java.text.ParseException;
/*   0*/import java.text.ParsePosition;
/*   0*/import java.util.Locale;
/*   0*/
/*   0*/public class ComplexFormat extends Format implements Serializable {
/*   0*/  private static final long serialVersionUID = -6337346779577272306L;
/*   0*/  
/*   0*/  private static final String DEFAULT_IMAGINARY_CHARACTER = "i";
/*   0*/  
/*   0*/  private String imaginaryCharacter;
/*   0*/  
/*   0*/  private NumberFormat imaginaryFormat;
/*   0*/  
/*   0*/  private NumberFormat realFormat;
/*   0*/  
/*   0*/  public ComplexFormat() {
/*  58*/    this("i", getDefaultNumberFormat());
/*   0*/  }
/*   0*/  
/*   0*/  public ComplexFormat(NumberFormat format) {
/*  67*/    this("i", format);
/*   0*/  }
/*   0*/  
/*   0*/  public ComplexFormat(NumberFormat realFormat, NumberFormat imaginaryFormat) {
/*  78*/    this("i", realFormat, imaginaryFormat);
/*   0*/  }
/*   0*/  
/*   0*/  public ComplexFormat(String imaginaryCharacter) {
/*  87*/    this(imaginaryCharacter, getDefaultNumberFormat());
/*   0*/  }
/*   0*/  
/*   0*/  public ComplexFormat(String imaginaryCharacter, NumberFormat format) {
/*  97*/    this(imaginaryCharacter, format, (NumberFormat)format.clone());
/*   0*/  }
/*   0*/  
/*   0*/  public ComplexFormat(String imaginaryCharacter, NumberFormat realFormat, NumberFormat imaginaryFormat) {
/* 111*/    setImaginaryCharacter(imaginaryCharacter);
/* 112*/    setImaginaryFormat(imaginaryFormat);
/* 113*/    setRealFormat(realFormat);
/*   0*/  }
/*   0*/  
/*   0*/  public static String formatComplex(Complex c) {
/* 124*/    return getInstance().format(c);
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer format(Complex complex, StringBuffer toAppendTo, FieldPosition pos) {
/* 139*/    pos.setBeginIndex(0);
/* 140*/    pos.setEndIndex(0);
/* 143*/    double re = complex.getReal();
/* 144*/    formatDouble(re, getRealFormat(), toAppendTo, pos);
/* 147*/    double im = complex.getImaginary();
/* 148*/    if (im < 0.0D) {
/* 149*/      toAppendTo.append(" - ");
/* 150*/      formatDouble(-im, getImaginaryFormat(), toAppendTo, pos);
/* 151*/      toAppendTo.append(getImaginaryCharacter());
/* 152*/    } else if (im > 0.0D || Double.isNaN(im)) {
/* 153*/      toAppendTo.append(" + ");
/* 154*/      formatDouble(im, getImaginaryFormat(), toAppendTo, pos);
/* 155*/      toAppendTo.append(getImaginaryCharacter());
/*   0*/    } 
/* 158*/    return toAppendTo;
/*   0*/  }
/*   0*/  
/*   0*/  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
/* 177*/    StringBuffer ret = null;
/* 179*/    if (obj instanceof Complex) {
/* 180*/      ret = format((Complex)obj, toAppendTo, pos);
/* 181*/    } else if (obj instanceof Number) {
/* 182*/      ret = format(new Complex(((Number)obj).doubleValue(), 0.0D), toAppendTo, pos);
/*   0*/    } else {
/* 185*/      throw new IllegalArgumentException("Cannot format given Object as a Date");
/*   0*/    } 
/* 189*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private StringBuffer formatDouble(double value, NumberFormat format, StringBuffer toAppendTo, FieldPosition pos) {
/* 211*/    if (Double.isNaN(value) || Double.isInfinite(value)) {
/* 212*/      toAppendTo.append('(');
/* 213*/      toAppendTo.append(value);
/* 214*/      toAppendTo.append(')');
/*   0*/    } else {
/* 216*/      format.format(value, toAppendTo, pos);
/*   0*/    } 
/* 218*/    return toAppendTo;
/*   0*/  }
/*   0*/  
/*   0*/  public static Locale[] getAvailableLocales() {
/* 227*/    return NumberFormat.getAvailableLocales();
/*   0*/  }
/*   0*/  
/*   0*/  private static NumberFormat getDefaultNumberFormat() {
/* 237*/    return getDefaultNumberFormat(Locale.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  private static NumberFormat getDefaultNumberFormat(Locale locale) {
/* 248*/    NumberFormat nf = NumberFormat.getInstance(locale);
/* 249*/    nf.setMaximumFractionDigits(2);
/* 250*/    return nf;
/*   0*/  }
/*   0*/  
/*   0*/  public String getImaginaryCharacter() {
/* 258*/    return this.imaginaryCharacter;
/*   0*/  }
/*   0*/  
/*   0*/  public NumberFormat getImaginaryFormat() {
/* 266*/    return this.imaginaryFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public static ComplexFormat getInstance() {
/* 274*/    return getInstance(Locale.getDefault());
/*   0*/  }
/*   0*/  
/*   0*/  public static ComplexFormat getInstance(Locale locale) {
/* 283*/    NumberFormat f = getDefaultNumberFormat(locale);
/* 284*/    return new ComplexFormat(f);
/*   0*/  }
/*   0*/  
/*   0*/  public NumberFormat getRealFormat() {
/* 292*/    return this.realFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public Complex parse(String source) throws ParseException {
/* 304*/    ParsePosition parsePosition = new ParsePosition(0);
/* 305*/    Complex result = parse(source, parsePosition);
/* 306*/    if (parsePosition.getIndex() == 0)
/* 307*/      throw new ParseException("Unparseable complex number: \"" + source + "\"", parsePosition.getErrorIndex()); 
/* 310*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Complex parse(String source, ParsePosition pos) {
/* 321*/    int initialIndex = pos.getIndex();
/* 324*/    parseAndIgnoreWhitespace(source, pos);
/* 327*/    Number re = parseNumber(source, getRealFormat(), pos);
/* 328*/    if (re == null) {
/* 332*/      pos.setIndex(initialIndex);
/* 333*/      return null;
/*   0*/    } 
/* 337*/    int startIndex = pos.getIndex();
/* 338*/    char c = parseNextCharacter(source, pos);
/* 339*/    int sign = 0;
/* 340*/    switch (c) {
/*   0*/      case '\000':
/* 344*/        return new Complex(re.doubleValue(), 0.0D);
/*   0*/      case '-':
/* 346*/        sign = -1;
/*   0*/        break;
/*   0*/      case '+':
/* 349*/        sign = 1;
/*   0*/        break;
/*   0*/      default:
/* 355*/        pos.setIndex(initialIndex);
/* 356*/        pos.setErrorIndex(startIndex);
/* 357*/        return null;
/*   0*/    } 
/* 361*/    parseAndIgnoreWhitespace(source, pos);
/* 364*/    Number im = parseNumber(source, getRealFormat(), pos);
/* 365*/    if (im == null) {
/* 369*/      pos.setIndex(initialIndex);
/* 370*/      return null;
/*   0*/    } 
/* 374*/    int n = getImaginaryCharacter().length();
/* 375*/    startIndex = pos.getIndex();
/* 376*/    int endIndex = startIndex + n;
/* 377*/    if (source.substring(startIndex, endIndex).compareTo(getImaginaryCharacter()) != 0) {
/* 382*/      pos.setIndex(initialIndex);
/* 383*/      pos.setErrorIndex(startIndex);
/* 384*/      return null;
/*   0*/    } 
/* 386*/    pos.setIndex(endIndex);
/* 388*/    return new Complex(re.doubleValue(), im.doubleValue() * sign);
/*   0*/  }
/*   0*/  
/*   0*/  private void parseAndIgnoreWhitespace(String source, ParsePosition pos) {
/* 399*/    parseNextCharacter(source, pos);
/* 400*/    pos.setIndex(pos.getIndex() - 1);
/*   0*/  }
/*   0*/  
/*   0*/  private char parseNextCharacter(String source, ParsePosition pos) {
/* 411*/    int index = pos.getIndex();
/* 412*/    int n = source.length();
/* 413*/    char ret = Character.MIN_VALUE;
/* 415*/    if (index < n) {
/*   0*/      char c;
/*   0*/      do {
/* 418*/        c = source.charAt(index++);
/* 419*/      } while (Character.isWhitespace(c) && index < n);
/* 420*/      pos.setIndex(index);
/* 422*/      if (index < n)
/* 423*/        ret = c; 
/*   0*/    } 
/* 427*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private Number parseNumber(String source, double value, ParsePosition pos) {
/* 440*/    Number ret = null;
/* 442*/    StringBuffer sb = new StringBuffer();
/* 443*/    sb.append('(');
/* 444*/    sb.append(value);
/* 445*/    sb.append(')');
/* 447*/    int n = sb.length();
/* 448*/    int startIndex = pos.getIndex();
/* 449*/    int endIndex = startIndex + n;
/* 450*/    if (endIndex < source.length() && 
/* 451*/      source.substring(startIndex, endIndex).compareTo(sb.toString()) == 0) {
/* 452*/      ret = new Double(value);
/* 453*/      pos.setIndex(endIndex);
/*   0*/    } 
/* 457*/    return ret;
/*   0*/  }
/*   0*/  
/*   0*/  private Number parseNumber(String source, NumberFormat format, ParsePosition pos) {
/* 471*/    int startIndex = pos.getIndex();
/* 472*/    Number number = format.parse(source, pos);
/* 473*/    int endIndex = pos.getIndex();
/* 476*/    if (startIndex == endIndex) {
/* 478*/      double[] special = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
/* 479*/      for (int i = 0; i < special.length; i++) {
/* 480*/        number = parseNumber(source, special[i], pos);
/* 481*/        if (number != null)
/*   0*/          break; 
/*   0*/      } 
/*   0*/    } 
/* 487*/    return number;
/*   0*/  }
/*   0*/  
/*   0*/  public Object parseObject(String source, ParsePosition pos) {
/* 499*/    return parse(source, pos);
/*   0*/  }
/*   0*/  
/*   0*/  public void setImaginaryCharacter(String imaginaryCharacter) {
/* 508*/    if (imaginaryCharacter == null || imaginaryCharacter.length() == 0)
/* 509*/      throw new IllegalArgumentException("imaginaryCharacter must be a non-empty string."); 
/* 512*/    this.imaginaryCharacter = imaginaryCharacter;
/*   0*/  }
/*   0*/  
/*   0*/  public void setImaginaryFormat(NumberFormat imaginaryFormat) {
/* 522*/    if (imaginaryFormat == null)
/* 523*/      throw new IllegalArgumentException("imaginaryFormat can not be null."); 
/* 526*/    this.imaginaryFormat = imaginaryFormat;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRealFormat(NumberFormat realFormat) {
/* 536*/    if (realFormat == null)
/* 537*/      throw new IllegalArgumentException("realFormat can not be null."); 
/* 540*/    this.realFormat = realFormat;
/*   0*/  }
/*   0*/}
