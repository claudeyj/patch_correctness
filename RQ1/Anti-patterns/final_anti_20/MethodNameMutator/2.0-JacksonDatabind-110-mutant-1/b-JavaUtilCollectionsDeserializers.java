/*   0*/package com.fasterxml.jackson.databind.deser.impl;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.Converter;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public abstract class JavaUtilCollectionsDeserializers {
/*   0*/  private static final int TYPE_SINGLETON_SET = 1;
/*   0*/  
/*   0*/  private static final int TYPE_SINGLETON_LIST = 2;
/*   0*/  
/*   0*/  private static final int TYPE_SINGLETON_MAP = 3;
/*   0*/  
/*   0*/  private static final int TYPE_UNMODIFIABLE_SET = 4;
/*   0*/  
/*   0*/  private static final int TYPE_UNMODIFIABLE_LIST = 5;
/*   0*/  
/*   0*/  private static final int TYPE_UNMODIFIABLE_MAP = 6;
/*   0*/  
/*   0*/  public static final int TYPE_AS_LIST = 7;
/*   0*/  
/*  35*/  private static final Class<?> CLASS_AS_ARRAYS_LIST = Arrays.<Object>asList(new Object[] { null, null }).getClass();
/*   0*/  
/*   0*/  private static final Class<?> CLASS_SINGLETON_SET;
/*   0*/  
/*   0*/  private static final Class<?> CLASS_SINGLETON_LIST;
/*   0*/  
/*   0*/  private static final Class<?> CLASS_SINGLETON_MAP;
/*   0*/  
/*   0*/  private static final Class<?> CLASS_UNMODIFIABLE_SET;
/*   0*/  
/*   0*/  private static final Class<?> CLASS_UNMODIFIABLE_LIST;
/*   0*/  
/*   0*/  private static final Class<?> CLASS_UNMODIFIABLE_MAP;
/*   0*/  
/*   0*/  static {
/*  50*/    Set<?> set = Collections.singleton(Boolean.TRUE);
/*  51*/    CLASS_SINGLETON_SET = set.getClass();
/*  52*/    CLASS_UNMODIFIABLE_SET = Collections.unmodifiableSet(set).getClass();
/*  54*/    List<?> list = Collections.singletonList(Boolean.TRUE);
/*  55*/    CLASS_SINGLETON_LIST = list.getClass();
/*  56*/    CLASS_UNMODIFIABLE_LIST = Collections.unmodifiableList(list).getClass();
/*  59*/    Map<?, ?> map = Collections.singletonMap("a", "b");
/*  60*/    CLASS_SINGLETON_MAP = map.getClass();
/*  61*/    CLASS_UNMODIFIABLE_MAP = Collections.unmodifiableMap(map).getClass();
/*   0*/  }
/*   0*/  
/*   0*/  public static JsonDeserializer<?> findForCollection(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*   0*/    JavaUtilCollectionsConverter conv;
/*  71*/    if (type.hasRawClass(CLASS_AS_ARRAYS_LIST)) {
/*  72*/      conv = converter(7, type, List.class);
/*  73*/    } else if (type.hasRawClass(CLASS_SINGLETON_LIST)) {
/*  74*/      conv = converter(2, type, List.class);
/*  75*/    } else if (type.hasRawClass(CLASS_SINGLETON_SET)) {
/*  76*/      conv = converter(1, type, Set.class);
/*  78*/    } else if (type.isTypeOrSuperTypeOf(CLASS_UNMODIFIABLE_LIST)) {
/*  79*/      conv = converter(5, type, List.class);
/*  80*/    } else if (type.hasRawClass(CLASS_UNMODIFIABLE_SET)) {
/*  81*/      conv = converter(4, type, Set.class);
/*   0*/    } else {
/*  83*/      return null;
/*   0*/    } 
/*  85*/    return new StdDelegatingDeserializer(conv);
/*   0*/  }
/*   0*/  
/*   0*/  public static JsonDeserializer<?> findForMap(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
/*   0*/    JavaUtilCollectionsConverter conv;
/*  95*/    if (type.hasRawClass(CLASS_SINGLETON_MAP)) {
/*  96*/      conv = converter(3, type, Map.class);
/*  97*/    } else if (type.hasRawClass(CLASS_UNMODIFIABLE_MAP)) {
/*  98*/      conv = converter(6, type, Map.class);
/*   0*/    } else {
/* 100*/      return null;
/*   0*/    } 
/* 102*/    return new StdDelegatingDeserializer(conv);
/*   0*/  }
/*   0*/  
/*   0*/  static JavaUtilCollectionsConverter converter(int kind, JavaType concreteType, Class<?> rawSuper) {
/* 108*/    return new JavaUtilCollectionsConverter(kind, concreteType.findSuperType(rawSuper));
/*   0*/  }
/*   0*/  
/*   0*/  private static class JavaUtilCollectionsConverter implements Converter<Object, Object> {
/*   0*/    private final JavaType _inputType;
/*   0*/    
/*   0*/    private final int _kind;
/*   0*/    
/*   0*/    private JavaUtilCollectionsConverter(int kind, JavaType inputType) {
/* 123*/      this._inputType = inputType;
/* 124*/      this._kind = kind;
/*   0*/    }
/*   0*/    
/*   0*/    public Object convert(Object value) {
/*   0*/      Set<?> set;
/*   0*/      List<?> list;
/*   0*/      Map<?, ?> map;
/*   0*/      Map.Entry<?, ?> entry;
/* 129*/      if (value == null) {
/* 130*/          return null; 
/*   0*/         }
/* 133*/      switch (this._kind) {
/*   0*/        case 1:
/* 136*/          set = (Set)value;
/* 137*/          _checkSingleton(set.size());
/* 138*/          return Collections.singleton(set.iterator().next());
/*   0*/        case 2:
/* 142*/          list = (List)value;
/* 143*/          _checkSingleton(list.size());
/* 144*/          return Collections.singletonList(list.get(0));
/*   0*/        case 3:
/* 148*/          map = (Map<?, ?>)value;
/* 149*/          _checkSingleton(map.size());
/* 150*/          entry = map.entrySet().iterator().next();
/* 151*/          return Collections.singletonMap(entry.getKey(), entry.getValue());
/*   0*/        case 4:
/* 155*/          return Collections.unmodifiableSet((Set)value);
/*   0*/        case 5:
/* 157*/          return Collections.unmodifiableList((List)value);
/*   0*/        case 6:
/* 159*/          return Collections.unmodifiableMap((Map<?, ?>)value);
/*   0*/      } 
/* 164*/      return value;
/*   0*/    }
/*   0*/    
/*   0*/    public JavaType getInputType(TypeFactory typeFactory) {
/* 170*/      return this._inputType;
/*   0*/    }
/*   0*/    
/*   0*/    public JavaType getOutputType(TypeFactory typeFactory) {
/* 176*/      return this._inputType;
/*   0*/    }
/*   0*/    
/*   0*/    private void _checkSingleton(int size) {
/* 180*/      if (size != 1) {
/* 182*/          throw new IllegalArgumentException("Can not deserialize Singleton container from " + size + " entries"); 
/*   0*/         }
/*   0*/    }
/*   0*/  }
/*   0*/}
