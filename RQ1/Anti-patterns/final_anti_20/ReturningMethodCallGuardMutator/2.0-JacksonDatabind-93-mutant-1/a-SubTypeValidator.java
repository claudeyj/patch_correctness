/*   0*/package com.fasterxml.jackson.databind.jsontype.impl;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class SubTypeValidator {
/*   0*/  protected static final String PREFIX_STRING = "org.springframework.";
/*   0*/  
/*   0*/  protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
/*   0*/  
/*   0*/  static {
/*  28*/    Set<String> s = new HashSet<>();
/*  31*/    s.add("org.apache.commons.collections.functors.InvokerTransformer");
/*  32*/    s.add("org.apache.commons.collections.functors.InstantiateTransformer");
/*  33*/    s.add("org.apache.commons.collections4.functors.InvokerTransformer");
/*  34*/    s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
/*  35*/    s.add("org.codehaus.groovy.runtime.ConvertedClosure");
/*  36*/    s.add("org.codehaus.groovy.runtime.MethodClosure");
/*  37*/    s.add("org.springframework.beans.factory.ObjectFactory");
/*  38*/    s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
/*  39*/    s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
/*  41*/    s.add("com.sun.rowset.JdbcRowSetImpl");
/*  43*/    s.add("java.util.logging.FileHandler");
/*  44*/    s.add("java.rmi.server.UnicastRemoteObject");
/*  47*/    s.add("org.springframework.beans.factory.config.PropertyPathFactoryBean");
/*  48*/    s.add("com.mchange.v2.c3p0.JndiRefForwardingDataSource");
/*  49*/    s.add("com.mchange.v2.c3p0.WrapperConnectionPoolDataSource");
/*  51*/    s.add("org.apache.tomcat.dbcp.dbcp2.BasicDataSource");
/*  52*/    s.add("com.sun.org.apache.bcel.internal.util.ClassLoader");
/*  53*/    DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet(s);
/*   0*/  }
/*   0*/  
/*  59*/  protected Set<String> _cfgIllegalClassNames = DEFAULT_NO_DESER_CLASS_NAMES;
/*   0*/  
/*  61*/  private static final SubTypeValidator instance = new SubTypeValidator();
/*   0*/  
/*   0*/  public static SubTypeValidator instance() {
/*  65*/    return instance;
/*   0*/  }
/*   0*/  
/*   0*/  public void validateSubType(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*  71*/    Class<?> raw = type.getRawClass();
/*  72*/    String full = raw.getName();
/*  76*/    if (this._cfgIllegalClassNames.contains(full)) {
/*  97*/        throw JsonMappingException.from(ctxt, String.format("Illegal type (%s) to deserialize: prevented for security reasons", new Object[] { full })); 
/*   0*/       }
/*   0*/    if (full.startsWith("org.springframework.")) {
/*   0*/        for (Class<?> cls = raw; cls != Object.class; ) {
/*   0*/          String name = cls.getSimpleName();
/*   0*/          if (!"AbstractPointcutAdvisor".equals(name)) {
/*   0*/            if ("AbstractApplicationContext".equals(name)) {
/*   0*/                // Byte code: goto -> 90 
/*   0*/               }
/*   0*/            cls = cls.getSuperclass();
/*   0*/          } 
/*   0*/          // Byte code: goto -> 90
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/}
