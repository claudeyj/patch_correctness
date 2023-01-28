/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class Options implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  49*/  private final Map<String, Option> shortOpts = new LinkedHashMap<String, Option>();
/*   0*/  
/*  52*/  private final Map<String, Option> longOpts = new LinkedHashMap<String, Option>();
/*   0*/  
/*  57*/  private final List<Object> requiredOpts = new ArrayList();
/*   0*/  
/*  60*/  private final Map<String, OptionGroup> optionGroups = new LinkedHashMap<String, OptionGroup>();
/*   0*/  
/*   0*/  public Options addOptionGroup(OptionGroup group) {
/*  70*/    if (group.isRequired()) {
/*  72*/        this.requiredOpts.add(group); 
/*   0*/       }
/*  75*/    for (Option option : group.getOptions()) {
/*  80*/      option.setRequired(false);
/*  81*/      addOption(option);
/*  83*/      this.optionGroups.put(option.getKey(), group);
/*   0*/    } 
/*  86*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  Collection<OptionGroup> getOptionGroups() {
/*  96*/    return new HashSet<OptionGroup>(this.optionGroups.values());
/*   0*/  }
/*   0*/  
/*   0*/  public Options addOption(String opt, String description) {
/* 113*/    addOption(opt, null, false, description);
/* 114*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Options addOption(String opt, boolean hasArg, String description) {
/* 131*/    addOption(opt, null, hasArg, description);
/* 132*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Options addOption(String opt, String longOpt, boolean hasArg, String description) {
/* 150*/    addOption(new Option(opt, longOpt, hasArg, description));
/* 151*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Options addRequiredOption(String opt, String longOpt, boolean hasArg, String description) {
/* 178*/    Option option = new Option(opt, longOpt, hasArg, description);
/* 179*/    option.setRequired(true);
/* 180*/    addOption(option);
/* 181*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Options addOption(Option opt) {
/* 192*/    String key = opt.getKey();
/* 195*/    if (opt.hasLongOpt()) {
/* 197*/        this.longOpts.put(opt.getLongOpt(), opt); 
/*   0*/       }
/* 201*/    if (opt.isRequired()) {
/* 203*/      if (this.requiredOpts.contains(key)) {
/* 205*/          this.requiredOpts.remove(this.requiredOpts.indexOf(key)); 
/*   0*/         }
/* 207*/      this.requiredOpts.add(key);
/*   0*/    } 
/* 210*/    this.shortOpts.put(key, opt);
/* 212*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<Option> getOptions() {
/* 222*/    return Collections.unmodifiableCollection(helpOptions());
/*   0*/  }
/*   0*/  
/*   0*/  List<Option> helpOptions() {
/* 232*/    return new ArrayList<Option>(this.shortOpts.values());
/*   0*/  }
/*   0*/  
/*   0*/  public List getRequiredOptions() {
/* 242*/    return Collections.unmodifiableList(this.requiredOpts);
/*   0*/  }
/*   0*/  
/*   0*/  public Option getOption(String opt) {
/* 257*/    opt = Util.stripLeadingHyphens(opt);
/* 259*/    if (this.shortOpts.containsKey(opt)) {
/* 261*/        return this.shortOpts.get(opt); 
/*   0*/       }
/* 264*/    return this.longOpts.get(opt);
/*   0*/  }
/*   0*/  
/*   0*/  public List<String> getMatchingOptions(String opt) {
/* 276*/    opt = Util.stripLeadingHyphens(opt);
/* 278*/    List<String> matchingOpts = new ArrayList<String>();
/* 281*/    if (this.longOpts.keySet().contains(opt)) {
/* 283*/        return Collections.singletonList(opt); 
/*   0*/       }
/* 286*/    for (String longOpt : this.shortOpts.keySet()) {
/* 288*/      if (longOpt.startsWith(opt)) {
/* 290*/          matchingOpts.add(longOpt); 
/*   0*/         }
/*   0*/    } 
/* 294*/    return matchingOpts;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOption(String opt) {
/* 305*/    opt = Util.stripLeadingHyphens(opt);
/* 307*/    return (this.shortOpts.containsKey(opt) || this.longOpts.containsKey(opt));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasLongOption(String opt) {
/* 319*/    opt = Util.stripLeadingHyphens(opt);
/* 321*/    return this.longOpts.containsKey(opt);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasShortOption(String opt) {
/* 333*/    opt = Util.stripLeadingHyphens(opt);
/* 335*/    return this.shortOpts.containsKey(opt);
/*   0*/  }
/*   0*/  
/*   0*/  public OptionGroup getOptionGroup(Option opt) {
/* 346*/    return this.optionGroups.get(opt.getKey());
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 357*/    StringBuilder buf = new StringBuilder();
/* 359*/    buf.append("[ Options: [ short ");
/* 360*/    buf.append(this.shortOpts.toString());
/* 361*/    buf.append(" ] [ long ");
/* 362*/    buf.append(this.longOpts);
/* 363*/    buf.append(" ]");
/* 365*/    return buf.toString();
/*   0*/  }
/*   0*/}
