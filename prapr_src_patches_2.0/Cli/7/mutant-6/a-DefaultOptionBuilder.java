/*   0*/package org.apache.commons.cli2.builder;
/*   0*/
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Set;
/*   0*/import org.apache.commons.cli2.Argument;
/*   0*/import org.apache.commons.cli2.Group;
/*   0*/import org.apache.commons.cli2.option.DefaultOption;
/*   0*/import org.apache.commons.cli2.resource.ResourceHelper;
/*   0*/
/*   0*/public class DefaultOptionBuilder {
/*   0*/  private final String shortPrefix;
/*   0*/  
/*   0*/  private final String longPrefix;
/*   0*/  
/*   0*/  private final boolean burstEnabled;
/*   0*/  
/*   0*/  private String preferredName;
/*   0*/  
/*   0*/  private Set aliases;
/*   0*/  
/*   0*/  private Set burstAliases;
/*   0*/  
/*   0*/  private boolean required;
/*   0*/  
/*   0*/  private String description;
/*   0*/  
/*   0*/  private Argument argument;
/*   0*/  
/*   0*/  private Group children;
/*   0*/  
/*   0*/  private int id;
/*   0*/  
/*   0*/  public DefaultOptionBuilder() {
/*  51*/    this("-", "--", true);
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder(String shortPrefix, String longPrefix, boolean burstEnabled) throws IllegalArgumentException {
/*  67*/    if (shortPrefix == null || shortPrefix.length() == 0) {
/*  68*/        throw new IllegalArgumentException(ResourceHelper.getResourceHelper().getMessage("Option.illegal.short.prefix")); 
/*   0*/       }
/*  71*/    if (longPrefix == null || longPrefix.length() == 0) {
/*  72*/        throw new IllegalArgumentException(ResourceHelper.getResourceHelper().getMessage("Option.illegal.long.prefix")); 
/*   0*/       }
/*  75*/    this.shortPrefix = shortPrefix;
/*  76*/    this.longPrefix = longPrefix;
/*  77*/    this.burstEnabled = burstEnabled;
/*  78*/    reset();
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOption create() throws IllegalStateException {
/*  88*/    if (this.preferredName == null) {
/*  89*/        throw new IllegalStateException(ResourceHelper.getResourceHelper().getMessage("Option.no.name")); 
/*   0*/       }
/*  92*/    DefaultOption option = new DefaultOption(this.shortPrefix, this.longPrefix, this.burstEnabled, this.preferredName, this.description, this.aliases, this.burstAliases, this.required, this.argument, this.children, this.id);
/*  96*/    reset();
/*  98*/    return option;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder reset() {
/* 105*/    this.preferredName = null;
/* 106*/    this.description = null;
/* 107*/    this.aliases = new HashSet();
/* 108*/    this.burstAliases = new HashSet();
/* 109*/    this.required = false;
/* 110*/    this.argument = null;
/* 111*/    this.children = null;
/* 112*/    this.id = 0;
/* 114*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder withShortName(String shortName) {
/* 125*/    String name = this.shortPrefix + shortName;
/* 127*/    if (this.preferredName == null) {
/* 128*/      this.preferredName = name;
/*   0*/    } else {
/* 130*/      this.aliases.add(name);
/*   0*/    } 
/* 133*/    if (this.burstEnabled && name.length() == this.shortPrefix.length() + 1) {
/* 134*/        this.burstAliases.add(name); 
/*   0*/       }
/* 137*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder withLongName(String longName) {
/* 148*/    String name = this.longPrefix + longName;
/* 150*/    if (this.preferredName == null) {
/* 151*/      this.preferredName = name;
/*   0*/    } else {
/* 153*/      this.aliases.add(name);
/*   0*/    } 
/* 156*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder withDescription(String newDescription) {
/* 165*/    this.description = newDescription;
/* 167*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder withRequired(boolean newRequired) {
/* 176*/    this.required = newRequired;
/* 178*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder withChildren(Group newChildren) {
/* 187*/    this.children = newChildren;
/* 189*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public DefaultOptionBuilder withArgument(Argument newArgument) {
/* 198*/    this.argument = newArgument;
/* 200*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public final DefaultOptionBuilder withId(int newId) {
/* 211*/    this.id = newId;
/* 213*/    return this;
/*   0*/  }
/*   0*/}
