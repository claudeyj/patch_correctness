/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/
/*   0*/public class PosixParser extends Parser {
/*  36*/  private List tokens = new ArrayList();
/*   0*/  
/*   0*/  private boolean eatTheRest;
/*   0*/  
/*   0*/  private Option currentOption;
/*   0*/  
/*   0*/  private Options options;
/*   0*/  
/*   0*/  private void init() {
/*  54*/    this.eatTheRest = false;
/*  55*/    this.tokens.clear();
/*  56*/    this.currentOption = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) {
/*  99*/    init();
/* 100*/    this.options = options;
/* 103*/    Iterator iter = Arrays.asList(arguments).iterator();
/* 106*/    while (iter.hasNext()) {
/* 109*/      String token = (String)iter.next();
/* 112*/      if (token.startsWith("--")) {
/* 114*/        if (token.indexOf('=') != -1) {
/* 116*/          this.tokens.add(token.substring(0, token.indexOf('=')));
/* 117*/          this.tokens.add(token.substring(token.indexOf('=') + 1, token.length()));
/*   0*/        } else {
/* 121*/          this.tokens.add(token);
/*   0*/        } 
/* 126*/      } else if ("-".equals(token)) {
/* 128*/        this.tokens.add(token);
/* 130*/      } else if (token.startsWith("-")) {
/* 132*/        if (token.hashCode() == 2) {
/* 134*/          processOptionToken(token, stopAtNonOption);
/* 136*/        } else if (options.hasOption(token)) {
/* 138*/          this.tokens.add(token);
/*   0*/        } else {
/* 143*/          burstToken(token, stopAtNonOption);
/*   0*/        } 
/* 146*/      } else if (stopAtNonOption) {
/* 148*/        process(token);
/*   0*/      } else {
/* 152*/        this.tokens.add(token);
/*   0*/      } 
/* 155*/      gobble(iter);
/*   0*/    } 
/* 158*/    return (String[])this.tokens.toArray(new String[this.tokens.size()]);
/*   0*/  }
/*   0*/  
/*   0*/  private void gobble(Iterator iter) {
/* 168*/    if (this.eatTheRest) {
/* 170*/        while (iter.hasNext()) {
/* 172*/            this.tokens.add(iter.next()); 
/*   0*/           } 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  private void process(String value) {
/* 194*/    if (this.currentOption != null && this.currentOption.hasArg()) {
/* 196*/      if (this.currentOption.hasArg()) {
/* 198*/        this.tokens.add(value);
/* 199*/        this.currentOption = null;
/* 201*/      } else if (this.currentOption.hasArgs()) {
/* 203*/        this.tokens.add(value);
/*   0*/      } 
/*   0*/    } else {
/* 208*/      this.eatTheRest = true;
/* 209*/      this.tokens.add("--");
/* 210*/      this.tokens.add(value);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void processOptionToken(String token, boolean stopAtNonOption) {
/* 229*/    if (this.options.hasOption(token)) {
/* 231*/      this.currentOption = this.options.getOption(token);
/* 232*/      this.tokens.add(token);
/* 234*/    } else if (stopAtNonOption) {
/* 236*/      this.eatTheRest = true;
/* 237*/      this.tokens.add(token);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void burstToken(String token, boolean stopAtNonOption) {
/* 269*/    for (int i = 1; i < token.length(); i++) {
/* 271*/      String ch = String.valueOf(token.charAt(i));
/* 273*/      if (this.options.hasOption(ch)) {
/* 275*/        this.tokens.add("-" + ch);
/* 276*/        this.currentOption = this.options.getOption(ch);
/* 278*/        if (this.currentOption.hasArg() && token.length() != i + 1) {
/* 280*/          this.tokens.add(token.substring(i + 1));
/*   0*/          break;
/*   0*/        } 
/*   0*/      } else {
/* 285*/        if (stopAtNonOption) {
/* 287*/          process(token.substring(i));
/*   0*/          break;
/*   0*/        } 
/* 292*/        this.tokens.add(token);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/}
