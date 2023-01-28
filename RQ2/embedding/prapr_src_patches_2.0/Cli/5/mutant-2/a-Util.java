/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/class Util {
/*   0*/  static String stripLeadingHyphens(String str) {
/*  36*/    if (str.startsWith("--")) {
/*  38*/        return str.substring(2, str.length()); 
/*   0*/       }
/*  40*/    if (str.startsWith("-")) {
/*  42*/        return str.substring(1, str.length()); 
/*   0*/       }
/*  45*/    return str;
/*   0*/  }
/*   0*/  
/*   0*/  static String stripLeadingAndTrailingQuotes(String str) {
/*  59*/    if (str.startsWith("\"")) {
/*  60*/        str = str.substring(1, str.length()); 
/*   0*/       }
/*  62*/    if (str.endsWith("\"")) {
/*  63*/        str = str.substring(0, str.length() - 1); 
/*   0*/       }
/*  65*/    return str;
/*   0*/  }
/*   0*/}
