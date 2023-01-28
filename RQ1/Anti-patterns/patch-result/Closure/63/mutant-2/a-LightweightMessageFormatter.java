/*   0*/package com.google.javascript.jscomp;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/
/*   0*/public class LightweightMessageFormatter extends AbstractMessageFormatter {
/*   0*/  private SourceExcerptProvider.SourceExcerpt excerpt;
/*   0*/  
/*  32*/  private static final SourceExcerptProvider.ExcerptFormatter excerptFormatter = new LineNumberingFormatter();
/*   0*/  
/*   0*/  private LightweightMessageFormatter() {
/*  39*/    super(null);
/*  40*/    this.excerpt = SourceExcerptProvider.SourceExcerpt.LINE;
/*   0*/  }
/*   0*/  
/*   0*/  public LightweightMessageFormatter(SourceExcerptProvider source) {
/*  44*/    this(source, SourceExcerptProvider.SourceExcerpt.LINE);
/*   0*/  }
/*   0*/  
/*   0*/  public LightweightMessageFormatter(SourceExcerptProvider source, SourceExcerptProvider.SourceExcerpt excerpt) {
/*  49*/    super(source);
/*  50*/    Preconditions.checkNotNull(source);
/*  51*/    this.excerpt = excerpt;
/*   0*/  }
/*   0*/  
/*   0*/  static LightweightMessageFormatter withoutSource() {
/*  55*/    return new LightweightMessageFormatter();
/*   0*/  }
/*   0*/  
/*   0*/  public String formatError(JSError error) {
/*  59*/    return format(error, false);
/*   0*/  }
/*   0*/  
/*   0*/  public String formatWarning(JSError warning) {
/*  63*/    return format(warning, true);
/*   0*/  }
/*   0*/  
/*   0*/  private String format(JSError error, boolean warning) {
/*  68*/    SourceExcerptProvider source = getSource();
/*  69*/    String sourceExcerpt = (source == null) ? null : this.excerpt.get(source, error.sourceName, error.lineNumber, excerptFormatter);
/*  74*/    StringBuilder b = new StringBuilder();
/*  75*/    if (error.sourceName != null) {
/*  76*/      b.append(error.sourceName);
/*  77*/      if (error.lineNumber > 0) {
/*  78*/        b.append(':');
/*  79*/        b.append(error.lineNumber);
/*   0*/      } 
/*  81*/      b.append(": ");
/*   0*/    } 
/*  84*/    b.append(getLevelName(warning ? CheckLevel.WARNING : CheckLevel.ERROR));
/*  85*/    b.append(" - ");
/*  87*/    b.append(error.description);
/*  88*/    b.append('\n');
/*  89*/    if (sourceExcerpt != null) {
/*  90*/      b.append(sourceExcerpt);
/*  91*/      b.append('\n');
/*  92*/      int charno = error.getCharno();
/*  97*/      if (this.excerpt.equals(SourceExcerptProvider.SourceExcerpt.LINE) && 0 <= charno && charno < sourceExcerpt.length()) {
/*  99*/        for (int i = 0; i < charno; i++) {
/* 100*/          char c = sourceExcerpt.charAt(i);
/* 101*/          if (Character.isWhitespace(c)) {
/* 102*/            b.append(c);
/*   0*/          } else {
/* 104*/            b.append(' ');
/*   0*/          } 
/*   0*/        } 
/* 107*/        b.append("^\n");
/*   0*/      } 
/*   0*/    } 
/* 110*/    return b.toString();
/*   0*/  }
/*   0*/  
/*   0*/  static class LineNumberingFormatter implements SourceExcerptProvider.ExcerptFormatter {
/*   0*/    public String formatLine(String line, int lineNumber) {
/* 122*/      return line;
/*   0*/    }
/*   0*/    
/*   0*/    public String formatRegion(Region region) {
/* 126*/      if (region == null)
/* 127*/        return null; 
/* 129*/      String code = region.getSourceExcerpt();
/* 130*/      if (code.length() == 0)
/* 131*/        return null; 
/* 135*/      int numberLength = Integer.toString(region.getEndingLineNumber()).length();
/* 139*/      StringBuilder builder = new StringBuilder(code.length() * 2);
/* 140*/      int start = 0;
/* 141*/      int end = code.indexOf('\n', start);
/* 142*/      int lineNumber = region.getBeginningLineNumber();
/* 143*/      while (start >= 0) {
/*   0*/        String line;
/* 146*/        if (end < 0) {
/* 147*/          line = code.substring(start);
/* 148*/          if (line.length() == 0)
/* 149*/            return builder.substring(0, builder.length() - 1); 
/*   0*/        } else {
/* 152*/          line = code.substring(start, end);
/*   0*/        } 
/* 154*/        builder.append("  ");
/* 157*/        int spaces = numberLength - Integer.toString(lineNumber).length();
/* 158*/        builder.append(Strings.repeat(" ", spaces));
/* 159*/        builder.append(lineNumber);
/* 160*/        builder.append("| ");
/* 163*/        if (end < 0) {
/* 164*/          builder.append(line);
/* 165*/          start = -1;
/*   0*/          continue;
/*   0*/        } 
/* 167*/        builder.append(line);
/* 168*/        builder.append('\n');
/* 169*/        start = end + 1;
/* 170*/        end = code.indexOf('\n', start);
/* 171*/        lineNumber++;
/*   0*/      } 
/* 174*/      return builder.toString();
/*   0*/    }
/*   0*/  }
/*   0*/}
