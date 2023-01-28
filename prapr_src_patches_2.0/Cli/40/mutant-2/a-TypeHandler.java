/*   0*/package org.apache.commons.cli;
/*   0*/
/*   0*/import java.io.File;
/*   0*/import java.io.FileInputStream;
/*   0*/import java.io.FileNotFoundException;
/*   0*/import java.net.MalformedURLException;
/*   0*/import java.net.URL;
/*   0*/import java.util.Date;
/*   0*/
/*   0*/public class TypeHandler {
/*   0*/  public static Object createValue(String str, Object obj) throws ParseException {
/*  49*/    return createValue(str, (Class)obj);
/*   0*/  }
/*   0*/  
/*   0*/  public static <T> T createValue(String str, Class<T> clazz) throws ParseException {
/*  65*/    if (PatternOptionBuilder.STRING_VALUE == clazz) {
/*  67*/        return (T)str; 
/*   0*/       }
/*  69*/    if (PatternOptionBuilder.OBJECT_VALUE == clazz) {
/*  71*/        return (T)createObject(str); 
/*   0*/       }
/*  73*/    if (PatternOptionBuilder.NUMBER_VALUE == clazz) {
/*  75*/        return (T)createNumber(str); 
/*   0*/       }
/*  77*/    if (PatternOptionBuilder.DATE_VALUE == clazz) {
/*  79*/        return (T)createDate(str); 
/*   0*/       }
/*  81*/    if (PatternOptionBuilder.CLASS_VALUE == clazz) {
/*  83*/        return (T)createClass(str); 
/*   0*/       }
/*  85*/    if (PatternOptionBuilder.FILE_VALUE == clazz) {
/*  87*/        return (T)createFile(str); 
/*   0*/       }
/*  89*/    if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz) {
/*  91*/        return (T)openFile(str); 
/*   0*/       }
/*  93*/    if (PatternOptionBuilder.FILES_VALUE == clazz) {
/*  95*/        return (T)createFiles(str); 
/*   0*/       }
/*  97*/    if (PatternOptionBuilder.URL_VALUE == clazz) {
/*  99*/        return (T)createURL(str); 
/*   0*/       }
/* 103*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static Object createObject(String classname) throws ParseException {
/*   0*/    Class<?> cl;
/*   0*/    try {
/* 120*/      cl = Class.forName(classname);
/* 122*/    } catch (ClassNotFoundException cnfe) {
/* 124*/      throw new ParseException("Unable to find the class: " + classname);
/*   0*/    } 
/*   0*/    try {
/* 129*/      return cl.newInstance();
/* 131*/    } catch (Exception e) {
/* 133*/      throw new ParseException(e.getClass().getName() + "; Unable to create an instance of: " + classname);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Number createNumber(String str) throws ParseException {
/*   0*/    try {
/* 149*/      if (str.indexOf('.') != -1) {
/* 151*/          return Double.valueOf(str); 
/*   0*/         }
/* 153*/      return Long.valueOf(str);
/* 155*/    } catch (NumberFormatException e) {
/* 157*/      throw new ParseException(e.getMessage());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Class<?> createClass(String classname) throws ParseException {
/*   0*/    try {
/* 172*/      return Class.forName(classname);
/* 174*/    } catch (ClassNotFoundException e) {
/* 176*/      throw new ParseException("Unable to find the class: " + classname);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Date createDate(String str) {
/* 193*/    throw new UnsupportedOperationException("Not yet implemented");
/*   0*/  }
/*   0*/  
/*   0*/  public static URL createURL(String str) throws ParseException {
/*   0*/    try {
/* 207*/      return new URL(str);
/* 209*/    } catch (MalformedURLException e) {
/* 211*/      throw new ParseException("Unable to parse the URL: " + str);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static File createFile(String str) {
/* 223*/    return new File(str);
/*   0*/  }
/*   0*/  
/*   0*/  public static FileInputStream openFile(String str) throws ParseException {
/*   0*/    try {
/* 237*/      return new FileInputStream(str);
/* 239*/    } catch (FileNotFoundException e) {
/* 241*/      throw new ParseException("Unable to find file: " + str);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static File[] createFiles(String str) {
/* 259*/    throw new UnsupportedOperationException("Not yet implemented");
/*   0*/  }
/*   0*/}
