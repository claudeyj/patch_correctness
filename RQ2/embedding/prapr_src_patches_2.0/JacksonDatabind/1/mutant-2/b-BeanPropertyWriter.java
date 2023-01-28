/*   0*/package com.fasterxml.jackson.databind.ser;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.SerializableString;
/*   0*/import com.fasterxml.jackson.core.io.SerializedString;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.JsonSerializer;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.SerializerProvider;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*   0*/import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
/*   0*/import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*   0*/import com.fasterxml.jackson.databind.node.ObjectNode;
/*   0*/import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
/*   0*/import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
/*   0*/import com.fasterxml.jackson.databind.util.Annotations;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.util.HashMap;
/*   0*/
/*   0*/public class BeanPropertyWriter implements BeanProperty {
/*  38*/  public static final Object MARKER_FOR_EMPTY = new Object();
/*   0*/  
/*   0*/  protected final AnnotatedMember _member;
/*   0*/  
/*   0*/  protected final Annotations _contextAnnotations;
/*   0*/  
/*   0*/  protected final JavaType _declaredType;
/*   0*/  
/*   0*/  protected final Method _accessorMethod;
/*   0*/  
/*   0*/  protected final Field _field;
/*   0*/  
/*   0*/  protected HashMap<Object, Object> _internalSettings;
/*   0*/  
/*   0*/  protected final SerializedString _name;
/*   0*/  
/*   0*/  protected final PropertyName _wrapperName;
/*   0*/  
/*   0*/  protected final JavaType _cfgSerializationType;
/*   0*/  
/*   0*/  protected JsonSerializer<Object> _serializer;
/*   0*/  
/*   0*/  protected JsonSerializer<Object> _nullSerializer;
/*   0*/  
/*   0*/  protected PropertySerializerMap _dynamicSerializers;
/*   0*/  
/*   0*/  protected final boolean _suppressNulls;
/*   0*/  
/*   0*/  protected final Object _suppressableValue;
/*   0*/  
/*   0*/  protected final Class<?>[] _includeInViews;
/*   0*/  
/*   0*/  protected TypeSerializer _typeSerializer;
/*   0*/  
/*   0*/  protected JavaType _nonTrivialBaseType;
/*   0*/  
/*   0*/  protected final boolean _isRequired;
/*   0*/  
/*   0*/  public BeanPropertyWriter(BeanPropertyDefinition paramBeanPropertyDefinition, AnnotatedMember paramAnnotatedMember, Annotations paramAnnotations, JavaType paramJavaType1, JsonSerializer<?> paramJsonSerializer, TypeSerializer paramTypeSerializer, JavaType paramJavaType2, boolean paramBoolean, Object paramObject) {
/* 191*/    this._member = paramAnnotatedMember;
/* 192*/    this._contextAnnotations = paramAnnotations;
/* 193*/    this._name = new SerializedString(paramBeanPropertyDefinition.getName());
/* 194*/    this._wrapperName = paramBeanPropertyDefinition.getWrapperName();
/* 195*/    this._declaredType = paramJavaType1;
/* 196*/    this._serializer = (JsonSerializer)paramJsonSerializer;
/* 197*/    this._dynamicSerializers = (paramJsonSerializer == null) ? PropertySerializerMap.emptyMap() : null;
/* 198*/    this._typeSerializer = paramTypeSerializer;
/* 199*/    this._cfgSerializationType = paramJavaType2;
/* 200*/    this._isRequired = paramBeanPropertyDefinition.isRequired();
/* 202*/    if (paramAnnotatedMember instanceof com.fasterxml.jackson.databind.introspect.AnnotatedField) {
/* 203*/      this._accessorMethod = null;
/* 204*/      this._field = (Field)paramAnnotatedMember.getMember();
/* 205*/    } else if (paramAnnotatedMember instanceof com.fasterxml.jackson.databind.introspect.AnnotatedMethod) {
/* 206*/      this._accessorMethod = (Method)paramAnnotatedMember.getMember();
/* 207*/      this._field = null;
/*   0*/    } else {
/* 209*/      throw new IllegalArgumentException("Can not pass member of type " + paramAnnotatedMember.getClass().getName());
/*   0*/    } 
/* 211*/    this._suppressNulls = paramBoolean;
/* 212*/    this._suppressableValue = paramObject;
/* 213*/    this._includeInViews = paramBeanPropertyDefinition.findViews();
/* 216*/    this._nullSerializer = null;
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanPropertyWriter(BeanPropertyWriter paramBeanPropertyWriter) {
/* 223*/    this(paramBeanPropertyWriter, paramBeanPropertyWriter._name);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanPropertyWriter(BeanPropertyWriter paramBeanPropertyWriter, SerializedString paramSerializedString) {
/* 228*/    this._name = paramSerializedString;
/* 229*/    this._wrapperName = paramBeanPropertyWriter._wrapperName;
/* 231*/    this._member = paramBeanPropertyWriter._member;
/* 232*/    this._contextAnnotations = paramBeanPropertyWriter._contextAnnotations;
/* 233*/    this._declaredType = paramBeanPropertyWriter._declaredType;
/* 234*/    this._accessorMethod = paramBeanPropertyWriter._accessorMethod;
/* 235*/    this._field = paramBeanPropertyWriter._field;
/* 236*/    this._serializer = paramBeanPropertyWriter._serializer;
/* 237*/    this._nullSerializer = paramBeanPropertyWriter._nullSerializer;
/* 239*/    if (paramBeanPropertyWriter._internalSettings != null) {
/* 240*/        this._internalSettings = new HashMap<Object, Object>(paramBeanPropertyWriter._internalSettings); 
/*   0*/       }
/* 242*/    this._cfgSerializationType = paramBeanPropertyWriter._cfgSerializationType;
/* 243*/    this._dynamicSerializers = paramBeanPropertyWriter._dynamicSerializers;
/* 244*/    this._suppressNulls = paramBeanPropertyWriter._suppressNulls;
/* 245*/    this._suppressableValue = paramBeanPropertyWriter._suppressableValue;
/* 246*/    this._includeInViews = paramBeanPropertyWriter._includeInViews;
/* 247*/    this._typeSerializer = paramBeanPropertyWriter._typeSerializer;
/* 248*/    this._nonTrivialBaseType = paramBeanPropertyWriter._nonTrivialBaseType;
/* 249*/    this._isRequired = paramBeanPropertyWriter._isRequired;
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyWriter rename(NameTransformer paramNameTransformer) {
/* 253*/    String str = paramNameTransformer.transform(this._name.getValue());
/* 254*/    if (str.equals(this._name.toString())) {
/* 255*/        return this; 
/*   0*/       }
/* 257*/    return new BeanPropertyWriter(this, new SerializedString(str));
/*   0*/  }
/*   0*/  
/*   0*/  public void assignSerializer(JsonSerializer<Object> paramJsonSerializer) {
/* 268*/    if (this._serializer != null && this._serializer != paramJsonSerializer) {
/* 269*/        throw new IllegalStateException("Can not override serializer"); 
/*   0*/       }
/* 271*/    this._serializer = paramJsonSerializer;
/*   0*/  }
/*   0*/  
/*   0*/  public void assignNullSerializer(JsonSerializer<Object> paramJsonSerializer) {
/* 282*/    if (this._nullSerializer != null && this._nullSerializer != paramJsonSerializer) {
/* 283*/        throw new IllegalStateException("Can not override null serializer"); 
/*   0*/       }
/* 285*/    this._nullSerializer = paramJsonSerializer;
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyWriter unwrappingWriter(NameTransformer paramNameTransformer) {
/* 293*/    return new UnwrappingBeanPropertyWriter(this, paramNameTransformer);
/*   0*/  }
/*   0*/  
/*   0*/  public void setNonTrivialBaseType(JavaType paramJavaType) {
/* 302*/    this._nonTrivialBaseType = paramJavaType;
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/* 313*/    return this._name.getValue();
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getType() {
/* 318*/    return this._declaredType;
/*   0*/  }
/*   0*/  
/*   0*/  public PropertyName getWrapperName() {
/* 323*/    return this._wrapperName;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRequired() {
/* 328*/    return this._isRequired;
/*   0*/  }
/*   0*/  
/*   0*/  public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> paramClass) {
/* 333*/    return this._member.getAnnotation(paramClass);
/*   0*/  }
/*   0*/  
/*   0*/  public <A extends java.lang.annotation.Annotation> A getContextAnnotation(Class<A> paramClass) {
/* 338*/    return this._contextAnnotations.get(paramClass);
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMember getMember() {
/* 343*/    return this._member;
/*   0*/  }
/*   0*/  
/*   0*/  public void depositSchemaProperty(JsonObjectFormatVisitor paramJsonObjectFormatVisitor) throws JsonMappingException {
/* 351*/    if (paramJsonObjectFormatVisitor != null) {
/* 352*/        if (isRequired()) {
/* 353*/          paramJsonObjectFormatVisitor.property(this);
/*   0*/        } else {
/* 355*/          paramJsonObjectFormatVisitor.optionalProperty(this);
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public Object getInternalSetting(Object paramObject) {
/* 374*/    if (this._internalSettings == null) {
/* 375*/        return null; 
/*   0*/       }
/* 377*/    return this._internalSettings.get(paramObject);
/*   0*/  }
/*   0*/  
/*   0*/  public Object setInternalSetting(Object paramObject1, Object paramObject2) {
/* 387*/    if (this._internalSettings == null) {
/* 388*/        this._internalSettings = new HashMap<Object, Object>(); 
/*   0*/       }
/* 390*/    return this._internalSettings.put(paramObject1, paramObject2);
/*   0*/  }
/*   0*/  
/*   0*/  public Object removeInternalSetting(Object paramObject) {
/* 400*/    Object object = null;
/* 401*/    if (this._internalSettings != null) {
/* 402*/      object = this._internalSettings.remove(paramObject);
/* 404*/      if (this._internalSettings.size() == 0) {
/* 405*/          this._internalSettings = null; 
/*   0*/         }
/*   0*/    } 
/* 408*/    return object;
/*   0*/  }
/*   0*/  
/*   0*/  public SerializedString getSerializedName() {
/* 417*/    return this._name;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasSerializer() {
/* 419*/    return (this._serializer != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasNullSerializer() {
/* 420*/    return (this._nullSerializer != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean willSuppressNulls() {
/* 422*/    return this._suppressNulls;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonSerializer<Object> getSerializer() {
/* 426*/    return this._serializer;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getSerializationType() {
/* 430*/    return this._cfgSerializationType;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getRawSerializationType() {
/* 434*/    return (this._cfgSerializationType == null) ? null : this._cfgSerializationType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getPropertyType() {
/* 439*/    if (this._accessorMethod != null) {
/* 440*/        return this._accessorMethod.getReturnType(); 
/*   0*/       }
/* 442*/    return this._field.getType();
/*   0*/  }
/*   0*/  
/*   0*/  public Type getGenericPropertyType() {
/* 452*/    if (this._accessorMethod != null) {
/* 453*/        return this._accessorMethod.getGenericReturnType(); 
/*   0*/       }
/* 455*/    return this._field.getGenericType();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?>[] getViews() {
/* 458*/    return this._includeInViews;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected boolean isRequired(AnnotationIntrospector paramAnnotationIntrospector) {
/* 473*/    return this._isRequired;
/*   0*/  }
/*   0*/  
/*   0*/  public void depositSchemaProperty(ObjectNode paramObjectNode, SerializerProvider paramSerializerProvider) throws JsonMappingException {
/*   0*/    JsonNode jsonNode;
/* 498*/    JavaType javaType = getSerializationType();
/* 500*/    Type type = (javaType == null) ? getGenericPropertyType() : javaType.getRawClass();
/* 503*/    JsonSerializer<Object> jsonSerializer = getSerializer();
/* 504*/    if (jsonSerializer == null) {
/* 505*/      Class<?> clazz = getRawSerializationType();
/* 506*/      if (clazz == null) {
/* 507*/          clazz = getPropertyType(); 
/*   0*/         }
/* 509*/      jsonSerializer = paramSerializerProvider.findValueSerializer(clazz, this);
/*   0*/    } 
/* 511*/    boolean bool = !isRequired();
/* 512*/    if (jsonSerializer instanceof SchemaAware) {
/* 513*/      jsonNode = ((SchemaAware)jsonSerializer).getSchema(paramSerializerProvider, type, bool);
/*   0*/    } else {
/* 515*/      jsonNode = JsonSchema.getDefaultSchemaNode();
/*   0*/    } 
/* 517*/    paramObjectNode.put(getName(), jsonNode);
/*   0*/  }
/*   0*/  
/*   0*/  public void serializeAsField(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws Exception {
/* 534*/    Object object = get(paramObject);
/* 536*/    if (object == null) {
/* 537*/      if (this._nullSerializer != null) {
/* 538*/        paramJsonGenerator.writeFieldName((SerializableString)this._name);
/* 539*/        this._nullSerializer.serialize(null, paramJsonGenerator, paramSerializerProvider);
/*   0*/      } 
/*   0*/      return;
/*   0*/    } 
/* 544*/    JsonSerializer<Object> jsonSerializer = this._serializer;
/* 545*/    if (jsonSerializer == null) {
/* 546*/      Class<?> clazz = object.getClass();
/* 547*/      PropertySerializerMap propertySerializerMap = this._dynamicSerializers;
/* 548*/      jsonSerializer = propertySerializerMap.serializerFor(clazz);
/* 549*/      if (jsonSerializer == null) {
/* 550*/          jsonSerializer = _findAndAddDynamic(propertySerializerMap, clazz, paramSerializerProvider); 
/*   0*/         }
/*   0*/    } 
/* 554*/    if (this._suppressableValue != null) {
/* 555*/        if (MARKER_FOR_EMPTY == this._suppressableValue) {
/* 556*/          if (jsonSerializer.isEmpty(object)) {
/*   0*/              return; 
/*   0*/             }
/* 559*/        } else if (this._suppressableValue.equals(object)) {
/*   0*/          return;
/*   0*/        }  
/*   0*/       }
/* 564*/    if (object == paramObject) {
/* 565*/        _handleSelfReference(paramObject, jsonSerializer); 
/*   0*/       }
/* 567*/    paramJsonGenerator.writeFieldName((SerializableString)this._name);
/* 568*/    if (this._typeSerializer == null) {
/* 569*/      jsonSerializer.serialize(object, paramJsonGenerator, paramSerializerProvider);
/*   0*/    } else {
/* 571*/      jsonSerializer.serializeWithType(object, paramJsonGenerator, paramSerializerProvider, this._typeSerializer);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void serializeAsColumn(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws Exception {
/* 585*/    Object object = get(paramObject);
/* 586*/    if (object == null) {
/* 587*/        if (this._nullSerializer != null) {
/* 588*/          null;
/*   0*/        } else {
/* 590*/          paramJsonGenerator.writeNull();
/*   0*/        }  
/*   0*/       }
/* 594*/    JsonSerializer<Object> jsonSerializer = this._serializer;
/* 595*/    if (jsonSerializer == null) {
/* 596*/      Class<?> clazz = object.getClass();
/* 597*/      PropertySerializerMap propertySerializerMap = this._dynamicSerializers;
/* 598*/      jsonSerializer = propertySerializerMap.serializerFor(clazz);
/* 599*/      if (jsonSerializer == null) {
/* 600*/          jsonSerializer = _findAndAddDynamic(propertySerializerMap, clazz, paramSerializerProvider); 
/*   0*/         }
/*   0*/    } 
/* 604*/    if (this._suppressableValue != null) {
/* 605*/        if (MARKER_FOR_EMPTY == this._suppressableValue) {
/* 606*/          if (jsonSerializer.isEmpty(object)) {
/* 607*/            serializeAsPlaceholder(paramObject, paramJsonGenerator, paramSerializerProvider);
/*   0*/            return;
/*   0*/          } 
/* 610*/        } else if (this._suppressableValue.equals(object)) {
/* 611*/          serializeAsPlaceholder(paramObject, paramJsonGenerator, paramSerializerProvider);
/*   0*/          return;
/*   0*/        }  
/*   0*/       }
/* 616*/    if (object == paramObject) {
/* 617*/        _handleSelfReference(paramObject, jsonSerializer); 
/*   0*/       }
/* 619*/    if (this._typeSerializer == null) {
/* 620*/      jsonSerializer.serialize(object, paramJsonGenerator, paramSerializerProvider);
/*   0*/    } else {
/* 622*/      jsonSerializer.serializeWithType(object, paramJsonGenerator, paramSerializerProvider, this._typeSerializer);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void serializeAsPlaceholder(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider) throws Exception {
/* 637*/    if (this._nullSerializer != null) {
/* 638*/      this._nullSerializer.serialize(null, paramJsonGenerator, paramSerializerProvider);
/*   0*/    } else {
/* 640*/      paramJsonGenerator.writeNull();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap paramPropertySerializerMap, Class<?> paramClass, SerializerProvider paramSerializerProvider) throws JsonMappingException {
/*   0*/    PropertySerializerMap.SerializerAndMapResult serializerAndMapResult;
/* 654*/    if (this._nonTrivialBaseType != null) {
/* 655*/      JavaType javaType = paramSerializerProvider.constructSpecializedType(this._nonTrivialBaseType, paramClass);
/* 656*/      serializerAndMapResult = paramPropertySerializerMap.findAndAddSerializer(javaType, paramSerializerProvider, this);
/*   0*/    } else {
/* 658*/      serializerAndMapResult = paramPropertySerializerMap.findAndAddSerializer(paramClass, paramSerializerProvider, this);
/*   0*/    } 
/* 661*/    if (paramPropertySerializerMap != serializerAndMapResult.map) {
/* 662*/        this._dynamicSerializers = serializerAndMapResult.map; 
/*   0*/       }
/* 664*/    return serializerAndMapResult.serializer;
/*   0*/  }
/*   0*/  
/*   0*/  public final Object get(Object paramObject) throws Exception {
/* 677*/    if (this._accessorMethod != null) {
/* 678*/        return this._accessorMethod.invoke(paramObject, new Object[0]); 
/*   0*/       }
/* 680*/    return this._field.get(paramObject);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _handleSelfReference(Object paramObject, JsonSerializer<?> paramJsonSerializer) throws JsonMappingException {
/* 689*/    if (paramJsonSerializer.usesObjectId()) {
/*   0*/        return; 
/*   0*/       }
/* 692*/    throw new JsonMappingException("Direct self-reference leading to cycle");
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 698*/    StringBuilder stringBuilder = new StringBuilder(40);
/* 699*/    stringBuilder.append("property '").append(getName()).append("' (");
/* 700*/    if (this._accessorMethod != null) {
/* 701*/      stringBuilder.append("via method ").append(this._accessorMethod.getDeclaringClass().getName()).append("#").append(this._accessorMethod.getName());
/*   0*/    } else {
/* 703*/      stringBuilder.append("field \"").append(this._field.getDeclaringClass().getName()).append("#").append(this._field.getName());
/*   0*/    } 
/* 705*/    if (this._serializer == null) {
/* 706*/      stringBuilder.append(", no static serializer");
/*   0*/    } else {
/* 708*/      stringBuilder.append(", static serializer of type " + this._serializer.getClass().getName());
/*   0*/    } 
/* 710*/    stringBuilder.append(')');
/* 711*/    return stringBuilder.toString();
/*   0*/  }
/*   0*/}
