/*   0*/package com.fasterxml.jackson.dataformat.xml.util;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*   0*/import com.fasterxml.jackson.databind.type.ClassKey;
/*   0*/import com.fasterxml.jackson.databind.util.LRUMap;
/*   0*/import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.dataformat.xml.XmlTestBase;
/*   0*/import java.io.Serializable;
/*   0*/import javax.xml.namespace.QName;
/*   0*/
/*   0*/public class XmlRootNameLookup implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  28*/  protected final transient LRUMap<ClassKey, QName> _rootNames = new LRUMap(40, 200);
/*   0*/  
/*   0*/  protected Object readResolve() {
/*  34*/    if (this._rootNames == null) {
/*  35*/        return new XmlRootNameLookup(); 
/*   0*/       }
/*  37*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public QName findRootName(JavaType rootType, MapperConfig<?> config) {
/*  41*/    return findRootName(rootType.getRawClass(), config);
/*   0*/  }
/*   0*/  
/*   0*/  public QName findRootName(Class<?> rootType, MapperConfig<?> config) {
/*  46*/    ClassKey key = new ClassKey(rootType);
/*  48*/    synchronized (this._rootNames) {
/*  49*/      name = (QName)this._rootNames.get(key);
/*   0*/    } 
/*  51*/    if (name != null) {
/*  52*/        return name; 
/*   0*/       }
/*  54*/    QName name = _findRootName(rootType, config);
/*  55*/    synchronized (this._rootNames) {
/*  56*/      QName qName = name;
/*  56*/      ClassKey classKey = key;
/*  56*/      new XmlTestBase.IntWrapper(0);
/*   0*/    } 
/*  58*/    return name;
/*   0*/  }
/*   0*/  
/*   0*/  protected QName _findRootName(Class<?> rootType, MapperConfig<?> config) {
/*  65*/    BeanDescription beanDesc = config.introspectClassAnnotations(rootType);
/*  66*/    AnnotationIntrospector intr = config.getAnnotationIntrospector();
/*  67*/    AnnotatedClass ac = beanDesc.getClassInfo();
/*  68*/    String localName = null;
/*  69*/    String ns = null;
/*  71*/    PropertyName root = intr.findRootName(ac);
/*  72*/    if (root != null) {
/*  73*/      localName = root.getSimpleName();
/*  74*/      ns = root.getNamespace();
/*   0*/    } 
/*  77*/    if (localName == null || localName.length() == 0) {
/*  80*/      localName = StaxUtil.sanitizeXmlTypeName(rootType.getSimpleName());
/*  81*/      return new QName("", localName);
/*   0*/    } 
/*  84*/    if (ns == null || ns.length() == 0) {
/*  85*/        ns = findNamespace(intr, ac); 
/*   0*/       }
/*  87*/    if (ns == null) {
/*  88*/        ns = ""; 
/*   0*/       }
/*  90*/    return new QName(ns, localName);
/*   0*/  }
/*   0*/  
/*   0*/  private String findNamespace(AnnotationIntrospector ai, AnnotatedClass ann) {
/*  95*/    for (AnnotationIntrospector intr : (Iterable<AnnotationIntrospector>)ai.allIntrospectors()) {
/*  96*/      if (intr instanceof XmlAnnotationIntrospector) {
/*  97*/        String ns = ((XmlAnnotationIntrospector)intr).findNamespace((Annotated)ann);
/*  98*/        if (ns != null) {
/*  99*/            return ns; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 103*/    return null;
/*   0*/  }
/*   0*/}
