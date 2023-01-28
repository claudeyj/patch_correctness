/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/
/*   0*/public class PosixParser extends Parser {
/*  35*/  private List tokens = new ArrayList();
/*   0*/  
/*   0*/  private boolean eatTheRest;
/*   0*/  
/*   0*/  private Options options;
/*   0*/  
/*   0*/  private void init() {
/*  52*/    this.eatTheRest = false;
/*  53*/    this.tokens.clear();
/*   0*/  }
/*   0*/  
/*   0*/  protected String[] flatten(Options options, String[] arguments, boolean stopAtNonOption) {
/*  96*/    init();
/*  97*/    this.options = options;
/* 100*/    Iterator iter = Arrays.asList(arguments).iterator();
/* 103*/    while (iter.hasNext()) {
/* 106*/      String token = (String)iter.next();
/* 109*/      if (token.startsWith("--")) {
/* 111*/        int pos = token.indexOf('=');
/* 112*/        String opt = (pos == -1) ? token : token.substring(0, pos);
/* 114*/        if (!options.hasOption(opt)) {
/* 116*/          processNonOptionToken(token);
/*   0*/        } else {
/* 121*/          this.tokens.add(opt);
/* 122*/          if (pos != -1) {
/* 124*/              this.tokens.add(token.substring(pos + 1)); 
/*   0*/             }
/*   0*/        } 
/* 130*/      } else if ("-".equals(token)) {
/* 132*/        this.tokens.add(token);
/* 134*/      } else if (token.startsWith("-")) {
/* 136*/        if (token.length() == 2 || options.hasOption(token)) {
/* 138*/          processOptionToken(token, stopAtNonOption);
/*   0*/        } else {
/* 143*/          burstToken(token, stopAtNonOption);
/*   0*/        } 
/* 146*/      } else if (stopAtNonOption) {
/* 148*/        processNonOptionToken(token);
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
/*   0*/  private void processNonOptionToken(String value) {
/* 186*/    this.eatTheRest = true;
/* 187*/    this.tokens.remove("--");
/* 189*/    this.tokens.add(value);
/*   0*/  }
/*   0*/  
/*   0*/  private void processOptionToken(String token, boolean stopAtNonOption) {
/* 206*/    if (stopAtNonOption && !this.options.hasOption(token)) {
/* 208*/        this.eatTheRest = true; 
/*   0*/       }
/* 212*/    this.tokens.add(token);
/*   0*/  }
/*   0*/  
/*   0*/  protected void burstToken(String token, boolean stopAtNonOption) {
/* 244*/    for (int i = 1; i < token.length(); i++) {
/* 246*/      String ch = String.valueOf(token.charAt(i));
/* 248*/      if (this.options.hasOption(ch)) {
/* 250*/        this.tokens.add("-" + ch);
/* 251*/        Option currentOption = this.options.getOption(ch);
/* 253*/        if (currentOption.hasArg() && token.length() != i + 1) {
/* 255*/          this.tokens.add(token.substring(i + 1));
/*   0*/          break;
/*   0*/        } 
/*   0*/      } else {
/* 260*/        if (stopAtNonOption) {
/* 262*/          processNonOptionToken(token.substring(i));
/*   0*/          break;
/*   0*/        } 
/* 267*/        this.tokens.add(token);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/}
