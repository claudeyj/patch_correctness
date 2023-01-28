/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.io.PrintWriter;
/*   0*/import java.util.ArrayList;
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
/*   0*/  public static final String DEFAULT_ARG_NAME = "arg";
/*   0*/  
/*  69*/  public int defaultWidth = 74;
/*   0*/  
/*  77*/  public int defaultLeftPad = 1;
/*   0*/  
/*  86*/  public int defaultDescPad = 3;
/*   0*/  
/*  94*/  public String defaultSyntaxPrefix = "usage: ";
/*   0*/  
/* 102*/  public String defaultNewLine = System.getProperty("line.separator");
/*   0*/  
/* 110*/  public String defaultOptPrefix = "-";
/*   0*/  
/* 118*/  public String defaultLongOptPrefix = "--";
/*   0*/  
/* 126*/  public String defaultArgName = "arg";
/*   0*/  
/* 133*/  protected Comparator optionComparator = new OptionComparator();
/*   0*/  
/*   0*/  public void setWidth(int width) {
/* 142*/    this.defaultWidth = width;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWidth() {
/* 152*/    return this.defaultWidth;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLeftPadding(int padding) {
/* 162*/    this.defaultLeftPad = padding;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLeftPadding() {
/* 172*/    return this.defaultLeftPad;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDescPadding(int padding) {
/* 182*/    this.defaultDescPad = padding;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDescPadding() {
/* 192*/    return this.defaultDescPad;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSyntaxPrefix(String prefix) {
/* 202*/    this.defaultSyntaxPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getSyntaxPrefix() {
/* 212*/    return this.defaultSyntaxPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNewLine(String newline) {
/* 222*/    this.defaultNewLine = newline;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNewLine() {
/* 232*/    return this.defaultNewLine;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptPrefix(String prefix) {
/* 242*/    this.defaultOptPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getOptPrefix() {
/* 252*/    return this.defaultOptPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLongOptPrefix(String prefix) {
/* 262*/    this.defaultLongOptPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getLongOptPrefix() {
/* 272*/    return this.defaultLongOptPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setArgName(String name) {
/* 282*/    this.defaultArgName = name;
/*   0*/  }
/*   0*/  
/*   0*/  public String getArgName() {
/* 292*/    return this.defaultArgName;
/*   0*/  }
/*   0*/  
/*   0*/  public Comparator getOptionComparator() {
/* 302*/    return this.optionComparator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptionComparator(Comparator comparator) {
/* 312*/    if (comparator == null) {
/* 314*/      this.optionComparator = new OptionComparator();
/*   0*/    } else {
/* 318*/      this.optionComparator = comparator;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, Options options) {
/* 335*/    printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, Options options, boolean autoUsage) {
/* 351*/    printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, autoUsage);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, String header, Options options, String footer) {
/* 367*/    printHelp(cmdLineSyntax, header, options, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
/* 385*/    printHelp(this.defaultWidth, cmdLineSyntax, header, options, footer, autoUsage);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer) {
/* 403*/    printHelp(width, cmdLineSyntax, header, options, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
/* 422*/    PrintWriter pw = new PrintWriter(System.out);
/* 424*/    printHelp(pw, width, cmdLineSyntax, header, options, this.defaultLeftPad, this.defaultDescPad, footer, autoUsage);
/* 426*/    pw.flush();
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer) {
/* 448*/    printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer, boolean autoUsage) {
/* 474*/    if (cmdLineSyntax == null || cmdLineSyntax.length() == 0) {
/* 476*/        throw new IllegalArgumentException("cmdLineSyntax not provided"); 
/*   0*/       }
/* 479*/    if (autoUsage) {
/* 481*/      printUsage(pw, width, cmdLineSyntax, options);
/*   0*/    } else {
/* 485*/      printUsage(pw, width, cmdLineSyntax);
/*   0*/    } 
/* 488*/    if (header != null && header.trim().length() > 0) {
/* 490*/        printWrapped(pw, width, header); 
/*   0*/       }
/* 493*/    printOptions(pw, width, options, leftPad, descPad);
/* 495*/    if (footer != null && footer.trim().length() > 0) {
/* 497*/        printWrapped(pw, width, footer); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printUsage(PrintWriter pw, int width, String app, Options options) {
/* 514*/    StringBuffer buff = new StringBuffer(this.defaultSyntaxPrefix).append(app).append(" ");
/* 518*/    Collection processedGroups = new ArrayList();
/* 523*/    List optList = new ArrayList(options.getOptions());
/* 524*/    Collections.sort(optList, getOptionComparator());
/* 526*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 529*/      Option option = (Option)i.next();
/* 532*/      OptionGroup group = options.getOptionGroup(option);
/* 535*/      if (group != null) {
/* 538*/        if (!processedGroups.contains(group)) {
/* 541*/          processedGroups.add(group);
/* 545*/          appendOptionGroup(buff, group);
/*   0*/        } 
/*   0*/      } else {
/* 555*/        appendOption(buff, option, option.isRequired());
/*   0*/      } 
/* 558*/      if (i.hasNext()) {
/* 560*/          buff.append(" "); 
/*   0*/         }
/*   0*/    } 
/* 566*/    printWrapped(pw, width, buff.toString().indexOf(' ') + 1, buff.toString());
/*   0*/  }
/*   0*/  
/*   0*/  private void appendOptionGroup(StringBuffer buff, OptionGroup group) {
/* 581*/    if (!group.isRequired()) {
/* 583*/        buff.append("["); 
/*   0*/       }
/* 586*/    List optList = new ArrayList(group.getOptions());
/* 587*/    Collections.sort(optList, getOptionComparator());
/* 589*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 592*/      appendOption(buff, (Option)i.next(), true);
/* 594*/      if (i.hasNext()) {
/* 596*/          buff.append(" | "); 
/*   0*/         }
/*   0*/    } 
/* 600*/    if (!group.isRequired()) {
/* 602*/        buff.append("]"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendOption(StringBuffer buff, Option option, boolean required) {
/* 617*/    if (!required) {
/* 619*/        buff.append("["); 
/*   0*/       }
/* 622*/    if (option.getOpt() != null) {
/* 624*/      buff.append("-").append(option.getOpt());
/*   0*/    } else {
/* 628*/      buff.append("--").append(option.getLongOpt());
/*   0*/    } 
/* 632*/    if (option.hasArg() && option.getArgName() != null) {
/* 634*/        buff.append(" <").append(option.getArgName()).append(">"); 
/*   0*/       }
/* 638*/    if (!required) {
/* 640*/        buff.append("]"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printUsage(PrintWriter pw, int width, String cmdLineSyntax) {
/* 654*/    int argPos = cmdLineSyntax.indexOf(' ') + 1;
/* 656*/    printWrapped(pw, width, this.defaultSyntaxPrefix.length() + argPos, this.defaultSyntaxPrefix + cmdLineSyntax);
/*   0*/  }
/*   0*/  
/*   0*/  public void printOptions(PrintWriter pw, int width, Options options, int leftPad, int descPad) {
/* 675*/    StringBuffer sb = new StringBuffer();
/* 677*/    renderOptions(sb, width, options, leftPad, descPad);
/* 678*/    pw.println(sb.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public void printWrapped(PrintWriter pw, int width, String text) {
/* 690*/    printWrapped(pw, width, 0, text);
/*   0*/  }
/*   0*/  
/*   0*/  public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) {
/* 704*/    StringBuffer sb = new StringBuffer(text.length());
/* 706*/    renderWrappedText(sb, width, nextLineTabStop, text);
/* 707*/    pw.println(sb.toString());
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) {
/* 730*/    String lpad = createPadding(leftPad);
/* 731*/    String dpad = createPadding(descPad);
/* 737*/    int max = 0;
/* 739*/    List prefixList = new ArrayList();
/* 741*/    List optList = options.helpOptions();
/* 743*/    Collections.sort(optList, getOptionComparator());
/* 745*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 747*/      Option option = (Option)i.next();
/* 748*/      StringBuffer optBuf = new StringBuffer(8);
/* 750*/      if (option.getOpt() == null) {
/* 752*/        optBuf.append(lpad).append("   " + this.defaultLongOptPrefix).append(option.getLongOpt());
/*   0*/      } else {
/* 757*/        optBuf.append(lpad).append(this.defaultOptPrefix).append(option.getOpt());
/* 760*/        if (option.hasLongOpt()) {
/* 762*/            optBuf.append(',').append(this.defaultLongOptPrefix).append(option.getLongOpt()); 
/*   0*/           }
/*   0*/      } 
/* 767*/      if (option.hasArg()) {
/* 769*/          if (option.hasArgName()) {
/* 771*/            optBuf.append(" <").append(option.getArgName()).append(">");
/*   0*/          } else {
/* 775*/            optBuf.append(' ');
/*   0*/          }  
/*   0*/         }
/* 779*/      prefixList.add(optBuf);
/* 780*/      max = (optBuf.length() > max) ? optBuf.length() : max;
/*   0*/    } 
/* 783*/    int x = 0;
/* 785*/    for (Iterator iterator1 = optList.iterator(); iterator1.hasNext(); ) {
/* 787*/      Option option = (Option)iterator1.next();
/* 788*/      StringBuffer optBuf = new StringBuffer(prefixList.get(x++).toString());
/* 790*/      if (optBuf.length() < max) {
/* 792*/          optBuf.append(createPadding(max - optBuf.length())); 
/*   0*/         }
/* 795*/      optBuf.append(dpad);
/* 797*/      int nextLineTabStop = max + descPad;
/* 799*/      if (option.getDescription() != null) {
/* 801*/          optBuf.append(option.getDescription()); 
/*   0*/         }
/* 804*/      renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());
/* 806*/      if (iterator1.hasNext()) {
/* 808*/          sb.append(this.defaultNewLine); 
/*   0*/         }
/*   0*/    } 
/* 812*/    return sb;
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer renderWrappedText(StringBuffer sb, int width, int nextLineTabStop, String text) {
/* 829*/    int pos = findWrapPos(text, width, 0);
/* 831*/    if (pos == -1) {
/* 833*/      sb.append(rtrim(text));
/* 835*/      return sb;
/*   0*/    } 
/* 837*/    sb.append(rtrim(text.substring(0, pos))).append(this.defaultNewLine);
/* 841*/    String padding = createPadding(nextLineTabStop);
/*   0*/    while (true) {
/* 845*/      text = padding + text.substring(pos).trim();
/* 846*/      pos = findWrapPos(text, width, 0);
/* 848*/      if (pos == -1) {
/* 850*/        sb.append(text);
/* 852*/        return sb;
/*   0*/      } 
/* 855*/      sb.append(rtrim(text.substring(0, pos))).append(this.defaultNewLine);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected int findWrapPos(String text, int width, int startPos) {
/* 874*/    int pos = -1;
/* 877*/    if (((pos = text.indexOf('\n', startPos)) != -1 && pos <= width) || ((pos = text.indexOf('\t', startPos)) != -1 && pos <= width)) {
/* 880*/        return pos + 1; 
/*   0*/       }
/* 882*/    if (startPos + width >= text.length()) {
/* 884*/        return -1; 
/*   0*/       }
/* 889*/    pos = startPos + width;
/*   0*/    char c;
/* 894*/    while (pos >= startPos && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r') {
/* 896*/        pos--; 
/*   0*/       }
/* 900*/    if (pos > startPos) {
/* 902*/        return pos; 
/*   0*/       }
/* 907*/    pos = startPos + width;
/* 910*/    while (pos <= text.length() && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r') {
/* 912*/        pos++; 
/*   0*/       }
/* 915*/    return (pos == text.length()) ? -1 : pos;
/*   0*/  }
/*   0*/  
/*   0*/  protected String createPadding(int len) {
/* 927*/    StringBuffer sb = new StringBuffer(len);
/* 929*/    for (int i = 0; i < len; i++) {
/* 931*/        sb.append(' '); 
/*   0*/       }
/* 934*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected String rtrim(String s) {
/* 946*/    if (s == null || s.length() == 0) {
/* 948*/        return s; 
/*   0*/       }
/* 951*/    int pos = s.length();
/* 953*/    while (pos > 0 && Character.isWhitespace(s.charAt(pos - 1))) {
/* 955*/        pos--; 
/*   0*/       }
/* 958*/    return s.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  private static class OptionComparator implements Comparator {
/*   0*/    private OptionComparator() {}
/*   0*/    
/*   0*/    public int compare(Object o1, Object o2) {
/* 985*/      Option opt1 = (Option)o1;
/* 986*/      Option opt2 = (Option)o2;
/* 988*/      return opt1.getKey().compareToIgnoreCase(opt2.getKey());
/*   0*/    }
/*   0*/  }
/*   0*/}
