/*   0*/package com.fasterxml.jackson.databind.ser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonInclude;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonSerializer;
/*   0*/import com.fasterxml.jackson.databind.SerializationConfig;
/*   0*/import com.fasterxml.jackson.databind.SerializationFeature;
/*   0*/import com.fasterxml.jackson.databind.SerializerProvider;
/*   0*/import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*   0*/import com.fasterxml.jackson.databind.util.Annotations;
/*   0*/import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/
/*   0*/public class PropertyBuilder {
/*  18*/  private static final Object NO_DEFAULT_MARKER = Boolean.FALSE;
/*   0*/  
/*   0*/  protected final SerializationConfig _config;
/*   0*/  
/*   0*/  protected final BeanDescription _beanDesc;
/*   0*/  
/*   0*/  protected final JsonInclude.Value _defaultInclusion;
/*   0*/  
/*   0*/  protected final AnnotationIntrospector _annotationIntrospector;
/*   0*/  
/*   0*/  protected Object _defaultBean;
/*   0*/  
/*   0*/  public PropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
/*  45*/    this._config = config;
/*  46*/    this._beanDesc = beanDesc;
/*  47*/    this._defaultInclusion = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion(beanDesc.getBeanClass()));
/*  49*/    this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*   0*/  }
/*   0*/  
/*   0*/  public Annotations getClassAnnotations() {
/*  59*/    return this._beanDesc.getClassAnnotations();
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanPropertyWriter buildWriter(SerializerProvider prov, BeanPropertyDefinition propDef, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping) throws JsonMappingException {
/*  75*/    JavaType serializationType = findSerializationType(am, defaultUseStaticTyping, declaredType);
/*  78*/    if (contentTypeSer != null) {
/*  83*/      if (serializationType == null) {
/*  85*/          serializationType = declaredType; 
/*   0*/         }
/*  87*/      JavaType ct = serializationType.getContentType();
/*  89*/      if (ct == null) {
/*  90*/          throw new IllegalStateException("Problem trying to create BeanPropertyWriter for property '" + propDef.getName() + "' (of type " + this._beanDesc.getType() + "); serialization type " + serializationType + " has no content"); 
/*   0*/         }
/*  93*/      serializationType = serializationType.withContentTypeHandler(contentTypeSer);
/*  94*/      ct = serializationType.getContentType();
/*   0*/    } 
/*  97*/    Object valueToSuppress = null;
/*   0*/    boolean suppressNulls = false;
/* 100*/    JsonInclude.Value inclV = this._defaultInclusion.withOverrides(propDef.findInclusion());
/* 101*/    JsonInclude.Include inclusion = inclV.getValueInclusion();
/* 102*/    if (inclusion == JsonInclude.Include.USE_DEFAULTS) {
/* 103*/        inclusion = JsonInclude.Include.ALWAYS; 
/*   0*/       }
/* 107*/    JavaType actualType = (serializationType == null) ? declaredType : serializationType;
/* 109*/    switch (inclusion) {
/*   0*/      case NON_DEFAULT:
/* 116*/        if (this._defaultInclusion.getValueInclusion() == JsonInclude.Include.NON_DEFAULT) {
/* 117*/          valueToSuppress = getPropertyDefaultValue(propDef.getName(), am, actualType);
/*   0*/        } else {
/* 119*/          valueToSuppress = getDefaultValue(actualType);
/*   0*/        } 
/* 121*/        if (valueToSuppress == null) {
/* 122*/          suppressNulls = true;
/*   0*/          break;
/*   0*/        } 
/* 124*/        if (valueToSuppress.getClass().isArray()) {
/* 125*/            valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case NON_ABSENT:
/* 132*/        suppressNulls = true;
/* 134*/        declaredType.isReferenceType();
/* 135*/        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*   0*/        break;
/*   0*/      case NON_EMPTY:
/* 140*/        suppressNulls = true;
/* 142*/        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*   0*/        break;
/*   0*/      case NON_NULL:
/* 145*/        suppressNulls = true;
/*   0*/      default:
/* 150*/        if (declaredType.isContainerType() && !this._config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) {
/* 152*/            valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY; 
/*   0*/           }
/*   0*/        break;
/*   0*/    } 
/* 156*/    BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, this._beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress);
/* 161*/    Object serDef = this._annotationIntrospector.findNullSerializer(am);
/* 162*/    if (serDef != null) {
/* 163*/        bpw.assignNullSerializer(prov.serializerInstance(am, serDef)); 
/*   0*/       }
/* 166*/    NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
/* 167*/    if (unwrapper != null) {
/* 168*/        bpw = bpw.unwrappingWriter(unwrapper); 
/*   0*/       }
/* 170*/    return bpw;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType findSerializationType(Annotated a, boolean useStaticTyping, JavaType declaredType) throws JsonMappingException {
/* 188*/    JavaType secondary = this._annotationIntrospector.refineSerializationType(this._config, a, declaredType);
/* 191*/    if (secondary != declaredType) {
/* 192*/      Class<?> serClass = secondary.getRawClass();
/* 194*/      Class<?> rawDeclared = declaredType.getRawClass();
/* 195*/      if (!serClass.isAssignableFrom(rawDeclared)) {
/* 204*/          if (!rawDeclared.isAssignableFrom(serClass)) {
/* 205*/              throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName()); 
/*   0*/             } 
/*   0*/         }
/* 212*/      useStaticTyping = true;
/* 213*/      declaredType = secondary;
/*   0*/    } 
/* 216*/    JsonSerialize.Typing typing = this._annotationIntrospector.findSerializationTyping(a);
/* 217*/    if (typing != null && typing != JsonSerialize.Typing.DEFAULT_TYPING) {
/* 218*/        useStaticTyping = (typing == JsonSerialize.Typing.STATIC); 
/*   0*/       }
/* 220*/    if (useStaticTyping) {
/* 222*/        return declaredType.withStaticTyping(); 
/*   0*/       }
/* 225*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object getDefaultBean() {
/* 236*/    Object def = this._defaultBean;
/* 237*/    if (def == null) {
/* 241*/      def = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
/* 242*/      if (def == null) {
/* 250*/          def = NO_DEFAULT_MARKER; 
/*   0*/         }
/* 252*/      this._defaultBean = def;
/*   0*/    } 
/* 254*/    return (def == NO_DEFAULT_MARKER) ? null : this._defaultBean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object getPropertyDefaultValue(String name, AnnotatedMember member, JavaType type) {
/* 272*/    Object defaultBean = getDefaultBean();
/* 273*/    if (defaultBean == null) {
/* 274*/        return getDefaultValue(type); 
/*   0*/       }
/*   0*/    try {
/* 277*/      return member.getValue(defaultBean);
/* 278*/    } catch (Exception e) {
/* 279*/      return _throwWrapped(e, name, defaultBean);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Object getDefaultValue(JavaType type) {
/* 300*/    Class<?> cls = type.getRawClass();
/* 302*/    Class<?> prim = ClassUtil.primitiveType(cls);
/* 303*/    if (prim != null) {
/* 304*/        return ClassUtil.defaultValue(prim); 
/*   0*/       }
/* 306*/    if (type.isContainerType() || type.isReferenceType()) {
/* 307*/        return JsonInclude.Include.NON_EMPTY; 
/*   0*/       }
/* 309*/    if (cls == String.class) {
/* 310*/        return ""; 
/*   0*/       }
/* 312*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _throwWrapped(Exception e, String propName, Object defaultBean) {
/* 323*/    Throwable t = e;
/* 324*/    while (t.getCause() != null) {
/* 325*/        t = t.getCause(); 
/*   0*/       }
/* 327*/    if (t instanceof Error) {
/* 327*/        throw (Error)t; 
/*   0*/       }
/* 328*/    if (t instanceof RuntimeException) {
/* 328*/        throw (RuntimeException)t; 
/*   0*/       }
/* 329*/    throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
/*   0*/  }
/*   0*/}
