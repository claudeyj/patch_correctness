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
/*   0*/  public void setWidth(int width) {
/* 135*/    this.defaultWidth = width;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWidth() {
/* 145*/    return this.defaultWidth;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLeftPadding(int padding) {
/* 155*/    this.defaultLeftPad = padding;
/*   0*/  }
/*   0*/  
/*   0*/  public int getLeftPadding() {
/* 165*/    return this.defaultLeftPad;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDescPadding(int padding) {
/* 175*/    this.defaultDescPad = padding;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDescPadding() {
/* 185*/    return this.defaultDescPad;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSyntaxPrefix(String prefix) {
/* 195*/    this.defaultSyntaxPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getSyntaxPrefix() {
/* 205*/    return this.defaultSyntaxPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNewLine(String newline) {
/* 215*/    this.defaultNewLine = newline;
/*   0*/  }
/*   0*/  
/*   0*/  public String getNewLine() {
/* 225*/    return this.defaultNewLine;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOptPrefix(String prefix) {
/* 235*/    this.defaultOptPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getOptPrefix() {
/* 245*/    return this.defaultOptPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLongOptPrefix(String prefix) {
/* 255*/    this.defaultLongOptPrefix = prefix;
/*   0*/  }
/*   0*/  
/*   0*/  public String getLongOptPrefix() {
/* 265*/    return this.defaultLongOptPrefix;
/*   0*/  }
/*   0*/  
/*   0*/  public void setArgName(String name) {
/* 275*/    this.defaultArgName = name;
/*   0*/  }
/*   0*/  
/*   0*/  public String getArgName() {
/* 285*/    return this.defaultArgName;
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, Options options) {
/* 301*/    printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, Options options, boolean autoUsage) {
/* 317*/    printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, autoUsage);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, String header, Options options, String footer) {
/* 333*/    printHelp(cmdLineSyntax, header, options, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
/* 351*/    printHelp(this.defaultWidth, cmdLineSyntax, header, options, footer, autoUsage);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer) {
/* 369*/    printHelp(width, cmdLineSyntax, header, options, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
/* 388*/    PrintWriter pw = new PrintWriter(System.out);
/* 390*/    printHelp(pw, width, cmdLineSyntax, header, options, this.defaultLeftPad, this.defaultDescPad, footer, autoUsage);
/* 392*/    pw.flush();
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer) {
/* 414*/    printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer, boolean autoUsage) {
/* 440*/    if (cmdLineSyntax == null || cmdLineSyntax.length() == 0) {
/* 442*/        throw new IllegalArgumentException("cmdLineSyntax not provided"); 
/*   0*/       }
/* 445*/    if (autoUsage) {
/* 447*/      printUsage(pw, width, cmdLineSyntax, options);
/*   0*/    } else {
/* 451*/      printUsage(pw, width, cmdLineSyntax);
/*   0*/    } 
/* 454*/    if (header != null && header.trim().length() > 0) {
/* 456*/        printWrapped(pw, width, header); 
/*   0*/       }
/* 459*/    printOptions(pw, width, options, leftPad, descPad);
/* 461*/    if (footer != null && footer.trim().length() > 0) {
/* 463*/        printWrapped(pw, width, footer); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printUsage(PrintWriter pw, int width, String app, Options options) {
/* 480*/    StringBuffer buff = new StringBuffer(this.defaultSyntaxPrefix).append(app).append(" ");
/* 484*/    Collection processedGroups = new ArrayList();
/* 489*/    List optList = new ArrayList(options.getOptions());
/* 490*/    Collections.sort(optList, new OptionComparator());
/* 492*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 495*/      Option option = (Option)i.next();
/* 498*/      OptionGroup group = options.getOptionGroup(option);
/* 501*/      if (group != null) {
/* 504*/        if (!processedGroups.contains(group)) {
/* 507*/          processedGroups.add(group);
/* 511*/          appendOptionGroup(buff, group);
/*   0*/        } 
/*   0*/      } else {
/* 521*/        appendOption(buff, option, option.isRequired());
/*   0*/      } 
/* 524*/      if (i.hasNext()) {
/* 526*/          buff.append(" "); 
/*   0*/         }
/*   0*/    } 
/* 532*/    printWrapped(pw, width, buff.toString().indexOf(' ') + 1, buff.toString());
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendOptionGroup(StringBuffer buff, OptionGroup group) {
/* 547*/    if (!group.isRequired()) {
/* 549*/        buff.append("["); 
/*   0*/       }
/* 552*/    List optList = new ArrayList(group.getOptions());
/* 553*/    Collections.sort(optList, new OptionComparator());
/* 555*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 558*/      appendOption(buff, (Option)i.next(), true);
/* 560*/      if (i.hasNext()) {
/* 562*/          buff.append(" | "); 
/*   0*/         }
/*   0*/    } 
/* 566*/    if (!group.isRequired()) {
/* 568*/        buff.append("]"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private static void appendOption(StringBuffer buff, Option option, boolean required) {
/* 583*/    if (!required) {
/* 585*/        buff.append("["); 
/*   0*/       }
/* 588*/    if (option.getOpt() != null) {
/* 590*/      buff.append("-").append(option.getOpt());
/*   0*/    } else {
/* 594*/      buff.append("--").append(option.getLongOpt());
/*   0*/    } 
/* 598*/    if (option.hasArg() && option.getArgName() != null) {
/* 600*/        buff.append(" <").append(option.getArgName()).append(">"); 
/*   0*/       }
/* 604*/    if (!required) {
/* 606*/        buff.append("]"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void printUsage(PrintWriter pw, int width, String cmdLineSyntax) {
/* 620*/    int argPos = cmdLineSyntax.indexOf(' ') + 1;
/* 622*/    printWrapped(pw, width, this.defaultSyntaxPrefix.length() + argPos, this.defaultSyntaxPrefix + cmdLineSyntax);
/*   0*/  }
/*   0*/  
/*   0*/  public void printOptions(PrintWriter pw, int width, Options options, int leftPad, int descPad) {
/* 641*/    StringBuffer sb = new StringBuffer();
/* 643*/    renderOptions(sb, width, options, leftPad, descPad);
/* 644*/    pw.println(sb.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public void printWrapped(PrintWriter pw, int width, String text) {
/* 656*/    printWrapped(pw, width, 0, text);
/*   0*/  }
/*   0*/  
/*   0*/  public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) {
/* 670*/    StringBuffer sb = new StringBuffer(text.length());
/* 672*/    renderWrappedText(sb, width, nextLineTabStop, text);
/* 673*/    pw.println(sb.toString());
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) {
/* 696*/    String lpad = createPadding(leftPad);
/* 697*/    String dpad = createPadding(descPad);
/* 703*/    int max = 0;
/* 705*/    List prefixList = new ArrayList();
/* 707*/    List optList = options.helpOptions();
/* 709*/    Collections.sort(optList, new OptionComparator());
/* 711*/    for (Iterator i = optList.iterator(); i.hasNext(); ) {
/* 713*/      Option option = (Option)i.next();
/* 714*/      StringBuffer optBuf = new StringBuffer(8);
/* 716*/      if (option.getOpt() == null) {
/* 718*/        optBuf.append(lpad).append("   " + this.defaultLongOptPrefix).append(option.getLongOpt());
/*   0*/      } else {
/* 723*/        optBuf.append(lpad).append(this.defaultOptPrefix).append(option.getOpt());
/* 726*/        if (option.hasLongOpt()) {
/* 728*/            optBuf.append(',').append(this.defaultLongOptPrefix).append(option.getLongOpt()); 
/*   0*/           }
/*   0*/      } 
/* 733*/      if (option.hasArg()) {
/* 735*/          if (option.hasArgName()) {
/* 737*/            optBuf.append(" <").append(option.getArgName()).append(">");
/*   0*/          } else {
/* 741*/            optBuf.append(' ');
/*   0*/          }  
/*   0*/         }
/* 745*/      prefixList.add(optBuf);
/* 746*/      max = (optBuf.length() > max) ? optBuf.length() : max;
/*   0*/    } 
/* 749*/    int x = 0;
/* 751*/    for (Iterator iterator1 = optList.iterator(); iterator1.hasNext(); ) {
/* 753*/      Option option = (Option)iterator1.next();
/* 754*/      StringBuffer optBuf = new StringBuffer(prefixList.get(x++).toString());
/* 756*/      if (optBuf.length() < max) {
/* 758*/          optBuf.append(createPadding(max - optBuf.length())); 
/*   0*/         }
/* 761*/      optBuf.append(dpad);
/* 763*/      int nextLineTabStop = max + descPad;
/* 765*/      if (option.getDescription() != null) {
/* 767*/          optBuf.append(option.getDescription()); 
/*   0*/         }
/* 770*/      renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());
/* 772*/      if (iterator1.hasNext()) {
/* 774*/          sb.append(this.defaultNewLine); 
/*   0*/         }
/*   0*/    } 
/* 778*/    return sb;
/*   0*/  }
/*   0*/  
/*   0*/  protected StringBuffer renderWrappedText(StringBuffer sb, int width, int nextLineTabStop, String text) {
/* 795*/    int pos = findWrapPos(text, width, 0);
/* 797*/    if (pos == -1) {
/* 799*/      sb.append(rtrim(text));
/* 801*/      return sb;
/*   0*/    } 
/* 803*/    sb.append(rtrim(text.substring(0, pos))).append(this.defaultNewLine);
/* 807*/    String padding = createPadding(nextLineTabStop);
/*   0*/    while (true) {
/* 811*/      text = padding + text.substring(pos).trim();
/* 812*/      pos = findWrapPos(text, width, this.defaultLeftPad);
/* 814*/      if (pos == -1) {
/* 816*/        sb.append(text);
/* 818*/        return sb;
/*   0*/      } 
/* 821*/      sb.append(rtrim(text.substring(0, pos))).append(this.defaultNewLine);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected int findWrapPos(String text, int width, int startPos) {
/* 840*/    int pos = -1;
/* 843*/    if (((pos = text.indexOf('\n', startPos)) != -1 && pos <= width) || ((pos = text.indexOf('\t', startPos)) != -1 && pos <= width)) {
/* 846*/        return pos + 1; 
/*   0*/       }
/* 848*/    if (startPos + width >= text.length()) {
/* 850*/        return -1; 
/*   0*/       }
/* 855*/    pos = startPos + width;
/*   0*/    char c;
/* 859*/    while (pos >= startPos && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r') {
/* 862*/        pos--; 
/*   0*/       }
/* 866*/    if (pos > startPos) {
/* 868*/        return pos; 
/*   0*/       }
/* 873*/    pos = startPos + width;
/* 875*/    while (pos <= text.length() && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r') {
/* 878*/        pos++; 
/*   0*/       }
/* 881*/    return (pos == text.length()) ? -1 : pos;
/*   0*/  }
/*   0*/  
/*   0*/  protected String createPadding(int len) {
/* 893*/    StringBuffer sb = new StringBuffer(len);
/* 895*/    for (int i = 0; i < len; i++) {
/* 897*/        sb.append(' '); 
/*   0*/       }
/* 900*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected String rtrim(String s) {
/* 912*/    if (s == null || s.length() == 0) {
/* 914*/        return s; 
/*   0*/       }
/* 917*/    int pos = s.length();
/* 919*/    while (pos > 0 && Character.isWhitespace(s.charAt(pos - 1))) {
/* 921*/        pos--; 
/*   0*/       }
/* 924*/    return s.substring(0, pos);
/*   0*/  }
/*   0*/  
/*   0*/  private static class OptionComparator implements Comparator {
/*   0*/    private OptionComparator() {}
/*   0*/    
/*   0*/    public int compare(Object o1, Object o2) {
/* 951*/      Option opt1 = (Option)o1;
/* 952*/      Option opt2 = (Option)o2;
/* 954*/      return opt1.getKey().compareToIgnoreCase(opt2.getKey());
/*   0*/    }
/*   0*/  }
/*   0*/}
