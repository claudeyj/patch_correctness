/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.io.PrintWriter;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/
/*   0*/public class HelpFormatter {
/*   0*/  public static final int DEFAULT_WIDTH = 74;
/*   0*/  
/*   0*/  public static final int DEFAULT_LEFT_PAD = 1;
/*   0*/  
/*   0*/  public static final int DEFAULT_DESC_PAD = 3;
/*   0*/  
/*   0*/  public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";
/*   0*/  
/*   0*/  public static final String DEFAULT_OPT_PREFIX = "-";
/*   0*/  
/*   0*/  public static final String DEFAULT_LONG_OPT_PREFIX = "--";
/*   0*/  
/*   0*/  public static final String DEFAULT_LONG_OPT_SEPARATOR = " ";
/*   0*/  
/*   0*/  public static final String DEFAULT_ARG_NAME = "arg";
/*   0*/  
/*  75*/  public int defaultWidth = 74;
/*   0*/  
/*  83*/  public int defaultLeftPad = 1;
/*   0*/  
/*  92*/  public int defaultDescPad = 3;
/*   0*/  
/* 100*/  public String defaultSyntaxPrefix = "usage: ";
/*   0*/  
/* 108*/  public String defaultNewLine = System.getProperty("line.separator");
/*   0*/  
/* 116*/  public String defaultOptPrefix = "-";
/*   0*/  
/* 124*/  public String defaultLongOptPrefix = "--";
/*   0*/  
/* 127*/  private String longOptSeparator = " ";
/*   0*/  
/* 135*/  public String defaultArgName = "arg";
/*   0*/  
/* 142*/  protected Comparator optionComparator = new OptionComparator();
/*   0*/  
/*   0*/  public void setWidth(int width) {
/* 151*/    this.defaultWidth = width;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWidth() {
/* 161*/    return this.defaultWidth;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLeftPadding(int padding) {
/* 171*/    this.defaultLeftPad = padding;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLeftPadding() {
/* 181*/    return this.defaultLeftPad;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDescPadding(int padding) {
/* 191*/    this.defaultDescPad = padding;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDescPadding() {
/* 201*/    return this.defaultDescPad;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSyntaxPrefix(String prefix) {
/* 211*/    this.defaultSyntaxPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getSyntaxPrefix() {
/* 221*/    return this.defaultSyntaxPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNewLine(String newline) {
/* 231*/    this.defaultNewLine = newline;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNewLine() {
/* 241*/    return this.defaultNewLine;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptPrefix(String prefix) {
/* 251*/    this.defaultOptPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getOptPrefix() {
/* 261*/    return this.defaultOptPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLongOptPrefix(String prefix) {
/* 271*/    this.defaultLongOptPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getLongOptPrefix() {
/* 281*/    return this.defaultLongOptPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLongOptSeparator(String longOptSeparator) {
/* 294*/    this.longOptSeparator = longOptSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public String getLongOptSeparator() {
/* 305*/    return this.longOptSeparator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setArgName(String name) {
/* 315*/    this.defaultArgName = name;
/*   0*/  }
/*   0*/  
/*   0*/  public String getArgName() {
/* 325*/    return this.defaultArgName;
/*   0*/  }
/*   0*/  
/*   0*/  public Comparator getOptionComparator() {
/* 336*/    return this.optionComparator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionComparator(Comparator comparator) {
/* 347*/    if (comparator == null) {
/* 349*/      this.optionComparator = new OptionComparator();
/*   0*/    } else {
/* 353*/      this.optionComparator = comparator;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, Options options) {
/* 367*/    printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, Options options, boolean autoUsage) {
/* 382*/    printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, autoUsage);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, String header, Options options, String footer) {
/* 397*/    printHelp(cmdLineSyntax, header, options, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
/* 414*/    printHelp(this.defaultWidth, cmdLineSyntax, header, options, footer, autoUsage);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer) {
/* 430*/    printHelp(width, cmdLineSyntax, header, options, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
/* 449*/    PrintWriter pw = new PrintWriter(System.out);
/* 451*/    printHelp(pw, width, cmdLineSyntax, header, options, this.defaultLeftPad, this.defaultDescPad, footer, autoUsage);
/* 452*/    pw.flush();
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer) {
/* 476*/    printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer, boolean autoUsage) {
/* 503*/    if (cmdLineSyntax == null || cmdLineSyntax.length() == 0) {
/* 505*/        throw new IllegalArgumentException("cmdLineSyntax not provided"); 
/*   0*/       }
/* 508*/    if (autoUsage) {
/* 510*/      printUsage(pw, width, cmdLineSyntax, options);
/*   0*/    } else {
/* 514*/      printUsage(pw, width, cmdLineSyntax);
/*   0*/    } 
/* 517*/    if (header != null && header.trim().length() > 0) {
/* 519*/        printWrapped(pw, width, header); 
/*   0*/       }
/* 522*/    printOptions(pw, width, options, leftPad, descPad);
/* 524*/    if (footer != null && footer.trim().length() > 0) {
/* 526*/        printWrapped(pw, width, footer); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printUsage(PrintWriter pw, int width, String app, Options options) {
/* 541*/    StringBuffer buff = new StringBuffer(this.defaultSyntaxPrefix).append(app).append(" ");
/* 544*/    Collection processedGroups = new ArrayList();
/* 549*/    List optList = new ArrayList(options.getOptions());
/* 550*/    Collections.sort(optList, getOptionComparator());
/* 552*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 555*/      Option option = (Option)i.next();
/* 558*/      OptionGroup group = options.getOptionGroup(option);
/* 561*/      if (group != null) {
/* 564*/        if (!processedGroups.contains(group)) {
/* 567*/          processedGroups.add(group);
/* 571*/          appendOptionGroup(buff, group);
/*   0*/        } 
/*   0*/      } else {
/* 581*/        appendOption(buff, option, option.isRequired());
/*   0*/      } 
/* 584*/      if (i.hasNext()) {
/* 586*/          buff.append(" "); 
/*   0*/         }
/*   0*/    } 
/* 592*/    printWrapped(pw, width, buff.toString().indexOf(' ') + 1, buff.toString());
/*   0*/  }
/*   0*/  
/*   0*/  private void appendOptionGroup(StringBuffer buff, OptionGroup group) {
/* 605*/    if (!group.isRequired()) {
/* 607*/        buff.append("["); 
/*   0*/       }
/* 610*/    List optList = new ArrayList(group.getOptions());
/* 611*/    Collections.sort(optList, getOptionComparator());
/* 613*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 616*/      appendOption(buff, (Option)i.next(), true);
/* 618*/      if (i.hasNext()) {
/* 620*/          buff.append(" | "); 
/*   0*/         }
/*   0*/    } 
/* 624*/    if (!group.isRequired()) {
/* 626*/        buff.append("]"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void appendOption(StringBuffer buff, Option option, boolean required) {
/* 639*/    if (!required) {
/* 641*/        buff.append("["); 
/*   0*/       }
/* 644*/    if (option.getOpt() != null) {
/* 646*/      buff.append("-").append(option.getOpt());
/*   0*/    } else {
/* 650*/      buff.append("--").append(option.getLongOpt());
/*   0*/    } 
/* 654*/    if (option.hasArg() && (option.getArgName() == null || option.getArgName().length() != 0)) {
/* 656*/      buff.append((option.getOpt() == null) ? this.longOptSeparator : " ");
/* 657*/      buff.append("<").append((option.getArgName() != null) ? option.getArgName() : getArgName()).append(">");
/*   0*/    } 
/* 661*/    if (!required) {
/* 663*/        buff.append("]"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printUsage(PrintWriter pw, int width, String cmdLineSyntax) {
/* 677*/    int argPos = cmdLineSyntax.indexOf(' ') + 1;
/* 679*/    printWrapped(pw, width, this.defaultSyntaxPrefix.length() + argPos, this.defaultSyntaxPrefix + cmdLineSyntax);
/*   0*/  }
/*   0*/  
/*   0*/  public void printOptions(PrintWriter pw, int width, Options options, int leftPad, int descPad) {
/* 697*/    StringBuffer sb = new StringBuffer();
/* 699*/    renderOptions(sb, width, options, leftPad, descPad);
/* 700*/    pw.println(sb.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public void printWrapped(PrintWriter pw, int width, String text) {
/* 712*/    printWrapped(pw, width, 0, text);
/*   0*/  }
/*   0*/  
/*   0*/  public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) {
/* 725*/    StringBuffer sb = new StringBuffer(text.length());
/* 727*/    renderWrappedText(sb, width, nextLineTabStop, text);
/* 728*/    pw.println(sb.toString());
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) {
/* 749*/    String lpad = createPadding(leftPad);
/* 750*/    String dpad = createPadding(descPad);
/* 756*/    int max = 0;
/* 758*/    List prefixList = new ArrayList();
/* 760*/    List optList = options.helpOptions();
/* 762*/    Collections.sort(optList, getOptionComparator());
/* 764*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 766*/      Option option = (Option)i.next();
/* 767*/      StringBuffer optBuf = new StringBuffer();
/* 769*/      if (option.getOpt() == null) {
/* 771*/        optBuf.append(lpad).append("   " + this.defaultLongOptPrefix).append(option.getLongOpt());
/*   0*/      } else {
/* 775*/        optBuf.append(lpad).append(this.defaultOptPrefix).append(option.getOpt());
/* 777*/        if (option.hasLongOpt()) {
/* 779*/            optBuf.append(',').append(this.defaultLongOptPrefix).append(option.getLongOpt()); 
/*   0*/           }
/*   0*/      } 
/* 783*/      if (option.hasArg()) {
/* 785*/        String argName = option.getArgName();
/* 786*/        if (argName != null && argName.length() == 0) {
/* 789*/          optBuf.append(' ');
/*   0*/        } else {
/* 793*/          optBuf.append(option.hasLongOpt() ? this.longOptSeparator : " ");
/* 794*/          optBuf.append("<").append((argName != null) ? option.getArgName() : getArgName()).append(">");
/*   0*/        } 
/*   0*/      } 
/* 798*/      prefixList.add(optBuf);
/* 799*/      max = (optBuf.length() > max) ? optBuf.length() : max;
/*   0*/    } 
/* 802*/    int x = 0;
/* 804*/    for (Iterator iterator1 = optList.iterator(); iterator1.hasNext(); ) {
/* 806*/      Option option = (Option)iterator1.next();
/* 807*/      StringBuffer optBuf = new StringBuffer(prefixList.get(x++).toString());
/* 809*/      if (optBuf.length() < max) {
/* 811*/          optBuf.append(createPadding(max - optBuf.length())); 
/*   0*/         }
/* 814*/      optBuf.append(dpad);
/* 816*/      int nextLineTabStop = max + descPad;
/* 818*/      if (option.getDescription() != null) {
/* 820*/          optBuf.append(option.getDescription()); 
/*   0*/         }
/* 823*/      renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());
/* 825*/      if (iterator1.hasNext()) {
/* 827*/          sb.append(this.defaultNewLine); 
/*   0*/         }
/*   0*/    } 
/* 831*/    return sb;
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer renderWrappedText(StringBuffer sb, int width, int nextLineTabStop, String text) {
/* 848*/    int pos = findWrapPos(text, width, 0);
/* 850*/    if (pos == -1) {
/* 852*/      sb.append(rtrim(text));
/* 854*/      return sb;
/*   0*/    } 
/* 856*/    sb.append(rtrim(text.substring(0, pos))).append(this.defaultNewLine);
/* 858*/    if (nextLineTabStop >= width) {
/* 861*/        nextLineTabStop = 1; 
/*   0*/       }
/* 865*/    String padding = createPadding(nextLineTabStop);
/*   0*/    while (true) {
/* 869*/      text = padding + text.substring(pos).trim();
/* 870*/      pos = findWrapPos(text, width, 0);
/* 872*/      if (pos == -1) {
/* 874*/        sb.append(text);
/* 876*/        return sb;
/*   0*/      } 
/* 879*/      if (text.length() > width && pos == nextLineTabStop - 1) {
/* 881*/          pos = width; 
/*   0*/         }
/* 884*/      sb.append(rtrim(text.substring(0, pos))).append(this.defaultNewLine);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected int findWrapPos(String text, int width, int startPos) {
/*   0*/    int pos;
/* 907*/    if (((pos = text.indexOf('\n', startPos)) != -1 && pos <= width) || ((pos = text.indexOf('\t', startPos)) != -1 && pos <= width)) {
/* 910*/        return pos + 1; 
/*   0*/       }
/* 912*/    if (startPos + width >= text.length()) {
/* 914*/        return -1; 
/*   0*/       }
/* 919*/    pos = startPos + width;
/*   0*/    char c;
/* 924*/    while (pos >= startPos && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r') {
/* 926*/        pos--; 
/*   0*/       }
/* 930*/    if (pos > startPos) {
/* 932*/        return pos; 
/*   0*/       }
/* 936*/    pos = startPos + width;
/* 938*/    for (; pos <= text.length(); 32);
/*   0*/    while (true) {
/* 942*/      if (pos == text.length());
/* 942*/      return pos;
/*   0*/    } 
/* 942*/    return pos;
/*   0*/  }
/*   0*/  
/*   0*/  protected String createPadding(int len) {
/* 954*/    char[] padding = new char[len];
/* 955*/    Arrays.fill(padding, ' ');
/* 957*/    return new String(padding);
/*   0*/  }
/*   0*/  
/*   0*/  protected String rtrim(String s) {
/* 969*/    if (s == null || s.length() == 0) {
/* 971*/        return s; 
/*   0*/       }
/* 974*/    int pos = s.length();
/* 976*/    while (pos > 0 && Character.isWhitespace(s.charAt(pos - 1))) {
/* 978*/        pos--; 
/*   0*/       }
/* 981*/    return s.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  private static class OptionComparator implements Comparator {
/*   0*/    private OptionComparator() {}
/*   0*/    
/*   0*/    public int compare(Object o1, Object o2) {
/*1007*/      Option opt1 = (Option)o1;
/*1008*/      Option opt2 = (Option)o2;
/*1010*/      return opt1.getKey().compareToIgnoreCase(opt2.getKey());
/*   0*/    }
/*   0*/  }
/*   0*/}
