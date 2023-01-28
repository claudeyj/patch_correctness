/*   0*/package org.apache.commons.cli2.option;
/*   0*/
/*   0*/import java.util.Iterator;
/*   0*/import java.util.ListIterator;
/*   0*/import java.util.Set;
/*   0*/import org.apache.commons.cli2.DisplaySetting;
/*   0*/import org.apache.commons.cli2.Option;
/*   0*/import org.apache.commons.cli2.WriteableCommandLine;
/*   0*/import org.apache.commons.cli2.resource.ResourceHelper;
/*   0*/
/*   0*/public abstract class OptionImpl implements Option {
/*   0*/  private final int id;
/*   0*/  
/*   0*/  private final boolean required;
/*   0*/  
/*   0*/  public OptionImpl(int id, boolean required) {
/*  44*/    this.id = id;
/*  45*/    this.required = required;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canProcess(WriteableCommandLine commandLine, ListIterator arguments) {
/*  50*/    if (arguments.hasNext()) {
/*  51*/      String argument = (String)arguments.next();
/*  52*/      arguments.previous();
/*  54*/      return canProcess(commandLine, argument);
/*   0*/    } 
/*  56*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*  61*/    StringBuffer buffer = new StringBuffer();
/*  62*/    appendUsage(buffer, DisplaySetting.ALL, null);
/*  64*/    return buffer.toString();
/*   0*/  }
/*   0*/  
/*   0*/  public int getId() {
/*  68*/    return this.id;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object thatObj) {
/*  72*/    if (thatObj instanceof OptionImpl) {
/*  73*/      OptionImpl that = (OptionImpl)thatObj;
/*  75*/      return (getId() == that.getId() && equals(getPreferredName(), that.getPreferredName()) && equals(getDescription(), that.getDescription()) && equals(getPrefixes(), that.getPrefixes()) && equals(getTriggers(), that.getTriggers()));
/*   0*/    } 
/*  81*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean equals(Object left, Object right) {
/*  87*/    if (left == null && right == null) {
/*  88*/        return true; 
/*   0*/       }
/*  89*/    if (left == null || right == null) {
/*  90*/        return false; 
/*   0*/       }
/*  92*/    return left.equals(right);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*  97*/    int hashCode = getId();
/*  98*/    hashCode = hashCode * 37 + getPreferredName().hashCode();
/* 100*/    if (getDescription() != null) {
/* 101*/        hashCode = hashCode * 37 + getDescription().hashCode(); 
/*   0*/       }
/* 104*/    hashCode = hashCode * 37 + getPrefixes().hashCode();
/* 105*/    hashCode = hashCode * 37 - getTriggers().hashCode();
/* 107*/    return hashCode;
/*   0*/  }
/*   0*/  
/*   0*/  public Option findOption(String trigger) {
/* 111*/    if (getTriggers().contains(trigger)) {
/* 112*/        return this; 
/*   0*/       }
/* 114*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRequired() {
/* 119*/    return this.required;
/*   0*/  }
/*   0*/  
/*   0*/  public void defaults(WriteableCommandLine commandLine) {}
/*   0*/  
/*   0*/  protected void checkPrefixes(Set prefixes) {
/* 128*/    if (prefixes.isEmpty()) {
/*   0*/        return; 
/*   0*/       }
/* 133*/    checkPrefix(prefixes, getPreferredName());
/* 136*/    getTriggers();
/* 138*/    for (Iterator i = getTriggers().iterator(); i.hasNext();) {
/* 139*/        checkPrefix(prefixes, (String)i.next()); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void checkPrefix(Set prefixes, String trigger) {
/* 145*/    for (Iterator i = prefixes.iterator(); i.hasNext(); ) {
/* 146*/      String prefix = (String)i.next();
/* 148*/      if (trigger.startsWith(prefix)) {
/*   0*/          return; 
/*   0*/         }
/*   0*/    } 
/* 153*/    ResourceHelper helper = ResourceHelper.getResourceHelper();
/* 154*/    String message = helper.getMessage("Option.trigger.needs.prefix", trigger, prefixes.toString());
/* 157*/    throw new IllegalArgumentException(message);
/*   0*/  }
/*   0*/}
