/*   0*/package org.apache.commons.cli2.option;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.ListIterator;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.SortedMap;
/*   0*/import java.util.TreeMap;
/*   0*/import org.apache.commons.cli2.Argument;
/*   0*/import org.apache.commons.cli2.DisplaySetting;
/*   0*/import org.apache.commons.cli2.Group;
/*   0*/import org.apache.commons.cli2.HelpLine;
/*   0*/import org.apache.commons.cli2.Option;
/*   0*/import org.apache.commons.cli2.OptionException;
/*   0*/import org.apache.commons.cli2.WriteableCommandLine;
/*   0*/
/*   0*/public class GroupImpl extends OptionImpl implements Group {
/*   0*/  private final String name;
/*   0*/  
/*   0*/  private final String description;
/*   0*/  
/*   0*/  private final List options;
/*   0*/  
/*   0*/  private final int minimum;
/*   0*/  
/*   0*/  private final int maximum;
/*   0*/  
/*   0*/  private final List anonymous;
/*   0*/  
/*   0*/  private final SortedMap optionMap;
/*   0*/  
/*   0*/  private final Set prefixes;
/*   0*/  
/*   0*/  public GroupImpl(List options, String name, String description, int minimum, int maximum) {
/*  69*/    super(0, false);
/*  71*/    this.name = name;
/*  72*/    this.description = description;
/*  73*/    this.minimum = minimum;
/*  74*/    this.maximum = maximum;
/*  78*/    this.options = Collections.unmodifiableList(options);
/*  81*/    List newAnonymous = new ArrayList();
/*  84*/    SortedMap newOptionMap = new TreeMap(ReverseStringComparator.getInstance());
/*  87*/    Set newPrefixes = new HashSet();
/*  90*/    for (Iterator i = options.iterator(); i.hasNext(); ) {
/*  91*/      Option option = (Option)i.next();
/*  93*/      if (option instanceof Argument) {
/*  94*/        i.remove();
/*  95*/        newAnonymous.add(option);
/*   0*/        continue;
/*   0*/      } 
/*  97*/      Set triggers = option.getTriggers();
/*  99*/      for (Iterator j = triggers.iterator(); j.hasNext();) {
/* 100*/          newOptionMap.put(j.next(), option); 
/*   0*/         }
/* 104*/      newPrefixes.addAll(option.getPrefixes());
/*   0*/    } 
/* 108*/    this.anonymous = Collections.unmodifiableList(newAnonymous);
/* 109*/    this.optionMap = Collections.unmodifiableSortedMap(newOptionMap);
/* 110*/    this.prefixes = Collections.unmodifiableSet(newPrefixes);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canProcess(WriteableCommandLine commandLine, String arg) {
/* 115*/    if (arg == null) {
/* 116*/        return false; 
/*   0*/       }
/* 120*/    if (this.optionMap.containsKey(arg)) {
/* 121*/        return true; 
/*   0*/       }
/* 125*/    Map tailMap = this.optionMap.tailMap(arg);
/* 128*/    for (Iterator iter = tailMap.values().iterator(); iter.hasNext(); ) {
/* 129*/      Option option = (Option)iter.next();
/* 131*/      if (option.canProcess(commandLine, arg)) {
/* 132*/          return true; 
/*   0*/         }
/*   0*/    } 
/* 136*/    if (commandLine.looksLikeOption(arg)) {
/* 137*/        return false; 
/*   0*/       }
/* 141*/    if (this.anonymous.size() > 0) {
/* 142*/        return true; 
/*   0*/       }
/* 145*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Set getPrefixes() {
/* 149*/    return this.prefixes;
/*   0*/  }
/*   0*/  
/*   0*/  public Set getTriggers() {
/* 153*/    return this.optionMap.keySet();
/*   0*/  }
/*   0*/  
/*   0*/  public void process(WriteableCommandLine commandLine, ListIterator arguments) throws OptionException {
/* 159*/    String previous = null;
/* 162*/    while (arguments.hasNext()) {
/* 164*/      String arg = (String)arguments.next();
/* 167*/      if (arg == previous) {
/* 169*/        arguments.previous();
/*   0*/        break;
/*   0*/      } 
/* 175*/      previous = arg;
/* 177*/      Option opt = (Option)this.optionMap.get(arg);
/* 180*/      if (opt != null) {
/* 181*/        arguments.previous();
/* 182*/        opt.process(commandLine, arguments);
/*   0*/        continue;
/*   0*/      } 
/* 188*/      if (commandLine.looksLikeOption(arg)) {
/* 190*/        Collection values = this.optionMap.tailMap(arg).values();
/*   0*/        boolean foundMemberOption = false;
/* 194*/        for (Iterator iterator = values.iterator(); iterator.hasNext() && !foundMemberOption; ) {
/* 195*/          Option option = (Option)iterator.next();
/* 197*/          if (option.canProcess(commandLine, arg)) {
/* 198*/            foundMemberOption = true;
/* 199*/            arguments.previous();
/* 200*/            option.process(commandLine, arguments);
/*   0*/          } 
/*   0*/        } 
/* 205*/        if (!foundMemberOption) {
/* 206*/          arguments.previous();
/*   0*/          return;
/*   0*/        } 
/*   0*/        continue;
/*   0*/      } 
/* 215*/      arguments.previous();
/* 219*/      if (this.anonymous.isEmpty()) {
/*   0*/          break; 
/*   0*/         }
/* 225*/      for (Iterator i = this.anonymous.iterator(); i.hasNext(); ) {
/* 226*/        Argument argument = (Argument)i.next();
/* 228*/        if (argument.canProcess(commandLine, arguments)) {
/* 229*/            argument.process(commandLine, arguments); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void validate(WriteableCommandLine commandLine) throws OptionException {
/* 240*/    int present = 0;
/* 243*/    Option unexpected = null;
/* 245*/    for (Iterator i = this.options.iterator(); i.hasNext(); ) {
/* 246*/      Option option = (Option)i.next();
/* 249*/      boolean validate = (option.isRequired() || option instanceof Group);
/* 250*/      if (validate) {
/* 251*/          option.validate(commandLine); 
/*   0*/         }
/* 255*/      if (commandLine.hasOption(option)) {
/* 256*/        if (++present > this.maximum) {
/* 257*/          unexpected = option;
/*   0*/          break;
/*   0*/        } 
/* 262*/        option.validate(commandLine);
/*   0*/      } 
/*   0*/    } 
/* 267*/    if (unexpected != null) {
/* 268*/        throw new OptionException(this, "Unexpected.token", unexpected.getPreferredName()); 
/*   0*/       }
/* 273*/    if (present < this.minimum) {
/* 274*/        throw new OptionException(this, "Missing.option"); 
/*   0*/       }
/* 278*/    for (Iterator iterator1 = this.anonymous.iterator(); iterator1.hasNext(); ) {
/* 279*/      Option option = (Option)iterator1.next();
/* 280*/      option.validate(commandLine);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String getPreferredName() {
/* 285*/    return this.name;
/*   0*/  }
/*   0*/  
/*   0*/  public String getDescription() {
/* 289*/    return this.description;
/*   0*/  }
/*   0*/  
/*   0*/  public void appendUsage(StringBuffer buffer, Set helpSettings, Comparator comp) {
/* 295*/    appendUsage(buffer, helpSettings, comp, "|");
/*   0*/  }
/*   0*/  
/*   0*/  public void appendUsage(StringBuffer buffer, Set helpSettings, Comparator comp, String separator) {
/* 302*/    Set helpSettingsCopy = new HashSet(helpSettings);
/* 304*/    boolean optional = (this.minimum == 0 && helpSettingsCopy.contains(DisplaySetting.DISPLAY_OPTIONAL));
/* 307*/    boolean expanded = (this.name == null || helpSettingsCopy.contains(DisplaySetting.DISPLAY_GROUP_EXPANDED));
/* 310*/    boolean named = (!expanded || (this.name != null && helpSettingsCopy.contains(DisplaySetting.DISPLAY_GROUP_NAME)));
/* 314*/    boolean arguments = helpSettingsCopy.contains(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
/* 316*/    boolean outer = helpSettingsCopy.contains(DisplaySetting.DISPLAY_GROUP_OUTER);
/* 318*/    helpSettingsCopy.remove(DisplaySetting.DISPLAY_GROUP_OUTER);
/* 320*/    boolean both = (named && expanded);
/* 322*/    if (optional) {
/* 323*/        buffer.append('['); 
/*   0*/       }
/* 326*/    if (named) {
/* 327*/        buffer.append(this.name); 
/*   0*/       }
/* 330*/    if (both) {
/* 331*/        buffer.append(" ("); 
/*   0*/       }
/* 334*/    if (expanded) {
/*   0*/      Set childSettings;
/*   0*/      List list;
/* 337*/      if (!helpSettingsCopy.contains(DisplaySetting.DISPLAY_GROUP_EXPANDED)) {
/* 338*/        childSettings = DisplaySetting.NONE;
/*   0*/      } else {
/* 340*/        childSettings = new HashSet(helpSettingsCopy);
/* 341*/        childSettings.remove(DisplaySetting.DISPLAY_OPTIONAL);
/*   0*/      } 
/* 347*/      if (comp == null) {
/* 349*/        list = this.options;
/*   0*/      } else {
/* 352*/        list = new ArrayList(this.options);
/* 353*/        Collections.sort(list, comp);
/*   0*/      } 
/* 357*/      for (Iterator i = list.iterator(); i.hasNext(); ) {
/* 358*/        Option option = (Option)i.next();
/* 361*/        option.appendUsage(buffer, childSettings, comp);
/* 364*/        if (i.hasNext()) {
/* 365*/            buffer.append(separator); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 370*/    if (both) {
/* 371*/        buffer.append(')'); 
/*   0*/       }
/* 374*/    if (optional && outer) {
/* 375*/        buffer.append(']'); 
/*   0*/       }
/* 378*/    if (arguments) {
/* 379*/        for (Iterator i = this.anonymous.iterator(); i.hasNext(); ) {
/* 380*/          buffer.append(' ');
/* 382*/          Option option = (Option)i.next();
/* 383*/          option.appendUsage(buffer, helpSettingsCopy, comp);
/*   0*/        }  
/*   0*/       }
/* 387*/    if (optional && !outer) {
/* 388*/        buffer.append(']'); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public List helpLines(int depth, Set helpSettings, Comparator comp) {
/* 395*/    List helpLines = new ArrayList();
/* 397*/    if (helpSettings.contains(DisplaySetting.DISPLAY_GROUP_NAME)) {
/* 398*/      HelpLine helpLine = new HelpLineImpl(this, depth);
/* 399*/      helpLines.add(helpLine);
/*   0*/    } 
/* 402*/    if (helpSettings.contains(DisplaySetting.DISPLAY_GROUP_EXPANDED)) {
/*   0*/      List list;
/* 406*/      if (comp == null) {
/* 408*/        list = this.options;
/*   0*/      } else {
/* 411*/        list = new ArrayList(this.options);
/* 412*/        Collections.sort(list, comp);
/*   0*/      } 
/* 416*/      for (Iterator i = list.iterator(); i.hasNext(); ) {
/* 417*/        Option option = (Option)i.next();
/* 418*/        helpLines.addAll(option.helpLines(depth + 1, helpSettings, comp));
/*   0*/      } 
/*   0*/    } 
/* 422*/    if (helpSettings.contains(DisplaySetting.DISPLAY_GROUP_ARGUMENT)) {
/* 423*/        for (Iterator i = this.anonymous.iterator(); i.hasNext(); ) {
/* 424*/          Option option = (Option)i.next();
/* 425*/          helpLines.addAll(option.helpLines(depth + 1, helpSettings, comp));
/*   0*/        }  
/*   0*/       }
/* 429*/    return helpLines;
/*   0*/  }
/*   0*/  
/*   0*/  public List getOptions() {
/* 438*/    return this.options;
/*   0*/  }
/*   0*/  
/*   0*/  public List getAnonymous() {
/* 446*/    return this.anonymous;
/*   0*/  }
/*   0*/  
/*   0*/  public Option findOption(String trigger) {
/* 450*/    Iterator i = getOptions().iterator();
/* 452*/    while (i.hasNext()) {
/* 453*/      Option option = (Option)i.next();
/* 454*/      Option found = option.findOption(trigger);
/* 456*/      if (found != null) {
/* 457*/          return found; 
/*   0*/         }
/*   0*/    } 
/* 461*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinimum() {
/* 465*/    return this.minimum;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximum() {
/* 469*/    return this.maximum;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRequired() {
/* 473*/    return (getMinimum() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public void defaults(WriteableCommandLine commandLine) {
/* 477*/    super.defaults(commandLine);
/* 479*/    for (Iterator i = this.options.iterator(); i.hasNext(); ) {
/* 480*/      Option option = (Option)i.next();
/* 481*/      option.defaults(commandLine);
/*   0*/    } 
/* 484*/    for (Iterator iterator1 = this.anonymous.iterator(); iterator1.hasNext(); ) {
/* 485*/      Option option = (Option)iterator1.next();
/* 486*/      option.defaults(commandLine);
/*   0*/    } 
/*   0*/  }
/*   0*/}
