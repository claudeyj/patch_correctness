/*   0*/package com.fasterxml.jackson.databind.ser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonInclude;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonSerializer;
/*   0*/import com.fasterxml.jackson.databind.MapperFeature;
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
/*   0*/  protected final AnnotationIntrospector _annotationIntrospector;
/*   0*/  
/*   0*/  protected Object _defaultBean;
/*   0*/  
/*   0*/  protected final JsonInclude.Value _defaultInclusion;
/*   0*/  
/*   0*/  protected final boolean _useRealPropertyDefaults;
/*   0*/  
/*   0*/  public PropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
/*  54*/    this._config = config;
/*  55*/    this._beanDesc = beanDesc;
/*  64*/    JsonInclude.Value inclPerType = JsonInclude.Value.merge(beanDesc.findPropertyInclusion(JsonInclude.Value.empty()), config.getDefaultPropertyInclusion(beanDesc.getBeanClass(), JsonInclude.Value.empty()));
/*  68*/    this._defaultInclusion = JsonInclude.Value.merge(config.getDefaultPropertyInclusion(), inclPerType);
/*  70*/    this._useRealPropertyDefaults = (inclPerType.getValueInclusion() == JsonInclude.Include.NON_DEFAULT);
/*  71*/    this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*   0*/  }
/*   0*/  
/*   0*/  public Annotations getClassAnnotations() {
/*  81*/    return this._beanDesc.getClassAnnotations();
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanPropertyWriter buildWriter(SerializerProvider prov, BeanPropertyDefinition propDef, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping) throws JsonMappingException {
/*   0*/    JavaType serializationType;
/*   0*/    try {
/*  99*/      serializationType = findSerializationType(am, defaultUseStaticTyping, declaredType);
/* 100*/    } catch (JsonMappingException e) {
/* 101*/      return prov.<BeanPropertyWriter>reportBadPropertyDefinition(this._beanDesc, propDef, e.getMessage(), new Object[0]);
/*   0*/    } 
/* 105*/    if (contentTypeSer != null) {
/* 110*/      if (serializationType == null) {
/* 112*/          serializationType = declaredType; 
/*   0*/         }
/* 114*/      JavaType ct = serializationType.getContentType();
/* 116*/      if (ct == null) {
/* 117*/          prov.reportBadPropertyDefinition(this._beanDesc, propDef, "serialization type " + serializationType + " has no content", new Object[0]); 
/*   0*/         }
/* 120*/      serializationType = serializationType.withContentTypeHandler(contentTypeSer);
/* 121*/      ct = serializationType.getContentType();
/*   0*/    } 
/* 124*/    Object valueToSuppress = null;
/*   0*/    boolean suppressNulls = false;
/* 128*/    JavaType actualType = (serializationType == null) ? declaredType : serializationType;
/* 133*/    JsonInclude.Value inclV = this._config.getDefaultPropertyInclusion(actualType.getRawClass(), this._defaultInclusion);
/* 138*/    inclV = inclV.withOverrides(propDef.findInclusion());
/* 139*/    JsonInclude.Include inclusion = inclV.getValueInclusion();
/* 141*/    if (inclusion == JsonInclude.Include.USE_DEFAULTS) {
/* 142*/        inclusion = JsonInclude.Include.ALWAYS; 
/*   0*/       }
/* 145*/    switch (inclusion) {
/*   0*/      case NON_DEFAULT:
/* 157*/        if (this._useRealPropertyDefaults) {
/* 159*/          if (prov.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
/* 160*/              am.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/             }
/* 162*/          valueToSuppress = getPropertyDefaultValue(propDef.getName(), am, actualType);
/*   0*/        } else {
/* 164*/          valueToSuppress = getDefaultValue(actualType);
/* 165*/          suppressNulls = true;
/*   0*/        } 
/* 167*/        if (valueToSuppress == null) {
/* 168*/          suppressNulls = true;
/*   0*/          break;
/*   0*/        } 
/* 170*/        if (valueToSuppress.getClass().isArray()) {
/* 171*/            valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case NON_ABSENT:
/* 177*/        suppressNulls = true;
/* 179*/        if (actualType.isReferenceType()) {
/* 180*/            valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY; 
/*   0*/           }
/*   0*/        break;
/*   0*/      case NON_EMPTY:
/* 185*/        suppressNulls = true;
/* 187*/        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*   0*/        break;
/*   0*/      case NON_NULL:
/* 190*/        suppressNulls = true;
/*   0*/      default:
/* 195*/        if (actualType.isContainerType() && !this._config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) {
/* 197*/            valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY; 
/*   0*/           }
/*   0*/        break;
/*   0*/    } 
/* 201*/    BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, this._beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress);
/* 206*/    Object serDef = this._annotationIntrospector.findNullSerializer(am);
/* 207*/    if (serDef != null) {
/* 208*/        bpw.assignNullSerializer(prov.serializerInstance(am, serDef)); 
/*   0*/       }
/* 211*/    NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
/* 212*/    if (unwrapper != null) {
/* 213*/        bpw = bpw.unwrappingWriter(unwrapper); 
/*   0*/       }
/* 215*/    return bpw;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType findSerializationType(Annotated a, boolean useStaticTyping, JavaType declaredType) throws JsonMappingException {
/* 233*/    JavaType secondary = this._annotationIntrospector.refineSerializationType(this._config, a, declaredType);
/* 237*/    if (secondary != declaredType) {
/* 238*/      Class<?> serClass = secondary.getRawClass();
/* 240*/      Class<?> rawDeclared = declaredType.getRawClass();
/* 241*/      if (!serClass.isAssignableFrom(rawDeclared)) {
/* 250*/          if (!rawDeclared.isAssignableFrom(serClass)) {
/* 251*/              throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName()); 
/*   0*/             } 
/*   0*/         }
/* 258*/      useStaticTyping = true;
/* 259*/      declaredType = secondary;
/*   0*/    } 
/* 262*/    JsonSerialize.Typing typing = this._annotationIntrospector.findSerializationTyping(a);
/* 263*/    if (typing != null && typing != JsonSerialize.Typing.DEFAULT_TYPING) {
/* 264*/        useStaticTyping = (typing == JsonSerialize.Typing.STATIC); 
/*   0*/       }
/* 266*/    if (useStaticTyping) {
/* 268*/        return declaredType.withStaticTyping(); 
/*   0*/       }
/* 271*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object getDefaultBean() {
/* 282*/    Object def = this._defaultBean;
/* 283*/    if (def == null) {
/* 287*/      def = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
/* 288*/      if (def == null) {
/* 296*/          def = NO_DEFAULT_MARKER; 
/*   0*/         }
/* 298*/      this._defaultBean = def;
/*   0*/    } 
/* 300*/    return (def == NO_DEFAULT_MARKER) ? null : this._defaultBean;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected Object getPropertyDefaultValue(String name, AnnotatedMember member, JavaType type) {
/* 322*/    Object defaultBean = getDefaultBean();
/* 323*/    if (defaultBean == null) {
/* 324*/        return getDefaultValue(type); 
/*   0*/       }
/*   0*/    try {
/* 327*/      return member.getValue(defaultBean);
/* 328*/    } catch (Exception e) {
/* 329*/      return _throwWrapped(e, name, defaultBean);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Object getDefaultValue(JavaType type) {
/* 350*/    Class<?> cls = type.getRawClass();
/* 352*/    Class<?> prim = ClassUtil.primitiveType(cls);
/* 353*/    if (prim != null) {
/* 354*/        return ClassUtil.defaultValue(prim); 
/*   0*/       }
/* 356*/    if (type.isContainerType() || type.isReferenceType()) {
/* 357*/        return JsonInclude.Include.NON_EMPTY; 
/*   0*/       }
/* 359*/    if (cls == String.class) {
/* 360*/      "";
/* 360*/      return null;
/*   0*/    } 
/* 362*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _throwWrapped(Exception e, String propName, Object defaultBean) {
/* 373*/    Throwable t = e;
/* 374*/    while (t.getCause() != null) {
/* 375*/        t = t.getCause(); 
/*   0*/       }
/* 377*/    if (t instanceof Error) {
/* 377*/        throw (Error)t; 
/*   0*/       }
/* 378*/    if (t instanceof RuntimeException) {
/* 378*/        throw (RuntimeException)t; 
/*   0*/       }
/* 379*/    throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
/*   0*/  }
/*   0*/}
